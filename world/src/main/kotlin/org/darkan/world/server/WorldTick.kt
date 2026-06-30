package org.darkan.world.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.darkan.core.EnvVars
import org.darkan.core.Logger.logError
import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.AntiCheatChallenge
import org.darkan.core.net.prot.DestroyZoneData
import org.darkan.core.net.prot.RebuildNormalSimple
import org.darkan.core.net.prot.TriggerOnDialogAbort
import org.darkan.world.net.NpcInfoEncoder
import org.darkan.world.net.PlayerInfoEncoder
import org.darkan.world.net.ZoneBundleBuilder
import org.darkan.world.net.ZoneStreamer
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.world.SceneBuildMode
import org.darkan.world.world.Npcs
import org.darkan.world.world.PlayerInfoSlots
import org.darkan.world.world.Players
import org.darkan.world.world.Zones
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import world.gregs.voidps.type.Tile

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

    /**
     * Parsed `DARKAN_DEBUG_WALK_PATH` (1.2b increment 2a) — the scripted local-player walk path as a
     * list of 3-bit [Direction8] indices, or empty when the env var is unset (the PROD default ⇒ no
     * scripted walk). Parsed ONCE at class init; an unknown compass token fails fast there.
     */
    private val debugWalkPath: List<Int> = parseDebugWalkPath(EnvVars.debugWalkPath)

    /**
     * Player indices already seeded with [debugWalkPath]. The scripted enqueue is a ONE-SHOT on the
     * first tick a player is [Players.forEach]-visible + [Player.readyForTick]; this set stops it
     * re-enqueuing every tick. (Empty + unused when [debugWalkPath] is empty.)
     */
    private val debugWalkSeeded: MutableSet<Int> = ConcurrentHashMap.newKeySet()

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

                // Movement (1.2b increment 2a — WALK only): seed the scripted debug path once, then
                // apply at most ONE queued one-tile step BEFORE building this player's op22, so the
                // walk bits the encoder emits reflect THIS tick's tile. No-op when no path is queued.
                maybeSeedDebugWalk(player)
                val stepped = applyPendingStep(player)
                if (stepped) {
                    maybeQueueSceneRebuild(player)
                }

                queueAntiCheatChallenge(player, now)

                // Zone bundle goes FIRST so the client has the zone-relative state set
                // up before PLAYER_INFO / NPC_INFO position deltas reference it.
                val zonePackets = ZoneBundleBuilder.build(player)
                for (packet in zonePackets) {
                    player.session.queuePacket(packet)
                }

                // Always emits an op22 each tick (stationary-hold when idle) — the prod "idle loop"
                // that re-commits the local avatar's smoothing each tick so a spawned, stationary
                // player STAYS at its tile (BUG-1 fix; see PlayerInfoEncoder.buildIfNeeded).
                player.session.queuePacket(PlayerInfoEncoder.buildIfNeeded(player))
                if (viewport.visibleNpcs.isNotEmpty()) {
                    player.session.queuePacket(NpcInfoEncoder.build(player))
                }

                // Per-tick scene-load-generation advance (op162). The NXT client's SceneLoadRegistry
                // gates appearance/scene compose on its generation counter advancing each tick
                // (handler @0x100084980 does `inc [registry+0xDBF0]`): a PlayerAppearancePending filed
                // at world entry only composes once the generation moves PAST the one it was filed in.
                // Prod emits this once per tick paired with PLAYER_INFO (op162 count == op22 count);
                // without it the local avatar's appearance never composes — no body/animation rig
                // (the walk-animation bug). The `TriggerOnDialogAbort` name reflects an incomplete RE;
                // its real per-tick job is the scene-load tick (see docs/net/serverprot/scene-load-registry-948.md).
                player.session.queuePacket(TriggerOnDialogAbort())
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
        // they must only be reset here, never inside a per-viewer build. The walk-step
        // marker is per-tick too: reset it so a player that did NOT step next tick emits the
        // stationary form (1.2b increment 2a).
        Players.forEach {
            if (it.readyForTick) {
                it.pendingUpdates.clear()
                it.lastWalkStepDir = MovementQueue.NO_STEP
            }
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

    // ---- Movement (1.2b increment 2a — local-player WALK only) -----------------------------------

    /**
     * ONE-SHOT scripted-walk seeding: the first tick [player] is ready, enqueue [debugWalkPath] onto
     * its [Player.movementQueue]. No-op when the path is empty (PROD default) or already seeded for
     * this index. This is the debug trigger that drives a known A→B local walk for a capture.
     */
    private fun maybeSeedDebugWalk(player: Player) {
        if (debugWalkPath.isEmpty()) return
        if (!debugWalkSeeded.add(player.index)) return
        for (dir in debugWalkPath) {
            player.movementQueue.enqueueStep(Direction8.DX[dir], Direction8.DY[dir])
        }
        logInfo("[debug-walk] enqueued ${debugWalkPath.size}-step path for ${player.account.username} (slot ${player.index})")
    }

    /**
     * Apply at most ONE pending one-tile step from [Player.movementQueue] this tick (the canonical RS
     * 1-tile/tick walk): poll the 3-bit [Direction8] index, move [Player.tile] by its `(dx,dy)`, update
     * the GPI slot's low-res region anchor via [PlayerInfoSlots.setCoord] when the region (`tile>>6`)
     * changed, and record the index in [Player.lastWalkStepDir] so the op22 encoder emits the WALK form
     * THIS tick. No-op (leaves `lastWalkStepDir == NO_STEP`) when nothing is queued.
     *
     * increment 2b: run (2 steps/tick), teleport, and client-input-driven steps build on this.
     */
    private fun applyPendingStep(player: Player): Boolean {
        if (!player.movementQueue.hasPendingStep()) return false
        val dir = player.movementQueue.pollStep()
        if (dir == MovementQueue.NO_STEP) return false

        val from = player.tile
        val to = Tile(from.x + Direction8.DX[dir], from.y + Direction8.DY[dir], from.level)
        player.tile = to
        player.lastWalkStepDir = dir

        // Keep the per-viewer GPI slot anchor coherent when the region changed — the slot model is the
        // viewer's own mirror of the client decode (mirrors the low-res region-move anchor update).
        if ((from.x shr 6) != (to.x shr 6) || (from.y shr 6) != (to.y shr 6)) {
            val slots = player.viewport.playerSlots
            slots.setCoord(
                player.index,
                PlayerInfoSlots.LowResCoord(to.level, to.x shr 6, to.y shr 6),
            )
        }
        return true
    }

    private fun maybeQueueSceneRebuild(player: Player) {
        if (!player.viewport.requiresSceneRebuild(player.tile)) return

        val plan = player.viewport.loadSceneBuild(player.tile, SceneBuildMode.Rebuild)
        player.viewport.visibleNpcs.clear()
        player.viewport.sentNpcAdds.clear()

        player.session.queuePacket(DestroyZoneData())
        player.session.queuePacket(
            RebuildNormalSimple(
                zoneX = plan.centreZoneX,
                zoneZ = plan.centreZoneY,
                packedCoordA = plan.buildArea.packedCoordA,
                packedCoordB = plan.buildArea.packedCoordB,
                npcInfoCoordBitWidth = NPC_INFO_COORD_BIT_WIDTH,
                sceneRootId = plan.worldAreaTypeId,
            )
        )
        for (packet in ZoneStreamer.buildPackets(plan)) {
            player.session.queuePacket(packet)
        }
        logInfo(
            "Scene rebuild queued for ${player.account.username}: " +
                "tile=${player.tile.x},${player.tile.y},${player.tile.level} " +
                "centreZone=${plan.centreZoneX},${plan.centreZoneY} buildArea=${plan.buildArea}"
        )
    }

    /**
     * Parse `DARKAN_DEBUG_WALK_PATH` into a list of 3-bit [Direction8] indices. Tokens are
     * comma-separated, case-insensitive, whitespace-trimmed compass directions (`N S E W NE NW SE SW`)
     * mapped to `(dx,dy)` with RS y increasing NORTH, then resolved via [Direction8.indexOf]. An empty
     * / blank string yields an empty list (scripted walk OFF). An unknown token throws so a typo fails
     * fast at startup instead of silently dropping a step.
     */
    private fun parseDebugWalkPath(raw: String): List<Int> {
        if (raw.isBlank()) return emptyList()
        return raw.split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { token ->
                val (dx, dy) = compassToDelta(token.uppercase())
                Direction8.indexOf(dx, dy)
            }
    }

    /** Compass token → `(dx,dy)` (RS y increases NORTH). Throws on an unknown token. */
    private fun compassToDelta(token: String): Pair<Int, Int> = when (token) {
        "N" -> 0 to 1
        "S" -> 0 to -1
        "E" -> 1 to 0
        "W" -> -1 to 0
        "NE" -> 1 to 1
        "NW" -> -1 to 1
        "SE" -> 1 to -1
        "SW" -> -1 to -1
        else -> throw IllegalArgumentException(
            "DARKAN_DEBUG_WALK_PATH: unknown compass token '$token' (expected one of N S E W NE NW SE SW)"
        )
    }

    private const val NPC_INFO_COORD_BIT_WIDTH = 7
}
