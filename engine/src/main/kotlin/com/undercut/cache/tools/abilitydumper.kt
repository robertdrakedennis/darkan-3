package com.undercut.cache.tools

import com.undercut.cache.type.enums.EnumType
import com.undercut.cache.type.structs.StructType
import com.undercut.game.interfaces.AbilityGroup

private const val ABILITY_SLOT_PARAM = 2793
private const val ABILITY_NAME_PARAM = 2794
private const val ABILITY_DESCRIPTION_PARAM = 2795
private const val ABILITY_SKILL_REQ_PARAM = 2806
private const val ABILITY_LEVEL_REQ_PARAM = 2807

fun main(args: Array<String>) {
    val enumsUsed = mutableSetOf<Int>()
    AbilityGroup.entries.forEach { group ->
        if (enumsUsed.contains(group.enumId)) return@forEach
        enumsUsed.add(group.enumId)
        try {
            val enumType = EnumType.get(group.enumId).values
            enumType.keys.forEach { slotId ->
                val abilityStructId: Int = enumType[slotId] as? Int ?: return@forEach
                val abilityStruct = StructType.get(abilityStructId)
                val abilityName = abilityStruct.params.getString(ABILITY_NAME_PARAM)
                println("${abilityName.clean()}(\"$abilityName\", ${group.enumId}, $slotId, $abilityStructId),")
            }
        } catch(e: Throwable) {
            return@forEach
        }
    }
}

private fun String.clean() = replace(Regex("[^A-Za-z0-9\\s]"), "").replace("\\s+".toRegex(), "_").uppercase()