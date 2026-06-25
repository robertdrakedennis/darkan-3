package com.undercut.script.impl.qb.Temporary

import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.findClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer

@ScriptDescription(
    name = "Fort Build",
    version = "1.0.0",
    author = "Billy",
    description = "Builds optimal construction hotspots in Fort Forinthry"
)
class FortBuild : StateMachineScript<FortBuild>() {
    override fun getStartState(): State<FortBuild> = CheckResources()

    var hasClickedBuild = false
    val activePlay = true // Set based on preference
}

class CheckResources : State<FortBuild>() {
    override suspend fun FortBuild.checkNext(): State<FortBuild>? {
        inventory.filter { it.name.contains("Plank") }
        inventory.filter { it.name.contains("Stone wall segment") }
        val hotspot = findClosestObject("Optimal Construction hotspot", 60)
        val buildspot = findClosestObject("Construction hotspot", 60)

        return when {
            hotspot != null -> Build()
            buildspot != null -> BuildSpot()
//            planks.size >= 10 && stoneBricks.size >= 6 -> CheckBlueprints
            else -> null
        }
    }

    override suspend fun FortBuild.stateLoop() {
        delay(600, 800)
    }
}

class Build : State<FortBuild>() {
    override suspend fun FortBuild.checkNext(): State<FortBuild>? {
        val hotspot = findClosestObject("Optimal Construction hotspot", 60)
        return if (hotspot == null) CheckResources() else null
    }

    override suspend fun FortBuild.stateLoop() {
        val hotspot = findClosestObject("Optimal Construction hotspot", 60) ?: return

        if (!localPlayer.isMoving) {
            val distance = localPlayer.tile.getDistance(hotspot.tile)

            if (!hasClickedBuild && distance >= 1) {
                if (hotspot.interact("Build")) {
                    println("Found construction spot")
                    hasClickedBuild = true

                    if (activePlay) {
                        delay(800, 1200)
                    } else {
                        delay(3000, 10000)
                    }
                }
            } else if (hasClickedBuild && distance > 1) {
                if (activePlay) {
                    delay(800, 1200)
                } else {
                    delay(3000, 10000)
                }
                hasClickedBuild = false
            }
        }
    }
}

class BuildSpot : State<FortBuild>() {
    override suspend fun FortBuild.checkNext(): State<FortBuild>? {
        val hotspot = findClosestObject("Construction hotspot", 60)
        return if (hotspot == null) CheckResources() else null
    }

    override suspend fun FortBuild.stateLoop() {
        val hotspot = findClosestObject("Construction hotspot", 60) ?: return

        if (!localPlayer.isMoving) {
            val distance = localPlayer.tile.getDistance(hotspot.tile)

            if (!hasClickedBuild && distance >= 1) {
                if (hotspot.interact("Build")) {
                    println("Found construction spot")
                    hasClickedBuild = true

                    if (activePlay) {
                        delay(800, 1200)
                    } else {
                        delay(3000, 10000)
                    }
                }
            } else if (hasClickedBuild && distance > 1) {
                if (activePlay) {
                    delay(800, 1200)
                } else {
                    delay(3000, 10000)
                }
                hasClickedBuild = false
            }
        }
    }
}

//object Bank : State<FortBuild>() {
//    override suspend fun FortBuild.checkNext(): State<FortBuild>? {
//        val planks = inventory.filter { it.name.contains("Plank") }
//        val stoneBricks = inventory.filter { it.name.contains("Stone wall segment") }
//
//        return if (planks.size >= 10 && stoneBricks.size >= 6) CheckResources() else null
//    }
//
//    override suspend fun FortBuild.stateLoop() {
//        val planks = inventory.filter { it.name.contains("Plank") }
//        val stoneBricks = inventory.filter { it.name.contains("Stone wall segment") }
//
//        if (planks.size < 10 || stoneBricks.size < 6) {
//            println("No Planks or stone bricks")
//
//            if (bankOpen) {
//                when {
//                    planks.size < 10 -> {
//                        // Withdraw planks (ID 960)
//                        bank.withdraw(960, 4)
//                        delay(1200, 1800)
//                    }
//                    stoneBricks.size < 6 -> {
//                        // Withdraw stone wall segments (ID 54460)
//                        bank.withdraw(54460, 4)
//                        delay(1200, 1800)
//                    }
//                }
//            } else {
//                if (interactClosestObject("Bank chest", "Use")) {
//                    println("Interacting with chest")
//                    delay(600, 800)
//                }
//            }
//        }
//    }
//}

//object CheckBlueprints : State<FortBuild>() {
//    override suspend fun FortBuild.checkNext(): State<FortBuild>? = CheckResources()
//
//    override suspend fun FortBuild.stateLoop() {
//        val planks = inventory.filter { it.name.contains("Plank") }
//        val stoneBricks = inventory.filter { it.name.contains("Stone wall segment") }
//
//        if (planks.size >= 10 && stoneBricks.size >= 6) {
//            if (interactClosestObject("Fort Forinthry blueprints", "Check plans")) {
//                delay(600, 800)
//
//                // Interact with dialogue option - may need adjustment based on actual interface
//                val component = interfaces.getComponent(89784350, 89784350)
//                component?.interact()
//                delay(600, 800)
//            }
//        }
//    }
//}