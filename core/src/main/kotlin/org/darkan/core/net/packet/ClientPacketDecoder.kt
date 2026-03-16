package org.darkan.core.net.packet

import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.session.GameSession
import world.gregs.voidps.buffer.read.BufferReader

/**
 * Abstract base for decoding ClientProt packets.
 *
 * Subclasses implement [decode] to read payload bytes via [BufferReader]
 * and optionally send response packets via [GameSession].
 */
abstract class ClientPacketDecoder(val prot: ClientProt) {

    /**
     * Decode the packet payload and handle it.
     *
     * @param reader BufferReader wrapping the raw payload bytes
     * @param session the client's game session (for sending responses)
     */
    abstract suspend fun decode(reader: BufferReader, session: GameSession)
}
