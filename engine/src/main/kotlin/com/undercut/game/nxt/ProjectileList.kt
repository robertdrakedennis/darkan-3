package com.undercut.game.nxt

import com.undercut.game.memory.NativeAccess.deref
import com.undercut.game.memory.eastl.EastlFixedPool
import com.undercut.game.memory.eastl.EastlFixedPoolNode
import com.undercut.game.nxt.entity.Projectile
import java.lang.foreign.MemorySegment

class ProjectileList(val ptr: MemorySegment) : Iterable<Projectile> {
    val pool
        get() = EastlFixedPool(ptr)

    override fun iterator(): Iterator<Projectile> = ProjectileIterator(pool.iterator())

    private class ProjectileIterator(private val nodeIterator: Iterator<EastlFixedPoolNode>) : Iterator<Projectile> {
        override fun hasNext(): Boolean = nodeIterator.hasNext()

        override fun next(): Projectile {
            val node = nodeIterator.next()
            return Projectile(node.value(0x8L).deref(size = 0x200L))
        }
    }
}