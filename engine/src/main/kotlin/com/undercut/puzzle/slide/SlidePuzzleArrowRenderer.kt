package com.undercut.puzzle.slide

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.hooks.Priority
import com.undercut.game.math.Vector2f
import com.undercut.game.nxt.MainState
import com.undercut.quest.overlay.arrow
import com.undercut.quest.overlay.pulseAlpha
import com.undercut.ui.backend.dsl.ImGuiDsl.backgroundDrawList
import com.undercut.ui.backend.dsl.scopes.BackgroundDrawListScope
import com.undercut.ui.backend.dsl.utils.ImGuiColors
import com.undercut.ui.backend.rendering.ImGUIRender
import kotlin.math.min

/**
 * Draws the slide-puzzle solution overlay: a directional arrow on each tile the player should slide,
 * with the immediate move bold/pulsing and the next few faint. Per-cell screen rects are derived by
 * subdividing the grid container's live rect, so this works for any grid size without per-slot data.
 *
 * Auto-discovered via [ImGUIRender]; reads only the snapshot published by [SlidePuzzleFeature], never
 * game state beyond the gated interface-rect lookup (same SIGSEGV-safety as the interface highlighter).
 */
object SlidePuzzleArrowRenderer {

    @JvmStatic
    @ImGUIRender(priority = Priority.LOW)
    fun render() {
        val state = SlidePuzzleFeature.overlay ?: return
        try {
            if (Bootstrap.client.mainState != MainState.LOGGED_IN) return
        } catch (_: Throwable) {
            return
        }
        val grid = resolveGridRect(state) ?: return
        backgroundDrawList {
            for (hint in state.arrows) {
                try {
                    drawArrowHint(grid, state.rows, state.cols, hint)
                } catch (_: Throwable) {
                }
            }
        }
    }

    private fun resolveGridRect(state: SlidePuzzleOverlayState): IntRect? {
        val list = try { Bootstrap.client.interfaceList } catch (_: Throwable) { return null }
        val open = try { list.isOpen(state.interfaceId) } catch (_: Throwable) { false }
        if (!open) return null
        val comp = try { list.getComponent(state.interfaceId, state.gridComponentId) } catch (_: Throwable) { return null }
            ?: return null
        val r = try { comp.screenRect } catch (_: Throwable) { return null } ?: return null
        if (r.width <= 0 || r.height <= 0) return null
        return IntRect(r.x, r.y, r.width, r.height)
    }

    private fun BackgroundDrawListScope.drawArrowHint(grid: IntRect, rows: Int, cols: Int, hint: ArrowHint) {
        val cellW = grid.w.toFloat() / cols
        val cellH = grid.h.toFloat() / rows
        val row = hint.slot / cols
        val col = hint.slot % cols
        val x0 = grid.x + col * cellW
        val y0 = grid.y + row * cellH
        val cx = x0 + cellW / 2f
        val cy = y0 + cellH / 2f

        // Bright cyan throughout, matching the interface highlighter / quest helper. The immediate
        // move pulses at full alpha; look-ahead moves stay cyan but dim with distance.
        val primary = hint.ordinal == 1
        val color = if (primary) {
            pulseAlpha(ImGuiColors.CYAN, minAlpha = 220, maxAlpha = 255)
        } else {
            val alpha = (170 - (hint.ordinal - 2) * 30).coerceIn(90, 170)
            (ImGuiColors.CYAN and 0x00FFFFFF) or (alpha shl 24)
        }

        val tl = Vector2f(x0 + 2f, y0 + 2f)
        val br = Vector2f(x0 + cellW - 2f, y0 + cellH - 2f)
        rect(tl, br, color, rounding = 4f, thickness = if (primary) 3f else 1.5f)

        val len = min(cellW, cellH) * 0.62f
        val dx: Float
        val dy: Float
        when (hint.direction) {
            Direction.UP -> { dx = 0f; dy = -1f }
            Direction.DOWN -> { dx = 0f; dy = 1f }
            Direction.LEFT -> { dx = -1f; dy = 0f }
            Direction.RIGHT -> { dx = 1f; dy = 0f }
        }
        val from = Vector2f(cx - dx * len / 2f, cy - dy * len / 2f)
        val to = Vector2f(cx + dx * len / 2f, cy + dy * len / 2f)
        arrow(from, to, color, thickness = if (primary) 5f else 3f, headSize = min(cellW, cellH) * 0.34f)

        text(Vector2f(x0 + 4f, y0 + 1f), color, hint.ordinal.toString())
    }

    private data class IntRect(val x: Int, val y: Int, val w: Int, val h: Int)
}
