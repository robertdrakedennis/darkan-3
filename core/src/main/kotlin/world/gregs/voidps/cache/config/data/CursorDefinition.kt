package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition

/**
 * Cursor type definition (CONFIG index, archive 34 for 948-5).
 */
data class CursorDefinition(
    override var id: Int = -1,
    var spriteId: Int = -1,
    var hotspotX: Int = 0,
    var hotspotY: Int = 0,
) : Definition {
    companion object {
        val EMPTY = CursorDefinition()
    }
}
