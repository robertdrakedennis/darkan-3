package org.darkan.world.entity

import world.gregs.voidps.collision.CacheCollisionFlagMapBuilder
import world.gregs.voidps.collision.CollisionFlagMap
import world.gregs.voidps.collision.CollisionMap
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.cache.definition.data.RegionDefinition
import world.gregs.voidps.cache.definition.decoder.MapDecoder
import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.map.RenderFlag
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

    fun applyLocAdd(tile: Tile, locId: Int, shapeFlags: Int) {
        ensureLoadedForRoute(tile)
        val shape = shapeFromFlags(shapeFlags) ?: return
        val def = Cache.obj(locId) ?: return
        applyObjectClip(tile, shape, shapeFlags and 0x3, def, add = true)
    }

    fun applyLocRemove(tile: Tile, locId: Int, shapeFlags: Int) {
        ensureLoadedForRoute(tile)
        val shape = shapeFromFlags(shapeFlags) ?: return
        val def = Cache.obj(locId) ?: return
        applyObjectClip(tile, shape, shapeFlags and 0x3, def, add = false)
    }

    fun applyLocDel(tile: Tile, shapeFlags: Int) {
        ensureLoadedForRoute(tile)
        val shape = shapeFromFlags(shapeFlags) ?: return
        val region = MapDecoder().decode(Cache.get(), tile.region.id) ?: return
        val localX = tile.x and 0x3f
        val localY = tile.y and 0x3f
        for (obj in region.objects) {
            if (obj.localX != localX || obj.localY != localY || obj.shape != shape.id) continue
            val plane = adjustedPlane(region, obj.plane, obj.localX, obj.localY)
            if (plane != tile.level) continue
            val def = Cache.obj(obj.id) ?: continue
            applyObjectClip(tile, shape, obj.rotation, def, add = false)
        }
    }

    private fun shapeFromFlags(shapeFlags: Int): ObjectShape? =
        runCatching { ObjectShape.forId(shapeFlags ushr 2) }.getOrNull()

    private fun applyObjectClip(tile: Tile, shape: ObjectShape, rotation: Int, def: ObjectDefinition, add: Boolean) {
        val clip = LocClip.from(def)
        if (!clip.clips) return
        when {
            shape.isWallCollisionShape() -> {
                if (add) flags.addWall(tile, shape, rotation, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
                else flags.removeWall(tile, shape, rotation, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
            }

            shape.isObjectCollisionShape() -> {
                val (sizeX, sizeY) = clip.rotatedSize(rotation)
                if (add) flags.addObject(tile, sizeX, sizeY, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
                else flags.removeObject(tile, sizeX, sizeY, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
            }

            shape == ObjectShape.GROUND_DECORATION && clip.blocksProjectiles -> {
                if (add) flags.addBlockWalkAndProj(tile)
                else flags.removeBlockWalkAndProj(tile)
            }
        }
    }

    private fun adjustedPlane(region: RegionDefinition, plane: Int, localX: Int, localY: Int): Int {
        var adjusted = plane
        if (localX in 0 until REGION_SIZE &&
            localY in 0 until REGION_SIZE &&
            RenderFlag.flagged(region.tileFlags[BRIDGE_FLAG_PLANE][localX][localY], RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING)
        ) {
            adjusted--
        }
        return adjusted
    }

    private data class LocClip(
        val width: Int,
        val length: Int,
        val clips: Boolean,
        val blocksProjectiles: Boolean,
    ) {
        fun rotatedSize(rotation: Int): Pair<Int, Int> =
            if (rotation and 0x1 == 1) {
                length.coerceAtLeast(1) to width.coerceAtLeast(1)
            } else {
                width.coerceAtLeast(1) to length.coerceAtLeast(1)
            }

        companion object {
            fun from(def: ObjectDefinition): LocClip {
                val clips = def.solid != 0 && def.blocksSky
                return LocClip(
                    width = def.sizeX,
                    length = def.sizeY,
                    clips = clips,
                    blocksProjectiles = clips && def.solid == 1,
                )
            }
        }
    }

    private fun ObjectShape.isWallCollisionShape(): Boolean = id in 0..3

    private fun ObjectShape.isObjectCollisionShape(): Boolean = id == 9 || id == 10 || id == 11

    private const val REGION_SIZE = 64
    private const val BRIDGE_FLAG_PLANE = 1
    private const val MAX_COORD = 0x3fff
}
