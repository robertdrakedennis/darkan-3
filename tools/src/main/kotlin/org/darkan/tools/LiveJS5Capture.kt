package org.darkan.tools

import org.darkan.tools.cachedownloader.JS5Protocol
import org.darkan.tools.util.JavConfig
import org.darkan.tools.util.toHex
import world.gregs.voidps.buffer.read.BufferReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Live JS5 capture: connects to both Jagex live servers and our local server,
 * downloads the master index from each, and dumps raw wire bytes for comparison.
 *
 * This is the "ground truth" tool for debugging JS5 response format differences.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.LiveJS5CaptureKt
 */

private const val ENTRY_SIZE = 80

fun main() {
    // =====================================================================
    // Step 1: Fetch live jav_config.ws
    // =====================================================================
    println("=".repeat(70))
    println("=== Step 1: Fetching live jav_config.ws ===")
    println("=".repeat(70))

    try {
        val config = JavConfig.fetch()
        val serverVersion = config.settings["server_version"]
        val lobbyHost = config.params[3]
        val js5Token = config.params[29]
        println("server_version: $serverVersion")
        println("param 3 (lobby host): $lobbyHost")
        println("param 29 (JS5 token): $js5Token")
        println("param 10 (login token): ${config.params[10]}")

        if (lobbyHost != null && js5Token != null && serverVersion != null) {
            val (major, minor) = config.serverVersion(948, 1)

            println("\n" + "=".repeat(70))
            println("=== Step 2: Connecting to LIVE Jagex JS5 ($lobbyHost:43594) ===")
            println("=".repeat(70))
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
    println("\n" + "=".repeat(70))
    println("=== Step 3: Connecting to LOCAL server (localhost:43594) ===")
    println("=".repeat(70))
    try {
        captureMasterIndex(
            host = "localhost",
            port = 43594,
            major = 948,
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
        val sync = JS5Protocol.handshake(output, input, major, minor, token)
        println("  Sent handshake: version=$major.$minor, token=$token")
        println("  SYNC response: $sync (expected 0)")
        if (sync != 0) {
            println("  FAILED: Bad SYNC response, aborting")
            return
        }

        // ACK + READY
        JS5Protocol.sendConnectionInit(output, major)
        println("  Sent ACK + READY")

        // Request master index (255/255) — urgent
        JS5Protocol.sendFileRequest(output, 255, 255, major = 0, flags = 0x01)
        println("  Sent request: 255/255 (master index)")

        // Read response, capturing the exact wire bytes
        val response = JS5Protocol.readResponse(
            input,
            captureWire = true,
            onHeader = { respIndex, respHash, compression, compressedSize ->
                println("\n  [$label] Response header (${JS5Protocol.RESPONSE_HEADER_LEN} bytes):")
                println("    index=$respIndex hash=0x${"%08x".format(respHash)} compression=$compression compressedSize=$compressedSize")
            },
            onContinuation = { payloadOffset, contIndex, contHash ->
                println("    Continuation header at payload offset $payloadOffset: index=$contIndex hash=0x${"%08x".format(contHash)}")
            },
        )
        val containerBytes = response.container
        val totalWireBytes = response.wire!!
        println("    Raw header: ${totalWireBytes.copyOfRange(0, JS5Protocol.RESPONSE_HEADER_LEN).toHex()}")

        val compressionByte = containerBytes[0].toInt() and 0xFF
        val compressedSize = BufferReader(containerBytes).apply { skip(1) }.readInt()
        println("\n  [$label] Container: ${containerBytes.size} bytes")
        println("  [$label] Total wire bytes: ${totalWireBytes.size} bytes")
        println("  [$label] Container first 40 bytes:")
        println("    ${containerBytes.copyOfRange(0, minOf(40, containerBytes.size)).toHex()}")

        // Parse master index
        if (compressionByte == 0) {
            val reader = BufferReader(containerBytes)
            reader.skip(5)
            val archiveCount = reader.readUnsignedByte()
            println("\n  [$label] Archive count: $archiveCount")
            val rsaBlockSz = compressedSize - 1 - archiveCount * ENTRY_SIZE
            println("  [$label] RSA block size: $rsaBlockSz bytes")

            // Show ALL entries with fileCount values
            println("  [$label] --- All entries (index: crc, version, fileCount, uncompressedSize) ---")
            for (i in 0 until archiveCount) {
                if (6 + (i + 1) * ENTRY_SIZE > containerBytes.size) break
                val crc = reader.readInt()
                val ver = reader.readInt()
                val fc = reader.readInt()
                val us = reader.readInt()
                reader.skip(64) // whirlpool
                // Only print non-zero entries to reduce noise
                if (crc != 0 || ver != 0 || fc != 0) {
                    println("  [$label] Entry[$i]: crc=0x${"%08x".format(crc)} ver=$ver fc=$fc usize=$us")
                }
            }

            // Show RSA block info
            val rsaOff = 6 + archiveCount * ENTRY_SIZE
            if (rsaOff < containerBytes.size) {
                val rsaEnd = minOf(rsaOff + 16, containerBytes.size)
                println("  [$label] RSA block first bytes: ${containerBytes.copyOfRange(rsaOff, rsaEnd).toHex()}")
            }
        } else {
            println("  [$label] Compressed master index (type=$compressionByte) — cannot parse without decompression")
        }

        // Also request one archive index (255/2) to compare framing
        println("\n  [$label] Requesting archive index 255/2...")
        JS5Protocol.sendFileRequest(output, 255, 2, major = 0, flags = 0x01)

        val archResponse = JS5Protocol.readResponse(
            input,
            captureWire = true,
            onHeader = { _, _, compression, compressedSize ->
                println("  [$label] Archive 2: compression=$compression compressedSize=$compressedSize")
            },
        )
        println("  [$label] Archive 2 response header: ${archResponse.wire!!.copyOfRange(0, JS5Protocol.RESPONSE_HEADER_LEN).toHex()}")
        println("  [$label] Archive 2 container: ${archResponse.container.size} bytes")
        println("  [$label] Archive 2 first 20 bytes: ${archResponse.container.copyOfRange(0, minOf(20, archResponse.container.size)).toHex()}")
    } finally {
        socket.close()
    }
}
