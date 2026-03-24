package world.gregs.voidps.cache.sqlite

import java.io.Closeable
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class IndexFile(path: Path) : Closeable {

    private var connection: Connection? = null

    init {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$path")
            connection?.prepareStatement("PRAGMA journal_mode=WAL;")?.use { it.executeQuery().close() }
            connection?.prepareStatement("PRAGMA busy_timeout=30000;")?.use { it.executeQuery().close() }
            connection?.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `cache`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
            )?.executeUpdate()
            connection?.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `cache_index`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
            )?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun hasReferenceTable(): Boolean {
        return try {
            connection?.prepareStatement("SELECT DATA FROM cache_index WHERE KEY = 1")?.use { stmt ->
                stmt.executeQuery().use { it.next() }
            } ?: false
        } catch (e: SQLException) {
            false
        }
    }

    fun getRaw(id: Int): ByteArray? {
        return try {
            connection?.prepareStatement("SELECT DATA FROM cache WHERE KEY = ?")?.use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { result ->
                    if (result.next()) result.getBytes("DATA") else null
                }
            }
        } catch (e: SQLException) {
            null
        }
    }

    fun getRawTable(): ByteArray? {
        return try {
            connection?.prepareStatement("SELECT DATA FROM cache_index WHERE KEY = 1")?.use { stmt ->
                stmt.executeQuery().use { result ->
                    if (result.next()) result.getBytes("DATA") else null
                }
            }
        } catch (e: SQLException) {
            null
        }
    }

    fun getMaxArchive(): Int {
        return try {
            connection?.prepareStatement("SELECT MAX(`KEY`) FROM cache")?.use { stmt ->
                stmt.executeQuery().use { result ->
                    if (result.next()) result.getInt(1) else 0
                }
            } ?: 0
        } catch (e: SQLException) {
            0
        }
    }

    fun exists(id: Int): Boolean {
        return try {
            connection?.prepareStatement("SELECT 1 FROM cache WHERE KEY = ?")?.use { stmt ->
                stmt.setInt(1, id)
                stmt.executeQuery().use { it.next() }
            } ?: false
        } catch (e: SQLException) {
            false
        }
    }

    fun allKeys(): Set<Int> {
        return try {
            connection?.prepareStatement("SELECT KEY FROM cache")?.use { stmt ->
                stmt.executeQuery().use { result ->
                    val keys = mutableSetOf<Int>()
                    while (result.next()) {
                        keys.add(result.getInt(1))
                    }
                    keys
                }
            } ?: emptySet()
        } catch (e: SQLException) {
            emptySet()
        }
    }

    /** Returns map of archiveId -> (version, crc) for all stored archives. */
    fun allVersions(): Map<Int, Pair<Int, Int>> {
        return try {
            connection?.prepareStatement("SELECT KEY, VERSION, CRC FROM cache")?.use { stmt ->
                stmt.executeQuery().use { result ->
                    val map = mutableMapOf<Int, Pair<Int, Int>>()
                    while (result.next()) {
                        map[result.getInt(1)] = result.getInt(2) to result.getInt(3)
                    }
                    map
                }
            } ?: emptyMap()
        } catch (e: SQLException) {
            emptyMap()
        }
    }

    fun putRaw(archiveId: Int, data: ByteArray, version: Int, crc: Int) {
        try {
            connection?.prepareStatement(
                "INSERT OR REPLACE INTO cache (KEY, DATA, VERSION, CRC) VALUES (?, ?, ?, ?)"
            )?.use { stmt ->
                stmt.setInt(1, archiveId)
                stmt.setBytes(2, data)
                stmt.setInt(3, version)
                stmt.setInt(4, crc)
                stmt.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun putRefTable(data: ByteArray, version: Int, crc: Int) {
        try {
            connection?.prepareStatement(
                "INSERT OR REPLACE INTO cache_index (KEY, DATA, VERSION, CRC) VALUES (1, ?, ?, ?)"
            )?.use { stmt ->
                stmt.setBytes(1, data)
                stmt.setInt(2, version)
                stmt.setInt(3, crc)
                stmt.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun close() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}
