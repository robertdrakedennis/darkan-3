package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for miscellaneous packets (keepalive, flags, worldlist, etc.).
 * Opcodes and sizes verified from rs2client binary.
 */
internal fun Codec.registerRev947ServerCodecsMisc() {
    // NO_TIMEOUT (216, 0B) — trivial return handler (keepalive)
    serverProt<NoTimeout>(opcode = 216, size = 0)

    // RESET_CLIENT_VARCACHE (48, 0B) — handler: ClientState::RESET_ALL_VARPS
    serverProt<ResetClientVarcache>(opcode = 48, size = 0)

    // SET_READY_FLAG (65, 0B) — handler: ClientState::SET_READY_FLAG
    serverProt<SetReadyFlag>(opcode = 65, size = 0)

    // UPDATE_RUNENERGY (19, 1B) — handler: Misc::SET_RUN_ENERGY
    serverProt<UpdateRunenergy>(opcode = 19, size = 1) { out ->
        out.writeByte(energy)
    }

    // CHANGE_LOBBY (30, var_short) — handler: Lobby::CHANGE_LOBBY (was mislabeled UPDATE_IGNORELIST)
    serverProt<UpdateIgnoreList>(opcode = 30, size = ProtSize.VarShort)

    // UPDATE_FRIENDLIST (102, var_short) — handler: UPDATE_SITESETTINGS at 0x00247a60
    // RE-verified from rs2client rev 947-1. Structurally identical to 946 handler.
    // All reads big-endian, no byte transforms. Strings are null-terminated CP1252.
    serverProt<UpdateFriendList>(opcode = 102, size = ProtSize.VarShort) { out ->
        for (friend in friends) {
            out.writeByte(friend.warnMessage)
            out.writeRSString(friend.displayName)
            out.writeRSString(friend.previousName)
            out.writeShort(friend.worldId)
            out.writeByte(friend.fcRank)
            out.writeByte(friend.flags)
            if (friend.worldId > 0) {
                out.writeRSString(friend.worldName)
                out.writeByte(friend.platform)
                out.writeInt(friend.worldFlags)
            }
            out.writeRSString(friend.notes)
        }
    }

    // WORLDLIST_FETCH_REPLY (159, var_short) — handler: Social::UPDATE_FRIENDCHAT_CHANNEL
    serverProt<WorldListPacket>(opcode = 159, size = ProtSize.VarShort) { out ->
        // Frame byte: 0x01 = last (and only) segment
        out.writeByte(0x01)

        if (checksum == -1 || checksum == 0) {
            // Full world list + player counts (updateType = 0x03: bit0 + bit1)
            out.writeByte(0x03)

            // Country list
            out.writeByte(1)                        // hasCountries = true
            out.writeSmart(1)                       // 1 country
            out.writeSmart(0)                       // countryId = 0
            out.writePrefixedString("Local")        // country name (gjStr2)

            // World list
            val worldId = 1
            out.writeSmart(worldId)                 // minWorldId
            out.writeSmart(worldId)                 // maxWorldId
            out.writeSmart(1)                       // worldCount = 1

            // World entry
            out.writeSmart(0)                       // idDelta = 0
            out.writeByte(0)                        // countryIndex = 0
            out.writeInt(0x00000001)                // flags: bit0 = members
            out.writeSmart(0)                       // countryOverride = 0
            out.writePrefixedString("Darkan")       // activity (gjStr2)
            out.writePrefixedString("localhost")    // hostname (gjStr2)

            // CRC + Player counts
            out.writeInt(0x00000001)                // CRC
            out.writeSmart(0)                       // worldIdDelta = 0
            out.writeShort(1)                       // playerCount = 1
        } else {
            // Delta: player counts only (updateType = 0x01: bit0 only)
            out.writeByte(0x01)
            out.writeInt(checksum)                  // echo back the CRC
            out.writeSmart(0)                       // worldIdDelta = 0
            out.writeShort(1)                       // playerCount = 1
        }
    }
}
