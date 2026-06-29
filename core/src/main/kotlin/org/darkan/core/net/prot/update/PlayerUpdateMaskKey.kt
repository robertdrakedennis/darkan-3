package org.darkan.core.net.prot.update

/**
 * Identity of a single PLAYER_INFO extended-info block within a particular protocol revision.
 *
 * The bit positions and dispatch order for these blocks are revision-specific — 947-3 and 948
 * use different layouts (the 948 expansion-bit positions in particular reclaim bits 13 and 22
 * for header use, displacing the 947-3 data slots). To keep revision-specific tables out of
 * shared code, each revision provides its own table of `PlayerUpdateMaskKey` instances:
 *
 *  * `rev948/Rev948PlayerUpdateMaskKey.kt` — the 948 layout, per the Phase 1 delta doc.
 *
 * `PlayerUpdateMaskEncoder` is keyed by this interface (not by enum identity), so encoders
 * from any revision's table can register simultaneously without collisions — the per-revision
 * key instances are distinct objects.
 *
 * Consumers (PendingUpdates, PlayerInfoEncoder, ext-info encoders) operate on the interface
 * type, so swapping the active revision only swaps the key table the world produces.
 */
interface PlayerUpdateMaskKey {
    /** LE bit position within the player-info flag bitset (0..31). */
    val bit: Int
    /** Position in the client's fixed processing order. Encoders MUST serialise in ascending order. */
    val order: Int
    /** Convenience for `1 shl bit`. */
    val flag: Int get() = 1 shl bit
    /** Stable, log-friendly name for diagnostics. */
    val name: String
}
