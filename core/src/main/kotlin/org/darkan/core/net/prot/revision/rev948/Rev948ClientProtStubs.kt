package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize

/**
 * Registers opcode metadata (name + size) for ALL 130 ClientProt opcodes in rev 948-2-2.
 * Opcodes that have real decoder registrations will NOT be overwritten (putIfAbsent).
 *
 * SIZES: 130/130 extracted directly from `jag::ClientProt::RegisterAll @ 0x000c45b0` in
 * rs2client.948-2-2 (each `ClientProt(&entry, opcode, size)` call). Authoritative.
 *
 * NAMES — THE LAW: every name is the OFFICIAL `jag::ClientProt::<NAME>` enum symbol harvested
 * from the beta unstripped binary (librs2client.so, the `jag::ClientProt` ProtEntry globals at
 * 0x00a392d8..0x00a410e8). A packet's name is NEVER the C++ function that builds it. The official
 * vocabulary is fixed; if a 948 opcode cannot be mapped to an official enum name with HIGH+
 * confidence it stays `UNKNOWN_<op>` (size + emitter addr + read-shape retained in the comment).
 *
 * METHOD: opcode N's ProtEntry base = &DAT_(0x015d3ea0 - idx*0x10) (idx skips 0x19 at 0x015bfb40).
 * `get_xrefs_to(entryBase)` gives the 948 emitter. The beta references each official name via
 * `MakeClientMessage<jag::ClientProt>(&buf, conn, &jag::ClientProt::<NAME>)`; the 948 emitter is
 * matched to the beta send-site by byte-layout + semantics to learn the official name.
 *
 * CORRECTIONS THIS PASS (function-name -> official enum name). All prior names that were actually
 * C++ FUNCTION names (Send*, Process*, *_impl, OP1..OP6, *_CS2, OPLOCT_SHORT_*, IF_BUTTON_CS2_*,
 * CS2_*, ENCODED_STRING*, etc.) were replaced with the official enum name where provable, else
 * reverted to UNKNOWN_<op>:
 *   51 NO_TIMEOUT          (was PROCESS_CONNECTIONS — the keepalive; ProcessConnections is the FN
 *                           that sends it. CERTAIN: beta ConnectionManager::MainLogic builds
 *                           &ClientProt::NO_TIMEOUT on the same 0x32-tick counter; 948
 *                           ProcessConnections @ 0x0013e6c0 is the byte-identical port, entry 0x015d3b80)
 *   14 ABORT_P_DIALOG      (was NO_TIMEOUT — WRONG. op14 is a size-0 dialog-abort, NOT the timer
 *                           keepalive. HIGH: 948 emitter @ 0x002d0040 is size-0, resets dialog
 *                           state field 0x57->0x17 & result->-3; beta Resume::Resume lambda#7
 *                           builds &ClientProt::ABORT_P_DIALOG, same size-0 active-conn send)
 *   6/24/36/64/101/112 OPOBJ5/1/4/3/2/6 (were OP5/OP1/OP4/OP3/OP2/OP6 — function placeholders.
 *                           HIGH: 948 FUN_0026b8e0=SendOpTargetOption_CS2 6-case dispatcher; 7-byte
 *                           tile-coord+id+flag layout matches beta DoOpObj OPOBJ1..6 family)
 *   26 RESUME_P_COUNTDIALOG (was RESUME_COUNT_DIALOG — wrong spelling; HIGH beta layout match)
 *   71 RESUME_P_NAMEDIALOG  (was RESUME_NAME_DIALOG — wrong spelling; HIGH exact beta match)
 *   35 IF_PLAYER            (was IF_BUTTON_X_INNER — function name. HIGH: 948 IfButtonXInner long
 *                           path entry 0x015d3c80=op35; beta IfButtonX references &ClientProt::IF_PLAYER)
 *   58 MOVE_GAMECLICK       (was MOVE_GAME — beta &ClientProt::MOVE_GAMECLICK, ThreeDView click-to-move)
 * Everything else that was a Send-prefixed function name and not provably mappable -> UNKNOWN_<op>.
 */
internal fun Codec.registerRev948ClientProtStubs() {
    c(0, "UNKNOWN_0", -1)                             // CONF:NONE — FUN_00280630, no entry-base xref identity
    c(1, "UNKNOWN_1", -1)                             // CONF:NONE — fn SendOpNpcCS2 @ 0x00311830 (entry 0x015d3e90); no official OPNPC*_CS2 enum, OPNPC1-6/OPNPCT are the only OPNPC official names — cannot pin which
    c(2, "UNKNOWN_2", 9)                              // CONF:NONE — shim FUN_0015c7b0 → SendOpLocTLong else-branch; no provable official name
    c(3, "UNKNOWN_3", 9)                              // CONF:NONE — fn HandleAntiCheatChallenge @ 0x001807a0 (entry 0x015d3e70); no matching official enum (no ANTI_CHEAT_* in vocab)
    c(4, "UNKNOWN_4", -1)                             // CONF:NONE — fn SendOpPlayerCS2 @ 0x0033df80 (entry 0x015d3e60); no official OPPLAYER*_CS2 enum
    c(5, "UNKNOWN_5", 4)                              // CONF:NONE — fn SendSceneGraphReport @ 0x00229d80; scene-graph rbtree maint + 4B int (p4 of param+0x10), entry 0x015d3e50; NO official enum maps (not FACE_SQUARE/EVENT_CAMERA_POSITION — wrong layout)
    c(6, "UNKNOWN_6", 7)                              // PROBABLE: OPOBJ5 — SendOpTargetOption_CS2 case->OPOBJ5; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a393a8). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(7, "UNKNOWN_7", 4)                              // CONF:NONE — UNBOUND (RegisterAll only)
    c(8, "UNKNOWN_8", 4)                              // CONF:NONE — fn SendMultiDisplayPackets @ 0x001a2d90 (entry 0x015d3e20); no MULTI_DISPLAY official enum
    c(9, "UNKNOWN_9", 12)                             // CONF:NONE — fn FUN_0015b5c0 use-item-on-loc; no official enum maps (OPLOCU is not in the beta vocab)
    c(10, "IF_BUTTOND", 16)                           // CONF:HIGH — beta InterfaceManager::UpdateDragging→&ClientProt::IF_BUTTOND; 16B 2-component drag (entry 0x015d3e10)
    c(11, "UNKNOWN_11", 3)                            // CONF:NONE — shim FUN_0015c3d0 → FUN_0015c100 else-branch (3B comp-target); no provable official name
    c(12, "UNKNOWN_12", -1)                           // CONF:NONE — fn SendMultiDisplayPackets @ 0x001a2d90 (entry 0x015d3de0); no MULTI_DISPLAY official enum
    c(13, "UNKNOWN_13", 8)                            // CONF:NONE — UNBOUND (CS2 data-table 0x01365960)
    c(14, "ABORT_P_DIALOG", 0)                        // CONF:HIGH — beta Resume::Resume lambda#7→&ClientProt::ABORT_P_DIALOG; 948 emitter @ 0x002d0040 size-0 dialog-abort (entry 0x015d3dc0). NOT the keepalive (that is op51).
    c(15, "EVENT_MOUSE_CLICK", 6)                     // CONF:HIGH — beta ClientWatch::MainLogic→&ClientProt::EVENT_MOUSE_CLICK; 948 SendEventMouseClick @ 0x001805b0 (entry 0x015d3db0)
    c(16, "UNKNOWN_16", 6)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-sanity VETO: no interfaceHash/slot/item). Only one IF_BUTTON1..10 set exists and it's owned by the size-8 clicks; this carries no official enum.
    c(17, "MOVE_SCRIPTED", 5)                         // CONF:CERTAIN — jag::opcode::movescripted @ 0x002a3a70 self-named; official enum
    c(18, "UNKNOWN_18", 17)                           // PROBABLE: OPLOCT — beta DoOpLoc->&OPLOCT long-form (beta 0x00a395d8); 948 SendOpLocTLong entry 0x015d3d80 (17B). Layout family-match but exact send-site not isolated -> stays UNKNOWN (strict).
    c(19, "UNKNOWN_19", 9)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(20, "UNKNOWN_20", -1)                           // CONF:NONE — fn SendOpObjCS2_2 @ 0x002a24c0 (entry 0x015d3d60); no official OPOBJ*_CS2 enum
    c(21, "IF_BUTTON10", 8)                           // CONFIRMED — beta 0x00a395c8 / 948 IfButtonXInner 0x002978d0, CS2-table opt10 slot 0x01365968->ProtEntry 0x015d3d50 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(22, "UNKNOWN_22", 3)                            // CONF:NONE — FUN_002d9970 IF_BUTTON family, entry not in 1-10 switch → generic 3B comp; no provable official name
    c(23, "UNKNOWN_23", 8)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(24, "UNKNOWN_24", 7)                            // PROBABLE: OPOBJ1 — SendOpTargetOption_CS2 case->OPOBJ1; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a39548). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(25, "UNKNOWN_25", 11)                           // CONF:NONE — FUN_00aeeb60 outlier entry 0x015bfb40; server-active-props transmit; no provable official name
    c(26, "RESUME_P_COUNTDIALOG", -2)                 // CONF:HIGH — 948 SendResumeCountDialog @ 0x002cfdb0 CS2 count-pair (entry 0x015d3d10); official enum
    c(27, "UNKNOWN_27", 1)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(28, "UNKNOWN_28", 4)                            // CONF:NONE — fn DoOpLoc family (entry 0x015d3cf0); no provable mapping to OPLOC1-6 case (dispatcher aliased to message-init helper)
    c(29, "PING_STATISTICS", 4)                       // CONF:CERTAIN — beta ProcessingWorldPing→&ClientProt::PING_STATISTICS; GetPing()+count layout (FUN_001fc8c0, entry 0x015d3ce0)
    c(30, "IF_BUTTON5", 8)                            // CONFIRMED — beta 0x00a394f0 / 948 IfButtonXInner 0x002978d0, CS2-table opt5 slot 0x01365940->ProtEntry 0x015d3cd0 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(31, "UNKNOWN_31", 3)                            // CONF:NONE — shim FUN_0015c450 → FUN_0015c100 else-branch (3B comp-target); no provable official name
    c(32, "UNKNOWN_32", 3)                            // CONF:NONE — shim FUN_0015c430 → SendOpLocTLong family else-branch; no provable official name
    c(33, "UNKNOWN_33", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(34, "UNKNOWN_34", 15)                           // CONF:NONE — UNBOUND (RegisterAll only)
    c(35, "IF_PLAYER", -1)                            // CONF:HIGH — 948 IfButtonXInner long path entry 0x015d3c80=op35; beta IfButtonX references &ClientProt::IF_PLAYER (use-button-on-player)
    c(36, "UNKNOWN_36", 7)                            // PROBABLE: OPOBJ4 — SendOpTargetOption_CS2 case->OPOBJ4; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a39368). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(37, "UNKNOWN_37", 0)                            // CONF:NONE — fn SendQueuedPacket @ 0x002d3870 (entry 0x015d3c60); no QUEUED_PACKET official enum
    c(38, "MESSAGE_PRIVATE", -2)                      // CONF:HIGH — beta &ClientProt::MESSAGE_PRIVATE; 948 SendMessagePrivate @ 0x00306dd0 tinyKeyEncrypt (entry 0x015d3c50); official enum
    c(39, "UNKNOWN_39", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(40, "UNKNOWN_40", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(41, "UNKNOWN_41", 9)                            // CONF:NONE — shim FUN_0015c7f0 → SendOpLocTLong else-branch (9B use-on-loc); no provable official name
    c(42, "UNKNOWN_42", -1)                           // CONF:NONE — FUN_002d04b0 CS2 string-stack type+value; no provable official name
    c(43, "IF_BUTTON7", 8)                            // CONFIRMED — beta 0x00a39380 / 948 IfButtonXInner 0x002978d0, CS2-table opt7 slot 0x01365950->ProtEntry 0x015d3c00 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(44, "UNKNOWN_44", 4)                            // CONF:NONE — fn SendStrtol @ 0x002d4d20 strtol(CS2 str)→4B int (entry 0x015d3bf0); no STRTOL official enum
    c(45, "IF_BUTTON4", 8)                            // CONFIRMED — beta 0x00a395c0 / 948 IfButtonXInner 0x002978d0, CS2-table opt4 slot 0x01365938->ProtEntry 0x015d3be0 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(46, "UNKNOWN_46", 1)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(47, "UNKNOWN_47", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(48, "UNKNOWN_48", -1)                           // CONF:NONE — FUN_00172900 returns entry only (getter stub), no payload writes
    c(49, "UNKNOWN_49", -1)                           // CONF:NONE — fn SendOpObjCS2 @ 0x0033ddc0 (entry 0x015d3ba0); no official OPOBJ*_CS2 enum
    c(50, "UNKNOWN_50", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(51, "NO_TIMEOUT", 0)                            // CONF:CERTAIN — the keepalive. beta ConnectionManager::MainLogic→&ClientProt::NO_TIMEOUT on 0x32-tick counter; 948 ProcessConnections @ 0x0013e6c0 byte-identical port (entry 0x015d3b80). Client floods this every ~1s.
    c(52, "UNKNOWN_52", 6)                            // CONF:NONE — fn SendDisplayInfo @ 0x001a2ba0 (entry 0x015d3b70); no DISPLAY_INFO official enum
    c(53, "UNKNOWN_53", 15)                           // CONF:NONE — FUN_0015bcf0 long-form use-on-loc (entry 0x015d3b60); no provable official name
    c(54, "WORLDLIST_FETCH", 4)                       // CONF:HIGH — fn SendWorldlistFetch @ 0x00194a80 (entry 0x015d3b50); official enum
    c(55, "UNKNOWN_55", 11)                           // CONF:NONE — FUN_0015bad0 11B drag/use-component variant; no provable official name
    c(56, "UNKNOWN_56", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(57, "UNKNOWN_57", 2)                            // CONF:NONE — fn SendAffinedTransformSet_Main @ 0x0033e1f0 (entry 0x015d3b20); no AFFINED_TRANSFORM* official enum (AFFINEDCLANSETTINGS_* are different)
    c(58, "MOVE_GAMECLICK", -2)                       // CONF:HIGH — beta minimenuactions::ThreeDView→&ClientProt::MOVE_GAMECLICK; 948 SendMoveGame @ 0x0026e1d0 (entry 0x015d3b10)
    c(59, "UNKNOWN_59", 4)                            // CONF:NONE — fn SendDetectModifiedClient @ 0x00480620 (entry 0x015d3b00); no DETECT_MODIFIED* official enum
    c(60, "UNKNOWN_60", -1)                           // CONF:NONE — FUN_002d02c0 CS2 string-stack type+value (entry 0x015d3af0); no provable official name
    c(61, "UNKNOWN_61", 16)                           // PROBABLE: IF_BUTTONT — beta InterfaceActions->&IF_BUTTONT (use-component-on-target, 16B; beta 0x00a39490); 948 SendIfButtonT entry 0x015d3ae0. Size-match; emitter not byte-diffed -> stays UNKNOWN (strict).
    c(62, "UNKNOWN_62", 1)                            // CONF:NONE — FUN_001729b0 returns entry only (getter stub)
    c(63, "UNKNOWN_63", 3)                            // CONF:NONE — shim FUN_0015c3f0 → FUN_0015c100 else-branch (3B comp-target); no provable official name
    c(64, "UNKNOWN_64", 7)                            // PROBABLE: OPOBJ3 — SendOpTargetOption_CS2 case->OPOBJ3; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a39458). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(65, "UNKNOWN_65", 4)                            // CONF:NONE — FUN_002383e0 packs 3 small fields WriteUInt32LE (camera/minimap angle); no provable official enum
    c(66, "CLOSE_MODAL", 0)                           // CONF:HIGH — beta CloseModalInterface→&ClientProt::CLOSE_MODAL; 948 SendCloseModal @ 0x002d01f0 (entry 0x015d3a90); official enum
    c(67, "UNKNOWN_67", 18)                           // CONF:NONE — UNBOUND (RegisterAll only)
    c(68, "IF_BUTTON6", 8)                            // CONFIRMED — beta 0x00a39580 / 948 IfButtonXInner 0x002978d0, CS2-table opt6 slot 0x01365948->ProtEntry 0x015d3a70 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(69, "UNKNOWN_69", 2)                            // CONF:NONE — FUN_002d03d0 pops one CS2 int, writes p2 (entry 0x015d3a60); no provable official name
    c(70, "FRIENDLIST_DEL", -1)                       // CONF:HIGH — fn SendFriendlistDel @ 0x0026b1f0 (entry 0x015d3a50); official enum
    c(71, "RESUME_P_NAMEDIALOG", -1)                  // CONF:HIGH — beta Resume::Resume lambda#4→&ClientProt::RESUME_P_NAMEDIALOG (length-prefixed UTF8); 948 @ 0x002cff20 (entry 0x015d3a40)
    c(72, "UNKNOWN_72", -2)                           // CONF:NONE — UNBOUND (RegisterAll only)
    c(73, "UNKNOWN_73", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(74, "UNKNOWN_74", 5)                            // PROBABLE: MOVE_MINIMAPCLICK — official enum; 948 FUN_0015b7d0 walk/move flag+tileX/Y sets map flag, entry 0x015d3a10. Single-fn lead, beta send-site not pinned -> stays UNKNOWN (strict).
    c(75, "UNKNOWN_75", -1)                           // CONF:NONE — FUN_0015b7d0 op-type-1 extended walk (entry 0x015d39d0/0x015d3a10); no distinct official name (MOVE_MINIMAPCLICK is op74)
    c(76, "UNKNOWN_76", 4)                            // CONF:NONE — fn SendEncodedString2 @ 0x0028ea60 (entry 0x015d39f0); no official enum maps
    c(77, "UNKNOWN_77", 9)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(78, "UNKNOWN_78", 18)                           // CONF:NONE — UNBOUND (RegisterAll only)
    c(79, "UNKNOWN_79", 1)                            // CONF:NONE — FUN_0015b7d0 shim, 1B flag-only walk variant; no distinct official name
    c(80, "IGNORELIST_ADD", -1)                       // CONF:HIGH — fn SendIgnorelistAdd @ 0x0030f4b0 + "ignore list is full" (entry 0x015d39b0); official enum
    c(81, "UNKNOWN_81", -2)                           // CONF:NONE — generic SendMultiDisplayPackets PARAM ref only
    c(82, "UNKNOWN_82", -2)                           // CONF:NONE — UNBOUND (entry unreferenced)
    c(83, "UNKNOWN_83", -1)                           // CONF:NONE — UNBOUND (RegisterAll only)
    c(84, "UNKNOWN_84", 22)                           // CONF:NONE — fn SendIfButtonTargetMenu @ 0x0029aae0 (entry 0x015d3970); no official IF_BUTTON_TARGET_MENU enum
    c(85, "UNKNOWN_85", -1)                           // CONF:NONE — fn jag::opcode::activechatphrase_sendprivate @ 0x00340850; no ACTIVECHATPHRASE* official enum (not in beta vocab) — likely MESSAGE_QUICKCHAT_PRIVATE but layout not yet confirmed
    c(86, "UNKNOWN_86", 11)                           // CONF:NONE — FUN_0015c100 entry 0x015d3950: flag+target+slot+hash+sub; no provable official name
    c(87, "UNKNOWN_87", -1)                           // CONF:NONE — fn SendMessagePublicWithEffects @ 0x0037c1c0 (entry 0x015d3940); no MESSAGE_PUBLIC_EFFECTS official enum (MESSAGE_PUBLIC is op124) — possibly MESSAGE_QUICKCHAT_PUBLIC, layout not confirmed
    c(88, "UNKNOWN_88", -2)                           // CONF:NONE — fn SendDataReport @ 0x00341230 (entry 0x015d3930); no DATA_REPORT official enum (SEND_SNAPSHOT/URL_REQUEST candidates unconfirmed)
    c(89, "CLANCHANNEL_KICKUSER", -1)                 // CONF:HIGH — fn SendSocialRequest @ 0x0026aef0 + "not in this channel" (entry 0x015d3920); beta &ClientProt::CLANCHANNEL_KICKUSER channel-kick
    c(90, "UNKNOWN_90", -1)                           // CONF:NONE — UNBOUND (entry unreferenced)
    c(91, "UNKNOWN_91", 3)                            // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(92, "IF_BUTTON3", 8)                            // CONFIRMED — beta 0x00a39598 / 948 IfButtonXInner 0x002978d0, CS2-table opt3 slot 0x01365930->ProtEntry 0x015d38f0 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(93, "UNKNOWN_93", -2)                           // CONF:NONE — fn SendVerifiedStringSend @ 0x00344600 (entry 0x015d38e0); no official enum maps
    c(94, "WINDOW_STATUS", 3)                         // CONF:HIGH — beta ClientWatch::SendWindowStatus→&ClientProt::WINDOW_STATUS; 948 @ 0x0033e470 (entry 0x015d38d0); official enum
    c(95, "UNKNOWN_95", 4)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(96, "UNKNOWN_96", 0)                            // CONF:NONE — UNBOUND (RegisterAll only)
    c(97, "UNKNOWN_97", -1)                           // CONF:NONE — FUN_002d06a0 CS2 string-stack type+value (entry 0x015d38a0); no provable official name
    c(98, "UNKNOWN_98", -1)                           // CONF:NONE — FUN_001729b0 returns entry only (getter stub)
    c(99, "UNKNOWN_99", -1)                           // CONF:NONE — FUN_002d0850 tinyKeyEncrypt string gated login state 0x17/0x1e (entry 0x015d3880); no provable official name
    c(100, "FRIENDLIST_ADD", -1)                      // CONF:HIGH — fn SendFriendlistAdd @ 0x0026a530 (entry 0x015d3870); official enum
    c(101, "UNKNOWN_101", 7)                          // PROBABLE: OPOBJ2 — SendOpTargetOption_CS2 case->OPOBJ2; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a39408). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(102, "UNKNOWN_102", -2)                         // CONF:NONE — FUN_00298c30 GetServerActiveProperties bit; CS2 value+component triple transmit; no provable official name
    c(103, "IF_BUTTON2", 8)                           // CONFIRMED — beta 0x00a393e0 / 948 IfButtonXInner 0x002978d0, CS2-table opt2 slot 0x01365928->ProtEntry 0x015d3840 (xref-verified). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(104, "UNKNOWN_104", 2)                          // CONF:NONE — UNBOUND (RegisterAll only)
    c(105, "EVENT_APPLET_FOCUS", -2)                  // CONF:HIGH — beta ClientWatch::MainLogic→&ClientProt::EVENT_APPLET_FOCUS (SDL_GetKeyboardFocus toggle); 948 SendAppletFocusEvents @ 0x00194f40 (entry 0x015d3820)
    c(106, "UNKNOWN_106", 1)                          // CONF:NONE — fn SendMultiDisplayPackets @ 0x001a2d90 (entry 0x015d3810); no MULTI_DISPLAY official enum
    c(107, "MAP_BUILD_COMPLETE", 0)                   // CONF:HIGH — fn SendMapBuildComplete @ 0x002bc030 (entry 0x015d3800); official enum
    c(108, "UNKNOWN_108", 3)                          // CONF:NONE — CS2 SendIfButtonN_CS2 @0x002d9970 component-press, not a click (size-3 flag+2B comp-subid; no interfaceHash/slot/item -> size-sanity VETO). No official enum (only one IF_BUTTON1..10 set, owned by the size-8 clicks).
    c(109, "UNKNOWN_109", 9)                          // CONF:NONE — shim FUN_0015c810 → SendOpLocTLong else-branch (9B use-on-loc); no provable official name
    c(110, "EVENT_MOUSE_MOVE", 7)                     // CONF:HIGH — beta ClientWatch::MainLogic→&ClientProt::EVENT_MOUSE_MOVE (ring buffer); 948 FUN_001803b0 (entry 0x015d37a0)
    c(111, "UNKNOWN_111", 9)                          // CONF:NONE — shim FUN_0015c830 → SendOpLocTLong else-branch (9B use-on-loc); no provable official name
    c(112, "UNKNOWN_112", 7)                          // PROBABLE: OPOBJ6 — SendOpTargetOption_CS2 case->OPOBJ6; beta DoOpObj OPOBJ family 7B tile-coord+id+flag (beta 0x00a39538). Case-to-N mapping inferred, not byte-diffed -> stays UNKNOWN (strict).
    c(113, "UNKNOWN_113", 3)                          // CONF:NONE — shim FUN_0015c410 → FUN_0015c100 else-branch (3B comp-target); no provable official name
    c(114, "UNKNOWN_114", -1)                         // CONF:NONE — FUN_00269680 CS2 type byte+1 + int + encoded value (entry 0x015d3790); no provable official name
    c(115, "UNKNOWN_115", 3)                          // CONF:NONE — FUN_00269580 writes flag byte + short (entry 0x015d3780); no provable official name
    c(116, "UNKNOWN_116", 2)                          // CONF:NONE — FUN_002d05c0 pops one CS2 int, writes p2 (entry 0x015d3770); no provable official name
    c(117, "UNKNOWN_117", -1)                         // CONF:NONE — fn SendEncodedString @ 0x00311130 CS2 type-prefixed encoded string (entry 0x015d3760); no official ENCODED_STRING enum
    c(118, "UNKNOWN_118", -1)                         // CONF:NONE — fn jag::opcode::activechatphrase_send @ 0x00340d50; no ACTIVECHATPHRASE* official enum — likely MESSAGE_QUICKCHAT_PUBLIC, layout not confirmed
    c(119, "RESUME_PAUSEBUTTON", -2)                  // CONF:CERTAIN — beta SendPauseComponentMessage→&ClientProt::RESUME_PAUSEBUTTON (entry 0x015d3740); official enum
    c(120, "UNKNOWN_120", 8)                          // CONF:NONE — fn SendStrtoll @ 0x002d74e0 strtoll(CS2 str)→8B (entry 0x015d3730); no STRTOLL official enum
    c(121, "UNKNOWN_121", 3)                          // CONF:NONE — shim FUN_0015c3b0 → FUN_0015c100 else-branch (3B comp-target); no provable official name
    c(122, "UNKNOWN_122", -2)                         // CONF:NONE — FUN_002d1b10 tinyKeyEncrypt+pSizeMarker gated login state; no provable official name
    c(123, "BUG_REPORT", 1)                           // CONF:HIGH — beta BugReporting→&ClientProt::BUG_REPORT; 948 SendBugReport @ 0x002d0140 (entry 0x015d3700); official enum
    c(124, "MESSAGE_PUBLIC", -1)                      // CONF:HIGH — beta &ClientProt::MESSAGE_PUBLIC; 948 SendMessagePublic @ 0x0037a9c0 (entry 0x015d36f0); official enum
    c(125, "UNKNOWN_125", 9)                          // CONF:NONE — shim FUN_0015c850 → SendOpLocTLong else-branch (9B use-on-loc); no provable official name
    c(126, "UNKNOWN_126", 9)                          // CONF:NONE — shim FUN_0015c7d0 → SendOpLocTLong else-branch (9B use-on-loc); no provable official name
    c(127, "IF_BUTTON1", 8)                           // CONFIRMED — beta 0x00a394d0 / 948 IfButtonXInner 0x002978d0, CS2-table opt1 slot 0x01365920->ProtEntry 0x015d36c0 (xref-verified) / capture op127 size8 (interface click). 8B click: interfaceHash intLittle + slotId uShortAddLittle + itemId uShortAdd. Decoder in Rev948ClientCodecs.
    c(128, "UNKNOWN_128", -2)                         // CONF:NONE — UNBOUND (RegisterAll only)
    c(129, "UNKNOWN_129", -1)                         // CONF:NONE — fn SendOpLocCS2 @ 0x0033e140 (entry 0x015d36a0); no official OPLOC*_CS2 enum
}

private fun Codec.c(opcode: Int, name: String, size: Int) {
    val protSize = when (size) {
        -1 -> ProtSize.VarByte
        -2 -> ProtSize.VarShort
        else -> ProtSize.Fixed(size)
    }
    clientProtInfo.putIfAbsent(opcode, Codec.ProtInfo(name, protSize))
}
