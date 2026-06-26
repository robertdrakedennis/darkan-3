package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.HookManager
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.util.componentIdFromHash
import com.undercut.util.interfaceIdFromHash
import world.gregs.voidps.gameval.Gameval
import java.lang.foreign.MemorySegment

object IfButtonX {
    @JvmStatic
    //@Hook(OFunctions.IF_BUTTONX_INNER)
    fun IFButtonXHook(interfaceManagerPtr: MemorySegment, ifComponentSharedPtr: MemorySegment, ifComponentHash: Int, slotId: Int, opNum: Int, targetString: MemorySegment, doActionSourced: Boolean) {
        synchronized(Bootstrap.lock) {
            if (!doActionSourced) {
                println("[IFButtonX]: IFComponentSharedPtr: ${ifComponentSharedPtr.address().toString(16)}")
                val str = targetString.getOrNull?.reinterpret(0x200)?.getString(0x0)
                println("\tOpNum: $opNum IFSlot(${Gameval.interfaceLabel(interfaceIdFromHash(ifComponentHash))}, ${Gameval.componentLabel(interfaceIdFromHash(ifComponentHash), componentIdFromHash(ifComponentHash))}, $slotId) targetStr: $str")
            }
            HookManager.trampoline(::IFButtonXHook.name).invoke(interfaceManagerPtr, ifComponentSharedPtr, ifComponentHash, slotId, opNum, targetString, doActionSourced)
        }
    }
}