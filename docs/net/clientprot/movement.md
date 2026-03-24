# ClientProt: Movement Packets

> **Rev 947-1**: Opcodes and many sizes changed significantly from 946. See `clientprot-table.md` for the 947-1 table.

Packets related to player movement, including minimap clicks and game-world walking.

## MOVE_GAME (Primary Movement)

### MOVE_GAME (Standard)
| Field | Description |
|-------|-------------|
| **Opcode** | 102 |
| **Size** | VAR_SHORT |
| **DAT Address** | `0x016fb460` |
| **Sender** | `0x003d68a0` |
| **Category** | Movement |

This is the primary movement packet sent when the player clicks to walk/run in the game world. It is VAR_SHORT sized because it can contain a variable-length path.

**Packet Format:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | pathLength | Number of path waypoints (x4 bytes each) |
| 2 | N*4 | int[] | waypoints | Packed coordinate waypoints |
| varies | 1 | byte | runFlag | 0 = walk, 1 = run (ctrl-click) |

### MOVE_GAME (From Minimenu)
| Field | Description |
|-------|-------------|
| **Opcode** | 33 |
| **Size** | 5 (fixed) |
| **DAT Address** | `0x016fb8b0` |
| **Sender** | `0x001fe300` |
| **Category** | Movement |

Simplified movement packet sent when clicking an action in the minimenu that requires walking to a location. Fixed 5-byte format.

**Packet Format (5 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | destX | Destination X coordinate |
| 2 | 2 | ushort | destY | Destination Y coordinate |
| 4 | 1 | byte | runFlag | 0 = walk, 1 = run |

### MOVE_GAME (Extended Form)
| Field | Description |
|-------|-------------|
| **Opcode** | 92 |
| **Size** | 18 (fixed) |
| **DAT Address** | `0x016fb500` |
| **Sender** | `0x001fe300` (same as opcode 33) |
| **Category** | Movement |

Extended movement packet with additional data, used for specific minimenu interactions that carry extra context (e.g., when clicking on an entity at a location).

**Packet Format (18 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | destX | Destination X coordinate |
| 2 | 2 | ushort | destY | Destination Y coordinate |
| 4 | 1 | byte | runFlag | 0 = walk, 1 = run |
| 5 | 13 | mixed | extendedData | Additional context data (entity/target info) |

---

## Notes

- Three distinct MOVE_GAME variants exist for different contexts:
  - **Opcode 102** (VAR_SHORT): Full pathfinding with variable-length waypoint list
  - **Opcode 33** (5 bytes): Simple single-destination walk from minimenu actions
  - **Opcode 92** (18 bytes): Extended form with entity/target context data
- The `runFlag` field is present in all variants and is set when the player holds Ctrl while clicking
- The sender function at `0x001fe300` handles both opcode 33 and opcode 92 -- it chooses the extended form when additional target data is available
- Movement from the minimap likely uses the same opcode 102 path
