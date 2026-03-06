# Social System Protocol (Friends, Ignore, Chat)

## Overview

The NXT client manages social relationships through `jag::RelationshipManager`, which maintains separate friend and ignore lists. The system handles friend/ignore list updates from the server, login/logout notifications, and integrates with chat message filtering.

## Data Structures

### FriendIgnore (0x50 = 80 bytes, base class)

Ghidra struct: `FriendIgnore` in `/jag`

| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0x00 | 24 | eastl::string | displayName | Current display name |
| 0x18 | 24 | eastl::string | previousName | Previous display name |
| 0x30 | 24 | eastl::string | notes | Additional info / notes |

**Symbol dump confirms:** `jag::FriendIgnore::FriendIgnore(jag::FriendIgnore const&)` (copy ctor), `jag::FriendIgnore::~FriendIgnore()` (dtor)

### Friend (0x78 = 120 bytes, extends FriendIgnore)

Ghidra struct: `Friend` in `/jag`

| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0x00 | 24 | eastl::string | displayName | Current display name (from FriendIgnore) |
| 0x18 | 24 | eastl::string | previousName | Previous display name (from FriendIgnore) |
| 0x30 | 24 | eastl::string | notes | Notes (from FriendIgnore) |
| 0x48 | 8 | long | timestamp | steady_clock timestamp of last status update |
| 0x50 | 4 | int | world | World number (0 = offline) |
| 0x54 | 4 | int | rank | Friend chat rank |
| 0x58 | 4 | int | fcRank | Legacy FC rank |
| 0x5C-0x77 | 28 | | padding/extra | Additional friend-specific data |

**Symbol dump confirms:** `jag::Friend::Friend(jag::Friend const&)`, `jag::Friend::operator=(jag::Friend const&)`, `jag::Friend::~Friend()`

### RelationshipManager Layout

| Offset | Size | Type | Name | Description |
|--------|------|------|------|-------------|
| 0x18 | 8 | Friend* | friendsBegin | Start of friends vector |
| 0x20 | 8 | Friend* | friendsEnd | End of friends vector |
| 0x30 | 8 | FriendIgnore* | ignoresBegin | Start of ignores vector |
| 0x38 | 8 | FriendIgnore* | ignoresEnd | End of ignores vector |

**Symbol dump confirms:** `jag::RelationshipManager::ChangeMainState`, `CompareNames`, `FriendComparitor`, `IsFriend`, `IsIgnored`, `SortFriendsList`

## Server Packet Handlers

### UPDATE_FRIENDLIST

**Address:** `0x00277a40`
**Ghidra name:** `jag::packethandlers::Social::UPDATE_FRIENDLIST`

This is the primary friend list data handler. It manages a complex object with entries of stride 0xB0 (176 bytes) containing multiple strings and status data. The handler supports both full updates (byte 0x01 for full decode) and partial updates (byte 0x02 for sub-dispatch).

**Key behaviors:**
- Slot 0: Full clear and rebuild of the friend/relationship data
- Reads member entries with per-entry variable decode via `FUN_005b4210`
- Manages vector of member names (eastl::string, stride 0x18)
- Supports extra settings/variables per member
- Stores results in shared_ptr at the `0x498` offset of the packet handler base

### UPDATE_FRIENDCHAT_CHANNEL

**Address:** `0x0022f660`
**Ghidra name:** `jag::packethandlers::Social::UPDATE_FRIENDCHAT_CHANNEL`

Handles friend chat (FC) channel data updates. Manages a hash map of FC members.

**Key behaviors:**
- Reads first byte to distinguish full update vs incremental
- For full updates: clears the hash map, reads member count via `FUN_004b6450`
- Each member entry (0x98 bytes in hash map):
  - Key: member index (uint)
  - Values: category index, name/rank data, multiple eastl::strings
  - Read via `FUN_004b6450` (smart value) and `FUN_00babc60` (string)
- Reads banned names as an array of eastl::strings
- Manages timestamp for cache invalidation
- Allocates 20,000 byte buffer for incoming data if not initialized

## Friend/Ignore List Queries

### IsOnFriendsList

**Address:** `0x00238800`
**Ghidra name:** `jag::RelationshipManager::IsOnFriendsList`

Iterates the friend list (entries at stride 0x78) checking both `displayName` (offset 0x00) and `previousName` (offset 0x18) for a match. Also checks the local player's own name.

### IsOnIgnoreList

**Address:** `0x00237600`
**Ghidra name:** `jag::RelationshipManager::IsOnIgnoreList`

Iterates the ignore list (entries at stride 0x50) at RelationshipManager offset 0x30-0x38.

### NotifyFriendLoginStatus

**Address:** `0x002377b0`
**Ghidra name:** `jag::RelationshipManager::NotifyFriendLoginStatus`

Checks a friend entry's timestamp. If status changed more than 5 seconds ago (prevents login flood spam), shows either:
- "has logged in" (if world > 0) via `PTR_s_has_logged_in_`
- "has logged out" (if world == 0) via `PTR_s_has_logged_out_`

## CS2 Script Opcodes

### friendlist_add

**Address:** `0x003d3f80`
**Ghidra name:** `jag::opcode::friendlist_add`

**Stack:** Pops string (name) from string stack
**Validation:**
1. Checks capacity: 200 (normal) or 400 (premium)
2. Rejects self-add
3. Rejects if already on friends list
4. Rejects if on ignore list
5. Sends `FRIENDLIST_ADD` ClientProt to server

### friendlist_contains

**Address:** `0x003d3ca0`
**Ghidra name:** `jag::opcode::friendlist_contains`

**Stack:** Pops string (name), pushes int (boolean result)
Calls `IsOnFriendsList` and pushes result to int stack.

### ignorelist_add

**Address:** `0x003afbc0`
**Ghidra name:** `jag::opcode::ignorelist_add`

**Stack:** Pops string (name) from string stack
**Validation:**
1. Checks capacity: 100 (normal) or 400 (premium)
2. Rejects self-add
3. Rejects if already on ignore list
4. Rejects if on friends list (with message "Remove from your friends list first")
5. Sends `IGNORELIST_ADD` ClientProt to server

## Chat Message Handlers

All chat message handlers check the ignore list before displaying messages. They share a common pattern:
1. Read sender name from packet
2. Check against ignore list via `IsOnIgnoreList`
3. If not ignored, decode message and add to `ChatHistory::AddChat`

| Address | Ghidra Name | Description |
|---------|-------------|-------------|
| 0x001e6640 | jag::packethandlers::Chat::MESSAGE_GAME | Game/system messages with type routing |
| 0x001eb6c0 | jag::packethandlers::Chat::MESSAGE_PRIVATE | Private message from another player |
| 0x001ebcd0 | jag::packethandlers::Chat::MESSAGE_PRIVATE_ECHO | Echo of sent private message |
| 0x001ee310 | jag::packethandlers::Chat::MESSAGE_PUBLIC | Public chat from another player |
| 0x001ec340 | jag::packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE | Quick chat private message |
| 0x0026b5f0 | jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL | Friend chat channel message |

### Common Chat Message Fields

```
// MESSAGE_PRIVATE format:
string senderName          // gStringCP1252ToUTF8
ushort world               // big-endian
byte[3] messageId          // unique message identifier
byte   chatType            // chat channel type
byte   quickChatFlag       // 0=normal, 1=quickchat
ushort pmSenderWorld       // sender's world (big-endian)
// then message content...
```

### Chat Type Table

The global table at `DAT_016cdd60` contains chat type definitions, each 0xC (12) bytes:
- `+0x00`: int typeId
- `+0x04`: int iconId (-1 = no icon)
- `+0x08`: byte flags (bit 0 = requires ignore check, bit 1 = has icon prefix)

## Related Functions

| Address | Name | Description |
|---------|------|-------------|
| 0x00238800 | jag::RelationshipManager::IsOnFriendsList | Check if name is on friends list |
| 0x00237600 | jag::RelationshipManager::IsOnIgnoreList | Check if name is on ignore list |
| 0x002377b0 | jag::RelationshipManager::NotifyFriendLoginStatus | Show login/logout notification |
| 0x00277a40 | jag::packethandlers::Social::UPDATE_FRIENDLIST | Full friend list update handler |
| 0x0022f660 | jag::packethandlers::Social::UPDATE_FRIENDCHAT_CHANNEL | FC channel data handler |
| 0x003d3f80 | jag::opcode::friendlist_add | CS2 add friend opcode |
| 0x003d3ca0 | jag::opcode::friendlist_contains | CS2 check friend opcode |
| 0x003afbc0 | jag::opcode::ignorelist_add | CS2 add ignore opcode |

## Error Strings

| Address | String |
|---------|--------|
| 0x010d6ce8 | "Your friends list is full (200 names maximum)" |
| 0x010d6e18 | "Your friends list is full (400 names maximum)" |
| 0x010d6bb8 | "You can't add yourself to your own friends list." |
| 0x010d6ae8 | " is already on your friends list." |
| 0x010d53e8 | "Your ignore list is full. Max of 100 users." |
| 0x010d5530 | "Your ignore list is full. Max of 400 users." |
| 0x010d52b8 | "You can't add yourself to your own ignore list." |
| 0x010d51e8 | " is already on your ignore list." |
| 0x010d5178 | " from your friends list first." |
| 0x010d5e0d | " from your ignore list first." |
