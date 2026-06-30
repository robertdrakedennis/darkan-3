package org.darkan.world.world

import org.darkan.world.entity.Player
import world.gregs.voidps.type.Tile

/**
 * Per-player visibility state read each tick by the PLAYER_INFO / NPC_INFO builders
 * in B6. Holds the high-res / low-res cohorts for PLAYER_INFO (op 27) per A4
 * §"List structure" and the visible-NPC cohort for NPC_INFO (op 12) per A5 Phase 1.
 *
 * Lifecycle:
 *  * On the very first tick after login, [firstTick] is true and the builder emits
 *    the "init form" of PLAYER_INFO with 18-bit region hashes for every low-res
 *    player (per A4 §"First tick / init payload"). The builder clears [firstTick]
 *    after the init payload is appended.
 *  * Subsequent ticks emit the incremental form (1-bit gating + per-cohort movement).
 *
 * The high-res list MUST always start with the owner at position 0 (the client's
 * "local player" slot); the builder validates this invariant.
 */
class Viewport(val owner: Player) {
    /** Players currently in the owner's high-res cohort. Owner is always element [0]. */
    val highResIndices: MutableList<Int> = mutableListOf(owner.index)

    /** Players currently in the owner's low-res cohort (visible region, not yet promoted to high-res). */
    val lowResIndices: MutableList<Int> = mutableListOf()

    /**
     * The op22 PLAYER_INFO **slot model** (VisibilityManager) — the exact server mirror of the
     * client's GPI decode state. Seeded from the op81 GPI prefix by [resetAfterGpiPrefix] and driven
     * by [org.darkan.world.net.PlayerInfoEncoder] each tick. Its
     * [PlayerInfoSlots.renderList]/[PlayerInfoSlots.pendingList] cohorts and per-slot `active` flag
     * are the SOURCE OF TRUTH for the four GPI passes (the encoder filters on the slot's `active`,
     * not [Player.active]). See [PlayerInfoSlots] for the decode-contract mapping.
     */
    val playerSlots: PlayerInfoSlots = PlayerInfoSlots()

    /**
     * Cached encoded appearance hash per other-player slot. Indexed by player index
     * (0..2047). Builder compares against the latest appearance bytes to decide
     * whether to re-emit the APPEARANCE block this tick.
     */
    val cachedApprHashes: Array<ByteArray?> = arrayOfNulls(2048)

    /** NPC indices currently in the owner's visible cohort (NPC_INFO Phase 1). */
    val visibleNpcs: MutableList<Int> = mutableListOf()

    /**
     * NPC indices whose NPC_INFO Phase 2 add-record has already been sent to THIS viewer.
     * Per-viewer (not global Npc state): with multiple players, only the first-built viewer
     * would otherwise ever receive an add record. Entries are removed when the NPC leaves
     * [visibleNpcs] so a re-entering NPC is re-added.
     */
    val sentNpcAdds: MutableSet<Int> = mutableSetOf()

    /**
     * Scene plan from the last op81, including the build-area map-square grid, render-scene zones,
     * world-area type, and op78 local-origin mapping. Computed from the owner's spawn tile by
     * [loadSceneBuild]; defaults to a window centred on
     * the owner's current tile so the viewport is coherent before the first explicit rebuild.
     *
     * Per `docs/protocol/packed-coord-buildarea-948.md`, this drives op81's `packedCoordA`
     * (SW corner) / `packedCoordB` (NE corner). Keep it in sync with what was sent on the wire:
     * [loadSceneBuild] is the single routine that recomputes it and (callers then) re-send op81.
     */
    var sceneBuildPlan: SceneBuildPlan =
        SceneBuildPlanner.planFor(owner.tile, SceneBuildMode.Rebuild, BuildAreaSize.DEFAULT, resolveWorldAreaType = false)
        private set

    var buildArea: BuildArea = sceneBuildPlan.buildArea
        private set

    /**
     * Build area centre in chunk (zone) coordinates — the op81 coord-header centre zone. Derived
     * from the owner's spawn tile (`tile >> 3`); the render scene window is positioned here inside
     * the larger [buildArea] map-square grid (spec §2).
     */
    var buildAreaChunkX: Int = sceneBuildPlan.centreZoneX
    var buildAreaChunkY: Int = sceneBuildPlan.centreZoneY

    /**
     * Recomputes [buildArea] (and the centre zone) from the given spawn tile + size, returning the
     * new build area so the caller can encode op81's corners from it. This is the
     * `loadMapRegions`-style routine (alerion shape): one place that recomputes the build-area
     * state, keeping [buildArea] coherent with the op81 the world server then ships.
     */
    fun loadBuildArea(tile: Tile, size: BuildAreaSize = BuildAreaSize.DEFAULT): BuildArea {
        loadSceneBuild(tile, SceneBuildMode.Rebuild, size, resolveWorldAreaType = false)
        return buildArea
    }

    fun loadFirstLightBuildArea(tile: Tile): BuildArea {
        loadSceneBuild(tile, SceneBuildMode.WorldEntry)
        return buildArea
    }

    fun loadSceneBuild(
        tile: Tile,
        mode: SceneBuildMode,
        size: BuildAreaSize = BuildAreaSize.DEFAULT,
        resolveWorldAreaType: Boolean = true,
    ): SceneBuildPlan {
        sceneBuildPlan = SceneBuildPlanner.planFor(tile, mode, size, resolveWorldAreaType)
        buildArea = sceneBuildPlan.buildArea
        buildAreaChunkX = sceneBuildPlan.centreZoneX
        buildAreaChunkY = sceneBuildPlan.centreZoneY
        return sceneBuildPlan
    }

    fun requiresSceneRebuild(tile: Tile): Boolean = sceneBuildPlan.requiresRebuildFor(tile)

    /** First-tick init flag — must send the init-form PlayerInfo with 18-bit region hashes. */
    var firstTick: Boolean = true

    /**
     * Reset the viewport to its post-op81-prefix state — the local player in the high-res cohort and
     * every other slot in the low-res cohort — and seed the [playerSlots] model from the SAME prefix
     * the client just decoded.
     *
     * The slot model is seeded with the owner's current [Player.tile] as the local low-res anchor
     * (only consumed by increment-2 low-res moves; cohort membership is tile-independent). The
     * legacy [highResIndices] / [lowResIndices] lists are kept in sync for any non-encoder reader,
     * but the encoder now drives off [playerSlots].
     */
    fun resetAfterGpiPrefix(localIndex: Int) {
        require(localIndex in 1 until cachedApprHashes.size)
        highResIndices.clear()
        highResIndices.add(localIndex)
        lowResIndices.clear()
        for (slot in 1 until cachedApprHashes.size) {
            if (slot != localIndex) lowResIndices.add(slot)
        }
        playerSlots.seedFromGpiPrefix(owner.tile, localIndex)
    }
}
