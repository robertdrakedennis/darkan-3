package org.darkan.world.net

import org.darkan.world.entity.Direction8
import org.darkan.world.entity.Player
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Single responsibility: encode the **per-target PLAYER_INFO position bits** for one cohort entry —
 * the high-res move-state forms (walk-start / walk-step / walk-stop / stationary) AND the teleport /
 * absolute-tile init form. It owns nothing about cohorts, passes, ext-info, or the per-slot walk
 * latch; the orchestrator ([PlayerInfoEncoder]) decides the per-tick [WalkPhase] (from the slot's
 * `wasWalking` latch + whether the target stepped this tick) and calls the matching method, and the
 * ext-info gating is delegated to [PlayerExtInfoEncoder].
 *
 * ## The prod walk protocol (LOCAL-slot decode of a live-prod walk — the spec)
 *
 * Decoded from `:tools:walkExtInfo` over `session-20260630-033557-27478-production` (local idx 1160),
 * every walk segment is the SAME three-phase move-mode shape (4/4 segments consistent):
 *
 *  1. **WALK-START** (idle→walk, the first step): `mvt=3` small move-mode form, descriptor byte
 *     offset **0x8** (the WALK move-mode token a38), carrying the first step's ±1-tile signed-5 delta.
 *     Prod examples: tick11 `code15=0x201f` (S, yS5=-1), tick20 `0x2001` (N, yS5=+1). This sets the
 *     client's move-state to WALK → the avatar binds its walk seqs and animates.
 *  2. **WALK-STEP** (walk→walk, the middle steps): plain `mvt=1` = `[3-bit dir][1-bit followup=0]`.
 *  3. **WALK-STOP** (walk→idle, the tick AFTER the last step): `mvt=3` small move-mode form,
 *     descriptor byte offset **0x0** (the IDLE token a30), `code15=0x0`, NO move (tile unchanged).
 *     This returns the client to the idle move-state → frame-perfect stop. It is its OWN tick and IS
 *     an update (the orchestrator's `knownHasUpdate` returns true for it).
 *  4. **POSITION-ONLY**: zero movement ext-info on every walk/stop tick (no bit-0x20 MOVEMENT_ANIM,
 *     no bit-0x80 FORCED_MOVEMENT). prod's local slot carried 0 of each across all 30 walk steps.
 *
 * This is SERVER-DRIVEN for the local slot too (the earlier "local walk is client-predicted" premise
 * was falsified — the prior tools mis-identified the local slot as a remote player; reading the
 * authoritative local index from client memory revealed prod forces the local walk). So the local
 * player and any walker take the same three-phase encoding; there is no local special-case.
 *
 * ## The move-mode descriptor field → token mapping (RE-confirmed, route-node RE)
 *
 * The client reads the small-form descriptor table BYTE offset as `(code15 >> 10) & 0x1c`
 * (rs2client 948-5 @0x1000263cd) and the plane delta as `(code15 >> 10) & 3` (@0x100026477). The
 * byte-offset → move-mode-token mapping is `0x0` = IDLE (a30), `0x8` = WALK (a38), `0xc` = RUN (a3c)
 * — and `0x10` = the SMOOTH glide descriptor (a40, descriptor entry 4) used for a REMOTE smooth step.
 * To land on a given byte offset with a ZERO plane delta the 5-bit field (bits 14:10) must equal that
 * offset (`offset & 0x1c == offset` for these multiples of 4, `offset & 3 == 0`). So the field value
 * IS the byte offset: WALK-start uses field [MOVE_MODE_FIELD_WALK] (8), STOP uses field 0.
 *
 * Wire references:
 *  * the small/large move-mode form + `mvt=1` walk — the verified decode `decodeKnownPlayerUpdate`
 *    (`core/.../recorder/ClientStateCrossCheck.kt`) + rs2client 948-5
 *    `jag::packethandlers::PlayerInfo::DecodeKnownPlayerUpdate @0x100025640`.
 *  * teleport / absolute-tile init — `docs/protocol/world-bootstrap-948.md` §4.3.
 *
 * Run (`mvt=2` / move-mode RUN token a3c, byte offset 0xc) is a future increment; [encodeAbsoluteTile]
 * is the real-teleport primitive a teleport path will consume.
 */
object PlayerMovementEncoder {

    /** No-movement form: a stationary high-res update writes `movementType=0` (no walk/run). */
    private const val MOVEMENT_TYPE_NONE = 0

    /** `movementType=1` — the plain `[3-bit dir][1-bit followup]` walk-STEP form (the middle steps of a walk). */
    private const val MOVEMENT_TYPE_WALK = 1

    /**
     * Move-mode-descriptor form — `movementType=3`. prod uses the small (15-bit) variant of this form
     * for BOTH the WALK-START marker (descriptor byte offset 0x8 = WALK token a38, carrying the first
     * step's delta) and the WALK-STOP marker (descriptor byte offset 0x0 = IDLE token a30, no move).
     * The client routes `code15` through the move-mode descriptor table; the byte offset selects the
     * move-state token (see the class KDoc).
     */
    private const val MOVEMENT_TYPE_MOVE_MODE = 3

    /**
     * The small-form 5-bit descriptor-selector field (`code15` bits 14:10) for the **WALK move-mode
     * token a38** — byte offset 0x8 (`8 & 0x1c == 0x8`), plane delta 0 (`8 & 3 == 0`). This is the
     * WALK-START marker prod sends on the idle→walk tick; it carries the first step's signed-5 tile
     * delta and puts the client in the WALK move-state (→ walk anim). Prod-verified: tick11
     * `code15=0x201f` (`8<<10 | yS5(-1)` = a 1-tile-south walk-start), tick20 `0x2001` (north).
     */
    private const val MOVE_MODE_FIELD_WALK = 8

    /**
     * The small-form 5-bit descriptor-selector field for the **IDLE move-mode token a30** — byte
     * offset 0x0, plane delta 0, and NO tile delta. This is the WALK-STOP marker prod sends on the
     * walk→idle tick; `code15 == 0` returns the client to the idle move-state → frame-perfect stop.
     * Prod-verified: ticks 13/26/48/69 `code15=0x0`.
     */
    private const val MOVE_MODE_FIELD_IDLE = 0

    /** Teleport / jump form: read an absolute 30-bit tile next. Real-teleport seam (NOT on any current build path). */
    private const val MOVEMENT_TYPE_TELEPORT = 3

    /**
     * The per-tick walk phase the orchestrator resolves for a high-res slot from its `wasWalking` latch
     * and whether the target stepped this tick. Each maps to one of the encoder's high-res forms.
     */
    enum class WalkPhase {
        /** Not walking and did not step → stationary `mvt=0` hold (or the inline-appearance add). */
        NONE,

        /** Idle→walk (`!wasWalking && stepped`) → WALK-START marker `mvt=3` desc 0x8 + the step's delta. */
        START,

        /** Walk→walk (`wasWalking && stepped`) → plain `mvt=1` `[dir][followup=0]`. */
        STEP,

        /** Walk→idle (`wasWalking && !stepped`) → WALK-STOP marker `mvt=3` desc 0x0, `code15=0`, no move. */
        STOP,
    }

    /**
     * Local-player absolute-tile / teleport high-res form, per `docs/protocol/world-bootstrap-948.md`
     * §4.3 and `GetHighResolutionPlayerPosition`:
     *   gBit(1)=1 hasUpdate ; gBit(1) hasExtInfo ; gBit(2)=3 movementType(teleport) ; gBit(30) tile.
     *
     * **NOT on any current build path.** Retained as the real-teleport primitive a teleport path will
     * consume (the world-entry add uses the stationary inline-appearance form, not this).
     *
     * The 30-bit tile is `(plane<<28)|(x<<14)|y` ([world.gregs.voidps.type.Tile.id]). `hasExtInfo` is
     * set only when a real appearance blob exists; when set, [local]'s index is appended to
     * [flaggedForExtInfo] so the orchestrator emits the ext-info block afterwards.
     */
    fun encodeAbsoluteTile(
        out: BufferWriter,
        viewer: Player,
        local: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(viewer, local)
        out.writeBits(1, 1)                                        // hasUpdate
        out.writeBits(1, if (hasExtInfo) 1 else 0)                 // hasExtendedInfo
        out.writeBits(2, MOVEMENT_TYPE_TELEPORT)                   // movementType = 3 (teleport → absolute tile)
        out.writeBits(30, local.tile.id)                          // absolute tile (plane<<28)|(x<<14)|y
        if (hasExtInfo) {
            flaggedForExtInfo.add(local.index)
        }
    }

    /**
     * High-res update form — the exact inverse of the decode's `decodeKnownPlayerUpdate`
     * (`core/.../recorder/ClientStateCrossCheck.kt`):
     * ```
     * 1 bit:  hasExtendedInfo
     * 2 bits: movementType  (0 = none, 1 = walk, 2 = run, 3 = move-mode)
     * ```
     * The decode reads `hasExtendedInfo` first, THEN the 2-bit `movementType`, so every branch below
     * emits `[hasExt][mvt][…]` in that order. The [phase] (resolved by the orchestrator from the slot's
     * walk latch) selects the form; this is SERVER-DRIVEN for every walker including the local slot
     * (see the class KDoc — the prod local walk decode is the spec).
     *
     * ## The three walk forms (POSITION-ONLY — no movement ext-info)
     *
     *  * [WalkPhase.START] → WALK-START marker: `[hasExt][mvt=3][large=0][code15]` with
     *    `code15 = (8<<10) | (xS5<<5) | yS5` (descriptor byte offset 0x8 = WALK token a38 + the ±1-tile
     *    signed-5 delta). Sets the client's WALK move-state and applies the first step.
     *  * [WalkPhase.STEP] → `[hasExt][mvt=1][dir:3][followup:1=0]` (the middle steps).
     *  * [WalkPhase.STOP] → WALK-STOP marker: `[hasExt][mvt=3][large=0][code15=0]` (descriptor byte
     *    offset 0x0 = IDLE token a30, no move). Returns the client to idle → frame-perfect stop.
     *  * [WalkPhase.NONE] → stationary `mvt=0` hold (the local first-tick inline-appearance add takes
     *    this too: `[hasExt=1][mvt=0]` then the APPEARANCE block; a non-local hold with `hasExt=0`
     *    writes the trailing `demoteToLowRes=0` bit).
     *
     * `hasExt` is independent of [phase] and set only when the slot also has deliverable ext-info this
     * tick (index appended to [flaggedForExtInfo]). prod's walk/stop ticks carry NO ext-info.
     */
    fun encodeHighResPosition(
        out: BufferWriter,
        viewer: Player,
        target: Player,
        phase: WalkPhase,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(viewer, target)

        when (phase) {
            WalkPhase.START -> {
                // WALK-START: [hasExt][mvt=3][large=0][code15] with descriptor byte offset 0x8 (WALK
                // token a38) + the first step's ±1-tile signed-5 delta. Position-only.
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_MOVE_MODE)
                out.writeBits(1, 0)                                  // large = 0 → 15-bit small form
                out.writeBits(15, walkStartCode15(target.lastWalkStepDir))
                if (hasExtInfo) flaggedForExtInfo.add(target.index)
                return
            }
            WalkPhase.STEP -> {
                // WALK-STEP: [hasExt][mvt=1][dir:3][followup:1=0]. The middle steps of a walk.
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_WALK)
                out.writeBits(3, target.lastWalkStepDir)
                out.writeBits(1, 0)                                  // hasFollowup = 0 (single 1-tile step)
                if (hasExtInfo) flaggedForExtInfo.add(target.index)
                return
            }
            WalkPhase.STOP -> {
                // WALK-STOP: [hasExt][mvt=3][large=0][code15=0]. Descriptor byte offset 0x0 (IDLE token
                // a30), no move — returns the client to the idle move-state. Position-only.
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_MOVE_MODE)
                out.writeBits(1, 0)                                  // large = 0 → 15-bit small form
                out.writeBits(15, MOVE_MODE_FIELD_IDLE shl 10)       // code15 = 0 (idle, no move)
                if (hasExtInfo) flaggedForExtInfo.add(target.index)
                return
            }
            WalkPhase.NONE -> {
                // Stationary hold — and the local first-tick inline-appearance add (hasExt=1 + APPEARANCE).
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_NONE)                 // movementType 0 — no movement.
                if (!hasExtInfo) {
                    // movementType=0 && hasExtInfo=0: write 1-bit demoteToLowRes=0.
                    out.writeBits(1, 0)
                } else {
                    flaggedForExtInfo.add(target.index)
                }
            }
        }
    }

    /**
     * Build the 15-bit small-form `code15` for the WALK-START marker of a one-tile step in
     * [Direction8] index [walkDir]. Layout (the inverse of the binary's small-form decode):
     * `(field << 10) | ((xS5 & 0x1f) << 5) | (yS5 & 0x1f)`, with `field` = [MOVE_MODE_FIELD_WALK] (8 →
     * descriptor byte offset 0x8 = WALK token a38, plane delta 0), and `xS5`/`yS5` the signed-5 tile
     * deltas (each ±1 for a one-tile step; 512 fine = 1 tile in the client). Each sub-field is masked
     * so the result is a clean non-negative 15-bit int (`BufferWriter.writeBits` masks the value).
     *
     * Prod-verified (`session-20260630-033557-27478-production`, local idx 1160): a SOUTH start
     * (DX=0,DY=-1) → `0x201f` (== prod tick11/tick33); a NORTH start (DX=0,DY=+1) → `0x2001`
     * (== prod tick20/tick58). The decode recovers descriptor byte offset `(0x201f>>10)&0x1c == 0x8`.
     */
    private fun walkStartCode15(walkDir: Int): Int {
        val xTiles = Direction8.DX[walkDir]   // ±1 tile (or 0); 512 fine == 1 tile
        val yTiles = Direction8.DY[walkDir]
        return ((MOVE_MODE_FIELD_WALK and 0x1f) shl 10) or
            ((xTiles and 0x1f) shl 5) or
            (yTiles and 0x1f)
    }

    /**
     * Low-res update form, per §4B `GetLowResolutionPlayerPosition`:
     * ```
     * 2 bits: updateType  (0 = promote to high-res, 1 = level change, 2 = small chunk, 3 = large multi-chunk)
     * ```
     * Today no low-res transitions happen; we emit `updateType=1` (level change, delta 0) as a safe
     * no-op. This path is currently unreachable because the low-res list never flags a needed update
     * (it folds into the skip-run), but is wired in for forward compatibility.
     */
    fun encodeLowResPosition(
        out: BufferWriter,
        @Suppress("UNUSED_PARAMETER") target: Player,
        @Suppress("UNUSED_PARAMETER") flaggedForExtInfo: MutableList<Int>,
    ) {
        out.writeBits(2, 1)   // updateType=1 (level change only)
        out.writeBits(2, 0)   // levelDelta=0
    }
}
