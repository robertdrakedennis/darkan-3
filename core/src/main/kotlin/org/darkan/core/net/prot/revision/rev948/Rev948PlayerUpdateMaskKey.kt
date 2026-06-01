package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.update.PlayerUpdateMaskKey

/**
 * Rev948 PLAYER_INFO extended-info flag bitset.
 *
 * **CONFIRMED by exhaustive walk** of `jag::PlayerEntity::ProcessExtendedInfo @ 0x0015e110`
 * (rs2client.948-2-2). Every entry below corresponds to one top-level `if ((mask & flag) != 0)`
 * test in the decompiled body, read in source order (= the client's fixed dispatch order).
 * This supersedes the earlier provisional table that was derived from a partial top-level grep
 * of a delta doc — that table had APPEARANCE missing and several wrong bit/order pairings.
 *
 * Header: 1..4 byte LE value. EXPANSION BITS = {0, 13, 22} (CONFIRMED from the decode prologue:
 * byte0 bit0 -> read byte1; byte1 bit5 [= overall bit 13] -> read byte2; byte2 bit6
 * [= overall bit 22] -> read byte3). Same expansion pattern as 947-3.
 *
 * **Key 947-3 -> 948 migrations (the world-login render blocker):**
 *  * APPEARANCE        : bit 2  -> **bit 3**  (order 4)   — len byte + mode-buffer -> QueueExtendedInfoPacket
 *  * FORCED_MOVEMENT   : bit 4  -> **bit 7**  (order 9)   — SetForcedMovement (named fn)
 *  * OVERHEAD_OPACITY  : bit 8  -> **bit 12** (order 18)  — stores to PlayerEntity+0x1074 (offset match)
 *  * CHAT_TEXT         : bit 15 -> **bit 10** (order 20)  — ChatHistory::AddChat (named fn)
 *  * FACE_DIRECTION    : bit 1  -> bit 1      (order 14)  — JagexAngleToRadians -> player+0x23c (unchanged bit)
 *  * POSITION_COLOR    : bit 22 -> bit 21     (order 21)  — HSLToRGBLookup -> player[0x32..0x34]
 *  * HITMARKS          : bit 3  -> bit 4      (order 22)  — AddHitmark / AddHeadbar (named fns)
 *  * OVERHEAD_CHAT     : bit 21 -> bit 20     (order 3)   — AddChat with effect flags
 *  * CHAT_TEXT_PRIVATE : bit 16 -> bit 14     (order 10)  — compound guard (len + buffer)
 *
 * **948 ext-info readers use a per-field "scrambled" mode byte.** Each `gScrambled*` read in the
 * handler (`gScrambledByte/Ubyte/Ushort/Uint/Medium`, plus `FUN_0047f240` mode-select short)
 * consumes a leading 1-byte mode selector choosing one of 4 transforms (0=BE/plain, 1=LE/reverse,
 * 2=BE+0x80, 3=LE+0x80) before the value. The server may always emit mode 0 + the plain value.
 * See `docs/net/serverprot/948-research-C-player-npc-misc.md` for the full reader spec.
 *
 * Blocks whose exact wire payload was NOT fully characterised here are named UNK_BIT<n> with the
 * observed read shape in the comment; do not wire production encoders for those without a focused
 * re-walk + capture. The named entries (APPEARANCE/FORCED_MOVEMENT/etc.) are encoder-ready.
 */
enum class Rev948PlayerUpdateMaskKey(
    override val bit: Int,
    override val order: Int,
) : PlayerUpdateMaskKey {
    // ord1  bit26 0x4000000 : SPOT_ANIM_REMOVAL — count byte + BE-short slot ids, -1 => remove all (FUN_0044f360/0044f150)
    SPOT_ANIM_REMOVAL(26, 1),
    // ord2  bit18 0x40000   : transient bool -> PlayerEntity+0x1071 (1 scrambled byte; ==1)
    TRANSIENT_BOOL_BIT18(18, 2),
    // ord3  bit20 0x100000  : OVERHEAD_CHAT — string (FUN_00ad89a0) + flags byte -> ChatHistory::AddChat
    OVERHEAD_CHAT(20, 3),
    // ord4  bit3  0x8       : APPEARANCE — len byte + mode-buffer -> PathingEntity::QueueExtendedInfoPacket
    APPEARANCE(3, 4),
    // ord5  bit6  0x40      : OVERHEAD_TEXT — gScrambledMedium type-dispatch (FUN_0042e610, player+0x35)
    OVERHEAD_TEXT(6, 5),
    // ord6  bit24 0x1000000 : spot-anim list (count byte + per-anim flags/conditional uints) — complex
    SPOT_ANIM_LIST_BIT24(24, 6),
    // ord7  bit11 0x800     : spot-anim slot block (g2 + scrambled uint + scrambled byte)
    UNK_BIT11(11, 7),
    // ord8  bit15 0x8000    : spot-anim slot block (g2 + scrambled uint + scrambled byte)
    UNK_BIT15(15, 8),
    // ord9  bit7  0x80      : FORCED_MOVEMENT — 6x scrambled byte + 3x g2 -> PathingEntity::SetForcedMovement
    FORCED_MOVEMENT(7, 9),
    // ord10 bit14 0x4000    : CHAT_TEXT_PRIVATE — compound guard: len byte + mode-buffer (FUN_00121980)
    CHAT_TEXT_PRIVATE(14, 10),
    // ord11 bit25 0x2000000 : spot-anim slot block (g2 + scrambled uint + scrambled byte)
    UNK_BIT25(25, 11),
    // ord12 bit16 0x10000   : spot-anim list with SpotAnim::GetTypeBySlot
    SPOT_ANIM_LIST_BIT16(16, 12),
    // ord13 bit9  0x200     : 2x scrambled byte + g2
    UNK_BIT9(9, 13),
    // ord14 bit1  0x2       : FACE_DIRECTION — g2 angle -> JagexAngleToRadians -> player+0x23c
    FACE_DIRECTION(1, 14),
    // ord15 bit23 0x800000  : spot-anim list
    SPOT_ANIM_LIST_BIT23(23, 15),
    // ord16 bit5  0x20      : EXACT_MOVE — 4x gSmart2or4s + scrambled byte (anchor/timer vector)
    EXACT_MOVE(5, 16),
    // ord17 bit19 0x80000   : spot-anim slot block (g2 + scrambled uint + scrambled byte)
    UNK_BIT19(19, 17),
    // ord18 bit12 0x1000    : OVERHEAD_OPACITY — 1 scrambled byte -> PlayerEntity+0x1074
    OVERHEAD_OPACITY(12, 18),
    // ord19 bit2  0x4       : spot-anim slot block (g2 + scrambled uint + scrambled byte)
    UNK_BIT2(2, 19),
    // ord20 bit10 0x400     : CHAT_TEXT — string (FUN_00ad89a0) -> ChatHistory::AddChat(type 2)
    CHAT_TEXT(10, 20),
    // ord21 bit21 0x200000  : POSITION_COLOR — 3x ubyte HSL + byte + 2x g2 -> HSLToRGBLookup -> player[0x32..0x34]
    POSITION_COLOR(21, 21),
    // ord22 bit4  0x10      : HITMARKS — count byte + gSmart1or2 list (0x7fff/0x7ffe) -> AddHitmark/AddHeadbar
    HITMARKS(4, 22),
    // ord23 bit17 0x20000   : head-icon — scrambled byte + 3x g2
    HEAD_ICON_BIT17(17, 23);

    companion object {
        /** Enumeration of every flag bit defined here, sorted by client processing order. */
        val byOrder: List<Rev948PlayerUpdateMaskKey> = entries.sortedBy { it.order }

        /** LE bit positions of the expansion bits in the 948 player-info bitset header (CONFIRMED). */
        val EXPANSION_BITS = intArrayOf(0, 13, 22)
    }
}
