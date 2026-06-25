package org.darkan.tools

import org.darkan.tools.util.RefTable
import org.darkan.tools.util.parseRefTable
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import java.io.Closeable
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import kotlin.math.min

object NativeJagexCacheImport {

    fun convert(inputDir: Path, outputDir: Path, selectedIndices: Set<Int>?) {
        require(Files.isDirectory(inputDir)) { "Input native cache dir not found: $inputDir" }
        require(Files.isRegularFile(inputDir.resolve("main_file_cache.dat2"))) {
            "Missing main_file_cache.dat2 under $inputDir"
        }
        require(Files.isRegularFile(inputDir.resolve("main_file_cache.idx255"))) {
            "Missing main_file_cache.idx255 under $inputDir"
        }
        Files.createDirectories(outputDir)

        NativeJagexCacheStore(inputDir).use { store ->
            DecompressionContext().use { context ->
                val indices = selectedIndices?.sorted() ?: store.masterIndexIds()
                println("Native Jagex cache -> NXT jcache converter")
                println("  Input:  $inputDir")
                println("  Output: $outputDir")
                println("  Indices: ${indices.size} (${indices.joinToString(",")})")
                println()

                var totalGroups = 0
                var totalMissing = 0
                var totalCrcMismatch = 0
                val failed = ArrayList<String>()

                for (index in indices) {
                    val result = convertIndex(store, context, inputDir, outputDir, index)
                    totalGroups += result.groupsWritten
                    totalMissing += result.missingGroups
                    totalCrcMismatch += result.crcMismatches
                    if (result.error != null) failed.add("index ${result.index}: ${result.error}")
                    val tail = buildString {
                        if (result.missingGroups != 0) append(", MISSING=${result.missingGroups}")
                        if (result.crcMismatches != 0) append(", CRC_MISMATCH=${result.crcMismatches}")
                        if (result.error != null) append(", ERROR=${result.error}")
                    }
                    println("  js5-$index.jcache: ${result.groupsWritten}/${result.refEntries} groups + ref table$tail")
                }

                println()
                println("=== Conversion summary ===")
                println("Total groups written: $totalGroups")
                println("Missing groups:       $totalMissing")
                println("CRC mismatches:       $totalCrcMismatch")
                if (failed.isNotEmpty()) {
                    println("FAILED indices:")
                    failed.forEach { println("  $it") }
                }
                println("Output size: ${formatBytes(directorySize(outputDir))}")
            }
        }
    }

    private fun convertIndex(
        store: NativeJagexCacheStore,
        context: DecompressionContext,
        inputDir: Path,
        outputDir: Path,
        index: Int,
    ): IndexResult {
        val rawRef = try {
            store.read(MASTER_INDEX, index) ?: return IndexResult(index, 0, 0, 0, 0, "missing ref table")
        } catch (e: Exception) {
            return IndexResult(index, 0, 0, 0, 0, "ref read failed: ${e.message}")
        }
        val refTable = parseRefTable(context, rawRef)
            ?: return IndexResult(index, 0, 0, 0, 0, "ref table failed to parse/decompress")

        val outputFile = outputDir.resolve("js5-$index.jcache")
        Files.deleteIfExists(outputFile)

        var written = 0
        var missing = 0
        var crcMismatches = 0

        SqliteIndexWriter(outputFile).use { writer ->
            writer.putRefTable(rawRef, 0, CRC.calculate(rawRef, 0, rawRef.size))
            for (entry in refTable.entries) {
                val rawGroup = try {
                    store.read(index, entry.id)
                } catch (e: Exception) {
                    throw IllegalStateException("index=$index group=${entry.id} read failed: ${e.message}", e)
                }
                if (rawGroup == null) {
                    missing++
                    continue
                }
                val blob = stripVersionTrailer(rawGroup)
                val crc = CRC.calculate(blob, 0, blob.size)
                if (crc != entry.crc) crcMismatches++
                writer.putRaw(entry.id, blob, entry.version, crc)
                written++
            }
        }

        if (indexHasUnexpectedMissingGroups(inputDir, index, refTable, missing)) {
            return IndexResult(index, refTable.entries.size, written, missing, crcMismatches, "native idx has fewer sectors than ref table")
        }
        return IndexResult(index, refTable.entries.size, written, missing, crcMismatches)
    }

    private fun indexHasUnexpectedMissingGroups(
        inputDir: Path,
        index: Int,
        refTable: RefTable,
        missing: Int,
    ): Boolean {
        if (missing == 0) return false
        val indexFile = inputDir.resolve("main_file_cache.idx$index")
        if (!Files.exists(indexFile)) return true
        val possibleEntries = (Files.size(indexFile) / INDEX_ENTRY_SIZE).toInt()
        return refTable.entries.any { it.id >= possibleEntries }
    }

    private fun stripVersionTrailer(raw: ByteArray): ByteArray {
        val containerLen = containerLength(raw) ?: return raw
        if (containerLen >= raw.size) return raw
        return raw.copyOfRange(0, containerLen)
    }

    private fun containerLength(raw: ByteArray): Int? {
        if (raw.size < 5) return null
        val compression = raw[0].toInt() and 0xFF
        val compressedSize = readInt(raw, 1)
        if (compressedSize < 0 || compressedSize > MAX_CONTAINER_SIZE) return null
        val headerSize = if (compression == 0) 5 else 9
        val length = headerSize + compressedSize
        if (length <= 0 || length > raw.size) return null
        return length
    }

    private fun directorySize(dir: Path): Long {
        if (!Files.exists(dir)) return 0L
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

    private data class IndexResult(
        val index: Int,
        val refEntries: Int,
        val groupsWritten: Int,
        val missingGroups: Int,
        val crcMismatches: Int,
        val error: String? = null,
    )

    private const val MASTER_INDEX = 255
    private const val INDEX_ENTRY_SIZE = 6
    private const val MAX_CONTAINER_SIZE = 100_000_000
}

private class NativeJagexCacheStore(private val basePath: Path) : Closeable {
    private val data = RandomAccessFile(basePath.resolve("main_file_cache.dat2").toFile(), "r")
    private val indices = HashMap<Int, RandomAccessFile>()

    fun masterIndexIds(): List<Int> {
        val index = indexFile(255) ?: return emptyList()
        val count = (index.length() / INDEX_ENTRY_SIZE).toInt()
        val ids = ArrayList<Int>(count)
        for (archive in 0 until count) {
            val entry = readIndexEntry(index, archive) ?: continue
            if (entry.length > 0 && entry.sector > 0) ids.add(archive)
        }
        return ids
    }

    fun read(index: Int, archive: Int): ByteArray? {
        val indexFile = indexFile(index) ?: return null
        val entry = readIndexEntry(indexFile, archive) ?: return null
        if (entry.length <= 0 || entry.sector <= 0) return null

        val result = ByteArray(entry.length)
        var written = 0
        var sector = entry.sector
        var chunk = 0

        while (written < result.size) {
            if (sector <= 0) throw IllegalStateException("sector chain ended early")
            val sectorOffset = sector.toLong() * SECTOR_SIZE
            if (sectorOffset + STANDARD_HEADER_SIZE > data.length()) {
                throw IllegalStateException("sector $sector outside dat2")
            }

            data.seek(sectorOffset)
            val sectorBytes = ByteArray(SECTOR_SIZE)
            val read = data.read(sectorBytes)
            if (read < STANDARD_HEADER_SIZE) {
                throw IllegalStateException("short sector $sector")
            }

            val extended = archive > 0xFFFF
            val headerSize = if (extended) EXTENDED_HEADER_SIZE else STANDARD_HEADER_SIZE
            if (read < headerSize) throw IllegalStateException("short sector header $sector")

            val sectorArchive = if (extended) readInt(sectorBytes, 0) else readUnsignedShort(sectorBytes, 0)
            val sectorChunk = if (extended) readUnsignedShort(sectorBytes, 4) else readUnsignedShort(sectorBytes, 2)
            val nextSectorOffset = if (extended) 6 else 4
            val nextSector = readMedium(sectorBytes, nextSectorOffset)
            val sectorIndex = sectorBytes[nextSectorOffset + 3].toInt() and 0xFF

            if (sectorArchive != archive) {
                throw IllegalStateException("sector archive mismatch: expected $archive got $sectorArchive")
            }
            if (sectorChunk != chunk) {
                throw IllegalStateException("sector chunk mismatch: expected $chunk got $sectorChunk")
            }
            if (sectorIndex != index) {
                throw IllegalStateException("sector index mismatch: expected $index got $sectorIndex")
            }

            val payload = min(result.size - written, SECTOR_SIZE - headerSize)
            if (read < headerSize + payload) {
                throw IllegalStateException("short sector payload $sector")
            }
            System.arraycopy(sectorBytes, headerSize, result, written, payload)
            written += payload
            sector = nextSector
            chunk++
        }

        return result
    }

    private fun indexFile(index: Int): RandomAccessFile? {
        return indices.getOrPut(index) {
            val path = basePath.resolve("main_file_cache.idx$index")
            if (!Files.isRegularFile(path)) return null
            RandomAccessFile(path.toFile(), "r")
        }
    }

    private fun readIndexEntry(indexFile: RandomAccessFile, archive: Int): CacheIndexEntry? {
        val offset = archive.toLong() * INDEX_ENTRY_SIZE
        if (offset + INDEX_ENTRY_SIZE > indexFile.length()) return null
        val buffer = ByteArray(INDEX_ENTRY_SIZE)
        indexFile.seek(offset)
        indexFile.readFully(buffer)
        return CacheIndexEntry(readMedium(buffer, 0), readMedium(buffer, 3))
    }

    override fun close() {
        indices.values.forEach { it.close() }
        data.close()
    }

    private data class CacheIndexEntry(val length: Int, val sector: Int)

    companion object {
        private const val SECTOR_SIZE = 520
        private const val STANDARD_HEADER_SIZE = 8
        private const val EXTENDED_HEADER_SIZE = 10
        private const val INDEX_ENTRY_SIZE = 6
    }
}

private class SqliteIndexWriter(path: Path) : Closeable {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:$path")
    private val putRaw: PreparedStatement
    private val putRefTable: PreparedStatement
    private var batchSize = 0

    init {
        connection.prepareStatement("PRAGMA journal_mode=WAL;").use { it.executeQuery().close() }
        connection.prepareStatement("PRAGMA busy_timeout=30000;").use { it.executeQuery().close() }
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS `cache`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
        ).use { it.executeUpdate() }
        connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS `cache_index`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
        ).use { it.executeUpdate() }
        connection.autoCommit = false
        putRaw = connection.prepareStatement(
            "INSERT OR REPLACE INTO cache (KEY, DATA, VERSION, CRC) VALUES (?, ?, ?, ?)"
        )
        putRefTable = connection.prepareStatement(
            "INSERT OR REPLACE INTO cache_index (KEY, DATA, VERSION, CRC) VALUES (1, ?, ?, ?)"
        )
    }

    fun putRefTable(data: ByteArray, version: Int, crc: Int) {
        putRefTable.setBytes(1, data)
        putRefTable.setInt(2, version)
        putRefTable.setInt(3, crc)
        putRefTable.executeUpdate()
    }

    fun putRaw(archiveId: Int, data: ByteArray, version: Int, crc: Int) {
        putRaw.setInt(1, archiveId)
        putRaw.setBytes(2, data)
        putRaw.setInt(3, version)
        putRaw.setInt(4, crc)
        putRaw.addBatch()
        batchSize++
        if (batchSize >= BATCH_SIZE) flush()
    }

    private fun flush() {
        if (batchSize == 0) return
        putRaw.executeBatch()
        connection.commit()
        batchSize = 0
    }

    override fun close() {
        try {
            flush()
            connection.commit()
        } finally {
            putRaw.close()
            putRefTable.close()
            connection.close()
        }
    }

    companion object {
        private const val BATCH_SIZE = 1_000
    }
}

private fun readUnsignedShort(bytes: ByteArray, offset: Int): Int {
    return ((bytes[offset].toInt() and 0xFF) shl 8) or
        (bytes[offset + 1].toInt() and 0xFF)
}

private fun readMedium(bytes: ByteArray, offset: Int): Int {
    return ((bytes[offset].toInt() and 0xFF) shl 16) or
        ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
        (bytes[offset + 2].toInt() and 0xFF)
}

private fun readInt(bytes: ByteArray, offset: Int): Int {
    return ((bytes[offset].toInt() and 0xFF) shl 24) or
        ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
        ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
        (bytes[offset + 3].toInt() and 0xFF)
}

fun main(args: Array<String>) {
    val inputDir = Paths.get(args.getOrNull(0) ?: "/Users/robert/jagexcache/runescape/LIVE")
    val outputDir = Paths.get(args.getOrNull(1) ?: "./build/live-sqlite-cache")
    val selected = args.getOrNull(2)
        ?.takeUnless { it.equals("all", ignoreCase = true) }
        ?.split(",")
        ?.filter { it.isNotBlank() }
        ?.map { it.toInt() }
        ?.toSet()
    NativeJagexCacheImport.convert(inputDir, outputDir, selected)
}
