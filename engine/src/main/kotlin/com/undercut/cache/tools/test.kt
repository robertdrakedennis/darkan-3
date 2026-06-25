package com.undercut.cache.tools

import com.undercut.util.componentIdFromHash
import com.undercut.util.interfaceIdFromHash

fun main() {
    val interfaceHash = 70254766
    println("${interfaceIdFromHash(interfaceHash)}, ${componentIdFromHash(interfaceHash)}")

    val overloadRegex = Regex("overload", RegexOption.IGNORE_CASE)
    val name = "Holy overload potion"
    println(overloadRegex.matches(name))
}