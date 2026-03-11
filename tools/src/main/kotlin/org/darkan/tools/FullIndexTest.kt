package org.darkan.tools

import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.Whirlpool
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.zip.CRC32

/**
 * Comprehensive JS5 index verification test.
 *
 * Downloads the master index + ALL archive indices that the real client would request,
 * verifying for each: CRC, Whirlpool, decompression, format=0x07, and LoadIndex parsing
 * (BigSmart, delta-encoded group IDs, file counts, etc.)
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.FullIndexTestKt
 */

private const val HOST = "localhost"
private const val PORT = 43594
private const val MAJOR_VERSION = 946
private const val MINOR_VERSION = 1
private const val JS5_TOKEN = "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK"
private const val BLOCK_SIZE = 102_400
private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5
private const val ENTRY_SIZE = 80

fun main() {
    val socket = Socket(HOST, PORT)
    socket.soTimeout = 15_000
    val output = DataOutputStream(socket.getOutputStream())
    val input = DataInputStream(socket.getInputStream())
    val decomp = DecompressionContext()
    var passed = 0
    var failed = 0

    fun pass(msg: String) { println("  PASS: $msg"); passed++ }
    fun fail(msg: String) { println("  FAIL: $msg"); failed++ }

    try {
        // Handshake
        val tokenBytes = JS5_TOKEN.toByteArray(Charsets.US_ASCII)
        val payloadSize = 4 + 4 + tokenBytes.size + 1 + 1
        output.writeByte(0x0F)
        output.writeByte(payloadSize)
        output.writeInt(MAJOR_VERSION)
        output.writeInt(MINOR_VERSION)
        output.write(tokenBytes)
        output.writeByte(0)
        output.writeByte(0)
        output.flush()

        val sync = input.readByte().toInt() and 0xFF
        if (sync != 0) { fail("SYNC failed: $sync"); return }
        pass("Handshake SYNC")

        // ACK + READY
        output.write(byteArrayOf(0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.write(byteArrayOf(0x03, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.flush()
        pass("ACK + READY")

        // Request master index (255/255)
        sendRequest(output, flags = 0x01, index = 255, group = 255)
        val masterRaw = readResponse(input)
        val masterDecomp = decomp.decompress(masterRaw) ?: run { fail("Master index decompress failed"); return }
        val archiveCount = masterDecomp[0].toInt() and 0xFF
        println("\n=== Master Index: $archiveCount archives ===")
        pass("Master index downloaded (${masterRaw.size} bytes, $archiveCount archives)")

        // Parse master index entries
        data class MasterEntry(val crc: Int, val version: Int, val fileCount: Int, val uncompSize: Int, val whirlpool: ByteArray)
        val entries = mutableMapOf<Int, MasterEntry>()
        for (i in 0 until archiveCount) {
            val off = 1 + i * ENTRY_SIZE
            if (off + ENTRY_SIZE > masterDecomp.size) break
            val crc = readBEInt(masterDecomp, off)
            val ver = readBEInt(masterDecomp, off + 4)
            val fc = readBEInt(masterDecomp, off + 8)
            val us = readBEInt(masterDecomp, off + 12)
            val wp = masterDecomp.copyOfRange(off + 16, off + 16 + 64)
            entries[i] = MasterEntry(crc, ver, fc, us, wp)
        }

        // Test each archive with non-zero CRC
        val activeArchives = entries.filter { it.value.crc != 0 || it.value.version != 0 }
        println("\n=== Testing ${activeArchives.size} active archives ===\n")

        for ((idx, entry) in activeArchives.toSortedMap()) {
            print("Archive $idx: ")

            // Request the archive index
            sendRequest(output, flags = 0x01, index = 255, group = idx)
            val raw: ByteArray
            try {
                raw = readResponse(input)
            } catch (e: Exception) {
                fail("Archive $idx: download failed: ${e.message}")
                continue
            }

            // 1. Verify CRC
            val crc32 = CRC32()
            crc32.update(raw)
            val computedCrc = crc32.value.toInt()
            if (computedCrc != entry.crc) {
                fail("Archive $idx: CRC MISMATCH computed=0x${"%08x".format(computedCrc)} expected=0x${"%08x".format(entry.crc)}")
                continue
            }

            // 2. Verify Whirlpool
            val wp = Whirlpool()
            wp.add(raw)
            val wpOut = ByteArray(64)
            wp.finalize(wpOut)
            val wpMatch = wpOut.contentEquals(entry.whirlpool)

            // 3. Decompress
            val decompressed = decomp.decompress(raw)
            if (decompressed == null) {
                fail("Archive $idx: CRC OK, Whirlpool=${if (wpMatch) "OK" else "MISMATCH"}, decompress FAILED")
                continue
            }

            // 4. Check format byte
            if (decompressed.isEmpty() || decompressed[0].toInt() and 0xFF != 0x07) {
                val fmt = if (decompressed.isEmpty()) "empty" else "0x${"%02x".format(decompressed[0])}"
                fail("Archive $idx: format=$fmt (expected 0x07)")
                continue
            }

            // 5. Read version (bytes 1-4, matches client's gT_uint after format check)
            val dataVersion = readBEInt(decompressed, 1)

            // 6. Parse with client's exact LoadIndex algorithm starting at offset 5
            try {
                val result = parseLoadIndex(decompressed, 5)
                println("CRC OK, WP=${if (wpMatch) "OK" else "MISMATCH"}, ver=$dataVersion/${entry.version}, " +
                    "groups=${result.groupCount}, maxGroupId=${result.maxGroupId}, flags=0x${"%02x".format(result.flags)}, " +
                    "totalFiles=${result.totalFileCount}, bytesLeft=${result.bytesRemaining}")
                if (!wpMatch) {
                    fail("Archive $idx: Whirlpool MISMATCH")
                    println("    Expected: ${entry.whirlpool.toHexString()}")
                    println("    Computed: ${wpOut.toHexString()}")
                } else if (result.bytesRemaining != 0) {
                    fail("Archive $idx: ${result.bytesRemaining} bytes remaining after parse")
                } else if (result.groupCount == 0 && entry.fileCount > 0) {
                    fail("Archive $idx: 0 groups parsed but master says fileCount=${entry.fileCount}")
                } else {
                    pass("Archive $idx")
                }
            } catch (e: Exception) {
                fail("Archive $idx: LoadIndex parse error: ${e.message}")
            }
        }

        println("\n========================================")
        println("=== RESULTS: $passed passed, $failed failed ===")
        println("========================================")
        if (failed > 0) System.exit(1)
    } finally {
        socket.close()
    }
}

/** Parse data using the exact NXT client LoadIndex algorithm. */
private fun parseLoadIndex(data: ByteArray, startOffset: Int): LoadIndexResult {
    var pos = startOffset

    // 1. Flags byte
    val flags = data[pos++].toInt() and 0xFF
    val hasNames = flags and 0x01 != 0
    val hasDigests = flags and 0x02 != 0
    val hasVersions = flags and 0x04 != 0
    val hasUnknown8 = flags and 0x08 != 0

    // 2. Group count (BigSmart)
    val (groupCount, p2) = readBigSmart(data, pos)
    pos = p2

    if (groupCount == 0) {
        return LoadIndexResult(flags, 0, -1, 0, data.size - pos)
    }

    // 3. Group IDs (delta-encoded BigSmart)
    val groupIds = IntArray(groupCount)
    var accum = 0
    var maxGroupId = -1
    for (i in 0 until groupCount) {
        val (delta, np) = readBigSmart(data, pos)
        pos = np
        accum += delta
        groupIds[i] = accum
        if (accum > maxGroupId) maxGroupId = accum
    }

    // 4. Name hashes (if flag & 1)
    if (hasNames) {
        for (i in 0 until groupCount) {
            pos += 4 // int32 name hash
        }
    }

    // 5. CRCs (4 bytes each)
    for (i in 0 until groupCount) {
        pos += 4
    }

    // 6. Unknown8 (if flag & 8, 4 bytes each)
    if (hasUnknown8) {
        for (i in 0 until groupCount) {
            pos += 4
        }
    }

    // 7. Digests (if flag & 2, 64 bytes each)
    if (hasDigests) {
        for (i in 0 until groupCount) {
            pos += 64
        }
    }

    // 8. Sizes (if flag & 4, 8 bytes = compressed + uncompressed)
    if (hasVersions) {
        for (i in 0 until groupCount) {
            pos += 8 // compressed(4) + uncompressed(4)
        }
    }

    // 9. Per-group versions (4 bytes each)
    for (i in 0 until groupCount) {
        pos += 4
    }

    // 10. File counts (BigSmart per group)
    val fileCounts = IntArray(maxGroupId + 1)
    var totalFiles = 0
    for (i in 0 until groupCount) {
        val (fc, np) = readBigSmart(data, pos)
        pos = np
        fileCounts[groupIds[i]] = fc
        totalFiles += fc
    }

    // 11. File IDs (delta-encoded BigSmart per file in each group)
    for (i in 0 until groupCount) {
        val gid = groupIds[i]
        val fc = fileCounts[gid]
        if (fc == 0) continue
        var fileAccum = 0
        var maxFileId = -1
        for (j in 0 until fc) {
            val (delta, np) = readBigSmart(data, pos)
            pos = np
            fileAccum += delta
            if (fileAccum > maxFileId) maxFileId = fileAccum
        }
    }

    // 12. File name hashes (if flag & 1, int per file in each group)
    if (hasNames) {
        for (i in 0 until groupCount) {
            val gid = groupIds[i]
            val fc = fileCounts[gid]
            for (j in 0 until fc) {
                pos += 4
            }
        }
    }

    return LoadIndexResult(flags, groupCount, maxGroupId, totalFiles, data.size - pos)
}

data class LoadIndexResult(
    val flags: Int,
    val groupCount: Int,
    val maxGroupId: Int,
    val totalFileCount: Int,
    val bytesRemaining: Int
)

/** Read BigSmart: peek byte, if high bit set -> read 4 bytes & 0x7FFFFFFF, else read 2 bytes as ushort */
private fun readBigSmart(data: ByteArray, offset: Int): Pair<Int, Int> {
    val peek = data[offset].toInt()
    return if (peek < 0) {
        // High bit set: read 4 bytes, mask off high bit
        val value = readBEInt(data, offset) and 0x7FFFFFFF
        Pair(value, offset + 4)
    } else {
        // Read 2 bytes as unsigned short
        val value = ((data[offset].toInt() and 0xFF) shl 8) or (data[offset + 1].toInt() and 0xFF)
        Pair(value, offset + 2)
    }
}

private fun readBEInt(data: ByteArray, offset: Int): Int =
    ((data[offset].toInt() and 0xFF) shl 24) or
    ((data[offset + 1].toInt() and 0xFF) shl 16) or
    ((data[offset + 2].toInt() and 0xFF) shl 8) or
    (data[offset + 3].toInt() and 0xFF)

private fun sendRequest(output: DataOutputStream, flags: Int, index: Int, group: Int) {
    output.writeByte(flags)
    output.writeByte(index)
    output.writeInt(group)
    output.writeInt(0)
    output.flush()
}

private fun readResponse(input: DataInputStream): ByteArray {
    val respIndex = input.readByte().toInt() and 0xFF
    val respHash = input.readInt()
    val compressionByte = input.readByte().toInt() and 0xFF
    val compressedSize = input.readInt()
    val payloadSize = compressedSize + if (compressionByte != 0) 4 else 0

    val result = ByteArrayOutputStream(5 + payloadSize)
    result.write(compressionByte)
    result.write((compressedSize shr 24) and 0xFF)
    result.write((compressedSize shr 16) and 0xFF)
    result.write((compressedSize shr 8) and 0xFF)
    result.write(compressedSize and 0xFF)

    var bytesRead = 0
    var blockOffset = RESPONSE_HEADER_LEN

    while (bytesRead < payloadSize) {
        val blockRemaining = BLOCK_SIZE - blockOffset
        val toRead = minOf(blockRemaining, payloadSize - bytesRead)
        val buf = ByteArray(toRead)
        input.readFully(buf)
        result.write(buf)
        bytesRead += toRead
        blockOffset += toRead

        if (blockOffset == BLOCK_SIZE && bytesRead < payloadSize) {
            input.readByte() // continuation index
            input.readInt()  // continuation hash
            blockOffset = CONTINUATION_HEADER_LEN
        }
    }

    return result.toByteArray()
}

private fun ByteArray.toHexString(): String = joinToString("") { "%02x".format(it) }
