package org.darkan.tools

import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.net.URL

/**
 * Live JS5 capture: connects to both Jagex live servers and our local server,
 * downloads the master index from each, and dumps raw wire bytes for comparison.
 *
 * This is the "ground truth" tool for debugging JS5 response format differences.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.LiveJS5CaptureKt
 */

private const val BLOCK_SIZE = 102_400
private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5
private const val ENTRY_SIZE = 80

fun main() {
    // =====================================================================
    // Step 1: Fetch live jav_config.ws
    // =====================================================================
    println("=" .repeat(70))
    println("=== Step 1: Fetching live jav_config.ws ===")
    println("=" .repeat(70))

    val configUrl = "https://www.runescape.com/k=5/l=0/jav_config.ws?binaryType=4"
    val config: Map<String, String>
    val params: Map<Int, String>
    try {
        val configText = URL(configUrl).readText()
        config = mutableMapOf<String, String>()
        val paramMap = mutableMapOf<Int, String>()

        for (line in configText.lines()) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("param=") -> {
                    val rest = trimmed.removePrefix("param=")
                    val eqIdx = rest.indexOf('=')
                    if (eqIdx > 0) {
                        val num = rest.substring(0, eqIdx).toIntOrNull()
                        val value = rest.substring(eqIdx + 1)
                        if (num != null) paramMap[num] = value
                    }
                }
                trimmed.startsWith("msg=") -> {}
                trimmed.contains('=') -> {
                    val eqIdx = trimmed.indexOf('=')
                    config[trimmed.substring(0, eqIdx)] = trimmed.substring(eqIdx + 1)
                }
            }
        }
        params = paramMap

        val serverVersion = config["server_version"]
        val lobbyHost = params[3]
        val js5Token = params[29]
        println("server_version: $serverVersion")
        println("param 3 (lobby host): $lobbyHost")
        println("param 29 (JS5 token): $js5Token")
        println("param 10 (login token): ${params[10]}")

        if (lobbyHost != null && js5Token != null && serverVersion != null) {
            val versionParts = serverVersion.split(".")
            val major = versionParts.getOrNull(0)?.toIntOrNull() ?: 947
            val minor = versionParts.getOrNull(1)?.toIntOrNull() ?: 1

            println("\n" + "=" .repeat(70))
            println("=== Step 2: Connecting to LIVE Jagex JS5 ($lobbyHost:43594) ===")
            println("=" .repeat(70))
            try {
                captureMasterIndex(lobbyHost, 43594, major, minor, js5Token, "LIVE")
            } catch (e: Exception) {
                println("  ERROR connecting to live server: ${e::class.simpleName}: ${e.message}")
                println("  (This is expected if Jagex blocks direct connections or version has changed)")
            }
        }
    } catch (e: Exception) {
        println("  ERROR fetching config: ${e::class.simpleName}: ${e.message}")
        println("  Skipping live capture.")
    }

    // =====================================================================
    // Step 3: Connect to our local server
    // =====================================================================
    println("\n" + "=" .repeat(70))
    println("=== Step 3: Connecting to LOCAL server (localhost:43594) ===")
    println("=" .repeat(70))
    try {
        captureMasterIndex(
            host = "localhost",
            port = 43594,
            major = 947,
            minor = 1,
            token = "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK",
            label = "LOCAL"
        )
    } catch (e: Exception) {
        println("  ERROR connecting to local server: ${e::class.simpleName}: ${e.message}")
        println("  Is the lobby server running? (./gradlew :lobby:run)")
    }
}

private fun captureMasterIndex(host: String, port: Int, major: Int, minor: Int, token: String, label: String) {
    val socket = Socket(host, port)
    socket.soTimeout = 15_000

    val output = DataOutputStream(socket.getOutputStream())
    val input = DataInputStream(socket.getInputStream())

    try {
        // Handshake
        val tokenBytes = token.toByteArray(Charsets.US_ASCII)
        val payloadSize = 4 + 4 + tokenBytes.size + 1 + 1
        output.writeByte(0x0F)
        output.writeByte(payloadSize)
        output.writeInt(major)
        output.writeInt(minor)
        output.write(tokenBytes)
        output.writeByte(0)
        output.writeByte(0)
        output.flush()
        println("  Sent handshake: version=$major.$minor, token=$token")

        // SYNC
        val sync = input.readByte().toInt() and 0xFF
        println("  SYNC response: $sync (expected 0)")
        if (sync != 0) {
            println("  FAILED: Bad SYNC response, aborting")
            return
        }

        // ACK + READY
        output.write(byteArrayOf(0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.write(byteArrayOf(0x03, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.flush()
        println("  Sent ACK + READY")

        // Request master index (255/255) — urgent
        output.writeByte(0x01) // flags: urgent
        output.writeByte(255)  // index
        output.writeInt(255)   // group
        output.writeInt(0)     // padding
        output.flush()
        println("  Sent request: 255/255 (master index)")

        // Read response header (raw wire bytes)
        val headerBytes = ByteArray(RESPONSE_HEADER_LEN)
        input.readFully(headerBytes)
        println("\n  [$label] Response header (${RESPONSE_HEADER_LEN} bytes):")
        println("    Raw: ${headerBytes.joinToString(" ") { "%02x".format(it) }}")

        val respIndex = headerBytes[0].toInt() and 0xFF
        val respHash = readBEInt32(headerBytes, 1)
        val compressionByte = headerBytes[5].toInt() and 0xFF
        val compressedSize = readBEInt32(headerBytes, 6)
        println("    index=$respIndex hash=0x${"%08x".format(respHash)} compression=$compressionByte compressedSize=$compressedSize")

        // Read full payload with block framing
        val payloadDataSize = compressedSize + if (compressionByte != 0) 4 else 0
        val rawWire = ByteArrayOutputStream(RESPONSE_HEADER_LEN + payloadDataSize * 2)
        rawWire.write(headerBytes) // include response header in raw dump

        val container = ByteArrayOutputStream(5 + payloadDataSize)
        container.write(compressionByte)
        container.write((compressedSize shr 24) and 0xFF)
        container.write((compressedSize shr 16) and 0xFF)
        container.write((compressedSize shr 8) and 0xFF)
        container.write(compressedSize and 0xFF)

        var bytesRead = 0
        var blockOffset = RESPONSE_HEADER_LEN

        while (bytesRead < payloadDataSize) {
            val blockRemaining = BLOCK_SIZE - blockOffset
            val toRead = minOf(blockRemaining, payloadDataSize - bytesRead)
            val buf = ByteArray(toRead)
            input.readFully(buf)
            rawWire.write(buf)
            container.write(buf)
            bytesRead += toRead
            blockOffset += toRead

            if (blockOffset == BLOCK_SIZE && bytesRead < payloadDataSize) {
                val contHeader = ByteArray(CONTINUATION_HEADER_LEN)
                input.readFully(contHeader)
                rawWire.write(contHeader)
                println("    Continuation header at payload offset $bytesRead: ${contHeader.joinToString(" ") { "%02x".format(it) }}")
                blockOffset = CONTINUATION_HEADER_LEN
            }
        }

        val containerBytes = container.toByteArray()
        val totalWireBytes = rawWire.toByteArray()
        println("\n  [$label] Container: ${containerBytes.size} bytes")
        println("  [$label] Total wire bytes: ${totalWireBytes.size} bytes")
        println("  [$label] Container first 40 bytes:")
        println("    ${containerBytes.take(40).joinToString(" ") { "%02x".format(it) }}")

        // Parse master index
        if (compressionByte == 0) {
            val archiveCount = containerBytes[5].toInt() and 0xFF
            println("\n  [$label] Archive count: $archiveCount")
            val rsaBlockSz = compressedSize - 1 - archiveCount * ENTRY_SIZE
            println("  [$label] RSA block size: $rsaBlockSz bytes")

            // Show ALL entries with fileCount values
            println("  [$label] --- All entries (index: crc, version, fileCount, uncompressedSize) ---")
            for (i in 0 until archiveCount) {
                val off = 6 + i * ENTRY_SIZE
                if (off + ENTRY_SIZE > containerBytes.size) break
                val crc = readBEInt32(containerBytes, off)
                val ver = readBEInt32(containerBytes, off + 4)
                val fc = readBEInt32(containerBytes, off + 8)
                val us = readBEInt32(containerBytes, off + 12)
                // Only print non-zero entries to reduce noise
                if (crc != 0 || ver != 0 || fc != 0) {
                    println("  [$label] Entry[$i]: crc=0x${"%08x".format(crc)} ver=$ver fc=$fc usize=$us")
                }
            }

            // Show RSA block info
            val rsaOff = 6 + archiveCount * ENTRY_SIZE
            if (rsaOff < containerBytes.size) {
                val rsaEnd = minOf(rsaOff + 16, containerBytes.size)
                println("  [$label] RSA block first bytes: ${containerBytes.copyOfRange(rsaOff, rsaEnd).joinToString(" ") { "%02x".format(it) }}")
            }
        } else {
            println("  [$label] Compressed master index (type=$compressionByte) — cannot parse without decompression")
        }

        // Also request one archive index (255/2) to compare framing
        println("\n  [$label] Requesting archive index 255/2...")
        output.writeByte(0x01)
        output.writeByte(255)
        output.writeInt(2)
        output.writeInt(0)
        output.flush()

        val archHeader = ByteArray(RESPONSE_HEADER_LEN)
        input.readFully(archHeader)
        val archCompression = archHeader[5].toInt() and 0xFF
        val archCompressedSize = readBEInt32(archHeader, 6)
        println("  [$label] Archive 2 response header: ${archHeader.joinToString(" ") { "%02x".format(it) }}")
        println("  [$label] Archive 2: compression=$archCompression compressedSize=$archCompressedSize")

        // Read enough to verify format
        val archPayloadSize = archCompressedSize + if (archCompression != 0) 4 else 0
        val archContainer = ByteArrayOutputStream(5 + archPayloadSize)
        archContainer.write(archCompression)
        archContainer.write((archCompressedSize shr 24) and 0xFF)
        archContainer.write((archCompressedSize shr 16) and 0xFF)
        archContainer.write((archCompressedSize shr 8) and 0xFF)
        archContainer.write(archCompressedSize and 0xFF)

        var archBytesRead = 0
        var archBlockOffset = RESPONSE_HEADER_LEN
        while (archBytesRead < archPayloadSize) {
            val blockRemaining = BLOCK_SIZE - archBlockOffset
            val toRead = minOf(blockRemaining, archPayloadSize - archBytesRead)
            val buf = ByteArray(toRead)
            input.readFully(buf)
            archContainer.write(buf)
            archBytesRead += toRead
            archBlockOffset += toRead
            if (archBlockOffset == BLOCK_SIZE && archBytesRead < archPayloadSize) {
                input.readFully(ByteArray(CONTINUATION_HEADER_LEN))
                archBlockOffset = CONTINUATION_HEADER_LEN
            }
        }
        val archContainerBytes = archContainer.toByteArray()
        println("  [$label] Archive 2 container: ${archContainerBytes.size} bytes")
        println("  [$label] Archive 2 first 20 bytes: ${archContainerBytes.take(20).joinToString(" ") { "%02x".format(it) }}")

    } finally {
        socket.close()
    }
}

private fun readBEInt32(data: ByteArray, offset: Int): Int =
    ((data[offset].toInt() and 0xFF) shl 24) or
    ((data[offset + 1].toInt() and 0xFF) shl 16) or
    ((data[offset + 2].toInt() and 0xFF) shl 8) or
    (data[offset + 3].toInt() and 0xFF)
