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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * DECODE-EQUALS round-trip acceptance for REMOTE-player high-res rendering in op22 PLAYER_INFO.
 *
 * Proves the low-res-add → high-res-promote transition the [PlayerInfoEncoder] now produces is the
 * exact inverse of the verified decode's `decodeExternalPlayerUpdate` branch 0
 * (`core/.../recorder/ClientStateCrossCheck.kt:1805-1822`): build the VIEWER's op22 through the REAL
 * encoder ([PlayerInfoEncoder.buildIfNeeded] / [PlayerInfoEncoder.buildWorldEntrySync]), feed every
 * frame in arrival order through the verified stateful recorder decode
 * ([PlayerInfoDecoder.DetailedSession] — the same fold the live oracle uses), and assert the REMOTE
 * shows up PRESENT in the viewer's render cohort at the correct tile, then takes a real walk step.
 *
 * This is the multiplayer counterpart to [PlayerWalkStateMachineTest] (which round-trips the LOCAL
 * walk) and the real-add upgrade of [PlayerInfoEncoderMultiPlayerTest] (which only hand-derives the
 * absent-cohort skip-runs and so could not catch a missing remote add).
 *
 * Two players spawn in the SAME region so the remote's live tile sits inside its op81-seeded low-res
 * region anchor (the ADD carries 6-bit local offsets relative to the anchor). The viewer is the only
 * one whose op22 we build/decode; the remote is plain world state the viewer's encoder reads.
 */
class PlayerInfoEncoderRemoteRenderTest {

    private val allocated = ArrayList<Int>()

    @BeforeTest
    fun setUp() {
        register948() // publishes ActiveMaskKeys.playerAppearance (read by the appearance synth).
    }

    @AfterTest
    fun tearDown() {
        allocated.forEach { Players.release(it) }
        allocated.clear()
    }

    private fun newPlayer(name: String, spawn: Tile): Player {
        val account = Account(username = name, displayName = name)
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = name,
        )
        val player = Player(index = 0, account = account, session = session)
        val idx = Players.allocate(player) { i -> player.index = i }
        allocated += idx
        player.tile = spawn
        // Seed the slot model from the prefix AFTER the tile is set + AFTER both players are allocated
        // (the prefix seeds each other slot's low-res anchor from the occupant's live tile).
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

    @Test
    fun `a second player in view is added to the viewer's render cohort at its tile and then walks`() {
        // Both in region (3200>>6, 3200>>6) = (50,50); remote 3 tiles east of the viewer — well inside the
        // render window. The remote's live tile (3203,3200) is inside its seeded anchor region (50,50), so
        // the branch-0 ADD's 6-bit local offsets (3,0) fit.
        val viewerSpawn = Tile(3200, 3200, 0)
        val remoteSpawn = Tile(3203, 3200, 0)
        val viewer = newPlayer("viewer", viewerSpawn)
        val remote = newPlayer("remote", remoteSpawn)
        val viewerIdx = viewer.index
        val remoteIdx = remote.index

        // Seed the viewer's slot model now that BOTH players exist, so the remote's slot gets its live
        // tile as the low-res anchor (Op81GpiPrefix / seedFromGpiPrefix read Players.get(idx).tile).
        viewer.viewport.resetAfterGpiPrefix(viewerIdx)

        // The op81 prefix the world server ships alongside the viewer's first op22 — the SAME prefix the
        // decode seeds from, with the remote's seeded anchor word carrying its region.
        val prefix = Op81GpiPrefix.build(spawnTile = viewerSpawn, localPlayerIndex = viewerIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, viewerIdx))

        // ---- Tick 1: world entry. The viewer's encoder should ADD the in-range remote (branch-0). ----
        val entry = viewer.viewport.let { PlayerInfoEncoder.buildWorldEntrySync(viewer) }
        val d1 = session.next(wireBody(entry))
        assertTrue(d1.decodeClean, "world-entry frame must decode cleanly (no skip-run desync)")

        // The remote is PRESENT in the viewer's decoded render cohort, at its exact spawn tile.
        val remoteState1 = assertNotNull(
            d1.scene.players[remoteIdx],
            "the in-range remote must be added to the viewer's render cohort on world entry",
        )
        val t1 = assertNotNull(remoteState1.tile, "the added remote must decode to a concrete tile")
        assertEquals(remoteSpawn.x, t1.x, "remote decoded X == its spawn X (region anchor 50<<6 + localX 3)")
        assertEquals(remoteSpawn.y, t1.y, "remote decoded Y == its spawn Y")
        assertEquals(remoteSpawn.level, t1.plane, "remote decoded plane == its spawn plane")

        // The viewer's own slot is present too (the local inline-appearance add), so this is a real
        // two-player render cohort, not just the remote.
        assertNotNull(d1.scene.players[viewerIdx], "the viewer's own slot is present in its render cohort")

        // ---- Tick 2: the remote walks one tile NORTH. The now-high-res remote takes a WALK-START. ----
        val north = Direction8.indexOf(0, 1)
        remote.tile = Tile(remote.tile.x, remote.tile.y + 1, remote.tile.level)
        remote.lastWalkStepDir = north
        val walk = viewer.viewport.let { PlayerInfoEncoder.buildIfNeeded(viewer) }
        remote.lastWalkStepDir = MovementQueue.NO_STEP // end-of-tick reset (WorldTick does this).

        val d2 = session.next(wireBody(walk))
        assertTrue(d2.decodeClean, "walk frame must decode cleanly")

        // The remote's per-frame movement is a high-res move-mode WALK-START (mvt=3) — it is now driven
        // by the SAME slot-generic walk state machine as the local slot.
        val move = assertNotNull(
            d2.scene.movements.singleOrNull { it.index == remoteIdx },
            "the promoted remote must produce a high-res movement record this tick",
        )
        assertEquals(3, move.movementType, "first remote walk tick is the WALK-START move-mode form (mvt=3)")
        assertEquals(0x8 ushr 2, move.descriptor, "WALK-START descriptor entry == (byteOffset 0x8)>>2 == 2 (WALK token a38)")

        // And its decoded tile advanced one north step from the spawn.
        val remoteState2 = assertNotNull(d2.scene.players[remoteIdx], "remote stays present after its walk step")
        val t2 = assertNotNull(remoteState2.tile)
        assertEquals(remoteSpawn.x, t2.x, "north walk keeps X")
        assertEquals(remoteSpawn.y + 1, t2.y, "north walk increments Y (RS y increases north)")
    }

    @Test
    fun `the remote walk drives off the viewer's own slot latch with no cross-viewer bleed`() {
        // A promoted remote walks for the VIEWER even though the remote's OWN viewport never processed
        // it — the wasWalking latch lives on the viewer's GpiSlot (viewer.viewport.playerSlots), so a
        // second step is a plain mvt=1 WALK-STEP (latch was set by the START), not another START.
        val viewerSpawn = Tile(3200, 3200, 0)
        val remoteSpawn = Tile(3202, 3200, 0)
        val viewer = newPlayer("viewer", viewerSpawn)
        val remote = newPlayer("remote", remoteSpawn)
        val viewerIdx = viewer.index
        val remoteIdx = remote.index
        viewer.viewport.resetAfterGpiPrefix(viewerIdx)

        val prefix = Op81GpiPrefix.build(spawnTile = viewerSpawn, localPlayerIndex = viewerIdx)
        val session = PlayerInfoDecoder.DetailedSession(PlayerInfoDecoder.GpiPrefix(prefix, viewerIdx))

        // Tick 1: world entry (adds the remote).
        session.next(wireBody(PlayerInfoEncoder.buildWorldEntrySync(viewer)))

        val north = Direction8.indexOf(0, 1)
        val mvts = ArrayList<Int?>()
        // Two consecutive north steps.
        repeat(2) {
            remote.tile = Tile(remote.tile.x, remote.tile.y + 1, remote.tile.level)
            remote.lastWalkStepDir = north
            val frame = PlayerInfoEncoder.buildIfNeeded(viewer)
            remote.lastWalkStepDir = MovementQueue.NO_STEP
            val d = session.next(wireBody(frame))
            assertTrue(d.decodeClean, "remote walk step ${it + 1} must decode cleanly")
            mvts += d.scene.movements.singleOrNull { m -> m.index == remoteIdx }?.movementType
        }
        // STOP tick: no step → WALK-STOP (mvt=3).
        val stopFrame = PlayerInfoEncoder.buildIfNeeded(viewer)
        val dStop = session.next(wireBody(stopFrame))
        val stopMove = dStop.scene.movements.singleOrNull { it.index == remoteIdx }

        assertEquals(3, mvts[0], "step 1 = WALK-START (mvt=3)")
        assertEquals(1, mvts[1], "step 2 = WALK-STEP (mvt=1) — the viewer's slot latch was set by the START")
        assertEquals(3, stopMove?.movementType, "the no-step tick after the walk is the WALK-STOP marker (mvt=3)")
    }
}
