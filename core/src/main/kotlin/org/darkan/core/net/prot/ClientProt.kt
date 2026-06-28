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

@JvmInline
value class AbortPDialog(val dummy: Int = 0) : ClientProt

@JvmInline
value class MapBuildComplete(val dummy: Int = 0) : ClientProt

data class AntiCheatChallengeResponse(val challengeA: Int, val challengeB: Int, val sequence: Int) : ClientProt

data class RequestWorldList(val worldlistVersion: Int) : ClientProt

data class SceneGraphReport(val value: Int) : ClientProt

data class CameraOrientation(val yaw: Int, val pitch: Int) : ClientProt

data class NativeMouseClick(
    val field294: Int,
    val field28c: Int,
    val field290: Int,
    val clickY: Int,
    val clickX: Int,
) : ClientProt

data class DisplayMetrics(val flags: Int, val width: Int, val height: Int, val tail: Int) : ClientProt

data class ClientProfileBlock(val values: List<Int>) : ClientProt

data class SceneRebuildTimingReport(val elapsedTicks: Int) : ClientProt

/**
 * Batched native input events from the client watcher. [timeDelta20] is the sender's timestamp
 * delta divided by 20; `8191` is used by the client for an absolute/start event.
 */
data class ClientInputEventBatch(val events: List<ClientInputEvent>, val trailingBytes: ByteArray = byteArrayOf()) : ClientProt {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientInputEventBatch) return false
        return events == other.events && trailingBytes.contentEquals(other.trailingBytes)
    }

    override fun hashCode(): Int = 31 * events.hashCode() + trailingBytes.contentHashCode()
}

data class ClientInputEvent(
    val encoding: ClientInputEventEncoding,
    val coordinateMode: ClientInputCoordinateMode,
    val timeDelta20: Int,
    val x: Int?,
    val y: Int?,
)

enum class ClientInputEventEncoding {
    DELTA_SMALL,
    DELTA_MEDIUM,
    ABSOLUTE_SHORT_TIME,
    ABSOLUTE_LONG_TIME,
}

enum class ClientInputCoordinateMode {
    DELTA,
    ABSOLUTE,
    SENTINEL,
}

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

/** CHAT_SETFILTER (opcode 94, fixed 3) — public, private, trade chat filter modes. */
data class ChatSetFilter(val public: Int, val private: Int, val trade: Int) : ClientProt

// --- Interface ---

/**
 * IF_BUTTON1..IF_BUTTON10 (948: op 127/103/92/45/30/68/43/21/13/23, 8B fixed) — interface
 * component CLICK. [buttonId] is the option index (1..10). interfaceHash packs the interface id
 * (ushr 16) and component id (and 0xFFFF). slotId/itemId identify the clicked sub-element.
 */
data class IfButton(val buttonId: Int, val interfaceHash: Int, val slotId: Int, val itemId: Int) : ClientProt

data class MacOsLobbyHandoff(val button: IfButton?) : ClientProt

/** RESUME_P_NAMEDIALOG (opcode 84, varByte) — typed display name from name dialog. */
data class ResumePNameDialog(val name: String) : ClientProt

/** Catch-all for opcodes we haven't implemented handlers for yet. Carries opcode for logging. */
data class UnhandledClientProt(val opcode: Int, val name: String, val size: Int) : ClientProt
