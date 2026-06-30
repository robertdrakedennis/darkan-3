package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.PlayerInfoDecoder
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * DECODE-EQUALS acceptance for the LOCAL op22 (PLAYER_INFO) RUN state machine — the RUN counterpart of
 * [PlayerWalkStateMachineTest].
 *
 * Builds a multi-tile LOCAL run through the REAL encoder ([PlayerInfoEncoder.buildIfNeeded], driven
 * exactly as `WorldTick` does — `running=true`, drain TWO queued one-tile steps/tick into one 2-tile RUN
 * delta via the real [MovementQueue.pollRunStep] + [PlayerMovementEncoder.runStepCode], and on an odd
 * last tile fall back to a single 1-tile WALK step), feeds every produced op22 in arrival order through
 * the verified recorder decoder ([PlayerInfoDecoder.DetailedSession]), and asserts the local slot's
 * per-tick movement is the binary-verified RUN shape:
 *
 *   run-START (`mvt=3` desc byte-offset 0xc) → RUN-STEP (`mvt=2`, correct runCode, +2 tiles)×N
 *     → run-STOP (`mvt=3` desc 0x0)
 *
 * plus an ODD-TAIL run whose last tile is a single `mvt=1` WALK step (the binary's run→walk handoff,
 * the runner slowing to 1 tile). The encoder being the EXACT inverse of `decodeKnownPlayerUpdate`
 * (`core/.../recorder/ClientStateCrossCheck.kt`) is what this proves — including the verified
 * `RUN_DX/RUN_DY` 2-tile run table.
 */
class PlayerRunStateMachineTest {

    private var allocatedIndex: Int = -1

    @BeforeTest
    fun setUp() {
        register948()
    }

    @AfterTest
    fun tearDown() {
        if (allocatedIndex > 0) Players.release(allocatedIndex)
        allocatedIndex = -1
    }

    private fun newPlayer(spawn: Tile): Player {
        val account = Account(username = "runner", displayName = "Runner")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "runner",
        )
        val player = Player(index = 0, account = account, session = session)
        allocatedIndex = Players.allocate(player) { idx -> player.index = idx }
        player.viewport.resetAfterGpiPrefix(allocatedIndex)
        player.tile = spawn
        return player
    }

    /** Reassemble the on-wire op22 body exactly as the rev948 codec serializes it (bitBlock + per-block [u16 len][bytes]). */
    private fun wireBody(info: PlayerInfo): ByteArray {
        val out = BufferWriter(8192)
        out.writeBytes(info.bitBlock)
        for (block in info.extendedInfo) {
            out.writeShort(block.size)
            out.writeBytes(block)
        }
        return out.toArray()
    }

    /**
     * Drive a RUN of [steps] one-tile [Direction8] steps through the real encoder one tick at a time,
     * mirroring `WorldTick.applyPendingStep` with `running=true`: each tick consume TWO queued steps as
     * one 2-tile run move (real [MovementQueue.pollRunStep] + [PlayerMovementEncoder.runStepCode] →
     * `lastRunDelta`), or a single 1-tile WALK step on the odd last tile (`lastWalkStepDir`). After the
     * path drains, one more tick with NO step is the STOP. Returns every op22 produced (world-entry first).
     */
    private fun runRun(player: Player, steps: List<Int>): List<PlayerInfo> {
        val frames = ArrayList<PlayerInfo>()
        frames += PlayerInfoEncoder.buildWorldEntrySync(player) // tick 0 — world entry (inline-appearance add).

        player.running = true
        for (dir in steps) player.movementQueue.enqueueStep(Direction8.DX[dir], Direction8.DY[dir])

        while (player.movementQueue.hasPendingStep()) {
            // Replicate WorldTick.applyPendingStep's run drain EXACTLY, exercising the real queue + lookup.
            val from = player.tile
            if (player.running && player.movementQueue.pendingCount() >= 2) {
                val run = player.movementQueue.pollRunStep()!!
                player.tile = Tile(from.x + run.dx, from.y + run.dy, from.level)
                player.lastRunDelta = PlayerMovementEncoder.runStepCode(run.dx, run.dy)
                player.lastWalkStepDir = run.firstStepDir
            } else {
                val d = player.movementQueue.pollStep()
                player.tile = Tile(from.x + Direction8.DX[d], from.y + Direction8.DY[d], from.level)
                player.lastWalkStepDir = d
            }
            frames += PlayerInfoEncoder.buildIfNeeded(player)
            // End-of-tick reset (WorldTick does this; `running` persists).
            player.lastWalkStepDir = MovementQueue.NO_STEP
            player.lastRunDelta = MovementQueue.NO_STEP
        }

        // STOP tick — nothing queued; the encoder emits the run-STOP idle marker.
        frames += PlayerInfoEncoder.buildIfNeeded(player)
        return frames
    }

    /** Decode every frame statefully from the op81 prefix; return the local slot's per-frame movement record. */
    private fun decodeLocal(
        frames: List<PlayerInfo>,
        spawn: Tile,
        localIdx: Int,
    ): List<DecodedTick> {
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = localIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, localIdx))
        val out = ArrayList<DecodedTick>()
        for (info in frames) {
            val detailed = session.next(wireBody(info))
            assertTrue(detailed.decodeClean, "every frame must decode cleanly (no skip-run desync)")
            val move = detailed.scene.movements.singleOrNull { it.index == localIdx }
            val t = detailed.scene.players[localIdx]?.tile
            out += DecodedTick(
                mvt = move?.movementType,
                descriptor = move?.descriptor,
                runCode = if (move?.movementType == 2) move.dir else null,
                tile = t?.let { it.x to it.y },
                tileExact = move?.tileExact,
            )
        }
        return out
    }

    private data class DecodedTick(
        val mvt: Int?,
        val descriptor: Int?,
        val runCode: Int?,
        val tile: Pair<Int, Int>?,
        val tileExact: Boolean?,
    )

    @Test
    fun `an even-length LOCAL run re-decodes to START(0xc) RUNSTEP(mvt2) x N STOP(0x0) with correct 2-tile tiles`() {
        val spawn = Tile(3220, 3218, 0) // the gold-capture running remote's run-start tile (slot 898 tick2).
        val player = newPlayer(spawn)
        val localIdx = player.index

        // 8 straight-EAST one-tile steps → 4 run ticks of code-8 (dx=+2,dy=0) each (the gold capture's run).
        val east = Direction8.indexOf(1, 0)
        val steps = List(8) { east }
        val frames = runRun(player, steps)
        val ticks = decodeLocal(frames, spawn, localIdx)

        // Frame 0 = world entry: stationary inline-appearance hold (mvt=0).
        assertEquals(0, ticks[0].mvt, "world-entry frame: local slot is the stationary inline-appearance add (mvt=0)")
        assertEquals(spawn.x to spawn.y, ticks[0].tile, "world-entry tile == spawn")

        // Frame 1 = run-START: mvt=3, descriptor byte-offset 0xc → entry index 3 ((0xc>>2)).
        assertEquals(3, ticks[1].mvt, "first run tick: run-START is mvt=3 (move-mode)")
        assertEquals(0xc ushr 2, ticks[1].descriptor, "run-START descriptor entry == (byteOffset 0xc)>>2 == 3 (RUN token a3c)")
        assertEquals(spawn.x + 2 to spawn.y, ticks[1].tile, "run-START moves the FULL 2 tiles east")

        // Frames 2..4 = RUN-STEP: mvt=2, runCode 8 (dx=+2,dy=0), +2 tiles/tick, tileExact.
        var expX = spawn.x + 2
        for (f in 2..4) {
            assertEquals(2, ticks[f].mvt, "run tick $f: RUN-STEP is mvt=2")
            assertEquals(8, ticks[f].runCode, "run tick $f: straight-east runCode == 8 (dx=+2,dy=0)")
            assertEquals(true, ticks[f].tileExact, "run tick $f: 2-tile run delta is byte-exact")
            expX += 2
            assertEquals(expX to spawn.y, ticks[f].tile, "run tick $f lands +2 east at ($expX,${spawn.y})")
        }

        // Final frame = run-STOP: mvt=3, descriptor byte-offset 0x0 (== walk-STOP idle token a30), no move.
        val stop = frames.size - 1
        assertEquals(5, frames.size - 1, "1 world-entry + 4 run ticks + 1 stop = 6 frames")
        assertEquals(3, ticks[stop].mvt, "stop tick: run-STOP is mvt=3 (move-mode)")
        assertEquals(0, ticks[stop].descriptor, "run-STOP descriptor entry == (byteOffset 0x0)>>2 == 0 (IDLE token a30)")
        assertEquals(expX to spawn.y, ticks[stop].tile, "run-STOP leaves the tile at the last run tile (no move)")
    }

    @Test
    fun `an ODD-length LOCAL run ends with a mvt=1 WALK step (the run to walk handoff) then STOP`() {
        val spawn = Tile(3220, 3218, 0)
        val player = newPlayer(spawn)
        val localIdx = player.index

        // 5 straight-EAST one-tile steps → run ticks code-8 ×2 (4 tiles), then a SINGLE odd-tail WALK step.
        val east = Direction8.indexOf(1, 0)
        val steps = List(5) { east }
        val frames = runRun(player, steps)
        val ticks = decodeLocal(frames, spawn, localIdx)

        // tick0=entry, tick1=run-START(+2), tick2=RUN-STEP(+2), tick3=WALK odd-tail(+1), tick4=run/walk-STOP.
        // 5 east steps drain as: run-START(2 tiles) + RUN-STEP(2 tiles) + WALK tail(1 tile) = 3 movement
        // ticks; plus the world-entry frame and the STOP frame ⇒ 5 frames total.
        assertEquals(5, frames.size, "entry + run-START + RUN-STEP + WALK tail + STOP = 5 frames")

        assertEquals(3, ticks[1].mvt, "run-START is mvt=3")
        assertEquals(0xc ushr 2, ticks[1].descriptor, "run-START descriptor entry 3 (byte offset 0xc)")
        assertEquals(spawn.x + 2 to spawn.y, ticks[1].tile, "run-START +2 east")

        assertEquals(2, ticks[2].mvt, "second run tick is RUN-STEP mvt=2")
        assertEquals(8, ticks[2].runCode, "RUN-STEP runCode 8 (east 2-tile)")
        assertEquals(spawn.x + 4 to spawn.y, ticks[2].tile, "RUN-STEP +2 east → +4 total")

        // Odd tail: a single 1-tile WALK step (mvt=1), the runner slowing to 1 tile.
        assertEquals(1, ticks[3].mvt, "odd-tail tick: a single 1-tile WALK step is mvt=1 (run→walk handoff)")
        assertEquals(spawn.x + 5 to spawn.y, ticks[3].tile, "odd-tail WALK step +1 east → +5 total")

        // STOP: mvt=3 idle marker (the handoff transferred the move-state to WALK, so this is a clean walk-STOP).
        val stop = frames.size - 1
        assertEquals(3, ticks[stop].mvt, "stop tick is mvt=3 (move-mode idle marker)")
        assertEquals(0, ticks[stop].descriptor, "STOP descriptor entry 0 (idle token a30)")
        assertEquals(spawn.x + 5 to spawn.y, ticks[stop].tile, "STOP leaves the tile at +5 (no move)")
    }

    @Test
    fun `a diagonal LOCAL run emits the correct perimeter runCodes`() {
        val spawn = Tile(3300, 3300, 0)
        val player = newPlayer(spawn)
        val localIdx = player.index

        // 4 NORTH-EAST one-tile steps → 2 run ticks of (dx=+2,dy=+2) == runCode 15 (the SE corner of the box).
        val ne = Direction8.indexOf(1, 1)
        val frames = runRun(player, List(4) { ne })
        val ticks = decodeLocal(frames, spawn, localIdx)

        // run-START carries the 2-tile NE delta as a signed-5 move-mode marker (byte offset 0xc).
        assertEquals(3, ticks[1].mvt, "run-START is mvt=3")
        assertEquals(0xc ushr 2, ticks[1].descriptor, "run-START byte offset 0xc")
        assertEquals(spawn.x + 2 to spawn.y + 2, ticks[1].tile, "run-START +2,+2 (NE 2-tile)")

        // RUN-STEP: runCode 15 == (dx=+2,dy=+2), the last perimeter entry.
        assertEquals(2, ticks[2].mvt, "RUN-STEP is mvt=2")
        assertEquals(15, ticks[2].runCode, "NE 2-tile delta (dx=+2,dy=+2) == runCode 15")
        assertEquals(spawn.x + 4 to spawn.y + 4, ticks[2].tile, "RUN-STEP +2,+2 → +4,+4 total")
    }
}
