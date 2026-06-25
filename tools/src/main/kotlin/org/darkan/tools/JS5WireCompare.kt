package org.darkan.tools

import org.darkan.tools.cachedownloader.JS5Protocol
import org.darkan.tools.util.JavConfig
import org.darkan.tools.util.toHex
import org.darkan.core.EnvVars
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

/**
 * Compares JS5 wire bytes between Jagex live servers and our local server
 * for specific file requests. Helps identify format differences causing crashes.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.JS5WireCompareKt
 */

// Files to compare — archive/group pairs that the client requests early
private val DEFAULT_FILES_TO_COMPARE = listOf(
    255 to 27,
    27 to 0,
    27 to 1,
)

fun main(args: Array<String>) {
    val filesToCompare = args.getOrNull(0)
        ?.split(",")
        ?.mapNotNull { it.toFilePairOrNull() }
        ?.takeIf { it.isNotEmpty() }
        ?: DEFAULT_FILES_TO_COMPARE
    val localPort = args.getOrNull(1)?.toIntOrNull() ?: 43596
    val livePort = args.getOrNull(2)?.toIntOrNull() ?: 43594

    // Fetch live config
    println("=".repeat(70))
    println("Fetching live jav_config.ws...")
    val config = try { JavConfig.fetch() } catch (e: Exception) {
        println("ERROR fetching config: ${e.message}")
        println("Skipping live server comparison — will only dump local server data")
        null
    }

    val liveHost = config?.params?.get(3)
    val liveToken = config?.params?.get(29)
    val (liveMajor, liveMinor) = config?.serverVersion(948, 1) ?: (948 to 1)
    if (config != null) {
        println("Live: host=$liveHost, version=$liveMajor.$liveMinor, token=$liveToken")
    }

    // Connect to local server
    println("\n${"=".repeat(70)}")
    println("Connecting to LOCAL server (localhost:$localPort)...")
    val localResults = try {
        captureFiles("localhost", localPort, EnvVars.majorVersion, EnvVars.minorVersion, EnvVars.js5ServerToken, filesToCompare)
    } catch (e: Exception) {
        println("ERROR connecting to local: ${e.message}")
        emptyMap()
    }

    // Connect to live server
    val liveResults = if (liveHost != null && liveToken != null) {
        println("\n${"=".repeat(70)}")
        println("Connecting to LIVE server ($liveHost:$livePort)...")
        try {
            captureFiles(liveHost, livePort, liveMajor, liveMinor, liveToken, filesToCompare)
        } catch (e: Exception) {
            println("ERROR connecting to live: ${e.message}")
            emptyMap()
        }
    } else emptyMap()

    // Compare
    println("\n${"=".repeat(70)}")
    println("=== COMPARISON ===")
    println("=".repeat(70))

    val headerLen = JS5Protocol.RESPONSE_HEADER_LEN
    for ((key, localData) in localResults) {
        val (idx, grp) = key
        val liveData = liveResults[key]
        println("\n--- $idx/$grp ---")
        println("  LOCAL: ${localData.wire.size} wire bytes, container=${localData.container.size} bytes")
        println("  LOCAL header: ${localData.wire.copyOfRange(0, headerLen).toHex()}")
        println("  LOCAL container first 32: ${localData.container.copyOfRange(0, minOf(32, localData.container.size)).toHex()}")

        if (liveData != null) {
            println("  LIVE:  ${liveData.wire.size} wire bytes, container=${liveData.container.size} bytes")
            println("  LIVE  header: ${liveData.wire.copyOfRange(0, headerLen).toHex()}")
            println("  LIVE  container first 32: ${liveData.container.copyOfRange(0, minOf(32, liveData.container.size)).toHex()}")

            // Compare headers
            val localHeader = localData.wire.copyOfRange(0, headerLen)
            val liveHeader = liveData.wire.copyOfRange(0, headerLen)
            if (localHeader.contentEquals(liveHeader)) {
                println("  HEADER: MATCH")
            } else {
                println("  HEADER: MISMATCH!")
                for (i in 0 until headerLen) {
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

private fun String.toFilePairOrNull(): Pair<Int, Int>? {
    val parts = split("/")
    if (parts.size != 2) return null
    val index = parts[0].toIntOrNull() ?: return null
    val group = parts[1].toIntOrNull() ?: return null
    return index to group
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
        val sync = JS5Protocol.handshake(output, input, major, minor, token)
        if (sync != 0) {
            println("  Bad SYNC: $sync")
            return results
        }

        // ACK + READY
        JS5Protocol.sendConnectionInit(output, major)
        println("  Handshake complete")

        for ((idx, grp) in files) {
            println("  Requesting $idx/$grp...")
            JS5Protocol.sendFileRequest(output, idx, grp, major = 0, flags = 0x01)

            val response = JS5Protocol.readResponse(input, captureWire = true)
            results[idx to grp] = CapturedFile(response.wire!!, response.container)
            println("  Got $idx/$grp: ${response.wire!!.size} wire bytes")
        }
    } finally {
        socket.close()
    }
    return results
}
