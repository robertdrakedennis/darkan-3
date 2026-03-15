# IF_OPENTOP (ServerProt opcode 207 / 0xCF)

**Binary:** rs2client rev 946 (STRIPPED)
**Packet size:** 2 bytes (fixed)
**Verified:** 2026-03-12

**IMPORTANT:** Previous documentation incorrectly identified this as opcode 108 (0x6C) with size 6.
That was actually IF_SETOBJECT_ALWAYSNUM. The correct mapping was verified from RegisterAll at
`0x00182030`: `InitEntry(&DAT_016fd220, 0xcf, 2)`.

## Packet Format

| Offset | Size | Read Method | Type | Field Name | Description |
|--------|------|-------------|------|------------|-------------|
| 0 | 2 | ushort BE | uint16 | interfaceId | The interface definition ID to open as the top-level (root) interface. |

## Handler Behavior

The handler creates an InterfaceManager update entry of type 0xC, marks it dirty, and stores the
interface ID in slot +0x20 via `SetUpdateSlotValue`. On the next interface update tick
(`FUN_002e2af0`), the dirty entry is processed: the interface definition is loaded from cache,
components are created and laid out, and `ScriptRunner::ExecuteOnLoad` fires OnLoad scripts for each
component.

## Server Implementation

```
// IF_OPENTOP -- opcode 0xCF (207), fixed 2 bytes
// Write interfaceId as ushort BE

// Interface ID (big-endian ushort):
output.writeByte((interfaceId shr 8) and 0xFF)  // high byte
output.writeByte(interfaceId and 0xFF)           // low byte
```

## Lobby Interface ID

The interface ID is a server-side choice. The client does not hardcode any specific interface ID for
the lobby state. For rev 946 cache, interface 1477 is the modern lobby interface.

The old Java client (rev ~880) auto-opened the lobby interface from EntityDefaults.lobbyWindow
(opcode 6 in defaults config, archive 28 file 3). The NXT client does NOT auto-open — the server
MUST send IF_OPENTOP explicitly.
