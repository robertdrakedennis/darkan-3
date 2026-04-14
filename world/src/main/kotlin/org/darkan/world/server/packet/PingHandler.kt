package org.darkan.world.server.packet

import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

class PingHandler : PacketHandler<GameSession, Ping> {
    override suspend fun handle(player: GameSession, packet: Ping) {
        // Keepalive — nothing to do. Server sends its own keepalive on a timer.
    }
}
