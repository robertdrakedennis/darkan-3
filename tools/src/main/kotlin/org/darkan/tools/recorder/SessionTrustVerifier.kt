package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.CapturePacketDecode
import org.darkan.core.net.recorder.ClientStateCrossCheck
import java.io.File
import java.util.Base64
import kotlin.math.abs

/**
 * Turns a recorder session directory into a *trust report* — a provable answer to "is this capture
 * complete and coherent, or are we flying blind?".
 *
 * The in-process dylib's `framed-*.jsonl` is already authoritative: each line is the opcode/size/body
 * the CLIENT ITSELF decoded (`LAST_OPCODE`, the client's resolved size, the real receive buffer), not
 * an offline guess. So the verifier does not re-derive the protocol; it *proves* three independent
 * properties and fails loudly if any is off:
 *
 *  1. **Byte-accounting** (primary completeness proof, claude-re's gold standard): per (conn, dir),
 *     reconstruct each framed packet's on-wire footprint = opcode bytes + size-prefix bytes + body,
 *     sum it, and reconcile against the socket-plane byte total for that conn+dir. 100% coverage of
 *     the post-handshake bytes means PROVABLY no packet was dropped.
 *  2. **Clean-decode rate**: the share of framed packets that round-trip through the live core codecs
 *     without throwing. A failing opcode is a prime suspect for an offset/mislabel bug. c2s
 *     `xtea_body` packets are reported as "encrypted — not decodable offline", never as failures.
 *  3. **Sanity vs known production**: per-opcode counts/sizes compared to the documented 948-5
 *     world-entry session ([ProductionBaseline]); large deviations flag either a non-real capture or
 *     a mislabel of the kind claude-re corrected.
 *
 * The size class (fixed / varByte / varShort) always comes from the live [Codec] — never a
 * hand-rolled table. Body *lengths* come from the capture itself (the client's own resolved size).
 */
class SessionTrustVerifier(private val codec: Codec, private val known: KnownOpcodes) {

    private val json = Json { ignoreUnknownKeys = true }
    private val base64 = Base64.getDecoder()

    // ---- result model (also the shape serialized to trust-report.json) --------------------------

    enum class Verdict { PASS, WARN, FAIL }

    /** A single framing anomaly found inside a conn+dir stream, with its running prot-stream offset. */
    data class Desync(
        val conn: String,
        val dir: String,
        val frameIndex: Int,
        val streamOffset: Int,
        val opcode: Int,
        val name: String,
        val detail: String,
    )

    /**
     * A positive double-count signal for a conn+dir plane: a large FRACTION of the plane's frames are
     * adjacent byte-identical duplicates. A framing hook that emitted every packet twice duplicates the
     * WHOLE plane (≈100% adjacent-duplicate), whereas legitimately-repeating telemetry/input opcodes
     * (e.g. idle mouse op52 reporting the same delta each tick) only make a SMALL fraction repeat — so
     * the fraction, not any single run, is the honest discriminator. Detected by [detectDoubleCount].
     *
     * @property duplicateFrames frames that are an immediate repeat of the previous frame (multi-byte).
     * @property totalFrames total frames on the plane.
     * @property worstOpcode the opcode with the longest single duplicate run (for the message).
     * @property worstRun that run's length.
     */
    data class DoubleCount(
        val conn: String,
        val dir: String,
        val duplicateFrames: Int,
        val totalFrames: Int,
        val worstOpcode: Int,
        val worstName: String,
        val worstRun: Int,
    ) {
        val duplicateFraction: Double get() = if (totalFrames == 0) 0.0 else duplicateFrames.toDouble() / totalFrames.toDouble()
    }

    data class ByteAccount(
        val conn: String,
        val dir: String,
        val framedPackets: Int,
        val framedFootprint: Long,
        val socketTotal: Long,
        val handshakeOverhead: Long,
        val accountableSocket: Long,
        val coveragePct: Double,
        val residualBytes: Long,
        val socketPresent: Boolean,
        /**
         * True when the socket plane *did* see this conn+dir but captured so few bytes relative to the
         * framed footprint that it plainly missed the stream (the s2c game/login ciphertext the mac
         * dylib barely sampled, drowned out by the `conn=unknown` cache download). Byte-accounting is
         * then UNAVAILABLE — not a "frames invented" failure — so completeness can't be proven and we
         * lean on clean-decode instead. See [accountBytes].
         */
        val socketUncaptured: Boolean,
    )

    data class DecodeStat(
        val conn: String,
        val dir: String,
        val decodable: Int,
        val cleanDecoded: Int,
        val failed: Int,
        val encryptedSkipped: Int,
        val cleanRatePct: Double,
        val failingOpcodes: Map<Int, Int>,
    )

    data class SanityDelta(
        val conn: String,
        val dir: String,
        val opcode: Int,
        val name: String,
        val actualCount: Int,
        val expectedCount: Int,
        val kind: String,
        val note: String,
    )

    data class Report(
        val sessionId: String,
        val build: String,
        val serverMode: String,
        val byteAccounts: List<ByteAccount>,
        val decodeStats: List<DecodeStat>,
        val desyncs: List<Desync>,
        /** Detected duplicate-frame runs — the positive "frames invented / double-counted" signal. */
        val doubleCounts: List<DoubleCount>,
        val sanityDeltas: List<SanityDelta>,
        val crossValidation: String,
        /**
         * The CLIENT-IS-KING cross-check: our decoded state-bearing s2c packets folded into the
         * EXPECTED client state, compared to the client's OWN final snapshot (ground truth). A
         * mismatch/missed here is a real correctness failure and a first-class verdict input. When no
         * snapshot was captured this is [ClientStateCrossCheck.CrossCheck.available] == false.
         */
        val clientCrossCheck: ClientStateCrossCheck.CrossCheck,
        /** Human-readable rendering of [clientCrossCheck] for the report body. */
        val clientCrossSummary: String,
        /** Non-game socket volume (cache/JS5/HTTP, e.g. `conn=unknown`) reported separately as INFO. */
        val nonGameSocket: List<NonGameSocket>,
        val verdict: Verdict,
        val reasons: List<String>,
    )

    /** Socket-plane volume for a conn+dir that is NOT a game-protocol plane (cache/JS5/HTTP download). */
    data class NonGameSocket(
        val conn: String,
        val dir: String,
        val entries: Int,
        val bytes: Long,
    )

    // ---- one framed record (the fields the verifier needs) -------------------------------------

    private data class FramedRecord(
        val conn: String,
        val opcode: Int,
        val len: Int,
        val xteaBody: Boolean,
        val body: ByteArray?,
        /**
         * The connection's `MAIN_STATE` at the instant the dylib framed this packet (the recorder's
         * `state` field). `LOGGED_IN` (= [STATE_LOGGED_IN]) is the in-game prot-dispatch phase;
         * anything below it (`LOGIN_SCREEN`/`LOBBY_SCREEN`) is the login state machine, where the
         * connection-type + RSA login blocks are sent. -1 when the recorder could not read the state
         * (pre-connect / unresolved client base). Used to tell a login-handshake op16/op19 (the RSA
         * blocks) from the in-game ClientProt op16/op19 that share the same opcode number.
         */
        val state: Int,
    )

    /**
     * Verify [session] and return a [Report]. [crossValidation] is the human-readable line produced
     * by [CrossValidator] (or a clear "unavailable" reason). [clientCross] is the CLIENT-IS-KING
     * comparison produced by [ClientStateCrossChecker] — a first-class verdict input: a varp/skill
     * mismatch (or a state-bearing packet the client committed but we never captured) is a real
     * correctness FAIL because the client's committed state is authoritative.
     */
    fun verify(
        session: File,
        crossValidation: String,
        clientCross: ClientStateCrossChecker.Result,
    ): Report {
        val meta = readSessionMeta(session)
        val blobsDir = File(session, "blobs")

        val byteAccounts = ArrayList<ByteAccount>()
        val decodeStats = ArrayList<DecodeStat>()
        val desyncs = ArrayList<Desync>()
        val sanityDeltas = ArrayList<SanityDelta>()
        val nonGameSocket = ArrayList<NonGameSocket>()
        val doubleCounts = ArrayList<DoubleCount>()

        for (dir in listOf("s2c", "c2s")) {
            val framedFile = File(session, "framed-$dir.jsonl")
            if (!framedFile.exists()) continue

            val byConn = readFramed(framedFile, dir, blobsDir)
            val socketByConn = readSocketTotals(session, dir)

            // Only the game-protocol planes (login / game) participate in byte-accounting. The
            // `conn=unknown` cache-asset download (and any js5/http conn) is a different protocol on
            // the same socket plane and would pollute the accounting — it is reported separately as
            // INFO via [nonGameSocket] and otherwise ignored here.
            for ((conn, records) in byConn) {
                if (!isGameConn(conn)) continue
                byteAccounts += accountBytes(conn, dir, records, socketByConn[conn]?.bytes, desyncs)
                decodeStats += decodeRate(conn, dir, records)
                sanityDeltas += sanityCheck(conn, dir, records)
                doubleCounts += detectDoubleCount(conn, dir, records)
            }
            // Surface every non-game socket conn for this direction (cache/JS5/HTTP volume).
            for ((conn, tally) in socketByConn) {
                if (!isGameConn(conn)) nonGameSocket += NonGameSocket(conn, dir, tally.entries, tally.bytes)
            }
        }

        val (verdict, reasons) = verdict(
            byteAccounts, decodeStats, desyncs, sanityDeltas, nonGameSocket, clientCross.check, doubleCounts,
        )
        return Report(
            sessionId = meta.first,
            build = meta.second,
            serverMode = meta.third,
            byteAccounts = byteAccounts,
            decodeStats = decodeStats,
            desyncs = desyncs,
            doubleCounts = doubleCounts,
            sanityDeltas = sanityDeltas,
            crossValidation = crossValidation,
            clientCrossCheck = clientCross.check,
            clientCrossSummary = clientCross.summary,
            nonGameSocket = nonGameSocket,
            verdict = verdict,
            reasons = reasons,
        )
    }

    /**
     * A game-protocol conn (participates in byte-accounting + clean-decode + sanity). The mac dylib
     * tags the two real game sockets `login` / `game`; everything else on the socket plane —
     * `unknown` (the cache-asset download), or any `js5` / `http` / `cache` conn — is a different
     * protocol and is excluded from the game accounting.
     */
    private fun isGameConn(conn: String): Boolean = conn.lowercase() in GAME_CONNS

    // ---- 1. byte-accounting --------------------------------------------------------------------

    /**
     * On-wire footprint of one packet = opcode bytes (1 if <128, else 2) + size-prefix bytes
     * (0 fixed / 1 varByte / 2 varShort, from the codec size class) + body length (from the capture).
     */
    private fun footprintOf(dir: String, rec: FramedRecord): Int {
        val opcodeBytes = if (rec.opcode < 128) 1 else 2
        val sizeClass = if (dir == "s2c") codec.serverProtSize(rec.opcode) else codec.clientProtSize(rec.opcode)
        val prefixBytes = when (sizeClass) {
            SIZE_VAR_BYTE -> 1
            SIZE_VAR_SHORT -> 2
            else -> 0
        }
        return opcodeBytes + prefixBytes + rec.len
    }

    /**
     * True when [rec] is a client→server **login-handshake** packet — the connection-type / RSA
     * login block the `jag::LoginManager` state machine sends before the connection reaches
     * `LOGGED_IN`, not a prot-dispatched in-game [ClientProt].
     *
     * These packets traverse the SAME outgoing message queue the recorder walks, so they surface on
     * the framed plane carrying a login opcode (16 = world, 19 = lobby) and a multi-hundred-byte RSA
     * block as their body (≈664 B / 644 B). The in-game ClientProt table also defines an op16
     * (`UNKNOWN_16`, fixed 6 — `SendIfButtonN_CS2` component-press) and op19 (`UNKNOWN_19`, fixed 9 —
     * `SendOpObjCS2_2`); those are byte-accurate against the binary `RegisterAll` and are correct. The
     * login RSA block is a different protocol PHASE that merely reuses the same opcode number, so
     * asserting the in-game fixed size against it is a category error (it produced the spurious
     * framing-coherence desync this guard removes — without it, promoting op16/op19 to a confident
     * name would resurface the false desync).
     *
     * Discriminated by BOTH the opcode (a known login opcode) AND the phase (state present and below
     * [STATE_LOGGED_IN]); the recorder's per-record `state` snapshot can lag a hair behind the live
     * transition, so genuine in-game op16/op19 — which arrive at `LOGGED_IN` with their small fixed
     * bodies — are never swept up by this. The c2s `state` may also be -1 when the dylib framed the
     * packet before it could resolve the connection's client base (the `conn=unknown` lobby login
     * case); -1 (state-unknown) is therefore treated as "not yet LOGGED_IN" and counts as login-phase.
     */
    private fun isLoginHandshakeC2s(dir: String, rec: FramedRecord): Boolean {
        if (dir != "c2s") return false
        if (rec.opcode !in LOGIN_HANDSHAKE_C2S_OPCODES) return false
        return rec.state < STATE_LOGGED_IN
    }

    private fun accountBytes(
        conn: String,
        dir: String,
        records: List<FramedRecord>,
        socketTotal: Long?,
        desyncs: MutableList<Desync>,
    ): ByteAccount {
        var footprint = 0L
        var streamOffset = 0
        records.forEachIndexed { idx, rec ->
            val fp = footprintOf(dir, rec)
            // Per-packet framing coherence: a resolved length that cannot fit the declared size
            // class is the high-value mislabel signal (op78/op216/op22/op98 class). The client's
            // own resolved size is trusted; if it contradicts the codec's size class, that opcode is
            // mislabeled in OUR codec (or the dylib mis-resolved it) — record it with its offset.
            //
            // Only assert this for opcodes the codec actually MODELS: an `UNKNOWN_<n>` opcode has no
            // confident size class (its 0/stub size is a placeholder), so a mismatch there is a
            // "we don't model this" sanity signal, not a confident framing desync.
            val sizeClass = if (dir == "s2c") codec.serverProtSize(rec.opcode) else codec.clientProtSize(rec.opcode)
            val name = if (dir == "s2c") codec.serverProtName(rec.opcode) else codec.clientProtName(rec.opcode)
            // A login-handshake c2s packet (the connection-type + RSA login blocks at op16/op19,
            // sent before LOGGED_IN) is NOT prot-dispatched and must NOT be size-checked against the
            // in-game ClientProt table — that table's op16/op19 are the unrelated in-game prots that
            // merely reuse the same opcode number. See [isLoginHandshakeC2s].
            val modeled = !name.startsWith("UNKNOWN_") && !isLoginHandshakeC2s(dir, rec)
            when {
                modeled && sizeClass >= 0 && rec.len != sizeClass ->
                    desyncs += Desync(
                        conn, dir, idx, streamOffset, rec.opcode, name,
                        "fixed size class=$sizeClass but captured len=${rec.len} (codec/dylib size disagree)",
                    )
                modeled && sizeClass == SIZE_VAR_BYTE && rec.len > 0xFF ->
                    desyncs += Desync(
                        conn, dir, idx, streamOffset, rec.opcode, name,
                        "varByte size class but captured len=${rec.len} > 255 (impossible framing — mislabel?)",
                    )
                modeled && sizeClass == SIZE_VAR_SHORT && rec.len > 0xFFFF ->
                    desyncs += Desync(
                        conn, dir, idx, streamOffset, rec.opcode, name,
                        "varShort size class but captured len=${rec.len} > 65535 (impossible framing — mislabel?)",
                    )
            }
            footprint += fp
            streamOffset += fp
        }

        val socketPresent = socketTotal != null
        val socket = socketTotal ?: 0L
        // The socket plane may be "present" for a conn+dir yet have captured almost none of the real
        // stream — exactly the production case where the mac dylib's socket hook barely sampled the
        // s2c game/login ciphertext (11B / 1402B) while the `conn=unknown` cache download drowned it
        // out. A socket total that is a tiny fraction of the framed footprint is NOT evidence that the
        // frames were invented; it means the socket plane simply did not capture this direction. We
        // detect that and treat byte-accounting as UNAVAILABLE rather than letting the negative
        // residual masquerade as a "frames invented" failure.
        val socketUncaptured = socketPresent && footprint > 0L &&
            socket < footprint * MIN_SOCKET_PLAUSIBILITY_RATIO
        // Byte-accounting only stands as a real proof when the socket plane actually captured the
        // direction. When it didn't (absent, or uncaptured-tiny), there is no honest denominator.
        val accountable_ = socketPresent && !socketUncaptured

        // After the login handshake, the socket carries ONLY the prot stream. The pre-prot login
        // overhead (RSA block + first/second/third response) is bounded — never more than
        // [HANDSHAKE_MAX_PLAUSIBLE]. We estimate it as the socket surplus over the framed footprint,
        // but CAP it at that bound: anything beyond the cap is NOT handwaved as "handshake" — it is a
        // real shortfall (wire bytes that produced no framed packet => dropped packets), which must
        // drag coverage below 100%. This is what makes the byte-accounting an actual completeness
        // proof rather than a tautology.
        val surplus = maxOf(0L, socket - footprint)
        val overhead = if (accountable_) minOf(surplus, HANDSHAKE_MAX_PLAUSIBLE) else 0L
        val accountable = if (accountable_) socket - overhead else footprint
        val coverage = if (!accountable_) {
            // No usable socket ground truth for this conn — coverage is asserted only against itself.
            100.0
        } else if (accountable <= 0L) {
            if (footprint == 0L) 100.0 else 0.0
        } else {
            footprint.toDouble() / accountable.toDouble() * 100.0
        }
        // Residual = signed gap between the wire and the framed plane. Positive => wire bytes the
        // framed plane did NOT account for (drops, once the bounded handshake is removed); negative
        // => framed claims MORE bytes than the wire carried (invented/double-counted frames). Only
        // meaningful when the socket plane actually captured this direction.
        val residual = if (accountable_) socket - footprint else 0L

        return ByteAccount(
            conn = conn,
            dir = dir,
            framedPackets = records.size,
            framedFootprint = footprint,
            socketTotal = socket,
            handshakeOverhead = overhead,
            accountableSocket = accountable,
            coveragePct = coverage,
            residualBytes = residual,
            socketPresent = socketPresent,
            socketUncaptured = socketUncaptured,
        )
    }

    // ---- 2. clean-decode rate ------------------------------------------------------------------

    private fun decodeRate(conn: String, dir: String, records: List<FramedRecord>): DecodeStat {
        var decodable = 0
        var clean = 0
        var failed = 0
        var encrypted = 0
        val failing = HashMap<Int, Int>()

        for (rec in records) {
            if (dir == "c2s" && rec.xteaBody) {
                encrypted++
                continue
            }
            val hasDecoder = if (dir == "s2c") {
                codec.serverDecodersByOpcode[rec.opcode] != null
            } else {
                CapturePacketDecode.hasClientDecoder(codec, rec.opcode)
            }
            if (!hasDecoder) continue
            decodable++
            val body = rec.body
            if (body == null) {
                // Decoder exists but the body was elided (oversized blob missing) — cannot assert.
                continue
            }
            val decoded = if (dir == "s2c") {
                CapturePacketDecode.decodeServer(codec, rec.opcode, body)
            } else {
                CapturePacketDecode.decodeClient(codec, rec.opcode, body)
            }
            if (decoded != null) clean++ else {
                failed++
                failing[rec.opcode] = (failing[rec.opcode] ?: 0) + 1
            }
        }
        val rate = if (decodable == 0) 100.0 else clean.toDouble() / decodable.toDouble() * 100.0
        return DecodeStat(conn, dir, decodable, clean, failed, encrypted, rate, failing.toSortedMap())
    }

    // ---- 3. sanity vs documented production ----------------------------------------------------

    private fun sanityCheck(conn: String, dir: String, records: List<FramedRecord>): List<SanityDelta> {
        val expected = ProductionBaseline.forPlane(conn, dir) ?: return emptyList()
        val actualCounts = records.groupingBy { it.opcode }.eachCount()
        val deltas = ArrayList<SanityDelta>()

        val expectedByOp = expected.associateBy { it.opcode }
        for (exp in expected) {
            val actual = actualCounts[exp.opcode] ?: 0
            if (actual == 0) {
                deltas += SanityDelta(
                    conn, dir, exp.opcode, opName(dir, exp.opcode), 0, exp.count, "missing",
                    "documented production had ${exp.count}; this capture has none",
                )
            } else if (deviates(actual, exp.count)) {
                deltas += SanityDelta(
                    conn, dir, exp.opcode, opName(dir, exp.opcode), actual, exp.count, "count-deviation",
                    "count ${pctDelta(actual, exp.count)} vs documented ${exp.count}",
                )
            }
        }
        // Opcodes present here but never seen in the documented session: only interesting in BULK
        // (a single stray is normal session variance; a flood is a mislabel or a different protocol).
        for ((op, actual) in actualCounts) {
            if (op !in expectedByOp && actual >= UNKNOWN_OPCODE_FLOOD) {
                deltas += SanityDelta(
                    conn, dir, op, opName(dir, op), actual, 0, "unexpected-opcode",
                    "opcode absent from documented production but appears ×$actual here",
                )
            }
        }
        return deltas
    }

    // ---- double-count detection (positive "frames invented" signal) ----------------------------

    /**
     * Detect systemic frame double-counting on a conn+dir plane by the FRACTION of frames that are an
     * immediate byte-identical repeat of the previous frame. A hook emitting every packet twice
     * duplicates the whole plane (fraction ≈ 50–100%); legitimately-repeating telemetry/input (idle
     * mouse op52 streaming the same delta each tick) only repeats a small fraction. So the fraction —
     * not any single run — is the honest signal: it is flagged only when it crosses
     * [DOUBLE_COUNT_FRACTION] on a plane with enough frames ([DOUBLE_COUNT_MIN_FRAMES]) to be
     * meaningful. Returns at most one entry per plane (empty when below threshold).
     *
     * Only multi-byte bodies count toward a duplicate (empty/1-byte keepalives/flags/tick markers
     * legitimately repeat); a frame with an elided body never counts as a duplicate.
     */
    private fun detectDoubleCount(conn: String, dir: String, records: List<FramedRecord>): List<DoubleCount> {
        if (records.size < DOUBLE_COUNT_MIN_FRAMES) return emptyList()
        var duplicateFrames = 0
        var worstOpcode = -1
        var worstRun = 0
        var runStart = 0
        while (runStart < records.size) {
            val first = records[runStart]
            var end = runStart + 1
            while (end < records.size && sameFrame(first, records[end])) end++
            val runLen = end - runStart
            val body = first.body
            if (body != null && body.size >= MIN_DUP_BODY && runLen >= 2) {
                duplicateFrames += runLen - 1 // every frame after the first in a run is a repeat
                if (runLen > worstRun) { worstRun = runLen; worstOpcode = first.opcode }
            }
            runStart = end
        }
        val fraction = duplicateFrames.toDouble() / records.size.toDouble()
        if (fraction < DOUBLE_COUNT_FRACTION) return emptyList()
        return listOf(
            DoubleCount(conn, dir, duplicateFrames, records.size, worstOpcode, opName(dir, worstOpcode), worstRun),
        )
    }

    /** Two framed records are the same frame iff opcode, length, and body bytes all match (body present). */
    private fun sameFrame(a: FramedRecord, b: FramedRecord): Boolean {
        if (a.opcode != b.opcode || a.len != b.len) return false
        val ba = a.body ?: return false
        val bb = b.body ?: return false
        return ba.contentEquals(bb)
    }

    private fun opName(dir: String, opcode: Int): String =
        if (dir == "s2c") codec.serverProtName(opcode) else codec.clientProtName(opcode)

    /** A count deviates when it differs from the reference by > [SANITY_TOLERANCE_PCT] AND by > 5. */
    private fun deviates(actual: Int, expected: Int): Boolean {
        if (expected == 0) return false
        val diff = abs(actual - expected)
        if (diff <= SANITY_ABS_FLOOR) return false
        return diff.toDouble() / expected.toDouble() > SANITY_TOLERANCE_PCT
    }

    private fun pctDelta(actual: Int, expected: Int): String {
        val pct = (actual - expected).toDouble() / expected.toDouble() * 100.0
        val sign = if (pct >= 0) "+" else ""
        return "$actual ($sign${"%.0f".format(pct)}%)"
    }

    // ---- verdict -------------------------------------------------------------------------------

    private fun verdict(
        accounts: List<ByteAccount>,
        decode: List<DecodeStat>,
        desyncs: List<Desync>,
        sanity: List<SanityDelta>,
        nonGameSocket: List<NonGameSocket>,
        clientCross: ClientStateCrossCheck.CrossCheck,
        doubleCounts: List<DoubleCount>,
    ): Pair<Verdict, List<String>> {
        val reasons = ArrayList<String>()
        var verdict = Verdict.PASS

        fun fail(r: String) { reasons += "FAIL: $r"; verdict = Verdict.FAIL }
        fun warn(r: String) { reasons += "WARN: $r"; if (verdict != Verdict.FAIL) verdict = Verdict.WARN }
        fun info(r: String) { reasons += "INFO: $r" }

        // Byte-accounting is the primary completeness proof — but ONLY when the socket plane actually
        // captured the direction. A "frames invented" FAIL therefore requires socket bytes to be
        // PRESENT and materially smaller than the framed footprint; an absent or uncaptured-tiny
        // socket plane is a capture-coverage gap (WARN), not a framing fault.
        for (a in accounts) {
            val tag = "${a.conn}/${a.dir}"
            if (!a.socketPresent) {
                warn("$tag has no socket-plane ground truth — byte-accounting unavailable; completeness unproven, relying on clean-decode (framed footprint ${a.framedFootprint}B)")
                continue
            }
            if (a.socketUncaptured) {
                warn("byte-accounting unavailable for $tag: socket plane did not capture it (${a.socketTotal} socket bytes vs ${a.framedFootprint} framed) — completeness unproven, relying on clean-decode")
                continue
            }
            val shortfall = a.accountableSocket - a.framedFootprint
            if (a.residualBytes < 0) {
                // Framed footprint exceeds the socket bytes. The tiny-socket case (socket a small
                // fraction of framed — the cache-flood reality) was already diverted to a WARN above by
                // [socketUncaptured]; everything reaching here has a PRESENT, non-tiny socket that is
                // still somewhat smaller than the framed plane. That is a socket UNDER-CAPTURE, not
                // invented frames: this is the real production login/s2c case (framed ≈18 KB vs socket
                // ≈13 KB, ~140%) where the lobby s2c is read by an inline recv-ring drain + Fill in two
                // phases (plaintext handshake ++ ISAAC ciphertext) and the raw Fill plane under-samples
                // it. Every framed packet is real — the socket plane just didn't record all the bytes —
                // so completeness is UNPROVEN (WARN), NOT a "frames invented" FAIL. A genuine invented/
                // double-counted-frames fault is caught separately by [doubleCountFrames] below, which
                // is a positive duplicate detection rather than a byte-ratio guess.
                warn("$tag socket under-capture (completeness unproven): framed footprint (${a.framedFootprint}B) exceeds socket bytes (${a.socketTotal}B) by ${-a.residualBytes}B (${"%.0f".format(a.coveragePct)}%) — the socket plane recorded fewer raw bytes than the client framed (under-sampled), NOT invented frames")
            } else if (a.coveragePct < COVERAGE_FAIL_PCT) {
                fail("$tag byte coverage ${"%.2f".format(a.coveragePct)}% (< ${COVERAGE_FAIL_PCT}%); ${shortfall}B of wire data produced no framed packet (beyond a ${a.handshakeOverhead}B handshake) — packets dropped")
            } else if (a.coveragePct < COVERAGE_PASS_PCT) {
                warn("$tag byte coverage ${"%.2f".format(a.coveragePct)}% (< ${COVERAGE_PASS_PCT}%); ${shortfall}B unaccounted beyond the ${a.handshakeOverhead}B handshake estimate")
            }
        }

        // Double-counted frames — the positive "frames invented" FAIL. A plane where a large FRACTION
        // of frames are adjacent byte-identical duplicates is the signature of a framing hook emitting
        // each packet twice. Unlike the byte-ratio (which cannot tell under-capture from invention)
        // and unlike a single run (which legitimately-repeating telemetry produces), the plane-wide
        // duplicate fraction is direct evidence, so it is a hard FAIL.
        for (dc in doubleCounts) {
            fail("${dc.conn}/${dc.dir} double-counted frames: ${"%.0f".format(dc.duplicateFraction * 100)}% of ${dc.totalFrames} frames are adjacent byte-identical duplicates (worst run: op${dc.worstOpcode} ${dc.worstName} ×${dc.worstRun}) — frames invented or double-counted")
        }

        // CLIENT-IS-KING — the strongest correctness proof. Our decoded state-bearing s2c packets,
        // folded into the EXPECTED client state, must reproduce the client's OWN final snapshot (the
        // client is authoritative). A varp/skill/scalar disagreement means OUR decode is wrong; a varp
        // the client committed that we never set means a state-bearing packet was DROPPED. Either is a
        // hard FAIL — this is the bar that catches a byte-clean-but-wrong decoder. A missing snapshot
        // is not a failure (older capture); it is surfaced as INFO so the gap is visible.
        if (!clientCross.available) {
            info("client-is-king cross-check unavailable (${clientCross.reason}) — run a fresh capture to verify decoded state against the client's own committed state")
        } else if (clientCross.hasFailure) {
            val bits = ArrayList<String>()
            if (clientCross.varpMismatches.isNotEmpty()) {
                bits += "${clientCross.varpMismatches.size} varp MISMATCH " +
                    clientCross.varpMismatches.take(6).joinToString(prefix = "[", postfix = "]") {
                        "var${it.varId} ours=${it.ourValue}≠client=${it.clientValue}"
                    }
            }
            if (clientCross.varpMissed.isNotEmpty()) {
                bits += "${clientCross.varpMissed.size} varp MISSED (client committed it, we never decoded it → dropped packet) " +
                    clientCross.varpMissed.take(8).joinToString(prefix = "[", postfix = "]") { "var$it" }
            }
            if (clientCross.skillMismatches.isNotEmpty()) {
                bits += "${clientCross.skillMismatches.size} skill MISMATCH " +
                    clientCross.skillMismatches.take(6).joinToString(prefix = "[", postfix = "]") {
                        "skill${it.skillId}.${it.field} ours=${it.ourValue}≠client=${it.clientValue}"
                    }
            }
            if (clientCross.skillMissed.isNotEmpty()) bits += "${clientCross.skillMissed.size} skill MISSED"
            if (clientCross.scalarMismatches.isNotEmpty()) {
                bits += clientCross.scalarMismatches.joinToString { "${it.field} ours=${it.ourValue}≠client=${it.clientValue}" }
            }
            fail("client-is-king: our decoded state does NOT reproduce the client's committed state (client is ground truth) — ${bits.joinToString("; ")}")
        } else {
            info("client-is-king: every decoded value matches the client's own final state (${clientCross.varpMatches} varps, ${clientCross.skillMatches} skills verified)")
        }

        // A framing-coherence desync flags that a captured length disagrees with the codec's size
        // class. This is a real coherence signal worth surfacing, but it is NOT on its own a proof of
        // a broken codec: the codec size can be authoritative (verified against the binary prot table)
        // and the disagreement instead a dylib LAST_OPCODE mis-resolution. So it is a WARN, not a hard
        // FAIL — clean-decode is the arbiter of whether the modeled bodies are actually right.
        if (desyncs.isNotEmpty()) {
            val byOp = desyncs.groupingBy { "op${it.opcode} ${it.name}" }.eachCount()
            warn("${desyncs.size} framing-coherence desync(s) — captured length disagrees with the codec size class for: ${byOp.entries.joinToString { "${it.key}×${it.value}" }} (codec size may be prot-table-authoritative; suspect dylib opcode mis-resolution)")
        }

        // Clean-decode rate — the strongest evidence that the modeled bodies are correct.
        for (d in decode) {
            val tag = "${d.conn}/${d.dir}"
            if (d.decodable == 0) continue
            if (d.cleanRatePct < DECODE_FAIL_PCT) {
                fail("$tag clean-decode ${"%.1f".format(d.cleanRatePct)}% (< ${DECODE_FAIL_PCT}%); failing opcodes ${d.failingOpcodes.keys} — offset/mislabel suspects")
            } else if (d.cleanRatePct < DECODE_WARN_PCT) {
                warn("$tag clean-decode ${"%.1f".format(d.cleanRatePct)}% (< ${DECODE_WARN_PCT}%); failing opcodes ${d.failingOpcodes.keys}")
            }
        }

        // Sanity deviations are advisory — claude-re is a soft, first-iteration reference, so these
        // NEVER escalate beyond INFO (a different account / session length legitimately shifts counts,
        // and the baseline itself may be a bit off).
        val hardSanity = sanity.filter { it.kind == "unexpected-opcode" || it.kind == "missing" }
        if (hardSanity.isNotEmpty()) {
            info("${hardSanity.size} sanity deviation(s) vs documented production — ADVISORY only (claude-re baseline is a soft reference): ${hardSanity.take(6).joinToString { "op${it.opcode}:${it.kind}" }}${if (hardSanity.size > 6) ", …" else ""}")
        }
        if (sanity.size > hardSanity.size) {
            info("${sanity.size - hardSanity.size} count deviation(s) vs documented production (expected for a different account / session length)")
        }

        // Non-game socket volume (cache/JS5/HTTP) is reported as INFO, never accounted as game bytes.
        if (nonGameSocket.isNotEmpty()) {
            val totalBytes = nonGameSocket.sumOf { it.bytes }
            val totalEntries = nonGameSocket.sumOf { it.entries }
            val breakdown = nonGameSocket
                .sortedByDescending { it.bytes }
                .take(4)
                .joinToString { "${it.conn}/${it.dir} ${it.bytes}B×${it.entries}" }
            info("non-game socket volume excluded from accounting: ${totalBytes}B over $totalEntries entries (cache/JS5/HTTP) — $breakdown")
        }

        if (reasons.isEmpty()) reasons += "PASS: byte-accounting, clean-decode, and sanity all within thresholds"
        return verdict to reasons
    }

    // ---- IO: read the planes -------------------------------------------------------------------

    /** Read framed records grouped by `conn`. Bodies are decoded lazily only for the decode check. */
    private fun readFramed(file: File, dir: String, blobsDir: File): Map<String, List<FramedRecord>> {
        val byConn = LinkedHashMap<String, MutableList<FramedRecord>>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            val opcode = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
            val conn = obj["conn"]?.jsonPrimitive?.contentOrNull ?: "unknown"
            val len = obj["len"]?.jsonPrimitive?.intOrNull ?: 0
            val xtea = obj["xtea_body"]?.jsonPrimitive?.contentOrNull == "true"
            val state = obj["state"]?.jsonPrimitive?.intOrNull ?: -1
            val body = resolveBody(obj, blobsDir)
            byConn.getOrPut(conn) { ArrayList() }
                .add(FramedRecord(conn, opcode, len, xtea, body, state))
        }
        return byConn
    }

    /** Socket-plane entry count + byte total for one (conn, dir). */
    private data class SocketTally(val entries: Int, val bytes: Long)

    /** Sum socket-plane entries + `len` per conn for one direction. Absent conn => no socket data. */
    private fun readSocketTotals(session: File, dir: String): Map<String, SocketTally> {
        val file = File(session, "socket.jsonl")
        if (!file.exists()) return emptyMap()
        val totals = HashMap<String, SocketTally>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["dir"]?.jsonPrimitive?.contentOrNull != dir) return@forEachLine
            val conn = obj["conn"]?.jsonPrimitive?.contentOrNull ?: "unknown"
            val len = obj["len"]?.jsonPrimitive?.intOrNull ?: 0
            val prev = totals[conn]
            totals[conn] = SocketTally((prev?.entries ?: 0) + 1, (prev?.bytes ?: 0L) + len.toLong())
        }
        return totals
    }

    private fun resolveBody(obj: JsonObject, blobsDir: File): ByteArray? {
        obj["body"]?.jsonPrimitive?.contentOrNull?.let { b64 ->
            return runCatching { base64.decode(b64) }.getOrNull()
        }
        obj["body_ref"]?.jsonPrimitive?.contentOrNull?.let { ref ->
            val f = if (File(ref).isAbsolute) File(ref) else File(blobsDir, File(ref).name)
            if (f.exists()) return runCatching { f.readBytes() }.getOrNull()
        }
        return null
    }

    /** (session_id, build, server_mode) from session.json, with safe fallbacks. */
    private fun readSessionMeta(session: File): Triple<String, String, String> {
        val file = File(session, "session.json")
        if (!file.exists()) return Triple(session.name, "unknown", "unknown")
        val obj = runCatching { json.parseToJsonElement(file.readText()) as? JsonObject }.getOrNull()
            ?: return Triple(session.name, "unknown", "unknown")
        fun str(k: String, d: String) = obj[k]?.jsonPrimitive?.contentOrNull ?: d
        return Triple(str("session_id", session.name), str("build", "unknown"), str("server_mode", "unknown"))
    }

    companion object {
        const val SIZE_VAR_BYTE = -1
        const val SIZE_VAR_SHORT = -2

        /** Conns that carry the game protocol (and so participate in byte-accounting). */
        val GAME_CONNS = setOf("login", "game")

        /**
         * The client `MAIN_STATE` value for `LOGGED_IN` — the in-game prot-dispatch phase. Below it
         * (`LOGIN_SCREEN` = 10, `LOBBY_SCREEN` = 20) the `jag::LoginManager` handshake is in flight.
         * Mirrors the recorder's `STATE_LOGGED_IN` (see `recorder-mac/src/offsets.rs`).
         */
        const val STATE_LOGGED_IN = 30

        /**
         * Client→server opcodes that are LOGIN-HANDSHAKE packets (the connection-type + RSA login
         * block built by `jag::LoginManager::CreateLoginRSAPacket`), NOT prot-dispatched in-game
         * [org.darkan.core.net.prot.ClientProt]s: **19 = lobby login, 16 = world login** (RSA blocks
         * ≈644 B / 664 B, per `claude-re/findings/34-login-flow.md`). They reuse the same opcode
         * numbers as unrelated in-game prots, so the framing-coherence check must skip them when they
         * appear in the login phase. See [isLoginHandshakeC2s].
         */
        val LOGIN_HANDSHAKE_C2S_OPCODES = setOf(16, 19)

        /**
         * Minimum socket-bytes / framed-footprint ratio for the socket plane to count as having
         * actually captured a direction. Below this the socket plane plainly missed the stream (the
         * production s2c case: 11B/1402B socket vs ~38 KB framed, swamped by the cache download), so
         * byte-accounting is reported UNAVAILABLE rather than failing "frames invented".
         */
        const val MIN_SOCKET_PLAUSIBILITY_RATIO = 0.5

        // Verdict thresholds.
        const val COVERAGE_PASS_PCT = 99.9
        const val COVERAGE_FAIL_PCT = 95.0
        const val DECODE_WARN_PCT = 99.0
        const val DECODE_FAIL_PCT = 90.0

        /**
         * Fraction of a conn+dir plane's frames that must be adjacent byte-identical duplicates to flag
         * a double-count. A hook emitting every packet twice yields ≈50% (each packet + its echo) or
         * more; legitimately-repeating telemetry only makes a small fraction repeat. 0.5 cleanly
         * separates "the whole plane is duplicated" (FAIL) from "some telemetry idled" (fine).
         */
        const val DOUBLE_COUNT_FRACTION = 0.5

        /** Minimum frames on a plane before the duplicate-fraction is meaningful (tiny planes are noisy). */
        const val DOUBLE_COUNT_MIN_FRAMES = 20

        /**
         * Minimum body size for a frame to participate in double-count detection. Empty/1-byte frames
         * (keepalives, flags, tick markers) legitimately repeat back-to-back, so only multi-byte bodies
         * — which carry real state and should not recur byte-identically — can flag a double-count.
         */
        const val MIN_DUP_BODY = 2

        /** A login RSA block + first/second/third response is < ~2 KB; more is suspicious. */
        const val HANDSHAKE_MAX_PLAUSIBLE = 4096L

        // Sanity tolerances: a count must miss by both a fraction AND an absolute floor to flag.
        const val SANITY_TOLERANCE_PCT = 0.50
        const val SANITY_ABS_FLOOR = 5
        const val UNKNOWN_OPCODE_FLOOD = 20
    }
}
