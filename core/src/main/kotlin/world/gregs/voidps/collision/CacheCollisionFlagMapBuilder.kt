package world.gregs.voidps.collision

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Definitions
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import world.gregs.voidps.cache.definition.data.RegionDefinition
import world.gregs.voidps.cache.definition.decoder.MapDecoder
import world.gregs.voidps.cache.definition.decoder.ObjectDecoder
import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.map.RenderFlag
import world.gregs.voidps.type.Tile

/**
 * Derives runtime collision flags from decoded MAPSV2 region data and loc definitions.
 *
 * The cache does not store collision directly. It stores terrain flags, loc placements,
 * and loc config definitions; this builder applies the NXT-verified clip-flag rules into
 * a routefinder-ready [CollisionFlagMap].
 */
class CacheCollisionFlagMapBuilder(
    private val cache: Cache = Cache.get(),
    private val objectDefinitions: Definitions<ObjectDefinition> =
        Definitions(ObjectDecoder(EnvVars.members), cache),
) {

    fun loadRegion(regionId: Int, flags: CollisionFlagMap): Boolean {
        val region = CacheRegionLoader.decode(cache, regionId) ?: return false
        val baseX = (regionId shr 8) shl 6
        val baseY = (regionId and 0xff) shl 6
        allocateRegion(flags, baseX, baseY)
        applyTileFlags(region, flags, baseX, baseY)
        applyObjects(region, flags, baseX, baseY)
        return true
    }

    private fun allocateRegion(flags: CollisionFlagMap, baseX: Int, baseY: Int) {
        for (plane in 0 until LEVELS) {
            for (x in 0 until REGION_SIZE step ZONE_SIZE) {
                for (y in 0 until REGION_SIZE step ZONE_SIZE) {
                    flags.allocateIfAbsent(baseX + x, baseY + y, plane)
                }
            }
        }
    }

    private fun applyTileFlags(region: RegionDefinition, flags: CollisionFlagMap, baseX: Int, baseY: Int) {
        for (plane in 0 until LEVELS) {
            for (localX in 0 until REGION_SIZE) {
                for (localY in 0 until REGION_SIZE) {
                    if (!RenderFlag.flagged(region.tileFlags[plane][localX][localY], RenderFlag.CLIPPED)) {
                        continue
                    }
                    val finalPlane = adjustedPlane(region, plane, localX, localY)
                    if (finalPlane >= 0) {
                        flags.addBlockedTile(Tile(baseX + localX, baseY + localY, finalPlane))
                    }
                }
            }
        }
    }

    private fun applyObjects(region: RegionDefinition, flags: CollisionFlagMap, baseX: Int, baseY: Int) {
        for (obj in region.objects) {
            val shape = runCatching { ObjectShape.forId(obj.shape) }.getOrNull() ?: continue
            val def = objectDefinitions.getOrNull(obj.id) ?: continue
            val clip = LocClip.from(def)
            if (!clip.clips) {
                continue
            }
            val plane = adjustedPlane(region, obj.plane, obj.localX, obj.localY)
            if (plane < 0) {
                continue
            }
            val tile = Tile(baseX + obj.localX, baseY + obj.localY, plane)
            when {
                shape.isWallCollisionShape() -> {
                    flags.addWall(tile, shape, obj.rotation, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
                }

                shape.isObjectCollisionShape() -> {
                    val (sizeX, sizeY) = clip.rotatedSize(obj.rotation)
                    flags.addObject(tile, sizeX, sizeY, blocksProjectiles = clip.blocksProjectiles, pathfinder = false)
                }

                shape == ObjectShape.GROUND_DECORATION && clip.blocksProjectiles -> {
                    flags.addBlockWalkAndProj(tile)
                }
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

    private object CacheRegionLoader {
        fun decode(cache: Cache, regionId: Int): RegionDefinition? {
            val regionX = regionId shr 8
            val regionY = regionId and 0xff
            if (regionX !in 0..0x7f || regionY !in 0..0xff) {
                return null
            }
            return MapDecoder().decode(cache, regionId)
        }
    }

    private companion object {
        private const val REGION_SIZE = 64
        private const val ZONE_SIZE = 8
        private const val LEVELS = 4
        private const val BRIDGE_FLAG_PLANE = 1
    }
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
