package org.darkan.world.net

import org.darkan.core.net.prot.NpcInfo
import org.darkan.world.entity.Player
import org.darkan.world.world.Npcs
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Orchestrator for op12 NPC_INFO. Single responsibility: drive the **three phases** (Phase 1 update
 * existing, Phase 2 add new, Phase 3 ext-info) over the viewer's visible-NPC cohort and assemble the
 * [NpcInfo] DTO, delegating the per-NPC bytes to [NpcMovementEncoder] (position / movement) and
 * [NpcExtInfoEncoder] (the ext-info block + `hasExtInfo` gating). Per
 * `docs/kb/glossary/player-npc-info.md` §"s2c op52 — NpcInfo framing":
 *  1. Phase 1 — update existing NPCs: 8-bit count + per-NPC 1-bit hasUpdate + movement bits.
 *  2. Phase 2 — add new NPCs: 16-bit serverIndex loop terminated by the `0xFFFF` sentinel.
 *  3. Phase 3 — extended info: per-flagged NPC byte-packed block.
 *
 * Two public entry points map to the two live call sites:
 *  * [buildInit] — `WorldServer` world-entry first-tick empty form (no NPCs in viewport yet).
 *  * [build] — the `WorldTick` per-tick incremental form driven by `Player.viewport.visibleNpcs`.
 */
object NpcInfoEncoder {

    private const val TICK_BUFFER_CAPACITY = 4096

    /** 16-bit sentinel terminating Phase 2; also reserved (cannot be a valid server index). */
    private const val ADD_LIST_SENTINEL = 0xFFFF

    /**
     * First-tick init form. Per §"Phase 1": gBit(8) = 0 (no NPCs in update phase). Per §"Phase 2":
     * loop terminator is the 16-bit sentinel `0xFFFF` — so the absolute minimum-content packet is
     * `[8-bit 0] + [16-bit 0xFFFF]` = 3 bytes + bit-padding to a byte boundary. Ext-info is empty.
     *
     * Relocated unchanged from the old monolithic builder — byte output is identical.
     */
    fun buildInit(@Suppress("UNUSED_PARAMETER") player: Player): NpcInfo {
        val bitOut = BufferWriter(8)
        bitOut.startBitAccess()
        bitOut.writeBits(8, 0)                     // Phase 1: 0 NPCs in update phase.
        bitOut.writeBits(16, ADD_LIST_SENTINEL)    // Phase 2: sentinel — no NPCs to add.
        bitOut.stopBitAccess()
        return NpcInfo(bitBlock = bitOut.toArray(), extendedInfo = emptyList())
    }

    /**
     * Per-tick incremental form. Walks the visible NPCs in `Player.viewport.visibleNpcs`, emits
     * update bits for known NPCs (Phase 1), then add-records for newly-visible NPCs (Phase 2), then
     * ext-info blocks for any NPC flagged hasExtInfo (Phase 3).
     *
     * Relocated unchanged from the old monolithic builder — byte output is identical.
     */
    fun build(player: Player): NpcInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // NPC indices that flagged hasExtInfo this tick — Phase 3 iterates them in appearance order
        // (Phase 1 first, then Phase 2).
        val flaggedForExtInfo = ArrayList<Int>(8)

        // Phase 1: gBit(8, count) — count of NPCs currently in the local list.
        val existing = viewport.visibleNpcs
        bitOut.writeBits(8, existing.size and 0xFF)
        for (serverIdx in existing) {
            val npc = Npcs.get(serverIdx) ?: continue
            val hasUpdate = npc.pendingUpdates.hasNpcUpdates()
            if (!hasUpdate) {
                bitOut.writeBits(1, 0)
                continue
            }
            bitOut.writeBits(1, 1)
            NpcMovementEncoder.encodePhase1Update(bitOut)
            // §"Phase 1 case 0" has NO explicit hasExtInfo bit — the ext-info side-effect for case 0
            // is undecoded (see NpcMovementEncoder Phase-1.2b note). To stay byte-correct we DO append
            // the index to the flagged list if ext-info exists; worst case a 1-tick ext-info attached
            // to an NPC that just got removed (harmless).
            if (NpcExtInfoEncoder.hasFlaggableNpcExtendedInfo(npc.pendingUpdates)) {
                flaggedForExtInfo.add(serverIdx)
            }
        }

        // Phase 2: gBit(16, serverIndex) loop, terminated by 0xFFFF.
        // Add-records are tracked PER VIEWER via viewport.sentNpcAdds — global Npc state (like
        // Npc.spawned) must not be mutated here, otherwise only the first-built viewer each tick would
        // receive the add record. Npc.spawned is cleared once per tick by WorldTick after every viewer
        // has been built.
        for (npcIdx in viewport.visibleNpcs) {
            val npc = Npcs.get(npcIdx) ?: continue
            if (!viewport.sentNpcAdds.add(npcIdx)) continue // already added for this viewer
            val hasExt = NpcExtInfoEncoder.hasFlaggableNpcExtendedInfo(npc.pendingUpdates)
            NpcMovementEncoder.encodePhase2Add(
                out = bitOut,
                npc = npc,
                serverIndex = serverIdxOrTruncate(npcIdx),
                viewerTileX = player.tile.x,
                viewerTileY = player.tile.y,
                hasExtInfo = hasExt,
            )
            if (hasExt) flaggedForExtInfo.add(npcIdx)
        }
        // Sentinel: gBit(16, 0xFFFF) terminates Phase 2.
        bitOut.writeBits(16, ADD_LIST_SENTINEL)

        bitOut.stopBitAccess()

        // Phase 3: per-NPC ext-info blocks. Each NPC's bytes are wrapped by a 2-byte BE length header
        // by the codec (NOT here — we emit raw block bytes; the codec prefixes).
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val npc = Npcs.get(slot) ?: continue
            extendedInfo.add(NpcExtInfoEncoder.encodeExtendedInfoBlock(npc.pendingUpdates))
        }

        return NpcInfo(bitBlock = bitOut.toArray(), extendedInfo = extendedInfo)
    }

    /** Server indices are 16-bit ushorts per §"Phase 2 — Add new NPCs". 0 and 0xFFFF are reserved. */
    private fun serverIdxOrTruncate(idx: Int): Int {
        if (idx <= 0 || idx >= ADD_LIST_SENTINEL) {
            // 0 and 0xFFFF are reserved (0xFFFF is the sentinel). If the caller passed one of these we
            // have a bug — fail loudly.
            error("NPC server index $idx is out of the 1..0xFFFE valid range")
        }
        return idx
    }
}
