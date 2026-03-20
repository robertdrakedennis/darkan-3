package org.darkan.core.net.prot

/**
 * Marker interface for all server-to-client packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries its fields. The Codec registry maps each class to its opcode, size,
 * and encoder lambda for a given revision.
 */
interface ServerProt

// --- Variables ---

data class VarpSmall(val id: Int, val value: Int) : ServerProt
data class VarpLarge(val id: Int, val value: Int) : ServerProt
data class VarpLong(val id: Int, val value: Long) : ServerProt
data class ClientSetVarcSmall(val id: Int, val value: Int) : ServerProt
data class ClientSetVarcLarge(val id: Int, val value: Int) : ServerProt
data class UpdateStat(val skillId: Int, val xp: Int, val level: Int) : ServerProt

@JvmInline
value class ClearVarps(val dummy: Int = 0) : ServerProt

// --- Interfaces ---

data class IfOpenTopLobby(val interfaceId: Int) : ServerProt
data class IfOpenSubLobby(val parentIfId: Int, val parentComp: Int, val subIfId: Int) : ServerProt

// --- Misc ---

@JvmInline
value class SetReadyFlag(val dummy: Int = 0) : ServerProt

@JvmInline
value class KeepAlive(val dummy: Int = 0) : ServerProt

data class RunEnergy(val energy: Int) : ServerProt

@JvmInline
value class UpdateIgnoreList(val dummy: Int = 0) : ServerProt

// --- Social ---

/**
 * UPDATE_FRIENDLIST (opcode 18, varShort) — jag::ServerProt::UPDATE_FRIENDLIST.
 * Sends friend list entries. Each entry has display name, world, rank, flags, notes.
 * Fields 7-9 (worldName, platform, worldFlags) only present when worldId > 0.
 * RE-verified from rs2client rev 946 handler at 0x00242840.
 */
data class UpdateFriendList(val friends: List<FriendEntry>) : ServerProt {
    data class FriendEntry(
        val warnMessage: Int = 0,
        val displayName: String,
        val previousName: String = "",
        val worldId: Int = 0,
        val fcRank: Int = 0,
        val flags: Int = 0,
        val worldName: String = "",
        val platform: Int = 0,
        val worldFlags: Int = 0,
        val notes: String = "",
    )
}

// --- World list ---

data class WorldListPacket(val checksum: Int) : ServerProt
