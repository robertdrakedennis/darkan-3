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
    private val hashes: MutableMap<Int, Int> = Int2IntOpenHashMap(32767)

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

    override fun indexCount() = _indices.size

    override fun indices() = _indices

    override fun indexCrcs() = indexCrcs

    override fun archives(index: Int) = archives.getOrNull(index) ?: IntArray(0)

    override fun archiveCount(index: Int) = archives.getOrNull(index)?.size ?: 0

    override fun lastArchiveId(indexId: Int) = archives.getOrNull(indexId)?.lastOrNull() ?: -1

    override fun archiveId(index: Int, hash: Int) = hashes[hash] ?: -1

    override fun files(index: Int, archive: Int) = files.getOrNull(index)?.getOrNull(archive) ?: IntArray(0)

    override fun fileCount(indexId: Int, archiveId: Int) = fileCounts.getOrNull(indexId)?.getOrNull(archiveId) ?: 0

    override fun lastFileId(indexId: Int, archive: Int) = files.getOrNull(indexId)?.getOrNull(archive)?.lastOrNull() ?: -1

    override fun data(index: Int, archive: Int, file: Int, xtea: IntArray?): ByteArray? {
        val raw = sector(index, archive) ?: return null
        val context = DecompressionContext()
        val keys = if (xtea != null && index == Index.MAPS) xtea else null
        val decompressed = context.decompress(raw, keys) ?: return null

        val fileCount = fileCounts.getOrNull(index)?.getOrNull(archive) ?: return null
        val fileIds = files.getOrNull(index)?.getOrNull(archive) ?: return null

        if (fileCount <= 1) {
            return if (file == 0 || fileIds.contains(file)) decompressed else null
        }

        val matchingIndex = fileIds.indexOf(file)
        if (matchingIndex == -1) return null

        val archiveFiles = parseMultiFileArchive(decompressed, fileCount) ?: return null
        return archiveFiles.getOrNull(matchingIndex)
    }

    private fun parseMultiFileArchive(decompressed: ByteArray, fileCount: Int): Array<ByteArray?>? {
        if (decompressed.isEmpty()) return null
        val first = decompressed[0].toInt() and 0xFF

        // Modern format: leading 1 byte + (N+1) 24-bit offsets + file data
        if (first == 1) {
            return parseModernMultiFile(decompressed, fileCount)
        }

        // Legacy format: trailing chunk count + int deltas at end
        return parseLegacyMultiFile(decompressed, fileCount)
    }

    private fun parseModernMultiFile(decompressed: ByteArray, fileCount: Int): Array<ByteArray?> {
        val reader = BufferReader(decompressed)
        reader.readByte() // skip the leading 1

        val offsets = IntArray(fileCount + 1) { reader.readUnsignedMedium() }
        val files = Array<ByteArray?>(fileCount) { i ->
            val size = offsets[i + 1] - offsets[i]
            val data = ByteArray(size)
            reader.readBytes(data, 0, size)
            data
        }
        return files
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
                        indexFiles[indexId] = IndexFile(file)
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
            val context = DecompressionContext()
            val whirlpool = Whirlpool()

            for (indexId in cache._indices) {
                try {
                    cache.parseRefTable(context, indexId, versionTable, whirlpool)
                } catch (e: Exception) {
                    logWarn("Failed to parse ref table for index $indexId: ${e.message}")
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
                for (i in 0 until archiveCount) {
                    hashes[reader.readInt()] = archiveIds[i]
                }
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
