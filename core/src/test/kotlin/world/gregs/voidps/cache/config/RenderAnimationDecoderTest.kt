package world.gregs.voidps.cache.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import world.gregs.voidps.cache.Definitions
import world.gregs.voidps.cache.config.data.RenderAnimationDefinition
import world.gregs.voidps.cache.config.decoder.RenderAnimationDecoder
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Files
import java.nio.file.Path

/**
 * Ground-truth decode of the default-avatar bas (render-animation / base-animation-set) def `2699`
 * (`combat_2012_casual_unarmed`) from the real 948 SQLite cache, and a guard that
 * `Cache.renderAnimation`'s backing decoder ([RenderAnimationDecoder], CONFIG index 2 / archive 32)
 * decodes it non-empty.
 *
 * The decoded seq ids are the source of the PLAYER_INFO ext-info MOVEMENT_ANIM block (bit 0x20):
 * `[primaryWalk, run, turning, primaryIdle]` (def offsets `+0x94/+0xa4/+0xac/+0x8c`) → the client's
 * `SetMovementAnimSet @0x1003a69f0` walk/run leg-animation driver. See
 * `re-resources/docs/net/serverprot/player-appearance-948.md`.
 *
 * The cache lives OUTSIDE the repo (`/Users/robert/darkan-3/948-sqlite-cache`), so this test resolves
 * it via `DARKAN_948_CACHE_DIR` → that default path → a walked-up `data/cache`, and skips (does not
 * fail) when none is present, keeping CI without the cache green. The cache is opened read-only and the
 * shared `Cache` singleton is never touched (a standalone [SQLiteCache] + fresh decoder are used).
 */
class RenderAnimationDecoderTest {

    private fun cacheDir(): Path? {
        // js5-2.jcache is the CONFIG index (archive 32 = RENDER_ANIMATIONS lives here).
        val marker = "js5-2.jcache"
        System.getenv("DARKAN_948_CACHE_DIR")?.let { Path.of(it) }
            ?.takeIf { Files.exists(it.resolve(marker)) }
            ?.let { return it }
        Path.of(DEFAULT_CACHE_DIR)
            .takeIf { Files.exists(it.resolve(marker)) }
            ?.let { return it }
        var here: Path? = Path.of("").toAbsolutePath()
        while (here != null) {
            val candidate = here.resolve("data/cache")
            if (Files.exists(candidate.resolve(marker))) return candidate
            here = here.parent
        }
        return null
    }

    @Test
    fun `decodes the default-avatar bas 2699 and logs its movement seqs`() {
        val dir = cacheDir()
        assumeTrue(dir != null, "948 SQLite cache not present (set DARKAN_948_CACHE_DIR) — skipping")

        // Standalone read-only cache + a fresh per-id decoder view — mirrors Cache.renderAnimation's
        // backing without binding the shared Cache singleton.
        val cache = SQLiteCache.load(dir!!, readOnly = true)
        val renderAnimations = Definitions(RenderAnimationDecoder(), cache)

        val def = assertDoesNotThrow("decoding bas 2699 must not throw") {
            renderAnimations.getOrNull(DEFAULT_BAS)
        }
        assertNotNull(def, "bas $DEFAULT_BAS must decode (default avatar render-anim set)")
        def!!

        // The 4 ids the bit-0x20 MOVEMENT_ANIM block carries, in wire order, plus the strafe variants.
        println("[bas$DEFAULT_BAS] movement seqs (wire order [primaryWalk, run, turning, primaryIdle]):")
        println("[bas$DEFAULT_BAS]   primaryWalk  (def+0x94) = ${def.primaryWalk}")
        println("[bas$DEFAULT_BAS]   run          (def+0xa4) = ${def.run}")
        println("[bas$DEFAULT_BAS]   turning      (def+0xac) = ${def.turning}")
        println("[bas$DEFAULT_BAS]   primaryIdle  (def+0x8c) = ${def.primaryIdle}")
        println("[bas$DEFAULT_BAS]   walkBackwards(def+0x9c) = ${def.walkBackwards}")
        println("[bas$DEFAULT_BAS]   sideStepLeft           = ${def.sideStepLeft}")
        println("[bas$DEFAULT_BAS]   sideStepRight          = ${def.sideStepRight}")
        println("[bas$DEFAULT_BAS]   secondaryWalk          = ${def.secondaryWalk}")

        assertEqualsNonEmpty(def)
    }

    /**
     * "Non-empty" = the def actually carried movement-animation data, not just the default `-1`/`0`
     * fields of an un-decoded [RenderAnimationDefinition]. The default avatar bas has, at minimum, a
     * walk and an idle seq.
     */
    private fun assertEqualsNonEmpty(def: RenderAnimationDefinition) {
        assertTrue(def.id == DEFAULT_BAS, "decoded def id matches the requested bas")
        val seqs = intArrayOf(def.primaryWalk, def.run, def.turning, def.primaryIdle)
        assertTrue(
            seqs.any { it >= 0 },
            "bas $DEFAULT_BAS must decode at least one movement seq (got ${seqs.toList()})",
        )
    }

    private companion object {
        const val DEFAULT_BAS = 2699
        const val DEFAULT_CACHE_DIR = "/Users/robert/darkan-3/948-sqlite-cache"
    }
}
