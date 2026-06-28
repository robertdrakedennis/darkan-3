package org.darkan.lobby.server.packet

import org.darkan.core.EnvVars
import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.MacOsLobbyHandoff
import org.darkan.core.net.prot.SwitchWorld
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState

/**
 * Handles IfButton clicks in the lobby UI (IF_BUTTON1..7/10, the size-8 interface-CLICK family —
 * 948 ops 127/103/92/45/30/68/43/21). Consumes + logs every click at INFO.
 *
 * The lobby "Play Now" click (interface 906, component 81, buttonId 1 — see [isPlayNowClick]) is, by
 * default, LOGGED ONLY: the client drives the lobby->world handoff itself from the login-data world
 * tail (LoginServer.buildLobbyData) and renders the "Joining World / Please Wait" dialog via its own
 * LOBBY_ENTERGAME flow — matching PROD, which never sends op212/op213. The legacy op213 SWITCH_WORLD
 * push (which bypasses that dialog by driving WorldSwitcher / MAIN_STATE 0x25 directly) is gated
 * behind [EnvVars.lobbyPushWorldSwitch] (LOBBY_PUSH_WORLD_SWITCH=true). All other clicks — world-row
 * Select hotspots (e.g. 906/32, 906/3), tabs (friends/ignore), news entries, settings — are
 * client-side navigation and are only logged; they must NOT log the player into a world.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF
        logInfo("IfButton from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${packet.slotId} item=${packet.itemId} option=${packet.buttonId}")
        if (packet.isPlayNowClick()) {
            player.sendWorldSwitch("Play Now click")
        }
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
        if (button.isPlayNowClick()) {
            player.sendWorldSwitch("MacOsLobbyHandoff Play Now")
        }
    }
}

private suspend fun GameSession.sendWorldSwitch(source: String) {
    if (!EnvVars.lobbyPushWorldSwitch) {
        // PROD-like cold-lobby handoff (default): do NOT push op213 SwitchWorld. The client drives the
        // world connect itself from the login-data world tail (LoginServer.buildLobbyData #26-31) and
        // renders the "Joining World N / Please Wait" dialog via its own LOBBY_ENTERGAME flow
        // (script3062 -> script10210 -> script3093). PROD never sends op212/op213; pushing op213 sets
        // MAIN_STATE 0x25 and opens the world socket directly through WorldSwitcher, BYPASSING that
        // dialog loop -> the dialog never renders. Set LOBBY_PUSH_WORLD_SWITCH=true to restore the push.
        logInfo("$source from $ip; client-driven handoff via login-data tail (op213 push disabled — renders 'Joining World' dialog)")
        return
    }
    if (lobbyWorldSwitchSent) {
        logInfo("$source from $ip ignored; world switch already sent")
        return
    }

    val world = LobbyState.worldList.get(EnvVars.worldId)
    if (world == null) {
        logInfo("$source from $ip ignored; world ${EnvVars.worldId} is not registered")
        return
    }

    lobbyWorldSwitchSent = true
    logInfo("$source from $ip; switching to world ${world.number} at ${world.hostname}:${world.port}")
    send(SwitchWorld(hostname = world.hostname, worldId = world.number, port1 = world.port, port2 = world.port))
    flush()
}

private fun IfButton.isPlayNowClick(): Boolean {
    val interfaceId = interfaceHash ushr 16
    val componentId = interfaceHash and 0xFFFF
    return buttonId == 1 && interfaceId == 906 && componentId == 81
}
