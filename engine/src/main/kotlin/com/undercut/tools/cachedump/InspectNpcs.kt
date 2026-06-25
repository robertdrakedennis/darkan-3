package com.undercut.tools.cachedump

import com.undercut.cache.type.npcs.NPCType

object InspectNpcsMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val targets = intArrayOf(3090, 781, 783, 5706, 4365, 4026, 4575, 4497)

        val parentsOf = mutableMapOf<Int, MutableList<Int>>()
        val byId = mutableMapOf<Int, NPCType>()
        for (id in 0..50_000) {
            val t = runCatching { NPCType.get(id) }.getOrNull() ?: continue
            byId[id] = t
            t.transformTo?.forEach { child ->
                if (child > 0) parentsOf.getOrPut(child) { mutableListOf() }.add(id)
            }
        }
        println("[inspect-npcs] loaded ${byId.size} NPCs, ${parentsOf.size} child→parents links")

        for (id in targets) {
            val t = byId[id]
            println("\n=== NPC #$id ===")
            if (t == null) {
                println("  (not present in cache)")
                continue
            }
            println("  name        = ${t.name}")
            println("  modelIds    = ${t.modelIds?.toList()}")
            println("  headModels  = ${t.headModels?.toList()}")
            println("  transformTo = ${t.transformTo?.toList()}")
            println("  varpBit/varp= ${t.varpBit} / ${t.varp}")
            val parents = parentsOf[id]?.take(10) ?: emptyList()
            println("  parents     = ${parents.map { "$it=${byId[it]?.name}" }}")
        }
    }
}
