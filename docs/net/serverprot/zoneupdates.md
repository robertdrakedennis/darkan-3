# ServerProt: ZoneUpdates Category

## Overview

The ZoneUpdates packet handler category manages all zone-specific scene updates: adding/removing/animating locations (objects), ground items, projectile animations, map animations, and area sounds. The constructor at `0x0014c4c8` (`jag::packethandlers::ZoneUpdates::ZoneUpdates`) registers 21 handlers.

A key feature of this category is the **dual protocol system**: each handler (except UPDATE_ZONE_PARTIAL, UPDATE_ZONE_FULL_FOLLOWS, and UPDATE_ZONE_PARTIAL_FOLLOWS) is registered in both the main ServerProt protocol AND a zone sub-protocol. The zone sub-protocol is used within UPDATE_ZONE_FULL_FOLLOWS and UPDATE_ZONE_PARTIAL_FOLLOWS payloads, which contain a sequence of zone-protocol-encoded sub-packets.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success)

## ServerProt Opcode Table (Main Protocol)

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|--------------|-----------------|-------------------|
| 24 | 20 | MAP_PROJANIM | `0x00193e10` | `0x016f4fe0` |
| 30 | var-byte | LOC_ADD | `0x001e46b0` | `0x016f51e0` |
| 43 | var-short | UPDATE_ZONE_PARTIAL | `0x0018f370` | `0x016f5220` |
| 64 | 2 | LOC_DEL | `0x001dc500` | `0x016f50e0` |
| 78 | 7 | LOC_PREFETCH | `0x001e4520` | `0x016f5060` |
| 82 | var-byte | LOC_CUSTOMISE | `0x001dd650` | `0x016f5160` |
| 87 | 5 | OBJ_ADD | `0x001b9550` | `0x016f4e60` |
| 90 | 3 | UPDATE_ZONE_FULL_FOLLOWS | `0x001989b0` | `0x016f5260` |
| 98 | 3 | OBJ_DEL | `0x001fa050` | `0x016f4de0` |
| 120 | 3 | UPDATE_ZONE_PARTIAL_FOLLOWS | `0x0018f500` | `0x016f52a0` |
| 122 | 11 | MAP_ANIM | `0x00206a70` | `0x016f4ee0` |
| 127 | 7 | OBJ_REVEAL | `0x00194cd0` | `0x016f4ce0` |
| 132 | 5 | LOC_MERGE | `0x0018ef50` | `0x016f4ba0` |
| 147 | 29 | PROJANIM_SPECIFIC_HALT | `0x001933f0` | `0x016f4a20` |
| 159 | 28 | MAP_PROJANIM_HALT | `0x00193770` | `0x016f4aa0` |
| 194 | 14 | MAP_ANIM_SPECIFIC | `0x002065e0` | `0x016f49a0` |
| 198 | var-byte | SOUND_AREA | `0x001cc090` | `0x016f4b20` |
| 214 | 21 | PROJANIM_SPECIFIC | `0x00193ae0` | `0x016f4f60` |
| 3 | 7 | OBJ_COUNT | `0x001b9410` | `0x016f4d60` |
| 6 | 10 | LOC_ANIM_SPECIFIC | `0x00192ac0` | `0x016f4c60` |

*Note: LOC_ANIM is registered only in the zone sub-protocol (opcode 0), not the main protocol.*

## Zone Sub-Protocol Opcode Table

These opcodes are used within UPDATE_ZONE_FULL_FOLLOWS and UPDATE_ZONE_PARTIAL_FOLLOWS payloads. Registered via `FUN_001825d0` (zone protocol init) and cloned from primary handlers via `FUN_00920170`.

| Zone Opcode | Size | Handler Name | ServerProt Global |
|-------------|------|--------------|-------------------|
| 0 | 11 | LOC_ANIM | `0x016f4be0` |
| 1 | var-byte | LOC_CUSTOMISE | `0x016f5120` |
| 2 | 5 | OBJ_ADD | `0x016f4e20` |
| 3 | 3 | OBJ_DEL | `0x016f4da0` |
| 4 | 14 | MAP_ANIM_SPECIFIC | `0x016f4960` |
| 5 | 7 | LOC_PREFETCH | `0x016f5020` |
| 6 | 20 | MAP_PROJANIM | `0x016f4fa0` |
| 7 | 7 | OBJ_COUNT | `0x016f4d20` |
| 8 | 11 | MAP_ANIM | `0x016f4ea0` |
| 9 | 2 | LOC_DEL | `0x016f50a0` |
| 10 | 10 | LOC_ANIM_SPECIFIC | `0x016f4c20` |
| 11 | 28 | MAP_PROJANIM_HALT | `0x016f4a60` |
| 12 | 29 | PROJANIM_SPECIFIC_HALT | `0x016f49e0` |
| 13 | 5 | LOC_MERGE | `0x016f4b60` |
| 14 | var-byte | SOUND_AREA | `0x016f4ae0` |
| 15 | 21 | PROJANIM_SPECIFIC | `0x016f4f20` |
| 16 | var-byte | LOC_ADD | `0x016f51a0` |
| 17 | 7 | OBJ_REVEAL | `0x016f4ca0` |

---

## Handler Reference

### Zone Frame Packets

#### UPDATE_ZONE_PARTIAL (opcode 43, size var-short)
**Handler Address:** `0x0018f370` | **ServerProt Global:** `0x016f5220`

**Behavior:** Reads zone coordinates (3 bytes: x, z, plane) and sets the current zone context for subsequent zone sub-protocol packets. Does not contain embedded sub-packets itself (unlike FOLLOWS variants).

---

#### UPDATE_ZONE_FULL_FOLLOWS (opcode 90, size 3)
**Handler Address:** `0x001989b0` | **ServerProt Global:** `0x016f5260`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Zone X coordinate |
| g1_sub128 | byte | Zone Z coordinate |
| g1 | byte | Plane level |

**Behavior:** Sets the current zone context, then processes a sequence of zone sub-protocol packets that follow in the stream. Each sub-packet is decoded using the zone opcode table. The "FULL" variant implies a complete zone reset before applying updates. Has a duplicate implementation at `0x0019a330`.

---

#### UPDATE_ZONE_PARTIAL_FOLLOWS (opcode 120, size 3)
**Handler Address:** `0x0018f500` | **ServerProt Global:** `0x016f52a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Zone X coordinate |
| g1_sub128 | byte | Zone Z coordinate |
| g1 | byte | Plane level |

**Behavior:** Sets the current zone context, then processes following zone sub-protocol packets. The "PARTIAL" variant applies updates incrementally without resetting existing zone data.

---

### Location (Object) Operations

#### LOC_ADD (opcode 30, size var-byte)
**Handler Address:** `0x001e46b0` | **ServerProt Global:** `0x016f51e0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Flags byte (bit 7 = unknown) |
| g1_sub128 | byte | Packed coordinate (bits 4-6 = local X, bits 0-2 = local Z) |
| g4s_alt3 | int | Location ID |
| (variable) | - | Shape/rotation data via `FUN_00bc1090` |

**Behavior:** Creates a new location in the scene at the zone-relative position. Computes local X from bits 4-6 and local Z from bits 0-2 of the packed coordinate. Location ID is read via `Packet::g4s_alt3`. For certain flag values (0x02, 0x08), creates additional overlay or underlayer entries.

---

#### LOC_DEL (opcode 64, size 2)
**Handler Address:** `0x001dc500` | **ServerProt Global:** `0x016f50e0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1_sub128 | byte | Packed coordinate (bits 4-6 = local X, bits 0-2 = local Z) |
| g1_sub128 | byte | Shape/rotation data |

**Behavior:** Removes a matching location from the scene tree. Iterates scene tree nodes comparing packed position/shape data to find and clear the matching entry. Also processes the pending location list to remove matching entries. Has a duplicate implementation at `0x001dd290`.

---

#### LOC_ANIM (zone opcode 0, size 11)
**Handler Address:** `0x001928b0` | **ServerProt Global:** `0x016f4be0`

**Behavior:** Applies an animation to a location in the scene. Only available in the zone sub-protocol (not as a standalone main-protocol packet).

---

#### LOC_ANIM_SPECIFIC (opcode 6, size 10)
**Handler Address:** `0x00192ac0` | **ServerProt Global:** `0x016f4c60`

**Behavior:** Applies a specific animation to a location, with additional targeting data compared to LOC_ANIM.

---

#### LOC_CUSTOMISE (opcode 82, size var-byte)
**Handler Address:** `0x001dd650` | **ServerProt Global:** `0x016f5160`

**Behavior:** Customises a location's appearance (recoloring, retexturing). Variable-length packet containing customization data. Has a duplicate implementation at `0x001df560`.

---

#### LOC_PREFETCH (opcode 78, size 7)
**Handler Address:** `0x001e4520` | **ServerProt Global:** `0x016f5060`

**Behavior:** Prefetches location model data to avoid loading delays when the location is later added to the scene.

---

#### LOC_MERGE (opcode 132, size 5)
**Handler Address:** `0x0018ef50` | **ServerProt Global:** `0x016f4ba0`

**Behavior:** Merges or updates an existing location in the scene, potentially changing its properties without a full remove/add cycle.

---

### Ground Item Operations

#### OBJ_ADD (opcode 87, size 5)
**Handler Address:** `0x001b9550` | **ServerProt Global:** `0x016f4e60`

**Behavior:** Adds a ground item to the scene at a zone-relative position.

---

#### OBJ_DEL (opcode 98, size 3)
**Handler Address:** `0x001fa050` | **ServerProt Global:** `0x016f4de0`

**Behavior:** Removes a ground item from the scene.

---

#### OBJ_COUNT (opcode 3, size 7)
**Handler Address:** `0x001b9410` | **ServerProt Global:** `0x016f4d60`

**Behavior:** Updates the stack count of an existing ground item.

---

#### OBJ_REVEAL (opcode 127, size 7)
**Handler Address:** `0x00194cd0` | **ServerProt Global:** `0x016f4ce0`

**Behavior:** Reveals a previously hidden ground item (e.g., when a dropped item becomes visible to other players after the visibility timer).

---

### Projectile Animation Operations

#### MAP_PROJANIM (opcode 24, size 20)
**Handler Address:** `0x00193e10` | **ServerProt Global:** `0x016f4fe0`

**Behavior:** Creates a projectile animation on the map between two map coordinates.

---

#### MAP_PROJANIM_HALT (opcode 159, size 28)
**Handler Address:** `0x00193770` | **ServerProt Global:** `0x016f4aa0`

**Behavior:** Creates a projectile animation that halts/stops at a specific point.

---

#### PROJANIM_SPECIFIC (opcode 214, size 21)
**Handler Address:** `0x00193ae0` | **ServerProt Global:** `0x016f4f60`

**Behavior:** Creates a targeted projectile animation directed at a specific entity.

---

#### PROJANIM_SPECIFIC_HALT (opcode 147, size 29)
**Handler Address:** `0x001933f0` | **ServerProt Global:** `0x016f4a20`

**Behavior:** Creates a targeted projectile animation that halts at a specific point.

---

### Map Animation Operations

#### MAP_ANIM (opcode 122, size 11)
**Handler Address:** `0x00206a70` | **ServerProt Global:** `0x016f4ee0`

**Behavior:** Creates a graphical animation (spot anim / gfx) at a map position.

---

#### MAP_ANIM_SPECIFIC (opcode 194, size 14)
**Handler Address:** `0x002065e0` | **ServerProt Global:** `0x016f49a0`

**Behavior:** Creates a graphical animation with additional specific parameters compared to MAP_ANIM.

---

### Sound Operations

#### SOUND_AREA (opcode 198, size var-byte)
**Handler Address:** `0x001cc090` | **ServerProt Global:** `0x016f4b20`

**Behavior:** Plays an area sound effect at a zone-relative position. Variable-length packet containing sound parameters. Has a duplicate implementation at `0x001cc750`.

---

## Common Packet Patterns

### Packed Zone Coordinate (1 byte)
Many zone handlers read a single byte encoding relative position within the zone:
- Bits 4-6: Local X coordinate (0-7)
- Bits 0-2: Local Z coordinate (0-7)
- The byte is typically transformed via `-128` subtraction before bit extraction

### Zone Context
Zone sub-protocol packets operate within the context of a previously set zone (via UPDATE_ZONE_PARTIAL/FULL_FOLLOWS). The zone coordinates (X, Z, plane) are stored on the client and used to compute absolute world positions from the relative coordinates in each sub-packet.

Global zone coordinate storage:
- `DAT_014bf28c`: Current zone X base
- `DAT_014bf288`: Current zone plane
- `DAT_014bf290`: Current zone Z base

### Dual Registration
18 of the 21 handlers are registered in both protocols via `FUN_00920170` (clone registration). The 3 exceptions are:
- UPDATE_ZONE_PARTIAL (main only, opcode 43)
- UPDATE_ZONE_FULL_FOLLOWS (main only, opcode 90)
- UPDATE_ZONE_PARTIAL_FOLLOWS (main only, opcode 120)

These framing packets only make sense in the main protocol since they establish the context for zone sub-protocol processing.
