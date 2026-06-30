package org.darkan.world.entity

import org.darkan.world.net.PlayerMovementEncoder
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

/**
 * Unit coverage for the RUN movement primitives: [MovementQueue.pollRunStep] (2 steps/tick) and the
 * encoder's [PlayerMovementEncoder.runStepCode] reverse lookup. These are the building blocks the world
 * tick's run drain + the op22 RUN-STEP form rely on; the full encode→decode round-trip is asserted by
 * [org.darkan.world.net.PlayerRunStateMachineTest].
 *
 * The verified RUN table (`core/.../recorder/ClientStateCrossCheck.kt`, mirrored in the encoder):
 * ```
 *   RUN_DX = [-2,-1, 0, 1, 2, -2, 2, -2, 2, -2, 2, -2,-1, 0, 1, 2]
 *   RUN_DY = [-2,-2,-2,-2,-2, -1,-1,  0, 0,  1, 1,  2, 2, 2, 2, 2]
 * ```
 */
class MovementQueueRunTest {

    private val runDx = intArrayOf(-2, -1, 0, 1, 2, -2, 2, -2, 2, -2, 2, -2, -1, 0, 1, 2)
    private val runDy = intArrayOf(-2, -2, -2, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 2, 2, 2)

    @Test
    fun `pollRunStep combines two one-tile steps into one Chebyshev-2 delta`() {
        val q = MovementQueue()
        // Two EAST steps → summed (dx=+2, dy=0).
        q.enqueueStep(1, 0)
        q.enqueueStep(1, 0)
        val run = q.pollRunStep()!!
        assertEquals(2, run.dx, "two east steps sum to dx=+2")
        assertEquals(0, run.dy, "two east steps sum to dy=0")
        assertEquals(Direction8.indexOf(1, 0), run.firstStepDir, "firstStepDir is the first sub-step (east)")
        assertEquals(0, q.pendingCount(), "both steps consumed")
    }

    @Test
    fun `pollRunStep combines two differing one-tile steps (diagonal run)`() {
        val q = MovementQueue()
        // EAST then NORTH-EAST → (dx=1+1, dy=0+1) = (2,1) (Chebyshev 2).
        q.enqueueStep(1, 0)
        q.enqueueStep(1, 1)
        val run = q.pollRunStep()!!
        assertEquals(2 to 1, run.dx to run.dy, "E + NE sums to (2,1)")
        assertEquals(max(abs(run.dx), abs(run.dy)), 2, "the summed delta is Chebyshev-distance 2")
    }

    @Test
    fun `pollRunStep returns null when fewer than two steps remain (odd tail)`() {
        val q = MovementQueue()
        q.enqueueStep(1, 0)
        assertNull(q.pollRunStep(), "one queued step cannot form a 2-tile run")
        assertEquals(1, q.pendingCount(), "the single step is NOT consumed (left for a WALK pollStep)")
        assertEquals(Direction8.indexOf(1, 0), q.pollStep(), "the odd tail polls as a single WALK step")
    }

    @Test
    fun `runStepCode is the exact inverse of the RUN table for every code`() {
        for (code in runDx.indices) {
            val back = PlayerMovementEncoder.runStepCode(runDx[code], runDy[code])
            assertEquals(code, back, "runStepCode(${runDx[code]},${runDy[code]}) round-trips to code $code")
        }
    }

    @Test
    fun `runStepCode rejects a non-perimeter delta`() {
        // (1,1) is Chebyshev distance 1 (a walk diagonal), not on the 5x5 run perimeter.
        assertFailsWith<IllegalArgumentException> { PlayerMovementEncoder.runStepCode(1, 1) }
        // (0,0) no-move.
        assertFailsWith<IllegalArgumentException> { PlayerMovementEncoder.runStepCode(0, 0) }
        // (3,0) out of range.
        assertFailsWith<IllegalArgumentException> { PlayerMovementEncoder.runStepCode(3, 0) }
    }

    @Test
    fun `every two-one-tile-step combination that runStepCode accepts is Chebyshev-2`() {
        // Exhaustively pair all 8 directions with all 8 directions; whenever the summed delta is a legal
        // run code, it MUST be Chebyshev-distance 2 (the run-box perimeter invariant).
        for (a in Direction8.DX.indices) {
            for (b in Direction8.DX.indices) {
                val dx = Direction8.DX[a] + Direction8.DX[b]
                val dy = Direction8.DY[a] + Direction8.DY[b]
                val code = runCatching { PlayerMovementEncoder.runStepCode(dx, dy) }.getOrNull() ?: continue
                assertTrue(max(abs(dx), abs(dy)) == 2, "accepted run delta ($dx,$dy) [code $code] is Chebyshev-2")
            }
        }
    }
}
