package com.undercut.game.nxt

import com.undercut.game.Tile
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess
import com.undercut.game.memory.NativeAccess.toMemorySegment
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT_UNALIGNED
import java.lang.foreign.ValueLayout.JAVA_LONG_UNALIGNED

/**
 * Live terrain fine-height (Z) at a world-fine position, read straight from memory by replicating
 * `jag::game::HeightMap::GetFineHeight` (rs2client 948-5 @ 0x4d18c0) — no native call.
 *
 * Path: `World[0x14038]` = HeightMap; `*(hm+8)` = a 2D region grid (0x18-byte cells, X-major); a cell's
 * `+8` = regionHeightData; that holds a per-plane vertex-height grid which is bilinear-sampled at the
 * sub-tile position. Coords are world-fine (512 units/tile; 0x8000 = one 64-tile region). All offsets
 * disassembly-verified for 948-5. Used by tile overlays so tiles sit on the real ground.
 */
object HeightMap {
    private const val HEIGHT_MAP = 0x14038L
    private const val GRID = 0x8L

    private const val MIN_REGION_X = 0x13fb4L
    private const val MIN_REGION_Y = 0x13fb8L
    private const val MAX_REGION_X = 0x13fbcL
    private const val MAX_REGION_Y = 0x13fc0L
    private const val DATA_ARRAY = 0x14000L
    private const val CELL_STRIDE = 0x18L
    private const val CELL_REGION_DATA = 0x8L
    private const val CELL_SENTINEL = 0x10L

    private const val CONTAINER_PRIMARY = 0x90L
    private const val CONTAINER_FALLBACK = 0xeb88L
    private const val READY_LO = 0x11L
    private const val READY_HI = 0x12L
    private const val NOT_LOADED_FLAG = 0x159L
    private const val PLANE_VEC_BEGIN = 0x160L
    private const val PLANE_VEC_END = 0x168L
    private const val PLANE_STRIDE = 0x10L
    private const val PLANE_ELEM = 0x8L

    private const val EMPTY_CELL_SENTINEL_REL = 0x15df0b0L

    private fun r64(addr: Long) = addr.toMemorySegment(8).get(JAVA_LONG_UNALIGNED, 0L)
    private fun r32(addr: Long) = addr.toMemorySegment(4).get(JAVA_INT_UNALIGNED, 0L)
    private fun r8(addr: Long) = addr.toMemorySegment(1).get(JAVA_BYTE, 0L)
    // A region container is fully loaded when its two state bytes match (the sampler uses the
    // primary grid only on byte[0x11] == byte[0x12]; otherwise it falls through to the fallback).
    private fun ready(c: Long) = c != 0L && r8(c + READY_LO) == r8(c + READY_HI)

    /** Terrain fine-height at [tile]'s centre, or null if the height map has no data there. */
    fun fineHeight(tile: Tile): Int? =
        fineHeight(tile.plane.toInt(), tile.x.toInt() * 512 + 256, tile.y.toInt() * 512 + 256)

    /** Terrain fine-height (Z) at world-fine ([worldFineX], [worldFineY]) on [plane], or null. */
    fun fineHeight(plane: Int, worldFineX: Int, worldFineY: Int): Int? =
        runCatching { sample(plane, worldFineX, worldFineY) }.getOrNull()

    private fun sample(plane: Int, fineX: Int, fineY: Int): Int? {
        val world = (Bootstrap.client.sceneManager.currentWorld?.ptr ?: return null).address()
        val grid = r64(world + HEIGHT_MAP + GRID)
        if (grid == 0L) return null

        val regionX = (if (fineX >= 0) fineX else fineX + 0x7fff) shr 0xf
        val regionY = (if (fineY >= 0) fineY else fineY + 0x7fff) shr 0xf
        val withinX = fineX and 0x7fff
        val withinY = fineY and 0x7fff

        val minRX = r32(grid + MIN_REGION_X); val maxRX = r32(grid + MAX_REGION_X)
        val minRY = r32(grid + MIN_REGION_Y); val maxRY = r32(grid + MAX_REGION_Y)
        if (regionX < minRX || regionX > maxRX || regionY < minRY || regionY > maxRY) return null

        val dataArray = r64(grid + DATA_ARRAY)
        if (dataArray == 0L) return null
        val rowPtr = r64(dataArray + (regionX - minRX).toLong() * CELL_STRIDE)
        if (rowPtr == 0L) return null
        val cell = rowPtr + (regionY - minRY).toLong() * CELL_STRIDE
        if (r64(cell + CELL_SENTINEL) == NativeAccess.BASE_ADDR.address() + EMPTY_CELL_SENTINEL_REL) return null
        val regionData = r64(cell + CELL_REGION_DATA)
        if (regionData == 0L) return null

        var container = r64(regionData + CONTAINER_PRIMARY)
        if (!ready(container)) {
            container = r64(regionData + CONTAINER_FALLBACK)
            if (!ready(container)) return null
        }
        if (r8(container + NOT_LOADED_FLAG).toInt() != 0) return null

        val planeBegin = r64(container + PLANE_VEC_BEGIN)
        val planeCount = ((r64(container + PLANE_VEC_END) - planeBegin) shr 4).toInt()
        if (plane < 0 || plane >= planeCount) return null
        val planeGrid = r64(planeBegin + PLANE_ELEM + plane.toLong() * PLANE_STRIDE)
        if (planeGrid == 0L) return null

        val base = r64(planeGrid)
        if (base == 0L) return null
        val col = ((if (withinX >= 0) withinX else withinX + 0x1ff) shr 9) + 1
        val row = ((if (withinY >= 0) withinY else withinY + 0x1ff) shr 9) + 1
        val subX = withinX and 0x1ff
        val subY = withinY and 0x1ff

        fun h(c: Int, r: Int): Int = r32(r64(base + c.toLong() * CELL_STRIDE) + r.toLong() * 4)
        fun blend(a: Int, b: Int, t: Int): Int = (a * (0x200 - t) + b * t).let { if (it >= 0) it else it + 0x1ff } shr 9

        val height = when {
            subX == 0 && subY == 0 -> h(col, row)
            subX == 0 -> blend(h(col, row), h(col, row + 1), subY)
            subY == 0 -> blend(h(col, row), h(col + 1, row), subX)
            else -> blend(
                blend(h(col, row), h(col + 1, row), subX),
                blend(h(col, row + 1), h(col + 1, row + 1), subX),
                subY,
            )
        }
        return if (height == -1) null else height
    }
}
