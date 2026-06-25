package com.undercut.game.hooks.impl

import com.undercut.game.hooks.HookManager
import java.lang.foreign.MemorySegment

object AddHeadbar {
    @JvmStatic
    //@Hook(OFunctions.ADD_HEADBAR)
    fun addHeadbarHook(hitbarsAndHitmarksPtr: MemorySegment, param2: MemorySegment, hitbarType: Int, param4: Int, frameCreated: Int, fromFill: Int, toFill: Int, param8: Int, param9: Int, fillFrameCount: Int) {
        HookManager.trampoline(::addHeadbarHook.name).invokeExact(hitbarsAndHitmarksPtr, param2, hitbarType, param4, frameCreated, fromFill, toFill, param8, param9, fillFrameCount)
    }
}