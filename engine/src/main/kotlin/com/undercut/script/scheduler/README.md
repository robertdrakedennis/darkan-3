# Script Scheduling – Developer Guide

This guide explains how to make your script schedulable, request a skip/removal at runtime, and (optionally) accept per-entry configuration provided by the Scheduler UI.

## Overview

- The scheduler runs one child script at a time based on a queue of `ScheduleItem`s.
- Scripts must opt-in by implementing `SchedulableScript`.
- A running script can request to be skipped/removed by reporting a `RemovalCode`.
- Each schedule item may include a per-entry JSON `configuration` overlay applied before the child script starts.

Key types (packages in `src/main/kotlin/`):
- `com.undercut.script.scheduler.SchedulableScript`
- `com.undercut.script.scheduler.SchedulerAware` and `com.undercut.script.scheduler.SchedulerHandle`
- `com.undercut.script.scheduler.SchedulerRuntime`
- `com.undercut.script.scheduler.SchedulerScript`
- `com.undercut.script.scheduler.ScheduleItem`
- `com.undercut.script.scheduler.StopCondition`
- `com.undercut.script.scheduler.RemovalCode`, `RemovalReason`
- `com.undercut.script.scheduler.SchedulesStore`
- `com.undercut.script.ScriptConfigJson` and `com.undercut.script.scheduler.SchedulerConfigurable`

## Quick start: make a script schedulable

Implement `SchedulableScript` on your script class. Nothing else is required for basic scheduling.

```kotlin
@ScriptDescription(name = "My Woodcutter", version = "1.0", author = "me", description = "...")
class Woodcutter : Script(), SchedulableScript {
    override suspend fun loop() {
        // ... your normal script logic ...
    }
}
```

Your script will now appear in the Scheduler tab’s script list.

## Requesting skip/removal during runtime

Most scripts should call the convenience helper on `Script`:

```kotlin
if (!hasRequiredItems()) {
    requestSchedulerRemoval(
        com.undercut.script.scheduler.RemovalCode.MISSING_REQUIREMENTS,
        "Missing axe"
    )
}
```

Advanced: If you prefer an explicit handle, implement `SchedulerAware`:

```kotlin
class ExampleScript : Script(), SchedulableScript, SchedulerAware {
    private var handle: SchedulerHandle? = null
    override fun setSchedulerHandle(handle: SchedulerHandle) { this.handle = handle }

    override suspend fun loop() {
        if (!hasRequiredItems()) {
            handle?.requestRemoval(RemovalCode.MISSING_REQUIREMENTS, "Missing axe")
        }
    }
}
```

`RemovalCode` options include `MISSING_REQUIREMENTS`, `UNHANDLED_EXCEPTION`, `TIME_REACHED`, `LEVEL_REACHED`, and `USER_REQUEST`.

## Per-entry configuration (optional)

The Scheduler UI lets users attach a JSON object to each queue item. This overlay is applied to the script instance before it starts.

You have two options:

1) Implement `SchedulerConfigurable` for full control and validation:

```kotlin
class MyScript : Script(), SchedulableScript, SchedulerConfigurable {
    override fun applyScheduleConfiguration(config: JsonObject): SchedulerConfigurable.Validation {
        // Validate and apply to in-memory fields
        val ok = tryApplyConfig(config)
        return if (ok) SchedulerConfigurable.Validation(true) else SchedulerConfigurable.Validation(false, "Invalid config")
    }
}
```

2) Use the generic mapping via `ScriptConfigJson` (no extra code):
- Matches JSON keys to your `ConfigItem` fields.
- Supports `BooleanConfigItem`, `IntConfigItem` (clamped), `StringConfigItem`, `OptionsConfigItem`, and `EnumConfigItem`.
- Unknown keys/types are ignored with warnings aggregated into the returned message.

Example JSON overlay:

```json
{
  "enableBanking": true,
  "dropThreshold": 24,
  "mode": "EFFICIENCY"
}
```

If validation fails or mapping is invalid, the entry is skipped with `MISSING_REQUIREMENTS` and the scheduler advances to the next item.

## Using the Scheduler UI

1. Open the "Scheduler" tab.
2. Pick a script that implements `SchedulableScript`.
3. Choose a stop condition:
   - Time-based: run for N minutes.
   - Level-based: stop when a `Skill` reaches the target level.
4. Click "Edit Config" to open the Entry Config Editor:
   - Baseline: Defaults or Global Defaults (applies `ScriptConfigStore` to a fresh instance).
   - Mode: Delta (only changes vs baseline) or Full (all fields).
   - Edit fields via the live form; changes are session-only and do not persist to `ScriptConfigStore`.
   - Preview JSON shows the overlay; Save writes pretty JSON back to the item.
5. Add multiple items; reorder as needed.
6. Click "Start Scheduler". Use "Paused" to pause/resume, and "Stop Scheduler" to stop.
7. Use the "Presets" section to save/load queues via `SchedulesStore`.

Tip: To edit raw JSON directly, enable the "Advanced JSON Editor" toggle. UI overlays and raw JSON are fully compatible: both are applied using `ScriptConfigJson`. Enums map by `name`, options by `toString()`, and booleans/ints/strings map directly. Delta mode stores a minimal overlay; Full stores a complete snapshot. Existing presets with raw JSON continue to work.

## Programmatic usage (optional)

You can also construct and run a `SchedulerScript` yourself:

```kotlin
val items = listOf(
    ScheduleItem(
        id = "wood-1",
        scriptClass = my.script.Woodcutter::class.java.name,
        stop = StopCondition.TimeBased(10 * 60 * 1000L) // 10 minutes
    )
)
val controller = SchedulerScript()
controller.setSchedule(items)
ScriptExecutor.activate(controller)
```

## Notes & best practices

- Do not block in `applyScheduleConfiguration`; keep it fast and in-memory.
- Use `requestSchedulerRemoval(...)` for quick, no-throw removal signaling.
- The scheduler enforces stop conditions and advances the queue automatically.
- `SchedulerRuntime.active` is provided for convenience; do not hold onto it long-term.
- Level-based stops depend on the live game state; null/unavailable levels will keep running until state is available.

## References

- `com/undercut/script/SchedulableScript.kt`
- `com/undercut/script/SchedulerAware.kt`, `com/undercut/script/SchedulerHandle.kt`, `com/undercut/script/SchedulerRuntime.kt`
- `com/undercut/script/SchedulerScript.kt`
- `com/undercut/script/scheduler/StopCondition.kt`, `RemovalCode.kt`, `RemovalReason.kt`, `ScheduleItem.kt`
- `com/undercut/script/scheduler/SchedulesStore.kt`
- `com/undercut/script/ScriptConfigJson.kt`, `com/undercut/script/SchedulerConfigurable.kt`
