package world.gregs.voidps.cache.config.data

import world.gregs.voidps.cache.Definition

/**
 * Packed world coordinate used by WorldAreaType opcodes 3 and 4.
 *
 * Layout: `(plane << 28) | (x << 14) | y`; `-1` is the null coordinate sentinel.
 */
data class WorldAreaCoord(val plane: Int, val x: Int, val y: Int) {
    val isNull: Boolean get() = plane < 0

    val mapSquareX: Int get() = x ushr 6
    val mapSquareY: Int get() = y ushr 6

    companion object {
        val NULL = WorldAreaCoord(-1, 0, 0)

        fun decode(packed: Int): WorldAreaCoord {
            if (packed == -1) {
                return NULL
            }
            return WorldAreaCoord(
                plane = (packed ushr 28) and 0x3,
                x = (packed ushr 14) and 0x3FFF,
                y = packed and 0x3FFF,
            )
        }
    }
}

/**
 * WorldAreaType config from CONFIG archive 83.
 *
 * The login scene-root lookup uses the client's map-square-set bounding box: the map square of each
 * op3 first corner plus each op4 point. The smallest containing box is treated as most specific.
 */
data class WorldAreaDefinition(
    override var id: Int = -1,
    var value: Int = -1,
    var rects: MutableList<Pair<WorldAreaCoord, WorldAreaCoord>>? = null,
    var points: MutableList<Pair<WorldAreaCoord, Int>>? = null,
    var minMapSquareX: Int = -1,
    var maxMapSquareX: Int = -1,
    var minMapSquareY: Int = -1,
    var maxMapSquareY: Int = -1,
) : Definition {

    fun containsMapSquare(mapSquareX: Int, mapSquareY: Int): Boolean {
        if (minMapSquareX < 0) {
            return false
        }
        return mapSquareX in minMapSquareX..maxMapSquareX &&
            mapSquareY in minMapSquareY..maxMapSquareY
    }

    val mapSquareBoundsSize: Int
        get() = if (minMapSquareX < 0) {
            Int.MAX_VALUE
        } else {
            (maxMapSquareX - minMapSquareX + 1) * (maxMapSquareY - minMapSquareY + 1)
        }

    fun computeMapSquareBounds() {
        var minX = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE
        var maxY = Int.MIN_VALUE

        fun include(coord: WorldAreaCoord) {
            if (coord.isNull) {
                return
            }
            val mx = coord.mapSquareX
            val my = coord.mapSquareY
            if (mx < minX) minX = mx
            if (mx > maxX) maxX = mx
            if (my < minY) minY = my
            if (my > maxY) maxY = my
        }

        rects?.forEach { (corner, _) -> include(corner) }
        points?.forEach { (coord, _) -> include(coord) }

        if (minX == Int.MAX_VALUE) {
            minMapSquareX = -1
            maxMapSquareX = -1
            minMapSquareY = -1
            maxMapSquareY = -1
        } else {
            minMapSquareX = minX
            maxMapSquareX = maxX
            minMapSquareY = minY
            maxMapSquareY = maxY
        }
    }
}
