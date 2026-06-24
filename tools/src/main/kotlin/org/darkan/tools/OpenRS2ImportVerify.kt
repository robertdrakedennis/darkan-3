package org.darkan.tools

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.math.BigInteger
import java.nio.file.Paths

/**
 * Verifies a converted `js5-*.jcache` cache loads through [SQLiteCache.load] and is internally
 * consistent: ref tables present for every index, the master version table builds without CRC
 * errors, and a sample data group decompresses. Exits non-zero on any failed assertion.
 */
fun main(args: Array<String>) {
    val path = Paths.get(args.getOrNull(0) ?: "./data/cache")
    val expectedIndices = args.getOrNull(1)?.toIntOrNull() ?: 45

    val exponent = BigInteger(EnvVars.js5RsaExponent)
    val modulus = BigInteger(EnvVars.js5RsaModulus)

    println("Verifying converted cache at $path")
    println("  Using dev JS5 keys (exponent ${EnvVars.js5RsaExponent.length} digits, modulus ${modulus.bitLength()} bits)")
    println()

    var failures = 0
    fun check(name: String, ok: Boolean, detail: String = "") {
        if (ok) {
            println("  PASS  $name${if (detail.isNotEmpty()) " - $detail" else ""}")
        } else {
            println("  FAIL  $name${if (detail.isNotEmpty()) " - $detail" else ""}")
            failures++
        }
    }

    val cache = SQLiteCache.load(path, exponent, modulus)

    // 1. Loaded without throwing (we got here) and index count is as expected.
    val indexCount = cache.indexCount()
    check("indexCount() ~= openrs2 index count", indexCount == expectedIndices, "indexCount()=$indexCount expected=$expectedIndices")

    // 2. Every loaded index has a non-null ref table (sector(255, idx) != null).
    val indices = cache.indices()
    var missingRef = 0
    for (idx in indices) {
        if (cache.sector(255, idx) == null) {
            missingRef++
            println("    index $idx has NULL ref table")
        }
    }
    check("sector(255, idx) non-null for every loaded index", missingRef == 0, "${indices.size} indices, $missingRef missing ref tables")

    // 3. Master version table built (non-empty) - this exercises the per-index CRC + whirlpool
    //    and the RSA signature; an inconsistent ref-table blob would throw or yield 0 bytes here.
    val versionTableSize = cache.versionTable.size
    check("master version table built", versionTableSize > 6 + indexCount * 80, "${versionTableSize} bytes")

    // 4. Index CRCs computed for every index (no zero CRC, which would mean a missing sector).
    val crcs = cache.indexCrcs()
    val zeroCrcs = crcs.count { it == 0 }
    check("index CRCs all non-zero", zeroCrcs == 0, "${crcs.size} CRCs, $zeroCrcs zero")

    // 5. Sample data groups decompress. Index 2 = CONFIGS; pull a few small single-file groups.
    fun tryGroup(index: Int, archive: Int, file: Int = 0): String {
        return try {
            val data = cache.data(index, archive, file)
            if (data == null) "null" else "${data.size} bytes"
        } catch (e: Exception) {
            "EXC ${e::class.simpleName}: ${e.message}"
        }
    }

    // A config group through the loader (underlays, archive 1).
    val cfg = cache.data(Index.CONFIGS, 1)
    check("cache.data(CONFIGS=2, 1) decompresses", cfg != null && cfg.isNotEmpty(), "result=${tryGroup(Index.CONFIGS, 1)}")

    println("  group samples (loader cache.data):")
    println("    data(2, 5)   -> ${tryGroup(2, 5)}")
    println("    data(2, 35)  -> ${tryGroup(2, 35)}")
    println("    data(17, 0)  -> ${tryGroup(17, 0)}")

    // Multi-file group through the loader: ITEMS=19 archive 0, file 0 (item def 0). This exercises
    // parseMultiFileArchive's trailing-stripe split end-to-end.
    val item0 = cache.data(Index.ITEMS, 0, 0)
    check("cache.data(ITEMS=19, 0, file 0) splits + decompresses", item0 != null && item0.isNotEmpty(), "result=${tryGroup(Index.ITEMS, 0, 0)}")
    println("    data(19, 0, file 5)  -> ${tryGroup(Index.ITEMS, 0, 5)}")

    // Belt-and-suspenders: independently confirm the stored blob is a byte-valid 256-file group
    // via the canonical trailing-stripe layout (does not rely on the loader).
    val itemsOk = verifyMultiFileBlobIntegrity(cache, Index.ITEMS, 0)
    check("ITEMS group (19,0) stored blob is a valid 256-file group", itemsOk.first, itemsOk.second)

    cache.close()

    println()
    if (failures == 0) {
        println("VERIFICATION PASSED: $indexCount indices, version table $versionTableSize bytes.")
    } else {
        println("VERIFICATION FAILED: $failures assertion(s) failed.")
        System.exit(1)
    }
}

/**
 * Reads the raw stored container for a multi-file group via [Cache.sector], decompresses it, and
 * splits it with the canonical RS3 trailing-stripe layout (last byte = chunk count; preceding
 * `chunks * fileCount * 4` ints are per-chunk per-file size deltas). Returns (ok, detail) where ok
 * means the decoded file sizes exactly fill the payload region - proving the stored blob is a
 * byte-valid group. This deliberately bypasses SQLiteCache.parseMultiFileArchive.
 */
private fun verifyMultiFileBlobIntegrity(
    cache: world.gregs.voidps.cache.Cache,
    index: Int,
    archive: Int,
): Pair<Boolean, String> {
    val raw = cache.sector(index, archive) ?: return false to "sector($index,$archive) null"
    val ctx = world.gregs.voidps.cache.compress.DecompressionContext()
    try {
        val dec = ctx.decompress(raw) ?: return false to "decompress returned null"
        val fileCount = cache.fileCount(index, archive)
        if (fileCount <= 1) return false to "fileCount=$fileCount (not multi-file)"
        val r = world.gregs.voidps.buffer.read.BufferReader(dec)
        var off = dec.size
        val chunks = dec[--off].toInt() and 0xFF
        val tableStart = off - chunks * fileCount * 4
        if (tableStart < 0) return false to "bad table offset (chunks=$chunks fileCount=$fileCount dec=${dec.size})"
        r.position(tableStart)
        val sizes = IntArray(fileCount)
        for (c in 0 until chunks) {
            var prev = 0
            for (f in 0 until fileCount) {
                prev += r.readInt()
                sizes[f] += prev
            }
        }
        val total = sizes.sum()
        val ok = total == tableStart && sizes.all { it >= 0 }
        return ok to "fileCount=$fileCount chunks=$chunks decompressed=${dec.size} fileBytes=$total payload=$tableStart nonzeroFiles=${sizes.count { it > 0 }}"
    } finally {
        ctx.close()
    }
}
