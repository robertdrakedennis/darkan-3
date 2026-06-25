package com.undercut.game.nxt.entity

import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.NativeAccess.getOrNull
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.memory.eastl.EastlLinkedList
import com.undercut.game.nxt.OHeadbar
import com.undercut.game.nxt.OHit
import com.undercut.game.nxt.OHitmarksAndHeadbars
import java.lang.foreign.MemorySegment

class HitmarksAndHeadbars(val ptr: MemorySegment) {
    val headbars
        get() = ptr.getOrNull?.let { basePtr ->
            (0..5).asSequence()
                .map { i -> EastlLinkedList(basePtr.deref(OHitmarksAndHeadbars.HEADBAR_LINKEDLIST_VECTOR_START, 0x1B0 * 5).pointerAtOffset(i * 0x1B0L, 0x1B0L))  }
                .takeWhile { it.size > 0 }
                .flatMap { it.map { node -> Headbar(node.value(0x100)) } }
                .toList()
        }
    val hits
        get() = ptr.getOrNull?.let { HitArray(it.deref(OHitmarksAndHeadbars.HIT_VECTOR, 0x100L), 8).toList() }
}

class HitArray(val ptr: MemorySegment, val size: Int) : Iterable<Hit> {
    fun get(index: Int) = Hit(ptr.pointerAtOffset(index * 0x18L, 0x18L))

    override fun iterator(): Iterator<Hit> {
        return object : Iterator<Hit> {
            private var index = 0
            override fun hasNext() = index < size
            override fun next() = get(index++)
        }
    }
}

enum class HitType(val legacy: Int, val legacyCrit: Int, val legacyOther: Int) {
    MISS(482, -1, -1),
    DODGE(141, -1, -1),
    MELEE(133, 134, 150),
    RANGED(136, 137, -1),
    MAGIC(139, 140, -1),
    TYPELESS(144, -1, -1),
    REFLECTED(146, -1, -1),
    NECROMANCY(477, 478, -1),
    NECROMANCY_CONJURE(480, -1, -1),
    UNKNOWN(-1, -1, -1);

    companion object {
        private val BY_ID = entries.associateBy(HitType::legacy) + entries.associateBy(HitType::legacyCrit) + entries.associateBy(HitType::legacyOther)
        fun byId(id: Int) = BY_ID[id] ?: UNKNOWN
    }
}

class Hit(val ptr: MemorySegment) {
    val typeId
        get() = ptr.readInt(OHit.TYPE)
    val type = HitType.byId(typeId)
    val damage
        get() = ptr.readInt(OHit.DAMAGE)
    val createdClientcycle
        get() = ptr.readInt(OHit.CLIENTCYCLE_CREATED)
    val unk1
        get() = ptr.readInt(OHit.UNKNEG1_1)
    val unk2
        get() = ptr.readInt(OHit.UNKNEG1_2)
    val durationClientcycles
        get() = ptr.readInt(OHit.DURATION_CLIENTCYLES)
    val durationMillis
        get() = durationClientcycles * 20L
    val cyclesLeft
        get() = durationClientcycles - (Bootstrap.client.clientCycle - createdClientcycle)
    val timeLeftMillis
        get() = cyclesLeft * 20L

    override fun toString() = "[${if (type == HitType.UNKNOWN) "UNKNOWN($typeId)" else type}, ${damage}]"
}

class Headbar(val ptr: MemorySegment) {
    val typePtr
        get() = ptr.deref(OHeadbar.TYPE_PTR, 0x20L)
    val type
        get() = typePtr.readInt(OHeadbar.TYPE_ID)
    val createdClientcycle
        get() = ptr.readInt(OHeadbar.CLIENTCYCLE_CREATED)
    val fromFill
        get() = ptr.readInt(OHeadbar.FROM_FILL)
    val toFill
        get() = ptr.readInt(OHeadbar.TO_FILL)
    val durationClientcycles
        get() = ptr.readInt(OHeadbar.DURATION_CLIENTCYCLES)
    val durationMillis
        get() = durationClientcycles * 20L
    val cyclesLeft
        get() = durationClientcycles - (Bootstrap.client.clientCycle - createdClientcycle)
    val timeLeftMillis
        get() = cyclesLeft * 20L
}