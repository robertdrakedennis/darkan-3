package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Definition
import world.gregs.voidps.cache.definition.ColourPalette
import world.gregs.voidps.cache.definition.Extra
import world.gregs.voidps.cache.definition.Parameterized
import world.gregs.voidps.cache.definition.Recolourable

data class ItemDefinition(
    override var id: Int = -1,
    var modelId: Int = 0,
    var name: String = "null",
    var spriteScale: Int = 2000,
    var spritePitch: Int = 0,
    var spriteCameraRoll: Int = 0,
    var spriteTranslateX: Int = 0,
    var spriteTranslateY: Int = 0,
    var stackable: Int = 0,
    var cost: Long = 1,
    var members: Boolean = false,
    var wearPos: Int = -1,       // Primary equipment slot (-1 = not equippable)
    var wearPos2: Int = -1,      // Secondary equipment slot
    var wearPos3: Int = -1,      // Tertiary equipment slot
    var multiStackSize: Int = -1,
    var primaryMaleModel: Int = -1,
    var secondaryMaleModel: Int = -1,
    var primaryFemaleModel: Int = -1,
    var secondaryFemaleModel: Int = -1,
    var floorOptions: Array<String?> = arrayOf(null, null, "Take", null, null, "Examine"),
    var options: Array<String?> = arrayOf(null, null, null, null, "Drop"),
    override var originalColours: ShortArray? = null,
    override var modifiedColours: ShortArray? = null,
    override var originalTextureColours: ShortArray? = null,
    override var modifiedTextureColours: ShortArray? = null,
    override var recolourPalette: ByteArray? = null,
    var exchangeable: Boolean = false,
    var tertiaryMaleModel: Int = -1,
    var tertiaryFemaleModel: Int = -1,
    var primaryMaleDialogueHead: Int = -1,
    var primaryFemaleDialogueHead: Int = -1,
    var secondaryMaleDialogueHead: Int = -1,
    var secondaryFemaleDialogueHead: Int = -1,
    var spriteCameraYaw: Int = 0,
    var dummyItem: Int = 0,
    var noteId: Int = -1,
    var notedTemplateId: Int = -1,
    var stackIds: IntArray? = null,
    var stackAmounts: IntArray? = null,
    var floorScaleX: Int = 128,
    var floorScaleZ: Int = 128,
    var floorScaleY: Int = 128,
    var ambience: Int = 0,
    var diffusion: Int = 0,
    var team: Int = 0,
    var lendId: Int = -1,
    var lendTemplateId: Int = -1,
    var maleWieldX: Int = 0,
    var maleWieldZ: Int = 0,
    var maleWieldY: Int = 0,
    var femaleWieldX: Int = 0,
    var femaleWieldZ: Int = 0,
    var femaleWieldY: Int = 0,
    var primaryCursorOpcode: Int = -1,
    var primaryCursor: Int = -1,
    var secondaryCursorOpcode: Int = -1,
    var secondaryCursor: Int = -1,
    var primaryInterfaceCursorOpcode: Int = -1,
    var primaryInterfaceCursor: Int = -1,
    var secondaryInterfaceCursorOpcode: Int = -1,
    var secondaryInterfaceCursor: Int = -1,
    var campaigns: IntArray? = null,
    var pickSizeShift: Int = 0,
    var singleNoteId: Int = -1,
    var singleNoteTemplateId: Int = -1,
    var category: Int = -1,
    var geBuyLimit: Int = 0,
    var tradeable: Boolean = false,
    var searchable: Boolean = false,
    var shardItemId: Int = -1,
    var shardTemplateId: Int = -1,
    var shardCombineAmount: Int = 0,
    var shardName: String? = null,
    var bindId: Int = -1,
    var boundTemplateId: Int = -1,
    var headModels: IntArray? = null,
    var groundCursors: IntArray? = null,
    var examine: String? = null,
    var notedId: Int = -1,
    var wornActions: Array<String?> = arrayOfNulls(8),
    override var params: Map<Int, Any>? = null,
    override var stringId: String = "",
    override var extras: Map<String, Any>? = null
) : Definition, Recolourable, ColourPalette, Parameterized, Extra {

    val noted: Boolean
        get() = notedTemplateId != -1

    val lent: Boolean
        get() = lendTemplateId != -1

    val singleNote: Boolean
        get() = boundTemplateId != -1

    // --- Engine (ItemType) public-surface aliases ---

    var price: Long
        get() = cost
        set(value) { cost = value }

    var equipmentSlot: Int
        get() = wearPos
        set(value) { wearPos = value }

    var equipmentType: Int
        get() = wearPos2
        set(value) { wearPos2 = value }

    var equipmentType2: Int
        get() = wearPos3
        set(value) { wearPos3 = value }

    var maleModel1: Int
        get() = primaryMaleModel
        set(value) { primaryMaleModel = value }

    var maleModel2: Int
        get() = secondaryMaleModel
        set(value) { secondaryMaleModel = value }

    var maleModel3: Int
        get() = tertiaryMaleModel
        set(value) { tertiaryMaleModel = value }

    var femaleModel1: Int
        get() = primaryFemaleModel
        set(value) { primaryFemaleModel = value }

    var femaleModel2: Int
        get() = secondaryFemaleModel
        set(value) { secondaryFemaleModel = value }

    var femaleModel3: Int
        get() = tertiaryFemaleModel
        set(value) { tertiaryFemaleModel = value }

    /** Engine alias for [options] (inventory/right-click ops). */
    val inventoryActions: Array<String?>
        get() = options

    /** Engine alias for [floorOptions] (ground item ops). */
    val groundActions: Array<String?>
        get() = floorOptions

    /** Engine alias for [noteId] (the real item a note points to). */
    val notedItemId: Int
        get() = noteId

    /** Engine alias for [notedTemplateId]. */
    val notedTemplate: Int
        get() = notedTemplateId

    // Internal shims kept for the helpers below; delegate to the public
    // [Parameterized.getParamInt]/[getParamString] pair. paramStringOrNull keeps
    // the legacy "null" placeholder default (wornActions etc. expect it).
    private fun paramInt(key: Int, default: Int = 0): Int = getParamInt(key, default)

    private fun paramStringOrNull(key: Int): String = getParamString(key) ?: "null"

    // --- Re-homed pure ItemType helpers ---

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

    fun containsInvOp(i: Int, option: String): Boolean =
        inventoryActions.getOrNull(i)?.equals(option, ignoreCase = true) == true

    fun containsInvOp(option: String): Boolean =
        inventoryActions.any { it.equals(option, ignoreCase = true) }

    fun getEquipOpIdForName(opName: String): Int {
        wornActions.forEachIndexed { index, _ ->
            if (containsEquipOp(index, opName)) return index
        }
        return -1
    }

    fun getEquipOp(optionId: Int): String = wornActions.getOrNull(optionId) ?: "null"

    fun containsEquipOp(option: String): Boolean =
        wornActions.any { it.equals(option, ignoreCase = true) }

    fun containsEquipOp(optionId: Int, option: String): Boolean =
        wornActions.getOrNull(optionId)?.equals(option, ignoreCase = true) == true

    fun getGroundOp(optionId: Int): String = groundActions.getOrNull(optionId) ?: "null"

    fun containsGroundOp(option: String): Boolean =
        groundActions.any { it.equals(option, ignoreCase = true) }

    fun containsGroundOp(optionId: Int, option: String): Boolean =
        groundActions.getOrNull(optionId)?.equals(option, ignoreCase = true) == true

    fun getGroundOpIdForName(opName: String): Int {
        groundActions.forEachIndexed { index, _ ->
            if (containsGroundOp(index, opName)) return index
        }
        return -1
    }

    /** Populates [wornActions] from equipment-op params (528-531, 1211, 6712-6714). */
    fun loadEquippedOps() {
        wornActions[0] = paramStringOrNull(528)
        wornActions[1] = paramStringOrNull(529)
        wornActions[2] = paramStringOrNull(530)
        wornActions[3] = paramStringOrNull(531)
        wornActions[4] = paramStringOrNull(1211)
        wornActions[5] = paramStringOrNull(6712)
        wornActions[6] = paramStringOrNull(6713)
        wornActions[7] = paramStringOrNull(6714)
    }

    fun getCraftingType(): Int = paramInt(2696)

    fun getCreationLevelReq(): Int = paramInt(2645)

    fun getCreationSkillId(): Int = (Cache.enum(681)?.getValue(getCraftingType()) as? Int) ?: -1

    fun getCreationAmount(): Int = paramInt(2653, 1)

    fun getCreationExperience(): Double = paramInt(2697) / 10.0

    fun getCombatMap(): StructDefinition? {
        val structId = paramInt(686, 0)
        return if (structId != 0) Cache.struct(structId) else null
    }

    fun getCombatOpcode(opcode: Int): Int {
        val direct = paramInt(opcode, -1)
        if (direct != -1) return direct
        return (getCombatMap()?.params?.get(opcode) as? Int) ?: -1
    }

    fun getCombatStyle(): CombatStyle? = CombatStyle.forId(getCombatOpcode(2853))

    fun getToolBeltReqItem(): Int = paramInt(2650, -1)

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
            private val MAP = entries.associateBy(CombatStyle::id)

            fun forId(id: Int): CombatStyle? = MAP[id]
        }
    }

    // --- Java interop convenience methods ---

    /** Legacy alias for [cost]. */
    fun getValue() = cost.toInt()

    /** Whether this item is stackable (stackable != 0). */
    fun isStackable() = stackable != 0

    /** Whether this item is lent (lendTemplateId != -1). */
    fun isLended() = lent

    /** Whether this item is noted (notedTemplateId != -1). */
    fun isNoted() = noted

    /** Legacy alias for [noteId]. */
    fun getCertId() = noteId

    /** Whether this item can be exchanged on the GE. */
    fun canExchange() = exchangeable

    /** Legacy alias for [wearPos]. */
    fun getEquipSlot() = wearPos

    /** Checks equipment type via params (param 528). */
    fun isEquipType(type: Int): Boolean {
        val equipType = params?.get(528) as? Int ?: return false
        return equipType == type
    }

    /** Checks if the item's options array contains the given option string (case-insensitive). */
    fun containsOption(option: String): Boolean {
        return options.filterNotNull().any { it.equals(option, ignoreCase = true) }
    }

    /** Whether this item can be worn by the given gender. */
    fun isWearItem(isMale: Boolean): Boolean {
        if (wearPos == -1) return false
        return if (isMale) primaryMaleModel != -1 else primaryFemaleModel != -1
    }

    /** Whether this item is a destroy item (cannot be dropped, param 1134 == 1). */
    fun isDestroyItem(): Boolean = (params?.get(1134) as? Int ?: 0) == 1

    /** Gets the render animation ID from params (param 644). */
    fun getRenderAnimId(): Int = params?.get(644) as? Int ?: -1

    /** Gets equipment bonuses array from item params. */
    fun getBonuses(): IntArray {
        val p = params ?: return IntArray(17)
        return intArrayOf(
            p[0] as? Int ?: 0, p[1] as? Int ?: 0, p[2] as? Int ?: 0, p[3] as? Int ?: 0, p[4] as? Int ?: 0,
            p[5] as? Int ?: 0, p[6] as? Int ?: 0, p[7] as? Int ?: 0, p[8] as? Int ?: 0, p[9] as? Int ?: 0,
            p[10] as? Int ?: 0, p[11] as? Int ?: 0, p[14] as? Int ?: 0, p[12] as? Int ?: 0,
            p[956] as? Int ?: 0, p[957] as? Int ?: 0, p[958] as? Int ?: 0
        )
    }

    /** Gets the equipment option at the given index (0-based). */
    fun getEquipmentOption(index: Int): String? {
        if (index < 0 || index >= options.size) return null
        return options[index]
    }

    /** Gets the inventory option at the given index (0-based). */
    fun getInventoryOption(index: Int): String? {
        if (index < 0 || index >= options.size) return null
        return options[index]
    }

    /** Gets the dungeoneering shop value multiplier from params (param 1149, default 100 = 1.0x). */
    fun getDungShopValueMultiplier(): Double {
        val raw = params?.get(1149) as? Int ?: 100
        return raw / 100.0
    }

    /** Gets original model colours as an int array for legacy Java compat. */
    fun getOriginalModelColors(): IntArray = originalColours?.map { it.toInt() }?.toIntArray() ?: IntArray(0)

    /** Gets the primary male worn model ID. */
    fun getMaleWornModelId1(): Int = primaryMaleModel

    /** Gets the primary female worn model ID. */
    fun getFemaleWornModelId1(): Int = primaryFemaleModel

    /** Gets the general store sell price (cost * 0.3, minimum 1). */
    fun getSellPrice(): Int = (cost.toInt() * 0.3).toInt().coerceAtLeast(1)

    /** Gets the high alchemy price (cost * 0.6, minimum 1). */
    fun getHighAlchPrice(): Int = (cost.toInt() * 0.6).toInt().coerceAtLeast(1)

    /** Whether the item has a face/head covering mask (param 625). */
    fun faceMask(): Boolean = (params?.get(625) as? Int ?: 0) != 0

    /** Gets wearing skill requirements from params. */
    fun getWearingSkillRequiriments(): Map<Int, Int> {
        val reqs = mutableMapOf<Int, Int>()
        val p = params ?: return reqs
        val pairs = listOf(749 to 750, 751 to 752, 753 to 754)
        for ((skillParam, levelParam) in pairs) {
            val skill = p[skillParam] as? Int ?: continue
            val level = p[levelParam] as? Int ?: continue
            if (skill > 0) reqs[skill] = level
        }
        return reqs
    }

    /**
     * Gets the stage on death for this item.
     * 1 = always kept (untradeable), -1 = always lost, 0 = normal.
     */
    fun getStageOnDeath(): Int {
        // If it's a destroy item (untradeable), it's always kept
        if (isDestroyItem()) return 1
        return 0
    }

    /** Whether this item is bound (dungeoneering). */
    fun isBinded(): Boolean = (params?.get(1135) as? Int ?: 0) == 1

    /** Gets the quest requirements for wearing this item from params. */
    fun getWieldQuestReq(): Map<Int, Int> {
        val reqs = mutableMapOf<Int, Int>()
        val p = params ?: return reqs
        val questParam = p[1053] as? Int
        if (questParam != null && questParam > 0) reqs[questParam] = 1
        return reqs
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemDefinition

        if (id != other.id) return false
        if (modelId != other.modelId) return false
        if (name != other.name) return false
        if (spriteScale != other.spriteScale) return false
        if (spritePitch != other.spritePitch) return false
        if (spriteCameraRoll != other.spriteCameraRoll) return false
        if (spriteTranslateX != other.spriteTranslateX) return false
        if (spriteTranslateY != other.spriteTranslateY) return false
        if (stackable != other.stackable) return false
        if (cost != other.cost) return false
        if (members != other.members) return false
        if (wearPos != other.wearPos) return false
        if (wearPos2 != other.wearPos2) return false
        if (wearPos3 != other.wearPos3) return false
        if (multiStackSize != other.multiStackSize) return false
        if (primaryMaleModel != other.primaryMaleModel) return false
        if (secondaryMaleModel != other.secondaryMaleModel) return false
        if (primaryFemaleModel != other.primaryFemaleModel) return false
        if (secondaryFemaleModel != other.secondaryFemaleModel) return false
        if (!floorOptions.contentEquals(other.floorOptions)) return false
        if (!options.contentEquals(other.options)) return false
        if (originalColours != null) {
            if (other.originalColours == null) return false
            if (!originalColours.contentEquals(other.originalColours)) return false
        } else if (other.originalColours != null) return false
        if (modifiedColours != null) {
            if (other.modifiedColours == null) return false
            if (!modifiedColours.contentEquals(other.modifiedColours)) return false
        } else if (other.modifiedColours != null) return false
        if (originalTextureColours != null) {
            if (other.originalTextureColours == null) return false
            if (!originalTextureColours.contentEquals(other.originalTextureColours)) return false
        } else if (other.originalTextureColours != null) return false
        if (modifiedTextureColours != null) {
            if (other.modifiedTextureColours == null) return false
            if (!modifiedTextureColours.contentEquals(other.modifiedTextureColours)) return false
        } else if (other.modifiedTextureColours != null) return false
        if (recolourPalette != null) {
            if (other.recolourPalette == null) return false
            if (!recolourPalette.contentEquals(other.recolourPalette)) return false
        } else if (other.recolourPalette != null) return false
        if (exchangeable != other.exchangeable) return false
        if (tertiaryMaleModel != other.tertiaryMaleModel) return false
        if (tertiaryFemaleModel != other.tertiaryFemaleModel) return false
        if (primaryMaleDialogueHead != other.primaryMaleDialogueHead) return false
        if (primaryFemaleDialogueHead != other.primaryFemaleDialogueHead) return false
        if (secondaryMaleDialogueHead != other.secondaryMaleDialogueHead) return false
        if (secondaryFemaleDialogueHead != other.secondaryFemaleDialogueHead) return false
        if (spriteCameraYaw != other.spriteCameraYaw) return false
        if (dummyItem != other.dummyItem) return false
        if (noteId != other.noteId) return false
        if (notedTemplateId != other.notedTemplateId) return false
        if (stackIds != null) {
            if (other.stackIds == null) return false
            if (!stackIds.contentEquals(other.stackIds)) return false
        } else if (other.stackIds != null) return false
        if (stackAmounts != null) {
            if (other.stackAmounts == null) return false
            if (!stackAmounts.contentEquals(other.stackAmounts)) return false
        } else if (other.stackAmounts != null) return false
        if (floorScaleX != other.floorScaleX) return false
        if (floorScaleZ != other.floorScaleZ) return false
        if (floorScaleY != other.floorScaleY) return false
        if (ambience != other.ambience) return false
        if (diffusion != other.diffusion) return false
        if (team != other.team) return false
        if (lendId != other.lendId) return false
        if (lendTemplateId != other.lendTemplateId) return false
        if (maleWieldX != other.maleWieldX) return false
        if (maleWieldZ != other.maleWieldZ) return false
        if (maleWieldY != other.maleWieldY) return false
        if (femaleWieldX != other.femaleWieldX) return false
        if (femaleWieldZ != other.femaleWieldZ) return false
        if (femaleWieldY != other.femaleWieldY) return false
        if (primaryCursorOpcode != other.primaryCursorOpcode) return false
        if (primaryCursor != other.primaryCursor) return false
        if (secondaryCursorOpcode != other.secondaryCursorOpcode) return false
        if (secondaryCursor != other.secondaryCursor) return false
        if (primaryInterfaceCursorOpcode != other.primaryInterfaceCursorOpcode) return false
        if (primaryInterfaceCursor != other.primaryInterfaceCursor) return false
        if (secondaryInterfaceCursorOpcode != other.secondaryInterfaceCursorOpcode) return false
        if (secondaryInterfaceCursor != other.secondaryInterfaceCursor) return false
        if (campaigns != null) {
            if (other.campaigns == null) return false
            if (!campaigns.contentEquals(other.campaigns)) return false
        } else if (other.campaigns != null) return false
        if (pickSizeShift != other.pickSizeShift) return false
        if (singleNoteId != other.singleNoteId) return false
        if (singleNoteTemplateId != other.singleNoteTemplateId) return false
        if (category != other.category) return false
        if (geBuyLimit != other.geBuyLimit) return false
        if (tradeable != other.tradeable) return false
        if (searchable != other.searchable) return false
        if (shardItemId != other.shardItemId) return false
        if (shardTemplateId != other.shardTemplateId) return false
        if (shardCombineAmount != other.shardCombineAmount) return false
        if (shardName != other.shardName) return false
        if (bindId != other.bindId) return false
        if (boundTemplateId != other.boundTemplateId) return false
        if (headModels != null) {
            if (other.headModels == null) return false
            if (!headModels.contentEquals(other.headModels)) return false
        } else if (other.headModels != null) return false
        if (groundCursors != null) {
            if (other.groundCursors == null) return false
            if (!groundCursors.contentEquals(other.groundCursors)) return false
        } else if (other.groundCursors != null) return false
        if (params != other.params) return false
        if (stringId != other.stringId) return false
        if (extras != other.extras) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + modelId
        result = 31 * result + name.hashCode()
        result = 31 * result + spriteScale
        result = 31 * result + spritePitch
        result = 31 * result + spriteCameraRoll
        result = 31 * result + spriteTranslateX
        result = 31 * result + spriteTranslateY
        result = 31 * result + stackable
        result = 31 * result + cost.hashCode()
        result = 31 * result + members.hashCode()
        result = 31 * result + wearPos
        result = 31 * result + wearPos2
        result = 31 * result + wearPos3
        result = 31 * result + multiStackSize
        result = 31 * result + primaryMaleModel
        result = 31 * result + secondaryMaleModel
        result = 31 * result + primaryFemaleModel
        result = 31 * result + secondaryFemaleModel
        result = 31 * result + floorOptions.contentHashCode()
        result = 31 * result + options.contentHashCode()
        result = 31 * result + (originalColours?.contentHashCode() ?: 0)
        result = 31 * result + (modifiedColours?.contentHashCode() ?: 0)
        result = 31 * result + (originalTextureColours?.contentHashCode() ?: 0)
        result = 31 * result + (modifiedTextureColours?.contentHashCode() ?: 0)
        result = 31 * result + (recolourPalette?.contentHashCode() ?: 0)
        result = 31 * result + exchangeable.hashCode()
        result = 31 * result + tertiaryMaleModel
        result = 31 * result + tertiaryFemaleModel
        result = 31 * result + primaryMaleDialogueHead
        result = 31 * result + primaryFemaleDialogueHead
        result = 31 * result + secondaryMaleDialogueHead
        result = 31 * result + secondaryFemaleDialogueHead
        result = 31 * result + spriteCameraYaw
        result = 31 * result + dummyItem
        result = 31 * result + noteId
        result = 31 * result + notedTemplateId
        result = 31 * result + (stackIds?.contentHashCode() ?: 0)
        result = 31 * result + (stackAmounts?.contentHashCode() ?: 0)
        result = 31 * result + floorScaleX
        result = 31 * result + floorScaleZ
        result = 31 * result + floorScaleY
        result = 31 * result + ambience
        result = 31 * result + diffusion
        result = 31 * result + team
        result = 31 * result + lendId
        result = 31 * result + lendTemplateId
        result = 31 * result + maleWieldX
        result = 31 * result + maleWieldZ
        result = 31 * result + maleWieldY
        result = 31 * result + femaleWieldX
        result = 31 * result + femaleWieldZ
        result = 31 * result + femaleWieldY
        result = 31 * result + primaryCursorOpcode
        result = 31 * result + primaryCursor
        result = 31 * result + secondaryCursorOpcode
        result = 31 * result + secondaryCursor
        result = 31 * result + primaryInterfaceCursorOpcode
        result = 31 * result + primaryInterfaceCursor
        result = 31 * result + secondaryInterfaceCursorOpcode
        result = 31 * result + secondaryInterfaceCursor
        result = 31 * result + (campaigns?.contentHashCode() ?: 0)
        result = 31 * result + pickSizeShift
        result = 31 * result + singleNoteId
        result = 31 * result + singleNoteTemplateId
        result = 31 * result + category
        result = 31 * result + geBuyLimit
        result = 31 * result + tradeable.hashCode()
        result = 31 * result + searchable.hashCode()
        result = 31 * result + shardItemId
        result = 31 * result + shardTemplateId
        result = 31 * result + shardCombineAmount
        result = 31 * result + (shardName?.hashCode() ?: 0)
        result = 31 * result + bindId
        result = 31 * result + boundTemplateId
        result = 31 * result + (headModels?.contentHashCode() ?: 0)
        result = 31 * result + (groundCursors?.contentHashCode() ?: 0)
        result = 31 * result + (params?.hashCode() ?: 0)
        result = 31 * result + stringId.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

    fun toLend(item: ItemDefinition?, template: ItemDefinition?) {
        if (item == null || template == null) {
            return
        }
        modifiedColours = item.modifiedColours
        primaryMaleDialogueHead = item.primaryMaleDialogueHead
        secondaryMaleDialogueHead = item.secondaryMaleDialogueHead
        tertiaryMaleModel = item.tertiaryMaleModel
        team = item.team
        params = item.params
        members = item.members
        wearPos = item.wearPos
        wearPos2 = item.wearPos2
        wearPos3 = item.wearPos3
        modifiedTextureColours = item.modifiedTextureColours
        maleWieldZ = item.maleWieldZ
        secondaryFemaleModel = item.secondaryFemaleModel
        spriteCameraYaw = template.spriteCameraYaw
        floorOptions = item.floorOptions
        secondaryFemaleDialogueHead = item.secondaryFemaleDialogueHead
        recolourPalette = item.recolourPalette
        femaleWieldZ = item.femaleWieldZ
        spritePitch = template.spritePitch
        primaryFemaleModel = item.primaryFemaleModel
        modelId = template.modelId
        options = arrayOfNulls(5)
        spriteCameraRoll = template.spriteCameraRoll
        spriteTranslateY = template.spriteTranslateY
        originalTextureColours = item.originalTextureColours
        femaleWieldX = item.femaleWieldX
        secondaryMaleModel = item.secondaryMaleModel
        cost = 0
        maleWieldY = item.maleWieldY
        originalColours = item.originalColours
        spriteTranslateX = template.spriteTranslateX
        femaleWieldY = item.femaleWieldY
        primaryFemaleDialogueHead = item.primaryFemaleDialogueHead
        spriteScale = template.spriteScale
        name = item.name
        tertiaryFemaleModel = item.tertiaryFemaleModel
        primaryMaleModel = item.primaryMaleModel
        maleWieldX = item.maleWieldX
        System.arraycopy(item.options, 0, options, 0, 4)
        options[4] = "Discard"
    }

    fun toNote(template: ItemDefinition?, item: ItemDefinition?) {
        if (item == null || template == null) {
            return
        }
        spriteTranslateY = template.spriteTranslateY
        originalColours = template.originalColours
        cost = item.cost
        name = item.name
        modifiedTextureColours = template.modifiedTextureColours
        spriteCameraRoll = template.spriteCameraRoll
        spriteCameraYaw = template.spriteCameraYaw
        originalTextureColours = template.originalTextureColours
        modelId = template.modelId
        spriteScale = template.spriteScale
        recolourPalette = template.recolourPalette
        stackable = 1
        spritePitch = template.spritePitch
        spriteTranslateX = template.spriteTranslateX
        members = item.members
        modifiedColours = template.modifiedColours
    }

    fun toSingleNote(template: ItemDefinition?, item: ItemDefinition?) {
        if (item == null || template == null) {
            return
        }
        cost = 0
        tertiaryMaleModel = item.tertiaryMaleModel
        stackable = item.stackable
        members = item.members
        wearPos = item.wearPos
        wearPos2 = item.wearPos2
        wearPos3 = item.wearPos3
        recolourPalette = item.recolourPalette
        spriteTranslateY = template.spriteTranslateY
        team = item.team
        secondaryMaleModel = item.secondaryMaleModel
        options = arrayOfNulls(5)
        floorOptions = item.floorOptions
        maleWieldZ = item.maleWieldZ
        primaryMaleDialogueHead = item.primaryMaleDialogueHead
        femaleWieldZ = item.femaleWieldZ
        name = item.name
        spriteScale = template.spriteScale
        originalColours = item.originalColours
        secondaryFemaleDialogueHead = item.secondaryFemaleDialogueHead
        params = item.params
        primaryFemaleModel = item.primaryFemaleModel
        spritePitch = template.spritePitch
        spriteCameraRoll = template.spriteCameraRoll
        femaleWieldX = item.femaleWieldX
        secondaryMaleDialogueHead = item.secondaryMaleDialogueHead
        tertiaryFemaleModel = item.tertiaryFemaleModel
        modifiedTextureColours = item.modifiedTextureColours
        maleWieldX = item.maleWieldX
        primaryFemaleDialogueHead = item.primaryFemaleDialogueHead
        modelId = template.modelId
        modifiedColours = item.modifiedColours
        secondaryFemaleModel = item.secondaryFemaleModel
        spriteTranslateX = template.spriteTranslateX
        spriteCameraYaw = template.spriteCameraYaw
        primaryMaleModel = item.primaryMaleModel
        femaleWieldY = item.femaleWieldY
        maleWieldY = item.maleWieldY
        originalTextureColours = item.originalTextureColours
        System.arraycopy(item.options, 0, options, 0, 4)
        options[4] = "Discard"
    }

    companion object {
        val EMPTY = ItemDefinition()
    }
}