package org.darkan.lobby.server.packet.decoders

import org.darkan.core.net.packet.ClientPacketDecoder
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.session.GameSession
import world.gregs.voidps.buffer.read.BufferReader

/** NO_TIMEOUT / NO_TIMEOUT_2 — client keepalive, no action needed. */
class NoTimeoutDecoder(prot: ClientProt) : ClientPacketDecoder(prot) {
    override suspend fun decode(reader: BufferReader, session: GameSession) {
        // Keepalive — nothing to do
    }
}
