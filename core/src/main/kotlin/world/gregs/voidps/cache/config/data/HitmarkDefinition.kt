package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition
import world.gregs.voidps.cache.definition.Parameterized

/**
 * Hitmark (hit splat) type definition (CONFIG index, archive 29 for 948-5).
 */
data class HitmarkDefinition(
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
    var field21: Int = 0,
    var field22: Int = 0,
    var field23a: Int = 0,
    var field23b: Int = 0,
    var field23c: Int = 0,
    var field24a: Int = 0,
    var field24b: Int = 0,
    var field25: Int = 0,
    var field28: Int = 0,
    var field29: Int = 0,
    var field30: Int = 0,
    override var params: Map<Int, Any>? = null,
) : Definition, Parameterized {
    companion object {
        val EMPTY = HitmarkDefinition()
    }
}
