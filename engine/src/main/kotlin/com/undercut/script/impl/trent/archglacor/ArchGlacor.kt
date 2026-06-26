package com.undercut.script.impl.trent.archglacor
import com.undercut.game.tileOfLocal

import world.gregs.voidps.type.Tile
import com.undercut.game.interfaces.Ability
import com.undercut.script.ConfigurableScript
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.script.impl.trent.AutoPrayerSwitcher
import com.undercut.script.impl.trent.CombatRotation
import com.undercut.util.random

private const val ENTRANCE_PORTAL = 121338
private const val EXIT_PORTAL = 121339

private val isOutside get() = Tile.of(1751, 1104, 0).withinDistance(localPlayer.tile, 20)
private val centerTile get() = tileOfLocal(34, 44, 1)
private val centerWars = Tile.of(3295, 10146, 0)
private val atWarsRetreat get() = Tile.of(3295, 10146, 0).withinDistance(localPlayer.tile, 50)

@ScriptDescription(
    name = "Arch Glacor",
    version = "1.0.0",
    author = "Trent",
    description = "Kills arch glacor"
)
class ArchGlacor : StateMachineScript<ArchGlacor>(), ConfigurableScript {
    override fun getStartState() = Init

    override fun onStart() {
        addParallelScript(CombatRotation())
        addParallelScript(AutoPrayerSwitcher())
    }
}

object Init : State<ArchGlacor>() {
    override suspend fun ArchGlacor.checkNext() = if (isOutside) StartInstance else Fight
    override suspend fun ArchGlacor.stateLoop() {}
}

object StartInstance : State<ArchGlacor>() {
    override suspend fun ArchGlacor.checkNext() = if (inventory.isFull) Bank else if (!isOutside) Fight else null

    override suspend fun ArchGlacor.stateLoop() {
        if (interactClosestObject(ENTRANCE_PORTAL, "Enter"))
            delayUntil(10000) { instanceStartInterfaceOpen }
        if (instanceStartInterfaceOpen && startInstance())
            delayUntil(10000) { !isOutside }
    }
}

object Fight : State<ArchGlacor>() {
    override suspend fun ArchGlacor.checkNext() = if (inventory.isFull) Bank else if (isOutside) StartInstance else null

    override suspend fun ArchGlacor.stateLoop() {
        if (!areaLootOpen && !groundItems.isEmpty() && openAreaLoot())
            delay(1000, 630)
        if (areaLootOpen && areaLootContainsHoldableItems) {
            lootAllAreaLoot()
            delay(1000, 630)
        }
        if (instanceExpired && interactClosestObject(EXIT_PORTAL, "Exit")) {
            delayUntil(10000) { isOutside }
            return
        }
        if (!centerTile.withinDistance(localPlayer.tile, 5) && walkTo(centerTile.randomizeX(1), false))
            waitThenDelayUntil(5000, timeoutMillis = random(10000L, 15000L)) { centerTile.withinDistance(localPlayer.tile, 8) }
        if (!inCombat && interactClosestNPC("Attack"))
            delayUntil(5000) { inCombat }
    }
}

object Bank : State<ArchGlacor>() {
    override suspend fun ArchGlacor.checkNext() = if (!inventory.isFull && isOutside) StartInstance else null
    override suspend fun ArchGlacor.stateLoop() {
        if (inventory.isFull) {
            if (!atWarsRetreat) {
                castAbility(Ability.WARS_RETREAT_TELEPORT)
                delayUntil(random(5000L, 8000L)) { atWarsRetreat }
                return
            }
            if (loadLastPresetClosestBank())
                delayUntil(5000) { !inventory.isFull }
            else
                delay(1000, 250)
            return
        }
        val portal = findClosestObject("Portal (Arch-Glacor)")
        if (portal?.interact("Enter") == true)
            delayUntil(15000) { isOutside }
        else if (walkTo(centerWars.randomize(2), false))
            delay(2000, 1059)
    }
}