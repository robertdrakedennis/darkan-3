package org.darkan.core.net.session

import io.ktor.utils.io.*
import org.darkan.core.net.Isaac
import org.darkan.core.net.packet.ServerPacketEncoder
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.prot.ServerProt

/**
 * Wraps a client connection's I/O channels and ISAAC ciphers.
 *
 * Provides typed methods for sending [ServerPacketEncoder] packets and
 * reading ISAAC-decrypted client opcodes.
 */
class GameSession(
    val input: ByteReadChannel,
    val output: ByteWriteChannel,
    val inCipher: Isaac,
    val outCipher: Isaac,
    val ip: String
) {
    /** Send a packet with a body. */
    suspend fun write(encoder: ServerPacketEncoder) = encoder.writeTo(output, outCipher)

    /** Send a size-0 packet (opcode only, no body). */
    suspend fun writeEmpty(prot: ServerProt) =
        ServerPacketEncoder.writeOpcode(output, prot.opcode, outCipher)

    suspend fun flush() = output.flush()

    /**
     * Read an ISAAC-decrypted ClientProt opcode from the input channel.
     *
     * Returns the decoded opcode (0-129 for game/lobby packets).
     * If the first byte decodes to >= 128, a second byte is read for extended opcodes.
     */
    suspend fun readOpcode(): Int {
        val first = ((input.readByte().toInt() and 0xFF) - inCipher.nextInt()) and 0xFF
        return if (first < 128) {
            first
        } else {
            val second = ((input.readByte().toInt() and 0xFF) - inCipher.nextInt()) and 0xFF
            ((first - 128) shl 8) or second
        }
    }

    /**
     * Read the payload bytes for a [ClientProt] packet.
     *
     * Handles fixed, var_byte, and var_short size modes.
     */
    suspend fun readPayload(prot: ClientProt): ByteArray {
        val size = when (prot.size) {
            0 -> 0
            -1 -> input.readByte().toInt() and 0xFF
            -2 -> input.readShort().toInt() and 0xFFFF
            else -> prot.size
        }
        return if (size > 0) {
            ByteArray(size).also { input.readFully(it, 0, size) }
        } else {
            ByteArray(0)
        }
    }
}
