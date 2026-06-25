package com.undercut.cache.type.cursors

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.getSmartInt
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class CursorTypeParser : TypeParser<CursorType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveId(id: Int): Int = 34

    override fun getFileId(id: Int): Int = id

    override fun getArchiveBitSize(): Int = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): CursorType {
        val def = CursorType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xFF) {
                0 -> break
                1 -> def.spriteId = buffer.getSmartInt()
                2 -> {
                    def.hotspotX = buffer.get().toInt() and 0xFF
                    def.hotspotY = buffer.get().toInt() and 0xFF
                }
                else -> throw IllegalArgumentException("Invalid CursorType opcode $opcode")
            }
        }
        return def
    }
}
