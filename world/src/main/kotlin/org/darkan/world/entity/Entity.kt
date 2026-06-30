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
     */
    @Volatile
    var lastWalkStepDir: Int = MovementQueue.NO_STEP
}
