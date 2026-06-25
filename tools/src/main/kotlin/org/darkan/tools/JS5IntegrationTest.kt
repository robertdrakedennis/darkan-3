package org.darkan.tools

import org.darkan.tools.cachedownloader.JS5Protocol
import org.darkan.tools.util.toHex
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext
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
// Lobby/JS5 port. NOTE: the mac split-port setup uses .env LOBBY_PORT=43596;
// point this at 43596 (not the legacy 43594) when testing that running server.
private const val PORT = 43596
private const val MAJOR_VERSION = 948
private const val MINOR_VERSION = 1
private const val JS5_TOKEN = "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK"

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
        println("  Sending: opcode=0x0F, version=$MAJOR_VERSION.$MINOR_VERSION, token=$JS5_TOKEN")

        // ── Step 2: Read SYNC response ──
        println("\n=== Step 2: Reading SYNC response ===")
        val syncResponse = JS5Protocol.handshake(output, input, MAJOR_VERSION, MINOR_VERSION, JS5_TOKEN)
        println("  Response byte: $syncResponse (expected 0 = SYNC)")
        if (syncResponse != 0) {
            fail("Expected SYNC (0), got $syncResponse")
            return
        }
        pass("Got SYNC")

        // ── Step 3: Send ACK + READY ──
        println("\n=== Step 3: Sending ACK + READY ===")
        JS5Protocol.sendConnectionInit(output, MAJOR_VERSION)
        pass("ACK + READY sent")

        // ── Step 4: Request master index (255/255) ──
        println("\n=== Step 4: Requesting master index (255/255) ===")
        sendFileRequest(output, flags = 0x21, index = 255, group = 255)

        // ── Step 5: Read master index response ──
        println("\n=== Step 5: Reading master index response ===")
        val masterRaw = readJS5Response(input)
        println("  Raw container size: ${masterRaw.size} bytes")
        println("  First 20 bytes: ${masterRaw.copyOfRange(0, minOf(20, masterRaw.size)).toHex()}")
        if (masterRaw.size > 10) {
            pass("Received master index (${masterRaw.size} bytes)")
        } else {
            fail("Master index too small (${masterRaw.size} bytes)")
        }

        // ── Step 6: Parse master index ──
        println("\n=== Step 6: Parsing master index ===")
        // Container header: compression(1) + compressedSize(4)
        val headerReader = BufferReader(masterRaw)
        val compression = headerReader.readUnsignedByte()
        val compressedSize = headerReader.readInt()
        println("  Compression: $compression, compressedSize: $compressedSize")

        // Decompress master index container
        val masterDecompressed = decompressionContext.decompress(masterRaw)
        if (masterDecompressed == null) {
            fail("Failed to decompress master index")
            return
        }

        val masterReader = BufferReader(masterDecompressed)
        val archiveCount = masterReader.readUnsignedByte()
        println("  Archive count: $archiveCount")

        data class MasterEntry(val crc: Int, val version: Int, val fileCount: Int, val uncompSize: Int)

        val entries = mutableMapOf<Int, MasterEntry>()
        for (i in 0 until archiveCount) {
            if (1 + (i + 1) * ENTRY_SIZE > masterDecompressed.size) {
                println("  WARN: Entry $i would exceed data bounds (dataLen=${masterDecompressed.size})")
                break
            }
            val crc = masterReader.readInt()
            val version = masterReader.readInt()
            val fileCount = masterReader.readInt()
            val uncompSize = masterReader.readInt()
            masterReader.skip(64) // whirlpool
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
        println("  First 20 bytes: ${archive2Raw.copyOfRange(0, minOf(20, archive2Raw.size)).toHex()}")

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
                val archiveReader = BufferReader(archive2Decompressed)
                val formatVersion = archiveReader.readUnsignedByte()
                println("  Format version: $formatVersion (expected 7 for modern RS3)")
                if (formatVersion == 7) {
                    pass("Format version is 7")
                } else {
                    fail("Format version is $formatVersion, expected 7")
                }
                if (archive2Decompressed.size >= 5) {
                    val revision = archiveReader.readInt()
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
        println("  First 20 bytes: ${configRaw.copyOfRange(0, minOf(20, configRaw.size)).toHex()}")
        if (!JS5Protocol.hasGroupVersionSuffix(2, configRaw)) {
            pass("Config response is a bare JS5 container")
        } else {
            fail("Config response includes a 2-byte group version suffix")
        }
        val configHeaderReader = BufferReader(configRaw)
        val configCompression = configHeaderReader.readUnsignedByte()
        val configCompressedSize = configHeaderReader.readInt()
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

/** Send a 10-byte JS5 file request via the canonical protocol implementation. */
private fun sendFileRequest(output: DataOutputStream, flags: Int, index: Int, group: Int) {
    JS5Protocol.sendFileRequest(output, index, group, major = 0, flags = flags)
    println("  Sent request: flags=0x${"%02x".format(flags)}, index=$index, group=$group")
}

/**
 * Read a full JS5 response via the canonical block-framing reader, logging the
 * response/continuation headers as they arrive.
 */
private fun readJS5Response(input: DataInputStream): ByteArray {
    val response = JS5Protocol.readResponse(
        input,
        onHeader = { index, hash, compression, compressedSize ->
            println("  Response header: index=$index, hash=0x${"%08x".format(hash)}, compression=$compression, compressedSize=$compressedSize")
        },
        onContinuation = { payloadOffset, contIndex, contHash ->
            println("  Continuation header at payload offset $payloadOffset: index=$contIndex, hash=0x${"%08x".format(contHash)}")
        },
    )
    return response.container
}
