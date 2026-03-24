package org.darkan.tools.cachedownloader

import kotlinx.coroutines.runBlocking
import java.net.URI

fun fetchJavConfig(): Map<String, String> {
    val url = URI("https://www.runescape.com/k=5/l=0/jav_config.ws?binaryType=4").toURL()
    val text = url.readText()
    val params = mutableMapOf<String, String>()
    for (line in text.lines()) {
        if (line.startsWith("param=")) {
            val rest = line.removePrefix("param=")
            val eqIdx = rest.indexOf('=')
            if (eqIdx > 0) {
                params[rest.substring(0, eqIdx)] = rest.substring(eqIdx + 1)
            }
        }
    }
    return params
}

fun main(args: Array<String>) {
    val host = args.getOrNull(0) ?: "content.runescape.com"
    val port = args.getOrNull(1)?.toIntOrNull() ?: 43594
    val major = args.getOrNull(2)?.toIntOrNull() ?: 947
    val minor = args.getOrNull(3)?.toIntOrNull() ?: 1
    val connections = args.getOrNull(4)?.toIntOrNull() ?: 8
    val outputPath = args.getOrNull(5) ?: "./data/cache"

    println("Cache Downloader")
    println("  Fetching config from jav_config.ws...")
    val params = fetchJavConfig()
    val token = params["29"] ?: throw IllegalStateException("Could not find JS5 token (param=29) in jav_config.ws")
    // Content URL from param 49 or 37 (HTTP JS5 base URL)
    val contentUrl = params["49"]?.let { "http://$it" }
        ?: params["37"]?.let { "http://$it" }
        ?: "http://content.runescape.com"
    println("  Token: $token")
    println("  Content URL: $contentUrl")
    println("  Host: $host:$port")
    println("  Version: $major.$minor")
    println("  Connections: $connections")
    println("  Output: $outputPath")
    println()

    runBlocking {
        CacheDownloader(host, port, major, minor, connections, outputPath, token, contentUrl).download()
    }
}
