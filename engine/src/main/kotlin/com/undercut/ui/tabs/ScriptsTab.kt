package com.undercut.ui.tabs

import com.undercut.script.*
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.boolState
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.ImGuiChildFlags
import com.undercut.ui.backend.native.ImGuiTexture

object ScriptsTab {
    val statusLabels = listOf("All", "Running", "Stopped")
    var frame = 0L
    
    // Caching for filtered and sorted scripts
    private var cachedFilteredList: List<ScriptMetadata>? = null
    private var lastQuery = ""
    private var lastFilterIdx = 0
    private var lastScriptsVersion = 0
    private var lastRunningStates: Map<Class<out Script>, Boolean> = emptyMap()
    private var lastFavorites: Set<Class<out Script>> = emptySet()

    private val undercutLogo = ImGuiTexture.fromPath("/icons/logo_alpha.png")!!

    private fun getFilteredScripts(): List<ScriptMetadata> {
        val query = UIState.scriptSearchText.value.trim().lowercase()
        val filterIdx = UIState.statusFilterIndex.value
        val scriptsVersion = ScriptExecutor.scripts.hashCode()
        val currentRunningStates = ScriptExecutor.scripts.values.associate { 
            it.scriptClass to ScriptExecutor.isScriptRunning(it.scriptClass)
        }
        val currentFavorites = UIState.favoriteScripts.toSet()

        // Check if we can use cached results
        if (cachedFilteredList != null && 
            query == lastQuery && 
            filterIdx == lastFilterIdx && 
            scriptsVersion == lastScriptsVersion &&
            currentRunningStates == lastRunningStates &&
            currentFavorites == lastFavorites) {
            return cachedFilteredList!!
        }
        
        // Recalculate filtered and sorted list - favorites first, then by running status, then alphabetically
        val sorted = ScriptExecutor.scripts.values
            .sortedWith(
                compareByDescending<ScriptMetadata> { currentFavorites.contains(it.scriptClass) }
                    .thenByDescending { currentRunningStates[it.scriptClass] ?: false }
                    .thenBy { it.name.lowercase() }
            )

        val filtered = sorted.filter { meta ->
            val isRunning = currentRunningStates[meta.scriptClass] ?: false
            val matchesQuery = query.isEmpty() || 
                meta.name.lowercase().contains(query) || 
                meta.author.lowercase().contains(query)
            val matchesStatus = when (filterIdx) {
                1 -> isRunning
                2 -> !isRunning
                else -> true
            }
            matchesQuery && matchesStatus
        }
        
        // Update cache
        cachedFilteredList = filtered
        lastQuery = query
        lastFilterIdx = filterIdx
        lastScriptsVersion = scriptsVersion
        lastRunningStates = currentRunningStates
        lastFavorites = currentFavorites

        return filtered
    }
    
    fun ChildScope.render() {
        frame++
        group {
            image(undercutLogo, 100f, 100f)
        }
        sameLine()
        group {
            spacing()
            button("Stop All") { ScriptExecutor.stopAll() }
            sameLine()
            setNextItemWidth(150f)
            combo("Status", UIState.statusFilterIndex, statusLabels)
            spacing()

            inputText("Search", UIState.scriptSearchText)

            button("Reload scripts") { ScriptExecutor.loadScripts() }
        }

        child("ScriptScrollArea", height = -8f, childFlags = ImGuiChildFlags.Borders) {
            getFilteredScripts().forEach { meta ->
                val isRunning = ScriptExecutor.isScriptRunning(meta.scriptClass)
                val isFavorite = UIState.favoriteScripts.contains(meta.scriptClass)

                spacing()

                // Favorite star button
                button(if (isFavorite) "★##fav_${meta.scriptClass.name}" else "☆##fav_${meta.scriptClass.name}") {
                    if (isFavorite) {
                        UIState.favoriteScripts.remove(meta.scriptClass)
                    } else {
                        UIState.favoriteScripts.add(meta.scriptClass)
                    }
                    UIState.saveFavorites() // Save favorites to config
                }
                sameLine()

                collapsingHeader("${meta.name} v${meta.version} by ${meta.author}", flags = 0) {
                    textWrapped(meta.description)
                    if (!isRunning) {
                        button("Start##${meta.scriptClass.name}") {
                            try {
                                val instance = meta.scriptClass.getDeclaredConstructor().newInstance()
                                if (instance is ConfigurableScript) ScriptConfigStore.applyTo(instance)
                                ScriptExecutor.activate(instance)
                            } catch (e: Exception) {
                                println("Failed to start script: ${e.message}")
                            }
                        }
                    } else {
                        button("Stop##${meta.scriptClass.name}") {
                            ScriptExecutor.deactivate(meta.scriptClass)
                        }
                    }                 
                    if (ConfigurableScript::class.java.isAssignableFrom(meta.scriptClass)) {
                        button("Settings##${meta.scriptClass.name}") {
                            val windowState = UIState.openConfigWindows.getOrPut(meta) {
                                boolState(true)
                            }
                            windowState.value = true
                        }
                    }
                }
            }
        }
    }
}
