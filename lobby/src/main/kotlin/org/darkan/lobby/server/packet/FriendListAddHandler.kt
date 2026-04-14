package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.FriendListAdd
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.social.SocialGateway

class FriendListAddHandler : PacketHandler<GameSession, FriendListAdd> {
    override suspend fun handle(player: GameSession, packet: FriendListAdd) {
        logInfo("FRIENDLIST_ADD from ${player.username}: '${packet.displayName}'")
        SocialGateway.handleClientPacket(player.username, packet)
    }
}
