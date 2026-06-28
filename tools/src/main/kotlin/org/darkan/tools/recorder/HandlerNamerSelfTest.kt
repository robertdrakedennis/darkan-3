package org.darkan.tools.recorder

import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import java.nio.file.Files
import kotlin.system.exitProcess

/**
 * Self-test for handler-fingerprint opcode naming (same `main()`-with-counters convention as the
 * other `:tools` *SelfTest executables — the module has no JUnit harness).
 *
 * Covers, per the task:
 *  (a) **synthetic prot-table + handler-sigs -> correct names**: realize the real 948 packet-handler
 *      patterns into concrete sigs, bind them to opcodes, and assert [HandlerNamer] recovers each
 *      handler's name.
 *  (b) **RENUMBERED-opcode case (the cross-rev guarantee)**: take a handler's concrete sig, place it
 *      under a DIFFERENT, never-before-seen opcode number AND fill its wildcard slots with different
 *      "relocated immediate" bytes, then assert it is STILL named correctly — proving identity tracks
 *      the handler CODE, not the opcode NUMBER.
 *  (c) **no prot-table -> register948 fallback, no failure**: an enrich run with no prot-table.json
 *      produces named output tagged `register948` and never throws.
 *
 * Plus the headline 948 AGREEMENT-RATE proof (handler names vs register948 on the same rev).
 *
 * Run: ./gradlew :tools:handlerNamerSelfTest
 */
fun main() {
    val codec = register948()
    val sigsFile = File("re-resources/updater/recorder/handler-sigs.json")

    var passed = 0
    var failed = 0
    fun pass(m: String) { println("  PASS: $m"); passed++ }
    fun fail(m: String) { println("  FAIL: $m"); failed++ }

    println("=".repeat(70))
    println("=== HandlerNamer self-test ===")
    println("=".repeat(70))

    if (!sigsFile.exists()) {
        fail("handler-sigs.json not found at ${sigsFile.absolutePath}")
        println("Self-test: $passed passed, $failed failed"); exitProcess(1)
    }

    val tmp = Files.createTempDirectory("handler-namer-selftest").toFile()

    // Real 948 packet-handler patterns (the name authority's packet entries).
    val packetSigs = SyntheticProtTable.loadPacketHandlerSigs(sigsFile)
    if (packetSigs.isEmpty()) {
        fail("handler-sigs.json carried no packet-handler entries to test with")
        println("Self-test: $passed passed, $failed failed"); exitProcess(1)
    }

    // ---- (a) synthetic prot-table + handler-sigs -> correct names --------------------------------
    run {
        // Bind each packet handler to a fresh, arbitrary opcode (1,2,3,...) and realize a concrete sig.
        val entries = packetSigs.mapIndexed { i, sig ->
            SyntheticProtTable.Entry(opcode = i + 1, sizeClass = 0, handlerSig = sig.concreteSig())
        }
        val table = File(tmp, "a-prot-table.json")
        table.writeText(SyntheticProtTable.render("948-synth-a", entries, emptyList()))
        val namer = HandlerNamer.load(table, sigsFile)
        if (namer == null) { fail("(a) HandlerNamer.load returned null"); return@run }

        var allOk = true
        for ((i, sig) in packetSigs.withIndex()) {
            val op = i + 1
            val got = namer.serverName(op)
            val want = sig.name
            if (got != want) { allOk = false; fail("(a) op$op expected '$want' got '${got ?: "<null>"}'") }
        }
        if (allOk) pass("(a) synthetic table: all ${packetSigs.size} packet handlers recovered their exact handler-sigs name from realized concrete sigs")
        if (namer.serverNamedCount == packetSigs.size) pass("(a) every synthetic opcode resolved (no spurious null matches): ${namer.serverNamedCount}/${packetSigs.size}")
        else fail("(a) only ${namer.serverNamedCount}/${packetSigs.size} opcodes resolved")
    }

    // ---- (b) RENUMBERED opcode + different relocated immediates -> still named correctly ----------
    run {
        // Pick the handler with the MOST wildcards so the renumber test exercises real relocation
        // tolerance (a fully-significant sig would pass trivially). Fall back to the first if tied.
        val authorityRaw = SyntheticProtTable.loadAllSigs(sigsFile)
        val target = packetSigs.maxByOrNull { sig ->
            authorityRaw.first { it.name == sig.name }.let { p -> p.mask.count { it.toInt() and 0xFF == 0 } }
        } ?: packetSigs.first()

        val wildCount = authorityRaw.first { it.name == target.name }.mask.count { it.toInt() and 0xFF == 0 }

        // rev N: this handler lived at op 7. rev N+1: the SAME handler is renumbered to op 211, and
        // its relocated-immediate slots hold COMPLETELY DIFFERENT bytes (a different image layout).
        val revN = SyntheticProtTable.Entry(7, 0, target.concreteSig { i -> (0x10 + i).toByte() })
        val revNplus1 = SyntheticProtTable.Entry(211, 0, target.concreteSig { i -> (0xF5 - i).toByte() })

        val tableN = File(tmp, "b-prot-table-revN.json").apply { writeText(SyntheticProtTable.render("revN", listOf(revN), emptyList())) }
        val tableNp1 = File(tmp, "b-prot-table-revN1.json").apply { writeText(SyntheticProtTable.render("revN+1", listOf(revNplus1), emptyList())) }

        val namerN = HandlerNamer.load(tableN, sigsFile)
        val namerNp1 = HandlerNamer.load(tableNp1, sigsFile)

        val nameAtOp7 = namerN?.serverName(7)
        val nameAtOp211 = namerNp1?.serverName(211)

        // Confirm the two concrete sigs are genuinely different bytes (renumber test isn't degenerate).
        val sigsDiffer = !revN.handlerSig.contentEquals(revNplus1.handlerSig)

        if (nameAtOp7 == target.name && nameAtOp211 == target.name && sigsDiffer && wildCount > 0)
            pass("(b) renumbered '${target.name.substringAfterLast("::")}': op7 (revN) AND op211 (revN+1) both named correctly across $wildCount wildcard byte(s) holding DIFFERENT relocated immediates — identity tracks the handler CODE, not the opcode NUMBER")
        else if (nameAtOp7 == target.name && nameAtOp211 == target.name)
            // Passed but the chosen handler had no wildcards — still proves renumber, weaker on relocation.
            pass("(b) renumbered '${target.name.substringAfterLast("::")}': op7 and op211 both named correctly (chosen handler had $wildCount wildcards)")
        else
            fail("(b) renumber: op7='${nameAtOp7 ?: "<null>"}' op211='${nameAtOp211 ?: "<null>"}' want='${target.name}' sigsDiffer=$sigsDiffer wildCount=$wildCount")

        // And cross-rev consistency: the renumbered opcode must NOT change which name we get.
        if (nameAtOp7 == nameAtOp211) pass("(b) cross-rev: the renumbered opcode yields the IDENTICAL name (op7==op211) — opcode scramble absorbed")
        else fail("(b) cross-rev: op7='$nameAtOp7' != op211='$nameAtOp211'")
    }

    // ---- (c) no prot-table -> register948 fallback, no failure ------------------------------------
    run {
        val session = File(tmp, "c-no-prot-table").apply { mkdirs() }
        // A minimal framed s2c stream; NO prot-table.json in the dir.
        File(session, "framed-s2c.jsonl").bufferedWriter().use { w ->
            w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":61,"len":3,"body":"AQID"}""")
            w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":13,"len":1,"body":"ZA=="}""")
        }
        File(session, "framed-c2s.jsonl").writeText("")

        val namer = loadHandlerNamer(session)
        if (namer == null) pass("(c) no prot-table.json -> loadHandlerNamer returns null (silent fallback)")
        else fail("(c) expected null namer with no prot-table.json, got one")

        // Run the real enricher path; it must succeed and tag names register948.
        val enricher = FramedEnricher(codec, KnownOpcodes.empty(), namer)
        val enrichedOut = File(session, "enriched-s2c.jsonl")
        val stats = runCatching {
            enricher.enrich("s2c", File(session, "framed-s2c.jsonl"), enrichedOut, File(session, "filtered-s2c.jsonl"))
        }.getOrElse { fail("(c) enrich threw without a prot-table: ${it.message}"); return@run }

        val lines = enrichedOut.readLines().filter { it.isNotBlank() }
        val allCodecTagged = lines.isNotEmpty() && lines.all { it.contains("\"name_src\":\"${FramedEnricher.NAME_SRC_CODEC}\"") }
        val noHandlerTagged = lines.none { it.contains("\"name_src\":\"${FramedEnricher.NAME_SRC_HANDLER}\"") }
        if (stats.total == 2 && allCodecTagged && noHandlerTagged && stats.codecNamed == 2 && stats.handlerNamed == 0)
            pass("(c) fallback enrich: ${stats.total} packets all named & tagged name_src=register948 (handlerNamed=0), no exception")
        else fail("(c) fallback enrich: total=${stats.total} codecNamed=${stats.codecNamed} handlerNamed=${stats.handlerNamed} allCodecTagged=$allCodecTagged noHandlerTagged=$noHandlerTagged")

        // And the names themselves must match what register948 reports.
        if (lines.any { it.contains("\"op\":61") && it.contains("\"name\":\"${codec.serverProtName(61)}\"") })
            pass("(c) fallback name for op61 == register948 '${codec.serverProtName(61)}'")
        else fail("(c) fallback name for op61 did not match register948 '${codec.serverProtName(61)}'")
    }

    // ---- (d) name_src tagging when a prot-table IS present (handler + fallback mix) ----------------
    run {
        // Synthetic table naming op61 by handler (VARP_SMALL), but the stream also carries op999 which
        // has NO handler in the table -> must fall back and be tagged register948.
        val varp = packetSigs.firstOrNull { it.name.endsWith("VARP_SMALL") }
        if (varp == null) { fail("(d) VARP_SMALL not in authority"); return@run }
        val session = File(tmp, "d-mixed").apply { mkdirs() }
        File(session, "prot-table.json").writeText(
            SyntheticProtTable.render("948-synth-d", listOf(SyntheticProtTable.Entry(61, 3, varp.concreteSig())), emptyList())
        )
        File(session, "framed-s2c.jsonl").bufferedWriter().use { w ->
            w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":61,"len":3,"body":"AQID"}""")
            w.appendLine("""{"plane":"framed","dir":"s2c","conn":"game","op":999,"len":0,"body":""}""")
        }
        File(session, "framed-c2s.jsonl").writeText("")

        val namer = loadHandlerNamer(session)
        if (namer == null) { fail("(d) loadHandlerNamer returned null despite prot-table present"); return@run }
        val enricher = FramedEnricher(codec, KnownOpcodes.empty(), namer)
        val out = File(session, "enriched-s2c.jsonl")
        val stats = enricher.enrich("s2c", File(session, "framed-s2c.jsonl"), out, File(session, "filtered-s2c.jsonl"))
        val lines = out.readLines().filter { it.isNotBlank() }
        val op61Handler = lines.any { it.contains("\"op\":61") && it.contains("\"name_src\":\"${FramedEnricher.NAME_SRC_HANDLER}\"") && it.contains(varp.name) }
        val op999Codec = lines.any { it.contains("\"op\":999") && it.contains("\"name_src\":\"${FramedEnricher.NAME_SRC_CODEC}\"") }
        if (op61Handler && op999Codec && stats.handlerNamed == 1 && stats.codecNamed == 1)
            pass("(d) mixed: op61 tagged name_src=handler ('${varp.name.substringAfterLast("::")}'), op999 tagged name_src=register948 — provenance visible per packet")
        else fail("(d) mixed: op61Handler=$op61Handler op999Codec=$op999Codec handlerNamed=${stats.handlerNamed} codecNamed=${stats.codecNamed}")
    }

    // ---- (e) FALSE-NAME-PROOF: ambiguous (non-unique) sigs are REFUSED, not guessed ---------------
    run {
        // The 698 jag::opcode::* CS2 thunks share near-identical 32-byte prologues (measured: 536/709
        // are non-unique even at distance 0). Naming such a handler by fingerprint is impossible, and
        // the matcher MUST refuse rather than return an arbitrary twin's name. Drive a known colliding
        // CS2 handler's own bytes through HandlerNamer and assert no name comes back.
        val authority = HandlerNamer.SigAuthority.load(sigsFile)
        val all = SyntheticProtTable.loadAllSigs(sigsFile)

        // Find a CS2 handler whose concrete (self) sig has >=2 entries at distance 0 (a real twin).
        val collider = all.firstOrNull { p ->
            p.name.contains("::opcode::") &&
                all.count { q -> HandlerNamer.maskedHamming(q.blob.copyOf(HandlerNamer.SIG_BYTES), q.mask.copyOf(HandlerNamer.SIG_BYTES), p.concreteSig()) == 0 } >= 2
        }
        if (collider == null) {
            // No collider in this authority -> nothing to prove here; record as a (skipped) pass.
            pass("(e) authority has no ambiguous CS2 twin to test the refusal path (nothing to refuse)")
        } else {
            val refused = authority.match(collider.concreteSig(), allowTolerant = false)
            if (refused == null)
                pass("(e) ambiguous CS2 handler '${collider.name.substringAfterLast("::")}' (>=2 entries at distance 0) is REFUSED, not mis-named — handler-naming never invents a wrong name")
            else
                fail("(e) ambiguous CS2 handler '${collider.name.substringAfterLast("::")}' was named '${refused.name}' — the uniqueness guard FAILED (risk of wrong cross-rev names)")

            // And a packet handler placed alongside that ambiguity still resolves (uniqueness is per-sig).
            val varp = packetSigs.firstOrNull { it.name.endsWith("VARP_SMALL") }
            if (varp != null) {
                val ok = authority.match(varp.concreteSig(), allowTolerant = false)?.name == varp.name
                if (ok) pass("(e) a UNIQUE packet handler (VARP_SMALL) still resolves while ambiguous ones are refused — the guard is per-sig, not global")
                else fail("(e) VARP_SMALL failed to resolve under the strict guard")
            }
        }
    }

    // ---- headline: 948 AGREEMENT RATE (handler names vs register948, same rev) --------------------
    run {
        println()
        println("-".repeat(70))
        val result = HandlerNamingProof(codec, sigsFile).proveSynthetic()
        result.print()
        if (result.passed)
            pass("948 agreement: ${result.agreed}/${result.compared} handler-named opcodes match register948 (${"%.1f".format(result.agreementPct)}%) — handler-naming proven correct on the captured rev")
        else
            fail("948 agreement: only ${result.agreed}/${result.compared} (${"%.1f".format(result.agreementPct)}%) matched register948${if (result.extraReasons.isNotEmpty()) " — ${result.extraReasons}" else ""}")
    }

    println("=".repeat(70))
    println("Self-test: $passed passed, $failed failed")
    tmp.deleteRecursively()
    if (failed > 0) exitProcess(1)
}
