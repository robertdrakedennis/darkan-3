package com.undercut.script

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 * Utilities to create snapshots and overlays (delta or full) of a script's ConfigItem fields.
 *
 * - No UI dependencies
 * - Side-effect free except mutating the provided in-memory script instance
 * - Does NOT write to ScriptConfigStore
 */
object ConfigOverlaySession {

    /** Baseline source for comparisons */
    enum class Baseline { Defaults, GlobalDefaults }

    /** Overlay mode */
    enum class Mode { Full, Delta }

    /**
     * Create a fresh instance of the given ConfigurableScript class and apply the chosen baseline.
     * Defaults -> brand new instance; GlobalDefaults -> new + ScriptConfigStore.applyTo(instance)
     */
    fun <T : ConfigurableScript> createScriptInstance(
        clazz: Class<T>,
        baseline: Baseline
    ): Result<T> {
        return runCatching {
            val ctor = clazz.getDeclaredConstructor()
            ctor.isAccessible = true
            val instance = ctor.newInstance()
            if (baseline == Baseline.GlobalDefaults) {
                // Apply previously saved global/default values to the fresh instance
                ScriptConfigStore.applyTo(instance)
            }
            instance
        }
    }

    /**
     * Take a full snapshot of the script's ConfigItem fields into a JsonObject that is compatible with
     * ScriptConfigJson.applyTo mapping rules.
     */
    fun snapshot(script: Any): JsonObject {
        val result = JsonObject()

        val clazz = script.javaClass
        for (field in clazz.declaredFields) {
            try {
                field.isAccessible = true
                val v = field.get(script)
                if (v is ConfigItem<*> && v !is InfoDisplayConfigItem) {
                    val key = field.name
                    when (v) {
                        is BooleanConfigItem -> result.add(key, JsonPrimitive(v.value))
                        is IntConfigItem -> result.add(key, JsonPrimitive(v.value))
                        is StringConfigItem -> result.add(key, JsonPrimitive(v.value))
                        is OptionsConfigItem<*> -> {
                            val str = v.value?.toString()
                            if (str != null) result.add(key, JsonPrimitive(str))
                        }
                        is EnumConfigItem<*> -> {
                            val enumVal = (v.value as? Enum<*>)?.name
                            if (enumVal != null) result.add(key, JsonPrimitive(enumVal))
                        }
                        else -> {
                            // Unknown ConfigItem type - ignore silently to keep compatibility
                        }
                    }
                }
            } catch (_: Throwable) {
                // Ignore field-level issues; snapshot best-effort
            }
        }

        return result
    }

    /**
     * Compute an overlay for [script] relative to a baseline derived from [baseline].
     * - Mode.Full: returns full snapshot
     * - Mode.Delta: returns only keys whose values differ from the baseline snapshot
     */
    fun overlay(script: Any, baseline: Baseline, mode: Mode): JsonObject {
        return when (mode) {
            Mode.Full -> snapshot(script)
            Mode.Delta -> {
                val baseSnapshot = createBaselineSnapshot(script, baseline)
                val current = snapshot(script)
                diff(baseSnapshot, current)
            }
        }
    }

    /** Build a baseline snapshot using a fresh instance of the same class as [script]. */
    private fun createBaselineSnapshot(script: Any, baseline: Baseline): JsonObject {
        val clazz = script.javaClass.asSubclass(ConfigurableScript::class.java)
        val base = createScriptInstance(clazz, baseline).getOrNull() ?: return JsonObject()
        return snapshot(base)
    }

    /**
     * Return only entries that differ between [base] and [current].
     * Keys present in [current] and absent in [base] are included.
     */
    fun diff(base: JsonObject, current: JsonObject): JsonObject {
        val out = JsonObject()
        for ((k, v) in current.entrySet()) {
            val baseV: JsonElement? = base.get(k)
            if (!jsonEquals(baseV, v)) {
                out.add(k, v)
            }
        }
        return out
    }

    private fun jsonEquals(a: JsonElement?, b: JsonElement?): Boolean {
        // Treat both null as equal
        if (a == null && b == null) return true
        // One null, one not
        if (a == null || b == null) return false
        // Delegate to JsonElement equality (covers primitives)
        return a == b
    }
}
