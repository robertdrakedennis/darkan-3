package com.undercut.game.nxt.minimenu

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.memory.NativeAccess.getLong
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.eastl.EastlString
import com.undercut.game.nxt.OMiniMenuEntry
import java.lang.foreign.MemorySegment

class MiniMenuEntry(val ptr: MemorySegment) {
    val targetString: EastlString
        get() = EastlString(ptr.pointerAtOffset(OMiniMenuEntry.TARGET_STRING, 0x24L))
    val actionString: EastlString
        get() = EastlString(ptr.pointerAtOffset(OMiniMenuEntry.ACTION_STRING, 0x24L))
    val highlightType: Long
        get() = ptr.getLong(OMiniMenuEntry.HIGHLIGHT_TYPE)
    val action: MiniMenuAction
        get() = MiniMenuAction(ptr.deref(OMiniMenuEntry.ACTION, 0x40L))
    val unk1: Int
        get() = ptr.getInt(OMiniMenuEntry.UNK_1)
    val itemId: Int
        get() = ptr.getInt(OMiniMenuEntry.ITEM_ID)
    val param1: Int
        get() = ptr.getInt(OMiniMenuEntry.PARAM_1)
    val param2: Int
        get() = ptr.getInt(OMiniMenuEntry.PARAM_2)
    val param3: Int
        get() = ptr.getInt(OMiniMenuEntry.PARAM_3)
    val target: MemorySegment
        get() = ptr.pointerAtOffset(OMiniMenuEntry.TARGETED_ENTITY, 0x2000L)
}