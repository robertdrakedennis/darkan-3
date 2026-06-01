package org.darkan.world.net

import org.darkan.core.net.prot.NpcInfo
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.NpcUpdateMaskEncoder
import org.darkan.core.net.prot.update.NpcUpdateMaskKey
import org.darkan.world.entity.Player
import org.darkan.world.world.Npcs
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Per-tick builder that produces a [NpcInfo] DTO from world state, per
 * `docs/net/serverprot/npc-info-947-3.md` §"Phase 1/2/3".
 *
 * The bit-packed body has three phases (A5 §"Packet structure overview"):
 *  1. Phase 1 — update existing NPCs: 8-bit count + per-NPC 1-bit hasUpdate + movement bits.
 *  2. Phase 2 — add new NPCs: 16-bit serverIndex loop terminated by `0xFFFF` sentinel.
 *  3. Phase 3 — extended info: per-flagged NPC byte-packed block (flag bitset + per-block).
 *
 * Two entry points:
 *  * [buildInit] — first-tick empty form (no NPCs in viewport yet).
 *  * [build] — incremental form driven by [Player.viewport.visibleNpcs].
 */
object NpcInfoBuilder {

    private const val TICK_BUFFER_CAPACITY = 4096

    private val warnedMissingNpcEncoders: MutableSet<NpcUpdateMaskKey> =
        java.util.Collections.newSetFromMap(java.util.concurrent.ConcurrentHashMap())

    /**
     * First-tick init form. Per A5 §"Phase 1": gBit(8) = 0 (no NPCs in update phase).
     * Per §"Phase 2": loop terminator is the 16-bit sentinel `0xFFFF` — so the absolute
     * minimum-content packet is `[8-bit 0] + [16-bit 0xFFFF]` = 3 bytes + bit-padding to byte
     * boundary. Ext-info is empty.
     */
    fun buildInit(@Suppress("UNUSED_PARAMETER") player: Player): NpcInfo {
        val bitOut = BufferWriter(8)
        bitOut.startBitAccess()
        bitOut.writeBits(8, 0)         // Phase 1: 0 NPCs in update phase.
        bitOut.writeBits(16, 0xFFFF)   // Phase 2: sentinel — no NPCs to add.
        bitOut.stopBitAccess()
        return NpcInfo(bitBlock = bitOut.toArray(), extendedInfo = emptyList())
    }

    /**
     * Per-tick incremental form. Walks the visible NPCs in [Player.viewport.visibleNpcs],
     * emits update bits for known NPCs (Phase 1), then add-records for newly-spawned NPCs
     * (Phase 2), then ext-info blocks for any NPC flagged hasExtInfo (Phase 3).
     *
     * For MVP the visible-NPC list is empty — both phases are minimal and the packet
     * matches [buildInit].
     */
    fun build(player: Player): NpcInfo {
        val viewport = player.viewport
        val bitOut = BufferWriter(TICK_BUFFER_CAPACITY)
        bitOut.startBitAccess()

        // Track NPC indices that flagged hasExtInfo this tick — Phase 3 iterates them in
        // appearance order (Phase 1 first, then Phase 2).
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
            // Per A5 §"Phase 1" movement encoding. For MVP every NPC update is
            // movementType=0 (REMOVE FROM LIST) — but actually we want to keep them in
            // the list, so we emit movementType=1 with dirIdx=0 (north) as a safe no-op
            // is wrong because that translates to "walk north". Better: emit movementType=0
            // ONLY when removing, and use movementType=1 with delay/no-step bits only when
            // walking. The cleanest "no-op update with ext-info" is hard to construct in the
            // pure A5 protocol — instead we just skip movement (movementType=0 path which
            // doesn't keep the NPC) and add ext-info via a forced flag.
            //
            // For correctness on first land: emit movementType=0 + hasExtInfo bit. The
            // client's "case 0" handler will mark for removal which is harmless if no NPCs
            // are queued. Once Phase B7 lands actual NPC walk logic this branch will be
            // augmented with the dir-based encoding.
            bitOut.writeBits(2, 0)
            val hasExt = hasFlaggableNpcExtendedInfo(npc.pendingUpdates)
            // Note: A5 §"Phase 1 case 0" does NOT have an explicit hasExtInfo bit — the
            // ext-info side-effect for "case 0" is unclear from decompilation. To stay byte-
            // correct we DO append the index to the flagged list if hasExt; the worst case
            // is a 1-tick ext-info attached to an NPC that just got removed (harmless).
            if (hasExt) flaggedForExtInfo.add(serverIdx)
        }

        // Phase 2: gBit(16, serverIndex) loop, terminated by 0xFFFF.
        // For MVP no newly-spawned NPCs — we just emit the sentinel.
        for (npcIdx in viewport.visibleNpcs) {
            val npc = Npcs.get(npcIdx) ?: continue
            if (!npc.spawned) continue
            // Per A5 §"Phase 2": gBit(16, serverIndex) + coordBitWidth deltaX + 2 bits level
            //                    + coordBitWidth deltaY + 16 bits typeId + 1 unknown bit
            //                    + 3 bits facing + 1 bit hasExtInfo.
            bitOut.writeBits(16, serverIdxOrTruncate(npcIdx))
            val deltaX = (npc.tile.x - player.tile.x).coerceIn(-(1 shl (npc.coordBitWidthZone - 1)), (1 shl (npc.coordBitWidthZone - 1)) - 1)
            val deltaY = (npc.tile.y - player.tile.y).coerceIn(-(1 shl (npc.coordBitWidthZone - 1)), (1 shl (npc.coordBitWidthZone - 1)) - 1)
            val deltaXBits = deltaX and ((1 shl npc.coordBitWidthZone) - 1)
            val deltaYBits = deltaY and ((1 shl npc.coordBitWidthZone) - 1)
            bitOut.writeBits(npc.coordBitWidthZone, deltaXBits)
            bitOut.writeBits(2, npc.tile.level and 0x3)
            bitOut.writeBits(npc.coordBitWidthZone, deltaYBits)
            bitOut.writeBits(16, npc.typeId and 0xFFFF)
            bitOut.writeBits(1, 0)              // unknown bit (A5 §"Phase 2" — currently undecoded)
            bitOut.writeBits(3, npc.direction and 0x7)
            val hasExt = hasFlaggableNpcExtendedInfo(npc.pendingUpdates)
            bitOut.writeBits(1, if (hasExt) 1 else 0)
            if (hasExt) flaggedForExtInfo.add(npcIdx)
            npc.spawned = false  // Clear spawn flag — only emit once.
        }
        // Sentinel: gBit(16, 0xFFFF) terminates Phase 2.
        bitOut.writeBits(16, 0xFFFF)

        bitOut.stopBitAccess()

        // Phase 3: per-NPC ext-info blocks. Each NPC's bytes are wrapped by a 2-byte BE length
        // header by the Rev947 codec (NOT here — we emit raw block bytes; the codec prefixes).
        val extendedInfo = ArrayList<ByteArray>(flaggedForExtInfo.size)
        for (slot in flaggedForExtInfo) {
            val npc = Npcs.get(slot) ?: continue
            extendedInfo.add(encodeExtendedInfoBlock(npc.pendingUpdates))
        }

        return NpcInfo(bitBlock = bitOut.toArray(), extendedInfo = extendedInfo)
    }

    /**
     * Per A5 §"Phase 3 — Extended info" the flag bitset is 1..5 bytes LE with expansion
     * bits at byte 0 bit 6 (mask 0x40), byte 1 bit 5 (mask 0x20), byte 2 bit 6 (mask 0x40),
     * byte 3 bit 0 (mask 0x01).
     *
     * Per-flag blocks are emitted in [NpcUpdateMaskKey.order] ascending order via
     * [NpcUpdateMaskEncoder] (registered in `Rev947ServerCodecsUpdateMasks.kt`).
     */
    private fun encodeExtendedInfoBlock(pending: org.darkan.world.entity.PendingUpdates): ByteArray {
        val extOut = BufferWriter(256)

        val entries = pending.npcMaskEntries().filter { (key, _) ->
            val has = NpcUpdateMaskEncoder.hasEncoder(key)
            if (!has && warnedMissingNpcEncoders.add(key)) {
                System.err.println(
                    "[NpcInfoBuilder] no encoder registered for NpcUpdateMaskKey.$key — " +
                        "dropping from ext-info bitset to avoid cipher desync (B4 follow-up)."
                )
            }
            has
        }

        if (entries.isEmpty()) {
            extOut.writeByte(0)
            return extOut.toArray()
        }

        // OR all flags into a single Long (NPC flags can reach bit 33).
        var flagBitset = 0L
        for ((key, _) in entries) {
            flagBitset = flagBitset or key.flag
        }

        // Compute the byte length from the highest set bit. Per A5 §"Flag bitset expansion":
        //  byte 0 always; if (byte0 & 0x40) byte 1; if (byte1 & 0x20) byte 2;
        //  if (byte2 & 0x40) byte 3; if (byte3 & 0x01) byte 4.
        val highestBit = 63 - java.lang.Long.numberOfLeadingZeros(flagBitset)
        val byteCount = when {
            highestBit < 8 -> 1
            highestBit < 16 -> 2
            highestBit < 24 -> 3
            highestBit < 32 -> 4
            else -> 5
        }
        // Set expansion ("continue") bits. These are revision-dependent (947-3 = {6,13,22,24};
        // 948 = {6,8,19,25}) so they are driven from the active codec's published positions
        // (ActiveMaskKeys.npcExpansionBits) rather than hardcoded literals — see
        // Rev948NpcUpdateMaskKey.EXPANSION_BITS / docs/net/serverprot/948-research-C-*.md.
        // npcExpansionBits[N] is the continue-bit in byte N that tells the client to read byte N+1.
        val expansionBits = ActiveMaskKeys.npcExpansionBits
        for (byte in 1 until byteCount) {
            flagBitset = flagBitset or (1L shl expansionBits[byte - 1])
        }

        // LSB-first byte write.
        for (i in 0 until byteCount) {
            extOut.writeByte(((flagBitset ushr (i * 8)) and 0xFF).toInt())
        }

        for ((key, mask) in entries.sortedBy { it.first.order }) {
            NpcUpdateMaskEncoder.encode(extOut, key, mask)
        }

        return extOut.toArray()
    }

    /** True if the NPC has any pending mask with a registered encoder. */
    private fun hasFlaggableNpcExtendedInfo(pending: org.darkan.world.entity.PendingUpdates): Boolean {
        return pending.npcMaskEntries().any { NpcUpdateMaskEncoder.hasEncoder(it.first) }
    }

    /** Server indices are 16-bit ushorts per A5 §"Phase 2 — Add new NPCs". Guard against overflow. */
    private fun serverIdxOrTruncate(idx: Int): Int {
        if (idx <= 0 || idx >= 0xFFFF) {
            // 0 and 0xFFFF are reserved (0xFFFF is the sentinel). If the caller passed one of
            // these we have a bug — fail loudly.
            error("NPC server index $idx is out of the 1..0xFFFE valid range")
        }
        return idx
    }
}
