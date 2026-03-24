# Lobby ServerProt Handler Mapping (Build 947-1)

> **Name convention update**: Some names in this doc use fabricated RE names.
> See `serverprot-table.md` for canonical names.

Binary: rs2client (rev 947-1, STRIPPED) — verified in Ghidra on port 8083.
Cross-referenced with: rs2client (rev 946-5) on port 8082, librs2client.so (rev ~890) on port 8080.

## Architecture: Lobby Handlers Share the Game ServerProt Table

**Critical finding:** Lobby handlers are NOT in a separate protocol table. They share the same `g_serverProtVector` (218 entries at `0x016ee0a0`) used during game phase. The `BindHandlers` function at `0x001185aa` binds handler functions to ServerProt entries, and the same entries serve both lobby and game phases.

### How Lobby Packets Work

During the lobby phase, the client uses TcpIn to dispatch incoming packets through `g_serverProtVector`, the exact same dispatch path as game-phase packets. Lobby-specific handlers are among the 218 ServerProt entries. There is no separate "lobby ServerProt table."

This means the server must register all 218 ServerProt opcodes even during the lobby phase (or at minimum, the client expects valid entries for all opcodes that could arrive during lobby).

## Confirmed Lobby Handler Mappings (rev 947-1)

Handlers identified from BindHandlers at `0x001185aa` with named handler functions, cross-referenced with RegisterAll at `0x00181e10` for opcodes/sizes.

### Definitively Matched

| Rev 947 Opcode | Size | Handler Name | Notes |
|----------------|------|--------------|-------|
| 0xD8 (216) | 0 (fixed) | **NO_TIMEOUT** | Trivial return handler (keepalive) |
| 0x1E (30) | varShort | **CHANGE_LOBBY** | handler: Lobby::CHANGE_LOBBY |
| 0x41 (65) | 0 (fixed) | **SET_READY_FLAG** | handler: ClientState::SET_READY_FLAG |
| 0x30 (48) | 0 (fixed) | **RESET_CLIENT_VARCACHE** | handler: ClientState::RESET_ALL_VARPS |

### Not Found in rev 946 (4 of 8)

The following old binary lobby handlers have no clear equivalent among the unnamed handlers in rev 946's `BindHandlers`:

| Old Handler Name | Old Signature | Status |
|-----------------|---------------|--------|
| **CREATE_ACCOUNT_REPLY** | Bitmask `0x23e01803fe3`, check `< 0x2a`, writes +0x30 | No matching handler found. Account creation may have been moved to a web-based flow in rev 946. |
| **CREATE_SUGGEST_NAME_ERROR** | Bitmask `0xe3`, check `< 8`, writes +0x3c, clears string | No matching handler found. |
| **CREATE_SUGGEST_NAME_REPLY** | Reads CP1252 string, writes +0x3c=2, assigns string to +0x40 | No matching handler found. |
| **LOBBY_APPEARANCE** | Calls `PlayerEntity::SetBaseAppearanceAsPlayer` | No direct handler found. `SetBaseAppearanceAsPlayer` (at `0x001f6220`) is called through a vtable chain: `FUN_00311530` -> `FUN_002eb240` -> `FUN_001f7760` -> `SetBaseAppearanceAsPlayer`. This may be triggered by the player info/update system rather than a dedicated lobby packet in rev 946. |

## All Unnamed Handlers in BindHandlers (Complete List)

Every ServerProt entry with an unnamed (FUN_) invoke handler, with opcode and size from `RegisterAll`:

| Opcode | Size | Entry Base | Invoke | Identification |
|--------|------|------------|--------|---------------|
| 0x05 | 10 | `0x016e93c0` | `FUN_00191b80` | SpotAnim/entity handler: reads int + byte + short + byte + short |
| 0x11 | varShort | `0x016e9200` | `FUN_00235bd0` | **CHANGE_LOBBY** (probable): reads CP1252 strings, builds vector |
| 0x12 | varShort | `0x016e91c0` | `FUN_002428f0` | `SiteSettings::UPDATE_SITESETTINGS` (Ghidra has type info) |
| 0x17 | 1 | `0x016e9100` | `FUN_001c6ae0` | Reads 1 byte to struct+0x50, calls sub functions, triggers interface update |
| 0x21 | 0 | `0x016e8f80` | `FUN_00192900` | Accesses client field, optionally calls `FUN_007ca570` |
| 0x31 | 10 | `0x016e8c80` | `FUN_00191a20` | SpotAnim/entity handler (identical pattern to 0x05) |
| 0x6D | varShort | `0x016e84c0` | `FUN_00198110` | ISAAC-decrypted chat/message: delegates to `FUN_0032a030` |
| 0x6F | varByte | `0x016e8440` | `FUN_0018cdb0` | Trivial return (NONE) — unused/placeholder |
| 0x85 | 1 | `0x016e8240` | `FUN_00212d90` | **CREATE_CHECK_EMAIL_REPLY** |
| 0x92 | 0 | `0x016e8000` | `FUN_00212df0` | **NO_TIMEOUT** |
| 0x9B | varByte | `0x016e7e80` | `FUN_001e0360` | `Misc::UPDATE_URL_STRING`: reads 4 bytes + CP1252 string |
| 0xA1 | 1 | `0x016e7dc0` | `FUN_002123a0` | Reads 1 byte + 0x80, writes to struct+0xa0 |
| 0xA6 | 0 | `0x016e7c80` | `FUN_00191ce0` | Accesses client field, calls `FUN_00cb8a50` |
| 0xA8 | varShort | `0x016e7c00` | `FUN_00244ab0` | VarPlayer handler: uses `ConfigProvider::GetVarPlayerTypeList` |
| 0xA9 | 1 | `0x016e7bc0` | `FUN_00212330` | Reads 1 byte + 0x80, writes to struct+0xa4 |
| 0xAA | varShort | `0x016e7b80` | `FUN_00211eb0` | Trivial return (NONE) — unused/placeholder |
| 0xB1 | varShort | `0x016e7a00` | `FUN_00198120` | ISAAC-decrypted chat/message: delegates to `FUN_00321810` |
| 0xB5 | 1 | `0x016e7980` | `FUN_00212930` | Reads 1 byte, sets boolean flag, increments counter |
| 0xBB | 4 | `0x016e78c0` | `FUN_00225ea0` | Reads 4 bytes (mixed-endian int), writes to struct+0x40, triggers script |
| 0xC4 | varShort | `0x016e7700` | `FUN_0018cdc0` | Trivial return (NONE) — unused/placeholder |
| 0xCC | 1 | `0x016e7580` | `FUN_00212c80` | **CREATE_CHECK_NAME_REPLY** |

## Old Binary Handler Reference

From `jag::packethandlers::Lobby::Lobby(jag::Client&)` in librs2client.so (rev ~890):

| Lambda | ServerProt Name | Old Opcode | Old Size | Invoke Address | Behavior |
|--------|----------------|------------|----------|----------------|----------|
| #1 | NO_TIMEOUT | 0xBB | 0 | `0x001a6900` | Returns `PacketError::NONE` immediately |
| #2 | CREATE_CHECK_EMAIL_REPLY | 0x60 | 1 | `0x001a4940` | Bitmask `0x1800063`, writes `client+0x7d8+0x34` |
| #3 | CREATE_ACCOUNT_REPLY | 0x80 | 1 | `0x001a48e0` | Bitmask `0x23e01803fe3`, writes `client+0x7d8+0x30` |
| #4 | CREATE_CHECK_NAME_REPLY | 0x6C | 1 | `0x001a4890` | Bitmask `0xfe3`, writes `client+0x7d8+0x38` |
| #5 | CREATE_SUGGEST_NAME_ERROR | 0xA3 | 1 | `0x001a4820` | Bitmask `0xe3`, writes `+0x3c`, clears string at `+0x40` |
| #6 | CREATE_SUGGEST_NAME_REPLY | 0xBF | varShort | `0x004bda90` | Reads CP1252 string, writes `+0x3c`=2, assigns string to `+0x40` |
| #7 | LOBBY_APPEARANCE | 0x75 | varShort | `0x0051f710` | Skips 1 byte, calls `PlayerEntity::SetBaseAppearanceAsPlayer` |
| #8 | CHANGE_LOBBY | 0x1E | varShort | `0x004bd250` | Reads CP1252 string + 3 unsigned shorts (worldId, port1, port2) |

## Zone Update Sub-Opcodes (g_clientProtVector)

The 18 entries in `g_clientProtVector` at `0x016e9fc0` are zone update sub-opcodes, NOT lobby entries. They are dispatched by `UPDATE_ZONE_PARTIAL` during game phase. Registered via `ClientProt::InitEntry` at `0x00181da0`.

| Sub-Opcode | Size | Entry Address | Zone Update Type |
|------------|------|--------------|-----------------|
| 0 | 11 | `0x016fe560` | LOC_ANIM |
| 1 | varByte | `0x016feaa0` | LOC_CUSTOMISE |
| 2 | 5 | `0x016fe7a0` | OBJ_ADD |
| 3 | 3 | `0x016fe720` | OBJ_DEL |
| 4 | 14 | `0x016fe2e0` | MAP_ANIM_SPECIFIC |
| 5 | 7 | `0x016fe9a0` | LOC_PREFETCH |
| 6 | 20 | `0x016fe920` | MAP_PROJANIM |
| 7 | 7 | `0x016fe6a0` | OBJ_COUNT |
| 8 | 11 | `0x016fe820` | MAP_ANIM |
| 9 | 2 | `0x016fea20` | LOC_DEL |
| 10 | 10 | `0x016fe5a0` | LOC_ANIM_SPECIFIC |
| 11 | 28 | `0x016fe3e0` | MAP_PROJANIM_HALT |
| 12 | 29 | `0x016fe360` | PROJANIM_SPECIFIC_HALT |
| 13 | 5 | `0x016fe4e0` | LOC_MERGE |
| 14 | varByte | `0x016fe460` | SOUND_AREA |
| 15 | 21 | `0x016fe8a0` | PROJANIM_SPECIFIC |
| 16 | varByte | `0x016feb20` | LOC_ADD |
| 17 | 7 | `0x016fe620` | OBJ_REVEAL |

## Implementation Priority for Lobby

To get the client into the lobby screen, the server must handle:

1. **Login protocol** (see `login-wire-format.md`) — complete the login handshake
2. **NO_TIMEOUT** (opcode 0x92, size 0) — respond to client keepalive pings
3. **CHANGE_LOBBY** (opcode 0x11, varShort) — send world list/lobby data
4. Potentially **LOBBY_APPEARANCE** — player preview (may be handled through player info system)

The 5 account creation packets (CREATE_CHECK_EMAIL_REPLY, CREATE_ACCOUNT_REPLY, etc.) are only needed if implementing in-client account creation. In rev 946, some or all of these may have been removed (web-based account creation).

## Login Protocol Step Opcodes

Registered via `jag::LoginProt::InitEntry` at `0x00181e00`. Active during login handshake only.

| Opcode | Hex | Size | Ghidra Label |
|--------|-----|------|-------------|
| 14 | 0x0E | 0 | `LoginProt::STEP_0E` |
| 15 | 0x0F | varByte (-1) | `LoginProt::STEP_0F` |
| 16 | 0x10 | varShort (-2) | `LoginProt::STEP_10` |
| 19 | 0x13 | varShort (-2) | `LoginProt::STEP_13` |
| 23 | 0x17 | 4 | `LoginProt::STEP_17` |
| 24 | 0x18 | varByte (-1) | `LoginProt::STEP_18` |
| 26 | 0x1A | 0 | `LoginProt::STEP_1A` |
| 27 | 0x1B | 0 | `LoginProt::STEP_1B` |
| 28 | 0x1C | varShort (-2) | `LoginProt::STEP_1C` |
| 29 | 0x1D | varShort (-2) | `LoginProt::STEP_1D` |
| 30 | 0x1E | varShort (-2) | `LoginProt::STEP_1E` |
| 31 | 0x1F | 4 | `LoginProt::STEP_1F` |
