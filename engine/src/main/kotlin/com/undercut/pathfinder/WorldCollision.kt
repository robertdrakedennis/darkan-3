package com.undercut.pathfinder

import com.undercut.cache.type.maps.ObjectShape
import com.undercut.cache.type.maps.Region
import com.undercut.cache.type.objects.ObjectType
import com.undercut.game.Tile
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.script.api.inInstancedArea
import com.undercut.util.MapUtils

object WorldCollision {
    private const val CHUNK_SIZE = 2048 //2048 chunk size = max capacity 16384x16384 tiles
    private val LOADED_REGIONS = mutableSetOf<Int>()
    val allFlags: Array<IntArray?> = arrayOfNulls(CHUNK_SIZE * CHUNK_SIZE * 4)
    private val LOCK = Any()

    var inDynamic = false
    var sceneBase: Tile? = null

    @JvmStatic
    fun checkLoad() {
        try {

            val tile = Bootstrap.client.loggedInPlayer.self.tile
            val dynamicRegion = inInstancedArea
            if (dynamicRegion && !inDynamic) {
                sceneBase = Tile.of(tile.regionX shl 6, tile.regionY shl 6, 0)
                println("Entering dynamic region. SceneBase: ${sceneBase!!.x}, ${sceneBase!!.y}")
                inDynamic = true
            } else if (!dynamicRegion && inDynamic) {
                println("Leaving dynamic region.")
                sceneBase = null
                inDynamic = false
                DynamicRegionCollision.clear()
            }
            if (dynamicRegion) {
                DynamicRegionCollision.loadInstanceCollision(tile)
            } else if (tile.x > 0) {
                for (x in tile.regionX-4..tile.regionX+4)
                    for (y in tile.regionY-4..tile.regionY+4)
                        checkLoadRegion((x shl 8) + y)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

//    @JvmStatic
//    fun regionExists(regionId: Int): Boolean {
//        return Cache.get().exists(Index.MAPSV2.id, (regionId shr 8) or ((regionId and 0xff) shl 7))
//    }

    @JvmStatic
    fun checkLoadRegion(regionId: Int) {
        synchronized(LOCK) {
            Region.get(regionId, true)
            LOADED_REGIONS.add(regionId)
        }
    }

    @JvmStatic
    fun clearChunk(chunkCollisionHash: Int) {
        synchronized(LOCK) {
            allFlags[chunkCollisionHash] = null
        }
    }

    @JvmStatic
    fun removeFlag(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        removeFlag(tile, flag)
    }

    @JvmStatic
    fun addFlag(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        addFlag(tile, flag)
    }

    @JvmStatic
    fun setFlags(tile: Tile, vararg flags: ClipFlag) {
        var flag = 0
        for (f in flags) flag = flag or f.flag
        setFlags(tile, flag)
    }

    @JvmStatic
    fun addBlockedTile(tile: Tile) {
        addFlag(tile, ClipFlag.PFBW_FLOOR)
    }

    @JvmStatic
    fun removeBlockedTile(tile: Tile) {
        removeFlag(tile, ClipFlag.PFBW_FLOOR)
    }

    @JvmStatic
    fun addBlockWalkAndProj(tile: Tile) {
        addFlag(tile, ClipFlag.PFBW_GROUND_DECO)
    }

    @JvmStatic
    fun removeBlockWalkAndProj(tile: Tile) {
        removeFlag(tile, ClipFlag.PFBW_GROUND_DECO)
    }

    @JvmStatic
    fun addClipNPC(tile: Tile) {
        addFlag(tile, ClipFlag.BW_NPC)
    }

    @JvmStatic
    fun removeClipNPC(tile: Tile) {
        removeFlag(tile, ClipFlag.BW_NPC)
    }

    @JvmStatic
    fun addClipPlayer(tile: Tile) {
        addFlag(tile, ClipFlag.BW_PLAYER)
    }

    @JvmStatic
    fun removeClipPlayer(tile: Tile) {
        removeFlag(tile, ClipFlag.BW_PLAYER)
    }

    @JvmStatic
    fun addObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        var flag = ClipFlag.BW_FULL.flag
        if (blocksProjectiles) flag = flag or ClipFlag.BP_FULL.flag
        if (pathfinder) flag = flag or ClipFlag.PF_FULL.flag
        for (tileX in tile.x until tile.x + sizeX) for (tileY in tile.y until tile.y + sizeY) addFlag(Tile.of(tileX, tileY, tile.plane.toInt()), flag)
    }

    @JvmStatic
    fun removeObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        var flag = ClipFlag.BW_FULL.flag
        if (blocksProjectiles) flag = flag or ClipFlag.BP_FULL.flag
        if (pathfinder) flag = flag or ClipFlag.PF_FULL.flag
        for (tileX in tile.x until tile.x + sizeX) for (tileY in tile.y until tile.y + sizeY) removeFlag(Tile.of(tileX, tileY, tile.plane.toInt()), flag)
    }

    @JvmStatic
    fun addWall(tile: Tile, type: ObjectShape?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        when (type) {
            ObjectShape.WALL_STRAIGHT -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectShape.WALL_DIAGONAL_CORNER, ObjectShape.WALL_STRAIGHT_CORNER -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 1, 0), ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 1, 0), ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, -1, 0), ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, -1, 0), ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectShape.WALL_WHOLE_CORNER -> {
                when (rotation) {
                    0 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        addFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        addFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder) or ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        addFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        addFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            else -> {}
        }
    }

    @JvmStatic
    fun removeWall(tile: Tile, type: ObjectShape?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) {
        when (type) {
            ObjectShape.WALL_STRAIGHT -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectShape.WALL_DIAGONAL_CORNER, ObjectShape.WALL_STRAIGHT_CORNER -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 1, 0), ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 1, 0), ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockSouthEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, -1, 0), ClipFlag.blockNorthWest(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouthWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, -1, 0), ClipFlag.blockNorthEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            ObjectShape.WALL_WHOLE_CORNER -> {
                when (rotation) {
                    0 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                    }

                    1 -> {
                        removeFlag(tile, ClipFlag.blockNorth(true, blocksProjectiles, pathfinder) or ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, 1, 0), ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                    }

                    2 -> {
                        removeFlag(tile, ClipFlag.blockEast(true, blocksProjectiles, pathfinder) or ClipFlag.blockSouth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(1, 0, 0), ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                    }

                    3 -> {
                        removeFlag(tile, ClipFlag.blockSouth(true, blocksProjectiles, pathfinder) or ClipFlag.blockWest(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(0, -1, 0), ClipFlag.blockNorth(true, blocksProjectiles, pathfinder))
                        removeFlag(tile.transform(-1, 0, 0), ClipFlag.blockEast(true, blocksProjectiles, pathfinder))
                    }
                }
            }

            else -> {}
        }
    }

    @JvmStatic
    fun getFlags(tile: Tile): Int {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) return -1
            return allFlags[chunkId]?.get(tile.xInChunk or (tile.yInChunk shl 3)) ?: ClipFlag.BW_FULL.flag
        }
    }

    @JvmStatic
    fun getFlags(x: Int, y: Int, plane: Int): Int {
        synchronized(LOCK) {
            val chunkId = MapUtils.encode(MapUtils.Structure.CHUNK, x shr 3, y shr 3, plane)
            if (allFlags[chunkId] == null) return -1
            return allFlags[chunkId]?.get(x and 7 or ((y and 7) shl 3)) ?: ClipFlag.BW_FULL.flag
        }
    }

    @JvmStatic
    fun addFlag(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]?.set(tile.xInChunk or (tile.yInChunk shl 3), (allFlags[chunkId]?.get(tile.xInChunk or (tile.yInChunk shl 3)) ?: ClipFlag.BW_FULL.flag) or flag)
        }
    }

    @JvmStatic
    fun removeFlag(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]?.set(tile.xInChunk or (tile.yInChunk shl 3), (allFlags[chunkId]?.get(tile.xInChunk or (tile.yInChunk shl 3)) ?: ClipFlag.BW_FULL.flag) and flag.inv())
        }
    }

    @JvmStatic
    fun setFlags(tile: Tile, flag: Int) {
        synchronized(LOCK) {
            val chunkId = tile.chunkId
            if (allFlags[chunkId] == null) allFlags[chunkId] = IntArray(64)
            allFlags[chunkId]?.set(tile.xInChunk or (tile.yInChunk shl 3), flag)
        }
    }

    @JvmStatic
    fun clip(obj: SceneObject) {
        if (obj.id == -1) return
        val type: ObjectShape = obj.shape
        val rotation: Int = obj.rotation.toInt()

        val defs: ObjectType = obj.defs

        if (defs.clipType == 0) return

        when (type) {
            ObjectShape.WALL_STRAIGHT, ObjectShape.WALL_DIAGONAL_CORNER, ObjectShape.WALL_WHOLE_CORNER, ObjectShape.WALL_STRAIGHT_CORNER -> addWall(obj.tile, type, rotation, defs.blocks, !defs.ignoreAltClip)
            ObjectShape.WALL_INTERACT, ObjectShape.SCENERY_INTERACT, ObjectShape.GROUND_INTERACT, ObjectShape.STRAIGHT_SLOPE_ROOF, ObjectShape.DIAGONAL_SLOPE_ROOF, ObjectShape.DIAGONAL_SLOPE_CONNECT_ROOF, ObjectShape.STRAIGHT_SLOPE_CORNER_CONNECT_ROOF, ObjectShape.STRAIGHT_SLOPE_CORNER_ROOF, ObjectShape.STRAIGHT_FLAT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_ROOF, ObjectShape.DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF -> {
                val sizeX: Int
                val sizeY: Int
                if (rotation != 1 && rotation != 3) {
                    sizeX = defs.sizeX
                    sizeY = defs.sizeY
                } else {
                    sizeX = defs.sizeY
                    sizeY = defs.sizeX
                }
                addObject(obj.tile, sizeX, sizeY, defs.blocks, !defs.ignoreAltClip)
            }

            ObjectShape.GROUND_DECORATION -> if (defs.clipType == 1) addBlockWalkAndProj(obj.tile)
            else -> {}
        }
    }

    @JvmStatic
    fun unclip(tile: Tile) {
        setFlags(tile, 0)
    }

    @JvmStatic
    fun unclip(obj: SceneObject) {
        if (obj.id == -1) // dont clip or noclip with id -1
            return
        val type: ObjectShape = obj.shape
        val rotation: Int = obj.rotation.toInt()
        val defs: ObjectType = obj.defs

        if (defs.clipType == 0) return

        when (type) {
            ObjectShape.WALL_STRAIGHT, ObjectShape.WALL_DIAGONAL_CORNER, ObjectShape.WALL_WHOLE_CORNER, ObjectShape.WALL_STRAIGHT_CORNER -> removeWall(obj.tile, type, rotation, defs.blocks, !defs.ignoreAltClip)
            ObjectShape.WALL_INTERACT, ObjectShape.SCENERY_INTERACT, ObjectShape.GROUND_INTERACT, ObjectShape.STRAIGHT_SLOPE_ROOF, ObjectShape.DIAGONAL_SLOPE_ROOF, ObjectShape.DIAGONAL_SLOPE_CONNECT_ROOF, ObjectShape.STRAIGHT_SLOPE_CORNER_CONNECT_ROOF, ObjectShape.STRAIGHT_SLOPE_CORNER_ROOF, ObjectShape.STRAIGHT_FLAT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_ROOF, ObjectShape.DIAGONAL_BOTTOM_EDGE_CONNECT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_CONNECT_ROOF, ObjectShape.STRAIGHT_BOTTOM_EDGE_CONNECT_CORNER_ROOF -> {
                val sizeX: Int
                val sizeY: Int
                if (rotation == 1 || rotation == 3) {
                    sizeX = defs.sizeY
                    sizeY = defs.sizeX
                } else {
                    sizeX = defs.sizeX
                    sizeY = defs.sizeY
                }
                removeObject(obj.tile, sizeX, sizeY, defs.blocks, !defs.ignoreAltClip)
            }

            ObjectShape.GROUND_DECORATION -> if (defs.clipType == 1) removeBlockWalkAndProj(obj.tile)
            else -> {}
        }
    }
}
