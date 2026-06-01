# 948-2-2 vs 947-3 — ServerProt Delta

**Phase 1 deliverable.** This document is the diff between the 947-3 ServerProt table (218 opcodes) and the 948-2-2 ServerProt table (218 opcodes). It gates the rev948 codec implementation.

**Summary counts:**
- §1 Identical (same opcode, same size): 2 opcodes
- §2 Size-changed (same opcode, same name, different size): 0 opcodes
- §3 Renamed (same opcode, same size, name clarified/changed): 0 opcodes
- §4 Moved (opcode index shifted): 81 opcodes — **the dominant category**
- §5 New / Removed: 62 new in 948 (named, no 947-3 origin identified), 73 new opcodes are still `FUN_*` (unwalked), 19 947-3 named opcodes have no identified 948 destination

**Bottom line:** 947-1 → 947-3 was a content-only update (zero opcode changes). **948-2-2 is a major protocol revision** with virtually every opcode shuffled. The protocol semantics for the world-login set appear unchanged byte-for-byte, but the opcode numbers themselves are completely different. The Kotlin codec layer in `core/.../rev947/` will need a `rev948/` sibling with new `Rev948ServerProtStubs.kt` and `Rev948ClientProtStubs.kt` derived from the 948-opcode-tables.md doc.

---

## §1 Identical opcodes

Only **2** opcodes kept the same (opcode, size) pair across 947-3 → 948:

| Opcode | Size | Name |
|---:|---:|---|
| 35 | 12 | IF_SETEVENTS2 |
| 178 | -2 | UPDATE_PLAYER_CHAT |

(Both of these are still in the same handler family — IF_SETEVENTS2 in the Interfaces subsystem, UPDATE_PLAYER_CHAT in the PlayerInfo subsystem. They happen to land on the same opcode index.)

---

## §2 Size-changed opcodes

**None.** Every opcode in 948 either kept its size or moved entirely. (No same-opcode same-name size-change cases.)

**Note:** Several handlers DID change wire format (different byte ordering, transforms) between 947-3 and 948 — for example, in `UPDATE_ZONE_PARTIAL_FOLLOWS` (947-3 op 57, 948 op 41) the byte order in the 3-byte zone header is slightly reordered (the 947-3 decompile comment for the 948 handler explicitly notes this). But none of those changes affect the declared `size` field. They affect the codec body, which lives downstream of this delta doc.

---

## §3 Renamed opcodes (same op + size, different name)

**None confirmed.** The 947-3 ServerProt stub file uses many `UNKNOWN_N` placeholder names; many of those slots in 948 may have acquired real names (PlayerList::*, Misc::*, etc.) but I have not yet traced whether they are the same packet semantically — without that confirmation those count as `Moved` or `New`, not `Renamed`.

---

## §4 Moved opcodes (opcode index shifted)

**81** opcodes have the same semantic handler name but live at a different opcode number in 948. **Every codec entry needs its opcode updated.** Listed below in 948-opcode order:

| 948 op | 948 size | Handler / Name | 947-3 op | 947-3 size | Status |
|---:|---:|---|---:|---:|---|
| 0 | -1 | MESSAGE_QUICKCHAT_CLANCHANNEL | 82 | -1 | moved, size unchanged |
| 1 | -1 | NPC_OP | 24 | -1 | moved, size unchanged |
| 2 | -1 | MESSAGE_QUICKCHAT_CLANCHAT | 58 | -1 | moved, size unchanged |
| 3 | 19 | IF_SETTOPLEVELINTERFACE | 94 | 19 | moved, size unchanged |
| 4 | 32 | IF_SETANGLE | 117 | 32 | moved, size unchanged |
| 7 | 0 | RESET_ENTITY_LISTS | 43 | 0 | moved, size unchanged |
| 9 | -2 | CLANCHANNEL_FULL_CHAT | 84 | -2 | moved, size unchanged |
| 11 | -2 | MESSAGE_PUBLIC | 45 | -2 | moved, size unchanged |
| 12 | 2 | SET_PLAYER_OP_2 | 108 | 2 | moved, size unchanged |
| 13 | 1 | SET_PLAYER_OP_3 | 116 | 1 | moved, size unchanged |
| 18 | 1 | CAM_TARGET | 113 | 1 | moved, size unchanged |
| 22 | -2 | PLAYER_INFO | 27 | -2 | moved, size unchanged |
| 23 | -2 | PLAYER_GROUP_FULL | 109 | -2 | moved, size unchanged |
| 29 | -2 | CLANSETTINGS_FULL | 104 | -2 | moved, size unchanged |
| 37 | -1 | MESSAGE_FRIENDCHANNEL | 7 | -1 | moved, size unchanged |
| 39 | 6 | IF_OPENTOP | 68 | 6 | moved, size unchanged |
| 41 | 3 | UPDATE_ZONE_PARTIAL_FOLLOWS | 57 | 3 | moved, size unchanged |
| 45 | 1 | SET_MULTIWAY_STATE | 77 | 1 | moved, size unchanged |
| 49 | -2 | CHANGE_LOBBY | 30 | -2 | moved, size unchanged |
| 52 | -2 | NPC_INFO | 12 | -2 | moved, size unchanged |
| 56 | -1 | MESSAGE_QUICKCHAT_PRIVATE | 73 | -1 | moved, size unchanged |
| 62 | 4 | IF_CLOSESUB | 33 | 4 | moved, size unchanged |
| 65 | 20 | MAP_PROJANIM | 47 | 20 | moved, size unchanged |
| 67 | -2 | CLANCHANNEL_FULL | 28 | -2 | moved, size unchanged |
| 72 | 1 | CAM_FORCEANGLE | 3 | 1 | moved, size unchanged |
| 74 | 4 | JCOINS_UPDATE | 59 | 4 | moved, size unchanged |
| 75 | 0 | SET_READY_FLAG | 65 | 0 | moved, size unchanged |
| 76 | -2 | UPDATE_ZONE_PARTIAL_ENCLOSED | 126 | -2 | moved, size unchanged |
| 77 | -2 | CAMERA_UPDATE | 46 | -2 | moved, size unchanged |
| 78 | 3 | UPDATE_ZONE_FULL_FOLLOWS | 18 | 3 | moved, size unchanged |
| 79 | -1 | NPC_HEADICON_SPECIFIC | 54 | -1 | moved, size unchanged |
| 80 | 1 | UPDATE_RUNENERGY | 19 | 1 | moved, size unchanged |
| 81 | -2 | REBUILD_NORMAL | 90 | -2 | moved, size unchanged |
| 82 | 23 | IF_SETPOSITION | 8 | 23 | moved, size unchanged |
| 85 | -2 | UPDATE_INV_FULL | 69 | -2 | moved, size unchanged |
| 87 | 0 | CAM_RESET | 89 | 0 | moved, size unchanged |
| 91 | 5 | IF_SETHIDE | 103 | 5 | moved, size unchanged |
| 93 | -1 | MESSAGE_GAME | 105 | -1 | moved, size unchanged |
| 94 | 8 | IF_OPENSUB | 17 | 8 | moved, size unchanged |
| 95 | 5 | MIDI_SONG | 87 | 5 | moved, size unchanged |
| 97 | 10 | IF_SETEVENTS | 34 | 10 | moved, size unchanged |
| 104 | 14 | PLAYER_INFO_DECODE | 78 | 14 | moved, size unchanged |
| 106 | 1 | NPC_UPDATE_ORIGIN | 61 | 1 | moved, size unchanged |
| 108 | -2 | CLANSETTINGS_DELTA | 63 | -2 | moved, size unchanged |
| 109 | 28 | HANDSHAKE_UID | 36 | 28 | moved, size unchanged |
| 112 | 2 | SET_TICK_TIMER | 52 | 2 | moved, size unchanged |
| 114 | -1 | CHAT_FILTER_SETTINGS | 39 | -1 | moved, size unchanged |
| 117 | -1 | CLANCHANNEL_DELTA | 83 | -1 | moved, size unchanged |
| 119 | 35 | CUTSCENE_DATA | 91 | 35 | moved, size unchanged |
| 120 | 0 | CAM_SMOOTHRESET | 80 | 0 | moved, size unchanged |
| 121 | -2 | UPDATE_INV_PARTIAL | 5 | -2 | moved, size unchanged |
| 124 | -2 | CLANSETTINGS_DELTA_CHAT | 11 | -2 | moved, size unchanged |
| 126 | -1 | MESSAGE_FRIENDCHAT | 21 | -1 | moved, size unchanged |
| 130 | -1 | UPDATE_IGNORELIST | 211 | -1 | moved, size unchanged |
| 133 | 1 | NPC_UPDATE_FLAG | 138 | 1 | moved, size unchanged |
| 137 | 1 | SET_CHAT_FILTER_A | 217 | 1 | moved, size unchanged |
| 139 | 0 | LOGOUT_TRANSFER | 209 | 0 | moved, size unchanged |
| 141 | -2 | IF_OPENSUB_THUNK | 186 | -2 | moved, size unchanged |
| 142 | -1 | SET_URL_STRING | 200 | -1 | moved, size unchanged |
| 151 | 21 | PROJANIM_SPECIFIC | 196 | 21 | moved, size unchanged |
| 152 | -1 | IF_SET_HTTP_IMAGE | 146 | -1 | moved, size unchanged |
| 155 | 0 | LOGOUT | 147 | 0 | moved, size unchanged |
| 156 | 1 | SET_CHAT_FILTER_B | 155 | 1 | moved, size unchanged |
| 161 | -2 | PLAYER_GROUP_DELTA | 207 | -2 | moved, size unchanged |
| 162 | 0 | TRIGGER_ONDIALOGABORT | 195 | 0 | moved, size unchanged |
| 169 | 8 | SERVER_TICK_END | 171 | 8 | moved, size unchanged |
| 174 | 8 | ANTI_CHEAT_CHALLENGE | 198 | 8 | moved, size unchanged |
| 175 | -1 | MESSAGE_PRIVATE_ECHO | 129 | -1 | moved, size unchanged |
| 176 | 3 | SET_INTERACTION_FLAG_D | 144 | 3 | moved, size unchanged |
| 184 | 4 | UPDATE_REBOOT_TIMER | 132 | 4 | moved, size unchanged |
| 185 | -1 | MESSAGE_PRIVATE | 151 | -1 | moved, size unchanged |
| 186 | -2 | REBUILD_WORLDENTITY | 188 | -2 | moved, size unchanged |
| 193 | 1 | SET_INTERACTION_FLAG_C | 174 | 1 | moved, size unchanged |
| 197 | -2 | SKIP_DATA | 166 | -2 | moved, size unchanged |
| 198 | -1 | UPDATE_URL_STRING | 214 | -1 | moved, size unchanged |
| 199 | -2 | REBUILD_REGION | 172 | -2 | moved, size unchanged |
| 202 | -2 | PLAYER_INFO_DECODE_2 | 180 | -2 | moved, size unchanged |
| 208 | -2 | UPDATE_INV_GROUP | 177 | -2 | moved, size unchanged |
| 209 | -2 | NPC_INFO_THUNK | 205 | -2 | moved, size unchanged |
| 212 | -1 | SET_WORLD_TARGET | 187 | -1 | moved, size unchanged |
| 213 | -1 | SWITCH_WORLD | 179 | -1 | moved, size unchanged |

---

## §5 New / Removed

### New in 948 (no 947-3 named equivalent identified) — 62 entries

These are 948 opcodes whose handler has a real name in 948 (per the CSV / BindHandlers walk) but whose 947-3 origin I have NOT identified. They could be either:
(a) Genuinely new packets introduced after 947-3
(b) 947-3 packets that had `UNKNOWN_N` names which we never resolved — the 948 handler binding now reveals their identity

Without walking each handler's 947-3 equivalent body, I can't distinguish (a) from (b) yet. Listed in 948-opcode order:

| 948 op | 948 size | Handler / Name |
|---:|---:|---|
| 5 | 0 | jag::packethandlers::ClientState::RESET_ALL_VARPS |
| 6 | 7 | jag::packethandlers::ZoneUpdates::LOC_PREFETCH |
| 8 | 5 | IF_SETPLAYERHEAD_ACTIVE |
| 14 | 8 | IF_SETSPRITE |
| 16 | 2 | jag::packethandlers::ZoneUpdates::LOC_DEL |
| 21 | 10 | jag::packethandlers::ZoneUpdates::LOC_ANIM_SPECIFIC |
| 24 | -1 | jag::packethandlers::ClientState::UPDATE_ZONE_PARTIAL |
| 30 | 8 | IF_SET2DANGLE |
| 32 | 8 | IF_SETCOLOUR |
| 34 | 8 | packethandlers::Audio::SOUND_AREA_SYNTH |
| 38 | 8 | IF_SET_MODEL_FRAME |
| 40 | 8 | IF_SUBSWAP |
| 44 | 6 | jag::game::StatTable::UpdateStat |
| 46 | 5 | jag::packethandlers::ZoneUpdates::OBJ_ADD |
| 50 | -1 | jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE |
| 55 | 0 | jag::packethandlers::ClientState::DESTROY_ZONE_DATA |
| 58 | 0 | jag::packethandlers::ClientState::RESET_CLIENT_STATE |
| 59 | 10 | IF_SETNPCMODEL |
| 60 | 25 | IF_SETPLAYERMODEL_SELF |
| 68 | 10 | IF_SETMODELORIGIN |
| 70 | 25 | IF_SETPLAYERMODEL_OTHER |
| 71 | 7 | jag::packethandlers::ZoneUpdates::OBJ_REVEAL |
| 84 | 10 | IF_SETOBJECT |
| 86 | 10 | IF_SETANIM |
| 90 | -1 | jag::packethandlers::ZoneUpdates::LOC_ADD |
| 92 | -1 | jag::packethandlers::Misc::SET_VARC_STR_SMALL |
| 96 | 4 | IF_SETANIM_ACTIVE |
| 99 | 6 | IF_SETRECOL |
| 101 | 4 | IF_SETOBJECT_ACTIVE |
| 102 | 8 | IF_SETMODEL |
| 103 | 8 | IF_SETGRAPHIC |
| 105 | -1 | packethandlers::Chat::MESSAGE_CLANCHANNEL |
| 107 | 3 | jag::packethandlers::ZoneUpdates::OBJ_DEL |
| 113 | 11 | jag::packethandlers::ZoneUpdates::MAP_ANIM |
| 115 | 10 | IF_SETNPCHEAD |
| 118 | 29 | IF_SETPLAYERMODEL_SNAPSHOT |
| 122 | -2 | IF_SETTEXT |
| 123 | 0 | IF_TRIGGER_CLOSE |
| 125 | 7 | jag::packethandlers::ZoneUpdates::OBJ_COUNT |
| 132 | 2 | packethandlers::Audio::SOUND_STOP |
| 136 | 5 | IF_SETANIM_SMALL |
| 138 | 2 | packethandlers::Misc::SKIP_2_BYTES |
| 148 | 2 | IF_CLOSESUB_BY_ID |
| 158 | 9 | IF_SETSCROLLSIZE |
| 164 | 28 | jag::packethandlers::ZoneUpdates::MAP_PROJANIM_HALT |
| 165 | 14 | IF_SETMODEL_COORD |
| 167 | 12 | packethandlers::Audio::SYNTH_SOUND |
| 168 | -1 | jag::packethandlers::ZoneUpdates::SOUND_AREA |
| 170 | 5 | jag::packethandlers::ZoneUpdates::LOC_MERGE |
| 173 | -2 | packethandlers::ZoneUpdates::UNKNOWN_op173_handler |
| 177 | 29 | jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC_HALT |
| 179 | 9 | IF_SETSCROLLPOS |
| 180 | 5 | IF_SETOBJECT_SMALL |
| 183 | 14 | jag::packethandlers::ZoneUpdates::MAP_ANIM_SPECIFIC |
| 189 | 4 | packethandlers::Audio::VORBIS_PRELOAD |
| 190 | 0 | jag::packethandlers::ClientState::CLEAR_PENDING_UPDATES |
| 195 | 4 | packethandlers::Audio::SOUND_AREA_SYNTH_2 |
| 200 | 2 | packethandlers::Audio::SOUND_GROUP_STOP |
| 205 | 6 | packethandlers::Audio::SOUND_GROUP_SPEED |
| 206 | 5 | IF_SETNPCHEAD_ACTIVE |
| 210 | 6 | packethandlers::Audio::VORBIS_SONG |
| 217 | 4 | packethandlers::Audio::SOUND_MODIFY |

### Still unnamed in 948 (`FUN_*` handlers, no name resolution) — 73 entries

These are 948 opcodes whose handler is still a raw `FUN_*` symbol. They are listed here so the implementation agent knows where the codec table has gaps. Each entry needs a separate Ghidra walk to identify what it does.

| 948 op | 948 size | 948 handler symbol |
|---:|---:|---|
| 10 | 3 | FUN_00119870 |
| 15 | 11 | FUN_001dade0 |
| 17 | -1 | FUN_0013eec0 |
| 19 | -2 | FUN_000f7080 |
| 20 | 3 | FUN_001741b0 |
| 25 | -2 | FUN_001898a0 |
| 26 | -2 | FUN_001a63e0 |
| 27 | 10 | FUN_00187490 |
| 28 | 6 | FUN_00119910 |
| 31 | 6 | FUN_00186f40 |
| 33 | -1 | FUN_001a0f40 |
| 36 | 10 | FUN_00186c60 |
| 42 | 10 | FUN_001875f0 |
| 43 | 12 | FUN_00157080 |
| 47 | 3 | FUN_001196f0 |
| 48 | 3 | FUN_00119560 |
| 51 | 6 | FUN_001197b0 |
| 53 | -2 | UNKNOWN |
| 54 | -1 | FUN_000ef260 |
| 57 | 6 | FUN_00186e60 |
| 61 | 3 | FUN_001199b0 |
| 63 | 10 | FUN_0014ebc0 |
| 64 | 6 | FUN_00119600 |
| 66 | 5 | FUN_00187160 |
| 69 | 6 | FUN_001194b0 |
| 73 | 2 | FUN_00175810 |
| 83 | -2 | FUN_001def40 |
| 88 | 4 | FUN_001dad40 |
| 89 | 19 | FUN_000f1f70 |
| 98 | 25 | FUN_000f25d0 |
| 100 | 4 | FUN_00187020 |
| 110 | -2 | thunk_FUN_001d2060 |
| 111 | 6 | FUN_00175960 |
| 116 | -2 | FUN_001500a0 |
| 127 | 0 | FUN_00182610 |
| 128 | 0 | FUN_00173e40 |
| 129 | 3 | FUN_001dc630 |
| 131 | 3 | FUN_000f9600 |
| 134 | -2 | FUN_00179460 |
| 135 | -2 | FUN_001758f0 |
| 140 | 17 | FUN_000f2280 |
| 143 | 6 | FUN_00119e20 |
| 144 | 2 | FUN_00119c30 |
| 145 | 2 | FUN_00187890 |
| 146 | 2 | FUN_000f98f0 |
| 147 | 10 | FUN_00141510 |
| 149 | 6 | FUN_000f9a20 |
| 150 | 3 | FUN_001db620 |
| 153 | 4 | FUN_001dc100 |
| 154 | 5 | FUN_001de6b0 |
| 157 | 1 | FUN_00173a50 |
| 159 | -2 | FUN_000ec3a0 |
| 160 | 9 | FUN_001d76a0 |
| 163 | 15 | FUN_001db050 |
| 166 | 5 | FUN_00120150 |
| 171 | 3 | FUN_000ef800 |
| 172 | 1 | FUN_000f03f0 |
| 181 | 4 | FUN_00186b60 |
| 182 | 3 | FUN_001dbb80 |
| 187 | 1 | FUN_00173cd0 |
| 188 | -2 | FUN_000f7090 |
| 191 | 4 | FUN_00186be0 |
| 192 | 1 | FUN_00173de0 |
| 194 | 1 | FUN_000efa80 |
| 196 | 10 | FUN_00119350 |
| 201 | 3 | FUN_000ef970 |
| 203 | 3 | FUN_001dd3e0 |
| 204 | 1 | FUN_000f0380 |
| 207 | 3 | FUN_00119b80 |
| 211 | 33 | FUN_000f1420 |
| 214 | 0 | FUN_00182650 |
| 215 | 8 | FUN_001deaa0 |
| 216 | -2 | FUN_0018fea0 |

### Removed (947-3 named opcodes with no identified 948 destination) — 19 entries

These 947-3 opcodes had named handlers, but their handler's short name does not appear in the 948 BindHandlers walk. They may be:
- Renamed in 948 to a name we don't recognize
- Removed from the 948 protocol
- Moved to a Variables/Interfaces/Audio sub-constructor that we haven't walked yet

| 947-3 op | 947-3 size | 947-3 name |
|---:|---:|---|
| 1 | 3 | CLIENT_SETVARC_SMALL |
| 6 | -1 | HASHED_WORLD_TOKEN |
| 10 | 3 | VARP_SMALL |
| 48 | 0 | RESET_CLIENT_VARCACHE |
| 55 | 6 | CLIENT_SETVARCBIT_LARGE |
| 66 | 6 | UPDATE_STAT |
| 67 | -1 | CLIENT_SETVARC_STR |
| 101 | -2 | MESSAGE_TYPE6 |
| 102 | -2 | UPDATE_FRIENDLIST |
| 111 | 6 | VARP_LARGE |
| 112 | 6 | CLIENT_SETVARC_LARGE |
| 115 | 3 | CLIENT_SETVARCBIT_SMALL |
| 121 | -2 | RUNCLIENTSCRIPT |
| 125 | -1 | MESSAGE_CLANCHANNEL_SYSTEM |
| 149 | 17 | NEW_PACKET_149 |
| 159 | -2 | WORLDLIST_FETCH_REPLY |
| 170 | 10 | VARP_LONG |
| 189 | 3 | IF_MOVESUB |
| 216 | 0 | NO_TIMEOUT |

---

## World-Login Critical Opcode Migration

The user explicitly flagged these as gating world-login work. **All 8 packets are MOVED — codec changes required.** Decompiled and verified in 948 (each handler's behavior is byte-equivalent to its 947-3 ancestor):

| 947-3 op | Name | 948 op | 948 size | 948 handler addr | Verified? |
|---:|---|---:|---:|---|---|
| 12 | NPC_INFO | **52** (0x34) | -2 | 0x001d79a0 (`jag::NPCList::ProcessNpcInfo`) | ✓ decompiled |
| 27 | PLAYER_INFO | **22** (0x16) | -2 | 0x00161720 (`jag::PlayerList::ProcessPlayerInfo`) | ✓ decompiled |
| 90 | REBUILD_NORMAL_SIMPLE | **81** (0x51) | -2 | 0x001da8b0 | ✓ decompiled (0x7B magic at offset 3 confirmed) |
| 172 | REBUILD_REGION → REBUILD_NORMAL | **199** (0xc7) | -2 | 0x00120260 | ✓ decompiled (multi-scene grid form) |
| 188 | REBUILD_WORLDENTITY | **186** (0xba) | -2 | 0x000efd80 | ✓ decompiled (0xff-terminator loop pattern) |
| 18 | UPDATE_ZONE_FULL_FOLLOWS | **78** (0x4e) | 3 | 0x000f9510 | ✓ decompiled |
| 57 | UPDATE_ZONE_PARTIAL_FOLLOWS | **41** (0x29) | 3 | 0x000ef150 | ✓ decompiled (note: byte order in 3B header subtly reordered vs 947-3) |
| 126 | UPDATE_ZONE_PARTIAL_ENCLOSED | **76** (0x4c) | -2 | 0x000eefc0 | ✓ decompiled (sub-prot dispatch table at DAT_015d4580) |

---

## PlayerUpdateMaskKey & NpcUpdateMaskKey — Bit Position Diff

The user explicitly expected the per-revision mask key tables to shift. They have. Both the bit-position semantics AND the dispatch order have changed.

**Source decompiles:**
- `jag::PlayerEntity::ProcessExtendedInfo @ 0x0015e110` (948) — newly named in this session
- `jag::NPCEntity::ProcessExtendedInfoNPC @ 0x001621c0` (948) — newly named in this session

### Player update mask — header format

**947-3:** 1..4 byte LE value, expansion bits at LE positions **0, 14, 18**
**948:**   1..4 byte LE value, expansion bits at LE positions **0, 13, 22**

Expansion bit semantics: bit 0 set in byte 0 → read byte 1; bit (varies) set in byte 1 → read byte 2; bit (varies) set in byte 2 → read byte 3. **The expansion-bit positions ARE part of the protocol**, so the rev948 mask encoder must use the new positions or the client will mis-parse the header.

### Player update mask — dispatch order in 948

Decompiled from `jag::PlayerEntity::ProcessExtendedInfo @ 0x0015e110`. The 19 blocks below are tested in this fixed order; the encoder must serialize blocks in the same order.

| order | bit | mask (hex) | 947-3 PlayerUpdateMaskKey entry (if matching) | Notes |
|---:|---:|---|---|---|
| 1  | 26 | 0x4000000  | SPOT_ANIM_REMOVAL(26, order 12 in 947-3) | dispatch order moved 12→1 |
| 2  | 18 | 0x40000    | (947-3 had no bit-18 entry in the listed enum — UNK_BIT?) | needs lookup |
| 3  | 20 | 0x100000   | UNK_BIT20(20, order 21 in 947-3) | dispatch order moved 21→3 |
| 4  |  6 | 0x40       | OVERHEAD_TEXT(6, order 7 in 947-3) | dispatch order moved 7→4 |
| 5  | 24 | 0x1000000  | SPOT_ANIMS(24, order 13 in 947-3) | dispatch order moved 13→5 |
| 6  | 11 | 0x800      | (947-3 had no bit-11 entry in the listed enum) | likely new |
| 7  | 15 | 0x8000     | CHAT_TEXT(15, order 23 in 947-3) | dispatch order moved 23→7 |
| 8  |  7 | 0x80       | SET_DISPLAY_COLOR_BIT7(7, order 20 in 947-3) | dispatch order moved 20→8 |
| 9  | 25 | 0x2000000  | UNK_BIT25(25, order 16 in 947-3) | dispatch order moved 16→9 |
| 10 | 16 | 0x10000    | CHAT_TEXT_PRIVATE(16, order 19 in 947-3) | dispatch order moved 19→10 |
| 11 |  9 | 0x200      | UNK_BIT9(9, order 3 in 947-3) | dispatch order moved 3→11 |
| 12 | 23 | 0x800000   | OVERHEAD_ICON_BLOCK(23, order 17 in 947-3) | dispatch order moved 17→12 |
| 13 |  5 | 0x20       | FACE_ENTITY(5, order 14 in 947-3) | dispatch order moved 14→13 |
| 14 | 19 | 0x80000    | TRANSIENT_BOOL_19(19, order 5 in 947-3) | dispatch order moved 5→14 |
| 15 | 12 | 0x1000     | NAME_STRING(12, order 2 in 947-3) | dispatch order moved 2→15 |
| 16 | 10 | 0x400      | UNK_BIT10(10, order 8 in 947-3) | dispatch order moved 8→16 |
| 17 | 21 | 0x200000   | OVERHEAD_CHAT(21, order 9 in 947-3) | dispatch order moved 9→17 |
| 18 |  4 | 0x10       | FORCED_MOVEMENT(4, order 4 in 947-3) | dispatch order moved 4→18 |
| 19 | 17 | 0x20000    | UNK_BIT17(17, order 1 in 947-3) | dispatch order moved 1→19 |

**Bits in 947-3 NOT seen in 948 dispatch list:** bit 1 (FACE_DIRECTION), bit 2 (APPEARANCE), bit 3 (HITMARKS), bit 8 (OVERHEAD_OPACITY), bit 13 (UNK_BIT13 — now used as expansion bit in 948!), bit 14 (was expansion bit in 947-3), bit 22 (POSITION_COLOR — now used as expansion bit in 948!).

**Bit 13 and 22 are now EXPANSION BITS in 948**, so they cannot also be data flags. This is a meaningful protocol change. The 947-3 UNK_BIT13 (order 11) and POSITION_COLOR (order 10) entries must be either repurposed onto different bits in 948 or removed.

**CAVEAT:** The dispatch-order analysis above was extracted by `grep`-ing all `(uVar38 & 0xXXX) != 0` tests from the linear decompile of `ProcessExtendedInfo`. Some bits (especially bit 1, 2, 3, 8) may be handled inside larger compound `if` branches that I didn't fully unwind — the table reflects ONLY the top-level `if (mask & X)` conditions and may be incomplete for blocks that are nested inside outer guards. **Before encoding any new packets in rev948, the implementation agent should re-walk `0x0015e110` end-to-end and confirm each block's specific transforms.**

### NPC update mask — header format

**947-3:** 1..5 byte LE value, expansion bits at LE positions **6, 13, 22, 24**
**948:**   1..5 byte LE value (uses `local_2b0` ulong as mask), expansion bits TBD (header decode runs through `bVar14 & 0x40` etc.; the exact expansion pattern needs a focused decompile of the first ~150 lines of `ProcessExtendedInfoNPC`)

### NPC update mask — dispatch order in 948

Top-level mask tests in `jag::NPCEntity::ProcessExtendedInfoNPC @ 0x001621c0`, in source order. The 64-bit mask uses bits up to position 31 (e.g. `0x80000000`):

| order | bit | mask (hex) | 947-3 NpcUpdateMaskKey entry (if matching) | Notes |
|---:|---:|---|---|---|
| 1  |  4 | 0x10        | FACE_ENTITY(4, order 19 in 947-3) | moved 19→1 |
| 2  |  5 | 0x20        | ANIMATION(5, order 18 in 947-3) | moved 18→2 |
| 3  | 31 | 0x80000000  | BOOLEAN_FLAG(31, order 5 in 947-3) | moved 5→3 |
| 4  | 17 | 0x20000     | SPOT_ANIM_LIST_PRIMARY(17, order 16 in 947-3) | moved 16→4 |
| 5  | 14 | 0x4000      | UNK_BIT14(14, order 7 in 947-3) | moved 7→5 |
| 6  | 28 | 0x10000000  | COMBAT_LEVEL_OVERRIDE_RGB(28, order 8 in 947-3) | moved 8→6 |
| 7  | 20 | 0x100000    | SPOT_ANIM_LIST_HEAD(20, order 25 in 947-3) | moved 25→7 |
| 8  | 29 | 0x20000000  | UNK_BIT29(29, order 12 in 947-3) | moved 12→8 |
| 9  | 27 | 0x8000000   | VISIBILITY_FLAG(27, order 23 in 947-3) | moved 23→9 |
| 10 | 13 | 0x2000      | (no 947-3 bit-13 entry — possibly new) | |
| 11 | 30 | 0x40000000  | UNK_BIT30(30, order 3 in 947-3) | moved 3→11 |
| 12 |  7 | 0x80        | FORCED_MOVEMENT(7, order 1 in 947-3) | moved 1→12 |
| 13 | 12 | 0x1000      | OVERHEAD_ICON(12, order 6 in 947-3) | moved 6→13 |
| 14 |  9 | 0x200       | UNK_BIT9(9, order 28 in 947-3) | moved 28→14 |
| 15 | 24 | 0x1000000   | (947-3 had no bit-24 data entry — was an expansion bit) | likely new data slot in 948 |
| 16 | 11 | 0x800       | ANIMATION_LIST(11, order 4 in 947-3) | moved 4→16 |
| 17 | 21 | 0x200000    | EXACT_MOVE(21, order 9 in 947-3) | moved 9→17 |
| 18 | 23 | 0x800000    | COMBAT_LEVEL_HEADBAR_ID(23, order 27 in 947-3) | moved 27→18 |
| 19 | 18 | 0x40000     | NAME_OVERRIDE(18, order 21 in 947-3) | moved 21→19 |
| 20 | 15 | 0x8000      | MODEL_OVERRIDE_ID(15, order 17 in 947-3) | moved 17→20 |
| 21 | 16 | 0x10000     | CLIENT_SCRIPT_OVERRIDE(16, order 15 in 947-3) | moved 15→21 |
| 22 | 26 | 0x4000000   | UNK_BIT26(26, order 29 in 947-3) | moved 29→22 |
| 23 | 22 | 0x400000    | (947-3 had no bit-22 entry — possibly new) | |

**Same CAVEAT as Player mask** — top-level tests only; nested branches may add more blocks. The 24 entries above cover the bulk of the dispatch but the implementation agent should re-walk `0x001621c0` for completeness.

**947-3 NPC bits NOT seen in 948 dispatch:** bit 0 (CHAT_OVERHEAD), bit 1 (HITMARKS_AND_HEADBARS), bit 3 (EXACT_MOVE_DESTINATION), bit 8 (UNK_BIT8), bit 19 (SPOT_ANIM_CLEAR_BY_SLOT), bit 25 (REMOVE_FROM_LIST_BY_ID), bit 32 (TRANSIENT_BOOL_E80), bit 33 (SPOTANIM_TRANSFORM_LIST).

---

## Implementation guidance for the rev948 codec author

1. **Create `core/.../rev948/Rev948ServerProtStubs.kt`** as a sibling to the existing `Rev947ServerProtStubs.kt`. Copy the structure of `s(op, name, size)` calls; the 218 entries come straight from `docs/net/serverprot/948-opcode-tables.md` ServerProt table.
2. **Do NOT reuse rev947 codec bodies wholesale** — most opcodes moved, and the world-login critical packet handlers in 948 have subtly different byte-orderings (e.g., UPDATE_ZONE_PARTIAL_FOLLOWS 3-byte header order is reshuffled). Each codec body needs to be re-derived from the 948 handler decompile.
3. **Make PlayerUpdateMaskKey and NpcUpdateMaskKey revision-aware** — split into `Rev947PlayerUpdateMaskKey.kt` (current) + `Rev948PlayerUpdateMaskKey.kt` (new). The 948 enum uses different bit positions for expansion and a substantially different dispatch order. The encoder should select the enum table based on the active revision.
4. **All `Codec.ProtSize` declarations**: use `-1` for varByte, `-2` for varShort, positive int for fixed — same as 947-3.
5. **ClientProt is provisional** — only 48 of 130 opcodes have confirmed names. The implementation agent should walk each ClientProt opcode used by their codec (typically just the input handlers wired to `IfButtonHandler` etc.) before committing.

**Reference docs in this campaign:**
- `docs/net/serverprot/948-opcode-tables.md` — the full opcode tables (this delta's companion)
- `docs/net/serverprot/bindhandlers-947-3.md` — 947-3 BindHandlers ground truth (for comparing the binding patterns to 948)
- `core/.../rev947/Rev947ServerProtStubs.kt` — 947-3 stub table (the migration source)
