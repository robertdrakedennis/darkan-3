package com.undercut.script.scheduler

import com.undercut.game.interfaces.IFSlot
import com.undercut.script.ConfigurableScript
import com.undercut.script.Script
import com.undercut.script.ScriptConfigJson
import com.undercut.script.ScriptConfigStore
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.getCurrentLevelOrNull
import com.undercut.script.api.interfaces

/**
 * Sequential scheduler controller.
 *
 * - Maintains an in-memory queue of ScheduleItem
 * - Starts one primary child at a time using addParallelScript
 * - Enforces StopCondition (time/level)
 * - Handles script-driven removal via SchedulerHandle
 * - Idempotent removal and minimal logging
 * - No UI/IO dependencies
 */
open class SchedulerScript : Script(), SchedulerHandle {

    private val queue = ArrayDeque<ScheduleItem>()

    @Volatile
    private var currentItem: ScheduleItem? = null

    @Volatile
    private var child: Script? = null

    @Volatile
    private var childStartTimeMs: Long = 0L

    @Volatile
    private var pendingRemoval: RemovalReason? = null

    @Volatile
    private var startingChild = false

    /**
     * Replace the current queue with [items].
     *
     * Thread-safety: synchronized. Does not start execution by itself; the scheduler
     * will pick up the first item on the next loop tick when idle.
     */
    @Synchronized
    fun setSchedule(items: List<ScheduleItem>) {
        queue.clear()
        queue.addAll(items)
    }

    /**
     * Append [item] to the end of the queue.
     *
     * Thread-safety: synchronized. Has no immediate effect on the currently running child.
     */
    @Synchronized
    fun enqueue(item: ScheduleItem) {
        queue.addLast(item)
    }

    /**
     * True when there is no active child, no current item, and the queue is empty.
     */
    fun isIdle(): Boolean = child == null && currentItem == null && queue.isEmpty()

    override fun onStart() {
        println("${this.javaClass.simpleName} started.")
        SchedulerRuntime.active = this
    }

    override fun onStop() {
        try {
            val toRemove = child
            if (toRemove != null) {
                try {
                    removeParallelScript(toRemove)
                } catch (_: Throwable) {
                }
            }
        } finally {
            child = null
            currentItem = null
            pendingRemoval = null
            SchedulerRuntime.active = null
            println("${this.javaClass.simpleName} stopped.")
        }
    }

    @Volatile
    private var paused: Boolean = false

    @Volatile
    private var pauseStartedAtMs: Long = 0L

    @Volatile
    private var accumulatedPausedMs: Long = 0L

    /**
     * Pause or resume scheduler advancement. When paused, the current child continues
     * running but stop conditions are not evaluated or enforced until resumed.
     */
    fun setPaused(p: Boolean) {
        if (p == paused) return

        if (p) {
            paused = true
            pauseStartedAtMs = System.currentTimeMillis()
        } else {
            val startedAt = pauseStartedAtMs
            if (startedAt != 0L) {
                accumulatedPausedMs += (System.currentTimeMillis() - startedAt).coerceAtLeast(0L)
            }
            pauseStartedAtMs = 0L
            paused = false
        }
    }

    /** Returns whether the scheduler is currently paused. */
    fun isSchedulerPaused(): Boolean = paused

    /**
     * Main scheduler loop. Starts the next item when idle and checks stop conditions
     * for the current child. Never block inside this method.
     */
    override suspend fun loop() {
        try {
            
            // Keep runtime pointer fresh
            SchedulerRuntime.active = this

            if (paused) return

            if (child == null && !startingChild) {
                tryStartNext()
            } else {
                checkStopConditionsAndAdvanceIfNeeded()
            }

            if (queue.isEmpty() && child == null) {
                if (!interfaces.isOpen(1433)) {
                    IFSlot(1431, 0, 7).click()
                    delayUntil { interfaces.isOpen(1433) }
                    return
                }
                IFSlot(1433,68,-1).click()
                delay(2000)
                return
            }

        } catch (t: Throwable) {
            val reason = RemovalReason(RemovalCode.UNHANDLED_EXCEPTION, t.message)
            performRemovalAndAdvance(reason)
        }
    }

    private fun tryStartNext() {
        if (queue.isEmpty()) return

        startingChild = true
        try {
            currentItem = queue.removeFirst()
            val item = currentItem!!

            val instance = instantiateScript(item.scriptClass)
            if (instance == null) {
                performRemovalAndAdvance(
                    RemovalReason(
                        RemovalCode.MISSING_REQUIREMENTS,
                        "Unable to instantiate ${item.scriptClass}"
                    )
                )
                return
            }

            if (instance is SchedulerAware) {
                instance.setSchedulerHandle(this)
            }

            // 1) Apply global/default configuration if supported (no persistence here)
            try {
                if (instance is ConfigurableScript) {
                    ScriptConfigStore.applyTo(instance)
                }
            } catch (_: Throwable) { /* Best-effort; proceed */
            }

            // 2) Apply per-entry configuration overlay if present
            val cfg = item.configuration
            if (cfg != null) {
                try {
                    val validation = if (instance is SchedulerConfigurable) {
                        instance.applyScheduleConfiguration(cfg)
                    } else {
                        ScriptConfigJson.applyTo(instance, cfg)
                    }
                    if (!validation.ok) {
                        val msg = validation.message ?: "Invalid configuration"
                        performRemovalAndAdvance(RemovalReason(RemovalCode.MISSING_REQUIREMENTS, msg))
                        return
                    }
                } catch (t: Throwable) {
                    val msg = t.message ?: "Invalid configuration"
                    performRemovalAndAdvance(RemovalReason(RemovalCode.MISSING_REQUIREMENTS, msg))
                    return
                }
            }

            // Start the child only after successful configuration application
            child = instance
            childStartTimeMs = System.currentTimeMillis()
            accumulatedPausedMs = 0L
            pauseStartedAtMs = 0L
            addParallelScript(instance)
        } catch (t: Throwable) {
            performRemovalAndAdvance(RemovalReason(RemovalCode.UNHANDLED_EXCEPTION, t.message))
        } finally {
            startingChild = false
        }
    }

    private fun checkStopConditionsAndAdvanceIfNeeded() {
        val c = child ?: return
        val item = currentItem ?: return

        // Script-driven removal has priority
        val req = pendingRemoval
        if (req != null) {
            performRemovalAndAdvance(req)
            return
        }

        val reason = when (val stop = item.stop) {
            is StopCondition.TimeBased -> {
                val elapsed = effectiveElapsedMs()
                if (elapsed >= stop.durationMs) RemovalReason(
                    RemovalCode.TIME_REACHED,
                    "Elapsed ${elapsed}ms"
                ) else null
            }

            is StopCondition.LevelBased -> {
                val level = safelyGetCurrentLevel(stop)
                if (level != null && level >= stop.targetLevel) RemovalReason(
                    RemovalCode.LEVEL_REACHED,
                    "${stop.skill} >= ${stop.targetLevel}"
                ) else null
            }
        }

        if (reason != null) {
            performRemovalAndAdvance(reason)
        }
    }

    private fun safelyGetCurrentLevel(stop: StopCondition.LevelBased): Int? = getCurrentLevelOrNull(stop.skill)

    private fun performRemovalAndAdvance(reason: RemovalReason) {
        val toRemove = child ?: run {
            // Already removed; reset and try start next if present
            currentItem = null
            pendingRemoval = null
            if (queue.isNotEmpty()) tryStartNext()
            return
        }

        try {
            println("Scheduler: removing ${toRemove.javaClass.simpleName} due to ${reason.code}${reason.message?.let { ": $it" } ?: ""}")
        } catch (_: Throwable) {
        }

        try {
            removeParallelScript(toRemove)
        } catch (_: Throwable) {
        } finally {
            child = null
            currentItem = null
            pendingRemoval = null
            accumulatedPausedMs = 0L
            pauseStartedAtMs = if (paused) System.currentTimeMillis() else 0L
        }

        tryStartNext()
    }

    /**
     * Non-blocking request from a child script to remove the current item.
     * Idempotent: subsequent calls simply overwrite the pending reason until processed.
     */
    override fun requestRemoval(code: RemovalCode, message: String?) {
        // Non-blocking and idempotent
        pendingRemoval = RemovalReason(code, message)
    }

    // ---- Status helpers (no UI dependencies) ----
    /** Returns the currently running [ScheduleItem], or null if idle. */
    fun current(): ScheduleItem? = currentItem

    /** Returns milliseconds elapsed since the current child started, or 0 if idle. */
    fun elapsedMs(): Long = if (child != null) effectiveElapsedMs() else 0L

    /** Returns the number of items remaining in the queue. */
    fun queuedCount(): Int = queue.size

    private fun instantiateScript(className: String): Script? {
        // 1) Prefer ScriptExecutor metadata
        try {
            val meta = ScriptExecutor.scripts.values.firstOrNull { it.scriptClass.name == className }
            if (meta != null) {
                return meta.scriptClass.getDeclaredConstructor().newInstance()
            }
        } catch (_: Throwable) {
        }

        // 2) Fallback: reflection by name
        return try {
            val clazz = Class.forName(className)
            if (Script::class.java.isAssignableFrom(clazz)) {
                @Suppress("UNCHECKED_CAST")
                val sClass = clazz as Class<out Script>
                sClass.getDeclaredConstructor().newInstance()
            } else null
        } catch (_: Throwable) {
            null
        }
    }

    private fun effectiveElapsedMs(): Long {
        val start = childStartTimeMs
        if (start == 0L) return 0L
        val now = System.currentTimeMillis()
        val pausedContribution = accumulatedPausedMs + if (paused && pauseStartedAtMs != 0L) {
            (now - pauseStartedAtMs).coerceAtLeast(0L)
        } else 0L
        val elapsed = now - start - pausedContribution
        return if (elapsed < 0L) 0L else elapsed
    }
}
