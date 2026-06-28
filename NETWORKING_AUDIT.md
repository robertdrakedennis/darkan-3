# Darkan-3 Networking Layer — Architecture & Quality Audit

**Date:** 2026-06-28
**Scope:** The full server networking stack — `org.darkan.core.net` (protocol, codec, session, login, JS5, update masks), the world entity-sync + scene pipeline (`org.darkan.world.net`, `org.darkan.world.world`), the lobby/world server loops, and the C2S handler layer.
**Lens:** A *faithful* RS3 NXT (948-5) private server that must be byte-exact on the wire **and** clean, extensible, and professional — the quality bar of a mature emulator (Apollo / Matrix / RSMod), not "it works."
**Method:** Lead read the protocol spine first-hand (Codec, Session, ServerProt, the update-mask framework, both info builders, the revision wiring); four parallel read-only deep-dives covered login/crypto, session/tick/scene, the revision/codec system, and the handler/JS5 layer. Every finding is cited `file:line`.

---

## 1. Verdict

**The byte/correctness layer is genuinely professional. The architecture layer is pervasively single-player-MVP-shaped, and the wire-format truth is duplicated across multiple hand-synced sites.** This is not a mess — it's a well-engineered prototype at the exact inflection point where scaffolding must become architecture. The good news: **the cleanup and the next feature slice (movement, multiple players, NPCs, map/minimap) are the same work.** You cannot add those cleanly onto the current foundation, so the refactor is not a detour — it's the prerequisite.

Three meta-themes unify every slice's findings.

---

## 2. The three meta-themes (the synthesis)

### Theme A — Single-player scaffolding masquerading as architecture
The server runs one player beautifully by doing things that cannot scale to two:

- **The world tick blocks on serial per-player socket flushes.** `WorldTick.runTick` calls `session.flushBlocking()` (`Session.kt:114` → `runBlocking { flush() }`) inside `Players.forEach` — the entire world tick blocks on each client's socket flush, one at a time, on one thread, contending `writeMutex`. One slow client stalls *every* player's sync for that tick. This is the headline scaling defect.
- **World entry is a 140-line inline send-script**, not a reusable pipeline (`WorldServer.initWorldLogin` steps 11–17). There is no `rebuild(player, tile)` to call on teleport / respawn / re-entry — adding a second trigger means copy-pasting the block.
- **"First light" is a frozen capture.** `sendInitialStats` hardcodes 28 stats (`if (i==3) UpdateStat(i,1154,10)`), `sendInitialInventories` hardcodes magic item ids, `spawnWornEquipment` is a single bronze dagger, and `FirstLightSceneBootstrap` is ~40 `ObjAdd`/`LocAdd` records replayed from one Lumbridge recording. None of it is derived from world state.
- **The tick is one-directional.** `player.tile` is never reassigned anywhere in `world/`; there is no movement/input loop; world handlers `logInfo`-ack input and drop it. `movementType=0`/`skipMode=0` are hardcoded literals (`PlayerInfoBuilder.kt:401,315`) because *nothing produces movement*.
- **Visibility is "everyone."** `Viewport.resetAfterGpiPrefix` seeds all 2047 slots into `lowResIndices` every login (`Viewport.kt:90`); `Npcs` iterates a global `ConcurrentHashMap` with no spatial index and never recycles indices (`Npcs.kt:16,39`).
- **Lifecycle is scattered booleans.** `disconnected` / `readyForTick` / `lobbyWorldSwitchSent` / map membership — there is no state machine. `Session.State` exists but is *set and never read for control flow*.

### Theme B — Wire-format truth duplicated across hand-synced sites
For a byte-exact protocol, the single most dangerous pattern is the same fact transcribed in N places that can silently drift — and drift here has **already shipped client-facing bugs** (op80 was mislabeled `UPDATE_RUNENERGY` while the handler disagreed; the comment at `Rev948ServerProtStubs.kt:128` is the post-mortem).

- **A packet's opcode↔name↔size↔encoder lives in 3–4 places**: the `ServerProt` data class, the encoder registration, the stub table, and (948) the decoder table. Nothing enforces agreement; `Codec.kt` comments literally describe drift-management ("encoders win over stubs").
- **The login block is transcribed three times** — `LoginServer.handleLogin`, `WorldServer.initWorldLogin`, and re-encoded a third time in `writeWorldLoginResponse` — with **two independent username decoders** and **two XTEA call paths** that must stay byte-identical or the lobby-issued username won't match the world's join key.
- **Opcodes are magic literals at 116 server + 20 client call sites** (rev948), with no named constants and no compiler help on a renumber.
- **The per-revision split is duplication without isolation.** ~4 files are pure verbatim copy-paste (PlayerInfo, NpcInfo, the two stub helpers); the genuinely-divergent files are isolated by *file copy*, not a shared base they override.
- **Two registration mechanisms** with different idioms: decode is explicit + opcode-keyed + in-file (good); handlers are classpath-scanned + bound by `genericInterfaces[0]` reflection (implicit, package-keyed, silently fails).

### Theme C — Dead / known-wrong code shipped beside live code with no signal
The codebase carries things that don't work or don't run, indistinguishable from things that do:

- **The entire `rev947` package is dead code** — `register947()` has zero callers (lobby pins `Codec.get(948)`, `EnvVars.majorVersion` defaults `948`). ~2,107 LOC across 16 files, never executed, silently drifting out of sync (still says APPEARANCE bit 2, op27).
- **Six 948 mask encoders are registered with a wire format the file itself documents as guaranteed-desync** (`Rev948ServerCodecsUpdateMasks.kt:57–67` — the `sByte`/`sShort` retired-mode-prefix). Latent only because nothing sends them on the first-light path yet.
- **`handleBlocking` / `getHandler` are dead** (`PacketHandlers.kt:48,71`); `RECONNECT` is documented as a state-machine branch but `initWorldLogin` treats it identically to LOGIN and never reads the fields that distinguish it (`sessionId1/2` are dead columns).
- **No-op handlers are indistinguishable from unimplemented ones** — `= Unit` vs `logInfo`-only vs genuinely-terminal, with no marker; the lobby even duplicates the world's anti-cheat/map handlers as empty stubs that conceptually shadow real behavior.

---

## 3. What's genuinely good (preserve — do not throw away in the refactor)

- **ISAAC write serialization (`writeMutex`)** — every opcode+length+payload+`nextInt()` sequence is atomic across all three writer threads (tick, session loop, social gateway). The one invariant the protocol cannot survive losing, done right, with documented rationale. (`Session.kt:43,96,235`)
- **The `UpdateMask` sealed hierarchy** keyed by *interface* — one source of truth for block shapes shared across PLAYER_INFO/NPC_INFO, each with its binary offset; lets 947/948 key instances co-register without collision. The one place multi-revision is actually sound.
- **Data-driven, centralized decode** — every C2S decoder is a `Source.() -> ClientProt` lambda registered against an opcode in one file; handlers never touch the wire. The unframable-opcode path is disciplined (frames from stub size to stay in sync, or stops without consuming garbage).
- **Login crypto hygiene** — `LoginToken` uses constant-time compare (`MessageDigest.isEqual`), `SecureRandom`, length-validated parse; `requireSecret` fails closed in production; `MultilogLimiter` is lock-free and correct; `IsaacTest` pins KAT vectors.
- **`WorldTick` cadence** — drift-corrected absolute-deadline accumulator with re-anchor on overrun, per-player try/catch isolation. Correct tick model.
- **`Players` as `AtomicReferenceArray(2048)` with CAS allocate** + leak-proof `try/finally` release — the one world-state structure built to scale.
- **`SocialClient` reconnect + presence resnapshot** — backoff loop + `onConnected` snapshot replay that repairs desync after a lobby restart.
- **Packets as immutable data classes + reified-generic registration DSL**, with **byte-level provenance comments** citing binary addresses. For a faithful server this is the asset that lets you trust the wire format.
- **`AntiCheatChallengeResponseHandler` (world)** — the model handler: typed fields, validates against session state, disconnects on mismatch. Use it as the template for the handler standard.

---

## 4. Findings by slice

### 4.1 Entity-sync builders (the spine — the heart of the concern)
- `PlayerInfoBuilder` has four entry points (`buildInit` / `build` / `buildIfNeeded` / `buildWorldEntrySync`), near-duplicate pass encoders (`encodeHighResInitPass` vs `encodeHighResPass`), and the appearance-synth special-case threaded through four methods (`needsAnyUpdate`, `hasFlaggableExtendedInfo`, `encodeExtendedInfoBlock`, `encodeLocalPlayerInit`).
- Movement is hardcoded `movementType=0 / skipMode=0` "for MVP"; the skip-run RLE is half-built; the low-res pass walks all 2047 seeded slots.
- `NpcInfoBuilder.kt:76–95` contains a paragraph of in-code uncertainty reasoning about whether its movement bits are even correct.
- **Local-appearance delivery is an unsolved workaround the code flags itself** (`PlayerInfoBuilder.buildWorldEntrySync` NOTE): the avatar currently renders via a synth path that is *not* prod-accurate; the real fix (inline-GPI delivery) is RE-gated. This is foundational — worn-models, equipment, appearance-change, and other players' appearances all ride this mechanism.
- **Target:** one state-driven `encode(player, tick)` orchestrating three separated, testable units — **VisibilityManager** (spatial cohorts, add/remove transitions, the 8-adds-per-tick throttle), **MovementEncoder** (walk/run/teleport/stationary + the skip RLE, actually implemented), and the **ext-info encoder** (already good). The differences between first-tick / teleport / idle should *fall out of player state*, not out of which method the caller picked.

### 4.2 Login, handshake & crypto
- **Unify the login decode into one revision-selected `LoginBlockReader`** in `core.net.login` — kills the 3-place transcription, the two username decoders, the two XTEA paths, and the lobby-vs-world version-check disagreement (lobby rejects 947; world would accept it).
- **Resolve RECONNECT/session-binding**: either finish parsing the XTEA tail to `loginType`/`sessionToken` and enforce the binding + the `sessionCheck` server-seed echo (anti-replay is 80% built and currently unenforced — captured RSA blocks replay), or delete the dead reconnect docstring and `sessionId1/2` columns.
- **Harden `RsaCredentialTailParser`** (trial-and-error real→legacy fallback on the same buffer is a decode ambiguity — pin the single 948 layout) and **`RSA.crypt`** (no fixed-width normalization; `BigInteger` sign/length is masked by the `magic==10` check — left-pad to modulus width so the magic check is real).
- **World login has no pre-auth timeout and no per-IP limit** (just a global `> 20` magic number); the lobby has both — share the policy.
- `ISAAC.MASK` reads as a precedence bug but is load-bearing (KAT-proven) — parenthesize + comment so nobody "fixes" it into a desync.

### 4.3 Session lifecycle, tick loops & scene pipeline
- **De-block the tick** (the Theme-A headline): tick enqueues only; flushing moves off the tick thread (per-session loop owns cadence, or bounded IO fan-out with a deadline). Collapse the three `Players.forEach` passes into one.
- **Extract `SceneBuilder.rebuild(player, tile)`** from the inline `initWorldLogin` script; move the captured first-light literals behind a debug flag and replace with real state (a real `Inventory`, a per-zone `ObjSpawn`/`LocSpawn` registry `ZoneBundleBuilder` reads).
- **A `MovementQueue` per `Entity`**, fed by real walk/interaction handlers (today's `logInfo` stubs), is the prerequisite the MovementEncoder consumes.
- **An explicit `PlayerState` enum** (`CONNECTING / IN_LOBBY / ENTERING_WORLD / IN_WORLD / DISCONNECTING`) gating tick + dispatch + social delivery; bound the **unbounded outbound `pendingPackets` queue** (`Session.kt:35` — heap growth on a stalled client); give the gateway/world a single owned `CoroutineScope` instead of 50+ ad-hoc `CoroutineScope(dispatcher).launch`.
- **`Npcs` needs the slot-array + zone-indexed spatial treatment** before NPC ticking multiplies the per-tick cost.
- **The builder DTOs are pure** (`PlayerInfo`/`NpcInfo`/`List<ServerProt>`, no side effects) — so dropping a VisibilityManager *in front of them* is mechanically clean. The seams are in the right place; the fight is the blocking tick + the single-player visibility/movement shortcuts.

### 4.4 Revision system, codec duplication & masks
- **Decide multi-revision honestly.** rev947 is dead (~2,107 LOC). Either delete it (the honest move for 948-only reality; git retains it) or wire `register947()` in with a loading test. Carrying a broken multi-revision abstraction is strictly worse than a clean single-revision one.
- **Collapse to one declarative opcode table** — `ServerProtDef(opcode, name, size, encoder?, decoder?)` rows the registration loop walks once, so the codec map, display metadata, stub rows, and decoder table physically cannot drift. Ideally **generate the table from the RE findings** (`14-serverprot-table.md` / `prot-table.json`) so the doc is the source. Centralize opcodes as **named constants** (a missed renumber then fails to compile).
- **Stop registering the known-wrong mask encoders** (`Rev948ServerCodecsUpdateMasks.kt`) — gate them to fail loudly or finish the transform map; a faithful server must not ship encoders it documents as wrong.
- **Per-`Codec` instance state** — move `ActiveMaskKeys` + the mask-encoder registries off process globals (required for the mask layer to truly isolate revisions).
- Cost to add rev949 *today*: ~19 files, ~3,300 LOC, ~270 hand-edited opcode/name/size sites, ~95% mechanical copy-renumber the compiler can't verify — the worst possible shape for a byte-exact protocol. Target: shared `BaseServerCodecs` (verbatim bodies once) + a revision delta supplying only opcode overrides and the genuinely-changed bodies (Variable transforms, Rebuild layout, Zone header order, the four mask enums).

### 4.5 C2S handlers, ClientProt & JS5
- **Replace ClassGraph-scan + `genericInterfaces[0]` reflection with explicit checked registration** (`register(PacketClass) { session, packet -> }`): makes the handler↔packet edge compile-checked, turns duplicate-class registration into a hard error (today `FriendListAddHandler` exists in both lobby and world, last-loaded-wins silently), removes the "never invoked" silent failure. Fold in **collapsing `PacketHandler<T,K>` to a single `GameSession` receiver** (the `Session`-vs-`GameSession` split buys nothing but an unchecked cast). Delete dead `getHandler` / `handleBlocking`.
- **One handler shape + explicit no-op intent** — standardize on `GameSession`, mark terminal handlers distinctly from unimplemented acks, remove the duplicate inert lobby `ClientLifecycleHandlers`. Pull singleton reach-ins (`EnvVars` / `LobbyState` / `WorldServer` / `SocialGateway`) behind a passed-in context so handlers stay thin and testable.
- **Move JS5 and the recorder out of the live game-protocol package** — `JS5Server` is clean and self-contained (own handshake, reader/writer pair, urgent/prefetch channels) and shares nothing with the game codec; give it its own package/module. `recorder/*` is offline RE/diagnostic tooling that belongs in `tools` next to its existing peers. Split JS5 framing opcodes out of the login-oriented `RequestOpcode`/`ResponseOpcode` objects.

---

## 5. Unified prioritized roadmap

Ordered so each phase de-risks the next, and so the foundation work directly enables the stated next slice (movement / players / NPCs / map).

### Phase 0 — Honesty pass *(cheap, low-risk, do first — makes the code legible)*
- Delete `rev947` (or wire + test it).
- Delete dead `handleBlocking` / `getHandler`.
- Make the known-wrong `sByte`/`sShort` mask encoders **fail loudly** instead of silently registering.
- Mark terminal vs unimplemented handlers; remove duplicate inert lobby handlers.
- Resolve the RECONNECT docstring-vs-reality contradiction.
- Name the magic numbers (world `>20` login cap; begin opcode constants).

### Phase 1 — Foundation for the next slice *(the load-bearing refactors)*
1. **De-block the tick** — tick enqueues only; flush off-thread. *Gates whether >a handful of players is possible.*
2. **Entity-sync pipeline** — collapse the four `build*` methods into a state-driven encoder; introduce `MovementQueue` (input→intent→movement) + a spatial **VisibilityManager** (replacing the all-2047 seed and global Npc iteration) + **MovementEncoder**. *The next slice's foundation.* Keep byte output identical — the tests pin `c0 7f f4` / the idle forms.
3. **Reusable world-entry / `SceneBuilder.rebuild(player, tile)`** — extract from the inline script; debug-gate the frozen first-light captures; back them with real inventory/spawn state. *Lets teleport / respawn / re-entry exist.*
4. **Close local-appearance delivery** (inline-GPI, RE-gated) — make the avatar foundation prod-faithful before building worn-models on it.

### Phase 2 — Wire-truth single-source-of-truth *(faithfulness + extensibility)*
5. **One declarative opcode table** (opcode↔name↔size↔encoder↔decoder), generated from RE findings; named opcode constants. Kills the 4-place drift + the 218/135-row stub mirror.
6. **One revision-selected `LoginBlockReader`** — kills the 3-place login-block transcription, the two username decoders, the two XTEA paths.
7. **Unified explicit handler registration** — checked, greppable, duplicate = boot error; single `GameSession` receiver.

### Phase 3 — Boundaries & hardening
8. **Split the `Codec` god-object** — live wire codec vs RE/display metadata vs instance factory; replace reflective `createInstanceForOpcode` with registered singleton suppliers.
9. **Per-`Codec` instance state** — `ActiveMaskKeys` + mask encoders off process globals (or consciously commit single-revision).
10. **Connection/player state machine**; bound the outbound queue; world-login pre-auth timeout + per-IP limiter; one structured coroutine scope.
11. **Relocate `recorder/` → `tools`; `JS5Server` → own package/module**; split JS5 framing opcodes from login connection types.
12. **Crypto hardening** — enforce the `sessionCheck` echo (anti-replay), RSA fixed-width normalization, ISAAC `MASK` parens+comment, document/widen the Compact-token strength.
13. **Hygiene** — split `ServerProt.kt` (1356 LOC) by domain; `ByteArrayPayload` wrapper to kill ~20 hand-rolled `equals`/`hashCode`; fix inline-FQN violations in `prot/update`.

---

## 6. Readiness for the next slice (map / minimap / movement / multiple players)

The next slice maps directly onto Phase 1 — it is not buildable cleanly on today's foundation:

| Next-slice feature | Blocked by | Phase-1 item that unblocks it |
|---|---|---|
| A second concurrent player | Tick blocks on serial flush; Viewport seeds all 2047; visibility is non-spatial | 1 (de-block tick) + 2 (VisibilityManager) |
| Player **movement** | `player.tile` never reassigned; no input loop; `movementType=0` hardcoded | 2 (MovementQueue + MovementEncoder) |
| NPC spawning / ticking | `Npcs` non-spatial, no index recycle; movement bits self-flagged uncertain | 2 (spatial Npc store + MovementEncoder) |
| **Map / minimap / scene** beyond Lumbridge | Scene is a frozen capture; world-entry is a one-shot inline script | 3 (`SceneBuilder.rebuild` + real zone-spawn state) |
| Worn models / equipment on the avatar | Local-appearance delivery is a non-faithful synth workaround | 4 (inline-GPI delivery) |

**Bottom line:** the byte layer is something to be proud of and to protect. The architecture is honest prototype scaffolding that now needs to become a real per-tick simulation. Doing Phase 0 + Phase 1 first means the map/movement/multiplayer slice lands on clean foundations instead of compounding the special-cases.
