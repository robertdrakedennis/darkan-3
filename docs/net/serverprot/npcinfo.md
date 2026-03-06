# ServerProt: NPCInfo Category

## Overview

The NPCInfo packet handler category manages NPC data decoding and NPC-specific animation updates. Two handlers are registered inline in `BindHandlers` (0x0011852a), not via a separate constructor call. The primary handler (`decode`) processes the main NPC_INFO packet which contains the full NPC update stream (positions, appearances, animations, etc.), while `decodeAnimSpecific` handles targeted animation updates for individual NPCs.

All handlers follow the standard ServerProt invocation pattern:
- Called via `ServerProt.invokePtr(ServerProt.callableData, Packet*, &packetSize)`
- `callableData` contains a pointer to the Client struct
- Return value: `&DAT_016fb240` (success)

## ServerProt Opcode Table

| Opcode | Size | Handler Name | Handler Address | ServerProt Global |
|--------|------|--------------|-----------------|-------------------|
| 28 | var-byte | NPC_INFO | `0x0027a030` | `0x016fada0` |
| 165 | 9 | NPC_ANIM_SPECIFIC | `0x00279ce0` | `0x016f9a20` |

---

## Handler Reference

### NPC_INFO (opcode 28, size var-byte)
**Handler Address:** `0x0027a030` | **ServerProt Global:** `0x016fada0`

**Prototype:** `void * decode(long * client, Packet * packet, uint * packetSize)`

**Behavior:** The primary NPC update packet. This is one of the most complex handlers in the client, processing the bitpacked NPC update stream that contains:
- NPC position updates (movement, teleportation)
- NPC appearance changes (model, recoloring)
- NPC animation state changes
- NPC spawn/despawn events
- Extended info blocks (hit markers, headbars, overhead text, etc.)

The handler reads bitpacked data from the packet to efficiently encode updates for all visible NPCs. This packet is sent every game tick and contains all NPC state changes within the player's viewport.

**Note:** The function at `0x0027a030` in `parsed_functions.txt` maps to a `_Base_manager` lambda stub for the `jag::opcode::Entities` constructor. The actual decode logic is contained within the function body but is extremely large and heavily optimized with bitwise operations.

**Client References:**
- `__DT_SYMTAB[0x497]`: NPCManager (NPC list, NPC count)

---

### NPC_ANIM_SPECIFIC (opcode 165, size 9)
**Handler Address:** `0x00279ce0` | **ServerProt Global:** `0x016f9a20`

**Packet Format (9 bytes):**
| Offset | Size | Type | Read Method | Description |
|--------|------|------|-------------|-------------|
| 0 | 4 | int | raw bytes (custom LE) | Animation ID (b0 + b1*0x100 + b2*0x10000 + b3*0x1000000) |
| 4 | 1 | byte | g1 | Slot index (negated, -0x80 transform) |
| 5 | 2 | ushort | g2 BE | NPC type (byte-swapped on LE) |
| 7 | 2 | ushort | g2 BE | NPC server index (byte-swapped on LE) |

**Behavior:** Applies a specific animation to an NPC's extended animation buffer. The process:

1. Reads the animation ID from 4 raw bytes with custom byte ordering
2. Reads the slot index byte, applying negation and -0x80 transform: `slot = (-byte - 0x80) & 0xFF`
3. Reads NPC type and server index as big-endian ushorts
4. Looks up the NPC via `NPCList::GetNPCNode(npcManager, serverIndex)`
5. If the NPC exists, accesses or creates its animation buffer at `PathingEntity+0xE28`

**Animation Buffer (0x98 bytes):**
The animation buffer contains two parallel arrays of 8 animation slots each:
- `+0x00`: Primary animation ID array (8 x int, initialized to -1)
- `+0x48`: Secondary animation type array (8 x int, initialized to -1)
- `+0x90`: Active flag byte

If no animation buffer exists, one is allocated:
- If `PathingEntity+0xDA8` is non-null (has render data): copies from template at `PathingEntity+0x1080` via `FUN_00630240`
- Otherwise: creates fresh buffer with both arrays initialized to -1 (0xFFFFFFFF)

After setting the animation values:
- `primaryAnimIds[slot] = animationId`
- `secondaryAnimTypes[slot] = (short)npcType`

Calls `PathingEntity::ApplyExtendedAnimations` to apply the changes to the entity's visual state.

**Client References:**
- `__DT_SYMTAB[0x497]`: NPCManager (NPC lookup via `NPCList::GetNPCNode`)

---

## Key Subsystem Functions

| Address | Name | Description |
|---------|------|-------------|
| `NPCList::GetNPCNode` | (via `__DT_SYMTAB[0x497]`) | Looks up NPC by server index |
| `PathingEntity::ApplyExtendedAnimations` | (called after anim update) | Applies animation buffer changes to entity |
| `0x00630240` | AnimBuffer::CopyFrom | Copies animation buffer from template |
| `0x001d69f0` | AnimBuffer::InitArray | Initializes animation array with capacity |
