package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.CapturePacketDecode
import org.darkan.core.net.prot.revision.rev948.register948
import java.io.File
import java.util.Base64
import kotlin.system.exitProcess

/**
 * Offline enricher for a packet-recorder session directory.
 *
 * Reads `framed-s2c.jsonl` / `framed-c2s.jsonl` (flat JSON, one packet per line, opcode as a NUMBER
 * with no name) and produces the SAME flat shape plus:
 *  - `name`    — `serverProtName` (s2c) / `clientProtName` (c2s) from the shared rev948 [Codec].
 *  - `decoded` — for c2s opcodes that have a registered structured decoder, the rendered fields.
 *
 * `body` / `body_ref` / base64 pass through UNTOUCHED. Output is one JSON object per line with a
 * stable key order (original keys first, in their original order, then `name`, then `decoded`) so
 * `fff`/`rg`/`jq` over the result is deterministic.
 *
 * Filtering is OPT-IN: with no `--known`, everything is enriched and nothing is set aside. When an
 * explicit `--known` list of CONFIRMED fully-RE'd (dir, conn, opcode) packets is supplied, those are
 * routed to `<session>/filtered/` instead of `<session>/enriched/` — never dropped. (The
 * claude-re-seeded `recorder-known-opcodes.txt` is a first-iteration CANDIDATE list — it can be a bit
 * off, so it is NOT applied by default.)
 *
 * Run:
 *   ./gradlew :tools:enrichSession -Pargs="<session-dir> [--known <known-opcodes.txt>] [--no-crossvalidate]"
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
            "--help", "-h" -> { printUsage(); return }
            else -> {
                if (a.startsWith("--")) { System.err.println("Unknown arg: $a"); printUsage(); exitProcess(2) }
                sessionDir = a
            }
        }
        i++
    }

    val session = File(sessionDir ?: run { printUsage(); exitProcess(2) })
    if (!session.isDirectory) {
        System.err.println("Not a directory: ${session.absolutePath}")
        exitProcess(1)
    }

    val codec = register948()
    val known = if (knownPath != null) KnownOpcodes.load(File(knownPath)) else KnownOpcodes.empty()

    // Handler-fingerprint naming (rev-accurate): if the recorder emitted a `prot-table.json` for this
    // capture, resolve every opcode to its handler's name via the handler-sigs.json authority. Names
    // derived this way survive cross-rev opcode renumbering; `register948` is the static fallback.
    val handlerNamer = loadHandlerNamer(session)

    val enrichedDir = File(session, "enriched").apply { mkdirs() }
    val filteredDir = File(session, "filtered").apply { mkdirs() }

    println("==================== Enrich session ====================")
    println("  session : ${session.absolutePath}")
    println("  enriched: ${enrichedDir.absolutePath}")
    println("  filtered: ${filteredDir.absolutePath}")
    println("  known-opcodes rules: ${known.size}${if (known.size == 0) "  (filtering OFF — pass --known with a CONFIRMED list to enable)" else ""}")
    if (handlerNamer != null) {
        println(
            "  handler-naming: ON (prot-table build=${handlerNamer.build ?: "?"}, sigs=${handlerNamer.sigVersion ?: "?"}) " +
                "— s2c ${handlerNamer.serverNamedCount}/${handlerNamer.serverOpcodeCount}, " +
                "c2s ${handlerNamer.clientNamedCount}/${handlerNamer.clientOpcodeCount} opcodes matched to handlers"
        )
    } else {
        println("  handler-naming: OFF (no prot-table.json) — names from register948 only")
    }
    println()

    val enricher = FramedEnricher(codec, known, handlerNamer)
    for (dir in listOf("s2c", "c2s")) {
        val input = File(session, "framed-$dir.jsonl")
        if (!input.exists()) {
            println("  [skip] no framed-$dir.jsonl")
            continue
        }
        val stats = enricher.enrich(
            dir = dir,
            input = input,
            enrichedOut = File(enrichedDir, "framed-$dir.jsonl"),
            filteredOut = File(filteredDir, "framed-$dir.jsonl"),
        )
        val srcNote = if (handlerNamer != null) " (named by: ${stats.handlerNamed} handler, ${stats.codecNamed} register948)" else ""
        println("  [$dir] ${stats.total} records -> ${stats.enriched} enriched, ${stats.filtered} filtered, ${stats.decoded} c2s-decoded${if (stats.errors > 0) ", ${stats.errors} parse errors" else ""}$srcNote")
    }

    if (crossValidate) {
        println()
        println("  [crossvalidate] ${CrossValidator(codec, known).independentDeframeSummary(session)}")
    }

    println("\nDone.")
}

/**
 * Enriches one framed JSONL file. The flat input shape is preserved key-for-key; we only resolve
 * `name`/`decoded` and split the stream by [KnownOpcodes].
 */
class FramedEnricher(
    private val codec: Codec,
    private val known: KnownOpcodes,
    /** Handler-fingerprint namer for this capture (rev-accurate); null falls back to [codec] names. */
    private val handlerNamer: HandlerNamer? = null,
) {

    data class Stats(
        val total: Int,
        val enriched: Int,
        val filtered: Int,
        val decoded: Int,
        val errors: Int,
        /** Packets named via the prot-table handler match (rev-accurate). */
        val handlerNamed: Int = 0,
        /** Packets named via the static [Codec] (register948) fallback. */
        val codecNamed: Int = 0,
    )

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val base64 = Base64.getDecoder()

    fun enrich(dir: String, input: File, enrichedOut: File, filteredOut: File): Stats {
        var total = 0; var enrichedCount = 0; var filteredCount = 0; var decodedCount = 0; var errors = 0
        var handlerNamed = 0; var codecNamed = 0

        val blobsDir = File(input.parentFile, "blobs")

        enrichedOut.bufferedWriter().use { enrichedW ->
            filteredOut.bufferedWriter().use { filteredW ->
                input.forEachLine { rawLine ->
                    val line = rawLine.trim()
                    if (line.isEmpty()) return@forEachLine
                    total++

                    val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull()
                    if (obj == null) {
                        errors++
                        System.err.println("[$dir] line $total: not a JSON object, passing through raw")
                        enrichedW.appendLine(line)
                        enrichedCount++
                        return@forEachLine
                    }

                    val opcode = obj["op"]?.jsonPrimitive?.intOrNull
                    if (opcode == null) {
                        errors++
                        enrichedW.appendLine(line)
                        enrichedCount++
                        return@forEachLine
                    }
                    val conn = obj["conn"]?.jsonPrimitive?.contentOrNull

                    // Handler-match name FIRST (rev-accurate from the client's own dispatch table),
                    // then the static register948 codec name. Tag the SOURCE so coverage is visible.
                    val handlerName = handlerNamer?.let { if (dir == "s2c") it.serverName(opcode) else it.clientName(opcode) }
                    val name: String
                    val nameSrc: String
                    if (handlerName != null) {
                        name = handlerName; nameSrc = NAME_SRC_HANDLER; handlerNamed++
                    } else {
                        name = if (dir == "s2c") codec.serverProtName(opcode) else codec.clientProtName(opcode)
                        nameSrc = NAME_SRC_CODEC; codecNamed++
                    }

                    var decoded: String? = null
                    if (dir == "c2s" && CapturePacketDecode.hasClientDecoder(codec, opcode)) {
                        val body = resolveBody(obj, blobsDir)
                        if (body != null) {
                            decoded = CapturePacketDecode.decodeClient(codec, opcode, body)
                            if (decoded != null) decodedCount++
                        }
                    }

                    val outLine = appendFields(obj, name, nameSrc, decoded)
                    if (known.isKnown(dir, conn, opcode)) {
                        filteredW.appendLine(outLine)
                        filteredCount++
                    } else {
                        enrichedW.appendLine(outLine)
                        enrichedCount++
                    }
                }
            }
        }
        return Stats(total, enrichedCount, filteredCount, decodedCount, errors, handlerNamed, codecNamed)
    }

    /** base64 `body`, else `body_ref` -> blobs/<hash>.bin. Null when neither is present/usable. */
    private fun resolveBody(obj: JsonObject, blobsDir: File): ByteArray? {
        obj["body"]?.jsonPrimitive?.contentOrNull?.let { b64 ->
            return runCatching { base64.decode(b64) }.getOrNull()
        }
        obj["body_ref"]?.jsonPrimitive?.contentOrNull?.let { ref ->
            val file = if (File(ref).isAbsolute) File(ref) else File(blobsDir, File(ref).name)
            if (file.exists()) return runCatching { file.readBytes() }.getOrNull()
        }
        return null
    }

    /**
     * Re-serialize [obj] preserving original key order, then append `name`, `name_src`, and (if
     * present) `decoded`. We rebuild the line by hand (rather than re-encoding the map) so existing
     * keys keep the dylib's exact ordering and formatting of pass-through fields. `name_src` is
     * `"handler"` (rev-accurate prot-table match) or `"register948"` (static codec fallback) so the
     * naming provenance — and thus handler-naming coverage — is visible in every enriched record.
     */
    private fun appendFields(obj: JsonObject, name: String, nameSrc: String, decoded: String?): String {
        val ordered = LinkedHashMap<String, JsonElement>(obj.size + 3)
        for ((k, v) in obj) {
            if (k == "name" || k == "name_src" || k == "decoded") continue
            ordered[k] = v
        }
        ordered["name"] = JsonPrimitive(name)
        ordered["name_src"] = JsonPrimitive(nameSrc)
        if (decoded != null) ordered["decoded"] = JsonPrimitive(decoded)
        return json.encodeToString(JsonObject.serializer(), JsonObject(ordered))
    }

    companion object {
        /** `name_src` value when an opcode was named from the prot-table handler-fingerprint match. */
        const val NAME_SRC_HANDLER = "handler"
        /** `name_src` value when an opcode fell back to the static [Codec] (register948) name. */
        const val NAME_SRC_CODEC = "register948"
    }
}

/**
 * Locate this capture's `prot-table.json` + the handler-sigs.json name authority and build a
 * [HandlerNamer], or return null (silent fallback to register948) when either is absent.
 *
 * `prot-table.json` is emitted per-session by the recorder dylib into the session dir. The name
 * authority lives at the canonical `re-resources/updater/recorder/handler-sigs.json`; a per-session
 * copy (if the recorder dropped one beside the table) takes precedence so a capture can pin the exact
 * authority it was named against.
 */
fun loadHandlerNamer(session: File): HandlerNamer? {
    val protTable = File(session, "prot-table.json")
    if (!protTable.exists()) return null
    val sessionSigs = File(session, "handler-sigs.json")
    val sigs = if (sessionSigs.exists()) sessionSigs else File("re-resources/updater/recorder/handler-sigs.json")
    if (!sigs.exists()) {
        System.err.println("[handler-namer] prot-table.json present but handler-sigs.json not found (${sigs.absolutePath}) — falling back to register948")
        return null
    }
    return runCatching { HandlerNamer.load(protTable, sigs) }
        .onFailure { System.err.println("[handler-namer] failed to load (${it.message}) — falling back to register948") }
        .getOrNull()
}

private fun printUsage() {
    System.err.println(
        """
        EnrichSession — turn a recorder session dir into named/decoded/filtered JSONL.

        Usage:
          ./gradlew :tools:enrichSession -Pargs="<session-dir> [options]"

        Options:
          --known <file>        OPT-IN: route confirmed fully-RE'd (dir conn opcode) packets to filtered/.
                                Default OFF — nothing is set aside. A claude-re-seeded CANDIDATE list lives
                                at re-resources/docs/net/recorder-known-opcodes.txt; pass it only after
                                confirming those opcodes are truly fully RE'd (claude-re can be slightly off).
          --no-crossvalidate    skip the raw-*.bin independent deframe diff
          -h, --help            show this help

        Outputs (under <session-dir>):
          enriched/framed-s2c.jsonl, enriched/framed-c2s.jsonl   (ALL packets, unless --known is set)
          filtered/framed-s2c.jsonl, filtered/framed-c2s.jsonl   (only with --known: confirmed fully-RE'd)
        """.trimIndent()
    )
}
