package org.darkan.tools

import world.gregs.voidps.buffer.read.BufferReader
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.math.BigInteger
import java.nio.file.Paths
import java.util.zip.CRC32

/**
 * Focused diagnostic for the archive-40 group-38557 rejection loop.
 *
 * The macOS 948-5 client requests /ms?a=40&g=38557&c=-2015981693&v=1544449208 3,023
 * times and rejects every response. The echoed c=/v= come from index 40's REF TABLE
 * (255/40), which the client downloaded and parsed. This tool:
 *   1. Parses index 40's ref table fully (per-group CRC + version arrays).
 *   2. Prints the expected CRC/version the ref table holds for group 38557.
 *   3. Reads the raw group container sector(40, 38557) and computes its actual CRC
 *      (over full bytes AND full-minus-2), version, decompressibility.
 *   4. Compares every value to the client's echoed c=-2015981693 / v=1544449208.
 *   5. Dumps the master-index (255/255) entry for archive 40 and the 67-vs-45 details.
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.Archive40GroupDiagKt
 */

private const val TARGET_INDEX = 40
private const val TARGET_GROUP = 38557
private const val ECHOED_CRC = -2015981693    // 0x87D8E2C3 as signed int
private const val ECHOED_VERSION = 1544449208
private const val ENTRY_SIZE = 80
private const val WHIRLPOOL_SIZE = 64

private fun crc32(data: ByteArray, offset: Int = 0, len: Int = data.size): Int {
    val c = CRC32()
    c.update(data, offset, len)
    return c.value.toInt()
}

private fun hex(v: Int) = "0x${"%08x".format(v)}"

fun main() {
    val cachePath = Paths.get("./data/cache")
    val exponent = BigInteger(org.darkan.core.EnvVars.js5RsaExponent)
    val modulus = BigInteger(org.darkan.core.EnvVars.js5RsaModulus)

    println("=== Archive 40 / Group 38557 rejection-loop diagnostic ===")
    println("Cache path: ${cachePath.toAbsolutePath()}")
    println("Client echoed: c=$ECHOED_CRC (${hex(ECHOED_CRC)}) v=$ECHOED_VERSION")
    println()

    val cache = SQLiteCache.load(cachePath, exponent, modulus)
    val ctx = DecompressionContext()

    // ── Index 40 ref table (255/40) — the source of the echoed c=/v= ──
    val rawTable = cache.sector(255, TARGET_INDEX)
    if (rawTable == null) {
        println("FATAL: sector(255, $TARGET_INDEX) == null — index 40 has NO ref table.")
        println("       But the client got c=/v= for it, so this would be a contradiction.")
        return
    }
    println("Index $TARGET_INDEX ref table (255/$TARGET_INDEX): ${rawTable.size} raw container bytes")
    println("  ref-table CRC32 (full):       ${hex(crc32(rawTable))}")
    val decTable = ctx.decompress(rawTable)
    if (decTable == null) {
        println("FATAL: index 40 ref table failed to decompress.")
        return
    }
    println("  ref-table decompressed:       ${decTable.size} bytes")

    // Parse the ref table fully — replicate SQLiteCache.parseRefTable but KEEP the
    // per-group CRC and version arrays (the real parser skips them).
    val r = BufferReader(decTable)
    val version = r.readUnsignedByte()
    println("  format version: $version")
    var revision = 0
    if (version >= 6) { revision = r.readInt(); println("  ref-table revision: $revision") }
    val flags = r.readUnsignedByte()
    println("  flags: ${hex(flags)} (names=${flags and 1}, digest=${(flags shr 1) and 1}, sizes=${(flags shr 2) and 1}, hash8=${(flags shr 3) and 1})")
    val groupCount = if (version >= 7) r.readBigSmart() else r.readUnsignedShort()
    println("  groupCount: $groupCount")

    var prev = 0
    var highest = 0
    val groupIds = IntArray(groupCount) {
        val id = (if (version >= 7) r.readBigSmart() else r.readUnsignedShort()) + prev
        prev = id
        if (id > highest) highest = id
        id
    }
    println("  group id range: min=${groupIds.minOrNull()} max=$highest  (count=$groupCount)")
    val targetPos = groupIds.indexOf(TARGET_GROUP)
    if (targetPos == -1) {
        println()
        println(">>> Group $TARGET_GROUP is NOT listed in index 40's ref table!")
        println("    The client should not be requesting it. Listing nearby ids…")
        val sorted = groupIds.sorted()
        val near = sorted.filter { it in (TARGET_GROUP - 5)..(TARGET_GROUP + 5) }
        println("    ids near $TARGET_GROUP: $near")
        return
    }
    println("  group $TARGET_GROUP found at ref-table position $targetPos")

    if (flags and 1 != 0) r.skip(groupCount * 4) // name hashes

    // CRCs (one per group)
    val crcs = IntArray(groupCount) { r.readInt() }
    if (flags and 0x8 != 0) r.skip(groupCount * 4) // unknown8/hash8
    if (flags and 0x2 != 0) r.skip(groupCount * WHIRLPOOL_SIZE) // digests
    if (flags and 0x4 != 0) r.skip(groupCount * 8) // sizes
    // Versions (one per group)
    val versions = IntArray(groupCount) { r.readInt() }

    val expectedCrc = crcs[targetPos]
    val expectedVersion = versions[targetPos]
    println()
    println("── Index 40 ref-table entry for group $TARGET_GROUP ──")
    println("  ref-table CRC     = $expectedCrc (${hex(expectedCrc)})")
    println("  ref-table version = $expectedVersion")
    println("  client echoed CRC = $ECHOED_CRC (${hex(ECHOED_CRC)})  ${if (expectedCrc == ECHOED_CRC) "MATCH" else "*** MISMATCH ***"}")
    println("  client echoed ver = $ECHOED_VERSION              ${if (expectedVersion == ECHOED_VERSION) "MATCH" else "*** MISMATCH ***"}")

    // ── The actual served group container sector(40, 38557) ──
    println()
    println("── Served group container sector($TARGET_INDEX, $TARGET_GROUP) ──")
    val group = cache.sector(TARGET_INDEX, TARGET_GROUP)
    if (group == null) {
        println(">>> sector($TARGET_INDEX, $TARGET_GROUP) == null — NO DATA STORED for this group!")
        println("    But the server log shows 87103 container bytes served, so the running")
        println("    server's cache differs from ./data/cache, OR memCache path differs.")
        return
    }
    println("  container size: ${group.size} bytes")
    val compType = group[0].toInt() and 0xFF
    val compLen = ((group[1].toInt() and 0xFF) shl 24) or ((group[2].toInt() and 0xFF) shl 16) or
            ((group[3].toInt() and 0xFF) shl 8) or (group[4].toInt() and 0xFF)
    println("  compression type: $compType  declared compressedSize: $compLen")
    val headerLen = if (compType != 0) 9 else 5
    val expectedContainerLen = headerLen + compLen
    println("  expected container length (header $headerLen + compLen $compLen) = $expectedContainerLen")
    println("  actual length = ${group.size}  diff = ${group.size - expectedContainerLen} (>0 means trailing bytes e.g. version suffix)")

    // CRC the client computes: over the response body MINUS the 2-byte version suffix.
    // We test multiple interpretations to nail exactly which one the ref-table CRC matches.
    val crcFull = crc32(group)
    val crcMinus2 = if (group.size >= 2) crc32(group, 0, group.size - 2) else 0
    val crcContainerOnly = if (group.size >= expectedContainerLen) crc32(group, 0, expectedContainerLen) else -1
    println()
    println("  CRC32 over full container (${group.size}B):            ${hex(crcFull)}")
    println("  CRC32 over container minus 2 (${group.size - 2}B):       ${hex(crcMinus2)}")
    if (crcContainerOnly != -1)
        println("  CRC32 over declared container only (${expectedContainerLen}B):  ${hex(crcContainerOnly)}")
    println()
    println("  Which one equals the ref-table CRC ($expectedCrc / ${hex(expectedCrc)})?")
    println("    full        : ${crcFull == expectedCrc}")
    println("    minus2      : ${crcMinus2 == expectedCrc}")
    if (crcContainerOnly != -1) println("    containerOnly: ${crcContainerOnly == expectedCrc}")
    println("  Which one equals the client's echoed CRC ($ECHOED_CRC)?")
    println("    full        : ${crcFull == ECHOED_CRC}")
    println("    minus2      : ${crcMinus2 == ECHOED_CRC}")
    if (crcContainerOnly != -1) println("    containerOnly: ${crcContainerOnly == ECHOED_CRC}")

    // Decompressibility
    println()
    val decGroup = try { ctx.decompress(group) } catch (e: Exception) { println("  decompress threw: ${e.message}"); null }
    if (decGroup != null) println("  group decompresses OK: ${decGroup.size} bytes")
    else println("  >>> group FAILED to decompress (malformed container?)")

    // ── Master index entry for archive 40 ──
    println()
    println("── Master index (255/255) entry for archive $TARGET_INDEX ──")
    val master = cache.versionTable
    val masterCompLen = ((master[1].toInt() and 0xFF) shl 24) or ((master[2].toInt() and 0xFF) shl 16) or
            ((master[3].toInt() and 0xFF) shl 8) or (master[4].toInt() and 0xFF)
    val archiveCount = master[5].toInt() and 0xFF
    println("  master archiveCount byte: $archiveCount   master size: ${master.size}  compLen: $masterCompLen")
    val off = 6 + TARGET_INDEX * ENTRY_SIZE
    val mCrc = beInt(master, off)
    val mVer = beInt(master, off + 4)
    val mFiles = beInt(master, off + 8)
    val mUncomp = beInt(master, off + 12)
    println("  entry[$TARGET_INDEX]: CRC=$mCrc (${hex(mCrc)}) version=$mVer fileCount=$mFiles uncompSize=$mUncomp")
    println("  CRC of index-40 ref table (full) was ${hex(crc32(rawTable))} — master entry CRC ${if (mCrc == crc32(rawTable)) "MATCHES" else "*** MISMATCH ***"}")
    println("  (master fileCount $mFiles should equal highest groupId+1 = ${highest + 1}: ${if (mFiles == highest + 1) "OK" else "*** MISMATCH ***"})")

    // ── 67 vs 45 indices ──
    println()
    println("── Index-set sizing (67 vs 45) ──")
    println("  cache.indexCount() (indices WITH a ref table): ${cache.indexCount()}")
    println("  master archiveCount byte (= maxIndex+1):       $archiveCount")
    println("  Non-zero master entries (advertised archives):")
    var nonZero = 0
    for (i in 0 until archiveCount) {
        val o = 6 + i * ENTRY_SIZE
        if (o + 16 > master.size) break
        val c = beInt(master, o); val v = beInt(master, o + 4)
        if (c != 0 || v != 0) nonZero++
    }
    println("    $nonZero archives have non-zero CRC/version in the master index")
    println("  -> The 67 entries include zeroed placeholders for indices without a ref table.")
    println("     A zeroed master entry for an index the client never requests is harmless;")
    println("     index 40 IS advertised (CRC=${hex(mCrc)}) and the client DID request it.")
}

private fun beInt(d: ByteArray, o: Int): Int =
    ((d[o].toInt() and 0xFF) shl 24) or ((d[o + 1].toInt() and 0xFF) shl 16) or
    ((d[o + 2].toInt() and 0xFF) shl 8) or (d[o + 3].toInt() and 0xFF)
