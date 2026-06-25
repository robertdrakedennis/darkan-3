package com.undercut.game.nxt.entity

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.NativeAccess.readLong
import com.undercut.game.memory.eastl.EastlString
import com.undercut.game.nxt.OPathingEntity
import java.lang.foreign.MemorySegment

abstract class PathingEntity(ptr: MemorySegment) : Entity(ptr) {
    val name: String
        get() = EastlString(ptr.pointerAtOffset(OPathingEntity.NAME, 0x24)).toString()

    val serverIndex
        get() = ptr.readInt(OPathingEntity.SERVER_INDEX)

    val routeWaypointManager
        get() = ptr.deref(OPathingEntity.ROUTE_WAYPOINT_MANAGER, 0x300L)

    val isMoving: Boolean
        get() = routeWaypointManager.readLong(OPathingEntity.WAYPOINT_COUNT) > 0

    val isInteracting
        get() = interactionSid != -1

    val isAniMoving: Boolean
        get() = isAnimating || isMoving

    val interactionSid
        get() = ptr.readInt(OPathingEntity.INTERACTING_NPC_SID)

    val hitmarksAndHeadbars
        get() = ptr.deref(OPathingEntity.HITMARKS_AND_HEADBARS, 0x1200L).getOrNull?.let { HitmarksAndHeadbars(it) }

    val headbars
        get() = hitmarksAndHeadbars?.headbars ?: emptyList()

    val hits
        get() = hitmarksAndHeadbars?.hits?.filter { it.typeId > 0 && it.timeLeftMillis > 0 } ?: emptyList()

    fun interactingWith(target: PathingEntity) = target.serverIndex == interactionSid
}