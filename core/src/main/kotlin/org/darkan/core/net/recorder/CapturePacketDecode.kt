package org.darkan.core.net.recorder

import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.AbortPDialog
import org.darkan.core.net.prot.AntiCheatChallengeResponse
import org.darkan.core.net.prot.CameraOrientation
import org.darkan.core.net.prot.ChatFilterSettingsPrivateChat
import org.darkan.core.net.prot.ChatSetFilter
import org.darkan.core.net.prot.ClanChannelKickUser
import org.darkan.core.net.prot.ClientInputCoordinateMode
import org.darkan.core.net.prot.ClientInputEvent
import org.darkan.core.net.prot.ClientInputEventBatch
import org.darkan.core.net.prot.ClientInputEventEncoding
import org.darkan.core.net.prot.ClientProfileBlock
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.ClientSetVarcBitLarge
import org.darkan.core.net.prot.ClientSetVarcBitSmall
import org.darkan.core.net.prot.ClientSetVarcLarge
import org.darkan.core.net.prot.ClientSetVarcSmall
import org.darkan.core.net.prot.ClientSetVarcStr
import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.DisplayMetrics
import org.darkan.core.net.prot.FriendListAdd
import org.darkan.core.net.prot.FriendListDel
import org.darkan.core.net.prot.IfButton
import org.darkan.core.net.prot.IfCloseSub
import org.darkan.core.net.prot.IfOpenSub
import org.darkan.core.net.prot.IfOpenTop
import org.darkan.core.net.prot.IfSetAnim
import org.darkan.core.net.prot.IfSetAnimActive
import org.darkan.core.net.prot.IfSetEvents
import org.darkan.core.net.prot.IfSetEvents1
import org.darkan.core.net.prot.IfSetGraphic
import org.darkan.core.net.prot.IfSetHide
import org.darkan.core.net.prot.IfSetHttpImage
import org.darkan.core.net.prot.IfSetModel
import org.darkan.core.net.prot.IfSetNpcHead
import org.darkan.core.net.prot.IfSetObjectActive
import org.darkan.core.net.prot.IfSetPosition
import org.darkan.core.net.prot.IfSetText
import org.darkan.core.net.prot.IgnoreListAdd
import org.darkan.core.net.prot.JcoinsUpdate
import org.darkan.core.net.prot.MapBuildComplete
import org.darkan.core.net.prot.MessagePrivateSend
import org.darkan.core.net.prot.MessagePublicSend
import org.darkan.core.net.prot.NativeMouseClick
import org.darkan.core.net.prot.Ping
import org.darkan.core.net.prot.RequestWorldList
import org.darkan.core.net.prot.ResumePNameDialog
import org.darkan.core.net.prot.RunClientScript
import org.darkan.core.net.prot.SceneGraphReport
import org.darkan.core.net.prot.SceneRebuildTimingReport
import org.darkan.core.net.prot.SceneTimingBase
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.SetFilterPrivate
import org.darkan.core.net.prot.UpdateStat
import org.darkan.core.net.prot.UpdateRunenergy
import org.darkan.core.net.prot.VarpBitLarge
import org.darkan.core.net.prot.VarpBitSmall
import org.darkan.core.net.prot.VarpLarge
import org.darkan.core.net.prot.VarpLong
import org.darkan.core.net.prot.VarpSmall
import org.darkan.core.net.prot.revision.rev948.IfEventsWire
import world.gregs.voidps.buffer.readByteAdd
import world.gregs.voidps.buffer.readByteInverse
import world.gregs.voidps.buffer.readByteSubtract
import world.gregs.voidps.buffer.readRSString
import world.gregs.voidps.buffer.readShortLittle
import world.gregs.voidps.buffer.readUByte
import world.gregs.voidps.buffer.readUIntInverseMiddle
import world.gregs.voidps.buffer.readUIntLittle
import world.gregs.voidps.buffer.readUIntMiddle
import world.gregs.voidps.buffer.readUShort
import world.gregs.voidps.buffer.readUShortAdd
import world.gregs.voidps.buffer.readUShortAddLittle
import world.gregs.voidps.buffer.readUShortLittle
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

/**
 * Structured-decode helpers over the shared [Codec], mirroring the engine's `PacketLogger`:
 *  - **c2s**: run the registered [Codec.clientProtsByOpcode] decoder, render via `toString()`.
 *  - **s2c**: run the registered [Codec.serverDecodersByOpcode] display decoder.
 *
 * A decoder that throws (short body, not-yet-mapped field) yields `null` rather than aborting the
 * enrich pass — the raw body is still preserved upstream.
 */
object CapturePacketDecode {

    data class RoundTrip(
        val decoded: String?,
        val roundTrips: Boolean,
        val reencoded: ByteArray?,
        val failure: String?,
    ) {
        val decodedOk: Boolean get() = decoded != null
        val roundTripSupported: Boolean get() = reencoded != null || failure == ROUND_TRIP_MISMATCH
    }

    fun decodeClient(codec: Codec, opcode: Int, body: ByteArray): String? {
        val entry = codec.clientProtsByOpcode[opcode]
        val decoder = entry?.decoder
        if (entry != null && decoder == null) {
            if (body.isNotEmpty()) return null
            return codec.createInstanceForOpcode<ClientProt>(opcode)?.toString()
        }
        if (decoder == null) return decodeSpecialClient(opcode, body)
        return runCatching {
            val source = Buffer().apply { write(body) }
            runBlocking { decoder.invoke(source, body.size) }.toString()
        }.getOrNull()
    }

    fun decodeServer(codec: Codec, opcode: Int, body: ByteArray): String? {
        val decoder = codec.serverDecodersByOpcode[opcode] ?: return null
        return runCatching {
            val source = Buffer().apply { write(body) }
            runBlocking { decoder.invoke(source, body.size) }
        }.getOrNull()
    }

    /** True when this c2s opcode has a structured decoder registered in [codec]. */
    fun hasClientDecoder(codec: Codec, opcode: Int): Boolean =
        codec.clientProtsByOpcode[opcode] != null || opcode == 105

    fun roundTripServer(codec: Codec, opcode: Int, body: ByteArray): RoundTrip {
        val decoded = decodeServer(codec, opcode, body)
            ?: return RoundTrip(null, roundTrips = false, reencoded = null, failure = DECODE_FAILED)
        val prot = decodeServerProt(opcode, body)
            ?: return RoundTrip(decoded, roundTrips = false, reencoded = null, failure = ROUND_TRIP_UNSUPPORTED)
        val reencoded = encodeServerProt(codec, prot)
            ?: return RoundTrip(decoded, roundTrips = false, reencoded = null, failure = ROUND_TRIP_UNSUPPORTED)
        val ok = reencoded.contentEquals(body)
        return RoundTrip(decoded, ok, reencoded, if (ok) null else ROUND_TRIP_MISMATCH)
    }

    fun roundTripClient(codec: Codec, opcode: Int, body: ByteArray): RoundTrip {
        val decoded = decodeClient(codec, opcode, body)
            ?: return RoundTrip(null, roundTrips = false, reencoded = null, failure = DECODE_FAILED)
        val packet = decodeClientPacket(codec, opcode, body)
            ?: return RoundTrip(decoded, roundTrips = false, reencoded = null, failure = ROUND_TRIP_UNSUPPORTED)
        val reencoded = encodeClientPacket(packet)
            ?: return RoundTrip(decoded, roundTrips = false, reencoded = null, failure = ROUND_TRIP_UNSUPPORTED)
        val ok = reencoded.contentEquals(body)
        return RoundTrip(decoded, ok, reencoded, if (ok) null else ROUND_TRIP_MISMATCH)
    }

    private fun decodeSpecialClient(opcode: Int, body: ByteArray): String? =
        when (opcode) {
            105 -> decodeServerClientVarDataIntBlock(body)
            else -> null
        }

    private fun decodeServerClientVarDataIntBlock(body: ByteArray): String? {
        if (body.isEmpty()) return null
        val payloadSize = body.size - 1
        if (payloadSize % 6 != 0) {
            return "ServerClientVarData(ack=${body[0].toInt() and 0xFF}, rawBytes=$payloadSize, mixedTypes=unparsed)"
        }

        val entries = ArrayList<Pair<Int, Int>>(payloadSize / 6)
        var offset = 1
        while (offset < body.size) {
            val id = readUShort(body, offset)
            val value = readInt(body, offset + 2)
            entries += id to value
            offset += 6
        }

        val first = entries.take(8).joinToString(", ") { (id, value) -> "$id=$value" }
        val last = entries.takeLast(4).joinToString(", ") { (id, value) -> "$id=$value" }
        return "ServerClientVarData(ack=${body[0].toInt() and 0xFF}, intRecords=${entries.size}, first=[$first], last=[$last])"
    }

    private fun readUShort(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 8) or
            (bytes[offset + 1].toInt() and 0xFF)

    private fun readInt(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)

    private fun decodeServerProt(opcode: Int, body: ByteArray): ServerProt? = runCatching {
        val src = Buffer().apply { write(body) }
        when (opcode) {
            61 -> VarpSmall(src.readUShort(), src.readByteSubtract())
            28 -> {
                val value = src.readInt()
                val id = src.readUShort()
                VarpLarge(id, value)
            }
            147 -> {
                val id = src.readUShortAdd() and 0xFFFF
                val high = src.readUIntInverseMiddle()
                val low = src.readUIntInverseMiddle()
                VarpLong(id, (high.toLong() shl 32) or (low.toLong() and 0xFFFFFFFFL))
            }
            10 -> VarpBitSmall(src.readUShort(), src.readByteSubtract())
            51 -> VarpBitLarge(src.readUShort(), src.readUIntInverseMiddle())
            47 -> {
                val value = src.readByteAdd()
                val id = src.readUShortLittle()
                ClientSetVarcSmall(id, value)
            }
            64 -> {
                val value = src.readUIntMiddle()
                val id = src.readUShortAddLittle()
                ClientSetVarcLarge(id, value)
            }
            48 -> {
                val value = src.readByte().toInt()
                val id = src.readUShortAdd() and 0xFFFF
                ClientSetVarcBitSmall(id, value)
            }
            69 -> ClientSetVarcBitLarge(src.readUShort(), src.readInt())
            92 -> ClientSetVarcStr(src.readUShortLittle(), src.readRSString())
            44 -> {
                val xp = src.readUIntLittle()
                val level = src.readUByte()
                val skillId = src.readByteInverse()
                UpdateStat(skillId = skillId, xp = xp, level = level)
            }
            39 -> IfOpenTop(topLevelId = src.readUIntLittle(), subId = src.readUShort())
            94 -> IfOpenSub(subId = src.readUShortLittle(), walkable = src.readUShortLittle(), parentHash = src.readInt())
            35 -> IfSetEvents(IfEventsWire.decode(body))
            97 -> IfSetEvents1(
                componentHash = src.readUIntInverseMiddle(),
                eventsMask = src.readUShortAdd() and 0xFFFF,
                endSlot = src.readUShortLittle(),
                startSlot = src.readUShortAdd() and 0xFFFF,
            )
            91 -> {
                val hide = src.readUByte() == 0x81
                val componentHash = src.readUIntMiddle()
                IfSetHide(componentHash = componentHash, hide = hide)
            }
            62 -> IfCloseSub(src.readUIntLittle())
            96 -> IfSetAnimActive(src.readUIntInverseMiddle())
            101 -> IfSetObjectActive(src.readUIntMiddle())
            102 -> IfSetModel(value = src.readUIntMiddle(), componentHash = src.readInt())
            30 -> IfSetGraphic(componentHash = src.readUIntInverseMiddle(), graphicId = src.readUIntInverseMiddle())
            103 -> IfSetAnim(animationId = src.readUIntInverseMiddle(), componentHash = src.readUIntInverseMiddle())
            122 -> {
                val text = src.readRSString()
                val componentHash = src.readUIntInverseMiddle()
                IfSetText(componentHash = componentHash, text = text)
            }
            115 -> IfSetNpcHead(scale = src.readUShort(), partA = src.readUShortLittle(), partB = src.readUShortLittle(), componentHash = src.readUIntLittle())
            152 -> IfSetHttpImage(src.readRSString())
            13 -> UpdateRunenergy(src.readUByte())
            80 -> SetFilterPrivate(src.readUByte())
            191 -> JcoinsUpdate(src.readInt())
            74 -> SceneTimingBase(src.readInt())
            156 -> ChatFilterSettingsPrivateChat(src.readUByte())
            110 -> src.readRunClientScript()
            else -> null
        }
    }.getOrNull()

    private fun decodeClientPacket(codec: Codec, opcode: Int, body: ByteArray): ClientProt? {
        val entry = codec.clientProtsByOpcode[opcode] ?: return null
        val decoder = entry.decoder
        if (decoder == null) return if (body.isEmpty()) codec.createInstanceForOpcode(opcode) else null
        return runCatching {
            val source = Buffer().apply { write(body) }
            runBlocking { decoder.invoke(source, body.size) }
        }.getOrNull()
    }

    private fun encodeServerProt(codec: Codec, prot: ServerProt): ByteArray? = runCatching {
        val entry = codec.serverProts[prot::class] ?: return null
        val encoder = entry.encoder ?: return null
        val channel = ByteChannel()
        runBlocking {
            encoder.invoke(prot, channel)
            channel.flush()
            val out = ByteArray(channel.availableForRead)
            channel.readFully(out)
            channel.close()
            out
        }
    }.getOrNull()

    private fun encodeClientPacket(packet: ClientProt): ByteArray? {
        val out = PacketBodyWriter()
        when (packet) {
            is Ping, is AbortPDialog, is MapBuildComplete -> Unit
            is SceneGraphReport -> out.i4(packet.value)
            is CameraOrientation -> {
                out.u1(packet.yaw)
                out.u1(packet.yaw ushr 8)
                out.u1((packet.pitch and 0xFF) - 128)
                out.u1(packet.pitch ushr 8)
            }
            is NativeMouseClick -> {
                out.u2le(packet.field294)
                out.i4(packet.field28c)
                out.u2le(packet.field290)
                out.u2le(packet.clickY)
                out.u2AddLe(packet.clickX)
            }
            is DisplayMetrics -> {
                out.u1(packet.flags)
                out.u2(packet.width)
                out.u2(packet.height)
                out.u1(packet.tail)
            }
            is ClientProfileBlock -> packet.values.forEach { out.u1(it) }
            is SceneRebuildTimingReport -> out.i4(packet.elapsedTicks)
            is ClientInputEventBatch -> {
                for (event in packet.events) out.inputEvent(event)
                out.raw(packet.trailingBytes)
            }
            is AntiCheatChallengeResponse -> {
                out.i4(packet.challengeA)
                out.i4le(packet.challengeB)
                out.u1(packet.sequence - 128)
            }
            is RequestWorldList -> out.i4(packet.worldlistVersion)
            is FriendListDel -> out.rsString(packet.displayName)
            is IgnoreListAdd -> out.rsString(packet.displayName)
            is FriendListAdd -> out.rsString(packet.displayName)
            is ClanChannelKickUser -> out.rsString(packet.username)
            is ChatSetFilter -> {
                out.u1(packet.public)
                out.u1(packet.private)
                out.u1(packet.trade)
            }
            is ResumePNameDialog -> out.rsString(packet.name)
            is IfButton -> {
                out.i4le(packet.interfaceHash)
                out.u2AddLe(packet.slotId)
                out.u2Add(packet.itemId)
            }
            is MessagePublicSend -> {
                out.u1(packet.color)
                out.u1(packet.effect)
                out.raw(packet.message)
            }
            is MessagePrivateSend -> {
                out.rsString(packet.toDisplayName)
                out.raw(packet.message)
            }
            else -> return null
        }
        return out.toByteArray()
    }

    private fun Buffer.readRunClientScript(): RunClientScript {
        val types = readRSString()
        val args = ArrayList<Any>(types.length)
        for (c in types.reversed()) {
            when (c) {
                'i' -> args.add(readInt())
                's' -> args.add(readRSString())
                'l' -> args.add(readLong())
                else -> args.add(readInt())
            }
        }
        val scriptId = readInt()
        args.reverse()
        return RunClientScript(scriptId, types, args.toTypedArray())
    }

    private class PacketBodyWriter {
        private val out = ByteArrayOutputStream()

        fun toByteArray(): ByteArray = out.toByteArray()
        fun raw(bytes: ByteArray) { out.write(bytes) }
        fun u1(value: Int) { out.write(value and 0xFF) }
        fun u2(value: Int) {
            u1(value ushr 8)
            u1(value)
        }
        fun u2le(value: Int) {
            u1(value)
            u1(value ushr 8)
        }
        fun u2Add(value: Int) {
            u1(value ushr 8)
            u1(value + 128)
        }
        fun u2AddLe(value: Int) {
            u1(value + 128)
            u1(value ushr 8)
        }
        fun i4(value: Int) {
            u1(value ushr 24)
            u1(value ushr 16)
            u1(value ushr 8)
            u1(value)
        }
        fun i4le(value: Int) {
            u1(value)
            u1(value ushr 8)
            u1(value ushr 16)
            u1(value ushr 24)
        }
        fun rsString(value: String) {
            raw(value.toByteArray(StandardCharsets.ISO_8859_1))
            u1(0)
        }
        fun inputEvent(event: ClientInputEvent) {
            when (event.encoding) {
                ClientInputEventEncoding.DELTA_SMALL -> {
                    val packed = ((event.timeDelta20 and 0x07) shl 12) or
                        (((event.x ?: 0) + 32) shl 6) or
                        ((event.y ?: 0) + 32)
                    u2(packed)
                }
                ClientInputEventEncoding.DELTA_MEDIUM -> {
                    u1(0x80 + event.timeDelta20)
                    u1((event.x ?: 0) + 128)
                    u1((event.y ?: 0) + 128)
                }
                ClientInputEventEncoding.ABSOLUTE_SHORT_TIME -> {
                    u1(0xC0 + event.timeDelta20)
                    absoluteCoordinates(event)
                }
                ClientInputEventEncoding.ABSOLUTE_LONG_TIME -> {
                    u1(0xE0 or ((event.timeDelta20 ushr 8) and 0x1F))
                    u1(event.timeDelta20)
                    absoluteCoordinates(event)
                }
            }
        }
        private fun absoluteCoordinates(event: ClientInputEvent) {
            if (event.coordinateMode == ClientInputCoordinateMode.SENTINEL) {
                raw(byteArrayOf(0x80.toByte(), 0, 0, 0))
                return
            }
            val x = event.x ?: 0
            val y = event.y ?: 0
            u2(y)
            u2(x)
        }
    }

    private const val DECODE_FAILED = "decode-failed"
    private const val ROUND_TRIP_UNSUPPORTED = "round-trip-unsupported"
    private const val ROUND_TRIP_MISMATCH = "round-trip-mismatch"
}
