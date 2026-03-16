package org.darkan.lobby.server.packet.encoders

import org.darkan.core.net.packet.ServerPacketEncoder
import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.buffer.write.BufferWriter

/** SET_RUN_ENERGY (27, 1B): g1 unsigned byte */
class SetRunEnergyEncoder(private val energy: Int) :
    ServerPacketEncoder(ServerProt.SET_RUN_ENERGY) {
    override fun encodeBody(buf: BufferWriter) {
        buf.writeByte(energy)
    }
}

/** UPDATE_IGNORELIST (17, var_short): empty payload for initial lobby state */
class UpdateIgnorelistEncoder : ServerPacketEncoder(ServerProt.UPDATE_IGNORELIST) {
    override fun encodeBody(buf: BufferWriter) {}
}

/** UPDATE_SITESETTINGS (18, var_short): empty payload for initial lobby state */
class UpdateSiteSettingsEncoder : ServerPacketEncoder(ServerProt.UPDATE_SITESETTINGS) {
    override fun encodeBody(buf: BufferWriter) {}
}

/** NOOP (146, 0B): keepalive / no-op packet */
class NoopEncoder : ServerPacketEncoder(ServerProt.NOOP) {
    override fun encodeBody(buf: BufferWriter) {}
}
