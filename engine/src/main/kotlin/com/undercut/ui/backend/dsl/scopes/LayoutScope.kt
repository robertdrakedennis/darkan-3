package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.commands.ImGuiCommand

/**
 * Common scope for ImGui DSL containers.
 * Implemented by both WindowScope and ChildScope to enable shared extensions.
 */
internal interface LayoutScope {
    val commands: MutableList<ImGuiCommand>
}


