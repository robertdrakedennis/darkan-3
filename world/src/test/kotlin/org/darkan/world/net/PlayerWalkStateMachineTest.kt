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
 * DECODE-EQUALS-PROD acceptance for the LOCAL op22 (PLAYER_INFO) walk state machine.
 *
 * Builds a multi-step LOCAL walk through the REAL encoder ([PlayerInfoEncoder.buildIfNeeded], driven
 * exactly as `WorldTick` does — set [Player.lastWalkStepDir] on each step tick, clear it on the stop
 * tick), feeds every produced op22 in arrival order through the verified recorder decoder
 * ([PlayerInfoDecoder.DetailedSession], the same stateful decode the live oracle uses), and asserts the
 * local slot's per-tick movement is the PROD shape:
 *
 *   WALK-START (`mvt=3` desc byte-offset 0x8) → WALK-STEP (`mvt=1`)×N → WALK-STOP (`mvt=3` desc 0x0)
 *
 * with the correct per-tick tile at every step — i.e. it re-decodes to the START/WALK/STOP form in
 * `:tools:walkExtInfo` over `session-20260630-033557-27478-production` (local idx 1160). This is the
 * encoder being the exact inverse of `decodeKnownPlayerUpdate` (`ClientStateCrossCheck.kt`).
 *
 * POSITION-ONLY is also asserted: no walk/stop tick carries a movement-anim (bit 0x20) or
 * forced-movement (bit 0x80) ext-info block on the local slot.
 */
class PlayerWalkStateMachineTest {

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
        val account = Account(username = "walker", displayName = "Walker")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "walker",
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
     * Drive [steps] one-tile walk steps (Direction8 indices) through the real encoder one tick at a time,
     * mirroring `WorldTick`: apply the step to [player.tile] + set lastWalkStepDir, build the op22, then
     * (at end of tick) clear lastWalkStepDir. After the last step, one more tick with NO step is the STOP.
     * Returns every op22 produced (world-entry frame first), in arrival order.
     */
    private fun runWalk(player: Player, steps: List<Int>): List<PlayerInfo> {
        val frames = ArrayList<PlayerInfo>()
        // Tick 0 — world entry (the inline-appearance add). firstTick cleared here.
        frames += PlayerInfoEncoder.buildWorldEntrySync(player)

        // One walk step per tick.
        for (dir in steps) {
            player.tile = Tile(player.tile.x + Direction8.DX[dir], player.tile.y + Direction8.DY[dir], player.tile.level)
            player.lastWalkStepDir = dir
            frames += PlayerInfoEncoder.buildIfNeeded(player)
            player.lastWalkStepDir = MovementQueue.NO_STEP // end-of-tick reset (WorldTick does this).
        }

        // STOP tick — no step queued; the encoder emits the WALK-STOP idle marker.
        frames += PlayerInfoEncoder.buildIfNeeded(player)
        return frames
    }

    @Test
    fun `a multi-step LOCAL walk re-decodes to the prod START WALK STOP shape with correct per-tick tiles`() {
        val spawn = Tile(3235, 3226, 0) // prod local op81 tile (session-20260630-033557-27478-production)
        val player = newPlayer(spawn)
        val localIdx = player.index

        // A 4-step SOUTH-ish walk (matches the prod first segment's southward run direction).
        val south = Direction8.indexOf(0, -1)
        val steps = listOf(south, south, south, south)
        val frames = runWalk(player, steps)

        // Decode every frame statefully from the op81 prefix the world server ships alongside.
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = localIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, localIdx))

        // Collect the local slot's per-frame movement + tile from the stateful decode.
        val mvts = ArrayList<Int?>()
        val descriptors = ArrayList<Int?>()
        val tiles = ArrayList<Pair<Int, Int>?>()
        for (info in frames) {
            val detailed = session.next(wireBody(info))
            assertTrue(detailed.decodeClean, "every frame must decode cleanly (no skip-run desync)")
            val move = detailed.scene.movements.singleOrNull { it.index == localIdx }
            mvts += move?.movementType
            descriptors += move?.descriptor
            val t = detailed.scene.players[localIdx]?.tile
            tiles += t?.let { it.x to it.y }
        }

        // Frame 0 = world entry: the local slot is the stationary inline-appearance hold (mvt=0).
        assertEquals(0, mvts[0], "world-entry frame: local slot is the stationary inline-appearance add (mvt=0)")

        // Frame 1 = WALK-START: mvt=3, descriptor byte-offset 0x8 → entry index 2 ((0x8>>2)). The decoder
        // exposes the descriptor as the entry index = ((code>>10)&0x1c)>>2.
        assertEquals(3, mvts[1], "first walk tick: WALK-START is mvt=3 (move-mode)")
        assertEquals(0x8 ushr 2, descriptors[1], "WALK-START descriptor entry == (byteOffset 0x8)>>2 == 2 (WALK token a38)")

        // Frames 2..N = WALK-STEP: plain mvt=1.
        for (f in 2..steps.size) {
            assertEquals(1, mvts[f], "walk tick $f: WALK-STEP is mvt=1")
        }

        // Final frame = WALK-STOP: mvt=3, descriptor byte-offset 0x0 → entry index 0 (IDLE token a30).
        val stopFrame = frames.size - 1
        assertEquals(3, mvts[stopFrame], "stop tick: WALK-STOP is mvt=3 (move-mode)")
        assertEquals(0, descriptors[stopFrame], "WALK-STOP descriptor entry == (byteOffset 0x0)>>2 == 0 (IDLE token a30)")

        // Per-tick tiles: the START applies the first step, each STEP applies its delta, the STOP leaves the
        // tile unchanged. So the decoded tile walks spawn → spawn+4 south, then holds.
        assertEquals(spawn.x to spawn.y, tiles[0], "world-entry tile == spawn")
        var expY = spawn.y
        for (f in 1..steps.size) {
            expY -= 1 // each south step decrements y (RS y increases north)
            assertEquals(spawn.x to expY, tiles[f], "after walk tick $f the decoded local tile is (spawn.x, $expY)")
        }
        assertEquals(spawn.x to expY, tiles[stopFrame], "STOP tick leaves the tile at the last step (no move)")
    }

    @Test
    fun `the LOCAL walk and stop ticks carry NO movement ext-info (position-only)`() {
        val spawn = Tile(3235, 3226, 0)
        val player = newPlayer(spawn)
        val localIdx = player.index
        val south = Direction8.indexOf(0, -1)
        val frames = runWalk(player, listOf(south, south, south))

        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = localIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, localIdx))

        // Frame 0 (world entry) legitimately carries the APPEARANCE block; the WALK/STOP frames (1..) must
        // carry NO ext-info for the local slot at all — and specifically no bit-0x20 / bit-0x80.
        frames.forEachIndexed { i, info ->
            val detailed = session.next(wireBody(info))
            val local = detailed.extInfo[localIdx]
            if (i == 0) return@forEachIndexed // world-entry appearance is expected.
            assertTrue(
                local == null,
                "walk/stop frame $i must ship NO ext-info block for the local slot (position-only), got $local",
            )
            assertEquals(0, info.extendedInfo.size, "walk/stop frame $i emits zero ext-info blocks")
        }
    }
}
