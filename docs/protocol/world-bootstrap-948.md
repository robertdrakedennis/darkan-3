# RS3 NXT 948-5 — World bootstrap (op81 → first PlayerInfo) byte-precise spec

> ## ⚠️ CORRECTION (2026-06-23) — op81 REQUIRES the GPI prefix; Shape A is FATAL; the prefix format is RESOLVED bit-exact
>
> This revision **supersedes the Shape A recommendation below** (§0, §1.4, §7) and
> the `cameraRotation` "keystone" claim in `world-login-camera-render-948.md`.
> All four points are decompile-verified in 948-5 (headless, read-only) and
> bit-exact against `build/undercut-socket-session-production-isaac.jsonl` op81 seq 0.
>
> 1. **op81 ALWAYS parses a GPI prefix on world entry.** The op81 handler
>    `REBUILD_NORMAL_SIMPLE @0x001daa70` checks `worldState+0x49` (the "GPI-present"
>    flag) at entry (`@0x001daa8e CMP byte[RCX+0x49],0`). That flag is set to **1 by
>    the state→30 (LOGGED_IN) transition** (`REBUILD_REGION_HANDLER @0x000f7036`,
>    `param_3==0x1e`). So on every fresh world login `+0x49==1`, and op81 calls the
>    **GPI-prefix parser `FUN_00b254a0(client, packet)` @0x001dab87** BEFORE reading
>    the 18-byte coord header. The parser consumes the prefix bitstream and advances
>    the packet cursor; the handler then reads the coord header at the **advanced**
>    cursor (5119 in production), which is why the magic 0x85 lands at body offset
>    5122 and the check passes.
> 2. **The GPI-prefix bit layout is RESOLVED (bit-exact, generatable):**
>    - **Local player:** `gBit(30)` = packed absolute tile `(plane<<28)|(x<<14)|y`,
>      decoded by `DecodePackedCoord`. Production = `0x0328CCA2` = `(0, 3235, 3234)`
>      → zone (404,404), **exactly** the coord-header centre. This places the local
>      player entity at the spawn tile (`FUN_00b04eb0` spawn).
>    - **Other slots:** a loop `for slot in 1..2047` that **skips the local index**
>      → **2046** iterations, each `gBit(20)` = packed region init
>      `plane=(v>>16)&3, regionX=(v>>8)&0xff, regionZ=v&0xff, dirFlag=(v>>18)&3`
>      (`DecodePackedCoord((v>>16&3)<<28 | (v>>8&0xff)<<14 | (v&0xff))` per `@0x00b256xx`).
>    - **Total bits** = `30 + 2046×20 = 40950` → byte-aligns to **5119**, landing the
>      18-byte coord header at exactly 5119. (Verified: the parser byte-aligns the
>      cursor at its tail, `@0x00b25xxx *(packet+0x18) = (bits+7)>>3`.)
>    - The **local player index** the parser uses is `client[0x19b30]+0x48` = the
>      **`playerIndex`** from the world-login response Part C (`HandleLoginData
>      @0x1cd360`, `rec+0x48`). `WorldLoginDetails.playerIndex` MUST equal this slot.
> 3. **Shape A (op81 = 18-byte header only, no prefix) is FATAL, not "incomplete".**
>    With `+0x49==1`, op81 still calls the GPI parser on the 18-byte body. The parser
>    reads `30 + 2046×20 = 40950` bits = **5119 bytes** from an **18-byte** buffer —
>    a 5101-byte heap over-read (`gBit @0x0013e970` has **NO bounds check**). The
>    local player is placed at a GARBAGE tile, the cursor advances to 5119, and the
>    handler reads the coord header from **5101 bytes past the buffer** → magic byte
>    ≠ 0x85 → op81 **returns the abort sentinel `&DAT_015d35c0` at `@0x001dab4d`,
>    BEFORE** the BuildArea alloc (`@0x001dade7`) and BEFORE `ProcessCameraReset`
>    (`@0x001daea8`). **No scene is built, the camera is never reset → black screen.**
>    `cameraRotation` is irrelevant in this path (op81 aborts before its tail).
> 4. **The camera anchor is NOT `cameraRotation` and NOT directly the GPI.** op81's
>    tail anchor `*(*(client[0x18cb8])+0x240)+0xd8` (the byte `ProcessCameraReset`
>    keys on for `+0x418=6`) is a **map-config flag decoded from the spawn region's
>    cache file** (`MapSquare::LoadFiles @0x005c3fe0`, opcode 0x10 → `+0xd8=1`,
>    object stored to `ConfigProvider+0x240`). It is read without a NULL check, so
>    the spawn MapSquare context must already exist when op81 runs (pre-loaded during
>    the lobby→world loading screen). **But this only matters once op81 actually
>    reaches its tail — which Shape A prevents entirely.** The wire `cameraRotation`
>    byte goes to build-state `+0x428` (`@0x001dab06`), not the anchor.
>
> **THE FIX (Shape B, mandatory):** op81 body = `[generated GPI prefix][18-byte
> header]`. Generate the prefix as: `writeBits(30, localTile.id)` then 2046×
> `writeBits(20, packedRegionInit)` for the other slots (region init may be all-zero
> for a solo spawn — the slots are reset to absent), byte-align, then append the
> existing 18-byte coord header. The local 30-bit tile MUST equal the coord-header
> centre zone and the build-area must contain it (coherence, §4 — still required).
> darkan's `RebuildNormalSimple.rebuildPrefix` field already supports this; it is
> currently EMPTY (`WorldServer.kt:580`) = Shape A = fatal. **darkan's existing
> `PlayerInfoBuilder.buildInit` is NOT prefix-compatible** — it emits the
> ProcessPlayerInfo *per-tick* format (`gBit(1)hasUpdate; gBit(1)hasExt; gBit(2)=3
> movetype; gBit(30)tile`), whereas op81's prefix parser reads `gBit(30)` DIRECTLY
> (no 4-bit header) + 2046×`gBit(20)`. A NEW encoder is required for the prefix; the
> standalone op22 at world entry must be DROPPED (production's first standalone op22
> is seq ~1619, LATE). See §1.3, §4.3, §5 below — corrected to match.
>
> ---

> **Scope.** Everything the WORLD server must send, in order, after a successful
> world login so the NXT 948-5 client builds the scene, renders the avatar, and
> stays alive — instead of cleanly quitting ~1 s after the first S2C burst. This
> is the spec for **generating** the bootstrap from local state rather than
> replaying the three opaque production blobs darkan ships today.
>
> **Owner for implementation:** `networking-protocol-engineer`.
> **Target build:** Linux/macOS `rs2client.948-5`. Opcodes are 948-specific.
>
> **Evidence base.** Every wire claim is grounded in either (a) the 948-5
> decompile (Ghidra project `~/projects/reclass-data/rs2client-948` /
> `gzf-947-3`, program `rs2client.948-5`, read-only), cited as `@0xADDR`, or
> (b) the 0-desync production capture
> `build/undercut-socket-session-production-isaac.jsonl` +
> `~/.undercut/recordings/session-20260622-152729-production-isaac/`, cited as
> `[prod]`. Prior docs this builds on (read, not duplicated):
> `docs/protocol/lobby-world-switch-948.md` (§7 op81 coord layout RESOLVED, §9
> world-login response RESOLVED), `docs/net/serverprot/948-research-A-state-var-zone.md`
> (varp/varc decoders), `docs/net/serverprot/948-research-C-player-npc-misc.md`
> (PLAYER_INFO framing + mask tables), `world/.../PlayerInfoBuilder.kt` (GPI
> per-tick structure).

---

## 0. TL;DR — the three answers

### P0 — why the client quits, and the minimum viable bootstrap

**The failure is a clean `exit()`, not a crash.** The macOS crash report for the
failing darkan run (`rs2client-2026-06-22-15:14:30.ips`, `15:15:15.ips`, both
minutes before the 15:27 production capture) is **`EXC_CRASH / SIGABRT`** with
the main-thread stack:

```
-[NSApplication terminate:]  →  exit  →  __cxa_finalize_ranges (static dtors)
  →  rs2client +0x61fd  →  std::terminate → abort
```

This is the macOS manifestation of the NXT client **deciding to shut the
application down** (post a quit block to the main dispatch queue); the `abort`
is a *secondary* artifact of a C++ global destructor throwing during static
teardown. It is **NOT** a segfault in PLAYER_INFO, the scene build, or a NULL
`VarType` deref (that older SIGSEGV — `docs/protocol/lobby-world-switch-948.md`
§8/§9 — is a different, already-fixed bug). The "`Missing ClientProt
opcode=236`" the server logs is the dying socket's last partial byte — a red
herring, confirmed.

**Root cause: the world-init burst darkan sends is mutually inconsistent and
structurally incomplete, so the client's in-game state logic hits a fatal
condition and quits.** The three concrete incoherences, in priority order:

1. **op81 carries a 5119-byte GPI player-init bitstream whose baked-in local
   tile is zone (404, 404), but darkan hand-sets the op81 coordinate header to
   zone (400, 400)** (`WorldServer.sendWorldLoginCore`). The client reads the
   GPI prefix → places the local player at 404/404, then reads the coord header
   → builds the scene at 400/400. The avatar's GPI tile and the build area
   disagree by 4 zones → the player is outside its own scene. (§1, §4)
2. **darkan then sends a SECOND, standalone `op22 PlayerInfo`** (the 289-byte
   `productionFirstLightPayload`) **immediately after op81** — re-running GPI on
   an already-initialised player list, with a *third* tile that matches neither
   the prefix nor the header, and from a *different capture session* entirely.
   (§4, §5)
3. **darkan sends ZERO varps** between `ResetClientVarcache` (op5) and the first
   `op22`. Production streams **1 598 varps** (1 157 `op61 VarpSmall` + 441
   `op28 VarpLarge` + 5 `op147 VarpLong`) there — the player's entire saved
   account state — *before* the first per-tick `op22`. (§2, §3)

**Minimum viable bootstrap (the smallest coherent thing that should keep the
client alive). Two valid shapes — pick A:**

- **Shape A (recommended — single coherent GPI, no double-send).** Send op81 as
  the **18-byte coord header ONLY (no GPI prefix)** with `packedCoordA/B`
  correct for the player's real spawn zone, then send the GPI exactly once as a
  standalone `op22 PlayerInfo` init whose local 30-bit tile equals that zone.
  Do **not** ship the production prefix. (§1.4 proves an 18-byte op81 is
  structurally valid — the handler reads the coord header at `position`, which
  is 0 when no prefix precedes it.)
- **Shape B (bundle GPI in op81, like production).** Send op81 as
  `[generated GPI bitstream][18-byte coord header]` where the GPI's local tile
  and the header's centre zone are the SAME zone, and send **no** standalone
  `op22` on the first tick. This needs a real GPI-init encoder (the 2047-slot
  bit structure, §4.3) — more work than Shape A.

**On the varp baseline:** the clean-`exit` failure is driven by the *coordinate
incoherence + double GPI* far more than by missing varps (PLAYER_INFO has no
varp/scene gate — `docs/.../948-research-C` §7.3, re-confirmed `@0x001618a0`).
But several world-entry CS2/onload scripts read varps, and an empty baseline
leaves the HUD/skills/settings in a default state that *can* trip a script
assert. The **safe, generate-from-state baseline** is: emit every varp/varc the
player actually has a non-default value for (the account's saved var map). There
is **no fixed "magic N varps" the client hard-requires** — production simply
dumps the whole account var table. Send what you have; do **not** send var ids
the client's cache doesn't define (that path *does* SIGSEGV — §2.4).

### P1 — op81 RebuildNormalSimple full payload

Handler `jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE @ 0x001daa70`
(confirmed; the brief's address is correct). The op81 body is **two
concatenated structures**:

```
op81 body (VarShort, production size 5137):
  [0       .. 5118]  GPI player-init bitstream  (5119 bytes)   ← the "opaque prefix"
  [5119    .. 5136]  18-byte build-area coordinate header
```

The **5119-byte prefix is a Global-Player-Info (GPI) initialisation
bitstream** — local player tile + the 2047 other-slot init — NOT map/landscape
data, NOT XTEA keys. Proof: the first 30 bits of the production prefix decode to
`(plane 0, x 3235, y 3234)` → **zone (404, 404)**, byte-identical to the coord
header's centre zone (404, 404). (§1, §4) The 18-byte header is fully resolved
(§1.2) and already correctly encoded by `Rev948ServerCodecsRebuild.kt`.

### P2 — op22 PlayerInfo first-login (GPI) format

PLAYER_INFO is `op22`, VarShort, handler
`jag::PlayerList::ProcessPlayerInfo @ 0x001618a0`. It parses **unconditionally**
— no scene-ready / map-built gate, returns the OK sentinel `&DAT_015d3620` on
every path. The **first-transmission GPI lives in op81's prefix** (Shape B) or a
standalone op22 init (Shape A); the *289-byte `productionFirstLightPayload`*
darkan replays is a **per-tick** op22 from a different session (its first bits
decode `localHasUpdate=1, hasMove=0` — a per-tick update, not an init), and its
~22-byte bit block + ext-info do NOT match this session's player. (§5)

---

## 1. op81 REBUILD_NORMAL_SIMPLE — full payload (P1)

### 1.1 The two-part body (DEFINITIVE)

`[prod]` op81 is one packet: `opcode 81`, VarShort, body **5137 bytes**,
`isaac_index=1` (the **first** in-game ISAAC packet), `main_state=30
(LOGGED_IN)`, `truncated=false`. On the wire the body is:

```
offset  size   structure
0        5119   GPI player-init bitstream  (consumed first; advances packet position to 5119)
5119     18     build-area coordinate header (read by REBUILD_NORMAL_SIMPLE @0x001daa70)
```

The magic byte `0x85` occurs **exactly once** in the whole 5137-byte body, at
offset **5122** (= 5119 + 3). The handler checks magic at `position + 3`
(`@0x001dab40 CMP R14B,0x85`; decompile: `if (cVar4 != -0x7b) return
&DAT_015d35c0` — `-0x7b` signed == `0x85` unsigned, so the "0x7B" in stale
comments and the "0x85" in the disasm are the **same byte**). For that check to
pass, **`position` must be 5119 when the handler runs** — i.e. the 5119-byte
prefix was consumed before the coord read.

### 1.2 The 18-byte coordinate header (RESOLVED — re-confirmed @0x001daa70)

Decompile of `REBUILD_NORMAL_SIMPLE` reads, starting at `packetPtr->position`:

| off | field | type / transform | decompile |
|----|-------|------------------|-----------|
| +0 | filler | ignored | `position += 3` skips it |
| +1 | centreZoneZ **low**  | u8 | `bVar2 = buf[pos+1]` |
| +2 | centreZoneZ **high** | u8 | `bVar3 = buf[pos+2]`; `Z = (lo + hi*0x100) & 0xffff` → **LE u16** |
| +3 | **magic = 0x85** | u8 | `if (cVar4 != -0x7b) return &DAT_015d35c0` (abort) |
| +4 | centreZoneX | **BE u16** | `uVar15 = buf[pos+4..5]; uVar15<<8 \| uVar15>>8` |
| +6 | cameraRotation | u8, **writeByteAdd** | `cVar5`; stored `(cVar5 + 0x80) & 0xFF` → wire `= value + 0x80` |
| +7 | filler | ignored | genuine gap (`position` goes +6→+7→+10) |
| +8 | targetWorldId | **BE u16** | `local_6a = buf[pos+8..9]; <<8 \| >>8` |
| +10 | packedCoordA | **BE u32** | `gT_unsigned_int @0x00121a60` → `DecodePackedCoord @0x006d4320` |
| +14 | packedCoordB | **BE u32** | `gT_unsigned_int` → `DecodePackedCoord` |

`DecodePackedCoord @0x006d4320` (whole function, 55 bytes):
`word = (plane << 28) | (field1 << 14) | field0`, fields are 14-bit (0..0x3FFF),
plane 2-bit; `0xFFFFFFFF` = "no coord" sentinel. The handler passes both fields
of each word `>> 6` to the BuildArea allocator
(`SceneManager::FUN_00c55e80 @0x00c56160` → BuildArea ctor `FUN_00643d10`,
`form=3`): **packedA = origin pair `{originZoneX, originZoneZ}`**, **packedB =
span pair `{sizeZonesX, sizeZonesZ}`**, both in zones (0..255 after `>>6`).
Therefore encode `field = (zone << 6) & 0x3FFF` (this is exactly what
`RebuildNormalSimple.packZoneCoord` does). Production values:
`packedA=0x01a00940` → `>>6 (26, 37)`, `packedB=0x048e23b8` → `>>6 (72, 142)`;
`cameraRotation=7`, `targetWorldId=474` (**note: nonzero in production — it is
the world id, not 0**), `centreZoneX=centreZoneZ=404`.

> **Absolute world position travels in `centreZoneX`/`centreZoneZ` (+4, +1/+2),
> NOT in the packed words.** The 14-bit packed fields physically cannot hold an
> absolute world coord (`>>6` caps them at 255 zones); they are build-area-local
> origin+span only. The handler turns the centre into local tiles via
> `local_64 = (centreZoneX − (camGrid>>4)) * 8`.

### 1.3 The 5119-byte prefix is a GPI bitstream (the P1 answer)

Decoding the production prefix as a GPI bitstream (MSB-first bit reader, like
`Packet::Bit::gBit`):

- **First 30 bits = local player absolute tile**, packed `(plane<<28) |
  (x<<14) | y` = `0x0328CCA2` → `plane 0, x 3235, y 3234` → **zone (404, 404)**.
  This is byte-identical to the coord header's centre zone — proving the prefix
  is GPI, and proving the local tile and the build-area centre **must agree**.
- The remaining bits are the **2047 other-slot init** in the 4-pass
  `ProcessPlayerInfo` structure (high-res active/inactive, low-res
  active/inactive). It is *not* a flat `2047 × 18-bit` region array (that decode
  ends at byte 4610, not 5119); it is the variable-width bit structure the
  client's `GetLowResolutionPlayerPosition @0x001538e0` reads (the 20-bit
  absolute path `gBit(0x14)`, the multi-chunk paths, etc.). It byte-aligns at
  exactly **5119** so the coord header lands on a byte boundary.

**What the prefix does NOT contain:** no map-square group ids, no XTEA keys, no
landscape data. The build area's map squares are loaded by the client from its
**local cache** keyed off the build-area zones (the `>>6` origin/span from the
coord header) — the server does not stream map data in op81. (Confirmed:
`SceneManager::FUN_00c55e80` does not read the packet; it allocates the
BuildArea from the four coord ints and pulls map squares from the scene's own
world ref.)

### 1.4 op81 can be sent as 18 bytes (no GPI prefix) — IMPORTANT for the fix

op81's ProtEntry is `InitEntry(&DAT_013a1360, 0x51, -2)` — a single VarShort
packet with the single handler `REBUILD_NORMAL_SIMPLE`. The handler reads the
coord header at `packetPtr->position`; if no prefix precedes it, `position = 0`
and the handler reads bytes 0–17 as the header (magic at byte 3). **So a bare
18-byte op81 with magic at offset 3 is fully valid and builds the scene.** The
GPI is then supplied separately as a standalone `op22 PlayerInfo` init. This is
**Shape A** and is the simplest correct fix: it removes the prefix↔header
incoherence and the double-GPI problem in one move, with no GPI-init encoder
needed beyond the standalone op22 (Shape A only needs the local-player tile +
empty other-slots, §4.3).

> **Why production bundles it (Shape B) and darkan should not (yet):** the live
> server emits a *correct* 5119-byte GPI whose local tile matches the header.
> darkan replays a *captured* prefix (wrong session, wrong zone) AND a separate
> op22 — the worst of both. Either generate a correct bundled GPI (Shape B,
> needs the full 2047-slot encoder) or drop the prefix and use a standalone op22
> (Shape A). **Do not ship the captured prefix.**

---

## 2. The varp / varc baseline (P0 — what fills op5 → first op22)

### 2.1 Production ordering `[prod]`

World S2C, in order (indices are S2C packet ordinals; isaac game stream):

```
[0]     op81  REBUILD_NORMAL_SIMPLE     5137   ← scene build + bundled GPI init
[1]     op54  HashedWorldToken          44     (base64 token string, NUL-terminated)
[2]     op73  MinimapState              2
[3]     op74  JcoinsUpdate              4
[4]     op172 MinimapFlagA              1
[5]     op204 MinimapFlagB              1
[6..12] op17  SetPlayerOp x6 + op95 MidiSong   ("Follow","Trade with","Req Assist","null","Examine","Duel")
[13]    op5   ResetClientVarcache       0
[14..1616]    VARP / VARC BASELINE      ← 1603 packets, the account var dump
[1617]  op55  DestroyZoneData           0
[1618]  op1   SetNpcOp                  0
[1619]  op22  PlayerInfo (first PER-TICK) 677
[1620]  op77  CAMERA_UPDATE             121
[1621]  op130 UpdateIgnoreListRaw       10
[1622+] op78  UpdateZoneFullFollowsV2 x616, op76 x64, op46 ObjAdd x21, op16 LocDel x24 …  ← zone content
[2292+] op110 RunClientScript x86, op3 IfSetTopLevelInterface, op82 IfSetPosition x56, op35 IfSetEvents x871  ← HUD/interfaces
[3449]  op75  SetReadyFlag              0     ← VERY LATE (after everything)
```

So the canonical order is: **scene-build → player-ops → op5 → full varp baseline
→ op55/op1/op22 → zone content → interfaces/HUD → SetReadyFlag**. The first
`op22` is at index **1619**, after **1605** baseline packets.

### 2.2 Baseline composition `[prod]` (op5 → first op22)

| op  | name        | count | wire (decompile-confirmed) |
|----|-------------|-------|----------------------------|
| 61 | VARP_SMALL  | 1157  | `id (BE u16)` + `value (1 byte, transform `(-0x80 - byte) & 0xFF`)` → `GetVarType(id)` + `PlayerVarDomain::set` @0x00119b30 |
| 28 | VARP_LARGE  | 441   | `value (BE i32)` + `id (BE u16)` → `GetVarType(id)` + `PlayerVarDomain::set` @0x00119a90 |
| 147| VARP_LONG   | 5     | (8-byte value form) @0x00141690 |
| 55 | DESTROY_ZONE_DATA | 1 | size 0 @0x000ef4e0 |
| 1  | SET_NPC_OP  | 1     | @0x0017ff30 |

**1 598 distinct varp ids, range 3 – 12863, monotonically increasing
(VarpSmall), ~99 % non-zero.** This is the player's complete saved small+int
varp table (quest progress, settings, unlocks, etc.). VarpSmall examples
(id, value): `(27, 255) (45, 2) (69, 1) (82, 17) (111, 50) …`. VarpLarge
examples (id, value): `(3, -1807744892) (20, 687865856) (25, 4194304) …`.

> **op61 decode correction for the impl agent.** The id is **BE** (`id<<8 |
> id>>8`), not LE; the value transform is `(-0x80 - rawByte) & 0xFF`
> (≡ `writeByteSubtract`-family). darkan's `Rev948ServerCodecsVariable.kt` should
> be checked against this — `948-research-A` listed op61 only as the unregistered
> `VarpSmall` and noted "no plain VARP-SMALL handler in 948-2-2"; in 948-5 the
> handler **does** exist at `@0x00119b30` (bound by a different `BindHandlers`).

### 2.3 Is the baseline a HARD requirement before op22? (P0 nuance)

**No hard handler-level gate.** `ProcessPlayerInfo @0x001618a0` was
re-decompiled: it reads the bit block then ext-info, consults **no** scene-ready
/ varp / varc predicate, and returns `&DAT_015d3620` on every path. Sending
op22 with zero prior varps is **parsed normally** and is not itself the quit
cause. Likewise `SET_READY_FLAG (op75) @0x00175120` only flips the renderer
ready flag and snapshots a tick; it does not read varps.

**But** the world-entry **CS2 onload scripts** (driven by the `op110
RunClientScript` burst and the interface setup) read varps/varcs to populate the
HUD, skill guide, settings, etc. With an empty baseline those reads return
defaults; in some scripts a missing-but-required var drives a script error, and
the NXT client's response to an unrecoverable script/state error is to **shut
down** (the clean `NSApplication terminate` we observe). So the baseline is
*practically* required for a stable session even though no single packet handler
asserts on it.

### 2.4 HARD HAZARD — do not send unknown var ids

`ConfigProvider::GetVarType @0x005addf0` virtual-dispatches `GetType(varId)`
through the domain's type list and returns **NULL** for an id the client's cache
doesn't define. The varp handlers then deref it with **no NULL check**
(`PlayerVarDomain::set @0x004e1da0` does `*(VarType+8)` immediately). **Sending a
varp/varc id absent from the client's loaded config cache → NULL deref →
SIGSEGV** (this is the *other*, separate failure mode — the §8/§9 varc crash).
**Rule: only emit var ids that exist as `VarType`/`VarcType` defs in the cache
the client actually loaded, and only with an exact length.** When generating
from state, iterate the account's saved var map (which is by definition a subset
of real ids), never a fixed hardcoded list that might contain stale ids.

---

## 3. Generating the varp/varc baseline from local state (P0 — actionable)

There is **no minimal magic set the client hard-requires**; production dumps the
whole account var table and so should darkan. The generate-from-state recipe:

1. After `op5 ResetClientVarcache`, iterate the player's saved **varp** map. For
   each `(id, value)` where the value differs from the type's default:
   - if `value` fits a signed byte under the op61 transform → emit
     `op61 VARP_SMALL` = `writeShort(id) ; writeByte((-0x80 - value) & 0xFF)`
     (BE id; value transform per §2.2).
   - else → emit `op28 VARP_LARGE` = `writeInt(value) ; writeShort(id)` (BE).
   - 64-bit varps → `op147 VARP_LONG`.
2. Iterate the player's saved **varc** (client var) map similarly using the 948
   varc opcodes from `948-research-A` (op47/64/92/116 etc.). Most first-light
   varcs already arrive in **Part A of the world-login response**
   (`WORLD_LOGIN_SERVER_CLIENT_VAR_BLOCK`, the 1321-byte server-client-var block
   — that block is *correct* and already sent; do not duplicate those ids
   in-game).
3. Send the baseline **between op5 and the first op22** (production order). It
   does not need to be byte-identical to production; it needs to (a) cover the
   vars the world-entry CS2 scripts read and (b) contain only cache-valid ids
   with exact sizes.
4. For a **truly minimal first-light** (accept a bare HUD, no quest/skill
   state): the baseline can be small or empty *provided* you also do not push
   the CS2/interface scripts that would read missing vars. But the coordinate
   coherence (§4) and single-GPI (§5) fixes are mandatory regardless — they are
   what stops the quit.

> **Concrete first-light target:** make the avatar render at a coherent tile and
> keep the client in `main_state 30` sending `op51 KEEPALIVE_NUDGE` (the *only*
> C2S packet production sends post-burst — verified, 31 keepalives, nothing
> else). You do **not** need to reproduce the 616 zone-update packets or the 871
> `IfSetEvents` to stay alive; those populate content/HUD. Add them once the
> client survives.

---

## 4. Coordinate coherence constraints (P3 — the incoherence that quits)

Three coordinates must agree. Using the player's spawn tile `(px, py)` at plane
`pl`:

- **Spawn zone** = `(px >> 3, py >> 3)`. (Lumbridge `(3235, 3234)` → zone
  `(404, 404)` `[prod]`.)
- **op81 coord header** centre zone `centreZoneX` / `centreZoneZ` **must equal
  the spawn zone** (`px>>3`, `py>>3`). (+4 BE, +1/+2 LE.)
- **op81 `packedCoordA`** = build-area **origin** in zones; **`packedCoordB`** =
  build-area **span** in zones (both `(zone<<6)&0x3FFF` per field). The window is
  the classic 104-tile / 13-zone area; origin = `centreZone − span/2`. (Whether
  A is the window corner or centre is the one residual capture-only ambiguity —
  `docs/.../lobby-world-switch-948.md` §7.2; it only shifts the scene by ≤ half
  a window and does not cause the quit.)
- **op81 GPI prefix local 30-bit tile** (Shape B) — `(pl<<28)|(px<<14)|py` —
  **must equal the spawn tile** the header centres on. In production
  `0x0328CCA2` = `(0, 3235, 3234)` matches `centreZone (404,404)`.
- **op22 GPI local 30-bit tile** (the standalone init, Shape A) — same
  constraint: **must equal the spawn tile.**
- **`WorldLoginDetails.playerIndex`** (world-login response Part C, +12 BE u16)
  **must equal the GPI slot** the local player occupies.

**Why darkan's current burst is incoherent (the quit trigger):**

| value | darkan now | should be |
|-------|-----------|-----------|
| op81 coord header centre | zone **400/400** (hand-set) | spawn zone, e.g. 404/404 |
| op81 GPI prefix local tile | zone **404/404** (captured prod prefix) | == coord header centre |
| standalone op22 local tile | a **third** session's tile (289-byte blob) | == coord header centre, sent ONCE |
| packedCoordB | span (13/13) — **correct** since §7 fix | span in zones |

The client reads the prefix (avatar → 404/404), builds the scene at 400/400
(player outside its scene), then a standalone op22 re-inits GPI to yet another
tile. The avatar is in an unbuilt zone with contradictory positions → the
in-game logic gives up and quits. **Fix: one tile, everywhere (Shape A: drop the
prefix, op81 header = op22 local tile = spawn zone; or Shape B: prefix local
tile = header centre, no standalone op22).**

### 4.3 GPI first-transmission init structure (for generating it)

The standalone op22 init (Shape A) bit block, per `ProcessPlayerInfo
@0x001618a0` + `GetHighResolutionPlayerPosition @0x00154d30` +
`GetLowResolutionPlayerPosition @0x001538e0` (and the existing
`PlayerInfoBuilder` comments / `player-info-947-3` semantics):

- **Local player** (high-res, slot = `playerIndex`): the absolute-tile path —
  `gBit(1)=1` (has update), into `GetHigh…`, the branch that does
  `gBit(3)` (a direction/flag) then **`gBit(30)` = the absolute tile**
  `(plane<<28)|(x<<14)|y`. Set it to the spawn tile.
- **Other 2047 slots**: on first transmission each is initialised to its
  region (low-res) or left absent. The simplest valid init writes every other
  slot as "no update / not present" — the client keeps them at the reset state.
  Production fills ~hundreds with region inits, but an empty other-slot init is
  sufficient for a solo first-light (no other players visible).
- **ext-info**: append the local player's APPEARANCE block (each ext-info block
  is preceded by a **2-byte length prefix** before the block body —
  `948-research-C` §1, `packet+0x18 += 2`). For 948 the player mask **APPEARANCE
  is bit 3** (mask `0x8`), expansion bits `{0, 13, 22}` (`948-research-C` §4).

> Build on `world/.../PlayerInfoBuilder.kt` — its per-tick `build()` is correct;
> its `buildInit()` currently returns the captured 289-byte blob and **must be
> replaced** with a generated bit block (local 30-bit tile + empty other-slots +
> APPEARANCE ext-info) keyed on the real spawn tile and slot.

---

## 5. op22 PlayerInfo first-login format (P2)

- **Opcode 22, VarShort, handler `jag::PlayerList::ProcessPlayerInfo
  @0x001618a0`** (self-comment "opcode 0x16 (22), size -2"). Framing CONFIRMED
  unchanged from 947-3: bit block, then per-ext-info-block a **2-byte length
  prefix** + block bytes. Codec `Rev948ServerCodecsPlayerInfo`
  (`writeFully(bitBlock)` + per-block `writeShort(size)+writeFully`) is correct.
- **Parses unconditionally** — no scene/ready/varp gate (re-confirmed). Sending
  it in the same burst as op81 is accepted; it is **not** the quit cause.
- **The 289-byte `productionFirstLightPayload` is a PER-TICK op22, not an init,
  and from a different session.** Its leading bits decode `localHasUpdate=1,
  GH.b1=1, GH.move2=0` (a local player with an update and no movement — a
  per-tick shape), and its bit-block + ext-info are this-session-specific
  (different from the 677-byte first op22 in the 15:27 capture, which shares
  only the ext-info tail structure). **Do not replay it.** Generate the init per
  §4.3.
- **Relationship to op81's prefix:** in production the *first* GPI is the op81
  prefix (Shape B); the standalone op22s (first at index 1619) are all
  **per-tick** updates that follow. darkan must choose ONE source of the initial
  GPI (Shape A: standalone op22 init; Shape B: op81 prefix) — never both.
- 948 player ext-info mask table and the `gScrambled*` per-field mode selector
  (prefix each scalar with a `0` mode byte for plain BE) are fully documented in
  `948-research-C` §3–4 — not duplicated here.

---

## 6. End-to-end (server view) — the corrected burst

```
WORLD login (response framing already fixed per lobby-world-switch-948.md §9):
  client --[14 CONNECT_LOGIN]--> world
  world  --[0 + 8-byte seed]--> client
  client --[16 LOGIN + has-extra + RSA + XTEA, mode=2]--> world
  world  --[2 SUCCESS]
         --[Part A: server-client-var block (1321B prod, ackFlag 0x01)]
         --[Part B: players byte 0x02]
         --[Part C: 1-byte len + leadFlag 0 + WorldLoginDetails + 4 trailing fields]
         (all pre-ISAAC; installs ISAAC; client → main_state 30 LOGGED_IN)

WORLD-INIT burst (ISAAC game stream) — the part this doc fixes:
  world  --[op81 REBUILD_NORMAL_SIMPLE]-->
           Shape A: 18-byte header only, centre = spawn zone, packedA/B = origin/span
           Shape B: [generated GPI, local tile = spawn] + 18-byte header (centre = same zone)
  world  --[op54 HashedWorldToken, op73 MinimapState, op74 Jcoins, op172/204 flags,
            op17 SetPlayerOp x6, op95 MidiSong]-->
  world  --[op5 ResetClientVarcache]-->
  world  --[varp baseline: op61/op28/op147 for the account's saved vars
            (cache-valid ids only, exact sizes)]-->            ← §2/§3
  world  --[op55 DestroyZoneData, op1 SetNpcOp]-->
  world  --[op22 PlayerInfo]-->
           Shape A: standalone GPI init, local tile = spawn (§4.3)
           Shape B: per-tick op22 (or omit on tick 1)
  world  --[op77 CAMERA_UPDATE, op130 UpdateIgnoreListRaw]-->
  world  --[zone content: op78/op76/op46/op16 … as available]-->
  world  --[op110 RunClientScript / op3 IfSetTopLevelInterface / interface setup]-->
  world  --[op75 SetReadyFlag]-->   (after the scene + vars exist; production sends it last)
  client renders world; sends only op51 KEEPALIVE_NUDGE (~1/s)   ← success signal
```

---

## 7. Concrete changes for `networking-protocol-engineer`

1. **Make the three coordinates coherent (highest impact — stops the quit).**
   In `WorldServer.sendWorldLoginCore`: set `centreZoneX/centreZoneZ` to the
   player's **real spawn zone** (`px>>3`, `py>>3`), and ensure the GPI local
   tile (whichever shape) equals `(pl<<28)|(px<<14)|py` for the same tile. Stop
   hand-setting 400/400 while shipping a 404/404 prefix.
2. **Stop replaying the captured op81 prefix.** Either (Shape A) drop
   `rebuildPrefix` entirely and send op81 as the 18-byte header only, supplying
   the GPI via a *generated* standalone op22 init; or (Shape B) generate a real
   5119-style GPI bitstream whose local tile matches the header and send **no**
   standalone op22 on tick 1. `RebuildNormalSimplePayloads.productionRev948Prefix`
   should be deleted/retired — it is a wrong-session, wrong-zone blob.
3. **Replace `PlayerInfoBuilder.buildInit`'s 289-byte blob** with a generated
   init bit block (local 30-bit spawn tile + empty other-slots + APPEARANCE
   ext-info with its 2-byte length prefix), per §4.3. Set
   `WorldLoginDetails.playerIndex` = the local GPI slot.
4. **Generate the varp baseline from the account's saved var map** (op61/op28/
   op147), between op5 and op22, **cache-valid ids only, exact sizes** (§2.4 — an
   unknown id SIGSEGVs). Fix the op61 encoder to BE id + `(-0x80 - value)` value
   transform (§2.2).
5. **Send `op75 SetReadyFlag` after** the scene + var baseline (it is the render
   gate; production sends it last). It is independent of op22 ordering.
6. Leave the world-login **response** framing (Parts A/B/C) and the **op81
   18-byte coord layout / `packZoneCoord`** as-is — both are already correct.

---

## 8. Remaining unknowns / warnings for the impl agent

- **op81 prefix exact 2047-slot bit layout (Shape B only).** The local 30-bit
  tile is proven; the precise per-slot encoding for the *other* 2047 slots is
  the 4-pass `ProcessPlayerInfo`/`GetLowResolutionPlayerPosition` bit structure
  (verified to byte-align at 5119) but was not reproduced bit-for-bit here. **If
  you choose Shape B, validate your generated GPI byte-aligns the coord header at
  the right offset.** Shape A sidesteps this entirely — prefer it for first light.
- **packedCoordA = window corner vs centre.** Unresolved (lives in the 9 KB
  BuildArea ctor `FUN_00643d10`). ≤ half-window scene offset; not the quit cause.
  Capture one live op81 to settle if the scene looks shifted.
- **Which exact varps the world-entry CS2 scripts require.** Not enumerated here
  (would need to trace every `op110 RunClientScript` arg + its CS2). The safe
  path is "dump the account's saved vars"; the risky path is "guess a minimal
  set". If a *specific* CS2 still asserts with a full account dump, trace that
  script's var reads.
- **The clean `exit` has no single faulting client function** — it is
  `NSApplication terminate` posted to the main queue (a deliberate quit), so
  there is no "fix this one address". The fix is structural coherence (§4/§5),
  not a packet-field tweak. If after the coherence fixes the client *still*
  quits, capture a fresh `.ips` — a different abort site would point at the next
  cause.
- **op54 HashedWorldToken** body is a base64 token **string** (NUL-terminated),
  44 bytes in prod; darkan already generates one. Not load-bearing for render.
- The brief's `op22 first = 289 bytes` is darkan's blob; **production's first
  op22 is 677 bytes** (per-tick, this session) — sizes are session-specific, do
  not hardcode either.
```
