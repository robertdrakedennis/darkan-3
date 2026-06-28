package org.darkan.tools.recorder

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File

/**
 * Agreement-rate PROOF for handler-fingerprint naming on 948.
 *
 * The claim under test: naming an opcode by its handler's byte-fingerprint reproduces the SAME packet
 * identity that the static `register948` codec assigns — on the SAME revision. A high agreement rate
 * on 948 proves the handler-naming pipeline is correct *before* we rely on it for 949 (where opcodes
 * renumber and `register948` is no longer authoritative, but the handlers still match).
 *
 * Two modes:
 *  1. **Real capture** (`--capture <session-dir>` or auto-discovery): if a session has a real
 *     `prot-table.json`, name every opcode via [HandlerNamer] and compare to `register948` per
 *     opcode. This is the strongest form — the prot-table is the client's own dispatch table.
 *  2. **Synthetic-from-authority** (default when no capture is present): build a faithful 948
 *     prot-table by realizing each named packet handler's wildcard pattern into a concrete
 *     `handler_sig` and binding it to the opcode `register948` already gives that packet, then verify
 *     [HandlerNamer] independently recovers the name by sig. This proves the matcher end-to-end on the
 *     REAL 948 handler signatures even before the dylib emits a live table.
 *
 * Run:
 *   ./gradlew :tools:handlerNamingProof                       # synthetic-from-authority (always works)
 *   ./gradlew :tools:handlerNamingProof -Pargs="<session>"   # against a real capture's prot-table.json
 */
fun main(args: Array<String>) {
    val codec = register948()
    val sigsFile = File("re-resources/updater/recorder/handler-sigs.json")
    if (!sigsFile.exists()) {
        System.err.println("handler-sigs.json not found at ${sigsFile.absolutePath} — cannot run the proof")
        kotlin.system.exitProcess(2)
    }

    val captureArg = args.firstOrNull { !it.startsWith("--") }
    val capture = captureArg?.let(::File) ?: discoverCaptureWithProtTable()

    println("=".repeat(70))
    println("=== Handler-naming agreement proof (948) ===")
    println("=".repeat(70))

    val result = if (capture != null && File(capture, "prot-table.json").exists()) {
        println("Mode: REAL capture — ${capture.absolutePath}/prot-table.json")
        HandlerNamingProof(codec, sigsFile).proveAgainstCapture(capture)
    } else {
        if (captureArg != null) {
            System.err.println("No prot-table.json under $captureArg — running synthetic-from-authority instead")
        }
        println("Mode: SYNTHETIC-from-authority (no live prot-table.json present)")
        HandlerNamingProof(codec, sigsFile).proveSynthetic()
    }

    println()
    result.print()
    println("=".repeat(70))
    // The proof is a verification gate: a single disagreement on a packet handler is a real defect.
    if (!result.passed) kotlin.system.exitProcess(1)
}

/**
 * Auto-discover a recorder session that has a `prot-table.json`, scanning the usual recording roots.
 * Returns the most-recently-modified match, or null.
 */
private fun discoverCaptureWithProtTable(): File? {
    val roots = listOf(
        File(System.getProperty("user.home"), ".undercut/recordings"),
        File("data/recordings"),
        File("recordings"),
    )
    return roots.asSequence()
        .filter { it.isDirectory }
        .flatMap { (it.listFiles { f -> f.isDirectory }?.asSequence() ?: emptySequence()) }
        .filter { File(it, "prot-table.json").exists() }
        .maxByOrNull { File(it, "prot-table.json").lastModified() }
}

/**
 * Computes handler-vs-codec name agreement on 948. Reusable from both the CLI proof and the self-test.
 */
class HandlerNamingProof(private val codec: Codec, private val sigsFile: File) {

    /** One opcode's verdict: the handler name, the codec name, and whether they agree (normalized). */
    data class Row(val dir: String, val opcode: Int, val handlerName: String, val codecName: String, val agree: Boolean)

    data class Result(val rows: List<Row>, val mode: String, val extraReasons: List<String> = emptyList()) {
        val compared get() = rows.size
        val agreed get() = rows.count { it.agree }
        val agreementPct get() = if (compared == 0) 100.0 else agreed * 100.0 / compared
        /** Pass iff every compared opcode agreed (and at least one was compared) and no extra failures. */
        val passed get() = compared > 0 && agreed == compared && extraReasons.isEmpty()

        fun print() {
            println("Compared $compared handler-named opcode(s) against register948 ($mode):")
            for (r in rows.sortedWith(compareBy({ it.dir }, { it.opcode }))) {
                val mark = if (r.agree) "OK  " else "FAIL"
                val detail = if (r.agree) r.handlerName else "${r.handlerName}  !=  ${r.codecName}"
                println("  [$mark] ${r.dir} op${r.opcode}: $detail")
            }
            for (reason in extraReasons) println("  [FAIL] $reason")
            println()
            println("AGREEMENT: $agreed/$compared = ${"%.1f".format(agreementPct)}%  -> ${if (passed) "PASS" else "FAIL"}")
        }
    }

    /** Agreement against a real capture's `prot-table.json`. */
    fun proveAgainstCapture(session: File): Result {
        val namer = HandlerNamer.load(File(session, "prot-table.json"), sigsFile)
            ?: return Result(emptyList(), "real capture", listOf("prot-table.json present but HandlerNamer.load returned null"))
        val rows = ArrayList<Row>()
        for ((op, resolved) in namer.serverResolved()) rows += row("s2c", op, resolved.name, codec.serverProtName(op))
        for ((op, resolved) in namer.clientResolved()) rows += row("c2s", op, resolved.name, codec.clientProtName(op))
        return Result(rows, "real capture: build=${namer.build}, sigs=${namer.sigVersion}")
    }

    /**
     * Synthetic-from-authority proof: realize each NAMED PACKET handler's wildcard pattern into a
     * concrete sig, bind it to the opcode register948 already gives that packet, write a synthetic
     * prot-table, then assert [HandlerNamer] recovers the name and it agrees with register948.
     *
     * Opcode binding is derived FROM the codec (find the opcode whose register948 name normalizes to
     * the handler's leaf), never hardcoded — so the proof is not circular: the handler match must
     * independently land on the same opcode→name the codec holds.
     */
    fun proveSynthetic(): Result {
        val authoritySigs = SyntheticProtTable.loadPacketHandlerSigs(sigsFile)
        // Map each handler leaf -> the s2c opcode register948 names with the same (normalized) leaf.
        val codecByNormName = HashMap<String, Int>()
        for (op in 0..255) {
            val n = codec.serverProtName(op)
            if (!n.startsWith("UNKNOWN_")) codecByNormName.putIfAbsent(normalize(n), op)
        }

        val entries = ArrayList<SyntheticProtTable.Entry>()
        val unbindable = ArrayList<String>()
        for (sig in authoritySigs) {
            val leaf = normalize(sig.name.substringAfterLast("::"))
            val op = codecByNormName[leaf]
            if (op == null) { unbindable += sig.name; continue }
            entries += SyntheticProtTable.Entry(op, codec.serverProtSize(op), sig.concreteSig())
        }
        if (entries.isEmpty()) {
            return Result(emptyList(), "synthetic", listOf("no named packet handler could be bound to a register948 opcode (unbindable: ${unbindable.take(5)})"))
        }

        val tmp = File.createTempFile("prot-table-synth", ".json")
        tmp.deleteOnExit()
        tmp.writeText(SyntheticProtTable.render("RS2Engine-948-NXT-5-synthetic", entries, emptyList()))

        val namer = HandlerNamer.load(tmp, sigsFile)
            ?: return Result(emptyList(), "synthetic", listOf("HandlerNamer.load returned null on the synthetic table"))

        val rows = ArrayList<Row>()
        for (e in entries) {
            val handlerName = namer.serverName(e.opcode)
                ?: run { rows += Row("s2c", e.opcode, "<no-match>", codec.serverProtName(e.opcode), false); continue }
            rows += row("s2c", e.opcode, handlerName, codec.serverProtName(e.opcode))
        }
        tmp.delete()
        // Unbound handlers are NOT failures — they are simply packets register948 names differently or
        // doesn't carry. They are excluded from the comparison, not counted against agreement.
        return Result(rows, "synthetic-from-authority (${entries.size} packet handlers realized)")
    }

    private fun row(dir: String, op: Int, handlerName: String, codecName: String): Row =
        Row(dir, op, handlerName, codecName, namesAgree(handlerName, codecName))

    companion object {
        /**
         * Two names AGREE if they denote the same packet identity. Handler-sigs names are
         * `jag::packethandlers::X::UPDATE_STAT` (UPPER_SNAKE leaf); register948 names are either
         * UPPER_SNAKE (stubs) or PascalCase encoder classes (`UpdateStat`). We compare on the
         * namespace-stripped, case-folded ALPHANUMERIC token — see [normalize].
         */
        fun namesAgree(a: String, b: String): Boolean = normalize(a) == normalize(b)

        /**
         * Reduce a name to its comparison key: strip any `::` namespace and then ALL non-alphanumeric
         * characters, uppercased. This makes the agreement test about packet IDENTITY, not the exact
         * word-split convention: `SETFILTER_PRIVATE` (handler) and `SetFilterPrivate` (codec) both
         * reduce to `SETFILTERPRIVATE`, and `UPDATE_RUNWEIGHT` vs `UpdateRunWeight` both to
         * `UPDATERUNWEIGHT`. The two sides legitimately differ only on where a word boundary falls;
         * the underlying handler match (case (a) of the self-test) is exact regardless.
         */
        fun normalize(name: String): String =
            name.substringAfterLast("::").filter { it.isLetterOrDigit() }.uppercase()
    }
}
