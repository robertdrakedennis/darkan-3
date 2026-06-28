package org.darkan.core.net.prot

// === Entity sync packets (per A4, A5) ===

/**
 * PLAYER_INFO — rev948 op 22, varShort. Carries the pre-built bit block + per-player
 * extended-info blocks (the bit block is built by the world-side viewport traversal in B6).
 * [firstTick] toggles the first-tick init layout (30-bit local tile + 2047 18-bit region
 * hashes; see A4 §4A).
 */
data class PlayerInfo(
    val bitBlock: ByteArray,
    val extendedInfo: List<ByteArray>,
    val firstTick: Boolean,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlayerInfo) return false
        if (firstTick != other.firstTick) return false
        if (!bitBlock.contentEquals(other.bitBlock)) return false
        if (extendedInfo.size != other.extendedInfo.size) return false
        return extendedInfo.indices.all { extendedInfo[it].contentEquals(other.extendedInfo[it]) }
    }
    override fun hashCode(): Int {
        var result = bitBlock.contentHashCode()
        result = 31 * result + extendedInfo.fold(0) { acc, b -> 31 * acc + b.contentHashCode() }
        result = 31 * result + firstTick.hashCode()
        return result
    }
}

/**
 * NPC_INFO — rev948 op 52, varShort. Carries the pre-built bit block + per-NPC
 * extended-info blocks.
 */
data class NpcInfo(
    val bitBlock: ByteArray,
    val extendedInfo: List<ByteArray>,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NpcInfo) return false
        if (!bitBlock.contentEquals(other.bitBlock)) return false
        if (extendedInfo.size != other.extendedInfo.size) return false
        return extendedInfo.indices.all { extendedInfo[it].contentEquals(other.extendedInfo[it]) }
    }
    override fun hashCode(): Int {
        var result = bitBlock.contentHashCode()
        result = 31 * result + extendedInfo.fold(0) { acc, b -> 31 * acc + b.contentHashCode() }
        return result
    }
}

// NOTE: UPDATE_UID192 is intentionally not modelled here; it is handshake identity binding.
