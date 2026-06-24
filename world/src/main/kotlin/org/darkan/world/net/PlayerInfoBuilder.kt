package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskKey
import org.darkan.core.net.prot.update.UpdateMask
import org.darkan.core.net.prot.update.UpdateMaskHeader
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Per-tick builder that produces a [PlayerInfo] DTO from world state, per
 * `docs/net/serverprot/player-info-947-3.md` §4A (4-pass structure) and §4B (position
 * decoders) and §4C (extended-info flag bitset + per-block layouts).
 *
 * Two entry points:
 *  * [buildInit] — first-tick form, called once per session after `RebuildNormalSimple`.
 *    Emits the local player's 30-bit packed tile + 2047 18-bit region-hash placeholders
 *    for every other slot, with an immediate APPEARANCE ext-info block for the local
 *    player so the client renders the avatar at scene load.
 *  * [build] — per-tick incremental form. Walks the four cohorts (high-res-active,
 *    high-res-inactive, low-res-active, low-res-inactive) and emits the bit-packed
 *    update / skip-count structure documented in A4 §4A, followed by per-player ext-info
 *    blocks for any player flagged hasExtendedInfo.
 *
 * Encoder registration check: ext-info bytes go through [PlayerUpdateMaskEncoder]. Only
 * mask bits with a registered encoder are flagged in the bitset — flagging without an
 * encoder would leave the client trying to consume bytes the server never emitted. Unregistered
 * keys are dropped with a WARN-once log.
 */
object PlayerInfoBuilder {

    /** Initial per-tick bit-packed body capacity — generous default; grows naturally via ByteBuffer reallocation if needed. */
    private const val TICK_BUFFER_CAPACITY = 8192

    /** Reduced log-spam: report each missing mask-encoder key only once per JVM lifetime. */
    private val warnedMissingPlayerEncoders: MutableSet<PlayerUpdateMaskKey> =
        java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap())

    /**
     * First-tick GPI init form — **generated from local state**, Shape A per
     * `docs/protocol/world-bootstrap-948.md` §0/§4.3/§5. This is the ONLY GPI on the first tick
     * (op81 ships no prefix), so it must be a coherent standalone `ProcessPlayerInfo @0x001618a0`
     * init whose local 30-bit tile equals the op81 coord-header centre zone (the same spawn tile).
     *
     * The bit block is the same four-pass structure the per-tick [build] emits
     * (high-res-active → high-res-inactive → low-res-active → low-res-inactive over the viewport's
     * cohorts), with one difference: the local player (high-res slot 0 == `playerIndex`) is routed
     * through the **absolute-tile / teleport** high-res path instead of the per-tick movementType-0
     * path. That path (§4.3 + `GetHighResolutionPlayerPosition @0x00154d30`) is:
     *   gBit(1)=1   hasUpdate
     *   gBit(1)     hasExtendedInfo
     *   gBit(2)=3   movementType = 3 (teleport / jump → read absolute tile next)
     *   gBit(30)    absolute tile = `(plane<<28) | (x<<14) | y`  (== [Tile.id])
     *
     * On a solo first-light the viewport has ONLY the local player in `highResIndices` and an empty
     * `lowResIndices` (see [Viewport]); the other three passes are empty loops, which is exactly the
     * "empty other-slots / no other players visible" init §4.3 blesses. No 2047-slot region array is
     * written (the doc §1.3/§8 explicitly warns the prefix is NOT a flat 2047×18-bit array — and
     * Shape A's standalone init sidesteps that bit layout entirely while no other players exist).
     *
     * **APPEARANCE ext-info:** emitted (with its 2-byte length prefix, added by the op22 codec) ONLY
     * when the local player has a real pre-built appearance blob ([Appearance.cachedBytes]). The
     * appearance PAYLOAD byte format (what goes inside [UpdateMask.Appearance.data], read by the
     * client's `QueueExtendedInfoPacket`) is NOT documented and is cache-coupled, so we never
     * fabricate it — if there is no real blob we set `hasExtendedInfo=0` for the local player and the
     * avatar renders with a placeholder appearance. Per §0 the appearance is NOT the quit driver
     * (coordinate coherence + single-GPI is); a placeholder avatar keeps the client alive. When a
     * real appearance builder lands upstream, populating `cachedBytes` automatically lights up the
     * block here with no further change.
     *
     * `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        viewport.firstTick = false

        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // Players flagged hasExtendedInfo this tick, in cohort processing order. The local player
        // is appended first when its appearance block is available.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Pass 1: HIGH-RES ACTIVE — the local player (slot 0) takes the absolute-tile init path;
        // any other high-res actives (none on first light) fall back to the per-tick high-res path.
        encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo)
        // Pass 2: HIGH-RES INACTIVE.
        encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo)
        // Pass 3 & 4: LOW-RES (empty on first light — no other players in viewport yet).
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo)
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo)

        bitOut.stopBitAccess()

        // Record the appearance we sent so the per-tick path won't re-emit it next tick.
        val cached = player.appearance.cachedBytes
        if (cached != null && player.index in viewport.cachedApprHashes.indices) {
            viewport.cachedApprHashes[player.index] = cached
        }

        // Build ext-info blocks per flagged player (currently only the local player's APPEARANCE).
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val target = Players.get(slot) ?: continue
            extendedInfo.add(encodeExtendedInfoBlock(target))
        }

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = extendedInfo,
            firstTick = true,
        )
    }

    /**
     * High-res pass for the INIT form. Identical cohort filtering to [encodeHighResPass], but the
     * local player (the viewport owner) is encoded via the absolute-tile teleport path
     * [encodeLocalPlayerInit] so the client places the avatar at its real spawn tile. Any other
     * high-res player (not present on first light) uses the standard per-tick high-res encoder.
     */
    private fun encodeHighResInitPass(
        out: BufferWriter,
        viewer: Player,
        indices: List<Int>,
        activeFilter: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        for (slot in indices) {
            val target = Players.get(slot) ?: continue
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            if (target.index == viewer.index) {
                encodeLocalPlayerInit(out, target, flaggedForExtInfo)
            } else {
                val needsUpdate = needsAnyUpdate(viewer, target)
                if (needsUpdate) {
                    out.writeBits(1, 1)
                    encodeHighResPosition(out, target, flaggedForExtInfo)
                } else {
                    out.writeBits(1, 0)
                    out.writeBits(2, 0)
                }
            }
        }
    }

    /**
     * Local-player first-transmission high-res init (absolute-tile / teleport path), per
     * `docs/protocol/world-bootstrap-948.md` §4.3 and `GetHighResolutionPlayerPosition`:
     *   gBit(1)=1 hasUpdate ; gBit(1) hasExtInfo ; gBit(2)=3 movementType(teleport) ; gBit(30) tile.
     *
     * The 30-bit tile is `(plane<<28)|(x<<14)|y` ([Tile.id]) — this MUST equal the op81 coord-header
     * centre zone's tile (the coherence constraint that stops the quit). hasExtInfo is set only when
     * a real appearance blob exists (we do not fabricate the undocumented appearance payload).
     */
    private fun encodeLocalPlayerInit(
        out: BufferWriter,
        local: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = hasFlaggableExtendedInfo(local)
        out.writeBits(1, 1)                       // hasUpdate
        out.writeBits(1, if (hasExtInfo) 1 else 0) // hasExtendedInfo
        out.writeBits(2, 3)                       // movementType = 3 (teleport → absolute tile)
        out.writeBits(30, local.tile.id)          // absolute tile (plane<<28)|(x<<14)|y
        if (hasExtInfo) {
            flaggedForExtInfo.add(local.index)
        }
    }

    fun buildIfNeeded(player: Player): PlayerInfo? {
        if (player.viewport.firstTick) return buildInit(player)
        return if (hasTickUpdate(player)) build(player) else null
    }

    /**
     * Per-tick incremental form. Per A4 §"4A — Pass Structure" the bit-packed body has four
     * passes, filtering each list by the `+0x27` (active) flag:
     *  1. HIGH-RES ACTIVE — `viewport.highResIndices` where `active == true`.
     *  2. HIGH-RES INACTIVE — `viewport.highResIndices` where `active == false`.
     *  3. LOW-RES ACTIVE — `viewport.lowResIndices` where `active == true`.
     *  4. LOW-RES INACTIVE — `viewport.lowResIndices` where `active == false`.
     *
     * For MVP, the local player is the ONLY high-res entry and starts the first per-tick
     * as `active=true`; pass 1 emits hasUpdate=1, movementType=0, hasExtendedInfo=1 with
     * the APPEARANCE block so the client renders the avatar.
     */
    fun build(player: Player): PlayerInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // Tracks player indices flagged hasExtendedInfo this tick, in cohort processing order.
        // Per A4 §"Extended-info dispatch" the ext-info blocks are emitted in the order the
        // hasExtendedInfo bits fired.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Pass 1: HIGH-RES ACTIVE — players where active == true.
        encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo)
        // Pass 2: HIGH-RES INACTIVE — players where active == false.
        encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo)
        // Pass 3: LOW-RES ACTIVE — players where active == true.
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo)
        // Pass 4: LOW-RES INACTIVE — players where active == false.
        encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo)

        bitOut.stopBitAccess()

        // Build ext-info blocks per flagged player.
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val target = Players.get(slot) ?: continue
            val block = encodeExtendedInfoBlock(target)
            extendedInfo.add(block)
        }

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = extendedInfo,
            firstTick = false,
        )
    }

    /**
     * Encode one high-res pass (active or stationary filter). Per A4 §"Per-pass loop body":
     * for each player in [indices] matching the filter:
     *  * 1-bit `hasUpdate`.
     *  * If `hasUpdate==1`: write high-resolution position bits per A4 §4B
     *    `GetHighResolutionPlayerPosition`.
     *  * Else: write 2-bit skip-mode + 0..11-bit skip-count for the run-length encoding of
     *    consecutive players with no updates. (We always emit `skipMode=0` for MVP — i.e.,
     *    no skip — because every iteration produces an explicit hasUpdate bit.)
     */
    private fun encodeHighResPass(
        out: BufferWriter,
        viewer: Player,
        indices: List<Int>,
        activeFilter: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        for (slot in indices) {
            val target = Players.get(slot) ?: continue
            // activeFilter mirrors A4 §4A's `+0x27 == 0` (active) vs `!= 0` (inactive) cohort.
            // Player.active is the modern-named mirror of `+0x27`.
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            val needsUpdate = needsAnyUpdate(viewer, target)
            if (needsUpdate) {
                out.writeBits(1, 1)
                encodeHighResPosition(out, target, flaggedForExtInfo)
            } else {
                // No update for this slot. Per A4 §4A passes 2/3/4 use ReadStationary —
                // 2-bit mode then variable-width skip count. We emit mode=0 (no skip) so the
                // next iteration reads its own hasUpdate bit. This is the conservative emit-
                // every-player path; an optimisation would batch consecutive no-update slots
                // via mode 1/2/3 skip counts. For MVP correctness > compactness.
                out.writeBits(1, 0)
                out.writeBits(2, 0)  // skipMode=0: no skip, continue with next slot.
            }
        }
    }

    /**
     * Encode one low-res pass (active or stationary filter). Per A4 §"Per-pass loop body":
     * mirrors the high-res pass but calls into `GetLowResolutionPlayerPosition` (A4 §4B) when
     * `hasUpdate==1`.
     *
     * For MVP the low-res list is empty (no other players in viewport), so this is a no-op
     * loop. The structure is in place so it stays correct when other players appear.
     */
    private fun encodeLowResPass(
        out: BufferWriter,
        viewer: Player,
        indices: List<Int>,
        activeFilter: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        for (slot in indices) {
            val target = Players.get(slot) ?: continue
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            val needsUpdate = needsAnyUpdate(viewer, target)
            if (needsUpdate) {
                out.writeBits(1, 1)
                encodeLowResPosition(out, target, flaggedForExtInfo)
            } else {
                out.writeBits(1, 0)
                out.writeBits(2, 0)
            }
        }
    }

    /**
     * Per A4 §4B `GetHighResolutionPlayerPosition`:
     * ```
     * 1 bit:  hasExtendedInfo
     * 2 bits: movementType  (0 = none, 1 = walk, 2 = run, 3 = teleport)
     * ```
     * For MVP every high-res update is `movementType=0` (no movement). When `movementType=0`
     * and `hasExtendedInfo=1` no further movement bits are emitted; the player just gets new
     * extended-info. When `movementType=0` and `hasExtendedInfo=0` we'd write a 1-bit
     * `demoteToLowRes` flag; since we never demote in MVP we leave that path unreachable
     * (any player that reaches here MUST have hasExtendedInfo set — guarded by [needsAnyUpdate]).
     */
    private fun encodeHighResPosition(
        out: BufferWriter,
        target: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = hasFlaggableExtendedInfo(target)
        out.writeBits(1, if (hasExtInfo) 1 else 0)
        out.writeBits(2, 0)  // movementType 0 — no movement (MVP).
        if (!hasExtInfo) {
            // movementType=0 && hasExtInfo=0: per A4 §4B write 1-bit demoteToLowRes=0.
            out.writeBits(1, 0)
        } else {
            flaggedForExtInfo.add(target.index)
        }
    }

    /**
     * Per A4 §4B `GetLowResolutionPlayerPosition`:
     * ```
     * 2 bits: updateType  (0 = promote to high-res, 1 = level change, 2 = small chunk, 3 = large multi-chunk)
     * ```
     * For MVP no low-res transitions happen; emit `updateType=1` (level change with delta 0)
     * as a safe no-op. This path is currently unreachable because the low-res list is empty
     * but is wired in for forward compatibility.
     */
    private fun encodeLowResPosition(
        out: BufferWriter,
        target: Player,
        @Suppress("UNUSED_PARAMETER") flaggedForExtInfo: MutableList<Int>,
    ) {
        out.writeBits(2, 1)   // updateType=1 (level change only)
        out.writeBits(2, 0)   // levelDelta=0
    }

    /**
     * Encode the extended-info byte block for one player per A4 §4C.
     *
     * Wire layout:
     *  * 1..4 byte expansion-driven flag bitset (LE). Expansion bits at byte 0 bit 0
     *    (mask 0x01), byte 1 bit 6 (mask 0x40), byte 2 bit 2 (mask 0x04).
     *  * Per-flag block in fixed processing order (ascending [PlayerUpdateMaskKey.order]).
     *    Encoders dispatched via [PlayerUpdateMaskEncoder] (registered in
     *    `Rev947ServerCodecsUpdateMasks.kt`).
     *
     * For the local player's first-tick render we synthesise an APPEARANCE block from
     * [Player.appearance.cachedBytes] (built once at Player creation) and include it in the
     * pending-updates map under [PlayerUpdateMaskKey.APPEARANCE]. The caller is responsible
     * for setting this up; here we simply encode whatever is in [PendingUpdates].
     */
    private fun encodeExtendedInfoBlock(target: Player): ByteArray {
        val extOut = BufferWriter(256)

        // Filter pending masks down to those with a registered encoder. Per B4 the unregistered
        // ones MUST NOT be flagged in the bitset — doing so would desync the cipher counter.
        val entries = target.pendingUpdates.playerMaskEntries().filter { (key, _) ->
            val has = PlayerUpdateMaskEncoder.hasEncoder(key)
            if (!has && warnedMissingPlayerEncoders.add(key)) {
                System.err.println(
                    "[PlayerInfoBuilder] no encoder registered for PlayerUpdateMaskKey.$key — " +
                        "dropping from ext-info bitset to avoid cipher desync (B4 follow-up)."
                )
            }
            has
        }

        // Special MVP case: include APPEARANCE block automatically if we have a cached appearance
        // and APPEARANCE is not already in the pending masks. This matches the world-login
        // bootstrap: on the first per-tick build we need the client to receive the avatar's
        // appearance even if the world-side game logic hasn't yet queued it.
        //
        // The APPEARANCE key is revision-specific (different bit positions in 947 vs 948), so
        // we look it up via [ActiveMaskKeys.playerAppearance] which the active codec registers
        // at startup. If null (no codec registered or revision doesn't model APPEARANCE), we
        // skip the synth and rely on explicit `setPlayer(rev.APPEARANCE, ...)` from game logic.
        val effective = ArrayList(entries)
        val appearanceKey = ActiveMaskKeys.playerAppearance
        if (appearanceKey != null) {
            val hasAppearancePending = effective.any { it.first === appearanceKey }
            if (!hasAppearancePending && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)) {
                val cached = target.appearance.cachedBytes
                if (cached != null) {
                    // Synthesise an in-flight Appearance mask. We don't mutate the PendingUpdates
                    // so the next tick won't re-emit unless game logic explicitly queues another one.
                    effective.add(
                        appearanceKey to
                            UpdateMask.Appearance(cached)
                    )
                }
            }
        }

        if (effective.isEmpty()) {
            // Empty flag bitset = single 0x00 byte. No blocks follow. This is technically
            // wasted work (the player was flagged hasExtInfo but produced nothing); guard
            // against it in the bit-pass by checking hasFlaggableExtendedInfo. Defensive.
            extOut.writeByte(0)
            return extOut.toArray()
        }

        // Compute the OR of all flags into a single int. Per A4 §4C the bitset is LE.
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
     * Returns true if [target] has any update worth transmitting to [viewer] this tick —
     * either a non-empty pending-updates mask OR a cached appearance THIS VIEWER's client
     * hasn't seen yet. This drives the `hasUpdate` bit in the bit-packed passes.
     *
     * First-appearance state is keyed on the VIEWER's viewport ([Viewport.cachedApprHashes]
     * indexed by the target's slot): each viewer's client tracks appearances independently,
     * so keying on the target's own viewport would mean only one viewer ever received the
     * first APPEARANCE block.
     *
     * For MVP we treat the local player's first per-tick as "always has update" because
     * the synthesised APPEARANCE block needs to land before the client renders the avatar.
     */
    private fun needsAnyUpdate(viewer: Player, target: Player): Boolean {
        if (target.pendingUpdates.hasPlayerUpdates()) return true
        // First-tick appearance synth: if the target has cachedBytes but this VIEWER's
        // viewport has no recorded appearance for the target's slot, the target needs an
        // update so the viewer's client gets the APPEARANCE block.
        val cached = target.appearance.cachedBytes ?: return false
        val viewerHashes = viewer.viewport.cachedApprHashes
        if (target.index !in viewerHashes.indices) return false
        if (viewerHashes[target.index] == null) {
            viewerHashes[target.index] = cached
            return true
        }
        return false
    }

    private fun hasTickUpdate(viewer: Player): Boolean {
        val viewport = viewer.viewport
        for (slot in viewport.highResIndices) {
            val target = Players.get(slot) ?: continue
            if (hasQueuedUpdateOrUndeliveredAppearance(viewer, target)) return true
        }
        for (slot in viewport.lowResIndices) {
            val target = Players.get(slot) ?: continue
            if (hasQueuedUpdateOrUndeliveredAppearance(viewer, target)) return true
        }
        return false
    }

    private fun hasQueuedUpdateOrUndeliveredAppearance(viewer: Player, target: Player): Boolean {
        if (target.pendingUpdates.hasPlayerUpdates()) return true
        val cached = target.appearance.cachedBytes ?: return false
        val viewerHashes = viewer.viewport.cachedApprHashes
        return target.index in viewerHashes.indices && viewerHashes[target.index] == null
    }

    /**
     * Returns true if any of [target]'s pending masks has a registered encoder, OR the
     * synthesised APPEARANCE block would emit non-empty bytes. Used to gate the
     * `hasExtendedInfo` bit in [encodeHighResPosition].
     */
    private fun hasFlaggableExtendedInfo(target: Player): Boolean {
        val entries = target.pendingUpdates.playerMaskEntries()
        if (entries.any { PlayerUpdateMaskEncoder.hasEncoder(it.first) }) return true
        // Synthesised APPEARANCE — same logic as encodeExtendedInfoBlock's MVP fast-path.
        val appearanceKey = ActiveMaskKeys.playerAppearance ?: return false
        val cached = target.appearance.cachedBytes
        return cached != null && PlayerUpdateMaskEncoder.hasEncoder(appearanceKey)
    }
}
