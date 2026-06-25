package com.undercut.cache.type.vars

import com.undercut.cache.Cache
import com.undercut.cache.Index
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class VarbitDefParser : TypeParser<VarbitType>() {

    override fun getIndex() = Index.CONFIG

    override fun getArchiveId(id: Int) = 69

    override fun getFileId(id: Int) = id

    override fun getArchiveBitSize() = 1

    override fun getMaxId(): Int {
        return Cache.get()
            .getArchive(getIndex(), getArchiveId(0))
            .files.keys.maxOrNull() ?: 0
    }

    override fun decode(id: Int, buffer: ByteBuffer): VarbitType {
        return VarbitType().apply {
            this.id = id
            while (buffer.hasRemaining()) {
                when (val opcode = buffer.get().toInt() and 0xff) {
                    0 -> break
                    1 -> {
                        domainId = (buffer.get().toInt() and 0xff).toByte()
                        domain = VarDomain.forId(domainId.toInt())
                        baseVar = buffer.short.toInt() and 0xffff
                    }
                    2 -> {
                        startBit = buffer.get().toInt() and 0xff
                        endBit = buffer.get().toInt() and 0xff
                    }
                    16 -> flags = buffer.get().toInt() and 0xff
                    else -> throw IllegalArgumentException("Invalid VarbitDefinition opcode $opcode")
                }
            }
        }
    }
}