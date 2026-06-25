package com.undercut.util

import com.google.gson.reflect.TypeToken
import com.undercut.game.hooks.impl.SDLKeycode
import java.io.File
import java.io.IOException

data class PersistentConfig(
    val discordWebhookUrl: String = "",
    val discordEnabled: Boolean = true,
    val systemTrayEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val discordUsername: String = "Undercut",
    val discordAvatarUrl: String = "",
    val uiToggleKey: Int = SDLKeycode.F12.value,
    val favoriteScripts: Set<String> = emptySet()
)

object Configuration {
    private val configDir = File(System.getProperty("user.home"), ".undercut")
    private val configFile = File(configDir, "config.json")
    private var _config: PersistentConfig? = null

    val config: PersistentConfig
        get() {
            if (_config == null)
                loadConfig()
            return _config!!
        }

    private fun loadConfig() {
        try {
            if (configFile.exists()) {
                val type = object : TypeToken<PersistentConfig>() {}.type
                _config = JsonFileManager.loadJsonFile(configFile, type)
            } else {
                _config = PersistentConfig()
                saveConfig()
            }
        } catch (e: IOException) {
            println("Failed to load configuration: ${e.message}")
            _config = PersistentConfig()
        }
    }

    private fun saveConfig() {
        try {
            JsonFileManager.saveJsonFile(_config!!, configFile)
        } catch (e: IOException) {
            println("Failed to save configuration: ${e.message}")
        }
    }

    fun updateConfig(newConfig: PersistentConfig) {
        _config = newConfig
        saveConfig()
    }

    fun saveFavoriteScripts(favoriteScriptNames: Set<String>) {
        _config = config.copy(favoriteScripts = favoriteScriptNames)
        saveConfig()
    }

    fun reloadConfig() {
        _config = null
        loadConfig()
    }
}
