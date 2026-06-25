package com.undercut.script.impl.trent.archaeology

import com.undercut.game.Tile
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.event.impl.ManualDoAction

@ScriptDescription(
    name = "Archaeology",
    version = "1.0.0",
    author = "Trent",
    description = "Super basic archaeology that requires some modification for which spot you want to excavate."
)
class Archaeology : StateMachineScript<Archaeology>() {
    lateinit var depositTile: Tile
    fun hasDepositTile() = ::depositTile.isInitialized
    lateinit var depositOp: String
    var depositNpc = false

    lateinit var excavateName: String
    fun hasExcavateName() = ::excavateName.isInitialized

    lateinit var excavateTile: Tile
    fun hasExcavateTile() = ::excavateTile.isInitialized

    override fun getStartState() = Init
}

private val gravelIds = intArrayOf(49516, 49517, 49521, 49519, 49523, 50696, 49525)
private val materialIds = intArrayOf(
    49444, 49445, 49460, 49514, 49456, 49510, 49464, 49450, 49454, 49462, 49496, 49506, 49512, 49452,
    49498, 49500, 49502, 49504, 49490, 49494, 49486, 49488, 49492, 49508, 49458, 49472, 49474, 50690,
    50694, 49446, 49468, 49476, 49478, 49480, 49482, 50688, 50686, 50692, 49448, 49484, 49466, 49470
)
private val timespriteLocation get() = spotAnims.firstOrNull { it.id == 7307 }?.tile

object Init: State<Archaeology>() {
    override suspend fun Archaeology.checkNext(): State<Archaeology>? {
        return if (hasDepositTile() && hasExcavateName() && hasExcavateTile()) Gather else null
    }

    override suspend fun Archaeology.stateLoop() { }

    override fun Archaeology.onStateEvent(event: Event) {
        if (event !is ManualDoAction) return

        val target = event.target
        if (target !is SceneObject && target !is NPC) return

        val depositOptions = listOf("Deposit materials", "Deposit all", "Load Last Preset from")
        val hasDepositOption = depositOptions.any { option ->
            when (target) {
                is SceneObject -> target.hasOption(option)
                is NPC -> target.hasOption(option)
                else -> false
            }
        }

        if (!hasDepositTile() && hasDepositOption) {
            depositOp = depositOptions.first { option ->
                when (target) {
                    is SceneObject -> {
                        depositTile = target.tile
                        target.hasOption(option)
                    }
                    is NPC -> {
                        depositTile = target.tile
                        target.hasOption(option)
                    }
                    else -> false
                }
            }
            if (target is NPC)
                depositNpc = true
        }

        if (!hasDepositTile() && inventory.any { it.name.contains(" porter") })
            depositTile = Tile.of(0, 0, 0)

        if (target is SceneObject && !hasExcavateName() && target.hasOption("Excavate")) {
            excavateName = target.name()
            excavateTile = target.tile
        }
    }
}

object Gather: State<Archaeology>() {
    var lastTimeSpriteLocation = timespriteLocation

    override suspend fun Archaeology.checkNext(): State<Archaeology>? {
        if (!inventory.isFull) return null
        val materialCount = inventory.count { materialIds.contains(it.id) }
        val gravelCount = inventory.count(*gravelIds)
//        return if (gravelCount.toDouble() / materialCount.toDouble() > 0.15)
//            Drop
//        else
         return   Deposit
    }

    override suspend fun Archaeology.stateLoop() {
        checkPorter()
        if (localPlayer.isAniMoving && lastTimeSpriteLocation == timespriteLocation) return
//        val materialCacheRegex = Regex("^Material cache.*")
//        if (interactClosestReachableObject(materialCacheRegex, "Excavate")) {
        if (interactClosestReachableObjectToTile(timespriteLocation ?: localPlayer.tile, excavateName, "Excavate")) {
            lastTimeSpriteLocation = timespriteLocation
            delay(3500, 5200)
        } else if (excavateTile.withinDistance(localPlayer.tile, 10) || walkTo(excavateTile.randomize(3), true))
            delayUntil(5283) { excavateTile.withinDistance(localPlayer.tile, 11) }
        delay(150, 200)
    }
}

object Drop: State<Archaeology>() {
    override suspend fun Archaeology.checkNext() = if (!inventory.hasItem(*gravelIds)) Gather else null

    override suspend fun Archaeology.stateLoop() {
        if (inventory.clickItem("Archaeological soil box", "Fill"))
            delay(1200, 1000)
        inventory.filter { gravelIds.contains(it.id) }.forEach {
            it.click("Drop")
            delay(110, 100)
        }
        delay(1200, 1100)
    }
}

object Deposit: State<Archaeology>() {
    override suspend fun Archaeology.checkNext() = if (!inventory.hasItem(*materialIds)) Gather else null

    override suspend fun Archaeology.stateLoop() {
        val deposit = if (inventory.hasItem(49976)) {
            if (findClosestObject(range = 15) { it.hasOption("Deposit all") } != null)
                "Deposit all"
            else
                "Deposit materials"
        } else depositOp
        if (if (depositNpc && deposit == depositOp) interactClosestNPC(deposit, 20) else interactClosestReachableObject(deposit, 20))
            waitThenDelayUntil(1200, 60000) { !inventory.hasItem(*materialIds) }
        else if (depositTile.withinDistance(localPlayer.tile, 10) || walkTo(depositTile.randomize(3), true))
            delayUntil(5283) { depositTile.withinDistance(localPlayer.tile, 11) }
        delay(150, 200)
    }
}