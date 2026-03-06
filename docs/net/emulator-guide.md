# Server Emulator Implementation Guide

Step-by-step guide for implementing a RuneScape NXT 946-3 compatible server, ordered by implementation priority.

## Step 1: TCP Listener

Accept TCP connections on the game port. The client connects with:
- Read buffer: 1MB, Write buffer: 10KB, TCP_NODELAY enabled
- No TLS required (despite `hasSSL` field existing in ClientStream)

## Step 2: JS5 Cache Serving

The client connects to the JS5 port first, before login.

### Handshake
1. Client sends: `[version:int32] [subVersion:int32]` (946, 1)
2. Server responds: `[status:byte]` (0 = OK)

### Request/Response
- Client requests cache groups by archive + group ID
- Response format: see [js5-protocol.md](js5-protocol.md) — container header (compression type, lengths), compressed data
- Master index is RSA-signed with Whirlpool hash verification
- Compression: 0=none, 1=bzip2, 2=gzip, 3=LZMA

## Step 3: Login Protocol

See [login-protocol.md](login-protocol.md) for complete byte-level format.

### Summary
1. Client sends login request (LoginProt opcode)
2. Server responds with exchange data or error code
3. Client sends RSA-encrypted block containing:
   - 4 XTEA key ints (randomly generated)
   - Client version, token, credentials
4. Server RSA-decrypts to recover XTEA key
5. Client sends XTEA-encrypted credential block
6. Server XTEA-decrypts and validates credentials
7. Server sends login response (OK + session data, or error code)

### Isaac Initialization
After successful login:
- **Server→Client Isaac**: Seed with `[key0, key1, key2, key3]`
- **Client→Server Isaac**: Seed with `[key0+delta0, key1+delta1, key2+delta2, key3+delta3]`
  - Delta constants are at `0x00dc37d0` in the binary
- Both sides must consume Isaac values in lockstep (one per opcode byte)

### Response Codes
| Code | Meaning |
|------|---------|
| 0 | OK_EXCHANGE_DATA |
| 2 | OK |
| 3 | INVALID_CREDENTIALS |
| 5 | ALREADY_LOGGED_IN |
| 6 | CLIENT_OUTDATED |
| 7 | SERVER_FULL |
| 14 | QUEUE |
| 21 | TOTP_REQUIRED |

## Step 4: Core ServerProt Packets

These must be sent immediately after login succeeds.

### Required Post-Login Sequence
1. **REBUILD_NORMAL** (opcode 0xBA, var_short) — Sets the player's map region. Without this, the client has no map data.
2. **RESET_CLIENT_STATE** — Clears all pending state
3. **PLAYER_INFO** — Initial player list (at minimum, the local player)
4. **NPC_INFO_SMALL** or **NPC_INFO_LARGE** — Initial NPC list (can be empty)
5. **SET_VARP_***, **SET_VARBIT_*** — Send all active player variables
6. **IF_OPENTOP** — Open the main game interface

### Sending Packets
```
1. Pick opcode and look up fixedSize from framing.md table
2. Isaac-encrypt the opcode:
   - Opcodes 0-127:   wire = (opcode + isaac_val) & 0xFF
   - Opcodes 128-216: wire0 = (0x80 + isaac_val0) & 0xFF
                       wire1 = (opcode + isaac_val1) & 0xFF
3. If var_byte:  write 1 byte payload length
   If var_short: write 2 byte BE payload length
   If fixed:     no size header
4. Write payload bytes (NOT Isaac-encrypted)
```

## Step 5: Player Info Protocol

See [player-info.md](player-info.md) for complete bit-level format.

The PLAYER_INFO packet uses bit-packed encoding:
- **High-resolution**: Nearby players with full position data (movement types: stationary, walk, run, teleport)
- **Low-resolution**: Distant players with compressed region coordinates
- **Extended info blocks**: 23 flag bits for appearance, animation, hits, chat, forced movement, etc.
- **Appearance encoding**: Equipment slots, body colors, animation IDs, display name

Key: The local player MUST be in the high-resolution list.

## Step 6: NPC Info Protocol

See [npc-info.md](npc-info.md) for complete bit-level format.

Similar to Player Info but with NPC-specific fields:
- NPC type ID (from cache definitions)
- 28 extended info blocks (appearance, animation, hits, head icons, etc.)
- Two opcodes: NPC_INFO_SMALL (small worlds) and NPC_INFO_LARGE (large worlds)

## Step 7: Map & Zone System

### Rebuild
- **REBUILD_NORMAL**: Sends map region keys (XTEA-encrypted map data references)
- **REBUILD_REGION**: Sends custom region data for instanced areas

### Zone Updates
Zone-relative updates use a two-step process:
1. Send **UPDATE_ZONE_PARTIAL_FOLLOWS** or **UPDATE_ZONE_FULL_FOLLOWS** with zone coordinates
2. Follow with zone entity packets (LOC_ADD, OBJ_ADD, etc.) — these use the "clone" sub-opcodes

See [serverprot/zone-updates.md](serverprot/zone-updates.md) and [serverprot/clientstate.md](serverprot/clientstate.md).

## Step 8: Interface System

See [serverprot/interfaces.md](serverprot/interfaces.md).

Key operations:
- **IF_OPENTOP**: Open main game interface (game frame)
- **IF_OPENSUB**: Open sub-interfaces (tabs, panels, dialogs)
- **IF_SETTEXT/SETHIDE/SETOBJECT/SETMODEL**: Update component properties
- **IF_CLOSESUB**: Close interfaces

Component hashes: `(interfaceId << 16) | componentId`

## Step 9: Variable System

See [serverprot/variables.md](serverprot/variables.md).

Three variable types:
- **Varp** (Player Variables): Persistent per-player state (SET_VARP_SMALL/INT/LONG)
- **Varbit**: Bit-packed subfields within varps (SET_VARBIT_SMALL/INT)
- **Varc** (Client Variables): Client-side state (SET_VARC_SMALL/INT/STR/COORD)

## Step 10: Receiving Client Packets

See [clientprot-table.md](clientprot-table.md) for all opcodes.

### Decrypting Incoming Client Packets
```
1. Read opcode byte(s), Isaac-decrypt (subtract server's Isaac value)
2. If opcode < 128: single byte opcode
   If opcode >= 128: read second byte, combine
3. Look up fixed size or read variable length prefix
4. Read payload
```

### Essential Client Packets to Handle
| Packet | Opcode | Purpose |
|--------|--------|---------|
| NO_TIMEOUT | 15 | Keepalive (sent every 50 ticks) |
| MAP_BUILD_COMPLETE | 21 | Client finished loading map |
| MOVE_GAME | 102 | Player movement |
| CLOSE_MODAL | 87 | Close dialog |
| IF_BUTTON* | (via table) | Interface button clicks |
| OPLOC1-5 | 70, etc. | Location interactions |
| OPNPC1-6 | 26, 25, 23, 90, 77, 103 | NPC interactions |
| OPOBJ1-10 | 20, 46, 115, 96, 6, 60, 14, 59, 91, 30 | Object interactions |
| WINDOW_STATUS | 82 | Client window mode/size |

## Step 11: Chat & Social

See [serverprot/chat.md](serverprot/chat.md), [clans.md](clans.md), [social.md](social.md).

- **MESSAGE_GAME**: Server messages, filtered chat
- **MESSAGE_PRIVATE/ECHO**: Whisper system
- **MESSAGE_FRIENDCHAT/CLANCHANNEL**: Group chat
- **RUN_CLIENTSCRIPT**: Execute client-side scripts (used for many UI updates)

## Step 12: Audio & Camera

See [serverprot/audio.md](serverprot/audio.md), [serverprot/camera.md](serverprot/camera.md).

These are lower priority but needed for full client experience.

## Key Constants

| Constant | Value | Usage |
|----------|-------|-------|
| Client version | 946 | Login handshake |
| Sub-version | 1 | Login handshake |
| Max packets/tick | 100 | TcpIn loop limit |
| Keepalive interval | 50 ticks | NO_TIMEOUT packet |
| Isaac seed size | 4 ints | Login key exchange |
| XTEA rounds | 32 | Login encryption |
| XTEA delta | 0x9E3779B9 | Block cipher |
| Max opcode | 0xD8 (216) | ServerProt range |
| Max ClientProt | 129 | ClientProt range |
| Yield warning threshold | 500 | Handler yield counter |

## Implementation Order Summary

```
1. TCP listener
2. JS5 cache serving (master index, group responses)
3. Login protocol (RSA decrypt, XTEA decrypt, Isaac init)
4. REBUILD_NORMAL + PLAYER_INFO (minimum to get client "in-game")
5. NPC_INFO + zone updates (populate the world)
6. Interface system (IF_OPENTOP + IF_OPENSUB for game UI)
7. Variable system (varps/varbits for game state)
8. Client packet handling (movement, clicks, buttons)
9. Chat/social systems
10. Audio/camera (polish)
```

The minimum viable server needs steps 1-4. After step 4, the client will display the game world with the local player visible.
