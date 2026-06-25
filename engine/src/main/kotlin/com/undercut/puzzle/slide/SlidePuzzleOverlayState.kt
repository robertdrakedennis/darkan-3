package com.undercut.puzzle.slide

/**
 * Immutable snapshot published by [SlidePuzzleFeature] on the main-logic thread and consumed by the
 * render thread. Carries enough to resolve on-screen tile cells (interface + grid component + grid
 * size) plus the move hints to draw.
 */
data class SlidePuzzleOverlayState(
    val interfaceId: Int,
    val gridComponentId: Int,
    val rows: Int,
    val cols: Int,
    val arrows: List<ArrowHint>,
)

/** One arrow to draw: slide the tile in [slot] toward [direction]; [ordinal] 1 = next move. */
data class ArrowHint(val slot: Int, val direction: Direction, val ordinal: Int)
