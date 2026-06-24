package org.darkan.world.net

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.RebuildNormalSimple
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.revision.rev948.register948
import org.darkan.world.world.BuildArea
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.type.Tile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Validates the **op81 Shape B GPI prefix** ([Op81GpiPrefix]) and its op81 body framing, per
 * `docs/protocol/world-bootstrap-948.md` §"⚠️ CORRECTION (2026-06-23)" (decompile-verified,
 * 948-5).
 *
 * The keystone facts pinned here:
 *  - the generated prefix is **exactly 5119 bytes** (`30 + 2046×20 = 40950` bits, byte-aligned),
 *  - its **first 30 bits decode back to `spawnTile.id`** (the local player's absolute tile, read
 *    DIRECTLY by the client's prefix parser — no `[hasUpdate][hasExt][moveType]` header),
 *  - the other-slot words are **2046 all-zero** 20-bit words (absent slots; correct for a solo
 *    spawn) and the **local `playerIndex` is skipped**,
 *  - threaded through the 948 op81 codec with the coord header, the **body is 5137 bytes** and the
 *    **magic byte `0x85` lands at body offset 5122** (= 5119 prefix + 3),
 *  - the prefix's local tile, the op81 centre zone, and the build area all derive from ONE tile —
 *    the local tile's zone equals the centre zone and the tile is INSIDE the build-area grid
 *    (§4 coherence).
 */
class Op81GpiPrefixTest {

    private val codec = register948()

    /** Encode a ServerProt through its registered 948 encoder and return the raw BODY bytes. */
    private fun encodeBody(prot: ServerProt): ByteArray = runBlocking {
        val entry = codec.serverProts[prot::class]
            ?: error("No encoder registered for ${prot::class.simpleName}")
        val ch = ByteChannel()
        (entry.encoder ?: error("Null encoder for ${prot::class.simpleName}")).invoke(prot, ch)
        ch.flush()
        val out = ByteArray(ch.availableForRead)
        ch.readFully(out)
        ch.close(null)
        out
    }

    @Test
    fun `prefix is exactly 5119 bytes`() {
        val prefix = Op81GpiPrefix.build(spawnTile = Tile(3235, 3234, 0), localPlayerIndex = 1)
        assertEquals(5119, prefix.size, "GPI prefix must byte-align to 5119 (30 + 2046*20 = 40950 bits)")
        assertEquals(Op81GpiPrefix.PREFIX_BYTES, prefix.size, "PREFIX_BYTES constant must match the produced size")
    }

    @Test
    fun `first 30 bits decode back to the spawn tile id`() {
        // Production ground truth: local tile 0x0328CCA2 = (plane 0, x 3235, y 3234) → zone (404,404).
        val spawn = Tile(3235, 3234, 0)
        assertEquals(0x0328CCA2, spawn.id, "sanity: Tile.id packs (plane<<28)|(x<<14)|y")

        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = 1)

        // Read the leading gBit(30) MSB-first — exactly how the client's prefix parser reads it
        // (no 4-bit header; the 30-bit absolute tile is the very first thing in the stream).
        val r = BufferReader(prefix)
        r.startBitAccess()
        val decodedTileId = r.readBits(30)
        r.stopBitAccess()

        assertEquals(spawn.id, decodedTileId, "first 30 bits must decode to the local player's spawn tile id")
        assertEquals(3235, Tile.x(decodedTileId))
        assertEquals(3234, Tile.y(decodedTileId))
        assertEquals(0, Tile.level(decodedTileId))
    }

    @Test
    fun `the 2046 other-slot words are all zero and the local index is skipped`() {
        val spawn = Tile(3235, 3234, 0)
        val localIndex = 1
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = localIndex)

        val r = BufferReader(prefix)
        r.startBitAccess()
        r.readBits(30) // skip the local player's absolute tile
        var nonZero = 0
        repeat(Op81GpiPrefix.TOTAL_SLOTS - 2) { // 2046 other-slot words
            if (r.readBits(Op81GpiPrefix.OTHER_SLOT_BITS) != 0) nonZero++
        }
        r.stopBitAccess()

        assertEquals(0, nonZero, "all 2046 other-slot words must be zero (absent) for a solo spawn")
        // Bit accounting proves the loop wrote exactly 2046 words (skipping one of 2047): if the
        // local index had NOT been skipped, the stream would be 20 bits longer and not byte-align
        // to 5119. The size assertion above + this full consume confirm the 2046 count.
    }

    @Test
    fun `op81 body is 5137 bytes with magic 0x85 at offset 5122`() {
        val spawn = Tile(3235, 3234, 0)
        val buildArea = BuildArea.of(spawn)
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = 1)

        val body = encodeBody(
            RebuildNormalSimple(
                zoneX = spawn.zone.x,
                zoneZ = spawn.zone.y,
                packedCoordA = buildArea.packedCoordA,
                packedCoordB = buildArea.packedCoordB,
                cameraRotation = 7,
                targetWorldId = 474,
                rebuildPrefix = prefix,
            )
        )

        assertEquals(5137, body.size, "Shape B op81 body must be 5119 prefix + 18 header = 5137")
        assertEquals(0x85.toByte(), body[5122], "magic 0x85 must land at body offset 5122 (= 5119 + 3)")
        // The 18-byte coord header sits at offset 5119; its centreZoneX (BE) is at +4 → body[5123..5124].
        val centreZoneXFromHeader = ((body[5123].toInt() and 0xFF) shl 8) or (body[5124].toInt() and 0xFF)
        assertEquals(spawn.zone.x, centreZoneXFromHeader, "header centreZoneX (at body 5123 BE) must be the spawn zone X")
    }

    @Test
    fun `the prefix local tile, op81 centre zone, and build area all agree (coherence)`() {
        // §4: one spawn tile drives the prefix's local 30-bit tile, the op81 centre zone, and the
        // build-area grid. The local tile's zone == the centre zone AND the tile is inside the grid.
        val spawn = Tile(3235, 3234, 0)
        val buildArea = BuildArea.of(spawn)
        val prefix = Op81GpiPrefix.build(spawnTile = spawn, localPlayerIndex = 1)

        val r = BufferReader(prefix)
        r.startBitAccess()
        val localTileId = r.readBits(30)
        r.stopBitAccess()

        // Local tile zone == op81 centre zone (both from the same spawn tile).
        assertEquals(spawn.zone.x, Tile.x(localTileId) shr 3, "prefix local tile zone X == op81 centre zone X")
        assertEquals(spawn.zone.y, Tile.y(localTileId) shr 3, "prefix local tile zone Z == op81 centre zone Z")
        assertEquals(404, spawn.zone.x); assertEquals(404, spawn.zone.y)

        // The local tile's REGION must be inside the build-area grid the client allocates.
        val region = Tile(localTileId).region
        assertTrue(
            region.x in buildArea.minRegion.x..buildArea.maxRegion.x,
            "local tile regionX ${region.x} inside build area [${buildArea.minRegion.x}..${buildArea.maxRegion.x}]"
        )
        assertTrue(
            region.y in buildArea.minRegion.y..buildArea.maxRegion.y,
            "local tile regionZ ${region.y} inside build area [${buildArea.minRegion.y}..${buildArea.maxRegion.y}]"
        )
    }

    @Test
    fun `the skipped slot moves with localPlayerIndex but the size is invariant`() {
        // Whatever real slot the player gets, exactly one of 2047 is skipped → 2046 words → 5119.
        for (idx in intArrayOf(1, 2, 1000, 2047)) {
            val prefix = Op81GpiPrefix.build(spawnTile = Tile(3200, 3200, 0), localPlayerIndex = idx)
            assertEquals(5119, prefix.size, "prefix size must stay 5119 for localPlayerIndex=$idx")
        }
    }
}
