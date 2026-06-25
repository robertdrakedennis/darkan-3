package com.undercut.cache

import java.nio.ByteBuffer

class Container(
    var data: ByteArray,
    var compression: Compression = Compression.ZLIB
)

fun decodeContainer(data: ByteBuffer): Container {
    if (!data.hasRemaining()) throw IllegalArgumentException("Provided non-readable (empty?) buffer")

    val compression = Compression.forData(data.array())
    val compressed = data.array()

    if (compression == null)
        throw IllegalArgumentException("Unknown compression type")
    val payloadSize = getPayloadSize(compressed, compression)
    val headerSize = getHeaderSize(compression)
    val decompressed = when (compression) {
        Compression.NONE -> compressed.copyOfRange(headerSize, headerSize + payloadSize)
        Compression.BZIP -> bzip2Decomp(compressed.copyOfRange(headerSize, headerSize + payloadSize))
        Compression.GZIP -> gzipDecomp(compressed.copyOfRange(headerSize, headerSize + payloadSize))
        Compression.ZLIB -> zlibDecomp(compressed)
    }

    return Container(decompressed, compression)
}
