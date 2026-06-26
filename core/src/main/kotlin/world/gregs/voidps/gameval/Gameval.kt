package world.gregs.voidps.gameval

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * Maps cache numeric IDs to their RuneScape dev/rscm names (e.g. varbit `0` -> `zaros_spellbook`,
 * seq `0` -> `swarm_walk`), so UIs and logs can render real names instead of magic numbers.
 *
 * Data source: the RE `re-resources/gamevals/` JSON dictionaries, bundled into `:core`'s jar under
 * `/gamevals/<type>.json` at build time (see `core/build.gradle.kts`). Because the data ships on the
 * classpath, this works for the server AND for the injected engine shadowJar — neither needs repo
 * files present at runtime.
 *
 * Each JSON file is one config type (filename stem = type name) shaped as:
 * ```
 * { "revision": 947, "url": "...", "date": "...", "entries": { "<id>": "<name>", ... } }
 * ```
 * Names are unique per file, so a reverse name->id lookup is available too. The lone exception is
 * `component.json`, whose keys are composite `"<interfaceId>:<componentId>"` strings rather than
 * plain ints — handled by the dedicated [component]/[componentLabel] helpers.
 *
 * Maps are loaded lazily on first access and cached thread-safely. A missing/unreadable resource is
 * treated as empty rather than throwing, so absent types degrade gracefully to bare-id rendering.
 */
object Gameval {

    // --- Type-name constants (filename stems under /gamevals/) ---
    const val VARBIT = "varbit"
    const val VAR_CLIENT = "var_client"
    const val VAR_PLAYER = "var_player"
    const val VAR_NPC = "var_npc"
    const val VAR_CLAN = "var_clan"
    const val SEQ = "seq"
    const val SPRITE = "graphic" // gameval file `graphic.json` actually names sprites (2D images)
    const val INTERFACE = "interface"
    const val COMPONENT = "component"
    const val NPC = "npc"
    const val LOC = "loc"
    const val OBJ = "obj"
    const val STRUCT = "struct"
    const val ENUM = "enum"
    const val PARAM = "param"
    const val INV = "inv"
    const val SOUND = "sound"
    const val MIDI = "midi"
    const val MODEL = "model"
    const val CATEGORY = "category"
    const val DBROW = "dbrow"
    const val DBTABLE = "dbtable"

    @Serializable
    private data class GamevalFile(
        @SerialName("entries") val entries: Map<String, String> = emptyMap(),
    )

    private val json = Json { ignoreUnknownKeys = true }

    private val emptyStrings: Map<String, String> = emptyMap()
    private val emptyInts: Map<Int, String> = emptyMap()
    private val emptyReverse: Map<String, Int> = emptyMap()

    /** Raw `entries` map exactly as stored: composite keys for `component`, stringified ints otherwise. */
    private val rawCache = ConcurrentHashMap<String, Map<String, String>>()
    /** Int-keyed id->name view, derived from [rawCache] (keys that aren't ints are skipped). */
    private val intCache = ConcurrentHashMap<String, Map<Int, String>>()
    /** Reverse name->id view, derived from [intCache]. */
    private val reverseCache = ConcurrentHashMap<String, Map<String, Int>>()

    private fun rawMap(type: String): Map<String, String> = rawCache.computeIfAbsent(type, ::load)

    private fun load(type: String): Map<String, String> {
        val stream = Gameval::class.java.getResourceAsStream("/gamevals/$type.json") ?: return emptyStrings
        return try {
            stream.use { json.decodeFromString(GamevalFile.serializer(), it.readBytes().decodeToString()).entries }
        } catch (e: Exception) {
            emptyStrings
        }
    }

    private fun intMap(type: String): Map<Int, String> = intCache.computeIfAbsent(type) { t ->
        val raw = rawMap(t)
        if (raw.isEmpty()) return@computeIfAbsent emptyInts
        val out = HashMap<Int, String>(raw.size)
        for ((key, name) in raw) {
            val id = key.toIntOrNull() ?: continue
            out[id] = name
        }
        out
    }

    private fun reverseMap(type: String): Map<String, Int> = reverseCache.computeIfAbsent(type) { t ->
        val ints = intMap(t)
        if (ints.isEmpty()) return@computeIfAbsent emptyReverse
        val out = HashMap<String, Int>(ints.size)
        for ((id, name) in ints) out[name] = id
        out
    }

    // --- Generic API ---

    /** The dev-name for [id] in [type], or `null` if the type or id is unknown. */
    fun name(type: String, id: Int): String? = intMap(type)[id]

    /** Display label: `"name (id)"` when a name exists, else just `"id"`. The primary UI helper. */
    fun label(type: String, id: Int): String = name(type, id)?.let { "$it ($id)" } ?: id.toString()

    /** Reverse lookup: the id whose name is [name] in [type], or `null` if absent. Names are unique per type. */
    fun id(type: String, name: String): Int? = reverseMap(type)[name]

    /** True if [type] is bundled and has at least one entry. */
    fun has(type: String): Boolean = rawMap(type).isNotEmpty()

    /** The full id→dev-name map for [type] (empty if absent) — for enumeration/search UIs. */
    fun entries(type: String): Map<Int, String> = intMap(type)

    // --- Typed convenience wrappers (thin views over name/label) ---

    fun varbit(id: Int): String? = name(VARBIT, id)
    fun varbitLabel(id: Int): String = label(VARBIT, id)

    /** Client var (file `var_client`). */
    fun varc(id: Int): String? = name(VAR_CLIENT, id)
    fun varcLabel(id: Int): String = label(VAR_CLIENT, id)

    /** Player var (file `var_player`). */
    fun varp(id: Int): String? = name(VAR_PLAYER, id)
    fun varpLabel(id: Int): String = label(VAR_PLAYER, id)

    /** Animation (file `seq`). */
    fun seq(id: Int): String? = name(SEQ, id)
    fun seqLabel(id: Int): String = label(SEQ, id)

    /** Sprite names (2D images). The gameval file is `graphic.json` but it labels sprites — NOT
     * spotanims; spotanims/projectiles have no gameval and are named via their linked `seq`. */
    fun sprite(id: Int): String? = name(SPRITE, id)
    fun spriteLabel(id: Int): String = label(SPRITE, id)

    /** Top-level interface (file `interface`). */
    fun interfaceName(id: Int): String? = name(INTERFACE, id)
    fun interfaceLabel(id: Int): String = label(INTERFACE, id)

    fun npc(id: Int): String? = name(NPC, id)
    fun npcLabel(id: Int): String = label(NPC, id)

    fun loc(id: Int): String? = name(LOC, id)
    fun locLabel(id: Int): String = label(LOC, id)

    fun obj(id: Int): String? = name(OBJ, id)
    fun objLabel(id: Int): String = label(OBJ, id)

    fun struct(id: Int): String? = name(STRUCT, id)
    fun structLabel(id: Int): String = label(STRUCT, id)

    fun enum(id: Int): String? = name(ENUM, id)
    fun enumLabel(id: Int): String = label(ENUM, id)

    fun param(id: Int): String? = name(PARAM, id)
    fun paramLabel(id: Int): String = label(PARAM, id)

    /** Inventory/container (file `inv`) — e.g. 93 backpack, 94 worn equipment, 95 bank. */
    fun inv(id: Int): String? = name(INV, id)
    fun invLabel(id: Int): String = label(INV, id)

    // --- Component (composite "<interfaceId>:<componentId>" keys) ---

    /** The dev-name for interface component [interfaceId]:[componentId], or `null` if unknown. */
    fun component(interfaceId: Int, componentId: Int): String? = rawMap(COMPONENT)["$interfaceId:$componentId"]

    /** Display label: `"name (iface:comp)"` when a name exists, else just `"iface:comp"`. */
    fun componentLabel(interfaceId: Int, componentId: Int): String {
        val key = "$interfaceId:$componentId"
        return rawMap(COMPONENT)[key]?.let { "$it ($key)" } ?: key
    }
}
