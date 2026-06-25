package com.undercut.cache.type.maps

import com.undercut.cache.*
import com.undercut.game.Tile
import com.undercut.game.scene.CachedSceneObject
import com.undercut.pathfinder.WorldCollision
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer

class Region(val regionId: Int, load: Boolean = true) {

    companion object {
        const val OBJECTS = 0
        const val UNDERWATER = 1
        const val NPCS = 2
        const val TILES = 3
        const val WATER_TILES = 4
        private val regions = mutableMapOf<Int, Region>()

        fun get(regionId: Int, load: Boolean = true): Region {
            return regions.getOrPut(regionId) { Region(regionId, load) }.apply {
                if (load && !loaded) load()
            }
        }
    }

    var objects: Array<Array<Array<Array<CachedSceneObject?>>>>? = null
    var objectList: MutableList<CachedSceneObject>? = null
    var npcSpawns: List<NPCSpawn>? = null
    var waterPatches: List<WaterPatch>? = null
    private var overlayIds: Array<Array<IntArray>>? = null
    private var underlayIds: Array<Array<IntArray>>? = null
    private var overlayPathShapes: Array<Array<ByteArray>>? = null
    private var overlayRotations: Array<Array<ByteArray>>? = null
    private var tileFlags: Array<Array<ByteArray>>? = null
    private var loaded = false

    init {
        if (load) load()
    }

    fun load(): Boolean {
        val regionX = regionId shr 8
        val regionY = regionId and 0xff
        if (!Cache.get().exists(Index.MAPSV2.id, regionX or (regionY shl 7))) {
            loaded = true
            return false
        }
        try {
            val archive = Cache.get().getArchive(Index.MAPSV2, regionX or (regionY shl 7))
            archive.files[TILES]?.let { decodeTileData(it) }
            archive.files[OBJECTS]?.let { decodeObjectData(it) }
            archive.files[UNDERWATER]?.let { decodeObjectData(it) }
            archive.files[NPCS]?.let { decodeNPCData(it) }
            archive.files[WATER_TILES]?.let { decodeWaterData(it) }
        } catch (t: Throwable) {
            //region was missing from cache
            loaded = true
            return false
        }
        loaded = true
        return true
    }

    private fun decodeTileData(file: ArchiveFile) {
        val data = file.data
        val stream = ByteBuffer.wrap(data)
        overlayIds = Array(4) { Array(64) { IntArray(64) } }
        underlayIds = Array(4) { Array(64) { IntArray(64) } }
        overlayPathShapes = Array(4) { Array(64) { ByteArray(64) } }
        overlayRotations = Array(4) { Array(64) { ByteArray(64) } }
        tileFlags = Array(4) { Array(64) { ByteArray(64) } }
        stream.skip(5)
        for (plane in 0 until 4) {
            for (x in 0 until 64) {
                for (y in 0 until 64) {
                    val flags = stream.get().toInt() and 0xff
                    if (flags and 0x1 != 0) {
                        val shapeHash = stream.get().toInt() and 0xff
                        overlayIds!![plane][x][y] = stream.getUnsignedSmart()
                        overlayPathShapes!![plane][x][y] = (shapeHash shr 2).toByte()
                        overlayRotations!![plane][x][y] = (shapeHash and 0x3).toByte()
                    }
                    if (flags and 0x2 != 0) tileFlags!![plane][x][y] = stream.get()
                    if (flags and 0x4 != 0) underlayIds!![plane][x][y] = stream.getUnsignedSmart()
                    if (flags and 0x8 != 0) underlayIds!![plane][x][y] = stream.short.toInt() and 0xFFFF
                }
            }
        }

        for (plane in 0 until 4) {
            for (localX in 0 until 64) {
                for (localY in 0 until 64) {
                    if (RenderFlag.flagged(tileFlags!![plane][localX][localY].toInt(), RenderFlag.CLIPPED)) {
                        var finalPlane = plane
                        if (RenderFlag.flagged(tileFlags!![1][localX][localY].toInt(), RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING)) {
                            finalPlane--
                        }
                        if (finalPlane >= 0) {
                            WorldCollision.addBlockedTile(
                                Tile.of(
                                    localX + (regionId shr 8) * 64,
                                    localY + (regionId and 0xff) * 64,
                                    finalPlane
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun decodeObjectData(file: ArchiveFile) {
        val data = file.data
        val stream = ByteBuffer.wrap(data)
        var objectId = -1
        while (true) {
            val incr = stream.getSmartSizeVar()
            if (incr == 0) break
            objectId += incr
            var location = 0
            while (true) {
                val incr2 = stream.getUnsignedSmart()
                if (incr2 == 0) break
                location += incr2 - 1
                val localX = (location shr 6) and 0x3f
                val localY = location and 0x3f
                val plane = location shr 12
                val objectData = stream.get().toInt() and 0xff
                val flag = objectData and 0x80 != 0
                if (flag)
                    stream.readFlag0x80Data()
                val type = ObjectShape.forId(objectData shr 2 and 0x1f)
                val rotation = objectData and 0x3
                val objectPlane = if (tileFlags != null && tileFlags!![1][localX][localY].toInt() and 0x2 != 0) plane - 1 else plane
                if (objectPlane < 0) continue
                val obj = CachedSceneObject(MemorySegment.NULL, objectId, objectId, Tile.of(localX + (regionId shr 8) * 64, localY + (regionId and 0xff) * 64, objectPlane), type, rotation.toByte())
                spawnObject(obj, objectPlane, localX, localY)
            }
        }
    }

    fun spawnObject(obj: CachedSceneObject, plane: Int, localX: Int, localY: Int) {
        if (objects == null) objects = Array(4) { Array(64) { Array(64) { arrayOfNulls(4) } } }
        if (objectList == null) objectList = mutableListOf()
        objectList!!.add(obj)
        objects!![plane][localX][localY][obj.slot] = obj
        WorldCollision.clip(obj)
    }

    private fun decodeNPCData(file: ArchiveFile) {
        val stream = ByteBuffer.wrap(file.data)
        val spawns = mutableListOf<NPCSpawn>()
        val count = stream.getUnsignedSmart()
        for (i in 0 until count) {
            val peek = stream.get(stream.position()).toInt() and 0xFF
            val typeId = if (peek < 0x80) {
                stream.get().toInt() and 0xFF
            } else {
                (stream.short.toInt() and 0xFFFF) + 0x8000
            }
            // Handler-specific data follows but varies by NPC type.
            // We only extract typeId; position info requires handler analysis.
            spawns.add(NPCSpawn(typeId, 0, 0, 0))
        }
        if (npcSpawns == null) {
            npcSpawns = spawns
        } else {
            npcSpawns = (npcSpawns as List<NPCSpawn>) + spawns
        }
    }

    private fun decodeWaterData(file: ArchiveFile) {
        val stream = ByteBuffer.wrap(file.data)
        val patches = mutableListOf<WaterPatch>()
        val count = stream.get().toInt() and 0xFF
        for (i in 0 until count) {
            val posX = stream.get().toInt()
            val posZ = stream.get().toInt()
            val posY = stream.short.toInt() and 0xFFFF
            val extentX = stream.get().toInt()
            val extentZ = stream.get().toInt()
            val qx = stream.float
            val qy = stream.float
            val qz = stream.float
            val qw = stream.float
            val waterTypeId = stream.short.toInt() and 0xFFFF
            val scale1 = stream.get().toInt()
            val scale2 = stream.get().toInt()
            val waterMeshId = stream.short.toInt() and 0xFFFF
            patches.add(WaterPatch(posX, posZ, posY, extentX, extentZ, qx, qy, qz, qw, waterTypeId, scale1, scale2, waterMeshId))
        }
        waterPatches = patches
    }

    private fun ByteBuffer.readFlag0x80Data() {
        val i: Int = get().toInt()
        var f = 0.0f
        var f1 = 0.0f
        var f2 = 0.0f
        var f3 = 1.0f
        if (i and 0x1 != 0) {
            f = short.toFloat() / 32768.0f
            f1 = short.toFloat() / 32768.0f
            f2 = short.toFloat() / 32768.0f
            f3 = short.toFloat() / 32768.0f
        }
        var f4 = 0.0f
        var f5 = 0.0f
        var f6 = 0.0f
        if (i and 0x2 != 0) f4 = short.toFloat()
        if (i and 0x4 != 0) f5 = short.toFloat()
        if (i and 0x8 != 0) f6 = short.toFloat()
        var f7 = 1.0f
        var f8 = 1.0f
        var f9 = 1.0f
        if (i and 0x10 != 0) {
            val f10 = short.toFloat() / 128.0f
            f9 = f10
            f8 = f9
            f7 = f8
        } else {
            if (i and 0x20 != 0) f7 = short.toFloat() / 128.0f
            if (i and 0x40 != 0) f8 = short.toFloat() / 128.0f
            if (i and 0x80 != 0) f9 = short.toFloat() / 128.0f
        }
    }
}