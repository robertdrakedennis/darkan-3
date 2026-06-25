package world.gregs.voidps.cache.file

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.close
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class FileProviderTest {

    @Test
    fun `non-index group serves bare container`() {
        val provider = provider(container = byteArrayOf(0, 0, 0, 0, 3, 0x11, 0x22, 0x33))

        val wire = serveToBytes(provider, ref(index = 2, archive = 1))

        assertContentEquals(
            byteArrayOf(2, 0, 0, 0, 1, 0, 0, 0, 0, 3, 0x11, 0x22, 0x33),
            wire,
        )
    }

    @Test
    fun `index response does not append group version suffix`() {
        val provider = provider(container = byteArrayOf(0, 0, 0, 0, 3, 0x11, 0x22, 0x33))

        val wire = serveToBytes(provider, ref(index = 255, archive = 2))

        assertContentEquals(
            byteArrayOf(0xFF.toByte(), 0, 0, 0, 2, 0, 0, 0, 0, 3, 0x11, 0x22, 0x33),
            wire,
        )
    }

    @Test
    fun `stored suffix is ignored`() {
        val provider = provider(container = byteArrayOf(0, 0, 0, 0, 3, 0x11, 0x22, 0x33, 0x55, 0x66))

        val wire = serveToBytes(provider, ref(index = 2, archive = 1))

        assertContentEquals(
            byteArrayOf(2, 0, 0, 0, 1, 0, 0, 0, 0, 3, 0x11, 0x22, 0x33),
            wire,
        )
    }

    /**
     * Skips when ./data/cache is absent so CI hosts without the large cache stay green.
     */
    @Test
    fun `served groups match stored containers (real cache)`() {
        val cacheDir = locateCacheDir()
        if (cacheDir == null) {
            println("FileProviderTest: data/cache absent — skipping real-cache serve proof")
            return
        }

        val cache = SQLiteCache.load(cacheDir)
        try {
            val provider = CacheFileProviderForTest(cache)
            DecompressionContext().use { ctx ->
                assertServedGroupMatchesContainer(cache, provider, ctx, index = 12, sampleGroups = listOf(0, 1, 2, 5, 10, 100, 270))
                if (Files.exists(cacheDir.resolve("js5-19.jcache"))) {
                    assertServedGroupMatchesContainer(cache, provider, ctx, index = 19, sampleGroups = listOf(0, 1, 2, 5, 50, 100))
                }
            }
        } finally {
            cache.close()
        }
    }

    /**
     * Finds the `data/cache` directory containing `js5-12.jcache`, walking up from the test's
     * working directory (which is the `core` module dir under Gradle, not the repo root) and
     * honouring the `CACHE_PATH` env var when set. Returns null when no such cache is present.
     */
    private fun locateCacheDir(): java.nio.file.Path? {
        val candidates = buildList {
            System.getenv("CACHE_PATH")?.let { add(Paths.get(it)) }
            var dir: java.nio.file.Path? = Paths.get("").toAbsolutePath()
            repeat(5) {
                dir?.let { add(it.resolve("data/cache")) }
                dir = dir?.parent
            }
        }
        return candidates.firstOrNull { Files.exists(it.resolve("js5-12.jcache")) }
    }

    /**
     * For each [sampleGroups] entry that exists in [index]'s ref table, serve it through the
     * real [provider] and assert the response body is the stored container exactly.
     */
    private fun assertServedGroupMatchesContainer(
        cache: world.gregs.voidps.cache.Cache,
        provider: FileProvider,
        ctx: DecompressionContext,
        index: Int,
        sampleGroups: List<Int>,
    ) {
        val refRaw = cache.sector(255, index) ?: error("no ref table for index $index")
        val refVersions = parseRefTableVersions(ctx.decompress(refRaw) ?: error("ref table $index failed to decompress"))
        var asserted = 0
        for (group in sampleGroups) {
            if (!refVersions.containsKey(group)) continue
            val container = cache.sector(index, group) ?: continue
            val wire = serveToBytes(provider, ref(index, group))
            val expected = ByteArray(5 + container.size)
            expected[0] = index.toByte()
            expected[1] = 0
            expected[2] = 0
            expected[3] = (group shr 8).toByte()
            expected[4] = group.toByte()
            System.arraycopy(container, 0, expected, 5, container.size)
            assertContentEquals(expected, wire, "index $index group $group served bytes differ from stored container")
            asserted++
        }
        assertTrue(asserted > 0, "index $index: no sample groups found in ref table to assert")
        println("FileProviderTest: index $index — served body matched stored container for $asserted groups")
    }

    /**
     * Minimal JS5 reference-table (formats 5-7) parser: returns groupId -> 32-bit version.
     * Mirrors the per-group version section the NXT client reads in `Js5Index::LoadIndex`.
     */
    private fun parseRefTableVersions(decompressed: ByteArray): Map<Int, Int> {
        val reader = BufferReader(decompressed)
        val format = reader.readUnsignedByte()
        require(format in 5..7) { "unexpected ref table format $format" }
        if (format >= 6) reader.readInt() // revision
        val flags = reader.readUnsignedByte()
        fun count() = if (format >= 7) reader.readBigSmart() else reader.readUnsignedShort()
        val archiveCount = count()
        var previous = 0
        val ids = IntArray(archiveCount) {
            previous += count()
            previous
        }
        if (flags and 0x1 != 0) reader.skip(archiveCount * 4)  // name hashes
        reader.skip(archiveCount * 4)                          // CRCs
        if (flags and 0x8 != 0) reader.skip(archiveCount * 4)  // extra hashes
        if (flags and 0x2 != 0) reader.skip(archiveCount * 64) // whirlpool
        if (flags and 0x4 != 0) reader.skip(archiveCount * 8)  // sizes
        val versions = IntArray(archiveCount) { reader.readInt() }
        return ids.indices.associate { ids[it] to versions[it] }
    }

    /** Local [CacheFileProvider] stand-in so the test does not depend on the in-memory provider. */
    private class CacheFileProviderForTest(private val cache: world.gregs.voidps.cache.Cache) : FileProvider {
        override fun data(index: Int, archive: Int): ByteArray? {
            if (index == 255 && archive == 255) return cache.versionTable
            return cache.sector(index, archive)
        }
    }

    private fun provider(container: ByteArray): FileProvider {
        return object : FileProvider {
            override fun data(index: Int, archive: Int): ByteArray = container
        }
    }

    private fun serveToBytes(provider: FileProvider, ref: Long): ByteArray = runBlocking {
        val channel = ByteChannel()
        provider.serve(channel, ref, prefetch = false)
        channel.close()
        val out = ByteArray(channel.availableForRead)
        channel.readFully(out)
        out
    }

    private fun ref(index: Int, archive: Int): Long {
        return (index.toLong() shl 32) or (archive.toLong() and 0xFFFFFFFFL)
    }
}
