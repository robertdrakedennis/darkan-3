# ClientProt Opcode Table (rev 947-1)

Binary: NXT Client (Build 947-1)
Extracted from: `jag::ServerProt::RegisterAll` at `0x00181e10` (947-1)
Constructor: `0x00181a20` (ClientProt), `0x00181b80` (ClientProt::InitEntry for lobby entries)
ClientProt struct size: 0x10 bytes (opcode:int32+0x00, size:int32+0x04, name:char*+0x08)
Entry base addresses: `0x016ed590` to `0x016edd80` (0x10 stride), outlier at `0x016f1020` (op 112)
Total game opcodes: 130 (0-129)
Total lobby opcodes: 18 (0-17)

Cross-referenced against: 946-5 binary (`clientprot-table.md`)

## Size Mode Legend

| Mode | Value | Description |
|------|-------|-------------|
| Fixed | >= 0 | Packet has exactly N bytes of payload |
| VAR_BYTE | -1 | Length prefixed with 1 byte (max 255 bytes payload) |
| VAR_SHORT | -2 | Length prefixed with 2 bytes (max 65535 bytes payload) |
| 0 | 0 | No payload bytes (opcode-only packet) |

## Game ClientProt Table (130 entries)

Confidence levels:
- **CONFIRMED**: Verified by decompiling the Send* function in 947-1 and checking which DAT_ address it references
- **HIGH**: Unique remaining size match after confirmed entries removed
- **MEDIUM**: Size count match (equal number of entries with that size in both revisions)
- **LOW**: Multiple candidates with same size, name is a guess
- **NEW**: Size does not appear in 946 table, likely a changed/new packet

| 947 Opcode | Size | DAT Address | Identified Name | 946 Opcode | Confidence | Notes |
|------------|------|-------------|-----------------|------------|------------|-------|
| 0 | 5 | 0x016edd80 | MOVE_GAME_MINIMENU | 33 | MEDIUM | size count match |
| 1 | 3 | 0x016edd70 | | | LOW | 3-byte group (NPC/OBJ ops) |
| 2 | 4 | 0x016edd60 | | | LOW | 4-byte group (LOC/callback ops) |
| 3 | 5 | 0x016edd50 | MOVE_SCRIPTED | 89 | MEDIUM | size count match |
| 4 | 8 | 0x016edd40 | **IF_BUTTON3** | | **CONFIRMED** | IfButtonXInner table[2]; format: short(comp) int(slot) short(item) |
| 5 | 8 | 0x016edd30 | **IF_BUTTON7** | | **CONFIRMED** | IfButtonXInner table[6]; format: short(comp) int(slot) short(item) |
| 6 | 15 | 0x016edd20 | OPNPC_T_EXTENDED | 26 | MEDIUM | size count match |
| 7 | 6 | 0x016edd10 | **EVENT_MOUSE_CLICK** | | **CONFIRMED** | SendEventMouseClick @ 0x002e05d0; writes int(y<<16|x) + byteAdd(timeDelta) + byte(flags) |
| 8 | 4 | 0x016edd00 | | | LOW | 4-byte group |
| 9 | 12 | 0x016edcf0 | | | LOW | 12-byte group (IF_BUTTON_T4/T9) |
| 10 | 9 | 0x016edce0 | | | LOW | 9-byte group |
| 11 | 9 | 0x016edcd0 | | | NEW | extra 9-byte entry |
| 12 | VAR_BYTE | 0x016edcc0 | | | LOW | varByte group |
| 13 | 16 | 0x016edcb0 | | | LOW | 16-byte group (IF_BUTTON_T) |
| 14 | VAR_SHORT | 0x016edca0 | | | LOW | varShort group |
| 15 | 6 | 0x016edc90 | | | NEW | new size |
| 16 | 8 | 0x016edc80 | | | LOW | 8-byte group |
| 17 | 7 | 0x016edc70 | | | LOW | 7-byte group |
| 18 | 8 | 0x016edc60 | **IF_BUTTON9** | | **CONFIRMED** | IfButtonXInner table[8]; format: short(comp) int(slot) short(item) |
| 19 | VAR_BYTE | 0x016edc50 | | | LOW | varByte group |
| 20 | 1 | 0x016edc40 | | | LOW | 1-byte group |
| 21 | 8 | 0x016edc30 | **IF_BUTTON8** | | **CONFIRMED** | IfButtonXInner table[7]; format: short(comp) int(slot) short(item) |
| 22 | VAR_BYTE | 0x016edc20 | **FRIENDLIST_DEL** | 121 | **CONFIRMED** | SendFriendlistDel @ 0x003dcba0 |
| 23 | 4 | 0x016edc10 | **WORLDLIST_FETCH** | 110 | **CONFIRMED** | SendWorldlistFetch @ 0x0023f2a0 |
| 24 | VAR_BYTE | 0x016edc00 | **CLANCHANNEL_KICKUSER** | 78 | **CONFIRMED** | SendSocialRequest @ 0x003dc8a0 |
| 25 | VAR_BYTE | 0x016edbf0 | | | LOW | varByte group |
| 26 | VAR_BYTE | 0x016edbe0 | | | LOW | varByte group |
| 27 | 0 | 0x016edbd0 | **NO_TIMEOUT** | | **CONFIRMED** | ProcessConnections @ 0x001de850; keepalive sent every 50 ticks on login+game connections |
| 28 | 18 | 0x016edbc0 | MOVE_GAME_EXTENDED | 92 | MEDIUM | unique size match |
| 29 | 8 | 0x016edbb0 | **IF_BUTTON5** | | **CONFIRMED** | IfButtonXInner table[4]; format: short(comp) int(slot) short(item) |
| 30 | 4 | 0x016edba0 | **TRANSMITVAR_VERIFYID** | | **CONFIRMED** | SendSceneGraphReport @ 0x00320ee5; writes int(verifyId) |
| 31 | VAR_BYTE | 0x016edb90 | | | LOW | varByte group |
| 32 | 4 | 0x016edb80 | | | LOW | 4-byte group |
| 33 | 3 | 0x016edb70 | | | LOW | 3-byte group |
| 34 | VAR_BYTE | 0x016edb60 | **CLIENT_DETAILOPTIONS_STATUS** | | **CONFIRMED** | SendMultiDisplayPackets @ 0x00321e30; SerialiseForServer graphics settings (1B count + data) |
| 35 | VAR_BYTE | 0x016edb50 | | | LOW | varByte group |
| 36 | 8 | 0x016edb40 | **IF_BUTTON10** | | **CONFIRMED** | IfButtonXInner table[9]; format: short(comp) int(slot) short(item) |
| 37 | 1 | 0x016edb30 | | | LOW | 1-byte group |
| 38 | 1 | 0x016edb20 | | | LOW | 1-byte group |
| 39 | VAR_SHORT | 0x016edb10 | | | LOW | varShort group |
| 40 | 16 | 0x016edb00 | | | LOW | 16-byte group (IF_BUTTON_T) |
| 41 | 0 | 0x016edaf0 | | | LOW | 0-byte group |
| 42 | VAR_BYTE | 0x016edae0 | | | LOW | varByte group |
| 43 | 0 | 0x016edad0 | | | LOW | 0-byte group |
| 44 | 7 | 0x016edac0 | | | LOW | 7-byte group |
| 45 | 9 | 0x016edab0 | | | NEW | extra 9-byte entry |
| 46 | 3 | 0x016edaa0 | | | LOW | 3-byte group |
| 47 | 4 | 0x016eda90 | **EVENT_CAMERA_POSITION** | | **CONFIRMED** | SendMultiDisplayPackets @ 0x00321e30; writes shortAdd(yaw) + short(pitch) via QuaternionToJagexAngles |
| 48 | VAR_BYTE | 0x016eda80 | **IGNORELIST_ADD** | 67 | **CONFIRMED** | SendIgnorelistAdd @ 0x003b6a40 |
| 49 | 17 | 0x016eda70 | | | NEW | new size |
| 50 | 3 | 0x016eda60 | | | LOW | 3-byte group |
| 51 | 8 | 0x016eda50 | **IF_BUTTON6** | | **CONFIRMED** | IfButtonXInner table[5]; format: short(comp) int(slot) short(item) |
| 52 | 1 | 0x016eda40 | | | LOW | 1-byte group |
| 53 | VAR_SHORT | 0x016eda30 | **EVENT_KEYBOARD** | | **CONFIRMED** | SendMultiDisplayPackets @ 0x00321e30; writes short(count) + entries of [byte(key) byte(deltaHi) byte(deltaMid) byte(deltaLo)] |
| 54 | 3 | 0x016eda20 | | | LOW | 3-byte group |
| 55 | 0 | 0x016eda10 | **MAP_BUILD_COMPLETE** | 21 | **CONFIRMED** | SendMapBuildComplete @ 0x00335aa0 |
| 56 | 1 | 0x016eda00 | **EVENT_APPLET_FOCUS** | | **CONFIRMED** | SendMultiDisplayPackets @ 0x00321e30; writes byte(hasFocus) via SDL_GetKeyboardFocus |
| 57 | 1 | 0x016ed9f0 | | | LOW | 1-byte group |
| 58 | 15 | 0x016ed9e0 | OPLOC_T_EXTENDED | 51 | MEDIUM | size count match |
| 59 | 4 | 0x016ed9d0 | **DETECT_MODIFIED_CLIENT** | 95 | **CONFIRMED** | SendDetectModifiedClient @ 0x002992e0 |
| 60 | 3 | 0x016ed9c0 | | | LOW | 3-byte group |
| 61 | 4 | 0x016ed9b0 | | | LOW | 4-byte group |
| 62 | 2 | 0x016ed9a0 | | | LOW | 2-byte group |
| 63 | 4 | 0x016ed990 | | | LOW | 4-byte group |
| 64 | VAR_BYTE | 0x016ed980 | | | LOW | varByte group |
| 65 | VAR_BYTE | 0x016ed970 | | | LOW | varByte group |
| 66 | VAR_BYTE | 0x016ed960 | | | LOW | varByte group |
| 67 | 7 | 0x016ed950 | | | LOW | 7-byte group |
| 68 | VAR_SHORT | 0x016ed940 | | | LOW | varShort group |
| 69 | VAR_BYTE | 0x016ed930 | **MESSAGE_PUBLIC (effects)** | 29 | **CONFIRMED** | SendMessagePublicWithEffects @ 0x003ac7a0 |
| 70 | VAR_SHORT | 0x016ed920 | | | LOW | varShort group |
| 71 | 0 | 0x016ed910 | | | LOW | 0-byte group |
| 72 | 9 | 0x016ed900 | | | NEW | extra 9-byte entry |
| 73 | 9 | 0x016ed8f0 | | | NEW | extra 9-byte entry |
| 74 | VAR_SHORT | 0x016ed8e0 | **EVENT_TELEMETRY** | 45 | **CONFIRMED** | SendAppletFocusEvents @ 0x0023f7a0 |
| 75 | VAR_BYTE | 0x016ed8d0 | | | LOW | varByte group |
| 76 | VAR_SHORT | 0x016ed8c0 | | | LOW | varShort group |
| 77 | 8 | 0x016ed8b0 | **IF_BUTTON2** | | **CONFIRMED** | IfButtonXInner table[1]; format: short(comp) int(slot) short(item) |
| 78 | VAR_SHORT | 0x016ed8a0 | **MOVE_GAME** | 102 | **CONFIRMED** | SendMoveGame @ 0x003e9b90 |
| 79 | 9 | 0x016ed890 | | | NEW | extra 9-byte entry |
| 80 | VAR_BYTE | 0x016ed880 | | | LOW | varByte group |
| 81 | 11 | 0x016ed870 | **OPLOC_T2** | 70 | **CONFIRMED** | SendOpLocTLong @ 0x002047b0 |
| 82 | 4 | 0x016ed860 | | | LOW | 4-byte group |
| 83 | 18 | 0x016ed850 | | | NEW | extra 18-byte entry |
| 84 | VAR_BYTE | 0x016ed840 | **RESUME_P_NAMEDIALOG** | | **CONFIRMED** | SendResumePNameDialog @ 0x00370340; writes byte(charCount) + pStringUTF8ToCP1252(text) |
| 85 | 11 | 0x016ed830 | OPNPC_T2_EXTENDED | 90 | MEDIUM | size count match |
| 86 | VAR_BYTE | 0x016ed820 | | | LOW | varByte group |
| 87 | VAR_BYTE | 0x016ed810 | | | LOW | varByte group |
| 88 | 3 | 0x016ed800 | | | LOW | 3-byte group |
| 89 | 3 | 0x016ed7f0 | | | LOW | 3-byte group |
| 90 | VAR_SHORT | 0x016ed7e0 | | | LOW | varShort group |
| 91 | 2 | 0x016ed7d0 | | | LOW | 2-byte group |
| 92 | 22 | 0x016ed7c0 | | | NEW | new size |
| 93 | VAR_BYTE | 0x016ed7b0 | **FRIENDLIST_ADD** | 39 | **CONFIRMED** | SendFriendlistAdd @ 0x003e2cd0 |
| 94 | VAR_SHORT | 0x016ed7a0 | | | LOW | varShort group |
| 95 | 8 | 0x016ed790 | **IF_BUTTON4** | | **CONFIRMED** | IfButtonXInner table[3]; format: short(comp) int(slot) short(item) |
| 96 | 8 | 0x016ed780 | **IF_BUTTON1** | | **CONFIRMED** | IfButtonXInner table[0]; format: short(comp) int(slot) short(item) |
| 97 | 9 | 0x016ed770 | **ANTI_CHEAT_REPLY** | | **CONFIRMED** | HandleAntiCheatChallenge @ 0x0021ba20 (ServerProt handler sends this); writes byte(~sessionIdx) + 4B(challenge1_rearranged) + 4B(challenge2_rearranged) |
| 98 | 7 | 0x016ed760 | | | LOW | 7-byte group |
| 99 | 2 | 0x016ed750 | | | LOW | 2-byte group |
| 100 | VAR_SHORT | 0x016ed740 | | | LOW | varShort group |
| 101 | 2 | 0x016ed730 | | | LOW | 2-byte group |
| 102 | 7 | 0x016ed720 | | | LOW | 7-byte group |
| 103 | VAR_BYTE | 0x016ed710 | | | LOW | varByte group |
| 104 | 3 | 0x016ed700 | | | LOW | 3-byte group |
| 105 | VAR_BYTE | 0x016ed6f0 | **EVENT_MOUSE_MOVE** | | **CONFIRMED** | SendCameraMovementUpdate @ 0x00247000 (via vtable); delta-encoded mouse/camera positions with timing |
| 106 | 4 | 0x016ed6e0 | | | LOW | 4-byte group |
| 107 | 3 | 0x016ed6d0 | | | LOW | 3-byte group |
| 108 | 6 | 0x016ed6c0 | **WINDOW_STATUS** | 82 | **CONFIRMED** | SendDisplayInfo @ 0x00321c60 (size 3->6) |
| 109 | 9 | 0x016ed6b0 | | | NEW | extra 9-byte entry |
| 110 | 3 | 0x016ed6a0 | | | LOW | 3-byte group |
| 111 | VAR_BYTE | 0x016ed690 | | | LOW | varByte group |
| 112 | 11 | 0x016f1020 | OPLOC_T3 | 111 | MEDIUM | size count match |
| 113 | 3 | 0x016ed680 | | | LOW | 3-byte group |
| 114 | 3 | 0x016ed670 | | | LOW | 3-byte group |
| 115 | 3 | 0x016ed660 | | | LOW | 3-byte group |
| 116 | VAR_SHORT | 0x016ed650 | | | LOW | varShort group |
| 117 | VAR_BYTE | 0x016ed640 | | | LOW | varByte group |
| 118 | 7 | 0x016ed630 | | | LOW | 7-byte group |
| 119 | VAR_BYTE | 0x016ed620 | | | LOW | varByte group |
| 120 | VAR_BYTE | 0x016ed610 | **MESSAGE_PUBLIC** | 29 | **CONFIRMED** | SendMessagePublic @ 0x003ab590 |
| 121 | VAR_SHORT | 0x016ed600 | **MESSAGE_PRIVATE** | 52 | **CONFIRMED** | SendMessagePrivate @ 0x003adf60 |
| 122 | 7 | 0x016ed5f0 | | | LOW | 7-byte group |
| 123 | 3 | 0x016ed5e0 | | | LOW | 3-byte group |
| 124 | VAR_BYTE | 0x016ed5d0 | **IF_BUTTON_D** | | **CONFIRMED** | IfButtonXInner extended path; writes byte(type+8) byte(len) byteAdd(opIndex) int_alt1(slot) string(data) short(item) |
| 125 | 3 | 0x016ed5c0 | | | LOW | 3-byte group |
| 126 | 9 | 0x016ed5b0 | | | NEW | extra 9-byte entry |
| 127 | 3 | 0x016ed5a0 | | | LOW | 3-byte group |
| 128 | 0 | 0x016ed590 | | | LOW | 0-byte group |
| 129 | 3 | 0x016ed580 | | | LOW | 3-byte group |

## Lobby ClientProt Table (18 entries)

Registered via `ClientProt::InitEntry` into the lobby-phase client prot vector.

| 947 Opcode | Size | DAT Address | 946 Opcode | Notes |
|------------|------|-------------|------------|-------|
| 0 | 21 | 0x017029e0 | 15 | unique size match |
| 1 | 11 | 0x017026a0 | 0 or 8 | ambiguous |
| 2 | 7 | 0x01702760 | 5, 7, or 17 | ambiguous |
| 3 | 5 | 0x017028e0 | 2 or 13 | ambiguous |
| 4 | 11 | 0x01702960 | 0 or 8 | ambiguous |
| 5 | 3 | 0x01702860 | 3 | unique size match |
| 6 | 20 | 0x01702a60 | 6 | unique size match |
| 7 | 29 | 0x017024a0 | 12 | unique size match |
| 8 | 5 | 0x01702620 | 2 or 13 | ambiguous |
| 9 | VAR_BYTE | 0x017025a0 | 1, 14, or 16 | ambiguous |
| 10 | 10 | 0x017026e0 | 10 | unique size match |
| 11 | 7 | 0x01702ae0 | 5, 7, or 17 | ambiguous |
| 12 | 7 | 0x017027e0 | 5, 7, or 17 | ambiguous |
| 13 | 2 | 0x01702b60 | 9 | unique size match |
| 14 | VAR_BYTE | 0x01702be0 | 1, 14, or 16 | ambiguous |
| 15 | 28 | 0x01702520 | 11 | unique size match |
| 16 | VAR_BYTE | 0x01702c60 | 1, 14, or 16 | ambiguous |
| 17 | 14 | 0x01702420 | 4 | unique size match |

## Key Confirmed Packets for Server Implementation

These are the packets critical for lobby/world functionality, all verified in the 947-1 binary:

| Packet | 947 Opcode | Size | Send Function | Purpose |
|--------|------------|------|---------------|---------|
| WORLDLIST_FETCH | 23 | 4 | 0x0023f2a0 | Client requests world list (writes 1 int) |
| MAP_BUILD_COMPLETE | 55 | 0 | 0x00335aa0 | Client signals map load complete |
| MOVE_GAME | 78 | VAR_SHORT | 0x003e9b90 | Player movement (walk/run) |
| WINDOW_STATUS | 108 | 6 | 0x00321c60 | Display mode + resolution (expanded from 3 to 6 bytes) |
| MESSAGE_PUBLIC | 120 | VAR_BYTE | 0x003ab590 | Public chat message |
| MESSAGE_PRIVATE | 121 | VAR_SHORT | 0x003adf60 | Private message (XTEA encrypted) |
| FRIENDLIST_ADD | 93 | VAR_BYTE | 0x003e2cd0 | Add friend |
| FRIENDLIST_DEL | 22 | VAR_BYTE | 0x003dcba0 | Remove friend |
| IGNORELIST_ADD | 48 | VAR_BYTE | 0x003b6a40 | Add to ignore list |
| CLANCHANNEL_KICKUSER | 24 | VAR_BYTE | 0x003dc8a0 | Kick from friend/clan channel |
| DETECT_MODIFIED_CLIENT | 59 | 4 | 0x002992e0 | Anti-cheat check (writes 1 int) |
| EVENT_TELEMETRY | 74 | VAR_SHORT | 0x0023f7a0 | Applet focus/telemetry events |
| MOVE_GAME_EXTENDED | 28 | 18 | - | Extended movement packet |
| OPLOC_T2 | 81 | 11 | 0x002047b0 | Use item on location (long form) |
| NO_TIMEOUT | 27 | 0 | 0x001de850 (ProcessConnections) | Keepalive; sent every 50 ticks on both login+game connections |
| IF_BUTTON1 | 96 | 8 | 0x00404b46 (IfButtonXInner) | Interface button click (op 1); short(comp) int(slot) short(item) |
| IF_BUTTON2 | 77 | 8 | " | Interface button click (op 2) |
| IF_BUTTON3 | 4 | 8 | " | Interface button click (op 3) |
| IF_BUTTON4 | 95 | 8 | " | Interface button click (op 4) |
| IF_BUTTON5 | 29 | 8 | " | Interface button click (op 5) |
| IF_BUTTON6 | 51 | 8 | " | Interface button click (op 6) |
| IF_BUTTON7 | 5 | 8 | " | Interface button click (op 7) |
| IF_BUTTON8 | 21 | 8 | " | Interface button click (op 8) |
| IF_BUTTON9 | 18 | 8 | " | Interface button click (op 9) |
| IF_BUTTON10 | 36 | 8 | " | Interface button click (op 10) |
| IF_BUTTON_D | 124 | VAR_BYTE | " | Extended button with dialog string data |
| RESUME_P_NAMEDIALOG | 84 | VAR_BYTE | 0x00370340 | Name dialog resume; byte(charCount) + string(text) |
| EVENT_MOUSE_CLICK | 7 | 6 | 0x002e05d0 | Mouse click; int(y<<16|x) byteAdd(timeDelta) byte(flags) |
| EVENT_MOUSE_MOVE | 105 | VAR_BYTE | 0x00247000 (SendCameraMovementUpdate) | Delta-encoded mouse/camera positions |
| EVENT_KEYBOARD | 53 | VAR_SHORT | 0x00321e30 (SendMultiDisplayPackets) | Key events; short(count) + entries |
| EVENT_CAMERA_POSITION | 47 | 4 | " | Camera pitch/yaw; shortAdd(yaw) short(pitch) |
| EVENT_APPLET_FOCUS | 56 | 1 | " | Window focus state; byte(hasFocus) |
| CLIENT_DETAILOPTIONS_STATUS | 34 | VAR_BYTE | " | Graphics settings; byte(count) + serialised data |
| TRANSMITVAR_VERIFYID | 30 | 4 | 0x00320ee5 (SendSceneGraphReport) | Var domain verify ID; int(verifyId) |
| ANTI_CHEAT_REPLY | 97 | 9 | 0x0021ba20 (handler) | Anti-cheat challenge response; byte(~idx) + 8B rearranged challenge |

## Size Distribution (947 vs 946)

| Size | 946 Count | 947 Count | Delta |
|------|-----------|-----------|-------|
| VAR_SHORT | 5 | 13 | +8 |
| VAR_BYTE | 16 | 28 | +12 |
| 0 | 36 | 6 | -30 |
| 1 | 5 | 6 | +1 |
| 2 | 2 | 4 | +2 |
| 3 | 22 | 18 | -4 |
| 4 | 21 | 11 | -10 |
| 5 | 2 | 2 | 0 |
| 6 | 0 | 3 | +3 |
| 7 | 2 | 7 | +5 |
| 8 | 7 | 11 | +4 |
| 9 | 1 | 9 | +8 |
| 11 | 2 | 3 | +1 |
| 12 | 2 | 1 | -1 |
| 15 | 2 | 2 | 0 |
| 16 | 3 | 2 | -1 |
| 17 | 0 | 1 | +1 |
| 18 | 1 | 2 | +1 |
| 22 | 0 | 1 | +1 |

**Major structural changes 946 -> 947:**
- 30 zero-size packets eliminated (most gained payload)
- 10 four-byte packets removed/changed
- 12 new varByte packets, 8 new varShort packets
- Many formerly fixed-size packets became variable-length
- Several packet sizes increased (e.g., WINDOW_STATUS 3->6)

This means the 947 protocol is NOT a simple opcode shuffle from 946 -- many packet structures were expanded or changed. Pure size-based cross-referencing is insufficient for most entries. Full identification requires decompiling each Send* function or handler.

## RE Methodology

1. Decompiled `jag::ServerProt::RegisterAll` at `0x00181e10` in 947-1 binary (port 8083)
2. Extracted all 130 `ClientProt::ClientProt(&addr, opcode, size)` calls
3. Extracted all 18 `ClientProt::InitEntry(&addr, opcode, size)` calls for lobby
4. Decompiled all available `jag::ClientProt::Send*` functions in 947-1:
   - SendWorldlistFetch, SendMapBuildComplete, SendMoveGame, SendDetectModifiedClient
   - SendFriendlistAdd, SendFriendlistDel, SendIgnorelistAdd, SendSocialRequest
   - SendMessagePublic, SendMessagePublicWithEffects, SendMessagePrivate
   - SendAppletFocusEvents, SendDisplayInfo, SendOpLocTLong, SendSceneGraphReport
   - SendMultiDisplayPackets, SendCameraMovementUpdate, SendEventMouseClick
   - SendResumePNameDialog, HandleAntiCheatChallenge, ProcessConnections
   - IfButtonXInner (IF_BUTTON1-10 table at 0x0149aba0)
5. Each Send function references a DAT_ address as the ClientProt object, which maps to exactly one opcode/size pair
6. Cross-referenced the DAT_ addresses from Send functions with the opcode table to produce confirmed mappings
7. Cross-referenced with unstripped binary (NXT_BETA_UNSTRIPPED, port 8080) to obtain real Jagex names:
   - ClientWatch::MainLogic contains EVENT_MOUSE_CLICK, EVENT_MOUSE_MOVE, EVENT_KEYBOARD, EVENT_CAMERA_POSITION, EVENT_APPLET_FOCUS, CLIENT_DETAILOPTIONS_STATUS
   - InterfaceManager::SendPauseComponentMessage references RESUME_PAUSEBUTTON
   - opcode::Resume lambda #4 references RESUME_P_NAMEDIALOG
   - ConnectionManager::ProcessConnections sends NO_TIMEOUT (keepalive) every 50 ticks
   - DelayedStateChange::MainLogic sends TRANSMITVAR_VERIFYID
   - ClientVarDomain::Service sends STORE_SERVERPERM_VARCS
