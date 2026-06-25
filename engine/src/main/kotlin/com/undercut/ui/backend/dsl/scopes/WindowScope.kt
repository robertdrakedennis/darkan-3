package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.commands.CloseCurrentPopupCommand
import com.undercut.ui.backend.dsl.commands.ImGuiCommand
import com.undercut.ui.backend.dsl.commands.IsPopupOpenQuery
import com.undercut.ui.backend.dsl.commands.OpenPopupCommand
import com.undercut.ui.backend.flags.PopupFlags
import java.util.concurrent.atomic.AtomicReference

/**
 * Main window scope for ImGui DSL
 */
@ImGuiDsl.ImGuiDsl
class WindowScope : LayoutScope {
    override val commands = mutableListOf<ImGuiCommand>()
    
    // Shared widgets and queries are provided via LayoutScope extensions
    
    // Popup functions
    fun openPopup(id: String, flags: PopupFlags = PopupFlags.None) {
        commands.add(OpenPopupCommand(id, flags.value))
    }
    
    fun closeCurrentPopup() {
        commands.add(CloseCurrentPopupCommand())
    }
    
    fun isPopupOpen(id: String, flags: PopupFlags = PopupFlags.None): Boolean {
        val result = AtomicReference(false)
        commands.add(IsPopupOpenQuery(id, flags.value, result))
        return result.get()
    }
}