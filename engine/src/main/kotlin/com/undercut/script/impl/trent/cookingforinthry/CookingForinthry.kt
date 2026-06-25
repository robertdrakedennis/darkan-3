package com.undercut.script.impl.trent.cookingforinthry

import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*

@ScriptDescription(
    name = "Cooking Forinthry",
    version = "1.0.0",
    author = "Trent",
    description = "Cooks using presets in fort forinthry"
)
class CookingForinthry : StateMachineScript<CookingForinthry>() {
    override fun getStartState() = Cook()
}

private val raw = Regex("Raw\\s+.*")

class Cook: State<CookingForinthry>() {
    var making = false

    override suspend fun CookingForinthry.checkNext() = if (making && !hasActiveMakeXProgress) Bank else null

    override suspend fun CookingForinthry.stateLoop() {
        if (hasActiveMakeXProgress) return
        if (!makeXOpen) {
            if (interactClosestObject("Cook-at"))
                delayUntil { makeXOpen }
            return
        }
        continueMakeX()
        delayUntil { interfaces.isOpen(1251) }
        making = true
    }
}

object Bank: State<CookingForinthry>() {
    override suspend fun CookingForinthry.checkNext() = if (inventory.hasItem(raw)) Cook() else null

    override suspend fun CookingForinthry.stateLoop() {
        if (!inventory.hasItem(raw) && interactClosestObject("Load Last Preset from"))
            delayUntil { inventory.hasItem(raw) }
    }
}