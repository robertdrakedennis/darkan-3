package org.darkan.tools

import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.sqlite.IndexFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Converts an openrs2 directory-format ("flat file store") RS3 cache into the NXT
 * `js5-<N>.jcache` SQLite format that [world.gregs.voidps.cache.sqlite.SQLiteCache.load]
 * reads, so the JS5 server can serve it byte-for-byte to the NXT client.
 *
 * Input layout (per `<inputDir>`):
 *   - `<index>/<group>.dat` — the raw JS5 *served* container for a group, with the
 *     trailing 2-byte version appended (openrs2 stores the on-wire group bytes which
 *     include the version trailer).
 *   - `255/<index>.dat` — the reference-table container for `<index>` (no version trailer).
 *
 * Output (per index `N`, into `<outputDir>`):
 *   - `js5-N.jcache`, populated via [IndexFile.putRaw] for every group and
 *     [IndexFile.putRefTable] for the index's reference table (stored in `cache_index` KEY=1).
 *
 * Blob form — matched exactly to the cache downloader ([org.darkan.tools.cachedownloader]):
 *   - Group `cache.DATA` = container = `compression(1) + compressedSize(4 BE) +
 *     [decompressedSize(4) if compressed] + data` — i.e. the `.dat` bytes with the trailing
 *     2-byte version STRIPPED. The downloader's [org.darkan.tools.cachedownloader.JS5Protocol.ResponseAssembler]
 *     reassembles exactly this (it never reads the trailer), and the JS5 serve path streams
 *     this blob back verbatim, so the group CRCs in the synthesized master index match.
 *   - Group `VERSION`/`CRC` columns come from the index's decoded reference table (the
 *     authoritative source the downloader uses), not from the trailer.
 *   - Ref-table `cache_index.DATA` = the `255/<N>.dat` bytes verbatim (no trailer). The
 *     server's [world.gregs.voidps.cache.secure.VersionTableBuilder] computes the master-index
 *     CRC + whirlpool over exactly these bytes.
 */
object OpenRS2Import {

    /** Per-index conversion outcome for the final report. */
    private data class IndexResult(
        val index: Int,
        val refEntries: Int,
        val groupsWritten: Int,
        val missingDats: List<Int>,
        val crcMismatches: List<Int>,
        val orphanDats: List<Int>,
        val error: String? = null,
    )

    fun convert(inputDir: Path, outputDir: Path) {
        require(Files.isDirectory(inputDir)) { "Input cache dir not found: $inputDir" }
        Files.createDirectories(outputDir)

        val indices = Files.list(inputDir).use { stream ->
            stream.filter { Files.isDirectory(it) }
                .map { it.fileName.toString() }
                .filter { it.toIntOrNull() != null && it != "255" }
                .map { it.toInt() }
                .sorted()
                .toList()
        }
        require(indices.isNotEmpty()) { "No index directories found under $inputDir" }

        val refDir = inputDir.resolve("255")
        require(Files.isDirectory(refDir)) { "No 255/ reference-table directory under $inputDir" }

        println("OpenRS2 -> NXT jcache converter")
        println("  Input:  $inputDir")
        println("  Output: $outputDir")
        println("  Indices: ${indices.size} (${indices.joinToString(",")})")
        println()

        val context = DecompressionContext()
        val results = ArrayList<IndexResult>(indices.size)
        var totalGroups = 0L

        try {
            for (index in indices) {
                val result = convertIndex(context, index, inputDir, refDir, outputDir)
                results.add(result)
                totalGroups += result.groupsWritten
                val tail = buildString {
                    if (result.missingDats.isNotEmpty()) append(", MISSING=${result.missingDats.size}")
                    if (result.crcMismatches.isNotEmpty()) append(", CRC_MISMATCH=${result.crcMismatches.size}")
                    if (result.orphanDats.isNotEmpty()) append(", orphan_dats=${result.orphanDats.size}")
                    if (result.error != null) append(", ERROR=${result.error}")
                }
                println(
                    "  js5-$index.jcache: ${result.groupsWritten}/${result.refEntries} groups + ref table$tail"
                )
            }
        } finally {
            context.close()
        }

        // ---- Summary ----
        println()
        println("=== Conversion summary ===")
        val converted = results.count { it.error == null }
        println("Indices converted:   $converted/${indices.size}")
        println("Total groups written: $totalGroups")

        val failed = results.filter { it.error != null }
        if (failed.isNotEmpty()) {
            println("FAILED indices:")
            for (r in failed) println("  index ${r.index}: ${r.error}")
        }
        val withMissing = results.filter { it.missingDats.isNotEmpty() }
        if (withMissing.isNotEmpty()) {
            println("Indices with missing group .dat files (ref-table entry but no file):")
            for (r in withMissing) {
                val sample = r.missingDats.take(10).joinToString(",")
                println("  index ${r.index}: ${r.missingDats.size} missing [${sample}${if (r.missingDats.size > 10) ",..." else ""}]")
            }
        }
        val withCrc = results.filter { it.crcMismatches.isNotEmpty() }
        if (withCrc.isNotEmpty()) {
            println("Indices with group CRC mismatches (stored blob CRC != ref-table CRC):")
            for (r in withCrc) {
                val sample = r.crcMismatches.take(10).joinToString(",")
                println("  index ${r.index}: ${r.crcMismatches.size} mismatched [${sample}${if (r.crcMismatches.size > 10) ",..." else ""}]")
            }
        } else {
            println("All stored group CRCs match their reference-table CRCs.")
        }
        val withOrphans = results.filter { it.orphanDats.isNotEmpty() }
        if (withOrphans.isNotEmpty()) {
            println("Indices with orphan .dat files (on disk but not in ref table) - not stored:")
            for (r in withOrphans) {
                println("  index ${r.index}: ${r.orphanDats.size} orphan(s)")
            }
        }

        val totalSize = directorySize(outputDir)
        println("Output size: ${formatBytes(totalSize)} ($totalSize bytes)")
    }

    private fun convertIndex(
        context: DecompressionContext,
        index: Int,
        inputDir: Path,
        refDir: Path,
        outputDir: Path,
    ): IndexResult {
        val refRaw = try {
            Files.readAllBytes(refDir.resolve("$index.dat"))
        } catch (e: Exception) {
            return IndexResult(index, 0, 0, emptyList(), emptyList(), emptyList(), "no ref table .dat: ${e.message}")
        }

        val refTable = parseRefTable(context, refRaw)
            ?: return IndexResult(index, 0, 0, emptyList(), emptyList(), emptyList(), "ref table failed to parse/decompress")

        val groupDir = inputDir.resolve(index.toString())
        val datIds: Set<Int> = if (Files.isDirectory(groupDir)) {
            Files.list(groupDir).use { stream ->
                stream.map { it.fileName.toString() }
                    .filter { it.endsWith(".dat") }
                    .map { it.removeSuffix(".dat").toIntOrNull() }
                    .filter { it != null }
                    .map { it!! }
                    .toList()
                    .toSet()
            }
        } else {
            emptySet()
        }

        val missingDats = ArrayList<Int>()
        val crcMismatches = ArrayList<Int>()
        var groupsWritten = 0

        IndexFile(outputDir.resolve("js5-$index.jcache")).use { indexFile ->
            // Reference table -> cache_index KEY=1, stored verbatim (no trailer on ref tables).
            indexFile.putRefTable(refRaw, 0, CRC.calculate(refRaw, 0, refRaw.size))

            for (entry in refTable.entries) {
                val datPath = groupDir.resolve("${entry.id}.dat")
                if (!Files.exists(datPath)) {
                    missingDats.add(entry.id)
                    continue
                }
                val raw = Files.readAllBytes(datPath)
                val blob = stripVersionTrailer(raw)
                val crc = CRC.calculate(blob, 0, blob.size)
                if (crc != entry.crc) {
                    crcMismatches.add(entry.id)
                }
                indexFile.putRaw(entry.id, blob, entry.version, crc)
                groupsWritten++
            }
        }

        // .dat files present on disk but absent from the ref table - the loader iterates
        // ref-table archive IDs, so these would never be served; report but do not store.
        val refIds = refTable.entries.mapTo(HashSet()) { it.id }
        val orphans = (datIds - refIds).sorted()

        return IndexResult(index, refTable.entries.size, groupsWritten, missingDats, crcMismatches, orphans)
    }

    /**
     * openrs2 group `.dat` files append a 2-byte big-endian version to the served container.
     * The stored blob must be the container WITHOUT that trailer (matching the downloader's
     * reassembled container and the bytes the JS5 server streams back).
     *
     * The container length is derivable from its own header:
     *   `5 + compressedSize + (compression != 0 ? 4 : 0)`.
     * We trust that length and drop whatever trails it (normally exactly 2 bytes). If the
     * header implies a length >= the file size (no trailer, as can happen for some stores),
     * the file is already a bare container and is returned unchanged.
     */
    private fun stripVersionTrailer(raw: ByteArray): ByteArray {
        if (raw.size < 5) return raw
        val compression = raw[0].toInt() and 0xFF
        val compressedSize =
            ((raw[1].toInt() and 0xFF) shl 24) or
                ((raw[2].toInt() and 0xFF) shl 16) or
                ((raw[3].toInt() and 0xFF) shl 8) or
                (raw[4].toInt() and 0xFF)
        if (compressedSize < 0 || compressedSize > 50_000_000) return raw // bogus header, leave as-is
        val containerLen = 5 + compressedSize + if (compression != 0) 4 else 0
        if (containerLen <= 0 || containerLen >= raw.size) return raw
        return raw.copyOfRange(0, containerLen)
    }

    private fun directorySize(dir: Path): Long {
        if (!Files.exists(dir)) return 0
        Files.walk(dir).use { stream ->
            return stream.filter { Files.isRegularFile(it) }
                .mapToLong { Files.size(it) }
                .sum()
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "%.1f KB".format(kb)
        val mb = kb / 1024.0
        if (mb < 1024) return "%.1f MB".format(mb)
        return "%.2f GB".format(mb / 1024.0)
    }
}

fun main(args: Array<String>) {
    val inputDir = Paths.get(args.getOrNull(0) ?: "/Users/robert/darkan-3/openrs2-948/cache")
    val outputDir = Paths.get(args.getOrNull(1) ?: "./data/cache")
    OpenRS2Import.convert(inputDir, outputDir)
}
