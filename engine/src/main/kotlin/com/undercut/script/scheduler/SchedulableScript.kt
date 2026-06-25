package com.undercut.script.scheduler

/**
 * Marker interface for scripts that opt-in to being scheduled by the Scheduler.
 *
 * This interface is intentionally empty and is used only for identification
 * (e.g., via reflection) so that the scheduler UI and controller can present
 * and manage eligible scripts without changing their runtime behavior.
 *
 * Usage:
 * ```kotlin
 * @ScriptDescription(name = "My Woodcutter", version = "1.0", author = "me", description = "...")
 * class Woodcutter : Script(), SchedulableScript { // Opt-in by implementing marker
 *     override suspend fun loop() {
 *         // ... normal script logic ...
 *         // If requirements are not met, request skip/removal:
 *         requestSchedulerRemoval(
 *             com.undercut.script.scheduler.RemovalCode.MISSING_REQUIREMENTS,
 *             "Missing axe"
 *         )
 *     }
 * }
 * ```
 * Tip: If you need the low-level handle, implement `SchedulerAware` to receive it,
 * but for most cases `Script.requestSchedulerRemoval(...)` is simpler.
 */
interface SchedulableScript
