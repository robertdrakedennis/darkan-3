package com.undercut.script.impl.trent.gemminer

import com.undercut.game.Skill
import com.undercut.game.chat.MessageType
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.scopes.image
import com.undercut.ui.backend.dsl.scopes.text
import com.undercut.ui.backend.dsl.scopes.xpProgressBar
import com.undercut.ui.backend.native.SpriteIds
import com.undercut.ui.backend.native.spriteTexture
import com.undercut.util.formatElapsedTime
import com.undercut.util.getFormattedXpPerHour
import com.undercut.util.getUnitsPerHour

@ScriptDescription(
    name = "Gem Miner",
    version = "1.0.0",
    author = "Trent",
    description = "Mines gems in Al Kharid"
)
class GemMiner : StateMachineScript<GemMiner>() {
    var startTime = 0L
    var startingXp = 0

    override fun onStart() {
        startTime = System.currentTimeMillis()
        startingXp = getXp(Skill.MINING)
    }

    override fun getStartState() = Mine

    override fun render() {
        ImGuiDsl.window("Gem Miner") {
            image(spriteTexture(SpriteIds.MINING), 32f, 32f)
            text("Runtime: ${formatElapsedTime(System.currentTimeMillis(), startTime)}")
            text("XP/hr: ${getFormattedXpPerHour(startingXp, getXp(Skill.MINING), startTime)}")
            xpProgressBar(Skill.MINING)
        }
    }
}

private var bagFull = false

object Mine: State<GemMiner>() {
    override suspend fun GemMiner.checkNext(): State<GemMiner>? {
        if (!inventory.isFull) return null
        return if (!bagFull) Fill else Deposit
    }

    override suspend fun GemMiner.stateLoop() {
        if (localPlayer.isMoving) return

        if (!interactClosestReachableObject(113047, "Mine")) {
            if (inventory.clickItem("Mystical sand seed", "Plant"))
                waitThenDelayUntil(1200) { findClosestReachableObject(113047) != null }
            return
        }
        waitForXPDrop(Skill.MINING, timeoutMillis = 25000)
        delay(11582, 10592)
    }
}

object Fill: State<GemMiner>() {
    override suspend fun GemMiner.checkNext() = if (!inventory.isFull || bagFull) Mine else null

    override suspend fun GemMiner.stateLoop() {
        if (inventory.clickItem("Gem bag (upgraded)", "Fill"))
            delay(1200, 1000)
    }

    override fun GemMiner.onStateEvent(event: Event) {
        if (event is Chat && event.messageType == MessageType.UNFILTERABLE && event.message.contains("You can't store anymore of the following gem"))
            bagFull = true
    }
}

object Deposit: State<GemMiner>() {
    override suspend fun GemMiner.checkNext() = if (!inventory.isFull) Mine else null

    override suspend fun GemMiner.stateLoop() {
        val bankChest = findClosestReachableObject("Bank chest")
        if (bankChest == null) {
            //click wars tele
            interactComponent(1, 1887, 1, 205)
            waitThenDelayUntil(1200) { findClosestReachableObject("Bank chest") != null }
            return
        }
        if (inventory.find { it.id != -1 && it.name == "Gem bag (upgraded)" }?.useOn(bankChest) == true) {
            waitForChatContaining(MessageType.FILTERABLE, "Emptied")
            bagFull = false
            bankChest.interact("Load Last Preset from")
            delayUntil { !inventory.isFull }
        }
    }
}