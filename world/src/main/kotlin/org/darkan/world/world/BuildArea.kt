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
 * @property size      symmetric rebuild window size; null for the production first-light profile.
 * @property minRegion SW / origin corner, in map-squares (`hi14_A >> 6`, `lo14_A >> 6`).
 * @property maxRegion NE / far corner, in map-squares (`hi14_B >> 6`, `lo14_B >> 6`).
 * @property plane     scene base level (0 for the overworld); occupies bits 28-29 of each word.
 */
class BuildArea private constructor(
    val size: BuildAreaSize?,
    val minRegion: Region,
    val maxRegion: Region,
    val plane: Int,
    private val label: String = size?.name ?: "custom",
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
    val packedCoordB: Int get() =
        packTile(
            plane = plane,
            tileX = (maxRegion.x shl REGION_TILE_BITS) + FAR_ZONE_OFFSET_TILES,
            tileZ = (maxRegion.y shl REGION_TILE_BITS) + FAR_ZONE_OFFSET_TILES,
        )

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
        "BuildArea($label, plane=$plane, X[${minRegion.x}..${maxRegion.x}] Z[${minRegion.y}..${maxRegion.y}], " +
            "packedA=0x${"%08x".format(packedCoordA)}, packedB=0x${"%08x".format(packedCoordB)})"

    companion object {
        val FIRST_LIGHT_MIN_REGION = Region(26, 37)
        val FIRST_LIGHT_MAX_REGION = Region(72, 142)

        /**
         * Packs a `DecodePackedCoord` word from a **region** corner (`@0x006d4320` inverse).
         *
         * The client reads `field >> 6` to recover the region, so the field must hold a tile value
         * inside that region. This emits the clean southwest form `region << 6`.
         *
         * @param plane    scene base level (bits 28-29).
         * @param regionX  map-square X — recovered as `(word >> 14) & 0x3FFF) >> 6`.
         * @param regionZ  map-square Z — recovered as `(word & 0x3FFF) >> 6`.
         */
        fun pack(plane: Int, regionX: Int, regionZ: Int): Int =
            packTile(plane, regionX shl REGION_TILE_BITS, regionZ shl REGION_TILE_BITS)

        fun packTile(plane: Int, tileX: Int, tileZ: Int): Int =
            ((plane and 0x3) shl 28) or
                ((tileX and PACKED_TILE_MASK) shl 14) or
                (tileZ and PACKED_TILE_MASK)

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

        fun firstLight(spawn: Tile): BuildArea {
            val area = BuildArea(
                size = null,
                minRegion = FIRST_LIGHT_MIN_REGION,
                maxRegion = FIRST_LIGHT_MAX_REGION,
                plane = spawn.level,
                label = "FIRST_LIGHT_PRODUCTION",
            )
            require(area.containsRegion(spawn.region)) {
                "First-light build-area ${area.minRegion}..${area.maxRegion} does not contain spawn region ${spawn.region}"
            }
            return area
        }

        private const val REGION_TILE_BITS = 6
        private const val PACKED_TILE_MASK = 0x3FFF
        private const val FAR_ZONE_OFFSET_TILES = 7 * 8
    }
}

/**
 * Build-area window size, modelled on alerion's `BuildAreaSize` value object (910 shape) but
 * carrying a **region (map-square)** half-extent rather than 910's tile edge-length, because the
 * 948-5 build area is bounded in **map-squares** (`field >> 6`), not chunks/tiles (spec §3).
 *
 * The client picks a detail level; symmetric sizes remain useful for future rebuilds. Fresh
 * world-entry uses [BuildArea.firstLight] because production rev948 sends a larger asymmetric grid
 * before first render.
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
         * Default symmetric window for non-login rebuilds.
         */
        val DEFAULT = MEDIUM
    }
}
