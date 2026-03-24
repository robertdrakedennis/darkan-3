# ClientProt: Action Packets

> **Rev 947-1**: Opcodes and many sizes changed significantly from 946. See `clientprot-table.md` for the 947-1 table.

Entity interaction packets sent when the player interacts with game objects (locations), NPCs, other players, or ground items.

## OPLOC (Location/Object Actions)

Location actions are dispatched by the minimenu system via `DoOpLoc` functions.

### OPLOC1 (Walk Here / Primary Action)
| Field | Description |
|-------|-------------|
| **Opcode** | 70 |
| **Size** | 4 (fixed) |
| **DAT Address** | `0x016fb660` |
| **Sender** | `0x001da9e0` (DoOpLoc, op=1) |

**Packet Format:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 4 | int | packedCoordAndId | Packed location coord + type ID |

### OPLOC_T (Use Item on Location)
| Field | Description |
|-------|-------------|
| **Opcode** | 27 |
| **Size** | 12 (fixed) |
| **DAT Address** | `0x016fb910` |
| **Sender** | `0x001fe0a0` |

**Packet Format:**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 12 | mixed | targetData | Location target + item source info |

### OPLOC_T (Long Form)
| Field | Description |
|-------|-------------|
| **Opcode** | 16 |
| **Size** | 11 (fixed) |
| **DAT Address** | `0x016fb9c0` |
| **Sender** | `0x001feca0` |

### OPLOC_T (Extended Form)
| Field | Description |
|-------|-------------|
| **Opcode** | 37 |
| **Size** | 15 (fixed) |
| **DAT Address** | `0x016fb870` |
| **Sender** | `0x001fe840` |

### OPLOC_CS2 (Script-Triggered Location Action)
| Field | Description |
|-------|-------------|
| **Opcode** | 94 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb4e0` |
| **Sender** | `0x00361910` (CS2 opcode handler) |

---

## OPNPC (NPC Actions)

NPC actions are dispatched via a switch statement in `0x003d4d70` that selects from 6 different ClientProt objects based on the action op (1-6).

### OPNPC1 (Primary/Attack)
| Field | Description |
|-------|-------------|
| **Opcode** | 26 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb920` |
| **Sender** | via OPNPC dispatch switch |

### OPNPC2
| Field | Description |
|-------|-------------|
| **Opcode** | 25 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb930` |
| **Sender** | via OPNPC dispatch switch |

### OPNPC3
| Field | Description |
|-------|-------------|
| **Opcode** | 23 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb950` |
| **Sender** | via OPNPC dispatch switch |

### OPNPC4
| Field | Description |
|-------|-------------|
| **Opcode** | 90 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb520` |
| **Sender** | via OPNPC dispatch switch |

### OPNPC5
| Field | Description |
|-------|-------------|
| **Opcode** | 77 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb5f0` |
| **Sender** | via OPNPC dispatch switch |

### OPNPC6
| Field | Description |
|-------|-------------|
| **Opcode** | 103 |
| **Size** | 7 (fixed) |
| **DAT Address** | `0x016fb450` |
| **Sender** | via OPNPC dispatch switch |

**Common OPNPC Packet Format (all 7 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 2 | ushort | npcServerIndex | Server index of the target NPC |
| 2 | 1 | byte | ctrlRun | 1 if ctrl-click (run), 0 otherwise |
| 3 | 4 | int | packedCoord | Packed coordinate data |

### OPNPC_CS2 (Script-Triggered NPC Action)
Handled by `0x003f4810` at opcode 104 (DAT `0x016fb440`, VAR_BYTE). The CS2 script provides the NPC target and action data from the script string stack.

---

## OPOBJ (Ground Item Actions)

Ground item actions are dispatched via a switch statement in `0x0037c940` that selects from 10 different ClientProt objects based on the action op (1-10).

### OPOBJ1-10 Opcodes
| Op | Opcode | Size | DAT Address |
|----|--------|------|-------------|
| 1 | 20 | 3 | `0x016fb980` |
| 2 | 46 | 3 | `0x016fb7e0` |
| 3 | 115 | 3 | `0x016fb390` |
| 4 | 96 | 3 | `0x016fb4c0` |
| 5 | 6 | 3 | `0x016fba60` |
| 6 | 60 | 3 | `0x016fb700` |
| 7 | 14 | 3 | `0x016fb9e0` |
| 8 | 59 | 3 | `0x016fb710` |
| 9 | 91 | 3 | `0x016fb510` |
| 10 | 30 | 3 | `0x016fb8e0` |

**Common OPOBJ Packet Format (3 bytes):**
| Offset | Size | Type | Field | Description |
|--------|------|------|-------|-------------|
| 0 | 1 | byte | runFlag | Bit 15 = ctrl-run, combined with delta bytes |
| 1 | 2 | ushort | objIdAndCoord | Object ID and location data |

### OPOBJ_T (Use Item on Ground Object)
| Field | Description |
|-------|-------------|
| **Opcode** | 105 |
| **Size** | 11 (fixed) |
| **DAT Address** | `0x016fb430` |
| **Sender** | `0x001fe630` |

### OPOBJ_CS2 (Script-Triggered Object Action)
| Field | Description |
|-------|-------------|
| **Opcode** | 98 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb4a0` |
| **Sender** | `0x00360750` (CS2 opcode handler) |

---

## OPPLAYER (Player Actions)

### OPPLAYER_T (Use Item on Player)
| Field | Description |
|-------|-------------|
| **Opcode** | 120 |
| **Size** | 11 (fixed) |
| **DAT Address** | `0x016f3660` |
| **Sender** | `0x00bb2b50` |

### OPPLAYER_T (Extended)
| Field | Description |
|-------|-------------|
| **Opcode** | 58 |
| **Size** | 17 (fixed) |
| **DAT Address** | `0x016fb720` |
| **Sender** | `0x001feff0` |

### OPPLAYER_CS2 (Script-Triggered Player Action)
| Field | Description |
|-------|-------------|
| **Opcode** | 119 |
| **Size** | VAR_BYTE |
| **DAT Address** | `0x016fb350` |
| **Sender** | `0x00360900` (CS2 opcode handler) |

---

## Notes

- All action packets are triggered by the minimenu (right-click menu) system through `DoActionEntry` (`0x001cb920`)
- The `_T` suffix indicates "targeted" variants where an item is being used ON an entity
- The `_CS2` suffix indicates the action was triggered by a ClientScript (CS2) rather than direct player interaction
- OPNPC and OPOBJ use dispatch switches to select the correct opcode based on the action operation number (1-N)
- The `ctrlRun` / `runFlag` field indicates whether the player held Ctrl to force-run to the target
