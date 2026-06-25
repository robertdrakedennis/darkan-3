package com.undercut.script.impl.trent

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.findNPC
import com.undercut.util.gaussian

@ScriptDescription(
    name = "Spirit Attraction Potion",
    version = "1.0.0",
    author = "Trent",
    description = "Acts as a spirit attraction potion to claim fire spirits, forge phoenixes, seren spirits, etc"
)
class SpiritAttractionPotion : Script() {
    var startTime = 0L

    override fun onStart() {
        startTime = System.currentTimeMillis()
    }

    val attractionNpcs = setOf(
        "Seren spirit",
        "Forge phoenix",
        "Divine blessing",
        "Elder chronicle",
        "Fire spirit",
        "Divine fire spirit",
        "Divine forge phoenix",
        "Manifested knowledge",
        "Divine carpet dust",
        "Catalyst of alteration",
    )

    override suspend fun loop() {
        val npc = findNPC { attractionNpcs.contains(it.name) } ?: return delay(1500, 2500)
        if (npc.interact(0))
            delay(gaussian(3000, 2500))
    }
}