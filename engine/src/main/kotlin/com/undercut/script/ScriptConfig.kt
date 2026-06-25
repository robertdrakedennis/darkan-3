package com.undercut.script

interface ConfigItem<T> {
    val name: String
    val description: String
    var value: T
}

interface ConfigurableScript

interface ConfigVisibilityProvider {
    fun isConfigItemVisible(fieldName: String, item: ConfigItem<*>): Boolean
}

class BooleanConfigItem(
    override val name: String,
    override val description: String,
    initialValue: Boolean = false
) : ConfigItem<Boolean> {
    override var value: Boolean = initialValue
}

class IntConfigItem(
    override val name: String,
    override val description: String,
    initialValue: Int = 0,
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE
) : ConfigItem<Int> {
    override var value: Int = initialValue
}

class StringConfigItem(
    override val name: String,
    override val description: String,
    initialValue: String = ""
) : ConfigItem<String> {
    override var value: String = initialValue
}

class OptionsConfigItem<T>(
    override val name: String,
    override val description: String,
    val options: Array<T>,
    initialValue: T
) : ConfigItem<T> {
    override var value: T = initialValue
}

class EnumConfigItem<T : Enum<T>>(
    override val name: String,
    override val description: String,
    val enumValues: Array<T>,
    initialValue: T
) : ConfigItem<T> {
    override var value: T = initialValue
}

class InfoDisplayConfigItem(
    override val name: String,
    override val description: String,
    initialValue: String = ""
) : ConfigItem<String> {
    override var value: String = initialValue
}

object ScriptConfigStore {
    private val configCache = mutableMapOf<String, Map<String, Any?>>()

    fun save(script: Any) {
        val values = mutableMapOf<String, Any?>()
        script.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val configItem = field.get(script)
            if (configItem is ConfigItem<*> && configItem !is InfoDisplayConfigItem) {
                values[field.name] = configItem.value
            }
        }
        configCache[script.javaClass.name] = values
    }

    fun applyTo(script: Any) {
        val saved = configCache[script.javaClass.name] ?: return
        script.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val configItem = field.get(script)
            if (configItem is ConfigItem<*> && configItem !is InfoDisplayConfigItem) {
                val savedValue = saved[field.name]
                if (savedValue != null) {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        (configItem as ConfigItem<Any?>).value = savedValue
                    } catch (e: Exception) {
                        println("❌ Failed to restore config value for ${field.name}: $e")
                    }
                }
            }
        }
    }
}

/* Examples
     // Boolean examples
    private val enableDebug = BooleanConfigItem(
        name = "Enable Debug",
        description = "Enables debug output",
        initialValue = true
    )

    private val showTimestamp = BooleanConfigItem(
        name = "Show Timestamp",
        description = "Shows timestamp in output",
        initialValue = false
    )

    // String examples
    private val debugPrefix = StringConfigItem(
        name = "Debug Prefix",
        description = "Prefix for debug messages",
        initialValue = "[DEBUG]"
    )

    private val filterText = StringConfigItem(
        name = "Filter Text",
        description = "Text to filter debug messages",
        initialValue = ""
    )

    // String options example
    private val debugModes = arrayOf("All", "Chat Only", "XP Only", "Varbit Only", "None")
    private val debugMode = OptionsConfigItem(
        name = "Debug Mode",
        description = "What type of debug info to show",
        options = debugModes,
        initialValue = "All"
    )

    // Int options example
    private val radiusOptions = arrayOf(5, 10, 15, 20, 25, 30)
    private val debugRadius = OptionsConfigItem(
        name = "Debug Radius",
        description = "Radius for debug area",
        options = radiusOptions,
        initialValue = 10
    )

    // More examples with common game values
    private val skillLevels = (1..99).toList().toTypedArray()
    private val minLevel = OptionsConfigItem(
        name = "Minimum Level",
        description = "Minimum level to start at",
        options = skillLevels,
        initialValue = 1
    )

    private val intervals = arrayOf(100, 500, 1000, 2000, 5000)
    private val updateInterval = OptionsConfigItem(
        name = "Update Interval",
        description = "How often to update (ms)",
        options = intervals,
        initialValue = 1000
    )
 */