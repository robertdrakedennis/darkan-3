package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.commands.ImGuiCommand

/**
 * Child window scope - relies on shared ImGuiScope extensions for common widgets.
 */
@ImGuiDsl.ImGuiDsl
class ChildScope : LayoutScope {
    override val commands = mutableListOf<ImGuiCommand>()
    // All common queries/widgets come from LayoutScope extensions
}