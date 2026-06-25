package com.undercut.script.impl.trent

import com.undercut.game.chat.MessageType
import com.undercut.script.Script
import com.undercut.script.ScriptDescription
import com.undercut.script.api.interactClosestObject
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat

@ScriptDescription(
    name = "AFK Smithing",
    version = "1.0.0",
    author = "Trent",
    description = "Reheats existing smithing items and smiths them"
)
class AFKSmithing : Script() {
    var needsReheat = false

    override suspend fun loop() {
        if (needsReheat) {
            if (interactClosestObject("Heat"))
                delayUntil { !needsReheat }
            return
        }
        if (interactClosestObject("Smith"))
            delayUntil { needsReheat }
    }

    val needsReheatMessages = setOf("has run out of heat", "cooled down slightly", "cooled down significantly")

    override fun onEvent(event: Event) {
        if (event !is Chat || event.messageType != MessageType.UNFILTERABLE) return
        if (event.message.contains("is at full heat"))
            needsReheat = false
        if (needsReheatMessages.any { event.message.contains(it) })
            needsReheat = true
    }
}