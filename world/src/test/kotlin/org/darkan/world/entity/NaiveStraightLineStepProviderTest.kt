package org.darkan.world.entity

import world.gregs.voidps.type.Tile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Self-test for the STAGE 2.1 naive straight-line [StepProvider]: the path it generates must walk from
 * the start tile to the clicked destination in the expected number of one-tile steps, where each step is
 * a valid [Direction8] index and the cumulative deltas land exactly on the destination. This is the
 * server-side half of the click-to-walk pipeline proof (the op22 GPI encoder, verified separately,
 * delivers the resulting steps to the client).
 */
class NaiveStraightLineStepProviderTest {

    private val provider = NaiveStraightLineStepProvider

    /** Walk the returned step indices from [from] and return the tile reached. */
    private fun walk(from: Tile, steps: List<Int>): Tile {
        var x = from.x
        var y = from.y
        for (dir in steps) {
            assertTrue(dir in 0..7, "each step must be a valid 3-bit Direction8 index, got $dir")
            x += Direction8.DX[dir]
            y += Direction8.DY[dir]
        }
        return Tile(x, y, from.level)
    }

    @Test
    fun `pure east click generates one cardinal step per tile and reaches the destination`() {
        val from = Tile(3227, 3219, 0)
        val dest = Tile(3232, 3219, 0) // +5 East, same Z
        val steps = provider.stepsTo(from, dest)

        assertEquals(5, steps.size, "5 tiles east → 5 cardinal steps")
        assertTrue(steps.all { it == Direction8.indexOf(1, 0) }, "every step is the EAST index")
        assertEquals(dest.x to dest.y, walk(from, steps).let { it.x to it.y }, "ends exactly on the destination")
    }

    @Test
    fun `pure diagonal click generates one diagonal step per tile`() {
        val from = Tile(3200, 3200, 0)
        val dest = Tile(3205, 3205, 0) // +5 East, +5 North → 5 NE diagonals
        val steps = provider.stepsTo(from, dest)

        assertEquals(5, steps.size, "equal X/Z delta → max(|dx|,|dz|)=5 diagonal steps")
        assertTrue(steps.all { it == Direction8.indexOf(1, 1) }, "every step is the NE index")
        assertEquals(dest.x to dest.y, walk(from, steps).let { it.x to it.y })
    }

    @Test
    fun `mixed click diagonals until one axis closes then goes cardinal`() {
        val from = Tile(3200, 3200, 0)
        val dest = Tile(3207, 3203, 0) // +7 East, +3 North → 3 NE diagonals, then 4 E cardinals = 7 steps
        val steps = provider.stepsTo(from, dest)

        // Chebyshev distance = max(7,3) = 7 steps.
        assertEquals(7, steps.size, "step count is the Chebyshev distance max(|dx|,|dz|)")
        assertEquals(3, steps.count { it == Direction8.indexOf(1, 1) }, "3 NE diagonals while both axes differ")
        assertEquals(4, steps.count { it == Direction8.indexOf(1, 0) }, "4 E cardinals after Z closes")
        assertEquals(dest.x to dest.y, walk(from, steps).let { it.x to it.y }, "ends exactly on the destination")
    }

    @Test
    fun `south-west click resolves the negative-delta diagonal`() {
        val from = Tile(3250, 3250, 0)
        val dest = Tile(3246, 3246, 0) // -4 West, -4 South → 4 SW diagonals
        val steps = provider.stepsTo(from, dest)

        assertEquals(4, steps.size)
        assertTrue(steps.all { it == Direction8.indexOf(-1, -1) }, "every step is the SW index")
        assertEquals(dest.x to dest.y, walk(from, steps).let { it.x to it.y })
    }

    @Test
    fun `clicking the current tile generates no steps`() {
        val from = Tile(3227, 3219, 0)
        assertTrue(provider.stepsTo(from, from).isEmpty(), "already at the destination → empty path")
    }

    @Test
    fun `a far destination is capped at MAX_STEPS and never spins`() {
        val from = Tile(3200, 3200, 0)
        val dest = Tile(3200 + NaiveStraightLineStepProvider.MAX_STEPS * 2, 3200, 0)
        val steps = provider.stepsTo(from, dest)

        assertEquals(NaiveStraightLineStepProvider.MAX_STEPS, steps.size, "step count is hard-capped")
    }
}
