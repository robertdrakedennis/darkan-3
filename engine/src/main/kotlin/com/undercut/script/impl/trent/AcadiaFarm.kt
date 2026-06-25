package com.undercut.script.impl.trent

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.interactClosestReachableObject
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer

@ScriptDescription(
    name = "Acadia Farm",
    version = "1.0.0",
    author = "Trent",
    description = "Cuts acadias and deposits the logs"
)
class AcadiaFarm : Script() {
    override suspend fun loop() {
        if (inventory.isFull) {
            if (interactClosestReachableObject("Load Last Preset from"))
                delayUntil(15000) { !inventory.isFull }
            return
        }
        if (localPlayer.isAnimating) return
        if (interactClosestReachableObject("Cut down")) {
            delayUntil(15000) { localPlayer.isAnimating }
            delayUntil(120000) { !localPlayer.isAnimating || inventory.isFull }
        }
    }
}