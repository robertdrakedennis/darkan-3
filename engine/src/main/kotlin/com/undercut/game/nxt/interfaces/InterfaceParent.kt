package com.undercut.game.nxt.interfaces

import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readLong
import com.undercut.game.memory.NativeAccess.toMemorySegment
import com.undercut.game.nxt.OInterfaceParent
import java.lang.foreign.MemorySegment

class InterfaceParent(val id: Int, val ptr: MemorySegment) : Iterable<InterfaceComponent> {
    // Reinterpret to cover the vector triple at +0x8..+0x20; callers (e.g. InterfaceList) hand
    // us a segment sized only for shared_ptr cell (0x10), too small to read end at +0x10.
    private val struct: MemorySegment = ptr.reinterpret(STRUCT_BYTES)
    private val begin: Long get() = struct.readLong(OInterfaceParent.CHILD_ARRAY_BEGIN)
    private val end: Long get() = struct.readLong(OInterfaceParent.CHILD_ARRAY_END)

    /** Number of slots in the parent's component vector — `(end - begin) / 0x18`. */
    val size: Int
        get() {
            val b = begin
            val e = end
            if (b == 0L || e == 0L || e < b) return 0
            return ((e - b) / OInterfaceParent.CHILD_SLOT_STRIDE).toInt()
        }

    operator fun get(componentId: Int): InterfaceComponent? {
        if (componentId < 0) return null
        val b = begin
        val e = end
        if (b == 0L || e == 0L) return null
        val slotAddr = b + componentId * OInterfaceParent.CHILD_SLOT_STRIDE
        if (slotAddr + OInterfaceParent.CHILD_SLOT_STRIDE > e) return null
        val slot = slotAddr.toMemorySegment(OInterfaceParent.CHILD_SLOT_STRIDE)
        if (slot.readByte(OInterfaceParent.CHILD_SLOT_REMOVED_FLAG) != 0.toByte()) return null
        val compAddr = slot.readLong(OInterfaceParent.CHILD_SLOT_PTR)
        if (compAddr == 0L) return null
        return InterfaceComponent(compAddr.toMemorySegment(0x200L))
    }

    /**
     * Walks the parent's component vector from begin to end. Matches the iteration shape
     * jag::InterfaceManager::DrawSlotChildren uses: skip slots whose `removed` flag is set,
     * skip null pointers, yield the rest.
     */
    override fun iterator(): Iterator<InterfaceComponent> = object : Iterator<InterfaceComponent> {
        private val total = size
        private var index = 0
        private var pending: InterfaceComponent? = null

        private fun advance() {
            while (index < total) {
                val c = get(index)
                index++
                if (c != null) {
                    pending = c
                    return
                }
            }
            pending = null
        }

        override fun hasNext(): Boolean {
            if (pending != null) return true
            advance()
            return pending != null
        }

        override fun next(): InterfaceComponent {
            if (pending == null) advance()
            val v = pending ?: throw NoSuchElementException()
            pending = null
            return v
        }
    }

    private companion object {
        const val STRUCT_BYTES = 0x40L
    }
}
