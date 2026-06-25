package com.undercut.cache.type.headbars

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.getString
import com.undercut.cache.getTriByte
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class HeadbarTypeParser : TypeParser<HeadbarType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 33

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): HeadbarType {
        val def = HeadbarType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> def.spriteId = buffer.getSmartInt()
                2 -> {
                    def.hasColor = true
                    def.color = buffer.getTriByte()
                }
                3 -> def.spriteId2 = buffer.getSmartInt()
                4 -> def.spriteId3 = buffer.getSmartInt()
                5 -> def.spriteId4 = buffer.getSmartInt()
                6 -> def.spriteId5 = buffer.getSmartInt()
                7 -> def.field7 = buffer.short.toInt() // signed short
                8 -> buffer.getString() // string, not stored
                9 -> def.height = buffer.short.toInt() and 0xFFFF
                10 -> def.field10 = buffer.short.toInt() // signed short
                11 -> def.field11 = 0
                12 -> def.field12 = buffer.get().toInt() and 0xFF
                13 -> def.field13 = buffer.short.toInt() // signed short
                14 -> def.field14 = buffer.short.toInt() and 0xFFFF
                15 -> Unit // no-op
                16 -> {
                    def.field16a = buffer.short.toInt() // signed short
                    def.field16b = buffer.short.toInt() // signed short
                }
                17, 18 -> {
                    // Stacked headbar sprites
                    var id1 = buffer.short.toInt() and 0xFFFF
                    if (id1 == 0xFFFF) id1 = -1
                    var id2 = buffer.short.toInt() and 0xFFFF
                    if (id2 == 0xFFFF) id2 = -1
                    val id3 = if (opcode == 18) {
                        var v = buffer.short.toInt() and 0xFFFF
                        if (v == 0xFFFF) v = -1
                        v
                    } else -1
                    val count = buffer.get().toInt() and 0xFF
                    // Read count ushorts (sprite IDs), 0xFFFF → -1
                    repeat(count) { buffer.short }
                }
                19 -> def.field19 = buffer.short.toInt() // signed short
                20 -> def.field20 = buffer.short.toInt() // signed short
                else -> throw IllegalArgumentException("Invalid HeadbarType opcode $opcode")
            }
        }
        return def
    }
}
