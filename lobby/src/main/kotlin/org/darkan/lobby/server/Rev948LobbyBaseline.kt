package org.darkan.lobby.server

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.prot.ClientSetVarcLarge
import org.darkan.core.net.prot.ClientSetVarcSmall
import org.darkan.core.net.prot.ServerProt
import org.darkan.core.net.prot.VarpLarge
import org.darkan.core.net.prot.VarpLong
import org.darkan.core.net.prot.VarpSmall
import org.darkan.core.net.session.GameSession

object Rev948LobbyBaseline {
    private const val RESOURCE = "/lobby/rev948-lobby-baseline-vars.tsv"

    private val packets: List<ServerProt> by lazy {
        val stream = requireNotNull(Rev948LobbyBaseline::class.java.getResourceAsStream(RESOURCE)) {
            "Missing $RESOURCE"
        }
        stream.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() }
                .map(::parseLine)
                .toList()
        }
    }

    suspend fun sendTo(session: GameSession) {
        for (packet in packets) {
            session.send(packet)
        }
        logInfo("Sent ${packets.size} rev948 lobby baseline vars to ${session.ip}")
    }

    private fun parseLine(line: String): ServerProt {
        val parts = line.split('\t')
        require(parts.size == 2) { "Invalid lobby baseline line: $line" }
        val opcode = parts[0].toInt()
        val payload = parts[1].split(' ')
            .filter { it.isNotBlank() }
            .map { it.toInt(16) }
            .toIntArray()
        return when (opcode) {
            28 -> VarpLarge(
                id = u16(payload, 4),
                value = i32(payload, 0),
            )
            47 -> ClientSetVarcSmall(
                id = u16le(payload, 1),
                value = signedSmall(payload[0] - 128),
            )
            61 -> VarpSmall(
                id = u16(payload, 0),
                value = signedSmall(128 - payload[2]),
            )
            64 -> ClientSetVarcLarge(
                id = ((payload[5] and 0xFF) shl 8) or ((payload[4] - 128) and 0xFF),
                value = intMiddle(payload, 0),
            )
            147 -> VarpLong(
                id = ((payload[0] and 0xFF) shl 8) or ((payload[1] - 128) and 0xFF),
                value = (intInverseMiddle(payload, 2).toLong() shl 32) or
                    (intInverseMiddle(payload, 6).toLong() and 0xFFFF_FFFFL),
            )
            else -> error("Unsupported lobby baseline opcode $opcode")
        }
    }

    private fun u16(bytes: IntArray, offset: Int): Int =
        ((bytes[offset] and 0xFF) shl 8) or (bytes[offset + 1] and 0xFF)

    private fun u16le(bytes: IntArray, offset: Int): Int =
        (bytes[offset] and 0xFF) or ((bytes[offset + 1] and 0xFF) shl 8)

    private fun i32(bytes: IntArray, offset: Int): Int =
        ((bytes[offset] and 0xFF) shl 24) or
            ((bytes[offset + 1] and 0xFF) shl 16) or
            ((bytes[offset + 2] and 0xFF) shl 8) or
            (bytes[offset + 3] and 0xFF)

    private fun intMiddle(bytes: IntArray, offset: Int): Int =
        ((bytes[offset + 2] and 0xFF) shl 24) or
            ((bytes[offset + 3] and 0xFF) shl 16) or
            ((bytes[offset] and 0xFF) shl 8) or
            (bytes[offset + 1] and 0xFF)

    private fun intInverseMiddle(bytes: IntArray, offset: Int): Int =
        ((bytes[offset + 1] and 0xFF) shl 24) or
            ((bytes[offset + 3] and 0xFF) shl 16) or
            ((bytes[offset] and 0xFF) shl 8) or
            (bytes[offset + 2] and 0xFF)

    private fun signedSmall(value: Int): Int = when {
        value < -128 -> value + 256
        value > 127 -> value - 256
        else -> value
    }
}
