package com.undercut.cache.type.enums

import com.undercut.cache.Cache
import com.undercut.cache.type.items.ItemType
import com.undercut.cache.type.npcs.NPCType
import com.undercut.cache.type.structs.StructType
import com.undercut.cache.type.vars.CS2VarType
import com.undercut.game.Skill
import com.undercut.game.Tile

data class EnumType(
    var id: Int = 0,
    var keyType: CS2VarType = CS2VarType.INT,
    var valueType: CS2VarType = CS2VarType.INT,
    var defaultInt: Int = 0,
    var defaultString: String? = null,
    var values: MutableMap<Int, Any> = mutableMapOf()
) {
    companion object {
        private val parser = EnumTypeParser()

        fun getParser(): EnumTypeParser = parser

        fun get(id: Int): EnumType = parser.get(Cache.get(), id)
    }

    override fun toString(): String {
        if (values.isEmpty()) return "null"

        return buildString {
            append("<$keyType, $valueType> - $defaultString - $defaultInt { \n")
            for ((key, value) in values) {
                append("${keyToType(key.toLong())} = ${valToType(value)}\n")
            }
            append("}\n")
        }
    }

    fun keyToType(l: Long): Any = when (keyType) {
        CS2VarType.INTERFACE -> {
            val interfaceId = l shr 16
            val componentId = l and 0xFFFF
            "IComponent($interfaceId, $componentId)"
        }
        CS2VarType.WORLDTILE -> Tile.of(l.toInt())
        CS2VarType.STAT -> Skill.entries[l.toInt()]
        CS2VarType.ITEM -> l // Placeholder for item lookup
        CS2VarType.NPC -> l // Placeholder for NPC lookup
        CS2VarType.STRUCT -> "$l: ${StructType.get(l.toInt())}"
        else -> l
    }

    fun valToType(o: Any): Any = when (valueType) {
        CS2VarType.COMPONENT -> {
            if (o is String) o
            else {
                val interfaceId = (o as Int) shr 16
                val componentId = o and 0xFFFF
                "IComponent($interfaceId, $componentId)"
            }
        }
        CS2VarType.WORLDTILE -> o as? String ?: Tile.of(o as Int)
        CS2VarType.STAT -> o as? String ?: Skill.entries[o as Int]
        CS2VarType.ITEM -> o as? String ?: "$o (${ItemType.get(o as Int).name})"
        CS2VarType.NPC -> o as? String ?: "$o (${NPCType.get(o as Int).name})"
        CS2VarType.STRUCT -> "$o: ${StructType.get(o as Int)}"
        else -> o
    }
}