package com.undercut.cache

import java.nio.ByteBuffer

class Archive(val id: Int, var name: Int = 0) {
    var crc: Int = 0
    var version: Int = 0
    var whirlpool: ByteArray? = null
    var uncompressedSize: Int = 0
    var compressedSize: Int = 0
    var hash: Int = 0

    var loaded: Boolean = false
    var requiresUpdate: Boolean = false
    val files = mutableMapOf<Int, ArchiveFile>()

    override fun toString(): String {
        return "[$id, $crc, $version, $uncompressedSize, $compressedSize, $hash, ${files.size}]"
    }

    constructor(id: Int) : this(id, 0)

    fun putFile(id: Int, data: ByteArray) {
        requiresUpdate = true
        files[id] = ArchiveFile(id, data, 0)
    }

    fun putFile(file: ArchiveFile) {
        requiresUpdate = true
        files[file.id] = file
    }

    fun decode(buffer: ByteBuffer) {
        loaded = true
        if (files.size == 1) {
            files.values.first().data = buffer.array()
            return
        }

        val first = buffer.get().toInt() and 0xff
        if (first != 1) {
            System.err.println("Invalid first byte (Expected 1): $first")
            return
        }

        val size = files.size
        val ids = files.keys.toIntArray()
        val offsets = IntArray(size + 1) { buffer.int and 0xffffff }

        for (i in ids.indices) {
            val dataSize = offsets[i + 1] - offsets[i]
            val data = ByteArray(dataSize)
            buffer.get(data)
            files[ids[i]]?.data = data
        }
    }
}