package com.undercut.game.nxt.mainlogicmanager

import com.undercut.cache.type.vars.VarbitType
import com.undercut.cache.type.vars.VarbitType.Companion.BIT_MASKS
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.eastl.EastlHashTable
import com.undercut.game.nxt.OClientVarDomain
import java.lang.foreign.MemorySegment

class ClientVarDomain(val ptr: MemorySegment) {
    val hashTable
        get() = EastlHashTable(ptr.pointerAtOffset(OClientVarDomain.HASH_TABLE, 0x100), 0x30, 0x30)

    fun getVar(id: Int) = hashTable[id]?.readInt() ?: 0
    fun getVarBit(id: Int): Int {
        try {
            val type = VarbitType.get(id)
            return getVar(type.baseVar) shr type.startBit and BIT_MASKS[type.endBit - type.startBit]
        } catch(e: Throwable) {
            return 0
        }
    }
}