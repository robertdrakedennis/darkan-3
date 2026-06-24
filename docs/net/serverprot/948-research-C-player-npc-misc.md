# 948 ServerProt subsystem C — Player / NPC / Misc (RE research)

Target: **rs2client.948-2-2** (Ghidra port 8081, STRIPPED — authority). Reference: librs2client.so
(rev ~890, symbols — pattern only). Verified 2026-05-29.

Naming bar: a block is NAMED only with (a) a self-identifying named-fn/format/offset, (b) confident
sig-match to a named beta fn, or (c) exact match to a capture-confirmed 947 packet. Otherwise it
stays UNK with its observed read shape.

---

## 1. PLAYER_INFO (op 22, varShort) — framing CONFIRMED UNCHANGED

`jag::PlayerList::ProcessPlayerInfo @ 0x00161720` (self-comment: "opcode 0x16 (22), size -2").
Bit-block uses `Bit::gBit` + `GetHighResolutionPlayerPosition` / `GetLowResolutionPlayerPosition`
/ `ReadStationary`. Ext-info section: each block preceded by a **2-byte length prefix**
(`packet+0x18 += 2` before `PlayerEntity::ProcessExtendedInfo`). Codec
`Rev948ServerCodecsPlayerInfo` (writeFully(bitBlock) + per-block writeShort(size)+writeFully) is
CORRECT.

## 2. NPC_INFO (op 52, varShort) — framing CONFIRMED UNCHANGED

`jag::NPCList::ProcessNpcInfo @ 0x001d79a0` (self-comment: "opcode 0x34 (52), size -2"). Same
2-byte length prefix before each `ProcessExtendedInfoNPC`. Codec `Rev948ServerCodecsNpcInfo` is
CORRECT.

---

## 3. The 948 ext-info "scrambled" reader family (NEW vs 947-3, IMPORTANT)

> **⚠️ CORRECTION (2026-06-22, world-entry RE).** The claim below that "Server may
> always emit mode 0 + plain BE value" / "prefix each scrambled scalar with
> `writeByte(0)`" is **WRONG**. The mode selector is **NOT on the wire** — the
> `packet+0x28` mode cursor is initialised to a pointer into **read-only `.rodata`**
> (`@0x00cb6a80`, block `.rodata` r=true **w=false**) and reset to a fixed offset
> before each dispatch block (36 resets in `PlayerEntity::ProcessExtendedInfo @0x0015e290`,
> 948-5; the 948-2-2 `0x0015e110` drifted). The modes are a **fixed client-side
> obfuscation table, not transmitted**, so the server must apply the *table-dictated*
> transform per field — NOT prepend a `0` byte (which the client would consume as
> real data → desync). The transform table below is still correct; only the
> "transmitted mode byte" model is wrong. Full analysis + the APPEARANCE example
> (length=mode3, body=mode2 at base `0x00cb6ac0`) in
> `docs/protocol/player-appearance-948.md` §1 and §6.

Every scalar field in both ext-info handlers is read through a `gScrambled*` reader
(`gScrambledByte/Ubyte`, `gScrambledUshort`=`jag::Packet::gScrambledUshort @ 0x0047f170`,
`FUN_0047f240` mode-select short, `gScrambledUint`, `gScrambledMedium`) or the mode-select
array-buffer reader `FUN_0047e7f0`. Each consumes a **leading 1-byte mode selector** (from a
separate cursor at packet+0x28), then reads with one of 4 transforms:

| mode | short transform | byte transform |
|---|---|---|
| 0 | BE / plain | plain |
| 1 | LE (b1<<8\|b0) | reversed copy |
| 2 | BE, low byte +0x80 | byte −0x80 |
| 3 | LE, low byte +0x80 | reversed −0x80 |

Server may always emit **mode 0 + plain BE value**. The `Rev948ServerCodecsUpdateMasks` encoders
therefore prefix each scrambled scalar with `writeByte(0)`. NOT mode-prefixed: raw `Bit::gBit`,
`gSmart1or2`, `gSmart2or4s`, BE-short slot reads (`FUN_00121880`), and the jag-string reader
`FUN_00ad89a0`.

This is the binary's own "scrambled" labeling for a per-field transform selector — it is NOT a
cipher and NOT a fixed 4-mode dispatch on the whole packet.

---

## 4. PLAYER mask table — CONFIRMED (exhaustive walk of ProcessExtendedInfo @ 0x0015e110)

Header: 1..4 byte LE. **EXPANSION BITS = {0, 13, 22}** (decode prologue: byte0 bit0 → byte1;
byte1 bit5[=13] → byte2; byte2 bit6[=22] → byte3). 23 top-level `if ((mask & flag))` tests, source
order = dispatch order:

| ord | bit | mask | identity | evidence | 947 bit |
|---|---|---|---|---|---|
| 1 | 26 | 0x4000000 | SPOT_ANIM_REMOVAL (count + BE-short ids, −1 ⇒ remove all) | structural | 26 |
| 2 | 18 | 0x40000 | transient bool → player+0x1071 | offset | (n/a) |
| 3 | 20 | 0x100000 | OVERHEAD_CHAT (string + flags → ChatHistory::AddChat) | named fn | 21 |
| **4** | **3** | **8** | **APPEARANCE** (len + mode-buffer → QueueExtendedInfoPacket) | named fn | **2** |
| 5 | 6 | 0x40 | OVERHEAD_TEXT (gScrambledMedium type-dispatch FUN_0042e610) | structural | 6 |
| 6 | 24 | 0x1000000 | spot-anim list (complex) | structural | 24 |
| 7 | 11 | 0x800 | spot-anim triple (g2+uint+byte) | structural | — |
| 8 | 15 | 0x8000 | spot-anim triple | structural | — |
| **9** | **7** | **0x80** | **FORCED_MOVEMENT** (6×byte+3×g2 → SetForcedMovement) | named fn | **4** |
| 10 | 14 | 0x4000 | CHAT_TEXT_PRIVATE (compound: len + mode-buffer FUN_00121980) | structural | 16 |
| 11 | 25 | 0x2000000 | spot-anim triple | structural | 25 |
| 12 | 16 | 0x10000 | spot-anim list (SpotAnim::GetTypeBySlot) | named fn | — |
| 13 | 9 | 0x200 | 2×byte + g2 | structural | 9 |
| 14 | 1 | 0x2 | FACE_DIRECTION (g2 → JagexAngleToRadians → player+0x23c) | named fn | 1 |
| 15 | 23 | 0x800000 | spot-anim list | structural | 23 |
| 16 | 5 | 0x20 | EXACT_MOVE (4×gSmart2or4s + byte) | structural | (was FACE_ENTITY) |
| 17 | 19 | 0x80000 | spot-anim triple | structural | — |
| **18** | **12** | **0x1000** | **OVERHEAD_OPACITY** → player+0x1074 | offset match | **8** |
| 19 | 2 | 0x4 | spot-anim triple | structural | — |
| **20** | **10** | **0x400** | **CHAT_TEXT** (FUN_00ad89a0 + ChatHistory::AddChat type 2) | named fn | **15** |
| 21 | 21 | 0x200000 | POSITION_COLOR (HSLToRGBLookup → player[0x32..0x34]) | named fn | 22 |
| **22** | **4** | **0x10** | **HITMARKS** (count + gSmart1or2 list → AddHitmark/AddHeadbar) | named fn | **3** |
| 23 | 17 | 0x20000 | head-icon (byte + 3×g2) | structural | — |

**APPEARANCE render blocker RESOLVED:** bit 2 → bit 3. `ActiveMaskKeys.playerAppearance` now wired.

## 5. NPC mask table — CONFIRMED (exhaustive walk of ProcessExtendedInfoNPC @ 0x001621c0)

Header: 1..5 byte LE 64-bit. **EXPANSION BITS = {6, 8, 19, 25}** (decode prologue: byte0 bit6 →
byte1; byte1 bit0[=8] → byte2; byte2 bit3[=19] → byte3; byte3 bit1[=25] → byte4). **CHANGED from
947-3 {6,13,22,24}.** 29 tests (incl. bit 32 via `mask>>0x20&1`, bit 33 via `mask>>0x21&1`):

| ord | bit | mask | identity | evidence |
|---|---|---|---|---|
| 1 | 4 | 0x10 | HITMARKS_AND_HEADBARS (gSmart1or2 list → AddHitmark/AddHeadbar) | named fn |
| 2 | 5 | 0x20 | ANIMATION (4×gSmart2or4s+byte → RequestAnimation vcall+0x1e0) | structural |
| 3 | 31 | 0x80000000 | spot-anim triple (g2+uint+byte) | structural |
| 4 | 17 | 0x20000 | spot-anim list primary | structural |
| 5 | 14 | 0x4000 | byte + 3×g2 | structural |
| 6 | 28 | 0x10000000 | COMBAT_LEVEL_OVERRIDE_RGB (HSLToRGBLookup → npc+0x190) | named fn |
| 7 | 20 | 0x100000 | MODEL_OVERRIDE_ID (g2, 0xffff ⇒ default) | semantic |
| 8 | 29 | 0x20000000 | spot-anim removal list (BE-short ids) | structural |
| 9 | 32 | >>0x20&1 | model-transform / spot-anim transform list | structural |
| 10 | 27 | 0x8000000 | VISIBILITY_FLAG (1 byte → render flag) | offset |
| 11 | 2 | 0x4 | STRING_OVERRIDE (FUN_00ad89a0 → vcall+0x158) | named fn |
| 12 | 0 | 0x1 | CHAT_OVERHEAD (gSmart2or4s → say vcall+0x220) | structural |
| 13 | 13 | 0x2000 | COMBAT_LEVEL_HEADBAR_ID (g2, 0xffff clears npc+0xf40) | semantic |
| 14 | 30 | 0x40000000 | spot-anim triple | structural |
| 15 | 1 | 0x2 | FACE_TILE (2×g2 → npc+0x22c/0x234) | offset |
| 16 | 7 | 0x80 | spot-anim triple | structural |
| 17 | 12 | 0x1000 | spot-anim list (flags-driven) — NOT 947 colour block | structural |
| 18 | 9 | 0x200 | spot-anim 2-field | structural |
| 19 | 24 | 0x1000000 | transient bool → npc+0xd95 | offset |
| 20 | 11 | 0x800 | spot-anim triple | structural |
| 21 | 21 | 0x200000 | spot-anim submask | structural |
| 22 | 23 | 0x800000 | NAME_OVERRIDE (FUN_00ad89a0 + "null" compare DAT_00fba09d) | named fn |
| 23 | 18 | 0x40000 | spot-anim clear + list | structural |
| 24 | 15 | 0x8000 | FORCED_MOVEMENT (6×ubyte+3×g2 → SetForcedMovement) | named fn |
| 25 | 16 | 0x10000 | CLIENT_SCRIPT_OVERRIDE (flags + heavy struct FUN_00c41cd0) | structural |
| 26 | 26 | 0x4000000 | spot-anim triple | structural |
| 27 | 22 | 0x400000 | spot-anim clear + list | structural |
| 28 | 3 | 0x8 | OVERHEAD_TEXT (gScrambledMedium type-dispatch → npc+0x1a8) | structural |
| 29 | 33 | >>0x21&1 | byte sentinel (0x80 clears) | structural |

**Prior provisional NPC enum was materially wrong** (expansion bits, and FACE_ENTITY/OVERHEAD_ICON/
FORCED_MOVEMENT/NAME_OVERRIDE/COMBAT_LEVEL_HEADBAR all on wrong bits). Now corrected.

---

## 6. Misc spot-checks (CONFIRMED)

- **SET_RUN_ENERGY** `@ 0x00174f40`: reads exactly 1 byte → struct+0x60. Codec op **80**, size 1. UNCHANGED.
- **SET_DISPLAY_INT / JcoinsUpdate** `@ 0x00121910`: g4 (uint32 BE) → display+0x64. Self-comment
  "Verified against 947-3 0x00219f10". Codec op **74**, size 4. UNCHANGED.
- Other Misc/WorldData opcodes (SetReadyFlag 75, ChangeLobby 49, SetWorldTarget 212, SwitchWorld 213,
  ResetAllVarps 5, NoTimeout 54, RunClientScript 110, WorldlistReply 216, FriendStatus 26) were
  registered + handler-verified by the prior pass; not re-walked here.

## 7. Cross-domain bug status

The world builders now drive extended-info header bytes from the active revision instead of
hardcoded 947 literals. `PlayerInfoBuilder` and `NpcInfoBuilder` call `UpdateMaskHeader` with
`ActiveMaskKeys.playerExpansionBits` and `ActiveMaskKeys.npcExpansionBits`; `UpdateMaskHeaderTest`
guards both 947 and 948 byte outputs for multi-byte player and NPC masks.
