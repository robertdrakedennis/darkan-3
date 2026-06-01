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

/** VARP_BIT_SMALL — sets a player varbit with a 1-byte value. 948 op 10. */
data class VarpBitSmall(val id: Int, val value: Int) : ServerProt

/** VARP_BIT_LARGE — sets a player varbit with a 4-byte value. 948 op 51. */
data class VarpBitLarge(val id: Int, val value: Int) : ServerProt

/** CLIENT_SETVARCBIT_SMALL — sets a client varbit with a 1-byte value. 948 op 48. */
data class ClientSetVarcBitSmall(val id: Int, val value: Int) : ServerProt

/** CLIENT_SETVARCBIT_LARGE — sets a client varbit with a 4-byte value. 948 op 69. */
data class ClientSetVarcBitLarge(val id: Int, val value: Int) : ServerProt

/**
 * CLIENT_SETVARC_STR_LARGE — sets a client string var. 948 op 116. STRING-FIRST variant,
 * distinct from [ClientSetVarcStr] (op 92, id-first).
 */
data class ClientSetVarcStrLarge(val id: Int, val value: String) : ServerProt

@JvmInline
value class ResetClientVarcache(val dummy: Int = 0) : ServerProt

@Serializable @JvmInline value class VarclanEnable(val dummy: Int = 0) : ServerProt
@Serializable @JvmInline value class VarclanDisable(val dummy: Int = 0) : ServerProt

// --- Interfaces ---

/** IF_OPENTOP (op 68, 6B) per A1 §1.1 — opens a top-level interface. */
data class IfOpenTop(val topLevelId: Int, val subId: Int = 0) : ServerProt

/** IF_SETTOPLEVELINTERFACE (op 94, 19B) per A1 §1.2 — switches the active top-level interface. */
data class IfSetTopLevelInterface(val topLevelId: Int) : ServerProt

/** IF_OPENSUB (op 17, 8B) per A1 §1.3 — opens a sub-interface inside a parent. */
data class IfOpenSub(val subId: Int, val walkable: Int, val parentHash: Int) : ServerProt

/** IF_SETPOSITION (op 8, 23B) per A1 §1.4 — sets a component's layer/position descriptor. */
data class IfSetPosition(val componentId: Int, val layer: Int, val position: Int) : ServerProt

/** IF_CLOSESUB (op 33, 4B) per A1 §1.6 — closes a sub-interface by component hash. */
data class IfCloseSub(val componentHash: Int) : ServerProt

/**
 * IF_MOVESUB (op 189, 3B) per A1 §1.7. [mode] is a tri-state operator:
 * 0x71 = close all subs under top, 0x7F = mark subs active, anything else = mark inactive.
 */
data class IfMoveSub(val topId: Int, val mode: Int) : ServerProt

/**
 * IF_SETEVENTS (opcode 35, 12B fixed) — IF_SETEVENTS2 per A1 §1.8. Sets the event mask for
 * a range of slots on an interface component. Use [IFEvents] to build the settings bitfield.
 * The existing 12B encoder maps to A1's IF_SETEVENTS2 (op 35) layout.
 */
data class IfSetEvents(val events: IFEvents) : ServerProt

/**
 * IF_SETEVENTS1 (opcode 34, 10B fixed) per A1 §1.8. Distinct from IF_SETEVENTS2: this variant
 * sends the events bitmask on the wire (vs IF_SETEVENTS2 hard-coding it to all-events).
 */
data class IfSetEvents1(
    val componentHash: Int,
    val eventsMask: Int,
    val endSlot: Int = -1,
    val startSlot: Int = -1,
) : ServerProt

/** IF_SETHIDE (op 103, 5B) per A1 §1.9 — toggles a component's hidden state. */
data class IfSetHide(val componentHash: Int, val hide: Boolean) : ServerProt

/** IF_SETANGLE (op 117, 32B) per A1 §1.10 — full angle/zoom/component packing. */
data class IfSetAngle(
    val componentId: Int,
    val angle1: Int,
    val angle3: Int,
    val colourIndex: Int,
    val angleZoom: Int,
    val packedAngle2: Int,
) : ServerProt

/** IF_SET_HTTP_IMAGE (op 146, varByte) per A1 §1.11. Stub name was IF_SETGRAPHIC_ACTIVE. */
data class IfSetHttpImage(val imageUrl: String) : ServerProt

// --- Interface property setters (A1 §2.1, SetComponentProperty) ---

/** IF_SETOBJECT_ACTIVE (op 16, 4B) per A1 §2.1. */
data class IfSetObjectActive(val componentHash: Int) : ServerProt

/** IF_SETMODEL (op 74, 8B) per A1 §2.1. */
data class IfSetModel(val value: Int, val componentHash: Int) : ServerProt

/** IF_SETANIM_ACTIVE (op 81, 4B) per A1 §2.1. */
data class IfSetAnimActive(val componentHash: Int) : ServerProt

/** IF_SETNPCHEAD (op 98, 10B) per A1 §2.1. */
data class IfSetNpcHead(val scale: Int, val componentHash: Int, val partA: Int, val partB: Int) : ServerProt

/** IF_SETOBJECT (op 100, 10B) per A1 §2.1. */
data class IfSetObject(val objectSlot: Int, val objectCount: Int, val componentHash: Int) : ServerProt

/** IF_SETANIM (op 106, 10B) per A1 §2.1. */
data class IfSetAnim(val componentHash: Int, val frame: Int, val animId: Int) : ServerProt

/** IF_SETCOLOUR (op 122, 8B) per A1 §2.1. */
data class IfSetColour(val colour24: Int, val componentHash: Int) : ServerProt

/** IF_SETOBJECT_SMALL (op 141, 5B) per A1 §2.1. Value derived as `-2 - smallIdx` client-side. */
data class IfSetObjectSmall(val componentHash: Int, val smallIdx: Int) : ServerProt

/** IF_SETANIM_SMALL (op 193, 5B) per A1 §2.1. Value derived as `-2 - smallIdx` client-side. */
data class IfSetAnimSmall(val smallIdx: Int, val componentHash: Int) : ServerProt

// --- Interface direct-update setters (A1 §2.2, CreateOrFindUpdateEntry) ---

/** IF_SETPLAYERHEAD_ACTIVE (op 14, 5B) per A1 §2.2. flag=1 if rawByte == 0x01. */
data class IfSetPlayerHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETRECOL (op 44, 6B) per A1 §2.2. RGB-555 expanded client-side to 24-bit. */
data class IfSetRecol(val rgb555: Int, val componentHash: Int) : ServerProt

/** IF_SET2DANGLE (op 53, 8B) per A1 §2.2. */
data class IfSet2DAngle(val angle: Int, val componentHash: Int) : ServerProt

/** IF_SET_MODEL_FRAME (op 64, 8B) per A1 §2.2. */
data class IfSetModelFrame(val frame: Int, val componentHash: Int) : ServerProt

/** IF_SETNPCMODEL (op 76, 10B) per A1 §2.2. npcId 0xFFFF means null. */
data class IfSetNpcModel(val componentHash: Int, val modelId: Int, val npcId: Int) : ServerProt

/** IF_SETMODELORIGIN (op 88, 10B) per A1 §2.2. */
data class IfSetModelOrigin(val componentHash: Int, val x: Int, val y: Int, val z: Int) : ServerProt

/** IF_SETGRAPHIC (op 92, 8B) per A1 §2.2. */
data class IfSetGraphic(val graphicId: Int, val componentHash: Int) : ServerProt

/** IF_SETSPRITE (op 123, 8B) per A1 §2.2. */
data class IfSetSprite(val componentHash: Int, val spriteValue: Int) : ServerProt

/** IF_SETSCROLLSIZE (op 136, 9B) per A1 §2.2. */
data class IfSetScrollSize(
    val scrollW: Int,
    val scrollH: Int,
    val componentHash: Int,
    val subSlot: Int,
) : ServerProt

/** IF_SETNPCHEAD_ACTIVE (op 150, 5B) per A1 §2.2. flag=1 if rawByte == 0x7F. */
data class IfSetNpcHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETMODEL_COORD (op 208, 14B) per A1 §2.2. */
data class IfSetModelCoord(val npcId: Int, val componentHash: Int, val part1: Int, val part2: Int) : ServerProt

/** IF_SETSCROLLPOS (op 210, 9B) per A1 §2.2. */
data class IfSetScrollPos(
    val scrollY: Int,
    val componentHash: Int,
    val scrollX: Int,
    val subSlot: Int,
) : ServerProt

// --- Interface complex/direct-allocation setters (A1 §2.3) ---

/** IF_SETPLAYERMODEL_OTHER (op 97, 25B) per A1 §2.3 — model from another player. Opaque payload until B4 unpacks. */
data class IfSetPlayerModelOther(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelOther && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SELF (op 107, 25B) per A1 §2.3 — local player's model. Opaque payload until B4. */
data class IfSetPlayerModelSelf(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSelf && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SNAPSHOT (op 110, 29B) per A1 §2.3 — embeds a packed-coord snapshot. Opaque until B4. */
data class IfSetPlayerModelSnapshot(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSnapshot && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SUBSWAP (op 85, 8B) per A1 §2.3 — atomic close-A-then-open-B for sub-interface swap. */
data class IfSubSwap(val componentA: Int, val componentB: Int) : ServerProt

// --- Interface trigger / close variants (A1 §2.4) ---

/** IF_TRIGGER_CLOSE (op 49, 0B) per A1 §2.4 — fires event 0x29 on current top-level. */
@JvmInline
value class IfTriggerClose(val dummy: Int = 0) : ServerProt

/** IF_CLOSESUB_BY_ID (op 169, 2B) per A1 §2.4 — closes a sub by 16-bit id. */
data class IfCloseSubById(val id: Int) : ServerProt

/** IF_SETTEXT (op 2, varShort) per A1 §3 — sets the text content of a component. */
data class IfSetText(val componentHash: Int, val text: String) : ServerProt

// NOTE: IF_OPENSUB_THUNK (op 186) intentionally not modelled — A1 §1.5 confirms this is a
// debug-only DBFilter path, never registered server-side.

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

/**
 * SET_WORLD_TARGET (opcode 187, varByte) — tells the client the hostname/port of the next lobby
 * target. Only populates the LOBBY login slot in WorldSwitcher — does NOT trigger a world transfer.
 * Use [SwitchWorld] for lobby→world transfers.
 *
 * Wire format (verified against 947-3 handler at 0x00222390):
 *   string hostname (CP1252 + null) + ushort worldId + ushort port1 + ushort port2 (all BE).
 */
data class SetWorldTarget(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
) : ServerProt

/**
 * SWITCH_WORLD (opcode 179, varByte) — triggers the lobby→world transfer on the client.
 *
 * The client handler (verified in 947-3 at 0x001c1bd5 — previously mislabeled `FRIENDCHAT_JOIN`)
 * stores the world target in `WorldSwitcher`, sets MainState to 0x25, which fires the login state
 * machine — the client then opens a TCP connection to hostname:port1 for world login.
 *
 * Wire format:
 *   string hostname (CP1252 + null) + ushort worldId + ushort port1 + ushort port2 + ubyte pendingFlag (all BE).
 */
data class SwitchWorld(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
    val pendingFlag: Int = 0,
) : ServerProt

/** JCOINS_UPDATE (opcode 59, 4B) — RuneCoins balance display. Value is BE int. */
data class JcoinsUpdate(val balance: Int) : ServerProt

// === Rebuild packets (per A2) ===

/**
 * REBUILD_NORMAL — opcode 90, varShort — the simple-form rebuild handler at 0x002140c0
 * (`ClientState::REBUILD_NORMAL_SIMPLE`). Used for normal world login per A2 §3.
 *
 * Wire format (16 bytes): chunkX BE u16 + forceRefresh byte + regionLow LE u16 + magic 0x7B
 * byte + chunkZ BE u16 + packedCoordA BE u32 + packedCoordB BE u32.
 *
 * `packedCoordA/B` use the BuildArea decomposition `(plane << 28) | (y << 14) | x`.
 */
data class RebuildNormalSimple(
    val chunkX: Int,
    val chunkZ: Int,
    val forceRefresh: Boolean,
    val regionLow: Int,
    val packedCoordA: Int,
    val packedCoordB: Int,
) : ServerProt

/**
 * REBUILD_REGION — opcode 172, varShort — multi-scene grid form for INSTANCED regions per
 * A2 §2. Field structure (per-scene seed + primary/secondary descriptor lists + per-cell XTEA
 * grid) is heavy; modelled as opaque payload until a downstream consumer needs structured
 * access. B4 will define the scene record API when an instanced-region flow is wired up.
 */
data class RebuildRegion(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildRegion && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/**
 * REBUILD_WORLDENTITY — opcode 188, varShort — per A2 §4. Triple-nested (level / regionX /
 * regionY) -1-terminated XTEA stream. Opaque payload until B4 defines a structured API.
 */
data class RebuildWorldEntity(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildWorldEntity && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

// === Zone update packets (per A3) ===

/** UPDATE_ZONE_FULL_FOLLOWS — opcode 18, 3B — per A3 §2. Sets zone globals + scene-clear. */
data class UpdateZoneFullFollowsV2(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/** UPDATE_ZONE_PARTIAL_FOLLOWS — opcode 57, 3B — per A3 §2. Sets zone globals only. */
data class UpdateZonePartialFollows(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/**
 * UPDATE_ZONE_PARTIAL_ENCLOSED — opcode 126, varShort — per A3 §2. Per A3 §1, in 947-3 only
 * sub-op 1 (LOC_ANIM) is bound; the server SHOULD prefer the standalone main-table opcodes
 * for everything else. Carries a header + an embedded list of sub-packets.
 */
data class UpdateZonePartialEnclosed(
    val level: Int,
    val zoneX: Int,
    val zoneY: Int,
    val subPackets: List<ServerProt>,
) : ServerProt

/** LOC_ADD — op 79, varByte — per A3 §3.1. Adds a location at a zone-relative tile. */
data class LocAdd(
    val packedCoord: Int,
    val locId: Int,
    val shapeFlags: Int,
) : ServerProt

/** LOC_DEL — op 37, 2B — per A3 §3.2. Removes a location by shape+rotation at a tile. */
data class LocDel(val shapeFlags: Int, val packedCoord: Int) : ServerProt

/**
 * LOC_CUSTOMISE — op 41, varByte — per A3 §3.3. Header (4B template + packed coord + shape +
 * flags) + optional (model list, recolor src list, recolor dst list). Opaque payload until B4.
 */
data class LocCustomise(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is LocCustomise && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** LOC_PREFETCH — op 51, 7B — per A3 §3.4. Pre-load a future location. */
data class LocPrefetch(
    val visTime: Int,
    val packedCoord: Int,
    val shapeFlags: Int,
    val locId: Int,
) : ServerProt

/** LOC_ANIM_SPECIFIC — op 56, 10B — per A3 §3.6. Like LOC_ANIM with constant flag (no per-packet flag byte). */
data class LocAnimSpecific(
    val packedCoord: Int,
    val animId: Int,
    val shapeFlags: Int,
    val unknown1: Int,
    val delay: Int,
    val speed: Int,
) : ServerProt

/** LOC_MERGE — op 197, 5B — per A3 §3.7. Merge a location's model with another entity. */
data class LocMerge(val entityServerIndex: Int, val packedCoordAndShape: Int) : ServerProt

/** OBJ_ADD — op 38, 5B — per A3 §3.8. Add a ground item to a zone tile. */
data class ObjAdd(
    val objIdHi: Int,
    val objIdLo: Int,
    val countHi: Int,
    val countLo: Int,
    val packedCoord: Int,
) : ServerProt

/** OBJ_DEL — op 42, 3B — per A3 §3.9. Remove a ground item from a zone tile. */
data class ObjDel(val packedCoord: Int, val objIdLo: Int, val objIdHi: Int) : ServerProt

/** OBJ_COUNT — op 60, 7B — per A3 §3.10. Update a ground item's stack count. */
data class ObjCount(
    val playerIndex: Int,
    val objIdLo: Int,
    val objIdHi: Int,
    val packedCoord: Int,
    val count: Int,
) : ServerProt

/** OBJ_REVEAL — op 20, 7B — per A3 §3.11. Reveal a ground item's true count to the viewer. */
data class ObjReveal(
    val packedCoord: Int,
    val objId: Int,
    val oldCount: Int,
    val newCount: Int,
) : ServerProt

/** MAP_ANIM — op 62, 11B — per A3 §3.12. Place/remove a spot animation at a zone tile. */
data class MapAnim(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
) : ServerProt

/** MAP_ANIM_SPECIFIC — op 145, 14B — per A3 §3.13. Like MAP_ANIM with fine sub-tile offsets. */
data class MapAnimSpecific(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
    val unknown: Int,
    val fineOffset: Int,
) : ServerProt

/** MAP_PROJANIM — op 47, 20B — per A3 §3.14. Spawn a projectile animation between zone tiles. */
data class MapProjAnim(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnim && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** MAP_PROJANIM_HALT — op 199, 28B — per A3 §3.15. Projectile with fine src/dst offsets. */
data class MapProjAnimHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnimHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC — op 196, 21B — per A3 §3.16. Projectile w/ double-resolution coords. */
data class ProjAnimSpecific(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecific && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC_HALT — op 192, 29B — per A3 §3.17. Combines halt + double-res. */
data class ProjAnimSpecificHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecificHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** SOUND_AREA — op 167, varByte — per A3 §3.18. Play an area sound effect at a zone tile. */
data class SoundArea(
    val packedCoord: Int,
    val soundId: Int,
    val volume: Int,
    val paramA: Int,
    val paramB: Int,
) : ServerProt

// NOTE: LOC_ANIM has no standalone main-table opcode in 947-3 (sub-op 1 only) per A3 §3.5;
// not modelled as ServerProt — emit it inline inside UpdateZonePartialEnclosed.

// === Entity sync packets (per A4, A5) ===

/**
 * PLAYER_INFO — opcode 27, varShort — per A4. Carries the pre-built bit block + per-player
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
 * NPC_INFO — opcode 12, varShort — per A5. Carries the pre-built bit block + per-NPC
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

// NOTE: UPDATE_UID192 (op 36) intentionally not modelled here — A4 §"Related Packets"
// confirms this is HANDSHAKE_UID (CRC32 identity binding), out of B2 scope.

// --- World list ---

data class WorldListPacket(
    val worldList: WorldList,
    val fullRefresh: Boolean,         // true = send full world defs, false = delta (counts only)
) : ServerProt
