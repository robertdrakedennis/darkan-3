package com.undercut.ui.backend.native

import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

object StringAllocator {
    private data class StringEntry(
        val lastAccess: AtomicLong,
        val segment: MemorySegment,
        val buffer: ByteBuffer
    )
    
    // Configuration parameters
    private const val CLEANUP_THRESHOLD_FRAMES = 10
    private const val MAX_CACHE_SIZE = 1000000
    private const val CLEANUP_INITIAL_DELAY_MS = 1000L
    private const val CLEANUP_INTERVAL_MS = 100L
    
    // State management
    private val stringMap = ConcurrentHashMap<String, StringEntry>()
    private val currentFrame = AtomicLong(0L)
    private val frameLock = ReentrantReadWriteLock()
    private val frameDepth = AtomicInteger(0) // 0=not in frame, >0=in frame (supports nesting)
    private val cleanupInterrupt = AtomicBoolean(false)
    private var isShutdown = AtomicBoolean(false)

    private val cleanupExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable).apply {
            isDaemon = true
            name = "StringAllocator-Cleanup"
        }
    }
    
    init {
        // Schedule cleanup to run every 100ms, but it will only actually clean when safe
        cleanupExecutor.scheduleWithFixedDelay({
            if (!isShutdown.get()) {
                tryRunCleanup()
            }
        }, CLEANUP_INITIAL_DELAY_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }
    
    private fun tryRunCleanup() {
        // Coordinate with frame operations using read lock
        // Frame operations use write lock, so this ensures mutual exclusion
        frameLock.read {
            if (frameDepth.get() == 0) { // Not in any frame
                doCleanupInterruptible(currentFrame.get())
            }
        }
    }
    
    private fun doCleanupInterruptible(frameNum: Long) {
        cleanupInterrupt.set(false)
        
        try {
            val iterator = stringMap.entries.iterator()
            var processed = 0
            
            while (iterator.hasNext() && !cleanupInterrupt.get() && !isShutdown.get()) {
                val entry = iterator.next()
                if (frameNum - entry.value.lastAccess.get() > CLEANUP_THRESHOLD_FRAMES) {
                    iterator.remove()
                }
                
                // Yield periodically to prevent long blocking
                if (++processed % 100 == 0) {
                    Thread.yield()
                }
            }
        } catch (e: Exception) {
            println("Wadu hek")
        }
    }

    private fun doAllocate(str: String): StringEntry? {
        try {
            val bytes = str.toByteArray(Charsets.UTF_8)
            val buffer = ByteBuffer.allocateDirect(bytes.size + 1) // +1 for null terminator
            buffer.put(bytes)
            buffer.put(0) // null terminator
            buffer.flip()
            val segment = MemorySegment.ofBuffer(buffer)
            return StringEntry(AtomicLong(0), segment, buffer)
        } catch (e: OutOfMemoryError) {
            // Force cleanup and try once more
            System.gc()
            tryRunCleanup()
            try {
                val bytes = str.toByteArray(Charsets.UTF_8)
                val buffer = ByteBuffer.allocateDirect(bytes.size + 1)
                buffer.put(bytes)
                buffer.put(0)
                buffer.flip()
                val segment = MemorySegment.ofBuffer(buffer)
                return StringEntry(AtomicLong(0), segment, buffer)
            } catch (e2: OutOfMemoryError) {
                return null
            }
        }
    }

    fun allocateString(str: String): MemorySegment {
        if (stringMap.size >= MAX_CACHE_SIZE) {
            return MemorySegment.NULL
        }
        
        try {
            var entry = stringMap[str]
            if (entry == null) {
                val newEntry = doAllocate(str) ?: return MemorySegment.NULL
                entry = stringMap.putIfAbsent(str, newEntry)
                if (entry == null) {
                    entry = newEntry
                }
            }
            
            entry.lastAccess.set(currentFrame.get())
            return entry.segment
        } catch (e: Exception) {
            return MemorySegment.NULL
        }
    }
    

    fun newFrame() {
        if (isShutdown.get()) return
        
        // Signal any running cleanup to stop
        cleanupInterrupt.set(true)
        
        frameLock.write {
            val depth = frameDepth.incrementAndGet()
            if (depth == 1) {
                // First frame - advance the frame counter
                currentFrame.incrementAndGet()
            }
        }
    }

    fun endFrame() {
        if (isShutdown.get()) return
        
        frameLock.write {
            val depth = frameDepth.decrementAndGet()
            if (depth <= 0) {
                frameDepth.set(0) // Prevent negative values from unmatched calls
            }
        }
    }

}