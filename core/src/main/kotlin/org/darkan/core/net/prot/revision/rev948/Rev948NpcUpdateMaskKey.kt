package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.update.NpcUpdateMaskKey

/**
 * Rev948 NPC_INFO extended-info flag bitset.
 *
 * **CONFIRMED by exhaustive walk** of `jag::NPCEntity::ProcessExtendedInfoNPC @ 0x001621c0`
 * (rs2client.948-2-2). 29 top-level `if ((mask & flag) != 0)` tests read in source order
 * (= the client's fixed dispatch order, with the high dword tested via `mask >> 0x20`).
 * This supersedes the earlier provisional table (which had EXPANSION_BITS = {6,13,22,24} copied
 * from 947-3, and several wrong bit/order pairings — e.g. FACE_ENTITY/OVERHEAD_ICON/FORCED_MOVEMENT
 * were all on wrong bits).
 *
 * Header: 1..5 byte LE 64-bit value. **EXPANSION BITS = {6, 8, 19, 25}** (CONFIRMED from the decode
 * prologue: byte0 bit6 -> byte1; byte1 bit0 [= overall 8] -> byte2; byte2 bit3 [= overall 19] ->
 * byte3; byte3 bit1 [= overall 25] -> byte4). **This CHANGED from 947-3's {6,13,22,24}.**
 *
 * **Confirmed named blocks (encoder-ready, evidence = named fn / exact offset / 0xffff-clear semantics):**
 *  * HITMARKS_AND_HEADBARS  : bit 4  order 1   — count + gSmart1or2 list (0x7fff/0x7ffe) -> AddHitmark/AddHeadbar
 *  * ANIMATION              : bit 5  order 2   — 4x gSmart2or4s + byte -> vcall+0x1e0 (RequestAnimation)
 *  * COMBAT_LEVEL_OVERRIDE_RGB: bit 28 order 6 — 3x ubyte HSL + byte + 2x g2 -> HSLToRGBLookup -> npc+0x190
 *  * MODEL_OVERRIDE_ID      : bit 20 order 7   — g2 (0xffff => restore type-def default)
 *  * VISIBILITY_FLAG        : bit 27 order 10  — 1 scrambled byte -> npc render-flag (symtab+0x98)
 *  * STRING_OVERRIDE        : bit 2  order 11  — string (FUN_00ad89a0) -> vcall+0x158
 *  * CHAT_OVERHEAD          : bit 0  order 12  — gSmart2or4s -> say vcall+0x220
 *  * COMBAT_LEVEL_HEADBAR_ID: bit 13 order 13  — g2 (0xffff clears npc+0xf40 headbar ref)
 *  * FACE_TILE              : bit 1  order 15  — 2x g2 -> npc+0x22c/0x234 (face coordinate)
 *  * NAME_OVERRIDE          : bit 23 order 22  — string (FUN_00ad89a0) + "null" compare (DAT_00fba09d) => default
 *  * FORCED_MOVEMENT        : bit 15 order 24  — 6x scrambled ubyte + 3x g2 -> PathingEntity::SetForcedMovement
 *  * CLIENT_SCRIPT_OVERRIDE : bit 16 order 25  — flags byte + heavy struct (FUN_00c41cd0, gSmart2or4s sub-fields)
 *  * OVERHEAD_TEXT          : bit 3  order 28  — gScrambledMedium type-dispatch (FUN_0042e610, npc+0x1a8)
 *  * TRANSIENT_BOOL_BIT24   : bit 24 order 19  — 1 scrambled byte -> npc+0xd95 (==1)
 *
 * **NOT a standalone block in 948 (contrary to 947-3):** there is no separate FACE_ENTITY (packed
 * gMedium) block and no separate simple OVERHEAD_ICON colour block — bit 12 carries a flags-driven
 * spot-anim list, not the 947 colour block. Do NOT register FACE_ENTITY / OVERHEAD_ICON encoders
 * blindly. The remaining UNK_BIT<n> entries are the recurring "g2 + scrambled uint + scrambled byte"
 * spot-anim transient triple or spot-anim lists — named UNK with read shape; wire only after a
 * focused re-walk + capture.
 *
 * 948 ext-info readers use the per-field "scrambled" mode byte (see Rev948PlayerUpdateMaskKey doc
 * and docs/net/serverprot/948-research-C-player-npc-misc.md).
 */
enum class Rev948NpcUpdateMaskKey(
    override val bit: Int,
    override val order: Int,
) : NpcUpdateMaskKey {
    // ord1  bit4  0x10        : HITMARKS_AND_HEADBARS — count + gSmart1or2 list -> AddHitmark/AddHeadbar
    HITMARKS_AND_HEADBARS(4, 1),
    // ord2  bit5  0x20        : ANIMATION — 4x gSmart2or4s + byte -> RequestAnimation (vcall+0x1e0)
    ANIMATION(5, 2),
    // ord3  bit31 0x80000000  : spot-anim transient triple (g2 + scrambled uint + scrambled byte)
    UNK_BIT31(31, 3),
    // ord4  bit17 0x20000     : spot-anim list primary (count + per-anim slot/uint/medium)
    SPOT_ANIM_LIST_PRIMARY(17, 4),
    // ord5  bit14 0x4000      : scrambled byte + 3x g2 block
    UNK_BIT14(14, 5),
    // ord6  bit28 0x10000000  : COMBAT_LEVEL_OVERRIDE_RGB — HSLToRGBLookup -> npc+0x190
    COMBAT_LEVEL_OVERRIDE_RGB(28, 6),
    // ord7  bit20 0x100000    : MODEL_OVERRIDE_ID — g2, 0xffff => restore type-def default
    MODEL_OVERRIDE_ID(20, 7),
    // ord8  bit29 0x20000000  : spot-anim removal list (BE-short ids via FUN_00121880)
    SPOT_ANIM_REMOVAL(29, 8),
    // ord9  bit32 (>>0x20 & 1): model-transform / spot-anim transform list (complex)
    UNK_BIT32(32, 9),
    // ord10 bit27 0x8000000   : VISIBILITY_FLAG — 1 scrambled byte -> npc render-flag (symtab+0x98)
    VISIBILITY_FLAG(27, 10),
    // ord11 bit2  0x4         : STRING_OVERRIDE — string (FUN_00ad89a0) -> vcall+0x158
    STRING_OVERRIDE(2, 11),
    // ord12 bit0  0x1         : CHAT_OVERHEAD — gSmart2or4s -> say (vcall+0x220)
    CHAT_OVERHEAD(0, 12),
    // ord13 bit13 0x2000      : COMBAT_LEVEL_HEADBAR_ID — g2, 0xffff clears npc+0xf40
    COMBAT_LEVEL_HEADBAR_ID(13, 13),
    // ord14 bit30 0x40000000  : spot-anim transient triple (g2 + scrambled uint + scrambled byte)
    UNK_BIT30(30, 14),
    // ord15 bit1  0x2         : FACE_TILE — 2x g2 -> npc+0x22c/0x234 (face coordinate)
    FACE_TILE(1, 15),
    // ord16 bit7  0x80        : spot-anim transient triple (g2 + scrambled uint + scrambled byte)
    UNK_BIT7(7, 16),
    // ord17 bit12 0x1000      : spot-anim list (flags-driven conditional sub-reads) — NOT the 947 colour block
    SPOT_ANIM_LIST_BIT12(12, 17),
    // ord18 bit9  0x200       : spot-anim 2-field block
    UNK_BIT9(9, 18),
    // ord19 bit24 0x1000000   : transient bool -> npc+0xd95 (==1)
    TRANSIENT_BOOL_BIT24(24, 19),
    // ord20 bit11 0x800       : spot-anim transient triple (g2 + scrambled uint + scrambled byte)
    UNK_BIT11(11, 20),
    // ord21 bit21 0x200000    : spot-anim submask block
    UNK_BIT21(21, 21),
    // ord22 bit23 0x800000    : NAME_OVERRIDE — string + "null" compare (DAT_00fba09d) => default
    NAME_OVERRIDE(23, 22),
    // ord23 bit18 0x40000     : spot-anim clear + list
    UNK_BIT18(18, 23),
    // ord24 bit15 0x8000      : FORCED_MOVEMENT — 6x scrambled ubyte + 3x g2 -> SetForcedMovement
    FORCED_MOVEMENT(15, 24),
    // ord25 bit16 0x10000     : CLIENT_SCRIPT_OVERRIDE — flags byte + heavy struct (FUN_00c41cd0)
    CLIENT_SCRIPT_OVERRIDE(16, 25),
    // ord26 bit26 0x4000000   : spot-anim transient triple (g2 + scrambled uint + scrambled byte)
    UNK_BIT26(26, 26),
    // ord27 bit22 0x400000    : spot-anim clear + list
    UNK_BIT22(22, 27),
    // ord28 bit3  0x8         : OVERHEAD_TEXT — gScrambledMedium type-dispatch (FUN_0042e610, npc+0x1a8)
    OVERHEAD_TEXT(3, 28),
    // ord29 bit33 (>>0x21 & 1): byte sentinel (0x80 clears) — tail flag block
    UNK_BIT33(33, 29);

    companion object {
        /** Enumeration of every flag entry defined here, sorted by client processing order. */
        val byOrder: List<Rev948NpcUpdateMaskKey> = entries.sortedBy { it.order }

        /** LE bit positions of the expansion bits in the 948 NPC-info bitset header (CONFIRMED). */
        val EXPANSION_BITS = intArrayOf(6, 8, 19, 25)
    }
}
