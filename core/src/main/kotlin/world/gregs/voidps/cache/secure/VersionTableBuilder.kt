package world.gregs.voidps.cache.secure

import org.darkan.core.Logger.logInfo
import java.math.BigInteger

/**
 * Builds the NXT RS3 master index (version table) for JS5 serving.
 *
 * Wire format (container):
 *   [compression=0 (1B)][compressedSize (4B BE)][archiveCount (1B)]
 *   [archiveCount * 80B: CRC(4)+version(4)+fileCount(4)+uncompressedSize(4)+whirlpool(64)]
 *   [RSA signature block]
 *
 * The RSA signature is computed as: sign(0x0A || whirlpool(archiveCount..entries))
 * Whirlpool is over bytes [5 .. 6 + archiveCount*80) of the container.
 */
class VersionTableBuilder(
    private val exponent: BigInteger,
    private val modulus: BigInteger,
    private val indexCount: Int
) {

    private val rsaMaxSize = (modulus.bitLength() + 7) / 8 + 1
    private val versionTable = ByteArray(positionFor(indexCount) + rsaMaxSize)
    private var built = false

    init {
        versionTable[5] = indexCount.toByte()
    }

    fun skip(index: Int) {
        val pos = positionFor(index)
        for (i in 0 until ENTRY_SIZE) {
            versionTable[pos + i] = 0
        }
    }

    /** Write CRC and whirlpool for an index's raw ref table data. */
    fun sector(index: Int, sectorData: ByteArray, whirlpool: Whirlpool) {
        val crc = CRC.calculate(sectorData)
        crc(index, crc)
        val output = ByteArray(WHIRLPOOL_SIZE)
        whirlpool.reset()
        whirlpool.add(sectorData)
        whirlpool.finalize(output)
        whirlpool(index, output)
    }

    fun crc(index: Int, crc: Int) {
        val pos = positionFor(index) + CRC_OFFSET
        writeInt(pos, crc)
    }

    fun revision(index: Int, revision: Int) {
        val pos = positionFor(index) + VERSION_OFFSET
        writeInt(pos, revision)
    }

    fun fileCount(index: Int, count: Int) {
        val pos = positionFor(index) + FILE_COUNT_OFFSET
        writeInt(pos, count)
    }

    fun uncompressedSize(index: Int, size: Int) {
        val pos = positionFor(index) + UNCOMPRESSED_SIZE_OFFSET
        writeInt(pos, size)
    }

    fun whirlpool(index: Int, whirlpool: ByteArray) {
        val pos = positionFor(index) + WHIRLPOOL_OFFSET
        System.arraycopy(whirlpool, 0, versionTable, pos, minOf(whirlpool.size, WHIRLPOOL_SIZE))
    }

    fun build(whirlpool: Whirlpool = Whirlpool()): ByteArray {
        if (built) {
            return versionTable
        }
        built = true

        // Compute Whirlpool over [archiveCount byte + all entries]
        val hashStart = 5
        val hashLen = positionFor(indexCount) - hashStart
        val output = ByteArray(WHIRLPOOL_SIZE + 1)
        output[0] = 10
        whirlpool.reset()
        whirlpool.add(versionTable, hashStart, hashLen)
        whirlpool.finalize(output, 1)

        // RSA sign: message^privateExp mod modulus
        val rsa = RSA.crypt(output, modulus, exponent)
        val pos = positionFor(indexCount)
        System.arraycopy(rsa, 0, versionTable, pos, rsa.size)

        // Write container header
        val end = pos + rsa.size
        versionTable[0] = 0 // compression = none
        writeInt(1, end - 5) // compressedSize

        val data = ByteArray(end)
        System.arraycopy(versionTable, 0, data, 0, data.size)

        logInfo("Version table built: ${data.size}B, $indexCount indices, RSA ${rsa.size}B")

        return data
    }

    private fun writeInt(pos: Int, value: Int) {
        versionTable[pos] = (value shr 24).toByte()
        versionTable[pos + 1] = (value shr 16).toByte()
        versionTable[pos + 2] = (value shr 8).toByte()
        versionTable[pos + 3] = (value).toByte()
    }

    companion object {
        /** NXT entry layout: CRC(4) + version(4) + fileCount(4) + uncompressedSize(4) + whirlpool(64) = 80 */
        private const val WHIRLPOOL_SIZE = 64
        private const val ENTRY_SIZE = 80
        private const val CRC_OFFSET = 0
        private const val VERSION_OFFSET = 4
        private const val FILE_COUNT_OFFSET = 8
        private const val UNCOMPRESSED_SIZE_OFFSET = 12
        private const val WHIRLPOOL_OFFSET = 16

        /** Position of entry for a given index within the versionTable array.
         *  Byte 0-4 = container header, byte 5 = archiveCount, byte 6+ = entries */
        private fun positionFor(index: Int) = 6 + index * ENTRY_SIZE
    }
}
