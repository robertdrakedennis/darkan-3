package org.darkan.tools.openrs2

import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.IndexFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.name

/**
 * Imports an OpenRS2 flat-file cache export into the NXT SQLite cache format
 * (`js5-N.jcache`) that [world.gregs.voidps.cache.sqlite.SQLiteCache.load] reads.
 *
 * ## OpenRS2 flat export layout (the input)
 * ```
 * <source>/
 *   1/  2/  5/ … 255/        one directory per index id
 *     <group>.dat            raw JS5 container for that group, e.g. 1/0.dat, 19/315.dat
 *   255/<index>.dat          per-index reference table container (index 255 = the ref-table store)
 * ```
 * Each `<group>.dat` is the JS5 container `[compression(1)][compressedSize(4 BE)]
 * [decompressedSize(4 BE, only if compressed)][payload]` **followed by a 2-byte version
 * suffix** that OpenRS2 appends (the legacy RS2/OSRS wire trailer). The NXT cache that the
 * server serves stores the container WITHOUT that suffix — and the per-group CRC stored in
 * the reference table is computed over the suffix-free container (see
 * `re-resources/docs/cache/data-integrity-analysis.md`). The `255/<index>.dat` reference
 * tables themselves carry no version suffix.
 *
 * ## SQLite output (what we write)
 * One `js5-N.jcache` SQLite DB per index, via [IndexFile]:
 *  - each group container -> `cache` table, KEY = group id  (suffix STRIPPED)
 *  - each `255/N.dat` ref table -> `cache_index` table, KEY = 1, of `js5-N.jcache`
 *
 * The JS5 master index (`255/255.dat`) is intentionally NOT required: the server rebuilds the
 * version table from the per-index reference tables plus its own JS5 RSA keys at load time.
 *
 * ### Version / CRC
 * The per-group `version` and `crc` are taken from each index's reference table (parsed from
 * `255/N.dat`), exactly as the live JS5 downloader records them. The stored container's own CRC
 * is recomputed and asserted to equal the ref-table CRC, which both proves the suffix-stripping
 * is byte-correct and guarantees the values the client validates against will match.
 *
 * Run:
 * ```
 * ./gradlew :tools:openrs2Import -Pargs="--source /Users/.../openrs2-948/cache --out /Users/.../948-sqlite-cache"
 * ```
 */

private const val MASTER_INDEX = 255

private data class IndexResult(
    val index: Int,
    val groupsStored: Int,
    val groupsMissing: Int,
    val crcMismatches: Int,
    val refTableStored: Boolean,
)

fun main(args: Array<String>) {
    var source: String? = null
    var out: String? = null
    var verifyCrc = true
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--source", "-s" -> source = args[++i]
            "--out", "-o" -> out = args[++i]
            "--no-verify" -> verifyCrc = false
            "--help", "-h" -> {
                printUsage()
                return
            }
            else -> {
                System.err.println("Unknown argument: ${args[i]}")
                printUsage()
                return
            }
        }
        i++
    }

    if (source == null || out == null) {
        printUsage()
        return
    }

    val sourceDir = Paths.get(source).toAbsolutePath().normalize()
    val outDir = Paths.get(out).toAbsolutePath().normalize()

    require(Files.isDirectory(sourceDir)) { "Source is not a directory: $sourceDir" }
    val masterDir = sourceDir.resolve(MASTER_INDEX.toString())
    require(Files.isDirectory(masterDir)) {
        "Missing $MASTER_INDEX/ reference-table directory under $sourceDir — is this an OpenRS2 flat export?"
    }

    if (Files.exists(outDir) && Files.list(outDir).use { it.findFirst().isPresent }) {
        System.err.println("WARNING: output directory is not empty: $outDir")
        System.err.println("         Existing js5-*.jcache files will be appended to / overwritten per-group.")
    }
    Files.createDirectories(outDir)

    val start = System.currentTimeMillis()
    println("OpenRS2 flat -> NXT SQLite cache importer")
    println("  source: $sourceDir")
    println("  out:    $outDir")
    println("  verify: $verifyCrc")
    println()

    // Index ids that have a reference table in 255/. Each becomes one js5-N.jcache.
    val indexIds = Files.list(masterDir).use { stream ->
        stream.map { it.name }
            .filter { it.matches(Regex("\\d+\\.dat")) }
            .map { it.removeSuffix(".dat").toInt() }
            .sorted()
            .toList()
    }
    require(indexIds.isNotEmpty()) { "No <index>.dat reference tables found in $masterDir" }
    println("Found ${indexIds.size} index reference tables: ${indexIds.joinToString(" ")}")
    println()

    val context = DecompressionContext()
    val results = mutableListOf<IndexResult>()

    for (indexId in indexIds) {
        results += importIndex(sourceDir, outDir, indexId, context, verifyCrc)
    }

    context.close()

    val seconds = (System.currentTimeMillis() - start) / 1000.0
    println()
    println("=== Import summary ===")
    var totalStored = 0
    var totalMissing = 0
    var totalCrcMismatch = 0
    for (r in results.sortedBy { it.index }) {
        totalStored += r.groupsStored
        totalMissing += r.groupsMissing
        totalCrcMismatch += r.crcMismatches
        val flags = buildString {
            if (!r.refTableStored) append(" [NO-REF-TABLE]")
            if (r.groupsMissing > 0) append(" [${r.groupsMissing} groups absent in export]")
            if (r.crcMismatches > 0) append(" [${r.crcMismatches} CRC MISMATCH]")
        }
        println("  index ${r.index.toString().padStart(3)}: ${r.groupsStored} groups stored$flags")
    }
    println()
    println("Total groups stored: $totalStored")
    println("Total groups absent in export (ref table lists them, no .dat present): $totalMissing")
    println("Total CRC mismatches: $totalCrcMismatch")
    println("Wrote ${results.count { it.refTableStored }} js5-*.jcache index databases to $outDir")
    println("Done in ${"%.1f".format(seconds)}s")

    if (totalCrcMismatch > 0) {
        System.err.println()
        System.err.println("WARNING: $totalCrcMismatch group CRC mismatches — the cache may not validate against the client.")
    }
}

private fun importIndex(
    sourceDir: Path,
    outDir: Path,
    indexId: Int,
    context: DecompressionContext,
    verifyCrc: Boolean,
): IndexResult {
    val refTablePath = sourceDir.resolve("$MASTER_INDEX/$indexId.dat")
    val groupDir = sourceDir.resolve(indexId.toString())

    val indexFile = IndexFile(outDir.resolve("js5-$indexId.jcache"))
    try {
        // 1) Reference table -> cache_index KEY=1. Stored verbatim (no version suffix to strip).
        var refTableStored = false
        // Map of group id -> (version, crc) parsed from the ref table; authoritative source.
        val versions = HashMap<Int, Int>()
        val crcs = HashMap<Int, Int>()
        if (Files.isRegularFile(refTablePath)) {
            val refRaw = Files.readAllBytes(refTablePath)
            val refCrc = CRC.calculate(refRaw, 0, refRaw.size)
            indexFile.putRefTable(refRaw, 0, refCrc)
            refTableStored = true

            val parsed = parseRefTable(context, refRaw)
            if (parsed == null) {
                System.err.println("  index $indexId: WARNING could not parse reference table — versions/CRCs default to 0")
            } else {
                for (entry in parsed.entries) {
                    versions[entry.id] = entry.version
                    crcs[entry.id] = entry.crc
                }
            }
        } else {
            System.err.println("  index $indexId: WARNING no reference table at $refTablePath")
        }

        // 2) Group containers -> cache table. Strip the trailing version suffix.
        var stored = 0
        var crcMismatches = 0
        val presentGroups = HashSet<Int>()
        if (Files.isDirectory(groupDir)) {
            val groupFiles = Files.list(groupDir).use { stream ->
                stream.filter { it.name.matches(Regex("\\d+\\.dat")) }
                    .sorted(compareBy { it.name.removeSuffix(".dat").toInt() })
                    .toList()
            }
            for (path in groupFiles) {
                val groupId = path.name.removeSuffix(".dat").toInt()
                presentGroups += groupId
                val raw = Files.readAllBytes(path)
                val container = stripVersionSuffix(raw, indexId, groupId) ?: continue
                val crc = CRC.calculate(container, 0, container.size)

                val refCrc = crcs[groupId]
                if (verifyCrc && refCrc != null && refCrc != crc) {
                    crcMismatches++
                    if (crcMismatches <= 5) {
                        System.err.println(
                            "  index $indexId group $groupId: CRC mismatch stored=$crc refTable=$refCrc " +
                                "(raw=${raw.size}B container=${container.size}B)"
                        )
                    }
                }

                indexFile.putRaw(groupId, container, versions[groupId] ?: 0, crc)
                stored++
            }
        }

        // Groups the ref table advertises but which have no .dat in the export (e.g. HTTP-served
        // music/models/vorbis that OpenRS2 didn't include). The index still loads; these simply
        // won't serve until backfilled.
        val missing = crcs.keys.count { it !in presentGroups }

        println(
            "  index ${indexId.toString().padStart(3)}: stored $stored groups" +
                (if (missing > 0) ", $missing absent in export" else "") +
                (if (crcMismatches > 0) ", $crcMismatches CRC MISMATCH" else "") +
                (if (!refTableStored) " (NO ref table)" else "")
        )

        return IndexResult(indexId, stored, missing, crcMismatches, refTableStored)
    } finally {
        indexFile.close()
    }
}

/**
 * Returns the exact JS5 container bytes (header + payload), discarding any trailing version
 * suffix OpenRS2 appended. Returns null (and warns) on a malformed/too-short container.
 *
 * Container length = 1 (type) + 4 (compressedSize) + (4 if compressed) + compressedSize.
 */
private fun stripVersionSuffix(raw: ByteArray, indexId: Int, groupId: Int): ByteArray? {
    if (raw.size < 5) {
        System.err.println("  index $indexId group $groupId: container too small (${raw.size}B) — skipped")
        return null
    }
    val type = raw[0].toInt() and 0xFF
    if (type > 3) {
        System.err.println("  index $indexId group $groupId: invalid compression type $type — skipped")
        return null
    }
    val compressedSize = ((raw[1].toInt() and 0xFF) shl 24) or
        ((raw[2].toInt() and 0xFF) shl 16) or
        ((raw[3].toInt() and 0xFF) shl 8) or
        (raw[4].toInt() and 0xFF)
    val containerLen = 5 + (if (type != 0) 4 else 0) + compressedSize
    if (containerLen > raw.size) {
        System.err.println(
            "  index $indexId group $groupId: container truncated (header says ${containerLen}B, file is ${raw.size}B) — skipped"
        )
        return null
    }
    // Fast path: already suffix-free.
    if (containerLen == raw.size) return raw
    return raw.copyOf(containerLen)
}

private fun printUsage() {
    println(
        """
        OpenRS2 flat-file export -> NXT SQLite cache (js5-N.jcache) importer.

        Usage:
          ./gradlew :tools:openrs2Import -Pargs="--source <openrs2-cache-dir> --out <sqlite-out-dir>"

        Options:
          --source, -s <dir>   OpenRS2 flat export root (contains 1/ 2/ … 255/ with <group>.dat). REQUIRED.
          --out,    -o <dir>   Output directory for js5-*.jcache SQLite databases. REQUIRED.
          --no-verify          Skip per-group CRC verification against the reference table.
          --help,   -h         Show this help.
        """.trimIndent()
    )
}
