package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.IgnoreListAdd
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.social.SocialManager

class IgnoreListAddHandler : PacketHandler<GameSession, IgnoreListAdd> {
    override suspend fun handle(player: GameSession, packet: IgnoreListAdd) {
        logInfo("IGNORELIST_ADD from ${player.username}: '${packet.displayName}'")
        val account = SocialManager.getPlayer(player.username)?.account ?: return
        SocialManager.addIgnore(account, packet.displayName)
    }
}
