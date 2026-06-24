package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.MacOsLobbyHandoff
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession

/**
 * Handles IfButton clicks in the lobby UI (IF_BUTTON1..7/10, the size-8 interface-CLICK family —
 * 948 ops 127/103/92/45/30/68/43/21). Consumes + logs every click at CONFIG.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF
        logInfo("IfButton from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${packet.slotId} item=${packet.itemId} option=${packet.buttonId}")
    }
}

class MacOsLobbyHandoffHandler : PacketHandler<GameSession, MacOsLobbyHandoff> {
    override suspend fun handle(player: GameSession, packet: MacOsLobbyHandoff) {
        val button = packet.button
        if (button == null) {
            logInfo("MacOsLobbyHandoff from ${player.ip}: no embedded Play Now click")
            return
        }
        val interfaceId = button.interfaceHash ushr 16
        val componentId = button.interfaceHash and 0xFFFF
        logInfo("MacOsLobbyHandoff from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${button.slotId} item=${button.itemId} option=${button.buttonId}")
    }
}
