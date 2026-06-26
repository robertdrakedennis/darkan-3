package com.undercut.game.nxt.entity

import world.gregs.voidps.type.Tile
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.EntityType
import com.undercut.game.nxt.OEntity
import com.undercut.game.math.Vector2f
import java.lang.foreign.MemorySegment
import kotlin.math.roundToInt

abstract class Entity(val ptr: MemorySegment) {
    val graphNode: GraphNode
        get() = GraphNode(ptr.deref(OEntity.GRAPH_NODE, 0x200L))

    val type: EntityType
        get() = EntityType.fromType(ptr.readByte(OEntity.ENTITY_TYPE).toInt())

    open val tile: Tile
        get() {
            val size = size
            val tileX = ((graphNode.tileFine.x - 256f - (size shl 8).toFloat()) / 512f).roundToInt()
            val tileY = ((graphNode.tileFine.y - 256f - (size shl 8).toFloat()) / 512f).roundToInt()
            return Tile.of(tileX, tileY, plane)

        }

    open val localTile: Tile
        get() {
            val tile = tile
            return Tile.of(tile.xInRegion, tile.yInRegion, plane)
        }

    open val size
        get() = ptr.readByte(OEntity.SIZE).toInt()

    val plane: Int
        get() = ptr.readInt(OEntity.ENTITY_PLANE)

    // --- Screen-space picking data (populated by the render pipeline) ---

    /** Picking mode: 0 = point/circle test, non-zero = line segment test. */
    val pickType: Int
        get() = ptr.readInt(OEntity.PICK_TYPE)

    /** Screen center X for point-mode picking. */
    val screenCenterX: Int
        get() = ptr.readInt(OEntity.SCREEN_CENTER_X)

    /** Screen center Y for point-mode picking. */
    val screenCenterY: Int
        get() = ptr.readInt(OEntity.SCREEN_CENTER_Y)

    /** Picking radius for point-mode. */
    val pointRadius: Int
        get() = ptr.readInt(OEntity.POINT_RADIUS)

    /** Line endpoint 1 for line-mode picking. */
    val screenLine1: Vector2f
        get() = Vector2f(ptr.readInt(OEntity.SCREEN_X1).toFloat(), ptr.readInt(OEntity.SCREEN_Y1).toFloat())

    /** Line endpoint 2 for line-mode picking. */
    val screenLine2: Vector2f
        get() = Vector2f(ptr.readInt(OEntity.SCREEN_X2).toFloat(), ptr.readInt(OEntity.SCREEN_Y2).toFloat())

    /** Picking radius for line-mode. */
    val lineRadius: Int
        get() = ptr.readInt(OEntity.LINE_RADIUS)

    /** Whether this entity has valid screen-space picking data. */
    val hasPickData: Boolean
        get() = pointRadius > 0 || lineRadius > 0

    val animationId: Int
        get() = ptr.readInt(OEntity.ANIMATION_ID)

    val animation: Animation?
        get() = ptr.pointerAtOffset(OEntity.ANIMATION_SHARED_PTR, 0x10L).toShared().valueOrNull(0x50)?.let { Animation(it) }

    val spotAnims: List<SpotAnim>
        get() = graphNode.spotAnims

    val isAnimating: Boolean
        get() = animationId != -1

    val frameCount
        get() = graphNode.frameCount

}