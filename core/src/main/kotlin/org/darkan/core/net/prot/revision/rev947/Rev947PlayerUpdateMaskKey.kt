package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.update.PlayerUpdateMaskKey

/**
 * Per `docs/net/serverprot/player-info-947-3.md` §4C, the PLAYER_INFO extended-info flag
 * bitset is a 1..4 byte LE value with expansion bits at LE positions 0, 14, 18. The 23
 * data flag bits are listed below; each entry records the LE bit position (`flag = 1 shl
 * bit`) and the position in the client's fixed processing order. Encoders MUST emit
 * blocks in `order` ascending — the client dispatcher checks masks in that exact order.
 *
 * `order` numbering matches the row index in the A4 doc's "Block table" (1..23).
 *
 * Previously this lived as a global enum under `core/.../prot/update/`; it has been moved
 * here to make room for the 948 sibling whose bit positions and dispatch order are different.
 * All consumer code now talks to the [PlayerUpdateMaskKey] interface, so the registered
 * encoders / pending-updates maps work uniformly across revisions.
 */
enum class Rev947PlayerUpdateMaskKey(
    override val bit: Int,
    override val order: Int,
) : PlayerUpdateMaskKey {
    UNK_BIT17(17, 1),
    NAME_STRING(12, 2),
    UNK_BIT9(9, 3),
    FORCED_MOVEMENT(4, 4),
    TRANSIENT_BOOL_19(19, 5),
    APPEARANCE(2, 6),
    OVERHEAD_TEXT(6, 7),
    UNK_BIT10(10, 8),
    OVERHEAD_CHAT(21, 9),
    POSITION_COLOR(22, 10),
    UNK_BIT13(13, 11),
    SPOT_ANIM_REMOVAL(26, 12),
    SPOT_ANIMS(24, 13),
    FACE_ENTITY(5, 14),
    FACE_DIRECTION(1, 15),
    UNK_BIT25(25, 16),
    OVERHEAD_ICON_BLOCK(23, 17),
    OVERHEAD_OPACITY(8, 18),
    CHAT_TEXT_PRIVATE(16, 19),
    SET_DISPLAY_COLOR_BIT7(7, 20),
    UNK_BIT20(20, 21),
    HITMARKS(3, 22),
    CHAT_TEXT(15, 23);

    companion object {
        /** Enumeration of every flag bit defined here, useful for testing / iteration. */
        val byOrder: List<Rev947PlayerUpdateMaskKey> = entries.sortedBy { it.order }

        /** LE bit positions of the expansion bits in the 947-3 player-info bitset header. */
        val EXPANSION_BITS = intArrayOf(0, 14, 18)
    }
}
