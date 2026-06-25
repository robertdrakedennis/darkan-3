package com.undercut.script.scheduler

/**
 * Structured reason for removing a scheduled script from the queue.
 * Kept minimal; suitable for future JSON adapters.
 */
data class RemovalReason(
    val code: RemovalCode,
    val message: String? = null
)

