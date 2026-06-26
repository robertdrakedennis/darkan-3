package com.undercut.pathfinder

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.map.Region
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.script.api.inInstancedArea
import world.gregs.voidps.collision.ClipFlag
import world.gregs.voidps.collision.CollisionMap
import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.type.Tile

object WorldCollision {
    private val map = CollisionMap()
    private val LOADED_REGIONS = mutableSetOf<Int>()
    private val LOCK = Any()

    val allFlags: Array<IntArray?> get() = map.allFlags

    var inDynamic = false
    var sceneBase: Tile? = null

    @JvmStatic
    fun checkLoad() {
        try {
            val tile = Bootstrap.client.loggedInPlayer.self.tile
            val dynamicRegion = inInstancedArea
            if (dynamicRegion && !inDynamic) {
                sceneBase = Tile(tile.regionX shl 6, tile.regionY shl 6, 0)
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
                for (x in tile.regionX - 4..tile.regionX + 4)
                    for (y in tile.regionY - 4..tile.regionY + 4)
                        checkLoadRegion((x shl 8) + y)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun checkLoadRegion(regionId: Int) {
        synchronized(LOCK) {
            Region.get(regionId, true)
            LOADED_REGIONS.add(regionId)
        }
    }

    @JvmStatic
    fun clearChunk(chunkCollisionHash: Int) = map.clearChunk(chunkCollisionHash)

    @JvmStatic
    fun removeFlag(tile: Tile, vararg flags: ClipFlag) = map.removeFlag(tile, *flags)

    @JvmStatic
    fun addFlag(tile: Tile, vararg flags: ClipFlag) = map.addFlag(tile, *flags)

    @JvmStatic
    fun setFlags(tile: Tile, vararg flags: ClipFlag) = map.setFlags(tile, *flags)

    @JvmStatic
    fun addBlockedTile(tile: Tile) = map.addBlockedTile(tile)

    @JvmStatic
    fun removeBlockedTile(tile: Tile) = map.removeBlockedTile(tile)

    @JvmStatic
    fun addBlockWalkAndProj(tile: Tile) = map.addBlockWalkAndProj(tile)

    @JvmStatic
    fun removeBlockWalkAndProj(tile: Tile) = map.removeBlockWalkAndProj(tile)

    @JvmStatic
    fun addClipNPC(tile: Tile) = map.addClipNPC(tile)

    @JvmStatic
    fun removeClipNPC(tile: Tile) = map.removeClipNPC(tile)

    @JvmStatic
    fun addClipPlayer(tile: Tile) = map.addClipPlayer(tile)

    @JvmStatic
    fun removeClipPlayer(tile: Tile) = map.removeClipPlayer(tile)

    @JvmStatic
    fun addObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) =
        map.addObject(tile, sizeX, sizeY, blocksProjectiles, pathfinder)

    @JvmStatic
    fun removeObject(tile: Tile, sizeX: Int, sizeY: Int, blocksProjectiles: Boolean, pathfinder: Boolean) =
        map.removeObject(tile, sizeX, sizeY, blocksProjectiles, pathfinder)

    @JvmStatic
    fun addWall(tile: Tile, type: ObjectShape?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) =
        map.addWall(tile, type, rotation, blocksProjectiles, pathfinder)

    @JvmStatic
    fun removeWall(tile: Tile, type: ObjectShape?, rotation: Int, blocksProjectiles: Boolean, pathfinder: Boolean) =
        map.removeWall(tile, type, rotation, blocksProjectiles, pathfinder)

    @JvmStatic
    fun getFlags(tile: Tile): Int = map.getFlags(tile)

    @JvmStatic
    fun getFlags(x: Int, y: Int, plane: Int): Int = map.getFlags(x, y, plane)

    @JvmStatic
    fun addFlag(tile: Tile, flag: Int) = map.addFlag(tile, flag)

    @JvmStatic
    fun removeFlag(tile: Tile, flag: Int) = map.removeFlag(tile, flag)

    @JvmStatic
    fun setFlags(tile: Tile, flag: Int) = map.setFlags(tile, flag)

    @JvmStatic
    fun unclip(tile: Tile) = map.unclip(tile)

    @JvmStatic
    fun clip(obj: SceneObject) {
        if (obj.id == -1) return
        map.applyObject(obj.tile, obj.shape, obj.rotation.toInt(), obj.defs)
    }

    @JvmStatic
    fun unclip(obj: SceneObject) {
        if (obj.id == -1) return
        map.removeObjectClip(obj.tile, obj.shape, obj.rotation.toInt(), obj.defs)
    }
}
