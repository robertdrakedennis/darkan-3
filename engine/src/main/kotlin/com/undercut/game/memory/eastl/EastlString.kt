package com.undercut.game.memory.eastl

import com.undercut.game.memory.NativeAccess.readByte
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

/**
 * Reader for an embedded EASTL `basic_string<char>` (1-byte chars, 24-byte union).
 *
 * The union is either heap or small-string-optimised, distinguished by the top bit of
 * the byte at [EastlOffsets.SIZE]:
 *  - **Heap**: `{ char* begin (0x00); size_t size (0x08); size_t capacity (0x10) }`. The
 *    capacity's top bit is set, which surfaces as bit 0x80 of the size byte.
 *  - **SSO**: 23 inline char bytes; the size byte holds `SSO_CAPACITY - size` (top bit
 *    clear). A full 23-char string therefore stores 0 there, which doubles as the NUL
 *    terminator — so the size must be *derived*, not read directly, or full inline
 *    strings look empty.
 */
class EastlString(private val ptr: MemorySegment) : CharSequence {

    private val sizeByte: Int
        get() = ptr.readByte(EastlOffsets.SIZE).toInt() and 0xFF

    fun isHeap(): Boolean = sizeByte and 0x80 != 0
    fun isSSO(): Boolean = !isHeap()

    override val length: Int
        get() = if (isHeap()) ptr.get(ValueLayout.JAVA_LONG, EastlOffsets.HEAP_SIZE).toInt()
                else EastlOffsets.SSO_CAPACITY - sizeByte

    override fun get(index: Int): Char {
        val data = if (isHeap()) ptr.get(ValueLayout.ADDRESS, 0).reinterpret((length + 1).toLong()) else ptr
        return data.get(ValueLayout.JAVA_BYTE, index.toLong()).toInt().toChar()
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        toString().subSequence(startIndex, endIndex)

    override fun toString(): String {
        val len = length
        if (len <= 0) return ""
        val data = if (isHeap()) ptr.get(ValueLayout.ADDRESS, 0).reinterpret((len + 1).toLong()) else ptr
        return data.getString(0)
    }

    object EastlOffsets {
        const val SIZE = 0x17L
        const val HEAP_SIZE = 0x08L
        const val SSO_CAPACITY = 23
    }
}
