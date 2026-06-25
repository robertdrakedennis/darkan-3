package com.undercut.script.impl.trent.vyres

import com.undercut.pathfinder.hasLineOfSight
import com.undercut.script.*
import com.undercut.script.api.*

@ScriptDescription(
    name = "Vyres",
    version = "1.0.0",
    author = "Trent",
    description = "Kills them vyres"
)
class Vyres : StateMachineScript<Vyres>(), ConfigurableScript {
    val attackRange = IntConfigItem(
        name = "Attack range",
        description = "Range to attack npcs from",
        initialValue = 1
    )
    override fun getStartState() = Attack
}

object Attack: State<Vyres>() {
    override suspend fun Vyres.checkNext() = null

    override suspend fun Vyres.stateLoop() {
        if (areaLootOpen && areaLootContainsHoldableItems) {
            lootAllAreaLoot()
            delay(1000, 630)
        }
        togglePrayer(Prayer.SOUL_SPLIT, prayerPoints > 0)
        togglePrayer(Prayer.LEECH_MELEE_STRENGTH, prayerPoints > 500)
        npcs[localPlayer.interactionSid]?.currentHealth?.let { if (localPlayer.isInteracting && (it > 0)) return delay(150, 250) }
//        val closestTarget = allNpcsWithinRange(attackRange.value)
//            .filter { it.name.contains("Vyre") && it.hasOption("Attack") && hasLineOfSight(localPlayer.tile, 1, it.tile, 1) }
//            .minByOrNull { it.tile.getDistance(localPlayer.tile) }
//
//        if (!channelingAbility && closestTarget?.interact("Attack") == true)
//            delay(1940, 1100)
        delay(250, 200)
    }
}
