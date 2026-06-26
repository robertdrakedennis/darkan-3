package com.undercut.game.nxt.entity

import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OAnimation
import world.gregs.voidps.gameval.Gameval
import java.lang.foreign.MemorySegment

class Animation(val ptr: MemorySegment) {
    val id
        get() = ptr.readInt(OAnimation.ID)
    val currentFrame
        get() = ptr.readInt(OAnimation.CURRENT_FRAME)

    override fun toString() = "[id=${Gameval.seqLabel(id)}, currentFrame=$currentFrame]"
}