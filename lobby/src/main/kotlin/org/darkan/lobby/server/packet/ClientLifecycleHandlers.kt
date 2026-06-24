package org.darkan.lobby.server.packet

import org.darkan.core.net.Session
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.handler.PacketHandler

class MapBuildCompleteHandler : PacketHandler<Session, MapBuildComplete> {
    override suspend fun handle(player: Session, packet: MapBuildComplete) = Unit
}

class AntiCheatChallengeResponseHandler : PacketHandler<Session, AntiCheatChallengeResponse> {
    override suspend fun handle(player: Session, packet: AntiCheatChallengeResponse) = Unit
}
