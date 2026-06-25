package com.undercut.cache.type.enums

import com.undercut.cache.*
import com.undercut.cache.type.TypeParser
import com.undercut.cache.type.vars.CS2VarType
import java.nio.ByteBuffer

class EnumTypeParser : TypeParser<EnumType>() {

    override fun getIndex(): Index = Index.ENUM_DEF

    override fun getArchiveBitSize(): Int = 8

    override fun decode(id: Int, buffer: ByteBuffer): EnumType {
        val def = EnumType(id = id)
        while (buffer.hasRemaining()) {
            when (val opcode = buffer.get().toInt() and 0xff) {
                0 -> break
                1 -> def.keyType = CS2VarType.getByChar(buffer.get().cp1252ToChar())
                2 -> def.valueType = CS2VarType.getByChar(buffer.get().cp1252ToChar())
                3 -> def.defaultString = buffer.getString()
                4 -> def.defaultInt = buffer.getInt()
                5, 6, 7, 8 -> {
                    val stringValues = opcode == 5 || opcode == 7
                    if (opcode == 7 || opcode == 8) buffer.skip(2)
                    val size = buffer.short.toInt() and 0xffff
                    repeat(size) {
                        val key = if (opcode == 5 || opcode == 6) buffer.getInt() else buffer.short.toInt() and 0xffff
                        val value: Any = if (stringValues) buffer.getString() else buffer.getInt()
                        def.values[key] = value
                    }
                }
                101 -> {
                    val type = buffer.getSmallSmartInt()
                    def.keyType = CS2VarType.getById(type)
                }
                102 -> {
                    val type = buffer.getSmallSmartInt()
                    def.valueType = CS2VarType.getById(type)
                }
                else -> throw IllegalArgumentException("Invalid EnumDefinition opcode $opcode")
            }
        }
        return def
    }
}