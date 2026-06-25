package com.undercut.script.impl.gibson

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.interactClosestNPC
import com.undercut.script.api.interactClosestReachableObject
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer

@ScriptDescription(
    name = "Menaphos Fishing",
    version = "1.0.0",
    author = "Gibson",
    description = "Baits fishing and deposits the fish in Menaphos VIP area"
)
class MenaphosVIPFishing : Script() {
    override suspend fun loop() {
        if (inventory.isFull) {
            if (interactClosestReachableObject("Load Last Preset from"))
                delayUntil(15000) { !inventory.isFull }
            return
        }
        if (localPlayer.isAnimating) return
        val bait = inventory.getItem("Fishing bait")
        if(bait == null) {
            println("No fishing bait found in inventory. Stopping script.")
            stop()
            return
        }
        if (interactClosestNPC("Bait")) {
            delayUntil(15000) { localPlayer.isAnimating }
            delayUntil(120000) { !localPlayer.isAnimating || inventory.isFull }
        }
    }
}