package com.undercut.game.memory.eastl

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import java.lang.foreign.MemorySegment

object OEastlLinkedList {
    const val SIZE = 0x40L
}

class EastlLinkedList(val ptr: MemorySegment) : Iterable<EastlLinkedListNode> {
    val startPtr
        get() = ptr.deref(0x0, 0x1B0)
    val size
        get() = ptr.readInt(OEastlLinkedList.SIZE)

    override fun iterator(): Iterator<EastlLinkedListNode> {
        return object : Iterator<EastlLinkedListNode> {
            private var currentNode: EastlLinkedListNode? = if (isListEmpty()) null else EastlLinkedListNode(startPtr)

            private fun isListEmpty() = startPtr.address() == startPtr.deref(0x0L, 0x8L).address()

            override fun hasNext() = currentNode != null

            override fun next(): EastlLinkedListNode {
                val node = currentNode ?: throw NoSuchElementException("No more elements")
                val nextPtr = node.next.ptr
                currentNode = if (nextPtr.address() == 0L || nextPtr.address() == startPtr.deref(0x0L, 0x8L).address()) null else EastlLinkedListNode(nextPtr)
                return node
            }
        }
    }
}

object OEastlLinkedListNode {
    const val NEXT = 0x0L
    const val PREV = 0x8L
    const val VALUE = 0x10L
}

class EastlLinkedListNode(val ptr: MemorySegment) {
    val next
        get() = EastlLinkedListNode(ptr.deref(OEastlLinkedListNode.NEXT, 0x18L))
    val prev
        get() = EastlLinkedListNode(ptr.deref(OEastlLinkedListNode.PREV, 0x18L))
    fun value(size: Long) = ptr.pointerAtOffset(OEastlLinkedListNode.VALUE, size)
}