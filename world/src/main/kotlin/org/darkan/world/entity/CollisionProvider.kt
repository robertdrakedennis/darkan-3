package org.darkan.world.entity

import world.gregs.voidps.collision.CollisionFlagMap
import world.gregs.voidps.type.Tile

interface CollisionProvider {
    val flags: CollisionFlagMap

    fun ensureLoadedForRoute(from: Tile)

    fun flagsFor(tile: Tile): Int
}

object WorldCollisionProvider : CollisionProvider {
    override val flags: CollisionFlagMap
        get() = WorldCollisionFlagMap.flags

    override fun ensureLoadedForRoute(from: Tile) {
        WorldCollisionFlagMap.ensureLoadedForRoute(from)
    }

    override fun flagsFor(tile: Tile): Int {
        WorldCollisionFlagMap.ensureLoadedForRoute(tile)
        return WorldCollisionFlagMap.flags.getFlags(tile)
    }

    fun applyLocAdd(tile: Tile, locId: Int, shapeFlags: Int) {
        WorldCollisionFlagMap.applyLocAdd(tile, locId, shapeFlags)
    }

    fun applyLocRemove(tile: Tile, locId: Int, shapeFlags: Int) {
        WorldCollisionFlagMap.applyLocRemove(tile, locId, shapeFlags)
    }

    fun applyLocDel(tile: Tile, shapeFlags: Int) {
        WorldCollisionFlagMap.applyLocDel(tile, shapeFlags)
    }
}
