package com.undercut.puzzle.combolock

import com.undercut.script.api.interfaces

/**
 * Layout and readout for the in-game alphabetical combination lock (interface 79) — four A–Z dials,
 * each with a left arrow ◄ that turns the letter backward and a right arrow ► that turns it forward,
 * plus an Enter button. Reusable across any content that uses this interface; holds no quest-specific
 * logic (deriving the target word is the caller's job).
 */
object CombinationLockInterface {
    const val INTERFACE_ID = 79
    const val ENTER_COMPONENT = 17
    const val DIALS = 4

    // Dial 1..4, left→right: the letter display, the backward (◄) arrow, the forward (►) arrow.
    private val LETTER_COMPONENT = intArrayOf(5, 6, 7, 8)
    private val BACKWARD_ARROW = intArrayOf(9, 10, 11, 12)
    private val FORWARD_ARROW = intArrayOf(13, 14, 15, 16)

    fun isOpen(): Boolean = runCatching { interfaces.isOpen(INTERFACE_ID) }.getOrDefault(false)

    /** Current letter on each dial (A–Z), or null if the interface can't be read cleanly this tick. */
    fun readDials(): CharArray? {
        val out = CharArray(DIALS)
        for (i in 0 until DIALS) {
            val text = runCatching { interfaces.getComponent(INTERFACE_ID, LETTER_COMPONENT[i])?.text }.getOrNull() ?: return null
            val c = text.trim().firstOrNull()?.uppercaseChar() ?: return null
            if (c !in 'A'..'Z') return null
            out[i] = c
        }
        return out
    }

    fun arrowComponent(dial: Int, direction: TurnDirection): Int =
        if (direction == TurnDirection.FORWARD) FORWARD_ARROW[dial] else BACKWARD_ARROW[dial]
}
