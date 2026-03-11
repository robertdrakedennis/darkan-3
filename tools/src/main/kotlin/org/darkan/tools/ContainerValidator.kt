package org.darkan.tools

import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Path

/**
 * Validates that all container data in the cache has consistent headers.
 * Checks: compression type valid, compressedSize matches actual data length.
 */
fun main() {
    val cachePath = Path.of("./data/cache")
    println("Loading cache from $cachePath...")
    val cache = SQLiteCache.load(cachePath)

    var totalFiles = 0
    var errorCount = 0
    var truncatedCount = 0

    for (index in cache.indices()) {
        val archives = cache.archives(index)
        for (archive in archives) {
            val data = cache.sector(index, archive) ?: continue
            totalFiles++

            if (data.size < 5) {
                println("ERROR: index=$index archive=$archive: too small (${data.size} bytes)")
                errorCount++
                continue
            }

            val compression = data[0].toInt() and 0xFF
            val compressedSize = ((data[1].toInt() and 0xFF) shl 24) or
                    ((data[2].toInt() and 0xFF) shl 16) or
                    ((data[3].toInt() and 0xFF) shl 8) or
                    (data[4].toInt() and 0xFF)

            if (compression > 3) {
                println("ERROR: index=$index archive=$archive: invalid compression type $compression")
                errorCount++
                continue
            }

            val expectedHeaderSize = 5 + if (compression != 0) 4 else 0
            val expectedTotalSize = expectedHeaderSize + compressedSize

            if (data.size < expectedTotalSize) {
                println("TRUNCATED: index=$index archive=$archive: compression=$compression compressedSize=$compressedSize expected=$expectedTotalSize actual=${data.size} (short by ${expectedTotalSize - data.size})")
                truncatedCount++
                if (truncatedCount <= 20) {
                    // Print header hex for first 20 truncated files
                    val headerHex = data.take(minOf(20, data.size)).joinToString(" ") { "%02x".format(it) }
                    println("  header: $headerHex")
                }
            } else if (data.size > expectedTotalSize) {
                // Extra bytes — might be version trailer or multi-entry container
                val extra = data.size - expectedTotalSize
                if (extra > 2) {
                    // More than 2 extra bytes is suspicious (2 bytes = version trailer)
                    if (totalFiles < 1000 || extra > 100) {
                        // Only log for small file counts or large discrepancies
                    }
                }
            }
        }

        // Also check index reference tables (255/index)
        val refData = cache.sector(255, index)
        if (refData != null && refData.size >= 5) {
            totalFiles++
            val compression = refData[0].toInt() and 0xFF
            val compressedSize = ((refData[1].toInt() and 0xFF) shl 24) or
                    ((refData[2].toInt() and 0xFF) shl 16) or
                    ((refData[3].toInt() and 0xFF) shl 8) or
                    (refData[4].toInt() and 0xFF)
            val expectedHeaderSize = 5 + if (compression != 0) 4 else 0
            val expectedTotalSize = expectedHeaderSize + compressedSize
            if (refData.size < expectedTotalSize) {
                println("TRUNCATED REF: 255/$index: compression=$compression compressedSize=$compressedSize expected=$expectedTotalSize actual=${refData.size}")
                truncatedCount++
            }
        }
    }

    println()
    println("=== Container Validation Summary ===")
    println("Total files checked: $totalFiles")
    println("Errors: $errorCount")
    println("Truncated: $truncatedCount")

    // Now specifically check the files that the client requests first
    println()
    println("=== First-requested files (index 59, 28, 62) ===")
    for ((idx, grp) in listOf(59 to 1, 59 to 2, 59 to 3, 59 to 4, 59 to 5, 28 to 1, 28 to 3, 28 to 4, 28 to 6, 28 to 7, 28 to 9, 28 to 10, 28 to 12, 62 to 1)) {
        val data = cache.sector(idx, grp)
        if (data == null) {
            println("  index=$idx group=$grp: NULL (not in cache)")
            continue
        }
        val compression = data[0].toInt() and 0xFF
        val compressedSize = ((data[1].toInt() and 0xFF) shl 24) or
                ((data[2].toInt() and 0xFF) shl 16) or
                ((data[3].toInt() and 0xFF) shl 8) or
                (data[4].toInt() and 0xFF)
        val expectedPayload = compressedSize + if (compression != 0) 4 else 0
        val actualPayload = data.size - 5
        val status = if (actualPayload >= expectedPayload) "OK" else "TRUNCATED (short by ${expectedPayload - actualPayload})"
        println("  index=$idx group=$grp: ${data.size} bytes, compression=$compression, compressedSize=$compressedSize, payload expected=$expectedPayload actual=$actualPayload $status")
    }

    cache.close()
}
