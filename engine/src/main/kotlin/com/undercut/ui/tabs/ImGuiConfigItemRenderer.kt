package com.undercut.ui.tabs

import com.undercut.script.*
import com.undercut.ui.backend.dsl.scopes.*

fun WindowScope.renderConfigItem(
	fieldName: String,
	configItem: ConfigItem<*>,
	onChanged: () -> Unit
) {
	spacing()
	when (configItem) {
		is BooleanConfigItem -> {
			checkbox(configItem.name, configItem.value) { newVal ->
				configItem.value = newVal
				onChanged()
			}
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		is IntConfigItem -> {
			text(configItem.name + ":")
			sameLine()
			group {
				inputInt("##$fieldName", configItem.value) { newVal ->
					if (newVal != configItem.value && newVal >= configItem.min && newVal <= configItem.max) {
						configItem.value = newVal
						onChanged()
					}
				}
			}
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		is StringConfigItem -> {
			text(configItem.name + ":")
			inputText("##$fieldName", configItem.value, maxLength = 256) { newVal ->
				if (newVal != configItem.value) {
					configItem.value = newVal
					onChanged()
				}
			}
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		is InfoDisplayConfigItem -> {
			text("${configItem.name}: ")
			sameLine()
			text(configItem.value)
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		is OptionsConfigItem<*> -> {
			text(configItem.name + ":")
			sameLine()
			val currentValue = configItem.value?.toString() ?: "None"
			combo("##$fieldName", currentValue) {
				configItem.options.forEach { option ->
					val isSelected = (option == configItem.value)
					selectable(option?.toString() ?: "null", isSelected) {
						@Suppress("UNCHECKED_CAST")
						(configItem as ConfigItem<Any?>).value = option
						onChanged()
					}
					if (isSelected) setItemDefaultFocus()
				}
			}
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		is EnumConfigItem<*> -> {
			text(configItem.name + ":")
			sameLine()
			val currentValue = configItem.value?.toString() ?: "None"
			combo("##$fieldName", currentValue) {
				configItem.enumValues.forEach { enumValue ->
					val isSelected = (enumValue == configItem.value)
					selectable(enumValue.toString(), isSelected) {
						@Suppress("UNCHECKED_CAST")
						(configItem as ConfigItem<Any?>).value = enumValue
						onChanged()
					}
					if (isSelected) setItemDefaultFocus()
				}
			}
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}

		else -> {
			text("${configItem.name}: ${configItem.value}")
			if (configItem.description.isNotEmpty()) textWrapped(configItem.description)
		}
	}
}


