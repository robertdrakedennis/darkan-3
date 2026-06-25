package com.undercut.cache.type.params

import com.undercut.cache.getString
import com.undercut.cache.getTriByte
import com.undercut.cache.type.items.ItemType
import com.undercut.cache.type.npcs.NPCType
import com.undercut.cache.type.vars.CS2VarType
import com.undercut.game.Skill
import com.undercut.game.Tile
import java.nio.ByteBuffer

class Params {

    val map: MutableMap<Int, Any> = mutableMapOf()

    fun parse(buffer: ByteBuffer) {
        val size = buffer.get().toInt() and 0xFF
        repeat(size) {
            val isString = (buffer.get().toInt() and 0xFF) == 1
            val key = buffer.getTriByte()
            val value = if (isString) buffer.getString() else buffer.int
            map[key] = value
        }
    }

    fun valToType(id: Int, value: Any?): Any? {
        val paramType = ParamType.get(id).type
        return when (paramType) {
            CS2VarType.COMPONENT -> value as? String ?: formatComponent(value as Int)
            CS2VarType.WORLDTILE -> value as? String ?: Tile.of(value as Int)
            CS2VarType.STAT -> value as? String ?: formatStat(value as Int)
            CS2VarType.ITEM -> value as? String ?: formatItem(value as Int)
            CS2VarType.NPC -> value as? String ?: formatNPC(value as Int)
            CS2VarType.STRUCT -> value // Uncomment if additional formatting is required for STRUCT
            else -> value
        }
    }

    private fun formatComponent(value: Int): String {
        val interfaceId = value shr 16
        val componentId = value and 0xFFFF
        return "IComponent($interfaceId, $componentId)"
    }

    private fun formatStat(value: Int): String {
        return if (value >= Skill.entries.size) value.toString()
        else "$value(${Skill.entries[value]})"
    }

    private fun formatItem(value: Int): String {
        return "$value" + if (value != -1) " (${ItemType.get(value).name})" else ""
    }

    private fun formatNPC(value: Int): String {
        return "$value" + if (value != -1) " (${NPCType.get(value).name})" else ""
    }

    override fun toString(): String {
        return map.entries.joinToString(prefix = "{ ", postfix = " }") { (key, value) ->
            "$key (${ParamType.get(key)?.type}) = ${valToType(key, value)}"
        }
    }

    fun get(id: Int): Any? = map[id]

    fun getString(opcode: Int, defaultVal: String): String {
        return (map[opcode] as? String) ?: defaultVal
    }

    fun getString(opcode: Int): String {
        return (map[opcode] as? String) ?: "null"
    }

    fun getInt(opcode: Int, defaultVal: Int): Int {
        return (map[opcode] as? Int) ?: defaultVal
    }

    fun getInt(opcode: Int): Int {
        return (map[opcode] as? Int) ?: 0
    }
}