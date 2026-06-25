package com.undercut.ui.highlight

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Priority
import com.undercut.game.math.Vector2f
import com.undercut.game.nxt.MainState
import com.undercut.ui.UI
import com.undercut.ui.UIState
import com.undercut.ui.backend.dsl.ImGuiDsl.backgroundDrawList
import com.undercut.ui.backend.dsl.utils.ImGuiColors
import com.undercut.ui.backend.rendering.ImGUIRender

/**
 * While the Interface Debug tab is open, faintly outlines the component currently selected there
 * (interface + component id), so it's easy to locate on screen — deliberately lighter and calmer than
 * the quest highlighter (soft white, low alpha, thin, no pulse) so the two never get confused.
 *
 * If only an interface is selected (no component), outlines its root component (0).
 */
object InterfaceDebugHighlightRenderer {

    @JvmStatic
    @ImGUIRender(priority = Priority.LOW)
    fun render() {
        if (!UIState.showMainWindow.value || UIState.selectedTab != UI.Tab.INTERFACE_DEBUG) return
        try {
            if (Bootstrap.client.mainState != MainState.LOGGED_IN) return
        } catch (_: Throwable) {
            return
        }

        val ifId = UIState.interfaceDebugInterfaceId.value
        if (ifId < 0) return
        val compId = if (UIState.interfaceDebugComponentId.value >= 0) UIState.interfaceDebugComponentId.value else 0

        val list = try { Bootstrap.client.interfaceList } catch (_: Throwable) { return }
        if (!(try { list.isOpen(ifId) } catch (_: Throwable) { false })) return
        val comp = try { list.getComponent(ifId, compId) } catch (_: Throwable) { return } ?: return
        val sr = try { comp.screenRect } catch (_: Throwable) { return } ?: return
        if (sr.width <= 0 || sr.height <= 0) return

        backgroundDrawList {
            val tl = Vector2f(sr.x.toFloat() - 1f, sr.y.toFloat() - 1f)
            val br = Vector2f((sr.x + sr.width).toFloat() + 1f, (sr.y + sr.height).toFloat() + 1f)
            val outline = (ImGuiColors.WHITE and 0x00FFFFFF) or (150 shl 24)
            val fill = (ImGuiColors.WHITE and 0x00FFFFFF) or (28 shl 24)
            rectFilled(tl, br, fill, rounding = 3f)
            rect(tl, br, outline, rounding = 3f, thickness = 1.5f)
            text(Vector2f(sr.x.toFloat() + 3f, sr.y.toFloat() - 15f), outline, "$ifId:$compId")
        }
    }
}
