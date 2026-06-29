package org.darkan.tools.recorder

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.prot.update.AppearanceSlot
import org.darkan.core.net.prot.update.PlayerAppearance
import org.darkan.core.net.prot.update.PlayerAppearanceEncoder
import org.darkan.core.net.recorder.CapturePacketDecode
import org.darkan.core.net.recorder.CaptureDeframer
import org.darkan.core.net.recorder.ClientStateCrossCheck
import org.darkan.core.net.recorder.Confidence
import org.darkan.core.net.recorder.ConfidenceAssigner
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.Base64
import kotlin.system.exitProcess

/**
 * Self-test for the capture trust verifier (same `main()`-with-counters convention as the other
 * `:tools` *SelfTest / *Test executables, since the module has no JUnit harness).
 *
 * Synthesizes byte-exact recorder sessions in a temp dir and asserts the verifier reaches the right
 * verdict for each: a clean full world-entry (PASS byte-accounting, sanity matches the documented
 * 948-5 session), a dropped-packet capture (FAIL coverage), a framing desync (WARN — a coherence
 * signal, not a hard FAIL), invented frames where the socket plane IS present and materially smaller
 * (FAIL), the fingerprint-only ISAAC case in both the structural and the labeled
 * `hex (fingerprint, NOT a seed)` production forms (cross-validation reports "no real seed", never a
 * bogus desync), and the production socket-uncaptured case (a tiny s2c socket beside a `conn=unknown`
 * cache flood -> byte-accounting UNAVAILABLE/WARN, never "frames invented"). Validates the on-wire
 * footprint formula matches the live codec's size classes.
 *
 * The independent-deframe block additionally proves the CrossValidator reproduces the client framing
 * from the raw socket: a legacy single-seed capture (`isaac-agree`), a plaintext socket
 * (`plaintext-agree`), a wrong seed (`wrong-seed`), and — for the recorder's new multi-line
 * `isaac-keys.txt` — PER-CONNECTION seeds where the game and login connections each validate with
 * their OWN seed (`per-conn-seeds`), plus a swapped-seed negative control (`per-conn-swapped`).
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.recorder.TrustReportSelfTestKt
 */
fun main() {
    val codec = register948()
    val verifier = SessionTrustVerifier(codec, KnownOpcodes.empty())
    val cross = CrossValidator(codec, KnownOpcodes.empty())
    val clientCrossChecker = ClientStateCrossChecker(codec)
    val b64 = Base64.getEncoder()

    /** Run the verifier with BOTH cross-checks (client-king + independent deframe) for [dir]. */
    fun verify(dir: File): SessionTrustVerifier.Report =
        verifier.verify(dir, cross.independentDeframeSummary(dir), clientCrossChecker.crossCheck(dir))

    var passed = 0
    var failed = 0
    fun pass(m: String) { println("  PASS: $m"); passed++ }
    fun fail(m: String) { println("  FAIL: $m"); failed++ }

    val tmp = Files.createTempDirectory("trust-selftest").toFile()

    // ---- helpers to write a session --------------------------------------------------------------

    fun footprint(dir: String, op: Int, len: Int): Int {
        val opBytes = if (op < 128) 1 else 2
        val sc = if (dir == "s2c") codec.serverProtSize(op) else codec.clientProtSize(op)
        val pre = when (sc) { -1 -> 1; -2 -> 2; else -> 0 }
        return opBytes + pre + len
    }

    // (op, count, len) populations transcribed from the documented world-entry session.
    val worldS2c = listOf(
        Triple(1, 1, 0), Triple(3, 1, 19), Triple(5, 1, 0), Triple(7, 1, 0), Triple(12, 1, 2),
        Triple(13, 1, 1), Triple(16, 24, 2), Triple(17, 6, 9), Triple(22, 54, 68), Triple(26, 1, 0),
        Triple(28, 491, 6), Triple(30, 2, 8), Triple(35, 871, 12), Triple(44, 29, 6), Triple(45, 1, 1),
        Triple(46, 21, 5), Triple(47, 61, 3), Triple(49, 1, 0), Triple(52, 53, 76), Triple(54, 1, 44),
        Triple(55, 1, 0), Triple(61, 1176, 3), Triple(62, 2, 4), Triple(64, 2, 6), Triple(67, 1, 0),
        Triple(73, 1, 2), Triple(74, 1, 4), Triple(75, 1, 0), Triple(76, 64, 10), Triple(77, 1, 121),
        Triple(78, 616, 3), Triple(80, 1, 1), Triple(81, 1, 5137), Triple(82, 56, 23), Triple(85, 8, 17),
        Triple(90, 2, 6), Triple(91, 11, 5), Triple(92, 35, 3), Triple(93, 1, 73), Triple(95, 1, 5),
        Triple(104, 8, 14), Triple(110, 86, 15), Triple(119, 8, 35), Triple(120, 1, 0), Triple(121, 1, 11),
        Triple(122, 7, 205), Triple(130, 1, 10), Triple(147, 5, 10), Triple(154, 1, 5), Triple(157, 1, 1),
        Triple(162, 54, 0), Triple(172, 2, 1), Triple(174, 5, 8), Triple(190, 1, 0), Triple(199, 1, 93),
        Triple(204, 2, 1), Triple(209, 1, 0), Triple(216, 1, 430),
    )
    val worldC2s = listOf(
        Triple(3, 5, 9), Triple(5, 18, 4), Triple(8, 1, 4), Triple(12, 3, 58), Triple(51, 31, 0),
        Triple(52, 10, 6), Triple(54, 1, 4), Triple(76, 1, 4), Triple(94, 1, 3), Triple(98, 2, 91),
        Triple(105, 1, 1321), Triple(106, 4, 1), Triple(127, 1, 8), Triple(240, 1, 7),
    )
    val handshakeS2c = 1447
    val handshakeC2s = 649

    var seed = 0
    fun body(len: Int): String {
        seed = (seed + 1) % 251
        return b64.encodeToString(ByteArray(len) { ((it * 3 + seed) and 0xFF).toByte() })
    }

    /**
     * Write a session from the world populations. [tamper] can mutate the framed s2c lines and
     * [socketScaleS2c]/[socketDeltaS2c] adjust the s2c prot-stream socket line to model drops/invented
     * frames. Returns the dir.
     */
    fun writeSession(
        name: String,
        realSeed: Boolean = false,
        socketDeltaS2c: Int = 0,
        extraS2c: List<Triple<Int, Int, Int>> = emptyList(),
        tamper: (MutableList<Triple<Int, Int, Int>>) -> Unit = {},
    ): File {
        val dir = File(tmp, name).apply { mkdirs() }
        val s2c = worldS2c.toMutableList().also { it.addAll(extraS2c) }.also(tamper)
        var fpS2c = 0
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, cnt, len) in s2c) repeat(cnt) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":$op,"len":$len,"body":"${body(len)}"}""")
                fpS2c += footprint("s2c", op, len)
            }
        }
        var fpC2s = 0
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            for ((op, cnt, len) in worldC2s) repeat(cnt) {
                w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","op":$op,"len":$len,"body":"${body(len)}"}""")
                fpC2s += footprint("c2s", op, len)
            }
        }
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            val z = b64.encodeToString(ByteArray(4))
            w.appendLine("""{"plane":"socket","dir":"s2c","conn":"game","len":$handshakeS2c,"body":"$z"}""")
            w.appendLine("""{"plane":"socket","dir":"s2c","conn":"game","len":${fpS2c + socketDeltaS2c},"body":"$z"}""")
            w.appendLine("""{"plane":"socket","dir":"c2s","conn":"game","len":$handshakeC2s,"body":"$z"}""")
            w.appendLine("""{"plane":"socket","dir":"c2s","conn":"game","len":$fpC2s,"body":"$z"}""")
        }
        File(dir, "session.json").writeText("""{"session_id":"$name","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText(
            if (realSeed) "ISAAC keys (hex): 0xdeadbeef,0x12345678,0xcafef00d,0x00c0ffee\n"
            else "game-s2c 1 2 3 4\ngame-c2s 5 6 7 8\n"
        )
        return dir
    }

    println("=".repeat(70))
    println("=== Trust verifier self-test ===")
    println("=".repeat(70))

    // 1. Clean full world-entry: byte coverage 100%, sanity matches, no desync.
    run {
        val dir = writeSession("session-clean")
        val r = verify(dir)
        val s2c = r.byteAccounts.first { it.dir == "s2c" }
        val c2s = r.byteAccounts.first { it.dir == "c2s" }
        if (s2c.coveragePct >= 99.999 && c2s.coveragePct >= 99.999) pass("clean: byte coverage 100% on both planes (${s2c.framedPackets}+${c2s.framedPackets} pkts)")
        else fail("clean: coverage s2c=${s2c.coveragePct} c2s=${c2s.coveragePct} (expected 100%)")

        if (r.desyncs.isEmpty()) pass("clean: no framing-coherence desyncs")
        else fail("clean: ${r.desyncs.size} unexpected desync(s): ${r.desyncs.take(3)}")

        val sanityHard = r.sanityDeltas.filter { it.kind == "missing" || it.kind == "unexpected-opcode" }
        if (sanityHard.isEmpty()) pass("clean: sanity population matches documented 948-5 world-entry (conn game→world mapped)")
        else fail("clean: ${sanityHard.size} sanity deviation(s): ${sanityHard.take(5).map { "op${it.opcode}:${it.kind}" }}")

        // s2c packet count must equal claude-re's documented 3790.
        if (s2c.framedPackets == 3790) pass("clean: world s2c packet count = 3790 (matches documented session)")
        else fail("clean: world s2c packet count ${s2c.framedPackets} (expected 3790)")
    }

    // 2. Dropped packets: 8000B of wire produced no framed packet -> coverage falls -> FAIL.
    run {
        val dir = writeSession("session-dropped", socketDeltaS2c = 8000)
        val r = verify(dir)
        val s2c = r.byteAccounts.first { it.dir == "s2c" }
        if (r.verdict == SessionTrustVerifier.Verdict.FAIL && s2c.coveragePct < SessionTrustVerifier.COVERAGE_FAIL_PCT)
            pass("dropped: coverage ${"%.1f".format(s2c.coveragePct)}% -> FAIL (8000B unaccounted beyond handshake cap)")
        else fail("dropped: verdict=${r.verdict} coverage=${s2c.coveragePct} (expected FAIL & <${SessionTrustVerifier.COVERAGE_FAIL_PCT}%)")
    }

    // 3. Framing desync: a fixed-3 VarpSmall captured as len 9 -> WARN with a byte offset.
    //    A size-class disagreement is a coherence signal worth surfacing, but NOT a hard FAIL: the
    //    codec size can be authoritative against the binary prot table and the disagreement a dylib
    //    LAST_OPCODE mis-resolution. It must still be recorded (with its stream offset) and must
    //    escalate the verdict to at least WARN.
    run {
        val dir = writeSession("session-desync") { pop ->
            // Replace op61's run with one mis-sized packet + the rest correct so a desync is forced.
            val idx = pop.indexOfFirst { it.first == 61 }
            pop[idx] = Triple(61, 1175, 3)
            pop.add(idx + 1, Triple(61, 1, 9)) // the bad one
        }
        val r = verify(dir)
        val d = r.desyncs.firstOrNull { it.opcode == 61 }
        if (r.verdict != SessionTrustVerifier.Verdict.PASS && d != null && d.streamOffset > 0)
            pass("desync: op61 fixed/len mismatch caught at stream offset ${d.streamOffset}B -> WARN (not a hard FAIL)")
        else fail("desync: verdict=${r.verdict} desyncs=${r.desyncs.size} (expected >=WARN with an op61 desync)")
    }

    // 4. Invented / double-counted frames: a framing hook emitting each packet twice leaves a run of
    //    adjacent byte-identical frames — the POSITIVE double-count signature. That is direct evidence
    //    of invention (unlike a byte-ratio guess) and is a hard FAIL.
    run {
        val dir = File(tmp, "session-invented").apply { mkdirs() }
        // A clean prefix, then the SAME op28 frame (id+value identical body) repeated many times in a
        // row — exactly what a double-emitting hook produces.
        val dupBody = b64.encodeToString(ByteArray(6) { (it + 1).toByte() })
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","state":30,"op":61,"len":3,"body":"${body(3)}"}""")
            repeat(20) { // 20 identical adjacent op28 frames -> a clear duplicate run
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","state":30,"op":28,"len":6,"body":"$dupBody"}""")
            }
        }
        File(dir, "framed-c2s.jsonl").writeText("")
        File(dir, "session.json").writeText("""{"session_id":"invented","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        val r = verify(dir)
        val dc = r.doubleCounts.firstOrNull { it.worstOpcode == 28 }
        val failsInvented = r.reasons.any { it.contains("double-counted frames", ignoreCase = true) }
        if (r.verdict == SessionTrustVerifier.Verdict.FAIL && dc != null && dc.duplicateFraction > 0.5 && dc.worstRun >= 20 && failsInvented)
            pass("invented: ${"%.0f".format(dc.duplicateFraction * 100)}% of the plane is adjacent byte-identical op28 duplicates -> FAIL (frames invented / double-counted)")
        else fail("invented: verdict=${r.verdict} doubleCounts=${r.doubleCounts} failsInvented=$failsInvented (expected FAIL with a high duplicate fraction)")
    }

    // 4b. VERDICT TWEAK — MODEST framed>socket overage (the production login/s2c ~140% reality): the
    //     socket plane is present but UNDER-CAPTURED (recorded fewer raw bytes than the client framed,
    //     because the lobby s2c is read by an inline ring-drain the raw Fill plane under-samples). This
    //     is a socket under-capture, NOT invented frames -> WARN "socket under-capture (completeness
    //     unproven)", never a FAIL. Mirrors the framed 18 KB / socket 13 KB production session.
    run {
        // -12000 puts game/s2c socket between 0.5x and 1.0x of framed — a modest overage that is NOT
        // the tiny-socket "uncaptured" case and must read as a socket under-capture WARN.
        val dir = writeSession("session-undercapture", socketDeltaS2c = -12000)
        val r = verify(dir)
        val s2c = r.byteAccounts.first { it.dir == "s2c" }
        val ratio = s2c.framedFootprint.toDouble() / s2c.socketTotal.toDouble()
        val modest = ratio > 1.0 && !s2c.socketUncaptured
        val warnsUnderCapture = r.reasons.any { it.contains("socket under-capture", ignoreCase = true) }
        val noInvent = r.reasons.none { it.contains("double-counted", ignoreCase = true) || it.contains("frames invented", ignoreCase = true) }
        if (modest && warnsUnderCapture && noInvent && r.verdict != SessionTrustVerifier.Verdict.FAIL)
            pass("under-capture: framed ${s2c.framedFootprint}B vs socket ${s2c.socketTotal}B (${"%.0f".format(ratio * 100)}%) -> WARN 'socket under-capture', NOT a 'frames invented' FAIL")
        else fail("under-capture: verdict=${r.verdict} ratio=${"%.2f".format(ratio)} modest=$modest warnsUnderCapture=$warnsUnderCapture noInvent=$noInvent")
    }

    // 5. Fingerprint-only ISAAC: cross-validation must report "no real seed", never a bogus desync.
    run {
        val dir = writeSession("session-fingerprint", realSeed = false)
        val line = cross.independentDeframeSummary(dir)
        if (line.contains("no real seed", ignoreCase = true) && line.contains("STATE-word", ignoreCase = true))
            pass("fingerprint (structural): cross-validation reports 'no real seed captured' (no bogus re-deframe)")
        else fail("fingerprint (structural): cross-validation line was: $line")
    }

    // 5b. LABELED fingerprint (the real mac dylib form): value lines say `hex (fingerprint, NOT a
    //     seed): 0x..`. The word "hex" must NOT trip the seed path — the disclaimer marker wins, the
    //     deframe is skipped, and NO bogus mismatch is produced. This is the exact production bug.
    run {
        val dir = File(tmp, "session-fingerprint-labeled").apply { mkdirs() }
        writeSession("session-fingerprint-labeled") // seed the framed/socket/session files
        File(dir, "isaac-keys.txt").writeText(
            """
            # ISAAC randrsl FINGERPRINT (NOT a construction seed).
            game-s2c hex (fingerprint, NOT a seed): 0x7f2a4e9a,0xe5ac634a,0x5779881b,0xb43fb422
            game-c2s hex (fingerprint, NOT a seed): 0x6f81b722,0x4b3e60a8,0x2e80fe9f,0xd902d9a5
            """.trimIndent() + "\n"
        )
        val line = cross.independentDeframeSummary(dir)
        // The fix works iff the deframe is reported unavailable ("no real seed") and the old
        // real-seed PATH was NOT taken (its header "deframe of the socket ciphertext (real seed
        // captured)" and any "MISMATCH" rows are absent). Note "no real seed captured" legitimately
        // contains the substring "real seed captured", so match the real-seed HEADER specifically.
        val ok = line.contains("no real seed", ignoreCase = true) &&
            !line.contains("MISMATCH", ignoreCase = true) &&
            !line.contains("deframe of the socket ciphertext (real seed captured)", ignoreCase = true)
        if (ok) pass("fingerprint (labeled): 'hex (fingerprint, NOT a seed)' lines skip the deframe — no bogus mismatch")
        else fail("fingerprint (labeled): cross-validation line was: $line")
    }

    // 6. Unexpected opcode flood -> sanity flags it (NOT a desync, since the opcode is unmodeled).
    run {
        val dir = writeSession("session-unexpected", extraS2c = listOf(Triple(217, 60, 0)))
        val r = verify(dir)
        val flagged = r.sanityDeltas.any { it.opcode == 217 && it.kind == "unexpected-opcode" }
        if (flagged) pass("unexpected: op217 flood flagged by sanity, not mislabeled as a desync")
        else fail("unexpected: op217 not flagged; sanity=${r.sanityDeltas.map { it.opcode to it.kind }}")
    }

    // 7. PRODUCTION case: the socket plane barely captured the s2c ciphertext (a tiny fraction of the
    //    framed footprint) while a `conn=unknown` cache download dominated the plane. Byte-accounting
    //    must report UNAVAILABLE (WARN), NOT a "frames invented" FAIL, and the cache volume must be
    //    surfaced as INFO and excluded from accounting. With clean-decode green this is overall WARN.
    run {
        val dir = File(tmp, "session-uncaptured").apply { mkdirs() }
        var fpS2c = 0
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, cnt, len) in worldS2c) repeat(cnt) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":$op,"len":$len,"body":"${body(len)}"}""")
                fpS2c += footprint("s2c", op, len)
            }
        }
        File(dir, "framed-c2s.jsonl").writeText("") // c2s framing broken in this capture (all empty)
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            val z = b64.encodeToString(ByteArray(4))
            // The real game s2c socket: a tiny 1402B vs ~42 KB framed -> uncaptured.
            w.appendLine("""{"plane":"socket","dir":"s2c","conn":"game","len":1402,"body":"$z"}""")
            // The cache-asset download tagged conn=unknown dominates the plane (different protocol).
            repeat(20) { w.appendLine("""{"plane":"socket","dir":"c2s","conn":"unknown","len":26000,"body":"$z"}""") }
        }
        File(dir, "session.json").writeText("""{"session_id":"uncaptured","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText("game-s2c hex (fingerprint, NOT a seed): 0x1,0x2,0x3,0x4\n")

        val r = verify(dir)
        val s2c = r.byteAccounts.first { it.conn == "game" && it.dir == "s2c" }
        val noInvent = r.reasons.none { it.contains("frames invented", ignoreCase = true) }
        val warnsUnavailable = r.reasons.any { it.contains("byte-accounting unavailable", ignoreCase = true) }
        val cacheExcluded = r.nonGameSocket.any { it.conn == "unknown" } &&
            r.byteAccounts.none { it.conn == "unknown" }
        if (s2c.socketUncaptured && noInvent && warnsUnavailable && r.verdict != SessionTrustVerifier.Verdict.FAIL && cacheExcluded)
            pass("uncaptured: tiny s2c socket -> byte-accounting UNAVAILABLE (WARN, not 'frames invented'); cache conn=unknown reported as INFO, excluded")
        else fail("uncaptured: verdict=${r.verdict} socketUncaptured=${s2c.socketUncaptured} noInvent=$noInvent warnsUnavailable=$warnsUnavailable cacheExcluded=$cacheExcluded")
    }

    // 7c. LOGIN-HANDSHAKE op16/op19: the connection-type + RSA login blocks (op16=world ~664B,
    //     op19=lobby ~644B) traverse the same outgoing queue the recorder walks, so they surface on
    //     the framed c2s plane with a login opcode and a multi-hundred-byte RSA body, while in the
    //     LOGIN phase (state < LOGGED_IN). They are NOT prot-dispatched in-game ClientProts (op16 is
    //     in-game fixed-6 SendIfButtonN_CS2, op19 in-game fixed-9) and must NOT trip the
    //     framing-coherence size check. An in-game op16 with the WRONG size at LOGGED_IN still must.
    run {
        val dir = File(tmp, "session-login-handshake").apply { mkdirs() }
        // Reuse the clean world s2c population so byte-accounting/decode stay green; the focus is c2s.
        var fpS2c = 0
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, cnt, len) in worldS2c) repeat(cnt) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","state":30,"op":$op,"len":$len,"body":"${body(len)}"}""")
                fpS2c += footprint("s2c", op, len)
            }
        }
        // c2s: the two RSA login blocks in the LOGIN phase (state 20 = LOBBY_SCREEN), then a normal
        // in-game keepalive at LOGGED_IN. op16/op19 here carry RSA-block bodies that grossly violate
        // their in-game fixed sizes (6 / 9) — the exact production shape.
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":20,"op":19,"len":644,"body":"${body(644)}"}""")
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":20,"op":16,"len":664,"body":"${body(664)}"}""")
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":30,"op":51,"len":0,"body":"${body(0)}"}""")
        }
        // No socket.jsonl -> byte-accounting is "unavailable" (WARN), which keeps the focus on the
        // desync check. Sanity has no c2s baseline contribution that matters here.
        File(dir, "session.json").writeText("""{"session_id":"login-handshake","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")

        val r = verifier.verify(dir, "skipped", clientCrossChecker.crossCheck(dir))
        val loginDesync = r.desyncs.any { it.dir == "c2s" && (it.opcode == 16 || it.opcode == 19) }
        if (!loginDesync) pass("login-handshake: op16/op19 RSA login blocks (state<LOGGED_IN) do NOT trip a framing-coherence desync")
        else fail("login-handshake: spurious desync(s) for login op16/op19: ${r.desyncs.filter { it.opcode == 16 || it.opcode == 19 }}")
    }

    // 7d. IN-GAME op16 at LOGGED_IN with the wrong size MUST still be caught — the login-phase guard
    //     is surgically scoped to the handshake phase and must not blind the in-game prot check. This
    //     requires op16 to be a MODELED name; it is UNKNOWN_16 in the codec, so we assert against the
    //     modeled op51 keepalive instead (fixed-0) given a non-zero body at LOGGED_IN.
    run {
        val dir = File(tmp, "session-ingame-mis-size").apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").writeText("")
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            // op51 NO_TIMEOUT is fixed-0 and MODELED; a non-zero body at LOGGED_IN is a real desync.
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":30,"op":51,"len":7,"body":"${body(7)}"}""")
        }
        File(dir, "session.json").writeText("""{"session_id":"ingame-mis-size","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        val r = verifier.verify(dir, "skipped", clientCrossChecker.crossCheck(dir))
        val caught = r.desyncs.any { it.dir == "c2s" && it.opcode == 51 }
        if (caught) pass("in-game mis-size: a modeled op51 with the wrong fixed length at LOGGED_IN is still caught (guard is login-phase only)")
        else fail("in-game mis-size: op51 desync NOT caught — the login-phase guard over-reached: ${r.desyncs}")
    }

    // 7e. ROBUSTNESS — the login-phase guard must work INDEPENDENTLY of the UNKNOWN_* gate, so that
    //     promoting op16 to a confident official name never resurfaces the false desync. Force op16 to
    //     a MODELED fixed-6 name in a throwaway codec and confirm: (a) a login-phase op16=664 (state
    //     20) is STILL not flagged (the new guard alone suppresses it), and (b) the SAME modeled op16
    //     with a wrong body at LOGGED_IN (state 30) IS flagged (the guard didn't blind the in-game
    //     check). This is the regression that the accidental UNKNOWN_* gate would have hidden.
    run {
        val modeledCodec = register948()
        // Force op16 to look like a confident in-game prot (fixed 6) — the future "promoted" state.
        modeledCodec.clientProtInfo[16] =
            Codec.ProtInfo("IF_BUTTON_PROMOTED_16", ProtSize.Fixed(6))
        val modeledVerifier = SessionTrustVerifier(modeledCodec, KnownOpcodes.empty())

        val dir = File(tmp, "session-login-modeled").apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").writeText("")
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            // (a) login-phase RSA block at op16 (state 20) — must NOT desync despite being modeled.
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":20,"op":16,"len":664,"body":"${body(664)}"}""")
            // (b) in-game op16 at LOGGED_IN with the wrong length (3 != fixed 6) — MUST desync.
            w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","state":30,"op":16,"len":3,"body":"${body(3)}"}""")
        }
        File(dir, "session.json").writeText("""{"session_id":"login-modeled","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")

        val r = modeledVerifier.verify(dir, "skipped", clientCrossChecker.crossCheck(dir))
        val loginPhaseFlagged = r.desyncs.any { it.opcode == 16 && it.detail.contains("664") }
        val ingameFlagged = r.desyncs.any { it.opcode == 16 && it.detail.contains("captured len=3") }
        if (!loginPhaseFlagged && ingameFlagged)
            pass("robustness: with op16 MODELED, the login-phase RSA block is still excluded while the in-game wrong-size op16 IS caught (guard independent of UNKNOWN_*)")
        else fail("robustness: loginPhaseFlagged=$loginPhaseFlagged ingameFlagged=$ingameFlagged desyncs=${r.desyncs.map { it.opcode to it.detail }}")
    }

    // ---- independent-deframe correctness (the third proof: socket → client framing) -------------
    //
    // These synthesize a session whose SOCKET plane is the real on-wire bytes for a known opcode
    // sequence, so the CrossValidator must independently reproduce that sequence from the socket and
    // report AGREE — proving the CaptureDeframer + ISAAC reconstruction is byte-correct — and must
    // honestly report failure (never a false AGREE) when the seed does not match the cipher.

    val realSeed = intArrayOf(0x8c61aa2c.toInt(), 0x7fe0a26d, 0xe2a81f6e.toInt(), 0x69c35fa2)
    // A small, codec-consistent game opcode population (op, len, body) — fixed/varByte/varShort mix.
    // The SAME body bytes go into both framed-*.jsonl and the synthesized socket wire so the
    // CrossValidator's body-match mapping lines up (as it does against a real capture).
    fun smallPop(dir: String): List<Triple<Int, Int, ByteArray>> {
        fun sc(op: Int) = if (dir == "s2c") codec.serverProtSize(op) else codec.clientProtSize(op)
        // pick opcodes whose size class we KNOW so the synthesized prefix matches the codec, and give
        // each a non-empty UNIQUE body so body-match mapping is unambiguous.
        return listOf(52, 8, 76, 94, 127, 52, 76, 8).filter { sc(it) > 0 }.mapIndexed { i, op ->
            val len = sc(op)
            Triple(op, len, ByteArray(len) { ((it * 31 + op * 7 + i * 101) and 0xFF).toByte() })
        }
    }

    /** Build the on-wire bytes for [pop] in [dir]: opcode(1/2) + prefix(0/1/2) + body. */
    fun buildWire(dir: String, pop: List<Triple<Int, Int, ByteArray>>, encipher: ((dir: String) -> Isaac)?): ByteArray {
        val out = ByteArrayOutputStream()
        val isaac = encipher?.invoke(dir)
        for ((op, _, bodyBytes) in pop) {
            val opByte0 = if (op < 128) op else 0x80 or (op ushr 8)
            val key0 = isaac?.nextInt() ?: 0
            out.write((opByte0 + key0) and 0xFF)
            if (op >= 128) {
                val key1 = isaac?.nextInt() ?: 0
                out.write(((op and 0xFF) + key1) and 0xFF)
            }
            when (val s = if (dir == "s2c") codec.serverProtSize(op) else codec.clientProtSize(op)) {
                -1 -> out.write(bodyBytes.size and 0xFF)
                -2 -> { out.write((bodyBytes.size ushr 8) and 0xFF); out.write(bodyBytes.size and 0xFF) }
                else -> require(s == bodyBytes.size) { "fixed op$op size $s != len ${bodyBytes.size}" }
            }
            out.write(bodyBytes)
        }
        return out.toByteArray()
    }

    fun isaacFor(seed: IntArray): (String) -> Isaac = { dir ->
        Isaac(if (dir == "s2c") IntArray(seed.size) { seed[it] + CaptureDeframer.S2C_ISAAC_DELTA } else seed.copyOf())
    }

    /** Write a session whose socket plane = [wire] bytes for the small population, with [seedLine]. */
    fun writeDeframeSession(
        name: String,
        seedLine: String,
        wire: (dir: String) -> ByteArray,
    ): File {
        val dir = File(tmp, name).apply { mkdirs() }
        for (d in listOf("s2c", "c2s")) {
            File(dir, "framed-$d.jsonl").bufferedWriter().use { w ->
                for ((op, len, bodyBytes) in smallPop(d)) {
                    w.appendLine("""{"plane":"framed","dir":"$d","conn":"game","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
                }
            }
        }
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            for (d in listOf("s2c", "c2s")) {
                val bytes = wire(d)
                w.appendLine("""{"plane":"socket","dir":"$d","conn":"game","len":${bytes.size},"body":"${b64.encodeToString(bytes)}"}""")
            }
        }
        File(dir, "session.json").writeText("""{"session_id":"$name","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText(seedLine)
        return dir
    }

    // 7b. ISAAC key-schedule vectors (the cipher reproduces jag::Isaac::Init exactly). These are the
    //     RE reference vectors; if the core Isaac ever drifts from the client, this fails FIRST and
    //     points at the cipher rather than the deframe. nextInt() returns the keystream ints; the
    //     opcode transform takes only the low byte.
    run {
        val zero = Isaac(IntArray(4)).nextInt()
        val worked = Isaac(intArrayOf(0x11111111, 0x22222222, 0x33333333, 0x44444444))
        val w = IntArray(5) { worked.nextInt() }
        val expect = intArrayOf(0x3fd79f8c, 0xf40f7869.toInt(), 0x4a485071, 0x5c305e4c, 0xe7e2593e.toInt())
        if (zero == 0x182600f3 && w.contentEquals(expect))
            pass("isaac-vectors: core Isaac matches jag::Isaac::Init ({0,0,0,0}->0x182600f3; worked-example first 5 outputs)")
        else fail("isaac-vectors: zero=${zero.toUInt().toString(16)} worked=${w.map { it.toUInt().toString(16) }}")
    }

    // 8. Real seed + ISAAC ciphertext: socket opcodes are ISAAC-enciphered with realSeed -> AGREE.
    run {
        val dir = writeDeframeSession(
            "session-isaac-agree",
            "ISAAC keys (hex): 0x8c61aa2c,0x7fe0a26d,0xe2a81f6e,0x69c35fa2\n",
            wire = { d -> buildWire(d, smallPop(d), isaacFor(realSeed)) },
        )
        val line = cross.independentDeframeSummary(dir)
        val s2cOk = line.contains("[game/s2c] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        val c2sOk = line.contains("[game/c2s] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        if (s2cOk && c2sOk) pass("isaac-agree: independent ISAAC deframe reproduces the client framing on BOTH planes (the third proof)")
        else fail("isaac-agree: cross line was:\n$line")
    }

    // 9. Real seed but PLAINTEXT socket: opcodes verbatim -> AGREE via identity mode (the production
    //    macOS reality: ClientStream::Write taps c2s before opcode encryption).
    run {
        val dir = writeDeframeSession(
            "session-plaintext-agree",
            "ISAAC keys (hex): 0x8c61aa2c,0x7fe0a26d,0xe2a81f6e,0x69c35fa2\n",
            wire = { d -> buildWire(d, smallPop(d), encipher = null) },
        )
        val line = cross.independentDeframeSummary(dir)
        val ok = line.contains("[game/c2s] OK") && line.contains("mode=identity", ignoreCase = true)
        if (ok) pass("plaintext-agree: a PLAINTEXT socket is detected and reproduces the client framing (mode=identity), not a bogus desync")
        else fail("plaintext-agree: cross line was:\n$line")
    }

    // 10. WRONG seed: ciphertext enciphered with realSeed, but isaac-keys.txt carries a DIFFERENT
    //     seed -> the deframe must NOT manufacture an AGREE, and must diagnose the seed mismatch
    //     (the exact session-…-4358 production situation: only the first Isaac::Init seed captured).
    run {
        val dir = writeDeframeSession(
            "session-wrong-seed",
            "ISAAC keys (hex): 0x11111111,0x22222222,0x33333333,0x44444444\n",
            wire = { d -> buildWire(d, smallPop(d), isaacFor(realSeed)) },
        )
        val line = cross.independentDeframeSummary(dir)
        val noFalseAgree = !line.contains("OK —", ignoreCase = false)
        val diagnosed = line.contains("captured seed is not the cipher seed", ignoreCase = true)
        if (noFalseAgree && diagnosed) pass("wrong-seed: no false AGREE; diagnoses the seed mismatch honestly (blames the seed, not the verified cipher)")
        else fail("wrong-seed: cross line was:\n$line")
    }

    // 11. PER-CONNECTION seeds (the recorder's new multi-line `isaac-keys.txt`): the GAME and LOGIN
    //     connections each build their OWN ISAAC pair from DIFFERENT session keys. The file carries one
    //     line per (conn, dir); a `-s2c` line is the matching `-c2s` SEND seed + 50 per int (exactly
    //     what the recv cipher was seeded with → used VERBATIM, no second +50). The socket's game wire
    //     is enciphered with the GAME seed and its login wire with the LOGIN seed. Each plane must be
    //     validated with ITS OWN seed → all four planes AGREE. This is the regression the old
    //     single-seed contract failed (game validated against the login seed → bogus desync).
    run {
        val gameSend = intArrayOf(0x8c61aa2c.toInt(), 0x7fe0a26d, 0xe2a81f6e.toInt(), 0x69c35fa2)
        val loginSend = intArrayOf(0x13572468, 0x0bad0d0e, 0x55aa55aa, 0x0f1e2d3c)
        // recv (s2c) seed = send seed + 50 per int, stored VERBATIM (what Isaac::Init was called with).
        fun plus50(s: IntArray) = IntArray(s.size) { s[it] + CaptureDeframer.S2C_ISAAC_DELTA }
        fun hex(s: IntArray) = s.joinToString(",") { "0x%08x".format(it) }

        val dir = File(tmp, "session-per-conn-seeds").apply { mkdirs() }
        // framed + socket for BOTH conns (game, login) × BOTH dirs, with each plane's wire enciphered
        // by that connection's own seed (isaacFor applies the +50 for s2c, matching the verbatim
        // `-s2c` line, so the wire and the deframer's verbatim ISAAC agree).
        val conns = listOf("game" to gameSend, "login" to loginSend)
        for (d in listOf("s2c", "c2s")) {
            File(dir, "framed-$d.jsonl").bufferedWriter().use { w ->
                for ((conn, _) in conns) for ((op, len, bodyBytes) in smallPop(d)) {
                    w.appendLine("""{"plane":"framed","dir":"$d","conn":"$conn","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
                }
            }
        }
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            for ((conn, send) in conns) for (d in listOf("s2c", "c2s")) {
                val bytes = buildWire(d, smallPop(d), isaacFor(send))
                w.appendLine("""{"plane":"socket","dir":"$d","conn":"$conn","len":${bytes.size},"body":"${b64.encodeToString(bytes)}"}""")
            }
        }
        File(dir, "session.json").writeText("""{"session_id":"per-conn-seeds","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText(
            "# Real ISAAC construction seeds — one line per connection and direction.\n" +
                "# A -c2s line holds the raw SEND keys; a -s2c line holds the RECV keys (send + 50 per int).\n" +
                "game-c2s ISAAC keys (hex): ${hex(gameSend)}\n" +
                "game-s2c ISAAC keys (hex): ${hex(plus50(gameSend))}\n" +
                "login-c2s ISAAC keys (hex): ${hex(loginSend)}\n" +
                "login-s2c ISAAC keys (hex): ${hex(plus50(loginSend))}\n"
        )

        val line = cross.independentDeframeSummary(dir)
        // Every plane must AGREE, each via its OWN per-(conn,dir) verbatim seed.
        val gameS2c = line.contains("[game/s2c] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        val gameC2s = line.contains("[game/c2s] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        val loginS2c = line.contains("[login/s2c] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        val loginC2s = line.contains("[login/c2s] OK") && line.contains("mode=ISAAC", ignoreCase = true)
        val verbatim = line.contains("verbatim", ignoreCase = true) // proves the per-plane seed path
        val noDesync = !line.contains("could NOT reproduce", ignoreCase = true)
        if (gameS2c && gameC2s && loginS2c && loginC2s && verbatim && noDesync)
            pass("per-conn-seeds: each (conn,dir) plane validated with ITS OWN seed — game s2c+c2s AND login s2c+c2s all AGREE (verbatim per-plane seeds)")
        else fail("per-conn-seeds: gameS2c=$gameS2c gameC2s=$gameC2s loginS2c=$loginS2c loginC2s=$loginC2s verbatim=$verbatim noDesync=$noDesync\n$line")
    }

    // 11b. PER-CONNECTION seeds, NEGATIVE control: with the game and login `-c2s`/`-s2c` lines SWAPPED
    //      (game wire enciphered with the game seed, but the file labels the game seed as login and vice
    //      versa), each plane is now validated against the WRONG connection's seed and must NOT produce a
    //      false AGREE — it must hit the honest seed-mismatch diagnostic. This proves the per-plane
    //      lookup actually selects by (conn,dir) rather than accidentally trying every seed.
    run {
        val gameSend = intArrayOf(0x8c61aa2c.toInt(), 0x7fe0a26d, 0xe2a81f6e.toInt(), 0x69c35fa2)
        val loginSend = intArrayOf(0x13572468, 0x0bad0d0e, 0x55aa55aa, 0x0f1e2d3c)
        fun plus50(s: IntArray) = IntArray(s.size) { s[it] + CaptureDeframer.S2C_ISAAC_DELTA }
        fun hex(s: IntArray) = s.joinToString(",") { "0x%08x".format(it) }

        val dir = File(tmp, "session-per-conn-swapped").apply { mkdirs() }
        val conns = listOf("game" to gameSend, "login" to loginSend)
        for (d in listOf("s2c", "c2s")) {
            File(dir, "framed-$d.jsonl").bufferedWriter().use { w ->
                for ((conn, _) in conns) for ((op, len, bodyBytes) in smallPop(d)) {
                    w.appendLine("""{"plane":"framed","dir":"$d","conn":"$conn","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
                }
            }
        }
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            for ((conn, send) in conns) for (d in listOf("s2c", "c2s")) {
                val bytes = buildWire(d, smallPop(d), isaacFor(send))
                w.appendLine("""{"plane":"socket","dir":"$d","conn":"$conn","len":${bytes.size},"body":"${b64.encodeToString(bytes)}"}""")
            }
        }
        File(dir, "session.json").writeText("""{"session_id":"per-conn-swapped","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        // SWAP: the game wire is enciphered with gameSend, but the `game-*` lines carry loginSend.
        File(dir, "isaac-keys.txt").writeText(
            "game-c2s ISAAC keys (hex): ${hex(loginSend)}\n" +
                "game-s2c ISAAC keys (hex): ${hex(plus50(loginSend))}\n" +
                "login-c2s ISAAC keys (hex): ${hex(gameSend)}\n" +
                "login-s2c ISAAC keys (hex): ${hex(plus50(gameSend))}\n"
        )

        val line = cross.independentDeframeSummary(dir)
        val noFalseAgree = !line.contains("OK —", ignoreCase = false)
        val diagnosed = line.contains("captured seed is not the cipher seed", ignoreCase = true)
        if (noFalseAgree && diagnosed)
            pass("per-conn-swapped: mismatched per-(conn,dir) seeds produce NO false AGREE and the honest seed-mismatch diagnostic (lookup selects by conn,dir)")
        else fail("per-conn-swapped: noFalseAgree=$noFalseAgree diagnosed=$diagnosed\n$line")
    }

    // ---- CLIENT-IS-KING cross-check (the headline: decoded state vs the client's own snapshot) ----
    //
    // These synthesize state-bearing s2c packets (VARP_SMALL/LARGE, UPDATE_STAT, UPDATE_RUNENERGY)
    // with KNOWN values plus a `state-snapshots.jsonl` and assert: an AGREE when the snapshot matches
    // the decoded values, and a FAIL when a varp deliberately disagrees (the client is authoritative)
    // or when the client holds a varp we never sent (a dropped state packet).

    // Wire encoders — must produce bodies the core ClientStateCrossCheck decoder reads back exactly.
    fun varpSmallBody(id: Int, value: Int): ByteArray =
        byteArrayOf((id ushr 8).toByte(), id.toByte(), ((-128 - value) and 0xFF).toByte())          // id g2 BE, value byteSubtract
    fun varpLargeBody(id: Int, value: Int): ByteArray = byteArrayOf(                                  // value g4 BE, id g2 BE
        (value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte(),
        (id ushr 8).toByte(), id.toByte(),
    )
    fun varpLongBody(id: Int, value: Long): ByteArray {                                               // id shortAdd, high/low g4_alt3
        fun g4Alt3(v: Int) = byteArrayOf(
            (v ushr 16).toByte(), (v ushr 24).toByte(), v.toByte(), (v ushr 8).toByte(),
        )
        val high = (value ushr 32).toInt()
        val low = value.toInt()
        return byteArrayOf((id ushr 8).toByte(), ((id - 128) and 0xFF).toByte()) + g4Alt3(high) + g4Alt3(low)
    }
    fun updateStatBody(skillId: Int, level: Int, xp: Int): ByteArray = byteArrayOf(                   // xp g4 LE, level g1, skillId byteInverse
        xp.toByte(), (xp ushr 8).toByte(), (xp ushr 16).toByte(), (xp ushr 24).toByte(),
        level.toByte(), ((-skillId) and 0xFF).toByte(),
    )
    fun runEnergyBody(energy: Int): ByteArray = byteArrayOf(energy.toByte())                          // g1
    fun varcSmallBody(id: Int, value: Int): ByteArray = byteArrayOf(                                  // value byteAdd, id u16 LE
        ((value + 128) and 0xFF).toByte(), id.toByte(), (id ushr 8).toByte(),
    )
    fun varcLargeBody(id: Int, value: Int): ByteArray = byteArrayOf(                                  // value intMiddle, id shortAddLittle
        (value ushr 8).toByte(), value.toByte(), (value ushr 24).toByte(), (value ushr 16).toByte(),
        ((id + 128) and 0xFF).toByte(), (id ushr 8).toByte(),
    )
    fun varcLongBody(id: Int, value: Long): ByteArray = byteArrayOf(                                  // value g8 BE, id g2 BE
        (value ushr 56).toByte(), (value ushr 48).toByte(), (value ushr 40).toByte(), (value ushr 32).toByte(),
        (value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte(),
        (id ushr 8).toByte(), id.toByte(),
    )
    fun rsString(value: String): ByteArray = value.encodeToByteArray() + byteArrayOf(0)
    fun varcStrBody(id: Int, value: String): ByteArray = byteArrayOf(id.toByte(), (id ushr 8).toByte()) + rsString(value)
    fun varcStrLargeBody(id: Int, value: String): ByteArray = rsString(value) +
        byteArrayOf((id ushr 8).toByte(), ((id + 128) and 0xFF).toByte())
    fun varcBitSmallBody(id: Int, value: Int): ByteArray = byteArrayOf(                               // value raw, id shortAdd
        value.toByte(), (id ushr 8).toByte(), ((id + 128) and 0xFF).toByte(),
    )
    fun varcBitLargeBody(id: Int, value: Int): ByteArray = byteArrayOf(                               // id BE, value BE
        (id ushr 8).toByte(), id.toByte(),
        (value ushr 24).toByte(), (value ushr 16).toByte(), (value ushr 8).toByte(), value.toByte(),
    )
    data class InvEntry(val slot: Int, val itemId: Int, val count: Long)
    fun ByteArrayOutputStream.u8(value: Int) { write(value and 0xFF) }
    fun ByteArrayOutputStream.u16(value: Int) {
        u8(value ushr 8)
        u8(value)
    }
    fun ByteArrayOutputStream.u32(value: Long) {
        u8((value ushr 24).toInt())
        u8((value ushr 16).toInt())
        u8((value ushr 8).toInt())
        u8(value.toInt())
    }
    fun ByteArrayOutputStream.smart(value: Int) {
        if (value >= 128) u16(value + 32768) else u8(value)
    }
    fun ByteArrayOutputStream.quantity(value: Long) {
        if (value < 0xFF) {
            u8(value.toInt())
        } else {
            u8(0xFF)
            u32(value)
        }
    }
    fun invFullBody(invId: Int, flags: Int = 0, entries: List<InvEntry> = emptyList()): ByteArray {
        val bySlot = entries.associateBy { it.slot }
        val count = (entries.maxOfOrNull { it.slot } ?: -1) + 1
        return ByteArrayOutputStream().apply {
            u16(invId)
            u8(flags)
            u16(count)
            repeat(count) { slot ->
                val entry = bySlot[slot]
                u16((entry?.itemId ?: -1) + 1)
                quantity(entry?.count ?: 0L)
                if (flags and 0x2 != 0) u8(0)
            }
        }.toByteArray()
    }
    fun invPartialBody(invId: Int, flags: Int = 0, entries: List<InvEntry>): ByteArray =
        ByteArrayOutputStream().apply {
            u16(invId)
            u8(flags)
            for (entry in entries) {
                smart(entry.slot)
                u16(entry.itemId + 1)
                if (entry.itemId == -1) continue
                quantity(entry.count)
                if (flags and 0x2 != 0) u8(0)
            }
        }.toByteArray()
    data class NpcAdd(val idx: Int, val typeId: Int, val x: Int, val y: Int, val plane: Int)
    data class PlayerAdd(val idx: Int, val x: Int, val y: Int, val plane: Int = 0)
    class BitWriter {
        private val bytes = ArrayList<Int>()
        private var bit = 0

        fun writeBits(count: Int, value: Int) {
            repeat(count) { i ->
                val srcBit = (value ushr (count - 1 - i)) and 1
                val byteIndex = bit ushr 3
                while (bytes.size <= byteIndex) bytes += 0
                if (srcBit != 0) bytes[byteIndex] = bytes[byteIndex] or (1 shl (7 - (bit and 7)))
                bit++
            }
        }

        fun alignToByte() {
            val used = bit and 7
            if (used != 0) writeBits(8 - used, 0)
        }

        fun toByteArray(): ByteArray = bytes.map { it.toByte() }.toByteArray()
    }
    fun writePlayerSkipRun(out: BitWriter, count: Int) {
        var remaining = count
        while (remaining > 0) {
            val following = minOf(remaining - 1, 2047)
            out.writeBits(1, 0)
            when {
                following == 0 -> out.writeBits(2, 0)
                following < 32 -> {
                    out.writeBits(2, 1)
                    out.writeBits(5, following)
                }
                following < 256 -> {
                    out.writeBits(2, 2)
                    out.writeBits(8, following)
                }
                else -> {
                    out.writeBits(2, 3)
                    out.writeBits(11, following)
                }
            }
            remaining -= following + 1
        }
    }
    fun playerRegionWord(tileX: Int, tileY: Int, plane: Int = 0, active: Boolean = true): Int {
        val activeBits = if (active) 0 else 1
        return (activeBits shl 18) or ((plane and 0x3) shl 16) or ((tileX ushr 6) shl 8) or (tileY ushr 6)
    }
    fun gpiPrefixBody(localIndex: Int, localX: Int, localY: Int, localPlane: Int, lowWords: Map<Int, Int>): ByteArray {
        val out = BitWriter()
        out.writeBits(30, ((localPlane and 0x3) shl 28) or (localX shl 14) or localY)
        val defaultWord = playerRegionWord(localX, localY)
        for (slot in 1 until 2048) {
            if (slot == localIndex) continue
            out.writeBits(20, lowWords[slot] ?: defaultWord)
        }
        return out.toByteArray()
    }
    fun playerInfoAddBody(localIndex: Int, adds: List<PlayerAdd>): ByteArray {
        val out = BitWriter()
        // Pass 1: local high-res slot, +0x27=false. Keep it present with mvt=0/no-ext.
        out.writeBits(1, 1)
        out.writeBits(1, 0)
        out.writeBits(2, 0)
        out.alignToByte()
        // Pass 2: no high-res inactive slots yet.
        out.alignToByte()
        val activeOrder = (1 until 2048).filter { it != localIndex }
        var cursor = 0
        for (add in adds.sortedBy { it.idx }) {
            val pos = activeOrder.indexOf(add.idx)
            require(pos >= cursor) { "player add indices must be ascending active low-res slots" }
            writePlayerSkipRun(out, pos - cursor)
            out.writeBits(1, 1)
            out.writeBits(2, 0) // external update mode 0: add/promote to high-res
            out.writeBits(1, 0) // no recursive low-res update
            out.writeBits(6, add.x and 0x3f)
            out.writeBits(6, add.y and 0x3f)
            out.writeBits(1, 0) // no ext-info block
            cursor = pos + 1
        }
        writePlayerSkipRun(out, activeOrder.size - cursor)
        out.alignToByte()
        // Pass 4: no inactive pending slots in this synthetic prefix.
        out.alignToByte()
        return out.toByteArray()
    }
    val appearanceEnabledSlots = (0..18).filter { it !in setOf(12, 13, 17) }
    fun appearanceSlots(overrides: Map<Int, Pair<Int, Int>>): List<AppearanceSlot> =
        appearanceEnabledSlots.map { slot ->
            val (kitId, itemId) = overrides[slot] ?: (-1 to -1)
            when {
                itemId >= 0 -> AppearanceSlot.Item(itemId)
                kitId >= 0 -> AppearanceSlot.Kit(kitId)
                else -> AppearanceSlot.Empty
            }
        }
    fun appearancePayload(overrides: Map<Int, Pair<Int, Int>>): ByteArray =
        PlayerAppearanceEncoder.encode(
            PlayerAppearance(
                slots = appearanceSlots(overrides),
                kitColours = IntArray(10),
                kitStyles = IntArray(10),
                name = "SelfTest",
            ),
        )
    fun ByteArrayOutputStream.appearanceEntry(payload: ByteArray) {
        u8((-0x80 - payload.size) and 0xFF)
        for (b in payload) u8(((b.toInt() and 0xFF) + 0x80) and 0xFF)
    }
    fun playerInfoLocalAppearanceBody(localIndex: Int, payload: ByteArray): ByteArray {
        val bits = BitWriter()
        // Pass 1: local high-res slot, mvt=0, has ext-info.
        bits.writeBits(1, 1)
        bits.writeBits(1, 1)
        bits.writeBits(2, 0)
        bits.alignToByte()
        // Pass 2: no active rendered slots.
        bits.alignToByte()
        // Pass 3: all other slots are low-res active in the synthetic prefix; skip them.
        writePlayerSkipRun(bits, 2046)
        bits.alignToByte()
        // Pass 4: no inactive pending slots.
        bits.alignToByte()

        val extBlock = ByteArrayOutputStream().apply {
            u8(0x08) // player update mask bit 3: APPEARANCE
            appearanceEntry(payload)
        }.toByteArray()
        return bits.toByteArray() + ByteArrayOutputStream().apply {
            u16(extBlock.size)
            write(extBlock)
        }.toByteArray()
    }
    fun playerInfoLocalEmptyAppearanceBody(localIndex: Int): ByteArray {
        val bits = BitWriter()
        bits.writeBits(1, 1)
        bits.writeBits(1, 1)
        bits.writeBits(2, 0)
        bits.alignToByte()
        bits.alignToByte()
        writePlayerSkipRun(bits, 2046)
        bits.alignToByte()
        bits.alignToByte()
        val extBlock = byteArrayOf(0x08, 0x80.toByte()) // mask APPEARANCE + zero-length transformed payload
        return bits.toByteArray() + ByteArrayOutputStream().apply {
            u16(extBlock.size)
            write(extBlock)
        }.toByteArray()
    }
    fun appearanceJson(overrides: Map<Int, Pair<Int, Int>>): String =
        (0 until 19).joinToString(prefix = "[", postfix = "]") { slot ->
            val (kitId, itemId) = overrides[slot] ?: (-1 to -1)
            """{"kitId":$kitId,"itemId":$itemId}"""
        }
    fun playerInfoRemoveThenAddBody(localIndex: Int, removeIdx: Int, add: PlayerAdd): ByteArray {
        val out = BitWriter()
        // Pass 1: local high-res slot, +0x27=false. Keep it present with mvt=0/no-ext.
        out.writeBits(1, 1)
        out.writeBits(1, 0)
        out.writeBits(2, 0)
        out.alignToByte()
        // Pass 2: remove the already-rendered active player.
        out.writeBits(1, 1)
        out.writeBits(1, 0)
        out.writeBits(2, 0)
        out.writeBits(1, 0)
        out.alignToByte()
        // Pass 3 must use the pending vector from packet entry, before removeIdx becomes pending.
        val activePendingOrder = (1 until 2048).filter { it != localIndex && it != removeIdx }
        val pos = activePendingOrder.indexOf(add.idx)
        require(pos >= 0) { "add index ${add.idx} must be in the old active pending vector" }
        writePlayerSkipRun(out, pos)
        out.writeBits(1, 1)
        out.writeBits(2, 0)
        out.writeBits(1, 0)
        out.writeBits(6, add.x and 0x3f)
        out.writeBits(6, add.y and 0x3f)
        out.writeBits(1, 0)
        writePlayerSkipRun(out, activePendingOrder.size - pos - 1)
        out.alignToByte()
        // Pass 4: no inactive pending slots in this synthetic prefix.
        out.alignToByte()
        return out.toByteArray()
    }
    fun npcInfoAddBody(localX: Int, localY: Int, adds: List<NpcAdd>): ByteArray {
        val out = BitWriter()
        out.writeBits(8, 0) // no existing NPCs in phase 1
        for (add in adds) {
            out.writeBits(16, add.idx)
            out.writeBits(7, (add.x - localX) and 0x7F)
            out.writeBits(2, add.plane and 0x3)
            out.writeBits(7, (add.y - localY) and 0x7F)
            out.writeBits(16, (add.typeId shl 1) or 1)
            out.writeBits(1, 0)
            out.writeBits(3, 0)
            out.writeBits(1, 0)
        }
        out.writeBits(16, 0xFFFF)
        return out.toByteArray()
    }

    /**
     * Write a session whose framed s2c plane is exactly [packets] (op -> body) and whose
     * `state-snapshots.jsonl` is [snapshotJson] (the at-exit client state). No socket plane is written
     * so byte-accounting stays "unavailable" (WARN) and the focus is the client cross-check.
     */
    fun writeClientStateSession(name: String, packets: List<Pair<Int, ByteArray>>, snapshotJson: String): File {
        val dir = File(tmp, name).apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, b) in packets) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","state":30,"op":$op,"len":${b.size},"body":"${b64.encodeToString(b)}"}""")
            }
        }
        File(dir, "framed-c2s.jsonl").writeText("")
        File(dir, "session.json").writeText("""{"session_id":"$name","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "state-snapshots.jsonl").writeText(snapshotJson)
        return dir
    }

    fun writeTimedClientStateSession(name: String, packets: List<Triple<Int, Long, ByteArray>>, snapshotJson: String): File {
        val dir = File(tmp, name).apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, monoUs, b) in packets) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","state":30,"op":$op,"len":${b.size},"mono_us":$monoUs,"body":"${b64.encodeToString(b)}"}""")
            }
        }
        File(dir, "framed-c2s.jsonl").writeText("")
        File(dir, "session.json").writeText("""{"session_id":"$name","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "state-snapshots.jsonl").writeText(snapshotJson)
        return dir
    }

    // 12a-0. AVATAR TRAJECTORY: trustReport summarizes the local player's per-tick tile path from
    //         state-snapshots.jsonl in both Markdown and JSON. Multiple oracle lines in one tick use
    //         the last tile for that tick, and the older top-level `player` shape still parses.
    run {
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":100,"tick":42,"local_player":{"render_tile":{"x":3200,"y":3200,"plane":0}}}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":180,"tick":42,"local_player":{"render_tile":{"x":3201,"y":3200,"plane":0}}}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":260,"tick":43,"player":{"x":3202,"y":3201,"plane":0}}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":340,"tick":44,"local_player":{"render_tile":{"x":3202,"y":3202,"plane":1}}}""")
        }
        val dir = writeTimedClientStateSession("session-king-avatar-trajectory", emptyList(), snapshot)
        val r = verify(dir)
        val expected = listOf(
            42 to ClientStateCrossCheck.Tile(3201, 3200, 0),
            43 to ClientStateCrossCheck.Tile(3202, 3201, 0),
            44 to ClientStateCrossCheck.Tile(3202, 3202, 1),
        )
        val trajectoryOk = r.avatarTrajectory.map { it.tick to it.tile } == expected
        val lastTickWins = r.avatarTrajectory.firstOrNull()?.monoUs == 180L
        val md = TrustReportRenderer.markdown(r)
        val jsonText = TrustReportRenderer.jsonText(r)
        val markdownOk = md.contains("### Avatar tile trajectory") &&
            md.contains("| 42 | (3201,3200,0) |") &&
            md.contains("| 43 | (3202,3201,0) |") &&
            md.contains("| 44 | (3202,3202,1) |")
        val jsonOk = jsonText.contains("\"avatar_trajectory\"") &&
            jsonText.contains("\"source\": \"state-snapshots.jsonl\"") &&
            jsonText.contains("\"local_player\"") &&
            jsonText.contains("\"tick\": 42") &&
            jsonText.contains("\"mono_us\": 180") &&
            jsonText.contains("\"x\": 3201") &&
            jsonText.contains("\"plane\": 1")
        if (trajectoryOk && lastTickWins && markdownOk && jsonOk)
            pass("avatar trajectory: per-tick local tile path renders in Markdown and JSON; same-tick snapshots use the latest tile")
        else fail("avatar trajectory: trajectoryOk=$trajectoryOk lastTickWins=$lastTickWins markdownOk=$markdownOk jsonOk=$jsonOk trajectory=${r.avatarTrajectory}\nMD:\n$md\nJSON:\n$jsonText")
    }

    // 12. CLIENT-IS-KING AGREE: a synthetic snapshot whose varps/skills/run-energy exactly match the
    //     values our decoder recovers from the state-bearing s2c packets -> OK, no failure, and the
    //     verdict is not FAIL on the cross-check's account.
    run {
        // var 100 set twice (last write wins -> 7); var 200 a large int; skill 6 (magic); run energy.
        // Run energy uses s2c op 0x0d (13) and a 0..100 RAW percentage — here 100 (a full bar), the
        // exact live-capture value. (Op 0x50/80 is the private-chat filter and is NOT decoded as run
        // energy; it is not in STATE_BEARING_OPCODES, so a stray op80 below must be IGNORED.)
        val packets = listOf(
            OP_VS to varpSmallBody(100, 3),
            OP_VS to varpSmallBody(100, 7),          // last write wins
            OP_VL to varpLargeBody(200, 123456),
            OP_VLONG to varpLongBody(250, 654321L),
            OP_US to updateStatBody(skillId = 6, level = 99, xp = 13_034_431),  // magic, level 99
            OP_RE to runEnergyBody(100),                                        // op 0x0d, 0..100 RAW
            80 to runEnergyBody(1),   // op 0x50 chat-filter "Friends" — MUST NOT be read as run energy
        )
        // base for xp 13_034_431 is level 99 (the xp→level table); the snapshot mirrors that.
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varps":{"100":7,"200":123456,"250":654321},""" +
            """"skills":[{"id":6,"level":99,"base":99,"xp":13034431}],"run_energy":100,"run_weight":12}""" + "\n"
        val dir = writeClientStateSession("session-king-agree", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        // Run energy decoded from op 0x0d must equal the client snapshot's 100 (proves op 0x0d is the
        // run-energy source); the op80 frame must NOT have overwritten it with the chat-filter 1.
        val energyOk = cc.scalarMismatches.none { it.field == "runEnergy" }
        val ok = cc.available && !cc.hasFailure && cc.varpMatches == 3 && cc.skillMatches == 1 &&
            cc.varpMismatches.isEmpty() && cc.varpMissed.isEmpty() && cc.scalarMismatches.isEmpty()
        // The cross-check must NOT contribute a FAIL (a missing-socket WARN is fine).
        val noKingFail = r.reasons.none { it.contains("client-is-king: our decoded state does NOT", ignoreCase = true) }
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val allFiveVerified = setOf(OP_VS, OP_VL, OP_VLONG, OP_US, OP_RE).all { it in verifiedOps }
        val s2cBreadth = r.clientVerifiedBreadth.first { it.dir == "s2c" }
        val s2cVolume = r.clientVerifiedCoverage.first { it.dir == "s2c" }
        val breadthOk = s2cBreadth.clientVerifiedOpcodes == 5 && s2cBreadth.observedOpcodes == 6
        val volumeOk = s2cVolume.clientVerifiedPackets == 6 && s2cVolume.observedPackets == 7
        val rendered = TrustReportRenderer.markdown(r)
        val breadthIndex = rendered.indexOf("s2c Client-verified opcodes")
        val volumeIndex = rendered.indexOf("s2c Client-verified volume")
        val breadthLeads = breadthIndex >= 0 && volumeIndex > breadthIndex
        if (ok && energyOk && noKingFail && allFiveVerified && breadthOk && volumeOk && breadthLeads)
            pass("client-king AGREE: decoded varps(3 incl long)+skills(1)+runEnergy(op0x0d=100, NOT op80) match snapshot; ops 28/61/147/44/13 are CLIENT_VERIFIED; breadth=5/6 opcodes leads volume=6/7 packets")
        else fail("client-king AGREE: available=${cc.available} hasFailure=${cc.hasFailure} varpMatch=${cc.varpMatches} skillMatch=${cc.skillMatches} mism=${cc.varpMismatches} missed=${cc.varpMissed} scalar=${cc.scalarMismatches} energyOk=$energyOk noKingFail=$noKingFail verifiedOps=$verifiedOps breadth=$s2cBreadth volume=$s2cVolume breadthLeads=$breadthLeads")

        File(dir, "prot-table.json").writeText(
            """
            {
              "build": "selftest",
              "server_prot": [
                {"op":13,"size_class":1},
                {"op":28,"size_class":6},
                {"op":44,"size_class":6},
                {"op":61,"size_class":3},
                {"op":80,"size_class":1},
                {"op":147,"size_class":10},
                {"op":191,"size_class":4}
              ],
              "client_prot": []
            }
            """.trimIndent()
        )
        val ledger = CoverageLedger(codec).write(dir, r)
        val s2cLedger = ledger.directions.first { it.dir == "s2c" }
        val ledgerOk = s2cLedger.tableSource == "prot-table" &&
            s2cLedger.inTable == 7 &&
            s2cLedger.observed == 6 &&
            s2cLedger.clientVerified == 5 &&
            s2cLedger.unseen.singleOrNull()?.opcode == 191 &&
            File(dir, "coverage.json").exists() &&
            File(dir, "coverage.md").exists()
        if (ledgerOk) pass("coverage ledger: live table minus observed writes coverage.{md,json}; s2c in_table=7 observed=6 client_verified=5 unseen=op191")
        else fail("coverage ledger: $s2cLedger")
    }

    // 12a-1b. CLIENT-IS-KING VARC AGREE: plain numeric/string varcs fold into the expected state,
    //          keyed by (recordKind,varId), op5 resets the expected varc state, and varcbit op48/69
    //          remain BINARY_PROVEN (not client-verified) until cache varcbit defs are wired.
    run {
        val inline = "inline-sso" // len < 0x18, oracle SSO inline branch
        val heap = "abcdefghijklmnopqrstuvwxyz" // len >= 0x18, oracle SSO heap branch
        val longValue = 0x0102_0304_0506_0708L
        val packets = listOf(
            OP_VC_SMALL to varcSmallBody(999, 1),
            OP_VC_STR to varcStrBody(999, "before-reset"),
            OP_RESET_VARCACHE to ByteArray(0),
            OP_VC_SMALL to varcSmallBody(28, -12),
            OP_VC_LARGE to varcLargeBody(61, 0x01020304),
            OP_VC_LONG to varcLongBody(196, longValue),
            OP_VC_STR to varcStrBody(147, inline),
            OP_VC_STR_LARGE to varcStrLargeBody(148, heap),
            OP_VCBIT_SMALL to varcBitSmallBody(5188, 3),
            OP_VCBIT_LARGE to varcBitLargeBody(5189, 0x01020304),
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varcs":[""" +
            """{"kind":1,"id":28,"value_kind":0,"val":-12},""" +
            """{"kind":1,"id":61,"value_kind":0,"val":16909060},""" +
            """{"kind":1,"id":196,"value_kind":1,"val":$longValue}],""" +
            """"varcstrings":[""" +
            """{"kind":2,"id":147,"value_kind":2,"str":"$inline"},""" +
            """{"kind":2,"id":148,"value_kind":2,"str":"$heap"}]}""" + "\n"
        val dir = writeClientStateSession("session-king-varc-agree", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val binaryOps = r.opcodeTrust
            .filter { it.confidence == Confidence.BINARY_PROVEN }
            .map { it.opcode }
            .toSet()
        val varcOk = cc.available && !cc.hasFailure &&
            cc.varcMatches == 3 && cc.varcMismatches.isEmpty() && cc.varcMissed.isEmpty() &&
            cc.varcUnverified.isEmpty() && cc.varcExtra.isEmpty() &&
            cc.varcStringMatches == 2 && cc.varcStringMismatches.isEmpty() && cc.varcStringMissed.isEmpty() &&
            cc.varcStringUnverified.isEmpty() && cc.varcStringExtra.isEmpty()
        val allVarcVerified = setOf(OP_VC_SMALL, OP_VC_LARGE, OP_VC_LONG, OP_VC_STR, OP_VC_STR_LARGE).all { it in verifiedOps }
        val varcbitDeferred = OP_VCBIT_SMALL in binaryOps && OP_VCBIT_LARGE in binaryOps &&
            OP_VCBIT_SMALL !in verifiedOps && OP_VCBIT_LARGE !in verifiedOps
        val resetNotVerified = OP_RESET_VARCACHE !in verifiedOps
        val sizesAndNames = codec.serverProtSize(OP_RESET_VARCACHE) == 0 && codec.serverProtName(OP_RESET_VARCACHE) == "ResetClientVarcache" &&
            codec.serverProtSize(OP_VC_SMALL) == 3 && codec.serverProtName(OP_VC_SMALL) == "ClientSetVarcSmall" &&
            codec.serverProtSize(OP_VC_LARGE) == 6 && codec.serverProtName(OP_VC_LARGE) == "ClientSetVarcLarge" &&
            codec.serverProtSize(OP_VC_LONG) == 10 && codec.serverProtName(OP_VC_LONG) == "UNKNOWN_196" &&
            codec.serverProtSize(OP_VC_STR) == -1 && codec.serverProtName(OP_VC_STR) == "ClientSetVarcStr" &&
            codec.serverProtSize(OP_VC_STR_LARGE) == -2 && codec.serverProtName(OP_VC_STR_LARGE) == "ClientSetVarcStrLarge" &&
            codec.serverProtSize(OP_VCBIT_SMALL) == 3 && codec.serverProtName(OP_VCBIT_SMALL) == "ClientSetVarcBitSmall" &&
            codec.serverProtSize(OP_VCBIT_LARGE) == 6 && codec.serverProtName(OP_VCBIT_LARGE) == "ClientSetVarcBitLarge"
        if (varcOk && allVarcVerified && varcbitDeferred && resetNotVerified && sizesAndNames)
            pass("client-king VARC AGREE: op47/64/196 numeric + op92/116 strings (inline+heap SSO snapshot values) match by (recordKind,varId); op5 reset honored; op48/69 stay BINARY_PROVEN")
        else fail("client-king VARC AGREE: varcOk=$varcOk verifiedOps=$verifiedOps binaryOps=$binaryOps resetNotVerified=$resetNotVerified sizesAndNames=$sizesAndNames cc=$cc")
    }

    // 12a-1b2. CLIENT-IS-KING VARC SNAPSHOT ACCUMULATION: varcs are captured from the client's
    //           transient active update tree, which drains by the final snapshot. Accumulate
    //           varcs/varcstrings across all post-handler snapshots, last-write-wins, reset on op5,
    //           while keeping varps on the final-snapshot model.
    run {
        val packets = listOf(
            Triple(OP_VC_SMALL, 1_000L, varcSmallBody(999, 1)),
            Triple(OP_RESET_VARCACHE, 2_000L, ByteArray(0)),
            Triple(OP_VS, 2_100L, varpSmallBody(100, 7)),
            Triple(OP_VC_SMALL, 3_000L, varcSmallBody(28, -12)),
            Triple(OP_VC_LARGE, 3_100L, varcLargeBody(61, 0x01020304)),
            Triple(OP_VC_STR, 3_200L, varcStrBody(147, "first")),
            Triple(OP_VC_STR, 3_300L, varcStrBody(147, "second")),
        )
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":1500,"varps":{"999":1},"varcs":[{"kind":1,"id":999,"value_kind":0,"val":1}],"varcstrings":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":2001,"varps":{"999":1},"varcs":[],"varcstrings":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":3001,"varps":{"999":1},"varcs":[{"kind":1,"id":28,"value_kind":0,"val":-12}],"varcstrings":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":3201,"varps":{"999":1},"varcs":[{"kind":1,"id":61,"value_kind":0,"val":16909060}],"varcstrings":[{"kind":2,"id":147,"value_kind":2,"str":"first"}]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":3301,"varps":{"100":7},"varcs":[],"varcstrings":[{"kind":2,"id":147,"value_kind":2,"str":"second"}]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":4000,"varps":{"100":7},"varcs":[],"varcstrings":[]}""")
        }
        val dir = writeTimedClientStateSession("session-king-varc-accumulate", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val accumulated = cc.available && !cc.hasFailure &&
            cc.varpMatches == 1 &&
            999 !in cc.varpMissed &&
            cc.varcMatches == 2 &&
            cc.varcMismatches.isEmpty() &&
            cc.varcMissed.isEmpty() &&
            cc.varcUnverified.isEmpty() &&
            cc.varcExtra.isEmpty() &&
            cc.varcStringMatches == 1 &&
            cc.varcStringMismatches.isEmpty() &&
            cc.varcStringMissed.isEmpty() &&
            cc.varcStringUnverified.isEmpty()
        val lastWriteWins = cc.varcStringExtra.isEmpty()
        val promoted = listOf(OP_VC_SMALL, OP_VC_LARGE, OP_VC_STR).all { it in verifiedOps }
        if (accumulated && lastWriteWins && promoted)
            pass("client-king VARC ACCUMULATION: varcs/varcstrings union across drained snapshots, last-write-wins for strings; varps stay final-only; op47/64/92 promote")
        else fail("client-king VARC ACCUMULATION: accumulated=$accumulated lastWriteWins=$lastWriteWins promoted=$promoted verifiedOps=$verifiedOps cc=$cc")
    }

    // 12a-1c. CLIENT-IS-KING VARC MISMATCH: the client's varc tree is authoritative. A wrong value
    //          or a client-held varc we never decoded is a hard FAIL, same as varps.
    run {
        val packets = listOf(OP_VC_SMALL to varcSmallBody(28, 5))
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varcs":[""" +
            """{"kind":1,"id":28,"value_kind":0,"val":6},""" +
            """{"kind":1,"id":61,"value_kind":0,"val":99}],""" +
            """"varcstrings":[]}""" + "\n"
        val dir = writeClientStateSession("session-king-varc-mismatch", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val mismatchFlagged = cc.varcMismatches.any {
            it.key == ClientStateCrossCheck.VarcKey(ClientStateCrossCheck.VARC_RECORD_KIND_NUMERIC, 28) &&
                it.ourValue == "kind0:5" && it.clientValue == "kind0:6"
        }
        val missedFlagged = cc.varcMissed.any {
            it.key == ClientStateCrossCheck.VarcKey(ClientStateCrossCheck.VARC_RECORD_KIND_NUMERIC, 61) &&
                it.valueKind == ClientStateCrossCheck.VARC_VALUE_KIND_INT32 &&
                it.value == 99L
        }
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        val reasonFail = r.reasons.any { it.contains("varc MISMATCH", ignoreCase = true) }
        if (mismatchFlagged && missedFlagged && verdictFail && reasonFail)
            pass("client-king VARC MISMATCH: numeric varc kind1/id28 ours=5≠client=6 + missed kind1/id61 -> FAIL")
        else fail("client-king VARC MISMATCH: mismatchFlagged=$mismatchFlagged missedFlagged=$missedFlagged verdictFail=$verdictFail reasonFail=$reasonFail cc=$cc reasons=${r.reasons}")
    }

    // 12a-1c2. CLIENT-IS-KING VARC UNVERIFIED: folded varc writes with an empty oracle array are not
    //           matches, are not advisory extras, and must never promote their opcodes.
    run {
        val packets = listOf(
            OP_VC_SMALL to varcSmallBody(28, 5),
            OP_VC_LARGE to varcLargeBody(61, 6),
            OP_VC_STR to varcStrBody(147, "no-oracle"),
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varcs":[],"varcstrings":[]}""" + "\n"
        val dir = writeClientStateSession("session-king-varc-unverified", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val checks = cc.opcodeWriteChecks.associateBy { it.opcode }
        val zeroMatchStats = listOf(OP_VC_SMALL, OP_VC_LARGE, OP_VC_STR).all { op ->
            val stat = checks[op]
            stat != null && stat.matches == 0 && stat.mismatches == 0 && stat.unverified == 1 && !stat.clientVerified
        }
        val unverifiedNotExtra = cc.varcMatches == 0 &&
            cc.varcUnverified.size == 2 &&
            cc.varcExtra.isEmpty() &&
            cc.varcStringMatches == 0 &&
            cc.varcStringUnverified.size == 1 &&
            cc.varcStringExtra.isEmpty()
        val noPromotion = listOf(OP_VC_SMALL, OP_VC_LARGE, OP_VC_STR).none { it in verifiedOps }
        val jsonText = TrustReportRenderer.jsonText(r)
        val jsonUnverified = jsonText.contains("\"varc_unverified\"") &&
            jsonText.contains("\"varcstring_unverified\"") &&
            jsonText.contains("\"unverified\": 1")
        if (zeroMatchStats && unverifiedNotExtra && noPromotion && jsonUnverified)
            pass("client-king VARC UNVERIFIED: domain with 0 matches keeps op47/64/92 out of CLIENT_VERIFIED and reports folded writes as unverified, not extras")
        else fail("client-king VARC UNVERIFIED: zeroMatchStats=$zeroMatchStats unverifiedNotExtra=$unverifiedNotExtra noPromotion=$noPromotion jsonUnverified=$jsonUnverified checks=${cc.opcodeWriteChecks} verifiedOps=$verifiedOps cc=$cc")
    }

    // 12a-1c3. CLIENT-IS-KING INVENTORY AGREE: op85 full inventory folds to final snapshot,
    //           item ids render through obj gamevals, and op85 promotes only through matches>0.
    run {
        val packets = listOf(OP_INV_FULL to invFullBody(93, entries = listOf(InvEntry(0, 995, 17))))
        val snapshot = """{"kind":"state_snapshot","main_state":30,"inventories":[""" +
            """{"key":186,"invId":93,"domainBit":0,"slots":[{"slot":0,"item":995,"count":17}]}]}""" + "\n"
        val dir = writeClientStateSession("session-king-inventory-agree", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val jsonText = TrustReportRenderer.jsonText(r)
        val agreed = cc.available && !cc.hasFailure &&
            cc.inventoryMatches == 1 &&
            cc.inventoryMismatches.isEmpty() &&
            cc.inventoryMissed.isEmpty()
        val promoted = OP_INV_FULL in verifiedOps
        val jsonCount = jsonText.contains("\"inventory_matches\": 1")
        if (agreed && promoted && jsonCount)
            pass("client-king INVENTORY AGREE: op85 full inventory slot matches final snapshot and promotes")
        else fail("client-king INVENTORY AGREE: agreed=$agreed promoted=$promoted jsonCount=$jsonCount verifiedOps=$verifiedOps cc=$cc\nJSON:\n$jsonText")
    }

    // 12a-1c4. CLIENT-IS-KING INVENTORY MISMATCH: final inventory store is authoritative.
    run {
        val packets = listOf(OP_INV_FULL to invFullBody(93, entries = listOf(InvEntry(0, 995, 17))))
        val snapshot = """{"kind":"state_snapshot","main_state":30,"inventories":[""" +
            """{"key":186,"invId":93,"domainBit":0,"slots":[{"slot":0,"item":995,"count":18}]}]}""" + "\n"
        val dir = writeClientStateSession("session-king-inventory-mismatch", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val mismatchFlagged = cc.inventoryMismatches.singleOrNull()?.let {
            it.invId == 93 && it.slot == 0 && it.ourItemId == 995 && it.ourCount == 17L &&
                it.clientItemId == 995 && it.clientCount == 18L
        } == true
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        val noPromotion = OP_INV_FULL !in verifiedOps
        val reasonFail = r.reasons.any { it.contains("inventory MISMATCH", ignoreCase = true) }
        val md = TrustReportRenderer.markdown(r)
        val jsonText = TrustReportRenderer.jsonText(r)
        val named = md.contains("obj:coins(995)×17") &&
            md.contains("obj:coins(995)×18") &&
            jsonText.contains("\"item_id\": 995") &&
            jsonText.contains("\"gameval_type\": \"obj\"") &&
            jsonText.contains("\"name\": \"coins\"")
        if (mismatchFlagged && verdictFail && noPromotion && reasonFail && named)
            pass("client-king INVENTORY MISMATCH: op85 ours coins×17 vs client coins×18 hard-fails, names obj coins(995), and does not promote")
        else fail("client-king INVENTORY MISMATCH: mismatchFlagged=$mismatchFlagged verdictFail=$verdictFail noPromotion=$noPromotion reasonFail=$reasonFail named=$named verifiedOps=$verifiedOps cc=$cc reasons=${r.reasons}\nMD:\n$md\nJSON:\n$jsonText")
    }

    // 12a-1c5. CLIENT-IS-KING INVENTORY RESET/MUTATE: later op85 replaces container, then op121
    //           mutates slots; removed pre-reset slots must not survive expected state.
    run {
        val packets = listOf(
            OP_INV_FULL to invFullBody(93, entries = listOf(InvEntry(0, 995, 17), InvEntry(1, 315, 1))),
            OP_INV_FULL to invFullBody(93, entries = listOf(InvEntry(0, 315, 1))),
            OP_INV_PARTIAL to invPartialBody(93, entries = listOf(InvEntry(3, 1205, 1))),
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"inventories":[""" +
            """{"key":186,"invId":93,"domainBit":0,"slots":[""" +
            """{"slot":0,"item":315,"count":1},""" +
            """{"slot":3,"item":1205,"count":1}]}]}""" + "\n"
        val dir = writeClientStateSession("session-king-inventory-reset-mutate", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val checks = cc.opcodeWriteChecks.associateBy { it.opcode }
        val verifiedOps = r.opcodeTrust
            .filter { it.confidence == Confidence.CLIENT_VERIFIED }
            .map { it.opcode }
            .toSet()
        val agreed = cc.available && !cc.hasFailure &&
            cc.inventoryMatches == 2 &&
            cc.inventoryMismatches.isEmpty() &&
            cc.inventoryMissed.isEmpty()
        val gate = checks[OP_INV_FULL]?.let { it.matches == 1 && it.mismatches == 0 && it.clientVerified } == true &&
            checks[OP_INV_PARTIAL]?.let { it.matches == 1 && it.mismatches == 0 && it.clientVerified } == true
        val promoted = OP_INV_FULL in verifiedOps && OP_INV_PARTIAL in verifiedOps
        if (agreed && gate && promoted)
            pass("client-king INVENTORY RESET/MUTATE: op85 replacement drops stale slot, op121 adds dagger slot, both op85/op121 promote via match gate")
        else fail("client-king INVENTORY RESET/MUTATE: agreed=$agreed gate=$gate promoted=$promoted checks=${cc.opcodeWriteChecks} verifiedOps=$verifiedOps cc=$cc")
    }

    // 12a-1c6. CLIENT-IS-KING SCENE NPC AGREE: op52 phase-2 adds fold to the final scene oracle by
    //           index, type id, and best-effort tile; op52 promotes only when matches>0 and no
    //           scene mismatches exist.
    run {
        val body = npcInfoAddBody(
            localX = 3200,
            localY = 3200,
            adds = listOf(
                NpcAdd(idx = 608, typeId = 0, x = 3202, y = 3202, plane = 0),
                NpcAdd(idx = 11684, typeId = 11, x = 3199, y = 3198, plane = 0),
            ),
        )
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":100,"local_player":{"render_tile":{"x":3200,"y":3200,"plane":0}},"npcs":[],"players":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":300,"local_player":{"render_tile":{"x":3200,"y":3200,"plane":0}},"npcs":[""" +
                """{"idx":608,"type":0,"x":3202,"y":3202,"plane":0},""" +
                """{"idx":11684,"type":11,"x":3199,"y":3198,"plane":0}],"players":[]}""")
        }
        val dir = writeTimedClientStateSession("session-king-scene-npc-agree", listOf(Triple(OP_NPC_INFO, 200L, body)), snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val agreed = cc.available && !cc.hasFailure &&
            cc.sceneNpcPresenceMatches == 2 &&
            cc.sceneNpcPresenceMismatches.isEmpty() &&
            cc.sceneNpcTypeMatches == 2 &&
            cc.sceneNpcTypeMismatches.isEmpty() &&
            cc.sceneNpcPositionMatches == 2 &&
            cc.sceneNpcPositionMismatches.isEmpty()
        val promoted = OP_NPC_INFO in verifiedOps
        val rendered = TrustReportRenderer.markdown(r)
        val named = rendered.contains("scene") || TrustReportRenderer.jsonText(r).contains("\"scene_npc_presence_matches\": 2")
        if (agreed && promoted && named)
            pass("client-king SCENE NPC AGREE: op52 add list matches final scene oracle for presence/type/tile and promotes")
        else fail("client-king SCENE NPC AGREE: agreed=$agreed promoted=$promoted named=$named verifiedOps=$verifiedOps cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c7. CLIENT-IS-KING SCENE PLAYER AGREE: op81 seeds the GPI low-res slots, then op22
    //           promotes external players to high-res. Presence and best-effort add positions match.
    run {
        val localIndex = 100
        val adds = listOf(
            PlayerAdd(idx = 101, x = 3202, y = 3203),
            PlayerAdd(idx = 110, x = 3210, y = 3211),
        )
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = adds.associate { it.idx to playerRegionWord(it.x, it.y, it.plane) },
        )
        val body = playerInfoAddBody(localIndex, adds)
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":300,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[""" +
                """{"idx":101,"x":3202,"y":3203,"plane":0},""" +
                """{"idx":110,"x":3210,"y":3211,"plane":0},""" +
                """{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-scene-player-agree",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 200L, body),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val checks = cc.opcodeWriteChecks.associateBy { it.opcode to it.domain }
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val agreed = cc.available && !cc.hasFailure &&
            cc.scenePlayerPresenceMatches == 3 &&
            cc.scenePlayerPresenceMismatches.isEmpty() &&
            cc.scenePlayerPositionMatches == 3 &&
            cc.scenePlayerPositionMismatches.isEmpty()
        val op22Gate = checks[OP_PLAYER_INFO to "scene-player"]?.let {
            it.matches == 2 && it.mismatches == 0 && it.clientVerified
        } == true
        val promoted = OP_PLAYER_INFO in verifiedOps && OP_REBUILD_NORMAL_SIMPLE !in verifiedOps
        if (agreed && op22Gate && promoted)
            pass("client-king SCENE PLAYER AGREE: op81 GPI prefix + op22 external adds match player presence/tile; op22 promotes, op81 does not")
        else fail("client-king SCENE PLAYER AGREE: agreed=$agreed op22Gate=$op22Gate promoted=$promoted checks=${cc.opcodeWriteChecks} verifiedOps=$verifiedOps cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c7a. CLIENT-IS-KING LOCAL APPEARANCE AGREE: op22 APPEARANCE ext-info decodes the
    //             local avatar's 19 kit/item identity slots and compares them to oracle appearance.
    run {
        val localIndex = 100
        val appearance = mapOf(
            4 to (880 to -1),
            14 to (-1 to 1205),
        )
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = emptyMap(),
        )
        val body = playerInfoLocalAppearanceBody(localIndex, appearancePayload(appearance))
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[],"appearance":${appearanceJson(appearance)}}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-appearance-agree",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 120L, body),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val checks = cc.opcodeWriteChecks.associateBy { it.opcode to it.domain }
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val agreed = cc.available && !cc.hasFailure &&
            cc.appearanceMatches == 19 &&
            cc.appearanceMismatches.isEmpty() &&
            cc.appearanceMissed.isEmpty()
        val gate = checks[OP_PLAYER_INFO to "appearance"]?.let {
            it.matches == 19 && it.mismatches == 0 && it.clientVerified
        } == true
        val promoted = OP_PLAYER_INFO in verifiedOps
        if (agreed && gate && promoted)
            pass("client-king APPEARANCE AGREE: op22 APPEARANCE folded 19 local kit/item slots (slot4 kit880, slot14 dagger) and promotes through the match gate")
        else fail("client-king APPEARANCE AGREE: agreed=$agreed gate=$gate promoted=$promoted checks=${cc.opcodeWriteChecks} verifiedOps=$verifiedOps cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c7a1. CLIENT-IS-KING LOCAL APPEARANCE EMPTY-THEN-VALID: a zero-length local APPEARANCE
    //              block must not abort GPI list maintenance or prevent a later valid local payload.
    run {
        val localIndex = 100
        val appearance = mapOf(4 to (880 to -1), 15 to (-1 to 1205))
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = emptyMap(),
        )
        val empty = playerInfoLocalEmptyAppearanceBody(localIndex)
        val valid = playerInfoLocalAppearanceBody(localIndex, appearancePayload(appearance))
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[],"appearance":${appearanceJson(appearance)}}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-appearance-empty-then-valid",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 120L, empty),
                Triple(OP_PLAYER_INFO, 140L, valid),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val agreed = cc.available && !cc.hasFailure &&
            cc.decodeFailures == 0 &&
            cc.appearanceMatches == 19 &&
            cc.appearanceMismatches.isEmpty() &&
            cc.appearanceMissed.isEmpty()
        if (agreed)
            pass("client-king APPEARANCE EMPTY-THEN-VALID: zero-length local appearance is ignored without poisoning GPI; later valid op22 matches 19 slots")
        else fail("client-king APPEARANCE EMPTY-THEN-VALID: agreed=$agreed cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c7a1b. CLIENT-IS-KING LOCAL APPEARANCE EQUIPMENT WEAPON: live capture proved the local
    //                op22 APPEARANCE block omits the worn weapon, while the client fills appearance
    //                weapon slot15 from verified worn-equipment inv94 slot3.
    run {
        val localIndex = 100
        val packetAppearance = mapOf(4 to (880 to -1))
        val clientAppearance = mapOf(
            4 to (880 to -1),
            15 to (-1 to 1205),
        )
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = emptyMap(),
        )
        val body = playerInfoLocalAppearanceBody(localIndex, appearancePayload(packetAppearance))
        val inv94 = invFullBody(94, entries = listOf(InvEntry(3, 1205, 1)))
        val snapshot = buildString {
            appendLine(
                """{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[],"inventories":[{"invId":94,"slots":[{"slot":3,"item":1205,"count":1}]}],"appearance":${appearanceJson(clientAppearance)}}""",
            )
        }
        val dir = writeTimedClientStateSession(
            "session-king-appearance-equipment-weapon",
            listOf(
                Triple(OP_INV_FULL, 100L, inv94),
                Triple(OP_REBUILD_NORMAL_SIMPLE, 110L, prefix),
                Triple(OP_PLAYER_INFO, 120L, body),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val checks = cc.opcodeWriteChecks.associateBy { it.opcode to it.domain }
        val agreed = cc.available && !cc.hasFailure &&
            cc.inventoryMatches == 1 &&
            cc.appearanceMatches == 19 &&
            cc.appearanceMismatches.isEmpty() &&
            cc.appearanceMissed.isEmpty()
        val equipmentSourced = checks[OP_INV_FULL to "appearance"]?.matches == 1
        if (agreed && equipmentSourced)
            pass("client-king APPEARANCE EQUIPMENT WEAPON: op22 local appearance omits weapon; expected slot15 is sourced from verified inv94 slot3 bronze_dagger(1205)")
        else fail("client-king APPEARANCE EQUIPMENT WEAPON: agreed=$agreed equipmentSourced=$equipmentSourced checks=${cc.opcodeWriteChecks} cc=$cc")
    }

    // 12a-1c7a2. CLIENT-IS-KING LOCAL APPEARANCE MISMATCH: final oracle appearance is authoritative,
    //              item ids render through obj gamevals, and any mismatch blocks op22 promotion.
    run {
        val localIndex = 100
        val ours = mapOf(
            4 to (880 to -1),
            14 to (-1 to 1205),
        )
        val client = mapOf(
            4 to (880 to -1),
            14 to (-1 to 995),
        )
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = emptyMap(),
        )
        val body = playerInfoLocalAppearanceBody(localIndex, appearancePayload(ours))
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[],"appearance":${appearanceJson(client)}}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-appearance-mismatch",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 120L, body),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val mismatch = cc.appearanceMismatches.singleOrNull()?.let {
            it.slot == 14 && it.ourItemId == 1205 && it.clientItemId == 995
        } == true
        val noPromotion = OP_PLAYER_INFO !in verifiedOps
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        val reasonFail = r.reasons.any { it.contains("appearance MISMATCH", ignoreCase = true) }
        val md = TrustReportRenderer.markdown(r)
        val jsonText = TrustReportRenderer.jsonText(r)
        val named = md.contains("obj:bronze_dagger(1205)") &&
            md.contains("obj:coins(995)") &&
            jsonText.contains("\"appearance_mismatches\"") &&
            jsonText.contains("\"gameval_type\": \"obj\"") &&
            jsonText.contains("\"name\": \"bronze_dagger\"") &&
            jsonText.contains("\"name\": \"coins\"")
        if (mismatch && noPromotion && verdictFail && reasonFail && named)
            pass("client-king APPEARANCE MISMATCH: op22 item slot ours bronze_dagger(1205) vs client coins(995) hard-fails and blocks promotion")
        else fail("client-king APPEARANCE MISMATCH: mismatch=$mismatch noPromotion=$noPromotion verdictFail=$verdictFail reasonFail=$reasonFail named=$named verifiedOps=$verifiedOps cc=$cc\nMD:\n$md\nJSON:\n$jsonText")
    }

    // 12a-1c7b. CLIENT-IS-KING SCENE PLAYER PERSISTENT VECTORS: op22's four passes walk the
    //             render/pending vectors captured at packet entry. A high-res removal in pass 2 must
    //             not be visible to pass 3's low-res add scan until the final rebuild.
    run {
        val localIndex = 100
        val initial = PlayerAdd(idx = 101, x = 3201, y = 3201)
        val replacement = PlayerAdd(idx = 120, x = 3220, y = 3221)
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = listOf(initial, replacement).associate { it.idx to playerRegionWord(it.x, it.y, it.plane) },
        )
        val addInitial = playerInfoAddBody(localIndex, listOf(initial))
        val removeThenAdd = playerInfoRemoveThenAddBody(localIndex, removeIdx = initial.idx, add = replacement)
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":350,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[""" +
                """{"idx":${replacement.idx},"x":${replacement.x},"y":${replacement.y},"plane":${replacement.plane}},""" +
                """{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-scene-player-persistent-vectors",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 200L, addInitial),
                Triple(OP_PLAYER_INFO, 300L, removeThenAdd),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val agreed = cc.available && !cc.hasFailure &&
            cc.scenePlayerPresenceMatches == 2 &&
            cc.scenePlayerPresenceMismatches.isEmpty() &&
            cc.scenePlayerPositionMismatches.isEmpty()
        val noEarlySlot = cc.scenePlayerPresenceMismatches.none { it.idx == replacement.idx - 1 }
        val promoted = OP_PLAYER_INFO in verifiedOps
        if (agreed && noEarlySlot && promoted)
            pass("client-king SCENE PLAYER VECTORS: pass-2 removal is invisible to pass-3 pending scan until rebuild, so op22 adds idx${replacement.idx} and promotes")
        else fail("client-king SCENE PLAYER VECTORS: agreed=$agreed noEarlySlot=$noEarlySlot promoted=$promoted verifiedOps=$verifiedOps cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c8. CLIENT-IS-KING SCENE PLAYER MISMATCH: final scene oracle is authoritative. A missing
    //           folded player or a client-only player is a hard FAIL and blocks op22 promotion.
    run {
        val localIndex = 100
        val adds = listOf(PlayerAdd(idx = 101, x = 3202, y = 3203))
        val prefix = gpiPrefixBody(
            localIndex = localIndex,
            localX = 3200,
            localY = 3200,
            localPlane = 0,
            lowWords = adds.associate { it.idx to playerRegionWord(it.x, it.y, it.plane) },
        )
        val body = playerInfoAddBody(localIndex, adds)
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":150,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":300,"local_player":{"server_index":$localIndex,"render_tile":{"x":3200,"y":3200,"plane":0}},"players":[""" +
                """{"idx":120,"x":3212,"y":3212,"plane":0},""" +
                """{"idx":$localIndex,"x":3200,"y":3200,"plane":0}],"npcs":[]}""")
        }
        val dir = writeTimedClientStateSession(
            "session-king-scene-player-mismatch",
            listOf(
                Triple(OP_REBUILD_NORMAL_SIMPLE, 100L, prefix),
                Triple(OP_PLAYER_INFO, 200L, body),
            ),
            snapshot,
        )
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val mismatch = cc.scenePlayerPresenceMismatches.size == 2 &&
            cc.scenePlayerPresenceMismatches.any { it.idx == 101 && it.expectedPresent && !it.clientPresent } &&
            cc.scenePlayerPresenceMismatches.any { it.idx == 120 && !it.expectedPresent && it.clientPresent }
        val noPromotion = OP_PLAYER_INFO !in verifiedOps
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        if (mismatch && noPromotion && verdictFail)
            pass("client-king SCENE PLAYER MISMATCH: op22 expected idx101 but oracle held idx120 -> FAIL and no promotion")
        else fail("client-king SCENE PLAYER MISMATCH: mismatch=$mismatch noPromotion=$noPromotion verdictFail=$verdictFail verifiedOps=$verifiedOps cc=$cc\n${TrustReportRenderer.jsonText(r)}")
    }

    // 12a-1c9. CLIENT-IS-KING SCENE NPC MISMATCH: final scene oracle is authoritative. A missing
    //           folded NPC or a client-only NPC is a hard FAIL and blocks op52 promotion.
    run {
        val body = npcInfoAddBody(
            localX = 3200,
            localY = 3200,
            adds = listOf(NpcAdd(idx = 608, typeId = 0, x = 3202, y = 3202, plane = 0)),
        )
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":100,"local_player":{"render_tile":{"x":3200,"y":3200,"plane":0}},"npcs":[],"players":[]}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":300,"local_player":{"render_tile":{"x":3200,"y":3200,"plane":0}},"npcs":[""" +
                """{"idx":700,"type":11,"x":3199,"y":3198,"plane":0}],"players":[]}""")
        }
        val dir = writeTimedClientStateSession("session-king-scene-npc-mismatch", listOf(Triple(OP_NPC_INFO, 200L, body)), snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val verifiedOps = r.opcodeTrust.filter { it.confidence == Confidence.CLIENT_VERIFIED }.map { it.opcode }.toSet()
        val mismatch = cc.sceneNpcPresenceMismatches.size == 2 &&
            cc.sceneNpcPresenceMismatches.any { it.idx == 608 && it.expectedPresent && !it.clientPresent } &&
            cc.sceneNpcPresenceMismatches.any { it.idx == 700 && !it.expectedPresent && it.clientPresent }
        val noPromotion = OP_NPC_INFO !in verifiedOps
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        val jsonText = TrustReportRenderer.jsonText(r)
        val named = jsonText.contains("\"gameval_type\": \"npc\"") && jsonText.contains("\"client_type_id\": 11")
        if (mismatch && noPromotion && verdictFail && named)
            pass("client-king SCENE NPC MISMATCH: op52 expected idx608 but oracle held npc:tramp(11) idx700 -> FAIL and no promotion")
        else fail("client-king SCENE NPC MISMATCH: mismatch=$mismatch noPromotion=$noPromotion verdictFail=$verdictFail named=$named verifiedOps=$verifiedOps cc=$cc\n$jsonText")
    }

    // 12a-1d. VARC NAMING: report detail keeps (recordKind,varId) authoritative but decorates with
    //          var_client gameval names when known, falls back to varc_<id>, and JSON carries raw
    //          fields plus structured valueKind/value or valueKind/str.
    run {
        val knownId = 4969 // mtxmgt_total_untradeable_bonds in re-resources/gamevals/var_client.json
        val unknownId = 65000
        val knownStringId = 147 // quickchat_phrase_obj2
        val packets = listOf(
            OP_VC_SMALL to varcSmallBody(knownId, 17),
            OP_VC_SMALL to varcSmallBody(unknownId, 19),
            OP_VC_STR to varcStrBody(knownStringId, "ours-inline"),
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varcs":[""" +
            """{"kind":1,"id":$knownId,"value_kind":0,"val":18},""" +
            """{"kind":1,"id":$unknownId,"value_kind":0,"val":20}],""" +
            """"varcstrings":[""" +
            """{"kind":2,"id":$knownStringId,"value_kind":2,"str":"client-inline"}]}""" + "\n"
        val dir = writeClientStateSession("session-king-varc-names", packets, snapshot)
        val r = verify(dir)
        val md = TrustReportRenderer.markdown(r)
        val jsonText = TrustReportRenderer.jsonText(r)
        val knownMd = md.contains("kind1:mtxmgt_total_untradeable_bonds(4969)=17") &&
            md.contains("kind1:mtxmgt_total_untradeable_bonds(4969)=18")
        val fallbackMd = md.contains("kind1:varc_65000=19") && md.contains("kind1:varc_65000=20")
        val stringMd = md.contains("kind2:quickchat_phrase_obj2(147)=\"ours-inline\"") &&
            md.contains("kind2:quickchat_phrase_obj2(147)=\"client-inline\"")
        val jsonRawKnown = jsonText.contains("\"recordKind\": 1") &&
            jsonText.contains("\"varId\": 4969") &&
            jsonText.contains("\"gameval_type\": \"var_client\"") &&
            jsonText.contains("\"name\": \"mtxmgt_total_untradeable_bonds\"") &&
            jsonText.contains("\"valueKind\": 0") &&
            jsonText.contains("\"value\": 17")
        val jsonStringKnown = jsonText.contains("\"recordKind\": 2") &&
            jsonText.contains("\"varId\": 147") &&
            jsonText.contains("\"gameval_type\": \"var_client\"") &&
            jsonText.contains("\"name\": \"quickchat_phrase_obj2\"") &&
            jsonText.contains("\"valueKind\": 2") &&
            jsonText.contains("\"str\": \"ours-inline\"")
        val jsonFallback = jsonText.contains("\"varId\": 65000") &&
            jsonText.contains("\"display\": \"kind1:varc_65000=19\"") &&
            !jsonText.contains("\"name\": \"varc_65000\"")
        if (knownMd && fallbackMd && stringMd && jsonRawKnown && jsonStringKnown && jsonFallback)
            pass("varc naming: known id renders as mtxmgt_total_untradeable_bonds(4969), unknown id falls back to varc_65000, JSON keeps raw recordKind/varId + name/valueKind/value")
        else fail("varc naming: knownMd=$knownMd fallbackMd=$fallbackMd stringMd=$stringMd jsonRawKnown=$jsonRawKnown jsonStringKnown=$jsonStringKnown jsonFallback=$jsonFallback\nMD:\n$md\nJSON:\n$jsonText")
    }

    // 12a-1e. GENERIC GAMEVAL NAMING: var_player names use the same resolver as var_client, unknown
    //          ids fall back to <type>_<id>, JSON carries raw ids plus gameval metadata, and a missing
    //          dictionary root degrades to fallback display rather than failing report generation.
    run {
        val knownVarp = 0 // lastcastspell in re-resources/gamevals/var_player.json
        val unknownVarp = 65000
        val packets = listOf(
            OP_VS to varpSmallBody(knownVarp, 7),
            OP_VS to varpSmallBody(unknownVarp, 1),
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varps":{"$knownVarp":9,"$unknownVarp":2}}""" + "\n"
        val dir = writeClientStateSession("session-king-varp-names", packets, snapshot)
        val r = verify(dir)
        val md = TrustReportRenderer.markdown(r)
        val jsonText = TrustReportRenderer.jsonText(r)
        val knownMd = md.contains("var_player:lastcastspell(0): 7≠9")
        val fallbackMd = md.contains("var_player:var_player_65000: 1≠2")
        val jsonKnown = jsonText.contains("\"var_id\": 0") &&
            jsonText.contains("\"gameval_type\": \"var_player\"") &&
            jsonText.contains("\"name\": \"lastcastspell\"") &&
            jsonText.contains("\"display\": \"var_player:lastcastspell(0)\"") &&
            jsonText.contains("\"our_value\": 7") &&
            jsonText.contains("\"client_value\": 9")
        val jsonFallback = jsonText.contains("\"var_id\": 65000") &&
            jsonText.contains("\"display\": \"var_player:var_player_65000\"") &&
            !jsonText.contains("\"name\": \"var_player_65000\"")
        val missingRoot = File(tmp, "missing-gameval-root").apply { mkdirs() }
        val missing = GamevalNameResolver.load(missingRoot)
        val missingFallback = missing.display(GamevalNameResolver.TYPE_VAR_PLAYER, knownVarp) == "var_player:var_player_0" &&
            missing.varcKeyDisplay(ClientStateCrossCheck.VarcKey(ClientStateCrossCheck.VARC_RECORD_KIND_NUMERIC, 4969)) == "kind1:varc_4969"
        if (knownMd && fallbackMd && jsonKnown && jsonFallback && missingFallback)
            pass("generic gameval naming: var_player lastcastspell(0) renders with raw id, unknown varp falls back, JSON carries gameval metadata, missing dictionary root is non-fatal")
        else fail("generic gameval naming: knownMd=$knownMd fallbackMd=$fallbackMd jsonKnown=$jsonKnown jsonFallback=$jsonFallback missingFallback=$missingFallback\nMD:\n$md\nJSON:\n$jsonText")
    }

    // 12a-1f. CLIENT-DYNAMIC VARP ADVISORY: D&D timer varps are only downgraded from hard-fail when
    //          snapshot history proves the packet value matched first and later drifted client-side.
    run {
        val timerVarp = 3913
        val packets = listOf(Triple(OP_VL, 1_000L, varpLargeBody(timerVarp, 1_000)))
        val snapshot = buildString {
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":1100,"tick":10,"varps":{"$timerVarp":1000}}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":2100,"tick":11,"varps":{"$timerVarp":999}}""")
            appendLine("""{"kind":"state_snapshot","main_state":30,"mono_us":3000,"tick":12,"varps":{"$timerVarp":999}}""")
        }
        val dir = writeTimedClientStateSession("session-king-dynamic-varp-advisory", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val advisory = cc.varpAdvisory.singleOrNull()?.let {
            it.varId == timerVarp && it.ourValue == 1_000 && it.clientValue == 999 &&
                it.evidence.contains("tick10") && it.evidence.contains("tick11")
        } == true
        val noHardFail = !cc.hasFailure && cc.varpMismatches.isEmpty() &&
            r.reasons.none { it.contains("client-is-king: our decoded state does NOT", ignoreCase = true) }
        val jsonText = TrustReportRenderer.jsonText(r)
        val rendered = TrustReportRenderer.markdown(r)
        val reported = jsonText.contains("\"varp_advisory\"") &&
            jsonText.contains("\"reason\": \"client_dynamic\"") &&
            rendered.contains("client-local timer drift")
        if (advisory && noHardFail && reported)
            pass("client-dynamic VARP ADVISORY: var3913 first matches packet fold, then drifts in snapshots without another write; reported advisory, not FAIL")
        else fail("client-dynamic VARP ADVISORY: advisory=$advisory noHardFail=$noHardFail reported=$reported reasons=${r.reasons} cc=$cc\nJSON:\n$jsonText\nMD:\n$rendered")
    }

    // 12a-1f2. CLIENT-DYNAMIC VARP NO-PROOF: known timer ids still hard-fail if the capture lacks
    //           the match-then-drift evidence needed to prove client-local mutation.
    run {
        val timerVarp = 3913
        val packets = listOf(Triple(OP_VL, 1_000L, varpLargeBody(timerVarp, 1_000)))
        val snapshot = """{"kind":"state_snapshot","main_state":30,"mono_us":3000,"tick":12,"varps":{"$timerVarp":999}}""" + "\n"
        val dir = writeTimedClientStateSession("session-king-dynamic-varp-no-proof", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val mismatchFlagged = cc.varpMismatches.singleOrNull()?.let {
            it.varId == timerVarp && it.ourValue == 1_000 && it.clientValue == 999
        } == true
        val noAdvisory = cc.varpAdvisory.isEmpty()
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        if (mismatchFlagged && noAdvisory && verdictFail)
            pass("client-dynamic VARP NO-PROOF: timer varp without prior matching snapshot remains a hard client-king mismatch")
        else fail("client-dynamic VARP NO-PROOF: mismatchFlagged=$mismatchFlagged noAdvisory=$noAdvisory verdictFail=$verdictFail reasons=${r.reasons} cc=$cc")
    }

    // 12a-2. CONFIDENCE TAXONOMY: each tier is reachable through the shared assignment rule, and
    //         capture-observed only applies when round-trip proof passes.
    run {
        val tiers = listOf(
            ConfidenceAssigner.assign(clientVerified = true, binaryProven = false, captureObserved = false),
            ConfidenceAssigner.assign(clientVerified = false, binaryProven = true, captureObserved = true),
            ConfidenceAssigner.assign(clientVerified = false, binaryProven = false, captureObserved = true),
            ConfidenceAssigner.assign(clientVerified = false, binaryProven = false, captureObserved = false),
        )
        val ordered = tiers == listOf(
            Confidence.CLIENT_VERIFIED,
            Confidence.BINARY_PROVEN,
            Confidence.CAPTURE_OBSERVED,
            Confidence.HYPOTHESIS,
        )
        if (ordered) pass("confidence taxonomy: CLIENT_VERIFIED > BINARY_PROVEN > CAPTURE_OBSERVED > HYPOTHESIS assignment asserted")
        else fail("confidence taxonomy: got $tiers")
    }

    // 12a-3. ROUND-TRIP PROOF: a field-swapped op28 decoder would parse without throwing, but its
    //         re-encoded body differs from the original. Correct decoder round-trips the same body.
    run {
        val body = varpLargeBody(0x1234, 0x01020304)
        val correct = CapturePacketDecode.roundTripServer(codec, OP_VL, body)
        val wrongDidNotThrow = runCatching {
            val wrongId = ((body[0].toInt() and 0xFF) shl 8) or (body[1].toInt() and 0xFF)
            val wrongValue = ((body[2].toInt() and 0xFF) shl 24) or
                ((body[3].toInt() and 0xFF) shl 16) or
                ((body[4].toInt() and 0xFF) shl 8) or
                (body[5].toInt() and 0xFF)
            byteArrayOf(
                (wrongValue ushr 24).toByte(), (wrongValue ushr 16).toByte(),
                (wrongValue ushr 8).toByte(), wrongValue.toByte(),
                (wrongId ushr 8).toByte(), wrongId.toByte(),
            )
        }.getOrNull()
        val wrongCaught = wrongDidNotThrow != null && !wrongDidNotThrow.contentEquals(body)
        if (correct.roundTrips && wrongCaught)
            pass("round-trip proof: correct op28 body round-trips; field-swapped decoder would not throw but re-encodes different bytes")
        else fail("round-trip proof: correct=${correct.roundTrips}/${correct.failure} wrongCaught=$wrongCaught")
    }

    // 12b. RUN-ENERGY OPCODE (the RE resolution, §10.4): run energy is s2c op 0x0d (13), g1, 0..100
    //      RAW — NOT op 0x50/80 (which is the private-chat filter). Decode directly through the core
    //      ClientStateCrossCheck to prove: op 0x0d across the full 0..100 range becomes run energy
    //      (last-write-wins), and an op80 frame is NOT folded into run energy at all.
    run {
        // op 0x0d must BE the run-energy opcode.
        val opIsCorrect = ClientStateCrossCheck.OP_RUN_ENERGY == 13
        // Last-write-wins across 0, 55, 100 — proving the 0..100 RAW range is taken verbatim.
        val ramp = listOf(0, 55, 100).map { ClientStateCrossCheck.S2cPacket(13, runEnergyBody(it)) }
        val rampState = ClientStateCrossCheck.buildExpected(ramp, codec)
        val rampOk = rampState.runEnergy == 100 && rampState.decodeFailures == 0
        // A boundary value of 100 decodes to exactly 100 (no /10, no /2.55 scaling).
        val full = ClientStateCrossCheck.buildExpected(listOf(ClientStateCrossCheck.S2cPacket(13, runEnergyBody(100))), codec)
        val rawOk = full.runEnergy == 100
        // op80 alone must yield NO run energy (it is the chat filter, not a state-bearing run-energy op).
        val op80 = ClientStateCrossCheck.buildExpected(listOf(ClientStateCrossCheck.S2cPacket(80, runEnergyBody(1))), codec)
        val op80Ignored = op80.runEnergy == null && 80 !in ClientStateCrossCheck.STATE_BEARING_OPCODES
        if (opIsCorrect && rampOk && rawOk && op80Ignored)
            pass("run-energy opcode: op 0x0d (13) decodes g1 0..100 RAW as run energy (ramp 0→55→100 last-write=100); op 0x50/80 is NOT run energy (ignored)")
        else fail("run-energy opcode: opIsCorrect=$opIsCorrect rampOk=$rampOk (got ${rampState.runEnergy}) rawOk=$rawOk (got ${full.runEnergy}) op80Ignored=$op80Ignored (got ${op80.runEnergy})")
    }

    // 13. CLIENT-IS-KING MISMATCH: we decode var 100 = 7, but the client's snapshot holds var 100 = 9.
    //     The client is authoritative -> a varp MISMATCH is flagged and the verdict is FAIL. A second
    //     var the client holds (var 300) that we NEVER sent is a MISSED (dropped) state packet.
    run {
        val packets = listOf(
            OP_VS to varpSmallBody(100, 7),
            OP_US to updateStatBody(skillId = 1, level = 50, xp = 101_333),  // defence, level 50
        )
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varps":{"100":9,"300":42},""" +
            """"skills":[{"id":1,"level":50,"base":50,"xp":101333}],"run_energy":100,"run_weight":0}""" + "\n"
        val dir = writeClientStateSession("session-king-mismatch", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val mismatchFlagged = cc.varpMismatches.any { it.varId == 100 && it.ourValue == 7 && it.clientValue == 9 }
        val missedFlagged = cc.varpMissed.contains(300)
        val verdictFail = r.verdict == SessionTrustVerifier.Verdict.FAIL
        val reasonFail = r.reasons.any { it.contains("client-is-king: our decoded state does NOT", ignoreCase = true) }
        // The matching skill must NOT be dragged into the failure.
        val skillStillMatches = cc.skillMatches == 1 && cc.skillMismatches.isEmpty()
        if (mismatchFlagged && missedFlagged && verdictFail && reasonFail && skillStillMatches)
            pass("client-king MISMATCH: var100 ours=7≠client=9 flagged + var300 MISSED (dropped) -> FAIL (client authoritative); matching skill untouched")
        else fail("client-king MISMATCH: mismatchFlagged=$mismatchFlagged missedFlagged=$missedFlagged verdictFail=$verdictFail reasonFail=$reasonFail skillStillMatches=$skillStillMatches cc=$cc")
    }

    // 13b. CLIENT-IS-KING UNAVAILABLE: no `state-snapshots.jsonl` (the production session's reality) ->
    //      the cross-check reports unavailable and is INFO, never a FAIL (the headline gracefully
    //      degrades on older captures).
    run {
        val dir = writeClientStateSession("session-king-nosnap", listOf(OP_VS to varpSmallBody(1, 1)), "")
        File(dir, "state-snapshots.jsonl").delete()
        val r = verify(dir)
        val cc = r.clientCrossCheck
        val unavailable = !cc.available
        val infoNotFail = r.reasons.any { it.contains("client-is-king cross-check unavailable", ignoreCase = true) } &&
            r.reasons.none { it.contains("client-is-king: our decoded state does NOT", ignoreCase = true) }
        if (unavailable && infoNotFail)
            pass("client-king UNAVAILABLE: no state-snapshots.jsonl -> reported unavailable (INFO), never a FAIL")
        else fail("client-king UNAVAILABLE: available=${cc.available} reasons=${r.reasons}")
    }

    // 14. LOGIN s2c PHASE-SPLIT (§8a): the login s2c plane = Phase A PLAINTEXT handshake ++ Phase B
    //     ISAAC ciphertext (after SetupLoginCiphers). A single whole-plane mode fails; the phase-split
    //     validation must reproduce it as a plaintext prefix + an ISAAC suffix from keystream pos 0.
    //     The `login_cipher_ready` event marks the boundary.
    run {
        val loginSeedSend = intArrayOf(0x13572468, 0x0bad0d0e, 0x55aa55aa, 0x0f1e2d3c)
        fun plus50(s: IntArray) = IntArray(s.size) { s[it] + CaptureDeframer.S2C_ISAAC_DELTA }
        fun hex(s: IntArray) = s.joinToString(",") { "0x%08x".format(it) }
        // Small login s2c population (fixed-size ops we know) split into Phase A (plaintext) + Phase B
        // (ISAAC). Use distinct unique bodies so the body-match mapping is unambiguous.
        val popA = smallPop("s2c").take(3)   // Phase A handshake frames (plaintext opcodes on the wire)
        val popB = smallPop("s2c").drop(3)   // Phase B frames (ISAAC-ciphered opcodes from ks pos 0)

        val dir = File(tmp, "session-login-phase-split").apply { mkdirs() }
        // framed-s2c for conn=login: Phase A then Phase B, in order.
        File(dir, "framed-s2c.jsonl").bufferedWriter().use { w ->
            for ((op, len, bodyBytes) in popA + popB) {
                w.appendLine("""{"plane":"framed","dir":"s2c","conn":"login","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
            }
        }
        File(dir, "framed-c2s.jsonl").writeText("")
        // socket: Phase A wire is PLAINTEXT (no cipher), Phase B wire is ISAAC from keystream pos 0.
        val wirePhaseA = buildWire("s2c", popA, encipher = null)
        val wirePhaseB = buildWire("s2c", popB, isaacFor(loginSeedSend))
        val phaseAlen = wirePhaseA.size
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            val combined = wirePhaseA + wirePhaseB
            w.appendLine("""{"plane":"socket","dir":"s2c","conn":"login","len":${combined.size},"body":"${b64.encodeToString(combined)}"}""")
        }
        // The recorder's boundary marker: ciphers became active after `phaseAlen` raw login-s2c bytes
        // (the authoritative Fill-plane byte count, per the dylib contract field `login_s2c_bytes`).
        File(dir, "events.jsonl").writeText(
            """{"plane":"event","kind":"login_cipher_ready","login_s2c_bytes":$phaseAlen}""" + "\n"
        )
        File(dir, "session.json").writeText("""{"session_id":"login-phase-split","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText(
            "login-c2s ISAAC keys (hex): ${hex(loginSeedSend)}\n" +
                "login-s2c ISAAC keys (hex): ${hex(plus50(loginSeedSend))}\n"
        )

        val line = cross.independentDeframeSummary(dir)
        val phaseSplitOk = line.contains("[login/s2c] OK") && line.contains("PHASE-SPLIT", ignoreCase = true)
        if (phaseSplitOk)
            pass("login-phase-split: login s2c reproduced as PLAINTEXT prefix ++ ISAAC suffix (phase-split via login_cipher_ready) — both phases AGREE")
        else fail("login-phase-split: cross line was:\n$line")
    }

    // 15. C2S PHASE-SPLIT (§8a, the c2s generalization): the c2s socket is captured at
    //     `ClientStream::Write` POST opcode-encryption, so EVERY c2s plane is two-phase — a PLAINTEXT
    //     login-handshake prefix (op14 + op19/op16 RSA blocks) ++ Phase B ISAAC ciphertext that
    //     RESTARTS at keystream position 0. There is NO c2s phase marker event, so the boundary k must
    //     be AUTO-DETECTED. Synthesize BOTH game/c2s and login/c2s as plaintext-prefix ++ ISAAC-from-0
    //     and assert both AGREE via an auto-detected PHASE-SPLIT (the exact production-capture shape:
    //     game/c2s split k=3, login/c2s split k=2). A whole-plane single mode cannot reproduce these.
    run {
        val gameSend = intArrayOf(0x8c61aa2c.toInt(), 0x7fe0a26d, 0xe2a81f6e.toInt(), 0x69c35fa2)
        val loginSend = intArrayOf(0x13572468, 0x0bad0d0e, 0x55aa55aa, 0x0f1e2d3c)
        fun plus50(s: IntArray) = IntArray(s.size) { s[it] + CaptureDeframer.S2C_ISAAC_DELTA }
        fun hex(s: IntArray) = s.joinToString(",") { "0x%08x".format(it) }

        // c2s populations: a plaintext Phase-A prefix of `kSplit` frames ++ a Phase-B ISAAC suffix.
        // Distinct unique bodies keep the body-match mapping unambiguous. (k=3 game, k=2 login.)
        val popAll = smallPop("c2s")
        require(popAll.size >= 4) { "need >=4 c2s ops to model a non-trivial phase split" }
        val conns = listOf(Triple("game", gameSend, 3), Triple("login", loginSend, 2))

        val dir = File(tmp, "session-c2s-phase-split").apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").writeText("")
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            for ((conn, _, _) in conns) for ((op, len, bodyBytes) in popAll) {
                w.appendLine("""{"plane":"framed","dir":"c2s","conn":"$conn","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
            }
        }
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            for ((conn, send, kSplit) in conns) {
                val popA = popAll.take(kSplit)                 // PLAINTEXT login-handshake prefix
                val popB = popAll.drop(kSplit)                 // Phase B ISAAC ciphertext from ks pos 0
                val combined = buildWire("c2s", popA, encipher = null) +
                    buildWire("c2s", popB, isaacFor(send))     // c2s SEND seed -> isaacFor uses raw keys
                w.appendLine("""{"plane":"socket","dir":"c2s","conn":"$conn","len":${combined.size},"body":"${b64.encodeToString(combined)}"}""")
            }
        }
        File(dir, "session.json").writeText("""{"session_id":"c2s-phase-split","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        // Per-(conn,dir) seeds, used VERBATIM. NO c2s phase marker — the split is auto-detected.
        File(dir, "isaac-keys.txt").writeText(
            "game-c2s ISAAC keys (hex): ${hex(gameSend)}\n" +
                "game-s2c ISAAC keys (hex): ${hex(plus50(gameSend))}\n" +
                "login-c2s ISAAC keys (hex): ${hex(loginSend)}\n" +
                "login-s2c ISAAC keys (hex): ${hex(plus50(loginSend))}\n"
        )

        val line = cross.independentDeframeSummary(dir)
        val gameOk = line.contains("[game/c2s] OK") && line.contains("PHASE-SPLIT", ignoreCase = true)
        val loginOk = line.contains("[login/c2s] OK") && line.contains("PHASE-SPLIT", ignoreCase = true)
        // The c2s split has no event, so the verdict must say it was auto-detected.
        val autoDetected = line.contains("split auto-detected", ignoreCase = true)
        // The auto-detected boundaries must be the true k=3 (game) and k=2 (login).
        val gameK3 = line.contains("first 3 frames PLAINTEXT")
        val loginK2 = line.contains("first 2 frames PLAINTEXT")
        val noDesync = !line.contains("could NOT reproduce", ignoreCase = true)
        if (gameOk && loginOk && autoDetected && gameK3 && loginK2 && noDesync)
            pass("c2s-phase-split: game/c2s (k=3) AND login/c2s (k=2) reproduced as PLAINTEXT prefix ++ ISAAC-from-0 suffix via AUTO-DETECTED phase-split — both AGREE")
        else fail("c2s-phase-split: gameOk=$gameOk loginOk=$loginOk autoDetected=$autoDetected gameK3=$gameK3 loginK2=$loginK2 noDesync=$noDesync\n$line")
    }

    // 15b. C2S PHASE-SPLIT NEGATIVE CONTROL: with the WRONG (swapped) per-(conn,dir) seed, the
    //      auto-detected phase-split must NOT manufacture a false AGREE — no split point can make the
    //      ISAAC suffix decode under a seed that did not encipher it, so it must fall through to the
    //      honest seed-mismatch diagnostic. Proves the auto-detection scan cannot false-positive.
    run {
        val gameSend = intArrayOf(0x8c61aa2c.toInt(), 0x7fe0a26d, 0xe2a81f6e.toInt(), 0x69c35fa2)
        val loginSend = intArrayOf(0x13572468, 0x0bad0d0e, 0x55aa55aa, 0x0f1e2d3c)
        fun plus50(s: IntArray) = IntArray(s.size) { s[it] + CaptureDeframer.S2C_ISAAC_DELTA }
        fun hex(s: IntArray) = s.joinToString(",") { "0x%08x".format(it) }
        val popAll = smallPop("c2s")
        val kSplit = 3

        val dir = File(tmp, "session-c2s-phase-split-wrong").apply { mkdirs() }
        File(dir, "framed-s2c.jsonl").writeText("")
        File(dir, "framed-c2s.jsonl").bufferedWriter().use { w ->
            for ((op, len, bodyBytes) in popAll) {
                w.appendLine("""{"plane":"framed","dir":"c2s","conn":"game","op":$op,"len":$len,"body":"${b64.encodeToString(bodyBytes)}"}""")
            }
        }
        // game/c2s wire is enciphered with gameSend, but the file labels game-c2s as loginSend.
        File(dir, "socket.jsonl").bufferedWriter().use { w ->
            val combined = buildWire("c2s", popAll.take(kSplit), encipher = null) +
                buildWire("c2s", popAll.drop(kSplit), isaacFor(gameSend))
            w.appendLine("""{"plane":"socket","dir":"c2s","conn":"game","len":${combined.size},"body":"${b64.encodeToString(combined)}"}""")
        }
        File(dir, "session.json").writeText("""{"session_id":"c2s-phase-split-wrong","build":"RS2Engine-948-NXT-5","server_mode":"production"}""")
        File(dir, "isaac-keys.txt").writeText(
            "game-c2s ISAAC keys (hex): ${hex(loginSend)}\n" +                 // WRONG seed for game c2s
                "game-s2c ISAAC keys (hex): ${hex(plus50(loginSend))}\n"
        )

        val line = cross.independentDeframeSummary(dir)
        val noFalseAgree = !line.contains("[game/c2s] OK")
        val diagnosed = line.contains("captured seed is not the cipher seed", ignoreCase = true)
        if (noFalseAgree && diagnosed)
            pass("c2s-phase-split (wrong seed): the auto-detected scan produces NO false AGREE and hits the honest seed-mismatch diagnostic (scan cannot false-positive)")
        else fail("c2s-phase-split (wrong seed): noFalseAgree=$noFalseAgree diagnosed=$diagnosed\n$line")
    }

    println("=".repeat(70))
    println("Self-test: $passed passed, $failed failed")
    tmp.deleteRecursively()
    if (failed > 0) exitProcess(1)
}

// State-bearing s2c opcodes used by the client-is-king self-tests (mirrors ClientStateCrossCheck).
private const val OP_VS = 61   // VARP_SMALL
private const val OP_VL = 28   // VARP_LARGE
private const val OP_VLONG = 147 // VARP_LONG
private const val OP_RESET_VARCACHE = 5 // RESET_CLIENT_VARCACHE
private const val OP_VC_SMALL = 47 // CLIENT_SETVARC_SMALL
private const val OP_VC_LARGE = 64 // CLIENT_SETVARC_LARGE
private const val OP_VC_STR = 92 // CLIENT_SETVARC_STR
private const val OP_VC_STR_LARGE = 116 // CLIENT_SETVARC_STR_LARGE
private const val OP_VC_LONG = 196 // CLIENT_SETVARC_LONG (metadata name UNKNOWN_196 in register948)
private const val OP_VCBIT_SMALL = 48 // CLIENT_SETVARCBIT_SMALL (deferred for client verification)
private const val OP_VCBIT_LARGE = 69 // CLIENT_SETVARCBIT_LARGE (deferred for client verification)
private const val OP_US = 44   // UPDATE_STAT
private const val OP_RE = 13   // UPDATE_RUNENERGY (s2c op 0x0d, g1, 0..100 RAW; NOT op 0x50/80 = chat filter)
private const val OP_INV_FULL = 85 // UPDATE_INV_FULL
private const val OP_INV_PARTIAL = 121 // UPDATE_INV_PARTIAL
private const val OP_REBUILD_NORMAL_SIMPLE = 81 // REBUILD_NORMAL_SIMPLE (GPI prefix initializer)
private const val OP_PLAYER_INFO = 22 // PLAYER_INFO
private const val OP_NPC_INFO = 52 // NPC_INFO
