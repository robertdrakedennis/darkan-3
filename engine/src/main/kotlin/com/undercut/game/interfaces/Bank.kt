package com.undercut.game.interfaces

import com.undercut.cache.type.items.ItemType
import com.undercut.game.nxt.interfaces.InterfaceComponent
import com.undercut.script.api.interfaces

class Bank {
    companion object {
        const val BANK_INTERFACE_ID = 517
        const val BANK_ITEMS_COMPONENT_ID = 202
        const val BANK_INV_COMPONENT_ID = 15

        fun fetchBankArray(componentId: Int): ArrayList<InterfaceComponent> {
            val bankItems = interfaces.getComponent(BANK_INTERFACE_ID, componentId)
            val bankChildren = bankItems?.slotChildren
            val res = ArrayList<InterfaceComponent>()
            if (bankChildren?.size!! > 0) {
                for (c in bankChildren) {
                    if (c.itemId > 0 && c.stackSize > 0)
                        res.add(c)
                }
            }
            return res
        }

        fun fetchBankItemsArray() = fetchBankArray(BANK_ITEMS_COMPONENT_ID)
        fun fetchBankInventoryArray() = fetchBankArray(BANK_INV_COMPONENT_ID)

        fun doBankAction(componentId: Int, slotId: Int = -1, optionNum: Int = 1) =
            IFSlot(BANK_INTERFACE_ID, componentId, slotId).click(optionNum)

        internal fun doBankItemsAction(name: String, optionNum: Int): Boolean {
            for (item in fetchBankItemsArray()) {
                if (ItemType.get(item.itemId).name == name)
                    return doBankAction(BANK_ITEMS_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }

        internal fun doBankItemsAction(regex: Regex, optionNum: Int): Boolean {
            for (item in fetchBankItemsArray()) {
                if (regex.matches(ItemType.get(item.itemId).name))
                    return doBankAction(BANK_ITEMS_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }

        internal fun doBankItemsAction(itemId: Int, optionNum: Int): Boolean {
            for (item in fetchBankItemsArray()) {
                if (item.itemId == itemId)
                    return doBankAction(BANK_ITEMS_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }

        internal fun doBankInventoryAction(name: String, optionNum: Int): Boolean {
            for (item in fetchBankInventoryArray()) {
                if (ItemType.get(item.itemId).name == name)
                    return doBankAction(BANK_INV_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }

        internal fun doBankInventoryAction(regex: Regex, optionNum: Int): Boolean {
            for (item in fetchBankInventoryArray()) {
                if (regex.matches(ItemType.get(item.itemId).name))
                    return doBankAction(BANK_INV_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }

        internal fun doBankInventoryAction(itemId: Int, optionNum: Int): Boolean {
            for (item in fetchBankInventoryArray()) {
                if (item.itemId == itemId)
                    return doBankAction(BANK_INV_COMPONENT_ID, item.slotId, optionNum)
            }
            return false
        }
    }
}