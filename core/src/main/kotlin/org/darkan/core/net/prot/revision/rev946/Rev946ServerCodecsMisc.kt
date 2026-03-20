package org.darkan.core.net.prot.revision.rev946

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev946 server encoders for miscellaneous packets (keepalive, flags, worldlist, etc.).
 * Opcodes and sizes verified from rs2client binary.
 */
internal fun Codec.registerRev946ServerCodecsMisc() {
    // NOOP (146, 0B) — keepalive
    serverProt<KeepAlive>(opcode = 146, size = 0)

    // RESET_ALL_VARPS (112, 0B)
    serverProt<ClearVarps>(opcode = 112, size = 0)

    // SET_READY_FLAG (35, 0B)
    serverProt<SetReadyFlag>(opcode = 35, size = 0)

    // SET_RUN_ENERGY (27, 1B)
    serverProt<RunEnergy>(opcode = 27, size = 1) { out ->
        out.writeByte(energy)
    }

    // UPDATE_IGNORELIST (17, var_short) — empty for lobby init
    serverProt<UpdateIgnoreList>(opcode = 17, size = ProtSize.VarShort)

    // UPDATE_FRIENDLIST (18, var_short) — jag::ServerProt::UPDATE_FRIENDLIST
    // RE-verified from rs2client rev 946 handler at 0x00242840.
    // All reads big-endian, no byte transforms. Strings are null-terminated CP1252.
    serverProt<UpdateFriendList>(opcode = 18, size = ProtSize.VarShort) { out ->
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

    // WORLDLIST_FETCH_REPLY (150, var_short)
    // RE-verified format from rs2client handler at 0x0022f710.
    serverProt<WorldListPacket>(opcode = 150, size = ProtSize.VarShort) { out ->
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
