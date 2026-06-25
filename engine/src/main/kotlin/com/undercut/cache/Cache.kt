package com.undercut.cache

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Cache private constructor() {

    private val checkedReferenceTables = BooleanArray(255)
    private val cachedReferenceTables = arrayOfNulls<RefTable>(255)
    private var indices: Array<IndexFile?> = arrayOfNulls(0)

    init {
        try {
            if (!Files.exists(PATH)) {
                Files.createDirectories(PATH)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        indices = arrayOfNulls(255)
        for (i in indices.indices) {
            if (Files.exists(PATH.resolve("js5-$i.jcache"))) {
                indices[i] = IndexFile(PATH.resolve("js5-$i.jcache"))
            }
        }
    }

    fun getReferenceTable(index: Index): RefTable {
        return getReferenceTable(index.id, ignoreChecked = false)
    }

    fun getReferenceTable(index: Int, ignoreChecked: Boolean): RefTable {
        cachedReferenceTables[index]?.let { return it }

        if (ignoreChecked) {
            if (checkedReferenceTables[index]) throw Exception("reference table not checked: index: $index")
            checkedReferenceTables[index] = true
        }

        val table = RefTable(this, index)
        val container = readReferenceTable(index)
        val data = ByteBuffer.wrap(decodeContainer(container).data)
        table.decode(data)
        cachedReferenceTables[index] = table
        return table
    }

    fun exists(index: Int, archive: Int): Boolean {
        if (index !in indices.indices) throw IndexOutOfBoundsException("Index out of bounds: $index")
        return indices[index]?.exists(archive) == true
    }

    fun read(index: Int, archive: Int): ByteBuffer {
        if (index !in indices.indices) throw IndexOutOfBoundsException("Index out of bounds: $index")
        val result = indices[index]?.getRaw(archive) ?: throw IOException("Data not found")
        return ByteBuffer.wrap(result)
    }

    fun read(index: Int, name: String): ByteBuffer {
        val table = getReferenceTable(index, ignoreChecked = false)
        val hash = name.toFilesystemHash()
        val id = table.archives.values.firstOrNull { it.name == hash }?.id ?: throw Exception("failed to get id from name: index: $index name: $name")
        return read(index, id)
    }

    fun readReferenceTable(index: Int): ByteBuffer {
        if (index !in indices.indices) throw IndexOutOfBoundsException("Index out of bounds: $index")
        val table = indices[index]?.getRawTable() ?: throw Exception("raw reftable unavailable: index: $index")
        return ByteBuffer.wrap(table)
    }

    fun numIndices(): Int {
        return indices.size
    }

    fun getArchive(index: Index, id: Int): Archive {
        val table = getReferenceTable(index)
        return table.loadArchive(id)
    }

    companion object {
        private val PATH: Path = resolveCacheDir()

        /**
         * Resolve the live NXT client cache dir. `RS_CACHE_DIR` (exported by the darkan launcher,
         * chosen per server mode — official vs custom) is authoritative; otherwise probe the known
         * launcher locations and pick the first that actually holds a cache. The injected engine
         * runs inside rs2client whose HOME the launcher redirects to its data dir, so the cache
         * lands at `$HOME/Jagex/RuneScape`. The resolved dir is logged so a wrong/empty path is
         * obvious on inject.
         */
        private fun resolveCacheDir(): Path {
            System.getenv("RS_CACHE_DIR")?.let {
                println("[Cache] cache dir = $it (RS_CACHE_DIR)")
                return Paths.get(it)
            }
            val home = System.getProperty("user.home")
            val candidates = listOf(
                "$home/Jagex/RuneScape",                              // launcher redirects HOME -> its data dir
                "$home/.local/share/darkan-launcher/Jagex/RuneScape", // data dir resolved from the real HOME
            )
            val chosen = candidates.firstOrNull { dir ->
                Files.isDirectory(Paths.get(dir)) && (0..255).any { Files.exists(Paths.get(dir, "js5-$it.jcache")) }
            } ?: candidates.first()
            println("[Cache] cache dir = $chosen (probed; RS_CACHE_DIR unset)")
            return Paths.get(chosen)
        }

        @Volatile
        private var instance: Cache? = null

        fun get(): Cache {
            return instance ?: synchronized(this) {
                instance ?: Cache().also { instance = it }
            }
        }
    }
}