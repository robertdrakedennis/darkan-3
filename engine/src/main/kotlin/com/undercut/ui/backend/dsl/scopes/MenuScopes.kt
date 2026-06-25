package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.BooleanState
import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.boolState
import com.undercut.ui.backend.dsl.commands.*
import com.undercut.ui.backend.rendering.CommandRenderer
import java.util.concurrent.atomic.AtomicReference

/**
 * Menu-related scopes for menu bars, menus, and context menus
 */

@ImGuiDsl.ImGuiDsl
class MenuScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun menuItem(label: String, shortcut: String = "", selected: Boolean = false): Boolean {
        val result = AtomicReference(false)
        commands.add(MenuItemCommand(label, shortcut, selected, result))
        return result.get()
    }

    // Action variant that triggers on click during execution
    fun menuItem(label: String, shortcut: String = "", selected: Boolean = false, onClick: () -> Unit) {
        commands.add(MenuItemActionCommand(label, shortcut, selected, onClick))
    }
    
    fun separator() {
        commands.add(SeparatorCommand)
    }
    
    inline fun menu(label: String, block: MenuScope.() -> Unit): Boolean {
        val key = "last_child_menu_${label}"
        val last = CommandRenderer.getSharedState(key) { boolState(false) }
        val menuResult = AtomicReference(false)
        commands.add(BeginMenuCommand(label, menuResult))
        
        val scope = MenuScope()
        scope.block()
        commands.addAll(scope.commands)
        
        commands.add(EndMenuCommand())
        commands.add(SetBoolStateFromRefCommand(last, menuResult))
        return last.value
    }
}

@ImGuiDsl.ImGuiDsl
class ContextMenuScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    // Lightweight scope optimized for context menus
    fun menuItem(label: String, shortcut: String = "", selected: Boolean = false): Boolean {
        val result = AtomicReference(false)
        commands.add(MenuItemCommand(label, shortcut, selected, result))
        return result.get()
    }

    // Action variant that triggers on click during execution
    fun menuItem(label: String, shortcut: String = "", selected: Boolean = false, onClick: () -> Unit) {
        commands.add(MenuItemActionCommand(label, shortcut, selected, onClick))
    }

    fun separator() {
        commands.add(SeparatorCommand)
    }

    fun text(text: String) {
        commands.add(TextCommand(text))
    }

    /** Runs [block] each frame the popup is actually open (skipped while closed). */
    fun whileShown(block: () -> Unit) {
        commands.add(CallbackCommand(block))
    }

    // Nested menu support for context menus
    inline fun menu(label: String, block: ContextMenuScope.() -> Unit): Boolean {
        val menuResult = AtomicReference(false)
        commands.add(BeginMenuCommand(label, menuResult))
        
        val scope = ContextMenuScope()
        scope.block()
        commands.addAll(scope.commands)
        
        commands.add(EndMenuCommand())
        return menuResult.get()
    }
    
    // Selectable items for context menus
    fun selectable(label: String, selected: Boolean = false): Boolean {
        val selectedState = BooleanState(selected)
        val resultState = boolState(false)
        commands.add(SelectableCommand(label, selectedState))
        return resultState.value
    }
    
    fun closeCurrentPopup() {
        commands.add(CloseCurrentPopupCommand())
    }
}
