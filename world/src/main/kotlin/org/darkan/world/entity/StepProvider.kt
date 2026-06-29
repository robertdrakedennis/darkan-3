package org.darkan.world.entity

import world.gregs.voidps.type.Tile
import kotlin.math.sign

/**
 * Path-generation seam for click-to-walk. Given a start tile and a destination tile, a [StepProvider]
 * yields the ordered sequence of one-tile walk steps (as 3-bit [Direction8] indices) the server enqueues
 * onto the player's [MovementQueue]. Returning indices — not raw `(dx,dy)` — keeps the contract aligned
 * with [MovementQueue.enqueueStep] / the op22 WALK encoder, which both speak the verified direction table.
 *
 * ## Why an interface (the stage 2.1 → 2.2 swap point)
 *
 * STAGE 2.1 ships [NaiveStraightLineStepProvider] — a collision-FREE greedy walk that proves the
 * decode → queue → encode pipeline end-to-end in open areas. STAGE 2.2 swaps in a real collision-aware
 * routefinder by providing a different [StepProvider] to [org.darkan.world.server.packet.MoveGameClickHandler]
 * with NO change to the handler, the decoder, or the encoder — this interface is the entire seam.
 *
 * Implementations MUST:
 *  - return steps that each move exactly one tile (a cardinal or diagonal [Direction8] index),
 *  - return an EMPTY list when already at the destination (or it cannot make progress),
 *  - never exceed a sane bound (the naive impl caps at [NaiveStraightLineStepProvider.MAX_STEPS]).
 */
fun interface StepProvider {
    /**
     * Produce the ordered 3-bit [Direction8] step indices to walk from [from] to [dest]. The plane is
     * taken from [from]; [dest]'s plane is ignored (op74 carries only X/Z — the player stays on its
     * current plane). An empty result means "no movement" (already there, or no reachable step).
     */
    fun stepsTo(from: Tile, dest: Tile): List<Int>
}

/**
 * STAGE 2.1 naive straight-line step provider: each step moves one tile toward the destination on each
 * axis independently (`dx = sign(destX-curX)`, `dz = sign(destZ-curZ)`), giving a cardinal step when only
 * one axis differs and a diagonal step when both do, until the destination tile is reached. There is NO
 * collision detection or obstacle avoidance — it works in open ground only, which is exactly the
 * pipeline-proof scope (the open spawn courtyard). STAGE 2.2 replaces this with a routefinder behind the
 * same [StepProvider] seam.
 *
 * The Chebyshev distance to the destination strictly decreases every step (each step closes both axes by
 * up to one), so the loop is bounded by that distance; [MAX_STEPS] is a hard defensive cap so a pathological
 * or far-off-map destination can never spin or flood the queue.
 */
object NaiveStraightLineStepProvider : StepProvider {

    /**
     * Defensive upper bound on generated steps. A single click can legitimately span a large open area,
     * but the queue is drained one tile/tick, so an unbounded path is never useful and a garbage
     * destination must not be able to enqueue an unbounded run. 256 tiles (~2.5 loaded scenes) is well
     * beyond any single on-screen click while staying a firm cap.
     */
    const val MAX_STEPS: Int = 256

    override fun stepsTo(from: Tile, dest: Tile): List<Int> {
        val steps = ArrayList<Int>()
        var x = from.x
        var y = from.y
        while ((x != dest.x || y != dest.y) && steps.size < MAX_STEPS) {
            val dx = (dest.x - x).sign
            val dy = (dest.y - y).sign
            steps += Direction8.indexOf(dx, dy)
            x += dx
            y += dy
        }
        return steps
    }
}
