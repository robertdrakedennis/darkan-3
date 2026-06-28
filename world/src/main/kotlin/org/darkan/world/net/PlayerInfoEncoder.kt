package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Orchestrator for op22 PLAYER_INFO. Single responsibility: drive the **four byte-aligned GPI
 * passes** (high-res active, high-res inactive, low-res active, low-res inactive) over the viewer's
 * viewport cohorts and assemble the [PlayerInfo] DTO, delegating the actual per-target bytes to
 * [PlayerMovementEncoder] (position / movement bits) and [PlayerExtInfoEncoder] (the ext-info block
 * + the `hasUpdate` / `hasExtendedInfo` gating). Per `docs/net/serverprot/player-info-947-3.md`
 * §4A (pass structure), §4B (position decoders) and §4C (extended-info).
 *
 * Two public entry points map to the two live call sites:
 *  * [buildWorldEntrySync] — `WorldServer` world-entry. Marks the appearance cached + clears
 *    `firstTick`, then emits the per-tick stationary form.
 *  * [buildIfNeeded] — the `WorldTick` per-tick caller. Emits the first-tick init form ([buildInit])
 *    while `firstTick` is set, otherwise the per-tick stationary form.
 *
 * [buildInit] is public because it is the documented first-tick / teleport-init form (the local
 * player is routed through [PlayerMovementEncoder.encodeAbsoluteTile]); it is exercised on the
 * un-suppressed `firstTick` path and by `PlayerInfoEncoderInitTest`.
 *
 * The per-tick body is the SAME four-pass structure regardless of entry point; the only difference
 * is whether the local high-res slot takes the absolute-tile init path ([buildInit]) or the
 * stationary form ([buildPerTick]). No movement is produced — every high-res update is stationary
 * (`movementType=0`) and every absent slot folds into the low-res skip-run.
 */
object PlayerInfoEncoder {

    /** Initial per-tick bit-packed body capacity — generous default; grows via ByteBuffer reallocation if needed. */
    private const val TICK_BUFFER_CAPACITY = 8192

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
     *
     * DO NOT regress this — packing passes together reintroduces the invisible-avatar / drift bug.
     */
    private inline fun byteAlignPass(out: BufferWriter, pass: () -> Unit) {
        out.startBitAccess()
        pass()
        out.stopBitAccess()
    }

    /**
     * Per-tick PLAYER_INFO emitter for the world tick loop. Returns the op22 to send THIS tick.
     *
     * **Always emits (never null) once past the first tick — the "idle loop" prod sends.** The NXT
     * client's local-player avatar is a smoothing/interpolating `PathingEntity` whose per-frame
     * position integrator is advanced+committed by the per-tick `ProcessPlayerInfo` (the op22 handler
     * `S2C_PLAYER_INFO_OP22 @0x100043500` → its high-res pass over the local slot). With NO op22
     * arriving each tick the integrator runs **open-loop** and the avatar's `graphNode` scene-fine
     * position creeps (capture `session-20260627-231803-41562-local`: a stationary spawn drifts
     * ~0.12 tiles/tick south, x held — y 3216→3181 over the session). Production sends an op22 EVERY
     * game tick (`docs/kb/services/world-stream-service.md`); the steady-state form is the
     * **stationary hold** `[hasUpdate=0][skip-run]` (decoded from prod: `hasUpdate=0, skipMode=1`).
     * That per-tick decode re-commits the avatar each tick and pins it to its tile.
     *
     * So when nothing changed we still emit [buildPerTick]'s stationary-hold body instead of null. The
     * op81 GPI prefix already placed the local 30-bit tile == `player.tile`; this idle loop keeps the
     * avatar THERE. The first tick is routed through [buildInit] for the un-suppressed `firstTick`
     * path (world entry suppresses it via [buildWorldEntrySync], which sets `firstTick=false`).
     */
    fun buildIfNeeded(player: Player): PlayerInfo = when {
        player.viewport.firstTick -> buildInit(player)
        else -> buildPerTick(player)
    }

    /**
     * World-entry op22 sent by `WorldServer` immediately after the op81 scene build.
     *
     * NOTE (2026-06-28): a movementType=3 TELEPORT delivery of the local appearance was tried and
     * REVERTED. The LOCAL player is architecturally excluded from the op22 ext-info APPEARANCE path
     * (RE: DecodeGpiPrefix excludes the local slot from the high-res ext-info walk list;
     * DecodeKnownPlayerUpdate@0x100025640 idle-returns the local slot), so the appearance never decoded
     * (current_appearance/avatar+0x12A0 stayed 0x0) AND the teleport's longer bit-block + the unconsumed
     * local ext-info block regressed the render plane (0→3, avatar floated above the ground). The
     * prod-accurate fix is to deliver the local appearance INLINE in the GPI add (slotObj+0x48 →
     * DecodeExternalPlayerUpdate@0x1000266e0 → immediate compose) — pending RE of the exact wire format
     * (op22 idle ext-info is kept here only as the prior baseline). See memory: avatar-render-next-steps.
     */
    fun buildWorldEntrySync(player: Player): PlayerInfo {
        player.appearance.ensureCachedBytes()
        player.viewport.firstTick = false
        return buildPerTick(player)
    }

    /**
     * First-tick GPI init form — **generated from local state**, Shape A per
     * `docs/protocol/world-bootstrap-948.md` §0/§4.3/§5. This is the ONLY GPI on the first tick
     * (op81 ships no prefix), so it must be a coherent standalone `ProcessPlayerInfo @0x001618a0`
     * init whose local 30-bit tile equals the op81 coord-header centre zone (the same spawn tile).
     *
     * The bit block is the same four-pass structure [buildPerTick] emits, with one difference: the
     * local player (high-res slot 0 == `playerIndex`) is routed through the **absolute-tile /
     * teleport** high-res path ([PlayerMovementEncoder.encodeAbsoluteTile]) instead of the per-tick
     * stationary path. On a solo first-light the viewport has ONLY the local player in
     * `highResIndices` and an empty `lowResIndices`; the other three passes are empty loops — exactly
     * the "no other players visible" init §4.3 blesses. No 2047-slot region array is written (§1.3/§8:
     * the prefix is NOT a flat 2047×18-bit array — Shape A sidesteps that bit layout entirely).
     *
     * APPEARANCE ext-info: emitted (with its 2-byte length prefix, added by the op22 codec) from the
     * local player's real pre-built appearance blob, built by [org.darkan.world.entity.Appearance].
     * The blob is always present (the Player ctor builds it), so the local player always carries a
     * real avatar at scene load. `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        viewport.firstTick = false

        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        // Players flagged hasExtInfo this tick, in cohort processing order. The local player's
        // appearance is appended first (pass 1 routes it through the absolute-tile path).
        val flaggedForExtInfo = ArrayList<Int>(8)

        // BYTE-ALIGN each pass (see [byteAlignPass]). Pass 1: HIGH-RES ACTIVE — the local player
        // (slot 0) takes the absolute-tile init path; any other high-res actives (none on first
        // light) fall back to the per-tick high-res path.
        byteAlignPass(bitOut) { encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeHighResInitPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo) }

        // Record the appearance we sent so the per-tick path won't re-emit it next tick.
        val cached = player.appearance.cachedBytes
        if (cached != null && player.index in viewport.cachedApprHashes.indices) {
            viewport.cachedApprHashes[player.index] = cached
        }

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = buildExtInfoBlocks(flaggedForExtInfo),
            firstTick = true,
        )
    }

    /**
     * Per-tick incremental form. Per §4A the bit-packed body has four passes, filtering each list by
     * the `+0x27` (active) flag:
     *  1. HIGH-RES ACTIVE — `viewport.highResIndices` where `active == true`.
     *  2. HIGH-RES INACTIVE — `viewport.highResIndices` where `active == false`.
     *  3. LOW-RES ACTIVE — `viewport.lowResIndices` where `active == true`.
     *  4. LOW-RES INACTIVE — `viewport.lowResIndices` where `active == false`.
     *
     * The local player is the ONLY high-res entry and emits the stationary form; absent low-res slots
     * fold into the skip-run.
     */
    private fun buildPerTick(player: Player): PlayerInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        // Player indices flagged hasExtInfo this tick, in cohort processing order. Per §4C the
        // ext-info blocks are emitted in the order the hasExtendedInfo bits fired.
        val flaggedForExtInfo = ArrayList<Int>(8)

        // BYTE-ALIGN each of the 4 passes (see [byteAlignPass]).
        byteAlignPass(bitOut) { encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeHighResPass(bitOut, player, viewport.highResIndices, activeFilter = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeLowResPass(bitOut, player, viewport.lowResIndices, activeFilter = false, flaggedForExtInfo) }

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = buildExtInfoBlocks(flaggedForExtInfo),
            firstTick = false,
        )
    }

    /**
     * High-res pass for the INIT form. Identical cohort filtering to [encodeHighResPass], but the
     * local player (the viewport owner) is encoded via the absolute-tile teleport path
     * ([PlayerMovementEncoder.encodeAbsoluteTile]). Any other high-res player (not present on first
     * light) uses the standard per-tick high-res encoder.
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
                PlayerMovementEncoder.encodeAbsoluteTile(out, target, flaggedForExtInfo)
            } else {
                val needsUpdate = PlayerExtInfoEncoder.needsAnyUpdate(viewer, target)
                if (needsUpdate) {
                    out.writeBits(1, 1)
                    PlayerMovementEncoder.encodeHighResPosition(out, target, flaggedForExtInfo)
                } else {
                    out.writeBits(1, 0)
                    out.writeBits(2, 0)
                }
            }
        }
    }

    /**
     * Encode one high-res pass (active or inactive filter). Per §4A's per-pass loop body, for each
     * player in [indices] matching the filter: 1-bit `hasUpdate`; if `hasUpdate==1` the high-res
     * position bits ([PlayerMovementEncoder.encodeHighResPosition]); else the no-skip stationary
     * marker (`mode=0`).
     *
     * `activeFilter` mirrors §4A's `+0x27 == 0` (active) vs `!= 0` (inactive) cohort;
     * [Player.active] is the modern-named mirror of `+0x27`. We emit `skipMode=0` (no skip) per slot
     * here — the conservative emit-every-player path; Phase 1.2b can batch consecutive no-update
     * slots via the skip-run for compactness.
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
            val matches = if (activeFilter) target.active else !target.active
            if (!matches) continue

            val needsUpdate = PlayerExtInfoEncoder.needsAnyUpdate(viewer, target)
            if (needsUpdate) {
                out.writeBits(1, 1)
                PlayerMovementEncoder.encodeHighResPosition(out, target, flaggedForExtInfo)
            } else {
                // No update for this slot. Per §4A passes 2/3/4 use ReadStationary — 2-bit mode then
                // variable-width skip count. We emit mode=0 (no skip) so the next iteration reads its
                // own hasUpdate bit. Phase 1.2b: batch consecutive no-update slots via mode 1/2/3.
                out.writeBits(1, 0)
                out.writeBits(2, 0)  // skipMode=0: no skip, continue with next slot.
            }
        }
    }

    /**
     * Encode one low-res pass (active or inactive filter). Per §4A's per-pass loop body, mirrors the
     * high-res pass but calls [PlayerMovementEncoder.encodeLowResPosition] when `hasUpdate==1`, and
     * run-length-encodes consecutive no-update slots into the stationary skip-run.
     *
     * Today the low-res cohort produces no needed updates (the local player is high-res; all other
     * slots are absent), so the whole pass collapses to a single skip-run over the absent slots.
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

            val needsUpdate = target != null && PlayerExtInfoEncoder.needsAnyUpdate(viewer, target)
            if (needsUpdate) {
                PlayerMovementEncoder.writeStationarySkipRun(out, skipRun)
                skipRun = 0
                out.writeBits(1, 1)
                PlayerMovementEncoder.encodeLowResPosition(out, target, flaggedForExtInfo)
            } else {
                skipRun++
            }
        }
        PlayerMovementEncoder.writeStationarySkipRun(out, skipRun)
    }

    /** Build the ext-info blocks for the players flagged this tick, in flag order. */
    private fun buildExtInfoBlocks(flaggedForExtInfo: List<Int>): List<ByteArray> {
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val target = Players.get(slot) ?: continue
            extendedInfo.add(PlayerExtInfoEncoder.encodeExtendedInfoBlock(target))
        }
        return extendedInfo
    }
}
