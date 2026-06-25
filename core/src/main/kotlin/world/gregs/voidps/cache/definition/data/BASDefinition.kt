package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition

/**
 * Base Animation Set (BAS / render animations) definition, CONFIG archive 32.
 *
 * Field layout and opcode mapping mirror the NXT client (engine BASType) for
 * 948-5. (The legacy core layout used an older revision's opcode assignment.)
 */
data class BASDefinition(
    override var id: Int = -1,
    var standAnim: Int = -1,
    var standTurnAnim: Int = -1,
    var walkAnim: Int = -1,
    var runAnim: Int = -1,
    var turnAroundAnim: Int = -1,
    var turnRightAnim: Int = -1,
    var walkBackAnim: Int = -1,
    var walkLeftAnim: Int = -1,
    var walkRightAnim: Int = -1,
    var crawlAnim: Int = -1,
    var renderOffsetX: Int = 0,
    var renderOffsetY: Int = 0,
    var anim38: Int = -1,
    var anim39: Int = -1,
    var anim40: Int = -1,
    var anim41: Int = -1,
    var anim42: Int = -1,
    var anim43: Int = -1,
    var anim44: Int = -1,
    var field45: Int = 0,
    var anim46: Int = -1,
    var anim47: Int = -1,
    var anim48: Int = -1,
    var anim49: Int = -1,
    var anim50: Int = -1,
    var anim51: Int = -1,
    var renderOffsetX2: Int = 0,
    var renderOffsetY2: Int = 0,
) : Definition {
    companion object {
        val EMPTY = BASDefinition()
    }
}
