package world.gregs.voidps.cache.sqlite

import world.gregs.voidps.buffer.write.BufferWriter
import world.gregs.voidps.cache.secure.CRC
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.util.zip.Deflater
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * End-to-end guard for reading the NXT client's own on-disk cache: a multi-file group is stored as a
 * ZLB blob whose inflated payload is the offset-table container
 * `[version 0x01][(N+1) u32 BE absolute offsets][file data]`
 * (`re-resources/docs/cache/sqlite-disk-blob-format.md`). [SQLiteCache.data] must ZLB-unwrap, then
 * split the offset table into the original files. A prior reader produced empty files here (the
 * NpcLoad BufferUnderflow / invisible-avatar bug); this test builds a real temp `js5-N.jcache` and
 * asserts every file comes back byte-exact.
 */
class DiskGroupSplitTest {

    private val tmp = Files.createTempDirectory("disk-group-split")

    @AfterTest
    fun cleanup() {
        Files.walk(tmp).sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
    }

    @Test
    fun `splits a ZLB offset-table multi-file group into its files`() {
        val index = 18
        val archive = 0
        val files = listOf(
            Random(1).nextBytes(104),
            Random(2).nextBytes(151),
            Random(3).nextBytes(0),   // empty file is legal (off[i] == off[i+1])
            Random(4).nextBytes(255),
        )
        roundTrip(index, archive, files)
    }

    @Test
    fun `single-file disk groups are read whole (no offset table)`() {
        // The client stores 1-file groups bare (the JS5 "payload IS the container" path; verified
        // empirically over 30k single-file groups). data() returns the whole ZLB payload as file 0.
        val index = 17
        val archive = 7
        val file0 = Random(5).nextBytes(73)
        val dir = tmp.resolve("single")
        Files.createDirectories(dir)
        IndexFile(dir.resolve("js5-$index.jcache")).use { idx ->
            idx.putRefTable(noneContainer(refTable(archive, fileCount = 1)), 0, 0)
            val blob = zlb(file0) // bare file, no offset-table wrapper
            idx.putRaw(archive, blob, 0, CRC.calculate(blob, 0, blob.size))
        }
        val cache = SQLiteCache.load(dir)
        try {
            val got = cache.data(index, archive, 0)
            assertNotNull(got)
            assertContentEquals(file0, got)
        } finally {
            cache.close()
        }
    }

    private fun roundTrip(index: Int, archive: Int, files: List<ByteArray>) {
        val dir = tmp.resolve("g$index-$archive")
        Files.createDirectories(dir)
        IndexFile(dir.resolve("js5-$index.jcache")).use { idx ->
            idx.putRefTable(noneContainer(refTable(archive, fileCount = files.size)), 0, 0)
            val container = offsetTableContainer(files)
            val blob = zlb(container)
            idx.putRaw(archive, blob, 0, CRC.calculate(blob, 0, blob.size))
        }
        val cache = SQLiteCache.load(dir)
        try {
            assertEquals(files.size, cache.fileCount(index, archive), "fileCount from ref table")
            for (i in files.indices) {
                val got = cache.data(index, archive, i)
                assertNotNull(got, "file $i must decode")
                assertContentEquals(files[i], got, "file $i bytes")
            }
        } finally {
            cache.close()
        }
    }

    // ---- builders ----

    /** `[version 0x01][(N+1) u32 BE absolute offsets][file data]` (jag::Js5Group::Repack output). */
    private fun offsetTableContainer(files: List<ByteArray>): ByteArray {
        val n = files.size
        val headerSize = 1 + (n + 1) * 4
        val w = BufferWriter(headerSize + files.sumOf { it.size })
        w.writeByte(0x01)
        var offset = headerSize
        w.writeInt(offset) // off[0]
        for (f in files) {
            offset += f.size
            w.writeInt(offset)
        }
        for (f in files) w.writeBytes(f)
        return w.toArray()
    }

    /** Wrap [payload] in the ZLB disk blob: `['Z''L''B'][0x01][size u32 BE][zlib stream]`. */
    private fun zlb(payload: ByteArray): ByteArray {
        val out = ByteArrayOutputStream()
        out.write('Z'.code); out.write('L'.code); out.write('B'.code); out.write(0x01)
        out.write((payload.size ushr 24) and 0xFF)
        out.write((payload.size ushr 16) and 0xFF)
        out.write((payload.size ushr 8) and 0xFF)
        out.write(payload.size and 0xFF)
        val d = Deflater(Deflater.DEFAULT_COMPRESSION, false)
        d.setInput(payload); d.finish()
        val buf = ByteArray(payload.size + 64)
        while (!d.finished()) out.write(buf, 0, d.deflate(buf))
        d.end()
        return out.toByteArray()
    }

    /** Wrap [data] as a type-0 (NONE) JS5 container: `[0][len u32 BE][data]`. */
    private fun noneContainer(data: ByteArray): ByteArray {
        val w = BufferWriter(5 + data.size)
        w.writeByte(0) // NONE
        w.writeInt(data.size)
        w.writeBytes(data)
        return w.toArray()
    }

    /**
     * Minimal format-7 reference table for a single [archive] holding one group of [fileCount] files
     * (delta file ids 0,1,2,…). Mirrors exactly what [SQLiteCache] parseRefTable reads: format,
     * revision, flags, archive count, delta ids, crcs, versions, file counts, delta file ids.
     */
    private fun refTable(archive: Int, fileCount: Int): ByteArray {
        val w = BufferWriter(64)
        w.writeByte(7)          // format 7
        w.writeInt(1)           // revision
        w.writeByte(0)          // flags: no names, no whirlpool, no sizes, no extra hashes
        writeBigSmart(w, 1)     // archive count = 1
        writeBigSmart(w, archive) // delta id (first id == absolute)
        w.writeInt(0)           // CRC (unused by parse)
        w.writeInt(0)           // version
        writeBigSmart(w, fileCount) // file count for this archive
        // delta-encoded file ids 0,1,2,... -> first delta = 0, rest = 1
        writeBigSmart(w, 0)
        repeat(fileCount - 1) { writeBigSmart(w, 1) }
        return w.toArray()
    }

    /** bigSmart write (gSmart2or4s): value < 0x8000 -> 2 bytes; else 4 bytes with top bit set. */
    private fun writeBigSmart(w: BufferWriter, value: Int) {
        if (value < 0x8000) {
            w.writeShort(value)
        } else {
            w.writeInt(value or (1 shl 31))
        }
    }
}
