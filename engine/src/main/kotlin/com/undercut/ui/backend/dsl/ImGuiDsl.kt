package com.undercut.ui.backend.dsl

import com.undercut.ui.backend.dsl.commands.*
import com.undercut.ui.backend.dsl.scopes.BackgroundDrawListScope
import com.undercut.ui.backend.dsl.scopes.WindowScope
import com.undercut.ui.backend.flags.ImGuiCond
import com.undercut.ui.backend.flags.WindowFlags
import com.undercut.ui.backend.native.NativeBridge
import com.undercut.ui.backend.rendering.CommandRenderer

// Import command classes from ImGuiExtensions.kt - these will be available after compilation
// Note: These are forward references to classes defined in ImGuiExtensions.kt

/**
 * Safe ImGui DSL that builds command queues for execution in render context.
 * 
 * This DSL builds commands that are executed later during EGLSwapBuffers hook
 * when a valid OpenGL context is available, preventing crashes and undefined behavior.
 */
object ImGuiDsl {
    

    /**
     * Get a persistent state that survives across script restarts.
     * Useful for maintaining UI state between script reloads.
     */
    fun <T> persistentState(key: String, factory: () -> ImGuiState<T>): ImGuiState<T> {
        return CommandRenderer.getSharedState(key, factory)
    }
    
    // Window management with automatic Begin/End and command building
    @DslMarker
    annotation class ImGuiDsl
    
    // Window scope is now imported from scopes package
    // WindowScope definition moved to scopes/WindowScope.kt
    
    // Main window function with scope
    inline fun window(
        title: String, 
        flags: WindowFlags = WindowFlags.None,
        open: ImGuiState<Boolean>? = null,
        block: WindowScope.() -> Unit
    ) {
        val commands = mutableListOf<ImGuiCommand>()
        
        // Window positioning/sizing commands go first
        commands.addAll(flushNextWindowCommands())
        
        // Begin window - pass ImGuiState directly instead of creating AtomicReference
        commands.add(BeginWindowCommand(title, flags.value, open))
        
        // Content commands
        val scope = WindowScope()
        scope.block()
        commands.addAll(scope.commands)
        
        // Ensure structural integrity: balance any missing ends for child/menu/combo/table
        // The structural command execution will skip closing if the corresponding begin didn't open.
        // This prevents ImGui Missing End() errors if DSL users exit early.
        commands.add(EndWindowCommand)
        
        // Submit to renderer
        CommandRenderer.submitCommands(commands)
        
        // No need to update open state - it's now directly modified by C++ through the persistent buffer
    }

    // Overload: window with action-value open state
    inline fun window(
        title: String,
        flags: WindowFlags = WindowFlags.None,
        open: Boolean,
        noinline onOpenChange: (Boolean) -> Unit,
        block: WindowScope.() -> Unit
    ) {
        val commands = mutableListOf<ImGuiCommand>()

        commands.addAll(flushNextWindowCommands())
        commands.add(BeginWindowActionValueCommand(title, flags.value, open, onOpenChange))

        val scope = WindowScope()
        scope.block()
        commands.addAll(scope.commands)

        commands.add(EndWindowCommand)
        CommandRenderer.submitCommands(commands)
    }
    
    // Window positioning and sizing - these affect the next window()
    private val nextWindowCommands = mutableListOf<ImGuiCommand>()
    
    fun setNextWindowPos(x: Float, y: Float, cond: ImGuiCond = ImGuiCond.None, pivotX: Float = 0f, pivotY: Float = 0f) {
        nextWindowCommands.add(SetNextWindowPosCommand(x, y, cond.value, pivotX, pivotY, useSafeBounds = true))
    }
    
    fun setNextWindowPosUnsafe(x: Float, y: Float, cond: ImGuiCond = ImGuiCond.None, pivotX: Float = 0f, pivotY: Float = 0f) {
        nextWindowCommands.add(SetNextWindowPosCommand(x, y, cond.value, pivotX, pivotY, useSafeBounds = false))
    }
    
    fun setNextWindowSize(width: Float, height: Float, cond: ImGuiCond = ImGuiCond.None) {
        nextWindowCommands.add(SetNextWindowSizeCommand(width, height, cond.value, useSafeBounds = true))
    }
    
    fun setNextWindowSizeUnsafe(width: Float, height: Float, cond: ImGuiCond = ImGuiCond.None) {
        nextWindowCommands.add(SetNextWindowSizeCommand(width, height, cond.value, useSafeBounds = false))
    }
    
    // Flush any pending window setup commands
    @PublishedApi
    internal fun flushNextWindowCommands(): List<ImGuiCommand> {
        val commands = nextWindowCommands.toList()
        nextWindowCommands.clear()
        return commands
    }
    
    // Utility functions
    fun framerate(): Float = 60.0f // Placeholder - would need actual IO access
    
    fun getDisplaySize(): Pair<Float, Float> {
        return try {
            NativeBridge.getDisplaySize()
        } catch (e: Exception) {
            Pair(1920.0f, 1080.0f) // Safe default
        }
    }
    
    fun displayWidth(): Float = getDisplaySize().first
    fun displayHeight(): Float = getDisplaySize().second

    // Safe window positioning utilities
    fun centerNextWindow(width: Float = 400f, height: Float = 300f) {
        val (displayWidth, displayHeight) = getDisplaySize()
        val x = (displayWidth - width) * 0.5f
        val y = (displayHeight - height) * 0.5f
        
        setNextWindowPos(x, y)
        setNextWindowSize(width, height)
    }
    
    fun setNextWindowPosTopLeft(margin: Float = 20f) {
        setNextWindowPos(margin, margin)
    }
    
    fun setNextWindowPosTopRight(width: Float = 400f, margin: Float = 20f) {
        val displayWidth = displayWidth()
        setNextWindowPos(displayWidth - width - margin, margin)
    }
    
    // Background DrawList function
    inline fun backgroundDrawList(block: BackgroundDrawListScope.() -> Unit) {
        val scope = BackgroundDrawListScope()
        scope.block()
        
        if (scope.drawCommands.isNotEmpty()) {
            val command = BackgroundDrawListScopeCommand(scope.drawCommands.toList())
            CommandRenderer.submitCommands(listOf(command))
        }
    }
}