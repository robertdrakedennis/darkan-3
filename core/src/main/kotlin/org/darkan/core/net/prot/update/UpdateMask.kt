package org.darkan.core.net.prot.update

/**
 * Sealed hierarchy of per-entity update masks for PLAYER_INFO (op 27) and NPC_INFO (op 12).
 *
 * Each subtype corresponds to one extended-info block from
 * `docs/net/serverprot/player-info-947-3.md` §4C (PLAYER_INFO) or
 * `docs/net/serverprot/npc-info-947-3.md` Phase 3 (NPC_INFO). The two protocols share several
 * block shapes (same byte layout on both packets) but assign different bit positions and
 * include some protocol-only blocks. Encoder selection happens via [PlayerUpdateMaskKey] /
 * [NpcUpdateMaskKey] lookups in B4.
 *
 * Some provisional UNK_BIT* blocks listed in the docs are NOT modelled here — they are
 * skipped until a downstream encoder requires them, per the "provisional names" caveat in
 * A4/A5.
 */
sealed class UpdateMask {

    // === Shared shapes (same wire layout on both PLAYER_INFO and NPC_INFO) ===

    /**
     * APPEARANCE — opaque pre-built appearance blob. PLAYER_INFO bit 2 (mask 0x4), NPC_INFO
     * has no direct "appearance" block (NPC type id encoded in Phase 2 instead). Server
     * pre-builds the block bytes; encoder writes length-prefixed raw bytes.
     */
    data class Appearance(val data: ByteArray) : UpdateMask() {
        override fun equals(other: Any?): Boolean = this === other ||
            (other is Appearance && data.contentEquals(other.data))
        override fun hashCode(): Int = data.contentHashCode()
    }

    /**
     * PLAYER_INFO CHAT_TEXT (bit 15, mask 0x8000): public chat from a visible player.
     * NPC_INFO CHAT_OVERHEAD (bit 0, mask 0x1): a string passed straight to the NPC's say
     * overlay. The two share the same on-wire shape (a CP1252 string + flags byte).
     */
    data class ChatText(val message: String, val effects: Int = 0, val color: Int = 0) : UpdateMask()

    /**
     * PLAYER_INFO OVERHEAD_TEXT / "say" block (bit 6, mask 0x40): 4 gSmart2or4s anchor/timer
     * fields + 1 p1 flags. The string contents are supplied separately via the
     * client's chat UI; this block configures the floating-text positioning.
     */
    data class OverheadText(val message: String) : UpdateMask()

    /**
     * PLAYER_INFO HITMARKS (bit 3, mask 0x8) AND NPC_INFO HITMARKS_AND_HEADBARS (bit 1,
     * mask 0x2): identical encoding per A4 §HITMARKS / A5 §HITMARKS_AND_HEADBARS.
     */
    data class HitMarksAndHeadbars(val hits: List<Hit>, val headbars: List<Headbar>) : UpdateMask()

    /** PLAYER_INFO FORCED_MOVEMENT (bit 4, mask 0x10). 6 p1 + 3 p2. */
    data class ForcedMovement(
        val srcDx: Int,
        val srcDz: Int,
        val dstDx: Int,
        val dstDz: Int,
        val delta3: Int,
        val delta4: Int,
        val startTime: Int,
        val endTime: Int,
        val animationId: Int,
    ) : UpdateMask()

    /**
     * PLAYER_INFO FACE_ENTITY (bit 5, mask 0x20), NPC_INFO FACE_ENTITY (bit 4, mask 0x10).
     * Single p3 = low 16 bits = entity index, high byte = type code.
     * Type codes: 0x01 = player, 0x02 = npc, 0xFF/0x7F = clear.
     */
    data class FaceEntity(val targetIndex: Int, val type: Int) : UpdateMask()

    /**
     * PLAYER_INFO FACE_DIRECTION (bit 1, mask 0x2). p2 + p4 +
     * p1; [angle] field stored as ushort.
     */
    data class FaceDirection(val angle: Int) : UpdateMask()

    /** NPC_INFO ANIMATION (bit 5, mask 0x20). gSmart2or4s anim id; speed implicit / 0. */
    data class Animation(val animId: Int, val speed: Int = 0) : UpdateMask()

    /**
     * PLAYER_INFO SPOT_ANIMS (bit 24, mask 0x1000000) and NPC_INFO SPOTANIM_TRANSFORM_LIST
     * (bit 33, mask 0x200000000). List of per-slot transforms; see [SpotAnimTransform].
     */
    data class SpotAnims(val transforms: List<SpotAnimTransform>) : UpdateMask()

    /**
     * PLAYER_INFO OVERHEAD_ICON_BLOCK (bit 23, mask 0x800000) and NPC_INFO OVERHEAD_ICON
     * (bit 12, mask 0x1000): RGB triple + alpha/extra byte + start/mid/end cycle ushorts.
     */
    data class PositionColor(
        val r: Int,
        val g: Int,
        val b: Int,
        val brightness: Int,
        val startCycle: Int,
        val endCycle: Int,
    ) : UpdateMask()

    /** NPC_INFO VISIBILITY_FLAG (bit 27, mask 0x8000000) — p1 bool stored at NPC+0xd95. */
    data class VisibilityFlag(val visible: Boolean) : UpdateMask()

    /**
     * NPC_INFO MODEL_OVERRIDE_ID (bit 15, mask 0x8000): single p2. 0xFFFF
     * clears the override; else sets the model override slot.
     */
    data class ModelOverride(val modelId: Int) : UpdateMask()

    // === Player-only ===

    /** PLAYER_INFO OVERHEAD_OPACITY (bit 8, mask 0x100): single p1 at +0x1074. */
    data class OverheadOpacity(val opacity: Int) : UpdateMask()

    /** PLAYER_INFO NAME_STRING (bit 12, mask 0x1000): p1 length + raw CP1252 bytes. */
    data class NameString(val name: String) : UpdateMask()

    /**
     * PLAYER_INFO CHAT_TEXT_PRIVATE (bit 16, mask 0x10000): CP1252 string + flags byte.
     * If `flags & 1` the client routes to ChatHistory::AddChat with player name/clan, otherwise
     * the message is set on the entity's chat overlay directly.
     */
    data class ChatTextPrivate(val message: String, val flags: Int) : UpdateMask()

    /**
     * PLAYER_INFO bit 17 (UNK_BIT17): ushort + uint + byte. Provisionally HEAD_ICONS in
     * 946-era docs but the byte layout doesn't match an icon list; kept as a 3-tuple until
     * a capture-driven RE pass disambiguates.
     */
    data class HeadIcons(val short: Int, val int: Long, val byte: Int) : UpdateMask()

    /**
     * PLAYER_INFO SPOT_ANIM_REMOVAL (bit 26, mask 0x4000000): byte count + gSmart1or2 ids;
     * id == -1 triggers a "clear all" inside the same loop and breaks.
     */
    data class SpotAnimRemoval(val slot: Int) : UpdateMask()

    // === NPC-only ===

    /**
     * Sentinel for NPCs whose appearance is driven by an NPC type id (set during Phase 2 of
     * NPC_INFO). Distinct from [Appearance] because no extended-info block carries it on the
     * wire — included here to let world-side code register a "needs respawn" signal.
     */
    data class NpcAppearance(val typeId: Int) : UpdateMask()

    /**
     * NPC_INFO BOOLEAN_FLAG (bit 31, mask 0x80000000): p1 stored at NPC+0x1160
     * with init marker at +0x1161.
     */
    data class BooleanFlag(val value: Boolean) : UpdateMask()

    /**
     * NPC_INFO COMBAT_LEVEL_HEADBAR_ID (bit 23, mask 0x800000): single p2.
     * 0xFFFF restores the default from the NPC type def.
     */
    data class CombatLevelHeadbarId(val id: Int) : UpdateMask()

    /**
     * NPC_INFO CLIENT_SCRIPT_OVERRIDE (bit 16, mask 0x10000): flags byte + optional
     * gSmart2or4s array / p2 array / 0xE0-byte struct. Modelled as opaque
     * payload bytes until a downstream consumer needs structured access.
     */
    data class ClientScriptOverride(val payload: ByteArray) : UpdateMask() {
        override fun equals(other: Any?): Boolean = this === other ||
            (other is ClientScriptOverride && payload.contentEquals(other.payload))
        override fun hashCode(): Int = payload.contentHashCode()
    }

    /**
     * Alias-style entry for NPC_INFO MODEL_OVERRIDE_ID (bit 15) when the encoder wants a
     * dedicated NPC-side type for clarity. Wire layout identical to [ModelOverride].
     */
    data class ModelOverrideId(val id: Int) : UpdateMask()

    /**
     * NPC_INFO ANIMATION_LIST (bit 11, mask 0x800): heavy variable-length block — full
     * byte layout deferred per A5 §"Detail: ANIMATION_LIST" pending a second decomp pass.
     */
    data class AnimationList(val payload: ByteArray) : UpdateMask() {
        override fun equals(other: Any?): Boolean = this === other ||
            (other is AnimationList && payload.contentEquals(other.payload))
        override fun hashCode(): Int = payload.contentHashCode()
    }

    /** NPC_INFO NAME_OVERRIDE (bit 18, mask 0x40000): CP1252 string; "null" restores default. */
    data class NameOverride(val name: String) : UpdateMask()

    /**
     * NPC_INFO SPOT_ANIM_LIST_PRIMARY (bit 17, mask 0x20000) and SPOT_ANIM_LIST_HEAD
     * (bit 20, mask 0x100000): byte count + per-entry (slot, id) triplet. Slot 0 conventionally
     * marks the primary list, non-zero the head list — selection happens server-side at
     * encode time.
     */
    data class OverheadIcon(val slotsAndIds: List<Pair<Int, Int>>) : UpdateMask()
}
