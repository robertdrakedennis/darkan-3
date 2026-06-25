package com.undercut.game.input

/**
 * Sealed interface for all capturable input events.
 * Each event carries a nanosecond timestamp and the game tick at capture time.
 */
sealed interface InputEvent {
    val timestampNanos: Long
    val gameTick: Int
}

data class MouseMotionEvent(
    override val timestampNanos: Long,
    override val gameTick: Int,
    val x: Int,
    val y: Int
) : InputEvent

data class MouseButtonEvent(
    override val timestampNanos: Long,
    override val gameTick: Int,
    val x: Int,
    val y: Int,
    val button: MouseButton,
    val pressed: Boolean
) : InputEvent

data class MouseScrollEvent(
    override val timestampNanos: Long,
    override val gameTick: Int,
    val x: Int,
    val y: Int,
    val scrollDelta: Int
) : InputEvent

data class KeyboardEvent(
    override val timestampNanos: Long,
    override val gameTick: Int,
    val keyCode: Int,
    val pressed: Boolean
) : InputEvent

enum class MouseButton(val id: Int) {
    LEFT(1),
    MIDDLE(2),
    RIGHT(3);

    companion object {
        fun fromId(id: Int): MouseButton? = entries.find { it.id == id }
    }
}
