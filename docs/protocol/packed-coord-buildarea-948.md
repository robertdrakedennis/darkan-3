# RS3 NXT 948-5 — op81 packed coordinates & build-area map-square loading (DEFINITIVE)

> **Scope.** The exact bit layout of the op81 `packedCoordA` / `packedCoordB`
> words, what they TRULY mean to the client (SW/NE corners of a build-area
> ADDRESSING grid in map-square units — **not** a streaming window), the `>>6`
> math the handler applies, the VERIFIED encode recipe, and — corrected — the real
> map-square LOAD TRIGGER (a per-frame camera-driven streamer, gated on render
> state + a positioned camera) explaining why darkan's client gets a coherent op81
> but issues **zero** index-5 (`m{X}_{Z}`/`l{X}_{Z}`) requests (permanent black
> screen). See the CORRECTION box below — the prior "streaming window" / "bounds
> are sufficient" framing was wrong.
>
> **Authority.** Every claim is grounded in the **948-5** Linux binary,
> read-only Ghidra project `~/projects/reclass-data/rs2client-948`, program
> `rs2client.948-5`, cited as `@0xADDR`, plus the raw production capture
> `build/prod-op81-rebuild.bin`. This is the ONLY source of truth.
> The 910-era `docs/protocol/alerion-buildarea-reference-910.md` is shape-only;
> where it disagrees with byte format, **948-5 wins** (see §7).
>
> **Builds on** `docs/protocol/world-bootstrap-948.md` §1 (the 18-byte coord
> header is RESOLVED there) and `world-entry-render-948.md`.
>
> ---
>
> > ### ⚠️ CORRECTION (2026-06-22) — THE PRIOR "STREAMING WINDOW" SEMANTIC WAS WRONG
> >
> > This revision corrects an earlier version of THIS doc. The byte/bit **DECODE**
> > below was, and remains, **byte-exact** (re-verified against the raw production
> > capture `build/prod-op81-rebuild.bin`, bytes 5119..5136 — see §5.1). What was
> > **wrong** were two semantic claims layered on top of the correct decode:
> >
> > 1. **WRONG:** "packedA/B are the SW + NE corners of *the build-area streaming
> >    window* (a small, player-centred box of map-squares the client JS5-pulls)."
> >    **RIGHT:** packedA/B ARE the SW + NE corners of the build-area grid — but
> >    that grid is a **large coordinate-ADDRESSING extent**, NOT the set of
> >    squares streamed. Production legitimately ships **regions X[26..72] Z[37..142]
> >    — a 47×106 map-square grid (~3008×6784 tiles)**, with the player sitting
> >    asymmetrically inside it (region 50,50: 24 W / 22 E, 13 S / **92 N**). That
> >    is impossible for a "streaming window" but perfectly normal for an
> >    addressing extent. Only the **camera-frustum subset** (a handful of squares
> >    near the eye) is ever streamed — the engine computes a *separate* frustum
> >    region span per-frame (`SceneState +0x620..+0x638`) and streams only those,
> >    indexing them INTO the large build grid (§3, §4).
> >
> > 2. **WRONG (the load-trigger claim):** "The trigger is valid build-area bounds
> >    that contain the player's region. Fix the bounds → the existing streaming
> >    loop requests the squares with no other change. There is no extra 'go'
> >    packet." **RIGHT:** valid bounds are **necessary but NOT sufficient.** The
> >    index-5 (`m{X}_{Z}`/`l{X}_{Z}`) requests are emitted by a **per-frame
> >    camera-driven streamer** (`FUN_00647fc0` / `FUN_0025bcb0`), gated on the
> >    SceneState being in render state (`+0x418 == 6`), a **map-ready flag**
> >    (`+0x650 == 1`, set only when the build/asset-job queues drain), and a
> >    **camera-target triple** (`+0x634/+0x638/+0x63c`) that is computed from the
> >    **eye/camera world position** (`+0x570`). op81 builds the grid CONTAINER but
> >    does NOT itself arm the stream. darkan ships a valid op81 and STILL loads
> >    zero squares because the camera/render preconditions are never met (§4).
> >
> > The two words are still correctly read as **two corners (SW + NE) in
> > map-square units** (`field >> 6` of a tile coordinate). The original
> > world-bootstrap §1.2 `{lo14, hi14, plane}` ordering and "origin/span" framing
> > were already corrected to `{plane, hi14, lo14}` / "two corners"; that part is
> > right. Everything in §1/§2/§5.1–5.3 below is unchanged and verified. §3, §4,
> > §5.4 are rewritten.
>
> ---
>
> **Owner for implementation:** `networking-protocol-engineer` (build-area
> service / op81 encoder) + game-loop (camera/viewport readiness). Do not modify
> server source from RE — this is spec.

---

## 0. TL;DR

1. **`DecodePackedCoord @0x006d4320`** unpacks a 32-bit word into a `{plane,
   hi14, lo14}` triple: `plane = w>>28 & 3`, `hi14 = w>>14 & 0x3FFF`,
   `lo14 = w & 0x3FFF`. `0xFFFFFFFF` = "no coord" sentinel. **The plane goes to
   out[0], hi14 to out[1], lo14 to out[2]** — NOT the order world-bootstrap §1.2
   printed.

2. **`packedCoordA` / `packedCoordB` are the SW and NE corners of the build-area
   grid, in map-square (region) units.** Each 14-bit field holds a **tile**
   coordinate; the handler `>>6`s it to a **region** (map-square = 64 tiles):
   - `packedA → {minRegionX = hi14_A>>6, minRegionZ = lo14_A>>6}` (SW corner)
   - `packedB → {maxRegionX = hi14_B>>6, maxRegionZ = lo14_B>>6}` (NE corner)

   They are **NOT** `(zone<<6)` and **NOT** an origin+span pair. **But the grid
   they bound is a large coordinate-ADDRESSING extent, NOT a streaming window**
   (the original framing). Production ships X[26..72] Z[37..142] = **47×106
   squares**, player asymmetric inside (see §3, §5.1). The proof the unit is
   `tile>>6` and not zones: the accessor `FUN_004d04f0 @0x004d04f0` does
   `SAR ESI,0x6` on a tile then compares to `+0x13fb4`; the build-grid loop in
   `FUN_0025bcb0 @0x0025d409` does `SHL EBX,0x6` to turn a region index back into
   a tile. ÷64 / ×64 throughout — never ÷8 (zone).

3. **Two SEPARATE region rectangles exist — do not conflate them (§3):**
   - **Build-area bounds** `SceneState +0x13fb4/+0x13fb8/+0x13fbc/+0x13fc0`
     (min/maxRegionX/Z, from packedA/B). Sizes the grid ALLOCATION
     (`(maxX-minX+1)×(maxZ-minZ+1)` cells of 0x18 bytes, `@0x00643d10`). This is
     the **addressing space** — what region ids the grid *can* hold.
   - **Camera-frustum bounds** `SceneState +0x620/+0x624/+0x628/+0x62c/+0x630/+0x638`,
     recomputed **every frame** by `FUN_0025bcb0 @0x0025f4xx` from the eye/camera
     world position (`+0x570`). This is the **small set of regions actually near
     the camera** that get streamed and rendered. The streamer/renderer iterate
     the FRUSTUM span and index INTO the build grid (`+0x14000`). This is why a
     47×106 build area is fine: only the ~frustum squares load.

4. **Map-square load trigger (the corrected core — §4).** The index-5 requests are
   emitted by the **per-frame camera-driven streamer** `FUN_00647fc0`
   (called from the world tick, `jag::ConnectionManager::LoginProtocolHandler
   @0x002507f3`), which walks the build-area bounds, range-gates each square
   against the camera position, and enqueues only near squares
   (`FUN_00692600` push-unique). It is **gated**, ALL required:
   - SceneState render state `+0x418 == 6`,
   - map-ready flag `+0x650 == 1` (set by the world tick `FUN_0023e660 @0x00242612`
     only when the build/asset-job queues drain),
   - camera-target triple `+0x634/+0x638/+0x63c` all ≠ -1 (computed from the
     eye position by `FUN_0025bcb0 @0x0025f57d`).

   **op81 builds the grid container and sets the scene centre, but does NOT arm
   the stream.** A valid, non-inverted, player-containing build area is
   **necessary but NOT sufficient.** darkan ships a coherent op81 yet streams zero
   squares because the **camera/render preconditions are never satisfied** — the
   eye is never positioned (player entity + GPI + camera bind), so the frustum
   target triple stays unset and `FUN_00647fc0` never enqueues anything.

5. **Inverted bounds are still independently fatal** (necessary condition): if
   `min > max` the ctor allocates an empty/negative column array and the indexer
   `FUN_0061c2c0` rejects every lookup. darkan's encoder was already fixed for
   this (§5.4); the remaining black screen is the trigger gap in item 4, not the
   bounds.

6. **VERIFIED encode recipe** — §5.2/§5.3 round-trips production
   `packedA=0x01a00940`, `packedB=0x048e23b8` exactly. But shipping correct
   corners is **only the necessary half**; see §4 for the render-readiness work.

---

## 1. `DecodePackedCoord @0x006d4320` — full function (DEFINITIVE)

948-5 decompile (the function is already named
`jag::game::BuildArea::DecodePackedCoord`, 55 bytes):

```c
// out is a uint[3]; packed is the 32-bit wire word (BE-read upstream).
void jag::game::BuildArea::DecodePackedCoord(uint *out, uint packed)
{
  out[2] = 0;
  out[0] = 0xffffffff;          // plane default = sentinel
  out[1] = 0;
  if (packed != 0xffffffff) {
    out[2] = packed & 0x3fff;          // lo14 = bits  0..13   -> out[2]
    out[0] = packed >> 0x1c & 3;       // plane = bits 28..29  -> out[0]
    out[1] = packed >> 0x0e & 0x3fff;  // hi14 = bits 14..27   -> out[1]
  }
  return;
}
```

**Bit layout of one packed coordinate word (32 bits, big-endian on the wire):**

```
 bit:  31 30 | 29 28 | 27 .................. 14 | 13 .................. 0
       [ -- ] [plane] [        hi14 (14b)       ] [        lo14 (14b)       ]
                ^2b      ^ -> out[1]                 ^ -> out[2]
```

- `plane` (out[0]): 2 bits, 0..3.
- `hi14`  (out[1]): 14 bits, 0..0x3FFF — the **X tile** of the corner.
- `lo14`  (out[2]): 14 bits, 0..0x3FFF — the **Z tile** of the corner.
- bits 30..31 unused.
- `0xFFFFFFFF` → `{plane=0xFFFFFFFF, X=0, Z=0}` = "no coordinate" sentinel
  (build area not changed).

> **Correction vs world-bootstrap §1.2.** That doc wrote `word = (plane<<28) |
> (field1<<14) | field0` and then assigned `field0 = out[0]`, `field1 = out[1]`,
> `plane = out[2]`, and labelled the two words "origin pair {originZoneX,
> originZoneZ}" and "span pair {sizeZonesX, sizeZonesZ}". **The widths are right,
> the assignment and the origin/span framing are wrong.** out[0] is the *plane*,
> out[1] is the *X tile*, out[2] is the *Z tile*; the two words are two
> *corners*, not origin+span. The "`>>6 (26,37)` / `>>6 (72,142)`" numbers in
> that doc are arithmetically correct but were mislabelled as "(originZone,
> originZone) in zones" — they are in fact `(minRegionX, minRegionZ)` and
> `(maxRegionX, maxRegionZ)` in **map-square (region) units** (§3).

---

## 2. How `REBUILD_NORMAL_SIMPLE @0x001daa70` consumes packedA / packedB

Handler `jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE` reads both
packed words via `Packet::gT_unsigned_int @0x00121a60` (BE u32) and decodes each:

948-5 decompile (the relevant tail):

```c
  uVar10 = Packet::gT_unsigned_int(packetPtr);        // packedA  (header +10, BE u32)
  game::BuildArea::DecodePackedCoord(local_60, uVar10);   // local_60 = {plane_A, hi14_A, lo14_A}
  uVar10 = Packet::gT_unsigned_int(packetPtr);        // packedB  (header +14, BE u32)
  game::BuildArea::DecodePackedCoord(local_54, uVar10);   // local_54 = {plane_B, hi14_B, lo14_B}
  if (cVar4 != -0x7b) { return &DAT_015d35c0; }       // magic 0x85 gate (abort if absent)
  ...
  // build the scene + the build-area map-square grid:
  game::SceneManager::FUN_00c55e80(                    // real call target 0x00c56160
        scene, &localRef,
        local_5c >> 6,    // = hi14_A >> 6   -> minRegionX
        local_58 >> 6,    // = lo14_A >> 6   -> minRegionZ
        local_50 >> 6,    // = hi14_B >> 6   -> maxRegionX
        local_4c >> 6);   // = lo14_B >> 6   -> maxRegionZ
```

**Instruction-level proof of the argument mapping** (`@0x001dab1b–0x001dadec`):

```
001dab1b  CALL 0x00121a60        ; packedA = gT_unsigned_int(pkt)
001dab20  LEA  RDI,[RSP + 0x28]   ; out buffer A
001dab27  CALL 0x006d4320        ; DecodePackedCoord(RSP+0x28, packedA)
                                  ;   -> [RSP+0x28]=plane_A [RSP+0x2c]=hi14_A [RSP+0x30]=lo14_A
001dab2f  CALL 0x00121a60        ; packedB = gT_unsigned_int(pkt)
001dab34  LEA  RDI,[RSP + 0x34]   ; out buffer B
001dab3b  CALL 0x006d4320        ; DecodePackedCoord(RSP+0x34, packedB)
                                  ;   -> [RSP+0x34]=plane_B [RSP+0x38]=hi14_B [RSP+0x3c]=lo14_B
...
001dadac  MOV  R8D,[RSP + 0x38]   ; hi14_B
001dadb1  MOV  ECX,[RSP + 0x30]   ; lo14_A
001dadb5  MOV  EDX,[RSP + 0x2c]   ; hi14_A
001dadb9  MOV  RDI,[RDI+0x19558]
001dadc0  SHR  R9D,0x6           ; arg4 = lo14_B >> 6   (logical shift)
001dadce  SHR  R8D,0x6           ; arg3 = hi14_B >> 6
001dadd2  SHR  ECX,0x6           ; arg2 = lo14_A >> 6
001dadd5  SHR  EDX,0x6           ; arg1 = hi14_A >> 6
001dade7  CALL 0x00c56160        ; SceneManager build  (EDX,ECX,R8D,R9D = the 4 region bounds)
```

`R9D` was loaded from `[RSP+0x3c]` (`lo14_B`) just before this block. So the four
build args, in order, are **`(hi14_A>>6, lo14_A>>6, hi14_B>>6, lo14_B>>6)`** —
i.e. `(minRegionX, minRegionZ, maxRegionX, maxRegionZ)`. The shifts are logical
(`SHR`), confirming the fields are unsigned.

These four ints flow `FUN_00c55e80 → BuildArea ctor FUN_00643d10` (form `3`) and
are stored verbatim (see §3). The **absolute** world position of the *rendered
scene* travels separately in `centreZoneX/Z` (header +4 BE / +1,+2 LE), turned
into local tiles by `(centreZone − camGridBase>>4) * 8` (`@0x001dabde–0x001dabe4`);
that positions the ~scene window *inside* the larger build-area grid and is
independent of packedA/B.

---

## 3. What the four numbers MEAN — the build-area map-square grid

The four region bounds are stored into the BuildArea object by the ctor
`FUN_00643d10 @0x00643d10` (form=3), unmodified:

```c
  *(int *)((long)this + 0x13fb4) = param_10;   // arg1 = minRegionX
  *(int *)((long)this + 0x13fb8) = param_11;   // arg2 = minRegionZ   (== this+0x27f7*8)
  *(int *)((long)this + 0x13fbc) = param_12;   // arg3 = maxRegionX
  *(int *)((long)this + 0x13fc0) = param_13;   // arg4 = maxRegionZ   (== this+0x27f8*8)
```

**Field semantics proven by the grid indexer `FUN_0061c2c0 @0x0061c2c0`**
(returns `&grid[X][Z]`, or the NULL sentinel `&DAT_0139fd90` if out of range):

```
0061c2c0  SUB EDX,[RDI + 0x13fb8]   ; localZ = (Z>>6) - minRegionZ
0061c2c6  SUB ESI,[RDI + 0x13fb4]   ; localX = (X>>6) - minRegionX
0061c2cc  JS  reject                ; localX < 0 -> reject
0061c2d0  JS  reject                ; localZ < 0 -> reject
0061c2d2  MOV R8,[RDI + 0x14000]    ; grid column array base
0061c2d9  MOV RCX,[RDI + 0x14008]   ; grid column array end
          ... numColumns = (end-base)/0x18 ; reject if localX >= numColumns
          ... per-column: numRows = (col.end-col.base)/0x18 ; reject if localZ >= numRows
0061c32d  RET                       ; -> &grid[localX][localZ]   (cell stride 0x18)
```

…and by the bound-check accessor `FUN_004d04f0 @0x004d04f0` (a virtual
`getTileData(this, X, Z)`):

```
004d0501  CMOVS ESI,EAX            ; X += 0x3f if negative (round toward 0 before >>6)
004d0504  SAR ESI,0x6             ; X >> 6  (region X)
004d0507  CMP ESI,[RDI+0x13fb4]    ; if  X>>6 < minRegionX  -> miss
004d050f  CMP ESI,[RDI+0x13fbc]    ; if  X>>6 > maxRegionX  -> miss
004d051f  SAR EDX,0x6             ; Z >> 6  (region Z)
004d0522  CMP EDX,[RDI+0x13fb8]    ; if  Z>>6 < minRegionZ  -> miss
004d052a  CMP EDX,[RDI+0x13fc0]    ; if  Z>>6 > maxRegionZ  -> miss
004d0532  CALL 0x0061c2c0         ; in-bounds: index the grid cell
```

So, definitively:

| BuildArea field | value (prod) | meaning |
|---|---|---|
| `+0x13fb4` | 26  | **minRegionX** — SW/origin corner X, in map-squares (`hi14_A >> 6`) |
| `+0x13fb8` | 37  | **minRegionZ** — SW/origin corner Z (`lo14_A >> 6`) |
| `+0x13fbc` | 72  | **maxRegionX** — NE/far corner X (`hi14_B >> 6`) |
| `+0x13fc0` | 142 | **maxRegionZ** — NE/far corner Z (`lo14_B >> 6`) |

- The grid is **inclusive** on both ends: a region `r` is inside iff
  `minRegion ≤ r ≤ maxRegion` on both axes.
- Grid dimensions = `(maxRegionX − minRegionX + 1)` columns ×
  `(maxRegionZ − minRegionZ + 1)` rows of map-square cells (24-byte cells).
- A coordinate is converted to a region by **`tile >> 6`** (a map-square = 64
  tiles = 8 zones). The `>>6` is the *only* transform; there is no `<<6`
  anywhere in the consume path.

**The unit is the map-square / region (`tile >> 6`), NOT the zone (`tile >> 3`).**
The packed fields hold *tile* coordinates; the client divides by 64 to index.

### 3.1 The build-area bounds are the ADDRESSING extent, not the streamed set

This is the correction. There are **two distinct region rectangles** in
`SceneState`, and the prior doc conflated them:

| rectangle | fields | set by | role |
|---|---|---|---|
| **build-area bounds** | `+0x13fb4/b8/bc/c0` | op81 packedA/B → ctor `@0x00643d10` | grid ALLOCATION extent — what region ids the grid *can* address |
| **camera-frustum span** | `+0x620/624/628/62c/630/638` | per-frame, `FUN_0025bcb0 @0x0025f4xx` from eye pos `+0x570` | the small set of regions actually streamed + rendered |

**Evidence the streamer/renderer iterate the FRUSTUM, not the build bounds.**
In the per-frame scene processor `FUN_0025bcb0`, the cell-walk loop is bounded by
the frustum fields and indexes the **build grid** `+0x14000`:

```
0025d3d7  MOV EBX,[R15 + 0x638]   ; loopMaxX = frustum max X     (NOT +0x13fbc)
0025d3de  MOV EBP,[R15 + 0x630]   ; loopMinX = frustum min X     (NOT +0x13fb4)
0025d3ec  MOV R13D,[R15 + 0x620]  ; frustum Z anchor
0025d409  SHL EBX,0x6            ; regionIndex << 6 = tile  (region<->tile = ×64)
0025d41f  MOV ECX,[R15 + 0x628]   ; loopMinZ = frustum min Z
0025d442  MOV RAX,[R14 + 0x14008] ; index the build grid (vector<vector<cell>>)
0025d449  MOV RDX,[R14 + 0x14000] ;   grid[frustumRegion - buildMin]…
```

And the frustum fields are derived from the camera/eye position each frame
(`FUN_0025bcb0 @0x0025f48d–0x0025f5d0`):

```
0025f48d  MOV R14D,[R15 + 0x570]  ; eye/camera grid position
0025f4ac  SHL EBX,0xf            ; << 15  (grid → sub-tile fixed point)
...        (float frustum math, MULSS/SUBSS with camera constants)
0025f547  CVTTSS2SI R13D,XMM6     ; frustum corner -> int
0025f55e  MOV [R15 + 0x624],R13D  ; frustum bound
0025f57d  MOV [R15 + 0x634],ECX   ; camera-target Z   (the stream target triple)
0025f5a6  MOV [R15 + 0x638],ESI   ; camera-target X
0025f5b3  MOV [R15 + 0x620],EAX   ; ...
0025f5d0  MOV [R15 + 0x630],EAX
```

So: op81's packedA/B set how big the grid *is*; the **camera** decides what
loads. A 47×106 build area only needs the player's region to be *inside* it (so
the grid can address the camera's squares); the engine never tries to load all
4982 cells. **This is the resolution of the "impossible 47×106" — it is an
addressing extent, and only the camera-frustum subset streams.**

---

## 4. The map-square LOAD TRIGGER (deliverable 3 — CORRECTED)

> **The prior claim — "valid bounds containing the player are sufficient; no extra
> packet" — is WRONG and is the actual bug.** Valid bounds are NECESSARY but not
> SUFFICIENT. The index-5 requests come from a per-frame camera-driven streamer
> that op81 does not arm.

### 4.1 The real emit path

`m{X}_{Z}` / `l{X}_{Z}` map groups (archive 5) are requested by the streamer
**`FUN_00647fc0`**, driven from the world tick:

```
jag::ConnectionManager::LoginProtocolHandler @0x0024e8c0   (the per-frame world/connection tick)
  ├─ @0x0025074a  SceneManager build  FUN_00c56160(..., 0,0, 0x62,0xc6)   ; ensure scene/grid
  └─ @0x002507f3  iVar = FUN_00647fc0(grid, centreZoneX, camFloatXZ, SceneState+0x690, 0)
                     │   walks build bounds +0x13fb4..c0, RANGE-GATES each square against the
                     │   camera float position (@0x006486a6 CMP camRegion vs bounds), and for
                     │   near squares calls FUN_00692600 (push_unique into the load queue at
                     │   SceneState+0x101a8 / +0x11848).  Returns load-progress 0..100.
                     └─ if (iVar < 100) return;   // still streaming → re-enter next frame
```

`FUN_00692600 @0x00692600` is a `vector<long>::push_back_if_absent` — it
accumulates the **unique set of map-square keys to fetch**; servicing that queue
issues the JS5 index-5 `GetFile(MAPS, group=mapSquareKey)` calls. (NXT addresses
map groups by computed group id, not a formatted `m%d_%d` string — there is no
such format string in 948-5; confirmed.) Per-cell scene build then runs via
`FUN_00619710 @0x00619710` as each cell's `cell+8` is populated.

### 4.2 The gate — ALL of these must hold for `FUN_00647fc0` to run

From `LoginProtocolHandler @0x0024e8c0` (decompile `@0x002507xx` / `@0x00253008`)
and the world tick `FUN_0023e660 @0x0023e660` (`@0x00242784`, `@0x00243006`):

```c
// SceneState = *(client + 0x19420)   (the op81 target object; same one op81 writes)
if ((SceneState->readyFlag_0x650 &
     (SceneState->camTargetX_0x638 != -1 && SceneState->camTargetZ_0x63c != -1)) == 0) return;
if (SceneState->camAnchor_0x634 == -1) return;
if (SceneState->sceneState_0x418 != 6 /* RENDER */) { /* skip render+stream */ }
```

| gate field | meaning | set by |
|---|---|---|
| `+0x418 == 6` | scene/world in **RENDER** state | world login/render state machine (op81 returns early if `==6`) |
| `+0x650 == 1` | **map-ready** flag | world tick `FUN_0023e660 @0x00242612`, only when the build/asset-job queues `+0x40==+0x48` and `+0x90==+0xb0` (i.e. the initial terrain/loc decode jobs finished) |
| `+0x634/+0x638/+0x63c != -1` | **camera target / anchor** (the tile to centre the stream on) | `FUN_0025bcb0 @0x0025f57d/+0x5a6` from the eye position `+0x570` |

`+0x638/+0x63c` are **tile** coords (the lookup at `@0x00253029`
`FUN_00b14520(.., +0x638>>6, +0x63c>>6)` `>>6`s them to a region). They are
computed from the **camera/eye**, which only exists once the local player entity
is placed and the camera is bound to it.

### 4.3 Why darkan gets ZERO index-5 requests (the real reason)

darkan's op81 now decodes to a **valid, non-inverted, player-containing** build
area (the §5.4 fix landed — `BuildArea.kt`). The grid allocates fine. But the
client still streams nothing because **the camera/render preconditions in §4.2
are never satisfied:**

- The **eye/camera is never positioned.** `+0x570` (and the player entity it
  tracks) is not established, so `FUN_0025bcb0` never computes a valid camera
  target → `+0x634/+0x638/+0x63c` stay `-1` → the §4.2 gate fails → `FUN_00647fc0`
  is never called → `FUN_00692600` enqueues nothing → zero JS5 index-5 requests.
- The **map-ready flag `+0x650`** likely also never flips, because the build/
  asset-job queues never receive work to drain (no squares enqueued).
- The **scene state `+0x418`** must reach `6` (RENDER); if the world-login state
  machine doesn't advance the client past the lobby/loading state, the render+
  stream block is skipped entirely.

In short: **op81 builds the empty grid container and parks the camera target at
-1.** What arms the stream is the **camera being positioned by the world-entry
sequence** — the local player's position (GPI / `op22` local tile), the camera
bind, and the render-state transition. There IS effectively a "go": it is not a
single packet but the **render-readiness state** (`+0x418 == 6` + a positioned
camera), driven by the player-info / world-entry flow, not by op81's bytes.

### 4.4 What darkan must do (handoff to networking-protocol-engineer)

Sending correct packedA/B is **half** the fix. To make the client emit index-5
requests, the world-entry sequence must also drive the client to **RENDER state
with a positioned camera**:

1. Keep the valid build area (§5) — necessary, already done.
2. Ensure the **local player is placed** so the client binds the camera: the
   first PlayerInfo/GPI must carry the player's absolute tile (`op22` local-tile
   prefix or the GPI init bitstream), coherent with op81's `centreZoneX/Z` and
   the build-area bounds (all three must agree on the spawn — world-bootstrap §4).
3. Drive the **render-state transition** to `+0x418 == 6`. This is the
   lobby→world "you are now in the world" handshake; trace the world-login
   response sequence (`docs/protocol/lobby-world-switch-948.md` §9) — the client
   advances its scene state on that, not on op81.
4. Once the camera is positioned and the state is RENDER, the **existing**
   per-frame streamer (`FUN_00647fc0`) requests the camera-frustum squares with
   no further server action. *That* is the only sense in which "no extra packet"
   is true — but it is downstream of camera readiness, not of op81's bounds.

> **Open precision item — RESOLVED (2026-06-23), see
> `docs/protocol/world-login-camera-render-948.md`.** The field that flips `+0x418`
> to `6` is `worldState+0x418`; the writer is `jag::game::Camera::ProcessCameraReset
> @0x001444e0` (final line: `+0x418 = anchorByte!=0 ? 6 : 1`); the trigger is **op81's
> own tail** (`@0x001daa70`, gated on `+0x418 != 6` at entry). **The camera is
> positioned by op81 itself, not by a separate C2S/S2C exchange and not by op22/op77**
> (op77 `CAM_UPDATE @0x001d3d10` is a bitflag *modifier* of an already-reset camera).
> The camera-target triple `+0x634/638/63c` is then armed by the per-frame tick
> `@0x0023e660` from `GraphNode::GetWorldTranslation(cameraNode)` — the node
> ProcessCameraReset positioned. darkan's remaining gaps: op81 ships
> `cameraRotation=0` (prod=7) and op75 is deferred — both fixed in that doc §6. This
> doc proves the *streamer gate*; the camera/state arming is now proven there.

### 4.5 op75 SET_READY_FLAG is a SEPARATE gate — do NOT confuse it with the streamer gate

`world-entry-render-948.md` documents `SET_READY_FLAG (op75) @0x00175120` as "the
render gate." That is a **different, complementary** gate from the streamer gate
(§4.2), on a **different object** — important so the two docs don't read as
contradictory:

```c
// op75 SET_READY_FLAG @0x00175120 (verified):
client->[0x194f8]->[0x10] = 1;                 // GLOBAL IsWorldReady flag (object +0x194f8)
client->[0x19520]->[0xf0] = (…->[0xe8])->[0x10]; // latch a frame/cycle counter
```

| gate | object | flips | who sets it | what it unblocks |
|---|---|---|---|---|
| **global IsWorldReady** | `client+0x194f8` `+0x10` | op75 (server, sent LAST) | the server, explicitly | final render *visibility* (client stops gating frames on `IsWorldReady`) |
| **SceneState streamer gate** (§4.2) | `client+0x19420` (`SceneState`) `+0x418`/`+0x650`/`+0x634..63c` | the engine, from camera + asset-jobs | nobody sends it — it's internal | the map-square **JS5 index-5 requests** (`FUN_00647fc0`) |

**Key consequence for darkan:** op75 is **not** what makes map squares load. The
streaming gate (§4.2) is internal and depends on the camera being positioned and
the initial decode jobs running. So:

- darkan correctly **defers op75** (its `WorldServer.kt` comment is right that op75
  must be LAST, after the world is built — sending it early makes the client
  render an empty scene and bail).
- **But the inverse claim in that same comment — "a non-inverted op81 build area …
  the streaming loop then pulls index-5 with no extra 'go' packet" — is the §4
  error.** The streaming loop only runs once the SceneState streamer gate (§4.2)
  is satisfied, which requires the **camera to be positioned** (player placed +
  camera bound), independent of both op81's bounds and op75.
- The correct sequence is: valid op81 (done) → **place the local player so the
  camera binds** (PlayerInfo/GPI with the spawn tile) → the engine's per-frame
  streamer arms itself and pulls index-5 → build the rest of the world → **op75
  LAST** to lift the global render gate. op75 and the build area are necessary;
  neither is the thing that *starts* the map streaming.

---

## 5. Production validation + the VERIFIED encode recipe (deliverable, ground truth)

### 5.0 Raw-capture verification (these are real production bytes)

The packedA/B values are **not** transcribed from a doc — they are read directly
from the raw production op81 capture `build/prod-op81-rebuild.bin` (5137 bytes, a
live `production-isaac` world-login session). The 18-byte coord header is the
**last** 18 bytes of the packet (the GPI bitstream precedes it):

```
bytes[5119..5136] = ff 94 01 85 01 94 87 00 01 da 01 a0 09 40 04 8e 23 b8
  +0  0xff
  +1,+2  centreZoneZ (LE u16) = 0x0194 = 404        ✓ (player zone Z)
  +3   magic = 0x85                                  ✓ (REBUILD_NORMAL_SIMPLE gate, cVar4 == -0x7b)
  +4,+5  centreZoneX (BE u16) = 0x0194 = 404         ✓ (player zone X)
  +6   camera byte = 0x87
  +7,+8,+9 = 00 01 da   (local_6a = BE u16 @+8 = 0x01da, camera/locality lookup)
  +10..13  packedA (BE u32) = 0x01a00940
  +14..17  packedB (BE u32) = 0x048e23b8
```

centreZone (404,404) ⇒ player region (50,50); decode below confirms the player is
inside the build area. All values cross-check.

### 5.1 Decoding production `packedA=0x01a00940`, `packedB=0x048e23b8`

```
packedA = 0x01a00940
  plane = 0x01a00940 >> 28 & 3      = 0
  hi14  = 0x01a00940 >> 14 & 0x3FFF = 1664   (= 0x680)   -> minRegionX = 1664 >> 6 = 26
  lo14  = 0x01a00940       & 0x3FFF = 2368   (= 0x940)   -> minRegionZ = 2368 >> 6 = 37

packedB = 0x048e23b8
  plane = 0x048e23b8 >> 28 & 3      = 0
  hi14  = 0x048e23b8 >> 14 & 0x3FFF = 4664   (= 0x1238)  -> maxRegionX = 4664 >> 6 = 72
  lo14  = 0x048e23b8       & 0x3FFF = 9144   (= 0x23B8)  -> maxRegionZ = 9144 >> 6 = 142
```

**Build-area grid bounds: regions X∈[26,72], Z∈[37,142]** (inclusive) — a
`47 × 106` map-square grid.

**Coherence check against the player.** The spawn (Lumbridge, tile ≈ (3232,3234),
which is `centreZoneX/Z = 404` ⇒ region `3232>>6 = 50`, `3234>>6 = 50`, also the
op81 GPI prefix's local tile `0x0328CCA2 = (0,3235,3234)` ⇒ region (50,50)):

```
  region (50,50) inside X[26..72]? 26 ≤ 50 ≤ 72  ✓
                        Z[37..142]? 37 ≤ 50 ≤ 142 ✓
```

The player's region sits **well inside** the build-area grid (asymmetric:
24 W / 22 E, **13 S / 92 N**). So the grid can *address* the camera's squares —
but rendering also requires the camera to be positioned and the scene to reach
RENDER state (§4). Production renders because BOTH hold: a containing build area
AND a positioned camera + render-state world-login. The build area alone does not
render — that was the prior doc's error.

> **Note on the window shape — and why it is large.** Production's grid is large
> and *not* centred on the player. The 92-region northward span is the live
> Jagex server shipping a **generous active-region extent** for the Lumbridge
> landmass — an ADDRESSING extent, not a streaming window (§3.1). The corners are
> **independent**; there is no "player ± half-window" relation baked into
> packedA/B. The only hard wire constraints are `minRegion ≤ playerRegion ≤
> maxRegion` and `minRegion ≤ maxRegion`. The low 6 bits of each field are
> **ignored** by the build path (`region = field>>6`): production's near corner
> has low6 = 0 (`1664 & 0x3F = 0`, `2368 & 0x3F = 0`); the far corner has
> low6 = 0x38 (`4664 & 0x3F = 56`, `9144 & 0x3F = 56`). Encode either way — only
> bits `[6..13]` of each field (the region) matter. **darkan does not need to
> reproduce this large asymmetric box; a small containing window is fine** (§5.4)
> because only the camera-frustum subset ever streams.

### 5.2 ENCODE — the inverse (compute packedA / packedB)

```
pack(plane, xTile, zTile):
    return ((plane & 3) << 28) | ((xTile & 0x3FFF) << 14) | (zTile & 0x3FFF)

# Given the four region bounds you want the build-area grid to span:
#   minRegionX, minRegionZ  (SW / origin corner)
#   maxRegionX, maxRegionZ  (NE / far corner),  with min ≤ max on both axes
# convert each region to a tile (region << 6) and pack:
packedA = pack(plane, minRegionX << 6, minRegionZ << 6)   # SW corner, tile-aligned
packedB = pack(plane, maxRegionX << 6, maxRegionZ << 6)   # NE corner, tile-aligned
```

`plane` is the scene base level (0 for the overworld). Because the client only
uses `field >> 6`, you may pack `region << 6` (low6 = 0, the clean form) and the
grid bounds come out exactly `[minRegion .. maxRegion]`. (To byte-match
production's far-corner low bits you would `(maxRegion << 6) | 0x38`, but that is
cosmetic — the loaded grid is identical.)

### 5.3 VERIFIED round-trip (reproduces production exactly)

Production's near corner is region (26,37) tile-aligned → `pack(0, 26<<6, 37<<6)
= pack(0, 1664, 2368) = 0x01a00940` ✓. The far corner is region (72,142) with the
`+0x38` low bits → `pack(0, 72<<6 | 0x38, 142<<6 | 0x38) = pack(0, 4664, 9144) =
0x048e23b8` ✓. Round-trip confirmed against the brief's ground-truth values:

```
pack(0, 1664, 2368)            -> 0x01a00940   (target 0x01a00940)  ✓
pack(0, 4664, 9144)            -> 0x048e23b8   (target 0x048e23b8)  ✓
  where 4664 = (72<<6)|0x38,  9144 = (142<<6)|0x38
```

For darkan you do **not** need the `|0x38`; use the clean tile-aligned form:

```
pack(0, minRegionX<<6, minRegionZ<<6)   # packedA
pack(0, maxRegionX<<6, maxRegionZ<<6)   # packedB
```

### 5.4 darkan's encoder: the bounds bug is FIXED; the trigger gap REMAINS

**History.** darkan originally did `packedA = packZoneCoord(394,394)`,
`packedB = packZoneCoord(13,13)` with `packZoneCoord(z) = (z<<6) & 0x3FFF` — three
compounding defects (wrong `zone<<6` model; 14-bit overflow for zone ≥ 256;
inverted `X[138..13] Z[138..13]` bounds → empty grid). That has been **corrected**:
`world/.../world/BuildArea.kt` now uses the §5.2 corner encoder
(`pack(plane, region<<6, region<<6)`), picks a small symmetric window
(`BuildAreaSize.MEDIUM` = ±2 regions about the spawn region), and asserts
`min ≤ max`. **The bounds are now valid and non-inverted.** Confirm:

```
spawn region (50,50), MEDIUM (R=2):
  minRegion (48,48)  maxRegion (52,52)
  packedA = pack(0, 48<<6, 48<<6) = pack(0, 3072, 3072) = 0x03000c00
  packedB = pack(0, 52<<6, 52<<6) = pack(0, 3328, 3328) = 0x03400d00
  decode: X[48..52] Z[48..52], player region 50 inside  ✓  (a clean 5×5 grid)
  (pack: ((3072 & 0x3FFF) << 14) | (3072 & 0x3FFF) = (0x0C00 << 14) | 0x0C00)
```

**The remaining black screen is NOT the bounds.** It is the §4 trigger gap: op81
builds a valid grid container, but the **camera/render preconditions** (§4.2) are
never met, so the per-frame streamer `FUN_00647fc0` never enqueues squares and
**zero index-5 requests** are emitted. The fix from here is **render-readiness**,
not packed-coord encoding (§4.4): position the local player so the camera binds
(`+0x570`/camera target triple), and drive the scene to RENDER state
(`+0x418 == 6`) via the world-login response sequence.

**Encode recipe (still required, necessary half).** Keep the §5.2 corner encoder.
For a solo first-light:

```
spawnRegionX = spawnTileX >> 6        # e.g. 3232 >> 6 = 50  (== zone 404 >> 3)
spawnRegionZ = spawnTileZ >> 6
R = halfWindowInRegions               # >= 1; MEDIUM=2 is fine
minRegionX = max(0, spawnRegionX - R) ; maxRegionX = spawnRegionX + R
minRegionZ = max(0, spawnRegionZ - R) ; maxRegionZ = spawnRegionZ + R
packedA = pack(0, minRegionX<<6, minRegionZ<<6)
packedB = pack(0, maxRegionX<<6, maxRegionZ<<6)
centreZoneX = centreZoneZ = spawnTile>>3     # op81 +4 / +1,+2 — the scene centre
```

> **On window size.** Production used a large asymmetric 47×106 box (the live
> server's active extent); you do **not** need to reproduce it. A small symmetric
> window (R=2..4) that contains the spawn region is sufficient — only the
> camera-frustum subset streams (§3.1), so the box size barely matters for load
> cost. The grid allocation (`@0x00643d10`: fixed 0x90000-byte scratch + a column
> array sized `(maxX-minX+1)`) is the only cost of a big box; keep it modest.
>
> **Do NOT expect a small valid box to fix the black screen by itself** — it
> won't, until the §4 camera/render preconditions are also satisfied. The bounds
> are necessary, not sufficient.

---

## 6. Field-offset reference (948-5 `BuildArea`)

| offset | type | name | source |
|---|---|---|---|
| `+0x13fb4` | int | `minRegionX` (SW corner X, map-squares) | ctor `@0x00643d10`, indexer `@0x0061c2c0`, accessor `@0x004d04f0` |
| `+0x13fb8` | int | `minRegionZ` (SW corner Z) | same |
| `+0x13fbc` | int | `maxRegionX` (NE corner X) | same |
| `+0x13fc0` | int | `maxRegionZ` (NE corner Z) | same |
| `+0x13ff0` | int | grid X-span `(maxRegionX-minRegionX)` | ctor `@0x00644d0f` |
| `+0x13ff4` | int | grid Z-span `(maxRegionZ-minRegionZ)` | ctor `@0x00644d01` |
| `+0x14000` | ptr | grid column-array `begin` (vector<vector<Cell>>) | indexer `@0x0061c2c0` |
| `+0x14008` | ptr | grid column-array `end` | indexer `@0x0061c2c0`, resize `@0x00619710` |

**Camera-frustum + stream-target fields (the per-frame streaming inputs — NOT
from packedA/B). These are what actually drive index-5 requests (§3.1, §4):**

| offset | type | name | source |
|---|---|---|---|
| `+0x418` | int | **scene/world state enum** (`6` = RENDER) | op81 `@0x001dae81`, tick `@0x00243014` |
| `+0x570` | int | **eye/camera grid position** (`<<15` → sub-tile) | `FUN_0025bcb0 @0x0025f48d` |
| `+0x620`..`+0x638` | int×6 | **camera-frustum region span** (the loop bounds the streamer/renderer actually walk) | `FUN_0025bcb0 @0x0025f4xx`; loop `@0x0025d3d7` |
| `+0x634` | int | **camera-target anchor** (≠ -1 required) | set `FUN_0025bcb0 @0x0025f57d`; gate `@0x002506ae` |
| `+0x638` | int | **camera-target X (tile)** (`>>6` = region; ≠ -1 required) | set `@0x0025f5a6`; lookup `FUN_00b14520 @0x00253029` |
| `+0x63c` | int | **camera-target Z (tile)** (≠ -1 required) | set `@0x0025f5xx` |
| `+0x650` | byte | **map-ready flag** (`1` required) | set tick `FUN_0023e660 @0x00242612` when build/asset-job queues drain |
| `+0x690` | — | per-frame stream scratch passed to `FUN_00647fc0` | tick `@0x002507f3` |

Each build-grid cell is `0x18` bytes; `cell+0x8` holds the loaded map-square
object pointer (NULL until the region's JS5 maps group finishes loading). Column
count = `(maxRegionX-minRegionX)+1`, rows = `(maxRegionZ-minRegionZ)+1` (allocated
`@0x00643d10`, `iVar28 = +0x13fbc − +0x13fb4`, `uVar24 = iVar28 + 1`).

Key functions:

| addr | role |
|---|---|
| `0x006d4320` | `jag::game::BuildArea::DecodePackedCoord(uint* out, uint packed)` — byte decode (verified) |
| `0x001daa70` | `REBUILD_NORMAL_SIMPLE` (op81 handler) — reads header + both packed words, builds grid |
| `0x00121a60` | `Packet::gT_unsigned_int` (BE u32 read used for packedA/B) |
| `0x00c56160` | `SceneManager` build entry (`FUN_00c55e80`'s real target; takes the 4 region bounds). Also called from `LoginProtocolHandler @0x0025074a` (corners 0,0,98,198) and `FUN_00492ce0` |
| `0x00643d10` | `BuildArea` ctor (form=3) — stores the 4 bounds, allocates the grid. Also called from `REBUILD_REGION_ALT @0x001dfc8c` |
| `0x0061c2c0` | grid indexer `getCell(this, X, Z)` → `&grid[X−minX][Z−minZ]` (X/Z already region) |
| `0x004d04f0` | bound-check tile accessor (`tile>>6` vs bounds; NULL sentinel when out of bounds) |
| **`0x00647fc0`** | **map-square STREAMER** — walks build bounds, range-gates vs camera, `push_unique`s near squares, returns load-progress 0..100. Called from `LoginProtocolHandler @0x002507f3` and `FUN_0016b680` |
| **`0x00692600`** | `vector<long>::push_back_if_absent` — the map-square load-key queue accumulator |
| **`0x0024e8c0`** | `jag::ConnectionManager::LoginProtocolHandler` — the per-frame world/connection tick that drives the streamer + scene build (gated by §4.2) |
| **`0x0023e660`** | world tick (`float` time param) — sets the map-ready flag `+0x650`, gates on the camera-target triple + `+0x418==6` |
| `0x0025bcb0` | per-frame scene processor — computes the camera-frustum span + target triple from the eye pos (`+0x570`), walks the frustum span, indexes the grid |
| `0x00619710` | per-cell grid visitor (runs scene build for each loaded map-square) |
| `0x00b14520` | region lookup by `(camTargetX>>6, camTargetZ>>6)` — resolves the camera-target region |

---

## 7. Reconciliation with `alerion-buildarea-reference-910.md` (910 shape vs 948-5 bytes)

The alerion 910 reference is correct on **shape** and matches 948-5 conceptually:
the build area is the gating set for all spatial data; the client JS5-pulls each
map square itself; the scene origin is the SW corner. But its **byte format does
not apply to 948-5** — and it says so (its line 100: "DO NOT COPY
`getRegionKey()`'s packing `(x>>6)|((z>>6)<<7)`"; its line 145: "948-5 … has its
own packed-coord scheme `BuildArea::DecodePackedCoord`"). Concrete differences:

| aspect | alerion 910 | **948-5 (this doc, authoritative)** |
|---|---|---|
| coord packing | `(x>>6) \| ((z>>6)<<7)` region key | `(plane<<28) \| (xTile<<14) \| zTile`, then `>>6` per field |
| build-area model | player-centred window of a `BuildAreaSize` (104/120/136/168/72/256 tiles); origin chunk `= max(0, chunk − size/16)` | **two explicit corners** (min/max region) carried in packedA/B; window need not be centred or a fixed size |
| field unit | chunks (8 tiles) in 910's scene fields | **map-squares / regions (64 tiles)** for the packedA/B grid bounds; the *render scene* uses zones/tiles separately (§2) |
| who derives bounds | server computes from size + centre | server computes both corners and ships them; client just `>>6`s and grid-indexes |

**Use alerion only for the service skeleton** (one routine computes the build area
from player position, resets `mapRegions`, and emits op81; track a scene origin).
**Take every byte/bit/opcode/unit from this 948-5 doc.** Where they conflict,
948-5 wins.

---

## 8. Residual unknowns / warnings

- **THE primary open item — render-state arming (blocks the black screen).** This
  doc proves the *streamer gate* (§4.2: `+0x418==6`, `+0x650==1`, camera-target
  triple ≠ -1, eye positioned). What it does **not** fully pin is the **minimal
  S2C/C2S sequence that flips `+0x418` to `6` and positions the camera/eye
  (`+0x570`)** — i.e. the lobby→world "you are now rendering" transition. That is
  owned by the world-login RE (`lobby-world-switch-948.md` §9 +
  `world-bootstrap-948.md`: the PlayerInfo/GPI that places the local player, the
  camera bind, and the state advance). **This is the actual next step to make
  darkan load map squares** — the build area (this doc) is already correct.
- **Window-size policy for darkan.** 948-5 accepts any `min ≤ playerRegion ≤ max`
  rectangle; production used a large asymmetric one because the live server ships
  a generous active extent (§3.1). The *exact* server-side rule is **not encoded
  in the client** — only the corners are consumed. darkan should pick a small
  centred window (§5.4); policy choice, not client-dictated. **Window size does
  not affect the trigger** (only the camera-frustum subset streams).
- **plane (out[0]) usage.** Both production words have plane 0. The plane bits
  feed the scene base level; for multi-level builds set them to the scene's base
  plane. Not exercised by the Lumbridge capture.
- **Map-square group id derivation.** The precise region-id → JS5-archive-5-group-id
  mapping the streamer uses (the `m…`/`l…` group ids) lives in the cache-side maps
  index, owned by the cache library, unchanged from the standard NXT region scheme
  (`groupForMap(regionX, regionZ)` in index 5). Out of scope here.
- **`centreZoneX/Z` vs packedA/B.** Keep them coherent: `centreZone` = spawn
  **zone** (`tile>>3`), packedA/B corners in **region** (`tile>>6`) units must
  contain `centreZone>>3`. world-bootstrap §4's coherence rules still apply (the
  GPI local tile, the op81 centre zone, and the build-area bounds must all agree
  on the spawn). Note op81 ALSO sets the scene centre via `FUN_00143fa0` (writes
  `SceneState+0x608` from the centre-zone-derived local tile), but that is the
  render-scene centre, distinct from the camera-target triple `+0x634/638/63c`
  that gates streaming.
- **Production capture is a world-LOGIN (not a fresh cold spawn).** The 47×106 box
  is what the **live Jagex server** sent to a Lumbridge world-login
  (`production-isaac` session). A fresh darkan spawn legitimately differs — a
  small centred box is correct and sufficient (§5.4). Do not chase reproducing
  production's exact asymmetric corners.
