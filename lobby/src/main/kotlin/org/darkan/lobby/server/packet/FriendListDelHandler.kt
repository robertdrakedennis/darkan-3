package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.FriendListDel
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.social.SocialManager

class FriendListDelHandler : PacketHandler<GameSession, FriendListDel> {
    override suspend fun handle(player: GameSession, packet: FriendListDel) {
        logInfo("FRIENDLIST_DEL from ${player.username}: '${packet.displayName}'")
        val account = SocialManager.getPlayer(player.username)?.account ?: return
        SocialManager.removeFriend(account, packet.displayName)
    }
}
