package com.undercut.game.hooks.impl

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.cs2.CS2Executor
import com.undercut.game.highlight.HighlightTick
import com.undercut.game.invention.InventionXpTracker
import com.undercut.quest.runtime.ConditionEvaluator
import com.undercut.quest.runtime.QuestHighlightTick
import com.undercut.quest.runtime.QuestInstanceTracker
import com.undercut.quest.runtime.StepAdvancer
import com.undercut.quest.overlay.QuestOverlayRenderer
import com.undercut.game.hooks.Hook
import com.undercut.game.hooks.HookManager
import com.undercut.game.hooks.Priority
import com.undercut.game.input.InputRecorder
import com.undercut.game.input.SyntheticInputShadow
import com.undercut.game.nxt.OFunctions
import com.undercut.mcp.MainLogicTickQueue
import com.undercut.puzzle.slide.SlidePuzzleFeature
import com.undercut.script.ScriptExecutor
import com.undercut.ui.UIState
import com.undercut.ui.backend.rendering.UiFrameProducer
import java.lang.foreign.MemorySegment

object ClientMainLogic {
    @JvmStatic
    @Hook(OFunctions.CLIENT_MAINLOGIC, priority = Priority.LAST)
    fun clientMainLogicHook(clientBaseAddr: MemorySegment, unkByte: Byte) {
        // During teardown become a pure passthrough (no engine logic, no lock) so a frame landing
        // mid-shutdown can't run against half-torn state. Checked before taking Bootstrap.lock,
        // which the teardown holds while quiescing.
        if (Bootstrap.stopping) {
            HookManager.trampoline(::clientMainLogicHook.name).invoke(clientBaseAddr, 1.toByte())
            return
        }
        synchronized(Bootstrap.lock) {
            try {
                MainLogicTickQueue.drain()
                ScriptExecutor.mainLogic()
                CS2Executor.mainLogicTick()
                InputRecorder.tickFlush()
                SyntheticInputShadow.tick()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            HookManager.trampoline(::clientMainLogicHook.name).invoke(clientBaseAddr, 1.toByte())
            // Run AFTER the engine's per-frame UpdateHighlight so our direct writes
            // to render_model+0x100..0x134 aren't immediately zeroed by its CATEGORY-path
            // fade-out branch.
            try {
                // Instance-origin tracking must run before any quest renderer / tick that
                // resolves instance-relative direction/path coordinates to world space.
                QuestInstanceTracker.tick()
                HighlightTick.tick(range = UIState.entityRange.value.coerceAtLeast(40))
                // After HighlightTick so the quest-target highlight (yellow) overrides
                // the generic clickbox highlight (cyan) on the same entity.
                QuestHighlightTick.tick()
                SlidePuzzleFeature.tick(UIState.slidePuzzleSolver.value)
                // Step advancer and dialog text snapshot run on the main-logic tick (NOT
                // the render thread) so the InterfaceList traversal happens while scene
                // mutation is paused. Reading dialog interface peer components from the
                // render thread races scene tear-down on instance transitions → SIGSEGV.
                StepAdvancer.maybeTick(UIState.questHelperAutoAdvance.value)
                ConditionEvaluator.tickSnapshot()
                // Dialog/inventory/component highlight matching reads live component text (EASTL
                // getString); must run here, not on the render thread, or it races the dialogue
                // rebuild → torn heap pointer → SIGSEGV (masked by the EGL shutdown core).
                QuestOverlayRenderer.tickInterfaceHighlights()
                // Sample augmented-item XP on the main-logic thread (owns inventory memory); the
                // Skills tab reads the published snapshot. Isolated so a stale read can't skip build().
                try {
                    InventionXpTracker.tick()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                // Build the next UI frame HERE, on the main-logic thread, after all tick state is
                // updated. Every render() can read live native state directly (no snapshotting) because
                // this thread owns that state and isn't mutating it concurrently. The render thread only
                // replays the published command buffer.
                UiFrameProducer.build()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}