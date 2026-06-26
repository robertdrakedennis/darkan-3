package com.undercut.script.impl.qb.Temporary

import world.gregs.voidps.type.Tile
import com.undercut.game.interfaces.IFSlot
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.util.random

@ScriptDescription(
    name = "HerbyWerby State Machine",
    version = "1.0.0",
    author = "Converted from BWU",
    description = "Automated HerbyWerby minigame with state machine logic"
)
class HerbyWerby : StateMachineScript<HerbyWerby>() {

    private val herbyEntranceArea = Tile(5611, 2453, 0)
    private val herbyCompletionVarbit = 44351
    private val herbyCompletionValue = 100

    override fun getStartState(): State<HerbyWerby> = CheckCompletionState()

    // State: Check if HerbyWerby is already completed
    class CheckCompletionState : State<HerbyWerby>() {
        override suspend fun HerbyWerby.checkNext(): State<HerbyWerby>? {
            return when {
                varps.getVarBit(herbyCompletionVarbit) == herbyCompletionValue -> IdleState()
                isNearRitualFire() -> CollectHerbState()
                isAtEntrance() -> EnterHerbyState()
                else -> TravelToEntranceState()
            }
        }

        override suspend fun HerbyWerby.stateLoop() {
            delay(random(1000, 1500))
        }
    }

    // State: Idle when activity is completed
    class IdleState : State<HerbyWerby>() {
        override suspend fun HerbyWerby.checkNext(): State<HerbyWerby>? {
            return if (varps.getVarBit(herbyCompletionVarbit) != herbyCompletionValue) {
                CheckCompletionState()
            } else null
        }

        override suspend fun HerbyWerby.stateLoop() {
            println("HerbyWerby completed for this week!")
            delay(random(5000, 10000))
        }
    }

    // State: Travel to HerbyWerby entrance
    class TravelToEntranceState : State<HerbyWerby>() {
        override suspend fun HerbyWerby.checkNext(): State<HerbyWerby>? {
            return when {
                isAtEntrance() -> EnterHerbyState()
                isNearRitualFire() -> CollectHerbState()
                else -> null
            }
        }

        override suspend fun HerbyWerby.stateLoop() {
            println("Traveling to HerbyWerby entrance...")

            // Use traversal system to get to entrance
//            if (!Traversal.traversal().to(herbyEntranceArea).traverse()) {
//                println("Failed to traverse to HerbyWerby entrance")
//                delay(random(2000, 3000))
//            }

            delay(random(1000, 2000))
        }
    }

    // State: Enter HerbyWerby by climbing down tree roots
    class EnterHerbyState : State<HerbyWerby>() {
        override suspend fun HerbyWerby.checkNext(): State<HerbyWerby>? {
            return when {
                isNearRitualFire() -> CollectHerbState()
                !isAtEntrance() -> TravelToEntranceState()
                else -> null
            }
        }

        override suspend fun HerbyWerby.stateLoop() {
            println("At HerbyWerby entrance, preparing to enter...")

            // Remove weapon and shield if equipped
            removeEquippedWeapons()

            // Find and interact with tree roots
            val treeRoots = findClosestObject("Tree roots")
            if (treeRoots != null) {
                println("Found tree roots, climbing down...")
                treeRoots.interact("Climb down")

                // Wait for interface to open
                waitForInterface(1188, 10000)
                delay(random(500, 1000))
                if(interfaces.isOpen(1188))
                {
                    IFSlot(188, 8, -1).click()
                }
                // Handle dialog if needed
                // Dialog handling would go here based on the interface

                delay(random(300, 4000))
            } else {
                println("Could not find tree roots")
                delay(random(1000, 2000))
            }
        }
    }

    // State: Collect herbs from green zygomites
    class CollectHerbState : State<HerbyWerby>() {
        override suspend fun HerbyWerby.checkNext(): State<HerbyWerby>? {
            return when {
                varps.getVarBit(herbyCompletionVarbit) == herbyCompletionValue -> IdleState()
                !isNearRitualFire() -> CheckCompletionState()
                else -> null
            }
        }

        override suspend fun HerbyWerby.stateLoop() {
            println("Collecting herbs from green zygomites...")

            findClosestNPCWithOption("Take herb")?.let{
                    zygomite -> if( zygomite.interact("Take herb") ) {
                    waitThenDelayUntil(856) { localPlayer.isAnimating }
                    while(zygomite.exists()) {
                        // Check for spot animation indicating herb collection
                        if (zygomite.spotAnims.any { it.id == 7237 }) {
                            delay(225, 350)
                            if (findClosestNPC { it.hasOption("Take herb") }?.interact("Take herb") == true)
                                delayUntil(30000) { !zygomite.spotAnims.any { it.id == 7237 } || !zygomite.exists() }
                        }
                        delay(150, 100)
                    }

                }

            }
            // Find green zygomite with specific spot animation
//            val greenZygomite = findClosestNPCWithOption("Take herb")// Spot animation ID for green zygomite
//            val greenZygomiteWithHerb =
//            //val greenZygomite = findClosestSpotAnim(7237, 30) // Spot animation ID for green zygomite
//            if (greenZygomite != null) {
//                println("Found green zygomite with herb, collecting...")
//                findClosestSpotAnim(7237, 30)?.takeIf {it.spotAnims. }("Collect herb")
//                delay(random(1000, 1500))
//            } else {
//                println("No green zygomite with herb found, waiting...")
//                delay(random(2000, 3000))
//            }
        }
    }

    // Helper functions
    private fun isAtEntrance(): Boolean {
        return localPlayer.tile.getDistance(herbyEntranceArea) <= 5
    }

    private fun isNearRitualFire(): Boolean {
        return findClosestObject("Ritual fire") != null
    }

    private fun removeEquippedWeapons() {
        // Remove weapon if equipped
//        val weaponSlot = Equipment.Companion.Slot.useItemInSlot(Equipment.Companion.Slot.WEAPON) // Weapon slot
//        println("Removing equipped weapon...")
//        //weaponSlot?
//        delay(random(500, 1000))
//
//        // Remove shield if equipped
//        val shieldSlot = getEquippedItem(5) // Shield slot
//        if (shieldSlot != null) {
//            println("Removing equipped shield...")
//            shieldSlot.interact("Remove")
//            delay(random(500, 1000))
//        }
    }

//    private fun findNearestObject(name: String): SceneObject? {
//        return getObjects().filter { it.name == name && !it.hidden }.minByOrNull {
//            it.location.distanceTo(localPlayer.location)
//        }
//    }

//    private fun findNearestNPCWithSpotAnimation(spotAnimId: Int): NPC? {
//        return
//        npcs.filter {
//            it.spotAnimation == spotAnimId
//        }.minByOrNull {
//            it.location.distanceTo(localPlayer.location)
//        } as NPC?
//    }

    private suspend fun waitForInterface(interfaceId: Int, timeout: Long) {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeout) {
            if (interfaces.isOpen(interfaceId)) {
                return
            }
            delay(100)
        }
    }
}