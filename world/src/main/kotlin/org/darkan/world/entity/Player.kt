package org.darkan.world.entity

import org.darkan.core.model.Account
import org.darkan.core.net.session.GameSession
import org.darkan.world.interfaces.InterfaceManager
import org.darkan.world.world.Viewport

val GameSession.player: Player?
    get() = attachment as? Player

val GameSession.interfaceManager: InterfaceManager
    get() = (player ?: error("GameSession has no Player attached")).interfaceManager

/**
 * World-side player entity. Wraps an authenticated [Account] and its live [GameSession]
 * with simulation state (tile, viewport, pending update masks).
 *
 * Per PLAYER_INFO (op 27) §3, every player carries two bits per tick:
 *  * [active] — was moving / had updates last tick (drives high-res "active" loop selection).
 *  * [stationary] — flagged once the player has been idle for a sustained window.
 */
class Player(
    index: Int,
    val account: Account,
    val session: GameSession,
) : Entity() {
    /**
     * Player slot id (1..2047). Backing field exposed via the Entity.index property override.
     * Mutable so [org.darkan.world.world.Players.allocate] can install the final slot id
     * after construction (the Players API claims a slot and then writes its index back via
     * a setter, which lets us keep slot allocation and Player construction decoupled).
     */
    override var index: Int = index
        internal set
    /** Pre-built appearance block; encoder reads [Appearance.cachedBytes] and falls back to rebuild on null. */
    val appearance: Appearance = Appearance(account)

    val interfaceManager: InterfaceManager = InterfaceManager(this)

    /** Per-player visibility state — high-res / low-res index lists, cached appearance hashes, build area center. */
    val viewport: Viewport = Viewport(this)

    val varps: VarpManager = VarpManager()

    @Volatile
    var readyForTick: Boolean = false

    /**
     * PLAYER_INFO "active" bit — true if this player was moving or had updates the previous tick.
     *
     * Defaults to `false` to match the op81 GPI prefix the client decodes at world entry: the local
     * player seeds `active=false` (`resetFromGpiPrefix` →
     * [org.darkan.world.world.PlayerInfoSlots.seedFromGpiPrefix]). NOTE the per-viewer GPI slot model
     * ([org.darkan.world.world.PlayerInfoSlots]) — not this field — is the SOURCE OF TRUTH the
     * op22 passes filter on; this field is retained as incidental simulation state and kept
     * consistent with the spawn cohort.
     */
    var active: Boolean = false

    /** PLAYER_INFO "stationary" bit — true once the player has been idle long enough to qualify for the stationary cohort. */
    var stationary: Boolean = false

    init {
        appearance.ensureCachedBytes()
    }

    companion object {
        // Body/Wearpos-def equipment slot indices (also the worn-container 94 slot layout).
        const val EQUIP_SLOT_HAT = 0
        const val EQUIP_SLOT_CAPE = 1
        const val EQUIP_SLOT_AMULET = 2
        const val EQUIP_SLOT_WEAPON = 3
        const val EQUIP_SLOT_CHEST = 4
        const val EQUIP_SLOT_SHIELD = 5
        const val EQUIP_SLOT_LEGS = 7
        const val EQUIP_SLOT_HANDS = 9
        const val EQUIP_SLOT_FEET = 10
        const val EQUIP_SLOT_RING = 12
        const val EQUIP_SLOT_ARROWS = 13
        const val EQUIP_SLOT_AURA = 14
        const val EQUIP_SLOT_POCKET = 17
        const val EQUIP_SLOT_WINGS = 18
    }
}
