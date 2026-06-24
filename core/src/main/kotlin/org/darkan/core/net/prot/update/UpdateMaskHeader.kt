package org.darkan.core.net.prot.update

object UpdateMaskHeader {
    fun player(flagBitset: Int, expansionBits: IntArray): ByteArray {
        var flags = flagBitset
        val byteCount = playerByteCount(flags)
        require(expansionBits.size >= byteCount - 1)

        for (byte in 1 until byteCount) {
            flags = flags or (1 shl expansionBits[byte - 1])
        }

        return ByteArray(byteCount) { index ->
            ((flags ushr (index * 8)) and 0xFF).toByte()
        }
    }

    fun npc(flagBitset: Long, expansionBits: IntArray): ByteArray {
        var flags = flagBitset
        val byteCount = npcByteCount(flags)
        require(expansionBits.size >= byteCount - 1)

        for (byte in 1 until byteCount) {
            flags = flags or (1L shl expansionBits[byte - 1])
        }

        return ByteArray(byteCount) { index ->
            ((flags ushr (index * 8)) and 0xFF).toByte()
        }
    }

    private fun playerByteCount(flagBitset: Int): Int {
        if (flagBitset == 0) return 1
        val highestBit = 31 - Integer.numberOfLeadingZeros(flagBitset)
        return when {
            highestBit < 8 -> 1
            highestBit < 16 -> 2
            highestBit < 24 -> 3
            else -> 4
        }
    }

    private fun npcByteCount(flagBitset: Long): Int {
        if (flagBitset == 0L) return 1
        val highestBit = 63 - java.lang.Long.numberOfLeadingZeros(flagBitset)
        return when {
            highestBit < 8 -> 1
            highestBit < 16 -> 2
            highestBit < 24 -> 3
            highestBit < 32 -> 4
            else -> 5
        }
    }
}
