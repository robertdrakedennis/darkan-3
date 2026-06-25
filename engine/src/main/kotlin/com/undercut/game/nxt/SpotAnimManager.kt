package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readLong
import java.lang.foreign.MemorySegment

class SpotAnimManager(internal val ptr: MemorySegment) : Iterable<MemorySegment> {
    val vtable
        get() = ptr.pointerAtOffset(OSpotAnimManager.VTABLE, 0x8L)
    val firstNode
        get() = ptr.deref(OSpotAnimManager.FIRST_NODE, 0x18L)
    val listSize
        get() = ptr.readLong(OSpotAnimManager.LIST_SIZE)

    override fun iterator(): Iterator<MemorySegment> {
        return object : Iterator<MemorySegment> {
            private var currentNode = firstNode
            private val endNode = ptr.pointerAtOffset(OSpotAnimManager.FIRST_NODE, 0x18L)

            override fun hasNext() = currentNode.address() != endNode.address()

            override fun next(): MemorySegment {
                if (!hasNext()) throw NoSuchElementException()

                val node = SpotAnimNode(currentNode)
                currentNode = node.next
                return node.value
            }
        }
    }
}

class SpotAnimNode(val ptr: MemorySegment) {
    val next
        get() = ptr.deref(OSpotAnimNode.NEXT, 0x18L)
    val value
        get() = ptr.pointerAtOffset(OSpotAnimNode.VALUE, 0x200L)
}