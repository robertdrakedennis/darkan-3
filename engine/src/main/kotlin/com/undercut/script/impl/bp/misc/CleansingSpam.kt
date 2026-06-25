package com.undercut.script.impl.bp.misc

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.nxt.MainState
import com.undercut.script.ScriptCategory
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.continueDialogueContaining
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer

@ScriptDescription(
    name = "Cleansing Crystal Spam",
    version = "1.0.0",
    author = "BP",
    description = "Spams cleansing crystals on corrupted seren stone",
    category = ScriptCategory.PRAYER
)
class CleansingCrystal : StateMachineScript<CleansingCrystal>() {
    override fun getStartState(): State<CleansingCrystal> = Cleanse
}

object Cleanse: State<CleansingCrystal>() {

    var lastTick = Bootstrap.client.clientCycle / 30.0

    override suspend fun CleansingCrystal.checkNext() = null
    override suspend fun CleansingCrystal.stateLoop() {
        if (!inventory.hasItem("Cleansing crystal")) return delay(1000)


        if (Bootstrap.client.clientCycle / 30.0 - lastTick > 1) {
            delay(300,300)
            if (interactClosestObject("Cleanse"))
                lastTick = Bootstrap.client.clientCycle / 30.0
        }
    }
}


