package com.undercut.game.net

/**
 * Position-tracked reader for raw packet byte arrays.
 * All multi-byte reads are big-endian to match the game's wire format.
 */
class PacketReader(private val data: ByteArray) {
    var pos: Int = 0
        private set

    val remaining: Int get() = data.size - pos

    fun g1(): Int {
        return data[pos++].toInt() and 0xFF
    }

    fun g1s(): Int {
        return data[pos++].toInt()
    }

    fun g2(): Int {
        val v = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
        pos += 2
        return v
    }

    fun g2s(): Int {
        val v = (data[pos].toInt() shl 8) or (data[pos + 1].toInt() and 0xFF)
        pos += 2
        return v.toShort().toInt()
    }

    fun g3(): Int {
        val v = ((data[pos].toInt() and 0xFF) shl 16) or
                ((data[pos + 1].toInt() and 0xFF) shl 8) or
                (data[pos + 2].toInt() and 0xFF)
        pos += 3
        return v
    }

    fun g4(): Int {
        val v = ((data[pos].toInt() and 0xFF) shl 24) or
                ((data[pos + 1].toInt() and 0xFF) shl 16) or
                ((data[pos + 2].toInt() and 0xFF) shl 8) or
                (data[pos + 3].toInt() and 0xFF)
        pos += 4
        return v
    }

    fun g8(): Long {
        val high = g4().toLong() and 0xFFFFFFFFL
        val low = g4().toLong() and 0xFFFFFFFFL
        return (high shl 32) or low
    }

    fun gstr(): String {
        val start = pos
        while (pos < data.size && data[pos].toInt() != 0) pos++
        val s = String(data, start, pos - start, Charsets.UTF_8)
        if (pos < data.size) pos++ // skip null terminator
        return s
    }

    fun gdata(length: Int): ByteArray {
        val result = data.copyOfRange(pos, pos + length)
        pos += length
        return result
    }

    /** Little-endian unsigned short */
    fun g2le(): Int {
        val v = (data[pos].toInt() and 0xFF) or ((data[pos + 1].toInt() and 0xFF) shl 8)
        pos += 2
        return v
    }

    /** Little-endian int */
    fun g4le(): Int {
        val v = (data[pos].toInt() and 0xFF) or
                ((data[pos + 1].toInt() and 0xFF) shl 8) or
                ((data[pos + 2].toInt() and 0xFF) shl 16) or
                ((data[pos + 3].toInt() and 0xFF) shl 24)
        pos += 4
        return v
    }

    /** Read byte and subtract 128 (RS g1sub128 / g1_sub128) */
    fun g1sub128(): Int {
        return (data[pos++].toInt() and 0xFF) - 128
    }

    /** Read byte and add 128 (RS g1_add128) */
    fun g1add128(): Int {
        return ((data[pos++].toInt() and 0xFF) + 128) and 0xFF
    }

    /** Read negated byte */
    fun g1neg(): Int {
        return -(data[pos++].toInt() and 0xFF) and 0xFF
    }

    /** Word-swapped 4-byte read: from bytes [a,b,c,d] produces (c<<24)|(d<<16)|(a<<8)|b */
    fun g4alt1(): Int {
        val a = data[pos].toInt() and 0xFF
        val b = data[pos + 1].toInt() and 0xFF
        val c = data[pos + 2].toInt() and 0xFF
        val d = data[pos + 3].toInt() and 0xFF
        pos += 4
        return (c shl 24) or (d shl 16) or (a shl 8) or b
    }

    /** Middle-endian 4-byte read: from bytes [b2,b3,b0,b1] produces (b3<<24)|(b2<<16)|(b1<<8)|b0.
     *  Inverse of p4alt2 which writes [b2,b3,b0,b1]. Same formula as g4alt3. */
    fun g4alt2(): Int = g4alt3()

    /** Middle-endian 4-byte read: from bytes [a,b,c,d] produces (b<<24)|(a<<16)|(d<<8)|c */
    fun g4alt3(): Int {
        val a = data[pos].toInt() and 0xFF
        val b = data[pos + 1].toInt() and 0xFF
        val c = data[pos + 2].toInt() and 0xFF
        val d = data[pos + 3].toInt() and 0xFF
        pos += 4
        return (b shl 24) or (a shl 16) or (d shl 8) or c
    }

    /** Big-endian 2-byte read with +128 on the low byte: (byte0 << 8) | ((byte1 + 0x80) & 0xFF) */
    fun g2add128(): Int {
        val high = data[pos].toInt() and 0xFF
        val low = (data[pos + 1].toInt() + 0x80) and 0xFF
        pos += 2
        return (high shl 8) or low
    }

    /** Little-endian 2-byte read with +128 on the low byte: (byte1 << 8) | ((byte0 + 0x80) & 0xFF) */
    fun g2leAdd128(): Int {
        val low = (data[pos].toInt() + 0x80) and 0xFF
        val high = data[pos + 1].toInt() and 0xFF
        pos += 2
        return (high shl 8) or low
    }

    /** Smart 1-or-2 byte unsigned int */
    fun gsmart(): Int {
        val peek = data[pos].toInt() and 0xFF
        return if (peek < 128) g1() else g2() - 0x8000
    }

    /** Read boolean (byte != 0) */
    fun gboolean(): Boolean {
        return g1() != 0
    }

    fun skip(count: Int) {
        pos += count
    }

    /** Return all remaining bytes as a ByteArray */
    fun remainingBytes(): ByteArray {
        val result = data.copyOfRange(pos, data.size)
        pos = data.size
        return result
    }

    fun toHexDump(maxBytes: Int = 64): String {
        val end = minOf(data.size, maxBytes)
        val hex = data.take(end).joinToString(" ") { "%02X".format(it) }
        return if (data.size > maxBytes) "$hex ..." else hex
    }

    companion object {
        fun hexDump(data: ByteArray, maxBytes: Int = 64): String {
            val end = minOf(data.size, maxBytes)
            val hex = data.take(end).joinToString(" ") { "%02X".format(it) }
            return if (data.size > maxBytes) "$hex ..." else hex
        }
    }
}
