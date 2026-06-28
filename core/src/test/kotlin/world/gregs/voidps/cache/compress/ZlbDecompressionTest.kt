package world.gregs.voidps.cache.compress

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Regression guard for the NXT client's on-disk group blob ("ZLB") format
 * (`jag::Js5DiskCache::DecodeStoredBlob`, see `re-resources/docs/cache/sqlite-disk-blob-format.md`).
 *
 * The blob is `['Z' 'L' 'B'][version 0x01][uncompressedSize u32 BE][zlib stream]`. The size lives at
 * offset 4 (AFTER the version byte) and is big-endian; the deflate stream begins at offset 8. A prior
 * implementation read the size at offset 3 — consuming the version byte as the high byte — which
 * produced a ~16 MB bogus length and an all-zero buffer, making every multi-file group split into
 * empty files (the in-game invisible-avatar / NpcLoad BufferUnderflow bug).
 */
class ZlbDecompressionTest {

    private fun zlb(payload: ByteArray, version: Int = 0x01): ByteArray {
        val out = ByteArrayOutputStream()
        out.write('Z'.code); out.write('L'.code); out.write('B'.code)
        out.write(version)
        // uncompressedSize, big-endian
        out.write((payload.size ushr 24) and 0xFF)
        out.write((payload.size ushr 16) and 0xFF)
        out.write((payload.size ushr 8) and 0xFF)
        out.write(payload.size and 0xFF)
        // full zlib stream (RFC1950, includes 0x78 header + adler32)
        val deflater = Deflater(Deflater.DEFAULT_COMPRESSION, false)
        deflater.setInput(payload); deflater.finish()
        val buf = ByteArray(payload.size + 64)
        while (!deflater.finished()) {
            val n = deflater.deflate(buf)
            out.write(buf, 0, n)
        }
        deflater.end()
        return out.toByteArray()
    }

    @Test
    fun `round-trips a ZLB blob`() {
        val payload = "the cooked shrimp swims home".toByteArray(Charsets.US_ASCII)
        DecompressionContext().use { ctx ->
            val decoded = ctx.decompress(zlb(payload))
            assertNotNull(decoded, "ZLB blob must decode")
            assertContentEquals(payload, decoded)
        }
    }

    @Test
    fun `size is read after the version byte, not over it`() {
        // A payload whose size has a non-zero low byte: if the reader took the size at offset 3 it
        // would read [version, sizeHi, sizeMid, sizeLo-ish] and get a wildly wrong (huge) length.
        val payload = Random(7).nextBytes(15762) // the real NPC-group size that triggered the bug
        DecompressionContext().use { ctx ->
            val decoded = ctx.decompress(zlb(payload))
            assertNotNull(decoded)
            assertEquals(15762, decoded.size, "must allocate exactly uncompressedSize, not a 16MB misread")
            assertContentEquals(payload, decoded)
        }
    }

    @Test
    fun `decodes various payload sizes`() {
        val random = Random(99)
        DecompressionContext().use { ctx ->
            for (size in intArrayOf(1, 9, 64, 517, 4096, 32768)) {
                val payload = random.nextBytes(size)
                val decoded = ctx.decompress(zlb(payload))
                assertNotNull(decoded, "size=$size")
                assertContentEquals(payload, decoded, "size=$size")
            }
        }
    }
}
