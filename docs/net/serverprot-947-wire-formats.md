# ServerProt Wire Formats — Rev 947-1 (Capture-Verified)

All formats below are verified from live Jagex 947-1 packet captures, NOT from Ghidra decompilation.
Ghidra RE was wrong about multiple byte transforms — the capture is the only reliable source.

## Variable Packets

### VARP_SMALL (opcode 10, 3B fixed)
```
[2B LE] varpId
[1B]    value (raw signed byte, NO transform)
```
Example: varp 180 = -1 → `B4 00 FF`

### VARP_LARGE (opcode 111, 6B fixed)
```
[4B middle-endian] value (writeIntMiddle)
[2B LE]            varpId
```
Example: `00 00 20 00 95 04` → value=0x20000000, id=1173

### VARP_LONG (opcode 170, 10B fixed)
```
[8B BE] value (writeLong)
[2B LE] varpId
```
Example: varp 11920 = -1 → `FF FF FF FF FF FF FF FF 90 2E`

### CLIENT_SETVARC_SMALL (opcode 1, 3B fixed)
```
[1B]    value (writeByteSubtract: wire = 0x80 - value)
[2B LE] varcId
```

### CLIENT_SETVARC_LARGE (opcode 112, 6B fixed)
```
[4B BE] value (writeInt)
[2B LE] varcId
```

### CLIENT_SETVARC_STR (opcode 67, varByte)
```
[str]   value (null-terminated CP1252)
[2B LE] varcId
```
Example: varc 2380 = "Yehp" → `59 65 68 70 00 09 4C`

### UPDATE_STAT (opcode 66, 6B fixed)
```
[4B BE] xp (writeInt)
[1B]    level (writeByteSubtract: wire = 0x80 - level)
[1B]    skillId (writeByteAdd: wire = skillId + 0x80)
```
Example: skill 0, xp=6124708, level=91 → `00 5D 74 A4 25 80`

## Interface Packets

### IF_SETTOPLEVELINTERFACE (opcode 94, 19B fixed)
```
[1B]    unused (0x00)
[1B]    interfaceId high byte
[1B]    interfaceId low byte + 0x80 (writeByteAdd)
[16B]   zeros (read but discarded by handler)
```
Example: interface 906 → `00 03 0A 00×16`

### IF_SETPOSITION (opcode 8, 23B fixed) — used for IF_OPENSUB
```
[4B]    zeros
[4B LE] subInterfaceId (writeIntLittle)
[6B]    zeros
[1B]    0xFF constant
[5B]    zeros
[1B]    parentComponent
[2B BE] parentInterfaceId (writeShort)
```
Example: parent 906:44, sub 907 → `00 00 00 00 8B 03 00 00 00 00 00 00 00 00 FF 00 00 00 00 00 2C 03 8A`

### IF_SETEVENTS (opcode 35, 12B fixed)
```
[4B LE] zeros (padding)
[2B LE] componentId
[2B LE] interfaceId
[2B BE] fromSlot
[2B LE] settings
```
Example: if=907, comp=39, fromSlot=1, settings=2 → `00 00 00 00 27 00 8B 03 00 01 02 00`

## Misc Packets

### NO_TIMEOUT (opcode 216, 0B)
Empty keepalive.

### RESET_CLIENT_VARCACHE (opcode 48, 0B)
Empty, resets all varps/varcs.

### SET_READY_FLAG (opcode 65, 0B)
Empty, signals lobby ready.

### UPDATE_RUNENERGY (opcode 19, 1B fixed)
```
[1B] energy (writeByte)
```

### CHANGE_LOBBY (opcode 30, varShort)
Empty in lobby init.

## Key Rule
**ALL varp/varc ID fields use little-endian shorts.** The Ghidra RE consistently produced wrong
endianness for these fields because the decompiler optimizes out the reads. Never trust Ghidra
for byte ordering — always verify against live captures.
