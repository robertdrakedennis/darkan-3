package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize

/**
 * Registers opcode metadata (name + size) for ALL 130 ClientProt opcodes in rev 947-1.
 * Opcodes that have real decoder registrations will NOT be overwritten (putIfAbsent).
 *
 * Extracted from rs2client.947-1 binary, RegisterAll at 0x00181e10.
 * 14 packets confirmed by Send function decompilation; rest identified by size matching.
 */
internal fun Codec.registerRev947ClientProtStubs() {
    c(0, "MOVE_GAME_MINIMENU", 5)
    c(1, "UNKNOWN_1", 3)
    c(2, "UNKNOWN_2", 4)
    c(3, "MOVE_SCRIPTED", 5)
    c(4, "UNKNOWN_4", 8)
    c(5, "UNKNOWN_5", 8)
    c(6, "OPNPC_T_EXTENDED", 15)
    c(7, "UNKNOWN_7", 6)
    c(8, "UNKNOWN_8", 4)
    c(9, "UNKNOWN_9", 12)
    c(10, "UNKNOWN_10", 9)
    c(11, "UNKNOWN_11", 9)
    c(12, "UNKNOWN_12", -1)
    c(13, "UNKNOWN_13", 16)
    c(14, "UNKNOWN_14", -2)
    c(15, "UNKNOWN_15", 6)
    c(16, "UNKNOWN_16", 8)
    c(17, "UNKNOWN_17", 7)
    c(18, "UNKNOWN_18", 8)
    c(19, "UNKNOWN_19", -1)
    c(20, "UNKNOWN_20", 1)
    c(21, "UNKNOWN_21", 8)
    c(22, "FRIENDLIST_DEL", -1)          // CONFIRMED: SendFriendlistDel @ 0x003dcba0
    c(23, "WORLDLIST_FETCH", 4)          // CONFIRMED: SendWorldlistFetch @ 0x0023f2a0
    c(24, "CLANCHANNEL_KICKUSER", -1)    // CONFIRMED: SendSocialRequest @ 0x003dc8a0
    c(25, "UNKNOWN_25", -1)
    c(26, "UNKNOWN_26", -1)
    c(27, "UNKNOWN_27", 0)
    c(28, "MOVE_GAME_EXTENDED", 18)      // unique size match
    c(29, "UNKNOWN_29", 8)
    c(30, "UNKNOWN_30", 4)
    c(31, "UNKNOWN_31", -1)
    c(32, "UNKNOWN_32", 4)
    c(33, "UNKNOWN_33", 3)
    c(34, "UNKNOWN_34", -1)
    c(35, "UNKNOWN_35", -1)
    c(36, "UNKNOWN_36", 8)
    c(37, "UNKNOWN_37", 1)
    c(38, "UNKNOWN_38", 1)
    c(39, "UNKNOWN_39", -2)
    c(40, "UNKNOWN_40", 16)
    c(41, "UNKNOWN_41", 0)
    c(42, "UNKNOWN_42", -1)
    c(43, "UNKNOWN_43", 0)
    c(44, "UNKNOWN_44", 7)
    c(45, "UNKNOWN_45", 9)
    c(46, "UNKNOWN_46", 3)
    c(47, "UNKNOWN_47", 4)
    c(48, "IGNORELIST_ADD", -1)          // CONFIRMED: SendIgnorelistAdd @ 0x003b6a40
    c(49, "UNKNOWN_49", 17)
    c(50, "UNKNOWN_50", 3)
    c(51, "UNKNOWN_51", 8)
    c(52, "UNKNOWN_52", 1)
    c(53, "UNKNOWN_53", -2)
    c(54, "UNKNOWN_54", 3)
    c(55, "MAP_BUILD_COMPLETE", 0)       // CONFIRMED: SendMapBuildComplete @ 0x00335aa0
    c(56, "UNKNOWN_56", 1)
    c(57, "UNKNOWN_57", 1)
    c(58, "OPLOC_T_EXTENDED", 15)        // size count match
    c(59, "DETECT_MODIFIED_CLIENT", 4)   // CONFIRMED: SendDetectModifiedClient @ 0x002992e0
    c(60, "UNKNOWN_60", 3)
    c(61, "UNKNOWN_61", 4)
    c(62, "UNKNOWN_62", 2)
    c(63, "UNKNOWN_63", 4)
    c(64, "UNKNOWN_64", -1)
    c(65, "UNKNOWN_65", -1)
    c(66, "UNKNOWN_66", -1)
    c(67, "UNKNOWN_67", 7)
    c(68, "UNKNOWN_68", -2)
    c(69, "MESSAGE_PUBLIC_EFFECTS", -1)  // CONFIRMED: SendMessagePublicWithEffects @ 0x003ac7a0
    c(70, "UNKNOWN_70", -2)
    c(71, "UNKNOWN_71", 0)
    c(72, "UNKNOWN_72", 9)
    c(73, "UNKNOWN_73", 9)
    c(74, "EVENT_TELEMETRY", -2)         // CONFIRMED: SendAppletFocusEvents @ 0x0023f7a0
    c(75, "UNKNOWN_75", -1)
    c(76, "UNKNOWN_76", -2)
    c(77, "UNKNOWN_77", 8)
    c(78, "MOVE_GAME", -2)               // CONFIRMED: SendMoveGame @ 0x003e9b90
    c(79, "UNKNOWN_79", 9)
    c(80, "UNKNOWN_80", -1)
    c(81, "OPLOC_T2", 11)               // CONFIRMED: SendOpLocTLong @ 0x002047b0
    c(82, "UNKNOWN_82", 4)
    c(83, "UNKNOWN_83", 18)
    c(84, "UNKNOWN_84", -1)
    c(85, "OPNPC_T2_EXTENDED", 11)      // size count match
    c(86, "UNKNOWN_86", -1)
    c(87, "UNKNOWN_87", -1)
    c(88, "UNKNOWN_88", 3)
    c(89, "UNKNOWN_89", 3)
    c(90, "UNKNOWN_90", -2)
    c(91, "UNKNOWN_91", 2)
    c(92, "UNKNOWN_92", 22)
    c(93, "FRIENDLIST_ADD", -1)          // CONFIRMED: SendFriendlistAdd @ 0x003e2cd0
    c(94, "UNKNOWN_94", -2)
    c(95, "UNKNOWN_95", 8)
    c(96, "UNKNOWN_96", 8)
    c(97, "UNKNOWN_97", 9)
    c(98, "UNKNOWN_98", 7)
    c(99, "UNKNOWN_99", 2)
    c(100, "UNKNOWN_100", -2)
    c(101, "UNKNOWN_101", 2)
    c(102, "UNKNOWN_102", 7)
    c(103, "UNKNOWN_103", -1)
    c(104, "UNKNOWN_104", 3)
    c(105, "UNKNOWN_105", -1)
    c(106, "UNKNOWN_106", 4)
    c(107, "UNKNOWN_107", 3)
    c(108, "WINDOW_STATUS", 6)           // CONFIRMED: SendDisplayInfo @ 0x00321c60 (size 3->6)
    c(109, "UNKNOWN_109", 9)
    c(110, "UNKNOWN_110", 3)
    c(111, "UNKNOWN_111", -1)
    c(112, "OPLOC_T3", 11)              // size count match
    c(113, "UNKNOWN_113", 3)
    c(114, "UNKNOWN_114", 3)
    c(115, "UNKNOWN_115", 3)
    c(116, "UNKNOWN_116", -2)
    c(117, "UNKNOWN_117", -1)
    c(118, "UNKNOWN_118", 7)
    c(119, "UNKNOWN_119", -1)
    c(120, "MESSAGE_PUBLIC", -1)         // CONFIRMED: SendMessagePublic @ 0x003ab590
    c(121, "MESSAGE_PRIVATE", -2)        // CONFIRMED: SendMessagePrivate @ 0x003adf60
    c(122, "UNKNOWN_122", 7)
    c(123, "UNKNOWN_123", 3)
    c(124, "UNKNOWN_124", -1)
    c(125, "UNKNOWN_125", 3)
    c(126, "UNKNOWN_126", 9)
    c(127, "UNKNOWN_127", 3)
    c(128, "UNKNOWN_128", 0)
    c(129, "UNKNOWN_129", 3)
}

private fun Codec.c(opcode: Int, name: String, size: Int) {
    val protSize = when (size) {
        -1 -> ProtSize.VarByte
        -2 -> ProtSize.VarShort
        else -> ProtSize.Fixed(size)
    }
    clientProtInfo.putIfAbsent(opcode, Codec.ProtInfo(name, protSize))
}
