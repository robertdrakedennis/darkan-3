package com.undercut.ui.backend.rendering

import com.undercut.ui.backend.dsl.commands.ImGuiCommand
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KClass

/**
 * Object pool for ImGui commands to reduce allocation pressure.
 * Commands are recycled after use to avoid constant object creation.
 */
object CommandPool {
    private val pools = mutableMapOf<KClass<*>, ConcurrentLinkedQueue<Any>>()
    private const val MAX_POOL_SIZE = 100
    
    /**
     * Borrow a command from the pool or create a new one if pool is empty
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ImGuiCommand> borrow(klass: KClass<T>, factory: () -> T): T {
        val pool = pools.getOrPut(klass) { ConcurrentLinkedQueue() }
        
        return (pool.poll() as? T) ?: factory()
    }
    
    /**
     * Return a command to the pool for reuse
     */
    fun recycle(command: ImGuiCommand) {
        val klass = command::class
        val pool = pools.getOrPut(klass) { ConcurrentLinkedQueue() }
        
        if (pool.size < MAX_POOL_SIZE) {
            // Reset command state if needed
            resetCommand(command)
            pool.offer(command)
        }
    }
    
    /**
     * Batch recycle multiple commands
     */
    fun recycleBatch(commands: List<ImGuiCommand>) {
        for (command in commands) {
            recycle(command)
        }
    }
    
    /**
     * Reset command to default state before returning to pool
     */
    private fun resetCommand(command: ImGuiCommand) {
        // Commands should implement a reset method if they have mutable state
        // For now, we'll just return them as-is since most are immutable data classes
    }
    
    /**
     * Clear all pools
     */
    fun clear() {
        pools.clear()
    }
    
    /**
     * Get pool statistics for monitoring
     */
    fun getStats(): Map<String, Int> {
        return pools.mapKeys { it.key.simpleName ?: "Unknown" }
            .mapValues { it.value.size }
    }
}

/**
 * Extension to make command execution automatically recycle commands
 */
fun ImGuiCommand.executeAndRecycle() {
    try {
        this.execute()
    } finally {
        CommandPool.recycle(this)
    }
}