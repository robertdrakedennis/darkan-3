package org.darkan.core.net.prot.revision.rev946

import org.darkan.core.net.prot.*

/**
 * All 130 ClientProt opcodes (0-129) for build 946.
 * Sizes verified from rs2client binary at jag::ClientProt::RegisterAll.
 *
 * Packets with actual handlers: Ping (15, 80), RequestWorldList (110).
 * All others registered as UnhandledClientProt (skipped by Session.readPackets).
 */
internal fun Codec.registerRev946ClientProts() {
    // --- Handled packets ---
    clientProt<Ping>(opcodes = intArrayOf(15, 80), size = 0)

    clientProt<RequestWorldList>(opcode = 110, size = 4) {
        RequestWorldList(worldlistVersion = readInt())
    }

    // --- Unhandled packets (size-only, for framing) ---
    // Each entry preserves the RE-verified opcode and size from the old ClientProt enum.
    clientProt<UnhandledClientProt>(opcode = 0, size = ProtSize.VarShort)  // EVENT_APPLET_FOCUS
    clientProt<UnhandledClientProt>(opcode = 1, size = 9)                  // EVENT_CAMERA_POSITION
    clientProt<UnhandledClientProt>(opcode = 2, size = 6)                  // CAMERA_DIRECTION
    clientProt<UnhandledClientProt>(opcode = 3, size = ProtSize.VarShort)  // VERIFIED_STRING_SEND
    clientProt<UnhandledClientProt>(opcode = 4, size = ProtSize.VarByte)   // MESSAGE_PUBLIC_EFFECTS
    clientProt<UnhandledClientProt>(opcode = 5, size = 15)                 // OPNPC_T_LONG
    clientProt<UnhandledClientProt>(opcode = 6, size = 3)                  // OPOBJ5
    clientProt<UnhandledClientProt>(opcode = 7, size = ProtSize.VarShort)  // RESUME_COUNTDIALOG
    clientProt<UnhandledClientProt>(opcode = 8, size = 4)                  // STRTOL_SEND
    clientProt<UnhandledClientProt>(opcode = 9, size = 3)                  // OPNPC_T1
    clientProt<UnhandledClientProt>(opcode = 10, size = ProtSize.VarShort) // RESUME_PAUSEBUTTON
    clientProt<UnhandledClientProt>(opcode = 11, size = 4)                 // UNKNOWN_11
    clientProt<UnhandledClientProt>(opcode = 12, size = 16)                // IF_BUTTON_T
    clientProt<UnhandledClientProt>(opcode = 13, size = 2)                 // UNKNOWN_13
    clientProt<UnhandledClientProt>(opcode = 14, size = 3)                 // OPOBJ7
    // 15 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 16, size = 11)                // OPLOC_T_LONG
    clientProt<UnhandledClientProt>(opcode = 17, size = ProtSize.VarByte)  // EVENT_MOUSE_CLICK
    clientProt<UnhandledClientProt>(opcode = 18, size = 8)                 // IF_BUTTON5
    clientProt<UnhandledClientProt>(opcode = 19, size = 3)                 // OPNPC_T2
    clientProt<UnhandledClientProt>(opcode = 20, size = 3)                 // OPOBJ1
    clientProt<UnhandledClientProt>(opcode = 21, size = 0)                 // MAP_BUILD_COMPLETE
    clientProt<UnhandledClientProt>(opcode = 22, size = 1)                 // UNKNOWN_22
    clientProt<UnhandledClientProt>(opcode = 23, size = 7)                 // OPNPC3
    clientProt<UnhandledClientProt>(opcode = 24, size = ProtSize.VarByte)  // CLIENT_CHEAT
    clientProt<UnhandledClientProt>(opcode = 25, size = 7)                 // OPNPC2
    clientProt<UnhandledClientProt>(opcode = 26, size = 7)                 // OPNPC1
    clientProt<UnhandledClientProt>(opcode = 27, size = 12)                // OPLOC_T
    clientProt<UnhandledClientProt>(opcode = 28, size = ProtSize.VarByte)  // DEVICE_INFO
    clientProt<UnhandledClientProt>(opcode = 29, size = ProtSize.VarByte)  // MESSAGE_PUBLIC
    clientProt<UnhandledClientProt>(opcode = 30, size = 3)                 // OPOBJ10
    clientProt<UnhandledClientProt>(opcode = 31, size = ProtSize.VarShort) // EVENT_CAMERA_POSITION_2
    clientProt<UnhandledClientProt>(opcode = 32, size = ProtSize.VarByte)  // CS2_CALLBACK
    clientProt<UnhandledClientProt>(opcode = 33, size = 5)                 // MOVE_GAME_MINIMENU
    clientProt<UnhandledClientProt>(opcode = 34, size = 0)                 // QUEUED_PACKET
    clientProt<UnhandledClientProt>(opcode = 35, size = 3)                 // OPNPC_T5
    clientProt<UnhandledClientProt>(opcode = 36, size = 3)                 // OPNPC_T6
    clientProt<UnhandledClientProt>(opcode = 37, size = 15)                // OPLOC_T_EXTENDED
    clientProt<UnhandledClientProt>(opcode = 38, size = 9)                 // OPLOC_T1
    clientProt<UnhandledClientProt>(opcode = 39, size = ProtSize.VarByte)  // SOCIAL_REQUEST
    clientProt<UnhandledClientProt>(opcode = 40, size = 9)                 // OPLOC_T3
    clientProt<UnhandledClientProt>(opcode = 41, size = 0)                 // UNKNOWN_41
    clientProt<UnhandledClientProt>(opcode = 42, size = 3)                 // UNKNOWN_3BYTE_42
    clientProt<UnhandledClientProt>(opcode = 43, size = 9)                 // OPLOC_T6
    clientProt<UnhandledClientProt>(opcode = 44, size = ProtSize.VarShort) // UNKNOWN_44
    clientProt<UnhandledClientProt>(opcode = 45, size = ProtSize.VarShort) // EVENT_TELEMETRY
    clientProt<UnhandledClientProt>(opcode = 46, size = 3)                 // OPOBJ2
    clientProt<UnhandledClientProt>(opcode = 47, size = 8)                 // IF_BUTTON10
    clientProt<UnhandledClientProt>(opcode = 48, size = ProtSize.VarByte)  // EVENT_KEYBOARD
    clientProt<UnhandledClientProt>(opcode = 49, size = ProtSize.VarShort) // DATA_REPORT_VARSHORT
    clientProt<UnhandledClientProt>(opcode = 50, size = 4)                 // SCENE_GRAPH_REPORT
    clientProt<UnhandledClientProt>(opcode = 51, size = 4)                 // CAMERA_ANGLE
    clientProt<UnhandledClientProt>(opcode = 52, size = ProtSize.VarShort) // MESSAGE_PRIVATE
    clientProt<UnhandledClientProt>(opcode = 53, size = 9)                 // UNKNOWN_53
    clientProt<UnhandledClientProt>(opcode = 54, size = 8)                 // IF_BUTTON3
    clientProt<UnhandledClientProt>(opcode = 55, size = 1)                 // UNKNOWN_55
    clientProt<UnhandledClientProt>(opcode = 56, size = ProtSize.VarByte)  // RESUME_NAMEDIALOG
    clientProt<UnhandledClientProt>(opcode = 57, size = 9)                 // UNKNOWN_57
    clientProt<UnhandledClientProt>(opcode = 58, size = 17)                // OPPLAYER_T_EXTENDED
    clientProt<UnhandledClientProt>(opcode = 59, size = 3)                 // OPOBJ8
    clientProt<UnhandledClientProt>(opcode = 60, size = 3)                 // OPOBJ6
    clientProt<UnhandledClientProt>(opcode = 61, size = 8)                 // IF_BUTTON7
    clientProt<UnhandledClientProt>(opcode = 62, size = ProtSize.VarByte)  // ENCODEDSTRING_SEND
    clientProt<UnhandledClientProt>(opcode = 63, size = 8)                 // IF_BUTTON9
    clientProt<UnhandledClientProt>(opcode = 64, size = 8)                 // IF_BUTTON6
    clientProt<UnhandledClientProt>(opcode = 65, size = 4)                 // SCENE_INTERACTION
    clientProt<UnhandledClientProt>(opcode = 66, size = 4)                 // EVENT_APPLET_FOCUS_2
    clientProt<UnhandledClientProt>(opcode = 67, size = ProtSize.VarByte)  // CLAN_JOINCHAT
    clientProt<UnhandledClientProt>(opcode = 68, size = 9)                 // OPLOC2_T
    clientProt<UnhandledClientProt>(opcode = 69, size = 3)                 // OPNPC4_T
    clientProt<UnhandledClientProt>(opcode = 70, size = 4)                 // OPLOC1
    clientProt<UnhandledClientProt>(opcode = 71, size = ProtSize.VarByte)  // ACTIVE_CHAT_PHRASE_SEND
    clientProt<UnhandledClientProt>(opcode = 72, size = 1)                 // UNKNOWN_72
    clientProt<UnhandledClientProt>(opcode = 73, size = 18)                // UNKNOWN_73
    clientProt<UnhandledClientProt>(opcode = 74, size = ProtSize.VarShort) // ENCRYPTED_STRING_SEND2
    clientProt<UnhandledClientProt>(opcode = 75, size = ProtSize.VarShort) // IF_BUTTON_TARGETMENU
    clientProt<UnhandledClientProt>(opcode = 76, size = 2)                 // SOUND_SONGEND
    clientProt<UnhandledClientProt>(opcode = 77, size = 7)                 // OPNPC5
    clientProt<UnhandledClientProt>(opcode = 78, size = ProtSize.VarByte)  // FRIENDLIST_ADD
    clientProt<UnhandledClientProt>(opcode = 79, size = 1)                 // UNKNOWN_79
    // 80 = Ping (registered above)
    clientProt<UnhandledClientProt>(opcode = 81, size = ProtSize.VarByte)  // ACTIVE_CHAT_PHRASE_SENDPRIVATE
    clientProt<UnhandledClientProt>(opcode = 82, size = 3)                 // WINDOW_STATUS
    clientProt<UnhandledClientProt>(opcode = 83, size = 1)                 // FOCUS_CHANGED
    clientProt<UnhandledClientProt>(opcode = 84, size = ProtSize.VarByte)  // OPOBJ_CS2_2
    clientProt<UnhandledClientProt>(opcode = 85, size = 7)                 // EVENT_MOUSE_MOVE
    clientProt<UnhandledClientProt>(opcode = 86, size = ProtSize.VarByte)  // MESSAGE_CLAN_CHAT
    clientProt<UnhandledClientProt>(opcode = 87, size = 0)                 // CLOSE_MODAL
    clientProt<UnhandledClientProt>(opcode = 88, size = 2)                 // SOUND_SONGSELECT
    clientProt<UnhandledClientProt>(opcode = 89, size = 5)                 // MOVE_SCRIPTED
    clientProt<UnhandledClientProt>(opcode = 90, size = 7)                 // OPNPC4
    clientProt<UnhandledClientProt>(opcode = 91, size = 3)                 // OPOBJ9
    clientProt<UnhandledClientProt>(opcode = 92, size = 18)                // MOVE_GAME_EXTENDED
    clientProt<UnhandledClientProt>(opcode = 93, size = ProtSize.VarByte)  // CLAN_LEAVECHAT
    clientProt<UnhandledClientProt>(opcode = 94, size = ProtSize.VarByte)  // OPLOC_CS2
    clientProt<UnhandledClientProt>(opcode = 95, size = 4)                 // DETECT_MODIFIED_CLIENT
    clientProt<UnhandledClientProt>(opcode = 96, size = 3)                 // OPOBJ4
    clientProt<UnhandledClientProt>(opcode = 97, size = 8)                 // IF_BUTTON1
    clientProt<UnhandledClientProt>(opcode = 98, size = ProtSize.VarByte)  // OPOBJ_CS2
    clientProt<UnhandledClientProt>(opcode = 99, size = 2)                 // AFFINEDTRANSFORM_SET
    clientProt<UnhandledClientProt>(opcode = 100, size = 4)                // UNKNOWN_100
    clientProt<UnhandledClientProt>(opcode = 101, size = 9)                // OPLOC4_T
    clientProt<UnhandledClientProt>(opcode = 102, size = ProtSize.VarShort) // MOVE_GAME
    clientProt<UnhandledClientProt>(opcode = 103, size = 7)                // OPNPC6
    clientProt<UnhandledClientProt>(opcode = 104, size = ProtSize.VarByte) // OPNPC_CS2
    clientProt<UnhandledClientProt>(opcode = 105, size = 11)               // OPOBJ_T
    clientProt<UnhandledClientProt>(opcode = 106, size = 6)                // DISPLAY_INFO
    clientProt<UnhandledClientProt>(opcode = 107, size = ProtSize.VarByte) // IF_BUTTONT
    clientProt<UnhandledClientProt>(opcode = 108, size = ProtSize.VarShort) // UNKNOWN_108
    clientProt<UnhandledClientProt>(opcode = 109, size = ProtSize.VarByte) // IGNORELIST_ADD
    // 110 = RequestWorldList (registered above)
    clientProt<UnhandledClientProt>(opcode = 111, size = 9)                // OPLOC5_T
    clientProt<UnhandledClientProt>(opcode = 112, size = ProtSize.VarByte) // UNKNOWN_112
    clientProt<UnhandledClientProt>(opcode = 113, size = 4)                // RENDER_REPORT
    clientProt<UnhandledClientProt>(opcode = 114, size = 22)               // IF_BUTTON_TARGETMENU_SEND
    clientProt<UnhandledClientProt>(opcode = 115, size = 3)                // OPOBJ3
    clientProt<UnhandledClientProt>(opcode = 116, size = ProtSize.VarByte) // ENCRYPTED_STRING_SEND
    clientProt<UnhandledClientProt>(opcode = 117, size = 6)                // CLOSE_MODAL_COMPONENT
    clientProt<UnhandledClientProt>(opcode = 118, size = 8)                // IF_BUTTON2
    clientProt<UnhandledClientProt>(opcode = 119, size = ProtSize.VarByte) // OPPLAYER_CS2
    clientProt<UnhandledClientProt>(opcode = 120, size = 11)               // OPPLAYER_T
    clientProt<UnhandledClientProt>(opcode = 121, size = ProtSize.VarByte) // FRIENDLIST_DEL
    clientProt<UnhandledClientProt>(opcode = 122, size = 16)               // INTERFACE_INTERACTION
    clientProt<UnhandledClientProt>(opcode = 123, size = 1)                // BUG_REPORT
    clientProt<UnhandledClientProt>(opcode = 124, size = 8)                // IF_BUTTON8
    clientProt<UnhandledClientProt>(opcode = 125, size = 3)                // OPNPC3_T
    clientProt<UnhandledClientProt>(opcode = 126, size = ProtSize.VarByte) // UNKNOWN_126
    clientProt<UnhandledClientProt>(opcode = 127, size = ProtSize.VarByte) // ENCODEDSTRING_SEND2
    clientProt<UnhandledClientProt>(opcode = 128, size = 8)                // IF_BUTTON4
    clientProt<UnhandledClientProt>(opcode = 129, size = 8)                // STRTOLL_SEND
}
