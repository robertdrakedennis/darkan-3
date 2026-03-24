# ServerProt: ClientState Category

> **Rev 947-1**: Opcodes reshuffled from 946. See `serverprot-table.md` for current opcode/size table.

## Overview

The ClientState packet handler category manages map rebuilds, world entity operations, minimap toggling, zone data lifecycle, and connection state flags. The primary constructor is at `0x0014d50c` (`jag::packethandlers::ClientState::ClientState`), which binds 12 lambda handlers to ServerProt globals in the address range `0x016f41e0` - `0x016f44a0`. An additional 4 ClientState handlers (CLEAR_PENDING_UPDATES, DESTROY_ZONE_DATA, RESET_CLIENT_STATE, UPDATE_ZONE_PARTIAL) are registered by the Variables constructor at `0x0014ce1e`. Two more handlers (SET_TICK_TIMER, SET_READY_FLAG) are registered inline in `BindHandlers` (0x0011852a).

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success), `&DAT_016fb220` (yield), `&DAT_016fb1e0` (error)

## ServerProt Opcode Table

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|--------------|-----------------|-------------------|
| 22 | 2 | SET_TICK_TIMER | `0x002141c0` | `0x016faea0` |
| 32 | 0 | RESET_CLIENT_STATE | `0x00191560` | `0x016f4560` |
| 35 | 0 | SET_READY_FLAG | `0x00213e90` | `0x016fac60` |
| 65 | var-byte | UPDATE_ZONE_PARTIAL | `0x001c17f0` | `0x016f4520` |
| 104 | 0 | DESTROY_ZONE_DATA | `0x0018f890` | `0x016f45a0` |
| 129 | 6 | SET_MAP_FLAG | `0x0019a840` | `0x016f42a0` |
| 142 | 1 | WORLDENTITY_INFO_V4 | `0x0018fe20` | `0x016f43e0` |
| 151 | 3 | MINIMAP_TOGGLE | `0x0018fbb0` | `0x016f41e0` |
| 153 | 2 | WORLDENTITY_INFO_V2 | `0x001b9ef0` | `0x016f4360` |
| 156 | 6 | WORLDENTITY_INFO_V1 | `0x001ba0e0` | `0x016f43a0` |
| 157 | 3 | WORLDENTITY_INFO_V3 | `0x0018fd20` | `0x016f4320` |
| 178 | var-short | REBUILD_WORLDENTITY | `0x00190120` | `0x016f4460` |
| 186 | var-short | REBUILD_NORMAL | `0x001bfd00` | `0x016f44a0` |
| 191 | 2 | CLEAR_MAP_FLAG | `0x0019a710` | `0x016f4260` |
| 206 | 0 | CLEAR_PENDING_UPDATES | `0x00190e50` | `0x016f45e0` |
| 208 | 3 | WORLDENTITY_INFO_V5 | `0x001b9e40` | `0x016f42e0` |
| 211 | 5 | REBUILD_REGION | `0x001bfbe0` | `0x016f4420` |
| 215 | 3 | REORDER_MAP_FLAG | `0x0019a420` | `0x016f4220` |

## Key Data Structures

### Build Area Entry (0x68 bytes)

World entity build data is stored in a vector of 0x68-byte entries. Each entry represents a world entity's map configuration:

| Offset | Size | Type | Description |
|--------|------|------|-------------|
| 0x00 | 8 | long* | Unknown pointer |
| 0x08 | 8 | long* | Region tile data (allocated array) |
| 0x20 | 24 | vector<uint> | Zone IDs (map square identifiers) |
| 0x38 | 24 | vector<int> | Sort order indices (-1 = unset, 0xFFFFFFFF = sentinel) |
| 0x50 | 24 | vector<xor_group> | XOR group data (0x18 per entry, nested uint arrays) |

### Zone Data Object (0x38 bytes)

Allocated by RESET_CLIENT_STATE and stored at `ClientVarDomain+0x5238`:

| Offset | Size | Type | Description |
|--------|------|------|-------------|
| 0x00 | 8 | void** | Vtable pointer (`PTR_FUN_01493600`) |
| 0x08 | 8 | void* | Map data structure |
| 0x10 | 8 | void* | Secondary data pointer (`DAT_014be8e0`) |
| 0x18 | 8 | long | Reference count (initialized to 1) |
| 0x20 | 8 | - | Zero-initialized |
| 0x28 | 8 | float[2] | `{1.0f, 2.0f}` (packed as 0x400000003f800000) |
| 0x30 | 4 | int | Zero-initialized |

---

## Handler Reference

### Map Rebuild Operations

#### RESET_CLIENT_STATE (opcode 32, size 0)
**Handler Address:** `0x00191560` | **ServerProt Global:** `0x016f4560`

**Packet Format:** No data (zero-length packet).

**Behavior:** Allocates a new zone data object (0x38 bytes) with vtable `PTR_FUN_01493600`, initializes it with default values (reference count = 1, float pair = {1.0, 2.0}), and stores it at `ClientVarDomain+0x5238`. If a previous zone data object exists, calls its destructor via `vtable+0x08`.

---

#### DESTROY_ZONE_DATA (opcode 104, size 0)
**Handler Address:** `0x0018f890` | **ServerProt Global:** `0x016f45a0`

**Packet Format:** No data (zero-length packet).

**Behavior:** Clears the zone data object pointer at `ClientVarDomain+0x5238` to zero. If the previous pointer was non-null, calls the destructor via `vtable+0x08`. This is the inverse of RESET_CLIENT_STATE.

---

#### REBUILD_NORMAL (opcode 186, size var-short)
**Handler Address:** `0x001bfd00` | **ServerProt Global:** `0x016f44a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| Outer loop | byte | Build area count (0 = done) |
| Per build area: g4 BE | uint | Map square ID |
| Per build area: g1 | byte | Zone count |
| Per zone: g4 BE | uint | Zone ID |
| Per zone: sorted indices | varies | Sort index data |
| Per zone per tile: g1 + optional g4 BE | byte + uint | Flag byte (0 = sentinel 0x80000000, non-zero = g4 BE value) |

**Behavior:** Full map rebuild. Clears all existing 0x68-sized build area entries (freeing sub-arrays and XOR group data), then iterates through the packet data to reconstruct the map: reads zone IDs, allocates XOR group data, reads tile data arrays with sort indices. Calls `FUN_007d63c0` (load map) and `FUN_007d6840` (finalize) for each build area. This is the primary map loading packet sent on login and teleport.

---

#### REBUILD_REGION (opcode 211, size 5)
**Handler Address:** `0x001bfbe0` | **ServerProt Global:** `0x016f4420`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 4 bytes (custom LE) | uint | Map square ID (byte-swapped: b0*0x100 + b2*0x1000000 + b3*0x10000 + b1) |
| g1_sub128 | byte | Build area index (-128 transform) |

**Behavior:** Rebuilds a single region within an existing build area. Reads the map square ID with a custom 4-byte little-endian ordering, then calls `FUN_007d63c0` (load map) + `FUN_007d6840` (finalize) targeting the specific build area index. Used for incremental map updates when moving between regions.

---

#### REBUILD_WORLDENTITY (opcode 178, size var-short)
**Handler Address:** `0x00190120` | **ServerProt Global:** `0x016f4460`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| Triple-nested loop (sentinel-terminated, -1 = 0xFF) | | |
| Outer: g1 | byte | World entity index (-1 = end) |
| Middle: g1 | byte | Zone index (-1 = end) |
| Inner: g1 | byte | Tile index (-1 = end) |
| Per tile: g4 BE | uint | Tile data value |

**Behavior:** Populates world entity location data using a triple-nested sentinel-terminated loop. For each (worldEntity, zone, tile) triple, reads a big-endian 4-byte value and stores it in the XOR group data array at `buildArea[worldEntity].xorGroup[zone][tile]`. Uses endianness-aware decoding (checks `DAT_011591e0 == 0x3020100` for little-endian).

---

### World Entity Management

#### WORLDENTITY_INFO_V1 (opcode 156, size 6)
**Handler Address:** `0x001ba0e0` | **ServerProt Global:** `0x016f43a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Insertion position (-128 transform, -1 = append) |
| g1 | byte | World entity index |
| g4 BE | uint | Map square ID (big-endian) |

**Behavior:** Adds a map square to a world entity's zone vector (+0x20). Inserts at the specified position (or appends if position is -1). Also inserts a sentinel sort entry (-1) at the corresponding position in the sort array (+0x38), and allocates a new XOR group data entry (+0x50). After insertion, re-validates sort indices by calling `FUN_007d6150` for any out-of-order entries.

---

#### WORLDENTITY_INFO_V2 (opcode 153, size 2)
**Handler Address:** `0x001b9ef0` | **ServerProt Global:** `0x016f4360`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_add128 | byte | Position (+128 transform) |
| g1_sub128 | byte | World entity index (-128 transform) |

**Behavior:** Removes a map square from a world entity. Removes the entry at the specified position from the zone vector (+0x20), sort array (+0x38), and XOR group data (+0x50) using memmove to shift remaining entries. Frees the removed XOR group's allocated memory. After removal, re-validates sort indices.

---

#### WORLDENTITY_INFO_V3 (opcode 157, size 3)
**Handler Address:** `0x0018fd20` | **ServerProt Global:** `0x016f4320`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Position (-128 transform) |
| g1 | byte | World entity index |
| g1 | byte | Flag value |

**Behavior:** Sets a sort order entry for a world entity. If the flag byte equals `0x7F`, sets the sort value to the position index; otherwise sets it to -1 (effectively clearing the entry). Writes to the sort array at `buildArea[worldEntity].sortArray[position]` (offset +0x38).

---

#### WORLDENTITY_INFO_V4 (opcode 142, size 1)
**Handler Address:** `0x0018fe20` | **ServerProt Global:** `0x016f43e0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1s | byte (signed) | World entity index |

**Behavior:** Removes an entire world entity from the build area vector. Reads the index as a signed byte, then shifts all entries after the removed one in the 0x68-sized entry vector (using element-by-element copy and swap), freeing sub-arrays (zone IDs, sort indices, XOR groups) for the removed entry. Decrements the vector size by one entry (0x68 bytes).

---

#### WORLDENTITY_INFO_V5 (opcode 208, size 3)
**Handler Address:** `0x001b9e40` | **ServerProt Global:** `0x016f42e0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Position (-128 transform) |
| g1 | byte | Unknown parameter |
| g1_add128 | byte | World entity index (+128 transform) |

**Behavior:** Reorders world entity data entries. Calls `FUN_007d6150` to swap/reorder entries at the given positions, then iterates the sort array and updates all non-sentinel entries to sequential indices (0, 1, 2, ...), ensuring sort order consistency after the reorder.

---

### Map Flag Operations

#### SET_MAP_FLAG (opcode 129, size 6)
**Handler Address:** `0x0019a840` | **ServerProt Global:** `0x016f42a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | World entity index |
| g1_sub128 | byte | Insertion position (-128 transform, -1 = append) |
| 4 bytes (custom LE) | uint | Map ID (3-byte LE packed: b0 + b1*0x100 + b2*0x10000 + b3*0x1000000) |

**Behavior:** Adds a region map square to a world entity. Checks for duplicates and maximum capacity (8 entries, 0x20 bytes at 4 bytes each). Inserts into the region vector (+0x08/+0x10/+0x18) at the specified position. Also inserts sentinel entries (0x80000000) into all parallel XOR group data arrays (+0x50) at the corresponding position, growing them with reallocation as needed.

---

#### CLEAR_MAP_FLAG (opcode 191, size 2)
**Handler Address:** `0x0019a710` | **ServerProt Global:** `0x016f4260`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_add128 | byte | Position (+128 transform) |
| g1_sub128 | byte | World entity index (-128 transform) |

**Behavior:** Removes a region map square from a world entity. Removes from the region vector (+0x08) using memmove, then iterates all parallel XOR group arrays (+0x50) and removes the corresponding element from each, shrinking them.

---

#### REORDER_MAP_FLAG (opcode 215, size 3)
**Handler Address:** `0x0019a420` | **ServerProt Global:** `0x016f4220`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_add128 | byte | New position (+128 transform) |
| g1 | byte | Old position (negated) |
| g1_sub128 | byte | World entity index (-128 transform) |

**Behavior:** Reorders a region map square within a world entity. Removes the entry at the old position from the region vector (+0x08) and reinserts it at the new position. Also performs the same remove+reinsert operation on all parallel XOR group data arrays (+0x50), preserving the values during the move.

---

### Zone Operations

#### UPDATE_ZONE_PARTIAL (opcode 65, size var-byte)
**Handler Address:** `0x001c17f0` | **ServerProt Global:** `0x016f4520`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g2 BE | ushort | Zone type ID (big-endian, byte-swapped on LE) |
| (variable) | - | Zone-type-specific data (read via virtual call) |

**Behavior:** Partial zone update. If no zone data object exists at `ClientVarDomain+0x5238`, creates one (same as RESET_CLIENT_STATE). Looks up the zone type config from a type manager via virtual call (`vtable+0x40`), then reads zone data from the packet via another virtual call (`vtable+0x20`) on the type's reader object. Stores the result in the zone data map. Tracks modified zone IDs in a circular buffer at `ClientVarDomain+0x88/0x90` (64-entry ring buffer, 4 bytes per entry).

---

#### CLEAR_PENDING_UPDATES (opcode 206, size 0)
**Handler Address:** `0x00190e50` | **ServerProt Global:** `0x016f45e0`

**Packet Format:** No data (zero-length packet).

**Behavior:** Iterates over pending client var updates stored in a vector at `ClientVarDomain+0x5170/0x5178`. Each update entry is 0x28 bytes with a type byte at offset 0x20. For each entry, if the type is not 4 (unset) or 0xFF, calls a type-specific cleanup function from a dispatch table at `PTR_FUN_014ada60`. Resets the type to 4 (unset). After processing all entries, sets the update count at `ClientVarDomain+0x5188` to zero and resets the vector end pointer to match the start.

---

### Minimap

#### MINIMAP_TOGGLE (opcode 151, size 3)
**Handler Address:** `0x0018fbb0` | **ServerProt Global:** `0x016f41e0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Position 1 (-128 transform) |
| g1_sub128 | byte | Position 2 (-128 transform) |
| g1_sub128 | byte | World entity index (-128 transform) |

**Behavior:** Sets a visibility/toggle flag in a world entity's XOR group data. Writes the sentinel value `0x80000000` at `buildArea[worldEntity].xorGroup[pos1][pos2]`, effectively clearing/hiding that entry. Used to toggle minimap tile visibility for specific world entity positions.

---

### Connection State

#### SET_TICK_TIMER (opcode 22, size 2)
**Handler Address:** `0x002141c0` | **ServerProt Global:** `0x016faea0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g2 BE | ushort | Timer value (big-endian, byte-swapped on LE) |

**Behavior:** Sets the client's tick timer duration. Reads a 2-byte unsigned short, then multiplies by a state-dependent factor:
- **Game state 0x14** (loading/in-game): `(value * 5 * 5) / 10` = effectively `value * 2.5` (rounded to integer)
- **Other states**: `value * 30`

The computed duration is stored at `ConnectionManager+0x0C`. After setting the timer, copies a counter from the connection state at `ClanManager+0xF8 → +0x10` into `ClanManager+0x100`.

**Client References:**
- `__DT_SYMTAB[0x4dc]`: Game state check (== 0x14 for loading/in-game)
- Connection manager pointer: via `__DT_SYMTAB` offset from client
- `__DT_SYMTAB[0x49c]`: Clan system manager (counter sync)

---

#### SET_READY_FLAG (opcode 35, size 0)
**Handler Address:** `0x00213e90` | **ServerProt Global:** `0x016fac60`

**Packet Format:** No data (zero-length packet).

**Behavior:** Sets a ready/loaded flag to 1. This is a minimal handler that reads no packet data:

1. Sets `*(int*)(readyFlagPtr + 0x10) = 1` (via `__DT_SYMTAB[0x49a]`)
2. Copies a counter from `ClanManager+0xE8 → +0x10` into `ClanManager+0xF0`

The ready flag likely signals to the server that the client has finished processing a map rebuild or state transition.

**Client References:**
- `__DT_SYMTAB[0x49a]`: Ready flag structure (+0x10 = flag value)
- `__DT_SYMTAB[0x49c]`: Clan system manager (counter at +0xE8/+0xF0)

---

## Byte Transform Reference

Many handlers apply byte transforms when reading single-byte values:

| Transform | Formula | Description |
|-----------|---------|-------------|
| g1_sub128 | `value - 128` (equivalently `0x80 - value`) | Subtracts 128, used for signed-range encoding |
| g1_add128 | `value + 128` (equivalently `value + 0x80`) | Adds 128 |
| g1s | Signed byte cast | Reads as signed char, sign-extended to int |
| g1 (negated) | `-(value & 0xFF)` | Negates the unsigned byte |

These transforms are RuneScape's standard packet obfuscation for single-byte values, making raw packet captures harder to interpret without knowing the specific handler.
