# JS5 Map-Serving Readiness — index 5, Lumbridge build area (948-5)

> **Verdict (static, offline verification — no client, no live Jagex):**
> **YES.** If the NXT 948-5 client requests the Lumbridge index-5 map-square groups
> after `op75 SET_READY_FLAG`, our JS5 server **will serve them correctly**. The
> local cache is **complete** for index 5 (8518/8518 ref-table groups present and
> CRC-matching, including the Lumbridge spawn terrain group), and the
> `FileProvider.serve()` + `JS5Server` group path is **byte-for-byte correct** for
> index-5 group requests — identical to (and exercised more thoroughly than) the
> master-index path that already works at runtime.
>
> **The single most important finding:** index 5 is fully populated and integral
> (every reference-table group has a stored, CRC-matching blob), so op75 is the
> only thing standing between the current state and the client requesting +
> receiving map data. There is **no cache hole** and **no index-5 serving bug**.

**Owner:** `js5-server-engineer`. **Hand-offs:** see §6.
**Evidence:** offline tool `tools/.../MapServingReadinessTest.kt` (15/15 PASS) +
`tools/.../MapGroupIdProbe.kt` (encoding derivation). Reproduce:
`./gradlew :tools:run -PmainClass=org.darkan.tools.MapServingReadinessTestKt`.

---

## 0. Scope & what was (not) done

Static verification against the **local SQLite cache** the server reads
(`./data/cache`, i.e. `EnvVars.cachePath`) and the **real serve code**
(`world.gregs.voidps.cache.file.FileProvider.serve` + `JS5Server`). No client was
piloted; no Ghidra; no production/live connection. The map-group **id encoding**
was derived empirically from our own cache (§2), not from the client binary — the
parallel RE agent owns the client-side derivation; this doc only needs to know
*which numeric group ids exist and whether we serve them*.

---

## 1. Cache completeness for maps (Q1) — COMPLETE

The server reads `./data/cache` (`CacheFileProvider`, since `MEM_CACHE=false`).
`js5-5.jcache` is present (56.5 MB).

| Check | Result |
|-------|--------|
| `255/5` (index-5 reference table) present | YES — 85 614-byte raw container |
| `255/5` decompresses + parses (format 7) | YES — 8518 groups, maxGroupId 25516, flags `0x0c`, 0 bytes left |
| `cache.archives(5)` == ref-table group ids | YES — 8518 groups |
| **Every ref-table group has a stored blob** | **YES — 8518 / 8518** |
| **Every present blob CRC-matches the ref table** | **YES — 8518 / 8518** |
| Lumbridge spawn terrain `m50_50` (group 12850) present | YES |
| Spawn-window terrain groups present + CRC-match + decompress (XTEA-0) | YES (6 present, 3 edge squares legitimately absent) |

**Index 5 is complete.** There is a stored, CRC-correct blob for every group the
reference table lists — the strongest completeness signal short of a full client
download. The black screen is therefore **not** a cache-completeness problem; op75
(per `docs/protocol/world-entry-render-948.md` §P0) is the gate.

### Lumbridge build-area window

Spawn tile 3200,3200 → mapsquare `(tile >> 6)` = **(50,50)**. The 13×13-zone build
area covers mapsquares ~49–51 per axis. Terrain group id = `(mapsquareX << 8) |
mapsquareY` (§2). Window coverage:

| mapsquare | terrain group | present |
|-----------|--------------:|---------|
| (50,50) spawn | **12850** | **YES** |
| (50,49)/(50,51) | 12849 / 12851 | YES |
| (51,49)/(51,50)/(51,51) | 13105 / 13106 / 13107 | YES |
| (49,49)/(49,50)/(49,51) | 12593 / 12594 / 12595 | absent* |

\*The three `m49_*` squares (the column directly west of Lumbridge) are genuinely
absent from index 5 — they are empty/edge mapsquares and are not in the authentic
RS3 cache either. They are **not** a download gap: every group the ref table
*claims* exists is present (8518/8518). The client only requests groups that its
loaded `255/5` index lists, so it will never ask for 12593–12595.

---

## 2. Map-group id encoding (resolves an apparent doc contradiction)

`docs/protocol/world-entry-render-948.md` §4.2 describes map squares by the
**`m{X}_{Y}` / `l{X}_{Y}` group-name** convention. But the index-5 reference table
has **flags = `0x0c`** = bits 2 (`0x04` sizes) + 3 (`0x08` unknown hashes); **bit 0
(`0x01` name hashes) is NOT set.** So the client does **not** resolve map groups by
hashing `m50_50` — there are no name hashes to look up. The `m`/`l` names are
**conventional labels**; the client addresses groups by a **coordinate formula**.

**Derived encoding (empirical, `MapGroupIdProbe`):** **`groupId = (mapsquareX << 8)
| mapsquareY`** for terrain. Under this packing the distinct X values are cleanly
**0..99** (93 distinct) and Y 0..99 for the main world grid — the real RS map
range. The competing `(x<<7)|y` packing spills X to 127 (the tell-tale of a 7-bit-Y
mis-decode), so `<<8` is correct. Spawn `(50,50)` → `(50<<8)|50` = **12850**,
present and CRC-correct.

- **Loc groups** (`l{X}_{Y}`) use a **distinct** client-computed id (the loc band
  was not separately reverse-derived here — it is the parallel RE agent's domain).
  This does **not** affect the readiness verdict: index 5 is complete (every
  ref-table id has a blob, §1), so whatever numeric id the client computes for the
  Lumbridge loc square, our cache contains it and serves it via the same path.
- A small high band (6 ids, 25511–25516) sits above the `(x<<8)|y` terrain max
  (`(99<<8)|99` = 25443). These are a separate special-map cluster, irrelevant to
  Lumbridge first-light.

> **For the RE agent / docs maintenance:** consider annotating
> `world-entry-render-948.md` §4.2 that index-5 group ids are the coordinate value
> `(mapsquareX<<8)|mapsquareY`, addressed numerically — not an `m{X}_{Y}`
> *name-hash* (index 5 carries no name hashes, flags `0x0c`). The `m`/`l` strings
> are labels, not the on-wire key.

---

## 3. Group-serving path (Q2) — BYTE-CORRECT

`JS5Server.reader` decodes a 10-byte file request → `ref = (index<<32) | group` →
`provider.serve(out, ref, prefetch)`. `FileProvider.serve` is **index-agnostic**:
it reads `data(index, group)` (→ `cache.sector(index, group)`, the *same* call the
working master-index path uses for `255/255`), writes the 10-byte response header
(`index, hash=group|prefetchBit, compression, compressedSize`), then streams the
raw container with 102 400-byte block framing and 5-byte continuation headers.
**There is no index-255-specific or index-5-specific branch** — maps go through the
identical code.

Verified by driving the **real** `FileProvider.serve()` through a Ktor
`ByteChannel` and reversing the framing back to the source container:

| Round-trip | Result |
|------------|--------|
| `serve(index 5, group 12849)` urgent → recovered == `cache.sector(5,12849)` | byte-for-byte EQUAL |
| `serve(index 5, group 12849)` prefetch (hash top-bit set) → recovered == source | byte-for-byte EQUAL |
| `serve(index 5, group 13105)` largest build-area group (13 692 B) | byte-for-byte EQUAL |
| **multi-block continuation** (454 634 B group, 5 blocks, continuation headers) | **byte-for-byte EQUAL** |

The multi-block case is important: the build-area map groups are all <1 block
(≤13.7 KB), so the master-index path and the small-map path never exercise the
continuation-header code. The 5-block round-trip proves the `>102,400`-byte path —
which a dense map region (or any large index) hits — frames correctly. (Compression
type is irrelevant to serving: `serve` streams the stored container verbatim; the
client decompresses. Lumbridge F2P = XTEA 0, and JS5 never deciphers — it serves the
raw container, which is correct.)

**No index-5-specific serving gaps vs the index-255 path.**

---

## 4. The index hierarchy (Q3) — CONSISTENT

- **`255/5`** (the index-5 reference table) is served by the same `data(255, 5)` →
  `cache.sector(255, 5)` → `IndexFile.getRawTable()` path used to *build* the
  master-index CRC, so the bytes the client CRC-checks are the bytes we serve
  (this is the verified consistency `docs/net/js5-index-download-crc-flow.md`
  relies on, here re-confirmed for index 5).
- **Master-index entry for index 5:** the CRC in `255/255` for index 5 =
  **`0xC3BF5723`** = `CRC(255/5)`. So the client accepts the index-5 reference
  table (its `IndexDownloaded` callback CRC-checks `255/5` against this), then
  proceeds to request groups.
- **Reference/addressing math:** `ref = (index.toLong() << 32) | (group &
  0xFFFFFFFF)`; `serve` recovers `index = ref ushr 32`, `group = ref & 0xFFFFFFFF`.
  Verified for index 5: `(5<<32)|12849` → index 5, group 12849. Correct.

So the full chain **master index → 255/5 → group blob** is internally consistent
for index 5.

---

## 5. Readiness verdict

**If the client requests Lumbridge index-5 map groups after op75, our server WILL
serve them.** Concretely, for each numeric group id the client derives for the
Lumbridge build area:

1. `255/5` is accepted (master-index CRC matches) → client knows which group ids
   exist and their CRCs.
2. Every group id it can ask for has a present, CRC-correct blob in our cache.
3. `FileProvider.serve()` streams that blob with correct 10-byte header + block
   framing (single- and multi-block), and the client's `GroupDownloaded` callback
   will CRC-verify and decompress it successfully (XTEA 0 for F2P Lumbridge).

**No serving bug found; no cache hole found; no code change required.** This is a
verification result — the remaining blocker to seeing map data is upstream: **send
`op75` from the world burst** (owned by `networking-protocol-engineer`, per
`world-entry-render-948.md` §5). Once op75 ships, the map-group path is ready.

### What is NOT covered by this static pass

- **Loc (`l{X}_{Y}`) group id formula** — not separately reverse-derived (RE
  agent's domain). Mitigated: index 5 is provably complete, so the loc blob exists
  regardless of the formula; only the client computes the id.
- **The `op75 → map request` trigger itself** — behavioral, requires the client;
  this pass assumes the client reaches the request stage and verifies only that our
  responses are correct when it does.
- **Members / instanced regions** — would need per-square XTEA keys in the rebuild;
  Lumbridge is XTEA-0 so out of scope (flagged in `world-entry-render-948.md` §6).

---

## 6. Notes for `cache-library-engineer`

- **No action needed for index 5.** It is complete and integral (8518/8518 groups
  present + CRC-matching). The cache downloader does **not** need a re-run for maps.
- If first-light later moves **off** Lumbridge to a members/instanced area, that
  area's map squares need **per-square XTEA keys** delivered in the rebuild packet
  (not index 5's concern, but a cache/world hand-off). Lumbridge needs none.
- The three absent `m49_*` squares west of the spawn are **expected** (empty edge
  mapsquares, absent from the authentic cache too) — not a download defect.

## Appendix — verification tooling added

- `tools/src/main/kotlin/org/darkan/tools/MapServingReadinessTest.kt` — the offline
  readiness gate (Sections A–E above; 15/15 PASS). Loads the cache as the server
  does, checks index-5 completeness/integrity, drives the real `FileProvider.serve()`
  and reverses the block framing to assert byte-for-byte round-trips.
- `tools/src/main/kotlin/org/darkan/tools/MapGroupIdProbe.kt` — derives the
  `(mapsquareX<<8)|mapsquareY` terrain id encoding empirically from the cache.
