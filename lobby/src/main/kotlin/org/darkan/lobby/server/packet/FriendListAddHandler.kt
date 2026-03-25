package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.FriendListAdd
import org.darkan.core.net.prot.UpdateFriendList
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.social.SocialManager

class FriendListAddHandler : PacketHandler<GameSession, FriendListAdd> {
    override suspend fun handle(player: GameSession, packet: FriendListAdd) {
        logInfo("FRIENDLIST_ADD from ${player.username}: '${packet.displayName}'")
        val account = SocialManager.getPlayer(player.username)?.account ?: return

        val entry = SocialManager.addFriend(account, packet.displayName)
        if (entry != null) {
            player.send(UpdateFriendList(listOf(entry)))
            player.flush()
            SocialManager.notifyFriendsOfStatus(account, online = true)
        }
    }
}
