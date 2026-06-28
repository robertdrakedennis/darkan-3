package org.darkan.core.net.prot

import kotlinx.serialization.Serializable
import org.darkan.core.model.ChatMessageType

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
