//package com.undercut.script
//
//import com.undercut.game.items.Item
//import com.undercut.game.nxt.inventories.Inventory
//import com.undercut.script.api.inventory
//import com.undercut.script.event.impl.InventoryUpdateEvent
//
//class InventoryMonitor {
//    private val inventorySnapshots = mutableMapOf<Int, Array<Item?>>()
//
//    fun checkInventoryChanges(inventory: Inventory) {
//        val inventoryId = inventory.id
//        val currentItems = Array<Item?>(inventory.items.size.toInt()) { index ->
//            try {
//                inventory[index]
//            } catch (e: Exception) {
//                null
//            }
//        }
//
//        val previousItems = inventorySnapshots[inventoryId]
//
//        if (previousItems == null) {
//            // First time seeing this inventory, just store the snapshot
//            inventorySnapshots[inventoryId] = currentItems.copyOf()
//            return
//        }
//
//        // Check for changes
//        val maxSize = maxOf(currentItems.size, previousItems.size)
//        for (slot in 0 until maxSize) {
//            val oldItem = previousItems.getOrNull(slot)
//            val newItem = currentItems.getOrNull(slot)
//
//            if (hasItemChanged(oldItem, newItem)) {
//                val event = InventoryUpdateEvent(
//                    inventoryId = inventoryId,
//                    slot = slot,
//                    oldItem = oldItem,
//                    newItem = newItem
//                )
//                // Push the event to the ScriptExecutor event bus
//                ScriptExecutor.pushEvent(event)
//            }
//        }
//
//        // Update the snapshot
//        inventorySnapshots[inventoryId] = currentItems.copyOf()
//    }
//
//    private fun hasItemChanged(oldItem: Item?, newItem: Item?): Boolean {
//        // Both null - no change
//        if (oldItem == null && newItem == null) return false
//
//        // One is null, other isn't - changed
//        if (oldItem == null || newItem == null) return true
//
//        // Both exist - check if they're different
//        return oldItem.id != newItem.id || oldItem.amount != newItem.amount
//    }
//
//    fun monitorMainInventory() {
//        try {
//            checkInventoryChanges(inventory)
//        } catch (e: Exception) {
//            // Silently handle errors - inventory might not be accessible
//            println("Error monitoring main inventory: ${e.message}")
//
//        }
//    }
//
//    companion object {
//        val instance = InventoryMonitor()
//    }
//}
