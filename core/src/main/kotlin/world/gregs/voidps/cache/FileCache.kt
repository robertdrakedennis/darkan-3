package world.gregs.voidps.cache

import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.ReadOnlyCache.Companion.INDEX_SIZE
import world.gregs.voidps.cache.ReadOnlyCache.Companion.SECTOR_DATA_SIZE_BIG
import world.gregs.voidps.cache.ReadOnlyCache.Companion.SECTOR_DATA_SIZE_SMALL
import world.gregs.voidps.cache.ReadOnlyCache.Companion.SECTOR_HEADER_SIZE_BIG
import world.gregs.voidps.cache.ReadOnlyCache.Companion.SECTOR_HEADER_SIZE_SMALL
import world.gregs.voidps.cache.ReadOnlyCache.Companion.SECTOR_SIZE
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.secure.VersionTableBuilder
import world.gregs.voidps.cache.secure.Whirlpool
import java.io.File
import java.io.RandomAccessFile
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.ConcurrentHashMap

/**
 * [Cache] which reads data directly from file.
 * Thread-safe for concurrent reads from multiple threads.
 * Average read speeds, fast loading and low but variable memory usage.
 */
class FileCache(
    private val main: RandomAccessFile,
    private val index255: RandomAccessFile,
    private val indexes: Array<RandomAccessFile?>,
    indexCount: Int,
    val xteas: Map<Int, IntArray>?
) : ReadOnlyCache(indexCount) {

    private val mainChannel: FileChannel = main.channel
    private val index255Channel: FileChannel = index255.channel
    private val indexChannels: Array<FileChannel?> = indexes.map { it?.channel }.toTypedArray()

    private val dataCache = ConcurrentHashMap<Int, Array<ByteArray?>>()
    private val sectorCache = ConcurrentHashMap<Int, ByteArray?>()
    private val length = main.length()
    private val context = ThreadLocal.withInitial { DecompressionContext() }
    private val indexCrcs by lazy {
        indices.map {
            val data = sector(255, it) ?: return@map 0
            CRC.calculate(data, 0, data.size)
        }.toIntArray()
    }

    override fun indexCrcs() = indexCrcs

    override fun sector(index: Int, archive: Int): ByteArray? {
        val key = index + (archive shl 6)
        sectorCache[key]?.let { return it }

        val indexChannel = if (index == 255) index255Channel else indexChannels[index] ?: return null
        val result = readSectorThreadSafe(mainChannel, length, indexChannel, index, archive) ?: return null
        sectorCache[key] = result
        return result
    }

    override fun data(index: Int, archive: Int, file: Int, xtea: IntArray?): ByteArray? {
        if (index >= files.size) {
            return null
        }
        val archives = files[index]
        if (archives == null || archive >= archives.size) {
            return null
        }
        val fileIds = archives[archive] ?: return null
        val matchingIndex = fileIds.indexOf(file)
        if (matchingIndex == -1) {
            return null
        }

        // If XTEA key is provided directly, use it; otherwise fall back to the class field map
        // For MAPS index, create a temporary map with the archive ID as key
        val effectiveXteas = if (xtea != null && index == Index.MAPS) {
            mapOf(archive to xtea)
        } else {
            xteas
        }

        val hash = index + (archive shl 6) + (if (xtea != null) xtea.contentHashCode() else 0)
        val cached = dataCache[hash]
        if (cached != null) return cached[matchingIndex]

        // Thread-safe: each thread gets its own DecompressionContext and we use positional FileChannel reads
        val indexChannel = indexChannels[index] ?: return null
        val sectorData = readSectorThreadSafe(mainChannel, length, indexChannel, index, archive) ?: return null
        val ctx = context.get()
        val keys = if (effectiveXteas != null && index == Index.MAPS) effectiveXteas[archive] else null
        val decompressed = ctx.decompress(sectorData, keys) ?: return null

        val fileCount = fileCounts[index]?.getOrNull(archive) ?: return null
        val result = if (fileCount == 1) {
            val fileId = fileIds.last()
            Array(fileId + 1) {
                if (it == fileId) decompressed else null
            }
        } else {
            parseMultiFileArchive(decompressed, fileCount)
        }

        dataCache[hash] = result
        return result[matchingIndex]
    }

    /**
     * Parses a multi-file archive from decompressed data into individual file byte arrays.
     * The archive format stores chunk counts and per-file sizes at the end of the data,
     * with the actual file data packed sequentially from the beginning.
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseMultiFileArchive(decompressed: ByteArray, fileCount: Int): Array<ByteArray?> {
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

    override fun close() {
        mainChannel.close()
        index255Channel.close()
        indexChannels.forEach { it?.close() }
        main.close()
        index255.close()
        for (file in indexes) {
            file?.close()
        }
    }

    companion object : CacheLoader {
        const val CACHE_FILE_NAME = "main_file_cache"

        operator fun invoke(path: String, exponent: BigInteger? = null, modulus: BigInteger? = null, xteas: Map<Int, IntArray>? = null): Cache {
            return load(path, exponent, modulus, xteas)
        }

        /**
         * Reads a sector using positional [FileChannel] reads, which are thread-safe
         * and do not affect or depend on the shared file position.
         */
        private fun readSectorThreadSafe(
            mainChannel: FileChannel,
            mainLength: Long,
            indexChannel: FileChannel,
            indexId: Int,
            sectorId: Int
        ): ByteArray? {
            if (mainLength < INDEX_SIZE.toLong() * sectorId + INDEX_SIZE) {
                return null
            }

            val indexBuffer = ByteBuffer.allocate(INDEX_SIZE)
            val bytesRead = indexChannel.read(indexBuffer, sectorId.toLong() * INDEX_SIZE)
            if (bytesRead < INDEX_SIZE) return null
            indexBuffer.flip()

            val bigSector = sectorId > 65535
            val sectorHeaderSize = if (bigSector) SECTOR_HEADER_SIZE_BIG else SECTOR_HEADER_SIZE_SMALL
            val sectorDataSize = if (bigSector) SECTOR_DATA_SIZE_BIG else SECTOR_DATA_SIZE_SMALL

            val sectorSize = ((indexBuffer.get().toInt() and 0xFF) shl 16) or
                ((indexBuffer.get().toInt() and 0xFF) shl 8) or
                (indexBuffer.get().toInt() and 0xFF)
            var sectorPosition = ((indexBuffer.get().toInt() and 0xFF) shl 16) or
                ((indexBuffer.get().toInt() and 0xFF) shl 8) or
                (indexBuffer.get().toInt() and 0xFF)

            if (sectorSize < 0 || sectorPosition <= 0 || sectorPosition > mainLength / SECTOR_SIZE) {
                return null
            }

            val output = ByteArray(sectorSize)
            var read = 0
            var chunk = 0
            val sectorBuffer = ByteBuffer.allocate(SECTOR_SIZE)

            while (read < sectorSize) {
                if (sectorPosition == 0) return null

                var requiredToRead = sectorSize - read
                if (requiredToRead > sectorDataSize) {
                    requiredToRead = sectorDataSize
                }

                sectorBuffer.clear()
                sectorBuffer.limit(requiredToRead + sectorHeaderSize)
                val mainBytesRead = mainChannel.read(sectorBuffer, sectorPosition.toLong() * SECTOR_SIZE)
                if (mainBytesRead < requiredToRead + sectorHeaderSize) return null
                sectorBuffer.flip()

                val id = if (bigSector) {
                    sectorBuffer.int
                } else {
                    ((sectorBuffer.get().toInt() and 0xFF) shl 8) or (sectorBuffer.get().toInt() and 0xFF)
                }
                val sectorChunk = ((sectorBuffer.get().toInt() and 0xFF) shl 8) or (sectorBuffer.get().toInt() and 0xFF)
                val sectorNextPosition = ((sectorBuffer.get().toInt() and 0xFF) shl 16) or
                    ((sectorBuffer.get().toInt() and 0xFF) shl 8) or
                    (sectorBuffer.get().toInt() and 0xFF)
                val sectorIndex = sectorBuffer.get().toInt() and 0xFF

                if (sectorIndex != indexId || id != sectorId || sectorChunk != chunk) {
                    return null
                } else if (sectorNextPosition < 0 || sectorNextPosition > mainLength / SECTOR_SIZE) {
                    return null
                }

                sectorBuffer.get(output, read, requiredToRead)
                read += requiredToRead
                sectorPosition = sectorNextPosition
                chunk++
            }
            return output
        }

        /**
         * Create [RandomAccessFile]'s for each index file, load only the archive data into memory
         */
        override fun load(
            path: String,
            mainFile: File,
            main: RandomAccessFile,
            index255File: File,
            index255: RandomAccessFile,
            indexCount: Int,
            versionTable: VersionTableBuilder?,
            xteas: Map<Int, IntArray>?,
            threadUsage: Double
        ): Cache {
            val length = mainFile.length()
            val context = DecompressionContext()
            val indices = Array(indexCount) { indexId ->
                val file = File(path, "${CACHE_FILE_NAME}.idx$indexId")
                if (file.exists()) RandomAccessFile(file, "r") else null
            }
            val whirlpool = Whirlpool()
            val cache = FileCache(main, index255, indices, indexCount, xteas)
            for (indexId in 0 until indexCount) {
                cache.archiveData(context, main, length, index255, indexId, versionTable, whirlpool)
            }
            cache.versionTable = versionTable?.build(whirlpool) ?: ByteArray(0)
            return cache
        }
    }
}
