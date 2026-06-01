# 948 ServerProt RE — Subsystem B: Interface / Chat / Clans / Social / SiteSettings

Binary: `rs2client.948-2-2` (Ghidra port 8081), STRIPPED. Authority for all assertions below.
Reference: `librs2client.so` (rev ~890) used ONLY for structural sig-match. NEVER for wire format.

Method (evidence-first):
1. `jag::ServerProt::RegisterAll @ 0x000c4700` — the authoritative opcode/size table. Each
   `InitEntry(&entry, opcode, size)` call fixes (entry-struct-base, opcode, size). Size convention:
   `-1 = VarByte`, `-2 = VarShort`, `>=0 = fixed N bytes`, `0 = empty payload`.
2. `jag::packethandlers::Interfaces::BindHandlers @ 0x000ab888` — binds each IF_* handler. For each
   handler it writes `handlerTarget` at `entryBase+0x20` and `invokePtr` (the handler fn) at
   `entryBase+0x28`. So: `entryBase = invokePtrGlobal - 0x28`. Cross-referencing the entry base back
   into RegisterAll yields the TRUE opcode + size for each handler.
3. Each handler decompiled/disassembled to extract the exact `gT*` read sequence (field order +
   transform). Compared against the capture-confirmed rev947 codec
   (`core/.../revision/rev947/Rev947ServerCodecsInterface.kt`).

## Byte-order decode key (from disassembly shift patterns)

For a 4-byte field at bytes b0..b3, the value the client computes maps to a JagExtensions writer:

| Client value expression                                  | Reader     | Server writer            |
|----------------------------------------------------------|------------|--------------------------|
| `b3<<24 \| b2<<16 \| b1<<8 \| b0`                         | g4_alt1    | `writeIntLittle`         |
| `b2<<24 \| b3<<16 \| b0<<8 \| b1`                         | g4_alt2    | `writeIntMiddle`         |
| `b1<<24 \| b0<<16 \| b3<<8 \| b2`                         | g4_alt3    | `writeIntInverseMiddle`  |
| BSWAP of native (host-LE swap)                            | g4 (BE)    | `writeInt`               |

For a 2-byte field:
| Client value expression                | Reader   | Server writer                       |
|----------------------------------------|----------|-------------------------------------|
| `b1<<8 \| b0`                          | LE u16   | `writeShortLittle`                  |
| `b0<<8 \| b1` (ROL/bswap native)       | BE u16   | `writeShort`                        |
| `b0<<8 \| (b1+0x80)`                   | g2_alt2  | `writeByte(hi)`+`writeByteAdd(lo)`  |
| `b0<<8 \| (b1-0x80)`/`(0x80-b1)`       | g2_alt2_neg | `writeByte(hi)`+`writeByteSubtract(lo)` |

1-byte transforms: raw=`writeByte`; `(b+0x80)`=`writeByteAdd`; `(0x80-b)`/`-(b)-0x80`=`writeByteSubtract`.

---

## CRITICAL FINDING: 948 IF_* wire formats DIVERGE from 947-3

The 948 IF_* OPCODES and SIZES match the existing `Rev948ServerCodecsInterface.kt` EXACTLY (all 38
verified against RegisterAll — see table). **But the per-field WIRE FORMATS (byte order, transforms,
field order) were widely changed in 948 and the previous codec pass copied 947-3 layouts verbatim.**
The "delta-doc presumed-equivalent" assumption is FALSE. Every handler was re-derived from the 948
binary below.

## IF_* opcode/size verification table (100% evidence)

All entries: opcode + size read from `RegisterAll` via `entryBase = invokePtr - 0x28`. CONFIDENCE
column for the *opcode/size* is HIGH(a/b) for all — anchored to BindHandlers binder position +
RegisterAll InitEntry. WIRE column states whether the codec's byte layout matches the 948 handler.

| Opcode | Name | Size | Handler @ | Entry base | Wire vs 947-codec | Status |
|--------|------|------|-----------|-----------|-------------------|--------|
| 39  | IF_OPENTOP            | 6  | 0x00194050 | 0x015c0c40 | topId g4_alt1; subId **BE u16** | topId OK; subId codec writes LE → see note |
| 3   | IF_SETTOPLEVELINTERFACE | 19 | 0x00186900 | 0x015c0c00 | id at **offset 9-10 g2_alt2**, not 1-2 | CHANGED |
| 94  | IF_OPENSUB           | 8  | 0x00194100 | 0x015c0c80 | subId **LE i16**; walkable **LE i16**; parentHash **BE u32** | CHANGED |
| 82  | IF_SETPOSITION       | 23 | 0x00189180 | 0x015c0bc0 | complex; id@off13 BE u16; layer byteSubtract | CHANGED (zeros OK) |
| 70  | IF_SETPLAYERMODEL_OTHER | 25 | 0x00186710 | 0x015c0b80 | structured (see §PlayerModel) | document |
| 60  | IF_SETPLAYERMODEL_SELF | 25 | 0x00186520 | 0x015c0b40 | structured | document |
| 4   | IF_SETANGLE          | 32 | 0x001da480 | 0x015c0b00 | complex packed-coord | CHANGED (zeros OK) |
| 118 | IF_SETPLAYERMODEL_SNAPSHOT | 29 | 0x001da230 | 0x015c0ac0 | structured | document |
| 62  | IF_CLOSESUB_ACTIVE   | 4  | 0x00186360 | 0x015c0a80 | componentHash **g4_alt1** | OK |
| 40  | IF_SUBSWAP           | 8  | 0x00186100 | 0x015c0a40 | A **g4_alt2**; B **g4_alt1** | CHANGED (B was alt2) |
| 35  | IF_SETEVENTS2        | 12 | 0x00186040 | 0x015c0a00 | settings g4_alt2; fromSlot LE i16; toSlot LE i16; compHash **BE u32** | CHANGED |
| 97  | IF_SETEVENTS         | 10 | 0x00185f60 | 0x015c09c0 | compHash g4_alt3; +3 shorts (see §Events) | CHANGED |
| 122 | IF_SETTEXT           | VarShort | 0x00185ec0 | 0x015c0980 | **string FIRST**, then compHash **g4_alt3** | CHANGED |
| 91  | IF_SETHIDE           | 5  | 0x00193fc0 | 0x015c0940 | flag(0x81) byte; compHash **g4_alt2** | CHANGED (was LE) |
| 30  | IF_SET2DANGLE        | 8  | 0x00193f10 | 0x015c0900 | compHash **g4_alt3** FIRST; angle g4_alt3 | CHANGED |
| 38  | IF_SET_MODEL_FRAME   | 8  | 0x00185b70 | 0x015c08c0 | frame **g4_alt3**; compHash g4_alt3 | CHANGED (frame was alt2) |
| 84  | IF_SETOBJECT         | 10 | 0x00185a00 | 0x015c0880 | compHash **g4_alt1** FIRST; count g4_alt3; slot **LE u16** | CHANGED |
| 101 | IF_SETOBJECT_ACTIVE  | 4  | 0x00185b20 | 0x015c0840 | compHash **g4_alt2** | CHANGED (was alt3) |
| 180 | IF_SETOBJECT_SMALL   | 5  | 0x00185980 | 0x015c0800 | compHash **BE u32**; idx byte (0x80-b) → small marker | CHANGED |
| 102 | IF_SETMODEL          | 8  | 0x001858e0 | 0x015c07c0 | modelId **g4_alt2**; compHash **BE u32** | CHANGED |
| 103 | IF_SETGRAPHIC        | 8  | 0x00193e60 | 0x015c0780 | graphicId g4_alt3; compHash g4_alt3 | OK |
| 32  | IF_SETCOLOUR         | 8  | 0x00185850 | 0x015c0740 | colour **g4_alt2**; compHash **g4_alt3** | CHANGED (swapped) |
| 96  | IF_SETANIM_ACTIVE    | 4  | 0x00185800 | 0x015c0700 | compHash g4_alt3 | OK |
| 86  | IF_SETANIM           | 10 | 0x00185740 | 0x015c06c0 | compHash **g4_alt1** FIRST; frame **BE u16**; anim **BE u32** | CHANGED |
| 115 | IF_SETNPCHEAD        | 10 | 0x00185660 | 0x015c0680 | scale BE u16; compHash g4_alt1; partA g2_alt2; partB BE u16 | CHANGED (see §NpcHead) |
| 136 | IF_SETANIM_SMALL     | 5  | 0x00185aa0 | 0x015c0640 | compHash **g4_alt2**; idx (0x80-b) | CHANGED |
| 59  | IF_SETNPCMODEL       | 10 | 0x00193c50 | 0x015c0600 | compHash g4_alt3; npcId **LE u16**; modelHash g4_alt3 | CHANGED (see §NpcModel) |
| 165 | IF_SETMODEL_COORD    | 14 | 0x001939d0 | 0x015c05c0 | compHash **BE u32**; p1 g4_alt3; p2 g4_alt3; npcId g2_alt2 | CHANGED |
| 14  | IF_SETSPRITE         | 8  | 0x00193940 | 0x015c0580 | sprite **BE u32** FIRST; compHash **g4_alt3** | CHANGED |
| 99  | IF_SETRECOL          | 6  | 0x00193860 | 0x015c0540 | rgb555 **g2_alt2**; compHash **g4_alt1** | CHANGED |
| 179 | IF_SETSCROLLPOS      | 9  | 0x00193760 | 0x015c0500 | see §Scroll | CHANGED |
| 158 | IF_SETSCROLLSIZE     | 9  | 0x00193660 | 0x015c04c0 | see §Scroll | CHANGED |
| 206 | IF_SETNPCHEAD_ACTIVE | 5  | 0x001935c0 | 0x015c0480 | compHash **BE u32** FIRST; flag(==1) byte | CHANGED |
| 8   | IF_SETPLAYERHEAD_ACTIVE | 5 | 0x00193530 | 0x015c0440 | flag(==1) byte; compHash **g4_alt3** | CHANGED (was LE) |
| 123 | IF_TRIGGER_CLOSE     | 0  | 0x00185620 | 0x015c0400 | empty | OK |
| 68  | IF_SETMODELORIGIN    | 10 | 0x00193420 | 0x015c03c0 | compHash g4_alt3; then x/y/z g2_alt2-family (see §Origin) | CHANGED |
| 148 | IF_CLOSESUB_BY_ID    | 2  | 0x00193a... 0x001855d0 | 0x015c0380 | id **BE u16** | CHANGED (was LE) |
| 152 | IF_SET_HTTP_IMAGE    | VarByte | 0x001ba9f0 | 0x013a0960 | gStr url | OK (bound by separate BindHandlers @0x000779xx) |

### §Detailed field layouts (cumulative byte offsets)

**IF_OPENTOP (op 39, 6B):** `[0..3] g4_alt1 topLevelId`, `[4..5] BE u16 subId` (MOVZX word + ROL8).
Note: codec writes subId LE. Login works because top-level subId for full-screen root is typically 0
(byte order irrelevant). For non-zero subId emit `writeShort(subId)` (BE).

**IF_OPENSUB (op 94, 8B):** `[0..1] LE i16 subId` (no transform), `[2..3] LE i16 walkable`
(no transform), `[4..7] BE u32 parentHash`. Disasm: MOVSX of (b1<<8|b0); MOVSX of (b3<<8|b2);
BSWAP dword[4]. Codec currently g2_alt2/g2_alt2/LE — all 3 wrong.

**IF_SETTOPLEVELINTERFACE (op 3, 19B):** read order: `gT_unsigned_int [0..3] DISCARD`; `skip 1 [4]`;
`g4_alt2 [5..8] DISCARD`; `[9..10] g2_alt2 interfaceId` (`b9<<8 | (b10+0x80)`); `g4_alt1 [11..14]
DISCARD`; `g4_alt1 [15..18] DISCARD`. So id lives at offsets 9(hi)/10(lo+0x80). Codec places it at
1-2 → wrong. Fix: `skip(9); writeByte(id>>8); writeByteAdd(id&0xFF); skip(8)`.

**IF_SETEVENTS2 (op 35, 12B):** `g4_alt2 settings [0..3]`; `LE i16 fromSlot [4..5]` (0xffff→-1);
`LE i16 toSlot [6..7]` (0xffff→-1); `BE u32 componentHash [8..11]`. Mapped to
SetServerActiveProperties(this, compHash, fromSlot, toSlot, settings, 0xffffffff, 0).

**IF_SETEVENTS (op 97, 10B):** `g4_alt3 componentHash [0..3]`; `[4..5] g2_alt2 X`
(`b4<<8|(b5+0x80)`); `[6..7] LE u16 fromSlot` (`b7<<8|b6`); `[8..9] g2_alt2 Y` (`b9<<8|(b8+0x80)`).
SetServerActiveProperties(this, compHash, fromSlot=field@6-7, toSlot=field@8-9, settings=0,
slotRange=field@4-5, 1). Field semantics ambiguous between the two g2_alt2 fields; FLAGGED — encode
must match 947 IfSetEvents1 data-class field meaning; do NOT guess.

**IF_SETHIDE (op 91, 5B):** `[0] hide byte` (client: `==0x81` → hidden); `g4_alt2 componentHash
[1..4]`. Codec wrote componentHash LE → wrong, must be `writeIntMiddle`.

**IF_SETOBJECT (op 84, 10B):** `g4_alt1 componentHash [0..3]`; `g4_alt3 objectCount [4..7]`;
`LE u16 objectSlot [8..9]`.

**IF_SETANIM (op 86, 10B):** `g4_alt1 componentHash [0..3]`; `BE u16 frame [4..5]`;
`BE u32 animId [6..9]`.

**IF_SETMODEL (op 102, 8B):** `g4_alt2 modelId [0..3]`; `BE u32 componentHash [4..7]`. kind=1.

**IF_SETCOLOUR (op 32, 8B):** `g4_alt2 colour24 [0..3]`; `g4_alt3 componentHash [4..7]`. kind=2.
(The previous codec had the two transforms swapped.)

**IF_SETOBJECT_ACTIVE (op 101, 4B):** `g4_alt2 componentHash`. kind=5.

**IF_SETOBJECT_SMALL (op 180, 5B):** `BE u32 componentHash [0..3]`; `[4] byte` decoded as
`-2 - (0x80 - b)` → server emits `writeByteSubtract(idx)` where idx maps to the small/idle marker.

**IF_SETANIM_SMALL (op 136, 5B):** `g4_alt2 componentHash [0..3]`; `[4] byte` decoded as
`-2 - (0x80 - b)` → `writeByteSubtract`.

**IF_SET2DANGLE (op 30, 8B):** `g4_alt3 componentHash [0..3]`; `g4_alt3 angle [4..7]`.

**IF_SET_MODEL_FRAME (op 38, 8B):** `g4_alt3 frame [0..3]`; `g4_alt3 componentHash [4..7]`.

**IF_SETGRAPHIC (op 103, 8B):** `g4_alt3 graphicId [0..3]`; `g4_alt3 componentHash [4..7]`.
(Codec already correct.)

**IF_SETSPRITE (op 14, 8B):** `BE u32 spriteValue [0..3]`; `g4_alt3 componentHash [4..7]`.

**IF_SETRECOL (op 99, 6B):** `g2_alt2 rgb555 [0..1]` (`b0<<8|(b1+0x80)`); `g4_alt1 componentHash
[2..5]`. The client expands rgb555 → rgb888.

**IF_SUBSWAP (op 40, 8B):** `g4_alt2 componentA(uVar5) [0..3]`; `g4_alt1 componentB [4..7]`
(`b5<<8|b7<<24|b6<<16|b4` = LE).

**IF_SETNPCHEAD_ACTIVE (op 206, 5B):** `BE u32 componentHash [0..3]`; `[4] flag` (`==1`). kind=0x14.

**IF_SETPLAYERHEAD_ACTIVE (op 8, 5B):** `[0] flag` (`==1`); `g4_alt3 componentHash [1..4]`. kind=0x15.

**IF_CLOSESUB_BY_ID (op 148, 2B):** `BE u16 id`.

**§Origin — IF_SETMODELORIGIN (op 68, 10B):** `[0..1] x-part g2_alt2_neg` (`b0`-pair, low byte
`b0-0x80`), `[2..3] g2_alt2 y-part`, `g4_alt3 componentHash [4..7]`, `[8..9] z g2_alt2` (low byte
`+0x80`). Exact: reads 2B(b0,b1)→stored as third slot `b1<<8|(b0-0x80)`; 2B(b2,b3)→second slot
`b3<<8|b2`; g4_alt3 compHash; 2B(b8,b9)→first slot `b9<<8|(b8+0x80)`. kind=8, slots +0x20/+0x40/+0x60.
FLAGGED — multi-part, verify field meaning before encoding.

**§Scroll — IF_SETSCROLLPOS (op 179, 9B):** `[0..1] field` (`b1<<8|b0`, LE), `[2] byte`
(`0x80-b2` → byteSubtract), `g4_alt3 componentHash [3..6]`... actual: 2B(b0,b1); 1B(b2→0x80-b2);
2B(b3,b4) → but read jumps: `lVar5+5` after the byte then g4_alt3. Layout: 2B, 1B, g4_alt3(4B), 2B.
slots filled: `bVar8=0x80-b2`; `b3<<8|b4`(LE-ish); `b0<<8|(b1-0x80)`. FLAGGED multi-part.

**§Scroll — IF_SETSCROLLSIZE (op 158, 9B):** `[0..1] g2_alt2` (`b1<<8|(b0+0x80)`), `[2] byte`
(`b2+0x80` → byteAdd), `[3..4] BE u16`, `g4_alt3 [5..8]`. slots: bVar7=(b2+0x80); BE u16; g2_alt2.
FLAGGED multi-part.

**§NpcHead — IF_SETNPCHEAD (op 115, 10B):** `[0..1] BE u16 scale`; `[2..3] g... partB-lo`;
`[4..5] partB-hi`; compHash = `b7<<8|b9<<24|b8<<16|b6` (g4_alt3-on-bytes6..9). Multi-field packed;
FLAGGED.

**§NpcModel — IF_SETNPCMODEL (op 59, 10B):** `g4_alt3 modelHashA [0..3]`; `[4..5] LE u16 npcId`
(`b5<<8|b4`, 0xffff→-1); `g4_alt3 componentHash [6..9]`. Then client derives head/anim slots.

**§PlayerModel — IF_SETPLAYERMODEL_OTHER (op 70, 25B):** `[0..1] componentId` (`b0<<8|(b1-0x80)`,
g2_alt2_neg) — NOTE read at +0..1 but used near end; `gT_unsigned_int DISCARD [2..5]`;
`BE u32 modelId [6..9]`; `gT_unsigned_int DISCARD [10..13]`; `g4_alt2 DISCARD [14..17]`;
`gT_unsigned_int DISCARD [18..21]`; `[22] age/zoom byte` (`-b-0x80` → byteSubtract);
`skip 2 [23..24]`. The world layer must build this 25B blob; current codec `writeFully(payload)` is
acceptable only if the producer emits this exact layout. DOCUMENTED — recommend a structured encoder.
IF_SETPLAYERMODEL_SELF (op 60, 25B) and _SNAPSHOT (op 118, 29B) follow the same alloc-then-dispatch
pattern (not yet byte-detailed; both currently opaque-payload in codec).

**IF_SETPOSITION (op 82, 23B):** `[0] byte` (`-b-0x80` → byteSubtract, the "layer"); `gT_uint
DISCARD [1..4]`; `g4_alt3 DISCARD-ish [5..8]` (stored at entry+0x30); `gT_uint DISCARD [9..12]`;
`g4_alt2 DISCARD [13..16]`; `gT_uint DISCARD [17..20]`; `[21..22] BE u16 componentId`. The codec
writes all-zeros for discarded fields which is fine, but componentId must be BE u16 at offset 21,
and layer byte (byteSubtract) at offset 0. Current codec layout differs. CHANGED.

---

## Chat / Clans / Social / SiteSettings

These are NOT bound by a dedicated "Chat"/"Clans" BindHandlers. They are all bound by the single
large `jag::ServerProt::BindHandlers @ 0x0007509a` (the catch-all binder, same one that binds
IF_SET_HTTP_IMAGE). Opcodes/sizes below are 100% verified via RegisterAll (entryBase = invokePtr-0x28).

| Opcode | Name | Size | Handler @ | Entry base | Wire vs codec |
|--------|------|------|-----------|-----------|---------------|
| 37  | Chat::MESSAGE_FRIENDCHANNEL          | VarByte  | 0x0019e990/0x0019f5d0 | 0x013a1820 | n/a (not in codec) |
| 93  | Chat::MESSAGE_GAME                   | VarByte  | 0x00198220 | 0x013a11e0 | OK (gSmart type; BE int effectFlags; byte flags; opt sender str; message str) |
| 11  | Chat::MESSAGE_PUBLIC                 | VarShort | 0x00197c40 | 0x013a1ba0 | n/a |
| 126 | Chat::MESSAGE_FRIENDCHAT             | VarByte  | 0x0019fc50 | 0x013a0da0 | n/a |
| 2   | Chat::MESSAGE_QUICKCHAT_CLANCHAT     | VarByte  | 0x001a2160 | 0x013a1c60 | n/a |
| 105 | Chat::MESSAGE_CLANCHANNEL           | VarByte  | 0x001a1cc0 | 0x013a10a0 | **WRONG** (see below) |
| 0   | Chat::MESSAGE_QUICKCHAT_CLANCHANNEL  | VarByte  | 0x001a1530 | 0x013a1ce0 | n/a |
| 56  | Chat::MESSAGE_QUICKCHAT_PRIVATE      | VarByte  | 0x001a08e0 | 0x013a1620 | n/a |
| 175 | Chat::MESSAGE_PRIVATE_ECHO           | VarByte  | 0x0019f5e0 | 0x013a05a0 | n/a |
| 185 | Chat::MESSAGE_PRIVATE                | VarByte  | 0x001a02d0 | 0x013a04e0 | n/a |
| 29  | Clans::CLANSETTINGS_FULL             | VarShort | 0x001a8500 | 0x013a1960 | opcode OK; wire complex (see below) |
| 124 | Chat::CLANSETTINGS_DELTA_CHAT        | VarShort | 0x001af120 | 0x013a0de0 | n/a |
| 9   | Chat::CLANCHANNEL_FULL_CHAT          | VarShort | 0x00183b20 | 0x013a1be0 | n/a |
| 108 | Clans::CLANSETTINGS_DELTA            | VarShort | 0x001aed60 | 0x013a1020 | n/a |
| 130 | Social::UPDATE_IGNORELIST(?)         | VarByte  | 0x001d29c0 (thunk) | 0x013a0ca0 | **NAME SUSPECT** (see below) |
| 67  | Clans::CLANCHANNEL_FULL              | VarShort | 0x00198f70 | 0x013a1520 | opcode OK; wire complex |
| 117 | Clans::CLANCHANNEL_DELTA             | VarByte  | 0x001a3870 | 0x013a0ee0 | n/a |
| 26  | SiteSettings::UPDATE_SITESETTINGS    | VarShort | 0x001a44e0 (thunk 0x001a63e0) | 0x013a19e0 | n/a |
| 23  | PlayerGroup::PLAYER_OP               | VarShort | 0x001869f0 | 0x013a1a60 | **distinct from op-17 SetPlayerOp** |
| 156 | Chat::SET_CHAT_FILTER_B              | 1        | 0x00173d50 | 0x013a0860 | OK (1 raw byte) |
| 137 | Chat::SET_CHAT_FILTER_A              | 1        | 0x00173c20 | 0x013a0b60 | n/a |
| 152 | Interfaces::IF_SET_HTTP_IMAGE        | VarByte  | 0x001ba9f0 | 0x013a0960 | OK |
| 17  | Misc::SET_PLAYER_OP                  | VarByte  | 0x0013eec0 | 0x015c0f00 (PlayerList binder) | **FIXED** wire (see below) |

### Codec opcode/size verification (current Rev948ServerCodecsSocial.kt)

| Codec packet | Codec op/size | Verified op/size | Result |
|--------------|---------------|------------------|--------|
| GameMessage (MESSAGE_GAME)         | 93 / VarByte  | 93 / VarByte  | ✓ OK (wire matches) |
| UpdateIgnoreList                   | 130 / VarByte | 130 / VarByte | opcode/size OK, **WIRE WRONG** |
| ClanChannelFull                    | 67 / VarShort | 67 / VarShort | opcode/size OK, wire unverified-complex |
| ClanSettingsFull                   | 29 / VarShort | 29 / VarShort | opcode/size OK, wire unverified-complex |
| MessageClanChannel                 | 105 / VarByte | 105 / VarByte | opcode/size OK, **WIRE WRONG** |
| ChatFilterSettingsPrivateChat (SET_CHAT_FILTER_B) | 156 / 1 | 156 / 1 | ✓ OK |
| SetPlayerOp                        | 17 / VarByte  | 17 / VarByte  | opcode OK; **WIRE FIXED** this pass |

### SET_PLAYER_OP (op 17, VarByte) — FIXED

Handler 0x0013eec0 disasm. Correct wire: `[LE u16 worldId (0xFFFF=-1)] [byte slot] [CP1252 string text]
[byte cursorVisible]`. slot byte: client computes `slotIndex = ((-rawByte)&0xFF) - 1` → server emits
`writeByteInverse(slot+1)`. cursorVisible: client `visible = (byte==0)`. The prior codec wrote the
string BEFORE the slot byte and used `writeByteSubtract` — both corrected.

### MESSAGE_CLANCHANNEL (op 105, VarByte) — WIRE WRONG, NOT FIXED (flagged)

Handler 0x001a1cc0 reads: `[byte channelIndex] [gSmart hi-byte + 3 raw bytes = 4-byte hashed msgId]
[CP1252 string message]`. The current codec writes `byte 0; short 0; byte 0; short 0; string` — does
NOT match. Left unchanged pending capture confirmation of the channelIndex / hashed-msgId semantics.
DO NOT trust the current MessageClanChannel encoder.

### CLANCHANNEL_FULL (op 67) / CLANSETTINGS_FULL (op 29) — opcode OK, wire complex

Both are reached via sub-prot dispatch (`param_3` = sub-index) and delegate to clan-buffer decoders
(`Inventory::UPDATE_INV_GROUP`-style sub-readers, ClanSettingsDelta::*::Decode). The flat encoders in
the codec MAY work for the common case but the exact wire was not byte-verified this pass. FLAGGED for
capture verification.

### op 130 "Social::UPDATE_IGNORELIST" — NAME SUSPECT (critical)

The handler bound at op 130 (0x001d29c0) reads a **64-bit flag mask** (`gT_ulong`) followed by ~48
conditionally-gated fields (uint flags, smarts, strings, coords) and stores a single **0x1e0-byte
relationship/friend-entry struct** to `*(RelationshipManager+0x98)` with a dirty flag at +0xa0. This is
a per-friend RELATIONSHIP DELTA structure — NOT a list of (displayName, previousName) ignore pairs as
the rev-947 UPDATE_IGNORELIST and the current Kotlin `UpdateIgnoreList` encoder assume. It also does
NOT match the documented rev-947 UPDATE_FRIENDLIST string-loop. It appears to be a **948-redesigned
friend/relationship update packet**. Per the NAMING BAR this was NOT renamed in Ghidra (identity
unconfirmed without a live capture), but a decompiler comment documenting the finding was added at
0x001d29c0. **The codec's `UpdateIgnoreList @ op 130` almost certainly does not match this handler —
escalate to the orchestrator.**

### UPDATE_FRIENDLIST resolution

UPDATE_FRIENDLIST was op 102 in rev 947 (per docs/net/serverprot/update-friendlist.md). In 948,
op 102 = IF_SETMODEL (verified). No 948 handler is named UPDATE_FRIENDLIST, and the only flag-mask
relationship handler found is the op-130 one above (which is structurally a friend delta but
redesigned and unconfirmed). **UPDATE_FRIENDLIST's true 948 binding is most likely op 130** based on
the relationship-struct store, but this requires live-capture confirmation before naming. Flagged.

## Ghidra symbols changed this pass
- Decompiler comment added at 0x001d29c0 documenting op-130 identity doubt (no rename — NAMING BAR).
- All IF_* / Chat / Clans / SiteSettings handler NAMES were already applied by the prior pass and were
  cross-checked against RegisterAll opcodes (all consistent); no name corrections needed for those.

## Bottom line
- IF_* opcodes/sizes: 100% verified, codec already correct.
- IF_* WIRE formats: widely diverged from 947-3; ~20 handlers corrected in
  Rev948ServerCodecsInterface.kt this pass; the multi-field ones (SETEVENTS1 field-mapping,
  SETPOSITION, SETANGLE, SETNPCHEAD, SETNPCMODEL, SETMODEL_COORD, SETMODELORIGIN, SETSCROLLPOS/SIZE)
  documented with byte offsets but NOT all re-encoded (field→semantic mapping needs capture).
- Social opcodes/sizes: 100% verified. MESSAGE_GAME + SET_CHAT_FILTER_B wire OK; SET_PLAYER_OP wire
  FIXED; MESSAGE_CLANCHANNEL + UpdateIgnoreList(op130) wire WRONG (flagged, not blindly re-encoded).
</content>
</invoke>
