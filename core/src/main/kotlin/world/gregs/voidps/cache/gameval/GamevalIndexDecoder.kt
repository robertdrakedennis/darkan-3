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
 */
class GamevalIndexDecoder {

    /**
     * Decodes every gameval type present in [cache]'s index 67 into `typeName -> (id -> name)`.
     * For [GamevalIndex.COMPONENT] the inner-map keys are the packed `(interfaceId << 16) |
     * componentId` ints (use [decodeComponents] for the friendlier `"iface:comp"` view).
     */
    fun decode(cache: Cache): Map<String, Map<Int, String>> {
        val result = LinkedHashMap<String, Map<Int, String>>()
        for (archive in cache.archives(GamevalIndex.INDEX)) {
            val type = GamevalIndex.typeName(archive)
            if (!GamevalIndex.TYPE_BY_ARCHIVE.containsKey(archive)) {
                logWarn("Unknown gameval index-67 archive $archive — naming it '$type'")
            }
            val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: continue
            result[type] = decodeFile(data, type)
        }
        return result
    }

    /** Decodes a single gameval [type]'s `id -> name` table, or `null` if it is not in index 67. */
    fun decode(cache: Cache, type: String): Map<Int, String>? {
        val archive = GamevalIndex.archiveId(type) ?: return null
        val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: return null
        return decodeFile(data, type)
    }

    /** Decodes a single index-67 [archive]'s `id -> name` table, or `null` if it has no data. */
    fun decodeArchive(cache: Cache, archive: Int): Map<Int, String>? {
        val data = cache.data(GamevalIndex.INDEX, archive, 0) ?: return null
        return decodeFile(data, GamevalIndex.typeName(archive))
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

    companion object {
        private const val VERSION_DENSE = 1
        private const val VERSION_SPARSE = 2

        /** Cap on the pre-sized map capacity so a corrupt huge [count] cannot OOM up front. */
        private const val MAX_PREALLOC = 1 shl 21
    }
}
