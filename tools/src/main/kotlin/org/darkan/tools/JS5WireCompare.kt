package org.darkan.tools

import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.net.URL

/**
 * Compares JS5 wire bytes between Jagex live servers and our local server
 * for specific file requests. Helps identify format differences causing crashes.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.JS5WireCompareKt
 */

private const val BLOCK_SIZE = 102_400
private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5

// Files to compare — archive/group pairs that the client requests early
private val FILES_TO_COMPARE = listOf(
    255 to 255,   // master index
    255 to 2,     // archive index 2
    255 to 28,    // archive index 28
    28 to 1,      // first group file the client requests
    28 to 2,
    59 to 1,
    62 to 1,
)

fun main() {
    // Fetch live config
    println("=".repeat(70))
    println("Fetching live jav_config.ws...")
    val configUrl = "https://www.runescape.com/k=5/l=0/jav_config.ws?binaryType=4"
    val configText = try { URL(configUrl).readText() } catch (e: Exception) {
        println("ERROR fetching config: ${e.message}")
        println("Skipping live server comparison — will only dump local server data")
        null
    }

    var liveHost: String? = null
    var liveToken: String? = null
    var liveMajor = 947
    var liveMinor = 1

    if (configText != null) {
        val params = mutableMapOf<Int, String>()
        val config = mutableMapOf<String, String>()
        for (line in configText.lines()) {
            val t = line.trim()
            when {
                t.startsWith("param=") -> {
                    val rest = t.removePrefix("param=")
                    val eq = rest.indexOf('=')
                    if (eq > 0) {
                        val num = rest.substring(0, eq).toIntOrNull()
                        if (num != null) params[num] = rest.substring(eq + 1)
                    }
                }
                t.contains('=') && !t.startsWith("msg=") -> {
                    val eq = t.indexOf('=')
                    config[t.substring(0, eq)] = t.substring(eq + 1)
                }
            }
        }
        liveHost = params[3]
        liveToken = params[29]
        val sv = config["server_version"]
        if (sv != null) {
            val parts = sv.split(".")
            liveMajor = parts.getOrNull(0)?.toIntOrNull() ?: 947
            liveMinor = parts.getOrNull(1)?.toIntOrNull() ?: 1
        }
        println("Live: host=$liveHost, version=$liveMajor.$liveMinor, token=$liveToken")
    }

    // Connect to local server
    println("\n${"=".repeat(70)}")
    println("Connecting to LOCAL server (localhost:43594)...")
    val localResults = try {
        captureFiles("localhost", 43594, 947, 1, "ev9+VAp5/tMKeNR/7MOuH6lKWS+rGkHK", FILES_TO_COMPARE)
    } catch (e: Exception) {
        println("ERROR connecting to local: ${e.message}")
        emptyMap()
    }

    // Connect to live server
    val liveResults = if (liveHost != null && liveToken != null) {
        println("\n${"=".repeat(70)}")
        println("Connecting to LIVE server ($liveHost:43594)...")
        try {
            captureFiles(liveHost, 43594, liveMajor, liveMinor, liveToken, FILES_TO_COMPARE)
        } catch (e: Exception) {
            println("ERROR connecting to live: ${e.message}")
            emptyMap()
        }
    } else emptyMap()

    // Compare
    println("\n${"=".repeat(70)}")
    println("=== COMPARISON ===")
    println("=".repeat(70))

    for ((key, localData) in localResults) {
        val (idx, grp) = key
        val liveData = liveResults[key]
        println("\n--- $idx/$grp ---")
        println("  LOCAL: ${localData.wire.size} wire bytes, container=${localData.container.size} bytes")
        println("  LOCAL header: ${localData.wire.take(RESPONSE_HEADER_LEN).joinToString(" ") { "%02x".format(it) }}")
        println("  LOCAL container first 32: ${localData.container.take(32).joinToString(" ") { "%02x".format(it) }}")

        if (liveData != null) {
            println("  LIVE:  ${liveData.wire.size} wire bytes, container=${liveData.container.size} bytes")
            println("  LIVE  header: ${liveData.wire.take(RESPONSE_HEADER_LEN).joinToString(" ") { "%02x".format(it) }}")
            println("  LIVE  container first 32: ${liveData.container.take(32).joinToString(" ") { "%02x".format(it) }}")

            // Compare headers
            val localHeader = localData.wire.take(RESPONSE_HEADER_LEN)
            val liveHeader = liveData.wire.take(RESPONSE_HEADER_LEN)
            if (localHeader == liveHeader) {
                println("  HEADER: MATCH")
            } else {
                println("  HEADER: MISMATCH!")
                for (i in 0 until RESPONSE_HEADER_LEN) {
                    if (i < localHeader.size && i < liveHeader.size && localHeader[i] != liveHeader[i]) {
                        println("    byte[$i]: local=0x${"%02x".format(localHeader[i])} live=0x${"%02x".format(liveHeader[i])}")
                    }
                }
            }

            // Compare containers
            if (localData.container.contentEquals(liveData.container)) {
                println("  CONTAINER: MATCH")
            } else {
                println("  CONTAINER: MISMATCH!")
                val minLen = minOf(localData.container.size, liveData.container.size)
                var diffs = 0
                for (i in 0 until minLen) {
                    if (localData.container[i] != liveData.container[i]) {
                        if (diffs < 10) {
                            println("    byte[$i]: local=0x${"%02x".format(localData.container[i])} live=0x${"%02x".format(liveData.container[i])}")
                        }
                        diffs++
                    }
                }
                if (diffs > 10) println("    ... and ${diffs - 10} more differences")
                if (localData.container.size != liveData.container.size) {
                    println("    Size differs: local=${localData.container.size} live=${liveData.container.size}")
                }
            }
        } else {
            println("  LIVE: not available")
        }
    }
}

data class CapturedFile(val wire: ByteArray, val container: ByteArray)

fun captureFiles(
    host: String, port: Int, major: Int, minor: Int, token: String,
    files: List<Pair<Int, Int>>
): Map<Pair<Int, Int>, CapturedFile> {
    val socket = Socket(host, port)
    socket.soTimeout = 15_000
    val output = DataOutputStream(socket.getOutputStream())
    val input = DataInputStream(socket.getInputStream())
    val results = mutableMapOf<Pair<Int, Int>, CapturedFile>()

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

        val sync = input.readByte().toInt() and 0xFF
        if (sync != 0) {
            println("  Bad SYNC: $sync")
            return results
        }

        // ACK + READY
        output.write(byteArrayOf(0x06, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.write(byteArrayOf(0x03, 0x00, 0x00, 0x05, 0x00, 0x00, 0x03, 0xB2.toByte(), 0x00, 0x00))
        output.flush()
        println("  Handshake complete")

        for ((idx, grp) in files) {
            println("  Requesting $idx/$grp...")
            output.writeByte(0x01) // urgent
            output.writeByte(idx)
            output.writeInt(grp)
            output.writeInt(0)
            output.flush()

            val captured = readCapturedResponse(input)
            results[idx to grp] = captured
            println("  Got $idx/$grp: ${captured.wire.size} wire bytes")
        }
    } finally {
        socket.close()
    }
    return results
}

fun readCapturedResponse(input: DataInputStream): CapturedFile {
    val headerBytes = ByteArray(RESPONSE_HEADER_LEN)
    input.readFully(headerBytes)

    val compressionByte = headerBytes[5].toInt() and 0xFF
    val compressedSize = readBEInt(headerBytes, 6)

    val payloadSize = compressedSize + if (compressionByte != 0) 4 else 0

    val rawWire = ByteArrayOutputStream(RESPONSE_HEADER_LEN + payloadSize * 2)
    rawWire.write(headerBytes)

    val container = ByteArrayOutputStream(5 + payloadSize)
    container.write(compressionByte)
    container.write((compressedSize shr 24) and 0xFF)
    container.write((compressedSize shr 16) and 0xFF)
    container.write((compressedSize shr 8) and 0xFF)
    container.write(compressedSize and 0xFF)

    var bytesRead = 0
    var blockOffset = RESPONSE_HEADER_LEN

    while (bytesRead < payloadSize) {
        val blockRemaining = BLOCK_SIZE - blockOffset
        val toRead = minOf(blockRemaining, payloadSize - bytesRead)
        val buf = ByteArray(toRead)
        input.readFully(buf)
        rawWire.write(buf)
        container.write(buf)
        bytesRead += toRead
        blockOffset += toRead

        if (blockOffset == BLOCK_SIZE && bytesRead < payloadSize) {
            val contHeader = ByteArray(CONTINUATION_HEADER_LEN)
            input.readFully(contHeader)
            rawWire.write(contHeader)
            blockOffset = CONTINUATION_HEADER_LEN
        }
    }

    return CapturedFile(rawWire.toByteArray(), container.toByteArray())
}

private fun readBEInt(data: ByteArray, offset: Int): Int =
    ((data[offset].toInt() and 0xFF) shl 24) or
    ((data[offset + 1].toInt() and 0xFF) shl 16) or
    ((data[offset + 2].toInt() and 0xFF) shl 8) or
    (data[offset + 3].toInt() and 0xFF)
