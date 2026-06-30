package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Direction8
import org.darkan.world.entity.MovementQueue
import org.darkan.world.entity.Player
import org.darkan.world.world.PlayerInfoSlots
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit guardrail for the per-target WALK encode forms ([PlayerMovementEncoder.encodeHighResPosition]),
 * the leaf the orchestrator's walk state machine calls.
 *
 * **SERVER-DRIVEN WALK (2026-06-30 — the prod-decoded model):** prod forces the walk for the LOCAL
 * slot too. A decoded live-prod local walk (`session-20260630-033557-27478-production`, idx 1160) is
 * the three-phase move-mode shape, identical for any walker:
 *  * **WALK-START** (idle→walk): `mvt=3` small move-mode form, descriptor byte offset 0x8 (WALK token
 *    a38), carrying the first step's ±1-tile signed-5 delta — `code15 = (8<<10)|(xS5<<5)|yS5`.
 *  * **WALK-STEP** (walk→walk): plain `mvt=1` = `[3-bit dir][1-bit followup=0]`.
 *  * **WALK-STOP** (walk→idle): `mvt=3` small move-mode form, descriptor byte offset 0x0 (IDLE token
 *    a30), `code15 = 0`, no move.
 * POSITION-ONLY — no movement ext-info on any walk/stop tick.
 *
 * The earlier "local walk is client-predicted, server sends mvt=0/absent" premise (and the mvt=3
 * descriptor-entry-4 SMOOTH form) was FALSIFIED: the prior tools mis-identified the local slot as a
 * remote player. The orchestrator-level state machine (latch → phase) is covered by
 * [PlayerWalkStateMachineTest]; here we pin the leaf forms byte-for-byte against the prod decode.
 */
class PlayerWalkStepTest {

    private val allocatedIndices = ArrayList<Int>()

    @BeforeTest
    fun setUp() {
        register948() // publishes ActiveMaskKeys.playerAppearance (read by hasFlaggableExtendedInfo).
    }

    @AfterTest
    fun tearDown() {
        for (idx in allocatedIndices) Players.release(idx)
        allocatedIndices.clear()
    }

    private fun newPlayer(spawn: Tile, name: String = "tester"): Player {
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
        allocatedIndices += idx
        player.viewport.resetAfterGpiPrefix(idx)
        player.tile = spawn
        return player
    }

    private fun signed5(v: Int): Int = if (v < 0x10) v else v - 0x20

    /** Encode one high-res form and return `(hasExt, mvt, large?, code15?, dir?, followup?)` fields. */
    private data class HighRes(
        val hasExt: Int,
        val mvt: Int,
        val large: Int? = null,
        val code15: Int? = null,
        val dir: Int? = null,
        val followup: Int? = null,
    )

    private fun encode(
        viewer: Player,
        target: Player,
        phase: PlayerMovementEncoder.WalkPhase,
        flagged: MutableList<Int> = ArrayList(),
    ): HighRes {
        val out = BufferWriter(64)
        out.startBitAccess()
        PlayerMovementEncoder.encodeHighResPosition(out, viewer, target, phase, flagged)
        out.stopBitAccess()
        val r = BufferReader(out.toArray())
        r.startBitAccess()
        val hasExt = r.readBits(1)
        val mvt = r.readBits(2)
        return when (mvt) {
            1 -> {
                val dir = r.readBits(3)
                val followup = r.readBits(1)
                r.stopBitAccess()
                HighRes(hasExt, mvt, dir = dir, followup = followup)
            }
            3 -> {
                val large = r.readBits(1)
                val code15 = r.readBits(15)
                r.stopBitAccess()
                HighRes(hasExt, mvt, large = large, code15 = code15)
            }
            else -> {
                r.stopBitAccess()
                HighRes(hasExt, mvt)
            }
        }
    }

    @Test
    fun `Direction8 indexOf is the faithful inverse of the verified DX DY table`() {
        for (d in Direction8.DX.indices) {
            assertEquals(d, Direction8.indexOf(Direction8.DX[d], Direction8.DY[d]), "round-trip index $d")
        }
        assertEquals(4, Direction8.indexOf(1, 0), "E = (1,0) → index 4")
        assertEquals(6, Direction8.indexOf(0, 1), "N = (0,1) → index 6 (RS y increases NORTH)")
        assertEquals(3, Direction8.indexOf(-1, 0), "W = (-1,0) → index 3")
        assertEquals(1, Direction8.indexOf(0, -1), "S = (0,-1) → index 1")
        assertEquals(7, Direction8.indexOf(1, 1), "NE = (1,1) → index 7")
    }

    @Test
    fun `a queued step mutates player tile and the GPI slot anchor like the tick applies it`() {
        // Spawn at a region boundary so a one-tile EAST step crosses into the next region (tile>>6),
        // exercising the GpiSlot.coord anchor update the tick performs.
        val spawn = Tile(3263, 3200, 0) // x=3263 → region 50; x=3264 → region 51
        val player = newPlayer(spawn)
        val slots = player.viewport.playerSlots

        player.movementQueue.enqueueStep(1, 0) // E
        assertTrue(player.movementQueue.hasPendingStep(), "step is queued")

        val dir = player.movementQueue.pollStep()
        assertEquals(Direction8.indexOf(1, 0), dir, "polled dir is the E index")
        val from = player.tile
        val to = Tile(from.x + Direction8.DX[dir], from.y + Direction8.DY[dir], from.level)
        player.tile = to
        if ((from.x shr 6) != (to.x shr 6) || (from.y shr 6) != (to.y shr 6)) {
            slots.setCoord(player.index, PlayerInfoSlots.LowResCoord(to.level, to.x shr 6, to.y shr 6))
        }

        assertEquals(3264, player.tile.x, "tile moved one tile east")
        assertEquals(3200, player.tile.y, "y unchanged")
        assertFalse(player.movementQueue.hasPendingStep(), "queue drained after the single step")

        val coord = slots.slot(player.index)!!.coord
        assertEquals(51, coord.regionX, "GPI slot region X advanced to 51 (tile>>6) after crossing the boundary")
        assertEquals(50, coord.regionY, "GPI slot region Y unchanged")
        assertEquals(0, coord.plane)
    }

    @Test
    fun `WALK-START writes the prod mvt3 desc 0x8 small form carrying the step delta`() {
        // The idle->walk marker: mvt=3, small form, descriptor byte offset 0x8 (WALK token a38), with the
        // first step's ±1-tile signed-5 delta. Byte-for-byte against the prod local walk decode.
        val viewer = newPlayer(Tile(3200, 3200, 0), name = "viewer")
        viewer.lastWalkStepDir = Direction8.indexOf(0, -1) // SOUTH

        val r = encode(viewer, viewer, PlayerMovementEncoder.WalkPhase.START)
        assertEquals(3, r.mvt, "WALK-START: movementType=3 (move-mode)")
        assertEquals(0, r.large, "small (15-bit) form")
        // SOUTH start == prod tick11/tick33 code15 0x201f.
        assertEquals(0x201f, r.code15, "SOUTH WALK-START == prod code15 0x201f (field 8 | yS5=-1)")
        assertEquals(0x8, (r.code15!! ushr 10) and 0x1c, "descriptor byte offset must be 0x8 (WALK token a38)")
        assertEquals(0, (r.code15 ushr 10) and 0x3, "plane delta must be 0")
        assertEquals(0, signed5((r.code15 ushr 5) and 0x1f), "X delta = 0 (due south)")
        assertEquals(-1, signed5(r.code15 and 0x1f), "Y delta = -1 tile (south)")
    }

    @Test
    fun `WALK-START code15 matches the prod-captured walk-start steps byte-for-byte`() {
        // Ground-truth from session-20260630-033557-27478-production (local idx 1160):
        //   tick11/33 SOUTH (DX=0,DY=-1) -> code15 = 0x201f ; tick20/58 NORTH (DX=0,DY=+1) -> 0x2001.
        val viewer = newPlayer(Tile(3200, 3200, 0), name = "viewer")
        fun code15For(dir: Int): Int {
            viewer.lastWalkStepDir = dir
            return encode(viewer, viewer, PlayerMovementEncoder.WalkPhase.START).code15!!
        }
        assertEquals(0x201f, code15For(Direction8.indexOf(0, -1)), "SOUTH start == prod 0x201f")
        assertEquals(0x2001, code15For(Direction8.indexOf(0, 1)), "NORTH start == prod 0x2001")
        assertEquals(0x2020, code15For(Direction8.indexOf(1, 0)), "EAST start == 0x2020 (field 8 | +1 tile X)")
        assertEquals(0x23e0, code15For(Direction8.indexOf(-1, 0)), "WEST start == 0x23e0 (field 8 | -1 tile X)")
    }

    @Test
    fun `WALK-STEP writes the plain mvt1 dir-followup form`() {
        // The middle steps: mvt=1, [3-bit dir][1-bit followup=0]. The dir is the verified Direction8 index.
        val viewer = newPlayer(Tile(3200, 3200, 0), name = "viewer")
        val eastDir = Direction8.indexOf(1, 0)
        viewer.lastWalkStepDir = eastDir

        val r = encode(viewer, viewer, PlayerMovementEncoder.WalkPhase.STEP)
        assertEquals(1, r.mvt, "WALK-STEP: movementType=1 (walk)")
        assertEquals(eastDir, r.dir, "dir is the EAST Direction8 index")
        assertEquals(0, r.followup, "single one-tile step → hasFollowup=0")
    }

    @Test
    fun `WALK-STOP writes the prod mvt3 desc 0x0 idle marker with code15 0`() {
        // The walk->idle marker: mvt=3, small form, descriptor byte offset 0x0 (IDLE token a30), code15=0,
        // no move. == prod ticks 13/26/48/69.
        val viewer = newPlayer(Tile(3200, 3200, 0), name = "viewer")
        // STOP does not read lastWalkStepDir (no move) — leave it NO_STEP.

        val r = encode(viewer, viewer, PlayerMovementEncoder.WalkPhase.STOP)
        assertEquals(3, r.mvt, "WALK-STOP: movementType=3 (move-mode)")
        assertEquals(0, r.large, "small (15-bit) form")
        assertEquals(0x0, r.code15, "WALK-STOP code15 == 0 (IDLE token a30, no move)")
        assertEquals(0x0, (r.code15!! ushr 10) and 0x1c, "descriptor byte offset must be 0x0 (IDLE token a30)")
    }

    @Test
    fun `WalkPhase NONE keeps the stationary movementType 0 form`() {
        val player = newPlayer(Tile(3200, 3200, 0))
        assertEquals(MovementQueue.NO_STEP, player.lastWalkStepDir, "no step applied this tick")
        // Mark the appearance delivered so NONE is the bare hasExt=0 stationary hold (no inline appearance).
        player.viewport.cachedApprHashes[player.index] = player.appearance.cachedBytes

        val r = encode(player, player, PlayerMovementEncoder.WalkPhase.NONE)
        assertEquals(0, r.mvt, "NONE → stationary movementType=0 (unchanged form)")
        assertEquals(0, r.hasExt, "appearance delivered → no ext-info on a stationary hold")
    }

    @Test
    fun `the walk forms carry no movement ext-info (position-only) once appearance is delivered`() {
        // POSITION-ONLY: a walk/start/step/stop tick must not flag the slot for an ext-info block once the
        // appearance is delivered (prod's local slot carried 0 movement-anim / forced-movement bits).
        val viewer = newPlayer(Tile(3200, 3200, 0))
        viewer.viewport.cachedApprHashes[viewer.index] = viewer.appearance.cachedBytes
        viewer.lastWalkStepDir = Direction8.indexOf(1, 0)

        for (phase in listOf(
            PlayerMovementEncoder.WalkPhase.START,
            PlayerMovementEncoder.WalkPhase.STEP,
            PlayerMovementEncoder.WalkPhase.STOP,
        )) {
            val flagged = ArrayList<Int>()
            encode(viewer, viewer, phase, flagged)
            assertTrue(flagged.isEmpty(), "$phase must not flag an ext-info block (position-only)")
        }
    }
}
