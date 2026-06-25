package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition

/**
 * Headbar (health bar) type definition (CONFIG index, archive 33 for 948-5).
 */
data class HeadbarDefinition(
    override var id: Int = -1,
    var spriteId: Int = -1,
    var hasColor: Boolean = false,
    var color: Int = 0,
    var spriteId2: Int = -1,
    var spriteId3: Int = -1,
    var spriteId4: Int = -1,
    var spriteId5: Int = -1,
    var field7: Int = 0,
    var height: Int = 0,
    var field10: Int = 0,
    var field11: Int = 0,
    var field12: Int = 0,
    var field13: Int = 0,
    var field14: Int = 0,
    var field16a: Int = 0,
    var field16b: Int = 0,
    var field19: Int = 0,
    var field20: Int = 0,
) : Definition {
    companion object {
        val EMPTY = HeadbarDefinition()
    }
}
