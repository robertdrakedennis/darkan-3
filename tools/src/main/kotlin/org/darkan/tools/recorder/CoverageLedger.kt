package org.darkan.tools.recorder

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.recorder.Confidence
import java.io.File

/**
 * Writes `<session>/coverage.{md,json}`: table opcodes minus observed opcodes, per direction.
 */
class CoverageLedger(private val codec: Codec) {

    data class Result(val directions: List<Direction>)

    data class Direction(
        val dir: String,
        val tableSource: String,
        val inTable: Int,
        val observed: Int,
        val clientVerified: Int,
        val unseen: List<UnseenOpcode>,
    )

    data class UnseenOpcode(
        val opcode: Int,
        val sizeClass: Int,
        val handler: String,
        val handlerAddr: String?,
    )

    private data class TableOpcode(
        val opcode: Int,
        val sizeClass: Int,
        val handler: String,
        val handlerAddr: String?,
    )

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun write(session: File, report: SessionTrustVerifier.Report): Result {
        val tables = readOpcodeTable(session)
        val directions = listOf("s2c", "c2s").map { dir ->
            val table = tables[dir].orEmpty()
            val observed = readObservedOpcodes(session, dir)
            val observedInTable = observed.intersect(table.keys)
            val clientVerified = report.opcodeTrust
                .asSequence()
                .filter { it.dir == dir && it.confidence == Confidence.CLIENT_VERIFIED }
                .map { it.opcode }
                .toSet()
            val unseen = table.values
                .asSequence()
                .filter { it.opcode !in observed }
                .sortedBy { it.opcode }
                .map { UnseenOpcode(it.opcode, it.sizeClass, it.handler, it.handlerAddr) }
                .toList()
            Direction(
                dir = dir,
                tableSource = tableSource(session),
                inTable = table.size,
                observed = observedInTable.size,
                clientVerified = clientVerified.intersect(table.keys).size,
                unseen = unseen,
            )
        }
        val result = Result(directions)
        File(session, "coverage.md").writeText(markdown(session, result))
        File(session, "coverage.json").writeText(jsonText(session, result))
        return result
    }

    private fun tableSource(session: File): String =
        if (File(session, "prot-table.json").exists()) "prot-table" else "register948"

    private fun readOpcodeTable(session: File): Map<String, Map<Int, TableOpcode>> {
        val protTable = File(session, "prot-table.json")
        if (!protTable.exists()) return fallbackCodecTable()

        val root = runCatching { json.parseToJsonElement(protTable.readText()).jsonObject }.getOrNull()
            ?: return fallbackCodecTable()
        val namer = loadHandlerNamer(session)
        return mapOf(
            "s2c" to readDirectionTable(root["server_prot"], "s2c", namer),
            "c2s" to readDirectionTable(root["client_prot"], "c2s", namer),
        )
    }

    private fun readDirectionTable(
        element: JsonElement?,
        dir: String,
        namer: HandlerNamer?,
    ): Map<Int, TableOpcode> {
        if (element == null) return emptyMap()
        val out = LinkedHashMap<Int, TableOpcode>()
        for (el in element.jsonArray) {
            val obj = el as? JsonObject ?: continue
            val op = obj["op"]?.jsonPrimitive?.intOrNull ?: continue
            val sizeClass = obj["size_class"]?.jsonPrimitive?.intOrNull ?: 0
            val handlerName = if (dir == "s2c") namer?.serverName(op) else namer?.clientName(op)
            val fallback = if (dir == "s2c") codec.serverProtName(op) else codec.clientProtName(op)
            out[op] = TableOpcode(
                opcode = op,
                sizeClass = sizeClass,
                handler = handlerName ?: fallback,
                handlerAddr = obj["handler"]?.jsonPrimitive?.contentOrNull,
            )
        }
        return out
    }

    private fun fallbackCodecTable(): Map<String, Map<Int, TableOpcode>> =
        mapOf(
            "s2c" to codec.serverProtInfo.mapValues { (op, info) ->
                TableOpcode(op, info.size.toInt(), info.name, null)
            },
            "c2s" to codec.clientProtInfo.mapValues { (op, info) ->
                TableOpcode(op, info.size.toInt(), info.name, null)
            },
        )

    private fun readObservedOpcodes(session: File, dir: String): Set<Int> {
        val enriched = File(session, "enriched/framed-$dir.jsonl")
        val root = File(session, "framed-$dir.jsonl")
        val file = if (enriched.exists()) enriched else root
        if (!file.exists()) return emptySet()
        val out = LinkedHashSet<Int>()
        file.forEachLine { rawLine ->
            val line = rawLine.trim()
            if (line.isEmpty()) return@forEachLine
            val obj = runCatching { json.parseToJsonElement(line) as? JsonObject }.getOrNull() ?: return@forEachLine
            val op = obj["op"]?.jsonPrimitive?.intOrNull ?: return@forEachLine
            out += op
        }
        return out
    }

    private fun markdown(session: File, result: Result): String {
        val sb = StringBuilder()
        sb.appendLine("# Coverage ledger")
        sb.appendLine()
        sb.appendLine("- **Session**: `${session.name}`")
        sb.appendLine()
        sb.appendLine("| dir | table source | in_table | observed | client_verified | unseen |")
        sb.appendLine("|---|---|--:|--:|--:|--:|")
        for (d in result.directions) {
            sb.appendLine("| ${d.dir} | ${d.tableSource} | ${d.inTable} | ${d.observed} | ${d.clientVerified} | ${d.unseen.size} |")
        }
        for (d in result.directions) {
            sb.appendLine()
            sb.appendLine("## ${d.dir} unseen")
            sb.appendLine()
            if (d.unseen.isEmpty()) {
                sb.appendLine("None.")
                continue
            }
            sb.appendLine("| opcode | size_class | handler | handler_addr |")
            sb.appendLine("|--:|--:|---|---|")
            for (u in d.unseen) {
                sb.appendLine("| ${u.opcode} | ${u.sizeClass} | ${u.handler} | ${u.handlerAddr ?: "—"} |")
            }
        }
        return sb.toString()
    }

    private fun jsonText(session: File, result: Result): String {
        fun num(n: Number) = JsonPrimitive(n)
        fun str(s: String) = JsonPrimitive(s)
        fun obj(vararg pairs: Pair<String, JsonElement>) = JsonObject(linkedMapOf(*pairs))
        val dirs = JsonObject(result.directions.associate { d ->
            d.dir to obj(
                "table_source" to str(d.tableSource),
                "in_table" to num(d.inTable),
                "observed" to num(d.observed),
                "client_verified" to num(d.clientVerified),
                "unseen" to JsonArray(d.unseen.map { u ->
                    obj(
                        "opcode" to num(u.opcode),
                        "size_class" to num(u.sizeClass),
                        "handler" to str(u.handler),
                        "handler_addr" to (u.handlerAddr?.let { str(it) } ?: JsonNull),
                    )
                }),
            )
        })
        return json.encodeToString(
            JsonElement.serializer(),
            obj(
                "session_id" to str(session.name),
                "directions" to dirs,
            ),
        )
    }
}
