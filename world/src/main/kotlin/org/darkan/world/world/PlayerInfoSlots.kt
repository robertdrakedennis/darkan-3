package org.darkan.world.world

import world.gregs.voidps.type.Tile

/**
 * Server-side GPI (Global-Player-Info) slot model — the **VisibilityManager** for one viewer's op22
 * PLAYER_INFO stream. It is the exact in-memory mirror of the client's op22 decode state, so the
 * [org.darkan.world.net.PlayerInfoEncoder] can emit a bit-stream the client reads back to the same
 * scene.
 *
 * ## The decode is the contract
 *
 * Every field, cohort and transition here mirrors the recorder oracle's verified decode in
 * `core/.../recorder/ClientStateCrossCheck.kt` (validated against live client memory — the
 * "client-is-king" reference). The pairing is one-to-one:
 *
 *  * [GpiSlot] ↔ the decode's `PlayerSlot` (`active`, `nextActive`, `present`, `coord`)
 *    (`ClientStateCrossCheck.kt:1275`).
 *  * [renderList] ↔ the decode's `renderList` — the **known / high-res** cohort iterated by the two
 *    known passes (`ClientStateCrossCheck.kt:1040`).
 *  * [pendingList] ↔ the decode's `pendingList` — the **external / low-res** cohort iterated by the
 *    two external passes (`ClientStateCrossCheck.kt:1041`).
 *  * [seedFromGpiPrefix] ↔ `resetFromGpiPrefix` (`ClientStateCrossCheck.kt:1047`): the op81 GPI
 *    prefix the client parsed at world entry seeds BOTH sides identically — local slot
 *    `active=false, present=true` into [renderList]; every other slot `active=((word>>18)&3)==0`,
 *    `present=false` into [pendingList]. The prefix seeds empty slots with the local player's map
 *    square and occupied slots with the occupant's map square, so low-res anchors never default to
 *    map square (0,0).
 *  * [rebuildAfterPasses] ↔ `rebuildActivityFlagsAndLists` (`ClientStateCrossCheck.kt:1259`): after
 *    the 4th pass, `active = nextActive` and `present` re-buckets each slot into [renderList] /
 *    [pendingList].
 *
 * ## Source of truth for the passes' `active` flag
 *
 * The encoder filters each pass by [GpiSlot.active] (NOT [org.darkan.world.entity.Player.active]).
 * The slot model is the authority for cohort membership because it is seeded from the SAME prefix
 * the client decoded; the per-`Player` `active` field is incidental simulation state. On spawn the
 * local player is therefore `active=false` here — matching the prefix — regardless of the `Player`
 * default.
 *
 * ## Increment scope (1.2b increment 1 — FOUNDATION)
 *
 * This increment seeds the model from the prefix and exposes the cohorts + the post-pass rebuild so
 * the encoder can produce the verified four-pass shape. Real walk/run/teleport mutation of
 * [GpiSlot.coord] / [GpiSlot.present] (the low-res add → high-res promote → external move flow) is
 * increment 2; the mutators it needs ([promoteToRender], [demoteToPending], [setCoord]) are wired
 * here but unused this increment.
 */
class PlayerInfoSlots(private val capacity: Int = SLOT_COUNT) {

    /**
     * One GPI slot — the server mirror of the decode's `PlayerSlot`. [active] selects which pass
     * processes this slot; [nextActive] accumulates the NEXT tick's active flag during a pass (the
     * decode sets it on every emitted/skipped slot) and is committed by [rebuildAfterPasses].
     * [present] decides [renderList] (high-res, present) vs [pendingList] (external, absent).
     * [coord] is the low-res region anchor the external passes add local offsets onto.
     */
    class GpiSlot(
        var active: Boolean,
        var present: Boolean,
        var coord: LowResCoord,
    ) {
        /** The active flag for the NEXT tick, accumulated during this tick's passes; committed by [rebuildAfterPasses]. */
        var nextActive: Boolean = false
    }

    /** Low-res region anchor: `plane` + region coords (`tile >> 6`). Mirrors the decode's `LowResCoord`. */
    data class LowResCoord(val plane: Int, val regionX: Int, val regionY: Int)

    private val slots = arrayOfNulls<GpiSlot>(capacity)

    /** Known / high-res cohort — the two known passes iterate this in order. Mirrors decode `renderList`. */
    val renderList: MutableList<Int> = ArrayList(capacity)

    /** External / low-res cohort — the two external passes iterate this in order. Mirrors decode `pendingList`. */
    val pendingList: MutableList<Int> = ArrayList(capacity)

    /** The local (viewer's own) slot — the only [renderList] member on a solo spawn. */
    var localIndex: Int = -1
        private set

    /** Returns the slot for [index], or null if it was never seeded (slot 0 / out of range). */
    fun slot(index: Int): GpiSlot? = slots.getOrNull(index)

    /**
     * Seed the model from the op81 GPI prefix the client parsed at world entry — the exact inverse of
     * `resetFromGpiPrefix` (`ClientStateCrossCheck.kt:1047`).
     *
     * The local slot is `active=false, present=true` (into [renderList]); every other slot is
     * `present=false` (into [pendingList]) with `active=true`. The coord mirrors the 20-bit prefix
     * word: occupied slots get that player's map square; empty slots get the local player's map
     * square rather than `(0,0)`.
     *
     * @param localTile the local player's spawn tile — its region (`tile >> 6`) anchors the local
     *                  slot's [LowResCoord], matching the 30-bit packed tile the prefix wrote.
     * @param localIndex the local player's allocated slot (1..capacity-1) — the slot the client's
     *                   prefix parser treats as local.
     */
    fun seedFromGpiPrefix(localTile: Tile, localIndex: Int) {
        require(localIndex in 1 until capacity) {
            "localIndex must be a real slot in 1..${capacity - 1}, was $localIndex"
        }
        this.localIndex = localIndex
        slots.fill(null)
        renderList.clear()
        pendingList.clear()

        slots[localIndex] = GpiSlot(
            active = false,
            present = true,
            coord = LowResCoord(localTile.level, localTile.x ushr 6, localTile.y ushr 6),
        )
        renderList += localIndex

        val defaultCoord = LowResCoord(localTile.level, localTile.x ushr 6, localTile.y ushr 6)

        // Every other slot mirrors Op81GpiPrefix's 20-bit-word seeding.
        for (idx in 1 until capacity) {
            if (idx == localIndex) continue
            val occupantTile = Players.get(idx)?.tile
            val coord = if (occupantTile != null) {
                LowResCoord(occupantTile.level, occupantTile.x ushr 6, occupantTile.y ushr 6)
            } else {
                defaultCoord
            }
            slots[idx] = GpiSlot(
                active = true,
                present = false,
                coord = coord,
            )
            pendingList += idx
        }
    }

    /**
     * Re-bucket every slot after the 4th pass — the exact inverse of `rebuildActivityFlagsAndLists`
     * (`ClientStateCrossCheck.kt:1259`): commit `active = nextActive`, reset `nextActive`, then place
     * `present` slots into [renderList] and the rest into [pendingList], preserving ascending index
     * order (the cohort order both sides iterate).
     */
    fun rebuildAfterPasses() {
        renderList.clear()
        pendingList.clear()
        for (idx in 1 until capacity) {
            val slot = slots[idx] ?: continue
            slot.active = slot.nextActive
            slot.nextActive = false
            if (slot.present) renderList += idx else pendingList += idx
        }
    }

    /**
     * Promote a pending (external/low-res) slot into the render (known/high-res) cohort — the model
     * side of the low-res "add → promote" transition (decode `decodeExternalPlayerUpdate` branch 0,
     * `ClientStateCrossCheck.kt:1209`). Wired for increment 2; unused this increment.
     */
    fun promoteToRender(index: Int) {
        slots[index]?.present = true
    }

    /**
     * Demote a known (high-res) slot back to the external (low-res) cohort — the model side of the
     * high-res "demote to low-res" transition (decode `decodeKnownPlayerUpdate` mvt=0 branch,
     * `ClientStateCrossCheck.kt:1170`). Wired for increment 2; unused this increment.
     */
    fun demoteToPending(index: Int) {
        slots[index]?.present = false
    }

    /**
     * Update a slot's low-res region anchor — the model side of the external move branches (plane /
     * region / large, decode `decodeExternalPlayerUpdate` branches 1-3,
     * `ClientStateCrossCheck.kt:1225`). Wired for increment 2; unused this increment.
     */
    fun setCoord(index: Int, coord: LowResCoord) {
        slots[index]?.coord = coord
    }

    companion object {
        /** Slot pool size — 2048 (slot 0 reserved). Matches the decode's `PLAYER_SLOT_COUNT`. */
        const val SLOT_COUNT: Int = 2048
    }
}
