package com.undercut.puzzle.slide

/**
 * Drives the slide-puzzle overlay. Each main-logic tick it picks the active puzzle, reads the board,
 * solves it (recomputing only when the board actually changes), and publishes a [SlidePuzzleOverlayState]
 * for the render thread. Advisory only — it never clicks anything.
 *
 * Adding another puzzle is a one-line entry in [sources]; the solver and renderer are shared.
 */
object SlidePuzzleFeature {

    private const val LOOKAHEAD = 4

    private val sources: List<SlidePuzzleSource> = listOf(
        InterfaceSlidePuzzleSource(
            id = "puzzle-box-1931",
            interfaceId = 1931,
            gridComponentId = 18,
            rows = 5,
            cols = 5,
            firstTileVarbit = 39405,
            blankTileId = 24,
        ),
    )

    @Volatile
    var overlay: SlidePuzzleOverlayState? = null
        private set

    private var cacheSig = 0L
    private var cachedSourceId: String? = null
    private var cachedMoves: List<SlideMove> = emptyList()

    fun tick(enabled: Boolean) {
        if (!enabled) { clear(); return }
        val src = sources.firstOrNull { it.isActive() }
        if (src == null) { clear(); return }

        val board = src.readBoard()
        if (board == null) { overlay = null; return }

        val goal = src.goal()
        if (board.isSolved(goal)) { clear(); return }

        val sig = board.signature()
        if (sig != cacheSig || src.id != cachedSourceId) {
            cachedMoves = SlidePuzzleSolver.solve(board, goal) ?: emptyList()
            cacheSig = sig
            cachedSourceId = src.id
        }

        val ifaceSrc = src as? InterfaceSlidePuzzleSource
        val arrows = cachedMoves.take(LOOKAHEAD).mapIndexed { i, m -> ArrowHint(m.tileSlot, m.direction, i + 1) }
        overlay = if (ifaceSrc == null || arrows.isEmpty()) null
        else SlidePuzzleOverlayState(ifaceSrc.interfaceId, ifaceSrc.gridComponentId, src.rows, src.cols, arrows)
    }

    private fun clear() {
        overlay = null
        cacheSig = 0L
        cachedSourceId = null
        cachedMoves = emptyList()
    }
}
