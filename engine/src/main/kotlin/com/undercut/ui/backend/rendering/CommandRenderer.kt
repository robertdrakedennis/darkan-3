package com.undercut.ui.backend.rendering

import com.undercut.ui.backend.dsl.ImGuiState
import com.undercut.ui.backend.dsl.commands.ImGuiCommand
import com.undercut.ui.backend.dsl.commands.ImGuiExecState
import java.util.concurrent.ConcurrentHashMap

/**
 * Optimized command renderer using ring buffer and object pooling.
 * Reduces allocations and improves performance compared to the original implementation.
 */
object CommandRenderer {
    private val commandBuffer = CommandBuffer(capacity = 16384)
    private val sharedStates = ConcurrentHashMap<String, ImGuiState<*>>(64)
    
    // Thread-local capture sink for off-thread precompute
    private val captureSink: ThreadLocal<MutableList<ImGuiCommand>?> = ThreadLocal.withInitial { null }
    
    // Use simple array for click states with hash-based indexing
    private val clickStates = BooleanArray(256)
    private val clickStateKeys = Array<String?>(256) { null }
    
    // Pre-compile frequently used lambdas to avoid allocation
    private val commandExecutor: (ImGuiCommand) -> Unit = { command ->
        executeCommand(command)
    }
    
    /**
     * Submit commands for rendering in the next frame.
     * Uses batch submission for better performance.
     */
    fun submitCommands(commands: List<ImGuiCommand>) {
        // If a capture sink is present (background precompute), append there instead of enqueuing
        val sink = captureSink.get()
        if (sink != null) {
            sink.addAll(commands)
            return
        }
        if (!commandBuffer.offerBatch(commands)) {
            // Buffer full, fall back to individual offers
            for (command in commands) {
                if (!commandBuffer.offer(command)) {
                    System.err.println("[OptimizedCommandRenderer] Command buffer full, dropping commands")
                    break
                }
            }
        }
    }
    
    /**
     * Execute all queued commands - called from EGLSwapBuffers hook.
     * Optimized to minimize allocations and improve cache locality.
     */
    fun executeQueuedCommands() {
        if (commandBuffer.isEmpty()) return
        
        // Process all commands in one pass
        commandBuffer.drain(commandExecutor)
    }
    
    /**
     * Execute a precomputed list of commands directly (no re-queuing).
     */
    fun executePrecomputed(commands: List<ImGuiCommand>) {
        if (commands.isEmpty()) return
        for (i in 0 until commands.size) {
            val cmd = commands[i]
            try {
                if (!ImGuiExecState.isSkipping() || cmd.isStructural()) {
                    cmd.execute()
                }
            } catch (e: Throwable) {
                System.err.println("ImGui command failed: ${cmd.javaClass.simpleName} - ${e.message}")
            }
        }
    }
    
    /**
     * Execute a single command with proper error handling
     */
    private fun executeCommand(command: ImGuiCommand) {
        try {
            if (!ImGuiExecState.isSkipping() || command.isStructural()) {
                command.execute()
            }
        } catch (e: Throwable) {
            System.err.println("ImGui command failed: ${command.javaClass.simpleName} - ${e.message}")
        }
    }

    /**
     * Get or create a shared state for a given key
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getSharedState(key: String, factory: () -> ImGuiState<T>): ImGuiState<T> {
        return sharedStates.getOrPut(key, factory) as ImGuiState<T>
    }
    
    /**
     * Clear all shared states and reset buffers
     */
    fun cleanup() {
        sharedStates.values.forEach { it.close() }
        sharedStates.clear()
        commandBuffer.clear()
        clickStates.fill(false)
        clickStateKeys.fill(null)
        CommandPool.clear()
    }
    
    /**
     * Run a block with a capture sink set to collect commands instead of enqueuing.
     */
    fun <R> withCapture(target: MutableList<ImGuiCommand>, block: () -> R): R {
        captureSink.set(target)
        return try {
            block()
        } finally {
            captureSink.set(null)
        }
    }
}