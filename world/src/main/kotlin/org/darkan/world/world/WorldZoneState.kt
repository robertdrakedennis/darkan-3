package org.darkan.world.world

import org.darkan.core.EnvVars
import org.darkan.core.net.prot.LocAdd
import org.darkan.core.net.prot.LocAnim
import org.darkan.core.net.prot.LocDel
import org.darkan.core.net.prot.ObjAdd
import org.darkan.core.net.prot.ObjCount
import org.darkan.core.net.prot.ObjDel
import org.darkan.core.net.prot.ServerProt
import org.darkan.world.entity.WorldCollisionProvider
import org.darkan.world.net.FirstLightSceneBootstrap
import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.type.Tile
import world.gregs.voidps.type.Zone
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Persistent server-owned zone state streamed during scene entry/rebuild.
 *
 * Static terrain and cache locations are deliberately absent from this registry; the client requests
 * those map/loc groups from JS5 after op81. Only dynamic server-owned deltas live here.
 */
object WorldZoneState : ZoneStateProvider {
    private val groundItems = ConcurrentHashMap<Int, CopyOnWriteArrayList<GroundItemState>>()
    private val locAdds = ConcurrentHashMap<Int, CopyOnWriteArrayList<LocAddState>>()
    private val locDeletes = ConcurrentHashMap<Int, CopyOnWriteArrayList<LocDeleteState>>()
    private val locAnims = ConcurrentHashMap<Int, CopyOnWriteArrayList<LocAnimState>>()

    override fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone): List<ServerProt> =
        packetsFor(zone, localSceneZone, includeFirstLightScaffolding = EnvVars.firstLightScaffolding)

    fun packetsFor(zone: Zone, localSceneZone: LocalSceneZone, includeFirstLightScaffolding: Boolean): List<ServerProt> {
        val out = ArrayList<ServerProt>()
        for (state in groundItems[zone.id].orEmpty()) out += state.toPacket()
        for (state in locDeletes[zone.id].orEmpty()) out += state.toPacket()
        for (state in locAdds[zone.id].orEmpty()) out += state.toPacket()
        for (state in locAnims[zone.id].orEmpty()) out += state.toPacket()
        if (includeFirstLightScaffolding) {
            out += FirstLightSceneBootstrap.packets(localSceneZone.level, localSceneZone.zoneX, localSceneZone.zoneY)
        }
        return out
    }

    fun clear() {
        groundItems.clear()
        locAdds.clear()
        locDeletes.clear()
        locAnims.clear()
    }

    fun addGroundItem(tile: Tile, objId: Int, count: Int) {
        groundItems.listFor(tile).add(GroundItemState(tile, objId, count))
        Zones.queue(tile.zone, ObjAdd(packedCoord = packedCoord(tile), objId = objId, count = count))
    }

    fun removeGroundItem(tile: Tile, objId: Int) {
        val list = groundItems.listFor(tile)
        list.removeIf { it.tile == tile && it.objId == objId }
        Zones.queue(tile.zone, ObjDel(packedCoord = packedCoord(tile), objIdLo = objId and 0xff, objIdHi = objId ushr 8))
    }

    fun updateGroundItemCount(tile: Tile, objId: Int, count: Int, playerIndex: Int = 0) {
        val list = groundItems.listFor(tile)
        list.removeIf { it.tile == tile && it.objId == objId }
        list.add(GroundItemState(tile, objId, count))
        Zones.queue(
            tile.zone,
            ObjCount(
                playerIndex = playerIndex,
                objIdLo = objId and 0xff,
                objIdHi = objId ushr 8,
                packedCoord = packedCoord(tile),
                count = count,
            )
        )
    }

    fun addLoc(tile: Tile, locId: Int, shape: ObjectShape, rotation: Int, extra: Int? = null) {
        addLoc(tile, locId, shapeFlags(shape, rotation), extra)
    }

    fun addLoc(tile: Tile, locId: Int, shapeFlags: Int, extra: Int? = null) {
        val list = locAdds.listFor(tile)
        list.removeIf { it.tile == tile && it.shapeFlags == shapeFlags }
        list.add(LocAddState(tile, locId, shapeFlags, extra))
        locDeletes[tile.zone.id]?.removeIf { it.tile == tile && it.shapeFlags == shapeFlags }
        WorldCollisionProvider.applyLocAdd(tile, locId, shapeFlags)
        Zones.queue(tile.zone, LocAdd(packedCoord = packedCoord(tile), locId = locId, shapeFlags = shapeFlags, extra = extra))
    }

    fun deleteLoc(tile: Tile, shape: ObjectShape, rotation: Int) {
        deleteLoc(tile, shapeFlags(shape, rotation))
    }

    fun deleteLoc(tile: Tile, shapeFlags: Int) {
        val addedLocs = locAdds[tile.zone.id].orEmpty().filter { it.tile == tile && it.shapeFlags == shapeFlags }
        locAdds[tile.zone.id]?.removeIf { it.tile == tile && it.shapeFlags == shapeFlags }
        val list = locDeletes.listFor(tile)
        if (list.none { it.tile == tile && it.shapeFlags == shapeFlags }) {
            list.add(LocDeleteState(tile, shapeFlags))
        }
        for (loc in addedLocs) {
            WorldCollisionProvider.applyLocRemove(tile, loc.locId, shapeFlags)
        }
        WorldCollisionProvider.applyLocDel(tile, shapeFlags)
        Zones.queue(tile.zone, LocDel(shapeFlags = shapeFlags, packedCoord = packedCoord(tile)))
    }

    fun animateLoc(tile: Tile, animId: Int, shapeFlags: Int, unknown1: Int = 0, delay: Int = 0, speed: Int = 0, mode: Int = 0) {
        locAnims.listFor(tile).add(LocAnimState(tile, animId, shapeFlags, unknown1, delay, speed, mode))
        Zones.queue(
            tile.zone,
            LocAnim(
                packedCoord = packedCoord(tile),
                animId = animId,
                shapeFlags = shapeFlags,
                unknown1 = unknown1,
                delay = delay,
                speed = speed,
                mode = mode,
            )
        )
    }

    fun shapeFlags(shape: ObjectShape, rotation: Int): Int = (shape.id shl 2) or (rotation and 0x3)

    fun packedCoord(tile: Tile): Int = tile.chunkLocalHash

    private fun <T> ConcurrentHashMap<Int, CopyOnWriteArrayList<T>>.listFor(tile: Tile): CopyOnWriteArrayList<T> =
        computeIfAbsent(tile.zone.id) { CopyOnWriteArrayList() }

    private data class GroundItemState(val tile: Tile, val objId: Int, val count: Int) {
        fun toPacket(): ObjAdd = ObjAdd(packedCoord = packedCoord(tile), objId = objId, count = count)
    }

    private data class LocAddState(val tile: Tile, val locId: Int, val shapeFlags: Int, val extra: Int?) {
        fun toPacket(): LocAdd = LocAdd(packedCoord = packedCoord(tile), locId = locId, shapeFlags = shapeFlags, extra = extra)
    }

    private data class LocDeleteState(val tile: Tile, val shapeFlags: Int) {
        fun toPacket(): LocDel = LocDel(shapeFlags = shapeFlags, packedCoord = packedCoord(tile))
    }

    private data class LocAnimState(
        val tile: Tile,
        val animId: Int,
        val shapeFlags: Int,
        val unknown1: Int,
        val delay: Int,
        val speed: Int,
        val mode: Int,
    ) {
        fun toPacket(): LocAnim =
            LocAnim(
                packedCoord = packedCoord(tile),
                animId = animId,
                shapeFlags = shapeFlags,
                unknown1 = unknown1,
                delay = delay,
                speed = speed,
                mode = mode,
            )
    }
}
