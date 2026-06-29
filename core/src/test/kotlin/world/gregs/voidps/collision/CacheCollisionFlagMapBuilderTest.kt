package world.gregs.voidps.collision

import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CacheCollisionFlagMapBuilderTest {

    @Test
    fun `lumbridge map square derives blocked wall flags from cache locs`() {
        val cache = SQLiteCache.load(repoCachePath(), readOnly = true)
        try {
            val flags = CollisionMap()
            val loaded = CacheCollisionFlagMapBuilder(cache).loadRegion(LUMBRIDGE_REGION_ID, flags)

            assertTrue(loaded, "Lumbridge map square exists in the cache")
            val wallFlags = flags.getFlags(3200, 3204, 0)
            assertNotEquals(-1, wallFlags, "loaded map square allocated the routefinder zone")
            assertTrue(
                wallFlags and CollisionFlag.WALL_EAST != 0,
                "tile (3200,3204,0) carries the cache-derived east wall walk bit"
            )
            assertEquals(
                0,
                wallFlags and CollisionFlag.WALL_EAST_PROJECTILE_BLOCKER,
                "this cache loc is the default walk-only clip type, so projectile lane stays clear"
            )
            assertEquals(
                0,
                wallFlags and CollisionFlag.WALL_EAST_ROUTE_BLOCKER,
                "static cache derivation does not set the optional route-blocker lane"
            )
        } finally {
            cache.close()
        }
    }

    private fun repoCachePath(): Path {
        val cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize()
        return if (cwd.fileName.toString() == "core") {
            cwd.resolve("../data/cache").normalize()
        } else {
            cwd.resolve("data/cache").normalize()
        }
    }

    private companion object {
        private const val LUMBRIDGE_REGION_ID = (50 shl 8) or 50
    }
}
