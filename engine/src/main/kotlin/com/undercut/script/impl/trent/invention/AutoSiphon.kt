package com.undercut.script.impl.trent.invention

import com.undercut.game.interfaces.IFSlot
import com.undercut.game.invention.InventionXpTracker
import com.undercut.game.items.Item
import com.undercut.script.BooleanConfigItem
import com.undercut.script.ConfigurableScript
import com.undercut.script.IntConfigItem
import com.undercut.script.Script
import com.undercut.script.ScriptCategory
import com.undercut.script.ScriptDescription
import com.undercut.script.StringConfigItem
import com.undercut.script.api.Equipment
import com.undercut.script.api.interfaces
import com.undercut.script.api.inventory
import com.undercut.script.api.localPlayer
import com.undercut.util.gaussian

/**
 * Parallel helper that watches worn augmented gear via [InventionXpTracker]. When a worn item
 * reaches its optimal siphon level it pauses whatever script is running, de-equips the item,
 * uses an Equipment siphon on it, re-equips it, and resumes the paused script.
 *
 * Augmented items carry no right-click "Siphon" option and siphoning is an item-on-item action
 * (Equipment siphon -> augmented item), so the item must sit in the inventory to be siphoned —
 * hence the de-equip / re-equip dance.
 */
@ScriptDescription(
    name = "Auto Siphon",
    version = "1.0.0",
    author = "Trent",
    description = "Optional parallel helper: when a worn augmented item hits its siphon level it pauses your active " +
        "script, de-equips the item, siphons it with an Equipment siphon, re-equips it, then resumes.",
    category = ScriptCategory.INVENTION
)
class AutoSiphon : Script(), ConfigurableScript {

    val siphonLevel = IntConfigItem(
        name = "Siphon level (0 = auto)",
        description = "Item level that triggers a siphon. 0 follows the tracker's optimal level (10 below Invention 60, 12 at/above).",
        initialValue = 0,
        min = 0,
        max = 20
    )

    val siphonItemName = StringConfigItem(
        name = "Siphon item",
        description = "Inventory item used to siphon (item-on-item).",
        initialValue = DEFAULT_SIPHON_ITEM
    )

    val onlyWhenStill = BooleanConfigItem(
        name = "Only while standing still",
        description = "Skip siphoning while the player is moving, so we don't yank gear mid-step.",
        initialValue = true
    )

    override suspend fun loop() {
        if (onlyWhenStill.value && isPlayerMoving()) return
        val candidate = findCandidate() ?: return
        siphon(candidate.first, candidate.second)
    }

    private fun targetLevel(): Int {
        val override = siphonLevel.value
        return if (override in 1..20) override else InventionXpTracker.assumedSiphonLevel
    }

    private fun findCandidate(): Pair<Equipment.Slot, Item>? {
        val target = targetLevel()
        return Equipment.Slot.getEquippedItems().entries.firstOrNull { (_, item) ->
            InventionXpTracker.isAugmentedItem(item) &&
                InventionXpTracker.levelForXp(InventionXpTracker.itemXpOf(item)) >= target
        }?.toPair()
    }

    private suspend fun siphon(slot: Equipment.Slot, wornItem: Item) {
        val itemId = wornItem.id
        val siphonName = siphonItemName.value.ifBlank { DEFAULT_SIPHON_ITEM }

        if (inventory.isFull) return
        if (!inventory.hasItem(siphonName)) return

        if (!pauseOthers()) return
        try {
            if (!unequip(slot, itemId)) return
            // Past here the item is in the inventory, so it must always be put back on.
            try {
                val unequipped = inventory.getItem(itemId) ?: return
                val siphonItem = inventory.getItem(siphonName) ?: return
                if (!siphonItem.useOn(unequipped)) return
                if (!confirmSiphon()) return
                delayUntil(gaussian(6182L, 1110L)) {
                    val current = inventory.getItem(itemId) ?: return@delayUntil false
                    InventionXpTracker.levelForXp(InventionXpTracker.itemXpOf(current)) < targetLevel()
                }
            } finally {
                reequip(itemId)
            }
        } finally {
            resumeOthers()
        }
    }

    private suspend fun unequip(slot: Equipment.Slot, itemId: Int): Boolean {
        if (Equipment.Slot.getItem(slot)?.id != itemId) return false
        if (!Equipment.Slot.useItemInSlot(slot, REMOVE_OPTION)) return false
        delayUntil(gaussian(4582L, 1110L)) {
            !Equipment.Slot.isEquipped(itemId) && inventory.getItem(itemId) != null
        }
        return !Equipment.Slot.isEquipped(itemId) && inventory.getItem(itemId) != null
    }

    private suspend fun reequip(itemId: Int): Boolean {
        if (Equipment.Slot.isEquipped(itemId)) return true
        val item = inventory.getItem(itemId) ?: return false
        delay(612, 240)
        item.click("Wear") || item.click("Wield") || item.click("Equip")
        delayUntil(gaussian(4582L, 1110L)) { Equipment.Slot.isEquipped(itemId) }
        return Equipment.Slot.isEquipped(itemId)
    }

    private suspend fun confirmSiphon(): Boolean {
        delayUntil(gaussian(2782L, 760L)) { interfaces.isOpen(CONFIRM_INTERFACE) }
        if (!interfaces.isOpen(CONFIRM_INTERFACE)) return true

        var attempts = 0
        while (interfaces.isOpen(CONFIRM_INTERFACE) && attempts < CONFIRM_ATTEMPTS) {
            attempts++
            delay(519, 180)
            IFSlot(CONFIRM_INTERFACE, CONFIRM_OPTION, -1).dialogueContinue()
            delayUntil(gaussian(1800L, 500L)) { !interfaces.isOpen(CONFIRM_INTERFACE) }
        }
        return !interfaces.isOpen(CONFIRM_INTERFACE)
    }

    private fun isPlayerMoving(): Boolean =
        runCatching { localPlayer.isMoving }.getOrDefault(false)

    private companion object {
        const val DEFAULT_SIPHON_ITEM = "Equipment siphon"
        const val REMOVE_OPTION = 1

        const val CONFIRM_INTERFACE = 847
        const val CONFIRM_OPTION = 22
        const val CONFIRM_ATTEMPTS = 3
    }
}
