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
 * NOTE — WorldLoginDetails is NOT registered here (and must NOT be). It used to be an op-2
 * VarByte packet sent via `session.send(..., noIsaac=true)`, but that was WRONG: in world mode
 * the client routes the post-SUCCESS result byte 2 to its server-client-var state
 * (LoginStepDealWithFirstResponse → 0xfa), so it reads the smart-opcode byte `0x02` + the VarByte
 * length byte `0x14` as a 2-byte BE varc-block length (=532), then walks ~532 bytes of the body +
 * following ISAAC burst as varc ids → NULL GetVarcType → SIGSEGV
 * (docs/protocol/lobby-world-switch-948.md §9.3). The world-login RESPONSE is a fixed THREE-PART
 * pre-ISAAC stream (server-client-var block + players byte + login-data block), NOT a single
 * framed packet, so it is now assembled and written raw by
 * `WorldServer.writeWorldLoginResponse` (§9.6 Option 1). The `WorldLoginDetails` data class is
 * retained — it carries the correct field VALUES for Part C's body (fields 1–11, §9.4). Do not
 * re-register it as a codec entry: routing it back through `Session.encodePacket` would re-emit
 * the smart-opcode/length framing that is the crash.
 */
internal fun Codec.registerRev948ServerCodecsMisc() {
    // RESET_CLIENT_VARCACHE (op 5, 0B) — confirmed by live 948 captures and Rev948ServerProtStubs.
    // Do not confuse this normal game-stream packet with the raw pre-ISAAC world-login
    // server-client-var block; adding a VarShort length here desyncs every following lobby packet.
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

    serverProt<HashedWorldToken>(opcode = 54, size = ProtSize.VarByte) { out ->
        out.writeRSString(token)
    }

    serverProt<MidiSong>(opcode = 95, size = 5) { out ->
        out.writeFully(payload)
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
    // FIELD ORDER per ground-truth 948-5 decompile of WorldData::SWITCH_WORLD @ 0x001aeba0
    // (binary-verified): the handler reads worldId (BE u16) FIRST, then host (jstr), portA (BE u16),
    // portB (BE u16), reconnectFlag (u8). This is worldId-first, UNLIKE op212 SET_WORLD_TARGET
    // (host-first). Writing host-first made the client read worldId out of the host bytes and the
    // host from the remainder ("calhost") → connect to a garbage host:port → crash. port2 (+0x2a)
    // is the port the client actually connects on; flag must be 1 (world target).
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

    serverProt<ResetEntityLists>(opcode = 7, size = 0)
    serverProt<DestroyZoneData>(opcode = 55, size = 0)
    serverProt<NoopVarA>(opcode = 128, size = 0)
    serverProt<ClearPendingUpdates>(opcode = 190, size = 0)
    serverProt<TriggerOnDialogAbort>(opcode = 162, size = 0)

    // ANTI_CHEAT_CHALLENGE behavior at op 174. The 948-5 symbol is the handler name
    // `HandleAntiCheatChallenge`.
    // Body is two BE u32 values. The client replies with op3 as BE first value, LE second value,
    // then the client-side `*(*client + 0x534)` value biased by -128.
    serverProt<AntiCheatChallenge>(opcode = 174, size = 8) { out ->
        out.writeInt(challengeA)
        out.writeInt(challengeB)
    }

    serverProt<MinimapState>(opcode = 73, size = 2) { out ->
        out.writeByte(128 - first)
        out.writeByte(128 - second)
    }

    serverProt<EntityAnimAtTile>(opcode = 154, size = 5) { out ->
        out.writeByte(128 - value)
        out.writeByte((target + 128) and 0xFF)
        out.writeByte((target ushr 8) and 0xFF)
        out.writeByte((cycleOffset + 128) and 0xFF)
        out.writeByte((cycleOffset ushr 8) and 0xFF)
    }

    serverProt<SceneFlag>(opcode = 157, size = 1) { out ->
        out.writeByte((-value) and 0xFF)
    }

    serverProt<CamSmoothReset>(opcode = 120, size = 0)

    serverProt<SetMultiwayState>(opcode = 45, size = 1) { out ->
        out.writeByte(state)
    }

    serverProt<MinimapFlagA>(opcode = 172, size = 1) { out ->
        out.writeByte(value)
    }

    serverProt<MinimapFlagB>(opcode = 204, size = 1) { out ->
        out.writeByte((-value) and 0xFF)
    }

    serverProt<SetNpcOp>(opcode = 1, size = ProtSize.VarByte) { out ->
        if (text != null) {
            out.writeRSString(text)
            out.writeShort(if (cursor < 0) 0xFFFF else cursor)
        }
    }

    serverProt<SetPlayerOp2>(opcode = 12, size = 2) { out ->
        out.writeShort(value)
    }

    serverProt<SetPlayerOp3>(opcode = 13, size = 1) { out ->
        out.writeByte(value)
    }

    serverProt<PlayerInfoDecode>(opcode = 104, size = 14) { out ->
        out.writeByte(((slot and 0x7) shl 5) or (mode and 0x1F))
        repeat(13) {
            out.writeByte(0)
        }
    }

    serverProt<CutsceneData>(opcode = 119, size = 35) { out ->
        out.writeByte(group)
        out.writeByte(slot)
        out.writeByte(mode)
        out.writeByte(extendedMode)
        out.writeByte(((flags and 0x1) shl 3) or (shape and 0x7))
        out.writeShort(id)
        out.writeLong(primaryLong)
        out.writeInt(primaryInt)
        out.writeInt(secondaryInt)
        out.writeLong(secondaryLong)
        out.writeInt(skipLength)
    }

    serverProt<CamUpdate>(opcode = 77, size = ProtSize.VarShort) { out ->
        var flags = 0
        if (byteA0) flags = flags or 0x01
        if (modeA8 != null) flags = flags or 0x08
        if (modeC0 != null) flags = flags or 0x10
        if (extended != null) flags = flags or 0x80

        out.writeByte(flags)
        modeA8?.let { out.writeByte(it) }
        modeC0?.let { out.writeByte(it) }
        extended?.let { out.writeCameraUpdateExtended(it) }
    }

    serverProt<UpdateIgnoreListRaw>(opcode = 130, size = ProtSize.VarByte) { out ->
        out.writeLong(mask)
        out.writeFully(encodedFields)
        out.writeShort(entryId)
    }

    // NPC_INFO_THUNK zero-length path clears the client's world-entity NPC state. Non-empty
    // payloads are a separate world-entity NPC envelope still preserved raw pending field split.
    serverProt<NpcInfoThunk>(opcode = 209, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }

    // WORLDLIST_FETCH_REPLY (op 216, varShort) — was op 159 in 947-3.
    // 948-5 handler: WorldData::WORLDLIST_FETCH_REPLY @ 0x00190020.
    // Bound by main ServerProt::BindHandlers (entry 0x0139ffa0). Verified by reading the
    // handler body: reads frame indicator, then reassembles a buffer whose header is [2, mode].
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

                out.writeSmart(0)
                out.writeJagString(world.activity.ifEmpty { "-" })
                out.writeJagString(world.hostname)
            }

            // 4. Revision
            out.writeInt(worldList.revision)
        } else {
            out.writeByte(0)
            out.writeInt(worldList.revision)
        }

        // 5. Player count section
        // Each entry is [smart worldId-delta][u16 count].
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
    // FriendlistLoaded — no 947-3 origin and no 948 destination. TODO: identify.
    //
    // VARP/VARC bit/large variants — handler bodies in ClientState::BindHandlers (0x000aa85e)
    //   at ops 10, 28, 47, 48, 51, 61, 64, 69 have wire-format differences from 947-3
    //   (endianness, byte transforms, field order all changed). See Rev948ServerCodecsVariable.kt
    //   for tentative registrations.
}

private suspend fun ByteWriteChannel.writeCameraUpdateExtended(update: CamUpdateExtended) {
    var flags = 0
    if (update.vector138 != null) flags = flags or 0x0001
    if (update.vector150 != null) flags = flags or 0x0002
    if (update.vector168 != null) flags = flags or 0x0004
    if (update.vector180 != null) flags = flags or 0x0008
    if (update.pair1e8 != null) flags = flags or 0x0010
    if (update.pair1dc != null) flags = flags or 0x0020
    if (update.byteA4 != null) flags = flags or 0x0040
    if (update.ignored80 != null) flags = flags or 0x0080
    if (update.flagsFcFd != null) flags = flags or 0x0100
    if (update.scriptedCommandCount != null) flags = flags or 0x0200
    if (update.pair118 != null) flags = flags or 0x0400
    if (update.byte100 != null) flags = flags or 0x0800
    if (update.envelope198 != null) flags = flags or 0x1000
    if (update.scalar108 != null) flags = flags or 0x2000
    if (update.scalar110 != null) flags = flags or 0x4000

    writeShort(flags)
    update.vector138?.let { writeCameraVector(it) }
    update.vector150?.let { writeCameraVector(it) }
    update.vector168?.let { writeCameraVector(it) }
    update.vector180?.let { writeCameraVector(it) }
    update.pair1e8?.let { writeCameraPair(it) }
    update.pair1dc?.let { writeCameraPair(it) }
    update.byteA4?.let { writeByte(it) }
    update.ignored80?.let { writeInt(it) }
    update.flagsFcFd?.let {
        writeByte((if (it.first) 1 else 0) or (if (it.second) 2 else 0))
    }
    update.scriptedCommandCount?.let { writeByte(it) }
    update.pair118?.let {
        writeShort(it.id)
        writeCameraFloat(it.value)
    }
    update.byte100?.let { writeByte(it) }
    update.envelope198?.let {
        writeCameraVector(it.first)
        writeCameraVector(it.second)
        writeCameraFloat(it.firstScalar)
        writeCameraFloat(it.secondScalar)
    }
    update.scalar108?.let { writeCameraFloat(it) }
    update.scalar110?.let { writeCameraFloat(it) }
}

private suspend fun ByteWriteChannel.writeCameraVector(value: CamVector3) {
    writeCameraFloat(value.x)
    writeCameraFloat(value.y)
    writeCameraFloat(value.z)
}

private suspend fun ByteWriteChannel.writeCameraPair(value: CamFloatPair) {
    writeCameraFloat(value.first)
    writeCameraFloat(value.second)
}

private suspend fun ByteWriteChannel.writeCameraFloat(value: Float) {
    writeInt(value.toRawBits())
}
