package org.darkan.lobby.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.SwitchWorld
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.LobbyState

/**
 * Handles IfButton clicks in the lobby UI (IF_BUTTON1..10, the size-8 interface-CLICK family —
 * 948 ops 127/103/92/45/30/68/43/21/13/23). Consumes + logs every click at INFO.
 *
 * ## World selection (option B — per-world, server-driven)
 * A click on a WORLD ROW of the lobby worldlist (parent interface 906) is what initiates a
 * lobby->world transfer. We resolve which world was clicked from the worldlist and send op213
 * SWITCH_WORLD for THAT world; op213 both commits the world target (WorldSwitcher+0x20) AND
 * triggers the client's reconnect to the world's host:port2. We deliberately do NOT:
 *   - auto-switch the moment the worldlist tab is opened, and
 *   - blindly fall back to `worldList.getDefault()` regardless of the clicked row.
 *
 * We trigger on the row click itself (interface 906) rather than the live client's separate
 * "Play" button (interface 1281, observed in capture) because our lobby does not render interface
 * 1281; op213 makes the two-step select-then-play unnecessary.
 *
 * ## Row -> world mapping (KNOWN LIMITATION — flagged)
 * The mapping from an interface-906 row component id to a specific world is performed entirely by
 * the client-side lobby worldlist CS2 script. Interface 906 is a lobby UI interface and is NOT
 * present in the cache gameval / cs2-dumps, so we cannot derive `componentId -> worldId` from the
 * binary (confirmed: world_handoff_wire_spec "row_to_world_mapping"; no cs2-dump for if 906). The
 * live capture also shows world-row clicks carry `slotId=0xFFFF, itemId=-1` (a plain row button) —
 * i.e. the slot does NOT carry the row index either.
 *
 * Resolution strategy (safest correct mapping available without the 906 layout):
 *   1. If [IfButton.slotId] is a valid index into the worldlist (some interface variants DO send the
 *      row index in the slot), select that world. (Honours "use slotId; same order the rows were
 *      sent" — getWorldArray() is the exact order op216 WORLDLIST_FETCH_REPLY transmits.)
 *   2. Else if exactly one world is registered, select it (the unambiguous common case).
 *   3. Else select the first world and WARN — multi-world component->world mapping needs live
 *      confirmation of the interface-906 row layout (see TODO).
 *
 * TODO(live): with >1 world, capture interface-906 row clicks against a populated worldlist and
 *   build the row-component -> world-index map (or switch the worldlist to a slot-indexed
 *   component so slotId carries the row index directly), then make step 1/2 authoritative.
 */
class IfButtonHandler : PacketHandler<GameSession, IfButton> {
    companion object {
        /** Lobby worldlist parent interface (the IF_SETTOPLEVELINTERFACE id). */
        private const val LOBBY_INTERFACE_ID = 906

        /** Sentinel meaning "no item / plain button" in the IF_BUTTON1 slot+item fields. */
        private const val NO_VALUE = 0xFFFF
    }

    override suspend fun handle(player: GameSession, packet: IfButton) {
        val interfaceId = packet.interfaceHash ushr 16
        val componentId = packet.interfaceHash and 0xFFFF
        logInfo("IfButton from ${player.ip}: interfaceId=$interfaceId componentId=$componentId slot=${packet.slotId} item=${packet.itemId} option=${packet.buttonId}")

        // Only clicks on the worldlist parent interface (906) can start a world transfer. Clicks on
        // child interfaces (friends 907, news, settings, ...) carry THEIR interface id, not 906, so
        // those are pure client-side navigation — log and return, never log into a world.
        if (interfaceId != LOBBY_INTERFACE_ID) return

        // A world ROW is a plain button press: no item attached (itemId == -1 / 0xFFFF). This filters
        // out any item-bearing interactions on interface 906 while accepting every row component
        // (32, 81, ...) without hardcoding a single one (the old WORLD_SELECT_COMPONENT==32 hack).
        val isPlainRowButton = packet.itemId == -1 || packet.itemId == NO_VALUE
        if (!isPlainRowButton) {
            logInfo("Ignoring non-row click on interface 906 (component=$componentId, item=${packet.itemId}) from ${player.ip}")
            return
        }

        // getWorldArray() is the SAME order the rows were sent to the client in op216
        // WORLDLIST_FETCH_REPLY (sorted by world id), so a row index maps directly into it.
        val worlds = LobbyState.worldList.getWorldArray()
        if (worlds.isEmpty()) {
            logInfo("Worldlist empty — cannot switch world for ${player.ip}")
            return
        }

        val target = when {
            packet.slotId in worlds.indices -> worlds[packet.slotId]
            worlds.size == 1 -> worlds[0]
            else -> {
                logWarn(
                    "World-row click from ${player.ip} could not be mapped to a specific world " +
                        "(component=$componentId, slot=${packet.slotId}; ${worlds.size} worlds registered). " +
                        "Interface-906 row->world mapping is client-side and unconfirmed — defaulting to the " +
                        "first world. See IfButtonHandler TODO(live)."
                )
                worlds[0]
            }
        }

        logInfo("World-row click -> switching ${player.ip} to world ${target.number} at ${target.hostname}:${target.port}")
        // op213 SWITCH_WORLD: worldId-first field order (binary-verified, handler 0x001aeba0),
        // port2 = the world port (client connects there), flag=1 marks a world target. This both
        // sets the WORLD-node target and triggers the reconnect — no op212 needed.
        player.send(
            SwitchWorld(
                hostname = target.hostname,
                worldId = target.number,
                port1 = target.port,
                port2 = target.port,
                pendingFlag = 1,
            )
        )
        player.flush()
    }
}
