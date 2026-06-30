package org.darkan.world.server.packet

import org.darkan.core.Logger.logInfo
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.MoveGameClick
import org.darkan.core.net.prot.handler.PacketHandler
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.Player
import org.darkan.world.entity.RoutefinderStepProvider
import org.darkan.world.entity.StepProvider
import org.darkan.world.world.Players
import world.gregs.voidps.type.Tile

/**
 * op74 CLICK-TO-WALK handler (STAGE 2.2). The client sends a single absolute destination tile when the
 * player clicks the ground; this handler resolves the destination to a step sequence and enqueues it onto
 * the player's [org.darkan.world.entity.MovementQueue], which [org.darkan.world.server.WorldTick] drains
 * one tile/tick and the verified op22 GPI WALK encoder delivers to the client.
 *
 * ## Pluggable path generation (the stage 2.1 → 2.2 seam)
 *
 * The actual start→dest routing is delegated to a [StepProvider]. STAGE 2.2 uses the collision-aware
 * [RoutefinderStepProvider]. Nothing else in the handler, the op74 decoder, or the GPI encoder changes.
 * The provider is the entire swap point.
 *
 * ## Run modifier (the op74 ctrl-run bit)
 *
 * The modifier bit ([MoveGameClick.modifier], RE'd as `body[0] & 1` = `(modifierFlags>>2)&1`, a ctrl-
 * style click modifier — `re-resources/docs/kb/glossary/ghidra-packet-bindings.md` c2s op74) selects
 * RUN for this click: set on the click, the entity runs (2 tiles/tick) to the destination via the
 * verified op22 RUN forms ([org.darkan.world.net.PlayerMovementEncoder]); clear, it walks. The flag is
 * written onto [org.darkan.world.entity.Entity.running] and consumed by [org.darkan.world.server.WorldTick]'s
 * 2-tile run drain. (A persistent run-orb toggle is a later increment; for now the per-click modifier is
 * the run source.)
 */
class MoveGameClickHandler(
    private val stepProvider: StepProvider = RoutefinderStepProvider(),
) : PacketHandler<GameSession, MoveGameClick> {

    override suspend fun handle(player: GameSession, packet: MoveGameClick) {
        val entity = playerFor(player)
        if (entity == null) {
            logWarn("op74 MoveGameClick from ${player.username}@${player.ip} with no allocated Player; ignoring")
            return
        }

        val from = entity.tile
        // op74 carries only X/Z (absolute world tiles); the player stays on its current plane.
        val dest = Tile(packet.destX, packet.destZ, from.level)
        val steps = stepProvider.stepsTo(from, dest)

        // The ctrl-run modifier bit selects RUN (2 tiles/tick) for this click; consume it onto the
        // entity so WorldTick's run drain + the op22 RUN forms fire. A plain click clears it (walk).
        entity.running = packet.modifier != 0

        // A fresh click supersedes any in-progress walk: drop the queued tail before enqueueing the new
        // path so the avatar redirects to the latest destination instead of finishing the old one first.
        val queue = entity.movementQueue
        queue.clear()
        for (dir in steps) {
            queue.enqueueStep(Direction8.DX[dir], Direction8.DY[dir])
        }

        logInfo(
            "op74 click-to-${if (entity.running) "run" else "walk"} ${player.username}: " +
                "(${from.x},${from.y}) -> (${dest.x},${dest.y}) modifier=${packet.modifier} steps=${steps.size}"
        )
    }

    /** Resolve the world [Player] backing this [session] by identity match in the global pool. */
    private fun playerFor(session: GameSession): Player? {
        var found: Player? = null
        Players.forEach { if (it.session === session) found = it }
        return found
    }
}
