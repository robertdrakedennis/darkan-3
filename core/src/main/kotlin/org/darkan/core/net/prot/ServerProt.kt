package org.darkan.core.net.prot

/**
 * Marker interface for all server-to-client packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries its fields. The Codec registry maps each class to its opcode, size,
 * and encoder lambda for a given revision.
 */
import kotlinx.serialization.Serializable
import org.darkan.core.model.ChatMessageType
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

@Serializable @JvmInline value class VarclanEnable(val dummy: Int = 0) : ServerProt
@Serializable @JvmInline value class VarclanDisable(val dummy: Int = 0) : ServerProt

// --- Interfaces ---

data class IfOpenTopLobby(val interfaceId: Int) : ServerProt
data class IfOpenSubLobby(val parentIfId: Int, val parentComp: Int, val subIfId: Int) : ServerProt

/**
 * IF_SETEVENTS (opcode 59, 12B fixed) -- jag::ServerProt::IF_SETEVENTS.
 * Sets the event mask for a range of slots [fromSlot..toSlot] on an interface component.
 * Use [IFEvents] to build the settings bitfield.
 */
data class IfSetEvents(val events: IFEvents) : ServerProt

// --- Misc ---

@JvmInline
value class SetReadyFlag(val dummy: Int = 0) : ServerProt

/** CHANGE_LOBBY (opcode 30, varShort) — empty packet that triggers lobby transition on client. */
@JvmInline
value class ChangeLobby(val dummy: Int = 0) : ServerProt

@JvmInline
value class NoTimeout(val dummy: Int = 0) : ServerProt

data class UpdateRunenergy(val energy: Int) : ServerProt

data class SetPlayerOp(val slot: Int, val text: String?, val priority: Boolean = false) : ServerProt

// --- Social / Chat ---

/**
 * UPDATE_IGNORELIST (varShort) -- sends ignore list entries to the client.
 * Each entry has a display name and optional previous name.
 */
@Serializable
data class UpdateIgnoreList(val ignores: List<IgnoreEntry>) : ServerProt {
    @Serializable
    data class IgnoreEntry(val displayName: String, val previousName: String = "")
}

/**
 * UPDATE_FRIENDLIST (opcode 102, varShort) -- sends friend list entries.
 * Each entry has display name, world, rank, flags, notes.
 * Fields worldName, platform, worldFlags only present when worldId > 0.
 * RE-verified from rs2client rev 947 handler at 0x00242840.
 */
@Serializable
data class FriendStatus(val updates: List<FriendStatusUpdate>) : ServerProt {
    @Serializable
    data class FriendStatusUpdate(
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

/** Sent after the friend list has been fully transmitted. */
@Serializable
data class FriendlistLoaded(val dummy: Int = 0) : ServerProt

/** Private chat filter setting. */
@Serializable
data class ChatFilterSettingsPrivateChat(val filter: Int) : ServerProt

// --- Game Messages ---

/** GAME_MESSAGE -- sends a filtered chat message to the client chatbox. */
@Serializable
data class GameMessage(
    val type: ChatMessageType,
    val message: String,
    val targetDisplayName: String? = null,
    val effectFlags: Int = 0,
) : ServerProt

// --- Private Messages ---

/** MESSAGE_PRIVATE -- incoming private message from another player. */
@Serializable
data class MessagePrivate(
    val crown: Int,
    val displayName: String,
    val quickResponseName: String = displayName,
    val message: String,
) : ServerProt

/** MESSAGE_PRIVATE_ECHO -- echo of a PM we sent (appears in our own chatbox). */
@Serializable
data class MessagePrivateEcho(
    val senderDisplayName: String,
    val message: String,
) : ServerProt

// --- Friends Chat (FC) ---

/** MESSAGE_FRIENDSCHAT -- a message in a friends chat channel. */
@Serializable
data class MessageFriendsChat(
    val crown: Int,
    val displayName: String,
    val quickResponseName: String = displayName,
    val chatName: String,
    val message: String,
) : ServerProt

/**
 * UPDATE_FRIENDCHAT_CHANNEL_FULL -- full friends chat channel state.
 * Send with [clear]=true and null fields to leave/clear the channel.
 */
@Serializable
data class FriendsChatChannel(
    val clear: Boolean = false,
    val ownerDisplayName: String? = null,
    val ownerUsername: String? = ownerDisplayName,
    val chatName: String? = null,
    val minRankCanKick: Int = 0,
    val players: Array<FriendsChatPlayer>? = null,
) : ServerProt {
    @Serializable
    data class FriendsChatPlayer(
        val displayName: String,
        val username: String = displayName,
        val worldId: Int = 0,
        val rank: Int = 0,
        val worldName: String = "",
    )

    init {
        if (!clear) {
            require(ownerDisplayName != null) { "ownerDisplayName is required unless clear=true" }
            require(chatName != null) { "chatName is required unless clear=true" }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as FriendsChatChannel
        if (clear != other.clear) return false
        if (ownerDisplayName != other.ownerDisplayName) return false
        if (ownerUsername != other.ownerUsername) return false
        if (chatName != other.chatName) return false
        if (minRankCanKick != other.minRankCanKick) return false
        return players.contentEquals(other.players)
    }

    override fun hashCode(): Int {
        var result = clear.hashCode()
        result = 31 * result + (ownerDisplayName?.hashCode() ?: 0)
        result = 31 * result + (ownerUsername?.hashCode() ?: 0)
        result = 31 * result + (chatName?.hashCode() ?: 0)
        result = 31 * result + minRankCanKick
        result = 31 * result + (players?.contentHashCode() ?: 0)
        return result
    }
}

// --- Clan Channel (CC) ---

/**
 * CLANCHANNEL_FULL -- full clan channel state.
 * [main]=true for the player's own clan, false for a guest clan channel.
 */
@Serializable
data class ClanChannelFull(
    val main: Boolean,
    val clanName: String? = null,
    val updateNum: Long = 0,
    val kickRank: Int = -1,
    val talkRank: Int = -1,
    val chatters: Array<ClanChannelChatter>? = null,
) : ServerProt {
    @Serializable
    data class ClanChannelChatter(
        val displayName: String,
        val rank: Int,
        val worldId: Int = 0,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClanChannelFull
        if (main != other.main) return false
        if (clanName != other.clanName) return false
        if (updateNum != other.updateNum) return false
        if (kickRank != other.kickRank) return false
        if (talkRank != other.talkRank) return false
        return chatters.contentEquals(other.chatters)
    }

    override fun hashCode(): Int {
        var result = main.hashCode()
        result = 31 * result + (clanName?.hashCode() ?: 0)
        result = 31 * result + updateNum.hashCode()
        result = 31 * result + kickRank
        result = 31 * result + talkRank
        result = 31 * result + (chatters?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * CLANSETTINGS_FULL -- full clan settings state.
 * [main]=true for the player's own clan, false for a guest clan.
 */
@Serializable
data class ClanSettingsFull(
    val main: Boolean,
    val clanName: String? = null,
    val updateCount: Int = 0,
    val allowGuests: Boolean = false,
    val talkRank: Int = -1,
    val kickRank: Int = -1,
    val members: Array<ClanSettingsMember>? = null,
    val bannedUsers: Array<String>? = null,
    val settings: Array<ClanVarSetting>? = null,
) : ServerProt {
    @Serializable
    data class ClanSettingsMember(
        val displayName: String,
        val rank: Int,
    )

    @Serializable
    data class ClanVarSetting(
        val key: Int,
        val intValue: Int? = null,
        val longValue: Long? = null,
        val stringValue: String? = null,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ClanSettingsFull
        if (main != other.main) return false
        if (clanName != other.clanName) return false
        if (updateCount != other.updateCount) return false
        if (allowGuests != other.allowGuests) return false
        if (talkRank != other.talkRank) return false
        if (kickRank != other.kickRank) return false
        if (!members.contentEquals(other.members)) return false
        if (!bannedUsers.contentEquals(other.bannedUsers)) return false
        return settings.contentEquals(other.settings)
    }

    override fun hashCode(): Int {
        var result = main.hashCode()
        result = 31 * result + (clanName?.hashCode() ?: 0)
        result = 31 * result + updateCount
        result = 31 * result + allowGuests.hashCode()
        result = 31 * result + talkRank
        result = 31 * result + kickRank
        result = 31 * result + (members?.contentHashCode() ?: 0)
        result = 31 * result + (bannedUsers?.contentHashCode() ?: 0)
        result = 31 * result + (settings?.contentHashCode() ?: 0)
        return result
    }
}

/** MESSAGE_CLANCHANNEL -- a message in a clan channel. */
@Serializable
data class MessageClanChannel(
    val guest: Boolean,
    val crown: Int,
    val displayName: String,
    val message: String,
) : ServerProt

// --- Quick Chat variants ---

/** MESSAGE_QUICKCHAT_PRIVATE -- incoming quick chat private message. */
@Serializable
data class MessageQuickChatPrivate(
    val crown: Int,
    val displayName: String,
    val quickResponseName: String = displayName,
    val qcId: Int,
    val qcData: ByteArray? = null,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MessageQuickChatPrivate
        if (crown != other.crown) return false
        if (displayName != other.displayName) return false
        if (quickResponseName != other.quickResponseName) return false
        if (qcId != other.qcId) return false
        return qcData.contentEquals(other.qcData)
    }

    override fun hashCode(): Int {
        var result = crown
        result = 31 * result + displayName.hashCode()
        result = 31 * result + quickResponseName.hashCode()
        result = 31 * result + qcId
        result = 31 * result + (qcData?.contentHashCode() ?: 0)
        return result
    }
}

/** MESSAGE_QUICKCHAT_PRIVATE_ECHO -- echo of a quick chat PM we sent. */
@Serializable
data class MessageQuickChatPrivateEcho(
    val senderDisplayName: String,
    val qcId: Int,
    val qcData: ByteArray? = null,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MessageQuickChatPrivateEcho
        if (senderDisplayName != other.senderDisplayName) return false
        if (qcId != other.qcId) return false
        return qcData.contentEquals(other.qcData)
    }

    override fun hashCode(): Int {
        var result = senderDisplayName.hashCode()
        result = 31 * result + qcId
        result = 31 * result + (qcData?.contentHashCode() ?: 0)
        return result
    }
}

/** MESSAGE_QUICKCHAT_FRIENDSCHAT -- quick chat in a friends chat channel. */
@Serializable
data class MessageQuickChatFriendsChat(
    val chatName: String,
    val crown: Int,
    val displayName: String,
    val quickResponseName: String = displayName,
    val qcId: Int,
    val qcData: ByteArray? = null,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MessageQuickChatFriendsChat
        if (chatName != other.chatName) return false
        if (crown != other.crown) return false
        if (displayName != other.displayName) return false
        if (quickResponseName != other.quickResponseName) return false
        if (qcId != other.qcId) return false
        return qcData.contentEquals(other.qcData)
    }

    override fun hashCode(): Int {
        var result = chatName.hashCode()
        result = 31 * result + crown
        result = 31 * result + displayName.hashCode()
        result = 31 * result + quickResponseName.hashCode()
        result = 31 * result + qcId
        result = 31 * result + (qcData?.contentHashCode() ?: 0)
        return result
    }
}

/** MESSAGE_QUICKCHAT_CLANCHANNEL -- quick chat in a clan channel. */
@Serializable
data class MessageQuickChatClanChannel(
    val guest: Boolean,
    val crown: Int,
    val displayName: String,
    val qcId: Int,
    val qcData: ByteArray? = null,
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MessageQuickChatClanChannel
        if (guest != other.guest) return false
        if (crown != other.crown) return false
        if (displayName != other.displayName) return false
        if (qcId != other.qcId) return false
        return qcData.contentEquals(other.qcData)
    }

    override fun hashCode(): Int {
        var result = guest.hashCode()
        result = 31 * result + crown
        result = 31 * result + displayName.hashCode()
        result = 31 * result + qcId
        result = 31 * result + (qcData?.contentHashCode() ?: 0)
        return result
    }
}

// --- Client Scripts ---

/**
 * RUNCLIENTSCRIPT (opcode 121, varShort) -- invokes a CS2 script on the client.
 *
 * Wire format: type descriptor (RS string) + args (in REVERSED type order) + script ID (4B BE).
 * The type descriptor is a string of chars: 'i' = int, 's' = string, 'l' = long.
 * Args are written in REVERSED order of the type chars because the client reads them reversed.
 */
data class RunClientScript(val scriptId: Int, val types: String, val args: Array<Any>) : ServerProt {
    companion object {
        /** Build with named args -- ints and strings. */
        fun of(scriptId: Int, vararg args: Any): RunClientScript {
            val types = StringBuilder()
            for (arg in args) {
                when (arg) {
                    is Int -> types.append('i')
                    is String -> types.append('s')
                    is Long -> types.append('l')
                    else -> error("Unsupported arg type: ${arg::class}")
                }
            }
            return RunClientScript(scriptId, types.toString(), arrayOf(*args))
        }

        /** Component hash from interface ID and component ID. */
        fun componentHash(interfaceId: Int, componentId: Int) = (interfaceId shl 16) or componentId
    }
}

// --- World Login ---

/**
 * WorldLoginDetails — sent by the world server immediately after login success (byte 2).
 * Sent with noIsaac=true because ISAAC is not yet active at this point in the handshake.
 * Opcode 2, VarByte — matches the client's world-login response parser.
 */
data class WorldLoginDetails(
    val rights: Int,
    val modLevel: Int,
    val quickChat: Boolean,
    val verifiedEmail: Boolean,
    val aBool7322: Boolean,
    val quickChatOnly: Boolean,
    val playerIndex: Int,
    val members: Boolean,
    val dob: Int,
    val memberWorld: Boolean,
    val worldName: String,
) : ServerProt

// --- World init ---

/** HASHED_WORLD_TOKEN (opcode 6, varByte) — session nonce for the world connection. */
data class HashedWorldToken(val token: String) : ServerProt

/** REBUILD_NORMAL (opcode 172, varShort) — builds the map scene around player coordinates. */
data class RebuildNormal(
    val chunkX: Int,       // player chunk X (tile X / 8)
    val chunkZ: Int,       // player chunk Z (tile Z / 8)
    val forceRefresh: Boolean = true,
    val mapSize: Int = 0,  // 0 = default 104x104, 1-4 = larger
    val xteaKeys: Array<IntArray> = emptyArray(),  // per-region XTEA keys
) : ServerProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RebuildNormal) return false
        return chunkX == other.chunkX && chunkZ == other.chunkZ && forceRefresh == other.forceRefresh && mapSize == other.mapSize
    }
    override fun hashCode(): Int = 31 * (31 * chunkX + chunkZ) + mapSize
}

// --- World list ---

data class WorldListPacket(
    val worldList: WorldList,
    val fullRefresh: Boolean,         // true = send full world defs, false = delta (counts only)
) : ServerProt
