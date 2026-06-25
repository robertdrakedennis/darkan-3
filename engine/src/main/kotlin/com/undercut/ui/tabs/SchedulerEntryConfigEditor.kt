package com.undercut.ui.tabs

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.undercut.script.*
import com.undercut.ui.backend.dsl.ImGuiDsl.setNextWindowPos
import com.undercut.ui.backend.dsl.ImGuiDsl.setNextWindowSize
import com.undercut.ui.backend.dsl.ImGuiDsl.window
import com.undercut.ui.backend.dsl.ImGuiState
import com.undercut.ui.backend.dsl.boolState
import com.undercut.ui.backend.dsl.intState
import com.undercut.ui.backend.dsl.scopes.WindowScope
import com.undercut.ui.backend.dsl.scopes.*
import com.undercut.ui.backend.dsl.scopes.group
import com.undercut.ui.backend.dsl.scopes.sameLine
import com.undercut.ui.backend.dsl.scopes.separator
import com.undercut.ui.backend.dsl.scopes.spacing
import com.undercut.ui.backend.dsl.scopes.text
import com.undercut.ui.backend.dsl.scopes.textWrapped
import com.undercut.ui.backend.flags.ImGuiCond
import com.undercut.ui.backend.flags.WindowFlags

/**
 * Editor window for per-entry Scheduler configuration.
 *
 * - Renders a temporary script instance using ScriptConfigRenderer in SessionOnly mode
 * - Produces a JSON overlay (Delta or Full) compatible with ScriptConfigJson
 * - Does not write to ScriptConfigStore at any time while editing
 */
object SchedulerEntryConfigEditor {

    private val prettyGson = GsonBuilder().setPrettyPrinting().create()

    private data class Session(
        val id: String,
        val scriptClassName: String,
        val onSave: (String) -> Unit,
        val open: ImGuiState<Boolean> = boolState(true),
        val baselineIdx: ImGuiState<Int> = intState(0), // 0: Defaults, 1: Global
        val modeIdx: ImGuiState<Int> = intState(0), // 0: Delta, 1: Full
        var instance: ConfigurableScript? = null,
        var error: String = "",
        var initWarning: String? = null
    )

    private val sessions = mutableListOf<Session>()

    /** Open an editor for the given script class name. */
    fun open(scriptClassName: String, initialJson: String? = null, onSave: (String) -> Unit) {
        val session = Session(
            id = "editor-${System.nanoTime()}",
            scriptClassName = scriptClassName,
            onSave = onSave
        )
        // Attempt to create instance (Defaults baseline initially)
        try {
            val clazz = Class.forName(scriptClassName).asSubclass(ConfigurableScript::class.java)
            val result = ConfigOverlaySession.createScriptInstance(clazz, ConfigOverlaySession.Baseline.Defaults)
            session.instance = result.getOrThrow()

            // Apply initial JSON (if any)
            val trimmed = initialJson?.trim().orEmpty()
            if (trimmed.isNotEmpty() && trimmed != "{}") {
                try {
                    val element = JsonParser.parseString(trimmed)
                    if (element.isJsonObject) {
                        val validation = ScriptConfigJson.applyTo(session.instance!!, element.asJsonObject)
                        session.initWarning = validation.message
                    } else {
                        session.initWarning = "Initial JSON is not an object"
                    }
                } catch (e: Throwable) {
                    session.initWarning = "Failed to parse initial JSON: ${e.message}"
                }
            }
        } catch (e: Throwable) {
            session.error = "Failed to create script instance: ${e.message ?: e.javaClass.simpleName}"
        }

        sessions += session
    }

    /** Render all open editor windows. Call from the main UI render loop. */
    fun render() {
        val it = sessions.iterator()
        var index = 0
        while (it.hasNext()) {
            val s = it.next()
            if (!safeIsOpen(s)) {
                cleanupSession(s)
                it.remove()
                continue
            }

            try {
                // Stagger positions a bit
                setNextWindowPos(120f + (index * 20), 120f + (index * 20), ImGuiCond.FirstUseEver)
                setNextWindowSize(520f, 560f, ImGuiCond.FirstUseEver)

                window(title = "Entry Config Editor", flags = WindowFlags.None, open = s.open) {
                    renderSession(this, s)
                }
            } catch (e: Throwable) {
                // If rendering fails, close and log error next frame
                s.error = "Render error: ${e.message ?: e.javaClass.simpleName}"
                s.open.value = false
            }

            index++
        }
    }

    // --- Internals ---

    private fun renderSession(scope: WindowScope, s: Session) {
        with(scope) {
            if (s.error.isNotEmpty()) {
                text("Error: ${s.error}")
                spacing()
                group {
                    text("Close this window and try again.")
                }
                return
            }

            val instance = s.instance
            if (instance == null) {
                text("Error: No script instance available")
                return
            }

            // Controls bar
            group {
                val baselineNames = listOf("Defaults", "Global Defaults")
                val modeNames = listOf("Delta", "Full")

                text("Baseline:")
                sameLine()
                combo("##baseline", s.baselineIdx, baselineNames)

                sameLine()
                text("Mode:")
                sameLine()
                combo("##mode", s.modeIdx, modeNames)

                spacing()
                sameLine()
                button("Reset to Defaults") {
                    resetTo(instance, ConfigOverlaySession.Baseline.Defaults)
                }
                sameLine()
                button("Import Global Defaults") {
                    resetTo(instance, ConfigOverlaySession.Baseline.GlobalDefaults)
                }
            }

            if (s.initWarning?.isNotBlank() == true) {
                spacing()
                textWrapped("Note: ${s.initWarning}")
            }

            separator()

            // Render script config (SessionOnly: do not persist)
            with(ScriptConfigRenderer) {
                renderScriptConfig(instance, ScriptConfigRenderer.PersistenceMode.SessionOnly)
            }

            separator()
            text("Preview JSON:")
            val preview = computeOverlay(instance, s)
            if (preview.error != null) {
                textWrapped("Error generating preview: ${preview.error}")
            } else {
                textWrapped(preview.pretty ?: "{}")
            }

            spacing()
            group {
                button("Save") {
                    val res = computeOverlay(instance, s)
                    if (res.error == null && res.pretty != null) {
                        try {
                            s.onSave(res.pretty)
                        } catch (_: Throwable) {}
                        s.open.value = false
                    }
                }
                sameLine()
                button("Cancel") { s.open.value = false }
            }
        }
    }

    private data class OverlayResult(val obj: JsonObject?, val pretty: String?, val error: String?)

    private fun computeOverlay(instance: ConfigurableScript, s: Session): OverlayResult {
        return try {
            val baseline = if (s.baselineIdx.value == 0) ConfigOverlaySession.Baseline.Defaults else ConfigOverlaySession.Baseline.GlobalDefaults
            val mode = if (s.modeIdx.value == 0) ConfigOverlaySession.Mode.Delta else ConfigOverlaySession.Mode.Full
            val obj = ConfigOverlaySession.overlay(instance, baseline, mode)
            val pretty = prettyGson.toJson(obj)
            OverlayResult(obj, pretty, null)
        } catch (e: Throwable) {
            OverlayResult(null, null, e.message ?: e.javaClass.simpleName)
        }
    }

    private fun resetTo(target: ConfigurableScript, baseline: ConfigOverlaySession.Baseline) {
        try {
            val clazz = target.javaClass.asSubclass(ConfigurableScript::class.java)
            val base = ConfigOverlaySession.createScriptInstance(clazz, baseline).getOrNull()
            if (base != null) copyConfigValues(base, target)
        } catch (_: Throwable) { }
    }

    private fun copyConfigValues(from: Any, to: Any) {
        try {
            val fields = from.javaClass.declaredFields
            for (f in fields) {
                try {
                    f.isAccessible = true
                    val src = f.get(from)
                    val dst = f.get(to)
                    if (src is ConfigItem<*> && dst is ConfigItem<*> && src !is InfoDisplayConfigItem && dst !is InfoDisplayConfigItem) {
                        @Suppress("UNCHECKED_CAST")
                        (dst as ConfigItem<Any?>).value = src.value
                    }
                } catch (_: Throwable) { }
            }
            // Do not persist; just in-memory update
            ScriptConfigRenderer.invokeOnConfigUpdated(to)
        } catch (_: Throwable) { }
    }

    private fun safeIsOpen(s: Session): Boolean {
        return try { s.open.value } catch (_: Throwable) { false }
    }

    private fun cleanupSession(s: Session) {
        try { s.open.close() } catch (_: Throwable) {}
        try { s.baselineIdx.close() } catch (_: Throwable) {}
        try { s.modeIdx.close() } catch (_: Throwable) {}
    }
}
