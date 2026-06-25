package org.darkan.core.net.prot.update

/**
 * Identity of a single NPC_INFO extended-info block within a particular protocol revision.
 *
 * The bit positions and dispatch order for these blocks are revision-specific — 947-3 and 948
 * use different layouts (e.g., 948 reaches bit 31, and the per-block dispatch ordering is
 * substantially shuffled vs 947-3). To keep revision-specific tables out of shared code, each
 * revision provides its own table of `NpcUpdateMaskKey` instances:
 *
 *  * `rev948/Rev948NpcUpdateMaskKey.kt` — the 948 layout, per the Phase 1 delta doc.
 *
 * `NpcUpdateMaskEncoder` is keyed by this interface so encoders from any revision can register
 * simultaneously without collisions. Consumers (PendingUpdates, NpcInfoBuilder, etc.) operate
 * on the interface type so swapping revisions only swaps the underlying key table.
 *
 * Flag is exposed as a 64-bit Long because NPC masks reach bit 33 in 947-3 and bit 31 in 948.
 */
interface NpcUpdateMaskKey {
    /** LE bit position within the NPC-info flag bitset (0..63). */
    val bit: Int
    /** Position in the client's fixed processing order. Encoders MUST serialise in ascending order. */
    val order: Int
    /** Convenience for `1L shl bit`. */
    val flag: Long get() = 1L shl bit
    /** Stable, log-friendly name for diagnostics. */
    val name: String
}
