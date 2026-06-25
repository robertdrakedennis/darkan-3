package com.undercut.ui.backend.dsl

import com.undercut.game.memory.NativeAccess
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout


// State factory functions
fun boolState(initialValue: Boolean): ImGuiState<Boolean> = BooleanState(initialValue)
fun intState(initialValue: Int): ImGuiState<Int> = IntState(initialValue)

fun floatState(initialValue: Float): ImGuiState<Float> = FloatState(initialValue)
fun stringState(initialValue: String, maxLength: Int = 256): ImGuiState<String> = StringState(initialValue, maxLength)

// Range state helpers for creating pairs of min/max states
data class FloatRange(val min: ImGuiState<Float>, val max: ImGuiState<Float>) : AutoCloseable {
    override fun close() {
        min.close()
        max.close()
    }
}

data class IntRange(val min: ImGuiState<Int>, val max: ImGuiState<Int>) : AutoCloseable {
    override fun close() {
        min.close()
        max.close()
    }
}

fun rangeStateFloat(minValue: Float, maxValue: Float): FloatRange {
    return FloatRange(floatState(minValue), floatState(maxValue))
}

fun rangeStateInt(minValue: Int, maxValue: Int): IntRange {
    return IntRange(intState(minValue), intState(maxValue))
}

/**
 * State management for ImGui values with proper memory handling and thread safety
 */
abstract class ImGuiState<T> : AutoCloseable {
    abstract var value: T
    open val buffer: MemorySegment? = null

    @Volatile
    private var closed = false

    protected fun checkNotClosed() {
        if (closed) {
            throw IllegalStateException("ImGuiState has been closed")
        }
    }

    override fun close() {
        closed = true
    }
}

class BooleanState(initialValue: Boolean) : ImGuiState<Boolean>() {
    // Persistent buffer C++ writes to directly; freed when the engine unloads (per-load arena).
    override val buffer: MemorySegment = NativeAccess.engineArena.allocate(ValueLayout.JAVA_BOOLEAN)
    
    init {
        // Set initial value in buffer
        buffer.set(ValueLayout.JAVA_BOOLEAN, 0, initialValue)
    }
    
    override var value: Boolean
        get() {
            checkNotClosed()
            return buffer.get(ValueLayout.JAVA_BOOLEAN, 0)
        }
        set(newValue) {
            checkNotClosed()
            buffer.set(ValueLayout.JAVA_BOOLEAN, 0, newValue)
        }

    override fun close() {
        super.close()
        // Buffer is in global arena, no need to explicitly free
        // Just clear the value
        try {
            buffer.set(ValueLayout.JAVA_BOOLEAN, 0, false)
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}

class IntState(initialValue: Int) : ImGuiState<Int>() {
    // Persistent buffer C++ writes to directly; freed when the engine unloads (per-load arena).
    override val buffer: MemorySegment = NativeAccess.engineArena.allocate(ValueLayout.JAVA_INT)
    
    init {
        // Set initial value in buffer
        buffer.set(ValueLayout.JAVA_INT, 0, initialValue)
    }
    
    override var value: Int
        get() {
            checkNotClosed()
            return buffer.get(ValueLayout.JAVA_INT, 0)
        }
        set(newValue) {
            checkNotClosed()
            buffer.set(ValueLayout.JAVA_INT, 0, newValue)
        }

    override fun close() {
        super.close()
        // Buffer is in global arena, no need to explicitly free
        // Just clear the value
        try {
            buffer.set(ValueLayout.JAVA_INT, 0, 0)
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}

class FloatState(initialValue: Float) : ImGuiState<Float>() {
    // TODO: Native access buffer
    override var value: Float = initialValue
        get() {
            checkNotClosed()
            return field
        }
        set(newValue) {
            checkNotClosed()
            field = newValue
        }

    override fun close() {
        // Clear state while still open to avoid setter throwing on closed check
        value = 0.0f
        super.close()
    }
}

class StringState(initialValue: String, private val maxLength: Int) : ImGuiState<String>() {
    // Persistent buffer C++ writes to directly; freed when the engine unloads (per-load arena).
    override val buffer: MemorySegment = NativeAccess.engineArena.allocate(maxLength.toLong())

    override var value: String = initialValue
        get() {
            checkNotClosed()
            return field
        }
        set(newValue) {
            checkNotClosed()

            // Validate and sanitize input
            val sanitized = newValue
                .take(maxLength - 1) // Reserve space for null terminator
                .filter { it.code <= 0x10FFFF && it.code >= 0x20 || it in "\t\n\r" } // Valid printable chars + basic whitespace
                .replace("\u0000", "") // Remove any null terminators

            field = sanitized

            try {
                // Update buffer safely
                val bytes = sanitized.toByteArray(Charsets.UTF_8)
                val maxBytes = (maxLength - 1).coerceAtMost(bytes.size)

                // Clear buffer first
                buffer.fill(0.toByte())

                // Copy safe bytes + null terminator
                if (maxBytes > 0) {
                    buffer.asSlice(0, maxBytes.toLong())
                        .copyFrom(MemorySegment.ofArray(bytes.take(maxBytes).toByteArray()))
                }
                buffer.set(ValueLayout.JAVA_BYTE, maxBytes.toLong(), 0.toByte()) // Null terminate
            } catch (e: Exception) {
                // Fallback to empty string on error
                field = ""
                buffer.fill(0.toByte())
            }
        }

    init {
        value = initialValue // Trigger buffer update
    }

    override fun close() {
        // Clear value while still open to avoid closed-state checks in setter
        try {
            value = ""
        } catch (_: Exception) {}

        // Clear sensitive data in buffer
        try {
            buffer.fill(0.toByte())
        } catch (_: Exception) {
            // Ignore cleanup errors
        }

        super.close()
    }
}
