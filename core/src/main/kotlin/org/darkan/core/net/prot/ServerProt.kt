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

/** IF_OPENTOP — rev948 op 39, 6B. Opens a top-level interface. */
data class IfOpenTop(val topLevelId: Int, val subId: Int = 0) : ServerProt

/** IF_SETTOPLEVELINTERFACE — rev948 op 3, 19B. Switches the active top-level interface. */
data class IfSetTopLevelInterface(val topLevelId: Int) : ServerProt

/** IF_OPENSUB — rev948 op 94, 8B. Opens a sub-interface inside a parent. */
data class IfOpenSub(val subId: Int, val walkable: Int, val parentHash: Int) : ServerProt

/** IF_SETPOSITION — rev948 op 82, 23B. Sets a component's layer/position descriptor. */
data class IfSetPosition(val componentId: Int, val layer: Int, val position: Int) : ServerProt

/** IF_CLOSESUB — rev948 op 62, 4B. Closes a sub-interface by component hash. */
data class IfCloseSub(val componentHash: Int) : ServerProt

/**
 * IF_MOVESUB is not mapped in the rev948 codec. [mode] is a tri-state operator:
 * 0x71 = close all subs under top, 0x7F = mark subs active, anything else = mark inactive.
 */
data class IfMoveSub(val topId: Int, val mode: Int) : ServerProt

/**
 * IF_SETEVENTS — rev948 op 35, 12B fixed. Sets the event mask for
 * a range of slots on an interface component. Use [IFEvents] to build the settings bitfield.
 */
data class IfSetEvents(val events: IFEvents) : ServerProt

/**
 * IF_SETEVENTS1 — rev948 op 97, 10B fixed. Distinct from IF_SETEVENTS: this variant
 * sends the events bitmask on the wire (vs IF_SETEVENTS2 hard-coding it to all-events).
 */
data class IfSetEvents1(
    val componentHash: Int,
    val eventsMask: Int,
    val endSlot: Int = -1,
    val startSlot: Int = -1,
) : ServerProt

/** IF_SETHIDE — rev948 op 91, 5B. Toggles a component's hidden state. */
data class IfSetHide(val componentHash: Int, val hide: Boolean) : ServerProt

/** IF_SETANGLE — rev948 op 4, 32B. Full angle/zoom/component packing. */
data class IfSetAngle(
    val componentId: Int,
    val angle1: Int,
    val angle3: Int,
    val colourIndex: Int,
    val angleZoom: Int,
    val packedAngle2: Int,
) : ServerProt

/** IF_SET_HTTP_IMAGE — rev948 op 152, varByte. */
data class IfSetHttpImage(val imageUrl: String) : ServerProt

// --- Interface property setters (A1 §2.1, SetComponentProperty) ---

/** IF_SETOBJECT_ACTIVE — rev948 op 101, 4B. */
data class IfSetObjectActive(val componentHash: Int) : ServerProt

/** IF_SETMODEL — rev948 op 102, 8B. */
data class IfSetModel(val value: Int, val componentHash: Int) : ServerProt

/** IF_SETANIM_ACTIVE — rev948 op 96, 4B. */
data class IfSetAnimActive(val componentHash: Int) : ServerProt

/** IF_SETNPCHEAD — rev948 op 115, 10B. */
data class IfSetNpcHead(val scale: Int, val componentHash: Int, val partA: Int, val partB: Int) : ServerProt

/** IF_SETOBJECT — rev948 op 84, 10B. */
data class IfSetObject(val objectSlot: Int, val objectCount: Int, val componentHash: Int) : ServerProt

/** IF_SETANIM — rev948 op 86, 10B. */
data class IfSetAnim(val componentHash: Int, val frame: Int, val animId: Int) : ServerProt

/** IF_SETCOLOUR — rev948 op 32, 8B. */
data class IfSetColour(val colour24: Int, val componentHash: Int) : ServerProt

/** IF_SETOBJECT_SMALL — rev948 op 180, 5B. Value derived as `-2 - smallIdx` client-side. */
data class IfSetObjectSmall(val componentHash: Int, val smallIdx: Int) : ServerProt

/** IF_SETANIM_SMALL — rev948 op 136, 5B. Value derived as `-2 - smallIdx` client-side. */
data class IfSetAnimSmall(val smallIdx: Int, val componentHash: Int) : ServerProt

// --- Interface direct-update setters (A1 §2.2, CreateOrFindUpdateEntry) ---

/** IF_SETPLAYERHEAD_ACTIVE — rev948 op 8, 5B. flag=1 if rawByte == 0x01. */
data class IfSetPlayerHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETRECOL — rev948 op 99, 6B. RGB-555 expanded client-side to 24-bit. */
data class IfSetRecol(val rgb555: Int, val componentHash: Int) : ServerProt

/** IF_SET2DANGLE — rev948 op 30, 8B. */
data class IfSet2DAngle(val angle: Int, val componentHash: Int) : ServerProt

/** IF_SET_MODEL_FRAME — rev948 op 38, 8B. */
data class IfSetModelFrame(val frame: Int, val componentHash: Int) : ServerProt

/** IF_SETNPCMODEL — rev948 op 59, 10B. npcId 0xFFFF means null. */
data class IfSetNpcModel(val componentHash: Int, val modelId: Int, val npcId: Int) : ServerProt

/** IF_SETMODELORIGIN — rev948 op 68, 10B. */
data class IfSetModelOrigin(val componentHash: Int, val x: Int, val y: Int, val z: Int) : ServerProt

/** IF_SETGRAPHIC — rev948 op 103, 8B. */
data class IfSetGraphic(val graphicId: Int, val componentHash: Int) : ServerProt

/** IF_SETSPRITE — rev948 op 14, 8B. */
data class IfSetSprite(val componentHash: Int, val spriteValue: Int) : ServerProt

/** IF_SETSCROLLSIZE — rev948 op 158, 9B. */
data class IfSetScrollSize(
    val scrollW: Int,
    val scrollH: Int,
    val componentHash: Int,
    val subSlot: Int,
) : ServerProt

/** IF_SETNPCHEAD_ACTIVE — rev948 op 206, 5B. flag=1 if rawByte == 0x7F. */
data class IfSetNpcHeadActive(val flag: Int, val componentHash: Int) : ServerProt

/** IF_SETMODEL_COORD — rev948 op 165, 14B. */
data class IfSetModelCoord(val npcId: Int, val componentHash: Int, val part1: Int, val part2: Int) : ServerProt

/** IF_SETSCROLLPOS — rev948 op 179, 9B. */
data class IfSetScrollPos(
    val scrollY: Int,
    val componentHash: Int,
    val scrollX: Int,
    val subSlot: Int,
) : ServerProt

// --- Interface complex/direct-allocation setters (A1 §2.3) ---

/** IF_SETPLAYERMODEL_OTHER — rev948 op 70, 25B. Opaque payload until B4 unpacks. */
data class IfSetPlayerModelOther(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelOther && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SELF — rev948 op 60, 25B. Opaque payload until B4. */
data class IfSetPlayerModelSelf(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSelf && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SETPLAYERMODEL_SNAPSHOT — rev948 op 118, 29B. Opaque until B4. */
data class IfSetPlayerModelSnapshot(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is IfSetPlayerModelSnapshot && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** IF_SUBSWAP — rev948 op 40, 8B. Atomic close-A-then-open-B for sub-interface swap. */
data class IfSubSwap(val componentA: Int, val componentB: Int) : ServerProt

// --- Interface trigger / close variants (A1 §2.4) ---

/** IF_TRIGGER_CLOSE — rev948 op 123, 0B. Fires event 0x29 on current top-level. */
@JvmInline
value class IfTriggerClose(val dummy: Int = 0) : ServerProt

/** IF_CLOSESUB_BY_ID — rev948 op 148, 2B. Closes a sub by 16-bit id. */
data class IfCloseSubById(val id: Int) : ServerProt

/** IF_SETTEXT — rev948 op 122, varShort. Sets the text content of a component. */
data class IfSetText(val componentHash: Int, val text: String) : ServerProt

// NOTE: IF_OPENSUB_THUNK is intentionally not modelled; the active rev948 codec does not emit it.

// --- Misc ---

@JvmInline
value class SetReadyFlag(val dummy: Int = 0) : ServerProt

/** CHANGE_LOBBY — rev948 op 49, varShort. Empty packet that triggers lobby transition on client. */
@JvmInline
value class ChangeLobby(val dummy: Int = 0) : ServerProt

@JvmInline
value class NoTimeout(val dummy: Int = 0) : ServerProt

@JvmInline
value class ResetEntityLists(val dummy: Int = 0) : ServerProt

@JvmInline
value class DestroyZoneData(val dummy: Int = 0) : ServerProt

@JvmInline
value class NoopVarA(val dummy: Int = 0) : ServerProt

@JvmInline
value class ClearPendingUpdates(val dummy: Int = 0) : ServerProt

@JvmInline
value class TriggerOnDialogAbort(val dummy: Int = 0) : ServerProt

data class AntiCheatChallenge(val challengeA: Int, val challengeB: Int) : ServerProt

data class MinimapState(val first: Int, val second: Int) : ServerProt

data class EntityAnimAtTile(val value: Int, val target: Int, val cycleOffset: Int) : ServerProt

data class SceneFlag(val value: Int) : ServerProt

@JvmInline
value class CamSmoothReset(val dummy: Int = 0) : ServerProt

data class SetMultiwayState(val state: Int) : ServerProt

data class MinimapFlagA(val value: Int) : ServerProt

data class MinimapFlagB(val value: Int) : ServerProt

data class MidiSong(val payload: ByteArray) : ServerProt {
    init {
        require(payload.size == 5) { "MidiSong payload must be 5 bytes" }
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is MidiSong && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

data class SetNpcOp(val text: String? = null, val cursor: Int = -1) : ServerProt

data class SetPlayerOp2(val value: Int) : ServerProt

data class SetPlayerOp3(val value: Int) : ServerProt

data class PlayerInfoDecode(val slot: Int, val mode: Int = 0) : ServerProt {
    init {
        require(slot in 0..7) { "player info decode slot out of range: $slot" }
        require(mode == 0) { "player info decode mode $mode is not modelled yet" }
    }
}

data class CutsceneData(
    val group: Int,
    val slot: Int,
    val mode: Int,
    val extendedMode: Int,
    val shape: Int,
    val flags: Int,
    val id: Int,
    val primaryLong: Long,
    val primaryInt: Int,
    val secondaryInt: Int,
    val secondaryLong: Long,
    val skipLength: Int,
) : ServerProt

data class CamUpdate(
    val byteA0: Boolean = false,
    val modeA8: Int? = null,
    val modeC0: Int? = null,
    val extended: CamUpdateExtended? = null,
) : ServerProt {
    init {
        require(modeA8 == null || modeA8 in 0..0xFF) { "modeA8 out of range: $modeA8" }
        require(modeC0 == null || modeC0 in 0..0xFF) { "modeC0 out of range: $modeC0" }
    }

    companion object {
        fun firstLight(): CamUpdate =
            CamUpdate(
                byteA0 = true,
                extended = CamUpdateExtended(
                    vector138 = CamVector3(100f, 100f, 100f),
                    vector150 = CamVector3(100f, 100f, 100f),
                    vector168 = CamVector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    vector180 = CamVector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    pair1e8 = CamFloatPair(50f, 10000f),
                    pair1dc = CamFloatPair(1.5707964f, 1.5707964f),
                    byteA4 = 0,
                    ignored80 = 0x00000500,
                    flagsFcFd = CamBooleanPair(first = true, second = true),
                    scriptedCommandCount = 0,
                    pair118 = CamUShortFloat(0, 1f),
                    byte100 = 1,
                    envelope198 = CamEnvelope(
                        first = CamVector3(1f, 1f, 1f),
                        second = CamVector3(1f, 1f, 1f),
                        firstScalar = 1.1f,
                        secondScalar = 1.1f,
                    ),
                    scalar108 = 0.05f,
                    scalar110 = 0.05f,
                ),
            )
    }
}

data class CamVector3(val x: Float, val y: Float, val z: Float)

data class CamFloatPair(val first: Float, val second: Float)

data class CamBooleanPair(val first: Boolean, val second: Boolean)

data class CamUShortFloat(val id: Int, val value: Float) {
    init {
        require(id in 0..0xFFFF) { "camera ushort out of range: $id" }
    }
}

data class CamEnvelope(
    val first: CamVector3,
    val second: CamVector3,
    val firstScalar: Float,
    val secondScalar: Float,
)

data class CamUpdateExtended(
    val vector138: CamVector3? = null,
    val vector150: CamVector3? = null,
    val vector168: CamVector3? = null,
    val vector180: CamVector3? = null,
    val pair1e8: CamFloatPair? = null,
    val pair1dc: CamFloatPair? = null,
    val byteA4: Int? = null,
    val ignored80: Int? = null,
    val flagsFcFd: CamBooleanPair? = null,
    val scriptedCommandCount: Int? = null,
    val pair118: CamUShortFloat? = null,
    val byte100: Int? = null,
    val envelope198: CamEnvelope? = null,
    val scalar108: Float? = null,
    val scalar110: Float? = null,
) {
    init {
        require(byteA4 == null || byteA4 in 0..0xFF) { "byteA4 out of range: $byteA4" }
        require(scriptedCommandCount == null || scriptedCommandCount == 0) {
            "scripted camera commands are not modelled yet"
        }
        require(byte100 == null || byte100 in 0..0xFF) { "byte100 out of range: $byte100" }
    }
}

data class UpdateIgnoreListRaw(
    val mask: Long = 0,
    val encodedFields: ByteArray = byteArrayOf(),
    val entryId: Int = 0,
) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is UpdateIgnoreListRaw &&
            mask == other.mask &&
            encodedFields.contentEquals(other.encodedFields) &&
            entryId == other.entryId)

    override fun hashCode(): Int = 31 * (31 * mask.hashCode() + encodedFields.contentHashCode()) + entryId
}

data class NpcInfoThunk(
    val payloadKind: PayloadKind = PayloadKind.ResetWorldEntityNpcs,
    val payload: ByteArray = byteArrayOf(),
) : ServerProt {
    init {
        require(payloadKind != PayloadKind.ResetWorldEntityNpcs || payload.isEmpty()) {
            "ResetWorldEntityNpcs must not carry payload bytes"
        }
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is NpcInfoThunk && payloadKind == other.payloadKind && payload.contentEquals(other.payload))
    override fun hashCode(): Int = 31 * payloadKind.hashCode() + payload.contentHashCode()

    enum class PayloadKind {
        ResetWorldEntityNpcs,
        RawWorldEntityPayload,
    }
}

data class InventoryEntry(val itemId: Int, val quantity: Int, val metadata: Int = 0)

data class UpdateInvFull(
    val inventoryId: Int,
    val flags: Int = 0,
    val entries: List<InventoryEntry> = emptyList(),
) : ServerProt

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
 * UPDATE_FRIENDLIST — rev948 op 26, varShort. Sends friend list entries.
 * Each entry has display name, world, rank, flags, notes.
 * Fields worldName, platform, worldFlags only present when worldId > 0.
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
 * RUNCLIENTSCRIPT — rev948 op 110, varShort. Invokes a CS2 script on the client.
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

/** HASHED_WORLD_TOKEN — revision-dependent world session nonce packet. */
data class HashedWorldToken(val token: String) : ServerProt

/**
 * SET_WORLD_TARGET — rev948 op 212, varByte. Tells the client the hostname/port of the next lobby
 * target. Only populates the LOBBY login slot in WorldSwitcher — does NOT trigger a world transfer.
 * Cold-lobby Play Now uses the world-target tail in the lobby login response, not this packet.
 *
 * Wire format:
 *   string hostname (CP1252 + null) + ushort worldId + ushort port1 + ushort port2 (all BE).
 */
data class SetWorldTarget(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
) : ServerProt

/**
 * SWITCH_WORLD — rev948 op 213, varByte. Explicit world-switch path, not cold-lobby Play Now.
 *
 * The client handler stores the world target in `WorldSwitcher`, sets MainState to 0x25, which fires
 * the login state machine — the client then opens a TCP connection to hostname:port1 for world login.
 *
 * Rev948 wire format is worldId BE u16, hostname (CP1252 + null), portA BE u16,
 * portB BE u16, reconnectFlag u8. worldId is first, unlike [SetWorldTarget].
 */
data class SwitchWorld(
    val hostname: String,
    val worldId: Int,
    val port1: Int,
    val port2: Int = port1,
    val pendingFlag: Int = 0,
) : ServerProt

/** JCOINS_UPDATE — rev948 op 74, 4B. RuneCoins balance display. Value is BE int. */
data class JcoinsUpdate(val balance: Int) : ServerProt

// === Rebuild packets (per A2) ===

/**
 * REBUILD_NORMAL_SIMPLE — the simple-form (non-instanced) world-login scene build
 * (`ClientState::REBUILD_NORMAL_SIMPLE`). Allocates + installs the BuildArea; without it the
 * client has no scene and stays on the loading screen. Rev948 op 81, varShort, magic 0x85,
 * with a production prefix plus 18-byte tail. The handler parses this tail after packet position
 * has advanced through the prefix:
 *      +0  u8   ignored filler (send 0)
 *      +1  u8   centreZoneZ low
 *      +2  u8   centreZoneZ high            (Z is LE u16: lo then hi; may exceed 255)
 *      +3  u8   magic = 0x85
 *      +4  u16  centreZoneX (BE)
 *      +6  u8   cameraRotation, writeByteAdd (wire = (value + 0x80) & 0xFF)
 *      +7  u8   ignored filler (send 0)
 *      +8  u16  targetWorldId (BE)          (0 for a normal non-instanced login)
 *      +10 u32  packedCoordA (BE)           build-area corner ORIGIN
 *      +14 u32  packedCoordB (BE)           build-area SIZE
 *
 * `packedCoordA/B` use the BuildArea `DecodePackedCoord` packing (`BuildArea::DecodePackedCoord`
 * @ 0x006d4320): `word = (plane << 28) | (hi14 << 14) | lo14`, two 14-bit fields + 2-bit plane.
 * Per `docs/protocol/packed-coord-buildarea-948.md` (948-5-verified), the handler discards plane
 * and passes both 14-bit fields `>> 6` to the scene builder as **map-square (region) corners**:
 *   - `packedCoordA` = the **SW / origin corner** {minRegionX = hi14>>6, minRegionZ = lo14>>6}
 *   - `packedCoordB` = the **NE / far corner**  {maxRegionX = hi14>>6, maxRegionZ = lo14>>6}
 * `hi14` is the X-**tile**, `lo14` the Z-**tile**; the client divides each by 64 (`>>6`) to get
 * the region. The two words are two corners, NOT origin+span, and NOT `zone<<6` (the prior broken
 * model overflowed 14 bits and produced inverted bounds → empty grid → black screen). Build each
 * word from a region corner with [packRegionCoord], or use the world `BuildArea` service.
 */
data class RebuildNormalSimple(
    /** Centre zone X (8-tile units). Rev948: +4 BE u16. */
    val zoneX: Int,
    /** Centre zone Z (8-tile units). Rev948: +1/+2 LE u16. */
    val zoneZ: Int,
    val packedCoordA: Int,
    val packedCoordB: Int,
    /** 948 camera rotation byte (written +0x80). Default 0. */
    val cameraRotation: Int = 0,
    /** 948 instanced-source world id (+8 BE u16). 0 = normal non-instanced login. */
    val targetWorldId: Int = 0,
    /** Legacy encoder field. Ignored by the rev948 encoder. */
    val forceRefresh: Boolean = true,
    /** Legacy encoder field. Ignored by the rev948 encoder. */
    val regionLow: Int = 0,
    /** Rev948 prefix before the 18-byte parser tail. */
    val rebuildPrefix: ByteArray = ByteArray(0),
) : ServerProt {
    init {
        require(rebuildPrefix.size <= 65517) { "Rebuild prefix is too large for VarShort framing" }
    }

    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildNormalSimple &&
            zoneX == other.zoneX &&
            zoneZ == other.zoneZ &&
            packedCoordA == other.packedCoordA &&
            packedCoordB == other.packedCoordB &&
            cameraRotation == other.cameraRotation &&
            targetWorldId == other.targetWorldId &&
            forceRefresh == other.forceRefresh &&
            regionLow == other.regionLow &&
            rebuildPrefix.contentEquals(other.rebuildPrefix))

    override fun hashCode(): Int {
        var result = zoneX
        result = 31 * result + zoneZ
        result = 31 * result + packedCoordA
        result = 31 * result + packedCoordB
        result = 31 * result + cameraRotation
        result = 31 * result + targetWorldId
        result = 31 * result + forceRefresh.hashCode()
        result = 31 * result + regionLow
        result = 31 * result + rebuildPrefix.contentHashCode()
        return result
    }

    companion object {
        /**
         * Builds a `DecodePackedCoord` word from a **map-square (region) corner**, the inverse of
         * `BuildArea::DecodePackedCoord @0x006d4320`.
         *
         * The client recovers a region by `field >> 6`, so each 14-bit field must hold the
         * **tile-aligned** value `region << 6` (`hi14` = X-tile, `lo14` = Z-tile). The low 6 bits
         * are ignored by the build path, so we emit the clean `region << 6` form. plane occupies
         * bits 28-29. Verified against production (`docs/protocol/packed-coord-buildarea-948.md`
         * §5.3): `packRegionCoord(26, 37) = 0x01a00940`.
         *
         * Callers should prefer the world `BuildArea` service (`BuildArea.packedCoordA/B`), which
         * also enforces non-inverted bounds; this helper is the low-level encode used by the
         * service and by tests.
         *
         * @param regionX map-square X — recovered as `((word >> 14) & 0x3FFF) >> 6`.
         * @param regionZ map-square Z — recovered as `(word & 0x3FFF) >> 6`.
         */
        fun packRegionCoord(regionX: Int, regionZ: Int, plane: Int = 0): Int =
            ((plane and 0x3) shl 28) or
                (((regionX shl 6) and 0x3FFF) shl 14) or
                ((regionZ shl 6) and 0x3FFF)
    }
}

data class RebuildRegion(val scenes: List<RebuildRegionScene>) : ServerProt {
    init {
        require(scenes.size in 0..0xFF) { "rebuild region scene count out of range: ${scenes.size}" }
    }

    companion object {
        fun firstLight(): RebuildRegion =
            RebuildRegion(
                listOf(
                    RebuildRegionScene(sceneId = 4, secondaryIds = listOf(1)),
                    RebuildRegionScene(
                        sceneId = 1,
                        primaryIds = listOf(0),
                        secondaryIds = listOf(1, 2, 4),
                        primaryMetadata = listOf(0),
                        matrix = listOf(listOf(0, 0, 0)),
                    ),
                    RebuildRegionScene(sceneId = 2, secondaryIds = listOf(22, 15, 17, 11)),
                    RebuildRegionScene(sceneId = 3, secondaryIds = listOf(36, 31, 33, 35)),
                ),
            )
    }
}

data class RebuildRegionScene(
    val sceneId: Int,
    val primaryIds: List<Int> = emptyList(),
    val secondaryIds: List<Int> = emptyList(),
    val primaryMetadata: List<Int> = emptyList(),
    val matrix: List<List<Int?>> = emptyList(),
) {
    init {
        require(primaryIds.size in 0..0xFF) { "primary id count out of range: ${primaryIds.size}" }
        require(secondaryIds.size in 0..0xFF) { "secondary id count out of range: ${secondaryIds.size}" }
        require(primaryMetadata.size == primaryIds.size) {
            "primary metadata count ${primaryMetadata.size} does not match primary id count ${primaryIds.size}"
        }
        require(matrix.size == primaryIds.size) {
            "matrix row count ${matrix.size} does not match primary id count ${primaryIds.size}"
        }
        require(primaryMetadata.all { it in -128..127 }) { "primary metadata must fit signed byte" }
        require(matrix.all { it.size == secondaryIds.size }) {
            "matrix column count must match secondary id count ${secondaryIds.size}"
        }
    }
}

/**
 * REBUILD_WORLDENTITY — rev948 op 186, varShort. Triple-nested (level / regionX /
 * regionY) -1-terminated XTEA stream. Opaque payload until B4 defines a structured API.
 */
data class RebuildWorldEntity(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is RebuildWorldEntity && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

// === Zone update packets (per A3) ===

/** UPDATE_ZONE_FULL_FOLLOWS — rev948 op 78, 3B. Sets zone globals + scene-clear. */
data class UpdateZoneFullFollowsV2(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/** UPDATE_ZONE_PARTIAL_FOLLOWS — rev948 op 41, 3B. Sets zone globals only. */
data class UpdateZonePartialFollows(val level: Int, val zoneX: Int, val zoneY: Int) : ServerProt

/**
 * UPDATE_ZONE_PARTIAL_ENCLOSED — rev948 op 76, varShort. Carries a header plus embedded
 * sub-packets; prefer standalone main-table zone opcodes when possible.
 */
data class UpdateZonePartialEnclosed(
    val level: Int,
    val zoneX: Int,
    val zoneY: Int,
    val subPackets: List<ServerProt>,
) : ServerProt

/** LOC_ADD — rev948 op 90, varByte. Adds a location at a zone-relative tile. */
data class LocAdd(
    val packedCoord: Int,
    val locId: Int,
    val shapeFlags: Int,
    val extra: Int? = null,
) : ServerProt

/** LOC_DEL — rev948 op 16, 2B. Removes a location by shape+rotation at a tile. */
data class LocDel(val shapeFlags: Int, val packedCoord: Int) : ServerProt

/**
 * LOC_CUSTOMISE — rev948 op 50, varByte. Header (4B template + packed coord + shape +
 * flags) + optional (model list, recolor src list, recolor dst list). Opaque payload until B4.
 */
data class LocCustomise(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is LocCustomise && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** LOC_PREFETCH — rev948 op 6, 7B. Pre-load a future location. */
data class LocPrefetch(
    val visTime: Int,
    val packedCoord: Int,
    val shapeFlags: Int,
    val locId: Int,
) : ServerProt

/** LOC_ANIM_SPECIFIC — rev948 op 21, 10B. Like LOC_ANIM with constant flag. */
data class LocAnimSpecific(
    val packedCoord: Int,
    val animId: Int,
    val shapeFlags: Int,
    val unknown1: Int,
    val delay: Int,
    val speed: Int,
) : ServerProt

/** LOC_MERGE — rev948 op 170, 5B. Merge a location's model with another entity. */
data class LocMerge(val entityServerIndex: Int, val packedCoordAndShape: Int) : ServerProt

/** OBJ_ADD — rev948 op 46, 5B. Add a ground item to a zone tile. */
data class ObjAdd(
    val packedCoord: Int,
    val objId: Int,
    val count: Int,
) : ServerProt

/** OBJ_DEL — rev948 op 107, 3B. Remove a ground item from a zone tile. */
data class ObjDel(val packedCoord: Int, val objIdLo: Int, val objIdHi: Int) : ServerProt

/** OBJ_COUNT — rev948 op 125, 7B. Update a ground item's stack count. */
data class ObjCount(
    val playerIndex: Int,
    val objIdLo: Int,
    val objIdHi: Int,
    val packedCoord: Int,
    val count: Int,
) : ServerProt

/** OBJ_REVEAL — rev948 op 71, 7B. Reveal a ground item's true count to the viewer. */
data class ObjReveal(
    val packedCoord: Int,
    val objId: Int,
    val oldCount: Int,
    val newCount: Int,
) : ServerProt

/** MAP_ANIM — rev948 op 113, 11B. Place/remove a spot animation at a zone tile. */
data class MapAnim(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
) : ServerProt

/** MAP_ANIM_SPECIFIC — rev948 op 183, 14B. Like MAP_ANIM with fine sub-tile offsets. */
data class MapAnimSpecific(
    val packedCoord: Int,
    val entityIdLow: Int,
    val entityIdHigh: Int,
    val heightOffset: Int,
    val angleHeight: Int,
    val unknown: Int,
    val fineOffset: Int,
) : ServerProt

/** MAP_PROJANIM — rev948 op 65, 20B. Spawn a projectile animation between zone tiles. */
data class MapProjAnim(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnim && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** MAP_PROJANIM_HALT — rev948 op 164, 28B. Projectile with fine src/dst offsets. */
data class MapProjAnimHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is MapProjAnimHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC — rev948 op 151, 21B. Projectile with double-resolution coords. */
data class ProjAnimSpecific(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecific && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** PROJANIM_SPECIFIC_HALT — rev948 op 177, 29B. Combines halt plus double-res. */
data class ProjAnimSpecificHalt(val payload: ByteArray) : ServerProt {
    override fun equals(other: Any?): Boolean = this === other ||
        (other is ProjAnimSpecificHalt && payload.contentEquals(other.payload))
    override fun hashCode(): Int = payload.contentHashCode()
}

/** SOUND_AREA — rev948 op 168, varByte. Play an area sound effect at a zone tile. */
data class SoundArea(
    val packedCoord: Int,
    val soundId: Int,
    val volume: Int,
    val paramA: Int,
    val paramB: Int,
) : ServerProt

// NOTE: LOC_ANIM is not modelled as ServerProt; emit it inline inside UpdateZonePartialEnclosed.

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

// --- World list ---

data class WorldListPacket(
    val worldList: WorldList,
    val fullRefresh: Boolean,         // true = send full world defs, false = delta (counts only)
) : ServerProt
