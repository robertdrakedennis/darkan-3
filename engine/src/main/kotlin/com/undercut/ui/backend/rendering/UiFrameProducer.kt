package com.undercut.ui.backend.rendering

import com.undercut.script.ScriptExecutor

/**
 * Builds the next frame's ImGui command buffer on the game's main-logic thread (driven by
 * `ClientMainLogic`), NOT on a background thread.
 *
 * This is the whole fix for the recurring render-thread SIGSEGVs: `render()` methods read live native
 * game state (varps, cache types, the Client struct, EASTL containers). The only thread that mutates
 * that state is the main-logic thread, and it is never mutating and building at the same time — so
 * reading live state here is race-free, with no snapshotting/copying. The DSL records commands into a
 * plain list ([CommandRenderer.withCapture]); the render thread ([EGLSwapBuffers]) only replays that
 * list and never touches game memory.
 */
object UiFrameProducer {
    val frames = FrameCommandBuffers()

    /** Set once ImGui has been initialized on the render thread; before that there is nothing to draw. */
    @Volatile
    var enabled = false

    /** Run every registered render method into a fresh buffer and publish it for the render thread. */
    fun build() {
        if (!enabled) return
        val target = frames.acquireForWrite()
        ImGuiRenderManager.getRenderMethods().forEach { handle ->
            try {
                CommandRenderer.withCapture<Unit>(target) { handle.invokeExact() }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        try {
            ScriptExecutor.activeScripts.forEach { script ->
                try {
                    CommandRenderer.withCapture(target) { script.render() }
                } catch (t: Throwable) {
                    println("Error in script render ${script.javaClass.simpleName}: ${t.message}")
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        if (target.isNotEmpty()) frames.publishReady(target)
    }
}
