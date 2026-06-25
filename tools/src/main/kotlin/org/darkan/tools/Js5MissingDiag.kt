package org.darkan.tools

import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.IndexFile
import java.nio.file.Paths

fun main(args: Array<String>) {
    val cachePath = Paths.get(args.getOrNull(0) ?: "./data/cache")
    val index = args.getOrNull(1)?.toInt() ?: 12
    val interesting = args.drop(2).mapNotNull { it.toIntOrNull() }.toSet()

    DecompressionContext().use { context ->
        IndexFile(cachePath.resolve("js5-$index.jcache")).use { indexFile ->
            val refRaw = indexFile.getRawTable() ?: error("missing ref table for index $index")
            val refTable = parseRefTable(context, refRaw, parseFiles = true)
                ?: error("failed to parse ref table for index $index")
            val refIds = refTable.entries.map { it.id }.toSet()
            val stored = indexFile.allKeys()
            val versions = indexFile.allVersions()
            var stale = 0
            for (entry in refTable.entries) {
                val raw = indexFile.getRaw(entry.id) ?: continue
                if (CRC.calculate(raw, 0, raw.size) != entry.crc) stale++
            }
            val missing = refIds.asSequence().filter { it !in stored }.sorted().toList()
            val orphan = stored.asSequence().filter { it !in refIds }.sorted().toList()

            println("index=$index")
            println("refEntries=${refIds.size} stored=${stored.size} missing=${missing.size} stale=$stale orphan=${orphan.size}")
            println("maxRef=${refTable.maxGroupId} maxStored=${stored.maxOrNull() ?: -1}")
            if (interesting.isNotEmpty()) {
                val entriesById = refTable.entries.associateBy { it.id }
                for (id in interesting.sorted()) {
                    val entry = entriesById[id]
                    val raw = indexFile.getRaw(id)
                    val computed = raw?.let { CRC.calculate(it, 0, it.size) }
                    val storedVersion = versions[id]?.first
                    val storedCrc = versions[id]?.second
                    println(
                        "id=$id ref=${entry != null} stored=${raw != null} " +
                            "bytes=${raw?.size ?: -1} refCrc=${entry?.crc} computedCrc=$computed " +
                            "dbCrc=$storedCrc refVersion=${entry?.version} dbVersion=$storedVersion files=${entry?.fileCount}"
                    )
                }
            }
            println("missingSample=${missing.take(80).joinToString(",")}")
            println("orphanSample=${orphan.take(40).joinToString(",")}")
        }
    }
}
