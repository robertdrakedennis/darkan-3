package org.darkan.tools

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.Socket

/**
 * Wire byte dump tool for JS5 debugging.
 *
 * Connects to the running lobby server on localhost:43594, performs the JS5
 * handshake, requests 255/255 (master index), and dumps the raw wire bytes
 * to capture/local-255-255.bin. Also hex dumps first/last bytes and compares
 * against what the cache would produce locally.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.WireDumpKt
 */

private const val HOST = "localhost"
private const val BLOCK_SIZE = 102_400
private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5

fun main() {
    val port = EnvVars.lobbyPort
    val majorVersion = EnvVars.majorVersion
    val minorVersion = EnvVars.minorVersion
    val token = EnvVars.js5ServerToken

    println("=".repeat(70))
    println("=== Wire Dump: JS5 Master Index (255/255) ===")
    println("=".repeat(70))
    println("Connecting to $HOST:$port ...")

    val socket = Socket(HOST, port)
    socket.soTimeout = 15_000
    val output = DataOutputStream(socket.getOutputStream())
    val input = DataInputStream(socket.getInputStream())

    try {
        // =====================================================================
        // Step 2: JS5 Handshake
        // =====================================================================
        println("\n--- Handshake ---")
        val tokenBytes = token.toByteArray(Charsets.US_ASCII)
        val payloadSize = 4 + 4 + tokenBytes.size + 1 + 1  // major + minor + token + null + platform

        // Send: 0x0F opcode + size(1B) + major(4B) + minor(4B) + token(N+null) + platform(1B)
        output.writeByte(0x0F)
        output.writeByte(payloadSize)
        output.writeInt(majorVersion)
        output.writeInt(minorVersion)
        output.write(tokenBytes)
        output.writeByte(0)  // null terminator
        output.writeByte(0)  // platform byte
        output.flush()

        println("Sent handshake: opcode=0x0F, size=$payloadSize, version=$majorVersion.$minorVersion, token=${token.take(20)}...")

        // Read 1-byte response
        val syncByte = input.readByte().toInt() and 0xFF
        println("Response byte: 0x${"%02x".format(syncByte)} (${if (syncByte == 0) "SYNC/success" else "REJECTED"})")
        if (syncByte != 0) {
            println("FATAL: Server rejected handshake with code $syncByte")
            return
        }

        // =====================================================================
        // Step 3: ACK + CONNECTION_READY
        // =====================================================================
        println("\n--- ACK + CONNECTION_READY ---")
        // ACK (opcode 6)
        output.write(byteArrayOf(0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        // CONNECTION_READY (opcode 3)
        output.write(byteArrayOf(0x03, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.flush()
        println("Sent ACK + CONNECTION_READY (20 bytes)")

        // =====================================================================
        // Step 5: Request 255/255
        // =====================================================================
        println("\n--- Requesting 255/255 (master index) ---")
        // flags=0x01 (urgent, not prefetch), index=255, group=255 as int32, padding=4 zeros
        output.writeByte(0x01)       // flags: urgent
        output.writeByte(0xFF)       // index: 255
        output.writeInt(0xFF)        // group: 255
        output.writeInt(0)           // padding
        output.flush()
        println("Sent request: flags=0x01, index=255, group=255")

        // =====================================================================
        // Step 6: Read full response and dump to file
        // =====================================================================
        println("\n--- Reading response ---")

        // Read 10-byte response header
        val rawCapture = ByteArrayOutputStream(8192)

        val respIndex = input.readByte()
        val respHash0 = input.readByte()
        val respHash1 = input.readByte()
        val respHash2 = input.readByte()
        val respHash3 = input.readByte()
        val compressionByte = input.readByte()
        val cs0 = input.readByte()
        val cs1 = input.readByte()
        val cs2 = input.readByte()
        val cs3 = input.readByte()

        // Write the 10-byte header to capture
        rawCapture.write(byteArrayOf(respIndex, respHash0, respHash1, respHash2, respHash3,
            compressionByte, cs0, cs1, cs2, cs3))

        val respIndexVal = respIndex.toInt() and 0xFF
        val respHashVal = ((respHash0.toInt() and 0xFF) shl 24) or
            ((respHash1.toInt() and 0xFF) shl 16) or
            ((respHash2.toInt() and 0xFF) shl 8) or
            (respHash3.toInt() and 0xFF)
        val compressionVal = compressionByte.toInt() and 0xFF
        val compressedSize = ((cs0.toInt() and 0xFF) shl 24) or
            ((cs1.toInt() and 0xFF) shl 16) or
            ((cs2.toInt() and 0xFF) shl 8) or
            (cs3.toInt() and 0xFF)

        println("Response header:")
        println("  index: $respIndexVal (expected 255)")
        println("  hash: 0x${"%08x".format(respHashVal)} (group=${respHashVal and 0x7FFFFFFF}, prefetch=${(respHashVal.toLong() and 0x80000000L) != 0L})")
        println("  compression: $compressionVal")
        println("  compressedSize: $compressedSize")

        val payloadLen = compressedSize + if (compressionVal != 0) 4 else 0
        println("  payload to read: $payloadLen bytes")

        // Read payload with block framing
        var bytesRead = 0
        var blockOffset = RESPONSE_HEADER_LEN

        while (bytesRead < payloadLen) {
            val blockRemaining = BLOCK_SIZE - blockOffset
            val toRead = minOf(blockRemaining, payloadLen - bytesRead)
            val buf = ByteArray(toRead)
            input.readFully(buf)
            rawCapture.write(buf)
            bytesRead += toRead
            blockOffset += toRead

            if (blockOffset == BLOCK_SIZE && bytesRead < payloadLen) {
                // Read continuation header (5 bytes) and include in capture
                val contHdr = ByteArray(5)
                input.readFully(contHdr)
                rawCapture.write(contHdr)
                blockOffset = CONTINUATION_HEADER_LEN
                println("  (continuation header at payload offset $bytesRead)")
            }
        }

        val capturedBytes = rawCapture.toByteArray()
        println("\nTotal captured: ${capturedBytes.size} bytes (header + payload + continuations)")

        // =====================================================================
        // Step 7: Hex dumps
        // =====================================================================
        println("\n--- Hex Dump ---")
        val first50 = capturedBytes.take(50).joinToString(" ") { "%02x".format(it) }
        val last20 = capturedBytes.takeLast(20).joinToString(" ") { "%02x".format(it) }
        println("First 50 bytes: $first50")
        println("Last 20 bytes:  $last20")

        // =====================================================================
        // Step 8: Parse container from wire data
        // =====================================================================
        println("\n--- Container Parse (from wire) ---")
        // Strip the 10-byte response header to get the container
        // But we also need to strip continuation headers from the payload
        val container = ByteArrayOutputStream(payloadLen + 5)
        // Write compression + compressedSize (from response header bytes 5-9)
        container.write(compressionByte.toInt() and 0xFF)
        container.write(cs0.toInt() and 0xFF)
        container.write(cs1.toInt() and 0xFF)
        container.write(cs2.toInt() and 0xFF)
        container.write(cs3.toInt() and 0xFF)

        // Now extract just the payload data (skip response header, strip continuation headers)
        var srcPos = RESPONSE_HEADER_LEN  // skip 10-byte response header
        var written = 0
        blockOffset = RESPONSE_HEADER_LEN
        while (written < payloadLen) {
            val blockRemaining = BLOCK_SIZE - blockOffset
            val toWrite = minOf(blockRemaining, payloadLen - written)
            container.write(capturedBytes, srcPos, toWrite)
            srcPos += toWrite
            written += toWrite
            blockOffset += toWrite

            if (blockOffset == BLOCK_SIZE && written < payloadLen) {
                srcPos += CONTINUATION_HEADER_LEN  // skip continuation header
                blockOffset = CONTINUATION_HEADER_LEN
            }
        }

        val containerBytes = container.toByteArray()
        println("Container size: ${containerBytes.size} bytes")

        if (containerBytes.size >= 6) {
            val wireArchiveCount = containerBytes[5].toInt() and 0xFF
            println("Archive count from wire: $wireArchiveCount")
        }

        // =====================================================================
        // Step 9: Compare against local cache
        // =====================================================================
        println("\n--- Comparison with Local Cache ---")
        try {
            val cache = SQLiteCache.load()
            val localVT = cache.versionTable
            println("Local versionTable: ${localVT.size} bytes")
            println("Wire container: ${containerBytes.size} bytes")

            if (localVT.contentEquals(containerBytes)) {
                println("  MATCH: Wire container is byte-for-byte identical to local versionTable")
            } else {
                println("  MISMATCH!")
                println("  Local size: ${localVT.size}")
                println("  Wire size:  ${containerBytes.size}")
                val minLen = minOf(localVT.size, containerBytes.size)
                var diffCount = 0
                for (i in 0 until minLen) {
                    if (localVT[i] != containerBytes[i]) {
                        if (diffCount == 0) {
                            println("  First difference at byte $i: local=0x${"%02x".format(localVT[i])} wire=0x${"%02x".format(containerBytes[i])}")
                        }
                        diffCount++
                    }
                }
                if (localVT.size != containerBytes.size) {
                    diffCount += Math.abs(localVT.size - containerBytes.size)
                }
                println("  Total differing bytes: $diffCount")
            }
        } catch (e: Exception) {
            println("  Could not load local cache for comparison: ${e.message}")
        }

        // =====================================================================
        // Save to file
        // =====================================================================
        val captureDir = File("capture")
        captureDir.mkdirs()
        val outFile = File(captureDir, "local-255-255.bin")
        outFile.writeBytes(capturedBytes)
        println("\nRaw wire capture saved to: ${outFile.absolutePath} (${capturedBytes.size} bytes)")

        // Also save just the container (without wire framing)
        val containerFile = File(captureDir, "local-255-255-container.bin")
        containerFile.writeBytes(containerBytes)
        println("Container (no framing) saved to: ${containerFile.absolutePath} (${containerBytes.size} bytes)")

    } finally {
        socket.close()
        println("\nConnection closed.")
    }
}
