package org.darkan.world.entity

import world.gregs.voidps.type.Tile

/**
 * World-side NPC entity. Constructed when an NPC is spawned into the world and
 * destroyed when it despawns. Index is a 16-bit ushort allocated by [org.darkan.world.world.Npcs].
 *
 * Per NPC_INFO (op 12) Phase 2, every newly spawned NPC must be transmitted with its
 * type id, coord-relative position bits, and a "spawned this tick" flag so the client
 * can allocate render slots. [spawned] is set true on construction and cleared by the
 * NpcInfoEncoder after the first tick's Phase 2 emission.
 */
class Npc(
    override val index: Int,
    val typeId: Int,
    /**
     * Bit width of the per-zone coordinate field used for NPC_INFO Phase 2 add-entries. The client
     * takes this from op81's +6 byte; it must match the RebuildNormalSimple value or NPC adds desync.
     * Rev948 official login uses 7 (947-3 used 14).
     */
    val coordBitWidthZone: Int = 7,
    initialTile: Tile,
) : Entity() {
    init {
        tile = initialTile
    }

    /** True until the first NPC_INFO tick has emitted this NPC's spawn record in Phase 2. */
    var spawned: Boolean = true
}
