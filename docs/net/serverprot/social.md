# ServerProt: Social Category

## Overview

The Social packet handler category manages friend chat channel data and friend list updates. Two handlers are registered inline in `BindHandlers` (0x0011852a), not via a separate constructor call. Both are large, complex handlers dealing with variable-length social data structures.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success)

## ServerProt Opcode Table

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|--------------|-----------------|-------------------|
| 150 | var-byte | UPDATE_FRIENDCHAT_CHANNEL | `0x0022f660` | `0x016f9ca0` |
| 174 | var-byte | UPDATE_FRIENDLIST | `0x00277a40` | `0x016f97e0` |

---

## Handler Reference

### UPDATE_FRIENDCHAT_CHANNEL (opcode 150, size var-byte)
**Handler Address:** `0x0022f660` | **ServerProt Global:** `0x016f9ca0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Slot/action byte |
| (variable) | - | Channel data (multi-part, buffered) |

**Buffering Mechanism:**
This handler uses a multi-packet buffering system. On first invocation (when `FriendChat+0xD8 == 0`), it allocates a 20,000-byte buffer via `FUN_00cd4550` and stores it at `FriendChat+0xD8/0xE0/0xE8`. Subsequent packets append data to this buffer until the full message is assembled.

**Slot/Action Values:**
- `0x01`: Full channel decode (clears existing data, reads all members and settings)
- `0x02`: Sub-dispatch (partial update or member change)
- Other: Buffer continuation (appends packet data to buffer)

**Full Decode (slot == 0x01):**
1. Clears existing member hash map (EASTL hash_map with 0x98-byte entries)
2. Reads member count via `FUN_004b6450` (smart integer read)
3. Resizes bucket array to match member count
4. For each member: reads memberKey (uint via `FUN_004b6450`), display name (string via `FUN_00babc60`), then populates hash map entry
5. Reads channel settings: rank counts, variable domain data
6. For each variable domain entry: reads key, flag byte, packed value (via `FUN_0022f630`), world ID, name, strings
7. Assigns sort ordering by finding the member with highest rank byte at entry+0x35

**Hash Map Entry (0x98 bytes):**

| Offset | Size | Type | Description |
|--------|------|------|-------------|
| 0x00 | 4 | uint | Member key (hash map key) |
| 0x08 | 4 | uint | Entry index |
| 0x10 | 4 | uint | Previous value |
| 0x18 | 24 | eastl::string | Display name |
| 0x30 | 1 | byte | Unknown flag |
| 0x32 | 2 | ushort | World ID |
| 0x34 | 1 | byte | Unknown |
| 0x35 | 1 | byte | Rank byte (used for sort ordering) |
| 0x38 | 4 | uint | Unknown parameter |
| 0x3C | 1 | byte | Unknown flag |
| 0x40-0x4F | 16 | - | Zone data object header (vtable + refs) |
| 0x50 | 4 | uint | Update ID |
| 0x58 | 24 | eastl::string | World name |
| 0x70 | 24 | eastl::string | Additional string |
| 0x88 | 8 | long | Timestamp (packed) |
| 0x90 | 8 | long* | Next entry pointer (hash chain) |

**Client References:**
- `__DT_SYMTAB[0x49d]`: FriendChat system (+0x10=member hash map, +0xD0=buffer control)
- `__DT_SYMTAB[0x442]`: World lookup service

---

### UPDATE_FRIENDLIST (opcode 174, size var-byte)
**Handler Address:** `0x00277a40` | **ServerProt Global:** `0x016f97e0`

**Packet Format (when packetSize == 0):**
Clears the existing friend list (sets shared_ptr to null, decrements ref count).

**Packet Format (when packetSize > 0):**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Slot/action byte |

**If slot < 2 (full decode):**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Flags byte (bit 0 = unknown, bit 2 = has extra data) |
| g1 | byte | Visibility flag |
| gT_uint | uint | Update ID |
| gT_ulong | ulong | Creation timestamp |
| gStringCP1252ToUTF8 | string | Display name |
| gT_ushort_raw | ushort | Kick rank |
| gT_uint | uint | Member count |
| gT_ulong | ulong | Member timestamp |
| gT_ushort | ushort | Friend entry count |
| Per friend (0xB0 bytes each) | - | Friend data via `FUN_005b4210` |
| gT_ushort | ushort | Banned name count |
| Per banned name | string | Banned player name (gStringCP1252ToUTF8) |
| gT_ushort | ushort | Extra settings count |
| Per setting | - | Variable domain setting data |

**If slot >= 2:**
Clears the existing friend list shared_ptr.

**Friend Entry (0xB0 bytes):**
Each friend entry is decoded by `FUN_005b4210` and contains multiple EASTL strings, a zone data object (with vtable `PTR_FUN_01493600`), and numeric fields. The entries are stored in a vector at `FriendList+0x60/0x68/0x70` (begin/end/capacity pointers).

**Banned Names:**
Stored in a parallel vector at `FriendList+0x78/0x80/0x88` as EASTL strings (0x18 bytes each with SSO).

**Post-Processing:**
After reading all entries, the handler scans all friend entries to find the one with the highest rank byte (at entry+0x35), storing its index at `FriendList+0x90` (used for owner identification).

The handler wraps the allocated data structure in a shared_ptr (vtable `PTR_FUN_0148c0d0`) with reference counting and stores it at `__DT_SYMTAB[0x498]`.

**Client References:**
- `__DT_SYMTAB[0x498]`: Friend list shared_ptr (st_size field)
- `__DT_SYMTAB[0x442]`: World lookup service (for variable domain settings)
- `__DT_SYMTAB[0x49c]`: Clan system manager

---

## Key Subsystem Functions

| Address | Name | Description |
|---------|------|-------------|
| `0x004b6450` | SmartRead::ReadUint | Reads variable-length encoded uint |
| `0x00babc60` | SmartRead::ReadString | Reads string from buffered data |
| `0x0022f630` | SmartRead::ReadUshort | Reads ushort from buffered data |
| `0x0022eeb0` | SmartRead::ReadSmart | Reads smart-encoded value |
| `0x005b4210` | FriendEntry::Decode | Decodes a 0xB0-byte friend entry from Packet |
| `0x00cb29b0` | HeapInterface::Alloc | General heap allocation |
| `0x00cd4550` | BufferAlloc | Allocates large buffer (20000 bytes) |
| `0x001c2110` | FriendEntry::Destroy | Destructs a 0xB0-byte friend entry |
| `0x00d01ab0` | HashTable::GrowCheck | Checks if hash table needs growth |
| `0x00316df0` | HashTable::Clear | Clears hash table bucket array |
