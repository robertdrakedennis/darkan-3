package com.undercut.game.net.decode

import com.undercut.game.net.PacketReader
import com.undercut.game.net.ServerProt
import com.undercut.game.net.model.ServerPacket

/**
 * Decodes raw server packet payloads into typed [ServerPacket] data classes.
 * Every [ServerProt] entry has a registered decoder. Failures fall through to null
 * (the logger will show the hex dump instead).
 */
object ServerPacketDecoder {
    private val decoders = HashMap<ServerProt, (PacketReader) -> ServerPacket>()

    init {
        // TODO Phase 3: Re-register all decoders with 947-3 byte transforms.
        // All 946-5 decoders removed — opcode assignments and byte-scramble
        // sequences changed completely in the 946→947 revision update.
        // See darkan-3 docs/net/serverprot-947-wire-formats.md for verified layouts.
    }

    /* ══════════════════════════════════════════════════════════════════════════
     * DISABLED: All 946-5 decoders below need rewriting for 947-3.
     * Uncomment and update per-packet as Phase 3 progresses.
     * ══════════════════════════════════════════════════════════════════════════

    private fun registerChat() {
        register(ServerProt.MESSAGE_GAME) { r ->
            val type = r.gsmart()
            r.skip(4) // param (uint)
            val flags = r.g1()
            val sender = if (flags and 1 != 0) r.gstr() else ""
            if (flags and 2 != 0) r.gstr() // skip displayName
            val message = if (r.remaining > 0) r.gstr() else ""
            ServerPacket.MessageGame(type, sender, message)
        }
        register(ServerProt.MESSAGE_PUBLIC) { r -> ServerPacket.MessagePublic(r.g1(), r.remaining) }
        register(ServerProt.MESSAGE_PRIVATE) { r -> ServerPacket.MessagePrivate(r.remaining) }
        // MESSAGE_FRIENDCHANNEL (opcode 56) — verified: was MESSAGE_PRIVATE_SYSTEM in 946-3
        register(ServerProt.MESSAGE_FRIENDCHANNEL) { r -> ServerPacket.MessageFriendchannel(r.remaining) }
        register(ServerProt.MESSAGE_PRIVATE_ECHO) { r -> ServerPacket.MessagePrivateEcho(r.remaining) }
        register(ServerProt.MESSAGE_FRIENDCHAT) { r -> ServerPacket.MessageFriendchat(r.remaining) }
        register(ServerProt.MESSAGE_CLANCHANNEL) { r -> ServerPacket.MessageClanchannel(r.remaining) }
        register(ServerProt.MESSAGE_QUICKCHAT_CLANCHAT) { r -> ServerPacket.MessageQuickchatClanchat(r.remaining) }
        register(ServerProt.MESSAGE_QUICKCHAT_CLANCHANNEL) { r -> ServerPacket.MessageQuickchatClanchannel(r.remaining) }
        register(ServerProt.MESSAGE_QUICKCHAT_FRIENDCHAT) { r -> ServerPacket.MessageQuickchatFriendchat(r.remaining) }
        register(ServerProt.MESSAGE_QUICKCHAT_PRIVATE) { r -> ServerPacket.MessageQuickchatPrivate(r.remaining) }
        register(ServerProt.RUN_CLIENTSCRIPT) { r -> ServerPacket.RunClientscript(r.remaining) }
        register(ServerProt.CHAT_FILTER_SETTINGS) { r -> ServerPacket.ChatFilterSettings(r.remaining) }
        register(ServerProt.SET_CHAT_FILTER_A) { r -> ServerPacket.SetChatFilterA(r.g1()) }
        register(ServerProt.SET_CHAT_FILTER_B) { r -> ServerPacket.SetChatFilterB(r.g1()) }
        // SET_CHAT_FILTER_C removed: op 164 is now SET_CHAT_FILTER_A (registered above)
        // SET_CHAT_FILTER_D removed: op 133 is now CREATE_CHECK_EMAIL_REPLY
        register(ServerProt.CREATE_CHECK_EMAIL_REPLY) { r -> ServerPacket.CreateCheckEmailReply(r.g1()) }
        // CREATE_CHECK_NAME_REPLY (opcode 204, 1B) — verified: was SET_CHAT_FILTER_A in 946-3
        register(ServerProt.CREATE_CHECK_NAME_REPLY) { r -> ServerPacket.CreateCheckNameReply(r.g1()) }
        register(ServerProt.CLANSETTINGS_DELTA_CHAT) { r -> ServerPacket.ClansettingsDeltaChat(r.remaining) }
        register(ServerProt.FRIENDCHAT_JOIN) { r -> ServerPacket.FriendchatJoin(r.remaining) }
        register(ServerProt.CLANCHANNEL_FULL_CHAT) { r -> ServerPacket.ClanchannelFullChat(r.remaining) }
    }

    // ── Variables ────────────────────────────────────────────────────────────

    private fun registerVariables() {
        // SET_VARC_INT (opcode 2, var_short): g2 varc_id + g4 value
        // Binary handler at 0x001b97d0: reads ushort (BE byte-swap) for varId, then 4 bytes (BE byte-swap) for value
        register(ServerProt.SET_VARC_INT) { r ->
            val varId = r.g2()
            val value = if (r.remaining >= 4) r.g4()
                else if (r.remaining >= 2) r.g2()
                else if (r.remaining >= 1) r.g1()
                else 0
            ServerPacket.SetVarcInt(varId, value)
        }

        // SET_VARC_INT_2 (opcode 12, 6B) — handler: Variables::SET_VARC_INT at 0x001b9860
        // Wire: [key:2BE] [value:4LE] — g2(key) + g4le(value)
        register(ServerProt.SET_VARC_INT_2) { r ->
            val key = r.g2()
            val value = r.g4le()
            ServerPacket.SetVarcInt2(key, value)
        }

        // SET_VARC_SMALL_2 (opcode 19, 3B) — handler: Variables::SET_VARC_SMALL at 0x001b9950
        // Wire: [value:1sub128] [key:2LE] — g1sub128(value) + g2le(key)
        register(ServerProt.SET_VARC_SMALL_2) { r ->
            val value = r.g1sub128()
            val key = r.g2le()
            ServerPacket.SetVarcSmall2(key, value)
        }

        // UPDATE_STAT (opcode 114, 6B) — handler: StatTable::UpdateStat at 0x0018f6e0
        // Wire: [xp:4alt1] [level:1add128] [statId:1neg]
        register(ServerProt.UPDATE_STAT) { r ->
            val xp = r.g4alt1()
            val level = r.g1add128()
            val statId = r.g1neg()
            ServerPacket.UpdateStat(statId, xp, level)
        }

        // RESET_VARC_SMALL (opcode 60, var_byte) — passthrough
        register(ServerProt.RESET_VARC_SMALL) { r ->
            ServerPacket.ResetVarcSmall(r.remaining)
        }

        // SET_VARP_SMALL (opcode 14, 3B): g2add128 varpId + g1sub128 value
        // Binary handler at 0x001b9b10: reads BE ushort (byte[0]*256 + (byte[1]+0x80)), then byte - 0x80
        register(ServerProt.SET_VARP_SMALL) { r ->
            val varpId = r.g2add128()
            val value = r.g1sub128()
            ServerPacket.SetVarpSmall(varpId, value)
        }

        // SET_VARP_INT (opcode 124, 6B): g2le varpId + g4alt1 value
        // Binary handler at 0x001b9bc0: reads LE ushort (byte[1]*256+byte[0]), then word-swapped int
        register(ServerProt.SET_VARP_INT) { r ->
            val varpId = r.g2le()
            val value = r.g4alt1()
            ServerPacket.SetVarpInt(varpId, value)
        }

        // SET_VARP_LONG (opcode 138, 10B): g8 value + g2add128 varpId
        // Binary handler at 0x001b9c90: reads BE long (8B byte-swap), then BE ushort with +0x80 on low byte
        register(ServerProt.SET_VARP_LONG) { r ->
            val value = r.g8()
            val varpId = r.g2add128()
            ServerPacket.SetVarpLong(varpId, value)
        }

        // SET_VARBIT_INT_2 (opcode 51, 6B) — handler: Variables::SET_VARBIT_INT at 0x001b9a10
        // Wire: [value:4BE] [key:2BE] — g4(value) + g2(key)
        register(ServerProt.SET_VARBIT_INT_2) { r ->
            val value = r.g4()
            val key = r.g2()
            ServerPacket.SetVarbitInt2(key, value)
        }

        // SET_VARBIT_SMALL_2 (opcode 72, 3B) — handler: Variables::SET_VARBIT_SMALL at 0x001b9ac0
        // Wire: [key:2BE] [value:1] — g2(key) + g1(value)
        register(ServerProt.SET_VARBIT_SMALL_2) { r ->
            val key = r.g2()
            val value = r.g1()
            ServerPacket.SetVarbitSmall2(key, value)
        }

        // RESET_ALL_VARPS (opcode 112, 0B) — verified: was UPDATE_STAT in 946-3 (rotation chain)
        register(ServerProt.RESET_ALL_VARPS) { ServerPacket.ResetAllVarps() }

        // RESET_CLIENT_STATE (opcode 32, 0B) — verified: was RESET_ALL_VARPS in 946-3 (rotation chain)
        register(ServerProt.RESET_CLIENT_STATE) { ServerPacket.ResetClientState() }
    }

    // ── Interfaces ──────────────────────────────────────────────────────────

    private fun registerInterfaces() {
        // IF_OPENTOP (opcode 207, 2B)
        register(ServerProt.IF_OPENTOP) { r -> ServerPacket.IfOpenTop(r.g2()) }

        // IF_OPENSUB (opcode 182, 5B): g4 componentHash + g1 type
        register(ServerProt.IF_OPENSUB) { r ->
            ServerPacket.IfOpenSub(r.g4(), r.g1())
        }

        // IF_OPENSUB_ACTIVE (opcode 20, var_short)
        register(ServerProt.IF_OPENSUB_ACTIVE) { r -> ServerPacket.IfOpenSubActive(r.remaining) }

        // IF_CLOSESUB (opcode 180, 5B): g4 componentHash + g1 type
        register(ServerProt.IF_CLOSESUB) { r ->
            ServerPacket.IfCloseSub(r.g4(), r.g1())
        }

        // IF_MOVESUB (opcode 8, 6B): g4 srcHash + g2 dstHash
        register(ServerProt.IF_MOVESUB) { r ->
            ServerPacket.IfMoveSub(r.g4(), r.g2())
        }

        // IF_SETTEXT_ACTIVE (opcode 190, 3B) — handler writes IC+0x154/0x155
        register(ServerProt.IF_SETTEXT_ACTIVE) { r ->
            ServerPacket.IfSetTextActive(r.g2(), r.g1())
        }

        // IF_SETTEXT (opcode 59, 12B): g4 componentHash + remaining text data
        register(ServerProt.IF_SETTEXT) { r ->
            val hash = r.g4()
            ServerPacket.IfSetText(hash, r.remaining)
        }

        // IF_SETTEXT2 (opcode 125, 8B): g4 componentHash + g4 textSize
        register(ServerProt.IF_SETTEXT2) { r ->
            ServerPacket.IfSetText2(r.g4(), r.g4())
        }

        // IF_SETHIDE (opcode 4, 10B): g4 componentHash + g1 hidden flag
        register(ServerProt.IF_SETHIDE) { r ->
            val hash = r.g4()
            val hidden = r.g1() == 0x7F
            ServerPacket.IfSetHide(hash, hidden)
        }

        // IF_SETHIDE_ACTIVE (opcode 145, 8B): g4 componentHash + g4 (boolean packed)
        register(ServerProt.IF_SETHIDE_ACTIVE) { r ->
            val hash = r.g4()
            val hidden = r.g4() != 0
            ServerPacket.IfSetHideActive(hash, hidden)
        }

        // IF_SETCOLOUR (opcode 48, 5B): g4 colour + g1 extra
        register(ServerProt.IF_SETCOLOUR) { r ->
            val hash = r.g4()
            val colour = r.g1()
            ServerPacket.IfSetColour(hash, colour)
        }

        // IF_SETPOSITION (opcode 38, 23B): g4 hash + g2 x + g2 y + remaining params
        register(ServerProt.IF_SETPOSITION) { r ->
            val hash = r.g4()
            val x = r.g2()
            val y = r.g2()
            ServerPacket.IfSetPosition(hash, x, y)
        }

        // IF_SETMODEL_ACTIVE (opcode 195, 3B) — handler writes IC+0x152/0x153
        register(ServerProt.IF_SETMODEL_ACTIVE) { r ->
            ServerPacket.IfSetModelActive(r.g2())
        }

        // IF_SETSCROLLPOS (opcode 45, 5B): g1 index + g4 hash
        register(ServerProt.IF_SETSCROLLPOS) { r ->
            val scrollPos = r.g1()
            val hash = r.g4()
            ServerPacket.IfSetScrollPos(hash, scrollPos)
        }

        // IF_SETANGLE (opcode 21, 32B)
        register(ServerProt.IF_SETANGLE) { r ->
            val hash = r.g4()
            ServerPacket.IfSetAngle(hash)
        }

        // IF_SETOBJECT_ACTIVE (opcode 128, 3B) — handler writes IC+0x150/0x151
        register(ServerProt.IF_SETOBJECT_ACTIVE) { r ->
            ServerPacket.IfSetObjectActive(r.g2())
        }

        // IF_SETCLICKMASK (opcode 89, 8B): g4 hash + g4 mask
        register(ServerProt.IF_SETCLICKMASK) { r ->
            ServerPacket.IfSetClickMask(r.g4())
        }

        // IF_MOVESUB_ACTIVE (opcode 197, 3B) — handler writes IC+0x156/0x157
        register(ServerProt.IF_MOVESUB_ACTIVE) { r ->
            ServerPacket.IfMoveSubActive(r.g2())
        }

        // IF_SETTARGETPARAM (opcode 57, var_short)
        register(ServerProt.IF_SETTARGETPARAM) { r -> ServerPacket.IfSetTargetParam(r.remaining) }

        // IF_SETEVENTS (opcode 202, 9B): g2 fromSlot + g2 toSlot + g1 index + g4 hash
        register(ServerProt.IF_SETEVENTS) { r ->
            val fromSlot = r.g2()
            val toSlot = r.g2()
            r.skip(1)
            val hash = r.g4()
            ServerPacket.IfSetEvents(hash, fromSlot, toSlot)
        }

        // IF_SETOBJECT (opcode 50, 4B): g2 slot + g2 hash
        register(ServerProt.IF_SETOBJECT) { r ->
            val objId = r.g2()
            val hash = r.g2()
            ServerPacket.IfSetObject(hash, objId)
        }

        // IF_SETOBJECT_NONUM (opcode 26, 8B): g4 hash + g4 objId
        register(ServerProt.IF_SETOBJECT_NONUM) { r ->
            val hash = r.g4()
            val objId = r.g4()
            ServerPacket.IfSetObjectNoNum(hash, objId)
        }

        // IF_SETOBJECT_NONUM_2 (opcode 140, 5B)
        register(ServerProt.IF_SETOBJECT_NONUM_2) { r ->
            ServerPacket.IfSetObjectNoNum2(r.g4())
        }

        // IF_SETOBJECT_ALWAYSNUM (opcode 108, 6B): g2 objId + g4 hash
        register(ServerProt.IF_SETOBJECT_ALWAYSNUM) { r ->
            val objId = r.g2()
            val hash = r.g4()
            ServerPacket.IfSetObjectAlwaysNum(hash, objId)
        }

        // IF_SETMODEL (opcode 7, 29B)
        register(ServerProt.IF_SETMODEL) { r -> ServerPacket.IfSetModel(r.g4()) }

        // IF_OPENSUB_2 (opcode 173, var_short) — verified: was IF_SETMODEL_ACTIVE in 946-3
        register(ServerProt.IF_OPENSUB_2) { r -> ServerPacket.IfOpensub2(r.remaining) }

        // IF_SETMODEL_ANIMATION (opcode 77, 4B): g2 animId + g2 hash
        register(ServerProt.IF_SETMODEL_ANIMATION) { r ->
            val animId = r.g2()
            val hash = r.g2()
            ServerPacket.IfSetModelAnimation(hash, animId)
        }

        // IF_SETMODEL_BODYTYPE (opcode 67, 25B)
        register(ServerProt.IF_SETMODEL_BODYTYPE) { r ->
            ServerPacket.IfSetModelBodyType(r.g4())
        }

        // IF_SETMODEL_COLOUR (opcode 113, 10B)
        register(ServerProt.IF_SETMODEL_COLOUR) { r ->
            ServerPacket.IfSetModelColour(r.g4())
        }

        // IF_SETMODEL_RECOLOUR (opcode 116, 25B)
        register(ServerProt.IF_SETMODEL_RECOLOUR) { r ->
            ServerPacket.IfSetModelRecolour(r.g4())
        }

        // IF_SETGRAPHIC (opcode 126, 19B): g4 hash + g4 graphicId + remaining
        register(ServerProt.IF_SETGRAPHIC) { r ->
            ServerPacket.IfSetGraphic(r.g4())
        }

        // IF_SETGRAPHIC_ACTIVE (opcode 162, var_short)
        register(ServerProt.IF_SETGRAPHIC_ACTIVE) { r ->
            ServerPacket.IfSetGraphicActive(r.remaining)
        }

        // IF_SETANIM (opcode 106, 4B): g2 animId + g2 hash
        register(ServerProt.IF_SETANIM) { r ->
            val animId = r.g2()
            val hash = r.g2()
            ServerPacket.IfSetAnim(hash, animId)
        }

        // IF_SETTEXTFONT (opcode 61, 8B): g4 hash + g4 fontId
        register(ServerProt.IF_SETTEXTFONT) { r ->
            val hash = r.g4()
            val fontId = r.g4()
            ServerPacket.IfSetTextFont(hash, fontId)
        }

        // IF_SETRECOL (opcode 62, 10B)
        register(ServerProt.IF_SETRECOL) { r -> ServerPacket.IfSetRecol(r.g4()) }

        // IF_SETRECOL_ACTIVE (opcode 210, 4B)
        register(ServerProt.IF_SETRECOL_ACTIVE) { r -> ServerPacket.IfSetRecolActive(r.g4()) }

        // IF_SETRETEX (opcode 75, 10B)
        register(ServerProt.IF_SETRETEX) { r -> ServerPacket.IfSetRetex(r.g4()) }

        // IF_SETPLAYERMODEL (opcode 93, 10B)
        register(ServerProt.IF_SETPLAYERMODEL) { r -> ServerPacket.IfSetPlayerModel(r.g4()) }

        // IF_SETPLAYERMODEL_SELF (opcode 105, 8B)
        register(ServerProt.IF_SETPLAYERMODEL_SELF) { r -> ServerPacket.IfSetPlayerModelSelf(r.g4()) }

        // IF_SETPLAYERMODEL_OTHER (opcode 36, var_short)
        register(ServerProt.IF_SETPLAYERMODEL_OTHER) { r -> ServerPacket.IfSetPlayerModelOther(r.remaining) }

        // IF_SETPLAYERMODEL_ANIM (opcode 119, 8B): g4 hash + g4 animId
        register(ServerProt.IF_SETPLAYERMODEL_ANIM) { r ->
            val hash = r.g4()
            val animId = r.g4()
            ServerPacket.IfSetPlayerModelAnim(hash, animId)
        }

        // IF_SETPLAYERMODEL_BASECOLOUR (opcode 71, 8B)
        register(ServerProt.IF_SETPLAYERMODEL_BASECOLOUR) { r ->
            ServerPacket.IfSetPlayerModelBaseColour(r.g4())
        }

        // IF_SETPLAYERMODEL_BODYTYPE (opcode 74, 8B)
        register(ServerProt.IF_SETPLAYERMODEL_BODYTYPE) { r ->
            ServerPacket.IfSetPlayerModelBodyType(r.g4())
        }

        // IF_SETPLAYERMODEL_EXACTMOVE (opcode 176, 14B)
        register(ServerProt.IF_SETPLAYERMODEL_EXACTMOVE) { r ->
            ServerPacket.IfSetPlayerModelExactmove(r.g4())
        }

        // IF_SETNPCMODEL (opcode 100, 10B): g2 slotA + g2 npcId + g2 slotB + g4 hash
        register(ServerProt.IF_SETNPCMODEL) { r ->
            r.skip(2) // slotA
            val npcId = r.g2()
            r.skip(2) // slotB
            val hash = r.g4()
            ServerPacket.IfSetNpcModel(hash, npcId)
        }

        // IF_SETNPCMODEL_ACTIVE (opcode 172, 5B)
        register(ServerProt.IF_SETNPCMODEL_ACTIVE) { r ->
            ServerPacket.IfSetNpcModelActive(r.g4())
        }

        // IF_SETNPCMODEL_ANIM (opcode 137, 9B): g4 hash + g2 npcId + g2 animId + g1 extra
        register(ServerProt.IF_SETNPCMODEL_ANIM) { r ->
            val hash = r.g4()
            val npcId = r.g2()
            val animId = r.g2()
            ServerPacket.IfSetNpcModelAnim(hash, npcId, animId)
        }
    }

    // ── Zone Updates ────────────────────────────────────────────────────────

    private fun registerZoneUpdates() {
        // UPDATE_ZONE_PARTIAL_FOLLOWS (opcode 120, 3B): level + zoneX + zoneY
        register(ServerProt.UPDATE_ZONE_PARTIAL_FOLLOWS) { r ->
            val level = r.g1()
            val zoneX = r.g1()
            val zoneY = r.g1()
            ServerPacket.UpdateZonePartialFollows(zoneX, zoneY, level)
        }

        // UPDATE_ZONE_FULL_FOLLOWS (opcode 90, 3B): level + zoneX + zoneY
        register(ServerProt.UPDATE_ZONE_FULL_FOLLOWS) { r ->
            val level = r.g1()
            val zoneX = r.g1()
            val zoneY = r.g1()
            ServerPacket.UpdateZoneFullFollows(zoneX, zoneY, level)
        }

        // UPDATE_ZONE_FULL_FOLLOWS_2 (opcode 65, var_byte)
        register(ServerProt.UPDATE_ZONE_FULL_FOLLOWS_2) { r ->
            ServerPacket.UpdateZoneFullFollows2(r.remaining)
        }

        // UPDATE_ZONE_FULL_FOLLOWS_3 (opcode 141, var_short)
        register(ServerProt.UPDATE_ZONE_FULL_FOLLOWS_3) { r ->
            ServerPacket.UpdateZoneFullFollows3(r.remaining)
        }

        // UPDATE_ZONE_PARTIAL (opcode 43, var_short)
        register(ServerProt.UPDATE_ZONE_PARTIAL) { r ->
            ServerPacket.UpdateZonePartial(r.remaining)
        }

        // OBJ_ADD (opcode 87, 5B): g2leAdd128 objId + g1neg coord + g2add128 count
        // Binary handler at 0x001b9550: reads LE ushort+128 (objId), negated byte (coord), BE ushort+128 (count)
        register(ServerProt.OBJ_ADD) { r ->
            val objId = r.g2leAdd128()
            val coord = r.g1neg()
            val count = r.g2add128()
            ServerPacket.ObjAdd(coord, objId, count)
        }

        // OBJ_DEL (opcode 98, 3B): g1 coord + g2 objId
        register(ServerProt.OBJ_DEL) { r ->
            val coord = r.g1()
            val objId = r.g2()
            ServerPacket.ObjDel(coord, objId)
        }

        // OBJ_COUNT (opcode 3, 7B): g2le objId + g2le newCount + g1sub128 coord + g2le playerIndex
        // Binary handler at 0x001b9410: reads LE ushorts for objId and counts, sub128 for coord
        register(ServerProt.OBJ_COUNT) { r ->
            val objId = r.g2le()
            val newCount = r.g2le()
            val coord = r.g1sub128()
            val oldCount = r.g2le()
            ServerPacket.ObjCount(coord, objId, oldCount, newCount)
        }

        // OBJ_REVEAL (opcode 127, 7B): g1 coord + g2 objId + g2 oldCount + g2 newCount
        register(ServerProt.OBJ_REVEAL) { r ->
            val coord = r.g1()
            val objId = r.g2()
            ServerPacket.ObjReveal(coord, objId)
        }

        // LOC_ADD_CHANGE (opcode 41, 10B): g4alt3 packedLoc + g4alt3 secondary + g1 flags + g1 opacity
        // Binary handler at 0x001e4320: reads two g4s_alt3 (middle-endian), then 2 bytes
        // packedLoc: bits 0-13 = locId, bits 14-27 = shape, bits 28-29 = rotation; -1 = remove
        register(ServerProt.LOC_ADD_CHANGE) { r ->
            val packed = r.g4alt3()
            val locId = if (packed == -1) -1 else packed and 0x3FFF
            val shape = if (packed == -1) 0 else (packed ushr 14) and 0x3FFF
            val rotation = if (packed == -1) 0 else (packed ushr 28) and 3
            ServerPacket.LocAddChange(rotation, shape, locId)
        }

        // LOC_DEL (opcode 64, 2B): g1 coord + g1 shapeRot
        register(ServerProt.LOC_DEL) { r ->
            val coord = r.g1()
            val shapeAndRot = r.g1()
            ServerPacket.LocDel(coord, shapeAndRot)
        }

        // LOC_ADD (opcode 30, var_byte)
        register(ServerProt.LOC_ADD) { r -> ServerPacket.LocAdd(r.remaining) }

        // LOC_MERGE (opcode 132, 5B): g4 entityIndex (BE) + g1 coord
        // Binary handler at 0x0018ef50: reads BE uint (entity server index), then 1 byte (coord+shape)
        register(ServerProt.LOC_MERGE) { r ->
            r.skip(4) // entityIndex (BE)
            val coord = r.g1()
            ServerPacket.LocMerge(coord)
        }

        // LOC_ANIM_SPECIFIC (opcode 6, 10B): g1 coord + remaining
        register(ServerProt.LOC_ANIM_SPECIFIC) { r ->
            val coord = r.g1()
            ServerPacket.LocAnimSpecific(coord)
        }

        // LOC_PREFETCH (opcode 78, 7B): g1 flags + g4alt3 locId + g1 shapeCoord + g1 coord
        // Binary handler at 0x001e4520: reads g1 (flags), then g4s_alt3 (middle-endian locId), then 2 bytes
        register(ServerProt.LOC_PREFETCH) { r ->
            r.skip(1) // flags
            val locId = r.g4alt3()
            ServerPacket.LocPrefetch(locId)
        }

        // LOC_CUSTOMISE (opcode 82, var_byte)
        register(ServerProt.LOC_CUSTOMISE) { r -> ServerPacket.LocCustomise(r.remaining) }

        // MAP_PROJANIM (opcode 24, 20B)
        register(ServerProt.MAP_PROJANIM) { r -> ServerPacket.MapProjanim(r.remaining) }

        // MAP_PROJANIM_HALT (opcode 159, 28B)
        register(ServerProt.MAP_PROJANIM_HALT) { r -> ServerPacket.MapProjanimHalt(r.remaining) }

        // MAP_PROJANIM_2 (opcode 185, 33B)
        register(ServerProt.MAP_PROJANIM_2) { r -> ServerPacket.MapProjanim2(r.remaining) }

        // MAP_ANIM (opcode 122, 11B)
        register(ServerProt.MAP_ANIM) { r -> ServerPacket.MapAnim(r.remaining) }

        // MAP_ANIM_SPECIFIC (opcode 194, 14B)
        register(ServerProt.MAP_ANIM_SPECIFIC) { r -> ServerPacket.MapAnimSpecific(r.remaining) }

        // PROJANIM_SPECIFIC (opcode 214, 21B)
        register(ServerProt.PROJANIM_SPECIFIC) { r -> ServerPacket.ProjanimSpecific(r.remaining) }

        // PROJANIM_SPECIFIC_HALT (opcode 147, 29B)
        register(ServerProt.PROJANIM_SPECIFIC_HALT) { r -> ServerPacket.ProjanimSpecificHalt(r.remaining) }

        // SPOTANIM_SPECIFIC (opcode 91, 12B)
        register(ServerProt.SPOTANIM_SPECIFIC) { r -> ServerPacket.SpotanimSpecific(r.remaining) }

        // SOUND_AREA (opcode 198, var_byte)
        register(ServerProt.SOUND_AREA) { r -> ServerPacket.SoundArea(r.remaining) }
    }

    // ── Camera ──────────────────────────────────────────────────────────────

    private fun registerCamera() {
        // CAM_TARGET (opcode 23, 1B)
        register(ServerProt.CAM_TARGET) { r -> ServerPacket.CamTarget(r.g1()) }

        // CAM_MOVETO (opcode 44, 6B): g1 x-0x80, g1 y-0x80, g2 height, g1 speed, g1 accel
        register(ServerProt.CAM_MOVETO) { r ->
            val x = r.g1() - 128
            val y = r.g1() - 128
            val height = r.g2()
            ServerPacket.CamMoveto(x, y, height)
        }

        // CAM_MOVETO_ARC (opcode 96, 4B): g4le (little-endian int)
        // Binary handler at 0x001c6220: reads g4s_alt2 = g4le()
        register(ServerProt.CAM_MOVETO_ARC) { r -> ServerPacket.CamMovetoArc(r.g4le()) }

        // CAM_LOOKAT (opcode 58, 6B): same layout as CAM_MOVETO
        register(ServerProt.CAM_LOOKAT) { r ->
            val x = r.g1() - 128
            val y = r.g1() - 128
            val height = r.g2()
            ServerPacket.CamLookat(x, y, height)
        }

        // CAM_LOOKAT_ARC (opcode 79, 6B): 4 signed byte coords (-0x80 transform) + ushort
        // Binary handler at 0x0018cda0: reads 6 individual bytes with transforms, not a plain g4
        register(ServerProt.CAM_LOOKAT_ARC) { r -> ServerPacket.CamLookatArc(r.g2()) }

        // CAM_FORCEANGLE (opcode 52, 1B)
        register(ServerProt.CAM_FORCEANGLE) { r -> ServerPacket.CamForceAngle(r.g1()) }

        // CAM_SHAKE (opcode 55, 4B): two scrambled ushorts with sub128/add128 transforms
        // Binary handler at 0x001eaf30: ushort1 = b[1]*256 + ((b[0]-0x80)&0xFF), ushort2 = b[3]*256 + ((b[2]+0x80)&0xFF)
        register(ServerProt.CAM_SHAKE) { r ->
            val low0 = r.g1sub128() and 0xFF
            val high0 = r.g1()
            val shakeType = (high0 shl 8) or low0
            r.skip(2) // second ushort (intensity) not captured
            ServerPacket.CamShake(shakeType)
        }

        // CAM_RESET (opcode 69, 0B)
        register(ServerProt.CAM_RESET) { ServerPacket.CamReset() }

        // CAM_SMOOTHRESET (opcode 118, 0B)
        register(ServerProt.CAM_SMOOTHRESET) { ServerPacket.CamSmoothReset() }

        // CAM_UPDATE (opcode 110, var_short)
        register(ServerProt.CAM_UPDATE) { r -> ServerPacket.CamUpdate(r.remaining) }

        // UNUSED_NOOP (opcode 196, var_short) — verified: no-op handler; was OCULUS_SYNC in 946-3
        register(ServerProt.UNUSED_NOOP) { r -> ServerPacket.UnusedNoop(r.remaining) }
    }

    // ── Audio ───────────────────────────────────────────────────────────────

    private fun registerAudio() {
        // SYNTH_SOUND (opcode 158, 12B): g4 soundId + g1 volume + g2 loopDelay + g1 loopCount + g2 delay + g2 attenuation
        register(ServerProt.SYNTH_SOUND) { r -> ServerPacket.SynthSound(r.g4()) }

        // MIDI_SONG (opcode 49, 10B): g4 songId + g1 volume + g2 fadeIn + g1 loop + g2 delay
        register(ServerProt.MIDI_SONG) { r -> ServerPacket.MidiSong(r.g4()) }

        // MIDI_JINGLE (opcode 5, 10B): same structure as MIDI_SONG
        register(ServerProt.MIDI_JINGLE) { r -> ServerPacket.MidiJingle(r.g4()) }

        // MIDI_STOP (opcode 166, 0B)
        register(ServerProt.MIDI_STOP) { ServerPacket.MidiStop() }

        // MIDI_SWAP (opcode 70, 5B): g1 volume + g4alt3 songId
        // Binary handler at 0x001e4180: reads g1 (volume) + g4s_alt3 (middle-endian int)
        register(ServerProt.MIDI_SWAP) { r ->
            r.skip(1) // volume
            ServerPacket.MidiSwap(r.g4alt3())
        }

        // SOUND_STOP_ALL (opcode 33, 0B)
        register(ServerProt.SOUND_STOP_ALL) { ServerPacket.SoundStopAll() }

        // SOUND_STOP (opcode 216, 2B): g2 soundId
        register(ServerProt.SOUND_STOP) { r -> ServerPacket.SoundStop(r.g2()) }

        // SOUND_MIXBUSS_SETLEVEL (opcode 34, 5B): g1 level + g4 bussId
        register(ServerProt.SOUND_MIXBUSS_SETLEVEL) { r ->
            r.skip(1) // level
            ServerPacket.SoundMixbussSetLevel(r.g4())
        }

        // SOUND_AREA_SYNTH (opcode 76, 8B): g4 soundId + 4B params
        register(ServerProt.SOUND_AREA_SYNTH) { r -> ServerPacket.SoundAreaSynth(r.g4()) }

        // SOUND_AREA_SYNTH_2 (opcode 184, 4B): g4 soundId
        register(ServerProt.SOUND_AREA_SYNTH_2) { r -> ServerPacket.SoundAreaSynth2(r.g4()) }

        // SOUND_GROUP (opcode 117, 11B): g4 soundId + g1 vol + g2 x + g2 y + g1 bone + g1 type
        register(ServerProt.SOUND_GROUP) { r -> ServerPacket.SoundGroup(r.g4()) }

        // SOUND_GROUP_STOP (opcode 136, 2B): g2 groupId
        register(ServerProt.SOUND_GROUP_STOP) { r -> ServerPacket.SoundGroupStop(r.g2()) }

        // SOUND_GROUP_SPEED (opcode 160, 6B): g4 groupId + g2 speed
        register(ServerProt.SOUND_GROUP_SPEED) { r -> ServerPacket.SoundGroupSpeed(r.g4()) }

        // SOUND_MODIFY (opcode 200, 4B): g2 soundId + g2 speed
        register(ServerProt.SOUND_MODIFY) { r -> ServerPacket.SoundModify(r.g2()) }

        // VORBIS_PRELOAD (opcode 209, 4B): g4alt3 resourceId (middle-endian)
        // Binary handler at 0x001e4110: reads g4s_alt3 (middle-endian int)
        register(ServerProt.VORBIS_PRELOAD) { r -> ServerPacket.VorbisPreload(r.g4alt3()) }

        // VORBIS_SONG (opcode 213, 6B): g2 songId + g2 loopCount + g2 duration
        register(ServerProt.VORBIS_SONG) { r -> ServerPacket.VorbisSong(r.g2()) }
    }

    // ── Inventory ───────────────────────────────────────────────────────────

    private fun registerInventory() {
        // UPDATE_INV_PARTIAL (opcode 81, var_short): g4 interfaceHash + g2 invId + remaining slots
        register(ServerProt.UPDATE_INV_PARTIAL) { r ->
            val interfaceHash = r.g4()
            val invId = r.g2()
            ServerPacket.UpdateInvPartial(interfaceHash, invId, r.remaining)
        }

        // UPDATE_INV_FULL (opcode 121, var_short): g4 interfaceHash + g2 invId + remaining slots
        register(ServerProt.UPDATE_INV_FULL) { r ->
            val interfaceHash = r.g4()
            val invId = r.g2()
            ServerPacket.UpdateInvFull(interfaceHash, invId, r.remaining)
        }

        // UPDATE_INV_GROUP (opcode 168, var_short)
        register(ServerProt.UPDATE_INV_GROUP) { r -> ServerPacket.UpdateInvGroup(r.remaining) }
    }

    // ── Combat ──────────────────────────────────────────────────────────────

    private fun registerCombat() {
        // PROJANIM (opcode 54, 25B)
        register(ServerProt.PROJANIM) { r -> ServerPacket.Projanim(r.remaining) }

        // NPC_HITMARKS_AND_HEADBARS (opcode 94, 19B)
        register(ServerProt.NPC_HITMARKS_AND_HEADBARS) { r -> ServerPacket.NpcHitmarksAndHeadbars(r.remaining) }
    }

    // ── NPC Info ─────────────────────────────────────────────────────────────

    private fun registerNpcInfo() {
        register(ServerProt.NPC_ANIM_SPECIFIC) { r -> ServerPacket.NpcAnimSpecific(r.remaining) }
        register(ServerProt.SET_NPC_OP) { r -> ServerPacket.SetNpcOp(r.remaining) }
        register(ServerProt.NPC_HEADICON_SPECIFIC) { r -> ServerPacket.NpcHeadiconSpecific(r.remaining) }
        register(ServerProt.SET_NPC_UPDATE_ORIGIN) { r -> ServerPacket.SetNpcUpdateOrigin(r.g1()) }
        register(ServerProt.SET_NPC_UPDATE_FLAG) { r -> ServerPacket.SetNpcUpdateFlag(r.g1()) }
    }

    // ── Player Info ─────────────────────────────────────────────────────────

    private fun registerPlayerInfo() {
        register(ServerProt.PLAYER_INFO_DECODE) { r -> ServerPacket.PlayerInfoDecode(r.remaining) }
        register(ServerProt.PLAYER_INFO_DECODE_2) { r -> ServerPacket.PlayerInfoDecode2(r.remaining) }
        register(ServerProt.MAP_FLAG_SET_PLAYER) { r -> ServerPacket.MapFlagSetPlayer(r.remaining) }
        register(ServerProt.SET_PLAYER_CHAT_EFFECTS) { r -> ServerPacket.SetPlayerChatEffects(r.g2()) }
        register(ServerProt.UPDATE_PLAYER_CHAT) { r -> ServerPacket.UpdatePlayerChat(r.remaining) }
        register(ServerProt.REBUILD_PLAYERINFO_POSITIONS) { r -> ServerPacket.RebuildPlayerinfoPositions(r.remaining) }
    }

    // ── Player Group ────────────────────────────────────────────────────────

    private fun registerPlayerGroup() {
        register(ServerProt.SET_PLAYER_GROUP) { r -> ServerPacket.SetPlayerGroup(r.remaining) }
        // SET_PLAYER_GROUP_2 (opcode 139, 4B): g4alt1 (word-swapped int) — was PLAYER_OP in 946-3
        // Binary handler at 0x00225d60: reads custom byte-order = g4alt1
        register(ServerProt.SET_PLAYER_GROUP_2) { r -> ServerPacket.SetPlayerGroup2(r.g4alt1()) }
        // PLAYER_OP (opcode 66, var_short) — verified: was FRIENDLIST_LOADED in 946-3
        register(ServerProt.PLAYER_OP) { r -> ServerPacket.PlayerOp(r.remaining) }
        // SET_TRIGGER_VAR (opcode 187, 4B) — verified: was PLAYER_GROUP_DELTA in 946-3
        register(ServerProt.SET_TRIGGER_VAR) { r -> ServerPacket.SetTriggerVar(r.remaining) }
        register(ServerProt.UPDATE_PLAYER_GROUP) { r -> ServerPacket.UpdatePlayerGroup(r.remaining) }
    }

    // ── Social ──────────────────────────────────────────────────────────────

    private fun registerSocial() {
        register(ServerProt.UPDATE_IGNORELIST) { r -> ServerPacket.UpdateIgnorelist(r.remaining) }
        register(ServerProt.UPDATE_IGNORELIST_2) { r -> ServerPacket.UpdateIgnorelist2(r.remaining) }
        // NPC_INFO_2 (opcode 174, var_short) — verified: was UPDATE_FRIENDLIST in 946-3
        register(ServerProt.NPC_INFO_2) { r -> ServerPacket.NpcInfo2(r.remaining) }
        // NPC_INFO_DECODE (opcode 28) — verified: was UPDATE_FRIENDLIST_2 in 946-3
        register(ServerProt.NPC_INFO_DECODE) { r -> ServerPacket.NpcInfoDecode(r.remaining) }
        // FRIENDLIST_LOADED removed: op 66 is now PLAYER_OP (registered in registerPlayerGroup)
        register(ServerProt.UPDATE_FRIENDCHAT_CHANNEL) { r -> ServerPacket.UpdateFriendchatChannel(r.remaining) }
        register(ServerProt.FRIENDCHAT_SYSUPDATE) { r -> ServerPacket.FriendchatSysupdate(r.g2()) }
    }

    // ── Clans ───────────────────────────────────────────────────────────────

    private fun registerClans() {
        register(ServerProt.CLANSETTINGS_FULL) { r -> ServerPacket.ClansettingsFull(r.remaining) }
        register(ServerProt.CLANSETTINGS_FULL_2) { r -> ServerPacket.ClansettingsFull2(r.remaining) }
        register(ServerProt.CLANSETTINGS_DELTA) { r -> ServerPacket.ClansettingsDelta(r.remaining) }
        register(ServerProt.CLANCHANNEL_FULL) { r -> ServerPacket.ClanchannelFull(r.remaining) }
        register(ServerProt.CLANCHANNEL_DELTA) { r -> ServerPacket.ClanchannelDelta(r.remaining) }
    }

    // ── Client State ────────────────────────────────────────────────────────

    private fun registerClientState() {
        // SET_TICK_TIMER (opcode 22, 2B): g2 timer value
        register(ServerProt.SET_TICK_TIMER) { r -> ServerPacket.SetTickTimer(r.g2()) }

        // SET_MAP_FLAG (opcode 129, 6B): g1 worldEntity + g1 position + g4 mapId
        register(ServerProt.SET_MAP_FLAG) { r ->
            val x = r.g1()
            val y = r.g1()
            ServerPacket.SetMapFlag(x, y)
        }

        // SPOTANIM_SPECIFIC_2 (opcode 193, 15B) — verified: was MAP_FLAG_SET in 946-3
        register(ServerProt.SPOTANIM_SPECIFIC_2) { r -> ServerPacket.SpotanimSpecific2(r.remaining) }

        // SET_READY_FLAG (opcode 35, 0B)
        register(ServerProt.SET_READY_FLAG) { ServerPacket.SetReadyFlag() }

        // DESTROY_ZONE_DATA (opcode 104, 0B) — verified: was RESET_CLIENT_STATE in 946-3 (rotation chain)
        register(ServerProt.DESTROY_ZONE_DATA) { ServerPacket.DestroyZoneData() }

        // CLEAR_PENDING_UPDATES (opcode 206, 0B) — verified: was DESTROY_ZONE_DATA in 946-3 (rotation chain)
        register(ServerProt.CLEAR_PENDING_UPDATES) { ServerPacket.ClearPendingUpdates() }

        // MINIMAP_TOGGLE (opcode 151, 3B) — verified: was RESET_ANIMS in 946-3
        register(ServerProt.MINIMAP_TOGGLE) { r -> ServerPacket.MinimapToggle(r.remaining) }

        // CLEAR_MAP_FLAG (opcode 191, 2B) — verified: was TRIGGER_ONDIALOGABORT in 946-3
        register(ServerProt.CLEAR_MAP_FLAG) { r -> ServerPacket.ClearMapFlag(r.remaining) }

        // TRIGGER_ONDIALOGABORT_2 (opcode 212, 0B)
        register(ServerProt.TRIGGER_ONDIALOGABORT_2) { ServerPacket.TriggerOndialogabort2() }

        // REBUILD_NORMAL (opcode 186, var_short) — carry raw payload for instance collision parsing
        register(ServerProt.REBUILD_NORMAL) { r -> ServerPacket.RebuildNormal(r.remainingBytes()) }

        // REBUILD_REGION (opcode 211, 5B) — verified: was SET_HEATMAP in 946-3
        register(ServerProt.REBUILD_REGION) { r -> ServerPacket.RebuildRegion(r.remaining) }

        // URL_OPEN (opcode 215, 3B): g2 urlId + g1 extra
        register(ServerProt.URL_OPEN) { r -> ServerPacket.UrlOpen(r.g2()) }
    }

    // ── World Entity ─────────────────────────────────────────────────────────

    private fun registerWorldEntity() {
        // WORLDENTITY_INFO_V1 (opcode 156, 6B) — verified: was CLANCHANNEL_DELTA_CS in 946-3
        register(ServerProt.WORLDENTITY_INFO_V1) { r -> ServerPacket.WorldentityInfoV1(r.remaining) }

        // WORLDENTITY_INFO_V2 (opcode 153, 2B) — verified: was RUNCLIENTSCRIPT in 946-3
        register(ServerProt.WORLDENTITY_INFO_V2) { r -> ServerPacket.WorldentityInfoV2(r.remaining) }

        // WORLDENTITY_INFO_V3 (opcode 157, 3B) — verified: was SET_PLAYER_OP in 946-3
        register(ServerProt.WORLDENTITY_INFO_V3) { r -> ServerPacket.WorldentityInfoV3(r.remaining) }

        // WORLDENTITY_INFO_V4 (opcode 142, 1B) — verified: was MINIMAP_FLAG_SET in 946-3
        register(ServerProt.WORLDENTITY_INFO_V4) { r -> ServerPacket.WorldentityInfoV4(r.remaining) }

        // WORLDENTITY_INFO_V5 (opcode 208, 3B) — verified: was UPDATE_REBOOT_TIMER in 946-3
        register(ServerProt.WORLDENTITY_INFO_V5) { r -> ServerPacket.WorldentityInfoV5(r.remaining) }

        // REBUILD_WORLDENTITY (opcode 178, var_short) — verified: was REBUILD_REGION in 946-3
        register(ServerProt.REBUILD_WORLDENTITY) { r -> ServerPacket.RebuildWorldentity(r.remaining) }
    }

    // ── Rebuild ─────────────────────────────────────────────────────────────

    private fun registerRebuild() {
        register(ServerProt.REBUILD_NORMAL_HANDLER) { r -> ServerPacket.RebuildNormalHandler(r.remaining) }
        register(ServerProt.REBUILD_REGION_HANDLER) { r -> ServerPacket.RebuildRegionHandler(r.remaining) }
    }

    // ── World Data ──────────────────────────────────────────────────────────

    private fun registerWorldData() {
        // SET_CAMERA_TARGET (opcode 143, 8B): g4 targetId + 4B extra
        register(ServerProt.SET_CAMERA_TARGET) { r -> ServerPacket.SetCameraTarget(r.g4()) }

        // SET_WORLD_TARGET (opcode 149, var_byte)
        register(ServerProt.SET_WORLD_TARGET) { r -> ServerPacket.SetWorldTarget(r.remaining) }
    }

    // ── Site Settings ───────────────────────────────────────────────────────

    private fun registerSiteSettings() {
        register(ServerProt.UPDATE_SITESETTINGS) { r -> ServerPacket.UpdateSiteSettings(r.remaining) }
    }

    // ── Misc ────────────────────────────────────────────────────────────────

    private fun registerMisc() {
        // NOOP (opcode 146, 0B)
        register(ServerProt.NOOP) { ServerPacket.Noop() }

        // NOOP_VAR_1 (opcode 111, var_byte)
        register(ServerProt.NOOP_VAR_1) { r -> ServerPacket.NoopVar(r.remaining) }

        // NOOP_VAR_2 (opcode 170, var_short)
        register(ServerProt.NOOP_VAR_2) { r -> ServerPacket.NoopVar(r.remaining) }

        // SERVER_TICK_END (opcode 203, 8B): g8 serverTime (ulong timestamp in ms)
        // Binary handler at 0x00221020: reads gT<ulong> (single 8-byte BE unsigned long)
        // Semantically one timestamp, but byte-wise equivalent to two g4 reads
        register(ServerProt.SERVER_TICK_END) { r ->
            val serverCycle = r.g4()
            val clientCycle = r.g4()
            ServerPacket.ServerTickEnd(serverCycle, clientCycle)
        }

        // LOGOUT (opcode 134, 0B)
        register(ServerProt.LOGOUT) { ServerPacket.Logout(false) }

        // LOGOUT_TRANSFER (opcode 131, 0B)
        register(ServerProt.LOGOUT_TRANSFER) { ServerPacket.Logout(true) }

        // RESET_ENTITY_LISTS (opcode 25, 0B)
        register(ServerProt.RESET_ENTITY_LISTS) { ServerPacket.ResetEntityLists() }

        // SET_RUN_ENERGY (opcode 27, 1B)
        register(ServerProt.SET_RUN_ENERGY) { r -> ServerPacket.SetRunEnergy(r.g1()) }

        // SET_WEIGHT (opcode 135, 1B)
        register(ServerProt.SET_WEIGHT) { r -> ServerPacket.SetWeight(r.g1()) }

        // SET_MULTIWAY_STATE (opcode 80, 1B)
        register(ServerProt.SET_MULTIWAY_STATE) { r -> ServerPacket.SetMultiwayState(r.g1()) }

        // SET_DISPLAY_INT (opcode 85, 4B)
        register(ServerProt.SET_DISPLAY_INT) { r -> ServerPacket.SetDisplayInt(r.g4()) }

        // SET_SYSUPDATE_TIMER (opcode 144, 4B): g3 ticks (BE medium, sign-extended) + g1 enabled
        // Binary handler at 0x00211e40: reads 3-byte BE medium (sign-extend if >= 0x800000) + 1 bool
        register(ServerProt.SET_SYSUPDATE_TIMER) { r ->
            var ticks = r.g3()
            if (ticks > 0x7FFFFF) ticks -= 0x1000000
            ServerPacket.SetSysupdateTimer(ticks)
        }

        // CUTSCENE_DATA (opcode 53, 35B)
        register(ServerProt.CUTSCENE_DATA) { r -> ServerPacket.CutsceneData(r.remaining) }

        // DETAIL_OPTIONS (opcode 84, var_short)
        register(ServerProt.DETAIL_OPTIONS) { r -> ServerPacket.DetailOptions(r.remaining) }

        // REMOVE_TRACKED_ENTRY (opcode 101, 3B): g2le key + g1 flag
        // Binary handler at 0x002130b0: reads 2 bytes as LE u16 key (b[1]*256+b[0]), then 1 flag byte
        register(ServerProt.REMOVE_TRACKED_ENTRY) { r -> ServerPacket.RemoveTrackedEntry(r.g2le()) }

        // REMOVE_PLAYER_FROM_LIST (opcode 205, 1B)
        register(ServerProt.REMOVE_PLAYER_FROM_LIST) { r -> ServerPacket.RemovePlayerFromList(r.g1()) }

        // SET_PLAYER_OP_2 (opcode 97, 2B)
        register(ServerProt.SET_PLAYER_OP_2) { r -> ServerPacket.SetPlayerOp2(r.g2()) }

        // SET_PLAYER_OP_3 (opcode 13, 1B)
        register(ServerProt.SET_PLAYER_OP_3) { r -> ServerPacket.SetPlayerOp3(r.g1()) }

        // SET_INTERACTION_FLAG_B (opcode 161, 1B)
        register(ServerProt.SET_INTERACTION_FLAG_B) { r -> ServerPacket.SetInteractionFlagB(r.g1()) }

        // SET_INTERACTION_FLAG_C (opcode 169, 1B)
        register(ServerProt.SET_INTERACTION_FLAG_C) { r -> ServerPacket.SetInteractionFlagC(r.g1()) }

        // SET_INTERACTION_FLAG_D (opcode 175, 3B)
        register(ServerProt.SET_INTERACTION_FLAG_D) { r -> ServerPacket.SetInteractionFlagD(r.g1()) }

        // SKIP_DATA (opcode 192, var_short)
        register(ServerProt.SKIP_DATA) { r -> ServerPacket.SkipData(r.remaining) }

        // SKIP_2_BYTES (opcode 167, 2B)
        register(ServerProt.SKIP_2_BYTES) { r -> ServerPacket.Skip2Bytes(r.g2()) }

        // UPDATE_URL_STRING (opcode 155, var_short)
        register(ServerProt.UPDATE_URL_STRING) { r -> ServerPacket.UpdateUrlString(r.remaining) }

        // SET_URL_STRING (opcode 171, var_byte)
        register(ServerProt.SET_URL_STRING) { r -> ServerPacket.SetUrlString(r.remaining) }

        // Formerly UNKNOWN opcodes — identified by Agent 1C, wire format TBD
        register(ServerProt.RESET_VARC_SMALL_2) { r -> ServerPacket.ResetVarcSmall2(r.remaining) }
        register(ServerProt.NOOP_UNHANDLED) { r -> ServerPacket.NoopUnhandled(r.remaining) }
        register(ServerProt.IF_TRIGGER_CLOSE) { r -> ServerPacket.IfTriggerClose() }
        register(ServerProt.RESET_VARC_INT) { r -> ServerPacket.ResetVarcInt(r.remaining) }
        register(ServerProt.SET_VARC_COORD_2) { r -> ServerPacket.SetVarcCoord2(r.remaining) }

        // SET_UID (opcode 0, 28 bytes) — wire format TBD
        register(ServerProt.SET_UID) { r -> ServerPacket.SetUid(r.remaining) }
    }
    ══════════════════════════════════════════════════════════════════════════ */

    // ── Registration & Decode ───────────────────────────────────────────────

    private fun register(prot: ServerProt, decoder: (PacketReader) -> ServerPacket) {
        decoders[prot] = decoder
    }

    fun decode(prot: ServerProt, data: ByteArray): ServerPacket? {
        val decoder = decoders[prot] ?: return null
        return try {
            decoder(PacketReader(data))
        } catch (_: Exception) {
            null
        }
    }
}
