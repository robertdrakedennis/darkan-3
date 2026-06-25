package com.undercut.ui.tabs

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.ui.UI
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiTableColumnFlags
import com.undercut.ui.backend.dsl.utils.ImGuiTableFlags

object InventoryTab {
    fun ChildScope.render() {
        text("Inventory")

        // Build a custom combo with selectable entries, binding directly to external inventory ID
        val types = UI.InventoryType.entries
        val current = types.firstOrNull { it.id == UIState.inventoryId.value } ?: UI.InventoryType.BACKPACK
        combo("InventoryType", current.displayName) {
                types.forEach { t ->
                    val isSelected = (t.id == UIState.inventoryId.value)
                    selectable(t.displayName, isSelected) {
                        UIState.inventoryId.value = t.id
                    }
                }
        }
         
        text("Inventory ID:")
        sameLine()
        group {
            inputInt("##invid", UIState.inventoryId.value) { newVal ->
                UIState.inventoryId.value = newVal
            }
        }

        checkbox("Auto Refresh", UIState.inventoryEnabled)
        sameLine()
        button("Load") {
            loadInventory(UIState.inventoryId.value)
        }
        sameLine()
        button("Clear") {
            UIState.inventoryData.clear()
        }

        inputText("Search", UIState.inventorySearchText)

        separator()

        table(id = "InventoryTable", columns = 4, flags = ImGuiTableFlags.SizingStretchSame or ImGuiTableFlags.BordersInnerH) {
            setupColumn("Slot", ImGuiTableColumnFlags.WidthFixed)
            setupColumn("ID", ImGuiTableColumnFlags.WidthFixed)
            setupColumn("Name" )
            setupColumn("Amount", ImGuiTableColumnFlags.WidthFixed)
            headersRow()

            val filteredData = if (UIState.inventorySearchText.value.isEmpty()) {
                UIState.inventoryData
            } else {
                UIState.inventoryData.filter { item ->
                    item.name.contains(UIState.inventorySearchText.value, ignoreCase = true)
                }
            }

            filteredData.forEach { item ->
                nextRow()
                nextColumn()
                text(item.slot.toString())
                nextColumn()
                text(item.itemId.toString())
                nextColumn()
                text(item.name)
                nextColumn()
                text(item.amount.toString())
            }
        }
    }

    fun loadInventory(inventoryId: Int) {
        try {
            UIState.inventoryData.clear()

            if (!Bootstrap.client.inventoryManager.exists(inventoryId)) {
                return
            }

            val inventory = Bootstrap.client.inventoryManager[inventoryId]

            if (inventory.isEmpty) {
                return
            }

            inventory.forEachIndexed { index, item ->
                UIState.inventoryData.add(
                    UI.InventoryEntry(
                        slot = item.slot.slotId,
                        itemId = item.id,
                        name = item.name,
                        amount = item.amount
                    )
                )
            }
        } catch (e: Exception) {
            println("Failed to load inventory: ${e.message}")
        }
    }
}