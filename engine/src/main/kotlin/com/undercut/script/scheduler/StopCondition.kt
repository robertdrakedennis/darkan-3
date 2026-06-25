package com.undercut.script.scheduler

import com.undercut.game.Skill

/**
 * Stop conditions for scheduled items.
 * Kept minimal and UI-free; future JSON adapters can be added without breaking changes.
 */
sealed class StopCondition {
    /** Stop the current script after the given duration (milliseconds). */
    data class TimeBased(val durationMs: Long) : StopCondition()

    /** Stop when the specified skill reaches the target level. */
    data class LevelBased(val skill: Skill, val targetLevel: Int) : StopCondition()
}


