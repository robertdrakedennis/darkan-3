package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.nxt.OFunctions
import com.undercut.game.nxt.entity.HitType
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.localPlayer
import com.undercut.script.event.impl.Hitsplat
import java.lang.foreign.MemorySegment

object AddHitmark {
    @JvmStatic
    @Hook(OFunctions.HITMARKSANDHEADBARS_ADDHITMARK)
    fun addHitmarkHook(hitbarsAndHitmarksPtr: MemorySegment, param2: Long, hitType: Int, damage: Int, unk1: Int, unk2: Int, createdClientCycle: Int, param8: Int) {
        synchronized(Bootstrap.lock) {
            try {
                if (hitbarsAndHitmarksPtr.address() == localPlayer.hitmarksAndHeadbars?.ptr?.address()) ScriptExecutor.pushEvent(Hitsplat(hitType, HitType.byId(hitType), damage, localPlayer))

                HookManager.trampoline(::addHitmarkHook.name).invoke(hitbarsAndHitmarksPtr, param2, hitType, damage, unk1, unk2, createdClientCycle, param8)
            } catch (e: Throwable) {
                println("[ERROR] addHitmarkHook failed: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
}