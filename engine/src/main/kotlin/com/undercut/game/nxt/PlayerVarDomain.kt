package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.eastl.EastlHashTable
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.VarBitDefinition.Companion.BIT_MASKS
import java.lang.foreign.MemorySegment

class PlayerVarDomain(val ptr: MemorySegment) {
    val hashTable
        get() = EastlHashTable(ptr.pointerAtOffset(OPlayerVarDomain.HASH_TABLE, 0x100), 0x30)

    fun getVar(id: Int) = hashTable[id]?.readInt() ?: 0
    fun getVarBit(id: Int): Int {
        try {
            val type = Cache.varbit(id) ?: return 0
            return getVar(type.baseVar) shr type.startBit and BIT_MASKS[type.endBit - type.startBit]
        } catch(e: Throwable) {
            return 0
        }
    }
}