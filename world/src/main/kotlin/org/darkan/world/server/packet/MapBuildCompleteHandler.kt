package org.darkan.world.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

/**
 * STUB: the client sends this when it finishes building the world scene. We only log it today — the
 * server does NOT yet use it to gate readiness / zone-streaming (readiness is set earlier in the login
 * flow). Wire it into player-state when the world-entry pipeline is refactored. See NETWORKING_AUDIT.md.
 */
class MapBuildCompleteHandler : PacketHandler<GameSession, MapBuildComplete> {
    override suspend fun handle(player: GameSession, packet: MapBuildComplete) {
        logInfo("Map build complete from ${player.username}@${player.ip}")
    }
}
