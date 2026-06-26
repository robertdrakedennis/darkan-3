package org.darkan.world.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.SceneGraphReport
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

class SceneGraphReportHandler : PacketHandler<GameSession, SceneGraphReport> {
    override suspend fun handle(player: GameSession, packet: SceneGraphReport) {
        logInfo("Scene graph report from ${player.username}@${player.ip}: value=${packet.value}")
    }
}
