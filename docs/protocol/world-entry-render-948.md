# RS3 NXT 948-5 — World-entry → rendered scene (why "Loading" never clears)

> **Scope.** This is the byte-precise, self-contained spec for getting the
> NXT 948-5 client OUT of the top-left "Loading" indicator and into a rendered,
> interactive world, after our Shape-A world bootstrap (which already keeps the
> client alive on the game socket). It answers, in priority order: **[P0]** what
> the client blocks on after the first PlayerInfo and the minimal trigger to
> render; **[P1]** how the build-area map loads (local cache vs JS5) and any XTEA
> need; **[P2]** the minimal world-entry interface/HUD + var sequence built as
> real systems; **[P3]** ordering/coherence constraints.
>
> **Owner for implementation:** `networking-protocol-engineer` (burst/ordering),
> with `js5-server-engineer` / `cache-library-engineer` for the map-asset path
> (P1).
>
> **Read first:** `docs/protocol/world-bootstrap-948.md` (op81 byte layout, GPI
> coherence, varp baseline — NOT duplicated here; this doc resolves the *render
> gate* that bootstrap left open). Builds on
> `docs/net/serverprot/948-research-A-state-var-zone.md` (varp/varc),
> `docs/net/serverprot/948-research-B-interface-social.md` (IF_* wire),
> `docs/net/serverprot/948-research-C-player-npc-misc.md` (PLAYER_INFO framing).
>
> **Evidence base.** Every claim is grounded in either (a) the 948-5 decompile
> (Ghidra program `rs2client.948-5` in `~/projects/reclass-data/rs2client-948`,
> read via headless `analyzeHeadless -process`), cited `@0xADDR`; or (b) the
> 0-desync golden production capture
> `build/undercut-socket-session-production-isaac.jsonl` +
> `~/.undercut/recordings/session-20260622-152729-production-isaac/`
> (`events.jsonl` carries the client's own `main_state` + per-packet opcode
> names; `network.pcapng` carries the JS5/content TCP), cited `[prod]`.
>
> **Note on the live GhidraMCP server:** its `Module.manifest` is malformed
> (`GHIDRA_MODULE_NAME=…` parse error) and port 8080 is taken by OrbStack, so the
> MCP HTTP bridge is down. All decompilation below was done **headless** via
> `ghidra-headless-class … rs2client.948-5 RS3DecompToFile.java process-noanalysis`.
> The headless path is fully functional and was used for every `@0xADDR` here.

---

## 0. TL;DR — the four answers

### P0 — what the client is blocking on, and the minimal trigger to render

**The "Loading" text is NOT `main_state`. `main_state` is already `30
(LOGGED_IN)` before our world burst even arrives** — proven by the client's own
hook: `[prod]` `events.jsonl` shows the only `main_state` transitions are
`10 (LOGIN_SCREEN) → 20 (LOBBY_SCREEN) → 30 (LOGGED_IN)`, and the 30-transition
fires at the **start** of the world connection (client cycle 14927), *one tick
before* op81. So the prior framing ("client stuck in a Loading main_state") is
wrong — we are already in-world state; we are stuck in a **render sub-state**.

**"Loading - please wait." is a GameShell *render-gate* sub-state**, a field at
`gameShell+0xd8` (the `PTR_s_Loading___please_wait__01365700` message table is
indexed by it). The screen-state-change handlers (`@0x002bcc10`, `@0x002440e0`)
set `+0xd8 = 10` to show the message and `+0xd8 = -1` to clear it. This sub-state
is independent of `main_state` and is what we are stuck in.

**The gate that clears it is `SET_READY_FLAG (op75)`.** Handler
`jag::packethandlers::ClientState::SET_READY_FLAG @0x00175120` (48 bytes, whole
body):

```c
undefined * SET_READY_FLAG(long *client) {
  worldState = client->RELA[0xd00];                 // game/world-state object
  tickSrc    = *(worldState + 0xe8);
  *(client->RELA[0xcfe] + 0x10) = 1;                //  <-- THE RENDER-READY FLAG := 1
  *(worldState + 0xf0)         = *(tickSrc + 0x10); //  snapshot server tick
  return &DAT_015d3620;                             // OK sentinel
}
```

It sets a **ready flag (=1)** on a client subsystem and snapshots the server
tick. `[prod]` confirms this is THE trigger: the client ingests the entire world
burst in a tight loop (100 buffered packets per cycle, cycles 14927–14961, no
real ticking), then at **cycle 14961 → 14976 it jumps 15 cycles in one frame**
(the heavy scene build + map load) and **emits `op75 SET_READY_FLAG` exactly at
the boundary**; from cycle 14976 on it advances in real ~600 ms ticks with 3–6
`PLAYER_INFO` per tick — i.e. **rendered, interactive, ticking**. op75 is the
last "logic" packet of the burst; everything after it is steady-state.

**Why OUR client never renders:** our 18-packet burst **ends at
`UpdateIgnoreListRaw` and never sends `op75 SET_READY_FLAG`** (confirmed against
the brief's burst list). So the render-ready flag is never set, the GameShell
`+0xd8` loading sub-state never clears, and — because the client gates
scene-asset (map) loading behind being render-ready — **it never progresses to
request map groups over JS5.** The black screen + permanent "Loading" + zero JS5
group requests is one single failure: **the render gate (op75) is missing.**

**Minimal trigger to leave "Loading":** after a *coherent* op81 (correct spawn
zone, per `world-bootstrap-948.md` §4 — that part is already done in Shape A) and
the first GPI, **send `op75 SET_READY_FLAG` (opcode 75, size 0, empty body)**.
That is the single load-bearing addition. It must come *after* the scene-build
(op81) and the first PlayerInfo exist (§3 ordering). Everything else in the
production burst (the 1598 varps, 871 IfSetEvents, zone content) is HUD/content
population, NOT the render gate.

> **One nuance:** op75's handler only *sets a flag*; it does not itself verify
> the scene is built. The render loop reads that flag AND requires a built scene
> (op81 must have run and the BuildArea allocated). With a coherent Shape-A op81
> the scene IS built, so op75 will trip rendering. If op81 is incoherent (wrong
> zone / failed magic), op75 will set the flag but the scene is empty → you would
> still see a black/partly-built world. So: **coherent op81 (already done) + op75
> (the fix) = render.**

### P1 — how the build-area map loads: **JS5/content server, triggered by world entry; not pure local cache; no XTEA packet needed**

`[prod]` `network.pcapng` shows the production client downloaded **~4.16 MB from
the content/JS5 CDN `172.64.155.209:443`** across the session, plus an **83 KB
burst from `184.25.255.178` at t=102.8 s — ~3 s into the world session (world
connected at t=99.9 s)**. That 83 KB post-world-entry burst is the **build-area
map-square / scene-asset load**, fetched **over the network**, *triggered by
world entry*. The map is therefore **JS5/content-served, not read from a
complete pre-existing local cache** (the client streams what it doesn't already
have). Our server's client made **zero group requests** purely because it never
got render-ready (P0) — i.e. this is an **init-gap, not a cache/decrypt gap**.

**XTEA:** the op81 body carries **no map-square group ids and no XTEA keys** (the
5119-byte prefix is the GPI bitstream, decoded below; the 18-byte header is
origin/span zones only — `world-bootstrap-948.md` §1.3). The client loads map
squares for the build-area zones itself and (for **F2P Lumbridge**, the spawn
here) the map data is **unencrypted (XTEA key = 0)**. So **no map-keys packet is
required for the spawn build area.** (Members/instanced regions DO need per-square
XTEA keys delivered in the rebuild; that is out of scope for first-light at
Lumbridge and is flagged in §6.)

**Consequence for our stack:** once op75 makes the client render-ready, it WILL
start requesting map groups over JS5. Our JS5 server (`JS5Server.kt` +
`world.gregs.voidps.cache.file.FileProvider`) must then serve **index 5 (maps)**
groups for the Lumbridge build area on demand (it already serves the master index
correctly). If those groups are missing/uncompressed-wrong, the *next* failure
after the op75 fix will be a half-built scene — but that is a cache-completeness
problem, downstream of this doc.

### P2 — the minimal world-entry interface/HUD + var sequence (real systems)

Built as **real systems**, the smallest correct render sequence is (full detail
in §2):

1. **op81** coherent rebuild (Shape A, already done) — builds the BuildArea.
2. **op5 RESET_ALL_VARPS** then a **varp baseline** — *required for a non-broken
   HUD*, but the **minimal** baseline is "the player's saved varp map"
   (cache-valid ids only), NOT a fixed list and NOT the full 1598-id production
   dump. No single varp is a render gate (§2.4).
3. **First GPI** — Shape A: a standalone `op22 PlayerInfo` init (local 30-bit
   spawn tile); or Shape B: the GPI bundled in op81's prefix.
4. **HUD open as a real system:** `op3 IF_SETTOPLEVELINTERFACE` setting the
   gameframe root **1477**, then the child interfaces opened/positioned into
   1477's slots via `op82 IF_SETPOSITION` (component-hash parent = 1477) and
   driven by `op110 RUNCLIENTSCRIPT` (the HUD-build CS2, script **8862**) +
   `op35 IF_SETEVENTS`. This is what production does at `[prod]` idx 2292–2334;
   it is **NOT render-required** but IS required for an interactive HUD.
5. **op75 SET_READY_FLAG** — the render gate (P0). Production sends it LAST.

The **"joining the game world" dialog is client-side**, not a server packet — it
is the GameShell `+0xd8` loading message shown *during* the heavy build frame and
cleared by op75. Do not try to send it.

### P3 — ordering (see §3)

`op81 (scene build)` → `op5 + varp baseline` → `first GPI (op22 / op81-prefix)` →
`HUD open (op3/op82/op35/op110)` → **`op75 SET_READY_FLAG` LAST**. op75 must
follow a built scene; varps must follow op5; the HUD open must follow op3. Out of
order, the client either renders an empty/default HUD or never renders.

---

## 1. The "Loading" render-gate state machine (P0, decompile-grounded)

### 1.1 `main_state` is not the loading screen

`[prod]` `events.jsonl` tags every tick + packet with the client's live
`main_state`. The ONLY transitions in the whole session:

| cycle | main_state | name | context |
|------:|-----------:|------|---------|
| 9567  | 10 | LOGIN_SCREEN | app start |
| 14424 | 20 | LOBBY_SCREEN | after lobby login (1670 lobby S2C packets) |
| 14927 | 30 | **LOGGED_IN** | **at world connect, BEFORE op81** |

So when our world burst arrives, the client is **already `main_state 30
LOGGED_IN`**. The black "Loading" screen is a **render sub-state inside
LOGGED_IN**, not a distinct main_state. This corrects the working assumption.

### 1.2 The loading message is `gameShell+0xd8`

The string table `PTR_s_Loading___please_wait__01365700` (and siblings
`PTR_s_Please_wait___attempting_to_rees_013656c0`,
`PTR_s_Please_wait____01365680`) is indexed **by a state int** and written by the
GameShell screen-state-change handlers:

- `FUN_002bcc10(shell, prevState, newState)` `@0x002bcc10` — on `newState==0x28`
  (40) / `0x23` (35) / `0x25` (37): if `shell.state(+0xd8) == -1 || >9` then set
  `+0xd8 = 10` and `+0x30 = "Loading - please wait."` On *leaving* the loading
  state (tail, `@0x002bccb8`): `if (+0xd8 == 10) +0xd8 = -1` and clear the string
  → **the message disappears.**
- `FUN_002440e0(shell, prevState, newState)` `@0x002440e0` — same family, sets
  `+0xd8 = 5` + "Loading - please wait." for a different screen.

These handlers are **vtable methods** (only INDIRECTION/DATA xrefs — they are
per-screen `onStateChange` slots), confirming the loading indicator is a generic
GameShell screen sub-state, not packet-specific. The state codes
`10/20/30/35/37/40` are the GameShell/login connection phases (30 == LOGGED_IN,
matching `[prod]`).

### 1.3 op75 SET_READY_FLAG clears it — the gate

Full handler at §0/P0. It sets `client.RELA[0xcfe]->(+0x10) = 1` (the
render-ready flag) and snapshots the server tick into the world-state
(`worldState+0xf0 = tickSrc+0x10`). The render/main loop consumes this flag to
leave the loading sub-state and begin per-tick scene rendering.

**Empirical proof it is the gate** `[prod]` (world S2C grouped by client cycle):

```
cyc 14927..14961 : 100 pkts/cycle  (tight burst ingest — op81 at 14927; PLAYER_INFO/CAMERA at 14943;
                                     IF_SETTOP/RUNCS at 14949; SCENE_FLAG at 14953; CLEAR_PENDING at 14960;
                                     REBUILD_REGION at 14961)   ← NOT real ticks, just socket drain
cyc 14961 -> 14976 : +15-cycle JUMP in one frame              ← heavy scene build + map load
cyc 14976          : 19 pkts incl. SET_READY_FLAG (op75), CHANGE_LOBBY (op49)   ← GATE TRIPS
cyc 15015,15042,…  : 3–6 PLAYER_INFO per cycle, ~30-cycle steps  ← REAL TICKS, rendered & interactive
```

The render onset is exactly co-incident with op75. Our burst omits op75 → the
flag is never set → permanent loading sub-state.

### 1.4 op81's own tail does NOT clear loading

For completeness: `REBUILD_NORMAL_SIMPLE @0x001daa70` builds the scene
(`game::SceneManager::FUN_00c55e80(... packedA>>6, packedB>>6 ...)` — the
BuildArea ctor `FUN_00c53680`, 10809 bytes) and at its tail gates a camera reset
on the world-state phase:

```c
worldState = client->RELA[0xcf5];
if (*(worldState + 0x418) == 6) return &DAT_015d3620;   // skip camera reset in phase 6
game::Camera::ProcessCameraReset(worldState, sceneRoot->...+0xd8);
```

It does **not** touch the GameShell `+0xd8` loading flag and does **not** set the
render-ready flag. So op81 alone (even coherent) leaves the client in the loading
sub-state until op75 arrives. The `worldState+0x418` phase value and the
`gameShell+0xd8` loading state are distinct objects; do not conflate them.

---

## 2. The world-entry sequence (P2) — what `[prod]` sends and what is minimal

### 2.1 Production world S2C burst shape `[prod]`

3790 world S2C packets. Milestone ordinals (index = world-S2C packet ordinal):

| idx  | cyc   | op  | client name | role | render-required? |
|-----:|------:|----:|-------------|------|------------------|
| 0    | 14927 | 81  | REBUILD_NORMAL_SIMPLE | scene build + bundled GPI | **YES** (coherent op81) |
| 1    | 14927 | 54  | HASHED_WORLD_TOKEN | session token (44B str) | no |
| 2    | 14927 | 73  | MINIMAP_STATE | minimap mode (2B) | no |
| 3    | 14927 | 74  | **SET_DISPLAY_INT** (=Jcoins) | display int (4B) | no |
| 4/5  | 14927 | 172/204 | SET_PLAYER_FLAG_A / _B | player flags (1B) | no |
| 6–12 | 14927 | 17  | SET_PLAYER_OP ×6 + 95 MIDI_SONG | right-click ops + music | no |
| 13   | 14927 | 5   | **RESET_ALL_VARPS** | clears varp cache | **YES** (before baseline) |
| 14–1616 | 14927 | 28/61/147 | **varp baseline** (1598 ids) | account var dump | partial (see §2.4) |
| 1617 | 14927 | 55  | DESTROY_ZONE_DATA | zone reset | no |
| 1618 | 14927 | 1   | SET_NPC_OP | npc right-click op | no |
| 1619 | 14943 | 22  | **PLAYER_INFO** (first per-tick) | GPI | **YES** (a GPI must exist) |
| 1620 | 14943 | 77  | CAMERA_UPDATE | camera (121B) | no |
| 1621 | 14943 | 130 | UPDATE_IGNORELIST(raw) | social | no |
| 1622+| …     | 78/76/46/16/90 | zone content | locs/objs/ground | no |
| 2292+| 14949 | 110/3/82/35 | **HUD open** (script 8862, root 1477) | gameframe | no (HUD, not scene) |
| 3449 | 14976 | 75  | **SET_READY_FLAG** | **RENDER GATE** | **YES — the fix** |
| 3463 | 14976 | 49  | CHANGE_LOBBY | lobby-context flag | no |

(Client opcode names above are the client's OWN names from the `[prod]` hook;
e.g. op61 is **SET_VARBIT_NEG**, op74 is **SET_DISPLAY_INT**, not the darkan
codec names. Wire is identical; names differ.)

### 2.2 op81 prefix + header — re-confirmed from `[prod]` bytes

op81 production body = 5137 bytes = `[5119-byte GPI prefix][18-byte header]`.
Decoded from the actual capture payload:

- **GPI prefix, first 30 bits = `0x0328CCA2` → plane 0, x 3235, y 3234 → zone
  (404, 404)** (Lumbridge). Leads with the absolute tile directly (no
  `localHasUpdate` prefix bit) — this is the **first-transmission** GPI form.
- **18-byte header** (last 18 bytes `ff9401850194870001da01a00940048e23b8`):
  `coordZ(LE u16)=404`, `magic=0x85 ✓`, `coordX(BE u16)=404`,
  `cameraRotation=7` (wire 0x87 = value+0x80), `targetWorldId(BE u16)=474`,
  `packedA(BE u32)=0x01A00940`, `packedB(BE u32)=0x048E23B8`.
  `packedA>>6 per 14-bit field = (26, 37)`, `packedB>>6 = (72, 142)`.

Byte layout, magic check (`if (cVar4 != -0x7b) return &DAT_015d35c0`, `-0x7b ==
0x85`), and `DecodePackedCoord` are unchanged from `world-bootstrap-948.md` §1 —
not re-derived. **Coherence (spawn zone == header centre == GPI tile) is the
Shape-A requirement already implemented; this doc does not change it.**

> The `packedA>>6 = (26,37)` / `packedB>>6 = (72,142)` values are the build-area
> origin/span as the live server encodes them. They do NOT match a naive
> "centre 404 ± 6 zones" window, which is the unresolved `packedA = corner vs
> centre` ambiguity flagged in `world-bootstrap-948.md` §8 (lives in the 9 KB
> BuildArea ctor `FUN_00c53680`). It does not affect the render gate.

### 2.3 The HUD open as a real system (root 1477)

`[prod]` idx 2292–2334 builds the gameframe. Decoded:

- **`op3 IF_SETTOPLEVELINTERFACE`** (19B, `@0x00186900`) — sets the top-level
  gameframe root. The component-hash high word across the subsequent
  `IF_SETPOSITION` calls is `0x05C5 = **1477**` (the modern RS3 resizable
  gameframe). Children are mounted into 1477's component slots.
  *(op3's own id field at off 9–10 is the complex g2_alt2 form from
  `948-research-B` §IF_SETTOPLEVELINTERFACE; the parent hash in op82 below is the
  authoritative source of "root = 1477".)*
- **`op82 IF_SETPOSITION`** (23B, `@0x00189180`) — repeated, each
  `7f 00000000 c505 CCCC 00…00 SSSS`: layer byte `0x7f`, **component hash
  `g4_alt3[5..8] = 0x05C5_00CC` = (interface 1477, component CC)**, trailing
  `BE/LE u16 SSSS` = the child sub-interface mounted into that slot. Examples:
  slot 31 ← 1482; many children (1349, `0x07xx`, `0x05xx`). This is the HUD panel
  layout.
- **`op110 RUN_CLIENT_SCRIPT`** (`@` RunCS handler) — the HUD-build CS2.
  Structure: `[arg-type descriptor: 0x69='i' per int arg][BE-int arg values]
  [scriptId as gSmart2or4s (2 trailing bytes, e.g. 0x229E → script 8862)]`.
  The repeated `6969 …00 XX 00 …229E` (script **8862**, one int arg = tab index
  0,2,3,4,5,14,15,…) builds each HUD tab. Two earlier `69…3FAC` (script 16300)
  and `69…029F` (script 671) run once.
- **`op35 IF_SETEVENTS`** (12B, `@0x00185f60`) — sets per-component event masks
  (right-click/use/drag settings) on the HUD components. ~871 in the full stream;
  these enable interaction, not rendering.

**Minimal HUD:** to *render the scene* you do not need any of the HUD packets —
the scene renders behind the HUD. To have a *usable* client you need at least
`op3` (root 1477) + the core panel `op82`/`op110(8862)` set. Build it as the real
1477 interface system, not a byte replay.

### 2.4 The varp baseline — minimal vs full

- **Format (re-confirmed from `[prod]` bytes):**
  - `op28 SET_VARP_INT` (6B, `@0x00119910`): `value(BE i32)` + `id(BE u16)`.
    e.g. `944000840003` → id 3, value −1807744892.
  - `op61 SET_VARBIT_NEG` (3B, `@0x00119b30`): `id(BE u16)` +
    `value(1 byte, decode = (-0x80 - raw) & 0xFF)`. e.g. `001b81` → id 27, value
    255 (−1); `006f4e` → id 111, value 50. (Client name is **SET_VARBIT_NEG**;
    this is the negate/subtract-transform small varp. darkan's `op61` encoder
    must use **BE id + `(-0x80 - value)` transform** — confirms
    `world-bootstrap-948.md` §2.2.)
  - `op147 SET_VARP_LONG` (8-byte value form, `@0x00141690`): 5 of them.
- **Volume `[prod]`:** 1598 distinct ids (441 op28 + 1157 op61 + 5 op147), ids
  3–12863, monotonically increasing — the **entire saved account varp table**,
  dumped between `op5` (idx 13) and the first `op22` (idx 1619).
- **Is it render-required? NO single varp is.** `ProcessPlayerInfo @0x001618a0`
  and `SET_READY_FLAG @0x00175120` consult **no varp predicate**
  (`948-research-C` §1; re-confirmed). The scene renders without varps.
- **But the HUD-build CS2 (script 8862 etc.) reads varps** to populate skills,
  settings, orbs. With an empty baseline they read defaults; a *missing-but-
  required* var can drive a CS2 error → the NXT client's response is to shut down
  (the clean-quit failure documented in `world-bootstrap-948.md` §0). So the
  baseline is *practically* required for a stable HUD, *not* for the bare scene.
- **HARD HAZARD (unchanged):** `GetVarType @0x005addf0` returns NULL for an id
  the loaded config cache doesn't define, and the varp setters deref it with no
  NULL check → SIGSEGV. **Only emit ids that exist as `VarType` in the client's
  cache, exact length.** Generate from the account's saved var map (a subset of
  real ids by construction); never a hardcoded list with possibly-stale ids.
- **Minimal recipe:** after `op5`, iterate the player's saved varp map; for each
  non-default `(id, value)`: byte-fits-transform → `op61` (`writeShort(id);
  writeByte((-0x80 - value) & 0xFF)`), else `op28` (`writeInt(value);
  writeShort(id)`), 64-bit → `op147`. For a *bare* first-light you may send an
  empty/tiny baseline **iff** you also skip the HUD CS2 that would read missing
  vars (accept a blank HUD). The render gate (op75) + coherent op81 are what get
  you the scene either way.

---

## 3. Ordering & coherence (P3)

Required order of the load-bearing packets:

```
1. op81  REBUILD_NORMAL_SIMPLE   — builds BuildArea; spawn zone coherent (Shape A, done).
2. op5   RESET_ALL_VARPS         — must precede the varp baseline.
3. varp baseline (op28/op61/op147) — cache-valid ids only; between op5 and the first GPI.
4. first GPI:
      Shape A: standalone op22 PLAYER_INFO init (local 30-bit = spawn tile), OR
      Shape B: GPI bundled in op81 prefix (no standalone op22 on tick 1).
   (Pick ONE source of the initial GPI — never both; world-bootstrap-948.md §5.)
5. HUD open: op3 IF_SETTOPLEVELINTERFACE (root 1477) → op82 IF_SETPOSITION (children
   into 1477 slots) → op110 RUN_CLIENT_SCRIPT (HUD CS2 8862) → op35 IF_SETEVENTS.
   (HUD only; not the render gate. Skippable for a bare scene.)
6. op75  SET_READY_FLAG          — LAST. The render gate. Empty body (size 0).
```

What the client does if something is missing/out of order:

- **op75 missing** → permanent "Loading" sub-state, no scene render, **no map JS5
  requests** (our current bug). This is the single fix.
- **op81 incoherent** (wrong zone / magic fail) → handler aborts
  (`return &DAT_015d35c0`) or builds the scene at the wrong place; op75 then makes
  the client "ready" over an empty/wrong scene → black/half world.
- **varps before op5** → cleared by the subsequent `RESET_ALL_VARPS`; always op5
  first.
- **HUD before op3** → child `op82`/`op35` reference a non-existent parent 1477 →
  ignored or CS2 error.
- **op75 before scene exists** → flag set but nothing to render; render loop shows
  a black ready scene.

Production order (the canonical reference): scene-build → player-ops → op5 → full
varp baseline → op55/op1 → first op22 → zone content → HUD/interfaces → **op75
last**.

---

## 4. The map-asset path (P1) — local cache vs JS5, in detail

### 4.1 Evidence the map is network-loaded at world entry

`[prod]` `network.pcapng` TCP analysis (tcpdump, inbound bytes per remote):

| remote | bytes in | first–last (rel s) | identity |
|--------|---------:|--------------------|----------|
| `8.42.17.253:443` | 18 339 | 90.5–100.1 | **lobby** (matches capture lobby s2c) |
| `8.26.16.145:443` | 47 350 | 99.9–136.5 | **world** (matches capture world s2c) |
| `172.64.155.209:443` | **4 164 467** | 0.0–136.6 | **content/JS5 CDN** (persistent) |
| `184.25.255.178:443` | 83 345 | **102.8–102.9** | **post-world-entry asset burst** |
| `184.25.255.186:443` | 575 192 | 96.2–96.6 | login-time asset prefetch |

The 83 KB burst at t=102.8 s (≈3 s after world connect at 99.9 s, well after the
world burst) is the **build-area scene-asset / map-square load**, fetched **over
the network**. The 4 MB persistent CDN connection is the always-on content
stream. **The client does NOT rely on a complete local cache for the spawn map —
it streams what it lacks.**

### 4.2 What triggers it, and the index/group/XTEA answer

- **Trigger:** being **render-ready** (op75) + the BuildArea allocated (op81).
  The client computes the build-area zones from op81's header (`packedA/packedB
  >> 6`) and requests the covering **map groups from JS5 index 5 (maps)** on
  demand. Our client never triggers this because op75 is missing (P0).
- **Index/group ids:** map squares are addressed in **JS5 index 5** by
  `"m{mapsquareX}_{mapsquareY}"` (terrain) and `"l{mapsquareX}_{mapsquareY}"`
  (locs) group names, where mapsquare = `zone >> 3` (mapsquare = `tile >> 6`).
  For Lumbridge spawn tile (3235, 3234): mapsquare = (50, 50). The build area
  (13×13 zones ≈ 104 tiles) spans mapsquares ~49–51 in each axis. The client
  resolves these group ids itself from the build-area coords — **the server does
  not send group ids in op81.**
- **XTEA:** the spawn build area is **F2P Lumbridge → map data is unencrypted
  (XTEA key = 0)**. op81 carries no keys and none are needed for first-light at
  Lumbridge. (`world-bootstrap-948.md` §1.3: the prefix holds no XTEA.) Members /
  instanced / higher-security regions DO require per-square XTEA keys; for those
  the rebuild form carries keys (the `REBUILD_REGION`/world-entity forms, op199 /
  op186) — **out of scope here, flagged §6.**

### 4.3 Net for our stack

After the op75 fix, the client will issue index-5 group requests for the
Lumbridge mapsquares. Our `JS5Server.kt` + `FileProvider` must serve them (it
already serves the master index correctly). If a group is absent or compressed
wrong, the post-op75 symptom changes from "permanent Loading" to "black/partial
scene that renders" — a **cache-completeness** issue downstream of this doc, for
`js5-server-engineer` / `cache-library-engineer`.

---

## 5. Concrete changes for `networking-protocol-engineer`

1. **Send `op75 SET_READY_FLAG` (opcode 75, size 0, empty body) at the END of the
   world-entry burst** — after op81 and the first GPI. This is THE fix that
   clears "Loading" and lets the client build/render the scene and begin
   requesting map groups over JS5.
   - **Verified against server source:** the live world path
     `WorldServer.handleWorldLogin` → `sendWorldInitPackets` →
     `sendWorldLoginCore` ends at `UpdateIgnoreListRaw()` + `flush()`
     (`WorldServer.kt` Step 14–16, ~line 397–409) and **never sends op75**.
   - The codec already exists (`ServerProt.SetReadyFlag`,
     `Rev948ServerCodecsMisc` `serverProt<SetReadyFlag>(opcode = 75, size = 0)`)
     and there is already a function `WorldServer.sendFirstLightTail()` (line 614)
     that ends with `session.send(SetReadyFlag())` — **but it is defined and never
     called.** Wiring op75 in is a one-line change: call (the relevant part of)
     `sendFirstLightTail`, or add `session.send(SetReadyFlag())` after the first
     GPI burst. Send it LAST.
2. **Keep the Shape-A op81 coherence** (spawn zone == header centre == GPI tile)
   from `world-bootstrap-948.md` §4/§7 — that is a precondition for op75 to render
   a *correct* scene rather than a black ready one.
3. **Emit a varp baseline from the account's saved var map** (op28 BE-int+id;
   op61 BE-id + `(-0x80 - value)` byte; op147 long), between `op5` and the first
   GPI, **cache-valid ids only, exact length** (§2.4 — unknown id SIGSEGVs). For a
   first bring-up a *small* baseline is acceptable **iff** you also gate off the
   HUD CS2 that reads missing vars.
4. **Build the HUD as the real 1477 gameframe** (op3 root 1477 → op82 children
   into 1477 slots → op110 script 8862 → op35 events) once the scene renders.
   Optional for a bare scene; required for an interactive client. Do not replay
   captured bytes — open 1477 as a real interface.
5. **Do NOT send a "joining game world" packet** — it is the client-side GameShell
   loading message, shown automatically during the build frame and cleared by
   op75.
6. After this, expect the client to request **index-5 map groups** for Lumbridge
   mapsquares (~49–51) over JS5 — coordinate with `js5-server-engineer` to serve
   them (XTEA 0 for F2P Lumbridge).

---

## 6. Remaining unknowns / hand-offs

- **Exact render-loop consumer of the op75 ready flag.** op75 sets
  `client.RELA[0xcfe]->(+0x10) = 1`; the function that *reads* it to leave the
  loading sub-state lives in the per-frame GameShell update, which is an unnamed
  `FUN_` (binary is stripped — only `Camera::ProcessCameraReset` and
  `RebuildSceneEntry::Reset` are named in this subsystem). The behavioral proof
  (op75 ↔ render onset at cyc 14976) is conclusive; the precise reader was not
  pinned. If after sending op75 the client still won't render, decompile the
  GameShell frame update and confirm it also requires a built BuildArea +
  loaded mapsquares (i.e. the failure has moved to the cache path, §4.3).
- **`worldState+0x418` phase values.** op81's tail skips the camera reset when
  `+0x418 == 6`; the full phase enum (other values, who sets them) was not
  enumerated. Not render-blocking for first-light.
- **packedA = window corner vs centre** (op81 header). Production
  `packedA>>6 = (26,37)`, `packedB>>6 = (72,142)` do not match a naive centred
  window; the mapping lives in the 10809-byte BuildArea ctor `FUN_00c53680` and
  was not fully reversed. ≤ half-window scene offset; not the render gate. Capture
  one live op81 if the scene looks shifted.
- **Members / instanced map XTEA.** The Lumbridge spawn is XTEA-0; any non-F2P or
  instanced first-light needs per-square XTEA keys delivered via the appropriate
  rebuild form (op199 REBUILD_REGION / op186 REBUILD_WORLDENTITY — opcode/handler
  in `948-research-A`; wire not byte-detailed here). Flag for a follow-up if the
  spawn moves off Lumbridge.
- **Which exact varps the HUD CS2 (8862, 16300, 671) require.** Not enumerated
  (would mean tracing every `op110` arg + its CS2 var reads). Safe path: dump the
  account's saved vars. If a specific CS2 still asserts with a full account dump,
  trace that script.
- **op49 CHANGE_LOBBY at cyc 14976** (sent alongside op75) sets a lobby-context
  flag; it was not byte-detailed and is not believed render-load-bearing. Note it
  if the post-op75 client misbehaves.
- **Client opcode-name vs darkan-codec-name drift.** The `[prod]` hook uses the
  client's own names (op61 SET_VARBIT_NEG, op74 SET_DISPLAY_INT, op172/204
  SET_PLAYER_FLAG_A/B). Wire bytes match the darkan codecs; only the labels
  differ. Don't let the names cause a re-encode.

---

## 7. HUD world-entry sequence — implementation-grade (deepens §2.3/§P2)

> This section adds the byte-precise HUD setup that §2.3 described conceptually:
> the **root-1477 component map**, the **op82 / op3 / op35 / op110 wire formats**,
> the full **script-8862 tab list**, and the **fresh-account default-var**
> guidance. All `[prod]` from the golden capture. **None of this is render-gated**
> (the scene renders behind the HUD via op75); it is required for an *interactive*
> HUD, and for the world-entry CS2 not to assert on missing vars.

### 7.1 `op3 IF_SETTOPLEVELINTERFACE` — open root 1477

`@0x00186900`. Production `[prod]` sends it once at world-entry (idx 2295), body
19 bytes. The authoritative "root = 1477" comes from the op82 parent hash
(§7.2) — every child mounts into interface `0x05C5 = **1477**` (the modern RS3
resizable gameframe). Send `IF_SETTOPLEVELINTERFACE` for root **1477** first;
the children below are invalid without it.

### 7.2 `op82 IF_SETPOSITION` — the root-1477 child component map (DEFINITIVE)

`@0x00189180`, body **23 bytes**:

```
7f  00 00 00 00  C5 05  CC CC  00 00 00 00 00 00 00 00 00 00 00 00  SS SS
└┬┘ └────┬────┘  └─┬─┘  └─┬─┘  └──────────────┬───────────────────┘  └─┬─┘
layer   (4 bytes  parent  comp        (10 bytes zero padding)         child
=0x7f    zero)    iface   slot                                        iface
                 0x05C5  (LE u16)                                     (LE u16)
                 =1477
```

- **parent iface** (bytes 5–6, LE) = `0x05C5` = **1477** for every entry → the
  full component-hash is `(1477 << 16) | slot`.
- **comp slot** (bytes 7–8, LE u16) = which 1477 component the child mounts into.
- **child iface** (last 2 bytes, LE u16) = the sub-interface placed there.

The complete `[prod]` 1477 child map (slot ← child), 56 placements:

| 1477 slot | child | | 1477 slot | child | | 1477 slot | child |
|----:|----:|---|----:|----:|---|----:|----:|
| 31 | 1482 | | 158 | 1452 | | 461 | 1470 |
| 300 | 1466 | | 224 | 1219 | | 471 | 464 |
| 103 | 1473 | | 235 | 1220 | | 481 | 1529 |
| 114 | 1464 | | 246 | 1221 | | 501 | 550 |
| 136 | 1458 | | 311 | 1416 | | 512 | 1110 |
| 169 | 1461 | | 343 | 1588 | | 523 | 1519 |
| 147 | 1460 | | 354 | 1678 | | 545 | 1417 |
| 180 | 1884 | | 376 | 190 | | 556 | 1427 |
| 191 | 1885 | | 387 | 1854 | | 613 | 291 |
| 202 | 1887 | | 398 | 1894 | | 617 | 284 |
| 213 | 1886 | | 409 | 590 | | 621 | 1483 |
| 257 | 1883 | | 420 | 137 | | 634 | 745 |
| 268 | 1449 | | 431 | 1467 | | 668 | 1213 |
| 279 | 1882 | | 441 | 1472 | | 691 | 568 |
| 64 | 1431 | | 451 | 1471 | | 715 | 1448 |
| 70 | 1430 | | 95 | 1465 | | 726 | 1281 |
| 43 | 994 | | 96 | 1919 | | 797 | 653 |
| 805 | 1433 | | 814 | 1488 | | 911 | 1847 |

> **Minimal interactive HUD subset (not the full 56):** the load-bearing panels
> are the inventory/equipment/skills/orbs cluster. The core children to mount for
> a usable client are roughly: **1473** (slot 103, the main backpack/inventory),
> **1464** (114), **1458** (136), **1461** (169, the combat/stats group), plus
> the minimap/orbs (**1465**/**1466**). You can stage the rest. The full map above
> is what a fully-built gameframe looks like; build it as the real 1477 interface
> system, not a byte replay.

### 7.3 `op110 RUN_CLIENT_SCRIPT` — HUD-build scripts (DEFINITIVE arg lists)

`@` RunClientScript handler. Wire structure:
`[arg-type descriptor bytes, each 0x69 = 'i'(int)][... 0-terminator ...][int args, BE][scriptId as gSmart2or4s, trailing]`.

Three scripts drive HUD setup `[prod]`:

- **Script 16300** (`0x3FAC`): `69 00000000 00000000 3fac` — one int arg `0`,
  run **once** (the gameframe master setup).
- **Script 671** (`0x029F`): `69 00000000 00000000 029f` — one int arg `0`, run
  **once**.
- **Script 8862** (`0x229E`): `69 69 00 [int1 BE] [int2 BE] 00 00 22 9e` — **two**
  int args, run **22 times**, once per HUD tab/panel. `(arg1, arg2)` observed
  `[prod]`, arg2 = the **tab/component index**:

  ```
  (1,0) (1,2) (1,3) (1,4) (1,5) (1,9) (1,10) (1,11) (0,12) (1,14) (1,15) (1,16)
  (1,27) (1,28) (1,29) (0,30) (1,31) (1,32) (1,41) (0,45) (0,46) (0,1025)
  ```

  i.e. `RUN_CLIENT_SCRIPT(8862, [arg1, tabIndex])` for each tab. arg1 is a
  visibility/enable flag (0 or 1). **Run 16300 and 671 once first, then 8862 per
  tab.** These are the scripts that populate skills/orbs/settings tabs and read
  the player's varps (§7.5).

  (Two further one-off scripts appear in the burst — `0x2B89`=11145 and
  `0x20E4`=8420 — with mixed int/string/component args; they are
  cosmetic/secondary and not required for a minimal HUD.)

### 7.4 `op35 IF_SETEVENTS` — per-component event masks

`@0x00185f60`, body **12 bytes**: `[fromSlot u16][toSlot u16][?? u16][?? u16][componentHash u32][events u16]` — sets the right-click/use/drag event bitmask on a
range of components of a HUD interface (the `componentHash` high word is the
interface id, e.g. `0x05C1`=1473, `0x05BA`=1466). Production sends **871** of
these across the burst. **None are render-required**; they enable interaction.
For a minimally interactive HUD, set events on the inventory (1473) and the core
action components only; stage the rest. (Exact event-bit semantics are in
`948-research-B` IF_SETEVENTS; not re-derived here.)

### 7.5 Default varps for a fresh account (the HUD-safe minimum)

The world-entry HUD scripts (8862 / 16300 / 671) read varps/varbits to populate
skills, orbs, settings, and tab state. With an empty baseline they read defaults;
a **missing-but-required** var can drive a CS2 error → the clean-quit failure
(`world-bootstrap-948.md` §0). The **safe, generate-from-state** baseline is the
production approach: dump the account's saved varp map (cache-valid ids only,
§2.4 — an undefined id SIGSEGVs).

`[prod]` baseline composition (between `op5` and the first `op22`):

- **op28 SET_VARP_INT** (`value BE i32` + `id BE u16`): **491** packets, 447
  unique ids, range **3..12863**. Lowest ids: `3, 20, 25, 26, 37, 38, 39, 40, 41,
  42, 43, 46, 47, 48, 49, 51..55, …`.
- **op61 SET_VARBIT_NEG** (`id BE u16` + `value` byte, decode `(-0x80 - raw) &
  0xFF`): **1176** packets, 1170 unique ids, range **27..12704**. Lowest ids:
  `27, 45, 69, 82, 85, 89, 97, 98, 99, 100, 111, 120, 135, 145..149, 161,
  186..188, 249, 250, 261, 262, 299, 300, 304, 305, …`. Examples (id→value):
  `27→-1, 45→2, 69→1, 82→17, 111→50`.
- **op147 SET_VARP_LONG**: **5** packets.

> **Fresh-account default-var guidance.** There is **no fixed "magic N" var set**
> the client hard-requires — the scene + op75 render *without* any varps
> (`ProcessPlayerInfo` / `SET_READY_FLAG` consult no varp predicate, §2.4). The
> minimal-risk path for a fresh account is to send the **type defaults** for the
> low-id varps the HUD scripts touch — i.e. for each varp the account has no saved
> value for, send its `VarpType` default (often `-1` for "unset", `0`, or a
> type-specified default) rather than omitting it, **but only for ids that exist
> in the loaded cache**. The cleanest implementation: iterate the player's saved
> varp map (empty for a fresh account) AND, for the low-id HUD-critical varps,
> fall back to the `VarpType.defaultValue` from the cache. **Do not** hardcode a
> list — derive it from the cache's `VarpType` table so it can never contain a
> stale/undefined id.
>
> **The definitive "which exact varps does script 8862 read" answer is CACHE-side,
> not binary-side.** Script 8862/16300/671 are CS2 bytecode stored in the cache
> (the client binary contains only the CS2 *interpreter*, not the scripts). To
> enumerate the precise varp/varbit reads, `cache-library-engineer` must decode
> those CS2 scripts (interface index, the `cs2` archive) and list their
> `get_varp`/`get_varbit` opcodes. Until then, the account-var-dump (or
> type-default fallback) is the safe baseline. This boundary is unchanged from §6.
```
