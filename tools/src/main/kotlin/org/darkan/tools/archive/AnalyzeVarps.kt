// org.darkan.tools.archive: unmaintained one-shot experiments kept for reference only.
// These tools were written against specific captures/revisions, are not part of any
// build task, and may rely on stale capture data or stale opcode identities.
package org.darkan.tools.archive

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.decoder.VarBitDecoder
import java.io.File

/**
 * Analyzes lobby varps to find "magic number" packed values that contain
 * meaningful varbits. Cross-references with CS2 scripts for naming.
 */
fun main() {
    val cache = Cache.get()
    val varbits = VarBitDecoder().load(cache)
    println("Loaded ${varbits.size} varbit definitions")

    // Load lobby varps
    val varpMap = mutableMapOf<Int, Long>()
    File("lobby/src/main/resources/capture/lobby-varps.txt").readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .forEach { line ->
            val parts = line.trim().split(" ", limit = 2)
            if (parts.size == 2) varpMap[parts[0].toInt()] = parts[1].toLong()
        }

    // Build reverse index: varpId -> list of varbits that map to it
    val varpToVarbits = mutableMapOf<Int, MutableList<Int>>()
    for (i in varbits.indices) {
        val vb = varbits[i]
        if (vb.id < 0) continue
        // Skip default/empty definitions (varp=0, bits 0-0)
        if (vb.index == 0 && vb.startBit == 0 && vb.endBit == 0) continue
        varpToVarbits.getOrPut(vb.index) { mutableListOf() }.add(i)
    }

    println("Found ${varpToVarbits.size} varps with varbit mappings\n")

    // Find varps in our lobby data that have non-trivial values AND have varbits
    var found = 0
    for ((varpId, rawValue) in varpMap.toSortedMap()) {
        if (rawValue == 0L || rawValue == -1L) continue // Skip trivial values
        val intVal = rawValue.toInt()
        val varbitIds = varpToVarbits[varpId] ?: continue

        // Extract each varbit value from this varp
        val nonZeroBits = mutableListOf<Triple<Int, Int, Int>>() // (varbitId, value, bitLen)
        for (vbId in varbitIds) {
            val vb = varbits[vbId]
            val bitLen = vb.endBit - vb.startBit
            if (bitLen <= 0 || bitLen > 31) continue
            val mask = (1 shl bitLen) - 1
            val extracted = (intVal shr vb.startBit) and mask
            if (extracted != 0) {
                nonZeroBits.add(Triple(vbId, extracted, bitLen))
            }
        }

        if (nonZeroBits.isNotEmpty()) {
            found++
            println("varp[$varpId] = $intVal (0x${"%08X".format(intVal)})")
            for ((vbId, value, bitLen) in nonZeroBits.sortedBy { it.first }) {
                val vb = varbits[vbId]
                println("  varbit[$vbId] bits ${vb.startBit}-${vb.endBit} (${bitLen}b) = $value")
            }
        }
    }
    println("\nTotal: $found varps with non-zero varbits")
}
