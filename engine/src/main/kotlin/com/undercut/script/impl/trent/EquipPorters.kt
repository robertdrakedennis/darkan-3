package com.undercut.script.impl.trent

import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.checkPorter

@ScriptDescription(
    name = "Equip Porters",
    version = "1.0.0",
    author = "Trent",
    description = "Equips porters if they run out."
)
class EquipPorters : Script() {
    override suspend fun loop() {
        checkPorter()
    }
}