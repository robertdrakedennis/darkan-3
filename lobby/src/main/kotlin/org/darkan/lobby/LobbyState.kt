package org.darkan.lobby

import org.darkan.core.worldlist.Country
import org.darkan.core.worldlist.World
import org.darkan.core.worldlist.WorldList

/**
 * Global lobby state — world list, online players, etc.
 */
object LobbyState {
    val worldList = WorldList(300)

    fun init() {
        // Default development world
        worldList.put(World(
            number = 300,
            hostname = "localhost",
            port = 43595,
            activity = "Darkan",
            country = Country.USA,
            members = true,
        ))
    }
}
