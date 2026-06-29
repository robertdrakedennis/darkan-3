package org.darkan.world.server.packet

import org.darkan.core.Logger.logTrace
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.player

/**
 * IF_BUTTON1..10 handler. It validates that the clicked interface is open for this player before
 * later component-specific dispatch layers get a chance to act on the input.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF

        val worldPlayer = player.player
        if (worldPlayer == null) {
            logTrace("IF_BUTTON${packet.buttonId} ($interfaceId:$componentId) before player attached from ${player.ip}")
            return
        }

        if (!worldPlayer.interfaceManager.validateClick(packet.interfaceHash)) {
            logTrace("Dropping IF_BUTTON${packet.buttonId} on not-open interface $interfaceId:$componentId from ${player.username}")
            return
        }

        logTrace("IF_BUTTON${packet.buttonId} $interfaceId:$componentId slot=${packet.slotId} item=${packet.itemId} from ${player.username}")
    }
}
