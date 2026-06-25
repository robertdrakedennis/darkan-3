package org.darkan.world.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

class MapBuildCompleteHandler : PacketHandler<GameSession, MapBuildComplete> {
    override suspend fun handle(player: GameSession, packet: MapBuildComplete) {
        logInfo("Map build complete from ${player.username}@${player.ip}")
    }
}
