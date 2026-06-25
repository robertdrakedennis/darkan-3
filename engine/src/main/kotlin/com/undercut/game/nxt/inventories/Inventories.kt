package com.undercut.game.nxt.inventories

import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.toFunctionHandle
import com.undercut.game.nxt.OFunctions
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.*
import java.lang.invoke.MethodHandle

class Inventories(val ptr: MemorySegment) {

    private val getInventoryHandle: MethodHandle = NativeAccess.BASE_ADDR.pointerAtOffset(OFunctions.INVENTORYMANAGER_GETINVENTORY, 0x0L)
        .toFunctionHandle(FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_INT, JAVA_BYTE))

    operator fun get(invId: Int) : Inventory {
        val inventory = getInventoryHandle.invokeExact(ptr, invId, 0.toByte()) as MemorySegment
        if (inventory.address() == 0L) error("Invalid inventory: $invId")
        return Inventory(inventory.reinterpret(0x40L))
    }

    fun getWithInterface(invId: Int, interfaceId: Int, componentId: Int) : Inventory {
        try{
        val inventory = getInventoryHandle.invokeExact(ptr, invId, 0.toByte()) as MemorySegment
        if (inventory.address() == 0L) error("Invalid inventory: $invId")
        return Inventory(inventory.reinterpret(0x40L), interfaceId, componentId)
        }catch (e: Exception) {
            e.printStackTrace()
            println("Failed to get inventory: $invId")
            return Inventory(MemorySegment.ofAddress(0L), interfaceId, componentId)
        }
    }

    fun exists(invId: Int) = (getInventoryHandle.invokeExact(ptr, invId, 0.toByte()) as MemorySegment).address() != 0L
}