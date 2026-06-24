# Adversarial Review — `libdarkan_recorder.dylib` Boot-Transcript Recorder

**Reviewer role:** skeptical senior systems/RE engineer.
**Subject:** `docs/tools/recorder-plan.md` (16 sections; 8 author assumptions in §14).
**Date:** 2026-06-21.
**Method:** empirical probing of the actual binaries with `nm`/`otool`/`codesign`, plus a
**live injection test** of a hand-built x86_64 interpose dylib into the real staging client under
Rosetta 2 on this machine (Apple M1 Max). Findings below cite the commands and their output.

---

## TL;DR verdict

**Proceed with amendments.** The plan's central bet — that DYLD interpose of `recv`/`send`/etc.
captures the game stream with zero code-patching risk — is **empirically TRUE on this binary under
Rosetta** (I proved it fires). The interpose half is sound. But four of the plan's load-bearing
claims are wrong or unverified, and the prod-capture framing (the highest-value milestone) rests on
a prerequisite the plan understates. Net: the *mechanism* is viable; the *critical path and several
fallbacks are mis-stated* and need correcting before work starts. Critically, the recorder is
**probably not the fastest path to the immediate world-login blocker** — see §(b).

---

## (a) Is the core interpose approach viable on this binary? — **YES, proven.**

This was the #1 risk in the prompt and it is **resolved in favor of the plan.**

**Evidence — symbol shape.** Both candidate clients import the socket functions as ordinary
two-level-namespace undefined symbols bound through lazy stubs that dyld can rebind:

```
$ nm -mu rs2client.948-5-mac | grep -E '_recv|_send|_read|_write|_connect|_close'
   (undefined) external _close   (from libSystem)
   (undefined) external _connect (from libSystem)
   (undefined) external _read    (from libSystem)
   (undefined) external _recv    (from libSystem)
   (undefined) external _recvfrom(from libSystem)
   (undefined) external _send    (from libSystem)
   (undefined) external _sendto  (from libSystem)
   (undefined) external _write   (from libSystem)
$ otool -Iv rs2client.948-5-mac   # indirect (stub/lazy) symbol table
   ... _connect, _recv, _send, _write all present in __stubs + __la_symbol_ptr ...
```

No `recvmsg`/`sendmsg`/`readv`/`writev` are imported at all — only `recv/recvfrom/send/sendto/
read/write`. (Assumption §14.3 partially confirmed: the vector/msg variants don't even exist as
imports, though `recvfrom`/`sendto` do — interpose those too, see deframer notes.) Same shape on
the prod 948 client `~/Jagex/launcher/rs2client`.

**Evidence — live interpose actually fires under Rosetta.** I built a minimal x86_64 dylib with
`__attribute__((section("__DATA,__interpose")))` pairs for `connect` and `recv`, adhoc-signed it,
and injected it into the real staging client:

```
$ env HOME=$scratch DYLD_INSERT_LIBRARIES=/tmp/.../libprobe.dylib DYLD_PRINT_INTERPOSING=1 \
      rs2client.948-5-mac "rs-launch://127.0.0.1:8829/jav_config.ws"
dyld[...]: libprobe.dylib has interposed '_connect' to replacing binds to 0x7FF810513238 with 0x10EA204C0
dyld[...]: libprobe.dylib has interposed '_recv'    to replacing binds to 0x7FF8103F8106 with 0x10EA20570
# and my replacement functions WROTE THE MARKER FILE:
FIRED.log:  ctor-loaded pid=...
            connect pid=...
            recv pid=...
```

So on an Apple M1 Max translating the x86_64 client, dyld **registered** the interposes and the
client **actually called** `connect` and `recv` through them during boot. The game socket I/O is
NOT done via raw `syscall()`, a statically-inlined path, or a libSystem-bypassing custom
`jag::Socket`. **T1–T4 (the bulk of the value) are viable. This is a green light.**

> Caveat worth recording: modern clang put the section in **`__DATA_CONST,__interpose`**, not
> `__DATA,__interpose` as the plan (§3.1) and the proxy comments state. dyld honors both for an
> *inserted* dylib (it applies interposing before sealing `__DATA_CONST`), and my test used the
> classic `__DATA,__interpose` and still worked — but the recorder's Rust `#[link_section]` must
> use whatever the toolchain actually emits; verify with `otool -l` on the built dylib, don't
> hardcode the segment name.

---

## Issue-by-issue findings

### B1 — [BLOCKER, corrected] The §11 "interpose the RSA encrypt symbol" fallback is **impossible** — no such symbol exists. The prod seeds therefore *require* the one inline hook (or a Ghidra-derived offset). The plan's framing that T8 is "the only risky surface, and even it can be an interpose" is false.

The plan repeatedly de-risks T8 by claiming (§11 bullet "Login block without a full trampoline",
§14.4) that the *preferred* implementation is to **interpose the RSA encrypt symbol** (an OpenSSL
`RSA_public_encrypt` / BoringSSL equivalent), converting the medium-risk inline hook into a
zero-risk interpose. **This symbol does not exist in the client.**

Evidence:
```
$ nm -m rs2client.948-5-mac | grep -iE 'ssl|tls|boring|openssl|RSA_|BN_mod|EVP_|crypto'
   (no output — zero matches)
$ otool -L rs2client.948-5-mac
   /usr/lib/libSystem.B.dylib
   /usr/lib/libcurl.4.dylib        # <-- system curl (SecureTransport-backed on macOS)
   /usr/lib/libz.1.dylib
   ... GL/AV/Cocoa frameworks ...   # NO libssl / libcrypto / BoringSSL
```
And `docs/binary/patch-targets-948-mac.md` confirms RSA is Jagex's **own inlined
`jag::math::BigInteger`**: the login modulus is parsed with radix `0x10` through the C++ static
initializers and consumed by `jag::LoginManager`'s RSA-packet builders (ModPow inlined). There is
no library RSA call to interpose.

**Consequence:** capturing the **prod** ISAAC seeds (and the pre-RSA login block) has exactly two
options, both of which the plan treats as fallbacks rather than the primary:
1. Inline-hook `SendLoginPacket_xplat948` (T8) — the genuine medium-risk trampoline; **the doc
   already has the exact site**: `PADDD XMM0,[0x100ad7b50]` at `0x1000cb894`, client seeds read
   from `[R14+0x48]`, then `jag::Isaac::Init(server_seeds)` (`patch-targets-948-mac.md` §P4). On
   prod (rev 947) the address differs and must be re-derived by the RE agent.
2. Don't hook at all; read the seeds out of process memory after the client computes them
   (e.g. an interpose of `connect`/first-`send` that then peeks `[R14+0x48]`-derived state) — but
   that needs the same RE work to know where the seeds live, so it's not actually cheaper.

**Correction:** delete every reference to "interpose `RSA_public_encrypt` / BoringSSL" as a T8
implementation (§3.2, §11, §14.4). State plainly: **prod seeds require either the T8 inline hook or
a Ghidra-derived in-memory read; there is no interpose escape hatch for RSA/seeds.** This makes
§14.4's "preferred implementation interposes the RSA encrypt symbol" assumption **dead on arrival**.

### B2 — [BLOCKER for the stated goal] The acknowledged tension in the prompt is real and the plan half-buries it: the offline deframer is **useless for the game stream without the 4 seeds**, so at least one inline hook (or RE-derived seed read) is load-bearing for the entire prod game-stream transcript — the thing the project is for.

The plan says (§3, T7) `Isaac::Init` is "optional, not load-bearing" and (§0/§3.1) that capturing
raw `recv`/`send` "loses nothing" because structure is recoverable offline from the seeds. Both
statements are true *only if you have the seeds*. For the **prod** capture (Phase 5, the
"highest-value milestone") you do **not** know the seeds from any server log — the whole point is
that Jagex's server picked them. So:

- On **our server** (Phases 1/3): seeds come from our server log → interpose-only is genuinely
  sufficient. The plan is right here.
- On **prod** (Phase 5): the raw `recv`/`send` bytes are **un-deframable** without T8 (or the
  RE-derived seed read). So T8 is **load-bearing for the only deliverable that justifies the
  project**, not a "belt-and-braces cross-check."

The plan does eventually concede this in R5.2 ("if T8 can't be sited on prod… cannot deframe
opcodes without the prod seeds") — but the headline framing (§3.2 "we can often skip T7 entirely",
§8 critical path) underplays it. **Correction:** promote "prod seeds via T8 (or RE-derived read)"
to a **hard, on-critical-path prerequisite for Phase 5**, co-equal with the §10 signing resolution,
and stop calling the seed hook "optional." Phase 5 has *two* gates, not one.

### B3 — [MAJOR] The "prod" client the plan/prompt point at is a **different revision (947)** than staging/the live client (948). There are at least 3 revisions on disk. This breaks the deframer's 948 size tables and the §4 signature reuse.

Revision fingerprints (login-modulus prefix; `8f389edb`=947, `aad4a780`=948):
```
~/rs-re-staging/rs2client.948-5-mac          => aad4a780  (948-5)   [staging]
~/Jagex/launcher/rs2client                   => aad4a780  (948)      [REAL live-launcher client]
~/darkan-3/macos/Jagex/launcher/rs2client    => 8f389edb  (947)      [the prompt's "prod" path]
~/darkan-3/live-trace/.../rs2client          => 8f389edb  (947)
~/projects/reclass-data/rs2client.947-3      => (947)
```
The prompt called `~/darkan-3/macos/Jagex/launcher/rs2client` the "prod" client; it is actually a
**stale 947 custom-mode copy**. The genuinely-current client the Jagex launcher downloaded is
`~/Jagex/launcher/rs2client` (948, **unsigned** — `codesign` says "code object is not signed at
all"). This matters three ways:

1. **Deframer (concern #6):** `register948()` opcode/size tables will **mis-frame a 947 stream**
   wherever 948 differs (and `docs/net/948-5-delta-from-948-2.md` shows the tables *do* drift even
   between 948 point releases). If you capture against a 947 client you must deframe with a 947
   codec, which the project may not have. **Decide which revision the prod capture targets and use
   the matching codec.** The right target is the **948 live client** `~/Jagex/launcher/rs2client`,
   because (a) it matches `register948()` and (b) it's what the live launcher actually runs.
2. **§4 hook signatures:** the `0x1000cb894` PADDD address in `patch-targets-948-mac.md` is a
   *948-5* address. It will not be at that VA on the 947 client and may not be on plain 948 either.
   The pattern-scan anchor (the `[50,50,50,50]` const + the PADDD-from-rip) is the durable part and
   should hold, but **this needs an RE-agent pass against the exact capture-target binary** before
   Phase 5 (the plan says this in §4.3/§14.8 — good — but it's now clearly *required*, not
   contingent, because the target is a different rev than the one analyzed).
3. **Build fingerprint table (§4.3):** the plan's "login-modulus-prefix fingerprint" scheme is
   sound and is exactly how `build-mac.sh` already distinguishes 947 from 948. Keep it.

### B4 — [MAJOR] The prod-capture **prerequisite** (concern #4) is real and under-weighted: reaching a live WORLD login needs a working Jagex auth/session, and the launcher path that provides it has **hardened runtime with zero entitlements**, which strips `DYLD_*`.

Two coupled problems:

**(i) DYLD stripping on the launcher path.** The Jagex launcher
`/Applications/RuneScape.app/Contents/MacOS/RuneScape` is Developer-ID-signed by Jagex
(`TeamIdentifier=UG2BR73SV5`) **with hardened runtime and no entitlements at all**:
```
$ codesign -dvvv /Applications/RuneScape.app/Contents/MacOS/RuneScape
   flags=0x10000(runtime)         # hardened runtime ON
   Authority=Developer ID Application: Jagex (UG2BR73SV5)
$ codesign -d --entitlements <file> ...   => 0-byte entitlements   # NONE
```
No `com.apple.security.cs.allow-dyld-environment-variables` ⇒ macOS **strips
`DYLD_INSERT_LIBRARIES` from the launcher process**, and a normally-spawned child inherits a clean
(stripped) environment. So you **cannot** inject by setting `DYLD_*` and clicking the Jagex
launcher. The plan's §10 lists this; the mitigation that actually works is **launch the downloaded
`rs2client` directly** (à la `run-client-mac.sh`, which already direct-execs precisely because
`open`/LaunchServices strips `DYLD_*`). The downloaded client itself is **unsigned** (proven
above), so direct-exec injection works on it.

**(ii) But direct-exec means you must reproduce the launcher's job: a valid live session.** A real
**world** login (the capture we actually want) requires the client to first complete Jagex
auth/lobby with a valid session token — normally the launcher's responsibility (OAuth/Jagex
account). The user has previously wanted to *avoid* the Jagex launcher. To capture a live world
boot you need *either*:
  - the Jagex launcher to produce the session, then hand off to a directly-exec'd client carrying
    that session (non-trivial: the launcher passes session state via the `configURI`/env it sets on
    its child — which is exactly the child you can't easily interpose if the launcher spawns it), or
  - a headless reproduction of the Jagex OAuth/session flow feeding a direct-exec client (the
    launcher's `client/launcher/src/auth/` already does OAuth — this is the realistic route).

**This is the real gating risk for Phase 5, larger than signing.** The plan's §1/§5/§14 treat "own
account, observe-only" as settled and never confronts *how the unmodified client reaches a live
world under direct-exec injection*. **Correction:** add an explicit Phase-0 spike: "can a
`DYLD_INSERT_LIBRARIES`-injected client complete a real lobby+world login to live Jagex at all on
this setup?" If that spike fails, the highest-value capture (world-login ISAAC boundary) is
**unreachable by this tool** and the whole prod-capture rationale collapses to lobby/JS5 only —
which does *not* contain the world-init sequence we're stuck on. Resolve this before investing the
~2 weeks.

### B5 — [MAJOR] The recorder is **probably not the fastest path** to the immediate blocker, and the plan never honestly does the build-vs-buy. We already own most of the deframer.

The immediate wall (per the prompt) is **our own server's** world-login response framing + the
inbound ISAAC engagement boundary (garbage opcodes 162/200). Assess the options:

- **(d) `LoginProxy` against our own server / existing harnesses — already built.** The repo
  already contains: `LoginProxy.kt` (full bidirectional ISAAC deframer with the exact
  partial-read/2-byte-opcode state machine), `WorldLoginProbe.kt` (503 lines — a *headless
  world-login handshake simulator* that already runs the type-14 → opcode-16 → result flow at the
  wire level), `FramingRegression.kt` (309 lines — a bidirectional ISAAC framing **oracle** that
  asserts `bytesConsumed == totalBytes` and pinpoints the offending opcode/offset/size), plus
  `DecodeCapture` and `WireFormatVerify`. The recorder's "offline deframer" (§6) is **a
  near-duplicate of `FramingRegression`/`DecodeCapture`.** For the *our-server* blocker, you don't
  need a new in-process dylib at all — you need a known-seed capture of our server's world-login
  bytes (which our server logs) fed through the framing oracle we already have. That's hours, not
  weeks.
- **(a) RE the ISAAC engagement boundary in Ghidra directly.** The boundary is "the precise point
  where opcode obfuscation begins" — i.e., *when* `jag::Isaac::Init` is called relative to the
  world-login data block, and whether the first post-login S2C/C2S byte consumes an ISAAC value.
  This is a single, bounded RE question the ghidra-reverse-engineer agent can answer from
  `jag::LoginManager` world-login steps + the `Isaac::Init` call site (already located at the P4
  region). That likely resolves "consuming an ISAAC value one packet too early/late" **directly**,
  without any capture.
- **(b) Linux/Windows client capture.** Injection is *easier* on Linux (`LD_PRELOAD`, the existing
  `patcher/`) and the framing is identical. If a capture is wanted, a Linux client + `LD_PRELOAD`
  recorder is lower-friction than fighting macOS hardened-runtime/Rosetta — though it shares B4's
  "needs a live session" problem.
- **(c) OpenRS2 / community captures.** Not assessed in the plan at all; worth a look for a
  reference 948 boot before building bespoke tooling.

**The recorder's unique value is narrow but real:** a *prod* (Jagex-server) ground-truth transcript
that none of the above produce, *if* B4 is solvable. But the plan oversells it as the fastest route
to the *immediate* blocker. **Correction:** reorder. First spend a day on (a)+(d) — RE the boundary
and run our-server bytes through the existing oracle. Only build the recorder if that fails to
close world-login, and scope it explicitly to "prod ground truth" (contingent on B4), not "the
fastest fix for 162/200."

### B6 — [MAJOR→resolved-as-favorable] TLS capture (§5, Phase 2) is **Branch C only** (SecureTransport via libcurl), and `SSL_read`/`SSL_write` interpose (Branch A) is **dead**.

Same evidence as B1: zero `SSL_*` symbols; TLS is `libcurl.4.dylib` → on macOS that's
SecureTransport (`Security.framework`), so the relevant symbols would be `SSLRead`/`SSLWrite`
(deprecated SecureTransport) or a `Network.framework`/`nw_` path — **not** OpenSSL. The plan lists
Branch C as a possibility; the evidence says it's the *only* possibility, and even SecureTransport
may be reached through curl's internal function pointers rather than a clean interposable export.
**This is fine** because the plan correctly marks TLS as off the critical path (Branch D: we already
have `jav_config_live.ws` and a working OAuth flow). **Correction:** rewrite §5 to state Branch C is
the only live option, lower Phase 2 further (or cut it), and stop implying `SSL_*` interpose is
likely.

### B7 — [MINOR] Deframer correctness gaps (concern #6) — mostly handled by the ported logic, but enumerate the multi-fd/multi-socket case explicitly.

The ported `LoginProxy` logic already handles the two hard cases well: TCP segmentation across
`recv()` boundaries (the `s2cAccum`/`c2sAccum` + `pendingOpcode`/`pendingSize` state) and the
1-vs-2-byte opcode rule with correct double-ISAAC-consume avoidance
(`processPostLoginS2C`/`parseClientPostLogin`). Two things to nail down:

- **Concurrent fds / multiple sockets.** JS5, lobby, and world live on *different* sockets, and
  the lobby→world hop **reuses or swaps** the connection. The plan's R1.2 fix (key ISAAC state on
  `(connection-epoch, fd)`, reset at each `connect`, bracket with `connect`/`close`) is correct in
  principle but is the part with **no prior art in the proxy** (the proxy is a single MITM pipe, not
  a multiplexed per-fd tap). This is the one genuinely new piece of deframer logic and should be
  called out as such, with a test: interleaved JS5 + lobby traffic on two fds must deframe
  independently. JS5 is **not** ISAAC-obfuscated and must be tagged by fd (via the `connect`
  host:port map, T3) and routed to a *non*-ISAAC deframer, or it'll be garbage-"decoded."
- **Interleaving within a single `recv`.** Handled by the accumulator model; fine.
- **Revision/size-table match.** See B3 — the deframer codec must match the captured client's rev.

### B8 — [MINOR] Rosetta inline-hook timing claim (§11/§14.5) is plausible but **unproven**; the interpose claim is now proven, the trampoline claim is not.

§11 argues the ctor-time inline hook lands before Rosetta AOT-translates the (late-called)
login/ISAAC function, so first translation sees patched bytes. That's a reasonable theory and the
patcher's existing `VM_PROT_COPY` + `sys_icache_invalidate` data-patch path works — but **writing a
control-flow `JMP` trampoline into `__TEXT` of a Rosetta-translated process is materially different
from patching a static immediate**, and nobody has demonstrated it on this client. Rosetta caches
translations keyed on the original bytes in `/var/db/oah`; a stale cached translation of the hooked
function is a real failure mode the `sys_icache_invalidate` (an *Apple-Silicon-native* I-cache op)
does **not** address for the *translated* code. **Correction:** keep §14.5 at "medium," not "low,"
and add a Phase-0/1 spike that installs a *no-op* trampoline (jump to a stub that just jumps back)
into a known late-called function on our-server runs and confirms the client survives — before
relying on T8. If the trampoline approach proves flaky under Rosetta, fall back to the in-memory
seed read (B1 option 2) rather than control-flow patching.

### B9 — [MINOR] §10 Mitigation A/C self-verification worry is moot for the realistic target.

§10 frets about re-signing a hardened/library-validated prod client and whether it self-verifies.
Moot: the actual capture-target client (`~/Jagex/launcher/rs2client`, 948) is **unsigned**, so no
re-sign is needed — direct-exec injection works as on staging. The only hardened-runtime binary in
the chain is the **launcher** (B4), and you solve that by not routing through it, not by re-signing
it. Simplify §10 accordingly.

### B10 — [MINOR] Scaffolding reuse is accurate and low-risk.

The claim that the recorder is a clean sibling of `patcher-mac/` (reuse `find_rs2client_segments`,
the `#[ctor]` ordering guarantee, `build-mac.sh`'s x86_64 guard + adhoc-sign + multi-slot deploy)
checks out against `lib.rs` and `build-mac.sh`. For **interpose-only** taps you don't even need the
`mach_vm_protect`/dyld-image machinery — interpose needs no image base, no `__TEXT` write. Good.
Note `build-mac.sh`'s deploy fan-out and 947/948 marker guards are patcher-specific; the recorder's
build script should *not* blindly copy the RSA-marker preflight (the recorder has no RSA string).

---

## (b) Corrected critical path to the world-login fix

The plan's critical path (§8) is `P0 → P1 → P3/T8 → P5(prod) → P4/P6`. That optimizes for "prod
ground truth," which (per B4/B5) is both gated on an unproven prerequisite and slower than
necessary for the *immediate* blocker. Corrected, fastest-first:

1. **RE spike (ghidra-reverse-engineer), ~0.5–1 day, no code:** document the exact ISAAC
   *engagement boundary* for world-login — when `jag::Isaac::Init` runs relative to the world
   login-data block, and whether the first inbound post-login byte consumes an ISAAC value. This is
   the direct cause of the 162/200 garbage. Output: a precise note in `docs/net/`.
2. **Our-server framing oracle, ~0.5 day, reuse existing tools:** capture our server's world-login
   S2C/C2S bytes (server already logs the seeds) and run them through the **existing**
   `FramingRegression`/`DecodeCapture` with the boundary from step 1 as a tunable. Confirm a clean
   decode (`bytesConsumed == totalBytes`). This validates the fix *against our own stack* with zero
   new in-process code. If steps 1–2 close world-login, **stop — you didn't need the recorder.**
3. **Only if 1–2 are insufficient:** build the recorder, but **interpose-only first** (T1–T4 + T3
   host:port map), validated on our server against the seed log (no inline hook). This gives a
   faithful raw capture and exercises the multi-fd deframer (B7).
4. **Prod ground truth (the recorder's unique value) — gated on resolving B4 first:** run the
   Phase-0 "can an injected client reach a live world at all?" spike. If yes, add the T8 inline
   hook (or RE-derived seed read) located by signature against the **948 live client**
   (`~/Jagex/launcher/rs2client`), capture, deframe with `register948()`, diff against our server.
   If the B4 spike fails, prod world-login capture is **out of reach** — descope to lobby/JS5 and
   rely on steps 1–3 for world-login.

The differ/conformance harness (Phase 4) and feedback loop (Phase 6) are fine as stated and remain
the long-term payoff.

---

## (c) The author's 8 assumptions (§14) — which survive scrutiny

| # | Assumption | Verdict | Evidence / correction |
|---|------------|---------|------------------------|
| 1 | Prod client signature unknown; audit in P0 | **PARTLY WRONG (favorable)** | Already auditable now. The real 948 live client `~/Jagex/launcher/rs2client` is **unsigned** → injectable. The hardened-runtime binary is the **launcher**, not the client (B4/B9). The path the prompt called "prod" is a stale 947 copy (B3). |
| 2 | TLS stack / `SSL_*` export unknown | **WRONG (now known)** | Zero `SSL_*` symbols; TLS = libcurl→SecureTransport. Branch A dead; Branch C only. TLS off critical path, so impact is low (B6). |
| 3 | Client uses `recv`/`send`/`read`/`write` not msg/vector variants | **CONFIRMED (proven live)** | `nm`/`otool` show only `recv/recvfrom/send/sendto/read/write`; no `recvmsg`/`readv`. Live test fired `recv`+`connect`. Must also interpose `recvfrom`/`sendto` (they're imported) (a)/(B7). |
| 4 | Login-block hookable at clean pre-RSA point; **preferred = interpose RSA encrypt symbol** | **HALF WRONG (BLOCKER)** | The "interpose RSA encrypt symbol" preference is **impossible — no such symbol** (B1). Inline-hook siting itself is plausible (the P4 site is documented) but unproven under Rosetta (B8) and address-shifted on the 947/948 target (B3). |
| 5 | Rosetta ctor-time hook lands before first translation | **UNPROVEN — keep at medium** | Interpose proven; **trampoline not** (B8). Add a no-op-trampoline spike before trusting T8; the data-patch precedent doesn't transfer to control-flow patching of translated code. |
| 6 | ISAAC `+50` and deframer logic correct | **CONFIRMED** | `EnvVars.ISAAC_DELTA=50` (`EnvVars.kt:89`); `patch-targets-948-mac.md` §P4 shows the `PADDD [+50]` derivation; proxy deframer is the proven reference. |
| 7 | C2S 0–129; S2C per `register948()`; bytes authoritative | **CONFIRMED (with B3 caveat)** | Sound — *provided the capture target is a 948 client*. A 947 capture needs a 947 codec (B3). |
| 8 | Prod build may differ; needs RE pass for signatures before P5 | **CONFIRMED & now mandatory** | The intended target is a different rev than any analyzed binary, so the RE-agent signature pass is **required**, not contingent (B3). Promote from "named dependency" to "hard P5 gate." |

Net: **#3 and #6 fully survive; #7 survives with a caveat; #1, #2, #4, #5, #8 are wrong or need
material correction** — and #4's headline ("preferred implementation interposes the RSA symbol") is
a blocker because the symbol doesn't exist.

---

## (d) Final verdict — **proceed with amendments**

The interpose mechanism is **empirically validated** on this binary under Rosetta and is the right
backbone. But adopt these amendments before building:

1. **Reorder the critical path (B5/(b)):** do the RE boundary spike + our-server framing oracle
   (using the *existing* `FramingRegression`/`WorldLoginProbe`/`DecodeCapture`) **first**. The
   immediate 162/200 blocker is very likely solvable there in ~1 day with no new dylib. Build the
   recorder only if that fails, and scope it to its unique value (prod ground truth).
2. **Resolve B4 before committing to Phase 5:** run a Phase-0 spike proving an injected client can
   reach a **live world** login at all on this setup (given hardened-runtime launcher strips
   `DYLD_*`, and direct-exec needs a real Jagex session). If it can't, prod world-login capture is
   out of reach — descope.
3. **Fix the seed/RSA story (B1/B2):** delete the "interpose RSA encrypt symbol" fallback (no such
   symbol). State that prod seeds **require** the T8 inline hook or an RE-derived in-memory read,
   and put that on the Phase-5 critical path as a second gate alongside signing.
4. **Pin the capture target to the 948 live client** `~/Jagex/launcher/rs2client` (not the stale
   947 `~/darkan-3/macos/...`), and require an RE-agent signature pass against that exact binary
   before Phase 5 (B3/§14.8).
5. **Prove the Rosetta trampoline with a no-op spike (B8)** before relying on T8; keep an
   in-memory-seed-read fallback that avoids `__TEXT` control-flow patching.
6. **Rewrite §5 (B6):** SecureTransport-only; lower/cut Phase 2.
7. **Call out the multi-fd deframer as the one genuinely new piece (B7):** route JS5 (non-ISAAC) by
   fd via the `connect` map; test interleaved two-socket capture.
8. **Minor:** use the toolchain's actual interpose section name (`__DATA_CONST,__interpose` on
   current clang), don't copy the patcher's RSA-marker build preflight, and drop the §10
   re-signing hand-wringing (target client is unsigned).

With those, the recorder is a sound *complement* to the existing tooling for the one thing only it
can produce — a live-Jagex ground-truth transcript — but it should not be the first thing built to
fix world-login.

---

### Appendix — commands run for this review

- `nm -mu` / `otool -Iv` / `otool -L` on `rs2client.948-5-mac`, `~/Jagex/launcher/rs2client`,
  `~/darkan-3/macos/Jagex/launcher/rs2client` — socket/SSL/crypto symbol shape + linked dylibs.
- `codesign -dvvv` / `codesign -d --entitlements` on the three clients and on
  `/Applications/RuneScape.app/Contents/MacOS/RuneScape` (launcher).
- `strings | grep` for login-modulus-prefix revision fingerprints across all on-disk `rs2client`.
- **Live interpose test:** hand-built x86_64 `libprobe.dylib` (`__DATA,__interpose` pairs for
  `connect`+`recv`, adhoc-signed) injected via `DYLD_INSERT_LIBRARIES` +
  `DYLD_PRINT_INTERPOSING=1` into `rs2client.948-5-mac` under Rosetta on Apple M1 Max — confirmed
  dyld registered the interposes and the client invoked both through them during boot.
- Cross-read: `patcher-mac/src/lib.rs`, `build-mac.sh`, `run-client-mac.sh`, `LoginProxy.kt` (full),
  `Codec.kt` size functions, `EnvVars.kt`, `patch-targets-948-mac.md` §P3/§P4, and the existing
  `WorldLoginProbe.kt` / `FramingRegression.kt` headers.
