package org.darkan.tools

import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.secure.RSA
import world.gregs.voidps.cache.secure.Whirlpool
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.math.BigInteger
import java.nio.file.Paths

/**
 * CRC/Whirlpool Consistency Test
 *
 * Verifies that the CRC and Whirlpool values stored in the master index match
 * the actual raw container bytes served for each archive index via sector(255, N).
 *
 * This is the exact check the NXT client performs in IndexDownloaded:
 *   1. CRC32 over entire raw container bytes
 *   2. Whirlpool over entire raw container bytes
 *   3. Compare against master index entries
 *
 * Also performs RSA round-trip verification on the master index signature.
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.CRCConsistencyTestKt
 */

private const val ENTRY_SIZE = 80
private const val WHIRLPOOL_SIZE = 64

fun main() {
    val cachePath = Paths.get("./data/cache")
    println("=== CRC Consistency Test ===")
    println("Cache path: $cachePath")
    println()

    // Load cache exactly as the lobby server does (with RSA signing)
    val envExponent = System.getenv("RSA_JS5_EXPONENT")
    val envModulus = System.getenv("RSA_JS5_MODULUS")

    // Use EnvVars defaults if env not set
    val exponent = BigInteger(envExponent ?: org.darkan.core.EnvVars.js5RsaExponent)
    val modulus = BigInteger(envModulus ?: org.darkan.core.EnvVars.js5RsaModulus)

    println("RSA modulus bits: ${modulus.bitLength()}")
    println("RSA exponent bits: ${exponent.bitLength()}")
    println()

    val cache = SQLiteCache.load(cachePath, exponent, modulus)

    // The built master index (version table) - this is what gets served as 255/255
    val masterIndexRaw = cache.versionTable
    println("Master index size: ${masterIndexRaw.size} bytes")

    // Parse master index container header
    val compression = masterIndexRaw[0].toInt() and 0xFF
    val compressedSize = readBEInt(masterIndexRaw, 1)
    println("  compression: $compression (should be 0)")
    println("  compressedSize: $compressedSize")

    // Payload starts at offset 5 (after container header)
    val archiveCount = masterIndexRaw[5].toInt() and 0xFF
    println("  archiveCount: $archiveCount")
    println()

    // Parse master index entries
    data class MasterEntry(
        val crc: Int,
        val version: Int,
        val fileCount: Int,
        val uncompSize: Int,
        val whirlpool: ByteArray
    )

    val entries = mutableMapOf<Int, MasterEntry>()
    for (i in 0 until archiveCount) {
        val off = 6 + i * ENTRY_SIZE  // 5 bytes container header + 1 byte archiveCount
        if (off + ENTRY_SIZE > masterIndexRaw.size) {
            println("WARNING: master index too short for entry $i at offset $off")
            break
        }
        val crc = readBEInt(masterIndexRaw, off)
        val ver = readBEInt(masterIndexRaw, off + 4)
        val fc = readBEInt(masterIndexRaw, off + 8)
        val us = readBEInt(masterIndexRaw, off + 12)
        val wp = masterIndexRaw.copyOfRange(off + 16, off + 16 + WHIRLPOOL_SIZE)
        entries[i] = MasterEntry(crc, ver, fc, us, wp)
    }

    // Test each archive index
    var passed = 0
    var failed = 0
    var skipped = 0

    println("=== Testing ${entries.size} archive indices ===")
    println()

    for ((idx, entry) in entries.toSortedMap()) {
        val isZero = entry.crc == 0 && entry.version == 0 && entry.fileCount == 0 &&
                entry.uncompSize == 0 && entry.whirlpool.all { it == 0.toByte() }

        if (isZero) {
            skipped++
            continue
        }

        // Get raw container bytes exactly as FileProvider.data(255, idx) returns
        val rawData = cache.sector(255, idx)
        if (rawData == null) {
            println("  FAIL Archive $idx: sector(255, $idx) returned null but master index has non-zero entry")
            println("       Master: CRC=0x${"%08x".format(entry.crc)} ver=${entry.version} files=${entry.fileCount}")
            failed++
            continue
        }

        // Compute CRC32 over ALL raw bytes (same as java.util.zip.CRC32)
        val computedCrc = CRC.calculate(rawData, 0, rawData.size)

        // Compute Whirlpool over ALL raw bytes
        val wp = Whirlpool()
        wp.add(rawData)
        val computedWp = ByteArray(WHIRLPOOL_SIZE)
        wp.finalize(computedWp)

        val crcMatch = computedCrc == entry.crc
        val wpMatch = computedWp.contentEquals(entry.whirlpool)

        if (crcMatch && wpMatch) {
            println("  PASS Archive $idx: CRC=0x${"%08x".format(entry.crc)} size=${rawData.size}B")
            passed++
        } else {
            println("  FAIL Archive $idx:")
            if (!crcMatch) {
                println("       CRC MISMATCH: computed=0x${"%08x".format(computedCrc)} master=0x${"%08x".format(entry.crc)}")
                // Show raw data header for debugging
                val header = rawData.take(10).joinToString(" ") { "%02x".format(it) }
                println("       Raw data header (first 10 bytes): $header")
                println("       Raw data size: ${rawData.size} bytes")
            }
            if (!wpMatch) {
                println("       WHIRLPOOL MISMATCH:")
                println("         computed: ${computedWp.joinToString("") { "%02x".format(it) }}")
                println("         master:   ${entry.whirlpool.joinToString("") { "%02x".format(it) }}")
            }
            failed++
        }
    }

    println()
    println("=== RSA Signature Verification ===")

    // The RSA signature block is after the entries
    val rsaStart = 6 + archiveCount * ENTRY_SIZE
    val rsaBlock = masterIndexRaw.copyOfRange(rsaStart, 5 + compressedSize)
    println("RSA block: offset=$rsaStart size=${rsaBlock.size} bytes")

    // Decrypt with PUBLIC key (exponent 65537)
    val publicExponent = BigInteger.valueOf(65537)
    val decrypted = RSA.crypt(rsaBlock, modulus, publicExponent)
    println("Decrypted RSA block: ${decrypted.size} bytes")

    if (decrypted.size == 65) {
        println("  Size: OK (65 bytes)")
    } else {
        println("  Size: UNEXPECTED (${decrypted.size}, expected 65)")
    }

    if (decrypted.isNotEmpty()) {
        val marker = decrypted[0].toInt() and 0xFF
        if (marker == 0x0A) {
            println("  Marker byte: OK (0x0A)")
        } else {
            // Could be 0x01 based on some docs
            println("  Marker byte: 0x${"%02x".format(marker)} (expected 0x0A)")
        }

        // The remaining 64 bytes should be Whirlpool of [archiveCount + entries]
        val hashStart = 5  // archiveCount byte
        val hashLen = rsaStart - hashStart
        val expectedWp = Whirlpool()
        expectedWp.add(masterIndexRaw, hashStart, hashLen)
        val expectedWpOut = ByteArray(WHIRLPOOL_SIZE)
        expectedWp.finalize(expectedWpOut)

        val signedWp = decrypted.copyOfRange(1, minOf(65, decrypted.size))
        val sigMatch = signedWp.contentEquals(expectedWpOut)
        if (sigMatch) {
            println("  Whirlpool in signature: MATCHES entries hash")
        } else {
            println("  Whirlpool in signature: MISMATCH")
            println("    Signed:   ${signedWp.joinToString("") { "%02x".format(it) }}")
            println("    Expected: ${expectedWpOut.joinToString("") { "%02x".format(it) }}")
        }
    }

    println()
    println("========================================")
    println("RESULTS: $passed passed, $failed failed, $skipped skipped (zero entries)")
    println("========================================")

    // Additional diagnostic: compare what VersionTableBuilder computed vs what sector() returns
    if (failed > 0) {
        println()
        println("=== Diagnostic: Checking VersionTableBuilder vs sector() data flow ===")
        println()
        println("The VersionTableBuilder.sector() method computes CRC and Whirlpool over the")
        println("raw data from IndexFile.getRawTable(). The master index stores these values.")
        println("FileProvider.data(255, N) calls cache.sector(255, N) which calls IndexFile.getRawTable().")
        println()
        println("If there's a mismatch, it means either:")
        println("  1. getRawTable() returns different data at build time vs serve time")
        println("  2. The CRC/Whirlpool computation differs between build and verification")
        println("  3. The master index entry positions are wrong")
        println()

        // Double check: re-compute from scratch for first failing archive
        for ((idx, entry) in entries.toSortedMap()) {
            val isZero = entry.crc == 0 && entry.version == 0
            if (isZero) continue
            val rawData = cache.sector(255, idx) ?: continue
            val computedCrc = CRC.calculate(rawData, 0, rawData.size)
            if (computedCrc != entry.crc) {
                println("First mismatch at archive $idx:")
                println("  sector(255,$idx) returns ${rawData.size} bytes")
                println("  CRC of that data: 0x${"%08x".format(computedCrc)}")
                println("  Master index says: 0x${"%08x".format(entry.crc)}")

                // Check if maybe the CRC was computed over a subset
                if (rawData.size >= 5) {
                    val crcNoHeader = CRC.calculate(rawData, 5, rawData.size)
                    println("  CRC without container header (offset 5..end): 0x${"%08x".format(crcNoHeader)}")
                    if (crcNoHeader == entry.crc) {
                        println("  >>> FOUND IT: Master index CRC was computed WITHOUT the 5-byte container header!")
                    }
                }

                // Also check java.util.zip.CRC32 vs custom CRC
                val javaCrc = java.util.zip.CRC32()
                javaCrc.update(rawData)
                val javaCrcVal = javaCrc.value.toInt()
                println("  java.util.zip.CRC32 over all bytes: 0x${"%08x".format(javaCrcVal)}")
                if (javaCrcVal != computedCrc) {
                    println("  >>> WARNING: Custom CRC.calculate differs from java.util.zip.CRC32!")
                    if (javaCrcVal == entry.crc) {
                        println("  >>> FOUND IT: Master index uses java.util.zip.CRC32 but serving uses custom CRC!")
                    }
                }
                break
            }
        }
    }

    if (failed > 0) System.exit(1)
}

private fun readBEInt(data: ByteArray, offset: Int): Int =
    ((data[offset].toInt() and 0xFF) shl 24) or
    ((data[offset + 1].toInt() and 0xFF) shl 16) or
    ((data[offset + 2].toInt() and 0xFF) shl 8) or
    (data[offset + 3].toInt() and 0xFF)
