package org.darkan.lobby.server.packet.encoders

import org.darkan.core.net.packet.ServerPacketEncoder
import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * WORLDLIST_FETCH_REPLY (150, var_short).
 *
 * Sends a world list response. If [checksum] is 0 or -1 (first request),
 * sends a full world list with one local world. Otherwise sends a delta
 * with player counts only.
 *
 * RE-verified format from rs2client handler at 0x0022f710.
 */
class WorldlistFetchReplyEncoder(private val checksum: Int) :
    ServerPacketEncoder(ServerProt.WORLDLIST_FETCH_REPLY) {

    override fun encodeBody(buf: BufferWriter) {
        // Frame byte: 0x01 = last (and only) segment
        buf.writeByte(0x01)

        if (checksum == -1 || checksum == 0) {
            // Full world list + player counts (updateType = 0x03: bit0 + bit1)
            buf.writeByte(0x03)

            // --- Country list ---
            buf.writeByte(1)              // hasCountries = true (nonzero)
            buf.writeSmart(1)             // 1 country
            buf.writeSmart(0)             // countryId = 0
            buf.writePrefixedString("Local") // country name (gjStr2)

            // --- World list ---
            val worldId = 1
            buf.writeSmart(worldId)       // minWorldId
            buf.writeSmart(worldId)       // maxWorldId
            buf.writeSmart(1)             // worldCount = 1

            // World entry
            buf.writeSmart(0)             // idDelta = 0 (absolute = minWorldId + 0 = 1)
            buf.writeByte(0)              // countryIndex = 0 ("Local")
            buf.writeInt(0x00000001)      // flags: bit0 = members
            buf.writeSmart(0)             // countryOverride = 0 (no override)
            buf.writePrefixedString("Darkan")    // activity (gjStr2)
            buf.writePrefixedString("localhost")  // hostname (gjStr2)

            // --- CRC + Player counts ---
            buf.writeInt(0x00000001)      // CRC (arbitrary, client stores and sends back)
            buf.writeSmart(0)             // worldIdDelta = 0
            buf.writeShort(1)             // playerCount = 1
        } else {
            // Delta: player counts only (updateType = 0x01: bit0 only)
            buf.writeByte(0x01)
            buf.writeInt(checksum)        // echo back the CRC
            buf.writeSmart(0)             // worldIdDelta = 0
            buf.writeShort(1)             // playerCount = 1
        }
    }
}
