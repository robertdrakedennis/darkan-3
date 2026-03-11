package org.darkan.tools

import org.darkan.core.EnvVars
import world.gregs.voidps.cache.compress.DecompressionContext
import world.gregs.voidps.cache.secure.CRC
import world.gregs.voidps.cache.secure.Whirlpool
import world.gregs.voidps.cache.sqlite.SQLiteCache
import java.math.BigInteger
import java.util.zip.CRC32

/**
 * Comprehensive RSA + Master Index Verifier.
 *
 * Run with: ./gradlew :tools:run -PmainClass=org.darkan.tools.RSASelfTestKt
 */

private const val ENTRY_SIZE = 80
private const val PUBLIC_EXPONENT = 65537L

fun main() {
    var passed = 0
    var failed = 0

    fun pass(msg: String) { println("  PASS: $msg"); passed++ }
    fun fail(msg: String) { println("  FAIL: $msg"); failed++ }

    // =====================================================================
    // Step 1-3: Load cache and FileProvider, compare version tables
    // =====================================================================
    println("=".repeat(70))
    println("=== STEP 1: Load Cache and FileProvider ===")
    println("=".repeat(70))

    println("Cache path: ${EnvVars.cachePath}")
    val cache = SQLiteCache.load()
    val versionTable = cache.versionTable
    println("cache.versionTable: ${versionTable.size} bytes")
    pass("Cache loaded, versionTable is ${versionTable.size} bytes")

    // =====================================================================
    // Step 4-5: Parse container header
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 2: Container Header ===")
    println("=".repeat(70))

    val compression = versionTable[0].toInt() and 0xFF
    val compressedSize = readBEInt(versionTable, 1)
    val archiveCount = versionTable[5].toInt() and 0xFF
    println("Container size: ${versionTable.size} bytes")
    println("Compression: $compression (expected 0)")
    println("CompressedSize field: $compressedSize")
    println("ArchiveCount: $archiveCount")

    if (compression != 0) {
        fail("Master index compression is $compression, expected 0")
    } else {
        pass("Master index compression is 0 (none)")
    }

    // Validate compressedSize == containerSize - 5
    val expectedCompressedSize = versionTable.size - 5
    if (compressedSize == expectedCompressedSize) {
        pass("CompressedSize ($compressedSize) matches container size - 5 (${versionTable.size} - 5 = $expectedCompressedSize)")
    } else {
        fail("CompressedSize ($compressedSize) != container size - 5 ($expectedCompressedSize)")
    }

    // =====================================================================
    // Step 6-7: Hex dumps
    // =====================================================================
    println("\n--- Hex Dump ---")
    val first20 = versionTable.take(20).joinToString(" ") { "%02x".format(it) }
    val last20 = versionTable.takeLast(20).joinToString(" ") { "%02x".format(it) }
    println("First 20 bytes: $first20")
    println("Last 20 bytes:  $last20")

    // =====================================================================
    // Step 8: RSA Verification with PUBLIC key
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 3: RSA Verification (Public Key, e=65537) ===")
    println("=".repeat(70))

    val entryDataEnd = 6 + archiveCount * ENTRY_SIZE
    val rsaBlock = versionTable.copyOfRange(entryDataEnd, versionTable.size)
    println("Entry data ends at offset: $entryDataEnd")
    println("RSA block: bytes [$entryDataEnd .. ${versionTable.size}) = ${rsaBlock.size} bytes")
    println("RSA block first 16 bytes: ${rsaBlock.take(16).joinToString(" ") { "%02x".format(it) }}")
    println("RSA block last 16 bytes:  ${rsaBlock.takeLast(16).joinToString(" ") { "%02x".format(it) }}")

    val modulus = BigInteger(EnvVars.js5RsaModulus)
    val publicExp = BigInteger.valueOf(PUBLIC_EXPONENT)
    println("RSA modulus bit length: ${modulus.bitLength()}")
    println("Expected RSA block size for ${modulus.bitLength()}-bit key: ${(modulus.bitLength() + 7) / 8} bytes")

    // --- UNSIGNED BigInteger(1, rsaBlock) ---
    println("\n--- 8a: UNSIGNED BigInteger(1, rsaBlock) ---")
    val ciphertextUnsigned = BigInteger(1, rsaBlock)
    println("Ciphertext (unsigned) bit length: ${ciphertextUnsigned.bitLength()}")
    val resultUnsigned = ciphertextUnsigned.modPow(publicExp, modulus)
    val resultBytesUnsigned = resultUnsigned.toByteArray()
    // Strip leading 0x00 sign byte if present
    val resultCleanUnsigned = if (resultBytesUnsigned.isNotEmpty() && resultBytesUnsigned[0] == 0.toByte() && resultBytesUnsigned.size > 1) {
        resultBytesUnsigned.copyOfRange(1, resultBytesUnsigned.size)
    } else {
        resultBytesUnsigned
    }
    println("Decrypted result size (raw toByteArray): ${resultBytesUnsigned.size}")
    println("Decrypted result size (stripped sign byte): ${resultCleanUnsigned.size}")
    println("Decrypted first 10 bytes: ${resultCleanUnsigned.take(10).joinToString(" ") { "%02x".format(it) }}")

    if (resultCleanUnsigned.size == 65) {
        pass("UNSIGNED: Decrypted result is 65 bytes")
    } else {
        fail("UNSIGNED: Decrypted result is ${resultCleanUnsigned.size} bytes, expected 65")
    }

    if (resultCleanUnsigned.isNotEmpty() && resultCleanUnsigned[0] == 0x01.toByte()) {
        pass("UNSIGNED: result[0] == 0x01")
    } else {
        val b0 = if (resultCleanUnsigned.isNotEmpty()) "0x%02x".format(resultCleanUnsigned[0]) else "empty"
        fail("UNSIGNED: result[0] == $b0, expected 0x01")
    }

    // Compute Whirlpool over container bytes [5 .. 6 + archiveCount*80)
    val hashStart = 5
    val hashLen = 1 + archiveCount * ENTRY_SIZE
    println("\nWhirlpool hash range: container[$hashStart .. ${hashStart + hashLen}) = $hashLen bytes")

    val wp = Whirlpool()
    wp.add(versionTable, hashStart, hashLen)
    val computedWhirlpool = ByteArray(64)
    wp.finalize(computedWhirlpool)
    println("Computed Whirlpool: ${computedWhirlpool.joinToString("") { "%02x".format(it) }}")

    if (resultCleanUnsigned.size >= 65) {
        val rsaWhirlpool = resultCleanUnsigned.copyOfRange(1, 65)
        println("RSA Whirlpool:      ${rsaWhirlpool.joinToString("") { "%02x".format(it) }}")
        if (rsaWhirlpool.contentEquals(computedWhirlpool)) {
            pass("UNSIGNED: Whirlpool matches RSA signature")
        } else {
            fail("UNSIGNED: Whirlpool MISMATCH")
            for (i in 0 until 64) {
                if (rsaWhirlpool[i] != computedWhirlpool[i]) {
                    println("    First difference at byte $i: RSA=0x${"%02x".format(rsaWhirlpool[i])} computed=0x${"%02x".format(computedWhirlpool[i])}")
                    break
                }
            }
        }
    }

    // --- 9: SIGNED vs UNSIGNED comparison ---
    println("\n--- 9: SIGNED BigInteger(rsaBlock) vs UNSIGNED ---")
    val ciphertextSigned = BigInteger(rsaBlock)
    val isNegative = ciphertextSigned.signum() < 0
    println("rsaBlock[0] = 0x${"%02x".format(rsaBlock[0])} — high bit ${if (rsaBlock[0].toInt() and 0x80 != 0) "SET" else "clear"}")
    println("Signed interpretation: ${if (isNegative) "NEGATIVE" else "positive"} (signum=${ciphertextSigned.signum()})")

    val resultSigned = ciphertextSigned.modPow(publicExp, modulus)
    val resultBytesSigned = resultSigned.toByteArray()
    val resultCleanSigned = if (resultBytesSigned.isNotEmpty() && resultBytesSigned[0] == 0.toByte() && resultBytesSigned.size > 1) {
        resultBytesSigned.copyOfRange(1, resultBytesSigned.size)
    } else {
        resultBytesSigned
    }
    println("Signed decrypted size: ${resultCleanSigned.size} (raw: ${resultBytesSigned.size})")
    if (resultCleanSigned.size >= 1) {
        println("Signed decrypted first byte: 0x${"%02x".format(resultCleanSigned[0])}")
    }
    if (resultCleanUnsigned.contentEquals(resultCleanSigned)) {
        println("  Signed and unsigned results are IDENTICAL")
        pass("Signed == Unsigned (rsaBlock high bit not set)")
    } else {
        println("  Signed and unsigned results DIFFER!")
        println("  WARNING: If the client uses unsigned interpretation, our signed RSA.crypt is wrong!")
        fail("Signed != Unsigned — BigInteger signedness mismatch on ciphertext")
    }

    // Also check the SIGNING side: plaintext = 0x01 + whirlpool
    println("\n--- Server RSA.crypt signing check ---")
    val plaintext = ByteArray(65)
    plaintext[0] = 0x01
    System.arraycopy(computedWhirlpool, 0, plaintext, 1, 64)
    println("Plaintext[0]: 0x${"%02x".format(plaintext[0])} — high bit ${if (plaintext[0].toInt() and 0x80 != 0) "SET (problem!)" else "clear (OK)"}")
    val signedPlaintext = BigInteger(plaintext)
    val unsignedPlaintext = BigInteger(1, plaintext)
    if (signedPlaintext == unsignedPlaintext) {
        pass("Plaintext byte[0]=0x01 — signed/unsigned match (signing is safe)")
    } else {
        fail("Plaintext byte[0] causes signed/unsigned mismatch! RSA.crypt signing is WRONG!")
    }

    // =====================================================================
    // Step 10: Verify ALL index CRCs
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 4: Index CRC Verification ===")
    println("=".repeat(70))

    data class MasterEntry(val crc: Int, val version: Int, val fileCount: Int, val uncompSize: Int, val whirlpool: ByteArray)
    val entries = mutableMapOf<Int, MasterEntry>()
    for (i in 0 until archiveCount) {
        val off = 6 + i * ENTRY_SIZE
        if (off + ENTRY_SIZE > versionTable.size) break
        val crc = readBEInt(versionTable, off)
        val ver = readBEInt(versionTable, off + 4)
        val fc = readBEInt(versionTable, off + 8)
        val us = readBEInt(versionTable, off + 12)
        val wpBytes = versionTable.copyOfRange(off + 16, off + 16 + 64)
        entries[i] = MasterEntry(crc, ver, fc, us, wpBytes)
    }

    val activeEntries = entries.filter { it.value.crc != 0 || it.value.version != 0 }
    println("Total archives in master index: $archiveCount")
    println("Active archives (non-zero CRC or version): ${activeEntries.size}")
    println()

    for ((idx, entry) in activeEntries.toSortedMap()) {
        val raw = cache.sector(255, idx)
        if (raw == null) {
            fail("Archive $idx: cache.sector(255, $idx) returned null — MISSING!")
            continue
        }

        // CRC with java.util.zip.CRC32
        val javaCrc32 = CRC32()
        javaCrc32.update(raw)
        val javaCrcValue = javaCrc32.value.toInt()

        // CRC with our custom CRC class (what VersionTableBuilder uses)
        val customCrcValue = CRC.calculate(raw, 0, raw.size)

        // CRC from master index entry
        val masterCrc = entry.crc

        val javaMatch = javaCrcValue == masterCrc
        val customMatch = customCrcValue == masterCrc

        if (!javaMatch || !customMatch) {
            fail("Archive $idx CRC: master=0x${"%08x".format(masterCrc)} java=0x${"%08x".format(javaCrcValue)} custom=0x${"%08x".format(customCrcValue)}")
        } else {
            pass("Archive $idx CRC: 0x${"%08x".format(masterCrc)} OK (${raw.size}B)")
        }
    }

    // =====================================================================
    // Step 11: Verify ALL index Whirlpools
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 5: Index Whirlpool Verification ===")
    println("=".repeat(70))

    for ((idx, entry) in activeEntries.toSortedMap()) {
        val raw = cache.sector(255, idx) ?: continue  // already reported missing above

        val wpCheck = Whirlpool()
        wpCheck.add(raw)
        val wpResult = ByteArray(64)
        wpCheck.finalize(wpResult)

        if (wpResult.contentEquals(entry.whirlpool)) {
            pass("Archive $idx Whirlpool OK")
        } else {
            fail("Archive $idx Whirlpool MISMATCH")
            println("    Expected: ${entry.whirlpool.joinToString("") { "%02x".format(it) }}")
            println("    Computed: ${wpResult.joinToString("") { "%02x".format(it) }}")
        }
    }

    // =====================================================================
    // Step 12: Decompress each index and check format byte
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 6: Index Decompression & Format Check ===")
    println("=".repeat(70))

    val decomp = DecompressionContext()
    for ((idx, _) in activeEntries.toSortedMap()) {
        val raw = cache.sector(255, idx) ?: continue

        val decompressed = decomp.decompress(raw)
        if (decompressed == null) {
            fail("Archive $idx: decompress FAILED")
            continue
        }
        if (decompressed.isEmpty()) {
            fail("Archive $idx: decompressed to empty")
            continue
        }

        val fmt = decompressed[0].toInt() and 0xFF
        if (fmt in listOf(0x05, 0x06, 0x07)) {
            pass("Archive $idx: format=0x${"%02x".format(fmt)} (${decompressed.size}B decompressed)")
        } else {
            fail("Archive $idx: format=0x${"%02x".format(fmt)} — expected 0x05, 0x06, or 0x07")
        }
    }

    // =====================================================================
    // Client Disk Cache State
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== STEP 7: Client Disk Cache State ===")
    println("=".repeat(70))

    val darkanCacheDir = java.io.File(System.getProperty("user.home"), ".darkan3/Jagex/RuneScape")
    println("Cache directory: ${darkanCacheDir.absolutePath}")
    println("Exists: ${darkanCacheDir.exists()}")

    if (darkanCacheDir.exists()) {
        val files = darkanCacheDir.listFiles()?.sortedBy { it.name } ?: emptyList()
        if (files.isEmpty()) {
            println("  (empty directory)")
        } else {
            println("  Files:")
            for (f in files) {
                println("    ${f.name}: ${f.length()} bytes")
            }
            val jcacheFiles = files.filter { it.name.endsWith(".jcache") }
            println("\n  .jcache files: ${jcacheFiles.size}")
        }
    } else {
        println("  Directory does not exist.")
    }

    // =====================================================================
    // Summary
    // =====================================================================
    println("\n" + "=".repeat(70))
    println("=== RESULTS: $passed passed, $failed failed ===")
    println("=".repeat(70))
    if (failed > 0) {
        System.exit(1)
    }
}

private fun readBEInt(data: ByteArray, offset: Int): Int =
    ((data[offset].toInt() and 0xFF) shl 24) or
    ((data[offset + 1].toInt() and 0xFF) shl 16) or
    ((data[offset + 2].toInt() and 0xFF) shl 8) or
    (data[offset + 3].toInt() and 0xFF)
