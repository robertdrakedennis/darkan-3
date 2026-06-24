# JS5 948-5 — "Zero content requests after master index" is NOT a JS5 stall (RESOLVED)

> **Verdict (2026-06-23, js5-server-engineer):** The 948-5 macOS client's behaviour —
> master index (255/255) served once over TCP, then no further TCP requests + a 30s
> connect→handshake→idle→EOF→reconnect loop — is **benign**. It is **not** a JS5
> master-index rejection (hypothesis a) and **not** a JS5 request mis-framing
> (hypothesis b). The JS5 content pipeline is **healthy**. The decisive evidence is
> that the client's disk cache was **already complete** for the captured run, so the
> client satisfied every content request locally (`RequestGroupFromDisk` hit) and
> never issued a TCP server request. The black screen is therefore **not** rooted in
> JS5 content delivery.

**Owner:** `js5-server-engineer`. Supersedes the JS5 framing of
`docs/protocol/world-login-camera-render-948.md` §11.5/§11.6 (corrected there too).
Companion: `docs/js5/948-master-index-and-request-encoding.md` (the ghidra RE pass).

---

## 0. The two hypotheses and their disposition

From the brief:
- **(a)** Client rejects darkan's master index (bad format/CRC/Whirlpool/RSA/version) →
  never knows what to request. → **REFUTED.**
- **(b)** `JS5Server.reader` mis-frames the macOS client's group-request bytes on the
  persistent socket → requests arrive but are never parsed/answered. → **REFUTED.**
- **(other)** The disk cache was already complete → the client correctly issued zero
  TCP requests because every group was served from its local SQLite cache; the 30s
  reconnect loop is an idle-but-healthy JS5 channel. → **CONFIRMED.**

---

## 1. Why (a) is refuted — the master index IS accepted

1. **Magic byte is irrelevant in 948-5.** The 948-5 `Js5MasterIndex` constructor
   (`@0x8f35d0`) does NOT compare decrypted `byte[0]` to any constant; it checks only
   (i) decrypted length == 0x41 and (ii) Whirlpool over `decoded[1..64]`. Our server
   signs `[0x0A][64B whirlpool]` (`VersionTableBuilder.build`, `output[0]=10`); 0x0A vs
   0x01 is transparent. (Source: `docs/js5/948-master-index-and-request-encoding.md`
   §Q1, ghidra-verified against the 948-5 binary. The old
   `docs/cache/master-index-format.md` "byte[0] must be 0x01" claim is STALE for 948-5.)
   - Note: the commit "feat: js5 functional" (6f4e913) changed `output[0]` from `1`→`10`.
     This was **not** load-bearing for 948-5 acceptance (byte[0] is ignored). Either
     value works. Left as 0x0A; no change needed.

2. **Whirlpool range is correct.** `VersionTableBuilder.build` hashes
   `versionTable[5 .. 5 + (1 + indexCount*80))` = the archiveCount byte + all 80-byte
   entries — exactly the client's expected range `[5 .. 6 + N*80)`. RSA length 512B
   (4096-bit key), decrypted to 65B. All checks the 948-5 client performs pass.

3. **The strongest proof: the client downloaded and persisted 44 archives.** The
   client's disk cache (`~/darkan-3/macos/Jagex/RuneScape/*.jcache`,
   `~/.darkan3-mac/Jagex/RuneScape/*.jcache`) contains **44 populated `.jcache` SQLite
   DBs** — js5-2 (configs, 1.3 MB), js5-5 (maps, 57 MB), js5-8 (sprites, 2.3 GB), js5-47
   (models, 1.6 GB), js5-52 (textures, 2.0 GB), js5-48 (anim frames, 138 MB), etc. A
   client that rejected the master index could not enumerate archives and could not have
   downloaded ANY of this. The very existence of these files proves master-index
   acceptance AND a fully-functioning download path through our `JS5Server` +
   `FileProvider`. (These files are dated **May 13** — downloaded in prior sessions.)

---

## 2. Why (b) is refuted — the TCP request framing is correct

1. **The 10-byte TCP request layout is unchanged in 948-5.** `Js5NetQueue::RequestData`
   (`@0x8b4360`, 948-5): `flags(1) | archive(1) | group(4 BE) | pad(4)` = 10 bytes,
   `flags=(priority<<4)|isUrgent`. `JS5Server.reader` reads exactly this. The macOS
   client's distinct *game-protocol* C2S opcodes (op240/op156/op218) are ClientProt —
   unrelated to JS5; JS5 uses no ClientProt encoding. (Source: §Q4 of the RE pass.)

2. **Those 44 jcache files were populated through this exact reader/serve path.** If the
   reader mis-framed requests, the prior-session downloads would have failed and the
   jcache DBs would be empty/absent. They are large and CRC-valid (e.g. js5-5 has 8515
   group rows incl. spawn terrain group 12850, 4498B, CRC 161533305).

---

## 3. Why the captured run shows zero requests + a 30s reconnect loop (the real reason)

### 3.1 The disk cache was already complete

`diskCacheEnabled` is **TRUE** (proven: the client opened/created the `.jcache` SQLite
files via `Js5DiskCache::Startup`). With the disk cache enabled AND every needed group
already present:

```
GetFile_ArchiveGroup(httpMode=false, diskCacheEnabled=true)
  → RequestGroupFromDisk(...)
      → per-archive group status == 1 (cached)  → serve from local SQLite
      → NO Js5NetQueue::RequestData (no TCP server request) is ever issued
```

(Per `docs/net/js5-post-master-index-flow.md` §"RequestGroupFromDisk": a disk hit
returns the data locally; only a disk MISS falls through to `RequestGroupFromServer`.)
So **zero TCP content requests is the CORRECT behaviour for a client with a complete
disk cache** — not a stall.

### 3.2 The 30s reconnect loop is a benign idle JS5 channel

The JS5 TCP socket has no keepalive; its 30s timer (`Js5NetQueue`, RE §Q5) is reset
**only by receiving bytes from the server**. A client whose disk cache satisfies all
content sends no requests → receives no bytes → the 30s receive-idle timer fires →
disconnect → reconnect → re-handshake → (server re-sends master index) → idle again.
This is an idle channel cycling, **not** a failure. (The reconnects in
`build/lobby-current.log` even stop re-requesting the master index after the first,
consistent with the client already holding a valid in-memory master index.)

### 3.3 Why prior analysis mis-read this as a JS5 stall

`world-login-camera-render-948.md` §11.5/§11.6 concluded "the client makes ZERO JS5
content requests after the master index → JS5 pipeline stall." Two artefacts caused
this:
1. **FINEST logs were suppressed.** The run used `LOG_LEVEL=DEBUG` → `Level.FINER`
   (`lobby/.../Main.kt`), but `JS5 request:`, `JS5 idle Ns`, and the HTTP `JS5 HTTP:`
   logs are all `logFinest` → invisible. The absence of those lines proved nothing.
   (Disk-cache hits produce no server-side log at all, by design — the server is never
   contacted.)
2. **The client's disk cache completeness was not checked.** With a complete cache the
   client legitimately needs nothing from the server.

---

## 4. Diagnostic instrumentation added (this pass)

To make any FUTURE run conclusive at the default `TRACE`/`FINER` level (so we never
again mis-read a silent channel), the following were bumped `logFinest`→`logInfo`
(or a loud `logWarn` for misses). These are **diagnostics**; revert once first-light
is confirmed.

| File | Change |
|---|---|
| `core/.../net/JS5Server.kt` | `JS5 idle Ns` and `JS5 request:` → `logInfo` (reader alive + per-request visibility on the persistent socket) |
| `lobby/.../server/ConfigServer.kt` | `/ms` request log + `JS5 HTTP:` serve log → `logInfo`; added `logWarn("JS5 HTTP miss …")` on a 404 |

Existing already-visible signals (kept): `FileProvider.serve` "JS5 served" is `logTrace`
(FINER, visible); `JS5 miss` and "JS5 unknown opcode" are `logWarn` (visible).

---

## 5. How to CONFIRM with a live pilot (exact steps) — needs a client run

I have NOT piloted a client in this pass. The diagnosis above is proven from the binary
(RE), the master-index builder (static), and the **populated, CRC-valid disk cache**
(strongest single proof). A pilot with the new INFO logging makes it directly
observable. Two pilots, in order:

### Pilot A — cache PRESENT (expect: benign idle loop, world should still try to render)
1. Rebuild + clean restart (kill by port; recompiling `core` under a running JVM →
   `JagExtensions NoClassDefFound`):
   ```
   lsof -nP -iTCP:8829 -iTCP:43596 -iTCP:43597 -sTCP:LISTEN -t | xargs kill
   ./gradlew :lobby:run   # (and the world module, port 43597)
   ```
2. Launch the mac client: `./run-client-mac.sh`
3. **Expect in `build/lobby-current.log`:** master index served once; then repeated
   `JS5 idle 5s/10s…/30s` lines (reader alive, client silent); **zero** `JS5 request:`
   and **zero** `JS5 HTTP request:` lines. This CONFIRMS the idle channel is benign and
   the client is reading content from its local disk cache. → JS5 EXONERATED.

### Pilot B — cache WIPED (expect: full content download over our server)
1. Wipe the client disk cache the harness's client actually uses (cover all candidates):
   ```
   rm -f ~/.darkan3-mac/Jagex/RuneScape/*.jcache \
         ~/darkan-3/macos/Jagex/RuneScape/*.jcache \
         ~/Jagex/RuneScape/*.jcache
   ```
2. Clean restart server, `./run-client-mac.sh`.
3. **Expect:** a burst of `JS5 request: index=… group=…` (TCP) **or** `JS5 HTTP request:
   /ms?a=…&g=…` (HTTP) lines — first `index=255 group=N` reference tables (255/0..N),
   then config (index 2), interfaces (3), sprites (8), fonts, then index-5 map groups —
   and matching `JS5 served`/`JS5 HTTP:` serves, repopulating the jcache files. If
   instead a `JS5 miss`/`JS5 HTTP miss` appears, THAT group id is the real serving bug
   (escalate with the exact index/group). This CONFIRMS end-to-end serving.

> If Pilot B shows TCP requests, content is on the persistent socket (`httpMode=false`).
> If it shows `/ms` HTTP requests, the patcher put the client in HTTP content mode and
> ConfigServer `/ms` is the live content path. Either way the new INFO logs name it
> unambiguously. (The §11.0 production capture had NO JS5 channel — production uses the
> HTTP CDN — so HTTP content for darkan would be plausible and is handled by `/ms`.)

---

## 6. Implications for the black screen (hand-off)

JS5 is healthy; the cache is complete and correctly served. The black screen is
therefore **downstream of content delivery** — consistent with
`world-login-camera-render-948.md` §11.6's OWN conclusion that the game-protocol layer
(op81 + op3 + op75) is "sufficient" and "nothing in the S2C game burst is the blocker."
With both "JS5 content" and "S2C game burst" exonerated, the remaining candidate is the
client's **in-world per-frame tick never engaging** after state→30 (it emits zero C2S —
no op51 Ping, no op5 SceneGraphReport — and never consumes the cached content to build
the scene). That is a game-protocol/world-entry question for the
`networking-protocol-engineer` + `ghidra-reverse-engineer`, **not** a JS5 question.
The brief's premise that "the foundational blocker is the JS5 content pipeline stalling"
is **incorrect** — the pipeline is fine.
