package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for social/chat packets.
 * Opcodes verified from rs2client.947-1 binary.
 * Wire formats from docs/net/serverprot/chat.md and docs/net/serverprot/social.md.
 */
internal fun Codec.registerRev947ServerCodecsSocial() {

    // MESSAGE_GAME (105, varByte) — system/game messages in chatbox.
    // Wire: smart(chatType) + int(param) + byte(flags) + [string(senderName)] + [string(displayName)] + string(message)
    // RE from docs/net/serverprot/chat.md handler at 0x001e6640
    serverProt<GameMessage>(opcode = 105, size = ProtSize.VarByte) { out ->
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

    // UPDATE_IGNORELIST (211, varByte) — sends ignore list entries to client.
    // Wire: per entry: string(displayName) + string(previousName)
    // Handler at 0x00277a40 (Social::UPDATE_IGNORELIST_thunk)
    serverProt<UpdateIgnoreList>(opcode = 211, size = ProtSize.VarByte) { out ->
        for (entry in ignores) {
            out.writeRSString(entry.displayName)
            out.writeRSString(entry.previousName)
        }
    }

    // FRIENDCHAT_JOIN (179, varByte) — friend chat channel member list.
    // Wire format from reference encoder adapted for 947.
    // When clear=true: write single 0 byte.
    // When clear=false: ownerDisplayName + channelId(8B) + kickRank(1B) + memberCount(1B) + members
    serverProt<FriendsChatChannel>(opcode = 179, size = ProtSize.VarByte) { out ->
        if (clear || ownerDisplayName == null) {
            // Signal channel leave / no channel
            out.writeLong(0)
            return@serverProt
        }
        out.writeRSString(ownerDisplayName!!)
        // Channel name encoded as base37-like long (simplified: write as 8-byte zero for now)
        // TODO: proper base37 encoding of chatName
        out.writeLong(0)
        out.writeByte(minRankCanKick)
        val members = players ?: emptyArray()
        out.writeByte(members.size)
        for (player in members) {
            out.writeRSString(player.displayName)
            out.writeShort(player.worldId)
            out.writeByte(player.rank)
            out.writeRSString(player.worldName)
        }
    }

    // CLANCHANNEL_FULL (28, varShort) — full clan channel member list.
    // Handler at 0x001be8b0. Wire: channelByte(1B) + channel data.
    // channelByte: 0 or 1 = slot index, <0 = clear
    serverProt<ClanChannelFull>(opcode = 28, size = ProtSize.VarShort) { out ->
        if (clanName == null || chatters == null) {
            // Clear: write -1 to signal no channel
            out.writeByte(0xFF)
            return@serverProt
        }
        out.writeByte(if (main) 0 else 1)
        out.writeLong(updateNum)
        out.writeRSString(clanName!!)
        // Unused field
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

    // CLANSETTINGS_FULL (104, varShort) — full clan settings.
    // Handler at Clans::CLANSETTINGS_FULL
    serverProt<ClanSettingsFull>(opcode = 104, size = ProtSize.VarShort) { out ->
        if (clanName == null || members == null) {
            out.writeByte(0xFF)
            return@serverProt
        }
        out.writeByte(if (main) 0 else 1)
        out.writeByte(3) // version/format byte
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

    // MESSAGE_CLANCHANNEL_SYSTEM (125, varByte) — clan channel chat message.
    // Simplified encoding: channelId(1B) + messageId(2B) + timestamp(3B) + message data
    serverProt<MessageClanChannel>(opcode = 125, size = ProtSize.VarByte) { out ->
        // Use simple game message encoding for clan chat
        // The client handler dispatches based on channel byte
        out.writeByte(0)  // channelId
        out.writeShort(0) // messageId
        // 3-byte timestamp
        out.writeByte(0)
        out.writeShort(0)
        // Message: just write as raw string for now
        // TODO: proper Huffman-compressed message encoding
        out.writeRSString(message)
    }

    // SET_CHAT_FILTER_B (155, 1B) — private chat filter echo.
    serverProt<ChatFilterSettingsPrivateChat>(opcode = 155, size = 1) { out ->
        out.writeByte(filter)
    }

    // SET_PLAYER_OP (0, varByte) — set player right-click options.
    // Wire: padding(1B) + text(string) + slot(1B) + priorityHigh(1B) + priorityLow(1B)
    serverProt<SetPlayerOp>(opcode = 0, size = ProtSize.VarByte) { out ->
        out.writeByte(0) // padding
        out.writeRSString(text ?: "null")
        out.writeByte(slot + 0x80) // slot offset (0x83=Follow for slot 3, etc.)
        out.writeByte(if (priority) 0xFF else 0x00)
        out.writeByte(if (priority) 0x7F else 0x00)
    }

    // HASHED_WORLD_TOKEN (6, varByte) — session nonce for world connection.
    serverProt<HashedWorldToken>(opcode = 6, size = ProtSize.VarByte) { out ->
        out.writeRSString(token)
    }

    // REBUILD_NORMAL (172, varShort) — builds the map scene.
    // 947 format from handler at 0x002144e0:
    //   mapSize(1B) + chunkX(2B) + chunkZ(2B) + forceRefresh(1B) + xteas(16B per region)
    serverProt<RebuildNormal>(opcode = 172, size = ProtSize.VarShort) { out ->
        out.writeByte(mapSize)
        out.writeShort(chunkX)
        out.writeShort(chunkZ)
        out.writeBoolean(forceRefresh)
        for (keys in xteaKeys) {
            for (key in keys) {
                out.writeInt(key)
            }
        }
    }
}
