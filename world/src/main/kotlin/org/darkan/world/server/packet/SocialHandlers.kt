package org.darkan.world.server.packet

import org.darkan.core.Logger.logTrace
import org.darkan.core.net.prot.*
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.world.server.WorldServer

/**
 * Social packet handlers for the world server.
 *
 * These handlers forward social ClientProt packets to the lobby server via the
 * SocialClient WebSocket gateway. The lobby owns all social state (friends list,
 * ignore list, clans, private messages) and processes these requests, sending
 * ServerProt responses back to the world server for delivery to the player session.
 */

class FriendListAddHandler : PacketHandler<GameSession, FriendListAdd> {
    override suspend fun handle(player: GameSession, packet: FriendListAdd) {
        logTrace("FRIENDLIST_ADD from ${player.username}: '${packet.displayName}'")
        WorldServer.forwardToLobby(player.username, packet)
    }
}

class FriendListDelHandler : PacketHandler<GameSession, FriendListDel> {
    override suspend fun handle(player: GameSession, packet: FriendListDel) {
        logTrace("FRIENDLIST_DEL from ${player.username}: '${packet.displayName}'")
        WorldServer.forwardToLobby(player.username, packet)
    }
}

class IgnoreListAddHandler : PacketHandler<GameSession, IgnoreListAdd> {
    override suspend fun handle(player: GameSession, packet: IgnoreListAdd) {
        logTrace("IGNORELIST_ADD from ${player.username}: '${packet.displayName}'")
        WorldServer.forwardToLobby(player.username, packet)
    }
}

class ClanChannelKickUserHandler : PacketHandler<GameSession, ClanChannelKickUser> {
    override suspend fun handle(player: GameSession, packet: ClanChannelKickUser) {
        logTrace("CLANCHANNEL_KICKUSER from ${player.username}: '${packet.username}'")
        WorldServer.forwardToLobby(player.username, packet)
    }
}

class MessagePrivateSendHandler : PacketHandler<GameSession, MessagePrivateSend> {
    override suspend fun handle(player: GameSession, packet: MessagePrivateSend) {
        logTrace("MESSAGE_PRIVATE from ${player.username} to '${packet.toDisplayName}'")
        WorldServer.forwardToLobby(player.username, packet)
    }
}
