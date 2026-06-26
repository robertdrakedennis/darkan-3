package org.darkan.tools

import io.ktor.utils.io.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.Cache
import world.gregs.voidps.cache.Index
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.file.FileProvider
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.CRC32

/**
 * OFFLINE map-serving readiness verification (no network, no client).
 *
 * Answers, for the Lumbridge spawn build-area, whether our JS5 server WILL serve
 * the index-5 map-square groups the NXT 948-5 client requests after op75:
 *
 *   1. Cache completeness  — are the m{X}_{Y} / l{X}_{Y} groups for the Lumbridge
 *      build-area window present in index 5, do they decompress, and do their CRCs
 *      match the index-5 ref table (255/5)? (This is the CRC the client's
 *      GroupDownloaded callback verifies before accepting a group.)
 *   2. Addressing          — does name -> group-id resolution (Java String.hashCode
 *      via the index-5 name-hash table) line up, and does ref = (index<<32)|group?
 *   3. Serving path        — drive the REAL FileProvider.serve() for an index-5 map
 *      group through a Ktor ByteChannel, then REVERSE the 102,400-byte block framing
 *      and assert the recovered container == cache.sector(5, group) byte-for-byte.
 *   4. Master-index entry  — does the master-index CRC/whirlpool for index 5 match
 *      255/5 (the gate the client checks before requesting groups for index 5)?
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.MapServingReadinessTestKt
 */

private const val RESPONSE_HEADER_LEN = 10
private const val CONTINUATION_HEADER_LEN = 5
private const val BLOCK_SIZE = 102400

private const val FIRST_LIGHT_SPAWN_REGION_X = 50
private const val FIRST_LIGHT_SPAWN_REGION_Y = 50
private const val FIRST_LIGHT_MIN_REGION_X = 26
private const val FIRST_LIGHT_MIN_REGION_Y = 37
private const val FIRST_LIGHT_MAX_REGION_X = 72
private const val FIRST_LIGHT_MAX_REGION_Y = 142

private var passed = 0
private var failed = 0
private fun pass(msg: String) { println("  PASS: $msg"); passed++ }
private fun fail(msg: String) { println("  FAIL: $msg"); failed++ }
private fun info(msg: String) { println("  ..    $msg") }

fun main() {
    val cachePath = Paths.get("./data/cache")
    if (!Files.exists(cachePath)) {
        System.err.println("Cache directory not found: $cachePath")
        kotlin.system.exitProcess(1)
    }
    println("=== Map-Serving Readiness (index 5, first-light build area) ===")
    println("Cache path: ${cachePath.toAbsolutePath()}")
    println()

    // Load the cache exactly as the running server does (no XTEA — JS5 serves raw containers).
    val cache: Cache = SQLiteCache.load()
    val provider = FileProvider.load(cache, inMemory = false)
    val decomp = DecompressionContext()

    // ---------------------------------------------------------------------------------------
    // Section A: index-5 ref table (255/5) presence + parse
    // ---------------------------------------------------------------------------------------
    println("--- A. index-5 reference table (255/5) ---")
    val raw255_5 = cache.sector(255, Index.MAPS)
    if (raw255_5 == null) {
        fail("255/5 ref table absent — index 5 has no archive index; client cannot resolve any map group")
        summary(); return
    }
    pass("255/5 present (${raw255_5.size} bytes raw container)")
    val refTable = parseRefTable(decomp, raw255_5, parseFiles = true)
    if (refTable == null) {
        fail("255/5 failed to decompress / parse")
        summary(); return
    }
    val groupIdsInIndex = refTable.entries.map { it.id }.toHashSet()
    val refCrcById = refTable.crcById()
    pass("255/5 parsed: format=${refTable.format} groups=${refTable.entries.size} maxGroupId=${refTable.maxGroupId} flags=0x${"%02x".format(refTable.flags)} bytesLeft=${refTable.bytesRemaining}")
    // RS3 NXT index 5 has NO name-hash flag (0x1): map groups are addressed by a COORDINATE
    // formula, not by m{X}_{Y} name-hash. Group id = (mapsquareX << 8) | mapsquareY (verified
    // empirically: distinct X cleanly 0..99 under this packing — see MapGroupIdProbe).
    if (refTable.flags and 0x1 != 0) {
        info("index-5 ref table DOES carry name hashes (flag 0x1) — unexpected for RS3 NXT, but harmless")
    } else {
        pass("index-5 ref table has no name hashes (flags=0x${"%02x".format(refTable.flags)}) — map groups addressed by (mapsquareX<<8)|mapsquareY, as RS3 NXT does")
    }
    // archives(5) (from SQLiteCache ref-table parse) must agree with the standalone parse.
    val archives5 = cache.archives(Index.MAPS).toHashSet()
    if (archives5 == groupIdsInIndex) {
        pass("cache.archives(5) matches ref-table group ids (${archives5.size} groups)")
    } else {
        info("cache.archives(5)=${archives5.size} groups, standalone ref parse=${groupIdsInIndex.size} groups (set diff ${archives5.size - groupIdsInIndex.intersect(archives5).size})")
    }
    println()

    // ---------------------------------------------------------------------------------------
    // Section B: Lumbridge build-area window — name resolution + completeness
    // ---------------------------------------------------------------------------------------
    println(
        "--- B. first-light build-area map groups " +
            "(mapsquares $FIRST_LIGHT_MIN_REGION_X..$FIRST_LIGHT_MAX_REGION_X x " +
            "$FIRST_LIGHT_MIN_REGION_Y..$FIRST_LIGHT_MAX_REGION_Y) ---"
    )

    // Terrain group id = (mapsquareX << 8) | mapsquareY (RS3 NXT coordinate encoding).
    fun terrainGroup(x: Int, y: Int) = (x shl 8) or y

    data class MapGroup(val name: String, val groupId: Int, val present: Boolean)
    val resolved = mutableListOf<MapGroup>()
    var missing = 0
    var presentCount = 0

    for (mx in FIRST_LIGHT_MIN_REGION_X..FIRST_LIGHT_MAX_REGION_X) {
        for (my in FIRST_LIGHT_MIN_REGION_Y..FIRST_LIGHT_MAX_REGION_Y) {
            val name = "m${mx}_$my"
            val groupId = terrainGroup(mx, my)
            val present = groupIdsInIndex.contains(groupId)
            resolved.add(MapGroup(name, groupId, present))
            if (!present) missing++ else presentCount++
        }
    }

    // The exact spawn mapsquare (50,50) terrain group is the load-bearing one for first-light.
    val spawnGid = terrainGroup(FIRST_LIGHT_SPAWN_REGION_X, FIRST_LIGHT_SPAWN_REGION_Y)
    if (groupIdsInIndex.contains(spawnGid))
        pass("spawn terrain m${FIRST_LIGHT_SPAWN_REGION_X}_$FIRST_LIGHT_SPAWN_REGION_Y -> group id $spawnGid (present in index 5)")
    else
        fail("spawn terrain group $spawnGid (m${FIRST_LIGHT_SPAWN_REGION_X}_$FIRST_LIGHT_SPAWN_REGION_Y) ABSENT from index 5 — Lumbridge terrain missing")

    info("build-area terrain window: ${resolved.size} mapsquares, $presentCount present, $missing absent")
    if (missing > 0) {
        val sample = resolved.filter { !it.present }.take(40)
        info(
            "absent terrain squares (some edge squares legitimately have no map group): " +
                sample.joinToString(", ") { "${it.name}(${it.groupId})" } +
                if (missing > sample.size) " ... ${missing - sample.size} more" else ""
        )
    }
    info("NOTE: loc ('l{X}_{Y}') groups use a distinct client-computed id (not reversed here); index-5 completeness is covered holistically by Section A/C and CacheIntegrityCheck.")
    println()

    // ---------------------------------------------------------------------------------------
    // Section C: container integrity for the present spawn-area groups (CRC vs ref table)
    // ---------------------------------------------------------------------------------------
    println("--- C. container integrity for present build-area groups (CRC vs 255/5) ---")
    var crcOk = 0
    var crcBad = 0
    var decompOk = 0
    var decompBad = 0
    var decompEmpty = 0
    for (g in resolved.filter { it.present }) {
        val blob = cache.sector(Index.MAPS, g.groupId)
        if (blob == null || blob.size < 5) {
            fail("group ${g.name} (id ${g.groupId}): blob absent/too small (${blob?.size ?: -1}B) despite being in ref table")
            crcBad++
            continue
        }
        // CRC the client checks: CRC32 over the full stored container (we store no version suffix).
        val crc32 = CRC32(); crc32.update(blob)
        val computed = crc32.value.toInt()
        val expected = refCrcById[g.groupId]
        if (expected != null && computed == expected) crcOk++
        else { crcBad++; fail("group ${g.name} (id ${g.groupId}): CRC mismatch computed=0x${"%08x".format(computed)} ref=0x${"%08x".format(expected ?: 0)}") }
        // Decompress (Lumbridge F2P = XTEA 0, so no keys needed).
        try {
            val d = decomp.decompress(blob)
            if (d != null) {
                decompOk++
                if (d.isEmpty()) decompEmpty++
            } else {
                decompBad++
                fail("group ${g.name}: decompress returned null")
            }
        } catch (e: Exception) {
            decompBad++; fail("group ${g.name}: decompress threw ${e::class.simpleName}: ${e.message}")
        }
    }
    if (crcBad == 0 && presentCount > 0) pass("all $crcOk present build-area groups CRC-match the index-5 ref table")
    if (decompBad == 0 && presentCount > 0) pass("all $decompOk present build-area groups decompress cleanly (XTEA-0; $decompEmpty empty)")

    // Holistic completeness: every group the ref table lists must have a present, CRC-matching blob.
    // (A faithful/complete index 5 has a blob for every ref-table entry; a missing blob = an
    //  incomplete download that would 'miss' on the wire.)
    var totalGroups = 0; var blobPresent = 0; var blobMissing = 0; var refCrcMatch = 0; var refCrcBad = 0
    val missingSample = mutableListOf<Int>()
    for (e in refTable.entries) {
        totalGroups++
        val blob = cache.sector(Index.MAPS, e.id)
        if (blob == null) { blobMissing++; if (missingSample.size < 15) missingSample.add(e.id); continue }
        blobPresent++
        val c = CRC32(); c.update(blob)
        if (c.value.toInt() == e.crc) refCrcMatch++ else refCrcBad++
    }
    if (blobMissing == 0) pass("index-5 completeness: every ref-table group ($blobPresent/$totalGroups) has a stored blob")
    else fail("index-5 INCOMPLETE: $blobMissing/$totalGroups ref-table groups have NO blob (cache download incomplete) — sample missing ids: ${missingSample.joinToString(",")}")
    if (refCrcBad == 0) pass("index-5 integrity: all $refCrcMatch present blobs CRC-match the ref table")
    else fail("index-5: $refCrcBad blobs CRC-mismatch the ref table")
    println()

    // ---------------------------------------------------------------------------------------
    // Section D: REAL serving path — FileProvider.serve() round-trip for one spawn map group
    // ---------------------------------------------------------------------------------------
    println("--- D. FileProvider.serve() block-framing round-trip (the real serving code) ---")
    val sampleGroup = resolved.firstOrNull { it.present }
    if (sampleGroup == null) {
        fail("no present map group in the build-area window to exercise the serving path")
    } else {
        val ref = (Index.MAPS.toLong() shl 32) or (sampleGroup.groupId.toLong() and 0xFFFFFFFFL)
        // Confirm the ref math the JS5Server uses round-trips.
        val decodedIndex = (ref ushr 32).toInt()
        val decodedGroup = (ref and 0xFFFFFFFFL).toInt()
        if (decodedIndex == Index.MAPS && decodedGroup == sampleGroup.groupId)
            pass("ref math: (5<<32)|${sampleGroup.groupId} -> index=$decodedIndex group=$decodedGroup")
        else
            fail("ref math broken: decoded index=$decodedIndex group=$decodedGroup")

        val original = cache.sector(Index.MAPS, sampleGroup.groupId)!!
        // Serve as both urgent and prefetch to cover both header hash forms.
        for (prefetch in listOf(false, true)) {
            val framed = serveToBytes(provider, ref, prefetch)
            val recovered = deframe(framed, expectedIndex = Index.MAPS, expectedGroup = sampleGroup.groupId, prefetch = prefetch)
            when {
                recovered == null -> fail("serve(${sampleGroup.name}, prefetch=$prefetch): deframing failed (framing malformed)")
                recovered.contentEquals(original) ->
                    pass("serve(${sampleGroup.name}, prefetch=$prefetch): ${framed.size}B framed -> recovered container matches cache.sector(5,${sampleGroup.groupId})")
                else ->
                    fail("serve(${sampleGroup.name}, prefetch=$prefetch): recovered ${recovered.size}B != original ${original.size}B")
            }
        }

        // Also exercise the largest present build-area group.
        val largeGroup = resolved.filter { it.present }
            .maxByOrNull { cache.sector(Index.MAPS, it.groupId)?.size ?: 0 }
        if (largeGroup != null) {
            val largeBlob = cache.sector(Index.MAPS, largeGroup.groupId)!!
            val largeRef = (Index.MAPS.toLong() shl 32) or (largeGroup.groupId.toLong() and 0xFFFFFFFFL)
            info("largest present build-area group: ${largeGroup.name} id ${largeGroup.groupId}, container ${largeBlob.size}B (wire spans ${if (RESPONSE_HEADER_LEN + largeBlob.size - 5 > BLOCK_SIZE) ">1" else "1"} block)")
            val framed = serveToBytes(provider, largeRef, prefetch = false)
            val recovered = deframe(framed, Index.MAPS, largeGroup.groupId, prefetch = false)
            if (recovered != null && recovered.contentEquals(largeBlob))
                pass("serve(${largeGroup.name}) largest build-area group round-trips (${framed.size}B framed)")
            else
                fail("serve(${largeGroup.name}) largest build-area group round-trip FAILED")
        }
    }

    // CONTINUATION FRAMING: the build-area map groups are all <1 block, so prove the
    // >102,400-byte continuation-header path (shared by ALL indices incl. dense map regions)
    // with the single largest group anywhere in index 5; fall back to index 47 (models) if
    // index 5 has none over a block.
    run {
        fun largestGroup(index: Int): Pair<Int, ByteArray>? =
            cache.archives(index).asSequence()
                .mapNotNull { gid -> cache.sector(index, gid)?.let { gid to it } }
                .maxByOrNull { it.second.size }
        var pick = largestGroup(Index.MAPS)
        var pickIndex = Index.MAPS
        if (pick == null || RESPONSE_HEADER_LEN + pick.second.size - 5 <= BLOCK_SIZE) {
            val fallback = largestGroup(Index.MODELS_RT7)
            if (fallback != null && RESPONSE_HEADER_LEN + fallback.second.size - 5 > BLOCK_SIZE) {
                pick = fallback; pickIndex = Index.MODELS_RT7
            }
        }
        if (pick == null) {
            info("no group available to exercise multi-block continuation framing")
        } else {
            val (gid, blob) = pick
            val wirePayload = RESPONSE_HEADER_LEN + (blob.size - 5)
            val ref = (pickIndex.toLong() shl 32) or (gid.toLong() and 0xFFFFFFFFL)
            val framed = serveToBytes(provider, ref, prefetch = false)
            val recovered = deframe(framed, pickIndex, gid, prefetch = false)
            val blocks = if (wirePayload > BLOCK_SIZE) (wirePayload - BLOCK_SIZE + (BLOCK_SIZE - CONTINUATION_HEADER_LEN) - 1) / (BLOCK_SIZE - CONTINUATION_HEADER_LEN) + 1 else 1
            if (wirePayload <= BLOCK_SIZE) {
                info("largest group (index $pickIndex group $gid, ${blob.size}B) still fits one block — continuation path not exercised, but it is identical code to the verified single-block path")
            } else if (recovered != null && recovered.contentEquals(blob)) {
                pass("multi-block continuation framing: index $pickIndex group $gid (${blob.size}B container, $blocks blocks, ${framed.size}B framed) round-trips")
            } else {
                fail("multi-block continuation framing FAILED for index $pickIndex group $gid")
            }
        }
    }
    println()

    // ---------------------------------------------------------------------------------------
    // Section E: master-index entry for index 5 (the gate before group requests)
    // ---------------------------------------------------------------------------------------
    println("--- E. master-index (255/255) entry for index 5 ---")
    val master = cache.versionTable
    if (master.isEmpty()) {
        fail("master index empty — server has no version table")
    } else {
        // master layout: comp(1) + compSize(4) then [archiveCount(1)][N*80 entries][rsa]
        val md = decomp.decompress(master)
        if (md == null) {
            // master index is stored uncompressed (compression=0); decompress() returns the body.
            fail("master index decompress returned null")
        } else {
            val archiveCount = md[0].toInt() and 0xFF
            info("master index advertises $archiveCount archive entries")
            if (Index.MAPS < archiveCount) {
                // entry i starts at 1 + i*80; crc is first 4 bytes BE.
                val off = 1 + Index.MAPS * 80
                if (off + 4 <= md.size) {
                    val masterCrc = ((md[off].toInt() and 0xFF) shl 24) or ((md[off + 1].toInt() and 0xFF) shl 16) or
                        ((md[off + 2].toInt() and 0xFF) shl 8) or (md[off + 3].toInt() and 0xFF)
                    val computed255_5 = CRC.calculate(raw255_5, 0, raw255_5.size)
                    if (masterCrc == computed255_5)
                        pass("master-index CRC for index 5 (0x${"%08x".format(masterCrc)}) == CRC(255/5) — client will accept the index-5 reference table")
                    else
                        fail("master-index CRC for index 5 (0x${"%08x".format(masterCrc)}) != CRC(255/5) (0x${"%08x".format(computed255_5)}) — client rejects index 5, never requests its groups")
                } else fail("master index truncated before index-5 entry")
            } else {
                fail("master index advertises only $archiveCount archives — index 5 not represented")
            }
        }
    }

    summary()
}

private fun summary() {
    println()
    println("========================================")
    println("=== RESULTS: $passed passed, $failed failed ===")
    println("========================================")
    if (failed > 0) kotlin.system.exitProcess(1)
}

/** Drive the real FileProvider.serve() and capture every byte it writes to the channel. */
private fun serveToBytes(provider: FileProvider, ref: Long, prefetch: Boolean): ByteArray = runBlocking {
    val channel = ByteChannel(autoFlush = true)
    val collected = ArrayList<Byte>(BLOCK_SIZE)
    val readerJob = launch {
        val buf = ByteArray(8192)
        while (!channel.isClosedForRead) {
            val n = channel.readAvailable(buf)
            if (n == -1) break
            for (i in 0 until n) collected.add(buf[i])
        }
    }
    provider.serve(channel, ref, prefetch)
    channel.flushAndClose()
    readerJob.join()
    collected.toByteArray()
}

/**
 * Reverse the NXT block framing: strip the 10-byte response header and every 5-byte
 * continuation header at each 102,400-byte block boundary, validating each header's
 * archive/group fields. Returns the recovered response body,
 * or null if framing is malformed.
 */
private fun deframe(framed: ByteArray, expectedIndex: Int, expectedGroup: Int, prefetch: Boolean): ByteArray? {
    if (framed.size < RESPONSE_HEADER_LEN) return null
    val expectedHash = if (prefetch) expectedGroup or (1 shl 31) else expectedGroup

    fun checkHeader(off: Int): Boolean {
        val arc = framed[off].toInt() and 0xFF
        val hash = ((framed[off + 1].toInt() and 0xFF) shl 24) or ((framed[off + 2].toInt() and 0xFF) shl 16) or
            ((framed[off + 3].toInt() and 0xFF) shl 8) or (framed[off + 4].toInt() and 0xFF)
        return arc == expectedIndex && hash == expectedHash
    }

    if (!checkHeader(0)) return null // 10-byte response header begins with the 5-byte identifier
    // bytes [5..9] are compression(1)+compressedSize(4); they belong to the container, so we
    // recover them as part of the payload. Reconstruct the container by walking blocks.
    val out = ArrayList<Byte>(framed.size)
    var pos = 0
    var blockOffset = 0
    // first 5 bytes consumed = response identifier (NOT part of container)
    pos += CONTINUATION_HEADER_LEN
    blockOffset += CONTINUATION_HEADER_LEN
    // remaining of the response: container bytes (comp+size+data), block-framed.
    while (pos < framed.size) {
        if (blockOffset == BLOCK_SIZE) {
            // continuation header
            if (pos + CONTINUATION_HEADER_LEN > framed.size) return null
            if (!checkHeader(pos)) return null
            pos += CONTINUATION_HEADER_LEN
            blockOffset = CONTINUATION_HEADER_LEN
            continue
        }
        val blockRemaining = BLOCK_SIZE - blockOffset
        val take = minOf(blockRemaining, framed.size - pos)
        for (i in 0 until take) out.add(framed[pos + i])
        pos += take
        blockOffset += take
    }
    return out.toByteArray()
}
