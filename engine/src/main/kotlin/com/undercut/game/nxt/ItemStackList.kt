package com.undercut.game.nxt

import com.undercut.game.Tile
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.entity.ItemStack
import java.lang.foreign.MemorySegment

class ItemStackList(val ptr: MemorySegment) : Iterable<MemorySegment> {
    val rbTreeBase
        get() = ptr.deref(OItemStackList.RB_TREE_BASE, 0x100L)

    override fun iterator(): Iterator<MemorySegment> {
        return object : Iterator<MemorySegment> {
            private var current = findLeftmostNode(rbTreeBase)
            private val treeRoot = rbTreeBase

            override fun hasNext() = current != MemorySegment.NULL

            override fun next(): MemorySegment {
                if (!hasNext()) throw NoSuchElementException()

                val result = current
                current = getNextNode(current)
                return result
            }

            private fun findLeftmostNode(root: MemorySegment): MemorySegment {
                if (root == MemorySegment.NULL) return MemorySegment.NULL
                var current = root
                var leftChild = current.deref(OItemStackNode.LEFT, 0x100L)
                while (leftChild != MemorySegment.NULL) {
                    current = leftChild
                    leftChild = current.deref(OItemStackNode.LEFT, 0x100L)
                }
                return current
            }

            private fun getNextNode(node: MemorySegment): MemorySegment {
                val rightChild = node.deref(OItemStackNode.RIGHT, 0x100L)
                if (rightChild != MemorySegment.NULL) {
                    return findLeftmostNode(rightChild)
                }

                var current = node
                var parent = current.deref(OItemStackNode.PARENT, 0x100L)

                while (parent != MemorySegment.NULL) {
                    if (current.address() == treeRoot.address())
                        return MemorySegment.NULL

                    val parentLeft = parent.deref(OItemStackNode.LEFT, 0x100L)
                    if (parentLeft.address() == current.address())
                        return parent

                    current = parent
                    parent = current.deref(OItemStackNode.PARENT, 0x100L)
                }

                return MemorySegment.NULL
            }
        }
    }

    val allGroundItems
        get() = this.map { ItemStackNode(it).itemStack.groundItems }.flatten()
}

class ItemStackNode(val ptr: MemorySegment) {
    val left
        get() = ptr.deref(OItemStackNode.LEFT, 0x100L)
    val right
        get() = ptr.deref(OItemStackNode.RIGHT, 0x100L)
    val parent
        get() = ptr.deref(OItemStackNode.PARENT, 0x100L)
    val tile: Tile
        get() = Tile.of(ptr.readInt(OItemStackNode.POS_X), ptr.readInt(OItemStackNode.POS_Y), ptr.readInt(OItemStackNode.POS_PLANE))
    val itemStack
        get() = ItemStack(ptr.deref(OItemStackNode.ITEM_STACK, 0x100L))
}