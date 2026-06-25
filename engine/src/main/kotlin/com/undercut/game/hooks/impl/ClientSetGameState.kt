package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.nxt.OFunctions
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.timeLoggedInAt
import com.undercut.script.event.impl.MainStateChanged
import java.lang.foreign.MemorySegment

object ClientSetGameState {
    @JvmStatic
    @Hook(OFunctions.CLIENT_SETMAINSTATE)
    fun addClientSetMainStateHook(clientMainPtr: MemorySegment, state: Int) {
        synchronized(Bootstrap.lock) {
            try {
                ScriptExecutor.pushEvent(MainStateChanged(state))
            } catch (t : Throwable) {
                t.printStackTrace()
            }
            if (state == 30) timeLoggedInAt = System.currentTimeMillis()
            HookManager.trampoline(::addClientSetMainStateHook.name).invoke(clientMainPtr, state)
        }
    }
}