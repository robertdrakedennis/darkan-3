package world.gregs.voidps.cache.gameval

import org.darkan.core.Logger.logWarn
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.Cache

/**
 * Decodes cache **index 67** (the gameval / RSCM id <-> dev-name index) into per-type
 * `id -> name` tables. See [GamevalIndex] for the archive-id -> type mapping and
 * `re-resources/docs/cache/gameval_index67.md` for the byte-precise format.
 *
 * Each gameval type is a single-file archive whose decompressed file has the layout:
 *
 * ```
 * int32  version   // 1 = dense (implicit ids), 2 = sparse (explicit keys)
 * int32  count     // number of slots
 * // version 1: count x int32 offset   (id == slot index; offset == -1 means "no name")
 * // version 2: count x (int32 key, int32 offset)   (explicit, possibly composite, keys)
 * byte[] blob      // null-terminated CP1252 strings; offset indexes into this region
 * ```
 *
 * `offset` is relative to the start of the string blob (i.e. the first byte after the
 * offset/pair table). Each name runs from its offset up to the next `0x00`.
 *
 * Raw cache names are UPPERCASE (e.g. `SWARM_WALK`); this decoder lowercases them to the canonical
 * RSCM dev-name used throughout the project and by the bundled `re-resources/gamevals/<type>.json`
 * (e.g. `swarm_walk`). For the [GamevalIndex.COMPONENT] type the raw name encodes
 * `INTERFACE_NAME__COMPONENT_NAME`; the first `__` is rewritten to `:` so the value matches the
 * bundled `component.json` (`interface_name:component_name`).
 *
 * Because the produced names match the bundled gamevals exactly, this is an alternate, cache-backed
 * source for [world.gregs.voidps.gameval.Gameval] — it could later load names from a real index-67
 * cache instead of the bundled json (not wired up here).
 *
 * ### Combined var/varbit domains
 * Each `var_*` archive is a **combined** namespace (see [GamevalIndex.VAR_DOMAINS]): entries whose
 * dev-name starts with `_` are that domain's **varbits**, the rest are its **vars**. [decode]
 * therefore emits TWO tables for every such archive — `var_<domain>` and `varbit_<domain>` — applying
 * [splitVarDomain]:
 * - **var-part** (`var_<domain>`): entries whose name does NOT start with `_`, keeping their archive
 *   id (those ids are the real game var ids).
 * - **varbit-part** (`varbit_<domain>`): entries whose name starts with `_`, rebased to `id - offset`
 *   (the leading `_` stripped), where `offset` is the smallest `_`-entry id in the archive.
 *
 * Verified from the real beta bytes: `var_player` (archive 61) → 10056 vars + 50171 varbits at
 * offset 12865; the union of the stripped `_`-names across every var domain reproduces the legacy
 * bundled `varbit.json` (53,359 names) exactly. Non-var archives are unaffected — they still decode
 * to a single `typeName -> (id -> name)` table. The lower-level [decodeArchive]/[decodeFile] return
 * the RAW combined map (no split); only [decode] and the per-domain [decodeVar]/[decodeVarbit]
 * helpers apply the split.
 */
class GamevalIndexDecoder {

    /**
     * Decodes every gameval type present in [cache]'s index 67 into `typeName -> (id -> name)`.
     * For [GamevalIndex.COMPONENT] the inner-map keys are the packed `(interfaceId << 16) |
     * componentId` ints (use [decodeComponents] for the friendlier `"iface:comp"` view). Each
     * combined `var_*` archive is split into both `var_<domain>` and `varbit_<domain>` entries (the
     * latter omitted when the domain has no varbits) — see the class KDoc and [splitVarDomain].
     */
    fun decode(cache: Cache): Map<String, Map<Int, String>> {
        val result = LinkedHashMap<String, Map<Int, String>>()
        for (archive in cache.archives(GamevalIndex.INDEX)) {
            val type = GamevalIndex.typeName(archive)
            if (!GamevalIndex.TYPE_BY_ARCHIVE.containsKey(archive)) {
                logWarn("Unknown gameval index-67 archive $archive — naming it '$type'")
            }
            val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: continue
            val combined = decodeFile(data, type)
            val domain = GamevalIndex.VAR_DOMAIN_BY_ARCHIVE[archive]
            if (domain == null) {
                result[type] = combined
            } else {
                val split = splitVarDomain(combined)
                result[domain.varType] = split.vars
                if (split.varbits.isNotEmpty()) result[domain.varbitType] = split.varbits
            }
        }
        return result
    }

    /**
     * Decodes a single gameval [type]'s `id -> name` table, or `null` if it is not in index 67. For
     * a `var_<domain>` / `varbit_<domain>` type this returns the split var-part / varbit-part
     * respectively (consistent with [decode]); all other types return their raw table.
     */
    fun decode(cache: Cache, type: String): Map<Int, String>? {
        GamevalIndex.VAR_DOMAIN_BY_VARBIT_TYPE[type]?.let { return decodeVarbit(cache, it.domain) }
        GamevalIndex.VAR_DOMAIN_BY_VAR_TYPE[type]?.let { return decodeVar(cache, it.domain) }
        val archive = GamevalIndex.archiveId(type) ?: return null
        val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: return null
        return decodeFile(data, type)
    }

    /**
     * Decodes a single index-67 [archive]'s RAW `id -> name` table (no var/varbit split applied),
     * or `null` if it has no data. For a combined `var_*` archive this returns the un-split combined
     * map; use [decodeVar]/[decodeVarbit] (or [decode]) for the split halves.
     */
    fun decodeArchive(cache: Cache, archive: Int): Map<Int, String>? {
        val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: return null
        return decodeFile(data, GamevalIndex.typeName(archive))
    }

    /**
     * The var-part of a combined var [domain] (`player`, `npc`, `clan`, `clan_setting`, `object`,
     * `client`, `player_group`) — entries whose name does NOT start with `_`, keeping their archive
     * id (the real game var ids). `null` if the domain's archive has no data; an unknown domain also
     * yields `null`. See [splitVarDomain].
     */
    fun decodeVar(cache: Cache, domain: String): Map<Int, String>? =
        decodeCombinedDomain(cache, domain)?.let { splitVarDomain(it).vars }

    /**
     * The varbit-part of a combined var [domain] — entries whose name starts with `_`, rebased to
     * `id - offset` with the leading `_` stripped (`offset` = smallest `_`-entry id). Returns an
     * empty map for a domain with no varbits (e.g. `client`, `player_group`); `null` if the domain's
     * archive has no data or the domain is unknown. See [splitVarDomain].
     */
    fun decodeVarbit(cache: Cache, domain: String): Map<Int, String>? =
        decodeCombinedDomain(cache, domain)?.let { splitVarDomain(it).varbits }

    /** Decodes the RAW combined map for var [domain], or `null` if unknown / no data. */
    private fun decodeCombinedDomain(cache: Cache, domain: String): Map<Int, String>? {
        val varDomain = GamevalIndex.VAR_DOMAIN_BY_NAME[domain] ?: return null
        val data = cache.data(GamevalIndex.INDEX, varDomain.archive, 0) ?: return null
        return decodeFile(data, varDomain.varType)
    }

    /**
     * Splits a combined var-domain [combined] `id -> name` table into its var-part and varbit-part,
     * per the proven rule (see the class KDoc):
     * - **vars**: entries whose name does NOT start with [GamevalIndex.VARBIT_PREFIX], ids kept.
     * - **varbits**: entries whose name DOES, rebased to `id - offset` with the leading prefix
     *   stripped, where `offset` is the smallest varbit-entry id (`null`/empty when no varbits).
     */
    fun splitVarDomain(combined: Map<Int, String>): VarSplit {
        val prefix = GamevalIndex.VARBIT_PREFIX
        val offset = combined.entries
            .filter { it.value.startsWith(prefix) }
            .minOfOrNull { it.key }
        val vars = LinkedHashMap<Int, String>()
        val varbits = LinkedHashMap<Int, String>()
        for ((id, name) in combined) {
            if (name.startsWith(prefix)) {
                // offset is non-null here: at least this varbit entry exists.
                varbits[id - offset!!] = name.removePrefix(prefix)
            } else {
                vars[id] = name
            }
        }
        return VarSplit(vars, varbits)
    }

    /**
     * The [GamevalIndex.COMPONENT] archive viewed as `"interfaceId:componentId" -> name`, mirroring
     * the composite keys of the bundled `component.json`. Empty if the archive is absent.
     */
    fun decodeComponents(cache: Cache): Map<String, String> {
        val packed = decode(cache, GamevalIndex.COMPONENT) ?: return emptyMap()
        val out = LinkedHashMap<String, String>(packed.size)
        for ((key, name) in packed) {
            val interfaceId = (key ushr 16) and 0xffff
            val componentId = key and 0xffff
            out["$interfaceId:$componentId"] = name
        }
        return out
    }

    /**
     * Decodes one already-container-decompressed gameval file ([data]) into `id -> name`, applying
     * the [type]-specific name normalisation (lowercasing, plus the `__`->`:` split for
     * [GamevalIndex.COMPONENT]). [type] only affects normalisation, not the byte layout.
     */
    fun decodeFile(data: ByteArray, type: String = ""): Map<Int, String> {
        val reader = BufferReader(data)
        val version = reader.readInt()
        val count = reader.readInt()
        if (count < 0) {
            logWarn("Negative gameval entry count $count for type '$type' — skipping")
            return emptyMap()
        }
        val component = type == GamevalIndex.COMPONENT
        // count is the table capacity, so it is a sound initial map size for both versions.
        val names = LinkedHashMap<Int, String>(count.coerceAtMost(MAX_PREALLOC))
        when (version) {
            VERSION_DENSE -> {
                // Implicit ids: slot i names content id i; a -1 offset marks "no name for this id".
                val offsets = IntArray(count) { reader.readInt() }
                val blobBase = reader.position()
                for (id in 0 until count) {
                    val offset = offsets[id]
                    if (offset < 0) continue
                    reader.position(blobBase + offset)
                    names[id] = normalise(reader.readString(), component)
                }
            }
            VERSION_SPARSE -> {
                // Explicit (key, offset) pairs — keys may be sparse or composite (component).
                val keys = IntArray(count)
                val offsets = IntArray(count)
                for (i in 0 until count) {
                    keys[i] = reader.readInt()
                    offsets[i] = reader.readInt()
                }
                val blobBase = reader.position()
                for (i in 0 until count) {
                    val offset = offsets[i]
                    if (offset < 0) continue
                    reader.position(blobBase + offset)
                    names[keys[i]] = normalise(reader.readString(), component)
                }
            }
            else -> throw IllegalArgumentException(
                "Unknown gameval index-67 file version $version for type '$type'"
            )
        }
        return names
    }

    /**
     * Lowercases the raw cache name to the canonical RSCM dev-name. For [component] names, the
     * `INTERFACE__COMPONENT` form additionally has its first `__` rewritten to `:` to match the
     * bundled `component.json` (`interface:component`).
     */
    private fun normalise(raw: String, component: Boolean): String {
        val lower = raw.lowercase()
        return if (component) lower.replaceFirst("__", ":") else lower
    }

    /**
     * The two halves of a split combined var-domain archive (see [splitVarDomain]).
     *
     * @property vars the var-part (`var_<domain>`), entries keyed by their original archive id.
     * @property varbits the varbit-part (`varbit_<domain>`), entries rebased to `id - offset` with
     *   their leading `_` stripped; empty for a domain that has no varbits.
     */
    data class VarSplit(val vars: Map<Int, String>, val varbits: Map<Int, String>)

    companion object {
        private const val VERSION_DENSE = 1
        private const val VERSION_SPARSE = 2

        /** Cap on the pre-sized map capacity so a corrupt huge [count] cannot OOM up front. */
        private const val MAX_PREALLOC = 1 shl 21
    }
}
