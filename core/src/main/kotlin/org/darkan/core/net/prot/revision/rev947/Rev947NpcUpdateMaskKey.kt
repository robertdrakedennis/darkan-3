package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.update.NpcUpdateMaskKey

/**
 * Per `docs/net/serverprot/npc-info-947-3.md` §"Flag block table", the NPC_INFO extended-info
 * flag bitset is a 1..5 byte LE value with expansion bits at LE positions 6, 13, 22, 24.
 * The 27 unique data flag bits are listed below; each entry records the LE bit position
 * (`flag = 1L shl bit`) and the position in the client's fixed processing order.
 *
 * NOTE: bit 14 appears in two dispatch slots (orders 7 and 24) per A5 — the duplicate is
 * marked as TODO until capture validation disambiguates whether it's two distinct blocks
 * or a decompilation artifact. For now we model the first occurrence only and treat the
 * second as a follow-up; the second slot would require a sibling enum entry with the same
 * `bit` and a different `order`, which the encoder lookup cannot disambiguate without
 * additional context.
 *
 * `order` numbering matches the row index in the A5 doc's "Flag block table" (1..29 with
 * bit 14 appearing at order 7 and order 24 — the 24th order is omitted here pending RE
 * follow-up).
 *
 * Previously this lived as a global enum under `core/.../prot/update/`; it has been moved
 * here so the 948 sibling can supply its own (substantially shuffled) layout without
 * polluting the shared interface.
 */
enum class Rev947NpcUpdateMaskKey(
    override val bit: Int,
    override val order: Int,
) : NpcUpdateMaskKey {
    FORCED_MOVEMENT(7, 1),
    TRANSIENT_BOOL_E80(32, 2),
    UNK_BIT30(30, 3),
    ANIMATION_LIST(11, 4),
    BOOLEAN_FLAG(31, 5),
    OVERHEAD_ICON(12, 6),
    UNK_BIT14(14, 7),
    COMBAT_LEVEL_OVERRIDE_RGB(28, 8),
    EXACT_MOVE(21, 9),
    SPOT_ANIM_CLEAR_BY_SLOT(19, 10),
    HITMARKS_AND_HEADBARS(1, 11),
    UNK_BIT29(29, 12),
    REMOVE_FROM_LIST_BY_ID(25, 13),
    EXACT_MOVE_DESTINATION(3, 14),
    CLIENT_SCRIPT_OVERRIDE(16, 15),
    SPOT_ANIM_LIST_PRIMARY(17, 16),
    MODEL_OVERRIDE_ID(15, 17),
    ANIMATION(5, 18),
    FACE_ENTITY(4, 19),
    CHAT_OVERHEAD(0, 20),
    NAME_OVERRIDE(18, 21),
    SPOTANIM_TRANSFORM_LIST(33, 22),
    VISIBILITY_FLAG(27, 23),
    // TODO: bit 14 second-dispatch slot (order 24) per A5 §"Flag block table" — verify with
    // capture whether this is a distinct block or a decompilation if-cascade artifact.
    SPOT_ANIM_LIST_HEAD(20, 25),
    UNK_BIT8(8, 26),
    COMBAT_LEVEL_HEADBAR_ID(23, 27),
    UNK_BIT9(9, 28),
    UNK_BIT26(26, 29);

    companion object {
        /** Enumeration of every flag entry defined here, sorted by client processing order. */
        val byOrder: List<Rev947NpcUpdateMaskKey> = entries.sortedBy { it.order }

        /** LE bit positions of the expansion bits in the 947-3 NPC-info bitset header. */
        val EXPANSION_BITS = intArrayOf(6, 13, 22, 24)
    }
}
