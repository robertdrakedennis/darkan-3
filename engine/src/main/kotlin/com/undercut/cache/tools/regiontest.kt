package com.undercut.cache.tools

import com.undercut.cache.type.maps.Region
import com.undercut.game.Tile
import com.undercut.pathfinder.routeToTile
import com.undercut.pathfinder.toTiles

fun main(args: Array<String>) {
    // regionId uses 8-bit packing: (regionX shl 8) or regionY
    // 6439 is the MAPSV2 archive ID (7-bit), not the regionId
    // For region (39, 50) containing tile (2514, 3256): regionId = (39 shl 8) or 50 = 10034
    val regionId = (39 shl 8) or 50
    Region.get(regionId)
    Region.get(regionId).objectList?.forEach {
        if (it.name().isNotEmpty())
            println(it.name())
    }
    val route = routeToTile(Tile.of(2514, 3256, 0), Tile.of(2514, 3228, 0))
    println(route.success)
    println(route.coords.toArray().contentToString())
    println(route.toTiles(0).toTypedArray().contentToString())
}