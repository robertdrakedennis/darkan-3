package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.ImGuiState
import com.undercut.ui.backend.dsl.boolState
import com.undercut.ui.backend.dsl.commands.*
import com.undercut.ui.backend.dsl.utils.ImGuiTreeNodeFlags
import com.undercut.ui.backend.rendering.CommandRenderer
import java.util.concurrent.atomic.AtomicReference

/**
 * Container widget scopes for tabs, trees, listboxes, and combos
 */

@ImGuiDsl.ImGuiDsl
class TabBarScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    inline fun tabItem(label: String, block: ChildScope.() -> Unit): Boolean {
        val key = "last_tabItem_${label}"
        val last = CommandRenderer.getSharedState(key) { boolState(false) }
        val result = AtomicReference(false)
        commands.add(BeginTabItemCommand(label, null, 0, result))
        
        val scope = ChildScope()
        scope.block()
        commands.addAll(scope.commands)
        
        commands.add(EndTabItemCommand())
        commands.add(SetBoolStateFromRefCommand(last, result))
        return last.value
    }

    inline fun tabItemButton(label: String, flags: Int = 0, crossinline onClick: () -> Unit) {
        commands.add(TabItemButtonActionCommand(label, flags) { onClick() })
    }
}

@ImGuiDsl.ImGuiDsl
class TreeScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun text(text: String) {
        commands.add(TextCommand(text))
    }
    
    inline fun treeNode(label: String, flags: Int = ImGuiTreeNodeFlags.None, block: TreeScope.() -> Unit): Boolean {
        val result = AtomicReference(false)
        commands.add(TreeNodeCommand(label, flags, result))
        
        val scope = TreeScope()
        scope.block()
        commands.addAll(scope.commands)
        
        // TreeNode requires EndTreeNodeCommand to call treePop when open
        commands.add(EndTreeNodeCommand())
        return result.get()
    }
}

@ImGuiDsl.ImGuiDsl
class ListBoxScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun selectable(label: String, selected: ImGuiState<Boolean>): Boolean {
        commands.add(SelectableCommand(label, selected))
        return selected.value
    }
    
    fun text(text: String) {
        commands.add(TextCommand(text))
    }
}

@ImGuiDsl.ImGuiDsl
class ComboScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun selectable(label: String, selected: ImGuiState<Boolean>): Boolean {
        commands.add(SelectableCommand(label, selected))
        return selected.value
    }

    // Action-enabled selectable that triggers a callback when clicked
    fun selectable(
        label: String,
        selected: ImGuiState<Boolean>,
        onClick: () -> Unit
    ) {
        commands.add(SelectableActionCommand(label, selected, onClick))
    }

    // Safe selectable without persistent state, avoids leaks in tight render loops
    fun selectable(
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        commands.add(SelectableActionValueCommand(label, isSelected, onClick))
    }
    
    fun setItemDefaultFocus() {
        commands.add(SetItemDefaultFocusCommand())
    }
    
    fun text(text: String) {
        commands.add(TextCommand(text))
    }
}