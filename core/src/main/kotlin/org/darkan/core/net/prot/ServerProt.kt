package org.darkan.core.net.prot

/**
 * Marker interface for all server-to-client packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries its fields. The Codec registry maps each class to its opcode, size,
 * and encoder lambda for a given revision.
 */
import org.darkan.core.model.IFEvents
import org.darkan.core.worldlist.WorldList

interface ServerProt

// --- Variables ---

data class VarpSmall(val id: Int, val value: Int) : ServerProt
data class VarpLarge(val id: Int, val value: Int) : ServerProt
data class VarpLong(val id: Int, val value: Long) : ServerProt
data class ClientSetVarcSmall(val id: Int, val value: Int) : ServerProt
data class ClientSetVarcLarge(val id: Int, val value: Int) : ServerProt
data class ClientSetVarcStr(val id: Int, val value: String) : ServerProt
data class UpdateStat(val skillId: Int, val xp: Int, val level: Int) : ServerProt

@JvmInline
value class ResetClientVarcache(val dummy: Int = 0) : ServerProt

// --- Interfaces ---

data class IfOpenTopLobby(val interfaceId: Int) : ServerProt
data class IfOpenSubLobby(val parentIfId: Int, val parentComp: Int, val subIfId: Int) : ServerProt

/**
 * IF_SETEVENTS (opcode 59, 12B fixed) — jag::ServerProt::IF_SETEVENTS.
 * Sets the event mask for a range of slots [fromSlot..toSlot] on an interface component.
 * Use [IFEvents] to build the settings bitfield.
 */
data class IfSetEvents(val events: IFEvents) : ServerProt

// --- Misc ---

@JvmInline
value class SetReadyFlag(val dummy: Int = 0) : ServerProt

@JvmInline
value class NoTimeout(val dummy: Int = 0) : ServerProt

data class UpdateRunenergy(val energy: Int) : ServerProt

@JvmInline
value class UpdateIgnoreList(val dummy: Int = 0) : ServerProt

// --- Social ---

/**
 * UPDATE_FRIENDLIST (opcode 18, varShort) — jag::ServerProt::UPDATE_FRIENDLIST.
 * Sends friend list entries. Each entry has display name, world, rank, flags, notes.
 * Fields 7-9 (worldName, platform, worldFlags) only present when worldId > 0.
 * RE-verified from rs2client rev 947 handler at 0x00242840.
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

data class WorldListPacket(
    val worldList: WorldList,
    val fullRefresh: Boolean,         // true = send full world defs, false = delta (counts only)
) : ServerProt
