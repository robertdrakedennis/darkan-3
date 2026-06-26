package org.darkan.lobby.server.packet

import org.darkan.core.net.Session
import org.darkan.core.net.prot.SceneGraphReport
import org.darkan.core.net.prot.handler.PacketHandler

class SceneGraphReportHandler : PacketHandler<Session, SceneGraphReport> {
    override suspend fun handle(player: Session, packet: SceneGraphReport) = Unit
}
