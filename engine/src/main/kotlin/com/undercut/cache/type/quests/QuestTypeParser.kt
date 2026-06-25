package com.undercut.cache.type.quests

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.getString
import com.undercut.cache.skip
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class QuestTypeParser : TypeParser<QuestType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 35

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): QuestType {
        val def = QuestType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> def.name = buffer.getString()
                2 -> def.description = buffer.getString()
                3, 4 -> {
                    // MasterQuestVar array: count * {ushort, uint, uint} = 10 bytes each.
                    // Captured into varEntries / varEntriesAlt for quest-stage detection.
                    val count = buffer.get().toInt() and 0xFF
                    val sink = if (opcode == 3) def.varEntries else def.varEntriesAlt
                    repeat(count) {
                        val operator = buffer.short.toInt() and 0xFFFF
                        val varId = buffer.int
                        val value = buffer.int
                        sink += QuestVarEntry(operator, varId, value)
                    }
                }
                5 -> def.sortKey = buffer.short.toInt() and 0xFFFF
                6 -> def.difficulty = buffer.get().toInt() and 0xFF
                7 -> def.members = buffer.get().toInt() and 0xFF
                8 -> def.questFlags = true
                9 -> def.questPointReward = buffer.get().toInt() and 0xFF
                10 -> {
                    // Child quest coords: count * uint (4 bytes each)
                    val count = buffer.get().toInt() and 0xFF
                    buffer.skip(count * 4)
                }
                12 -> buffer.int // coordGrid (uint)
                13 -> {
                    // Quest point array: count * ushort (2 bytes each)
                    val count = buffer.get().toInt() and 0xFF
                    buffer.skip(count * 2)
                }
                14 -> {
                    // Achievement array: count * 2 bytes (byte pairs)
                    val count = buffer.get().toInt() and 0xFF
                    buffer.skip(count * 2)
                }
                15 -> buffer.short // ushort
                17 -> def.spriteId = buffer.getSmartInt()
                18, 19 -> {
                    // PrerequisiteVar array: count * {uint, uint, uint, string}
                    val count = buffer.get().toInt() and 0xFF
                    repeat(count) {
                        buffer.int   // uint
                        buffer.int   // uint
                        buffer.int   // uint
                        buffer.getString() // CP1252 string
                    }
                }
                249 -> def.params.parse(buffer)
                else -> throw IllegalArgumentException("Invalid QuestType opcode $opcode")
            }
        }
        return def
    }
}
