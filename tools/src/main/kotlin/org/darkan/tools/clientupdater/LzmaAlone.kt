package org.darkan.tools.clientupdater

import lzma.sdk.lzma.Decoder
import java.io.ByteArrayInputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Decoder for the LZMA "alone" (.lzma) stream format that Jagex serves the NXT client binary in.
 *
 * Layout (verified live against content/world{N}.runescape.com):
 *   [0]      1 byte   LZMA properties (lc/lp/pb), always 0x5D for these downloads
 *   [1..4]   4 bytes  dictionary size, little-endian (0x00800000 = 8 MiB)
 *   [5..12]  8 bytes  uncompressed size, little-endian
 *   [13..]   N bytes  LZMA-compressed stream
 *
 * This mirrors the LZMA branch of [world.gregs.voidps.cache.compress.DecompressionContext] (same
 * `lzma.sdk.lzma.Decoder`) — the only difference is the 5-byte props + 8-byte size live in the file
 * header here, rather than in an RS container header.
 */
object LzmaAlone {
    private const val PROPS_LEN = 5
    private const val SIZE_LEN = 8
    private const val HEADER_LEN = PROPS_LEN + SIZE_LEN

    /** Number of header bytes needed to read the embedded uncompressed size (without decompressing). */
    const val MIN_HEADER = HEADER_LEN

    /** Reads the embedded uncompressed size from an alone-format header (first 13 bytes suffice). */
    fun uncompressedSize(header: ByteArray): Long {
        require(header.size >= HEADER_LEN) { "LZMA header too short: ${header.size} < $HEADER_LEN" }
        return ByteBuffer.wrap(header, PROPS_LEN, SIZE_LEN).order(ByteOrder.LITTLE_ENDIAN).long
    }

    /** Decompresses a complete alone-format stream into the original binary. */
    fun decompress(data: ByteArray): ByteArray {
        require(data.size > HEADER_LEN) { "LZMA stream too short: ${data.size} bytes" }
        val size = uncompressedSize(data)
        require(size in 0..Int.MAX_VALUE.toLong()) { "Unreasonable LZMA uncompressed size: $size" }

        val props = data.copyOfRange(0, PROPS_LEN)
        val decoder = Decoder()
        require(decoder.setDecoderProperties(props)) { "LZMA: bad decoder properties" }

        val out = ByteArray(size.toInt())
        val input = ByteArrayInputStream(data, HEADER_LEN, data.size - HEADER_LEN)
        decoder.code(input, FixedArrayOutputStream(out), size)
        return out
    }

    /** Writes straight into a preallocated array — the decompressed length is known up front. */
    private class FixedArrayOutputStream(private val target: ByteArray) : OutputStream() {
        private var pos = 0
        override fun write(b: Int) {
            target[pos++] = b.toByte()
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            System.arraycopy(b, off, target, pos, len)
            pos += len
        }
    }
}
