package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for miscellaneous packets (keepalive, flags, worldlist, etc.).
 * Opcodes and sizes verified from rs2client binary.
 */
internal fun Codec.registerRev947ServerCodecsMisc() {
    // RUNCLIENTSCRIPT (121, varShort) — RE-verified: invokes CS2 script
    // Wire: RS string (type descriptor) + args in REVERSED type order + script_id (4B BE)
    serverProt<RunClientScript>(opcode = 121, size = ProtSize.VarShort) { out ->
        // Type descriptor as null-terminated string (no version prefix)
        out.writeRSString(types)
        // Args in REVERSED order (client reads reversed type chars)
        for (i in args.indices.reversed()) {
            when (val arg = args[i]) {
                is Int -> out.writeInt(arg)
                is String -> out.writeRSString(arg)
                is Long -> out.writeLong(arg)
            }
        }
        // Script ID
        out.writeInt(scriptId)
    }

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
    //
    // refreshFlag MUST be 2 for the client to read ANY data (including player counts).
    // separator=1 means full definitions + counts; separator=0 means counts only.
    // refreshFlag != 2 is a no-op (client rebuilds vector from cache, reads nothing).
    serverProt<WorldListPacket>(opcode = 159, size = ProtSize.VarShort) { out ->
        val worlds = worldList.getWorldArray()
        val minWorldId = worlds.minOfOrNull { it.number } ?: 0
        val maxWorldId = worlds.maxOfOrNull { it.number } ?: 0

        out.writeByte(1)                            // frame: 1 = last segment
        out.writeByte(2)                            // refreshFlag: MUST be 2 for data to be read

        if (fullRefresh) {
            out.writeByte(1)                        // separator: 1 = has world defs + counts

            // 1. Country list — deduplicated, worlds reference by index
            val countries = worlds.map { it.country }.distinct()
            out.writeSmart(countries.size)
            for (country in countries) {
                out.writeSmart(country.id)
                out.writeJagString(country.name.lowercase().replaceFirstChar { it.uppercase() })
            }

            // 2. World ID range and count
            out.writeSmart(minWorldId)              // minWorldId (base for offsets)
            out.writeSmart(maxWorldId)              // maxWorldId (inclusive upper bound, NOT max+1)
            out.writeSmart(worlds.size)             // worldCount

            // 3. World entries
            // With actPres=0: hostname field = UI display text, serverAddress = actual server
            // With actPres>0: activity = extra display text, hostname = "-", serverAddress = actual server
            for (world in worlds) {
                out.writeSmart(world.number - minWorldId) // worldNumberOffset (= hashtable key)
                out.writeByte(countries.indexOf(world.country)) // country array index (0-based)

                var flags = 0
                if (world.members) flags = flags or 0x1
                if (world.quickchat) flags = flags or 0x2
                if (world.pvp) flags = flags or 0x4
                if (world.lootShare) flags = flags or 0x8
                if (world.highlighted) flags = flags or 0x10
                out.writeInt(flags)

                out.writeSmart(0)                       // actPres=0: no conditional activity
                out.writeJagString(world.activity)      // hostname field: UI display text
                out.writeJagString(world.hostname)      // serverAddress field: actual server hostname
            }

            // 4. Revision
            out.writeInt(worldList.revision)
        } else {
            out.writeByte(0)                        // separator: 0 = player counts only
        }

        // 5. Player count section (read when refreshFlag==2, regardless of separator)
        // World numbers here MUST be offsets (worldNumber - minWorldId), NOT actual world numbers.
        // The client looks these up in the hashtable which is keyed by offsets.
        for (world in worlds) {
            out.writeSmart(world.number - minWorldId)
            out.writeShort(if (world.offline) -1 else world.playersOnline)
        }
    }
}
