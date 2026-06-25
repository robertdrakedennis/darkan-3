package com.undercut.game.input

import com.undercut.game.hooks.impl.isMouseReady
import com.undercut.game.hooks.impl.keyDown as hookKeyDown
import com.undercut.game.hooks.impl.keyUp as hookKeyUp
import com.undercut.game.hooks.impl.leftClick as hookLeftClick
import com.undercut.game.hooks.impl.leftClickDown as hookLeftClickDown
import com.undercut.game.hooks.impl.leftClickUp as hookLeftClickUp
import com.undercut.game.hooks.impl.moveMouse as hookMoveMouse
import com.undercut.game.hooks.impl.rightClick as hookRightClick
import com.undercut.game.hooks.impl.scroll as hookScroll

/**
 * Facade for injecting synthetic input through the game's native input pipeline.
 *
 * All injection calls suppress recording via [InputRecorder.suppressRecording] to prevent
 * feedback loops (injected events being re-captured into the training data).
 *
 * Mouse injection calls trampolines (bypassing our hooks entirely).
 * Keyboard injection calls the raw function handles — the suppress flag prevents re-recording.
 */
object InputInjector {

    val isReady: Boolean get() = isMouseReady()

    private inline fun <T> withSuppression(block: () -> T): T {
        InputRecorder.suppressRecording = true
        try {
            return block()
        } finally {
            InputRecorder.suppressRecording = false
        }
    }

    fun moveMouse(x: Int, y: Int) = withSuppression {
        hookMoveMouse(x, y)
    }

    fun leftClickDown(x: Int, y: Int) = withSuppression {
        hookLeftClickDown(x, y)
    }

    fun leftClickUp(x: Int, y: Int) = withSuppression {
        hookLeftClickUp(x, y)
    }

    fun leftClick(x: Int, y: Int) = withSuppression {
        hookLeftClick(x, y)
    }

    fun rightClick(x: Int, y: Int) = withSuppression {
        hookRightClick(x, y)
    }

    fun scroll(x: Int, y: Int, delta: Int) = withSuppression {
        hookScroll(x, y, delta)
    }

    fun keyDown(key: Int): Int = withSuppression {
        hookKeyDown(key)
    }

    fun keyUp(key: Int): Int = withSuppression {
        hookKeyUp(key)
    }

    fun keyPress(key: Int) = withSuppression {
        hookKeyDown(key)
        hookKeyUp(key)
    }
}
