package org.darkan.core.net.packet

import io.ktor.utils.io.*
import org.darkan.core.net.Isaac
import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Abstract base for encoding ServerProt packets.
 *
 * Subclasses implement [encodeBody] to write payload bytes via [BufferWriter].
 * The base handles ISAAC opcode encryption and var_byte/var_short framing.
 */
abstract class ServerPacketEncoder(val prot: ServerProt) {

    /**
     * Encode the packet body into the given [BufferWriter].
     * For size-0 packets or empty var-size packets, this may be a no-op.
     */
    abstract fun encodeBody(buf: BufferWriter)

    /**
     * Write this packet (opcode + framing + body) to the channel.
     */
    suspend fun writeTo(channel: ByteWriteChannel, cipher: Isaac) {
        writeOpcode(channel, prot.opcode, cipher)
        when (prot.size) {
            0 -> {} // no body
            -1 -> {
                val body = encode()
                channel.writeByte(body.size.toByte())
                if (body.isNotEmpty()) channel.writeFully(body)
            }
            -2 -> {
                val body = encode()
                channel.writeByte(((body.size shr 8) and 0xFF).toByte())
                channel.writeByte((body.size and 0xFF).toByte())
                if (body.isNotEmpty()) channel.writeFully(body)
            }
            else -> {
                channel.writeFully(encode())
            }
        }
    }

    private fun encode(): ByteArray {
        val buf = BufferWriter(maxOf(prot.size.coerceAtLeast(0), 256))
        encodeBody(buf)
        return buf.toArray()
    }

    companion object {
        /**
         * Write an ISAAC-encrypted ServerProt opcode to the channel.
         *
         * Opcodes 0-127: 1 byte, (opcode + isaac_val) & 0xFF
         * Opcodes 128-216: 2 bytes, each ISAAC-encrypted independently.
         */
        suspend fun writeOpcode(channel: ByteWriteChannel, opcode: Int, cipher: Isaac) {
            if (opcode >= 128) {
                channel.writeByte((((opcode shr 8) + 128 + cipher.nextInt()) and 0xFF).toByte())
                channel.writeByte((((opcode and 0xFF) + cipher.nextInt()) and 0xFF).toByte())
            } else {
                channel.writeByte(((opcode + cipher.nextInt()) and 0xFF).toByte())
            }
        }
    }
}
