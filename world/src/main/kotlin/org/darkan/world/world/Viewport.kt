package org.darkan.world.world

import org.darkan.world.entity.Player

/**
 * Per-player visibility state read each tick by the PLAYER_INFO / NPC_INFO builders
 * in B6. Holds the high-res / low-res cohorts for PLAYER_INFO (op 27) per A4
 * §"List structure" and the visible-NPC cohort for NPC_INFO (op 12) per A5 Phase 1.
 *
 * Lifecycle:
 *  * On the very first tick after login, [firstTick] is true and the builder emits
 *    the "init form" of PLAYER_INFO with 18-bit region hashes for every low-res
 *    player (per A4 §"First tick / init payload"). The builder clears [firstTick]
 *    after the init payload is appended.
 *  * Subsequent ticks emit the incremental form (1-bit gating + per-cohort movement).
 *
 * The high-res list MUST always start with the owner at position 0 (the client's
 * "local player" slot); the builder validates this invariant.
 */
class Viewport(val owner: Player) {
    /** Players currently in the owner's high-res cohort. Owner is always element [0]. */
    val highResIndices: MutableList<Int> = mutableListOf(owner.index)

    /** Players currently in the owner's low-res cohort (visible region, not yet promoted to high-res). */
    val lowResIndices: MutableList<Int> = mutableListOf()

    /**
     * Cached encoded appearance hash per other-player slot. Indexed by player index
     * (0..2047). Builder compares against the latest appearance bytes to decide
     * whether to re-emit the APPEARANCE block this tick.
     */
    val cachedApprHashes: Array<ByteArray?> = arrayOfNulls(2048)

    /** NPC indices currently in the owner's visible cohort (NPC_INFO Phase 1). */
    val visibleNpcs: MutableList<Int> = mutableListOf()

    /**
     * NPC indices whose NPC_INFO Phase 2 add-record has already been sent to THIS viewer.
     * Per-viewer (not global Npc state): with multiple players, only the first-built viewer
     * would otherwise ever receive an add record. Entries are removed when the NPC leaves
     * [visibleNpcs] so a re-entering NPC is re-added.
     */
    val sentNpcAdds: MutableSet<Int> = mutableSetOf()

    /**
     * Build area centre in chunk (zone) coordinates. Default Lumbridge: tile
     * (3200, 3200) -> chunk (400, 400). [buildAreaSize] is 13 chunks (= 104 tiles)
     * matching the standard NXT viewport.
     */
    var buildAreaChunkX: Int = 400
    var buildAreaChunkY: Int = 400
    var buildAreaSize: Int = 13

    /** First-tick init flag — must send the init-form PlayerInfo with 18-bit region hashes. */
    var firstTick: Boolean = true
}
