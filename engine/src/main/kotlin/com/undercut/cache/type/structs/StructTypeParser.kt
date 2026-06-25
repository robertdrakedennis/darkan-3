package com.undercut.cache.type.structs

import com.undercut.cache.Index
import com.undercut.cache.type.TypeParser
import java.nio.ByteBuffer

class StructTypeParser : TypeParser<StructType>() {

    override fun getIndex(): Index = Index.STRUCT_DEF

    override fun getArchiveBitSize(): Int = 5

    override fun decode(id: Int, buffer: ByteBuffer): StructType {
        val def = StructType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xff) {
                0 -> break
                249 -> def.params.parse(buffer)
                else -> throw IllegalArgumentException("Invalid StructType opcode $opcode")
            }
        }
        return def
    }
}