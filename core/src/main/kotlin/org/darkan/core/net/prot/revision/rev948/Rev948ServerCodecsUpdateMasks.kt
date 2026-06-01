package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.update.NpcUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.UpdateMask

/**
 * Rev948 PLAYER_INFO / NPC_INFO extended-info encoders.
 *
 * **CONFIRMED** against an exhaustive walk of `ProcessExtendedInfo @ 0x0015e110` and
 * `ProcessExtendedInfoNPC @ 0x001621c0` (rs2client.948-2-2). Bit/order/identity per
 * [Rev948PlayerUpdateMaskKey] / [Rev948NpcUpdateMaskKey].
 *
 * **948 ext-info wire format — per-field "scrambled" mode byte (IMPORTANT, NEW vs 947-3):**
 * Every scalar field read by the 948 ext-info handlers goes through a `gScrambled*` reader
 * (`gScrambledByte/Ubyte/Ushort/Uint/Medium`, and `FUN_0047f240` for the mode-select short).
 * Each such reader FIRST consumes a 1-byte *mode selector* from a separate cursor (packet+0x28),
 * then reads the value with one of 4 transforms:
 *   mode 0 = plain / big-endian   (server emits this)
 *   mode 1 = little-endian / reversed
 *   mode 2 = big-endian, low byte +0x80
 *   mode 3 = little-endian, low byte +0x80
 * The server is free to always pick **mode 0** and emit the plain value. Therefore every
 * scrambled scalar below is encoded as `writeByte(0)` (mode) followed by the plain BE value.
 * Raw `gBit`/`gSmart*`/`gArrayBuffer` reads and the BE-short slot reads (FUN_00121880) are NOT
 * mode-prefixed — only the `gScrambled*` family is.
 *
 * Only blocks with DEFINITIVE identity (named fn / exact offset / 0xffff-clear semantics) and a
 * fully-characterised wire payload are registered here. Spot-anim list / transient-triple blocks
 * (the UNK_BIT* entries) are deferred — they are not on the first-tick render path and their exact
 * payloads need a focused re-walk + capture before wiring.
 */
internal fun registerRev948ServerCodecsUpdateMasks() {
    registerPlayerMaskEncoders()
    registerNpcMaskEncoders()
}

/** Mode-0 (plain BE) scrambled-scalar helpers — emit the mode selector then the value. */
private fun world.gregs.voidps.buffer.write.BufferWriter.sByte(value: Int) {
    writeByte(0); writeByte(value)
}

private fun world.gregs.voidps.buffer.write.BufferWriter.sShort(value: Int) {
    writeByte(0); writeShort(value)
}

private fun world.gregs.voidps.buffer.write.BufferWriter.sMedium(value: Int) {
    writeByte(0); writeMedium(value)
}

// ---------------------------------------------------------------------------
// PLAYER_INFO ext-info encoders (bit/order per Rev948PlayerUpdateMaskKey)
// ---------------------------------------------------------------------------

private fun registerPlayerMaskEncoders() {
    // APPEARANCE (bit 3, order 4). Wire: scrambled length byte + length raw bytes (mode-buffer; mode 0 = plain copy).
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.APPEARANCE) { mask ->
        val payload = (mask as UpdateMask.Appearance).data
        sByte(payload.size and 0xFF)
        if (payload.isNotEmpty()) {
            // mode-buffer reader (FUN_0047e7f0): mode 0 = plain copy. The length-byte's mode byte
            // above also governs the buffer copy (single mode byte precedes the whole block).
            writeBytes(payload)
        }
    }

    // FORCED_MOVEMENT (bit 7, order 9). Wire: 6x scrambled byte + 3x scrambled short -> SetForcedMovement.
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.FORCED_MOVEMENT) { mask ->
        val m = mask as UpdateMask.ForcedMovement
        sByte(m.srcDx)
        sByte(m.srcDz)
        sByte(m.dstDx)
        sByte(m.dstDz)
        sByte(m.delta3)
        sByte(m.delta4)
        sShort(m.startTime and 0xFFFF)
        sShort(m.endTime and 0xFFFF)
        sShort(m.animationId and 0xFFFF)
    }

    // OVERHEAD_OPACITY (bit 12, order 18). Wire: 1 scrambled byte -> PlayerEntity+0x1074.
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.OVERHEAD_OPACITY) { mask ->
        sByte((mask as UpdateMask.OverheadOpacity).opacity and 0xFF)
    }

    // FACE_DIRECTION (bit 1, order 14). Wire: 1 scrambled short angle -> JagexAngleToRadians -> player+0x23c.
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.FACE_DIRECTION) { mask ->
        sShort((mask as UpdateMask.FaceDirection).angle and 0xFFFF)
    }

    // CHAT_TEXT (bit 10, order 20). Wire: CP1252 string read via FUN_00ad89a0 (jag string), no mode byte.
    // FUN_00ad89a0 is the plain jag-string reader (length-prefixed), NOT a gScrambled reader.
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.CHAT_TEXT) { mask ->
        val message = (mask as UpdateMask.ChatText).message
        // jag string: byte 0 version marker + CP1252 bytes + null terminator (matches FUN_00ad89a0 read shape).
        writeByte(0)
        writeBytes(message.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
    }

    // DEFERRED (not on first-tick render path; need focused re-walk + capture before wiring):
    //  - OVERHEAD_CHAT (bit 20), OVERHEAD_TEXT (bit 6), CHAT_TEXT_PRIVATE (bit 14),
    //    POSITION_COLOR (bit 21), HITMARKS (bit 4), EXACT_MOVE (bit 5), HEAD_ICON (bit 17),
    //    SPOT_ANIM_REMOVAL (bit 26), and all spot-anim list/slot UNK_BIT* blocks.
}

// ---------------------------------------------------------------------------
// NPC_INFO ext-info encoders (bit/order per Rev948NpcUpdateMaskKey)
// ---------------------------------------------------------------------------

private fun registerNpcMaskEncoders() {
    // VISIBILITY_FLAG (bit 27, order 10). Wire: 1 scrambled byte -> npc render-flag.
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.VISIBILITY_FLAG) { mask ->
        sByte(if ((mask as UpdateMask.VisibilityFlag).visible) 1 else 0)
    }

    // MODEL_OVERRIDE_ID (bit 20, order 7). Wire: 1 scrambled short (0xffff restores type-def default).
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.MODEL_OVERRIDE_ID) { mask ->
        val id = when (mask) {
            is UpdateMask.ModelOverride -> mask.modelId
            is UpdateMask.ModelOverrideId -> mask.id
            else -> error("Unexpected mask payload for MODEL_OVERRIDE_ID: ${mask::class.simpleName}")
        }
        sShort(id and 0xFFFF)
    }

    // COMBAT_LEVEL_HEADBAR_ID (bit 13, order 13). Wire: 1 scrambled short (0xffff clears npc+0xf40).
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.COMBAT_LEVEL_HEADBAR_ID) { mask ->
        sShort((mask as UpdateMask.CombatLevelHeadbarId).id and 0xFFFF)
    }

    // NAME_OVERRIDE (bit 23, order 22). Wire: jag string via FUN_00ad89a0; "null" restores default.
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.NAME_OVERRIDE) { mask ->
        val name = (mask as UpdateMask.NameOverride).name
        writeByte(0)
        writeBytes(name.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
    }

    // FORCED_MOVEMENT (bit 15, order 24). Wire: 6x scrambled byte + 3x scrambled short -> SetForcedMovement.
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.FORCED_MOVEMENT) { mask ->
        val m = mask as UpdateMask.ForcedMovement
        sByte(m.srcDx)
        sByte(m.srcDz)
        sByte(m.dstDx)
        sByte(m.dstDz)
        sByte(m.delta3)
        sByte(m.delta4)
        sShort(m.startTime and 0xFFFF)
        sShort(m.endTime and 0xFFFF)
        sShort(m.animationId and 0xFFFF)
    }

    // CLIENT_SCRIPT_OVERRIDE (bit 16, order 25). Opaque payload (heavy struct; server builds raw bytes).
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.CLIENT_SCRIPT_OVERRIDE) { mask ->
        writeBytes((mask as UpdateMask.ClientScriptOverride).payload)
    }

    // DEFERRED: HITMARKS_AND_HEADBARS (bit 4), ANIMATION (bit 5, gSmart2or4s — no mode byte),
    //  COMBAT_LEVEL_OVERRIDE_RGB (bit 28), CHAT_OVERHEAD (bit 0), OVERHEAD_TEXT (bit 3),
    //  FACE_TILE (bit 1), STRING_OVERRIDE (bit 2), TRANSIENT_BOOL_BIT24 (bit 24),
    //  and all spot-anim list/transient-triple UNK_BIT* blocks.
}
