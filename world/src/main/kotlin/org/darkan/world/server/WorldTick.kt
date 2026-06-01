package org.darkan.world.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.world.net.NpcInfoBuilder
import org.darkan.world.net.PlayerInfoBuilder
import org.darkan.world.net.ZoneBundleBuilder
import org.darkan.world.world.Npcs
import org.darkan.world.world.Players
import org.darkan.world.world.Zones

/**
 * Game tick loop driving per-player PLAYER_INFO / NPC_INFO / zone-bundle dispatch.
 *
 * Runs on [Dispatchers.Default] at the canonical RS 600ms cadence. Each tick:
 *  1. For every [Players.forEach] entry, build a PlayerInfo / NpcInfo / zone bundle from
 *     world state and enqueue the packets on the player's session (via [org.darkan.core.net.Session.queuePacket],
 *     which is safe to call from this thread; the session loop drains and flushes them).
 *  2. After all players have been processed, clear every entity's pending-updates mask
 *     and the global [Zones.pending] queue so the next tick starts fresh.
 *
 * The loop uses an absolute `nextTickAt` accumulator (start-time + N × 600ms) so that if
 * any single tick runs long, the next sleep is shortened to catch up rather than drifting
 * forward in real time. After a stall longer than one tick interval the accumulator is
 * reset to "now" to avoid bursty catch-up storms.
 *
 * Per-player builder/encoder exceptions are caught and logged so one misbehaving player
 * never collapses the world tick — the rest of the world continues to tick normally.
 */
object WorldTick {
    /** Canonical RuneScape tick interval in milliseconds. */
    private const val TICK_INTERVAL_MS = 600L

    private val tickScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Volatile
    private var running = false

    /**
     * Idempotently start the tick loop. Safe to call multiple times — second and later
     * invocations are no-ops while [running] is true. The tick coroutine is owned by
     * [tickScope] and persists across [start]/[stop] cycles.
     */
    fun start() {
        if (running) return
        running = true
        logInfo("WorldTick starting (interval=${TICK_INTERVAL_MS}ms)")
        tickScope.launch {
            var nextTickAt = System.currentTimeMillis()
            while (running && isActive) {
                try {
                    runTick()
                } catch (e: Exception) {
                    logError("WorldTick error", e)
                }
                nextTickAt += TICK_INTERVAL_MS
                val sleep = nextTickAt - System.currentTimeMillis()
                if (sleep > 0) {
                    delay(sleep)
                } else {
                    // Tick ran longer than one interval — re-anchor to "now" so we
                    // don't burn CPU catching up on accumulated debt.
                    nextTickAt = System.currentTimeMillis()
                }
            }
            logInfo("WorldTick loop exited")
        }
    }

    /**
     * Stop the tick loop and cancel the in-flight tick (if any). Safe to call after
     * [start] has not been invoked.
     */
    fun stop() {
        if (!running) return
        running = false
        tickScope.coroutineContext.cancelChildren()
        logInfo("WorldTick stopped")
    }

    /**
     * Run one tick: build entity-sync packets for every player, queue them on the
     * player's session, then clear all pending state.
     */
    private fun runTick() {
        Players.forEach { player ->
            try {
                val viewport = player.viewport

                // Zone bundle goes FIRST so the client has the zone-relative state set
                // up before PLAYER_INFO / NPC_INFO position deltas reference it.
                val zonePackets = ZoneBundleBuilder.build(player)
                for (packet in zonePackets) {
                    player.session.queuePacket(packet)
                }

                // PLAYER_INFO and NPC_INFO use the init form on the first tick after
                // login (when the viewport's `firstTick` flag is still set) and the
                // per-tick incremental form thereafter. The init form is responsible
                // for clearing `firstTick`.
                val playerInfo = if (viewport.firstTick) {
                    PlayerInfoBuilder.buildInit(player)
                } else {
                    PlayerInfoBuilder.build(player)
                }
                val npcInfo = if (playerInfo.firstTick) {
                    NpcInfoBuilder.buildInit(player)
                } else {
                    NpcInfoBuilder.build(player)
                }

                player.session.queuePacket(playerInfo)
                player.session.queuePacket(npcInfo)
            } catch (e: Exception) {
                logError("Per-player tick failed: ${player.account.username}", e)
            }
        }

        // Clear all per-tick pending state. The order doesn't matter — pending updates
        // are owned by each entity and the global Zones queue is per-tick scratch.
        Players.forEach { it.pendingUpdates.clear() }
        Npcs.forEach { it.pendingUpdates.clear() }
        Zones.clear()
    }
}
