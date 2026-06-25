package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit regression for [PlayerInfoBuilder.buildInit] (docs/protocol/world-bootstrap-948.md §4.3).
 *
 * Proves `buildInit` generates a per-tick-shaped GPI init from local state with the local player's
 * 30-bit tile == its spawn tile, routed through the teleport/absolute high-res path
 * `[hasUpdate=1][hasExtInfo][movementType=3][30-bit tile]`.
 *
 * NOTE on wiring: as of the Shape-B fix, `buildInit` is NO LONGER sent at world entry — op81's
 * generated GPI prefix ([Op81GpiPrefix]) IS the world-entry GPI, and its prefix parser reads the
 * local tile as a DIRECT `gBit(30)` (no 4-bit header), which is INCOMPATIBLE with `buildInit`'s
 * per-tick framing. `buildInit` is kept intact for later per-tick use and is tested here in
 * isolation; the world-entry op81 body / prefix coherence is covered by [Op81GpiPrefixTest]. The
 * 30-bit tile == spawn tile invariant proven here is the same coherence the prefix must also honour
 * (both derive from `player.tile`).
 */
class PlayerInfoBuilderInitTest {

    private var allocatedIndex: Int = -1

    @BeforeTest
    fun setUp() {
        // Wire the active codec so ActiveMaskKeys.playerAppearance is published (buildInit reads it).
        register948()
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
    fun `buildInit local GPI tile equals the spawn tile and agrees with the op81 centre zone`() {
        val spawn = Tile(3200, 3200, 0)            // Lumbridge → zone (400, 400)
        val player = newPlayer(spawn)

        val info = PlayerInfoBuilder.buildInit(player)

        // Decode the local-player high-res init bits: [1 hasUpdate][1 hasExtInfo][2 movementType][30 tile].
        val r = BufferReader(info.bitBlock)
        r.startBitAccess()
        assertEquals(1, r.readBits(1), "local player must have an update (hasUpdate=1)")
        val hasExtInfo = r.readBits(1)
        assertEquals(3, r.readBits(2), "movementType must be 3 (teleport → absolute tile)")
        val tileId = r.readBits(30)
        r.stopBitAccess()

        assertEquals(spawn.id, tileId, "GPI local 30-bit tile must equal the player's spawn tile")

        // The op81 coord-header centre zone WorldServer would send, derived from the SAME tile.
        val centreZoneX = spawn.x shr 3
        val centreZoneZ = spawn.y shr 3
        assertEquals(400, centreZoneX)
        assertEquals(400, centreZoneZ)
        // The GPI tile's zone must equal that op81 centre zone — the coherence the fix guarantees.
        assertEquals(centreZoneX, Tile.x(tileId) shr 3, "GPI tile zone X == op81 centre zone X")
        assertEquals(centreZoneZ, Tile.y(tileId) shr 3, "GPI tile zone Z == op81 centre zone Z")

        assertEquals(1, hasExtInfo, "fresh account has a default appearance payload")
        assertEquals(1, info.extendedInfo.size, "default appearance emits one ext-info block")
        assertTrue(info.firstTick, "buildInit marks PlayerInfo.firstTick = true")
    }

    @Test
    fun `tick-1 emits per-tick appearance sync once firstTick is cleared`() {
        val player = newPlayer(Tile(3200, 3200, 0))

        // Sanity: with firstTick still set (the un-suppressed default), tick 1 WOULD emit an init op22.
        assertTrue(player.viewport.firstTick, "fresh viewport starts with firstTick == true")
        val withInit = PlayerInfoBuilder.buildIfNeeded(player)
        assertNotNull(withInit, "with firstTick set, buildIfNeeded returns the GPI init (would be op22)")

        player.viewport.cachedApprHashes[player.index] = null
        player.viewport.firstTick = false
        val sync = PlayerInfoBuilder.buildIfNeeded(player)
        assertNotNull(sync, "with firstTick cleared, undelivered appearance emits per-tick op22")
        assertEquals(1, sync.extendedInfo.size)

        val afterAppearance = PlayerInfoBuilder.buildIfNeeded(player)
        assertNull(afterAppearance, "after appearance delivery, no-op tick emits no PlayerInfo")
    }

    @Test
    fun `world-entry sync starts with production local appearance shape`() {
        val player = newPlayer(Tile(3200, 3200, 0))

        val info = PlayerInfoBuilder.buildWorldEntrySync(player)

        assertContentEquals(byteArrayOf(0xC7.toByte(), 0xFF.toByte(), 0x40), info.bitBlock)
        assertEquals(1, info.extendedInfo.size)
        assertTrue(!info.firstTick)
        assertTrue(!player.viewport.firstTick)
    }

    @Test
    fun `buildInit local GPI tile tracks a non-default spawn tile`() {
        // Verify the tile is genuinely read from player.tile, not a hardcoded Lumbridge constant.
        val spawn = Tile(2440, 3090, 0)            // Falador-ish → zone (305, 386)
        val player = newPlayer(spawn)

        val info = PlayerInfoBuilder.buildInit(player)

        val r = BufferReader(info.bitBlock)
        r.startBitAccess()
        r.readBits(1); r.readBits(1); r.readBits(2)
        val tileId = r.readBits(30)
        r.stopBitAccess()

        assertEquals(spawn.id, tileId, "GPI local tile must follow player.tile for a non-default spawn")
        assertEquals(305, Tile.x(tileId) shr 3)
        assertEquals(386, Tile.y(tileId) shr 3)
    }
}
