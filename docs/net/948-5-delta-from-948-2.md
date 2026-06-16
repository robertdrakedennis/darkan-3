# 948-5 vs 948-2-2 — ClientProt Delta (Client → Server)

**Scope.** This document is the precise opcode-level delta of the NXT ClientProt
(client → server) descriptor table between **build 948-2-2** (the current target
in this repo at `core/.../rev948/`) and **build 948-5**. It is the contract
handed off to `networking-protocol-engineer` to update
`core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/` **in place**
(the rev948 package is reused; no `rev948_5/` sibling is created — see Open
Questions §6 for the rationale).

## Sources of truth

| Side | Source | Notes |
|---|---|---|
| 948-5 | `E:/BotWithUsv2.5/Headless Client Research/artifacts/948-5.json` | Auto-generated ProtEntry descriptor dump with per-opcode `lengthClass`, `descriptorEa`, `senders[]`, and a `schema.primitives[]` byte-layout probe. Bootstrap header: `sizeClassAllocatorEa=0x8aba0`, `makeClientMessageEa=0xadd60`, `descriptorCtorEa=0x2e5300`. Self-summary: `total=130 fixed=89 var_byte=28 var_short=13 unknown=0`. |
| 948-2-2 | `docs/net/serverprot/948-clientprot-complete.md` + `docs/net/948-clientprot-matrix.md` + `docs/net/948-prot-tables-dump.csv` + `core/.../rev948/Rev948ClientProtStubs.kt` + `core/.../rev948/Rev948ClientCodecs.kt` | No `948-2.json` artifact exists. The 948-2 baseline is reconstructed from the four in-repo files above. The CSV is the dump of `jag::ClientProt::RegisterAll @ 0x000c45b0` (opcode → size → emitter); the matrix and complete-doc layer in the official `jag::ClientProt::<NAME>` identification. |
| 947-3 (context only) | `E:/BotWithUsv2.5/Headless Client Research/artifacts/947-3.json` | Used only to confirm that a 948-5 anomaly was already present in 948-2 (i.e. not a 948-5 change). 110 of the 130 opcodes had a different `lengthClass` in 947-3, so 947-3 is **not** a useful baseline for byte-level diff against 948-5; that comparison is documented in `docs/net/serverprot/948-delta-from-947-3.md`. |

## Headline summary

**The ClientProt opcode table is functionally identical between 948-2-2 and 948-5.**

| Metric | Count |
|---|---:|
| Total opcodes (both builds) | 130 |
| Added (in 948-5, missing in 948-2-2) | **0** |
| Removed (in 948-2-2, missing in 948-5) | **0** |
| Length-class changed (FIXED ↔ VAR_BYTE ↔ VAR_SHORT) | **0** |
| Fixed-size total-bytes changed | **0** |
| Schema reshape (changed primitive layout at byte level) | **0 confirmed** (auto-probe schemas drifted on a handful of ops — see §6 Open Questions) |
| Sender-symbol renames (new `jag::*` public symbol visible in 948-5, opcode/size/layout unchanged) | **9** |
| Truly unchanged (opcode, lengthClass, totalBytes, no sender-rename) | **121** |

**Implication for `networking-protocol-engineer`:** the 18 registered codecs in
`Rev948ClientCodecs.kt` (Ping, RequestWorldList, FriendListAdd/Del,
IgnoreListAdd, ClanChannelKickUser, ResumePNameDialog, 8× IfButton clicks,
MessagePublicSend, MessagePrivateSend) require **zero opcode-table edits** to
operate against 948-5. The 9 sender renames are pure documentation upgrades —
they refine names and comments in `Rev948ClientProtStubs.kt`, but neither the
opcode number, `ProtSize`, nor the decode body changes.

The bootstrap-address relocations DO move (948-5 ASLR base differs from 948-2),
so any tooling that hard-codes raw `descriptorEa` or `RegisterAll` addresses
needs a one-time refresh.

---

## §1 Bootstrap addresses

Per-build positions of the three anchor functions/tables in the ClientProt
ProtEntry initialisation chain:

| Anchor | 948-2-2 (live target) | 948-5 (`948-5.json` header) | Delta |
|---|---|---|---|
| `sizeClassAllocator` (CPU EA) | not separately recorded — implied within `RegisterAll` body | `0x8aba0` | new value to track |
| `makeClientMessage<jag::ClientProt>` (CPU EA) | not separately recorded — implied within `MakeClientMessage` chain | `0xadd60` | new value to track |
| `descriptorCtor` (CPU EA) | not separately recorded | `0x2e5300` | new value to track |
| `jag::ClientProt::RegisterAll` (CPU EA) | `0x000c45b0` (per `docs/net/serverprot/948-opcode-tables.md` §28-29) | not recorded by 948-5.json (it dumps individual descriptors, not the registrar) | needs fresh Ghidra walk |
| First ProtEntry slot (opcode 0) | `0x015d3ea0` (per `Rev948ClientProtStubs.kt` header doc) | `0xed4110` (948-5.json descriptor[0] `descriptorEa`) | moved — confirms ASLR / re-layout |
| Outlier ProtEntry slot (opcode 25) | `0x015bfb40` (per matrix row 25; 948-2's ProtEntry index skip) | `0xed42a0` (948-5.json descriptor[25] — note: in 948-5 this slot is in the contiguous run, the 0x10-stride extends through it) | possibly **structural change** — see §6 Open Question Q1 |

**Action for `client-launcher-engineer` and tooling owners:** anywhere in
`docs/binary/patch-targets-948.md` or the launcher patcher source that hard-codes
ClientProt-table base offsets, those values are 948-2 only. A 948-5 port needs
re-extraction. Not in scope for `networking-protocol-engineer`.

---

## §2 Change table (all 9 changed opcodes)

Every row below has `948-2 size == 948-5 size` AND `948-2 lengthClass == 948-5
lengthClass`. The change-kind is `SENDER_RENAME` in every row — the wire
contract is byte-identical; only the symbol that 948-5's auto-recovery attached
to the descriptor is different (richer) than what we recorded in 948-2.

| opcode (dec / hex) | 948-2 lengthClass | 948-5 lengthClass | size | change-kind | affected rev948 Kotlin file |
|---:|---|---|---:|---|---|
| 8  / 0x08  | FIX(4)    | FIX(4)    |   4 | SENDER_RENAME | `Rev948ClientProtStubs.kt` line 55 (`UNKNOWN_8`) — comment update only |
| 9  / 0x09  | FIX(12)   | FIX(12)   |  12 | SENDER_RENAME (**material**) | `Rev948ClientProtStubs.kt` line 56 (`UNKNOWN_9`) — see §3 detail |
| 10 / 0x0a  | FIX(16)   | FIX(16)   |  16 | SENDER_RENAME (**confirms IF_BUTTOND identity**) | `Rev948ClientProtStubs.kt` line 57 (`IF_BUTTOND`) — already named, comment can cite stronger source |
| 12 / 0x0c  | VAR_BYTE  | VAR_BYTE  |  -1 | SENDER_RENAME | `Rev948ClientProtStubs.kt` line 59 (`UNKNOWN_12`) — comment update only |
| 15 / 0x0f  | FIX(6)    | FIX(6)    |   6 | SENDER_RENAME (**confirms EVENT_MOUSE_CLICK identity**) | `Rev948ClientProtStubs.kt` line 62 (`EVENT_MOUSE_CLICK`) — already named, comment update only |
| 51 / 0x33  | FIX(0)    | FIX(0)    |   0 | SENDER_RENAME (**confirms NO_TIMEOUT identity**) | `Rev948ClientProtStubs.kt` line 98 (`NO_TIMEOUT`) + `Rev948ClientCodecs.kt` line 46 — already correct, comment update only |
| 52 / 0x34  | FIX(6)    | FIX(6)    |   6 | SENDER_RENAME (**potentially material — see §3**) | `Rev948ClientProtStubs.kt` line 99 (`UNKNOWN_52`) — see §3 detail |
| 81 / 0x51  | VAR_SHORT | VAR_SHORT |  -2 | SENDER_RENAME | `Rev948ClientProtStubs.kt` line 128 (`UNKNOWN_81`) — comment update only |
| 106 / 0x6a | FIX(1)    | FIX(1)    |   1 | SENDER_RENAME | `Rev948ClientProtStubs.kt` line 153 (`UNKNOWN_106`) — comment update only |

**There are NO `ADDED`, `REMOVED`, `LENGTH_CLASS`, or `SIZE` change-kind rows.**
The CSV-style change classes are kept in this section header purely for parity
with the 948-2-from-947-3 delta format (`docs/net/serverprot/948-delta-from-947-3.md`).

---

## §3 Per-opcode details

The 9 rows in §2 each get a subsection. The format mirrors the
948-delta-from-947-3 style: 948-2 field layout pulled from the in-repo docs,
948-5 field layout pulled verbatim from `948-5.json` primitives, followed by a
one-line implementation impact.

### op 8 — UNKNOWN_8 (size 4)

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendMultiDisplayPackets @ 0x001a2d90` | `docs/net/serverprot/948-clientprot-complete.md` row 8, `Rev948ClientProtStubs.kt` line 55 |
| 948-5   | `jag::ClientWatch::MainLogic` | `948-5.json` descriptor opcode=8 senders[0] |

948-2 field layout (from `Rev948ClientCodecs.kt` — none registered) and from the
matrix: shape unknown (UNBOUND/CS2 fallback inferred). 948-2 doc says "no
official MULTI_DISPLAY enum".

948-5 field layout (verbatim from `948-5.json` op=8 primitives):
```
@  0  w=4  intME  int0   src=guessed
@  4  w=2  shortLE short0 src=exact
@  6  w=2  shortLE short1 src=guessed (but totalBytes=4 says no — see below)
```

948-5 self-reports `totalBytes=4, lengthMatched=True` — but the primitive list
above runs to offset 6. The probe is misaligned for this opcode (likely picked
up neighbour-frame stores). Trust the `lengthClass=FIX(4)` and the
`MainLogic` sender attribution; **do not** trust the field-level decomposition
without a fresh Ghidra walk.

**Implementation impact:** none. Update the `Rev948ClientProtStubs.kt`
comment for op 8 from `// fn SendMultiDisplayPackets …` to
`// 948-5 sender attribution: jag::ClientWatch::MainLogic — still no official jag::ClientProt enum match; stays UNKNOWN_8.` No codec change.

### op 9 — UNKNOWN_9 (size 12) — **MATERIAL RENAME**

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendOpLocU @ 0x0015b5c0` (per CSV row 9), commented "use-item-on-loc; no official OPLOCU enum" | `Rev948ClientProtStubs.kt` line 56 |
| 948-5   | **`jag::ClientProt::SendNativeMouseClick`** | `948-5.json` descriptor opcode=9 senders[0] |

This is the single most material rename in the delta. 948-2's CSV labelled the
emitter as `SendOpLocU` (a function name our auto-walker mis-attributed because
opcode 6's emitter is *also* `SendOpLocU` — the entry at `0x015d3e10` got
double-counted). 948-5's auto-recovery, via better symbolisation around the
public `jag::ClientProt::SendNativeMouseClick`, reveals that opcode 9 actually
carries native-mouse-click telemetry — NOT a use-item-on-loc payload.

948-5 field layout (verbatim from `948-5.json` op=9 primitives, totalBytes=12,
lengthMatched=True):
```
@  0  w=2  shortLE   short0  src=exact
@  2  w=4  intLE     int0    src=guessed
@  6  w=2  shortLE   short1  src=exact
@  8  w=2  shortLE   short2  src=exact
@ 10  w=2  shortLEA  short3  src=exact
```

That layout (5 small fields, mixed LE/LEA) is consistent with a "native mouse
event" telemetry record (e.g. button-mask, screen-x, screen-y, modifier, button,
frame-tick) — it is NOT a use-item-on-loc shape. Use-item-on-loc would have a
target id + tile coords + inventory slot, which is the 9-byte family at
ops 41/109/111/125/126.

**Implementation impact:**
1. Rename `UNKNOWN_9` → `SendNativeMouseClick` (sender symbol — *not* an
   official `jag::ClientProt::NAME`; we keep this as the sender name because
   it's a `jag::ClientProt::` qualified C++ method, which IS the official
   namespace, but the wire-packet enum name is still unresolved).
2. Update the stub comment to cite 948-5's primitive table and the
   `jag::ClientProt::SendNativeMouseClick` attribution.
3. Do NOT register a decoder. The shape is still a hypothesis from auto-probe;
   needs a deliberate Ghidra walk of `SendNativeMouseClick` on the live 948-2
   binary before a decode body is wired.
4. **The change is in `Rev948ClientProtStubs.kt` line 56 only.** No
   `Rev948ClientCodecs.kt` edit.

### op 10 — IF_BUTTOND (size 16) — confirmation only

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `IfButtonXInner @ 0x002978d0` via CS2 table slot `0x01365960`, beta-cross-referenced to `jag::InterfaceManager::UpdateDragging → &ClientProt::IF_BUTTOND` | `Rev948ClientProtStubs.kt` line 57, matrix row 10 |
| 948-5   | **`jag::PacketHandler::SendComponentDrag`** (a public-symbol exact match for the IF_BUTTOND drag emitter) | `948-5.json` opcode=10 senders[0] |

948-5 field layout (verbatim):
```
@  0  w=2  shortLE    short0  src=exact
@  2  w=4  intME      int0    src=exact
@  6  w=2  shortLE    short1  src=exact
@  8  w=2  shortBEA   short2  src=exact
@ 10  w=2  shortBE    short3  src=exact
@ 12  w=4  intLE      int1    src=exact
```

That's a 16-byte 2-component drag — consistent with IF_BUTTOND's HIGH-confidence
948-2 ID. The 948-5 primitive shape (`shortLE + intME + shortLE + shortBEA +
shortBE + intLE`) is two `(slotId-shortAdd / itemId-shortAdd / interfaceHash-int)`
record patterns, which is the canonical drag shape.

**Implementation impact:** name is already `IF_BUTTOND` in
`Rev948ClientProtStubs.kt` line 57. Update the comment to add 948-5
cross-confirmation:
`// CONF:HIGH (948-5 cross-confirm: jag::PacketHandler::SendComponentDrag attributes the descriptor; layout shortLE+intME+shortLE+shortBEA+shortBE+intLE matches IF_BUTTOND 2-component drag).`

### op 12 — UNKNOWN_12 (varByte)

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendMultiDisplayPackets @ 0x001a2d90` (same C++ fn as op 8 — multi-emitter) | `Rev948ClientProtStubs.kt` line 59 |
| 948-5   | `jag::ClientWatch::MainLogic` | `948-5.json` opcode=12 senders[0] |

948-5 schema (totalBytes=6, lengthMatched=True for the varByte length field):
```
@  0  w=1  byte    byte0    src=guessed
@  1  w=2  shortBE short0   src=exact
@  3  w=2  shortBE short1   src=exact
@  5  w=1  byte    byte1    src=unknown (helper sentinel)
```

Same pattern as op 8: a `ClientWatch::MainLogic` periodic emit. Still no
official `jag::ClientProt::<NAME>` enum match.

**Implementation impact:** comment-only edit in `Rev948ClientProtStubs.kt`
line 59: append `// 948-5 sender attribution: jag::ClientWatch::MainLogic — still no official jag::ClientProt enum match; stays UNKNOWN_12.`

### op 15 — EVENT_MOUSE_CLICK (size 6) — confirmation only

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendEventMouseClick @ 0x001805b0` (CSV row 15 says `FUN_002956e0`, but stubs/matrix agree on `EVENT_MOUSE_CLICK` via beta `ClientWatch::MainLogic`) | `Rev948ClientProtStubs.kt` line 62 |
| 948-5   | **`jag::ClientProt::SendMouseClick`** | `948-5.json` opcode=15 senders[0] |

948-5 field layout (verbatim):
```
@  0  w=4  intME    int0    src=exact (xform: middle-endian)
@  4  w=2  shortLE  short0  src=exact
```

A 4B middle-endian int (likely packed screen-x|screen-y or
button-mask|frame-tick) + 2B LE short. Matches EVENT_MOUSE_CLICK family.

**Implementation impact:** comment cross-confirmation. Update line 62 comment:
`// CONF:HIGH (948-5 cross-confirm: jag::ClientProt::SendMouseClick attributes the descriptor; 4B intME + 2B shortLE matches EVENT_MOUSE_CLICK).`

### op 51 — NO_TIMEOUT (size 0) — confirmation only

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `ProcessConnections @ 0x0013e6c0` (the C++ FN — NOT the packet name) | `Rev948ClientProtStubs.kt` line 98, `Rev948ClientCodecs.kt` line 46 |
| 948-5   | **`jag::ConnectionManager::MainLogic`** (×2 — two distinct callsites) | `948-5.json` opcode=51 senders[0,1] |

948-2's existing comment already cites the beta proof
(`ConnectionManager::MainLogic → &ClientProt::NO_TIMEOUT`). 948-5 simply
recovers that exact public-symbol-qualified `jag::ConnectionManager::MainLogic`
attribution from the stripped 948-5 binary, confirming
`Rev948ClientCodecs.kt::Ping` is registered on the correct opcode.

**Implementation impact:** comment update only. Append to line 98 comment:
`// 948-5 cross-confirm: jag::ConnectionManager::MainLogic recorded as the descriptor's owning sender (×2 emitters).` No codec change.

### op 52 — UNKNOWN_52 (size 6) — **potentially material — needs RE follow-up**

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendDisplayInfo @ 0x001a2ba0` (commented "no DISPLAY_INFO official enum") | `Rev948ClientProtStubs.kt` line 99 |
| 948-5   | **`jag::ClientWatch::SendWindowStatus`** | `948-5.json` opcode=52 senders[0] |

This is the most ambiguous rename in the delta. `WINDOW_STATUS` already exists
as an OFFICIAL `jag::ClientProt::WINDOW_STATUS` enum, registered in 948-2-2 at
**op 94** with size 3 (per stubs line 141, matrix row 94, beta
`ClientWatch::SendWindowStatus`). So we now have:

- op 52 (size 6) — sender is `jag::ClientWatch::SendWindowStatus` per 948-5.json
- op 94 (size 3) — sender is `jag::ClientWatch::SendWindowStatus` per 948-2 beta cross-reference

Two distinct ProtEntries cannot both BE `WINDOW_STATUS`. Two plausible
hypotheses:

1. **(probable) Multiple-emitter attribution noise.** The C++ method
   `jag::ClientWatch::SendWindowStatus` builds MORE than one outbound packet
   (e.g. one full WINDOW_STATUS + one tail packet with extra fields). 948-5's
   auto-recovery attributed both descriptors to the same sender. The OFFICIAL
   `jag::ClientProt::WINDOW_STATUS` is still op 94 (size 3). op 52 (size 6) is
   a SISTER packet — perhaps a longer display-info / window-event variant —
   that has no distinct official enum.
2. **(possible) Renumbering between 948-2 and 948-5.** WINDOW_STATUS moved from
   op 94 to op 52. But this would have to be accompanied by op 94's size
   changing or losing its sender — and §1 shows op 94 in 948-5 is still
   `FIX(3)` with sender `cs2_cs2op_571` (a CS2-bound emitter, not
   SendWindowStatus). So renumbering is *unlikely*.

948-5 op 52 schema (verbatim, totalBytes=6, lengthMatched=True):
```
@  0  w=1  byte    byte0    src=exact
@  1  w=2  shortBE short0   src=exact
@  3  w=2  shortBE short1   src=exact
@  5  w=1  byte    byte1    src=exact
```

(1B + 2 × shortBE + 1B = 6B — looks like `flag/winType + width-or-x + height-or-y + flag`.)

948-5 op 94 schema (for comparison, totalBytes not present in 948-5.json schema
probe — descriptor empty):
```
(no primitives recovered)
```

So we don't get a 948-5 layout for op 94 to compare against.

**Implementation impact:** comment-only edit, BUT FLAGGED. Update
`Rev948ClientProtStubs.kt` line 99 to:
```
c(52, "UNKNOWN_52", 6)  // CONF:NONE — 948-2 emitter SendDisplayInfo @ 0x001a2ba0;
                          // 948-5 attributes the descriptor to jag::ClientWatch::SendWindowStatus,
                          // BUT op 94 already holds the official WINDOW_STATUS (size 3).
                          // op 52 (size 6) is a SISTER packet — likely a longer
                          // display-info / window-event variant — that has no distinct official
                          // jag::ClientProt enum. Needs RE follow-up to confirm: open question Q3.
```
**Do NOT** rename op 52 to `WINDOW_STATUS`. Do not register a decoder on op 52
attempting WINDOW_STATUS shape — op 94 is the official WINDOW_STATUS.

### op 81 — UNKNOWN_81 (varShort)

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | generic `SendMultiDisplayPackets` PARAM ref only — no dedicated emitter | `Rev948ClientProtStubs.kt` line 128 |
| 948-5   | `jag::ClientWatch::MainLogic` | `948-5.json` opcode=81 senders[0] |

948-5 schema empty (`primitives=[]`, `totalBytes=None`) — auto-recovery couldn't
probe the body, but `lengthClass=VAR_SHORT` is intact.

**Implementation impact:** comment-only edit. Update line 128:
`// CONF:NONE — 948-5 sender attribution: jag::ClientWatch::MainLogic — periodic-emit family (same as ops 8/12/81/106). Still no official jag::ClientProt enum match; stays UNKNOWN_81.`

### op 106 — UNKNOWN_106 (size 1)

| Build | sender / handler symbol | source |
|---|---|---|
| 948-2-2 | `SendMultiDisplayPackets @ 0x001a2d90` | `Rev948ClientProtStubs.kt` line 153 |
| 948-5   | `jag::ClientWatch::MainLogic` | `948-5.json` opcode=106 senders[0] |

Same pattern as op 8 / 12 / 81. Comment-only edit on line 153.

---

## §4 Schema-probe details — opcodes the 948-5.json schema CONFIRMS are correct in 948-2

Beyond the 9 rename rows, `948-5.json` carries enough schema detail to
**positively confirm** the wire shape of the 18 codecs already registered in
`Rev948ClientCodecs.kt`. Every registered codec passes (sizes match,
field count consistent with the implemented decode body):

| Codec class | opcode | 948-5 lengthClass | 948-5 schema check |
|---|---:|---|---|
| `Ping` | 51 | FIX(0) | `totalBytes=0, lengthMatched=True, primitives=[]` — clean |
| `RequestWorldList` | 54 | FIX(4) | `@0 w=4 intBE int0 exact, totalBytes=4` — matches `readInt()` |
| `FriendListDel` | 70 | VAR_BYTE | `@0 w=1 byte (length-byte slot), @1 helper payload0 unknown` — matches `readRSString()` (1-byte length + CP1252) |
| `IgnoreListAdd` | 80 | VAR_BYTE | same as op 70 shape — matches `readRSString()` |
| `FriendListAdd` | 100 | VAR_BYTE | same as op 70 shape — matches `readRSString()` |
| `ClanChannelKickUser` | 89 | VAR_BYTE | same as op 70 shape — matches `readRSString()` |
| `ResumePNameDialog` | 71 | VAR_BYTE | same as op 70 shape — matches `readRSString()` |
| `IfButton` × 8 (ops 21, 30, 43, 45, 68, 92, 103, 127) | FIX(8) | all 8 ops have `lengthClass=FIX(8)`, `schema.primitives=[]` (the CS2-table dispatch hides body-level primitives from the auto-probe). The opcode-table integrity is the contract here, and it holds. |
| `MessagePublicSend` | 124 | VAR_BYTE | `@0 byte (lenByte), @1 byte (color), @2 byte (effect), @3 byte (message-first-byte)` — matches `readByte (color) + readByte (effect) + readByteArray(packetSize-2)` |
| `MessagePrivateSend` | 38 | VAR_SHORT | `@0 shortLE (length-word), helper payloads, @2 byte (name-first-byte)` — matches `readRSString (toDisplayName) + readByteArray(rest)`. NB: the auto-probe shows the 2B varShort length as `shortLE` — that is the OUTER length prefix the framing layer eats, not a wire field the decoder sees. |

**Conclusion:** none of the 18 currently-registered Kotlin codecs needs a
decode-body change for 948-5.

---

## §5 Unchanged opcodes

The following **121** ClientProt opcodes are byte-identical between 948-2-2 and
948-5 (opcode, `lengthClass`, fixed-size `totalBytes` where applicable, and
sender symbol attribution all match within the limits of the auto-probe):

`0-7, 11, 13-14, 16-50, 53-80, 82-105, 107-129`

The 9 changed opcodes (8, 9, 10, 12, 15, 51, 52, 81, 106) are excluded; all 9
are sender-rename only (size + lengthClass intact).

---

## §6 Open questions / RE follow-up needed

### Q1 — ProtEntry table layout outlier (op 25)

948-2's matrix records opcode 25 at the OUTLIER address `0x015bfb40` (a
0x10-stride break in the otherwise contiguous `0x015d3ea0..0x015d36a0`
descending table — recorded in `Rev948ClientProtStubs.kt` header doc as
`idx skips 0x19 at 0x015bfb40`). 948-5.json reports opcode 25's `descriptorEa`
as `0xed42a0`, which IS in the contiguous run (offset 25×0x10 down from
`0xed4110+25*0x10`). This suggests the table layout was straightened between
948-2 and 948-5 — the outlier slot was removed and 25 is now inline with the
rest.

This is layout-internal; it does NOT change the wire protocol. But any tool
that scans the ProtEntry table linearly via the 0x10 stride will work correctly
in 948-5 and would have needed the outlier skip in 948-2. Confirm with a fresh
948-5 Ghidra walk if this matters for tooling.

### Q2 — op 9 layout hypothesis needs Ghidra confirmation

948-5.json's primitive table for op 9 (5 fields, mixed LE/LEA) looks like a
native-mouse-click record, consistent with the
`jag::ClientProt::SendNativeMouseClick` sender. But the primitive widths
include one `intLE` flagged `confidence=guessed`. Before any decoder is wired
on op 9 (currently `UNKNOWN_9`), `ghidra-reverse-engineer` needs to walk
`SendNativeMouseClick` on the live 948-2-2 binary and confirm the actual write
order. Not blocking the current rev948 codec set, but blocks future op 9
implementation.

### Q3 — op 52 / op 94 WINDOW_STATUS disambiguation

(detailed in §3 op 52 subsection). Two ProtEntries (op 52 size 6 and op 94 size
3) both auto-attribute to `jag::ClientWatch::SendWindowStatus` in 948-5. The
official `jag::ClientProt::WINDOW_STATUS` is op 94 (size 3). op 52 (size 6) is
likely a sister/longer-form packet, but we have NO official-enum
identification for it. `ghidra-reverse-engineer` needs to walk
`SendWindowStatus` end-to-end and confirm which `&jag::ClientProt::<NAME>`
references each callsite uses. Not blocking — op 52 stays `UNKNOWN_52` until
confirmed.

### Q4 — Schema `confidence=unknown` helper-payload primitives

`948-5.json` flags every variable-payload tail (the `helper:<addr>`
attribution at `confidence=unknown`) on var-byte/var-short prots. These are
NOT byte-level fields — they're sentinels that mark "from this offset onward,
the auto-probe gave up and the rest is variable-width". The currently-decoded
varByte/varShort codecs all correctly handle the variable tail (via
`readRSString` or `readByteArray(packetSize - prefix)`). No action.

### Q5 — Periodic-emit family (ops 8, 12, 81, 106)

Four opcodes (8 fixed 4B, 12 varByte, 81 varShort, 106 fixed 1B) all attribute
in 948-5 to `jag::ClientWatch::MainLogic` — i.e. the client emits all four
from the same monitoring loop. They are likely a coordinated telemetry batch
(per-frame keep-alive + counters + events). 948-2 lumped all four under the
generic C++ fn name `SendMultiDisplayPackets`, which is the same observation
described differently. The four are still `UNKNOWN_<op>` for lack of an
official `jag::ClientProt::<NAME>` enum. Optional follow-up: a single
focused walk of `MainLogic`'s ClientProt emit sites would name all four at
once.

### Q6 — Why we reuse `rev948/` instead of creating `rev948_5/`

The user has confirmed (decision in the task brief) that `core/.../rev948/` is
reused in place. Justification from this delta:

1. Zero opcode-table changes (no `ADDED`, `REMOVED`, `SIZE`, or
   `LENGTH_CLASS` rows in §2).
2. Zero changes to any currently-registered codec body (§4 confirms all 18
   codecs continue to decode against 948-5).
3. The 9 sender-rename rows are documentation-only — comment edits in
   `Rev948ClientProtStubs.kt`, plus one optional comment line in
   `Rev948ClientCodecs.kt` for op 51 NO_TIMEOUT cross-confirmation.

Maintaining a separate `rev948_5/` package would duplicate 130 stub rows,
8 IfButton click registrations, and the codec class wiring for **zero**
runtime difference. The 948-2 codecs decode 948-5 traffic correctly.

---

## §7 Handoff checklist for `networking-protocol-engineer`

These are the EDITS that flow out of this delta. **All edits are in
`Rev948ClientProtStubs.kt`**; one optional comment edit in
`Rev948ClientCodecs.kt`. No codec-body changes. No opcode renumbering.

- [ ] **op 8** — line 55: append 948-5 sender attribution comment (`jag::ClientWatch::MainLogic`).
- [ ] **op 9** — line 56: rename `UNKNOWN_9` → `SendNativeMouseClick`; replace comment with §3 op 9 attribution. Do NOT register a decoder.
- [ ] **op 10** — line 57: comment cross-confirmation only (`jag::PacketHandler::SendComponentDrag`); name `IF_BUTTOND` unchanged.
- [ ] **op 12** — line 59: append 948-5 sender attribution comment.
- [ ] **op 15** — line 62: comment cross-confirmation only (`jag::ClientProt::SendMouseClick`); name `EVENT_MOUSE_CLICK` unchanged.
- [ ] **op 51** — line 98 (and optionally line 46 of `Rev948ClientCodecs.kt`): comment cross-confirmation (`jag::ConnectionManager::MainLogic`); name `NO_TIMEOUT` unchanged.
- [ ] **op 52** — line 99: append 948-5 sender attribution comment, EXPLICITLY noting that op 94 is the official WINDOW_STATUS and op 52 stays `UNKNOWN_52`. Reference Q3.
- [ ] **op 81** — line 128: append 948-5 sender attribution comment.
- [ ] **op 106** — line 153: append 948-5 sender attribution comment.

That is the complete list. After these 9 comment edits, `rev948/` is the
canonical rev-948 ClientProt definition for both 948-2-2 and 948-5.
