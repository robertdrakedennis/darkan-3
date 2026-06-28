package org.darkan.core.net.prot

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

data class UpdateRunWeight(val value: Int) : ServerProt

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

data class InventoryItemParam(val key: Int, val value: Int)

data class InventoryEntry(
    val itemId: Int,
    val quantity: Int,
    val params: List<InventoryItemParam> = emptyList(),
)

data class UpdateInvFull(
    val inventoryId: Int,
    val flags: Int = 0,
    val entries: List<InventoryEntry> = emptyList(),
) : ServerProt

data class InventoryPartialEntry(
    val slot: Int,
    val itemId: Int,
    val quantity: Int,
    val params: List<InventoryItemParam> = emptyList(),
)

data class UpdateInvPartial(
    val inventoryId: Int,
    val flags: Int = 0,
    val entries: List<InventoryPartialEntry> = emptyList(),
) : ServerProt

/**
 * UPDATE_RUNENERGY — run energy as a single byte (g1, 0..100 RAW percentage, no scaling). The real
 * run-energy opcode is **op 13 (0x0d)** in rev948 and **op 19** in rev947; verified in rs2client
 * 948-5: handler `jag::packethandlers::Misc::UPDATE_RUNENERGY` (@0x1000448a0) writes the run-energy
 * status field (status +0x18). NOTE: rev948 op 80 is NOT run energy — it is the private-chat filter
 * [SetFilterPrivate]; binding run energy there silently flipped the chat filter. See
 * recorder-capture-points.md §10.4.
 */
data class UpdateRunenergy(val energy: Int) : ServerProt

/**
 * SETFILTER_PRIVATE — rev948 op 80 (0x50), fixed 1 byte (g1). Sets the player's private-chat filter
 * mode: {0=On, 1=Friends, 2=Off}. Verified in rs2client 948-5: handler
 * `jag::packethandlers::Misc::SETFILTER_PRIVATE` (@0x10009f760) writes `(Client+0x19780)+0x60`.
 * This opcode was previously mis-bound to [UpdateRunenergy] (run energy) — a real defect: the client
 * reads run energy on op 13, so an op-80 run-energy send actually flipped the private-chat filter.
 * (recorder-capture-points.md §10.4.) Distinct from [ChatFilterSettingsPrivateChat] (op 156,
 * `SET_CHAT_FILTER_B`).
 */
data class SetFilterPrivate(val filter: Int) : ServerProt

data class SetPlayerOp(val slot: Int, val text: String?, val priority: Boolean = false) : ServerProt
