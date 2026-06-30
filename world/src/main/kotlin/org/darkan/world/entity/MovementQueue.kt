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
 * ## Scope
 *
 * Enqueued by STAGE 2.1 click-to-walk: the op74 handler
 * ([org.darkan.world.server.packet.MoveGameClickHandler]) feeds real player clicks (path generated behind
 * the [StepProvider] seam — collision-aware routefinder in STAGE 2.2). The original
 * scripted-walk seeder (`DARKAN_DEBUG_WALK_PATH`) used the same enqueue path and is now retired to a test
 * fixture. Teleport is a later increment. RUN (2 steps/tick) is supported via [pollRunStep] — the world
 * tick polls TWO queued one-tile steps per tick when the entity is running, summing them into one 2-tile
 * RUN delta; an odd last tile falls back to a single [pollStep] WALK step (the run→walk handoff).
 */
class MovementQueue {

    /** FIFO of pending 3-bit walk-direction indices (0..7 into [Direction8]). One is consumed per tick. */
    private val steps = ArrayDeque<Int>()

    /** True if a walk step is waiting to be applied this tick. */
    fun hasPendingStep(): Boolean = steps.isNotEmpty()

    /** Number of pending one-tile steps still queued — the run drain peeks this to decide 2-vs-1-tile. */
    fun pendingCount(): Int = steps.size

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

    /**
     * Poll TWO pending one-tile steps for a single RUN tick, returning their first sub-step's 3-bit
     * direction index ([RunStep.firstStepDir]) and the SUMMED `(dx,dy)` of both ([RunStep.dx]/[dy]) — a
     * Chebyshev-distance-2 delta the op22 RUN-STEP form ([org.darkan.world.net.PlayerMovementEncoder])
     * maps to a 4-bit `runCode`. Returns `null` when fewer than two steps are queued: the caller (the
     * world tick) then falls back to a single [pollStep] WALK step for the odd last tile (the run→walk
     * handoff the binary uses to slow the runner to 1 tile).
     *
     * Both consumed sub-steps are real one-tile moves (each `(dx,dy) ∈ {-1,0,1}`, not both 0), so the
     * summed delta is in `[-2,2]×[-2,2]` and always has `max(|dx|,|dy|) == 2` — the perimeter of the 5×5
     * run box. (Two opposing steps that would sum to a sub-2 Chebyshev delta cannot occur on a real
     * generated path; the encoder's reverse lookup asserts the 2-tile invariant regardless.)
     */
    fun pollRunStep(): RunStep? {
        if (steps.size < 2) return null
        val first = steps.pollFirst()!!
        val second = steps.pollFirst()!!
        val dx = Direction8.DX[first] + Direction8.DX[second]
        val dy = Direction8.DY[first] + Direction8.DY[second]
        return RunStep(firstStepDir = first, secondStepDir = second, dx = dx, dy = dy)
    }

    /**
     * One consumed RUN tick: the two one-tile sub-steps and their summed tile delta. [firstStepDir] is
     * the 3-bit dir of the first sub-step (the WALK portion of a run-START); [dx]/[dy] is the combined
     * 2-tile delta the RUN-STEP `mvt=2` form encodes as a `runCode`.
     */
    data class RunStep(val firstStepDir: Int, val secondStepDir: Int, val dx: Int, val dy: Int)

    /** Drop every queued step (e.g. on teleport — increment 2b). */
    fun clear() = steps.clear()

    companion object {
        /** Sentinel returned by [pollStep] when nothing is queued. */
        const val NO_STEP: Int = -1
    }
}
