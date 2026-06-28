package org.darkan.lobby.server.packet

import org.darkan.core.net.Session
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.handler.PacketHandler

/**
 * TERMINAL (lobby): no world scene exists in the lobby, so map-build-complete needs no action here.
 * The live one is [org.darkan.world.server.packet.MapBuildCompleteHandler]. (Distinct binding per
 * context; de-duplicating the registration is deferred to the handler-registration overhaul — see
 * NETWORKING_AUDIT.md Phase 2.)
 */
class MapBuildCompleteHandler : PacketHandler<Session, MapBuildComplete> {
    override suspend fun handle(player: Session, packet: MapBuildComplete) = Unit
}

/**
 * TERMINAL (lobby): the lobby never issues anti-cheat challenges (a WORLD concern), so a response here
 * is unexpected and intentionally ignored. The live validator is
 * [org.darkan.world.server.packet.AntiCheatChallengeResponseHandler], which checks the response and
 * disconnects on mismatch. (Inert by design — NOT an unimplemented stub.)
 */
class AntiCheatChallengeResponseHandler : PacketHandler<Session, AntiCheatChallengeResponse> {
    override suspend fun handle(player: Session, packet: AntiCheatChallengeResponse) = Unit
}
