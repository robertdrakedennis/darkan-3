package com.undercut.cache.type

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.archiveId
import com.undercut.cache.fileId
import java.nio.ByteBuffer
import kotlin.math.pow

abstract class TypeParser<T> {
    private val cached: MutableMap<Int, T> = mutableMapOf()

    abstract fun getIndex(): Index
    abstract fun getArchiveBitSize(): Int
    abstract fun decode(id: Int, buffer: ByteBuffer): T

    open fun getArchiveId(id: Int): Int {
        return archiveId(id, getArchiveBitSize())
    }

    open fun getFileId(id: Int): Int {
        return fileId(id, getArchiveBitSize())
    }

    open fun getMaxId(): Int {
        val table = Cache.get().getReferenceTable(getIndex())
        val maxArchive = table.highestEntry() - 1
        val files = table.archives[maxArchive]?.files?.keys?.reduce { _, second -> second } ?: return 0
        return maxArchive * (2.0.pow(getArchiveBitSize()).toInt()) + files
    }

    fun list(): Map<Int, T> {
        for (i in 0..getMaxId()) {
            val def = get(Cache.get(), i)
            if (def != null) {
                cached[i] = def
            }
        }
        return cached
    }

    fun get(cache: Cache, id: Int): T {
        cached[id]?.let { return it }

        val archive = cache.getArchive(getIndex(), getArchiveId(id))
        val file = archive.files[getFileId(id)] ?: error("file not found: index: ${getIndex()} archiveId: ${getArchiveId(id)} fileId: ${getFileId(id)}")

        val buffer = ByteBuffer.wrap(file.data)
        val def = decode(id, buffer)
        if (def != null)
            cached[id] = def
        return def
    }
}