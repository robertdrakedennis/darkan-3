# NPC_INFO Protocol

The NPC_INFO packet is the primary mechanism for synchronizing NPC positions, movements, and extended information (appearance, animations, combat, overhead text, etc.) between server and client. It is sent once per game tick.

## Overview

| Property | Value |
|----------|-------|
| Opcode | **28** (0x1c) |
| Size Mode | Variable short (-2), up to 65535 bytes |
| Handler | `jag::packethandlers::NPCInfo::decode` at `0x0027a030` |
| Source namespace | `jag::packethandlers::NPCInfo` (from debug symbols) |

The packet uses **bit-level encoding** for position/movement updates and **byte-level scrambled encoding** for extended info blocks. Unlike PLAYER_INFO which uses four passes with resolution tiers, NPC_INFO uses a simpler two-phase approach:

1. **Phase 1** (bit-packed): Update existing NPCs in the local list (movement, removal)
2. **Phase 2** (bit-packed): Add new NPCs (server index, position, direction)
3. **Phase 3** (byte-packed): Process extended info blocks for flagged NPCs

## Related Opcodes

| Opcode | Name | Size | Handler | Description |
|--------|------|------|---------|-------------|
| 28 (0x1c) | NPC_INFO | var short | `0x0027a030` | Main NPC update packet |
| 165 (0xa5) | NPC_ANIM_SPECIFIC | fixed 9 | `0x00279ce0` | Direct animation slot update for a single NPC |

---

## Data Structures

### NPC Manager / NPC List

The NPC list manager object is accessed via `Client + OClient.NPC_MANAGER` (offset `0x194b8`). It contains:

| Offset | Type | Name | Description |
|--------|------|------|-------------|
| 0x10 | `long *` | hashTableBuckets | Hash table bucket array for NPC lookup |
| 0x18 | `ulong` | hashTableSize | Number of hash table buckets |
| 0x20 | `long` | hashTableCount | Number of entries in hash table |
| 0xA0A0 | `int[2048]` | localNpcIndices | Array of NPC server indices in the local list |
| 0xD7C | `uint` | localNpcCount | Number of NPCs in local list |
| 0xD84 | `int *` | removedNpcsBegin | Vector begin - NPCs to remove |
| 0xD8C | `int *` | removedNpcsEnd | Vector current end - NPCs to remove |
| 0xD94 | `int *` | removedNpcsCapacity | Vector capacity |
| 0x1DB4 | `long` | clientDataPtr | Pointer to client subsystem data |
| 0x1DBC | `byte` | pendingRemoval | Flag: pending removal processing |
| 0x1DC0 | `int` | serverCycle | Server tick/cycle counter |
| 0x1DC4 | `uint` | coordBitWidth | Number of bits for coordinate encoding (varies by world size) |
| 0x1DCC | `int *` | extInfoListBegin | Vector begin - NPCs needing extended info |
| 0x1DD4 | `int *` | extInfoListEnd | Vector current end |
| 0x1DDC | `int *` | extInfoListCapacity | Vector capacity |

### NPC Entity

Each NPC entity is 0x1190 bytes (4496 bytes). Key offsets within the entity relevant to NPC_INFO:

| Offset | Type | Name | Description |
|--------|------|------|-------------|
| 0x78 | `long *` | graphNode | Pointer to the NPC's scene graph node |
| 0x148 | `void *` | headIconArray | Head icon sprite data area |
| 0x150 | `void *` | headIconListBegin | Head icon list begin ptr (for HeadIconsCustom) |
| 0x158 | `void *` | headIconListEnd | Head icon list end ptr |
| 0x160 | `long` | headIconState | Head icon state (cleared by HeadIconsCustom) |
| 0x178 | `long` | lastUpdateCycle | Last server cycle this NPC was updated |
| 0x190 | `float[4]` | tintColor | RGBA tint color (PositionColor block) |
| 0x1A0 | `int` | colorStartCycle | Tint start client cycle |
| 0x1A4 | `int` | colorEndCycle | Tint end client cycle |
| 0x22C | `float` | exactMoveDestX | Exact move destination X (from extended info) |
| 0x230 | `float` | exactMoveDestZ | Exact move destination Z |
| 0x234 | `float` | exactMoveDestY | Exact move destination Y |
| 0x240 | - | animationState | Animation state buffer |
| 0xCF8 | `void *` | spotAnimTransforms | Spot animation transform data (bit 32) |
| 0xD95 | `byte` | isVisible | Visibility flag (VisibilityFlag block) |
| 0xD98 | `long` | npcTypePtr | Pointer to NPC type definition |
| 0xDA0 | `float` | forcedMoveStartX | Forced movement start X |
| 0xDA4 | `float` | forcedMoveStartY | Forced movement start Y |
| 0xDA8 | `float` | forcedMoveStartZ | Forced movement start Z |
| 0xDAC | `int` | forcedMoveStartCycle | Forced movement start timing |
| 0xDB0 | `float` | forcedMoveEndX | Forced movement end X |
| 0xDB4 | `float` | forcedMoveEndY | Forced movement end Y |
| 0xDB8 | `float` | forcedMoveEndZ | Forced movement end Z |
| 0xDBC | `int` | forcedMoveEndCycle | Forced movement end timing |
| 0xE28 | `void *` | extendedAnimBuffer | Extended animation slot buffer (8 slots) |
| 0xEF8 | `void *` | hitmarksAndHeadbars | HitmarksAndHeadbars component pointer |
| 0xF40 | `void *` | modelOverridePtr | Model override pointer (ModelOverride block) |
| 0xF48 | `void *` | modelOverrideRef | Model override reference |
| 0x1008 | `void *` | spotAnimListBegin | Spot anim primary list begin |
| 0x1010 | `void *` | spotAnimListEnd | Spot anim primary list end |
| 0x1028 | `void *` | spotAnimList2Begin | Spot anim secondary list begin |
| 0x1030 | `void *` | spotAnimList2End | Spot anim secondary list end |
| 0x1078 | `long` | npcTypeDefinition | NPC type definition reference |
| 0x1088 | `void *` | npcTypeDefPtr | NPC type definition pointer (for overlay data) |
| 0x10D0 | `int` | bodyTypeId | Body/model type override ID (ModelOverride block) |
| 0x1108 | `void *` | extendedAnimState | Extended animation state (bit 21) |
| 0x1120 | `uint[8]` | animSlotIds | Per-slot animation IDs (ExtendedAnimSlots) |
| 0x113C | `int[8]` | animSlotDelays | Per-slot animation delays (ExtendedAnimSlots) |
| 0x1158 | `int` | npcTypeId | Current NPC type ID |
| 0x115C | `uint` | hiddenMenuOpFlags | Hidden menu option flags (HiddenMenuOpFlags block, Engine: ONPC.HIDDEN_MENUOP_FLAGS) |
| 0x1160 | `byte` | booleanFlagValue | Boolean flag value (BooleanFlag block) |
| 0x1161 | `byte` | booleanFlagInit | Boolean flag initialization marker |

### NPC Hash Table Node

NPCs are stored in an EASTL-style hash table. Each node is 32 bytes:

| Offset | Type | Name | Description |
|--------|------|------|-------------|
| 0x00 | `int` | serverIndex | NPC server index (key) |
| 0x08 | `void *` | refCountCtrl | shared_ptr control block |
| 0x10 | `void *` | entityPtr | Pointer to NPCEntity object |
| 0x18 | `void *` | nextNode | Next node in hash chain |

Lookup function: `jag::NPCList::GetNPCNode` at `0x00312770`

---

## Phase 1: Existing NPC Updates (Bit-Packed)

```
8 bits: localNpcStartIndex
  - Starting index in the local NPC array for updates
  - NPCs before this index are copied to the new local list without updates

For each NPC from localNpcStartIndex to localNpcCount:
  1 bit: hasUpdate
    if 0:
      - NPC is stationary, copy to new local list
      - Update lastUpdateCycle to current serverCycle

    if 1:
      2 bits: movementType

      Type 0 (stationary with extended info):
        - Copy to new local list
        - Update lastUpdateCycle
        - Add to extended info processing list

      Type 1 (walk):
        - Copy to new local list
        - Update lastUpdateCycle
        3 bits: direction (CompassPoint, 0-7)
        - Call DecodeMoveCode with WALK speed
        1 bit: hasExtendedInfo
          if 1: add to extended info list

      Type 2 (run):
        - Copy to new local list
        - Update lastUpdateCycle
        1 bit: isRunning
          if 0 (walk):
            3 bits: direction
            - DecodeMoveCode with RUN speed
          if 1 (run):
            3 bits: direction1
            - DecodeMoveCode with RUN speed
            3 bits: direction2
            - DecodeMoveCode with RUN speed
        1 bit: hasExtendedInfo
          if 1: add to extended info list

      Type 3 (remove):
        - Add to removal list
        - Do NOT copy to new local list
```

### CompassPoint Directions

The 3-bit direction encodes 8 compass points used by `DecodeMoveCode` at `0x001e71e0`:

| Value | Direction | dX | dZ |
|-------|-----------|----|----|
| 0 | South | 0 | +1 |
| 1 | South-West | +1 | +1 |
| 2 | West | +1 | 0 |
| 3 | North-West | -1 | +1 |
| 4 | North | -1 | 0 |
| 5 | North-East | -1 | -1 |
| 6 | East | 0 | -1 |
| 7 | South-East | +1 | -1 |

Note: dX/dZ values are multiplied by a tile size constant (`DAT_00dc2d28`).

### Movement Speed Constants

Three global speed constant structures are referenced:

| Address | Name | Used For |
|---------|------|----------|
| 0x016e7d18 | walkSpeed | Walk (type 1) |
| 0x016e7d14 | runSpeed | Run walk-step (type 2, not running) |
| 0x016e7d1c | runSpeed2 | Run (type 2, both steps when running) |

---

## Phase 2: Adding New NPCs (Bit-Packed)

After processing existing NPCs, the handler reads new NPCs until fewer than 16 bits remain in the bit buffer:

```
while (packetSize * 8 - bitPosition > 15):
  16 bits: npcServerIndex
    if 0xFFFF: stop adding new NPCs (terminator)

  - Look up npcServerIndex in hash table
  - If not found, create new NPCEntity:
    1. Allocate from NPC pool (0x1190 bytes per entity)
    2. Create GraphNode for scene graph
    3. Initialize Entity (vtable, shared_ptr, etc.)
    4. Insert into hash table
  - Add to local NPC list
  - Update lastUpdateCycle

  coordBitWidth bits: coordX (relative X position)
  2 bits: plane (height level)
  coordBitWidth bits: coordZ (relative Z position)
  1 bit: jumpFlag (teleport vs interpolate)
  1 bit: hasExtendedInfo
    if 1: add to extended info list
  3 bits: directionIndex (initial facing direction, 0-7)

  - Convert direction to radians, compute sin/cos for facing
  - Set GraphNode direction quaternion

  16 bits: npcTypeId
  - Call vtable+0x220 (SetAppearanceNPC) with npcTypeId

  - Compute world position from local player position + relative offset
  - Apply coordinate bias: (value - halfBitRange) if value >= halfBitRange
  - Position = base + offset * tileSize + entitySizeOffset

  - Call vtable+0x170 (SetPosition) to place in world
```

### Coordinate Encoding

Position coordinates use a configurable bit width stored at NPC manager offset `0x1DC4`. The position is relative to the local player's position:

```
halfBitRange = 1 << (coordBitWidth - 1)
fullBitRange = 1 << coordBitWidth

rawCoord = gBit(coordBitWidth)
if rawCoord >= halfBitRange:
    relativeCoord = rawCoord - fullBitRange
else:
    relativeCoord = rawCoord

worldCoord = playerCoord + relativeCoord
```

---

## Phase 3: Extended Info Processing

After bit-packed phases, byte alignment is restored (`pos = (bitPos + 7) >> 3`). Then, for each NPC in the extended info list:

```
For each npcServerIndex in extInfoList:
  - Skip 2 bytes (extended info length prefix)
  - Call ProcessExtendedInfoNPC(npcManager, npcNode, worldId, clientCycle, packet)
```

### Extended Info Handler

**Function**: `jag::NPCEntity::ProcessExtendedInfoNPC` at `0x00209030`

### Flag Encoding

Extended info uses a variable-length flag system. Flags are read as 1-5 bytes:

```
byte1 = readByte()
flags = byte1

if flags & 0x40:           // bit 6 = continuation
  byte2 = readByte()
  flags += byte2 << 8

  if flags & 0x400:        // bit 10 = continuation
    byte3 = readByte()
    flags += byte3 << 16

    if flags & 0x40000:    // bit 18 = continuation
      byte4 = readByte()
      flags += byte4 << 24

      if flags & 0x40000000:  // bit 30 = continuation
        byte5 = readByte()
        flags += byte5 << 32
```

Bits 6, 10, 18, and 30 are **continuation bits** (not info blocks). The remaining bits each trigger a specific extended info block.

### Packet Scrambling

Extended info blocks use **scrambled packet reads**. Each read operation consults a cipher table at `packet+0x28` to determine how to unscramble the data:

| Function | Address | Description |
|----------|---------|-------------|
| `gScrambledByte` | `0x00313d70` | Read 1 byte with mode: 0=raw, 1=byte-0x80, 2=negate, 3=-0x80-byte |
| `gScrambledUbyte` | `0x00313cc0` | Read 1 unsigned byte with same modes |
| `gScrambledUshort` | `0x00313bf0` | Read 2 bytes with byte-order modes |
| `gT_obf_uint` | `0x00313910` | Read 4 bytes with byte-order modes |
| `gT_obf_medium` | `0x00313a20` | Read 3 bytes (24-bit) with byte-order modes |

The cipher table advances one entry per read, providing per-field scrambling unique to each packet.

---

## Extended Info Blocks

Each bit in the flag field triggers a specific block. Blocks are processed in a fixed order regardless of which bits are set. **Continuation bits (6, 10, 18, 30) are NOT info blocks.**

### Processing Order and Flag Mapping

| Order | Bit | Flag | Block Name | Description |
|-------|-----|------|------------|-------------|
| 1 | 21 | 0x200000 | ExtendedAnimations | 8-slot animation ID/delay array |
| 2 | 1 | 0x000002 | OverheadText | Overhead chat/say text |
| 3 | 7 | 0x000080 | FaceEntity | Face direction toward entity |
| 4 | 20 | 0x100000 | ChatText | NPC chat text with chat history |
| 5 | 33 | 0x200000000 | BooleanFlag | Sets byte at entity+0x1160 |
| 6 | 9 | 0x000200 | ForcedMovement | Forced movement (start/end coords + delays) |
| 7 | 24 | 0x1000000 | AnimBlock24 | Reads ushort + uint + byte (animation-related) |
| 8 | 22 | 0x400000 | BodyOverlay | Body/model overlay with customization colors |
| 9 | 17 | 0x020000 | HeadIcons | Head icon sprites (prayer/skull icons) |
| 10 | 19 | 0x080000 | ExtendedAnimSlots | Per-slot animation ID + delay values |
| 11 | 16 | 0x010000 | NpcTypeChange | Change NPC type/model ID |
| 12 | 0 | 0x000001 | ExactMove | Exact movement destination |
| 13 | 12 | 0x001000 | AnimBlock12 | Reads byte + 3 ushorts |
| 14 | 26 | 0x4000000 | AnimBlock26 | Reads ushort + uint + byte |
| 15 | 3 | 0x000008 | SetAppearance | NPC appearance/type change with anim reset |
| 16 | 28 | 0x10000000 | HiddenMenuOpFlags | Sets byte at entity+0x115C |
| 17 | 14 | 0x004000 | ModelOverride | Model override ID (clears old model) |
| 18 | 25 | 0x2000000 | SpotAnimRemoval | Remove + add spot anims by ID with transforms |
| 19 | 11 | 0x000800 | CachedAppearance | Cached appearance overlay (same structure as BodyOverlay) |
| 20 | 2 | 0x000004 | Hits | Damage splats / hitsplats |
| 21 | 23 | 0x800000 | HeadIconsCustom | Custom head icons (clears existing + adds new) |
| 22 | 13 | 0x002000 | AnimBlock13 | Reads ushort + uint + byte |
| 23 | 4 | 0x000010 | SpotAnim | Spot animation (graphics) effects |
| 24 | 32 | (BT bit 32) | SpotAnimTransforms | Spot animation transforms (rotation/scale/translation matrices) |
| 25 | 5 | 0x000020 | Headbars | Health bars above NPC |
| 26 | 15 | 0x008000 | AnimBlock15 | Reads byte + byte + ushort |
| 27 | 29 | 0x20000000 | VisibilityFlag | Sets entity visibility byte |
| 28 | 27 | 0x8000000 | PositionColor | Tinting/color overlay with start/end timing |

### Block Details

#### Bit 0 (0x01): ExactMove

Reads the NPC's exact movement destination coordinates.

```
ushort destX = gScrambledUshort()    // destination X * 2 + 1
ushort destZ = gScrambledUshort()    // destination Z * 2 + 1

entity.exactMoveDestX = (destX - 1) / 2.0    // float at +0x22C
entity.exactMoveDestZ = 0.0                   // float at +0x230
entity.exactMoveDestY = (destZ - 1) / 2.0    // float at +0x234
```

#### Bit 1 (0x02): OverheadText

Reads overhead text (say/chat) to display above the NPC.

```
string text = gStringCP1252ToUTF8()    // null-terminated string
entity->vtable+0x158(text)              // SetOverheadText
```

#### Bit 2 (0x04): Hits

Reads damage splats to display on the NPC. Entry point at `0x0020afa0`.

```
byte hitCount = gScrambledByte()

for i in 0..hitCount:
  ushort hitType = gSmart1or2()        // hit splat type
    if 0x7FFF: special handling (tinted/soaked)
    if 0x7FFE: special handling (blocked?)
  ushort damage = gSmart1or2()         // damage value
  ushort delay = gSmart1or2()          // display delay

  AddHitmark(entity.hitmarksAndHeadbars, entity.graphNode,
             hitType, damage, delay, clientCycle, ...)
```

#### Bit 3 (0x08): SetAppearance

Sets the NPC's appearance/type with animation reset. Entry point at `0x0020a490`.

```
ushort frameId = readUshort(DAT_00dc33f0)  // animation frame counter
entity.animationState.reset(frameId)        // offset +0x240

uint npcTypeId = gSmart2or4s()             // NPC definition ID
entity->vtable+0x220(npcTypeId)             // SetAppearanceNPC
```

#### Bit 4 (0x10): SpotAnim

Manages spot animations (graphic effects) on the NPC. Entry point at `0x0020b990`.

```
medium command = gT_obf_medium()    // 3-byte scrambled read
byte spotAnimSlot = (command << 8) >> 24   // extract slot index (signed)

switch spotAnimSlot:
  case 1:  // Add spot anim
    ushort spotAnimId = gScrambledUshort()
    uint delay = gT_obf_uint()
    byte height = gScrambledByte()
    // process spotanim addition

  case 2:  // Add spot anim (variant)
    // similar to case 1

  case 0x7F:  // Remove/clear spot anim
    entity->clearSpotAnim(slot)

  default:  // Unknown/no-op
    break
```

#### Bit 5 (0x20): Headbars

Updates health/status bars above the NPC. Entry point follows bit 4 processing.

```
int[4] headbarParams = for i in 0..4: gSmart2or4s()
byte headbarFlags = gScrambledByte()

// Process headbar parameters (type, fill, duration, etc.)
// Calls AddHeadbar at 0x005351d0
```

#### Bit 7 (0x80): FaceEntity

Makes the NPC face toward an entity or direction.

```
ushort targetIndex = gScrambledUshort()
uint animId = gT_obf_uint()
byte faceSpeed = gScrambledByte()
```

#### Bit 9 (0x200): ForcedMovement

Applies a forced movement (exact move) path with start/end coordinates and timing. Entry point at `0x0020a0b0`. Calls `jag::PathingEntity::SetForcedMovement` at `0x0053a060`.

```
sbyte startDeltaX = gScrambledUbyte()    // signed byte, tile-fine offset
sbyte startDeltaY = gScrambledUbyte()
sbyte startDeltaZ = gScrambledUbyte()
sbyte endDeltaX   = gScrambledUbyte()
sbyte endDeltaY   = gScrambledUbyte()
sbyte endDeltaZ   = gScrambledUbyte()
ushort startDelay = gScrambledUshort()   // start timing offset
ushort endDelay   = gScrambledUshort()   // end timing offset
ushort colorValue = gScrambledUshort()   // -> color lookup via 0x00b93c90

SetForcedMovement(entity, clientCycle,
    startDeltaX, startDeltaY, startDeltaZ,
    endDeltaX, endDeltaY, endDeltaZ,
    startDelay, endDelay)
```

Stored at entity offsets:
- `+0xDA0`: startX (float)
- `+0xDA4`: startY (float)
- `+0xDA8`: startZ (float)
- `+0xDB0`: endX (float)
- `+0xDB4`: endY (float)
- `+0xDB8`: endZ (float)
- `+0xDAC`/`+0xDBC`: start/end timing cycles

#### Bit 11 (0x800): CachedAppearance

Reads a cached appearance overlay with body customization data. Entry point at `0x0020aa00`. Shares the same internal structure as Bit 22 (BodyOverlay).

```
byte flags = gScrambledByte()

if flags & 0x1:
  // Special handling path (0x0020cebe)

if flags & 0x4:
  // Read recolor data from entity+0x1088 -> NPC type definition
  // Offsets +0x348 (ptr1), +0x330 (ptr2) in NPC type
  // Copies color palette data

if flags & 0x8:
  // Read retexture data from entity+0x1088
  // Offsets +0x390 (ptr1), +0x378 (ptr2) in NPC type
  // Copies texture palette data

// Constructs 0xE0 byte appearance structure
// Stores at global counter 0x014bc630 (auto-incrementing ID)
// Structure contains recolor/retexture mappings
```

#### Bit 12 (0x1000): AnimBlock12

Reads animation-related data.

```
byte  slot     = gScrambledByte()
ushort animId1 = gScrambledUshort()
ushort animId2 = gScrambledUshort()
ushort animId3 = gScrambledUshort()
```

#### Bit 13 (0x2000): AnimBlock13

Reads animation-related data (same format as bit 24 and bit 26).

```
ushort value1 = gScrambledUshort()
uint   value2 = gT_obf_uint()
byte   value3 = gScrambledByte()
```

#### Bit 14 (0x4000): ModelOverride

Overrides the NPC's model/body type. Entry point at `0x0020a400`. Clears any existing model override before setting the new one.

```
ushort modelId = gScrambledUshort()

if modelId == 0xFFFF:
  // Use default model from NPC type definition at +0x1078
  // Stores default at entity+0x2a8
else:
  // Clear old model override:
  entity.modelOverridePtr = null    // +0xF40
  entity.modelOverrideRef = null    // +0xF48
  // If old pointer existed, free it via 0x00314380

  entity.bodyTypeId = modelId       // stored at entity+0x10D0
```

#### Bit 15 (0x8000): AnimBlock15

Reads animation-related data.

```
byte  value1 = gScrambledByte()
byte  value2 = gScrambledByte()
ushort value3 = gScrambledUshort()
```

#### Bit 16 (0x10000): NpcTypeChange

Changes the NPC's type ID (transforms the NPC into a different type).

```
ushort npcTypeId = gScrambledUshort()

if npcTypeId == 0xFFFF:
  // Use default type from NPC definition at +0x1078
  entity.npcTypeId = entity.npcTypeDefinition.defaultTypeId  // +0x2a8
else:
  entity.npcTypeId = npcTypeId   // stored at entity+0x1158
```

#### Bit 17 (0x20000): HeadIcons

Updates head icon sprites (prayer icons, skull icon, etc.) displayed above the NPC. Entry point at `0x0020a270`. Comparable to player info HEAD_ICONS (bit 17).

```
ushort headIconCategoryId = gT_ushort()

// Read from cipher byte array
byte count = nextCipherByte()

if count == 0: return  // no icons to process

for i in 0..count (pairs, stepping by 2):
  byte  slotByte1 = gScrambledByte()
  ushort iconData1 = gT_ushort()
  slotIndex1 = slotByte1 & 0xFF
  spotAnimType1 = GetTypeBySlot(slotIndex1)  // 0x007c2030
  spotAnimType1->vtable+0x20(packet, buffer)  // decode icon

  // Store at entity+0x148 area
  // Calls 0x00ba98f0 and 0x003231b0 for sprite setup

  byte  slotByte2 = gScrambledByte()
  ushort iconData2 = gT_ushort()
  // Same processing for second icon in pair
```

#### Bit 19 (0x80000): ExtendedAnimSlots

Writes animation IDs and delay values into specific animation slots on the entity. Entry point at `~0x00209280`. Each entry specifies a slot index and the animation + delay to place there.

```
byte controlByte = nextCipherByte()
if controlByte == 0: return

slotMode = controlByte & 0x3

if slotMode == 2:
  // Read 2 entries:
  byte slot1 = gScrambledByte()      // slot index (0-7)
  uint animId1 = gT_obf_uint()      // animation ID
  int  delay1  = gT_obf_medium()    // delay (24-bit medium)
  entity[slot1 * 4 + 0x1120] = animId1
  entity[slot1 * 4 + 0x113C] = delay1
  // fall through to read second entry

if slotMode >= 1:
  byte slot2 = gScrambledByte()
  uint animId2 = gT_obf_uint()
  int  delay2  = gT_obf_medium()
  entity[slot2 * 4 + 0x1120] = animId2
  entity[slot2 * 4 + 0x113C] = delay2

if controlByte > slotMode:
  // Loop reading 4 additional entries per iteration:
  for each group of 4 until count reached:
    for j in 0..4:
      byte slotN = gScrambledByte()
      uint animIdN = gT_obf_uint()
      int  delayN  = gT_obf_medium()
      entity[slotN * 4 + 0x1120] = animIdN
      entity[slotN * 4 + 0x113C] = delayN
```

Entity offsets for animation slots (slot 0-7):
- `entity + slot*4 + 0x1120`: animation ID (uint)
- `entity + slot*4 + 0x113C`: animation delay (int, 24-bit medium)

#### Bit 20 (0x100000): ChatText

Reads NPC chat text and processes it through the chat system. Entry point at `0x0020a1a0`. Unlike OverheadText (bit 1) which just displays text, this block goes through a chat filter/history path.

```
string chatText = gStringCP1252ToUTF8()    // null-terminated string

// Compare with empty string sentinel at 0x10da05f
if chatText != "":
  entity->processChat(chatText)            // at entity+0x90
  // Calls 0x001f6110 for chat history processing
```

#### Bit 21 (0x200000): ExtendedAnimations

Updates up to 8 animation slots with animation IDs and delays. Entry point at `0x0020a790`. The handler uses two parallel buffer structures of size 0x28 each.

```
byte flags = gScrambledByte()

// Initialize 8 slot pairs in two buffers to -1 (0xFFFFFFFF):
buffer1[0..7] = -1    // at [RSP + 0x240]
buffer2[0..7] = -1    // at [RSP + 0x1F0]

// Each bit in flags controls a slot pair:
if flags & 0x01: read slot 0 data via 0x00cc77f0
if flags & 0x02: read slot 1 data
if flags & 0x04: read slot 2 data
if flags & 0x08: read slot 3 data
if flags & 0x10: read slot 4 data
if flags & 0x20: read slot 5 data
if flags & 0x40: read slot 6 data
if flags & 0x80: read slot 7 data (sign bit)

// After populating buffers, applies to entity+0x1108
// (extended animation state structure)
```

#### Bit 22 (0x400000): BodyOverlay

Reads body overlay / customization data including recoloring and retexturing. Entry point at `0x00209ce0`. Complex handler that reads from the NPC type definition's color/texture palette data.

```
byte flags = gScrambledByte()

if flags & 0x1:
  // Special processing path

if flags & 0x2:
  // Read additional data

if flags & 0x4:
  // Recolor data from NPC type definition:
  // entity+0x1088 -> npcTypeDef+0x348 (recolor source ptr)
  // entity+0x1088 -> npcTypeDef+0x330 (recolor data ptr)
  // Copies and modifies color palette entries

if flags & 0x8:
  // Retexture data from NPC type definition:
  // entity+0x1088 -> npcTypeDef+0x390 (retexture source ptr)
  // entity+0x1088 -> npcTypeDef+0x378 (retexture data ptr)
  // Copies and modifies texture palette entries

// Constructs 0xE0 byte overlay structure at [RSP + 0x240]
// Assigns auto-incrementing ID from global counter at 0x014bc630
```

#### Bit 23 (0x800000): HeadIconsCustom

Custom head icon management. Entry point at `0x0020ba20`. Clears existing head icons before adding new ones. Comparable to player info HEAD_ICONS_CUSTOM (bit 23).

```
ushort headIconCategoryId = gT_ushort()

// Clear existing head icons:
clearHeadIcons(entity+0x150, entity+0x158)  // via 0x005b2350
entity.headIconState = 0                     // +0x160

byte count = nextCipherByte()
if count == 0: return

for i in 0..count (pairs, stepping by 2):
  byte  slotByte1 = gScrambledByte()
  ushort iconData1 = gT_ushort()
  spotAnimType1 = GetTypeBySlot(slotByte1 & 0xFF)
  spotAnimType1->vtable+0x20(packet, buffer)
  // Process and attach head icon sprite

  byte  slotByte2 = gScrambledByte()
  ushort iconData2 = gT_ushort()
  // Same processing for second icon
```

#### Bit 24 (0x1000000): AnimBlock24

Reads animation-related data (same wire format as bit 13 and bit 26).

```
ushort value1 = gScrambledUshort()
uint   value2 = gT_obf_uint()
byte   value3 = gScrambledByte()
```

#### Bit 25 (0x2000000): SpotAnimRemoval

Removes existing spot animations by ID and optionally adds new ones with transform data. Entry point at `~0x00209541`. Comparable to player info SPOT_ANIM_REMOVAL (bit 25).

```
byte count = nextCipherByte()
if count == 0: go to post-removal

// Phase 1: Remove spot anims
for i in 0..count:
  short spotAnimId = gSmart1or2()     // -1 = clear all
  if spotAnimId == -1:
    // Clear all spot anims
  else:
    // Search entity+0x1008..+0x1010 (primary list)
    // Search entity+0x1028..+0x1030 (secondary list)
    // Each entry is 0x20 bytes, check ptr+0x74 for matching ID
    // Remove matching entries

// Phase 2: Add new spot anims
byte newCount = gScrambledByte()
if newCount == 0: return

for i in 0..newCount:
  byte  slotId     = gScrambledByte()
  ushort animId    = gScrambledUshort()
  uint   flags     = gT_obf_uint()       // 0xFFFF check for special handling
  byte   delay     = gScrambledByte()
  medium transform = gT_obf_medium()     // packed transform/bone data

  // Extract from medium:
  //   bits 22-31: animation param
  //   bit 15: boolean flag
  //   bits 0-14: frame offset

  // Create spot anim via FUN_00200ce0 with transform data
```

#### Bit 26 (0x4000000): AnimBlock26

Reads animation-related data (same wire format as bit 13 and bit 24).

```
ushort value1 = gScrambledUshort()
uint   value2 = gT_obf_uint()
byte   value3 = gScrambledByte()
```

#### Bit 27 (0x8000000): PositionColor

Applies a tinting/color overlay to the entity with start and end timing. Entry point at `0x0020a620`. Comparable to player info POSITION_COLOR (bit 22).

```
ubyte color1 = gScrambledUbyte()     // packed color high bits
ubyte color2 = gScrambledUbyte()     // mid bits (& 0x7)
ubyte color3 = gScrambledUbyte()     // low bits (& 0x7F)
byte  brightness = gScrambledByte()  // brightness/opacity value
ushort startOffset = gScrambledUshort()  // start cycle offset
ushort endOffset = gScrambledUshort()    // end cycle offset

// Assemble packed color:
packedColor = (color1 << 10) | ((color2 & 0x7) << 7) | (color3 & 0x7F)

// Unpack to RGBA via 0x00b94d60:
rgba = unpackColor(packedColor)    // returns 32-bit ARGB
red   = (rgba >> 24) & 0xFF
green = (rgba >> 16) & 0xFF
blue  = (rgba >> 8) & 0xFF

// Convert to floats with scaling:
scale = DAT_00dc2d88  // float scale factor
entity.colorR = red * scale       // +0x190
entity.colorG = green * scale     // +0x194
entity.colorB = blue * scale      // +0x198
entity.colorA = brightness * DAT_00dc2d8c  // +0x19C (different scale)

entity.colorStartCycle = clientCycle + startOffset  // +0x1A0
entity.colorEndCycle   = clientCycle + endOffset    // +0x1A4
```

#### Bit 28 (0x10000000): HiddenMenuOpFlags

Sets the hidden menu option flags (controls which right-click options are suppressed). Entry point at `0x0020a460`. Engine: `ONPC.HIDDEN_MENUOP_FLAGS = 0x115C`.

```
byte flags = gScrambledByte()
entity.hiddenMenuOpFlags = flags    // stored as uint at entity+0x115C
```

#### Bit 29 (0x20000000): VisibilityFlag

Sets the entity's visibility/hidden state. Entry point at `0x0020a5e0`.

```
byte isVisible = gScrambledByte()
entity.isVisible = (isVisible == 1)    // stored at entity+0xD95
```

#### Bit 32 (BT bit 32): SpotAnimTransforms

Complex spot animation transform data with per-slot rotation, scale, and translation matrices. Entry point at `0x0020b300`. Comparable to player info SPOT_ANIMS (bit 24).

```
sbyte slotCount = gScrambledUbyte()    // signed, negative = error
if slotCount == 0: skip to end

// Access entity+0xCF8 (spot anim transform array)
// Max 8 slots per entity

for i in 0..slotCount:
  short flags1 = gScrambledShort()
  short flags2 = gScrambledShort()

  // flags1 bits control which transform components to read:
  if flags1 & 0x400: uint startTransformId = gScrambledShort()
  if flags1 & 0x800: uint endTransformId = gScrambledShort()

  if flags1 & 0x001: uint translationX
  if flags1 & 0x002: uint translationY
  if flags1 & 0x004: uint translationZ
  if flags1 & 0x008: uint rotationX -> angle = value * angleScale
  if flags1 & 0x010: uint rotationY -> angle lookup
  if flags1 & 0x020: uint rotationZ -> angle lookup
  if flags1 & 0x040: model-space transform flag
  if flags1 & 0x080: uint scaleX -> float * scale
  if flags1 & 0x100: uint scaleY -> float * scale
  if flags1 & 0x200: uint scaleZ -> float * scale

  // Build 3x3 rotation matrix via EulerToRotationMatrix (0x001e2d90)
  // using sincosf for each Euler angle
  // Combine with scale and translation into 4x4 transform

  if flags1 & 0x40:
    // Model-space: multiply with existing entity transform
  else:
    // World-space: apply directly

  // Store transform at entity+0xCF8 spot anim data
```

#### Bit 33 (BT bit 33): BooleanFlag

Sets a single-byte flag on the entity. Entry point at `0x0020a160`.

```
ubyte value = gScrambledUbyte()

if value == 0x80:
  // Special handling (jump to 0x0020cbd0)
else:
  if entity.flagInitialized == 0:    // +0x1161
    entity.flagInitialized = 1       // mark as set
  entity.flagValue = value           // stored at entity+0x1160
```

---

## NPC_ANIM_SPECIFIC Packet

A separate fixed-size packet for directly setting a specific animation slot on an NPC.

| Property | Value |
|----------|-------|
| Opcode | **165** (0xa5) |
| Size | Fixed 9 bytes |
| Handler | `jag::packethandlers::NPCInfo::decodeAnimSpecific` at `0x00279ce0` |

### Wire Format

```
Offset  Size  Description
0       4     Animation ID (big-endian int, byte-swapped for endianness)
4       1     Slot index (byte, converted to 0-based: value - 0x80)
5       2     NPC type ID? (big-endian ushort, endian-aware)
7       2     NPC server index (big-endian ushort, endian-aware)
```

The handler looks up the NPC by server index, ensures its extended animation buffer (+0xe28) exists, and writes the animation ID and delay value into the specified slot.

---

## Key Functions

| Address | Name | Signature | Description |
|---------|------|-----------|-------------|
| `0x0027a030` | `jag::packethandlers::NPCInfo::decode` | `void*(long*, Packet*, uint*)` | Main NPC_INFO handler |
| `0x00279ce0` | `jag::packethandlers::NPCInfo::decodeAnimSpecific` | `void*(long*, long)` | NPC_ANIM_SPECIFIC handler |
| `0x00209030` | `jag::NPCEntity::ProcessExtendedInfoNPC` | `void(long, long, uint, uint, Packet*)` | Extended info block decoder |
| `0x001e71e0` | `jag::NPCList::DecodeMoveCode` | `void(long, int, void*)` | Movement direction decoder |
| `0x00312770` | `jag::NPCList::GetNPCNode` | `int*(long, int)` | Hash table NPC lookup |
| `0x0053c470` | `jag::PathingEntity::ApplyExtendedAnimations` | `void(long)` | Apply queued animation state |
| `0x00313d70` | `jag::Packet::gScrambledByte` | `ulong(long)` | Scrambled byte read |
| `0x00313cc0` | `jag::Packet::gScrambledUbyte` | `uint(long)` | Scrambled unsigned byte read |
| `0x00313bf0` | `jag::Packet::gScrambledUshort` | `ushort(long)` | Scrambled unsigned short read |
| `0x00313910` | `jag::Packet::gT_obf_uint` | `ulong(long)` | Scrambled unsigned int read |
| `0x00313a20` | `jag::Packet::gT_obf_medium` | `int(long)` | Scrambled 3-byte medium read |
| `0x001e3620` | `jag::Packet::Bit::gBit` | `uint(PacketBit*, uint)` | Read N bits from packet |
| `0x001c20e0` | `jag::Packet::gSmart1or2` | `ushort(Packet*)` | Variable 1-or-2 byte unsigned smart |
| `0x001c1d80` | `jag::Packet::gSmart2or4s` | `uint(Packet*)` | Variable 2-or-4 byte signed smart |
| `0x001c1cc0` | `jag::Packet::gT_ushort` | `ushort(Packet*)` | Read unsigned short (endian-aware) |
| `0x001d6b20` | `jag::Packet::gT_ushort_raw` | `ushort(long)` | Read unsigned short (raw, no scramble) |
| `0x00313b20` | `jag::Packet::gScrambledShort` | `long(void*)` | Scrambled signed short read |
| `0x0053a060` | `jag::PathingEntity::SetForcedMovement` | `void(PathingEntity*, ...)` | Set forced movement path with coords + timing |
| `0x00b93c90` | (color lookup) | `uint(ushort)` | Look up color value from ushort |
| `0x00b94d60` | (color unpack) | `uint(ushort)` | Unpack 17-bit color to 32-bit ARGB |
| `0x001e2d90` | `jag::math::EulerToRotationMatrix` | `float*(float,float,float,float*)` | Euler angles to 3x3 rotation matrix |
| `0x007c2030` | `jag::SpotAnim::GetTypeBySlot` | `void*(int)` | Get spot anim type object by slot (0-3) |
| `0x005b2350` | (clear head icons) | `void(void*,void*)` | Clear head icon list entries |
| `0x00200ce0` | (create spot anim) | `void(...)` | Create and attach spot animation to entity |

### Debug Symbol Names (from `parsed_functions.txt`, different build)

These are ground-truth C++ symbol names from the developers. Addresses do not match our binary but names are authoritative:

| Symbol Address (other build) | Symbol Name |
|------------------------------|-------------|
| `0x412cb0` | `jag::NPCList::DecodeMoveCode(shared_ptr<NPCEntity> const&, game::CompassPoint, MoveSpeed const*)` |
| `0x498700` | `jag::NPCEntity::ProcessExtendedInfo(Packet &, shared_ptr<ExtendedInfoState> &)` |
| `0x16fbd0` | `jag::NPCEntity::SetAppearanceNPC(int)` |
| `0x3993c0` | `jag::NPCEntity::LoadNPCType(void)` |
| `0x16e1d0` | `jag::NPCList::ClearNPCList(void)` |
| `0x16e440` | `jag::NPCList::ChangeMainState(MainState, MainState)` |
| `0xb5770` | `jag::NPCList::GetNPC(int)` |
| `0xcd030` | `jag::NPCList::IterateNPCs(function<void(shared_ptr<NPCEntity> const&)> const&)` |
| `0x310e00` | `jag::PathingEntity::QueueExtendedInfoPacket(Packet &, uint)` |
| `0x337800` | `jag::PathingEntity::ProcessExtendedInfoPackets(void)` |
| `0x4125f0` | `jag::PathingEntity::AddRouteWaypointFine(game::CoordFine const&, MoveSpeed const*)` |

---

## Comparison with PLAYER_INFO

| Feature | PLAYER_INFO | NPC_INFO |
|---------|-------------|----------|
| Passes | 4 (hi-res active, hi-res inactive, lo-res active, lo-res inactive) | 2 (update existing, add new) |
| Resolution tiers | Yes (high/low) | No |
| Index size | 11 bits (2048 players) | 16 bits (65535 NPCs) |
| Position encoding | Absolute with chunking | Relative to local player |
| Coordinate bits | Fixed per tier | Configurable (stored at +0x1DC4) |
| Extended info flags | Variable-length (1-4 bytes, bit 2/5/3 continuation) | Variable-length (1-5 bytes, bit 6/10/18/30 continuation) |
| Extended info blocks | ~22 block types | 28 block types |
| Scrambled reads | Yes | Yes (same cipher system) |
| Entity size | Different | 0x1190 bytes |

### Cross-Reference: Shared Extended Info Blocks

Many extended info blocks serve the same purpose between NPC_INFO and PLAYER_INFO but use different flag bit assignments:

| Block Purpose | NPC Bit | Player Bit | Notes |
|---------------|---------|------------|-------|
| Overhead Text | 1 | N/A (via SAY_TEXT bit 6) | NPC: simple text; Player: has bounds params |
| Hits/Hitmarks | 2 | 3 | Same AddHitmark/AddHeadbar calls |
| SpotAnim | 4 | N/A | NPC: medium+ushort+uint+byte pattern |
| Headbars | 5 | 3 (shared with Hits) | Player combines hits+headbars in one block |
| FaceEntity | 7 | 5 | Same ushort+uint+byte format |
| ForcedMovement | 9 | 4 | Same SetForcedMovement call, 6 bytes + 3 ushorts |
| HeadIcons | 17 | 17 | Both use gT_ushort + count + icon entries |
| ChatText | 20 | 15/18 | Player has CHAT_TEXT and OVERHEAD_CHAT separately |
| ExtendedAnimations | 21 | N/A | NPC-specific 8-slot animation buffer |
| BodyOverlay | 22 | 9 (CACHED_APPEARANCE) | Same 0xE0 structure, reads from NPC type def |
| HeadIconsCustom | 23 | 23 | Both clear + re-add pattern |
| SpotAnimTransforms | 32 | 24 | Same Euler rotation matrix + scale + translation |
| SpotAnimRemoval | 25 | 25 | Same remove-by-ID + add-new pattern |
| PositionColor | 27 | 22 | Same packed color + timing format |
| VisibilityFlag | 29 | 21 (BOOL_FLAG) | NPC: entity+0xD95; Player: entity+0x1071 |
| NpcTypeChange | 16 | N/A | NPC-specific |
| SetAppearance | 3 | 0 | NPC: SetAppearanceNPC; Player: SetAppearanceAsPlayer |
| ModelOverride | 14 | N/A | NPC-specific model override at +0x10D0 |
| BooleanFlag | 33 | N/A | NPC-specific byte at +0x1160 |
| HiddenMenuOpFlags | 28 | N/A | NPC-specific hidden right-click options at +0x115C |

---

## Notes

- The NPC pool uses a slab allocator with 0x1190-byte chunks. Pool management is at `DAT_014bf480`.
- The `coordBitWidth` at NPC manager offset `0x1DC4` is set by the server and determines how many bits encode each coordinate. Typical values are 8-11 bits depending on world size.
- The cipher table for scrambled reads is initialized per-packet and provides a sequence of scrambling modes. The table pointer at `packet+0x28` advances one entry per scrambled read call.
- The 0x0053c470 function (`ApplyExtendedAnimations`) processes 8-slot animation buffers. Each slot holds an animation ID and delay, using -1 as the "empty" sentinel value.
- Animation-related blocks (bits 12, 13, 15, 24, 26) all read small fixed-size payloads (ushort+uint+byte or byte+byte+ushort). Their exact purposes may relate to different animation modes (base anim, override anim, sequence, etc.) but the specific semantics need further investigation through runtime testing.
- The BodyOverlay (bit 22) and CachedAppearance (bit 11) blocks share identical structure layouts. Both read recolor/retexture data from the NPC type definition and construct 0xE0-byte overlay structures. The difference is likely in how the overlay is applied (immediate vs cached).
- The `nextCipherByte()` notation refers to reading the next byte from the cipher/scramble table at `packet+0x28` area, separate from the packet data stream itself. This is used by HeadIcons, HeadIconsCustom, ExtendedAnimSlots, and SpotAnimRemoval to encode iteration counts.
- SpotAnimTransforms (bit 32) uses BT (bit test) instruction instead of TEST because the flag value exceeds 32 bits. The BT instruction tests bit 32 of a 64-bit register.
- The global auto-incrementing counter at `0x014bc630` provides unique IDs for appearance overlay structures (used by both BodyOverlay and CachedAppearance blocks).
