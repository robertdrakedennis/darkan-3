package com.undercut.script.impl.trent

import com.undercut.script.ConfigurableScript
import com.undercut.script.EnumConfigItem
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.findClosestNPC
import com.undercut.script.api.healthCurrent
import com.undercut.script.api.localPlayer
import com.undercut.script.impl.trent.combatutils.optimizedNecromancyRevo
import com.undercut.script.impl.trent.combatutils.optimizedNecromancyRotation

enum class RotationType(val rotationFunction: suspend Script.() -> Unit) {
    NONE({}),
    NECRO_OPTIMAL(Script::optimizedNecromancyRotation),
    NECRO_REVOLUTION(Script::optimizedNecromancyRevo)
}

@ScriptDescription(
    name = "Combat Rotation",
    version = "1.0.0",
    author = "Trent",
    description = "Performs various combat rotations on the targeted NPC"
)
class CombatRotation : Script(), ConfigurableScript {
    val rotation = EnumConfigItem(
        name = "Rotation type",
        description = "Which rotation the script should do on a target",
        enumValues = RotationType.entries.toTypedArray(),
        initialValue = RotationType.NONE
    )

    override suspend fun loop() {
        if (!localPlayer.isInteracting || healthCurrent <= 0) return
        findClosestNPC { localPlayer.interactingWith(it) && it.maxHealth > 0 && it.currentHealth > 0 } ?: return
        rotation.value.rotationFunction(this)
    }
}