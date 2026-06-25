package com.undercut.game.nxt.entity.npc

import com.undercut.cache.type.npcs.NPCType
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.memory.NativeAccess.readByte
import com.undercut.game.memory.NativeAccess.readInt
import com.undercut.game.nxt.DoActionOpcode
import com.undercut.game.nxt.ONPC
import com.undercut.game.nxt.entity.PathingEntity
import com.undercut.script.api.combatTarget
import java.lang.foreign.MemorySegment

private val MENU_OPS = arrayOf(
    DoActionOpcode.NPC_1,
    DoActionOpcode.NPC_2,
    DoActionOpcode.NPC_3,
    DoActionOpcode.NPC_4,
    DoActionOpcode.NPC_5,
    DoActionOpcode.NPC_6
)

class NPC(ptr: MemorySegment) : PathingEntity(ptr) {
    val id: Int
        get() = ptr.readInt(ONPC.ID)

    val typeId: Int
        get() = ptr.readInt(ONPC.TYPE_ID)

    val hiddenMenuOpFlags: Int
        get() = ptr.readByte(ONPC.HIDDEN_MENUOP_FLAGS).toInt()

    val renderAnim: Int
        get() = ptr.readInt(ONPC.RENDER_ANIM)

    val currentHealth: Int
        get() = ptr.readInt(ONPC.CURRENT_HP)

    val maxHealth: Int
        get() = ptr.readInt(ONPC.MAX_HP)

    val isCombatTarget
        get() = exists() && serverIndex == combatTarget?.serverIndex

    private val _firstServerIndex = serverIndex

    fun exists() = _firstServerIndex == serverIndex && Bootstrap.client.npcManager[serverIndex] != null

    fun interact(action: Int): Boolean {
        if (!exists()) return false
        if (action < 0 || action >= MENU_OPS.size) return false
        val doAction = MENU_OPS.getOrNull(action) ?: return false
        if (getDef().getOp(action) == "null") return false
        doAction.fire(serverIndex, 0, 0)
        return true
    }

    fun interact(action: String): Boolean {
        if (!exists()) return false
        val op = getDef().getOpIdForName(action)
        return if (op != -1) {
            interact(op)
            true
        } else {
            false
        }
    }

    fun target(): Boolean {
        if (!exists()) return false
        DoActionOpcode.SELECT_NPC.fire(serverIndex, 0, 0)
        return true
    }

    fun getDef(): NPCType = NPCType.get(if (typeId == -1) id else typeId)

    fun hasOption(option: String): Boolean = getDef().containsOp(option)

    fun name(): String = getDef().name
}