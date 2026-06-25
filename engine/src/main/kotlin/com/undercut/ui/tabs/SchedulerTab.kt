package com.undercut.ui.tabs

import com.undercut.game.Skill
import com.undercut.script.*
import com.undercut.script.scheduler.*
import com.undercut.ui.backend.dsl.*
import com.undercut.ui.backend.dsl.scopes.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Scheduler UI tab.
 *
 * - Lists Schedulable scripts
 * - Allows building a queue of ScheduleItem (add/configure/reorder/remove)
 * - Controls a single SchedulerScript instance (Start/Pause/Stop)
 * - Persists/loads presets via SchedulesStore
 * - Shows basic status for active scheduler
 */
object SchedulerTab {

    // In-memory builder model (UI only)
    private data class BuilderItem(
        val id: String,
        var scriptClass: String,
        var stopType: StopType = StopType.Time,
        var durationMs: Int = 5 * 60 * 1000, // default 5 minutes
        var skill: Skill = Skill.ATTACK,
        var targetLevel: Int = 10,
        var configJson: String = "{}"
    )

    private enum class StopType { Time, Level }

    private val builderItems = mutableListOf<BuilderItem>()

    // Persistent UI state
    private val presetName = stringState("", 128)
    private val pauseState = boolState(false)
    private val selectedScriptLabel = stringState("", 256)
    private val stopTypeIndex = intState(0)
    private val minutesState = intState(5)
    private val skillLabel = stringState(Skill.entries.first().name, 64)
    private val targetState = intState(10)
    private val presetLoadLabel = stringState("", 128)

    // Config editor modal-like UI state
    private val configEditorVisibleIndex = intState(-1)
    private val configEditorText = stringState("", 4096)
    private val configEditorError = stringState("", 512)
    private val advancedJsonEditor = boolState(false)

    // Pretty printer for UI presentation
    private val prettyGson = GsonBuilder().setPrettyPrinting().create()

    // Internal scheduler instance
    private var scheduler: SchedulerScript? = null

    // Runtime tracking: used to remove items that have completed
    private var lastSeenCurrentId: String? = null
    private var builderIdCounter: Long = 0

    private fun newBuilderId(): String {
        builderIdCounter += 1
        return "item-" + builderIdCounter + "-" + System.nanoTime().toString()
    }

    private fun removeById(id: String) {
        val idx = builderItems.indexOfFirst { it.id == id }
        if (idx >= 0) builderItems.removeAt(idx)
    }

    private fun syncBuilderWithRuntime() {
        val active = SchedulerRuntime.active
        val currentId = try { active?.current()?.id } catch (_: Throwable) { null }
        if (currentId != lastSeenCurrentId) {
            // Previous item finished/removed; drop it from the UI list
            lastSeenCurrentId?.let { removeById(it) }
            lastSeenCurrentId = currentId
        }
    }

    fun ChildScope.render() {
        // Header
        text("Scheduler")
        spacing()

        // Keep the UI builder list in sync with the runtime scheduler
        syncBuilderWithRuntime()

        // Section: Script Picker + Add
        group {
            text("Add Item")
            val schedulable = availableSchedulableScripts()
            if (selectedScriptLabel.value.isEmpty() && schedulable.isNotEmpty()) {
                selectedScriptLabel.value = schedulable.first().first
            }

            // Script selector
            combo("Script", if (schedulable.isEmpty()) "<No schedulable>" else selectedScriptLabel.value) {
                schedulable.forEach { (display, className) ->
                    val isSelected = (selectedScriptLabel.value == display)
                    selectable(display, isSelected) { selectedScriptLabel.value = display }
                }
            }

            // Stop type
            val stopTypeNames = arrayOf("Time-Based", "Level-Based")
            combo("Stop Type", stopTypeNames[stopTypeIndex.value]) {
                stopTypeNames.forEachIndexed { idx, name ->
                    val isSelected = (idx == stopTypeIndex.value)
                    selectable(name, isSelected) { stopTypeIndex.value = idx }
                }
            }

            // Stop configuration inputs
            when (stopTypeIndex.value) {
                0 -> { // Time-Based
                    inputInt("Minutes", minutesState.value) { newVal ->
                        minutesState.value = newVal.coerceAtLeast(0)
                    }
                    sameLine()
                    button("Add") {
                        val scriptClass = schedulable.firstOrNull { it.first == selectedScriptLabel.value }?.second
                        if (scriptClass != null) {
                            builderItems.add(
                                BuilderItem(
                                    id = newBuilderId(),
                                    scriptClass = scriptClass,
                                    stopType = StopType.Time,
                                    durationMs = minutesState.value.coerceAtLeast(0) * 60 * 1000
                                )
                            )
                        }
                    }
                }
                1 -> { // Level-Based
                    val skills = Skill.entries
                    combo("Skill", skillLabel.value) {
                        skills.forEach { s ->
                            val isSelected = (skillLabel.value == s.name)
                            selectable(s.name, isSelected) { skillLabel.value = s.name }
                        }
                    }
                    inputInt("Target Level", targetState.value) { newVal ->
                        targetState.value = newVal.coerceAtLeast(1)
                    }
                    sameLine()
                    button("Add") {
                        val scriptClass = schedulable.firstOrNull { it.first == selectedScriptLabel.value }?.second
                        val skill = skills.firstOrNull { it.name == skillLabel.value } ?: Skill.ATTACK
                        if (scriptClass != null) {
                            builderItems.add(
                                BuilderItem(
                                    id = newBuilderId(),
                                    scriptClass = scriptClass,
                                    stopType = StopType.Level,
                                    skill = skill,
                                    targetLevel = targetState.value.coerceAtLeast(1)
                                )
                            )
                        }
                    }
                }
            }
        }

        separator()

        // Section: Current Builder Items
        text("Schedule Items (${builderItems.size})")
        sameLine()
        checkbox("Advanced JSON Editor", advancedJsonEditor)
        if (builderItems.isEmpty()) {
            text("No items. Use the controls above to add.")
        } else {
            builderItems.forEachIndexed { index, it ->
                spacing()
                group {
                    text("#${index + 1}")
                    sameLine()
                    val displayName = scriptDisplayName(it.scriptClass)
                    text(displayName)
                    sameLine()
                    when (it.stopType) {
                        StopType.Time -> text("Time: ${formatElapsed(it.durationMs.toLong())}")
                        StopType.Level -> text("Level: ${it.skill} -> ${it.targetLevel}")
                    }
                    sameLine()
                    button("↑##up_$index") { moveUp(index) }
                    sameLine()
                    button("↓##down_$index") { moveDown(index) }
                    // sameLine()
                    // button("Edit##$index") { openEditPopup(index) }
                    sameLine()
                    button("Edit Config##cfg_$index") { openConfigEditorWindow(index) }
                    if (advancedJsonEditor.value) {
                        sameLine()
                        button("Edit JSON##json_$index") { openConfigEditor(index) }
                    }
                    sameLine()
                    button("Remove##$index") { removeAt(index) }
                }
            }
        }

        // Inline config editor (modal-like) - shown only when Advanced JSON Editor is enabled
        if (advancedJsonEditor.value && configEditorVisibleIndex.value >= 0) {
            spacing()
            group {
                val idx = configEditorVisibleIndex.value
                text("Edit Config: #${idx + 1} – ${scriptDisplayName(builderItems.getOrNull(idx)?.scriptClass ?: "?")}")
                text("Enter a JSON object. Example: { \"key\": 123 }")
                inputText("JSON", configEditorText)
                if (configEditorError.value.isNotEmpty()) {
                    text("Error: ${configEditorError.value}")
                }
                sameLine()
                button("Validate & Save") {
                    val i = configEditorVisibleIndex.value
                    val obj = parseJsonObject(configEditorText.value)
                    if (obj != null) {
                        builderItems.getOrNull(i)?.let { bi ->
                            bi.configJson = prettyGson.toJson(obj)
                        }
                        configEditorError.value = ""
                        configEditorVisibleIndex.value = -1
                    } else {
                        configEditorError.value = "Invalid JSON: must be a JSON object"
                    }
                }
                sameLine()
                button("Cancel") {
                    configEditorVisibleIndex.value = -1
                    configEditorError.value = ""

                }
            }
        }

        separator()

        // Section: Presets
        group {
            text("Presets")
            inputText("Preset Name", presetName)
            sameLine()
            button("Save") { savePreset() }

            val presets = SchedulesStore.listPresets()
            if (presetLoadLabel.value.isEmpty() && presets.isNotEmpty()) {
                presetLoadLabel.value = presets[0]
            }
            combo("Load##preset", presetLoadLabel.value.ifEmpty { if (presets.isEmpty()) "<No presets>" else presets[0] }) {
                presets.forEach { name ->
                    val isSelected = (name == presetLoadLabel.value)
                    selectable(name, isSelected) { presetLoadLabel.value = name }
                }
            }
            sameLine()
            button("Load##apply") { loadPreset(presetLoadLabel.value) }
        }

        separator()

        // Section: Controls
        group {
            val active = SchedulerRuntime.active

            if (active == null) {
                button("Start Scheduler") { startScheduler() }
            } else {
                val sched = active
                checkbox("Paused", pauseState)
                sameLine()
                button("Apply Pause") {
                    try { sched.setPaused(pauseState.value) } catch (_: Throwable) {}
                }
                sameLine()
                button("Stop Scheduler") {
                    try { sched.stop() } catch (_: Throwable) {}
                    // Ensure immediate UI/runtime clear even if coroutine onStop() hasn't run yet
                    try { SchedulerRuntime.active = null } catch (_: Throwable) {}
                    scheduler = null
                    pauseState.value = false
                    // Do not treat this as a completion; reset tracking
                    lastSeenCurrentId = null
                }
            }
        }

        // Section: Status
        spacing()
        text("Status")
        val active = SchedulerRuntime.active
        if (active == null) {
            text("Idle")
        } else {
            try {
                val current = active.current()
                val elapsed = active.elapsedMs()
                val q = active.queuedCount()
                val paused = active.isSchedulerPaused()
                text("Current: ${current?.scriptClass ?: "<none>"}")
                text("Stop: ${current?.stop}")
                val cfgPreview = current?.configuration?.let { previewConfig(it) } ?: "<none>"
                text("Config: $cfgPreview")
                text("Elapsed: ${formatElapsed(elapsed)}")
                text("Queued: $q")
                text("Paused: $paused")
            } catch (e: Throwable) {
                text("Status unavailable: ${e.message}")
            }
        }

        // Render any open entry config editor windows
        SchedulerEntryConfigEditor.render()
    }

    // --- Internals ---

    private fun availableSchedulableScripts(): List<Pair<String, String>> {
        return ScriptExecutor.scripts.values
            .filter { SchedulableScript::class.java.isAssignableFrom(it.scriptClass) }
            .sortedBy { it.name.lowercase() }
            .map { meta ->
                val display = "${meta.name} by ${meta.author}"
                val clazz = meta.scriptClass.name
                display to clazz
            }
    }

    private fun scriptDisplayName(className: String): String {
        val meta = ScriptExecutor.scripts.values.firstOrNull { it.scriptClass.name == className }
        return if (meta != null) "${meta.name} by ${meta.author}" else className.substringAfterLast('.')
    }

    private fun moveUp(index: Int) {
        if (index <= 0) return
        val item = builderItems.removeAt(index)
        builderItems.add(index - 1, item)
    }

    private fun moveDown(index: Int) {
        if (index >= builderItems.lastIndex) return
        val item = builderItems.removeAt(index)
        builderItems.add(index + 1, item)
    }

    private fun removeAt(index: Int) {
        if (index in builderItems.indices) builderItems.removeAt(index)
    }

    private fun openEditPopup(index: Int) {
        // Minimal inline editor: toggle stop type & adjust values
        if (index !in builderItems.indices) return
        val item = builderItems[index]
        // Toggle type
        item.stopType = when (item.stopType) {
            StopType.Time -> StopType.Level
            StopType.Level -> StopType.Time
        }
    }

    private fun openConfigEditor(index: Int) {
        if (index !in builderItems.indices) return
        configEditorVisibleIndex.value = index
        configEditorText.value = builderItems[index].configJson.take(4096)
        configEditorError.value = ""
    }

    private fun openConfigEditorWindow(index: Int) {
        if (index !in builderItems.indices) return
        val item = builderItems[index]
        SchedulerEntryConfigEditor.open(item.scriptClass, item.configJson) { pretty ->
            builderItems[index].configJson = pretty
        }
    }

    private fun toScheduleItems(): List<ScheduleItem> {
        return builderItems.mapIndexed { idx, it ->
            val stop = when (it.stopType) {
                StopType.Time -> StopCondition.TimeBased(it.durationMs.toLong())
                StopType.Level -> StopCondition.LevelBased(it.skill, it.targetLevel)
            }
            val cfg = parseForSchedule(it.configJson)
            ScheduleItem(id = it.id, scriptClass = it.scriptClass, stop = stop, configuration = cfg)
        }
    }

    private fun startScheduler() {
        val items = toScheduleItems()
        if (items.isEmpty()) return
        try {
            // Reuse existing instance if possible
            val instance = scheduler ?: SchedulerScript().also { scheduler = it }
            instance.setSchedule(items)
            instance.setPaused(false)
            pauseState.value = false
            lastSeenCurrentId = null
            ScriptExecutor.activate(instance)
        } catch (e: Throwable) {
            println("Failed to start scheduler: ${e.message}")
        }
    }

    private fun savePreset() {
        val name = presetName.value.trim()
        if (name.isEmpty()) return
        try {
            val items = toScheduleItems()
            SchedulesStore.save(name, items)
        } catch (e: Throwable) {
            println("Failed to save preset '$name': ${e.message}")
        }
    }

    private fun loadPreset(name: String) {
        if (name.isBlank()) return
        try {
            val items = SchedulesStore.load(name)
            builderItems.clear()
            items.forEach { item ->
                when (val stop = item.stop) {
                    is StopCondition.TimeBased -> builderItems.add(
                        BuilderItem(
                            id = item.id,
                            scriptClass = item.scriptClass,
                            stopType = StopType.Time,
                            durationMs = stop.durationMs.toInt(),
                            configJson = item.configuration?.let { prettyGson.toJson(it) } ?: "{}"
                        )
                    )
                    is StopCondition.LevelBased -> builderItems.add(
                        BuilderItem(
                            id = item.id,
                            scriptClass = item.scriptClass,
                            stopType = StopType.Level,
                            skill = stop.skill,
                            targetLevel = stop.targetLevel,
                            configJson = item.configuration?.let { prettyGson.toJson(it) } ?: "{}"
                        )
                    )
                }
            }
        } catch (e: Throwable) {
            println("Failed to load preset '$name': ${e.message}")
        }
    }

    private fun parseJsonObject(text: String): JsonObject? {
        return try {
            val element = JsonParser.parseString(text)
            if (element.isJsonObject) element.asJsonObject else null
        } catch (_: Throwable) { null }
    }

    private fun parseForSchedule(text: String): JsonObject? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null
        return try {
            val element = JsonParser.parseString(trimmed)
            if (element.isJsonObject) element.asJsonObject else null
        } catch (e: Throwable) {
            println("Invalid config JSON; ignoring: ${e.message}")
            null
        }
    }

    private fun formatElapsed(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun previewConfig(obj: JsonObject): String {
        val parts = obj.entrySet().take(3).map { e ->
            val v = e.value
            val vStr = when {
                v.isJsonPrimitive -> v.asJsonPrimitive.toString()
                v.isJsonObject -> "{...}"
                v.isJsonArray -> "[...]"
                else -> v.toString()
            }
            "${e.key}=$vStr"
        }
        val s = parts.joinToString(", ")
        return if (s.length > 80) s.take(77) + "..." else s
    }
}
