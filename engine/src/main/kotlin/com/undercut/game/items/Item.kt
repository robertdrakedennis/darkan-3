package com.undercut.game.items

import com.undercut.cache.type.items.ItemType
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.nxt.entity.GroundItem
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.game.nxt.inventories.ObjVarDomain
import com.undercut.script.api.bank
import com.undercut.script.api.equipment
import com.undercut.script.api.interfaces
import com.undercut.script.api.inventory

data class Item(
    val id: Int,
    var amount: Int = 1,
    val slot: IFSlot = IFSlot(-1, -1, -1),
    val varDomain: ObjVarDomain? = null
) {
    val name: String
        get() = ItemType.get(id).name

    val invOps: Array<String?>
        get() = ItemType.get(id).inventoryActions

    fun click(option: Int): Boolean {
        return slot.click(option)
    }

    fun useOn(target: Item): Boolean {
        if (slot.select())
            return target.slot.target()
        return false
    }

    fun useOn(target: NPC): Boolean {
        if (slot.select())
            return target.target()
        return false
    }

    fun useOn(target: SceneObject): Boolean {
        if (slot.select())
            return target.target()
        return false
    }

    fun useOn(target: GroundItem): Boolean {
        if (slot.select())
            return target.target()
        return false
    }

    fun alch(): Boolean {
        if (!interfaces.isOpen(1886)) return false
        val alch = IFSlot(1886, 1, 47)
        if (alch.select())
            return slot.target()
        return false
    }

    fun disassemble(): Boolean {
        if (!interfaces.isOpen(1886)) return false
        val alch = IFSlot(1886, 1, 189)
        if (alch.select())
            return slot.target()
        return false
    }

//    fun useOn(target: GroundItem): Boolean {
//        if (slot.select())
//            return target.target()
//        return false
//    }

    fun getDef() = ItemType.get(id)

    fun click(option: String): Boolean {
        var op = -1
        if (slot.interfaceId == equipment.interfaceId) {
            op = getDef().getEquipOpIdForName(option)
            if (op == -1) return false
            op += 2
        } else
            if (slot.interfaceId == inventory.interfaceId) {
                op = getDef().getInvOpIdForName(option)
                op = when (op) {
                    0 -> 1
                    1 -> 2
                    2 -> 3
                    3 -> 7
                    4 -> 8
                    else -> -1
                }
            } else if (slot.interfaceId == bank.interfaceId) {
                op = when (option) {
                    "Withdraw-1" -> 1
                    "Withdraw-5" -> 3
                    "Withdraw-10" -> 4
                    "Withdraw-All" -> 7
                    "Withdraw-X" -> 6
                    "Withdraw-Placeholder" -> 8
                    else -> 1
                }
            }
//        else if (slot.interfaceId == bankInventory.interfaceId) {
//            op = when (option) {
//                "Deposit-1" -> 2
//                "Deposit-5" -> 3
//                "Deposit-10" -> 4
//                "Deposit-50" -> 5
//                "Deposit-X" -> 6
//                "Deposit-All" -> 7
//                else -> 1
//            }
//        } else if (slot.interfaceId == depositBox.interfaceId) {
//            op = when (option) {
//                "Deposit-1" -> 2
//                "Deposit-5" -> 3
//                "Deposit-10" -> 4
//                "Deposit-All" -> 5
//                "Deposit-X" -> 6
//                else -> 1
//            }
//        }
        if (op != -1) {
            click(op)
            return true
        }
        return false
    }
}
