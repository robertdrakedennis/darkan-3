package com.undercut.cache.type.inventories

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class InvTypeParser : TypeParser<InvType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 5

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): InvType {
        val def = InvType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                2 -> def.size = buffer.short.toInt() and 0xFFFF
                4 -> {
                    val count = buffer.get().toInt() and 0xFF
                    val items = mutableMapOf<Int, Int>()
                    repeat(count) {
                        val itemId = buffer.short.toInt() and 0xFFFF
                        val amount = buffer.short.toInt() and 0xFFFF
                        items[itemId] = amount
                    }
                    def.items = items
                }
                else -> throw IllegalArgumentException("Invalid InvType opcode $opcode")
            }
        }
        return def
    }
}
