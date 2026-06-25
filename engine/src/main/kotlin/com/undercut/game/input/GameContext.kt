package com.undercut.game.input

/**
 * Per-tick snapshot of game state, written alongside input events for training context.
 */
data class GameContext(
    val gameTick: Int,
    val mainState: Int,
    val playerX: Int,
    val playerY: Int,
    val playerPlane: Int,
    val cameraYaw: Float,
    val cameraPitch: Float,
    val timestampNanos: Long
)
