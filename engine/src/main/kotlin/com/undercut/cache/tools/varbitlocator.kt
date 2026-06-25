package com.undercut.cache.tools

import com.undercut.cache.type.vars.VarbitType

fun main(args: Array<String>) {
    for (i in 0..VarbitType.getParser().getMaxId()) {
        try {
            val type = VarbitType.get(i)
            if (type.baseVar == 4501)
                println("${type.id} - size: ${type.startBit}->${type.endBit} (${type.endBit-type.startBit})")
        } catch (e: Exception) {

        }
    }
}