package com.undercut.script.impl.trent.smelting

import com.undercut.game.interfaces.IFSlot
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.hasActiveMakeXProgress
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.interfaces
import com.undercut.script.api.inventory

@ScriptDescription(
    name = "Smelting",
    version = "1.0.0",
    author = "Trent",
    description = "Smelts lamaoomao"
)
class Smelting : StateMachineScript<Smelting>() {
    override fun getStartState() = Smelt()
}

class Smelt: State<Smelting>() {
    var smelting = false

    override suspend fun Smelting.checkNext() = if (inventory.isFull || (smelting && !hasActiveMakeXProgress)) Deposit else null

    override suspend fun Smelting.stateLoop() {
        if (hasActiveMakeXProgress) return
        if (!interfaces.isOpen(37)) {
            interactClosestObject("Smelt")
            delayUntil(4500) { interfaces.isOpen(37) }
            return
        }
        IFSlot(37, 163, -1).click(1)
        delayUntil { hasActiveMakeXProgress }
        smelting = true
    }
}

object Deposit: State<Smelting>() {
    override suspend fun Smelting.checkNext() = if (!inventory.hasItem(Regex(".*\\s+bar$"))) Smelt() else null

    override suspend fun Smelting.stateLoop() {
        if (inventory.hasItem(Regex(".*\\s+bar$")))
            interactClosestObject("Deposit-all (into metal bank)")
        delay(1520, 852)
    }
}