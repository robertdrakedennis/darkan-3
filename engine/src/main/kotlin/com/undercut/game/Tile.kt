package com.undercut.game

import com.undercut.game.nxt.DoActionOpcode
import com.undercut.pathfinder.WorldCollision
import com.undercut.profiling.PlayerProfiles
import com.undercut.script.api.localPlayer
import com.undercut.util.MapUtils
import com.undercut.util.MapUtils.Structure
import com.undercut.util.random
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class LocalTile(val x: Short, val y: Short)

data class Tile(val x: Short, val y: Short, val plane: Byte) {

    companion object {
        fun of(x: Int, y: Int, plane: Int): Tile {
            return Tile(x.toShort(), y.toShort(), plane.toByte())
        }

        fun of(x: Int, y: Int, plane: Int, size: Int): Tile {
            return Tile(getCoordFaceX(x, size, size, -1).toShort(), getCoordFaceY(y, size, size, -1).toShort(), plane.toByte())
        }

        fun of(tile: Tile): Tile {
            return Tile(tile.x, tile.y, tile.plane)
        }

        fun ofLocal(localX: Int, localY: Int, plane: Int): Tile {
            return Tile(((localPlayer.tile.regionX shl 6) + localX).toShort(), ((localPlayer.tile.regionY shl 6) + localY).toShort(), plane.toByte())
        }

        fun ofSceneLocal(sceneLocalX: Int, sceneLocalY: Int, plane: Int): Tile? {
            if (WorldCollision.sceneBase != null)
                return Tile((WorldCollision.sceneBase!!.x + sceneLocalX.toShort()).toShort(), (WorldCollision.sceneBase!!.y + sceneLocalY).toShort(), plane.toByte())
            return null
        }

        fun of(tile: Tile, randomize: Int): Tile {
            return Tile(
                (tile.x + random(randomize * 2 + 1) - randomize).toShort(),
                (tile.y + random(randomize * 2 + 1) - randomize).toShort(),
                tile.plane
            )
        }

        fun of(hash: Int): Tile {
            return Tile((hash shr 14 and 0x3fff).toShort(), (hash and 0x3fff).toShort(), (hash shr 28).toByte())
        }

        fun of(z: Int, regionX: Int, regionY: Int, localX: Int, localY: Int): Tile {
            return Tile((regionX shl 6 or localX).toShort(), (regionY shl 6 or localY).toShort(), z.toByte())
        }

        fun getCoordFaceX(x: Int, sizeX: Int, sizeY: Int, rotation: Int): Int {
            return x + ((if (rotation == 1 || rotation == 3) sizeY else sizeX) - 1) / 2
        }

        fun getCoordFaceY(y: Int, sizeX: Int, sizeY: Int, rotation: Int): Int {
            return y + ((if (rotation == 1 || rotation == 3) sizeX else sizeY) - 1) / 2
        }

        fun toInt(x: Int, y: Int, plane: Int): Int {
            return y + (x shl 14) + (plane shl 28)
        }
    }

    fun isAt(x: Int, y: Int) = this.x == x.toShort() && this.y == y.toShort()

    fun isAt(x: Int, y: Int, z: Int) = this.x == x.toShort() && this.y == y.toShort() && this.plane == z.toByte()

    val xInRegion: Int
        get() = x.toInt() and 0x3F

    val yInRegion: Int
        get() = y.toInt() and 0x3F

    val xInChunk: Int
        get() = x.toInt() and 0x7

    val yInChunk: Int
        get() = y.toInt() and 0x7

    val chunkX: Int
        get() = x.toInt() shr 3

    val chunkY: Int
        get() = y.toInt() shr 3

    val regionX: Int
        get() = x.toInt() shr 6

    val regionY: Int
        get() = y.toInt() shr 6

    val regionId: Int
        get() = (regionX shl 8) + regionY

    val regionHash: Int
        get() = regionY + (regionX shl 8) + (plane.toInt() shl 16)

    val tileHash: Int
        get() = toInt(x.toInt(), y.toInt(), plane.toInt())

    val chunkId: Int
        get() = MapUtils.encode(Structure.CHUNK, chunkX, chunkY, plane.toInt())

    val chunkLocalHash: Int
        get() = (xInChunk shl 4) or yInChunk

    fun getChunkXInScene(chunkId: Int): Int {
        return chunkX - MapUtils.decode(Structure.CHUNK, chunkId)[0]
    }

    fun getChunkYInScene(chunkId: Int): Int {
        return chunkY - MapUtils.decode(Structure.CHUNK, chunkId)[1]
    }

    fun getXInScene(chunkId: Int): Int {
        return x.toInt() - MapUtils.decode(Structure.CHUNK, chunkId)[0] * 8
    }

    fun getYInScene(chunkId: Int): Int {
        return y.toInt() - MapUtils.decode(Structure.CHUNK, chunkId)[1] * 8
    }

    fun localizeScene(): LocalTile? {
        if (WorldCollision.sceneBase == null) return null
        return LocalTile((x - WorldCollision.sceneBase!!.x).toShort(), (y - WorldCollision.sceneBase!!.y).toShort())
    }

//    fun localizeScene(sceneBaseChunk: Int): Tile {
//        return of(getXInScene(sceneBaseChunk), getYInScene(sceneBaseChunk), plane.toInt())
//    }
//
//    fun localizeRegion(): Tile {
//        return of(xInRegion, yInRegion, plane.toInt())
//    }

    fun withinDistance(tile: Tile, distance: Int = 20): Boolean {
        if (tile.plane != plane) return false
        val deltaX = tile.x - x
        val deltaY = tile.y - y
        return deltaX <= distance && deltaX >= -distance && deltaY <= distance && deltaY >= -distance
    }

    fun withinInteractionRange(tile: Tile) = withinDistance(tile, PlayerProfiles.get().interactDistanceRange)

    fun getDistance(tile: Tile): Int {
        val deltaX = tile.x - x
        val deltaY = tile.y - y
        return sqrt(deltaX.toDouble().pow(2.0) + deltaY.toDouble().pow(2.0)).toInt()
    }

    fun getCoordFaceX(sizeX: Int, sizeY: Int, rotation: Int): Int {
        return x + ((if (rotation == 1 || rotation == 3) sizeY else sizeX) - 1) / 2
    }

    fun getCoordFaceY(sizeX: Int, sizeY: Int, rotation: Int): Int {
        return y + ((if (rotation == 1 || rotation == 3) sizeX else sizeY) - 1) / 2
    }

    fun transform(x: Int, y: Int, plane: Int = 0): Tile {
        return Tile((this.x + x).toShort(), (this.y + y).toShort(), (this.plane + plane).toByte())
    }

    fun matches(other: Tile): Boolean {
        return this.x == other.x && this.y == other.y && this.plane == other.plane
    }

    fun withinArea(a: Int, b: Int, c: Int, d: Int): Boolean {
        return getX() >= a && getY() >= b && getX() <= c && getY() <= d
    }

    fun getLongestDelta(other: Tile): Int {
        val deltaX = abs(getX() - other.getX())
        val deltaY = abs(getY() - other.getY())
        return maxOf(deltaX, deltaY)
    }

    override fun toString(): String {
        return "[ X: $x, Y: $y, Z: $plane ]"
    }

    fun getX() = x.toInt()
    fun getY() = y.toInt()
    fun getPlane() = plane.toInt().coerceAtMost(3)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tile) return false
        return x == other.x && y == other.y && plane == other.plane
    }

    override fun hashCode() = tileHash

    fun randomize(range: Int) = of(this, range)
    fun randomizeX(range: Int) = Tile((x + random(range * 2 + 1) - range).toShort(), y, plane)
    fun randomizeY(range: Int) = Tile(x, (y + random(range * 2 + 1) - range).toShort(), plane)

    fun target(): Boolean {
        if (localPlayer.tile.withinDistance(this, 20)) {
            DoActionOpcode.SELECT_TILE.fire(0, x.toInt(), y.toInt())
            return true
        } else
            return false
    }



}