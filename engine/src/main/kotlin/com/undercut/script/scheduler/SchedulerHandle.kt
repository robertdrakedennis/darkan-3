package com.undercut.script.scheduler

/**
 * Handle provided by the scheduler to a running script, allowing the script
 * to request its own removal from the schedule with a structured reason.
 *
 * Example (using `SchedulerAware` to receive the handle):
 * ```kotlin
 * class ExampleScript : Script(), SchedulableScript, SchedulerAware {
 *     private var handle: SchedulerHandle? = null
 *     override fun setSchedulerHandle(handle: SchedulerHandle) { this.handle = handle }
 *     override suspend fun loop() {
 *         if (/* missing requirements */ false) {
 *             handle?.requestRemoval(RemovalCode.MISSING_REQUIREMENTS, "Missing setup")
 *         }
 *     }
 * }
 * ```
 * Tip: Most scripts can call `requestSchedulerRemoval(...)` directly on `Script` and
 * do not need to implement `SchedulerAware` unless they prefer an explicit handle.
 */
interface SchedulerHandle {
    /**
     * Request removal from the active schedule.
     * Thread-safe: implementations must be safe to call from the script thread.
     */
    fun requestRemoval(code: RemovalCode, message: String? = null)
}
