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
 * **948 ext-info wire format — the "scrambled" mode is a FIXED client-side `.rodata` table,
 * NOT a wire-transmitted mode byte (CORRECTED — see `re-resources/docs/net/serverprot/player-appearance-948.md` §1/§6).**
 * The earlier model below ("server emits `writeByte(0)` mode 0 then the plain value") was WRONG.
 * `ProcessExtendedInfo @0x0015e290` (948-5) drives each scrambled read from a per-block offset into
 * the client's read-only `.rodata` table at `@0x00cb6a80` (block `.rodata` r=true **w=false** — it
 * CANNOT be filled from the wire). Emitting a `0x00` mode prefix injects a spurious byte the client
 * consumes as real field data → guaranteed desync. The transform for each scrambled field is fixed
 * by `table[blockBase + fieldIndex]`; the server must apply that transform with NO mode byte.
 *
 * For the **APPEARANCE** block (the only ext-info block on the first-light render path), the base is
 * `0x00cb6ac0`: `table[0]=3` governs the length byte (mode 3) and `table[1]=2` governs the body
 * (mode 2, forward). The four transforms (`gScrambledByte @0x0047f840` / buffer `FUN_0047ec70`):
 *   mode 0 — scalar: `wire = value`            ; buffer: verbatim, forward
 *   mode 1 — scalar: `wire = (value+0x80)&0xFF`; buffer: verbatim, REVERSED
 *   mode 2 — scalar: `wire = (-value)&0xFF`    ; buffer: each `(b+0x80)&0xFF`, forward
 *   mode 3 — scalar: `wire = (-0x80-value)&0xFF`; buffer: each `(b+0x80)&0xFF`, REVERSED
 * So APPEARANCE: length byte = mode 3 = `(-0x80 - L) & 0xFF`; body = mode 2 = every byte `+0x80`.
 *
 * **APPEARANCE (bit 3), FORCED_MOVEMENT (bit 7, the glide block) and MOVEMENT_ANIM (bit 5, the walk/run
 * anim block) are byte-verified and wired** — each writes the exact inverse of the client's per-field
 * jag::Packet transforms (`g1_add/g1_neg/g1_sub`, `g2`, `gSmart2or4s`), with NO mode-prefix byte, per
 * `re-resources/docs/net/serverprot/player-appearance-948.md`.
 *
 * **The remaining scrambled-scalar encoders below still FAIL LOUD** (`sByte`/`sShort`/`sMedium` throw).
 * They are LATENT — none are on the movement render path — and emitting the now-retired `writeByte(0)`
 * mode prefix silently DESYNCS the client, so rather than ship known-wrong bytes the guards throw if any
 * of these masks is ever actually sent under 948. Re-auditing the remaining per-block `.rodata` bases is
 * out of scope here (doc defers it: "trace them when those masks are actually sent"); when
 * FACE_DIRECTION/OVERHEAD_OPACITY/etc. are wired, each must be re-pointed at its own
 * `table[blockBase + fieldIndex]` transform, replacing the guard.
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

/**
 * FAIL-LOUD guards for the not-yet-RE'd 948 scrambled-scalar ext-info fields.
 *
 * The earlier implementation emitted a `writeByte(0)` mode selector + the plain value. That is **WRONG**
 * (`re-resources/docs/net/serverprot/player-appearance-948.md` §6 — the scramble mode is a fixed client
 * `.rodata` table, NOT a wire byte), so it silently DESYNCS the client. The blocks still routed here are
 * not on the movement render path today, so rather than emit known-wrong bytes the helpers THROW: any code
 * path that actually tries to send OVERHEAD_OPACITY / FACE_DIRECTION / VISIBILITY_FLAG / MODEL_OVERRIDE_ID /
 * COMBAT_LEVEL_HEADBAR_ID under 948 fails loudly here instead of corrupting the stream. (APPEARANCE,
 * FORCED_MOVEMENT and MOVEMENT_ANIM are byte-verified and wired with real transforms — not routed here.)
 * To wire one of these, re-point it at its block's `table[blockBase + fieldIndex]` transform first
 * (per doc §6 action item 2), then replace the guard with the real encode. See NETWORKING_AUDIT.md (Phase 0).
 */
private fun scrambled948NotImplemented(kind: String, value: Int): Nothing = error(
    "948 scrambled ext-info $kind transform is not yet reverse-engineered (value=$value). The retired " +
        "writeByte(0) mode prefix DESYNCS the client (player-appearance-948.md §6). Re-point this block at " +
        "its table[blockBase+fieldIndex] transform before emitting it on the wire — see NETWORKING_AUDIT.md.",
)

private fun world.gregs.voidps.buffer.write.BufferWriter.sByte(value: Int) {
    scrambled948NotImplemented("byte", value)
}

private fun world.gregs.voidps.buffer.write.BufferWriter.sShort(value: Int) {
    scrambled948NotImplemented("short", value)
}

private fun world.gregs.voidps.buffer.write.BufferWriter.sMedium(value: Int) {
    scrambled948NotImplemented("medium", value)
}

/**
 * Ext-info APPEARANCE framing transforms (`re-resources/docs/net/serverprot/player-appearance-948.md` §1.2), exposed
 * `internal` so [Rev948ExtInfoTransforms] / unit tests can assert them against the doc table.
 *
 * The client reads the appearance entry as `[length: mode 3][body: mode 2 over the whole run]`,
 * where the modes come from the fixed `.rodata` table (NOT the wire). The server therefore writes:
 *  - length byte: `(-0x80 - L) & 0xFF`  (mode 3 scalar transform; L = true payload byte count)
 *  - body bytes : each plain payload byte `(b + 0x80) & 0xFF`  (mode 2 buffer transform, forward)
 *
 * No `writeByte(0)` mode prefix precedes either (that was the retired §6 model).
 */
internal object Rev948ExtInfoTransforms {
    /** APPEARANCE length-byte transform: mode 3 scalar = `(-0x80 - L) & 0xFF`. */
    fun appearanceLengthByte(payloadLength: Int): Int = (-0x80 - payloadLength) and 0xFF

    /** APPEARANCE body per-byte transform: mode 2 buffer (forward) = `(b + 0x80) & 0xFF`. */
    fun appearanceBodyByte(plainByte: Int): Int = (plainByte + 0x80) and 0xFF

    /** Write the framed APPEARANCE ext-info entry: length(mode 3) + body(mode 2) over [payload]. */
    fun write(out: world.gregs.voidps.buffer.write.BufferWriter, payload: ByteArray) {
        out.writeByte(appearanceLengthByte(payload.size))
        for (b in payload) out.writeByte(appearanceBodyByte(b.toInt() and 0xFF))
    }
}

// ---------------------------------------------------------------------------
// PLAYER_INFO ext-info encoders (bit/order per Rev948PlayerUpdateMaskKey)
// ---------------------------------------------------------------------------

private fun registerPlayerMaskEncoders() {
    // APPEARANCE (bit 3, order 4). Wire (player-appearance-948.md "Ext-info framing"):
    //   [length byte]  mode 3 scalar = (-0x80 - L) & 0xFF   (NOT a writeByte(0)+len mode prefix)
    //   [L body bytes] mode 2 buffer = each payload byte (b + 0x80) & 0xFF, forward
    // The mode selectors are read from the client's fixed `.rodata` table (base 0xcb6ac0:
    // table[0]=3 length, table[1]=2 body), NOT from the wire. [UpdateMask.Appearance.data] is the
    // PLAIN appearance payload (built by PlayerAppearanceEncoder per the doc's field table); the
    // framing transform is applied here once over the whole block.
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.APPEARANCE) { mask ->
        Rev948ExtInfoTransforms.write(this, (mask as UpdateMask.Appearance).data)
    }

    // FORCED/TEMP MOVEMENT — the GLIDE block (bit 7 / 0x80, order 9). 12 bytes ->
    // GraphEntity::SetRenderWaypoint @0x10039e220 (opens the avatar+0xDBC lerp window so the avatar
    // interpolates tile→tile instead of snapping). Wire layout (plain payload; transforms are the
    // exact inverse of the client's per-byte reads, player-appearance-948.md §"Bit-7 (0x80)"):
    //   +0 g1_add  (client wire-128)  -> writeByteAdd       srcDx tile delta (client * 0x200 fine)
    //   +1 g1_neg  (client -wire)     -> writeByteInverse   srcDz tile delta (client * 0x200 fine)
    //   +2 g1                          -> writeByte          dstDx tile delta (client * 0x200 fine)
    //   +3 g1_sub  (client 128-wire)  -> writeByteSubtract  dstDz tile delta (client * 0x200 fine)
    //   +4 g1_add                      -> writeByteAdd       delta3
    //   +5 g1_neg                      -> writeByteInverse   delta4
    //   +6..7 g2 BE                    -> writeShort         startTick
    //   +8..9 g2 BE                    -> writeShort         endTick
    //   +0xa yaw low  g1_add           -> writeByteAdd(yaw & 0xFF)
    //   +0xb yaw high (wire & 0x3f)<<8 -> writeByte((yaw >> 8) & 0x3f)  (14-bit angle, NOT a full short)
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.FORCED_MOVEMENT) { mask ->
        val m = mask as UpdateMask.ForcedMovement
        writeByteAdd(m.srcDx)
        writeByteInverse(m.srcDz)
        writeByte(m.dstDx)
        writeByteSubtract(m.dstDz)
        writeByteAdd(m.delta3)
        writeByteInverse(m.delta4)
        writeShort(m.startTime and 0xFFFF)
        writeShort(m.endTime and 0xFFFF)
        writeByteAdd(m.yaw and 0xFF)
        writeByte((m.yaw shr 8) and 0x3F)
    }

    // MOVEMENT_ANIM — the walk/run leg-animation block (bit 5 / 0x20, order 16). Wire:
    //   4x gSmart2or4s (movement-anim seq ids: walk/run/turn/idle set) -> writeBigSmart
    //   1x g1_sub (priority flag, client 128-wire)                     -> writeByteSubtract
    // -> GraphEntity::SetMovementAnimSet @0x1003a69f0 pushes the seqs into the route-anim queue so the
    // bas walk/run seq plays (player-appearance-948.md §"What ACTUALLY animates a remote-player walk").
    // writeBigSmart already encodes -1 as the 2-byte 0x7FFF sentinel the client reads as -1 (4×-1 ⇒
    // ResetMovementSeqs, stop). All four ids are emitted regardless of value (the client always reads 4).
    PlayerUpdateMaskEncoder.register(Rev948PlayerUpdateMaskKey.MOVEMENT_ANIM) { mask ->
        val m = mask as UpdateMask.MovementAnim
        writeBigSmart(m.seq0)
        writeBigSmart(m.seq1)
        writeBigSmart(m.seq2)
        writeBigSmart(m.seq3)
        writeByteSubtract(m.priority and 0xFF)
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

    // DEFERRED (not on the movement render path; need focused re-walk + capture before wiring):
    //  - OVERHEAD_CHAT (bit 20), OVERHEAD_TEXT (bit 6), CHAT_TEXT_PRIVATE (bit 14),
    //    POSITION_COLOR (bit 21), HITMARKS (bit 4), HEAD_ICON (bit 17),
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

    // FORCED/TEMP MOVEMENT — the GLIDE block (bit 15 / 0x8000, order 24). NPCs use the IDENTICAL
    // temp-movement -> SetRenderWaypoint glide path as players (player-appearance-948.md: SetRenderWaypoint
    // has exactly two callers, NpcInfo::DecodeNpcExtendedInfo and the player bit-0x80 block), so the wire
    // shape matches the player FORCED_MOVEMENT block above (6x transformed byte + 2x g2 + 14-bit yaw).
    NpcUpdateMaskEncoder.register(Rev948NpcUpdateMaskKey.FORCED_MOVEMENT) { mask ->
        val m = mask as UpdateMask.ForcedMovement
        writeByteAdd(m.srcDx)
        writeByteInverse(m.srcDz)
        writeByte(m.dstDx)
        writeByteSubtract(m.dstDz)
        writeByteAdd(m.delta3)
        writeByteInverse(m.delta4)
        writeShort(m.startTime and 0xFFFF)
        writeShort(m.endTime and 0xFFFF)
        writeByteAdd(m.yaw and 0xFF)
        writeByte((m.yaw shr 8) and 0x3F)
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
