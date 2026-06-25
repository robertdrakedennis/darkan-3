package com.undercut.game.hooks.impl

import com.undercut.cache.type.vars.VarDomain
import com.undercut.cache.type.vars.VarbitType
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.nxt.OFunctions
import com.undercut.script.ScriptExecutor
import com.undercut.script.event.impl.Varp
import com.undercut.script.event.impl.Varpbit
import com.undercut.ui.UI
import com.undercut.ui.UIState
import java.lang.foreign.MemorySegment

object SetVarPlayer {
    @JvmStatic
    @Hook(OFunctions.PLAYERVARDOMAIN_SET)
    fun setVarPlayerHook(varManager: MemorySegment, varInfo: MemorySegment, valuePtr: MemorySegment): MemorySegment {
        synchronized(Bootstrap.lock) {
            try {
                val varId = varInfo.reinterpret(0x10L).deref(0x8L, 0x10L).getInt(0x8L)
                val value = valuePtr.reinterpret(0x8).getInt()
                val prev = Bootstrap.client.playerVarDomain.getVar(varId)
                if (prev != value) {
                    val wantsDebug = UI.wantsVarpDebug()
                    if (wantsDebug) {
                        // println("[varp_$varId] $prev -> $value")
                        UI.addVarTableEntry("varp", varId, prev, value)
                    }
                    ScriptExecutor.pushEvent(Varp(varId, prev, value))
                    val varBits = VarbitType.baseVarMap[VarDomain.PLAYER]?.get(varId)
                    varBits?.forEach { bit ->
                        val vbPrev = bit.getValue(prev)
                        val vbValue = bit.getValue(value)
                        if (vbPrev != vbValue) {
                            ScriptExecutor.pushEvent(Varpbit(bit.id, vbPrev, vbValue))
                            if (wantsDebug) {
                                // println("\t[varpbit_${bit.id}] $vbPrev -> $vbValue")
                                UI.addVarTableEntry("varpbit", bit.id, vbPrev, vbValue)
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return HookManager.trampoline(::setVarPlayerHook.name)
            .invokeExact(varManager, varInfo, valuePtr) as MemorySegment
    }

    @JvmStatic
    @Hook(OFunctions.PLAYERVARDOMAIN_SETBIT)
    fun setVarbitPlayerHook(varManager: MemorySegment, varInfo: MemorySegment, valuePtr: MemorySegment): MemorySegment {
        synchronized(Bootstrap.lock) {
            try {
                val varId = varInfo.reinterpret(0x10L).deref(0x8L, 0x10L).getInt(0x8L)
                val value = valuePtr.reinterpret(0x8).getInt()
                val prev = Bootstrap.client.playerVarDomain.getVar(varId)
                if (prev != value) {
                    val debugEnabled = UIState.varpDebugEnabled.value
                    if (debugEnabled) {
                        // println("[varp_$varId] $prev -> $value")
                        UI.addVarTableEntry("varp", varId, prev, value)
                    }
                    ScriptExecutor.pushEvent(Varp(varId, prev, value))
                    val varBits = VarbitType.baseVarMap[VarDomain.PLAYER]?.get(varId)
                    varBits?.forEach { bit ->
                        val vbPrev = bit.getValue(prev)
                        val vbValue = bit.getValue(value)
                        if (vbPrev != vbValue) {
                            ScriptExecutor.pushEvent(Varpbit(bit.id, vbPrev, vbValue))
                            if (debugEnabled) {
                                // println("\t[varpbit_${bit.id}] $vbPrev -> $vbValue")
                                UI.addVarTableEntry("varpbit", bit.id, vbPrev, vbValue)
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return HookManager.trampoline(::setVarbitPlayerHook.name)
            .invokeExact(varManager, varInfo, valuePtr) as MemorySegment
    }
}