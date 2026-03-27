# Account Creation Sequence (Build 947-1)

> **Source**: Live packet capture via project-undercut/engine, 2026-03-26.
> **Verified against**: rs2client 947-1 binary (Ghidra port 8083) with handler cross-references.

## Overview

Account creation in RS3 947-1 is NOT a separate connection. The existing game session transitions
through these phases:

```
Lobby → World Login → Character Creation UI → Game World (Tutorial Island)
```

The server drives the entire flow using standard ServerProt packets. The client never sends a
dedicated "create account" packet — it simply interacts via IF_BUTTON clicks and
RESUME_P_NAMEDIALOG for the display name.

---

## Phase 1: World Login (Lobby → World)

Triggered when the user clicks "Play" on a world in the lobby. The server sends the world
initialization sequence.

### Step 1.1: Map Build

```
REBUILD_REGION (opcode 90, varShort)
    Builds the instanced character creation scene.
    Fields:
        regionCoords    : ushort    — packed region X/Y
        level           : ubyte     — map level (0-3)
        coordPair       : varies    — world coordinates
        buildType       : byte      — scene type identifier
        metadata        : bytes     — region-specific map metadata
    Note: No XTEA keys — instanced regions don't use encrypted map data.
```

### Step 1.2: Session Token

```
HASHED_WORLD_TOKEN (opcode 6, varByte)
    One-time session nonce for the world connection.
    Fields:
        token           : cstring   — Base64url-encoded session token (null-terminated)
    Example: "wwGlrZHF5gIvByO6QzWWBbVeiGlFuJzgNbNSFKVIyTc\0"
```

### Step 1.3: Display Metadata

```
UNKNOWN_114 (opcode 114, 2 bytes fixed)
    Purpose unconfirmed. Sent once immediately after session token.
    Fields:
        value1          : ubyte     — observed: 0x80
        value2          : ubyte     — observed: 0x80

JCOINS_UPDATE (opcode 59, 4 bytes fixed)
    RuneCoins balance display.
    Fields:
        balance         : int       — coin balance (big-endian)
```

### Step 1.4: Interaction Flags

```
UNKNOWN_213 (opcode 213, 1 byte fixed)
    Sent at every phase transition, always paired with UNKNOWN_154.
    Fields:
        flags           : ubyte     — observed: 0x7F

UNKNOWN_154 (opcode 154, 1 byte fixed)
    Fields:
        flags           : ubyte     — observed: 0xFF
```

### Step 1.5: Player Right-Click Options

```
SET_PLAYER_OP (opcode 0, varByte) × 6
    Sets the player right-click context menu options.
    Fields:
        padding         : ubyte     — always 0x00
        optionText      : cstring   — null-terminated CP1252 string
        slot            : ubyte     — option slot (0x83=Follow, 0x84=Trade, 0x85=null,
                                      0x86=Req Assist, 0x87=null, 0x88=Examine)
        priorityHigh    : ubyte     — observed: 0xFF
        priorityLow     : ubyte     — observed: 0x7F

    Standard options sent (in order):
        slot 3: "Follow"
        slot 4: "Trade with"
        slot 6: "Req Assist"
        slot 7: "null" (empty)
        slot 8: "Examine"
        slot 5: "null" (empty)
```

### Step 1.6: Audio

```
MIDI_SONG (opcode 87, 5 bytes fixed) × 2
    Starts background music for the scene.
    Fields:
        data            : bytes[5]  — audio parameters (song ID, fade, etc.)
```

### Step 1.7: Variable Reset & Reload

```
RESET_CLIENT_VARCACHE (opcode 48, 0 bytes)
    Clears all cached varps on the client. Sent before the bulk varp dump.

VARP_SMALL (opcode 10, 3 bytes) × ~2263
VARP_LARGE (opcode 111, 6 bytes) × ~635
VARP_BIT (opcode 50, 3 bytes) × ~7
    Full player variable state dump. Same format as lobby login.

    VARP_SMALL:
        id              : ushort_le — varp ID (little-endian)
        value           : byte      — signed value

    VARP_LARGE:
        value           : int_mid   — 32-bit value (middle-endian)
        id              : ushort_le — varp ID (little-endian)

    VARP_BIT:
        id              : ushort    — varp key
        value           : byte_sub  — signed value (subtract 0x80 transform)
```

### Step 1.8: Entity & NPC Setup

```
UNKNOWN_25 (opcode 25, 0 bytes)
    Purpose unconfirmed. Sent once, no payload.

NPC_OP (opcode 24, varByte, 0 bytes payload)
    NPC operation setup. Empty in this context (no NPCs in instanced scene).
```

### Step 1.9: Second Map Build (Tutorial Island)

```
UNKNOWN_86 (opcode 86, varShort, ~5518 bytes)
    Large map data packet. Handler is TcpIn-dispatched (special case).
    Likely REBUILD_NORMAL for the tutorial island map with XTEA keys.
    Sent after all varps are loaded.

CAMERA_UPDATE (opcode 46, varShort, ~121 bytes)
    Camera position/orientation for the character creation scene.
    Fields:
        cameraData      : bytes     — camera matrices, positions, angles
```

### Step 1.10: Social

```
UPDATE_IGNORELIST (opcode 211, varByte)
    Ignore list initialization. Empty list = 10 zero bytes.

UPDATE_ZONE_FULL_FOLLOWS (opcode 18, 3 bytes) × 768
    Clears all zone entities across the full 16×16×3 map grid.
    Fields:
        zoneOffsetX     : ubyte     — X offset within build area (0xE9-0xF8 range)
        zoneOffsetY     : ubyte     — Y offset within build area (0x88-0x97 range)
        level           : ubyte     — map level (0x7E/0x7F/0x80 = levels 0/1/2)

    Total: 16 X × 16 Y × 3 levels = 768 packets
```

---

## Phase 2: Character Creation UI

After the zone grid is cleared, the server opens the character creation interface.

### Step 2.1: Pre-Interface Scripts

```
RUNCLIENTSCRIPT (opcode 121, varShort) × 3
    Setup scripts run before the interface opens.

    Script 16300 (0x3FAC):  types="i", args=[0, 0, 0, 0]
    Script 671 (0x029F):    types="i", args=[0, 0, 0, 0]
    Script 20611 (0x5083):  types="", args=[]
```

### Step 2.2: Top-Level Interface

```
IF_SETTOPLEVELINTERFACE (opcode 94, 19 bytes)
    Opens the character creation root interface.
    Fields:
        padding         : ubyte     — 0x00
        interfaceId     : ushort    — 1349 (0x0545) = character creation root
        reserved        : bytes[16] — all zeros
```

### Step 2.3: Sub-Interface Layout

The server attaches sub-interfaces to the root. Each sub-interface is positioned via
IF_SETPOSITION and enabled via IF_SETEVENTS2.

```
IF_SETPOSITION (opcode 8, 23 bytes) × ~40
    Attaches a child interface to a parent component.
    (See docs/net/serverprot/interfaces.md for field layout)

IF_SETEVENTS2 (opcode 35, 12 bytes) × ~800+
    Enables click/interaction events on interface components.
    (See docs/net/serverprot/if-setevents.md for field layout)
```

**Key sub-interfaces (parentInterface = 1477 / 0x05C5):**

| Sub-Interface ID | Hex    | Purpose |
|-----------------|--------|---------|
| 1482            | 0x05CA | Main character customization panel |
| 1473            | 0x05C1 | Appearance options (gender, body, etc.) |
| 1464            | 0x05B8 | Clothing/color options |
| 1458            | 0x05B2 | Preset selector |
| 1461            | 0x05B5 | Equipment preview tabs |
| 1420            | 0x058C | Appearance cycle buttons (hair, face, etc.) |
| 1417            | 0x0589 | Skill/music tab (scrollable list) |
| 1414            | 0x058A | World map tab |
| 1349+subs       | varies | Nested panels for each customization category |

### Step 2.4: Tab Initialization Scripts

For each tab/panel, the server sends:

```
RUNCLIENTSCRIPT (opcode 121, varShort)
    Script 8862 (0x229E): types="ii", args=[enabled:int, tabIndex:int]
    Called once per tab with incrementing tabIndex (0, 2, 3, 4, 5, 9, 10, 11, 12, 14, 15, ...)
    enabled=1 for active tabs, enabled=0 for disabled/hidden tabs.
```

### Step 2.5: Client Variables for Creation

```
CLIENT_SETVARC_SMALL (opcode 1, 3 bytes)
CLIENT_SETVARC_STR (opcode 67, varByte)
    Various varcs controlling the character creation UI state.

    Example: varc 181 (0xB5) = 0 (music unlocked state)
    Example: varc string at 0x094C = "" (clear tooltip)
```

### Step 2.6: Music & Loading Text

```
IF_SETTEXT (opcode 2, varShort)
    Sets text on interface components.
    Fields:
        componentHash   : int       — packed interfaceId:componentId (big-endian)
        text            : cstring   — null-terminated CP1252 string

    Examples:
        Component 0x05880006: "Adventure" (music track name)
        Component 0x05890005: "Loading notes<br>Please wait..."
        Component 0x00BB0007: "Newbie Melody" (now-playing display)

IF_SETHIDE (opcode 103, 5 bytes)
    Shows/hides interface components.
    Fields:
        hideFlag        : ubyte     — 0x80=show, 0x81=hide (compared to 0x81)
        componentHash   : int       — packed interfaceId:componentId (g4s_alt2 transform)
```

---

## Phase 3: Game World Initialization (after character creation)

After the character creation interface, the server loads the actual game world.

### Step 3.1: IF_CLOSESUB & Cleanup

```
IF_CLOSESUB (opcode 33, 4 bytes) × 17
    Closes character creation sub-interfaces.
    Fields:
        componentId     : ushort    — component being closed
        interfaceId     : ushort    — parent interface (always 1477/0x05C5)

CLEAR_PENDING_UPDATES (opcode 142, 0 bytes)
    Flushes any pending zone/entity updates before scene transition.
```

### Step 3.2: Stats & Entity Init

```
UPDATE_STAT (opcode 66, 6 bytes) × 29
    Initializes all 29 skills to default values.
    Fields:
        xp              : int       — experience points (big-endian)
        level           : byte_sub  — current level (subtract transform)
        skillId         : byte_add  — skill index (add transform)

    New account values: xp=0 for most skills, level=1 (encoded as 0x7F)

SET_PLAYER_OP_2 (opcode 108, 2 bytes)
SET_PLAYER_OP_3 (opcode 116, 1 byte)
RESET_ENTITY_LISTS (opcode 43, 0 bytes)
SET_MULTIWAY_STATE (opcode 77, 1 byte)
    Standard world login entity setup.

CLANCHANNEL_FULL (opcode 28, varShort, 0 bytes payload)
    Empty clan channel (new account).
```

### Step 3.3: Camera Waypoints

```
CUTSCENE (opcode 91, 35 bytes) × 8
    Camera waypoint definitions for the tutorial intro sequence.
    Fields:
        padding         : ubyte     — 0x00
        waypointIndex   : ubyte     — 0-7 (sequential)
        waypointType    : ushort    — 0x0702 for all entries
        waypointData    : bytes[31] — all zeros (default positions)
```

### Step 3.4: Player Group & Sync

```
PLAYER_GROUP_FULL (opcode 109, varShort)
    Player group initialization (16 bytes observed).

UNKNOWN_172 (opcode 172, varShort, 93 bytes)
    Structured entity/player data. Contains counts and nested records.
    Possibly REBUILD_NORMAL for the tutorial area.
```

### Step 3.5: Phase Transition Markers

```
UNKNOWN_154 (opcode 154, 1 byte) — flags: 0xFF
UNKNOWN_213 (opcode 213, 1 byte) — flags: 0x7F
TRIGGER_ONDIALOGABORT (opcode 195, 0 bytes)
    Closes any open dialog before the game UI loads.
```

### Step 3.6: Game UI Setup

```
IF_SETTOPLEVELINTERFACE (opcode 94, 19 bytes)
    Opens the main game interface.
    Fields:
        padding         : ubyte     — 0x00
        interfaceId     : ushort    — 1420 (0x058C) = main game HUD
        reserved        : bytes[16] — all zeros

IF_SETPOSITION (opcode 8, 23 bytes) × many
IF_SETEVENTS2 (opcode 35, 12 bytes) × many
    Game HUD sub-interface layout (chat, minimap, inventory, etc.)
```

### Step 3.7: Inventory & Equipment

```
UPDATE_INV_FULL (opcode 69, varShort) × 15
    Initializes equipment/inventory containers for the character.
    Fields:
        inventoryId     : ushort    — container ID
        padding         : ushort    — 0x0000
        itemCount       : ubyte     — number of items
        [repeated itemCount times:]
            itemId      : ushort    — item ID (smart-encoded with 0x4D prefix)
            quantity    : ubyte     — stack size (0x01 for singles)

    Container IDs observed:
        592 (0x0250): equipment slot
        593 (0x0251): equipment slot
        ...etc (standard equipment containers)
```

### Step 3.8: Friends & Music

```
RUNCLIENTSCRIPT (opcode 121, varShort)
    Script 15557 (0x3CC5): types="sss"
        args=["Mimekitesay", "2dark2639", "Deadorbrage"]
        Sets initial friends list display names.

IF_SETTEXT (opcode 2, varShort)
    Sets "Newbie Melody" on the music player widget.
```

### Step 3.9: Anti-Cheat (Periodic)

```
ANTI_CHEAT_CHALLENGE (opcode 198, 8 bytes) — every ~6 seconds
    Server sends random challenge bytes.
    Fields:
        challenge1      : int       — random 32-bit value
        challenge2      : int       — random 32-bit value

    Client must respond with:
    ANTI_CHEAT_REPLY (ClientProt opcode 97, 9 bytes)
        sessionByte     : ubyte     — ~sessionIndex (bitwise NOT)
        response1       : int       — challenge1 bytes rearranged
        response2       : int       — challenge2 bytes rearranged
```

---

## Client Packets During Character Creation

| Packet | Opcode | Size | Purpose |
|--------|--------|------|---------|
| NO_TIMEOUT | 27 | 0 | Keepalive, sent every ~1 second |
| TRANSMITVAR_VERIFYID | 30 | 4 | Scene graph verification counter |
| CLIENT_DETAILOPTIONS_STATUS | 34 | varByte (58B) | Graphics settings report |
| EVENT_CAMERA_POSITION | 47 | 4 | Camera yaw/pitch after rotation |
| EVENT_KEYBOARD | 53 | varShort | Key events during name entry |
| RESUME_P_NAMEDIALOG | 84 | varByte | Typed display name (progressive) |
| IF_BUTTON1 | 96 | 8 | Appearance cycle clicks (interface 1420) |
| ANTI_CHEAT_REPLY | 97 | 9 | Response to server challenge |
| EVENT_MOUSE_MOVE | 105 | varByte | Delta-encoded mouse trajectory |
| EVENT_MOUSE_CLICK | 7 | 6 | Click coordinates + timing |
| WINDOW_STATUS | 108 | 6 | Window size/mode reports |

### Name Validation Flow

1. Server sends `RUNCLIENTSCRIPT(1246)` with prompt: `"Please enter a displayname."`
2. User types → client sends `RESUME_P_NAMEDIALOG` with each keystroke: `"m\0"`, `"me\0"`, `"mem\0"`
3. Server validates name, responds with `RUNCLIENTSCRIPT(1246)`:
   - Accepted: args = `(typedName, "", 0, 1)`
   - Rejected: args = `(typedName, "This name is already taken.", 0, 1)`
4. User continues typing → repeat from step 2
