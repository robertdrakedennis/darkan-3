package com.undercut.game.nxt.minimenu

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OMiniMenuAction
import java.lang.foreign.MemorySegment

class MiniMenuAction(val ptr: MemorySegment) {
    val id: Int
        get() = ptr.readInt(OMiniMenuAction.ACTION_ID)
    val vtable: MemorySegment
        get() = ptr.pointerAtOffset(OMiniMenuAction.ACTION_VTABLE, 0x40)
    val actionSendFunc: MemorySegment
        get() = vtable.deref(OMiniMenuAction.VTABLE_ACTION_SEND_FUNCTION, 0x0)
}