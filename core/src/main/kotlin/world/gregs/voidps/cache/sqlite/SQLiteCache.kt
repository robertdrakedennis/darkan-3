package world.gregs.voidps.cache.sqlite

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.secure.VersionTableBuilder
import world.gregs.voidps.cache.secure.Whirlpool
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SQLiteCache private constructor(
    private val indexFiles: Array<IndexFile?>,
    indexCount: Int
) : Cache {

    private val _indices: IntArray
    private val archives: Array<IntArray?> = arrayOfNulls(indexCount)
    private val fileCounts: Array<IntArray?> = arrayOfNulls(indexCount)
    private val files: Array<Array<IntArray?>?> = arrayOfNulls(indexCount)

    /** Name-hash -> archive id lookups, keyed per index to avoid cross-index hash collisions. */
    private val hashes: Array<Int2IntOpenHashMap?> = arrayOfNulls(indexCount)

    /** Single-entry memo of the last decompressed multi-file group (see [data]). */
    private val groupLock = Any()
    private var groupIndex = -1
    private var groupArchive = -1
    private var groupFiles: Array<ByteArray?>? = null

    override lateinit var versionTable: ByteArray

    private val indexCrcs by lazy {
        _indices.map {
            val data = sector(255, it) ?: return@map 0
            CRC.calculate(data, 0, data.size)
        }.toIntArray()
    }

    init {
        val validIndices = mutableListOf<Int>()
        for (i in 0 until indexCount) {
            if (indexFiles[i] != null && indexFiles[i]!!.hasReferenceTable()) {
                validIndices.add(i)
            }
        }
        _indices = validIndices.toIntArray()
    }

    override fun sector(index: Int, archive: Int): ByteArray? {
        if (index == 255) {
            if (archive >= indexFiles.size) return null
            return indexFiles[archive]?.getRawTable()
        }
        if (index >= indexFiles.size) return null
        return indexFiles[index]?.getRaw(archive)
    }

    override fun sectorSize(index: Int, archive: Int): Int {
        if (index == 255) {
            if (archive >= indexFiles.size) return -1
            return indexFiles[archive]?.getRawTable()?.size ?: -1
        }
        if (index >= indexFiles.size) return -1
        return indexFiles[index]?.getLength(archive) ?: -1
    }

    override fun sectorVersion(index: Int, archive: Int): Int {
        if (index == 255) {
            if (archive >= indexFiles.size) return 0
            return indexFiles[archive]?.getRawTableVersion() ?: 0
        }
        if (index >= indexFiles.size) return 0
        return indexFiles[index]?.getVersion(archive) ?: 0
    }

    override fun indexCount() = _indices.size

    override fun indices() = _indices

    override fun indexCrcs() = indexCrcs

    override fun archives(index: Int) = archives.getOrNull(index) ?: IntArray(0)

    override fun archiveCount(index: Int) = archives.getOrNull(index)?.size ?: 0

    override fun lastArchiveId(indexId: Int) = archives.getOrNull(indexId)?.lastOrNull() ?: -1

    override fun archiveId(index: Int, hash: Int) = hashes.getOrNull(index)?.get(hash) ?: -1

    override fun files(index: Int, archive: Int) = files.getOrNull(index)?.getOrNull(archive) ?: IntArray(0)

    override fun fileCount(indexId: Int, archiveId: Int) = fileCounts.getOrNull(indexId)?.getOrNull(archiveId) ?: 0

    override fun lastFileId(indexId: Int, archive: Int) = files.getOrNull(indexId)?.getOrNull(archive)?.lastOrNull() ?: -1

    override fun data(index: Int, archive: Int, file: Int, xtea: IntArray?): ByteArray? {
        val keys = if (xtea != null && index == Index.MAPS) xtea else null
        val fileCount = fileCounts.getOrNull(index)?.getOrNull(archive) ?: return null
        val fileIds = files.getOrNull(index)?.getOrNull(archive) ?: return null

        if (fileCount <= 1) {
            if (file != 0 && !fileIds.contains(file)) return null
            val raw = sector(index, archive) ?: return null
            return decompressionContexts.get().decompress(raw, keys)
        }

        val matchingIndex = fileIds.indexOf(file)
        if (matchingIndex == -1) return null

        // Encrypted groups (XTEA map data) are never memoized - retries with different
        // keys must re-read and re-decipher the raw container.
        if (keys != null) {
            val raw = sector(index, archive) ?: return null
            val decompressed = decompressionContexts.get().decompress(raw, keys) ?: return null
            val archiveFiles = parseMultiFileArchive(decompressed, fileCount) ?: return null
            return archiveFiles.getOrNull(matchingIndex)
        }

        // Single-entry memo: definition loading requests files of the same group
        // sequentially, so decompress and split each group only once instead of
        // once per file (256-file groups for items/npcs/objects).
        synchronized(groupLock) {
            if (groupIndex != index || groupArchive != archive || groupFiles == null) {
                val raw = sector(index, archive) ?: return null
                val decompressed = decompressionContexts.get().decompress(raw) ?: return null
                val archiveFiles = parseMultiFileArchive(decompressed, fileCount) ?: return null
                groupFiles = archiveFiles
                groupIndex = index
                groupArchive = archive
            }
            return groupFiles?.getOrNull(matchingIndex)
        }
    }

    private fun parseMultiFileArchive(decompressed: ByteArray, fileCount: Int): Array<ByteArray?>? {
        if (decompressed.isEmpty()) return null

        // The RS3 NXT cache uses the trailing-stripe (chunked) group layout: file data, then a
        // size-delta table, then a 1-byte chunk count at the very end. A leading 0x01 byte is NOT
        // a format marker here - it is simply the first byte of file data, so the old
        // "first byte == 1 => modern 24-bit offset table" heuristic misfired on every such group
        // (NegativeArraySizeException / BufferUnderflow). We only take the modern path when its
        // leading offset table is self-consistent, otherwise fall back to trailing-stripe.
        parseModernMultiFile(decompressed, fileCount)?.let { return it }

        return parseLegacyMultiFile(decompressed, fileCount)
    }

    /**
     * Modern "smart" group layout: leading version byte (1) + (N+1) 24-bit offsets + file data.
     * Returns null (rather than throwing) when the offset table is not self-consistent, so the
     * caller can fall back to the trailing-stripe layout used by the RS3 NXT cache.
     */
    private fun parseModernMultiFile(decompressed: ByteArray, fileCount: Int): Array<ByteArray?>? {
        // Need: 1 version byte + (fileCount + 1) * 3 offset bytes.
        val headerSize = 1 + (fileCount + 1) * 3
        if (decompressed.size < headerSize) return null
        if ((decompressed[0].toInt() and 0xFF) != 1) return null

        val reader = BufferReader(decompressed)
        reader.readByte() // version byte
        val offsets = IntArray(fileCount + 1) { reader.readUnsignedMedium() }

        // Validate: first offset is the header end, offsets are non-decreasing, and the last
        // offset is exactly the buffer length (file data fills the remainder). Any deviation
        // means this is not the modern layout.
        if (offsets[0] != headerSize) return null
        for (i in 1..fileCount) {
            if (offsets[i] < offsets[i - 1]) return null
        }
        if (offsets[fileCount] != decompressed.size) return null

        return Array(fileCount) { i ->
            val size = offsets[i + 1] - offsets[i]
            val data = ByteArray(size)
            reader.readBytes(data, 0, size)
            data
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseLegacyMultiFile(decompressed: ByteArray, fileCount: Int): Array<ByteArray?> {
        val reader = BufferReader(decompressed)
        val rawArray = reader.array()
        var fileDataSizesOffset = decompressed.size
        val chunkSize: Int = rawArray[--fileDataSizesOffset].toInt() and 0xFF
        fileDataSizesOffset -= chunkSize * (fileCount * 4)
        val offsets = IntArray(fileCount)
        reader.position(fileDataSizesOffset)
        for (i in 0 until chunkSize) {
            var previousLength = 0
            for (fileIndex in 0 until fileCount) {
                previousLength += reader.readInt()
                offsets[fileIndex] += previousLength
            }
        }
        val archiveFiles = Array(fileCount) { index ->
            val array = ByteArray(offsets[index])
            offsets[index] = 0
            array
        }
        var offset = 0
        reader.position(fileDataSizesOffset)
        for (i in 0 until chunkSize) {
            var length = 0
            for (fileIndex in 0 until fileCount) {
                val readLen = reader.readInt()
                val fileData = archiveFiles[fileIndex]
                length += readLen
                System.arraycopy(rawArray, offset, fileData, offsets[fileIndex], length)
                offset += length
                offsets[fileIndex] += length
            }
        }
        return archiveFiles as Array<ByteArray?>
    }

    override fun write(index: Int, archive: Int, file: Int, data: ByteArray, xteas: IntArray?) {
        throw UnsupportedOperationException("Write not yet supported for SQLite cache.")
    }

    override fun write(index: Int, archive: String, data: ByteArray, xteas: IntArray?) {
        throw UnsupportedOperationException("Write not yet supported for SQLite cache.")
    }

    override fun update(): Boolean = false

    override fun close() {
        for (indexFile in indexFiles) {
            indexFile?.close()
        }
    }

    companion object {
        private const val NAME_FLAG = 0x1
        private const val WHIRLPOOL_FLAG = 0x2
        private const val WHIRLPOOL_SIZE = 64

        /** One decompression context (native Inflater etc.) per thread - never per call. */
        private val decompressionContexts: ThreadLocal<DecompressionContext> =
            ThreadLocal.withInitial { DecompressionContext() }

        fun load(): Cache {
            val path = Paths.get(EnvVars.cachePath)
            return load(
                path,
                BigInteger(EnvVars.js5RsaExponent),
                BigInteger(EnvVars.js5RsaModulus)
            )
        }

        fun load(
            path: Path,
            exponent: BigInteger? = null,
            modulus: BigInteger? = null
        ): Cache {
            if (!Files.exists(path)) {
                throw IllegalArgumentException("Cache directory not found: $path")
            }

            // Scan for js5-*.jcache files
            val indexFiles = arrayOfNulls<IndexFile>(256)
            var maxIndex = -1
            Files.list(path).use { stream ->
                stream.filter { it.fileName.toString().matches(Regex("js5-\\d+\\.jcache")) }
                    .forEach { file ->
                        val indexId = file.fileName.toString()
                            .removePrefix("js5-")
                            .removeSuffix(".jcache")
                            .toInt()
                        indexFiles[indexId] = try {
                            IndexFile(file)
                        } catch (e: Exception) {
                            logWarn("Skipping unreadable cache index $indexId ($file): ${e.message}", e)
                            null
                        }
                        if (indexId != 255 && indexId > maxIndex) maxIndex = indexId
                    }
            }

            if (maxIndex == -1) {
                throw IllegalArgumentException("No .jcache files found in $path")
            }

            val indexCount = maxIndex + 1
            val versionTable = if (exponent != null && modulus != null) {
                VersionTableBuilder(exponent, modulus, indexCount)
            } else null

            val cache = SQLiteCache(indexFiles, indexCount)
            val whirlpool = Whirlpool()

            DecompressionContext().use { context ->
                for (indexId in cache._indices) {
                    try {
                        cache.parseRefTable(context, indexId, versionTable, whirlpool)
                    } catch (e: Exception) {
                        logWarn("Failed to parse ref table for index $indexId: ${e.message}")
                    }
                }
            }

            cache.versionTable = versionTable?.build(whirlpool) ?: ByteArray(0)

            return cache
        }

        private fun SQLiteCache.parseRefTable(
            context: DecompressionContext,
            indexId: Int,
            versionTable: VersionTableBuilder?,
            whirlpool: Whirlpool
        ) {
            val rawTable = indexFiles[indexId]?.getRawTable()
            if (rawTable == null) {
                logTrace("No ref table for index $indexId")
                versionTable?.skip(indexId)
                return
            }

            versionTable?.sector(indexId, rawTable, whirlpool)
            val decompressed = context.decompress(rawTable) ?: return
            versionTable?.uncompressedSize(indexId, decompressed.size)

            val reader = BufferReader(decompressed)
            val version = reader.readUnsignedByte()
            if (version < 5 || version > 7) {
                throw RuntimeException("Unknown ref table version: $version for index $indexId")
            }
            if (version >= 6) {
                val revision = reader.readInt()
                versionTable?.revision(indexId, revision)
            }
            val flags = reader.readUnsignedByte()
            val archiveCount = if (version >= 7) reader.readBigSmart() else reader.readUnsignedShort()
            var previous = 0
            var highest = 0
            val archiveIds = IntArray(archiveCount) {
                val archiveId = if (version >= 7)
                    reader.readBigSmart() + previous
                else
                    reader.readUnsignedShort() + previous
                previous = archiveId
                if (archiveId > highest) highest = archiveId
                archiveId
            }
            archives[indexId] = archiveIds
            // fileCount in the master index is used by the client to size tracking arrays
            // indexed by group ID. It must be the highest group ID + 1 (array size),
            // NOT the number of groups (which can be much smaller for sparse IDs).
            versionTable?.fileCount(indexId, highest + 1)

            if (flags and NAME_FLAG != 0) {
                val indexHashes = Int2IntOpenHashMap(archiveCount)
                indexHashes.defaultReturnValue(-1)
                for (i in 0 until archiveCount) {
                    indexHashes[reader.readInt()] = archiveIds[i]
                }
                hashes[indexId] = indexHashes
            }
            // CRCs
            reader.skip(archiveCount * 4)
            // Hashes (flag 0x8)
            if (flags and 0x8 != 0) reader.skip(archiveCount * 4)
            // Whirlpool
            if (flags and WHIRLPOOL_FLAG != 0) {
                for (i in 0 until archiveCount) reader.skip(WHIRLPOOL_SIZE)
            }
            // Sizes (flag 0x4)
            if (flags and 0x4 != 0) reader.skip(archiveCount * 8)
            // Revisions
            reader.skip(archiveCount * 4)

            val archiveSizes = IntArray(highest + 1)
            for (i in 0 until archiveCount) {
                val id = archiveIds[i]
                val size = if (version >= 7) reader.readBigSmart() else reader.readUnsignedShort()
                archiveSizes[id] = size
            }
            fileCounts[indexId] = archiveSizes
            val fileIds = arrayOfNulls<IntArray>(highest + 1)
            files[indexId] = fileIds
            for (i in 0 until archiveCount) {
                var fileId = 0
                val archiveId = archiveIds[i]
                val fileCount = archiveSizes[archiveId]
                fileIds[archiveId] = IntArray(fileCount) {
                    fileId += if (version >= 7) reader.readBigSmart() else reader.readUnsignedShort()
                    fileId
                }
            }
            if (flags and NAME_FLAG != 0) {
                for (i in 0 until archiveCount) {
                    val archiveId = archiveIds[i]
                    val fileCount = archiveSizes[archiveId]
                    for (j in 0 until fileCount) {
                        reader.readInt()
                    }
                }
            }
        }
    }
}
