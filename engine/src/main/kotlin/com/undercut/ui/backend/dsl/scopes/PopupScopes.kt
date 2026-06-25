package com.undercut.ui.backend.dsl.scopes

import com.undercut.ui.backend.dsl.ImGuiDsl
import com.undercut.ui.backend.dsl.ImGuiState
import com.undercut.ui.backend.dsl.commands.*

/**
 * Popup and modal-related scopes for dialogs and overlay windows
 */

@ImGuiDsl.ImGuiDsl
class PopupScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun text(text: String) {
        commands.add(TextCommand(text))
    }
    
    fun button(label: String, onClick: () -> Unit) {
        commands.add(ButtonCommand(label, 0f, 0f, onClick))
    }
    
    fun separator() {
        commands.add(SeparatorCommand)
    }
    
    fun closeCurrentPopup() {
        commands.add(CloseCurrentPopupCommand())
    }
}

@ImGuiDsl.ImGuiDsl
class ModalScope {
    @PublishedApi
    internal val commands = mutableListOf<ImGuiCommand>()
    
    fun text(text: String) {
        commands.add(TextCommand(text))
    }
    
    fun button(label: String, onClick: () -> Unit) {
        commands.add(ButtonCommand(label, 0f, 0f, onClick))
    }
    
    fun separator() {
        commands.add(SeparatorCommand)
    }
    
    fun spacing() {
        commands.add(SpacingCommand)
    }
    
    fun sameLine() {
        commands.add(SameLineCommand)
    }
    
    fun inputText(label: String, state: ImGuiState<String>) {
        commands.add(InputTextCommand(label, state, 0))
    }
    
    fun closeCurrentPopup() {
        commands.add(CloseCurrentPopupCommand())
    }
    
    inline fun okCancelButtons(
        crossinline onOk: () -> Unit,
        crossinline onCancel: () -> Unit
    ) {
        button("OK") {
            onOk()
            closeCurrentPopup()
        }
        sameLine()
        button("Cancel") {
            onCancel()
            closeCurrentPopup()
        }
    }
}