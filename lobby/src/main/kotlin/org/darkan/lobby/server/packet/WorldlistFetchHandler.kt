package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.Session
import org.darkan.core.net.prot.RequestWorldList
import org.darkan.core.net.prot.WorldListPacket
import org.darkan.core.net.prot.handler.PacketHandler

class WorldlistFetchHandler : PacketHandler<Session, RequestWorldList> {
    override suspend fun handle(player: Session, packet: RequestWorldList) {
        logInfo("WORLDLIST_FETCH from ${player.ip}: checksum=0x${"%08x".format(packet.worldlistVersion)}")
        player.send(WorldListPacket(packet.worldlistVersion))
        logInfo("Sent WORLDLIST_FETCH_REPLY to ${player.ip}")
    }
}
