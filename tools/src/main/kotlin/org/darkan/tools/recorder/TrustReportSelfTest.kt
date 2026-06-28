package org.darkan.tools.recorder

import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.CaptureDeframer
import org.darkan.core.net.recorder.ClientStateCrossCheck
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
    fun updateStatBody(skillId: Int, level: Int, xp: Int): ByteArray = byteArrayOf(                   // xp g4 LE, level g1, skillId byteInverse
        xp.toByte(), (xp ushr 8).toByte(), (xp ushr 16).toByte(), (xp ushr 24).toByte(),
        level.toByte(), ((-skillId) and 0xFF).toByte(),
    )
    fun runEnergyBody(energy: Int): ByteArray = byteArrayOf(energy.toByte())                          // g1

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
            OP_US to updateStatBody(skillId = 6, level = 99, xp = 13_034_431),  // magic, level 99
            OP_RE to runEnergyBody(100),                                        // op 0x0d, 0..100 RAW
            80 to runEnergyBody(1),   // op 0x50 chat-filter "Friends" — MUST NOT be read as run energy
        )
        // base for xp 13_034_431 is level 99 (the xp→level table); the snapshot mirrors that.
        val snapshot = """{"kind":"state_snapshot","main_state":30,"varps":{"100":7,"200":123456},""" +
            """"skills":[{"id":6,"level":99,"base":99,"xp":13034431}],"run_energy":100,"run_weight":12}""" + "\n"
        val dir = writeClientStateSession("session-king-agree", packets, snapshot)
        val r = verify(dir)
        val cc = r.clientCrossCheck
        // Run energy decoded from op 0x0d must equal the client snapshot's 100 (proves op 0x0d is the
        // run-energy source); the op80 frame must NOT have overwritten it with the chat-filter 1.
        val energyOk = cc.scalarMismatches.none { it.field == "runEnergy" }
        val ok = cc.available && !cc.hasFailure && cc.varpMatches == 2 && cc.skillMatches == 1 &&
            cc.varpMismatches.isEmpty() && cc.varpMissed.isEmpty() && cc.scalarMismatches.isEmpty()
        // The cross-check must NOT contribute a FAIL (a missing-socket WARN is fine).
        val noKingFail = r.reasons.none { it.contains("client-is-king: our decoded state does NOT", ignoreCase = true) }
        if (ok && energyOk && noKingFail)
            pass("client-king AGREE: decoded varps(2)+skills(1)+runEnergy(op0x0d=100, NOT op80) match the synthetic client snapshot (no failure)")
        else fail("client-king AGREE: available=${cc.available} hasFailure=${cc.hasFailure} varpMatch=${cc.varpMatches} skillMatch=${cc.skillMatches} mism=${cc.varpMismatches} missed=${cc.varpMissed} scalar=${cc.scalarMismatches} energyOk=$energyOk noKingFail=$noKingFail")
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
private const val OP_US = 44   // UPDATE_STAT
private const val OP_RE = 13   // UPDATE_RUNENERGY (s2c op 0x0d, g1, 0..100 RAW; NOT op 0x50/80 = chat filter)
