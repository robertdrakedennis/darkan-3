package org.darkan.world.net

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import org.darkan.core.net.prot.update.ActiveMaskKeys
import org.darkan.core.net.prot.update.NpcUpdateMaskEncoder
import org.darkan.core.net.prot.update.NpcUpdateMaskKey
import org.darkan.core.net.prot.update.UpdateMaskHeader
import org.darkan.world.entity.PendingUpdates
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Single responsibility: encode the **NPC_INFO Phase 3 extended-info byte block** for one NPC — the
 * flag-bitset header + the per-block payloads — and answer the [hasFlaggableNpcExtendedInfo] gating
 * question the Phase 1 / Phase 2 emitters ask. It owns no phase/cohort/movement logic; the
 * orchestrator ([NpcInfoEncoder]) and movement encoder ([NpcMovementEncoder]) call into it.
 *
 * Wire reference (relocated verbatim — do not delete): per §"Phase 3 — Extended info" the flag
 * bitset is 1..5 bytes LE with expansion bits at byte 0 bit 6 (0x40), byte 1 bit 5 (0x20), byte 2
 * bit 6 (0x40), byte 3 bit 0 (0x01); per-flag blocks are emitted in ascending [NpcUpdateMaskKey.order]
 * via [NpcUpdateMaskEncoder] (registered in `Rev948ServerCodecsUpdateMasks.kt`).
 *
 * Encoder-registration invariant: only mask bits with a registered encoder are flagged in the bitset
 * (flagging one without an encoder would desync the cipher counter). Unregistered keys are dropped
 * with a WARN-once log.
 */
object NpcExtInfoEncoder {

    /** Reduced log-spam: report each missing NPC mask-encoder key only once per JVM lifetime. */
    private val warnedMissingNpcEncoders: MutableSet<NpcUpdateMaskKey> =
        Collections.newSetFromMap(ConcurrentHashMap())

    /**
     * Encode the Phase 3 ext-info byte block for one NPC. The 2-byte BE length header is added by the
     * codec (NOT here — we emit the raw block bytes).
     *
     * Relocated unchanged from the old monolithic builder's ext-info block encoder — byte output is identical.
     */
    fun encodeExtendedInfoBlock(pending: PendingUpdates): ByteArray {
        val extOut = BufferWriter(256)

        val entries = pending.npcMaskEntries().filter { (key, _) ->
            val has = NpcUpdateMaskEncoder.hasEncoder(key)
            if (!has && warnedMissingNpcEncoders.add(key)) {
                System.err.println(
                    "[NpcExtInfoEncoder] no encoder registered for NpcUpdateMaskKey.$key — " +
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

        for (byte in UpdateMaskHeader.npc(flagBitset, ActiveMaskKeys.npcExpansionBits)) {
            extOut.writeByte(byte.toInt() and 0xFF)
        }

        for ((key, mask) in entries.sortedBy { it.first.order }) {
            NpcUpdateMaskEncoder.encode(extOut, key, mask)
        }

        return extOut.toArray()
    }

    /**
     * True if the NPC has any pending mask with a registered encoder. Drives the `hasExtInfo` bit in
     * both the Phase 1 update path and the Phase 2 add-record.
     *
     * Relocated unchanged from the old monolithic builder's ext-info gate — behavior is identical.
     */
    fun hasFlaggableNpcExtendedInfo(pending: PendingUpdates): Boolean {
        return pending.npcMaskEntries().any { NpcUpdateMaskEncoder.hasEncoder(it.first) }
    }
}
