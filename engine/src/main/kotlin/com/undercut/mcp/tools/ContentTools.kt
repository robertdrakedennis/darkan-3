package com.undercut.mcp.tools

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import world.gregs.voidps.cache.Cache
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

object ContentTools {

    private val DUMP_CACHE = ConcurrentHashMap<String, JsonElement>()

    fun register(server: Server): Int {
        registerGetContentType(server)
        registerListContentType(server)
        registerSearchCacheJson(server)
        return 3
    }

    private fun locateDumpsDir(): Path? {
        val env = System.getenv("UNDERCUT_DUMPS_DIR")
        if (env != null) {
            val p = Paths.get(env)
            if (Files.isDirectory(p)) return p
        }
        val candidates = listOf(
            Paths.get(System.getProperty("user.home"), "projects", "project-undercut", "rs3-cs2-dumps"),
            Paths.get(System.getProperty("user.dir"), "rs3-cs2-dumps"),
            Paths.get(System.getProperty("user.dir")).parent?.resolve("rs3-cs2-dumps"),
        ).filterNotNull()
        return candidates.firstOrNull { Files.isDirectory(it) }
    }

    private fun loadDump(filename: String): JsonElement = DUMP_CACHE.getOrPut(filename) {
        val dir = locateDumpsDir() ?: throw EngineState("rs3-cs2-dumps directory not found; set UNDERCUT_DUMPS_DIR env var")
        val file = dir.resolve(filename)
        if (!Files.exists(file)) throw EngineState("dump file not found: ${file.toAbsolutePath()}")
        Json.parseToJsonElement(Files.readString(file))
    }

    private fun dumpFileForKind(kind: String): String = when (kind.lowercase()) {
        "item" -> "items.json"
        "npc" -> "npcs.json"
        "obj", "loc" -> "locations.json"
        "enum" -> "enums.json"
        "struct" -> "structs.json"
        "quest" -> "quests.json"
        "varbit" -> "latest_varbits.json"
        "varp" -> "latest_varps.json"
        "dbrow" -> "dbrows.json"
        "achievement" -> "achievements.json"
        else -> throw BadRequest("no dump file mapped for kind '$kind'")
    }

    private fun reflectToJson(obj: Any, verbose: Boolean): JsonObject = buildJsonObject {
        val cls = obj::class
        for (prop in cls.declaredMemberProperties) {
            if (prop.visibility != KVisibility.PUBLIC) continue
            runCatching {
                prop.isAccessible = true
                val value = prop.getter.call(obj)
                val element = valueToJson(value, verbose)
                if (!verbose && isDefaultLike(element)) return@runCatching
                put(prop.name, element)
            }
        }
    }

    private fun isDefaultLike(e: JsonElement): Boolean = when (e) {
        is JsonNull -> true
        is JsonPrimitive -> e.content == "0" || e.content == "-1" || e.content == "0.0" || e.content == "false" || e.content == ""
        is JsonArray -> e.isEmpty()
        is JsonObject -> e.isEmpty()
    }

    private fun valueToJson(v: Any?, verbose: Boolean): JsonElement = when (v) {
        null -> JsonNull
        is Number -> JsonPrimitive(v)
        is Boolean -> JsonPrimitive(v)
        is String -> JsonPrimitive(v)
        is ByteArray -> JsonPrimitive("ByteArray(${v.size})")
        is IntArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i)) }
        is ShortArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i.toInt())) }
        is LongArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i)) }
        is FloatArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i)) }
        is DoubleArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i)) }
        is BooleanArray -> buildJsonArray { for (i in v) add(JsonPrimitive(i)) }
        is Array<*> -> buildJsonArray { for (e in v) add(valueToJson(e, verbose)) }
        is List<*> -> buildJsonArray { for (e in v) add(valueToJson(e, verbose)) }
        is Set<*> -> buildJsonArray { for (e in v) add(valueToJson(e, verbose)) }
        is Map<*, *> -> buildJsonObject {
            for ((k, vv) in v) {
                if (k == null) continue
                put(k.toString(), valueToJson(vv, verbose))
            }
        }
        is Enum<*> -> JsonPrimitive(v.name)
        else -> {
            runCatching { reflectToJson(v as Any, verbose) }
                .getOrElse { JsonPrimitive("(unserializable ${v.javaClass.simpleName})") }
        }
    }

    private fun lookupKotlinType(kind: String, id: Int): Any? = try {
        when (kind.lowercase()) {
            "npc" -> Cache.npc(id)
            "item" -> Cache.item(id)
            "obj", "loc" -> Cache.obj(id)
            "enum" -> Cache.enum(id)
            "struct" -> Cache.struct(id)
            "param" -> Cache.param(id)
            "varbit" -> Cache.varbit(id)
            "seq" -> Cache.seq(id)
            "bas" -> Cache.bas(id)
            "inv" -> Cache.inv(id)
            "quest" -> Cache.quest(id)
            else -> null
        }
    } catch (_: Throwable) {
        null
    }

    private val KOTLIN_BACKED_KINDS = setOf("npc", "item", "obj", "loc", "enum", "struct", "param", "varbit", "seq", "bas", "inv", "quest")
    private val DUMP_BACKED_KINDS = setOf("varbit", "varp", "dbrow", "achievement", "npc", "item", "obj", "loc", "enum", "struct", "quest")

    private fun findDumpRecord(kind: String, id: Int): JsonObject? {
        val element = runCatching { loadDump(dumpFileForKind(kind)) }.getOrNull() ?: return null
        val array = element as? JsonArray ?: return null
        return array.asSequence()
            .filterIsInstance<JsonObject>()
            .firstOrNull { (it["id"] ?: it["varid"])?.jsonPrimitive?.content?.toIntOrNull() == id }
    }

    private fun registerGetContentType(server: Server) {
        server.addTool(
            name = "get_content_type",
            description = """
                Purpose: Unified lookup of any cache-defined content type — NPC, item (obj-type), location/object (loc-type), enum, struct, param, varbit, seq, bas, inv, quest. Returns the full field dump from the Kotlin cache reader (when available) and optionally merges supplementary fields from the rs3-cs2-dumps JSON.
                || Returns: JSON envelope with: kind, id, sources (array of "kotlin" / "dump"), fields (Kotlin-reflected fields, null defaults trimmed unless verbose), dump_extra (only present when verbose=true and the dump file has a record). For kinds with no Kotlin parser yet (varp, dbrow, achievement), only `dump_extra` is populated.
                || Inputs: `kind` (required string, one of npc|item|obj|loc|enum|struct|param|varbit|seq|bas|inv|quest|varp|dbrow|achievement). `id` (required int). `verbose` (optional bool, default false) — include default/empty fields and merge dump extras.
                || Use cases: "What does NPC type 0 look like (Hans)?", "List the option names on object id 1816 (a door)?", "Decode a varbit's base+bits", "Look up a struct's params by id".
                || Related tools: list_content_type (range scan), search_cache_json (text search across dumps), get_varbit (live decoded value).
                || Pitfalls: Kotlin parsers throw on unknown ids; this tool catches and reports `engine_state` with the cause. The Kotlin cache returns "default" types (id=0 fields) for unknown ids on some kinds — check `id` in the response. The dump fallback is the only source for varp/dbrow/achievement.
            """.trimIndent().replace("\n", " "),
            inputSchema = ToolSchema(
                properties = buildJsonObject {
                    putJsonObject("kind") {
                        put("type", "string")
                        put("description", "Content kind (npc, item, obj|loc, enum, struct, param, varbit, seq, bas, inv, quest, varp, dbrow, achievement)")
                    }
                    putJsonObject("id") {
                        put("type", "integer")
                        put("description", "Content id")
                    }
                    putJsonObject("verbose") {
                        put("type", "boolean")
                        put("description", "If true, include default/empty fields and merge rs3-cs2-dumps JSON fields under dump_extra")
                    }
                },
                required = listOf("kind", "id"),
            ),
        ) { request ->
            safeJsonCall("get_content_type") { warnings ->
                val args = request.arguments ?: throw BadRequest("missing arguments")
                val kind = args["kind"]?.jsonPrimitive?.content?.lowercase()
                    ?: throw BadRequest("missing 'kind'")
                val id = args["id"]?.jsonPrimitive?.content?.toIntOrNull()
                    ?: throw BadRequest("missing/invalid 'id'")
                val verbose = args["verbose"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false

                put("kind", kind)
                put("id", id)

                val sources = mutableListOf<String>()

                if (kind in KOTLIN_BACKED_KINDS) {
                    val obj = lookupKotlinType(kind, id)
                    if (obj != null) {
                        sources.add("kotlin")
                        put("fields", reflectToJson(obj, verbose))
                    } else {
                        warnings.add("Kotlin parser for kind=$kind id=$id threw or returned null")
                    }
                }

                if (verbose || kind !in KOTLIN_BACKED_KINDS) {
                    if (kind in DUMP_BACKED_KINDS) {
                        runCatching {
                            val record = findDumpRecord(kind, id)
                            if (record != null) {
                                sources.add("dump")
                                put(if (verbose && "kotlin" in sources) "dump_extra" else "fields", record)
                            }
                        }.onFailure { warnings.add("dump read failed: ${it.message}") }
                    }
                }

                if (sources.isEmpty()) {
                    throw EngineState("no source available for kind=$kind id=$id")
                }
                put("sources", buildJsonArray { for (s in sources) add(JsonPrimitive(s)) })
            }
        }
    }

    private fun registerListContentType(server: Server) {
        server.addTool(
            name = "list_content_type",
            description = """
                Purpose: Scan a content kind by id range or substring filter. For kinds with a Kotlin parser, iterates the parser's max-id range and runs each through the lookup. For kinds backed only by dumps (varp/dbrow/achievement), filters the dump array.
                || Returns: JSON envelope with kind, count, total, truncated, items[]. Each item: id + a compact summary (name when available, top-level fields trimmed). Use get_content_type for full detail.
                || Inputs: `kind` (required string). Standard list filters: `id` (exact filter — usually pointless here, but supported), `name` (substring on the resolved name when present), `limit` (default 50, hard max 500), `offset`, `verbose` (full per-item dump instead of summary).
                || Use cases: "Find every NPC with 'goblin' in the name", "List items 1000..1100", "Scan all varbits in domain PLAYER (use dump fallback)".
                || Related tools: get_content_type (full detail), search_cache_json (text-grep across dump files), find_cs2_callers (Phase 4 — who reads this content id from CS2).
                || Pitfalls: Scanning the Kotlin parsers calls `get(id)` for every id up to maxId; that's slow for large content sets. The first call after engine start cold-loads the parser cache. For dump-only kinds, the whole JSON file is parsed into memory and cached.
            """.trimIndent().replace("\n", " "),
            inputSchema = listSchema {
                putJsonObject("kind") {
                    put("type", "string")
                    put("description", "Content kind")
                }
            },
        ) { request ->
            safeJsonCall("list_content_type") { warnings ->
                val args = request.arguments ?: throw BadRequest("missing arguments")
                val kind = args["kind"]?.jsonPrimitive?.content?.lowercase()
                    ?: throw BadRequest("missing 'kind'")
                val filters = parseListFilters(args)

                put("kind", kind)

                if (kind in KOTLIN_BACKED_KINDS) {
                    val maxId = parserMaxId(kind)
                    if (maxId < 0) throw EngineState("could not determine maxId for kind=$kind")
                    put("max_id", maxId)

                    val rows = mutableListOf<Pair<Int, Any>>()
                    val startId = filters.offset
                    val limit = filters.limit
                    var scanned = 0
                    for (i in startId..maxId) {
                        if (rows.size >= limit) break
                        runCatching {
                            val obj = lookupKotlinType(kind, i) ?: return@runCatching
                            scanned++
                            if (filters.id != null && i != filters.id) return@runCatching
                            if (filters.name != null) {
                                val name = (obj::class.declaredMemberProperties.firstOrNull { it.name == "name" }
                                    ?.also { it.isAccessible = true }
                                    ?.getter?.call(obj) as? String)
                                if (name == null || !name.contains(filters.name, ignoreCase = true)) return@runCatching
                            }
                            rows.add(i to obj)
                        }
                    }
                    put("count", rows.size)
                    put("scanned", scanned)
                    put("truncated", rows.size == limit)
                    put("items", buildJsonArray {
                        for ((id, obj) in rows) {
                            add(buildJsonObject {
                                put("id", id)
                                if (filters.verbose) {
                                    put("fields", reflectToJson(obj, true))
                                } else {
                                    val name = (obj::class.declaredMemberProperties.firstOrNull { it.name == "name" }
                                        ?.also { it.isAccessible = true }
                                        ?.getter?.call(obj) as? String)
                                    if (name != null) put("name", name)
                                }
                            })
                        }
                    })
                } else if (kind in DUMP_BACKED_KINDS) {
                    val element = loadDump(dumpFileForKind(kind))
                    val arr = element as? JsonArray ?: throw EngineState("dump for kind=$kind is not a JSON array")
                    val filtered = arr.asSequence()
                        .filterIsInstance<JsonObject>()
                        .filter { row ->
                            val rowId = (row["id"] ?: row["varid"])?.jsonPrimitive?.content?.toIntOrNull()
                            if (filters.id != null && rowId != filters.id) return@filter false
                            if (filters.name != null) {
                                val name = row["name"]?.jsonPrimitive?.content
                                if (name == null || !name.contains(filters.name, ignoreCase = true)) return@filter false
                            }
                            true
                        }
                        .toList()
                    val window = filtered.drop(filters.offset).take(filters.limit)
                    put("count", window.size)
                    put("total", filtered.size)
                    put("truncated", filtered.size > window.size + filters.offset)
                    put("items", JsonArray(window))
                } else {
                    throw BadRequest("unknown kind '$kind'")
                }
            }
        }
    }

    // param/inv/quest have no eager whole-array decoder in :core, so a full id scan
    // (and therefore list_content_type) isn't available for them; get_content_type
    // still resolves a single id via the per-id accessors.
    private fun parserMaxId(kind: String): Int = try {
        when (kind.lowercase()) {
            "npc" -> Cache.npcs.size - 1
            "item" -> Cache.items.size - 1
            "obj", "loc" -> Cache.objects.size - 1
            "enum" -> Cache.enums.size - 1
            "struct" -> Cache.structs.size - 1
            "varbit" -> Cache.varbits.size - 1
            "seq" -> Cache.animations.size - 1
            "bas" -> Cache.bas.size - 1
            "param" -> Cache.params.size - 1
            "inv" -> Cache.invs.size - 1
            "quest" -> Cache.quests.size - 1
            else -> -1
        }
    } catch (_: Throwable) {
        -1
    }

    private fun registerSearchCacheJson(server: Server) {
        server.addTool(
            name = "search_cache_json",
            description = """
                Purpose: Keyword search across the rs3-cs2-dumps JSON files (items.json, npcs.json, locations.json, enums.json, structs.json, quests.json, dbrows.json, achievements.json, latest_varbits.json, latest_varps.json). Matches `name` (case-insensitive substring) and optional `id`.
                || Returns: JSON envelope with kind, count, total, items[]. Each row is the raw JSON record from the dump (id + all known fields).
                || Inputs: `kind` (required string, see get_content_type for valid kinds), standard list filters (`name`, `id`, `limit`, `offset`).
                || Use cases: "Find anything named 'lobster'", "Look up dbrow 12345 in the database tables", "Search achievements containing 'slayer'".
                || Related tools: get_content_type (single record with Kotlin parser too), list_content_type (sibling for parser-backed scan).
                || Pitfalls: Reads the entire dump file the first time per process (cached forever). For very large files (locations.json ~46MB) the first call can take 1-2s. The dumps are auto-updated externally — restart the engine to pick up a fresh dump.
            """.trimIndent().replace("\n", " "),
            inputSchema = listSchema {
                putJsonObject("kind") {
                    put("type", "string")
                    put("description", "Dump kind (see get_content_type for valid kinds)")
                }
            },
        ) { request ->
            safeJsonCall("search_cache_json") { _ ->
                val args = request.arguments ?: throw BadRequest("missing arguments")
                val kind = args["kind"]?.jsonPrimitive?.content?.lowercase() ?: throw BadRequest("missing 'kind'")
                val filters = parseListFilters(args)

                put("kind", kind)
                val element = loadDump(dumpFileForKind(kind))
                val arr = element as? JsonArray ?: throw EngineState("dump for kind=$kind is not a JSON array")
                val filtered = arr.asSequence()
                    .filterIsInstance<JsonObject>()
                    .filter { row ->
                        val rowId = (row["id"] ?: row["varid"])?.jsonPrimitive?.content?.toIntOrNull()
                        if (filters.id != null && rowId != filters.id) return@filter false
                        if (filters.name != null) {
                            val name = row["name"]?.jsonPrimitive?.content
                            if (name == null || !name.contains(filters.name, ignoreCase = true)) return@filter false
                        }
                        true
                    }
                    .toList()
                val window = filtered.drop(filters.offset).take(filters.limit)
                put("count", window.size)
                put("total", filtered.size)
                put("truncated", filtered.size > window.size + filters.offset)
                put("items", JsonArray(window))
            }
        }
    }
}
