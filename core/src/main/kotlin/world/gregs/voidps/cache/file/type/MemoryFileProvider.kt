package world.gregs.voidps.cache.file.type

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.file.FileProvider

/**
 * Reads all [Cache] sectors into memory as raw container data.
 * Fast read speed, high memory usage.
 *
 * Block framing for the NXT JS5 protocol is handled by [FileProvider.serve].
 */
class MemoryFileProvider(cache: Cache) : FileProvider {

    private val sectors: Array<Array<ByteArray?>?> = arrayOfNulls(256)
    private val versions: Array<IntArray?> = arrayOfNulls(256)

    init {
        val index255 = arrayOfNulls<ByteArray>(256)
        sectors[255] = index255
        val index255Versions = IntArray(256)
        versions[255] = index255Versions
        index255[255] = cache.versionTable
        index255Versions[255] = cache.sectorVersion(255, 255)
        for (index in cache.indices()) {
            val last = cache.lastArchiveId(index)
            val archives = arrayOfNulls<ByteArray>(last + 1)
            sectors[index] = archives
            val archiveVersions = IntArray(last + 1)
            versions[index] = archiveVersions
            for (archive in cache.archives(index)) {
                archives[archive] = cache.sector(index, archive) ?: continue
                archiveVersions[archive] = cache.sectorVersion(index, archive)
            }
            index255[index] = cache.sector(255, index) ?: continue
            index255Versions[index] = cache.sectorVersion(255, index)
        }
    }

    override fun data(index: Int, archive: Int): ByteArray? {
        return sectors.getOrNull(index)?.getOrNull(archive)
    }

    override fun version(index: Int, archive: Int): Int? {
        if (data(index, archive) == null) {
            return null
        }
        return versions.getOrNull(index)?.getOrNull(archive)
    }
}
