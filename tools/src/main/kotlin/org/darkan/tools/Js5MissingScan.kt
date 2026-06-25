package org.darkan.tools

import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.IndexFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

fun main(args: Array<String>) {
    val cachePath = Paths.get(args.getOrNull(0) ?: "./data/cache")
    val indices = indices(cachePath)

    DecompressionContext().use { context ->
        for (index in indices) {
            try {
                IndexFile(cachePath.resolve("js5-$index.jcache")).use { indexFile ->
                    val refRaw = indexFile.getRawTable()
                    if (refRaw == null) {
                        println("index=$index missingRefTable=true")
                        return@use
                    }
                    val refTable = parseRefTable(context, refRaw, parseFiles = false)
                    if (refTable == null) {
                        println("index=$index parseRefTable=false")
                        return@use
                    }
                    val refIds = refTable.entries.map { it.id }.toSet()
                    val stored = indexFile.allKeys()
                    var stale = 0
                    for (entry in refTable.entries) {
                        val raw = indexFile.getRaw(entry.id) ?: continue
                        if (CRC.calculate(raw, 0, raw.size) != entry.crc) stale++
                    }
                    val missing = refIds.count { it !in stored }
                    val orphan = stored.count { it !in refIds }
                    if (missing != 0 || stale != 0 || orphan != 0) {
                        println("index=$index refEntries=${refIds.size} stored=${stored.size} missing=$missing stale=$stale orphan=$orphan")
                    }
                }
            } catch (e: Exception) {
                println("index=$index error=${e.javaClass.simpleName}:${e.message}")
            }
        }
    }
}

private fun indices(cachePath: Path): List<Int> =
    Files.newDirectoryStream(cachePath, "js5-*.jcache").use { stream ->
        stream.mapNotNull { path ->
            path.name.removePrefix("js5-").removeSuffix(".jcache").toIntOrNull()
        }.sorted()
    }
