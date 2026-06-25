package com.undercut.script.impl.devin

import com.undercut.game.Tile
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.event.impl.ManualDoAction
import com.undercut.util.gaussian

@ScriptDescription(
    name = "Generic Pickpocket",
    version = "1.0.0",
    author = "Devin",
    description = "Pickpockets anything and stands near the bank to heal."
)
class GenericPickpocket : StateMachineScript<GenericPickpocket>() {
    lateinit var npcName: String
    lateinit var startTile: Tile
    fun selectedNPCName() = ::npcName.isInitialized

    override fun getStartState() = Init
}

object Init : State<GenericPickpocket>() {
    override suspend fun GenericPickpocket.checkNext(): State<GenericPickpocket>? {
        return if (selectedNPCName()) Pickpocket else null
    }

    override suspend fun GenericPickpocket.stateLoop() {}

    override fun GenericPickpocket.onStateEvent(event: Event) {
        if (event !is ManualDoAction || event.target !is NPC) return
        val npc = event.target
        if (!selectedNPCName() && npc.hasOption("Pickpocket")) {
            npcName = npc.name()
            startTile = npc.tile
        }
    }
}

object Pickpocket : State<GenericPickpocket>() {
    override suspend fun GenericPickpocket.checkNext() = if (healthCurrent < 500 || inventory.isFull) Heal else null

    override suspend fun GenericPickpocket.stateLoop() {
        if (inCombat) {
            val target = findNPC { it.interactingWith(localPlayer) && it.currentHealth > 0 }
            if (target != null) {
                if (!localPlayer.isInteracting && target.interact("Attack"))
                    delayUntil(gaussian(2115L, 4421L)) { localPlayer.isInteracting }
                return
            }
        }

        if (inventory.freeSlots < 2) {
            inventory.forEach {
                if (!it.getDef().isStackable) {
                    it.click("Drop")
                    delay(554, 733)
                }
            }
        }

        if (interactClosestNPC(npcName, "Pickpocket"))
            delay(712, 969)
        else if (walkTo(startTile.randomize(3), true))
            waitThenDelayUntil(gaussian(6436L, 2114L)) { localPlayer.tile.withinDistance(startTile, 3) }
    }
}

object Heal : State<GenericPickpocket>() {
    override suspend fun GenericPickpocket.checkNext() =
        if (healthCurrent > 500 && !inventory.isFull) Pickpocket else null

    override suspend fun GenericPickpocket.stateLoop() {
        val bank = findClosestReachableObjectWithOption("Load Last Preset from", 25) ?: return
        bank.interact("Load Last Preset from")
        waitThenDelayUntil(1200, gaussian(14285L, 6629L)) { !inventory.isFull && healthPercent > 90.0 }
    }
}