package com.undercut.game.nxt.entity

import com.undercut.game.math.Vector3f
import com.undercut.game.math.Vector3i
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readFloat
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.EntityType
import com.undercut.game.nxt.MapSquare
import com.undercut.game.nxt.OEntity
import com.undercut.game.nxt.OGraphNode
import com.undercut.game.nxt.entity.location.CombinedLocation
import com.undercut.game.nxt.entity.location.CombinedLocationSection
import com.undercut.game.nxt.entity.location.Location
import com.undercut.game.nxt.entity.location.SceneObject
import com.undercut.game.nxt.entity.npc.NPC
import com.undercut.game.nxt.entity.player.Player
import com.undercut.game.nxt.types.Vector
import java.lang.foreign.MemorySegment

class GraphNode(val ptr: MemorySegment) {

    val direction: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.DIRECTION_X), ptr.readFloat(OGraphNode.DIRECTION_Y), ptr.readFloat(OGraphNode.DIRECTION_Z))

    val scene: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.SCENE_X), ptr.readFloat(OGraphNode.SCENE_Y), ptr.readFloat(OGraphNode.SCENE_Z))

    val tile: Vector3i
        get() = Vector3i((ptr.readFloat(OGraphNode.SCENE_X) / 512f).toInt(), (ptr.readFloat(OGraphNode.SCENE_Y) / 512f).toInt(), (ptr.readFloat(OGraphNode.SCENE_Z) / 1024f).toInt())

    val tileFine: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.SCENE_X), ptr.readFloat(OGraphNode.SCENE_Y), ptr.readFloat(OGraphNode.SCENE_Z))

    /** Current transformed AABB minimum corner. */
    val boundsMin: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.BOUNDS_MIN_X), ptr.readFloat(OGraphNode.BOUNDS_MIN_Y), ptr.readFloat(OGraphNode.BOUNDS_MIN_Z))

    /** Current transformed AABB maximum corner. */
    val boundsMax: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.BOUNDS_MAX_X), ptr.readFloat(OGraphNode.BOUNDS_MAX_Y), ptr.readFloat(OGraphNode.BOUNDS_MAX_Z))

    /** Source/untransformed AABB minimum corner. */
    val srcBoundsMin: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.SRC_BOUNDS_MIN_X), ptr.readFloat(OGraphNode.SRC_BOUNDS_MIN_Y), ptr.readFloat(OGraphNode.SRC_BOUNDS_MIN_Z))

    /** Source/untransformed AABB maximum corner. */
    val srcBoundsMax: Vector3f
        get() = Vector3f(ptr.readFloat(OGraphNode.SRC_BOUNDS_MAX_X), ptr.readFloat(OGraphNode.SRC_BOUNDS_MAX_Y), ptr.readFloat(OGraphNode.SRC_BOUNDS_MAX_Z))

    /** GraphNode flags. Bit 0x08 = has bounding box. */
    val flags: Int
        get() = ptr.readInt(OGraphNode.FLAGS)

    /** Whether this node has a valid bounding box. */
    val hasBounds: Boolean
        get() = (flags and 0x08) != 0

    val frameCount
        get() = ptr.readInt(OGraphNode.FRAME_COUNT)

    val entity: Entity?
        get() {
            val entity = ptr.deref(OGraphNode.ENTITY, 0x2000L).getOrNull ?: return null
            val type = EntityType.fromType(entity.readByte(OEntity.ENTITY_TYPE).toInt())
            return when(type) {
                EntityType.LOCATION -> Location(entity)
                EntityType.NPC_ENTITY -> NPC(entity)
                EntityType.PLAYER_ENTITY -> Player(entity)
                EntityType.OBJ_STACK -> ItemStack(entity)
                EntityType.SPOT_ANIMATION -> SpotAnim(entity)
//                EntityType.PROJECTILE_ANIMATION -> TODO()
//                EntityType.TERRAIN -> TODO()
//                EntityType.WATER -> TODO()
                EntityType.COMBINED_LOCATION -> CombinedLocation(entity)
//                EntityType.LOCATION_CONTAINER -> TODO()
                EntityType.MAP_SQUARE -> MapSquare(entity)
//                EntityType.LIGHT_SOURCE -> TODO()
                EntityType.COMBINED_LOCATION_SECTION -> CombinedLocationSection(entity)
                EntityType.HINT_ARROW -> HintArrow(entity)
                EntityType.HINT_ARROW_POINTER -> HintArrow(entity)
                else -> null
            }
        }

    val childEntities: List<Entity>
        get() = children.mapNotNull {
            if (it.address() == 0L) return@mapNotNull null
            val derefed = it.deref(size = 0x200).getOrNull ?: return@mapNotNull null
            return@mapNotNull GraphNode(derefed).entity
        }

    val sceneObjects: List<SceneObject>
        get() = childEntities.flatMap { entity ->
            when (entity) {
                // combinedLocationSections (NOT the raw graph-node recursion) drops hidden/replaced
                // sections at the source — e.g. a chopped tree's hidden section under its stump.
                is CombinedLocation -> entity.combinedLocationSections
                is CombinedLocationSection -> if (entity.hidden) emptyList() else listOf(entity)
                is Location -> if (entity.isHidden || entity.isDeleted) emptyList() else listOf(entity)
                else -> emptyList()
            }
        }

    val itemStacks: List<ItemStack>
        get() = childEntities.filter { it is ItemStack }.map { it as ItemStack }

    val spotAnims: List<SpotAnim>
        get() = childEntities.filter { it is SpotAnim }.map { it as SpotAnim }

    val children : Vector
        get() = Vector(ptr.pointerAtOffset(OGraphNode.CHILDREN, 0x20L))
}