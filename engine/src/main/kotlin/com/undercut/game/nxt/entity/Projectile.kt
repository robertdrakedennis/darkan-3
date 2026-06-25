package com.undercut.game.nxt.entity

import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readShort
import com.undercut.game.nxt.OProjectile
import java.lang.foreign.MemorySegment

class Projectile(ptr: MemorySegment) : Entity(ptr) {
    val id
        get() = ptr.readInt(OProjectile.ID)
    val lockedToServerIndex
        get() = ptr.readShort(OProjectile.LOCKON_SERVER_INDEX).toInt()

    fun lockedOnto(pathingEntity: PathingEntity) = lockedToServerIndex == pathingEntity.serverIndex
}