package com.undercut.script.impl.trent

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer

@ScriptDescription(
    name = "Cleansing Crystals",
    version = "1.0.0",
    author = "Trent",
    description = "Cleanses cleansing crystals"
)
class CleansingCrystals : Script() {
    override suspend fun loop() {
        if (!localPlayer.headbars.isEmpty()) return
        if (inventory.hasItem("Cleansing crystal")) {
            if (interactClosestObject("Cleanse")) {
                delayUntil(15000) { !localPlayer.headbars.isEmpty() }
                delay(2502, 600)
            }
        } else
            stop()
    }
}