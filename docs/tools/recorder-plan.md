# `libdarkan_recorder.dylib` — In-Process Boot-Transcript Recorder

**Status:** DESIGN — not implemented. This document is for review/approval before any code is written.
**Owner:** client-launcher-engineer (sibling to `client/launcher/patcher-mac/`).
**Date:** 2026-06-21.

---

## 0. Problem statement and thesis

We are bringing up world-login by guessing protocol → watching the client crash/desync → reverse-engineering the crash → fixing → repeating. We are ~6 layers deep on world-login and the loop is slow because **we have no ground truth for what the real server actually sends**. Every byte we emit is a hypothesis.

The recorder replaces guessing with observation. It is an in-process dylib injected the same way as the proven `libdarkan_patcher.dylib`, but instead of *patching* the client it *taps* the client at the points where data is already cleartext, and writes the full boot transcript to disk:

- **Game TCP streams** (JS5 + lobby + world + in-world) — raw bytes, both directions, per fd, timestamped.
- **ISAAC seeds** — the 4 C2S seeds captured at `Isaac::Init` (S2C derived as `+50`).
- **Login-block plaintext** — captured pre-RSA, so we see the exact C→S login layout the client builds.
- **TLS/HTTP** — auth + `jav_config.ws` request/response bodies, decrypted in-process before TLS encrypts them.

An **offline deframer** then replays the captured ISAAC seeds over the raw game stream, de-obfuscates the opcode bytes, splits packets per the 948 size tables, and emits a **JSONL transcript** annotated with our own codec's opcode names. This transcript is the RE ground truth: it shows the exact ServerProt sequence, byte layouts, and the ISAAC engagement boundary that we are currently stuck reverse-engineering from crashes.

### Why this is strictly better than the existing `LoginProxy`

We already have `tools/src/main/kotlin/org/darkan/tools/loginproxy/LoginProxy.kt` — a TCP MITM that decrypts the RSA block with our key, extracts ISAAC seeds, re-encrypts with Jagex's key, forwards to live, and ISAAC-decodes both directions. It is excellent prior art and **the recorder reuses its deframer logic almost verbatim** (offline). But the proxy has two structural limits the recorder removes:

1. **The proxy requires a patched client** (so the client encrypts the login block with *our* RSA key, letting the proxy decrypt it). The recorder taps the login block *before* RSA encryption inside the process, so **no RSA round-trip and no client patching are required** to read the login plaintext or the seeds. This means we can capture against **prod, unmodified, talking to the real Jagex servers** — which is exactly the ground truth we lack.
2. **The proxy must sit on the wire and re-encrypt**, which is fragile (RSA size drift, leading-zero handling, session-key swap on world transfer — all visible in the proxy's `performRsaMitmWithKeySwap`). The recorder reads cleartext directly at the syscall boundary and never re-encrypts anything. It is read-only and cannot perturb the connection.

The recorder and the proxy are complementary: the proxy is a live MITM that can *rewrite* traffic (useful for redirection); the recorder is a passive, in-process observer that produces clean ground-truth transcripts with zero wire interference.

---

## 1. Authorization and safety scope (read first)

This is **interoperability reverse engineering on the user's own client, own machine, and own account/session**, for the user's own private-server project. It is the same category of work as the Ghidra analysis already in `docs/binary/` and the existing `LoginProxy`:

- We capture **only our own traffic** on **our own machine**, from a client **we launched** with **our own Jagex account**.
- We are **not** targeting other users, intercepting anyone else's session, or exfiltrating third-party secrets. The only credentials/tokens that pass through the TLS tap are the operator's own, captured locally for the operator's own debugging.
- The recorder is **read-only on the wire** — it observes; it does not modify, replay-to-third-parties, or attack any server.
- Captured artifacts contain the operator's own session tokens and auth material. They are **secrets**: the plan mandates a redaction pass and `.gitignore` for capture output (see §13). Nothing sensitive is logged to stdout or committed.

This scope is stated plainly so the reviewer can confirm it before approving. If any phase drifts outside "own client + own account, observe-only," that is out of scope and must be re-approved.

---

## 2. Mechanism primer — what we inherit from the patcher (proven)

The recorder is a **sibling crate** to `client/launcher/patcher-mac/`. It inherits, verbatim, the proven mechanics from `client/launcher/patcher-mac/src/lib.rs` and `build-mac.sh`:

| Proven mechanic | Source | Recorder reuse |
|---|---|---|
| `DYLD_INSERT_LIBRARIES` injection into the x86_64 NXT client under Rosetta 2 | `run-client-mac.sh` line 98; `build-mac.sh` header | Identical injection path; recorder dylib added to `DYLD_INSERT_LIBRARIES` (colon-separated, can coexist with the patcher) |
| `#[ctor]` constructor runs **before** the main image's `__mod_init_func` C++ static initializers | `lib.rs` lines 13–17, 177 | Recorder installs interpose tables + inline hooks from a ctor, guaranteeing taps are live before the client's first network/crypto call |
| Must build `--target x86_64-apple-darwin` (translated process loads only x86_64 dylibs) | `Cargo.toml` lines 21–23; `build-mac.sh` lines 16–18 | Same target; same `file | grep x86_64` guard |
| Adhoc-sign (`codesign -s -`) so dyld accepts the inserted dylib | `build-mac.sh` lines 98–105 | Same adhoc sign step |
| dyld image enumeration via `_dyld_image_count` / `_dyld_get_image_name` / `_dyld_get_image_header` / `_dyld_get_image_vmaddr_slide`; `strstr(name,"rs2client")` to find the main image | `lib.rs` lines 155–163, 393–418 | Reused **only** for inline-hook taps (ISAAC, login-block) that need the main-image slide + `__TEXT` bounds for pattern scanning |
| `mach_vm_protect(VM_PROT_COPY)` to break COW on r-x `__TEXT`, write, restore r-x; `sys_icache_invalidate` insurance | `lib.rs` lines 512–565; `patch-targets-948-mac.md` §Runtime | Reused **only** for installing inline trampolines (writing a `JMP` into `__TEXT`). Interpose taps need none of this. |
| Pattern scan with `memchr::memmem::Finder` over the slid `__TEXT` range | `lib.rs` lines 474–496 | Reused to locate inline-hook targets by signature in the prod build (addresses differ from staging — see §4) |

**The crucial split** (and the heart of "Rosetta inline-hook robustness", §11): the patcher only ever patches *static data* (RSA hex strings) and one *16-bit immediate* (port). It never redirects control flow. The recorder needs three things, two of which are far gentler than what the patcher already does:

- **Interpose** of libc/libSSL functions (`recv`/`send`/`read`/`write`, `SSL_read`/`SSL_write`) — **no code patching at all**, just a `__DATA,__interpose` section dyld wires up at load. Rosetta-safe by construction. This covers the entire game TCP stream and TLS, i.e. the bulk of the value.
- **Inline hooks** of two client functions (`Isaac::Init`, the login-block builder) — these *do* redirect control flow and are the only Rosetta-risk surface. They are needed only for the seeds and the login plaintext, and there are robust fallbacks (§3.2, §11).

---

## 3. Tap inventory — interpose vs inline-hook, per target

This is the central technical decision table. "Mechanism" states exactly how each tap is installed and, for inline hooks, how the target is located in the **prod** build (which has shifted addresses and rotated keys vs our staging `rs2client.948-5-mac`).

| # | Tap | What it yields | Mechanism | Located in prod build how | Rosetta risk |
|---|-----|----------------|-----------|---------------------------|--------------|
| T1 | `recv` / `read` | Raw inbound game bytes (JS5/lobby/world), per fd | **Interpose** (`__DATA,__interpose`) | N/A — symbol resolved by dyld, no address | **None** (no code patch) |
| T2 | `send` / `write` | Raw outbound game bytes, per fd | **Interpose** | N/A | **None** |
| T3 | `connect` | fd ↔ (host,port) map, connection lifecycle, direction-of-first-byte | **Interpose** | N/A | **None** |
| T4 | `close` | fd lifecycle close (flush per-fd state) | **Interpose** | N/A | **None** |
| T5 | `SSL_read` | Decrypted inbound TLS (auth + jav_config response bodies) | **Interpose** (if statically/dynamically linked OpenSSL/BoringSSL symbol is present) — else fallback in §5 | Symbol name `SSL_read`; if absent, scan for the TLS lib & hook its read (§5 fallback) | **None** if symbol present |
| T6 | `SSL_write` | Decrypted outbound TLS (auth + jav_config requests, headers, tokens) | **Interpose** (same caveat as T5) | Symbol name `SSL_write` | **None** if symbol present |
| T7 | `jag::Isaac::Init` | The 4 C2S ISAAC seeds (S2C = +50) | **Inline hook** (trampoline) — or **fallback: read seeds at the login-block tap T8**, which already has them in plaintext | Pattern-scan `__TEXT` for `Isaac::Init` prologue signature; cross-check via the `+50` PADDD const `[50,50,50,50]` site and its reader | **Medium** — mitigated, see §11 |
| T8 | login-block builder (pre-RSA), the `SendLoginPacket`-family function | Exact C→S login-block plaintext layout (solves `success=9`); **also yields the 4 ISAAC seeds** as a T7 fallback | **Inline hook** at the point where the assembled plaintext buffer + length are known, *before* the RSA call | Pattern-scan for the structurally-unique PADDD-from-`ISAAC_DELTA` site (staging: `66 0f fe 05 …` reading `[50,50,50,50]`) and the RSA-call boundary | **Medium** — mitigated, see §11 |

### 3.1 Why T1–T6 carry zero control-flow risk

DYLD interposing replaces the symbol binding via a `__DATA,__interpose` section of `{replacement, original}` function-pointer pairs. dyld rewrites the lazy/auth pointers at load time; the CPU never executes a hand-written trampoline. Under Rosetta this is identical to native because Rosetta translates whatever code dyld points the call at — there is no "patch a live instruction stream while it may be mid-translation" hazard. This is why **the entire game-stream capture (the single highest-value deliverable) and the TLS capture sit on the safe side of the line.**

The recorder is `recv`/`send` interpose at heart. The proxy's own deframer comments confirm payloads are plaintext on the wire and only the opcode byte is ISAAC-obfuscated (`LoginProxy.kt` §`parseClientPostLogin`), so capturing raw `recv`/`send` bytes loses nothing — all the structure is recoverable offline from the seeds.

### 3.2 Why T7/T8 are the only risky taps — and why we can often skip T7 entirely

T7 and T8 redirect control flow inside the client (write a `JMP` into `__TEXT`, run our prologue, jump back). That is the genuine Rosetta-AOT hazard (§11). Two things de-risk this:

1. **The login-block tap (T8) already contains the seeds.** The mac patch-targets doc shows `SendLoginPacket_xplat948` computes `server_seed[i] = client_seed[i] + 50` via `PADDD XMM0,[0x100ad7b50]` at `0x1000cb894`, reading the 4 client seeds from `[R14+0x48]` (`patch-targets-948-mac.md` §P4). The login block the client builds embeds those same 4 seeds (the proxy extracts them from exactly this plaintext — `LoginProxy.kt` `performRsaMitm` reads "ISAAC/XTEA keys: 4 x int32" right after the magic byte). **So one inline hook at T8 yields both the login layout and the seeds.** T7 becomes an optional cross-check, not a requirement.
2. **If even one inline hook is too risky, there is a no-inline-hook fallback for seeds**: in **our-server validation runs only**, we already know the seeds (our server logs them). So Phase 1's validation does not strictly need T7/T8 at all — it can be proven end-to-end with interpose-only taps plus the server-side seed log. The inline hook is only strictly required for the **prod** capture where we don't otherwise know the seeds. (And even there, T8 is the single hook that covers it.)

This is the key robustness story: **we get to the full boot transcript with interpose-only taps + our own server's seed knowledge first, and only introduce the one inline hook (T8) for the prod run, where it is cross-validated against everything we learned on our server.**

---

## 4. Prod-build divergence — locating inline-hook targets durably

Per the prompt and `patch-targets-948-mac.md` §"Open Items 3", **the prod client is a different build** than staging `rs2client.948-5-mac`: rotated RSA keys, shifted addresses. The patcher already copes by **pattern-scanning** rather than hardcoding addresses (`lib.rs` scans for `aad4a780…`, `a6400fbc…`, `66 b8 50 00`). The recorder's inline hooks must do the same.

### 4.1 Interpose taps need nothing (T1–T6)

`recv`/`send`/`read`/`write`/`connect`/`close`/`SSL_*` are resolved by dyld by **name**. Build divergence is irrelevant — these work on any build, any revision, signed or unsigned, with zero address knowledge. This is a major durability win and is why we lean on interpose for the bulk of the capture.

### 4.2 Inline-hook target signatures (T7, T8)

For the two inline hooks we define **pattern signatures** anchored on revision-stable structural features, with a Ghidra-import fallback when a signature misses:

**T8 — login-block builder / `SendLoginPacket` family (primary inline hook).** Anchor candidates, in order of durability:
- **Anchor A (preferred): the ISAAC-`+50` derivation site.** The 16-byte constant `32 00 00 00 32 00 00 00 32 00 00 00 32 00 00 00` (`[50,50,50,50]`) is RELOCATED-stable across builds (the `+50` contract is fixed — `patch-targets-948-mac.md` §P4, confirmed unchanged on every platform). Scan `__TEXT,__const` for that 16-byte run, then scan `__text` for the `PADDD XMM,[rip+disp]` instruction whose RIP-relative target resolves to that constant (staging encoding `66 0f fe 05 <disp32>`). That instruction lives inside the login-block builder. Walk backward to the function prologue (the buffer+seeds are assembled just before) and forward to the RSA-call boundary to find the exact hook point where the plaintext buffer pointer + length are in known registers/stack slots.
- **Anchor B (cross-check): the RSA modulus consumer.** The login modulus string (`aad4a780…`, located exactly as the patcher already does) is loaded via `LEA RDX,[rip+disp]` and handed to the BigInteger parser feeding the login RSA packet builder (`patch-targets-948-mac.md` §P1 parse chain). The function that references the parsed login key and builds the RSA packet is the login-block builder. Use this to confirm Anchor A found the right function.
- **Hook point:** the instruction *after* the plaintext login buffer is fully assembled and *before* it is encrypted. We capture `(buf_ptr, len)` there. This is the moment that reveals the exact field order/types that currently drift on `success=9`.

**T7 — `Isaac::Init` (optional, cross-check only).** Anchor on the `Isaac::Init` prologue signature plus the fact that the login-block builder calls it with the server seeds right after the PADDD (`patch-targets-948-mac.md` §P4: "then `jag::Isaac::Init(server_seeds)`"). Because T8 already yields the seeds, T7 is a belt-and-braces confirmation, not load-bearing.

### 4.3 Ghidra-import fallback (durability across client updates)

When a signature fails to match a new prod build (Jagex ships updates regularly), the documented fallback is:

1. Import the exact prod binary into Ghidra (`~/rs-re-staging/macproj` workflow; the ghidra-reverse-engineer agent owns this).
2. Re-derive the T8 hook point and the T7 prologue, and **record the new signature** in `docs/binary/` (a new `recorder-hook-targets-<rev>.md`, or an addendum to `patch-targets-948-mac.md`).
3. The recorder reads signatures from a small embedded table keyed by a build fingerprint (e.g. the login-modulus prefix, which already distinguishes 947 `8f389edb` from 948 `aad4a780` — `build-mac.sh` lines 45–47). On an unknown fingerprint the recorder **logs a clear "inline hooks unavailable for this build — run interpose-only + import to Ghidra" message and degrades to interpose-only capture** rather than crashing the client. This mirrors the patcher's "no-op cleanly if pattern not found" policy (`lib.rs` lines 278–281).

**Durability principle (explicit):** interpose taps are update-proof; inline taps are signature-driven with a Ghidra fallback and a safe degrade-to-interpose path. We never hardcode a prod address. **The RE-agent dependency for hook signatures is a hard prerequisite for the prod run (Phase 5) and must be requested before that phase if the prod build differs from any analyzed build.**

---

## 5. TLS/HTTP capture — symbol-availability caveat (flagged uncertainty)

The cleanest TLS tap is interposing `SSL_read`/`SSL_write` (T5/T6): read the buffer *after* decrypt / *before* encrypt, getting cleartext request/response bodies for auth and `jav_config.ws`. **This works only if the client links an OpenSSL/BoringSSL-style API with those exported symbols.** That is an assumption that must be verified on the actual binary, not assumed:

- **Verification step (Phase 2, before building T5/T6):** `nm -gU` / `dyld_info -exports` / `otool -L` the prod (and staging) client to determine the TLS stack. RS3 NXT historically bundles its own TLS (OpenSSL-family) statically; if so, `SSL_read`/`SSL_write` are present and interpose works, OR they are internal (not exported) and need an inline hook located by signature.
- **Branch A — exported `SSL_*`:** interpose, zero risk. Preferred.
- **Branch B — statically-linked, non-exported `SSL_*`:** locate `SSL_read`/`SSL_write` by signature in `__TEXT` (the functions have recognizable prologues and call `ssl3_read_bytes`-style internals) and inline-hook them. Medium Rosetta risk, same mitigation as §11. Document the signature in `docs/binary/`.
- **Branch C — macOS `Security.framework`/`SecureTransport` or `Network.framework`:** interpose `SSLRead`/`SSLWrite` (SecureTransport) or hook the `nw_` path. Different symbols; same interpose approach if exported.
- **Branch D — TLS is irrelevant to our blockers:** auth (OAuth) and `jav_config.ws` are the only TLS consumers, and we already have `jav_config_live.ws` captured (`docs/binary/`) and a working OAuth flow in the launcher. **TLS capture is therefore the lowest-priority phase** (Phase 2) and can be deferred or skipped if the symbol situation is hostile — it does not block the world-login transcript, which is all game-TCP + ISAAC.

This is flagged as the single biggest "unknown until we look at the binary" in the plan. The mitigation is that TLS capture is genuinely optional for the critical path.

---

## 6. Offline deframer — the transcript producer

The deframer is a **standalone Kotlin tool** in the existing `tools/` module (NOT in the dylib — the dylib only captures raw bytes + seeds; all parsing is offline so the in-process footprint stays minimal and the capture stays a faithful raw record). It reuses the proxy's proven logic directly.

### 6.1 Inputs
- `raw-c2s.bin`, `raw-s2c.bin` per connection (from T1/T2), with a sidecar index of `(offset, ts, fd, direction)` records (from the interpose taps) so the deframer knows where each TCP read/write boundary fell and when.
- `isaac-keys.txt` (4 C2S seeds, from T7/T8; S2C derived `+50`) per game connection.
- The connection-type byte and the login handshake bytes (already in the raw streams).

### 6.2 Algorithm (lifted from `LoginProxy.kt`)
Reuse the proxy's exact post-login deframer, which is already correct and handles the hard cases:
- **Pre-ISAAC handshake** is parsed by the documented phase machine (`LoginProxy.kt` `Phase` enum; `docs/net/login-wire-format.md`): connection-type byte (14/15/16/18/19/28), `[1B result][8B session_key]`, login packet `[1B opcode][2B varShort][payload]`, login result, login data. The deframer walks these by byte count exactly as the proxy does.
- **ISAAC engagement boundary** — the precise point where opcode obfuscation begins. The proxy transitions to `Phase.POST_LOGIN` after the login-data block and *then* starts consuming ISAAC values. **This boundary is exactly what we're stuck on for world-login** (the "Missing ClientProt 162" desync = consuming an ISAAC value one packet too early/late). The recorder resolves it because we have the *ground-truth* raw stream + the *exact* seeds, so the deframer can be run with the boundary as a tunable and we can see which boundary yields clean opcodes 0–129 for the whole transcript.
- **Opcode de-obfuscation:** `opcode = (rawByte - cipher.nextInt()) & 0xFF`, with the 1-or-2-byte rule (`docs/net/framing.md` §1: decoded0 > 0x7F → read second byte, `opcode = (decoded0-0x80)*256 + decoded1`). Both bytes consume an ISAAC value (`docs/net/tcpin-isaac-decoding.md`). The proxy's `parseClientPostLogin` / S2C decoder implement this including the **partial-read pending-opcode/pending-size state** (`c2sPendingOpcode`/`c2sPendingSize`) so a packet split across TCP reads doesn't double-consume an ISAAC value — we reuse that state machine verbatim.
- **Size resolution + annotation:** `codec.clientProtSize(opcode)` / `codec.serverProtSize(opcode)` from `register948()` (`core/.../prot/Codec.kt` lines 169–179): fixed=N, varByte=`-1` (1 size byte), varShort=`-2` (2 size bytes). Names from `codec.clientProtName` / `serverProtName`. This is the same codec the proxy uses (`LoginProxy.kt` line 175: `private val codec: Codec = register948()`).

### 6.3 Output — JSONL transcript
One line per packet:
```json
{"ts": 1718900000.123, "dir": "S2C", "fd": 7, "conn": "world", "seq": 412,
 "opcode": 81, "name": "REBUILD_NORMAL", "size_kind": "fixed", "size": 8,
 "payload_hex": "1a2b...", "isaac_index": 1234, "notes": ""}
```
Plus a per-connection header (connection type, seeds used, login plaintext from T8, ISAAC engagement offset) and a `desync` marker line if an opcode decodes out of range (mirrors the proxy's `POST_LOGIN DESYNC` log). The transcript is the artifact handed to the networking-protocol-engineer and ghidra-reverse-engineer agents.

### 6.4 Cross-validation with our codecs
The deframer annotates with our 948 names but the **bytes are authoritative**. Where our `serverProtSize(op)` disagrees with what cleanly frames the stream, that disagreement is a finding (our size table is wrong). The opcode tables at `~/projects/reclass-data/{clientprot,serverprot}_948-5_opcode_table.json` are the secondary cross-reference for names/sizes the codec doesn't have.

---

## 7. Phase plan

Each phase: **goal · tap points + mechanism · outputs · dependencies/ordering · validation · risks + mitigations.**

### Phase 0 — Scaffolding, injection, de-risk, prod-target strategy
**Goal:** Stand up the recorder crate as a sibling to the patcher, prove it injects and runs a ctor inside the Rosetta client, and lock the prod-build target-location strategy — all with **zero taps that touch client code** (interpose only, or even just a "hello from ctor" log).

**Tap points + mechanism:**
- A `#[ctor]` that logs `[darkan-recorder] loaded, pid=…, images=…` and enumerates dyld images (reusing `find_rs2client_segments` from the patcher).
- A single trivial interpose of `connect` that logs `(fd, host:port)` — proves the interpose section is wired by dyld under Rosetta with no code patching.
- New crate `client/launcher/patcher-mac/` sibling: `client/launcher/recorder-mac/` (crate `darkan-recorder`, `cdylib` named `darkan_recorder`), `Cargo.toml` mirroring the patcher's (`ctor`, `libc`, `memchr`, `mach2`), `build-mac.sh` mirroring the patcher's adhoc-sign + x86_64 guard + deploy-to-all-slots logic.
- Launch integration: extend the env launch so `DYLD_INSERT_LIBRARIES` can carry **both** patcher and recorder (colon-separated). For Phase 0–1 validation we drive it through `run-client-mac.sh` semantics (direct exec, not `open` — LaunchServices strips `DYLD_*`, see `run-client-mac.sh` lines 90–92).

**Outputs:** the crate skeleton; a `build-mac.sh`; a proof-of-injection log; a written **target-location strategy doc** (this §4) reviewed against the prod binary's `nm`/signature.

**Dependencies/ordering:** none (first). Establishes everything downstream relies on.

**Validation:**
- Run staging `rs2client.948-5-mac` with the recorder in `DYLD_INSERT_LIBRARIES`; confirm the ctor log appears and `connect()` interpose fires when the client dials the server.
- Confirm coexistence: patcher + recorder both inserted, both run, neither breaks the other (interpose + the patcher's data patches are orthogonal).
- **Prod signature/entitlement check (gating, see §10):** run `codesign -dv` / `codesign -d --entitlements -` on the prod client. Staging is confirmed `code object is not signed at all` (verified) so insert works freely; the prod binary's signature/hardened-runtime/library-validation status determines whether prod injection needs a workaround (§10).

**Risks + mitigations:**
- *R0.1 prod binary has hardened runtime + library validation → DYLD insert blocked.* Mitigation in §10 (re-sign workflow, `--inject` of an adhoc copy, or `DYLD_*` allowance). Discovered now, not in Phase 5.
- *R0.2 recorder shadows a stale copy in a deploy slot.* Mitigation: reuse the patcher's atomic multi-slot deploy + marker-verify pattern (`build-mac.sh` lines 110–166) so a fresh build can't be shadowed.

### Phase 1 — Game-stream interpose + ISAAC seed hook + offline deframer, validated on OUR server
**Goal:** Produce a clean, fully-deframed JSONL transcript of a complete boot against **our own server**, where we know every byte and every seed, so the recorder + deframer are proven before we point them at prod. **This phase is the de-risking spine of the whole project.**

**Tap points + mechanism:**
- **T1/T2/T3/T4 interpose** (`recv`/`read`/`send`/`write`/`connect`/`close`) — capture raw per-fd streams + boundary index. Zero risk.
- **Seeds:** prove the deframer **two ways**:
  1. *Interpose-only path:* take the seeds from **our server's log** (the server knows them — `WorldServer.kt`/`LoginServer.kt` derive S2C as `+50`). No inline hook needed. This isolates deframer correctness from inline-hook risk.
  2. *Inline-hook path:* enable T8 (login-block builder) and confirm the seeds it captures **match the server's logged seeds exactly**. This validates the inline hook against ground truth before prod.
- **Offline deframer** (§6) in `tools/`.

**Outputs:**
- `transcript.jsonl` for a full our-server boot: JS5 → lobby login → lobby state → world login → in-world.
- A `seeds-match` report (inline-hook seeds == server seeds).
- The deframer tool itself (permanent, reused every phase).

**Dependencies/ordering:** Phase 0. Requires our lobby+world servers running (they exist: `lobby/`, `world/`). Uses `register948()` codec (exists).

**Validation (the gold standard — every byte is known):**
- Byte-diff: the deframer's reconstructed packet stream must re-serialize to **exactly** the raw bytes our server sent (we have both the server's send log and the captured `recv` bytes). Any mismatch is a deframer bug.
- Opcode sanity: every decoded opcode ∈ valid range (C2S 0–129; S2C per 948 table), no DESYNC markers across the whole transcript.
- ISAAC boundary: confirm the engagement offset that yields a clean full-transcript decode, and record it.
- Seeds-match (above).

**Risks + mitigations:**
- *R1.1 interpose misses bytes if the client uses `recvmsg`/`readv`/`recvfrom` instead of `recv`/`read`.* Mitigation: in Phase 0/1, `dtruss`/`fs_usage` the client to enumerate which socket syscalls it actually uses, then interpose the full set it uses. (RS3 NXT is plain `recv`/`send` historically, but verify.) Flagged assumption.
- *R1.2 fd reuse across lobby→world transfer confuses per-fd state.* Mitigation: T3/T4 (`connect`/`close`) bracket each fd's lifetime; the deframer keys ISAAC state on `(connection-epoch, fd)` not bare fd, and resets at each `connect`. The proxy already handles the lobby→world socket swap (`handleGameLogin`), informing this.
- *R1.3 the inline hook (T8) perturbs the client under Rosetta.* Mitigation: path (1) doesn't use it at all; path (2) only adds it once path (1) is green, so we can attribute any regression to the hook immediately. Plus §11.

### Phase 2 — TLS/HTTP capture
**Goal:** Capture decrypted auth + `jav_config.ws` request/response bodies. **Lowest priority** (§5 Branch D) — does not block the world-login transcript.

**Tap points + mechanism:** T5/T6 — interpose `SSL_read`/`SSL_write` if exported (Branch A); else inline-hook by signature (Branch B) or interpose SecureTransport `SSLRead`/`SSLWrite` (Branch C). Decision driven by the Phase-2 symbol audit (§5).

**Outputs:** `tls-<host>.log` with method/URL/headers/body per request, paired with responses; redacted (§13).

**Dependencies/ordering:** Phase 0. Independent of Phase 1 (different taps). Can run in parallel with Phase 1 work but ships after, given lower priority.

**Validation:** captured `jav_config.ws` body matches the known-good `docs/binary/jav_config_live.ws`; OAuth token exchange bodies match the launcher's own OAuth flow (`client/launcher/src/auth/`). Since we control the request (our own account), we can diff against expectations.

**Risks + mitigations:**
- *R2.1 TLS symbols not exported / unknown stack.* Mitigation: the §5 branch analysis; degrade to "TLS capture unavailable, document the stack for later" without blocking anything.
- *R2.2 capturing our own tokens is sensitive.* Mitigation: mandatory redaction pass (§13); capture dir `.gitignore`'d; never logged to stdout.
- *R2.3 HTTP/2 or chunked bodies need reassembly.* Mitigation: capture is raw `SSL_*` buffers with offsets; reassembly is an offline concern, same model as the game stream.

### Phase 3 — Pre-RSA login-block dump (hardening T8)
**Goal:** Emit the **exact byte layout** of the C→S login block the client builds, pre-RSA — directly resolving the `success=9` field drift. (T8 is introduced in Phase 1 path (2) for seeds; Phase 3 *productizes* its full-buffer dump + annotation.)

**Tap points + mechanism:** T8 inline hook (§3, §4.2) at the assembled-plaintext / pre-encrypt boundary; capture `(buf_ptr, len)` → dump the full plaintext login block.

**Outputs:** `login-block.bin` + an annotated field breakdown (magic `0x0A`, 4× ISAAC seed int32, 8B session key, then credential/version/token fields — cross-referenced to `docs/net/login-wire-format.md` and the proxy's `performRsaMitm` parse). This is diffed field-by-field against what our `LoginServer`/world login expects.

**Dependencies/ordering:** Phase 1 (T8 already located + seed-validated). Reuses the §4.2 anchors.

**Validation:** on **our** server, the captured login block must decode to the same fields our server parses (we control both ends). Then the same hook on prod reveals the real layout; the diff between "what we send/expect" and "what the real client builds" is the `success=9` fix.

**Risks + mitigations:**
- *R3.1 T8 hook point is off by a few instructions → buffer not fully assembled / already encrypted.* Mitigation: validate on our server first (we know the correct plaintext); only trust prod once the our-server capture round-trips. Anchor B (RSA-consumer cross-check) confirms the function.
- *R3.2 the builder is inlined differently in prod.* Mitigation: §4.3 Ghidra-import fallback; degrade to interpose-only + seeds-from-T8-PADDD if full-buffer capture can't be sited.

### Phase 4 — Diff/Conformance tool (ground-truth vs our server)
**Goal:** Turn the captured prod transcript into an **automatic conformance oracle**: diff the ground-truth transcript against our server's actual output for the same boot phase, auto-flagging mismatches in **opcode set, ordering, sizes, and bytes**. This is what makes the recorder a permanent regression harness.

**Tap points + mechanism:** none new — pure offline tooling in `tools/` consuming two transcripts:
- **Ground-truth transcript** = prod capture (Phase 5) or a saved reference capture.
- **Our-server transcript** = recorder run against our own server (Phase 1 path).
- The differ aligns the two by phase (JS5 / lobby-login / lobby-state / world-login / world-init) and by packet sequence, then reports: opcodes present in ground-truth but missing from ours (and vice versa), opcodes out of order, size-kind mismatches, and per-field byte diffs for matching opcodes (especially the **world-init ServerProt sequence** and **REBUILD op-81 coords**).

**Outputs:** `conformance-report.{md,json}` with a pass/fail per phase and a ranked list of mismatches; a CI-friendly exit code (0 = conformant for the covered phase).

**Dependencies/ordering:** Phase 1 (deframer) + at least one ground-truth transcript (ideally Phase 5 prod, but can bootstrap against a saved capture). 

**Validation:** seed it with a deliberately-wrong server output (e.g. our current `success=9` build) and confirm it flags the exact divergence we already know about. Then confirm a corrected build reports conformant.

**Risks + mitigations:**
- *R4.1 alignment ambiguity (which of our packets corresponds to which ground-truth packet when counts differ).* Mitigation: align on opcode-name anchors + phase boundaries; where ambiguous, report a "structural divergence" block rather than forcing a misalignment. Human-readable diff first; machine gate second.
- *R4.2 timing/keepalive noise inflates diffs.* Mitigation: a suppress-list of keepalive/periodic opcodes (the proxy already maintains `SUPPRESS_S2C`/`SUPPRESS_C2S` — reuse), and order-insensitive comparison for known-async packets.

### Phase 5 — Prod capture run (own account, observe-only)
**Goal:** Capture the **real, complete boot transcript from Jagex prod** — the actual ground truth — with our own account, having validated everything on our server first.

**Tap points + mechanism:** T1–T4 interpose (game stream), T8 inline hook (seeds + login block) located via §4.2 signatures against the **prod** build, optionally T5/T6 (TLS). **No patcher loaded** (we want the unmodified client talking to real Jagex). Launch the unmodified prod client (its real launcher or our direct-exec harness) with the recorder in `DYLD_INSERT_LIBRARIES` — subject to the §10 signing resolution.

**Outputs:** `prod-transcript.jsonl` (the canonical RE ground truth): JS5 handshake, lobby login + response codes, lobby state, **world-login response byte layout + ISAAC engagement boundary**, the **real world-init ServerProt sequence**, and **REBUILD op-81 coordinates** from a live world. Plus the prod login-block layout (Phase 3) and seeds.

**Dependencies/ordering:** Phases 0–3 green on our server; §10 signing resolved; §4 prod signatures located (RE-agent if the build differs). **This is the highest-value milestone** and gates the world-login fix.

**Validation:** the prod transcript deframes cleanly end-to-end (no DESYNC) — that itself proves seeds + boundary + sizes are all correct. Spot-check known opcodes (login result codes from `LOGIN_RESULT_NAMES`, world list op) against documented values.

**Risks + mitigations:**
- *R5.1 prod injection blocked by signing.* → §10 (the dominant risk for this phase; resolved in Phase 0).
- *R5.2 prod build differs → inline-hook signatures miss.* → §4.3 Ghidra import; degrade to interpose-only (still yields the raw streams; seeds then come from a single login on our patched client where we know them, applied to deframe the prod stream only if seeds are shared — they are not, so prefer fixing the signature). Flag: **if T8 can't be sited on prod, we still capture raw prod streams but cannot deframe opcodes without the prod seeds** — so T8 on prod is important; the Ghidra fallback is the safety net.
- *R5.3 capturing prod traffic must stay within scope (§1).* Mitigation: own account, observe-only, redaction; this is interop RE on our own session.
- *R5.4 anti-tamper / integrity checks in prod client detect the dylib.* Mitigation: the recorder is observe-only and doesn't modify `__TEXT` except the single T8 trampoline (which restores bytes); if integrity checks are present, prefer interpose-only + Ghidra-derived seed extraction. Flagged as possible; assess during Phase 0 prod inspection.

### Phase 6 — Feed findings back + permanent regression harness
**Goal:** Convert the ground truth into server fixes and keep the recorder as a standing validation harness.

**Tap points + mechanism:** none new. Process + tooling:
- Hand `prod-transcript.jsonl` + the login-block layout to the **networking-protocol-engineer** (server codecs/handlers) and **ghidra-reverse-engineer** (to update `docs/protocol/`, `docs/net/`, `docs/binary/`). The recorder agent doesn't modify `org.darkan.core.net` — it produces the transcript that those owners consume (per the project's agent-ownership rules).
- Wire the Phase-4 conformance tool into a repeatable check: a saved reference prod transcript per phase becomes the regression baseline; our-server boots are diffed against it on demand (and ideally in CI for the phases we've reached).

**Outputs:** updated server codecs (by the networking agent), updated docs (by the RE agent), a committed set of **reference transcripts** (redacted) + a conformance gate.

**Dependencies/ordering:** Phase 4 + Phase 5.

**Validation:** the world-login blockers close — our server's world-login output is conformant to the prod transcript for the world-init sequence and REBUILD op-81; the client reaches an in-world scene against our server.

**Risks + mitigations:**
- *R6.1 reference transcripts drift when Jagex updates the protocol.* Mitigation: re-capture (Phase 5 is repeatable); version transcripts by client revision (the login-modulus-prefix fingerprint, §4.3).
- *R6.2 redacted reference transcripts lose bytes needed for diffing.* Mitigation: redact only TLS auth material + account identifiers; game-stream payloads are protocol bytes, not secrets, and are kept (see §13).

### Optional Phase 7 — Replay / fuzz mode (stretch, design-only here)
**Goal (as the prompt frames it):** replay a captured server transcript *at our own client* (does the unmodified client accept our re-emission of the prod server bytes?), and/or diff our server's live output against the capture in real time.

**Approach sketch:**
- **Server-transcript replay:** a tiny replay server reads `prod-transcript.jsonl` (or the raw S2C stream + seeds), re-encrypts opcodes with a fresh ISAAC keyed by the seeds it dictates in the login response, and streams the recorded S2C bytes to a locally-launched client (patched to connect to it). If the client renders the lobby/world, our framing understanding is provably complete. This is essentially the proxy run "from a file" instead of "from Jagex."
- **Live diff mode:** the recorder + deframer run against our server while the Phase-4 differ compares to the reference transcript online, surfacing the first divergent opcode as it happens (faster loop than post-hoc diffing).
- **Fuzz (carefully scoped):** mutate non-structural payload fields in replay to probe client tolerance — **only against our own locally-launched client, never against prod.**

**Dependencies:** Phases 4–5. **Risks:** replay fidelity (ISAAC must be driven by the same seeds the client receives in the replayed login response — solvable because the replay server controls the login response); scope (fuzzing stays local-only, §1). Marked stretch; not on the critical path.

---

## 8. Critical path to "full boot transcript"

The single highest-value milestone is the **prod boot transcript** (Phase 5). The shortest path to it:

```
Phase 0 (inject + prod signature/target strategy)
   → Phase 1 game-stream interpose + deframer, validated on OUR server   [de-risk spine]
      → Phase 3 / T8 login-block + seeds hook, validated on OUR server
         → Phase 5 prod capture  ← FULL BOOT TRANSCRIPT (the goal)
            → Phase 4 conformance + Phase 6 feedback (close world-login)
```

Phase 2 (TLS) and Phase 7 (replay/fuzz) are **off** the critical path. The reason world-login closes "at a stroke": Phase 5's transcript directly contains the world-login response byte layout, the ISAAC engagement boundary, the world-init ServerProt sequence, and REBUILD op-81 coords — the exact unknowns we are currently reverse-engineering from crashes.

---

## 9. Rough effort / sizing

| Phase | Scope | Rough size |
|---|---|---|
| 0 | New crate skeleton + `build-mac.sh` (clone the patcher's), ctor + one interpose, prod signature/target audit | **S** (1–2 days; most is cloning proven patcher scaffolding) |
| 1 | recv/send/connect/close interpose + boundary index; offline deframer (port `LoginProxy` logic); our-server validation | **M–L** (3–5 days; deframer port is the bulk, but the logic already exists) |
| 2 | TLS symbol audit + SSL_* interpose/hook; redaction | **M** (2–3 days; risk is the symbol unknown, §5) |
| 3 | T8 inline hook siting + full login-block dump + annotation | **M** (2–3 days; the inline hook is the only novel-risk code) |
| 4 | Conformance differ (two-transcript align + report) | **M** (2–3 days) |
| 5 | Prod capture run (mostly operational + §10 signing + §4 prod signatures) | **S–M** (1–2 days if signing/targets cooperate; +RE-agent time if the build differs) |
| 6 | Feedback to server (other agents) + reference transcripts + gate | **M** (ongoing; depends on networking agent) |
| 7 | Replay/fuzz (stretch) | **L** (deferred) |

Critical path to full transcript ≈ Phases 0+1+3+5 ≈ **~1.5–2 weeks** of focused work, dominated by the deframer port (which is de-risked by the existing proxy) and the one inline hook.

---

## 10. Codesign / injection on the prod client (gating risk)

**Staging is confirmed unsigned** (`codesign -dv rs2client.948-5-mac` → "code object is not signed at all"), so `DYLD_INSERT_LIBRARIES` works freely — the patcher proves it. **The prod client's signature is the open question that gates Phase 5** and must be checked in Phase 0:

- **If prod is unsigned / adhoc-signed, no hardened runtime, no library validation:** insert works exactly like staging. Best case; nothing extra needed.
- **If prod is Developer-ID-signed *with* `runtime` hardened flag and/or library validation:** macOS blocks `DYLD_INSERT_LIBRARIES` unless the inserted dylib is signed by the same Team ID, OR the main binary carries `com.apple.security.cs.disable-library-validation` / `com.apple.security.cs.allow-dyld-environment-variables` entitlements. Check with `codesign -d --entitlements -` and `codesign -dv | grep -i 'flags='`.
  - **Mitigation A (preferred for RE):** make a local copy of the prod binary, strip its signature (`codesign --remove-signature`) or re-adhoc-sign it (`codesign -f -s -`), and launch that copy. For interop RE on our own machine this is the simplest path and mirrors how the patcher runs on the adhoc staging binary. (Note: re-signing changes the binary; confirm the client doesn't self-verify its own signature — if it does, see Mitigation C.)
  - **Mitigation B:** re-sign the prod binary adding `--entitlements` with `disable-library-validation` + `allow-dyld-environment-variables` and adhoc-sign the recorder dylib — keeps the dylib insert path working without stripping.
  - **Mitigation C (if the client self-checks integrity or signature):** avoid touching `__TEXT` — go interpose-only (no T8 trampoline) and extract seeds via a non-code-patching route, or run the prod client under a debugger/`task_for_pid` based injector that doesn't require re-signing. Flagged; assess only if A/B fail.
- **SIP / `DYLD_*` stripping:** macOS strips `DYLD_*` from processes spawned by protected/`open`/LaunchServices paths — that's why `run-client-mac.sh` direct-execs (lines 90–92). For the prod launcher chain, we may need to launch the client directly (bypassing its launcher) to keep `DYLD_INSERT_LIBRARIES` set, or set it on the launcher and let it propagate (verify it isn't stripped).

**Decision:** the signature/entitlement audit is a Phase-0 deliverable precisely so this risk surfaces at the start, not when we're ready to capture prod.

---

## 11. Rosetta inline-hook robustness (the core technical risk)

Under Rosetta 2 the x86_64 client is **ahead-of-time translated** to arm64 (cached in `/var/db/oah/...`). The hazards and mitigations:

- **Interpose taps (T1–T6): no hazard.** dyld rewrites data pointers; Rosetta translates whatever the call lands on. There is no instruction-stream patch to race against translation. *This is why we put the entire game-stream + TLS capture on interpose.*
- **Inline hooks (T7/T8): the real hazard.** We write a `JMP` into `__TEXT`. Two sub-risks:
  1. *Translation timing:* if Rosetta has already AOT-translated the function before we patch it, the cached translation won't reflect our patched bytes. **Mitigation:** install the hook from the **`#[ctor]`, before first translation** — the patcher already relies on dyld running inserted-dylib ctors before the client's `__mod_init_func` (`lib.rs` lines 13–17), and the login-block builder/`Isaac::Init` are not called until well after startup (login time), so the ctor-time patch lands long before first execution → first translation sees patched bytes. We additionally `sys_icache_invalidate` (patcher already does, `lib.rs` line 561) and can force Rosetta cache invalidation by patching before any execution.
  2. *Trampoline correctness under translation:* a hand-rolled x86_64 trampoline that relocates the overwritten instructions must be correct x86_64 that Rosetta can translate. **Mitigation:** keep the hook minimal — overwrite at an instruction boundary, save/restore the exact displaced instructions, and prefer a hook *point* whose first instructions are simple (no RIP-relative operands to relocate). Validate on our-server (Phase 1/3) where any corruption shows immediately, before prod.
- **Concrete fallbacks (so inline-hook risk never blocks the milestone):**
  - **Seeds without any inline hook:** on our server we know the seeds (server log) — Phase 1 path (1). For prod, T8 is the one hook; if it can't be sited, fall back to the Ghidra-import re-derivation (§4.3) rather than a riskier hook.
  - **Login block without a full trampoline:** if trampolining the builder is too risky, an alternative is interposing the **RSA function the builder calls** (if it's a known symbol like an OpenSSL `RSA_public_encrypt` / BoringSSL equivalent) — that gives us the plaintext at the encrypt boundary via an *interpose*, not a code patch. Whether this symbol is interposable is part of the §5 TLS/crypto symbol audit. **This is the preferred T8 implementation if the symbol exists, because it converts a medium-risk inline hook into a zero-risk interpose.**

**Bottom line:** the plan is structured so that **the only Rosetta-risky code (one inline hook) is (a) validated on our server before prod, (b) replaceable by an interpose if the RSA symbol is exported, and (c) backed by a Ghidra-import fallback.** Everything else is interpose and carries no Rosetta hazard.

---

## 12. Validate-on-our-server-first methodology (cross-cutting)

Every phase is proven against our own server before prod, because **on our server we know every byte and every seed**:

- Phase 1: deframer output must re-serialize to our server's exact sent bytes; seeds-from-hook must equal server-logged seeds.
- Phase 2: captured `jav_config.ws` must equal our served config / `jav_config_live.ws`.
- Phase 3: captured login block must decode to the fields our `LoginServer` parses.
- Phase 4: differ must flag a known-bad build and pass a known-good one.
- Only Phase 5 points at prod, and only after 0–4 are green.

This converts "does the recorder work?" from a prod gamble into a closed-loop test against a known oracle.

---

## 13. Capture-artifact handling (secrets)

- Capture output dir (raw streams, seeds, login blocks, TLS logs) is **`.gitignore`'d**; never committed except deliberately-redacted reference transcripts (Phase 6).
- **Redaction pass** before any artifact is shared/committed: strip TLS auth bearer tokens, OAuth codes, session IDs, and account identifiers from TLS logs and from the login-block dump (the session key + credential fields). Game-stream protocol payloads are not secrets and are retained for diffing.
- Seeds (`isaac-keys.txt`) are per-session ephemeral; retain only as needed for offline deframing, and treat as sensitive (they decrypt that session's opcodes).
- No sensitive values to stdout (the patcher already avoids logging key material; recorder follows suit).

---

## 14. Explicit assumptions flagged for adversarial review

1. **Prod client signature is unknown.** Staging is confirmed unsigned; prod must be audited in Phase 0. If hardened-runtime + library-validation, Phase 5 needs §10 mitigation. *(High-impact unknown.)*
2. **TLS stack / `SSL_*` symbol export is unknown.** §5 — drives whether TLS capture is interpose (easy) or inline-hook (risky) or skipped. *(Medium; TLS is off the critical path.)*
3. **The client uses `recv`/`send`/`read`/`write`** (not exclusively `recvmsg`/`readv`). Must confirm by `dtruss`/`fs_usage` in Phase 0/1. If it uses the vector/msg variants, interpose those too. *(Medium; verifiable cheaply.)*
4. **The login-block builder can be hooked at a clean pre-RSA point** in the prod build; signatures (§4.2) hold or the Ghidra fallback applies. The preferred implementation interposes the RSA encrypt symbol instead, contingent on (2). *(Medium.)*
5. **Rosetta ctor-time hook lands before first translation** of the (late-called) login/ISAAC functions. Strongly supported by the patcher's working ctor-ordering guarantee, but inline-hook execution under Rosetta is validated on our server before prod. *(Low-medium.)*
6. **ISAAC `+50` and the deframer logic are correct** — inherited from the working proxy and `docs/net/framing.md`/`tcpin-isaac-decoding.md`; `EnvVars.ISAAC_DELTA = 50` (confirmed). *(Low.)*
7. **948 opcode ranges:** C2S 0–129 (confirmed by prompt); S2C per the 948 `serverProt` table/`register948()`. The transcript treats bytes as authoritative and flags codec disagreements as findings. *(Low.)*
8. **Prod build may differ from any analyzed build**, requiring an RE-agent pass to produce hook signatures before Phase 5. This is a named cross-agent dependency. *(Medium.)*

---

## 15. Summary of deliverables by phase

- **P0:** `client/launcher/recorder-mac/` crate + `build-mac.sh`; injection proof; prod signature/target audit doc.
- **P1:** game-stream interpose dylib taps; offline deframer in `tools/`; our-server `transcript.jsonl`; seeds-match report. **(De-risk spine.)**
- **P2:** TLS capture (if symbols cooperate); redacted TLS logs.
- **P3:** login-block dump + annotated layout; the `success=9` fix input.
- **P4:** conformance differ + `conformance-report`.
- **P5:** `prod-transcript.jsonl` — **the full boot ground truth.**
- **P6:** server fixes (networking agent) + doc updates (RE agent) + committed redacted reference transcripts + conformance gate.
- **P7 (stretch):** replay/fuzz harness.
