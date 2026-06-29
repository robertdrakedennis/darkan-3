package org.darkan.world.net

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskKey
import org.darkan.core.net.prot.update.UpdateMask
import org.darkan.core.net.prot.update.UpdateMaskHeader
import org.darkan.world.entity.Player
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Single responsibility: encode the **PLAYER_INFO extended-info byte block** for one player — the
 * flag-bitset header + the per-block payloads + the local-player APPEARANCE-synth fast-path — and
 * answer the two gating questions the bit passes ask: [needsAnyUpdate] (drives `hasUpdate`) and
 * [hasFlaggableExtendedInfo] (drives `hasExtendedInfo`). It owns no cohort/pass/movement logic; the
 * orchestrator ([PlayerInfoEncoder]) and movement encoder ([PlayerMovementEncoder]) call into it.
 *
 * Wire reference (relocated verbatim — do not delete): ext-info layout is
 * `docs/net/serverprot/player-extinfo-948.md` — a 1..4 byte expansion-driven LE flag bitset
 * followed by per-flag blocks in ascending [PlayerUpdateMaskKey.order]; encoders dispatched via
 * [PlayerUpdateMaskEncoder] (registered in `Rev948ServerCodecsUpdateMasks.kt`).
 *
 * Encoder-registration invariant: only mask bits with a registered encoder are flagged in the
 * bitset. Flagging a bit without an encoder would leave the client consuming bytes the server never
 * emitted → cipher-counter desync. Unregistered keys are dropped with a WARN-once log.
 */
object PlayerExtInfoEncoder {

    /** Reduced log-spam: report each missing mask-encoder key only once per JVM lifetime. */
    private val warnedMissingPlayerEncoders: MutableSet<PlayerUpdateMaskKey> =
        Collections.newSetFromMap(ConcurrentHashMap())

    /**
     * Encode the extended-info byte block for one player per §4C.
     *
     * Wire layout:
     *  * 1..4 byte expansion-driven flag bitset (LE). Expansion bits at byte 0 bit 0
     *    (mask 0x01), byte 1 bit 6 (mask 0x40), byte 2 bit 2 (mask 0x04).
     *  * Per-flag block in fixed processing order (ascending [PlayerUpdateMaskKey.order]).
     *
     * APPEARANCE synth fast-path: for the local player's render we synthesise an APPEARANCE block
     * from [Player.appearance.cachedBytes] (built once at Player creation) when it is not already in
     * the pending masks. The APPEARANCE key is revision-specific (different bit positions in 947 vs
     * 948), so we look it up via [ActiveMaskKeys.playerAppearance] which the active codec registers
     * at startup. If null (no codec registered, or the revision doesn't model APPEARANCE) we skip the
     * synth and rely on explicit `setPlayer(rev.APPEARANCE, ...)` from game logic.
     *
     * Relocated unchanged from the old monolithic builder's ext-info block encoder — byte output is identical.
     */
    fun encodeExtendedInfoBlock(target: Player): ByteArray {
        val extOut = BufferWriter(256)

        // Filter pending masks down to those with a registered encoder. The unregistered ones MUST
        // NOT be flagged in the bitset — doing so would desync the cipher counter.
        val entries = target.pendingUpdates.playerMaskEntries().filter { (key, _) ->
            val has = PlayerUpdateMaskEncoder.hasEncoder(key)
            if (!has && warnedMissingPlayerEncoders.add(key)) {
                System.err.println(
                    "[PlayerExtInfoEncoder] no encoder registered for PlayerUpdateMaskKey.$key — " +
                        "dropping from ext-info bitset to avoid cipher desync (B4 follow-up)."
                )
            }
            has
        }

        // APPEARANCE synth: include the cached appearance automatically if APPEARANCE is not already
        // pending. Matches the world-login bootstrap — on the first per-tick build the client must
        // receive the avatar's appearance even if game logic hasn't yet queued it.
        val effective = ArrayList(entries)
        val appearanceKey = ActiveMaskKeys.playerAppearance
        if (appearanceKey != null) {
            val hasAppearancePending = effective.any { it.first === appearanceKey }
            if (!hasAppearancePending && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) {
                val cached = target.appearance.cachedBytes
                if (cached != null) {
                    // Synthesise an in-flight Appearance mask. We don't mutate PendingUpdates so the
                    // next tick won't re-emit unless game logic explicitly queues another one.
                    effective.add(appearanceKey to UpdateMask.Appearance(cached))
                }
            }
        }

        if (effective.isEmpty()) {
            // Empty flag bitset = single 0x00 byte. No blocks follow. This is technically wasted work
            // (the player was flagged hasExtInfo but produced nothing); guarded against in the bit-pass
            // by checking [hasFlaggableExtendedInfo]. Defensive.
            extOut.writeByte(0)
            return extOut.toArray()
        }

        // Compute the OR of all flags into a single int. Per §4C the bitset is LE.
        var flagBitset = 0
        for ((key, _) in effective) {
            flagBitset = flagBitset or key.flag
        }

        for (byte in UpdateMaskHeader.player(flagBitset, ActiveMaskKeys.playerExpansionBits)) {
            extOut.writeByte(byte.toInt() and 0xFF)
        }

        // Per-flag blocks in fixed processing order (ascending by PlayerUpdateMaskKey.order).
        for ((key, mask) in effective.sortedBy { it.first.order }) {
            PlayerUpdateMaskEncoder.encode(extOut, key, mask)
        }

        return extOut.toArray()
    }

    /**
     * Returns true if [target] has any update worth transmitting to [viewer] this tick — either a
     * non-empty pending-updates mask OR a cached appearance THIS VIEWER's client hasn't seen yet.
     * Drives the `hasUpdate` bit in the bit-packed passes.
     *
     * First-appearance state is keyed on the VIEWER's viewport ([Viewport.cachedApprHashes] indexed
     * by the target's slot): each viewer's client tracks appearances independently, so keying on the
     * target's own viewport would mean only one viewer ever received the first APPEARANCE block.
     *
     * **Side effect (preserved):** the first time a viewer sees the target's appearance this records
     * it into `viewer.viewport.cachedApprHashes[target.index]`, so the next tick returns false.
     *
     * Relocated unchanged from the old monolithic builder's update gate — behavior is identical.
     */
    fun needsAnyUpdate(viewer: Player, target: Player): Boolean {
        if (target.pendingUpdates.hasPlayerUpdates()) return true
        // First-tick appearance synth: if the target has cachedBytes but this VIEWER's viewport has
        // no recorded appearance for the target's slot, the target needs an update so the viewer's
        // client gets the APPEARANCE block.
        val cached = target.appearance.cachedBytes ?: return false
        val viewerHashes = viewer.viewport.cachedApprHashes
        if (target.index !in viewerHashes.indices) return false
        if (viewerHashes[target.index] == null) {
            viewerHashes[target.index] = cached
            return true
        }
        return false
    }

    /**
     * Returns true if any of [target]'s pending masks has a registered encoder, OR the synthesised
     * APPEARANCE block would emit non-empty bytes. Used to gate the `hasExtendedInfo` bit in
     * [PlayerMovementEncoder.encodeHighResPosition] / [PlayerMovementEncoder.encodeAbsoluteTile].
     *
     * Relocated unchanged from the old monolithic builder's ext-info gate — behavior is identical.
     */
    fun hasFlaggableExtendedInfo(target: Player): Boolean {
        val entries = target.pendingUpdates.playerMaskEntries()
        if (entries.any { PlayerUpdateMaskEncoder.hasEncoder(it.first) }) return true
        // Synthesised APPEARANCE — same logic as encodeExtendedInfoBlock's synth fast-path.
        val appearanceKey = ActiveMaskKeys.playerAppearance ?: return false
        val cached = target.appearance.cachedBytes
        return cached != null && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)
    }
}
