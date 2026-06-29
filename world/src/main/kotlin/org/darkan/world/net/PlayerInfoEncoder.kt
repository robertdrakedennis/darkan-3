package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.world.PlayerInfoSlots
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Orchestrator for op22 PLAYER_INFO. Emits the bit-packed body as the **exact inverse** of the
 * recorder oracle's verified op22 decode (`core/.../recorder/ClientStateCrossCheck.kt`), driving the
 * per-viewer GPI [PlayerInfoSlots] model so the byte-stream reads back to the same scene.
 *
 * ## The four passes, in the VERIFIED decode order
 *
 * `decodePlayerInfo` (`ClientStateCrossCheck.kt:1085`) runs FOUR passes, each followed by a byte
 * align, then rebuilds the cohorts. This encoder mirrors that order exactly:
 *
 *  1. **known, active=false** — `runKnownPass(renderList, false)` (`ClientStateCrossCheck.kt:1086`).
 *  2. **known, active=true**  — `runKnownPass(renderList, true)`  (`ClientStateCrossCheck.kt:1089`).
 *  3. **external, active=true**  — `runExternalPass(pendingList, true)`  (`ClientStateCrossCheck.kt:1092`).
 *  4. **external, active=false** — `runExternalPass(pendingList, false)` (`ClientStateCrossCheck.kt:1095`).
 *
 * Each pass is wrapped in [byteAlignPass] (the client byte-aligns the bit cursor at every pass
 * boundary — `alignToByte()` after each pass, `ClientStateCrossCheck.kt:1087/1090/1093/1096`). After
 * pass 4 the cohorts are rebuilt via [PlayerInfoSlots.rebuildAfterPasses], mirroring
 * `rebuildActivityFlagsAndLists` (`ClientStateCrossCheck.kt:1098/1259`) — so the server's slot model
 * tracks the client's tick-over-tick.
 *
 * ## Per-slot pass body (the inverse of `runKnownPass` / `runExternalPass`)
 *
 * For each slot in the cohort list whose `active == activeFlag` (`ClientStateCrossCheck.kt:1117`):
 *  * a 1-bit `hasUpdate`; if the slot has an update → `1` + the known/external update bits;
 *  * else `0` + a [writeSkipCount] run covering the FOLLOWING same-cohort no-update slots, with
 *    `nextActive=true` set on this slot and every skipped slot (mirroring the decode's
 *    `slot.nextActive = true` on the skip path, `ClientStateCrossCheck.kt:1130/1122`).
 *
 * ## Increment scope (1.2b increment 1 — FOUNDATION; movementType=0 ONLY)
 *
 * Every known update is the stationary form `movementType=0` ([PlayerMovementEncoder]); no real
 * walk/run/teleport motion and no low-res add → high-res promote is produced. The teleport form
 * ([PlayerMovementEncoder.encodeAbsoluteTile]) belongs to the op81 prefix and is used only by the
 * standalone first-tick init ([buildInit]). The precise increment-2 seam is documented at
 * [encodeKnownPass] / [encodeExternalPass].
 *
 * Two public entry points map to the two live call sites:
 *  * [buildWorldEntrySync] — `WorldServer` world-entry. Marks the appearance cached + clears
 *    `firstTick`, then emits the per-tick form.
 *  * [buildIfNeeded] — the `WorldTick` per-tick caller. Emits [buildInit] while `firstTick` is set,
 *    otherwise the per-tick form.
 */
object PlayerInfoEncoder {

    /** Initial per-tick bit-packed body capacity — generous default; grows via ByteBuffer reallocation if needed. */
    private const val TICK_BUFFER_CAPACITY = 8192

    /**
     * Runs one GPI pass inside its OWN bit-access window so the pass's bits are byte-aligned. The NXT
     * client (`S2C_PLAYER_INFO_OP22 @0x100043500`) rounds the player-info bit cursor UP to the next
     * byte (`ceil(bp/8)×8`) at every one of the 4 pass boundaries (RE-verified instruction-level
     * @0x10004363f / 0x100043732 / 0x100043854 / 0x100043938; mirrored by the decode's `alignToByte()`
     * after each pass, `ClientStateCrossCheck.kt:1087/1090/1093/1096`), so each pass MUST begin on a
     * byte boundary. Packing more than one pass into a byte (the old `c7 ff 40`) made the client read
     * the local entry's bits, round up, DISCARD that byte's trailing bits — where our skip-run began —
     * and resume the next pass mid-skip-run → bit-cursor desync → the ext-info drain reads the
     * appearance at the wrong byte → no avatar model. With a zeroed BufferWriter the pad bits are 0
     * (the client discards them). Live-verified: solo-spawn bit-block `c7 ff 40` → `c0 7f f4`.
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
     * position integrator is advanced+committed by the per-tick `ProcessPlayerInfo`. With NO op22
     * arriving each tick the integrator runs **open-loop** and the avatar's scene-fine position creeps
     * (capture `session-20260627-231803-41562-local`: a stationary spawn drifts ~0.12 tiles/tick
     * south). Production sends an op22 EVERY game tick; the steady-state form is the **stationary
     * hold** (`hasUpdate=0` then a skip-run, decoded from prod) which re-commits the avatar each tick
     * and pins it to its tile. So when nothing changed we still emit [buildPerTick]'s stationary-hold
     * body. The first tick is routed through [buildInit] for the un-suppressed `firstTick` path
     * (world entry suppresses it via [buildWorldEntrySync]).
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
     * DecodeKnownPlayerUpdate idle-returns the local slot — mirrored by the decode at
     * `ClientStateCrossCheck.kt:1171`), so the appearance never decoded AND the teleport's longer
     * bit-block regressed the render plane. The prod-accurate fix (deliver the local appearance INLINE
     * in the GPI add) is pending RE of the exact wire format. See memory: avatar-render-next-steps.
     */
    fun buildWorldEntrySync(player: Player): PlayerInfo {
        player.appearance.ensureCachedBytes()
        player.viewport.firstTick = false
        return buildPerTick(player)
    }

    /**
     * First-tick GPI init form — **generated from local state**, Shape A per
     * `docs/protocol/world-bootstrap-948.md` §0/§4.3/§5. This is a coherent standalone init whose
     * local 30-bit tile equals the op81 coord-header centre zone (the same spawn tile).
     *
     * The bit block is the same four-pass structure [buildPerTick] emits, with one difference: the
     * local player is routed through the **absolute-tile / teleport** high-res path
     * ([PlayerMovementEncoder.encodeAbsoluteTile]) instead of the per-tick stationary path. On a solo
     * first-light the render cohort has ONLY the local player and the pending cohort the 2046 absent
     * others; the empty cohorts collapse to no bits / a single skip-run.
     *
     * NOTE: this standalone init is NO LONGER sent at world entry (op81's [Op81GpiPrefix] IS the
     * world-entry GPI). It is retained + tested in isolation as the documented teleport-init form; the
     * un-suppressed `firstTick` path routes here. `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        viewport.firstTick = false

        val slots = viewport.playerSlots
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        // Players flagged hasExtInfo this tick, in cohort-processing order; the ext-info blocks are
        // emitted in the order the hasExtendedInfo bits fired (§4C).
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Four byte-aligned passes in the verified decode order. The local slot (in renderList,
        // active=false → pass 1) takes the absolute-tile init path; everything else mirrors buildPerTick.
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = false, init = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = true, init = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        slots.rebuildAfterPasses()

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
     * Per-tick incremental form — the four byte-aligned passes in the verified decode order over the
     * viewer's [PlayerInfoSlots] cohorts, then a cohort rebuild. See the class KDoc for the pass list
     * and the decode line citations.
     */
    private fun buildPerTick(player: Player): PlayerInfo {
        val viewport = player.viewport
        val slots = viewport.playerSlots
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        val flaggedForExtInfo = ArrayList<Int>(8)

        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = false, init = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = true, init = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        slots.rebuildAfterPasses()

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = buildExtInfoBlocks(flaggedForExtInfo),
            firstTick = false,
        )
    }

    /**
     * Encode ONE known (high-res) pass — the inverse of `runKnownPass` (`ClientStateCrossCheck.kt:1110`).
     *
     * Iterates [PlayerInfoSlots.renderList] in order, processing slots whose `active == activeFlag`.
     * For each processed slot: if it has an update (appearance/masks to deliver, or — when [init] and
     * it is the local slot — the absolute-tile init), write `hasUpdate=1` + the known position bits.
     * Otherwise it starts a stationary skip-run: write `hasUpdate=0` + a [writeSkipCount] over the
     * following matching no-update slots, and set `nextActive=true` on this slot and every skipped
     * slot (the decode does `slot.nextActive = true` on both the bit-reading slot and each skipped
     * slot, `ClientStateCrossCheck.kt:1130/1122`).
     *
     * INCREMENT-2 SEAM: real walk/run for a known slot plugs into [PlayerMovementEncoder.encodeHighResPosition]
     * (today `movementType=0`); the demote-to-low-res case (decode mvt=0 + present→false,
     * `ClientStateCrossCheck.kt:1170`) calls [PlayerInfoSlots.demoteToPending].
     */
    private fun encodeKnownPass(
        out: BufferWriter,
        viewer: Player,
        slots: PlayerInfoSlots,
        activeFlag: Boolean,
        init: Boolean,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val order = slots.renderList
        var i = 0
        while (i < order.size) {
            val idx = order[i]
            val slot = slots.slot(idx)
            if (slot == null || slot.active != activeFlag) {
                i++
                continue
            }

            val target = Players.get(idx)
            if (init && idx == slots.localIndex && target != null) {
                // First-tick init ONLY: the local player's known update is the absolute-tile teleport
                // form, which writes its OWN leading [hasUpdate=1] then [hasExt][mvt=3][30-bit tile].
                // (Standalone init form per docs/protocol/world-bootstrap-948.md §4.3; NOT sent at the
                // live world entry — that is op81's [Op81GpiPrefix].)
                PlayerMovementEncoder.encodeAbsoluteTile(out, target, flaggedForExtInfo)
                i++
            } else if (target != null && knownHasUpdate(viewer, target)) {
                out.writeBits(1, 1)
                PlayerMovementEncoder.encodeHighResPosition(out, target, flaggedForExtInfo)
                // The decode does NOT set nextActive for a known mvt=0 stay (local/has-ext idle-return,
                // ClientStateCrossCheck.kt:1171) — leave nextActive=false so the slot stays in this
                // cohort next tick.
                i++
            } else {
                // Start of a stationary skip-run: count the following same-cohort no-update slots.
                val skip = countKnownSkipRun(viewer, slots, order, i + 1, activeFlag)
                out.writeBits(1, 0)
                writeSkipCount(out, skip)
                slot.nextActive = true
                // Mark the skipped slots (the decode sets nextActive=true on each, ClientStateCrossCheck.kt:1122).
                markSkippedKnown(slots, order, i + 1, activeFlag, skip)
                i = advancePastSkipRun(slots, order, i + 1, activeFlag, skip)
            }
        }
    }

    /**
     * Encode ONE external (low-res) pass — the inverse of `runExternalPass`
     * (`ClientStateCrossCheck.kt:1136`). Same structure as [encodeKnownPass] over
     * [PlayerInfoSlots.pendingList]; an external update would be the low-res add/move form
     * ([PlayerMovementEncoder.encodeLowResPosition]). This increment produces NO external updates —
     * every pending slot is absent/no-update, so each matching cohort collapses to a single skip-run.
     *
     * INCREMENT-2 SEAM: a low-res "add → promote to high-res" (decode `decodeExternalPlayerUpdate`
     * branch 0, `ClientStateCrossCheck.kt:1209`) writes `hasUpdate=1` here, emits the add bits via
     * [PlayerMovementEncoder.encodeLowResPosition], sets `nextActive=true`, and calls
     * [PlayerInfoSlots.promoteToRender]; the move branches (1-3) call [PlayerInfoSlots.setCoord].
     */
    private fun encodeExternalPass(
        out: BufferWriter,
        viewer: Player,
        slots: PlayerInfoSlots,
        activeFlag: Boolean,
        @Suppress("UNUSED_PARAMETER") flaggedForExtInfo: MutableList<Int>,
    ) {
        val order = slots.pendingList
        var i = 0
        while (i < order.size) {
            val idx = order[i]
            val slot = slots.slot(idx)
            if (slot == null || slot.active != activeFlag) {
                i++
                continue
            }

            // No external slot has an update this increment — every matching slot is a no-update run.
            val skip = countExternalSkipRun(slots, order, i + 1, activeFlag)
            out.writeBits(1, 0)
            writeSkipCount(out, skip)
            slot.nextActive = true
            markSkippedExternal(slots, order, i + 1, activeFlag, skip)
            i = advancePastSkipRun(slots, order, i + 1, activeFlag, skip)
        }
    }

    /**
     * Does this known-cohort [target] have an update to deliver to [viewer] this tick? True when the
     * target has ext-info to send ([PlayerExtInfoEncoder.needsAnyUpdate] — a pending mask or an
     * undelivered appearance) OR it WALKED this tick (a step applied by the world tick,
     * [Player.lastWalkStepDir] != [MovementQueue.NO_STEP], increment 2a). Either makes the slot emit
     * `hasUpdate=1` and breaks any surrounding skip-run, so the walk bits actually reach the wire even
     * when the appearance is already delivered.
     *
     * [PlayerExtInfoEncoder.needsAnyUpdate] is evaluated FIRST and unconditionally so its
     * first-appearance recording side effect still runs every tick regardless of whether the player
     * also walked. (For the local slot on the init path the caller forces the absolute-tile form
     * regardless; this gate governs the per-tick path.)
     */
    private fun knownHasUpdate(viewer: Player, target: Player): Boolean {
        val needsExtInfo = PlayerExtInfoEncoder.needsAnyUpdate(viewer, target)
        val walkedThisTick = target.lastWalkStepDir != MovementQueue.NO_STEP
        return needsExtInfo || walkedThisTick
    }

    /**
     * Count the stationary skip-run starting at [from] in the known cohort [order]: the number of
     * CONSECUTIVE matching (`active == activeFlag`) slots that have NO update. Mirrors the decode's
     * skip semantics — the run covers the following same-cohort slots until the next matching slot
     * WITH an update.
     */
    private fun countKnownSkipRun(
        viewer: Player,
        slots: PlayerInfoSlots,
        order: List<Int>,
        from: Int,
        activeFlag: Boolean,
    ): Int {
        var count = 0
        var i = from
        while (i < order.size) {
            val slot = slots.slot(order[i])
            if (slot == null || slot.active != activeFlag) {
                i++
                continue
            }
            val target = Players.get(order[i])
            if (target != null && knownHasUpdate(viewer, target)) break
            count++
            i++
        }
        return count
    }

    /** Count the stationary skip-run starting at [from] in the external cohort — every matching slot is a no-update slot this increment. */
    private fun countExternalSkipRun(
        slots: PlayerInfoSlots,
        order: List<Int>,
        from: Int,
        activeFlag: Boolean,
    ): Int {
        var count = 0
        var i = from
        while (i < order.size) {
            val slot = slots.slot(order[i])
            if (slot != null && slot.active == activeFlag) count++
            i++
        }
        return count
    }

    /** Set `nextActive=true` on the [skip] matching known-cohort slots starting at [from] (decode `ClientStateCrossCheck.kt:1122`). */
    private fun markSkippedKnown(
        slots: PlayerInfoSlots,
        order: List<Int>,
        from: Int,
        activeFlag: Boolean,
        skip: Int,
    ) {
        var remaining = skip
        var i = from
        while (remaining > 0 && i < order.size) {
            val slot = slots.slot(order[i])
            if (slot != null && slot.active == activeFlag) {
                slot.nextActive = true
                remaining--
            }
            i++
        }
    }

    /** Set `nextActive=true` on the [skip] matching external-cohort slots starting at [from] (decode `ClientStateCrossCheck.kt:1148`). */
    private fun markSkippedExternal(
        slots: PlayerInfoSlots,
        order: List<Int>,
        from: Int,
        activeFlag: Boolean,
        skip: Int,
    ) = markSkippedKnown(slots, order, from, activeFlag, skip)

    /** Advance the cohort index past the [skip] matching slots consumed by a skip-run, returning the next index to process. */
    private fun advancePastSkipRun(
        slots: PlayerInfoSlots,
        order: List<Int>,
        from: Int,
        activeFlag: Boolean,
        skip: Int,
    ): Int {
        var remaining = skip
        var i = from
        while (remaining > 0 && i < order.size) {
            val slot = slots.slot(order[i])
            if (slot != null && slot.active == activeFlag) remaining--
            i++
        }
        return i
    }

    /**
     * Write the 2-bit-mode + variable-width skip count tail of the stationary run — the inverse of
     * `readPlayerSkipCount` (`ClientStateCrossCheck.kt:1290`): mode 0 = no further slots; mode 1 =
     * 5-bit count (<32); mode 2 = 8-bit count (<256); mode 3 = 11-bit count (<2048). A run longer than
     * the 11-bit max would need splitting, but a single cohort here never exceeds 2047 matching slots
     * so one tail always suffices.
     */
    private fun writeSkipCount(out: BufferWriter, count: Int) {
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
