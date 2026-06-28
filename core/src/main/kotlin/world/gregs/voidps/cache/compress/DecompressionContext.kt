package world.gregs.voidps.cache.compress

import lzma.sdk.lzma.Decoder
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.secure.Xtea
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.Inflater

/**
 * Context per thread for decompressing data in parallel
 */
class DecompressionContext : AutoCloseable {
    private val gzipInflater = Inflater(true)
    private val bzip2Compressor: BZIP2Compressor by lazy { BZIP2Compressor() }
    private val lzmaDecoder: Decoder by lazy { Decoder() }

    /** Releases the native [Inflater] resources held by this context. */
    override fun close() {
        gzipInflater.end()
    }

    fun decompress(data: ByteArray, keys: IntArray? = null): ByteArray? {
        // Check for ZLIB magic (ZLB) before standard container format
        if (data.size >= 3 && data[0] == 0x5A.toByte() && data[1] == 0x4C.toByte() && data[2] == 0x42.toByte()) {
            return decompressZlib(data)
        }

        if (keys != null && (keys[0] != 0 || keys[1] != 0 || keys[2] != 0 || 0 != keys[3])) {
            Xtea.decipher(data, keys, 5)
        }
        val buffer = BufferReader(data)
        val type = buffer.readUnsignedByte()
        val compressedSize = buffer.readInt()
        var decompressedSize = 0
        if (type != 0) {
            decompressedSize = buffer.readInt()
        }
        when (type) {
            NONE -> {
                val decompressed = ByteArray(compressedSize)
                buffer.readBytes(decompressed, 0, compressedSize)
                return decompressed
            }
            BZIP2 -> {
                if (!warned.get()) {
                    logWarn("BZIP2 Compression found - replace to improve read performance.")
                    warned.set(true)
                }
                val decompressed = ByteArray(decompressedSize)
                bzip2Compressor.decompress(decompressed, decompressedSize, data, 9)
                return decompressed
            }
            GZIP -> {
                val offset = buffer.position()
                if (buffer.readByte() != 31 || buffer.readByte() != -117) {
                    return null
                }
                return try {
                    val decompressed = ByteArray(decompressedSize)
                    gzipInflater.setInput(data, offset + 10, data.size - (offset + 18))
                    var count = 0
                    while (count < decompressedSize && !gzipInflater.finished()) {
                        val inflated = gzipInflater.inflate(decompressed, count, decompressedSize - count)
                        if (inflated == 0) {
                            break
                        }
                        count += inflated
                    }
                    if (count != decompressedSize) {
                        logWarn("GZIP size mismatch: expected $decompressedSize bytes, inflated $count.")
                    }
                    decompressed
                } catch (exception: Exception) {
                    logWarn("Error decompressing gzip data.")
                    null
                } finally {
                    gzipInflater.reset()
                }
            }
            LZMA -> {
                val decompressed = ByteArray(decompressedSize)
                decompress(data, buffer.position(), decompressed, decompressedSize)
                return decompressed
            }
        }
        return null
    }

    private fun decompress(compressed: ByteArray, propsOffset: Int, decompressed: ByteArray, decompressedLength: Int) {
        // LZMA properties are 5 bytes at propsOffset (offset 9 in the container).
        // Extract them into a separate array for setDecoderProperties.
        val props = ByteArray(5)
        System.arraycopy(compressed, propsOffset, props, 0, 5)
        if (!lzmaDecoder.setDecoderProperties(props)) {
            logError("LZMA: Bad properties.")
            return
        }
        // Compressed stream starts after the 5-byte properties header
        val streamOffset = propsOffset + 5
        val input = ByteArrayInputStream(compressed, streamOffset, compressed.size - streamOffset)
        val output = ByteArrayWrapperOutputStream(decompressed)
        lzmaDecoder.code(input, output, decompressedLength.toLong())
    }

    private class ByteArrayWrapperOutputStream(private val byteArray: ByteArray) : OutputStream() {
        private var position = 0

        override fun write(b: Int) {
            byteArray[position++] = b.toByte()
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            System.arraycopy(b, off, byteArray, position, len)
            position += len
        }

        override fun flush() {
        }

        override fun close() {
        }
    }

    /**
     * Decodes the NXT client's on-disk group blob (`jag::Js5DiskCache::DecodeStoredBlob`,
     * rs2client 948-5 @ 0x100808e00). The client wraps every group it writes to its own
     * `js5-N.jcache` SQLite cache in a ZLB header followed by a complete RFC1950 zlib stream:
     *
     * ```
     * offset 0: [3] magic 'Z' 'L' 'B'  (0x5A 0x4C 0x42)
     * offset 3: [1] version            (0x01)
     * offset 4: [4] uncompressedSize   (u32 BIG-ENDIAN)
     * offset 8: [N] zlib stream        (0x78 0x9C … + 4-byte adler32; no extra trailer)
     * ```
     *
     * The previous implementation read the size at offset 3 (consuming the version byte as the
     * high byte), yielding a ~16 MB bogus size and an all-zero buffer — which then made every
     * multi-file group split into empty files. The size is BIG-ENDIAN and sits at offset 4, after
     * the version byte; the deflate stream begins at offset 8.
     *
     * Authoritative format: `re-resources/docs/cache/sqlite-disk-blob-format.md` §1.
     */
    private fun decompressZlib(data: ByteArray): ByteArray? {
        if (data.size <= ZLB_HEADER) {
            logWarn("ZLB blob too small: ${data.size} bytes.")
            return null
        }
        val buffer = BufferReader(data)
        buffer.skip(3) // 'Z' 'L' 'B'
        buffer.skip(1) // version byte (0x01)
        val decompressedSize = buffer.readInt() // u32 big-endian
        val offset = ZLB_HEADER // zlib stream starts at offset 8
        return try {
            val decompressed = ByteArray(decompressedSize)
            val inflater = Inflater()
            inflater.setInput(data, offset, data.size - offset)
            var count = 0
            while (count < decompressedSize && !inflater.finished()) {
                val inflated = inflater.inflate(decompressed, count, decompressedSize - count)
                if (inflated == 0) {
                    break
                }
                count += inflated
            }
            inflater.end()
            if (count != decompressedSize) {
                logWarn("ZLB size mismatch: expected $decompressedSize bytes, inflated $count.")
            }
            decompressed
        } catch (e: Exception) {
            logWarn("Error decompressing ZLIB data.")
            null
        }
    }

    companion object {
        private const val NONE = 0
        private const val BZIP2 = 1
        private const val GZIP = 2
        private const val LZMA = 3

        /** ZLB on-disk blob header size: 3 (magic) + 1 (version) + 4 (BE uncompressed size). */
        private const val ZLB_HEADER = 8
        private val warned = AtomicBoolean()
    }
}