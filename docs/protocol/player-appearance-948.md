# RS3 NXT 948-5 — PLAYER_INFO APPEARANCE extended-info block (byte-precise)

> **Scope.** The exact 948-5 byte format of the PLAYER_INFO (op22) **APPEARANCE**
> extended-info block — the bytes the server writes into
> `UpdateMask.Appearance.data` and the client reads in
> `jag::PlayerEntity::SetAppearanceAsPlayer`. This is the block that was flagged
> "undocumented + cache-coupled" in `docs/protocol/world-bootstrap-948.md` §4.3
> (the GPI init currently ships **no** APPEARANCE block, so the avatar is a
> placeholder). With this spec the networking team can encode a real default
> avatar from a `Player`'s appearance model.
>
> **Owner for implementation:** `networking-protocol-engineer`.
> **Target build:** Linux/macOS `rs2client.948-5`.
>
> **Read first / builds on:** `docs/net/serverprot/948-research-C-player-npc-misc.md`
> (§1 PLAYER_INFO framing, §3 the "scrambled" reader family, §4 the player mask
> table — APPEARANCE is **bit 3 / mask 0x8**), `docs/protocol/world-bootstrap-948.md`
> (§4.3 GPI init + the 2-byte ext-info length prefix), `docs/protocol/world-entry-render-948.md`.
>
> **Evidence base.** Every wire claim is grounded in the 948-5 decompile (Ghidra
> program `rs2client.948-5` in `~/projects/reclass-data/rs2client-948`, read
> headless), cited `@0xADDR`, or the golden production capture
> `build/undercut-socket-session-production-isaac.jsonl`, cited `[prod]`.

---

## 0. TL;DR — what the server must emit

The APPEARANCE block is dispatched at **player-mask bit 3 (mask `0x8`)** inside
`PlayerEntity::ProcessExtendedInfo @0x0015e290` (948-2-2's `0x0015e110` has
**drifted** to `0x0015e290` in 948-5 — match by symbol, not address). On the
wire the APPEARANCE entry is:

```
[1 byte: length]                 ← transformed (see §1, "mode 3"): wire = (-128 - L) & 0xFF
[L bytes: appearance payload]    ← EACH byte transformed (see §1, "mode 2"): wire = (payloadByte + 0x80) & 0xFF
```

The **payload** (after un-transforming) is parsed field-by-field by
`jag::PlayerEntity::SetAppearanceAsPlayer @0x0014d5b0` in this exact order:

| # | field | read | gate |
|---|-------|------|------|
| 1 | **flags** | g1 | always |
| 2 | **title / prefix id** | gSmart1or2 | `flags & 0x40` |
| 3 | **extra-model list** | g1 count, then count × (g2 value + g1 type) | `flags & 0x2` |
| 4 | **gender / body-type** | g1 (signed) | always |
| 5 | **body/equipment model slots** | `partCount` × slot (see §3) | always (delegated to `FUN_00141b40`) |
| 6 | **base/model-override** | g1, then (`flags&4`? g2 : g1+g1) | always |
| 7 | **colours** | g1 flag; if ≠0 → 4 × g2 + 1 × g1 | always (flag byte always present) |

There is **NO username, NO combat level, NO skull/headicon, NO prayer icon** in
the APPEARANCE block (§5). Those travel via other mechanisms (the username via
the player-list/title lookup; head-icon is **mask bit 17**, a *separate* ext-info
block — `948-research-C` §4 ord 23).

**Typical size:** a standard 12-slot player avatar with no title, no extras, and
colours present is **~30–40 payload bytes** (1 flags + 1 gender + ~24 for 12
slots + 3 model-override + 1 colour-flag + 9 colours). Plus the 1-byte length
prefix and the GPI codec's 2-byte block-length prefix.

> **⚠️ CRITICAL CORRECTION to `948-research-C` §3 (the "scrambled" mode bytes).**
> §3 states each scrambled scalar is "mode-prefixed" and the server should
> `writeByte(0)` before each one. **That is WRONG for the ext-info path.** The
> mode/cipher byte is **NOT on the wire** — it is read from a **fixed
> `.rodata` table** baked into the client (`@0x00cb6a80`, confirmed `.rodata`
> r=true w=false). See §1. The server must instead apply the *fixed* transform
> that the table dictates for each field. For the APPEARANCE block the two
> transforms are **length = mode 3, body = mode 2** (the table values at the
> appearance block's cursor base `0x00cb6ac0`). Emitting a `0x00` mode prefix
> would inject a spurious byte the client consumes as appearance data → desync.

---

## 1. The "scrambled" transform is a fixed client-side table (NOT transmitted)

`ProcessExtendedInfo @0x0015e290` builds a working copy of the GPI packet
(`local_138`, a `jag::Packet`) and reads ext-info from it through two cursors:

- **data cursor** `packet+0x18` — the actual wire bytes (the memcpy'd GPI buffer).
- **mode cursor** `packet+0x28` — the per-field transform selector.

The decisive finding: **`packet+0x28` is initialised to a pointer into the
client's static `.rodata`, not into the packet.** Disasm of the prologue:

```
0015e3c7  MOV qword ptr [RSP+0x128], RBP     ; RBP = &DAT_00cb6ac5  (the mode cursor, local_138+0x28)
```

and before **every** dispatch block the mode cursor is reset to a fixed
`.rodata` offset (36 distinct `local_110 = &DAT_00cb6aXX` resets in the function).
For the **APPEARANCE** block (right before `if (mask & 8)`):

```c
local_110 = &DAT_00cb6ac0;            // @0x0015e290 decompile, line preceding the appearance branch
if ((mask & 8) != 0) {
    len  = Packet::gScrambledByte(packet);              // mode = *cursor++ = DAT_00cb6ac0[0]
    FUN_00c45fe0(buf, len & 0xff);
    FUN_0047ec70(packet, buf, len & 0xff);              // body, mode = DAT_00cb6ac0[1]
    PathingEntity::QueueExtendedInfoPacket(player, buf, ...);   // → SetAppearanceAsPlayer
}
```

The static table at `@0x00cb6a80` (verified `.rodata`, immutable):

```
00cb6a80: 03 01 00 01 03 00 01 02 00 02 00 00 01 03 01 03
00cb6a90: 03 01 00 01 02 03 02 02 00 00 00 00 00 00 00 00
00cb6aa0: 01 02 00 03 01 02 00 00 03 03 03 00 00 02 01 00
00cb6ab0: 01 03 01 00 02 00 00 01 03 03 01 02 02 03 7f 01
00cb6ac0: 03 02 00 00 01 01 03 01 01 03 00 00 ...   ← appearance base = 0xcb6ac0
```

So for APPEARANCE: **`table[0xcb6ac0] = 0x03`** (the length's mode) and
**`table[0xcb6ac1] = 0x02`** (the body's mode).

### 1.1 The four transforms (from `gScrambledByte @0x0047f840` and `FUN_0047ec70`)

For a **scalar byte** read (`gScrambledByte`, length field):

| mode | client computes (value it gets) | ⇒ server must write |
|------|---------------------------------|---------------------|
| 0 | `wire` | `wire = value` |
| 1 | `wire - 0x80` | `wire = (value + 0x80) & 0xFF` |
| 2 | `-(wire)` (unsigned negate) | `wire = (-value) & 0xFF` |
| 3 | `-0x80 - wire` (signed) | `wire = (-0x80 - value) & 0xFF` = `(-(value) - 128) & 0xFF` |

For a **buffer** read (`FUN_0047ec70`, the body bytes — applies to the WHOLE
buffer, one mode for all `len` bytes):

| mode | per-byte client computes | byte order | ⇒ server must write per byte |
|------|--------------------------|-----------|------------------------------|
| 0 | `wire` (verbatim memcpy) | forward | `wire = b` |
| 1 | `wire` | **reversed** | `wire[i] = b[len-1-i]` |
| 2 | `wire - 0x80` | forward | `wire = (b + 0x80) & 0xFF` |
| 3 | `wire - 0x80` | **reversed** | `wire[i] = (b[len-1-i] + 0x80) & 0xFF` |

### 1.2 Net rule for the APPEARANCE block

- **Length byte** (mode 3): `wire = (-0x80 - L) & 0xFF`, where `L` = true payload
  byte-count. (e.g. `L = 33` → `wire = (-0x80 - 33) & 0xFF = 0x5F`.)
- **Payload** (mode 2, forward): every payload byte `b` → `wire = (b + 0x80) & 0xFF`.
  (This is the `writeByteAdd` / `p1_add` transform applied uniformly to the
  whole appearance payload.)

> **JagExtensions impact.** The body is a *uniform* `+0x80` over a byte run, and
> the length is `(-128 - L)`. Neither maps to a single existing JagExtensions
> helper applied per-field; the cleanest implementation is: build the appearance
> payload into a scratch buffer using **plain** writes (the field table in §2/§3),
> then (a) write the length as `writeByte((-0x80 - L) and 0xFF)` and (b) copy the
> scratch buffer applying `(byte + 0x80) and 0xFF` to each byte (a small loop, or
> a `writeByteAdd`-over-array helper). Do **not** prepend `writeByte(0)` mode
> bytes (the now-retired §3 model). See §6 for the contradiction note.

---

## 2. `SetAppearanceAsPlayer @0x0014d5b0` — the payload field walk

`param_1` is a parser-state object set up by
`jag::PathingEntity::QueueExtendedInfoPacket @0x0014e890`, which `memcpy`s the
un-transformed appearance bytes into the state and calls `SetAppearanceAsPlayer`
immediately. State layout used below: `param_1+0x1a` = data ptr, `param_1+0x1c` =
read cursor, `param_1+0x16` = the inner `jag::Packet` used for `gSmart1or2`/g2.
Reads are: direct `data[cursor++]` (= **g1**), `FUN_00121a30`/`FUN_00121a00` (=
**g2 big-endian**, verified identical to `gT<unsigned_short>`), and
`Packet::gSmart1or2 @0x0013d9c0`.

### Field 1 — flags (g1, always)  `@0x0014d5dc`

```c
flags = data[0];               // does NOT advance cursor yet; cursor advances to 1 after
*param_1 = flags;
```

**flags bit meanings** (each confirmed at the cited address in `SetAppearanceAsPlayer`):

| bit | mask | meaning | evidence |
|-----|------|---------|----------|
| 0 | 0x01 | "is-other/visible" flag → stored to model `+0xe0` | `@0x0014d9db AND R15D,0x1` |
| 1 | 0x02 | **has extra-model list** (field 3) | `@0x0014d5f4 TEST R15B,0x2` |
| 2 | 0x04 | **model-override is NPC/morph form** (field 6 reads g2 not g1+g1) | `@0x0014db45 TEST byte[RBX],0x4` |
| 3,4,5 | — | **scale / size 0..7** = `(flags >> 3) & 7`, +1 → `FUN_0042b380(def, ((flags>>3)&7)+1)` | `@0x0014dca0` |
| 6 | 0x40 | **has title / prefix id** (field 2) | `@0x0014d5e0 TEST R15B,0x40` |
| 7 | 0x80 | **gender-variant** (selects the female name-render variant in the title lookup) | `@0x0014dbcd AND R9D,0x80` |

> For a **plain default male avatar**: `flags = 0x00` (no title, no extras, scale
> 0, male). For a female default: `flags = 0x80`.

### Field 2 — title / prefix id (gSmart1or2)  `@0x0014de60`, gated `flags & 0x40`

```c
if (flags & 0x40)
    title = gSmart1or2();        // jag::Packet::gSmart1or2 @0x0013d9c0
```
Stored to `param_1+0x4` (a short). Later resolved to a title *string* via
`FUN_005b5bb0(titleTable, …, title)` (`@0x0014dc06`) — i.e. it is a **title /
display-name-prefix index**, NOT a raw name. Omit (don't set `flags & 0x40`) for
a no-title avatar.

### Field 3 — extra-model list  `@0x0014d611`, gated `flags & 0x2`

```c
if (flags & 0x2) {
    count = data[cursor++];                      // g1
    for (k = 0; k < count; k++) {
        value = g2();                            // BE u16
        type  = data[cursor++];                  // g1
        // pushed as an 8-byte {value, type} into an eastl vector (model add-ons)
    }
}
```
These are appended display models (auras/pets/cosmetic overrides layered on the
avatar). For a default avatar, **leave `flags & 0x2` clear** (no list).

### Field 4 — gender / body-type (g1 signed, always)  `@0x0014d96e`

```c
gender = (signed char) data[cursor++];           // -> param_1+0x8
// special: if this is a lobby/replay world (worldType check), gender is forced to 0
```
`0` = male, `1` = female in the classic mapping (signed so other values are
possible for special body types). For a default avatar: `0` (male) or `1`
(female). This is the body-skeleton selector that also drives which body-part def
table is used in field 5.

### Field 5 — body / equipment model slots (delegated)  `@0x0014d976 → FUN_00141b40`

See **§3** — this is the meat of the avatar and the part with the
`0/kit/item` slot encoding. `SetAppearanceAsPlayer` builds a sub-object, copies
the remaining buffer into it, calls `FUN_00141b40`, then advances the cursor by
the number of bytes that sub-decoder consumed (`cursor += subobj.bytesConsumed`,
`@0x0014db1b`).

### Field 6 — base / model-override  `@0x0014db45`

Read **after** the body models, **always present**:

```c
b = data[cursor++];                              // param_1+0x2c  (a base/billboard byte)
if (flags & 0x4) {                               // NPC/morph form
    modelId = g2();                              // BE u16; 0xFFFF -> -1
    // param_1+0x28 = modelId, param_1+0x2c stays, param_1+0x34 = -1
} else {                                          // common player form
    param_1+0x28 = 0;
    x = data[cursor++];                          // param_1+0x30  (a byte)
    y = data[cursor++];                          // param_1+0x34  (a byte; 0xFF -> -1)
}
```
These three values feed `def+0x109c / +0x10a0 / +0x10a4 / +0x10a8` (render/base
fields). For a default avatar with no NPC morph and no special base: write
`flags & 0x4` **clear** and three bytes `00 00 00` (the common form), or whatever
the avatar's base-animation set requires. (Production sends `00 00 00` for a
plain avatar.)

### Field 7 — colours  `@0x0014db99`

```c
colourFlag = data[cursor++];                     // param_1+0x38
if (colourFlag != 0) {                           // @0x0014de10
    c0 = g2();   // param_1+0x3a   (BE u16)
    c1 = g2();   // param_1+0x3c
    c2 = g2();   // param_1+0x3e
    c3 = g2();   // param_1+0x40
    c4 = data[cursor++];   // param_1+0x42   (a single BYTE, not a short!)
} else {
    c0..c4 = -1;            // 0xFF fill
}
```
**5 colour indices: the first four are BE u16, the fifth is a single byte.** The
leading `colourFlag` is a presence flag (any nonzero enables the colour read; it
is itself stored to `def+0x1090`). For a default avatar: write `colourFlag = 1`
then the five colour indices (or `colourFlag = 0` to use all-default colours).
The classic 5 player colour channels are: hair, torso, legs, feet/boots, skin —
mapped to palette indices the client resolves from its colour tables.

> **End of payload.** After field 7 the cursor must equal the payload length `L`.
> The two `FUN_00c46090(state, 10)` recolour/retexture arrays inside `FUN_00141b40`
> (§3.3) only consume bytes when the avatar's equipped items declare recolour
> slots; for a default avatar they consume **0 bytes**.

---

## 3. `FUN_00141b40` — the body/equipment model-slot decoder (the 0/kit/item encoding)

This is the most important sub-block. It is delegated from field 5 and operates
on the **same appearance buffer** from the current cursor (it receives a
sub-struct whose data ptr/cursor mirror `param_1+0x1a/0x1c`). It loops over the
avatar's **body-part definition table** (`bodyDef = avatarDef+0x260`):

- `partCount = *(bodyDef + 8)` — the number of body-part slots. For a standard
  player skeleton this is the **12-part list** (hat, cape, amulet, weapon, torso,
  shield, arms, legs, hair, hands, feet, jaw/beard) **in the def's slot order**.
  **This count and order are CACHE-DRIVEN** — the server must use the same
  body-part def the client loaded (the player `bodytype` / IDK base). See §4.
- `partType[i] = *(bodyDef+0x10)[i]` — per-slot type; slots with `partType == 1`
  are **auto/skipped** (no wire bytes).

### 3.1 Per-slot wire format  `@0x00141dc6 / 0x00141d70`

For each slot `i` (where `partType[i] != 1`):

```c
b0 = data[cursor++];                             // first byte
if (b0 == 0) {
    slot[i] = EMPTY;                             // -> -1, no model
} else {
    value = (b0 << 8) | data[cursor++];          // 16-bit big-endian (b0 is the high byte)
    if (i == 0 && value == 0xFFFF) {             // slot 0 only: MORPH / NPC form
        morphId = gSmart2or4s();                 // jag::Packet::gSmart2or4s @0x00121ac0
        gender2 = data[cursor++];                // overrides gender/body-type
    } else if (value < 0x800) {                  // KIT
        slot[i].kit = value - 0x100;
    } else {                                     // ITEM (equipped)
        slot[i].item = value - 0x800;            // resolves ObjType; may override body via obj+0x218
    }
}
```

**The split constants are FIXED for the player avatar** (hard-coded in the
sub-object ctor `@0x0014d9c6 MOV [RBP+0x20], 0x80000000100`):

- **base = `0x100`** (the kit base: `kitId = value - 0x100`).
- **threshold = `0x800`** (the item boundary: `value >= 0x800` ⇒ item, else kit).

So the slot value space is:

| wire `value` (16-bit BE) | meaning | decoded id |
|--------------------------|---------|------------|
| (1 byte `0x00`) | **empty slot** | none (-1) |
| `0x0100 .. 0x07FF` | **kit / body-part appearance** | `kitId = value - 0x100` (range 0..0x6FF) |
| `0x0800 .. 0xFFFE` | **equipped item** | `itemId = value - 0x800` |
| `0xFFFF` (slot 0 only) | **NPC morph form** | next: `gSmart2or4s` morph id + 1 byte gender |

> **Encoder rule per slot:**
> - empty → `writeByte(0)` (1 byte).
> - kit `k` → `writeShort(k + 0x100)` (BE; value in 0x100..0x7FF).
> - item `it` → `writeShort(it + 0x800)` (BE; value ≥ 0x800).
> - morph (slot 0) → `writeShort(0xFFFF)` then `writeBigSmart(morphId)` then `writeByte(gender)`.
>
> Write **exactly `partCount` slots** in body-part-def order, skipping
> `partType==1` slots (write nothing for those). The most robust default avatar:
> for unequipped slots write the player's IDK kit values (`k`), for equipped
> slots write `it`, for empty cosmetic slots write `0`.

### 3.2 Captured example (validates the kit/item split)  `[prod]`

A `[prod]` first-sighting PLAYER_INFO appearance block, after the §1.2
un-transform, decodes (slot order) to a realistic mix: `kit816, kit848, kit14,
EMPTY, item1205, EMPTY, EMPTY, item6144, kit128, kit31, item11648, kit31` — i.e.
kit body-parts for unequipped slots (`kit14/kit31/kit128` are typical IDK
indices) interleaved with item ids for equipped gear (`item1205`, `item6144`,
`item11648`). The **kit values are < 0x800-0x100 and the items decode to plausible
equipment ids only under the base=0x100 / threshold=0x800 split** — confirming
the §3.1 constants. (Full multi-block bit-exact capture replay was not
reproduced — the surrounding spot-anim/movement blocks make offset isolation
unreliable; the slot encoding itself is decompiler-conclusive.)

### 3.3 Recolour / retexture arrays (advanced — 0 bytes for a default avatar)

After the slot loop, `FUN_00141b40` reads two optional per-item override arrays:

```c
FUN_00c46090(state+0x16, 10);    // reserve up to 10
if (state.recolourCount != 0)    // only if an equipped item declares recolour slots (bodyDef+0x88)
    for (k=0; k<recolourCount; k++) part_recolour[k] = data[cursor++];   // 1 byte each
FUN_00c46090(state+0x1c, 10);
if (state.retextureCount != 0)   // bodyDef+0xb8
    for (k=0; k<retextureCount; k++) part_retexture[k] = data[cursor++]; // 1 byte each
```
Plus a trailing `g2` read into `state+0xc` (`@0x00141f44`, an aura/idk extra).
**For a plain default avatar (no recolourable equipped items) these consume 0
override bytes**, but the trailing `g2` IS always read — so the body-model
sub-block always ends with a 2-byte value. (When generating a default avatar,
include this trailing `g2` = the avatar's recolour-palette id, typically the
bodytype's default, or `0`.)

---

## 4. Cache coupling — what the server needs to know

The appearance encode is coupled to the cache in two places:

1. **Body-part count & order (`partCount`, field 5 / §3).** The slot loop iterates
   the player **body-part definition** (`avatarDef+0x260`, `*(+8)` = count,
   `*(+0x10)` = per-slot type). The server must emit exactly that many slots in
   that order, skipping `partType==1` slots. For the standard human player base
   this is the well-known 12-slot list, but it is **data-driven** — if the
   client's `bodytype`/IDK base def changes, the count/order changes. Use the
   same player body-part def the client loaded.
2. **Item → body-part resolution (§3.1 ITEM path).** When a slot is an item
   (`value >= 0x800`), the client resolves `ObjType(itemId)` and may pull a
   replacement body-part / gender from the obj's `+0x218` field. The server does
   **not** need to do this resolution to *encode* (it just writes `it + 0x800`),
   but it must only send item ids the client's cache defines (an undefined item
   id will mis-render or crash in `ObjType` resolution).

**For a first-light default avatar** (no equipment), the simplest correct encode
is: all 12 slots as **kit** values from the player's IDK (identity-kit) selection,
`flags = 0x00` (or `0x80` for female), `gender = 0` (or `1`), colours present with
the 5 default channel indices, model-override `00 00 00`. No items, no title, no
extras, no morph.

---

## 5. What is NOT in the APPEARANCE block (important)

Confirmed absent from `SetAppearanceAsPlayer` (no read for them):

- **Username / display name** — there is **no string read** in the appearance
  decoder. The only string-ish ops are the *title* lookup (`FUN_005b5bb0`, by the
  field-2 id) and a `<name>` token substitution (`FUN_00ad8050(… "<name>" …)`).
  The username arrives via the player-list slot, not the appearance block.
- **Combat level** — not read here.
- **Skull icon / head icon (prayer/PK)** — not here. Head-icon is **player mask
  bit 17** (`948-research-C` §4 ord 23: `byte + 3×g2`), a *separate* ext-info
  block.
- **Overhead prayer/PK indicators** — separate masks (POSITION_COLOR bit 21,
  etc.).

So a "default avatar" = flags + (gender) + body slots + colours. Combat level,
name, and icons are layered by other packets/masks.

---

## 6. Contradiction with `948-research-C` §3 — resolved (read this)

`948-research-C` §3 and the current `Rev948ServerCodecsUpdateMasks.kt` model the
"scrambled" reads as **wire-prefixed mode bytes** ("server may always emit mode 0
+ plain value", "encoders prefix each scrambled scalar with `writeByte(0)`").

**The decompile disproves this for the ext-info path.** The mode/cipher cursor
(`packet+0x28`) is initialised to a pointer into **read-only `.rodata`**
(`@0x00cb6a80`, block `.rodata` r=true **w=false** x=false — it *cannot* be
filled from the wire) and is reset to a fixed offset before each dispatch block
(36 resets in `ProcessExtendedInfo`). The modes are therefore a **fixed
client-side obfuscation table, not transmitted**. Emitting `writeByte(0)` mode
prefixes injects bytes the client reads as real appearance/field data → guaranteed
desync.

**Why this hasn't surfaced yet:** per `world-bootstrap-948.md` §0, the world burst
currently never reaches a rendered state and the APPEARANCE block is never sent,
so the `Rev948ServerCodecsUpdateMasks` mode-prefix path has never been exercised
against a live client. This is an **untested assumption**, now corrected.

**Action for `networking-protocol-engineer`:**
1. Implement APPEARANCE per §0/§2/§3 with the **fixed** transforms of §1.2
   (length `(-128 - L) & 0xFF`; body uniform `+0x80`). Do **not** prepend mode
   bytes.
2. Re-audit **all** other 948 ext-info scalar encoders (`Rev948ServerCodecsUpdateMasks`):
   each scrambled field's transform is dictated by `table[blockBase + fieldIndex]`,
   where `blockBase` is the per-block `.rodata` offset (the `local_110 = &DAT_…`
   reset for that block) and `fieldIndex` increments per scrambled read **within**
   the block. The full per-block base map can be extracted from the 36 resets in
   `ProcessExtendedInfo @0x0015e290` if/when other masks are sent. For first-light
   only APPEARANCE (bit 3) is needed; its base is `0x00cb6ac0`.
3. **Plain (non-scrambled) reads do NOT use the table** and need no transform:
   raw `Bit::gBit`, `gSmart1or2`, `gSmart2or4s`, the BE-short slot reads, and the
   jag-string reader — same carve-out as `948-research-C` §3. Inside the
   APPEARANCE *payload* itself, the field reads (§2/§3) are all plain g1/g2/gSmart
   (the `+0x80` body transform is applied once over the whole payload buffer by
   `FUN_0047ec70`, **not** per field).

---

## 7. Worked encode — a default male Lumbridge avatar (illustrative)

Plain payload (before the §1.2 transform), 12-slot human base, all kit, colours
present:

```
00                       flags = 0x00 (no title, no extras, scale 0, male)
00                       gender = 0 (male)
-- 12 body slots (kit values k0..k11 from the IDK; 0 = empty cosmetic slot) --
01 06                    slot0 hat?     -> value 0x106 -> kit 6     (example)
00                       slot1          -> empty
01 ...                   ...            (write writeShort(kit + 0x100) per non-empty slot, or 0x00 for empty)
...                      (exactly partCount slots, def order, skipping partType==1)
00 00 00                 model-override common form (+0x2c, +0x30, +0x34)
01                       colourFlag = 1 (colours present)
00 00 00 00 00 00 00 00  4 × g2 colour indices (BE)
00                       5th colour (single byte)
<g2>                     trailing recolour-palette id from FUN_00141b40 (e.g. 00 00)
```
Then frame it:
1. `L` = byte length of the above plain payload.
2. Length byte on wire = `(-0x80 - L) & 0xFF`.
3. Body on wire = each plain byte `+ 0x80` (mod 256).
4. This `[lenByte][transformedBody]` is the APPEARANCE entry; it sits inside the
   player's ext-info block after the LE mask header (with bit 3 set), and that
   block is prefixed by the GPI codec's 2-byte block-length (`world-bootstrap-948.md`
   §4.3 / `Rev948ServerCodecsPlayerInfo`).

> The exact kit values, colour indices, and 12-slot mapping for *your* default
> avatar come from the player's identity-kit selection + the body-part def order
> (§4). This doc specifies the **wire format**; the **content** is the `Player`'s
> appearance model.

---

## 8. Remaining unknowns / handoffs

- **Per-block `.rodata` mode-base map for the OTHER ext-info scalars.** Only
  APPEARANCE (base `0xcb6ac0`) is fully needed for first-light. The other 35
  block bases are enumerable from the `local_110 = &DAT_…` resets in
  `ProcessExtendedInfo @0x0015e290` (e.g. bit 1 FACE_DIRECTION uses a later base,
  bit 4 HITMARKS another). Trace them when those masks are actually sent (they
  are not render-required for first light).
- **Exact 12-slot body-part def order for the current 948 player base.** The slot
  loop order is whatever `avatarDef+0x260` (`+0x10` array) holds for the loaded
  player bodytype. This is **cache data** — `cache-library-engineer` can dump the
  player body-part/IDK def to give the networking team the precise slot order and
  count. (The classic order is hat, cape, amulet, weapon, torso, shield, arms,
  legs, hair, hands, feet, jaw — but verify against the loaded cache.)
- **The trailing `g2` in `FUN_00141b40` (§3.3).** Always read; for a default
  avatar it is the recolour-palette / aura id. Confirm its default value from a
  clean `[prod]` solo appearance if the avatar mis-renders colours.
- **Capture bit-exact replay.** The decompiler is authoritative for the format;
  a full byte-exact capture decode of one appearance block (consuming the
  preceding variable-length spot-anim/movement ext-info blocks) was not completed
  — those blocks have sentinel-terminated loops that make offset isolation
  fragile. If the encoded avatar mis-renders, capture a **solo** first-sighting
  PLAYER_INFO (one ext-info block, appearance-first mask) and replay it against
  §2/§3 to pin any residual field.
