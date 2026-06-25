package com.undercut.script.impl.qb.utilities.BankStanding

import com.undercut.game.hooks.impl.DoAction
import com.undercut.game.interfaces.IFSlot
import com.undercut.script.State
import com.undercut.script.StateMachineScript
import com.undercut.script.api.inventory
import com.undercut.script.api.loadLastPresetClosestBank
import com.undercut.script.ScriptDescription
import com.undercut.script.api.bankOpen
import com.undercut.script.api.interfaces
import com.undercut.script.api.openClosestBank

@ScriptDescription(
	name = "BankStandingScript", version = "1.0.0", author = "Query", description = "BankStandingScript"
)
class BankStandingScript : StateMachineScript<BankStandingScript>() {
	override fun getStartState(): State<BankStandingScript> {
		return BankStandingState()
	}

}

class BankStandingState : State<BankStandingScript>() {
	override suspend fun BankStandingScript.checkNext(): State<BankStandingScript>? {

		return null
	}

	override suspend fun BankStandingScript.stateLoop() {


		if (inventory.getItem(Regex(".*lamp.*", RegexOption.IGNORE_CASE)) != null) {
			if (interfaces.isOpen(678)) {
				//CLick USE ALL
				IFSlot(678, 19, -1).dialogueContinue()


			}
			if(interfaces.isOpen(1263)){
				//SELECT SKILL - MINING
				IFSlot(1263, 18, -1).click()
				delay(1200,200)
				IFSlot(1263, 74, 13).dialogueContinue(13)
				delay(1200,200)
				return
			}

			inventory.getItem(Regex(".*lamp.*", RegexOption.IGNORE_CASE))?.click("Rub")
			delay(1200,200)
			return
		}


		if (inventory.getItem(Regex(".*star.*", RegexOption.IGNORE_CASE)) != null) {
			if (interfaces.isOpen(678)) {
				//CLick USE ALL
				IFSlot(678, 19, -1).dialogueContinue()


			}
			if(interfaces.isOpen(1263)){
				//SELECT SKILL - MINING
				IFSlot(1263, 18, -1).click()
				delay(1200,200)
				IFSlot(1263, 74, 13).dialogueContinue(13)
				delay(1200,200)
				return

			}
			inventory.getItem(Regex(".*star.*", RegexOption.IGNORE_CASE))?.click("Choose skill")
			delay(1200,200)
			return
		}

		if (inventory.getItem(Regex(".*token box.*", RegexOption.IGNORE_CASE)) != null) {

			inventory.getItem(Regex(".*token box.*", RegexOption.IGNORE_CASE))?.click("Open")
			delay(1200,200)
			return
		}

		if (inventory.getItem(Regex(".*cash bag.*", RegexOption.IGNORE_CASE)) != null) {

			inventory.getItem(Regex(".*cash bag.*", RegexOption.IGNORE_CASE))?.click("Open")
			delay(1200,200)
			return
		}

		if (inventory.getItem(Regex(".*Coupon.*", RegexOption.IGNORE_CASE)) != null) {

			inventory.getItem(Regex(".*Coupon.*", RegexOption.IGNORE_CASE))?.click("Consume")
			delay(1200,200)
			return
		}

		if (inventory.getItem(Regex(".*rune.*", RegexOption.IGNORE_CASE)) != null) {

			inventory.getItem(Regex(".*rune.*", RegexOption.IGNORE_CASE))?.click("Investigate")
			delay(1200,200)
			return
		}


		if (inventory.freeSlots > 16 && inventory.getItem(59427) != null) {
			inventory.getItem(59427)?.click("Open all")
			delay(2400,200)
			return
		}

		if(bankOpen){
			IFSlot(517,39,-1).click()
			delay(1200,200)
			IFSlot(517,318,-1).click()
			delay(1200,200)
			return
		}else{
			openClosestBank()
			delay(1200,200)
			return
		}



		


//		println("WTF")
//		for(y in 1..81) {
//			println("$y")
//			var x = interfaces.getComponent(550, y)
//
//
//			if (x != null) {
//
//				if (x.text.isNotEmpty()) {
//					println("World: ${x.text}")
//				} else
//
//					x.slotChildren?.forEach {
//						if (it.text.isNotEmpty())
//							println("World: ${it.text}")
//
//					}
//			}
//			delay(200)
//		}

	}

}


		