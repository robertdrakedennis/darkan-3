# Interface ServerProt Field Layouts (Build 946-5)

**Binary:** rs2client rev 946 (STRIPPED)
**Verified:** 2026-03-15 via Ghidra MCP decompilation + disassembly

## Packet Read Method Reference (Byte-Level)

All read methods operate on a `jag::Packet` struct where:
- `packet + 0x10` = pointer to data buffer
- `packet + 0x18` = current read position (advanced after each read)

Endianness detection at `DAT_0115ee00`: equals `0x03020100` on little-endian platforms.

### `gT<uint>()` — Standard Big-Endian i32

**Ghidra address:** `0x001d6ee0`

Reads 4 bytes as native uint32, byte-swaps on little-endian.
Wire bytes `[A][B][C][D]` produce value `(A<<24)|(B<<16)|(C<<8)|D`.

Server writes: standard big-endian int32.

### `g4_alt1()` — Middle-Endian 1 (BADC order)

**Ghidra address:** `0x001c2270`

Wire bytes `[A][B][C][D]` produce:
```
value = A*0x100 + C*0x1000000 + D*0x10000 + B
      = (C << 24) | (D << 16) | (A << 8) | B
```

**To write value V on the server:**
```
byte[0] = (V >> 8) & 0xFF    // bits 8-15
byte[1] = V & 0xFF           // bits 0-7
byte[2] = (V >> 24) & 0xFF   // bits 24-31
byte[3] = (V >> 16) & 0xFF   // bits 16-23
```

### `g4s_alt2()` — Little-Endian i32 (DCBA order)

**Ghidra address:** `0x001c62a0`

Wire bytes `[A][B][C][D]` produce:
```
value = B*0x100 + D*0x1000000 + C*0x10000 + A
      = (D << 24) | (C << 16) | (B << 8) | A
```

This is standard **little-endian** int32.

**To write value V on the server:**
```
byte[0] = V & 0xFF           // bits 0-7
byte[1] = (V >> 8) & 0xFF    // bits 8-15
byte[2] = (V >> 16) & 0xFF   // bits 16-23
byte[3] = (V >> 24) & 0xFF   // bits 24-31
```

### `g4s_alt3()` — Middle-Endian 3 (CDAB order)

**Ghidra address:** `0x001e4190` (named `ClientState::RESET_ANIMS` in Ghidra)

Wire bytes `[A][B][C][D]` produce:
```
value = D*0x100 + B*0x1000000 + A*0x10000 + C
      = (B << 24) | (A << 16) | (D << 8) | C
```

**To write value V on the server:**
```
byte[0] = (V >> 16) & 0xFF   // bits 16-23
byte[1] = (V >> 24) & 0xFF   // bits 24-31
byte[2] = V & 0xFF           // bits 0-7
byte[3] = (V >> 8) & 0xFF    // bits 8-15
```

### `gT<ushort>()` / `gT_ushort_()` — Standard Big-Endian u16

**Ghidra address:** `0x001c1d50`

Standard big-endian unsigned short.
```
value = (byte[0] << 8) | byte[1]
```

### Inline Short with XOR 0x80 Transform

Several handlers read 2 bytes in little-endian order with an XOR 0x80 transform on the low byte:
```
// Wire: [lowByte] [highByte]  (little-endian order)
// Client reads: value = (highByte << 8) | ((lowByte + 0x80) & 0xFF)
```

Since `+0x80` and `-0x80` are equivalent mod 256, this is the same as XOR 0x80 on the low byte:
```
value = (highByte << 8) | (lowByte ^ 0x80)
```

**Server writes (for value V):**
```
byte[0] = (V & 0xFF) ^ 0x80     // low byte with XOR 0x80
byte[1] = (V >> 8) & 0xFF       // high byte unchanged
```

### `gStringCP1252ToUTF8()`

**Ghidra address:** `0x00cd2900`

Reads null-terminated CP1252 string, converts to UTF-8 internally. Position advances past the null terminator.

---

## Architecture Note: Dual Handler Dispatch

The Interface category in build 946-5 has TWO sets of handler registrations:

1. **ServerProt ProtEntry handlers** (addresses in `0x0027xxxx` range): These are wrapper/thunk functions registered in the `g_serverProtVector` table. They perform InterfaceComponent dirty-flag updates (writing to IC struct fields like `+0x150`..`+0x157`). They read only 3 bytes from the packet: a 2-byte big-endian ushort (component lookup key) + 1 byte (value/flag).

2. **Interfaces constructor handlers** (addresses in `0x0022xxxx`/`0x0023xxxx` range): These are the "real" decode handlers registered via `FUN_0014df8c`. They read ALL fields from the packet and call `InterfaceManager::CreateOrFindUpdateEntry`, `SetUpdateSlotValue`, etc.

Both handler sets are called for the same packet. The thunk handler reads from the END of the packet (the last 3 bytes are the "active" portion), while the real handler reads from the BEGINNING.

For server implementation: write the "real handler" fields first, then append the 3-byte "active" suffix. The total packet size = real handler bytes + 3 bytes active suffix (for most Interface packets).

---

## Handler Field Layouts

### Opcode 207 / 0xCF: IF_OPENTOP (size 2)

**Real handler:** `IF_OPENTOP` at `0x00233c90`
**Update type:** 0xC
**No active suffix** (only 2 bytes total, no thunk processing)

| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 2 | gT\<ushort\>() | u16 | interfaceId | Interface definition ID to open as top-level root |

**Server write (2 bytes):**
```kotlin
pT<ushort>(interfaceId)   // big-endian u16
```

---

### Opcode 59 / 0x3B: IF_SETTEXT (size 12 = var portion + 4 + active)

**Real handler:** `IF_SETTEXT` at `0x00224ad0`
**Update type:** via `SetComponentText` (type=1)
**Has active suffix** at `0x0027d170` (writes IC+0x154/0x155)

The real handler reads:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | N+1 | gStringCP1252ToUTF8() | string | text | Null-terminated CP1252 text string |
| N+1 | 4 | g4s_alt2() | i32 | componentHash | `(interfaceId << 16) \| componentId` — little-endian |

Note: The serverprot table lists IF_SETTEXT as size=12. But the real handler reads a null-terminated string (variable length) + 4-byte componentHash. The "12" likely means the packet is var_short-framed (the 12 is a typo or refers to a different revision). The Interfaces constructor handler reads all fields from position 0: string first, then g4s_alt2 componentHash. The active suffix (3 bytes) at the thunk reads from the END of the packet (last 3 bytes). The var_short frame length tells both handlers where the data boundaries are.

**Server write:**
```kotlin
pStringCP1252(text)       // null-terminated string
p4s_alt2(componentHash)   // little-endian i32
```

**Active suffix (3 bytes) from thunk at `0x0027d170`:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| +0 | 1 | byte | flag | Value flag (compared against -1/0xFF for dirty) |
| +1 | 2 | inline BE ushort | componentKey | Component lookup key for active processing |

The active suffix writes to IC+0x154 (flag == 0xFF) and IC+0x155 (dirty marker).

---

### Opcode 4 / 0x04: IF_SETHIDE (size 10 = 5 + 2 + 3 active)

**Real handler:** `IF_SETHIDE` at `0x00233bf0`
**Update type:** 7 (hidden flag)
**Has active suffix** at `0x0027e8a0` (writes IC+0x150/0x151)

The real handler reads:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 4 | gT\<uint\>() | u32 | componentHash | `(interfaceId << 16) \| componentId` — standard big-endian |
| 4 | 1 | byte | u8 | hidden | Hidden flag: handler computes `(0x80 - byte) == 1`, so byte `0x7F` = hidden=true |

**Verified from disassembly at `0x00233bf0`:**
- `MOV R12D,0xffffff80` then `SUB R12B,byte ptr [RCX + RAX*0x1 + 0x4]` → `R12B = 0x80 - byte[4]`
- `CMP R12B,0x1` / `SETZ` → hidden = (R12B == 1) = (byte == 0x7F)

**Server write (5 bytes for real handler):**
```kotlin
pT<uint>(componentHash)   // big-endian u32
p1(if (hidden) 0x7F else 0x80)   // 1 byte: 0x7F=hidden, 0x80=visible
```

**Active suffix (3 bytes) from thunk at `0x0027e8a0`:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 5 | 1 | byte | activeFlag | Low nibble + 0x80 transform; nibble==1 means set, nibble==0xF means clear |
| 6 | 1 | byte | keyLow | Component key low byte |
| 7 | 1 | byte | keyHigh | Component key high byte, assembled as `keyHigh * 0x100 + keyLow` for lookup |

Note: The thunk reads byte[0], then assembles bytes[1..2] as `byte2 * 0x100 + (byte1 + 0x80)` for the component key. The remaining 2 bytes (offset 8-9) account for the size=10 total.

---

### Opcode 38 / 0x26: IF_SETPOSITION (size 23 = 10 + 10 + 3 active)

**Real handler:** `IF_SETPOSITION` at `0x00233070`
**Update type:** 8 (position)
**Has active suffix** at `0x0027db70` (writes IC+0x152/0x153)

The real handler reads:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 4 | g4_alt1() | i32 | componentHash | `(interfaceId << 16) \| componentId` — middle-endian BADC |
| 4 | 2 | inline LE ushort XOR 0x80 | u16 | posY | Y position: `(byte[5] << 8) \| ((byte[4] + 0x80) & 0xFF)` — stored in slot 0x40 |
| 6 | 2 | inline LE ushort XOR 0x80 | u16 | posX | X position: `(byte[7] << 8) \| ((byte[6] + 0x80) & 0xFF)` — stored in slot 0x20 |
| 8 | 2 | inline LE ushort | u16 | alignment | Alignment/position type: `(byte[9] << 8) \| byte[8]` — stored in slot 0x60 |

**Verified from disassembly at `0x00233070`:**
- Bytes 4-5 → posY: `SHL R12D,0x8` (byte[5]<<8) + `MOVZX R8D,SIL` (byte[4] after `ADD ESI,-0x80`)
- Bytes 6-7 → posX: `SHL R13D,0x8` (byte[7]<<8) + `MOVZX R11D,R10B` (byte[6] after `ADD R10D,-0x80`)
- Bytes 8-9 → align: `SHL EBX,0x8` (byte[9]<<8) + `ECX` (byte[8], no transform)

**Server write (10 bytes for real handler):**
```kotlin
p4_alt1(componentHash)
// posY — little-endian with XOR 0x80 on low byte:
p1((posY and 0xFF) xor 0x80)
p1((posY shr 8) and 0xFF)
// posX — little-endian with XOR 0x80 on low byte:
p1((posX and 0xFF) xor 0x80)
p1((posX shr 8) and 0xFF)
// alignment — little-endian, no transform:
p1(alignment and 0xFF)
p1((alignment shr 8) and 0xFF)
```

Note: Total size is 23. Real handler reads 10 bytes. Active suffix reads 3 bytes. The remaining 10 bytes are consumed by additional processing in the handler chain (intermediate wrapper reads).

---

### Opcode 126 / 0x7E: IF_SETGRAPHIC (size 19 = 8 + 8 + 3 active)

**Real handler:** `IF_SETGRAPHIC` at `0x00233a80`
**Update type:** 5 (graphic/sprite)
**Has active suffix** via thunk at `0x0027c8b0`

The real handler reads:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 4 | g4_alt1() | i32 | componentHash | `(interfaceId << 16) \| componentId` — middle-endian BADC |
| 4 | 4 | inline i32 | i32 | graphicId | Graphic/sprite ID assembled as: `byte[5]*0x100 + byte[7]*0x1000000 + byte[6]*0x10000 + byte[4]` |

The graphicId assembly pattern `B*0x100 + D*0x1000000 + C*0x10000 + A` is the same as `g4s_alt2()` (little-endian). So graphicId is read as **little-endian i32**.

**Server write (8 bytes for real handler):**
```kotlin
p4_alt1(componentHash)    // middle-endian BADC
p4s_alt2(graphicId)       // little-endian i32
```

---

### Opcode 180 / 0xB4: IF_CLOSESUB (size 5)

**Serverprot-table handler:** `0x0027c680` (DBFilter dispatch thunk)
**Interfaces constructor handler:** `IF_CLOSESUB` at `0x00225bb0` (reads 19 bytes -- for a DIFFERENT opcode/size)
**Action:** Closes a sub-interface

The Ghidra-named `IF_CLOSESUB` at `0x00225bb0` reads 19 bytes (g4_alt1 + g4s_alt2 + g4s_alt2 + ushort + g4s_alt3 + byte), which does NOT match the 5-byte size of opcode 180. That function is registered for a different opcode with a larger packet size (possibly from an older revision or a different opcode mapping).

For opcode 180 (size 5), the handler at `0x0027c680` dispatches through the DBFilter tree:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 1 | byte | u8 | typeFlag | First byte (routing flag) |
| 1 | 1 | byte | u8 | routeControl | If 0, triggers URL string scan; nonzero skips |
| 2-4 | 1-3 | smart ushort | u16 | interfaceId | Smart-encoded interface ID (see below) |

Smart ushort encoding:
- If `byte < 0x80`: value = byte (1 byte, range 0-127), advance 1
- If `byte >= 0x80`: value = `gT<ushort>()` result + 0x8000 (2 bytes BE), advance 2

For a fixed 5-byte packet: byte[1] is nonzero (skipping URL), and interfaceId uses the 2-byte smart ushort form. Total: 1 + 1 + 1(skip flag) + 2(smart) = 5.

**Server write (5 bytes):**
```kotlin
p1(typeFlag)
p1(0x01)                  // nonzero to skip URL string
pSmart_ushort(interfaceId)  // smart-encoded (1 or 2 bytes)
// pad to 5 bytes total if smart used only 1 byte
```

**Note:** The exact routing through the DBFilter depends on the `typeFlag` and `interfaceId`. For a basic close, the type flag and routing parameters need further RE analysis of the DBFilter tree dispatch.

---

### Opcode 182 / 0xB6: IF_OPENSUB (size 5)

**Serverprot-table handler:** `0x0027c5a0` (DBFilter dispatch thunk, same pattern as IF_CLOSESUB)

Same dispatch structure as IF_CLOSESUB:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 1 | byte | u8 | typeFlag | Routing flag |
| 1 | 1 | byte | u8 | routeControl | If 0, triggers URL string scan; nonzero skips |
| 2-4 | 1-3 | smart ushort | u16 | interfaceId | Smart-encoded interface/component ID |

After the smart ushort, the handler dispatches through the DBFilter tree which calls the registered Interfaces constructor handler to perform the actual open operation.

The Interfaces constructor's `IF_OPENSUB` at `0x00233d30` reads from the packet AFTER the thunk consumed its bytes:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 4 | g4s_alt3() | i32 | componentHash | Target component — middle-endian CDAB |
| 4 | 2 | gT\<ushort\>() | u16 | subInterfaceId | Interface ID to open as sub — big-endian |
| 6 | 2 | inline BE ushort | u16 | overlayFlags | Overlay/mode flags |

---

### Opcode 202 / 0xCA: IF_SETEVENTS (size 9)

**Serverprot-table handler:** `0x0027cb50` (falls inside `IF_MOVESUB_thunk` function body)

The handler at `0x0027cb50` is actually mid-function in the IF_MOVESUB_thunk. This address corresponds to the "active" suffix processing point for the IF_SETEVENTS opcode. The actual IF_SETEVENTS packet format is handled by the Interfaces constructor handler for the IF_SETSCROLLSIZE operation (since IF_SETEVENTS = IF_SETSCROLLSIZE in the 946-5 opcode rotation).

Based on the `IF_SETSCROLLSIZE` handler at `0x00233290`:
| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | 2 | inline LE ushort | u16 | sizeA | First value: `(byte[1] << 8) \| byte[0]` |
| 2 | 2 | inline LE ushort | u16 | sizeB | Second value: `(byte[3] << 8) \| byte[2]` |
| 4 | 1 | byte | u8 | scrollIndex | Scrollbar index, transformed: `(0x80 - byte) & 0xFF` |
| 5 | 4 | g4s_alt2() | i32 | componentHash | Target component — little-endian i32 |

Total: 2+2+1+4 = 9 bytes. Matches the size=9 in the serverprot table.

**Server write (9 bytes):**
```kotlin
// sizeA little-endian:
p1(sizeA and 0xFF)
p1((sizeA shr 8) and 0xFF)
// sizeB little-endian:
p1(sizeB and 0xFF)
p1((sizeB shr 8) and 0xFF)
// scrollIndex with 0x80 transform:
p1((0x80 - scrollIndex) and 0xFF)
// componentHash little-endian:
p4s_alt2(componentHash)
```

**CAVEAT:** The name "IF_SETEVENTS" for opcode 202 may be incorrect. The handler logic matches IF_SETSCROLLSIZE behavior (update type 0x12, storing scrollbar index + two size values). This may be a naming error in the serverprot table or an opcode rotation artifact.

---

### Opcode 36 / 0x24: CHANGE_LOBBY / IF_SETPLAYERMODEL_OTHER (size var_short)

**Handler:** `FUN_002680c0` at `0x002680c0` (called from the handler chain at `0x00268960`)

This is a complex variable-length packet. The handler reads:

| Offset | Size | Read Method | Type | Field | Description |
|--------|------|-------------|------|-------|-------------|
| 0 | N+1 | gStringCP1252ToUTF8() | string | formatString | Format string of 'i'/'s'/'l' chars defining field types |
| N+1 | var | per-char reads | mixed | fields[] | One field per char in formatString, read in REVERSE order |

For each character in the format string (iterated in reverse):
- `'i'` (int): reads 4 bytes via `gT<uint>()` (big-endian i32)
- `'s'` (string): reads null-terminated CP1252 string via `gStringCP1252ToUTF8()`
- `'l'` (long): reads 8 bytes via big-endian i64 (byte-swapped on LE)

After all fields are read, the handler calls:
```c
iVar5 = FUN_001c1d80(param_2);  // reads a final value from packet
ScriptRunner::ExecuteHookInner(scriptRunner, params, 500000);
```

`FUN_001c1d80` reads a "smart int" (1 or 2 bytes based on high bit, signed, with 0x8000 offset).

**Server write:**
```kotlin
pStringCP1252(formatString)   // e.g., "iiiisssssi"
// Fields in REVERSE order of the format string:
for (i in formatString.lastIndex downTo 0) {
    when (formatString[i]) {
        'i' -> pT<uint>(intValue)        // big-endian i32
        's' -> pStringCP1252(strValue)   // null-terminated
        'l' -> pT<ulong>(longValue)      // big-endian i64
    }
}
pSmart_int(scriptId)  // smart-encoded final value
```

---

## Summary: Int32 Read Method Byte Orders

| Method | Wire → Value | Mnemonic | Kotlin Server Write |
|--------|-------------|----------|-------------------|
| `gT<uint>()` | ABCD → 0xABCD | Standard BE | `writeByte(v shr 24); writeByte(v shr 16); writeByte(v shr 8); writeByte(v)` |
| `g4_alt1()` | ABCD → 0xCDAB | Mid-endian BADC | `writeByte(v shr 8); writeByte(v); writeByte(v shr 24); writeByte(v shr 16)` |
| `g4s_alt2()` | ABCD → 0xDCBA | Little-endian | `writeByte(v); writeByte(v shr 8); writeByte(v shr 16); writeByte(v shr 24)` |
| `g4s_alt3()` | ABCD → 0xBADC | Mid-endian CDAB | `writeByte(v shr 16); writeByte(v shr 24); writeByte(v); writeByte(v shr 8)` |

---

## Key Findings

1. **Handler Duality:** Each Interface ServerProt opcode has TWO handler registrations: a thunk/active handler in the `0x0027xxxx` range (from the serverprot vector table) and a "real" decode handler in the `0x0022xxxx`/`0x0023xxxx` range (from the Interfaces constructor at `0x0014df8c`). The serverprot table addresses point to the thunks; the Interfaces constructor registers the real handlers.

2. **Active Suffix:** Most Interface packets include a 3-byte suffix at the end consumed by the thunk handler for InterfaceComponent dirty-flag processing. The real handler reads the meaningful fields from the start of the packet.

3. **Four Int32 Encodings:** The client uses four distinct 4-byte integer read methods, each with a different byte order. The server must use the matching write method for each field.

4. **+0x80 Byte Transform:** Several inline ushort reads add 0x80 to the low byte before assembly. The server must subtract 0x80 from the low byte of the value before writing.

5. **IF_OPENTOP is simple:** Only 2 bytes (big-endian ushort interfaceId), no active suffix, no component hash.

6. **CHANGE_LOBBY / IF_SETPLAYERMODEL_OTHER:** Variable-length format string protocol. Reads a format string of type chars, then reads fields in REVERSE order per the format string.
