package com.undercut.script.impl.qb.skilling

import com.undercut.game.Skill
import world.gregs.voidps.type.Tile
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.ScriptDescription
import com.undercut.script.api.Lodestone
import com.undercut.script.api.interfaces
import com.undercut.script.api.localPlayer
import com.undercut.traversal.Traversal.Companion.traversal
import com.undercut.script.event.Event
import com.undercut.script.event.impl.Chat

@ScriptDescription(
	name = "CroesusSoups",
	version = "1.0.0",
	author = "QB",
	description = "Cooks using presets"
)
class CroesusSoups : StateMachineScript<CroesusSoups>() {
	override fun getStartState(): State<CroesusSoups> = CombineComponents()

	override fun onEvent(event: Event) {
		when (event) {
			is Chat -> {
				println(event.message)
				if (event.message.startsWith("item could not be found:", true)) {
					
				}				
			}
		}
	}
}

private val enrichedComponent = Regex("Enriched\\s+.*")
private val waterBowl = "Bowl of water"

class CombineComponents : State<CroesusSoups>() {
	override suspend fun CroesusSoups.checkNext() =
		if (interactClosestObject("Load Last Preset from") == false) walkToCatherbySoups else
			if (!inventory.hasItem(enrichedComponent) || !inventory.hasItem(waterBowl)) BankingSoups else null

	override suspend fun CroesusSoups.stateLoop() {

//		if (checkWorldPop())
//			return


		if (interfaces.isOpen(1251)) {
			println("Still cooking")
//			delayUntil(5000) { !interfaces.isOpen(1251) }
			delay(1500, 1000)
			return
		}

		var waterBowlItem = inventory.getItem(waterBowl)
		if (waterBowlItem == null) {
			println("No Water Bowl")
			return
		}

		inventory.getItem(enrichedComponent)?.useOn(waterBowlItem)
		delayUntil(5000) { interfaces.isOpen(1251) }
		waitForXPDrop(Skill.COOKING)
		delayUntil(5000) { !interfaces.isOpen(1251) }

	}


}

val walkToCatherbySoups = traversal(BankingSoups, { localPlayer.tile.getDistance(Tile.of(3091, 3488, 0)) < 8 }) {
	chebychevPath(
		localPlayer.tile,
		listOf(
			Tile.of(2810, 3449, 0),
			Tile.of(2797, 3444, 0)
		),
		fallback = { useLodestone(Lodestone.CATHERBY) },
		reached = { localPlayer.tile.getDistance(Tile.of(3091, 3488, 0)) < 8 }
	)
}

object BankingSoups : State<CroesusSoups>() {
	override suspend fun CroesusSoups.checkNext() =
		if (inventory.hasItem(enrichedComponent) && inventory.hasItem(waterBowl)) CombineComponents() else null

	override suspend fun CroesusSoups.stateLoop() {
		if (interactClosestObject("Load Last Preset from"))
			delayUntil(6000) { inventory.hasItem(enrichedComponent) && inventory.hasItem(waterBowl) }

	}
}