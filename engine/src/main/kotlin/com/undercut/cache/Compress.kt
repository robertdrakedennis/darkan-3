package com.undercut.cache

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

enum class Compression(vararg val header: Byte) {
    NONE(0),
    BZIP(1),
    GZIP(2),
    ZLIB('Z'.code.toByte(), 'L'.code.toByte(), 'B'.code.toByte());

    companion object {
        fun forData(data: ByteArray): Compression? {
            return Compression.entries.find { c ->
                if (data.size < c.header.size) {
                    false
                } else {
                    c.header.indices.all { i -> data[i] == c.header[i] }
                }
            }
        }
    }
}

fun getHeaderSize(compression: Compression): Int {
    return when (compression) {
        Compression.NONE -> 5
        Compression.GZIP -> 9
        Compression.BZIP -> 9
        Compression.ZLIB -> 0
    }
}


fun getPayloadSize(header: ByteArray, compression: Compression): Int {
    return when (compression) {
        Compression.NONE,
        Compression.GZIP,
        Compression.BZIP -> {
            (header[1].toInt() and 0xFF shl 24) or
                    (header[2].toInt() and 0xFF shl 16) or
                    (header[3].toInt() and 0xFF shl 8) or
                    (header[4].toInt() and 0xFF)
        }
        Compression.ZLIB -> header.size
    }
}

fun gzipDecomp(compressed: ByteArray): ByteArray {
    return GZIPInputStream(ByteArrayInputStream(compressed)).use { it.readBytes() }
}

fun bzip2Decomp(compressed: ByteArray): ByteArray {
    val fixed = ByteArray(compressed.size + 4)
    System.arraycopy(compressed, 0, fixed, 4, compressed.size)
    fixed[0] = 'B'.code.toByte()
    fixed[1] = 'Z'.code.toByte()
    fixed[2] = 'h'.code.toByte()
    fixed[3] = '1'.code.toByte()

    BZip2CompressorInputStream(ByteArrayInputStream(fixed)).use { inputStream ->
        return inputStream.readBytes()
    }
}

fun zlibDecomp(compressed: ByteArray): ByteArray {
    val inputStream = InflaterInputStream(ByteArrayInputStream(compressed.copyOfRange(8, compressed.size)))
    val outputStream = ByteArrayOutputStream()

    inputStream.use { input ->
        outputStream.use { output ->
            shovelInToOut(input, output)
            return output.toByteArray()
        }
    }
}

private fun shovelInToOut(input: InputStream, output: OutputStream) {
    val buffer = ByteArray(1000)
    var len: Int
    while (input.read(buffer).also { len = it } > 0) {
        output.write(buffer, 0, len)
    }
}
