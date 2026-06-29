package org.darkan.world.entity

import java.util.ArrayDeque

/**
 * Per-entity queue of pending **one-tile walk steps** — the server-side movement source the world
 * tick drains, one step per tick (the canonical RS walk cadence = 1 tile/tick).
 *
 * ## What a step is
 *
 * Each queued element is a **3-bit walk direction** — the index into the verified 8-direction table
 * `PLAYER_REGION_DX` / `PLAYER_REGION_DY` (`core/.../recorder/ClientStateCrossCheck.kt:1614-1615`,
 * `DX=[-1,0,1,-1,1,-1,0,1]`, `DY=[-1,-1,-1,0,0,1,1,1]`). Storing the already-resolved direction index
 * (not the raw `(dx,dy)`) means the world tick and the op22 encoder agree on the EXACT 3-bit value the
 * client reads back: the op22 high-res WALK form emits this index verbatim as `gBit(3)` (the inverse
 * of the decode's `decodeKnownPlayerUpdate` mvt=1 branch, `ClientStateCrossCheck.kt:1260-1266`).
 *
 * ## Increment scope (1.2b increment 2a — LOCAL-PLAYER WALK)
 *
 * This increment only ever enqueues a SCRIPTED debug path (see [DARKAN_DEBUG_WALK_PATH][org.darkan.core.EnvVars.debugWalkPath]).
 * Client-input-driven enqueue (a C2S move handler feeding real player clicks) and run/teleport are
 * later increments — this queue holds nothing but single walk steps.
 */
class MovementQueue {

    /** FIFO of pending 3-bit walk-direction indices (0..7 into [Direction8]). One is consumed per tick. */
    private val steps = ArrayDeque<Int>()

    /** True if a walk step is waiting to be applied this tick. */
    fun hasPendingStep(): Boolean = steps.isNotEmpty()

    /**
     * Enqueue a one-tile step given its `(dx, dy)` with `dx, dy ∈ {-1, 0, 1}` (RS y increases NORTH).
     * The `(dx,dy)` is resolved to its 3-bit direction index via [Direction8.indexOf]; a `(0,0)`
     * no-move or an out-of-range delta throws (the caller must pass a real one-tile step).
     */
    fun enqueueStep(dx: Int, dy: Int) {
        steps.addLast(Direction8.indexOf(dx, dy))
    }

    /**
     * Poll the next pending step's 3-bit direction index (0..7), or `-1` if the queue is empty.
     * The world tick polls at most ONE per tick, applies it to the entity's tile, and hands the same
     * index to the op22 encoder so the WALK form emits it this tick.
     */
    fun pollStep(): Int = steps.pollFirst() ?: NO_STEP

    /** Drop every queued step (e.g. on teleport — increment 2b). */
    fun clear() = steps.clear()

    companion object {
        /** Sentinel returned by [pollStep] when nothing is queued. */
        const val NO_STEP: Int = -1
    }
}
