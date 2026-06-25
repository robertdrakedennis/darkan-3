package com.undercut.script.impl.trent.portermine

import com.undercut.game.Skill
import com.undercut.game.chat.MessageType
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.findClosestObjectToTile
import com.undercut.script.api.findClosestReachableObject
import com.undercut.script.api.inventory
import com.undercut.script.api.spotAnims
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat
import com.undercut.script.event.impl.ManualDoAction
import com.undercut.script.impl.trent.EquipPorters

private val rockertunitySpotAnims = intArrayOf(7164, 7165)

@ScriptDescription(
    name = "Porter Mining",
    version = "1.0.0",
    author = "Trent",
    description = "Mines chosen rocks with porters"
)
class PorterMining : StateMachineScript<PorterMining>() {
    lateinit var rockName: String
    fun hasRockName() = ::rockName.isInitialized

    override fun getStartState() = Init

    override fun onStart() {
        addParallelScript(EquipPorters())
    }
}

object Init: State<PorterMining>() {
    override suspend fun PorterMining.checkNext() = if (hasRockName()) Mine() else null

    override suspend fun PorterMining.stateLoop() { }

    override fun PorterMining.onStateEvent(event: Event) {
        if (event !is ManualDoAction || event.target !is SceneObject) return
        val rock = event.target
        if (!hasRockName() && rock.hasOption("Mine"))
            rockName = rock.name()
    }
}

private val oreboxes = """.*ore box$""".toRegex()

class Mine: State<PorterMining>() {
    var currentRock: SceneObject? = null
    var oreboxFull = false

    override suspend fun PorterMining.checkNext() = if (inventory.freeSlots <= 1 && oreboxFull) { stop(); null } else null

    override suspend fun PorterMining.stateLoop() {
        if (inventory.freeSlots <= 1 && inventory.hasItem(oreboxes)) {
            inventory.clickItem(oreboxes, "Fill")
            delayUntil(2500) { inventory.freeSlots > 1 }
            return
        }
        currentRock = findClosestReachableObject(24) { it.name() == rockName && it.hasOption("Mine") }
        val rockertunity = spotAnims.find { rockertunitySpotAnims.contains(it.id) }
        if (rockertunity != null)
            currentRock = findClosestObjectToTile(rockertunity.tile) { it.name() == rockName && it.hasOption("Mine") }
        if (currentRock?.interact("Mine") == true) {
            waitForXPDrop(Skill.MINING)
            delay(5529, 10592)
        }
    }

    override fun PorterMining.onStateEvent(event: Event) {
        if (event is Chat && event.messageType == MessageType.UNFILTERABLE && event.message.contains("You are not able to deposit anything in your backpack into your ore box."))
            oreboxFull = true
    }
}