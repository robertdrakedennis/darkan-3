package org.darkan.tools

import world.gregs.voidps.cache.compress.DecompressionContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.DriverManager
import java.util.zip.DeflaterOutputStream
import kotlin.io.path.name

private data class CacheSeedRow(val key: Int, val data: ByteArray, val version: Int, val crc: Int)
private data class GroupSeedResult(val count: Int, val sample: List<CacheSeedRow>)

fun main(args: Array<String>) {
    val source = Paths.get(args.getOrNull(0) ?: "./data/cache")
    val target = Paths.get(args.getOrNull(1) ?: "/Users/robert/darkan-3/macos/Jagex/RuneScape")
    val indices = args.drop(2).mapNotNull { it.toIntOrNull() }.toSet()

    require(Files.isDirectory(source)) { "Source cache directory not found: $source" }
    Files.createDirectories(target)

    val files = Files.list(source).use { stream ->
        stream
            .filter { it.name.matches(Regex("js5-\\d+\\.jcache")) }
            .map { path ->
                val index = path.name.removePrefix("js5-").removeSuffix(".jcache").toInt()
                index to path
            }
            .filter { (index, _) -> index != 255 && (indices.isEmpty() || index in indices) }
            .sorted(Comparator.comparingInt<Pair<Int, Path>> { it.first })
            .toList()
    }

    DecompressionContext().use { context ->
        for ((index, path) in files) {
            val output = target.resolve(path.name)
            seedIndex(index, path, output, context)
        }
    }
}

private fun seedIndex(index: Int, source: Path, target: Path, context: DecompressionContext) {
    DriverManager.getConnection("jdbc:sqlite:$source").use { src ->
        DriverManager.getConnection("jdbc:sqlite:$target").use { dst ->
            initClientCache(dst)
            val refRows = rows(src, "cache_index").map { it.toClientDiskRow(context) }
            val groupResult: GroupSeedResult

            dst.autoCommit = false
            try {
                dst.prepareStatement("DELETE FROM cache_index").use { it.executeUpdate() }
                dst.prepareStatement("DELETE FROM cache").use { it.executeUpdate() }
                insertRows(dst, "cache_index", refRows)
                groupResult = insertClientGroupRows(src, dst, context)
                dst.commit()
            } catch (e: Exception) {
                dst.rollback()
                throw e
            } finally {
                dst.autoCommit = true
            }

            verifySeed(index, dst, "cache_index", refRows, clientDiskFormat = true)
            verifySeed(index, dst, "cache", groupResult.sample, clientDiskFormat = true)

            println("seeded js5-$index.jcache: refs=${refRows.size} groups=${groupResult.count} -> $target")
        }
    }
}

private fun initClientCache(connection: Connection) {
    connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS cache(KEY INTEGER PRIMARY KEY,DATA BLOB,VERSION INTEGER,CRC INTEGER)"
    ).use { it.executeUpdate() }
    connection.prepareStatement(
        "CREATE TABLE IF NOT EXISTS cache_index(KEY INTEGER PRIMARY KEY,DATA BLOB,VERSION INTEGER,CRC INTEGER)"
    ).use { it.executeUpdate() }
}

private fun rows(connection: Connection, table: String): List<CacheSeedRow> {
    val rows = mutableListOf<CacheSeedRow>()
    connection.prepareStatement("SELECT KEY, DATA, VERSION, CRC FROM $table ORDER BY KEY").use { statement ->
        statement.executeQuery().use { result ->
            while (result.next()) {
                rows += CacheSeedRow(
                    key = result.getInt(1),
                    data = result.getBytes(2),
                    version = result.getInt(3),
                    crc = result.getInt(4),
                )
            }
        }
    }
    return rows
}

private fun insertClientGroupRows(source: Connection, target: Connection, context: DecompressionContext): GroupSeedResult {
    val sample = ArrayList<CacheSeedRow>(3)
    var count = 0
    target.prepareStatement("INSERT INTO cache(KEY, DATA, VERSION, CRC) VALUES (?, ?, ?, ?)").use { insert ->
        source.prepareStatement("SELECT KEY, DATA, VERSION, CRC FROM cache ORDER BY KEY").use { select ->
            select.executeQuery().use { result ->
                while (result.next()) {
                    val row = CacheSeedRow(
                        key = result.getInt(1),
                        data = result.getBytes(2),
                        version = result.getInt(3),
                        crc = result.getInt(4),
                    ).toClientDiskRow(context)
                    insert.setInt(1, row.key)
                    insert.setBytes(2, row.data)
                    insert.setInt(3, row.version)
                    insert.setInt(4, row.crc)
                    insert.addBatch()
                    if (sample.size < 3) sample += row
                    count++
                    if (count % 1_000 == 0) insert.executeBatch()
                }
            }
        }
        insert.executeBatch()
    }
    return GroupSeedResult(count, sample)
}

private fun insertRows(connection: Connection, table: String, rows: List<CacheSeedRow>) {
    connection.prepareStatement("INSERT INTO $table(KEY, DATA, VERSION, CRC) VALUES (?, ?, ?, ?)").use { statement ->
        for (row in rows) {
            statement.setInt(1, row.key)
            statement.setBytes(2, row.data)
            statement.setInt(3, row.version)
            statement.setInt(4, row.crc)
            statement.addBatch()
        }
        statement.executeBatch()
    }
}

private fun verifySeed(
    index: Int,
    connection: Connection,
    table: String,
    sourceRows: List<CacheSeedRow>,
    clientDiskFormat: Boolean = false,
) {
    if (sourceRows.isEmpty()) return
    val sample = sourceRows.take(3)
    connection.prepareStatement("SELECT DATA, VERSION, CRC FROM $table WHERE KEY = ?").use { statement ->
        for (row in sample) {
            statement.setInt(1, row.key)
            statement.executeQuery().use { result ->
                check(result.next()) { "js5-$index $table KEY=${row.key}: missing after seed" }
                val storedData = result.getBytes(1)
                val storedVersion = result.getInt(2)
                val storedCrc = result.getInt(3)

                check(storedData.contentEquals(row.data)) {
                    "js5-$index $table KEY=${row.key}: stored DATA (${storedData.size}B) != expected DATA (${row.data.size}B)"
                }
                check(storedVersion == row.version) {
                    "js5-$index $table KEY=${row.key}: stored VERSION=$storedVersion != source VERSION=${row.version}"
                }
                check(storedCrc == row.crc) {
                    "js5-$index $table KEY=${row.key}: stored CRC=$storedCrc != expected CRC=${row.crc}"
                }
                println(
                    "  verify js5-$index $table KEY=${row.key}: DATA=${storedData.size}B " +
                        "${if (clientDiskFormat) "ZLB" else "verbatim"}, " +
                        "CRC=0x${Integer.toHexString(storedCrc)}, VERSION=$storedVersion OK"
                )
            }
        }
    }
}

private fun CacheSeedRow.toClientDiskRow(context: DecompressionContext): CacheSeedRow {
    val decoded = context.decompress(data) ?: error("failed to decode group KEY=$key")
    return copy(data = zlb(decoded), crc = crc + 1)
}

private fun zlb(decoded: ByteArray): ByteArray {
    val deflated = ByteArrayOutputStream()
    DeflaterOutputStream(deflated).use { it.write(decoded) }
    val compressed = deflated.toByteArray()
    return ByteArray(8 + compressed.size).also {
        it[0] = 'Z'.code.toByte()
        it[1] = 'L'.code.toByte()
        it[2] = 'B'.code.toByte()
        it[3] = 1
        it[4] = (decoded.size shr 24).toByte()
        it[5] = (decoded.size shr 16).toByte()
        it[6] = (decoded.size shr 8).toByte()
        it[7] = decoded.size.toByte()
        compressed.copyInto(it, destinationOffset = 8)
    }
}
