package com.undercut.cache.type.idk

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class IDKTypeParser : TypeParser<IDKType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 3

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): IDKType {
        val def = IDKType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> def.bodyPart = buffer.get().toInt() and 0xFF
                2 -> {
                    val count = buffer.get().toInt() and 0xFF
                    def.modelIds = IntArray(count) { buffer.getSmartInt() }
                }
                3 -> def.nonSelectable = true
                in 4..39, in 42..43, in 46..59 -> Unit // no-op ranges from binary
                40 -> { // 0x28 - recolor pairs
                    val count = buffer.get().toInt() and 0xFF
                    def.recolorSrc = IntArray(count)
                    def.recolorDst = IntArray(count)
                    for (i in 0 until count) {
                        def.recolorSrc!![i] = buffer.short.toInt() and 0xFFFF
                        def.recolorDst!![i] = buffer.short.toInt() and 0xFFFF
                    }
                }
                41 -> { // 0x29 - retexture pairs
                    val count = buffer.get().toInt() and 0xFF
                    def.retextureSrc = IntArray(count)
                    def.retextureDst = IntArray(count)
                    for (i in 0 until count) {
                        def.retextureSrc!![i] = buffer.short.toInt() and 0xFFFF
                        def.retextureDst!![i] = buffer.short.toInt() and 0xFFFF
                    }
                }
                44 -> buffer.short // 0x2c - recolor bitmask (derived from mask, no extra reads)
                45 -> buffer.short // 0x2d - retexture bitmask (derived from mask, no extra reads)
                in 60..64 -> { // 0x3c-0x40 - head model IDs
                    def.headModelIds[opcode - 60] = buffer.getSmartInt()
                }
                else -> throw IllegalArgumentException("Invalid IDKType opcode $opcode")
            }
        }
        return def
    }
}
