package org.darkan.tools

import world.gregs.voidps.cache.compress.DecompressionContext
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.zip.CRC32

/**
 * JS5 integration test — connects to localhost:43594, performs the full NXT JS5
 * handshake, requests the master index (255/255) and an archive index (255/2),
 * then verifies CRC and revision fields match.
 *
 * Also requests a non-255 group (index=2, group=1) to verify data serving.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.JS5IntegrationTestKt
 */

private const val HOST = "localhost"
private const val PORT = 43594
private const val MAJOR_VERSION = 947
private const val MINOR_VERSION = 1
private const val JS5_TOKEN = "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK"

// NXT JS5 framing constants
private const val BLOCK_SIZE = 102_400
private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5

// Master index entry layout
private const val ENTRY_SIZE = 80

fun main() {
    val socket = Socket(HOST, PORT)
    socket.soTimeout = 10_000
    val output = DataOutputStream(socket.getOutputStream())
    val input = DataInputStream(socket.getInputStream())
    val decompressionContext = DecompressionContext()
    var passed = 0
    var failed = 0

    fun pass(msg: String) { println("  PASS: $msg"); passed++ }
    fun fail(msg: String) { println("  FAIL: $msg"); failed++ }

    try {
        // ── Step 1: Send JS5 handshake ──
        println("=== Step 1: Sending JS5 handshake ===")
        val tokenBytes = JS5_TOKEN.toByteArray(Charsets.US_ASCII)
        // Payload: major(4) + minor(4) + token(N) + null(1) + platform(1)
        val payloadSize = 4 + 4 + tokenBytes.size + 1 + 1
        output.writeByte(0x0F) // JS5_INIT opcode
        output.writeByte(payloadSize)
        output.writeInt(MAJOR_VERSION)
        output.writeInt(MINOR_VERSION)
        output.write(tokenBytes)
        output.writeByte(0) // null terminator for RS string
        output.writeByte(0) // platform byte
        output.flush()
        println("  Sent: opcode=0x0F, size=$payloadSize, version=$MAJOR_VERSION.$MINOR_VERSION, token=$JS5_TOKEN")

        // ── Step 2: Read SYNC response ──
        println("\n=== Step 2: Reading SYNC response ===")
        val syncResponse = input.readByte().toInt() and 0xFF
        println("  Response byte: $syncResponse (expected 0 = SYNC)")
        if (syncResponse != 0) {
            fail("Expected SYNC (0), got $syncResponse")
            return
        }
        pass("Got SYNC")

        // ── Step 3: Send ACK + READY ──
        println("\n=== Step 3: Sending ACK + READY ===")
        // ACK: opcode=6, medium=0x000005, int=0x000003B2, short=0x0000
        val ack = byteArrayOf(0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00)
        output.write(ack)
        // READY: opcode=3, medium=0x000005, int=0x000003B2, short=0x0000
        val ready = byteArrayOf(0x03, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00)
        output.write(ready)
        output.flush()
        println("  Sent ACK (${ack.toHex()}) and READY (${ready.toHex()})")
        pass("ACK + READY sent")

        // ── Step 4: Request master index (255/255) ──
        println("\n=== Step 4: Requesting master index (255/255) ===")
        sendFileRequest(output, flags = 0x21, index = 255, group = 255)

        // ── Step 5: Read master index response ──
        println("\n=== Step 5: Reading master index response ===")
        val masterRaw = readJS5Response(input)
        println("  Raw container size: ${masterRaw.size} bytes")
        println("  First 20 bytes: ${masterRaw.take(20).toByteArray().toHex()}")
        if (masterRaw.size > 10) {
            pass("Received master index (${masterRaw.size} bytes)")
        } else {
            fail("Master index too small (${masterRaw.size} bytes)")
        }

        // ── Step 6: Parse master index ──
        println("\n=== Step 6: Parsing master index ===")
        // Container header: compression(1) + compressedSize(4)
        val compression = masterRaw[0].toInt() and 0xFF
        val compressedSize = readBEInt(masterRaw, 1)
        println("  Compression: $compression, compressedSize: $compressedSize")

        // Decompress master index container
        val masterDecompressed = decompressionContext.decompress(masterRaw)
        if (masterDecompressed == null) {
            fail("Failed to decompress master index")
            return
        }

        val archiveCount = masterDecompressed[0].toInt() and 0xFF
        println("  Archive count: $archiveCount")

        data class MasterEntry(val crc: Int, val version: Int, val fileCount: Int, val uncompSize: Int)

        val entries = mutableMapOf<Int, MasterEntry>()
        for (i in 0 until archiveCount) {
            val offset = 1 + i * ENTRY_SIZE
            if (offset + ENTRY_SIZE > masterDecompressed.size) {
                println("  WARN: Entry $i would exceed data bounds (offset=$offset, dataLen=${masterDecompressed.size})")
                break
            }
            val crc = readBEInt(masterDecompressed, offset)
            val version = readBEInt(masterDecompressed, offset + 4)
            val fileCount = readBEInt(masterDecompressed, offset + 8)
            val uncompSize = readBEInt(masterDecompressed, offset + 12)
            entries[i] = MasterEntry(crc, version, fileCount, uncompSize)
            if (crc != 0 || version != 0) {
                println("  Entry[$i]: crc=$crc, version=$version, fileCount=$fileCount, uncompSize=$uncompSize")
            }
        }
        pass("Parsed $archiveCount master index entries")

        // ── Step 7: Request archive 2 index (255/2) ──
        println("\n=== Step 7: Requesting archive 2 index (255/2) ===")
        sendFileRequest(output, flags = 0x21, index = 255, group = 2)

        // ── Step 8: Read archive 2 index response ──
        println("\n=== Step 8: Reading archive 2 index response ===")
        val archive2Raw = readJS5Response(input)
        println("  Raw container size: ${archive2Raw.size} bytes")
        println("  First 20 bytes: ${archive2Raw.take(20).toByteArray().toHex()}")

        // ── Step 9: Verify CRC of archive 2 ──
        println("\n=== Step 9: Verifying CRC of archive 2 ===")
        val crc32 = CRC32()
        crc32.update(archive2Raw)
        val computedCrc = crc32.value.toInt()
        val expectedCrc = entries[2]?.crc
        println("  Computed CRC32: $computedCrc (0x${"%08x".format(computedCrc)})")
        println("  Master index CRC: $expectedCrc (0x${if (expectedCrc != null) "%08x".format(expectedCrc) else "N/A"})")
        if (expectedCrc != null && computedCrc == expectedCrc) {
            pass("CRC matches for archive 2!")
        } else {
            fail("CRC mismatch for archive 2! computed=$computedCrc expected=$expectedCrc")
        }

        // ── Step 10: Decompress archive 2 and verify revision ──
        println("\n=== Step 10: Decompressing archive 2 and verifying revision ===")
        val archive2Decompressed = decompressionContext.decompress(archive2Raw)
        if (archive2Decompressed == null) {
            fail("Failed to decompress archive 2")
        } else {
            println("  Decompressed size: ${archive2Decompressed.size} bytes")
            if (archive2Decompressed.isNotEmpty()) {
                val formatVersion = archive2Decompressed[0].toInt() and 0xFF
                println("  Format version: $formatVersion (expected 7 for modern RS3)")
                if (formatVersion == 7) {
                    pass("Format version is 7")
                } else {
                    fail("Format version is $formatVersion, expected 7")
                }
                if (archive2Decompressed.size >= 5) {
                    val revision = readBEInt(archive2Decompressed, 1)
                    val expectedVersion = entries[2]?.version
                    println("  Revision from data: $revision")
                    println("  Master index version: $expectedVersion")
                    if (expectedVersion != null && revision == expectedVersion) {
                        pass("Revision matches for archive 2!")
                    } else {
                        fail("Revision mismatch! data=$revision master=$expectedVersion")
                    }
                }
            }
        }

        // ── Step 11: Request a non-255 group (index=2, group=1) ──
        // Note: group 0 doesn't exist in index 2; first valid group is 1
        println("\n=== Step 11: Requesting config data (index=2, group=1) ===")
        sendFileRequest(output, flags = 0x01, index = 2, group = 1)

        println("\n=== Step 12: Reading config data response ===")
        val configRaw = readJS5Response(input)
        println("  Raw container size: ${configRaw.size} bytes")
        println("  First 20 bytes: ${configRaw.take(20).toByteArray().toHex()}")
        val configCompression = configRaw[0].toInt() and 0xFF
        val configCompressedSize = readBEInt(configRaw, 1)
        println("  Compression type: $configCompression")
        println("  Compressed size: $configCompressedSize")
        if (configRaw.size >= 5 + configCompressedSize) {
            pass("Received valid container for index=2, group=1 (${configRaw.size} bytes)")
            // Try decompressing to verify data integrity
            val configDecompressed = decompressionContext.decompress(configRaw)
            if (configDecompressed != null) {
                println("  Decompressed config data: ${configDecompressed.size} bytes")
                pass("Config data decompresses successfully")
            } else {
                fail("Config data failed to decompress")
            }
        } else {
            fail("Container too small for index=2, group=1")
        }

        // ── Summary ──
        println("\n========================================")
        println("=== RESULTS: $passed passed, $failed failed ===")
        println("========================================")
        if (failed > 0) {
            System.exit(1)
        }
    } finally {
        socket.close()
    }
}

/** Send a 10-byte JS5 file request: flags(1) + index(1) + group(4 BE) + padding(4) */
private fun sendFileRequest(output: DataOutputStream, flags: Int, index: Int, group: Int) {
    output.writeByte(flags)
    output.writeByte(index)
    output.writeInt(group)
    output.writeInt(0) // 4 bytes padding
    output.flush()
    println("  Sent request: flags=0x${"%02x".format(flags)}, index=$index, group=$group")
}

/**
 * Read a full JS5 response from the stream, stripping NXT block framing
 * (continuation headers) to produce the raw container bytes.
 *
 * Response format:
 *   index(1) + hash(4 BE) + compression(1) + compressedSize(4 BE) + payload...
 *   Block framing: every [BLOCK_SIZE] bytes, a 5-byte continuation header
 *   (index(1) + hash(4)) is inserted and must be stripped.
 *
 * Returns the raw container: compression(1) + compressedSize(4) + [decompressedSize(4)] + compressedData
 */
private fun readJS5Response(input: DataInputStream): ByteArray {
    // Read the 10-byte response header
    val respIndex = input.readByte().toInt() and 0xFF
    val respHash = input.readInt()
    val compressionByte = input.readByte().toInt() and 0xFF
    val compressedSize = input.readInt()

    println("  Response header: index=$respIndex, hash=0x${"%08x".format(respHash)}, compression=$compressionByte, compressedSize=$compressedSize")

    // Total payload after the 5-byte container header = compressedSize + (4 if compressed for decompressedSize field)
    val payloadSize = compressedSize + if (compressionByte != 0) 4 else 0

    // Reconstruct the raw container bytes (container header + payload)
    val result = ByteArrayOutputStream(5 + payloadSize)
    // Write the 5-byte container header: compression(1) + compressedSize(4 BE)
    result.write(compressionByte)
    result.write((compressedSize shr 24) and 0xFF)
    result.write((compressedSize shr 16) and 0xFF)
    result.write((compressedSize shr 8) and 0xFF)
    result.write(compressedSize and 0xFF)

    var bytesRead = 0
    // We've consumed the 10-byte response header from the first block already
    var blockOffset = RESPONSE_HEADER_LEN

    while (bytesRead < payloadSize) {
        val blockRemaining = BLOCK_SIZE - blockOffset
        val toRead = minOf(blockRemaining, payloadSize - bytesRead)
        val buf = ByteArray(toRead)
        input.readFully(buf)
        result.write(buf)
        bytesRead += toRead
        blockOffset += toRead

        // At a block boundary, read and discard the 5-byte continuation header
        if (blockOffset == BLOCK_SIZE && bytesRead < payloadSize) {
            val contIndex = input.readByte().toInt() and 0xFF
            val contHash = input.readInt()
            println("  Continuation header at payload offset $bytesRead: index=$contIndex, hash=0x${"%08x".format(contHash)}")
            blockOffset = CONTINUATION_HEADER_LEN
        }
    }

    return result.toByteArray()
}

private fun readBEInt(data: ByteArray, offset: Int): Int {
    return ((data[offset].toInt() and 0xFF) shl 24) or
            ((data[offset + 1].toInt() and 0xFF) shl 16) or
            ((data[offset + 2].toInt() and 0xFF) shl 8) or
            (data[offset + 3].toInt() and 0xFF)
}

private fun ByteArray.toHex(): String = joinToString(" ") { "%02x".format(it) }
