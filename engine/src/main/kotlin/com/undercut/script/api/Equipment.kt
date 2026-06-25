package com.undercut.script.api

import com.undercut.game.items.Item
import com.undercut.script.api.Equipment.Slot.Companion.fromIndex
import com.undercut.script.api.Equipment.Slot.Companion.getItem

class Equipment {

    enum class Slot(val index: Int) {
        HEAD(0),
        CAPE(1),
        NECK(2),
        WEAPON(3),
        BODY(4),
        SHIELD(5),
        LEGS(7),
        HANDS(9),
        FEET(10),
        RING(12),
        AMMUNITION(13),
        POCKET(17);

        companion object {
            /**
             * Click the equipped item in the given [slot] using a textual [option] (e.g., "Wear", "Activate").
             * Returns false if the slot is empty or the interaction fails.
             */
            fun useItemInSlot(slot: Slot, option: String): Boolean {
                return equipment.firstOrNull { it.slot.slotId == slot.index }?.click(option) == true
            }

            /**
             * Click the default option (index 0) on the equipped item in the given [slot].
             * Returns false if the slot is empty or the interaction fails.
             */
            fun useItemInSlot(slot: Slot): Boolean {
                return equipment.firstOrNull { it.slot.slotId == slot.index }?.click(0) == true
            }

            /**
             * Click the equipped item in the given [slot] using an option [index].
             * Returns false if the slot is empty or the interaction fails.
             */
            fun useItemInSlot(slot: Slot, option: Int): Boolean {
                return equipment.firstOrNull { it.slot.slotId == slot.index }?.click(option) == true
            }

            @Deprecated("Use getItem(slot) which returns Item?", ReplaceWith("getItem(slot)"))
                    /**
                     * Unsafe accessor for the item in [slot].
                     * Prefer [getItem] which returns null if the slot is empty.
                     */
            fun getItemInSlot(slot: Slot): Item {
                return equipment.first { it.slot.slotId == slot.index }
            }

            /**
             * Safe accessor for the item in a given equipment slot.
             * Returns null when the slot is empty.
             */
            fun getItem(slot: Slot): Item? {
                return equipment.firstOrNull { it.slot.slotId == slot.index }
            }

            /**
             * Returns true if there is an equipped item in the given [slot].
             */
            fun hasItem(slot: Slot): Boolean = getItem(slot) != null

            /**
             * Returns true if any equipped item has the given [id].
             */
            fun isEquipped(id: Int): Boolean = equipment.any { it.id == id }

            /**
             * Returns true if any equipped item has the given [name].
             * Name comparison is case-insensitive.
             */
            fun isEquipped(name: String): Boolean =
                equipment.any { it.name.equals(name, ignoreCase = true) }

            /**
             * Returns a map of equipped items keyed by their [Slot].
             * Only slots that map to a known [Slot] are included.
             */
            fun getEquippedItems(): Map<Slot, Item> =
                equipment.mapNotNull { item ->
                    fromIndex(item.slot.slotId)?.let { s -> s to item }
                }.toMap()

            /**
             * Safe lookup for equipment slot by index.
             */
            fun fromIndex(index: Int): Slot? = entries.firstOrNull { it.index == index }

            @Deprecated("Use fromIndex(var0)", ReplaceWith("fromIndex(var0)"))
                    /**
                     * Deprecated alias for [fromIndex].
                     */
            fun resolve(var0: Int): Slot? = fromIndex(var0)
        }
    }

}

/**
 * Equipment interaction helpers as Script extensions for ergonomic flows.
 */

/** Click the equipped item in [slot] using a textual [option]. */
fun equipmentClick(slot: Equipment.Slot, option: String): Boolean =
    Equipment.Slot.useItemInSlot(slot, option)

/** Click the equipped item in [slot] using an option [index] (default 0). */
fun equipmentClick(slot: Equipment.Slot, option: Int = 0): Boolean =
    Equipment.Slot.useItemInSlot(slot, option)

/** Get the equipped [Item], or null if the [slot] is empty. */
fun equipmentItem(slot: Equipment.Slot): Item? =
    Equipment.Slot.getItem(slot)

/** Returns true if [slot] contains an item whose id is in [ids]. */
fun isEquipped(slot: Equipment.Slot, vararg ids: Int): Boolean =
    Equipment.Slot.getItem(slot)?.let { ids.contains(it.id) } == true