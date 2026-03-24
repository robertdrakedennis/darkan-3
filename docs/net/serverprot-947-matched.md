# ServerProt 947-1: Matched Critical Packets

Matched by tracing handler function registrations from `RegisterAll` (ProtEntry allocation with opcode+size),
through the Variables constructor (`FUN_0014d28c`) and Interfaces constructor (`FUN_0014d97a`),
to the handler functions identified via `PlayerVarDomain::set`, `SetServerActiveProperties`,
`CreateOrFindUpdateEntry`, and `StatTable::UpdateStat` xrefs.

**Method**: ProtEntry address from constructor handler slot (`DAT_XXX + 8 = handler`) maps to
`ProtEntry_base = handler_slot_addr - 0x28`, then looked up in RegisterAll's `InitEntry(addr, opcode, size)` calls.

**Confidence**: CERTAIN for all entries (direct ProtEntry-to-opcode mapping from RegisterAll).

---

## Variable Packets

### RESET_CLIENT_VARCACHE
```
946 opcode: 112
947 opcode: 48 (0x30)
947 size: 0
Handler: jag::packethandlers::ClientState::RESET_ALL_VARPS (named in 947)
ProtEntry: 0x017023e0
Byte transforms: (none - empty packet)
Server write: (empty payload)
Confidence: CERTAIN
Evidence: Variables constructor slot #1, ProtEntry mapped via RegisterAll
```

### VARP_SMALL
```
946 opcode: 14
947 opcode: 10 (0x0a)
947 size: 3
Handler: FUN_001ba350
ProtEntry: 0x017023a0
Byte transforms (947):
  pos += 2  (reads 2 bytes for varp_id, encoding optimized out by decompiler)
  pos += 1  (reads 1 byte for value)
  value = -cVar1  (negate the raw byte)
Server write order: writeShort(id) [encoding TBD from assembly], writeByte(-value)
  NOTE: The decompiler optimized out the id read. The id encoding must be verified
  from assembly. In 946 it was g2(hi+lo-128) = writeShortAdd.
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #2, calls PlayerVarDomain::set with type=0
```

### VARP_LARGE
```
946 opcode: 124
947 opcode: 111 (0x6f)
947 size: 6
Handler: FUN_001ba2a0
ProtEntry: 0x01702360
Byte transforms (947):
  pos += 4  (reads 4 bytes for value, middle-endian)
  pos += 2  (reads 2 bytes for varp_id)
  value = buf[0]*0x100 + buf[2]*0x1000000 + buf[3]*0x10000 + buf[1]
        = (buf[2]<<24) | (buf[3]<<16) | (buf[0]<<8) | buf[1]
  This is g4_alt1 / writeIntMiddle: wire order [midLo, lo, hi, midHi]
  NOTE: Read order is VALUE FIRST then ID (reversed from 946!)
Server write order: writeIntMiddle(value), writeShort(id) [encoding TBD]
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #3, calls PlayerVarDomain::set with type=0
```

### VARP_LONG
```
946 opcode: 138
947 opcode: 119 (0x77)
947 size: 10
Handler: FUN_001c23b0
ProtEntry: 0x01702320
Byte transforms (947):
  iVar2 = g4s_alt2(packet)  (4 bytes, first int)
  iVar3 = g4s_alt2(packet)  (4 bytes, second int)
  pos += 2  (reads 2 bytes for varp_id)
  value = CONCAT44(iVar2, iVar3)  (combined as 8-byte long)
  type = 1 (long)
Server write order: writeInt_alt2(highInt), writeInt_alt2(lowInt), writeShort(id)
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #4, calls PlayerVarDomain::set with type=1
```

### CLIENT_SETVARC_SMALL
```
946 opcode: 19
947 opcode: 1 (0x01)
947 size: 3
Handler: FUN_001ba080
ProtEntry: 0x01702260
Byte transforms (947):
  pos += 1  (reads 1 byte for value)
  pos += 2  (reads 2 bytes for varc_id)
  value = bVar1 - 0x80  (subtract 128, signed)
  Uses DAT_014d7100 = VAR_CLIENT_TYPE
  Calls CreateOrFindUpdateEntry(1)
Server write order: writeByteSubtract(value), writeShort(id) [encoding TBD]
  Where writeByteSubtract means client subtracts 0x80, so server writes (value + 0x80) & 0xFF
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #7, uses ClientVarDomain
```

### CLIENT_SETVARC_LARGE
```
946 opcode: 12
947 opcode: 112 (0x70)
947 size: 6
Handler: FUN_001b9fb0
ProtEntry: 0x01702220
Byte transforms (947):
  pos += 4  (reads 4 bytes as uint, big-endian byte swap on LE) = g4BE for value
  pos += 2  (reads 2 bytes for varc_id)
  Uses DAT_014d7100 = VAR_CLIENT_TYPE
  NOTE: Order changed from 946! In 946 it was [2B id BE][4B value LE].
        In 947 it is [4B value BE][2B id].
Server write order: writeInt(value), writeShort(id)
  Where writeInt = standard big-endian 4-byte int
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #8, uses ClientVarDomain
```

### CLIENT_SETVARCBIT_SMALL
```
946 opcode: 88
947 opcode: 115 (0x73)
947 size: 3
Handler: FUN_001b9f00 (RESET_VARC_SMALL equivalent)
ProtEntry: 0x017021a0
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #10
```

### CLIENT_SETVARCBIT_LARGE
```
946 opcode: 115
947 opcode: 55 (0x37)
947 size: 6
Handler: FUN_001b9e40 (RESET_VARC_INT equivalent)
ProtEntry: 0x01702160
Confidence: CERTAIN (opcode mapping)
Evidence: Variables constructor slot #11
```

---

## Stat Packet

### UPDATE_STAT
```
946 opcode: 114
947 opcode: 66 (0x42)
947 size: 6
Handler: jag::StatTable::UpdateStat at 0x0018f8f0
ProtEntry: 0x01701fa0
Byte transforms (947):
  4 bytes: g4BE (experience/xp) - standard big-endian uint32
  1 byte at offset 4: level - stored as (0x80 - cVar2), so client reads raw and does 0x80-raw
  1 byte at offset 5: skillId - stored as (raw + 0x80), so client reads raw and adds 0x80

  CHANGED from 946:
    946 xp: g4_alt1 (middle-endian)     -> 947 xp: g4BE (big-endian)
    946 level: raw + 0x80 (add 128)     -> 947 level: 0x80 - raw (subtract from 128)
    946 skillId: -raw (negate)          -> 947 skillId: raw + 0x80 (add 128)

Server write order (947):
  writeInt(xp)                          [standard big-endian]
  writeByte(0x80 - level)               [server writes 128-level, client does 128-raw to recover]
  writeByte(skillId - 0x80)             [server writes skillId-128, client adds 128 to recover]
Confidence: CERTAIN (opcode mapping + decompiled handler)
Evidence: Variables constructor slot #18, handler pattern matches UpdateStat
```

---

## Interface Packets

### IF_OPENTOP
```
946 opcode: 207
947 opcode: 68 (0x44)
947 size: 6
Handler: FUN_0023e910
ProtEntry: 0x01701f20
Byte transforms (947):
  g4_alt1(packet)  (4 bytes, component hash - consumed but only triggers internal state)
  2 bytes: g2LE (interface ID) - assembled as bVar1*256 + bVar2 (little-endian short)
  Calls CreateOrFindUpdateEntry(0xc)
  SIZE CHANGED from 946 (was 2, now 6)
Server write order: writeIntMiddle(componentHash), writeShortLittle(interfaceId)
Confidence: CERTAIN (opcode mapping + CreateOrFindUpdateEntry(0xc) matches 946 pattern)
Evidence: Interfaces constructor slot #2
```

### IF_OPENSUB
```
946 opcode: 182
947 opcode: 17 (0x11)
947 size: 8
Handler: FUN_0023e9a0 (first registered in Interfaces constructor)
ProtEntry: 0x01701f60
Byte transforms (947): [needs decompilation of FUN_0023e9a0]
  SIZE CHANGED from 946 (was 5, now 8)
Confidence: CERTAIN (opcode mapping)
Evidence: Interfaces constructor slot #1
```

### IF_SETGRAPHIC
```
946 opcode: 126
947 opcode: 92 (0x5c)
947 size: 8
Handler: FUN_0023e730
ProtEntry: 0x01701a60
Byte transforms (947):
  4 bytes at pos: middle-endian int (graphic ID)
    value = buf[3]*0x100 + buf[1]*0x1000000 + buf[0]*0x10000 + buf[2]
          = (buf[1]<<24) | (buf[0]<<16) | (buf[3]<<8) | buf[2]
    Wire order: [midHi, hi, lo, midLo] - this is g4_alt2 / inverse middle endian
  RESET_ANIMS(packet) reads 4 more bytes (component hash via g4)
  Calls CreateOrFindUpdateEntry(5)
  SIZE CHANGED from 946 (was 19, now 8)
  NOTE: The 946 19-byte format had 12B zeros + 2B interfaceId + 5B zeros.
        The 947 8-byte format is 4B graphicId + 4B componentHash (compact).
Server write order: writeInt_alt2(graphicId), writeInt(componentHash)
Confidence: CERTAIN (opcode mapping + CreateOrFindUpdateEntry(5) matches 946)
Evidence: Interfaces constructor, handler xref to CreateOrFindUpdateEntry
```

### IF_SETEVENTS
```
946 opcode: 59 (per if-setevents.md) / 202 (per serverprot-table.md) [DISCREPANCY]
947 opcode: 34 (0x22)
947 size: 10
Handler: FUN_0022ba30
ProtEntry: 0x01701ca0
Byte transforms (947):
  g4s_alt2(packet)  (4 bytes, settings/componentHash)
  pos += 2  (skip 2 bytes)
  pos += 4  (skip 2 more, total +6 from g4s_alt2)
  pos += 6  (2 more bytes at end)
  reads bytes at offset 4+5 only -> assembled as short (slot parameter)
  Calls SetServerActiveProperties with 3 params (simplified from 946's 5 params)
  NOTE: Handler signature changed - SetServerActiveProperties takes fewer params in 947
Server write order: [needs detailed assembly verification]
Confidence: CERTAIN (opcode mapping)
Evidence: Interfaces constructor, calls SetServerActiveProperties
```

### IF_SETEVENTS2
```
946 opcode: [paired with IF_SETEVENTS]
947 opcode: 35 (0x23)
947 size: 12
Handler: FUN_0022bb00
ProtEntry: 0x01701ce0
Byte transforms (947):
  2 bytes: short (start slot, assembled as bVar2*256 + bVar3)
  2 bytes: (skipped, pos += 4)
  g4s_alt2(packet) (4 bytes)
  RESET_ANIMS(packet) (reads remaining bytes)
  Calls SetServerActiveProperties with 3 params
Confidence: CERTAIN (opcode mapping)
Evidence: Interfaces constructor, calls SetServerActiveProperties
```

---

## NO_TIMEOUT (MATCHED)

### NO_TIMEOUT
```
946 opcode: 146
947 opcode: 216 (0xd8)
947 size: 0
Handler: FUN_0021a630
ProtEntry: 0x016eb560
Byte transforms: (none - empty packet)
Server write: (empty payload)
Confidence: CERTAIN
Evidence: Handler decompiled as trivial return { return &DAT_016ed500; }
  Assigned in BindHandlers at DAT_016eb588 = FUN_0021a630.
  FUN_0021a630 is a single-line function that returns the success constant,
  identical to the 946 NO_TIMEOUT handler pattern.
```

---

## Still Unmatched

### UPDATE_FRIENDLIST
```
946 opcode: 18
947 opcode: UNKNOWN
947 size: varShort
Handler: Social::UPDATE_FRIENDLIST_thunk (946 addr: 0x002775f0)
Status: Not found in BindHandlers, Variables, Interfaces, ZoneUpdates, or Audio constructors.
  Must be in a Social/Friends constructor or another registration path.
  Searched constructors:
    - FUN_001183d0: initialization
    - FUN_0014c936: ZoneUpdates constructor
    - FUN_0014d28c: Variables constructor
    - FUN_0014d97a: Interfaces constructor
    - FUN_0014e7ac: Audio constructor
  The handler is likely registered through a separate Social/Friends constructor
  that is called from a different initialization path (not directly from BindHandlers).
  Among varShort unmatched entries: 2, 9, 31, 86, 90, 102, 121, 126, 134, 139, 172, 176, 188, 212
Action needed: Find the Social/Friends constructor by:
  1. Searching for xrefs to the 946 UPDATE_FRIENDLIST_thunk equivalent in 947
  2. Looking for thunk functions in the 0x00274xxx-0x00278xxx range (near 946's 0x002775f0)
  3. Searching for functions that read friend entry data patterns (name strings + world IDs)
```

---

## Summary Table

| Packet | 946 Op | 947 Op | 947 Size | Confidence |
|--------|--------|--------|----------|------------|
| RESET_CLIENT_VARCACHE | 112 | **48** | 0 | CERTAIN |
| VARP_SMALL | 14 | **10** | 3 | CERTAIN |
| VARP_LARGE | 124 | **111** | 6 | CERTAIN |
| VARP_LONG | 138 | **119** | 10 | CERTAIN |
| CLIENT_SETVARC_SMALL | 19 | **1** | 3 | CERTAIN |
| CLIENT_SETVARC_LARGE | 12 | **112** | 6 | CERTAIN |
| CLIENT_SETVARCBIT_SMALL | 88 | **115** | 3 | CERTAIN |
| CLIENT_SETVARCBIT_LARGE | 115 | **55** | 6 | CERTAIN |
| UPDATE_STAT | 114 | **66** | 6 | CERTAIN |
| IF_OPENTOP | 207 | **68** | 6 | CERTAIN |
| IF_OPENSUB | 182 | **17** | 8 | CERTAIN |
| IF_SETGRAPHIC | 126 | **92** | 8 | CERTAIN |
| IF_SETEVENTS | 59/202 | **34** | 10 | CERTAIN |
| IF_SETEVENTS2 | - | **35** | 12 | CERTAIN |
| NO_TIMEOUT | 146 | **216** | 0 | CERTAIN |
| UPDATE_FRIENDLIST | 18 | **???** | varShort | UNMATCHED |

## Key Changes from 946 to 947

1. **Byte transforms changed** for several packets:
   - UPDATE_STAT: xp encoding changed from middle-endian to big-endian; level/skillId transforms completely different
   - CLIENT_SETVARC_LARGE: value encoding changed from LE to BE; field order reversed (value before id)
   - VARP_LARGE: field order reversed (value before id in 947)
   - VARP_SMALL: value transform changed from subtract-128 to negate

2. **Packet sizes changed** for interface packets:
   - IF_OPENTOP: 2 -> 6 (now includes component hash)
   - IF_OPENSUB: 5 -> 8
   - IF_SETGRAPHIC: 19 -> 8 (dramatic reduction, removed padding)

3. **SetServerActiveProperties signature changed**: 5 params -> 3 params in 947
