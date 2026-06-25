package org.darkan.tools

import org.darkan.tools.util.RefTableEntry
import org.darkan.tools.util.JavConfig
import org.darkan.tools.util.parseRefTable
import org.darkan.tools.cachedownloader.JS5Protocol
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.IndexFile
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URI
import java.nio.file.Paths

fun main(args: Array<String>) {
    val cachePath = Paths.get(args.getOrNull(0) ?: "./data/cache")
    val index = args.getOrNull(1)?.toInt() ?: 12
    val contentUrl = args.getOrNull(2) ?: "http://content.runescape.com"
    val host = args.getOrNull(3) ?: "content.runescape.com"
    val port = args.getOrNull(4)?.toIntOrNull() ?: 43594
    val defaultMajor = args.getOrNull(5)?.toIntOrNull() ?: 948
    val defaultMinor = args.getOrNull(6)?.toIntOrNull() ?: 1
    val restrict = args.drop(7).mapNotNull { it.toIntOrNull() }.toSet()
    val javConfig = JavConfig.fetch()
    val token = javConfig.params[29] ?: error("missing JS5 token in jav_config.ws")
    val liveVersion = javConfig.serverVersion(defaultMajor, defaultMinor)

    DecompressionContext().use { context ->
        IndexFile(cachePath.resolve("js5-$index.jcache")).use { indexFile ->
            val refRaw = indexFile.getRawTable() ?: error("missing ref table for index $index")
            val refTable = parseRefTable(context, refRaw, parseFiles = true)
                ?: error("failed to parse ref table for index $index")
            val stored = indexFile.allKeys()
            val candidates = refTable.entries
                .filter { entry ->
                    val raw = indexFile.getRaw(entry.id)
                    raw == null || CRC.calculate(raw, 0, raw.size) != entry.crc
                }
                .filter { restrict.isEmpty() || it.id in restrict }

            println("index=$index candidates=${candidates.size}")
            var storedCount = 0
            var failedCount = 0
            for (entry in candidates) {
                val data = fetchHttp(contentUrl, index, entry)
                    ?: fetchTcp(host, port, index, entry, token, defaultMajor, defaultMinor, liveVersion)
                if (data == null) {
                    failedCount++
                    println("MISS ${entry.id} fetch")
                    continue
                }
                val crc = CRC.calculate(data, 0, data.size)
                if (crc != entry.crc) {
                    failedCount++
                    println("MISS ${entry.id} crc expected=${entry.crc} actual=$crc bytes=${data.size}")
                    continue
                }
                indexFile.putRaw(entry.id, data, entry.version, crc)
                storedCount++
                println("OK ${entry.id} bytes=${data.size} crc=$crc version=${entry.version}")
            }
            println("stored=$storedCount failed=$failedCount")
        }
    }
}

private fun fetchHttp(contentUrl: String, index: Int, entry: RefTableEntry): ByteArray? {
    val url = "$contentUrl/ms?m=0&a=$index&k=0&g=${entry.id}&c=${entry.crc}&v=${entry.version}"
    val connection = URI(url).toURL().openConnection() as HttpURLConnection
    connection.connectTimeout = 15_000
    connection.readTimeout = 30_000
    connection.requestMethod = "GET"
    return try {
        if (connection.responseCode != 200) return null
        connection.inputStream.readAllBytes()
    } finally {
        connection.disconnect()
    }
}

private fun fetchTcp(
    host: String,
    port: Int,
    index: Int,
    entry: RefTableEntry,
    token: String,
    defaultMajor: Int,
    defaultMinor: Int,
    liveVersion: Pair<Int, Int>,
): ByteArray? {
    for ((major, minor) in versionCandidates(defaultMajor, defaultMinor, liveVersion)) {
        try {
            val (socket, input, output) = JS5Protocol.connect(host, port, major, minor, token, soTimeout = 30_000)
            return socket.use {
                JS5Protocol.sendFileRequest(output, index, entry.id, major)
                val response = JS5Protocol.readResponse(input)
                if (response.index == index && response.archive == entry.id) response.container else null
            }
        } catch (_: Exception) {
        }
    }
    return null
}

private fun versionCandidates(
    defaultMajor: Int,
    defaultMinor: Int,
    liveVersion: Pair<Int, Int>,
): List<Pair<Int, Int>> {
    val candidates = LinkedHashSet<Pair<Int, Int>>()
    candidates.add(defaultMajor to defaultMinor)
    candidates.add(liveVersion)
    for (delta in 0..10) {
        candidates.add(defaultMajor to defaultMinor + delta)
        if (defaultMinor - delta >= 0) candidates.add(defaultMajor to defaultMinor - delta)
    }
    return candidates.toList()
}
