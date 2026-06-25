package com.undercut.script.impl.trent

import com.undercut.game.Skill
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.interactClosestReachableObject
import com.undercut.script.api.inventory
import com.undercut.util.gaussian

@ScriptDescription(
    name = "Sandstone Farm",
    version = "1.0.0",
    author = "Trent",
    description = "Mines sandstone and deposits the the in MENAPHOS STROKE LMAO"
)
class SandstoneFarm : Script() {
    override suspend fun loop() {
        if (inventory.isFull && interactClosestReachableObject("Load Last Preset from"))
            delayUntil(15000) { !inventory.isFull }
        else if (interactClosestReachableObject("Mine")) {
            waitForXPDrop(Skill.MINING)
            delayUntil(gaussian(5529L, 10592L), 300) { inventory.isFull }
        }
    }
}