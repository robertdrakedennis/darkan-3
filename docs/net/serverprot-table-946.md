# ServerProt Master Opcode Table (Rev 946)

All 217 ServerProt opcodes (0x00–0xD8). Sizes verified from rs2client binary (`jag::ServerProt::RegisterAll` at `0x00182030`).

Names are verified `jag::ServerProt::*` symbols from librs2client.so (rev ~890) matched to rev 946 handlers. Verification method key:
- **[LIB]** = exact symbol match from librs2client.so namespace
- **[SIZE]** = unique size within category deduction
- **[HAND]** = handler logic decompilation match (librs2client.so vs rs2client)
- **[CAP]** = live packet capture verification
- **UNKNOWN_XX** = no verified Jagex symbol match

Size: positive = fixed bytes, `var_byte` = variable 1-byte length prefix, `var_short` = variable 2-byte BE length prefix, `0` = no payload.

| Opcode | Hex | Size | Name | Source |
|--------|-----|------|------|--------|
| 0 | 0x00 | 28 | UPDATE_UID192 | [LIB] |
| 1 | 0x01 | var_byte | MESSAGE_GAME | [LIB] |
| 2 | 0x02 | var_short | UNKNOWN_2 | |
| 3 | 0x03 | 7 | OBJ_COUNT | [LIB] |
| 4 | 0x04 | 10 | IF_SETHIDE | [LIB] |
| 5 | 0x05 | 10 | MIDI_JINGLE | [LIB] |
| 6 | 0x06 | 10 | LOC_ANIM_SPECIFIC | [LIB] |
| 7 | 0x07 | 29 | IF_SETMODEL | [LIB] |
| 8 | 0x08 | 6 | IF_MOVESUB | [LIB] |
| 9 | 0x09 | var_byte | UNKNOWN_9 | |
| 10 | 0x0A | var_byte | UNKNOWN_10 | |
| 11 | 0x0B | var_short | UNKNOWN_11 | |
| 12 | 0x0C | 6 | CLIENT_SETVARC_LARGE | [SIZE] |
| 13 | 0x0D | 1 | UNKNOWN_13 | |
| 14 | 0x0E | 3 | VARP_SMALL | [LIB+CAP] |
| 15 | 0x0F | var_short | MESSAGE_PUBLIC | [LIB] |
| 16 | 0x10 | var_short | RUNCLIENTSCRIPT | [LIB] |
| 17 | 0x11 | var_short | UPDATE_IGNORELIST | [LIB] |
| 18 | 0x12 | var_short | UPDATE_FRIENDLIST | [LIB+HAND] |
| 19 | 0x13 | 3 | CLIENT_SETVARC_SMALL | [LIB+CAP] |
| 20 | 0x14 | var_short | IF_OPENSUB_ACTIVE | [HAND] |
| 21 | 0x15 | 32 | IF_SETANGLE | [LIB] |
| 22 | 0x16 | 2 | UNKNOWN_22 | |
| 23 | 0x17 | 1 | UNKNOWN_23 | |
| 24 | 0x18 | 20 | MAP_PROJANIM | [LIB] |
| 25 | 0x19 | 0 | UNKNOWN_25 | |
| 26 | 0x1A | 8 | UNKNOWN_26 | |
| 27 | 0x1B | 1 | UPDATE_RUNENERGY | [LIB] |
| 28 | 0x1C | var_short | NPC_INFO | [LIB] |
| 29 | 0x1D | var_byte | UNKNOWN_29 | |
| 30 | 0x1E | var_byte | UNKNOWN_30 | |
| 31 | 0x1F | var_byte | MESSAGE_QUICKCHAT_CLANCHANNEL | [LIB] |
| 32 | 0x20 | 0 | UNKNOWN_32 | |
| 33 | 0x21 | 0 | VORBIS_SPEECH_STOP | [HAND] |
| 34 | 0x22 | 5 | SOUND_MIXBUSS_SETLEVEL | [LIB] |
| 35 | 0x23 | 0 | UNKNOWN_35 | |
| 36 | 0x24 | var_short | CHANGE_LOBBY | [LIB] |
| 37 | 0x25 | var_byte | MESSAGE_QUICKCHAT_PRIVATE | [LIB] |
| 38 | 0x26 | 23 | IF_SETPOSITION | [LIB] |
| 39 | 0x27 | var_short | CLANSETTINGS_FULL | [LIB] |
| 40 | 0x28 | var_byte | UNKNOWN_40 | |
| 41 | 0x29 | 10 | LOC_ADD_CHANGE | [LIB] |
| 42 | 0x2A | var_short | PLAYER_INFO | [LIB] |
| 43 | 0x2B | var_short | UPDATE_ZONE_PARTIAL_ENCLOSED | [LIB] |
| 44 | 0x2C | 6 | CAM_MOVETO | [LIB] |
| 45 | 0x2D | 5 | IF_SETSCROLLPOS | [LIB] |
| 46 | 0x2E | var_byte | MESSAGE_CLANCHANNEL_SYSTEM | [HAND] |
| 47 | 0x2F | 14 | UNKNOWN_47 | |
| 48 | 0x30 | 5 | IF_SETCOLOUR | [LIB] |
| 49 | 0x31 | 10 | MIDI_SONG | [LIB] |
| 50 | 0x32 | 4 | IF_SETOBJECT | [LIB] |
| 51 | 0x33 | 6 | VARBIT_LARGE | [LIB] |
| 52 | 0x34 | 1 | CAM_FORCEANGLE | [LIB] |
| 53 | 0x35 | 35 | CUTSCENE | [LIB] |
| 54 | 0x36 | 25 | UNKNOWN_54 | |
| 55 | 0x37 | 4 | CAM_SHAKE | [LIB] |
| 56 | 0x38 | var_byte | MESSAGE_FRIENDCHANNEL | [LIB] |
| 57 | 0x39 | var_short | IF_SETTARGETPARAM | [LIB] |
| 58 | 0x3A | 6 | CAM_LOOKAT | [LIB] |
| 59 | 0x3B | 12 | UNKNOWN_59 | |
| 60 | 0x3C | var_byte | UNKNOWN_60 | |
| 61 | 0x3D | 8 | IF_SETTEXTFONT | [LIB] |
| 62 | 0x3E | 10 | IF_SETRECOL | [LIB] |
| 63 | 0x3F | var_short | CLANSETTINGS_DELTA | [LIB] |
| 64 | 0x40 | 2 | LOC_DEL | [LIB] |
| 65 | 0x41 | var_byte | UNKNOWN_65 | |
| 66 | 0x42 | var_short | PLAYER_GROUP_FULL | [HAND] |
| 67 | 0x43 | 25 | UNKNOWN_67 | |
| 68 | 0x44 | var_byte | MESSAGE_QUICKCHAT_FRIENDCHAT | [LIB] |
| 69 | 0x45 | 0 | CAM_RESET | [LIB] |
| 70 | 0x46 | 5 | UNKNOWN_70 | |
| 71 | 0x47 | 8 | UNKNOWN_71 | |
| 72 | 0x48 | 3 | VARBIT_SMALL | [LIB] |
| 73 | 0x49 | 2 | UNKNOWN_73 | |
| 74 | 0x4A | 8 | UNKNOWN_74 | |
| 75 | 0x4B | 10 | IF_SETRETEX | [LIB] |
| 76 | 0x4C | 8 | UNKNOWN_76 | |
| 77 | 0x4D | 4 | UNKNOWN_77 | |
| 78 | 0x4E | 7 | LOC_PREFETCH | [LIB] |
| 79 | 0x4F | 6 | UNKNOWN_79 | |
| 80 | 0x50 | 1 | UNKNOWN_80 | |
| 81 | 0x51 | var_short | UPDATE_INV_PARTIAL | [LIB] |
| 82 | 0x52 | var_byte | LOC_CUSTOMISE | [LIB] |
| 83 | 0x53 | var_short | CLANCHANNEL_FULL | [LIB] |
| 84 | 0x54 | var_short | UNKNOWN_84 | |
| 85 | 0x55 | 4 | JCOINS_UPDATE | [HAND] |
| 86 | 0x56 | var_byte | NPC_HEADICON_SPECIFIC | [LIB] |
| 87 | 0x57 | 5 | OBJ_ADD | [LIB] |
| 88 | 0x58 | 3 | CLIENT_SETVARCBIT_SMALL | [SIZE] |
| 89 | 0x59 | 8 | IF_SETCLICKMASK | [LIB] |
| 90 | 0x5A | 3 | UPDATE_ZONE_FULL_FOLLOWS | [LIB] |
| 91 | 0x5B | 12 | SPOTANIM_SPECIFIC | [LIB] |
| 92 | 0x5C | var_short | UNKNOWN_92 | |
| 93 | 0x5D | 10 | IF_SETPLAYERHEAD | [HAND] |
| 94 | 0x5E | 19 | UNKNOWN_94 | |
| 95 | 0x5F | var_short | UNKNOWN_95 | |
| 96 | 0x60 | 4 | UNKNOWN_96 | |
| 97 | 0x61 | 2 | UNKNOWN_97 | |
| 98 | 0x62 | 3 | OBJ_DEL | [LIB] |
| 99 | 0x63 | 0 | UNKNOWN_99 | |
| 100 | 0x64 | 10 | IF_SETNPCHEAD | [HAND] |
| 101 | 0x65 | 3 | UNKNOWN_101 | |
| 102 | 0x66 | 1 | UNKNOWN_102 | |
| 103 | 0x67 | 10 | UNKNOWN_103 | |
| 104 | 0x68 | 0 | UNKNOWN_104 | |
| 105 | 0x69 | 8 | IF_SETPLAYERMODEL_SELF | [LIB] |
| 106 | 0x6A | 4 | IF_SETANIM | [LIB] |
| 107 | 0x6B | var_byte | CHAT_FILTER_SETTINGS | [LIB] |
| 108 | 0x6C | 6 | UNKNOWN_108 | |
| 109 | 0x6D | var_short | UNKNOWN_109 | |
| 110 | 0x6E | var_short | CAMERA_UPDATE | [LIB] |
| 111 | 0x6F | var_byte | UNKNOWN_111 | |
| 112 | 0x70 | 0 | RESET_CLIENT_VARCACHE | [LIB+CAP] |
| 113 | 0x71 | 10 | UNKNOWN_113 | |
| 114 | 0x72 | 6 | UPDATE_STAT | [LIB] |
| 115 | 0x73 | 6 | CLIENT_SETVARCBIT_LARGE | [SIZE] |
| 116 | 0x74 | 25 | UNKNOWN_116 | |
| 117 | 0x75 | 11 | VORBIS_SOUND_GROUP | [HAND] |
| 118 | 0x76 | 0 | CAM_SMOOTHRESET | [LIB] |
| 119 | 0x77 | 8 | UNKNOWN_119 | |
| 120 | 0x78 | 3 | UPDATE_ZONE_PARTIAL_FOLLOWS | [LIB] |
| 121 | 0x79 | var_short | UPDATE_INV_FULL | [LIB] |
| 122 | 0x7A | 11 | MAP_ANIM | [LIB] |
| 123 | 0x7B | var_byte | CLANCHANNEL_DELTA | [LIB] |
| 124 | 0x7C | 6 | VARP_LARGE | [LIB+CAP] |
| 125 | 0x7D | 8 | UNKNOWN_125 | |
| 126 | 0x7E | 19 | IF_SETGRAPHIC | [LIB] |
| 127 | 0x7F | 7 | OBJ_REVEAL | [LIB] |
| 128 | 0x80 | 3 | UNKNOWN_128 | |
| 129 | 0x81 | 6 | SET_MAP_FLAG | [LIB] |
| 130 | 0x82 | var_byte | MESSAGE_PRIVATE | [LIB] |
| 131 | 0x83 | 0 | LOGOUT_TRANSFER | [LIB] |
| 132 | 0x84 | 5 | UNKNOWN_132 | |
| 133 | 0x85 | 1 | CREATE_CHECK_EMAIL_REPLY | [LIB] |
| 134 | 0x86 | 0 | LOGOUT | [LIB] |
| 135 | 0x87 | 1 | UPDATE_RUNWEIGHT | [LIB] |
| 136 | 0x88 | 2 | VORBIS_SOUND_GROUP_START | [HAND] |
| 137 | 0x89 | 9 | UNKNOWN_137 | |
| 138 | 0x8A | 10 | UNKNOWN_138 | |
| 139 | 0x8B | 4 | UNKNOWN_139 | |
| 140 | 0x8C | 5 | UNKNOWN_140 | |
| 141 | 0x8D | var_short | UNKNOWN_141 | |
| 142 | 0x8E | 1 | UNKNOWN_142 | |
| 143 | 0x8F | 8 | UNKNOWN_143 | |
| 144 | 0x90 | 4 | UPDATE_REBOOT_TIMER | [LIB] |
| 145 | 0x91 | 8 | UNKNOWN_145 | |
| 146 | 0x92 | 0 | NO_TIMEOUT | [LIB+CAP] |
| 147 | 0x93 | 29 | UNKNOWN_147 | |
| 148 | 0x94 | var_short | UNKNOWN_148 | |
| 149 | 0x95 | var_byte | UNKNOWN_149 | |
| 150 | 0x96 | var_short | WORLDLIST_FETCH_REPLY | [LIB] |
| 151 | 0x97 | 3 | MINIMAP_TOGGLE | [LIB] |
| 152 | 0x98 | var_byte | UNKNOWN_152 | |
| 153 | 0x99 | 2 | UNKNOWN_153 | |
| 154 | 0x9A | var_short | PLAYER_GROUP_DELTA | [HAND] |
| 155 | 0x9B | var_byte | UNKNOWN_155 | |
| 156 | 0x9C | 6 | UNKNOWN_156 | |
| 157 | 0x9D | 3 | UNKNOWN_157 | |
| 158 | 0x9E | 12 | SYNTH_SOUND | [LIB] |
| 159 | 0x9F | 28 | UNKNOWN_159 | |
| 160 | 0xA0 | 6 | UNKNOWN_160 | |
| 161 | 0xA1 | 1 | UNKNOWN_161 | |
| 162 | 0xA2 | var_byte | UNKNOWN_162 | |
| 163 | 0xA3 | var_short | UNKNOWN_163 | |
| 164 | 0xA4 | 1 | UNKNOWN_164 | |
| 165 | 0xA5 | 9 | NPC_ANIM_SPECIFIC | [LIB] |
| 166 | 0xA6 | 0 | MIDI_SONG_STOP | [LIB] |
| 167 | 0xA7 | 2 | UNKNOWN_167 | |
| 168 | 0xA8 | var_short | UNKNOWN_168 | |
| 169 | 0xA9 | 1 | UNKNOWN_169 | |
| 170 | 0xAA | var_short | UNKNOWN_170 | |
| 171 | 0xAB | var_byte | UNKNOWN_171 | |
| 172 | 0xAC | 5 | UNKNOWN_172 | |
| 173 | 0xAD | var_short | UNKNOWN_173 | |
| 174 | 0xAE | var_short | UNKNOWN_174 | |
| 175 | 0xAF | 3 | UNKNOWN_175 | |
| 176 | 0xB0 | 14 | UNKNOWN_176 | |
| 177 | 0xB1 | var_short | UNKNOWN_177 | |
| 178 | 0xB2 | var_short | UNKNOWN_178 | |
| 179 | 0xB3 | 2 | UNKNOWN_179 | |
| 180 | 0xB4 | 5 | IF_CLOSESUB | [LIB] |
| 181 | 0xB5 | 1 | UNKNOWN_181 | |
| 182 | 0xB6 | 5 | IF_OPENSUB | [LIB] |
| 183 | 0xB7 | 10 | UNKNOWN_183 | |
| 184 | 0xB8 | 4 | UNKNOWN_184 | |
| 185 | 0xB9 | 33 | UNKNOWN_185 | |
| 186 | 0xBA | var_short | REBUILD_NORMAL | [LIB] |
| 187 | 0xBB | 4 | UNKNOWN_187 | |
| 188 | 0xBC | 1 | UNKNOWN_188 | |
| 189 | 0xBD | var_short | UNKNOWN_189 | |
| 190 | 0xBE | 3 | UNKNOWN_190 | |
| 191 | 0xBF | 2 | UNKNOWN_191 | |
| 192 | 0xC0 | var_short | UNKNOWN_192 | |
| 193 | 0xC1 | 15 | UNKNOWN_193 | |
| 194 | 0xC2 | 14 | UNKNOWN_194 | |
| 195 | 0xC3 | 3 | UNKNOWN_195 | |
| 196 | 0xC4 | var_short | UNKNOWN_196 | |
| 197 | 0xC5 | 3 | UNKNOWN_197 | |
| 198 | 0xC6 | var_byte | SOUND_AREA | [LIB] |
| 199 | 0xC7 | var_byte | UNKNOWN_199 | |
| 200 | 0xC8 | 4 | UNKNOWN_200 | |
| 201 | 0xC9 | var_byte | MESSAGE_PRIVATE_ECHO | [LIB] |
| 202 | 0xCA | 9 | IF_SETEVENTS | [LIB] |
| 203 | 0xCB | 8 | SERVER_TICK_END | [LIB] |
| 204 | 0xCC | 1 | CREATE_CHECK_NAME_REPLY | [LIB] |
| 205 | 0xCD | 1 | CLEAR_PLAYER_SNAPSHOT | [HAND] |
| 206 | 0xCE | 0 | UNKNOWN_206 | |
| 207 | 0xCF | 2 | IF_OPENTOP | [LIB] |
| 208 | 0xD0 | 3 | UNKNOWN_208 | |
| 209 | 0xD1 | 4 | VORBIS_PRELOAD_SOUNDS | [HAND] |
| 210 | 0xD2 | 4 | UNKNOWN_210 | |
| 211 | 0xD3 | 5 | REBUILD_REGION | [LIB] |
| 212 | 0xD4 | 0 | TRIGGER_ONDIALOGABORT | [HAND] |
| 213 | 0xD5 | 6 | VORBIS_SPEECH_SOUND | [HAND] |
| 214 | 0xD6 | 21 | PROJANIM_SPECIFIC | [LIB] |
| 215 | 0xD7 | 3 | URL_OPEN | [LIB] |
| 216 | 0xD8 | 2 | VORBIS_SOUND_GROUP_STOP | [HAND] |

**Statistics:** 217 registered opcodes (0x00-0xD8). 114 verified Jagex names, 103 UNKNOWN_XX.

## Verified Jagex Names Not Found in Rev 946

These `jag::ServerProt::*` symbols from librs2client.so (rev ~890) were confirmed absent in rev 946 via handler matching:

- `CREATE_ACCOUNT_REPLY`, `CREATE_SUGGEST_NAME_REPLY`, `CREATE_SUGGEST_NAME_ERROR` — lobby account creation removed
- `LOBBY_APPEARANCE` — absorbed into player info system
- `LOGOUT_FULL` — merged into `LOGOUT`
- `SET_PLAYER_OP` — replaced by WorldEntity system
- `SET_MOVEACTION` — likely handled via `RUNCLIENTSCRIPT`
- `STORE_SERVERPERM_VARCS_ACK`, `VARCLAN`, `VARCLAN_ENABLE`, `VARCLAN_DISABLE` — clan variable system restructured
- `PLAYER_SNAPSHOT`, `UPDATE_DOB`, `LAST_LOGIN_INFO` — no matching handler found
- `CAM_REMOVEROOF`, `LOYALTY_UPDATE`, `PLAYER_GROUP_VARPS` — no matching handler found

## Rev 946 Structural Changes (vs rev ~890)

1. **IF_OPENSUB_ACTIVE merger**: Four separate handlers (`IF_OPENSUB_ACTIVE_PLAYER`, `_NPC`, `_LOC`, `_OBJ`) merged into single opcode 20 with polymorphic dispatch.
2. **SetInterfaceModel consolidation**: Individual `IF_SET*HEAD` / `IF_SETPLAYERMODEL_SNAPSHOT` handlers restructured into shared `IF_SETMODEL_thunk` pattern.
3. **WorldEntity system**: New packet category (ops 142, 153, 156, 157, 178, 208) not present in rev 890.
4. **Sound group naming**: Doc's `SOUND_GROUP_STOP` (op 136) is actually `VORBIS_SOUND_GROUP_START` — handler starts group sounds, not stops them. `VORBIS_SOUND_GROUP_STOP` is at op 216.
