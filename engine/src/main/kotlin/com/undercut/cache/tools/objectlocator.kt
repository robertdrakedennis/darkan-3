package com.undercut.cache.tools

import com.undercut.cache.type.objects.ObjectType

fun main(args: Array<String>) {
    for (i in 0..ObjectType.getParser().getMaxId()) {
        try {
            val type = ObjectType.get(i)
            if (type.containsOp("Gather"))
                println("${type.id} (${type.name}) - ${type.options.contentToString()} - ${type.transformTo?.contentToString()}")
//            if (type.transformTo?.contains(127316) == true)
//                println("${type.id} (${type.name}) - ${type.options.contentToString()} - ${type.varp} ${type.varpBit} - ${type.transformTo?.contentToString()}")
        } catch (e: Exception) {

        }
    }
}