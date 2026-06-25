package com.undercut.cache.tools

import com.undercut.cache.type.npcs.NPCType

fun main(args: Array<String>) {
    val souls = intArrayOf(17720, 17739, 17802, 18222)
    for (i in 0..NPCType.getParser().getMaxId()) {
        try {
            val type = NPCType.get(i)
//            if (type.containsOp("Siphon"))
//                println("${type.id} (${type.name}) - ${type.options.contentToString()} - ${type.transformTo?.contentToString()}")
//            if (type.transformTo?.contains(30432) == true)
//                print("$i,")
            if (type.name.contains(" Soul"))
                println("${type.id} - ${type.name} - ${type.options.contentToString()}")
            if (souls.any { type.transformTo?.contains(it) == true })
                println("${type.id} - ${type.name} - ${type.transformTo.contentToString()}")
        } catch (e: Exception) {

        }
    }
    val type = NPCType.get(16181)
    println("${type.id} - ${type.varp} - ${type.varpBit} - ${type.transformTo.contentToString()}")
}