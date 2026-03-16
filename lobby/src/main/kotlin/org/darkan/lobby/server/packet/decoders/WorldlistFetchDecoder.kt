package org.darkan.lobby.server.packet.decoders

import org.darkan.core.Logger.logInfo
import org.darkan.core.net.packet.ClientPacketDecoder
import org.darkan.core.net.prot.ClientProt
import org.darkan.core.net.session.GameSession
import org.darkan.lobby.server.packet.encoders.WorldlistFetchReplyEncoder
import world.gregs.voidps.buffer.read.BufferReader

/** WORLDLIST_FETCH (110, 4B): client requests world list update. */
class WorldlistFetchDecoder : ClientPacketDecoder(ClientProt.WORLDLIST_FETCH) {
    override suspend fun decode(reader: BufferReader, session: GameSession) {
        val checksum = reader.readInt()
        logInfo("WORLDLIST_FETCH from ${session.ip}: checksum=0x${"%08x".format(checksum)}")
        session.write(WorldlistFetchReplyEncoder(checksum))
        session.flush()
        logInfo("Sent WORLDLIST_FETCH_REPLY to ${session.ip}")
    }
}
