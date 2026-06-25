package com.undercut.ui

import com.undercut.game.Skill
import com.undercut.game.hooks.impl.SDLKeycode
import com.undercut.script.Script
import com.undercut.script.ScriptMetadata
import com.undercut.ui.backend.dsl.*
import com.undercut.util.Configuration
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

object UIState {
    // Tab and Navigation State
    var selectedTab = UI.Tab.SCRIPTS
    // Main window visibility
    val showMainWindow = boolState(true)
    // Configurable key to toggle main UI (SDL keycode), defaults from config (fallback to F12 if missing/zero)
    val uiToggleKey = intState(Configuration.config.uiToggleKey.takeIf { it != 0 } ?: SDLKeycode.F12.value)

    // Script Configuration Windows State
    val openConfigWindows = mutableMapOf<ScriptMetadata, ImGuiState<Boolean>>()
    val configSaveConfirmations = mutableMapOf<ScriptMetadata, Long>()
    
    // Search and Filter State
    val scriptSearchText = stringState("", 128)
    val statusFilterIndex = intState(0) // 0: All, 1: Running, 2: Stopped
    val favoriteScripts = mutableSetOf<Class<out Script>>().apply {
        // Load favorites from config on startup
        val savedFavorites = Configuration.config.favoriteScripts
        savedFavorites.forEach { className ->
            try {
                val scriptClass = Class.forName(className) as? Class<out Script>
                scriptClass?.let { add(it) }
            } catch (e: ClassNotFoundException) {
                // Script class no longer exists, ignore
            }
        }
    }
    val varcSearchText = stringState("", 128)
    val inventorySearchText = stringState("", 128)
    val buffsDebuffsSearchText = stringState("", 128)
    val entitySearchText = stringState("", 128)
    val packetSearchText = stringState("", 128)
    val questHelperFilter = stringState("", 128)

    val interfaceDebugInterfaceId = intState(0)
    val interfaceDebugComponentId = intState(0)
    val interfaceDebugInterfaceScanMax = intState(2000)
    val interfaceDebugComponentScanMax = intState(600)
    val interfaceDebugTextFilter = stringState("", 128)
    val interfaceDebugItemIdFilter = intState(-1)
    val interfaceDebugShowSlotChildren = boolState(true)
    val interfaceDebugOpenInterfaces = mutableListOf<Int>()
    val interfaceDebugFoundComponents = mutableListOf<Int>()

    val varDebugDomainIndex = intState(0)
    val varDebugReadModeIndex = intState(0)
    val varDebugId = intState(0)
    val varDebugLive = boolState(true)
    val varDebugCachedValue = intState(0)

    val cs2TraceEnabled = boolState(false)
    val cs2TraceFilterId = intState(-1)
    val cs2TraceSearch = stringState("", 64)
    val cs2TraceShowArgs = boolState(true)

    data class VarDebugWatch(
        var domainIndex: Int,
        var readModeIndex: Int,
        var id: Int,
        var live: Boolean = true,
        var cachedValue: Int = 0
    )

    val varDebugWatches = mutableListOf<VarDebugWatch>()

    val varChangeTrackingEnabled = boolState(false)
    val varChangeSearchText = stringState("", 128)
    val varChangeTrackVarp = boolState(true)
    val varChangeTrackVarpbit = boolState(true)
    val varChangeTrackVarc = boolState(true)
    val varChangeTrackVarcbit = boolState(true)

    // Feature Toggle State
    val mcpEnabled = boolState(false)
    val varpDebugEnabled = boolState(false)
    val varcDebugEnabled = boolState(false)
    val discordEnabled = boolState(Configuration.config.discordEnabled)
    val inventoryEnabled = boolState(false)
    val buffsDebuffsEnabled = boolState(false)
    val packetCaptureEnabled = boolState(true)
    val packetAutoScroll = boolState(true)
    // Raw login/RSA handshake byte dump (RawLoginDump). Off by default — it hooks the per-byte
    // socket funnels, so only enable while capturing a login.
    val rawLoginDumpEnabled = boolState(false)
    val showSceneObjects = boolState(false)
    val showNpcs = boolState(false)
    val showPlayers = boolState(false)
    val showSpotAnims = boolState(false)
    val showProjectiles = boolState(false)
    val showGroundItems = boolState(false)
    val showClickboxes = boolState(false)
    val showClickboxesFilled = boolState(false)
    val questHelperEnabled = boolState(true)
    val questHelperShowOverlay = boolState(true)
    val questHelperAutoAdvance = boolState(true)
    val questHelperShowLocked = boolState(false)
    val questHelperShowCompleted = boolState(false)
    val slidePuzzleSolver = boolState(true)
    val inventionXpTrackerEnabled = boolState(false)

    // Configuration State
    val discordWebhookUrl = stringState(Configuration.config.discordWebhookUrl, 256)
    val discordUsername = stringState(Configuration.config.discordUsername, 64)
    val inventoryId = intState(UI.InventoryType.BACKPACK.id)
    val packetDirFilter = intState(0) // 0=All, 1=Server, 2=Client

    // Range and Limit State
    val entityRange = intState(20)
    val maxVarcEntries = 100
    val maxLogLines = 100
    
    // Overlay appearance
    
    val entityTextColorR = floatState(1.0f)
    val entityTextColorG = floatState(1.0f)
    val entityTextColorB = floatState(1.0f)
    val entityTextColorA = floatState(0.8f)

    // Clickbox / entity-outline color (drives the engine's highlight category we apply to
    // visible entities via HighlightTick; same slot is exposed to scripts via EntityHighlight).
    val clickboxColorR = floatState(0.0f)
    val clickboxColorG = floatState(1.0f)
    val clickboxColorB = floatState(1.0f)
    val clickboxOutlineMode = intState(2)   // 0=off 1=solid 2=outline 3=glow
    val clickboxIntensity = intState(8)     // 0..255 (engine maps to scale byte)

    // Refresh and Timing State
    var lastRefreshTime = 0L
    val refreshInterval = 100L // 3 seconds
    
    // Data Collections
    val xpData = mutableMapOf<Skill, Pair<Long, Int>>()
    val varTableData = mutableListOf<UI.VarcEntry>()
    val inventoryData = mutableListOf<UI.InventoryEntry>()
    val buffsDebuffsData = mutableListOf<UI.BuffDebuffEntry>()
    val logLines = CopyOnWriteArrayList<String>()
    
    // Logging State
    var logFile: File? = null
    var lastLogModifiedTime = 0L
    
    // Helper function to save favorites to persistent storage
    fun saveFavorites() {
        val favoriteClassNames = favoriteScripts.map { it.name }.toSet()
        Configuration.saveFavoriteScripts(favoriteClassNames)
    }

    fun cleanup() {
        // Clean up ImGui states
        showMainWindow.close()
        uiToggleKey.close()
        scriptSearchText.close()
        statusFilterIndex.close()
        varcSearchText.close()
        inventorySearchText.close()
        buffsDebuffsSearchText.close()
        entitySearchText.close()
        mcpEnabled.close()
        varpDebugEnabled.close()
        varcDebugEnabled.close()
        discordEnabled.close()
        discordWebhookUrl.close()
        discordUsername.close()
        inventoryId.close()
        inventoryEnabled.close()
        buffsDebuffsEnabled.close()
        packetCaptureEnabled.close()
        packetAutoScroll.close()
        rawLoginDumpEnabled.close()
        packetDirFilter.close()
        packetSearchText.close()

        // Close overlay color states
        
        entityTextColorR.close()
        entityTextColorG.close()
        entityTextColorB.close()
        entityTextColorA.close()
        clickboxColorR.close()
        clickboxColorG.close()
        clickboxColorB.close()
        clickboxOutlineMode.close()
        clickboxIntensity.close()
        showSceneObjects.close()
        showNpcs.close()
        showPlayers.close()
        showSpotAnims.close()
        showProjectiles.close()
        showGroundItems.close()
        showClickboxes.close()
        showClickboxesFilled.close()
        questHelperFilter.close()
        questHelperEnabled.close()
        slidePuzzleSolver.close()
        inventionXpTrackerEnabled.close()
        interfaceDebugInterfaceId.close()
        interfaceDebugComponentId.close()
        interfaceDebugInterfaceScanMax.close()
        interfaceDebugComponentScanMax.close()
        interfaceDebugTextFilter.close()
        interfaceDebugItemIdFilter.close()
        interfaceDebugShowSlotChildren.close()
        varDebugDomainIndex.close()
        varDebugReadModeIndex.close()
        varDebugId.close()
        varDebugLive.close()
        varDebugCachedValue.close()
        cs2TraceEnabled.close()
        cs2TraceFilterId.close()
        cs2TraceSearch.close()
        cs2TraceShowArgs.close()
        varChangeTrackingEnabled.close()
        varChangeSearchText.close()
        varChangeTrackVarp.close()
        varChangeTrackVarpbit.close()
        varChangeTrackVarc.close()
        varChangeTrackVarcbit.close()
        interfaceDebugOpenInterfaces.clear()
        interfaceDebugFoundComponents.clear()
        varDebugWatches.clear()
        questHelperShowOverlay.close()
        questHelperAutoAdvance.close()
        questHelperShowLocked.close()
        questHelperShowCompleted.close()
        
        // Close script configuration windows
        openConfigWindows.values.forEach { it.close() }
        openConfigWindows.clear()
    }
}
