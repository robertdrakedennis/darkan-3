package org.darkan.lobby.server.packet

import org.darkan.core.net.packet.ClientPacketDecoder
import org.darkan.core.net.prot.ClientProt
import org.darkan.lobby.server.packet.decoders.NoTimeoutDecoder
import org.darkan.lobby.server.packet.decoders.WorldlistFetchDecoder

/**
 * Maps ClientProt opcodes to their decoders for the lobby session.
 */
object LobbyPacketRegistry {
    private val decoders = arrayOfNulls<ClientPacketDecoder>(130)

    init {
        register(NoTimeoutDecoder(ClientProt.NO_TIMEOUT))
        register(NoTimeoutDecoder(ClientProt.NO_TIMEOUT_2))
        register(WorldlistFetchDecoder())
    }

    private fun register(decoder: ClientPacketDecoder) {
        decoders[decoder.prot.opcode] = decoder
    }

    fun get(opcode: Int): ClientPacketDecoder? =
        if (opcode in decoders.indices) decoders[opcode] else null
}
