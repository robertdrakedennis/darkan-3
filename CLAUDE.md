# Darkan-3-Undercut — Monorepo Guidelines

This monorepo unifies two previously separate, tightly-related projects into one all-in-one
platform for the RS3 (RuneScape 3) NXT client:

- **Darkan-3 server** — a Kotlin/JVM RS3 private server (Ktor networking, MongoDB, multi-module
  Gradle: `core`, `lobby`, `world`, `tools`) plus a **Rust launcher + LD_PRELOAD/DLL patcher**
  under `client/`.
- **Undercut engine** — a Kotlin/JVM + C++20 **injection engine** (`engine/`) that injects
  `libundercutbootstrap.so` into the live `rs2client` process via GDB `dlopen` (funchook hooks,
  ImGui overlay, in-process MCP, scripting framework, and a `TcpIn` network sniffer).

Both target the **same** NXT client binary (`rs2client`), share the same Ghidra RE workflow, and
now share one **knowledge hub** (`re-resources/`, a submodule) and one **all-in-one launcher**
that patches the client (Darkan-3) **and** injects the engine (Undercut) in a single step — with
**no network proxy required** (the injected sniffer replaces the deprecated login proxy).

---

## Repository Layout

```
darkan-3-undercut/
├── core/        lobby/   world/   tools/    # Darkan-3 server (Gradle modules)
├── client/                                  # Rust launcher (darkan-launcher) + LD_PRELOAD/DLL/dylib patchers
├── engine/                                  # Undercut injection engine == :engine Gradle module
│   ├── src/main/kotlin/com/undercut/…       #   cache, game/nxt, hooks, mcp, script, ui, scene, …
│   ├── native-bootstrap/                    #   C++20 bootstrap (funchook + imgui submodules)
│   ├── example-script-module/               #   :engine:example-script-module
│   └── inject                               #   GDB-dlopen injector script
├── data/                                    # NXT client binaries + ~24 GB JS5 cache (cache gitignored)
├── re-resources/         (SUBMODULE)        # Unified RE knowledge hub -> gitlab:reclass-data
│   ├── docs/   (== top-level ./docs symlink)#   net/ cache/ binary/ re-methodology/ engine/
│   ├── symbols/                             #   functions.txt, parsed_functions.txt, *_handlers.txt, …
│   ├── gamevals/                            #   cache id↔name dictionaries + gameval.py
│   ├── cs2-dumps/   (NESTED SUBMODULE)      #   -> github:GibsonHF/rs3-cs2-dumps (auto-updated)
│   ├── rstypes.rcnet  *.gzf  updater/  …    #   ReClass types, client-binary archives, offset updater
│   └── CROSS_VERSION_MIGRATION.md           #   cross-build porting guide
├── bridge_mcp_ghidra.py                     # Ghidra MCP bridge (referenced by .mcp.json)
├── docs -> re-resources/docs                # symlink to the unified docs (no duplication)
├── .claude/agents/   .claude/commands/      # merged agent roster + re-* RE commands
└── settings.gradle.kts  build.gradle.kts    # unified Gradle build (kotlin 2.3.20, JDK 25)
```

**Build:** unified Gradle build, **Kotlin 2.3.20 / JDK 25**. `./gradlew projects` shows
`:core :lobby :world :tools :engine :engine:example-script-module`. The engine keeps its native
CMake tasks (`:engine:buildNativeBootstrap` → `libundercutbootstrap.so`).

**Cloning:** the repo has submodules (`re-resources`, the engine native subs) and a nested
submodule (`re-resources/cs2-dumps`). Always clone recursively:
`git clone --recursive <url>` (or `git submodule update --init --recursive`).

---

## CRITICAL: Mandatory Agent-Driven Workflow

**Substantive work flows through specialized agents.** Each agent has a defined role, code
ownership, and handoff points. Do NOT implement protocol/offset details from memory — defer to
documentation produced by the reverse-engineering agent (now stored in `re-resources/docs/`).

### Agent Roster (`.claude/agents/`)

| Agent | Role | Owns | Produces |
|-------|------|------|----------|
| **ghidra-reverse-engineer** | Reverse engineers `rs2client` via Ghidra MCP | Ghidra DB (renames, structs, prototypes, comments) | Protocol docs, format specs, packet layouts, struct/offset definitions |
| **networking-protocol-engineer** | Server networking layer | `org.darkan.core.net`, codecs, handlers, login, JS5 | Network code byte-compatible with the NXT client |
| **cache-library-engineer** | Cache read/write/serve | `world.gregs.voidps` (buffer, cache, type) | Cache library, definition decoders, JS5 provider, compression |
| **client-launcher-engineer** | Launcher, patching, injection | `client/launcher/` (Rust `darkan-launcher`) | Launcher UI, OAuth, client download, LD_PRELOAD/DLL patching, **engine injection** |
| **js5-server-engineer** | JS5 file serving | JS5 listener/protocol | JS5 server (framing, compression, caching) |

RE **commands** (`.claude/commands/`): `/re-analyze`, `/re-identify`, `/re-overview`,
`/re-search`, `/re-trace` for ad-hoc Ghidra analysis.

### Workflow Rules (STRICTLY ENFORCED)

1. **No protocol/offset implementation without documentation.** If you need a packet, login step,
   cache format, or struct offset and no doc exists in `re-resources/docs/`, invoke the
   ghidra-reverse-engineer agent first. NEVER guess at packet formats, opcodes, or byte layouts.
2. **No cross-domain code changes.** Each agent owns its code exclusively (cache-library-engineer
   ↔ `world.gregs.voidps.*`, networking-protocol-engineer ↔ `org.darkan.core.net.*`, etc.). The
   RE agent NEVER modifies server/engine source — it produces docs + refactors the Ghidra DB.
3. **Documentation is the contract.** `re-resources/docs/` is the handoff between the RE agent and
   the implementation agents. Docs must be byte-precise, self-contained, and kept in sync.
4. **When in doubt, RE first.** Any uncertain field type, opcode meaning, encoding boundary, or
   offset — stop and confirm from the binary. Wrong assumptions compound into protocol mismatches.
5. **Engine ↔ Binary ↔ Ghidra three-way sync is mandatory** (see below) — discrepancies in
   offsets/structs/names are fixed in ALL THREE places, never left unfixed.

> Because `re-resources/` is a submodule, RE/protocol docs are committed to the shared
> `reclass-data` repo — that is the point: one shared knowledge base for both server and engine.

---

## Ghidra MCP Integration

This project uses GhidraMCP to interact with Ghidra. The MCP supports **multiple simultaneous
Ghidra instances** — analysis of the stripped target alongside an unstripped reference binary.

### Multi-Binary Support (Reference Binary Workflow)

1. **Target binary** (`rs2client`) — the current stripped binary we're reverse engineering. **All**
   renames, structs, comments, and prototypes go here.
2. **Reference binary** (`librs2client.so`) — an older unstripped Linux build with full debug
   symbols (~12,500 named functions). **Read-only pattern-matching reference ONLY.** It is
   **SEVERELY outdated** — its offsets, struct layouts, enum values, packet structures, and
   signatures DO NOT match the modern target. Never copy concrete data from it; use it only for
   code-pattern/behavioral comparison.

Management tools: `mcp__ghidra__list_binaries` (call at session start),
`mcp__ghidra__select_binary` (e.g. `select_binary("rs2client")`),
`mcp__ghidra__discover_ghidra_instances`. All other tools accept an optional `binary_name`.

**Reference workflow (PATTERN COMPARISON ONLY):** decompile in the target first → search the
reference (`search_functions_by_name(query="ClassName", binary_name="librs2client.so")`) →
compare CODE PATTERNS (control-flow shape, behavioral purpose, call-graph) → apply to target ONLY
if certain, deriving signatures from the TARGET's own code. **Never** copy offsets, enum values,
switch cases, struct field positions, or signatures from the reference.

### MANDATORY: Progressive Ghidra Documentation

**If you decompile it and understand it, document it immediately.** Rename every identified
function (full namespace path via `rename_function_by_address`), set COMPLETE prototypes (return +
all params via `set_function_prototype`), create structs for every `*(type *)(ptr + 0xNN)` access
pattern (`create_struct` + `add_struct_field`), apply them to locals
(`set_local_variable_type` → `obj->field` access), add entry-point comments, name params/vars,
create enums for magic-number sets, set return types. Do NOT defer documentation.

### Cross-Version Sig-Scan Workflow (new client builds)

When a new build lands and the auto-updater can't relocate a function, the canonical workflow is
**byte-pattern sig-scan** (`mcp__ghidra__search_memory_pattern`), NOT name search:
1. In the OLD build, grab ~12–20 distinctive bytes of the known function's body (avoid the prologue).
2. Wildcard varying immediates (`E8`/`E9` call/jmp targets, `48 8B 05`/`48 8D 05` RIP-relative
   displacements, `mov reg,imm64` absolutes) with `??`.
3. In the NEW build, `search_memory_pattern(pattern="…")` → the match is the equivalent function.
4. Read the new offset off the analogous instruction; rename + comment; feed the name back to the
   auto-updater signature DB. See `re-resources/CROSS_VERSION_MIGRATION.md` and the worked example
   in `.claude/agents/ghidra-reverse-engineer.md`. Operational guide:
   `re-resources/docs/re-methodology/UPDATING.md` + `AUTO_UPDATER.md`.

### Symbol Discovery sources (both SEVERELY outdated — pattern-match only)

1. **Reference binary** (`librs2client.so`) — loaded in Ghidra; for class/namespace discovery +
   code-pattern comparison.
2. **`re-resources/symbols/parsed_functions.txt`** — flat `ADDRESS SYMBOL_NAME(params)` dump
   (same symbols). Useful for quick class/namespace discovery. **Addresses and parameter types are
   from the old binary and DO NOT apply to the target.** Only rename when ABSOLUTELY CERTAIN with
   multiple independent lines of evidence; otherwise leave `FUN_` + a HYPOTHESIS comment.

### Namespace Enforcement

All renamed symbols MUST use full namespace paths (`::` auto-creates the hierarchy) — never leave
symbols in Global. Top-level namespaces: **`jag`** (game engine; sub: `jag::ScriptRunner`,
`jag::game`, `jag::graphics`, `jag::input`, `jag::opcode`, `jag::Packet`, `jag::ServerProt`, …)
and **`eastl`** (EA STL). When unsure of the sub-namespace, at minimum use `jag::`.

### Struct Creation Workflow

`create_struct("StatEntry", 24, "/jag")` → `add_struct_field(...)` per offset →
`get_struct_fields(...)` to verify → `set_local_variable_type(addr, "stat", "StatEntry *")` →
re-decompile. Naming: C++-style matching the binary's namespace (e.g. `Entity`, `NPCType`), NOT
the engine Kotlin `O*` offset-object names. Cross-reference verified offsets against the engine's
`Offsets.kt` before creating.

### Available Ghidra MCP Tools (summary)

Multi-binary: `list_binaries`, `select_binary`, `discover_ghidra_instances`. Functions:
`list/search_functions_by_name`, `decompile_function[_by_address]`, `disassemble_function`,
`rename_function[_by_address]`, `get_function_by_address`. Signatures: `set_function_prototype`,
`get_function_signature`, `set_return_type`, `add/remove/change_parameter_type`, `rename_parameter`,
`set_calling_convention`. Variables: `rename_variable` (re-decompile after — auto-vars renumber),
`set_local_variable_type`. Types: `create_struct`/`add/delete_struct_field`/`get_struct_fields`,
`create_union`/`add_union_field`, `create_enum`/`add_enum_value`, `get_data_type`,
`apply_struct_to_address`. Xrefs: `get_xrefs_to/from`, `get_function_xrefs`. Pattern:
`search_memory_pattern` (IDA-style, `??`/`4?` wildcards — the primary cross-version porting tool).
Data/symbols: `rename_data`, `list_data_items`, `list_strings`, `list_segments`, `list_imports`,
`list_exports`, `list/create_namespace`, `list_namespace_contents`, `move_symbol_to_namespace`.
Comments: `set_decompiler_comment`, `set_disassembly_comment`. Selection: `get_current_address`,
`get_current_function`.

---

## Engine ↔ Binary ↔ Ghidra Three-Way Synchronization (CRITICAL)

The engine Kotlin code, binary analysis, AND Ghidra data types must always be kept in sync.

- **Source of truth for offsets:** `engine/src/main/kotlin/com/undercut/game/nxt/Offsets.kt`
  (verified correct for the current binary; 30+ offset objects). Use it to fill struct fields in
  Ghidra and to cross-check decompiled field access.
- **Flag discrepancies, don't silently change engine code.** If the binary/Ghidra disagrees with
  `Offsets.kt`, surface it to the user before editing engine source (unless explicitly asked).
- **Debugging-driven sync (MANDATORY):** when debugging reveals a wrong offset/type/name, fix ALL
  THREE — the Ghidra struct/field, the `Offsets.kt` constant, AND the Kotlin entity/wrapper class.
  Never leave a known discrepancy unfixed.
- **Aggressive data-type creation:** every pointer-deref pattern is a struct; 2+ field accesses on
  a base pointer → create + apply a struct; every touched function gets a complete signature.

Key engine offset files: `Offsets.kt` (master), `DoActionOpcode.kt`, `types/Vector.kt`,
`memory/eastl/*`, `cs2/CS2Executor.kt`.

---

## Shared Resources (`re-resources/` submodule — USE PROACTIVELY)

The unified knowledge hub. Its own git repo (`gitlab:reclass-data`), included here as a submodule.

- **`re-resources/docs/`** (= `./docs` symlink) — unified documentation:
  `net/` (protocol/packet/login/JS5/social), `cache/` (JS5/LZMA/SQLite), `binary/` (patch
  targets, RSA keys, memory layout), `re-methodology/` (UPDATING, AUTO_UPDATER),
  `engine/` (engine offset notes). RE-produced docs are committed here.
- **`re-resources/symbols/`** — RE symbol dumps (`functions.txt`, `parsed_functions.txt`,
  `opcode_handlers.txt`, `cs2_opcode_handlers.txt`, `unique_handlers.txt`, …). Outdated build —
  pattern-match/namespace-discovery only; never copy concrete data.
- **`re-resources/gamevals/`** — cache id ↔ RuneScape dev-name dictionaries for every config type
  (`npc`/`obj`/`loc`/`varbit`/`var_player`/`param`/`component`/`enum`/`struct`/`seq`/`graphic`/…).
  **Decode any bare numeric id before guessing its meaning:**
  `./re-resources/gamevals/gameval.py npc 7987` (→ `sum1_ghost_erik_bonde_no_wander`),
  `loc -s yew`, `obj -n coins` (→ 995), `-s magic_logs` (search all types). Names are unique per
  file → fully bidirectional. Pairs with `cs2-dumps`: gamevals names the id, cs2-dumps holds its
  field values/usage.
- **`re-resources/cs2-dumps/`** (nested submodule, auto-updated: `git -C re-resources/cs2-dumps
  pull`) — ~20k decompiled CS2 clientscripts (`cs2/*.ts`) + item/npc/loc/struct/enum/varbit/varp
  JSON dumps from the live RS3 NXT cache. **Authoritative for** interface/component IDs, vars,
  structs/enums/params, item/npc/loc definitions, dbrows, achievements, quests, and CS2 opcode
  semantics. Grep `cs2/` for real callers before guessing what an opcode/var/interface does.
- **`re-resources/rstypes.rcnet`, `*.gzf`, `updater/`, `anchor_registry.json`,
  `CROSS_VERSION_MIGRATION.md`** — ReClass types, compressed client-binary archives (939-1…948-5),
  and the offset auto-updater.

**Canonical workflow:** `gameval.py` to name an id → `cs2-dumps` to read its data/usage → Ghidra
to document the struct/function (cite the gameval name in the comment).

---

## CS2 Engine Analysis

Namespaces: `jag::ScriptRunner` (execution), `jag::ClientScriptHelpers`, `jag::ClientScriptState`,
`jag::game::ClientScript`, `jag::opcode::*` (Camera, Core, Entities, InterfaceComponents, …).
Key data: CS2 opcode dispatch table `jag::ScriptRunner::g_opcodeDispatchTable` (948-5: ELF `0x013969a0`,
Mac `0x100f05ce0`; 2244 16-byte slots `{handler,u16 opcode,u8 flag}`, index==opcode, populated at runtime
by `jag::opcode::RegisterAllOpcodes`); `ClientScriptState` ≈ `0xC420` (int stack `+0x100`/SP `+0x10a0`,
string stack `+0x10a8`, long stack `+0x8db0`/SP `+0xacf8`); return sentinels (948-5 Mac) Success
`0x100f050b0`, Yield `0x100f04fa8`, Abort `0x100f05000`, Error `0x100f05058`. (Offsets are build-specific — re-verify per build.)

---

## Key Technical Details (Server)

### RS3 NXT Client Specifics
- The NXT client uses **SQLite** for cache storage (NOT legacy `.idx`/`.dat2`).
- Buffer ops follow `jag::Packet` naming: `gT<type>` reads, `pT<type>` writes; most are **inlined**
  in the modern binary — the RE agent must recognize assembly patterns.
- Encryption: **ISAAC** (opcode cipher), **RSA** (login + JS5), **XTEA/tinyKey** (data blocks).

### Client Launcher & Patching
- The launcher is a **Rust app** (`darkan-launcher`, `client/launcher/`) using **tao** + **wry** webview.
- Handles **Jagex OAuth2/PKCE**, session management, client download/update from the Jagex CDN.
- **LD_PRELOAD** (Linux `libdarkan_patcher.so`) / **DLL injection** (Windows) / **dylib** (macOS)
  patch the running client: **RSA key replacement**, **server URL redirection**, **JS5 URL
  redirection**, **configURI override**. Env-driven: `DARKAN_RSA_MODULUS`,
  `DARKAN_JS5_RSA_MODULUS`, `DARKAN_HTTP_PORT`, `DARKAN_PROXY_MODE`.
- Patch-target addresses/patterns are documented by the RE agent in `re-resources/docs/binary/`.

---

## Unified Launcher & Injection

The all-in-one launcher (`launch/`, see Phase 8 of the convergence) drives **both** mechanisms
against the same `rs2client` process:

1. **Patch + launch (Darkan-3):** source `.env`, `LD_PRELOAD=data/client/linux/libdarkan_patcher.so`,
   launch `rs3linux --configURI http://localhost:$DARKAN_HTTP_PORT/jav_config.ws`. The patcher
   rewrites RSA keys + server URLs so the client talks to the local Darkan-3 lobby/world.
2. **Inject (Undercut):** once `rs2client` is up, GDB-`dlopen` `libundercutbootstrap.so` (the
   `engine/inject` script) → JVM + bootstrap → funchook hooks, ImGui overlay, in-process MCP
   (`:7882`), scripting, and the `TcpIn` **network sniffer**.

Flags: `--no-engine` (server-only), `--no-patch` (inject into a live client).

### Proxy Deprecation (network sniffer replaces the MITM proxy)

Darkan-3's login proxy (`tools/.../loginproxy`, `run-proxy.sh`) is **DEPRECATED**. The Undercut
engine's injected `TcpIn` hook reads the protocol directly from client memory — **no network
redirection** is needed. The proxy code is retained (not deleted) for ad-hoc capture but is no
longer part of any build.

The old capture-regression pipeline has been **removed**: the `:tools` `framingRegression` /
`wireFormatVerify` HARD gates and the engine's `CaptureExport` writer (env-gated
`UNDERCUT_CAPTURE_EXPORT=1`) are gone, along with their dependency on the `capture/<session>/`
format (`raw-c2s.bin`, `raw-s2c.bin`, `isaac-keys.txt`). Wire-format correctness is no longer
asserted on `check`/`build`.

---

## Development Phases (Server roadmap)

Phase 1 **JS5 server & cache downloader** · Phase 2 **Lobby login** · Phase 3 **Worldlist &
social** · Phase 4 **World login** (player/NPC sync, scene build, core game packets). Each phase's
prerequisite RE docs live in `re-resources/docs/`. See git history + agent memory for current
status; do not implement a phase's protocol without its RE documentation.

---

## Documentation Standards

RE documentation produced by the ghidra-reverse-engineer agent (in `re-resources/docs/`):
- **Packet docs** (`docs/net/`): one file per system; every packet specifies opcode, direction,
  size type (fixed/varByte/varShort), and a field table (offset, size, `jag::Packet` type, name,
  description); include ISAAC/encoding notes + pseudocode for complex codecs.
- **JS5/cache docs** (`docs/cache/`, `docs/net/`): connection lifecycle + sequence diagrams,
  byte-level request/response formats, chunk framing, master-index + per-archive formats, SQLite
  schemas, blob/compression headers, `DecodeType` opcode tables.

### Code Conventions
- Packet types `PascalCase` (`AddFriend`, `IfSetText`); handlers `<PacketName>Handler`; definition
  data classes `<Type>Definition`; decoders `<Type>Decoder`; constants `SCREAMING_SNAKE_CASE`.
- Packages: `org.darkan.core.net.prot` (protocol),
  `org.darkan.lobby.server.packet` / `org.darkan.world.server.packet` (handlers).
- Engine: package `com.undercut.*`; struct names are C++-style (not the `O*` Kotlin offset names).
- **Never use fully-qualified names inline in Kotlin** — add imports (use import aliases for
  collisions). Applies to all packages (`java.io.File`, `kotlin.math.min`, `com.undercut.*`, …).
