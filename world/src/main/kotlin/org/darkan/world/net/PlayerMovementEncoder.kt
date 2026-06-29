package org.darkan.world.net

import org.darkan.world.entity.Direction8
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Single responsibility: encode the **per-target PLAYER_INFO position bits** for one cohort entry —
 * the high-res / low-res movement form AND the teleport / absolute-tile init form. It owns nothing
 * about cohorts, passes, or ext-info; the orchestrator ([PlayerInfoEncoder]) calls these per slot
 * once it has decided which form applies, and the ext-info gating is delegated to
 * [PlayerExtInfoEncoder].
 *
 * **Current behavior (preserved EXACTLY):** every live high-res update is the stationary form
 * `movementType=0`. No-update slots are run-length-encoded by the orchestrator's skip-run
 * ([PlayerInfoEncoder] — the single owner of the skip-count tail, the inverse of the decode's
 * `readPlayerSkipCount`). The teleport / absolute-tile form ([encodeAbsoluteTile]) is used ONLY by
 * the first-tick init path ([PlayerInfoEncoder.buildInit]); the live world-entry / per-tick path
 * never reaches it. None of these emit real walk/run motion.
 *
 * Wire references (relocated verbatim with their code — do not delete):
 *  * High-res form — `docs/net/serverprot/player-info-947-3.md` §4B `GetHighResolutionPlayerPosition`.
 *  * Low-res form — §4B `GetLowResolutionPlayerPosition`.
 *  * Teleport / absolute-tile init — `docs/protocol/world-bootstrap-948.md` §4.3 +
 *    `GetHighResolutionPlayerPosition @0x00154d30`.
 *
 * Phase 1.2b: real walk/run/teleport movement plugs in HERE. The extension seam is
 * [encodeHighResPosition] (today hardcoded `movementType=0`) and the skip-run gating in
 * [PlayerInfoEncoder]; [encodeAbsoluteTile] is the teleport primitive that path will consume.
 */
object PlayerMovementEncoder {

    /** No-movement form: the local-player / high-res update writes `movementType=0` (no walk/run). */
    private const val MOVEMENT_TYPE_NONE = 0

    /** Walk form: a single one-tile step — `movementType=1` then `[3-bit dir][1-bit hasFollowup]`. */
    private const val MOVEMENT_TYPE_WALK = 1

    /** Teleport / jump form: read an absolute 30-bit tile next. Used by the first-tick init path. */
    private const val MOVEMENT_TYPE_TELEPORT = 3

    /**
     * Local-player first-transmission high-res init (absolute-tile / teleport path), per
     * `docs/protocol/world-bootstrap-948.md` §4.3 and `GetHighResolutionPlayerPosition`:
     *   gBit(1)=1 hasUpdate ; gBit(1) hasExtInfo ; gBit(2)=3 movementType(teleport) ; gBit(30) tile.
     *
     * The 30-bit tile is `(plane<<28)|(x<<14)|y` ([world.gregs.voidps.type.Tile.id]) — this MUST equal
     * the op81 coord-header centre zone's tile (the coherence constraint that stops the quit).
     * `hasExtInfo` is set only when a real appearance blob exists (we do not fabricate the
     * undocumented appearance payload); when set, [local]'s index is appended to [flaggedForExtInfo]
     * so the orchestrator emits the ext-info block afterwards.
     *
     * Relocated unchanged from `PlayerInfoBuilder.encodeLocalPlayerInit` — byte output is identical.
     */
    fun encodeAbsoluteTile(
        out: BufferWriter,
        local: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(local)
        out.writeBits(1, 1)                                        // hasUpdate
        out.writeBits(1, if (hasExtInfo) 1 else 0)                 // hasExtendedInfo
        out.writeBits(2, MOVEMENT_TYPE_TELEPORT)                   // movementType = 3 (teleport → absolute tile)
        out.writeBits(30, local.tile.id)                          // absolute tile (plane<<28)|(x<<14)|y
        if (hasExtInfo) {
            flaggedForExtInfo.add(local.index)
        }
    }

    /**
     * High-res update form, per §4B `GetHighResolutionPlayerPosition` — the exact inverse of the
     * decode's `decodeKnownPlayerUpdate` (`core/.../recorder/ClientStateCrossCheck.kt:1245-1284`):
     * ```
     * 1 bit:  hasExtendedInfo
     * 2 bits: movementType  (0 = none, 1 = walk, 2 = run, 3 = teleport)
     * ```
     * The decode reads `hasExtendedInfo` first (line 1251), THEN the 2-bit `movementType` (line 1252),
     * so both branches below emit `[hasExt][mvt][…]` in that order.
     *
     * **WALK (`movementType=1`, increment 2a):** when the world tick applied a one-tile step this tick
     * ([Player.lastWalkStepDir] != [MovementQueue.NO_STEP]), emit `[hasExt][mvt=1][3-bit dir][1-bit
     * hasFollowup=0]` — the inverse of the mvt=1 branch (`ClientStateCrossCheck.kt:1260-1266`, which
     * reads `gBit(3)` then a 1-bit `hasFollowup`, reading 2 more bits only when set). A single step
     * emits `hasFollowup=0` and NO followup bits. The 3-bit dir is the verified [Direction8] index the
     * tick stored. `hasExt` is still set when the player also has deliverable ext-info this tick (its
     * index is appended to [flaggedForExtInfo]); the WALK bits and the ext-info bit are independent.
     *
     * **STATIONARY (`movementType=0`):** no step this tick → the unchanged hold form. When `hasExt=1`
     * no further movement bits follow (just new ext-info, index appended); when `hasExt=0` write the
     * 1-bit `demoteToLowRes=0` flag (unreachable for the per-tick path: any non-walking player reaching
     * here MUST have ext-info — guarded by [PlayerExtInfoEncoder.needsAnyUpdate]).
     *
     * increment 2b: run (`movementType=2`) and the demote-to-low-res transition plug in here.
     */
    fun encodeHighResPosition(
        out: BufferWriter,
        target: Player,
        flaggedForExtInfo: MutableList<Int>,
    ) {
        val hasExtInfo = PlayerExtInfoEncoder.hasFlaggableExtendedInfo(target)
        val walkDir = target.lastWalkStepDir

        if (walkDir != MovementQueue.NO_STEP) {
            // WALK: [hasExt][mvt=1][3-bit dir][1-bit hasFollowup=0]. Single step → no followup bits.
            out.writeBits(1, if (hasExtInfo) 1 else 0)
            out.writeBits(2, MOVEMENT_TYPE_WALK)
            out.writeBits(3, walkDir)
            out.writeBits(1, 0)                  // hasFollowup = 0 (single one-tile step)
            if (hasExtInfo) flaggedForExtInfo.add(target.index)
            return
        }

        out.writeBits(1, if (hasExtInfo) 1 else 0)
        out.writeBits(2, MOVEMENT_TYPE_NONE)  // movementType 0 — no movement.
        if (!hasExtInfo) {
            // movementType=0 && hasExtInfo=0: per §4B write 1-bit demoteToLowRes=0.
            out.writeBits(1, 0)
        } else {
            flaggedForExtInfo.add(target.index)
        }
    }

    /**
     * Low-res update form, per §4B `GetLowResolutionPlayerPosition`:
     * ```
     * 2 bits: updateType  (0 = promote to high-res, 1 = level change, 2 = small chunk, 3 = large multi-chunk)
     * ```
     * Today no low-res transitions happen; we emit `updateType=1` (level change, delta 0) as a safe
     * no-op. This path is currently unreachable because the low-res list never flags a needed update
     * (it folds into the skip-run), but is wired in for forward compatibility.
     *
     * Relocated unchanged from `PlayerInfoBuilder.encodeLowResPosition` — byte output is identical.
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
