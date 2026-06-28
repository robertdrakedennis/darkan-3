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
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.AntiCheatChallenge
import org.darkan.world.net.NpcInfoBuilder
import org.darkan.world.net.PlayerInfoBuilder
import org.darkan.world.net.ZoneBundleBuilder
import org.darkan.world.entity.Player
import org.darkan.world.world.Npcs
import org.darkan.world.world.Players
import org.darkan.world.world.Zones
import java.security.SecureRandom

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
    private const val ANTI_CHEAT_INTERVAL_MS = 6_000L
    private const val ANTI_CHEAT_TIMEOUT_MS = 20_000L

    private val tickScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Separate IO-dispatched scope for outbound flushes. Per-player flushes are launched here and NOT
     * awaited, so a slow / backpressured client's socket write never stalls the tick thread or any
     * other player's sync (the previous serial `flushBlocking()` did exactly that — one stuck client
     * blocked the whole tick; see NETWORKING_AUDIT.md §Phase 1.1). Per-session ISAAC byte ordering is
     * still guaranteed by [org.darkan.core.net.Session.writeMutex] + the FIFO outbound queue, regardless
     * of how many flush coroutines a session has in flight. Cancelled on [stop].
     */
    private val flushScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val antiCheatRandom = SecureRandom()

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
        flushScope.coroutineContext.cancelChildren()
        logInfo("WorldTick stopped")
    }

    /**
     * Run one tick: build entity-sync packets for every player, queue them on the
     * player's session, then clear all pending state.
     */
    private fun runTick() {
        val now = System.currentTimeMillis()
        Players.forEach { player ->
            if (!player.readyForTick) return@forEach
            try {
                val viewport = player.viewport

                queueAntiCheatChallenge(player, now)

                // Zone bundle goes FIRST so the client has the zone-relative state set
                // up before PLAYER_INFO / NPC_INFO position deltas reference it.
                val zonePackets = ZoneBundleBuilder.build(player)
                for (packet in zonePackets) {
                    player.session.queuePacket(packet)
                }

                // Always emits an op22 each tick (stationary-hold when idle) — the prod "idle loop"
                // that re-commits the local avatar's smoothing each tick so a spawned, stationary
                // player STAYS at its tile (BUG-1 fix; see PlayerInfoBuilder.buildIfNeeded).
                player.session.queuePacket(PlayerInfoBuilder.buildIfNeeded(player))
                if (viewport.visibleNpcs.isNotEmpty()) {
                    player.session.queuePacket(NpcInfoBuilder.build(player))
                }
            } catch (e: Exception) {
                logError("Per-player tick failed: ${player.account.username}", e)
            }
        }

        // Deliver everything queued this tick. Each player's flush is dispatched CONCURRENTLY off the
        // tick thread onto [flushScope] (was: serial `flushBlocking()` per player, which made one
        // slow/backpressured client's socket write stall the ENTIRE tick and every other player's
        // sync — the headline scaling defect; see NETWORKING_AUDIT.md §Phase 1.1). The packets are
        // already materialised in the session's outbound queue by the build pass above, so a flush only
        // drains + writes; per-session ISAAC ordering is preserved by Session.writeMutex (concurrent
        // flushes of the SAME session serialise on it and drain the queue in FIFO order). A slow client
        // now only delays ITS OWN flush, never the tick. NOTE: the flushes are fire-and-forget — the
        // tick does not await them, and the pending-state clear below is safe because it clears build
        // INPUTS (update masks / zone queue) already consumed into the queued packets, not the queue.
        Players.forEach { player ->
            if (!player.readyForTick) return@forEach
            flushScope.launch {
                try {
                    player.session.flush()
                } catch (e: Exception) {
                    logError("Per-player tick flush failed: ${player.account.username}", e)
                }
            }
        }

        // Clear all per-tick pending state AFTER every viewer has been built — global
        // flags like Npc.spawned are read by all viewers' builders during the tick, so
        // they must only be reset here, never inside a per-viewer build.
        Players.forEach {
            if (it.readyForTick) it.pendingUpdates.clear()
        }
        Npcs.forEach {
            it.pendingUpdates.clear()
            it.spawned = false
        }
        Zones.clear()
    }

    private fun queueAntiCheatChallenge(player: Player, nowMs: Long) {
        val session = player.session
        if (session.isAntiCheatChallengeTimedOut(nowMs, ANTI_CHEAT_TIMEOUT_MS)) {
            logWarn("Anti-cheat challenge timed out for ${player.account.username}@${session.ip}")
            return
        }
        if (!session.canIssueAntiCheatChallenge(nowMs, ANTI_CHEAT_INTERVAL_MS)) return

        val challenge = AntiCheatChallenge(
            challengeA = antiCheatRandom.nextInt(),
            challengeB = antiCheatRandom.nextInt(),
        )
        session.markAntiCheatChallengePending(challenge, nowMs)
        session.queuePacket(challenge)
    }
}
