package com.undercut.script.impl.trent

import com.undercut.game.Skill
import world.gregs.voidps.type.Tile
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.*

//private val trapNames = setOf("Box trap", "Shaking box")
private val trapNames = setOf("Marasamaw plant", "Wilted marasamaw plant", "Shaking marasamaw plant") //Pick, Pick, Check
private val crossOffsets = listOf(
    0 to 0,
    0 to 1,
    0 to -1,
    1 to 0,
    -1 to 0
)

@ScriptDescription(
    name = "Chinchompa Hunter",
    version = "1.0.0",
    author = "Trent",
    description = "Replaces hunter traps and catches what is in them."
)
class ChinchompaHunting : Script() {
    lateinit var startTile: Tile

    override fun onStart() {
        startTile = localPlayer.tile
    }

    override suspend fun loop() {
        if (!areaLoot.isEmpty) {
            lootAllAreaLoot()
            return delay(2500, 5000)
        }
        if (localPlayer.isAniMoving) return delay(850, 1000)

//        if (interactClosestReachableObject("Shaking box", "Check")) {
        if (interactClosestReachableObject("Shaking marasamaw plant", "Check")) {
            delay(1200)
            waitUntilNotAniMoving()
            placeTrap(localPlayer.tile)
            return
        }

        groundItems.forEach { println(it.name)
            println(it.groundOps)
        }


        val maxTraps = getMaxTraps(getCurrentLevel(Skill.HUNTER))
        val traps = getAllObjectsWithinRange(6).filter { trapNames.contains(it.name()) }
        if (traps.size < maxTraps) {
            val occupiedTiles = traps.filter { trapNames.contains(it.name()) }.map { it.tile }.toSet()
            val availableTile = crossOffsets.map { (x, y) -> startTile.transform(x, y) }.firstOrNull { it !in occupiedTiles } ?: return
            return placeTrap(availableTile)
        }
        if (interactClosestReachableObject("Wilted marasamaw plant", "Rebuild")) {
            delay(1200)
            waitUntilNotAniMoving()
            return
        }
    }

    suspend fun placeTrap(tile: Tile) {
        if (localPlayer.tile != tile) {
            walkTo(tile, false)
            waitUntilNotAniMoving()
        }
        inventory.clickItem("Marasamaw plant", "Lay")
        waitUntilNotAniMoving()
    }

    private fun getMaxTraps(level: Int): Int = when {
        level < 30 -> 2
        level < 60 -> 3
        level < 80 -> 4
        else -> 5
    }
}