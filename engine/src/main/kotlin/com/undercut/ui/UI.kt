package com.undercut.ui

import com.undercut.game.Skill
import com.undercut.game.hooks.Priority
import com.undercut.game.interfaces.effects.Effect
import com.undercut.game.math.Vector3f
import com.undercut.game.math.WorldToScreen
import com.undercut.game.nxt.entity.Entity
import com.undercut.scene.SceneSnapshot
import com.undercut.script.ConfigurableScript
import com.undercut.script.ScriptConfigStore
import com.undercut.script.ScriptExecutor
import com.undercut.script.api.*
import com.undercut.ui.backend.dsl.ImGuiDsl.backgroundDrawList
import com.undercut.ui.backend.dsl.ImGuiDsl.setNextWindowPos
import com.undercut.ui.backend.dsl.ImGuiDsl.setNextWindowSize
import com.undercut.ui.backend.dsl.ImGuiDsl.window
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.utils.Corner
import com.undercut.ui.backend.dsl.utils.ImGuiChildFlags
import com.undercut.ui.backend.dsl.utils.ImGuiCol
import com.undercut.ui.backend.dsl.utils.ImGuiColors
import com.undercut.ui.backend.dsl.utils.ImGuiColors.hex
import com.undercut.ui.backend.flags.ImGuiCond
import com.undercut.ui.backend.flags.WindowFlags
import com.undercut.ui.backend.native.ImGuiTexture
import com.undercut.ui.backend.native.ImageHelper.getNoiseTexture
import com.undercut.ui.backend.native.SpriteRotation
import com.undercut.ui.backend.native.spriteTexture
import com.undercut.ui.backend.rendering.ImGUIRender
import com.undercut.ui.tabs.*
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * ImGui-based UI implementation for Project Undercut
 * This replaces the Swing UI with an in-game overlay using ImGui
 */
object UI {
    // Reference to centralized state
    private val originalOut: PrintStream = System.out


    enum class Category(val displayName: String) {
        AUTOMATION("AUTOMATION"),
        GAME("GAME"),
        DEVELOPER("DEVELOPER"),
        SYSTEM("SYSTEM")
    }

    enum class Tab(val displayName: String, val category: Category) {
        SCRIPTS("Scripts", Category.AUTOMATION),
        SCHEDULER("Scheduler", Category.AUTOMATION),
        QUEST_HELPER("Quest Helper", Category.AUTOMATION),
        TILE_MARKERS("Tile Markers", Category.GAME),
        SKILLS("Skills", Category.GAME),
        INVENTORY("Inventory", Category.GAME),
        BUFFS_DEBUFFS("Buffs & Debuffs", Category.GAME),
        ENTITIES("Entities", Category.GAME),
        LOGS("Logs", Category.DEVELOPER),
        PACKETS("Packets", Category.DEVELOPER),
        TRAINING("Training", Category.DEVELOPER),
        VAR_DEBUG("Var Debug", Category.DEVELOPER),
        INTERFACE_DEBUG("Interface Debug", Category.DEVELOPER),
        CS2_TRACE("CS2 Trace", Category.DEVELOPER),
        SETTINGS("Settings", Category.SYSTEM)
    }

    enum class InventoryType(val id: Int, val displayName: String) {
        BACKPACK(93, "Backpack"),
        EQUIPMENT(94, "Equipment"),
        AREA_LOOT(773, "Area Loot"),
        BANK(95, "Bank"),
        BEAST_OF_BURDEN(530, "Beast of Burden")
    }

    data class VarcEntry(
        val type: String,
        val id: Int,
        val prevValue: Int,
        val newValue: Int
    )


    data class InventoryEntry(
        val slot: Int,
        val itemId: Int,
        val name: String,
        val amount: Int
    )

    data class BuffDebuffEntry(
        val effect: Effect,
        val name: String,
        val isActive: Boolean,
        val timeRemaining: Long,
        val stacks: Int,
        val isDebuff: Boolean
    )

    init {
        PacketsTab.registerCallback()
        setupLogging()
    }

    @JvmStatic
    @ImGUIRender(priority = Priority.LOW)
    fun render() {
        try {
            val anyEntityOverlay = UIState.showSceneObjects.value || UIState.showNpcs.value || UIState.showSpotAnims.value || UIState.showProjectiles.value || UIState.showClickboxes.value
            if (anyEntityOverlay) {
                val overlayColor = ImGuiColors.rgba(
                    UIState.entityTextColorR.value,
                    UIState.entityTextColorG.value,
                    UIState.entityTextColorB.value,
                    UIState.entityTextColorA.value
                )
                val showClickboxes = UIState.showClickboxes.value
                val clickboxFilled = UIState.showClickboxesFilled.value
                val clickboxColor = ImGuiColors.rgba(
                    UIState.clickboxColorR.value,
                    UIState.clickboxColorG.value,
                    UIState.clickboxColorB.value,
                    1f
                )

                backgroundDrawList {
                    val range = UIState.entityRange.value
                    val scene = SceneSnapshot.collect(range)
                    val playerX = scene.playerTileX
                    val playerY = scene.playerTileY

                    if (UIState.showSceneObjects.value) {
                        scene.objects.forEach { obj ->
                            val fine = Vector3f(obj.fine.x, obj.fine.y, obj.fine.z)
                            val xy = WorldToScreen.getEstimatedTileCenter(fine)
                            if (xy != null) {
                                tile(fine, overlayColor)
                                text(xy.transform(0f, -26f), overlayColor, "${obj.name} (${obj.id})")
                                var yOffset = -13f

                                if (obj.typeId != obj.id) {
                                    text(xy.transform(0f, yOffset), overlayColor, "TypeId: ${obj.typeId}")
                                    yOffset += 13f
                                }
                                text(xy.transform(0f, yOffset), overlayColor, "Shape: ${obj.shape}")
                                yOffset += 13f
                            }
                        }
                    }

                    if (UIState.showNpcs.value) {
                        scene.npcs.forEach { npc ->
                            val dx = npc.tileX - playerX
                            val dy = npc.tileY - playerY
                            val distanceSq = dx * dx + dy * dy
                            if (distanceSq <= range * range) {
                                val fine = Vector3f(npc.fine.x, npc.fine.y, npc.fine.z)
                                val xy = WorldToScreen.getEstimatedTileCenter(fine)
                                if (xy != null) {
                                    tile(fine, overlayColor)

                                    text(xy.transform(0f, -26f), overlayColor, "${npc.name} (${npc.id})")

                                    var yOffset = -13f

                                    if (npc.currentHealth > 0 || npc.maxHealth > 0) {
                                        text(xy.transform(0f, yOffset), overlayColor, "Health: ${npc.currentHealth}/${npc.maxHealth}")
                                        yOffset += 13f
                                    }

                                    if (npc.typeId != npc.id) {
                                        text(xy.transform(0f, yOffset), overlayColor, "TypeId: ${npc.typeId}")
                                        yOffset += 13f
                                    }

                                    if (npc.animationId > 0) {
                                        text(xy.transform(0f, yOffset), overlayColor, "Anim: ${npc.animationId}")
                                        yOffset += 13f
                                    }

                                    if (npc.hiddenMenuOpFlags > 0) {
                                        text(xy.transform(0f, yOffset), overlayColor, "Menu-flags: ${npc.hiddenMenuOpFlags}")
                                        yOffset += 13f
                                    }
                                }
                            }
                        }
                    }

                    if (UIState.showSpotAnims.value) {
                        scene.spotAnims.forEach { sa ->
                            val fine = Vector3f(sa.fine.x, sa.fine.y, sa.fine.z)
                            val xy = WorldToScreen.getEstimatedTileCenter(fine)
                            if (xy != null) {
                                tile(fine, overlayColor)
                                text(xy.transform(0f, -26f), overlayColor, "${sa.id})")
                            }
                        }
                    }

                    if (UIState.showProjectiles.value) {
                        scene.projectiles.forEach { p ->
                            val fine = Vector3f(p.fine.x, p.fine.y, p.fine.z)
                            val xy = WorldToScreen.getEstimatedTileCenter(fine)
                            if (xy != null) {
                                tile(fine, overlayColor)
                                text(xy.transform(0f, -26f), overlayColor, "${p.id})")
                            }
                        }
                    }

                    if (UIState.showGroundItems.value) {
                        val tileLines = HashMap<Long, Int>()
                        scene.items.forEach { item ->
                            val fine = Vector3f(item.fine.x, item.fine.y, item.fine.z)
                            val xy = WorldToScreen.getEstimatedTileCenter(fine) ?: return@forEach
                            val key = item.tileX.toLong() shl 20 or item.tileY.toLong()
                            val line = tileLines.getOrDefault(key, 0)
                            if (line == 0) tile(fine, overlayColor)
                            val label = if (item.amount > 1) "${item.name} (${item.id}) x${item.amount}" else "${item.name} (${item.id})"
                            text(xy.transform(0f, -26f - line * 13f), overlayColor, label)
                            tileLines[key] = line + 1
                        }
                    }

                    // Tile-overlay clickbox fallback for scene objects + ground items. NPCs use the
                    // engine's render-model glow via HighlightTick. Locations/ItemStacks structurally
                    // don't have a per-instance render-model in 948-2-2 — the highlight render-model
                    // is built per-draw-call from category-color globals — so until we wire that path,
                    // draw a clickbox-colored tile under each one.
                    if (showClickboxes) {
                        scene.objects.forEach { obj ->
                            tile(Vector3f(obj.fine.x, obj.fine.y, obj.fine.z), clickboxColor)
                        }
                        scene.items.forEach { item ->
                            tile(Vector3f(item.fine.x, item.fine.y, item.fine.z), clickboxColor)
                        }
                    }

                }
            }

            // Decorative watermark — best-effort. A texture that fails to (re)create (e.g. on a
            // hot-reload) must never abort the whole render and blank the overlay.
            ImGuiTexture.fromPath("/icons/logo_alpha.png")?.let { logo ->
                backgroundDrawList {
                    watermark(logo, Corner.TopRight, paddingX = 48f, paddingY = 4f, alpha = 0.5f, scale = 0.3f)
                }
            }

            // Only show the main control window if visible (Kotlin-side state)
            val showMain = UIState.showMainWindow.value
            if (showMain) {
                setNextWindowPos(10f, 10f, ImGuiCond.FirstUseEver)
                setNextWindowSize(520f, 800f, ImGuiCond.FirstUseEver)

                window("Project Undercut", WindowFlags.NoTitleBar) {
                    // Best-effort decorations — never let a failed texture abort the window content.
                    runCatching {
                        watermark(spriteTexture(18026, SpriteRotation.R0), Corner.BottomLeft, scale = 1.5f)
                        watermark(spriteTexture(18026, SpriteRotation.R270), Corner.BottomRight, scale = 1.5f)
                        applyBackgroundOverlay(getNoiseTexture(), 0.25f)
                    }

                    child("nav-sidebar", width = 140f, height = 0f, childFlags = ImGuiChildFlags.Borders) {
                        Category.entries.forEachIndexed { index, category ->
                            if (index > 0) {
                                spacing()
                                separator()
                                spacing()
                            }
                            pushStyleColor(ImGuiCol.Text, hex("#C89830"))
                            text(category.displayName)
                            popStyleColor(1)
                            Tab.entries.filter { it.category == category }.forEach { tab ->
                                selectable(tab.displayName, isSelected = UIState.selectedTab == tab) {
                                    UIState.selectedTab = tab
                                }
                            }
                        }
                    }
                    sameLine()
                    child("tab-content", width = 0f, height = 0f) {
                        renderTabContent()
                    }

                }
            }
            refreshDataForVisibleTabs()
            renderConfigurationWindows()
        } catch (t: Throwable) {
            // Catch Throwable (not Exception) so a missing-class hot-swap mismatch or any
            // other Error doesn't permanently kill the render loop.
            println("Error in ImGui render: ${t.message}")
            t.printStackTrace()
        }
    }

    private fun ChildScope.renderTabContent() {
        try {
            when (UIState.selectedTab) {
                Tab.SCRIPTS -> with(ScriptsTab) { render() }
                Tab.SCHEDULER -> with(SchedulerTab) { render() }
                Tab.QUEST_HELPER -> with(QuestHelperTab) { render() }
                Tab.TILE_MARKERS -> with(TileMarkersTab) { render() }
                Tab.SKILLS -> with(SkillsTab) { render() }
                Tab.INVENTORY -> with(InventoryTab) { render() }
                Tab.BUFFS_DEBUFFS -> with(BuffsDebuffsTab) { render() }
                Tab.ENTITIES -> with(EntitiesTab) { render() }
                Tab.LOGS -> with(LogsTab) { render() }
                Tab.PACKETS -> with(PacketsTab) { render() }
                Tab.TRAINING -> with(TrainingTab) { render() }
                Tab.VAR_DEBUG -> with(VarDebugTab) { render() }
                Tab.INTERFACE_DEBUG -> with(InterfaceDebugTab) { render() }
                Tab.CS2_TRACE -> with(CS2TraceTab) { render() }
                Tab.SETTINGS -> with(SettingsTab) { render() }
            }
        } catch (t: Throwable) {
            text("Tab '${UIState.selectedTab}' failed to render:")
            text(t.javaClass.simpleName + ": " + (t.message ?: ""))
            text("Pick another tab. (See logs for stack trace.)")
            println("[UI] Tab render error for ${UIState.selectedTab}: ${t.message}")
            t.printStackTrace()
        }
    }

    private fun refreshDataForVisibleTabs() {
        // Auto-refresh for enabled tabs (simplified)
        val currentTime = System.currentTimeMillis()
        if (currentTime - UIState.lastRefreshTime > UIState.refreshInterval) {
            UIState.lastRefreshTime = currentTime

            if (UIState.inventoryEnabled.value) {
                InventoryTab.loadInventory(UIState.inventoryId.value)
            }
            if (UIState.buffsDebuffsEnabled.value) {
                BuffsDebuffsTab.loadBuffsDebuffs()
            }
            // Tail logs like `tail -f`: pull recent lines when Logs tab visible
            if (UIState.selectedTab == Tab.LOGS) {
                LogsTab.updateLogLines()
            }
        }
    }

    // Expose debug flags to hooks
    fun wantsVarcDebug(): Boolean = SettingsTab.wantsVarcDebug()
    fun wantsVarpDebug(): Boolean = SettingsTab.wantsVarpDebug()

    private fun WindowScope.renderScriptConfig(script: ConfigurableScript) {
        with(ScriptConfigRenderer) { renderScriptConfig(script) }
    }

    fun addVarTableEntry(type: String, id: Int, prevValue: Int, newValue: Int) {
        SettingsTab.addVarTableEntry(type, id, prevValue, newValue)
    }

    fun updateXpTable(skill: Skill, xpGained: Int) {
        SkillsTab.updateXpTable(skill, xpGained)
    }

    private fun setupLogging() {
        try {
            val userHome = System.getProperty("user.home")
            val logDir = File(userHome, ".undercut/logs")
            if (!logDir.exists()) {
                logDir.mkdirs()
            }
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(Date())
            val pid = ProcessHandle.current().pid()
            UIState.logFile = File(logDir, "undercut-${timestamp}-${pid}.log")

            if (!UIState.logFile!!.exists()) {
                UIState.logFile!!.createNewFile()
            }

            val fileOutputStream = FileOutputStream(UIState.logFile!!, false)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

            UIState.logLines.add("Logging Initialized at ${LocalDateTime.now().format(formatter)}")

            // Redirect System.out and System.err
            val printStream = object : PrintStream(fileOutputStream, /* autoFlush = */ true) {
                override fun println(x: String?) {
                    val timestamped = "[${LocalDateTime.now().format(formatter)}] $x"
                    super.println(timestamped) // autoFlush ensures file mtime updates
                    originalOut.println(timestamped)
                    // Add to log lines for display
                    UIState.logLines.add(timestamped)
                    if (UIState.logLines.size > UIState.maxLogLines) {
                        UIState.logLines.removeAt(0)
                    }
                }

                override fun println(x: Any?) {
                    // Funnel to String variant so we always capture logs regardless of overload used
                    println(x?.toString())
                }

                override fun println() {
                    // Preserve empty line semantics
                    println("")
                }
            }

            System.setOut(printStream)
            System.setErr(printStream)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun renderConfigurationWindows() {
        // Render each open configuration window
        UIState.openConfigWindows.entries.removeAll { (metadata, windowState) ->
            // Safely read state; treat closed states as not open
            val isOpen = try {
                windowState.value
            } catch (_: IllegalStateException) {
                false
            }

            if (!isOpen) {
                // Window was closed or state is invalid, clean up
                try {
                    windowState.close()
                } catch (_: Throwable) {
                }

                return@removeAll true
            }

            try {
                // Get or create script instance for configuration
                val instance = ScriptExecutor.getScriptInstance(metadata.scriptClass)
                    ?: if (ConfigurableScript::class.java.isAssignableFrom(metadata.scriptClass)) {
                        try {
                            metadata.scriptClass.getDeclaredConstructor().newInstance().also {
                                if (it is ConfigurableScript) ScriptConfigStore.applyTo(it)
                            }
                        } catch (e: Exception) {
                            println("Failed to create script instance for configuration: ${e.message}")
                            null
                        }
                    } else null

                if (instance is ConfigurableScript) {
                    // Position window offset from configured base position
                    val windowIndex = UIState.openConfigWindows.keys.indexOf(metadata)
                    val offsetX = 100f + (windowIndex * 30f)
                    val offsetY = 100f + (windowIndex * 30f)
                    setNextWindowPos(offsetX, offsetY, cond = ImGuiCond.FirstUseEver)
                    setNextWindowSize(400f, 500f, cond = ImGuiCond.FirstUseEver)

                    window(
                        title = "${metadata.name} ${metadata.version} Settings",
                        flags = WindowFlags.None,
                        open = windowState
                    ) {
                        watermark(spriteTexture(18026, SpriteRotation.R0), Corner.BottomLeft, scale = 1.2f)
                        watermark(spriteTexture(18026, SpriteRotation.R270), Corner.BottomRight, scale = 1.2f)
                        applyBackgroundOverlay(getNoiseTexture(), 0.25f)
                        renderScriptConfig(instance)

                        separator()
                        button("Save & Close") {
                            ScriptConfigStore.save(instance)
                            windowState.value = false
                        }
                        sameLine()
                        button("Save") {
                            ScriptConfigStore.save(instance)
                            UIState.configSaveConfirmations[metadata] = System.currentTimeMillis()
                        }

                        // Show save confirmation message if recently saved
                        val lastSaveTime = UIState.configSaveConfirmations[metadata] ?: 0L
                        if (System.currentTimeMillis() - lastSaveTime < 2000) {
                            spacing()
                            text("✓ Settings saved!")
                        }
                    }
                } else {
                    // Script doesn't support configuration, close window
                    windowState.value = false
                }
            } catch (e: Exception) {
                println("Error rendering config window for ${metadata.name}: ${e.message}")
                e.printStackTrace()
                windowState.value = false
            }

            false // Don't remove from map yet
        }
    }

    fun cleanup() {
        UIState.cleanup()
    }

}
