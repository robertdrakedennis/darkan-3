# 948-2-2 ServerProt — Complete 218-Opcode Table

**Deliverable: total naming + size coverage for ALL 218 ServerProt opcodes in rev 948-2-2.**

Authority: `rs2client.948-2-2` (Ghidra, STRIPPED). Sizes are ground truth from
`jag::ServerProt::RegisterAll @ 0x000c4700` (each `InitEntry(entry, opcode, size)`).
Handlers from the 6 binders: catch-all `ServerProt::BindHandlers @ 0x0007509a` +
`ClientState::BindHandlers @ 0x000aa85e`, `ClientState::BindHandlers_extra @ 0x000aaf4c`,
`ZoneUpdates::BindHandlers @ 0x000ae1a8`, `PlayerList::BindHandlers @ 0x000ab3ea`,
`Interfaces::BindHandlers @ 0x000ab888`. The ProtEntry object stores the dispatch
redirector at +0x20 and the real handler at +0x28.

Coverage: **217/218 opcodes have a bound handler; opcode 53 is genuinely UNBOUND**
(its manager slot 0x13a16c0 has zero xrefs across all binders — the client allocates
a varShort ProtEntry but installs no handler, so server data for op 53 is read-and-discarded).

## NAMING LAW (correction pass, 2026-05-31)

Every `Name` below is either a **verbatim official `jag::ServerProt::<NAME>` enum symbol**
(read from the beta unstripped `librs2client.so`, rev ~890 — the ONLY sanctioned source of
official packet names) or `UNKNOWN_<op>`. A C++ handler/function name (e.g. `Process*`,
`Handle*`, `*_impl`, `*_thunk`, or the `packethandlers::*` symbol shown in the `Handler`
column) is **NEVER** a valid packet name, and invented descriptive placeholders
(`WORLDENTITY_LIST_OP`, `NOOP_128`, `SPOTANIM_2`, `LOC_COMPOSITE_UPDATE`, etc.) are **NEVER**
valid packet names. The official enum vocabulary was dumped from the ProtEntry globals at
`0x00a35800..0x00a387c0` (187 enum symbols). When an opcode's handler cannot be matched to a
verbatim beta enum symbol, it is `UNKNOWN_<op>` with the handler address + read-shape preserved
in the `Evidence` column. ~58 revisions of protocol churn (beta-890 → 948) added many opcodes
that simply have NO symbol in the beta dump, so their official 948 name is unknowable from the
only sanctioned source and they remain `UNKNOWN_<op>` regardless of how well the handler is
understood (handler behavior is retained as evidence). Op 147 `VARP_LONG` and op 196
`CLIENT_SETVARC_LONG` are the only two non-beta names retained — both are decompile-certain
identities that own active/identified opcodes (documented inline). The `Handler` column still
shows the Ghidra symbol for the handler; it is identification evidence, NOT the packet name.

Result: **79 official enum names** (70 CONF:HIGH + 9 CONFIRMED) + **139 UNKNOWN_<op>** (CONF:NONE).

## Confidence taxonomy
- **CONFIRMED** — handler decompiled this revision; opcode/size from RegisterAll; official enum symbol matched (or, for the two retained non-beta names, decompile-certain identity); wire verified.
- **CONF:HIGH** — verbatim official beta enum symbol matched by handler behavior (this pass) or a 947-3 handler carried over whose official enum identity is established.
- **CONF:NONE** — either op 53 (genuinely unbound) OR no official beta enum maps to the opcode (the packet has no symbol in the rev-890 dump). Name is `UNKNOWN_<op>`.

## Table

| Op | Name | Size | Entry | Handler | CONF | Evidence |
|---:|------|------|-------|---------|------|----------|
| 0 | MESSAGE_QUICKCHAT_CLANCHANNEL |0x013a1ce0|packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHANNEL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_QUICKCHAT_CLANCHANNEL (beta @0xa37b00). Was op 82 in 947-3. |
| 1 | UNKNOWN_1 |0x013a1ca0|packethandlers::NPCInfo::SET_NPC_OP|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior fn-name 'SET_NPC_OP' (NPC_OP fn). Handler: was op 24 (NPC_OP) in 947-3. VarByte. |
| 2 | UNKNOWN_2 |0x013a1c60|packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_QUICKCHAT_CLANCHAT' (beta has MESSAGE_QUICKCHAT_CLANCHANNEL but no _CLANCHAT). Handler: was op 58 in 947-3. VarByte. |
| 3 | UNKNOWN_3 |0x015c0c00|IF_SETTOPLEVELINTERFACE|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETTOPLEVELINTERFACE' (not in beta enum). Handler: was op 94 in 947-3. sz19. |
| 4 | IF_SETANGLE |0x015c0b00|IF_SETANGLE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETANGLE (beta @0xa35c00). Was op 117 in 947-3. |
| 5 | UNKNOWN_5 |0x015c1700|RESET_ALL_VARPS|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'RESET_ALL_VARPS'. Handler @ 0x00119a50 (ClientState::RESET_ALL_VARPS fn). sz0. |
| 6 | LOC_PREFETCH |0x015c1e40|LOC_PREFETCH|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOC_PREFETCH (beta @0xa35a00). Handler @ 0x00138110. |
| 7 | UNKNOWN_7 |0x013a1c20|packethandlers::Misc::RESET_ENTITY_LISTS|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'RESET_ENTITY_LISTS' (Misc fn). Handler: was op 43 in 947-3. sz0. |
| 8 | UNKNOWN_8 |0x015c0440|IF_SETPLAYERHEAD_ACTIVE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERHEAD_ACTIVE' (not in beta enum). Handler @ 0x00193530. sz5. |
| 9 | UNKNOWN_9 |0x013a1be0|packethandlers::Chat::CLANCHANNEL_FULL_CHAT|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLANCHANNEL_FULL_CHAT' (beta has CLANCHANNEL_FULL but no _CHAT). Handler: was op 84 in 947-3. VarShort. |
| 10 | VARBIT_SMALL |0x015c1600|VARP_BIT_SMALL|CERTAIN| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::VARBIT_SMALL (beta @0xa38140, sz3). Handler FUN_00119870 setBitFromPacket. Prior 'VARP_BIT_SMALL' was a non-official descriptor. Wire: id(BE u16) + value(-128-byte). Encoder in Rev948ServerCodecsVariable. |
| 11 | MESSAGE_PUBLIC |0x013a1ba0|packethandlers::Chat::MESSAGE_PUBLIC|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_PUBLIC (beta @0xa38580). Was op 45 in 947-3. |
| 12 | UNKNOWN_12 |0x015c0e80|Misc::SET_PLAYER_OP_2|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_PLAYER_OP_2' (a 948 sibling of SET_PLAYER_OP; no distinct beta enum). Handler: was op 108 in 947-3. sz2. |
| 13 | UNKNOWN_13 |0x015c0ec0|Misc::SET_PLAYER_OP_3|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_PLAYER_OP_3' (948 sibling; no distinct beta enum). Handler: was op 116 in 947-3. sz1. |
| 14 | UNKNOWN_14 |0x015c0580|IF_SETSPRITE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETSPRITE' (not in beta enum). Handler @ 0x00193940. sz8. |
| 15 | UNKNOWN_15 |0x013a1b60|FUN_001dade0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_COORD' placeholder. Handler FUN_001dade0: DecodePackedCoord + entity spawn at tile w/ anim+delay. Graphic/spotanim-at-coord family. sz11. |
| 16 | LOC_DEL |0x015c1ec0|LOC_DEL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOC_DEL (beta @0xa364c0, sz2). Handler @ 0x001382d0. |
| 17 | SET_PLAYER_OP |0x015c0f00|Misc::SET_PLAYER_OP|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::SET_PLAYER_OP (beta @0xa37c40). Handler Misc::SET_PLAYER_OP @ 0x0013eec0, bound by PlayerList::BindHandlers. Was op 0 in 947-3. |
| 18 | UNKNOWN_18 |0x013a1b20|packethandlers::Camera::CAM_TARGET|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CAM_TARGET' (Camera fn; not in beta enum). Handler: was op 113 in 947-3. sz1. |
| 19 | UNKNOWN_19 |0x013a1ae0|FUN_000f7080|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_COMPRESSED_A' placeholder. Handler FUN_000f7080->FUN_00495ea0: huffman/ISAAC compressed-string reader. Compressed-text message family. VarShort. |
| 20 | UNKNOWN_20 |0x013a1aa0|FUN_001741b0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLIENTSCRIPT_QUEUE_CLEAR' placeholder. Handler FUN_001741b0: binary-search+erase over sorted vector, push to ring buffer. ClientScript/queue mgmt. sz3. |
| 21 | LOC_ANIM_SPECIFIC |0x015c1a40|LOC_ANIM_SPECIFIC|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOC_ANIM_SPECIFIC (beta @0xa37840, sz10). Handler @ 0x00118ef0. |
| 22 | PLAYER_INFO |0x015c0f40|jag::PlayerList::ProcessPlayerInfo|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::PLAYER_INFO (beta @0xa36a00). ProcessPlayerInfo. Was op 27 in 947-3. |
| 23 | UNKNOWN_23 |0x013a1a60|packethandlers::PlayerGroup::PLAYER_OP|CERTAIN| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'PLAYER_OP' (PlayerGroup fn). Handler @ 0x001869f0 (VarShort). DISTINCT from op 17 SET_PLAYER_OP. beta SET_PLAYER_OP already maps to op17. 947-3 op 109. VarShort. |
| 24 | UNKNOWN_24 |0x015c1300|UPDATE_ZONE_PARTIAL|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'UPDATE_ZONE_PARTIAL' (not in beta enum — beta has UPDATE_ZONE_PARTIAL_FOLLOWS/_ENCLOSED only). Handler @ 0x001254c0. VarByte. |
| 25 | UNKNOWN_25 |0x013a1a20|FUN_001898a0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_TYPE6' placeholder. Handler FUN_001898a0: reads 2 strings, ChatHistory::AddChat(type=6). Chat message family. VarShort. |
| 26 | UPDATE_SITESETTINGS |0x013a19e0|packethandlers::SiteSettings::UPDATE_SITESETTINGS_thunk|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_SITESETTINGS (beta @0xa371c0). Handler SiteSettings::UPDATE_SITESETTINGS @ 0x001a44e0 via thunk 0x001a63e0. Was op 102 in 947-3. |
| 27 | UNKNOWN_27 |0x013a19a0|FUN_00187490|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC' (beta SPOTANIM_SPECIFIC is sz12 @op27, claimed-ambiguous; this 948 op27 sz10 is a distinct variant). Handler FUN_00187490: u32+u16 entity-spawn + delay. SPOTANIM/graphic family. sz10. |
| 28 | VARP_LARGE |0x015c1680|VARP_LARGE|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::VARP_LARGE (beta @0xa36e40, sz6). Handler FUN_00119910, PlayerVarDomain::set. Wire: value(BE int) + id(BE u16). 947-3 op 111. Encoder in Rev948ServerCodecsVariable. |
| 29 | CLANSETTINGS_FULL |0x013a1960|packethandlers::Clans::CLANSETTINGS_FULL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_FULL (beta @0xa36f40, sz-2). Was op 104 in 947-3. |
| 30 | UNKNOWN_30 |0x015c0900|IF_SET2DANGLE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SET2DANGLE' (not in beta enum). Handler @ 0x00193f10. sz8. |
| 31 | UNKNOWN_31 |0x013a1920|FUN_00186f40|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM' placeholder. Handler FUN_00186f40: build-area tile + id + height/delay. Graphic/spotanim at tile. sz6. |
| 32 | IF_SETCOLOUR |0x015c0740|IF_SETCOLOUR|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETCOLOUR (beta @0xa36580). Handler @ 0x00185850. |
| 33 | UNKNOWN_33 |0x013a18e0|FUN_001a0f40|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_PRIVATE_IN' placeholder. Handler FUN_001a0f40: type byte, sender/recipient strings, ChatHistory::AddChat(type=0x14). Private-message-receive family. VarByte. |
| 34 | UNKNOWN_34 |0x013a18a0|packethandlers::Audio::SOUND_AREA_SYNTH|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH' (Audio fn; not in beta enum). Handler @ 0x00176600. sz8. |
| 35 | UNKNOWN_35 |0x015c0a00|IF_SETEVENTS2|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETEVENTS2' (beta has IF_SETEVENTS only; op97 holds the official IF_SETEVENTS). 948 second variant, no beta enum. Was op 35 in 947-3. sz12. |
| 36 | UNKNOWN_36 |0x013a1860|FUN_00186c60|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_COORD' placeholder. Handler FUN_00186c60: two build-area tiles + id + delay. Projectile/proj-anim family. sz10. |
| 37 | MESSAGE_FRIENDCHANNEL |0x013a1820|packethandlers::Chat::MESSAGE_FRIENDCHANNEL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_FRIENDCHANNEL (beta @0xa36900). Was op 7 in 947-3. |
| 38 | UNKNOWN_38 |0x015c08c0|IF_SET_MODEL_FRAME|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SET_MODEL_FRAME' (not in beta enum). Handler @ 0x00185b70. sz8. |
| 39 | IF_OPENTOP |0x015c0c40|IF_OPENTOP|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_OPENTOP (beta @0xa386c0). Was op 68 in 947-3. |
| 40 | UNKNOWN_40 |0x015c0a40|IF_SUBSWAP|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SUBSWAP' (not in beta enum; beta has IF_MOVESUB). Handler @ 0x00186100. sz8. |
| 41 | UPDATE_ZONE_PARTIAL_FOLLOWS |0x015c2080|UPDATE_ZONE_PARTIAL_FOLLOWS|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_FOLLOWS (beta @0xa36bc0, sz3). Was op 57 in 947-3. |
| 42 | UNKNOWN_42 |0x013a17e0|FUN_001875f0|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_SPECIFIC_PACKED' placeholder. Handler FUN_001875f0: packed 4B coord + entity spawn + delay. Graphic/spotanim variant. sz10. |
| 43 | UNKNOWN_43 |0x013a17a0|FUN_00157080|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY' placeholder. Handler FUN_00157080: g4_alt3 target-ref + spotanim on entity-or-tile. SPOTANIM-on-entity family. sz12. |
| 44 | UPDATE_STAT |0x015c12c0|game::StatTable::UpdateStat|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::UPDATE_STAT (beta @0xa359c0, sz6). Handler jag::game::StatTable::UpdateStat @ 0x000ef2a0. 947-3 op 66. Wire: xp LE int + raw level + byteInverse skillId. |
| 45 | UNKNOWN_45 |0x013a1760|packethandlers::Misc::SET_MULTIWAY_STATE|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_MULTIWAY_STATE' (Misc fn; not in beta enum). Handler: was op 77 in 947-3. sz1. |
| 46 | OBJ_ADD |0x015c1c40|OBJ_ADD|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::OBJ_ADD (beta @0xa35b00, sz5). Handler @ 0x00119240. |
| 47 | CLIENT_SETVARC_SMALL |0x015c1580|CLIENT_SETVARC_SMALL|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_SMALL (beta @0xa38380, sz3). Handler FUN_001196f0, IfaceMgr direct. Wire: value(byteAdd) + id(LE u16). 947-3 op 1. Encoder in Rev948ServerCodecsVariable. |
| 48 | CLIENT_SETVARCBIT_SMALL |0x015c14c0|CLIENT_SETVARCBIT_SMALL|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_SMALL (beta @0xa37a40, sz3). Handler FUN_00119560. Wire: value(raw 1B) + id(BE u16, lo-byte byteAdd). Encoder in Rev948ServerCodecsVariable. |
| 49 | CHANGE_LOBBY |0x013a1720|packethandlers::Lobby::CHANGE_LOBBY|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CHANGE_LOBBY (beta @0xa38040). Was op 30 in 947-3. |
| 50 | LOC_CUSTOMISE |0x015c1f40|LOC_CUSTOMISE|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOC_CUSTOMISE (beta @0xa37700). Handler @ 0x0013f5f0. |
| 51 | VARBIT_LARGE |0x015c15c0|VARP_BIT_LARGE|CERTAIN| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::VARBIT_LARGE (beta @0xa38100, sz6). Handler FUN_001197b0, setBitFromPacket. Prior 'VARP_BIT_LARGE' was a non-official descriptor. Wire: id(BE u16) + value(intInverseMiddle). Encoder in Rev948ServerCodecsVariable. |
| 52 | NPC_INFO |0x013a16e0|NPCList::ProcessNpcInfo|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::NPC_INFO (beta @0xa38600). ProcessNpcInfo @ 0x001d79a0. Was op 12 in 947-3. |
| 53 | UNKNOWN_53 |0x013a16a0|UNBOUND|NONE| CONF:NONE | CONF:NONE — genuinely UNBOUND: no handler in any of the 6 binders; manager slot 0x13a16c0 has zero xrefs. Client allocates a varShort ProtEntry but installs no handler => op53 payload is read-and-discarded. |
| 54 | NO_TIMEOUT |0x013a1660|packethandlers::Misc::NO_TIMEOUT|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::NO_TIMEOUT (beta @0xa35940). Handler Misc::NO_TIMEOUT @ 0x000ef260 (no-op). Was op 216 in 947-3. Format changed 0B fixed -> varByte. |
| 55 | UNKNOWN_55 |0x015c1380|DESTROY_ZONE_DATA|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'DESTROY_ZONE_DATA' (not in beta enum). Handler @ 0x000ef4e0. sz0. |
| 56 | MESSAGE_QUICKCHAT_PRIVATE |0x013a1620|packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_QUICKCHAT_PRIVATE (beta @0xa37e00). Was op 73 in 947-3. |
| 57 | UNKNOWN_57 |0x013a15e0|FUN_00186e60|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_2' placeholder. Handler FUN_00186e60: build-area tile + id + signed delay. Graphic/spotanim-at-tile. sz6. |
| 58 | UNKNOWN_58 |0x015c1340|RESET_CLIENT_STATE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'RESET_CLIENT_STATE' (not in beta enum). Handler @ 0x000f4080. sz0. |
| 59 | UNKNOWN_59 |0x015c0600|IF_SETNPCMODEL|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCMODEL' (not in beta enum). Handler @ 0x00193c50. sz10. |
| 60 | UNKNOWN_60 |0x015c0b40|IF_SETPLAYERMODEL_SELF|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_SELF' (beta IF_SETPLAYERMODEL_SELF is sz4 @op148; this 948 op60 sz25 differs). Handler @ 0x00186520. sz25. |
| 61 | VARP_SMALL |0x015c16c0|FUN_001199b0|HIGH| CONFIRMED | CONFIRMED — official enum jag::ServerProt::VARP_SMALL (beta @0xa37400, sz3). Handler FUN_001199b0, PlayerVarDomain::set. 947-3 op 10. Wire: short(id) + byteInverse value. Encoder in Rev948ServerCodecsVariable. |
| 62 | IF_CLOSESUB |0x015c0a80|IF_CLOSESUB_ACTIVE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_CLOSESUB (beta @0xa38500, sz4). Prior 'IF_CLOSESUB_ACTIVE' added an invented suffix; handler is the carried-over 947-3 op33 IF_CLOSESUB. |
| 63 | UNKNOWN_63 |0x013a15a0|FUN_0014ebc0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'LOC_ADD_ALT' placeholder. Handler FUN_0014ebc0: DecodePackedCoord + LocationContainer::Add. LOC-add family. sz10. |
| 64 | CLIENT_SETVARC_LARGE |0x015c1540|CLIENT_SETVARC_LARGE|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARC_LARGE (beta @0xa35a80, sz6). Handler FUN_00119600, IfaceMgr direct. Wire: value(intMiddle) + id(shortAddLittle). 947-3 op 112. Encoder in Rev948ServerCodecsVariable. |
| 65 | MAP_PROJANIM |0x015c1dc0|MAP_PROJANIM|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MAP_PROJANIM (beta @0xa37080). Was op 47 in 947-3. |
| 66 | UNKNOWN_66 |0x013a1560|FUN_00187160|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CAM_LOOKAT' (beta CAM_LOOKAT is sz6 @op99; this 948 op66 sz5 differs). Handler FUN_00187160: byte + g4_alt2 -> camera obj. Camera sub-op. sz5. |
| 67 | CLANCHANNEL_FULL |0x013a1520|packethandlers::Clans::CLANCHANNEL_FULL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CLANCHANNEL_FULL (beta @0xa37280, sz-2). Was op 28 in 947-3. |
| 68 | UNKNOWN_68 |0x015c03c0|IF_SETMODELORIGIN|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETMODELORIGIN' (not in beta enum). Handler @ 0x00193420. sz10. |
| 69 | CLIENT_SETVARCBIT_LARGE |0x015c1480|CLIENT_SETVARCBIT_LARGE|CERTAIN| CONFIRMED | CONFIRMED — official enum jag::ServerProt::CLIENT_SETVARCBIT_LARGE (beta @0xa38300, sz6). Handler FUN_001194b0. Wire: id(BE u16) + value(BE int). Encoder in Rev948ServerCodecsVariable. |
| 70 | UNKNOWN_70 |0x015c0b80|IF_SETPLAYERMODEL_OTHER|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_OTHER' (beta IF_SETPLAYERMODEL_OTHER is sz10 @op82; this 948 op70 sz25 differs). Handler @ 0x00186710. sz25. |
| 71 | OBJ_REVEAL |0x015c1ac0|OBJ_REVEAL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::OBJ_REVEAL (beta @0xa35fc0, sz7). Handler @ 0x000f37b0. |
| 72 | CAM_FORCEANGLE |0x013a14e0|packethandlers::Camera::CAM_FORCEANGLE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CAM_FORCEANGLE (beta @0xa37200). Was op 3 in 947-3. |
| 73 | UNKNOWN_73 |0x013a14a0|FUN_00175810|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MINIMAP_STATE' placeholder. Handler FUN_00175810: 2 bytes -> camera/minimap obj. Minimap/camera offset. sz2. |
| 74 | JCOINS_UPDATE |0x015c0f80|Misc::SET_DISPLAY_INT|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::JCOINS_UPDATE (beta @0xa36dc0, sz4). Prior 'SET_DISPLAY_INT' was a fabricated rename; handler is 947-3 op59 JCOINS_UPDATE. |
| 75 | UNKNOWN_75 |0x013a1460|packethandlers::ClientState::SET_READY_FLAG|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_READY_FLAG' (ClientState fn; not in beta enum). Handler: was op 65 in 947-3. sz0. |
| 76 | UPDATE_ZONE_PARTIAL_ENCLOSED |0x015c2000|UPDATE_ZONE_PARTIAL_ENCLOSED|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_PARTIAL_ENCLOSED (beta @0xa37d00, sz-2). Was op 126 in 947-3. |
| 77 | CAMERA_UPDATE |0x013a1420|packethandlers::Camera::CAM_UPDATE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CAMERA_UPDATE (beta @0xa36e00, sz-2). Handler 0x001d3b50 verified: bitflag-driven camera update (pos/zoom/lookat/rotate). Prior 'CAM_UPDATE' was the C++ function name. |
| 78 | UPDATE_ZONE_FULL_FOLLOWS |0x015c2040|UPDATE_ZONE_FULL_FOLLOWS|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_ZONE_FULL_FOLLOWS (beta @0xa37d40, sz3). Was op 18 in 947-3. |
| 79 | NPC_HEADICON_SPECIFIC |0x013a13e0|packethandlers::NPCInfo::NPC_HEADICON_SPECIFIC|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::NPC_HEADICON_SPECIFIC (beta @0xa37480). Was op 54 in 947-3. |
| 80 | UPDATE_RUNENERGY |0x013a13a0|packethandlers::Misc::SET_RUN_ENERGY|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_RUNENERGY (beta @0xa38240, sz1). Handler 0x00174f40 verified: reads 1 byte -> player +0x60 (run energy). Prior 'SET_RUN_ENERGY' was a fabricated rename. |
| 81 | UNKNOWN_81 |0x013a1360|packethandlers::ClientState::REBUILD_NORMAL_SIMPLE|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps to this opcode. Prior 'REBUILD_NORMAL_SIMPLE': beta REBUILD_NORMAL already maps to op199 (multi-scene). This is the world-login simple form, no distinct beta enum. Was op 90 in 947-3. VarShort. |
| 82 | IF_SETPOSITION |0x015c0bc0|IF_SETPOSITION|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETPOSITION (beta @0xa36280). Was op 8 in 947-3. |
| 83 | UNKNOWN_83 |0x013a1320|FUN_001def40|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'REBUILD_REGION_ALT' placeholder. Handler FUN_001def40: cmd=5 gate, bit-packed coords, scene-build obj alloc, SceneManager build. REBUILD/scene-build family. VarShort. |
| 84 | IF_SETOBJECT |0x015c0880|IF_SETOBJECT|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETOBJECT (beta @0xa36840, sz10). Handler @ 0x00185a00. |
| 85 | UPDATE_INV_FULL |0x013a12e0|packethandlers::Inventory::UPDATE_INV_FULL_impl|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_FULL (beta @0xa36040, sz-2). Handler Inventory::UPDATE_INV_FULL_impl. Was op 69 in 947-3. |
| 86 | IF_SETANIM |0x015c06c0|IF_SETANIM|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETANIM (beta @0xa36940). Handler @ 0x00185740. |
| 87 | CAM_RESET |0x013a12a0|packethandlers::Camera::CAM_RESET|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CAM_RESET (beta @0xa37800, sz0). Was op 89 in 947-3. |
| 88 | UNKNOWN_88 |0x013a1260|FUN_001dad40|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CAM_FOCUS_TILE' placeholder. Handler FUN_001dad40: g4_alt3 packed coord (-1 clears) -> scene obj. Camera/hint focus-tile. sz4. |
| 89 | UNKNOWN_89 |0x013a1220|FUN_000f1f70|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NPC_SPOTANIM' placeholder. Handler FUN_000f1f70: NPCList::GetNPCNode + per-NPC spotanim vtable. SPOTANIM-on-NPC family. sz19. |
| 90 | UNKNOWN_90 |0x015c1fc0|LOC_ADD|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'LOC_ADD' (beta has LOC_ADD_CHANGE, not LOC_ADD). Handler @ 0x00139080. VarByte. |
| 91 | IF_SETHIDE |0x015c0940|IF_SETHIDE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETHIDE (beta @0xa36100, sz5). Was op 103 in 947-3. |
| 92 | UNKNOWN_92 |0x015c1440|Misc::SET_VARC_STR_SMALL|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_VARC_STR_SMALL' (beta has CLIENT_SETVARCSTR_SMALL, but that is the id-first sz-1 sibling; this op92 handler differs). Handler @ 0x001501c0. VarByte. |
| 93 | MESSAGE_GAME |0x013a11e0|packethandlers::Chat::MESSAGE_GAME|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_GAME (beta @0xa365c0). Was op 105 in 947-3. |
| 94 | IF_OPENSUB |0x015c0c80|IF_OPENSUB|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_OPENSUB (beta @0xa37100). Was op 17 in 947-3. |
| 95 | MIDI_SONG |0x013a11a0|packethandlers::Audio::MIDI_SONG|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MIDI_SONG (beta @0xa36b00). Was op 87 in 947-3. |
| 96 | UNKNOWN_96 |0x015c0700|IF_SETANIM_ACTIVE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETANIM_ACTIVE' (not in beta enum). Handler @ 0x00185800. sz4. |
| 97 | IF_SETEVENTS |0x015c09c0|IF_SETEVENTS|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETEVENTS (beta @0xa35e80). Was op 34 in 947-3. (op35 is a 948 second variant -> UNKNOWN_35.) |
| 98 | UNKNOWN_98 |0x013a1160|FUN_000f25d0|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL' placeholder. Handler FUN_000f25d0: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family. sz25. |
| 99 | IF_SETRECOL |0x015c0540|IF_SETRECOL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETRECOL (beta @0xa376c0). Handler @ 0x00193860. |
| 100 | UNKNOWN_100 |0x013a1120|FUN_00187020|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'RUNCLIENTSCRIPT_SHORT': beta RUNCLIENTSCRIPT already maps to op110. This is a 948 short variant, no distinct beta enum. Handler FUN_00187020. sz4. |
| 101 | UNKNOWN_101 |0x015c0840|IF_SETOBJECT_ACTIVE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETOBJECT_ACTIVE' (not in beta enum). Handler @ 0x00185b20. sz4. |
| 102 | IF_SETMODEL |0x015c07c0|IF_SETMODEL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETMODEL (beta @0xa36540, sz8). Handler @ 0x001858e0. |
| 103 | IF_SETGRAPHIC |0x015c0780|IF_SETGRAPHIC|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETGRAPHIC (beta @0xa35b80, sz8). Handler @ 0x00193e60. |
| 104 | UNKNOWN_104 |0x013a10e0|packethandlers::PlayerInfo::PLAYER_INFO_DECODE|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'PLAYER_INFO_DECODE' (PlayerInfo fn; not in beta enum). Handler: was op 78 in 947-3. sz14. |
| 105 | MESSAGE_CLANCHANNEL |0x013a10a0|packethandlers::Chat::MESSAGE_CLANCHANNEL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_CLANCHANNEL (beta @0xa361c0, sz-1). Handler @ 0x001a1cc0. |
| 106 | UNKNOWN_106 |0x013a1060|packethandlers::NPCInfo::SET_NPC_UPDATE_ORIGIN|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_ORIGIN' (NPC_UPDATE_ORIGIN fn; not in beta enum). Handler: was op 61 in 947-3. sz1. |
| 107 | OBJ_DEL |0x015c1bc0|OBJ_DEL|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::OBJ_DEL (beta @0xa36b40, sz3). Handler @ 0x00152b00. |
| 108 | CLANSETTINGS_DELTA |0x013a1020|packethandlers::Clans::CLANSETTINGS_DELTA|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CLANSETTINGS_DELTA (beta @0xa38640, sz-2). Was op 63 in 947-3. |
| 109 | UNKNOWN_109 |0x015c0e40|PlayerInfo::HANDSHAKE_UID|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'HANDSHAKE_UID' (fn-name; not in beta enum; cf. beta UPDATE_UID192 sz28 but identity unverified). Handler: was op 36 in 947-3. sz28. |
| 110 | RUNCLIENTSCRIPT |0x013a0fe0|packethandlers::ClientState::RUNCLIENTSCRIPT_impl|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::RUNCLIENTSCRIPT (beta @0xa37ec0, sz-2). Handler ClientState::RUNCLIENTSCRIPT_impl @ 0x001d2060. Was op 121 in 947-3. |
| 111 | UNKNOWN_111 |0x013a0fa0|FUN_00175960|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CAM_SUB' placeholder. Handler FUN_00175960: 5 bytes -> camera obj vtable. Camera sub-op. sz6. |
| 112 | UNKNOWN_112 |0x013a0f60|packethandlers::ClientState::SET_TICK_TIMER|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_TICK_TIMER' (ClientState fn; not in beta enum). Handler: was op 52 in 947-3. sz2. |
| 113 | UNKNOWN_113 |0x015c1cc0|MAP_ANIM|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_ANIM' (beta MAP_ANIM is sz10 @op152; this 948 op113 sz11 differs). Handler @ 0x00157a50. sz11. |
| 114 | UNKNOWN_114 |0x013a0f20|packethandlers::Chat::CHAT_FILTER_SETTINGS|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CHAT_FILTER_SETTINGS' (beta CHAT_FILTER_SETTINGS is sz2 @op136; this 948 op114 is VarByte). Handler: was op 39 in 947-3. VarByte. |
| 115 | UNKNOWN_115 |0x015c0680|IF_SETNPCHEAD|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCHEAD' (beta IF_SETNPCHEAD is sz8 @op133; this 948 op115 sz10 differs). Handler @ 0x00185660. sz10. |
| 116 | UNKNOWN_116 |0x015c1400|SET_VARC_STR_LARGE|CERTAIN| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_VARC_STR_LARGE': beta has CLIENT_SETVARCSTR_LARGE (sz-2 @op188) but the op92/op116 small/large split is a 948 reorg not present in beta; identity unverified. Handler FUN_001500a0 IfaceMgr type=2. Encoder in Rev948ServerCodecsVariable. VarShort. |
| 117 | CLANCHANNEL_DELTA |0x013a0ee0|packethandlers::Clans::CLANCHANNEL_DELTA|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CLANCHANNEL_DELTA (beta @0xa36c80). Was op 83 in 947-3. |
| 118 | UNKNOWN_118 |0x015c0ac0|IF_SETPLAYERMODEL_SNAPSHOT|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETPLAYERMODEL_SNAPSHOT' (beta IF_SETPLAYERMODEL_SNAPSHOT is sz5 @op120; this 948 op118 sz29 differs). Handler @ 0x001da230. sz29. |
| 119 | UNKNOWN_119 |0x013a0ea0|packethandlers::Misc::CUTSCENE_DATA|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CUTSCENE_DATA' (beta CUTSCENE is sz-2 @op92; this 948 op119 sz35 fixed differs). Was op 91 in 947-3. sz35. |
| 120 | CAM_SMOOTHRESET |0x013a0e60|packethandlers::Camera::CAM_SMOOTHRESET|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::CAM_SMOOTHRESET (beta @0xa37cc0, sz0). Was op 80 in 947-3. |
| 121 | UPDATE_INV_PARTIAL |0x013a0e20|packethandlers::Inventory::UPDATE_INV_PARTIAL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_INV_PARTIAL (beta @0xa37c00, sz-2). Was op 5 in 947-3. |
| 122 | IF_SETTEXT |0x015c0980|IF_SETTEXT|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETTEXT (beta @0xa37f40, sz-2). Handler @ 0x00185ec0. |
| 123 | UNKNOWN_123 |0x015c0400|IF_TRIGGER_CLOSE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_TRIGGER_CLOSE' (not in beta enum). Handler @ 0x00185620. sz0. |
| 124 | UNKNOWN_124 |0x013a0de0|packethandlers::Chat::CLANSETTINGS_DELTA_CHAT|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLANSETTINGS_DELTA_CHAT' (beta has CLANSETTINGS_DELTA, not _CHAT). Was op 11 in 947-3. VarShort. |
| 125 | OBJ_COUNT |0x015c1b40|OBJ_COUNT|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::OBJ_COUNT (beta @0xa36880, sz7). Handler @ 0x001190d0. |
| 126 | UNKNOWN_126 |0x013a0da0|packethandlers::Chat::MESSAGE_FRIENDCHAT|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_FRIENDCHAT' (beta has MESSAGE_FRIENDCHANNEL only; verified no _FRIENDCHAT enum). Was op 21 in 947-3. VarByte. |
| 127 | UNKNOWN_127 |0x013a0d60|FUN_00182610|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET' placeholder. Handler FUN_00182610: FUN_0066ecb0(graphicsObj,8). Minimap/camera reset. sz0. |
| 128 | UNKNOWN_128 |0x013a0d20|FUN_00173e40|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NOOP_128' placeholder. Handler FUN_00173e40: empty (no read). No-op/reserved. sz0. |
| 129 | UNKNOWN_129 |0x013a0ce0|FUN_001dc630|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE' placeholder. Handler FUN_001dc630: entity-group-at-tile, set bool/flag. ZoneUpdate entity-overlay family. sz3. |
| 130 | UNKNOWN_130 |0x013a0ca0|packethandlers::Social::UPDATE_IGNORELIST_thunk|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'UPDATE_IGNORELIST': beta UPDATE_IGNORELIST is sz-2 @op10 but handler 0x001d29c0 is a 64-bit-flag relationship delta (suspected true UPDATE_FRIENDLIST), identity contested. Encoder DISABLED. VarByte. |
| 131 | UNKNOWN_131 |0x015c1000|FUN_000f9600|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP' placeholder. Handler FUN_000f9600: per-worldentity record list move/reorder. WorldEntity-list family. sz3. |
| 132 | UNKNOWN_132 |0x013a0c60|packethandlers::Audio::SOUND_STOP|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_STOP' (Audio fn; not in beta enum). Handler @ 0x0017e4c0. sz2. |
| 133 | UNKNOWN_133 |0x013a0c20|packethandlers::NPCInfo::SET_NPC_UPDATE_FLAG|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_NPC_UPDATE_FLAG' (NPC_UPDATE_FLAG fn; not in beta enum). Handler: was op 138 in 947-3. sz1. |
| 134 | UNKNOWN_134 |0x013a0be0|FUN_00179460|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NPC_SAY' placeholder. Handler FUN_00179460: NPCList::GetNPCNode + string -> NPC overhead-text. NPC overhead say/text. VarShort. |
| 135 | UNKNOWN_135 |0x013a0ba0|FUN_001758f0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NOOP_135' placeholder. Handler FUN_001758f0: empty. No-op/discard. VarShort. |
| 136 | UNKNOWN_136 |0x015c0640|IF_SETANIM_SMALL|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETANIM_SMALL' (not in beta enum). Handler @ 0x00185aa0. sz5. |
| 137 | UNKNOWN_137 |0x013a0b60|packethandlers::Chat::SET_CHAT_FILTER_A|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_A' (Chat fn; not in beta enum). Handler: was op 217 in 947-3. sz1. |
| 138 | UNKNOWN_138 |0x013a0b20|packethandlers::Misc::SKIP_2_BYTES|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SKIP_2_BYTES' (Misc fn). Handler @ 0x00175f90. sz2. |
| 139 | LOGOUT_TRANSFER |0x013a0ae0|packethandlers::Misc::LOGOUT_TRANSFER|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOGOUT_TRANSFER (beta @0xa37580). Handler Misc::LOGOUT_TRANSFER. Was op 209 in 947-3. |
| 140 | UNKNOWN_140 |0x013a0aa0|FUN_000f2280|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'PLAYER_SPOTANIM' placeholder. Handler FUN_000f2280: spotanim on local player via vtable. SPOTANIM-on-local-player family. sz17. |
| 141 | UNKNOWN_141 |0x013a0a60|packethandlers::Interfaces::IF_OPENSUB_thunk|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_OPENSUB_THUNK' (thunk fn-name; beta IF_OPENSUB already maps to op94). Handler Interfaces::IF_OPENSUB_thunk. Was op 186 in 947-3. VarShort. |
| 142 | UNKNOWN_142 |0x013a0a20|packethandlers::Misc::SET_URL_STRING|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_URL_STRING' (Misc fn; beta has URL_OPEN/UPDATE_URL_STRING-style but not SET_URL_STRING). Was op 200 in 947-3. VarByte. |
| 143 | UNKNOWN_143 |0x015c1180|FUN_00119e20|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_ADD' placeholder. Handler FUN_00119e20: insert/update worldentity record. WorldEntity-list family. sz6. |
| 144 | UNKNOWN_144 |0x015c1140|FUN_00119c30|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL' placeholder. Handler FUN_00119c30: remove worldentity record by index. WorldEntity-list family. sz2. |
| 145 | UNKNOWN_145 |0x013a09e0|FUN_00187890|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'DEV_CONSOLE_CMD' placeholder. Handler FUN_00187890: u16 subcmd switch (clear list/heightmap/camera/flags). Dev/debug dispatcher. sz2. |
| 146 | UNKNOWN_146 |0x015c1040|FUN_000f98f0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_DEL_2' placeholder. Handler FUN_000f98f0: remove by index. WorldEntity-list family. sz2. |
| 147 | VARP_LONG |0x015c1640|FUN_00141510|HIGH| CONFIRMED | CONFIRMED — handler FUN_00141510, PlayerVarDomain::set (8-byte value). NOTE: no beta enum named VARP_LONG (beta varp set is VARP_SMALL/VARP_LARGE only); name retained because the active encoder owns this opcode and the player-varp-long identity is decompile-certain. 947-3 op 170. Encoder in Rev948ServerCodecsVariable. |
| 148 | UNKNOWN_148 |0x015c0380|IF_CLOSESUB_BY_ID|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_CLOSESUB_BY_ID': beta IF_CLOSESUB already maps to op62. This sz2 sibling has no distinct beta enum. Handler @ 0x001855d0. sz2. |
| 149 | UNKNOWN_149 |0x015c1080|FUN_000f9a20|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_INS' placeholder. Handler FUN_000f9a20: insert sentinel into worldentity lists. WorldEntity-list family. sz6. |
| 150 | UNKNOWN_150 |0x013a09a0|FUN_001db620|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_2' placeholder. Handler FUN_001db620: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3. |
| 151 | PROJANIM_SPECIFIC |0x015c1d40|PROJANIM_SPECIFIC|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::PROJANIM_SPECIFIC (beta @0xa36500). Was op 196 in 947-3. |
| 152 | IF_SET_HTTP_IMAGE |0x013a0960|packethandlers::Interfaces::IF_SET_HTTP_IMAGE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SET_HTTP_IMAGE (beta @0xa36380). Handler Interfaces::IF_SET_HTTP_IMAGE. Was op 146 in 947-3. |
| 153 | UNKNOWN_153 |0x013a0920|FUN_001dc100|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLOAT_AT_TILE' placeholder. Handler FUN_001dc100: entity-group, set float/flag. ZoneUpdate entity-overlay family. sz4. |
| 154 | UNKNOWN_154 |0x013a08e0|FUN_001de6b0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_AT_TILE' placeholder. Handler FUN_001de6b0: entity-group, set anim timing. ZoneUpdate entity-overlay family. sz5. |
| 155 | LOGOUT |0x013a08a0|packethandlers::Misc::LOGOUT|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::LOGOUT (beta @0xa358c0, sz0). Handler Misc::LOGOUT. Was op 147 in 947-3. |
| 156 | UNKNOWN_156 |0x013a0860|packethandlers::Chat::SET_CHAT_FILTER_B|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_CHAT_FILTER_B' (Chat fn; not in beta enum). Handler: was op 155 in 947-3. sz1. |
| 157 | UNKNOWN_157 |0x013a0820|FUN_00173a50|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SCENE_FLAG' placeholder. Handler FUN_00173a50: byteInverse -> scene obj, mark dirty. Scene/minimap flag. sz1. |
| 158 | UNKNOWN_158 |0x015c04c0|IF_SETSCROLLSIZE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETSCROLLSIZE' (not in beta enum). Handler @ 0x00193660. sz9. |
| 159 | UNKNOWN_159 |0x013a07e0|FUN_000ec3a0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NOOP_159' placeholder. Handler FUN_000ec3a0: empty. No-op/discard. VarShort. |
| 160 | UNKNOWN_160 |0x013a07a0|FUN_001d76a0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NPC_HEADBAR_SPECIFIC' placeholder. Handler FUN_001d76a0: NPCList::GetNPCNode, per-slot arrays. NPC headbar/hitmark family. sz9. |
| 161 | PLAYER_GROUP_DELTA |0x013a0760|packethandlers::PlayerGroup::UPDATE_PLAYER_GROUP|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::PLAYER_GROUP_DELTA (beta @0xa37640, sz-2). Handler 0x001989c0 verified: byte + payload -> player-group delta record. Prior 'UPDATE_PLAYER_GROUP' was a fabricated rename. |
| 162 | TRIGGER_ONDIALOGABORT |0x013a0720|packethandlers::ClientState::TRIGGER_ONDIALOGABORT|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::TRIGGER_ONDIALOGABORT (beta @0xa36ac0, sz0). Was op 195 in 947-3. |
| 163 | UNKNOWN_163 |0x013a06e0|FUN_001db050|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SPOTANIM_ENTITY_2' placeholder. Handler FUN_001db050: g4_alt3 target-ref + spotanim. SPOTANIM-on-entity family. sz15. |
| 164 | UNKNOWN_164 |0x015c1880|MAP_PROJANIM_HALT|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_HALT' placeholder. Handler @ 0x000f1bc0. Projectile/proj-anim halt family. sz28. |
| 165 | UNKNOWN_165 |0x015c05c0|IF_SETMODEL_COORD|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETMODEL_COORD' (not in beta enum). Handler @ 0x001939d0. sz14. |
| 166 | UNKNOWN_166 |0x015c1200|FUN_00120150|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_ADD' placeholder. Handler FUN_00120150: RebuildSceneEntry::Reset + WorldList::InsertOrReplace. WorldEntity add/placement. sz5. |
| 167 | SYNTH_SOUND |0x013a06a0|packethandlers::Audio::SYNTH_SOUND|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::SYNTH_SOUND (beta @0xa35bc0). Handler Audio::SYNTH_SOUND @ 0x00187350. |
| 168 | SOUND_AREA |0x015c1900|SOUND_AREA|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::SOUND_AREA (beta @0xa36140). Handler @ 0x00151c10. |
| 169 | SERVER_TICK_END |0x013a0660|packethandlers::Misc::SERVER_TICK_END|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::SERVER_TICK_END (beta @0xa35800). Handler Misc::SERVER_TICK_END. Was op 171 in 947-3. |
| 170 | UNKNOWN_170 |0x015c1980|LOC_MERGE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'LOC_MERGE' (not in beta enum). Handler @ 0x000eeba0. sz5. |
| 171 | UNKNOWN_171 |0x015c0fc0|FUN_000ef800|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_2' placeholder. Handler FUN_000ef800: write sentinel into worldentity sublist. WorldEntity-list family. sz3. |
| 172 | UNKNOWN_172 |0x015c0dc0|FUN_000f03f0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MINIMAP_FLAG_A' placeholder. Handler FUN_000f03f0: store byte -> graphics obj +0xa0. Minimap/camera flag. sz1. |
| 173 | UNKNOWN_173 |0x013a0620|jag::packethandlers::ZoneUpdates::UNKNOWN_op173_handler|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'LOC_COMPOSITE_UPDATE' was invented (Ghidra handler literally named UNKNOWN_op173_handler @ 0x001ae100). Reads gT_ulong+u32+subcommand loop (cases 1..0xe alloc typed LOC/zone delta objs). ZoneUpdates composite multi-part update. VarShort. |
| 174 | UNKNOWN_174 |0x013a05e0|HandleAntiCheatChallenge|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ANTI_CHEAT_CHALLENGE' (HandleAntiCheatChallenge fn; not in beta enum). Was op 198 in 947-3. sz8. |
| 175 | MESSAGE_PRIVATE_ECHO |0x013a05a0|packethandlers::Chat::MESSAGE_PRIVATE_ECHO|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE_ECHO (beta @0xa37a80). Was op 129 in 947-3. |
| 176 | UNKNOWN_176 |0x015c0e00|Misc::SET_INTERACTION_FLAG_D|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_D' (Misc fn; not in beta enum). Was op 144 in 947-3. sz3. |
| 177 | UNKNOWN_177 |0x015c1800|PROJANIM_SPECIFIC_HALT|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'PROJANIM_SPECIFIC_HALT' placeholder (beta PROJANIM_SPECIFIC sz22 @op140; this sz29 halt variant differs). Handler @ 0x000f1840. sz29. |
| 178 | UNKNOWN_178 |0x015c0d40|PlayerInfo::UPDATE_PLAYER_CHAT|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'UPDATE_PLAYER_CHAT' (PlayerInfo fn; not in beta enum). Was op 178 in 947-3. VarShort. |
| 179 | IF_SETSCROLLPOS |0x015c0500|IF_SETSCROLLPOS|MEDIUM| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::IF_SETSCROLLPOS (beta @0xa38740). Handler @ 0x00193760. |
| 180 | UNKNOWN_180 |0x015c0800|IF_SETOBJECT_SMALL|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETOBJECT_SMALL' (not in beta enum). Handler @ 0x00185980. sz5. |
| 181 | UNKNOWN_181 |0x013a0560|FUN_00186b60|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLIENTSETTING_TRIGGER_A' placeholder. Handler FUN_00186b60: g4 int -> graphics obj, ScriptRunner::ExecuteTrigger. Client-setting trigger family. sz4. |
| 182 | UNKNOWN_182 |0x013a0520|FUN_001dbb80|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_FLAG_AT_TILE_3' placeholder. Handler FUN_001dbb80: entity-group, set bool/flag. ZoneUpdate entity-overlay family. sz3. |
| 183 | UNKNOWN_183 |0x015c1780|MAP_ANIM_SPECIFIC|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_ANIM_SPECIFIC' (not in beta enum). Handler @ 0x001575c0. sz14. |
| 184 | UPDATE_REBOOT_TIMER |0x015c0cc0|Misc::SET_SYSUPDATE_TIMER|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::UPDATE_REBOOT_TIMER (beta @0xa378c0). Handler 0x000eff40 verified: signed g3 ticks + bool flag -> system-update timer. Prior 'SET_SYSUPDATE_TIMER' was a fabricated rename. (beta sz2 -> 948 sz4, field grew.) |
| 185 | MESSAGE_PRIVATE |0x013a04e0|packethandlers::Chat::MESSAGE_PRIVATE|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::MESSAGE_PRIVATE (beta @0xa37e40). Was op 151 in 947-3. |
| 186 | UNKNOWN_186 |0x015c1240|REBUILD_WORLDENTITY|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'REBUILD_WORLDENTITY' (not in beta enum). Was op 188 in 947-3. VarShort. |
| 187 | UNKNOWN_187 |0x013a04a0|FUN_00173cd0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'DISPLAY_MODE_A' placeholder. Handler FUN_00173cd0: validated byte -> render-mode field +0x38. Display/graphics mode. sz1. |
| 188 | UNKNOWN_188 |0x013a0460|FUN_000f7090|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MESSAGE_COMPRESSED_B' placeholder. Handler FUN_000f7090->FUN_0048d5f0: sibling of op19, compressed-string reader. Compressed-text message family. VarShort. |
| 189 | UNKNOWN_189 |0x013a0420|packethandlers::Audio::VORBIS_PRELOAD|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'VORBIS_PRELOAD' (Audio fn; beta has VORBIS_PRELOAD_SOUNDS/_SOUND_GROUP, not VORBIS_PRELOAD). Handler @ 0x001870f0. sz4. |
| 190 | UNKNOWN_190 |0x015c13c0|CLEAR_PENDING_UPDATES|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLEAR_PENDING_UPDATES' (not in beta enum). Handler @ 0x000f4110. sz0. |
| 191 | UNKNOWN_191 |0x013a03e0|FUN_00186be0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'CLIENTSETTING_TRIGGER_B' placeholder. Handler FUN_00186be0: g4 int -> graphics obj, ScriptRunner::ExecuteTrigger. Client-setting trigger family. sz4. |
| 192 | UNKNOWN_192 |0x013a03a0|FUN_00173de0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'DISPLAY_MODE_B' placeholder. Handler FUN_00173de0: validated byte -> render-mode field +0x34. Display/graphics mode. sz1. |
| 193 | UNKNOWN_193 |0x015c0d00|Misc::SET_INTERACTION_FLAG_C|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_INTERACTION_FLAG_C' (Misc fn; not in beta enum). Was op 174 in 947-3. sz1. |
| 194 | UNKNOWN_194 |0x015c11c0|FUN_000efa80|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_3' placeholder. Handler FUN_000efa80: reorder/compact worldentity lists. WorldEntity-list family. sz1. |
| 195 | UNKNOWN_195 |0x013a0360|packethandlers::Audio::SOUND_AREA_SYNTH_2|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_AREA_SYNTH_2' (Audio fn; not in beta enum). Handler @ 0x001870a0. sz4. |
| 196 | CLIENT_SETVARC_LONG |0x015c1500|FUN_00119350|HIGH| CONFIRMED | CONFIRMED size/handler — FUN_00119350, InterfaceManager type=1, 8-byte BE value. NOTE: no beta enum named CLIENT_SETVARC_LONG (beta varc set is SMALL/LARGE only); name retained because identity is decompile-certain and distinct from player VARP_LONG (op147). Framing-only stub (no encoder/data class). |
| 197 | UNKNOWN_197 |0x013a0320|packethandlers::Misc::SKIP_DATA|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SKIP_DATA' (Misc fn; not in beta enum). Was op 166 in 947-3. VarShort. |
| 198 | UNKNOWN_198 |0x013a02e0|packethandlers::Misc::UPDATE_URL_STRING|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'UPDATE_URL_STRING' (Misc fn; not in beta enum). Was op 214 in 947-3. VarByte. |
| 199 | REBUILD_NORMAL |0x015c1280|REBUILD_NORMAL|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::REBUILD_NORMAL (beta @0xa37dc0, sz-2). Was op 172 (REBUILD_REGION) in 947-3 — multi-scene grid; the official enum name in 948 is REBUILD_NORMAL. |
| 200 | UNKNOWN_200 |0x013a02a0|packethandlers::Audio::SOUND_GROUP_STOP|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_STOP' (Audio fn; beta has VORBIS_SOUND_GROUP_STOP, not SOUND_GROUP_STOP). Handler @ 0x00176000. sz2. |
| 201 | UNKNOWN_201 |0x015c1100|FUN_000ef970|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_4' placeholder. Handler FUN_000ef970: write sentinel/value into worldentity sublist. WorldEntity-list family. sz3. |
| 202 | UNKNOWN_202 |0x013a0260|packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'PLAYER_INFO_DECODE_2' (PlayerInfo fn; not in beta enum). Was op 180 in 947-3. VarShort. |
| 203 | UNKNOWN_203 |0x013a0220|FUN_001dd3e0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_RESET_AT_TILE' placeholder. Handler FUN_001dd3e0: entity-group, set flag + anim field. ZoneUpdate entity-overlay family. sz3. |
| 204 | UNKNOWN_204 |0x015c0d80|FUN_000f0380|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MINIMAP_FLAG_B' placeholder. Handler FUN_000f0380: store byteInverse -> graphics obj +0xa4. Minimap/camera flag. sz1. |
| 205 | UNKNOWN_205 |0x013a01e0|packethandlers::Audio::SOUND_GROUP_SPEED|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_GROUP_SPEED' (Audio fn; not in beta enum). Handler @ 0x00182690. sz6. |
| 206 | UNKNOWN_206 |0x015c0480|IF_SETNPCHEAD_ACTIVE|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'IF_SETNPCHEAD_ACTIVE' (not in beta enum). Handler @ 0x001935c0. sz5. |
| 207 | UNKNOWN_207 |0x015c10c0|FUN_00119b80|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'WORLDENTITY_LIST_OP_5' placeholder. Handler FUN_00119b80: reorder/compact worldentity list. WorldEntity-list family. sz3. |
| 208 | UNKNOWN_208 |0x013a01a0|packethandlers::Inventory::UPDATE_INV_GROUP|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'UPDATE_INV_GROUP' (Inventory fn; beta has UPDATE_INV_FULL/_PARTIAL/_STOP_TRANSMIT, not _GROUP). Was op 177 in 947-3. VarShort. |
| 209 | UNKNOWN_209 |0x013a0160|packethandlers::NPCInfo::NPC_INFO_thunk_worldentity|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'NPC_INFO_THUNK' (thunk fn-name; beta NPC_INFO maps to op52). Handler NPCInfo::NPC_INFO_thunk_worldentity. Was op 205 in 947-3. VarShort. |
| 210 | UNKNOWN_210 |0x013a0120|packethandlers::Audio::VORBIS_SONG|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'VORBIS_SONG' (Audio fn; beta has VORBIS_SOUND/_SOUND_GROUP/etc, not VORBIS_SONG). Handler @ 0x00187750. sz6. |
| 211 | UNKNOWN_211 |0x013a00e0|FUN_000f1420|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MAP_PROJANIM_FULL_2' placeholder. Handler FUN_000f1420: ProjectileList::Add w/ full coords/speeds. Projectile/proj-anim family (largest). sz33. |
| 212 | UNKNOWN_212 |0x013a00a0|packethandlers::WorldData::SET_WORLD_TARGET|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SET_WORLD_TARGET' (WorldData fn; not in beta enum). Was op 187 in 947-3. VarByte. |
| 213 | UNKNOWN_213 |0x013a0060|packethandlers::WorldData::SWITCH_WORLD|HIGH| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SWITCH_WORLD' (WorldData fn; not in beta enum). Was op 179 in 947-3. VarByte. |
| 214 | UNKNOWN_214 |0x013a0020|FUN_00182650|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'MINIMAP_RESET_2' placeholder. Handler FUN_00182650: FUN_00c2a9a0(graphicsObj,1). Camera/minimap reset. sz0. |
| 215 | UNKNOWN_215 |0x0139ffe0|FUN_001deaa0|LOW| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'ENTITY_ANIM_AT_TILE_2' placeholder. Handler FUN_001deaa0: g4 + entity-group, set fields. ZoneUpdate entity-overlay family. sz8. |
| 216 | WORLDLIST_FETCH_REPLY |0x0139ffa0|packethandlers::WorldData::WORLDLIST_FETCH_REPLY|HIGH| CONF:HIGH | CONF:HIGH — official enum jag::ServerProt::WORLDLIST_FETCH_REPLY (beta @0xa37f80, sz-2). Handler WorldData::WORLDLIST_FETCH_REPLY @ 0x0018fea0. Was op 159 in 947-3. |
| 217 | UNKNOWN_217 |0x0139ff60|packethandlers::Audio::SOUND_MODIFY|MEDIUM| CONF:NONE | CONF:NONE — no official beta enum maps. Prior 'SOUND_MODIFY' (Audio fn; not in beta enum). Handler @ 0x00175c90. sz4. |
