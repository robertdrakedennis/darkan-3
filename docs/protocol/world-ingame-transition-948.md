# RS3 NXT 948-5 — In-game vs lobby/worldlist transition (why Shape-B black-screens & falls back)

> **Status:** IN PROGRESS (live RE, headless read-only, 948-5). Written incrementally
> so findings survive. Authority: `analyzeHeadless -process rs2client.948-5` (project
> `~/projects/reclass-data/rs2client-948`), cross-checked against
> `build/undercut-socket-session-production-isaac.jsonl` (`conn=="world"`).
>
> **Builds on:** `world-bootstrap-948.md` (op81 GPI prefix RESOLVED, Shape B),
> `world-login-camera-render-948.md` (camera/streamer gate), `world-entry-plan.md`,
> `lobby-world-switch-948.md`.
>
> **Context (Shape-B pilot, from the brief):** op81 now ships the 5119-byte GPI
> prefix + 18-byte header; the client PARSES it and enters the world session for the
> first time (real C2S, `available=171`). But it black-screens, sends "lobby-style"
> C2S, and falls back to login. This doc finds why end-to-end.

---

## 0. TL;DR — the four answers

1. **THE TRANSITION (#1):** the client commits to main-state **30 (IN-GAME) correctly**
   on world login (mode==2 → `SetMainState(0x1e)`, §3). It black-screens & emits
   lobby-style C2S because darkan sends **none of the in-game interface block** —
   chiefly **`op3 IfSetTopLevelInterface`** (the HUD-root swap, production idx 2304),
   preceded by its `op110 RunClientScript` HUD onload scripts, and **`op75 SetReadyFlag`
   LAST**. Without `op3`, the client keeps the **lobby/worldlist top-level interface**,
   whose CS2 polls `op54 WORLDLIST_FETCH` (gated on state∈{20,30}, fires in-game too) —
   so op54-first is NOT a lobby-state bug, it's the wrong UI still being open. The client
   then "falls back to login" because **darkan's world server CLOSES the socket** when it
   reads a C2S opcode it can't frame (§4). The S2C that makes it commit: the HUD block
   (`op110…/op3 IfSetTopLevelInterface/op82/op35`) + `op75` last (§8).
2. **CAMERA/MAP (#2):** op81 does **NOT** enqueue map fetches (verified: BuildArea ctor
   has no JS5 call). The spawn map is loaded from **local cache during the lobby→world
   loading screen, BEFORE op81**, and its async `MapSquare::LoadFiles` callback sets the
   camera anchor `scene-root+0x240→+0xd8`. Only then does op81's `ProcessCameraReset`
   reach `+0x418=6`, arming the (camera-gated) map streamer. **darkan issues ZERO
   index-5 requests because it never triggers that pre-op81 spawn-map load** → anchor
   stays 0 → camera never positions → streamer's `+0x650 && triple!=-1` gate never opens.
   Both streamer call-sites (per-frame tick AND `LoginProtocolHandler @0x0024e8c0`) share
   the identical camera gate — there is **no camera-independent initial load** (§5).
3. **op156 (#3):** op54 is read at the **correct size (4)**; all darkan C2S sizes match
   the binary. op156 is **NOT a real ClientProt opcode** (Linux RegisterAll = op0-129
   only; no C2S op156, no op240). It is the symptom of the client running a **different
   C2S path** than production (op54-first vs production's op240-first) plus darkan's C2S
   table **lacking the macOS-only opcodes** the client emits. Reading 156 → no size meta →
   darkan `return`s → socket close. **Not a desync in the op54 read, not a size bug** —
   the macOS opcodes can't be named from the Linux binary; capture the macOS world C2S
   stream to resolve (§6).
4. **Double-GPI (#4):** op81's prefix fully inits the player list; a CORRECT per-tick
   op22 after it is harmless (production does it). But darkan's **replayed** op22 blob
   (wrong session) mis-walks the list op81 built → cursor desync / wrong-slot ext-info.
   **Omit the standalone op22 on tick 1** under Shape B (§7).

---

## 0b. HEADLINE FINDINGS (detail)

### F1 — `SendWorldlistFetch` (C2S op54) is NOT a lobby-only packet. The in-game client sends it. [VERIFIED]

`jag::ClientProt::SendWorldlistFetch @0x00194c00` (op54, wire size 4) is emitted by a
**CS2 client-script opcode handler** `FUN_00277d50` whose ONLY gate is:

```c
state = *(int *)(param_1 + RELA[0xd40]);     // the client main-state field
if (state != 0x1e && state != 0x14) { push 1; return; }   // 0x1e=30, 0x14=20
... // throttle by timestamp ...
jag::ClientProt::SendWorldlistFetch(...);     // sends op54
```

So op54 fires from a CS2 script **whenever the main state is 30 (in-game/LOGGED_IN)
OR 20 (lobby)**. Production's own in-game capture sends op54 at `isaac_index=13`
(mid-world-session) — proof the in-game client routinely sends worldlist fetches.
**Therefore "client sends RequestWorldList" is NOT evidence the client is stuck in
lobby UI.** This refutes the brief's framing of #1.

### F2 — Production's FIRST world C2S is op240 (size 7); op54 is normal mid-stream. [VERIFIED from capture]

From the golden capture (`conn=="world"`, C2S, by `isaac_index`):

| isaac_index | opcode | size | binary name | notes |
|---:|---:|---:|---|---|
| 1 | **240** | 7 | (NOT in 0-129 table) | first in-game C2S; payload `5e 02 0d80 0838 01` |
| 2 | 98 | 247 (varByte) | (unregistered) | |
| 3 | 106 | 1 | SendMultiDisplayPackets | `00` |
| 4 | 105 | 1321 (varShort) | SendAppletFocusEvents | |
| 5 | 98 | 91 | | |
| 6 | 5 | 4 | SendSceneGraphReport | `00000004` |
| 7-10 | 52 | 6 | SendDisplayInfo | `02 0d80 0838 01` (mode2 3456x2104 flag1) |
| 11 | 94 | 3 | SendWindowStatus | |
| 12 | 12 | 58 | SendMultiDisplayPackets | |
| **13** | **54** | **4** | **SendWorldlistFetch** | in-game worldlist fetch (NORMAL) |

The full ClientProt table (`RegisterAll @0x000e6170`) has **exactly opcodes 0-129**.
**op240 and op156 are NOT registered opcodes.** (Resolution of op240 / op156 in §2.)

---

## 1. op240 / op156 / op203 / op218 — the macOS-vs-Linux opcode divergence (#3 verdict) [VERIFIED]

### op240 is a REAL macOS-client opcode, absent from the Linux binary. NOT a desync.

- The Linux `rs2client.948-5` `ClientProt::RegisterAll @0x000e6170` registers **exactly
  opcodes 0-129** (130 entries). There is **no op240, op156, op203, or op218**.
- darkan's own `Rev948ClientProtStubs.kt` already documents this (lines 180-182):
  - `c(240,"UNKNOWN_240",7)` — *"LIVE: macOS 948-5 post-world-login wrapper; payload is
    0x5e + op52 display-info body... Linux 948-5 RegisterAll has no op240 entry."*
  - `c(218,"UNKNOWN_218",70)` — macOS lobby-handoff tail.
  - `c(203,"UNKNOWN_203",-2)` — macOS post-world sync packet.
- The deframer (`IsaacDeframer.decodeFrom`) decodes a C2S opcode as a **single byte**
  `(rawByte - isaac.next()) & 0xFF` (the 2-byte opcode rule is S2C-only, line 293) and
  accepts it if `< CLIENT_PROT_COUNT (256)` AND present in `codec.clientProtInfo`.
- **ISAAC is in sync at index 1**: production's op52 (idx 7), op54 (idx 13), op105,
  op98, etc. all decode cleanly. A desync at idx 1 would corrupt all later indices.
  Therefore production's op240 @ idx 1 is the **genuine** decrypted first opcode = 240.
  Its 7-byte payload `5e 02 0d80 0838 01` = `[0x5e][op52 SendDisplayInfo body]`
  (mode=2, w=3456, h=2104, flag=1). op240 is the macOS client's first-frame
  "post-world-login display wrapper".

### op156 on the C2S stream is NOT a real client packet (unframable → server bails). [VERIFIED]

- There is **no C2S op156** in the Linux binary (max 129) and **none in darkan's
  ClientProt codec** (darkan only has a *ServerProt* op156 = `SET_CHAT_FILTER_B`,
  `Rev948ServerProtStubs.kt:204`). The C2S `clientProtInfo` map has no key 156.
- So when darkan's live world server "reads a 2nd opcode decoding to op156", that 156
  is **misframed bytes**: after correctly reading op54 (4 bytes, the size IS right —
  `Rev948ClientCodecs.kt:68 clientProt<RequestWorldList>(opcode=54,size=4)`), the next
  opcode read lands on a byte that decrypts to 156, which is not a registered C2S
  opcode → the read is past the real packet boundary or the client genuinely sent an
  opcode the Linux table lacks (a macOS-only opcode like 240). With `available=171`
  the server then can't make progress and the session closes.
- **Verdict (#3):** op54 (RequestWorldList) is read with the CORRECT size (4). op156 is
  NOT a legitimate client opcode — it is the symptom of the client being on a
  DIFFERENT C2S path than production (it never sends op240 first; it sends op54 first),
  and/or darkan's Linux-derived C2S table missing the macOS-only opcodes the client
  emits. It is downstream of the real problem (§3), not a framing/ISAAC desync in the
  op54 read itself.

> **Implication for darkan's deframer/server:** the C2S `clientProtInfo` table must be
> extended with the macOS-only opcodes (240=7B, and whatever the client emits after) so
> the world server can frame the real macOS C2S stream. But that is a *parsing* fix; the
> *behavioural* problem (client sends op54-first and bails) is §3.

---

## 2. The in-game UI is built by an interface block darkan never sends [VERIFIED from capture + binary]

### 2.1 Production's full world S2C, by phase (from the golden capture)

| isaac_index | phase | packets |
|---:|---|---|
| 1 | scene build | op81 RebuildNormalSimple (5137) |
| 2-15 | world chrome | op54 HashedWorldToken, op73 MinimapState, op74 JcoinsUpdate, op172/204 MinimapFlags, op17 SetPlayerOp×6, op95 MidiSong |
| 16 | varp reset | op5 ResetClientVarcache |
| 17-1624 | **varp baseline** | op28 VarpLarge×491, op61 VarpSmall×1176, op147 VarpLong×5 (the saved account var dump) |
| 1625-1626 | zone/npc reset | op55 DestroyZoneData, op1 SetNpcOp |
| 1627-1630 | first per-tick sync | op22 PlayerInfo(677), op77 CAMERA_UPDATE(121), op130 UpdateIgnoreListRaw |
| ~1631-2300 | zone content | op78 UpdateZoneFullFollowsV2×616, op76×64, op46 ObjAdd×21, op16 LocDel×24, op85 UPDATE_INV_FULL×8, op44 UpdateStat×29 … |
| **2301-3466** | **IN-GAME HUD / interface construction** | **op110 RunClientScript×86, op3 IfSetTopLevelInterface(19)@2304, op82 IfSetPosition×56, op35 IfSetEvents×871, op122 IfSetText×7, op91 IfSetHide×11, op30 IfSet2DAngle×2, op62 IfCloseSub×2** |
| **3467** | **render onset** | **op75 SetReadyFlag(0) — LAST** |

**darkan's world-entry burst (per brief):** op81 → HashedWorldToken/Minimap/SetPlayerOp×6/MidiSong → op5 → op55 → op1 → op130 → op22 PlayerInfo(5B). **darkan stops here.** It sends:
- ZERO of the varp baseline (idx 17-1624)
- ZERO zone content (idx 1631-2300)
- **ZERO of the in-game HUD interface block (idx 2301-3466)** — no `op110 RunClientScript`, no **`op3 IfSetTopLevelInterface`**, no op82/op35
- NO `op75 SetReadyFlag`

### 2.2 The interface system (binary)

- `IF_SETTOPLEVELINTERFACE @0x00186a80` (S2C op3): sets `InterfaceManager+0xd8 = topLevelInterfaceId`,
  closes the previous top interface (`InterfaceManager::CloseInterface`), bumps the
  dirty counter `+0x10` / flag `+0x14=1`. **This is the packet that swaps the client's
  top-level interface from the lobby/worldlist UI to the in-game HUD root (1477-class).**
- `IF_OPENTOP @0x001941d0` (S2C, the brief's "IF_OPENTOP 1477"): creates/finds an
  InterfaceManager update entry for an interface hash and sets a sub-interface slot
  value; also marks the InterfaceManager dirty. (Opens a top-level/overlay interface.)
- The HUD is then populated by `op110 RUNCLIENTSCRIPT @0x00145370` (CS2 onload scripts)
  + `op82 IF_SETPOSITION` + `op35 IF_SETEVENTS` (event masks per component).

### 2.3 Why darkan's client sends op54 (worldlist-fetch) and bails

- op54 is emitted by CS2 opcode handler `FUN_00277d50`, gated on main-state ∈ {20,30}
  (§F1). The **lobby/worldlist interface's CS2** subscribes to this fetch on a timer.
- darkan reaches world-state (op81 parsed → state 30) but **never sends
  `op3 IfSetTopLevelInterface`**, so the client's top-level interface is STILL the
  lobby/worldlist UI it had before world login. That UI's CS2 keeps polling →
  client emits op54 (worldlist fetch) as its first "real" world C2S, exactly as observed.
- Meanwhile the scene never renders (black screen — camera/op75 gates, see
  `world-login-camera-render-948.md`), no in-game HUD opens, and the client's
  world session has nothing to commit to → it tears down and returns to login.

> **THE TRANSITION (the #1 answer, preliminary):** the client "becomes the in-game
> client" when the server sends the **in-game interface block** — chiefly
> **`op3 IfSetTopLevelInterface`** (swap top interface lobby→HUD), the `op110`
> CS2 onload scripts that build the HUD, and finally **`op75 SetReadyFlag`** (render
> onset, sent LAST). Without `op3`, the client stays on the lobby/worldlist interface
> regardless of being in world-state 30 — which is why it sends op54 and falls back.

(verifying the CS2→op54 subscription + the exact top-interface id next)

---

## 3. The state machine: the client IS in-game (state 30); op54 is normal [VERIFIED binary]

### 3.1 The lobby-vs-world fork is on login MODE, and darkan drives it correctly

`jag::LoginManager::LoginStepHandleLoginData @0x001cd360` ends with the fork
(decompile-verified, lines 696-719):

```c
if (LoginManager+0x20 == 2) {                 // login mode == 2  (WORLD login)
    Client::SetMainState(client, 0x1e);       //  -> 30 = WORLD / IN-GAME
    // reset the 8 social/contact slots (0x100 bytes / 0x20 stride)
} else {
    if (mode == 1) {...} else {...}
    Client::SetMainState(client, 0x14);       //  -> 20 = LOBBY
}
```

`jag::Client::SetMainState @0x004900d0` stores `newState` into the client main-state
int (the field read as `RELA[0xd40]` in the decompiler) and notifies state-listeners
via `vtable[0x10](listener, oldState, newState)`. **It does NOT itself build any
interface or render the world** — it only flips the state int + fires listeners.

**darkan's world login uses mode=2** (`world-bootstrap-948.md` §6), so the client
correctly transitions to **main-state 30 (IN-GAME)**. The client is NOT stuck in lobby
state. The "lobby-style C2S" framing in the brief is a misread: **op54
(WORLDLIST_FETCH) fires in BOTH state 20 and state 30** (its CS2 gate is
`state ∈ {20,30}`, §F1), and **production's own in-game client sends op54 at idx 13**.
op54 is a normal in-game packet, not a lobby-state symptom.

### 3.2 State enum (verified values)

| value | hex | meaning | set by |
|---:|---|---|---|
| 20 | 0x14 | LOBBY | `LoginStepHandleLoginData` (mode≠2) |
| 30 | 0x1e | WORLD / IN-GAME / LOGGED_IN | `LoginStepHandleLoginData` (mode==2) |
| 35 | 0x23 | world-switch transition | `OnMainStateTransition`; sets `+0x49` GPI flag in `REBUILD_REGION_HANDLER` |
| 36 | 0x24 | post-switch transition | `OnMainStateTransition` |
| 37 | 0x25 | reconnect/relogin transition | `OnMainStateTransition` → `StartWorldLogin` |

`jag::LoginManager::OnMainStateTransition @0x001b2b30` is a state-listener: on
transition INTO 0x23/0x25 it kicks `WorldSwitcher::SetWorldTarget` / `StartWorldLogin`
(the lobby→world handoff). It does NOT run on entry to 30.

---

## 4. THE ACTUAL "falls back to login" MECHANISM — darkan's server closes the socket on an unframable C2S opcode (#1 + #3 RESOLVED) [VERIFIED in darkan source]

The client returning to login is **not** a client-side "give up" decision — it is a
**reaction to darkan's world server closing the TCP connection.** Proof from
`core/src/main/kotlin/org/darkan/core/net/Session.kt` `readPackets` (the C2S read loop):

```kotlin
val opcode = readOpcode(input).opcode               // ISAAC-decrypt the opcode byte
val clientProt = codec.clientProtsByOpcode[opcode]
if (clientProt == null) {
    val info = codec.clientProtInfo[opcode]
    if (info == null) {                              // opcode has NO size metadata
        logMissingClientProt(input, decodedOpcode)
        return                                       // <-- bails out of the read loop
    }
    ... // else: skip `info.size` bytes, deliver UnhandledClientProt, continue
}
...
} finally {
    if (!disconnected) {
        disconnected = true
        state = State.LOST_CONNECTION
        disconnecting?.invoke()                      // <-- CLOSES THE SOCKET
    }
}
```

So: **the first C2S opcode darkan reads that is absent from `clientProtInfo` → `return`
→ `finally` closes the socket → client sees a dead connection → returns to login.**
The "op156, available=171, session closes" in the brief is exactly this: darkan
decrypted an opcode = 156, `clientProtInfo[156]` is null (no C2S op156 exists — Linux
table max 129, darkan only added macOS opcodes 203/218/240), so it bailed and closed.

**Two distinct ways darkan decrypts a 156 that isn't real:**

1. **C2S ISAAC engagement offset is wrong for WORLD** (most likely root). Production's
   first world C2S is **op240** (idx 1). darkan reads **op54** as its first. If darkan's
   ISAAC/offset were aligned to the same byte production starts at, darkan would ALSO
   see op240 first (op240 IS in darkan's table @ size 7 → it would consume 7 bytes and
   continue). Reading op54-first instead means darkan's **C2S ISAAC stream is engaged at
   a different offset / packet boundary than the client's** → every opcode is shifted →
   the 2nd "opcode" lands on garbage that happens to decrypt to 156 → fatal. (Verify
   §5.)
2. **The client genuinely emits a macOS-only opcode darkan doesn't know**, darkan can't
   size it, bails. (op240 it added; but the client may emit OTHER macOS-only opcodes —
   e.g. op203, or ones not yet captured — that darkan lacks.)

Either way the fix has two parts: (a) get darkan's C2S ISAAC engaged at the correct
WORLD offset so op240 is the first decrypted opcode (matching production), and (b)
**never `return` (close the socket) on an unknown opcode without size** — but that's a
darkan-server robustness fix, not the protocol cause.

> **THE #1 ANSWER, refined:** The client does *commit* to world-state 30 (it is the
> in-game client at the state level). It black-screens because darkan sends none of the
> render-gating sequence (varps, zone, **op3 IfSetTopLevelInterface**, op110 HUD
> scripts, **op75 SetReadyFlag**), and it "falls back to login" because **darkan's world
> server closes the socket** when it hits a C2S opcode it can't frame (the op156 bail).
> The transition the client *needs* to render and stay is the in-game interface block
> ending in `op75 SetReadyFlag` (§2.1) — AND darkan must stop closing the socket on the
> client's real C2S stream (§4/§5).

---

## 5. CAMERA / MAP chicken-and-egg — why darkan issues ZERO index-5 requests (#2 RESOLVED) [VERIFIED binary]

### 5.1 op81's BuildArea construction does NOT issue any map JS5 request

`SceneManager::FUN_00c55e80 @0x00c56160` (op81's BuildArea allocator) + the form-3
BuildArea ctor `FUN_00643d10 @0x00643d10` (9775 bytes) were decompiled in full:
**neither contains any `Js5*` / `GetFile` / `GetGroup` / `MapSquare::LoadFiles` call.**
They allocate the 0xe8340-byte build-area grid, run the ctor, and set a centre index
`+0x70`. **op81 never enqueues an index-5 map-square fetch.** (Confirms the prior doc.)

### 5.2 `MapSquare::LoadFiles` is an async callback — and it sets the camera anchor

`jag::game::MapSquare::LoadFiles @0x005c3fe0` (13278 bytes) has **no direct code
callers** — only data/vtable slots (`0x01067278`, `0x010e0ed4`, `0x0136ce60`). It is the
**completion callback** invoked when a map group's bytes are available (from the local
SQLite cache via `Js5ResourceProvider::GetFile @0x00963740` /
`RequestGroupFromDisk @0x008f76e0`, or `RequestGroupFromServer @0x009642c0`). Decoding a
spawn map file with opcode 0x10 sets the scene-config flag `ConfigProvider+0x240 → +0xd8`
= the **`anchorByte`** `ProcessCameraReset` keys on (prior doc §1).

### 5.3 The streamer gate is the SAME in the login path and the per-frame tick

`BuildArea_StreamMapSquares @0x00647fc0` is called from TWO sites:
- `FUN_0016b680` (the per-frame world tick path), and
- **`jag::ConnectionManager::LoginProtocolHandler @0x0024e8c0`** (the world-login
  orchestrator) — call-site at its line ~3132.

The login-path call is **gated identically** to the tick (decompile lines 3009-3016):

```c
worldState = client[RELA 0xcf5];
if ((worldState[0x650] & (worldState[0x638]!=-1 && worldState[0x63c]!=-1)) == 0) return;
if (worldState[0x634] == -1) return;
... compute camFloatXZ from +0x638/+0x63c ...
BuildArea_StreamMapSquares(buildArea, ..., camFloatXZ, worldState+0x690, 0);   // 3132
```

So **there is NO camera-independent initial map-load path.** Both streamer entries
require:
1. `worldState+0x650 == 1` (map-ready; set by the tick when the asset job-queue drains)
2. the camera-target triple `+0x634/638/63c != -1` (computed from
   `GetWorldTranslation(cameraNode)` — a POSITIONED camera).

### 5.4 The deadlock and its real resolution

```
streamer needs:  camera positioned (+0x418==6, triple != -1)
camera +0x418=6 needs:  ProcessCameraReset(anchorByte != 0)        [op81 tail]
anchorByte (+0xd8) set by:  MapSquare::LoadFiles  [async map decode]
map decode needs:  a JS5/cache GetFile to have been ISSUED for the spawn map
... but the only thing that issues map fetches is the streamer (camera-gated)  → DEADLOCK
```

**Resolution:** the spawn map square MUST already be loaded (its `LoadFiles` callback
already run, `+0xd8` already 1) **before op81's `ProcessCameraReset` executes.** That
load happens during the **lobby→world loading screen** (the REBUILD_REGION / world-area
setup that precedes op81), reading the spawn region from the **local SQLite cache** (no
network round-trip if cached). With `+0xd8==1`, op81's reset sets `+0x418=6`, the camera
node gets a valid world translation, the tick arms the triple, and THEN the streamer
(login-path or per-frame) pulls the surrounding index-5 squares.

**Why darkan issues ZERO index-5 requests:** darkan's lobby→world handoff never drives
the pre-op81 spawn-region map load, so when op81 runs, `+0xd8 == 0` → `ProcessCameraReset`
sets `+0x418 = 1` (NOT 6) → camera never positioned → triple stays -1 → **both streamer
gates fail forever → zero index-5 fetches → permanent black screen.** This is independent
of op54/op156/the lobby UI — it is purely the camera-anchor deadlock.

> **THE #2 ANSWER:** op81 does NOT enqueue the build-area map fetches. The spawn map is
> loaded from the local cache during the lobby→world transition (before op81), and its
> async `MapSquare::LoadFiles` callback sets the camera anchor `+0xd8`. ONLY then does
> op81's `ProcessCameraReset` reach `+0x418=6`, arming the (camera-gated) streamer.
> darkan issues zero index-5 requests because it never triggers that pre-op81 spawn-map
> load — so the camera anchor is 0, the camera is never positioned, and the streamer's
> map-ready + camera-triple gates never open. **The missing trigger is the pre-op81
> spawn-region map load (the lobby→world loading-screen REBUILD_REGION / world-area
> setup).** (The exact pre-op81 issuer is §6 — still pinning the precise call.)

---

## 6. op156 desync verdict (#3 FINAL) [VERIFIED]

- **op54 (RequestWorldList) is read with the CORRECT C2S size = 4** (darkan
  `Rev948ClientCodecs.kt:68` and the binary RegisterAll agree). The op54 read is NOT
  mis-sized.
- **Every in-game-init C2S opcode darkan can see has the correct size**: op5=4, op8=4,
  op12=−1, op52=6, op54=4, op94=3, op98=−1, op105=−2, op106=1 — all match the Linux
  binary. So op156 is **not** caused by a size-table error desyncing the stream.
- **op156 is NOT a real ClientProt opcode.** Linux RegisterAll max = 129; darkan's C2S
  `clientProtInfo` has no 156. So when darkan decrypts opcode=156 it has no size metadata
  → `logMissingClientProt` → `return` → socket close (§4).
- **Production never emits a C2S op156** (0 desync records in the 0-desync capture). Its
  first world C2S is op240 (a macOS-only opcode), then op98/op106/op105/op98/op5/op52…
  — none decode to 156.
- **Verdict:** op156 is the symptom of darkan's client running a **different C2S
  emission path** than production's in-game client (op54-first vs op240-first, because
  the in-game HUD interface was never opened — §2), combined with darkan's C2S table
  lacking the **macOS-only opcodes** the client emits (op240 is added; others like the
  ones following op54 in the lobby-UI path are not). The "op156 @ available=171" is
  EITHER (a) a macOS opcode darkan can't frame, OR (b) a downstream ISAAC desync once an
  earlier macOS opcode was mishandled. **It is not a desync in the op54 read, and not a
  size-table bug.** The Linux Ghidra binary cannot name op156 because it is not a Linux
  C2S opcode — this must be resolved by capturing the macOS client's full world C2S
  stream and extending darkan's C2S table, not from the Linux binary.

---

## 7. op22 PlayerInfo double-GPI interaction (#4) [VERIFIED binary]

The brief asks whether darkan's per-tick `op22 PlayerInfo` (5B), arriving AFTER op81's
GPI prefix, causes a double-GPI on an already-initialised list.

- op81's GPI-prefix parser `FUN_00b254a0` (§F/world-bootstrap §0) **fully initialises
  the player list**: it `operator_new`s the local player slot, spawns the entity
  (`FUN_00b04eb0`) at the `gBit(30)` tile, and initialises all 2046 other slots, then
  byte-aligns. After it runs, **every slot's "active" bookkeeping (`+0x26`/`+0x27`
  flags) is set** and the high/low-res index lists (`RELA[0x22]/[0x23]`,
  `__isoc99_sscanf+lVar2+10` etc.) are populated.
- A subsequent **per-tick** `op22 ProcessPlayerInfo @0x001618a0` does NOT re-run init —
  it walks the EXISTING index lists (the 4-pass active/inactive scan) and applies
  per-tick deltas. It reads `gBit(1)` "has-update" per slot; on a freshly-initialised
  list with no movement, the bits should be 0 (no-op). **So a correctly-encoded per-tick
  op22 after the op81 prefix is harmless** — it is what production does (first standalone
  op22 at world idx 1627, one cycle after op81).
- **The hazard is darkan's SPECIFIC op22 payload.** `world-bootstrap-948.md` §5 already
  established darkan's `buildInit`/`productionFirstLightPayload` is a *captured per-tick
  blob from a different session* whose leading bits decode `localHasUpdate=1, move=0`
  and whose ext-info is for a different player. Replaying that onto THIS session's
  freshly-initialised list feeds `ProcessPlayerInfo` a bit-stream that does not match the
  slot population op81 just created → it can mis-walk the index lists (the
  `lVar13 - lVar8 >> 2` loop bounds come from the lists op81 built; a mismatched update
  count `iVar11` from `GetHighResolutionPlayerPosition` desyncs the bit cursor) →
  ext-info applied to the wrong slot / garbage appearance. **This is a real corruption
  risk, but it does not by itself crash** (ProcessPlayerInfo returns OK on all paths).
- **Recommendation (unchanged from world-bootstrap §5/§7):** with Shape B, **do NOT send
  a standalone op22 on world-entry tick 1.** op81's prefix IS the GPI init. Send the
  first per-tick op22 only once you have a correctly *generated* (not replayed) per-tick
  bit block for THIS session's list. A wrong standalone op22 after the prefix is a
  double-GPI-style corruption; an omitted one is safe (production's first standalone op22
  is "late" relative to op81 and is a real per-tick for that session).

---

## 8. THE ORDERED S2C SEQUENCE darkan must send (the deliverable)

To make the macOS 948-5 client commit to a rendered, stable in-game world, darkan must
send the world-init burst production sends — in this order. The load-bearing additions
over darkan's current burst are marked **[MISSING]**.

```
=== Pre-op81 (lobby→world handoff / loading screen) ===
  • Ensure the SPAWN REGION's map square is loaded into the client's scene BEFORE op81
    so the camera anchor (scene-root+0x240→+0xd8) is set when op81's ProcessCameraReset
    runs. In NXT this is a LOCAL CACHE load (no JS5 network if cached), triggered by the
    lobby→world world-area setup. [MISSING / verify darkan drives it]  (§5 — the #2 fix)

=== op81 (idx 1) ===
  op81 REBUILD_NORMAL_SIMPLE  = [generated 5119-B GPI prefix][18-B coord header]
     - Shape B (already live): prefix local gBit(30) tile == coord-header centre zone
       == spawn zone; cameraRotation byte = 7 (match production); build area contains
       the spawn region.  (world-bootstrap §0, this is WORKING per the pilot.)

=== world chrome (idx 2-15) ===
  op54 HashedWorldToken, op73 MinimapState, op74 JcoinsUpdate, op172/204 MinimapFlags,
  op17 SetPlayerOp×6, op95 MidiSong         (darkan already sends most)

=== varp baseline (idx 16-1624) ===  [MISSING — darkan sends ZERO]
  op5  ResetClientVarcache
  op28 VarpLarge / op61 VarpSmall / op147 VarpLong  for the account's saved vars
     (cache-valid ids only, exact sizes; op61 BE id + (-0x80-value) transform)
     — world-bootstrap §2/§3. Required so world-entry CS2 scripts don't read defaults.

=== zone/npc reset + first per-tick (idx 1625-1630) ===
  op55 DestroyZoneData, op1 SetNpcOp
  op22 PlayerInfo  — Shape B: OMIT on tick 1 (op81 prefix is the GPI init; §7).
                     Production's first standalone op22 (idx 1627) is a real per-tick.
  op77 CAMERA_UPDATE — optional cosmetic; do NOT replay the production blob (camera-render §4)
  op130 UpdateIgnoreListRaw

=== zone content (idx ~1631-2300) ===  [MISSING — optional for first render]
  op78 UpdateZoneFullFollowsV2, op76, op46 ObjAdd, op16 LocDel, op85 UPDATE_INV_FULL,
  op44 UpdateStat …  (populate the spawn zones; not strictly required to render terrain)

=== IN-GAME HUD / interface block (idx 2301-3466) ===  [MISSING — THE #1 FIX]
  op110 RunClientScript × (the HUD onload scripts)
  op3   IfSetTopLevelInterface   ← swaps top-level interface lobby/worldlist → in-game HUD
  op82  IfSetPosition × (component positions)
  op35  IfSetEvents   × (component event masks)
  op122 IfSetText, op91 IfSetHide, op30 IfSet2DAngle, op62 IfCloseSub …
     — This is what makes the client STOP being the lobby/worldlist UI and BECOME the
       in-game client. Without op3 IfSetTopLevelInterface the client keeps the
       lobby/worldlist interface (→ emits op54 worldlist-fetch → eventual server-side
       framing failure → fall back to login).  (§2, §3)

=== render onset (idx 3467) ===  [MISSING — darkan defers it]
  op75 SetReadyFlag  ← LAST. Clears the "Loading - please wait" visibility gate.
     (camera-render §5.)

=== steady state ===
  client renders, sends ONLY op51 KEEPALIVE (~1/s). (world-bootstrap §3.)
```

### 8.1 Minimum viable to leave lobby/worldlist and render (priority order)

1. **Pre-op81 spawn-map load** so the camera anchor `+0xd8` is set → op81 positions the
   camera (`+0x418=6`) → streamer pulls index-5 → terrain renders (kills the black
   screen). (#2)  *If, after this, index-5 is still zero, the residual is dynamic — see §9.*
2. **`op3 IfSetTopLevelInterface`** (preceded by its `op110` HUD onload scripts) to swap
   the top-level interface to the in-game HUD so the client stops running the
   lobby/worldlist CS2 (kills the op54-first / lobby-C2S behaviour). (#1)
3. **`op75 SetReadyFlag` LAST** to lift the loading-screen visibility gate. (#1 render)
4. **A real (or empty) varp baseline** so HUD CS2 scripts don't trip on missing vars. (P0)
5. **darkan-server robustness:** do NOT close the socket on an unknown C2S opcode that
   has no size metadata — log it and resync/skip, and **extend the C2S `clientProtInfo`
   with the macOS-only opcodes** (op240=7 already; capture the rest of the macOS world
   C2S stream). This stops the premature "fall back to login". (#3, §4)

---

## 9. Residual unknowns (honest)

1. **The exact pre-op81 spawn-map-load issuer.** Verified: neither op81's BuildArea ctor
   nor REBUILD_NORMAL loads the spawn map; `MapSquare::LoadFiles` is an async callback;
   both streamer entries (tick + LoginProtocolHandler) are camera-gated. The spawn map
   MUST be loaded before op81's camera reset, during the loading screen, from local
   cache. **What issues that first cache load (world-area create vs a REBUILD_REGION vs
   the GPI local-player spawn keying a region load) was not pinned to a single call** —
   it is an async/timing path that a one-breakpoint dynamic check (read `+0xd8` and
   `worldState+0x418` right after op81) would settle. The prior camera-render doc §8.1
   flagged the same; this confirms it from the streamer-gate side.
2. **op156 / op240 / the macOS C2S opcode set.** These are macOS-client-specific and
   ABSENT from the Linux 948-5 binary (RegisterAll = 0-129). The Linux Ghidra project
   cannot name or size them. Resolving the exact macOS world C2S sequence (and why the
   client sends op54-first vs op240-first) requires capturing the macOS client's C2S
   under darkan (a working capture exists for production; need one for the darkan run).
3. **Whether the transition camera (`worldState+0x248`, used when `+0x418!=6`) can arm
   the camera-target triple on its own.** If it can, the camera-anchor deadlock would be
   softer than stated. The tick selects `+0x248` when `+0x418!=6` and still calls
   `GetWorldTranslation` — but only AFTER the `+0x650 && triple!=-1` gate already passed
   (chicken-and-egg). Static analysis cannot resolve the first-frame ordering; dynamic.
4. **Exact HUD interface id (the "1477-class" top-level). [RESOLVED — id = 1477].**
   `IF_SETTOPLEVELINTERFACE` sets `InterfaceManager+0xd8 = id` from the packet. The
   production op3 (world S2C idx 2295, size 19, payload
   `00000000000000000045050000000000000000`) decodes through the op3 codec
   (`Rev948ServerCodecsInterface` IF_SETTOPLEVELINTERFACE: byte9 = `writeByteAdd(id&0xFF)`,
   byte10 = `id>>8`) as `id = 0x05*0x100 + ((0x45+0x80)&0xFF) = 1280 + 197 = 1477`. So the
   in-game transition opens **1477** (the established-account HUD root), confirming the
   "1477-class" prediction — not a guess. darkan now sends `op3(1477)` for accounts with
   `characterCreated == true`, and `op3(1349)` (character-creation/gamemode-select) for a
   brand-new character. Implemented in `WorldServer.sendInGameHud` + `GameHud.open`;
   regression test `GameHudTest."production op3 in-game payload decodes to the
   GAME_HUD_INTERFACE 1477"`.

---

## 10. Function/offset reference (948-5, headless-verified this pass)

| addr | name | role |
|---|---|---|
| `0x000e6170` | `jag::ClientProt::RegisterAll` | C2S opcode table; registers ONLY op0-129 (no 156/240) |
| `0x00194c00` | `jag::ClientProt::SendWorldlistFetch` | emits C2S op54 (size 4); the "RequestWorldList" |
| `0x00277d50` | (CS2 opcode handler) | gates op54 on main-state ∈ {0x14,0x1e}; throttled by timestamp |
| `0x001618a0` | `jag::PlayerList::ProcessPlayerInfo` | op22 per-tick GPI; 4-pass list walk; no init |
| `0x00b254a0` | `FUN_00b254a0` ParseGpiPrefix_op81 | op81 GPI-prefix init: local gBit(30)+2046×gBit(20); spawns local entity |
| `0x001cd360` | `jag::LoginManager::LoginStepHandleLoginData` | world login; SetMainState(30) iff mode==2, else (20) |
| `0x004900d0` | `jag::Client::SetMainState` | sets main-state int + notifies listeners; no UI/render |
| `0x001b2b30` | `jag::LoginManager::OnMainStateTransition` | listener; on 0x23/0x25 kicks WorldSwitcher/StartWorldLogin |
| `0x000f6fe0` | `jag::packethandlers::Rebuild::REBUILD_REGION_HANDLER` | sets `+0x49` GPI flag on state→30 (param_3==0x1e, param_2≠0x23) |
| `0x00186a80` | `jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE` | op3; sets InterfaceManager+0xd8 = top interface; closes old |
| `0x001941d0` | `jag::packethandlers::Interfaces::IF_OPENTOP` | opens a top-level/overlay interface |
| `0x00145370` | `jag::packethandlers::ClientState::RUNCLIENTSCRIPT` | op110; runs CS2 (HUD onload) |
| `0x00190020` | `jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY` | S2C op216 worldlist reply |
| `0x00196170` | `jag::packethandlers::Lobby::CHANGE_LOBBY` | S2C op49 lobby↔world handoff |
| `0x001daa70` | `REBUILD_NORMAL_SIMPLE` (op81) | builds BuildArea (FUN_00c55e80); tail ProcessCameraReset(anchor) |
| `0x001203e0` | `REBUILD_NORMAL` | full rebuild; same BuildArea ctor + ProcessCameraReset(same anchor) |
| `0x00c56160` | `jag::game::SceneManager::FUN_00c55e80` | op81 BuildArea allocator; NO JS5/map call |
| `0x00643d10` | (BuildArea ctor, form 3) | 9775B; NO JS5/map call |
| `0x005c3fe0` | `jag::game::MapSquare::LoadFiles` | async map decode; sets scene anchor `+0xd8` on opcode 0x10 |
| `0x00647fc0` | `jag::game::BuildArea_StreamMapSquares` | enqueues index-5 squares; camera-gated; called by tick + LoginProtocolHandler |
| `0x0024e8c0` | `jag::ConnectionManager::LoginProtocolHandler` | world-login orchestrator; calls streamer (SAME camera gate, line ~3132) |
| `0x001444e0` | `jag::game::Camera::ProcessCameraReset` | positions camera; `+0x418 = anchorByte!=0 ? 6 : 1` |
| `0x00175120` | `SET_READY_FLAG` (op75) | global IsWorldReady; "Loading" visibility gate |

state enum: 0x14=20 LOBBY, 0x1e=30 WORLD/IN-GAME, 0x23=35/0x24=36/0x25=37 transitions.
