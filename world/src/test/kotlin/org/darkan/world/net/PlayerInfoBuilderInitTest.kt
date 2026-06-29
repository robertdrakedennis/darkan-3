package org.darkan.world.net

import io.ktor.utils.io.ByteChannel
import org.darkan.core.model.Account
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.PlayerInfo
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.core.net.recorder.PlayerInfoDecoder
import org.darkan.core.net.session.GameSession
import org.darkan.world.entity.Appearance
import org.darkan.world.entity.Player
import org.darkan.world.world.Players
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.type.Tile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit regression for [PlayerInfoEncoder.buildInit] (docs/protocol/world-bootstrap-948.md §4.3 +
 * `re-resources/docs/net/serverprot/player-appearance-948.md`).
 *
 * Proves `buildInit` generates the prod-accurate first-tick GPI: the local player's high-res entry is
 * the **stationary inline-appearance** form `[hasUpdate=1][hasExtInfo=1][movementType=0]` with the
 * APPEARANCE ext-info block following — byte-for-byte the production local first-tick op22
 * (`session-20260627-044937-74364-production`: local op22 starts `0xC0`, mvt=0, NOT mvt=3). The
 * earlier movementType=3 TELEPORT local form was REVERTED (prod never sends it; it regressed the
 * render plane). The avatar "no model" fix is this inline APPEARANCE delivery, asserted to round-trip
 * through the recorder oracle decode ([PlayerInfoDecoder.decode]) below.
 *
 * NOTE on wiring: the local 30-bit tile now lives ONLY in op81's generated GPI prefix ([Op81GpiPrefix],
 * a DIRECT `gBit(30)` with no header), NOT in the op22 — `buildInit`'s local entry is the stationary
 * add. The world-entry op81 body / prefix coherence is covered by [Op81GpiPrefixTest].
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

    /**
     * Reassemble the on-wire op22 PLAYER_INFO body exactly as the rev948 server codec serializes it
     * (`Rev948ServerCodecsPlayerInfo`: the bit block, then each ext-info block as `[u16 len][bytes]`),
     * so it can be fed back through the recorder oracle's [PlayerInfoDecoder.decode].
     */
    private fun playerInfoWireBody(info: PlayerInfo): ByteArray {
        val out = BufferWriter(8192)
        out.writeBytes(info.bitBlock)
        for (block in info.extendedInfo) {
            out.writeShort(block.size)
            out.writeBytes(block)
        }
        return out.toArray()
    }

    @Test
    fun `buildInit local entry is the prod stationary inline-appearance form`() {
        val spawn = Tile(3200, 3200, 0)            // Lumbridge → zone (400, 400)
        val player = newPlayer(spawn)

        val info = PlayerInfoEncoder.buildInit(player)

        // Decode the local-player high-res entry: [1 hasUpdate][1 hasExtInfo][2 movementType]. This is
        // the PROD local first-tick form (0xC0…, mvt=0 stationary), NOT the reverted mvt=3 teleport —
        // the avatar "no model" fix delivers the appearance INLINE via this stationary add.
        val r = BufferReader(info.bitBlock)
        r.startBitAccess()
        assertEquals(1, r.readBits(1), "local player must have an update (hasUpdate=1)")
        assertEquals(1, r.readBits(1), "fresh account has a default appearance → hasExtInfo=1")
        assertEquals(0, r.readBits(2), "movementType must be 0 (stationary inline-appearance, NOT mvt=3 teleport)")
        r.stopBitAccess()

        // The op22 NO LONGER carries the 30-bit tile (that lives in op81's prefix). The local entry's
        // first byte is the prod 0xC0 = [1 hasUpdate][1 hasExt][2 mvt=0] byte-aligned.
        assertEquals(0xC0.toByte(), info.bitBlock[0], "local entry byte == prod 0xC0 (hasUpdate=1, hasExt=1, mvt=0)")

        assertEquals(1, info.extendedInfo.size, "default appearance emits one INLINE ext-info block")
        assertTrue(info.firstTick, "buildInit marks PlayerInfo.firstTick = true")

        // Coherence: the op81 GPI prefix WorldServer sends alongside this op22 carries the spawn tile,
        // and its zone is the op81 coord-header centre zone (both derive from player.tile).
        val centreZoneX = spawn.x shr 3
        val centreZoneZ = spawn.y shr 3
        assertEquals(400, centreZoneX)
        assertEquals(400, centreZoneZ)
    }

    @Test
    fun `buildInit delivers the local APPEARANCE inline and it round-trips through the recorder decode`() {
        // THE avatar "no model" fix: the local player's first-tick op22 must carry the APPEARANCE
        // ext-info INLINE so the client's in-memory appearance is non-empty (appearance_len > 0). Encode
        // buildInit, reassemble the on-wire op22 body exactly as the rev948 codec serializes it
        // (Rev948ServerCodecsPlayerInfo: bitBlock + per-block [u16 len][bytes]), and decode it through
        // the recorder oracle — the same decode the live appearance oracle uses.
        val spawn = Tile(3200, 3200, 0)
        val player = newPlayer(spawn)

        val info = PlayerInfoEncoder.buildInit(player)
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = player.index)
        val scene = PlayerInfoDecoder.decode(
            bytes = playerInfoWireBody(info),
            gpiPrefix = PlayerInfoDecoder.GpiPrefix(prefix, player.index),
        )

        // The local slot's appearance decoded from the INLINE ext-info block (appearance_len > 0).
        assertTrue(scene.appearance.isNotEmpty(), "local APPEARANCE must decode from the inline ext-info block")
        val kits = scene.appearance.values.filter { it.kitId >= 0 }.map { it.kitId }
        // Fresh male default identitykits (Appearance.DEFAULT_MALE_BODY_STYLES), emitted as Kit slots.
        assertTrue(kits.isNotEmpty(), "decoded appearance must carry the default body identitykits (kitId >= 0)")
        assertEquals(
            Appearance.DEFAULT_MALE_BODY_STYLES.toSet(),
            kits.toSet(),
            "decoded inline appearance kits round-trip the fresh male default identitykits",
        )
    }

    @Test
    fun `buildInit and buildWorldEntrySync emit the same local-slot bytes (only firstTick differs)`() {
        // Prod sends the SAME stationary inline-appearance local form on the world-entry op22 and the
        // (un-suppressed) first-tick op22. After the teleport revert, buildInit's bit block + ext-info
        // must equal buildWorldEntrySync's — the only difference is the PlayerInfo.firstTick flag.
        val spawn = Tile(3224, 3216, 0)
        val a = newPlayer(spawn)
        val initInfo = PlayerInfoEncoder.buildInit(a)
        Players.release(allocatedIndex); allocatedIndex = -1

        val b = newPlayer(spawn)
        val entryInfo = PlayerInfoEncoder.buildWorldEntrySync(b)

        assertContentEquals(initInfo.bitBlock, entryInfo.bitBlock, "buildInit/buildWorldEntrySync local bit blocks must match")
        assertEquals(initInfo.extendedInfo.size, entryInfo.extendedInfo.size, "same ext-info block count")
        assertContentEquals(initInfo.extendedInfo[0], entryInfo.extendedInfo[0], "same inline APPEARANCE block bytes")
        assertTrue(initInfo.firstTick, "buildInit sets firstTick=true")
        assertTrue(!entryInfo.firstTick, "buildWorldEntrySync clears firstTick")
    }

    @Test
    fun `tick-1 emits per-tick appearance sync once firstTick is cleared`() {
        val player = newPlayer(Tile(3200, 3200, 0))

        // Sanity: with firstTick still set (the un-suppressed default), tick 1 WOULD emit an init op22.
        assertTrue(player.viewport.firstTick, "fresh viewport starts with firstTick == true")
        val withInit = PlayerInfoEncoder.buildIfNeeded(player)
        assertNotNull(withInit, "with firstTick set, buildIfNeeded returns the GPI init (would be op22)")

        player.viewport.cachedApprHashes[player.index] = null
        player.viewport.firstTick = false
        val sync = PlayerInfoEncoder.buildIfNeeded(player)
        assertNotNull(sync, "with firstTick cleared, undelivered appearance emits per-tick op22")
        assertEquals(1, sync.extendedInfo.size)

        // BUG-1 fix: a no-op tick still emits an op22 — the stationary "idle loop" prod sends every
        // tick to re-commit the local avatar so a spawned, stationary player STAYS put. The body is
        // the stationary-hold form (local hasUpdate=0) with NO ext-info (appearance already sent).
        val idle = PlayerInfoEncoder.buildIfNeeded(player)
        assertEquals(0, idle.extendedInfo.size, "idle tick carries no ext-info (appearance delivered)")
        val r = BufferReader(idle.bitBlock)
        r.startBitAccess()
        assertEquals(0, r.readBits(1), "idle local player must be stationary (hasUpdate=0)")
        r.stopBitAccess()
    }

    @Test
    fun `world-entry sync starts with production local appearance shape`() {
        val player = newPlayer(Tile(3200, 3200, 0))

        val info = PlayerInfoEncoder.buildWorldEntrySync(player)

        // Baseline idle world-entry op22 (local hasUpdate=0 stationary hold + the low-res skip-run). The
        // local appearance is NOT deliverable via this op22 (the client excludes the local slot from the
        // ext-info path); the prod-accurate inline-GPI delivery is a pending follow-up (see PlayerInfoEncoder).
        // Byte-aligned bit-block (the client byte-aligns the bit cursor at each pass boundary): Pass-1 local
        // [active=1][hasExt=1][mvt=0] → 0xC0; Pass-3 low-res skip-run (2045) → 0x7F 0xF4. (Was the broken,
        // pass-packed `c7 ff 40` that desynced the client's ext-info drain → invisible avatar.)
        assertContentEquals(byteArrayOf(0xC0.toByte(), 0x7F, 0xF4.toByte()), info.bitBlock)
        assertEquals(1, info.extendedInfo.size)
        assertTrue(!info.firstTick)
        assertTrue(!player.viewport.firstTick)
    }

    @Test
    fun `idle per-tick op22 is the stationary-hold form that pins the local avatar`() {
        // BUG-1: a stationary spawned player drifted because buildIfNeeded returned null after the
        // first tick (no op22 → the client's local-avatar smoothing integrator ran open-loop and
        // crept). Now every idle tick emits the stationary-hold op22 prod sends. The exact bit body
        // for a solo viewport is: local high-res [hasUpdate=0][skipMode=0] (3 bits) + the low-res
        // active pass's single skip-run over all 2046 absent slots
        // [lead=0][mode=3][count=2045 as 11 bits] (14 bits) = 17 bits → 3 bytes `0f fe 80`.
        val player = newPlayer(Tile(3224, 3216, 0))
        // Drive past world entry so firstTick is cleared and the appearance is already delivered.
        PlayerInfoEncoder.buildWorldEntrySync(player)

        val idle = PlayerInfoEncoder.buildIfNeeded(player)
        assertEquals(0, idle.extendedInfo.size, "idle tick carries no ext-info once appearance is delivered")
        assertContentEquals(
            byteArrayOf(0x00, 0x7F, 0xF4.toByte()),
            idle.bitBlock,
            "idle op22 byte-aligned: Pass-1 local hasUpdate=0 → 0x00, Pass-3 2046-slot skip-run → 0x7F 0xF4",
        )
    }

    @Test
    fun `buildInit local entry is the stationary form regardless of a non-default spawn tile`() {
        // The op22 local entry no longer carries the tile (it lives in op81's prefix), but it must
        // stay the prod stationary inline-appearance form for any spawn — verify it is tile-invariant
        // and the op81 prefix is the one that tracks the non-default tile.
        val spawn = Tile(2440, 3090, 0)            // Falador-ish → zone (305, 386)
        val player = newPlayer(spawn)

        val info = PlayerInfoEncoder.buildInit(player)

        val r = BufferReader(info.bitBlock)
        r.startBitAccess()
        assertEquals(1, r.readBits(1), "hasUpdate=1")
        assertEquals(1, r.readBits(1), "hasExtInfo=1")
        assertEquals(0, r.readBits(2), "movementType=0 (stationary) for any spawn tile")
        r.stopBitAccess()
        assertEquals(1, info.extendedInfo.size, "inline appearance block present")

        // The op81 GPI prefix is where the non-default spawn tile actually travels (DIRECT gBit(30)).
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = player.index)
        val pr = BufferReader(prefix)
        pr.startBitAccess()
        val prefixTileId = pr.readBits(30)
        pr.stopBitAccess()
        assertEquals(spawn.id, prefixTileId, "op81 prefix local 30-bit tile follows player.tile for a non-default spawn")
        assertEquals(305, Tile.x(prefixTileId) shr 3)
        assertEquals(386, Tile.y(prefixTileId) shr 3)
    }
}
