package com.undercut.game.nxt.entity

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OSpotAnim
import java.lang.foreign.MemorySegment

class SpotAnim(ptr: MemorySegment) : Entity(ptr) {
    val id
        get() = ptr.readInt(OSpotAnim.ID)
    val createdClientcycle
        get() = ptr.readInt(OSpotAnim.CREATED_CLIENTCYCLE)
    val cyclesAlive
        get() = Bootstrap.client.clientCycle - createdClientcycle
    val timeAliveMillis
        get() = cyclesAlive * 20L
}