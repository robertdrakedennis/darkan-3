package com.undercut.ui.backend.dsl.utils

import com.undercut.ui.backend.dsl.utils.ImGuiColors.hex
import com.undercut.ui.backend.native.NativeBridge

object ModernTheme {
    
    fun applyModernTheme() {
        with(NativeBridge) {
            // === Text ===
            pushStyleColor(ImGuiCol.Text, ImGuiColors.TEXT_PRIMARY)
            pushStyleColor(ImGuiCol.TextDisabled, ImGuiColors.TEXT_DISABLED)

            // === Backgrounds ===
            pushStyleColor(ImGuiCol.WindowBg, hex("#1A1410"))         // Dark brown base
            pushStyleColor(ImGuiCol.ChildBg, hex("#1E180E26"))        // Slightly lighter, semi-transparent
            pushStyleColor(ImGuiCol.PopupBg, hex("#1E1610"))          // Popup dark brown
            pushStyleColor(ImGuiCol.Border, hex("#5A402880"))         // Warm brown border
            pushStyleColor(ImGuiCol.BorderShadow, hex("#00000040"))   // Subtle dark shadow

            // === Frame (inputs, checkboxes, sliders) ===
            pushStyleColor(ImGuiCol.FrameBg, hex("#28201A"))          // Dark brown input bg
            pushStyleColor(ImGuiCol.FrameBgHovered, hex("#3A2E22"))   // Lighter on hover
            pushStyleColor(ImGuiCol.FrameBgActive, hex("#4A3A2A"))    // Lighter on active

            // === Title Bar ===
            pushStyleColor(ImGuiCol.TitleBg, hex("#221A12"))          // Dark brown
            pushStyleColor(ImGuiCol.TitleBgActive, hex("#3A2818"))    // Medium brown when focused
            pushStyleColor(ImGuiCol.TitleBgCollapsed, hex("#1A1410")) // Darkest when collapsed

            // === Menu Bar ===
            pushStyleColor(ImGuiCol.MenuBarBg, hex("#2A201860"))      // Semi-transparent brown

            // === Scrollbar ===
            pushStyleColor(ImGuiCol.ScrollbarBg, hex("#14100A55"))    // Very dark brown
            pushStyleColor(ImGuiCol.ScrollbarGrab, hex("#6A5028"))    // Golden brown grab
            pushStyleColor(ImGuiCol.ScrollbarGrabHovered, hex("#8A6830"))
            pushStyleColor(ImGuiCol.ScrollbarGrabActive, hex("#A88038"))

            // === Interactive accents ===
            pushStyleColor(ImGuiCol.CheckMark, hex("#C89830"))        // Gold checkmark
            pushStyleColor(ImGuiCol.SliderGrab, hex("#C89830"))       // Gold slider
            pushStyleColor(ImGuiCol.SliderGrabActive, hex("#DAAC40"))

            // === Buttons ===
            pushStyleColor(ImGuiCol.Button, hex("#3A2C1C"))           // Dark brown button
            pushStyleColor(ImGuiCol.ButtonHovered, hex("#4A3C28"))    // Lighter on hover
            pushStyleColor(ImGuiCol.ButtonActive, hex("#5A4830"))     // Lighter on press

            // === Headers (collapsing, tree nodes, selectables) ===
            pushStyleColor(ImGuiCol.Header, hex("#28201A33"))         // Subtle brown for selected selectables
            pushStyleColor(ImGuiCol.HeaderHovered, hex("#C8983025"))  // Gold tinted hover
            pushStyleColor(ImGuiCol.HeaderActive, hex("#C8983018"))   // Subtle gold active

            // === Separators ===
            pushStyleColor(ImGuiCol.Separator, ImGuiColors.BORDER_DEFAULT)
            pushStyleColor(ImGuiCol.SeparatorHovered, hex("#8A683055"))
            pushStyleColor(ImGuiCol.SeparatorActive, hex("#C89830"))

            // === Resize Grip ===
            pushStyleColor(ImGuiCol.ResizeGrip, hex("#5A402833"))
            pushStyleColor(ImGuiCol.ResizeGripHovered, hex("#8A683055"))
            pushStyleColor(ImGuiCol.ResizeGripActive, hex("#C89830"))

            // === Tabs ===
            pushStyleColor(ImGuiCol.Tab, hex("#2A201818"))            // Very subtle brown
            pushStyleColor(ImGuiCol.TabHovered, hex("#4A3A2A55"))     // Brown hover
            pushStyleColor(ImGuiCol.TabSelected, hex("#3A2C1C44"))    // Selected brown
            pushStyleColor(ImGuiCol.TabDimmed, hex("#2A201811"))      // Dimmed tab
            pushStyleColor(ImGuiCol.TabDimmedSelected, hex("#3A2C1C33"))
            pushStyleColor(ImGuiCol.TabSelectedOverline, hex("#C8983055")) // Gold overline
            pushStyleColor(ImGuiCol.TabDimmedSelectedOverline, hex("#8A683033"))

            // === Tables ===
            pushStyleColor(ImGuiCol.TableHeaderBg, hex("#3A2C1C44"))  // Brown header
            pushStyleColor(ImGuiCol.TableBorderStrong, ImGuiColors.BORDER_DEFAULT)
            pushStyleColor(ImGuiCol.TableBorderLight, hex("#4A382840"))
            pushStyleColor(ImGuiCol.TableRowBg, hex("#28201A33"))     // Alternating brown rows
            pushStyleColor(ImGuiCol.TableRowBgAlt, hex("#2A221A55"))

            // === Misc ===
            pushStyleColor(ImGuiCol.TextSelectedBg, hex("#C8983033")) // Gold text selection
            pushStyleColor(ImGuiCol.DragDropTarget, hex("#C8983055"))
            pushStyleColor(ImGuiCol.NavWindowingHighlight, hex("#C8983055"))
            pushStyleColor(ImGuiCol.NavWindowingDimBg, ImGuiColors.OVERLAY_DARK)
            pushStyleColor(ImGuiCol.ModalWindowDimBg, ImGuiColors.OVERLAY_DARK)

            // === Style Variables ===
            pushStyleVarFloat(ImGuiStyleVar.Alpha, 1.0f)
            pushStyleVarVec2(ImGuiStyleVar.WindowPadding, 12f, 12f)
            pushStyleVarFloat(ImGuiStyleVar.WindowRounding, 2f)      // Slight rounding like RS interfaces
            pushStyleVarFloat(ImGuiStyleVar.WindowBorderSize, 2f)
            pushStyleVarFloat(ImGuiStyleVar.ChildRounding, 1f)
            pushStyleVarFloat(ImGuiStyleVar.ChildBorderSize, 1f)
            pushStyleVarFloat(ImGuiStyleVar.PopupRounding, 2f)
            pushStyleVarFloat(ImGuiStyleVar.PopupBorderSize, 1f)
            pushStyleVarVec2(ImGuiStyleVar.FramePadding, 8f, 6f)
            pushStyleVarFloat(ImGuiStyleVar.FrameRounding, 2f)       // Minimal rounding, RS-like
            pushStyleVarFloat(ImGuiStyleVar.FrameBorderSize, 1f)     // Visible frame borders
            pushStyleVarVec2(ImGuiStyleVar.ItemSpacing, 8f, 6f)
            pushStyleVarVec2(ImGuiStyleVar.ItemInnerSpacing, 6f, 4f)
            pushStyleVarFloat(ImGuiStyleVar.IndentSpacing, 20f)
            pushStyleVarVec2(ImGuiStyleVar.CellPadding, 6f, 4f)
            pushStyleVarFloat(ImGuiStyleVar.ScrollbarSize, 14f)
            pushStyleVarFloat(ImGuiStyleVar.ScrollbarRounding, 2f)   // Less round, more RS-like
            pushStyleVarFloat(ImGuiStyleVar.GrabMinSize, 12f)
            pushStyleVarFloat(ImGuiStyleVar.GrabRounding, 2f)
            pushStyleVarFloat(ImGuiStyleVar.TabRounding, 2f)         // Square-ish tabs
            pushStyleVarVec2(ImGuiStyleVar.ButtonTextAlign, 0.5f, 0.5f)
            pushStyleVarVec2(ImGuiStyleVar.SelectableTextAlign, 0f, 0.5f)
        }
    }
    
    /**
     * Remove the applied modern theme styles
     * Call this if you need to revert to default styling
     */
    fun removeModernTheme() {
        with(NativeBridge) {
            // Pop all the style colors we pushed (48 colors)
            popStyleColor(50)
            
            // Pop all the style variables we pushed (19 variables)
            popStyleVar(22)
        }
    }
    
    /**
     * Apply theme for a specific component scope
     */
    inline fun <T> withModernTheme(block: () -> T): T {
        applyModernTheme()
        try {
            return block()
        } finally {
            removeModernTheme()
        }
    }
}