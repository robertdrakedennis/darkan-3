package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskKey
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

    /** Maximum bit-packed body size for buildInit: 30 bits + 2047 × 18 bits = 36876 bits ≈ 4610 bytes. */
    private const val INIT_BUFFER_CAPACITY = 5200

    /** Initial per-tick bit-packed body capacity — generous default; grows naturally via ByteBuffer reallocation if needed. */
    private const val TICK_BUFFER_CAPACITY = 8192

    /** Reduced log-spam: report each missing mask-encoder key only once per JVM lifetime. */
    private val warnedMissingPlayerEncoders: MutableSet<PlayerUpdateMaskKey> =
        java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap())

    /**
     * First-tick init form. Per A4 §"4A — Pass Structure" (with cross-protocol convention from
     * OSRS / 919-era init payloads): the body is purely bit-packed, no ext-info; the bit layout
     * is:
     *  * 30 bits — local player tile, encoded as `(level << 28) | (x << 14) | y` (matches
     *    [Tile.id] / [Tile.toInt]).
     *  * 2047 × 18 bits — region hashes for player slots 1..2047, encoded as
     *    `(plane << 16) | (regionX << 8) | regionY` (matches [Tile.getRegionHash]). Slot 0 is
     *    not transmitted (reserved sentinel).
     *
     * After buildInit completes:
     *  * Local player's APPEARANCE ext-info IS emitted via [build] on the NEXT tick (not
     *    here) — the first-tick payload carries positions only and the client will render
     *    placeholder appearances until the first per-tick APPEARANCE block lands.
     *  * `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(INIT_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // Local player slot — 30-bit packed tile id (level, x, y).
        // Decomposition per A4 §4B's 30-bit teleport raw layout: `(level << 28) | (x << 14) | y`.
        // We split into two writeBits calls because writeBits takes Int (≤32 bits) and we need
        // to ensure the upper 2 bits are placed in the MSB position of the 30-bit field.
        val tile = player.tile
        val packed30 = ((tile.level and 0x3) shl 28) or ((tile.x and 0x3FFF) shl 14) or (tile.y and 0x3FFF)
        bitOut.writeBits(30, packed30)

        // Slots 1..2047 — 18-bit region hash for every OTHER player. For slot == local player's
        // index we still emit a hash (consistent with the protocol — the client maps the local
        // slot to its actual entity separately via the 30-bit prefix). For all other slots we
        // emit the latest known position if a player exists; otherwise 0.
        for (slot in 1..2047) {
            if (slot == player.index) {
                // Local player's region (same hash form, used for low-res list rebuild on client).
                bitOut.writeBits(18, tile.getRegionHash() and 0x3FFFF)
            } else {
                val other = Players.get(slot)
                val hash = other?.tile?.getRegionHash() ?: 0
                bitOut.writeBits(18, hash and 0x3FFFF)
            }
        }

        bitOut.stopBitAccess()
        viewport.firstTick = false

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = emptyList(),
            firstTick = true,
        )
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
                            org.darkan.core.net.prot.update.UpdateMask.Appearance(cached)
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

        // Determine how many bytes the bitset needs. The expansion-driven layout is:
        //  byte 0 always; if (byte0 & 0x01) byte 1; if (byte1 & 0x40) byte 2; if (byte2 & 0x04) byte 3.
        // We compute the natural byte length from the highest set bit, then set expansion bits
        // in the LOWER bytes accordingly.
        val highestBit = 31 - Integer.numberOfLeadingZeros(flagBitset)
        val byteCount = when {
            highestBit < 8 -> 1
            highestBit < 16 -> 2
            highestBit < 24 -> 3
            else -> 4
        }
        // Set expansion ("continue") bits. These are revision-dependent (947-3 = {0,14,18};
        // 948 = {0,13,22}) so they are driven from the active codec's published positions
        // (ActiveMaskKeys.playerExpansionBits) rather than hardcoded literals — see
        // Rev948PlayerUpdateMaskKey.EXPANSION_BITS / docs/net/serverprot/948-research-C-*.md.
        // EXPANSION_BITS[N] is the continue-bit in byte N that tells the client to read byte N+1.
        val expansionBits = ActiveMaskKeys.playerExpansionBits
        for (byte in 1 until byteCount) {
            flagBitset = flagBitset or (1 shl expansionBits[byte - 1])
        }

        // Write LSB-first.
        for (i in 0 until byteCount) {
            extOut.writeByte((flagBitset ushr (i * 8)) and 0xFF)
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
