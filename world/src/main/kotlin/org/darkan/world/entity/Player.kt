package org.darkan.world.entity

import org.darkan.core.model.Account
import org.darkan.core.net.session.GameSession
import org.darkan.world.world.Viewport

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

    /** Per-player visibility state — high-res / low-res index lists, cached appearance hashes, build area center. */
    val viewport: Viewport = Viewport(this)

    val varps: VarpManager = VarpManager()

    @Volatile
    var readyForTick: Boolean = false

    /** PLAYER_INFO "active" bit — true if this player was moving or had updates the previous tick. */
    var active: Boolean = true

    /** PLAYER_INFO "stationary" bit — true once the player has been idle long enough to qualify for the stationary cohort. */
    var stationary: Boolean = false

    init {
        appearance.ensureCachedBytes()
    }
}
