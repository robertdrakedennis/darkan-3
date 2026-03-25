# ServerProt: WORLDLIST_FETCH_REPLY

> **Binary**: rs2client rev 947-1 (stripped, x86_64, Ghidra port 8083)
> **Handler**: `packethandlers::Social::WORLDLIST_FETCH_REPLY` at `0x0023a390`
> **Opcode**: 159 (varShort)
> **947 ProtEntry**: at `g_serverProt_WORLDLIST_FETCH_REPLY`
> **946 Opcode**: 150

## Overview

The WORLDLIST_FETCH_REPLY packet delivers the world list to the client. It supports multi-segment reassembly (the client accumulates data across multiple packets until the frame byte indicates the final segment) and two refresh modes: full refresh (complete world definitions + player counts) and delta refresh (player counts only).

The Ghidra label `UPDATE_FRIENDCHAT_CHANNEL` was misleading — this is the world list handler, confirmed by structural analysis of the read format and world hashtable population.

## Handler Architecture

The handler accesses the WorldList subsystem via `__DT_SYMTAB[0x49d]` (same vtable slot as the WorldData/FriendChat system). The WorldList object lives at offset `+0x10` from that subsystem pointer.

### Reassembly Buffer

The handler maintains a reassembly buffer at offsets `+0xd8`/`+0xe0`/`+0xe8` of the subsystem object:
- `+0xd8`: buffer capacity
- `+0xe0`: buffer data pointer
- `+0xe8`: current write position

On first invocation (when `+0xd8` is NULL), a 20,000-byte buffer is allocated via `jag::Packet::ResizeBuffer`. Subsequent data (after the frame byte) is appended via `memcpy`. When the frame byte is `0x01` (final segment), the reassembled buffer is parsed.

## Packet Format

### Outer Framing (from the raw ServerProt packet)

| Offset | Size | Read | Type | Description |
|--------|------|------|------|-------------|
| 0 | 1 | g1 | byte | **Frame byte**: `0` = more segments follow, `1` = final segment. If not `1`, remaining bytes are appended to the reassembly buffer and the handler returns. |

Bytes 1..N (the rest of the ServerProt payload after the frame byte) are appended to the reassembly buffer. When frame == 1, the full reassembled buffer is parsed as described below.

### Reassembled Payload Format

The reassembled payload position starts at 0. All reads below are from the reassembled buffer, NOT the original packet.

| Pos | Size | Read | Type | Description |
|-----|------|------|------|-------------|
| 0 | 1 | g1 | byte | **Refresh flag**: `2` = full refresh (world definitions + counts), `0` = delta (counts only) |

---

### Full Refresh (refreshFlag == 2)

| Pos | Size | Read | Type | Description |
|-----|------|------|------|-------------|
| 1 | 1 | g1 | byte | **Separator**: `1` = has world definitions section, `0` = skip to player counts |

#### When separator == 1: World Definitions Section

##### 1. Country List

| Read | Type | Description |
|------|------|-------------|
| getUnsignedSmart | smart | **countryCount**: number of country entries |

For each country (countryCount times):

| Read | Type | Description |
|------|------|-------------|
| getUnsignedSmart | smart | **countryId**: numeric country identifier |
| gjStr2 | JagString | **countryName**: country display name (version-byte-prefixed, null-terminated CP1252) |

The country entries are stored in an array at `WorldListObj+0x48`/`+0x50`/`+0x58`. Each entry is a 0x20-byte struct containing:
- `+0x00` (4 bytes): countryId
- `+0x08` (24 bytes): countryName (eastl::string, SSO capacity 0x17)

##### 2. World ID Range and Count

| Read | Type | Description |
|------|------|-------------|
| getUnsignedSmart | smart | **minWorldId**: lowest world number offset (typically 0) |
| getUnsignedSmart | smart | **maxWorldId**: stored at `WorldListObj+0x3C`. Upper bound for world IDs. |
| getUnsignedSmart | smart | **worldCount**: number of world entries to follow. Stored at `WorldListObj+0x40`. |

##### 3. World Entries

For each world (worldCount times):

| Read | Type | Description |
|------|------|-------------|
| getUnsignedSmart | smart | **worldNumberOffset**: added to `minWorldId` to get the actual world number used as hashtable key |
| g1 | byte | **worldIndex**: index into the country array (determines which country entry this world belongs to) |
| g4 (BE) | int | **flags**: world flags bitmask (members, quickchat, pvp, lootshare, etc.) |
| getUnsignedSmart | smart | **activityPresence**: if non-zero, an activity string follows. If 0, no activity string is read. The raw smart value is stored at field `+0x34` of the world entry. |
| gjStr2 (conditional) | JagString | **activity**: activity/status string (e.g. "Skill Total - 1500"). Only read if `activityPresence != 0`. |
| gjStr2 | JagString | **hostname**: server hostname for this world |
| gjStr2 | JagString | **serverAddress**: additional server address string (second address field at `+0x70` in the world entry struct) |

Each world entry is inserted into a hashtable at `WorldListObj+0x10`/`+0x18`/`+0x20`, keyed by world number. The entry struct is 0x98 bytes:

| Offset | Size | Type | Field |
|--------|------|------|-------|
| 0x00 | 4 | uint | worldNumber (hashtable key) |
| 0x08 | 4 | uint | worldIndex (country array index) |
| 0x10 | 4 | uint | countryId (resolved from country array) |
| 0x18 | 24 | string | countryName (copied from country array) |
| 0x30 | 4 | uint | flags |
| 0x34 | 4 | uint | activityPresence (smart value) |
| 0x38 | 24 | string | activity |
| 0x50 | 4 | uint | minWorldId + worldNumberOffset (= actual world number for the WorldListUI) |
| 0x58 | 24 | string | hostname |
| 0x70 | 24 | string | serverAddress (second address) |
| 0x88 | 8 | long | playerCount (initialized to -1/0xFFFFFFFF) |
| 0x90 | 8 | ptr | next pointer (hashtable chain) |

##### 4. Revision

| Read | Type | Description |
|------|------|-------------|
| g4 (BE) | int | **revision**: world list revision/CRC. Stored at `WorldListObj+0x60`. Client sends this back in WORLDLIST_FETCH to enable delta updates. |

After storing the revision, `WorldListObj` byte 0 is set to `1` (marks as initialized).

---

### Player Count Section (always sent, both full and delta)

Read for each world (worldCount iterations, using `WorldListObj+0x40` as the count):

| Read | Type | Description |
|------|------|-------------|
| (see below) | smart/short | **worldNumber**: encoded as either a 1-byte or 2-byte value |
| g2 (BE) | ushort | **playerCount**: player count for this world. `0xFFFF` means "use previous value" (i.e., no change) |

#### World Number Encoding in Player Counts

The world number uses a custom encoding (NOT getUnsignedSmart):

1. **Peek** at the next byte without consuming it
2. If the byte's **high bit is set** (value >= 0x80):
   - Read a **g2 (BE unsigned short)** and subtract `0x8000` from the result
   - This yields the world number
3. If the high bit is **clear** (value < 0x80):
   - Consume that single byte as the world number directly

This is functionally equivalent to the standard signed smart encoding that maps:
- `0x00..0x7F` -> values 0..127 (1 byte)
- `0x8000..0xFFFF` -> values 0..32767 (2 bytes, subtract 0x8000)

#### Player Count Sentinel

When the g2 read returns `0xFFFF`, the handler uses the **previous playerCount value** (stored in `R10D`/`EDI` — initialized to `0xFFFFFFFF` at the start of the loop). This allows the server to skip worlds whose counts haven't changed.

After processing player counts, the handler looks up each world number in the hashtable. If found, the world entry's `+0x88` field (playerCount) is updated — but only if the entry is NOT the sentinel entry at the end of the hashtable bucket chain.

---

### Delta Refresh (refreshFlag != 2)

When the refresh flag is NOT `2`:
- The separator byte is NOT read
- No country or world definition section
- Jumps directly to the player count section using `WorldListObj+0x40` (previously stored worldCount)

---

### Post-Processing: World Pointer Vector

After parsing, the handler builds a vector of pointers to world entries (at subsystem offsets `+0x120`/`+0x128`/`+0x130`). It iterates from `minWorldId` to `maxWorldId`, looking up each world number in the hashtable. For each found entry, a pointer to the entry's data (at entry `+0x08`, skipping the key field) is appended to the vector. This vector is used by the lobby UI to display worlds in order.

Finally:
- Subsystem `+0xB0` is set to `0` (refresh complete flag)
- The reassembly buffer is freed
- A timestamp is recorded at `+0xB8` (steady_clock / 1000000, in milliseconds)

## Key Differences from Expected Format

1. **No port field**: The expected format included a conditional `g4` port read when flags bit 30 is set. The 947-1 handler does **NOT** read any port. There is no flag-based conditional read at all. Port information must come from elsewhere (likely `SET_WORLD_TARGET` packet or jav_config).

2. **Three strings per world, not two**: The handler reads THREE `gjStr2` strings per world entry:
   - Activity string (conditional on activityPresence smart)
   - Hostname
   - Server address (a second address string, stored at entry `+0x70`)

3. **Activity string is conditional**: The activity string is only read if the preceding `getUnsignedSmart()` value is non-zero. When zero, the string read is skipped entirely.

4. **JagString encoding (gjStr2)**: All strings use the version-byte-prefixed format (`0x00` version byte, then null-terminated CP1252). This is `gjStr2`, NOT plain RS string.

5. **Smart encoding matches**: Country IDs, world count, world number offsets all use `getUnsignedSmart` as expected.

6. **minWorldId**: An additional smart value is read before `maxWorldId`, stored at `WorldListObj+0x38`. This is used as the base for the world number displayed in the UI (`minWorldId + worldNumberOffset` = actual world number).

7. **Player count world number encoding**: Uses a custom peek-and-branch encoding rather than `getUnsignedSmart`. Functionally similar but the code path differs (it manually checks the high bit and branches, rather than calling the smart function).

## String Encoding: gjStr2

The string reading function at `0x00ba30c0` implements `gjStr2`:

```
1. Read 1 byte (version)
2. If version != 0:
   - Return empty string (version mismatch)
3. Read null-terminated CP1252 string starting at next byte
4. Advance position past the null terminator
```

The server MUST write strings in this format:
- Write `0x00` (version byte)
- Write string bytes in CP1252 encoding
- Write `0x00` (null terminator)

For empty strings: write `0x00 0x00` (version + empty null-terminated string).

## Read Function Reference

| Function | Address | Description |
|----------|---------|-------------|
| g1 | inline | Read 1 unsigned byte |
| g2 (BE) | `0x00239be0` | Read 2-byte big-endian unsigned short |
| g4 (BE) | `0x0023a360` | Read 4-byte big-endian unsigned int |
| getUnsignedSmart | `0x004c59b0` | Peek byte: if < 0x80, read 1 byte; else read g2 and add 0x8000 (return unsigned short) |
| gjStr2 | `0x00ba30c0` | Read version byte (must be 0), then null-terminated CP1252 string |
