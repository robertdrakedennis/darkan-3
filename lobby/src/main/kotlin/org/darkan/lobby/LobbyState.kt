package org.darkan.lobby

import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.social.gateway.GatewayWorldInfo
import org.darkan.core.worldlist.Country
import org.darkan.core.worldlist.World
import org.darkan.core.worldlist.WorldList

/**
 * Global lobby state — world list, online players, etc.
 *
 * The world list is dynamically updated when world servers register/unregister
 * via the SocialGateway WebSocket connection.
 */
object LobbyState {
    val worldList = WorldList(300)

    fun init() {
        // Default development world (removed when a real world server registers)
        worldList.put(World(
            number = EnvVars.worldId,
            hostname = EnvVars.worldPublicHost,
            port = EnvVars.worldPort,
            activity = EnvVars.worldActivity.ifBlank { "-" },
            country = Country.valueOf(EnvVars.worldCountry),
            members = EnvVars.worldMembers,
            quickchat = EnvVars.worldQuickChat,
            pvp = EnvVars.worldPvp,
            lootShare = EnvVars.worldLootShare,
            highlighted = EnvVars.worldHighlighted,
        ))
    }

    /** Register a world from gateway connection. Replaces any existing entry for the same ID. */
    fun registerWorld(info: GatewayWorldInfo) {
        val world = info.toWorld()
        worldList.put(world)
        logInfo("LobbyState: registered world ${info.worldId} (${info.worldName}) at ${info.publicHost}:${info.port}")
    }

    /** Unregister a world when its gateway connection drops. */
    fun unregisterWorld(worldId: Int) {
        worldList.remove(worldId)
        logInfo("LobbyState: unregistered world $worldId")
    }

    /** Update player count for a world. */
    fun updatePlayerCount(worldId: Int, delta: Int) {
        val world = worldList.get(worldId) ?: return
        world.playersOnline = (world.playersOnline + delta).coerceAtLeast(0)
    }
}
