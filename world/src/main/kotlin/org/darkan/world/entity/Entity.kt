package org.darkan.world.entity

import org.darkan.world.world.SpawnService
import world.gregs.voidps.type.Tile

/**
 * Abstract parent for [Player] and [Npc] world entities.
 *
 * Holds only the minimum simulation state that PLAYER_INFO (op 27) and NPC_INFO (op 12)
 * builders need to read each tick: server index, tile, facing direction, and the
 * per-tick [PendingUpdates] mask collector. All higher-level state (combat, skills,
 * inventory, etc.) is layered on top by Player / Npc subclasses and downstream phases.
 */
abstract class Entity {
    /** Player slot (1..2047) or NPC server index (16-bit ushort per A5 phase 2). */
    abstract val index: Int

    // Note: Entity declares `val index` to keep the read-only contract for downstream
    // builders; concrete subclasses (Player) may override with `var` if the slot id has to
    // be assigned post-construction (see [org.darkan.world.world.Players.allocate]).

    /** Current world position. Updated from [SpawnService] during player login and by movement processing. */
    var tile: Tile = SpawnService.DEFAULT_SPAWN

    /** Facing direction (0-7 cardinal). Encoded into PLAYER_INFO / NPC_INFO appearance + face blocks. */
    var direction: Int = 0

    /** Per-tick update mask collector; consumed by PlayerInfoEncoder / NpcInfoEncoder. */
    val pendingUpdates: PendingUpdates = PendingUpdates()

    /**
     * Pending one-tile walk steps the world tick drains (1/tick). The movement SOURCE: this increment
     * (1.2b 2a) only the scripted debug path enqueues onto it; client-input-driven enqueue is a later
     * increment. See [MovementQueue].
     */
    val movementQueue: MovementQueue = MovementQueue()

    /**
     * The 3-bit walk-direction index applied THIS tick, or [MovementQueue.NO_STEP] (`-1`) when the
     * entity did not walk this tick. Set by the world tick when it polls + applies a step from
     * [movementQueue]; read by the op22 high-res encoder so the WALK form
     * ([org.darkan.world.net.PlayerMovementEncoder.encodeHighResPosition]) emits this exact index, then
     * reset to `-1` at the end of the tick. A non-walking entity keeps `-1` → the stationary form.
     *
     * On a RUN tick (2 tiles/tick) this still carries the FIRST of the two combined steps — the WALK
     * portion of the run handoff (the run-START's first tile, and an odd-tail walk step) reads it, while
     * the RUN step proper reads [lastRunDelta]. On a pure 2-tile run tick both are set ([lastRunDelta]
     * holds the summed delta; [lastWalkStepDir] holds the first sub-step's dir).
     */
    @Volatile
    var lastWalkStepDir: Int = MovementQueue.NO_STEP

    /**
     * Whether the entity is in the **run** movement mode (2 tiles/tick) — the op74 run modifier / ctrl-
     * click toggles it ([org.darkan.world.server.packet.MoveGameClickHandler]). When set, the world tick
     * drains up to TWO queued one-tile steps per tick and the op22 high-res encoder emits the verified
     * RUN forms (run-START `mvt=3` desc 0xc → RUN-STEP `mvt=2` runCode → run-STOP `mvt=3` desc 0x0), with
     * a clean handoff to a `mvt=1` WALK step on the odd last tile (the runner slows to 1 tile). Persists
     * across ticks until the modifier flips it.
     */
    @Volatile
    var running: Boolean = false

    /**
     * The combined 2-tile RUN delta applied THIS tick as a 4-bit `runCode` (index into the verified
     * `RUN_DX`/`RUN_DY` perimeter table — `core/.../recorder/ClientStateCrossCheck.kt`), or
     * [MovementQueue.NO_STEP] (`-1`) when the entity did not take a full 2-tile run step this tick. Set
     * by the world tick when it polls + applies TWO queued one-tile steps while [running]; read by the
     * op22 high-res RUN-STEP form ([org.darkan.world.net.PlayerMovementEncoder.encodeHighResPosition] /
     * [org.darkan.world.net.PlayerMovementEncoder.runStepCode]) so it emits this exact `runCode`, then
     * reset to `-1` at the end of the tick. A run tick whose tail is a single tile leaves this `-1` and
     * sets [lastWalkStepDir] instead (the run→walk handoff).
     */
    @Volatile
    var lastRunDelta: Int = MovementQueue.NO_STEP
}
