package org.darkan.tools.util

import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext

data class RefTableEntry(val id: Int, val crc: Int, val version: Int, val fileCount: Int = 0)

/**
 * Parsed JS5 reference table (archive index). Single canonical decoder for the
 * cache downloader, integrity checker and index verification tools.
 */
data class RefTable(
    val format: Int,
    val revision: Int,
    val flags: Int,
    val entries: List<RefTableEntry>,
    val totalFileCount: Int,
    val maxGroupId: Int,
    val bytesRemaining: Int,
) {
    fun crcById(): Map<Int, Int> = entries.associate { it.id to it.crc }
    fun versionById(): Map<Int, Int> = entries.associate { it.id to it.version }
}

/** Decompress [rawTable] then parse it. Returns null on decompression failure or unknown format. */
fun parseRefTable(context: DecompressionContext, rawTable: ByteArray, parseFiles: Boolean = false): RefTable? {
    val decompressed = context.decompress(rawTable) ?: return null
    return parseRefTable(decompressed, parseFiles)
}

/**
 * Parse an already-decompressed reference table (formats 5..7).
 *
 * With [parseFiles] the per-group file counts, delta-encoded file IDs and (flag 0x1)
 * file name hashes are consumed as well, so [RefTable.bytesRemaining] == 0 proves the
 * whole table was understood (used by FullIndexTest).
 */
fun parseRefTable(decompressed: ByteArray, parseFiles: Boolean = false): RefTable? {
    val reader = BufferReader(decompressed)

    val format = reader.readUnsignedByte()
    if (format < 5 || format > 7) return null
    val revision = if (format >= 6) reader.readInt() else 0

    val flags = reader.readUnsignedByte()
    fun count(): Int = if (format >= 7) reader.readBigSmart() else reader.readUnsignedShort()

    val archiveCount = count()

    // Archive/group IDs (delta-encoded)
    var previous = 0
    var maxId = -1
    val ids = IntArray(archiveCount) {
        val id = count() + previous
        previous = id
        if (id > maxId) maxId = id
        id
    }

    if (flags and 0x1 != 0) reader.skip(archiveCount * 4)      // name hashes
    val crcs = IntArray(archiveCount) { reader.readInt() }     // CRCs
    if (flags and 0x8 != 0) reader.skip(archiveCount * 4)      // unknown hashes
    if (flags and 0x2 != 0) reader.skip(archiveCount * 64)     // whirlpool digests
    if (flags and 0x4 != 0) reader.skip(archiveCount * 8)      // compressed + uncompressed sizes
    val versions = IntArray(archiveCount) { reader.readInt() } // versions

    var totalFiles = 0
    val fileCounts = IntArray(archiveCount)
    if (parseFiles) {
        for (i in 0 until archiveCount) {
            val fc = count()
            fileCounts[i] = fc
            totalFiles += fc
        }
        // File IDs (delta-encoded per group)
        for (i in 0 until archiveCount) {
            repeat(fileCounts[i]) { count() }
        }
        // File name hashes (flag 0x1, one int per file)
        if (flags and 0x1 != 0) {
            for (i in 0 until archiveCount) {
                reader.skip(fileCounts[i] * 4)
            }
        }
    }

    val entries = List(archiveCount) { i -> RefTableEntry(ids[i], crcs[i], versions[i], fileCounts[i]) }
    return RefTable(format, revision, flags, entries, totalFiles, maxId, reader.remaining)
}
