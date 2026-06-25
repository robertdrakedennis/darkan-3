package com.undercut.script.impl.trent.menaphites

import com.undercut.game.Skill
import com.undercut.game.Tile
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*

private const val TARGET = "Menaphite marketeer"

@ScriptDescription(
    name = "Menaphite marketeers",
    version = "1.0.0",
    author = "Trent",
    description = "Pickpockets menaphite marketeers in Menaphos"
)
class MenaphiteMarketeers : StateMachineScript<MenaphiteMarketeers>() {
    override fun getStartState() = Thieve
}

object Thieve: State<MenaphiteMarketeers>() {
    override suspend fun MenaphiteMarketeers.checkNext() = if (healthPercent < 8.0 || inventory.isFull) Heal else null

    override suspend fun MenaphiteMarketeers.stateLoop() {
        val target = if (getCurrentLevel(Skill.THIEVING) >= 104) "Menaphos market guard" else "Menaphite marketeer"
        if (interactClosestNPC(target, "Pickpocket"))
            delay(712, 969)
        else if (walkTo(Tile.of(3232, 2784, 0).randomize(3), true))
            delay(6273, 1958)
    }
}

object Heal: State<MenaphiteMarketeers>() {
    override suspend fun MenaphiteMarketeers.checkNext() = if (healthPercent > 86.0 && !inventory.isFull) Thieve else null

    override suspend fun MenaphiteMarketeers.stateLoop() {
        if (walkTo(Tile.of(3236, 2762, 0).randomize(3), true))
            waitThenDelayUntil(1200, 20000) { healthPercent > 85.0 }
    }
}