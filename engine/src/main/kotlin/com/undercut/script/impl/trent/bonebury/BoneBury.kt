package com.undercut.script.impl.trent.bonebury

import com.undercut.game.chat.MessageType
import com.undercut.game.interfaces.effects.Effect
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.api.keyDown
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat

@ScriptDescription(
    name = "Bone Bury",
    version = "1.0.0",
    author = "Trent",
    description = "Buries bones using 1 on the actionbar."
)
class BoneBury : StateMachineScript<BoneBury>() {
    override fun getStartState() = Bury()

    override fun onStart() {
        keyDown('1')
    }
}

class Bury: State<BoneBury>() {
    override suspend fun BoneBury.checkNext() = if (!inventory.hasItem(""".*bones$""".toRegex())) Bank() else null

    override suspend fun BoneBury.stateLoop() {
        if (Effect.POWDER_OF_BURIALS.notActive && inventory.clickItem("Powder of burials", "Scatter"))
            delayUntil(2000L) { Effect.POWDER_OF_BURIALS.active }
    }
}

class Bank: State<BoneBury>() {
    var withdrawn = false

    override suspend fun BoneBury.checkNext() = if (withdrawn) Bury() else null

    override suspend fun BoneBury.stateLoop() {
        interactClosestObject("Load Last Preset from")
        delay(1520, 852)
    }

    override fun BoneBury.onStateEvent(event: Event) {
        if (event !is Chat) return
        if (event.messageType == MessageType.FILTERABLE && event.message.contains("Your preset is being withdrawn"))
            withdrawn = true
        if (event.messageType == MessageType.FILTERABLE && event.message.contains("Item could not be found"))
            stop()
    }
}