# 948-2-2 Opcode Tables — ServerProt + ClientProt

**Status:** Phase 1 deliverable for 947→948 migration, **superseded in detail by the deep-RE campaign**.

> ## Deep-RE campaign (authoritative per-subsystem detail)
> This Phase-1 table was the first-pass map. A subsequent evidence-first campaign (rule: *no name without proof* — handler-body self-ID, confident beta sig-match, or capture-confirmed byte layout) re-verified every subsystem and corrected numerous wire formats the Phase-1 "presumed-equivalent" assumption got wrong. For any opcode, the per-subsystem research docs below are the authority over the tables in this file:
> - **`948-research-A-state-var-zone.md`** — VARP/VARC family (all formats changed), zone frames, rebuild. Confirmed: VARP_SMALL→op61, VARP_LARGE→op28, VARP_LONG→op147, VARP_BIT_SMALL→op10, VARP_BIT_LARGE→op51, CLIENT_SETVARC_SMALL→op47, CLIENT_SETVARC_LARGE→op64, CLIENT_SETVARCBIT_SMALL→op48, CLIENT_SETVARCBIT_LARGE→op69, CLIENT_SETVARC_STR→op92, SET_VARC_STR_LARGE→op116, CLIENT_SETVARC_LONG→op196, UPDATE_STAT→op44 (handler 0x000ef2a0, all 3 fields' transforms changed). REBUILD_NORMAL_SIMPLE magic byte 0x7B→0x85.
> - **`948-research-B-interface-social.md`** — ~22 IF_* wire formats were WRONG in the first codec (copied verbatim from 947-3) and were corrected. No dedicated Chat/Clans/Social binder; all via `ServerProt::BindHandlers @ 0x0007509a`. op 130 "UPDATE_IGNORELIST" identity SUSPECT (likely true UPDATE_FRIENDLIST delta) — encoder disabled pending capture. op 23 PlayerGroup::PLAYER_OP (VarShort) ≠ op 17 Misc::SET_PLAYER_OP (VarByte).
> - **`948-research-C-player-npc-misc.md`** — PLAYER_INFO op22 / NPC_INFO op52. APPEARANCE mask moved to **bit 3**; full player mask expansion bits {0,13,22}, NPC {6,8,19,25} (prior NPC enum was materially wrong). 948 ext-info scalars carry a per-field 1-byte mode selector (server emits 0).
> - **`948-research-D-clientprot.md`** — all 130 ClientProt sizes locked from RegisterAll; project-undercut CSV opcode column drifts +1 across ops 71–90 (corrected; fixed 3 inbound decoders that would have desynced).
>
> Tables below retain Phase-1 confidence tags; treat `M`/`L`/`?` rows as superseded by the research docs where they overlap.

**Status (original):** Phase 1 deliverable for 947→948 migration.

**Authority:** rs2client.948-2-2 (Ghidra port 8082, STRIPPED). Cross-confirmed against project-undercut CSV (`re-resources/948-2-phase4a/`) and behavioral walks of key handlers. All names below are sourced from one of:
- Ghidra's already-applied label (e.g. `IF_OPENTOP`, `IF_SETANGLE`) — these are stripped-binary labels created by project-undercut/Ghidra auto-analysis, verified by decompile pattern match against 947-3.
- BindHandlers cross-reference: I walked `jag::ServerProt::BindHandlers @ 0x0007509a` and `jag::packethandlers::PlayerList::BindHandlers @ 0x000ab3ea` (newly named in this session) to confirm each handler binding.
- 947-3 → 948 migration via handler short-name match (e.g. 947-3 `Chat::MESSAGE_PUBLIC` maps to 948 op whose handler is `packethandlers::Chat::MESSAGE_PUBLIC`).

**Confidence tags in the tables below:**
- **H** = high confidence: handler name is in the binary (Ghidra label or BindHandlers binding) AND decompile matches 947-3 equivalent
- **M** = medium confidence: name resolved by transitive logic (e.g., CSV used `*_thunk` name but real handler is X)
- **L** = low confidence: name guessed from sibling clues or known-but-unverified
- **?** = unknown: handler is `FUN_xxxxxxxx` with no name resolution yet — left as `UNKNOWN_<op>`

**Ground-truth anchors used (all in rs2client.948-2-2):**
- `jag::ServerProt::RegisterAll @ 0x000c4700` — initializes ProtEntry table for 218 ServerProt opcodes
- `jag::ClientProt::RegisterAll @ 0x000c45b0` — initializes ProtEntry table for 130 ClientProt opcodes (in same function as ServerProt's)
- `jag::ServerProt::BindHandlers @ 0x0007509a` — binds 125+ handlers directly
- `jag::packethandlers::PlayerList::BindHandlers @ 0x000ab3ea` — binds 13 PlayerList/Misc handlers (newly identified in this session)
- `InitEntry @ 0x000e5e30` — `void(undefined4 *entry, int opcode, int size)`
- `InitSubEntry @ 0x000e5e90` — `void(undefined4 *subEntry, int subOpcode, int size)`

**Critical caveat per project memory:** Pre-existing Ghidra labels (`IF_OPENTOP`, `IF_SETPOSITION`, etc.) in the stripped 948-2-2 project are NOT authoritative — they were applied by project-undercut's Phase 4a heuristics. The 947-3 round caught 5+ wrong labels (e.g., `IF_OPENTOP` was fabricated as op 2B, real op was 6B). For 948 I verified the world-login handler set (REBUILD_NORMAL_SIMPLE, PLAYER_INFO, NPC_INFO, REBUILD_WORLDENTITY, REBUILD_NORMAL, all three UPDATE_ZONE_*) by full decompile; the remainder retain their CSV-supplied names but should be re-verified before being used in production codec code.

---

## ServerProt Table (218 entries, opcodes 0–217)

Format: `Opcode (hex) | Size | Handler Name | 947-3 Origin | Confidence | Handler Addr`

Size codes: positive int = fixed byte length; `-1` = varByte (1-byte length prefix); `-2` = varShort (2-byte length prefix).

| Op | Hex | Size | Handler Name (948) | 947-3 Origin | Conf | Handler Addr |
|---:|----|-----:|---|---|---|---|
| 0 | 0x00 | -1 | MESSAGE_QUICKCHAT_CLANCHANNEL | was op 82 (MESSAGE_QUICKCHAT_CLANCHANNEL) | H | 0x001a1530 |
| 1 | 0x01 | -1 | SET_NPC_OP | was op 24 (NPC_OP) | H | 0x0017fdb0 |
| 2 | 0x02 | -1 | MESSAGE_QUICKCHAT_CLANCHAT | was op 58 (MESSAGE_QUICKCHAT_CLANCHAT) | H | 0x001a2160 |
| 3 | 0x03 | 19 | IF_SETTOPLEVELINTERFACE | was op 94 (IF_SETTOPLEVELINTERFACE) | H | 0x00186900 |
| 4 | 0x04 | 32 | IF_SETANGLE | was op 117 (IF_SETANGLE) | H | 0x001da480 |
| 5 | 0x05 | 0 | RESET_ALL_VARPS | new in 948 (no 947-3 equivalent) | M | 0x00119a50 |
| 6 | 0x06 | 7 | LOC_PREFETCH | new in 948 (no 947-3 equivalent) | M | 0x00138110 |
| 7 | 0x07 | 0 | RESET_ENTITY_LISTS | was op 43 (RESET_ENTITY_LISTS) | H | 0x00143bd0 |
| 8 | 0x08 | 5 | IF_SETPLAYERHEAD_ACTIVE | new in 948 (no 947-3 equivalent) | M | 0x00193530 |
| 9 | 0x09 | -2 | CLANCHANNEL_FULL_CHAT | was op 84 (CLANCHANNEL_FULL_CHAT) | H | 0x00183b20 |
| 10 | 0x0a | 3 | UNKNOWN_10 | — | ? | 0x00119870 |
| 11 | 0x0b | -2 | MESSAGE_PUBLIC | was op 45 (MESSAGE_PUBLIC) | H | 0x00197c40 |
| 12 | 0x0c | 2 | SET_PLAYER_OP_2 | was op 108 (SET_PLAYER_OP_2) | H | 0x000f0510 |
| 13 | 0x0d | 1 | SET_PLAYER_OP_3 | was op 116 (SET_PLAYER_OP_3) | H | 0x000f05a0 |
| 14 | 0x0e | 8 | IF_SETSPRITE | new in 948 (no 947-3 equivalent) | M | 0x00193940 |
| 15 | 0x0f | 11 | UNKNOWN_15 | — | ? | 0x001dade0 |
| 16 | 0x10 | 2 | LOC_DEL | new in 948 (no 947-3 equivalent) | M | 0x001382d0 |
| 17 | 0x11 | -1 | UNKNOWN_17 | — | ? | 0x0013eec0 |
| 18 | 0x12 | 1 | CAM_TARGET | was op 113 (CAM_TARGET) | H | 0x001b2b80 |
| 19 | 0x13 | -2 | UNKNOWN_19 | — | ? | 0x000f7080 |
| 20 | 0x14 | 3 | UNKNOWN_20 | — | ? | 0x001741b0 |
| 21 | 0x15 | 10 | LOC_ANIM_SPECIFIC | new in 948 (no 947-3 equivalent) | M | 0x00118ef0 |
| 22 | 0x16 | -2 | ProcessPlayerInfo | was op 27 (PLAYER_INFO) | H | 0x001a39d0 |
| 23 | 0x17 | -2 | PLAYER_OP | was op 109 (PLAYER_GROUP_FULL) | H | 0x001869f0 |
| 24 | 0x18 | -1 | UPDATE_ZONE_PARTIAL | new in 948 (no 947-3 equivalent) | M | 0x001254c0 |
| 25 | 0x19 | -2 | UNKNOWN_25 | — | ? | 0x001898a0 |
| 26 | 0x1a | -2 | UNKNOWN_26 | — | ? | 0x001a63e0 |
| 27 | 0x1b | 10 | UNKNOWN_27 | — | ? | 0x00187490 |
| 28 | 0x1c | 6 | UNKNOWN_28 | — | ? | 0x00119910 |
| 29 | 0x1d | -2 | CLANSETTINGS_FULL | was op 104 (CLANSETTINGS_FULL) | H | 0x001a8500 |
| 30 | 0x1e | 8 | IF_SET2DANGLE | new in 948 (no 947-3 equivalent) | M | 0x00193f10 |
| 31 | 0x1f | 6 | UNKNOWN_31 | — | ? | 0x00186f40 |
| 32 | 0x20 | 8 | IF_SETCOLOUR | new in 948 (no 947-3 equivalent) | M | 0x00185850 |
| 33 | 0x21 | -1 | UNKNOWN_33 | — | ? | 0x001a0f40 |
| 34 | 0x22 | 8 | SOUND_AREA_SYNTH | new in 948 (no 947-3 equivalent) | M | 0x00176600 |
| 35 | 0x23 | 12 | IF_SETEVENTS2 | same op 35 (IF_SETEVENTS2) | H | 0x00186040 |
| 36 | 0x24 | 10 | UNKNOWN_36 | — | ? | 0x00186c60 |
| 37 | 0x25 | -1 | MESSAGE_FRIENDCHANNEL | was op 7 (MESSAGE_FRIENDCHANNEL) | H | 0x0019f5d0 |
| 38 | 0x26 | 8 | IF_SET_MODEL_FRAME | new in 948 (no 947-3 equivalent) | M | 0x00185b70 |
| 39 | 0x27 | 6 | IF_OPENTOP | was op 68 (IF_OPENTOP) | H | 0x00194050 |
| 40 | 0x28 | 8 | IF_SUBSWAP | new in 948 (no 947-3 equivalent) | M | 0x00186100 |
| 41 | 0x29 | 3 | UPDATE_ZONE_PARTIAL_FOLLOWS | was op 57 (UPDATE_ZONE_PARTIAL_FOLLOWS) | H | 0x000ef150 |
| 42 | 0x2a | 10 | UNKNOWN_42 | — | ? | 0x001875f0 |
| 43 | 0x2b | 12 | UNKNOWN_43 | — | ? | 0x00157080 |
| 44 | 0x2c | 6 | UpdateStat | new in 948 (no 947-3 equivalent) | M | 0x001b1830 |
| 45 | 0x2d | 1 | SET_MULTIWAY_STATE | was op 77 (SET_MULTIWAY_STATE) | H | 0x00173ad0 |
| 46 | 0x2e | 5 | OBJ_ADD | new in 948 (no 947-3 equivalent) | M | 0x00119240 |
| 47 | 0x2f | 3 | UNKNOWN_47 | — | ? | 0x001196f0 |
| 48 | 0x30 | 3 | UNKNOWN_48 | — | ? | 0x00119560 |
| 49 | 0x31 | -2 | CHANGE_LOBBY | was op 30 (CHANGE_LOBBY) | H | 0x00195fb0 |
| 50 | 0x32 | -1 | LOC_CUSTOMISE | new in 948 (no 947-3 equivalent) | M | 0x0013f5f0 |
| 51 | 0x33 | 6 | UNKNOWN_51 | — | ? | 0x001197b0 |
| 52 | 0x34 | -2 | ProcessNpcInfo | was op 12 (NPC_INFO) | H | 0x001737d0 |
| 53 | 0x35 | -2 | UNKNOWN_53 | — | ? | 0xUNBOUND |
| 54 | 0x36 | -1 | UNKNOWN_54 | — | ? | 0x000ef260 |
| 55 | 0x37 | 0 | DESTROY_ZONE_DATA | new in 948 (no 947-3 equivalent) | M | 0x000ef4e0 |
| 56 | 0x38 | -1 | MESSAGE_QUICKCHAT_PRIVATE | was op 73 (MESSAGE_QUICKCHAT_PRIVATE) | H | 0x001a08e0 |
| 57 | 0x39 | 6 | UNKNOWN_57 | — | ? | 0x00186e60 |
| 58 | 0x3a | 0 | RESET_CLIENT_STATE | new in 948 (no 947-3 equivalent) | M | 0x000f4080 |
| 59 | 0x3b | 10 | IF_SETNPCMODEL | new in 948 (no 947-3 equivalent) | M | 0x00193c50 |
| 60 | 0x3c | 25 | IF_SETPLAYERMODEL_SELF | new in 948 (no 947-3 equivalent) | M | 0x00186520 |
| 61 | 0x3d | 3 | UNKNOWN_61 | — | ? | 0x001199b0 |
| 62 | 0x3e | 4 | IF_CLOSESUB_ACTIVE | was op 33 (IF_CLOSESUB) | H | 0x00186360 |
| 63 | 0x3f | 10 | UNKNOWN_63 | — | ? | 0x0014ebc0 |
| 64 | 0x40 | 6 | UNKNOWN_64 | — | ? | 0x00119600 |
| 65 | 0x41 | 20 | MAP_PROJANIM | was op 47 (MAP_PROJANIM) | H | 0x000f2c30 |
| 66 | 0x42 | 5 | UNKNOWN_66 | — | ? | 0x00187160 |
| 67 | 0x43 | -2 | CLANCHANNEL_FULL | was op 28 (CLANCHANNEL_FULL) | H | 0x00198f70 |
| 68 | 0x44 | 10 | IF_SETMODELORIGIN | new in 948 (no 947-3 equivalent) | M | 0x00193420 |
| 69 | 0x45 | 6 | UNKNOWN_69 | — | ? | 0x001194b0 |
| 70 | 0x46 | 25 | IF_SETPLAYERMODEL_OTHER | new in 948 (no 947-3 equivalent) | M | 0x00186710 |
| 71 | 0x47 | 7 | OBJ_REVEAL | new in 948 (no 947-3 equivalent) | M | 0x000f37b0 |
| 72 | 0x48 | 1 | CAM_FORCEANGLE | was op 3 (CAM_FORCEANGLE) | H | 0x00186de0 |
| 73 | 0x49 | 2 | UNKNOWN_73 | — | ? | 0x00175810 |
| 74 | 0x4a | 4 | SET_DISPLAY_INT | was op 59 (JCOINS_UPDATE) | H | 0x00121910 |
| 75 | 0x4b | 0 | SET_READY_FLAG | was op 65 (SET_READY_FLAG) | H | 0x00174fa0 |
| 76 | 0x4c | -2 | UPDATE_ZONE_PARTIAL_ENCLOSED | was op 126 (UPDATE_ZONE_PARTIAL_ENCLOSED) | H | 0x000eefc0 |
| 77 | 0x4d | -2 | CAM_UPDATE | was op 46 (CAMERA_UPDATE) | H | 0x001d3b50 |
| 78 | 0x4e | 3 | UPDATE_ZONE_FULL_FOLLOWS | was op 18 (UPDATE_ZONE_FULL_FOLLOWS) | H | 0x000f9510 |
| 79 | 0x4f | -1 | NPC_HEADICON_SPECIFIC | was op 54 (NPC_HEADICON_SPECIFIC) | H | 0x00185c30 |
| 80 | 0x50 | 1 | SET_RUN_ENERGY | was op 19 (UPDATE_RUNENERGY) | H | 0x00174f40 |
| 81 | 0x51 | -2 | REBUILD_NORMAL_SIMPLE | was op 90 (REBUILD_NORMAL) | H | 0x001da8b0 |
| 82 | 0x52 | 23 | IF_SETPOSITION | was op 8 (IF_SETPOSITION) | H | 0x00189180 |
| 83 | 0x53 | -2 | UNKNOWN_83 | — | ? | 0x001def40 |
| 84 | 0x54 | 10 | IF_SETOBJECT | new in 948 (no 947-3 equivalent) | M | 0x00185a00 |
| 85 | 0x55 | -2 | UPDATE_INV_FULL_impl | was op 69 (UPDATE_INV_FULL) | H | 0x001a8b70 |
| 86 | 0x56 | 10 | IF_SETANIM | new in 948 (no 947-3 equivalent) | M | 0x00185740 |
| 87 | 0x57 | 0 | CAM_RESET | was op 89 (CAM_RESET) | H | 0x00186e20 |
| 88 | 0x58 | 4 | UNKNOWN_88 | — | ? | 0x001dad40 |
| 89 | 0x59 | 19 | UNKNOWN_89 | — | ? | 0x000f1f70 |
| 90 | 0x5a | -1 | LOC_ADD | new in 948 (no 947-3 equivalent) | M | 0x00139080 |
| 91 | 0x5b | 5 | IF_SETHIDE | was op 103 (IF_SETHIDE) | H | 0x00193fc0 |
| 92 | 0x5c | -1 | SET_VARC_STR_SMALL | new in 948 (no 947-3 equivalent) | M | 0x001501c0 |
| 93 | 0x5d | -1 | MESSAGE_GAME | was op 105 (MESSAGE_GAME) | H | 0x00198220 |
| 94 | 0x5e | 8 | IF_OPENSUB | was op 17 (IF_OPENSUB) | H | 0x00194100 |
| 95 | 0x5f | 5 | MIDI_SONG | was op 87 (MIDI_SONG) | H | 0x001871b0 |
| 96 | 0x60 | 4 | IF_SETANIM_ACTIVE | new in 948 (no 947-3 equivalent) | M | 0x00185800 |
| 97 | 0x61 | 10 | IF_SETEVENTS | was op 34 (IF_SETEVENTS) | H | 0x00185f60 |
| 98 | 0x62 | 25 | UNKNOWN_98 | — | ? | 0x000f25d0 |
| 99 | 0x63 | 6 | IF_SETRECOL | new in 948 (no 947-3 equivalent) | M | 0x00193860 |
| 100 | 0x64 | 4 | UNKNOWN_100 | — | ? | 0x00187020 |
| 101 | 0x65 | 4 | IF_SETOBJECT_ACTIVE | new in 948 (no 947-3 equivalent) | M | 0x00185b20 |
| 102 | 0x66 | 8 | IF_SETMODEL | new in 948 (no 947-3 equivalent) | M | 0x001858e0 |
| 103 | 0x67 | 8 | IF_SETGRAPHIC | new in 948 (no 947-3 equivalent) | M | 0x00193e60 |
| 104 | 0x68 | 14 | PLAYER_INFO_DECODE | was op 78 (PLAYER_INFO_DECODE) | H | 0x00183d50 |
| 105 | 0x69 | -1 | MESSAGE_CLANCHANNEL | new in 948 (no 947-3 equivalent) | M | 0x001a1cc0 |
| 106 | 0x6a | 1 | SET_NPC_UPDATE_ORIGIN | was op 61 (NPC_UPDATE_ORIGIN) | H | 0x001b2c20 |
| 107 | 0x6b | 3 | OBJ_DEL | new in 948 (no 947-3 equivalent) | M | 0x00152b00 |
| 108 | 0x6c | -2 | CLANSETTINGS_DELTA | was op 63 (CLANSETTINGS_DELTA) | H | 0x001aed60 |
| 109 | 0x6d | 28 | HANDSHAKE_UID | was op 36 (HANDSHAKE_UID) | H | 0x001336e0 |
| 110 | 0x6e | -2 | UNKNOWN_110 | — | ? | 0x001d2060 |
| 111 | 0x6f | 6 | UNKNOWN_111 | — | ? | 0x00175960 |
| 112 | 0x70 | 2 | SET_TICK_TIMER | was op 52 (SET_TICK_TIMER) | H | 0x001752d0 |
| 113 | 0x71 | 11 | MAP_ANIM | new in 948 (no 947-3 equivalent) | M | 0x00157a50 |
| 114 | 0x72 | -1 | CHAT_FILTER_SETTINGS | was op 39 (CHAT_FILTER_SETTINGS) | H | 0x001a1b40 |
| 115 | 0x73 | 10 | IF_SETNPCHEAD | new in 948 (no 947-3 equivalent) | M | 0x00185660 |
| 116 | 0x74 | -2 | UNKNOWN_116 | — | ? | 0x001500a0 |
| 117 | 0x75 | -1 | CLANCHANNEL_DELTA | was op 83 (CLANCHANNEL_DELTA) | H | 0x001a3870 |
| 118 | 0x76 | 29 | IF_SETPLAYERMODEL_SNAPSHOT | new in 948 (no 947-3 equivalent) | M | 0x001da230 |
| 119 | 0x77 | 35 | CUTSCENE_DATA | was op 91 (CUTSCENE_DATA) | H | 0x00173e80 |
| 120 | 0x78 | 0 | CAM_SMOOTHRESET | was op 80 (CAM_SMOOTHRESET) | H | 0x00186d90 |
| 121 | 0x79 | -2 | UPDATE_INV_PARTIAL | was op 5 (UPDATE_INV_PARTIAL) | H | 0x001841a0 |
| 122 | 0x7a | -2 | IF_SETTEXT | new in 948 (no 947-3 equivalent) | M | 0x00185ec0 |
| 123 | 0x7b | 0 | IF_TRIGGER_CLOSE | new in 948 (no 947-3 equivalent) | M | 0x00185620 |
| 124 | 0x7c | -2 | CLANSETTINGS_DELTA_CHAT | was op 11 (CLANSETTINGS_DELTA_CHAT) | H | 0x001af120 |
| 125 | 0x7d | 7 | OBJ_COUNT | new in 948 (no 947-3 equivalent) | M | 0x001190d0 |
| 126 | 0x7e | -1 | MESSAGE_FRIENDCHAT | was op 21 (MESSAGE_FRIENDCHAT) | H | 0x0019fc50 |
| 127 | 0x7f | 0 | UNKNOWN_127 | — | ? | 0x00182610 |
| 128 | 0x80 | 0 | UNKNOWN_128 | — | ? | 0x00173e40 |
| 129 | 0x81 | 3 | UNKNOWN_129 | — | ? | 0x001dc630 |
| 130 | 0x82 | -1 | UPDATE_IGNORELIST_thunk | was op 211 (UPDATE_IGNORELIST) | H | 0x001d3b40 |
| 131 | 0x83 | 3 | UNKNOWN_131 | — | ? | 0x000f9600 |
| 132 | 0x84 | 2 | SOUND_STOP | new in 948 (no 947-3 equivalent) | M | 0x0017e4c0 |
| 133 | 0x85 | 1 | SET_NPC_UPDATE_FLAG | was op 138 (NPC_UPDATE_FLAG) | H | 0x00173980 |
| 134 | 0x86 | -2 | UNKNOWN_134 | — | ? | 0x00179460 |
| 135 | 0x87 | -2 | UNKNOWN_135 | — | ? | 0x001758f0 |
| 136 | 0x88 | 5 | IF_SETANIM_SMALL | new in 948 (no 947-3 equivalent) | M | 0x00185aa0 |
| 137 | 0x89 | 1 | SET_CHAT_FILTER_A | was op 217 (SET_CHAT_FILTER_A) | H | 0x00173c20 |
| 138 | 0x8a | 2 | SKIP_2_BYTES | new in 948 (no 947-3 equivalent) | M | 0x00175f90 |
| 139 | 0x8b | 0 | LOGOUT_TRANSFER | was op 209 (LOGOUT_TRANSFER) | H | 0x001c3a40 |
| 140 | 0x8c | 17 | UNKNOWN_140 | — | ? | 0x000f2280 |
| 141 | 0x8d | -2 | IF_OPENSUB_thunk | was op 186 (IF_OPENSUB_THUNK) | H | 0x001d9df0 |
| 142 | 0x8e | -1 | SET_URL_STRING | was op 200 (SET_URL_STRING) | H | 0x0017fd10 |
| 143 | 0x8f | 6 | UNKNOWN_143 | — | ? | 0x00119e20 |
| 144 | 0x90 | 2 | UNKNOWN_144 | — | ? | 0x00119c30 |
| 145 | 0x91 | 2 | UNKNOWN_145 | — | ? | 0x00187890 |
| 146 | 0x92 | 2 | UNKNOWN_146 | — | ? | 0x000f98f0 |
| 147 | 0x93 | 10 | UNKNOWN_147 | — | ? | 0x00141510 |
| 148 | 0x94 | 2 | IF_CLOSESUB_BY_ID | new in 948 (no 947-3 equivalent) | M | 0x001855d0 |
| 149 | 0x95 | 6 | UNKNOWN_149 | — | ? | 0x000f9a20 |
| 150 | 0x96 | 3 | UNKNOWN_150 | — | ? | 0x001db620 |
| 151 | 0x97 | 21 | PROJANIM_SPECIFIC | was op 196 (PROJANIM_SPECIFIC) | H | 0x000f2900 |
| 152 | 0x98 | -1 | IF_SET_HTTP_IMAGE | was op 146 (IF_SET_HTTP_IMAGE) | H | 0x001ba9f0 |
| 153 | 0x99 | 4 | UNKNOWN_153 | — | ? | 0x001dc100 |
| 154 | 0x9a | 5 | UNKNOWN_154 | — | ? | 0x001de6b0 |
| 155 | 0x9b | 0 | LOGOUT | was op 147 (LOGOUT) | H | 0x001ba250 |
| 156 | 0x9c | 1 | SET_CHAT_FILTER_B | was op 155 (SET_CHAT_FILTER_B) | H | 0x00173d50 |
| 157 | 0x9d | 1 | UNKNOWN_157 | — | ? | 0x00173a50 |
| 158 | 0x9e | 9 | IF_SETSCROLLSIZE | new in 948 (no 947-3 equivalent) | M | 0x00193660 |
| 159 | 0x9f | -2 | UNKNOWN_159 | — | ? | 0x000ec3a0 |
| 160 | 0xa0 | 9 | UNKNOWN_160 | — | ? | 0x001d76a0 |
| 161 | 0xa1 | -2 | UPDATE_PLAYER_GROUP | was op 207 (PLAYER_GROUP_DELTA) | H | 0x001989c0 |
| 162 | 0xa2 | 0 | TRIGGER_ONDIALOGABORT | was op 195 (TRIGGER_ONDIALOGABORT) | H | 0x000f1f30 |
| 163 | 0xa3 | 15 | UNKNOWN_163 | — | ? | 0x001db050 |
| 164 | 0xa4 | 28 | MAP_PROJANIM_HALT | new in 948 (no 947-3 equivalent) | M | 0x000f1bc0 |
| 165 | 0xa5 | 14 | IF_SETMODEL_COORD | new in 948 (no 947-3 equivalent) | M | 0x001939d0 |
| 166 | 0xa6 | 5 | UNKNOWN_166 | — | ? | 0x00120150 |
| 167 | 0xa7 | 12 | SYNTH_SOUND | new in 948 (no 947-3 equivalent) | M | 0x00187350 |
| 168 | 0xa8 | -1 | SOUND_AREA | new in 948 (no 947-3 equivalent) | M | 0x00151c10 |
| 169 | 0xa9 | 8 | SERVER_TICK_END | was op 171 (SERVER_TICK_END) | H | 0x00181700 |
| 170 | 0xaa | 5 | LOC_MERGE | new in 948 (no 947-3 equivalent) | M | 0x000eeba0 |
| 171 | 0xab | 3 | UNKNOWN_171 | — | ? | 0x000ef800 |
| 172 | 0xac | 1 | UNKNOWN_172 | — | ? | 0x000f03f0 |
| 173 | 0xad | -2 | UNKNOWN_op173_handler | new in 948 (no 947-3 equivalent) | M | 0x001ae100 |
| 174 | 0xae | 8 | HandleAntiCheatChallenge | was op 198 (ANTI_CHEAT_CHALLENGE) | H | 0x00173b60 |
| 175 | 0xaf | -1 | MESSAGE_PRIVATE_ECHO | was op 129 (MESSAGE_PRIVATE_ECHO) | H | 0x0019f5e0 |
| 176 | 0xb0 | 3 | SET_INTERACTION_FLAG_D | was op 144 (SET_INTERACTION_FLAG_D) | H | 0x000f0460 |
| 177 | 0xb1 | 29 | PROJANIM_SPECIFIC_HALT | new in 948 (no 947-3 equivalent) | M | 0x000f1840 |
| 178 | 0xb2 | -2 | UPDATE_PLAYER_CHAT | same op 178 (UPDATE_PLAYER_CHAT) | H | 0x00158240 |
| 179 | 0xb3 | 9 | IF_SETSCROLLPOS | new in 948 (no 947-3 equivalent) | M | 0x00193760 |
| 180 | 0xb4 | 5 | IF_SETOBJECT_SMALL | new in 948 (no 947-3 equivalent) | M | 0x00185980 |
| 181 | 0xb5 | 4 | UNKNOWN_181 | — | ? | 0x00186b60 |
| 182 | 0xb6 | 3 | UNKNOWN_182 | — | ? | 0x001dbb80 |
| 183 | 0xb7 | 14 | MAP_ANIM_SPECIFIC | new in 948 (no 947-3 equivalent) | M | 0x001575c0 |
| 184 | 0xb8 | 4 | SET_SYSUPDATE_TIMER | was op 132 (UPDATE_REBOOT_TIMER) | H | 0x000eff40 |
| 185 | 0xb9 | -1 | MESSAGE_PRIVATE | was op 151 (MESSAGE_PRIVATE) | H | 0x001a02d0 |
| 186 | 0xba | -2 | REBUILD_WORLDENTITY | was op 188 (REBUILD_WORLDENTITY) | H | 0x000efd80 |
| 187 | 0xbb | 1 | UNKNOWN_187 | — | ? | 0x00173cd0 |
| 188 | 0xbc | -2 | UNKNOWN_188 | — | ? | 0x000f7090 |
| 189 | 0xbd | 4 | VORBIS_PRELOAD | new in 948 (no 947-3 equivalent) | M | 0x001870f0 |
| 190 | 0xbe | 0 | CLEAR_PENDING_UPDATES | new in 948 (no 947-3 equivalent) | M | 0x000f4110 |
| 191 | 0xbf | 4 | UNKNOWN_191 | — | ? | 0x00186be0 |
| 192 | 0xc0 | 1 | UNKNOWN_192 | — | ? | 0x00173de0 |
| 193 | 0xc1 | 1 | SET_INTERACTION_FLAG_C | was op 174 (SET_INTERACTION_FLAG_C) | H | 0x000effe0 |
| 194 | 0xc2 | 1 | UNKNOWN_194 | — | ? | 0x000efa80 |
| 195 | 0xc3 | 4 | SOUND_AREA_SYNTH_2 | new in 948 (no 947-3 equivalent) | M | 0x001870a0 |
| 196 | 0xc4 | 10 | UNKNOWN_196 | — | ? | 0x00119350 |
| 197 | 0xc5 | -2 | SKIP_DATA | was op 166 (SKIP_DATA) | H | 0x00173800 |
| 198 | 0xc6 | -1 | UPDATE_URL_STRING | was op 214 (UPDATE_URL_STRING) | H | 0x0017a3e0 |
| 199 | 0xc7 | -2 | REBUILD_NORMAL | was op 172 (REBUILD_REGION) | H | 0x00120260 |
| 200 | 0xc8 | 2 | SOUND_GROUP_STOP | new in 948 (no 947-3 equivalent) | M | 0x00176000 |
| 201 | 0xc9 | 3 | UNKNOWN_201 | — | ? | 0x000ef970 |
| 202 | 0xca | -2 | PLAYER_INFO_DECODE_2 | was op 180 (PLAYER_INFO_DECODE_2) | H | 0x001bf300 |
| 203 | 0xcb | 3 | UNKNOWN_203 | — | ? | 0x001dd3e0 |
| 204 | 0xcc | 1 | UNKNOWN_204 | — | ? | 0x000f0380 |
| 205 | 0xcd | 6 | SOUND_GROUP_SPEED | new in 948 (no 947-3 equivalent) | M | 0x00182690 |
| 206 | 0xce | 5 | IF_SETNPCHEAD_ACTIVE | new in 948 (no 947-3 equivalent) | M | 0x001935c0 |
| 207 | 0xcf | 3 | UNKNOWN_207 | — | ? | 0x00119b80 |
| 208 | 0xd0 | -2 | UPDATE_INV_GROUP | was op 177 (UPDATE_INV_GROUP) | H | 0x001a88a0 |
| 209 | 0xd1 | -2 | NPC_INFO_thunk_worldentity | was op 205 (NPC_INFO_THUNK) | H | 0x001d5330 |
| 210 | 0xd2 | 6 | VORBIS_SONG | new in 948 (no 947-3 equivalent) | M | 0x00187750 |
| 211 | 0xd3 | 33 | UNKNOWN_211 | — | ? | 0x000f1420 |
| 212 | 0xd4 | -1 | SET_WORLD_TARGET | was op 187 (SET_WORLD_TARGET) | H | 0x0017faf0 |
| 213 | 0xd5 | -1 | SWITCH_WORLD | was op 179 (SWITCH_WORLD) | H | 0x001ae9e0 |
| 214 | 0xd6 | 0 | UNKNOWN_214 | — | ? | 0x00182650 |
| 215 | 0xd7 | 8 | UNKNOWN_215 | — | ? | 0x001deaa0 |
| 216 | 0xd8 | -2 | UNKNOWN_216 | — | ? | 0x0018fea0 |
| 217 | 0xd9 | 4 | SOUND_MODIFY | new in 948 (no 947-3 equivalent) | M | 0x00175c90 |

---

## ServerProt Zone Sub-Prot Table (18 entries, sub-opcodes 0-17)

These are inline within UPDATE_ZONE_PARTIAL_ENCLOSED (op 76) — the outer packet contains a 3-byte zone header followed by a stream of sub-opcodes from this table. Sub-op > 17 → packet drops/disconnect.

| Sub-op | Size | Handler Name | Handler Addr |
|---:|----:|---|---|
| 0  | -1 | jag::packethandlers::ZoneUpdates::LOC_ADD          | 0x00139080 |
| 1  | 29 | jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC_HALT | 0x000f1840 |
| 2  |  2 | jag::packethandlers::ZoneUpdates::LOC_DEL          | 0x001382d0 |
| 3  |  7 | jag::packethandlers::ZoneUpdates::OBJ_REVEAL       | 0x000f37b0 |
| 4  | -1 | jag::packethandlers::ZoneUpdates::LOC_MERGE        | 0x000eeba0 |
| 5  |  3 | jag::packethandlers::ZoneUpdates::OBJ_DEL          | 0x00152b00 |
| 6  | 14 | jag::packethandlers::ZoneUpdates::MAP_ANIM_SPECIFIC | 0x001575c0 |
| 7  | 20 | jag::packethandlers::ZoneUpdates::MAP_PROJANIM     | 0x000f2c30 |
| 8  |  7 | jag::packethandlers::ZoneUpdates::OBJ_COUNT        | 0x001190d0 |
| 9  |  5 | jag::packethandlers::ZoneUpdates::OBJ_ADD          | 0x00119240 |
| 10 | -1 | jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE    | 0x0013f5f0 |
| 11 |  7 | jag::packethandlers::ZoneUpdates::LOC_PREFETCH     | 0x00138110 |
| 12 |  5 | jag::packethandlers::ZoneUpdates::LOC_MERGE        | 0x000eeba0 |
| 13 | 11 | jag::packethandlers::ZoneUpdates::LOC_ANIM         | 0x00118ce0 |
| 14 | 11 | jag::packethandlers::ZoneUpdates::MAP_ANIM         | 0x00157a50 |
| 15 | 21 | jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC | 0x000f2900 |
| 16 | 10 | jag::packethandlers::ZoneUpdates::LOC_ANIM_SPECIFIC | 0x00118ef0 |
| 17 | 28 | jag::packethandlers::ZoneUpdates::MAP_PROJANIM_HALT | 0x000f1bc0 |

Sub-op 4 and 12 share handler `LOC_MERGE` — second is a thin wrapper (5-byte fixed instead of -1 varByte; likely a different protocol variant).

---

## ClientProt Table (130 entries, opcodes 0–129)

Loaded from project-undercut CSV (`clientprot_948-2_phase4a.csv`). Confidence is **LOWER** than ServerProt because:
1. ClientProt emitters are scattered across the codebase, not centralized in a BindHandlers loop
2. The CSV's `emitter_948_2_addr` column had 27 UNBOUND entries (sender function not yet identified)
3. I have not done full behavioral verification for ClientProt opcodes in this Phase 1 walk

**Per Cardinal Rule 1**, this table should be treated as a starting point — each ClientProt opcode used in implementation code (IfButtonHandler etc.) MUST be re-verified by decompiling the emitter and checking the wire format against a live capture before being trusted.

Size codes: positive int = fixed byte length; `-1` = varByte; `-2` = varShort. The 0xffffffff in the RegisterAll decompile = `-1`; 0xfffffffe = `-2`.

| Op | Hex | Size | Emitter Name (948) | Emitter Addr |
|---:|----|-----:|---|---|
| 0 | 0x00 | -1 | FUN_00280630 | 0x00280630 |
| 1 | 0x01 | -1 | SendOpNpcCS2 | 0x00311830 |
| 2 | 0x02 | 9 | FUN_0015c7b0 | 0x0015c7b0 |
| 3 | 0x03 | 9 | HandleAntiCheatChallenge | 0x001807a0 |
| 4 | 0x04 | -1 | SendOpPlayerCS2 | 0x0033df80 |
| 5 | 0x05 | 4 | SendSceneGraphReport | 0x00229d80 |
| 6 | 0x06 | 7 | FUN_0026b900 | 0x0026b900 |
| 7 | 0x07 | 4 | UNKNOWN_7 | 0xUNBOUND |
| 8 | 0x08 | 4 | SendMultiDisplayPackets | 0x001a2d90 |
| 9 | 0x09 | 12 | FUN_0015b5c0 | 0x0015b5c0 |
| 10 | 0x0a | 16 | FUN_002b7460 | 0x002b7460 |
| 11 | 0x0b | 3 | FUN_0015c3d0 | 0x0015c3d0 |
| 12 | 0x0c | -1 | SendMultiDisplayPackets | 0x001a2d90 |
| 13 | 0x0d | 8 | UNKNOWN_13 | 0xUNBOUND |
| 14 | 0x0e | 0 | SendNoTimeout | 0x002cffc0 |
| 15 | 0x0f | 6 | SendEventMouseClick | 0x001805b0 |
| 16 | 0x10 | 6 | FUN_00295700 | 0x00295700 |
| 17 | 0x11 | 5 | FUN_002a3a70 | 0x002a3a70 |
| 18 | 0x12 | 17 | SendOpLocTLong | 0x0015c470 |
| 19 | 0x13 | 9 | UNKNOWN_19 | 0xUNBOUND |
| 20 | 0x14 | -1 | SendOpObjCS2_2 | 0x002a24c0 |
| 21 | 0x15 | 8 | UNKNOWN_21 | 0xUNBOUND |
| 22 | 0x16 | 3 | FUN_002d9970 | 0x002d9970 |
| 23 | 0x17 | 8 | UNKNOWN_23 | 0xUNBOUND |
| 24 | 0x18 | 7 | FUN_0026b900 | 0x0026b900 |
| 25 | 0x19 | 11 | FUN_00aeeb60 | 0x00aeeb60 |
| 26 | 0x1a | -2 | SendResumeCountDialog | 0x002cfdb0 |
| 27 | 0x1b | 1 | UNKNOWN_27 | 0xUNBOUND |
| 28 | 0x1c | 4 | DoOpLoc | 0x00136900 |
| 29 | 0x1d | 4 | FUN_001fc8c0 | 0x001fc8c0 |
| 30 | 0x1e | 8 | UNKNOWN_30 | 0xUNBOUND |
| 31 | 0x1f | 3 | FUN_0015c450 | 0x0015c450 |
| 32 | 0x20 | 3 | FUN_0015c430 | 0x0015c430 |
| 33 | 0x21 | 3 | FUN_002d9970 | 0x002d9970 |
| 34 | 0x22 | 15 | UNKNOWN_34 | 0xUNBOUND |
| 35 | 0x23 | -1 | IfButtonXInner | 0x00297eb0 |
| 36 | 0x24 | 7 | FUN_0026b900 | 0x0026b900 |
| 37 | 0x25 | 0 | SendQueuedPacket | 0x002d3870 |
| 38 | 0x26 | -2 | SendMessagePrivate | 0x00306dd0 |
| 39 | 0x27 | 3 | FUN_002d9970 | 0x002d9970 |
| 40 | 0x28 | 3 | FUN_002d9970 | 0x002d9970 |
| 41 | 0x29 | 9 | FUN_0015c7f0 | 0x0015c7f0 |
| 42 | 0x2a | -1 | FUN_002d04b0 | 0x002d04b0 |
| 43 | 0x2b | 8 | UNKNOWN_43 | 0xUNBOUND |
| 44 | 0x2c | 4 | SendStrtol | 0x002d4d20 |
| 45 | 0x2d | 8 | UNKNOWN_45 | 0xUNBOUND |
| 46 | 0x2e | 1 | UNKNOWN_46 | 0xUNBOUND |
| 47 | 0x2f | 3 | FUN_002d9970 | 0x002d9970 |
| 48 | 0x30 | -1 | FUN_00172900 | 0x00172900 |
| 49 | 0x31 | -1 | SendOpObjCS2 | 0x0033ddc0 |
| 50 | 0x32 | 3 | FUN_002d9970 | 0x002d9970 |
| 51 | 0x33 | 0 | ProcessConnections | 0x0013e6c0 |
| 52 | 0x34 | 6 | SendDisplayInfo | 0x001a2ba0 |
| 53 | 0x35 | 15 | FUN_0015bcf0 | 0x0015bcf0 |
| 54 | 0x36 | 4 | SendWorldlistFetch | 0x00194a80 |
| 55 | 0x37 | 11 | FUN_0015bad0 | 0x0015bad0 |
| 56 | 0x38 | 3 | FUN_002d9970 | 0x002d9970 |
| 57 | 0x39 | 2 | SendAffinedTransformSet_Main | 0x0033e1f0 |
| 58 | 0x3a | -2 | SendMoveGame | 0x0026e1d0 |
| 59 | 0x3b | 4 | SendDetectModifiedClient | 0x00480620 |
| 60 | 0x3c | -1 | FUN_002d02c0 | 0x002d02c0 |
| 61 | 0x3d | 16 | FUN_0014edf0 | 0x0014edf0 |
| 62 | 0x3e | 1 | UNKNOWN_62 | 0xUNBOUND |
| 63 | 0x3f | 3 | FUN_0015c3f0 | 0x0015c3f0 |
| 64 | 0x40 | 7 | FUN_0026b900 | 0x0026b900 |
| 65 | 0x41 | 4 | FUN_002383e0 | 0x002383e0 |
| 66 | 0x42 | 0 | SendCloseModal | 0x002d01f0 |
| 67 | 0x43 | 18 | UNKNOWN_67 | 0xUNBOUND |
| 68 | 0x44 | 8 | UNKNOWN_68 | 0xUNBOUND |
| 69 | 0x45 | 2 | FUN_002d03d0 | 0x002d03d0 |
| 70 | 0x46 | -1 | SendFriendlistDel | 0x0026b1f0 |
| 71 | 0x47 | -1 | UNKNOWN_71 | 0xUNBOUND |
| 72 | 0x48 | -2 | SendResumeNameDialog | 0x002cff20 |
| 73 | 0x49 | 3 | UNKNOWN_73 | 0xUNBOUND |
| 74 | 0x4a | 5 | FUN_002d9970 | 0x002d9970 |
| 75 | 0x4b | -1 | FUN_0015b7d0 | 0x0015b7d0 |
| 76 | 0x4c | 4 | SendEncodedString2 | 0x0028ea60 |
| 77 | 0x4d | 9 | SendEventAppletFocus | 0x00166be0 |
| 78 | 0x4e | 18 | UNKNOWN_78 | 0xUNBOUND |
| 79 | 0x4f | 1 | FUN_0015b7d0 | 0x0015b7d0 |
| 80 | 0x50 | -1 | UNKNOWN_80 | 0xUNBOUND |
| 81 | 0x51 | -2 | SendIgnorelistAdd | 0x0030f4b0 |
| 82 | 0x52 | -2 | SendMultiDisplayPackets | 0x001a2d90 |
| 83 | 0x53 | -1 | UNKNOWN_83 | 0xUNBOUND |
| 84 | 0x54 | 22 | UNKNOWN_84 | 0xUNBOUND |
| 85 | 0x55 | -1 | SendIfButtonTargetMenu | 0x0029aae0 |
| 86 | 0x56 | 11 | activechatphrase_sendprivate | 0x00340850 |
| 87 | 0x57 | -1 | FUN_0015c100 | 0x0015c100 |
| 88 | 0x58 | -2 | SendMessagePublicWithEffects | 0x0037c1c0 |
| 89 | 0x59 | -1 | SendDataReport | 0x00341230 |
| 90 | 0x5a | -1 | SendSocialRequest | 0x0026aef0 |
| 91 | 0x5b | 3 | FUN_002d9970 | 0x002d9970 |
| 92 | 0x5c | 8 | UNKNOWN_92 | 0xUNBOUND |
| 93 | 0x5d | -2 | SendVerifiedStringSend | 0x00344600 |
| 94 | 0x5e | 3 | SendWindowStatus | 0x0033e470 |
| 95 | 0x5f | 4 | UNKNOWN_95 | 0xUNBOUND |
| 96 | 0x60 | 0 | UNKNOWN_96 | 0xUNBOUND |
| 97 | 0x61 | -1 | FUN_002d06a0 | 0x002d06a0 |
| 98 | 0x62 | -1 | FUN_001729b0 | 0x001729b0 |
| 99 | 0x63 | -1 | FUN_002d0850 | 0x002d0850 |
| 100 | 0x64 | -1 | SendFriendlistAdd | 0x0026a530 |
| 101 | 0x65 | 7 | FUN_0026b900 | 0x0026b900 |
| 102 | 0x66 | -2 | FUN_00298c30 | 0x00298c30 |
| 103 | 0x67 | 8 | UNKNOWN_103 | 0xUNBOUND |
| 104 | 0x68 | 2 | UNKNOWN_104 | 0xUNBOUND |
| 105 | 0x69 | -2 | SendAppletFocusEvents | 0x00194f40 |
| 106 | 0x6a | 1 | SendMultiDisplayPackets | 0x001a2d90 |
| 107 | 0x6b | 0 | SendMapBuildComplete | 0x002bc030 |
| 108 | 0x6c | 3 | FUN_002d9970 | 0x002d9970 |
| 109 | 0x6d | 9 | FUN_0015c810 | 0x0015c810 |
| 110 | 0x6e | 7 | FUN_001803b0 | 0x001803b0 |
| 111 | 0x6f | 9 | FUN_0015c830 | 0x0015c830 |
| 112 | 0x70 | 7 | FUN_0026b900 | 0x0026b900 |
| 113 | 0x71 | 3 | FUN_0015c410 | 0x0015c410 |
| 114 | 0x72 | -1 | FUN_00269680 | 0x00269680 |
| 115 | 0x73 | 3 | FUN_00269580 | 0x00269580 |
| 116 | 0x74 | 2 | FUN_002d05c0 | 0x002d05c0 |
| 117 | 0x75 | -1 | SendEncodedString | 0x00311130 |
| 118 | 0x76 | -1 | activechatphrase_send | 0x00340d50 |
| 119 | 0x77 | -2 | SendResumePauseButton | 0x002cfc50 |
| 120 | 0x78 | 8 | SendStrtoll | 0x002d74e0 |
| 121 | 0x79 | 3 | FUN_0015c3b0 | 0x0015c3b0 |
| 122 | 0x7a | -2 | FUN_002d1b10 | 0x002d1b10 |
| 123 | 0x7b | 1 | SendBugReport | 0x002d0140 |
| 124 | 0x7c | -1 | SendMessagePublic | 0x0037a9c0 |
| 125 | 0x7d | 9 | FUN_0015c850 | 0x0015c850 |
| 126 | 0x7e | 9 | FUN_0015c7d0 | 0x0015c7d0 |
| 127 | 0x7f | 8 | UNKNOWN_127 | 0xUNBOUND |
| 128 | 0x80 | -2 | UNKNOWN_128 | 0xUNBOUND |
| 129 | 0x81 | -1 | SendOpLocCS2 | 0x0033e140 |

---

## §6. Project-undercut CSV deviations

During this walk I found the following cases where the project-undercut CSV's handler name was either misleading or wrong:

1. **Op 0x16 (22) PLAYER_INFO** — CSV `handler_948_2_addr` = `0x001a39d0` is the `eastl::function` glue/wrapper. The actual handler is `jag::PlayerList::ProcessPlayerInfo @ 0x00161720`, bound via `jag::packethandlers::PlayerList::BindHandlers @ 0x000ab3ea` (newly named in this session). The CSV's `handler_name` field is correct (`jag::PlayerList::ProcessPlayerInfo`) but its `handler_948_2_addr` field points at the wrong physical address.
2. **Op 0x34 (52) NPC_INFO** — CSV `handler_948_2_addr` = `0x001737d0` is a 5-line thunk (`if (param_3 == 1) *param_1 = param_2; else if (param_3 == 2) *param_1 = *param_2; return 0;`). The actual handler is `jag::NPCList::ProcessNpcInfo @ 0x001d79a0`, bound via the main `BindHandlers @ 0x0007509a`. Same pattern as PLAYER_INFO.
3. **Op 0xc7 (199) REBUILD_NORMAL** — CSV name is correct, but note this is the 947-3 equivalent of REBUILD_REGION (op 172 in 947-3), NOT the world-login rebuild. The world-login rebuild is REBUILD_NORMAL_SIMPLE at op 0x51 (81).
4. **Op 0xae (174) `HandleAntiCheatChallenge`** — CSV name matches the function; this is the 947-3 op 198 ANTI_CHEAT_CHALLENGE (8B fixed payload, the server emits every ~6s and expects a 9B reply).

In general the CSV's `handler_name` field appears to read Ghidra's symbol-resolved name, which sometimes resolves through one or two layers of thunks/glue. The `handler_948_2_addr` is sometimes the glue address rather than the real handler body. **All handler addresses I retained in the table above are taken from the CSV as-is and should be re-verified by `decompile_function_by_address` before any production-critical use.**
