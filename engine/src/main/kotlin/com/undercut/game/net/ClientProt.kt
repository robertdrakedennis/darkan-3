package com.undercut.game.net

/**
 * All 130 client-to-server protocol opcodes (Build 948-2).
 * Regenerated from Phase 4a extraction at `re-resources/948-2-phase4a/clientprot_948-2_phase4a.csv`.
 *
 * Source: `jag::ClientProt::RegisterAll @ 0x000e6170` (130 entries, opcodes 0x00-0x81).
 *
 * Names match the CSV's emitter-name column where it's a recognizable `Send*`/named symbol.
 * `FUN_*` and `UNBOUND` entries are exposed as `UNKNOWN_<dec>` — the opcode + size is correct,
 * only the semantic label is missing.
 *
 * Note on shared emitters (single function emits multiple opcodes via switch):
 *   - `FUN_0026b900` emits the NPC long-form options 1-6 (opcodes 0x18,0x65,0x40,0x24,0x06,0x70)
 *   - `FUN_002d9970` emits the NPC short-form options 1-10 (opcodes 0x28,0x49,0x2f,0x21,0x6c,0x38,0x5b,0x32,0x16,0x27)
 *   - `FUN_0015c100` emits Loc-short options 1-6 (opcodes 0x1f,0x20,0x0b,0x71,0x3f,0x79)
 *   - `SendOpLocTLong` else-branch emits 6 long-form options (0x02,0x29,0x6d,0x6f,0x7d,0x7e); 17B if-branch is 0x12
 *   - `SendMultiDisplayPackets` emits opcodes 0x08,0x0c,0x6a,0x52,0x69
 *
 * @param opcode Wire opcode (0-129 / 0x00-0x81)
 * @param size Fixed payload size, -1 = var_byte, -2 = var_short, 0 = no payload
 * @param category Functional grouping for filtering
 */
enum class ClientProt(val opcode: Int, val size: Int, val category: Category) {
    UNKNOWN_0(0x00, -1, Category.MISC),                          // FUN_00280630 — VerifiedStringSend-style
    OP_NPC_CS2(0x01, -1, Category.ACTIONS),                      // SendOpNpcCS2
    OP_LOC_T_LONG_2(0x02, 9, Category.ACTIONS),                  // wrapper → SendOpLocTLong else-branch
    ANTI_CHEAT_CHALLENGE_RESPONSE(0x03, 9, Category.MISC),       // HandleAntiCheatChallenge response
    OP_PLAYER_CS2(0x04, -1, Category.ACTIONS),                   // SendOpPlayerCS2
    SCENE_GRAPH_REPORT(0x05, 4, Category.MISC),                  // SendSceneGraphReport
    OP_NPC_5(0x06, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 5
    UNBOUND_7(0x07, 4, Category.MISC),                           // UNBOUND
    MULTI_DISPLAY_8(0x08, 4, Category.MISC),                     // SendMultiDisplayPackets variant
    UNKNOWN_9(0x09, 12, Category.MISC),                          // FUN_0015b5c0
    UNKNOWN_10(0x0a, 16, Category.MISC),                         // FUN_002b7460
    OP_SHORT_3(0x0b, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 3
    MULTI_DISPLAY_12(0x0c, -1, Category.MISC),                   // SendMultiDisplayPackets variant
    UNBOUND_13(0x0d, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    NO_TIMEOUT(0x0e, 0, Category.MISC),                          // SendNoTimeout (keepalive)
    EVENT_MOUSE_CLICK(0x0f, 6, Category.MISC),                   // SendEventMouseClick
    MINIMENU_HOVER(0x10, 6, Category.ACTIONS),                   // FUN_00295700
    MOVE_SCRIPTED(0x11, 5, Category.MOVEMENT),                   // FUN_002a3a70
    OP_LOC_T_LONG(0x12, 17, Category.ACTIONS),                   // SendOpLocTLong (if-branch, 17B)
    UNBOUND_19(0x13, 9, Category.MISC),                          // UNBOUND
    OP_OBJ_CS2_2(0x14, -1, Category.ACTIONS),                    // SendOpObjCS2_2
    UNBOUND_21(0x15, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    OP_NPC_T_9(0x16, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 9
    UNBOUND_23(0x17, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    OP_NPC_1(0x18, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 1 (default)
    IF_BUTTON_X_INLINE(0x19, 11, Category.INTERFACES),           // FUN_00aeeb60
    RESUME_COUNTDIALOG(0x1a, -2, Category.DIALOG),               // SendResumeCountDialog
    UNBOUND_27(0x1b, 1, Category.MISC),                          // UNBOUND
    OP_LOC_3D_VIEW(0x1c, 4, Category.ACTIONS),                   // DoOpLoc (3D view variant)
    PING_REPORT(0x1d, 4, Category.MISC),                         // FUN_001fc8c0
    UNBOUND_30(0x1e, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    OP_SHORT_1(0x1f, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 1
    OP_SHORT_2(0x20, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 2
    OP_NPC_T_4(0x21, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 4
    UNBOUND_34(0x22, 15, Category.MISC),                         // UNBOUND
    IF_BUTTON_X(0x23, -1, Category.INTERFACES),                  // IfButtonXInner / IF_BUTTON_T
    OP_NPC_4(0x24, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 4
    QUEUED_PACKET(0x25, 0, Category.MISC),                       // SendQueuedPacket (flush marker)
    MESSAGE_PRIVATE(0x26, -2, Category.CHAT),                    // SendMessagePrivate
    OP_NPC_T_10(0x27, 3, Category.ACTIONS),                      // FUN_002d9970 NPC short-form case 10
    OP_NPC_T_1(0x28, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 1
    OP_LOC_T_LONG_29(0x29, 9, Category.ACTIONS),                 // wrapper → SendOpLocTLong else-branch
    STRING_SEND_42(0x2a, -1, Category.CHAT),                     // FUN_002d04b0
    UNBOUND_43(0x2b, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    STRTOL_SEND(0x2c, 4, Category.CHAT),                         // SendStrtol
    UNBOUND_45(0x2d, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    UNBOUND_46(0x2e, 1, Category.MISC),                          // UNBOUND
    OP_NPC_T_3(0x2f, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 3
    UNKNOWN_48(0x30, -1, Category.MISC),                         // FUN_00172900 — CSV ref is a getter, real emitter unknown
    OP_OBJ_CS2(0x31, -1, Category.ACTIONS),                      // SendOpObjCS2
    OP_NPC_T_8(0x32, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 8
    KEEPALIVE_NUDGE(0x33, 0, Category.MISC),                     // ConnectionManager::ProcessConnections — 50-tick keepalive
    DISPLAY_INFO(0x34, 6, Category.MISC),                        // SendDisplayInfo
    UNKNOWN_53(0x35, 15, Category.ACTIONS),                      // FUN_0015bcf0
    WORLDLIST_FETCH(0x36, 4, Category.MISC),                     // SendWorldlistFetch
    UNKNOWN_55(0x37, 11, Category.ACTIONS),                      // FUN_0015bad0
    OP_NPC_T_6(0x38, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 6
    AFFINED_TRANSFORM_SET(0x39, 2, Category.MISC),               // SendAffinedTransformSet_Main
    MOVE_GAME(0x3a, -2, Category.MOVEMENT),                      // SendMoveGame
    DETECT_MODIFIED_CLIENT(0x3b, 4, Category.MISC),              // SendDetectModifiedClient
    STRING_SEND_60(0x3c, -1, Category.CHAT),                     // FUN_002d02c0
    UNKNOWN_61(0x3d, 16, Category.ACTIONS),                      // FUN_0014edf0
    UNBOUND_62(0x3e, 1, Category.MISC),                          // UNBOUND
    OP_SHORT_5(0x3f, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 5
    OP_NPC_3(0x40, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 3
    RENDER_REPORT(0x41, 4, Category.MISC),                       // FUN_002383e0
    CLOSE_MODAL(0x42, 0, Category.MISC),                         // SendCloseModal
    UNBOUND_67(0x43, 18, Category.MISC),                         // UNBOUND
    UNBOUND_68(0x44, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    SOUND_SONGEND(0x45, 2, Category.MISC),                       // FUN_002d03d0
    FRIENDLIST_DEL(0x46, -1, Category.SOCIAL),                   // SendFriendlistDel
    UNBOUND_71(0x47, -1, Category.MISC),                         // UNBOUND
    RESUME_NAMEDIALOG(0x48, -2, Category.DIALOG),                // SendResumeNameDialog
    UNBOUND_73(0x49, 3, Category.MISC),                          // UNBOUND
    OP_LOC_SIMPLE(0x4a, 5, Category.ACTIONS),                    // FUN_0015b7d0 else-branch (CSV mis-attributes to FUN_002d9970)
    UNKNOWN_75(0x4b, -1, Category.MISC),                         // FUN_0015b7d0 — CSV anomalous
    ENCRYPTED_STRING_SEND2(0x4c, 4, Category.CHAT),              // SendEncodedString2
    EVENT_APPLET_FOCUS_SINGLE(0x4d, 9, Category.MISC),           // SendEventAppletFocus
    UNBOUND_78(0x4e, 18, Category.MISC),                         // UNBOUND
    UNKNOWN_79(0x4f, 1, Category.MISC),                          // FUN_0015b7d0 — CSV anomalous
    UNBOUND_80(0x50, -1, Category.MISC),                         // UNBOUND
    IGNORELIST_ADD(0x51, -2, Category.SOCIAL),                   // SendIgnorelistAdd
    MULTI_DISPLAY_82(0x52, -2, Category.MISC),                   // SendMultiDisplayPackets variant
    UNBOUND_83(0x53, -1, Category.MISC),                         // UNBOUND
    UNBOUND_84(0x54, 22, Category.MISC),                         // UNBOUND
    IF_BUTTON_TARGETMENU(0x55, -1, Category.INTERFACES),         // SendIfButtonTargetMenu
    ACTIVE_CHAT_PHRASE_SENDPRIVATE(0x56, 11, Category.CHAT),     // activechatphrase_sendprivate
    UNKNOWN_87(0x57, -1, Category.ACTIONS),                      // FUN_0015c100 — CSV size anomalous
    MESSAGE_PUBLIC_EFFECTS(0x58, -2, Category.CHAT),             // SendMessagePublicWithEffects
    DATA_REPORT(0x59, -1, Category.MISC),                        // SendDataReport
    SOCIAL_REQUEST(0x5a, -1, Category.SOCIAL),                   // SendSocialRequest
    OP_NPC_T_7(0x5b, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 7
    UNBOUND_92(0x5c, 8, Category.MISC),                          // UNBOUND_HASDATAREF
    VERIFIED_STRING_SEND(0x5d, -2, Category.CHAT),               // SendVerifiedStringSend
    WINDOW_STATUS(0x5e, 3, Category.MISC),                       // SendWindowStatus
    UNBOUND_95(0x5f, 4, Category.MISC),                          // UNBOUND
    UNBOUND_96(0x60, 0, Category.MISC),                          // UNBOUND
    STRING_SEND_97(0x61, -1, Category.CHAT),                     // FUN_002d06a0
    UNKNOWN_98(0x62, -1, Category.MISC),                         // FUN_001729b0
    ENCRYPTED_STRING_SEND(0x63, -1, Category.CHAT),              // FUN_002d0850 — tiny-key encrypted send
    FRIENDLIST_ADD(0x64, -1, Category.SOCIAL),                   // SendFriendlistAdd
    OP_NPC_2(0x65, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 2
    IF_BUTTON_TARGETMENU_STRING(0x66, -2, Category.INTERFACES),  // FUN_00298c30 — string-send target menu
    UNBOUND_103(0x67, 8, Category.MISC),                         // UNBOUND_HASDATAREF
    UNBOUND_104(0x68, 2, Category.MISC),                         // UNBOUND
    EVENT_APPLET_FOCUS(0x69, -2, Category.MISC),                 // SendAppletFocusEvents
    MULTI_DISPLAY_106(0x6a, 1, Category.MISC),                   // SendMultiDisplayPackets variant
    MAP_BUILD_COMPLETE(0x6b, 0, Category.MISC),                  // SendMapBuildComplete
    OP_NPC_T_5(0x6c, 3, Category.ACTIONS),                       // FUN_002d9970 NPC short-form case 5
    OP_LOC_T_LONG_109(0x6d, 9, Category.ACTIONS),                // wrapper → SendOpLocTLong else-branch
    EVENT_MOUSE_MOVE_BATCH(0x6e, 7, Category.MISC),              // FUN_001803b0 — drains mouse-move ring buffer
    OP_LOC_T_LONG_111(0x6f, 9, Category.ACTIONS),                // wrapper → SendOpLocTLong else-branch
    OP_NPC_6(0x70, 7, Category.ACTIONS),                         // FUN_0026b900 NPC long-form case 6
    OP_SHORT_4(0x71, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 4
    STRING_SEND_114(0x72, -1, Category.CHAT),                    // FUN_00269680 — flag + intByte + string
    UNKNOWN_115(0x73, 3, Category.MISC),                         // FUN_00269580
    SOUND_SONGSELECT(0x74, 2, Category.MISC),                    // FUN_002d05c0
    ENCRYPTED_STRING_SEND_117(0x75, -1, Category.CHAT),          // SendEncodedString
    ACTIVE_CHAT_PHRASE_SEND(0x76, -1, Category.CHAT),            // activechatphrase_send
    RESUME_PAUSEBUTTON(0x77, -2, Category.DIALOG),               // SendResumePauseButton
    STRTOLL_SEND(0x78, 8, Category.CHAT),                        // SendStrtoll
    OP_SHORT_6(0x79, 3, Category.ACTIONS),                       // FUN_0015c100 short loc case 6
    ENCRYPTED_STRING_SEND_122(0x7a, -2, Category.CHAT),          // FUN_002d1b10
    BUG_REPORT(0x7b, 1, Category.MISC),                          // SendBugReport
    MESSAGE_PUBLIC(0x7c, -1, Category.CHAT),                     // SendMessagePublic
    OP_LOC_T_LONG_125(0x7d, 9, Category.ACTIONS),                // wrapper → SendOpLocTLong else-branch
    OP_LOC_T_LONG_126(0x7e, 9, Category.ACTIONS),                // wrapper → SendOpLocTLong else-branch
    UNBOUND_127(0x7f, 8, Category.MISC),                         // UNBOUND_HASDATAREF
    UNBOUND_128(0x80, -2, Category.MISC),                        // UNBOUND
    OP_LOC_CS2(0x81, -1, Category.ACTIONS);                      // SendOpLocCS2

    enum class Category {
        MOVEMENT, ACTIONS, INTERFACES, CHAT, SOCIAL, DIALOG, MISC
    }

    companion object {
        private val BY_OPCODE = entries.associateBy { it.opcode }

        fun forOpcode(opcode: Int): ClientProt? = BY_OPCODE[opcode]
    }
}
