package world.gregs.voidps.routefinder

import world.gregs.voidps.collision.CollisionFlag
import world.gregs.voidps.collision.CollisionFlagMap
import world.gregs.voidps.collision.CollisionStrategies
import world.gregs.voidps.collision.CollisionStrategy
import java.util.Arrays

/**
 * Size-1 rsmod-style BFS routefinder for click-to-walk.
 *
 * It uses the canonical 128x128 search window, rsmod/NXT collision consult masks,
 * and an absent-zone default of `-1` through [CollisionFlagMap.get].
 */
class RouteFinding(
    private val flags: CollisionFlagMap,
    private val searchMapSize: Int = DEFAULT_SEARCH_MAP_SIZE,
    private val ringBufferSize: Int = DEFAULT_RING_BUFFER_SIZE,
) {
    private val directions = IntArray(searchMapSize * searchMapSize)
    private val distances = IntArray(searchMapSize * searchMapSize) { DEFAULT_DISTANCE_VALUE }
    private val validLocalX = IntArray(ringBufferSize)
    private val validLocalZ = IntArray(ringBufferSize)
    private var currLocalX = 0
    private var currLocalZ = 0
    private var bufReaderIndex = 0
    private var bufWriterIndex = 0

    fun findRoute(
        level: Int,
        srcX: Int,
        srcZ: Int,
        destX: Int,
        destZ: Int,
        collision: CollisionStrategy = CollisionStrategies.NORMAL,
    ): Route {
        require(srcX in 0..0x3fff && srcZ in 0..0x3fff)
        require(destX in 0..0x3fff && destZ in 0..0x3fff)
        require(level in 0..0x3)

        if (srcX == destX && srcZ == destZ) {
            return Route(tiles = emptyList(), alternative = false, success = true)
        }

        reset()
        val baseX = srcX - (searchMapSize / 2)
        val baseZ = srcZ - (searchMapSize / 2)
        val localSrcX = srcX - baseX
        val localSrcZ = srcZ - baseZ
        val localDestX = destX - baseX
        val localDestZ = destZ - baseZ
        if (localDestX !in 0 until searchMapSize || localDestZ !in 0 until searchMapSize) {
            return Route.FAILED
        }

        appendDirection(localSrcX, localSrcZ, DEFAULT_SRC_DIRECTION_VALUE, 0)
        if (!routeFindSize1(baseX, baseZ, level, localDestX, localDestZ, collision)) {
            return Route.FAILED
        }
        return Route(tiles = buildRoute(baseX, baseZ, level, localSrcX, localSrcZ), alternative = false, success = true)
    }

    private fun routeFindSize1(
        baseX: Int,
        baseZ: Int,
        level: Int,
        localDestX: Int,
        localDestZ: Int,
        collision: CollisionStrategy,
    ): Boolean {
        val relativeSearchSize = searchMapSize - 1
        while (bufWriterIndex != bufReaderIndex) {
            currLocalX = validLocalX[bufReaderIndex]
            currLocalZ = validLocalZ[bufReaderIndex]
            bufReaderIndex = (bufReaderIndex + 1) and (ringBufferSize - 1)

            if (currLocalX == localDestX && currLocalZ == localDestZ) {
                return true
            }

            val nextDistance = distances[currLocalX, currLocalZ] + 1
            tryAppend(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX - 1,
                z = currLocalZ,
                allowed = currLocalX > 0,
                blockFlag = CollisionFlag.BLOCK_WEST,
                directionFlag = DirectionFlag.EAST,
                distance = nextDistance,
                collision = collision,
            )
            tryAppend(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX + 1,
                z = currLocalZ,
                allowed = currLocalX < relativeSearchSize,
                blockFlag = CollisionFlag.BLOCK_EAST,
                directionFlag = DirectionFlag.WEST,
                distance = nextDistance,
                collision = collision,
            )
            tryAppend(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX,
                z = currLocalZ - 1,
                allowed = currLocalZ > 0,
                blockFlag = CollisionFlag.BLOCK_SOUTH,
                directionFlag = DirectionFlag.NORTH,
                distance = nextDistance,
                collision = collision,
            )
            tryAppend(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX,
                z = currLocalZ + 1,
                allowed = currLocalZ < relativeSearchSize,
                blockFlag = CollisionFlag.BLOCK_NORTH,
                directionFlag = DirectionFlag.SOUTH,
                distance = nextDistance,
                collision = collision,
            )
            tryAppendDiagonal(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX - 1,
                z = currLocalZ - 1,
                allowed = currLocalX > 0 && currLocalZ > 0,
                targetBlockFlag = CollisionFlag.BLOCK_SOUTH_WEST,
                sideX = currLocalX - 1,
                sideZ = currLocalZ,
                sideBlockFlag = CollisionFlag.BLOCK_WEST,
                otherSideX = currLocalX,
                otherSideZ = currLocalZ - 1,
                otherSideBlockFlag = CollisionFlag.BLOCK_SOUTH,
                directionFlag = DirectionFlag.NORTH_EAST,
                distance = nextDistance,
                collision = collision,
            )
            tryAppendDiagonal(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX + 1,
                z = currLocalZ - 1,
                allowed = currLocalX < relativeSearchSize && currLocalZ > 0,
                targetBlockFlag = CollisionFlag.BLOCK_SOUTH_EAST,
                sideX = currLocalX + 1,
                sideZ = currLocalZ,
                sideBlockFlag = CollisionFlag.BLOCK_EAST,
                otherSideX = currLocalX,
                otherSideZ = currLocalZ - 1,
                otherSideBlockFlag = CollisionFlag.BLOCK_SOUTH,
                directionFlag = DirectionFlag.NORTH_WEST,
                distance = nextDistance,
                collision = collision,
            )
            tryAppendDiagonal(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX - 1,
                z = currLocalZ + 1,
                allowed = currLocalX > 0 && currLocalZ < relativeSearchSize,
                targetBlockFlag = CollisionFlag.BLOCK_NORTH_WEST,
                sideX = currLocalX - 1,
                sideZ = currLocalZ,
                sideBlockFlag = CollisionFlag.BLOCK_WEST,
                otherSideX = currLocalX,
                otherSideZ = currLocalZ + 1,
                otherSideBlockFlag = CollisionFlag.BLOCK_NORTH,
                directionFlag = DirectionFlag.SOUTH_EAST,
                distance = nextDistance,
                collision = collision,
            )
            tryAppendDiagonal(
                baseX = baseX,
                baseZ = baseZ,
                level = level,
                x = currLocalX + 1,
                z = currLocalZ + 1,
                allowed = currLocalX < relativeSearchSize && currLocalZ < relativeSearchSize,
                targetBlockFlag = CollisionFlag.BLOCK_NORTH_EAST,
                sideX = currLocalX + 1,
                sideZ = currLocalZ,
                sideBlockFlag = CollisionFlag.BLOCK_EAST,
                otherSideX = currLocalX,
                otherSideZ = currLocalZ + 1,
                otherSideBlockFlag = CollisionFlag.BLOCK_NORTH,
                directionFlag = DirectionFlag.SOUTH_WEST,
                distance = nextDistance,
                collision = collision,
            )
        }
        return false
    }

    private fun tryAppend(
        baseX: Int,
        baseZ: Int,
        level: Int,
        x: Int,
        z: Int,
        allowed: Boolean,
        blockFlag: Int,
        directionFlag: Int,
        distance: Int,
        collision: CollisionStrategy,
    ) {
        if (allowed &&
            directions[x, z] == 0 &&
            collision.canMove(flags[baseX + x, baseZ + z, level], blockFlag)
        ) {
            appendDirection(x, z, directionFlag, distance)
        }
    }

    private fun tryAppendDiagonal(
        baseX: Int,
        baseZ: Int,
        level: Int,
        x: Int,
        z: Int,
        allowed: Boolean,
        targetBlockFlag: Int,
        sideX: Int,
        sideZ: Int,
        sideBlockFlag: Int,
        otherSideX: Int,
        otherSideZ: Int,
        otherSideBlockFlag: Int,
        directionFlag: Int,
        distance: Int,
        collision: CollisionStrategy,
    ) {
        if (allowed &&
            directions[x, z] == 0 &&
            collision.canMove(flags[baseX + x, baseZ + z, level], targetBlockFlag) &&
            collision.canMove(flags[baseX + sideX, baseZ + sideZ, level], sideBlockFlag) &&
            collision.canMove(flags[baseX + otherSideX, baseZ + otherSideZ, level], otherSideBlockFlag)
        ) {
            appendDirection(x, z, directionFlag, distance)
        }
    }

    private fun buildRoute(
        baseX: Int,
        baseZ: Int,
        level: Int,
        localSrcX: Int,
        localSrcZ: Int,
    ): List<RouteCoordinates> {
        val reversed = ArrayList<RouteCoordinates>()
        var nextDir = directions[currLocalX, currLocalZ]
        for (i in directions.indices) {
            if (currLocalX == localSrcX && currLocalZ == localSrcZ) {
                break
            }
            reversed += RouteCoordinates(baseX + currLocalX, baseZ + currLocalZ, level)
            if ((nextDir and DirectionFlag.EAST) != 0) {
                currLocalX++
            } else if ((nextDir and DirectionFlag.WEST) != 0) {
                currLocalX--
            }
            if ((nextDir and DirectionFlag.NORTH) != 0) {
                currLocalZ++
            } else if ((nextDir and DirectionFlag.SOUTH) != 0) {
                currLocalZ--
            }
            nextDir = directions[currLocalX, currLocalZ]
        }
        return reversed.asReversed()
    }

    private fun appendDirection(x: Int, z: Int, direction: Int, distance: Int) {
        directions[x, z] = direction
        distances[x, z] = distance
        validLocalX[bufWriterIndex] = x
        validLocalZ[bufWriterIndex] = z
        bufWriterIndex = (bufWriterIndex + 1) and (ringBufferSize - 1)
    }

    private fun reset() {
        Arrays.fill(directions, 0)
        Arrays.fill(distances, DEFAULT_DISTANCE_VALUE)
        bufReaderIndex = 0
        bufWriterIndex = 0
    }

    private operator fun IntArray.get(x: Int, z: Int): Int = this[(x * searchMapSize) + z]

    private operator fun IntArray.set(x: Int, z: Int, value: Int) {
        this[(x * searchMapSize) + z] = value
    }

    companion object {
        const val DEFAULT_SEARCH_MAP_SIZE = 128
        private const val DEFAULT_RING_BUFFER_SIZE = 4096
        private const val DEFAULT_DISTANCE_VALUE = 99_999_999
        private const val DEFAULT_SRC_DIRECTION_VALUE = 99
    }
}

private object DirectionFlag {
    const val NORTH: Int = 0x1
    const val EAST: Int = 0x2
    const val SOUTH: Int = 0x4
    const val WEST: Int = 0x8

    const val SOUTH_WEST: Int = WEST or SOUTH
    const val NORTH_WEST: Int = WEST or NORTH
    const val SOUTH_EAST: Int = EAST or SOUTH
    const val NORTH_EAST: Int = EAST or NORTH
}
