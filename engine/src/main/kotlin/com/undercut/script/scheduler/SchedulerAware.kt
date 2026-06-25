package com.undercut.script.scheduler

/**
 * Optional contract for scripts that want a reference to the scheduler handle
 * when they are being run by the scheduler.
 */
interface SchedulerAware {
    /**
     * Called by the scheduler prior to or at start-up of the child script to provide
     * a handle for requesting skip/removal.
     *
     * Most scripts can simply call `Script.requestSchedulerRemoval(...)` without keeping
     * a reference, but implementing this method allows advanced integrations.
     */
    fun setSchedulerHandle(handle: SchedulerHandle)
}
