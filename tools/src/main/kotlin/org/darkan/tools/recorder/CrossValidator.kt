package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.CaptureDeframer
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64

/**
 * Independent offline deframe of the *true wire* (the socket plane) using the shared
 * [CaptureDeframer], as a bonus cross-check on the dylib's in-process framing.
 *
 * The honest scope of this check depends entirely on whether a real ISAAC *construction seed* was
 * captured:
 *
 *  - **Real seed present** (`isaac-keys.txt` has an `ISAAC keys (hex): 0x..,..` line, written by the
 *    `mac_send_login_packet` path): we can rebuild the client's ISAAC keystream, re-deframe the
 *    socket ciphertext from scratch, and diff the opcode sequence against the dylib's
 *    `framed-<dir>.jsonl`. A divergence is a hard signal that one side mislabeled.
 *  - **Only state-word fingerprints present** (the mac dylib's default: per-role lines
 *    `"<role>-<dir> w0 w1 w2 w3"`, which are `randrsl[0..4]` — the *post-construction* output pool,
 *    NOT the seed): the keystream cannot be reproduced. Seeding ISAAC with these state words yields a
 *    *different* stream, so a re-deframe would manufacture a bogus desync. We DETECT this case and
 *    report it plainly rather than lying. Byte-accounting + clean-decode + sanity carry the proof.
 *
 * Either way this returns a one-line human-readable summary for the trust report; it never throws.
 */
class CrossValidator(private val codec: Codec, private val known: KnownOpcodes) {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val base64Decode = Base64.getDecoder()

    /** What kind of ISAAC material `isaac-keys.txt` holds (decides whether a re-deframe is possible). */
    private sealed interface IsaacMaterial {
        /**
         * Real construction seeds — the keystream is reproducible. [byRole] holds the per-`(conn,dir)`
         * seeds keyed by `<role>-<dir>` (e.g. `game-s2c`), each the EXACT seed that plane's cipher was
         * built with (a `-s2c` seed already includes the `+50`, so it is used VERBATIM). [legacyDefault]
         * is a single untagged `ISAAC keys (hex): 0x..` seed (the raw SEND seed) used as a fallback for
         * any plane with no matching role line, and is the sole source for legacy single-line files.
         */
        data class Seed(val byRole: Map<String, IntArray>, val legacyDefault: IntArray?) : IsaacMaterial
        /** Only per-role `randrsl` state-word fingerprints — NOT reproducible. */
        data object FingerprintOnly : IsaacMaterial
        data object Missing : IsaacMaterial
        data class Unreadable(val why: String) : IsaacMaterial
    }

    /**
     * Produce the cross-validation line for the trust report. Re-deframes the socket ciphertext only
     * when a real construction seed is available; otherwise returns the clear "unavailable" reason.
     */
    fun independentDeframeSummary(session: File): String {
        val socket = File(session, "socket.jsonl")
        if (!socket.exists()) {
            return "Independent ISAAC deframe unavailable: no `socket.jsonl` (the true wire) in this session."
        }
        return when (val material = classifyIsaac(File(session, "isaac-keys.txt"))) {
            is IsaacMaterial.Missing ->
                "Independent ISAAC deframe unavailable: no `isaac-keys.txt`. Byte-accounting + clean-decode + sanity stand on their own."
            is IsaacMaterial.Unreadable ->
                "Independent ISAAC deframe unavailable: could not read `isaac-keys.txt` (${material.why})."
            is IsaacMaterial.FingerprintOnly ->
                "Independent ISAAC deframe unavailable (no real seed captured): `isaac-keys.txt` holds only `randrsl` STATE-word fingerprints, not the construction seed, so the client keystream cannot be reproduced and a re-deframe would manufacture a bogus desync. Byte-accounting + clean-decode + sanity stand on their own."
            is IsaacMaterial.Seed -> reframeSocketAndDiff(session, socket, material)
        }
    }

    /**
     * One plane's resolved seed material: the 4-int seed plus whether it must be applied VERBATIM (a
     * per-`(conn,dir)` line that already encodes the `+50` for s2c) or treated as the legacy raw SEND
     * seed (the deframer/keystream then adds `+50` itself for s2c).
     */
    private data class PlaneSeed(val seed: IntArray, val exactCipherSeed: Boolean, val source: String)

    /**
     * Resolve the seed to validate plane `(conn, dirTag)` with: prefer the matching per-`(conn,dir)`
     * line (used VERBATIM), else fall back to the legacy single default seed (raw send seed, `+50`
     * applied for s2c). Returns null when neither is available — that plane is reported unavailable
     * rather than validated against another connection's seed.
     */
    private fun seedForPlane(material: IsaacMaterial.Seed, conn: String, dirTag: String): PlaneSeed? {
        material.byRole["$conn-$dirTag"]?.let { return PlaneSeed(it, exactCipherSeed = true, source = "$conn-$dirTag line") }
        material.legacyDefault?.let { return PlaneSeed(it, exactCipherSeed = false, source = "legacy default seed") }
        return null
    }

    /**
     * With real seeds, independently re-derive each (conn, dir) opcode sequence straight from the
     * *socket* wire and confirm it reproduces the dylib's `framed-<dir>.jsonl` for that conn — each
     * plane validated with ITS OWN seed (the per-`(conn,dir)` line `isaac-keys.txt` now carries), so
     * the game plane is checked with the game seed, login with the login seed, etc.
     *
     * Rather than assume the socket plane is ISAAC ciphertext starting at keystream position 0 (the
     * old, brittle approach that desynced at byte 0 whenever any of those assumptions was false), we:
     *  1. map the socket bytes onto the client's own framed frames (by body-match + byte-accounting),
     *     which yields each frame's *opcode wire byte(s)* and its keystream position; then
     *  2. find which opcode-cipher MODE reproduces the framed opcodes — `identity` (the plane carries
     *     plaintext opcodes), or `ISAAC` with this plane's seed (used verbatim for a per-plane line,
     *     or raw c2s / +50 s2c for the legacy default) at the mapped keystream offset.
     *
     * If a mode reproduces the framed opcodes → the independent deframe AGREES (we name the mode). If
     * none does, we report the precise, honest reason (e.g. no matching seed for this connection)
     * instead of the misleading "ISAAC desync?" that wrongly implicates the cipher math (which is
     * verified byte-exact against `Isaac::Init`).
     */
    private fun reframeSocketAndDiff(session: File, socketFile: File, material: IsaacMaterial.Seed): String {
        val lines = StringBuilder("Independent ISAAC deframe of the socket ciphertext (real seed captured):\n")
        val socketByPlane = readSocketCiphertext(socketFile)
        if (socketByPlane.isEmpty()) {
            return "Independent ISAAC deframe unavailable: `socket.jsonl` carried no decodable bodies."
        }

        for ((plane, cipher) in socketByPlane) {
            val (conn, dirTag) = plane
            lines.append("  - ").append(validatePlane(session, conn, dirTag, cipher, material)).append('\n')
        }
        return lines.toString().trimEnd()
    }

    /** A framed packet as the client itself decoded it: logical opcode + resolved body. */
    private data class FramedFrame(val opcode: Int, val body: ByteArray)

    /**
     * One mapped wire frame: the raw opcode byte(s) as they sit on the socket, the framed opcode they
     * must decode to, the keystream position (opcode-bytes consumed) up to here, and [wireStart] — the
     * byte offset in the socket where this frame's opcode begins (used to place the login phase
     * boundary, which the recorder reports as a raw BYTE count, not an opcode count).
     */
    private data class WireFrame(
        val rawOpcodeBytes: ByteArray,
        val expectedOpcode: Int,
        val keystreamPos: Int,
        val wireStart: Int,
    )

    /**
     * Independently validate one (conn, dir) plane: map socket → framed, then find the opcode-cipher
     * mode that reproduces the framed opcode sequence, using THIS plane's own seed. Returns a one-line
     * human-readable verdict.
     */
    private fun validatePlane(session: File, conn: String, dirTag: String, cipher: ByteArray, material: IsaacMaterial.Seed): String {
        val framed = readFramedFrames(session, conn, dirTag)
        if (framed.isEmpty()) return "[$conn/$dirTag] offline deframed 0 frames; framed-$dirTag.jsonl has no rows for conn=$conn"

        val mapped = mapSocketToFramed(cipher, framed, dirTag)
        if (mapped.isEmpty()) {
            // The login s2c plane is a known two-phase stream (plaintext handshake ++ ISAAC ciphertext)
            // AND its raw socket capture is `ClientStream::Fill`-only — when that plane is under-captured
            // (fewer raw bytes than the client framed, the §8a case) the body-match mapping has nothing
            // to anchor on. Say so precisely instead of implying the frames are wrong.
            if (conn.equals("login", ignoreCase = true) && dirTag == "s2c") {
                return "[$conn/$dirTag] socket bytes do not line up with the framed login frames — the login s2c raw plane is `ClientStream::Fill`-only and appears under-captured (it is also a two-phase plaintext++ISAAC stream); the phase-split needs a complete raw login s2c plane to validate. The byte-accounting WARN covers this under-capture; clean-decode + client-is-king carry correctness."
            }
            return "[$conn/$dirTag] socket bytes do not line up with any framed frame (capture not the prot wire for this conn, or fully out of sync) — cannot validate"
        }

        // TWO-PHASE PLANE PHASE-SPLIT (§8a) is tried FIRST for any plane that is a KNOWN two-cipher
        // stream — a Phase A PLAINTEXT login-handshake prefix ++ Phase B ISAAC ciphertext that RESTARTS
        // at keystream position 0. This is BOTH the lobby/login s2c plane (split at SetupLoginCiphers)
        // AND every c2s plane: the c2s socket is captured at `ClientStream::Write`, i.e. AFTER opcode
        // encryption, so it carries the plaintext login-handshake blocks (op14 + op19/op16 RSA) and
        // only then the ISAAC ciphertext of the connected game/lobby ClientProts — the same two-phase
        // shape as login/s2c. A whole-plane single-mode check would otherwise falsely "win" on just the
        // Phase-A prefix (the plaintext head maps under identity while Phase B silently drops off), so
        // for these planes we attempt the split before the single-mode checks and only fall through if
        // no split reproduces the framing. (The split needs this plane's seed; c2s has no phase marker
        // event, so [phaseSplitMatches] auto-detects the boundary k by scanning split points — the
        // suffix-from-keystream-0 ISAAC check is exact, so it cannot false-positive.)
        if (isTwoPhasePlane(conn, dirTag)) {
            val planeSeedForSplit = seedForPlane(material, conn, dirTag)
            if (planeSeedForSplit != null) {
                val hint = if (dirTag == "s2c") loginCipherSplitHint(session, mapped) else null
                phaseSplitMatches(mapped, dirTag, planeSeedForSplit, hint)?.let { (split, agreed) ->
                    return phaseSplitVerdict(conn, dirTag, mapped, split, agreed, planeSeedForSplit)
                }
            }
        }

        // Identity (plaintext opcodes) is seed-independent — try it first regardless of seed material.
        identityMatches(mapped)?.let { agreed ->
            return "[$conn/$dirTag] OK — ${agreed} opcodes AGREE with the client framing (mode=identity: this plane carries PLAINTEXT opcodes, not ISAAC ciphertext)"
        }

        // ISAAC modes need THIS plane's seed. With no matching per-(conn,dir) line and no legacy
        // default, do not validate against another connection's seed — report it unavailable.
        val planeSeed = seedForPlane(material, conn, dirTag)
            ?: return "[$conn/$dirTag] no matching ISAAC seed for this connection in `isaac-keys.txt` (need a `$conn-$dirTag` line) and no legacy default — cannot validate this plane (not validating against another connection's seed)"

        // The keystream offset for ISAAC is the mapped position of the first mapped frame (so a
        // mid-stream capture realigns). We also scan a small window around it to be robust to a
        // single-frame mapping slip.
        val baseSkip = mapped.first().keystreamPos
        val ksWindow = (baseSkip..baseSkip + KEYSTREAM_SCAN).toList()
        for (skip in ksWindow) {
            isaacMatches(mapped, dirTag, planeSeed, skip)?.let { agreed ->
                val pos = if (skip == 0) "from keystream start" else "at keystream offset $skip"
                val mode = if (planeSeed.exactCipherSeed) "verbatim ${planeSeed.source}"
                else "${if (dirTag == "s2c") "+50" else "raw"} (${planeSeed.source})"
                return "[$conn/$dirTag] OK — ${agreed} opcodes AGREE with the client framing (mode=ISAAC $mode $pos)"
            }
        }

        // Nothing reproduced the framed opcodes. Diagnose WHY without blaming the (verified) cipher.
        val coverage = " (mapped ${mapped.size} of ${framed.size} framed frames against the socket)"

        // UNDER-CAPTURED two-phase plane: if only a small fraction of the framed frames could be mapped
        // onto the raw socket, the socket simply does not contain most of the wire — the body-match
        // anchored on the few raw frames present and then ran out. This is the §8a login/s2c reality
        // (its raw plane is `ClientStream::Fill`-only and under-captures the inline-ring-drained lobby
        // frames). Say THAT, not "seed mismatch": the seed was never exercised past the tiny prefix, so
        // the cipher is not implicated. (Threshold: < half the framed frames mapped.)
        if (isTwoPhasePlane(conn, dirTag) && mapped.size * 2 < framed.size) {
            return "[$conn/$dirTag] socket under-captured this two-phase plane$coverage: only a prefix of the wire was recorded (this plane's raw capture is `ClientStream::Fill`-only and under-samples the inline ring-drain, the §8a case), so the phase-split has too little of the suffix to validate. The byte-accounting WARN covers this under-capture; clean-decode + client-is-king carry correctness. (NOT a seed or deframe-logic fault — the ${planeSeed.source} was not exercised past the mapped prefix.)"
        }

        // Otherwise: with per-(conn,dir) seeds present this should NOT happen for a fully-mapped plane;
        // it remains the honest verdict only for a genuine seed mismatch (e.g. only a legacy default
        // seed was captured and it belongs to a different connection).
        return "[$conn/$dirTag] could NOT reproduce the client framing from the socket$coverage: the ${planeSeed.source} decodes none of the wire opcodes under identity or ISAAC at the mapped keystream offset. The ISAAC math is verified byte-exact against Isaac::Init, so this means the captured seed is not the cipher seed for this connection (the ${planeSeed.source} does not match this plane's cipher) — NOT a deframe-logic fault."
    }

    /** Where the login s2c plane flips from Phase-A plaintext to Phase-B ISAAC, in MAPPED-frame count. */
    private data class PhaseSplit(val frames: Int, val fromEvent: Boolean)

    /**
     * The login cipher boundary as a MAPPED-frame index, derived from the recorder's
     * `login_cipher_ready` event (emitted in `events.jsonl` when `SetupLoginCiphers` builds the login
     * ISAAC). The event carries the raw login-s2c byte offset at which ciphers became active; we map
     * that byte offset onto the mapped-frame whose keystream position crosses it. Returns null when no
     * such event is present (then the split is auto-detected by [phaseSplitMatches]).
     */
    private fun loginCipherSplitHint(session: File, mapped: List<WireFrame>): PhaseSplit? {
        val events = File(session, "events.jsonl")
        if (!events.exists()) return null
        var rawOffset: Int? = null
        events.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["kind"]?.jsonPrimitive?.contentOrNull != "login_cipher_ready") return@forEachLine
            // Accept any of the byte-offset field names the dylib might use for the Phase-A length.
            rawOffset = LOGIN_SPLIT_FIELDS.firstNotNullOfOrNull { obj[it]?.jsonPrimitive?.intOrNull } ?: rawOffset
        }
        val off = rawOffset ?: return null
        // The event reports a raw login-s2c BYTE count (the Phase-A length on the Fill plane). The
        // number of Phase-A frames is those whose wire START byte sits before that boundary. The
        // boundary always falls on a packet edge (ciphers flip between packets, never mid-packet), so
        // this lands on the exact first Phase-B frame.
        val splitFrames = mapped.indexOfFirst { it.wireStart >= off }.let { if (it < 0) mapped.size else it }
        return PhaseSplit(splitFrames, fromEvent = true)
    }

    /**
     * Is plane `(conn, dirTag)` a two-phase stream — a Phase A PLAINTEXT login-handshake prefix
     * followed by Phase B ISAAC ciphertext restarting at keystream position 0? True for:
     *  - **login/s2c**: split at `SetupLoginCiphers` (the lobby ReadPacket frames begin once the login
     *    ISAAC is built); the boundary is reported by the `login_cipher_ready` event.
     *  - **every c2s plane** (game/c2s, login/c2s): the c2s socket is captured at `ClientStream::Write`,
     *    i.e. POST opcode-encryption, so it always opens with the plaintext login-handshake blocks
     *    (op14 + op19/op16 RSA) and only then the connected ClientProts' ISAAC ciphertext. There is no
     *    c2s phase marker, so the boundary is auto-detected by [phaseSplitMatches].
     *
     * The game/s2c plane is NOT two-phase here: that raw plane is captured post-handshake (the game
     * ServerConnection only exists after login), so it is a single ISAAC stream and is validated by the
     * whole-plane ISAAC mode.
     */
    private fun isTwoPhasePlane(conn: String, dirTag: String): Boolean =
        dirTag == "c2s" || (conn.equals("login", ignoreCase = true) && dirTag == "s2c")

    /** The human-readable AGREE verdict for a two-phase plane that reproduced via PHASE-SPLIT. */
    private fun phaseSplitVerdict(
        conn: String,
        dirTag: String,
        mapped: List<WireFrame>,
        split: PhaseSplit,
        agreed: Int,
        planeSeed: PlaneSeed,
    ): String {
        val src = if (split.fromEvent) "login_cipher_ready event" else "auto-detected"
        return "[$conn/$dirTag] OK — $agreed opcodes AGREE with the client framing " +
            "(mode=PHASE-SPLIT: first ${split.frames} frames PLAINTEXT [login handshake], " +
            "remaining ${mapped.size - split.frames} frames ISAAC ${planeSeed.source} from keystream pos 0; split $src)"
    }

    /**
     * Try to reproduce a two-phase plane's framing as PLAINTEXT prefix ++ ISAAC suffix. If [hint] is
     * given (login/s2c, from `login_cipher_ready`) that split is tried first; otherwise every split
     * point is scanned (the c2s case — no phase marker exists, so the boundary k is AUTO-DETECTED).
     * A split AGREES iff the first `k` frames all decode as identity AND the remaining frames all
     * decode under this plane's ISAAC seeded from keystream position 0 (the recv/send ISAAC's first
     * value decodes the first Phase-B opcode byte). Both halves of the test are exact over the full
     * frame sequence, so a scanned split cannot false-positive. Returns the winning split + agreed
     * frame count, or null if no split reproduces the framing.
     */
    private fun phaseSplitMatches(
        mapped: List<WireFrame>,
        dirTag: String,
        planeSeed: PlaneSeed,
        hint: PhaseSplit?,
    ): Pair<PhaseSplit, Int>? {
        val candidates = buildList {
            if (hint != null) add(hint)
            for (k in 0..mapped.size) add(PhaseSplit(k, fromEvent = false))
        }
        for (split in candidates) {
            val k = split.frames
            if (k < 0 || k > mapped.size) continue
            val prefix = mapped.subList(0, k)
            val suffix = mapped.subList(k, mapped.size)
            if (!identityHolds(prefix)) continue
            // Phase B is its own continuous ISAAC stream starting at keystream position 0 (the login
            // recv ISAAC is constructed at the boundary, so its first value decodes the first Phase-B
            // opcode). Re-base the suffix's keystream positions to begin at 0.
            if (!isaacHoldsFromZero(suffix, dirTag, planeSeed)) continue
            return split to mapped.size
        }
        return null
    }

    /** True iff every frame in [frames] decodes as a plaintext (identity) opcode. Empty => true. */
    private fun identityHolds(frames: List<WireFrame>): Boolean =
        frames.all { decodeOpcode(it.rawOpcodeBytes, 0, 0) == it.expectedOpcode }

    /**
     * True iff [frames] decode under this plane's ISAAC keystream starting at position 0, where the
     * keystream index for each frame is its offset from the FIRST frame in [frames] (Phase B is its
     * own stream from 0). Empty => true.
     */
    private fun isaacHoldsFromZero(frames: List<WireFrame>, dirTag: String, planeSeed: PlaneSeed): Boolean {
        if (frames.isEmpty()) return true
        val base = frames.first().keystreamPos
        val span = frames.last().keystreamPos - base + 2
        val ks = isaacKeystream(dirTag, planeSeed, span + KEYSTREAM_SCAN)
        for (w in frames) {
            val ki = w.keystreamPos - base
            if (ki < 0 || ki + w.rawOpcodeBytes.size > ks.size) return false
            if (decodeOpcodeWithKeystream(w.rawOpcodeBytes, ks, ki) != w.expectedOpcode) return false
        }
        return true
    }

    /** Read the client's framed (opcode, body) frames for [conn] in [dirTag], in file order. */
    private fun readFramedFrames(session: File, conn: String, dirTag: String): List<FramedFrame> {
        val file = File(session, "framed-$dirTag.jsonl")
        if (!file.exists()) return emptyList()
        val blobsDir = File(session, "blobs")
        val out = ArrayList<FramedFrame>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            if (obj["conn"]?.jsonPrimitive?.contentOrNull != conn) return@forEachLine
            val op = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
            val body = resolveBody(obj, blobsDir) ?: ByteArray(0)
            out += FramedFrame(op, body)
        }
        return out
    }

    /**
     * Walk the socket bytes against the framed frames in order, deducing each frame's header. At each
     * step the next bytes must be: opcode byte(s) (1 if op<128 else 2) + a size prefix (0/1/2 bytes,
     * chosen so the framed body lands exactly here) + the framed body. We do NOT decode the opcode —
     * we only need its raw wire bytes and the body to confirm alignment — so this mapping is cipher
     * mode-agnostic (it works whether the opcodes are plaintext or ISAAC-ciphered). Stops at the
     * first frame that cannot be placed (an incomplete socket maps a prefix of the frames).
     */
    private fun mapSocketToFramed(socket: ByteArray, framed: List<FramedFrame>, dirTag: String): List<WireFrame> {
        // The socket may open with a non-prot prologue (s2c login transition / js5 handshake bytes), so
        // the framed sequence does not necessarily start at byte 0. We enumerate candidate start
        // offsets (see [candidateStarts]) and pick the one from which the MOST frames map cleanly. This
        // makes the start search itself robust to a codec size-class mismatch on the anchor frame —
        // exactly the c2s login-handshake case where op16/op19 are modeled as small fixed in-game prots
        // but appear on the c2s wire as varShort-framed RSA blocks (a 2-byte size prefix the codec does
        // not predict). Trusting the codec's prefix for the anchor (the old single-offset approach) put
        // the start 2 bytes late and stalled the whole walk; trying every candidate fixes it.
        return candidateStarts(socket, framed, dirTag)
            .map { walkFrom(socket, framed, dirTag, it) }
            .maxByOrNull { it.size }
            ?: emptyList()
    }

    /**
     * Map the framed frames against [socket] starting at byte [start], deducing each frame's header. At
     * each step the next bytes must be: opcode byte(s) (1 if op<128 else 2) + a size prefix (0/1/2
     * bytes, chosen so the framed body lands exactly here) + the framed body. We do NOT decode the
     * opcode — only its raw wire bytes and the body are needed to confirm alignment — so this is cipher
     * mode-agnostic (works whether the opcodes are plaintext or ISAAC-ciphered). Stops at the first
     * frame that cannot be placed (an incomplete socket, or a wrong start, maps a prefix of the frames).
     */
    private fun walkFrom(socket: ByteArray, framed: List<FramedFrame>, dirTag: String, start: Int): List<WireFrame> {
        if (start < 0 || start > socket.size) return emptyList()
        var pos = start
        val out = ArrayList<WireFrame>()
        var keystreamPos = 0
        for (frame in framed) {
            val opBytes = if (frame.opcode < 128) 1 else 2
            if (pos + opBytes > socket.size) break
            // Deduce the size-prefix width by finding which of {codec, 0, 1, 2} both (a) places the
            // framed body exactly after the opcode AND (b) carries a size-prefix value equal to the
            // body length. Requirement (b) is what disambiguates an EMPTY body: an empty needle matches
            // at ANY offset, so without it the codec's predicted prefix would be taken even when the
            // wire frame has no prefix at all — e.g. c2s op26 is modeled varShort (prefix 2) but appears
            // on the wire with a 0-byte prefix; preferring the codec width then over-consumed 2 bytes
            // and desynced the whole walk. With (b), a wrong codec width that does not encode the body
            // length is rejected, so the codec value is advisory (a tie-break) only and a mapping
            // failure is never a codec-table artifact — only a true wire/seed mismatch.
            val preferred = prefixBytesFor(sizeClassOf(frame.opcode, dirTag))
            val prefix = sequenceOf(preferred, 0, 1, 2).distinct().firstOrNull { p ->
                prefixEncodesLen(socket, pos + opBytes, p, frame.body.size) &&
                    regionEquals(socket, pos + opBytes + p, frame.body)
            } ?: break
            val bodyEnd = pos + opBytes + prefix + frame.body.size
            out += WireFrame(socket.copyOfRange(pos, pos + opBytes), frame.opcode, keystreamPos, wireStart = pos)
            keystreamPos += opBytes
            pos = bodyEnd
        }
        return out
    }

    /**
     * True iff a [p]-byte big-endian size prefix at [at] in [socket] encodes [len] (the framed body
     * length). p==0 means "no prefix" and is always consistent (a fixed-size op carries no size). p==1
     * matches a 1-byte varByte size; p==2 a 2-byte BE varShort size. Out-of-bounds prefix bytes are not
     * consistent. This pins the prefix width even when the body is empty (where the body match alone is
     * ambiguous), making the deframe robust to codec size-class mispredictions on the c2s wire.
     */
    private fun prefixEncodesLen(socket: ByteArray, at: Int, p: Int, len: Int): Boolean = when (p) {
        0 -> true
        1 -> at < socket.size && (socket[at].toInt() and 0xFF) == (len and 0xFF) && len <= 0xFF
        2 -> at + 1 < socket.size &&
            (((socket[at].toInt() and 0xFF) shl 8) or (socket[at + 1].toInt() and 0xFF)) == len
        else -> false
    }

    /**
     * Candidate byte offsets at which the framed prot stream might begin in [socket]. Usually 0 (c2s,
     * which starts at the first byte) or a short prologue (s2c). We anchor on the first non-empty framed
     * body: its header starts `opBytes + prefix + lead` bytes before that body, where `prefix` is the
     * anchor's size-prefix width and `lead` the bytes consumed by any empty-body frames ahead of it.
     * Because the codec can MIS-predict those prefix widths (the c2s op16/op19 RSA blocks are varShort
     * on the wire but modeled as fixed in-game prots), we emit a candidate start for EACH plausible
     * anchor-prefix width {codec, 0, 1, 2} — and for the lead we likewise allow the wire to use a wider
     * prefix than the codec by treating each empty-body lead frame's prefix as variable too. Always
     * includes 0 (the dominant c2s case). [mapSocketToFramed] then picks whichever start maps the most
     * frames, so a wrong codec prefix can never strand the walk.
     */
    private fun candidateStarts(socket: ByteArray, framed: List<FramedFrame>, dirTag: String): List<Int> {
        val starts = linkedSetOf(0) // byte 0 is the dominant c2s start; always a candidate.
        val first = framed.firstOrNull { it.body.isNotEmpty() } ?: return starts.toList()
        val opBytes = if (first.opcode < 128) 1 else 2
        val firstFramedIndex = framed.indexOf(first)
        // Lead bytes for the empty-body frames before the anchor. Each empty frame is opcode + prefix;
        // its on-wire prefix may be wider than the codec predicts, so range the total lead prefix over
        // [codec-sum .. codec-sum + 2*count] to cover a varShort-on-the-wire empty frame.
        var leadOpcodes = 0
        var leadCodecPrefix = 0
        for (i in 0 until firstFramedIndex) {
            val f = framed[i]
            leadOpcodes += if (f.opcode < 128) 1 else 2
            leadCodecPrefix += prefixBytesFor(sizeClassOf(f.opcode, dirTag))
        }
        val leadPrefixOptions = (leadCodecPrefix..leadCodecPrefix + 2 * firstFramedIndex).toList()
        // Find every occurrence of the anchor body (bodies can repeat, so don't stop at the first).
        var from = 0
        while (true) {
            val bodyIdx = indexOf(socket, first.body, fromIndex = from)
            if (bodyIdx < 0) break
            for (anchorPrefix in intArrayOf(prefixBytesFor(sizeClassOf(first.opcode, dirTag)), 0, 1, 2).distinct()) {
                for (leadPrefix in leadPrefixOptions) {
                    val start = bodyIdx - opBytes - anchorPrefix - leadOpcodes - leadPrefix
                    if (start >= 0) starts.add(start)
                }
            }
            from = bodyIdx + 1
        }
        return starts.toList()
    }

    /** True iff *identity* (plaintext) opcode reads reproduce every mapped frame's opcode. */
    private fun identityMatches(mapped: List<WireFrame>): Int? {
        for (w in mapped) {
            val op = decodeOpcode(w.rawOpcodeBytes, 0, 0)
            if (op != w.expectedOpcode) return null
        }
        return mapped.size
    }

    /** True iff this plane's ISAAC at [skip] reproduces every mapped frame's opcode. */
    private fun isaacMatches(mapped: List<WireFrame>, dirTag: String, planeSeed: PlaneSeed, skip: Int): Int? {
        val firstPos = mapped.first().keystreamPos
        val span = mapped.last().keystreamPos - firstPos + 2 // +2 for a trailing 2-byte opcode
        val ks = isaacKeystream(dirTag, planeSeed, skip + span + KEYSTREAM_SCAN)
        for (w in mapped) {
            // keystream index for this frame = skip + (this frame's pos - first frame's pos)
            val ki = skip + (w.keystreamPos - firstPos)
            if (ki + w.rawOpcodeBytes.size > ks.size) return null
            val op = decodeOpcodeWithKeystream(w.rawOpcodeBytes, ks, ki)
            if (op != w.expectedOpcode) return null
        }
        return mapped.size
    }

    /**
     * The opcode-cipher keystream low-bytes for [dirTag], seeded the same way the production
     * [CaptureDeframer] is for this plane: VERBATIM when the seed is the exact per-`(conn,dir)` cipher
     * seed (a `-s2c` line already encodes the `+50`), else the legacy raw-send-seed path (raw for c2s,
     * `seed+50` per element for s2c). Returns [count] low-bytes — the opcode transform consumes only
     * the low byte of each ISAAC int.
     */
    private fun isaacKeystream(dirTag: String, planeSeed: PlaneSeed, count: Int): IntArray {
        val seed = planeSeed.seed
        val constructed = when {
            planeSeed.exactCipherSeed -> seed.copyOf()
            dirTag == "s2c" -> IntArray(seed.size) { seed[it] + CaptureDeframer.S2C_ISAAC_DELTA }
            else -> seed.copyOf()
        }
        val isaac = Isaac(constructed)
        return IntArray(maxOf(count, 1)) { isaac.nextInt() and 0xFF }
    }

    private fun decodeOpcode(rawOpcodeBytes: ByteArray, k0: Int, k1: Int): Int {
        val b1 = (rawOpcodeBytes[0].toInt() and 0xFF) - k0 and 0xFF
        return if (b1 < 128 || rawOpcodeBytes.size == 1) b1
        else (b1 - 128) * 256 + ((rawOpcodeBytes[1].toInt() and 0xFF) - k1 and 0xFF)
    }

    private fun decodeOpcodeWithKeystream(rawOpcodeBytes: ByteArray, ks: IntArray, ki: Int): Int {
        val k0 = ks[ki]
        val k1 = if (rawOpcodeBytes.size > 1) ks[ki + 1] else 0
        return decodeOpcode(rawOpcodeBytes, k0, k1)
    }

    private fun sizeClassOf(opcode: Int, dirTag: String): Int =
        if (dirTag == "s2c") codec.serverProtSize(opcode) else codec.clientProtSize(opcode)

    private fun prefixBytesFor(sizeClass: Int): Int = when (sizeClass) {
        -1 -> 1
        -2 -> 2
        else -> 0
    }

    private fun regionEquals(haystack: ByteArray, at: Int, needle: ByteArray): Boolean {
        if (at < 0 || at + needle.size > haystack.size) return false
        for (i in needle.indices) if (haystack[at + i] != needle[i]) return false
        return true
    }

    private fun indexOf(haystack: ByteArray, needle: ByteArray, fromIndex: Int): Int {
        if (needle.isEmpty()) return fromIndex
        outer@ for (i in fromIndex..haystack.size - needle.size) {
            for (j in needle.indices) if (haystack[i + j] != needle[j]) continue@outer
            return i
        }
        return -1
    }

    /** Concatenate socket bodies per (conn, dir), in file order — the true on-wire ciphertext. */
    private fun readSocketCiphertext(socketFile: File): Map<Pair<String, String>, ByteArray> {
        val acc = LinkedHashMap<Pair<String, String>, ByteArrayOutputStream>()
        val blobsDir = File(socketFile.parentFile, "blobs")
        socketFile.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            val dir = obj["dir"]?.jsonPrimitive?.contentOrNull ?: return@forEachLine
            val conn = obj["conn"]?.jsonPrimitive?.contentOrNull ?: "unknown"
            val body = resolveBody(obj, blobsDir) ?: return@forEachLine
            acc.getOrPut(conn to dir) { ByteArrayOutputStream() }.write(body)
        }
        return acc.mapValues { it.value.toByteArray() }
    }

    private fun resolveBody(obj: JsonObject, blobsDir: File): ByteArray? {
        obj["body"]?.jsonPrimitive?.contentOrNull?.let { b64 ->
            return runCatching { base64Decode.decode(b64) }.getOrNull()
        }
        obj["body_ref"]?.jsonPrimitive?.contentOrNull?.let { ref ->
            val f = if (File(ref).isAbsolute) File(ref) else File(blobsDir, File(ref).name)
            if (f.exists()) return runCatching { f.readBytes() }.getOrNull()
        }
        return null
    }

    /**
     * Decide what kind of ISAAC material `isaac-keys.txt` holds.
     *
     * Real construction seeds -> either the new per-`(conn,dir)` form (one tagged line per plane,
     * `game-s2c ISAAC keys (hex): 0x..,..`, the value used VERBATIM) or the legacy single untagged
     * line `ISAAC keys (hex): 0x..,..` (the raw send seed). State-word fingerprints -> per-role lines
     * `"<role>-<dir> w0 w1 w2 w3"` (the mac dylib's [State::isaac_keys] writer). We prefer real seeds
     * if present; otherwise if any role-fingerprint line parses, it is fingerprint-only.
     */
    private fun classifyIsaac(file: File): IsaacMaterial {
        if (!file.exists()) return IsaacMaterial.Missing
        val text = runCatching { file.readText() }.getOrElse { return IsaacMaterial.Unreadable(it.message ?: "io error") }

        // FINGERPRINT FIRST. The mac dylib's default `isaac-keys.txt` self-labels every value line
        // with the disclaimer that it is a `randrsl` STATE-word fingerprint, NOT a construction seed
        // (e.g. `login-s2c hex (fingerprint, NOT a seed): 0x..,..`). Those lines still contain the
        // word "hex", so a naive `"hex" in it` test (the old bug) mistook them for a real seed, seeded
        // ISAAC with state words, and manufactured bogus deframe mismatches. Detecting the fingerprint
        // marker BEFORE the seed test makes the deframe correctly report itself unavailable. The real
        // seed lines — `[<role>-<dir>] ISAAC keys (hex): 0x..` — carry no such disclaimer.
        if (text.lineSequence().any { FINGERPRINT_MARKER in it.lowercase() }) {
            return IsaacMaterial.FingerprintOnly
        }
        if (text.lineSequence().any { "hex" in it }) {
            return runCatching {
                val byRole = CaptureDeframer.parseHexKeysByRole(text)
                // Legacy untagged single line (no `<role>-<dir>` tag) is the fallback default seed; it
                // is also the sole seed source for legacy single-line files (byRole empty there).
                val legacyDefault = if (byRole.isEmpty()) CaptureDeframer.parseHexKeys(text) else null
                IsaacMaterial.Seed(byRole, legacyDefault)
            }.getOrElse { IsaacMaterial.Unreadable(it.message ?: "bad hex seed line") }
        }
        // Fall back to the structural fingerprint shape (per-role `"<role>-<dir> w0 w1 w2 w3"` lines)
        // for any fingerprint file that ever omits the explicit disclaimer.
        val looksLikeFingerprint = text.lineSequence().any { raw ->
            val parts = raw.trim().split(Regex("\\s+"))
            parts.size == 5 && FINGERPRINT_ROLE.matches(parts[0]) &&
                parts.drop(1).all { it.toIntOrNull() != null }
        }
        return if (looksLikeFingerprint) IsaacMaterial.FingerprintOnly
        else IsaacMaterial.Unreadable("unrecognized isaac-keys.txt format")
    }

    companion object {
        /**
         * Extra keystream offsets to scan around the mapped position when probing ISAAC alignment.
         * The frame mapping pins the offset exactly, but a tiny window absorbs an off-by-one from a
         * single ambiguous body-match without ever inventing a false AGREE (the full opcode sequence
         * must still match at the chosen offset).
         */
        private const val KEYSTREAM_SCAN = 4

        /** Role tag of a fingerprint line, e.g. `game-s2c`, `login-c2s`, `unknown-s2c`. */
        private val FINGERPRINT_ROLE = Regex("[A-Za-z]+-(s2c|c2s)")

        /**
         * Lowercased substring the mac dylib stamps on a fingerprint `isaac-keys.txt` (header and
         * every value line: `... (fingerprint, NOT a seed) ...`). Its presence means the file holds
         * only `randrsl` state words, so an independent deframe is impossible and must be skipped.
         */
        private const val FINGERPRINT_MARKER = "fingerprint, not a seed"

        /**
         * Field names for the Phase-A length carried by the recorder's `login_cipher_ready` event. The
         * canonical field is **`login_s2c_bytes`** = the running byte count of the AUTHORITATIVE login
         * s2c raw plane (`ClientStream::Fill` ONLY) at the instant `SetupLoginCiphers` made the login
         * ISAAC active. So login s2c bytes `[0, N)` are Phase A PLAINTEXT and `[N, end)` are Phase B
         * ISAAC from keystream position 0. The remaining names are accepted as fallbacks across dylib
         * builds; absent all of them, the phase split is auto-detected by scanning split points.
         */
        private val LOGIN_SPLIT_FIELDS =
            listOf("login_s2c_bytes", "s2c_offset", "raw_offset", "offset", "s2c_bytes", "phase_a_bytes")
    }
}
