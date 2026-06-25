package com.undercut.cache.tools

import com.undercut.cache.type.items.ItemType
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.items.Item

fun main(args: Array<String>) {
    for (i in 0..ItemType.getParser().getMaxId()) {
        try {
            val type = ItemType.get(i)
            if (type.name == "Passing bracelet")
                println("$i - ${type.name} - ${type.params} - ${type.getInvOpIdForName("Rub")}")
        } catch (e: Exception) {

        }
    }
    val test = Item(36619, 1, IFSlot(1464, 15, 2))
    println("teleport op: ${test.getDef().getInvOpIdForName("Rub")}")
}