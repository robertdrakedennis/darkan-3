package com.undercut.game
import com.undercut.game.tileOfSceneLocal
import com.undercut.game.tileOfLocal

import com.undercut.game.nxt.DoActionOpcode
import com.undercut.pathfinder.WorldCollision
import com.undercut.profiling.PlayerProfiles
import com.undercut.script.api.localPlayer
import world.gregs.voidps.type.Tile

data class LocalTile(val x: Short, val y: Short)

fun tileOfLocal(localX: Int, localY: Int, plane: Int): Tile {
    val base = localPlayer.tile
    return Tile((base.regionX shl 6) + localX, (base.regionY shl 6) + localY, plane)
}

fun tileOfSceneLocal(sceneLocalX: Int, sceneLocalY: Int, plane: Int): Tile? {
    val base = WorldCollision.sceneBase ?: return null
    return Tile(base.x + sceneLocalX, base.y + sceneLocalY, plane)
}

fun Tile.localizeScene(): LocalTile? {
    val base = WorldCollision.sceneBase ?: return null
    return LocalTile((x - base.x).toShort(), (y - base.y).toShort())
}

fun Tile.target(): Boolean {
    if (!localPlayer.tile.withinDistance(this, 20)) return false
    DoActionOpcode.SELECT_TILE.fire(0, x, y)
    return true
}

fun Tile.withinInteractionRange(other: Tile): Boolean =
    withinDistance(other, PlayerProfiles.get().interactDistanceRange)
