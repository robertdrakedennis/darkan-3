package com.undercut.script.impl.qb.Temporary

import com.undercut.game.Tile
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.interfaces.effects.Effect
import com.undercut.script.*
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.util.random

@ScriptDescription(
    name = "Beach Event",
    version = "1.0.0",
    author = "Query & Billy",
    description = "Fully automated beach event using state machine pattern for various activities.",
)
class BeachEvent : StateMachineScript<BeachEvent>(), ConfigurableScript {

    // Config items for the settings menu
    val selectedActivity = EnumConfigItem(
        name = "Beach Activity",
        description = "Select which beach activity to do",
        enumValues = BeachActivity.entries.toTypedArray(),
        initialValue = BeachActivity.SANDCASTLE
    )

    val enableDebug = BooleanConfigItem(
        name = "Debug Mode", description = "Enable debug output", initialValue = true
    )

    val useDrinks = BooleanConfigItem(
        name = "Use Drinks", description = "Drink the respective drink for what is being trained.", initialValue = true
    )

    val useSpotlight = BooleanConfigItem(
        name = "Use Spotlight", description = "Automatically switch to spotlight activity", initialValue = true
    )

    val waitForHappyHour = BooleanConfigItem(
        name = "Wait for Happy Hour", description = "Wait for Happy Hour before starting", initialValue = false
    )

    // Add a runtime variable for the current activity
    var currentActivity: BeachActivity = BeachActivity.SANDCASTLE

    // Getter that returns the current runtime activity
    val activity: BeachActivity
        get() = currentActivity

    // This getter ensures the config values are used
//    val activity: BeachActivity
//        get() = selectedActivity.value

    // This getter ensures config values are used
    val debug: Boolean
        get() = enableDebug.value

    // This getter ensures config values are used
    val spotlight: Boolean
        get() = useSpotlight.value

    // This getter ensures config values are used
    val happyHour: Boolean
        get() = waitForHappyHour.value

    // Beach Temperature and Happy Hour Varbits
    val BEACH_TEMP_VARBIT = 28441
    val HAPPY_HOUR_VARBIT = 33485
    val SPOTLIGHT_ACTIVITY_VARBIT = 28460
    val MAX_BEACH_TEMP = 1500

    // Statistics tracking
    var activityCompletions = 0
    var animationDelay = 0

    override fun getStartState() = BeachMainState()

    enum class BeachActivity(
        val displayName: String,
        val location: Tile,
        val area: Area,
        val spotlightId: Int
    ) {
        COCONUT_SHY("Coconut Shy", Tile.of(3262, 3234, 0), Area.Circular(Tile.of(3262, 3234, 0), 5.0), 0),
        SANDCASTLE("Sandcastle Building", Tile.of(3256, 3240, 0), Area.Circular(Tile.of(3256, 3240, 0), 5.0), 1),
        BARBEQUES("Barbeques", Tile.of(3272, 3235, 0), Area.Circular(Tile.of(3272, 3235, 0), 5.0), 2),
        ROCK_POOLS("Rock Pools", Tile.of(3263, 3245, 0), Area.Circular(Tile.of(3263, 3245, 0), 5.0), 3),
        PALM_TREE("Palm Tree Farming", Tile.of(3256, 3229, 0), Area.Circular(Tile.of(3256, 3229, 0), 5.0), 4),
        BODYBUILDING("Body Building", Tile.of(3270, 3227, 0), Area.Circular(Tile.of(3270, 3227, 0), 5.0), 5),
        HOOK_A_DUCK("Hook a Duck", Tile.of(3275, 3243, 0), Area.Circular(Tile.of(3275, 3243, 0), 5.0), 6),
        DUNGEONEERING("Dungeoneering Hole", Tile.of(3249, 3236, 0), Area.Circular(Tile.of(3249, 3236, 0), 5.0), 7);

        companion object {
            fun fromSpotlightId(id: Int): BeachActivity {
                return entries.find { it.spotlightId == id } ?: SANDCASTLE
            }
        }
    }

    override fun onEvent(event: Event) {
        super.onEvent(event)
        if (event is Chat && debug) {
            println(event.message)
        }
    }

    // Helper function to get current beach temperature
    fun getBeachTemperature(): Int {
        return varps.getVarBit(BEACH_TEMP_VARBIT)
    }

    // Helper function to check if happy hour is active
    fun isHappyHour(): Boolean {
        return varps.getVarBit(HAPPY_HOUR_VARBIT) == 1
    }

    // Helper function to get current spotlight activity
    fun getSpotlightActivity(): BeachActivity {
        val spotlightId = varps.getVarBit(SPOTLIGHT_ACTIVITY_VARBIT)
        return BeachActivity.fromSpotlightId(spotlightId)
    }
}

class BeachMainState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        // Check if beach temperature is maxed out

        if (interfaces.isOpen(1188)) {
            { continueDialogueContaining("Continue") }
        }

//        if (getBeachTemperature() >= MAX_BEACH_TEMP) {
//            return BeachIdleState()
//        }

        // Check if we need to wait for happy hour
        if (happyHour && !isHappyHour()) {
            return BeachIdleState()
        }


//
//        // Determine which activity to do
//       // val currentActivity = if (spotlight) getSpotlightActivity() else selectedActivity.value
//
//        // Update the selected activity if using spotlight
//        if (spotlight && currentActivity != selectedActivity.value) {
//            selectedActivity.value = currentActivity
//            if (debug) println("Switching to spotlight activity: ${currentActivity.displayName}")
//        }

        // Determine which activity to do
        currentActivity = if (spotlight) getSpotlightActivity() else selectedActivity.value

        if (debug && spotlight) {
            println("Using spotlight activity: ${currentActivity.displayName}")
        }

        return when (currentActivity) {
            BeachEvent.BeachActivity.COCONUT_SHY -> CoconutShyState()
            BeachEvent.BeachActivity.SANDCASTLE -> SandcastleState()
            BeachEvent.BeachActivity.BARBEQUES -> BarbequeState()
            BeachEvent.BeachActivity.ROCK_POOLS -> RockPoolState()
            BeachEvent.BeachActivity.PALM_TREE -> PalmTreeState()
            BeachEvent.BeachActivity.BODYBUILDING -> BodybuildingState()
            BeachEvent.BeachActivity.HOOK_A_DUCK -> HookADuckState()
            BeachEvent.BeachActivity.DUNGEONEERING -> DungeoneeringState()
        }
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Main controller")
            println("Current beach temperature: ${getBeachTemperature()}/${MAX_BEACH_TEMP}")
            println("Happy Hour active: ${isHappyHour()}")
            println("Current spotlight: ${getSpotlightActivity().displayName}")
        }

        delay(random(500, 1000))
    }
}

class BeachIdleState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        // Check if we can exit idle state
        if (getBeachTemperature() < MAX_BEACH_TEMP) {
            if (!happyHour || isHappyHour()) {
                return BeachMainState()
            }
        }

        return null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Idle")
            if (getBeachTemperature() >= MAX_BEACH_TEMP) {
                println("Beach temperature maxed out (${getBeachTemperature()}/${MAX_BEACH_TEMP})")
            }
            if (happyHour && !isHappyHour()) {
                println("Waiting for Happy Hour")
            }
        }

        // Just wait in idle state
        delay(random(5000, 10000))
    }
}

// Individual activity states (removing WalkToActivityState and updating other states)
class CoconutShyState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        // Return to main state for reevaluation after a period of time
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Coconut Shy activity")
        }



        // Debugging line to check headbars

        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        // Find the coconut shy object
        val coconutShy = findClosestObject (60) { it.name().contains("Coconut shy", ignoreCase = true) }
        if (coconutShy == null) {
            if (debug) println("No coconut shy found")
            delay(random(1000, 2000))
            return
        }



        // Interact with the coconut shy
        if(localPlayer.headbars.firstOrNull { it.type == 13 } == null)
        {
            coconutShy.interact("Play")
            activityCompletions++
            delay(random(2000, 3000))
            return
        }

    }
}

class SandcastleState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Sandcastle building activity")
        }

       println("Current animation: ${localPlayer.animation}") // Debugging line to check animation


        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        // Find the sandcastle spot
        val sandcastleSpot = findClosestObject (60){ it.name().contains("Sand Pyramid", ignoreCase = true) }
        if (sandcastleSpot == null) {
            if (debug) println("No sandcastle spot found")
            delay(random(1000, 2000))
            return
        }

        // Interact with the sandcastle spot
        if(localPlayer.headbars.firstOrNull { it.type == 13 } == null) {
            sandcastleSpot.interact("Build")
            activityCompletions++
            delay(random(2000, 3000))
        }
    }
}

class BarbequeState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Barbeque activity")
        }

        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        // Find the barbeque
        val barbeque = findClosestObject(60) { it.name().contains("Grill", ignoreCase = true) }
        if (barbeque == null) {
            if (debug) println("No Grill found")
            delay(random(1000, 2000))
            return
        }


        // Interact with the barbeque
        if(localPlayer.headbars.firstOrNull { it.type == 13 } == null)
        {
            if (debug) println("Interacting with Grill")
            barbeque.interact("Use")
            activityCompletions++
            delay(random(2000, 3000))
        } else {
            if (debug) println("Already interacting with Grill")
        }

    }
}

class RockPoolState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }
    var timesinceidle =0

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Rock pools activity")
        }

        // Check if inventory is full and contains fish
        if (inventory.isFull) {
            if (debug) println("Inventory full with fish, finding Wellington to hand in")

            // Find Wellington NPC to hand in fish
            val wellington = findClosestNPC(60) { it.name == "Wellington" }
            if (wellington != null) {
                wellington.interact("Hand in fish")
                delayUntil(5000) { !inventory.isFull }
                delay(random(1000, 2000))
                return
            } else {
                if (debug) println("No Wellington NPC found for fish hand-in")
                delay(random(1000, 2000))
                return
            }
        }

        // Check if player is animating
        if (localPlayer.isAnimating) {
            timesinceidle = 0
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        if(!localPlayer.isAnimating)
        {
            if(timesinceidle == 0)
            {
                timesinceidle = System.currentTimeMillis().toInt()
                return
            }

            if(System.currentTimeMillis().toInt() - timesinceidle < 5000)
            {
                return
            }
        }

        // Find the rock pool
        val rockPool = findClosestNPC (60){ it.name().contains("Fishing spot", ignoreCase = true) }
        if (rockPool == null) {
            if (debug) println("No fishing spot found")
            delay(random(1000, 2000))
            return
        }

        // Interact with the rock pool
        rockPool.interact("Catch")
        activityCompletions++
        delay(random(2000, 3000))
    }
}

class PalmTreeState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        // Return to main state for reevaluation after a period of time
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Palm tree activity")
        }

        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        // Check if inventory is full of coconuts
        if (inventory.isFull) {
            if (debug) println("Inventory full, depositing coconuts")

            // Find pile of coconuts to deposit
            val coconutPile = findClosestObject(60) { it.name().contains("Pile of coconuts", ignoreCase = true) }
            if (coconutPile != null) {
                coconutPile.interact("Deposit coconuts")
                delayUntil(5000) { !inventory.isFull }
                delay(random(1000, 2000))
                return
            } else {
                if (debug) println("No coconut pile found for depositing")
                delay(random(1000, 2000))
                return
            }
        }

        // Find the palm tree to pick coconuts from
        val palmTree = findClosestObject { it.name().contains("Palm tree", ignoreCase = true) }
        if (palmTree == null) {
            if (debug) println("No palm tree found")
            delay(random(1000, 2000))
            return
        }

        // Pick coconuts from the palm tree
        palmTree.interact("Pick coconut")
        activityCompletions++
        delay(random(2000, 3000))
    }
}

class BodybuildingState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    var timesinceidle = 0

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Bodybuilding activity")
        }

        //println(localPlayer.animation)
        val npcanimation = findClosestNPC(20){it.name == "Greta"}?.animationId

        if(localPlayer.animationId != npcanimation && npcanimation != -1)
        {
            when(npcanimation)
            {
                26551 -> println("Idle")
                26549 -> {IFSlot(796, 36, -1).click(1)
                    println("Raise") }//println("Raise")

                26554 ->  {IFSlot(796, 26, -1).click(1)
                    println("Fly")}//println("Fly")
                26553 -> {IFSlot(796, 16, -1).click(1)
                    println("Lundge")}//println("Lundge")
                26552 -> {IFSlot(796, 6, -1).click(1)
                    println("Curl" ) }//println("Curl" )
                else -> println("Unknown animation: $npcanimation")
            }
            delay(random(1000, 2000))
        }


        // Check if player is animating
        if (localPlayer.isAnimating) {
            timesinceidle = 0
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        if(!localPlayer.isAnimating)
        {
            if(timesinceidle == 0)
            {
                timesinceidle = System.currentTimeMillis().toInt()
                return
            }

            if(System.currentTimeMillis().toInt() - timesinceidle < 5000)
            {
                return
            }
        }


        // Find the muscle beach
        val muscleBeach = findClosestObject(60) { it.name().contains("Body building podium", ignoreCase = true) }
        if (muscleBeach == null) {
            if (debug) println("No muscle beach found")
            delay(random(1000, 2000))
            return
        }

//        2025-07-01 09:47:23.189] 26551   - Idle
//        [2025-07-01 09:47:25.073] Beach state: Bodybuilding activity
//        [2025-07-01 09:47:25.073] 26549  - Raise
        // 26554 - Fly
        // 26553 - Lundge
        // 26552 - Curl

//        findClosestNPC(20){it.name == "Greta" }?.let { npc ->
//            if (debug) println("Found Muscle Beach NPC: ${npc.name}")
//            // Interact with the NPC to start bodybuilding
//            println( npc.renderAnim)
//        } ?: run {
//            if (debug) println("No Muscle Beach NPC found")
//        }

        // Interact with the muscle beach
        if( localPlayer.headbars.firstOrNull { it.type == 13 } != null) {
            if (debug) println("Already interacting with Muscle Beach")
            return
        }

        muscleBeach.interact("Workout")
        activityCompletions++
        delay(random(2000, 3000))
    }
}

class HookADuckState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Hook-a-Duck activity")
        }

        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        // Find the hook-a-duck pond
        val duckPond = findClosestObject (60){ it.name().contains("Hook-a-Duck", ignoreCase = true) }
        if (duckPond == null) {
            if (debug) println("No duck pond found")
            delay(random(1000, 2000))
            return
        }

        // Interact with the duck pond
        if( localPlayer.headbars.firstOrNull { it.type == 13 } == null) {
            if (debug) println("Interacting with Hook-a-Duck pond")
            duckPond.interact("Play")
            activityCompletions++
            delay(random(2000, 3000))
        } else {
            if (debug) println("Already interacting with Hook-a-Duck pond")
        }

    }
}

class DungeoneeringState : State<BeachEvent>() {
    override suspend fun BeachEvent.checkNext(): State<BeachEvent>? {
        return if (random(0, 50) == 0) BeachMainState() else null
    }

    override suspend fun BeachEvent.stateLoop() {
        if (debug) {
            println("Beach state: Dungeoneering activity")
        }



        // Check if player is animating
        if (localPlayer.isAnimating) {
            animationDelay = 0
            delay(random(1000, 2000))
            return
        }

        if (useDrinks.value) {
            if (Effect.LEMON_SOUR_BEACH_COCKTAIL.notActive && inventory.clickItem("Lemon sour (beach cocktail)", 1)) {
                delayUntil(2500) { Effect.LEMON_SOUR_BEACH_COCKTAIL.active }
                return
            }
            if (Effect.BEACH_HOLE_COCKTAIL.notActive && inventory.clickItem("A Hole in One (beach cocktail)", 1)) {
                delayUntil(2500) { Effect.BEACH_HOLE_COCKTAIL.active }
                return
            }
        }

        // Find the dungeoneering hole
        val dungeoneeringHole = findClosestObject(60) { it.name().contains("Dungeoneering hole", ignoreCase = true) }
        if (dungeoneeringHole == null) {
            if (debug) println("No dungeoneering hole found")
            delay(random(1000, 2000))
            return
        }

        // Interact with the dungeoneering hole
        if( localPlayer.headbars.firstOrNull { it.type == 13 } == null) {
            if (debug) println("Interacting with Dungeoneering hole")
            dungeoneeringHole.interact("Dungeoneer")
            activityCompletions++
            delay(random(2000, 3000))
        } else {
            if (debug) println("Already interacting with Dungeoneering hole")
        }

    }
}