package org.darkan.world.world

import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile

/**
 * 948-5 build-area service: given a spawn [Tile] and a [BuildAreaSize], computes the build-area
 * map-square (region) grid the NXT client allocates from op81, and the two packed-coordinate
 * words (`packedCoordA` = SW corner, `packedCoordB` = NE corner) that drive it.
 *
 * **Why this exists.** op81 REBUILD_NORMAL_SIMPLE carries two 32-bit packed coordinates. The
 * client (`jag::game::BuildArea::DecodePackedCoord @0x006d4320`) unpacks each into
 * `{plane, hi14, lo14}` and `>>6`s the two 14-bit fields to recover *region (map-square, 64-tile)*
 * coordinates — the inclusive bounds of a `(maxRegionX-minRegionX+1) × (maxRegionZ-minRegionZ+1)`
 * grid it then JS5-pulls each region's index-5 map groups into. The two words are the **SW and NE
 * corners** of that grid expressed as **tile** coordinates (`hi14 = X-tile`, `lo14 = Z-tile`),
 * NOT a non-existent `zone<<6` field and NOT an origin+span pair.
 *
 * The previous encoder (`RebuildNormalSimple.packZoneCoord`, modelling `zone<<6`) overflowed the
 * 14-bit field and produced **inverted bounds** (min > max) → the client allocated an empty,
 * unindexable grid, requested ZERO index-5 map groups, and rendered a permanent black screen.
 * This service replaces that model with the corner encoder the binary actually consumes.
 *
 * **Spec:** `docs/protocol/packed-coord-buildarea-948.md` (948-5-verified, authoritative).
 * **Shape (only):** `docs/protocol/alerion-buildarea-reference-910.md` — a `BuildAreaSize` value
 * object, a position→build-area computation, and a covered-region set. Every byte/bit/unit here
 * is 948-5, not 910.
 *
 * @property size      the build-area window size (half-extent in regions about the spawn region).
 * @property minRegion SW / origin corner, in map-squares (`hi14_A >> 6`, `lo14_A >> 6`).
 * @property maxRegion NE / far corner, in map-squares (`hi14_B >> 6`, `lo14_B >> 6`).
 * @property plane     scene base level (0 for the overworld); occupies bits 28-29 of each word.
 */
class BuildArea private constructor(
    val size: BuildAreaSize,
    val minRegion: Region,
    val maxRegion: Region,
    val plane: Int,
) {
    init {
        // The single hard wire constraint (spec §3/§4): the grid is inclusive and must be
        // non-inverted, else the client's ctor allocates a negative/empty column array and the
        // grid indexer rejects every lookup. We construct via [of] which guarantees this, but
        // assert it so a future caller cannot smuggle in an inverted box.
        require(minRegion.x <= maxRegion.x && minRegion.y <= maxRegion.y) {
            "Inverted build-area bounds X[${minRegion.x}..${maxRegion.x}] Z[${minRegion.y}..${maxRegion.y}] — " +
                "the client would allocate an empty grid and request zero map squares (black screen)."
        }
    }

    /** SW-corner packed coordinate word for op81 `+10` (BE u32). */
    val packedCoordA: Int get() = pack(plane, minRegion.x, minRegion.y)

    /** NE-corner packed coordinate word for op81 `+14` (BE u32). */
    val packedCoordB: Int get() = pack(plane, maxRegion.x, maxRegion.y)

    /** Inclusive grid width in map-squares (`maxRegionX - minRegionX + 1`). */
    val regionWidth: Int get() = maxRegion.x - minRegion.x + 1

    /** Inclusive grid height in map-squares (`maxRegionZ - minRegionZ + 1`). */
    val regionHeight: Int get() = maxRegion.y - minRegion.y + 1

    /**
     * The set of map-square (region) ids the client's grid covers — the authoritative "what is
     * loaded for this player" spatial gate (alerion's `mapRegions`). Every region `r` with
     * `minRegion ≤ r ≤ maxRegion` on both axes is inside.
     */
    val regions: Set<Int> by lazy(LazyThreadSafetyMode.NONE) {
        val out = LinkedHashSet<Int>(regionWidth * regionHeight)
        for (rx in minRegion.x..maxRegion.x) {
            for (ry in minRegion.y..maxRegion.y) {
                out.add(Region.id(rx, ry))
            }
        }
        out
    }

    /** True iff [region] lies inside the inclusive grid (the client `>>6` membership test). */
    fun containsRegion(region: Region): Boolean =
        region.x in minRegion.x..maxRegion.x && region.y in minRegion.y..maxRegion.y

    /** True iff [tile]'s region lies inside the grid. Convenience over [containsRegion]. */
    fun containsTile(tile: Tile): Boolean = containsRegion(tile.region)

    override fun toString(): String =
        "BuildArea(size=$size, plane=$plane, X[${minRegion.x}..${maxRegion.x}] Z[${minRegion.y}..${maxRegion.y}], " +
            "packedA=0x${"%08x".format(packedCoordA)}, packedB=0x${"%08x".format(packedCoordB)})"

    companion object {
        /**
         * Packs a `DecodePackedCoord` word from a **region** corner (`@0x006d4320` inverse).
         *
         * The client reads `field >> 6` to recover the region, so the field must hold a
         * **tile-aligned** value `region << 6`. The low 6 bits are ignored by the build path, so
         * we emit the clean `region << 6` form (low6 = 0). Verified: `pack(0, 26, 37) = 0x01a00940`
         * and `pack(0, 72, 142) | <low6>` round-trips to production's words (spec §5.3).
         *
         * @param plane    scene base level (bits 28-29).
         * @param regionX  map-square X — recovered as `(word >> 14) & 0x3FFF) >> 6`.
         * @param regionZ  map-square Z — recovered as `(word & 0x3FFF) >> 6`.
         */
        fun pack(plane: Int, regionX: Int, regionZ: Int): Int =
            ((plane and 0x3) shl 28) or
                (((regionX shl 6) and 0x3FFF) shl 14) or
                ((regionZ shl 6) and 0x3FFF)

        /**
         * Computes a build area centred on [spawn]'s region with the [size] half-window.
         *
         * The window is `spawnRegion ± size.halfRegions` on each axis, clamped at 0 (regions
         * cannot be negative — the field is unsigned and `>>6` is logical). This guarantees the
         * single hard wire invariant `minRegion ≤ spawnRegion ≤ maxRegion` with `min ≤ max`, so
         * the player's map-square (and the ring around it) load.
         *
         * The render scene window is positioned *inside* this grid separately via op81's centre
         * zone (`spawn.zone`); see [org.darkan.world.server.WorldServer]. The corners here are
         * independent of the centre zone — the only coupling required (world-bootstrap §4) is that
         * both derive from the SAME spawn tile, which holds because both take [spawn].
         */
        fun of(spawn: Tile, size: BuildAreaSize = BuildAreaSize.DEFAULT): BuildArea {
            val region = spawn.region
            val r = size.halfRegions
            val minRegion = Region(maxOf(0, region.x - r), maxOf(0, region.y - r))
            val maxRegion = Region(region.x + r, region.y + r)
            return BuildArea(size, minRegion, maxRegion, spawn.level)
        }
    }
}

/**
 * Build-area window size, modelled on alerion's `BuildAreaSize` value object (910 shape) but
 * carrying a **region (map-square)** half-extent rather than 910's tile edge-length, because the
 * 948-5 build area is bounded in **map-squares** (`field >> 6`), not chunks/tiles (spec §3).
 *
 * The client picks a detail level; the server honours it by widening/narrowing this window. Until
 * the 948-5 detail-options handler is RE'd we expose a small set of sane symmetric windows and
 * default to [DEFAULT]. A window must contain the spawn region; bigger windows stream more map
 * squares (spec §5.4 / §8 — keep it modest, grow only if the visible edge looks unbuilt).
 *
 * @property halfRegions half-extent of the (square) window in map-squares about the spawn region.
 *   The covered grid spans `(2*halfRegions + 1)` regions per axis (clamped at 0 on the low side).
 */
enum class BuildAreaSize(val halfRegions: Int) {
    /** 3×3-region window (192 tiles). The smallest window that still loads a ring around spawn. */
    SMALL(1),

    /** 5×5-region window (320 tiles). Comfortable first-light default — spawn square + a ring. */
    MEDIUM(2),

    /** 7×7-region window (448 tiles) — wider margin before the rebuild must re-fire. */
    LARGE(3);

    companion object {
        /**
         * Default window for world-entry. MEDIUM (5×5 regions) guarantees the spawn square plus a
         * two-region ring load, which is enough for the scene to render while staying cheap to
         * stream. Production used a much larger asymmetric window; the client only requires
         * `min ≤ spawnRegion ≤ max`, so a small symmetric window suffices (spec §5.4 / §8).
         */
        val DEFAULT = MEDIUM
    }
}
