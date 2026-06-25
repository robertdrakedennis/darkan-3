# HANDOFF — NXT 948-5 client endlessly re-fetches multi-file group indices (login blocked)

**Status:** OPEN. The mac NXT 948-5 client cannot reach the login screen against the local Darkan server. It endlessly re-fetches **index 3 (interfaces)** groups. Date: 2026-06-25.

> **Framing (read this first):** The client is correct. There is nothing to "fix" in the client. The client re-fetches a group when the response we serve does not let it complete + cache that group. Our job is to find what is wrong **on the server side** (serving / cache data / container structure) for the groups it loops on. Earlier analysis that called this a "client in-session promotion limitation" is the wrong lens — re-read it as "our response does not satisfy the client's loader, so it correctly retries."

---

## 1. The single most important lead

**Single-file group indices load fine. Multi-file group indices loop.**

- Index **12** (clientscripts, 20,629 groups, mostly **single-file** groups) — accepted; the client gets past it.
- Index **3** (interfaces, 1,871 groups, **multi-file** groups) — NOT accepted; the client re-fetches it forever.

The client's loader splits a multi-file group into its member files using a **sub-file offset/size table**. The relevant client function (symbolicated against the macOS Mach-O `rs2client.948-5-mac`):

- Splitter at **`rs2client + 0x809c30`** (crash site `+0x809e8a` in the corrupt-cache case): reads a **4-byte big-endian sub-file offset table at `data[i*4 + 1]`**, computes `len = offset[i+1] − offset[i]`, and `memcpy`s each member file. With corrupt input it over-reads exactly 1 byte past the heap buffer (the original SIGSEGV); with clean-but-structurally-wrong input the split fails to produce decompressed data, so the group never lands in the in-memory cache and the client retries.

So the prime suspect is **how we serve/produce multi-file groups for index 3** — specifically the pieces the splitter depends on:
1. The **per-group file count** (comes from the index's ref table `255/3`, i.e. the `cache_index` rows). Wrong count → wrong buffer size → split fails/over-reads.
2. The **group container's trailing sub-file size/offset table** (the bytes the splitter reads at `data[i*4+1]`). If the decompressed group's trailing size table is malformed or our container is wrong, the offsets are wrong.

**Investigate: does our `data/cache` (and the served container) for index-3 groups carry the correct multi-file sub-file structure (file count in the ref table + size table in the group), exactly as Jagex serves it? Compare a known multi-file index-3 group byte-for-byte against the openrs2 source.**

---

## 2. Precise current symptom (with evidence)

Repro state: full server up, client's on-disk cache fully + correctly seeded (all 45 indices), client launched via the wrapper.

- JS5 socket handshake OK (`version 948.1`), master index served OK.
- Client validates + uses the seeded local cache for most indices and **gets past the index-12 loop** that previously blocked it.
- It then **re-fetches index 3 over the JS5 socket**: in a ~250 KB log window, `recent JS5 served = 1706, duplicate (index,archive) = 1379`, of which **index 3 = 1,371 requests** (HTTP `/ms` idle during this).
- The seeded `js5-3.jcache` **is being read** (atime 0s ago) — so the client reads our seeded index 3 and still re-fetches it.
- Never reaches login: `lsof` shows **0** established connections on the login port 43596, and **0** login/lobby events in the lobby log (no `Sending login`, no `RequestWorldList`, etc.).

Lobby log: `/tmp/js5dbg-lobby.log` (the live lobby's JS5 serve log — large, ~7 MB; read the tail with python `seek`).

---

## 3. The client-side mechanism (RE, from the 948-5 Mach-O; comments are in the Ghidra project)

The 948 client's group lifecycle, established by two ghidra passes:

- **`Js5Index::LoadIndex` @ 0x008ea410** — parses the per-group **version (32-bit, `Js5Index+0xD0`)** and **CRC (32-bit, `Js5Index+0xA0`)** from the `255/N` ref table. (Also the per-group **file count** for multi-file groups — confirm where this is parsed and how the splitter uses it.)
- **`Js5WorkerThread::GroupDownloaded` @ 0x008f0a50** — validates **CRC only**, over container body `[0 .. len-2)` (the trailing 2-byte version suffix is **excluded** and never value-compared).
- **`Js5MemoryCache::ProvideGroup` @ 0x008f1fb0** — sets the cached group's version/CRC straight from `Js5Index.versionArray[group]` / `crcArray[group]` (the ref table), never from the container suffix.
- **`Js5MemoryCache::LookupGroup` @ 0x008f1cc0** — returns the cached **decompressed-data pointer** (`subobject+0x30`); version/CRC come from the ref table on both sides so they cannot mismatch.
- **`FileReady_WithCheck` @ 0x00965880** — the loop. It exits **only when `LookupGroup` returns a non-null decompressed-data pointer.** "CRC mismatch" and "decompressed data never produced" are indistinguishable to it — **both loop.**

**Consequence:** the index-3 re-fetch loop means the index-3 group's **decompressed member-file data never gets produced/cached**. Since CRC/version can't be the cause (both from the ref table), the failure is in **producing the decompressed multi-file split** — i.e. the splitter (§1) can't split our group → no data → `LookupGroup` null → retry.

A prior pass attributed an *HTTP `/ms` `flag1=0`* "content-promotion gap" (group queued to disk but not made readable in-session) and documented it for index 40 in `docs/protocol/lobby-world-handoff-complete-948.md`. **But the current index-3 loop is over the SOCKET with `/ms` idle**, and the seeded data is read from disk, so the promotion-gap framing is incomplete — the unifying explanation is the **multi-file split failing to yield decompressed data** regardless of socket vs `/ms`. Treat §1 as the root and verify the multi-file container/ref-table structure.

---

## 4. VERIFIED CORRECT — do not re-chase these

- **Master index serving:** `5883 B` over the socket vs `5878 B` over HTTP is *correct framing* (socket = 10-byte response header `[index][hash:4][compression][compressedSize:4]`; HTTP = raw 5-byte container header; identical 5873-byte body). RSA-512 sig + Whirlpool validate. `FullIndexTest` 48/48.
- **Sparse index layout:** version table advertises 67 (= maxIndex+1) but 45 are active; the 22 absent are normal present-but-zero "skip" entries. The client accepts this.
- **`sectorVersion` / the 2-byte version suffix:** byte-for-byte correct — served suffix == `refTableVersion & 0xFFFF` == Jagex's wire trailer for every group (index 12 = `00 00`). Locked by `FileProviderTest` "served group version suffix equals ref-table per-group version". The client excludes it from the CRC check anyway.
- **JS5 socket framing, RSA login/JS5 keys, HTTP-port patch (80→8829):** all verified; the client connects to us and authenticates.
- **The patcher (DYLD):** works in BOTH wrapper and direct mode — patches the launcher wrapper (RSA + codebase regex) AND the spawned `rs2client` (login RSA, JS5 RSA, HTTP port). Confirmed in client logs.

## 5. RULED OUT / dead ends (so you don't repeat them)

- **Stale on-disk `.jcache`:** the very first symptom (a SIGSEGV right after the master index, `rs2client+0x809e8a`) was a **corrupt prior disk cache** making the splitter over-read. Fixed by **wiping `~/darkan-3/macos/Jagex/RuneScape/js5-*.jcache`** before launch. (Same splitter as §1 — corrupt data → crash; wrong-structure data → loop.) See memory `mac-client-stale-jcache`.
- **`SeedClientCache` CRC bug:** the seed tool stored `CRC+1` and re-encoded containers to a `ZLB` blob while keeping the *original* CRC → client rejects (32-bit-exact CRC gate) → crash. **Fixed** (now stores the source container **verbatim** with the ref-table VERSION/CRC, no `+1`, no re-encode). The fixed seed *works for single-file index 12* — proving the seed format is right for single-file groups. **Index 3 (multi-file) still loops even seeded** → the problem is multi-file-specific (§1), not the seed mechanism.
- **Today's networking changes are NOT implicated** — they're post-login (world-list codec, jav_config) and verified live.

## 6. What was tried (timeline)

1. Wipe stale jcache → fixed the initial master-index crash; client started downloading.
2. Reused a partial/stopped cache → index-12 re-fetch loop (corrupt boundary). Wipe fixes it.
3. Empty cache + full download → index-12 re-fetch loop (the `/ms` issue), impractically slow.
4. Seed login indices with the **old** SeedClientCache (CRC+1) → client crash.
5. RE disproved a "sectorVersion mismatch" hypothesis (it's correct); found the loop is *not* CRC/version.
6. Fixed SeedClientCache (verbatim + ref-table CRC) → **full 45-index seed in 1m41s** → client **gets past index 12** but **loops on index 3 (multi-file)** ← **WE ARE HERE.**

## 7. Environment + exact repro

- **Servers** (all running locally): lobby (login + JS5 socket) **43596**, ConfigServer HTTP (`/jav_config.ws`, `/ms` JS5-over-HTTP) **8829**, world **43597**, embedded mongo **37117**.
- **`.env`** (repo root): `LOBBY_PORT=43596 WORLD_PORT=43597 CONFIG_HTTP_PORT=8829 EMBEDDED_MONGO=true DEBUG=true CACHE_PATH=./data/cache CLIENT_BINARY_PATH=/Users/robert/darkan-3/macos/Jagex/launcher/rs2client.948-5-mac MAJOR_VERSION=948 MINOR_VERSION=1 LOG_LEVEL=DEBUG`.
- **Cache:** `./data/cache` = full 24.65 GB openrs2 948.1 cache (SQLite `js5-N.jcache`, tables `cache` + `cache_index`), 45 real indices + master. Source archive: `~/Downloads/cache-runescape-live-en-b948.1-2026-06-08-12-15-21-openrs2#2573.tar.gz` (openrs2 flat-file `cache/<idx>/<group>.dat` + `cache/255/<idx>.dat`).
- **Start lobby/world:** `./gradlew :lobby:run` and `./gradlew :world:run` (each long-running; redirect to a log). Lobby boot ~25 s (loads cache, ConfigServer 8829, lobby 43596).
- **Seed the client cache:** `./gradlew :tools:run -PmainClass=org.darkan.tools.SeedClientCacheKt --args="./data/cache /Users/robert/darkan-3/macos/Jagex/RuneScape [indices…]"` (no index list = all 45). Logs `seeded js5-N.jcache: refs=.. groups=..` per index.
- **Launch the client** (macOS, x86_64 under Rosetta): `MAC_CLIENT_MODE=wrapper HOST=localhost PORT=8829 ./run-client-mac.sh > /tmp/client.log 2>&1` (wrapper = RuneScape.app + `--configURI`; `MAC_CLIENT_MODE=direct` = `rs2client` + `rs-launch://localhost:8829/jav_config.ws`). The script DYLD-injects `client/launcher/patcher-mac/.../libdarkan_patcher.dylib` and forwards the dev RSA moduli + `DARKAN_HTTP_PORT=8829`. Client HOME/cache dir = `/Users/robert/darkan-3/macos/Jagex/RuneScape/`.
- **Before each clean run, wipe the live cache** (NOT the backup): `python3 -c "import glob,os;[os.remove(f) for f in glob.glob('/Users/robert/darkan-3/macos/Jagex/RuneScape/js5-*.jcache')]"`.
- **macOS gotchas:** no `timeout` binary; an fff hook blocks shell `grep/find/cat/tail/sed/awk` (use the editor's Read + fff tools, or python). Crash reports: `~/Library/Logs/DiagnosticReports/rs2client*.ips` (macOS throttles duplicate crashes — absence of a new `.ips` ≠ no crash). Parse `.ips` against the **Mach-O** (`rs2client.948-5-mac`), not the Linux ELF the project Ghidra MCP has loaded.

## 8. Code + docs + memories to read

- `core/.../world/gregs/voidps/cache/file/FileProvider.kt` — `serve()` (container + version-suffix framing; **how does it handle multi-file group containers / does it touch the sub-file size table?**).
- `core/.../world/gregs/voidps/cache/sqlite/SQLiteCache.kt`, `.../sqlite/IndexFile.kt` — ref-table (`cache_index`) + group (`cache`) access, `sectorVersion`, **per-group file-count parsing**.
- `core/.../cache/secure/VersionTableBuilder.kt` — master index (verified correct).
- `lobby/.../server/ConfigServer.kt` — `serveJs5Http` (the `/ms` HTTP JS5 path, incl. the `flag1`/`serveRaw` logic).
- `core/.../net/JS5Server.kt` — the socket JS5 session/framing.
- `tools/.../OpenRS2Import.kt` — how the openrs2 flat-file cache was imported into `data/cache` (check it preserves multi-file group bytes + ref-table file counts).
- `tools/.../SeedClientCache.kt` — the (now-fixed) seed tool.
- `docs/protocol/lobby-world-handoff-complete-948.md` — existing RE of the `/ms` `flag1=0` behavior (mapped for index 40).
- Memories (`.claude/.../memory/`): `client-cache-download-blockers`, `mac-client-stale-jcache`, `client-948-5-is-truth`, `build-real-rs-systems`.

## 9. Concrete next steps for the reviewer

1. **Pick one known multi-file index-3 group** (an interface with >1 component). Get: (a) its ref-table entry from `data/cache` `cache_index` (file count, child file IDs, version, CRC); (b) the served container bytes; (c) the openrs2 source `cache/3/<group>.dat`. **Diff them.** The defect is almost certainly that our ref-table **file count** and/or the group's **trailing sub-file size table** doesn't match what Jagex/openrs2 has, so the client's splitter (`rs2client+0x809c30`) computes wrong offsets and never yields decompressed data.
2. **Compare a single-file index-12 group the same way** to see exactly what differs structurally (12 works, 3 doesn't).
3. Confirm whether `OpenRS2Import` correctly preserved multi-file group payloads + ref-table file counts when it built `data/cache` (a likely culprit if it flattened/dropped the size table or miscounted files).
4. Once the multi-file structure is correct in `data/cache` (and thus in both the served container and a fresh seed), re-run §7: wipe → seed → launch wrapper → expect the client to load index 3 and reach the login screen (no 43596 connection yet = not there; a login handshake + `RequestWorldList` in the lobby log = success).

**Downstream (after login works):** lobby → world list (the world-list codec was fixed today), then world entry (Play Now → world) has its own separate, pre-existing blocker — see memory `world-entry-render-effort`.
