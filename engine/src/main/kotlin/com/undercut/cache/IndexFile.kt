package com.undercut.cache

import java.io.Closeable
import java.io.FileNotFoundException
import java.nio.file.Path
import java.sql.*

class IndexFile(path: Path) : Closeable {

    var table: RefTable? = null
    private var connection: Connection? = null

    init {
        try {
            val driver = Class.forName("org.sqlite.JDBC").getDeclaredConstructor().newInstance() as java.sql.Driver
            DriverManager.registerDriver(driver)
            connection = DriverManager.getConnection("jdbc:sqlite:$path")
            connection?.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `cache`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
            )?.executeUpdate()
            connection?.prepareStatement(
                "CREATE TABLE IF NOT EXISTS `cache_index`(`KEY` INTEGER PRIMARY KEY, `DATA` BLOB, `VERSION` INTEGER, `CRC` INTEGER);"
            )?.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hasReferenceTable(): Boolean {
        return try {
            connection?.prepareStatement("SELECT DATA FROM cache_index WHERE KEY = 1")
                ?.executeQuery()?.next() ?: false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    override fun close() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getMaxArchive(): Int {
        return try {
            val result: ResultSet? = connection?.prepareStatement("SELECT MAX(`KEY`) FROM cache")
                ?.executeQuery()
            if (result?.next() == true) {
                result.getInt(1)
            } else {
                0
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            -1
        }
    }

    fun exists(id: Int): Boolean {
        return try {
            val stmt: PreparedStatement? = connection?.prepareStatement("SELECT 1 FROM cache WHERE KEY = ?")
            stmt?.setInt(1, id)
            stmt?.executeQuery()?.next() == true
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun getRaw(id: Int): ByteArray? {
        return try {
            val stmt: PreparedStatement? = connection?.prepareStatement(
                "SELECT DATA, CRC, VERSION FROM cache WHERE KEY = ?"
            )
            stmt?.setInt(1, id)
            val result: ResultSet? = stmt?.executeQuery()
            if (result?.next() != true) return null
            val crc = result.getInt("CRC")
            val version = result.getInt("VERSION")
            if (crc == 0 || version == 0) throw FileNotFoundException("Archive does not exist.")
            result.getBytes("DATA")
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun getRawTable(): ByteArray? {
        return try {
            val result: ResultSet? = connection?.prepareStatement("SELECT DATA FROM cache_index")?.executeQuery()
            if (result?.next() == true) {
                result.getBytes("DATA")
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }
}