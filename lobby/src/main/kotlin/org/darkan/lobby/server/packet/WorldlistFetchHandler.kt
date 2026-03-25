package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.Session
import org.darkan.core.net.prot.RequestWorldList
import org.darkan.core.net.prot.WorldListPacket
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.lobby.LobbyState

class WorldlistFetchHandler : PacketHandler<Session, RequestWorldList> {
    override suspend fun handle(player: Session, packet: RequestWorldList) {
        val wl = LobbyState.worldList
        val needsRefresh = packet.worldlistVersion != wl.revision
        logInfo("WORLDLIST_FETCH from ${player.ip}: clientRev=${packet.worldlistVersion}, serverRev=${wl.revision}, fullRefresh=$needsRefresh")
        player.send(WorldListPacket(wl, fullRefresh = needsRefresh))
    }
}
