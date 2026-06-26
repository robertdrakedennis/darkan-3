package com.undercut.script.impl.trent.gatesofeledenis
import com.undercut.game.tileOfLocal

import world.gregs.voidps.map.ObjectShape
import world.gregs.voidps.type.Tile
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.interfaces.InstanceSystem
import com.undercut.game.interfaces.effects.Effect
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.entity.npc.NPC
import world.gregs.voidps.collision.CollisionStrategies
import com.undercut.pathfinder.routedDestination
import com.undercut.script.BooleanConfigItem
import com.undercut.script.ScriptDescription
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.*
import com.undercut.script.event.Event
import com.undercut.script.impl.trent.gatesofeledenis.ProcessFight.currentTarget
import com.undercut.util.random

private val DUNK_VAR_TO_PILLAR_OBJECT = mapOf(
    0 to 130994,
    1 to 130996,
    2 to 130998,
    3 to 131000
)
private val GATE_NPC_ID = 17645
private val MOONSTONE_FRAGMENTS = 57516
private val YIKES_PROJECTILE = 8361
private val AOE_3x3_SPOTANIM = 8279
private val AOE_7x7_SPOTANIM = 8285

/**
 * TODO
 * Prioritize the dangerous zone nodes when the Begone attack is nowhere close to being used
 */

@ScriptDescription(
    name = "Gates of Elidinis",
    version = "1.0.0",
    author = "Trent",
    description = "Closes the gates of eledenis"
)
class GatesOfElidinis : StateMachineScript<GatesOfElidinis>() {
    val farmCraftingXp = BooleanConfigItem(
        name = "Farm Crafting XP",
        description = "Farms moonstone fragments for crafting XP instead of killing boss.",
        initialValue = false
    )

    val targetFragments get() = if (farmCraftingXp.value) 50000 else 45

    private var _cachedBoss: NPC? = null
    private val bossNpc: NPC?
        get() =
            _cachedBoss?.takeIf { it.exists() } ?: npcs.values.firstOrNull { it.id == 17645 }
                .also { _cachedBoss = it }

    val chargingLaser
        get() = bossNpc?.headbars?.any {
            it.type == 45 && it.durationMillis == 24000L && it.timeLeftMillis >= -1200L
        } == true

    val pillarsUp
        get() = bossNpc?.headbars?.any {
            it.type == 45 && it.durationMillis == 24000L && it.timeLeftMillis >= -1200L && it.timeLeftMillis < ((24000L - 7200L) + pillarTimeOffset)
        } == true

    val pillarTimeOffset: Long
        get() {
            val dist = tileOfLocal(31, 28, 2).getDistance(localPlayer.tile)
            return 7200L + when {
                dist <= 5 -> 0L
                dist <= 8 -> 2000L
                dist <= 12 -> 4000L
                else -> 10000L
            }
        }

    var instanceStartTime = System.currentTimeMillis()
    val instanceTimeRemaining get() = (55L * 60L * 1000L) - (System.currentTimeMillis() - instanceStartTime)

    override fun getStartState() = StartInstance
}

object StartInstance : State<GatesOfElidinis>() {
    override suspend fun GatesOfElidinis.checkNext() = if (inInstancedArea) {
        instanceStartTime = System.currentTimeMillis()
        StartFight()
    } else null

    override suspend fun GatesOfElidinis.stateLoop() {
        if (!InstanceSystem.isOpen() && interactClosestObject(130974, "Enter"))
            return delayUntil(5000) { InstanceSystem.isOpen() }
        else {
            InstanceSystem.startInstance()
            delay(1200, 3000)
        }
    }

}

class StartFight() : State<GatesOfElidinis>() {
    override suspend fun GatesOfElidinis.checkNext() =
        if (fightActive)
            ProcessFight
        else if (!inInstancedArea)
            StartInstance
        else null

    override suspend fun GatesOfElidinis.stateLoop() {
        if (instanceTimeRemaining <= 0) {
            if (continueDialogueContaining("Yes.")) {
                delayUntil(5000) { !inInstancedArea }
                return
            }
            if (interactClosestNPC("Icthlarin", "Leave"))
                delayUntil(5000) { dialogueOptionVisible("Yes.") }
            return
        }
        if (localPlayer.tile != tileOfLocal(39, 8, 2)) {
            walkTo(tileOfLocal(39, 8, 2), false)
            waitThenDelayUntil(1200, 2500) { !localPlayer.isAniMoving }
            return
        }

        if (healthCurrent < healthMax && adrenaline >= 90 && !inCombat) {
            IFSlot(1430, 11, -1).click(1)
            delay(500, 400)
        }

        if (fightOnCooldown)
            return delay(500, 400)
        if (interactClosestNPC("Icthlarin", "Start"))
            waitThenDelayUntil(1000, 3200) { fightActive }
    }

    override fun GatesOfElidinis.onStateEvent(event: Event) {

    }
}

object GatherFragments : State<GatesOfElidinis>() {
    override suspend fun GatesOfElidinis.checkNext() =
        if (!fightActive || !inArena || healthCurrent <= 0)
            StartFight()
        else if (numFragments >= targetFragments)
            ProcessFight
        else null

    override suspend fun GatesOfElidinis.stateLoop() {
        val moonstone = findClosestObjectToTile(
            if (attacksBeforeSpec <= 0 || attacksBeforeSpec >= 10) tileOfLocal(
                38,
                9,
                2
            ) else localPlayer.tile
        ) {
            it.name() == "Moonstone"
        }
        if (moonstone?.interact("Gather") == true)
            waitThenDelayUntil(3000, 30000) { numFragments >= targetFragments }
        delay(100)
    }
}

object ProcessFight : State<GatesOfElidinis>() {
    internal var currentTarget: SceneObject? = null

    override suspend fun GatesOfElidinis.checkNext() =
        if (!fightActive || !inArena)
            StartFight()
        else if (pillarsUp)
            DunkAndHeal()
        else if (numFragments < 10)
            GatherFragments
        else null

    override suspend fun GatesOfElidinis.stateLoop() {
        if (!Effect.ENHANCED_EXCALIBUR.active && inventory.getItem("Augmented enhanced Excalibur") != null && healthMax - healthCurrent > 1500) {
            val item = inventory.getItem("Augmented enhanced Excalibur")
            item?.click("Activate")
        }

        dodgeAoes(currentTarget)
        if (shouldDunk()) {
            IFSlot(743, 1, -1).click(1)
            delayUntil(2000) { chargingLaser }
        }
        if (barrierPercent <= 10) return repairBarrier()
        if (killAkhs()) return
        val wasInDanger = inCriticalDanger
        if (findAndGatherNode("Cleansed shard of Elidinis", "Mine", wasInDanger))
            return
        if (findAndGatherNode("Corrupt shard of Elidinis", "Transmute", wasInDanger))
            return
        delay(210, 502)
    }
}

private suspend fun GatesOfElidinis.findAndGatherNode(
    nodeName: String,
    option: String,
    wasInDanger: Boolean,
    tile: Tile? = null
): Boolean {
    var tempDanger = wasInDanger
    if (getAllObjectsWithinRange(50).count { (it.name() == "Cleansed shard of Elidinis" || it.name() == "Corrupt shard of Elidinis") && it.tile.yInRegion >= 26 } < 4)
        tempDanger = true

    findClosestNode(nodeName, tile ?: localPlayer.tile, safeZone = if (tempDanger) true else null)?.let {
        gatherNode(option, it, wasInDanger)
        return true
    }
    return false
}

private suspend fun GatesOfElidinis.gatherNode(option: String, node: SceneObject, wasInDanger: Boolean) {
    currentTarget = node
    val targetTile = routedDestination(localPlayer.tile, node, collision = CollisionStrategies.NOCLIP)
    if (targetTile?.let { inAoe(it) } == true) return delay(100)
    node.interact(option)
    delayUntil(10000) {
        !fightActive || inAoe || pillarsUp ||
                (!wasInDanger && inCriticalDanger) ||
                (wasInDanger && !inCriticalDanger) ||
                !node.exists || findAkh() != null || shouldDunk()
    }
}

private fun findAkh() = findClosestNPC { it.name == "Feline akh" && it.currentHealth > 0 }

private suspend fun GatesOfElidinis.killAkhs(): Boolean {
    val akh = findAkh()
    if (akh != null) {
        while (akh.exists() && akh.currentHealth > 0) {
            akh.interact("Dismiss")
            delay(212, 100)
        }
        return true
    }
    return false
}


private suspend fun GatesOfElidinis.repairBarrier() {
    findClosestNPC("Moonstone conduit", 30)?.let { conduit ->
        if (conduit.tile.getDistance(localPlayer.tile) > 15) {
            if (dive(localPlayer.tile.transform(0, random(9, 11)))) {
                delay(125, 100)
                surge()
                waitThenDelayUntil(800, 2000) { findClosestNPC("Moonstone conduit") != null }
            }
            return
        }
        if (conduit.interact("Repair")) {
            waitThenDelayUntil(1200, 30000) { barrierPercent >= 90 }
        }
        return
    }
}

class DunkAndHeal() : State<GatesOfElidinis>() {
    var dunked = false
    var madeIt = false
    var tanked = false

    override suspend fun GatesOfElidinis.checkNext() =
        if (!fightActive || !inArena)
            StartFight()
        else if (dunked && tanked)
            ProcessFight
        else null

    override suspend fun GatesOfElidinis.stateLoop() {
        killAkhs()
        if (!dunked) {
            val nearPillars = tileOfLocal(31, 25, 2)
            if (findClosestObject(130994, range = 8) == null && nearPillars.getDistance(localPlayer.tile) > 3) {
                if (walkTo(nearPillars.randomize(1), false))
                    waitThenDelayUntil(1500, pollingDelayMillis = 600) { !localPlayer.isAniMoving || findClosestObject(130994, range = 8) != null }
                else
                    delay(101)
                return
            }
            if (findClosestObject(130994) != null)
                dunkIt()
            else
                delay(100)
            return
        }
        if (!tanked && !localPlayer.isAnimating) {
            val safeTile = tileOfLocal(31, 14, 2)
            if (localPlayer.tile != safeTile && !madeIt) {
                if (dive(localPlayer.tile.transform(0, -random(10, 13)))) {
                    delay(125, 100)
                    surge()
                    delay(1000)
                    surge()
                    delay(138, 100)
                }
                if (walkTo(safeTile, false)) {
                    while (!localPlayer.isMoving) {
                        delay(220, 55)
                        walkTo(safeTile, false)
                    }
                    waitThenDelayUntil(1000, 10000) { !localPlayer.isAniMoving }
                } else
                    delay(100)
            } else
                madeIt = true
            if (madeIt) {
                val node = findClosestNode("Corrupt shard of Elidinis", tileOfLocal(33, 13, 2)) ?: return
                if (node.tile == tileOfLocal(33, 13, 2)) {
                    node.interact("Transmute")
                    delayUntil(10000) { !chargingLaser }
                }
            }
            if (!chargingLaser)
                tanked = true
        }
    }

    private suspend fun GatesOfElidinis.dunkIt(): Boolean {
        val startTime = System.currentTimeMillis()
        while (dunkStage <= 3) {
            if (System.currentTimeMillis() - startTime > 30000) break
            val prev = dunkStage
            if (interactClosestObject(DUNK_VAR_TO_PILLAR_OBJECT[dunkStage] ?: return false, "Jump"))
                delayUntil(4000) { prev != dunkStage }
            else
                delay(100)
        }
        delayUntil(2000) { dunkStage == 4 }
        if (dunkStage == 4 && interactClosestNPC(GATE_NPC_ID, "Cleanse")) {
            delayUntil(10000) { dunkStage == 5 }
            dunked = true
        }
        return true
    }
}

/**
 * NPC 17677
 * headbar 48 = divination transmute progress
 * headbar 49 = mining progress
 */
private val divTransmuteProgress //can filter on npc 17677 as well to get more refined results
    get() = npcs.values.firstNotNullOfOrNull { it.headbars.firstOrNull { it -> it.type == 48 }?.fromFill } ?: 0
private val miningProgress
    get() = npcs.values.firstNotNullOfOrNull { it.headbars.firstOrNull { it -> it.type == 49 }?.fromFill } ?: 0

data class NodeProgress(val tile: Tile, val progress: Int)

private val nodeProgresses = mutableListOf<NodeProgress>()
private val fightActive
    get() = npcs.values.any { it.id == 17693 && it.hiddenMenuOpFlags and 1 != 0 }
private val fightOnCooldown
    get() = npcs.values.any {
        it.id == 17693 && it.headbars.any {
            it.type == 45 && it.durationMillis == 60000L && it.timeLeftMillis >= 0
        } == true
    }
private val barrierHealth
    get() = varps.getVar(10947)
private val barrierPercent
    get() = (barrierHealth.toDouble() / 20000.toDouble()) * 100.0
private val attackNum
    get() = varps.getVar(11843)
private val attacksBeforeSpec
    get() = varps.getVar(11844)
private val dunkPower
    get() = varps.getVar(11837)
private val dunkStage
    get() = varps.getVar(11838)
private val inArena
    get() = localPlayer.localTile.x >= 11 && localPlayer.localTile.x <= 51 && localPlayer.localTile.y >= 6

private fun inDangerous(tile: Tile) = tile.yInRegion >= 26
private val inAoe
    get() = inAoe(localPlayer.tile)

private fun inAoe(tile: Tile) = !localPlayer.isMoving && spotAnims.any {
    (it.id == AOE_3x3_SPOTANIM && it.timeAliveMillis > 2100 && tile.withinDistance(it.tile, 1)) ||
    (it.id == AOE_7x7_SPOTANIM && it.timeAliveMillis > 1500 && tile.withinDistance(it.tile, 4))
}

private fun findClosestNode(name: String, tile: Tile, safeZone: Boolean? = null) = findClosestObjectToTile(tile) {
    it.name() == name && when (safeZone) {
        true -> !inDangerous(it.tile)
        false -> inDangerous(it.tile)
        null -> true
    }
}

private val inCriticalDanger
    get() = projectiles.any {
        it.id == YIKES_PROJECTILE && it.lockedOnto(localPlayer) && it.tile.withinDistance(localPlayer.tile, 15)
    }

private val numFragments
    get() = inventory.count(MOONSTONE_FRAGMENTS)

private fun GatesOfElidinis.shouldDunk() =
    (healthPercent <= 35 || (bossHealthCurrent + 3125) < (inventory.count(57517) * 3125) || inventory.freeSlots < 1) && !chargingLaser

private suspend fun GatesOfElidinis.dodgeAoes(currentTarget: SceneObject? = null) {
    if (inAoe) {
        val safeTile = findClosestSafeTile(if (currentTarget != null) TileArea(currentTarget.tile, currentTarget.defs.sizeX, currentTarget.defs.sizeY) else null) ?: return
        if (safeTile.getDistance(localPlayer.tile) > 2 && !dive(safeTile)) {
            delay(300, 200)
            escape()
            delayUntil(1200) { !inAoe }
            return
        }
        if (walkTo(safeTile, false))
            delay(452, 100)
    }
}

private fun findClosestSafeTile(target: TileArea? = null): Tile? {
    if (!inAoe) return null
    val playerPos = localPlayer.tile
    val dangerZones = spotAnims
        .filter { it.id == AOE_3x3_SPOTANIM || it.id == AOE_7x7_SPOTANIM }
        .map { spotAnim ->
            val radius = if (spotAnim.id == AOE_3x3_SPOTANIM) 1 else 4
            DangerZone(spotAnim.tile, radius)
        }

    val sceneryDiveBlockers = getAllObjectsWithinRange(10)
        .filter { obj -> obj.shape == ObjectShape.SCENERY_INTERACT }
        .map { obj -> TileArea(tile = obj.tile, sizeX = obj.defs.sizeX, sizeY = obj.defs.sizeY) }

    val conduitDiveBlockers = npcs.values
        .filter { it.name() == "Moonstone conduit" }
        .map { npc -> TileArea(tile = npc.tile, sizeX = 3, sizeY = 3) }

    val obstacles = sceneryDiveBlockers + conduitDiveBlockers

    return calculateClosestSafeTile(playerPos, dangerZones, target ?: TileArea(playerPos, 1, 1), obstacles)
}
