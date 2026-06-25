package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition
import world.gregs.voidps.cache.definition.Extra
import world.gregs.voidps.cache.definition.Parameterized

data class QuestDefinition(
    override var id: Int = -1,
    var name: String? = null,
    var description: String? = null,
    var listName: String? = null,
    var sortKey: Int = 0,
    var difficulty: Int = -1,
    var members: Int = 0,
    var questFlags: Boolean = false,
    var questPoints: Int = -1,
    var pathStart: IntArray? = null,
    var otherPathStart: Int = -1,
    var questRequirements: IntArray? = null,
    var skillRequirements: Array<IntArray>? = null,
    var itemSprite: Int = -1,
    override var params: Map<Int, Any>? = null,
    override var stringId: String = "",
    override var extras: Map<String, Any>? = null,
) : Definition, Parameterized, Extra {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QuestDefinition

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (listName != other.listName) return false
        if (sortKey != other.sortKey) return false
        if (difficulty != other.difficulty) return false
        if (members != other.members) return false
        if (questFlags != other.questFlags) return false
        if (questPoints != other.questPoints) return false
        if (pathStart != null) {
            if (other.pathStart == null) return false
            if (!pathStart.contentEquals(other.pathStart)) return false
        } else if (other.pathStart != null) return false
        if (otherPathStart != other.otherPathStart) return false
        if (questRequirements != null) {
            if (other.questRequirements == null) return false
            if (!questRequirements.contentEquals(other.questRequirements)) return false
        } else if (other.questRequirements != null) return false
        if (skillRequirements != null) {
            if (other.skillRequirements == null) return false
            if (!skillRequirements.contentDeepEquals(other.skillRequirements)) return false
        } else if (other.skillRequirements != null) return false
        if (itemSprite != other.itemSprite) return false
        if (params != other.params) return false
        if (stringId != other.stringId) return false
        if (extras != other.extras) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (listName?.hashCode() ?: 0)
        result = 31 * result + sortKey
        result = 31 * result + difficulty
        result = 31 * result + members
        result = 31 * result + questFlags.hashCode()
        result = 31 * result + questPoints
        result = 31 * result + (pathStart?.contentHashCode() ?: 0)
        result = 31 * result + otherPathStart
        result = 31 * result + (questRequirements?.contentHashCode() ?: 0)
        result = 31 * result + (skillRequirements?.contentDeepHashCode() ?: 0)
        result = 31 * result + itemSprite
        result = 31 * result + (params?.hashCode() ?: 0)
        result = 31 * result + stringId.hashCode()
        result = 31 * result + (extras?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "QuestDefinition(id=$id, name=$name, description=$description, listName=$listName, sortKey=$sortKey, difficulty=$difficulty, members=$members, questFlags=$questFlags, questPoints=$questPoints, pathStart=${pathStart?.contentToString()}, otherPathStart=$otherPathStart, questRequirements=${questRequirements?.contentToString()}, skillRequirements=${skillRequirements?.contentDeepToString()}, itemSprite=$itemSprite, params=$params, stringId='$stringId', extras=$extras)"
    }

    // --- Engine (QuestType) public-surface aliases ---

    /** Engine alias for [questPoints]. */
    val questPointReward: Int get() = questPoints

    /** Engine alias for [itemSprite]. */
    val spriteId: Int get() = itemSprite

    companion object {
        val EMPTY = QuestDefinition()
    }
}