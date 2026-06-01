package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.update.NpcUpdateMaskEncoder
import org.darkan.core.net.prot.update.PlayerUpdateMaskEncoder
import org.darkan.core.net.prot.update.UpdateMask

internal fun registerRev947ServerCodecsUpdateMasks() {
    registerPlayerMaskEncoders()
    registerNpcMaskEncoders()
}

// ---------------------------------------------------------------------------
// PLAYER_INFO ext-info encoders (A4 §4C)
// ---------------------------------------------------------------------------

private fun registerPlayerMaskEncoders() {
    // APPEARANCE (bit 2, order 6) — A4 §4C row 6.
    // Wire: p1 length; if non-zero then `length` raw bytes (queued for deferred appearance decode).
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.APPEARANCE) { mask ->
        val payload = (mask as UpdateMask.Appearance).data
        writeByte(payload.size and 0xFF)
        if (payload.isNotEmpty()) {
            writeBytes(payload)
        }
    }

    // NAME_STRING (bit 12, order 2) — A4 §4C row 2.
    // Wire: p1 length + `length` raw CP1252 bytes (via FUN_00297630).
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.NAME_STRING) { mask ->
        val name = (mask as UpdateMask.NameString).name
        val bytes = name.toByteArray(Charsets.ISO_8859_1)
        writeByte(bytes.size and 0xFF)
        if (bytes.isNotEmpty()) {
            writeBytes(bytes)
        }
    }

    // FACE_DIRECTION (bit 1, order 15) — A4 §4C row 15.
    // Wire: p2 angle; p4 zero; p1 zero.
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.FACE_DIRECTION) { mask ->
        val angle = (mask as UpdateMask.FaceDirection).angle
        writeShort(angle)
        writeInt(0)
        writeByte(0)
    }

    // FACE_ENTITY (bit 5, order 14) — A4 §4C row 14.
    // Wire: p3 packed (low 16 bits = entityIndex, high byte = type code).
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.FACE_ENTITY) { mask ->
        val m = mask as UpdateMask.FaceEntity
        val packed = ((m.type and 0xFF) shl 16) or (m.targetIndex and 0xFFFF)
        writeMedium(packed)
    }

    // OVERHEAD_OPACITY (bit 8, order 18) — A4 §4C row 18.
    // Wire: single p1 stored at PlayerEntity+0x1074.
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.OVERHEAD_OPACITY) { mask ->
        writeByte((mask as UpdateMask.OverheadOpacity).opacity and 0xFF)
    }

    // CHAT_TEXT (bit 15, order 23) — A4 §4C row 23.
    // Wire: CP1252 null-terminated string.
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.CHAT_TEXT) { mask ->
        val message = (mask as UpdateMask.ChatText).message
        writeBytes(message.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
    }

    // CHAT_TEXT_PRIVATE (bit 16, order 19) — A4 §4C row 19.
    // Wire: CP1252 null-terminated string + p1 flags.
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.CHAT_TEXT_PRIVATE) { mask ->
        val m = mask as UpdateMask.ChatTextPrivate
        writeBytes(m.message.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
        writeByte(m.flags and 0xFF)
    }

    // OVERHEAD_ICON_BLOCK (bit 23, order 17) — A4 §4C row 17.
    // Wire: 3 × p1 (R,G,B 7-bit packed); p1 alpha; 2 × p2 (startCycle, endCycle).
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.OVERHEAD_ICON_BLOCK) { mask ->
        val m = mask as UpdateMask.PositionColor
        writeByte(m.r and 0x7F)
        writeByte(m.g and 0x7F)
        writeByte(m.b and 0x7F)
        writeByte(m.brightness and 0xFF)
        writeShort(m.startCycle and 0xFFFF)
        writeShort(m.endCycle and 0xFFFF)
    }

    // FORCED_MOVEMENT (bit 4, order 4) — A4 §4C row 4.
    // Wire: 6 × p1 + 3 × p2 → SetForcedMovement.
    PlayerUpdateMaskEncoder.register(Rev947PlayerUpdateMaskKey.FORCED_MOVEMENT) { mask ->
        val m = mask as UpdateMask.ForcedMovement
        writeByte(m.srcDx)
        writeByte(m.srcDz)
        writeByte(m.dstDx)
        writeByte(m.dstDz)
        writeByte(m.delta3)
        writeByte(m.delta4)
        writeShort(m.startTime and 0xFFFF)
        writeShort(m.endTime and 0xFFFF)
        writeShort(m.animationId and 0xFFFF)
    }

    // HITMARKS (bit 3, order 22) — A4 §4C row 22.
    // Wire: gSmart1or2 hit-type list with 0x7FFF/0x7FFE special variants — see A4 §"Detail: HITMARKS".
    // TODO: full layout requires pSmart variant + headbar sub-records; wire after smart-write helper extended.

    // OVERHEAD_TEXT (bit 6, order 7) — A4 §4C row 7.
    // Wire: 4 × gSmart2or4s (anchors/timers) + p1 flags.
    // TODO: needs pBigSmart variant per A4 doc; wire after smart-write helper extended.

    // Remaining provisional UNK_BIT* / partially-modelled blocks (UNK_BIT9/10/13/17/20/25,
    // OVERHEAD_CHAT, POSITION_COLOR, SPOT_ANIMS, SPOT_ANIM_REMOVAL, SET_DISPLAY_COLOR_BIT7,
    // TRANSIENT_BOOL_19) are TODO — block layouts not yet finalised per A4. Wire as needed.
}

// ---------------------------------------------------------------------------
// NPC_INFO ext-info encoders (A5 §"Flag block table")
// ---------------------------------------------------------------------------

private fun registerNpcMaskEncoders() {
    // CHAT_OVERHEAD (bit 0, order 20) — A5 row 20.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.CHAT_OVERHEAD) { mask ->
        val message = (mask as UpdateMask.ChatText).message
        writeBytes(message.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
    }

    // NAME_OVERRIDE (bit 18, order 21) — A5 row 21.
    // Literal "null" restores the type-def default.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.NAME_OVERRIDE) { mask ->
        val name = (mask as UpdateMask.NameOverride).name
        writeBytes(name.toByteArray(Charsets.ISO_8859_1))
        writeByte(0)
    }

    // MODEL_OVERRIDE_ID (bit 15, order 17) — A5 row 17.
    // 0xFFFF clears the override.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.MODEL_OVERRIDE_ID) { mask ->
        val id = when (mask) {
            is UpdateMask.ModelOverride -> mask.modelId
            is UpdateMask.ModelOverrideId -> mask.id
            else -> error("Unexpected mask payload for MODEL_OVERRIDE_ID: ${mask::class.simpleName}")
        }
        writeShort(id and 0xFFFF)
    }

    // COMBAT_LEVEL_HEADBAR_ID (bit 23, order 27) — A5 row 27.
    // 0xFFFF restores default from NPC type def.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.COMBAT_LEVEL_HEADBAR_ID) { mask ->
        writeShort((mask as UpdateMask.CombatLevelHeadbarId).id and 0xFFFF)
    }

    // VISIBILITY_FLAG (bit 27, order 23) — A5 row 23.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.VISIBILITY_FLAG) { mask ->
        writeByte(if ((mask as UpdateMask.VisibilityFlag).visible) 1 else 0)
    }

    // BOOLEAN_FLAG (bit 31, order 5) — A5 row 5.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.BOOLEAN_FLAG) { mask ->
        writeByte(if ((mask as UpdateMask.BooleanFlag).value) 1 else 0)
    }

    // ANIMATION (bit 5, order 18) — A5 row 18.
    // Wire: gSmart2or4s anim id. TODO: needs pBigSmart variant; defer.

    // FACE_ENTITY (bit 4, order 19) — A5 row 19.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.FACE_ENTITY) { mask ->
        val m = mask as UpdateMask.FaceEntity
        val packed = ((m.type and 0xFF) shl 16) or (m.targetIndex and 0xFFFF)
        writeMedium(packed)
    }

    // HITMARKS_AND_HEADBARS (bit 1, order 11) — A5 row 11. Identical to PLAYER_INFO HITMARKS.
    // TODO: same smart-int gap; defer.

    // OVERHEAD_ICON (bit 12, order 6) — A5 row 6.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.OVERHEAD_ICON) { mask ->
        val m = mask as UpdateMask.PositionColor
        writeByte(m.r and 0x7F)
        writeByte(m.g and 0x7F)
        writeByte(m.b and 0x7F)
        writeByte(m.brightness and 0xFF)
        writeByte(0)
        writeByte(0)
        writeShort(m.startCycle and 0xFFFF)
        writeShort((m.startCycle + m.endCycle) / 2 and 0xFFFF)
        writeShort(m.endCycle and 0xFFFF)
    }

    // CLIENT_SCRIPT_OVERRIDE (bit 16, order 15) — A5 row 15.
    // Heavy 0xE0-byte struct construction; encoded as opaque payload until structured.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.CLIENT_SCRIPT_OVERRIDE) { mask ->
        val payload = (mask as UpdateMask.ClientScriptOverride).payload
        writeBytes(payload)
    }

    // ANIMATION_LIST (bit 11, order 4) — A5 row 4. Opaque payload until structured.
    NpcUpdateMaskEncoder.register(Rev947NpcUpdateMaskKey.ANIMATION_LIST) { mask ->
        val payload = (mask as UpdateMask.AnimationList).payload
        writeBytes(payload)
    }

    // Provisional UNK_BITxx blocks (UNK_BIT8/9/14/26/29/30, TRANSIENT_BOOL_E80, EXACT_MOVE,
    // EXACT_MOVE_DESTINATION, SPOT_ANIM_CLEAR_BY_SLOT, REMOVE_FROM_LIST_BY_ID, SPOT_ANIM_LIST_*,
    // SPOTANIM_TRANSFORM_LIST, COMBAT_LEVEL_OVERRIDE_RGB, FORCED_MOVEMENT) are TODO — block
    // names provisional per A5; layouts incompletely modelled.
}
