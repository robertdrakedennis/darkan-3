package com.undercut.traversal

import com.undercut.script.Script

abstract class TraversalNode {
    var prev: TraversalNode? = null
    var next: TraversalNode? = null

    abstract suspend fun process(script: Script): Boolean
    abstract fun reached(script: Script): Boolean
    abstract fun copy(): TraversalNode
}