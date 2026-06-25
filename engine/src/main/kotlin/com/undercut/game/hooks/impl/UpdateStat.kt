package com.undercut.game.hooks.impl

import com.undercut.game.Skill
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.nxt.OFunctions
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.refreshLastXpDrop
import com.undercut.script.api.timeLoggedInAt
import com.undercut.script.event.impl.XPDrop
import com.undercut.ui.UI
import java.lang.foreign.MemorySegment

object UpdateStat {
    @JvmStatic
    @Hook(OFunctions.STATTABLE_UPDATESTAT)
    fun updateStatHook(clientPtr: MemorySegment, packetPtr: MemorySegment): MemorySegment {
        synchronized (Bootstrap.lock) {
            val prevXp = Bootstrap.client.skills.map { it.xp }.toMutableList()
            val retVal = HookManager.trampoline(::updateStatHook.name).invokeExact(clientPtr, packetPtr) as MemorySegment
            if ((System.currentTimeMillis() - timeLoggedInAt) > 15000L) {
                Bootstrap.client.skills.forEachIndexed { index, skill ->
                    val newXP = skill.xp
                    val oldXP = prevXp[index]
                    if (newXP > oldXP) {
                        val event = XPDrop(Skill.entries[index], newXP - oldXP)
                        refreshLastXpDrop()
                        ScriptExecutor.pushEvent(event)
                        // Mirror Swing UI behavior in ImGui
                        UI.updateXpTable(event.skill, event.gainedXp)
                    }
                }
            }
            return retVal
        }
    }
}