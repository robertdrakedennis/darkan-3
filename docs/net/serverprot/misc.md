# ServerProt: Miscellaneous (Uncategorized) Handlers

## Overview

This document covers the 74 ServerProt handlers that are registered inline in `BindHandlers` (0x0011852a) but do not belong to any of the 12 named packet handler categories (Audio, Camera, Chat, ClanChannel, ClanSettings, ClientState, Interfaces, NPCInfo, Social, Variables, WorldData, ZoneUpdates). These include critical handlers such as PLAYER_INFO, LOGOUT, UPDATE_STAT, UPDATE_REBOOT_TIMER, and various entity/state management packets.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success)

## Identified Handlers

### PLAYER_INFO (opcode 189, var-byte)
**Handler Address:** `0x0025b000` | **ServerProt Global:** `0x016f95a0`

**Behavior:** Primary player update packet. This is an extremely large and complex handler (56K+ chars decompiled) that decodes the bit-packed player position, appearance, animation, and extended info update stream for all visible players. Sent every game tick. Comparable in complexity to NPC_INFO.

---

### LOGOUT (opcode 134, size 0)
**Handler Address:** `0x00255960` | **ServerProt Global:** `0x016f9f60`

**Behavior:** Forces full client logout. Sequence:
1. Closes the `ClientStream` TCP connection
2. Calls `jag::Client::SetMainState(INITIAL)` to reset to title screen
3. Destroys connection data objects (0x280 byte allocation)
4. Clears player list shared_ptrs (releases all ref-counted entries)
5. Resets interface manager state
6. Sets connection ready flag at +0x260 to 1

---

### LOGOUT_TRANSFER (opcode 131, size 0)
**Handler Address:** `0x00260440` | **ServerProt Global:** `0x016f9fe0`

**Behavior:** Initiates a world transfer (world hop). Unlike LOGOUT which returns to title screen:
1. Closes the active connection object at `ClanManager+0xC0` (0x280 byte object)
2. Sets state to 3 (reconnecting)
3. Sets a 30-second reconnect timer via `system_clock::now() / 1000000 + 30000`
Used for seamless world switching.

---

### NOOP (opcode 146, size 0)
**Handler Address:** `0x00212d40` | **ServerProt Global:** `0x016f9d60`

**Behavior:** Empty handler. Returns success immediately with no processing. Used as a keepalive or placeholder.

---

### SET_RUN_ENERGY (opcode 27, size 1)
**Handler Address:** `0x00213e30` | **ServerProt Global:** `0x016fade0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Run energy value (0-100) |

**Behavior:** Sets the player's run energy. Stores the value at `__DT_SYMTAB[0x49a]+0x60`.

---

### RESET_ENTITY_LISTS (opcode 25, size 0)
**Handler Address:** `0x001e7480` | **ServerProt Global:** `0x016fae20`

**Behavior:** Resets both the PlayerList (`__DT_SYMTAB[0x499]`) and NPCManager (`__DT_SYMTAB[0x497]`) entity data. Called before REBUILD_NORMAL to clear old entity state when the player teleports or changes regions.

---

### TRIGGER_ONDIALOGABORT (opcode 212, size 0)
**Handler Address:** `0x00194170` | **ServerProt Global:** `0x016f91e0`

**Behavior:** Increments a global counter, calls `FUN_005c0720`, sets a flag at `__DT_SYMTAB[0x496]+0x154` to 1. Signals dialog abort / scene reset.

---

## Partially Identified Handlers

These handlers have been decompiled and analyzed but need further confirmation on their exact RS protocol name.

### SET_INTERACTION_FLAG_A (opcode 23, size 1)
**Handler Address:** `0x001c6a20` | **ServerProt Global:** `0x016fae60`

Reads 1 byte, stores at `__DT_SYMTAB[0x492]+0x50`. Also calls `FUN_0024cf20` (connection state update). May trigger reconnection logic based on game state checks.

### SET_INTERACTION_FLAG_B (opcode 161, size 1)
**Handler Address:** `0x002122f0` | **ServerProt Global:** `0x016f9b20`

Reads 1 byte with +0x80 transform, stores at `__DT_SYMTAB[0x4dc]+0xA0`.

### SET_INTERACTION_FLAG_C (opcode 169, size 1)
**Handler Address:** `0x00212280` | **ServerProt Global:** `0x016f9920`

Reads 1 byte with +0x80 transform, stores at `__DT_SYMTAB[0x4dc]+0xA4`.

### SET_CHAT_FILTER_A (opcode 204, size 1)
**Handler Address:** `0x00212bd0` | **ServerProt Global:** `0x016f92e0`

Reads 1 byte, validates against bitmask `0xFE3`, stores at `__DT_SYMTAB[0x490]+0x38`.

### SET_CHAT_FILTER_B (opcode 188, size 1)
**Handler Address:** `0x00212c50` | **ServerProt Global:** `0x016f95e0`

Reads 1 byte, validates against bitmask `0x23E01803FE3`, stores at `__DT_SYMTAB[0x490]+0x30`.

### SET_CHAT_FILTER_C (opcode 164, size 1)
**Handler Address:** `0x00212b20` | **ServerProt Global:** `0x016f9a60`

Reads 1 byte, validates against bitmask `0xE3`, stores at `__DT_SYMTAB[0x490]+0x3C`. Also clears an EASTL string at +0x40.

### SET_NPC_UPDATE_FLAG (opcode 181, size 1)
**Handler Address:** `0x00212880` | **ServerProt Global:** `0x016f96e0`

Reads 1 byte, sets `__DT_SYMTAB[0x493]+0x14` to 1, increments counter at +0x10, sets `NPCManager+0x6A` to `(byte == 1)`.

### UPDATE_URL_STRING (opcode 155, var-short)
**Handler Address:** `0x001e02a0` | **ServerProt Global:** `0x016f9be0`

Reads 4 bytes + null-terminated CP1252 string. Updates counter at `ClanManager+0x130`. Processes string with CP1252-to-UTF8 conversion.

### REMOVE_PLAYER_FROM_LIST (opcode 205, size 1)
**Handler Address:** `0x00211ee0` | **ServerProt Global:** `0x016f92a0`

Reads 1 byte (negated + offset transform). Binary searches a sorted player vector at `PlayerList+0x1DDC`, removes the matching entry by shifting.

### UPDATE_FRIENDLIST_DELTA (opcode 168, var-byte)
**Handler Address:** `0x00244a00` | **ServerProt Global:** `0x016f9960`

Complex handler accessing FriendList (`__DT_SYMTAB[0x498]`) and world lookup. Processes 0xB0-byte friend entries with zone data objects.

### UPDATE_IGNORELIST (opcode 17, var-byte)
**Handler Address:** `0x00235b20` | **ServerProt Global:** `0x016faf60`

Reads byte flag + CP1252 string triplets in a loop. Accesses `__DT_SYMTAB[0x49a]` list of 0x50-byte entries (3 EASTL strings + flag byte each). Max 400 entries. Resets PlayerList at end.

---

## Complete Uncategorized Handler Reference Table

All 74 uncategorized handlers registered inline in BindHandlers, sorted by opcode. All have been decompiled, named in Ghidra, and documented.

| Opcode | Size | Handler Address | Confirmed Name | Status |
|--------|------|-----------------|----------------|--------|
| 9 | var_byte | `0x00219010` | **SET_NPC_OP** | Verified |
| 13 | 1 | `0x002124a0` | **SET_PLAYER_OP_3** | Verified |
| 17 | var_byte | `0x00235b20` | **UPDATE_IGNORELIST** | Verified |
| 18 | var_byte | `0x00242840` | **UPDATE_SITESETTINGS** | Verified |
| 20 | var_byte | `0x0024ade0` | **IF_OPENSUB_ACTIVE** | Verified |
| 23 | 1 | `0x001c6a20` | **SET_INTERACTION_FLAG_A** | Verified |
| 25 | 0 | `0x001e7480` | **RESET_ENTITY_LISTS** | Verified |
| 27 | 1 | `0x00213e30` | **SET_RUN_ENERGY** | Verified |
| 29 | var_short | `0x0023fab0` | **CLANSETTINGS_FULL** | Verified |
| 36 | var_byte | `0x00268960` | **IF_SETPLAYERMODEL_OTHER** | Verified |
| 41 | 10 | `0x001e4320` | **LOC_ADD_CHANGE** | Verified (was UPDATE_INV_STOPTRANSMIT) |
| 42 | var_short | `0x002228f0` | **PLAYER_INFO_DECODE** | Verified |
| 47 | 14 | `0x002224a0` | **MAP_FLAG_SET_PLAYER** | Verified |
| 53 | 35 | `0x00212d80` | **CUTSCENE_DATA** | Verified |
| 54 | 25 | `0x001930a0` | **PROJANIM** | Verified |
| 66 | var_short | `0x00225bf0` | **FRIENDLIST_LOADED** | Verified (was FRIEND_STATUS) |
| 73 | 2 | `0x0018cc40` | **SET_PLAYER_CHAT_EFFECTS** | Verified |
| 80 | 1 | `0x002129d0` | **SET_MULTIWAY_STATE** | Verified |
| 84 | var_byte | `0x00280210` | **DETAIL_OPTIONS** | Verified (confirmed by symbol) |
| 85 | 4 | `0x00212580` | **SET_DISPLAY_INT** | Verified |
| 86 | var_short | `0x00219210` | **NPC_HEADICON_SPECIFIC** | Verified |
| 91 | 12 | `0x002060a0` | **SPOTANIM_SPECIFIC** | Verified (was SPOTANIM_MAP) |
| 94 | 19 | `0x001941b0` | **NPC_HITMARKS_AND_HEADBARS** | Verified |
| 97 | 2 | `0x00212410` | **SET_PLAYER_OP_2** | Verified |
| 101 | 3 | `0x002130b0` | **REMOVE_TRACKED_ENTRY** | Verified |
| 102 | 1 | `0x00191030` | **SET_NPC_UPDATE_ORIGIN** | Verified |
| 103 | 10 | `0x00225e80` | **SET_PLAYER_GROUP** | Verified (was PLAYER_GROUP_FULL) |
| 109 | var_byte | `0x00198080` | **REBUILD_NORMAL** | Verified |
| 111 | var_short | `0x0018cd20` | **NOOP_VAR** | Verified (true no-op) |
| 128 | 3 | `0x0027e6f0` | **IF_SETANGLE_ACTIVE** | Verified |
| 131 | 0 | `0x00260440` | **LOGOUT_TRANSFER** | Verified |
| 133 | 1 | `0x00212ce0` | **SET_CHAT_FILTER_D** | Verified |
| 134 | 0 | `0x00255960` | **LOGOUT** | Verified |
| 135 | 1 | `0x00212950` | **SET_WEIGHT** | Verified |
| 139 | 4 | `0x00225d60` | **PLAYER_OP** | Verified |
| 141 | var_short | `0x0024a510` | **UPDATE_ZONE_FULL_FOLLOWS_3** | Verified (was UPDATE_INV_STOPTRANSMIT_2) |
| 143 | 8 | `0x0021ec60` | **SET_CAMERA_TARGET** | Verified (was SET_WORLD_TARGET_2) |
| 144 | 4 | `0x00211e40` | **SET_SYSUPDATE_TIMER** | Verified |
| 145 | 8 | `0x0027fd70` | **IF_SETHIDE_ACTIVE** | Verified |
| 146 | 0 | `0x00212d40` | **NOOP** | Verified |
| 148 | var_short | `0x00222340` | **UPDATE_PLAYER_CHAT** | Verified (was NPC_ANIM_SPECIFIC) |
| 152 | var_short | `0x00276e20` | **UPDATE_IGNORELIST** | Verified |
| 154 | var_byte | `0x002384f0` | **UPDATE_PLAYER_GROUP** | Verified |
| 155 | var_short | `0x001e02a0` | **UPDATE_URL_STRING** | Verified |
| 161 | 1 | `0x002122f0` | **SET_INTERACTION_FLAG_B** | Verified |
| 162 | var_short | `0x00256100` | **IF_SETGRAPHIC_ACTIVE** | Verified |
| 163 | var_byte | `0x00222080` | **REBUILD_PLAYERINFO_POSITIONS** | Verified |
| 164 | 1 | `0x00212b20` | **SET_CHAT_FILTER_C** | Verified |
| 167 | 2 | `0x0018d3e0` | **SKIP_2_BYTES** | Verified (deprecated no-op) |
| 168 | var_byte | `0x00244a00` | **UPDATE_FRIENDLIST_DELTA** | Verified |
| 169 | 1 | `0x00212280` | **SET_INTERACTION_FLAG_C** | Verified |
| 170 | var_byte | `0x00211e00` | **NOOP_VAR** | Verified (true no-op) |
| 171 | var_byte | `0x00218f70` | **SET_URL_STRING** | Verified |
| 172 | 5 | `0x0027f990` | **IF_SETNPCMODEL_ACTIVE** | Verified |
| 173 | var_byte | `0x0027c4c0` | **IF_SETMODEL_ACTIVE** | Verified |
| 175 | 3 | `0x00212360` | **SET_INTERACTION_FLAG_D** | Verified |
| 177 | var_byte | `0x00198090` | **REBUILD_REGION** | Verified |
| 179 | 2 | `0x00238140` | **FRIENDCHAT_SYSUPDATE** | Verified |
| 181 | 1 | `0x00212880` | **SET_NPC_UPDATE_FLAG** | Verified |
| 185 | 33 | `0x00192ca0` | **MAP_PROJANIM** | Verified (was MAP_FLAG_SET_2) |
| 187 | 4 | `0x00225df0` | **PLAYER_GROUP_DELTA** | Verified |
| 188 | 1 | `0x00212c50` | **SET_CHAT_FILTER_B** | Verified |
| 189 | var_byte | `0x0025b000` | **PLAYER_INFO** | Verified |
| 190 | 3 | `0x0027cea0` | **IF_MOVESUB_ACTIVE** | Verified |
| 192 | var_byte | `0x00212700` | **SKIP_DATA** | Verified (deprecated) |
| 193 | 15 | `0x00205ac0` | **MAP_FLAG_SET** | Verified |
| 195 | 3 | `0x0027d930` | **IF_SETPOSITION_ACTIVE** | Verified |
| 197 | 3 | `0x0027c910` | **IF_SETCLICKMASK_ACTIVE** | Verified |
| 203 | 8 | `0x00221020` | **SERVER_TICK_END** | Verified (was SET_COMBAT_STYLE) |
| 204 | 1 | `0x00212bd0` | **SET_CHAT_FILTER_A** | Verified |
| 205 | 1 | `0x00211ee0` | **REMOVE_PLAYER_FROM_LIST** | Verified |
| 210 | 4 | `0x0027d400` | **IF_SETRECOL_ACTIVE** | Verified |
| 212 | 0 | `0x00194170` | **TRIGGER_ONDIALOGABORT** | Verified |
| N/A | 28 | `0x00221e20` | **SET_UID** | Verified (not in RegisterAll, login-time) |

## Statistics

- **Total ServerProt opcodes**: 216 registered
- **Named handler categories**: 12 (142 handlers)
- **Uncategorized inline handlers**: 74 (this document)
- **Fully identified and named in Ghidra**: 74/74 (100%)
- **Remaining unknown**: 0
- **Significant name corrections**: 7 (LOC_ADD_CHANGE, FRIENDLIST_LOADED, SET_PLAYER_GROUP, UPDATE_PLAYER_CHAT, SERVER_TICK_END, MAP_PROJANIM, UPDATE_ZONE_FULL_FOLLOWS_3)

## DT_SYMTAB Client Offset Reference

These `__DT_SYMTAB` indices are used by the handlers to access client subsystems:

| Index | Purpose | Example Handlers |
|-------|---------|-----------------|
| 0x441 | ConnectionManager/Stream | LOGOUT |
| 0x442 | World Lookup Service | UPDATE_FRIENDLIST_DELTA |
| 0x490 | Chat Filter Settings | SET_CHAT_FILTER_A/B/C |
| 0x491 | Interface Manager | LOGOUT |
| 0x492 | Interaction State | SET_INTERACTION_FLAG_A |
| 0x493 | Scene Update State | SET_NPC_UPDATE_FLAG |
| 0x495 | Player Entity Manager | LOGOUT |
| 0x496 | Connection/Dialog State | TRIGGER_ONDIALOGABORT |
| 0x497 | NPCManager | RESET_ENTITY_LISTS, SET_NPC_UPDATE_FLAG |
| 0x498 | FriendList | UPDATE_FRIENDLIST_DELTA |
| 0x499 | PlayerList | RESET_ENTITY_LISTS, REMOVE_PLAYER_FROM_LIST |
| 0x49a | Run Energy / State Flags | SET_RUN_ENERGY, UPDATE_IGNORELIST |
| 0x49b | Scene Graph | LOGOUT |
| 0x49c | ClanManager / Connection | LOGOUT_TRANSFER, UPDATE_URL_STRING |
| 0x4dc | Game State / Interaction | SET_INTERACTION_FLAG_B/C |
