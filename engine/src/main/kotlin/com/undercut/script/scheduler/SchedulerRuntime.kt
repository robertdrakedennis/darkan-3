package com.undercut.script.scheduler

/**
 * Global runtime hook for the active scheduler instance.
 * This is intentionally minimal and free of UI dependencies.
 */
object SchedulerRuntime {
    @Volatile
    var active: SchedulerScript? = null
}
