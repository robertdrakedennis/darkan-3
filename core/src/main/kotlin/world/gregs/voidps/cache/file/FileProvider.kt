package world.gregs.voidps.cache.file

import io.ktor.utils.io.*
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logTrace
import org.darkan.core.Logger.logWarn
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.file.type.CacheFileProvider
import world.gregs.voidps.cache.file.type.MemoryFileProvider

/**
 * Provides raw cache container data and serves it over the NXT JS5 protocol.
 *
 * NXT response format per file:
 *   index(1) + hash(4) + compression(1) + compressedSize(4) = 10-byte header
 *   Data payload in BLOCK_SIZE (102400) byte blocks.
 *   At each block boundary, a 5-byte continuation header is inserted:
 *     index(1) + hash(4)
 *
 * The hash field is the group ID with the top bit set for prefetch responses.
 */
interface FileProvider {

    fun data(index: Int, archive: Int): ByteArray?

    suspend fun serve(write: ByteWriteChannel, ref: Long, prefetch: Boolean): Boolean {
        val index = (ref ushr 32).toInt()
        val archive = (ref and 0xFFFFFFFFL).toInt()
        val data = data(index, archive)
        if (data == null || data.size < CONTAINER_HEADER_LEN) {
            logWarn("Unable to fulfill request $index $archive $prefetch.")
            return false
        }

        val hash = if (prefetch) archive or (1 shl 31) else archive
        val compression = data[0].toInt()
        val compressedSize = getInt(data[1], data[2], data[3], data[4])

        // Write 10-byte response header
        write.writeByte(index.toByte())
        write.writeInt(hash)
        write.writeByte(compression.toByte())
        write.writeInt(compressedSize)

        // Calculate payload size (data after the 5-byte container header)
        val payloadSize = compressedSize + if (compression != 0) 4 else 0
        val actualPayloadSize = minOf(payloadSize, data.size - CONTAINER_HEADER_LEN)

        // Write payload with NXT block framing
        var blockOffset = RESPONSE_HEADER_LEN // 10 bytes of header already "in" the first block
        var dataPos = CONTAINER_HEADER_LEN    // skip container header (already written above)
        var remaining = actualPayloadSize

        while (remaining > 0) {
            val blockRemaining = BLOCK_SIZE - blockOffset
            val toWrite = minOf(blockRemaining, remaining)
            write.writeFully(data, dataPos, toWrite)
            dataPos += toWrite
            remaining -= toWrite
            blockOffset += toWrite

            if (blockOffset == BLOCK_SIZE && remaining > 0) {
                // Insert continuation header at block boundary
                write.writeByte(index.toByte())
                write.writeInt(hash)
                blockOffset = CONTINUATION_HEADER_LEN
            }
        }

        write.flush()
        logTrace("JS5 served: index=$index archive=$archive prefetch=$prefetch — ${actualPayloadSize + RESPONSE_HEADER_LEN} bytes")
        return true
    }

    companion object {
        fun load(cache: Cache, inMemory: Boolean = true): FileProvider {
            val start = System.currentTimeMillis()
            val provider = if (inMemory) MemoryFileProvider(cache) else CacheFileProvider(cache)
            logInfo("Loaded file provider in ${System.currentTimeMillis() - start}ms")
            return provider
        }

        internal fun getInt(b1: Byte, b2: Byte, b3: Byte, b4: Byte) =
            b1.toInt() shl 24 or (b2.toInt() and 0xff shl 16) or (b3.toInt() and 0xff shl 8) or (b4.toInt() and 0xff)

        /** NXT JS5 block size (bytes per block including headers) */
        internal const val BLOCK_SIZE = 102400
        /** Response header: index(1) + hash(4) + compression(1) + compressedSize(4) */
        internal const val RESPONSE_HEADER_LEN = 10
        /** Continuation header at block boundaries: index(1) + hash(4) */
        internal const val CONTINUATION_HEADER_LEN = 5
        /** Container header in raw data: compression(1) + compressedSize(4) */
        internal const val CONTAINER_HEADER_LEN = 5
    }
}
