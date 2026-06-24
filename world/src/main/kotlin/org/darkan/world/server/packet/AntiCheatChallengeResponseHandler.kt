package org.darkan.world.server.packet

import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

class AntiCheatChallengeResponseHandler : PacketHandler<GameSession, AntiCheatChallengeResponse> {
    override suspend fun handle(player: GameSession, packet: AntiCheatChallengeResponse) {
        val pending = player.pendingAntiCheatChallenge
        if (pending == null) {
            logWarn("Unexpected anti-cheat response from ${player.username}@${player.ip}: sequence=${packet.sequence}")
            return
        }

        if (packet.challengeA != pending.challengeA || packet.challengeB != pending.challengeB) {
            logWarn("Invalid anti-cheat response from ${player.username}@${player.ip}: sequence=${packet.sequence}")
            player.disconnect()
            return
        }

        player.clearAntiCheatChallenge()
    }
}
