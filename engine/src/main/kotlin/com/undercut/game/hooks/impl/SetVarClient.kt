package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getInt
import com.undercut.game.nxt.OFunctions
import com.undercut.script.ScriptExecutor
import com.undercut.script.event.impl.Varc
import com.undercut.script.event.impl.Varcbit
import com.undercut.ui.UI
import world.gregs.voidps.cache.definition.data.VarBitDefinition
import world.gregs.voidps.cache.definition.data.VarDomain
import java.lang.foreign.MemorySegment

object SetVarClient {
    @JvmStatic
    @Hook(OFunctions.CLIENTVARDOMAIN_SETVARVALUE)
    fun setVarClientHook(varManager: MemorySegment, varInfo: MemorySegment, valuePtr: MemorySegment): MemorySegment {
        synchronized (Bootstrap.lock) {
            try {
                val varId = varInfo.reinterpret(0x10L).deref(0x8L, 0x10L).getInt(0x8L)
                val value = valuePtr.reinterpret(0x8).getInt()
                val prev = Bootstrap.client.clientVarDomain.getVar(varId)
                if (prev != value) {
                    val wantsDebug = UI.wantsVarcDebug()
                    if (wantsDebug) {
                        UI.addVarTableEntry("varc", varId, prev, value)
                    }
                    ScriptExecutor.pushEvent(Varc(varId, prev, value))
                    val varBits = VarBitDefinition.baseVarMap[VarDomain.CLIENT]?.get(varId)
                    varBits?.forEach { bit ->
                        val vbPrev = bit.getValue(prev)
                        val vbValue = bit.getValue(value)
                        if (vbPrev != vbValue) {
                            ScriptExecutor.pushEvent(Varcbit(bit.id, vbPrev, vbValue))
                            if (wantsDebug) {
                                UI.addVarTableEntry("varcbit", bit.id, vbPrev, vbValue)
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        return HookManager.trampoline(::setVarClientHook.name).invokeExact(varManager, varInfo, valuePtr) as MemorySegment
    }
}