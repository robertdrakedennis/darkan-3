package com.undercut.game.nxt.entity.location

import world.gregs.voidps.type.Tile
import world.gregs.voidps.map.ObjectShape
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.toShared
import com.undercut.game.nxt.OCombinedLocationSection
import com.undercut.game.nxt.entity.Entity
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.definition.data.ObjectDefinition
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class CombinedLocationSection(ptr: MemorySegment) : Entity(ptr), SceneObject {
    override val memPointer = ptr
    override val typeId
        get() = id

    override val shape = ObjectShape.forId(ptr.readByte(OCombinedLocationSection.SHAPE).toInt())
    override val rotation
        get() = ptr.readByte(OCombinedLocationSection.ROTATION)

    override val tile: Tile
        get() = Tile.of(ptr.readInt(OCombinedLocationSection.POS_X), ptr.readInt(OCombinedLocationSection.POS_Y), plane)

    override val id: Int
        get() = realType?.id ?: 0

    override val exists: Boolean
        get() = Bootstrap.client.sceneManager.getLocationContainer(tile)?.allSceneObjects?.any { it.tile == tile && it.id == id } == true

//    val visibleId: Int
//        get() = ptr.readInt(OCombinedLocationSection.VISIBLE_ID)

    override val defs: ObjectDefinition
        get() = Cache.obj(id) ?: ObjectDefinition.EMPTY

    override val sizeX: Int
        get() = realType?.sizeX ?: 1

    override val sizeY: Int
        get() = realType?.sizeY ?: 1

    val hidden: Boolean
        get() = ptr.readByte(OCombinedLocationSection.HIDDEN) != 0.toByte()

    /** Direct render-node pointer (same layout as ORenderModel). Parent-combined access
     *  path is intentionally not exposed — entity outlining is handled by the engine via
     *  [com.undercut.game.highlight.EntityHighlight], not by chasing render-model state. */
    val renderNodeAddr: Long
        get() = ptr.get(ADDRESS, OCombinedLocationSection.RENDER_NODE).address()

    private val realType: LocationType?
        get() {
            val addr = ptr.pointerAtOffset(OCombinedLocationSection.TYPE_SHAREDPTR, 0x24L)
            return if (addr.deref(size = 0x24L).address() == 0L) null else LocationType(addr.toShared().value(0x400L))
        }
}