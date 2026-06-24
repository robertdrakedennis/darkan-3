# Alerion Build-Area / Scene-Rebuild — Architectural Reference

> **910-era reference, NOT authoritative for 948-5.**
> This document captures the *service architecture* (class shape, abstractions, lifecycle,
> control flow) of how the [alerion](file:///Users/robert/projects/alerion) RS private server
> models a "build area" and drives scene rebuilds. Alerion targets revision **910**.
> darkan3 targets **948-5**, and a great deal changed between them.
>
> **Use this ONLY for shape.** Every byte format, opcode, packed-coordinate layout, bit width,
> and encoding shown below is **910 and will be wrong for 948-5**. The authoritative 948-5 wire
> formats come from the parallel ghidra RE of the 948-5 NXT binary — see
> [`world-bootstrap-948.md`](./world-bootstrap-948.md),
> [`world-entry-render-948.md`](./world-entry-render-948.md), and
> [`lobby-world-switch-948.md`](./lobby-world-switch-948.md). Where this doc and the 948 docs
> disagree on bytes, the 948 docs win, always.

---

## TL;DR — the abstractions worth borrowing

1. **A `BuildAreaSize` value object** — a tiny enum-like class mapping a client-selected `id` →
   a square scene `size` (in tiles). The client picks the size; the server honours it. The build
   area is a *square of `size × size` tiles* centered (roughly) on the player.
2. **The build-area state lives on the player** (not a standalone `BuildArea` object in 910) —
   a cluster of "last loaded scene" fields plus a `Set` of loaded map-square ids.
3. **A position → build-area computation** (`loadMapRegions`) that, from the player's current
   tile + `BuildAreaSize`, derives: the set of map squares the scene covers, the scene's
   base/origin chunk, and the "anchor" snapshot used for the next staleness check. It then emits
   the rebuild packet.
4. **A "did the player leave the area, rebuild?" check** (`needMapUpdate`) run every tick,
   comparing the player's current chunk against the chunk at last rebuild against a
   `sceneRadius` threshold — a hysteresis band so the rebuild fires *before* the player walks off
   the loaded edge, not after.
5. **A two-phase client handshake gate** (`clientLoadedMapRegion`): server sends rebuild →
   client builds the scene and acks (`MAP_BUILD_COMPLETE`) → only then does the server stream
   player/npc info and zone updates. Most game systems are gated on this flag.

darkan3 already has analogues started — `world/.../world/Viewport.kt` (`buildAreaSize`,
`buildAreaChunkX/Y`) and `world/.../net/ZoneBundleBuilder.kt` mirror the same half-area windowing
math. The patterns below are the fuller version of that.

---

## 1. `BuildAreaSize` — the size value object

**File:** [`server/src/jagex/core/constants/BuidlAreaSize.ts`](file:///Users/robert/projects/alerion/server/src/jagex/core/constants/BuidlAreaSize.ts)
(filename is misspelled "Buidl" in alerion — that is the real import path.)

Shape (910):

```ts
class BuildAreaSize {
  static readonly SIZE_104 = new BuildAreaSize(0, 104);   // default
  static readonly SIZE_120 = new BuildAreaSize(1, 120);
  static readonly SIZE_136 = new BuildAreaSize(2, 136);
  static readonly SIZE_168 = new BuildAreaSize(3, 168);
  static readonly SIZE_72  = new BuildAreaSize(4, 72);
  static readonly SIZE_256 = new BuildAreaSize(5, 256);
  readonly id: number;    // what the client sends in its detail-options packet
  readonly size: number;  // scene edge length in tiles
  static buildAreaSizeForId(id): BuildAreaSize | null { ... }
}
```

**Why it's worth modeling:** it cleanly separates *"which scene size did the client request"*
(an id off the wire) from *"how big is the scene in tiles"* (the math input). The default is the
104-tile (13-chunk) area. The client tells the server its choice via a detail-options /
preferences packet; the server stores it on the player and recomputes the build area
([`World.ts:3397-3407`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts)) —
when the size changes, it forces a rebuild.

> **DO NOT COPY:** the `id → size` table values and the set of supported sizes are 910. 948-5
> may support a different size set / different ids. Pull the real table from the 948-5 binary
> (the `BuildAreaSize` enum / `CLIENT_DETAILOPTIONS` handler).

---

## 2. Build-area state — fields on the player

**File:** [`server/src/lostcity/entity/Player.ts`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)

The build area in 910 is **not a class** — it's a field cluster on `Player`. The fields that
matter for the architecture:

| Field | Line | Role |
|---|---|---|
| `buildAreaSize: BuildAreaSize` | `934` | current scene size (defaults `SIZE_104`) |
| `mapRegions: Set<number>` | (set in `loadMapRegions`) | the map-square ids currently covered by the scene — the **authoritative "what is loaded"** set |
| `lastLoadedSceneLevel/X/Z` | `1870-1872` | the player tile at the *last* rebuild — the anchor for staleness checks |
| `lastRegionKey` | `1873` | coarse 64-tile region key at last rebuild (alt staleness signal) |
| `sceneBaseChunkX/Z` | `1876-1877` | the scene's origin chunk (SW corner) — used by REBUILD_REGION + zone streaming |
| `clientLoadedMapRegion: boolean` | `942` | the handshake gate (see §5) |
| `forceNextMapLoadRefresh: boolean` | — | one-shot "rebuild next tick regardless" flag |

> **Borrow:** the *set* of these fields is the right state to track. A cleaner design (and what
> darkan3 may prefer) is to hoist them into a dedicated `BuildArea` class owned by the player —
> 910 just inlines them. darkan3's `Viewport.kt` already does this partially
> (`buildAreaChunkX/Y`, `buildAreaSize`).
>
> **DO NOT COPY:** `getRegionKey()`'s packing `(x>>6) | ((z>>6)<<7)` and
> `packClientRegionId(rx,rz) = ((rx&0xff)<<8)|(rz&0xff)` are 910 map-square id encodings. 948-5
> map-square / zone ids must come from the 948 RE. (Note 910 even distinguishes
> `packClientRegionId` vs `packMapSquareGroupId` = `rx | (rz<<7)` — see
> [`MapSquare.ts:7-19`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/MapSquare.ts);
> these differ and both are revision-specific.)

---

## 3. Position → build-area computation (`loadMapRegions`)

**File:** [`Player.ts:1854-1903`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)

This is the central "compute a build area from a player position and (re)send the rebuild"
routine. Architecturally it does, in order:

1. **Store the chosen `buildAreaSize`** and **clear `mapRegions`**.
2. **Derive the covered map squares** from the player chunk (`x>>3`, `z>>3`) and a
   `mapHash = size >> 4` half-extent (in map-square units). It iterates a rectangle of
   region coords `[(chunkX-mapHash)/8 .. (chunkX+mapHash)/8] × [same for Z]`, clamped at 0, and
   adds each `packClientRegionId(rx,rz)` to `mapRegions`. → *this Set is what every "is tile X
   loaded for this player" check consults.*
3. **Snapshot the anchor**: `lastLoadedSceneLevel/X/Z = current`, `lastRegionKey = getRegionKey()`.
   → consumed by `needMapUpdate` next tick.
4. **Compute the scene origin chunk**: `sceneChunksRadius = floor(size/16)`;
   `sceneBaseChunkX/Z = max(0, chunk - sceneChunksRadius)`. → the SW corner the client's scene
   grid is laid out from; used when streaming per-zone data.
5. **Reset the handshake gate** (`setClientHasntLoadedMapRegion()`) and re-init the player
   viewport (`viewport.initLocalPlayerView`).
6. **Emit the rebuild packet** (`REBUILD_NORMAL`), but only if there's a client and not in the
   lobby map state.

There's a parallel copy for **dynamic/instanced** maps,
[`World.ts:2639-2668 loadDynamicMapRegions`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts),
which does the identical map-square + base-chunk math but emits `REBUILD_REGION` (the
constructed-scene variant that carries a per-chunk source-coordinate grid) instead of
`REBUILD_NORMAL`. **Borrow the split:** *normal* world scene vs *region/constructed* scene are
two rebuild flavours sharing one build-area computation.

> **Borrow:** the five-step shape (store size → derive covered squares → snapshot anchor →
> compute origin chunk → reset gate + emit). Especially the idea that **one function both
> computes the build area and (re)sends the rebuild**, keeping `mapRegions` and the wire in sync.
>
> **DO NOT COPY:** `mapHash = size>>4`, `sceneChunksRadius = floor(size/16)`, the `/8` region
> divisions, the clamp-at-0, and the region-id packing — all 910 scene geometry. 948-5 uses zone
> (8-tile) granularity with its own packed-coord scheme (`BuildArea::DecodePackedCoord`); get the
> exact extents and packing from the 948 RE.

---

## 4. "Did the player leave the area?" → rebuild check (`needMapUpdate`)

**File:** [`Player.ts:1905-1921`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)

Run every tick inside `cycle()`
([`Player.ts:1755`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)):

```ts
if ((this.teleported && !this.pendingViewportTeleportUpdate) || this.needMapUpdate(...)) {
    this.loadMapRegions(...);
}
```

`needMapUpdate` returns true when **any** of:
- `forceNextMapLoadRefresh` is set (one-shot override), OR
- the player changed plane (`level !== lastLoadedSceneLevel`), OR
- the player's chunk drifted from the last-rebuild chunk by `>= sceneRadius` on either axis,
  where `sceneRadius = floor((size>>3)/2) - 1`, OR
- (if the client supports it) the coarse `regionKey` changed.

**The key architectural idea — hysteresis / threshold rebuild.** The scene covers `size` tiles
but the rebuild fires when the player gets within `sceneRadius` chunks of having walked off the
*loaded* edge — i.e. *before* the player reaches the boundary, so the new scene is built while
there's still loaded map under them. This is the "rebuild on zone-boundary crossing" behaviour,
generalized to "crossing into the margin band of the current scene." darkan3's 948 work calls
this out too — the rebuild must re-fire as the player approaches the edge of the built area.

> **Borrow:** the *structure* of the staleness check — (force) ∨ (plane change) ∨
> (chunk-delta ≥ threshold) ∨ (coarse-region change) — and the hysteresis principle (rebuild on
> entering the margin, not on leaving the area). This is the canonical "when do I re-send the
> rebuild" predicate.
>
> **DO NOT COPY:** the literal `sceneRadius = floor((size>>3)/2)-1` formula and the chunk (`>>3`)
> granularity — 910 numbers. 948-5 zone math + the exact margin threshold come from the 948 RE.

---

## 5. The server → client rebuild flow (lifecycle + handshake gate)

The most important architectural pattern: **a rebuild is a request/ack round-trip, and almost
everything is gated on the ack.**

```
                         server                                  client
                           │                                       │
  loadMapRegions(): ───────┤  REBUILD_NORMAL  (scene origin,       │
   - clear+fill mapRegions │ ───────────────► size, region count)  │ build the scene from
   - snapshot anchor       │                                       │ JS5 map squares
   - sceneBaseChunk        │  clientLoadedMapRegion = FALSE        │
   - clientLoadedMapRegion │  ◄── (gate closed) ──                 │
        = false            │                                       │
                           │              MAP_BUILD_COMPLETE       │ scene built & rendered
   setClientHasLoaded ─────┤ ◄─────────────────────────────────── │
   MapRegion():            │                                       │
    gate = TRUE            │  now stream PLAYER_INFO / NPC_INFO /   │
                           │  zone updates / ground items ───────► │
```

- **Gate set false** on every `loadMapRegions` (`setClientHasntLoadedMapRegion`,
  [`Player.ts:1879`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)).
- **Gate set true** when the client acks: `ClientProt.MAP_BUILD_COMPLETE` →
  `setClientHasLoadedMapRegion()`
  ([`World.ts:3365-3371`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts)).
  On ack it also flushes visible ground items and sends a varc ack.
- **Everything is gated on the flag.** `updatePlayers()` (which sends PLAYER_INFO + NPC_INFO)
  early-returns unless `clientLoadedMapRegion`
  ([`Player.ts:1778-1783`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts));
  `processMovement` is frozen while not loaded; ground-item / loc / chat broadcasts all check the
  flag (dozens of call sites in `World.ts`); the viewport's add/remove of other players is gated
  on *both* parties' flags
  ([`Viewport.ts:59-65`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Viewport.ts)).
- There's also a **stuck path**: if the client can't finish the build it sends
  `MAP_BUILD_STUCK` with JS5 diagnostics
  ([`World.ts:3373+`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts)) —
  worth having a handler so a failed scene build is observable rather than a silent hang.

**Tick ordering** ([`World.ts:4148-4220`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts)):
decode inbound → per-player `cycle()` (movement + `needMapUpdate`→`loadMapRegions` may fire the
rebuild) → npc AI → **then** per-player `updatePlayers()` (PLAYER_INFO/NPC_INFO, skipped if gate
closed) → `finishViewportTeleportUpdate()`. So the rebuild decision happens *before* the info
packets in the same tick, and the info packets simply no-op until the client acks a later tick.

**Teleport vs walk re-anchoring nuance (worth knowing, not copying):** 910 carefully handles the
fact that a login-style rebuild (`nearbyPlayers=true`) carries the player's **absolute** position
(self-re-anchoring), whereas an in-game rebuild carries no absolute position — so for the latter
it stashes a `pendingSelfMovementAnchor` / `pendingViewportTeleportUpdate` so the first PLAYER_INFO
after the ack delivers the move exactly once as a teleport delta
([`Player.ts:1883-1902`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Player.ts)).
The *concept* (a rebuild interrupts self-movement delivery, so you must re-anchor the client's
believed position exactly once afterward) is real and transfers; the mechanism is 910-specific.

> **Borrow:** the request/ack gate (`clientLoadedMapRegion`), gating PLAYER_INFO/NPC_INFO/zone
> streaming on the ack, the rebuild-before-info tick ordering, and having an explicit
> "build stuck" handler. This is the single most load-bearing pattern.
>
> **DO NOT COPY:** the opcodes (`REBUILD_NORMAL=88`, `MAP_BUILD_COMPLETE`, `MAP_BUILD_STUCK`),
> the ack payload (`g4()`), and the post-ack packet set. 948-5's render gate is **op81
> REBUILD_NORMAL_SIMPLE → op75 render-ready**, documented in
> [`world-entry-render-948.md`](./world-entry-render-948.md) and
> [`world-bootstrap-948.md`](./world-bootstrap-948.md) — use those, not 910's op88/MBC.

---

## 6. How the build area relates to map-square loading

The build area is the **gating set for all spatial data**, not just the rebuild packet:

- `mapRegions: Set<mapSquareId>` (filled in §3) is the **single source of truth** for "is this
  tile's map square loaded for this player." Ground-item reveals, loc spawns, zone updates, and
  player visibility all test `player.mapRegions.has(packClientRegionIdForTile(x,z))`
  (e.g. [`World.ts:610,670,1017,1022,1269`](file:///Users/robert/projects/alerion/server/src/lostcity/engine/World.ts),
  [`Viewport.ts:60,64`](file:///Users/robert/projects/alerion/server/src/lostcity/entity/Viewport.ts)).
- `sceneBaseChunkX/Z` is the **origin** the per-zone stream is laid out from (the constructed-map
  `REBUILD_REGION` walks `chunks × chunks` from this base;
  [`ServerProt.ts:1134-1153`](file:///Users/robert/projects/alerion/server/src/jagex/network/protocol/ServerProt.ts)).
- The rebuild packet itself carries a **region/square count**
  (`getNormalRegionSquareCount`, [`ServerProt.ts:1081-1088`](file:///Users/robert/projects/alerion/server/src/jagex/network/protocol/ServerProt.ts)),
  computed from the same `mapHash` rectangle — i.e. the packet tells the client *how many* map
  squares to expect, derived from the same build-area geometry. (In 910 `REBUILD_NORMAL` carries
  only the count + origin + size + force flag; the client then JS5-pulls each square itself. The
  per-square XTEA keys are sent separately. **948-5 differs — see the 948 docs.**)

darkan3's [`ZoneBundleBuilder.kt`](file:///Users/robert/projects/darkan3-server/world/src/main/kotlin/org/darkan/world/net/ZoneBundleBuilder.kt)
already mirrors this: it windows a 13×13-chunk viewport around `buildAreaChunkX/Y ± halfArea` and
emits per-chunk relative offsets — the same "build area defines the chunk window, stream per-zone
within it" shape.

> **Borrow:** make the covered-map-square set the *one* authoritative spatial gate that all
> systems query; derive both the rebuild's square count and the per-zone stream window from the
> same build-area computation; track a scene origin chunk for relative addressing.
>
> **DO NOT COPY:** `getNormalRegionSquareCount`'s `/8` math, the count/origin/size/force wire
> layout of `REBUILD_NORMAL`, `packMapSquareGroupId` vs `packClientRegionId`, and the
> "client JS5-pulls squares itself" assumption. 948-5 build-area packing, zone granularity, and
> what the rebuild carries vs. what's streamed separately must come from the 948 RE
> (`BuildArea::DecodePackedCoord`, op81 payload).

---

## Client-side concepts (910-era, concepts ONLY — do not lift bytes)

For orientation on *what the client does* with a rebuild (so the server's job makes sense). All
of this is 910 and the byte/field details are wrong for 948-5; treat as conceptual background.

- **`BuildAreaSize.java`**
  ([`client/.../com/jagex/core/constants/BuildAreaSize.java`](file:///Users/robert/projects/alerion/client/client/src/main/java/com/jagex/core/constants/BuildAreaSize.java)
  and `com/jagex/game/world/BuildAreaSize.java`) — the client's mirror of the size enum; the
  client *chooses* its size and reports the id to the server. The server-side §1 class mirrors
  this.
- **`SceneManager.java`**
  ([`client/.../com/jagex/game/world/entity/SceneManager.java`](file:///Users/robert/projects/alerion/client/client/src/main/java/com/jagex/game/world/entity/SceneManager.java))
  — on receiving a rebuild, the client (re)allocates its scene/build-area structure of
  `size × size` tiles, computes which map squares it needs, JS5-fetches+decodes them, places locs,
  and when fully built fires the MAP_BUILD_COMPLETE ack. This is the client half of the §5 gate.
- **`PacketReader.java`**
  ([`client/.../rs2/client/PacketReader.java`](file:///Users/robert/projects/alerion/client/client/src/main/java/rs2/client/PacketReader.java))
  — decodes the rebuild packet fields. **Its field order/sizes are 910 and must NOT be lifted;**
  the 948-5 decode is in the binary (the ghidra agent's `BuildArea::DecodePackedCoord` + op81
  handler), captured in the 948 docs.

> **DO NOT COPY** anything byte-level from these client classes. They are useful only to
> understand *why* the server sends a rebuild and *what acks it* — the conceptual contract, not
> the wire format.

---

## Consolidated DO-NOT-COPY list (948-5 must come from the ghidra RE)

Everything in this list is **910** and will be wrong for darkan3's 948-5 target. Source of truth =
the parallel 948-5 ghidra RE + the existing `docs/protocol/*-948.md` files.

1. **Opcodes** — `REBUILD_NORMAL = 88`, `REBUILD_REGION`, `MAP_BUILD_COMPLETE`,
   `MAP_BUILD_STUCK`, the post-ack packet set. *(948-5: op81 REBUILD_NORMAL_SIMPLE render path,
   op75 render-ready — see `world-entry-render-948.md`.)*
2. **Packed-coordinate encodings** — `getRegionKey()` `(x>>6)|((z>>6)<<7)`;
   `packClientRegionId(rx,rz)` `((rx&0xff)<<8)|(rz&0xff)`; `packMapSquareGroupId` `rx|(rz<<7)`;
   the 30-bit `((level&3)<<28)|((x&0x3fff)<<14)|(z&0x3fff)` self-coord; the 18-bit per-player
   region hashes; the 26-bit constructed-chunk source-coord packing.
   *(948-5: use `BuildArea::DecodePackedCoord`.)*
3. **Scene geometry constants** — `mapHash = size>>4`, `sceneChunksRadius = floor(size/16)`,
   `sceneRadius = floor((size>>3)/2)-1`, the `/8` region divisions, chunk = `>>3` granularity.
   *(948-5 uses zone granularity with its own extents.)*
4. **`BuildAreaSize` id→size table** — `{0:104, 1:120, 2:136, 3:168, 4:72, 5:256}` and the
   supported-size set. *(948-5 table from the binary's enum + detail-options handler.)*
5. **Rebuild packet payload layout** — `REBUILD_NORMAL`'s count/origin/size/force fields and bit
   ordering (`p2_alt2`, `p1_alt3`, etc.); `REBUILD_REGION`'s per-chunk bit grid; the
   `MAP_BUILD_COMPLETE` `g4()` ack body. *(948-5: op81 payload per `world-bootstrap-948.md`.)*
6. **"Client JS5-pulls each square; XTEA keys sent separately"** — the data-delivery split is
   revision-specific. Confirm what 948-5's rebuild carries inline vs. streams vs. expects the
   client to fetch, from the 948 RE.

## Consolidated DO-BORROW list (architecture, revision-agnostic)

1. A `BuildAreaSize` value object: client-chosen `id` → scene `size`; default size; lookup-by-id;
   recompute + force-rebuild when it changes.
2. A build-area state cluster (ideally a `BuildArea` class on the player): the covered
   map-square `Set`, the last-rebuild anchor (level/x/z + coarse region key), the scene origin
   chunk, and the load gate.
3. One `loadMapRegions`-style routine that **both** computes the build area (covered squares +
   origin chunk + anchor) **and** (re)sends the rebuild, keeping state and wire in sync.
4. A `needMapUpdate`-style per-tick predicate: (force) ∨ (plane change) ∨ (chunk-delta ≥ margin
   threshold) ∨ (coarse-region change), with **hysteresis** (rebuild on entering the margin band,
   before walking off the loaded edge).
5. A request/ack **render gate** (`clientLoadedMapRegion`): close it on rebuild, open it on the
   client's build-complete ack, and gate PLAYER_INFO / NPC_INFO / zone streaming / spatial
   broadcasts on it. Order the tick so the rebuild decision precedes the info packets.
6. Make the covered-square `Set` the **single authoritative spatial gate** every system queries;
   derive the rebuild's square count and the per-zone stream window from the same computation;
   keep a scene origin chunk for relative zone addressing.
7. Two rebuild flavours over one build-area computation: a **normal** world scene vs a
   **region/constructed (instanced)** scene.
8. An explicit **build-stuck** handler so a failed client scene build is observable, not a silent
   hang.
