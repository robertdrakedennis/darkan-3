package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for social/chat packets.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md`:
 *
 * MOVED:
 *  - MessageGame (MESSAGE_GAME):       105 → 93
 *  - UpdateIgnoreList:                 211 → 130
 *  - ClanChannelFull:                   28 → 67
 *  - ClanSettingsFull:                 104 → 29
 *  - ChatFilterSettingsPrivateChat (SET_CHAT_FILTER_B): 155 → 156
 *  - MessageClanChannel (MESSAGE_CLANCHANNEL_SYSTEM in 947-3): no direct 948 destination
 *    identified, but the new 948 op 105 MESSAGE_CLANCHANNEL is the likely successor.
 *
 * ENCODERS DISABLED (wire WRONG/UNCONFIRMED — better no encoder than malformed bytes):
 *  - UpdateIgnoreList @ op 130 — handler 0x001d29c0 is a 64-bit-flag-mask friend/relationship
 *    DELTA (likely the true 948 UPDATE_FRIENDLIST), NOT ignore-pairs (SVR-B research doc B).
 *  - MessageClanChannel @ op 105 — handler 0x001a1cc0 wire is channelIndex + hashed msgId +
 *    string, not the byte/short stub the prior encoder emitted (SVR-B research doc B).
 *  Both data classes are retained; opcode metadata is supplied by Rev948ServerProtStubs. A live
 *  capture is needed before re-registering either.
 *
 * Wire formats for the remaining (registered) packets are presumed byte-equivalent to 947-3 (the
 * Phase 1 walk did not flag transform changes for them). Cross-verify before production use.
 */
internal fun Codec.registerRev948ServerCodecsSocial() {

    // MESSAGE_GAME (op 93, varByte) — was op 105 in 947-3.
    serverProt<GameMessage>(opcode = 93, size = ProtSize.VarByte) { out ->
        out.writeSmart(type.value)
        out.writeInt(effectFlags)
        val hasSender = targetDisplayName != null
        val flags = if (hasSender) 1 else 0
        out.writeByte(flags)
        if (hasSender) {
            out.writeRSString(targetDisplayName!!)
        }
        out.writeRSString(message)
    }

    // UPDATE_IGNORELIST (op 130, varByte) — ENCODER DISABLED (wire UNCONFIRMED).
    // SVR-B (docs/net/serverprot/948-research-B-interface-social.md §"op 130 ... NAME SUSPECT")
    // found the 948 op-130 handler (0x001d29c0) reads a 64-bit flag mask + ~48 conditionally-gated
    // fields and stores a single 0x1e0-byte per-friend RELATIONSHIP/friend-delta struct to
    // *(RelationshipManager+0x98). This is NOT a list of (displayName, previousName) ignore pairs —
    // it is structurally a redesigned friend/relationship update (likely the true 948
    // UPDATE_FRIENDLIST). The old ignore-pairs encoder would emit malformed bytes -> client crash.
    // Per "no wrong encoder", op 130 is left UNREGISTERED; opcode metadata is supplied by the
    // Rev948ServerProtStubs table (s(130, "UPDATE_IGNORELIST", -1)). The UpdateIgnoreList data class
    // is retained. TODO: re-derive the exact 64-bit-mask field layout from 0x001d29c0 + a live
    // capture, then register the correct (friend-delta) encoder under its confirmed identity.

    // CLANCHANNEL_FULL (op 67, varShort) — was op 28 in 947-3.
    serverProt<ClanChannelFull>(opcode = 67, size = ProtSize.VarShort) { out ->
        if (clanName == null || chatters == null) {
            out.writeByte(0xFF)
            return@serverProt
        }
        out.writeByte(if (main) 0 else 1)
        out.writeLong(updateNum)
        out.writeRSString(clanName!!)
        out.writeByte(0)
        out.writeByte(kickRank)
        out.writeByte(talkRank)
        val members = chatters!!
        out.writeShort(members.size)
        for (chatter in members) {
            out.writeRSString(chatter.displayName)
            out.writeByte(chatter.rank)
            out.writeShort(chatter.worldId)
        }
    }

    // CLANSETTINGS_FULL (op 29, varShort) — was op 104 in 947-3.
    serverProt<ClanSettingsFull>(opcode = 29, size = ProtSize.VarShort) { out ->
        if (clanName == null || members == null) {
            out.writeByte(0xFF)
            return@serverProt
        }
        out.writeByte(if (main) 0 else 1)
        out.writeByte(3)
        out.writeInt(updateCount)
        out.writeRSString(clanName!!)
        out.writeByte(if (allowGuests) 1 else 0)
        out.writeByte(talkRank)
        out.writeByte(kickRank)
        val memberList = members!!
        out.writeShort(memberList.size)
        for (member in memberList) {
            out.writeRSString(member.displayName)
            out.writeByte(member.rank)
        }
        val banned = bannedUsers ?: emptyArray()
        out.writeShort(banned.size)
        for (name in banned) {
            out.writeRSString(name)
        }
        val settingsList = settings ?: emptyArray()
        out.writeShort(settingsList.size)
        for (setting in settingsList) {
            out.writeInt(setting.key)
            when {
                setting.stringValue != null -> { out.writeByte(2); out.writeRSString(setting.stringValue!!) }
                setting.longValue != null -> { out.writeByte(1); out.writeLong(setting.longValue!!) }
                else -> { out.writeByte(0); out.writeInt(setting.intValue ?: 0) }
            }
        }
    }

    // MESSAGE_CLANCHANNEL (op 105, varByte) — ENCODER DISABLED (wire WRONG / UNCONFIRMED).
    // SVR-B (docs/net/serverprot/948-research-B-interface-social.md §"MESSAGE_CLANCHANNEL ... WIRE
    // WRONG") disassembled handler 0x001a1cc0: it reads [byte channelIndex][gSmart hi-byte + 3 raw
    // bytes = 4-byte hashed msgId][CP1252 string message]. The prior encoder wrote
    // byte0/short0/byte0/short0/string, which does NOT match and would emit malformed bytes ->
    // client crash. Per "no wrong encoder", op 105 is left UNREGISTERED; opcode metadata is supplied
    // by the Rev948ServerProtStubs table (s(105, "MESSAGE_CLANCHANNEL", -1)). The MessageClanChannel
    // data class is retained. TODO: confirm the channelIndex / hashed-msgId semantics via a live
    // capture, then register the correct encoder.

    // SET_CHAT_FILTER_B (op 156, 1B) — was op 155 in 947-3.
    serverProt<ChatFilterSettingsPrivateChat>(opcode = 156, size = 1) { out ->
        out.writeByte(filter)
    }

    // SET_PLAYER_OP (op 17 / 0x11, varByte). Opcode VERIFIED via RegisterAll: entry 0x015c0f00 =
    // InitEntry(opcode=0x11=17, size=-1=VarByte). Handler Misc::SET_PLAYER_OP @ 0x0013eec0, bound
    // by PlayerList::BindHandlers @ 0x000ab3ea.
    // NOTE: a SEPARATE handler PlayerGroup::PLAYER_OP @ 0x001869f0 lives at op 0x17=23 (VarShort) —
    // do not confuse the two; this codec targets op 17.
    //
    // 948 wire (re-derived from 0x0013eec0 disassembly — CORRECTED field order + slot transform):
    //   [0..1] LE u16 worldId (0xFFFF -> -1 "current world")
    //   [2]    byte slot: client computes slotIndex = ((-rawByte) & 0xFF) - 1, range 0..7.
    //          Server emits writeByteInverse(slot + 1) so that -(slot+1) decodes back to slotIndex.
    //   [3..]  CP1252 string text  (read AFTER the slot byte — order differs from prior pass)
    //   [end]  byte cursorVisible: client sets visible = (rawByte == 0).
    // The prior codec wrote string BEFORE the slot byte and used byteSubtract — both wrong.
    serverProt<SetPlayerOp>(opcode = 17, size = ProtSize.VarByte) { out ->
        out.writeShortLittle(0xFFFF)             // worldId: -1 = current world
        out.writeByteInverse(slot + 1)           // client: ((-b)&0xFF)-1 == slot
        out.writeRSString(text ?: "null")
        out.writeByte(if (priority) 0 else 1)    // client visible = (byte == 0)
    }

    // HASHED_WORLD_TOKEN is registered in Rev948ServerCodecsMisc at op 54; production login
    // replay shows it in the world bootstrap packet group, not the social packet group.
}
