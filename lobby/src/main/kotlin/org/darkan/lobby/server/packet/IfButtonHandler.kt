package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.SwitchWorld
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState

/**
 * Handles IfButton clicks in the lobby UI (IF_BUTTON1..7/10, the size-8 interface-CLICK family —
 * 948 ops 127/103/92/45/30/68/43/21). Consumes + logs every click at CONFIG so they no longer
 * fall through to the "Unhandled ClientProt" warn path.
 *
 * Currently every click also triggers a SWITCH_WORLD to the first registered world — that's enough
 * to test the lobby→world transfer flow. TODO: decode the interfaceHash to distinguish the play
 * button from other clicks (tabs, news entries, etc.), and decode the selected world from client
 * state.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF
        logInfo("IfButton from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${packet.slotId} item=${packet.itemId} option=${packet.buttonId}")

        val target = LobbyState.worldList.getDefault()
        if (target == null) {
            logInfo("No worlds registered — cannot switch world for ${player.ip}")
            return
        }

        logInfo("Switching ${player.ip} to world ${target.number} at ${target.hostname}:${target.port}")
        player.send(SwitchWorld(
            hostname = target.hostname,
            worldId = target.number,
            port1 = target.port,
            port2 = target.port,
            pendingFlag = 0,
        ))
        player.flush()
    }
}
