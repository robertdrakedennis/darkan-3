# IF_SETEVENTS / IF_SETEVENTS2 — ServerActiveProperties

**Verified against:** rs2client (rev 946, stripped) cross-referenced with librs2client.so (rev ~890, symbols)

## Overview

IF_SETEVENTS and IF_SETEVENTS2 set per-slot interaction properties (`ServerActiveProperties`) on interface components. These control which right-click options appear, whether use-on targeting is allowed, drag behavior, and 3D view target actions.

The client stores these as a nested hash map:
- **Outer key:** `(interfaceId << 16) | componentId` (component hash)
- **Inner key:** `slotId` (child slot index, or -1 for the component itself)
- **Value:** `{settings: uint32, cursor: int32}`

When no entry exists, falls back to the component's static defaults at `component+0x118` (settings) and `component+0x11c` (cursor), or the global default `{0, -1}`.

## Packet Formats

### IF_SETEVENTS (settings only)

Sets the `settings` bitfield. The `cursor` field defaults to -1.

| Offset | Size | Type | Field | Notes |
|--------|------|------|-------|-------|
| 0 | 4 | g4s_alt2 | settings | 32-bit settings bitfield |
| 4 | 2 | g2le | endSlot | End slot index (inclusive), 0xFFFF = -1 |
| 6 | 4 | g4 | componentHash | (interfaceId << 16) \| componentId |
| 10 | 2 | g2a | startSlot | Start slot index, 0xFFFF = -1 |

Total: 12 bytes (fixed size)

### IF_SETEVENTS2 (cursor only)

Sets the `cursor` field (a param type ID). The `settings` field defaults to 0.

| Offset | Size | Type | Field | Notes |
|--------|------|------|-------|-------|
| 0 | 2 | g2a | startSlot | Start slot index, 0xFFFF = -1 |
| 2 | 2 | g2 | endSlot | End slot index (inclusive), 0xFFFF = -1 |
| 4 | 4 | g4 | componentHash | (interfaceId << 16) \| componentId |
| 8 | 2 | g2a | cursor | Cursor param type ID, 0xFFFF = -1 (no custom cursor) |

Total: 10 bytes (fixed size)

## Settings Bitfield Layout (32-bit)

| Bit(s) | Mask | Name | Verified | Description |
|--------|------|------|----------|-------------|
| 0 | `0x00000001` | continueButton | [VERIFIED] | Enables "continue" / click-through action on the component. Checked in `LogicComponentList` and `BuildComponentMiniMenu`. |
| 1-10 | `0x000007FE` | rightClickOptions | [VERIFIED] | Each bit enables one right-click menu option (bit 1 = op 1, bit 10 = op 10). Checked via `(*settings >> (optionIndex+1) & 1)` in `IfButtonXInner`, `GetSlotOpText`, `GetSlotOpCursor`, and `LogicComponentList`. |
| 11-17 | `0x0003F800` | useOnTargetMask | [VERIFIED] | 7-bit mask controlling which entity types this component can target with use-on. Extracted via `(*settings >> 11) & 0x7f`. Read by `EnterTargetMode`, `cc_if_gettargetmask`, `GetComponentTargetVerb`. |
| 18-20 | `0x001C0000` | depth | [VERIFIED] | 3-bit value (0-7). Number of parent interface layers to traverse up when determining the drag target layer. Extracted via `(*settings >> 18) & 7`. Checked in `GetServerDragLayer` and `DragTryPickup`. |
| 21 | `0x00200000` | dragEnabled | [VERIFIED] | Enables drag interaction on the component. Checked in `LogicComponentList` (`*settings & 0x200000`). When set, the component can be picked up for dragging. |
| 22 | `0x00400000` | useTargetable | [VERIFIED] | Component can be a valid target for use-on operations. Checked in `LogicComponentList` and `BuildComponentMiniMenu` (`*settings & 0x400000`), always in conjunction with the "component" target flag (bit 16, mask 0x20 of the target mask at field 0x22c/0x214). |
| 23 | `0x00800000` | ignoreDepth | [VERIFIED] | Overrides the depth field. When set, drag operations skip layer traversal and drop to the component's own layer. Checked in `GetServerDragLayer`, `DragTryPickup`, and `DrawComponentList` (`*settings & 0x800000`). |
| 24 | `0x01000000` | allowTargetSend | [VERIFIED] | Enables sending target action operations (3D view target ops like DoTargetPlayer/DoOpPlayer) to the server. Checked in `SendIfButtonTargetMenu` (`*settings & 0x1000000`) at multiple code paths for various interaction types (click, drag-release, slider). |
| 25-31 | `0xFE000000` | unused | [VERIFIED] | No code in rs2client reads these bits. |

### Use-On Target Mask Detail (bits 11-17)

| Bit | Value in Mask | Target Type |
|-----|---------------|-------------|
| 11 | 0x01 | Ground item |
| 12 | 0x02 | NPC |
| 13 | 0x04 | Loc (scenery object) |
| 14 | 0x08 | Player |
| 15 | 0x10 | Self (own player) |
| 16 | 0x20 | Component (interface slot) |
| 17 | 0x40 | Tile (map square) |

## Cursor Field (Second Int)

The cursor field is set by IF_SETEVENTS2 (or by CS2 opcode `cc_if_settargetcursors`). It is a **param type ID** used to look up cursor configuration from `Js5ConfigGroup::enumArray`. A value of -1 means no custom cursor.

Read by:
- `EnterTargetMode`: stored at `InterfaceManager+0x230`, used for target-mode cursor display
- `BuildComponentMiniMenu`: looked up via `ConfigProvider::GetParamType` to get cursor info for target entries

## Key Functions (rs2client addresses)

| Address | Name | Reads |
|---------|------|-------|
| `0x00224c60` | `IF_SETEVENTS` | Packet handler (settings mode) |
| `0x00224b70` | `IF_SETEVENTS2` | Packet handler (cursor mode) |
| `0x003a1050` | `SetServerActiveProperties` | Hash map store function |
| `0x00399210` | `GetServerActiveProperties` | Hash map lookup, returns `&{settings, cursor}` |
| `0x00399340` | `cc_if_gettargetmask` | CS2 opcode: returns `(settings >> 11) & 0x7f` |
| `0x0039956c` | `IfButtonXInner` | Checks option bits 0-10 |
| `0x0029ebf7` | `EnterTargetMode` | Extracts target mask + cursor |
| `0x0039abcc` | `DragTryPickup` | Checks depth (18-20) + ignoreDepth (23) |
| `0x0039a6b1` | `SendIfButtonTargetMenu` | Checks allowTargetSend (24) |
| `0x0024ed50` | `BuildComponentMiniMenu` | Checks useTargetable (22) + continue (0) |
| `0x0029e9f0` | `GetComponentTargetVerb` | Checks target mask nonzero (`& 0x3f800`) |
| `0x0039a9b4` | `GetServerDragLayer` | Checks ignoreDepth (23) + depth (18-20) |
| `0x0021fc20` | `GetSlotOpText` | Checks option bit via `(settings >> (op+1)) & 1` |
| `0x003a1870` | `GetSlotOpCursor` | Checks option bit via `(settings >> (op+1)) & 1` |

## Key Functions (librs2client.so addresses, with Jagex symbols)

| Address | Jagex Name |
|---------|------------|
| `0x001d33c0` | `jag::InterfaceManager::GetServerActiveProperties` |
| `0x0026ea40` | `jag::MakeSharedPool<jag::game::ServerActiveProperties,98304>::MakeShared<int&,int&>` |
| `0x005662a0` | `jag::InterfaceManager::IfButtonX` |
| `0x00416a70` | `jag::InterfaceManager::IfButtonXSend` |
| `0x00493b70` | `jag::InterfaceManager::EnterTargetMode` |
| `0x005b4790` | `jag::InterfaceManager::EndTargetMode` |
| `0x003aefb0` | `jag::InterfaceManager::GetComponentTargetVerb` |
| `0x005adde0` | `jag::InterfaceManager::DragTryPickup` |
| `0x005adb70` | `jag::InterfaceManager::GetServerDragLayer` |
| `0x005eee40` | `jag::InterfaceManager::LogicComponentList` |
| `0x0020bc30` | `jag::InterfaceManager::DrawComponentList` |
| `0x003b1bf0` | `jag::InterfaceManager::CollectInputData` |
| `0x001d3460` | `jag::opcode::InterfaceComponents::cc_if_gettargetmask` |
| `0x005313c0` | `jag::opcode::InterfaceComponents::cc_if_setclickmask` |
| `0x001ad200` | `jag::opcode::InterfaceComponents::cc_if_settargetcursors` |

## Example Values

- `0x00000002` = bit 1 only = right-click option 1 enabled
- `0x01000002` = bit 1 + bit 24 = option 1 + allow 3D target send
- `0x000007FF` = bits 0-10 = continue + all 10 right-click options
- `0x0003F800` = bits 11-17 = all use-on targets enabled
- `0x001C0000` = bits 18-20 = depth=7 (max)
- `0x00E00000` = bits 21-23 = drag + useTargetable + ignoreDepth
