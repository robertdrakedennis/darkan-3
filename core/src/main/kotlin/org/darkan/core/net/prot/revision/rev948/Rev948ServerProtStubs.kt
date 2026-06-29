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
    s(0, "MESSAGE_QUICKCHAT_PRIVATE", -1)                            // UNKNOWN (2026-06-25 oracle) — prior MESSAGE_QUICKCHAT_CLANCHANNEL was a binder-mislabel; the official MESSAGE_QUICKCHAT_CLANCHANNEL is op2 (HANDLER_ID_HIGH). Handler Chat::MESSAGE_QUICKCHAT_CLANCHANNEL not behaviourally confirmed here. VarByte.
    s(1, "SET_MOVEACTION", -1)                            // CONF:NONE — no official beta enum maps. Prior fn-name 'SET_NPC_OP' (NPC_OP fn). Handler: was op 24 (NPC_OP) in 947-3. VarByte.
    s(2, "MESSAGE_QUICKCHAT_CLANCHANNEL", -1)        // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT decodes as the official MESSAGE_QUICKCHAT_CLANCHANNEL clan-channel quickchat. VarByte.
    s(3, "IF_OPENTOP", 19)                            // GHIDRA_9485 — descriptor 0x100f0f2b0; handler jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE_OP3 @0x1000a2740 decodes top-level interface id from payload[9] byteAdd low + payload[10] high, stores interface manager +0xf8, and clears stale top-level refs. Name stays UNKNOWN_3 because no official beta enum symbol maps.
    s(4, "IF_OPENSUB_ACTIVE_LOC", 32)                // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x001da640 (DB IF_SETANGLE): BuildArea::DecodePackedCoord + ints + compid (sz32) = IF_OPENSUB_ACTIVE_LOC. Was op 117 in 947-3.
    s(5, "RESET_CLIENT_VARCACHE", 0)                 // GHIDRA_9485 — descriptor 0x100f0f330; handler jag::packethandlers::ClientState::RESET_CLIENT_VARCACHE_OP5 @0x100048920 resets PlayerVarDomain at Client+0x19dc8 and marks client var/cache state dirty. Encoder ResetClientVarcache owns this opcode at runtime.
    s(6, "LOC_ANIM", 7)                             // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::LOC_PREFETCH plausible but size-ambiguous; demoted to UNKNOWN. Encoder LocPrefetch (Rev948ServerCodecsZone) owns this opcode at runtime, so the displayed name resolves to LOC_PREFETCH via pascalToScreamingSnake.
    s(7, "RESET_ANIMS", 0)                             // GHIDRA_9485 — descriptor 0x100f0f3b0; handler jag::packethandlers::Misc::RESET_ENTITY_LISTS_OP7 @0x100084290 walks player/NPC manager lists and invokes entity vfunc +0x1a0 for active entries. Name stays UNKNOWN_7 because no official beta enum symbol maps.
    s(8, "IF_SETTEXTANTIMACRO", 5)                             // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERHEAD_ACTIVE' (not in beta enum). Handler @ 0x00193530. sz5.
    s(9, "CLANCHANNEL_FULL", -2)                     // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::CLANCHANNEL_FULL_CHAT decodes as the official CLANCHANNEL_FULL. VarShort.
    s(10, "VARBIT_SMALL", 3)                         // CONF:HIGH — official enum jag::ServerProt::VARBIT_SMALL (beta @0xa38140, sz3). Handler FUN_00119870 setBitFromPacket. Prior 'VARP_BIT_SMALL' was a non-official descriptor. Wire: id(BE u16) + value(-128-byte). Encoder in Rev948ServerCodecsVariable.
    s(11, "MESSAGE_PUBLIC", -2)                      // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PUBLIC (beta @0xa38580). Was op 45 in 947-3.
    s(12, "UPDATE_RUNWEIGHT", 2)                     // GHIDRA_9485 — handler jag::packethandlers::Misc::UPDATE_RUNWEIGHT @0x100044a50 reads signed BE g2s -> player run weight (status +0x1c). Prior 'SET_PLAYER_OP_2' was stale.
    s(13, "UPDATE_RUNENERGY", 1)                     // CORRECTED 2026-06-27 (recorder-capture-points.md §10.4) — official enum jag::ServerProt::UPDATE_RUNENERGY. Handler jag::packethandlers::Misc::UPDATE_RUNENERGY @0x1000448a0 reads 1 byte (g1, 0..100 RAW) -> player run energy (status +0x18). RegisterProt(.., 0x0d, 1). Prior 'UNKNOWN_13'/'SET_PLAYER_OP_3' was wrong (run energy was mis-attributed to op80). Encoder UpdateRunenergy (Rev948ServerCodecsMisc) owns this opcode at runtime → displayed name resolves to UPDATE_RUNENERGY via pascalToScreamingSnake. sz1.
    s(14, "IF_SETTEXTFONT", 8)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00193940 (DB IF_SETSPRITE) decodes as the official IF_SETTEXTFONT. sz8. (Encoder IfSetSprite registered here; canonical name wins for display.)
    s(15, "MIDI_JINGLE", 11)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_COORD' placeholder. Handler FUN_001dade0: DecodePackedCoord + entity spawn at tile w/ anim+delay. Graphic/spotanim-at-coord family. sz11.
    s(16, "LOC_DEL", 2)                              // GHIDRA_9485 — descriptor 0x100f0f5f0; handler jag::packethandlers::ZoneUpdates::LOC_DEL_OP16 @0x100051e40. Wire: byte0 -> shapeFlags 0x80-byte, byte1 -> packedCoord -byte.
    s(17, "SET_PLAYER_OP", -1)                       // GHIDRA_9485 — handler jag::packethandlers::PlayerInfo::SET_PLAYER_OP_OP17 @0x1000444a0 reads cursor, transformed slot, option text, and priority flag into Client+0x19738 player-option table. Was op 0 in 947-3.
    s(18, "LOGOUT_FULL", 1)                           // CONF:NONE — no official beta enum maps. Prior 'CAM_TARGET' (Camera fn; not in beta enum). Handler: was op 113 in 947-3. sz1.
    s(19, "URL_OPEN", -2)                            // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::WebPage::URL_OPEN. VarShort.
    s(20, "UPDATE_INV_STOP_TRANSMIT", 3)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Inventory::UNKNOWN_op14_handler decodes as the official UPDATE_INV_STOP_TRANSMIT. sz3.
    s(21, "LOC_ANIM_SPECIFIC", 10)                   // CONF:HIGH — official enum jag::ServerProt::LOC_ANIM_SPECIFIC (beta @0xa37840, sz10). Handler @ 0x00118ef0.
    s(22, "PLAYER_INFO", -2)                         // CONF:GHIDRA_9485 — jag::packethandlers::PlayerInfo::S2C_PLAYER_INFO_OP22 @ 0x100043500; varShort. Was op 27 in 947-3.
    s(23, "HINT_TRAIL", -2)                          // CONF:NONE — no official beta enum maps. Prior 'PLAYER_OP' (PlayerGroup fn). Handler @ 0x001869f0 (VarShort). DISTINCT from op 17 SET_PLAYER_OP. beta SET_PLAYER_OP already maps to op17. 947-3 op 109. VarShort.
    s(24, "VARCLAN", -1)                          // CONF:NONE — no official beta enum maps. Prior 'UPDATE_ZONE_PARTIAL' (not in beta enum — beta has UPDATE_ZONE_PARTIAL_FOLLOWS/_ENCLOSED only). Handler @ 0x001254c0. VarByte.
    s(25, "MESSAGE_PRIVATE_ECHO", -2)                          // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_TYPE6' placeholder. Handler FUN_001898a0: reads 2 strings, ChatHistory::AddChat(type=6). Chat message family. VarShort.
    s(26, "UPDATE_FRIENDLIST", -2)                    // CONFIRMED — handler @ 0x001a44e0 (thunk 0x001a63e0) reads a per-friend record loop {warnMessage(g1), displayName(gStr), previousName(gStr), worldId(g2), fcRank(g1), flags(g1), [worldName(gStr)+platform(g1)+worldFlags(g4) if worldId>0], notes(gStr)} → beta jag::ServerProt::UPDATE_FRIENDLIST. Was op102 in 947-3. CORRECTION: the validation matrix mislabeled this UPDATE_SITESETTINGS (trusted a fabricated Ghidra label over the 4-string friend-record behavior); empirically confirmed UPDATE_FRIENDLIST by the working lobby friends list. Encoder = FriendStatus (Rev948ServerCodecsMisc).
    s(27, "SPOTANIM_SPECIFIC", 10)                   // HANDLER_ID_CONFIRMED size-drifted (2026-06-25 audit) — handler ZoneUpdates::SPOTANIM_SPECIFIC; size drift from beta is a false alarm, behaviour confirms the official SPOTANIM_SPECIFIC. sz10.
    s(28, "VARP_LARGE", 6)                           // CONFIRMED — official enum jag::ServerProt::VARP_LARGE (beta @0xa36e40, sz6). Handler FUN_00119910, PlayerVarDomain::set. Wire: value(BE int) + id(BE u16). 947-3 op 111. Encoder in Rev948ServerCodecsVariable.
    s(29, "CLANSETTINGS_FULL", -2)                   // CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_FULL (beta @0xa36f40, sz-2). Was op 104 in 947-3.
    s(30, "IF_SETGRAPHIC", 8)                        // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00193f10 (DB IF_SET2DANGLE) decodes as the official IF_SETGRAPHIC. sz8. (Encoder IfSet2DAngle registered here; canonical name wins for display.)
    s(31, "CAM_MOVETO", 6)                           // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM' placeholder. Handler FUN_00186f40: build-area tile + id + height/delay. Graphic/spotanim at tile. sz6.
    s(32, "IF_SETCOLOUR", 8)                         // CONF:HIGH — official enum jag::ServerProt::IF_SETCOLOUR (beta @0xa36580). Handler @ 0x00185850.
    s(33, "MESSAGE_QUICKCHAT_FRIENDCHAT", -1)                          // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_PRIVATE_IN' placeholder. Handler FUN_001a0f40: type byte, sender/recipient strings, ChatHistory::AddChat(type=0x14). Private-message-receive family. VarByte.
    s(34, "VORBIS_SPEECH_SOUND", 8)                           // CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH' (Audio fn; not in beta enum). Handler @ 0x00176600. sz8.
    s(35, "IF_SETEVENTS2", 12)                       // GHIDRA_9485 — handler @0x1000a5910, descriptor 0x100f0fab0; reads settings g4_alt2, LE from/to slots, and BE component hash. Descriptive current-binary name; beta IF_SETEVENTS is op97.
    s(36, "SET_MAP_FLAG", 10)                          // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_COORD' placeholder. Handler FUN_00186c60: two build-area tiles + id + delay. Projectile/proj-anim family. sz10.
    s(37, "MESSAGE_FRIENDCHANNEL", -1)               // CONF:HIGH — official enum jag::ServerProt::MESSAGE_FRIENDCHANNEL (beta @0xa36900). Was op 7 in 947-3.
    s(38, "IF_SET_HTTP_IMAGE", 8)                           // CONF:NONE — no official beta enum maps. Prior 'IF_SET_MODEL_FRAME' (not in beta enum). Handler @ 0x00185b70. sz8.
    s(39, "IF_OPENTOP", 6)                           // GHIDRA_9485 — handler jag::packethandlers::Interfaces::IF_OPENTOP_OP39 @0x1000a2530 reads BE component hash + swapped u16 top/sub interface id and queues update type 0xc. Descriptor 0x100f0fbb0. Encoder IfOpenTop correctly bound here.
    s(40, "IF_MOVESUB", 8)                           // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00186100 (DB IF_SUBSWAP) decodes as the official IF_MOVESUB. sz8. (Encoder IfSubSwap registered here; canonical name wins for display.)
    s(41, "UPDATE_ZONE_PARTIAL_FOLLOWS", 3)          // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_FOLLOWS (beta @0xa36bc0, sz3). Was op 57 in 947-3.
    s(42, "SYNTH_SOUND", 10)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_PACKED' placeholder. Handler FUN_001875f0: packed 4B coord + entity spawn + delay. Graphic/spotanim variant. sz10.
    s(43, "SPOTANIM_SPECIFIC", 12)                          // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY' placeholder. Handler FUN_00157080: g4_alt3 target-ref + spotanim on entity-or-tile. SPOTANIM-on-entity family. sz12.
    s(44, "UPDATE_STAT", 6)                          // CONFIRMED — official enum jag::ServerProt::UPDATE_STAT (beta @0xa359c0, sz6). Handler jag::game::StatTable::UpdateStat @ 0x000ef2a0. 947-3 op 66. Wire: xp LE int + raw level + byteInverse skillId.
    s(45, "SET_MULTIWAY_STATE", 1)                   // GHIDRA_9485 — handler @0x1000ac0a0, descriptor 0x100f0fd30; reads raw state, stores state % 3, and writes validity state < 3. Descriptive current-binary name; no verbatim beta enum maps.
    s(46, "OBJ_ADD", 5)                              // GHIDRA_9485 — handler jag::packethandlers::ZoneUpdates::OBJ_ADD_OP46 @0x100053f20 reads packed tile, BE obj id, transformed count, then inserts ground object via helper 0x100402b80.
    s(47, "CLIENT_SETVARC_SMALL", 3)                 // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_SMALL (beta @0xa38380, sz3). Handler FUN_001196f0, IfaceMgr direct. Wire: value(byteAdd) + id(LE u16). 947-3 op 1. Encoder in Rev948ServerCodecsVariable.
    s(48, "CLIENT_SETVARCBIT_SMALL", 3)              // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_SMALL (beta @0xa37a40, sz3). Handler FUN_00119560. Wire: value(raw 1B) + id(BE u16, lo-byte byteAdd). Encoder in Rev948ServerCodecsVariable.
    s(49, "CHANGE_LOBBY", -2)                        // GHIDRA_9485 — handler jag::packethandlers::Lobby::CHANGE_LOBBY_OP49 @0x10009ff50, descriptor 0x100f0fe30; varShort record loop reads flag + three appearance/name strings and refreshes lobby/player-list state.
    s(50, "LOC_CUSTOMISE", -1)                       // CONF:HIGH — official enum jag::ServerProt::LOC_CUSTOMISE (beta @0xa37700). Handler @ 0x0013f5f0.
    s(51, "VARBIT_LARGE", 6)                         // CONF:HIGH — official enum jag::ServerProt::VARBIT_LARGE (beta @0xa38100, sz6). Handler FUN_001197b0, setBitFromPacket. Prior 'VARP_BIT_LARGE' was a non-official descriptor. Wire: id(BE u16) + value(intInverseMiddle). Encoder in Rev948ServerCodecsVariable.
    s(52, "NPC_INFO", -2)                            // CONF:GHIDRA_9485 — jag::packethandlers::NpcInfo::S2C_NPC_INFO_OP52 @ 0x1000adba0; varShort. Was op 12 in 947-3.
    s(53, "UNKNOWN_53", -2)                          // CONF:NONE — genuinely UNBOUND: no handler in any of the 6 binders; manager slot 0x13a16c0 has zero xrefs. Client allocates a varShort ProtEntry but installs no handler => op53 payload is read-and-discarded.
    s(54, "HASHED_WORLD_TOKEN", -1)                  // GHIDRA_9485 — current handler jag::packethandlers::Misc::HASHED_WORLD_TOKEN_OP54 @0x10004b380 via descriptor 0x100f0ff30/callback 0x100f0ff40; flag byte + cipher-subtracted string(s). Prior NO_TIMEOUT carryover was stale.
    s(55, "DESTROY_ZONE_DATA", 0)                    // GHIDRA_9485 — handler jag::packethandlers::ZoneUpdates::DESTROY_ZONE_DATA_OP55 @0x10004a870, descriptor 0x100f0ff70; clears zone-data pointer at *(Client+0x19730)+0x76c0 and releases old value.
    s(56, "MESSAGE_QUICKCHAT_PRIVATE", -1)           // CONF:HIGH — official enum jag::ServerProt::MESSAGE_QUICKCHAT_PRIVATE (beta @0xa37e00). Was op 73 in 947-3.
    s(57, "CAM_LOOKAT", 6)                           // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_2' placeholder. Handler FUN_00186e60: build-area tile + id + signed delay. Graphic/spotanim-at-tile. sz6.
    s(58, "VARCLAN_ENABLE", 0)                           // CONF:NONE — no official beta enum maps. Prior 'RESET_CLIENT_STATE' (not in beta enum). Handler @ 0x000f4080. sz0.
    s(59, "IF_SETOBJECT", 10)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCMODEL' (not in beta enum). Handler @ 0x00193c50. sz10.
    s(60, "IF_OPENSUB_ACTIVE_NPC", 25)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_SELF' (beta IF_SETPLAYERMODEL_SELF is sz4 @op148; this 948 op60 sz25 differs). Handler @ 0x00186520. sz25.
    s(61, "VARP_SMALL", 3)                           // CONFIRMED — official enum jag::ServerProt::VARP_SMALL (beta @0xa37400, sz3). Handler FUN_001199b0, PlayerVarDomain::set. 947-3 op 10. Wire: short(id) + byteInverse value. Encoder in Rev948ServerCodecsVariable.
    s(62, "IF_CLOSESUB", 4)                          // CONF:HIGH — official enum jag::ServerProt::IF_CLOSESUB (beta @0xa38500, sz4). Prior 'IF_CLOSESUB_ACTIVE' added an invented suffix; handler is the carried-over 947-3 op33 IF_CLOSESUB.
    s(63, "LOC_ADD_CHANGE", 10)                      // HANDLER_ID_CONFIRMED size-drifted (2026-06-25 audit) — handler ZoneUpdates::LOC_ADD_CHANGE (DecodePackedCoord + LocationContainer::Add); behaviour confirms the official LOC_ADD_CHANGE. sz10.
    s(64, "CLIENT_SETVARC_LARGE", 6)                 // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_LARGE (beta @0xa35a80, sz6). Handler FUN_00119600, IfaceMgr direct. Wire: value(intMiddle) + id(shortAddLittle). 947-3 op 112. Encoder in Rev948ServerCodecsVariable.
    s(65, "MAP_PROJANIM", 20)                        // CONF:HIGH — official enum jag::ServerProt::MAP_PROJANIM (beta @0xa37080). Was op 47 in 947-3.
    s(66, "SONG_PRELOAD", 5)                           // CONF:NONE — no official beta enum maps. Prior 'CAM_LOOKAT' (beta CAM_LOOKAT is sz6 @op99; this 948 op66 sz5 differs). Handler FUN_00187160: byte + g4_alt2 -> camera obj. Camera sub-op. sz5.
    s(67, "UPDATE_FRIENDCHAT_CHANNEL_FULL", -2)                          // UNKNOWN (2026-06-25 oracle) — prior CLANCHANNEL_FULL had the wrong size; the official CLANCHANNEL_FULL is op9 (HANDLER_ID_HIGH). Handler Clans::CLANCHANNEL_FULL here not size-confirmed. VarShort.
    s(68, "IF_SETANGLE", 10)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETMODELORIGIN' (not in beta enum). Handler @ 0x00193420. sz10.
    s(69, "CLIENT_SETVARCBIT_LARGE", 6)              // CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_LARGE (beta @0xa38300, sz6). Handler FUN_001194b0. Wire: id(BE u16) + value(BE int). Encoder in Rev948ServerCodecsVariable.
    s(70, "IF_OPENSUB_ACTIVE_PLAYER", 25)                          // CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_OTHER' (beta IF_SETPLAYERMODEL_OTHER is sz10 @op82; this 948 op70 sz25 differs). Handler @ 0x00186710. sz25.
    s(71, "OBJ_REVEAL", 7)                           // CONF:HIGH — official enum jag::ServerProt::OBJ_REVEAL (beta @0xa35fc0, sz7). Handler @ 0x000f37b0.
    s(72, "CAM2_ENABLE", 1)                          // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00186f60 (DB CAM_FORCEANGLE): 1 boolean byte -> Camera::ProcessCameraReset = the official CAM2_ENABLE. sz1.
    s(73, "CHAT_FILTER_SETTINGS", 2)                 // HANDLER_ID_HIGH (2026-06-25 audit) — handler jag::packethandlers::Chat::UNKNOWN_op0x49_handler decodes as the official CHAT_FILTER_SETTINGS. sz2.
    s(74, "SCENE_TIMING_BASE", 4)                    // DESCRIPTIVE — current 948-5 handler jag::packethandlers::ClientState::SetSceneTimingBase_OP74 @0x100043360 writes BE g4 to SceneTargetContext+0x64; scene xrefs treat +0x64/+0x68 as a timing window. Not JCOINS_UPDATE. Encoder/decoder registered as SceneTimingBase.
    s(75, "SET_READY_FLAG", 0)                       // GHIDRA_9485 — handler jag::packethandlers::ClientState::SET_READY_FLAG_OP75 @0x10009f5c0, descriptor 0x100f10470; sets Client+0x19780+0x10 and refreshes Client+0x197a8+0xf0.
    s(76, "UPDATE_ZONE_PARTIAL_ENCLOSED", -2)        // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_ENCLOSED (beta @0xa37d00, sz-2). Was op 126 in 947-3.
    s(77, "CAMERA_UPDATE", -2)                       // CONF:HIGH — official enum jag::ServerProt::CAMERA_UPDATE (beta @0xa36e00, sz-2). Handler 0x001d3b50 verified: bitflag-driven camera update (pos/zoom/lookat/rotate). Prior 'CAM_UPDATE' was the C++ function name.
    s(78, "UPDATE_ZONE_FULL_FOLLOWS", 3)             // CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_FULL_FOLLOWS (beta @0xa37d40, sz3). Was op 18 in 947-3.
    s(79, "DEBUG_SERVER_TRIGGERS", -1)                          // LOW (2026-06-25 audit) — handler NPCInfo::NPC_HEADICON_SPECIFIC only LOW-confidence; demoted to UNKNOWN. No encoder. VarByte.
    s(80, "SETFILTER_PRIVATE", 1)                    // CORRECTED 2026-06-27 (recorder-capture-points.md §10.4) — official enum jag::ServerProt::SETFILTER_PRIVATE (private-chat filter), NOT run energy. Handler jag::packethandlers::Misc::SETFILTER_PRIVATE @0x10009f760 reads 1 byte (g1) -> (Client+0x19780)+0x60 = private-chat filter mode {0=On,1=Friends,2=Off}, -1=unset. sz1. The run-energy opcode is op13 (0x0d); the prior 'UPDATE_RUNENERGY'/'SET_RUN_ENERGY' label here was wrong (caught by the recorder client-is-king cross-check: this packet carried a constant 0x01 while run energy went 0→100).
    s(81, "REBUILD_NORMAL", -2)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::REBUILD_NORMAL_SIMPLE decodes as the official REBUILD_NORMAL (world-login simple form; op199 is the multi-scene form, same official name). Encoder RebuildNormalSimple registered here; canonical name wins for display. VarShort.
    s(82, "IF_SETPOSITION", 23)                      // GHIDRA_9485 — handler jag::packethandlers::Interfaces::IF_SETPOSITION_OP82 @0x1000a2a30 matches live codec: layer byte, position g4_alt3, child component id LE at bytes 21..22, then queues interface update. Older op82/op94 swap note was stale.
    s(83, "REBUILD_REGION", -2)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::REBUILD_REGION_ALT (cmd=5 gate, bit-packed coords, SceneManager build) decodes as the official REBUILD_REGION. VarShort.
    s(84, "IF_SETOBJECT", 10)                        // CONF:HIGH — official enum jag::ServerProt::IF_SETOBJECT (beta @0xa36840, sz10). Handler @ 0x00185a00.
    s(85, "UPDATE_INV_FULL", -2)                     // CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_FULL (beta @0xa36040, sz-2). Handler Inventory::UPDATE_INV_FULL_impl. Was op 69 in 947-3.
    s(86, "IF_SETPLAYERHEAD_OTHER", 10)                          // MISASSIGNED (2026-06-25 audit) — DB IF_SETANIM is part of the IF_SET* family the beta refactored into a unified SetComponentProperty(update-type) dispatch; no confident per-opcode official name, demoted to UNKNOWN. This is the SetComponentProperty propType-3 packet, NOT IF_SETANIM (that is op103). Encoder IfSetComponentProp3 bound here (Rev948ServerCodecsInterface) → displayed name resolves to IF_SET_COMPONENT_PROP3 via pascalToScreamingSnake. sz10.
    s(87, "CAM_RESET", 0)                            // CONF:HIGH — official enum jag::ServerProt::CAM_RESET (beta @0xa37800, sz0). Was op 89 in 947-3.
    s(88, "CAM_REMOVEROOF", 4)                       // GHIDRA_9485 — handler jag::packethandlers::Camera::CAM_REMOVEROOF_OP88 @0x100088ae0 reads packed coord; 0xffffffff clears, otherwise stores plane/x/y and present flag in camera state. Descriptor 0x100f107b0.
    s(89, "NPC_ANIM_SPECIFIC", 19)                   // HANDLER_ID_HIGH (2026-06-25 audit) — handler NPCInfo::NPC_SPOTANIM (NPCList::GetNPCNode + per-NPC anim vtable) decodes as the official NPC_ANIM_SPECIFIC. sz19.
    s(90, "LOC_ADD_CHANGE", -1)                          // CONF:NONE — no official beta enum maps. Prior 'LOC_ADD' (beta has LOC_ADD_CHANGE, not LOC_ADD). Handler @ 0x00139080. VarByte.
    s(91, "IF_SETHIDE", 5)                           // GHIDRA_9485 — handler jag::packethandlers::Interfaces::IF_SETHIDE_OP91 @0x1000a5fa0 reads hide byte 0x81 + component hash g4_alt2 and writes record type 7 bool at +0x20. Was op 103 in 947-3.
    s(92, "CLIENT_SETVARCSTR_SMALL", -1)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler Misc::SET_VARC_STR_SMALL decodes as the official CLIENT_SETVARCSTR_SMALL. Encoder ClientSetVarcStr registered here; canonical name wins for display. VarByte.
    s(93, "MESSAGE_GAME", -1)                        // GHIDRA_9485 — handler jag::packethandlers::Chat::MESSAGE_GAME_OP93 @0x10008a580 reads message type, BE u32 field, flags, optional prefix/sender strings, and message text. Was op 105 in 947-3.
    s(94, "IF_OPENSUB", 8)                           // GHIDRA_9485 — binary-only handler jag::packethandlers::Interfaces::IF_OPENSUB_OP94 @0x1000a22c0 reads LE subId, LE walkable, BE parentHash and opens interface record type 0xb. Distinct from op82 IF_SETPOSITION.
    s(95, "MIDI_SONG", 5)                            // CONF:HIGH — official enum jag::ServerProt::MIDI_SONG (beta @0xa36b00). Was op 87 in 947-3.
    s(96, "IF_SET_COMPONENT_PROPERTY_TYPE3", 4)      // GHIDRA_9485 — handler @0x1000a7210, descriptor 0x100f109b0; reads componentHash g4_alt3 and calls SetComponentProperty(type=3,argA=active value,argB=0). Encoder IfSetAnimActive is legacy API naming.
    s(97, "IF_SETEVENTS", 10)                        // CONF:HIGH — official enum jag::ServerProt::IF_SETEVENTS (beta @0xa35e80). Was op 34 in 947-3. (op35 is a 948 second variant -> UNKNOWN_35.)
    s(98, "PROJANIM_SPECIFIC", 25)                          // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL' placeholder. Handler FUN_000f25d0: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family. sz25.
    s(99, "IF_SETRECOL", 6)                          // CONF:HIGH — official enum jag::ServerProt::IF_SETRECOL (beta @0xa376c0). Handler @ 0x00193860.
    s(100, "CAM_FORCEANGLE", 4)                         // CONF:NONE — no official beta enum maps. Prior 'RUNCLIENTSCRIPT_SHORT': beta RUNCLIENTSCRIPT already maps to op110. This is a 948 short variant, no distinct beta enum. Handler FUN_00187020. sz4.
    s(101, "IF_SET_COMPONENT_PROPERTY_TYPE5", 4)     // GHIDRA_9485 — handler @0x1000a6820, descriptor 0x100f10af0; reads componentHash g4_alt2 and calls SetComponentProperty(type=5,argA=active value,argB=0). Encoder IfSetObjectActive is legacy API naming.
    s(102, "IF_SETMODEL", 8)                         // GHIDRA_9485 — jag::packethandlers::Interfaces::IF_SETMODEL_OP102 @0x1000a6be0 reads modelId g4_alt2, componentHash BE, then SetComponentProperty(type=1,argB=-1). sz8.
    s(103, "IF_SETANIM", 8)                          // HANDLER_ID_HIGH (2026-06-25 oracle) — handler Interfaces::IF_SETGRAPHIC (DB label) decodes as the official IF_SETANIM. The official IF_SETGRAPHIC is op30. sz8.
    s(104, "HINT_ARROW", 14)                         // GHIDRA_9485 — handler @0x10009f8f0, descriptor 0x100f10bb0; slot/type byte clears, targets, or writes coordinate hint-arrow forms. sz14.
    s(105, "MESSAGE_CLANCHANNEL", -1)                // CONF:HIGH — official enum jag::ServerProt::MESSAGE_CLANCHANNEL (beta @0xa361c0, sz-1). Handler @ 0x001a1cc0.
    s(106, "LOGOUT", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_ORIGIN' (NPC_UPDATE_ORIGIN fn; not in beta enum). Handler: was op 61 in 947-3. sz1.
    s(107, "OBJ_DEL", 3)                             // CONF:HIGH — official enum jag::ServerProt::OBJ_DEL (beta @0xa36b40, sz3). Handler @ 0x00152b00.
    s(108, "CLANSETTINGS_DELTA", -2)                 // CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_DELTA (beta @0xa38640, sz-2). Was op 63 in 947-3.
    s(109, "UPDATE_UID192", 28)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler PlayerInfo::HANDSHAKE_UID (24B UID + 4B CRC32, sz28) decodes as the official UPDATE_UID192. sz28.
    s(110, "RUNCLIENTSCRIPT", -2)                    // CONF:GHIDRA_9485 — jag::packethandlers::ClientScript::RUNCLIENTSCRIPT_OP110 @ 0x100096580; varShort. Was op 121 in 947-3.
    s(111, "CAM_SHAKE", 6)                         // CONF:NONE — no official beta enum maps. Prior 'CAM_SUB' placeholder. Handler FUN_00175960: 5 bytes -> camera obj vtable. Camera sub-op. sz6.
    s(112, "UPDATE_REBOOT_TIMER", 2)                 // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::SET_TICK_TIMER decodes as the official UPDATE_REBOOT_TIMER. sz2. (op184, prior holder of this name, demoted to UNKNOWN.)
    s(113, "MAP_ANIM", 11)                           // GUARANTEED (2026-06-25 audit) — handler ZoneUpdates::MAP_ANIM. Encoder MapAnim registered here; canonical name wins for display. sz11.
    s(114, "MESSAGE_CLANCHANNEL_SYSTEM", -1)         // GHIDRA_9485 — handler jag::packethandlers::Chat::MESSAGE_CLANCHANNEL_SYSTEM_OP114 @0x10008e430 reads channel display string, BE u16 key, decodes remaining payload, and queues chat type 0x13. Descriptor 0x100f10e30.
    s(115, "IF_SET_COMPONENT_PROPERTY_TYPE7", 10)    // GHIDRA_9485 — handler @0x1000a75d0, descriptor 0x100f10e70; calls SetComponentProperty(type=7) with field0 BE, field1/field2 LE, componentHash LE. Encoder IfSetNpcHead is legacy API name. sz10.
    s(116, "CLIENT_SETVARCSTR_LARGE", -2)                        // CONF:NONE — no official beta enum maps. Prior 'SET_VARC_STR_LARGE': beta has CLIENT_SETVARCSTR_LARGE (sz-2 @op188) but the op92/op116 small/large split is a 948 reorg not present in beta; identity unverified. Handler FUN_001500a0 IfaceMgr type=2. Encoder in Rev948ServerCodecsVariable. VarShort.
    s(117, "UPDATE_FRIENDCHAT_CHANNEL_SINGLEUSER", -1)                        // UNKNOWN (2026-06-25 oracle) — prior CLANCHANNEL_DELTA had the wrong size; the official CLANCHANNEL_DELTA is op124 (HANDLER_ID_HIGH). Handler Clans::CLANCHANNEL_DELTA here not size-confirmed. VarByte.
    s(118, "IF_OPENSUB_ACTIVE_OBJ", 29)             // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x001da3f0 (DB IF_SETPLAYERMODEL_SNAPSHOT): DecodePackedCoord -> (float)(coord<<9) (sz29) = the official IF_OPENSUB_ACTIVE_OBJ. sz29. (Encoder IfSetPlayerModelSnapshot registered here; canonical name wins for display.)
    s(119, "CUTSCENE_DATA", 35)                      // GHIDRA_9485 — handler @0x1000aaa60, descriptor 0x100f10f70; group/slot plus 33-byte cutscene record parsed by 0x1000beed0. sz35.
    s(120, "CAM_SMOOTHRESET", 0)                     // GHIDRA_9485 — handler jag::packethandlers::Camera::CAM_SMOOTHRESET_OP120 @0x100087e60 empty body resets camera/client state and marks Client+0x196d8 dirty. Was op 80 in 947-3.
    s(121, "UPDATE_INV_PARTIAL", -2)                 // CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_PARTIAL (beta @0xa37c00, sz-2). Was op 5 in 947-3.
    s(122, "IF_SETTEXT", -2)                         // GHIDRA_9485 — handler jag::packethandlers::Interfaces::IF_SETTEXT_OP122 @0x1000a5d70 reads CP1252 string first, then component hash g4_alt3, and calls interface text helper 0x10011f700.
    s(123, "TRIGGER_ONDIALOGABORT", 0)              // HANDLER_ID_HIGH (2026-06-25 audit) — handler @0x00185620 (DB IF_TRIGGER_CLOSE) decodes as the official TRIGGER_ONDIALOGABORT (also at op162/GUARANTEED). sz0. (Encoder IfTriggerClose registered here; canonical name wins for display.)
    s(124, "CLANCHANNEL_DELTA", -2)                  // HANDLER_ID_HIGH (2026-06-25 oracle) — handler Chat::CLANSETTINGS_DELTA_CHAT (DB label) decodes as the official CLANCHANNEL_DELTA. The official CLANSETTINGS_DELTA is op108 (HANDLER_ID_HIGH). VarShort.
    s(125, "OBJ_COUNT", 7)                           // CONF:HIGH — official enum jag::ServerProt::OBJ_COUNT (beta @0xa36880, sz7). Handler @ 0x001190d0.
    s(126, "MESSAGE_FRIENDCHANNEL", -1)                        // CONF:NONE — no official beta enum maps. Prior 'MESSAGE_FRIENDCHAT' (beta has MESSAGE_FRIENDCHANNEL only; verified no _FRIENDCHAT enum). Was op 21 in 947-3. VarByte.
    s(127, "VORBIS_SPEECH_STOP", 0)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET' placeholder. Handler FUN_00182610: FUN_0066ecb0(graphicsObj,8). Minimap/camera reset. sz0.
    s(128, "NO_TIMEOUT", 0)                         // CONF:NONE — no official beta enum maps. Prior 'NOOP_128' placeholder. Handler FUN_00173e40: empty (no read). No-op/reserved. sz0.
    s(129, "POINTLIGHT_SHADOW", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE' placeholder. Handler FUN_001dc630: entity-group-at-tile, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(130, "ENVIRONMENT_OVERRIDE", -1)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_IGNORELIST': beta UPDATE_IGNORELIST is sz-2 @op10 but handler 0x001d29c0 is a 64-bit-flag relationship delta (suspected true UPDATE_FRIENDLIST), identity contested. Encoder DISABLED. VarByte.
    s(131, "TELEMETRY_GRID_MOVE_COLUMN", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP' placeholder. Handler FUN_000f9600: per-worldentity record list move/reorder. WorldEntity-list family. sz3.
    s(132, "VORBIS_SOUND_GROUP_STOP", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_STOP' (Audio fn; not in beta enum). Handler @ 0x0017e4c0. sz2.
    s(133, "SETDRAWORDER", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_FLAG' (NPC_UPDATE_FLAG fn; not in beta enum). Handler: was op 138 in 947-3. sz1.
    s(134, "NPC_SAY_SPECIFIC", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NPC_SAY' placeholder. Handler FUN_00179460: NPCList::GetNPCNode + string -> NPC overhead-text. NPC overhead say/text. VarShort.
    s(135, "CUTSCENE", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NOOP_135' placeholder. Handler FUN_001758f0: empty. No-op/discard. VarShort.
    s(136, "IF_SETPLAYERHEAD_SNAPSHOT", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETANIM_SMALL' (not in beta enum). Handler @ 0x00185aa0. sz5.
    s(137, "CREATE_SUGGEST_NAME_ERROR", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_A' (Chat fn; not in beta enum). Handler: was op 217 in 947-3. sz1.
    s(138, "VORBIS_PRELOAD_SOUND_GROUP", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SKIP_2_BYTES' (Misc fn). Handler @ 0x00175f90. sz2.
    s(139, "MISC_NEW_945", 0)                         // MISASSIGNED (2026-06-25 audit) — DB Misc::LOGOUT_TRANSFER label not behaviourally confirmed; no confident official name, demoted to UNKNOWN. No encoder. sz0.
    s(140, "PLAYER_ANIM_SPECIFIC", 17)                        // CONF:NONE — no official beta enum maps. Prior 'PLAYER_SPOTANIM' placeholder. Handler FUN_000f2280: spotanim on local player via vtable. SPOTANIM-on-local-player family. sz17.
    s(141, "DBFILTER_DEBUG", -2)                        // CONF:NONE — no official beta enum maps. Prior 'IF_OPENSUB_THUNK' (thunk fn-name; beta IF_OPENSUB already maps to op94). Handler Interfaces::IF_OPENSUB_thunk. Was op 186 in 947-3. VarShort.
    s(142, "CREATE_SUGGEST_NAME_REPLY", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SET_URL_STRING' (Misc fn; beta has URL_OPEN/UPDATE_URL_STRING-style but not SET_URL_STRING). Was op 200 in 947-3. VarByte.
    s(143, "TELEMETRY_GRID_ADD_ROW", 6)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_ADD' placeholder. Handler FUN_00119e20: insert/update worldentity record. WorldEntity-list family. sz6.
    s(144, "TELEMETRY_GRID_REMOVE_ROW", 2)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL' placeholder. Handler FUN_00119c30: remove worldentity record by index. WorldEntity-list family. sz2.
    s(145, "EXECUTE_CLIENT_CHEAT", 2)                         // CONF:NONE — no official beta enum maps. Prior 'DEV_CONSOLE_CMD' placeholder. Handler FUN_00187890: u16 subcmd switch (clear list/heightmap/camera/flags). Dev/debug dispatcher. sz2.
    s(146, "TELEMETRY_GRID_REMOVE_COLUMN", 2)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL_2' placeholder. Handler FUN_000f98f0: remove by index. WorldEntity-list family. sz2.
    s(147, "VARP_LONG", 10)                        // CONF:NONE — VETO: handler FUN_00141510/0x00141510 is decompile-certain (player-varp 8B value) but NO verbatim beta enum exists (beta var set = VARP_SMALL/VARP_LARGE only). Official enum name VARP_LONG rejected per matrix; sz10 authoritative. Encoder VarpLong in Rev948ServerCodecsVariable owns this opcode at runtime.
    s(148, "CUTSCENE2D_PLAY", 2)                         // CONF:NONE — no official beta enum maps. Prior 'IF_CLOSESUB_BY_ID': beta IF_CLOSESUB already maps to op62. This sz2 sibling has no distinct beta enum. Handler @ 0x001855d0. sz2.
    s(149, "TELEMETRY_GRID_ADD_COLUMN", 6)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_INS' placeholder. Handler FUN_000f9a20: insert sentinel into worldentity lists. WorldEntity-list family. sz6.
    s(150, "POINTLIGHT_EXTEND_BELOW", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_2' placeholder. Handler FUN_001db620: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(151, "MAP_PROJANIM_HALFSQ", 21)                        // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::PROJANIM_SPECIFIC plausible but size-ambiguous; demoted to UNKNOWN. Encoder ProjAnimSpecific (Rev948ServerCodecsZone) owns this opcode at runtime → displayed name resolves to PROJ_ANIM_SPECIFIC via pascalToScreamingSnake. sz21.
    s(152, "IF_SET_HTTP_IMAGE", -1)                  // GHIDRA_9485 — jag::packethandlers::Interfaces::IF_SET_HTTP_IMAGE_OP152 @0x1000ac5b0 reads CP1252 image/resource path string. Was op 146 in 947-3.
    s(153, "POINTLIGHT_ATTENUATION_FALLOFF", 4)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLOAT_AT_TILE' placeholder. Handler FUN_001dc100: entity-group, set float/flag. ZoneUpdate entity-overlay family. sz4.
    s(154, "ENTITY_ANIM_AT_TILE", 5)                 // DESCRIPTIVE_GHIDRA_9485 — handler @0x1000995b0, descriptor 0x100f11830; resolves active-scene entity key and updates timing fields. No verbatim beta enum maps.
    s(155, "LOGOUT", 0)                              // CONF:HIGH — official enum jag::ServerProt::LOGOUT (beta @0xa358c0, sz0). Handler Misc::LOGOUT. Was op 147 in 947-3.
    s(156, "CREATE_ACCOUNT_REPLY", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_B' (Chat fn; not in beta enum). Handler: was op 155 in 947-3. sz1.
    s(157, "SCENE_FLAG", 1)                          // DESCRIPTIVE_GHIDRA_9485 — handler @0x1000ac260, descriptor 0x100f118f0; stores negated byte at *(Client+0x196a8)+0x678 and marks Client+0x196d8 dirty. No verbatim beta enum maps.
    s(158, "IF_SETRETEX", 9)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETSCROLLSIZE' (not in beta enum). Handler @ 0x00193660. sz9.
    s(159, "SERVER_REPLY", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NOOP_159' placeholder. Handler FUN_000ec3a0: empty. No-op/discard. VarShort.
    s(160, "NPC_HEADICON_SPECIFIC", 9)                         // CONF:NONE — no official beta enum maps. Prior 'NPC_HEADBAR_SPECIFIC' placeholder. Handler FUN_001d76a0: NPCList::GetNPCNode, per-slot arrays. NPC headbar/hitmark family. sz9.
    s(161, "PLAYER_GROUP_DELTA", -2)                 // CONF:HIGH — official enum jag::ServerProt::PLAYER_GROUP_DELTA (beta @0xa37640, sz-2). Handler 0x001989c0 verified: byte + payload -> player-group delta record. Prior 'UPDATE_PLAYER_GROUP' was a fabricated rename.
    s(162, "TRIGGER_ON_DIALOG_ABORT", 0)             // GHIDRA_9485 — handler @0x100084980, descriptor 0x100f11a30; empty body sets dialog-abort state at Client+0x19728+0x154. sz0.
    s(163, "SPOTANIM_SPECIFIC_V2", 15)                        // CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY_2' placeholder. Handler FUN_001db050: g4_alt3 target-ref + spotanim. SPOTANIM-on-entity family. sz15.
    s(164, "MAP_PROJANIM_HALFSQ", 28)               // HANDLER_ID_HIGH (2026-06-25 audit) — handler ZoneUpdates::MAP_PROJANIM_HALT decodes as the official MAP_PROJANIM_HALFSQ. sz28. (Encoder MapProjAnimHalt registered here; canonical name wins for display.)
    s(165, "IF_SETOBJECT_LONG", 14)                        // CONF:NONE — no official beta enum maps. Prior 'IF_SETMODEL_COORD' (not in beta enum). Handler @ 0x001939d0. sz14.
    s(166, "TELEMETRY_GRID_ADD_GROUP", 5)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_ADD' placeholder. Handler FUN_00120150: RebuildSceneEntry::Reset + WorldList::InsertOrReplace. WorldEntity add/placement. sz5.
    s(167, "SYNTH_SOUND", 12)                        // CONF:HIGH — official enum jag::ServerProt::SYNTH_SOUND (beta @0xa35bc0). Handler Audio::SYNTH_SOUND @ 0x00187350.
    s(168, "TEXT_COORD", -1)                        // AMBIGUOUS (2026-06-25 audit) — handler ZoneUpdates::SOUND_AREA plausible but size-ambiguous; demoted to UNKNOWN. Encoder SoundArea (Rev948ServerCodecsMisc) owns this opcode at runtime → displayed name resolves to SOUND_AREA via pascalToScreamingSnake. VarByte.
    s(169, "MISC_NEW_946", 8)                         // UNKNOWN (2026-06-25 audit; 2026-06-27 rechecked) — was mislabeled SEND_PING in pass 1; op169 reads a g8 server timestamp (latency delta), not the anti-cheat challenge. No confident official name. No encoder. sz8.
    s(170, "LOC_PREFETCH", 5)                         // CONF:NONE — no official beta enum maps. Prior 'LOC_MERGE' (not in beta enum). Handler @ 0x000eeba0. sz5.
    s(171, "TELEMETRY_CLEAR_GRID_VALUE", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_2' placeholder. Handler FUN_000ef800: write sentinel into worldentity sublist. WorldEntity-list family. sz3.
    s(172, "SCENE_TARGET_CONTEXT_FLAG_A", 1)         // DESCRIPTIVE_GHIDRA_9485 — handler @0x1000451c0, descriptor 0x100f11cb0; stores raw byte to Client+0x19db8 context +0xa0. Local MinimapFlagA meaning unverified.
    s(173, "PLAYER_GROUP_DELTA", -2)                        // CONF:NONE — no official beta enum maps. Prior 'LOC_COMPOSITE_UPDATE' was invented (Ghidra handler literally named UNKNOWN_op173_handler @ 0x001ae100). Reads gT_ulong+u32+subcommand loop (cases 1..0xe alloc typed LOC/zone delta objs). ZoneUpdates composite multi-part update. VarShort.
    s(174, "ANTI_CHEAT_CHALLENGE", 8)                // GHIDRA_9485 — handler jag::packethandlers::AntiCheat::ANTI_CHEAT_CHALLENGE_OP174 @0x1000abcd0 reads two 4-byte challenge words, queues C2S op3 anti-cheat response, and is not a plain SEND_PING.
    s(175, "MESSAGE_PRIVATE_ECHO", -1)               // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE_ECHO (beta @0xa37a80). Was op 129 in 947-3.
    s(176, "SET_TARGET", 3)                         // CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_D' (Misc fn; not in beta enum). Was op 144 in 947-3. sz3.
    s(177, "MAP_PROJANIM_HALFSQ_V2", 29)                        // CONF:NONE — no official beta enum maps. Prior 'PROJANIM_SPECIFIC_HALT' placeholder (beta PROJANIM_SPECIFIC sz22 @op140; this sz29 halt variant differs). Handler @ 0x000f1840. sz29.
    s(178, "PLAYER_SNAPSHOT", -2)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_PLAYER_CHAT' (PlayerInfo fn; not in beta enum). Was op 178 in 947-3. VarShort.
    s(179, "IF_SETSCROLLPOS", 9)                      // HANDLER_ID_HIGH (2026-06-25 binary re-verify) — handler @0x001938e0 update-type 0x11 (scrollY g2_add / subSlot g1_sub / scrollX g2 / componentHash g4_alt3) = the real IF_SETSCROLLPOS. Encoder IfSetScrollPos correctly bound here. (op39 is IF_OPENTOP, not this.)
    s(180, "IF_SETPLAYERMODEL_SNAPSHOT", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETOBJECT_SMALL' (not in beta enum). Handler @ 0x00185980. sz5.
    s(181, "LOYALTY_UPDATE", 4)                      // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::UNKNOWN_op0xB5_handler decodes as the official LOYALTY_UPDATE. sz4.
    s(182, "POINTLIGHT_EXTEND_ABOVE", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_3' placeholder. Handler FUN_001dbb80: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3.
    s(183, "MAP_ANIM_V2", 14)                        // CONF:NONE — no official beta enum maps. Prior 'MAP_ANIM_SPECIFIC' (not in beta enum). Handler @ 0x001575c0. sz14.
    s(184, "UPDATE_DOB", 4)                         // UNKNOWN (2026-06-25 audit) — handler Misc::SET_SYSUPDATE_TIMER, no official enum maps; the official UPDATE_REBOOT_TIMER is now op112 (HANDLER_ID_HIGH). No encoder. sz4.
    s(185, "MESSAGE_PRIVATE", -1)                    // CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE (beta @0xa37e40). Was op 151 in 947-3.
    s(186, "TELEMETRY_GRID_VALUES_DELTA", -2)                        // CONF:NONE — no official beta enum maps. Prior 'REBUILD_WORLDENTITY' (not in beta enum). Was op 188 in 947-3. VarShort.
    s(187, "CREATE_CHECK_NAME_REPLY", 1)            // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::Lobby::CREATE_CHECK_NAME_REPLY. sz1.
    s(188, "SOCIAL_NETWORK_LOGOUT", -2)             // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::WebPage::SOCIAL_NETWORK_LOGOUT. VarShort.
    s(189, "NEW_AUDIO_945", 4)                         // CONF:NONE — no official beta enum maps. Prior 'VORBIS_PRELOAD' (Audio fn; beta has VORBIS_PRELOAD_SOUNDS/_SOUND_GROUP, not VORBIS_PRELOAD). Handler @ 0x001870f0. sz4.
    s(190, "CLEAR_PENDING_UPDATES", 0)               // GHIDRA_9485 — handler @0x10004a640, descriptor 0x100f12130; drains pending update records and resets queue pointers. Descriptive current-binary name; no verbatim beta enum maps.
    s(191, "JCOINS_UPDATE", 4)                       // HANDLER_ID_HIGH (2026-06-25 audit) — handler ClientState::UNKNOWN_op0xBF_handler decodes as official JCOINS_UPDATE. Encoder is bound here. sz4.
    s(192, "CREATE_CHECK_EMAIL_REPLY", 1)           // GUARANTEED (2026-06-25 audit) — handler jag::packethandlers::Lobby::CREATE_CHECK_EMAIL_REPLY. sz1.
    s(193, "CLEAR_PLAYER_SNAPSHOT", 1)                         // CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_C' (Misc fn; not in beta enum). Was op 174 in 947-3. sz1.
    s(194, "TELEMETRY_GRID_REMOVE_GROUP", 1)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_3' placeholder. Handler FUN_000efa80: reorder/compact worldentity lists. WorldEntity-list family. sz1.
    s(195, "VORBIS_PRELOAD_SOUNDS", 4)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH_2' (Audio fn; not in beta enum). Handler @ 0x001870a0. sz4.
    s(196, "CLIENT_SETVARC_LONG", 10)                        // CONF:NONE — VETO: handler FUN_00119350/0x00119350 is decompile-certain (client-varc 8B BE value, InterfaceManager type=1) but NO verbatim beta enum exists (beta varc set = SMALL/LARGE only). Official enum name CLIENT_SETVARC_LONG rejected per matrix; sz10 authoritative. Framing-only stub (no encoder/data class).
    s(197, "MISC_NEW_946_2", -2)                        // CONF:NONE — no official beta enum maps. Prior 'SKIP_DATA' (Misc fn; not in beta enum). Was op 166 in 947-3. VarShort.
    s(198, "UNNAMED_1", -1)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_URL_STRING' (Misc fn; not in beta enum). Was op 214 in 947-3. VarByte.
    s(199, "TELEMETRY_GRID_FULL", -2)                        // UNKNOWN (2026-06-25 oracle) — the official REBUILD_NORMAL is op81 (HANDLER_ID_HIGH); op199 handler ClientState::REBUILD_NORMAL (DB label, multi-scene grid form) is not the canonical REBUILD_NORMAL per the oracle. VarShort.
    s(200, "VORBIS_SOUND_GROUP_START", 2)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_STOP' (Audio fn; beta has VORBIS_SOUND_GROUP_STOP, not SOUND_GROUP_STOP). Handler @ 0x00176000. sz2.
    s(201, "TELEMETRY_GRID_SET_ROW_PINNED", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_4' placeholder. Handler FUN_000ef970: write sentinel/value into worldentity sublist. WorldEntity-list family. sz3.
    s(202, "CONSOLE_FEEDBACK", -2)                        // CONF:NONE — no official beta enum maps. Prior 'PLAYER_INFO_DECODE_2' (PlayerInfo fn; not in beta enum). Was op 180 in 947-3. VarShort.
    s(203, "POINTLIGHT_ENABLED", 3)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_RESET_AT_TILE' placeholder. Handler FUN_001dd3e0: entity-group, set flag + anim field. ZoneUpdate entity-overlay family. sz3.
    s(204, "SCENE_TARGET_CONTEXT_FLAG_B", 1)         // DESCRIPTIVE_GHIDRA_9485 — handler @0x100045360, descriptor 0x100f124b0; stores negated byte to Client+0x19db8 context +0xa4. Local MinimapFlagB meaning unverified.
    s(205, "SOUND_MIXBUSS_ADD", 6)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_SPEED' (Audio fn; not in beta enum). Handler @ 0x00182690. sz6.
    s(206, "IF_SETCLICKMASK", 5)                         // CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCHEAD_ACTIVE' (not in beta enum). Handler @ 0x001935c0. sz5.
    s(207, "TELEMETRY_GRID_MOVE_ROW", 3)                         // CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_5' placeholder. Handler FUN_00119b80: reorder/compact worldentity list. WorldEntity-list family. sz3.
    s(208, "PLAYER_GROUP_VARPS", -2)                        // CONF:NONE — no official beta enum maps. Prior 'UPDATE_INV_GROUP' (Inventory fn; beta has UPDATE_INV_FULL/_PARTIAL/_STOP_TRANSMIT, not _GROUP). Was op 177 in 947-3. VarShort.
    s(209, "PLAYER_GROUP_FULL", -2)                        // CONF:NONE — no official beta enum maps. Prior 'NPC_INFO_THUNK' (thunk fn-name; beta NPC_INFO maps to op52). Handler NPCInfo::NPC_INFO_thunk_worldentity. Was op 205 in 947-3. VarShort.
    s(210, "SOUND_MIXBUSS_SETLEVEL", 6)                         // CONF:NONE — no official beta enum maps. Prior 'VORBIS_SONG' (Audio fn; beta has VORBIS_SOUND/_SOUND_GROUP/etc, not VORBIS_SONG). Handler @ 0x00187750. sz6.
    s(211, "PROJANIM_SPECIFIC_V2", 33)                        // CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL_2' placeholder. Handler FUN_000f1420: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family (largest). sz33.
    s(212, "CHANGE_LOBBY", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SET_WORLD_TARGET' (WorldData fn; not in beta enum). Was op 187 in 947-3. VarByte.
    s(213, "LOGOUT_TRANSFER", -1)                        // CONF:NONE — no official beta enum maps. Prior 'SWITCH_WORLD' (WorldData fn; not in beta enum). Was op 179 in 947-3. VarByte.
    s(214, "MIDI_SONG_LOCATION", 0)                         // CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET_2' placeholder. Handler FUN_00182650: FUN_00c2a9a0(graphicsObj,1). Camera/minimap reset. sz0.
    s(215, "POINTLIGHT_COLOUR", 8)                         // CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_AT_TILE_2' placeholder. Handler FUN_001deaa0: g4 + entity-group, set fields. ZoneUpdate entity-overlay family. sz8.
    s(216, "WORLDLIST_FETCH_REPLY", -2)              // GHIDRA_9485 — handler jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY_OP216 @0x1000acfb0 forwards to chunk parser 0x1000d6660; first byte is final-frame flag, rest appends to world-list accumulator. Was op 159 in 947-3.
    s(217, "MIDI_SONG", 4)                         // CONF:NONE — no official beta enum maps. Prior 'SOUND_MODIFY' (Audio fn; not in beta enum). Handler @ 0x00175c90. sz4.
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
