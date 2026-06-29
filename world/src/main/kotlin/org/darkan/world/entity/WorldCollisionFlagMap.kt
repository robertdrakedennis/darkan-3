package org.darkan.world.entity

import world.gregs.voidps.collision.CacheCollisionFlagMapBuilder
import world.gregs.voidps.collision.CollisionFlagMap
import world.gregs.voidps.collision.CollisionMap
import world.gregs.voidps.routefinder.RouteFinding
import world.gregs.voidps.type.Tile

/**
 * Lazy, cache-backed world collision flags for click-to-walk.
 *
 * Regions are decoded on demand from MAPSV2 tiles + loc placements + loc configs.
 * Each loaded 64x64 map square allocates its routefinder zones, so walking outside
 * decoded cache space remains blocked by the routefinder's absent-zone `-1` default.
 */
object WorldCollisionFlagMap {
    val flags: CollisionFlagMap = CollisionMap()

    private val builder by lazy { CacheCollisionFlagMapBuilder() }
    private val loadedRegionIds = mutableSetOf<Int>()
    private val lock = Any()

    fun ensureLoadedForRoute(from: Tile) {
        val half = RouteFinding.DEFAULT_SEARCH_MAP_SIZE / 2
        val minX = (from.x - half).coerceAtLeast(0)
        val maxX = (from.x + half - 1).coerceAtMost(MAX_COORD)
        val minY = (from.y - half).coerceAtLeast(0)
        val maxY = (from.y + half - 1).coerceAtMost(MAX_COORD)
        synchronized(lock) {
            for (regionX in (minX shr 6)..(maxX shr 6)) {
                for (regionY in (minY shr 6)..(maxY shr 6)) {
                    val regionId = (regionX shl 8) or regionY
                    if (loadedRegionIds.add(regionId)) {
                        builder.loadRegion(regionId, flags)
                    }
                }
            }
        }
    }

    private const val MAX_COORD = 0x3fff
}
