package com.undercut.traversal

import com.undercut.script.State
import com.undercut.script.StateMachineScript

open class Traversal<T : StateMachineScript<T>>(private val next: State<T>, finishedCondition: T.() -> Boolean, nodes: TraversalNodeList) : State<T>() {
    private val processor = TraversalProcessor(finishedCondition, nodes)

    override suspend fun T.checkNext(): State<T>? =
        if (!processor.process(this)) next else null

    override suspend fun T.stateLoop() { }

    companion object {
        fun <T : StateMachineScript<T>> traversal(next: State<T>, finishedCondition: T.() -> Boolean, init: TraversalBuilder<T>.() -> Unit): Traversal<T> {
            val builder = TraversalBuilder<T>()
            builder.init()
            return builder.build(next, finishedCondition)
        }
    }
}