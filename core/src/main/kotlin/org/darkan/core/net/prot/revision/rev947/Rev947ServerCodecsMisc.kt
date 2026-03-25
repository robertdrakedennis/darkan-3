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

    // WORLDLIST_FETCH_REPLY (159, var_short)
    // Format RE-verified from rs2client 947-1 handler at 0x0023a390.
    // See docs/net/serverprot/worldlist-fetch-reply.md for full wire format.
    serverProt<WorldListPacket>(opcode = 159, size = ProtSize.VarShort) { out ->
        val worlds = worldList.getWorldArray()
        out.writeByte(1)                            // frame: 1 = last segment
        out.writeByte(if (fullRefresh) 2 else 0)    // refresh: 2 = full, 0 = delta

        if (fullRefresh) {
            out.writeByte(1)                        // separator: 1 = has world defs

            // 1. Country list — deduplicated, worlds reference by index
            val countries = worlds.map { it.country }.distinct()
            out.writeSmart(countries.size)
            for (country in countries) {
                out.writeSmart(country.id)
                out.writeJagString(country.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // 2. World ID range and count
            val minWorldId = worlds.minOfOrNull { it.number } ?: 0
            val maxWorldId = worlds.maxOfOrNull { it.number } ?: 0
            out.writeSmart(minWorldId)              // minWorldId (base for offsets)
            out.writeSmart(maxWorldId + 1)          // maxWorldId (upper bound)
            out.writeSmart(worlds.size)             // worldCount

            // 3. World entries
            for (world in worlds) {
                out.writeSmart(world.number - minWorldId) // worldNumberOffset from minWorldId
                out.writeByte(countries.indexOf(world.country)) // country array index

                // Flags (no port bit — 947 doesn't read port from this packet)
                var flags = 0
                if (world.members) flags = flags or 0x1
                if (world.quickchat) flags = flags or 0x2
                if (world.pvp) flags = flags or 0x4
                if (world.lootShare) flags = flags or 0x8
                if (world.highlighted) flags = flags or 0x10
                out.writeInt(flags)

                // Activity (conditional: smart value > 0 means activity string follows)
                if (world.activity.isNotEmpty()) {
                    out.writeSmart(1)               // activityPresence: non-zero = has activity
                    out.writeJagString(world.activity)
                } else {
                    out.writeSmart(0)               // activityPresence: 0 = no activity string
                }

                // Hostname + serverAddress (two gjStr2 strings)
                out.writeJagString(world.hostname)
                out.writeJagString(world.hostname)  // serverAddress = same as hostname
            }

            // 4. Revision
            out.writeInt(worldList.revision)
        } else {
            out.writeByte(0)                        // separator: 0 = counts only
        }

        // 5. Player count section — always sent
        for (world in worlds) {
            out.writeSmart(world.number)
            out.writeShort(if (world.offline) -1 else world.playersOnline)
        }
    }
}
