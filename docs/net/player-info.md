# PLAYER_INFO Protocol

The PLAYER_INFO packet is the primary mechanism for synchronizing player positions, movements, and extended information (appearance, combat, chat, etc.) between server and client. It is sent once per game tick for every logged-in player.

## Overview

The packet uses **bit-level encoding** for position updates and **byte-level encoding** for extended info blocks. Players are divided into two resolution tiers:

- **High-resolution**: Nearby players with full movement tracking (walk/run/teleport)
- **Low-resolution**: Distant players with coarse chunk-level position tracking

## Packet Structure

The PLAYER_INFO packet is processed by `jag::PlayerList::ProcessPlayerInfo` at `0x002228f0`. It consists of **four bit-packed passes** followed by a **slot reassignment phase** and finally **extended info processing**.

### Processing Passes

```
Pass 1: High-res ACTIVE players (previously moving)
  For each player in the high-res active list:
    1 bit: hasUpdate
      if 1: call GetHighResolutionPlayerPosition (full movement decode)
      if 0: read skip count -> mark stationary
    Skip count: 2 bits mode
      0 = no more skips (0 players)
      1 = 5-bit count
      2 = 8-bit count
      3 = 11-bit count

Pass 2: High-res INACTIVE players (previously stationary)
  For each player in the high-res inactive list:
    1 bit: hasUpdate
      if 1: call GetHighResolutionPlayerPosition
      if 0: call ReadStationary -> skip count
    Skip count: same encoding as Pass 1

Pass 3: Low-res ACTIVE players
  For each player in the low-res active list:
    1 bit: hasUpdate
      if 1: call GetLowResolutionPlayerPosition (may promote to high-res)
      if 0: call ReadStationary -> skip count

Pass 4: Low-res INACTIVE players
  For each player in the low-res inactive list:
    1 bit: hasUpdate
      if 1: call GetLowResolutionPlayerPosition
      if 0: call ReadStationary -> skip count
```

After all four passes, byte alignment is restored, player slots are reassigned between active/inactive lists, and extended info is processed.

### Slot Reassignment

After bit-packed passes:
- The local player (index 1) is always processed first
- Remaining players (indices 2..2047) are processed in groups of 6
- Each player's `active` flag (offset +0x26) is swapped with `stationary` flag (+0x27)
- Players with entity references (`+0x38 != 0`) go to the high-res list
- Players without entity references go to the low-res list

---

## High-Resolution Position Updates

**Function**: `jag::PlayerList::GetHighResolutionPlayerPosition` at `0x002082e0`

```
1 bit: hasExtendedInfo
  if 1: add player index to extended info processing list

2 bits: movementType
  0 = No movement (stationary)
  1 = Walk (1 tile)
  2 = Run (2 tiles)
  3 = Teleport
```

### Type 0: No Movement / Remove from High-Res

If `hasExtendedInfo = 0`:
- For non-local player: extract full position from PathingEntity, compute direction, and call entity removal pipeline
- Followed by 1 bit: if set, recursively call `GetLowResolutionPlayerPosition` (demote to low-res)

If `hasExtendedInfo = 1`:
- Player stays in high-res, just needs extended info update

### Type 1: Walk (1 tile)

```
3 bits: direction (8 cardinal directions)
1 bit: hasSecondStep (run flag)

Direction encoding (0x200 = 512 tile-fine units per step):
  0: dX=-0x200, dZ=-0x200  (SW)
  1: dZ=-0x200             (S)
  2: dX=+0x200, dZ=-0x200  (SE)
  3: dX=-0x200             (W)
  4: dX=+0x200             (E)
  5: dX=-0x200, dZ=+0x200  (NW)
  6: dZ=+0x200             (N)
  7: dX=+0x200, dZ=+0x200  (NE)

If hasSecondStep:
  2 bits: second direction (4 directions, 0x200 unit steps)
    0: dX=-0x200
    1: dZ=+0x200 (wait, actually checked: 1=dX-0x200, see switch)
    Actually: 0=dZ+0x200, 1=dX-0x200, 2=dX+0x200, 3=dZ-0x200
```

### Type 2: Run (2 tiles)

```
4 bits: direction (16 two-tile directions)
  Each encodes dX and dZ offsets in multiples of 0x200 and 0x400:

  0:  dX=-0x400, dZ=-0x400
  1:  dX=-0x200, dZ=-0x400
  2:  dX=0,      dZ=-0x400
  3:  dX=+0x200, dZ=-0x400
  4:  dX=+0x400, dZ=-0x400
  5:  dX=-0x400, dZ=-0x200
  6:  dX=+0x400, dZ=-0x200
  7:  dX=-0x400, dZ=0
  8:  dX=+0x400, dZ=0
  9:  dX=-0x400, dZ=+0x200
  10: dX=+0x400, dZ=+0x200
  11: dX=-0x400, dZ=+0x400
  12: dX=-0x200, dZ=+0x400
  13: dX=0,      dZ=+0x400
  14: dX=+0x200, dZ=+0x400
  15: dX=+0x400, dZ=+0x400
```

### Type 3: Teleport

```
1 bit: isLargeDistance
  if 0 (small teleport - 15 bits):
    5 bits: signed dX (range -16..+15, in 0x200-unit tiles)
    5 bits: signed dZ (range -16..+15, in 0x200-unit tiles)
    2 bits: level delta
    3 bits: moveSpeed (index into speed table at 0x016e7d10)

    If moveSpeed == 2 (instant teleport):
      Uses vtable+0x170 for absolute position set
    Else:
      Stores speed reference and calls movement interpolation

  if 1 (large teleport - 30 bits):
    14 bits: absolute Z coordinate (tile-fine, multiplied by 0x200)
    14 bits: absolute X coordinate (tile-fine, multiplied by 0x200)
    2 bits: level change

    Position is XORed with current position to get absolute coords:
      newLevel = (levelDelta + currentLevel) & 3
      newX = (X_raw * 0x200 + currentX) & 0x7FFFFF
      newZ = (Z_raw + currentZ) & 0x7FFFFF
```

---

## Low-Resolution Position Updates

**Function**: `jag::PlayerList::GetLowResolutionPlayerPosition` at `0x00206e90`

```
2 bits: updateType
  0 = New player (transition to high-res)
  1 = Level change only
  2 = Small movement (1 chunk)
  3 = Large movement (multi-chunk)
```

### Type 0: New Player (Promote to High-Res)

```
1 bit: recursiveFlag (if set, recursively call GetLowResolutionPlayerPosition again)
6 bits: Z chunk coordinate
6 bits: X chunk coordinate
1 bit: hasExtendedInfo (if set, add to extended info list)

Creates a new PlayerEntity from the entity pool.
Transfers cached appearance data (equipment, overhead icons).
Sets absolute position: X = chunk * 0x40 * 0x200, Z = chunk * 0x40 * 0x200
```

### Type 1: Level Change

```
2 bits: level delta (added to current level, masked & 3)
```

### Type 2: Small Movement (1 chunk)

```
5 bits packed:
  2 bits: level delta
  3 bits: direction (8 cardinal directions)

Direction encoding (1 chunk unit = +/-1 to X or Z coordinate):
  0: dX=-1, dZ=-1
  1: dZ=-1
  2: dX=+1, dZ=-1
  3: dX=-1
  4: dX=+1
  5: dX=-1, dZ=+1
  6: dZ=+1
  7: dX=+1, dZ=+1
```

### Type 3: Large Movement

```
20 bits packed:
  2 bits: level delta
  8 bits: Z coordinate delta (unsigned)
  8 bits: X coordinate delta (unsigned)
  2 bits: moveSpeed
```

---

## ReadStationary (Skip Count)

**Function**: `jag::PlayerList::ReadStationary` at `0x00bc4a80`

When a player hasn't moved, the server sends a skip count to batch multiple stationary players:

```
2 bits: sizeMode
  0 = no more stationary players (return 0)
  1 = 5-bit skip count (up to 31)
  2 = 8-bit skip count (up to 255)
  3 = 11-bit skip count (up to 2047)
```

---

## Extended Info Blocks

**Function**: `jag::PlayerEntity::ProcessExtendedInfo` at `0x00202180`

Extended info is processed for each player that was flagged during the position update passes. The data uses **obfuscated byte readers** that apply per-read transformations (add/subtract 128, negate, byte-swap) determined by a cipher stream.

### Flag Field Encoding

The flag field is variable-length (1-4 bytes), with expansion bits indicating whether more bytes follow:

```
Byte 0: 8 bits of flags
  If bit 2 (0x04) is set -> read byte 1
Byte 1: 8 more bits (shifted)
  If bit 5 (0x20) is set -> read byte 2
Byte 2: 8 more bits (shifted)
  If bit 3 (0x08) is set -> read byte 3
Byte 3: final 8 bits
```

This gives a maximum of ~27 usable flag bits.

### Flag Bit Reference

All flag bits and their extended info block formats:

| Flag | Hex | Block Name | Description |
|------|-----|------------|-------------|
| 0 | 0x1 | APPEARANCE | Full appearance data (SetAppearanceAsPlayer) |
| 1 | 0x2 | FACE_DIRECTION | obf_ushort -> face direction angle |
| 3 | 0x8 | HITMARKS | Hit splats (count + hitmark data + headbars) |
| 4 | 0x10 | FORCED_MOVEMENT | 6 byte deltas + 2 ushort durations |
| 5 | 0x20 | FACE_ENTITY | obf_medium -> face entity index + type |
| 6 | 0x40 | SAY_TEXT | 4 smart values + byte + overhead text data |
| 7 | 0x80 | UNKNOWN_0x80 | obf_ushort + obf_uint + obf_byte |
| 8 | 0x100 | UNKNOWN_0x100 | obf_ushort + obf_uint + obf_byte |
| 9 | 0x200 | CACHED_APPEARANCE | Raw byte block (cached appearance data) |
| 10 | 0x400 | OVERHEAD_OPACITY | obf_byte -> overhead opacity value |
| 11 | 0x800 | UNKNOWN_0x800 | obf_ushort + obf_uint + obf_byte |
| 14 | 0x4000 | UNKNOWN_0x4000 | obf_byte + obf_byte + obf_ushort |
| 15 | 0x8000 | CHAT_TEXT | gStringCP1252ToUTF8 + chat history |
| 16 | 0x10000 | UNKNOWN_0x10000 | obf_ushort + obf_uint + obf_byte |
| 17 | 0x20000 | HEAD_ICONS | gT_ushort + count + icon entries |
| 18 | 0x40000 | OVERHEAD_CHAT | gStringCP1252ToUTF8 + byte flags + chat |
| 20 | 0x100000 | UNKNOWN_0x100000 | obf_byte + 3x obf_ushort |
| 21 | 0x200000 | BOOL_FLAG | obf_byte -> boolean at +0x1071 |
| 22 | 0x400000 | POSITION_COLOR | Position offsets + RGB color data |
| 23 | 0x800000 | HEAD_ICONS_CUSTOM | gT_ushort + count + custom icon entries |
| 24 | 0x1000000 | SPOT_ANIMS | Spot animation list with transforms |
| 25 | 0x2000000 | SPOT_ANIM_REMOVAL | Remove spot anim by ID |
| 26 | 0x4000000 | UNKNOWN_0x4000000 | obf_ushort + obf_uint + obf_byte |

---

## Extended Info Block Details

### APPEARANCE (flag 0x1)

**Function**: `jag::PlayerEntity::SetAppearanceAsPlayer` at `0x001f6170`

The appearance block is the most complex extended info. It is queued via `jag::PathingEntity::QueueExtendedInfoPacket` at `0x001f7450` for deferred processing.

```
obf_byte: dataLength
raw bytes[dataLength]: appearance data (copied from packet)

Queued appearance data format:
  Byte 0: flags
    Bit 0x40: has title ID
    Bit 0x02: has equipment list
    Bit 0x04: use ushort model IDs (instead of byte+byte+byte)
    Bit 0x80: alternate title table lookup
    Bits 3-5: (flags >> 3) & 7 + 1 = combat level display mode
    Bit 0x01: has detailed appearance data

  If flag 0x40:
    gSmart1or2: titleId

  If flag 0x02: Equipment block
    byte: equipmentCount
    For each equipment slot:
      ushort: modelId (or gT_ushort)
      byte: colorIndex

  byte: gender (0=male, 1=female)

  If flag !0x04: Simple model encoding
    byte: bodyType
    byte: unknown
    byte: modelId (0xFF = -1 meaning none)
  Else: Extended model encoding
    ushort: modelId (0xFFFF = -1)
    ushort: secondaryModelId
    ushort: tertiaryModelId

  byte: overheadIconCount
  If count > 0:
    For each icon:
      ushort: spriteId1
      ushort: spriteId2
      ushort: spriteId3
      ushort: spriteId4
      byte: unknown
  Else:
    10 bytes of 0xFF (no icons)

  After model data:
    ushort: titleId (looked up from title table)
    String: playerName (gStringCP1252ToUTF8)
    byte: combatLevel

  If flag !0x04:
    byte: totalLevel
    byte: legacyModelId (0xFF = none -> -1)
  Else:
    ushort: modelOverride (0xFFFF = none -> -1)
    byte: totalLevel matches combatLevel

  byte: skillLevel

  Colors and animations follow in the remaining data.
```

### HITMARKS (flag 0x8)

```
obf_byte: hitmarkCount
If hitmarkCount > 0:
  For each hitmark:
    gSmart1or2: hitmarkType
    If type == 0x7FFF: Extended hitmark
      gSmart1or2: type
      gSmart1or2: damage
      gSmart1or2: soak/absorption
      gSmart1or2: delay
    Else if type == 0x7FFE: Tinted hitmark
      obf_byte: tintType
      damage = type, soak = -1, delay = -1
    Else: Standard hitmark
      gSmart1or2: damage
      soak = -1, delay = -1
    gSmart1or2: duration
    -> calls AddHitmark on the entity's hitmark manager

obf_byte: headbarCount
If headbarCount > 0:
  For each headbar:
    gSmart1or2: headbarType
    gSmart1or2: duration
    If duration != 0x7FFF:
      gSmart1or2: delay
      obf_byte: startWidth
      If duration != 0:
        obf_byte: endWidth
      Else:
        endWidth = startWidth
      Smart/gT_ushort: fromFill (signed, -1 if < 0)
      If fromFill >= 0:
        obf_byte: fromFillAlpha
        If duration != 0:
          obf_byte: toFillAlpha
        Else:
          toFillAlpha = fromFillAlpha
      -> calls AddHeadbar
    Else:
      Remove headbar by type
```

### FORCED_MOVEMENT (flag 0x10)

```
obf_byte[6]: startDeltaX, startDeltaZ, endDeltaX, endDeltaZ, ?, ?
  (each in 0x200 tile-fine units, added to current entity position)
obf_ushort: startDelay (added to clientCycle)
obf_ushort: endDelay (added to clientCycle)
obf_ushort: unknown
  -> FUN_00b93c90 color lookup on the ushort

Stored at entity offsets:
  +0xDA0: startX (float)
  +0xDA4: startY (float, interpolated height)
  +0xDA8: startZ (float)
  +0xDAC: endTimeCycle
  +0xDB0: endX (float)
  +0xDB4: endY (float)
  +0xDB8: endZ (float)
  +0xDBC: startTimeCycle
  +0xDC0: endTimeCycle2
  +0xDC4: forcedMoveType
```

### FACE_ENTITY (flag 0x20)

```
obf_medium: packed value (3 bytes)
  Bits 0-15: entityIndex
  Bits 16: type indicator
    type 0x01: face specific entity with animation
    type 0x02: face entity type 2
    type -1 (0xFF) or 0x7F: stop facing
  -> calls FUN_0053cb80 to set face target
```

### SAY_TEXT (flag 0x40)

```
4x gSmart2or4s: text position/bounds values
obf_byte: flags
  -> vtable call at +0x1E0 or FUN_00ce8bd0 for overhead text rendering
```

### CHAT_TEXT (flag 0x8000)

```
gStringCP1252ToUTF8: chatMessage

If local player:
  Constructs chat message with player name and clan tags
  Calls jag::ChatHistory::AddChat with the assembled strings

For all players:
  vtable+0x158: SetChatText(entity, chatMessage, 0, 0)
```

### HEAD_ICONS (flag 0x20000)

```
gT_ushort: headIconCategoryId
Clears existing head icons for the entity

byte: iconCount
If iconCount > 0:
  For odd-indexed icons (pairs):
    obf_byte: iconType
    gT_ushort: spriteId
    -> Loads sprite and creates head icon entry
  For even-indexed icons:
    Same format, paired with previous
```

### OVERHEAD_CHAT (flag 0x40000)

```
gStringCP1252ToUTF8: chatText
obf_byte: chatFlags
  If bit 0 set:
    Constructs formatted chat message with player name/clan
    Calls jag::ChatHistory::AddChat
  vtable+0x158: SetChatText(entity, chatText, 0, 0)
```

### POSITION_COLOR (flag 0x400000)

```
obf_byte: level/plane
obf_byte: colorR (7 bits packed with level)
obf_byte: colorG
obf_byte: colorB (7 bits)
obf_ushort: startOffset (added to clientCycle at +0x500)
obf_ushort: endOffset (added to clientCycle at +0x500)

Packed color: (colorG & 0x7F) | ((colorR & 7) << 7) | ((level & 0x3F) << 10)
  -> FUN_00b94d60 unpacks to RGB floats

Stored at entity offsets:
  +0x1A0: startOffset value
  +0x194: green component * scale
  +0x198: blue component * scale
  +0x19C: brightness * scale
  +0x190: red component * scale
```

### SPOT_ANIMS (flag 0x1000000)

```
obf_byte: spotAnimCount
If count == 0:
  Clear all spot anims
  Return

If count > 0:
  For each spot anim (complex transform block):
    obf_short: spotAnimFlags (bit field)
    obf_short: slotId

    Conditionally reads based on flags:
      If flag 0x400: obf_uint startTransformId
      If flag 0x800: obf_uint endTransformId
      If flag 0x001: obf_uint translationX
      If flag 0x002: obf_uint translationY
      If flag 0x004: obf_uint translationZ
      If flag 0x008: obf_uint rotationX -> angle lookup
      If flag 0x010: obf_uint rotationY -> angle lookup
      If flag 0x020: obf_uint rotationZ -> angle lookup
      If flag 0x080: obf_uint scaleX -> float * scale
      If flag 0x100: obf_uint scaleY -> float * scale
      If flag 0x200: obf_uint scaleZ -> float * scale

    Constructs 4x4 transform matrix from rotation/scale/translation
    If flag 0x040: model-space transform (multiply with existing)
    Else: world-space transform

    Applies transform to spot anim's bone/attachment system
```

### SPOT_ANIM_REMOVAL (flag 0x2000000)

```
byte: count
For each removal:
  gSmart1or2: spotAnimId (-1 = clear all for entity)
  Searches entity's spot anim list (+0x201) and secondary list (+0x205)
  Removes matching entries by spotAnimId (checked at ptr+0x74)

After removals:
  byte: newSpotAnimCount
  For each new spot anim:
    obf_byte: typeId
    obf_ushort: spriteId/animId
    obf_uint: flags (0xFFFF check)
    obf_uint: duration
    obf_medium: packed data (transform flags, bone attachment, etc.)
    -> FUN_00200ce0: creates and attaches spot anim to entity
```

---

## Obfuscated Packet Readers

The extended info uses obfuscated byte readers instead of the standard Packet methods. Each reader consults a cipher stream (at packet+0x28) to determine the byte manipulation mode:

| Function | Address | Type | Modes |
|----------|---------|------|-------|
| `gScrambledByte` | `0x00313d70` | signed byte | 0=raw, 1=val-128, 2=-val, 3=-(val+128) |
| `gScrambledUbyte` | `0x00313cc0` | unsigned byte | 0=raw, 1=val-128, 2=-val, 3=128-val |
| `gScrambledShort` | `0x00313b20` | signed short | 0=BE, 1=LE, 2=BE+128lo, 3=LE+128lo |
| `gScrambledUshort` | `0x00313bf0` | unsigned short | 0=BE, 1=LE, 2=BE+128lo, 3=LE+128lo |
| `gScrambledMedium` | `0x00313a20` | 24-bit int | 0=BE(210), 1=(120), 2=(201), 3=(012) |
| `gScrambledUint` | `0x00313910` | unsigned int | 0=BE, 1=LE(3210), 2=mid(1032), 3=mid(2301) |

The cipher stream is initialized from the packet's `serverProt` definition and determines the obfuscation mode for each read in sequence.

---

## Additional ServerProt Handlers

These handlers are registered separately from PLAYER_INFO but relate to player updates:

### HandleAbsolutePlayerPositions

**Address**: `0x00222080`
**Handler**: `jag::PlayerList::HandleAbsolutePlayerPositions`

```
byte: playerIndex
byte: additionalData
-> Looks up player by adjusted index
-> Creates ExtendedInfoState, queues for deferred processing
```

### HandlePlayerChat

**Address**: `0x00222340`
**Handler**: `jag::PlayerList::HandlePlayerChat`

```
byte[2]: playerIndex (big-endian ushort)
byte[4]: chatMetadata (color, effect, rights, etc.)
gStringCP1252ToUTF8: chatMessage
-> Looks up PlayerEntity, sets chat text and display timer
```

### HandleMapFlagSet

**Address**: `0x002224a0`
**Handler**: `jag::PlayerList::HandleMapFlagSet`

```
byte: packed (bits 5-7 = group, bits 0-4 = type)
Clears existing entries for the group
byte: playerIndex
Based on type:
  Type 1/10: gT_ushort x2, 4 raw bytes, coords
  Type 2-6: directional offset encoding
  Other: basic position entry
```

---

## Functions Reference

| Address | Name | Signature |
|---------|------|-----------|
| `0x002228f0` | `jag::PlayerList::ProcessPlayerInfo` | `void*(PlayerList*, Packet*)` |
| `0x002082e0` | `jag::PlayerList::GetHighResolutionPlayerPosition` | `void(long, PacketBit*, int)` |
| `0x00206e90` | `jag::PlayerList::GetLowResolutionPlayerPosition` | `uint8(long, PacketBit*, int)` |
| `0x00bc4a80` | `jag::PlayerList::ReadStationary` | `int(PacketBit*)` |
| `0x00202180` | `jag::PlayerEntity::ProcessExtendedInfo` | `void(PlayerEntity*, Packet*, shared_ptr<ExtendedInfoState>&)` |
| `0x001f6170` | `jag::PlayerEntity::SetAppearanceAsPlayer` | `void(PlayerEntity*, Packet*, uint, uint)` |
| `0x001f7450` | `jag::PathingEntity::QueueExtendedInfoPacket` | `void(PathingEntity*, Packet*, uint)` |
| `0x0053a060` | `jag::PathingEntity::SetForcedMovement` | `void(uint, PathingEntity*, ...)` |
| `0x00222080` | `jag::PlayerList::HandleAbsolutePlayerPositions` | `void*(PlayerList*, Packet*)` |
| `0x00222340` | `jag::PlayerList::HandlePlayerChat` | `void*(PlayerList*, Packet*)` |
| `0x002224a0` | `jag::PlayerList::HandleMapFlagSet` | `void*(PlayerList*, Packet*)` |
| `0x00313d70` | `jag::Packet::gScrambledByte` | `long(Packet*)` |
| `0x00313cc0` | `jag::Packet::gScrambledUbyte` | `uint(Packet*)` |
| `0x00313b20` | `jag::Packet::gScrambledShort` | `long(Packet*)` |
| `0x00313bf0` | `jag::Packet::gScrambledUshort` | `ushort(Packet*)` |
| `0x00313a20` | `jag::Packet::gScrambledMedium` | `int(Packet*)` |
| `0x00313910` | `jag::Packet::gScrambledUint` | `ulong(Packet*)` |

## Ghidra Documentation Status

All 17 functions in the reference table above have been:
- **Renamed** with full namespace paths in Ghidra
- **Prototyped** with `set_function_prototype` (correct return types, parameter types, and names)
- **Commented** with `set_decompiler_comment` summarizing their purpose

### Data Types Created

- **PlayerExtendedInfoFlags** (enum, 4 bytes, `/jag`): All 23 extended info flag bits with symbolic names

## Coordinate System Notes

- **Tile-fine units**: 1 tile = 0x200 (512) fine units
- **Chunk units**: Used in low-res positions, 1 chunk = 0x40 (64) tiles = 0x8000 fine units
- **Level/Plane**: 0-3, masked with `& 3`
- **Maximum player index**: 2047 (0x7FF), with index 1 being the local player
- **High-res list**: stored at PlayerList+0x1D58/0x1D60 (begin/end pointers, int array)
- **Low-res list**: stored at PlayerList+0x3D80/0x3D88 (begin/end pointers, int array)
- **Extended info list**: stored at PlayerList+0x7DF0/0x7DF8 (begin/end pointers, int array)
