package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import kotlin.system.exitProcess

/**
 * `trustReport` entry point — verify a recorder session directory and emit the trust report.
 *
 * Proves a production capture is genuinely complete and coherent (byte-accounting), that its bodies
 * decode through the live core codecs (clean-decode), and that it looks like a real RS3 world-entry
 * (sanity vs the documented 948-5 session). Writes:
 *  - `<session>/trust-report.md`   — human-readable, leads with the PASS / WARN / FAIL verdict.
 *  - `<session>/trust-report.json` — machine-readable, same data.
 *
 * Run:
 *   ./gradlew :tools:trustReport -Pargs="<session-dir> [--known <known-opcodes.txt>] [--no-crossvalidate]"
 */
fun main(args: Array<String>) {
    var sessionDir: String? = null
    var knownPath: String? = null
    var crossValidate = true

    var i = 0
    while (i < args.size) {
        when (val a = args[i]) {
            "--known" -> knownPath = args[++i]
            "--no-crossvalidate" -> crossValidate = false
            "--help", "-h" -> { printTrustUsage(); return }
            else -> {
                if (a.startsWith("--")) { System.err.println("Unknown arg: $a"); printTrustUsage(); exitProcess(2) }
                sessionDir = a
            }
        }
        i++
    }

    val session = File(sessionDir ?: run { printTrustUsage(); exitProcess(2) })
    if (!session.isDirectory) {
        System.err.println("Not a directory: ${session.absolutePath}")
        exitProcess(1)
    }

    val codec = register948()
    val known = KnownOpcodes.load(File(knownPath ?: "re-resources/docs/net/recorder-known-opcodes.txt"))

    println("==================== Trust report ====================")
    println("  session : ${session.absolutePath}")

    val crossLine = if (crossValidate) {
        CrossValidator(codec, known).independentDeframeSummary(session)
    } else {
        "skipped (--no-crossvalidate)"
    }

    // CLIENT-IS-KING: decode our state-bearing s2c packets, fold them into the expected client state,
    // and compare against the client's OWN final snapshot. This is the headline trust input.
    val clientCross = ClientStateCrossChecker(codec).crossCheck(session)

    val report = SessionTrustVerifier(codec, known).verify(session, crossLine, clientCross)

    val md = File(session, "trust-report.md")
    val jsonFile = File(session, "trust-report.json")
    md.writeText(TrustReportRenderer.markdown(report))
    jsonFile.writeText(TrustReportRenderer.jsonText(report))

    println()
    println(TrustReportRenderer.console(report))
    println()
    println("  wrote ${md.absolutePath}")
    println("  wrote ${jsonFile.absolutePath}")

    // Non-zero exit on FAIL so this can gate CI / a capture pipeline.
    if (report.verdict == SessionTrustVerifier.Verdict.FAIL) exitProcess(3)
}

/** Renders a [SessionTrustVerifier.Report] to markdown, JSON, and a short console banner. */
object TrustReportRenderer {

    private val json = Json { prettyPrint = true }

    fun console(r: SessionTrustVerifier.Report): String {
        val sb = StringBuilder()
        sb.appendLine("  VERDICT: ${r.verdict}")
        for (reason in r.reasons) sb.appendLine("    - $reason")
        val cc = r.clientCrossCheck
        val ccLine = when {
            !cc.available -> "client-is-king: UNAVAILABLE (no snapshot — run a fresh capture)"
            cc.hasFailure -> "client-is-king: FAIL (${cc.varpMismatches.size} varp + ${cc.skillMismatches.size} skill mismatch, ${cc.varpMissed.size} varp missed)"
            else -> "client-is-king: OK (${cc.varpMatches} varps + ${cc.skillMatches} skills match the client's own state)"
        }
        sb.appendLine("  $ccLine")
        for (a in r.byteAccounts) {
            val cov = when {
                !a.socketPresent -> "coverage n/a (no socket plane)"
                a.socketUncaptured -> "coverage n/a (socket plane uncaptured: ${a.socketTotal}B socket)"
                else -> "coverage ${fmtPct(a.coveragePct)}"
            }
            sb.appendLine(
                "  [${a.conn}/${a.dir}] $cov " +
                    "(${a.framedFootprint}B framed / ${a.accountableSocket}B accountable; " +
                    "${a.framedPackets} pkts; handshake≈${a.handshakeOverhead}B)"
            )
        }
        return sb.toString().trimEnd()
    }

    fun markdown(r: SessionTrustVerifier.Report): String {
        val sb = StringBuilder()
        sb.appendLine("# Capture trust report")
        sb.appendLine()
        sb.appendLine("- **Session**: `${r.sessionId}`")
        sb.appendLine("- **Build**: `${r.build}`")
        sb.appendLine("- **Server mode**: `${r.serverMode}`")
        sb.appendLine()
        sb.appendLine("## Verdict: ${verdictBadge(r.verdict)}")
        sb.appendLine()
        for (reason in r.reasons) sb.appendLine("- $reason")
        sb.appendLine()

        clientCrossSection(sb, r)

        sb.appendLine("## 1. Byte-accounting (completeness proof)")
        sb.appendLine()
        sb.appendLine("On-wire footprint = opcode bytes (1 if <128 else 2) + size-prefix (0 fixed / 1 varByte / 2 varShort, from the live codec) + body length (the client's own resolved size). Coverage is framed footprint over the socket-plane bytes that remain after a login-handshake estimate. 100% means provably no packet was dropped. Only the game-protocol planes (`login` / `game`) are accounted; the `conn=unknown` cache/JS5/HTTP download is excluded and reported in §1a. When the socket plane is absent — or present but captured a tiny fraction of the framed footprint (it simply did not record that direction) — byte-accounting is _unavailable_, not a failure: completeness is then carried by clean-decode.")
        sb.appendLine()
        sb.appendLine("| conn | dir | pkts | framed footprint | socket total | handshake est. | accountable | coverage | residual |")
        sb.appendLine("|---|---|--:|--:|--:|--:|--:|--:|--:|")
        for (a in r.byteAccounts) {
            val socket = if (a.socketPresent) "${a.socketTotal}B" else "_(none)_"
            val cov = when {
                !a.socketPresent -> "_n/a (no socket)_"
                a.socketUncaptured -> "_n/a (uncaptured)_"
                else -> fmtPct(a.coveragePct)
            }
            val residual = if (a.socketPresent && !a.socketUncaptured) "${a.residualBytes}B" else "_n/a_"
            sb.appendLine(
                "| ${a.conn} | ${a.dir} | ${a.framedPackets} | ${a.framedFootprint}B | $socket | " +
                    "${a.handshakeOverhead}B | ${a.accountableSocket}B | $cov | $residual |"
            )
        }
        sb.appendLine()

        sb.appendLine("### 1a. Non-game socket volume (excluded from accounting)")
        sb.appendLine()
        if (r.nonGameSocket.isEmpty()) {
            sb.appendLine("None. Every socket-plane byte belonged to a game-protocol conn.")
        } else {
            sb.appendLine("Cache-asset / JS5 / HTTP traffic on the same socket plane (e.g. `conn=unknown`). Counted here for context only — it is a different protocol and never enters the game byte-accounting above.")
            sb.appendLine()
            sb.appendLine("| conn | dir | entries | bytes |")
            sb.appendLine("|---|---|--:|--:|")
            for (n in r.nonGameSocket.sortedByDescending { it.bytes }) {
                sb.appendLine("| ${n.conn} | ${n.dir} | ${n.entries} | ${n.bytes}B |")
            }
        }
        sb.appendLine()

        sb.appendLine("## 2. Clean-decode rate")
        sb.appendLine()
        sb.appendLine("Share of decodable framed packets that round-trip through the live core codecs without throwing. c2s `xtea_body` packets are encrypted and not decodable offline — counted separately, never as failures.")
        sb.appendLine()
        sb.appendLine("| conn | dir | decodable | clean | failed | encrypted (skipped) | clean rate | failing opcodes |")
        sb.appendLine("|---|---|--:|--:|--:|--:|--:|---|")
        for (d in r.decodeStats) {
            val failing = if (d.failingOpcodes.isEmpty()) "—"
            else d.failingOpcodes.entries.joinToString(", ") { "op${it.key}×${it.value}" }
            sb.appendLine(
                "| ${d.conn} | ${d.dir} | ${d.decodable} | ${d.cleanDecoded} | ${d.failed} | " +
                    "${d.encryptedSkipped} | ${fmtPct(d.cleanRatePct)} | $failing |"
            )
        }
        sb.appendLine()

        sb.appendLine("## 3. Framing-coherence desyncs")
        sb.appendLine()
        if (r.desyncs.isEmpty()) {
            sb.appendLine("None. Every packet's captured length is consistent with its codec size class.")
        } else {
            sb.appendLine("A captured length that cannot fit the opcode's size class means the codec and the client's own resolved size disagree — either a codec mislabel (the op78/op216/op22/op98 class claude-re corrected) or a dylib `LAST_OPCODE` mis-resolution where a different opcode's body was tagged under this one. This is a WARN, not a FAIL: when the codec size is authoritative against the binary prot table, the disagreement is a dylib artifact rather than a codec fault, and clean-decode is the arbiter of body correctness. Offset is into the post-handshake prot stream for that conn+dir.")
            sb.appendLine()
            sb.appendLine("| conn | dir | frame # | stream offset | opcode | name | detail |")
            sb.appendLine("|---|---|--:|--:|--:|---|---|")
            for (g in r.desyncs.take(MAX_DESYNC_ROWS)) {
                sb.appendLine("| ${g.conn} | ${g.dir} | ${g.frameIndex} | ${g.streamOffset}B | ${g.opcode} | ${g.name} | ${g.detail} |")
            }
            if (r.desyncs.size > MAX_DESYNC_ROWS) sb.appendLine("\n_(${r.desyncs.size - MAX_DESYNC_ROWS} more — see trust-report.json)_")
        }
        sb.appendLine()

        sb.appendLine("### 3a. Double-counted frames (invented-frame detector)")
        sb.appendLine()
        if (r.doubleCounts.isEmpty()) {
            sb.appendLine("None. No plane has a large fraction of adjacent byte-identical multi-byte frames — the signature of a framing hook emitting each packet twice. (Legitimately-repeating telemetry/input, e.g. idle-mouse op52, does not count.)")
        } else {
            sb.appendLine("A plane where a large FRACTION of frames are adjacent byte-identical duplicates — direct evidence of systemic double-counting (a hook that emitted each packet twice). The fraction (not any single run) is the discriminator, so legitimately-repeating telemetry does not trip it. This is a hard FAIL.")
            sb.appendLine()
            sb.appendLine("| conn | dir | duplicate fraction | duplicate frames | total frames | worst run |")
            sb.appendLine("|---|---|--:|--:|--:|---|")
            for (d in r.doubleCounts.sortedByDescending { it.duplicateFraction }.take(MAX_DESYNC_ROWS)) {
                sb.appendLine("| ${d.conn} | ${d.dir} | ${fmtPct(d.duplicateFraction * 100)} | ${d.duplicateFrames} | ${d.totalFrames} | op${d.worstOpcode} ${d.worstName} ×${d.worstRun} |")
            }
        }
        sb.appendLine()

        sb.appendLine("## 4. Sanity vs documented production (948-5 world-entry)")
        sb.appendLine()
        if (r.sanityDeltas.isEmpty()) {
            sb.appendLine("No notable per-opcode deviations from the documented session.")
        } else {
            sb.appendLine("Per-opcode counts compared to `claude-re/findings/12-opcode-map.md`. Count drift is expected for a different account or session length (advisory); `missing` and `unexpected-opcode` floods are stronger signals.")
            sb.appendLine()
            sb.appendLine("| conn | dir | opcode | name | actual | expected | kind | note |")
            sb.appendLine("|---|---|--:|---|--:|--:|---|---|")
            for (s in r.sanityDeltas.take(MAX_SANITY_ROWS)) {
                sb.appendLine("| ${s.conn} | ${s.dir} | ${s.opcode} | ${s.name} | ${s.actualCount} | ${s.expectedCount} | ${s.kind} | ${s.note} |")
            }
            if (r.sanityDeltas.size > MAX_SANITY_ROWS) sb.appendLine("\n_(${r.sanityDeltas.size - MAX_SANITY_ROWS} more — see trust-report.json)_")
        }
        sb.appendLine()

        sb.appendLine("## 5. Independent ISAAC deframe (cross-validation)")
        sb.appendLine()
        sb.appendLine(r.crossValidation)
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine("_Byte-accounting, clean-decode, and sanity stand on their own; the independent deframe is a bonus check when a real ISAAC construction seed was captured._")
        return sb.toString()
    }

    /**
     * The headline CLIENT-IS-KING section: a field-by-field comparison of our decoded state against
     * the client's OWN final state snapshot (ground truth). Rendered as section 0 because it is the
     * strongest correctness proof — byte-accounting proves we framed every byte, clean-decode proves
     * our decoders don't throw, but only THIS proves the values our decoders produce match what the
     * client committed.
     */
    private fun clientCrossSection(sb: StringBuilder, r: SessionTrustVerifier.Report) {
        val cc = r.clientCrossCheck
        sb.appendLine("## 0. Client-is-king cross-check (the headline correctness proof)")
        sb.appendLine()
        sb.appendLine("Our decoded state-bearing s2c packets (varps, skills, run energy) are folded into the EXPECTED client state and compared field-by-field to the client's OWN final snapshot (`state-snapshots.jsonl`, last line = authoritative). **The client is the ground truth** — a value our decode produces that disagrees with the client's committed state means OUR decode is wrong, and a var the client holds that we never decoded means a state-bearing packet was dropped. Either is a hard FAIL. This catches a decoder that is byte-clean (round-trips, never throws) yet produces the wrong integer — which the other proofs cannot.")
        sb.appendLine()
        if (!cc.available) {
            sb.appendLine("**Unavailable** — ${cc.reason}. This capture has no client state snapshot (older recorder, before `state-snapshots.jsonl`). Run a fresh capture to enable the cross-check. This is NOT a failure; the other three proofs stand on their own.")
            sb.appendLine()
            return
        }
        val status = if (cc.hasFailure) "FAIL — decoded state does not reproduce the client's committed state" else "OK — decoded state matches the client's committed state"
        sb.appendLine("**Result: $status**")
        sb.appendLine()
        sb.appendLine("| family | match | mismatch | missed | extra | detail |")
        sb.appendLine("|---|--:|--:|--:|--:|---|")
        val varpDetail = if (cc.varpMismatches.isEmpty() && cc.varpMissed.isEmpty()) "—"
        else (cc.varpMismatches.take(8).joinToString { "var${it.varId}: ${it.ourValue}≠${it.clientValue}" } +
            (if (cc.varpMissed.isNotEmpty()) " missed=" + cc.varpMissed.take(8).joinToString { "var$it" } else "")).trim()
        sb.appendLine("| VARPS | ${cc.varpMatches} | ${cc.varpMismatches.size} | ${cc.varpMissed.size} | ${cc.varpExtra.size} | $varpDetail |")
        val skillDetail = if (cc.skillMismatches.isEmpty() && cc.skillMissed.isEmpty()) "—"
        else cc.skillMismatches.take(8).joinToString { "skill${it.skillId}.${it.field}: ${it.ourValue}≠${it.clientValue}" }
        sb.appendLine("| SKILLS | ${cc.skillMatches} | ${cc.skillMismatches.size} | ${cc.skillMissed.size} | — | $skillDetail |")
        val scalarDetail = if (cc.scalarMismatches.isEmpty()) "—"
        else cc.scalarMismatches.joinToString { "${it.field}: ${it.ourValue}≠${it.clientValue}" }
        sb.appendLine("| SCALARS (run energy/weight, tile) | — | ${cc.scalarMismatches.size} | — | — | $scalarDetail |")
        sb.appendLine()
        if (cc.varpExtra.isNotEmpty()) {
            sb.appendLine("> ${cc.varpExtra.size} varp(s) we decoded are not in the client's final snapshot — advisory only: the client legitimately overwrites/resets vars after the last packet, so an extra is not a decode fault.")
            sb.appendLine()
        }
        if (cc.decodeFailures > 0) {
            sb.appendLine("> ${cc.decodeFailures} state-bearing body(ies) failed to decode against the documented wire format (short/garbled body).")
            sb.appendLine()
        }
    }

    fun jsonText(r: SessionTrustVerifier.Report): String = json.encodeToString(JsonElement.serializer(), toJson(r))

    private fun toJson(r: SessionTrustVerifier.Report): JsonObject {
        fun obj(vararg pairs: Pair<String, JsonElement>) = JsonObject(linkedMapOf(*pairs))
        fun num(n: Number) = JsonPrimitive(n)
        fun str(s: String) = JsonPrimitive(s)

        val byteAccounts = JsonArray(r.byteAccounts.map { a ->
            obj(
                "conn" to str(a.conn), "dir" to str(a.dir),
                "framed_packets" to num(a.framedPackets),
                "framed_footprint_bytes" to num(a.framedFootprint),
                "socket_total_bytes" to num(a.socketTotal),
                "socket_present" to JsonPrimitive(a.socketPresent),
                "socket_uncaptured" to JsonPrimitive(a.socketUncaptured),
                "handshake_overhead_bytes" to num(a.handshakeOverhead),
                "accountable_socket_bytes" to num(a.accountableSocket),
                "coverage_pct" to num(a.coveragePct),
                "residual_bytes" to num(a.residualBytes),
            )
        })
        val decodeStats = JsonArray(r.decodeStats.map { d ->
            obj(
                "conn" to str(d.conn), "dir" to str(d.dir),
                "decodable" to num(d.decodable), "clean_decoded" to num(d.cleanDecoded),
                "failed" to num(d.failed), "encrypted_skipped" to num(d.encryptedSkipped),
                "clean_rate_pct" to num(d.cleanRatePct),
                "failing_opcodes" to JsonObject(d.failingOpcodes.entries.associate { it.key.toString() to num(it.value) as JsonElement }),
            )
        })
        val desyncs = JsonArray(r.desyncs.map { g ->
            obj(
                "conn" to str(g.conn), "dir" to str(g.dir),
                "frame_index" to num(g.frameIndex), "stream_offset_bytes" to num(g.streamOffset),
                "opcode" to num(g.opcode), "name" to str(g.name), "detail" to str(g.detail),
            )
        })
        val nonGameSocket = JsonArray(r.nonGameSocket.map { n ->
            obj(
                "conn" to str(n.conn), "dir" to str(n.dir),
                "entries" to num(n.entries), "bytes" to num(n.bytes),
            )
        })
        val doubleCounts = JsonArray(r.doubleCounts.map { d ->
            obj(
                "conn" to str(d.conn), "dir" to str(d.dir),
                "duplicate_fraction" to num(d.duplicateFraction),
                "duplicate_frames" to num(d.duplicateFrames), "total_frames" to num(d.totalFrames),
                "worst_opcode" to num(d.worstOpcode), "worst_name" to str(d.worstName), "worst_run" to num(d.worstRun),
            )
        })
        val sanity = JsonArray(r.sanityDeltas.map { s ->
            obj(
                "conn" to str(s.conn), "dir" to str(s.dir),
                "opcode" to num(s.opcode), "name" to str(s.name),
                "actual_count" to num(s.actualCount), "expected_count" to num(s.expectedCount),
                "kind" to str(s.kind), "note" to str(s.note),
            )
        })
        val cc = r.clientCrossCheck
        val clientCrossJson = obj(
            "available" to JsonPrimitive(cc.available),
            "reason" to str(cc.reason),
            "has_failure" to JsonPrimitive(cc.hasFailure),
            "varp_matches" to num(cc.varpMatches),
            "varp_mismatches" to JsonArray(cc.varpMismatches.map {
                obj("var_id" to num(it.varId), "our_value" to num(it.ourValue), "client_value" to num(it.clientValue))
            }),
            "varp_missed" to JsonArray(cc.varpMissed.map { num(it) }),
            "varp_extra" to JsonArray(cc.varpExtra.map { num(it) }),
            "skill_matches" to num(cc.skillMatches),
            "skill_mismatches" to JsonArray(cc.skillMismatches.map {
                obj("skill_id" to num(it.skillId), "field" to str(it.field), "our_value" to num(it.ourValue), "client_value" to num(it.clientValue))
            }),
            "skill_missed" to JsonArray(cc.skillMissed.map { num(it) }),
            "scalar_mismatches" to JsonArray(cc.scalarMismatches.map {
                obj("field" to str(it.field), "our_value" to str(it.ourValue), "client_value" to str(it.clientValue))
            }),
            "decode_failures" to num(cc.decodeFailures),
        )
        return obj(
            "session_id" to str(r.sessionId),
            "build" to str(r.build),
            "server_mode" to str(r.serverMode),
            "verdict" to str(r.verdict.name),
            "reasons" to JsonArray(r.reasons.map { str(it) }),
            "client_cross_check" to clientCrossJson,
            "byte_accounting" to byteAccounts,
            "non_game_socket" to nonGameSocket,
            "decode" to decodeStats,
            "desyncs" to desyncs,
            "double_counts" to doubleCounts,
            "sanity_deltas" to sanity,
            "cross_validation" to str(r.crossValidation),
        )
    }

    private fun fmtPct(p: Double): String = "${"%.2f".format(p)}%"

    private fun verdictBadge(v: SessionTrustVerifier.Verdict): String = when (v) {
        SessionTrustVerifier.Verdict.PASS -> "PASS"
        SessionTrustVerifier.Verdict.WARN -> "WARN"
        SessionTrustVerifier.Verdict.FAIL -> "FAIL"
    }

    private const val MAX_DESYNC_ROWS = 40
    private const val MAX_SANITY_ROWS = 60
}

private fun printTrustUsage() {
    System.err.println(
        """
        TrustReport — prove a recorder session is complete and coherent.

        Usage:
          ./gradlew :tools:trustReport -Pargs="<session-dir> [options]"

        Options:
          --known <file>        path to recorder-known-opcodes.txt (default: re-resources/docs/net/recorder-known-opcodes.txt)
          --no-crossvalidate    skip the independent ISAAC deframe attempt
          -h, --help            show this help

        Outputs (under <session-dir>):
          trust-report.md       human-readable; leads with PASS / WARN / FAIL
          trust-report.json     machine-readable

        Exit code 3 on a FAIL verdict (so it can gate a pipeline).
        """.trimIndent()
    )
}
