package com.undercut.script.scheduler

import com.google.gson.JsonObject

/**
 * A single scheduled entry describing which script to run and when to stop it.
 * Script class is referenced by its fully qualified class name to simplify
 * persistence and reflection-based instantiation.
 *
 * The optional [configuration] field allows a per-entry JSON overlay to be applied
 * to the child script before it starts. If the script implements
 * `SchedulerConfigurable`, that interface is used for validation/application; otherwise
 * a best-effort mapping via `ScriptConfigJson` is attempted. Invalid configs cause the
 * entry to be skipped with a `MISSING_REQUIREMENTS` reason.
 */
data class ScheduleItem(
    val id: String,
    val scriptClass: String,
    val stop: StopCondition,
    val configuration: JsonObject? = null
)
