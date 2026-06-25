package com.undercut.cache.type.params

import com.undercut.cache.Index
import com.undercut.cache.cp1252ToChar
import com.undercut.cache.getString
import com.undercut.cache.getUnsignedSmart
import com.undercut.cache.type.TypeParser
import com.undercut.cache.type.vars.CS2VarType
import java.nio.ByteBuffer

class ParamTypeParser : TypeParser<ParamType>() {

    override fun getIndex(): Index = Index.CONFIG

    override fun getArchiveBitSize(): Int = 1

    override fun getArchiveId(id: Int): Int = 11

    override fun getFileId(id: Int): Int = id

    override fun decode(id: Int, buffer: ByteBuffer): ParamType {
        val def = ParamType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xff) {
                0 -> break
                1 -> {
                    val c = buffer.get().cp1252ToChar()
                    def.type = CS2VarType.getByChar(c)
                    def.typeId = def.type?.id ?: 0
                }
                101 -> {
                    def.typeId = buffer.getUnsignedSmart()
                    def.type = CS2VarType.getById(def.typeId)
                }
                2 -> def.defaultInt = buffer.int
                4 -> def.autoDisable = false
                5 -> def.defaultString = buffer.getString()
                else -> throw IllegalArgumentException("Invalid ParamDefinition opcode $opcode -> ${buffer.array().contentToString()}")
            }
        }
        return def
    }
}