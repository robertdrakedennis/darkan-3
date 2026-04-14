package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.FriendListDel
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.social.SocialGateway

class FriendListDelHandler : PacketHandler<GameSession, FriendListDel> {
    override suspend fun handle(player: GameSession, packet: FriendListDel) {
        logInfo("FRIENDLIST_DEL from ${player.username}: '${packet.displayName}'")
        SocialGateway.handleClientPacket(player.username, packet)
    }
}
