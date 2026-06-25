package com.undercut.cache.type.hitmarks

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.getString
import com.undercut.cache.getTriByte
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class HitmarkTypeParser : TypeParser<HitmarkType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 29

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): HitmarkType {
        val def = HitmarkType(id = id)
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
                    // Stacked hitmark sprites
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
                21 -> {
                    val v = buffer.int
                    def.field21 = (v shl 8) or (v ushr 24)
                }
                22 -> {
                    val v = buffer.int
                    def.field22 = (v shl 8) or (v ushr 24)
                }
                23 -> {
                    // 3 bytes for sub-structure fields
                    def.field23a = buffer.get().toInt() and 0xFF
                    def.field23b = buffer.get().toInt() and 0xFF
                    def.field23c = buffer.get().toInt() and 0xFF
                }
                24 -> {
                    def.field24a = buffer.short.toInt() shl 9
                    def.field24b = buffer.short.toInt() shl 9
                }
                25 -> def.field25 = buffer.getSmartInt()
                26, 27 -> {
                    // Stacked hitmark sprites (same pattern as opcodes 17/18)
                    var id1 = buffer.short.toInt() and 0xFFFF
                    if (id1 == 0xFFFF) id1 = -1
                    var id2 = buffer.short.toInt() and 0xFFFF
                    if (id2 == 0xFFFF) id2 = -1
                    val id3 = if (opcode == 27) {
                        var v = buffer.short.toInt() and 0xFFFF
                        if (v == 0xFFFF) v = -1
                        v
                    } else -1
                    val count = buffer.get().toInt() and 0xFF
                    repeat(count) { buffer.short }
                }
                28 -> def.field28 = buffer.get().toInt() and 0xFF
                29 -> def.field29 = buffer.get().toInt() and 0xFF
                30 -> def.field30 = buffer.get().toInt() and 0xFF
                249 -> def.params.parse(buffer)
                else -> throw IllegalArgumentException("Invalid HitmarkType opcode $opcode")
            }
        }
        return def
    }
}
