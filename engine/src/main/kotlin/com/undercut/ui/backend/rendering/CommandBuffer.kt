package com.undercut.ui.backend.rendering

import com.undercut.ui.backend.dsl.commands.ImGuiCommand
import java.util.concurrent.atomic.AtomicInteger

/**
 * High-performance command buffer implementation using ring buffer pattern.
 * Reduces allocations and improves cache locality compared to linked structures.
 */
class CommandBuffer(private val capacity: Int = 8192) {
    private val commands = arrayOfNulls<ImGuiCommand>(capacity)
    private val head = AtomicInteger(0)
    private val tail = AtomicInteger(0)
    
    /**
     * Add a command to the buffer. Returns false if buffer is full.
     */
    fun offer(command: ImGuiCommand): Boolean {
        val currentTail = tail.get()
        val nextTail = (currentTail + 1) % capacity
        
        if (nextTail == head.get()) {
            return false // Buffer full
        }
        
        commands[currentTail] = command
        tail.set(nextTail)
        return true
    }
    
    /**
     * Add multiple commands as a batch. More efficient than individual offers.
     */
    fun offerBatch(batch: List<ImGuiCommand>): Boolean {
        val batchSize = batch.size
        val currentTail = tail.get()
        val currentHead = head.get()
        
        // Calculate available space
        val available = if (currentHead <= currentTail) {
            capacity - currentTail + currentHead - 1
        } else {
            currentHead - currentTail - 1
        }
        
        if (batchSize > available) {
            return false // Not enough space
        }
        
        // Copy commands in batch
        var writePos = currentTail
        for (command in batch) {
            commands[writePos] = command
            writePos = (writePos + 1) % capacity
        }
        
        tail.set(writePos)
        return true
    }
    
    /**
     * Process all commands in the buffer with the given processor.
     * Clears the buffer after processing.
     */
    fun drain(processor: (ImGuiCommand) -> Unit) {
        val currentHead = head.get()
        val currentTail = tail.get()
        
        if (currentHead == currentTail) {
            return // Empty
        }
        
        var readPos = currentHead
        while (readPos != currentTail) {
            val command = commands[readPos]
            if (command != null) {
                processor(command)
                commands[readPos] = null // Help GC
            }
            readPos = (readPos + 1) % capacity
        }
        
        head.set(currentTail)
    }
    
    /**
     * Clear all commands without processing
     */
    fun clear() {
        val currentHead = head.get()
        val currentTail = tail.get()
        
        // Clear references for GC
        var pos = currentHead
        while (pos != currentTail) {
            commands[pos] = null
            pos = (pos + 1) % capacity
        }
        
        head.set(0)
        tail.set(0)
    }
    
    /**
     * Check if buffer is empty
     */
    fun isEmpty(): Boolean = head.get() == tail.get()
    
    /**
     * Get current number of commands in buffer
     */
    fun size(): Int {
        val h = head.get()
        val t = tail.get()
        return if (t >= h) {
            t - h
        } else {
            capacity - h + t
        }
    }
}