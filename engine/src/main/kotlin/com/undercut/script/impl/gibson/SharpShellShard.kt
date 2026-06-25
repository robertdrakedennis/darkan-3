package com.undercut.script.impl.gibson

import com.undercut.game.Skill
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.*
import com.undercut.util.random

@ScriptDescription(
        name = "Sharp Shell Shard",
        version = "1.0.0",
        author = "Gibson",
        description = "Ignites Sharp shell shard for Firemaking"
)
class SharpShellShard : Script() {
    override suspend fun loop() {
        if (hasActiveMakeXProgress) return

        if (timeSinceLastXpDrop > random(1802, 2359)) {
            if (!makeXOpen && inventory.hasItem("Sharp shell shard")) {
                inventory.clickItem("Sharp shell shard", "Ignite")
                delayUntil { makeXOpen }
                return
            }
            continueMakeX()
            waitForXPDrop(Skill.FIREMAKING)
        }
    }
}