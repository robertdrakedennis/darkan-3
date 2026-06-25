package com.undercut.cache.tools

import com.undercut.cache.type.structs.StructType
import com.undercut.game.interfaces.effects.Effect

private const val BUFF_NAME_PARAM = 2794
private const val IS_DEBUFF_PARAM = 8109
private const val BUFF_CATEGORY_PARAM = 8114
private const val BUFFBAR_ITEM_ID_PARAM = 4677

fun main(args: Array<String>) {
    val effects = mutableSetOf<StructType>()
    for (structId in 0..StructType.getParser().getMaxId()) {
        try {
            val struct = StructType.get(structId)
            val buffName = struct.params.getString(BUFF_NAME_PARAM)
            if (buffName == "null" || buffName.isEmpty()) continue
            if (Effect[struct.id] == null)
                effects.add(struct)
        } catch (e: Throwable) {

        }
    }

    println("enum class Effect(val structId: Int, val itemId: Int, val isDebuff: Boolean) {")
    effects.forEach { struct ->
        val buffName = struct.params.getString(BUFF_NAME_PARAM)
        println("\t${buffName.clean()}(${struct.id}, ${struct.params.getInt(BUFFBAR_ITEM_ID_PARAM, -1)}, ${struct.params.getInt(IS_DEBUFF_PARAM) == 1}),")
    }
    println("}")
}

private fun String.clean(): String {
    val strBeforeSeparator = when {
        contains(" - ") -> substringBefore(" - ")
        contains("<br>") -> substringBefore("<br>")
        else -> this
    }
    return strBeforeSeparator.replace(Regex("[^A-Za-z0-9\\s]"), "").replace("\\s+".toRegex(), "_").uppercase()
}