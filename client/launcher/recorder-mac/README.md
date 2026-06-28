# darkan-recorder-mac — macOS NXT packet recorder dylib

`libdarkan_recorder.dylib` is a macOS-only `cdylib` inserted into the rs2client
process via `DYLD_INSERT_LIBRARIES`, **alongside** the RSA patcher
(`client/launcher/patcher-mac`). It taps the running client's networking in
memory and writes a session-based, line-delimited capture to disk. It is the
mac-native counterpart of the Undercut engine's Linux `ServerPacketCapture` /
`RawLoginDump` hooks (which inject via GDB into a Linux client) — here the same
data is captured in-process on the x86_64 Mach-O build.

It is a **hard no-op unless `DARKAN_RECORD=1`** is set, so it is safe to leave in
`DYLD_INSERT_LIBRARIES`. It never mutates client code beyond its own detour
trampolines and is written so a capture error degrades to a missing line, never
a crash.

## Build

```sh
client/launcher/recorder-mac/build-mac.sh
```

Builds `x86_64-apple-darwin` release and adhoc-signs the dylib (dyld refuses an
unsigned insert library under the hardened runtime). Output:

```
client/launcher/recorder-mac/target/x86_64-apple-darwin/release/libdarkan_recorder.dylib
```

## Run

`run-client-mac.sh` already wires it: set `DARKAN_RECORD=1` (optionally
`DARKAN_RECORD_DIR=...`). It prepends the recorder to `DYLD_INSERT_LIBRARIES`
(before the patcher) and exports `DARKAN_RECORD_DIR`.

Env knobs: `DARKAN_RECORD=1` (gate), `DARKAN_RECORD_DIR` (output dir),
`DARKAN_RECORD_MODE=production|local` (override the mode inference), and
`DARKAN_SNAPSHOT_INTERVAL_MS` (client-state snapshot cadence; default 1000,
clamped 100..60000).

```sh
# LOCAL capture (patched client -> Darkan server):
DARKAN_RECORD=1 ./run-client-mac.sh
# or with a custom output dir:
DARKAN_RECORD=1 DARKAN_RECORD_DIR=~/captures ./run-client-mac.sh

# PRODUCTION capture (pristine client -> LIVE Jagex):
./run-client-mac.sh --production
# (forces DARKAN_RECORD=1, no-ops the patcher, passes no --configURI, and tags
#  the session server_mode=production)
```

`--production` does NOT set `DYLD_FORCE_FLAT_NAMESPACE`: the recorder's
`connect()`/`close()` taps use the `__DATA,__interpose` table (honoured under the
client's two-level namespace without it), and both ctors fire reliably without
it. Forcing a flat namespace on the real RuneScape.app wrapper could break its
own OAuth/TLS, so it is left alone.

## How it works

At load time (`#[ctor]`, before the host's `main()`):

1. **Gate** — exits immediately unless `DARKAN_RECORD=1`.
2. **Process identity** — finds the main executable image (the one with Mach-O
   `filetype == MH_EXECUTE`; NOT dyld index 0, which is the inserted dylib) and
   sig-scans it for the client's capture-point signatures. If they resolve it is
   `rs2client`; otherwise it is the `wrapper` (RuneScape.app), which only gets
   the socket plane + a process event. The DYLD env is inherited across the
   wrapper→client spawn, so both processes load the dylib and self-identify.
3. **Sig-scan** — every hook target is resolved by scanning the loaded Mach-O
   for the unique byte signatures in
   `re-resources/docs/binary/patch-targets-macos-recorder.md` (NOT hardcoded
   offsets — this survives sub-revisions). A signature that matches zero or
   more-than-one site, or lands at an unexpected file offset, is refused.
4. **Hooks** — installs inline detours via a small `iced-x86`-based trampoline
   engine (`src/detour.rs`), the self-contained analogue of the engine's C++
   funchook (the off-the-shelf Rust detour crates require nightly / don't build
   for x86_64-darwin). Code is patched with `mach_vm_protect(VM_PROT_COPY)` —
   the only primitive that can make the main image's `__text` writable under
   **Rosetta 2** (the x86_64 client runs translated on Apple Silicon).
5. **libc interpose** — `connect()`/`close()` are interposed via the
   `__DATA,__interpose` table for connect/disconnect events and per-fd peer
   tracking. (The interposers are pure passthroughs until the ctor finishes
   arming them, so libSystem's own bootstrap `close()` calls are never touched.)

## Capture points

| Plane | Source hook | What it records |
|-------|-------------|-----------------|
| `framed-s2c` | `ConnectionManager::ReadPacket` inline dispatch (`0x6d8ea`), `ReadPacket` entry as backstop | decoded incoming opcode (`conn+0x2C` CURRENT_OPCODE, coherent), size (`conn+0x30`), body (`conn+0x2D0`) |
| `framed-c2s` | `TcpConnectionMessage::Init` + `FlushOutgoingQueue` | pre-encrypt opcode/sizeClass at the Init choke point; the finished body read from the Packet sub-struct at flush |
| `socket` | `ClientStream::Read` (post) / `::Write` (pre) / `ClientStream::Fill` (post) | raw on-the-wire bytes (ciphertext + the RSA login block); `Fill` is the recv funnel for in-game + lobby s2c |
| `event: state_change` | `Client::SetMainState` | game-state transitions (enum 0/10/20/23/30/35/37/40) |
| `event: login_cipher_ready` | `ConnectionManager::SetupLoginCiphers` | the login s2c Phase A→B (plaintext→ISAAC) boundary + the Phase-A byte offset |
| `event: connect/disconnect` | libc `connect`/`close` | peer `ip:port` per connection |
| `state_snapshot` | `ClientStream::Fill` (periodic) + `Client::SetMainState` + at-exit | the client's OWN decoded varps / tile / skills / run energy+weight (the "client is king" oracle) |
| (login anchor) | `LoginStateMachine::OpenLoginStream` | publishes the Client base + login `ClientStream` at socket-creation time so the EARLIEST login s2c is tagged `login` (not `js5`) |
| `prot-table.json` | `g_serverProtTable` direct read (triggered at ctor + the `SetMainState` / s2c-dispatch hooks) | the client's OWN live ServerProt opcode table (opcode → sizeClass + handler fingerprint), dumped once per session for rev-agnostic framing/naming |

Connections are tagged `login` / `game` by comparing the live `ServerConnection*`
against `ConnectionManager+0x18` (game) / `+0x28` (login); the login stream is
ALSO recognised via the `LoginStateMachine` early anchor (RE §8a) so the first
login recv is tagged correctly before `ConnMgr+0x28` resolves. JS5 is HTTP and has
no `ServerConnection` slot, so it appears only on the socket plane (tagged `js5`,
length-only) / as connect events, never as a framed `conn`.

## On-disk format

Everything is written under `$DARKAN_RECORD_DIR` (default
`~/.undercut/recordings`) into one session directory per launch:

```
session-<UTC yyyyMMdd-HHmmss>-<pid>-<mode>/
├── session.json          summary (rewritten at start and at exit)
├── prot-table.json       the client's OWN live opcode table, dumped once at session start (rev-agnostic framing/naming)
├── framed-s2c.jsonl      one flat JSON object per S→C framed packet
├── framed-c2s.jsonl      one flat JSON object per C→S framed packet
├── socket.jsonl          raw ClientStream Read/Write/Fill bytes
├── events.jsonl          state_change / login_cipher_ready / connect / disconnect / process
├── state-snapshots.jsonl client-state ORACLE snapshots (varps/tile/skills/energy) — the "client is king" cross-check
├── blobs/<sha256-prefix>.bin   bodies larger than 8192 bytes
├── raw-<role>-s2c.bin / raw-<role>-c2s.bin   (optional) decrypted-framed bytes per role
└── isaac-keys.txt        (optional) per-(conn,dir) ISAAC construction seeds for cross-validation
```

`mode` (the dir tag) == `server_mode`. It is `local` when the sibling patcher
has redirected the client to the local Darkan server (a `DARKAN_RSA_MODULUS` /
`DARKAN_JS5_RSA_MODULUS` is set, or the configURI is localhost), and
`production` when the pristine client is talking to LIVE Jagex (no modulus, no
local configURI). An explicit `DARKAN_RECORD_MODE=production|local` overrides the
inference.

### `session.json`

```json
{
  "session_id": "session-20260626-215313-99624-local",
  "started_at": "2026-06-26T21:53:13.051Z",
  "build": "RS2Engine-948-NXT-5",
  "server_mode": "local",
  "proc": "rs2client",
  "wrapper_pid": 12340,
  "rs2client_pid": 99624,
  "connections": [{ "role": "game-or-login", "peer": "1.2.3.4:43594", "port": 43594 }],
  "stopped_at": "2026-06-26T21:55:01.220Z",
  "session_stop": true
}
```

### `prot-table.json` (the client's OWN live opcode table — rev-agnostic framing)

Dumped **once per session**, at session start, by reading the client's
`g_serverProtTable` directly out of the running image. The point is that the
offline enricher's **framing** (opcode → sizeClass) and **naming** (handler
fingerprint) come from *the client's own table for this build* — they are
**rev-agnostic** and need no static opcode table baked into the tools.

```json
{
  "build": "RS2Engine-948-NXT-5",
  "image_base": "0x100000000",
  "server_prot": [
    { "op": 13, "size_class": 1,  "handler": "0x1000448a0", "handler_sig": "554889e5415741564154..." },
    { "op": 81, "size_class": -1, "handler": null,          "handler_sig": null }
  ],
  "client_prot": []
}
```

- **`server_prot`** — one entry per registered incoming (s2c) opcode, walking
  `g_serverProtTable[0 ..= 0xD9]` (`ReadPacket` gates `opcode < 0xDA`):
  - `op` — the opcode (the table index `ReadPacket` dispatches on).
  - `size_class` — the `ProtEntry.sizeClass`: `-1` = 1-byte size prefix
    (varByte), `-2` = 2-byte BE size prefix (varShort), `>= 0` = fixed payload
    byte count. **This is the framing the enricher needs to deframe s2c** without
    a static table.
  - `handler` — the dispatched handler **function**, IMAGE-RELATIVE (a vmaddr,
    `runtime − slide`), resolved through the entry's handler chain
    (`*(*(ProtEntry + 0x30)) + 0x30`). Stable across runs/slides and matches the
    RE docs' `0x1XXXXXXXX` addresses.
  - `handler_sig` — the **first 32 bytes** at the handler, hex-encoded. This lets
    the offline enricher **fingerprint-match** a handler to `handler-sigs.json`
    (the migrator's name ↔ 32-byte-sig DB) **across revs WITHOUT the binary** — so
    an opcode whose number shifted between builds can still be named by matching
    its handler bytes. `handler`/`handler_sig` are `null` for an entry whose
    handler chain could not be resolved (the `op` + `size_class` are still
    recorded so the framing table stays complete; the address is never
    fabricated).

- **`client_prot`** — the outgoing (c2s) opcode table. **Currently emitted empty
  (`[]`).** Unlike the server side, the NXT client has **no contiguous indexed
  c2s prot table** analogous to `g_serverProtTable`: `jag::ClientProt::RegisterAll`
  registers each opcode into a **separate, scattered descriptor global**
  (`{opcode@+0, sizeClass@+4}` in `__common`, populated by the ClientProt
  registrar — RE doc §6), not an array indexable by `opcode*8`. Dumping it
  requires the RE agent to first document a c2s table base (or an enumeration of
  the scattered descriptors / the registrar's writes); until then we never
  fabricate one, so `client_prot` stays empty. (The c2s **framing** the recorder
  already captures live at the `TcpConnectionMessage::Init` choke point —
  `framed-c2s.jsonl` — so this gap does not block c2s capture; it only defers the
  static c2s opcode → sizeClass map in `prot-table.json`.)

**When it is written.** The dump is attempted from the ctor (best-effort) and, as
a guarded one-shot retry, from `Client::SetMainState` and the s2c-dispatch
observer — because the client's `ServerProt::RegisterAll` (a C++ static
initializer) may run *after* our ctor. The retry fires the moment the table is
populated (always before any packet flows) and writes the file exactly once; the
log line `prot-table dumped: <N> server, <M> client opcodes` marks it. If the
table pointer is never resolvable, `prot-table.json` is simply skipped (never a
crash).

### `framed-s2c.jsonl` / `framed-c2s.jsonl`

One flat JSON object per line, stable keys:

```json
{"ts":"2026-06-26T21:53:13.050Z","mono_us":9728,"proc":"rs2client","plane":"framed","dir":"s2c","conn":"game","state":30,"op":81,"len":42,"body":"<base64>"}
```

- `op` is the opcode **number only** — naming is the enricher's job; the dylib
  embeds **no** opcode/protocol tables.
- `body` is base64. If `len > 8192` the raw bytes are written to
  `blobs/<sha256-prefix>.bin` and the line carries `"body_ref":"blobs/<prefix>.bin"`
  instead of `"body"`.
- `xtea_body: true` is set only when the dylib knows the body was XTEA-encrypted
  by the sender. The dylib does **not** decrypt; the enricher does. (Normal
  ClientProts are not XTEA'd; the rare XTEA senders are not auto-detected here,
  so this field is conservative.)

### `socket.jsonl`

```json
{"ts":"...","mono_us":1234,"proc":"rs2client","plane":"socket","dir":"c2s","conn":"login","len":270,"body":"<base64>"}
```

Same `body` / `body_ref` rule. This is the ground-truth wire (pre-prot,
ciphertext, and the RSA login block) — the data the decoded planes never see.

### `events.jsonl`

```json
{"plane":"event","kind":"state_change","old_state":20,"new_state":30,"old_state_name":"LOBBY_SCREEN","new_state_name":"LOGGED_IN","ts":"...","mono_us":...}
{"plane":"event","kind":"login_cipher_ready","login_s2c_bytes":13312,"ts":"...","mono_us":...}
{"plane":"event","kind":"connect","fd":7,"peer":"1.2.3.4:43594","port":43594,"ts":"...","mono_us":...}
{"plane":"event","kind":"disconnect","fd":7,"peer":"1.2.3.4:43594","port":43594,"ts":"...","mono_us":...}
{"plane":"event","kind":"process","phase":"ctor|exit","pid":99624,"proc":"rs2client","unix_ms":...,"ts":"...","mono_us":...}
```

**`login_cipher_ready`** (RE §8a) marks the login s2c **Phase A→B boundary** — the
instant `ConnectionManager::SetupLoginCiphers` (login reply-state `0x50`) builds
the login ISAAC. Before this point the login reply protocol is **plaintext**
(Phase A handshake: RSA reply, world token, login result); after it the lobby s2c
is **ISAAC ciphertext** (Phase B — the same opcode loop the game uses, on the
login connection). `login_s2c_bytes` is the count of login-direction s2c bytes
observed on the socket plane (via `ClientStream::Fill`, the authoritative login
raw source) **before** the cipher became ready. The offline deframer splits the
login s2c stream (`socket.jsonl` with `conn:"login", dir:"s2c"`, or
`raw-login-s2c.bin`) at that byte offset: decode `[0, offset)` as **plaintext**
and `[offset, end)` with the `login-s2c` ISAAC seed **from keystream position 0**.
Emitted exactly once per session.

### `state-snapshots.jsonl` (the client-state ORACLE — "client is king")

The recorder periodically (and once authoritatively at exit) snapshots the
client's OWN decoded state, read straight out of the live `Client` struct via the
documented pointer chains (RE §10). The offline verifier replays our captured s2c
packets and confirms they reproduce these values — the client is ground truth.

One flat JSON object per snapshot:

```json
{"ts":"...","mono_us":...,"tick":7,"proc":"rs2client","kind":"state_snapshot","main_state":30,"varps":{"173":1,"1021":7},"player":{"x":3200,"y":3200,"plane":0},"skills":[{"id":0,"level":10,"base":10,"xp":1154}],"run_energy":200,"run_weight":-5}
```

- `tick` — the recorder's monotonically-increasing snapshot **sequence number**
  (the client exposes no stable tick counter at a documented offset). It orders
  the snapshots; the **highest `tick` is the at-exit / authoritative end-state**.
- **Every field is emitted ONLY when the client had it readable** — an unreadable
  pointer chain OMITS that field (never `null`, never fabricated, never a crash):
  - `main_state` — the game-state enum; absent until the Client base is known.
  - `varps` — an **object** `{ "<varId>": value, … }` of every active var_player.
    Emitted whenever the varp VALUES table is reachable (this includes the lobby);
    an **empty object** means the table is reachable but nothing is set yet; an
    **absent `varps` key** means the table itself was unreadable. Decode a bare
    `varId` with `re-resources/gamevals/gameval.py var_player <id>`.
  - `player` — `{x, y, plane}` local-player tile; appears once in-world.
  - `skills` — array of `{id, level, base, xp}` (boosted/current level, base/real
    level, total xp); appears once in-world.
  - `run_energy` (0..255) / `run_weight` (signed) — appear once in-world.

Cadence: a snapshot is taken at most once per **`DARKAN_SNAPSHOT_INTERVAL_MS`**
(default 1000 ms, clamped 100..60000), driven off the `ClientStream::Fill` recv
pulse and `Client::SetMainState` transitions, plus one unconditional snapshot at
process exit. `varps` are meaningful as early as the lobby; `player`/`skills`/
`run_energy`/`run_weight` only once `main_state == 30` (LOGGED_IN).

### Cross-validation outputs (optional)

- `raw-<role>-s2c.bin` / `raw-<role>-c2s.bin` — the decrypted framed S2C bodies
  and the complete C2S wire bytes per role, concatenated, for an independent
  framing check by `tools/DecodeCapture.kt`.
- `isaac-keys.txt` — the REAL ISAAC construction seeds, **one line per
  `(conn, dir)`**, each carrying the four session keys that connection+direction's
  cipher was seeded with:

  ```
  game-s2c ISAAC keys (hex): 0x..,0x..,0x..,0x..     # recv cipher (conn+0x2B8) on the GAME conn
  game-c2s ISAAC keys (hex): 0x..,0x..,0x..,0x..     # send cipher (conn+0x40)  on the GAME conn
  login-s2c ISAAC keys (hex): 0x..,0x..,0x..,0x..    # recv cipher (conn+0x2B8) on the LOGIN conn
  login-c2s ISAAC keys (hex): 0x..,0x..,0x..,0x..    # send cipher (conn+0x40)  on the LOGIN conn
  ```

  **Why per-connection (the bug this fixes):** every TCP connection — login AND
  game/world — builds its **own** pair of ISAAC ciphers from **different** session
  keys. The earlier recorder kept only the **first** `jag::Isaac::Init` seed (the
  lobby/login send seed), so an offline deframe of game/world traffic used the
  wrong keystream and could never agree (brute force confirmed the captured seed
  matched no captured stream). The recorder now captures **every** `Isaac::Init`
  call — both the four seed ints (RSI) and the seeded ISAAC state pointer (RDI,
  `jag::Isaac::Init` @ file `0x71a10`) — and associates each capture with a
  `{conn, dir}` by matching its state pointer against each connection's send
  (`conn+0x40`) / recv (`conn+0x2B8`) cipher pointers, for **both** the game
  (`ConnMgr+0x18`) and login (`ConnMgr+0x28`) connections. The match is lazy: a
  seed captured before its connection wired its cipher pointer is reconciled as
  soon as the pointer is readable (the emit hooks and `atexit` re-reconcile), and
  the recv direction can also be recovered from the `recv = send + 50` per-int
  relationship (RE §7) as a tie-breaker.

  **Seed values are stored VERBATIM per line:** a `-c2s` (send) line holds the raw
  send keys; a `-s2c` (recv) line holds the recv keys, which **already include the
  `+50` per int**. The offline deframer must therefore seed its ISAAC with each
  line's values **directly** for that plane and must **not** add 50 again to an
  `-s2c` line.

  Each line still carries the `hex` token + four `0x` words (the existing
  `CaptureDeframer.parseHexKeys` contract) and the file deliberately contains NO
  "fingerprint, NOT a seed" marker (that string flags a file as fingerprint-only
  and disables the deframe). The only consumer-side change needed is to select the
  **per-plane** line by its `<role>-<dir>` tag (validate the game s2c socket with
  the `game-s2c` seed, etc.) instead of using one connection's seed for every
  plane — see the hand-off note below.

### Verify on the next capture

A real lobby→world capture should now confirm ALL of the following:

1. **Client-state oracle present.** `state-snapshots.jsonl` exists and has
   multiple lines, including a final (highest-`tick`) at-exit snapshot. Once
   in-world the snapshots carry `varps` (a non-empty object), `player` (tile),
   `skills` (array), and `run_energy` / `run_weight`. In the lobby, snapshots
   carry `main_state` + `varps` (player/skills/energy are correctly absent). This
   is the ground truth the offline verifier cross-checks our decoded s2c against
   ("client is king").

2. **Login / s2c socket capture complete (~100%).** With the `OpenLoginStream`
   early anchor, the login s2c bytes are tagged `login` from the first recv (no
   longer leaking to `js5`/`unknown`), so `socket.jsonl` (`conn:"login",
   dir:"s2c"`) accounts for the full login plane — previously stuck at ~13 KB vs
   the ~18 KB / 1672-packet framed login plane. The byte total should now match
   the framed login plane (≈ 100%).

3. **Login s2c deframes.** `events.jsonl` contains exactly one
   `login_cipher_ready` with a `login_s2c_bytes` offset; the offline deframer
   splits the login s2c stream there (plaintext before, `login-s2c` ISAAC from
   position 0 after) and lines up against the framed login packets.

4. **ISAAC seeds (unchanged from the prior fix).** `isaac-keys.txt` has at least
   the four game + login `(conn, dir)` lines, each with distinct keys; once the
   offline `CrossValidator` selects the per-plane seed, `:tools:trustReport`
   **§5 (Independent ISAAC deframe)** reports **AGREE** for the `game` plane (and
   `login`). The offline deframer + ISAAC math are already proven byte-exact (the
   `isaac-agree` / `isaac-vectors` self-tests pass).

## Offsets: generated source of truth + per-rev flow

The recorder's struct/derived field offsets are **not hand-maintained**. They are
the migrator artifact `re-resources/updater/recorder/offsets_<rev>.rs` — emitted
by the mac-native offset migrator (`re-resources/ghidra-scripts/RS3RecorderUpdaterMac.java`)
by deriving each offset from in-binary anchors, with drift detection. The dylib
`include!`s that file as the single source of truth (see `src/offsets/mod.rs` +
`build.rs`); `src/offsets/mod.rs` re-exports it under the same module names every
consumer already imports (`offsets::server_connection::CURRENT_OPCODE`, …), and
additionally owns the things the migrator does **not** generate:

- the capture-point byte **signatures** (`SIG_*` — the runtime sig-scan locator);
- the `Client::SetMainState` **game-state enum** + `state_name`;
- two `connection_manager` fields not yet in the mac anchor registry
  (`OWNER_CLIENT` = `*(ConnMgr+8)`, `TINYKEY` = ConnMgr+0x10) — supplemented in the
  `connection_manager` shim until the migrator owns them (see "Known limitations").

**Active revision** — selected at build time. Default is `948-5` (the
`DEFAULT_OFFSET_REV` constant in `build.rs`); override per-build with the
`DARKAN_RECORDER_OFFSET_REV` env var. `build.rs` resolves the chosen
`offsets_<rev>.rs`, strips its file-level header, and writes the body into
`OUT_DIR` for `include!` (offset VALUES are carried through verbatim — never
hand-edited). A missing rev fails the build with a clear message.

### Per-rev flow (the payoff)

When a new client build lands, regenerate the offsets and rebuild — that's it:

```sh
# 1. Regenerate the offsets for the new rev with the mac migrator (headless
#    Ghidra against the new mac rs2client; the `selftest`/`import` mode emits
#    re-resources/updater/recorder/offsets_<newrev>.rs with drift detection):
analyzeHeadless <proj> <newProgram> -postScript RS3RecorderUpdaterMac.java import -readOnly

# 2. Point the active rev at it — either bump the one-liner default in build.rs:
#       const DEFAULT_OFFSET_REV: &str = "<newrev>";
#    OR build with the env override:
DARKAN_RECORDER_OFFSET_REV=<newrev> client/launcher/recorder-mac/build-mac.sh
```

If the migrator reports drift (an offset moved), `offsets_<newrev>.rs` carries
the new value with a `// *** CHANGED ***` comment; update the byte-identity
assertions in `src/offsets/mod.rs` only after the RE doc confirms the new layout.
Re-verify (and, if needed, re-capture the `SIG_*` patterns) the capture-point
**signatures** by hand — the migrator owns the struct/derived offsets, but the
runtime hook locators stay in `src/offsets/mod.rs`. The migrator also emits each
anchor function's file offset (in `results_<rev>.json`); treat those as the
post-scan sanity-check values for the `SIG_*` `expected_file`s.

## Field reference (mac 948-5)

The values below are the 948-5 snapshot of the generated offsets (the dylib does
**not** share the engine's `Offsets.kt`, which holds the Linux values). The
authoritative current values are whatever `offsets_<active-rev>.rs` generates;
this list is descriptive. Documented in
`re-resources/docs/binary/patch-targets-macos-recorder.md`:

- `Client.MAIN_STATE = 0x19DB0`, `Client.CONNECTION_MANAGER = 0x196C8` (both
  differ from Linux).
- `ServerConnection`: `CLIENT_STREAM 0x08`, `CURRENT_OPCODE 0x2C`,
  `RESOLVED_SIZE 0x30`, ISAAC-in `0x2B8`, `PACKET_BASE 0x2C0`, `BUF_DATA 0x2D0`,
  `LAST_OPCODE 0x2E0`, ISAAC-out `0x40`.
- `ConnectionManager`: `GAME_CONNECTION 0x18`, `LOGIN_CONNECTION 0x28`.
- `LoginStateMachine` (`Client+0x19720`): `OWNER_CLIENT +0x18`,
  `LOGIN_CONNECTION +0x30` (the early login anchor, RE §8a).
- **Client-state oracle (RE §10)** — every Client *base* field is `+0x288` vs
  Linux; inner struct layouts MATCH Linux:
  - `PlayerVarDomain` (embedded) `0x19DC8`; VALUES table bucketArray `PVD+0x28`,
    count `PVD+0x30`; node value `+0x8`, next `+0x28`, varId `+0x0`.
  - `LoggedInPlayer 0x19DB8` → serverIndex `+0x48`; `PlayerManager 0x19760` →
    list `+0x10` → node entity `+0x38` → graphNode `entity+0x8`; sceneFine
    X/Z/Y `graphNode+0xF0/0xF4/0xF8` (float), plane `entity+0x40`, size
    `entity+0x184`; `tile = round((sceneFine-256-(size<<8))/512)`.
  - `MainLogicManager 0x19730`; StatTable `*(MLM+0x7520)`, array begin `+0x10`,
    stride `0x18`, OStat xp `+0xC` / base `+0x10` / boosted `+0x14`; run energy
    `*(MLM+0x7520)+0x18` (0..255), run weight `+0x1C` (signed).

## Known limitations

- **No protocol semantics in the dylib.** `op` is a number; naming/decoding is
  the enricher's job, by design.
- **XTEA bodies are not auto-flagged.** A few C2S senders XTEA-encrypt their body
  with the ConnMgr tinyKey; the dylib does not identify them generically, so
  `xtea_body` is only set when known. The enricher decides.
- **Two `connection_manager` offsets are shim-supplemented, not generated.**
  `connection_manager::OWNER_CLIENT` (0x08) and `TINYKEY` (0x10) are real,
  documented mac offsets but are absent from `re-resources/updater/anchor_registry_mac.json`,
  so `RS3RecorderUpdaterMac` cannot emit them. They are defined in the
  `connection_manager` shim in `src/offsets/mod.rs`. To close the gap, add anchors
  for them to `anchor_registry_mac.json` (OWNER_CLIENT is `*(ConnMgr+8)`, read in
  `ReadPacket`/`OpenLoginStream`; TINYKEY overlaps the game msg-ctx at ConnMgr+0x10)
  so the generated file becomes a full superset and the shim can drop them.
- **`server_prot::TABLE_GLOBAL` is hand-owned, not generated.** The
  `g_serverProtTable` global (image-relative `0xf0eaf0`) used by the
  `prot-table.json` dump is a `DAT_` **data global**, not a struct field, so it is
  outside the struct-offset recipes the migrator emits today. It is defined in the
  `server_prot` module in `src/offsets/mod.rs`. To make it migrator-owned, add an
  anchor that captures the RIP-relative `mov`/`lea` to `g_serverProtTable` inside
  `ConnectionManager::ReadPacket` (the `g_serverProtTable + opcode*8` load) or
  `ServerProt::RegisterProt` (the table push) via the registry's `call_target` /
  data-ref DSL — then it regenerates per-rev with drift detection like the rest.
  The `ProtEntry` struct layout (opcode/sizeClass/handler-category offsets) is also
  in that module and could likewise be derived from `RegisterProt`/`InitEntry`.
- **`prot-table.json` `client_prot` is empty (RE gap).** There is no contiguous
  indexed c2s prot table in the binary (see the `prot-table.json` section); dumping
  it needs the RE agent to document a c2s table base / descriptor enumeration. The
  c2s framing is still captured live (`framed-c2s.jsonl`), so this only defers the
  static c2s opcode → sizeClass map, not c2s capture.
- **Rosetta self-modifying code.** Hooks are installed pre-`main()` (before
  Rosetta translates the targets) and patched via `mach_vm_protect(VM_PROT_COPY)`.
  This is validated end-to-end (`tests/detour_runtime.rs`), but inline hooking of
  a translated x86_64 binary is inherently fragile; if a future macOS/Rosetta
  build refuses the COW patch, the socket plane + connect/disconnect events still
  work (they use dyld interposition, not code patching).

## Tests

```sh
# Unit + runtime detour test (proves the trampoline runs the original and the
# entry redirect works on this machine):
cargo test --release --target x86_64-apple-darwin

# Validate prologue relocation for every hook target against the real RE binary
# (needs re-resources checked out):
cargo test --release --target x86_64-apple-darwin --test prologue_reloc -- --ignored --nocapture
```
