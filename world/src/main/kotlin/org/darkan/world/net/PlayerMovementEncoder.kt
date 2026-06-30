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
 * Run (`mvt=2` / move-mode RUN token a3c, byte offset 0xc) is implemented: run-START
 * ([WalkPhase.RUN_START], move-mode desc 0xc + first-tile delta) → RUN-STEP ([WalkPhase.RUN_STEP],
 * `mvt=2` + 4-bit `runCode`, 2 tiles/tick) → run-STOP ([WalkPhase.RUN_STOP], == walk-STOP idle 0x0),
 * with a `mvt=1` WALK handoff on the odd last tile. [encodeAbsoluteTile] is the real-teleport primitive
 * a teleport path will consume.
 */
object PlayerMovementEncoder {

    /** No-movement form: a stationary high-res update writes `movementType=0` (no walk/run). */
    private const val MOVEMENT_TYPE_NONE = 0

    /**
     * Low-res external `updateType=0` — the ADD / promote-to-high-res branch selector (decode
     * `decodeExternalPlayerUpdate` branch 0, `ClientStateCrossCheck.kt`). Distinct name from
     * [MOVEMENT_TYPE_NONE] (same value 0, different bit field: this is the 2-bit external selector, that
     * is the 2-bit high-res movementType) so the two never read as interchangeable.
     */
    private const val LOW_RES_UPDATE_TYPE_ADD = 0

    /**
     * Low-res external `updateType=1` — the PLANE-ONLY region move (decode `decodeExternalPlayerUpdate`
     * branch 1, `ClientStateCrossCheck.kt:1824`). Re-anchors only the slot coord's plane; region X/Y
     * unchanged. Body: `[2-bit planeDelta]`, `plane = (plane + planeDelta) & 3`.
     */
    private const val LOW_RES_UPDATE_TYPE_REGION_PLANE = 1

    /**
     * Low-res external `updateType=2` — the SINGLE-STEP region move (decode branch 2,
     * `ClientStateCrossCheck.kt:1830`). Re-anchors the slot coord by one Chebyshev-1 region step.
     * Body: `[5-bit packed]` = `(planeDelta<<3) | dir8`; the client adds `PLAYER_REGION_DX/DY[dir8]`
     * ([Direction8]) to the region coord.
     */
    private const val LOW_RES_UPDATE_TYPE_REGION_STEP = 2

    /**
     * Low-res external `updateType=3` — the LARGE / arbitrary region move (decode branch 3,
     * `ClientStateCrossCheck.kt:1843`). Re-anchors the slot coord by an arbitrary 8-bit-wrapping region
     * delta — the late-join / teleport-across-regions case. Body: `[20-bit packed]` =
     * `(planeDelta<<16) | (dRegX<<8) | dRegY`.
     */
    private const val LOW_RES_UPDATE_TYPE_REGION_LARGE = 3

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
     * The small-form 5-bit descriptor-selector field for the **RUN move-mode token a3c** — byte offset
     * 0xc (`0xc & 0x1c == 0xc`), plane delta 0 (`0xc & 3 == 0`). This is the run-START marker the server
     * sends on the idle→run tick; it carries the FIRST step's signed-5 tile delta (the run's first tile)
     * and puts the client in the RUN move-state. Mirrors [MOVE_MODE_FIELD_WALK] (8) exactly, one ring
     * out — RE-confirmed run token a3c, byte offset 0xc (rs2client 948-5 @0x1000263cd descriptor table).
     */
    private const val MOVE_MODE_FIELD_RUN = 0xc

    /**
     * High-res `movementType=2` — the plain `[2-bit mvt=0b10][4-bit runCode]` RUN-STEP form (the 2-tile/
     * tick middle steps of a run). `runCode` indexes the perimeter of the 5×5 box (every entry Chebyshev-
     * distance 2); the encoder maps the combined 2-tile delta to it via [runStepCode] (the inverse of the
     * decode's [RUN_DX]/[RUN_DY] table). RE-verified `DecodeKnownPlayerUpdate @0x100025640`.
     */
    private const val MOVEMENT_TYPE_RUN = 2

    /**
     * The verified 16-entry RUN step table — `runCode` (0..15) → tile delta (a verbatim copy of the
     * recorder oracle's `RUN_DX`/`RUN_DY`, `core/.../recorder/ClientStateCrossCheck.kt`). The op22
     * RUN-STEP form ([encodeHighResPosition] [WalkPhase.RUN_STEP]) emits the `runCode` whose
     * `(RUN_DX[code], RUN_DY[code])` equals the combined 2-tile delta. The decode reads the SAME table,
     * so this is its exact inverse — every entry is Chebyshev-distance 2 (the run advances 2 tiles/tick).
     */
    private val RUN_DX = intArrayOf(-2, -1, 0, 1, 2, -2, 2, -2, 2, -2, 2, -2, -1, 0, 1, 2)
    private val RUN_DY = intArrayOf(-2, -2, -2, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 2, 2, 2)

    /**
     * The per-tick movement phase the orchestrator resolves for a high-res slot from its `wasWalking` /
     * `wasRunning` latch and whether the target walk- or run-stepped this tick. Each maps to one of the
     * encoder's high-res forms. The WALK arms are the prod three-phase walk shape; the RUN arms mirror
     * them exactly one ring out (byte offset 0xc vs 0x8; run-STOP shares the idle 0x0 stop with walk).
     */
    enum class WalkPhase {
        /** Not moving and did not step → stationary `mvt=0` hold (or the inline-appearance add). */
        NONE,

        /** Idle→walk (`!wasMoving && walkStepped`) → WALK-START marker `mvt=3` desc 0x8 + the step's delta. */
        START,

        /** Walk→walk (`wasWalking && walkStepped`) → plain `mvt=1` `[dir][followup=0]`. */
        STEP,

        /** Walk→idle (`wasWalking && !stepped`) → WALK-STOP marker `mvt=3` desc 0x0, `code15=0`, no move. */
        STOP,

        /** Idle→run (`!wasMoving && runStepped`) → run-START marker `mvt=3` desc 0xc + the first step's delta. */
        RUN_START,

        /** Run→run (`wasRunning && runStepped`) → RUN-STEP `mvt=2` `[runCode:4]` (2 tiles/tick). */
        RUN_STEP,

        /** Run→idle (`wasRunning && !stepped`) → run-STOP marker `mvt=3` desc 0x0 (== walk-STOP, idle), no move. */
        RUN_STOP,
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
     *  * [WalkPhase.RUN_START] → run-START marker: `[hasExt][mvt=3][large=0][code15]` with
     *    `code15 = (0xc<<10) | (xS5<<5) | yS5` (descriptor byte offset 0xc = RUN token a3c + the first
     *    step's ±1-tile signed-5 delta). Sets the client's RUN move-state and applies the first tile.
     *  * [WalkPhase.RUN_STEP] → `[hasExt][mvt=2][runCode:4]` — the 2-tile/tick middle steps; [runCode]
     *    ([runStepCode]) is the inverse of the [RUN_DX]/[RUN_DY] table for the combined 2-tile delta.
     *  * [WalkPhase.RUN_STOP] → run-STOP marker: identical to [WalkPhase.STOP] (`mvt=3` desc 0x0, no
     *    move) — the run returns to idle through the SAME idle token a30.
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
                val wd = target.lastWalkStepDir
                out.writeBits(15, moveModeStartCode15(MOVE_MODE_FIELD_WALK, Direction8.DX[wd], Direction8.DY[wd]))
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
            WalkPhase.RUN_START -> {
                // run-START: [hasExt][mvt=3][large=0][code15] with descriptor byte offset 0xc (RUN token
                // a3c) + the first run step's 2-tile signed-5 delta. Mirrors WALK-START one ring out
                // (0xc vs 0x8) but carries the FULL 2-tile delta (a run advances 2 tiles/tick from its
                // first tick). Sets the client's RUN move-state. Position-only. Prod-verified: an east
                // 2-tile run-START → code15 0x3040 (byteOff 0xc, xS5=2).
                val runCode = target.lastRunDelta
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_MOVE_MODE)
                out.writeBits(1, 0)                                  // large = 0 → 15-bit small form
                out.writeBits(15, moveModeStartCode15(MOVE_MODE_FIELD_RUN, RUN_DX[runCode], RUN_DY[runCode]))
                if (hasExtInfo) flaggedForExtInfo.add(target.index)
                return
            }
            WalkPhase.RUN_STEP -> {
                // RUN-STEP: [hasExt][mvt=2][runCode:4]. The 2-tile/tick middle steps; runCode is the
                // inverse of the verified RUN_DX/RUN_DY table for the combined 2-tile delta the world
                // tick recorded (target.lastRunDelta). Position-only.
                out.writeBits(1, if (hasExtInfo) 1 else 0)
                out.writeBits(2, MOVEMENT_TYPE_RUN)
                out.writeBits(4, target.lastRunDelta)
                if (hasExtInfo) flaggedForExtInfo.add(target.index)
                return
            }
            WalkPhase.RUN_STOP -> {
                // run-STOP: identical to WALK-STOP — [hasExt][mvt=3][large=0][code15=0], descriptor byte
                // offset 0x0 (IDLE token a30), no move. The run returns to idle through the same idle
                // token. Position-only.
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
     * Build the 15-bit small-form `code15` for a move-mode-START marker carrying tile delta `(xTiles,
     * yTiles)`, using descriptor-selector [field]. Layout (the inverse of the binary's small-form
     * decode): `(field << 10) | ((xS5 & 0x1f) << 5) | (yS5 & 0x1f)`, with `field` the byte offset
     * (= [MOVE_MODE_FIELD_WALK] 8 for a WALK-START, [MOVE_MODE_FIELD_RUN] 0xc for a run-START; both have
     * plane delta 0 since `field & 3 == 0`), and `xS5`/`yS5` the signed-5 tile deltas (512 fine = 1 tile
     * in the client). Each sub-field is masked so the result is a clean non-negative 15-bit int
     * (`BufferWriter.writeBits` masks the value).
     *
     * The carried delta differs by mode: a WALK-START carries the ±1-tile first walk step; a run-START
     * carries the full **2-tile** first run step (the run advances 2 tiles/tick from its very first
     * tick). Both fit the signed-5 field (range ±16).
     *
     * Prod-verified for WALK (`session-20260630-033557-27478-production`, local idx 1160): a SOUTH start
     * (dx=0,dy=-1) → `0x201f` (== prod tick11/tick33); a NORTH start (dx=0,dy=+1) → `0x2001`
     * (== prod tick20/tick58). The decode recovers descriptor byte offset `(0x201f>>10)&0x1c == 0x8`.
     * Prod-verified for RUN (same capture, running remote slot 894/898): an EAST 2-tile run-START
     * (dx=2,dy=0) → `0x3040` (== remote tick48, byteOff 0xc); a (0,-2) run-START → `0x301e` (tick63).
     */
    private fun moveModeStartCode15(field: Int, xTiles: Int, yTiles: Int): Int {
        return ((field and 0x1f) shl 10) or
            ((xTiles and 0x1f) shl 5) or
            (yTiles and 0x1f)
    }

    /**
     * Reverse lookup `(dx,dy)` → 4-bit `runCode` — the inverse of the verified [RUN_DX]/[RUN_DY] run
     * step table. Given the combined 2-tile delta of a RUN tick, returns the `runCode` the RUN-STEP
     * `mvt=2` form emits (the decode reads the same table). `dx`/`dy` MUST be a Chebyshev-distance-2
     * perimeter delta (`max(|dx|,|dy|) == 2`, both in `-2..2`); a delta that is not on the 5×5 box
     * perimeter has no `runCode` and throws (the world tick only feeds summed two-one-tile-step deltas,
     * which are always Chebyshev-2 — see [org.darkan.world.entity.MovementQueue.pollRunStep]).
     */
    fun runStepCode(dx: Int, dy: Int): Int {
        for (code in RUN_DX.indices) {
            if (RUN_DX[code] == dx && RUN_DY[code] == dy) return code
        }
        throw IllegalArgumentException(
            "no run-code for 2-tile delta (dx=$dx, dy=$dy); a RUN step delta must be on the 5×5 box " +
                "perimeter (Chebyshev distance 2: dx,dy in -2..2 and max(|dx|,|dy|)==2)"
        )
    }

    /**
     * Low-res ADD form (`updateType=0`) — the exact inverse of the decode's
     * `decodeExternalPlayerUpdate` branch 0 (`core/.../recorder/ClientStateCrossCheck.kt`).
     * This is the "low-res add → promote to high-res" transition that makes an in-range remote enter the
     * viewer's render cohort (next tick the known passes drive it). Read the decode literally, MSB-first:
     *
     * ```
     * 2 bits: updateType = 0          // ADD (the branch selector)
     * 1 bit:  jumpFlag                // if set the client recurses decodeExternalPlayerUpdate FIRST; we
     *                                 //   emit 0 (the minimal ADD — no preceding low-res move chained in)
     * 6 bits: localX                  // tile.x - (coord.regionX << 6), 0..63 within the region anchor
     * 6 bits: localY                  // tile.y - (coord.regionY << 6), 0..63
     * 1 bit:  hasExtendedInfo         // an ext-info block follows for this slot this tick
     * ```
     *
     * The decode reconstructs the slot's absolute tile as
     * `Tile((coord.regionX<<6)+localX, (coord.regionY<<6)+localY, coord.plane)` and sets `slot.present =
     * true` (→ the rebuild moves it into `renderList`). [regionX]/[regionY]/[plane] are the slot's
     * low-res region anchor ([org.darkan.world.world.PlayerInfoSlots.LowResCoord], seeded by the op81
     * prefix); [tileX]/[tileY] are the remote's live absolute tile. The caller MUST have verified the
     * remote sits inside this region (same `tile>>6`) so `localX`/`localY` fit in 6 bits — a remote in a
     * different region than its low-res anchor needs a region-move branch first (a later increment); for
     * now the visibility gate ([PlayerInfoEncoder]) only adds remotes whose live region equals the
     * anchor seeded at world entry.
     *
     * `hasExtInfo` is set (and [target]'s index appended to [flaggedForExtInfo]) only when the slot has a
     * deliverable ext-info block this tick — which for a freshly-promoted remote is its first APPEARANCE
     * delivery to this viewer ([PlayerExtInfoEncoder.hasFlaggableExtendedInfo]). That is what makes the
     * newly-rendered avatar actually draw with its body/kit.
     */
    fun encodeLowResAdd(
        out: BufferWriter,
        viewer: Player,
        target: Player,
        regionX: Int,
        regionY: Int,
        plane: Int,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val localX = target.tile.x - (regionX shl 6)
        val localY = target.tile.y - (regionY shl 6)
        require(localX in 0..63 && localY in 0..63) {
            "low-res ADD for slot ${target.index}: tile (${target.tile.x},${target.tile.y}) is not inside " +
                "region anchor ($regionX,$regionY) — localX=$localX localY=$localY out of 0..63; a region-move " +
                "branch is required before the add"
        }
        require(plane == target.tile.level) {
            "low-res ADD for slot ${target.index}: anchor plane $plane != target plane ${target.tile.level}"
        }
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(viewer, target)
        out.writeBits(2, LOW_RES_UPDATE_TYPE_ADD)     // updateType = 0 (ADD / promote to high-res)
        out.writeBits(1, 0)                           // jumpFlag = 0 (no chained low-res move before the add)
        out.writeBits(6, localX)                      // local X within the region anchor
        out.writeBits(6, localY)                      // local Y within the region anchor
        out.writeBits(1, if (hasExtInfo) 1 else 0)    // hasExtendedInfo
        if (hasExtInfo) flaggedForExtInfo.add(target.index)
    }

    /**
     * Low-res **re-anchoring ADD** — the chained `jumpFlag=1` variant of branch 0 of
     * `decodeExternalPlayerUpdate` (`core/.../recorder/ClientStateCrossCheck.kt:1807-1822`). It first
     * emits a region-move ([encodeLowResRegionMove], the inverse of branches 1-3) to RE-ANCHOR the
     * slot's low-res coord to the remote's CURRENT region, then emits the 6+6 localX/localY relative to
     * that re-anchored region + the `hasExt` bit — exactly the decode's
     * `if (bits.readBits(1) != 0) decodeExternalPlayerUpdate(...)` recursion (the jumpFlag bit at branch
     * 0). This is what makes a late-joiner (slot seeded with the LOCAL player's region, never the
     * joiner's) and a low-res region-mover (seeded, then walked to a new region) get added at all —
     * without it the bare ADD's 6-bit local offsets cannot reach a tile outside the seeded anchor.
     *
     * Wire (MSB-first), mirroring the decode literally:
     * ```
     * 2 bits: updateType = 0          // ADD branch selector
     * 1 bit:  jumpFlag = 1            // → the client recurses decodeExternalPlayerUpdate FIRST
     *   <region-move bits>           // branch 1/2/3 — updates slot.coord to the remote's region
     * 6 bits: localX                  // tile.x - (newRegionX << 6), now in 0..63
     * 6 bits: localY                  // tile.y - (newRegionY << 6)
     * 1 bit:  hasExtendedInfo
     * ```
     *
     * The decode applies the chained region-move to `slot.coord` BEFORE reading localX/localY, so the
     * local offsets MUST be relative to [newRegionX]/[newRegionY] (the remote's live region) — which is
     * exactly what they are here. The caller ([org.darkan.world.world.PlayerInfoSlots.setCoord]) updates
     * the SERVER's mirror of `slot.coord` to the same region so both sides stay identical. [fromRegionX]/
     * [fromRegionY]/[fromPlane] are the slot's CURRENT (stale) anchor that the region-move deltas are
     * added onto; [newRegionX]/[newRegionY] are the remote's live region (`tile>>6`).
     */
    fun encodeLowResReanchoringAdd(
        out: BufferWriter,
        viewer: Player,
        target: Player,
        fromRegionX: Int,
        fromRegionY: Int,
        fromPlane: Int,
        newRegionX: Int,
        newRegionY: Int,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val newPlane = target.tile.level
        val localX = target.tile.x - (newRegionX shl 6)
        val localY = target.tile.y - (newRegionY shl 6)
        require(localX in 0..63 && localY in 0..63) {
            "re-anchoring ADD for slot ${target.index}: tile (${target.tile.x},${target.tile.y}) is not " +
                "inside the NEW region anchor ($newRegionX,$newRegionY) — localX=$localX localY=$localY out " +
                "of 0..63 (newRegion must be tile>>6)"
        }
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(viewer, target)
        out.writeBits(2, LOW_RES_UPDATE_TYPE_ADD)     // updateType = 0 (ADD)
        out.writeBits(1, 1)                           // jumpFlag = 1 → chained region-move follows
        encodeLowResRegionMove(
            out,
            fromRegionX = fromRegionX, fromRegionY = fromRegionY, fromPlane = fromPlane,
            toRegionX = newRegionX, toRegionY = newRegionY, toPlane = newPlane,
        )
        out.writeBits(6, localX)                      // local X within the NEW region anchor
        out.writeBits(6, localY)                      // local Y within the NEW region anchor
        out.writeBits(1, if (hasExtInfo) 1 else 0)    // hasExtendedInfo
        if (hasExtInfo) flaggedForExtInfo.add(target.index)
    }

    /**
     * Low-res **region-move** — the exact inverse of `decodeExternalPlayerUpdate` branches 1/2/3
     * (`core/.../recorder/ClientStateCrossCheck.kt:1824-1854`). Re-anchors a low-res slot's region
     * coord from `(fromRegionX, fromRegionY, fromPlane)` to `(toRegionX, toRegionY, toPlane)`, choosing
     * the smallest branch that can express the delta. Region coords are 8-bit (`& 0xff`); the decode
     * ADDS the deltas onto the current coord and masks, so the encoder emits `(to - from) & 0xff`.
     *
     * Branch selection (mirroring the three decode branches):
     *  * **branch 1** `[2:1][2:planeDelta]` — region X and Y unchanged, only the plane differs. The
     *    decode reads a 2-bit plane delta and does `plane = (plane + delta) & 3`. (Decode line 1824-1828.)
     *  * **branch 2** `[2:2][5:packed]` — a single Chebyshev-1 region step. `packed = (planeDelta<<3) |
     *    dir8`, where `dir8` is the [Direction8] index whose `(DX,DY)` equals the region delta (both in
     *    -1..1, not both 0). The decode reads `direction = packed & 0x7`, `planeDelta = packed >> 3`,
     *    `regionX += PLAYER_REGION_DX[direction]`, `regionY += PLAYER_REGION_DY[direction]`. The
     *    [Direction8] table IS the decode's `PLAYER_REGION_DX/DY`. (Decode line 1830-1842.)
     *  * **branch 3** `[2:3][20:packed]` — an arbitrary region jump (the late-join case). `packed =
     *    (planeDelta<<16) | (dRegX<<8) | dRegY`, all 8-bit wrapping deltas. The decode reads
     *    `planeDelta = (packed>>16)&3`, `regionX += (packed>>8)&0xff`, `regionY += packed&0xff`, each
     *    `& 0xff`. (Decode line 1843-1853.)
     *
     * planeDelta is `(toPlane - fromPlane) & 0x3` (the decode masks plane to 2 bits). NOTE branch 2's
     * planeDelta is also masked to 2 bits in the decode (`packed >> 3` of a 5-bit field = 2 bits), so a
     * branch-2 move can carry a plane change too; we use branch 2 only when the region delta is a single
     * step regardless of plane, and fall to branch 3 otherwise.
     */
    fun encodeLowResRegionMove(
        out: BufferWriter,
        fromRegionX: Int,
        fromRegionY: Int,
        fromPlane: Int,
        toRegionX: Int,
        toRegionY: Int,
        toPlane: Int,
    ) {
        val dRegX = (toRegionX - fromRegionX) and 0xff
        val dRegY = (toRegionY - fromRegionY) and 0xff
        val planeDelta = (toPlane - fromPlane) and 0x3

        // Signed single-step region delta (for the branch-2 Chebyshev-1 test). Region coords wrap at
        // 8 bits in the decode, but a real adjacent-region move is ±1 with no wrap, so compare the raw
        // signed difference.
        val sdx = toRegionX - fromRegionX
        val sdy = toRegionY - fromRegionY

        when {
            dRegX == 0 && dRegY == 0 -> {
                // Branch 1: plane-only change (region unchanged). planeDelta may be 0 (a no-op move);
                // we still emit it because the caller only invokes a region-move when SOMETHING differs,
                // and a 0/0/0 move round-trips as identity.
                out.writeBits(2, LOW_RES_UPDATE_TYPE_REGION_PLANE)
                out.writeBits(2, planeDelta)
            }
            sdx in -1..1 && sdy in -1..1 -> {
                // Branch 2: a single 8-direction region step (+ optional plane delta).
                val dir = Direction8.indexOf(sdx, sdy)
                out.writeBits(2, LOW_RES_UPDATE_TYPE_REGION_STEP)
                out.writeBits(5, (planeDelta shl 3) or dir)
            }
            else -> {
                // Branch 3: arbitrary region jump (the late-join / large-move case). 8-bit wrapping
                // deltas — exactly the values the decode adds back and masks.
                out.writeBits(2, LOW_RES_UPDATE_TYPE_REGION_LARGE)
                out.writeBits(20, (planeDelta shl 16) or (dRegX shl 8) or dRegY)
            }
        }
    }
}
