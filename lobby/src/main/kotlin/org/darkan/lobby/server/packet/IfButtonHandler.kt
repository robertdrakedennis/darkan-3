package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.SwitchWorld
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState

/**
 * Handles IfButton clicks in the lobby UI (IF_BUTTON1..7/10, the size-8 interface-CLICK family —
 * 948 ops 127/103/92/45/30/68/43/21). Consumes + logs every click at CONFIG.
 *
 * Only a click on the lobby world-select component triggers a SWITCH_WORLD. All other clicks
 * (tabs like friends/ignore, news entries, settings) are client-side navigation and are just
 * logged — they must NOT log the player into a world.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    companion object {
        /** Lobby parent interface (the IF_SETTOPLEVELINTERFACE id). */
        private const val LOBBY_INTERFACE_ID = 906

        /**
         * Component on interface 906 that represents the world-select / play action.
         * Ground truth: the captured live-lobby click that initiated a world transfer was
         * interface 906 / component 32 (capture/login-20260531-191837_s1, C->S op127).
         * The friends tab is component 33 (and other tabs are nearby) — those must NOT switch worlds.
         * TODO: map the remaining 906 components (per-world rows / slot semantics) once observed.
         */
        private const val WORLD_SELECT_COMPONENT = 32
    }

    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF
        logInfo("IfButton from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${packet.slotId} item=${packet.itemId} option=${packet.buttonId}")

        // Only the world-select component initiates a world login. Everything else (tabs, news, etc.)
        // is client-side navigation — log and return.
        if (interfaceId != LOBBY_INTERFACE_ID || componentId != WORLD_SELECT_COMPONENT) {
            return
        }

        val target = LobbyState.worldList.getDefault()
        if (target == null) {
            logInfo("No worlds registered — cannot switch world for ${player.ip}")
            return
        }

        logInfo("World-select click → switching ${player.ip} to world ${target.number} at ${target.hostname}:${target.port}")
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
