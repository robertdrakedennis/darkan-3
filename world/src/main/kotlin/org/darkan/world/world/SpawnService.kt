package org.darkan.world.world

import org.darkan.core.model.Account
import world.gregs.voidps.type.Tile

/**
 * Single source for the tile used when a character enters the world.
 *
 * Saved-position persistence is not modelled yet, so v1 intentionally falls back to the default
 * spawn through this service instead of letting individual op81/GPI/movement call sites carry their
 * own coordinates.
 */
object SpawnService {
    val DEFAULT_SPAWN: Tile = Tile(3224, 3216, 0)

    fun initialSpawn(account: Account): Tile =
        savedSpawn(account) ?: DEFAULT_SPAWN

    @Suppress("UNUSED_PARAMETER")
    fun savedSpawn(account: Account): Tile? = null
}
