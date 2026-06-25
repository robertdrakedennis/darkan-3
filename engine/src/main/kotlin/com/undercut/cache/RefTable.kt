package com.undercut.cache

import java.nio.ByteBuffer

class RefTable(val cache: Cache, val index: Int, var version: Int = 0, var format: Int = 7, var mask: Int = 0) {
    val archives = mutableMapOf<Int, Archive>()

    fun decode(buffer: ByteBuffer) {
        format = buffer.get().toInt()
        if (format !in 5..7) throw IllegalArgumentException("Reference table format not in range 5..7 ($format)")
        version = if (format >= 6) buffer.int else 0
        mask = buffer.get().toInt()

        val hasNames = (mask and 0x1) != 0
        val hasWhirlpools = (mask and 0x2) != 0
        val hasSizes = (mask and 0x4) != 0
        val hasHashes = (mask and 0x8) != 0

        val numArchives = buffer.getFormatInt(format)
        val archiveIds = IntArray(numArchives)
        for (i in archiveIds.indices) {
            val archiveId = buffer.getFormatInt(format) + if (i == 0) 0 else archiveIds[i - 1]
            archiveIds[i] = archiveId
            archives[archiveId] = Archive(archiveId)
        }

        if (hasNames) {
            for (archiveId in archiveIds) {
                archives[archiveId]?.name = buffer.int
            }
        }

        for (archiveId in archiveIds) {
            archives[archiveId]?.crc = buffer.int
        }

        if (hasHashes) {
            for (archiveId in archiveIds) {
                archives[archiveId]?.hash = buffer.int
            }
        }

        if (hasWhirlpools) {
            for (archiveId in archiveIds) {
                val whirlpool = ByteArray(64)
                buffer.get(whirlpool)
                archives[archiveId]?.whirlpool = whirlpool
            }
        }

        if (hasSizes) {
            for (archiveId in archiveIds) {
                val archive = archives[archiveId] ?: continue
                archive.compressedSize = buffer.int
                archive.uncompressedSize = buffer.int
            }
        }

        for (archiveId in archiveIds)
            archives[archiveId]?.version = buffer.int

        val archiveFileIds = Array(archives.size) { IntArray(buffer.getFormatInt(format)) }

        for (i in archiveIds.indices) {
            val archive = archives[archiveIds[i]] ?: continue
            val fileIds = archiveFileIds[i]
            var fileId = 0
            for (j in fileIds.indices) {
                fileId += buffer.getFormatInt(format)
                archive.files[fileId] = ArchiveFile(fileId)
                fileIds[j] = fileId
            }
        }

        if (hasNames) {
            for (i in archiveIds.indices) {
                val archive = archives[archiveIds[i]] ?: continue
                val fileIds = archiveFileIds[i]
                for (j in fileIds.indices) {
                    archive.files[fileIds[j]]?.name = buffer.int
                }
            }
        }
        val remaining = buffer.remaining()
        if (remaining > 0) throw Exception("failed to parse RefTable for $index. too many remaining bytes $remaining")
    }

    fun highestEntry(): Int {
        return if (archives.isEmpty()) {
            0
        } else {
            archives.keys.maxOrNull()?.plus(1) ?: 0
        }
    }

    fun archiveSize(): Int {
        return if (mask and 0x4 != 0) {
            archives.values.sumOf { it.uncompressedSize }
        } else {
            archives.keys.sumOf { key ->
                val data = cache.read(index, key)
                val container = decodeContainer(data)
                return@sumOf container.data.size
            }
        }
    }

    fun totalCompressedSize(): Long = archives.values.sumOf { it.compressedSize.toLong() }

    fun loadArchive(id: Int): Archive {
        val archive = archives[id] ?: throw Exception("archive not found: $id")
        if (archive.loaded) return archive

        val raw = cache.read(index, id)
        val file = ByteBuffer.wrap(decodeContainer(raw).data)
        archive.decode(file)

        return archive
    }
}