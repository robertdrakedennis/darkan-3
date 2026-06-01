package org.darkan.core.net.prot

import kotlinx.serialization.Serializable

/**
 * Marker interface for all client-to-server packets.
 *
 * Each packet is a data class (or value class for zero-payload packets) that
 * carries decoded fields. The Codec registry maps opcodes to their size and
 * decoder lambda for a given revision.
 */
interface ClientProt

@JvmInline
value class Ping(val dummy: Int = 0) : ClientProt

data class RequestWorldList(val worldlistVersion: Int) : ClientProt

// --- Social ---

@Serializable data class FriendListAdd(val displayName: String) : ClientProt
@Serializable data class FriendListDel(val displayName: String) : ClientProt
@Serializable data class IgnoreListAdd(val displayName: String) : ClientProt

// --- Chat ---

/** MESSAGE_PUBLIC (opcode 120, varByte) — public chat message. */
@Serializable data class MessagePublicSend(val color: Int, val effect: Int, val message: ByteArray) : ClientProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessagePublicSend) return false
        return color == other.color && effect == other.effect && message.contentEquals(other.message)
    }
    override fun hashCode(): Int = 31 * (31 * color + effect) + message.contentHashCode()
}

/** MESSAGE_PRIVATE (opcode 121, varShort) — send a private message to another player. */
@Serializable data class MessagePrivateSend(val toDisplayName: String, val message: ByteArray) : ClientProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessagePrivateSend) return false
        return toDisplayName == other.toDisplayName && message.contentEquals(other.message)
    }
    override fun hashCode(): Int = 31 * toDisplayName.hashCode() + message.contentHashCode()
}

/** CLANCHANNEL_KICKUSER (opcode 24, varByte) — kick a user from clan/friends channel. */
@Serializable data class ClanChannelKickUser(val username: String) : ClientProt

// --- Interface ---

/**
 * IF_BUTTON1..IF_BUTTON7 + IF_BUTTON10 (948: op 127/103/92/45/30/68/43/21, 8B fixed) — interface
 * component CLICK. [buttonId] is the option index (1..7, 10). interfaceHash packs the interface id
 * (ushr 16) and component id (and 0xFFFF). slotId/itemId identify the clicked sub-element.
 */
data class IfButton(val buttonId: Int, val interfaceHash: Int, val slotId: Int, val itemId: Int) : ClientProt

/** RESUME_P_NAMEDIALOG (opcode 84, varByte) — typed display name from name dialog. */
data class ResumePNameDialog(val name: String) : ClientProt

/** Catch-all for opcodes we haven't implemented handlers for yet. Carries opcode for logging. */
data class UnhandledClientProt(val opcode: Int, val name: String, val size: Int) : ClientProt
