package com.undercut.game.interfaces

import com.undercut.script.api.varps
import world.gregs.voidps.cache.Cache

private const val GCD_START_TICK_VAR = 4501
private const val GCD_END_TICK_VAR = 4502

private const val ABILITY_QUEUED_BAR_ID_VAR = 5861
private const val ABILITY_QUEUED_SLOT_VAR = 4164

private const val MAIN_ACTIONBAR_BAR_VARBIT = 1893
private const val ADDITIONAL_ACTIONBAR_1_BAR_VARBIT = 29138
private const val ADDITIONAL_ACTIONBAR_2_BAR_VARBIT = 29139
private const val ADDITIONAL_ACTIONBAR_3_BAR_VARBIT = 29140
private const val ADDITIONAL_ACTIONBAR_4_BAR_VARBIT = 29141

private const val ABILITY_SLOT_PARAM = 2793
private const val ABILITY_NAME_PARAM = 2794
private const val ABILITY_DESCRIPTION_PARAM = 2795
private const val ABILITY_SKILL_REQ_PARAM = 2806
private const val ABILITY_LEVEL_REQ_PARAM = 2807
const val ABILITY_SECONDARY_GLOBAL_COOLDOWN_PARAM = 2976

fun parseAllActionBarAbilities(): Map<Ability, IFSlot> {
    fun MutableMap<Ability, IFSlot>.parseBar(barLocNum: Int, barNum: Int) {
        mappings.keys.filter { it.first == barNum }.forEach { key ->
            val values = getSlotValues(barNum, key.second)
            if (values.groupId != 0 && values.groupId != 10) {
                val group = AbilityGroup.byId[values.groupId] ?: return@forEach println("Unknown ability group id: ${values.groupId} at $barNum, ${key.second}")
                var abilityStructId = Cache.enum(group.enumId)?.values?.get(values.slotId) as? Int ?: return@forEach println("Missing ability enum id: ${group.enumId} at $barNum, ${key.second}")
                abilityStructId = checkTransformedAbilities(abilityStructId)
                Cache.struct(abilityStructId) ?: return@forEach println("Missing ability struct id: $abilityStructId at $barNum, ${key.second}")
                val slotsMapping = actionbarSlots[barLocNum] ?: return
                val slot = slotsMapping[key.second] ?: return@forEach println("Unknown ability IFSlot at $barNum, ${key.second}")
                val ability = Ability.byStructId[abilityStructId] ?: return@forEach println("Unknown ability byStructId id: $abilityStructId, ${key.second}")
                put(ability, slot)
            }
        }
    }
    return buildMap {
        val primaryBarNum = varps.getVarBit(MAIN_ACTIONBAR_BAR_VARBIT)
        if (primaryBarNum > 0)
            parseBar(1, primaryBarNum)
        val additionalBar1 = varps.getVarBit(ADDITIONAL_ACTIONBAR_1_BAR_VARBIT)
        if (additionalBar1 > 0)
            parseBar(2, additionalBar1)
        val additionalBar2 = varps.getVarBit(ADDITIONAL_ACTIONBAR_2_BAR_VARBIT)
        if (additionalBar2 > 0)
            parseBar(3, additionalBar2)
        val additionalBar3 = varps.getVarBit(ADDITIONAL_ACTIONBAR_3_BAR_VARBIT)
        if (additionalBar3 > 0)
            parseBar(4, additionalBar3)
        val additionalBar4 = varps.getVarBit(ADDITIONAL_ACTIONBAR_4_BAR_VARBIT)
        if (additionalBar4 > 0)
            parseBar(5, additionalBar4)
    }
}

private fun checkTransformedAbilities(abilityStructId: Int): Int {
    return when (abilityStructId) {
        48302 -> if (varps.getVar(10994) == 1 && varps.getVarBit(53572) == 1) 48303 else abilityStructId
        48304 -> if (varps.getVar(11006) == 1 && varps.getVarBit(53574) == 1) 48305 else abilityStructId
        48306 -> if (varps.getVar(11018) == 1 && varps.getVarBit(53576) == 1) 48307 else abilityStructId
        31820 -> if (varps.getVar(11820) == 1 && varps.getVarBit(55974) == 1) 32342 else abilityStructId
        48311, 48312, 48313 -> {
            when {
                varps.getVar(11051) == 1 -> 48312
                varps.getVar(11054) == 1 -> 48313
                else -> 48311
            }
        }
        else -> abilityStructId
    }
}

enum class AbilityGroup(val id: Int, val enumId: Int) {

    MELEE(1, 10307),
    DEFENSE(3, 6736),
    HITPOINTS(4, 6737),
    HP_ORB(12, 6737),
    RANGED(5, 6738),
    MAGIC(6, 6740),
    TELEPORTS(11, 6740),
    PRAYER(7, 6739),
    FAMILIAR(13, 11278),
    NECROMANCY(17, 16973);


    companion object {
        val byId = entries.associateBy { it.id }
    }
}

val actionbarSlots = mapOf(
    1 to mapOf(
        1 to IFSlot(1430, 64),
        2 to IFSlot(1430, 77),
        3 to IFSlot(1430, 90),
        4 to IFSlot(1430, 103),
        5 to IFSlot(1430, 116),
        6 to IFSlot(1430, 129),
        7 to IFSlot(1430, 142),
        8 to IFSlot(1430, 155),
        9 to IFSlot(1430, 168),
        10 to IFSlot(1430, 181),
        11 to IFSlot(1430, 194),
        12 to IFSlot(1430, 207),
        13 to IFSlot(1430, 220),
        14 to IFSlot(1430, 233)
    ),
    2 to mapOf(
        1 to IFSlot(1670, 21),
        2 to IFSlot(1670, 34),
        3 to IFSlot(1670, 47),
        4 to IFSlot(1670, 60),
        5 to IFSlot(1670, 73),
        6 to IFSlot(1670, 86),
        7 to IFSlot(1670, 99),
        8 to IFSlot(1670, 112),
        9 to IFSlot(1670, 125),
        10 to IFSlot(1670, 138),
        11 to IFSlot(1670, 151),
        12 to IFSlot(1670, 164),
        13 to IFSlot(1670, 177),
        14 to IFSlot(1670, 190)
    ),
    3 to mapOf(
        1 to IFSlot(1671, 19),
        2 to IFSlot(1671, 32),
        3 to IFSlot(1671, 45),
        4 to IFSlot(1671, 58),
        5 to IFSlot(1671, 71),
        6 to IFSlot(1671, 84),
        7 to IFSlot(1671, 97),
        8 to IFSlot(1671, 110),
        9 to IFSlot(1671, 123),
        10 to IFSlot(1671, 136),
        11 to IFSlot(1671, 149),
        12 to IFSlot(1671, 162),
        13 to IFSlot(1671, 175),
        14 to IFSlot(1671, 188)
    ),
    4 to mapOf(
        1 to IFSlot(1672, 16),
        2 to IFSlot(1672, 29),
        3 to IFSlot(1672, 42),
        4 to IFSlot(1672, 55),
        5 to IFSlot(1672, 68),
        6 to IFSlot(1672, 81),
        7 to IFSlot(1672, 94),
        8 to IFSlot(1672, 107),
        9 to IFSlot(1672, 120),
        10 to IFSlot(1672, 133),
        11 to IFSlot(1672, 146),
        12 to IFSlot(1672, 159),
        13 to IFSlot(1672, 172),
        14 to IFSlot(1672, 185)
    ),
    5 to mapOf(
        1 to IFSlot(1673, 16),
        2 to IFSlot(1673, 29),
        3 to IFSlot(1673, 42),
        4 to IFSlot(1673, 55),
        5 to IFSlot(1673, 68),
        6 to IFSlot(1673, 81),
        7 to IFSlot(1673, 94),
        8 to IFSlot(1673, 107),
        9 to IFSlot(1673, 120),
        10 to IFSlot(1673, 133),
        11 to IFSlot(1673, 146),
        12 to IFSlot(1673, 159),
        13 to IFSlot(1673, 172),
        14 to IFSlot(1673, 185)
    )
)

data class ActionBarSlot(
    val groupId: Int,
    val slotId: Int,
    val itemId: Int
)

private val mappings = buildMap {
    //actionbar 1
    addBaseSlots(1, 1747, 1748, 823)
    put(Pair(1, 13), Triple(22337, 22338, 4429))
    put(Pair(1, 14), Triple(22339, 22340, 4430))

    //actionbar 2
    addBaseSlots(2, 1771, 1772, 835)
    put(Pair(2, 13), Triple(22341, 22342, 4431))
    put(Pair(2, 14), Triple(22343, 22344, 4432))

    //actionbar 3
    addBaseSlots(3, 1795, 1796, 847)
    put(Pair(3, 13), Triple(22345, 22346, 4433))
    put(Pair(3, 14), Triple(22347, 22348, 4434))

    //actionbar 4
    addBaseSlots(4, 1819, 1820, 859)
    put(Pair(4, 13), Triple(22349, 22350, 4435))
    put(Pair(4, 14), Triple(22351, 22352, 4436))

    //actionbar 5
    addBaseSlots(5, 1843, 1844, 871)
    put(Pair(5, 13), Triple(22353, 22354, 4437))
    put(Pair(5, 14), Triple(22355, 22356, 4438))

    //actionbar 6
    addBaseSlots(6, 27753, 27754, 5335)
    put(Pair(6, 1), Triple(22832, 27754, 5335)) //https://gitlab.com/project-undercut/engine/-/snippets/4786618 thanks for keeping consistent pattern :)

    //actionbar 7
    addBaseSlots(7, 27781, 27782, 5349) //finally. perfection

    //actionbar 8
    addBaseSlots(8, 27809, 27810, 5363)

    //actionbar 9
    addBaseSlots(9, 27837, 27838, 5377)

    //actionbar 10
    addBaseSlots(10, 27865, 27866, 5391)
    put(Pair(10, 10), Triple(27884, 27883, 5400))

    //actionbar 11
    addBaseSlots(11, 44638, 44639, 8801)
    put(Pair(11, 10), Triple(44657, 44656, 8810))

    //actionbar 12
    addBaseSlots(12, 44666, 44667, 8815)
    put(Pair(12, 10), Triple(44685, 44684, 8824))

    //actionbar 13
    addBaseSlots(13, 44694, 44695, 8829)
    put(Pair(13, 10), Triple(44713, 44712, 8838))

    //actionbar 14
    addBaseSlots(14, 49904, 49905, 10128)
    put(Pair(14, 10), Triple(49923, 49922, 10137))

    //actionbar 15
    addBaseSlots(15, 49932, 49933, 10142)
    put(Pair(15, 10), Triple(49951, 49950, 10151))

    //actionbar 16
    addBaseSlots(16, 53126, 53125, 11261)

    //actionbar 17
    addBaseSlots(17, 53154, 53153, 11275)

    //actionbar 18
    addBaseSlots(18, 53182, 53181, 11289)

    //unknown action bar (31)
    addBaseSlots(31, 1867, 1868, 883)
    put(Pair(31, 13), Triple(22357, 22358, 4439))
    put(Pair(31, 14), Triple(22359, 22360, 4440))
}

fun getSlotValues(actionbarId: Int, slotId: Int): ActionBarSlot {
    val (groupIdVb, slotIdVb, itemIdVar) = mappings[Pair(actionbarId, slotId)] ?: return ActionBarSlot(-1, -1, -1)
    return ActionBarSlot(
        groupId = varps.getVarBit(groupIdVb),
        slotId = varps.getVarBit(slotIdVb),
        itemId = varps.getVar(itemIdVar)
    )
}

private fun MutableMap<Pair<Int, Int>, Triple<Int, Int, Int>>.addBaseSlots(actionbar: Int, firstId: Int, secondId: Int, thirdId: Int) {
    (0..13).forEach { slot ->
        put(actionbar to (slot + 1), Triple(firstId + (slot * 2), secondId + (slot * 2), thirdId + slot))
    }
}