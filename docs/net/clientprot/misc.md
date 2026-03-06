# ClientProt: Miscellaneous Packets

All client-to-server packets not categorized under Actions, Interface, or Movement.

## Event Packets

### EVENT_APPLET_FOCUS (Opcode 0)
| Field | Description |
|-------|-------------|
| **Opcode** | 0 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fbac0` |
| **Sender** | `0x00234a80` / `0x0032f260` |

Batched focus/input event data. Collects events over time and sends them periodically (every 1000ms). Uses `pT_ushort` for event IDs and invokes virtual serializers per event type.

### EVENT_CAMERA_POSITION (Opcode 1)
| Field | Description |
|-------|-------------|
| **Opcode** | 1 |
| **Size** | 9 (fixed) |
| **DAT Address** | `0x016fbab0` |
| **Sender** | `0x0021ec60` |

Sends the camera position/orientation to the server. Fixed 9-byte packet.

### EVENT_MOUSE_MOVE (Opcode 85)
| Field | Description |
|-------|-------------|
| **Opcode** | 85 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb570` |
| **Sender** | `0x0021ea40` |

Reports mouse movement to the server. 7-byte fixed format.

---

## Chat Packets

### MESSAGE_PUBLIC (Opcode 29)
| Field | Description |
|-------|-------------|
| **Opcode** | 29 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb8f0` |
| **Sender** | `0x003a4890` |

Standard public chat message. VAR_BYTE payload with huffman-encoded text.

### MESSAGE_PUBLIC with Effects (Opcode 4)
| Field | Description |
|-------|-------------|
| **Opcode** | 4 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fba80` |
| **Sender** | `0x003a5aa0` |

Public chat message with color/animation effects. The effects byte precedes the message text.

### MESSAGE_PRIVATE (Opcode 52)
| Field | Description |
|-------|-------------|
| **Opcode** | 52 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fb780` |
| **Sender** | `0x003a7260` |

Private message to another player. VAR_SHORT to accommodate the recipient name and message.

### MESSAGE_CLAN_CHAT (Opcode 86)
| Field | Description |
|-------|-------------|
| **Opcode** | 86 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb560` |
| **Sender** | `0x003e6420` |

**Packet Format:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | segmentCount | Number of text segments + 3 |
| 1 | 1 | byte | colorEffect | Text color effect |
| 2 | 1 | byte | animEffect | Text animation effect |
| 3+ | varies | encoded | message | Huffman-encoded message text |

Sends a chat message. The text is whitespace-trimmed (leading and trailing spaces removed) before encoding. Maximum 252 bytes of trimmed text.

---

## Social Packets

### FRIENDLIST_ADD (Opcode 78)
| Field | Description |
|-------|-------------|
| **Opcode** | 78 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb5e0` |
| **Sender** | `0x003d3f80` |

Adds a player to the friends list.

### FRIENDLIST_DEL (Opcode 121)
| Field | Description |
|-------|-------------|
| **Opcode** | 121 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb340` |
| **Sender** | `0x003cdd50` (CS2 opcode handler) |

Removes a player from the friends list. References error message: "The channel you tried to join does not exist".

### IGNORELIST_ADD (Opcode 109)
| Field | Description |
|-------|-------------|
| **Opcode** | 109 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb3f0` |
| **Sender** | `0x003afbc0` |

Adds a player to the ignore list.

### CLAN_JOINCHAT (Opcode 67)
| Field | Description |
|-------|-------------|
| **Opcode** | 67 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb690` |
| **Sender** | `0x00361310` (CS2 opcode handler) |

Joins a clan chat channel. References error message: "That user is not in this channel".

### CLAN_LEAVECHAT (Opcode 93)
| Field | Description |
|-------|-------------|
| **Opcode** | 93 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb4f0` |
| **Sender** | `0x00361120` (CS2 opcode handler) |

Leaves a clan chat channel.

### SOCIAL_REQUEST (Opcode 39)
| Field | Description |
|-------|-------------|
| **Opcode** | 39 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb850` |
| **Sender** | `0x003cda50` (CS2 opcode handler) |

Sends an encoded string to the server (social channel request). References error messages about users/channels.

---

## System/Status Packets

### WINDOW_STATUS (Opcode 82)
| Field | Description |
|-------|-------------|
| **Opcode** | 82 |
| **Size** | 3 (fixed) |
| **DAT Address** | `0x016fb5a0` |
| **Sender** | `0x00360b70` (CS2 opcode handler) |

Reports the client window state.

### NO_TIMEOUT (Opcode 15)
| Field | Description |
|-------|-------------|
| **Opcode** | 15 |
| **Size** | 0 (no payload) |
| **DAT Address** | `0x016fb9d0` |
| **Sender** | `0x00360cb0` (CS2 opcode handler) |

Keepalive packet -- prevents the server from disconnecting the client due to inactivity. Zero-length payload.

### MAP_BUILD_COMPLETE (Opcode 21)
| Field | Description |
|-------|-------------|
| **Opcode** | 21 |
| **Size** | 0 (no payload) |
| **DAT Address** | `0x016fb970` |
| **Sender** | `0x00420950` |

Notifies the server that the client has finished loading the current map region. Zero-length payload.

### DETECT_MODIFIED_CLIENT (Opcode 95)
| Field | Description |
|-------|-------------|
| **Opcode** | 95 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb4d0` |
| **Sender** | `0x00314d70` |

Anti-cheat detection packet. Reports results of client integrity checks.

### CLIENT_CHEAT (Opcode 24)
| Field | Description |
|-------|-------------|
| **Opcode** | 24 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb940` |
| **Sender** | `0x00360f30` (CS2 opcode handler) |

Sends a client-side cheat/debug command string (e.g., `::command`).

### BUG_REPORT (Opcode 123)
| Field | Description |
|-------|-------------|
| **Opcode** | 123 |
| **Size** | 1 (fixed) |
| **DAT Address** | `0x016fb320` |
| **Sender** | `0x00360d90` (CS2 opcode handler) |

Submits a bug report. Single byte payload (report type/category).

### WORLDLIST_FETCH (Opcode 110)
| Field | Description |
|-------|-------------|
| **Opcode** | 110 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb3e0` |
| **Sender** | `0x00234580` |

Requests the world list from the server. 4-byte payload (likely a checksum or version).

---

## Sound Packets

### SOUND_SONGSELECT (Opcode 88)
| Field | Description |
|-------|-------------|
| **Opcode** | 88 |
| **Size** | 2 (fixed) |
| **DAT Address** | `0x016fb540` |
| **Sender** | `0x00361040` (CS2 opcode handler) |

Reports the currently playing music track to the server.

### SOUND_SONGEND (Opcode 76)
| Field | Description |
|-------|-------------|
| **Opcode** | 76 |
| **Size** | 2 (fixed) |
| **DAT Address** | `0x016fb600` |
| **Sender** | `0x00361230` (CS2 opcode handler) |

Notifies the server that a music track has finished playing.

---

## Transform/Affine Packets

### AFFINEDTRANSFORM_SET (Opcode 99)
| Field | Description |
|-------|-------------|
| **Opcode** | 99 |
| **Size** | 2 (fixed) |
| **DAT Address** | `0x016fb490` |
| **Sender** | `0x00360a80` / `0x00361bc0` (CS2 opcode handlers) |

**Packet Format (2 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | transformValue | Transform type/value |
| 1 | 1 | byte | param | Additional parameter (always 0 in one sender) |

---

## Display/Rendering Packets

### DISPLAY_INFO (Opcode 106)
| Field | Description |
|-------|-------------|
| **Opcode** | 106 |
| **Size** | 6 (fixed) |
| **DAT Address** | `0x016fb420` |
| **Sender** | `0x002c46e0` |

Reports display/rendering state. Contains GPU capability flags, window dimensions, and display mode info.

**Packet Format (6 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | renderMode | 2 or 3 (based on GPU capability flag) |
| 1 | 2 | ushort | screenWidth | Screen/window width |
| 3 | 2 | ushort | screenHeight | Screen/window height |
| 5 | 1 | byte | displayFlags | Additional display settings |

### FOCUS_CHANGED (Opcode 83)
| Field | Description |
|-------|-------------|
| **Opcode** | 83 |
| **Size** | 1 (fixed) |
| **DAT Address** | `0x016fb590` |
| **Sender** | `0x002c48b0` |

**Packet Format (1 byte):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | hasFocus | 1 if window has keyboard focus, 0 otherwise |

Sent when the game window gains or loses keyboard focus (uses `SDL_GetKeyboardFocus`).

---

## Data Reporting Packets

### CAMERA_DIRECTION (Opcode 2)
| Field | Description |
|-------|-------------|
| **Opcode** | 2 |
| **Size** | 6 (fixed) |
| **DAT Address** | `0x016fbaa0` |
| **Sender** | `0x002825b0` |

**Packet Format (6 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 4 | int | packedXY | Camera X (low 16 bits) and Y (high 16 bits), clamped to 0-65534 |
| 4 | 2 | custom | timeDelta | Time since last report (15-bit, max 32767), with additional flag bit |

### EVENT_TELEMETRY (Opcode 45)
| Field | Description |
|-------|-------------|
| **Opcode** | 45 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fb7f0` |
| **Sender** | `0x002c48b0` |

Telemetry/analytics data reporting. Sends input event samples with timestamps. Each sample is 4 bytes (1 byte type + 3 bytes 24-bit timestamp delta).

### SCENE_GRAPH_REPORT (Opcode 50)
| Field | Description |
|-------|-------------|
| **Opcode** | 50 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb7a0` |
| **Sender** | `0x002c2fc0` |

Sends scene graph status data. 4-byte int payload.

### CAMERA_ANGLE (Opcode 51)
| Field | Description |
|-------|-------------|
| **Opcode** | 51 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb790` |
| **Sender** | `0x002c48b0` |

**Packet Format (4 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | custom | cameraYaw | Camera yaw angle (encoded, 0x800 modular range) |
| 2 | 2 | custom | cameraPitch | Camera pitch angle |

### RENDER_REPORT (Opcode 113)
| Field | Description |
|-------|-------------|
| **Opcode** | 113 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb3b0` |
| **Sender** | `0x00299fa0` |

Reports rendering thread status. Sent after render thread synchronization is complete.

**Packet Format (4 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | zero | Always 0 |
| 1 | 2 | ushort | renderData | Render status data (little-endian) |
| 3 | 1 | byte | frameRate | Frame rate indicator (clamped to 0xFF, inverted encoding) |

### DEVICE_INFO (Opcode 28)
| Field | Description |
|-------|-------------|
| **Opcode** | 28 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb900` |
| **Sender** | `0x002c48b0` |

Device/system information packet sent once during session setup. Contains encoded system fingerprint data.

---

## CS2-Triggered Misc Packets

### ENCODEDSTRING_SEND (Opcode 62)
| Field | Description |
|-------|-------------|
| **Opcode** | 62 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb6e0` |
| **Sender** | `0x003f2e10` (CS2 opcode handler) |

CS2 script-triggered: sends an encoded string (max 30 chars) with encoding metadata.

### ENCODEDSTRING_SEND2 (Opcode 127)
| Field | Description |
|-------|-------------|
| **Opcode** | 127 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb2e0` |
| **Sender** | `0x003f30d0` (CS2 opcode handler) |

Similar to opcode 62 -- sends an encoded string (max 30 chars) from CS2 script context.

### VERIFIED_STRING_SEND (Opcode 3)
| Field | Description |
|-------|-------------|
| **Opcode** | 3 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fba90` |
| **Sender** | `0x0036ed10` (CS2 opcode handler) |

Sends two encoded strings with size validation (max 500 bytes each).

### ENCRYPTED_STRING_SEND (Opcode 116)
| Field | Description |
|-------|-------------|
| **Opcode** | 116 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb380` |
| **Sender** | `0x003779c0` (CS2 opcode handler) |

Sends a string encrypted with `tinyKeyEncrypt`. Requires game state 0x17 and sub-state 0x1e.

### ENCRYPTED_STRING_SEND2 (Opcode 74)
| Field | Description |
|-------|-------------|
| **Opcode** | 74 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fb620` |
| **Sender** | `0x00377b60` (CS2 opcode handler) |

Sends a string encrypted with `tinyKeyEncrypt`. VAR_SHORT variant. Similar state requirements.

### STRTOL_SEND (Opcode 8)
| Field | Description |
|-------|-------------|
| **Opcode** | 8 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fba40` |
| **Sender** | `0x00379490` (CS2 opcode handler) |

CS2 script: parses a string from the string stack as a decimal integer (via `strtol`) and sends it as a 4-byte int.

### STRTOLL_SEND (Opcode 129)
| Field | Description |
|-------|-------------|
| **Opcode** | 129 |
| **Size** | 8 (fixed) |
| **DAT Address** | `0x016fb2c0` |
| **Sender** | `0x0037a580` (CS2 opcode handler) |

CS2 script: parses a string from the string stack as a decimal long long (via `strtoll`) and sends it as an 8-byte long.

### QUEUED_PACKET (Opcode 34)
| Field | Description |
|-------|-------------|
| **Opcode** | 34 |
| **Size** | 0 (no payload) |
| **DAT Address** | `0x016fb8a0` |
| **Sender** | `0x00378630` (CS2 opcode handler) |

Queues a zero-payload packet into the send queue without immediately flushing. The packet is queued into a linked list and counted against the connection's bandwidth budget.

### CS2_CALLBACK (Opcode 32)
| Field | Description |
|-------|-------------|
| **Opcode** | 32 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb8c0` |
| **Sender** | `0x003cd910` (CS2 opcode handler) |

Generic CS2 callback packet. Sends encoded data from a CS2 script string stack entry.

### DATA_REPORT_VARSHORT (Opcode 49)
| Field | Description |
|-------|-------------|
| **Opcode** | 49 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fb7b0` |
| **Sender** | `0x0036b520` (CS2 opcode handler) |

CS2 script handler that sends variable-length encoded data. The payload starts with a 2-byte zero marker, followed by encoded data and optional string.

### INTERFACE_INTERACTION (Opcode 122)
| Field | Description |
|-------|-------------|
| **Opcode** | 122 |
| **Size** | 16 (fixed) |
| **DAT Address** | `0x016fb330` |
| **Sender** | `0x00267620` |

16-byte interface interaction packet. Sent after a script hook is executed on an interface component.

---

## Identified Opcodes by Sender (Cross-Reference)

### Multi-Packet Senders
The function at `0x002c48b0` sends multiple different packets during its execution:
- Opcode 45 (EVENT_TELEMETRY) -- `DAT_016fb7f0`
- Opcode 51 (CAMERA_ANGLE) -- `DAT_016fb790`
- Opcode 83 (FOCUS_CHANGED) -- `DAT_016fb590`
- Opcode 28 (DEVICE_INFO) -- `DAT_016fb900`

---

## Newly Identified Opcodes

### IF_BUTTON1-10 (Opcodes 97, 118, 54, 128, 18, 64, 61, 124, 63, 47)

All dispatched from `jag::InterfaceManager::IfButtonXInner` via `g_ifButtonPtrTable` at `0x014908a0`. Format: `ushort(componentHash) + byte(slotId_lo) + byte(slotId_hi) + int(opValue)` = 8 bytes each.

| Table Index | buttonOp | Opcode | Name |
|-------------|----------|--------|------|
| 0 | 1 | 97 | IF_BUTTON1 |
| 1 | 2 | 118 | IF_BUTTON2 |
| 2 | 3 | 54 | IF_BUTTON3 |
| 3 | 4 | 128 | IF_BUTTON4 |
| 4 | 5 | 18 | IF_BUTTON5 |
| 5 | 6 | 64 | IF_BUTTON6 |
| 6 | 7 | 61 | IF_BUTTON7 |
| 7 | 8 | 124 | IF_BUTTON8 |
| 8 | 9 | 63 | IF_BUTTON9 |
| 9 | 10 | 47 | IF_BUTTON10 |

When a component has a target name string (offset `+0x130`), opcode 107 (IF_BUTTONT, VAR_BYTE) is used instead.

---

### Targeted Action Opcodes (OPNPC_T / OPLOC_T)

NPC targeted (use-item-on-NPC), 3 bytes: `pT_ushort(npcIndex) + p1(ctrlRunFlag)`

| Opcode | Name | Sender | DoAction ID |
|--------|------|--------|-------------|
| 9 | OPNPC_T1 | SendOpNpc1T | NPC_1 (9) |
| 19 | OPNPC_T2 | SendOpNpc2T | NPC_2 (10) |
| 35 | OPNPC_T5 | SendOpNpc5T | NPC_5 (13) |
| 36 | OPNPC_T6 | SendOpNpc6T | NPC_6 (1003) |
| 69 | OPNPC4_T | SendOpNpc4T | NPC_4 (12) |
| 125 | OPNPC3_T | SendOpNpc3T | NPC_3 (11) |
| 5 | OPNPC_T_LONG | (extended, 15B) | Extended NPC targeted |

LOC targeted (use-item-on-location), 9 bytes: `p2(destY) + p1(ctrlRunFlag) + p2(destX) + p4_alt1(locId)`

| Opcode | Name | Sender | DoAction ID |
|--------|------|--------|-------------|
| 38 | OPLOC_T1 | SendOpLoc1T | OBJECT_1 (3) |
| 40 | OPLOC_T3 | SendOpLoc3T | OBJECT_3 (5) |
| 43 | OPLOC_T6 | SendOpLoc6T | OBJECT_6 (1002) |
| 68 | OPLOC2_T | SendOpLoc2T | OBJECT_2 (4) |
| 101 | OPLOC4_T | SendOpLoc4T | OBJECT_4 (6) |
| 111 | OPLOC5_T | SendOpLoc5T | OBJECT_5 (1001) |

---

### CS2 Script-Triggered Opcodes

| Opcode | Name | Size | Sender | Description |
|--------|------|------|--------|-------------|
| 17 | EVENT_MOUSE_CLICK | VAR_BYTE | CS2 getter at `0x00211130` | Reports mouse click events to server |
| 48 | EVENT_KEYBOARD | VAR_BYTE | CS2 getter at `0x00281c20` | Reports keyboard events to server |
| 71 | ACTIVE_CHAT_PHRASE_SEND | VAR_BYTE | `0x0036a340` | Sends chatphrase to public chat. Format: `byte(0) + ushort(phraseId) + encoded phrase data` |
| 81 | ACTIVE_CHAT_PHRASE_SENDPRIVATE | VAR_BYTE | `0x00369e70` | Sends chatphrase in private message. Format: `byte(0) + string(recipientName) + ushort(phraseId) + encoded data` |
| 89 | MOVE_SCRIPTED | 5 | `0x0040cfc0` | CS2-triggered scripted movement. Format: `ushort(coordData) + byte(-moveSpeed) + ushort(additionalCoord)` |

---

### Other Identified Opcodes

| Opcode | Name | Size | Sender | Description |
|--------|------|------|--------|-------------|
| 12 | IF_BUTTON_T | 16 | FUN_002e2b90 (dispatcher) | Interface button with targeted item context |
| 66 | EVENT_APPLET_FOCUS_2 | 4 | `0x0032f260` | Window focus/unfocus variant |
| 80 | NO_TIMEOUT_2 | 0 | ProcessConnections | Keepalive sent every 50 tick cycles on secondary connection |

---

## Remaining Unidentified Opcodes

17 opcodes have no sender xrefs found. These are likely dead/legacy code or sent from code paths too complex to trace (large dispatcher functions, indirect calls).

| Opcode | Size | DAT Address | Notes |
|--------|------|-------------|-------|
| 11 | 4 | `0x016fba10` | Fixed 4 bytes |
| 13 | 2 | `0x016fb9f0` | Fixed 2 bytes |
| 22 | 1 | `0x016fb960` | Fixed 1 byte |
| 41 | 0 | `0x016fb830` | No payload |
| 44 | VAR_SHORT | `0x016fb800` | |
| 53 | 9 | `0x016fb770` | Fixed 9 bytes, possibly OPPLAYER variant |
| 55 | 1 | `0x016fb750` | Fixed 1 byte |
| 57 | 9 | `0x016fb730` | Fixed 9 bytes, possibly OPPLAYER variant |
| 72 | 1 | `0x016fb640` | Fixed 1 byte, no sender xrefs |
| 73 | 18 | `0x016fb630` | Fixed 18 bytes, no sender xrefs |
| 79 | 1 | `0x016fb5d0` | Fixed 1 byte, no sender xrefs |
| 100 | 4 | `0x016fb480` | Fixed 4 bytes, no sender xrefs |
| 108 | VAR_SHORT | `0x016fb400` | No sender xrefs |
| 112 | VAR_BYTE | `0x016fb3c0` | No sender xrefs |
| 126 | VAR_BYTE | `0x016fb2f0` | No sender xrefs |
| 31 | VAR_SHORT | `0x016fb8d0` | Possibly EVENT_CAMERA_POSITION variant |
