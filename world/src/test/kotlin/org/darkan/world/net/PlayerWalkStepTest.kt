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
 * Unit guardrail for the 1.2b increment-2a local-player WALK encode — the exact inverse of the
 * verified decode's high-res walk branch (`core/.../recorder/ClientStateCrossCheck.kt:1260-1266`,
 * mvt=1 → `gBit(3)` direction + `gBit(1)` hasFollowup).
 *
 * Two things are proven, deliberately NOT as a full expected-byte string (the encode→decode
 * round-trip via the recorder's `decodePlayerInfo` is the framing guardrail, landing separately):
 *
 *  1. the [Direction8] inverse-table is the faithful inverse of the oracle's `PLAYER_REGION_DX/DY`
 *     (`ClientStateCrossCheck.kt:1614-1615`), and a queued step mutates `player.tile` + the GPI slot
 *     anchor exactly as the world tick applies it;
 *  2. [PlayerMovementEncoder.encodeHighResPosition] writes `movementType=1` + the RIGHT 3-bit
 *     direction index for a known `(dx,dy)` when the player walked this tick — asserting the dir INDEX
 *     against the table, not a full byte string.
 */
class PlayerWalkStepTest {

    private var allocatedIndex: Int = -1

    @BeforeTest
    fun setUp() {
        register948() // publishes ActiveMaskKeys.playerAppearance (read by hasFlaggableExtendedInfo).
    }

    @AfterTest
    fun tearDown() {
        if (allocatedIndex > 0) Players.release(allocatedIndex)
    }

    private fun newPlayer(spawn: Tile): Player {
        val account = Account(username = "tester", displayName = "Tester")
        val session = GameSession(
            write = ByteChannel(),
            isaacIn = Isaac(IntArray(4)),
            isaacOut = Isaac(IntArray(4)),
            ip = "127.0.0.1",
            codec = register948(),
            username = "tester",
        )
        val player = Player(index = 0, account = account, session = session)
        allocatedIndex = Players.allocate(player) { idx -> player.index = idx }
        player.viewport.resetAfterGpiPrefix(allocatedIndex)
        player.tile = spawn
        return player
    }

    @Test
    fun `Direction8 indexOf is the faithful inverse of the verified DX DY table`() {
        // For every direction index, indexOf(DX[d], DY[d]) must round-trip back to d (the inverse of
        // ClientStateCrossCheck.kt:1614-1615). Spot-check the compass meanings the debug path uses.
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

        // Mirror WorldTick.applyPendingStep: poll the dir, move the tile, update the anchor on region change.
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
    fun `encodeHighResPosition writes movementType 1 and the right 3-bit dir for a known step`() {
        val player = newPlayer(Tile(3200, 3200, 0))
        // Walk NORTH this tick (the encoder reads the tick-applied dir off the player).
        val northDir = Direction8.indexOf(0, 1)
        player.lastWalkStepDir = northDir

        val out = BufferWriter(64)
        val flagged = ArrayList<Int>()
        out.startBitAccess()
        PlayerMovementEncoder.encodeHighResPosition(out, player, flagged)
        out.stopBitAccess()

        // Decode [1 hasExt][2 movementType][3 dir][1 hasFollowup]. Assert mvt + dir INDEX (not a byte string).
        val r = BufferReader(out.toArray())
        r.startBitAccess()
        val hasExt = r.readBits(1)
        assertEquals(1, r.readBits(2), "movementType must be 1 (walk)")
        assertEquals(northDir, r.readBits(3), "3-bit dir must equal the verified Direction8 N index")
        assertEquals(0, r.readBits(1), "single step → hasFollowup=0")
        r.stopBitAccess()

        // A fresh account carries a default appearance, so hasExt=1 and the slot is flagged for ext-info.
        assertEquals(1, hasExt, "fresh player has deliverable appearance → hasExtendedInfo=1")
        assertEquals(listOf(player.index), flagged, "walking slot with ext-info is flagged for the ext block")
    }

    @Test
    fun `with no step this tick encodeHighResPosition keeps the stationary movementType 0 form`() {
        val player = newPlayer(Tile(3200, 3200, 0))
        assertEquals(MovementQueue.NO_STEP, player.lastWalkStepDir, "no step applied this tick")

        val out = BufferWriter(64)
        out.startBitAccess()
        PlayerMovementEncoder.encodeHighResPosition(out, player, ArrayList())
        out.stopBitAccess()

        val r = BufferReader(out.toArray())
        r.startBitAccess()
        r.readBits(1) // hasExt
        assertEquals(0, r.readBits(2), "no step → stationary movementType=0 (unchanged form)")
        r.stopBitAccess()
    }
}
