package org.darkan.tools.openrs2

import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.definition.decoder.ItemDecoder
import world.gregs.voidps.cache.definition.decoder.NPCDecoder
import world.gregs.voidps.cache.definition.decoder.ObjectDecoder
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

/**
 * Smoke-tests a freshly built NXT SQLite cache: loads it (the same path
 * [world.gregs.voidps.cache.sqlite.SQLiteCache.load] / `Cache.get()` would), reports the index
 * count, and decodes a few well-known definitions to prove the containers and reference tables are
 * coherent end-to-end.
 *
 * The canonical spot-check is **item 315 == "Shrimps"** (the cooked shrimp). It exercises archive
 * addressing (315 -> archive 1, file 59), multi-file group splitting, decompression and the
 * [ItemDecoder] opcode loop in one go.
 *
 * Run: `./gradlew :tools:cacheVerify -Pargs="/Users/.../948-sqlite-cache"`  (defaults to ./data/cache)
 */
fun main(args: Array<String>) {
    val cachePath: Path = if (args.isNotEmpty()) Paths.get(args[0]) else Paths.get("./data/cache")
    println("Verifying cache at ${cachePath.toAbsolutePath().normalize()}")

    val cache = SQLiteCache.load(cachePath)
    var failures = 0
    try {
        val indices = cache.indices()
        println("Indices loaded: ${cache.indexCount()} -> ${indices.joinToString(" ")}")
        if (cache.indexCount() < 30) {
            System.err.println("FAIL: expected at least ~40 indices for a full RS3 cache, got ${cache.indexCount()}")
            failures++
        }

        // Per-index archive counts for a few config indices, to confirm ref tables parsed.
        for (idx in intArrayOf(Index.CONFIGS, Index.ITEMS, Index.NPCS, Index.OBJECTS, Index.STRUCTS)) {
            val archives = cache.archiveCount(idx)
            println("  index $idx: $archives archives, last archive id ${cache.lastArchiveId(idx)}")
            if (archives == 0) {
                System.err.println("FAIL: index $idx has no archives — ref table did not parse")
                failures++
            }
        }

        // --- Canonical spot-check: item 315 == Shrimps ---
        val items = ItemDecoder().load(cache)
        val item315 = items.getOrNull(315)
        val name315 = item315?.name
        println()
        println("Spot-check item 315 name = ${name315?.let { "\"$it\"" } ?: "<null>"}")
        if (name315 == null || !name315.equals("Shrimps", ignoreCase = true)) {
            System.err.println("FAIL: item 315 expected \"Shrimps\", got ${name315?.let { "\"$it\"" } ?: "<null>"}")
            failures++
        } else {
            println("  OK: item 315 decoded as \"$name315\" (modelId=${item315.modelId}, cost=${item315.cost})")
        }

        // A couple more definitions to widen coverage across indices.
        val npcs = NPCDecoder().load(cache)
        val npc0 = npcs.getOrNull(0)?.name
        println("Sample NPC 0 name = ${npc0?.let { "\"$it\"" } ?: "<null>"}")

        val objects = ObjectDecoder().load(cache)
        val obj0 = objects.getOrNull(0)?.name
        println("Sample object 0 name = ${obj0?.let { "\"$it\"" } ?: "<null>"}")
    } finally {
        cache.close()
    }

    println()
    if (failures == 0) {
        println("VERIFY PASSED — cache is loadable and item 315 spot-check succeeded.")
    } else {
        System.err.println("VERIFY FAILED with $failures problem(s).")
        exitProcess(1)
    }
}
