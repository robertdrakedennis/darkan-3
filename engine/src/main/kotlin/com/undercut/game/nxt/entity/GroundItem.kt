package com.undercut.game.nxt.entity

import world.gregs.voidps.type.Tile
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.nxt.DoActionOpcode
import com.undercut.script.api.interfaces
import com.undercut.script.api.localPlayer
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.ItemDefinition

private val MENU_OPS = arrayOf(
    DoActionOpcode.GROUND_ITEM_1,
    DoActionOpcode.GROUND_ITEM_2,
    DoActionOpcode.GROUND_ITEM_3,
    DoActionOpcode.GROUND_ITEM_4,
    DoActionOpcode.GROUND_ITEM_5,
    DoActionOpcode.GROUND_ITEM_6
)

data class GroundItem(val id: Int, var amount: Int = 1, val tile: Tile) {
    val name: String
        get() = Cache.item(id)?.name ?: "null"

    val groundOps: Array<String?>
        get() = Cache.item(id)?.groundActions ?: arrayOfNulls(6)

    fun interact(option: Int): Boolean {
        if (!localPlayer.tile.withinDistance(tile, 25)) return false
        if (option < 0 || option >= MENU_OPS.size) return false
        val doAction = MENU_OPS.getOrNull(option) ?: return false
        doAction.fire(id, tile.x.toInt(), tile.y.toInt())
        return true
    }

    fun target(): Boolean {
        if (!localPlayer.tile.withinDistance(tile, 25)) return false
        DoActionOpcode.SELECT_GROUND_ITEM.fire(id, tile.x.toInt(), tile.y.toInt())
        return true
    }

    fun telegrab(): Boolean {
        if (!interfaces.isOpen(1886)) return false
        val alch = IFSlot(1886, 1, 32)
        if (alch.select())
            return target()
        return false
    }

    fun getDef() = Cache.item(id) ?: ItemDefinition.EMPTY

    fun interact(option: String): Boolean {
        val op = getDef().getGroundOpIdForName(option)
        if (op != -1) {
            interact(op)
            return true
        }
        return false
    }
}