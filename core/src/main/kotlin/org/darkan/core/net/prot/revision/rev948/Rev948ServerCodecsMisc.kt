package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for miscellaneous packets (keepalive, flags, login response, etc.).
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md`:
 *
 * MOVED:
 *  - SetReadyFlag:        65 → 75
 *  - UpdateRunenergy (SET_RUN_ENERGY): 19 → 80
 *  - JcoinsUpdate (SET_DISPLAY_INT):   59 → 74
 *  - ChangeLobby:         30 → 49
 *  - SetWorldTarget:     187 → 212
 *  - SwitchWorld:        179 → 213
 *  - ResetClientVarcache → RESET_ALL_VARPS: 48 → 5 (semantically equivalent; new handler in 948)
 *
 * REMOVED in 948 (no identified destination — likely registered via a Variables/Audio/Misc
 * subsystem BindHandlers we haven't walked yet):
 *  - NoTimeout (was op 216 in 947-3) — keepalive
 *  - RunClientScript (was op 121, RUNCLIENTSCRIPT) — heavyweight CS2 invoke
 *  - FriendStatus (was op 102, UPDATE_FRIENDLIST) — friends-list update
 *  - WorldListPacket (was op 159, WORLDLIST_FETCH_REPLY) — lobby world list
 *
 * The data classes for the removed packets are revision-agnostic and remain in
 * `core/.../prot/ServerProt.kt`. When the server emits them with the 948 codec active,
 * `Session.send()` will log "no encoder registered" until the missing opcodes are RE'd. Game
 * logic that depends on these packets must either be revision-gated or wait for a follow-up
 * RE pass that identifies the 948 destinations.
 *
 * Special case — WorldLoginDetails (opcode 2): the world-login response is sent pre-ISAAC
 * via `noIsaac=true` during the login state machine. The opcode 2 mapping is presumed to be
 * fixed by the client's login state machine (not the normal ProtEntry dispatch table), so it
 * is preserved at opcode 2 here. In the 948 main ServerProt table, opcode 2 happens to be
 * MESSAGE_QUICKCHAT_CLANCHAT — but that handler is never reached for this byte because the
 * client is in login state, not game-loop state, when it reads it.
 * **TODO:** verify the 948 client's world-login response opcode by tracing the login state
 *   machine in rs2client.948-2-2.
 */
internal fun Codec.registerRev948ServerCodecsMisc() {
    // WorldLoginDetails (opcode 2 — preserved from 947 — sent pre-ISAAC during world-login).
    serverProt<WorldLoginDetails>(opcode = 2, size = ProtSize.VarByte) { out ->
        out.writeByte(rights)
        out.writeByte(modLevel)
        out.writeBoolean(quickChat)
        out.writeBoolean(verifiedEmail)
        out.writeBoolean(aBool7322)
        out.writeBoolean(quickChatOnly)
        out.writeShort(playerIndex)
        out.writeBoolean(members)
        out.writeMedium(dob)
        out.writeBoolean(memberWorld)
        out.writeRSString(worldName)
    }

    // RESET_ALL_VARPS (op 5, 0B) — successor to 947-3 RESET_CLIENT_VARCACHE (op 48).
    serverProt<ResetClientVarcache>(opcode = 5, size = 0)

    // SET_READY_FLAG (op 75, 0B) — was op 65 in 947-3.
    serverProt<SetReadyFlag>(opcode = 75, size = 0)

    // SET_RUN_ENERGY (op 80, 1B) — was op 19 (UPDATE_RUNENERGY) in 947-3.
    serverProt<UpdateRunenergy>(opcode = 80, size = 1) { out ->
        out.writeByte(energy)
    }

    // JCOINS_UPDATE (op 191, 4B). OPCODE REBIND (2026-06-25 oracle): JCOINS_UPDATE is op191
    //   (HANDLER_ID_HIGH, handler ClientState::UNKNOWN_op0xBF_handler), NOT op74. op74 is
    //   Misc::SET_DISPLAY_INT (UNKNOWN). This encoder previously sent on op74 (wrong opcode).
    serverProt<JcoinsUpdate>(opcode = 191, size = 4) { out ->
        out.writeInt(balance)
    }

    // SET_WORLD_TARGET (op 212, varByte) — was op 187 in 947-3. Populates lobby login slot.
    serverProt<SetWorldTarget>(opcode = 212, size = ProtSize.VarByte) { out ->
        out.writeRSString(hostname)
        out.writeShort(worldId)
        out.writeShort(port1)
        out.writeShort(port2)
    }

    // SWITCH_WORLD (op 213, varByte) — was op 179 in 947-3. Writes the WORLD-node target
    // (WorldSwitcher+0x20) and triggers the reconnect to host:port2.
    // FIELD ORDER (handler 0x001aeba0, binary-verified): worldId FIRST, then host — op213 is
    // worldId-first, UNLIKE op212 (host-first). Writing host-first made the client read worldId from
    // the host bytes and the host from the remainder ("calhost") → connect to a garbage host → crash.
    // port2 (+0x2a) is the port the client actually connects on; flag must be 1 (world target).
    serverProt<SwitchWorld>(opcode = 213, size = ProtSize.VarByte) { out ->
        out.writeShort(worldId)
        out.writeRSString(hostname)
        out.writeShort(port1)
        out.writeShort(port2)
        out.writeByte(pendingFlag)
    }

    // CHANGE_LOBBY (op 49, varShort) — was op 30 in 947-3.
    serverProt<ChangeLobby>(opcode = 49, size = ProtSize.VarShort)

    // RUNCLIENTSCRIPT (op 110, varShort) — was op 121 in 947-3.
    // 948 handler: thunk_FUN_001d2060 @ 0x001d2070 -> RUNCLIENTSCRIPT_impl @ 0x001d2060.
    // Bound by main ServerProt::BindHandlers (entry 0x013a0fe0). Verified by reading the
    // handler body: it reads `gStringCP1252ToUTF8(typeDesc) + per-char-reversed args + gT<uint>(scriptId)`,
    // matching 947-3 RunClientScript wire format byte-for-byte.
    serverProt<RunClientScript>(opcode = 110, size = ProtSize.VarShort) { out ->
        // Type descriptor as null-terminated string (no version prefix)
        out.writeRSString(types)
        // Args in REVERSED order (client reads reversed type chars from string end backwards)
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

    // NO_TIMEOUT (op 54, varByte) — was op 216 (size 0) in 947-3.
    // 948 handler: FUN_000ef260 @ 0x000ef260 (empty body — no-op acknowledgement).
    // Bound by main ServerProt::BindHandlers (entry 0x013a13e0).
    // Wire format changed: 947-3 was 0-byte packet (just opcode); 948 is varByte with
    // 0-length payload (opcode + length=0). The client doesn't read any bytes either way.
    serverProt<NoTimeout>(opcode = 54, size = ProtSize.VarByte)

    // WORLDLIST_FETCH_REPLY (op 216, varShort) — was op 159 in 947-3.
    // 948 handler: WorldData::WORLDLIST_FETCH_REPLY @ 0x0018fea0.
    // Bound by main ServerProt::BindHandlers (entry 0x0139ffa0). Verified by reading the
    // handler body: reads frame indicator, smart-encoded world list + counts identical to 947-3.
    // Wire format: same byte layout as 947-3 (no changes detected in handler decompile).
    serverProt<WorldListPacket>(opcode = 216, size = ProtSize.VarShort) { out ->
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
            out.writeSmart(minWorldId)
            out.writeSmart(maxWorldId)
            out.writeSmart(worlds.size)

            // 3. World entries
            for (world in worlds) {
                out.writeSmart(world.number - minWorldId)
                out.writeByte(countries.indexOf(world.country))

                var flags = 0
                if (world.members) flags = flags or 0x1
                if (world.quickchat) flags = flags or 0x2
                if (world.pvp) flags = flags or 0x4
                if (world.lootShare) flags = flags or 0x8
                if (world.highlighted) flags = flags or 0x10
                out.writeInt(flags)

                out.writeSmart(0)                       // actPres=0: no conditional activity
                out.writeJagString(world.activity)
                out.writeJagString(world.hostname)
            }

            // 4. Revision
            out.writeInt(worldList.revision)
        } else {
            out.writeByte(0)
        }

        // 5. Player count section
        for (world in worlds) {
            out.writeSmart(world.number - minWorldId)
            out.writeShort(if (world.offline) -1 else world.playersOnline)
        }
    }

    // FRIENDSTATUS / UPDATE_FRIENDLIST (op 26, varShort) — was op 102 in 947-3.
    // 948 handler: SiteSettings::UPDATE_SITESETTINGS_thunk @ 0x001a63e0 -> UPDATE_SITESETTINGS
    // @ 0x001a44e0. Bound by main ServerProt::BindHandlers (entry 0x013a19e0).
    // Wire format: per-friend record { warnMessage(1B) + displayName(string) + previousName(string)
    // + worldId(2B BE) + fcRank(1B) + flags(1B) + [worldName(string) + platform(1B) + worldFlags(4B BE) if worldId>0]
    // + notes(string) }. Presumed identical to 947-3 layout.
    serverProt<FriendStatus>(opcode = 26, size = ProtSize.VarShort) { out ->
        for (friend in updates) {
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

    // -------------------------------------------------------------------------------
    // REMAINING packets — no 948 destination identified:
    // -------------------------------------------------------------------------------
    // HashedWorldToken (was op 6 in 947-3) — no clear 948 destination. 948 op 6 is LOC_PREFETCH
    //   (a zone packet), so the token nonce moved or was removed. TODO: trace world-token
    //   delivery path in 948 login flow.
    // FriendlistLoaded — no 947-3 origin and no 948 destination. TODO: identify.
    //
    // VARP/VARC bit/large variants — handler bodies in ClientState::BindHandlers (0x000aa85e)
    //   at ops 10, 28, 47, 48, 51, 61, 64, 69 have wire-format differences from 947-3
    //   (endianness, byte transforms, field order all changed). See Rev948ServerCodecsVariable.kt
    //   for tentative registrations.
}
