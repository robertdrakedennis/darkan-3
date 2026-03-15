# ClientProt Opcode Table (Complete)

Binary: NXT Client (Build 946-5)
Extracted from: `jag::ServerProt::RegisterAll` at `0x00182030` (946-5)
Constructor: `0x00181c40` (ClientProt), `0x00181da0` (ClientProt::InitEntry for lobby entries)
ClientProt struct size: 0x10 bytes (opcode:int32+0x00, size:int32+0x04, name:char*+0x08)
Entry base addresses: `0x016e9560` to `0x016e9d60` (0x10 stride), outlier at `0x016fcfe0` (op 120)
Total opcodes: 130 (0-129)

`[VERIFIED]` All 130 sizes confirmed matching between engine `ClientProt.kt` and 946-5 binary.
6 sizes changed from 946-3: ops 15 (0→3), 21 (0→3), 34 (0→5), 41 (0→9), 80 (0→1), 87 (0→var_byte).
16 UNKNOWN opcodes (11,13,22,41,42,44,53,55,57,72,73,79,100,108,112,126) — 15 have no sender, only op 42 has sender at `0x003693a0`.

## Size Mode Legend

| Mode | Value | Description |
|------|-------|-------------|
| Fixed | >= 0 | Packet has exactly N bytes of payload |
| VAR_BYTE | -1 | Length prefixed with 1 byte (max 255 bytes payload) |
| VAR_SHORT | -2 | Length prefixed with 2 bytes (max 65535 bytes payload) |
| 0 | 0 | No payload bytes (opcode-only packet) |

## Key Infrastructure

- **MakeClientMessage\<ClientProt\>**: `0x001da850` - Allocates packet from pool, writes opcode with Isaac encryption
- **SendClientMessage\<ClientProt\>**: `0x00cb4a50` - Queues packet for transmission
- **WriteOpcodeWithIsaac**: `0x001ccc80` - Writes encrypted opcode byte using Isaac cipher
- **Packet write methods**: p1=`0x001ccc60`, pT_ushort=`0x001c2570`, pT_int=`0x001da830`, pT_long=`0x0022f4b0`, pStringNoConversion=`0x00cb8840`

## Complete Opcode Table

| Opcode | Size | DAT Address | Identified Name | Sender Function | Category |
|--------|------|-------------|-----------------|-----------------|----------|
| 0 | VAR_SHORT | 0x016fbac0 | EVENT_APPLET_FOCUS | 0x0032f260 | Misc |
| 1 | 9 | 0x016fbab0 | EVENT_CAMERA_POSITION | 0x0021ec60 | Misc |
| 2 | 6 | 0x016fbaa0 | CAMERA_DIRECTION | 0x002825b0 | Misc |
| 3 | VAR_SHORT | 0x016fba90 | VERIFIED_STRING_SEND (cs2) | 0x0036ed10 | Misc |
| 4 | VAR_BYTE | 0x016fba80 | MESSAGE_PUBLIC (with effects) | 0x003a5aa0 | Chat |
| 5 | 15 | 0x016fba70 | OPNPC_T_LONG | (extended NPC targeted) | Actions |
| 6 | 3 | 0x016fba60 | OPOBJ5 | (via OPOBJ switch) | Actions |
| 7 | VAR_SHORT | 0x016fba50 | RESUME_COUNTDIALOG | 0x003604a0 | Interface |
| 8 | 4 | 0x016fba40 | STRTOL_SEND (cs2) | 0x00379490 | Misc |
| 9 | 3 | 0x016fba30 | OPNPC_T1 | SendOpNpc1T | Actions |
| 10 | VAR_SHORT | 0x016fba20 | RESUME_PAUSEBUTTON | 0x00360300 | Interface |
| 11 | 4 | 0x016fba10 | *(unknown)* | | |
| 12 | 16 | 0x016fba00 | IF_BUTTON_T | FUN_002e2b90 (dispatcher) | Interface |
| 13 | 2 | 0x016fb9f0 | *(unknown)* | | |
| 14 | 3 | 0x016fb9e0 | OPOBJ7 | (via OPOBJ switch) | Actions |
| 15 | 3 | 0x016e9630 | NO_TIMEOUT | 0x00360cb0 | Misc | `[946-5]` size 0→3 |
| 16 | 11 | 0x016fb9c0 | OPLOC_T (long form) | 0x001feca0 | Actions |
| 17 | VAR_BYTE | 0x016fb9b0 | EVENT_MOUSE_CLICK | 0x00211130 (CS2 getter) | Misc |
| 18 | 8 | 0x016fb9a0 | IF_BUTTON5 | IfButtonXInner (table[4]) | Interface |
| 19 | 3 | 0x016fb990 | OPNPC_T2 | SendOpNpc2T | Actions |
| 20 | 3 | 0x016fb980 | OPOBJ1 | (via OPOBJ switch) | Actions |
| 21 | 3 | 0x016e9670 | MAP_BUILD_COMPLETE | 0x00420950 | Misc | `[946-5]` size 0→3 |
| 22 | 1 | 0x016fb960 | *(unknown)* | | |
| 23 | 7 | 0x016fb950 | OPNPC3 | (via OPNPC switch) | Actions |
| 24 | VAR_BYTE | 0x016fb940 | CLIENT_CHEAT | 0x00360f30 | Misc |
| 25 | 7 | 0x016fb930 | OPNPC2 | (via OPNPC switch) | Actions |
| 26 | 7 | 0x016fb920 | OPNPC1 | (via OPNPC switch) | Actions |
| 27 | 12 | 0x016fb910 | OPLOC_T | 0x001fe0a0 | Actions |
| 28 | VAR_BYTE | 0x016fb900 | DEVICE_INFO | 0x002c48b0 | Display |
| 29 | VAR_BYTE | 0x016fb8f0 | MESSAGE_PUBLIC | 0x003a4890 | Chat |
| 30 | 3 | 0x016fb8e0 | OPOBJ10 | (via OPOBJ switch) | Actions |
| 31 | VAR_SHORT | 0x016fb8d0 | EVENT_CAMERA_POSITION_2 | (camera event variant) | Misc |
| 32 | VAR_BYTE | 0x016fb8c0 | CS2_CALLBACK | 0x003cd910 | Misc |
| 33 | 5 | 0x016fb8b0 | MOVE_GAME (from minimenu) | 0x001fe300 | Movement |
| 34 | 5 | 0x016e96f0 | QUEUED_PACKET | 0x00378630 | Misc | `[946-5]` size 0→5 |
| 35 | 3 | 0x016fb890 | OPNPC_T5 | SendOpNpc5T | Actions |
| 36 | 3 | 0x016fb880 | OPNPC_T6 | SendOpNpc6T | Actions |
| 37 | 15 | 0x016fb870 | OPLOC_T (extended form) | 0x001fe840 | Actions |
| 38 | 9 | 0x016fb860 | OPLOC_T1 | SendOpLoc1T | Actions |
| 39 | VAR_BYTE | 0x016fb850 | SOCIAL_REQUEST | 0x003cda50 | Social |
| 40 | 9 | 0x016fb840 | OPLOC_T3 | SendOpLoc3T | Actions |
| 41 | 9 | 0x016e97b0 | *(unknown)* | | | `[946-5]` size 0→9 |
| 42 | 3 | 0x016fb820 | UNKNOWN_3BYTE_42 | 0x003ce0b0 | Misc |
| 43 | 9 | 0x016fb810 | OPLOC_T6 | SendOpLoc6T | Actions |
| 44 | VAR_SHORT | 0x016fb800 | *(unknown)* | | |
| 45 | VAR_SHORT | 0x016fb7f0 | EVENT_TELEMETRY | 0x002c48b0 | Display |
| 46 | 3 | 0x016fb7e0 | OPOBJ2 | (via OPOBJ switch) | Actions |
| 47 | 8 | 0x016fb7d0 | IF_BUTTON10 | IfButtonXInner (table[9]) | Interface |
| 48 | VAR_BYTE | 0x016fb7c0 | EVENT_KEYBOARD | 0x00281c20 (CS2 getter) | Misc |
| 49 | VAR_SHORT | 0x016fb7b0 | DATA_REPORT_VARSHORT | 0x0036b520 | Misc |
| 50 | 4 | 0x016fb7a0 | SCENE_GRAPH_REPORT | 0x002c2fc0 | Display |
| 51 | 4 | 0x016fb790 | CAMERA_ANGLE | 0x002c48b0 | Display |
| 52 | VAR_SHORT | 0x016fb780 | MESSAGE_PRIVATE | 0x003a7260 | Chat |
| 53 | 9 | 0x016fb770 | *(unknown)* | | |
| 54 | 8 | 0x016fb760 | IF_BUTTON3 | IfButtonXInner (table[2]) | Interface |
| 55 | 1 | 0x016fb750 | *(unknown)* | | |
| 56 | VAR_BYTE | 0x016fb740 | RESUME_NAMEDIALOG | 0x00360610 | Interface |
| 57 | 9 | 0x016fb730 | *(unknown)* | | |
| 58 | 17 | 0x016fb720 | OPPLAYER_T (extended) | 0x001feff0 | Actions |
| 59 | 3 | 0x016fb710 | OPOBJ8 | (via OPOBJ switch) | Actions |
| 60 | 3 | 0x016fb700 | OPOBJ6 | (via OPOBJ switch) | Actions |
| 61 | 8 | 0x016fb6f0 | IF_BUTTON7 | IfButtonXInner (table[6]) | Interface |
| 62 | VAR_BYTE | 0x016fb6e0 | ENCODEDSTRING_SEND (cs2) | 0x003f2e10 | Misc |
| 63 | 8 | 0x016fb6d0 | IF_BUTTON9 | IfButtonXInner (table[8]) | Interface |
| 64 | 8 | 0x016fb6c0 | IF_BUTTON6 | IfButtonXInner (table[5]) | Interface |
| 65 | 4 | 0x016fb6b0 | SCENE_INTERACTION | 0x002d34d0 | Misc |
| 66 | 4 | 0x016fb6a0 | EVENT_APPLET_FOCUS_2 | 0x0032f260 | Misc |
| 67 | VAR_BYTE | 0x016fb690 | CLAN_JOINCHAT (cs2) | 0x00361310 | Chat |
| 68 | 9 | 0x016fb680 | OPLOC2_T | SendOpLoc2T | Actions |
| 69 | 3 | 0x016fb670 | OPNPC4_T | SendOpNpc4T | Actions |
| 70 | 4 | 0x016fb660 | OPLOC1 (walk here) | 0x001da9e0 | Actions |
| 71 | VAR_BYTE | 0x016fb650 | ACTIVE_CHAT_PHRASE_SEND | 0x0036a340 (CS2) | Chat |
| 72 | 1 | 0x016fb640 | *(unknown — no sender)* | | |
| 73 | 18 | 0x016fb630 | *(unknown — no sender)* | | |
| 74 | VAR_SHORT | 0x016fb620 | ENCRYPTED_STRING_SEND2 | 0x00377b60 | Misc |
| 75 | VAR_SHORT | 0x016fb610 | IF_BUTTON_TARGETMENU | 0x003ff000 | Interface |
| 76 | 2 | 0x016fb600 | SOUND_SONGEND (cs2) | 0x00361230 | Misc |
| 77 | 7 | 0x016fb5f0 | OPNPC5 | (via OPNPC switch) | Actions |
| 78 | VAR_BYTE | 0x016fb5e0 | FRIENDLIST_ADD | 0x003d3f80 | Social |
| 79 | 1 | 0x016fb5d0 | *(unknown — no sender)* | | |
| 80 | 1 | 0x016e9ac0 | NO_TIMEOUT_2 | ProcessConnections | Misc | `[946-5]` size 0→1 |
| 81 | VAR_BYTE | 0x016fb5b0 | ACTIVE_CHAT_PHRASE_SENDPRIVATE | 0x00369e70 (CS2) | Chat |
| 82 | 3 | 0x016fb5a0 | WINDOW_STATUS | 0x00360b70 | Misc |
| 83 | 1 | 0x016fb590 | FOCUS_CHANGED | 0x002c48b0 | Display |
| 84 | VAR_BYTE | 0x016fb580 | OPOBJ_CS2_2 | 0x0040b590 | Actions |
| 85 | 7 | 0x016fb570 | EVENT_MOUSE_MOVE | 0x0021ea40 | Misc |
| 86 | VAR_BYTE | 0x016fb560 | MESSAGE_CLAN_CHAT | 0x003e6420 | Chat |
| 87 | VAR_BYTE | 0x016e9b30 | CLOSE_MODAL | 0x00360e90 | Interface | `[946-5]` size 0→var_byte |
| 88 | 2 | 0x016fb540 | SOUND_SONGSELECT (cs2) | 0x00361040 | Misc |
| 89 | 5 | 0x016fb530 | MOVE_SCRIPTED | 0x0040cfc0 (CS2) | Movement |
| 90 | 7 | 0x016fb520 | OPNPC4 | (via OPNPC switch) | Actions |
| 91 | 3 | 0x016fb510 | OPOBJ9 | (via OPOBJ switch) | Actions |
| 92 | 18 | 0x016fb500 | MOVE_GAME (extended form) | 0x001fe300 | Movement |
| 93 | VAR_BYTE | 0x016fb4f0 | CLAN_LEAVECHAT (cs2) | 0x00361120 | Chat |
| 94 | VAR_BYTE | 0x016fb4e0 | OPLOC_CS2 | 0x00361910 | Actions |
| 95 | 4 | 0x016fb4d0 | DETECT_MODIFIED_CLIENT | 0x00314d70 | Misc |
| 96 | 3 | 0x016fb4c0 | OPOBJ4 | (via OPOBJ switch) | Actions |
| 97 | 8 | 0x016fb4b0 | IF_BUTTON1 | IfButtonXInner (table[0]) | Interface |
| 98 | VAR_BYTE | 0x016fb4a0 | OPOBJ_CS2 | 0x00360750 | Actions |
| 99 | 2 | 0x016fb490 | AFFINEDTRANSFORM_SET (cs2) | 0x00360a80 | Misc |
| 100 | 4 | 0x016fb480 | *(unknown — no sender)* | | |
| 101 | 9 | 0x016fb470 | OPLOC4_T | SendOpLoc4T | Actions |
| 102 | VAR_SHORT | 0x016fb460 | MOVE_GAME | 0x003d68a0 | Movement |
| 103 | 7 | 0x016fb450 | OPNPC6 | (via OPNPC switch) | Actions |
| 104 | VAR_BYTE | 0x016fb440 | OPNPC_CS2 | 0x003f4810 | Actions |
| 105 | 11 | 0x016fb430 | OPOBJ_T | 0x001fe630 | Actions |
| 106 | 6 | 0x016fb420 | DISPLAY_INFO | 0x002c46e0 | Display |
| 107 | VAR_BYTE | 0x016fb410 | IF_BUTTONT | IfButtonXInner | Interface |
| 108 | VAR_SHORT | 0x016fb400 | *(unknown — no sender)* | | |
| 109 | VAR_BYTE | 0x016fb3f0 | IGNORELIST_ADD | 0x003afbc0 | Social |
| 110 | 4 | 0x016fb3e0 | WORLDLIST_FETCH | 0x00234580 | Misc |
| 111 | 9 | 0x016fb3d0 | OPLOC5_T | SendOpLoc5T | Actions |
| 112 | VAR_BYTE | 0x016fb3c0 | *(unknown — no sender)* | | |
| 113 | 4 | 0x016fb3b0 | RENDER_REPORT | 0x00299fa0 | Display |
| 114 | 22 | 0x016fb3a0 | IF_BUTTON_TARGETMENU_SEND | 0x003ff770 | Interface |
| 115 | 3 | 0x016fb390 | OPOBJ3 | (via OPOBJ switch) | Actions |
| 116 | VAR_BYTE | 0x016fb380 | ENCRYPTED_STRING_SEND | 0x003779c0 | Misc |
| 117 | 6 | 0x016fb370 | CLOSE_MODAL_COMPONENT | 0x003fab50 | Interface |
| 118 | 8 | 0x016fb360 | IF_BUTTON2 | IfButtonXInner (table[1]) | Interface |
| 119 | VAR_BYTE | 0x016fb350 | OPPLAYER_CS2 | 0x00360900 | Actions |
| 120 | 11 | 0x016f3660 | OPPLAYER_T | 0x00bb2b50 | Actions |
| 121 | VAR_BYTE | 0x016fb340 | FRIENDLIST_DEL | 0x003d4750 | Social |
| 122 | 16 | 0x016fb330 | INTERFACE_INTERACTION | 0x00267620 | Interface |
| 123 | 1 | 0x016fb320 | BUG_REPORT (cs2) | 0x00360d90 | Misc |
| 124 | 8 | 0x016fb310 | IF_BUTTON8 | IfButtonXInner (table[7]) | Interface |
| 125 | 3 | 0x016fb300 | OPNPC3_T | SendOpNpc3T | Actions |
| 126 | VAR_BYTE | 0x016fb2f0 | *(unknown — no sender)* | | |
| 127 | VAR_BYTE | 0x016fb2e0 | ENCODEDSTRING_SEND2 (cs2) | 0x003f30d0 | Misc |
| 128 | 8 | 0x016fb2d0 | IF_BUTTON4 | IfButtonXInner (table[3]) | Interface |
| 129 | 8 | 0x016fb2c0 | STRTOLL_SEND (cs2) | 0x0037a580 | Misc |

## IF_BUTTON Pointer Table

The IF_BUTTON opcodes (1-10) are dispatched via a pointer table at `0x014908a0`.
Each entry is a pointer to a ClientProt object. The table is indexed by `(buttonOp - 1)`.

IfButtonXInner (`0x003fdd10`) handles two cases:
1. **No target string**: Sends `IF_BUTTON{N}` (opcode from table) with component hash (ushort), slot (2 bytes), opValue (int)
2. **With target string**: Sends `IF_BUTTONT` (opcode 107) with string data, button op, slot, opValue, and target name

## Statistics

| Category | Count |
|----------|-------|
| Total opcodes | 130 |
| Fixed size (> 0) | 83 |
| Zero size (no payload) | 6 |
| VAR_BYTE | 28 |
| VAR_SHORT | 13 |
| Identified by name | 113 |
| Unidentified (no sender found) | 17 |

## Notes

- Opcodes 0-3 have duplicate objects at 0x016fb1e0-0x016fb240 (initialized directly without constructor), likely used as sentinel/template values
- The ServerProt entries use a different constructor at 0x00182570 and are registered in a separate section of the same RegisterAll function
- ClientProt objects are 0x10 bytes: {opcode:int32, size:int32, name:eastl::string(SSO, always empty)}
- The dispatch vector at 0x016fbde0 is an `eastl::fixed_vector<ClientProt const*, 122>` but actually holds 130 entries
