package com.undercut.script.scheduler

import com.google.gson.JsonObject

/**
 * Optional contract for scripts that can accept and validate a per-entry
 * schedule configuration payload provided by the Scheduler.
 *
 * This interface is intentionally minimal and has no UI dependencies.
 * It is separate from [com.undercut.script.ConfigurableScript], which is used by the ScriptsTab UI
 * for global/default configuration management.
 */
interface SchedulerConfigurable {
    /**
     * Result of applying a per-entry configuration to a script instance.
     * When [ok] is false, [message] should describe the reason.
     */
    data class Validation(val ok: Boolean, val message: String? = null)

    /**
     * Apply and validate a per-entry schedule configuration. Implementations
     * should not perform any long-running work here and must be side-effect
     * safe beyond updating in-memory configuration on the script instance.
     */
    fun applyScheduleConfiguration(config: JsonObject): Validation
}
