package org.darkan.world.world

import world.gregs.voidps.type.Region
import world.gregs.voidps.type.Tile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Verifies the 948-5 [BuildArea] service computes a non-inverted map-square grid that contains the
 * spawn region, and that its packed coordinates round-trip through the documented client decode
 * (`hi14` = X-tile, `lo14` = Z-tile, `>>6` = region). Ground truth:
 * `docs/protocol/packed-coord-buildarea-948.md`.
 */
class BuildAreaTest {

    /** Decode a packed word exactly as `BuildArea::DecodePackedCoord @0x006d4320` + the `>>6` does. */
    private fun decodeRegionX(word: Int) = ((word ushr 14) and 0x3FFF) ushr 6
    private fun decodeRegionZ(word: Int) = (word and 0x3FFF) ushr 6
    private fun decodePlane(word: Int) = (word ushr 28) and 0x3

    @Test
    fun `Lumbridge spawn yields a non-inverted grid containing region 50,50`() {
        // Default spawn tile (3200,3200) -> region (50,50).
        val spawn = Tile(3200, 3200, 0)
        assertEquals(50, spawn.region.x); assertEquals(50, spawn.region.y)

        val area = BuildArea.of(spawn)

        // Non-inverted on both axes (the single hard wire constraint, spec §3/§4).
        assertTrue(area.minRegion.x <= area.maxRegion.x, "non-inverted X: ${area.minRegion.x} <= ${area.maxRegion.x}")
        assertTrue(area.minRegion.y <= area.maxRegion.y, "non-inverted Z: ${area.minRegion.y} <= ${area.maxRegion.y}")

        // Contains the spawn region.
        assertTrue(area.containsRegion(Region(50, 50)), "build area must contain spawn region (50,50)")
        assertTrue(area.containsTile(spawn), "build area must contain the spawn tile")

        // MEDIUM (default) is a 5x5-region window centred on (50,50): X/Z[48..52].
        assertEquals(48, area.minRegion.x); assertEquals(52, area.maxRegion.x)
        assertEquals(48, area.minRegion.y); assertEquals(52, area.maxRegion.y)
        assertEquals(5, area.regionWidth); assertEquals(5, area.regionHeight)
        assertEquals(25, area.regions.size)
    }

    @Test
    fun `packed coordinates round-trip through the documented decode to the grid corners`() {
        val spawn = Tile(3200, 3200, 0)
        val area = BuildArea.of(spawn)

        // packedCoordA is the SW corner; packedCoordB is the NE corner. Decode both as the client.
        assertEquals(area.minRegion.x, decodeRegionX(area.packedCoordA), "packedA hi14>>6 == minRegionX")
        assertEquals(area.minRegion.y, decodeRegionZ(area.packedCoordA), "packedA lo14>>6 == minRegionZ")
        assertEquals(area.maxRegion.x, decodeRegionX(area.packedCoordB), "packedB hi14>>6 == maxRegionX")
        assertEquals(area.maxRegion.y, decodeRegionZ(area.packedCoordB), "packedB lo14>>6 == maxRegionZ")
        assertEquals(0, decodePlane(area.packedCoordA), "plane 0 for the overworld")
        assertEquals(0, decodePlane(area.packedCoordB), "plane 0 for the overworld")

        // And the decoded grid still contains the spawn region (end-to-end).
        val rx = spawn.region.x; val rz = spawn.region.y
        assertTrue(rx in decodeRegionX(area.packedCoordA)..decodeRegionX(area.packedCoordB))
        assertTrue(rz in decodeRegionZ(area.packedCoordA)..decodeRegionZ(area.packedCoordB))
    }

    @Test
    fun `pack reproduces production packedA for region 26,37`() {
        // Ground truth (spec §5.3): pack(0, region 26, 37) == 0x01a00940.
        assertEquals(0x01a00940, BuildArea.pack(plane = 0, regionX = 26, regionZ = 37))
    }

    @Test
    fun `window size widens the grid while still containing spawn`() {
        val spawn = Tile(3232, 3234, 0) // doc's Lumbridge tile -> region (50,50)
        assertEquals(50, spawn.region.x); assertEquals(50, spawn.region.y)

        val small = BuildArea.of(spawn, BuildAreaSize.SMALL)  // 3x3 regions
        val large = BuildArea.of(spawn, BuildAreaSize.LARGE)  // 7x7 regions

        assertEquals(3, small.regionWidth); assertEquals(3, small.regionHeight)
        assertEquals(7, large.regionWidth); assertEquals(7, large.regionHeight)
        assertTrue(small.containsRegion(spawn.region))
        assertTrue(large.containsRegion(spawn.region))
        // Larger window strictly covers the smaller one.
        assertTrue(small.regions.all { it in large.regions }, "LARGE must cover every SMALL region")
    }

    @Test
    fun `low-side clamp keeps regions non-negative near the map origin`() {
        // A spawn in region (1,0): SMALL half-window would go to -0/-1 on the low side; clamp to 0.
        val spawn = Tile(1 * 64, 0, 0) // region (1,0)
        assertEquals(1, spawn.region.x); assertEquals(0, spawn.region.y)
        val area = BuildArea.of(spawn, BuildAreaSize.MEDIUM) // halfRegions=2

        assertEquals(0, area.minRegion.x, "clamped to 0 (would be -1)")
        assertEquals(0, area.minRegion.y, "clamped to 0 (would be -2)")
        assertTrue(area.minRegion.x <= area.maxRegion.x); assertTrue(area.minRegion.y <= area.maxRegion.y)
        assertTrue(area.containsRegion(spawn.region))
        assertFalse(area.containsRegion(Region(99, 99)), "a far region is not in the grid")
    }
}
