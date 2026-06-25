package com.undercut.script.impl.devin

import com.undercut.game.Skill
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.util.gaussian
import java.lang.System.currentTimeMillis
import kotlin.random.Random

@ScriptDescription(
    name = "Gem Power Mining",
    version = "1.0.0",
    author = "Devin",
    description = "Mine, cut & drop gems."
)
class GemRocks : StateMachineScript<GemRocks>() {
    override fun getStartState() = GatherGems
}

private val rock = "Uncommon gem rock"
private val uncuts = Regex("""Uncut\s+\w+""")
private val gems = listOf("Sapphire", "Emerald", "Ruby")

object GatherGems: State<GemRocks>() {
    private var randomDelay = Random.nextLong(2400, 20000)
    private var lastMineTime = currentTimeMillis() - randomDelay

    override suspend fun GemRocks.checkNext(): State<GemRocks>? = if (inventory.isFull) Craft else null

    override suspend fun GemRocks.stateLoop() {
        if (!localPlayer.isAniMoving || (currentTimeMillis() - lastMineTime >= randomDelay)) {
            randomDelay = Random.nextLong(2400, 20000)
            lastMineTime = currentTimeMillis()
            interactClosestReachableObject(rock, "Mine")
        }
        delay(3500, 5200)
    }
}

object Craft: State<GemRocks>() {
    override suspend fun GemRocks.checkNext() = if (!inventory.hasItem(uncuts)) Drop else null

    override suspend fun GemRocks.stateLoop() {
        if (timeSinceLastXpDrop > gaussian(1200, 2059)) {
            if (!makeXOpen) {
                if (inventory.clickItems("Uncut", "Craft"))
                    delayUntil { makeXOpen || timeSinceLastXpDrop < 1200 }
                return
            }
            if (makeXOpen) {
                continueMakeX()
                waitForXPDrop(Skill.CRAFTING)
            }
        }
    }
}

object Drop: State<GemRocks>() {
    override suspend fun GemRocks.checkNext(): State<GemRocks>? = if (inventory.filter { it.id != -1 && it.name.containsAny(gems, true) == true }.isEmpty()) GatherGems else null

    override suspend fun GemRocks.stateLoop() {
        inventory.filter { it.id != -1 && it.name.containsAny(gems, true) == true }.forEach {
            it.click("Drop")
            delay(110, 100)
        }
    }

    fun String.containsAny(keywords: List<String>, ignoreCase: Boolean = true): Boolean {
        return keywords.any { this.contains(it, ignoreCase) }
    }
}