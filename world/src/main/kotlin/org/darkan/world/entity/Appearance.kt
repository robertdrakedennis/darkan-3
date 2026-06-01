package org.darkan.world.entity

import org.darkan.core.model.Account

/**
 * Player appearance state. Encoder in B6 reads these fields and writes the binary
 * appearance block consumed by PLAYER_INFO bit 2 (mask 0x4) per
 * `docs/net/serverprot/player-info-947-3.md` §APPEARANCE.
 *
 * To avoid rebuilding the block every tick, the encoded byte array is memoised in
 * [cachedBytes]. Any code path that mutates a field below MUST set `cachedBytes = null`
 * so the next encoder invocation re-renders. (A property delegate / setter wrapper is
 * deferred to B6 when the actual encoder lands.)
 */
class Appearance(account: Account) {
    var gender: Int = 0
    var skullIcon: Int = -1
    var prayerIcon: Int = -1
    var headIcon: Int = -1

    /** 15-slot equipment (helm, cape, amulet, weapon, body, shield, legs, hands, feet, ring, ammo, aura, plus 3 cosmetic slots). */
    val equipment: IntArray = IntArray(15) { -1 }

    /** 5-channel colour palette (hair, torso, legs, feet, skin). */
    val colours: IntArray = IntArray(5) { 0 }

    var combatLevel: Int = 3
    var totalLevel: Int = 0
    var displayName: String = account.displayName
    var title: String = ""

    /**
     * Cached encoded appearance block. Builders MUST invalidate by setting `cachedBytes = null`
     * whenever any field above is mutated; the next encode pass will regenerate the block.
     */
    var cachedBytes: ByteArray? = null
}
