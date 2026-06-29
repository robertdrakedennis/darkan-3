package org.darkan.world.entity

import world.gregs.voidps.collision.CollisionMap
import world.gregs.voidps.type.Tile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoutefinderStepProviderTest {

    @Test
    fun `routes around a hand-built wall instead of walking straight through it`() {
        val flags = CollisionMap()
        val from = Tile(3200, 3202, 0)
        val dest = Tile(3206, 3202, 0)
        allocateSearchWindow(flags, from)
        val blockedTiles = (3199..3205).map { y -> 3203 to y }.toSet()
        for ((x, y) in blockedTiles) {
            flags.addObject(Tile(x, y, 0), sizeX = 1, sizeY = 1, blocksProjectiles = false, pathfinder = false)
        }

        val provider = RoutefinderStepProvider(flags = flags, ensureLoaded = {}, maxSteps = 64)
        val steps = provider.stepsTo(from, dest)
        val visited = walk(from, steps)

        assertTrue(steps.size > 6, "straight-line distance is 6, so a valid route must detour")
        assertEquals(dest.x to dest.y, visited.last(), "route reaches the clicked tile")
        assertTrue(visited.none { it in blockedTiles }, "route never steps onto blocked wall tiles")
        assertTrue(
            visited.any { (_, y) -> y == 3198 || y == 3206 },
            "route goes around one end of the wall"
        )
    }

    private fun allocateSearchWindow(flags: CollisionMap, from: Tile) {
        val minX = from.x - 64
        val maxX = from.x + 63
        val minY = from.y - 64
        val maxY = from.y + 63
        for (x in minX..maxX step 8) {
            for (y in minY..maxY step 8) {
                flags.allocateIfAbsent(x, y, from.level)
            }
        }
    }

    private fun walk(from: Tile, steps: List<Int>): List<Pair<Int, Int>> {
        val visited = ArrayList<Pair<Int, Int>>()
        var x = from.x
        var y = from.y
        for (dir in steps) {
            x += Direction8.DX[dir]
            y += Direction8.DY[dir]
            visited += x to y
        }
        return visited
    }
}
