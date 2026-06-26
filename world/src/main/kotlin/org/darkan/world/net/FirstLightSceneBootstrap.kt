package org.darkan.world.net

import org.darkan.core.net.prot.LocAdd
import org.darkan.core.net.prot.LocDel
import org.darkan.core.net.prot.ObjAdd
import org.darkan.core.net.prot.ServerProt

object FirstLightSceneBootstrap {
    private data class ZoneKey(val level: Int, val zoneX: Int, val zoneY: Int)

    private val packetsByZone: Map<ZoneKey, List<ServerProt>> = buildMap {
        put(ZoneKey(0, 14, 14), listOf(
            ObjAdd(packedCoord = 84, objId = 1, count = 946),
            ObjAdd(packedCoord = 96, objId = 1, count = 558),
        ))
        put(ZoneKey(0, 14, 16), listOf(
            ObjAdd(packedCoord = 83, objId = 1, count = 882),
        ))
        put(ZoneKey(0, 15, 14), listOf(
            ObjAdd(packedCoord = 6, objId = 1, count = 1923),
            ObjAdd(packedCoord = 22, objId = 1, count = 1931),
            ObjAdd(packedCoord = 52, objId = 1, count = 1935),
        ))
        put(ZoneKey(0, 15, 20), listOf(
            LocAdd(packedCoord = 96, locId = 89768, shapeFlags = 40),
        ))
        put(ZoneKey(0, 17, 13), listOf(
            ObjAdd(packedCoord = 2, objId = 1, count = 946),
        ))
        put(ZoneKey(0, 19, 12), listOf(
            LocDel(shapeFlags = 42, packedCoord = 86),
            LocDel(shapeFlags = 43, packedCoord = 87),
            LocDel(shapeFlags = 43, packedCoord = 68),
            LocDel(shapeFlags = 42, packedCoord = 84),
            LocDel(shapeFlags = 42, packedCoord = 53),
            LocDel(shapeFlags = 41, packedCoord = 102),
            LocDel(shapeFlags = 42, packedCoord = 103),
            LocDel(shapeFlags = 42, packedCoord = 67),
            LocDel(shapeFlags = 41, packedCoord = 83),
            LocDel(shapeFlags = 40, packedCoord = 37),
            LocDel(shapeFlags = 40, packedCoord = 36),
        ))
        put(ZoneKey(0, 19, 13), listOf(
            LocDel(shapeFlags = 41, packedCoord = 81),
        ))
        put(ZoneKey(0, 20, 18), listOf(
            ObjAdd(packedCoord = 5, objId = 1, count = 1203),
        ))
        put(ZoneKey(0, 20, 21), listOf(
            LocAdd(packedCoord = 67, locId = 2306, shapeFlags = 130, extra = 0),
            LocAdd(packedCoord = 66, locId = 2320, shapeFlags = 130, extra = 0),
        ))
        put(ZoneKey(0, 21, 22), listOf(
            ObjAdd(packedCoord = 101, objId = 1, count = 1925),
        ))
        put(ZoneKey(0, 22, 16), listOf(
            LocAdd(packedCoord = 39, locId = 70755, shapeFlags = 41),
        ))
        put(ZoneKey(0, 22, 17), listOf(
            LocAdd(packedCoord = 32, locId = 40356, shapeFlags = 171, extra = 0),
        ))
        put(ZoneKey(1, 14, 15), listOf(
            LocDel(shapeFlags = 2, packedCoord = 118),
        ))
        put(ZoneKey(1, 15, 15), listOf(
            LocAdd(packedCoord = 6, locId = 36845, shapeFlags = 3),
            ObjAdd(packedCoord = 65, objId = 1, count = 1205),
        ))
        put(ZoneKey(1, 22, 16), listOf(
            LocDel(shapeFlags = 43, packedCoord = 23),
        ))
        put(ZoneKey(2, 14, 16), listOf(
            ObjAdd(packedCoord = 80, objId = 1, count = 1511),
            ObjAdd(packedCoord = 82, objId = 1, count = 1511),
        ))
        put(ZoneKey(2, 15, 16), listOf(
            ObjAdd(packedCoord = 1, objId = 1, count = 1511),
            ObjAdd(packedCoord = 16, objId = 1, count = 1511),
        ))
        put(ZoneKey(2, 17, 14), listOf(
            ObjAdd(packedCoord = 87, objId = 1, count = 1265),
        ))
        put(ZoneKey(2, 17, 15), listOf(
            ObjAdd(packedCoord = 87, objId = 1, count = 1265),
        ))
    }

    fun packets(level: Int, zoneX: Int, zoneY: Int): List<ServerProt> =
        packetsByZone[ZoneKey(level, zoneX, zoneY)].orEmpty()

    fun packetCount(): Int = packetsByZone.values.sumOf { it.size }
}
