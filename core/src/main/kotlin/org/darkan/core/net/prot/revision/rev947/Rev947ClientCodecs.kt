package org.darkan.core.net.prot.revision.rev947

import kotlinx.io.readByteArray
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.readRSString

/**
 * All 130 ClientProt opcodes (0-129) for build 947-1.
 * Sizes extracted from rs2client.947-1 binary at jag::ClientProt::RegisterAll (0x00181e10).
 *
 * Packets with actual handlers: Ping (TBD), RequestWorldList (23).
 * All others registered as UnhandledClientProt (skipped by Session.readPackets).
 */
internal fun Codec.registerRev947ClientProts() {
    // --- Handled packets ---
    // NO_TIMEOUT opcodes: among 0-size entries {27, 41, 43, 71, 128}
    // TODO: Update once RE confirms exact NO_TIMEOUT opcodes in 947
    clientProt<Ping>(opcodes = intArrayOf(27, 41, 43, 71, 128), size = 0)

    // WORLDLIST_FETCH: CONFIRMED at opcode 23, size 4
    clientProt<RequestWorldList>(opcode = 23, size = 4) {
        RequestWorldList(worldlistVersion = readInt())
    }

    // --- Social packets (capture-confirmed opcodes) ---
    clientProt<FriendListDel>(opcode = 22, size = ProtSize.VarByte) {
        FriendListDel(displayName = readRSString())
    }
    clientProt<IgnoreListAdd>(opcode = 48, size = ProtSize.VarByte) {
        IgnoreListAdd(displayName = readRSString())
    }
    clientProt<FriendListAdd>(opcode = 93, size = ProtSize.VarByte) {
        FriendListAdd(displayName = readRSString())
    }

    // --- Unhandled packets (size-only, for framing) ---
    // 947-1 opcodes and sizes from RE extraction.
    clientProt<UnhandledClientProt>(opcode = 0, size = 5)
    clientProt<UnhandledClientProt>(opcode = 1, size = 3)
    clientProt<UnhandledClientProt>(opcode = 2, size = 4)
    clientProt<UnhandledClientProt>(opcode = 3, size = 5)
    clientProt<UnhandledClientProt>(opcode = 4, size = 8)
    clientProt<UnhandledClientProt>(opcode = 5, size = 8)
    clientProt<UnhandledClientProt>(opcode = 6, size = 15)
    clientProt<UnhandledClientProt>(opcode = 7, size = 6)
    clientProt<UnhandledClientProt>(opcode = 8, size = 4)
    clientProt<UnhandledClientProt>(opcode = 9, size = 12)
    clientProt<UnhandledClientProt>(opcode = 10, size = 9)
    clientProt<UnhandledClientProt>(opcode = 11, size = 9)
    clientProt<UnhandledClientProt>(opcode = 12, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 13, size = 16)
    clientProt<UnhandledClientProt>(opcode = 14, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 15, size = 6)
    clientProt<UnhandledClientProt>(opcode = 16, size = 8)
    clientProt<UnhandledClientProt>(opcode = 17, size = 7)
    clientProt<UnhandledClientProt>(opcode = 18, size = 8)
    clientProt<UnhandledClientProt>(opcode = 19, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 20, size = 1)
    clientProt<UnhandledClientProt>(opcode = 21, size = 8)
    // 22 = FriendListDel (registered above)
    // 23 = RequestWorldList (registered above)
    // CLANCHANNEL_KICKUSER: opcode 24, varByte — kick user from clan/friends channel
    clientProt<ClanChannelKickUser>(opcode = 24, size = ProtSize.VarByte) {
        ClanChannelKickUser(username = readRSString())
    }
    clientProt<UnhandledClientProt>(opcode = 25, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 26, size = ProtSize.VarByte)
    // 27 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 28, size = 18)                 // MOVE_GAME_EXTENDED
    clientProt<UnhandledClientProt>(opcode = 29, size = 8)
    clientProt<UnhandledClientProt>(opcode = 30, size = 4)
    clientProt<UnhandledClientProt>(opcode = 31, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 32, size = 4)
    clientProt<UnhandledClientProt>(opcode = 33, size = 3)
    clientProt<UnhandledClientProt>(opcode = 34, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 35, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 36, size = 8)
    clientProt<UnhandledClientProt>(opcode = 37, size = 1)
    clientProt<UnhandledClientProt>(opcode = 38, size = 1)
    clientProt<UnhandledClientProt>(opcode = 39, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 40, size = 16)
    // 41 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 42, size = ProtSize.VarByte)
    // 43 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 44, size = 7)
    clientProt<UnhandledClientProt>(opcode = 45, size = 9)
    clientProt<UnhandledClientProt>(opcode = 46, size = 3)
    clientProt<UnhandledClientProt>(opcode = 47, size = 4)
    // 48 = IgnoreListAdd (registered above)
    clientProt<UnhandledClientProt>(opcode = 49, size = 17)
    clientProt<UnhandledClientProt>(opcode = 50, size = 3)
    clientProt<UnhandledClientProt>(opcode = 51, size = 8)
    clientProt<UnhandledClientProt>(opcode = 52, size = 1)
    clientProt<UnhandledClientProt>(opcode = 53, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 54, size = 3)
    clientProt<UnhandledClientProt>(opcode = 55, size = 0)                  // MAP_BUILD_COMPLETE
    clientProt<UnhandledClientProt>(opcode = 56, size = 1)
    clientProt<UnhandledClientProt>(opcode = 57, size = 1)
    clientProt<UnhandledClientProt>(opcode = 58, size = 15)
    clientProt<UnhandledClientProt>(opcode = 59, size = 4)                  // DETECT_MODIFIED_CLIENT
    clientProt<UnhandledClientProt>(opcode = 60, size = 3)
    clientProt<UnhandledClientProt>(opcode = 61, size = 4)
    clientProt<UnhandledClientProt>(opcode = 62, size = 2)
    clientProt<UnhandledClientProt>(opcode = 63, size = 4)
    clientProt<UnhandledClientProt>(opcode = 64, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 65, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 66, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 67, size = 7)
    clientProt<UnhandledClientProt>(opcode = 68, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 69, size = ProtSize.VarByte)   // MESSAGE_PUBLIC_EFFECTS
    clientProt<UnhandledClientProt>(opcode = 70, size = ProtSize.VarShort)
    // 71 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 72, size = 9)
    clientProt<UnhandledClientProt>(opcode = 73, size = 9)
    clientProt<UnhandledClientProt>(opcode = 74, size = ProtSize.VarShort)  // EVENT_TELEMETRY
    clientProt<UnhandledClientProt>(opcode = 75, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 76, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 77, size = 8)
    clientProt<UnhandledClientProt>(opcode = 78, size = ProtSize.VarShort)  // MOVE_GAME
    clientProt<UnhandledClientProt>(opcode = 79, size = 9)
    clientProt<UnhandledClientProt>(opcode = 80, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 81, size = 11)                 // OPLOC_T2
    clientProt<UnhandledClientProt>(opcode = 82, size = 4)
    clientProt<UnhandledClientProt>(opcode = 83, size = 18)
    // RESUME_P_NAMEDIALOG: opcode 84, varByte — typed display name from name dialog
    clientProt<ResumePNameDialog>(opcode = 84, size = ProtSize.VarByte) {
        ResumePNameDialog(name = readRSString())
    }
    clientProt<UnhandledClientProt>(opcode = 85, size = 11)
    clientProt<UnhandledClientProt>(opcode = 86, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 87, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 88, size = 3)
    clientProt<UnhandledClientProt>(opcode = 89, size = 3)
    clientProt<UnhandledClientProt>(opcode = 90, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 91, size = 2)
    clientProt<UnhandledClientProt>(opcode = 92, size = 22)
    // 93 = FriendListAdd (registered above)
    clientProt<UnhandledClientProt>(opcode = 94, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 95, size = 8)
    // IF_BUTTON1: opcode 96, 8B fixed — first button click on interface component
    clientProt<IfButton>(opcode = 96, size = 8) {
        val interfaceHash = readInt()
        val slotId = readShort().toInt() and 0xFFFF
        val itemId = readShort().toInt() and 0xFFFF
        IfButton(buttonId = 1, interfaceHash = interfaceHash, slotId = slotId, itemId = itemId)
    }
    clientProt<UnhandledClientProt>(opcode = 97, size = 9)
    clientProt<UnhandledClientProt>(opcode = 98, size = 7)
    clientProt<UnhandledClientProt>(opcode = 99, size = 2)
    clientProt<UnhandledClientProt>(opcode = 100, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 101, size = 2)
    clientProt<UnhandledClientProt>(opcode = 102, size = 7)
    clientProt<UnhandledClientProt>(opcode = 103, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 104, size = 3)
    clientProt<UnhandledClientProt>(opcode = 105, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 106, size = 4)
    clientProt<UnhandledClientProt>(opcode = 107, size = 3)
    clientProt<UnhandledClientProt>(opcode = 108, size = 6)                 // WINDOW_STATUS
    clientProt<UnhandledClientProt>(opcode = 109, size = 9)
    clientProt<UnhandledClientProt>(opcode = 110, size = 3)
    clientProt<UnhandledClientProt>(opcode = 111, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 112, size = 11)
    clientProt<UnhandledClientProt>(opcode = 113, size = 3)
    clientProt<UnhandledClientProt>(opcode = 114, size = 3)
    clientProt<UnhandledClientProt>(opcode = 115, size = 3)
    clientProt<UnhandledClientProt>(opcode = 116, size = ProtSize.VarShort)
    clientProt<UnhandledClientProt>(opcode = 117, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 118, size = 7)
    clientProt<UnhandledClientProt>(opcode = 119, size = ProtSize.VarByte)
    // MESSAGE_PUBLIC: opcode 120, varByte — send public chat message
    clientProt<MessagePublicSend>(
        opcodes = intArrayOf(120),
        size = ProtSize.VarByte
    ) { packetSize ->
        val color = readByte().toInt() and 0xFF
        val effect = readByte().toInt() and 0xFF
        val message = readByteArray(packetSize - 2)
        MessagePublicSend(color = color, effect = effect, message = message)
    }
    // MESSAGE_PRIVATE: opcode 121, varShort — send private message to another player
    clientProt<MessagePrivateSend>(
        opcodes = intArrayOf(121),
        size = ProtSize.VarShort
    ) { packetSize ->
        val toDisplayName = readRSString()
        // Remaining bytes after the null-terminated string are the compressed message
        val nameLen = toDisplayName.length + 1  // +1 for null terminator
        val message = readByteArray(packetSize - nameLen)
        MessagePrivateSend(toDisplayName = toDisplayName, message = message)
    }
    clientProt<UnhandledClientProt>(opcode = 122, size = 7)
    clientProt<UnhandledClientProt>(opcode = 123, size = 3)
    clientProt<UnhandledClientProt>(opcode = 124, size = ProtSize.VarByte)
    clientProt<UnhandledClientProt>(opcode = 125, size = 3)
    clientProt<UnhandledClientProt>(opcode = 126, size = 9)
    clientProt<UnhandledClientProt>(opcode = 127, size = 3)
    // 128 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 129, size = 3)
}
