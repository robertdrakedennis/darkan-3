package com.undercut.game.nxt.mainlogicmanager

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.nxt.OMainLogicManager
import java.lang.foreign.MemorySegment

class MainLogicManager(val ptr: MemorySegment) {
    val statTable
        get() = StatTable(ptr.deref(OMainLogicManager.STAT_TABLE, 0x24L))
    val clientVarDomain
        get() = ClientVarDomain(ptr.pointerAtOffset(OMainLogicManager.CLIENT_VAR_DOMAIN, 0x20000L))
}