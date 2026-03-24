# ServerProt: UPDATE_FRIENDLIST

> **Name correction**: This packet was previously mislabeled `UPDATE_SITESETTINGS`.
> The verified Jagex name is `jag::ServerProt::UPDATE_FRIENDLIST` (from librs2client.so).

## Summary

| Field | Value |
|-------|-------|
| **Opcode (rev 946)** | 18 (0x12) |
| **Opcode (rev 947)** | 102 (0x66) |
| **Size** | var_short |
| **Handler Address (946)** | `0x00242840` (rs2client rev 946) |
| **Handler Address (947)** | `0x00247a60` (rs2client rev 947) |
| **Handler Wrapper (947)** | `FUN_00191190` (thin wrapper calling `UPDATE_SITESETTINGS`) |
| **ProtEntry Address (947)** | `DAT_016ec820` |
| **Data Address** | `0x016e91c0` (946) |
| **Category** | SiteSettings |
| **Client Subsystem** | `__DT_SYMTAB[0x49a]` (RelationshipManager / SiteSettings) |
| **Direction** | Server -> Client |
| **Old Java Client Name** | `FRIEND_STATUS` (opcode 74 in old revision) |

## Purpose

This packet sends the friends list to the client. It carries one or more friend
entries, each containing the friend's display name, previous name, online world,
friend chat rank, flags, and notes. The client uses this to populate and update
the friends list in the social tab and lobby UI.

The packet is sent:
- During lobby login (full friends list, all entries in one packet)
- When a friend's status changes (single entry update)
- When a friend changes their display name (`warnMessage = 1`)

If the packet is sent with 0 bytes of payload, the client's handler
falls through to the post-loop code which sorts the existing list and
triggers a UI refresh without modifying any entries.

## Packet Format

The payload consists of zero or more **friend entries** concatenated
sequentially. The client reads entries in a loop until the packet
position reaches the packet size.

### Per-Entry Format

| # | Read | Bytes | Type | Field | Description |
|---|------|-------|------|-------|-------------|
| 1 | g1 | 1 | byte | warnMessage | Action flag: `0` = normal update/add, `1` = display name change notification |
| 2 | gStr | var | CP1252 string | displayName | Current display name (null-terminated) |
| 3 | gStr | var | CP1252 string | previousName | Previous display name / username (null-terminated). Empty string if no previous name. |
| 4 | g2 | 2 | unsigned short | worldId | World the friend is on. `0` = offline. |
| 5 | g1 | 1 | unsigned byte | fcRank | Friend chat rank (0-255). See rank table below. |
| 6 | g1 | 1 | byte (bitfield) | flags | Packed flags: bit 0 = `referrer`, bit 1 = `referred` |
| **Conditional: only if worldId > 0** ||||
| 7 | gStr | var | CP1252 string | worldName | World display name (e.g., "World 1", "Lobby 1"). Only present when online. |
| 8 | g1 | 1 | unsigned byte | platform | Platform ID. `0` = RuneScape, `1` = Other/Mobile. Only present when online. |
| 9 | g4 | 4 | unsigned int | worldFlags | World flags bitmask (members, PvP, etc.). Only present when online. Big-endian. |
| **Unconditional (always present)** ||||
| 10 | gStr | var | CP1252 string | notes | Friend notes string (null-terminated). Empty string if no notes set. **NXT-only field, not present in the old Java client.** |

### Buffer Operations Mapping

| # | NXT Client Read | Server Write (Kotlin) |
|---|-----------------|----------------------|
| 1 | raw byte read (`*(byte*)(data + pos)`) | `writeByte(v)` |
| 2 | `gStringCP1252ToUTF8` (`FUN_00cd2900`) | `writeString(s)` |
| 3 | `gStringCP1252ToUTF8` (`FUN_00cd2900`) | `writeString(s)` |
| 4 | `gT<unsigned_short>` (`Packet::gT_ushort_`) | `writeShort(v)` |
| 5 | raw byte read | `writeByte(v)` |
| 6 | raw byte read (then bit-decomposed) | `writeByte(v)` |
| 7 | `gStringCP1252ToUTF8` (`FUN_00cd2900`) | `writeString(s)` |
| 8 | raw byte read | `writeByte(v)` |
| 9 | `gT<unsigned_int>` (`FUN_001c1d80`) | `writeInt(v)` |
| 10 | `gStringCP1252ToUTF8` (`FUN_00cd2900`) | `writeString(s)` |

All integer reads are **big-endian**. No alt byte order transforms are used.

## Flags Byte (Field 6) Decomposition

The flags byte at field 6 is decomposed by the client as follows:

```
bit 0 (& 0x01) = referrer     (was this friend referred by the player?)
bit 1 (>> 1 & 0x01) = referred (was the player referred by this friend?)
```

In the old Java client, this was a simple boolean byte (0 or 1 for referrer only).
The NXT client packs two flags into a single byte. For basic implementation,
sending `0x00` is safe.

## warnMessage Behavior

When `warnMessage == 0` (normal update):
- The client searches its existing friends list for an entry matching `displayName`
- If found AND `worldId` differs: updates the entry and logs a world-change notification
- If found AND `worldId` matches: updates all fields silently
- If not found: adds a new friend entry to the list

When `warnMessage == 1` (name change):
- The client searches for an entry matching `previousName` (field 3)
- If found: updates the `displayName` to the new name
- The `worldId` and other fields are still read but may be ignored for the name change notification

## Friend Chat Rank Values

| Value | Rank |
|-------|------|
| 0 | Not ranked |
| 1 | Recruit |
| 2 | Corporal |
| 3 | Sergeant |
| 4 | Lieutenant |
| 5 | Captain |
| 6 | General |
| 7 | Owner |

## Client Data Structure

Each friend entry is stored as a 0x78-byte (120-byte) struct in the client:

| Offset | Size | Type | Field |
|--------|------|------|-------|
| 0x00 | 24 | eastl::string | displayName |
| 0x18 | 24 | eastl::string | previousName |
| 0x30 | 4 | uint | worldId |
| 0x38 | 24 | eastl::string | worldName |
| 0x50 | 4 | uint | fcRank |
| 0x54 | 4 | uint | platform |
| 0x58 | 1 | byte | referred (flag bit 1) |
| 0x59 | 1 | byte | referrer (flag bit 0) |
| 0x5C | 4 | uint | worldFlags |
| 0x60 | 24 | eastl::string | notes |

The entries are stored in an `eastl::vector` at SiteSettings subsystem offsets:
- `+0x18`: vector begin pointer
- `+0x20`: vector end pointer
- `+0x28`: vector capacity pointer

Maximum 400 entries (hardcoded limit at `iVar31 < 400` check in handler).

## Post-Processing

After all entries are read, the handler:
1. Accesses `__DT_SYMTAB[0x49c]` (clan system) to read the current world ID
2. Performs an insertion sort on the friends vector, prioritizing:
   - Friends on the same world as the player
   - Online friends over offline friends
   - Referrer friends over non-referrer friends
3. Triggers registered UI listeners via `FUN_001e7210` to refresh the friends list display

## Server Implementation Notes

### Empty Packet (No Friends)

To send an empty friends list during lobby init, send the packet with 0 bytes
of payload (just the opcode + var_short length header of 0x0000). The client
will skip the entry loop and proceed directly to post-processing (sort + UI refresh).

### Minimal Friend Entry Example

For a single offline friend named "TestFriend" with no previous name, no rank,
and no notes:

```
writeByte(0)              // warnMessage = 0 (normal)
writeString("TestFriend") // displayName
writeString("")           // previousName (empty)
writeShort(0)             // worldId = 0 (offline)
writeByte(0)              // fcRank = 0 (unranked)
writeByte(0)              // flags = 0 (not referrer, not referred)
                          // NO worldName/platform/worldFlags (worldId == 0)
writeString("")           // notes (empty)
```

### Online Friend Entry Example

For an online friend named "OnlinePal" on world 1:

```
writeByte(0)              // warnMessage = 0
writeString("OnlinePal")  // displayName
writeString("")           // previousName
writeShort(1)             // worldId = 1
writeByte(0)              // fcRank = 0
writeByte(0)              // flags = 0
writeString("World 1")   // worldName (only because worldId > 0)
writeByte(0)              // platform = 0 (RuneScape)
writeInt(0x00000001)      // worldFlags (members bit)
writeString("")           // notes
```

## Cross-Reference

| Source | Name | Notes |
|--------|------|-------|
| rs2client (rev 946, NXT) | UPDATE_SITESETTINGS | Handler at `0x00242840`, opcode 18, Ghidra-verified |
| rs2client (rev 947, NXT) | UPDATE_SITESETTINGS | Handler at `0x00247a60`, opcode 102, Ghidra-verified |
| cheddarcheese (old Java client deob) | FRIEND_STATUS | Opcode 74, same format minus `notes` field |
| Darkan 2 server | FRIEND_STATUS | `FriendStatus.java` encoder, same format minus `notes` |
| librs2client.so (rev ~890) | `jag::packethandlers::Friends` | 3 handler lambdas for friends packets |

## Verified Against

- **rs2client** (rev 946, stripped) on Ghidra port 8082 -- handler fully decompiled and traced
- **rs2client** (rev 947, stripped) on Ghidra port 8083 -- handler decompiled, structurally identical to 946, opcode verified via ProtEntry at `DAT_016ec820`
- **cheddarcheese** Java client deob -- `PacketDecoder.java` FRIEND_STATUS handler (lines 1951-2051)
- **Darkan 2 server** -- `FriendStatus.java` encoder (confirms server-side field order)

## Key Ghidra Functions

### Rev 946 (rs2client.946-5, port 8082)

| Address | Name | Purpose |
|---------|------|---------|
| `0x00242840` | UPDATE_SITESETTINGS handler | Main packet handler |
| `0x00cd2900` | gStringCP1252ToUTF8 | Reads null-terminated CP1252 string |
| `0x001c1d50` | Packet::gT_ushort_ | Reads big-endian unsigned short |
| `0x001c1d80` | gT<unsigned_int> | Reads big-endian unsigned int |
| `0x00327d00` | eastl::string::assign | String copy/assign |
| `0x001c5cb0` | string compare | Compares eastl::string to C string |
| `0x001e7210` | UI trigger dispatch | Notifies listeners of friends list change |

### Rev 947 (rs2client.947-1, port 8083)

| Address | Name | Purpose |
|---------|------|---------|
| `0x00247a60` | UPDATE_SITESETTINGS handler | Main packet handler (structurally identical to 946) |
| `0x00191190` | Handler wrapper | Thin wrapper calling UPDATE_SITESETTINGS |
| `0x00cd5710` | gStringCP1252ToUTF8 | Reads null-terminated CP1252 string |
| `0x001c1480` | gT<unsigned_int> | Reads big-endian unsigned int |
| `0x002ac2f0` | eastl::string::assign | String copy/assign |
| `0x001c10a0` | string compare | Compares eastl::string to C string |
| `0x001e9ee0` | UI trigger dispatch | Notifies listeners of friends list change |

## Rev 947 Opcode Derivation

The opcode was determined by tracing the handler registration chain:

1. `jag::packethandlers::SiteSettings::UPDATE_SITESETTINGS` at `0x00247a60` is the inner handler
2. `FUN_00191190` at `0x00191190` is a thin wrapper that calls it
3. In `BindHandlers` at `0x001185aa`, the wrapper is stored at `DAT_016ec848` (ProtEntry base + 0x28)
4. The ProtEntry base is `DAT_016ec820`, created by `InitEntry(&DAT_016ec820, 0x66, -2)`
5. First arg = address, second = opcode (0x66 = 102), third = size (-2 = var_short)

ProtEntry structure layout (0x40 bytes):
- `+0x00`: opcode (int32)
- `+0x04`: size (int32, -1 = var_byte, -2 = var_short)
- `+0x08`: name string pointer
- `+0x10`: chain/back pointer (set by BindHandlers)
- `+0x18`: auxiliary data
- `+0x20`: setup function pointer
- `+0x28`: handler function pointer
