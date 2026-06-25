package com.undercut.game.nxt.entity.location

import com.undercut.game.Tile
import com.undercut.game.map.ObjectShape
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OLocation
import com.undercut.game.nxt.OLocationType
import com.undercut.game.nxt.entity.Entity
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class Location(ptr: MemorySegment) : Entity(ptr), SceneObject {
    override val memPointer = ptr

    val isDeleted: Boolean
        get() = ptr.readByte(OLocation.IS_DELETED) != 0.toByte()

    val isHidden: Boolean
        get() = ptr.readByte(OLocation.IS_HIDDEN) != 0.toByte()

    override val shape = ObjectShape.forId(ptr.readByte(OLocation.SHAPE).toInt())
    override val rotation
        get() = ptr.readByte(OLocation.ROTATION)

    override val tile: Tile
        get() = Tile.of(ptr.readInt(OLocation.POS_X), ptr.readInt(OLocation.POS_Y), plane)

    override val id: Int
        get() = realType?.id ?: typeId

    override val typeId: Int
        get() = ptr.readInt(OLocation.TYPE_ID)

    override val defs: ObjectDefinition
        get() = Cache.obj(id) ?: ObjectDefinition.EMPTY

    override val sizeX: Int
        get() = realType?.sizeX ?: 1

    override val sizeY: Int
        get() = realType?.sizeY ?: 1

    private val realType
        get() = ptr.deref(OLocation.ORIGINAL_TYPE, 0x400L).getOrNull?.let { LocationType(it) }

    /**
     * The render node pointer for this Location entity.
     * Same internal layout as ORenderModel (world transform at +0x30, mesh components at +0x288).
     * For PathingEntity (NPC/Player), the render model is at Entity+0xC58 instead.
     */
    val renderNodeAddr: Long
        get() = ptr.get(ADDRESS, OLocation.RENDER_NODE).address()

    override val exists: Boolean
        get() = Bootstrap.client.sceneManager.getLocationContainer(tile)?.allSceneObjects?.any { it.tile == tile && it.id == id } == true

//    val slot
//        get() = defs.shapes.slot

    override fun toString(): String {
        return "[$id (${getName()}), $type, $rotation, ${tile}, clipType: ${getDef().clipType}]"
    }
}

class LocationType(val ptr: MemorySegment) {
    val id: Int
        get() = ptr.readInt(OLocationType.ID)
    val sizeX: Int
        get() = ptr.readInt(OLocationType.SIZE_X)
    val sizeY: Int
        get() = ptr.readInt(OLocationType.SIZE_Y)
}