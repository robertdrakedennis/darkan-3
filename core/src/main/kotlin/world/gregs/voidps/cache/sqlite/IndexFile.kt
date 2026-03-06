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
            connection?.prepareStatement("SELECT DATA FROM cache_index WHERE KEY = 1")
                ?.executeQuery()?.next() ?: false
        } catch (e: SQLException) {
            false
        }
    }

    fun getRaw(id: Int): ByteArray? {
        return try {
            val stmt = connection?.prepareStatement(
                "SELECT DATA FROM cache WHERE KEY = ?"
            ) ?: return null
            stmt.setInt(1, id)
            val result = stmt.executeQuery()
            if (result.next()) result.getBytes("DATA") else null
        } catch (e: SQLException) {
            null
        }
    }

    fun getRawTable(): ByteArray? {
        return try {
            val result = connection?.prepareStatement("SELECT DATA FROM cache_index WHERE KEY = 1")
                ?.executeQuery() ?: return null
            if (result.next()) result.getBytes("DATA") else null
        } catch (e: SQLException) {
            null
        }
    }

    fun getMaxArchive(): Int {
        return try {
            val result = connection?.prepareStatement("SELECT MAX(`KEY`) FROM cache")
                ?.executeQuery() ?: return 0
            if (result.next()) result.getInt(1) else 0
        } catch (e: SQLException) {
            0
        }
    }

    fun exists(id: Int): Boolean {
        return try {
            val stmt = connection?.prepareStatement("SELECT 1 FROM cache WHERE KEY = ?") ?: return false
            stmt.setInt(1, id)
            stmt.executeQuery().next()
        } catch (e: SQLException) {
            false
        }
    }

    fun putRaw(archiveId: Int, data: ByteArray, version: Int, crc: Int) {
        try {
            val stmt = connection?.prepareStatement(
                "INSERT OR REPLACE INTO cache (KEY, DATA, VERSION, CRC) VALUES (?, ?, ?, ?)"
            ) ?: return
            stmt.setInt(1, archiveId)
            stmt.setBytes(2, data)
            stmt.setInt(3, version)
            stmt.setInt(4, crc)
            stmt.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun putRefTable(data: ByteArray, version: Int, crc: Int) {
        try {
            val stmt = connection?.prepareStatement(
                "INSERT OR REPLACE INTO cache_index (KEY, DATA, VERSION, CRC) VALUES (1, ?, ?, ?)"
            ) ?: return
            stmt.setBytes(1, data)
            stmt.setInt(2, version)
            stmt.setInt(3, crc)
            stmt.executeUpdate()
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
