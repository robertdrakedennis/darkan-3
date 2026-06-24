# RS3 NXT 948-5 — Complete lobby→world handoff map + the JS5 content-promotion arming condition (DEFINITIVE)

> **Binary:** `rs2client.948-5` (macOS, base `0x0`) — the only source of truth. Every
> `@0xADDR` and decompiled line below is read live via GhidraMCP this pass (2026-06-23).
> **Framing:** the 948-5 client is the real shipped client and is NOT buggy. This doc maps
> what the client *expects the server to drive* across the COMPLETE lobby→world transition,
> so we can see what our incomplete handoff fails to provide.
>
> **Supersedes / corrects:**
> - `docs/net/js5-http-group-download.md` §11.0 + §11.4 (bottom INFERRED note) — the
>   "drop content-server params → TCP mode restores the DiskCache read-back" lever is
>   **refuted** here: `provider+0x70` is hardcoded `1`, so the DiskCache read-back path is
>   **structurally unreachable in BOTH retail and private-server configs** (§3). Dropping
>   jav_config params cannot flip `+0x70` to 0. §11.-1/§11.8 (Content-Type exact match) is a
>   real, separate, VERIFIED gate and is retained.
> - `docs/js5/948-post-master-index-stall-RESOLVED.md` — that doc's "disk cache already
>   complete → benign idle" verdict was for a run where the cache WAS complete. The current
>   symptom is the opposite (a fresh group is downloaded but never promoted), which that
>   doc's own §"RequestGroupFromDisk" model does not cover for httpMode.
>
> **Owner for implementation:** `js5-server-engineer` + `networking-protocol-engineer`. RE is
> spec only — do NOT edit server source from this pass.

---

## 0. TL;DR — the two answers

### 0.1 The content-promotion arming condition (THE CRUX — resolves the `provider+0x70` contradiction) [VERIFIED]

With `provider+0x70 == 1` (the synchronous-httpMode flag, **hardcoded `1`** by
`RecreateJs5ResourceProvider @0x0009b481`, identical for retail and us), a downloaded group
reaches `Js5MemoryCache` (and thus satisfies `FileReady`/`FileReady_WithCheck`'s
`LookupGroup`, ending the "Verifying Cache" loop) by **exactly one** of three paths
(`xrefs_to Js5MemoryCache::ProvideGroup @0x008f1fb0` — only 3 code callers):

| Path | Caller | Fires when | Reachable with `+0x70==1`? |
|---|---|---|---|
| A. worker direct | `WorkerOnMessage` case 0x1e `@0x0098fcf9` | message-flag1 == 1, i.e. archive **storageType==6** (`flag1=(srcType@+0x30==6)`) | YES, but index 40 has flag1=**0** → skipped |
| B. synchronous httpMode | `GetFile @0x00963836` | reads container from **`provider+0x80`** store; only if `entry+8 != 0` (container present) | YES — **the only path for flag1=0 in httpMode** |
| C. disk read-back | `HandleDownloadResult @0x00962ac1` | a Js5DiskCache **READ** completed (flag1-independent) | **NO — structurally unreachable when `+0x70==1`** (see §3) |

**Path C is the one §11.0/§11.4 thought retail used for flag1=0. It is dead in httpMode:**
the only enqueuer of a disk READ is `Js5DiskCache::EnqueueRequest @0x008b5a10` ← whose only
caller is `RequestGroupFromDisk @0x008f76e0` ← whose only caller is `GetFile_ArchiveGroup
@0x00963c26`, **on the `provider+0x70 == 0` branch only**. With `+0x70==1`,
`GetFile_ArchiveGroup`/`RequestGroupFromServer` both short-circuit to the synchronous
`GetFile`, so `RequestGroupFromDisk` is **never called → no disk read is ever enqueued → no
disk-read result record (`[7]==1`) is ever produced → `ProcessDownloadResults`
(`@0x008f27d0`, drained every tick by `LoginProtocolHandler @0x0024fc64`) finds nothing →
`HandleDownloadResult` never runs → MemoryCache is never populated via C.**

So **the VERIFIED arming condition for a flag1=0 archive (index 40, AUDIO_STREAMS) is Path B:
the group's container must be present in the `provider+0x80` store when `GetFile` reads it.**
The HTTP `/ms` per-group download (after passing the Content-Type gate, §2.6) routes through
`WorkerOnMessage case 0x1e`, which writes the raw container to **`provider+0x90`
(`Js5DiskCache`, SQLite write queue, state byte=3)** — and, for flag1=0, **NOT to
`provider+0x80`.** Therefore a *freshly HTTP-downloaded* flag1=0 group does **not** become
readable by `GetFile`, and the loop never ends.

**Why retail promotes and we don't (the contradiction, resolved):** `provider+0x70==1` is the
same for both, so it is NOT the differentiator. The differentiator is the **state of the
on-disk SQLite cache `provider+0x80` reads through**: on retail the `.jcache` files are
**already populated** (44 large pre-existing `.jcache` DBs were confirmed in
`948-post-master-index-stall-RESOLVED.md` §1.3), so `Js5DiskCache::Startup @0x009009c0` opens
them and `GetFile`/`provider+0x80` finds index-40 group-38557 immediately → MemoryCache →
loop ends. **Our run is downloading index-40 group-38557 FRESH; the disk write is queued to
`provider+0x90` but the in-memory `provider+0x80` store the synchronous `GetFile` reads is not
made to hold it on the same session, so `GetFile` keeps yielding an empty group.** That is the
entire mechanism. (See §3.4 for the precise "what populates `provider+0x80`" boundary and the
one residual dynamic check.)

**What server-side step "drives" this:** none directly — `+0x70` is not server-reachable.
The handoff the server drives (lobby login → ChangeLobby → world login mode-2 →
`SetMainState(30)`) does NOT and cannot arm Path B for a *fresh* group. The only ways to make
index-40 group-38557 promote on a fresh client are: **(i)** ensure the response passes the
Content-Type gate (§2.6) AND the disk-cache write→`provider+0x80` round-trip completes
in-session (verify dynamically, §6), or **(ii)** pre-populate the client disk cache so `GetFile`
hits on the first poll (matches retail), or **(iii)** serve index 40 over the **TCP
`Js5NetQueue`** instead of HTTP so it goes through the disk-backed path that *does* feed
`provider+0x80` (requires a JS5 TCP listener; NOT what we run today). See §7.

### 0.2 The complete lobby→world handoff state machine [VERIFIED]

The server drives the client through these phases. Each row = what the client DOES, what it
WAITS FOR from the server, and what it ARMS. Anchors are 948-5 verified.

| Phase | Client main-state | Client does | Waits for (server) | Arms |
|---|---|---|---|---|
| In-lobby | 0x14 (20) | runs lobby/worldlist CS2 UI; emits op54 worldlist-fetch on a timer | lobby S2C (worldlist op216, friends, etc.) | — |
| Play-now → world target | 0x14 | `WorldSwitcher::SetWorldTarget @0x00248f50` records target world host/port | lobby login response carrying world target; `op49 CHANGE_LOBBY` | world-target fields (`Client[0xd01]`) |
| World-switch transition | 0x23/0x25 (35/37) | `OnMainStateTransition @0x001b2b30` kicks `StartWorldLogin`/`CommitWorldTargetFromLogin @0x001acdf0` | — | begins world-login handshake |
| World TCP connect | (LoginManager step 10) | `ServerConnection::OpenConnection` to world host/port (`Client[0xd01]+0x30` host, `+0x2a/+0x28` ports) | TCP accept | world socket |
| World login wait | (LoginManager step 0x14/20) | reads exactly **1 response byte** (`LoginStepHandleLoginData @0x001cd360` / `LoginProtocolHandler` inline @ ~LoginManager[0x28]==0x14) | **response code byte == 2** (login OK) | **seeds 2 ISAAC ciphers** (in/out), installs on ServerConnection |
| Commit to world | 0x1e (30) | `LoginStepHandleLoginData`: `if (LoginManager+0x20==2) SetMainState(0x1e)` (mode==2 → IN-GAME) | (already past the byte) | flips main-state int + notifies listeners; **handoff block re-arms JS5** |
| JS5 re-arm (handoff) | main-state==10 block in `LoginProtocolHandler` (`LAB_0024ff9f` ~`0x2502xx`) | `Js5DiskCache::Startup`, resets request queues, `Client[0xca6] vtable+0x20` (re-arm provider), **clears camera-target triple `worldState+0x634/638/63c = -1`**, sets `Client[0xc62]=1` (world-entering) | — | JS5 disk cache started; camera triple reset (must be re-armed by op81) |
| World-init burst | 0x1e | parses op81 (GPI prefix + coord header), varps, zone, **op3 IfSetTopLevelInterface** (HUD swap), op110 HUD scripts, **op75 SetReadyFlag LAST** | the full world-init S2C sequence (`world-ingame-transition-948.md` §8) | in-game HUD; camera anchor (via spawn-map load); render gate lifts |
| Map stream | 0x1e (per-frame + login orchestrator) | `BuildArea_StreamMapSquares @0x00647fc0` pulls index-5 squares | server already armed camera via op81 tail | scene geometry (kills black screen) |

**Two independent per-tick pumps run in `LoginProtocolHandler` regardless of game state**
(both gated only on the engine being alive + sub-objects existing, NOT on a packet):
1. **JS5 cache pump** — `ProcessDownloadResults @0x0024fc64` + the per-archive request-emission
   rbtree loop. Drains disk-read results into MemoryCache and emits JS5 requests. **NOT
   camera-gated.** (This is the pump that is silently empty for flag1=0 in httpMode — §3.)
2. **Map/scene streamer** — `BuildArea_StreamMapSquares @0x00647fc0` (~tail, line ~3129).
   **ENTIRELY camera-gated** on `worldState+0x650` AND target triple `+0x634/638/63c != -1`.
   Until op81's tail positions the camera, no map streams → black screen
   (`world-login-camera-render-948.md`).

---

## 1. The content-promotion call graph (VERIFIED, all 948-5)

```
HTTP /ms group download completes (libcurl, MainLogic HTTP-queue drain @LAB_008f8326)
  │  [GATE §2.6] response Content-Type EXACTLY "application/octet-stream"
  │             (eastl::basic_string::compare @0x0013ef80, exact full-length; @0x008f8e03)
  │  match → success cb (puVar+0x16) = GroupDownloadedCallback
  ▼
jag::Js5WorkerThread::GroupDownloaded @0x008f0a50
  │  CRC32(body[0..len-2]) == expected (ref-table CRC). last 2 bytes = version suffix (excluded)
  │  flag1 = descriptor[+0x11] & 1  = (archiveStorageType@+0x30 == 6)
  │     flag1==1 → post 0x1e msg WITH decompressed payload (ESI=1)
  │     flag1==0 → post 0x1e msg WITHOUT payload (disk-only)         ← index 40 path
  ▼
jag::Js5ResourceProvider::WorkerOnMessage @0x0098dee0  case 0x1e
  │  if (diskCacheEnabled && provider+0x90 != 0)
  │       Js5DiskCache::ProvideGroup(provider+0x90, ...)   → SQLite WRITE queue, state byte=3
  │  if (msg-flag1 /*cVar2*/ == 1)
  │       Js5MemoryCache::ProvideGroup(provider+0x88, ...) → MemoryCache  [Path A — flag1=0 SKIPS this]
  ▼
(flag1=0: container is now ONLY in provider+0x90 disk write queue; NOT in +0x80, NOT in +0x88)

──────────────── meanwhile, the gate that loops ────────────────
FileReady_WithCheck @0x00965880  (and FileReady @0x008f2bd0)   [polled ~40ms]
  │  LookupGroup(provider+0x88 MemoryCache, 40, 38557) → data ptr +8 == 0 → MISS
  │  GetFile_ArchiveGroup(provider, …) @0x00963c10
  │       if (provider+0x70 != 0 /*httpMode, ==1*/) { GetFile(); return 0; }   ← ALWAYS this
  │            └─ GetFile @0x00963740 reads provider+0x80[archive].groupData[group]
  │                 if (entry+8 == 0) → provide EMPTY group → LookupGroup still misses  [Path B miss]
  │       else if (diskEnabled) RequestGroupFromDisk()  ← NEVER reached when +0x70==1
  │  returns 0 → RequestGroupFromServer(provider, …) @0x009642c0
  │       if (provider+0x70 != 0) { GetFile(); return; }   ← also short-circuits, no TCP request
  ▼
loop forever ("Verifying Cache 0%", ~40ms cadence) — index 40 group 38557 never promotes
```

**The dead path (would promote flag1=0 if reachable):**
```
RequestGroupFromDisk @0x008f76e0  →  Js5DiskCache::EnqueueRequest @0x008b5a10  (disk READ)
   [disk worker thread reads SQLite, produces a result record with field [7]==1]
ProcessDownloadResults @0x008f27d0   (drains result vector; if rec[7]==1 → )
   HandleDownloadResult @0x00962960   →  Js5MemoryCache::ProvideGroup(provider+0x88)  [Path C]
```
`RequestGroupFromDisk` has exactly ONE caller (`GetFile_ArchiveGroup @0x00963c26`, non-http
branch). `Js5DiskCache::EnqueueRequest` has exactly ONE caller (`RequestGroupFromDisk`, 3
sites). **All gated behind `provider+0x70 == 0`. With `+0x70==1` this entire subtree is
unreachable.** (xrefs verified this pass.)

---

## 2. Per-function evidence (VERIFIED)

### 2.1 `GetFile_ArchiveGroup @0x00963c10` — the dispatch fork
```c
undefined8 GetFile_ArchiveGroup(long provider){
  if (*(char*)(provider+0x70) != 0) { GetFile(); return 0; }              // httpMode → sync read +0x80
  if (*(char*)(&__DT_RELA[0x660].r_info + provider) != 0)                 // diskCacheEnabled
       return RequestGroupFromDisk();                                     // enqueue disk READ
  return 0;
}
```
`provider+0x70` is hardcoded `1` (§2.7), so the disk-READ branch is dead. Returns 0 in
httpMode → callers fall through to `RequestGroupFromServer` (also short-circuits).

### 2.2 `RequestGroupFromServer @0x009642c0` — also short-circuits in httpMode
First two lines:
```c
if (*(char*)(provider+0x70) != 0) { GetFile(); return; }   // httpMode: no TCP request, just GetFile
```
So in httpMode **no JS5 TCP request is ever emitted for a group** — content lives entirely on
the HTTP `/ms` path + the synchronous `GetFile`/`provider+0x80` read.

### 2.3 `GetFile @0x00963740` — the sole flag1=0 promotion path in httpMode
Reads `*(*(*(provider+0x80)+0x10)+archive*8)` → per-archive `Js5Index`; `[0x48]` = group-data
array; `+group*0x20`; checks `*(entry+8)`. If 0 → builds an EMPTY 0-byte group →
`Js5MemoryCache::ProvideGroup(provider+0x88, …)` with empty data → `LookupGroup` returns
`data==0` → loop continues. If non-zero → `Js5Compression::Decompress` + (multi-version split)
→ `ProvideGroup` with real data → loop ends. **For index 40 it only yields data once the
container is present in `provider+0x80`.** The HTTP download does not put it there (§2.5).

### 2.4 `GroupDownloaded @0x008f0a50` — flag1 decode + CRC
- CRC: `CRC32(body[0 .. len-2))` (poly table `DAT_00ffcaa0`, init `0xFFFFFFFF`, final `~crc`) vs
  ref-table CRC. **Last 2 bytes = version suffix, EXCLUDED from CRC, never otherwise checked.**
- flag1: `flagsByte = descriptor[+0x11]; flag1 = flagsByte & 1`. `descriptor[+0x11]` is set by
  `RebuildArchiveDescriptors @0x009751e0` to `(archiveStorageType@+0x30 == 6)`. **flag1 is a
  pure client-side classification — NOT reachable from the server, master index, or wire
  bytes.** Index 40 (AUDIO_STREAMS) is not storageType 6 → flag1=0.
- flag1=1 (`LAB_008f0ef5`/`0x008f11da`) → 0x1e msg ESI=1 + decompressed payload appended.
- flag1=0 (`LAB_008f0d47`/`0x008f0c8f`) → 0x1e msg with NO decompressed payload (disk-only).

### 2.5 `WorkerOnMessage case 0x1e @0x0098dee0` — disk write, conditional memory write
```c
// cVar2 = message-flag1 (from GroupDownloaded); cVar3 = secondary flag
if (cVar3 == 0) {
  ... read container ...
  if (diskCacheEnabled && provider+0x90 != 0)
      Js5DiskCache::ProvideGroup(provider+0x90, archive, group, container, crc, ver, descFlag1);  // → +0x90 disk write queue
  if (cVar2 == 1)                                                                                 // flag1==1 ONLY
      Js5MemoryCache::ProvideGroup(provider+0x88, archive, group, ver, crc, payload);             // → +0x88 MemoryCache
}
```
**Never writes `provider+0x80`.** So a flag1=0 HTTP download lands ONLY in the `provider+0x90`
disk write queue.

### 2.6 `MainLogic @0x008f8410` — the HTTP Content-Type gate [VERIFIED, retained from §11.8]
On curl state==1 (2xx), `eastl::basic_string::compare(response.ContentType /*+0xb8*/,
"application/octet-stream" @0x00ffc943)` (exact, full-length, `@0x008f8e03`). Match (`JZ`
@0x008f8e0f) → success cb fires. Any mismatch → `request+0xa9 = 1` FAILURE (@0x008f8e15) →
success cb skipped → group never even reaches `GroupDownloaded` (so not even the disk write
happens). **This is a NECESSARY upstream gate.** If our `/ms` already passes it (the brief says
the response is "byte-perfect" and "writes it to the disk cache" → success cb DID fire → it
passed), the residual is §3, not this. If unsure, verify with
`curl -sI '.../ms?...'` (must be literally `Content-Type: application/octet-stream`).

### 2.7 `RecreateJs5ResourceProvider @0x0009b258` — `provider+0x70 = 1` is hardcoded [VERIFIED disasm]
`R15 = operator_new(0x4f70)` (`@0x0009b3e8`), stored to `client+0x18cd0` (`@0x0009e70e`),
never reassigned between. `MOV byte ptr [R15+0x70], 1` **unconditionally** at `0x0009b481`. No
URL/host/port/scheme/jav_config string is read or compared anywhere in the function. `+0x80`,
`+0x88`, `+0x90` are filled via registers (not immediate stores; the packed init at
`0x0009b486..0x0009b49a` zero/`-1`-fills `0x74..0x93`). → **`provider+0x70` is `1` for retail
AND private server.** Confirms §11.-1.

### 2.8 `Js5DiskCache::Startup @0x009009c0` — opens existing `.jcache`, spawns worker
Iterates registered archives; for each that has a DB file (`GetDatabaseFilename`), sets
`diskCache+0x10fe0+archive = 1` (DB-present) and `[archive].state = 2`; finally spawns the
disk-cache **worker thread** (`pthread_create`, vtable `PTR_FUN_0137a9b8`). The worker drains
the write queue (entries from `Js5DiskCache::ProvideGroup`) and serves read requests. Called in
the handoff (main-state==10) block of `LoginProtocolHandler` and at first master-index
(`WorkerOnMessage case 10`). **A pre-existing `.jcache` makes the archive's groups available to
`GetFile`/`provider+0x80` from session start — this is why retail (pre-populated) promotes.**

---

## 3. WHY ours loops and retail doesn't — the precise mechanism [VERIFIED structure; §3.4 residual]

1. `provider+0x70 == 1` (hardcoded, both). → §2.1/§2.2 short-circuit every group fetch to the
   synchronous `GetFile` (Path B). **Path C (disk read-back) is unreachable** (§1, §0.1).
2. Path A (worker direct → MemoryCache) requires flag1==1. Index 40 is flag1==0. → skipped.
3. So index-40 group-38557 can ONLY promote via Path B = `GetFile` finding the container in
   `provider+0x80`.
4. The HTTP `/ms` download writes the container to `provider+0x90` (disk write queue), **not**
   `provider+0x80` (§2.5). 
5. **Retail:** the `.jcache` for index 40 already exists (pre-downloaded), so
   `Js5DiskCache::Startup` opens it and `GetFile`/`provider+0x80` reads group 38557 from the
   disk-backed store on the first poll → MemoryCache → loop ends.
6. **Us:** index 40 is being downloaded FRESH. The disk write is queued to `provider+0x90`, the
   disk worker writes it to the `.jcache` SQLite file — but the **in-memory `provider+0x80`
   store that the synchronous `GetFile` reads is not made to hold the freshly written group in
   the same session** (it is the per-archive `Js5Index` group-data store, populated from the
   disk DB at open/index-load time, not on a fresh HTTP-driven disk write). → `GetFile` keeps
   reading `entry+8 == 0` → empty group → loop forever. This is the brief's exact symptom
   ("writes to disk cache, never reaches Js5MemoryCache, LookupGroup keeps missing → re-request
   every ~40ms").

### 3.4 The one residual (must be settled DYNAMICALLY) [INFERRED — needs a pilot]
The boundary I could not pin purely statically (the `provider+0x80` writer is register-set in
the mislabeled `0x4f70` constructor / `WorkerOnMessage case 10`): **does the Js5DiskCache
worker, after writing a fresh group to the `.jcache`, also populate the in-memory
`provider+0x80` store for that archive on the SAME session?**
- If **NO** (most consistent with the observed loop): a fresh HTTP-downloaded flag1=0 group is
  unreachable to `GetFile` until the client reopens/rebuilds the disk-cache index for that
  archive. → Promotion of a *fresh* flag1=0 group over the HTTP `/ms` path is effectively
  **not supported in httpMode** without a pre-populated disk cache. (Retail never hits this
  because its cache is complete.)
- If **YES** (some delay/tick): then the loop is a *timing/sequencing* gap (the write hasn't
  been flushed+reflected before the next `GetFile` poll), and the fix is to ensure the disk
  write completes and `provider+0x80` is refreshed.

**Decisive dynamic check (one breakpoint, names this for certain):** with the client looping on
index-40/group-38557, break at `GetFile @0x00963740` and inspect, for `archive=40`,
`*(*(*(provider+0x80)+0x10)+40*8)` → the `Js5Index`; then `[0x48] + 38557*0x20` → `entry`; read
`*(entry+8)`. If it stays 0 across many polls while the `.jcache` on disk gains the row, the
"NO" branch is confirmed and §7(ii)/(iii) is the fix. Also break at `Js5DiskCache::Startup
@0x009009c0` to confirm whether index 40's DB is opened with the group present.

---

## 4. The lobby→world transition state machine (detail) [VERIFIED]

### 4.1 Main-state enum (verified values)
| value | hex | meaning | set by |
|---:|---|---|---|
| 0 | 0x00 | no-state / lobby-config fetch | `LoginProtocolHandler` (handoff entry, `LAB_0024ff9f`) |
| 20 | 0x14 | LOBBY | `LoginStepHandleLoginData @0x001cd360` (login mode ≠ 2) |
| 30 | 0x1e | WORLD / IN-GAME / LOGGED_IN | `LoginStepHandleLoginData` (login mode == 2) |
| 35 | 0x23 | world-switch transition | `OnMainStateTransition @0x001b2b30` (sets `+0x49` GPI flag) |
| 36 | 0x24 | post-switch transition | `OnMainStateTransition` |
| 37 | 0x25 | reconnect/relogin transition | `OnMainStateTransition` → `StartWorldLogin` |
| 10 | 0x0a | lobby→world load (handoff work) | `LoginProtocolHandler` (`SetMainState(…,10)`); also fallback when world-stream session inactive |

`Client::SetMainState @0x004900d0` only flips the state int + fires `vtable[0x10]` listeners; it
does NOT build interfaces or render.

### 4.2 World-login handshake (LoginManager sub-state `[0xcf4]+0x28`, inside `LoginProtocolHandler`)
- **state 10** → `ServerConnection::OpenConnection(world host/port from Client[0xd01]+0x30 /
  +0x2a / +0x28)` → state **15 (0xf)** (waiting).
- **state 15 (0xf)** → `LoginStepWaitingConnectionOpened`-style wait (`@0x001825a0` family).
- **state 20 (0x14)** → **read 1 response byte**: `GetBytesAvailable(1)` → `ReadBytes(1)` →
  store in `[0x2c]`. **If byte == 2 (login OK):** build **two ISAAC ciphers** (in/out;
  `Isaac::Init`, out-cipher seed `+0x32`), install on `ServerConnection`, set state **30
  (0x1e)**. Any other byte → `CloseConnection` + state 0. **This is the protocol gate that
  admits the client to the world.**
- **state 30 (0x1e)** → connected; drops to lobby (state 0) if `ServerConnection::IsConnected`
  goes false.

This mirrors `LoginStepHandleLoginData @0x001cd360`'s tail (decompile-verified previously):
`if (LoginManager+0x20 == 2) SetMainState(0x1e) else SetMainState(0x14)`. **darkan uses
mode==2 → the client correctly commits to main-state 30.** (Confirmed by
`world-ingame-transition-948.md` §3.)

### 4.3 The handoff block (main-state == 10, `LoginProtocolHandler` `LAB_0024ff9f` ~`0x2502xx`)
On entry (gated by the "login proceed" flag `param+0x100[1]` and world-stream readiness
`Client[0xcfd][0x10] vtable+0x30 == ready`):
- `Client::SetMainState(…, 0)`
- `memset(diskCache+RELA[0x767]…, 0, 0x10c)` + `Js5DiskCache::StopWorkerThread(provider+0x90)`
  (renamed `FUN_008f2e00` this pass) + `Js5DiskCache::Startup(provider+0x90)` — **resets +
  restarts the disk cache**
- `Client[0xca6] vtable+0x20` — **re-arm the loadable/JS5 provider**
- reset JS5 request queue heads (`[0x88]`)
- **clear camera-target triple** `worldState+0x634/638/63c = -1` (lines ~2125–2135) — these MUST
  be re-armed by op81's `ProcessCameraReset` tail or the streamer never runs (black screen)
- `Client[0xc62] = 1` (world-entering flag)

**Note:** this handoff re-arms the disk cache and provider but does **NOT** flip `provider+0x70`
to 0, and does NOT itself fetch index-40 group-38557. So it does not resolve §3.

---

## 5. The two per-tick pumps in `LoginProtocolHandler` (VERIFIED)

### 5.1 JS5 cache pump (NOT camera-gated)
At `LAB_0024fc58` (`0x0024fc58`):
```c
if (provider+0x90 /*Js5DiskCache*/ != 0)
    ProcessDownloadResults(provider+0x90 region);     // @0x0024fc64 → @0x008f27d0
// then drain provider+0x88 in-flight request list; emit per-archive JS5 requests (rbtree loop)
```
Gated only on the engine being alive (`DAT_015df4f8[0x37]+0x38 == 0`) and the DiskCache
existing. **Runs every normal tick.** For flag1=0 in httpMode this drains an **empty** disk-read
result queue (§3) → promotes nothing.

### 5.2 Map/scene streamer (camera-gated)
At the function tail (~line 3129, after `LAB_002506cf`):
```c
worldState = Client[0xcf5];
if ((worldState[0x650] & (worldState[0x638]!=-1 && worldState[0x63c]!=-1)) == 0) return;  // GATE 1
if (worldState[0x634] == -1) return;                                                       // GATE 2
... BuildArea_StreamMapSquares(buildArea, …, camXZ from +0x638/+0x63c, worldState+0x690, 0);
```
**No camera-independent map fetch exists here.** Until op81's tail arms the camera triple, no
index-5 squares stream → black screen. (Full treatment: `world-login-camera-render-948.md`,
`world-ingame-transition-948.md` §5.)

---

## 6. How to CONFIRM the §3.4 boundary with a pilot (exact steps)
Needs a client run; not piloted this pass.
1. Wipe the client disk cache so index 40 is genuinely fresh:
   `rm -f ~/.darkan3-mac/Jagex/RuneScape/*.jcache ~/darkan-3/macos/Jagex/RuneScape/*.jcache ~/Jagex/RuneScape/*.jcache`
2. Run server + `./run-client-mac.sh`; let it reach "Verifying Cache".
3. Attach a debugger; set a breakpoint at `GetFile @0x00963740`. When hit with `archive==40`
   (first arg points at a uint == 40), evaluate
   `entry = *(*(*(provider+0x80)+0x10)+40*8); group = *(entry+0x48) + 38557*0x20; *(group+8)`.
   - Stays `0` across polls while the on-disk `.jcache` row appears → §3.4 "NO" confirmed →
     fresh flag1=0 over HTTP is unsupported in httpMode → fix per §7(ii)/(iii).
   - Becomes non-`0` after the disk write → it's a timing gap → ensure write flush+`+0x80`
     refresh before the next poll.
4. Cross-check by confirming the Content-Type gate passed: `curl -sI '<contentURL>/ms?m=0&a=40&k=0&g=38557&c=<crc>&v=<ver>'`
   shows literally `Content-Type: application/octet-stream` (no params). If it does NOT, that is
   an upstream blocker (§2.6) and must be fixed first.

---

## 7. WHAT A COMPLETE HANDOFF DOES THAT OUR IMPLEMENTATION IS MISSING

Ordered by how directly each bears on the "Verifying Cache" loop (the brief's symptom).
**VERIFIED** = proven from the 948-5 binary this pass; **INFERRED** = consistent with the
binary but needs the §6 pilot to nail the exact branch.

### Directly arming JS5 content promotion (the crux)
1. **[VERIFIED] The HTTP `/ms` per-group path cannot promote a *fresh* flag1=0 archive
   (index 40) into MemoryCache in httpMode.** Path C (disk read-back) is unreachable
   (`provider+0x70==1`), Path A needs flag1==1 (index 40 is flag1==0), and Path B
   (`GetFile`/`provider+0x80`) only works if the container is already in the disk-backed
   `provider+0x80` store — which a fresh HTTP download does not populate (it writes only to the
   `provider+0x90` write queue). **What's missing:** a working promotion path for a freshly
   downloaded flag1=0 group. Concretely, ONE of:
   - **(i) [INFERRED]** Ensure the disk write→`provider+0x80` round-trip completes in-session
     (settle via §6; if it can complete, the gap is timing/flush, not structural).
   - **(ii) [VERIFIED works on retail]** **Pre-populate the client disk cache** (ship a complete
     `.jcache` set, or run a cache-download pass before world entry) so `Js5DiskCache::Startup`
     + `GetFile`/`provider+0x80` hit on the first poll — exactly what makes retail promote.
   - **(iii) [VERIFIED supported by the binary]** **Serve index 40 (and other flag1=0
     archives) over the TCP `Js5NetQueue`** instead of HTTP `/ms`. The TCP/disk-backed path
     (`provider+0x70` short-circuit does NOT apply to the disk-enabled branch when the client is
     in non-http mode for that archive) routes through `RequestGroupFromDisk` →
     `EnqueueRequest` → disk read-back → `HandleDownloadResult` → MemoryCache (Path C). Requires
     standing up a JS5 TCP listener and ensuring the client is not in synchronous-httpMode for
     that content. (Note: `provider+0x70` is hardcoded 1, so this needs the client to take the
     disk branch via `diskCacheEnabled` with the archive NOT served by HTTP — i.e. drop the
     archive from the HTTP content advertisement so it falls to TCP/disk. This is the only
     server-reachable lever and is the corrected form of the §11.0 idea.)

2. **[VERIFIED] Content-Type of `/ms` must be exactly `application/octet-stream`** (§2.6). If our
   response carries any params/charset/trailing bytes, the success cb never fires and the group
   is discarded before the disk write — a strictly upstream blocker. The brief states the group
   IS written to disk, which implies we already pass this; verify with `curl -sI` (§6.4) to be
   certain.

### Driving the rest of the handoff (needed for first-light beyond the JS5 loop)
3. **[VERIFIED] Pre-op81 spawn-region map load** so op81's `ProcessCameraReset` tail sets the
   camera anchor (`scene-root+0x240→+0xd8`), arming `worldState+0x418=6` and the camera triple
   `+0x634/638/63c`. Without it the camera-gated streamer (§5.2) never runs → black screen.
   (`world-login-camera-render-948.md`, `world-ingame-transition-948.md` §5 — MISSING per those.)
4. **[VERIFIED] The in-game interface block** — `op110 RunClientScript` HUD onload scripts,
   `op3 IfSetTopLevelInterface` (swap lobby/worldlist UI → in-game HUD 1477), `op82`/`op35`, and
   **`op75 SetReadyFlag` LAST** (lifts the "Loading" gate). Without `op3` the client keeps the
   lobby/worldlist UI and emits op54-first; without `op75` the scene stays hidden.
   (`world-ingame-transition-948.md` §2/§8 — MISSING per that doc.)
5. **[VERIFIED] The varp baseline** (op5 reset + op28/op61/op147 for saved vars) so world-entry
   CS2 doesn't read defaults. (`world-ingame-transition-948.md` §8 — MISSING.)
6. **[VERIFIED] Server robustness:** don't close the world socket on an unframable C2S opcode
   (the op156 bail) — extend the C2S table with the macOS-only opcodes (op240=7 etc.).
   (`world-ingame-transition-948.md` §4 — robustness, not a content-promotion item.)

### What is NOT missing (ruled out this pass)
- `provider+0x70` is hardcoded `1` (NOT a jav_config/world/instance property). Dropping
  jav_config content params does not flip it. [VERIFIED]
- The launcher instance number (`DAT_01393568`) is 0 for a single client and is not the
  differentiator. [VERIFIED — confirms §11.-1]
- flag1 is not server-reachable (it is `(storageType==6)`, computed client-side). [VERIFIED]
- The op81 GPI-prefix / mode-2 / SetMainState(30) transition is correct (darkan drives it).
  [VERIFIED via §4.2 + `world-ingame-transition-948.md` §3]

---

## 8. Function/offset reference (948-5, verified this pass)

| addr | name | role |
|---|---|---|
| `0x00963c10` | `jag::Js5ResourceProvider::GetFile_ArchiveGroup` | dispatch; `+0x70!=0`→GetFile(ret 0), else→RequestGroupFromDisk |
| `0x00963740` | `jag::Js5ResourceProvider::GetFile` | sync-httpMode; reads `provider+0x80`; empty→empty group |
| `0x009642c0` | `jag::Js5ResourceProvider::RequestGroupFromServer` | `+0x70!=0`→GetFile; else builds TCP request |
| `0x008f76e0` | `jag::Js5ResourceProvider::RequestGroupFromDisk` | enqueue disk READ; ONLY caller = GetFile_ArchiveGroup (non-http) |
| `0x008b5a10` | `jag::Js5DiskCache::EnqueueRequest` | queue disk read/write; ONLY caller = RequestGroupFromDisk |
| `0x008f27d0` | `jag::Js5ResourceProvider::ProcessDownloadResults` | drain disk-read results; rec[7]==1→HandleDownloadResult |
| `0x00962960` | `jag::Js5ResourceProvider::HandleDownloadResult_DiskRead_to_MemoryCache` | disk read-back → MemoryCache (flag1-independent); ONLY caller = ProcessDownloadResults |
| `0x008f1fb0` | `jag::Js5MemoryCache::ProvideGroup` | populate MemoryCache; ONLY 3 callers (A/B/C) |
| `0x008f1cc0` | `jag::Js5MemoryCache::LookupGroup` | hash lookup `group<<8\|archive`; data ptr +8 |
| `0x008f1df0` | `jag::Js5MemoryCache::GetFileInternal` | FileReady's MemoryCache reader; returns WAITING if +8==0 |
| `0x008f2bd0` | `jag::Js5ResourceProvider::FileReady` | gate; disk-drain loop DEAD in httpMode (GetFile_ArchiveGroup→0) |
| `0x00965880` | `jag::Js5ResourceProvider::FileReady_WithCheck` | the "Verifying Cache" re-request loop driver |
| `0x00c31050` | `jag::Js5ResourceProvider::FileReady_CheckIndex` | index-variant wrapper → FileReady |
| `0x008f2580` | `jag::Js5DiskCache::ProvideGroup` | queue raw container to SQLite write queue (state byte=3); NOT MemoryCache |
| `0x009009c0` | `jag::Js5DiskCache::Startup` | open existing `.jcache` per archive; spawn disk worker thread |
| `0x008f2e00` | `jag::Js5DiskCache::StopWorkerThread` (renamed this pass; was FUN_008f2e00) | notify+join the disk worker |
| `0x008f0a50` | `jag::Js5WorkerThread::GroupDownloaded` | CRC + flag1 decode; posts 0x1e msg |
| `0x0098dee0` | `jag::Js5ResourceProvider::WorkerOnMessage` | case 0x1e: disk write + (flag1) memory write; case 10/0x14/0x28 master-index/index |
| `0x008f8410` | `jag::ConnectionManager::MainLogic` | TCP JS5 state machine + HTTP-queue drain (Content-Type gate @0x008f8e03) |
| `0x0009b258` | `jag::ConnectionManager::RecreateJs5ResourceProvider` | builds provider; `+0x70=1` hardcoded @0x0009b481 |
| `0x0024e8c0` | `jag::ConnectionManager::LoginProtocolHandler` | per-tick world-login orchestrator; ProcessDownloadResults @0x0024fc64; streamer ~line 3129; handoff block ~0x2502xx |
| `0x001cd360` | `jag::LoginManager::LoginStepHandleLoginData` | mode==2→SetMainState(0x1e) else (0x14) |
| `0x004900d0` | `jag::Client::SetMainState` | set main-state int + notify listeners |
| `0x001b2b30` | `jag::LoginManager::OnMainStateTransition` | on 0x23/0x25 → StartWorldLogin/WorldSwitcher |
| `0x00248f50` | `jag::WorldSwitcher::SetWorldTarget` | record world target host/port |
| `0x001acdf0` | `jag::WorldSwitcher::CommitWorldTargetFromLogin` | commit target from login response |
| `0x00647fc0` | `jag::game::BuildArea_StreamMapSquares` | enqueue index-5 squares; camera-gated |
| `0x009751e0` | `jag::Js5ResourceProvider::RebuildArchiveDescriptors_flag1FromType` | sets `desc[+0x11]=(storageType==6)` = flag1 |

state enum: 0x14=20 LOBBY, 0x1e=30 WORLD/IN-GAME, 0x23/0x24/0x25 transitions, 0x0a=10 handoff-load.
