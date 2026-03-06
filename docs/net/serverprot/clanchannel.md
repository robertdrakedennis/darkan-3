# ServerProt: ClanChannel Category

## Overview

The ClanChannel packet handler category manages full and incremental clan channel data updates. Two handlers are registered inline in `BindHandlers` (0x0011852a), not via a separate constructor call. Both handlers manage clan channel member lists stored in vectors of 0x50-byte entries.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success)

## ServerProt Opcode Table

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|--------------|-----------------|-------------------|
| 83 | var-byte | CLANCHANNEL_FULL | `0x00238aa0` | `0x016fa5a0` |
| 123 | var-size | CLANCHANNEL_DELTA | `0x0023fcd0` | `0x016fa0a0` |

---

## Handler Reference

### CLANCHANNEL_FULL (opcode 83, size var-byte)
**Handler Address:** `0x00238aa0` | **ServerProt Global:** `0x016fa5a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| g1 | byte | Channel slot selector |
| (variable) | - | Full clan channel data decoded via `FUN_0023b850` |

**Slot Selection:**
- `0` or `1`: Slot index (active or guest channel)
- `-1`: Clear active channel slot
- `< -1`: Clear both channel slots

**Behavior:** Allocates a ClanChannel object (0x80 bytes) via `FUN_00cb29b0`, decodes full channel data from the Packet via `FUN_0023b850`, wraps in a `shared_ptr`, and stores in the Client's clan channel slots at `__DT_SYMTAB[0x490]` offsets +0x20/+0x30/+0x50. If a previous ClanChannel object exists in the target slot, its reference count is decremented. This is the full-state version sent on login, clan join, or when too many deltas have accumulated.

**Client References:**
- `__DT_SYMTAB[0x490]`: Client state (clan channel slots)
- `__DT_SYMTAB[0x49c]`: Clan system manager

---

### CLANCHANNEL_DELTA (opcode 123, size var-size)
**Handler Address:** `0x0023fcd0` | **ServerProt Global:** `0x016fa0a0`

**Packet Format:**
| Read | Type | Description |
|------|------|-------------|
| gStringCP1252ToUTF8 | string | Display name |
| g1 | byte | Has previous name flag (1 = yes) |
| (if has previous name) gStringCP1252ToUTF8 | string | Previous display name |
| g2 BE | ushort | World ID (byte-swapped on LE) |
| g1s | byte (signed) | Rank value |
| (if rank != -128) gStringCP1252ToUTF8 | string | Clan name |

**Behavior:** Processes incremental clan channel updates. The operation depends on the rank value:

- **Rank == -128 (DELETE):** Searches the member list by display name and world ID. When found, removes the entry by shifting subsequent entries using `FUN_001d3400` (EASTL move operations) and shrinks the vector. Each member entry is 0x50 bytes containing strings (display name, previous name, clan name) at 0x18-byte EASTL string size with SSO.

- **Rank != -128 (ADD/UPDATE):** First checks if user already exists by comparing display names (using SSO-aware string comparison with `memcmp`). If found, updates the existing entry's fields (rank, name, clan name) via `FUN_00327ea0`. If not found, appends a new entry to the end of the vector (with capacity growth via `HeapInterface::Alloc` and element-by-element copy via `FUN_001c5d60`).

After any modification, if more than one member exists, re-sorts the member list via `FUN_0022b930`. Finally, invokes a callback at `channel+0x70` if set (notifies UI of clan channel change).

**Member Entry Layout (0x50 bytes):**

| Offset | Size | Type | Description |
|--------|------|------|-------------|
| 0x00 | 24 | eastl::string | Display name |
| 0x18 | 24 | eastl::string | Previous display name |
| 0x30 | 4 | uint | World ID |
| 0x34 | 4 | int | Rank |
| 0x38 | 24 | eastl::string | Clan name |

**Client References:**
- `__DT_SYMTAB[0x494]`: Client reference (for local player rank update)
- `__DT_SYMTAB[0x49c]`: Clan system manager
- `__DT_SYMTAB[0x4dc]`: Privacy/filtering settings (for ignore check)

---

## Key Subsystem Functions

| Address | Name | Description |
|---------|------|-------------|
| `0x00cb29b0` | HeapInterface::Alloc | Allocates memory for ClanChannel objects |
| `0x0023b850` | ClanChannel::Decode | Decodes full clan channel from Packet |
| `0x0022b930` | ClanChannel::SortMembers | Sorts member list after modification |
| `0x001c5d60` | ClanChannel::CopyEntry | Copies a 0x50-byte member entry |
| `0x00327ea0` | eastl::string::assign | EASTL string assignment |
| `0x001d3400` | eastl::move | EASTL move operation for entry shifting |
| `0x001c5c20` | ComparePlayerName | Compares player names for local player detection |
