package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition

/**
 * Base Animation Set (BAS / Render Animations) Definition
 *
 * Stores movement animation references for players and NPCs,
 * including stand, walk, run, and teleport animations with
 * directional variants and turn animations.
 */
data class BASDefinition(
    override var id: Int = -1,

    // Stand animations
    var standAnimation: Int = -1,
    var standTurn1: Int = -1,
    var standTurn2: Int = -1,

    // Walk animations
    var walkAnimation: Int = -1,
    var walkDir1: Int = -1,
    var walkDir2: Int = -1,
    var walkDir3: Int = -1,
    var walkTurn1: Int = -1,
    var walkTurn2: Int = -1,

    // Run animations
    var runAnimation: Int = -1,
    var runDir1: Int = -1,
    var runDir2: Int = -1,
    var runDir3: Int = -1,
    var runTurn1: Int = -1,
    var runTurn2: Int = -1,

    // Teleport animations
    var teleportAnimation: Int = -1,
    var teleDir1: Int = -1,
    var teleDir2: Int = -1,
    var teleDir3: Int = -1,
    var teleTurn1: Int = -1,
    var teleTurn2: Int = -1,
) : Definition {
    companion object {
        val EMPTY = BASDefinition()
    }
}
