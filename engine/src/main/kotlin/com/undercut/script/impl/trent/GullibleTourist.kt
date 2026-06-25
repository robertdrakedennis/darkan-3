package com.undercut.script.impl.trent

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.healthCurrent
import com.undercut.script.api.interactClosestNPC
import com.undercut.script.api.inventory

@ScriptDescription(
    name = "Gullible Tourist",
    version = "1.0.0",
    author = "Trent",
    description = "Pickpockets master farmer in Draynor"
)
class GullibleTourist : Script() {
    override suspend fun loop() {
        if (healthCurrent <= 60 || inventory.isFull)
            return
        if (interactClosestNPC(24432, "Pickpocket"))
            delay(422, 859)
    }
}