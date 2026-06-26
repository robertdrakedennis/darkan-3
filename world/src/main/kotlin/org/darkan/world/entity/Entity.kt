package org.darkan.world.entity

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

    /** Current world position. Default Lumbridge spawn. Updated by movement processing. */
    var tile: Tile = Tile(3224, 3216, 0)

    /** Facing direction (0-7 cardinal). Encoded into PLAYER_INFO / NPC_INFO appearance + face blocks. */
    var direction: Int = 0

    /** Per-tick update mask collector; consumed by PlayerInfoBuilder / NpcInfoBuilder in B6. */
    val pendingUpdates: PendingUpdates = PendingUpdates()
}
