package org.darkan.core.net.prot.update

/**
 * One hitmark inside an update mask's HITMARKS list.
 *
 * Per the player-info HITMARKS block (see `docs/net/serverprot/948-delta-from-947-3.md`):
 * - `type == 0x7FFF` => extended hitmark (4 gSmart1or2 fields: type/damage/soak/delay)
 * - `type == 0x7FFE` => tinted hitmark (extra p1 tint type; soak/delay implicit -1)
 * - else            => regular hitmark (damage stored in `type`; soak=delay=-1; duration follows)
 *
 * The encoder must inspect [type] to pick the wire layout.
 */
data class Hit(
    val type: Int,
    val damage: Int = -1,
    val soak: Int = -1,
    val delay: Int = -1,
    val duration: Int = -1,
    val tintType: Int = -1,
)

