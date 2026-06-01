package org.darkan.world.entity

import org.darkan.core.net.prot.update.NpcUpdateMaskKey
import org.darkan.core.net.prot.update.PlayerUpdateMaskKey
import org.darkan.core.net.prot.update.UpdateMask

/**
 * Per-tick update-mask collector held on every [Entity]. Game logic registers masks
 * via [setPlayer] / [setNpc] during a tick; the PlayerInfo / NpcInfo builders in B6
 * read them out (via [playerMaskEntries] / [npcMaskEntries]) and finally [clear] at
 * tick end.
 *
 * Masks are keyed by [PlayerUpdateMaskKey] / [NpcUpdateMaskKey] (the interface types —
 * revision-specific enums implement them). Overwrites of the same block in a single tick
 * keep only the latest payload (matches the legacy darkan / OSRS semantics — clients see
 * the most recent value when several mutate the same block in one frame).
 *
 * Note: NPC mask flags are 64-bit per A5 §"Flag block table" (bits 32, 33 exceed 32-bit
 * width); [npcMaskFlags] therefore returns [Long].
 */
class PendingUpdates {
    private val playerMasks = LinkedHashMap<PlayerUpdateMaskKey, UpdateMask>()
    private val npcMasks = LinkedHashMap<NpcUpdateMaskKey, UpdateMask>()

    fun setPlayer(key: PlayerUpdateMaskKey, mask: UpdateMask) {
        playerMasks[key] = mask
    }

    fun setNpc(key: NpcUpdateMaskKey, mask: UpdateMask) {
        npcMasks[key] = mask
    }

    fun hasPlayerUpdates(): Boolean = playerMasks.isNotEmpty()
    fun hasNpcUpdates(): Boolean = npcMasks.isNotEmpty()

    /** OR of [PlayerUpdateMaskKey.flag] for every queued player mask this tick. */
    fun playerMaskFlags(): Int {
        var f = 0
        for (k in playerMasks.keys) f = f or k.flag
        return f
    }

    /** OR of [NpcUpdateMaskKey.flag] for every queued NPC mask this tick (Long because NPC bits 32, 33 exceed Int width). */
    fun npcMaskFlags(): Long {
        var f = 0L
        for (k in npcMasks.keys) f = f or k.flag
        return f
    }

    /** Entries sorted by [PlayerUpdateMaskKey.order] — the order the client dispatches blocks. */
    fun playerMaskEntries(): List<Pair<PlayerUpdateMaskKey, UpdateMask>> =
        playerMasks.entries.sortedBy { it.key.order }.map { it.toPair() }

    /** Entries sorted by [NpcUpdateMaskKey.order] — the order the client dispatches blocks. */
    fun npcMaskEntries(): List<Pair<NpcUpdateMaskKey, UpdateMask>> =
        npcMasks.entries.sortedBy { it.key.order }.map { it.toPair() }

    fun clear() {
        playerMasks.clear()
        npcMasks.clear()
    }
}
