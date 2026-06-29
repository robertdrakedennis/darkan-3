package org.darkan.core.net.prot

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.darkan.core.net.prot.revision.rev948.register948
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Wire-format regression for the lobby→world hop packets SET_WORLD_TARGET (op 212) and
 * SWITCH_WORLD (op 213) under the 948 codec.
 *
 * Ground truth: ground-truth 948-5 decompile in
 * `~/projects/reclass-data/docs/binary/boot/05-worldlist-switch.md`:
 *  - §4.1 WorldData::SET_WORLD_TARGET @ 0x0017fc70 reads: host(jstr), worldId(BE u16),
 *    portA(BE u16), portB(BE u16).
 *  - §4.2 WorldData::SWITCH_WORLD @ 0x001aeba0 reads: worldId(BE u16), host(jstr),
 *    portA(BE u16), portB(BE u16), reconnectFlag(u8).
 *
 * The two packets have DIFFERENT field orders (212 = host-first, 213 = worldId-first). A prior
 * bug wrote host-first in BOTH, which made the client parse worldId out of the host bytes for
 * op 213 and connect to a garbage host:port (silent failure to open the world socket). This test
 * pins the corrected byte order so it cannot regress.
 */
class WorldSwitchEncoderTest {

    private val codec = register948()

    /** Encode a ServerProt through its registered 948 encoder and return the raw body bytes. */
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

    /** CP1252 host string + NUL terminator (matches writeRSString). */
    private fun jstr(s: String): ByteArray = s.toByteArray(Charsets.ISO_8859_1) + 0x00.toByte()

    private fun u16be(v: Int): ByteArray = byteArrayOf((v ushr 8).toByte(), v.toByte())

    @Test
    fun `SET_WORLD_TARGET op212 is host, worldId, portA, portB`() {
        val host = "127.0.0.1"
        val worldId = 1
        val portA = 43597
        val portB = 443

        val actual = encodeBody(SetWorldTarget(hostname = host, worldId = worldId, port1 = portA, port2 = portB))
        val expected = jstr(host) + u16be(worldId) + u16be(portA) + u16be(portB)

        assertContentEqualsHex(expected, actual)
        // The first bytes MUST be the host string (host-first), not the worldId.
        assertEquals('1'.code.toByte(), actual[0], "op212 must start with the host string")
    }

    @Test
    fun `SWITCH_WORLD op213 is worldId, host, portA, portB, flag`() {
        val host = "127.0.0.1"
        val worldId = 1
        val portA = 43597
        val portB = 443
        val flag = 0

        val actual = encodeBody(
            SwitchWorld(hostname = host, worldId = worldId, port1 = portA, port2 = portB, pendingFlag = flag)
        )
        val expected = u16be(worldId) + jstr(host) + u16be(portA) + u16be(portB) + byteArrayOf(flag.toByte())

        assertContentEqualsHex(expected, actual)
        // The first two bytes MUST be the BE worldId, NOT the host string. This is the regression
        // guard for the host-first bug.
        assertEquals(0x00.toByte(), actual[0], "op213 byte0 must be worldId high byte")
        assertEquals(0x01.toByte(), actual[1], "op213 byte1 must be worldId low byte")
        assertTrue(actual[2] == '1'.code.toByte(), "op213 host string must START at byte 2 (after worldId)")
    }

    @Test
    fun `SWITCH_WORLD differs from SET_WORLD_TARGET field order`() {
        // Same logical inputs, different byte layouts — guards against re-unifying the two encoders.
        val host = "h"
        val target = encodeBody(SetWorldTarget(hostname = host, worldId = 7, port1 = 1, port2 = 2))
        val switch = encodeBody(SwitchWorld(hostname = host, worldId = 7, port1 = 1, port2 = 2, pendingFlag = 0))
        // op212: 'h' 0x00 00 07 00 01 00 02            → byte0 = 'h'
        // op213: 00 07 'h' 0x00 00 01 00 02 00         → byte0 = 0x00 (worldId hi)
        assertEquals('h'.code.toByte(), target[0])
        assertEquals(0x00.toByte(), switch[0])
        assertEquals(0x07.toByte(), switch[1])
    }

    @Test
    fun `REBUILD_NORMAL_SIMPLE op81 is the verified 18-byte 948 layout`() {
        // Verified field-by-field against rs2client.948-5 handler @ 0x001daa70.
        val zoneX = 400        // 0x0190
        val zoneZ = 401        // 0x0191 — distinct from X, and high byte non-zero to prove LE order
        val npcInfoCoordBitWidth = 3
        val sceneRootId = 0
        val packedA = 0x01234567
        val packedB = 0x089ABCDE

        val actual = encodeBody(
            RebuildNormalSimple(
                zoneX = zoneX,
                zoneZ = zoneZ,
                packedCoordA = packedA,
                packedCoordB = packedB,
                npcInfoCoordBitWidth = npcInfoCoordBitWidth,
                sceneRootId = sceneRootId,
            )
        )

        val expected = byteArrayOf(
            0xFF.toByte(),                          // +0 filler
            (zoneZ and 0xFF).toByte(),              // +1 Z low  = 0x91
            ((zoneZ ushr 8) and 0xFF).toByte(),     // +2 Z high = 0x01
            0x85.toByte(),                          // +3 magic
            (zoneX ushr 8).toByte(), zoneX.toByte(),// +4 X BE = 01 90
            ((npcInfoCoordBitWidth + 0x80) and 0xFF).toByte(), // +6 NPC coord bit width (writeByteAdd)
            0x00,                                   // +7 filler
            (sceneRootId ushr 8).toByte(), sceneRootId.toByte(), // +8 sceneRootId BE
            // +10 packedA BE
            (packedA ushr 24).toByte(), (packedA ushr 16).toByte(), (packedA ushr 8).toByte(), packedA.toByte(),
            // +14 packedB BE
            (packedB ushr 24).toByte(), (packedB ushr 16).toByte(), (packedB ushr 8).toByte(), packedB.toByte(),
        )

        assertEquals(18, actual.size, "948 REBUILD_NORMAL_SIMPLE body must be exactly 18 bytes")
        assertContentEqualsHex(expected, actual)
        assertEquals(0x85.toByte(), actual[3], "magic at +3 must be 0x85")
        // Z is little-endian (lo at +1, hi at +2); X is big-endian (hi at +4, lo at +5).
        assertEquals(0x91.toByte(), actual[1]); assertEquals(0x01.toByte(), actual[2])
        assertEquals(0x01.toByte(), actual[4]); assertEquals(0x90.toByte(), actual[5])
    }

    @Test
    fun `REBUILD_NORMAL_SIMPLE op81 with an empty prefix is the bare 18-byte header`() {
        // The op81 encoder writes rebuildPrefix first, then the 18-byte coord header. With an empty
        // prefix (the data-class default — used by non-world callers/tests) the body is the bare
        // 18-byte header at offset 0, magic at +3. The WORLD login does NOT use this shape — it
        // ships the generated 5119-byte GPI prefix (Shape B); shipping the bare header on world
        // entry is FATAL (the client's prefix parser over-reads heap → op81 aborts → black screen,
        // docs/protocol/world-bootstrap-948.md §"⚠️ CORRECTION (2026-06-23)"). The Shape-B body math
        // (5137 bytes, magic at offset 5122) is covered by world's Op81GpiPrefixTest.
        //
        // Build-area corners are REGION corners (packed-coord-buildarea-948.md): a Lumbridge window
        // region X[48..52] Z[48..52] (contains spawn region 50,50). Non-inverted.
        val actual = encodeBody(
            RebuildNormalSimple(
                zoneX = 400,
                zoneZ = 400,
                packedCoordA = RebuildNormalSimple.packRegionCoord(regionX = 48, regionZ = 48),
                packedCoordB = RebuildNormalSimple.packRegionCoord(regionX = 52, regionZ = 52),
                npcInfoCoordBitWidth = 0,
                sceneRootId = 300,
                // no rebuildPrefix — defaults to ByteArray(0) → bare 18-byte header
            )
        )

        assertEquals(18, actual.size, "empty-prefix op81 body must be exactly 18 bytes (bare header)")
        // Coord header sits at offset 0 (position == 0 when no prefix precedes it); magic at +3.
        assertEquals(0x85.toByte(), actual[3], "magic at +3 must be 0x85 with no prefix offset")
        assertEquals(0x01.toByte(), actual[4]); assertEquals(0x90.toByte(), actual[5]) // centreZoneX=400 BE
    }

    @Test
    fun `op81 with a Shape-B prefix puts the body at prefix-size + 18 with magic after the prefix`() {
        // Shape B (docs/protocol/world-bootstrap-948.md §"⚠️ CORRECTION (2026-06-23)"): the op81
        // encoder writes the GPI prefix first, then the 18-byte coord header, so the magic byte
        // moves to prefix.size + 3. This is a codec-level proof (the production prefix is 5119, but
        // the encoder is prefix-length-agnostic, so a compact synthetic prefix exercises the same
        // framing path here; world's Op81GpiPrefixTest pins the real 5119/5137/5122 numbers).
        val prefix = ByteArray(40) { 0x11 }     // stand-in for the GPI prefix; contents irrelevant to framing
        val actual = encodeBody(
            RebuildNormalSimple(
                zoneX = 404,
                zoneZ = 404,
                packedCoordA = RebuildNormalSimple.packRegionCoord(regionX = 48, regionZ = 48),
                packedCoordB = RebuildNormalSimple.packRegionCoord(regionX = 52, regionZ = 52),
                npcInfoCoordBitWidth = 7,
                sceneRootId = 474,
                rebuildPrefix = prefix,
            )
        )

        assertEquals(prefix.size + 18, actual.size, "Shape-B op81 body must be prefix.size + 18 header")
        // Prefix bytes come first, verbatim.
        for (i in prefix.indices) assertEquals(0x11.toByte(), actual[i], "prefix byte $i must be written verbatim before the header")
        // Magic is now at prefix.size + 3 (NOT offset 3); the header starts at prefix.size.
        assertEquals(0x85.toByte(), actual[prefix.size + 3], "magic 0x85 must land at prefix.size + 3")
        // centreZoneX=404 BE at header +4 = prefix.size + 4.
        assertEquals(((404 ushr 8) and 0xFF).toByte(), actual[prefix.size + 4])
        assertEquals((404 and 0xFF).toByte(), actual[prefix.size + 5])
    }

    @Test
    fun `op81 centre zone and GPI prefix local tile pack from the SAME spawn tile (coherence)`() {
        // The load-bearing coherence invariant (§4): the op81 coord-header centre zone and the op81
        // GPI-prefix local 30-bit tile must derive from ONE spawn tile. Unlike the per-tick op22
        // shape, the prefix parser reads the local tile as a DIRECT gBit(30) with no 4-bit header.
        // This reproduces that exact leading write (mirroring Op81GpiPrefix.build's first word) and
        // proves the 30-bit tile round-trips to the same zone the op81 header centres on.
        val spawnX = 3200; val spawnY = 3200; val plane = 0
        val spawnTileId = world.gregs.voidps.type.Tile.id(spawnX, spawnY, plane)

        // Build the op81-prefix-style leading word: gBit(30) = absolute tile, read DIRECTLY.
        val bw = world.gregs.voidps.buffer.write.BufferWriter(8)
        bw.startBitAccess()
        bw.writeBits(30, spawnTileId)      // absolute tile — no [hasUpdate][hasExt][moveType] header
        bw.stopBitAccess()
        val bits = bw.toArray()

        // Decode the 30-bit absolute tile back out (MSB-first reader, from the very first bit).
        val r = world.gregs.voidps.buffer.read.BufferReader(bits)
        r.startBitAccess()
        val decodedTileId = r.readBits(30)
        r.stopBitAccess()

        assertEquals(spawnTileId, decodedTileId, "op81 prefix local 30-bit tile must round-trip to the spawn tile")
        // op81 centre zone derived from the same tile.
        val centreZoneX = spawnX shr 3
        val centreZoneZ = spawnY shr 3
        assertEquals(400, centreZoneX); assertEquals(400, centreZoneZ)
        // And the decoded GPI tile's zone equals the op81 centre zone — the coherence the fix guarantees.
        assertEquals(centreZoneX, world.gregs.voidps.type.Tile.x(decodedTileId) shr 3, "GPI tile zone X == op81 centre zone X")
        assertEquals(centreZoneZ, world.gregs.voidps.type.Tile.y(decodedTileId) shr 3, "GPI tile zone Z == op81 centre zone Z")
    }

    @Test
    fun `packRegionCoord round-trips through the documented decode (hi14 X-tile, lo14 Z-tile, shifted to region)`() {
        // DecodePackedCoord @0x006d4320: lo14 = word & 0x3FFF (Z-tile); hi14 = (word>>14)&0x3FFF
        // (X-tile); plane = (word>>28)&3. The handler then `>>6`s each 14-bit field to a REGION.
        val word = RebuildNormalSimple.packRegionCoord(regionX = 50, regionZ = 51)
        val lo14 = word and 0x3FFF          // Z-tile
        val hi14 = (word ushr 14) and 0x3FFF // X-tile
        assertEquals(50, hi14 ushr 6, "regionX must recover as (hi14 >> 6)")
        assertEquals(51, lo14 ushr 6, "regionZ must recover as (lo14 >> 6)")
        assertEquals(0, (word ushr 28) and 0x3, "plane defaults to 0")
    }

    @Test
    fun `packRegionCoord reproduces production packedA and packedB`() {
        // Ground truth (packed-coord-buildarea-948.md §5.3): production's SW corner is region
        // (26,37) and NE corner is region (72,142). The low6 of the field is ignored by the build
        // path, so the clean region<<6 form must reproduce packedA exactly; packedB matches once
        // the cosmetic +0x38 low bits are masked off (>>6 of both forms is identical).
        val packedA = RebuildNormalSimple.packRegionCoord(regionX = 26, regionZ = 37)
        assertEquals(0x01a00940, packedA, "packedA must reproduce production for SW region (26,37)")

        val packedB = RebuildNormalSimple.packRegionCoord(regionX = 72, regionZ = 142)
        // Decode both to regions and confirm they equal production's NE corner (72,142).
        assertEquals(72, ((packedB ushr 14) and 0x3FFF) ushr 6, "packedB hi14 >> 6 == maxRegionX 72")
        assertEquals(142, (packedB and 0x3FFF) ushr 6, "packedB lo14 >> 6 == maxRegionZ 142")
    }

    @Test
    fun `op81 region corners decode to a non-inverted grid containing the Lumbridge spawn region`() {
        // The definitive black-screen guard: the build-area corners op81 ships MUST decode to a
        // non-inverted grid (min <= max on both axes) that CONTAINS the spawn region. A Lumbridge
        // spawn tile (3200,3200) is region (50,50). Use a symmetric window region X/Z[48..52].
        val spawnRegionX = 3200 shr 6   // = 50
        val spawnRegionZ = 3200 shr 6   // = 50
        assertEquals(50, spawnRegionX); assertEquals(50, spawnRegionZ)

        val packedA = RebuildNormalSimple.packRegionCoord(regionX = 48, regionZ = 48)
        val packedB = RebuildNormalSimple.packRegionCoord(regionX = 52, regionZ = 52)

        // Decode exactly as the client does.
        val minRegionX = ((packedA ushr 14) and 0x3FFF) ushr 6
        val minRegionZ = (packedA and 0x3FFF) ushr 6
        val maxRegionX = ((packedB ushr 14) and 0x3FFF) ushr 6
        val maxRegionZ = (packedB and 0x3FFF) ushr 6

        assertTrue(minRegionX <= maxRegionX, "non-inverted X bounds: $minRegionX <= $maxRegionX")
        assertTrue(minRegionZ <= maxRegionZ, "non-inverted Z bounds: $minRegionZ <= $maxRegionZ")
        assertTrue(spawnRegionX in minRegionX..maxRegionX, "spawn regionX 50 inside [$minRegionX..$maxRegionX]")
        assertTrue(spawnRegionZ in minRegionZ..maxRegionZ, "spawn regionZ 50 inside [$minRegionZ..$maxRegionZ]")
    }

    private fun assertContentEqualsHex(expected: ByteArray, actual: ByteArray) {
        fun hex(b: ByteArray) = b.joinToString(" ") { "%02x".format(it) }
        assertEquals(hex(expected), hex(actual), "wire bytes mismatch")
    }
}
