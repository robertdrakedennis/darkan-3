package com.undercut.game.scene

import com.undercut.cache.type.maps.ObjectShape
import com.undercut.cache.type.objects.ObjectType
import com.undercut.game.Tile
import com.undercut.game.nxt.entity.location.SceneObject
import java.lang.foreign.MemorySegment

class CachedSceneObject(
    override val memPointer: MemorySegment = MemorySegment.NULL,
    override val id: Int,
    override val typeId: Int,
    override val tile: Tile,
    override val shape: ObjectShape,
    override val rotation: Byte
) : SceneObject {
    override val defs
        get() = ObjectType.get(typeId)
    override val exists: Boolean = true
    override val graphNode = null
}