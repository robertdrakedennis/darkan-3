package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.memory.NativeIntArray
import com.undercut.game.memory.eastl.EastlHashTable
import java.lang.foreign.MemorySegment

class NPCManager(val ptr: MemorySegment) : Iterable<MemorySegment> {
    val hashTable = EastlHashTable(ptr.pointerAtOffset(ONPCManager.HASH_TABLE, 0x100L), 0x20L)
    val indices = NativeIntArray(ptr.pointerAtOffset(ONPCManager.LOCAL_NPC_INDICES, 1024*0x4), 1024, 0x4)

    operator fun get(hashCode: Int): MemorySegment? = hashTable[hashCode]?.let { return it.toShared().value(Short.MAX_VALUE.toLong()) }

    // Iterate the local NPC index array (the in-scene NPCs), resolving each via the keyed lookup.
    // The EASTL hash-table value-index walk (elementCount/getNodeAtValuesIndex) comes back empty in
    // instanced areas even when keyed lookups succeed there, so iterating it silently yielded nothing
    // (e.g. list_npcs returned 0 inside the Desperate Times puzzle). The local index array is the
    // reliable, scene-correct source — the same path SceneSnapshot and the entity overlay use.
    override fun iterator(): Iterator<MemorySegment> {
        val out = ArrayList<MemorySegment>()
        for (hashCode in indices) {
            if (hashCode <= 0) continue
            val seg = get(hashCode) ?: continue
            if (seg.address() != 0L) out.add(seg)
        }
        return out.iterator()
    }
}