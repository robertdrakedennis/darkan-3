package com.undercut.ui.backend.dsl.commands

import com.undercut.ui.backend.native.NativeBridge
import java.util.concurrent.atomic.AtomicReference

/**
 * Commands for querying ImGui state and getting information
 */

// Query commands for items
data class IsItemHoveredQuery(val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isItemHovered())
    }
}

data class IsItemClickedQuery(
    val mouseButton: Int = 0,
    val result: AtomicReference<Boolean>
) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isItemClicked(mouseButton))
    }
}

data class IsItemActiveQuery(val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isItemActive())
    }
}

data class IsItemFocusedQuery(val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isItemFocused())
    }
}

data class IsItemVisibleQuery(val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isItemVisible())
    }
}

data class GetItemRectMinQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val pos = NativeBridge.getItemRectMin()
        result.set(pos)
    }
}

data class GetItemRectMaxQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val pos = NativeBridge.getItemRectMax()
        result.set(pos)
    }
}

data class GetItemRectSizeQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val size = NativeBridge.getItemRectSize()
        result.set(size)
    }
}

// Query commands for window
data class GetWindowPosQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val pos = NativeBridge.getWindowPos()
        result.set(pos)
    }
}

data class GetWindowSizeQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val size = NativeBridge.getWindowSize()
        result.set(size)
    }
}

data class GetContentRegionAvailQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val avail = NativeBridge.getContentRegionAvail()
        result.set(avail)
    }
}

// Query commands for mouse
data class GetMousePosQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val pos = NativeBridge.getMousePos()
        result.set(pos)
    }
}

data class IsMouseDownQuery(val button: Int, val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isMouseDown(button))
    }
}

data class IsMouseClickedQuery(val button: Int, val repeat: Boolean = false, val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isMouseClicked(button, repeat))
    }
}

data class IsMouseDoubleClickedQuery(val button: Int, val result: AtomicReference<Boolean>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isMouseDoubleClicked(button))
    }
}

// Query commands for cursor and layout
data class GetCursorPosQuery(val result: AtomicReference<Pair<Float, Float>>) : ImGuiCommand() {
    override fun execute() {
        val pos = NativeBridge.getCursorPos()
        result.set(pos)
    }
}

data class GetColumnWidthQuery(val columnIndex: Int, val result: AtomicReference<Float>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.getColumnWidth(columnIndex))
    }
}

// Scroll query commands
data class GetScrollYCommand(val result: AtomicReference<Float>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.getScrollY())
    }
}

data class GetScrollMaxYCommand(val result: AtomicReference<Float>) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.getScrollMaxY())
    }
}

data class SetScrollHereYCommand(val ratio: Float) : ImGuiCommand() {
    override fun execute() {
        NativeBridge.setScrollHereY(ratio)
    }
}

// Popup query commands
data class IsPopupOpenQuery(
    val id: String,
    val flags: Int = 0,
    val result: AtomicReference<Boolean>
) : ImGuiCommand() {
    override fun execute() {
        result.set(NativeBridge.isPopupOpen(id, flags))
    }
}