package com.undercut.script.impl.qb.skilling

import com.undercut.game.Skill
import world.gregs.voidps.type.Tile
import com.undercut.script.BooleanConfigItem
import com.undercut.script.ConfigurableScript
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.continueMakeX
import com.undercut.script.api.interactClosestObject
import com.undercut.script.api.inventory
import com.undercut.script.api.makeXOpen
import com.undercut.script.api.timeSinceLastXpDrop
import com.undercut.util.gaussian
import com.undercut.script.ScriptDescription
import com.undercut.script.api.Lodestone
import com.undercut.script.api.checkWorldPop
import com.undercut.script.api.findClosestObject
import com.undercut.script.api.interfaces
import com.undercut.script.api.localPlayer
import com.undercut.traversal.Traversal.Companion.traversal
import com.undercut.script.scheduler.SchedulableScript
import com.undercut.script.scheduler.SchedulerConfigurable
import com.google.gson.JsonObject
import com.undercut.script.ScriptConfigJson
import com.undercut.script.ConfigItem

@ScriptDescription(
	name = "CookingStateMachine", version = "1.0.0", author = "QB", description = "Cooks using last preset"
)
class CookingStateMachine : StateMachineScript<CookingStateMachine>(), ConfigurableScript, SchedulableScript, SchedulerConfigurable {

	val worldHop = BooleanConfigItem(
		name = "World Hop", description = "Enable world hop", initialValue = false
	)


	override fun getStartState(): State<CookingStateMachine> = Cook()


	/**
	 * Apply and validate a per-entry schedule configuration. Implementations
	 * should not perform any long-running work here and must be side-effect
	 * safe beyond updating in-memory configuration on the script instance.
	 */
	override fun applyScheduleConfiguration(config: JsonObject): SchedulerConfigurable.Validation {
		// Delegate to shared mapper which:
		// - Maps booleans/ints/strings
		// - Maps Options by toString()
		// - Maps Enum by name (case-insensitive)
		// - Aggregates non-fatal warnings into Validation.message
		val validation = ScriptConfigJson.applyTo(this, config)
		// Notify script about updated config (fast, side-effect safe)
		try {
			onConfigUpdated()
		} catch (_: Throwable) {
		}
		return validation
	}

	fun onConfigUpdated() {
		println("Config updated")
		this::class.java.declaredFields.filter { ConfigItem::class.java.isAssignableFrom(it.type) }.forEach { field ->
			field.isAccessible = true
			val configItem = field.get(this) as? ConfigItem<*>
			val name = configItem?.name
			val value = configItem?.value
			println("$name: $value")
		}
	}
}

private val raw = Regex("Raw\\s+.*")
private val sweetcorn = "Sweetcorn"

class Cook : State<CookingStateMachine>() {

	override suspend fun CookingStateMachine.checkNext(): State<CookingStateMachine>? {
		println("CookingCheckNext")
//		println(inventory.hasItem(raw).toString())
//		println(inventory.hasItem(sweetcorn).toString())


		if (findClosestObject( 40) {it.hasOption("Load Last Preset from")} == null) {
			println("Walking")
			return walkToCatherbyCooking
		}



		if (!inventory.hasItem(raw) && !inventory.hasItem(sweetcorn)) {
			println("Banking")
			return BankCooking
		}

		return null
	}

	override suspend fun CookingStateMachine.stateLoop() {

		println("CookingStateLoop")

		if (worldHop.value && checkWorldPop()) return

		if (interfaces.isOpen(1251)) {
			println("Still cooking")
			delayUntil(5000) { !interfaces.isOpen(1251) }
			delay(1500, 1000)
			return
		}

		if (timeSinceLastXpDrop > gaussian(10000, 2059)) {
			if (!makeXOpen) {
				if (interactClosestObject("Cook-at", 40)) delayUntil(10000) { makeXOpen }
				return
			}

             continueMakeX()
			waitForXPDrop(Skill.COOKING)
		}
	}

}

val walkToCatherbyCooking = traversal(BankCooking, { localPlayer.tile.getDistance(Tile.of(2797, 3444, 0)) < 8 }) {
	chebychevPath(
		localPlayer.tile,
		listOf(
			Tile.of(2810, 3449, 0), Tile.of(2797, 3444, 0)
		),
		fallback = { useLodestone(Lodestone.CATHERBY) },
		reached = { localPlayer.tile.getDistance(Tile.of(2797, 3444, 0)) < 8 })
}

object BankCooking : State<CookingStateMachine>() {
	override suspend fun CookingStateMachine.checkNext(): State<CookingStateMachine>? {
		println("BankingCheckNext")
		println(inventory.hasItem(raw))

		if (inventory.hasItem(raw) || inventory.hasItem(sweetcorn)) return Cook()

		return null
	}

	override suspend fun CookingStateMachine.stateLoop() {
		println("BankingStateLoop")
		if (!inventory.hasItem(raw) && !inventory.hasItem(sweetcorn) && interactClosestObject("Load Last Preset from")) delayUntil(
			6000
		) { inventory.hasItem(raw) || inventory.hasItem(sweetcorn) }

	}
}
