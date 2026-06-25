package com.undercut.script

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.undercut.script.scheduler.SchedulerConfigurable
import java.lang.reflect.Field

/**
 * Helper to apply a raw JSON object onto a script's ConfigItem fields.
 *
 * - Primary key: the field name of the ConfigItem on the script
 * - Fallback key: the ConfigItem.name value (only when unique among items)
 * - Supported coercions:
 *   - BooleanConfigItem: boolean or string "true"/"false" (case-insensitive)
 *   - IntConfigItem: number or numeric string; clamped to min..max
 *   - StringConfigItem: string (other types ignored)
 *   - OptionsConfigItem: string value matched to option.toString() (case-insensitive) or number treated as index
 *   - EnumConfigItem: string mapped to enum name (case-insensitive)
 * - Unknown keys/types are ignored with non-fatal warnings aggregated into the Validation message
 * - No UI dependencies; does not write back to ScriptConfigStore
 */
object ScriptConfigJson {

    fun applyTo(script: Any, config: JsonObject): SchedulerConfigurable.Validation {
        val warnings = mutableListOf<String>()
        var fatal = false

        try {
            // Collect eligible ConfigItem fields (exclude InfoDisplayConfigItem)
            val fieldInfos = mutableListOf<FieldInfo>()
            for (field in script.javaClass.declaredFields) {
                try {
                    field.isAccessible = true
                    val v = field.get(script)
                    if (v is ConfigItem<*> && v !is InfoDisplayConfigItem) {
                        fieldInfos += FieldInfo(field.name, field, v)
                    }
                } catch (e: Throwable) {
                    warnings += "Cannot inspect field '${field.name}': ${e.message ?: e.javaClass.simpleName}"
                }
            }

            // Maps for lookups
            val byFieldName = fieldInfos.associateBy { it.fieldName }
            val byDisplayName = fieldInfos.groupBy { it.item.name }
            val byDisplayNameCI = fieldInfos.groupBy { it.item.name.lowercase() }
            val uniqueByDisplayName = byDisplayName.filterValues { it.size == 1 }.mapValues { it.value.first() }
            val uniqueByDisplayNameCI = byDisplayNameCI.filterValues { it.size == 1 }.mapValues { it.value.first() }

            // Iterate JSON entries and attempt to apply
            for ((rawKey, rawElement) in config.entrySet()) {
                val key = rawKey.trim()
                val target = byFieldName[key]
                    ?: uniqueByDisplayName[key]
                    ?: uniqueByDisplayNameCI[key.lowercase()]

                if (target == null) {
                    // Unknown key: non-fatal
                    warnings += "No matching config item for key '$key'"
                    continue
                }

                val applied = tryApply(target, rawElement, warnings)
                if (!applied) {
                    // Non-fatal warning already recorded by tryApply
                    continue
                }
            }
        } catch (t: Throwable) {
            fatal = true
            warnings += "Unexpected error while applying configuration: ${t.message ?: t.javaClass.simpleName}"
        }

        val message = if (warnings.isEmpty()) null else warnings.joinToString("; ")
        return SchedulerConfigurable.Validation(ok = !fatal, message = message)
    }

    // --- Internals ---

    private data class FieldInfo(
        val fieldName: String,
        val field: Field,
        val item: ConfigItem<*>
    )

    private fun tryApply(info: FieldInfo, element: JsonElement, warnings: MutableList<String>): Boolean {
        if (element is JsonNull) {
            warnings += "Key '${info.fieldName}' is null; ignoring"
            return false
        }

        val item = info.item
        return try {
            when (item) {
                is BooleanConfigItem -> applyBoolean(item, element, info, warnings)
                is IntConfigItem -> applyInt(item, element, info, warnings)
                is StringConfigItem -> applyString(item, element, info, warnings)
                is OptionsConfigItem<*> -> applyOptions(item, element, info, warnings)
                is EnumConfigItem<*> -> applyEnum(item, element, info, warnings)
                else -> {
                    warnings += "Unsupported ConfigItem type for '${info.fieldName}'"
                    false
                }
            }
        } catch (t: Throwable) {
            warnings += "Failed to apply '${info.fieldName}': ${t.message ?: t.javaClass.simpleName}"
            false
        }
    }

    private fun applyBoolean(
        item: BooleanConfigItem,
        element: JsonElement,
        info: FieldInfo,
        warnings: MutableList<String>
    ): Boolean {
        val prim = element.asPrimitiveOrNull()
        val value = prim?.asBooleanFlexible()
        return if (value != null) {
            item.value = value
            true
        } else {
            warnings += "Key '${info.fieldName}': expected boolean or 'true'/'false'"
            false
        }
    }

    private fun applyInt(
        item: IntConfigItem,
        element: JsonElement,
        info: FieldInfo,
        warnings: MutableList<String>
    ): Boolean {
        val prim = element.asPrimitiveOrNull()
        val intVal = prim?.asIntFlexible()
        return if (intVal != null) {
            val clamped = intVal.coerceIn(item.min, item.max)
            if (clamped != intVal) {
                warnings += "Key '${info.fieldName}': value $intVal clamped to [$clamped]"
            }
            item.value = clamped
            true
        } else {
            warnings += "Key '${info.fieldName}': expected integer"
            false
        }
    }

    private fun applyString(
        item: StringConfigItem,
        element: JsonElement,
        info: FieldInfo,
        warnings: MutableList<String>
    ): Boolean {
        val prim = element.asPrimitiveOrNull()
        val str = if (prim?.isString == true) prim.asString else null
        return if (str != null) {
            item.value = str
            true
        } else {
            warnings += "Key '${info.fieldName}': expected string"
            false
        }
    }

    private fun applyOptions(
        rawItem: OptionsConfigItem<*>,
        element: JsonElement,
        info: FieldInfo,
        warnings: MutableList<String>
    ): Boolean {
        val prim = element.asPrimitiveOrNull()
        val options = rawItem.options

        // String match by option.toString() (case-insensitive)
        if (prim?.isString == true) {
            val s = prim.asString
            val idx = options.indexOfFirst { it?.toString()?.equals(s, ignoreCase = true) == true }
            if (idx >= 0) {
                @Suppress("UNCHECKED_CAST")
                (rawItem as OptionsConfigItem<Any?>).value = options[idx]
                return true
            }
            warnings += "Key '${info.fieldName}': no option matches '$s'"
            return false
        }

        // Numeric index
        val index = prim?.asIntFlexible()
        if (index != null) {
            if (index in options.indices) {
                @Suppress("UNCHECKED_CAST")
                (rawItem as OptionsConfigItem<Any?>).value = options[index]
                return true
            }
            warnings += "Key '${info.fieldName}': index $index out of range [0..${options.lastIndex}]"
            return false
        }

        warnings += "Key '${info.fieldName}': expected string (option) or integer (index)"
        return false
    }

    private fun applyEnum(
        rawItem: EnumConfigItem<*>,
        element: JsonElement,
        info: FieldInfo,
        warnings: MutableList<String>
    ): Boolean {
        val prim = element.asPrimitiveOrNull()
        if (prim?.isString == true) {
            val s = prim.asString
            val values = rawItem.enumValues
            val match = values.firstOrNull { it.name.equals(s, ignoreCase = true) }
            if (match != null) {
                // Avoid generic bounds issues (T : Enum<T>) by setting via reflection
                return try {
                    val valueField = rawItem.javaClass.getDeclaredField("value")
                    valueField.isAccessible = true
                    valueField.set(rawItem, match)
                    true
                } catch (e: Throwable) {
                    warnings += "Key '${info.fieldName}': failed to set enum value: ${e.message ?: e.javaClass.simpleName}"
                    false
                }
            }
            warnings += "Key '${info.fieldName}': unknown enum '$s'"
            return false
        }
        warnings += "Key '${info.fieldName}': expected string (enum name)"
        return false
    }

    // --- Json helpers ---

    private fun JsonElement.asPrimitiveOrNull(): JsonPrimitive? = try {
        if (this is JsonPrimitive) this else null
    } catch (_: Throwable) { null }

    private fun JsonPrimitive.asBooleanFlexible(): Boolean? = try {
        when {
            isBoolean -> asBoolean
            isString -> {
                val s = asString.trim().lowercase()
                when (s) {
                    "true" -> true
                    "false" -> false
                    else -> null
                }
            }
            else -> null
        }
    } catch (_: Throwable) { null }

    private fun JsonPrimitive.asIntFlexible(): Int? = try {
        when {
            isNumber -> asNumber.toInt()
            isString -> asString.toIntOrNull()
            else -> null
        }
    } catch (_: Throwable) { null }
}
