package org.darkan.tools

import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Path

fun main() {
    val cache = SQLiteCache.load(Path.of("./data/cache"))
    val decompressor = DecompressionContext()

    println("=== Archive Index Format Check ===")
    println("Checking decompressed format version byte for each archive index (255/N)")
    println()

    var checked = 0
    var formatOk = 0
    var formatBad = 0
    var decompressError = 0

    for (index in cache.indices()) {
        val raw = cache.sector(255, index) ?: continue
        if (raw.size < 5) continue
        checked++

        val compression = raw[0].toInt() and 0xFF

        try {
            val decompressed = decompressor.decompress(raw)
            if (decompressed == null) {
                println("  index=$index: DECOMPRESSION RETURNED NULL compression=$compression rawSize=${raw.size}")
                decompressError++
                continue
            }

            val formatVersion = if (decompressed.isNotEmpty()) decompressed[0].toInt() and 0xFF else -1
            if (formatVersion == 7) {
                formatOk++
            } else {
                println("  index=$index: FORMAT VERSION = $formatVersion (expected 7!) compression=$compression rawSize=${raw.size} decompressedSize=${decompressed.size}")
                if (decompressed.size >= 20) {
                    val hex = decompressed.take(20).joinToString(" ") { "%02x".format(it) }
                    println("    first 20 bytes: $hex")
                }
                formatBad++
            }
        } catch (e: Exception) {
            println("  index=$index: DECOMPRESSION FAILED compression=$compression error=${e.message}")
            decompressError++
        }
    }

    println()
    println("=== Summary ===")
    println("Checked: $checked")
    println("Format OK (version 7): $formatOk")
    println("Format BAD (not version 7): $formatBad")
    println("Decompression errors: $decompressError")

    cache.close()
}
