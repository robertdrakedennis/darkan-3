package org.darkan.core.net.prot.update

/**
 * One headbar entry inside an update mask's HITMARKS list.
 *
 * Per `docs/net/serverprot/player-extinfo-948.md` / `docs/net/serverprot/npc-extinfo-948.md` hitmark blocks:
 * - if `duration == 0x7FFF` the encoder emits only `[type, 0x7FFF]` => RemoveHeadbar
 * - else encoder emits `[type, duration, delay, startWidth, (endWidth if duration!=0),
 *   fromFill (gSmart1or2_signed; <0 emitted as -1), and optionally fromFillAlpha/toFillAlpha]`
 */
data class Headbar(
    val type: Int,
    val duration: Int,
    val delay: Int = 0,
    val startWidth: Int = 0,
    val endWidth: Int = startWidth,
    val fromFill: Int = -1,
    val fromFillAlpha: Int = 0,
    val toFillAlpha: Int = fromFillAlpha,
)
