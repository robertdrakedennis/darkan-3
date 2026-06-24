# RS3 NXT 948-5 — World-Entry ROADMAP (the authoritative multi-session build plan)

> **Purpose.** This is the ordered, op-by-op roadmap for building darkan3's **real**
> world-entry sequence — as *systems*, not a byte replay — so the NXT 948-5 client
> builds the scene, renders, and becomes interactive. A minimal burst will NOT
> render: the client needs the full world state (build area + varp baseline + GPI
> + zone content + HUD + the render gate) to leave the "Loading" sub-state and
> start ticking. Each phase below is sized so a single session can pick it up.
>
> **Owner:** `networking-protocol-engineer` (burst/ordering/codecs), with
> `cache-library-engineer` (varp/varc defaults from cache; index-5 map serving),
> `js5-server-engineer` (map-group serving), and `ghidra-reverse-engineer` for the
> RE-blocked items flagged below.
>
> **Source of truth.** The 0-desync golden production capture
> `build/undercut-socket-session-production-isaac.jsonl` (filter `conn=="world"`):
> **3796 world S2C** packets + **82 world C2S**. Every count and ordinal in §1–§3 is
> measured directly from that file. The byte/bit/opcode semantics are taken from the
> existing 948-5 docs, NOT re-derived here:
> - `docs/protocol/world-bootstrap-948.md` — op81 layout, GPI coherence, varp baseline.
> - `docs/protocol/world-entry-render-948.md` — the op75 render gate, HUD-as-system.
> - `docs/protocol/packed-coord-buildarea-948.md` — packedA/B = two **region** corners (DEFINITIVE).
> - `docs/protocol/alerion-buildarea-reference-910.md` — service *shape* only (910 bytes are wrong).
> - `docs/net/serverprot/948-research-A/B/C-*.md` — varp/varc, interface, player/npc-info wire.
>
> **One-line thesis.** *Coherent op81 build area (already fixed) + a varp baseline +
> a single coherent GPI + the HUD + `op75 SET_READY_FLAG` last = a rendered,
> interactive world.* The render gate is **server-driven (op75)**; the client emits
> no `MAP_BUILD_COMPLETE` opcode — its "scene built" signals are `op5
> SceneGraphReport` + `op52` (C2S), which are observational, not a gate (§3).

---

## 0. TL;DR — the build order at a glance

| # | Phase / system | Packets (S2C) | Client-validatable checkpoint | RE-blocked? |
|---|----------------|---------------|-------------------------------|-------------|
| **P0** | World-login response (pre-ISAAC A/B/C) | S2C ord 0–5 (`opNone`) | Client reaches `main_state 30 LOGGED_IN`, installs ISAAC | done |
| **P1** | op81 scene build + build area | ord 6 | Client allocates a non-inverted grid, requests **index-5** map groups over JS5 for the spawn mapsquares | **build-area semantic being re-RE'd** |
| **P2** | Initial burst (token/minimap/player-ops) | ord 7–18 | No-op visually; client accepts the UI-op preamble | done |
| **P3** | Varp/Varc baseline (real Varp system) | op5 (ord 19) → ord ~1622 | HUD scripts read non-default vars; no CS2 assert / clean-quit | blocked on Account var persistence + cache `VarpType` defaults |
| **P4** | First GPI (player info + appearance) | op55/op1 → op22 (ord 1625) | Local avatar placed at spawn tile; ext-info APPEARANCE decoded | **appearance ext-info framing** |
| **P5** | Zone/scene streaming (locs, ground items) | ord 1628–2290 (op78/76/46/16/90) | Locs + ground items appear in the built scene | partially (loc/obj encoders) |
| **P6** | NPC info system | op209 (ord 3338) + op52 stream | NPCs spawn and animate in the scene | partially |
| **P7** | Interface/HUD setup (root 1477 system) | ord 2291–3356 (op3/82/35/110) | Gameframe 1477 renders with tabs; HUD interactive | done (staged in `GameHud.kt`) |
| **P8** | Render-onset bundle + `op75` | ord 3338–3455 | **"Loading" clears; scene renders; client begins ~600ms ticks** | done (staged in `sendFirstLightTail`) |
| **P9** | Steady-state ticks | ord 3456–3795 + C2S | 3–6 `op22`/`op52` per tick; client sends only `op51 Ping` | done (WorldTick loop) |

**Critical path to first render (minimum coherent set):** P0 → P1 (coherent build
area) → P3 (at least a safe/empty baseline) → P4 (single GPI) → **P8 (op75 last)**.
P5/P6/P7 populate content/HUD and can be staged *after* the bare scene renders, but
P7's HUD CS2 reads varps, so P3 must cover what those scripts touch before P7 is
enabled (else clean-quit — `world-bootstrap-948.md` §2.3, §7.5).

---

## 1. Full op-by-op breakdown (measured from the capture)

### 1.1 World **S2C** — 3796 packets, 59 distinct opcodes (incl. the 6 pre-ISAAC `opNone`)

Counts (descending). `op<N>` is the darkan codec name; the client's own name differs
for a few (noted) but the **wire is identical**.

| count | op | darkan name | role / phase |
|------:|----|-------------|--------------|
| 1176 | 61 | VarpSmall (client: SET_VARBIT_NEG) | varp baseline (P3) |
| 871 | 35 | IfSetEvents | HUD interaction masks (P7) |
| 616 | 78 | UpdateZoneFullFollowsV2 | zone content (P5) |
| 491 | 28 | VarpLarge (client: SET_VARP_INT) | varp baseline (P3) |
| 86 | 110 | RunClientScript | HUD/CS2 build (P7) |
| 64 | 76 | UpdateZonePartialEnclosed | zone content (P5) |
| 61 | 47 | ClientSetVarcSmall | varc baseline / HUD (P3/P7) |
| 56 | 82 | IfSetPosition | HUD child mount into 1477 (P7) |
| 54 | 22 | PlayerInfo | GPI init + per-tick (P4/P9) |
| 54 | 162 | TRIGGER_ONDIALOGABORT | per-tick dialog reset (P9) |
| 53 | 52 | NpcInfo | NPC info per-tick (P6/P9) |
| 35 | 92 | ClientSetVarcStr | varc strings (P3/P7) |
| 29 | 44 | UpdateStat | skills (render-onset, P8) |
| 24 | 16 | LocDel | zone content (P5) |
| 21 | 46 | ObjAdd | ground items (P5) |
| 11 | 91 | IfSetHide | HUD (P7) |
| 8 | 85 | UPDATE_INV_FULL | inventory (P5/P8) |
| 8 | 119 | CutsceneData | render-onset bundle (P8) |
| 8 | 104 | PlayerInfoDecode | render-onset bundle (P8) |
| 7 | 122 | IfSetText | HUD text (P7) |
| 6 | — | `opNone` (pre-ISAAC login response A/B/C) | P0 |
| 6 | 17 | SetPlayerOp | initial burst player right-click ops (P2) |
| 5 | 147 | VarpLong | varp baseline (P3) |
| 5 | 174 | AntiCheatChallenge | steady-state anti-cheat (P9) |
| 2 | 172 | MinimapFlagA | initial burst + render-onset (P2/P8) |
| 2 | 204 | MinimapFlagB | initial burst + render-onset (P2/P8) |
| 2 | 90 | LocAdd | zone content (P5) |
| 2 | 62 | IfCloseSub | HUD (P7) |
| 2 | 30 | IfSet2DAngle | HUD model angle (P7) |
| 2 | 64 | ClientSetVarcLarge | varc baseline (P3) |
| 1 | 81 | RebuildNormalSimple | scene build (P1) |
| 1 | 54 | HashedWorldToken | session token (P2) |
| 1 | 73 | MinimapState | minimap mode (P2) |
| 1 | 74 | JcoinsUpdate (client: SET_DISPLAY_INT) | display int (P2) |
| 1 | 95 | MidiSong | music (P2) |
| 1 | 5 | ResetClientVarcache (client: RESET_ALL_VARPS) | varp reset, opens P3 |
| 1 | 55 | DestroyZoneData | zone reset before GPI (P4) |
| 1 | 1 | SetNpcOp | npc right-click op (P4) |
| 1 | 77 | CAMERA_UPDATE | camera (P4) |
| 1 | 130 | UpdateIgnoreListRaw | social (P4) |
| 1 | 3 | IfSetTopLevelInterface | open root 1477 (P7) |
| 1 | 120 | CAM_SMOOTHRESET | camera (P7) |
| 1 | 157 | SceneFlag | scene flag (P5/P7) |
| 1 | 209 | NpcInfoThunk | NPC-info init (P8) |
| 1 | 190 | ClearPendingUpdates | render-onset (P8) |
| 1 | 93 | GameMessage | welcome message (P8) |
| 1 | 154 | EntityAnimAtTile | render-onset anim (P8) |
| 1 | 121 | UPDATE_INV_PARTIAL | inventory (P8) |
| 1 | 12 | SetPlayerOp2 | render-onset (P8) |
| 1 | 13 | SetPlayerOp3 | render-onset (P8) |
| 1 | 7 | ResetEntityLists | render-onset (P8) |
| 1 | 45 | SetMultiwayState | render-onset (P8) |
| 1 | 67 | ClanChannelFull | clan (P8) |
| 1 | 199 | RebuildRegion | render-onset region rebuild (P8) |
| 1 | 80 | UpdateRunenergy | run energy (P8) |
| 1 | **75** | **SetReadyFlag** | **THE RENDER GATE (P8)** |
| 1 | 49 | ChangeLobby | lobby-context flag (P9) |
| 1 | 216 | WorldListPacket | world list (P9) |
| 1 | 26 | FriendStatus | social (P9) |

### 1.2 World **C2S** — 82 packets, 16 distinct opcodes (true client send order by `seq`)

| count | op | name | meaning (note: many are inbound game packets, names provisional) |
|------:|----|------|------|
| 31 | 51 | Ping | **keepalive** — the ONLY packet sent in steady state (first at seq 36) |
| 18 | 5 | SceneGraphReport | client "scene graph state" report (4B); first at **seq 5** |
| 10 | 52 | UNKNOWN_52 | 6B status report `02 0d80 0838 01` (packed-coord-shaped); first at **seq 6** |
| 5 | 3 | AntiCheatChallengeResponse | reply to S2C op174 AntiCheatChallenge (9B) |
| 4 | 106 | UNKNOWN_106 | 1B toggle (`00`/`01`) |
| 3 | 12 | UNKNOWN_12 | 58B detail/preferences-shaped block |
| 2 | 98 | UNKNOWN_98 | 247B then 91B — focus/window/interface state |
| 1 | 105 | EVENT_APPLET_FOCUS | 1321B applet-focus / client-var echo (seq 3) |
| 1 | 240 | UNKNOWN_240 | 7B — **the very first C2S** (seq 0) |
| 1 | 94 | WINDOW_STATUS | 3B `00 0100` window resize/state (seq 10) |
| 1 | 54 | RequestWorldList | request world list (seq 12) → S2C op216 |
| 1 | 8 | UNKNOWN_8 | 4B (seq 18) |
| 1 | 76 | UNKNOWN_76 | 4B `00 0002ee` (seq 33) |
| 1 | 127 | IfButton | interface button click (seq 41) |
| 2 | — | `opNone` / `op16` (unnamed, no `seq`) | partial/edge frames, ignore |

**No `MAP_BUILD_COMPLETE` opcode exists in the C2S stream.** See §3.

---

## 2. Phase segmentation of the S2C stream (with packet ranges + dependencies)

S2C ordinals below are 0-based positions in the world S2C stream (`conn=="world",
dir=="S2C"`). Boundaries are exact (measured first/last-appearance per opcode).

### P0 — World-login RESPONSE (pre-ISAAC) — **ord 0–5**
Six `opNone` rows = the pre-ISAAC Part A/B/C blob (server-client-var block + players
byte + `WorldLoginDetails`), NOT game-stream opcodes. Installs ISAAC; client →
`main_state 30 LOGGED_IN` **before op81**. *Already implemented* —
`WorldServer.writeWorldLoginResponse` (Parts A/B/C, `lobby-world-switch-948.md` §9).
**Dependency:** everything downstream rides the ISAAC game stream this establishes.

### P1 — op81 scene build + build area — **ord 6**
Single `op81 RebuildNormalSimple` (prod 5137B = 5119B GPI prefix + 18B coord header;
darkan ships **Shape A: 18-byte header only**). Carries the build-area corners
(packedA/B = two **region** corners, `>>6`), the centre zone, world id, camera.
**This builds the BuildArea grid the client JS5-pulls index-5 map groups into.**
**Dependency:** must be first game packet; its bounds must be non-inverted and
contain the spawn region or the client requests zero map groups (black screen).

### P2 — Initial burst (UI-op preamble) — **ord 7–18**
`op54 HashedWorldToken`, `op73 MinimapState`, `op74 JcoinsUpdate`, `op172/204`
minimap flags, `op17 SetPlayerOp ×6`, `op95 MidiSong`. Cosmetic/session setup; none
render-gated. *Already implemented* in `sendWorldLoginCore`.

### P3 — Varp/Varc baseline — **op5 (ord 19) → ~ord 1622**
`op5 ResetClientVarcache` (ord 19) opens it, then **1603 baseline packets**: op28
VarpLarge (491), op61 VarpSmall (1176 across the whole stream; the bulk here),
op147 VarpLong (5, ord 1507–1536), plus op47/92/64 varc. This is the account's saved
var table — quest progress, settings, unlocks. **Dependency:** must follow op5;
must precede the HUD CS2 (P7) that reads these vars; **only cache-valid ids**
(unknown id → SIGSEGV, `world-bootstrap-948.md` §2.4).

### P4 — First GPI (player info + appearance) — **op55/op1 → op22 (ord 1625)**
`op55 DestroyZoneData` (ord 1623), `op1 SetNpcOp` (ord 1624), then the first
`op22 PlayerInfo` (ord 1625) — the GPI placing the local avatar at the spawn tile —
`op77 CAMERA_UPDATE` (1626), `op130 UpdateIgnoreListRaw` (1627). Shape A sends the
GPI as a standalone op22 init (local 30-bit tile = spawn). **Dependency:** the GPI
local tile must equal the op81 centre zone (coherence). Appearance travels as
ext-info on this op22.

### P5 — Zone/scene content streaming — **ord 1628–2290**
`op78 UpdateZoneFullFollowsV2 ×616` (1628–2290), `op46 ObjAdd ×21` (ground items,
1636–2074), `op16 LocDel ×24` (1721–1757), `op90 LocAdd ×2` (1759–1760). Streams the
locs/objects/ground items for every zone in the build area. **Dependency:** the
covered-region set from P1 gates what may stream.

### P6 — NPC info — **op209 (ord 3338) + op52 stream**
`op209 NpcInfoThunk` (ord 3338) initialises NPC info; `op52 NpcInfo` then streams
per tick (first real op52 at ord 3354, 53 total). **Dependency:** rides the same
scene; interleaves with P8/P9.

### P7 — Interface/HUD setup (root 1477) — **ord 2291–3356**
`op3 IfSetTopLevelInterface` (ord 2301, opens root **1477**), `op82 IfSetPosition
×56` (mount children into 1477 slots), `op110 RunClientScript ×86` (HUD CS2 16300,
671, 8862×22), `op35 IfSetEvents ×871` (interaction masks), plus op85/121 inv,
op91/122/30/62 HUD ops. **Dependency:** op3 (root 1477) before any op82/op35; the
CS2 read varps (P3 must precede). Not render-gated — the scene renders behind it.

### P8 — Render-onset bundle + the render gate — **ord 3338–3455**
The tight pre-render bundle (measured): `op209 NpcInfoThunk` (3338) →
`op190 ClearPendingUpdates` (3340) → `op93 GameMessage` welcome (3348) →
`op44 UpdateStat ×29` skills (3399–3427) → `op12/13 SetPlayerOp2/3` (3428–3429) →
`op7 ResetEntityLists` (3430) → `op45 SetMultiwayState` (3431) →
`op67 ClanChannelFull` (3432) → `op119 CutsceneData ×8` (3433–3440) →
`op104 PlayerInfoDecode ×8` (3441–3448) → `op199 RebuildRegion` (3449) →
`op80 UpdateRunenergy` (3454) → **`op75 SetReadyFlag` (ord 3455)**. op75 sets the
client render-ready flag → "Loading" clears → scene renders. **op75 is the single
load-bearing addition for first render** (`world-entry-render-948.md` §0/P0).
**Dependency:** op75 must come AFTER a built scene (P1) and a GPI (P4); production
sends it dead last of the "logic" packets.

### P9 — Steady-state ticks — **ord 3456–3795 (+ all C2S)**
Immediately after op75: `op22`/`op52` pairs per tick (3456–3457, 3472–3473,
3477–3478…), `op162 TRIGGER_ONDIALOGABORT` per tick, `op49 ChangeLobby` (3469),
`op216 WorldListPacket` (3470), `op26 FriendStatus` (3475), `op174 AntiCheatChallenge`
(steady), zone deltas. Client now ticks at ~600ms and sends only `op51 Ping`.
*Already implemented* — `WorldTick` loop.

**Canonical S2C order (the contract):**
`P0 login-resp → P1 op81 → P2 UI-ops → P3 op5+varp baseline → P4 op55/op1/op22 →
P5 zone content → P6/P7 NPC-info/HUD → P8 render-onset+op75 (LAST) → P9 ticks`.

---

## 3. C2S timeline — what the client sends and WHEN

**Important capture caveat.** In this deframed file every C2S row is logged *after*
all 3796 S2C rows (a recording/deframe artifact — the S2C side was drained first).
So **C2S position relative to S2C ordinals in THIS file is not wall-clock truth.**
The authoritative timing is the client's own cycle hook (`events.jsonl` in the
recording, cited across the 948 docs): op75 fires at client cycle 14976, and the
keepalive ping stream starts immediately after. What IS reliable here is the C2S
**`seq` order** (the client's true send order) and the **content**.

**True client send order (by `seq`):**

```
seq 0   op240  UNKNOWN_240 (7B)          ← the very first C2S (client handshake/echo)
seq 1   op98   UNKNOWN_98 (247B)         ← focus/window/interface state
seq 2   op106  UNKNOWN_106 (1B)
seq 3   op105  EVENT_APPLET_FOCUS (1321B)← applet focus / client-var echo
seq 4   op98   UNKNOWN_98 (91B)
seq 5   op5    SceneGraphReport (4B)     ← FIRST "scene graph state" report
seq 6-9 op52   UNKNOWN_52 (6B ×4)        ← scene-status report `02 0d80 0838 01`
seq 10  op94   WINDOW_STATUS (3B)
seq 11  op12   UNKNOWN_12 (58B)          ← detail/preferences block
seq 12  op54   RequestWorldList (4B)     ← triggers S2C op216 WorldListPacket
seq 13+ op5/op52 interleaved             ← continued scene reports as the scene builds
seq 18  op8    UNKNOWN_8 (4B)
seq 33  op76   UNKNOWN_76 (4B)
seq 36  op51   Ping (0B)                 ← STEADY-STATE KEEPALIVE begins here
seq 41  op127  IfButton (8B)             ← a UI click
seq 46+ op3    AntiCheatChallengeResponse← replies to S2C op174
seq 36..81 op51 Ping ×31                 ← ~1/s keepalive, the success signal
```

**The render gate is server-driven (op75); there is NO client `MAP_BUILD_COMPLETE`
ack to wait on.** Unlike the 910 alerion model (where the server gates PLAYER_INFO
on a `MAP_BUILD_COMPLETE` C2S), 948-5's render gate is the **outbound** op75. The
client's "scene built" signals are observational:
- **`op5 SceneGraphReport`** (first at seq 5) — the client reporting its scene-graph
  state; the 948 analogue of "I built the scene." darkan has a
  `SceneGraphReportHandler` (lobby) already.
- **`op52` (6B, `02 0d80 0838 01`)** — a per-build status report (first at seq 6),
  packed-coord-shaped; the closest thing to a build-complete signal. darkan has a
  `MapBuildCompleteHandler` (world) staged for this family.

**What gates server-side progress:** nothing the *client* must send. The server
streams the full burst and emits op75 last on its own schedule. The only C2S the
server should *react* to during entry: `op54 RequestWorldList` → send `op216`;
`op174`→`op3` anti-cheat challenge/response; and treat `op51 Ping` as the liveness
heartbeat. **For first-render bring-up, none of the C2S packets block the server**
— if op75 + a coherent scene are sent, the client renders and starts pinging. Use
the `op5 SceneGraphReport` / `op52` arrival as the *observable confirmation* that
the scene built (log them; do not gate on them for the first pilot).

> **RE follow-up (not blocking first render):** decode `op240` (seq 0),
> `op98`/`op105` (focus/window state), `op12` (preferences/detail — likely carries
> the client's `BuildAreaSize` choice), and confirm `op52`'s 6B body semantics.
> These matter for build-area-size negotiation (P1 sizing) and a fully faithful
> handshake, not for getting the first scene on screen.

---

## 4. System mapping — phase → darkan3 system, with HAVE vs MISSING

Each phase maps to a real system. "HAVE" = exists in the tree today; "STAGED" =
written but intentionally un-wired; "MISSING" = to build.

| Phase | System to build | darkan3 status | Files |
|-------|-----------------|----------------|-------|
| **P1** | **Build-area service** (region-corner encoder + covered-region set + size value object) | **HAVE (new)** — `BuildArea.of(spawn, size)` produces the corner-packed words; `BuildAreaSize` half-region windows; `regions` set; wired into `sendWorldLoginCore` via `viewport.loadBuildArea`. Replaces the broken `packZoneCoord`. | `world/.../world/BuildArea.kt` (+`BuildAreaTest`), `world/.../world/Viewport.kt`, `core/.../RebuildNormalSimplePayloads.kt` |
| **P3** | **Varp/Varbit system** (per-player value map, dirty-flush, op61/28/147 selection, cache-default seeding) | **STAGED** — `VarpManager` set/flush/encode + `VarpDefaults` seam exist and are unit-tested, but `WorldServer.sendVarpBaseline` is an intentional **no-op** (empty baseline). Account has no persisted var map; the cache `VarpType` defaults provider is not implemented. | `world/.../entity/VarpManager.kt` (+test); hook in `WorldServer.sendVarpBaseline` |
| **P4** | **Player-info + appearance** (GPI init bit block + per-tick + ext-info APPEARANCE) | **HAVE per-tick; STAGED init** — `PlayerInfoBuilder.build()` per-tick is correct; `buildInit()` is wired into Step 15. Appearance ext-info framing is the open RE item. | `world/.../net/PlayerInfoBuilder.kt` (+`PlayerInfoBuilderInitTest`), rev948 player codecs |
| **P5** | **Zone/scene streaming** (build-area-windowed per-zone loc/obj/ground bundles) | **PARTIAL** — `ZoneBundleBuilder` windows the viewport and emits per-chunk offsets; loc/obj/ground-item *content* generation from world state is missing. | `world/.../net/ZoneBundleBuilder.kt`, `world/.../world/Viewport.kt` |
| **P6** | **NPC info** (spawn/move/update masks + thunk init) | **PARTIAL** — `NpcInfoBuilder` exists; spawn/region-init from world NPC state needs filling. | `world/.../net/NpcInfoBuilder.kt`, rev948 npc codecs |
| **P7** | **HUD / interface system** (root 1477 component map + script/event driver) | **STAGED** — `GameHud.open()` builds op3→op82×56→op110(16300/671/8862×22)→op35 as a real component map; intentionally **un-called** until the bare scene renders. | `world/.../net/GameHud.kt` (+`GameHudTest`) |
| **P8** | **Render-onset + render gate** | **STAGED** — `WorldServer.sendFirstLightTail()` reproduces the prod bundle (stats + `NpcInfoThunk` + `PlayerInfoDecode×8` + `CutsceneData×8` + `SetReadyFlag`) but is **defined and never called**; op75 send is deliberately deferred (Step 15b comment). Codec `ServerProt.SetReadyFlag` (op75, size 0) exists. | `world/.../server/WorldServer.kt` (`sendFirstLightTail`, Step 15b), `core/.../Rev948ServerCodecsMisc.kt` |
| **P9** | **Tick loop + C2S handlers** | **HAVE** — `WorldTick` loop; `PingHandler`, `SceneGraphReportHandler`, `MapBuildCompleteHandler`, `IfButtonHandler`, anti-cheat, worldlist handlers exist. | `world/.../server/WorldTick.kt`, `world/.../server/packet/*`, `lobby/.../server/packet/*` |
| C2S | **Scene-built observation** (op5/op52) | **STAGED** — `SceneGraphReportHandler` + `MapBuildCompleteHandler` parse the client's scene reports for logging/observation (not gating). | `lobby/.../packet/SceneGraphReportHandler.kt`, `world/.../packet/MapBuildCompleteHandler.kt` |

**Net assessment:** the scaffolding for the *whole* sequence already exists; the gap
is **wiring + content generation + two RE unblocks**, not greenfield. Specifically:
P1 is done; P8 (op75) is one wiring decision away; P3/P5/P6 need real content from
world/account state; P4 and P1-sizing have RE blockers.

---

## 5. Phased implementation plan (ordered; each phase = one session, with a checkpoint)

> Build in this order. Each phase has a **concrete client-validatable checkpoint** —
> what the real NXT client must do when the phase is correct. Capture a fresh client
> run (or watch the JS5/socket + the client's own logs) to confirm before advancing.
> Phases flagged **[RE]** are blocked on a reverse-engineering deliverable; do them
> behind the unblock or with the stated stopgap.

### Phase A — Coherent op81 build area (P1) — *foundation; mostly done, VERIFY*
**Build/verify:** `BuildArea.of(spawn)` corner encoder is wired; confirm the emitted
`packedCoordA/B` decode (per `packed-coord-buildarea-948.md` §5) to a **non-inverted**
region rectangle **containing the spawn region**, and that the centre zone =
`spawn.zone`. Default Lumbridge spawn (3200,3200 → region 50,50; note prod spawned at
3235,3234 → region 50,50 too — same region).
**Checkpoint:** with a coherent op81 (and the JS5 map server up), the client
**requests index-5 map groups** for the spawn mapsquares (~49–51) over JS5 — i.e. the
JS5 server logs `m{X}_{Z}` / `l{X}_{Z}` group fetches for the build area. (Today,
pre-fix, it requested **zero**.) If still zero → the bounds are inverted/empty; re-check.
**[RE] BLOCKER (active):** the *build-area semantic* is being re-RE'd. `BuildArea.kt`
encodes the §5 corner model (region corners, `>>6`), which supersedes
`world-bootstrap-948.md` §1.2's origin/span framing. Confirm the re-RE lands on the
two-corners model before trusting the window-sizing; `packedA = corner vs centre` and
the exact `BuildAreaSize` table remain open (`packed-coord-buildarea-948.md` §8). A
small symmetric window (MEDIUM, 5×5 regions) is the safe stopgap.

### Phase B — The render gate alone (P8: op75) — *the single highest-impact step*
**Build:** after a coherent op81 (Phase A) + the existing standalone first GPI
(`buildInit`), send **`op75 SetReadyFlag` LAST**. Reuse `sendFirstLightTail()` (it
already bundles the render-onset packets + op75) or, for the very first pilot, send
just `SetReadyFlag()` after the GPI. Today op75 is **deferred** (WorldServer Step 15b)
and `sendFirstLightTail` is never called — wiring it is the change.
**Checkpoint:** the client **leaves the "Loading - please wait." sub-state**, renders
the scene built from the JS5 map groups, and **begins ~600ms ticks** (the client's
cycle counter advances in real time; it emits `op5 SceneGraphReport` + `op52`, then
`op51 Ping` ~1/s). Black/permanent-Loading after this → the scene built wrong
(Phase A) or the map groups are missing (Phase A2 below), not the gate.
> Order matters: op75 must follow a built scene + a GPI. Sent too early it flips
> render-ready over an empty scene → black ready world (`world-entry-render-948.md`
> §3). For the first pilot, ship **zero HUD packets and zero ext-info** (defer P7/P4
> appearance) so failures are unambiguous.

### Phase A2 — JS5 index-5 map serving for the build area — *parallel to B; cache side*
**Build (cache/js5 owners):** ensure `JS5Server` + `FileProvider` serve **index 5**
map groups (`m{X}_{Z}` terrain + `l{X}_{Z}` locs) for the Lumbridge mapsquares on
demand, with correct compression. Lumbridge is F2P → **XTEA key = 0**, so no map-keys
packet is needed (`world-entry-render-948.md` §4).
**Checkpoint:** after Phase B, the post-op75 symptom is a **fully built** scene (not a
black/partial one). If partial → a group is missing or compressed wrong. Owners:
`js5-server-engineer` / `cache-library-engineer`.

### Phase C — Varp/Varbit baseline as a real system (P3)
**Build:** persist a saved-var map on `Account`; in `sendVarpBaseline`, iterate it and
emit op61/op28/op147 by magnitude (the `VarpManager.encode` selection is correct).
Add the cache-backed `VarpDefaults` provider (enumerate `VarpType` from the interface
config index) so a **fresh** account gets type defaults for the HUD-critical low-id
varps. **CRITICAL:** emit only ids the cache defines — an unknown id NULL-derefs
`GetVarType` → SIGSEGV (`world-bootstrap-948.md` §2.4). Send between op5 and the first
op22.
**Checkpoint:** with the HUD enabled (Phase E), the world-entry CS2 (8862/16300/671)
populate skills/orbs/settings from real values and the client **does not clean-quit**
(no `NSApplication terminate`). With an empty baseline + no HUD, the bare scene still
renders (no varp gate) — the baseline only matters once the HUD CS2 run.
**BLOCKER:** Account var persistence (account owner) + cache `VarpType` defaults
(`cache-library-engineer`). Until both land, keep the baseline empty AND keep the HUD
CS2 off (Phase E gated on this).

### Phase D — First GPI + appearance ext-info (P4)
**Build:** confirm `PlayerInfoBuilder.buildInit` emits the GPI init bit block (local
30-bit spawn tile + empty other-slots) and append the local player's **APPEARANCE**
ext-info (mask bit 3 / 0x8, expansion bits {0,13,22}) with its 2-byte length prefix.
Set `WorldLoginDetails.playerIndex` = the GPI slot.
**Checkpoint:** the local avatar renders **at the spawn tile** with a correct
appearance (not a default/blank model), and stays put (no teleport-jitter from an
incoherent tile). A wrong tile → avatar outside its scene; a malformed ext-info →
client may drop the packet or mis-render.
**[RE] BLOCKER:** the **appearance ext-info framing** for 948 (the `gScrambled*`
per-field mode selector + the exact APPEARANCE block layout) is the open item —
`948-research-C` §3–4 documents the mask/selector; validate the full block byte-for-
byte before enabling appearance. **Stopgap:** ship the GPI init with **no ext-info**
(empty appearance) for the bare-scene pilot — the avatar renders as a default model
but the scene/render path is exercised.

### Phase E — HUD as the real 1477 system (P7)
**Build:** call `GameHud.open(session)` after the scene renders — it emits op3 (root
1477) → op82×56 (children into 1477 slots) → op110 (16300, 671, then 8862×22 per the
exact `[arg1, tab]` lists) → op35 events. It is **un-called** today by design.
**Gate:** only enable after Phase C provides the varps the CS2 read (else clean-quit).
**Checkpoint:** the gameframe **1477** renders with its tabs (inventory/skills/orbs/
settings), and the HUD is **interactive** (op35 masks let clicks register; the client
sends `op127 IfButton` on a tab click, as prod does at seq 41).
**Open item (non-blocking):** the §7.2 component map is 54 rows in the table vs "56" in
prose — reconcile with RE (`GameHud.kt` flags it); start with the minimal inventory/
skills/orbs subset and expand.

### Phase F — Zone/scene content streaming (P5)
**Build:** generate per-zone loc/obj/ground-item bundles from world state through
`ZoneBundleBuilder`, windowed by the P1 covered-region set: op78 (full zone), op76
(partial), op46 (ObjAdd ground items), op16/op90 (LocDel/LocAdd).
**Checkpoint:** locs (trees, doors, fences) and ground items appear in the rendered
scene at the right tiles; walking shows the world populated, not an empty terrain.

### Phase G — NPC info system (P6)
**Build:** fill `NpcInfoBuilder` spawn/region-init + per-tick movement/update masks;
emit op209 NpcInfoThunk at init then op52 NpcInfo per tick.
**Checkpoint:** NPCs (e.g. Lumbridge cows, the Duke) spawn, idle-animate, and move in
the scene; the client renders and tracks them across ticks.

### Phase H — Steady-state hardening (P9)
**Build/verify:** the WorldTick loop drives per-tick op22/op52, op162 dialog reset,
op174 anti-cheat (with the op3 response handler), op216 worldlist on op54, op26
friend status. Confirm the client holds a stable session emitting only `op51 Ping`.
**Checkpoint:** the client stays in-world **indefinitely** (minutes), ticking at
~600ms, sending only keepalive pings — the definitive "fully entered and stable"
signal.

---

## 6. Dependency graph (what must precede what)

```
A (op81 build area) ──┬──► B (op75 render gate) ──► [SCENE RENDERS]
  └ A2 (JS5 map serve)┘                                   │
                                                          ▼
                        D (GPI+appearance) ──────► avatar renders correctly
                        C (varp baseline) ──┐
                                            ▼
                        E (HUD 1477) ◄──────┘  (E requires C: CS2 read varps)
                                            │
                        F (zone content) ───┤  (populate scene)
                        G (NPC info) ───────┤
                                            ▼
                        H (steady-state) ──► stable, interactive world
```

**Hard ordering rules (from the docs, restated):**
- op81 (A) is the first game packet; op75 (B) is the last logic packet.
- op5 precedes the varp baseline (C); the baseline precedes the HUD CS2 (E).
- The GPI local tile (D) == the op81 centre zone (A) == the spawn tile (coherence).
- op3 root 1477 (E) precedes any op82/op35.
- op75 (B) must follow a built scene (A) and a GPI (D).
- Only cache-valid var ids (C) — unknown id → SIGSEGV.

---

## 7. RE-blocked items (flag to ghidra-reverse-engineer before/with the phase)

1. **Build-area semantic (Phase A)** — *being re-RE'd.* Confirm the two-region-corners
   model (`packed-coord-buildarea-948.md` §5) over the old origin/span framing; settle
   `packedA = corner vs centre` and the real `BuildAreaSize` id→size table
   (`packed-coord-buildarea-948.md` §8). Stopgap: symmetric MEDIUM window.
2. **Appearance ext-info framing (Phase D)** — the exact 948 APPEARANCE block +
   `gScrambled*` per-field mode selector (`948-research-C` §3–4). Stopgap: GPI init
   with empty ext-info.
3. **op81 Shape-B GPI prefix (optional)** — the full 2047-slot bit layout, only if
   bundling the GPI in op81 instead of Shape A. Shape A (standalone op22) sidesteps it.
4. **C2S decode (Phase H polish, non-blocking)** — `op240`/`op98`/`op105`/`op12` (the
   client's focus/window/preferences/detail block, likely incl. its `BuildAreaSize`
   choice) and `op52`'s 6B status body. Needed for faithful build-area-size negotiation
   and a complete handshake, not for first render.
5. **HUD component map 54-vs-56 (Phase E, minor)** — reconcile the §7.2 table count.
6. **Which exact varps the HUD CS2 read (Phase C/E)** — CACHE-side: decode CS2 scripts
   8862/16300/671 for their `get_varp`/`get_varbit` opcodes (`cache-library-engineer`).
   Stopgap: dump the account's saved vars / type defaults.

---

## 8. First-render success criterion (the milestone this roadmap targets)

After **Phase A + A2 + B** (coherent op81 + JS5 map serving + op75 last), with the
GPI init shipped (Phase D stopgap: no ext-info) and **no HUD / empty varp baseline**:

> The NXT 948-5 client leaves "Loading - please wait.", renders the Lumbridge scene
> built from JS5 index-5 map groups, places the local avatar at the spawn tile, and
> begins ticking at ~600ms — sending `op5 SceneGraphReport` + `op52`, then a steady
> `op51 Ping` heartbeat, staying in-world indefinitely.

Everything after that (real appearance D, varp baseline C, HUD E, zone content F, NPCs
G) upgrades a bare rendered scene into a populated, interactive world — in that order.
