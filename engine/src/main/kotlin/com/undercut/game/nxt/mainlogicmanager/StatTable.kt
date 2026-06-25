package com.undercut.game.nxt.mainlogicmanager

import com.undercut.game.Skill
import com.undercut.game.memory.NativeAccess.pointerAtOffset
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.OStat
import com.undercut.game.nxt.OStatTable
import com.undercut.game.nxt.types.Vector
import java.lang.foreign.MemorySegment

class StatTable(val ptr: MemorySegment) : Iterable<Stat> {
    val size: Int
        get() = ptr.readInt(OStatTable.TABLE_SIZE)
    val stats
        get() = Vector(ptr.pointerAtOffset(OStatTable.TABLE_BEGIN, 0x20L), OStatTable.ENTRY_SIZE)

    operator fun get(skill: Skill): Stat {
        return Stat(stats[skill.ordinal])
    }

    override fun iterator(): Iterator<Stat> {
        return object : Iterator<Stat> {
            private var index = 0
            override fun hasNext(): Boolean {
                return index < size
            }

            override fun next(): Stat {
                val stat = get(Skill.entries[index])
                index++
                return stat
            }
        }
    }
}

class Stat(val ptr: MemorySegment) {
    val xp: Int
        get() = ptr.readInt(OStat.EXPERIENCE)
    val unkInt32_0: Int
        get() = ptr.readInt(OStat.UNKINT32_0)
    val realLevel: Int
        get() = ptr.readInt(OStat.REAL_LEVEL)
    val currentLevel: Int
        get() = ptr.readInt(OStat.CURRENT_LEVEL)
}