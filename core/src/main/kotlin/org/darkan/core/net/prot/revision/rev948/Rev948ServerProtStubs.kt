package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.Codec
import org.darkan.core.net.prot.ProtSize

/**
 * Registers opcode metadata (name + size) for ALL 218 ServerProt opcodes in rev 948-2-2.
 * Opcodes that have real encoder registrations (via serverProt<T>) will NOT be overwritten
 * because serverProtStub uses putIfAbsent.
 *
 * Source: `docs/net/serverprot/948-opcode-tables.md` ServerProt table.
 * Authority: rs2client.948-2-2 (Ghidra, STRIPPED). See Phase 1 deliverable for the
 * full traceability matrix.
 *
 * NAMING LAW (correction pass, 2026-05-31): every name below MUST be a verbatim official
 * `jag::ServerProt::<NAME>` enum symbol, read from the beta unstripped binary
 * (librs2client.so, rev ~890 — the ONLY source of official enum names). The official enum
 * vocabulary was dumped from the ProtEntry globals at 0x00a35800..0x00a387c0. A C++
 * handler/function name (e.g. `Process*`, `Handle*`, `*_impl`, `*_thunk`) is NEVER a valid
 * packet name, and invented descriptive placeholders (e.g. `WORLDENTITY_LIST_OP`, `NOOP_128`,
 * `SPOTANIM_2`) are NEVER valid packet names. If an opcode's handler cannot be mapped to a
 * verbatim official enum symbol in the beta, it MUST be `UNKNOWN_<op>` — with size (authoritative
 * from RegisterAll @0x000c4700), handler address, and read-shape preserved in the comment so a
 * later capture-driven pass can identify it.
 *
 * Why so many UNKNOWN_<op>: the beta is rev ~890; the target is rev 948. ~58 revisions of
 * protocol churn added many ServerProt opcodes that simply have NO enum symbol in the beta
 * dump. For those packets the official 948 name is unknowable from the only sanctioned source,
 * so they stay UNKNOWN_<op> regardless of how confidently the handler behavior is understood.
 * The handler behavior is retained in the comment as identification evidence.
 *
 * CONF taxonomy:
 *   CONFIRMED / CONF:CERTAIN — handler decompiled this revision; opcode/size from RegisterAll
 *     InitEntry; official enum symbol matched; wire verified.
 *   CONF:HIGH    — official beta enum symbol matched by handler behavior (this pass) or a 947-3
 *     handler carried over whose official enum identity is established.
 *   CONF:NONE    — either op 53 (genuinely unbound) OR no official beta enum maps to the opcode
 *     (the packet has no symbol in the rev-890 dump) — name is UNKNOWN_<op>.
 *
 * Encoders win over these stubs (putIfAbsent), so for encoded opcodes the runtime name comes from
 * the Kotlin class, not this table. (The varp/varc family opcodes 10/51/116/147/196 keep their
 * encoder-side names; their stubs below reflect the official enum identity where one exists in
 * the beta, else UNKNOWN_<op>.)
 *
 * Full per-opcode evidence table: `docs/net/serverprot/948-serverprot-complete.md`.
 */
internal fun Codec.registerRev948ServerProtStubs() {
    s(0, "UNKNOWN_0", -1)                            // UNKNOWN (2026-06-25 oracle) — prior MESSAGE_QUICKCHAT_CLANCHANNEL was a binder-mislabel; the official MESSAGE_QUICKCHAT_CLANCHANNEL is op2 (HANDLER_ID_HIGH). Handler Chat::MESSAGE_QUICKCHAT_CLANCHANNEL not behaviourally confirmed here. VarByte.
    s(1, "UNKNOWN_1", -1)                            // CONF:NONE — no official beta enum maps. Prior fn-name 'SET_NPC_OP' (NPC_OP fn). Handler: was op 24 (NPC_OP) in 947-3. VarByte.
    s(2, "MESSAGE_QUICKCHAT_CLANCHANNEL", -1)        // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT decodes as the official MESSAGE_QUICKCHAT_CLANCHANNEL clan-channel quickchat. VarByte.
    s(3, "UNKNOWN_3", 19)                            // CONF:NONE — no official beta enum maps. Prior 'IF_SETTOPLEVELINTERFACE' (not in beta enum). Handler: was op 94 in 947-3. sz19.
    s(4, "IF_OPENSUB_ACTIVE_LOC", 32)                // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x001da640 (DB IF_SETANGLE): BuildArea::DecodePackedCoord + ints + compid (sz32) = IF_OPENSUB_ACTIVE_LOC. Was op 117 in 947-3.
    s(5, "RESET_CLIENT_VARCACHE", 0)                 // CONFIRMED — beta 0x00a368c0 / 948 handler 0x00119a50 / capture. RECOVERY: clears BOTH player(+0x19b60)&varc(+0x35c08) domains; capture label ResetClientVarcache sz0. Prior stub anchored on C++ fn-name RESET_ALL_VARPS. Encoder ResetClientVarcache in Rev948ServerCodecsMisc owns this opcode at runtime.
    s(6, "UNKNOWN_6", 7)                             // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::LOC_PREFETCH plausible but size-ambiguous; demoted to UNKNOWN. Encoder LocPrefetch (Rev948ServerCodecsZone) owns this opcode at runtime, so the displayed name resolves to LOC_PREFETCH via pascalToScreamingSnake.
    s(7, "UNKNOWN_7", 0)                             // CONF:NONE — no official beta enum maps. Prior 'RESET_ENTITY_LISTS' (Misc fn). Handler: was op 43 in 947-3. sz0.
    s(8, "UNKNOWN_8", 5)                             // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERHEAD_ACTIVE' (not in beta enum). Handler @ 0x00193530. sz5.
    s(9, "CLANCHANNEL_FULL", -2)                     // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::CLANCHANNEL_FULL_CHAT decodes as the official CLANCHANNEL_FULL. VarShort.
    s(10, "VARBIT_SMALL", 3)                         // CONF:HIGH — official enum jag::ServerProt::VARBIT_SMALL (beta @0xa38140, sz3). Handler FUN_00119870 setBitFromPacket. Prior 'VARP_BIT_SMALL' was a non-official descriptor. Wire: id(BE u16) + value(-128-byte). Encoder in Rev948ServerCodecsVariable.
    s(11, "MESSAGE_PUBLIC", -2)                      // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PUBLIC (beta @0xa38580). Was op 45 in 947-3.
    s(12, "UNKNOWN_12", 2)                           // CONF:NONE — no official beta enum maps. Prior 'SET_PLAYER_OP_2' (a 948 sibling of SET_PLAYER_OP; no distinct beta enum). Handler: was op 108 in 947-3. sz2.
    s(13, "UNKNOWN_13", 1)                           // CONF:NONE — no official beta enum maps. Prior 'SET_PLAYER_OP_3' (948 sibling; no distinct beta enum). Handler: was op 116 in 947-3. sz1.
    s(14, "IF_SETTEXTFONT", 8)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00193940 (DB IF_SETSPRITE) decodes as the official IF_SETTEXTFONT. sz8. (Encoder IfSetSprite registered here; canonical name wins for display.)
    s(15, "UNKNOWN_15", 11)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_COORD' placeholder. Handler FUN_001dade0: DecodePackedCoord + entity spawn at tile w/ anim+delay. Graphic/spotanim-at-coord family. sz11.
    s(16, "LOC_DEL", 2)                              // CONF:HIGH — official enum jag::ServerProt::LOC_DEL (beta @0xa364c0, sz2). Handler @ 0x001382d0.
    s(17, "SET_PLAYER_OP", -1)                       // CONF:HIGH — official enum jag::ServerProt::SET_PLAYER_OP (beta @0xa37c40). Handler Misc::SET_PLAYER_OP @ 0x0013eec0, bound by PlayerList::BindHandlers. Was op 0 in 947-3.
    s(18, "UNKNOWN_18", 1)                           // CONF:NONE — no official beta enum maps. Prior 'CAM_TARGET' (Camera fn; not in beta enum). Handler: was op 113 in 947-3. sz1.
    s(19, "URL_OPEN", -2)                            // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::WebPage::URL_OPEN. VarShort.
    s(20, "UPDATE_INV_STOP_TRANSMIT", 3)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Inventory::UNKNOWN_op14_handler decodes as the official UPDATE_INV_STOP_TRANSMIT. sz3.
    s(21, "LOC_ANIM_SPECIFIC", 10)                   // CONF:HIGH — official enum jag::ServerProt::LOC_ANIM_SPECIFIC (beta @0xa37840, sz10). Handler @ 0x00118ef0.
    s(22, "PLAYER_INFO", -2)                         // CONF:HIGH — official enum jag::ServerProt::PLAYER_INFO (beta @0xa36a00). ProcessPlayerInfo. Was op 27 in 947-3.
    s(23, "UNKNOWN_23", -2)                          // CONF:NONE — no official beta enum maps. Prior 'PLAYER_OP' (PlayerGroup fn). Handler @ 0x001869f0 (VarShort). DISTINCT from op 17 SET_PLAYER_OP. beta SET_PLAYER_OP already maps to op17. 947-3 op 109. VarShort.
    s(24, "UNKNOWN_24", -1)                          // CONF:NONE — no official beta enum maps. Prior 'UPDATE_ZONE_PARTIAL' (not in beta enum — beta has UPDATE_ZONE_PARTIAL_FOLLOWS/_ENCLOSED only). Handler @ 0x001254c0. VarByte.
    s(25, "UNKNOWN_25", -2)                          // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_TYPE6' placeholder. Handler FUN_001898a0: reads 2 strings, ChatHistory::AddChat(type=6). Chat message family. VarShort.
    s(26, "UPDATE_FRIENDLIST", -2)                    // CONFIRMED — handler @ 0x001a44e0 (thunk 0x001a63e0) reads a per-friend record loop {warnMessage(g1), displayName(gStr), previousName(gStr), worldId(g2), fcRank(g1), flags(g1), [worldName(gStr)+platform(g1)+worldFlags(g4) if worldId>0], notes(gStr)} → beta jag::ServerProt::UPDATE_FRIENDLIST. Was op102 in 947-3. CORRECTION: the validation matrix mislabeled this UPDATE_SITESETTINGS (trusted a fabricated Ghidra label over the 4-string friend-record behavior); empirically confirmed UPDATE_FRIENDLIST by the working lobby friends list. Encoder = FriendStatus (Rev948ServerCodecsMisc).
    s(27, "SPOTANIM_SPECIFIC", 10)                   // HANDLER_ID_CONFIRMED size-drifted (2026-06-25 audit) — handler ZoneUpdates::SPOTANIM_SPECIFIC; size drift from beta is a false alarm, behaviour confirms the official SPOTANIM_SPECIFIC. sz10.
    s(28, "VARP_LARGE", 6)                           // CONFIRMED — official enum jag::ServerProt::VARP_LARGE (beta @0xa36e40, sz6). Handler FUN_00119910, PlayerVarDomain::set. Wire: value(BE int) + id(BE u16). 947-3 op 111. Encoder in Rev948ServerCodecsVariable.
    s(29, "CLANSETTINGS_FULL", -2)                   // CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_FULL (beta @0xa36f40, sz-2). Was op 104 in 947-3.
    s(30, "IF_SETGRAPHIC", 8)                        // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00193f10 (DB IF_SET2DANGLE) decodes as the official IF_SETGRAPHIC. sz8. (Encoder IfSet2DAngle registered here; canonical name wins for display.)
    s(31, "UNKNOWN_31", 6)                           // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM' placeholder. Handler FUN_00186f40: build-area tile + id + height/delay. Graphic/spotanim at tile. sz6.
    s(32, "IF_SETCOLOUR", 8)                         // CONF:HIGH — official enum jag::ServerProt::IF_SETCOLOUR (beta @0xa36580). Handler @ 0x00185850.
    s(33, "UNKNOWN_33", -1)                          // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_PRIVATE_IN' placeholder. Handler FUN_001a0f40: type byte, sender/recipient strings, ChatHistory::AddChat(type=0x14). Private-message-receive family. VarByte.
    s(34, "UNKNOWN_34", 8)                           // CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH' (Audio fn; not in beta enum). Handler @ 0x00176600. sz8.
    s(35, "UNKNOWN_35", 12)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETEVENTS2' (beta has IF_SETEVENTS only; op97 holds the official IF_SETEVENTS). 948 second variant, no beta enum. Was op 35 in 947-3. sz12.
    s(36, "UNKNOWN_36", 10)                          // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_COORD' placeholder. Handler FUN_00186c60: two build-area tiles + id + delay. Projectile/proj-anim family. sz10.
    s(37, "MESSAGE_FRIENDCHANNEL", -1)               // CONF:HIGH — official enum jag::ServerProt::MESSAGE_FRIENDCHANNEL (beta @0xa36900). Was op 7 in 947-3.
    s(38, "UNKNOWN_38", 8)                           // CONF:NONE — no official beta enum maps. Prior 'IF_SET_MODEL_FRAME' (not in beta enum). Handler @ 0x00185b70. sz8.
    s(39, "IF_OPENTOP", 6)                           // HANDLER_ID_HIGH (2026-06-25 binary re-verify) — handler @0x001941d0 update-type 0xc (topLevelId + subId) = IF_OPENTOP. (Pass2 mislabel IF_SETSCROLLPOS corrected; the real IF_SETSCROLLPOS is op179.) Encoder IfOpenTop correctly bound here.
    s(40, "IF_MOVESUB", 8)                           // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00186100 (DB IF_SUBSWAP) decodes as the official IF_MOVESUB. sz8. (Encoder IfSubSwap registered here; canonical name wins for display.)
    s(41, "UPDATE_ZONE_PARTIAL_FOLLOWS", 3)          // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_FOLLOWS (beta @0xa36bc0, sz3). Was op 57 in 947-3.
    s(42, "UNKNOWN_42", 10)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_PACKED' placeholder. Handler FUN_001875f0: packed 4B coord + entity spawn + delay. Graphic/spotanim variant. sz10.
    s(43, "UNKNOWN_43", 12)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY' placeholder. Handler FUN_00157080: g4_alt3 target-ref + spotanim on entity-or-tile. SPOTANIM-on-entity family. sz12.
    s(44, "UPDATE_STAT", 6)                          // CONFIRMED — official enum jag::ServerProt::UPDATE_STAT (beta @0xa359c0, sz6). Handler jag::game::StatTable::UpdateStat @ 0x000ef2a0. 947-3 op 66. Wire: xp LE int + raw level + byteInverse skillId.
    s(45, "UNKNOWN_45", 1)                           // CONF:NONE — no official beta enum maps. Prior 'SET_MULTIWAY_STATE' (Misc fn; not in beta enum). Handler: was op 77 in 947-3. sz1.
    s(46, "OBJ_ADD", 5)                              // CONF:HIGH — official enum jag::ServerProt::OBJ_ADD (beta @0xa35b00, sz5). Handler @ 0x00119240.
    s(47, "CLIENT_SETVARC_SMALL", 3)                 // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_SMALL (beta @0xa38380, sz3). Handler FUN_001196f0, IfaceMgr direct. Wire: value(byteAdd) + id(LE u16). 947-3 op 1. Encoder in Rev948ServerCodecsVariable.
    s(48, "CLIENT_SETVARCBIT_SMALL", 3)              // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_SMALL (beta @0xa37a40, sz3). Handler FUN_00119560. Wire: value(raw 1B) + id(BE u16, lo-byte byteAdd). Encoder in Rev948ServerCodecsVariable.
    s(49, "UNKNOWN_49", -2)                          // MEDIUM (2026-06-25 audit) — handler jag::packethandlers::Lobby::CHANGE_LOBBY suggests LOBBY_APPEARANCE but only medium-confidence; demoted to UNKNOWN. Encoder ChangeLobby (Rev948ServerCodecsMisc) owns this opcode at runtime → displayed name resolves to CHANGE_LOBBY via pascalToScreamingSnake. VarShort.
    s(50, "LOC_CUSTOMISE", -1)                       // CONF:HIGH — official enum jag::ServerProt::LOC_CUSTOMISE (beta @0xa37700). Handler @ 0x0013f5f0.
    s(51, "VARBIT_LARGE", 6)                         // CONF:HIGH — official enum jag::ServerProt::VARBIT_LARGE (beta @0xa38100, sz6). Handler FUN_001197b0, setBitFromPacket. Prior 'VARP_BIT_LARGE' was a non-official descriptor. Wire: id(BE u16) + value(intInverseMiddle). Encoder in Rev948ServerCodecsVariable.
    s(52, "NPC_INFO", -2)                            // CONF:HIGH — official enum jag::ServerProt::NPC_INFO (beta @0xa38600). ProcessNpcInfo @ 0x001d79a0. Was op 12 in 947-3.
    s(53, "UNKNOWN_53", -2)                          // CONF:NONE — genuinely UNBOUND: no handler in any of the 6 binders; manager slot 0x13a16c0 has zero xrefs. Client allocates a varShort ProtEntry but installs no handler => op53 payload is read-and-discarded.
    s(54, "UNKNOWN_54", -1)                          // LOW (2026-06-25 audit) — handler Misc::NO_TIMEOUT only LOW-confidence; demoted to UNKNOWN. Encoder NoTimeout (Rev948ServerCodecsMisc) owns this opcode at runtime → displayed name resolves to NO_TIMEOUT via pascalToScreamingSnake. VarByte.
    s(55, "UNKNOWN_55", 0)                           // CONF:NONE — no official beta enum maps. Prior 'DESTROY_ZONE_DATA' (not in beta enum). Handler @ 0x000ef4e0. sz0.
    s(56, "MESSAGE_QUICKCHAT_PRIVATE", -1)           // CONF:HIGH — official enum jag::ServerProt::MESSAGE_QUICKCHAT_PRIVATE (beta @0xa37e00). Was op 73 in 947-3.
    s(57, "UNKNOWN_57", 6)                           // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_2' placeholder. Handler FUN_00186e60: build-area tile + id + signed delay. Graphic/spotanim-at-tile. sz6.
    s(58, "UNKNOWN_58", 0)                           // CONF:NONE — no official beta enum maps. Prior 'RESET_CLIENT_STATE' (not in beta enum). Handler @ 0x000f4080. sz0.
    s(59, "UNKNOWN_59", 10)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCMODEL' (not in beta enum). Handler @ 0x00193c50. sz10.
    s(60, "UNKNOWN_60", 25)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_SELF' (beta IF_SETPLAYERMODEL_SELF is sz4 @op148; this 948 op60 sz25 differs). Handler @ 0x00186520. sz25.
    s(61, "VARP_SMALL", 3)                           // CONFIRMED — official enum jag::ServerProt::VARP_SMALL (beta @0xa37400, sz3). Handler FUN_001199b0, PlayerVarDomain::set. 947-3 op 10. Wire: short(id) + byteInverse value. Encoder in Rev948ServerCodecsVariable.
    s(62, "IF_CLOSESUB", 4)                          // CONF:HIGH — official enum jag::ServerProt::IF_CLOSESUB (beta @0xa38500, sz4). Prior 'IF_CLOSESUB_ACTIVE' added an invented suffix; handler is the carried-over 947-3 op33 IF_CLOSESUB.
    s(63, "LOC_ADD_CHANGE", 10)                      // HANDLER_ID_CONFIRMED size-drifted (2026-06-25 audit) — handler ZoneUpdates::LOC_ADD_CHANGE (DecodePackedCoord + LocationContainer::Add); behaviour confirms the official LOC_ADD_CHANGE. sz10.
    s(64, "CLIENT_SETVARC_LARGE", 6)                 // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_LARGE (beta @0xa35a80, sz6). Handler FUN_00119600, IfaceMgr direct. Wire: value(intMiddle) + id(shortAddLittle). 947-3 op 112. Encoder in Rev948ServerCodecsVariable.
    s(65, "MAP_PROJANIM", 20)                        // CONF:HIGH — official enum jag::ServerProt::MAP_PROJANIM (beta @0xa37080). Was op 47 in 947-3.
    s(66, "UNKNOWN_66", 5)                           // CONF:NONE — no official beta enum maps. Prior 'CAM_LOOKAT' (beta CAM_LOOKAT is sz6 @op99; this 948 op66 sz5 differs). Handler FUN_00187160: byte + g4_alt2 -> camera obj. Camera sub-op. sz5.
    s(67, "UNKNOWN_67", -2)                          // UNKNOWN (2026-06-25 oracle) — prior CLANCHANNEL_FULL had the wrong size; the official CLANCHANNEL_FULL is op9 (HANDLER_ID_HIGH). Handler Clans::CLANCHANNEL_FULL here not size-confirmed. VarShort.
    s(68, "UNKNOWN_68", 10)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETMODELORIGIN' (not in beta enum). Handler @ 0x00193420. sz10.
    s(69, "CLIENT_SETVARCBIT_LARGE", 6)              // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_LARGE (beta @0xa38300, sz6). Handler FUN_001194b0. Wire: id(BE u16) + value(BE int). Encoder in Rev948ServerCodecsVariable.
    s(70, "UNKNOWN_70", 25)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_OTHER' (beta IF_SETPLAYERMODEL_OTHER is sz10 @op82; this 948 op70 sz25 differs). Handler @ 0x00186710. sz25.
    s(71, "OBJ_REVEAL", 7)                           // CONF:HIGH — official enum jag::ServerProt::OBJ_REVEAL (beta @0xa35fc0, sz7). Handler @ 0x000f37b0.
    s(72, "CAM2_ENABLE", 1)                          // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00186f60 (DB CAM_FORCEANGLE): 1 boolean byte -> Camera::ProcessCameraReset = the official CAM2_ENABLE. sz1.
    s(73, "CHAT_FILTER_SETTINGS", 2)                 // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::UNKNOWN_op0x49_handler decodes as the official CHAT_FILTER_SETTINGS. sz2.
    s(74, "UNKNOWN_74", 4)                           // UNKNOWN (2026-06-25 audit) — handler Misc::SET_DISPLAY_INT, no official enum maps. ADJUDICATE: oracle places JCOINS_UPDATE at op191 (HANDLER_ID_HIGH), but the JcoinsUpdate ENCODER is still bound to op74 (Rev948ServerCodecsMisc) → displayed name resolves to JCOINS_UPDATE via pascalToScreamingSnake. sz4.
    s(75, "UNKNOWN_75", 0)                           // CONF:NONE — no official beta enum maps. Prior 'SET_READY_FLAG' (ClientState fn; not in beta enum). Handler: was op 65 in 947-3. sz0.
    s(76, "UPDATE_ZONE_PARTIAL_ENCLOSED", -2)        // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_ENCLOSED (beta @0xa37d00, sz-2). Was op 126 in 947-3.
    s(77, "CAMERA_UPDATE", -2)                       // CONF:HIGH — official enum jag::ServerProt::CAMERA_UPDATE (beta @0xa36e00, sz-2). Handler 0x001d3b50 verified: bitflag-driven camera update (pos/zoom/lookat/rotate). Prior 'CAM_UPDATE' was the C++ function name.
    s(78, "UPDATE_ZONE_FULL_FOLLOWS", 3)             // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_FULL_FOLLOWS (beta @0xa37d40, sz3). Was op 18 in 947-3.
    s(79, "UNKNOWN_79", -1)                          // LOW (2026-06-25 audit) — handler NPCInfo::NPC_HEADICON_SPECIFIC only LOW-confidence; demoted to UNKNOWN. No encoder. VarByte.
    s(80, "UPDATE_RUNENERGY", 1)                     // CONF:HIGH — official enum jag::ServerProt::UPDATE_RUNENERGY (beta @0xa38240, sz1). Handler 0x00174f40 verified: reads 1 byte -> player +0x60 (run energy). Prior 'SET_RUN_ENERGY' was a fabricated rename.
    s(81, "REBUILD_NORMAL", -2)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::REBUILD_NORMAL_SIMPLE decodes as the official REBUILD_NORMAL (world-login simple form; op199 is the multi-scene form, same official name). Encoder RebuildNormalSimple registered here; canonical name wins for display. VarShort.
    s(82, "IF_OPENSUB", 23)                          // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00189300 (DB IF_SETPOSITION): byte + 2B compid + 4B value (sz23), open-sub alloc = the official IF_OPENSUB. ADJUDICATE: the IfSetPosition ENCODER is still bound to op82 (Rev948ServerCodecsInterface) — encoder/handler-identity now disagree (op82<->op94 swap). Display name (canonical IF_OPENSUB) is correct; the encoder binding may need to move to op94.
    s(83, "REBUILD_REGION", -2)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::REBUILD_REGION_ALT (cmd=5 gate, bit-packed coords, SceneManager build) decodes as the official REBUILD_REGION. VarShort.
    s(84, "IF_SETOBJECT", 10)                        // CONF:HIGH — official enum jag::ServerProt::IF_SETOBJECT (beta @0xa36840, sz10). Handler @ 0x00185a00.
    s(85, "UPDATE_INV_FULL", -2)                     // CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_FULL (beta @0xa36040, sz-2). Handler Inventory::UPDATE_INV_FULL_impl. Was op 69 in 947-3.
    s(86, "UNKNOWN_86", 10)                          // MISASSIGNED (2026-06-25 audit) — DB IF_SETANIM is part of the IF_SET* family the beta refactored into a unified SetComponentProperty(update-type) dispatch; no confident per-opcode official name, demoted to UNKNOWN. This is the SetComponentProperty propType-3 packet, NOT IF_SETANIM (that is op103). Encoder IfSetComponentProp3 bound here (Rev948ServerCodecsInterface) → displayed name resolves to IF_SET_COMPONENT_PROP3 via pascalToScreamingSnake. sz10.
    s(87, "CAM_RESET", 0)                            // CONF:HIGH — official enum jag::ServerProt::CAM_RESET (beta @0xa37800, sz0). Was op 89 in 947-3.
    s(88, "CAM_REMOVEROOF", 4)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler Camera::CAM_LOOKAT (g4_alt3 packed coord, -1 clears) decodes as the official CAM_REMOVEROOF. sz4.
    s(89, "NPC_ANIM_SPECIFIC", 19)                   // HANDLER_ID_HIGH (2026-06-25 audit) — handler NPCInfo::NPC_SPOTANIM (NPCList::GetNPCNode + per-NPC anim vtable) decodes as the official NPC_ANIM_SPECIFIC. sz19.
    s(90, "UNKNOWN_90", -1)                          // CONF:NONE — no official beta enum maps. Prior 'LOC_ADD' (beta has LOC_ADD_CHANGE, not LOC_ADD). Handler @ 0x00139080. VarByte.
    s(91, "IF_SETHIDE", 5)                           // CONF:HIGH — official enum jag::ServerProt::IF_SETHIDE (beta @0xa36100, sz5). Was op 103 in 947-3.
    s(92, "CLIENT_SETVARCSTR_SMALL", -1)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler Misc::SET_VARC_STR_SMALL decodes as the official CLIENT_SETVARCSTR_SMALL. Encoder ClientSetVarcStr registered here; canonical name wins for display. VarByte.
    s(93, "MESSAGE_GAME", -1)                        // CONF:HIGH — official enum jag::ServerProt::MESSAGE_GAME (beta @0xa365c0). Was op 105 in 947-3.
    s(94, "IF_SETPOSITION", 8)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00194280 (DB IF_OPENSUB): two signed shorts (x,y) + compid, update-type 0xB (swap-partner of op82) = the official IF_SETPOSITION. ADJUDICATE: the IfOpenSub ENCODER is still bound to op94 (Rev948ServerCodecsInterface) — encoder/handler-identity now disagree (op82<->op94 swap). Display name (canonical IF_SETPOSITION) is correct; the encoder binding may need to move to op82.
    s(95, "MIDI_SONG", 5)                            // CONF:HIGH — official enum jag::ServerProt::MIDI_SONG (beta @0xa36b00). Was op 87 in 947-3.
    s(96, "UNKNOWN_96", 4)                           // CONF:NONE — no official beta enum maps. Prior 'IF_SETANIM_ACTIVE' (not in beta enum). Handler @ 0x00185800. sz4.
    s(97, "IF_SETEVENTS", 10)                        // CONF:HIGH — official enum jag::ServerProt::IF_SETEVENTS (beta @0xa35e80). Was op 34 in 947-3. (op35 is a 948 second variant -> UNKNOWN_35.)
    s(98, "UNKNOWN_98", 25)                          // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL' placeholder. Handler FUN_000f25d0: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family. sz25.
    s(99, "IF_SETRECOL", 6)                          // CONF:HIGH — official enum jag::ServerProt::IF_SETRECOL (beta @0xa376c0). Handler @ 0x00193860.
    s(100, "UNKNOWN_100", 4)                         // CONF:NONE — no official beta enum maps. Prior 'RUNCLIENTSCRIPT_SHORT': beta RUNCLIENTSCRIPT already maps to op110. This is a 948 short variant, no distinct beta enum. Handler FUN_00187020. sz4.
    s(101, "IF_SETPLAYERMODEL_SELF", 4)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00185b20 (DB IF_SETOBJECT_ACTIVE) decodes as the official IF_SETPLAYERMODEL_SELF. sz4. (Encoder IfSetObjectActive registered here; canonical name wins for display.)
    s(102, "IF_SETMODEL", 8)                         // CONF:HIGH — official enum jag::ServerProt::IF_SETMODEL (beta @0xa36540, sz8). Handler @ 0x001858e0.
    s(103, "IF_SETANIM", 8)                          // HANDLER_ID_HIGH (2026-06-25 oracle) — handler Interfaces::IF_SETGRAPHIC (DB label) decodes as the official IF_SETANIM. The official IF_SETGRAPHIC is op30. sz8.
    s(104, "HINT_ARROW", 14)                         // HANDLER_ID_HIGH (2026-06-25 audit) — handler PlayerInfo::PLAYER_INFO_DECODE decodes as the official HINT_ARROW. sz14.
    s(105, "MESSAGE_CLANCHANNEL", -1)                // CONF:HIGH — official enum jag::ServerProt::MESSAGE_CLANCHANNEL (beta @0xa361c0, sz-1). Handler @ 0x001a1cc0.
    s(106, "UNKNOWN_106", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_ORIGIN' (NPC_UPDATE_ORIGIN fn; not in beta enum). Handler: was op 61 in 947-3. sz1.
    s(107, "OBJ_DEL", 3)                             // CONF:HIGH — official enum jag::ServerProt::OBJ_DEL (beta @0xa36b40, sz3). Handler @ 0x00152b00.
    s(108, "CLANSETTINGS_DELTA", -2)                 // CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_DELTA (beta @0xa38640, sz-2). Was op 63 in 947-3.
    s(109, "UPDATE_UID192", 28)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler PlayerInfo::HANDSHAKE_UID (24B UID + 4B CRC32, sz28) decodes as the official UPDATE_UID192. sz28.
    s(110, "RUNCLIENTSCRIPT", -2)                    // CONF:HIGH — official enum jag::ServerProt::RUNCLIENTSCRIPT (beta @0xa37ec0, sz-2). Handler ClientState::RUNCLIENTSCRIPT_impl @ 0x001d2060. Was op 121 in 947-3.
    s(111, "UNKNOWN_111", 6)                         // CONF:NONE — no official beta enum maps. Prior 'CAM_SUB' placeholder. Handler FUN_00175960: 5 bytes -> camera obj vtable. Camera sub-op. sz6.
    s(112, "UPDATE_REBOOT_TIMER", 2)                 // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::SET_TICK_TIMER decodes as the official UPDATE_REBOOT_TIMER. sz2. (op184, prior holder of this name, demoted to UNKNOWN.)
    s(113, "MAP_ANIM", 11)                           // GUARANTEED (2026-06-25 audit) — handler ZoneUpdates::MAP_ANIM. Encoder MapAnim registered here; canonical name wins for display. sz11.
    s(114, "MESSAGE_CLANCHANNEL_SYSTEM", -1)         // HANDLER_ID_HIGH (2026-06-25 audit) — handler Chat::CHAT_FILTER_SETTINGS actually adds a clan-channel system message (channel type 19) = the official MESSAGE_CLANCHANNEL_SYSTEM. VarByte.
    s(115, "IF_SETPLAYERHEAD_IGNOREWORN", 10)        // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00185660 (DB IF_SETNPCHEAD) decodes as the official IF_SETPLAYERHEAD_IGNOREWORN. sz10. (Encoder IfSetNpcHead registered here; canonical name wins for display.)
    s(116, "UNKNOWN_116", -2)                        // CONF:NONE — no official beta enum maps. Prior 'SET_VARC_STR_LARGE': beta has CLIENT_SETVARCSTR_LARGE (sz-2 @op188) but the op92/op116 small/large split is a 948 reorg not present in beta; identity unverified. Handler FUN_001500a0 IfaceMgr type=2. Encoder in Rev948ServerCodecsVariable. VarShort.
    s(117, "UNKNOWN_117", -1)                        // UNKNOWN (2026-06-25 oracle) — prior CLANCHANNEL_DELTA had the wrong size; the official CLANCHANNEL_DELTA is op124 (HANDLER_ID_HIGH). Handler Clans::CLANCHANNEL_DELTA here not size-confirmed. VarByte.
    s(118, "IF_OPENSUB_ACTIVE_OBJ", 29)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x001da3f0 (DB IF_SETPLAYERMODEL_SNAPSHOT): DecodePackedCoord -> (float)(coord<<9) (sz29) = the official IF_OPENSUB_ACTIVE_OBJ. sz29. (Encoder IfSetPlayerModelSnapshot registered here; canonical name wins for display.)
    s(119, "UNKNOWN_119", 35)                        // CONF:NONE — no official beta enum maps. Prior 'CUTSCENE_DATA' (beta CUTSCENE is sz-2 @op92; this 948 op119 sz35 fixed differs). Was op 91 in 947-3. sz35.
    s(120, "CAM_SMOOTHRESET", 0)                     // CONF:HIGH — official enum jag::ServerProt::CAM_SMOOTHRESET (beta @0xa37cc0, sz0). Was op 80 in 947-3.
    s(121, "UPDATE_INV_PARTIAL", -2)                 // CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_PARTIAL (beta @0xa37c00, sz-2). Was op 5 in 947-3.
    s(122, "IF_SETTEXT", -2)                         // CONF:HIGH — official enum jag::ServerProt::IF_SETTEXT (beta @0xa37f40, sz-2). Handler @ 0x00185ec0.
    s(123, "TRIGGER_ONDIALOGABORT", 0)              // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00185620 (DB IF_TRIGGER_CLOSE) decodes as the official TRIGGER_ONDIALOGABORT (also at op162/GUARANTEED). sz0. (Encoder IfTriggerClose registered here; canonical name wins for display.)
    s(124, "CLANCHANNEL_DELTA", -2)                  // HANDLER_ID_HIGH (2026-06-25 oracle) — handler Chat::CLANSETTINGS_DELTA_CHAT (DB label) decodes as the official CLANCHANNEL_DELTA. The official CLANSETTINGS_DELTA is op108 (HANDLER_ID_HIGH). VarShort.
    s(125, "OBJ_COUNT", 7)                           // CONF:HIGH — official enum jag::ServerProt::OBJ_COUNT (beta @0xa36880, sz7). Handler @ 0x001190d0.
    s(126, "UNKNOWN_126", -1)                        // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_FRIENDCHAT' (beta has MESSAGE_FRIENDCHANNEL only; verified no _FRIENDCHAT enum). Was op 21 in 947-3. VarByte.
    s(127, "UNKNOWN_127", 0)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET' placeholder. Handler FUN_00182610: FUN_0066ecb0(graphicsObj,8). Minimap/camera reset. sz0.
    s(128, "UNKNOWN_128", 0)                         // CONF:NONE — no official beta enum maps. Prior 'NOOP_128' placeholder. Handler FUN_00173e40: empty (no read). No-op/reserved. sz0.
    s(129, "UNKNOWN_129", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE' placeholder. Handler FUN_001dc630: entity-group-at-tile, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(130, "UNKNOWN_130", -1)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_IGNORELIST': beta UPDATE_IGNORELIST is sz-2 @op10 but handler 0x001d29c0 is a 64-bit-flag relationship delta (suspected true UPDATE_FRIENDLIST), identity contested. Encoder DISABLED. VarByte.
    s(131, "UNKNOWN_131", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP' placeholder. Handler FUN_000f9600: per-worldentity record list move/reorder. WorldEntity-list family. sz3.
    s(132, "UNKNOWN_132", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_STOP' (Audio fn; not in beta enum). Handler @ 0x0017e4c0. sz2.
    s(133, "UNKNOWN_133", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_FLAG' (NPC_UPDATE_FLAG fn; not in beta enum). Handler: was op 138 in 947-3. sz1.
    s(134, "UNKNOWN_134", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NPC_SAY' placeholder. Handler FUN_00179460: NPCList::GetNPCNode + string -> NPC overhead-text. NPC overhead say/text. VarShort.
    s(135, "UNKNOWN_135", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NOOP_135' placeholder. Handler FUN_001758f0: empty. No-op/discard. VarShort.
    s(136, "UNKNOWN_136", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETANIM_SMALL' (not in beta enum). Handler @ 0x00185aa0. sz5.
    s(137, "UNKNOWN_137", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_A' (Chat fn; not in beta enum). Handler: was op 217 in 947-3. sz1.
    s(138, "UNKNOWN_138", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SKIP_2_BYTES' (Misc fn). Handler @ 0x00175f90. sz2.
    s(139, "UNKNOWN_139", 0)                         // MISASSIGNED (2026-06-25 audit) — DB Misc::LOGOUT_TRANSFER label not behaviourally confirmed; no confident official name, demoted to UNKNOWN. No encoder. sz0.
    s(140, "UNKNOWN_140", 17)                        // CONF:NONE — no official beta enum maps. Prior 'PLAYER_SPOTANIM' placeholder. Handler FUN_000f2280: spotanim on local player via vtable. SPOTANIM-on-local-player family. sz17.
    s(141, "UNKNOWN_141", -2)                        // CONF:NONE — no official beta enum maps. Prior 'IF_OPENSUB_THUNK' (thunk fn-name; beta IF_OPENSUB already maps to op94). Handler Interfaces::IF_OPENSUB_thunk. Was op 186 in 947-3. VarShort.
    s(142, "UNKNOWN_142", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SET_URL_STRING' (Misc fn; beta has URL_OPEN/UPDATE_URL_STRING-style but not SET_URL_STRING). Was op 200 in 947-3. VarByte.
    s(143, "UNKNOWN_143", 6)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_ADD' placeholder. Handler FUN_00119e20: insert/update worldentity record. WorldEntity-list family. sz6.
    s(144, "UNKNOWN_144", 2)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL' placeholder. Handler FUN_00119c30: remove worldentity record by index. WorldEntity-list family. sz2.
    s(145, "UNKNOWN_145", 2)                         // CONF:NONE — no official beta enum maps. Prior 'DEV_CONSOLE_CMD' placeholder. Handler FUN_00187890: u16 subcmd switch (clear list/heightmap/camera/flags). Dev/debug dispatcher. sz2.
    s(146, "UNKNOWN_146", 2)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL_2' placeholder. Handler FUN_000f98f0: remove by index. WorldEntity-list family. sz2.
    s(147, "UNKNOWN_147", 10)                        // CONF:NONE — VETO: handler FUN_00141510/0x00141510 is decompile-certain (player-varp 8B value) but NO verbatim beta enum exists (beta var set = VARP_SMALL/VARP_LARGE only). Official enum name VARP_LONG rejected per matrix; sz10 authoritative. Encoder VarpLong in Rev948ServerCodecsVariable owns this opcode at runtime.
    s(148, "UNKNOWN_148", 2)                         // CONF:NONE — no official beta enum maps. Prior 'IF_CLOSESUB_BY_ID': beta IF_CLOSESUB already maps to op62. This sz2 sibling has no distinct beta enum. Handler @ 0x001855d0. sz2.
    s(149, "UNKNOWN_149", 6)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_INS' placeholder. Handler FUN_000f9a20: insert sentinel into worldentity lists. WorldEntity-list family. sz6.
    s(150, "UNKNOWN_150", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_2' placeholder. Handler FUN_001db620: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(151, "UNKNOWN_151", 21)                        // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::PROJANIM_SPECIFIC plausible but size-ambiguous; demoted to UNKNOWN. Encoder ProjAnimSpecific (Rev948ServerCodecsZone) owns this opcode at runtime → displayed name resolves to PROJ_ANIM_SPECIFIC via pascalToScreamingSnake. sz21.
    s(152, "IF_SET_HTTP_IMAGE", -1)                  // CONF:HIGH — official enum jag::ServerProt::IF_SET_HTTP_IMAGE (beta @0xa36380). Handler Interfaces::IF_SET_HTTP_IMAGE. Was op 146 in 947-3.
    s(153, "UNKNOWN_153", 4)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLOAT_AT_TILE' placeholder. Handler FUN_001dc100: entity-group, set float/flag. ZoneUpdate entity-overlay family. sz4.
    s(154, "UNKNOWN_154", 5)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_AT_TILE' placeholder. Handler FUN_001de6b0: entity-group, set anim timing. ZoneUpdate entity-overlay family. sz5.
    s(155, "LOGOUT", 0)                              // CONF:HIGH — official enum jag::ServerProt::LOGOUT (beta @0xa358c0, sz0). Handler Misc::LOGOUT. Was op 147 in 947-3.
    s(156, "UNKNOWN_156", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_B' (Chat fn; not in beta enum). Handler: was op 155 in 947-3. sz1.
    s(157, "UNKNOWN_157", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SCENE_FLAG' placeholder. Handler FUN_00173a50: byteInverse -> scene obj, mark dirty. Scene/minimap flag. sz1.
    s(158, "UNKNOWN_158", 9)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETSCROLLSIZE' (not in beta enum). Handler @ 0x00193660. sz9.
    s(159, "UNKNOWN_159", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NOOP_159' placeholder. Handler FUN_000ec3a0: empty. No-op/discard. VarShort.
    s(160, "UNKNOWN_160", 9)                         // CONF:NONE — no official beta enum maps. Prior 'NPC_HEADBAR_SPECIFIC' placeholder. Handler FUN_001d76a0: NPCList::GetNPCNode, per-slot arrays. NPC headbar/hitmark family. sz9.
    s(161, "PLAYER_GROUP_DELTA", -2)                 // CONF:HIGH — official enum jag::ServerProt::PLAYER_GROUP_DELTA (beta @0xa37640, sz-2). Handler 0x001989c0 verified: byte + payload -> player-group delta record. Prior 'UPDATE_PLAYER_GROUP' was a fabricated rename.
    s(162, "UNKNOWN_162", 0)                         // UNKNOWN (2026-06-25 oracle) — the official TRIGGER_ONDIALOGABORT is op123 (HANDLER_ID_HIGH); op162 handler ClientState::TRIGGER_ONDIALOGABORT (DB label) not behaviourally confirmed as that packet. sz0.
    s(163, "UNKNOWN_163", 15)                        // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY_2' placeholder. Handler FUN_001db050: g4_alt3 target-ref + spotanim. SPOTANIM-on-entity family. sz15.
    s(164, "MAP_PROJANIM_HALFSQ", 28)               // HANDLER_ID_HIGH (2026-06-25 audit) — handler ZoneUpdates::MAP_PROJANIM_HALT decodes as the official MAP_PROJANIM_HALFSQ. sz28. (Encoder MapProjAnimHalt registered here; canonical name wins for display.)
    s(165, "UNKNOWN_165", 14)                        // CONF:NONE — no official beta enum maps. Prior 'IF_SETMODEL_COORD' (not in beta enum). Handler @ 0x001939d0. sz14.
    s(166, "UNKNOWN_166", 5)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_ADD' placeholder. Handler FUN_00120150: RebuildSceneEntry::Reset + WorldList::InsertOrReplace. WorldEntity add/placement. sz5.
    s(167, "SYNTH_SOUND", 12)                        // CONF:HIGH — official enum jag::ServerProt::SYNTH_SOUND (beta @0xa35bc0). Handler Audio::SYNTH_SOUND @ 0x00187350.
    s(168, "UNKNOWN_168", -1)                        // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::SOUND_AREA plausible but size-ambiguous; demoted to UNKNOWN. Encoder SoundArea (Rev948ServerCodecsMisc) owns this opcode at runtime → displayed name resolves to SOUND_AREA via pascalToScreamingSnake. VarByte.
    s(169, "UNKNOWN_169", 8)                         // UNKNOWN (2026-06-25 audit) — was mislabeled SEND_PING in pass 1; CORRECTED: op169 reads a g8 server timestamp (latency delta) = an unknown timestamp-sync packet, NOT SEND_PING. The real SEND_PING is op174. No confident official name. No encoder. sz8.
    s(170, "UNKNOWN_170", 5)                         // CONF:NONE — no official beta enum maps. Prior 'LOC_MERGE' (not in beta enum). Handler @ 0x000eeba0. sz5.
    s(171, "UNKNOWN_171", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_2' placeholder. Handler FUN_000ef800: write sentinel into worldentity sublist. WorldEntity-list family. sz3.
    s(172, "UNKNOWN_172", 1)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_FLAG_A' placeholder. Handler FUN_000f03f0: store byte -> graphics obj +0xa0. Minimap/camera flag. sz1.
    s(173, "UNKNOWN_173", -2)                        // CONF:NONE — no official beta enum maps. Prior 'LOC_COMPOSITE_UPDATE' was invented (Ghidra handler literally named UNKNOWN_op173_handler @ 0x001ae100). Reads gT_ulong+u32+subcommand loop (cases 1..0xe alloc typed LOC/zone delta objs). ZoneUpdates composite multi-part update. VarShort.
    s(174, "SEND_PING", 8)                           // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::ServerProt::HandleAntiCheatChallenge reads two 4-byte challenge ints and replies = the official SEND_PING (beta-verified). NOT op169. sz8.
    s(175, "MESSAGE_PRIVATE_ECHO", -1)               // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE_ECHO (beta @0xa37a80). Was op 129 in 947-3.
    s(176, "UNKNOWN_176", 3)                         // CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_D' (Misc fn; not in beta enum). Was op 144 in 947-3. sz3.
    s(177, "UNKNOWN_177", 29)                        // CONF:NONE — no official beta enum maps. Prior 'PROJANIM_SPECIFIC_HALT' placeholder (beta PROJANIM_SPECIFIC sz22 @op140; this sz29 halt variant differs). Handler @ 0x000f1840. sz29.
    s(178, "UNKNOWN_178", -2)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_PLAYER_CHAT' (PlayerInfo fn; not in beta enum). Was op 178 in 947-3. VarShort.
    s(179, "IF_SETSCROLLPOS", 9)                      // HANDLER_ID_HIGH (2026-06-25 binary re-verify) — handler @0x001938e0 update-type 0x11 (scrollY g2_add / subSlot g1_sub / scrollX g2 / componentHash g4_alt3) = the real IF_SETSCROLLPOS. Encoder IfSetScrollPos correctly bound here. (op39 is IF_OPENTOP, not this.)
    s(180, "UNKNOWN_180", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETOBJECT_SMALL' (not in beta enum). Handler @ 0x00185980. sz5.
    s(181, "LOYALTY_UPDATE", 4)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::UNKNOWN_op0xB5_handler decodes as the official LOYALTY_UPDATE. sz4.
    s(182, "UNKNOWN_182", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_3' placeholder. Handler FUN_001dbb80: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(183, "UNKNOWN_183", 14)                        // CONF:NONE — no official beta enum maps. Prior 'MAP_ANIM_SPECIFIC' (not in beta enum). Handler @ 0x001575c0. sz14.
    s(184, "UNKNOWN_184", 4)                         // UNKNOWN (2026-06-25 audit) — handler Misc::SET_SYSUPDATE_TIMER, no official enum maps; the official UPDATE_REBOOT_TIMER is now op112 (HANDLER_ID_HIGH). No encoder. sz4.
    s(185, "MESSAGE_PRIVATE", -1)                    // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE (beta @0xa37e40). Was op 151 in 947-3.
    s(186, "UNKNOWN_186", -2)                        // CONF:NONE — no official beta enum maps. Prior 'REBUILD_WORLDENTITY' (not in beta enum). Was op 188 in 947-3. VarShort.
    s(187, "CREATE_CHECK_NAME_REPLY", 1)            // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::Lobby::CREATE_CHECK_NAME_REPLY. sz1.
    s(188, "SOCIAL_NETWORK_LOGOUT", -2)             // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::WebPage::SOCIAL_NETWORK_LOGOUT. VarShort.
    s(189, "UNKNOWN_189", 4)                         // CONF:NONE — no official beta enum maps. Prior 'VORBIS_PRELOAD' (Audio fn; beta has VORBIS_PRELOAD_SOUNDS/_SOUND_GROUP, not VORBIS_PRELOAD). Handler @ 0x001870f0. sz4.
    s(190, "UNKNOWN_190", 0)                         // CONF:NONE — no official beta enum maps. Prior 'CLEAR_PENDING_UPDATES' (not in beta enum). Handler @ 0x000f4110. sz0.
    s(191, "JCOINS_UPDATE", 4)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::UNKNOWN_op0xBF_handler decodes as the official JCOINS_UPDATE. ADJUDICATE: the JcoinsUpdate ENCODER is still bound to op74 (Rev948ServerCodecsMisc), so the encoder and handler-identity disagree on the opcode. sz4.
    s(192, "CREATE_CHECK_EMAIL_REPLY", 1)           // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::Lobby::CREATE_CHECK_EMAIL_REPLY. sz1.
    s(193, "UNKNOWN_193", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_C' (Misc fn; not in beta enum). Was op 174 in 947-3. sz1.
    s(194, "UNKNOWN_194", 1)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_3' placeholder. Handler FUN_000efa80: reorder/compact worldentity lists. WorldEntity-list family. sz1.
    s(195, "UNKNOWN_195", 4)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH_2' (Audio fn; not in beta enum). Handler @ 0x001870a0. sz4.
    s(196, "UNKNOWN_196", 10)                        // CONF:NONE — VETO: handler FUN_00119350/0x00119350 is decompile-certain (client-varc 8B BE value, InterfaceManager type=1) but NO verbatim beta enum exists (beta varc set = SMALL/LARGE only). Official enum name CLIENT_SETVARC_LONG rejected per matrix; sz10 authoritative. Framing-only stub (no encoder/data class).
    s(197, "UNKNOWN_197", -2)                        // CONF:NONE — no official beta enum maps. Prior 'SKIP_DATA' (Misc fn; not in beta enum). Was op 166 in 947-3. VarShort.
    s(198, "UNKNOWN_198", -1)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_URL_STRING' (Misc fn; not in beta enum). Was op 214 in 947-3. VarByte.
    s(199, "UNKNOWN_199", -2)                        // UNKNOWN (2026-06-25 oracle) — the official REBUILD_NORMAL is op81 (HANDLER_ID_HIGH); op199 handler ClientState::REBUILD_NORMAL (DB label, multi-scene grid form) is not the canonical REBUILD_NORMAL per the oracle. VarShort.
    s(200, "UNKNOWN_200", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_STOP' (Audio fn; beta has VORBIS_SOUND_GROUP_STOP, not SOUND_GROUP_STOP). Handler @ 0x00176000. sz2.
    s(201, "UNKNOWN_201", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_4' placeholder. Handler FUN_000ef970: write sentinel/value into worldentity sublist. WorldEntity-list family. sz3.
    s(202, "UNKNOWN_202", -2)                        // CONF:NONE — no official beta enum maps. Prior 'PLAYER_INFO_DECODE_2' (PlayerInfo fn; not in beta enum). Was op 180 in 947-3. VarShort.
    s(203, "UNKNOWN_203", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_RESET_AT_TILE' placeholder. Handler FUN_001dd3e0: entity-group, set flag + anim field. ZoneUpdate entity-overlay family. sz3.
    s(204, "UNKNOWN_204", 1)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_FLAG_B' placeholder. Handler FUN_000f0380: store byteInverse -> graphics obj +0xa4. Minimap/camera flag. sz1.
    s(205, "UNKNOWN_205", 6)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_SPEED' (Audio fn; not in beta enum). Handler @ 0x00182690. sz6.
    s(206, "UNKNOWN_206", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCHEAD_ACTIVE' (not in beta enum). Handler @ 0x001935c0. sz5.
    s(207, "UNKNOWN_207", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_5' placeholder. Handler FUN_00119b80: reorder/compact worldentity list. WorldEntity-list family. sz3.
    s(208, "UNKNOWN_208", -2)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_INV_GROUP' (Inventory fn; beta has UPDATE_INV_FULL/_PARTIAL/_STOP_TRANSMIT, not _GROUP). Was op 177 in 947-3. VarShort.
    s(209, "UNKNOWN_209", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NPC_INFO_THUNK' (thunk fn-name; beta NPC_INFO maps to op52). Handler NPCInfo::NPC_INFO_thunk_worldentity. Was op 205 in 947-3. VarShort.
    s(210, "UNKNOWN_210", 6)                         // CONF:NONE — no official beta enum maps. Prior 'VORBIS_SONG' (Audio fn; beta has VORBIS_SOUND/_SOUND_GROUP/etc, not VORBIS_SONG). Handler @ 0x00187750. sz6.
    s(211, "UNKNOWN_211", 33)                        // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL_2' placeholder. Handler FUN_000f1420: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family (largest). sz33.
    s(212, "UNKNOWN_212", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SET_WORLD_TARGET' (WorldData fn; not in beta enum). Was op 187 in 947-3. VarByte.
    s(213, "UNKNOWN_213", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SWITCH_WORLD' (WorldData fn; not in beta enum). Was op 179 in 947-3. VarByte.
    s(214, "UNKNOWN_214", 0)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET_2' placeholder. Handler FUN_00182650: FUN_00c2a9a0(graphicsObj,1). Camera/minimap reset. sz0.
    s(215, "UNKNOWN_215", 8)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_AT_TILE_2' placeholder. Handler FUN_001deaa0: g4 + entity-group, set fields. ZoneUpdate entity-overlay family. sz8.
    s(216, "WORLDLIST_FETCH_REPLY", -2)              // CONF:HIGH — official enum jag::ServerProt::WORLDLIST_FETCH_REPLY (beta @0xa37f80, sz-2). Handler WorldData::WORLDLIST_FETCH_REPLY @ 0x0018fea0. Was op 159 in 947-3.
    s(217, "UNKNOWN_217", 4)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_MODIFY' (Audio fn; not in beta enum). Handler @ 0x00175c90. sz4.
}

private fun Codec.s(opcode: Int, name: String, size: Int) {
    val protSize = when (size) {
        -1 -> ProtSize.VarByte
        -2 -> ProtSize.VarShort
        else -> ProtSize.Fixed(size)
    }
    // Canonical official UPPER_SNAKE name wins for the DISPLAYED name; the encoder's authoritative
    // size (when present) is preserved. See Codec.serverProtStub.
    serverProtStub(opcode, name, protSize)
}
