package org.darkan.lobby.server.packet

import org.darkan.core.net.Session
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.handler.PacketHandler

/** TERMINAL: client keepalive — no server action required (the server sends its own keepalive on a timer). */
class PingHandler : PacketHandler<Session, Ping> {
    override suspend fun handle(player: Session, packet: Ping) {
        // Keepalive — nothing to do. Server sends its own keepalive on a timer.
    }
}
