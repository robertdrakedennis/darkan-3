package com.undercut.game.nxt.entity

import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.nxt.EntityType
import com.undercut.game.nxt.OEntity
import java.lang.foreign.MemorySegment

class EntityTypeContainer(val ptr: MemorySegment) {
    val type: EntityType
        get() = EntityType.fromType(ptr.readByte(OEntity.ENTITY_TYPE).toInt())
}