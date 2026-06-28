package org.darkan.core.net.recorder

import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.write
import org.darkan.core.net.prot.Codec

/**
 * Structured-decode helpers over the shared [Codec], mirroring the engine's `PacketLogger`:
 *  - **c2s**: run the registered [Codec.clientProtsByOpcode] decoder, render via `toString()`.
 *  - **s2c**: run the registered [Codec.serverDecodersByOpcode] display decoder.
 *
 * A decoder that throws (short body, not-yet-mapped field) yields `null` rather than aborting the
 * enrich pass — the raw body is still preserved upstream.
 */
object CapturePacketDecode {

    fun decodeClient(codec: Codec, opcode: Int, body: ByteArray): String? {
        val decoder = codec.clientProtsByOpcode[opcode]?.decoder ?: return decodeSpecialClient(opcode, body)
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
        codec.clientProtsByOpcode[opcode]?.decoder != null || opcode == 105

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
}
