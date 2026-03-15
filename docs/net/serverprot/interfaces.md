# ServerProt: Interfaces Category

## Overview

The Interfaces packet handler category manages all server-to-client interface/widget operations. It is registered by the constructor at `0x0014d9aa` (`jag::packethandlers::Interfaces::Interfaces`), which binds 37 lambda handlers to ServerProt globals in the address range `0x016f38b0` - `0x016f41c0`.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success), `&DAT_016fb220` (yield), `&DAT_016fb1e0` (error)

## Key Helper Functions

| Address | Name | Purpose |
|---------|------|---------|
| `0x00bbfd60` | `InterfaceManager::CreateOrFindUpdateEntry` | Creates or finds an update entry in the interface update tree. param_2=update type, param_3=component hash |
| `0x00bc6940` | `InterfaceManager::SetComponentProperty` | Wrapper: creates type-4 update with 3 values (componentHash, propertyType, value) |
| `0x002a1100` | `InterfaceManager::MarkUpdateEntryDirty` | Marks an update entry as needing processing |
| `0x00232f70` | `InterfaceManager::SetUpdateSlotValue` | Sets a value in an update entry's data slot |
| `0x004059d0` | `InterfaceManager::SetSlotObject` | Sets item/object data on component slots via nested hash table |
| `0x003fade0` | `InterfaceManager::CloseInterface` | Closes an interface, iterating children and triggering close callbacks |
| `0x002caad0` | `InterfaceManager::SetComponentText` | Sets text string on a component |

## Update Type Codes

Used by `CreateOrFindUpdateEntry` (param_2):

| Code | Purpose | Handler(s) |
|------|---------|------------|
| 4 | General property (via `SetComponentProperty`) | Multiple (colour, model, anim, object, npchead) |
| 5 | Graphic/sprite | IF_SETGRAPHIC |
| 6 | Colour (15-bit RGB packed) | IF_SETCOLOUR2 |
| 7 | Hidden flag (boolean) | IF_SETHIDE |
| 8 | Position (x, y, align) | IF_SETPOSITION, IF_SETMODEL_OBJECT (sub-entry) |
| 9 | Model + NPC type | IF_SETMODEL_OBJECT |
| 10 | Extra NPC properties | IF_SETMODEL_OBJECT (sub-entry) |
| 0xb | Open sub-interface | IF_OPENSUB |
| 0xc | Open top-level interface | IF_OPENTOP |
| 0xd | Angle (rotation, packed) | IF_SETANGLE |
| 0xf | Sprite/animation ID | IF_SETSPRITE |
| 0x11 | Scroll position | IF_SETSCROLLPOS |
| 0x12 | Scroll content size | IF_SETSCROLLSIZE |
| 0x14 | NPC head (active, boolean) | IF_SETNPCHEAD_ACTIVE |
| 0x15 | Player head (active, boolean) | IF_SETPLAYERHEAD_ACTIVE |
| 0x16 | Model + object (coord pair) | IF_SETMODEL_OBJECT2 |

## Property Type Codes

Used by `SetComponentProperty` (param_3, which maps to slot 0x40 of a type-4 update):

| Code | Purpose | Handler(s) |
|------|---------|------------|
| 1 | Colour | IF_SETCOLOUR |
| 2 | 3D Model | IF_SETMODEL |
| 3 | Animation | IF_SETANIM, IF_SETANIM_SELF, IF_SETANIM_NOCOUNT |
| 5 | Object/Item | IF_SETOBJECT, IF_SETOBJECT_SELF, IF_SETOBJECT_NOCOUNT |
| 7 | NPC Head | IF_SETNPCHEAD |

---

## Handler Reference

### Interface Open/Close Operations

#### IF_OPENSUB (opcode 119, size 8)
**Handler Address:** `0x233c80` | **ServerProt Global:** `0x016f41a0`
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt3 | int | Component hash |
| ushort_BE | ushort | Interface ID |
| 2 bytes | byte[2] | Overlay data (ushort raw) |

**Behavior:** Creates update type 0xb. Sets interfaceId at slot 0x20, overlay at slot 0x40 via `SetUpdateSlotValue`. Marks the interface update tree dirty.

---

#### IF_OPENTOP (opcode 207 / 0xCF, size 2) **CORRECTED**
**NOTE:** Previously incorrectly documented as opcode 108. Opcode 108 is IF_SETOBJECT_ALWAYSNUM.
**Verified from RegisterAll:** `InitEntry(&DAT_016fd220, 0xcf, 2)`
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| ushort_BE | ushort | Interface ID |

**Behavior:** Creates update type 0xc, marks it dirty, stores interfaceId at slot 0x20 via `SetUpdateSlotValue`. See `docs/net/serverprot/if-opentop.md` for full analysis.

---

#### IF_CLOSESUB (opcode 126, size 19)
**Handler Address:** `0x225b00` | **ServerProt Global:** `0x016f4120`
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | (unused) |
| g4s_alt2 | int | (unused) |
| g4s_alt2 | int | (unused) |
| 2 bytes | ushort | Interface ID |
| g4s_alt3 | int | (unused) |
| 1 byte | byte | (unused) |

**Behavior:** Calls `InterfaceManager::CloseInterface` to close the interface. Stores interface ID at InterfaceManager+0xd8. Clears shared pointers at InterfaceManager+0xe0/0xe8 and 0x170/0x178.

---

#### IF_CLOSESUB_ACTIVE (opcode 50, size 4)
**Handler Address:** `0x224ec0` | **ServerProt Global:** ``0x016f3fa0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt2 | int | Component hash |

**Behavior:** Looks up component in InterfaceManager hash table (offset 0xf8/0x100). If found and not the sentinel entry, detaches the sub-interface via `FUN_0041d510`. Clears shared pointer at InterfaceManager+0x158/0x160.

---

#### IF_MOVESUB (opcode 71, size 8)
**Handler Address:** `0x224c80` | **ServerProt Global:** ``0x016f3f60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Source component hash |
| g4s_alt2 | int | Destination component hash |

**Behavior:** Looks up both source and destination in InterfaceManager hash table. Detaches from source via `FUN_0041d510`, re-attaches at destination via `FUN_00406070`.

---

#### IF_OPENSUB_ACTIVE (opcode 38, size 23)
**Handler Address:** `0x226e00` | **ServerProt Global:** ``0x016f40e0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | (param) |
| g4s_alt2 | int | Component hash |
| g4_alt1 | int | (param) |
| gT_uint | uint | (param) |
| 1 byte | byte | (value) |
| 2 bytes | ushort | Interface ID |
| g4s_alt3 | int | (param) |

**Behavior:** Validates interface exists via `InterfaceList::GetInterface`. Allocates from object pool (with mutex), sets vtable pointers, stores properties. Registers the new active interface via `FUN_00420c70`.

---

#### IF_OPENSUB_OVERLAY (opcode 67, size 25)
**Handler Address:** `0x2254c0` | **ServerProt Global:** ``0x016f40a0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | Target hash |
| 2 bytes | ushort | Interface ID |
| 1 byte | byte | Overlay flag |
| 2 bytes | ushort | Interface ID (overlay) |
| g4s_alt2 x4 | int[4] | Additional params |

**Behavior:** Validates interface exists. Allocates 0x50 byte object with vtable. Registers via `FUN_00420c70`.

---

#### IF_OPENSUB_OVERLAY2 (opcode 116, size 25)
**Handler Address:** `0x2252f0` | **ServerProt Global:** ``0x016f4060``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| ushort_BE | ushort | (param) |
| 2 bytes | ushort | Interface ID |
| g4_alt1 | int | (param) |
| g4_alt1 | int | Target hash |
| g4s_alt2 x2 | int[2] | Additional params |
| 1 byte | byte | Flag |
| gT_uint | uint | (param) |

**Behavior:** Similar to IF_OPENSUB_OVERLAY but with different read order. Allocates 0x50 byte object.

---

#### IF_OPENSUB_WITH_PARAMS (opcode 21, size 32)
**Handler Address:** `0x225690` | **ServerProt Global:** ``0x016f4020``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt2 | int | Component hash |
| 1 byte | byte | (param) |
| g4s_alt3 | int | (param) |
| g4_alt1 | int | Packed coordinate (14-bit x/y + 2-bit plane) |
| 1 byte + uint32_BE | byte + uint | Color/transform data |
| g4_alt1 | int | (param) |
| gT_uint | uint | (param) |
| 2 bytes | ushort | Interface ID |
| gT_uint | uint | (param) |
| color data | varies | Extended color/transform via FUN_00bc1090 |

**Behavior:** Most complex open handler. Allocates 0xe0 byte object with extended properties including color transforms, 3D identity matrix, and position data. Unpacks 14-bit fine coordinates from packed value.

---

#### IF_OPENSUB_POSITION (opcode 7, size 29)
**Handler Address:** `0x225060` | **ServerProt Global:** ``0x016f3fe0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 2 bytes | ushort | Interface ID |
| ushort_BE | ushort | (param) |
| uint32_BE | uint | Additional data |
| g4s_alt2 x2 | int[2] | Component hashes |
| g4_alt1 | int | (param) |
| 1 byte | byte | (flag) |
| g4_alt1 | int | (param) |
| uint32_BE | uint | Packed position (14-bit fine coords) |

**Behavior:** Allocates 0x60 byte object. Decodes packed position: x = (packed >> 14) & 0x3FFF, y = packed & 0x3FFF, plane = (packed >> 28) & 3. Converts to float positions via `<< 9`.

---

#### IF_TRIGGER_CLOSE (opcode 99, size 0)
**Handler Address:** `0x224530` | **ServerProt Global:** ``0x016f3920``
**Packet Format:** None (no packet data read)

**Behavior:** Reads stored interface hash from InterfaceManager+0xd8. If not -1, calls `FUN_003fb510` with action code 0x29 to trigger the close event on the stored interface.

---

### Property Setting Operations

#### IF_SETTEXT (opcode 57, size var-short)
**Handler Address:** `0x224a20` | **ServerProt Global:** ``0x016f3ea0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| gStringCP1252ToUTF8 | string | Text content (CP1252 encoded, converted to UTF8) |
| g4s_alt2 | int | Component hash |

**Behavior:** Calls `InterfaceManager::SetComponentText` with type=1 (server text).

**InterfaceComponent Field:** Text at offset 0x160

---

#### IF_SETHIDE (opcode 45, size 5)
**Handler Address:** `0x233b40` | **ServerProt Global:** ``0x016f3e60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Component hash |
| 1 byte | byte | Hidden flag (0x7f = hidden, other = visible) |

**Behavior:** Creates update type 7 with boolean value (1 if byte == 0x7f, else 0).

---

#### IF_SETCOLOUR (opcode 61, size 8)
**Handler Address:** `0x224830` | **ServerProt Global:** ``0x016f3ce0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 4 bytes | uint | Colour (BGRA packed) |
| g4_alt1 | int | Component hash |

**Behavior:** Calls `SetComponentProperty` with propertyType=1, colour value, extra=0xffffffff.

---

#### IF_SETCOLOUR2 (opcode 8, size 6)
**Handler Address:** `0x2333e0` | **ServerProt Global:** ``0x016f3a60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Component hash |
| 2 bytes | ushort | 15-bit colour |

**Behavior:** Creates update type 6. Colour unpacked from 15-bit format: `((c & 0x3e0) << 6) | ((c & 0x7c00) << 9) + (c & 0x1f) * 8`. This converts 5-5-5 RGB to 8-8-8 RGB.

---

#### IF_SETMODEL (opcode 89, size 8)
**Handler Address:** `0x2247b0` | **ServerProt Global:** ``0x016f3c60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Model ID |
| g4s_alt3 | int | Component hash |

**Behavior:** Calls `SetComponentProperty` with propertyType=2, modelId, extra=0xffffffff.

---

#### IF_SETANIM (opcode 75, size 10)
**Handler Address:** `0x2246a0` | **ServerProt Global:** ``0x016f3be0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| ushort_BE | ushort | Animation slot |
| uint32_BE | uint | Component hash |
| 4 bytes | uint | Animation ID + extra data |

**Behavior:** Calls `SetComponentProperty` with propertyType=3, animSlot, animId.

---

#### IF_SETANIM_SELF (opcode 77, size 4)
**Handler Address:** `0x224760` | **ServerProt Global:** ``0x016f3c20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | Component hash |

**Behavior:** Uses logged-in player's interface slot from Client+LoggedInPlayer+0x48. Calls `SetComponentProperty` with propertyType=3, slot=playerSlot, count=0.

---

#### IF_SETANIM_NOCOUNT (opcode 140, size 5)
**Handler Address:** `0x224570` | **ServerProt Global:** ``0x016f3b60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 1 byte | byte | Slot (encoded) |
| g4s_alt3 | int | Component hash |

**Behavior:** Decodes slot as `-(byte + 0x80) - 2`. Calls `SetComponentProperty` with propertyType=3, slot, count=0.

---

#### IF_SETOBJECT (opcode 4, size 10)
**Handler Address:** `0x224990` | **ServerProt Global:** ``0x016f3da0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 2 bytes | ushort | Slot ID |
| uint32_BE | uint | Object count |
| g4s_alt2 | int | Component hash |

**Behavior:** Calls `SetComponentProperty` with propertyType=5, slotId, count.

**InterfaceComponent Fields:** Item ID at 0x180, Stack Size at 0x188

---

#### IF_SETOBJECT_SELF (opcode 106, size 4)
**Handler Address:** `0x224940` | **ServerProt Global:** ``0x016f3d60``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt3 | int | Component hash |

**Behavior:** Uses logged-in player's interface slot. Calls `SetComponentProperty` with propertyType=5, slot=playerSlot, count=0.

---

#### IF_SETOBJECT_NOCOUNT (opcode 182, size 5)
**Handler Address:** `0x2248c0` | **ServerProt Global:** ``0x016f3d20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 1 byte | byte | Slot (encoded as -(byte + 0x80) - 2) |
| uint32_BE | uint | Component hash |

**Behavior:** Calls `SetComponentProperty` with propertyType=5, decoded slot, count=0.

---

#### IF_SETOBJECT_SLOT (opcode 59, size 12)
**Handler Address:** `0x224bb0` | **ServerProt Global:** ``0x016f3f20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt2 | int | Component hash |
| 2 bytes | ushort | Object ID (0xffff = -1) |
| g4s_alt3 | int | Count |
| 2 bytes | ushort | Slot ID (0xffff = -1) |

**Behavior:** Calls `InterfaceManager::SetSlotObject` with the component hash, object, count, and slot.

---

#### IF_SETOBJECT_COUNT (opcode 93, size 10)
**Handler Address:** `0x224ac0` | **ServerProt Global:** ``0x016f3ee0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 2 bytes | ushort | Slot ID A |
| ushort_BE | ushort | Count (0xffff = -1) |
| g4s_alt3 | int | Component hash |
| 2 bytes | ushort | Slot ID B (0xffff = -1) |

**Behavior:** Calls `InterfaceManager::SetSlotObject` with range parameters.

---

#### IF_SETANGLE (opcode 74, size 8)
**Handler Address:** `0x233a90` | **ServerProt Global:** ``0x016f3e20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | Component hash |
| 4 bytes | uint | Angle (packed BGRA-style) |

**Behavior:** Creates update type 0xd. The angle value is packed from 4 individual bytes into a single 32-bit integer.

---

#### IF_SETSPRITE (opcode 125, size 8)
**Handler Address:** `0x2334b0` | **ServerProt Global:** ``0x016f3aa0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Sprite ID |
| g4s_alt3 | int | Component hash |

**Behavior:** Creates update type 0xf with sprite ID.

**InterfaceComponent Field:** Sprite ID at offset 0x168

---

#### IF_SETGRAPHIC (opcode 26, size 8)
**Handler Address:** `0x2339d0` | **ServerProt Global:** ``0x016f3ca0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | Component hash |
| 4 bytes | uint | Graphic ID (packed) |

**Behavior:** Creates update type 5 with graphic ID.

---

#### IF_SETNPCHEAD (opcode 113, size 10)
**Handler Address:** `0x2245e0` | **ServerProt Global:** ``0x016f3ba0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 2 bytes | ushort | Slot A |
| 2 bytes | ushort | NPC ID |
| 2 bytes | ushort | Slot B |
| g4s_alt3 | int | Component hash |

**Behavior:** Calls `SetComponentProperty` with propertyType=7, packed component hash (high*0x10000 | low), NPC ID.

---

#### IF_SETNPCHEAD_ACTIVE (opcode 180, size 5)
**Handler Address:** `0x233160` | **ServerProt Global:** ``0x016f39a0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 1 byte | byte | NPC ID flag |
| g4s_alt2 | int | Component hash |

**Behavior:** Creates update type 0x14 with boolean (1 if byte == 0x7f, else 0).

---

#### IF_SETPLAYERHEAD_ACTIVE (opcode 48, size 5)
**Handler Address:** `0x2330d0` | **ServerProt Global:** ``0x016f3960``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 1 byte | byte | Flag |
| g4_alt1 | int | Component hash |

**Behavior:** Creates update type 0x15 with boolean (1 if byte == 0x01, else 0).

---

#### IF_SETPLAYERMODEL (opcode 207, size 2)
**Handler Address:** `0x2244e0` | **ServerProt Global:** ``0x016f38a0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| ushort_BE | ushort | Model ID |

**Behavior:** Calls `FUN_005029b0` via Client player manager with modelId and flag=1. Does not use InterfaceManager directly.

---

#### IF_SETPLAYERMODEL_COLOUR (opcode 105, size 8)
**Handler Address:** `0x219130` | **ServerProt Global:** ``0x016f3de0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| uint32_BE | uint | Component hash |
| 4 bytes | uint | Colour (packed) |

**Behavior:** Resolves component via `InterfaceList::GetInterface`. Writes colour directly to InterfaceComponent+0x194. Falls back to global default component if hash is 0xffffffff.

**InterfaceComponent Field:** Player model colour at offset 0x194

---

#### IF_SETPOSITION (opcode 100, size 10)
**Handler Address:** `0x232fc0` | **ServerProt Global:** ``0x016f38e0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | Component hash |
| 2 bytes | ushort | Position X |
| 2 bytes | ushort | Position Y |
| 2 bytes | ushort | Alignment type |

**Behavior:** Creates update type 8 with 3 values at slots 0x20 (posX), 0x40 (posY), 0x60 (alignType).

---

#### IF_SETSCROLLPOS (opcode 137, size 9)
**Handler Address:** `0x2332d0` | **ServerProt Global:** ``0x016f3a20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 1 byte | byte | Scrollbar index (0x80 - byte) |
| g4_alt1 | int | Component hash |
| 2 bytes | ushort | Scroll position A |
| 2 bytes | ushort | Scroll position B |

**Behavior:** Creates update type 0x11 with 3 values: scrollbar index, posA, posB.

---

#### IF_SETSCROLLSIZE (opcode 202, size 9)
**Handler Address:** `0x2331e0` | **ServerProt Global:** ``0x016f39e0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| 2 bytes | ushort | Size A |
| 2 bytes | ushort | Size B |
| 1 byte | byte | Scrollbar index (0x80 - byte) |
| g4s_alt2 | int | Component hash |

**Behavior:** Creates update type 0x12 with 3 values: scrollbar index, sizeA, sizeB.

---

### Complex Model Operations

#### IF_SETMODEL_OBJECT (opcode 62, size 10)
**Handler Address:** `0x233790` | **ServerProt Global:** ``0x016f3b20``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4s_alt2 | int | Component hash |
| uint32_BE | uint | Model ID |
| 2 bytes | ushort | NPC type ID (0xffff = none) |

**Behavior:** Creates 3 update entries:
1. Type 9: NPC ID + model reference
2. Type 8: Position data from NPC type definition (offsets 0x2a6-0x2ac: camera X/Y/Z/rotation)
3. Type 10: Extra NPC type properties (offsets 0x2b0/0x2b4)

If NPC type data is not loaded (type != 4), uses fallback defaults from `DAT_01708b10`.

---

#### IF_SETMODEL_OBJECT2 (opcode 176, size 14)
**Handler Address:** `0x233540` | **ServerProt Global:** ``0x016f3ae0``
**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g4_alt1 | int | X coordinate |
| g4_alt1 | int | Y coordinate |
| 2 bytes | ushort | NPC type ID (0xffff = none) |
| g4s_alt3 | int | Component hash |

**Behavior:** Creates update type 0x16 with coordinate pair (x, y), then creates types 8 and 10 from NPC type data (same as IF_SETMODEL_OBJECT).

---

## ServerProt Opcode Table

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|-------------|-----------------|-------------------|
| 4 | 10 | IF_SETOBJECT | `0x224990` | `0x016f3da0` |
| 7 | 29 | IF_OPENSUB_POSITION | `0x225060` | `0x016f3fe0` |
| 8 | 6 | IF_SETCOLOUR2 | `0x2333e0` | `0x016f3a60` |
| 21 | 32 | IF_OPENSUB_WITH_PARAMS | `0x225690` | `0x016f4020` |
| 26 | 8 | IF_SETGRAPHIC | `0x2339d0` | `0x016f3ca0` |
| 38 | 23 | IF_OPENSUB_ACTIVE | `0x226e00` | `0x016f40e0` |
| 45 | 5 | IF_SETHIDE | `0x233b40` | `0x016f3e60` |
| 48 | 5 | IF_SETPLAYERHEAD_ACTIVE | `0x2330d0` | `0x016f3960` |
| 50 | 4 | IF_CLOSESUB_ACTIVE | `0x224ec0` | `0x016f3fa0` |
| 57 | var-short | IF_SETTEXT | `0x224a20` | `0x016f3ea0` |
| 59 | 12 | IF_SETOBJECT_SLOT | `0x224bb0` | `0x016f3f20` |
| 61 | 8 | IF_SETCOLOUR | `0x224830` | `0x016f3ce0` |
| 62 | 10 | IF_SETMODEL_OBJECT | `0x233790` | `0x016f3b20` |
| 67 | 25 | IF_OPENSUB_OVERLAY | `0x2254c0` | `0x016f40a0` |
| 71 | 8 | IF_MOVESUB | `0x224c80` | `0x016f3f60` |
| 74 | 8 | IF_SETANGLE | `0x233a90` | `0x016f3e20` |
| 75 | 10 | IF_SETANIM | `0x2246a0` | `0x016f3be0` |
| 77 | 4 | IF_SETANIM_SELF | `0x224760` | `0x016f3c20` |
| 89 | 8 | IF_SETMODEL | `0x2247b0` | `0x016f3c60` |
| 93 | 10 | IF_SETOBJECT_COUNT | `0x224ac0` | `0x016f3ee0` |
| 99 | 0 | IF_TRIGGER_CLOSE | `0x224530` | `0x016f3920` |
| 100 | 10 | IF_SETPOSITION | `0x232fc0` | `0x016f38e0` |
| 105 | 8 | IF_SETPLAYERMODEL_COLOUR | `0x219130` | `0x016f3de0` |
| 106 | 4 | IF_SETOBJECT_SELF | `0x224940` | `0x016f3d60` |
| 207 | 2 | IF_OPENTOP | (see if-opentop.md) | `0x016fd220` | **CORRECTED: was 108/6**
| 113 | 10 | IF_SETNPCHEAD | `0x2245e0` | `0x016f3ba0` |
| 116 | 25 | IF_OPENSUB_OVERLAY2 | `0x2252f0` | `0x016f4060` |
| 119 | 8 | IF_OPENSUB | `0x233c80` | `0x016f41a0` |
| 125 | 8 | IF_SETSPRITE | `0x2334b0` | `0x016f3aa0` |
| 126 | 19 | IF_CLOSESUB | `0x225b00` | `0x016f4120` |
| 137 | 9 | IF_SETSCROLLPOS | `0x2332d0` | `0x016f3a20` |
| 140 | 5 | IF_SETANIM_NOCOUNT | `0x224570` | `0x016f3b60` |
| 176 | 14 | IF_SETMODEL_OBJECT2 | `0x233540` | `0x016f3ae0` |
| 180 | 5 | IF_SETNPCHEAD_ACTIVE | `0x233160` | `0x016f39a0` |
| 182 | 5 | IF_SETOBJECT_NOCOUNT | `0x2248c0` | `0x016f3d20` |
| 202 | 9 | IF_SETSCROLLSIZE | `0x2331e0` | `0x016f39e0` |
| 207 | 2 | IF_SETPLAYERMODEL | `0x2244e0` | `0x016f38a0` |

## InterfaceComponent Field Cross-Reference

The following InterfaceComponent struct fields (448 bytes, `/jag` category in Ghidra) are written to by these handlers:

| Offset | Field | Size | Handlers |
|--------|-------|------|----------|
| 0x160 | text | 8 (ptr) | IF_SETTEXT |
| 0x168 | spriteId | 4 | IF_SETSPRITE |
| 0x180 | itemId | 4 | IF_SETOBJECT, IF_SETOBJECT_SLOT, IF_SETOBJECT_COUNT |
| 0x188 | stackSize | 4 | IF_SETOBJECT, IF_SETOBJECT_SLOT, IF_SETOBJECT_COUNT |
| 0x194 | playerModelColour | 4 | IF_SETPLAYERMODEL_COLOUR |

Note: Most handlers do not write directly to InterfaceComponent fields. Instead, they create update entries in the InterfaceManager's update tree, which are later applied during the interface rebuild phase. The update tree is organized by (updateType, componentHash) and contains 1-3 data slots (at offsets 0x20, 0x40, 0x60 from the update entry).

## Packet Read Method Reference

| Method | Description |
|--------|-------------|
| `g4_alt1` | Read 4-byte int (big-endian with XOR transform) |
| `g4s_alt2` | Read 4-byte signed int (alternative encoding 2) |
| `g4s_alt3` | Read 4-byte signed int (alternative encoding 3) |
| `gT_uint` | Read 4-byte unsigned int (transformed) |
| `gSmart1or2` | Read 1 or 2 byte smart int |
| `gStringCP1252ToUTF8` | Read null-terminated string (CP1252 to UTF8 conversion) |
| `ushort_BE` | Manual 2-byte big-endian unsigned short read |
| `uint32_BE` | Manual 4-byte big-endian unsigned int read |

All multi-byte manual reads include endianness detection via `DAT_011591e0` (== 0x3020100 for little-endian platforms, requiring byte swap).
