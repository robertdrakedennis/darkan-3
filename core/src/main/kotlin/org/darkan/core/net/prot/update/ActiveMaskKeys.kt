package org.darkan.core.net.prot.update

/**
 * Process-global registry of the currently-active set of well-known mask keys for the loaded
 * revision. Set once during codec registration (see `Rev948Codec`).
 *
 * This exists because a handful of world-side builders need to refer to specific mask blocks
 * by NAME (e.g., "the APPEARANCE block") rather than by enum identity — and those names
 * resolve to different bit positions in 947 vs 948. Without a lookup the builder would have
 * to know about every per-revision enum directly, which defeats the point of the per-revision
 * shape.
 *
 * The set of well-known keys exposed here is intentionally minimal — anything that game logic
 * can produce on its own (by emitting an explicit `setPlayer(MyRev.SOME_KEY, mask)`) does NOT
 * need to be in here. Only blocks that the builder MUST be able to synthesize as a fallback
 * (currently just APPEARANCE for the first-tick render path) live here.
 */
object ActiveMaskKeys {

    /**
     * APPEARANCE block key for the active revision. Set by the codec registration.
     *
     * If null, `PlayerInfoEncoder` skips its "synthesize APPEARANCE for first-tick" fast-path
     * and the caller is expected to set the appearance via `setPlayer(rev.APPEARANCE, ...)`
     * directly.
     */
    @Volatile
    var playerAppearance: PlayerUpdateMaskKey? = null

    /**
     * Absolute LE bit positions of the expansion ("continue") bits in the PLAYER_INFO ext-info
     * flag bitset header, for the active revision. Index N is the continue-bit that must be set in
     * byte N so the client reads byte N+1. Published by codec registration from each revision's
     * `EXPANSION_BITS` array so `PlayerInfoEncoder` never hardcodes per-revision literals.
     *
     * 947-3 = {0, 14, 18}; 948 = {0, 13, 22}.
     */
    @Volatile
    var playerExpansionBits: IntArray = intArrayOf(0, 14, 18)

    /**
     * Absolute LE bit positions of the expansion ("continue") bits in the NPC_INFO ext-info flag
     * bitset header, for the active revision. Same index convention as [playerExpansionBits].
     *
     * 947-3 = {6, 13, 22, 24}; 948 = {6, 8, 19, 25}.
     */
    @Volatile
    var npcExpansionBits: IntArray = intArrayOf(6, 13, 22, 24)
}
