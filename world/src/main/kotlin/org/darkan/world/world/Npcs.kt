package org.darkan.world.world

import org.darkan.world.entity.Npc
import java.util.concurrent.ConcurrentHashMap

/**
 * Global NPC registry. Indices are allocated monotonically; [allocate] returns the
 * assigned index. Per NPC_INFO Phase 2 (A5 §"Phase 2 — additions"), the client treats
 * NPC indices as 16-bit ushorts, so values stay within 1..65535.
 *
 * Storage is a [ConcurrentHashMap] (not a packed slot array like [Players]) because
 * NPC indices can become sparse as mobs spawn/despawn dynamically — most world ticks
 * touch a small fraction of the pool. The tick thread iterates via [forEach] each
 * tick when building NPC_INFO.
 *
 * TODO (B6/B7): replace [nextIndex] with a free-list / recycle pool to bound index
 * reuse latency once we have multi-hour soak tests.
 */
object Npcs {
    private val byIndex = ConcurrentHashMap<Int, Npc>()
    private var nextIndex = 1
    private val indexLock = Any()

    fun allocate(npc: Npc): Int {
        synchronized(indexLock) {
            val idx = nextIndex++
            byIndex[idx] = npc
            return idx
        }
    }

    fun release(index: Int) {
        byIndex.remove(index)
    }

    fun get(index: Int): Npc? = byIndex[index]

    fun forEach(block: (Npc) -> Unit) {
        byIndex.values.forEach(block)
    }
}
