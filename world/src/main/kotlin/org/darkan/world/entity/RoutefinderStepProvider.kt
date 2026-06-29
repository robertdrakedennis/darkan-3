package org.darkan.world.entity

import world.gregs.voidps.collision.CollisionFlagMap
import world.gregs.voidps.routefinder.RouteFinding
import world.gregs.voidps.type.Tile
import kotlin.math.abs

/**
 * Collision-aware click-to-walk [StepProvider].
 *
 * The provider loads cache-derived collision for the rsmod search window around
 * the player, runs the size-1 BFS routefinder, and converts each consecutive
 * route tile into the verified 3-bit [Direction8] index expected by [MovementQueue].
 */
class RoutefinderStepProvider(
    private val flags: CollisionFlagMap = WorldCollisionFlagMap.flags,
    private val ensureLoaded: (Tile) -> Unit = WorldCollisionFlagMap::ensureLoadedForRoute,
    private val routeFinding: RouteFinding = RouteFinding(flags),
    private val maxSteps: Int = NaiveStraightLineStepProvider.MAX_STEPS,
) : StepProvider {

    override fun stepsTo(from: Tile, dest: Tile): List<Int> {
        if (from.x == dest.x && from.y == dest.y) {
            return emptyList()
        }
        ensureLoaded(from)
        val route = routeFinding.findRoute(
            level = from.level,
            srcX = from.x,
            srcZ = from.y,
            destX = dest.x,
            destZ = dest.y,
        )
        if (route.failed) {
            return emptyList()
        }

        val steps = ArrayList<Int>(minOf(route.size, maxSteps))
        var x = from.x
        var y = from.y
        for (tile in route.take(maxSteps)) {
            val dx = tile.x - x
            val dy = tile.z - y
            if (abs(dx) > 1 || abs(dy) > 1 || (dx == 0 && dy == 0)) {
                return emptyList()
            }
            steps += Direction8.indexOf(dx, dy)
            x = tile.x
            y = tile.z
        }
        return steps
    }
}
