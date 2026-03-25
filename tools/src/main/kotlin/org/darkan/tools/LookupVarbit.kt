package org.darkan.tools

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.decoder.VarBitDecoder
import java.io.File

fun main() {
    val cache = Cache.get()
    val varbits = VarBitDecoder().load(cache)

    println("Cache loaded: ${varbits.size} varbit definitions")

    // Key varbits from CS2 scripts
    val targets = listOf(
        16467 to "lobby chat enable guard (lobbyscreen_load)",
        14528 to "some permission check (script13621)",
    )

    // Load lobby-varps.txt
    val varpMap = mutableMapOf<Int, Long>()
    File("lobby/src/main/resources/capture/lobby-varps.txt").readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .forEach { line ->
            val parts = line.trim().split(" ", limit = 2)
            if (parts.size == 2) varpMap[parts[0].toInt()] = parts[1].toLong()
        }

    println("\n=== Varbit Lookups ===")
    for ((id, desc) in targets) {
        if (id >= varbits.size) { println("Varbit $id: out of range"); continue }
        val vb = varbits[id]
        val bitLen = vb.endBit - vb.startBit
        val mask = (1 shl bitLen) - 1
        val varpVal = varpMap[vb.index]
        val bitVal = if (varpVal != null) (varpVal.toInt() shr vb.startBit) and mask else 0
        println("Varbit $id ($desc):")
        println("  varp=${vb.index}, bits=${vb.startBit}-${vb.endBit} (len=$bitLen)")
        println("  varp value in capture: ${varpVal ?: "NOT SET (0)"}")
        println("  extracted varbit value: $bitVal")
    }

    // Also scan for interesting varbits that might control chat/membership
    println("\n=== Scanning varps that contain chat-related varbits ===")
    // Check varp 3185 (referenced in script8889 for chat state)
    val varp3185 = varpMap[3185]
    println("Varp 3185 (chat state): ${varp3185 ?: "NOT SET"}")

    // Check varp that contains varbit 16467
    if (16467 < varbits.size) {
        val vb = varbits[16467]
        println("Varp ${vb.index} (backs varbit 16467): ${varpMap[vb.index] ?: "NOT SET"}")
    }
}
