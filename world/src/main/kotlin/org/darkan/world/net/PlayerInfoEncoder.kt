package org.darkan.world.net

import org.darkan.core.net.prot.PlayerInfo
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.world.PlayerInfoSlots
import org.darkan.world.world.Players
import org.darkan.world.world.SceneBuildPlanner
import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.math.abs

/**
 * Orchestrator for op22 PLAYER_INFO. Emits the bit-packed body as the **exact inverse** of the
 * recorder oracle's verified op22 decode (`core/.../recorder/ClientStateCrossCheck.kt`), driving the
 * per-viewer GPI [PlayerInfoSlots] model so the byte-stream reads back to the same scene.
 *
 * ## The four passes, in the VERIFIED decode order
 *
 * `decodePlayerInfo` (in `ClientStateCrossCheck.kt`) runs FOUR passes, each followed by a byte
 * align, then rebuilds the cohorts. This encoder mirrors that order exactly:
 *
 *  1. **known, active=false** — `runKnownPass(renderList, false)`.
 *  2. **known, active=true**  — `runKnownPass(renderList, true)`.
 *  3. **external, active=true**  — `runExternalPass(pendingList, true)`.
 *  4. **external, active=false** — `runExternalPass(pendingList, false)`.
 *
 * Each pass is wrapped in [byteAlignPass] (the client byte-aligns the bit cursor at every pass
 * boundary — the decode's `alignToByte()` after each pass). After
 * pass 4 the cohorts are rebuilt via [PlayerInfoSlots.rebuildAfterPasses], mirroring the decode's
 * `rebuildActivityFlagsAndLists` — so the server's slot model
 * tracks the client's tick-over-tick.
 *
 * ## Per-slot pass body (the inverse of `runKnownPass` / `runExternalPass`)
 *
 * For each slot in the cohort list whose `active == activeFlag` (the decode's `runKnownPass`/`runExternalPass`):
 *  * a 1-bit `hasUpdate`; if the slot has an update → `1` + the known/external update bits;
 *  * else `0` + a [writeSkipCount] run covering the FOLLOWING same-cohort no-update slots, with
 *    `nextActive=true` set on this slot and every skipped slot (mirroring the decode's
 *    `slot.nextActive = true` on the skip path).
 *
 * ## Movement scope (walk + run + remote render — all server-driven)
 *
 * A stationary known update is `movementType=0` ([PlayerMovementEncoder]) — including the local
 * first-tick add, which carries the APPEARANCE ext-info INLINE (the prod local form, `c0 …` + ext-info;
 * see [buildInit]). A MOVING known slot runs the prod three-phase state machine ([walkPhase]):
 * START (`mvt=3` move-mode desc 0x8 walk / 0xc run + the step delta) → STEP (`mvt=1` walk = 1 tile /
 * `mvt=2` run = 2 tiles) → STOP (`mvt=3` desc 0x0, no move), POSITION-ONLY (no movement ext-info). This is
 * SERVER-DRIVEN for the LOCAL slot too — a decoded live-prod local walk is exactly this shape (the earlier
 * "local walk is client-predicted, server sends mvt=0/absent" premise was falsified; the prior tools
 * mis-identified the local slot as a remote player). REMOTE players in view are added (low-res ADD →
 * [PlayerInfoSlots.promoteToRender] → high-res) and then driven by the SAME slot-generic state machine
 * ([encodeExternalPass]). The teleport form ([PlayerMovementEncoder.encodeAbsoluteTile]) is NOT used by
 * either build path; it is retained as the real-teleport seam. Remaining seams: remote region-move (a
 * remote walking out of its op81-seeded region) and remote run.
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
     * after each pass, `ClientStateCrossCheck.kt`), so each pass MUST begin on a
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
     * FIXED (2026-06-29): the local appearance is delivered INLINE in the GPI add — the prod
     * mechanism. The local slot's high-res entry is the stationary form
     * `[hasUpdate=1][hasExtInfo=1][movementType=0]` ([PlayerMovementEncoder.encodeHighResPosition])
     * with the APPEARANCE ext-info block following, byte-for-byte matching the production op22
     * (`session-20260627-044937-74364-production`, local first-tick op22 = `c0 …` then one ext-info
     * block; mvt=0, NOT mvt=3). The earlier movementType=3 TELEPORT delivery
     * ([PlayerMovementEncoder.encodeAbsoluteTile]) was REVERTED: prod never sends it, its longer
     * bit-block regressed the render plane, and it diverged from the steady-state per-tick form. Both
     * [buildWorldEntrySync] and [buildInit] now emit this same stationary inline-appearance local form.
     */
    fun buildWorldEntrySync(player: Player): PlayerInfo {
        player.appearance.ensureCachedBytes()
        player.viewport.firstTick = false
        return buildPerTick(player)
    }

    /**
     * First-tick GPI init form — **generated from local state**, per
     * `docs/protocol/world-bootstrap-948.md` §0/§4.3/§5 and the production op22 capture. This is a
     * coherent standalone init whose local 30-bit position is implied by the preceding op81 GPI prefix
     * (the prefix carries the absolute tile; the op22 add re-commits the avatar in place).
     *
     * **INLINE local appearance (prod-accurate, the avatar "no model" fix).** The local player is in
     * the render cohort with `active=false` (pass 1). Its high-res entry is the **stationary**
     * `[hasUpdate=1][hasExtInfo=1][movementType=0]` form ([PlayerMovementEncoder.encodeHighResPosition]),
     * and its APPEARANCE ext-info block (default kits, empty equipment — built by
     * [org.darkan.world.entity.Appearance.ensureCachedBytes]) is emitted INLINE after the bit block,
     * framed by [org.darkan.core.net.prot.revision.rev948.Rev948ExtInfoTransforms] (mask bit 3 / 0x08,
     * length mode 3, body mode 2). This is the EXACT form the production op22 sends for the local slot
     * (`c0 …` + one ext-info block; verified against `session-20260627-044937-74364-production`). The
     * empty cohorts collapse to no bits / a single skip-run on a solo first-light.
     *
     * The previous movementType=3 absolute-tile / TELEPORT local form was REMOVED — prod never sends
     * it and it regressed the render plane (see [buildWorldEntrySync]). `buildInit` and [buildPerTick]
     * now produce the SAME local-slot encoding; the only difference is `PlayerInfo.firstTick`.
     *
     * NOTE: at the live world entry op81's generated GPI prefix ([Op81GpiPrefix]) IS the world-entry
     * GPI and [buildWorldEntrySync] sends the following op22; this `buildInit` form is what the
     * un-suppressed `firstTick` path emits. `viewport.firstTick` is set to `false`.
     */
    fun buildInit(player: Player): PlayerInfo {
        val viewport = player.viewport
        viewport.firstTick = false

        // Match buildWorldEntrySync / prod: the inline-appearance local entry needs the appearance
        // bytes resolved before the ext-info pass reads them.
        player.appearance.ensureCachedBytes()

        val slots = viewport.playerSlots
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        // Players flagged hasExtInfo this tick, in cohort-processing order; the ext-info blocks are
        // emitted in the order the hasExtendedInfo bits fired (§4C).
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Four byte-aligned passes in the verified decode order. The local slot (renderList,
        // active=false → pass 1) is the stationary inline-appearance high-res form, identical to
        // buildPerTick — NOT the reverted absolute-tile teleport.
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        slots.rebuildAfterPasses()

        // NOTE: the appearance-delivered record is no longer set here. [buildExtInfoBlocks] →
        // [PlayerExtInfoEncoder.encodeExtendedInfoBlock] is now the SINGLE place that records the
        // delivery, exactly when the APPEARANCE block is emitted, so the `hasExtendedInfo` flag (set
        // during the passes above) and the emitted block stay consistent. Recording here would flip the
        // appearance-undelivered state before the block builder runs → hasExt=1 with an empty block →
        // desync.
        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = buildExtInfoBlocks(player, flaggedForExtInfo),
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

        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeKnownPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = true, flaggedForExtInfo) }
        byteAlignPass(bitOut) { encodeExternalPass(bitOut, player, slots, activeFlag = false, flaggedForExtInfo) }
        slots.rebuildAfterPasses()

        return PlayerInfo(
            bitBlock = bitOut.toArray(),
            extendedInfo = buildExtInfoBlocks(player, flaggedForExtInfo),
            firstTick = false,
        )
    }

    /**
     * Encode ONE known (high-res) pass — the inverse of `runKnownPass` (`ClientStateCrossCheck.kt`).
     *
     * Iterates [PlayerInfoSlots.renderList] in order, processing slots whose `active == activeFlag`.
     * For each processed slot: if it has an update (appearance/masks to deliver), write `hasUpdate=1`
     * + the known position bits ([PlayerMovementEncoder.encodeHighResPosition]). Otherwise it starts a
     * stationary skip-run: write `hasUpdate=0` + a [writeSkipCount] over the following matching
     * no-update slots, and set `nextActive=true` on this slot and every skipped slot (the decode does
     * `slot.nextActive = true` on both the bit-reading slot and each skipped slot,
     * `ClientStateCrossCheck.kt`).
     *
     * The LOCAL slot is NOT special-cased: like every known slot it runs the same prod walk state machine
     * ([walkPhase] + [PlayerMovementEncoder.encodeHighResPosition]). On a stationary tick with an undelivered
     * appearance it emits the inline-appearance hold `[hasUpdate=1][hasExt=1][mvt=0]` + the APPEARANCE
     * ext-info block (the prod local first-tick form `c0 …`, mvt=0). On a walk it emits the three-phase
     * shape: WALK-START (`mvt=3` desc 0x8) → WALK-STEP (`mvt=1`) → WALK-STOP (`mvt=3` desc 0x0). Both
     * [buildInit] and [buildPerTick] call this identically.
     *
     * After emitting the form, [commitWalkLatch] advances the slot's `wasWalking` latch (START/STEP set,
     * STOP clear) so the next tick resolves the correct phase. The future run (`mvt=2`) and demote-to-low-res
     * (decode mvt=0 + present→false) cases plug in at the same seam.
     */
    private fun encodeKnownPass(
        out: BufferWriter,
        viewer: Player,
        slots: PlayerInfoSlots,
        activeFlag: Boolean,
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
            if (target != null && knownHasUpdate(viewer, target)) {
                out.writeBits(1, 1)
                val phase = walkPhase(viewer, target)
                PlayerMovementEncoder.encodeHighResPosition(out, viewer, target, phase, flaggedForExtInfo)
                // Commit the walk-state latch now that the form is emitted (START/STEP set it, STOP clears
                // it). The decode does NOT set nextActive for a known stay (ClientStateCrossCheck.kt),
                // so leave nextActive=false → the slot stays in this cohort next tick.
                commitWalkLatch(slot, phase)
                i++
            } else {
                // Start of a stationary skip-run: count the following same-cohort no-update slots.
                val skip = countKnownSkipRun(viewer, slots, order, i + 1, activeFlag)
                out.writeBits(1, 0)
                writeSkipCount(out, skip)
                slot.nextActive = true
                // Mark the skipped slots (the decode sets nextActive=true on each, ClientStateCrossCheck.kt).
                markSkippedKnown(slots, order, i + 1, activeFlag, skip)
                i = advancePastSkipRun(slots, order, i + 1, activeFlag, skip)
            }
        }
    }

    /**
     * Encode ONE external (low-res) pass — the inverse of `runExternalPass`
     * (`ClientStateCrossCheck.kt`). Same interleaving structure as [encodeKnownPass] over
     * [PlayerInfoSlots.pendingList]: a matching slot either has an update (`hasUpdate=1` + the update
     * bits) or starts a skip-run (`hasUpdate=0` + a [writeSkipCount] over the following matching
     * no-update slots).
     *
     * The external update produced is the **low-res ADD** (`decodeExternalPlayerUpdate` branch 0,
     * `ClientStateCrossCheck.kt`): for a pending slot that holds an in-range, not-yet-rendered
     * REMOTE player ([shouldRenderRemote]) we write `hasUpdate=1` + the ADD bits (via [emitRemoteAdd]),
     * set `nextActive=true`, and [PlayerInfoSlots.promoteToRender] the slot so the next
     * [PlayerInfoSlots.rebuildAfterPasses] moves it into [PlayerInfoSlots.renderList] and the known
     * passes (the walk state machine) drive it from the following tick. Every other matching slot is a
     * no-update slot and folds into the skip-run (`nextActive=true` on the run leader + each skipped
     * slot, mirroring the decode's `slot.nextActive = true`, `ClientStateCrossCheck.kt`).
     *
     * [emitRemoteAdd] picks the bare branch-0 ADD ([PlayerMovementEncoder.encodeLowResAdd], `jumpFlag=0`)
     * when the slot's low-res anchor already matches the remote's region, or the **re-anchoring** ADD
     * ([PlayerMovementEncoder.encodeLowResReanchoringAdd], `jumpFlag=1` + a chained region-move, decode
     * branches 1-3) when it does not — the LATE-JOIN / region-mover case. So a remote whose seeded anchor
     * is stale (a late-joiner seeded with the local region, or a low-res slot whose remote walked to a new
     * region) is re-anchored AS PART OF the ADD and rendered, instead of folding into a perpetual
     * skip-run. The high-res→low-res demote remains a seam (a rendered remote is driven by the known
     * passes' high-res walk, which crosses region boundaries freely with no anchor).
     */
    private fun encodeExternalPass(
        out: BufferWriter,
        viewer: Player,
        slots: PlayerInfoSlots,
        activeFlag: Boolean,
        flaggedForExtInfo: MutableList<Int>,
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

            val target = Players.get(idx)
            if (target != null && shouldRenderRemote(viewer, target, slot)) {
                // Low-res ADD → promote to high-res: emit the branch-0 ADD bits, flip present, and set
                // nextActive (the decode's caller does slot.nextActive=true on a returning add).
                emitRemoteAdd(out, viewer, target, slots, slot, idx, flaggedForExtInfo)
                slots.promoteToRender(idx)
                slot.nextActive = true
                i++
            } else {
                // No-update slot: start a skip-run over the following matching slots that ALSO have no
                // add (an add slot breaks the run, exactly as a high-res update breaks a known skip-run).
                val skip = countExternalSkipRun(viewer, slots, order, i + 1, activeFlag)
                out.writeBits(1, 0)
                writeSkipCount(out, skip)
                slot.nextActive = true
                markSkippedExternal(slots, order, i + 1, activeFlag, skip)
                i = advancePastSkipRun(slots, order, i + 1, activeFlag, skip)
            }
        }
    }

    /**
     * Emit the low-res ADD bits for an in-range remote [target] in pending slot [idx] and re-anchor the
     * SERVER's slot coord to match — the inverse of `decodeExternalPlayerUpdate` branch 0
     * (`ClientStateCrossCheck.kt:1807-1822`), with the chained `jumpFlag=1` re-anchoring variant when the
     * slot's low-res anchor region != the remote's live region.
     *
     * Two cases, mirroring the decode's branch-0 jumpFlag bit:
     *  * **anchor already correct** (`tile>>6 == coord.region`, same plane): the bare ADD
     *    ([PlayerMovementEncoder.encodeLowResAdd], `jumpFlag=0`) — the 6-bit local offsets already fit.
     *  * **anchor stale** (a LATE-JOINER seeded with the local player's region, or a low-res slot whose
     *    remote walked to a new region): the re-anchoring ADD
     *    ([PlayerMovementEncoder.encodeLowResReanchoringAdd], `jumpFlag=1`) — a chained region-move
     *    (decode branches 1-3) updates `slot.coord` to the remote's CURRENT region FIRST, then the
     *    localX/localY are read relative to it. We update the server mirror via [PlayerInfoSlots.setCoord]
     *    so the server's coord stays identical to what the client decoded (the decode mutates
     *    `slot.coord` inside the chained recursion before reading the locals).
     *
     * This is the fix for the live multiplayer bug: render eligibility ([shouldRenderRemote]) is now
     * decoupled from the stale anchor — ANY in-window remote is added, re-anchoring as needed, so a
     * late-joiner and a region-mover both render instead of folding into a perpetual skip-run.
     */
    private fun emitRemoteAdd(
        out: BufferWriter,
        viewer: Player,
        target: Player,
        slots: PlayerInfoSlots,
        slot: PlayerInfoSlots.GpiSlot,
        idx: Int,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        out.writeBits(1, 1) // hasUpdate
        val anchor = slot.coord
        val tile = target.tile
        val liveRegionX = tile.x ushr 6
        val liveRegionY = tile.y ushr 6
        val anchorMatches =
            anchor.plane == tile.level && anchor.regionX == liveRegionX && anchor.regionY == liveRegionY

        if (anchorMatches) {
            PlayerMovementEncoder.encodeLowResAdd(
                out, viewer, target,
                regionX = anchor.regionX, regionY = anchor.regionY, plane = anchor.plane,
                flaggedForExtInfo,
            )
            return
        }

        // Re-anchor (chained jumpFlag=1 region-move) to the remote's live region, then ADD relative to
        // it. Keep the SERVER's coord mirror in lock-step with the client's decode.
        PlayerMovementEncoder.encodeLowResReanchoringAdd(
            out, viewer, target,
            fromRegionX = anchor.regionX, fromRegionY = anchor.regionY, fromPlane = anchor.plane,
            newRegionX = liveRegionX, newRegionY = liveRegionY,
            flaggedForExtInfo,
        )
        slots.setCoord(idx, PlayerInfoSlots.LowResCoord(tile.level, liveRegionX, liveRegionY))
    }

    /**
     * Should [viewer] add [target] (a registered remote in this pending slot) to its render cohort this
     * tick? True iff the slot is not already present (not yet promoted) and the remote is within the
     * viewer's render window (Chebyshev distance in zones from the scene centre ≤
     * [SceneBuildPlanner.RENDER_RADIUS_ZONES], same plane — see [withinRenderWindow]).
     *
     * NOTE (2026-06-30 — the live multiplayer fix): this no longer requires the remote's live tile to
     * sit inside the slot's low-res anchor region. That stale-anchor check is the bug — a LATE-JOINER's
     * slot is seeded with the LOCAL player's region (the joiner did not exist at world entry, so
     * `seedFromGpiPrefix` could not read its tile), and a low-res region-mover walks away from its
     * seeded anchor; either way the old `tile>>6 == anchor` gate failed forever → a perpetual skip-run →
     * no remote ever rendered. The add now RE-ANCHORS as part of the ADD ([emitRemoteAdd] emits the
     * chained `jumpFlag=1` region-move, decode branches 1-3), so the 6-bit local offsets always fit.
     *
     * The viewer itself is never a remote (its slot is in [PlayerInfoSlots.renderList], not the pending
     * cohort), so no self-check is needed. Pure read (no mutation) so the skip-run look-ahead can call it.
     */
    private fun shouldRenderRemote(viewer: Player, target: Player, slot: PlayerInfoSlots.GpiSlot): Boolean {
        if (slot.present) return false
        if (target.index == viewer.index) return false
        return withinRenderWindow(viewer, target)
    }

    /**
     * Is [target] inside [viewer]'s render window — within [SceneBuildPlanner.RENDER_RADIUS_ZONES] zones
     * (Chebyshev) of the viewer's scene-centre tile, on the same plane? Reuses the op81 scene plan
     * ([org.darkan.world.world.Viewport.sceneBuildPlan]) so visibility matches the actual loaded scene.
     */
    private fun withinRenderWindow(viewer: Player, target: Player): Boolean {
        val centre = viewer.viewport.sceneBuildPlan.tile
        if (centre.level != target.tile.level) return false
        val dzx = abs((target.tile.x ushr 3) - (centre.x ushr 3))
        val dzy = abs((target.tile.y ushr 3) - (centre.y ushr 3))
        return dzx <= SceneBuildPlanner.RENDER_RADIUS_ZONES && dzy <= SceneBuildPlanner.RENDER_RADIUS_ZONES
    }

    /**
     * Does this known-cohort [target] have an update to deliver to [viewer] this tick? True when the target
     * has ext-info to send ([PlayerExtInfoEncoder.needsAnyUpdate] — a pending mask or an appearance not yet
     * delivered to THIS viewer), OR its per-tick walk [walkPhase] is not [PlayerMovementEncoder.WalkPhase.NONE]
     * (a WALK-START / WALK-STEP / WALK-STOP marker is itself a high-res update).
     *
     * SERVER-DRIVEN LOCAL WALK (2026-06-30 — the prod-decoded model): prod FORCES the local walk too. A
     * decoded live-prod local walk (`session-20260630-033557-27478-production`, idx 1160) is the three-phase
     * move-mode shape START(mvt3 desc 0x8)→STEP(mvt1)×N→STOP(mvt3 desc 0x0); the START/STOP markers and every
     * STEP are `hasUpdate=1`. So the local slot is NOT special-cased — it takes forced movement exactly like
     * any walker. Crucially the WALK-STOP tick (`wasWalking && !stepped`) IS an update (the idle marker), so it
     * must NOT fold into a skip-run; [walkPhase] returning STOP makes this gate true for that tick. POSITION-
     * ONLY — no movement ext-info (no bit-0x20/0x80); the slot is flagged for an ext-info block only by the
     * `needsAnyUpdate` path (a real pending mask / first appearance).
     *
     * Pure read (no latch mutation) — safe to call during the skip-run look-ahead ([countKnownSkipRun]). The
     * latch is committed only at the slot's emit point in [encodeKnownPass].
     */
    private fun knownHasUpdate(viewer: Player, target: Player): Boolean {
        val needsExtInfo = PlayerExtInfoEncoder.needsAnyUpdate(viewer, target)
        // Any non-NONE phase (WALK-START/STEP/STOP and RUN_START/RUN_STEP/RUN_STOP) is itself a high-res
        // update, so a moving slot — walking OR running — never folds into a skip-run on a move/marker tick.
        return needsExtInfo || walkPhase(viewer, target) != PlayerMovementEncoder.WalkPhase.NONE
    }

    /**
     * Resolve [target]'s per-tick movement phase as seen by [viewer] — the prod three-phase WALK shape
     * and its RUN mirror, derived from the slot's persistent `wasWalking` / `wasRunning` latches + what
     * the target did this tick:
     *  * `walkStepped` = a 1-tile step was applied ([Player.lastWalkStepDir] != [MovementQueue.NO_STEP]).
     *  * `runStepped`  = a full 2-tile RUN step was applied ([Player.lastRunDelta] != [MovementQueue.NO_STEP]).
     *
     * WALK arms (idle/walk, 1 tile/tick):
     *  * `idle && walkStepped (not running)` → [PlayerMovementEncoder.WalkPhase.START] (idle→walk marker).
     *  * `wasWalking && walkStepped`         → [PlayerMovementEncoder.WalkPhase.STEP]  (walk→walk).
     *  * `wasWalking && !stepped`            → [PlayerMovementEncoder.WalkPhase.STOP]  (walk→idle marker).
     *
     * RUN arms (run, 2 tiles/tick — run-START carries the full first 2-tile delta, prod-verified):
     *  * `idle && runStepped`        → [PlayerMovementEncoder.WalkPhase.RUN_START] (idle→run marker, +2 tiles).
     *  * `wasRunning && runStepped`  → [PlayerMovementEncoder.WalkPhase.RUN_STEP]  (run→run, +2 tiles).
     *  * `wasRunning && walkStepped && !runStepped` → [PlayerMovementEncoder.WalkPhase.STEP] (run→walk
     *    odd-tail handoff: the runner slows to a 1-tile `mvt=1` WALK step).
     *  * `wasRunning && !stepped`    → [PlayerMovementEncoder.WalkPhase.RUN_STOP] (run→idle marker).
     *
     *  * otherwise → [PlayerMovementEncoder.WalkPhase.NONE] (stationary hold).
     *
     * `wasWalking` and `wasRunning` are mutually exclusive at rest. A full 2-tile run step ([runStepped],
     * `lastRunDelta` set by the world tick's run drain) drives both RUN_START (first tick) and RUN_STEP;
     * the run-START carries the 2-tile delta as a signed-5 move-mode marker (byte offset 0xc).
     *
     * **Pure** — reads the latches but never mutates them; the look-ahead and the gate can call it freely.
     * The encoder commits the transition at the emit point via [commitWalkLatch]. Applies to ANY mover;
     * the local player is the immediate target but the machine is slot-generic.
     */
    private fun walkPhase(viewer: Player, target: Player): PlayerMovementEncoder.WalkPhase {
        val walkStepped = target.lastWalkStepDir != MovementQueue.NO_STEP
        val runStepped = target.lastRunDelta != MovementQueue.NO_STEP
        val stepped = walkStepped || runStepped
        val slot = viewer.viewport.playerSlots.slot(target.index)
        val wasWalking = slot?.wasWalking ?: false
        val wasRunning = slot?.wasRunning ?: false
        val idle = !wasWalking && !wasRunning
        return when {
            // RUN move-state (checked first so a running slot never falls into a WALK arm).
            wasRunning && runStepped -> PlayerMovementEncoder.WalkPhase.RUN_STEP
            wasRunning && walkStepped -> PlayerMovementEncoder.WalkPhase.STEP   // run→walk odd-tail handoff
            wasRunning && !stepped -> PlayerMovementEncoder.WalkPhase.RUN_STOP
            // Idle→run: a full 2-tile run step from rest enters via RUN_START.
            idle && runStepped -> PlayerMovementEncoder.WalkPhase.RUN_START
            // Idle→walk.
            idle && walkStepped -> PlayerMovementEncoder.WalkPhase.START
            // WALK move-state.
            wasWalking && stepped -> PlayerMovementEncoder.WalkPhase.STEP
            wasWalking && !stepped -> PlayerMovementEncoder.WalkPhase.STOP
            else -> PlayerMovementEncoder.WalkPhase.NONE
        }
    }

    /**
     * Commit the slot's `wasWalking` / `wasRunning` latches after its high-res form is emitted:
     *  * WALK-START / WALK-STEP → WALK move-state (wasWalking=true, wasRunning=false). The run→walk
     *    odd-tail handoff lands here too (a STEP while `wasRunning`), cleanly transferring the move-state
     *    to WALK so the next idle tick is a plain walk-STOP.
     *  * run-START / RUN-STEP → RUN move-state (wasRunning=true, wasWalking=false).
     *  * WALK-STOP / run-STOP → idle (both latches false).
     *  * NONE → leave the latches (an idle/inline-appearance tick does not change the move-state).
     * Called once per slot at the emit point.
     */
    private fun commitWalkLatch(slot: PlayerInfoSlots.GpiSlot, phase: PlayerMovementEncoder.WalkPhase) {
        when (phase) {
            PlayerMovementEncoder.WalkPhase.START, PlayerMovementEncoder.WalkPhase.STEP -> {
                slot.wasWalking = true
                slot.wasRunning = false
            }
            PlayerMovementEncoder.WalkPhase.RUN_START, PlayerMovementEncoder.WalkPhase.RUN_STEP -> {
                slot.wasRunning = true
                slot.wasWalking = false
            }
            PlayerMovementEncoder.WalkPhase.STOP, PlayerMovementEncoder.WalkPhase.RUN_STOP -> {
                slot.wasWalking = false
                slot.wasRunning = false
            }
            PlayerMovementEncoder.WalkPhase.NONE -> Unit
        }
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

    /**
     * Count the skip-run starting at [from] in the external cohort: the number of CONSECUTIVE matching
     * (`active == activeFlag`) slots that have NO add — i.e. up to (but not including) the next matching
     * slot that [shouldRenderRemote] would add. Mirrors [countKnownSkipRun]: an add slot breaks the run
     * exactly as a high-res update breaks a known run, so the RLE stays the exact inverse of the decode's
     * `runExternalPass` (a `hasUpdate=1` add is read individually; the no-update slots between adds fold
     * into one skip-run).
     */
    private fun countExternalSkipRun(
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
            if (target != null && shouldRenderRemote(viewer, target, slot)) break
            count++
            i++
        }
        return count
    }

    /** Set `nextActive=true` on the [skip] matching known-cohort slots starting at [from] (decode `ClientStateCrossCheck.kt`). */
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

    /** Set `nextActive=true` on the [skip] matching external-cohort slots starting at [from] (decode `ClientStateCrossCheck.kt`). */
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

    /** Build the ext-info blocks for the players flagged this tick (as seen by [viewer]), in flag order. */
    private fun buildExtInfoBlocks(viewer: Player, flaggedForExtInfo: List<Int>): List<ByteArray> {
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val target = Players.get(slot) ?: continue
            extendedInfo.add(PlayerExtInfoEncoder.encodeExtendedInfoBlock(viewer, target))
        }
        return extendedInfo
    }
}
