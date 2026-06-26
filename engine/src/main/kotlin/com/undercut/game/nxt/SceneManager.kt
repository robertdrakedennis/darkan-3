package com.undercut.game.nxt

import world.gregs.voidps.type.Tile
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readFloat
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.entity.Entity
import com.undercut.game.nxt.entity.GraphNode
import com.undercut.game.nxt.entity.location.CombinedLocationSection
import com.undercut.game.nxt.entity.location.Location
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.types.Vector
import com.undercut.script.api.Area
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.util.Objects
import kotlin.math.abs

class SceneManager(val ptr: MemorySegment) {
    val currentWorldIndex: Int
        get() = ptr.readInt(OSceneManager.CURRENT_WORLD_INDEX)
    val currentWorld: World?
        get() = if (ptr.address() == 0L) null else World(
            ptr.deref(
                OSceneManager.WORLD_ARRAY + currentWorldIndex * 0x10L,
                0x10L
            ).toShared().value(0x20000L)
        )

    fun getMapSquare(tile: Tile): MapSquare? {
        val world = currentWorld ?: return null
        return world.getMapSquare(tile.regionX, tile.regionY)
    }

    fun getLocationContainer(tile: Tile): LocationContainer? {
        val mapSquare = getMapSquare(tile) ?: return null
        return mapSquare.getLocationContainer(tile.xInRegion, tile.yInRegion)
    }

    fun getAllObjectsWithinRange(tile: Tile, range: Int): List<SceneObject> {
        val world = currentWorld ?: return emptyList()
        val minRegionX = (tile.x - range) shr 6
        val maxRegionX = (tile.x + range) shr 6
        val minRegionY = (tile.y - range) shr 6
        val maxRegionY = (tile.y + range) shr 6
        val rangeSq = range * range

        val objects = mutableListOf<SceneObject>()
        for (regionX in minRegionX..maxRegionX) {
            for (regionY in minRegionY..maxRegionY) {
                val mapSquare = world.getMapSquare(regionX, regionY) ?: continue
                mapSquare.allSceneObjects.filterTo(objects) { obj ->
                    val dx = obj.tile.x - tile.x
                    val dy = obj.tile.y - tile.y
                    dx * dx + dy * dy <= rangeSq
                }
            }
        }
        return objects.distinctBy { it.memPointer.address() }
    }

    fun getAllObjectsInArea(area: Area): List<SceneObject> {
        val world = currentWorld ?: return emptyList()
        val rectangular = area.toRectangular()
        val bottomLeft = rectangular.getBottomLeft()
        val topRight = rectangular.getTopRight()
        val minRegionX = bottomLeft.x.toInt() shr 6
        val maxRegionX = topRight.x.toInt() shr 6
        val minRegionY = bottomLeft.y.toInt() shr 6
        val maxRegionY = topRight.y.toInt() shr 6

        val objects = mutableListOf<SceneObject>()
        for (regionX in minRegionX..maxRegionX) {
            for (regionY in minRegionY..maxRegionY) {
                val mapSquare = world.getMapSquare(regionX, regionY) ?: continue
                mapSquare.allSceneObjects.filterTo(objects) { area.contains(it.tile) }
            }
        }
        return objects.distinctBy { it.memPointer.address() }
    }
}

@Volatile private var projectionMatrixBacking: FloatArray? = null
class World(val ptr: MemorySegment) {

    val rootGraphNode: GraphNode?
        get() = ptr.deref(OWorld.ROOT_GRAPH_NODE, 0x200L).getOrNull?.let { GraphNode(it) }

    val mapsquareXOffset: Int
        get() = ptr.readInt(OWorld.MAPSQUARE_X_OFFSET)
    val mapsquareYOffset: Int
        get() = ptr.readInt(OWorld.MAPSQUARE_Y_OFFSET)

    val viewMatrix: FloatArray
        get() = FloatArray(16) { i -> ptr.readFloat(OWorld.VIEW_MATRIX + (i * 4L)) }
    val projectionMatrix: FloatArray
        get() {
            val matrix = FloatArray(16) { i -> ptr.readFloat(OWorld.PROJECTION_MATRIX + (i * 4L)) }
            val isSkyBoxTransitioning = matrix.any { abs(abs(it) - 1.015748f) <= 1e-6f }
            if (isSkyBoxTransitioning && projectionMatrixBacking != null) {
                return projectionMatrixBacking!!
            }
            projectionMatrixBacking = matrix
            return matrix
        }

    private val mapSquares: Vector
        get() = Vector(ptr.pointerAtOffset(OWorld.MAPSQUARES_VECTOR, 0x20L), 0x18L)

    fun getMapSquare(regionX: Int, regionY: Int): MapSquare? {
        val adjustedX = regionX - mapsquareXOffset
        val adjustedY = regionY - mapsquareYOffset

        if (adjustedX < 0 || adjustedY < 0) return null
        if (adjustedX >= mapSquares.size) return null

        val innerVectorSegment = mapSquares[adjustedX]
        val innerVector = Vector(innerVectorSegment, 0x18L)

        if (adjustedY >= innerVector.size) return null
        val shared = innerVector[adjustedY].toShared().value(0x10000L).getOrNull ?: return null
        return MapSquare(shared)
    }
}

class MapSquare(ptr: MemorySegment) : Entity(ptr) {

    val chunkSize: Int
        get() = if (ptr.get(JAVA_LONG, OMapSquare.CHUNK_SIZE_FLAG) == 0L) 16 else 8

    private val locationContainers: Vector
        get() = Vector(ptr.pointerAtOffset(OMapSquare.LOCATION_CONTAINERS, 0x20L), 0x18L)

    fun getLocationContainer(containerX: Int, containerY: Int): LocationContainer? {
        val xVector = locationContainers
        if (xVector.begin.address() == xVector.end.address()) return null

        val cs = chunkSize
        val xIdx = containerX / cs
        if (xIdx < 0 || xIdx >= xVector.size) return null

        val yVector = Vector(xVector[xIdx], 0x8L)
        if (yVector.begin.address() == yVector.end.address()) return null
        val yIdx = containerY / cs
        if (yIdx < 0 || yIdx >= yVector.size) return null

        val lc = yVector[yIdx].deref(0L, 0x300L).getOrNull ?: return null
        return LocationContainer(lc)
    }

    val allSceneObjects: List<SceneObject>
        get() {
            val xVector = locationContainers
            if (xVector.begin.address() == xVector.end.address()) return emptyList()
            val result = mutableListOf<SceneObject>()
            for (i in 0 until xVector.size.toInt()) {
                val yVector = Vector(xVector[i], 0x8L)
                if (yVector.begin.address() == yVector.end.address()) continue
                for (j in 0 until yVector.size.toInt()) {
                    val lc = yVector[j].deref(0L, 0x300L).getOrNull ?: continue
                    result += LocationContainer(lc).allSceneObjects
                }
            }
            return result
        }
}

class LocationContainer(ptr: MemorySegment) : Entity(ptr) {
    val allSceneObjects: List<SceneObject>
        get() = graphNode.sceneObjects

    @Deprecated("Slot-based traversal — kept for future R&D when the rbtree at LC+0x8/+0x10 and dispatch slots at LC+0xF8 are wired up.")
    val allSceneObjectsManual: List<SceneObject>
        get() = (
            slot(OLocationContainer.PRIMARY_LOCATIONS_BEGIN) +
            slot(OLocationContainer.MULTI_LOCATIONS_BEGIN) +
            slot(OLocationContainer.TERTIARY_LOCATIONS_BEGIN) +
            slot(OLocationContainer.DYNAMIC_LOCATIONS_BEGIN)
        ).mapNotNull { entry ->
            val locPtr = entry.deref(OLocationContainer.ENTRY_LOCATION_PTR_OFFSET, 0x300L).getOrNull ?: return@mapNotNull null
            when (EntityType.fromType(locPtr.get(JAVA_BYTE, OEntity.ENTITY_TYPE).toInt())) {
                EntityType.LOCATION -> Location(locPtr)
                EntityType.COMBINED_LOCATION_SECTION -> CombinedLocationSection(locPtr)
                else -> null
            }
        }.associateBy { Objects.hash(it.shape, it.tile) }.values.toList()

    private fun slot(vectorTripleOffset: Long): Sequence<MemorySegment> {
        val vec = Vector(ptr.pointerAtOffset(vectorTripleOffset, 0x20L), OLocationContainer.ENTRY_STRIDE)
        return (0 until vec.size.toInt()).asSequence().map { vec[it] }
    }
}
