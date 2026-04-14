package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize

/**
 * Registers opcode metadata (name + size) for ALL 218 ServerProt opcodes.
 * Opcodes that have real encoder registrations (via serverProt<T>) will NOT be overwritten
 * because serverProtStub uses putIfAbsent.
 *
 * Verified identical between rs2client.947-1 and rs2client.947-3:
 *   RegisterAll at 0x00181e10, BindHandlers at 0x001185aa
 *   g_serverProtVector at 0x016ee0a0, max opcode 0xD9 (217)
 *   All 218 opcodes and sizes are unchanged between 947-1 and 947-3.
 *
 * 79 entries matched by handler name from BindHandlers (77 from 947-1 + 2 newly identified in 947-3).
 * 8 entries matched by unique size after handler matches removed.
 * 1 new packet (opcode 149, size 17) not present in rev 946.
 * 130 entries unmatched (common sizes, unnamed FUN_ handlers).
 */
internal fun Codec.registerRev947ServerProtStubs() {
    s(0, "UNKNOWN_0", -1)
    s(1, "CLIENT_SETVARC_SMALL", 3)                 // Variables constructor slot #7
    s(2, "UNKNOWN_2", -2)
    s(3, "CAM_FORCEANGLE", 1)                        // handler: Camera::CAM_FORCEANGLE
    s(4, "UNKNOWN_4", 4)
    s(5, "UPDATE_INV_PARTIAL", -2)                   // handler: Inventory::UPDATE_INV_PARTIAL
    s(6, "HASHED_WORLD_TOKEN", -1)                   // empty invoke handler; Base64url token at world login
    s(7, "MESSAGE_FRIENDCHANNEL", -1)                // handler: Chat::MESSAGE_FRIENDCHANNEL
    s(8, "IF_SETPOSITION", 23)                       // unique size 23
    s(9, "UNKNOWN_9", -2)
    s(10, "VARP_SMALL", 3)                           // Variables constructor slot #2
    s(11, "CLANSETTINGS_DELTA_CHAT", -2)             // handler: Chat::CLANSETTINGS_DELTA_CHAT
    s(12, "NPC_INFO", -2)                            // handler: NPCInfo::NPC_INFO
    s(13, "UNKNOWN_13", 6)
    s(14, "UNKNOWN_14", 5)
    s(15, "UNKNOWN_15", -2)
    s(16, "UNKNOWN_16", 4)
    s(17, "IF_OPENSUB", 8)                           // Interfaces constructor slot #1
    s(18, "UNKNOWN_18", 3)
    s(19, "UPDATE_RUNENERGY", 1)                     // handler: Misc::SET_RUN_ENERGY
    s(20, "UNKNOWN_20", 7)
    s(21, "MESSAGE_FRIENDCHAT", -1)                  // handler: Chat::MESSAGE_FRIENDCHAT (identified in 947-3)
    s(22, "UNKNOWN_22", 6)
    s(23, "UNKNOWN_23", 4)
    s(24, "NPC_OP", -1)                              // handler: NPCInfo::SET_NPC_OP
    s(25, "UNKNOWN_25", 0)
    s(26, "UNKNOWN_26", 6)
    s(27, "PLAYER_INFO", -2)                         // handler: PlayerList::ProcessPlayerInfo
    s(28, "CLANCHANNEL_FULL", -2)                    // handler: Clans::CLANCHANNEL_FULL
    s(29, "UNKNOWN_29", 3)
    s(30, "CHANGE_LOBBY", -2)                        // handler: Lobby::CHANGE_LOBBY
    s(31, "UNKNOWN_31", -2)
    s(32, "UNKNOWN_32", 10)
    s(33, "IF_CLOSESUB", 4)                             // handler: Interfaces::IF_CLOSESUB_ACTIVE. Cross-ref: unstripped IF_CLOSESUB
    s(34, "IF_SETEVENTS", 10)                        // Interfaces constructor, SetServerActiveProperties
    s(35, "IF_SETEVENTS2", 12)                       // Interfaces constructor, SetServerActiveProperties
    s(36, "UPDATE_UID192", 28)                       // handler: PlayerInfo::REBUILD_PLAYERINFO_POSITIONS
    s(37, "UNKNOWN_37", 2)
    s(38, "UNKNOWN_38", 5)
    s(39, "CHAT_FILTER_SETTINGS", -1)                // handler: Chat::CHAT_FILTER_SETTINGS
    s(40, "UNKNOWN_40", 19)
    s(41, "UNKNOWN_41", -1)
    s(42, "UNKNOWN_42", 3)
    s(43, "RESET_ENTITY_LISTS", 0)                   // handler: Misc::RESET_ENTITY_LISTS
    s(44, "UNKNOWN_44", 6)
    s(45, "MESSAGE_PUBLIC", -2)                      // handler: Chat::MESSAGE_PUBLIC
    s(46, "CAMERA_UPDATE", -2)                       // handler: Camera::CAM_UPDATE
    s(47, "MAP_PROJANIM", 20)                        // unique size 20
    s(48, "RESET_CLIENT_VARCACHE", 0)                // handler: ClientState::RESET_ALL_VARPS
    s(49, "UNKNOWN_49", 0)
    s(50, "UNKNOWN_50", 3)
    s(51, "UNKNOWN_51", 7)
    s(52, "SET_TICK_TIMER", 2)                       // handler: ClientState::SET_TICK_TIMER
    s(53, "UNKNOWN_53", 8)
    s(54, "NPC_HEADICON_SPECIFIC", -1)               // handler: NPCInfo::NPC_HEADICON_SPECIFIC
    s(55, "CLIENT_SETVARCBIT_LARGE", 6)              // Variables constructor slot #11
    s(56, "UNKNOWN_56", 10)
    s(57, "UNKNOWN_57", 3)
    s(58, "MESSAGE_QUICKCHAT_CLANCHAT", -1)          // handler: Chat::MESSAGE_QUICKCHAT_CLANCHAT
    s(59, "JCOINS_UPDATE", 4)                        // handler: Misc::SET_DISPLAY_INT
    s(60, "UNKNOWN_60", 7)
    s(61, "NPC_UPDATE_ORIGIN", 1)                    // handler: NPCInfo::SET_NPC_UPDATE_ORIGIN
    s(62, "UNKNOWN_62", 11)
    s(63, "CLANSETTINGS_DELTA", -2)                  // handler: Clans::CLANSETTINGS_DELTA
    s(64, "UNKNOWN_64", 8)
    s(65, "SET_READY_FLAG", 0)                       // handler: ClientState::SET_READY_FLAG
    s(66, "UPDATE_STAT", 6)                          // handler: StatTable::UpdateStat
    s(67, "CLIENT_SETVARC_STR", -1)                 // capture: string "Yehp" + 2B varc ID
    s(68, "IF_OPENTOP", 6)                           // Interfaces constructor slot #2
    s(69, "UPDATE_INV_FULL", -2)                     // handler: Inventory::UPDATE_INV_FULL_impl
    s(70, "UNKNOWN_70", 0)
    s(71, "UNKNOWN_71", 6)
    s(72, "UNKNOWN_72", 5)
    s(73, "MESSAGE_QUICKCHAT_PRIVATE", -1)           // handler: Chat::MESSAGE_QUICKCHAT_PRIVATE
    s(74, "UNKNOWN_74", 8)
    s(75, "UNKNOWN_75", 10)
    s(76, "UNKNOWN_76", 10)
    s(77, "SET_MULTIWAY_STATE", 1)                   // handler: Misc::SET_MULTIWAY_STATE
    s(78, "PLAYER_INFO_DECODE", 14)                  // handler: PlayerInfo::PLAYER_INFO_DECODE
    s(79, "UNKNOWN_79", -1)
    s(80, "CAM_SMOOTHRESET", 0)                      // handler: Camera::CAM_SMOOTHRESET
    s(81, "UNKNOWN_81", 4)
    s(82, "MESSAGE_QUICKCHAT_CLANCHANNEL", -1)       // handler: Chat::MESSAGE_QUICKCHAT_CLANCHANNEL
    s(83, "CLANCHANNEL_DELTA", -1)                   // handler: Chat::CLANCHANNEL_DELTA
    s(84, "CLANCHANNEL_FULL_CHAT", -2)               // handler: Chat::CLANCHANNEL_FULL_CHAT
    s(85, "UNKNOWN_85", 8)
    s(86, "UNKNOWN_86", -2)
    s(87, "MIDI_SONG", 5)                               // handler: Audio::MIDI_SONG. Cross-ref: unstripped MIDI_SONG
    s(88, "UNKNOWN_88", 10)
    s(89, "CAM_RESET", 0)                            // handler: Camera::CAM_RESET
    s(90, "UNKNOWN_90", -2)
    s(91, "CUTSCENE_DATA", 35)                       // handler: Misc::CUTSCENE_DATA (confirmed in 947-3)
    s(92, "UNKNOWN_92", 8)                            // NOT IF_SETGRAPHIC (capture proves IF_SETGRAPHIC is op 94)
    s(93, "UNKNOWN_93", 10)
    s(94, "IF_SETTOPLEVELINTERFACE", 19)              // Ghidra: direct top-level interface set (handler 0x0022ca10)
    s(95, "UNKNOWN_95", 10)
    s(96, "UNKNOWN_96", 8)
    s(97, "UNKNOWN_97", 25)
    s(98, "UNKNOWN_98", 10)
    s(99, "UNKNOWN_99", -1)
    s(100, "UNKNOWN_100", 10)
    s(101, "MESSAGE_TYPE6", -2)                        // Ghidra: ChatHistory::AddChat with type 6 (NOT RUNCLIENTSCRIPT)
    s(102, "UPDATE_FRIENDLIST", -2)              // handler: UPDATE_SITESETTINGS at 0x00247a60
    s(103, "IF_SETHIDE", 5)                             // handler: Interfaces::IF_SETHIDE. 1B hide flag + 4B component hash
    s(104, "CLANSETTINGS_FULL", -2)                  // handler: Clans::CLANSETTINGS_FULL
    s(105, "MESSAGE_GAME", -1)                       // handler: Chat::MESSAGE_GAME
    s(106, "UNKNOWN_106", 10)
    s(107, "UNKNOWN_107", 25)
    s(108, "SET_PLAYER_OP_2", 2)                     // handler: Misc::SET_PLAYER_OP_2
    s(109, "PLAYER_GROUP_FULL", -2)                  // handler: PlayerGroup::PLAYER_OP
    s(110, "UNKNOWN_110", 29)
    s(111, "VARP_LARGE", 6)                          // Variables constructor slot #3
    s(112, "CLIENT_SETVARC_LARGE", 6)                // Variables constructor slot #8
    s(113, "CAM_TARGET", 1)                          // handler: Camera::CAM_TARGET
    s(114, "UNKNOWN_114", 2)
    s(115, "CLIENT_SETVARCBIT_SMALL", 3)             // Variables constructor slot #10
    s(116, "SET_PLAYER_OP_3", 1)                     // handler: Misc::SET_PLAYER_OP_3
    s(117, "IF_SETANGLE", 32)                        // unique size 32
    s(118, "UNKNOWN_118", 25)
    s(119, "UNKNOWN_119", 10)                         // NOT VARP_LONG (capture proves VARP_LONG is op 170)
    s(120, "UNKNOWN_120", 0)
    s(121, "RUNCLIENTSCRIPT", -2)                    // capture: lobby news with date strings, 15-274B entries
    s(122, "UNKNOWN_122", 8)
    s(123, "UNKNOWN_123", 8)
    s(124, "UNKNOWN_124", 12)
    s(125, "MESSAGE_CLANCHANNEL_SYSTEM", -1)         // handler: Chat::MESSAGE_CLANCHANNEL
    s(126, "UNKNOWN_126", -2)
    s(127, "UNKNOWN_127", 11)
    s(128, "UNKNOWN_128", 6)
    s(129, "MESSAGE_PRIVATE_ECHO", -1)               // handler: Chat::MESSAGE_PRIVATE_ECHO
    s(130, "UNKNOWN_130", 0)
    s(131, "UNKNOWN_131", 3)
    s(132, "UPDATE_REBOOT_TIMER", 4)                 // handler: Misc::SET_SYSUPDATE_TIMER
    s(133, "UNKNOWN_133", 2)
    s(134, "UNKNOWN_134", -2)
    s(135, "UNKNOWN_135", 3)
    s(136, "UNKNOWN_136", 9)
    s(137, "UNKNOWN_137", 8)
    s(138, "NPC_UPDATE_FLAG", 1)                     // handler: NPCInfo::SET_NPC_UPDATE_FLAG
    s(139, "UNKNOWN_139", -2)
    s(140, "UNKNOWN_140", 5)
    s(141, "UNKNOWN_141", 5)
    s(142, "UNKNOWN_142", 0)
    s(143, "UNKNOWN_143", 1)
    s(144, "SET_INTERACTION_FLAG_D", 3)              // handler: Misc::SET_INTERACTION_FLAG_D
    s(145, "UNKNOWN_145", 14)
    s(146, "IF_SETGRAPHIC_ACTIVE", -1)               // handler: Interfaces::IF_SETGRAPHIC_ACTIVE_handler
    s(147, "LOGOUT", 0)                              // handler: Misc::LOGOUT
    s(148, "UNKNOWN_148", 9)
    s(149, "NEW_PACKET_149", 17)                     // NEW in 947-1, not present in rev 946
    s(150, "UNKNOWN_150", 5)
    s(151, "MESSAGE_PRIVATE", -1)                    // handler: Chat::MESSAGE_PRIVATE
    s(152, "UNKNOWN_152", 4)
    s(153, "UNKNOWN_153", 4)
    s(154, "UNKNOWN_154", 1)
    s(155, "SET_CHAT_FILTER_B", 1)                   // handler: Chat::SET_CHAT_FILTER_B
    s(156, "UNKNOWN_156", 1)
    s(157, "UNKNOWN_157", 2)
    s(158, "UNKNOWN_158", 12)
    s(159, "WORLDLIST_FETCH_REPLY", -2)              // handler: Social::UPDATE_FRIENDCHAT_CHANNEL
    s(160, "UNKNOWN_160", 3)
    s(161, "UNKNOWN_161", 10)
    s(162, "UNKNOWN_162", 2)
    s(163, "UNKNOWN_163", 2)
    s(164, "UNKNOWN_164", 1)
    s(165, "UNKNOWN_165", 4)
    s(166, "SKIP_DATA", -2)                          // handler: Misc::SKIP_DATA
    s(167, "UNKNOWN_167", -1)
    s(168, "UNKNOWN_168", 2)
    s(169, "UNKNOWN_169", 2)
    s(170, "VARP_LONG", 10)                           // capture-verified: 8B long value + 2B varp ID
    s(171, "SERVER_TICK_END", 8)                     // handler: Misc::SERVER_TICK_END
    s(172, "REBUILD_NORMAL", -2)                        // handler: ClientState::REBUILD_NORMAL (0x002144e0). Cross-ref: unstripped REBUILD_NORMAL
    s(173, "UPDATE_ZONE_FULL_FOLLOWS", -2)           // handler: ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS_handler
    s(174, "SET_INTERACTION_FLAG_C", 1)              // handler: Misc::SET_INTERACTION_FLAG_C
    s(175, "UNKNOWN_175", 6)
    s(176, "UNKNOWN_176", -2)
    s(177, "UPDATE_INV_GROUP", -2)                   // handler: Inventory::UPDATE_INV_GROUP
    s(178, "UPDATE_PLAYER_CHAT", -2)                 // handler: PlayerInfo::UPDATE_PLAYER_CHAT
    s(179, "FRIENDCHAT_JOIN", -1)                    // handler: Chat::FRIENDCHAT_JOIN
    s(180, "PLAYER_INFO_DECODE_2", -2)               // handler: PlayerInfo::PLAYER_INFO_DECODE_2
    s(181, "UNKNOWN_181", 6)
    s(182, "UNKNOWN_182", 5)
    s(183, "UNKNOWN_183", 4)
    s(184, "UNKNOWN_184", 4)
    s(185, "UNKNOWN_185", 1)
    s(186, "IF_OPENSUB_THUNK", -2)                   // handler: Interfaces::IF_OPENSUB_thunk
    s(187, "SET_WORLD_TARGET", -1)                   // handler: WorldData::SET_WORLD_TARGET
    s(188, "REBUILD_WORLDENTITY", -2)                // handler: ClientState::REBUILD_WORLDENTITY (identified in 947-3)
    s(189, "IF_MOVESUB", 3)                          // handler: Interfaces::IF_MOVESUB_thunk
    s(190, "UNKNOWN_190", 15)                        // unique size 15
    s(191, "UNKNOWN_191", 2)
    s(192, "UNKNOWN_192", 29)
    s(193, "UNKNOWN_193", 5)
    s(194, "UNKNOWN_194", 3)
    s(195, "TRIGGER_ONDIALOGABORT", 0)               // handler: ClientState::TRIGGER_ONDIALOGABORT
    s(196, "PROJANIM_SPECIFIC", 21)                  // unique size 21
    s(197, "UNKNOWN_197", 5)
    s(198, "ANTI_CHEAT_CHALLENGE", 8)                  // handler: HandleAntiCheatChallenge. Server 8B, client responds 9B. Every ~6s
    s(199, "UNKNOWN_199", 28)                        // unique size 28 (after handler matches)
    s(200, "SET_URL_STRING", -1)                     // handler: Misc::SET_URL_STRING
    s(201, "UNKNOWN_201", 3)
    s(202, "UNKNOWN_202", 3)
    s(203, "UNKNOWN_203", 4)
    s(204, "UNKNOWN_204", 3)
    s(205, "NPC_INFO_THUNK", -2)                     // handler: NPCInfo::NPC_INFO_thunk
    s(206, "UNKNOWN_206", 33)                        // unique size 33
    s(207, "PLAYER_GROUP_DELTA", -2)                 // handler: PlayerGroup::UPDATE_PLAYER_GROUP
    s(208, "UNKNOWN_208", 14)
    s(209, "LOGOUT_TRANSFER", 0)                     // handler: Misc::LOGOUT_TRANSFER
    s(210, "UNKNOWN_210", 9)
    s(211, "UPDATE_IGNORELIST", -1)                  // handler: Social::UPDATE_IGNORELIST_thunk
    s(212, "UNKNOWN_212", -2)
    s(213, "UNKNOWN_213", 1)
    s(214, "UPDATE_URL_STRING", -1)                  // handler: Misc::UPDATE_URL_STRING
    s(215, "UNKNOWN_215", 6)
    s(216, "NO_TIMEOUT", 0)                          // trivial return handler (keepalive)
    s(217, "SET_CHAT_FILTER_A", 1)                   // handler: Chat::SET_CHAT_FILTER_A
}

private fun Codec.s(opcode: Int, name: String, size: Int) {
    val protSize = when (size) {
        -1 -> ProtSize.VarByte
        -2 -> ProtSize.VarShort
        else -> ProtSize.Fixed(size)
    }
    serverProtInfo.putIfAbsent(opcode, Codec.ProtInfo(name, protSize))
}
