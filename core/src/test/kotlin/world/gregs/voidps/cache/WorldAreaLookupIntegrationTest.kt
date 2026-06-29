package world.gregs.voidps.cache

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.streams.asSequence

/**
 * Verifies the production WorldAreaType tile lookup against known 948-5 surface-world tiles.
 * Skips on machines without the live NXT SQLite cache.
 */
class WorldAreaLookupIntegrationTest {

    private fun resolveCacheDir(): Path? {
        Path.of("data/cache").takeIf { it.hasCache() }?.let { return it }
        System.getenv("RS_CACHE_DIR")?.let { Path.of(it) }?.takeIf { it.hasCache() }?.let { return it }
        val home = System.getProperty("user.home")
        for (candidate in listOf("$home/Jagex/RuneScape", "$home/.local/share/darkan-launcher/Jagex/RuneScape")) {
            Path.of(candidate).takeIf { it.hasCache() }?.let { return it }
        }
        return null
    }

    private fun Path.hasCache(): Boolean = exists() && Files.list(this).use { stream ->
        stream.asSequence().any { it.fileName.toString().matches(Regex("js5-\\d+\\.jcache")) }
    }

    @Test
    fun `surface-world spawn tiles resolve to WorldAreaType 474`() {
        val dir = resolveCacheDir()
        assumeTrue(dir != null, "no live NXT cache on this machine - skipping")
        Cache.init(dir!!, readOnly = true)

        assertEquals(474, Cache.worldAreaTypeAt(3204, 3204), "Lumbridge should resolve to surface WorldAreaType")
        assertEquals(474, Cache.worldAreaTypeAt(2897, 3538), "Burthorpe should resolve to surface WorldAreaType")
    }
}
