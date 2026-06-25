package com.undercut.ui.highlight

import com.undercut.game.interfaces.IFSlot
import com.undercut.ui.backend.dsl.utils.ImGuiColors
import java.util.concurrent.ConcurrentHashMap

/**
 * Public API for drawing cyan-by-default highlights on top of game interface components.
 *
 * Usage from anywhere (script, quest helper, debugger):
 *   InterfaceHighlight.add("my-key", IFSlot(1188, 33))                          // dialog option row
 *   InterfaceHighlight.add("buy-button", IFSlot(620, 25), color = 0xFF00FF00.toInt())
 *   InterfaceHighlight.add("slide-puzzle-piece-3", IFSlot(363, 9, 3),
 *       label = "Drag here", style = HighlightStyle(color = ImGuiColors.YELLOW))
 *   InterfaceHighlight.remove("my-key")
 *   InterfaceHighlight.clear()
 *
 * Each registration is keyed by a string so callers can update or remove their entries
 * without tracking state. Adding the same key replaces the existing entry. Entries persist
 * across frames until removed.
 *
 * The renderer ([InterfaceHighlightRenderer]) reads each registered slot's live screen rect
 * from the InterfaceComponent struct and paints a pulsing outline + optional chevron + label.
 * When a component reports no real rect (some child components don't get absolute coords
 * populated) the entry is skipped silently.
 */
object InterfaceHighlight {
    internal val entries: MutableMap<String, Entry> = ConcurrentHashMap()

    fun add(key: String, slot: IFSlot, style: HighlightStyle = HighlightStyle.DEFAULT) {
        entries[key] = Entry(slot, style)
    }

    fun add(
        key: String,
        slot: IFSlot,
        color: Int = ImGuiColors.CYAN,
        label: String? = null,
        pulse: Boolean = true,
        chevron: Boolean = true,
    ) {
        entries[key] = Entry(slot, HighlightStyle(color = color, label = label, pulse = pulse, chevron = chevron))
    }

    fun remove(key: String) { entries.remove(key) }

    fun clear() { entries.clear() }

    fun isHighlighted(key: String): Boolean = entries.containsKey(key)

    internal data class Entry(val slot: IFSlot, val style: HighlightStyle)
}

data class HighlightStyle(
    val color: Int = ImGuiColors.CYAN,
    val label: String? = null,
    val pulse: Boolean = true,
    val chevron: Boolean = true,
    val thickness: Float = 2.5f,
) {
    companion object { val DEFAULT = HighlightStyle() }
}
