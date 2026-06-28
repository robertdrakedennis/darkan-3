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
     * Runs one GPI pass inside its OWN bit-access window so the pass's bits are byte-aligned. The NXT
     * client (`S2C_PLAYER_INFO_OP22 @0x100043500`) rounds the player-info bit cursor UP to the next byte
     * (`ceil(bp/8)×8`) at every one of the 4 pass boundaries (RE-verified instruction-level
     * @0x10004363f / 0x100043732 / 0x100043854 / 0x100043938), so each pass MUST begin on a byte boundary.
     * Packing more than one pass into a byte (the old `c7 ff 40`) made the client read the local entry's
     * bits, round up, DISCARD that byte's trailing bits — where our skip-run began — and resume the next
     * pass mid-skip-run → bit-cursor desync → the ext-info drain reads the appearance at the wrong byte →
     * no `PlayerAppearancePending` constructed → no avatar model. With a zeroed BufferWriter the pad bits
     * are 0 (the client discards them anyway). Live-verified: solo-spawn bit-block `c7 ff 40` → `c0 7f f4`.
     */
    private inline fun byteAlignPass(out: BufferWriter, pass: () -> Unit) {
        out.startBitAccess()
        pass()
        out.stopBitAccess()
    }

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
     * **APPEARANCE ext-info:** emitted (with its 2-byte length prefix, added by the op22 codec) from
     * the local player's real pre-built appearance blob ([Appearance.cachedBytes]). The appearance
     * PAYLOAD byte format is now byte-verified against the binary
     * (`re-resources/docs/net/serverprot/player-appearance-948.md`) and built by
     * [org.darkan.world.entity.Appearance] from gender + default identitykits + worn equipment. The
     * blob is always present (the Player ctor builds it), so the local player always carries a real
     * avatar at scene load.
     *
     * `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        viewport.firstTick = false

        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        // Players flagged hasExtendedInfo this tick, in cohort processing order. The local player
        // is appended first when its appearance block is available.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // BYTE-ALIGN each pass — the client byte-aligns the player-info bit cursor at every pass boundary
        // (see [byteAlignPass]). Pass 1: HIGH-RES ACTIVE — the local player (slot 0) takes the absolute-tile
        // init path; any other high-res actives (none on first light) fall back to the per-tick high-res path.
        byteAlignPass(bitOut) { encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo) }
        // Pass 2: HIGH-RES INACTIVE.
        byteAlignPass(bitOut) { encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo) }
        // Pass 3 & 4: LOW-RES (empty on first light — no other players in viewport yet).
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo) }

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

    /**
     * Per-tick PLAYER_INFO emitter for the world tick loop. Returns the op22 to send THIS tick.
     *
     * **Always emits (never null) once past the first tick — the "idle loop" prod sends.** This is
     * the BUG-1 fix: the NXT client's local-player avatar is a smoothing/interpolating
     * `PathingEntity` whose per-frame position integrator is advanced+committed by the per-tick
     * `ProcessPlayerInfo` (the op22 handler `S2C_PLAYER_INFO_OP22 @0x100043500` → its high-res pass
     * over the local slot). With NO op22 arriving each tick the integrator runs **open-loop** and
     * the avatar's `graphNode` scene-fine position creeps (capture
     * `session-20260627-231803-41562-local`: a stationary spawn drifts ~0.12 tiles/tick south,
     * x held — y 3216→3181 over the session). Production sends an op22 EVERY game tick
     * (`docs/kb/services/world-stream-service.md`: "rebuild/zone load before PlayerInfo/NpcInfo idle
     * loop"; 13 op22 in `…-production` vs our 1) — the steady-state form being the **stationary
     * hold** `[hasUpdate=0][skip-run]` (decoded from prod: `hasUpdate=0, skipMode=1`). That per-tick
     * decode re-commits the avatar each tick and pins it to its tile.
     *
     * So when nothing changed we still emit [build]'s stationary-hold body (local player
     * `hasUpdate=0, skipMode=0`, then the low-res cohort skip-run) instead of returning null. The
     * op81 GPI prefix already placed the local 30-bit tile == `player.tile` (verified coherent with
     * the op81 coord-header centre zone); this idle loop keeps the avatar THERE.
     *
     * The first tick is still routed through [buildInit] for the un-suppressed `firstTick` path
     * (world entry suppresses it via [buildWorldEntrySync], which sets `firstTick=false`).
     */
    fun buildIfNeeded(player: Player): PlayerInfo = when {
        player.viewport.firstTick -> buildInit(player)
        else -> build(player)
    }

    fun buildWorldEntrySync(player: Player): PlayerInfo {
        // NOTE (2026-06-28): a movementType=3 TELEPORT delivery of the local appearance was tried and
        // REVERTED. The LOCAL player is architecturally excluded from the op22 ext-info APPEARANCE path
        // (RE: DecodeGpiPrefix excludes the local slot from the high-res ext-info walk list;
        // DecodeKnownPlayerUpdate@0x100025640 idle-returns the local slot), so the appearance never decoded
        // (current_appearance/avatar+0x12A0 stayed 0x0) AND the teleport's longer bit-block + the unconsumed
        // local ext-info block regressed the render plane (0→3, avatar floated above the ground). The
        // prod-accurate fix is to deliver the local appearance INLINE in the GPI add (slotObj+0x48 →
        // DecodeExternalPlayerUpdate@0x1000266e0 → immediate compose) — pending RE of the exact wire format
        // (op22 idle ext-info is kept here only as the prior baseline). See memory: avatar-render-next-steps.
        player.appearance.ensureCachedBytes()
        player.viewport.firstTick = false
        return build(player)
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
        // Tracks player indices flagged hasExtendedInfo this tick, in cohort processing order.
        // Per A4 §"Extended-info dispatch" the ext-info blocks are emitted in the order the
        // hasExtendedInfo bits fired.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // BYTE-ALIGN each of the 4 passes: the NXT client rounds the player-info bit cursor up to a byte at
        // EVERY pass boundary (see [byteAlignPass]). Packing >1 pass into a byte (the old `c7 ff 40`) made the
        // client discard the local entry's trailing byte-bits — where our skip-run began — and resume the
        // next pass mid-skip-run → bit desync → the ext-info drain read the appearance at the wrong byte → no
        // PlayerAppearancePending → no avatar model. RE-verified; solo-spawn bit-block is now `c0 7f f4`.
        // Pass 1: HIGH-RES ACTIVE — players where active == true.
        byteAlignPass(bitOut) { encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo) }
        // Pass 2: HIGH-RES INACTIVE — players where active == false.
        byteAlignPass(bitOut) { encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo) }
        // Pass 3: LOW-RES ACTIVE — players where active == true.
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo) }
        // Pass 4: LOW-RES INACTIVE — players where active == false.
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo) }

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
        var skipRun = 0
        for (slot in indices) {
            val target = Players.get(slot)
            val matches = target?.let { if (activeFilter) it.active else !it.active } ?: activeFilter
            if (!matches) continue

            val needsUpdate = target != null && needsAnyUpdate(viewer, target)
            if (needsUpdate) {
                writeStationarySkipRun(out, skipRun)
                skipRun = 0
                out.writeBits(1, 1)
                encodeLowResPosition(out, target, flaggedForExtInfo)
            } else {
                skipRun++
            }
        }
        writeStationarySkipRun(out, skipRun)
    }

    private fun writeStationarySkipRun(out: BufferWriter, count: Int) {
        var remaining = count
        while (remaining > 0) {
            val following = minOf(remaining - 1, 2047)
            out.writeBits(1, 0)
            writeStationarySkipCount(out, following)
            remaining -= following + 1
        }
    }

    private fun writeStationarySkipCount(out: BufferWriter, count: Int) {
        when {
            count == 0 -> out.writeBits(2, 0)
            count < 32 -> {
                out.writeBits(2, 1)
                out.writeBits(5, count)
            }
            count < 256 -> {
                out.writeBits(2, 2)
                out.writeBits(8, count)
            }
            else -> {
                out.writeBits(2, 3)
                out.writeBits(11, count)
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
     *    `Rev948ServerCodecsUpdateMasks.kt`).
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
