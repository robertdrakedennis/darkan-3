package com.undercut.game.nxt.entity

import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OItemStack
import com.undercut.game.nxt.types.Vector
import java.lang.foreign.MemorySegment

class ItemStack(ptr: MemorySegment) : Entity(ptr) {
    val itemVector
        get() = Vector(ptr.pointerAtOffset(OItemStack.ITEM_VECTOR, 0x20L), OItemStack.ITEM_VECTOR_ELEM_SIZE)
    val groundItems
        get() = itemVector.map { GroundItem(it.readInt(OItemStack.ITEM_ID), it.readInt(OItemStack.ITEM_AMOUNT), tile) }
    override val size get() = 0
}