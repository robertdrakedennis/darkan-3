package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.ClientStateCrossCheck
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
    CoverageLedger(codec).write(session, report)

    println()
    println(TrustReportRenderer.console(report))
    println()
    println("  wrote ${md.absolutePath}")
    println("  wrote ${jsonFile.absolutePath}")
    println("  wrote ${File(session, "coverage.md").absolutePath}")
    println("  wrote ${File(session, "coverage.json").absolutePath}")

    // Non-zero exit on FAIL so this can gate CI / a capture pipeline.
    if (report.verdict == SessionTrustVerifier.Verdict.FAIL) exitProcess(3)
}

/** Renders a [SessionTrustVerifier.Report] to markdown, JSON, and a short console banner. */
object TrustReportRenderer {

    private val json = Json { prettyPrint = true }
    private val gamevals = GamevalNameResolver.default()

    fun console(r: SessionTrustVerifier.Report): String {
        val sb = StringBuilder()
        sb.appendLine("  VERDICT: ${r.verdict}")
        for (reason in r.reasons) sb.appendLine("    - $reason")
        val cc = r.clientCrossCheck
        val ccLine = when {
            !cc.available -> "client-is-king: UNAVAILABLE (no snapshot — run a fresh capture)"
            cc.hasFailure -> "client-is-king: FAIL (${cc.varpMismatches.size} varp + ${cc.varcMismatches.size} varc + ${cc.varcStringMismatches.size} varcstring + ${cc.skillMismatches.size} skill + ${cc.inventoryMismatches.size} inventory + ${cc.appearanceMismatches.size} appearance + ${cc.sceneNpcPresenceMismatches.size + cc.scenePlayerPresenceMismatches.size} scene presence mismatch)"
            cc.varpAdvisory.isNotEmpty() ->
                "client-is-king: OK (${cc.varpMatches} varps + ${cc.varcMatches} numeric varcs + ${cc.varcStringMatches} string varcs + ${cc.skillMatches} skills + ${cc.inventoryMatches} inventory slots + ${cc.appearanceMatches} appearance slots match; ${cc.varpAdvisory.size} client-dynamic varp advisory)"
            cc.varcUnverified.isNotEmpty() || cc.varcStringUnverified.isNotEmpty() || cc.appearanceUnverified.isNotEmpty() ->
                "client-is-king: OK (${cc.varpMatches} varps + ${cc.varcMatches} numeric varcs + ${cc.varcStringMatches} string varcs + ${cc.skillMatches} skills + ${cc.inventoryMatches} inventory slots + ${cc.appearanceMatches} appearance slots match; ${cc.varcUnverified.size + cc.varcStringUnverified.size} varc write(s), ${cc.appearanceUnverified.size} appearance slot(s) unverified)"
            else -> "client-is-king: OK (${cc.varpMatches} varps + ${cc.varcMatches} numeric varcs + ${cc.varcStringMatches} string varcs + ${cc.skillMatches} skills + ${cc.inventoryMatches} inventory slots + ${cc.appearanceMatches} appearance slots match the client's own state)"
        }
        sb.appendLine("  $ccLine")
        for (m in r.clientVerifiedBreadth) {
            sb.appendLine("  [${m.dir}] client-verified opcodes: ${m.clientVerifiedOpcodes}/${m.observedOpcodes} observed (${fmtPct(m.pct)})")
        }
        for (m in r.clientVerifiedCoverage) {
            sb.appendLine("  [${m.dir}] client-verified volume: ${m.clientVerifiedPackets}/${m.observedPackets} observed packets (${fmtPct(m.pct)})")
        }
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
        for (m in r.clientVerifiedBreadth) {
            sb.appendLine("- **${m.dir} Client-verified opcodes**: ${m.clientVerifiedOpcodes}/${m.observedOpcodes} observed (${fmtPct(m.pct)})")
        }
        for (m in r.clientVerifiedCoverage) {
            sb.appendLine("- **${m.dir} Client-verified volume**: ${m.clientVerifiedPackets}/${m.observedPackets} observed packets (${fmtPct(m.pct)})")
        }
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
        sb.appendLine("Clean decode now means `decode → re-encode == original body`. Decode-only packets whose current display decoder discards bytes are counted as unsupported and do not earn capture-observed confidence. c2s `xtea_body` packets are encrypted and not decodable offline — counted separately, never as failures.")
        sb.appendLine()
        sb.appendLine("| conn | dir | decodable | decoded | round-trip supported | round-trip clean | failed | unsupported | encrypted (skipped) | clean rate | failing opcodes |")
        sb.appendLine("|---|---|--:|--:|--:|--:|--:|--:|--:|--:|---|")
        for (d in r.decodeStats) {
            val failing = if (d.failingOpcodes.isEmpty()) "—"
            else d.failingOpcodes.entries.joinToString(", ") { "op${it.key}×${it.value}" }
            sb.appendLine(
                "| ${d.conn} | ${d.dir} | ${d.decodable} | ${d.decoded} | ${d.roundTripSupported} | " +
                    "${d.roundTripPassed} | ${d.failed} | ${d.roundTripUnsupported} | ${d.encryptedSkipped} | " +
                    "${fmtPct(d.cleanRatePct)} | $failing |"
            )
        }
        sb.appendLine()

        sb.appendLine("## 2a. Opcode confidence")
        sb.appendLine()
        sb.appendLine("| dir | opcode | name | observed packets | decoded | round-trip | confidence |")
        sb.appendLine("|---|--:|---|--:|--:|---:|---|")
        for (o in r.opcodeTrust.sortedWith(compareBy<SessionTrustVerifier.OpcodeTrust> { it.dir }.thenBy { it.opcode })) {
            val rt = if (o.roundTripSupported == 0) "n/a" else "${o.roundTripPassed}/${o.roundTripSupported} (${fmtPct(o.roundTripRatePct)})"
            sb.appendLine("| ${o.dir} | ${o.opcode} | ${o.name} | ${o.observedPackets} | ${o.decodedPackets} | $rt | ${o.confidence.name} |")
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
        sb.appendLine("Our decoded state-bearing s2c packets (varps, varcs, inventories, appearance, skills, run energy) are folded into the EXPECTED client state and compared field-by-field to the client's OWN final snapshot (`state-snapshots.jsonl`, last line = authoritative for persistent domains). **The client is the ground truth** — a value our decode produces that disagrees with the client's committed state means OUR decode is wrong, and state the client holds that we never decoded means a state-bearing packet was dropped. Either is a hard FAIL. Varcs are keyed by `(recordKind,varId)`, matching the client tree; inventories are keyed by `inventoryId` and compared against final snapshot only. This catches a decoder that is byte-clean (round-trips, never throws) yet produces the wrong integer — which the other proofs cannot.")
        sb.appendLine()
        if (!cc.available) {
            sb.appendLine("**Unavailable** — ${cc.reason}. This capture has no client state snapshot (older recorder, before `state-snapshots.jsonl`). Run a fresh capture to enable the cross-check. This is NOT a failure; the other three proofs stand on their own.")
            sb.appendLine()
            return
        }
        val status = when {
            cc.hasFailure -> "FAIL — decoded state does not reproduce the client's committed state"
            cc.varcUnverified.isNotEmpty() || cc.varcStringUnverified.isNotEmpty() || cc.appearanceUnverified.isNotEmpty() ->
                "OK — decoded values with client-oracle ground truth match; some folded writes are unverified"
            else -> "OK — decoded state matches the client's committed state"
        }
        sb.appendLine("**Result: $status**")
        sb.appendLine()
        avatarTrajectorySection(sb, r.avatarTrajectory)
        sb.appendLine("| family | match | mismatch | missed | unverified | extra | detail |")
        sb.appendLine("|---|--:|--:|--:|--:|--:|---|")
        val varpDetail = if (cc.varpMismatches.isEmpty() && cc.varpMissed.isEmpty() && cc.varpAdvisory.isEmpty()) "—"
        else (cc.varpMismatches.take(8).joinToString { "${gamevals.varpDisplay(it.varId)}: ${it.ourValue}≠${it.clientValue}" } +
            (if (cc.varpAdvisory.isNotEmpty()) " advisory=" + cc.varpAdvisory.take(8).joinToString {
                "${gamevals.varpDisplay(it.varId)}: ${it.ourValue}→${it.clientValue} (${it.evidence})"
            } else "") +
            (if (cc.varpMissed.isNotEmpty()) " missed=" + cc.varpMissed.take(8).joinToString { gamevals.varpDisplay(it) } else "")).trim()
        sb.appendLine("| VARPS | ${cc.varpMatches} | ${cc.varpMismatches.size} | ${cc.varpMissed.size} | 0 | ${cc.varpExtra.size} | $varpDetail |")
        val varcDetail = if (cc.varcMismatches.isEmpty() && cc.varcMissed.isEmpty() && cc.varcUnverified.isEmpty()) "—"
        else (cc.varcMismatches.take(8).joinToString {
            "ours=${gamevals.display(it.key, it.ourValueKind, it.ourNumberValue, it.ourStringValue)}" +
                "≠client=${gamevals.display(it.key, it.clientValueKind, it.clientNumberValue, it.clientStringValue)}"
        } +
            (if (cc.varcMissed.isNotEmpty()) " missed=" + cc.varcMissed.take(8).joinToString { gamevals.display(it) } else "") +
            (if (cc.varcUnverified.isNotEmpty()) " unverified=" + cc.varcUnverified.take(8).joinToString { gamevals.display(it) } else "")).trim()
        sb.appendLine("| VARCS | ${cc.varcMatches} | ${cc.varcMismatches.size} | ${cc.varcMissed.size} | ${cc.varcUnverified.size} | ${cc.varcExtra.size} | $varcDetail |")
        val varcStringDetail = if (cc.varcStringMismatches.isEmpty() && cc.varcStringMissed.isEmpty() && cc.varcStringUnverified.isEmpty()) "—"
        else (cc.varcStringMismatches.take(8).joinToString {
            "ours=${gamevals.display(it.key, it.ourValueKind, it.ourNumberValue, it.ourStringValue)}" +
                "≠client=${gamevals.display(it.key, it.clientValueKind, it.clientNumberValue, it.clientStringValue)}"
        } +
            (if (cc.varcStringMissed.isNotEmpty()) " missed=" + cc.varcStringMissed.take(8).joinToString { gamevals.display(it) } else "") +
            (if (cc.varcStringUnverified.isNotEmpty()) " unverified=" + cc.varcStringUnverified.take(8).joinToString { gamevals.display(it) } else "")).trim()
        sb.appendLine("| VARCSTRINGS | ${cc.varcStringMatches} | ${cc.varcStringMismatches.size} | ${cc.varcStringMissed.size} | ${cc.varcStringUnverified.size} | ${cc.varcStringExtra.size} | $varcStringDetail |")
        val skillDetail = if (cc.skillMismatches.isEmpty() && cc.skillMissed.isEmpty()) "—"
        else cc.skillMismatches.take(8).joinToString { "skill${it.skillId}.${it.field}: ${it.ourValue}≠${it.clientValue}" }
        sb.appendLine("| SKILLS | ${cc.skillMatches} | ${cc.skillMismatches.size} | ${cc.skillMissed.size} | 0 | — | $skillDetail |")
        val inventoryDetail = if (cc.inventoryMismatches.isEmpty() && cc.inventoryMissed.isEmpty()) "—"
        else (cc.inventoryMismatches.take(8).joinToString {
            "inv${it.invId}[${it.slot}]: ours=${inventoryItemDisplay(it.ourItemId, it.ourCount)}" +
                "≠client=${inventoryItemDisplay(it.clientItemId, it.clientCount)}"
        } +
            (if (cc.inventoryMissed.isNotEmpty()) " missed=" + cc.inventoryMissed.take(8).joinToString { inventorySlotDisplay(it) } else "")).trim()
        sb.appendLine("| INVENTORIES | ${cc.inventoryMatches} | ${cc.inventoryMismatches.size} | ${cc.inventoryMissed.size} | 0 | — | $inventoryDetail |")
        val appearanceDetail = if (cc.appearanceMismatches.isEmpty() && cc.appearanceMissed.isEmpty() && cc.appearanceUnverified.isEmpty()) "—"
        else (cc.appearanceMismatches.take(8).joinToString { appearanceMismatchDisplay(it) } +
            (if (cc.appearanceMissed.isNotEmpty()) " missed=" + cc.appearanceMissed.take(8).joinToString { appearanceSlotDisplay(it) } else "") +
            (if (cc.appearanceUnverified.isNotEmpty()) " unverified=" + cc.appearanceUnverified.take(8).joinToString { appearanceSlotDisplay(it) } else "")).trim()
        sb.appendLine("| APPEARANCE | ${cc.appearanceMatches} | ${cc.appearanceMismatches.size} | ${cc.appearanceMissed.size} | ${cc.appearanceUnverified.size} | — | $appearanceDetail |")
        val scenePlayerDetail = scenePlayerDetail(cc)
        sb.appendLine("| SCENE PLAYERS | ${cc.scenePlayerPresenceMatches} | ${cc.scenePlayerPresenceMismatches.size + cc.scenePlayerPositionMismatches.size} | — | ${if (cc.scenePlayerPresenceMatches == 0) 1 else 0} | — | $scenePlayerDetail |")
        val sceneNpcDetail = sceneNpcDetail(cc)
        sb.appendLine("| SCENE NPCS | ${cc.sceneNpcPresenceMatches} | ${cc.sceneNpcPresenceMismatches.size + cc.sceneNpcTypeMismatches.size + cc.sceneNpcPositionMismatches.size} | — | 0 | — | $sceneNpcDetail |")
        val scalarDetail = if (cc.scalarMismatches.isEmpty()) "—"
        else cc.scalarMismatches.joinToString { "${it.field}: ${it.ourValue}≠${it.clientValue}" }
        sb.appendLine("| SCALARS (run energy/weight, tile) | — | ${cc.scalarMismatches.size} | — | 0 | — | $scalarDetail |")
        sb.appendLine()
        if (cc.varpExtra.isNotEmpty()) {
            sb.appendLine("> ${cc.varpExtra.size} varp(s) we decoded are not in the client's final snapshot — advisory only: the client legitimately overwrites/resets vars after the last packet, so an extra is not a decode fault.")
            sb.appendLine()
        }
        if (cc.varpAdvisory.isNotEmpty()) {
            sb.appendLine("> ${cc.varpAdvisory.size} varp mismatch(es) are advisory because snapshot history proves client-local timer drift after the last matching server write:")
            for (a in cc.varpAdvisory) {
                sb.appendLine("> - ${gamevals.varpDisplay(a.varId)} ours=${a.ourValue} client=${a.clientValue}; ${a.evidence}")
            }
            sb.appendLine()
        }
        if (cc.varcExtra.isNotEmpty()) {
            sb.appendLine("> ${cc.varcExtra.size} numeric varc(s) we decoded are not in the client's final snapshot — advisory only: the client legitimately overwrites/resets vars after the last packet, so an extra is not a decode fault.")
            sb.appendLine()
        }
        if (cc.varcUnverified.isNotEmpty()) {
            sb.appendLine("> ${cc.varcUnverified.size} numeric varc write(s) were folded from packets but had no client-oracle value in the final snapshot — unverified, not client-verified.")
            sb.appendLine()
        }
        if (cc.varcStringExtra.isNotEmpty()) {
            sb.appendLine("> ${cc.varcStringExtra.size} string varc(s) we decoded are not in the client's final snapshot — advisory only: the client legitimately overwrites/resets vars after the last packet, so an extra is not a decode fault.")
            sb.appendLine()
        }
        if (cc.varcStringUnverified.isNotEmpty()) {
            sb.appendLine("> ${cc.varcStringUnverified.size} string varc write(s) were folded from packets but had no client-oracle value in the final snapshot — unverified, not client-verified.")
            sb.appendLine()
        }
        if (cc.decodeFailures > 0) {
            sb.appendLine("> ${cc.decodeFailures} state-bearing body(ies) failed to decode against the documented wire format (short/garbled body).")
            sb.appendLine()
        }
    }

    private fun avatarTrajectorySection(
        sb: StringBuilder,
        trajectory: List<ClientStateCrossChecker.AvatarTrajectoryPoint>,
    ) {
        sb.appendLine("### Avatar tile trajectory")
        sb.appendLine()
        if (trajectory.isEmpty()) {
            sb.appendLine("_No local-player tile snapshots were present in `state-snapshots.jsonl`._")
            sb.appendLine()
            return
        }
        sb.appendLine("| tick | tile |")
        sb.appendLine("|---:|---|")
        for (point in trajectory.take(MAX_TRAJECTORY_ROWS)) {
            sb.appendLine("| ${trajectoryTickDisplay(point)} | ${tileDisplay(point.tile)} |")
        }
        if (trajectory.size > MAX_TRAJECTORY_ROWS) {
            sb.appendLine("| ... | _${trajectory.size - MAX_TRAJECTORY_ROWS} more point(s) in trust-report.json_ |")
        }
        sb.appendLine()
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
                "decodable" to num(d.decodable), "decoded" to num(d.decoded),
                "clean_decoded" to num(d.cleanDecoded),
                "failed" to num(d.failed), "encrypted_skipped" to num(d.encryptedSkipped),
                "round_trip_supported" to num(d.roundTripSupported),
                "round_trip_passed" to num(d.roundTripPassed),
                "round_trip_failed" to num(d.roundTripFailed),
                "round_trip_unsupported" to num(d.roundTripUnsupported),
                "clean_rate_pct" to num(d.cleanRatePct),
                "round_trip_rate_pct" to num(d.roundTripRatePct),
                "failing_opcodes" to JsonObject(d.failingOpcodes.entries.associate { it.key.toString() to num(it.value) as JsonElement }),
            )
        })
        val opcodeTrust = JsonArray(r.opcodeTrust.map { o ->
            obj(
                "dir" to str(o.dir), "opcode" to num(o.opcode), "name" to str(o.name),
                "observed_packets" to num(o.observedPackets),
                "decoded_packets" to num(o.decodedPackets),
                "round_trip_supported" to num(o.roundTripSupported),
                "round_trip_passed" to num(o.roundTripPassed),
                "round_trip_failed" to num(o.roundTripFailed),
                "round_trip_unsupported" to num(o.roundTripUnsupported),
                "round_trip_rate_pct" to if (o.roundTripSupported == 0) JsonNull else num(o.roundTripRatePct),
                "confidence" to str(o.confidence.name),
            )
        })
        val clientVerifiedCoverage = JsonArray(r.clientVerifiedCoverage.map { m ->
            obj(
                "dir" to str(m.dir),
                "client_verified_packets" to num(m.clientVerifiedPackets),
                "observed_packets" to num(m.observedPackets),
                "pct" to num(m.pct),
            )
        })
        val clientVerifiedBreadth = JsonArray(r.clientVerifiedBreadth.map { m ->
            obj(
                "dir" to str(m.dir),
                "client_verified_opcodes" to num(m.clientVerifiedOpcodes),
                "observed_opcodes" to num(m.observedOpcodes),
                "pct" to num(m.pct),
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
        fun gamevalIdentity(gamevalType: String, id: Int, rawField: String): LinkedHashMap<String, JsonElement> {
            val resolved = gamevals.resolve(gamevalType, id)
            val fields = linkedMapOf<String, JsonElement>(
                rawField to num(id),
                "gameval_type" to str(resolved.gamevalType),
                "display" to str(resolved.display),
            )
            resolved.name?.let { fields["name"] = str(it) }
            return fields
        }
        fun varpIdentity(varId: Int): LinkedHashMap<String, JsonElement> =
            gamevalIdentity(GamevalNameResolver.TYPE_VAR_PLAYER, varId, "var_id")
        fun varpJson(varId: Int): JsonObject =
            JsonObject(varpIdentity(varId))
        fun varpMismatchJson(m: ClientStateCrossCheck.VarpMismatch): JsonObject {
            val fields = varpIdentity(m.varId)
            fields["our_value"] = num(m.ourValue)
            fields["client_value"] = num(m.clientValue)
            return JsonObject(fields)
        }
        fun varpAdvisoryJson(a: ClientStateCrossCheck.VarpAdvisory): JsonObject {
            val fields = varpIdentity(a.varId)
            fields["our_value"] = num(a.ourValue)
            fields["client_value"] = num(a.clientValue)
            fields["advisory"] = JsonPrimitive(true)
            fields["reason"] = str("client_dynamic")
            fields["evidence"] = str(a.evidence)
            return JsonObject(fields)
        }
        fun varcIdentity(key: ClientStateCrossCheck.VarcKey): LinkedHashMap<String, JsonElement> {
            val fields = linkedMapOf<String, JsonElement>(
                "recordKind" to num(key.recordKind),
                "varId" to num(key.varId),
                "record_kind" to num(key.recordKind),
                "var_id" to num(key.varId),
                "gameval_type" to str(GamevalNameResolver.TYPE_VAR_CLIENT),
                "display" to str(gamevals.varcKeyDisplay(key)),
            )
            gamevals.varcNameFor(key.varId)?.let { fields["name"] = str(it) }
            return fields
        }
        fun varcValueJson(
            key: ClientStateCrossCheck.VarcKey,
            valueKind: Int,
            numberValue: Long?,
            stringValue: String?,
        ): JsonObject {
            val fields = varcIdentity(key)
            fields["valueKind"] = num(valueKind)
            fields["value_kind"] = num(valueKind)
            if (stringValue != null) {
                fields["str"] = str(stringValue)
            } else if (numberValue != null) {
                fields["value"] = num(numberValue)
            }
            fields["display"] = str(gamevals.display(key, valueKind, numberValue, stringValue))
            return JsonObject(fields)
        }
        fun varcNumberJson(state: ClientStateCrossCheck.VarcNumberState): JsonObject =
            varcValueJson(state.key, state.valueKind, state.value, null)
        fun varcStringJson(state: ClientStateCrossCheck.VarcStringState): JsonObject =
            varcValueJson(state.key, state.valueKind, null, state.value)
        fun varcMismatchJson(m: ClientStateCrossCheck.VarcMismatch): JsonObject {
            val fields = varcIdentity(m.key)
            fields["our_value"] = str(m.ourValue)
            fields["client_value"] = str(m.clientValue)
            fields["our"] = varcValueJson(m.key, m.ourValueKind, m.ourNumberValue, m.ourStringValue)
            fields["client"] = varcValueJson(m.key, m.clientValueKind, m.clientNumberValue, m.clientStringValue)
            return JsonObject(fields)
        }
        fun inventoryItemJson(itemId: Int, count: Long): JsonObject {
            val fields = linkedMapOf<String, JsonElement>(
                "item_id" to num(itemId),
                "count" to num(count),
            )
            if (itemId == -1) {
                fields["display"] = str("empty")
            } else {
                val resolved = gamevals.resolve(GamevalNameResolver.TYPE_OBJ, itemId)
                fields["gameval_type"] = str(resolved.gamevalType)
                fields["display"] = str("${resolved.display}×$count")
                resolved.name?.let { fields["name"] = str(it) }
            }
            return JsonObject(fields)
        }
        fun inventorySlotJson(state: ClientStateCrossCheck.InventorySlotState): JsonObject =
            obj(
                "inv_id" to num(state.invId),
                "slot" to num(state.slot),
                "item" to inventoryItemJson(state.itemId, state.count),
            )
        fun inventoryMismatchJson(m: ClientStateCrossCheck.InventoryMismatch): JsonObject =
            obj(
                "inv_id" to num(m.invId),
                "slot" to num(m.slot),
                "our_item_id" to num(m.ourItemId),
                "our_count" to num(m.ourCount),
                "client_item_id" to num(m.clientItemId),
                "client_count" to num(m.clientCount),
                "our" to inventoryItemJson(m.ourItemId, m.ourCount),
                "client" to inventoryItemJson(m.clientItemId, m.clientCount),
            )
        fun appearanceValueJson(kitId: Int, itemId: Int): JsonObject {
            val fields = linkedMapOf<String, JsonElement>(
                "kit_id" to num(kitId),
                "item_id" to num(itemId),
                "display" to str(appearanceValueDisplay(kitId, itemId)),
            )
            if (itemId >= 0) {
                val resolved = gamevals.resolve(GamevalNameResolver.TYPE_OBJ, itemId)
                fields["gameval_type"] = str(resolved.gamevalType)
                resolved.name?.let { fields["name"] = str(it) }
            }
            return JsonObject(fields)
        }
        fun appearanceSlotJson(state: ClientStateCrossCheck.AppearanceSlotState): JsonObject =
            obj(
                "slot" to num(state.slot),
                "kit_id" to num(state.kitId),
                "item_id" to num(state.itemId),
                "value" to appearanceValueJson(state.kitId, state.itemId),
            )
        fun appearanceMismatchJson(m: ClientStateCrossCheck.AppearanceMismatch): JsonObject =
            obj(
                "slot" to num(m.slot),
                "our_kit_id" to num(m.ourKitId),
                "our_item_id" to num(m.ourItemId),
                "client_kit_id" to num(m.clientKitId),
                "client_item_id" to num(m.clientItemId),
                "our" to appearanceValueJson(m.ourKitId, m.ourItemId),
                "client" to appearanceValueJson(m.clientKitId, m.clientItemId),
            )
        fun tileJson(tile: ClientStateCrossCheck.Tile): JsonObject =
            obj("x" to num(tile.x), "y" to num(tile.y), "plane" to num(tile.plane))
        fun scenePresenceJson(m: ClientStateCrossCheck.ScenePresenceMismatch): JsonObject {
            val fields = linkedMapOf<String, JsonElement>(
                "family" to str(m.family),
                "idx" to num(m.idx),
                "expected_present" to JsonPrimitive(m.expectedPresent),
                "client_present" to JsonPrimitive(m.clientPresent),
            )
            m.expectedTypeId?.let {
                fields["expected_type_id"] = num(it)
                fields["expected_type"] = gamevalIdentity(GamevalNameResolver.TYPE_NPC, it, "npc_id").let(::JsonObject)
            }
            m.clientTypeId?.let {
                fields["client_type_id"] = num(it)
                fields["client_type"] = gamevalIdentity(GamevalNameResolver.TYPE_NPC, it, "npc_id").let(::JsonObject)
            }
            return JsonObject(fields)
        }
        fun sceneNpcTypeMismatchJson(m: ClientStateCrossCheck.SceneNpcTypeMismatch): JsonObject =
            obj(
                "idx" to num(m.idx),
                "expected_type_id" to num(m.expectedTypeId),
                "client_type_id" to num(m.clientTypeId),
                "expected_type" to JsonObject(gamevalIdentity(GamevalNameResolver.TYPE_NPC, m.expectedTypeId, "npc_id")),
                "client_type" to JsonObject(gamevalIdentity(GamevalNameResolver.TYPE_NPC, m.clientTypeId, "npc_id")),
            )
        fun scenePositionJson(m: ClientStateCrossCheck.ScenePositionMismatch): JsonObject {
            val fields = linkedMapOf<String, JsonElement>(
                "family" to str(m.family),
                "idx" to num(m.idx),
                "expected_tile" to tileJson(m.expectedTile),
                "client_tile" to tileJson(m.clientTile),
            )
            m.typeId?.let {
                fields["type_id"] = num(it)
                fields["type"] = JsonObject(gamevalIdentity(GamevalNameResolver.TYPE_NPC, it, "npc_id"))
            }
            return JsonObject(fields)
        }
        fun trajectoryPointJson(point: ClientStateCrossChecker.AvatarTrajectoryPoint): JsonObject =
            obj(
                "tick" to (point.tick?.let { num(it) } ?: JsonNull),
                "mono_us" to (point.monoUs?.let { num(it) } ?: JsonNull),
                "tile" to tileJson(point.tile),
            )
        val avatarTrajectory = obj(
            "source" to str(ClientStateCrossChecker.SNAPSHOT_FILE),
            "local_player" to JsonArray(r.avatarTrajectory.map { trajectoryPointJson(it) }),
        )
        val opcodeWriteChecks = JsonArray(cc.opcodeWriteChecks.map {
            obj(
                "opcode" to num(it.opcode),
                "domain" to str(it.domain),
                "matches" to num(it.matches),
                "mismatches" to num(it.mismatches),
                "unverified" to num(it.unverified),
                "client_verified" to JsonPrimitive(it.clientVerified),
            )
        })
        val clientCrossJson = obj(
            "available" to JsonPrimitive(cc.available),
            "reason" to str(cc.reason),
            "has_failure" to JsonPrimitive(cc.hasFailure),
            "varp_matches" to num(cc.varpMatches),
            "varp_mismatches" to JsonArray(cc.varpMismatches.map { varpMismatchJson(it) }),
            "varp_advisory" to JsonArray(cc.varpAdvisory.map { varpAdvisoryJson(it) }),
            "varp_missed" to JsonArray(cc.varpMissed.map { num(it) }),
            "varp_missed_detail" to JsonArray(cc.varpMissed.map { varpJson(it) }),
            "varp_extra" to JsonArray(cc.varpExtra.map { num(it) }),
            "varp_extra_detail" to JsonArray(cc.varpExtra.map { varpJson(it) }),
            "varc_matches" to num(cc.varcMatches),
            "varc_mismatches" to JsonArray(cc.varcMismatches.map { varcMismatchJson(it) }),
            "varc_missed" to JsonArray(cc.varcMissed.map { varcNumberJson(it) }),
            "varc_unverified" to JsonArray(cc.varcUnverified.map { varcNumberJson(it) }),
            "varc_extra" to JsonArray(cc.varcExtra.map { varcNumberJson(it) }),
            "varcs_available" to JsonPrimitive(cc.varcsAvailable),
            "varcstring_matches" to num(cc.varcStringMatches),
            "varcstring_mismatches" to JsonArray(cc.varcStringMismatches.map { varcMismatchJson(it) }),
            "varcstring_missed" to JsonArray(cc.varcStringMissed.map { varcStringJson(it) }),
            "varcstring_unverified" to JsonArray(cc.varcStringUnverified.map { varcStringJson(it) }),
            "varcstring_extra" to JsonArray(cc.varcStringExtra.map { varcStringJson(it) }),
            "varcstrings_available" to JsonPrimitive(cc.varcStringsAvailable),
            "skill_matches" to num(cc.skillMatches),
            "skill_mismatches" to JsonArray(cc.skillMismatches.map {
                obj("skill_id" to num(it.skillId), "field" to str(it.field), "our_value" to num(it.ourValue), "client_value" to num(it.clientValue))
            }),
            "skill_missed" to JsonArray(cc.skillMissed.map { num(it) }),
            "inventory_matches" to num(cc.inventoryMatches),
            "inventory_mismatches" to JsonArray(cc.inventoryMismatches.map { inventoryMismatchJson(it) }),
            "inventory_missed" to JsonArray(cc.inventoryMissed.map { inventorySlotJson(it) }),
            "appearance_matches" to num(cc.appearanceMatches),
            "appearance_mismatches" to JsonArray(cc.appearanceMismatches.map { appearanceMismatchJson(it) }),
            "appearance_missed" to JsonArray(cc.appearanceMissed.map { appearanceSlotJson(it) }),
            "appearance_unverified" to JsonArray(cc.appearanceUnverified.map { appearanceSlotJson(it) }),
            "appearance_available" to JsonPrimitive(cc.appearanceAvailable),
            "scene_player_presence_matches" to num(cc.scenePlayerPresenceMatches),
            "scene_player_presence_mismatches" to JsonArray(cc.scenePlayerPresenceMismatches.map { scenePresenceJson(it) }),
            "scene_player_position_matches" to num(cc.scenePlayerPositionMatches),
            "scene_player_position_mismatches" to JsonArray(cc.scenePlayerPositionMismatches.map { scenePositionJson(it) }),
            "scene_npc_presence_matches" to num(cc.sceneNpcPresenceMatches),
            "scene_npc_presence_mismatches" to JsonArray(cc.sceneNpcPresenceMismatches.map { scenePresenceJson(it) }),
            "scene_npc_type_matches" to num(cc.sceneNpcTypeMatches),
            "scene_npc_type_mismatches" to JsonArray(cc.sceneNpcTypeMismatches.map { sceneNpcTypeMismatchJson(it) }),
            "scene_npc_position_matches" to num(cc.sceneNpcPositionMatches),
            "scene_npc_position_mismatches" to JsonArray(cc.sceneNpcPositionMismatches.map { scenePositionJson(it) }),
            "scalar_mismatches" to JsonArray(cc.scalarMismatches.map {
                obj("field" to str(it.field), "our_value" to str(it.ourValue), "client_value" to str(it.clientValue))
            }),
            "avatar_trajectory" to avatarTrajectory,
            "opcode_write_checks" to opcodeWriteChecks,
            "decode_failures" to num(cc.decodeFailures),
        )
        return obj(
            "session_id" to str(r.sessionId),
            "build" to str(r.build),
            "server_mode" to str(r.serverMode),
            "verdict" to str(r.verdict.name),
            "reasons" to JsonArray(r.reasons.map { str(it) }),
            "client_verified_breadth" to clientVerifiedBreadth,
            "client_verified_coverage" to clientVerifiedCoverage,
            "client_cross_check" to clientCrossJson,
            "byte_accounting" to byteAccounts,
            "non_game_socket" to nonGameSocket,
            "decode" to decodeStats,
            "opcode_trust" to opcodeTrust,
            "desyncs" to desyncs,
            "double_counts" to doubleCounts,
            "sanity_deltas" to sanity,
            "cross_validation" to str(r.crossValidation),
        )
    }

    private fun inventorySlotDisplay(slot: ClientStateCrossCheck.InventorySlotState): String =
        "inv${slot.invId}[${slot.slot}]=${inventoryItemDisplay(slot.itemId, slot.count)}"

    private fun inventoryItemDisplay(itemId: Int, count: Long): String =
        if (itemId == -1) "empty" else "${gamevals.objDisplay(itemId)}×$count"

    private fun appearanceSlotDisplay(slot: ClientStateCrossCheck.AppearanceSlotState): String =
        "slot${slot.slot}=${appearanceValueDisplay(slot.kitId, slot.itemId)}"

    private fun appearanceMismatchDisplay(m: ClientStateCrossCheck.AppearanceMismatch): String =
        "slot${m.slot}: ours=${appearanceValueDisplay(m.ourKitId, m.ourItemId)}" +
            "≠client=${appearanceValueDisplay(m.clientKitId, m.clientItemId)}"

    private fun appearanceValueDisplay(kitId: Int, itemId: Int): String =
        when {
            itemId >= 0 -> gamevals.objDisplay(itemId)
            kitId >= 0 -> "kit$kitId"
            else -> "empty"
        }

    private fun scenePlayerDetail(cc: ClientStateCrossCheck.CrossCheck): String {
        if (cc.scenePlayerPresenceMismatches.isEmpty() && cc.scenePlayerPositionMismatches.isEmpty()) {
            return if (cc.scenePlayerPresenceMatches == 0) "op22 GPI external-player fold unverified" else "—"
        }
        return (
            cc.scenePlayerPresenceMismatches.take(8).joinToString { scenePresenceDisplay(it) } +
                (if (cc.scenePlayerPositionMismatches.isNotEmpty()) " pos=" + cc.scenePlayerPositionMismatches.take(8).joinToString { scenePositionDisplay(it) } else "")
            ).trim()
    }

    private fun sceneNpcDetail(cc: ClientStateCrossCheck.CrossCheck): String {
        if (cc.sceneNpcPresenceMismatches.isEmpty() && cc.sceneNpcTypeMismatches.isEmpty() && cc.sceneNpcPositionMismatches.isEmpty()) return "—"
        return (
            cc.sceneNpcPresenceMismatches.take(8).joinToString { scenePresenceDisplay(it) } +
                (if (cc.sceneNpcTypeMismatches.isNotEmpty()) " type=" + cc.sceneNpcTypeMismatches.take(8).joinToString {
                    "npc${it.idx}: ${gamevals.npcDisplay(it.expectedTypeId)}≠${gamevals.npcDisplay(it.clientTypeId)}"
                } else "") +
                (if (cc.sceneNpcPositionMismatches.isNotEmpty()) " pos=" + cc.sceneNpcPositionMismatches.take(8).joinToString { scenePositionDisplay(it) } else "")
            ).trim()
    }

    private fun scenePresenceDisplay(m: ClientStateCrossCheck.ScenePresenceMismatch): String =
        if (m.family == "npc") {
            val type = m.expectedTypeId ?: m.clientTypeId
            "npc${m.idx}${type?.let { ":" + gamevals.npcDisplay(it) } ?: ""} expected=${m.expectedPresent} client=${m.clientPresent}"
        } else {
            "player${m.idx} expected=${m.expectedPresent} client=${m.clientPresent}"
        }

    private fun scenePositionDisplay(m: ClientStateCrossCheck.ScenePositionMismatch): String {
        val type = m.typeId?.let { ":" + gamevals.npcDisplay(it) } ?: ""
        return "${m.family}${m.idx}$type ours=(${m.expectedTile.x},${m.expectedTile.y},${m.expectedTile.plane})" +
            "≠client=(${m.clientTile.x},${m.clientTile.y},${m.clientTile.plane})"
    }

    private fun trajectoryTickDisplay(point: ClientStateCrossChecker.AvatarTrajectoryPoint): String =
        point.tick?.toString() ?: point.monoUs?.let { "mono_us=$it" } ?: "snapshot"

    private fun tileDisplay(tile: ClientStateCrossCheck.Tile): String =
        "(${tile.x},${tile.y},${tile.plane})"

    private fun fmtPct(p: Double): String = "${"%.2f".format(p)}%"

    private fun verdictBadge(v: SessionTrustVerifier.Verdict): String = when (v) {
        SessionTrustVerifier.Verdict.PASS -> "PASS"
        SessionTrustVerifier.Verdict.WARN -> "WARN"
        SessionTrustVerifier.Verdict.FAIL -> "FAIL"
    }

    private const val MAX_DESYNC_ROWS = 40
    private const val MAX_SANITY_ROWS = 60
    private const val MAX_TRAJECTORY_ROWS = 40
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
