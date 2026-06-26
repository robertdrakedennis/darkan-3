package com.undercut.game.map

import com.undercut.game.scene.CachedSceneObject
import com.undercut.pathfinder.WorldCollision
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.RegionDefinition
import world.gregs.voidps.cache.definition.data.RegionNpcSpawn
import world.gregs.voidps.cache.definition.data.RegionWaterPatch
import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.map.RenderFlag
import world.gregs.voidps.type.Tile
import java.lang.foreign.MemorySegment

class Region(val regionId: Int, load: Boolean = true) {

    companion object {
        private val regions = mutableMapOf<Int, Region>()

        fun get(regionId: Int, load: Boolean = true): Region {
            return regions.getOrPut(regionId) { Region(regionId, load) }.apply {
                if (load && !loaded) load()
            }
        }
    }

    var objects: Array<Array<Array<Array<CachedSceneObject?>>>>? = null
    var objectList: MutableList<CachedSceneObject>? = null
    var npcSpawns: List<RegionNpcSpawn>? = null
    var waterPatches: List<RegionWaterPatch>? = null
    private var loaded = false

    init {
        if (load) load()
    }

    fun load(): Boolean {
        val definition = Cache.region(regionId)
        if (definition == null) {
            loaded = true
            return false
        }
        try {
            val regionX = regionId shr 8
            val regionY = regionId and 0xff
            decodeTiles(definition.tileFlags, regionX, regionY)
            decodeObjects(definition, regionX, regionY)
            npcSpawns = definition.npcSpawns
            waterPatches = definition.waterPatches
        } catch (t: Throwable) {
            loaded = true
            return false
        }
        loaded = true
        return true
    }

    private fun decodeTiles(tileFlags: Array<Array<IntArray>>, regionX: Int, regionY: Int) {
        for (plane in 0 until 4) {
            for (localX in 0 until 64) {
                for (localY in 0 until 64) {
                    if (!RenderFlag.flagged(tileFlags[plane][localX][localY], RenderFlag.CLIPPED)) continue
                    var finalPlane = plane
                    if (RenderFlag.flagged(tileFlags[1][localX][localY], RenderFlag.LOWER_OBJECTS_TO_OVERRIDE_CLIPPING)) {
                        finalPlane--
                    }
                    if (finalPlane >= 0) {
                        WorldCollision.addBlockedTile(Tile.of(localX + regionX * 64, localY + regionY * 64, finalPlane))
                    }
                }
            }
        }
    }

    private fun decodeObjects(definition: RegionDefinition, regionX: Int, regionY: Int) {
        val tileFlags = definition.tileFlags
        for (obj in definition.objects) {
            val localX = obj.localX
            val localY = obj.localY
            val objectPlane = if (tileFlags[1][localX][localY] and 0x2 != 0) obj.plane - 1 else obj.plane
            if (objectPlane < 0) continue
            val shape = ObjectShape.forId(obj.shape)
            val cached = CachedSceneObject(
                MemorySegment.NULL,
                obj.id,
                obj.id,
                Tile.of(localX + regionX * 64, localY + regionY * 64, objectPlane),
                shape,
                obj.rotation.toByte()
            )
            spawnObject(cached, objectPlane, localX, localY)
        }
    }

    fun spawnObject(obj: CachedSceneObject, plane: Int, localX: Int, localY: Int) {
        if (objects == null) objects = Array(4) { Array(64) { Array(64) { arrayOfNulls(4) } } }
        if (objectList == null) objectList = mutableListOf()
        objectList!!.add(obj)
        objects!![plane][localX][localY][obj.slot] = obj
        WorldCollision.clip(obj)
    }
}
