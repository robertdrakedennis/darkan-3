package com.undercut.game.nxt.interfaces

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.toMemorySegment
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.OInterfaceList
import java.lang.foreign.MemorySegment

class InterfaceList(val ptr: MemorySegment) {
    private val start
        get() = ptr.deref(OInterfaceList.START_PTR, 0x8L)

    private val end
        get() = ptr.deref(OInterfaceList.END_PTR, 0x8L)

    private val capacity
        get() = (ptr.deref(OInterfaceList.CAPACITY_PTR, 0x8L).address() - start.address()) / 0x10L

    val size
        get() = (end.address() - start.address()) / 0x10L

    operator fun get(interfaceId: Int): InterfaceParent? =
        getRaw(interfaceId)?.takeIf { it[0]?.visible == true }

    // Resolve the parent without filtering on the engine's visibility flag. Useful when
    // the flag false-negatives (some dialog interfaces don't set FLAGS bit 0x20 even when
    // they're on screen). Callers should still null-check.
    fun getRaw(interfaceId: Int): InterfaceParent? {
        if (interfaceId !in 0..<size) return null
        val entryAddress = start.address() + (interfaceId * 0x10L)
        val interfaceEntry = entryAddress.toMemorySegment(0x10L)
        interfaceEntry.deref(0x8L, 0x8L).getOrNull ?: return null
        val sharedPtr = interfaceEntry.toShared()
        val interfacePtr = sharedPtr.value(0x10L).getOrNull ?: return null
        return InterfaceParent(interfaceId, interfacePtr)
    }

    fun isOpen(interfaceId: Int) = get(interfaceId) != null

    fun getComponent(interfaceId: Int, componentId: Int): InterfaceComponent? {
        val parent = get(interfaceId) ?: return null
        return parent[componentId]
    }

    fun getComponentRaw(interfaceId: Int, componentId: Int): InterfaceComponent? =
        getRaw(interfaceId)?.get(componentId)
}