package com.undercut.ui.tabs

import com.undercut.script.*
import com.undercut.ui.backend.dsl.scopes.WindowScope
import com.undercut.ui.backend.dsl.scopes.text

object ScriptConfigRenderer {
    enum class PersistenceMode { Persist, SessionOnly }

    fun WindowScope.renderScriptConfig(script: ConfigurableScript) {
        renderScriptConfig(script, PersistenceMode.Persist)
    }

    fun WindowScope.renderScriptConfig(script: ConfigurableScript, mode: PersistenceMode) {

        try {
            val clazz = script::class.java
            val configFields = clazz.declaredFields
                .filter { ConfigItem::class.java.isAssignableFrom(it.type) }

            if (configFields.isEmpty()) {
                text("No configuration options available for this script.")
                return
            }

            val visibilityProvider = script as? ConfigVisibilityProvider

            configFields.forEach { field ->
                field.isAccessible = true
                val configItem = field.get(script) as? ConfigItem<*>
                if (configItem != null) {
                    val shouldRender = visibilityProvider?.isConfigItemVisible(field.name, configItem) ?: true
                    if (!shouldRender) return@forEach

                    renderConfigItem(field.name, configItem) {
                        if (mode == PersistenceMode.Persist) {
                            ScriptConfigStore.save(script)
                        }
                        invokeOnConfigUpdated(script)
                    }
                }
            }

        } catch (e: Exception) {
            text("Error loading configuration: ${e.message}")
            e.printStackTrace()
        }
    }

    fun invokeOnConfigUpdated(script: Any) {
        try {
            val method = script.javaClass.getMethod("onConfigUpdated")
            method.invoke(script)
        } catch (_: NoSuchMethodException) {
            // Method is optional, ignore if not present
        } catch (e: Exception) {
            println("Error invoking onConfigUpdated: ${e.message}")
        }
    }

    fun resetScriptConfigToDefaults(script: Any) {
        try {
            val clazz = script::class.java
            val configFields = clazz.declaredFields
                .filter { ConfigItem::class.java.isAssignableFrom(it.type) }

            configFields.forEach { field ->
                field.isAccessible = true
                val configItem = field.get(script) as? ConfigItem<*>
                if (configItem != null) {
                    val constructor = configItem.javaClass.declaredConstructors.firstOrNull()
                    if (constructor != null) {
                        constructor.isAccessible = true

                        when (configItem) {
                            is BooleanConfigItem -> {
                                configItem.value = false
                            }

                            is IntConfigItem -> {
                                configItem.value = 0
                            }

                            is StringConfigItem -> {
                                configItem.value = ""
                            }

                            is OptionsConfigItem<*> -> {
                                val optionsField = configItem.javaClass.getDeclaredField("options")
                                optionsField.isAccessible = true
                                @Suppress("UNCHECKED_CAST")
                                val options = optionsField.get(configItem) as Array<*>
                                if (options.isNotEmpty()) {
                                    @Suppress("UNCHECKED_CAST")
                                    (configItem as ConfigItem<Any?>).value = options[0]
                                }
                            }

                            is EnumConfigItem<*> -> {
                                val enumValuesField = configItem.javaClass.getDeclaredField("enumValues")
                                enumValuesField.isAccessible = true
                                @Suppress("UNCHECKED_CAST")
                                val enumValues = enumValuesField.get(configItem) as Array<Enum<*>>
                                if (enumValues.isNotEmpty()) {
                                    @Suppress("UNCHECKED_CAST")
                                    (configItem as ConfigItem<Any?>).value = enumValues[0]
                                }
                            }
                        }
                    }
                }
            }

            ScriptConfigStore.save(script)
            invokeOnConfigUpdated(script)

        } catch (e: Exception) {
            println("Error resetting config to defaults: ${e.message}")
            e.printStackTrace()
        }
    }
}