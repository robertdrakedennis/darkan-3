package org.darkan.world.interfaces

import org.darkan.core.net.prot.IfCloseSub
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.world.entity.Player
import java.util.concurrent.ConcurrentHashMap

/**
 * Per-player interface bookkeeping for the world game frame.
 *
 * [openSubs] tracks the placements actually served to the client so IF_BUTTON input can be gated
 * against live UI state. Placement emits our verified rev948 IF_SETPOSITION shape: child interface id
 * in [IfSetPosition.componentId], layer 1, and parent component hash in [IfSetPosition.position].
 */
class InterfaceManager(private val player: Player) {

    private val openSubs: MutableMap<Int, Int> = ConcurrentHashMap()

    var top: Top = Top.TOPLEVEL_V2
        private set

    suspend fun open(sub: GameInterface, childId: Int = sub.childId) {
        open(sub.hash, childId)
        sub.onOpen?.invoke(player)
    }

    suspend fun open(componentHash: Int, childId: Int) {
        openSubs.put(componentHash, childId)?.let { clearChildren(it) }
        player.session.send(
            IfSetPosition(
                componentId = childId,
                layer = LAYER,
                position = componentHash,
            )
        )
    }

    suspend fun close(sub: GameInterface) = close(sub.hash)

    suspend fun close(componentHash: Int) {
        val removed = openSubs.remove(componentHash) ?: return
        clearChildren(removed)
        player.session.send(IfCloseSub(componentHash = componentHash))
    }

    fun isOpen(sub: GameInterface): Boolean = openSubs.containsKey(sub.hash)

    fun isOpen(interfaceId: Int): Boolean = interfaceId == top.id || openSubs.containsValue(interfaceId)

    fun forHash(componentHash: Int): GameInterface? = GameInterface.forHash(componentHash)

    fun validateClick(interfaceHash: Int): Boolean = isOpen(interfaceHash ushr 16)

    fun clearChildren(parentInterfaceId: Int) {
        openSubs.keys.removeAll { it ushr 16 == parentInterfaceId }
    }

    companion object {
        const val LAYER = 1
    }
}
