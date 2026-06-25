package com.undercut.script.impl.trent

import com.undercut.game.interfaces.IFSlot
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.hasActiveMakeXProgress
import com.undercut.script.api.interfaces
import com.undercut.script.api.inventory

@ScriptDescription(
    name = "Disassemble Inventory",
    version = "1.0.0",
    author = "Trent",
    description = "Disassembles your entire inventory."
)
class DisassembleInventory : Script() {
    private val confirm = IFSlot(847, 22)
    override suspend fun loop() {
        if (hasActiveMakeXProgress) return
        if (inventory.firstOrNull()?.disassemble() == true) {
            delayUntil(3000) { interfaces.isOpen(847) }
            if (interfaces.isOpen(847) && confirm.dialogueContinue())
                delayUntil(3000) { hasActiveMakeXProgress }
            else
                delayUntil(3000) { hasActiveMakeXProgress }
        }
    }
}