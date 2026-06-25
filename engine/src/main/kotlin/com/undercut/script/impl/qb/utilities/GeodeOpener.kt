package com.undercut.script.impl.qb.utilities

import com.undercut.game.interfaces.IFSlot
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.inventory
import com.undercut.script.api.loadLastPresetClosestBank
import com.undercut.script.ScriptDescription

@ScriptDescription(
    name = "Geode Opener",
    version = "1.0.0",
    author = "QB",
    description = "Opens geodes"
)
class GeodeOpener : StateMachineScript<GeodeOpener>() {
	override fun getStartState(): State<GeodeOpener> {
		return GeodeOpeningState()
	}
}

class GeodeOpeningState : State<GeodeOpener>() {
	override suspend fun GeodeOpener.checkNext(): State<GeodeOpener>? {
		return null
	}

	override suspend fun GeodeOpener.stateLoop() {
		if (inventory.isFull) {
			loadLastPresetClosestBank()
			delay(1200, 100)
			return
		}
		var item = inventory.getItem(Regex(".*geode.*", RegexOption.IGNORE_CASE))
		if (item != null) {
			IFSlot(1473, 5, item.slot.slotId).click()
			delay(150, 100)
		}
	}

}