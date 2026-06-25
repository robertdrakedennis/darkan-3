package com.undercut.ui.highlight

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Priority
import com.undercut.game.interfaces.IFSlot
import com.undercut.game.math.Vector2f
import com.undercut.game.nxt.MainState
import com.undercut.game.nxt.interfaces.ScreenRect
import com.undercut.quest.overlay.pulseAlpha
import com.undercut.ui.backend.dsl.ImGuiDsl.backgroundDrawList
import com.undercut.ui.backend.dsl.scopes.BackgroundDrawListScope
import com.undercut.ui.backend.rendering.ImGUIRender

object InterfaceHighlightRenderer {
    @JvmStatic
    @ImGUIRender(priority = Priority.LOW)
    fun render() {
        if (InterfaceHighlight.entries.isEmpty()) return
        try {
            if (Bootstrap.client.mainState != MainState.LOGGED_IN) return
        } catch (_: Throwable) { return }

        backgroundDrawList {
            for ((_, entry) in InterfaceHighlight.entries) {
                try {
                    drawEntry(entry)
                } catch (_: Throwable) {
                    // Never let one bad entry kill the loop. Individual failures (stale ptr,
                    // missing component, etc.) just skip that highlight for the frame.
                }
            }
        }
    }

    private fun BackgroundDrawListScope.drawEntry(entry: InterfaceHighlight.Entry) {
        val slot = entry.slot
        val sr = resolveComponentRect(slot) ?: return
        val style = entry.style

        val outline = if (style.pulse) pulseAlpha(style.color, minAlpha = 200, maxAlpha = 255) else style.color
        val fill = if (style.pulse) pulseAlpha(style.color, minAlpha = 40, maxAlpha = 90)
                   else (style.color and 0x00FFFFFF) or (0x40 shl 24)

        val tl = Vector2f(sr.x.toFloat() - 2f, sr.y.toFloat() - 2f)
        val br = Vector2f((sr.x + sr.width).toFloat() + 2f, (sr.y + sr.height).toFloat() + 2f)
        rectFilled(tl, br, fill, rounding = 4f)
        rect(tl, br, outline, rounding = 4f, thickness = style.thickness)

        if (style.chevron) {
            val cy = (sr.y + sr.height * 0.5f).toFloat()
            val chev = 12f
            val tip = Vector2f(sr.x.toFloat() - 4f, cy)
            val up = Vector2f(tip.x - chev, tip.y - chev * 0.6f)
            val down = Vector2f(tip.x - chev, tip.y + chev * 0.6f)
            convexPolyFilled(floatArrayOf(tip.x, tip.y, up.x, up.y, down.x, down.y), outline)
        }

        style.label?.let { label ->
            text(Vector2f(sr.x.toFloat() + 6f, sr.y.toFloat() - 16f), outline, label)
        }
    }

    // Pulls the per-frame screen rect captured by the engine's InterfaceComponentRectCapture
    // hook on the layout function. Returns null when the component isn't drawn this frame
    // (e.g. the interface is closed) or hasn't been laid out yet.
    //
    // Gates on isOpen first — getComponentRaw skips the visibility filter and can read freed
    // memory of a closed-but-still-cached interface (a major source of native SIGSEGVs after
    // teleports or area changes).
    //
    // When [IFSlot.slotId] >= 0, walks the component's slotChildren for the matching slot
    // (item containers, slide puzzles, etc.) so the outline frames the individual cell
    // rather than the entire grid.
    private fun resolveComponentRect(slot: IFSlot): ScreenRect? {
        val list = try { Bootstrap.client.interfaceList } catch (_: Throwable) { return null }
        val isOpen = try { list.isOpen(slot.interfaceId) } catch (_: Throwable) { false }
        if (!isOpen) return null
        val comp = try { list.getComponent(slot.interfaceId, slot.componentId) } catch (_: Throwable) { return null }
            ?: return null
        val parentRect = runCatching { comp.screenRect }.getOrNull()
        if (slot.slotId < 0) return parentRect

        val children = runCatching { comp.slotChildren }.getOrNull().orEmpty()
        val byId = children.firstOrNull { runCatching { it.slotId == slot.slotId }.getOrDefault(false) }
        val byIndex = if (byId == null) children.getOrNull(slot.slotId) else null
        val target = byId ?: byIndex
        return target?.let { runCatching { it.screenRect }.getOrNull() } ?: parentRect
    }
}
