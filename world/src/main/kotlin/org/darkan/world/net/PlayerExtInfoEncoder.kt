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
 * flag-bitset header + the per-block payloads + the local-player APPEARANCE first-delivery fast-path —
 * and answer the two gating questions the bit passes ask: [needsAnyUpdate] (drives `hasUpdate`) and
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
 *
 * ## Movement is POSITION-ONLY (2026-06-29) — the walk + stop are 100% client-driven
 *
 * An ordinary walk/run step carries **NO** movement ext-info — no bit-0x20 MOVEMENT_ANIM, no bit-0x80
 * FORCED_MOVEMENT, no stop block. The step is the GPI `mvt=1` position update + dir alone; the client
 * self-animates AND self-smooths from the position stream. **Prod ground-truth (live-Jagex capture +
 * per-frame anim-trace + decoded wire):** during a walk the client's `render_anim` is the bas WALK seq
 * (18020) with the route-anim queue EMPTY and the lerp window NEVER open (the body glides via the
 * client's own smoother); at the stop `render_anim` flips to idle (18019) the EXACT frame the body
 * stops — frame-perfect, with no server signal. On the wire every moving step is a bare position update
 * with NO ext-info; bit-0x20 MOVEMENT_ANIM appears ONLY on STILL ticks (idle fidgets/emotes), never
 * while moving.
 *
 * Why the server must NOT send bit-0x20 for a walk: pushing the route-anim queue (bit-0x20 →
 * `SetMovementAnimSet`) switches the client OUT of its self-driven `render_anim` into server-anim mode,
 * whose per-frame `SelectMovementAnimation` then plays IDLE while the avatar moves (our local capture
 * confirmed: queue populated → render_anim idle while moving). An EMPTY queue lets the client
 * self-select `render_anim = walk` exactly like prod. See
 * `re-resources/docs/net/serverprot/player-appearance-948.md`.
 *
 * The only ext-info this object synthesises is the **first APPEARANCE delivery** per viewer (the
 * invisible-avatar fix), deduplicated against `viewer.viewport.cachedApprHashes` so it is sent exactly
 * once and never re-emitted on subsequent ticks.
 */
object PlayerExtInfoEncoder {

    /** Reduced log-spam: report each missing mask-encoder key only once per JVM lifetime. */
    private val warnedMissingPlayerEncoders: MutableSet<PlayerUpdateMaskKey> =
        Collections.newSetFromMap(ConcurrentHashMap())

    /**
     * Encode the extended-info byte block for one player as seen by [viewer], per §4C.
     *
     * Wire layout:
     *  * 1..4 byte expansion-driven flag bitset (LE). Expansion bits at byte 0 bit 0
     *    (mask 0x01), byte 1 bit 6 (mask 0x40), byte 2 bit 2 (mask 0x04).
     *  * Per-flag block in fixed processing order (ascending [PlayerUpdateMaskKey.order]).
     *
     * APPEARANCE first-delivery fast-path: synthesise an APPEARANCE block from
     * [Player.appearance.cachedBytes] ONLY when the appearance has not yet been delivered to THIS
     * viewer ([appearanceUndelivered]) and is not already an explicit pending mask. When the block is
     * emitted, the delivery is recorded into `viewer.viewport.cachedApprHashes[target.index]` so it is
     * never re-sent. This is the world-entry inline-appearance delivery ([PlayerInfoEncoder.buildInit]
     * / [PlayerInfoEncoder.buildWorldEntrySync]) and the avatar "no model" fix — preserved, but ONE-
     * SHOT (the prior fast-path re-added the cached appearance on EVERY flagged tick, which is why a
     * walking avatar re-sent its full appearance every step). The APPEARANCE key is revision-specific,
     * looked up via [ActiveMaskKeys.playerAppearance]; if null we rely on explicit
     * `setPlayer(rev.APPEARANCE, ...)` from game logic.
     *
     * NO movement synth: a walk/run step carries no ext-info (position-only — the client self-animates;
     * see the class KDoc). This method only emits real pending masks + the one-shot appearance.
     *
     * Consistency contract: this MUST agree per-tick with [hasFlaggableExtendedInfo] (which decides the
     * `hasExtendedInfo` bit). Both read the SAME `cachedApprHashes` state, and recording happens HERE
     * (after the block is built), so within one tick the flag and the block stay in sync. The
     * recording side-effect was deliberately removed from [needsAnyUpdate] so it cannot flip the
     * appearance-undelivered state before this runs.
     */
    fun encodeExtendedInfoBlock(viewer: Player, target: Player): ByteArray {
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

        // APPEARANCE first-delivery synth: include the cached appearance ONLY if it is not already a
        // pending mask AND it has not yet been delivered to this viewer. On emit, record the delivery so
        // it is never re-sent. (Was unconditional per-tick → re-sent the full appearance every walk
        // step; now exactly-once per viewer.)
        val effective = ArrayList(entries)
        val appearanceKey = ActiveMaskKeys.playerAppearance
        if (appearanceKey != null && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) {
            val hasAppearancePending = effective.any { it.first === appearanceKey }
            if (!hasAppearancePending && appearanceUndelivered(viewer, target)) {
                val cached = target.appearance.cachedBytes
                if (cached != null) {
                    effective.add(appearanceKey to UpdateMask.Appearance(cached))
                    recordAppearanceDelivered(viewer, target, cached)
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
     * **No recording side-effect (changed 2026-06-29):** this used to record the appearance hash here,
     * but that flipped the appearance-undelivered state BEFORE [encodeExtendedInfoBlock] /
     * [hasFlaggableExtendedInfo] ran, which (now that the appearance synth is gated on that state)
     * would mean the appearance is never actually emitted. Recording now happens exactly where the
     * block is built ([encodeExtendedInfoBlock]); this gate is a pure read.
     */
    fun needsAnyUpdate(viewer: Player, target: Player): Boolean {
        if (target.pendingUpdates.hasPlayerUpdates()) return true
        return appearanceUndelivered(viewer, target)
    }

    /**
     * Returns true if any of [target]'s pending masks has a registered encoder, OR the APPEARANCE block
     * still needs first delivery to [viewer]. Used to gate the `hasExtendedInfo` bit in
     * [PlayerMovementEncoder.encodeHighResPosition] / [PlayerMovementEncoder.encodeAbsoluteTile].
     *
     * NO movement branch (position-only): a plain walk/run step is NOT flagged for an ext-info block —
     * the step carries only the GPI position bits and the client self-animates (see the class KDoc). A
     * slot is flagged ONLY for a real pending mask or the one-shot appearance.
     */
    fun hasFlaggableExtendedInfo(viewer: Player, target: Player): Boolean {
        val entries = target.pendingUpdates.playerMaskEntries()
        if (entries.any { PlayerUpdateMaskEncoder.hasEncoder(it.first) }) return true
        // APPEARANCE first delivery — same per-viewer dedup as the encode fast-path. Only flags when the
        // appearance is genuinely undelivered to this viewer; after delivery this returns false.
        val appearanceKey = ActiveMaskKeys.playerAppearance ?: return false
        if (!PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) return false
        return appearanceUndelivered(viewer, target) && target.appearance.cachedBytes != null
    }

    /**
     * True iff [target]'s appearance has NOT yet been delivered to [viewer] (the per-viewer
     * `cachedApprHashes` slot for the target index is still null). Out-of-range indices are treated as
     * delivered (no block) so we never index past the array.
     */
    private fun appearanceUndelivered(viewer: Player, target: Player): Boolean {
        val hashes = viewer.viewport.cachedApprHashes
        if (target.index !in hashes.indices) return false
        return hashes[target.index] == null
    }

    /** Record that [target]'s appearance [cached] was delivered to [viewer] this tick (exactly-once gate). */
    private fun recordAppearanceDelivered(viewer: Player, target: Player, cached: ByteArray) {
        val hashes = viewer.viewport.cachedApprHashes
        if (target.index in hashes.indices) hashes[target.index] = cached
    }
}
