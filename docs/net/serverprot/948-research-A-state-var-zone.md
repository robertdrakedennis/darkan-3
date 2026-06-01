# 948 ServerProt Research — State / Variable / Zone subsystem (Agent A)

Target: `rs2client.948-2-2` (Ghidra port 8081, name `rs2client`, STRIPPED). Beta
`librs2client.so` (rev ~890) used ONLY for packet-family identity via structural match —
never for wire bytes. rev947 captures in `capture/login-*/` used as 947 ground-truth.

Anchors walked:
- `jag::packethandlers::ClientState::BindHandlers` @ 0x000aa85e (18 var/stat/zone-state handlers)
- `jag::packethandlers::ClientState::BindHandlers_extra` @ 0x000aaf4c (12 rebuild/env handlers)
- `jag::packethandlers::ZoneUpdates::BindHandlers` @ 0x000ae1a8 (21 zone-update handlers)

## VARP / VARC / STAT family

Family identity is DEFINITIVE per the domain-setter each 948 handler calls:
- `PlayerVarDomain::set` (0x4e1a40)            => plain VARP
- `PlayerVarDomain::setBitFromPacket` (0x4e1b40)=> VARP-BIT
- InterfaceManager CreateOrFindUpdateEntry + SetUpdateSlotValue (no XOR) => plain VARC
- `FUN_00af8950` (bit-range XOR into update slot) => VARC-BIT
- InterfaceManager type=2 (+string read FUN_00ad89a0) => VARC-string

Beta cross-confirm: `jag::packethandlers::Variables::Variables(jag::Client&)` lambdas
#2/#3 = SetVarValueFromServer (VARP), #4/#5 = SetVarBitValueFromServer (VARP-BIT),
#6/#7 = CLIENT plain, #8/#9 = CLIENT bit (DelayedStateChange XOR). All wire-byte details
below come ONLY from the 948 disassembly.

| op  | proposed name (948)        | size | handler @     | conf | evidence | wire format (948)                                            | vs 947 |
|-----|----------------------------|------|---------------|------|----------|--------------------------------------------------------------|--------|
| 10  | VARP_BIT_SMALL             | 3    | 0x00119870    | (a)(b)| setBitFromPacket; beta #4 | id(BE u16) + value(1B, read = -128 - byte)         | identity CHANGED (947 op10=plain VarpSmall) |
| 28  | VARP_LARGE                 | 6    | 0x00119910    | (a)(b)| PlayerVarDomain::set; beta #3 | value(BE int) + id(BE u16)                    | CHANGED (947 op111: intMiddle+idLE) |
| 44  | UpdateStat                 | 6    | 0x000ef2a0    | (a)(b)| StatTable::UpdateStat; asm | xp(LE int) + level(raw 1B) + skillId(byteInverse) | CHANGED in ALL 3 fields (947 op66: intBE+byteSub+byteAdd) |
| 47  | CLIENT_SETVARC_SMALL       | 3    | 0x001196f0    | (a)(b)| IfaceMgr direct; beta #6 | value(byteAdd) + id(LE u16)                       | CHANGED (947 op1: byteSubtract value) |
| 48  | CLIENT_SETVARCBIT_SMALL    | 3    | 0x00119560    | (a)(b)| FUN_00af8950; beta #8 | value(1B raw signed) + id(BE u16, lo-byte byteAdd)   | n/a (no 947 capture) |
| 51  | VARP_BIT_LARGE             | 6    | 0x001197b0    | (a)(b)| setBitFromPacket; beta #5 | id(BE u16) + value(4B, wire [B2,B3,B0,B1]=intInverseMiddle) | n/a |
| 64  | CLIENT_SETVARC_LARGE       | 6    | 0x00119600    | (a)(b)| IfaceMgr direct; beta #7 | value(writeIntMiddle [B1,B0,B3,B2]) + id(LE u16 lo-byte byteAdd = shortAddLittle) | CHANGED (947 op112) |
| 69  | CLIENT_SETVARCBIT_LARGE    | 6    | 0x001194b0    | (a)(b)| FUN_00af8950; beta #9 | id(BE u16) + value(BE int)                            | n/a |
| 92  | SET_VARC_STR_SMALL         | -2   | 0x001501c0    | (a)  | self-named handler; IfaceMgr type=2 | id(LE u16) + string(CP1252)               | id-first (matches existing reg) |
| 116 | SET_VARC_STR_LARGE         | -2   | 0x001500a0    | (a)  | IfaceMgr type=2; string-first | string(CP1252) + id(BE u16, lo-byte byteAdd)    | distinct from op92 (string-first) |

### Registered in `Rev948ServerCodecsVariable.kt`
- `VarpLarge` @ op 28 — `writeInt(value); writeShort(id)`
- `UpdateStat` @ op 44 — `writeIntLittle(xp); writeByte(level); writeByteInverse(skillId)` (CHANGED)
- `ClientSetVarcSmall` @ op 47 — `writeByteAdd(value); writeShortLittle(id)`
- `ClientSetVarcLarge` @ op 64 — `writeIntMiddle(value); writeShortAddLittle(id)`
- `ClientSetVarcStr` @ op 92 — `writeShortLittle(id); writeRSString(value)` (unchanged from prior)

### Left UNREGISTERED (blocked — fully reversed, no Kotlin data class exists)
- op 10 VARP_BIT_SMALL, op 51 VARP_BIT_LARGE — need `VarpBitSmall/Large` classes.
- op 48 CLIENT_SETVARCBIT_SMALL, op 69 CLIENT_SETVARCBIT_LARGE — need `ClientSetVarcBitSmall/Large` classes.
- op 116 SET_VARC_STR_LARGE — `ClientSetVarcStr` is already bound to op 92 (id-first); op 116 is
  string-first with a different id transform, so it needs its own class.

The data classes named in the task brief (`ClientSetVarcBitSmall/Large`) do NOT currently exist
in `core/.../prot/ServerProt.kt` — only `VarpSmall/Large/Long`, `ClientSetVarcSmall/Large/Str`.
Adding them is networking-protocol-engineer's call (ServerProt.kt ownership).

### CRASH-BLOCKER residual risk (the heaviest lobby-login packet)
In the 947 captures the dominant lobby-login traffic is the **plain VARP-SMALL** packet
(947 op 10 "VarpSmall", id LE + 1-byte value; 9567 occurrences). In 948, NO plain varp-small
handler exists among ClientState's 18 bindings — the only plain VARP handler bound here is
op 28 (4-byte int). The 1-byte plain-varp handler is bound by a DIFFERENT BindHandlers not in
this subsystem walk. **`VarpSmall` therefore has no 948 encoder yet** and the lobby may still
hit "no encoder registered" / SIGSEGV until that handler is located (other agent's subsystem).

## Zone frame headers (asm-verified — resolves the op 41 reorder)

Globals: `DAT_013942a8` = level, `DAT_013942ac` = zoneX (base608), `DAT_013942b0` = zoneY (base60c).

| op | name                          | handler @  | header wire (asm)                                        |
|----|-------------------------------|------------|----------------------------------------------------------|
| 41 | UPDATE_ZONE_PARTIAL_FOLLOWS   | 0x000ef150 | zoneX(raw byte) + level(byteAdd) + zoneY(raw signed byte) |
| 76 | UPDATE_ZONE_PARTIAL_ENCLOSED  | 0x000eefc0 | level(byteInverse) + zoneY(raw signed) + zoneX(byteSubtract), then sub-op stream |
| 78 | UPDATE_ZONE_FULL_FOLLOWS      | 0x000f9510 | level(byteAdd) + zoneY(byteSubtract) + zoneX(raw signed) |

The op 41 "3-byte reorder" flagged by the prior pass = **level is the MIDDLE byte** (zoneX first,
zoneY last). All three frame encoders in `Rev948ServerCodecsZone.kt` were FIXED to these orders.

Sub-opcode stream (op 76): dispatched via `g_zoneSubProtVector` @ `DAT_015d4580`; sub-op > 0x11
=> PacketError. (The standalone sub-packet opcodes — LOC_*/OBJ_*/MAP_* — were carried over from
the prior pass; their opcodes were corroborated by the ZoneUpdates::BindHandlers walk and left
as-is. Their bodies were not re-diffed in this pass — flag for a follow-up if world-zone traffic
misbehaves.)

## Rebuild (asm-verified)

| op  | name (948)              | handler @   | finding |
|-----|-------------------------|-------------|---------|
| 81  | REBUILD_NORMAL_SIMPLE   | 0x001da8b0  | **MAGIC BYTE 0x7B -> 0x85** (asm `CMP R14B,0x85`). Header also reorganised to 18 bytes (see below). |
| 199 | REBUILD_NORMAL (=REGION)| 0x00120260  | multi-scene grid form (RebuildSceneEntry::Reset + WorldList::InsertOrReplace). = 947 op172 REBUILD_REGION. Opaque mapping OK. |
| 186 | REBUILD_WORLDENTITY     | 0x000efd80  | triple-nested 0xFF-terminated loops, 4B BE int writes. = 947 op188. Opaque mapping OK. |

REBUILD_NORMAL_SIMPLE 948 asm read order (body offset -> field):
`[+0] ignored`, `[+1,+2] coordX LE u16`, `[+3] magic 0x85`, `[+4,+5] coordY BE u16`,
`[+6] level(byteAdd)`, `[+7] ignored`, `[+8,+9] key BE u16`, `[+10..13] coordA BE u32`,
`[+14..17] coordB BE u32` (18 bytes). Only the **magic fix (0x7B->0x85)** was committed to the
encoder; the full field re-mapping is left as a TODO pending a 948 world-login capture (the
existing `RebuildNormalSimple` data class fields don't cleanly map to this header).

## Stub-only metadata corrections for the orchestrator (Rev948ServerProtStubs.kt)
1. op 44 UPDATE_STAT: handler address in stub comment is WRONG. It says `0x001b1830`
   (= `jag::LoginManager::ResetLoginState`). The REAL handler is `jag::game::StatTable::UpdateStat`
   @ `0x000ef2a0`. Size 6 is correct.
2. op 81 REBUILD_NORMAL_SIMPLE: confirm size = VarShort (-2) (handler @ 0x001da8b0).
3. (No size/opcode mismatches found for the var ops 10/28/47/48/51/64/69/92/116 vs the stub
   table; the encoder registrations win via putIfAbsent so no stub edit is required for those.)

## Ghidra symbols renamed (948, this pass)
- 0x00119870 -> jag::packethandlers::ClientState::VARP_BIT_SMALL
- 0x00119910 -> jag::packethandlers::ClientState::VARP_LARGE
- 0x001196f0 -> jag::packethandlers::ClientState::CLIENT_SETVARC_SMALL
- 0x00119560 -> jag::packethandlers::ClientState::CLIENT_SETVARCBIT_SMALL
- 0x001197b0 -> jag::packethandlers::ClientState::VARP_BIT_LARGE
- 0x00119600 -> jag::packethandlers::ClientState::CLIENT_SETVARC_LARGE
- 0x001194b0 -> jag::packethandlers::ClientState::CLIENT_SETVARCBIT_LARGE
- 0x001500a0 -> jag::packethandlers::ClientState::SET_VARC_STR_LARGE
- (decompiler evidence comments added to all of the above + 0x000ef2a0, 0x000ef150)
