package org.darkan.lobby.server.packet

import org.darkan.core.net.Session
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.handler.PacketHandler

class PingHandler : PacketHandler<Session, Ping> {
    override suspend fun handle(player: Session, packet: Ping) {
        // Keepalive — nothing to do. Server sends its own keepalive on a timer.
    }
}
