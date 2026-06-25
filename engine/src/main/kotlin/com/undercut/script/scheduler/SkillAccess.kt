package com.undercut.script.scheduler

import com.undercut.game.Skill
import com.undercut.script.api.getCurrentLevelOrNull

/**
 * Minimal gameplay helper for skill levels.
 * Fast, UI-free, and returns null when data is unavailable.
 */
fun currentLevelOrNull(skill: Skill): Int? = getCurrentLevelOrNull(skill)
