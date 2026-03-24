# ServerProt: ZoneUpdates Category

> **Rev 947-1**: Opcodes reshuffled from 946. See `serverprot-table.md` for current opcode/size table.

Packet handlers for zone-specific map updates. These handlers are registered by
`jag::packethandlers::ZoneUpdates::ZoneUpdates(jag::Client &)` at address `0x0014c4c8`.

Called from `BindHandlers` at `0x0011b9fd`.

## Architecture

### Clone Registration Pattern

18 of the 21 handlers have **clone registrations** via `FUN_00920170`. Each cloned handler
is registered to TWO ServerProt opcodes:

- **Primary opcode**: The standalone opcode used outside of batch zone updates.
- **Clone opcode**: A low-numbered opcode (0x00-0x11) used as a sub-opcode within
  `UPDATE_ZONE_PARTIAL` batch packets.

The 3 handlers without clones (`UPDATE_ZONE_PARTIAL_FOLLOWS`, `UPDATE_ZONE_FULL_FOLLOWS`,
and `LOC_ANIM`) only operate standalone.

### Zone Coordinate Globals

Zone-based handlers operate relative to a "current zone" defined by three globals:

| Address        | Name           | Description                    |
|----------------|----------------|--------------------------------|
| `0x014bf288`   | `g_zoneLevel`  | Current zone level (plane)     |
| `0x014bf28c`   | `g_zoneX`      | Current zone X coordinate      |
| `0x014bf290`   | `g_zoneY`      | Current zone Y coordinate      |

These are set by `UPDATE_ZONE_PARTIAL_FOLLOWS` and `UPDATE_ZONE_FULL_FOLLOWS` before
zone-relative handlers execute.

### Packed Coordinate Byte

Most zone handlers read a 1-byte "packed coord" that encodes position within the zone:

```
bits [0:2]  = localY (0-7, added to g_zoneY)
bits [4:6]  = localX (0-7, added to g_zoneX)
bit  [7]    = above-ground flag (used by some handlers)
```

### Sub-Opcode Dispatch Table

`UPDATE_ZONE_PARTIAL` uses a secondary dispatch table at `DAT_016fbd20` to map sub-opcodes
(0x00-0x11) to their handler functions. The clone opcodes correspond to entries in this table.

## Key Addresses & Constants

| Address      | Name                      | Description                            |
|--------------|---------------------------|----------------------------------------|
| `0x016fb240`  | `PACKET_HANDLER_SUCCESS` | Return value for successful handling   |
| `0x016fbd20`  | `g_zoneSubOpcodeTable`   | Sub-opcode dispatch table (max 0x11)   |
| `0x014bf288`  | `g_zoneLevel`            | Current zone level (plane)             |
| `0x014bf28c`  | `g_zoneX`                | Current zone X coordinate              |
| `0x014bf290`  | `g_zoneY`                | Current zone Y coordinate              |

## Handler Registration Table

| # | Handler Address | Primary Op | Size | Clone Op | Handler Name |
|---|-----------------|-----------|------|----------|--------------|
| 1 | `0x0018f500` | `0x78` | 3 | -- | `UPDATE_ZONE_PARTIAL_FOLLOWS` |
| 2 | `0x001989b0` (thunk) | `0x5A` | 3 | -- | `UPDATE_ZONE_FULL_FOLLOWS` |
| 3 | `0x0018f370` | `0x2B` | -2 (var_short) | -- | `UPDATE_ZONE_PARTIAL` |
| 4 | `0x001e46b0` | `0x1E` | -1 (var_byte) | `0x10` | `LOC_ADD` |
| 5 | `0x001dd650` (thunk) | `0x52` | -1 (var_byte) | `0x01` | `LOC_CUSTOMISE` |
| 6 | `0x001dc500` (thunk) | `0x40` | 2 | `0x09` | `LOC_DEL` |
| 7 | `0x001e4520` | `0x4E` | 7 | `0x05` | `LOC_PREFETCH` |
| 8 | `0x00193e10` | `0x18` | 20 | `0x06` | `MAP_PROJANIM` |
| 9 | `0x00193ae0` | `0xD6` | 21 | `0x0F` | `PROJANIM_SPECIFIC` |
| 10 | `0x00206a70` | `0x7A` | 11 | `0x08` | `MAP_ANIM` |
| 11 | `0x001b9550` | `0x57` | 5 | `0x02` | `OBJ_ADD` |
| 12 | `0x001fa050` | `0x62` | 3 | `0x03` | `OBJ_DEL` |
| 13 | `0x001b9410` | `0x03` | 7 | `0x07` | `OBJ_COUNT` |
| 14 | `0x00194cd0` | `0x7F` | 7 | `0x11` | `OBJ_REVEAL` |
| 15 | `0x00192ac0` | `0x06` | 10 | `0x0A` | `LOC_ANIM_SPECIFIC` |
| 16 | `0x001928b0` | `0x00` | 11 | -- | `LOC_ANIM` |
| 17 | `0x0018ef50` | `0x84` | 5 | `0x0D` | `LOC_MERGE` |
| 18 | `0x001cc090` (thunk) | `0xC6` | -1 (var_byte) | `0x0E` | `SOUND_AREA` |
| 19 | `0x00193770` | `0x9F` | 28 | `0x0B` | `MAP_PROJANIM_HALT` |
| 20 | `0x001933f0` | `0x93` | 29 | `0x0C` | `PROJANIM_SPECIFIC_HALT` |
| 21 | `0x002065e0` | `0xC2` | 14 | `0x04` | `MAP_ANIM_SPECIFIC` |

---

## Handler Details

### UPDATE_ZONE_PARTIAL_FOLLOWS (opcode 0x78, 3 bytes)

**Address:** `0x0018f500`

Sets the current zone coordinates for subsequent zone-relative handlers.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | level | Zone level (plane) |
| 1 | 1 | byte | zoneX | Zone X coordinate |
| 2 | 1 | byte | zoneY | Zone Y coordinate |

**Behavior:** Writes the 3 values to `g_zoneLevel`, `g_zoneX`, `g_zoneY` globals. This
packet always precedes a sequence of standalone zone update packets that operate relative
to these coordinates.

---

### UPDATE_ZONE_FULL_FOLLOWS (opcode 0x5A, 3 bytes)

**Address:** `0x001989b0` (via thunk)

Sets zone coordinates AND clears all zone entities in that zone.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | level | Zone level (plane) |
| 1 | 1 | byte | zoneX | Zone X coordinate |
| 2 | 1 | byte | zoneY | Zone Y coordinate |

**Behavior:** Sets zone globals, then iterates through all zone entity types (locations,
ground items, SpotAnims) in that zone and clears/removes them. Used when entering a new
area to reset the zone state before receiving fresh data.

---

### UPDATE_ZONE_PARTIAL (opcode 0x2B, var_short)

**Address:** `0x0018f370`

Batch zone update dispatcher. Contains multiple sub-packets for a single zone.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | level | Zone level |
| 1 | 1 | byte | zoneX | Zone X coordinate |
| 2 | 1 | byte | zoneY | Zone Y coordinate |
| 3+ | var | -- | sub-packets | Sequence of sub-opcode + payload pairs |

**Sub-packet format:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | subOpcode | Index into DAT_016fbd20 (max 0x11) |
| 1+ | var | -- | payload | Sub-handler-specific payload |

**Behavior:** Sets zone globals, then loops reading 1-byte sub-opcodes from the
dispatch table at `DAT_016fbd20`. Each sub-opcode dispatches to the clone variant of
the corresponding zone handler. Processing continues until all packet data is consumed.

---

### LOC_ADD (opcode 0x1E / clone 0x10, var_byte)

**Address:** `0x001e46b0`

Adds a new location (game object) to the scene at a zone-relative position.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | flags | Location flags |
| 1 | 4 | g4s_alt3 | locId | Location type ID (signed smart) |
| 5 | 1 | byte | shapeAndCoord | Shape/coord encoding (-128 transform) |
| 6 | 1 | byte | packedCoord | Zone-relative position (localX<<4 | localY, bits) |

**Behavior:** Parses shape data via `FUN_00bc1090`, computes zone-relative coordinates
from packed coord and zone globals, creates a Location object via `FUN_001dbe00` and adds
it to the scene tree. Sets additional timing metadata on the created object.

---

### LOC_DEL (opcode 0x40 / clone 0x09, 2 bytes)

**Address:** `0x001dc500` (via thunk)

Removes a location from the scene.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 1 | byte | shapeRotation | Shape type and rotation |

**Behavior:** Extracts shape and rotation from the second byte, computes zone-relative
coordinates from packed coord, then searches the scene tree for a matching location
and removes it.

---

### LOC_CUSTOMISE (opcode 0x52 / clone 0x01, var_byte)

**Address:** `0x001dd650` (via thunk)

Customizes a location's appearance with replacement models and recolors.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | var | -- | bitmask | Flags byte (bits 1,2,4,8) |
| var | var | uint[] | modelIds | Replacement model IDs (when bit set) |
| var | var | ushort[] | recolors | Source/dest color pairs (when bit set) |

**Behavior:** Reads a bitmask controlling which customization types are present.
For each set bit, reads arrays of model IDs (4 bytes each) or recolor pairs
(2 bytes each, source+dest). Applies the customization to the target location.

---

### LOC_PREFETCH (opcode 0x4E / clone 0x05, 7 bytes)

**Address:** `0x001e4520`

Pre-loads a location that will appear at a specific time.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | flags | Location flags |
| 1 | 4 | g4s_alt3 | locId | Location type ID |
| 5 | 1 | byte | shapeAndCoord | Shape/coord encoding |
| 6 | 1 | byte | packedCoord | Zone-relative position |

**Behavior:** Creates a location like LOC_ADD but with additional timing metadata:
sets client cycle (+0x70), a time reference pointer (+0x78), and a cycle delta (+0x6c)
on the created location object. The location becomes visible at the specified time.

---

### LOC_ANIM (opcode 0x00, 11 bytes, standalone only)

**Address:** `0x001928b0`

Plays an animation on a location.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 4 | uint (BE) | animId | Animation ID |
| 5 | 1 | byte | shapeFlags | Shape type (bits 0-2) + height (bits 4-7) |
| 6 | 1 | byte | unknown1 | Unknown parameter |
| 7 | 1 | byte | delay | Animation delay |
| 8 | 2 | ushort (BE) | speed | Animation speed/duration |
| 10 | 1 | byte | flag | Additional flag (modifies behavior) |

**Behavior:** Validates zone coordinates against the build area bounds, gets tile height,
then calls `FUN_00bc1410` to play the animation on the location at the specified position.
The flag byte modifies the animation mode.

---

### LOC_ANIM_SPECIFIC (opcode 0x06 / clone 0x0A, 10 bytes)

**Address:** `0x00192ac0`

Plays an animation on a location (simplified variant without flag byte).

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 4 | uint (BE) | animId | Animation ID |
| 5 | 1 | byte | shapeFlags | Shape type + height |
| 6 | 1 | byte | unknown1 | Unknown parameter |
| 7 | 1 | byte | delay | Animation delay |
| 8 | 2 | ushort (BE) | speed | Animation speed/duration |

**Behavior:** Same as LOC_ANIM but uses a constant flag value of 6 instead of reading
the flag from the packet. One byte shorter.

---

### LOC_MERGE (opcode 0x84 / clone 0x0D, 5 bytes)

**Address:** `0x0018ef50`

Merges a location's model with an entity (e.g., player carrying an object).

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 4 | int (LE) | entityServerIndex | Server index of the entity to merge with |
| 4 | 1 | byte | coordAndShape | Packed: coord (bits 0-2, 4-6) + shape type |

**Behavior:** Looks up the entity by server index, extracts the shape type from the
packed byte, then iterates over shape slots matching the type byte. Merges the entity's
model with the location model.

---

### OBJ_ADD (opcode 0x57 / clone 0x02, 5 bytes)

**Address:** `0x001b9550`

Adds a ground item to the scene.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | objIdWithTransforms | Item ID with transform flags |
| 2 | 1 | byte | packedCoord | Zone-relative position |
| 3 | 2 | ushort | count | Item stack count |

**Behavior:** Calls `jag::ObjStackList::AddObjStack` to add the ground item at the
zone-relative position. The item ID may include transform bits.

---

### OBJ_DEL (opcode 0x62 / clone 0x03, 3 bytes)

**Address:** `0x001fa050`

Removes a ground item from the scene.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 2 | ushort (BE) | objId | Item ID to remove |

**Behavior:** Searches the obj stack list at the zone-relative position for the matching
item ID and removes it.

---

### OBJ_COUNT (opcode 0x03 / clone 0x07, 7 bytes)

**Address:** `0x001b9410`

Updates a ground item's stack count (or adds it if not present).

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | objId | Item ID |
| 2 | 2 | ushort | count | New stack count |
| 4 | 1 | byte | packedCoord | Zone-relative position |
| 5 | 2 | ushort | playerIndex | Player index check value |

**Behavior:** Calls `jag::ObjStackList::AddObjStack` similar to OBJ_ADD but includes
a player index check. Updates the count of an existing ground item or adds it.

---

### OBJ_REVEAL (opcode 0x7F / clone 0x11, 7 bytes)

**Address:** `0x00194cd0`

Reveals a ground item's real count to the player.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 2 | ushort (BE) | objId | Item ID to reveal |
| 3 | 2 | ushort (BE) | oldCount | Current displayed count |
| 5 | 2 | ushort (BE) | newCount | Revealed true count |

**Behavior:** Searches the ground item list at the tile position. For each item matching
both `objId` and `oldCount`, replaces the count with `newCount`. Also clears a dirty flag
at offset +0xB8 on the containing structure. Uses an unrolled loop (8x) for performance.

---

### MAP_ANIM (opcode 0x7A / clone 0x08, 11 bytes)

**Address:** `0x00206a70`

Places a spot animation at a map position.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 2 | ushort (BE) | entityIdLow | Entity ID low word (0xFFFF = sentinel) |
| 3 | 2 | ushort (BE) | entityIdHigh | Entity ID high word |
| 5 | 2 | ushort (BE) | height | Y offset / height |
| 7 | 1 | byte | angle | Rotation angle |

**Behavior:** Gets tile link and height. If entity ID is 0xFFFFFFFF (sentinel), removes
all existing SpotAnims at the matching position. Otherwise creates a new SpotAnim via
`FUN_002057e0` with the specified height and angle.

---

### MAP_ANIM_SPECIFIC (opcode 0xC2 / clone 0x04, 14 bytes)

**Address:** `0x002065e0`

Places a spot animation with fine-grain position offsets.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1-6 | 6 | -- | entityId + height + angle | Same as MAP_ANIM |
| 7 | 1 | byte | unknown | Additional parameter |
| 8-10 | 3 | packed | fineOffset | 11-bit dx, 11-bit dy, 1-bit flag |

**Behavior:** Same as MAP_ANIM but with additional 3-byte packed fine position offsets
(11-bit signed X delta, 11-bit signed Y delta, 1-bit above-ground flag). These offsets
are applied to the SpotAnim's position for sub-tile precision.

---

### MAP_PROJANIM (opcode 0x18 / clone 0x06, 20 bytes)

**Address:** `0x00193e10`

Spawns a projectile animation between two zone-relative tiles.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Source tile (zone-relative) |
| 1 | 1 | sbyte | targetDeltaX | Target X delta from source |
| 2 | 1 | sbyte | targetDeltaY | Target Y delta from source |
| 3 | 3 | packed | startHeight | Start height (3 bytes packed RGB-style) |
| 6 | 2 | ushort (BE) | projId | Projectile graphic ID (0xFFFF = skip) |
| 8 | 1 | sbyte | startAngle | Starting angle |
| 9 | 1 | sbyte | endAngle | Ending angle |
| 10 | 2 | ushort (BE) | slope | Arc slope |
| 12 | 2 | ushort (BE) | startDelay | Start delay in client cycles |
| 14 | 1 | byte | targetIndex | Target entity (0xFF = none, else ushort follows) |
| 15 | 2 | ushort (BE) | duration | Duration in client cycles |

**Behavior:** If `projId` is not 0xFFFF, computes source and target positions from zone
globals + deltas, then calls `jag::ProjectileList::Add` with all parameters. Duration is
left-shifted by 2 (multiplied by 4).

---

### PROJANIM_SPECIFIC (opcode 0xD6 / clone 0x0F, 21 bytes)

**Address:** `0x00193ae0`

Projectile animation with double-resolution coordinates.

**Packet fields:**
Same as MAP_PROJANIM but the packed coord uses double resolution (*2 tiles, *0x100 scale).
One additional byte for the double-resolution coordinate encoding.

**Behavior:** Same as MAP_PROJANIM but with double-resolution coordinate calculations:
tile positions are multiplied by 2 and scaled by 0x100 instead of using standard tile units.

---

### MAP_PROJANIM_HALT (opcode 0x9F / clone 0x0B, 28 bytes)

**Address:** `0x00193770`

Projectile with fine-grain start and end position offsets.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0-19 | 20 | -- | base | Same base fields as MAP_PROJANIM |
| 20 | 1 | byte | targetIndex | Target entity index |
| 21 | 2 | ushort (BE) | duration | Duration |
| 23 | 3 | packed | sourceOffset | 11-bit dx, 11-bit dy, 1-bit flag |
| 26 | 3 | packed | targetOffset | 11-bit dx, 11-bit dy, 1-bit flag |

**Fine offset packed format (3 bytes, 24 bits):**
```
bits [0:10]  = dx (signed, -0x3FF to +0x3FF, subtract 0x3FF)
bits [11:21] = dy (signed, -0x3FF to +0x3FF, subtract 0x3FF)
bit  [22]    = above-ground flag (1 = true)
```

**Behavior:** Calls `jag::ProjectileList::Add` with additional source and target fine
position adjustment structs (3 ints each: dx, dy, flag).

---

### PROJANIM_SPECIFIC_HALT (opcode 0x93 / clone 0x0C, 29 bytes)

**Address:** `0x001933f0`

Projectile with fine offsets AND double-resolution coordinates.

**Behavior:** Combines PROJANIM_SPECIFIC (double-res coords) with HALT variant
(fine source/target position offsets). Same packed offset format as MAP_PROJANIM_HALT.

---

### SOUND_AREA (opcode 0xC6 / clone 0x0E, var_byte)

**Address:** `0x001cc090` (via thunk)

Plays a sound effect at a zone position.

**Packet fields:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | packedCoord | Zone-relative position |
| 1 | 1 | byte | unknown | Flags/type byte |
| 2 | 2 | gT_ushort | soundId | Sound effect ID (big-endian with transforms) |
| 4 | 1 | byte | type | Sound type |
| 5 | 1 | byte | unknown2 | Additional parameter |
| 6 | 1 | byte | unknown3 | Additional parameter |
| 7 | 1 | byte | unknown4 | Additional parameter |
| 8+ | var | string | soundPath | Null-terminated sound path (cp1252 to UTF-8) |

**Behavior:** Allocates an entry from a memory pool (via `jag::game::Memory::PoolAllocatorGetSlot`),
converts the sound path string from cp1252 to UTF-8, creates a linked list node with
color/position data, and inserts it into the area sound list. The sound plays at the
zone-relative position with the specified parameters.
