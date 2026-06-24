# RS3 NXT 948-5 — World-login camera positioning & the map-stream gate (DEFINITIVE)

> ## ⚠️ CORRECTION (2026-06-23) — the anchor is NOT `cameraRotation`; Shape A makes op81 ABORT before its tail
>
> §6/§8 below honestly flagged that it could **not** statically prove the wire
> `cameraRotation` byte feeds the `ProcessCameraReset` anchor. **It does NOT.** The
> anchor `*(*(client[0x18cb8])+0x240)+0xd8` is resolved (`world-bootstrap-948.md`
> §0 correction, 2026-06-23): it is a **map-config flag decoded from the spawn
> region's cache file** (`MapSquare::LoadFiles @0x005c3fe0` → `+0xd8=1` on map opcode
> 0x10), stored to `ConfigProvider+0x240`. The op81 wire `cameraRotation` byte goes
> to build-state `+0x428` (`@0x001dab06`) — a DIFFERENT field. **Fix A (set
> cameraRotation=7) is therefore NOT the lever it was hoped to be**; matching
> production's `7` is harmless but does not arm the anchor.
>
> **The REAL keystone:** op81 requires a **GPI prefix** in its body on world entry.
> The handler checks `worldState+0x49` (set to 1 by the state→30 transition,
> `REBUILD_REGION_HANDLER @0x000f7036`) and, when set, calls the GPI-prefix parser
> `FUN_00b254a0 @0x001dab87` to consume the prefix (placing the local player +
> initialising 2046 other slots) BEFORE reading the 18-byte coord header. With
> darkan's current **Shape A** (empty prefix, 18-byte body), the parser over-reads
> 5119 bytes from an 18-byte buffer (`gBit @0x0013e970` has no bounds check),
> corrupts the cursor to 5119, and the handler reads the coord header out-of-bounds
> → **magic ≠ 0x85 → op81 returns `&DAT_015d35c0` at `@0x001dab4d`, BEFORE the
> BuildArea alloc (`@0x001dade7`) and BEFORE `ProcessCameraReset` (`@0x001daea8`).**
> So the camera reset / `+0x418=6` advance this doc describes **never runs at all**
> under Shape A — independent of `cameraRotation` or `op75`. The fix is to ship the
> GPI prefix (Shape B), format in `world-bootstrap-948.md` §0. Everything below
> about the streamer gate, `+0x418`, and op75 remains correct *once op81 reaches its
> tail* — which requires Shape B.
>
> ---

> **The question this answers.** darkan's client reaches the world, gets a coherent
> op81 build area, yet renders a permanent black screen and issues **zero** index-5
> (`m{X}_{Z}`/`l{X}_{Z}`) map-square JS5 requests. The prior RE
> (`packed-coord-buildarea-948.md` §4) correctly identified that the per-frame
> map streamer is gated on a **positioned camera** (a camera-target triple
> `+0x634/638/63c != -1`) but left **"what positions the camera"** as the open item.
> This doc resolves it **from the binary**: the camera is positioned by **op81's own
> tail** (`ProcessCameraReset`), not by op22, op77, or a default. It reconciles the
> three prior docs that disagreed, and gives darkan the exact fix.
>
> **Authority.** Every `@0xADDR` and every decompiled line below is from the **948-5**
> Linux binary, read **headless** (`analyzeHeadless -process rs2client.948-5`,
> Ghidra 12.0.4, project `~/projects/reclass-data/rs2client-948`) — the live
> GhidraMCP bridge is down (port 8080 is OrbStack), so the headless path was used
> exactly as `world-entry-render-948.md` did. Cross-checked against the 0-desync
> production capture `build/undercut-socket-session-production-isaac.jsonl`
> (`conn=="world"`) and `build/prod-op81-rebuild.bin`.
>
> **Ghidra annotations committed** (read-only project, persisted): renames
> `FUN_0025bcb0 → jag::game::SceneState_ProcessFrustumAndStream`,
> `FUN_00647fc0 → jag::game::BuildArea_StreamMapSquares`,
> `FUN_0023e660 → jag::game::ClientWorld_PerFrameTick`, plus plate/pre comments on
> those + `Camera::ProcessCameraReset @0x001444e0`, op81 `@0x001daa70`, op77
> `CAM_UPDATE @0x001d3d10`, `FUN_00692600`.
>
> **Owner for implementation:** `networking-protocol-engineer`. **Do NOT** edit
> server source from RE — this is spec. Supersedes the "what positions the camera"
> open item in `packed-coord-buildarea-948.md` §4.3/§8 and the
> `world-entry-render-948.md` §0 claim that **op75 alone** renders.

---

## 0. TL;DR — the answers

1. **WHAT POSITIONS THE CAMERA (the key answer): op81 itself.**
   `REBUILD_NORMAL_SIMPLE @0x001daa70` (the op81 handler), at its **tail**, calls
   `jag::game::Camera::ProcessCameraReset(worldState, anchorByte)` — **unless**
   `worldState+0x418 == 6` already. That function positions the camera transform
   AND, on its **final instruction**, sets `worldState+0x418 = 6` (the RENDER value
   the streamer requires) when `anchorByte != 0`. **op22 (PLAYER_INFO) and op77
   (CAMERA_UPDATE) do NOT position the camera** — op77 is a bitflag *modifier* of an
   already-reset camera (§4). So the camera is positioned **server-side by sending a
   coherent op81 with a non-zero camera anchor**, not by any later packet.

2. **HOW SCENE-STATE REACHES RENDER (`+0x418 == 6`):** the **same**
   `ProcessCameraReset` call sets it. `+0x418` is a field on the world/Camera
   state object (`client[RELA 0xcf5]`); its last line is
   `worldState+0x418 = (anchorByte=='\0') ? 1 : 6`. op81 advances the scene to
   RENDER *itself* via this call. **No separate "go to RENDER" packet exists.** (The
   prior docs' "`+0x418` reached by a later state-machine packet" was wrong; it is
   reached *inside op81*.)

3. **HOW MAP-READY IS SET (`+0x650 == 1`):** the per-frame world tick
   `jag::game::ClientWorld_PerFrameTick @0x0023e660` sets `worldState+0x650 = 1`
   **only when the asset-job queue drains** (its read-ptr `+0x90` == write-ptr
   `+0xb0`, `@~0x242612`). It is internal; **no packet sets it**. It flips a frame
   or two after the initial terrain/loc decode jobs (which op81's BuildArea kicks
   off) complete.

4. **THE STREAMER GATE (verified, the gate `packed-coord §4.2` named — confirmed
   byte-for-byte):** `BuildArea_StreamMapSquares @0x00647fc0` enqueues map squares
   only when the world tick's gate passes:
   `(worldState+0x650==1) && (worldState+0x638!=-1 && worldState+0x63c!=-1) &&
   (worldState+0x634!=-1)`. The camera-target triple `+0x634/638/63c` is computed
   by the tick from `GraphNode::GetWorldTranslation(cameraNode)` — i.e. **from the
   camera node that ProcessCameraReset positioned.** And `@0x00647fc0` itself bails
   instantly (enqueues nothing) if the camera float position passed in is `{0,0,0}`.

5. **THE darkan FIX (the send-sequence):** darkan already sends a coherent op81, but
   with **`cameraRotation = 0`** (`WorldServer.kt:570`) and it **replays a
   production op77 blob** whose baked Lumbridge coords don't match darkan's spawn.
   The corrective send-sequence (§6):
   - Send op81 with a **non-zero `cameraRotation`** (production used **7**) and the
     coherent centre/build-area (already correct). *Necessary so the BuildArea
     orientation/anchor path is exercised; see the caveat in §6 about the true
     anchor source.*
   - **Send op75 `SET_READY_FLAG` LAST** (after the scene + GPI). darkan currently
     **defers op75** (`WorldServer.kt:410`, Step 15b) — that is the OTHER half of the
     fix. op75 is a *separate visibility gate* (clears the "Loading" sub-state); it
     is **not** the streamer trigger, but the client will not leave "Loading"
     without it.
   - **Drop / stop relying on the replayed op77 blob** for positioning — it does not
     position the camera and its coords are production-specific. Send op77 only as a
     real (or empty) camera command if at all.
   - Keep the GPI init (`buildInit`) placing the local player at the spawn tile —
     correct and needed (it places the player entity the camera/scene track).

   In short: **coherent op81 (with a non-zero camera anchor) positions the camera +
   advances to RENDER; the tick sets map-ready when the build jobs drain; the
   streamer then pulls index-5 with no further packet — and op75 (sent LAST) lifts
   the loading-screen visibility gate.** Two server actions are missing today:
   the **non-zero op81 camera anchor** and **op75**.

---

## 1. The op81 tail — the camera positioner (decompiled)

`jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE @0x001daa70`, tail
(headless decompile, verbatim):

```c
LAB_001dae48:
  FUN_00143fa0(worldState_for_form, 3, &local_68);   // recompute local window (form 3)
  worldState = client[RELA 0xcf5];                    // the world/Camera state object
  if (worldState->flag(+0x49) != 0) worldState->flag(+0x49) = 0;  // clear "rebuild pending"
  worldState = client[RELA 0xcf5];
  if (*(int *)(worldState + 0x418) == 6) {            // ALREADY in RENDER -> skip reset
    return &DAT_015d3620;
  }
  game::Camera::ProcessCameraReset(
        worldState,
        *(byte *)( *(long *)( client[RELA 0xca6] + 0x240 ) + 0xd8 )   // <-- anchorByte
  );
  return &DAT_015d3620;
```

- **The camera is reset/positioned here**, at the very end of op81, *after* the
  BuildArea is allocated (`FUN_00c55e80`, the `>>6` corner math — unchanged, see
  `packed-coord-buildarea-948.md` §2).
- **`anchorByte`** = `*(*(client[RELA 0xca6]) + 0x240) + 0xd8` — a byte read from a
  **scene-root object** (`client[RELA 0xca6]` → its `+0x240` sub-object → `+0xd8`).
  It is **NOT** the op81 wire `cameraRotation` byte (that byte is stored separately
  to build-state `+0x428`, `@0x001dab06`). This distinction matters for the fix
  (§6 caveat).
- **The skip gate** `if (+0x418 == 6) return` means: if the scene is *already* in
  RENDER, op81 does NOT re-reset the camera (it preserves an in-progress camera,
  e.g. a cutscene). On a fresh world-login `+0x418` is not 6, so the reset **runs**.

> **Reconciliation note.** `world-entry-render-948.md` §1.4 quoted this same tail
> and concluded "op81 alone does not clear loading." That is correct for the
> *loading-screen visibility* (a different field, `gameShell+0xd8`, §5) — but it
> *mis-framed* the camera reset as a no-op for rendering. **The camera reset is
> exactly what advances `+0x418` to 6 and positions the camera** (§2). Both docs
> were each half-right; this is the synthesis.

---

## 2. `ProcessCameraReset @0x001444e0` — sets the camera AND `+0x418 = 6`

`jag::game::Camera::ProcessCameraReset(long worldState, char anchorByte)`, 2618
bytes. It rebuilds the entire camera transform block (writes `worldState+0x250`
through `+0x530`: the camera position, target, matrices, interpolation state — the
`local_2f0..local_30` → `+0x270..+0x530` copies in the body). The
**load-bearing final instruction** (verbatim):

```c
LAB_00144750:
  *(uint *)(worldState + 0x418) = (-(uint)(anchorByte == '\0') & 0xfffffffb) + 6;
  return;
```

Evaluate:
- `anchorByte == 0` → `(0xFFFFFFFF & 0xFFFFFFFB) + 6 = 0xFFFFFFFB + 6 = 1`.
- `anchorByte != 0` → `(0x00000000 & 0xFFFFFFFB) + 6 = 6`.

So:

| anchorByte | `worldState+0x418 :=` | meaning |
|---|---|---|
| `!= 0` | **6** | **RENDER** — the streamer-enabled state |
| `== 0` | **1** | transition / non-render |

**`+0x418` is the SAME field** the streamer (`@0x0025bcb0` lines 612/618) and the
world tick (`@0x0023e660` lines 3069/3076) test for `== 6` to select the live
in-render camera, and that op81's own tail tests to decide whether to reset. **op81
→ ProcessCameraReset is therefore the single thing that drives the scene to RENDER
state and positions the camera in one shot.**

> **Why this resolves `packed-coord §4.3`.** That doc said "`+0x418` must reach 6
> (RENDER) via the world-login response sequence … the exact field that flips it is
> the adjacent deliverable." **The field is `worldState+0x418`; the writer is
> `ProcessCameraReset`; the trigger is op81's tail** (gated on `anchorByte != 0`).
> There is no separate render-state packet.

---

## 3. The streamer gate & the camera-target triple (world tick, verified)

`jag::game::ClientWorld_PerFrameTick @0x0023e660` is the per-frame world/scene tick
that drives the map streamer. Three verified facts:

### 3.1 map-ready `+0x650` is set when the asset-job queue drains

`@~0x242612` (decompile `@~line 2643`):

```c
if (*(undefined8 **)(jobQueue + 0x90) == *(undefined8 **)(jobQueue + 0xb0)) {  // read==write -> empty
    ...
    worldState = client[RELA 0xcf5];
    *(undefined1 *)(worldState + 0x650) = 1;     // <-- MAP-READY := 1
} else {
    // drain one job this frame (the else branch advances +0x90)
}
```

So `+0x650` flips to 1 a frame or two after the initial build/decode jobs (queued
by op81's BuildArea construction) finish. **Internal; no packet sets it.**

### 3.2 the gate (confirms `packed-coord §4.2` byte-for-byte)

`@~0x243xxx` (decompile `@~line 2839` and `@~line 3061`):

```c
worldState = client[RELA 0xcf5];
gateOpen = (worldState[0x634] != -1)
        && (worldState[0x638] != -1 && worldState[0x63c] != -1)
        &&  worldState[0x650];                 // the map-ready byte
if (!gateOpen /* and a secondary +0x68c flag */) {
    // ... compute the camera-target triple (§3.3), then re-check ...
}
```

### 3.3 the camera-target triple `+0x634/638/63c` comes FROM the camera node

When the gate is not yet armed, the tick computes the triple from the camera's
scene-graph world translation (decompile `@~line 3068-3095`):

```c
plVar54 = (long *)(worldState + 0x248);            // transition camera...
if (*(int *)(worldState + 0x418) == 6)
    plVar54 = (long *)(worldState + 0x78);         // ...or the in-RENDER camera
(**(code **)(*plVar54 + 0x28))(plVar54, world);     // advance/select the camera
...
pfVar18 = jag::graphics::GraphNode::GetWorldTranslation(   // <-- the CAMERA node's world pos
              *(void **)(*(long *)(camWorld + 8) + 8));
...
FUN_00651040(camYaw, world, sceneCentre);           // computes & writes +0x634/638/63c
worldState = client[RELA 0xcf5];
gateOpen = (worldState[0x638] != -1) && (worldState[0x634] != -1
        && worldState[0x63c] != -1) && worldState[0x650];   // re-check
```

**The triple is derived from `GetWorldTranslation(cameraNode)`** — the camera node
that `ProcessCameraReset` positioned. If the camera was never reset/positioned, the
node has no valid world translation → the triple stays at its init sentinel `-1`
(set in the streamer's lazy-init block `@0x0025bcb0` line ~268: `+0x68c = -1`,
`+0x694 = -1`, etc.) → the gate never opens.

### 3.4 the streamer bails on a zero camera position

`jag::game::BuildArea_StreamMapSquares @0x00647fc0(client, ?, float *camFloatXZ,
byte *sceneState+0x690, mode)` — decompile `@~line 234`:

```c
fVar86 = camFloatXZ[0];  fVar88 = camFloatXZ[2];
if ((fVar86 == 0.0) && (camFloatXZ[1] == 0.0) && (fVar88 == 0.0)) goto RETURN_EMPTY;
... // else: walk build bounds +0x13fb4..0x13fc0, range-gate vs camera,
    // push_unique near squares via FUN_00692600 into sceneState+0x101a8 / +0x11848
```

So even reaching the streamer, **a zero/unset camera position enqueues nothing.**
This is the terminal symptom of an unpositioned camera: zero index-5 requests.

### 3.5 the full verified chain

```
op81 REBUILD_NORMAL_SIMPLE @0x001daa70
  └─ FUN_00c55e80  : alloc BuildArea grid (packedA/B >> 6 corners), queue terrain/loc decode jobs
  └─ if (worldState+0x418 != 6):
        Camera::ProcessCameraReset(worldState, anchorByte)
          ├─ positions the camera transform (camera node world pos becomes valid)
          └─ worldState+0x418 := (anchorByte!=0) ? 6 : 1        ← RENDER advance

[next frames] ClientWorld_PerFrameTick @0x0023e660
  ├─ drains asset-job queue; when empty: worldState+0x650 := 1   ← map-ready
  ├─ selects camera by +0x418==6, reads GraphNode::GetWorldTranslation(cameraNode),
  │    FUN_00651040 -> writes camera-target triple +0x634/638/63c                 ← arm
  └─ GATE: (+0x650==1) && (+0x638/63c != -1) && (+0x634 != -1)
        -> BuildArea_StreamMapSquares @0x00647fc0(camFloatXZ != {0,0,0})
             -> FUN_00692600 push_unique map keys -> JS5 index-5 m{}_{}/l{}_{} fetches
```

Every arrow is decompile-verified in 948-5.

---

## 4. op77 CAM_UPDATE is a *modifier*, not the positioner

`jag::packethandlers::Camera::CAM_UPDATE @0x001d3d10` (the **real** handler; the
opcode table's `0x001d3b50` is **stale** — that address lands inside
`op130_RELATIONSHIP_DELTA @0x001d2b80`. Bind by behaviour, not number). Decompiled
core:

```c
worldState = client[RELA 0xcf5];
camera     = worldState + 0x78;                 // the in-render camera sub-object
bVar2 = read_u8(packet);
worldState[0xa0] = bVar2 & 1;                    // a flag
if (bVar2 & 0x08) { FUN_006e8b00(camera, read_u8, 1); }      // target-lookat mode
if (bVar2 & 0x10) { FUN_006e9070(camera, read_u8, 1); }      // a second mode
if (bVar2 & 0x80) {                              // matrix params present
    // reads pos/rotate/zoom doubles into worldState+0x138..+0x1e8 via FUN_00546250
}
```

**op77 writes camera transform sub-fields on `worldState+0x78` and `+0x138..+0x1e8`.
It does NOT write `+0x418`, the camera-target triple `+0x634/638/63c`, the map-ready
flag `+0x650`, or the eye `+0x570`.** It assumes the camera **already exists and is
in render mode** (it operates on the `+0x78` in-render camera selected by `+0x418==6`).

**Conclusion: op77 cannot position/arm the camera for the streamer; it can only
tweak a camera that op81's `ProcessCameraReset` already established.** Production
sends op77 *after* op81 (ord 1620, one tick after op81 at ord 0) precisely to apply
a server-chosen camera angle on top of the reset — it is cosmetic for first-render.

### Why darkan's replayed op77 blob is doubly wrong
darkan sends `PRODUCTION_FIRST_CAM_UPDATE_PAYLOAD` (a 1321-byte base64 replay,
`WorldServer.kt:407`). Decoding its body: a TLV-style camera-command stream
(leading flag `0x01`, then `(tag,value)` records) carrying **production-session
absolute coords** — the recurring word `0x001860cf` and Lumbridge-specific targets
are baked from the 15:27 production capture, **not** darkan's spawn. So even
ignoring that op77 doesn't position the camera: its *contents* point the camera at
production's Lumbridge, which need not match darkan's build grid. **Stop replaying
it.** If a camera command is wanted at all, build a real op77 (or send none — the
op81 reset already gives a valid default camera).

---

## 5. op75 SET_READY_FLAG — the *separate* loading-screen gate (still required)

This is the OTHER half darkan is missing, and it is **distinct** from the streamer
gate. `jag::packethandlers::ClientState::SET_READY_FLAG @0x00175120` (48 bytes,
verbatim):

```c
undefined * SET_READY_FLAG(long *client) {
  worldState = client[RELA 0xd00];               // (note: 0xd00, a DIFFERENT obj from op81's 0xcf5)
  tickSrc    = *(worldState + 0xe8);
  *(client[RELA 0xcfe] + 0x10) = 1;              // GLOBAL render-ready / IsWorldReady flag := 1
  *(worldState + 0xf0)         = *(tickSrc + 0x10);  // snapshot the server tick
  return &DAT_015d3620;
}
```

- It sets a **global IsWorldReady flag** (`client[RELA 0xcfe]+0x10`) and snapshots a
  tick. This is the flag the GameShell render loop reads to **leave the
  "Loading - please wait." sub-state** (`gameShell+0xd8`, see
  `world-entry-render-948.md` §1.2).
- It **does NOT** set `+0x418`, `+0x650`, or the camera-target triple. It is a
  **visibility** gate, not the **streamer** gate.

**Both gates must pass for a rendered, populated world:**

| gate | object/field | set by | unblocks |
|---|---|---|---|
| **render visibility** | `client[RELA 0xcfe]+0x10` (+ `gameShell+0xd8`) | **op75** (server, LAST) | client leaves "Loading"; frames render |
| **scene RENDER state** | `worldState(0xcf5)+0x418 == 6` | **op81 → ProcessCameraReset** | streamer/renderer use the live camera |
| **map streamer** | `worldState(0xcf5)+0x650==1 && triple +0x634/638/63c != -1` | the engine (tick), from the camera op81 positioned | **JS5 index-5 map-square requests** |

So the prior docs were each partially right:
- `world-entry-render-948.md` §0: **op75 is needed** to clear "Loading" — TRUE, but
  it is NOT what makes map squares load.
- `packed-coord-buildarea-948.md` §4: the **streamer needs a positioned camera** —
  TRUE, and **op81 positions it** (which §4 left open; resolved here).
- Neither is sufficient alone: you need **op81 (with a non-zero camera anchor) +
  op75 last**.

---

## 6. The exact darkan send-sequence fix (handoff to networking-protocol-engineer)

darkan's current world-entry burst (`WorldServer.kt` `sendWorldLoginCore` →
`sendWorldInitPackets` → Step 14/15): op81 (`cameraRotation=0`) → UI ops → op5 →
(empty varp baseline) → op55 → op1 → `buildInit` op22 → **replayed op77 blob** →
op130, then `flush()`. It **defers op75** (Step 15b). Two corrective actions:

### Fix A — give op81 a non-zero camera anchor
`WorldServer.kt:570` sends `cameraRotation = 0`. Production sends **7**
(`world-bootstrap-948.md` §1, `world-entry-render-948.md` §2.2: wire `0x87 =
value + 0x80`). Change to a non-zero value (use **7** to match production):

```kotlin
RebuildNormalSimple(
    zoneX = centreZone.x,
    zoneZ = centreZone.y,
    packedCoordA = buildArea.packedCoordA,
    packedCoordB = buildArea.packedCoordB,
    cameraRotation = 7,            // was 0; non-zero, matches production
    targetWorldId = EnvVars.worldId,
)
```

> **Honest caveat (the one residual binary-unknown).** op81's `cameraRotation` wire
> byte is stored to build-state `+0x428` (`@0x001dab06`); the `anchorByte` that
> `ProcessCameraReset` reads is a **separate** scene-root field
> (`*(*(client[RELA 0xca6])+0x240)+0xd8`, §1). I could **not** statically prove that
> the op81 `cameraRotation` byte feeds that scene-root `+0xd8` (it is set elsewhere
> in the scene-build path, likely from the BuildArea/centre). So Fix A is
> **matching production to be safe**, not a proven causal lever on the anchor. The
> proven causal facts are: (a) op81 calls ProcessCameraReset, (b) ProcessCameraReset
> sets `+0x418=6` iff its anchor is non-zero, (c) the streamer needs `+0x418==6` +
> the camera-derived triple. **If, after Fix A + Fix B, the client still issues zero
> index-5 requests, the next RE step is dynamic: confirm the runtime value of
> `worldState+0x418` and the `anchorByte` right after op81** (a one-breakpoint check
> — static analysis cannot see the scene-root `+0xd8` value). Matching production's
> coherent op81 (non-zero cameraRotation, correct centre, spawn-containing build
> area) is the highest-probability static fix.

### Fix B — send op75 SET_READY_FLAG LAST
darkan **defers** op75 (`WorldServer.kt:410`, Step 15b) and never calls
`sendFirstLightTail()`. op75 is the **visibility** gate (§5) — without it the client
never leaves "Loading", regardless of the streamer. Send it **last**, after the
scene + GPI exist:

```kotlin
// after the GPI / world-init burst, as the LAST logic packet:
session.send(SetReadyFlag())     // op75, size 0
session.flush()
```

(For a faithful bundle, call `WorldServer.sendFirstLightTail()` which already wraps
the render-onset packets + op75 — it is defined and currently never called.)

### Fix C — stop relying on the replayed op77 blob
Remove (or replace) `session.writeRawServerProt(77,
PRODUCTION_FIRST_CAM_UPDATE_PAYLOAD)` (`WorldServer.kt:407`). op77 does **not**
position the camera (§4) and the replayed blob carries production-Lumbridge coords.
For first-render: **send nothing for op77** (the op81 reset gives a valid default
camera). Later, if a specific camera angle is wanted, emit a real op77 built from
darkan's own camera state (the §4 bitflag format), not a replay.

### Fix D — keep the GPI init (already correct)
`PlayerInfoBuilder.buildInit` places the local player at the spawn 30-bit tile via
the teleport path (`encodeLocalPlayerInit`). Keep it — the camera/scene track the
local player entity, and op81's centre/GPI tile must agree (coherence, unchanged
from `world-bootstrap-948.md` §4). This is **not** what arms the streamer, but it is
required for a correctly-placed avatar and a coherent scene.

### The resulting send order (load-bearing parts)

```
op81 REBUILD_NORMAL_SIMPLE  (cameraRotation = 7, centre = spawn zone, build area contains spawn region)
     └─ client: builds BuildArea + ProcessCameraReset positions camera + sets +0x418=6 (RENDER)
op5 RESET_ALL_VARPS  ->  varp baseline (cache-valid ids only; may be empty for a bare pilot)
op55 / op1
op22 PLAYER_INFO init  (buildInit: local player at spawn tile)         ← keep
[no replayed op77]                                                      ← remove Fix C
op130 ...
... (zone content / HUD optional for a bare scene) ...
op75 SET_READY_FLAG   ← LAST. clears "Loading" visibility gate          ← add Fix B
```

Expected result: the client advances to RENDER on op81, the tick sets map-ready as
the build jobs drain, computes the camera-target triple from the positioned camera,
and the streamer issues **index-5 `m{X}_{Z}`/`l{X}_{Z}` requests** for the spawn
mapsquares (~49–51); op75 lifts the loading screen and the scene renders.

---

## 7. Production cross-check (ground truth)

From `build/undercut-socket-session-production-isaac.jsonl` (`conn=="world"`,
S2C) and `world-entry-render-948.md` §2.1, the early world packets and their role in
camera/render:

| world S2C ord | client cycle | op | role re: camera/render |
|---:|---:|---|---|
| 0 | 14927 | **81 REBUILD_NORMAL_SIMPLE** | **positions the camera (ProcessCameraReset) + RENDER state.** `cameraRotation=7`, centre zone (404,404), build area regions X[26..72] Z[37..142] containing player region (50,50) |
| 1619 | 14943 | 22 PLAYER_INFO | places the local player (GPI); camera/scene track it |
| 1620 | 14943 | 77 CAMERA_UPDATE | **tweaks** the already-reset camera (121B blob) — cosmetic, not the positioner |
| ~14961→14976 | — | (engine) | +15-cycle build frame: asset jobs drain → `+0x650=1`; camera triple armed; streamer pulls index-5 (the 83 KB asset burst at t=102.8 s, `world-entry-render-948.md` §4.1) |
| 3455 | 14976 | **75 SET_READY_FLAG** | **clears the "Loading" visibility gate** — sent LAST |

So production's op22 + op77 are downstream of op81's camera reset. **darkan sends
op22 (correct) and a replayed op77 (wrong, §4) but mis-sets op81's camera anchor
(`cameraRotation=0`) and omits op75** — exactly the two missing server actions in §6.

The op77 121-byte production blob and the 1321-byte replayed blob in darkan are
**different sessions** and both production-specific; neither is a positioning packet.
This confirms the static finding: **the camera is positioned by op81, not op77.**

---

## 8. Residual unknowns (honest)

1. **op81 `cameraRotation` (+0x428) vs the ProcessCameraReset anchor (scene-root
   +0xd8).** Statically unproven that the wire byte feeds the anchor (§6 Fix A
   caveat). Highest-confidence static fix = match production (non-zero, 7). A
   **single dynamic breakpoint** right after op81 (read `worldState[0x418]` and the
   anchor byte) would settle it — flagged for a follow-up if Fix A+B don't yield
   index-5 requests.
2. **`FUN_00651040` exact triple write.** The world tick computes the triple from
   `GetWorldTranslation` and passes it to `FUN_00651040 @0x00651040`; the precise
   per-field stores into `+0x634/638/63c` are inside that helper (called with the
   camera yaw + scene centre). The causal chain (camera node → triple) is proven by
   the call-site context; the helper's internals were not line-traced (not needed
   for the answer).
3. **`+0x418` enum beyond {1,6}.** Only 1 (transition) and 6 (RENDER) are exercised
   on world-login. Other values (cutscene, instanced) exist but are not on the
   first-render path.
4. **Map-square group-id derivation** for the index-5 fetches is cache-side
   (unchanged NXT region scheme), owned by the cache library — out of scope here.

---

## 9. Function/offset reference (948-5, all headless-verified)

| addr | name (committed) | role |
|---|---|---|
| `0x001daa70` | `REBUILD_NORMAL_SIMPLE` (op81) | builds BuildArea; **tail calls ProcessCameraReset** (the positioner) |
| `0x001444e0` | `jag::game::Camera::ProcessCameraReset(worldState, anchorByte)` | positions camera; **final line sets +0x418 = anchorByte!=0 ? 6 : 1** |
| `0x0025bcb0` | `jag::game::SceneState_ProcessFrustumAndStream` (was FUN_0025bcb0) | per-frame frustum: selects camera by +0x418==6, computes frustum +0x620..638 from camera pos; reads/writes eye +0x570 |
| `0x0023e660` | `jag::game::ClientWorld_PerFrameTick` (was FUN_0023e660) | sets map-ready +0x650 on job-queue drain; arms camera-target triple from `GetWorldTranslation(cameraNode)`; **the streamer gate** |
| `0x00647fc0` | `jag::game::BuildArea_StreamMapSquares` (was FUN_00647fc0) | walks build bounds, enqueues near map squares (FUN_00692600); **bails if camFloatXZ=={0,0,0}** |
| `0x00692600` | `vector<long>::push_back_if_absent` | the unique map-key load queue → JS5 index-5 fetches |
| `0x00651040` | (helper) | computes/writes the camera-target triple +0x634/638/63c |
| `0x001d3d10` | `jag::packethandlers::Camera::CAM_UPDATE` (op77) | bitflag camera **modifier** on +0x78 / +0x138..; does NOT position or arm |
| `0x00175120` | `SET_READY_FLAG` (op75) | global IsWorldReady (`client[RELA 0xcfe]+0x10`) — **separate "Loading" visibility gate** |

worldState field map (object `client[RELA 0xcf5]`, the op81 target; aka "SceneState"
in `packed-coord-buildarea-948.md`):

| offset | type | name | set by / read by |
|---|---|---|---|
| `+0x78` | obj | in-RENDER camera | written by op77; selected when `+0x418==6` |
| `+0x248` | obj | transition camera | selected when `+0x418!=6` |
| `+0x418` | int | **scene RENDER state** (6=RENDER, 1=transition) | **set by ProcessCameraReset** (op81 tail); read by streamer + tick |
| `+0x428` | byte | build/camera rotation | op81 wire `cameraRotation` (`+0x80`) |
| `+0x570` | int | eye/camera grid position | read/written by `@0x0025bcb0` |
| `+0x620..+0x638` | int×6 | camera-frustum span | computed per-frame from camera pos |
| `+0x634/638/63c` | int | **camera-target triple** (≠ -1 required) | armed by tick from `GetWorldTranslation(cameraNode)` |
| `+0x650` | byte | **map-ready flag** (==1 required) | set by tick on asset-job-queue drain |
| `+0x68c/694` | int | frustum-prev (init -1) | streamer lazy-init |
| `+0x13fb4/b8/bc/c0` | int×4 | build-area region bounds (min/max X/Z) | op81 packedA/B `>>6` (see packed-coord doc) |
| `+0x101a8 / +0x11848` | queue | map-square load-key queues | `FUN_00692600` push_unique (streamer) |

---

# 11. SPAWN-MAP-LOAD TRIGGER RESOLUTION (2026-06-23 pass — static, 948-5 + golden capture)

> **Mandate:** resolve the ONE residual — what makes the client load the spawn map
> square so the camera arms. Static only (no dynamic debugging). Authority:
> `analyzeHeadless -process rs2client.948-5` (project `~/projects/reclass-data/rs2client-948`,
> Ghidra 12.0.4) + golden capture `build/undercut-socket-session-production-isaac.jsonl`.
> Written incrementally as derived.

## 11.0 Capture scope (settles brief Q2 from the capture side) [VERIFIED]

The golden capture `undercut-socket-session-production-isaac.jsonl` records **exactly
two TCP connections, both on port 443**:

| fd | role | peer | c2s_bytes | s2c_bytes | records |
|---|---|---|---|---|---|
| 56386 | lobby | 8.42.17.253:443 | 797 | 18339 | 1689 |
| 56394 | world | 8.26.16.145:443 | 2799 | 47350 | 3879 |

Every non-connection record is a **decoded game-protocol packet** (`{dir, conn, opcode,
name, size_kind, size, isaac_index, payload_hex}`). There is **NO JS5/cache channel in
this capture** — JS5 in NXT runs over a *separate* connection (the game/login socket
multiplexes ServerProt/ClientProt only; JS5 group fetches are not ServerProt). Confirmed:
only fds 56386/56394 exist; "js5/archive" substring hits were false positives in payload
hex (0 real JS5 records).

**Therefore: production's index-5 map-group fetches CANNOT appear in this capture, and
their absence here is NOT evidence darkan differs.** The capture answers *game-protocol*
timing (what S2C/C2S packets flow and when), not *JS5-transport* timing. To see
production's map fetches you need a JS5-channel capture (not this file). This reframes
brief Q2: the map-load timing question must be answered from the **binary** (which path
issues the load), not from this capture.

## 11.1 Hard xref facts (callers) [VERIFIED]

```
MapSquare::LoadFiles @0x005c3fe0 — callers:
   0x01067278, 0x010e0ed4, 0x0136ce60   (ALL data refs / vtable slots; NO code caller)
       → confirmed: LoadFiles is an indirect vtable callback, fired async on FileReady.

BuildArea_StreamMapSquares @0x00647fc0 — code callers:
   0x0016b680  FUN_0016b680 (15519B)                 — per-frame world-tick path
   0x0024e8c0  ConnectionManager::LoginProtocolHandler — world-login pump (line 3132)
       → BOTH camera-gated (+0x650 && triple +0x634/638/63c != -1). NO third caller.

ProcessCameraReset @0x001444e0 — code callers (9):
   0x00144f70  FUN_00144f70                            — (camera helper)
   0x00186f10  Camera::CAM_SMOOTHRESET
   0x00186f60  Camera::CAM_FORCEANGLE
   0x001daa70  ClientState::REBUILD_NORMAL_SIMPLE      — op81 (simple rebuild)
   0x001df100  ClientState::REBUILD_REGION_ALT (3374B) — *** ALSO resets camera ***
   0x00343350, 0x00343390  FUN_*                       — (small helpers)
```

**New fact vs prior docs:** `ProcessCameraReset` is called by BOTH op81
(`REBUILD_NORMAL_SIMPLE`) AND `REBUILD_REGION_ALT @0x001df100` (the full
region-rebuild handler). Whichever rebuild the server sends, the camera-reset tail runs.

(continued — tracing the LoadFiles vtable owner + the camera-independent load path)

## 11.2 ProcessCameraReset: anchorByte ONLY sets +0x418 (NOT the camera position) — the prior "deadlock" mechanism is WRONG [VERIFIED]

Full decompile of `ProcessCameraReset @0x001444e0` (2618B). **`param_2` (anchorByte) appears
in EXACTLY ONE statement** — the final line:

```c
*(uint *)(param_1 + 0x418) = (-(uint)(param_2 == '\0') & 0xfffffffb) + 6;  // anchor!=0 ? 6 : 1
```

The camera-transform writes (`+0x250` position, `+0x270..+0x340`, `+0x500/+0x530`, the
`+0x248` sub-camera setup) are **independent of anchorByte**. Top control flow:

```c
plVar4 = &DAT_013962b0;
if (*(client + RELA[0xd40]) != 0) plVar4 = FUN_00198b40();   // a state object
local_340 = plVar4[1];                                        // saved-camera ptr
if (local_340 == 0) {                       // NO saved camera (FRESH world-login case)
    FUN_006e9550(local_318, param_1 + 0x50);                 // compute camera from +0x50
    FUN_00ab1090(param_1 + 0x250, local_318 + 8);            // WRITE camera position +0x250
    ... fresh camera transform written to +0x270..+0x340 ...
} else {                                    // saved camera -> restore (+0x418 = local_148)
    ... restore block ...
}
... // unconditional tail:
*(param_1 + 0x418) = anchorByte!=0 ? 6 : 1;  // ONLY anchorByte effect
```

**CONCLUSION (overturns prior §5.4 deadlock mechanism):** the camera position is computed
FRESH from `param_1 + 0x50` whenever there is no saved camera — i.e. on a fresh world
login the camera transform IS written regardless of anchorByte. **anchorByte does NOT gate
whether the camera is positioned; it gates ONLY whether `+0x418` becomes 6 (RENDER) or 1
(transition).** So the chain "anchorByte=0 → camera never positioned → triple stays -1" is
mechanically incorrect: the camera node DOES get a position. The real consequence of
anchorByte=0 is `+0x418=1` (transition camera selected) — see §11.3 for whether that still
arms the streamer triple.

This reframes the whole residual: the question is NOT "how do we get the camera
positioned" (op81's reset already does that from `+0x50`), it is **"does the streamer
triple get armed when `+0x418==1` (anchor 0 / transition camera), or does it require
`+0x418==6`?"** — i.e. whether the transition camera `+0x248` yields a valid
`GetWorldTranslation`. That is the true chicken-and-egg (brief Q3).

## 11.3 The map-request path is camera-gated, BUT there is NO deadlock — +0xd8 is set AFTER the first streamer pass, not before [VERIFIED]

The per-square map cache lookup + JS5 request is `FUN_00b14520` (1776B):

```c
undefined * FUN_00b14520(provider_owner, ulong *outRef, uint x, int z) {
   key = (z << 7) | x;                          // region key
   ... hash-table probe for an already-loaded square ...
   if (notLoaded) {
       jag::Js5ResourceProvider::FileReady_CheckIndex(provider, ..., DAT_015c02d0, 3, key, 1, 0);
       // ^ THIS is the JS5 request whose completion fires MapSquare::LoadFiles (the +0xd8 setter)
   }
}
```

`FUN_00b14520` has **exactly two code callers, BOTH camera-gated**:
`LoginProtocolHandler @0x0024e8c0` (line 3031) and `BuildArea_StreamMapSquares @0x00647fc0`.

**The critical correction to prior §5.4:** combine this with §11.2 (camera is positioned
regardless of anchorByte). The true ordering on a fresh world-login is:

```
op81 tail → ProcessCameraReset → camera transform WRITTEN from +0x50 (anchorByte irrelevant
            to position); +0x418 := anchorByte!=0 ? 6 : 1
[next frame] tick → selects camera (+0x78 if +0x418==6, ELSE +0x248) → GetWorldTranslation
            → FUN_00651040 writes triple +0x634/638/63c   (BOTH +0x418 branches do this)
[same/next frame] streamer (tick or LoginProtocolHandler) → camFloatXZ from triple
            → FUN_00b14520 → FileReady_CheckIndex(map group)  ← FIRST map request ISSUED
[async] FileReady → MapSquare::LoadFiles → scene-root+0x240→+0xd8 := 1   ← anchor set AFTER
```

So **`+0xd8` is set as a CONSEQUENCE of the first streamer pass, not a precondition of it.**
There is no chicken-and-egg via `+0xd8`: the camera is positioned by op81 (from `+0x50`),
the triple arms from the positioned camera node, and the streamer issues the first map
request — all WITHOUT `+0xd8`. `+0xd8`/anchorByte only decides `+0x418` = 6 vs 1, and BOTH
the transition camera (`+0x248`, used when `+0x418==1`) and the render camera (`+0x78`,
`+0x418==6`) feed `GetWorldTranslation` in the tick's triple-arm block (lines 3025-3052).

**This means the prior doc's "darkan never triggers the pre-op81 spawn-map load → anchor 0
→ camera never positions → zero index-5" causal chain is mechanically WRONG.** The camera
positions from op81 regardless. So if darkan still issues ZERO index-5 requests, the block
must be EARLIER: the triple never arms, OR the streamer's `camFloatXZ` is `{0,0,0}`, OR the
camera node has no valid scene-graph world translation. §11.4 pins which precondition fails.

## 11.4 GROUND-TRUTH from the darkan run logs + crash report — the premise "camera never arms" needs re-framing [VERIFIED from darkan logs]

The darkan world-server log (`build/world-current.log`, run 2026-06-23 13:16:32) and the
client crash report (`~/Library/Logs/DiagnosticReports/rs2client-2026-06-23-132218.ips`)
show the ACTUAL behaviour, which differs from the brief's framing:

**(a) darkan ALREADY applies the prior-doc fixes.** The log shows darkan sends, in order:
op81 (5137, build area `X[48..52] Z[48..52]`, centre (400,400), gpiPrefix=5119B), world
chrome, op5, op55, op1, op130, **op110 RunClientScript ×3, op3 IfSetTopLevelInterface(19),
op75 SetReadyFlag**, then op26 FriendStatus + op156 ChatFilterSettingsPrivateChat. So Fix
A (camRot=7), the op3 HUD swap, and op75 are all live. op81 is byte-correct (§11 / encoder
verified) and reaches its tail.

**(b) The client received the full burst, then sent ZERO C2S for ~5.5 minutes.** The world
session log:
```
13:16:32  S2C ... op81 ... op3 ... op75 ... op156      (full burst sent)
13:22:13  Session read ended: EOFException: required 13314 bytes but only 2462 available
13:22:14  World session closed for test after 0 packets
```
"after **0 packets**" = the client never sent a single C2S to the world server. The EOF at
13:22 is the client closing its socket when it was quit 5.5 min later.

**(c) The brief's "op156 available=171 → session closes" is mis-attributed.** In THIS run
the `op156` in the log is darkan's own **S2C** `ChatFilterSettingsPrivateChat` (a legit
outbound social packet), NOT an inbound client opcode. The session did not close on a
framing error — it closed on socket EOF when the client process ended.

**(d) The client process aborted (SIGABRT) — but on the SHUTDOWN path, not in a packet
handler.** Crash report faulting thread:
```
-[NSApplication terminate:] → NSNotificationCenter post → exit → __cxa_finalize_ranges
  → rs2client+0x61fd (atexit dtor) → std::terminate() → abort (SIGABRT)
```
The main thread was in the normal AppKit run loop (`-[NSApplication run]`), then
`NSApplication terminate:` was invoked (app told to quit), and a C++ destructor threw
during `exit()` cleanup → `std::terminate`. **This is a clean-exit crash, not a
mid-render/packet crash.** So the client was NOT crashing while processing darkan's burst;
it ran its main loop, black-screened, emitted zero C2S, and was later quit (the abort is
incidental teardown noise).

**Re-framed problem:** the client is ALIVE in its main loop after the burst but its world
scene tick never progresses to streaming maps or sending C2S (op5 SceneGraphReport, op51
Ping — which production sends within the first frames). It is stuck in a pre-world-tick
state. The question is therefore NOT "what packet arms the camera" (op81 already runs
ProcessCameraReset) but **"why does the client's IN-GAME per-frame world tick never run
its scene/stream/C2S logic after darkan's burst"** — i.e. what state gate keeps the client
from entering the live world tick. §11.5 traces that gate.

## 11.5 THE ACTUAL ROOT CAUSE — the client makes ZERO JS5 content requests; it gets the master index ONCE then idles in a 30-second JS5 reconnect loop [VERIFIED from darkan JS5 log]

> ## ⚠️ CORRECTION (2026-06-23, js5-server-engineer) — §11.5/§11.6 mis-diagnosed this as a JS5 stall. It is NOT.
>
> The "zero JS5 content requests + 30s reconnect loop" is **benign**, not a pipeline
> failure. Full proof in `docs/js5/948-post-master-index-stall-RESOLVED.md`. Summary:
> 1. **Master index is ACCEPTED** (not rejected). 948-5 `Js5MasterIndex` ctor `@0x8f35d0`
>    does not check decrypted `byte[0]` (our `0x0A` is fine); Whirlpool range in
>    `VersionTableBuilder` is correct. **Decisive:** the client's disk cache holds **44
>    populated `.jcache` SQLite DBs** (configs/maps/sprites/models/textures, GB-scale) —
>    impossible without master-index acceptance + a working download path.
> 2. **The TCP request framing is CORRECT** (948-5 `Js5NetQueue::RequestData @0x8b4360`
>    = `flags(1)|archive(1)|group(4BE)|pad(4)`, unchanged; our reader matches). Those 44
>    jcache DBs were populated through our exact `JS5Server`+`FileProvider` path.
> 3. **Why zero requests:** `diskCacheEnabled=true` AND the cache was already complete →
>    `RequestGroupFromDisk` hits locally → **no TCP server request is issued** (correct).
>    The 30s loop is an idle JS5 channel (its timer resets only on RECEIVE; an
>    all-disk-cache-served client receives nothing → cycles every 30s). Benign.
> 4. **Why §11.5 mis-read it:** the run used `LOG_LEVEL=DEBUG`→`FINER`, but `JS5 request:`,
>    `JS5 idle Ns`, and HTTP `JS5 HTTP:` logs are `logFinest`→**suppressed**; and
>    disk-cache hits produce NO server log at all. The absent lines proved nothing.
> **Consequence:** JS5 is exonerated. The black screen is downstream — consistent with
> §11.6's OWN finding that the S2C game burst is "sufficient". The remaining blocker is
> the client's in-world tick not engaging after state→30 (zero C2S op51/op5), a
> game-protocol/world-entry question — **not** JS5. The original (now-superseded) §11.5
> text follows for history.
>
> ---

The darkan **lobby/JS5 server log** (`build/lobby-current.log`, same 13:16 run) is decisive
and **overturns the "camera-arm / index-5" framing entirely**:

```
13:16:14  JS5 connection ... handshake complete (948.1) ... ACK + READY complete
13:16:14  JS5 served: index=255 archive=255 prefetch=false — 5883 bytes   ← MASTER INDEX, once
            (no further "JS5 served" line EVER — not 255/N reference tables, not index 5 maps)
13:16:44  Client disconnected: EOFException   (exactly 30s later)
13:16:44  JS5 connection ... handshake ... ACK + READY     ← reconnect, requests NOTHING
13:17:14  Client disconnected: EOFException   (another 30s)
...  (this 30-second connect→handshake→idle→EOF→reconnect loop repeats for the whole 6-min session)
```

**Facts:**
1. The client requested the **master index (255/255) exactly once**, at 13:16:14 — BEFORE
   world login (13:16:31). It was served (5883 bytes).
2. **The client then requested ZERO further JS5 files** — not the per-archive reference
   tables (`255/0..N`), not any config archive, not index-5 map groups. The only "JS5
   served" line in the entire log is that one master index.
3. The client cycles JS5 every **exactly 30 seconds**: connect → handshake → SYNC → ACK +
   READY → (idle, no request) → EOF disconnect → reconnect. This is the client's JS5
   idle/stall timeout firing repeatedly.

**Implication (the real answer):** the client is NOT blocked at the camera/scene-packet
layer. It is blocked at the **JS5 layer** — it never pulls the cache data it needs to build
ANY scene. "Zero index-5 map requests" is a SUBSET of "zero JS5 content requests of any
kind after the master index". A client that can't fetch reference tables / config / models
can never build terrain regardless of op81, the camera, or op75. The map being present and
serveable in the cache is irrelevant if the client never ASKS for it.

**This reframes brief Q1/Q4 decisively:** the missing trigger is NOT a server game-protocol
packet (op81/op77/op75/op3 are all sent and op81 is byte-correct and creates the world +
positions the camera, §11.0–11.4). The block is that the client's JS5 download pipeline
stalls after the master index. The "30-second reconnect loop with zero requests" is the
exact signature of a JS5 connection/protocol mismatch — the client got the master index
(possibly on a one-shot HTTP-style request), then opened a *persistent* JS5 socket
expecting to stream further groups on it, and that socket never carries a request the
server logs (or the server's response framing for the master index made the client reject
it / not proceed to per-archive indices).

**Owner: this is a JS5-server-engineer problem, not a networking game-protocol or
camera/scene problem.** The ghidra/game-protocol residual the brief asked about
("what arms the camera / loads the spawn map") is moot until the JS5 pipeline delivers the
reference tables + map groups. The next diagnostic step is JS5-side (compare darkan's JS5
master-index framing + connection model against a live/production JS5 capture; confirm the
client is issuing group requests on the persistent socket and darkan is parsing them).

## 11.6 SYNTHESIS — the residual is NOT a game-protocol/camera packet; it is the client's content pipeline never engaging (JS5 + in-world tick both idle) [CONCLUSION]

### What the binary proves (game-protocol layer is SUFFICIENT)
- op81 `REBUILD_NORMAL_SIMPLE` is byte-correct (Shape B), reaches its tail, and:
  - `FUN_00c55e80` **creates the active world** and sets `SceneManager+0x70` (the
    active-world index `GetActiveWorld` returns), §11.3/§the FUN_00c55e80 decompile.
  - `ProcessCameraReset` **positions the camera from worldState+0x50 regardless of
    anchorByte** (§11.2); anchorByte (`scene-root+0x240→+0xd8`) ONLY sets `+0x418` = 6 vs 1.
- The map-stream path (`FUN_00b14520`→`FileReady_CheckIndex`) is camera-gated, but the
  camera IS positioned and the tick arms the triple from EITHER camera (`+0x78`/`+0x248`),
  so there is **no `+0xd8` deadlock** (§11.3). `+0xd8` is set AS A RESULT of the first
  stream pass, not as a precondition.
- Conclusion: op81 + op3 + op75 (all of which darkan now sends) are enough at the
  game-protocol level to drive the scene. The prior docs' "missing camera-arm packet" /
  "pre-op81 map-load trigger" framing is not the residual.

### What the darkan run logs prove (the client never engages its content pipeline)
From the 2026-06-23 13:16 run (`world-current.log`, `lobby-current.log`, crash `.ips`):
1. World login completes (state→30), client receives the FULL S2C burst (op81…op3…op75).
2. **Client sends ZERO world C2S** — no op51 Ping, no op5 SceneGraphReport. Production's
   in-world client sends op5 from world-C2S idx 6 and op51 ×31 (§11.5 cross-check). op5
   `SendSceneGraphReport @0x00229f40` is emitted UNCONDITIONALLY once per frame by the
   per-frame connection pump (`LoginProtocolHandler`) while the connection is live — so
   zero op5 ⇒ **the client's in-world per-frame pump is not running its body on the world
   connection.**
3. **Client makes ZERO JS5 content requests** — only the master index (255/255, served
   once at boot, 13:16:14). No per-archive reference tables (255/0..N), no config archives,
   no index-5 maps. (FINER `JS5 served` logs every successful serve; only one exists. No
   `JS5 miss` warns. FINEST request logs are off, but serves are visible and there is one.)
   The client then cycles JS5 every **30s** (connect→handshake→idle→client-side EOF→
   reconnect) — the signature of a client holding a JS5 channel with nothing to fetch.
4. The eventual SIGABRT is a clean **shutdown** crash (`NSApplication terminate:`→`exit`→
   a C++ dtor throws→`std::terminate`), NOT a mid-render/handler crash (§11.4).

### The reconciliation (why both C2S and JS5 are idle)
The client is in state 30 and its main loop runs, but it never advances into the **live
in-world session tick** that (a) emits op51/op5 C2S and (b) drives JS5 content fetches.
Both symptoms share one cause: **the world/scene subsystem never reaches "ready" because
it never loads the cache content it needs**, and it never loads that content because it
never issues JS5 requests for it. The scene streamer is camera-gated, but the broader
content load (config archives, the reference tables) is gated on the JS5 pipeline being
usable past the master index — which, in this run, it is not (the client never asks for
255/N). A client that cannot enumerate/fetch archives cannot build a world, cannot become
"ready", and therefore its in-world pump (op51/op5 + map stream) never runs → permanent
black screen.

### The exact darkan fix (brief Q4) — owner: JS5-server-engineer (NOT networking/camera)
The residual is a **JS5 download-pipeline stall**, not a missing game packet. The fix is
to make the client able to proceed PAST the master index to the per-archive reference
tables and content:
1. **Verify darkan's master-index (255/255) wire bytes against a live/production JS5
   capture** (tools `LiveJS5Capture` / `JS5WireCompare` already exist). If the client
   accepts it, it must immediately request `255/0..N` reference tables — confirm whether
   those requests arrive (raise JS5 logging to FINEST first; FINER hides `JS5 request:`).
2. **Confirm the persistent-JS5-socket request path is read correctly** for the macOS
   948-5 client: `JS5Server.reader` frames a file request as opcode(1)+index(1)+group(4)+
   pad(4). If the macOS client frames requests differently, the server reads garbage and
   the client times out (the 30s loop). This is the most likely concrete bug.
3. **The map being present/serveable is irrelevant** until (1)/(2) are fixed — the client
   never asks for index-5 maps because it never gets past the master index. So this is NOT
   "the client must already have the map locally" and NOT a missing S2C packet; it is the
   JS5 connection/protocol delivering nothing after the master index.

### Honest residual (what static analysis cannot finish here)
Whether the client (a) REJECTS darkan's master index (parse/CRC/format) and so never asks
for 255/N, or (b) IS asking for 255/N on the persistent socket but darkan mis-frames the
request and never serves it — cannot be separated from the Linux Ghidra binary + these
logs alone. The discriminator is a **JS5-channel capture of the darkan run with FINEST
logging** (or `LiveJS5Capture` diff). Both land in the JS5-server-engineer's domain. The
game-protocol/camera/ghidra side of this residual is RESOLVED: op81 is correct and
sufficient; nothing in the S2C game burst is the blocker.

---

# 12. THE IN-WORLD TICK GATE — why zero C2S / black screen (2026-06-23, definitive static + capture)

> **Mandate (this pass):** with JS5 (§11.5/§11.6 corrected, RESOLVED doc) and the camera
> (§0–§11.4) both exonerated, answer the ONE remaining question: after state→30 (and after
> op3 commits the client in-world), **what gates the client's per-frame world tick / scene
> construction / first C2S?** Trace from the main loop and the state-30 path; discriminate
> (A) client-self-builds-from-op81+cache vs (B) server-must-stream-zones; resolve whether
> op81+op3+op75 is sufficient (sequencing bug) or op77/op78×N/op199 are load-bearing.
>
> **Authority:** `analyzeHeadless -process rs2client.948-5` (project
> `~/projects/reclass-data/rs2client-948`, Ghidra 12.0.4; GhidraMCP bridge down — port 8080
> is OrbStack — so headless was used exactly as §0/§11). Cross-checked against the golden
> capture `build/undercut-socket-session-production-isaac.jsonl` (`conn=="world"`, 3790 S2C /
> 81 C2S decoded) and darkan's send path (`WorldServer.kt`). Every `@0xADDR` below is
> headless-verified. **No Ghidra DB writes were made** (read-only project; the prior pass's
> renames remain). One read-only helper script was added to `ghidra-scripts/`
> (`RS3FindDisp948.java`, displacement scanner) — it does not touch the program DB.

## 12.0 TL;DR — the answer

1. **The per-frame world tick IS being called in darkan.** It is dispatched by
   `FUN_0047d3a0` (the per-frame render callback), gated on a SINGLE field:
   `client+0x194a0 != 0` (the **ClientWorld** object). That object is constructed in the
   same block as the ConnectionManager (`client+0x194a8`) — which darkan demonstrably uses
   (it reached state 30) — so `client+0x194a0` is non-null and the tick runs. **This is NOT
   case S1 ("tick object missing").** [VERIFIED — disasm of `FUN_0047d3a0 @0x0047d3a0`]

2. **op5 (SceneGraphReport) and op51 (Ping) are NOT emitted because their two stacked gates
   are not satisfied — and both ultimately require a POPULATED scene graph, which darkan
   never produces.** Concretely:
   - op5/op51 live in `ConnectionManager::LoginProtocolHandler @0x0024e8c0`'s **WORLD
     sub-branch**, entered only when `ClientSceneManager+0x1c != 0` (`client+0x19578`, the
     same object the tick dispatcher reads). `+0x1c == 0` ⇒ the LOBBY sub-branch (no op5,
     no op51, `ProcessConnections(...,0)`); `+0x1c != 0` ⇒ the WORLD sub-branch
     (`ProcessConnections(...,1)` + op5 + `RunLoginStateMachine`). [VERIFIED — line 1358]
   - `ClientSceneManager+0x1c` is the **scene-build PHASE** the client reports back in op5
     itself (`FUN_00261900` copies `+0x1c` into the op5 body, then a build-progress %). At
     boot it is **0** = "Running Auto Configuration…" (the tick shows that loading string
     when `+0x1c==0`, `@perfetick line 2939`). [VERIFIED — `FUN_00261900 @0x00261900`,
     `ClientWorld_PerFrameTick @0x0023e660` line 2939]
   - Even inside the world branch, the op5 SEND itself (`SendSceneGraphReport @0x00229f40`)
     only transmits when `state ∈ {20,23,30}` (bitmask `0x40900000`) **AND a "scene-graph
     dirty" flag `+0x14 != 0`**, and the body is a walk of the **scene-graph map-square
     rbtrees** (`RELA[0x102a/102b/102c]`). With no scene-graph nodes there is nothing to
     report, `+0x14` is never set, and op5 never fires. [VERIFIED — `@0x00229f40` lines
     604–626 + the rbtree walk lines 540–602]

3. **The map streamer is gated, but NOT on state — purely on scene state.** At the TAIL of
   `LoginProtocolHandler` (reached every frame, lobby OR world) the streamer fires only if
   `SceneState+0x650==1 (map-ready) && +0x634/638/63c != -1 (camera triple)`
   [VERIFIED — `@0x0024e8c0` lines 3007–3014, the §3.2 gate confirmed]. The triple is armed
   by the tick (`@0x0023e660` lines 3013–3056) ONLY when **(G1)** `GetActiveWorld()->+8 != 0`
   **AND (G3)** the local-player/anchor scene node `FUN_00490080(client)->+8 != 0` **AND
   (G2)** either `SceneState+0x68c != 0` or `ClientWorld+0x153 != 0`. **op81 sets NONE of
   `+0x68c`/`+0x153`/`+0x650`** (it only creates the world via `FUN_00c55e80`, clears
   `+0x49`, and resets the camera/`+0x418`). [VERIFIED — op81 decompile @0x001daa70 touches
   none of these]

4. **`ClientWorld+0x153` — the triple-arm bootstrap — is set EXCLUSIVELY by a ServerProt
   ZONE-UPDATE handler.** Displacement scan: the ONLY writer of `+0x153=1` in the whole
   binary is `FUN_001dc7f0`, which the opcode table binds as **ServerProt opcode 129**
   (`serverprot_948-5_opcode_table.json` entry 129, size 3) — a `jag::packethandlers::
   ZoneUpdates`-family handler. It reads a `+0x80`-transformed packed zone coord, walks the
   active world's zone squares, and flips each square's `+0x153=1`. [VERIFIED — `RS3FindDisp948 0x153` → sole setter `FUN_001dc7f0 @0x001dc7f0`; bound to op129;
   `RS3Callers` → registered in `ServerProt::BindHandlers @0x0007509a`]

5. **THE DISCRIMINATION (A) vs (B): it is (B)-leaning — the scene the client renders is
   substantially server-streamed zone content, and the in-world tick will not advance past
   "configuring" without it.** Hard capture proof: production sends **ALL 616 op78
   `UpdateZoneFullFollowsV2` + 64 op76 `UpdateZonePartialEnclosed` BEFORE the client emits a
   single C2S** (op78 spans S2C file-order 3318–~4990; op75 is the LAST S2C at 5145; the
   client's FIRST C2S — op240 — is at 5488, op5 at 5493, op51 at 5524). The client is mute
   for the entire zone-stream, then begins ticking only after op75. The base terrain is
   still client-self-fetched from the JS5 cache (settled in the JS5 RESOLVED doc); op78/op76
   carry the **loc/obj/ground scene-graph content** that the client adds as nodes — and op5
   is literally a *report of those scene-graph nodes*. **darkan sends ZERO op78/op76/op22/
   op44/op199**, so the scene graph stays empty, `+0x153`/`+0x68c` never arm, the triple
   never arms, the streamer's camera-target gate never opens, `+0x1c` never advances out of
   "Running Auto Configuration", the world branch is never entered, and op5/op51 never fire.
   That is the black screen + zero C2S, exactly. [VERIFIED — capture timeline + binary]

6. **op75 is necessary but NOT sufficient, and darkan sends it PREMATURELY.** op75
   (`SET_READY_FLAG @0x00175120`) sets the global render-visibility flag
   `client[RELA 0xcfe]+0x10=1` (lifts "Loading - please wait") — confirmed unchanged from
   §5. It does NOT set `+0x418`, `+0x650`, the triple, or `+0x153`/`+0x1c`. Production sends
   it **LAST**, after the full zone stream + op199. darkan sends it at seq ~20 (right after
   op3), against an empty scene — so the loading screen lifts onto a black, unbuilt world.

### The one-line answer to the brief's three deliverables
- **(1) The exact runtime gate:** the client's in-world per-frame branch (op5/op51 + the
  streamer's downstream consumers) is gated on **`ClientSceneManager (client+0x19578) +0x1c
  != 0`**, the scene-build phase. It is 0 ("configuring") and never advances because the
  scene graph is never populated. The map streamer's own gate (`SceneState+0x650==1 &&
  triple+0x634/638/63c != -1`) never opens because the triple-arm needs
  `ClientWorld+0x153 != 0` (or `SceneState+0x68c != 0`), and **the only setter of `+0x153`
  is ServerProt op129 (a ZoneUpdates handler)** — darkan sends no zone packets.
- **(2) Minimal S2C set:** op81+op3+op75 is **NOT sufficient** — it is **(B)**. The
  zone-update stream (op78×N, and at least one packet that flips `+0x153`/the scene-graph
  dirty state) is **load-bearing for the scene to exist and the tick to advance**. The fix
  is to build the zone-streaming system (op78 full-follows per zone in the build area),
  THEN send op75 LAST — not merely reorder op75.
- **(3) Premature op3+op75 confirmed:** yes — darkan commits the client in-world (op3) and
  lifts the loading gate (op75) at seq ~20, against an empty scene graph, leaving it
  "committed-but-unbuilt" (black, tick stuck in the configuring phase).

## 12.1 The per-frame call graph (who calls the tick, who emits C2S) [VERIFIED]

```
jag::Client::MainLogic @0x004b37f0
  └─ if (client+0x5334d != 0)                       // "in-session fast path" (set @line 2030
     │                                              //  once boot/loading completes)
     │   ConnectionManager::LoginProtocolHandler(client[0x3295]=client+0x194a8, …); return;
     └─ else  → the big boot/loading state machine (constructs the session sub-objects:
                 client+0x194a0 ClientWorld, +0x194a8 ConnectionManager, +0x19578
                 ClientSceneManager — all built together in FUN_000851ae, torn down together
                 in FUN_004aef50 / jag::Client::FUN_000c04a0)

FUN_0047d3a0  (per-frame RENDER callback; referenced only from vtables 0x01063560/0x010c8928/
              0x013691f8 — invoked indirectly each frame)
  ├─ RCX = client[0x19578]  (ClientSceneManager)
  ├─ ESI = ClientSceneManager->+0x1c ; DIL = (ESI==0) ; client+0x518 = DIL   // lobby/world flag
  ├─ RDI = client+0x194a0  (ClientWorld)
  └─ if (RDI != 0)  JMP jag::game::ClientWorld_PerFrameTick(RDI)    // <-- THE TICK GATE
                                                                    //   (else RET, tick skipped)
```

`LoginProtocolHandler`'s per-frame dispatch (the relevant slice):

```c
// @0x0024e8c0, line ~1357
if (state != 1) {                                            // state = client[RELA 0xd40]
  if (ClientSceneManager->+0x1c == 0) {                      // LOBBY branch
      ScriptRunner::ProcessScripts(...);
      ProcessConnections(connMgr, 0);                        // mode 0
  } else {                                                   // WORLD branch  (REQUIRES +0x1c!=0)
      ProcessConnections(connMgr, 1);                        // mode 1
      // drain client+0x19b40 scene-node queue -> RELA[0xd00] ring buffer …
      ClientProt::SendSceneGraphReport(client[RELA 0xcf7]);  // op5  (line 1415)
      if (state == 0x1e) ClientProt::SendAppletFocusEvents(…);// op106 when state==30
      LoginManager::RunLoginStateMachine(...);
  }
}
// … later, LAB_0025000d region:
if (state == 0x1e) ClientProt::SendPingStatistics(client);  // op51  (line 2056, state==30 only)
// … function TAIL (reached every frame regardless of state):
sceneState = client[RELA 0xcf5];
if (!(sceneState->+0x650 && sceneState->+0x638!=-1 && sceneState->+0x63c!=-1)) return;  // streamer gate
if (sceneState->+0x634 == -1) return;
… FUN_00b14520(…, +0x638>>6, +0x63c>>6) …                   // map-square JS5 request
… jag::game::BuildArea_StreamMapSquares(…) …                // (the §3.5 chain)
```

## 12.2 The triple-arm gate inside the tick — needs zone-marked scene squares [VERIFIED]

`ClientWorld_PerFrameTick @0x0023e660`, tail (lines 3010–3056), verbatim shape:

```c
world = GetActiveWorld(SceneManager);          // RELA[0xd02]
if (*(world + 8) == 0) return;                 // (G1) active world content must exist (op81 -> FUN_00c55e80)
ss = client[RELA 0xcf5];                        // SceneState
armed = (ss->+0x634!=-1) & (ss->+0x638!=-1 && ss->+0x63c!=-1) & ss->+0x650;
if ((armed == 0) && (ss->+0x68c != '\0')) {     // (G2a) frustum-arm flag
    node = FUN_00490080(client);                 // local-player/anchor scene node
    if (*(node + 8) != 0) goto ARM;              // (G3) anchor node must be attached
} else {
    if (ClientWorld->+0x153 == '\0') return;      // (G2b) zone-bootstrap flag (set ONLY by op129)
    node = FUN_00490080(client);
    if (*(node + 8) != 0) goto ARM;               // (G3)
}
ARM:  // select camera by ss->+0x418==6 (render) vs +0x248 (transition);
      // GetWorldTranslation(cameraNode) -> FUN_00651040 writes triple ss->+0x634/638/63c
```

- **(G1)** is satisfied by op81 (`FUN_00c55e80 @0x00c55e80` allocates the 0xE8340-byte world
  content object and links it into `SceneManager+0x58/0x60`; `GetActiveWorld @0x0060f990`
  returns it via `SceneManager+0x70`). darkan's op81 is byte-correct, so **(G1) passes.**
- **(G3)** needs the local player's scene node attached (`FUN_00490080(client)->+8 != 0`).
  op81's GPI prefix places the local player; whether its scene node is attached at this
  point is the one field static analysis cannot resolve at runtime (see §12.4).
- **(G2)** is the killer for darkan: it needs `SceneState+0x68c != 0` OR
  `ClientWorld+0x153 != 0`.
  - `SceneState+0x68c` is set to 1 by `FUN_004932c0 @0x004932c0` (a per-frame scene-reset
    helper called from `MainLogic`) ONLY when `SceneState+0x678 (a pending-map-square count)
    > 0` — and `+0x678` is fed by the streamer's own return (`FUN_004eb110`, from the
    `LoginProtocolHandler` tail) and `SceneState_ProcessFrustumAndStream @0x0025bcb0`. This
    is a feedback loop that needs the streamer to have already run.
  - `ClientWorld+0x153` is the cycle-breaking bootstrap, and its **sole setter in the binary
    is `FUN_001dc7f0` = ServerProt op129** (a ZoneUpdates handler). No zone packet ⇒ `+0x153`
    stays 0 ⇒ (G2b) fails ⇒ triple never arms ⇒ streamer gate (§12.1 tail) never opens ⇒
    zero map-square requests + the camera-target triple stays `-1`.

## 12.3 Capture cross-check — production streams the whole scene before the client ticks [VERIFIED]

`build/undercut-socket-session-production-isaac.jsonl`, `conn=="world"`:

| milestone | world file-order | note |
|---|---:|---|
| op81 `RebuildNormalSimple` (S2C) | 1696 | size 5137 (Shape B) |
| op22 `PlayerInfo` (S2C, first) | 3315 | standalone GPI, AFTER ~1600 varps |
| op78 `UpdateZoneFullFollowsV2` (S2C, first) | 3318 | start of the **616×** zone stream (+ op76 ×64) |
| op3 `IfSetTopLevelInterface` (S2C) | 3991 | in-game HUD swap — AFTER zone stream begins |
| op199 `RebuildRegion` (S2C) | 5139 | |
| op75 `SetReadyFlag` (S2C) | 5145 | **LAST structural S2C** |
| **op240 (C2S, FIRST EVER world C2S)** | **5488** | size 7, payload `5e020d80083801` |
| op5 `SceneGraphReport` (C2S) | 5493 | payload `00000004` (then grows 0x68, 0xcc, 0x130 …) |
| op51 `Ping` (C2S) | 5524 | first of 31× |

S2C-op78 sent BEFORE the first C2S: **616 (all of them).** S2C count before the first C2S:
**3790 (the entire burst).** op5's payload is monotonic (`00000004`→`00000068`→`000000cc`→
`00000130`) — it is the **count of scene-graph map-squares the client has loaded**, growing
as the scene builds. The client is silent until the scene is built; op5 then reports the
node set, and the per-600ms tick (op51 ×31) begins.

darkan's world burst (from `WorldServer.kt` `sendWorldLoginCore` + the Step 14/15 dispatch):
`op81(Shape B, camRot=7)` → tokens/minimap/playerops/midi → `op5 ResetClientVarcache` →
(empty varp baseline) → `op55 DestroyZoneData` → `op1 SetNpcOp` → `op26 UpdateIgnoreListRaw`
→ `op3 IfSetTopLevelInterface(1477)` + HUD (op82/op110/op35) → `op75 SetReadyFlag`. It sends
**NO op22, NO op78/op76, NO op44 stats, NO op199**. `WorldServer.sendFirstLightTail()` (which
bundles stats + `NpcInfoThunk` + `PlayerInfoDecode×8` + `CutsceneData×8` + `SetReadyFlag`) is
**defined but never called** (dead code) — confirmed by grep.

## 12.4 The exact darkan fix (handoff to networking-protocol-engineer)

The residual is **NOT** camera, **NOT** JS5, **NOT** a single missing flag-packet. It is that
**darkan never streams the world's zone content, so the client's scene graph stays empty and
its scene-build phase (`ClientSceneManager+0x1c`) never leaves "configuring"** — which gates
off the world per-frame branch (op5/op51), the triple-arm (op129's `+0x153`), and therefore
the map streamer and all C2S.

**This is case (B): build the zone-streaming system.** The minimal, ordered world-entry
burst that the binary + capture say is required:

1. **op81 `RebuildNormalSimple`** (Shape B, coherent centre/build-area — already correct).
   Creates the world (G1) + resets camera (`+0x418`).
2. **A standalone op22 `PlayerInfo`** after the varp baseline (production sends one at idx
   3315). Ensures the local-player scene node is attached for **(G3)** — do NOT rely solely
   on op81's prefix for the node attachment that the triple-arm reads.
3. **The zone-content stream for every zone in the build area:** `op78
   UpdateZoneFullFollowsV2` per zone (production: 616×), plus `op76 UpdateZonePartialEnclosed`
   as needed, and the loc/obj/ground packets (op46 ObjAdd, op16 LocDel, …). **At least one
   zone packet must reach `FUN_001dc7f0`'s class (op129 is the proven `+0x153` setter; verify
   whether op78/op76 transitively mark scene squares too — see residual).** This is what
   populates the scene graph, arms `+0x153`/`+0x68c`, advances `+0x1c`, makes op5 have
   something to report, and opens the streamer gate.
4. **op44 `UpdateStat` / op52 `NpcInfo` / op35 `IfSetEvents`** as production does (HUD/entity
   content) — not strictly proven load-bearing for first render, but they are part of the
   real burst and harmless.
5. **op199 `RebuildRegion`** (production idx 5139) if/when the build area needs the full
   region rebuild path.
6. **op3 `IfSetTopLevelInterface`** AFTER the scene content (production: idx 3991, i.e. after
   the zone stream has begun) — not at seq ~20.
7. **op75 `SetReadyFlag` LAST** (production idx 5145, the final structural S2C). Re-enable
   `WorldServer.sendFirstLightTail()` and call it at the very end; STOP sending op75 at
   Step 15c against an empty scene.

Expected result if (B) is implemented: as zones stream in, the scene graph populates, the
tick advances `ClientSceneManager+0x1c` out of "configuring", the world branch engages, the
triple arms, the streamer pulls index-5 map groups, the client lifts "Loading" on op75, and
it begins emitting op240/op5/op51 — matching production.

## 12.5 Honest residual (what static analysis cannot finish) + a falsifiable pilot

Static analysis **proves**: the tick runs; the gates and their setters above; that op81 sets
none of `+0x68c/+0x153/+0x650`; that op129 is the sole `+0x153` setter and is a ZoneUpdates
handler; and that production streams the entire scene (616 op78) before any C2S. It **cannot**
resolve at runtime:

1. **The exact `ClientSceneManager+0x1c` 0→1→2 advance instruction.** The field is too
   generic for displacement scanning and the writer is buried in the 82 KB per-frame blob
   `FUN_000851ae`. Its SEMANTICS are nailed (build phase; 0="Running Auto Configuration",
   reported in op5; world branch needs `!=0`), and the dependency (scene-graph population
   via zone content) is proven via op5's body + the op129/`+0x153` chain — but the precise
   store was not line-located.
2. **Whether op78/op76 (not just op129) also flip `+0x153` / the scene-graph dirty state.**
   op129 is the proven setter; op78's handler (`@0x000f9510`) adds/removes scene-graph nodes
   on zone squares but was not shown to write `+0x153` directly. The implementation should
   stream the full ZoneUpdates set (op78 + op76 + op129 family) as production does, rather
   than betting on a single opcode.

**Falsifiable pilot prediction.** If the networking engineer implements §12.4 (stream op78
per build-area zone + a standalone op22, op3 after the stream, op75 last) and the client
STILL black-screens, the next discriminator is a **one-breakpoint dynamic read** right after
the burst: `ClientSceneManager+0x1c` (`client+0x19578`, +0x1c), `ClientWorld+0x153`
(`client+0x194a0`, +0x153), and `SceneState+0x650/+0x634` (`client[RELA 0xcf5]`). Predicted
healthy values once zones stream: `+0x1c != 0`, `+0x153 == 1` (during build), `+0x650 == 1`,
`+0x634 != -1`. If `+0x153` stays 0 after sending op78 (not op129), that confirms op129 (or a
specific ZoneUpdates opcode) must be sent explicitly. If `+0x1c` stays 0 with `+0x153==1`,
the `+0x1c` advance has an additional client-side precondition (asset-job completion) to
trace next. Either way the burst above is the highest-probability static fix and the exact
runtime signals to confirm it.

## 12.6 Function/offset reference (this pass, all headless-verified in 948-5)

| addr | name | role |
|---|---|---|
| `0x0047d3a0` | `FUN_0047d3a0` (per-frame render cb) | **tick dispatcher**: gates `ClientWorld_PerFrameTick` on `client+0x194a0 != 0`; reads ClientSceneManager `+0x1c` lobby/world flag |
| `0x0023e660` | `jag::game::ClientWorld_PerFrameTick` | the tick; triple-arm gates (G1/G2/G3) at lines 3010–3056; map-ready `+0x650` on job-drain (line 2622); "Running Auto Configuration" when `+0x1c==0` (line 2939) |
| `0x0024e8c0` | `jag::ConnectionManager::LoginProtocolHandler` | per-frame pump; **WORLD branch gated on ClientSceneManager+0x1c != 0** (line 1358); op5 (1415), op51 (2056); streamer tail gate (3007–3014) |
| `0x004b37f0` | `jag::Client::MainLogic` | calls LoginProtocolHandler (in-session fast path, line 153); sets `client+0x5334d=1` on boot-complete (line 2030); also calls `FUN_004932c0` |
| `0x00229f40` | `jag::ClientProt::SendSceneGraphReport` (op5) | emits when `state∈{20,23,30}` & dirty `+0x14 != 0` & active conn (lines 604–626); body walks scene-graph map-square rbtrees (`RELA[0x102a/b/c]`) |
| `0x001fca80` | `jag::ClientProt::SendPingStatistics` (op51) | emitted only when state==30 (line 2056) |
| `0x00261900` | `FUN_00261900` | op5 helper: copies `ClientSceneManager+0x1c` into the op5 body + a build-progress % |
| `0x00175120` | `SET_READY_FLAG` (op75) | sets `client[RELA 0xcfe]+0x10=1` (render-visibility "Loading" gate); sets nothing scene-related — production sends LAST |
| `0x001dc7f0` | `FUN_001dc7f0` = **ServerProt op129** (ZoneUpdates) | **sole setter of `ClientWorld+0x153=1`** (the triple-arm bootstrap); reads `+0x80`-transformed zone coord; bound in `ServerProt::BindHandlers @0x0007509a` |
| `0x000f9510` | `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS` (op78) | populates zone-square scene-graph nodes; production sends 616× before any C2S |
| `0x004932c0` | `FUN_004932c0` | per-frame scene helper (from MainLogic); sets `SceneState+0x68c=1` when `+0x678 (pending count) > 0` |
| `0x004eb110` | `FUN_004eb110` | latches `SceneState+0x678` from the streamer's returned map-square count (the §12.2 feedback) |
| `0x00490080` | `FUN_00490080` | returns the local-player/anchor scene node read by the triple-arm (G3) (`->+8 != 0` required) |
| `0x00c55e80` | `jag::game::SceneManager::FUN_00c55e80` | op81's world creator: allocs the 0xE8340 world-content object, links into `SceneManager+0x58/0x60`, sets active index `+0x70` |
| `0x0060f990` | `jag::game::SceneManager::GetActiveWorld` | `(+0x70==-1) ? &DAT_015df2b0 : SceneManager+0x58 + index*0x10` |

client field map (the world-session sub-objects, all constructed in `FUN_000851ae`, torn
down in `FUN_004aef50`/`jag::Client::FUN_000c04a0`):

| client offset | object | gate role |
|---|---|---|
| `+0x194a0` (`[0x3294]`) | **ClientWorld** (the tick `this`; `+8` = back-ptr to client) | tick dispatcher gate: `!= 0` to run the tick. `+0x153` = triple-arm bootstrap (set by op129) |
| `+0x194a8` (`[0x3295]`) | **ConnectionManager** | the `LoginProtocolHandler` `this`; non-null (darkan reached state 30) |
| `+0x19578` (`[0x32af]`) | **ClientSceneManager** (vtable `PTR_FUN_0136b698`) | **`+0x1c` = scene-build PHASE / lobby-vs-world flag** (0="Running Auto Configuration"). World branch + op5/op51 require `!= 0` |
| `+0x5334d` | byte flag | MainLogic "in-session fast path" (LoginProtocolHandler-only) once boot completes |
| `[RELA 0xcf5]` | **SceneState** | `+0x418` render state, `+0x650` map-ready, `+0x634/638/63c` camera triple, `+0x68c` frustum-arm, `+0x678` pending-square count |
| `[RELA 0xd40]` | int | login/main **state** (0x1e = 30 = in-world) |
| `[RELA 0xcfe]` `+0x10` | flag | op75 render-visibility ("Loading" gate) |
