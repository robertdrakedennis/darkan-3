package org.darkan.world.net

import org.darkan.world.entity.Npc
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Single responsibility: encode the **per-NPC position bits** for NPC_INFO (op12) — the Phase 1
 * update-movement bits and the Phase 2 add-record (relative coords + type + facing). It owns nothing
 * about phases, cohorts, the sentinel, or ext-info; the orchestrator ([NpcInfoEncoder]) drives those
 * and delegates the bytes here, with ext-info gating delegated to [NpcExtInfoEncoder].
 *
 * Wire references (relocated verbatim with their code — do not delete):
 *  * Phase 1 update — `docs/net/serverprot/npc-info-947-3.md` §"Phase 1".
 *  * Phase 2 add — §"Phase 2 — Add new NPCs".
 *
 * **Current behavior (preserved EXACTLY):** Phase 1 emits `movementType=0` for every updated NPC
 * (no walk). No real NPC motion is produced.
 *
 * Phase 1.2b: real NPC walk/run plugs in HERE. The §"Phase 1" `case 0` movement form is the only one
 * implemented; the dir-based walk encoding (movementType 1/2 with delay / no-step bits) and the
 * "keep-in-list vs remove" semantics are still unverified against the decompilation — when NPC walk
 * logic lands, this [encodePhase1Update] branch is where the dir-based encoding is added and the
 * keep/remove question is resolved. (Replaces the prior in-code uncertainty paragraph; no byte change.)
 */
object NpcMovementEncoder {

    /** Phase 1 no-movement form: every updated NPC writes `movementType=0` (no walk) today. */
    private const val MOVEMENT_TYPE_NONE = 0

    /**
     * Phase 1 movement bits for an NPC that `hasUpdate==1`. Today this is the `case 0`
     * (movementType=0) form only — see the class Phase-1.2b note for the keep/remove caveat.
     * The orchestrator has already written the 1-bit `hasUpdate=1`; this writes the 2-bit
     * movementType. The `hasExtInfo` flagging is the orchestrator's concern (the §"Phase 1 case 0"
     * ext-info side-effect is undecoded; the orchestrator appends to the flagged list if ext-info
     * exists, accepting a harmless 1-tick ext-info on a possibly-removed NPC).
     *
     * Relocated unchanged from the `NpcInfoBuilder.build` Phase-1 body — byte output is identical.
     */
    fun encodePhase1Update(out: BufferWriter) {
        out.writeBits(2, MOVEMENT_TYPE_NONE)
    }

    /**
     * Phase 2 add-record position + identity bits for a newly-visible NPC, per §"Phase 2":
     * ```
     * gBit(16)              serverIndex
     * gBit(coordBitWidth)   deltaX        (signed, clamped to ±2^(w-1))
     * gBit(2)               level
     * gBit(coordBitWidth)   deltaY
     * gBit(16)              typeId
     * gBit(1)               unknown        (currently undecoded)
     * gBit(3)               facing
     * gBit(1)               hasExtInfo
     * ```
     * [serverIndex] is the validated 1..0xFFFE index (the orchestrator pre-validates, since 0 and the
     * 0xFFFF sentinel are reserved). [hasExtInfo] is computed by the orchestrator via
     * [NpcExtInfoEncoder] and passed in so the flagged-list bookkeeping stays in one place.
     *
     * Relocated unchanged from the `NpcInfoBuilder.build` Phase-2 body — byte output is identical.
     */
    fun encodePhase2Add(
        out: BufferWriter,
        npc: Npc,
        serverIndex: Int,
        viewerTileX: Int,
        viewerTileY: Int,
        hasExtInfo: Boolean,
    ) {
        out.writeBits(16, serverIndex)
        val half = 1 shl (npc.coordBitWidthZone - 1)
        val mask = (1 shl npc.coordBitWidthZone) - 1
        val deltaX = (npc.tile.x - viewerTileX).coerceIn(-half, half - 1)
        val deltaY = (npc.tile.y - viewerTileY).coerceIn(-half, half - 1)
        out.writeBits(npc.coordBitWidthZone, deltaX and mask)
        out.writeBits(2, npc.tile.level and 0x3)
        out.writeBits(npc.coordBitWidthZone, deltaY and mask)
        out.writeBits(16, npc.typeId and 0xFFFF)
        out.writeBits(1, 0)                       // unknown bit (§"Phase 2" — currently undecoded)
        out.writeBits(3, npc.direction and 0x7)
        out.writeBits(1, if (hasExtInfo) 1 else 0)
    }
}
