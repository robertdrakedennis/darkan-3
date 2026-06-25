package com.undercut.script.scheduler

/**
 * Structured reason codes that a scheduled script can report when requesting
 * removal from the schedule.
 */
enum class RemovalCode {
    MISSING_REQUIREMENTS,
    UNHANDLED_EXCEPTION,
    TIME_REACHED,
    LEVEL_REACHED,
    USER_REQUEST
}
