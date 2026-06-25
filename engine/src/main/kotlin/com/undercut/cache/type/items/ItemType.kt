package com.undercut.cache.type.items

import com.undercut.cache.Cache
import com.undercut.cache.type.enums.EnumType
import com.undercut.cache.type.params.Params
import com.undercut.cache.type.structs.StructType
import com.undercut.game.items.Item

class ItemType {
    var id: Int = 0
    var modelId: Int = 0
    var name: String = ""
    var examine: String? = null
    var modelZoom: Int = 2000
    var modelRotationX: Int = 0
    var modelRotationY: Int = 0
    var modelOffsetX: Int = 0
    var modelOffsetY: Int = 0
    var stackable: Int = 0
    var price: Long = 1
    var equipmentSlot: Byte = -1
    var equipmentType: Byte = -1
    var members: Boolean = false
    var maleModel1: Int = -1
    var maleModel2: Int = -1
    var femaleModel1: Int = -1
    var femaleModel2: Int = -1
    var equipmentType2: Byte = -1
    var wornActions: Array<String?> = arrayOfNulls(8)
    var groundActions: Array<String?> = arrayOfNulls(5)
    var inventoryActions: Array<String?> = arrayOfNulls(5)
    var originalColors: ShortArray? = null
    var replacementColors: ShortArray? = null
    var originalTextures: ShortArray? = null
    var replacementTextures: ShortArray? = null
    var recolorPalette: ByteArray? = null
    var notedId: Int = -1
    var stockMarket: Boolean = false
    var maleModel3: Int = -1
    var femaleModel3: Int = -1
    var maleModelTranslateY: Int = -1
    var femaleModelTranslateY: Int = -1
    var maleHeadModel: Int = -1
    var femaleHeadModel: Int = -1
    var modelAngleZ: Int = 0
    var equipCategory: Int = 0
    var category: Int = -1
    var notedItemId: Int = -1
    var notedTemplate: Int = -1
    var stackAmounts: IntArray? = null
    var stackIds: IntArray? = null
    var resizeX: Float = 0f
    var resizeY: Float = 0f
    var resizeZ: Float = 0f
    var hasResize: Boolean = false
    var ambient: Byte = 0
    var contrast: Int = 0
    var teamId: Byte = 0
    var lentId: Int = -1
    var lentItemId: Int = -1
    var lendTemplate: Int = -1
    var maleModelOffsetX: Int = 0
    var maleModelOffsetY: Int = 0
    var maleModelOffsetZ: Int = 0
    var femaleModelOffsetX: Int = 0
    var femaleModelOffsetY: Int = 0
    var femaleModelOffsetZ: Int = 0
    var questIds: IntArray? = null
    var pickSizeShift: Int = 0
    var bindId: Int = -1
    var boundTemplate: Int = -1
    var headModels: IntArray? = null
    var groundCursors: IntArray? = null
    var tradeable: Boolean = true
    var searchable: Boolean = false
    var shardItemId: Int = -1
    var shardTemplateId: Int = -1
    var params: Params = Params()
    var buffEffect: String? = null
    var geBuyLimit: Int = 0
    var shardCombineAmount: Int = 0
    var shardName: String? = null
    var noted: Boolean = false
    var lended: Boolean = false

    val isStackable: Boolean get() = stackable > 0

    init {
        groundActions = arrayOfNulls<String>(5).apply { this[2] = "Take" }
        inventoryActions = arrayOfNulls<String>(5).apply { this[4] = "Drop" }
    }

    fun toNote() {
        val realItem = get(notedItemId)
        members = realItem.members
        price = realItem.price
        name = realItem.name
        stackable = 1
        noted = true
    }

    fun toBind() {
        val realItem = get(bindId)
        originalColors = realItem.originalColors
        maleModel3 = realItem.maleModel3
        femaleModel3 = realItem.femaleModel3
        teamId = realItem.teamId
        price = 0
        members = realItem.members
        name = realItem.name
        inventoryActions = arrayOfNulls(5)
        groundActions = realItem.groundActions
        realItem.inventoryActions.let {
            for (optionIndex in 0 until 4) {
                inventoryActions[optionIndex] = it[optionIndex]
            }
        }
        inventoryActions[4] = "Discard"
        maleModel1 = realItem.maleModel1
        maleModel2 = realItem.maleModel2
        femaleModel1 = realItem.femaleModel1
        femaleModel2 = realItem.femaleModel2
        params = realItem.params
        equipmentSlot = realItem.equipmentSlot
        equipmentType = realItem.equipmentType
    }

    fun toLend() {
        val realItem = get(lentItemId)
        originalColors = realItem.originalColors
        maleModel3 = realItem.maleModel3
        femaleModel3 = realItem.femaleModel3
        teamId = realItem.teamId
        price = 0
        members = realItem.members
        name = realItem.name
        inventoryActions = arrayOfNulls(5)
        groundActions = realItem.groundActions
        realItem.inventoryActions.let {
            for (optionIndex in 0 until 4) {
                inventoryActions[optionIndex] = it[optionIndex]
            }
        }
        inventoryActions[4] = "Discard"
        maleModel1 = realItem.maleModel1
        maleModel2 = realItem.maleModel2
        femaleModel1 = realItem.femaleModel1
        femaleModel2 = realItem.femaleModel2
        params = realItem.params
        equipmentSlot = realItem.equipmentSlot
        equipmentType = realItem.equipmentType
        lended = true
    }

    fun getInvOpIdForName(opName: String): Int {
        inventoryActions.forEachIndexed { index, _ ->
            if (containsInvOp(index, opName)) return index
        }
        return -1
    }

    fun getInvOp(optionId: Int): String {
        return when (id) {
            6099, 6100, 6101, 6102 -> if (optionId == 2) "Temple" else null
            19760, 13561, 13562 -> when (optionId) {
                0 -> inventoryActions[1]
                1 -> inventoryActions[0]
                else -> null
            }

            else -> null
        } ?: inventoryActions.getOrNull(optionId) ?: "null"
    }

    fun containsInvOp(i: Int, option: String): Boolean {
        return inventoryActions.getOrNull(i)?.equals(option, ignoreCase = true) == true
    }

    fun containsInvOp(option: String): Boolean {
        return inventoryActions.any { it.equals(option, ignoreCase = true) }
    }

    fun getEquipOpIdForName(opName: String): Int {
        wornActions.forEachIndexed { index, _ ->
            if (containsEquipOp(index, opName)) return index
        }
        return -1
    }

    fun loadEquippedOps() {
        wornActions[0] = params.getString(528)
        wornActions[1] = params.getString(529)
        wornActions[2] = params.getString(530)
        wornActions[3] = params.getString(531)
        wornActions[4] = params.getString(1211)
        wornActions[5] = params.getString(6712)
        wornActions[6] = params.getString(6713)
        wornActions[7] = params.getString(6714)
    }

    fun getEquipOp(optionId: Int): String {
        return wornActions.getOrNull(optionId) ?: "null"
    }

    fun containsEquipOp(option: String): Boolean {
        return wornActions.any { it.equals(option, ignoreCase = true) }
    }

    fun containsEquipOp(optionId: Int, option: String): Boolean {
        return wornActions.getOrNull(optionId)?.equals(option, ignoreCase = true) == true
    }

    fun getGroundOp(optionId: Int): String {
        return groundActions.getOrNull(optionId) ?: "null"
    }

    fun containsGroundOp(option: String): Boolean {
        return groundActions.any { it.equals(option, ignoreCase = true) }
    }

    fun containsGroundOp(optionId: Int, option: String): Boolean {
        return groundActions.getOrNull(optionId)?.equals(option, ignoreCase = true) == true
    }

    fun getGroundOpIdForName(opName: String): Int {
        groundActions.forEachIndexed { index, _ ->
            if (containsGroundOp(index, opName)) return index
        }
        return -1
    }

    fun getCreationSkillId(): Int {
        return EnumType.get(681)?.values[getCraftingType()] as Int
    }

    fun getCraftingType(): Int {
        return params.getInt(2696)
    }

    fun getCreationLevelReq(): Int {
        return params.getInt(2645)
    }

    fun getToolBeltReqItem(): Int {
        return params.getInt(2650, -1)
    }

    fun getMaterials(): List<Item> {
        val mats = mutableListOf<Item>()
        try {
            for (i in 0 until 6) {
                var item: Item? = null
                if (params.getInt(2655 + i, -1) != -1) {
                    item = Item(params.getInt(2655 + i))
                }
                if (params.getInt(2665 + i, -1) != -1 && item != null) {
                    item.amount = params.getInt(2665 + i, -1)
                } else if (params.getInt(2665 + i, -1) != -1 && item == null) {
                    item = Item(
                        EnumType.get(params.getInt(2675 + i, 0))?.values[2655 + i] as Int,
                        params.getInt(2665 + i, -1)
                    )
                }
                item?.let { mats.add(it) }
            }
        } catch (_: Throwable) {
        }
        return mats
    }

    fun getCreationAmount(): Int {
        return params.getInt(2653, 1)
    }

    fun getCreationExperience(): Double {
        return params.getInt(2697) / 10.0
    }

    fun getCombatMap(): StructType? {
        val structId = params.getInt(686, 0)
        return if (structId != 0) StructType.get(structId) else null
    }

    fun getCombatOpcode(opcode: Int): Int {
        return params.getInt(opcode, -1).takeIf { it != -1 } ?: getCombatMap()?.params?.getInt(opcode) ?: -1
    }

    enum class CombatStyle(val id: Int) {
        MAGIC_AIR(1),
        MAGIC_WATER(2),
        MAGIC_EARTH(3),
        MAGIC_FIRE(4),
        MELEE_STAB(5),
        MELEE_SLASH(6),
        MELEE_CRUSH(7),
        RANGE_BOW(8),
        RANGE_CROSSBOW(9),
        RANGE_THROWN(10);

        companion object {
            private val MAP: MutableMap<Int, CombatStyle> = HashMap()

            init {
                for (c in entries) {
                    MAP[c.id] = c
                }
            }

            fun forId(id: Int): CombatStyle? {
                return MAP[id]
            }
        }
    }

    fun getCombatStyle(): CombatStyle? {
        return CombatStyle.forId(getCombatOpcode(2853))
    }

    companion object {
        private val PARSER = ItemTypeParser()

        fun getParser(): ItemTypeParser {
            return PARSER
        }

        fun get(id: Int): ItemType {
            return PARSER.get(Cache.get(), id)
        }
    }
}
