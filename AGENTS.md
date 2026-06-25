# Darkan 3 — RS3 Private Server

## Project Overview

RS3 (RuneScape 3) private server targeting the NXT (C++) client binary. Written in Kotlin/JVM with Ktor networking, MongoDB persistence, and a multi-module Gradle structure (`core`, `lobby`, `world`).

- **Kotlin 2.3+**, **Java 25**, **Ktor**, **Kotlinx Coroutines**, **MongoDB**
- Cache library lives at `world.gregs.voidps` in `core/`
- Networking code at `org.darkan.core.net` in `core/`
- Client binaries in `client/` (Linux + Windows NXT builds)
- Protocol knowledge derived from reverse engineering the NXT client via Ghidra

---

## CRITICAL: Mandatory Agent-Driven Workflow

**ALL work in this project MUST flow through the specialized agent pipeline. This is non-negotiable.**

This project operates on a strict agent-driven workflow where each agent has a defined role, exclusive code ownership, and clear handoff points. Do NOT bypass agents by working on code outside your domain. Do NOT implement protocol details from memory — always defer to documentation produced by the reverse engineering agent.

### Agent Roster

| Agent | Role | Owns | Produces |
|-------|------|------|----------|
| **ghidra-reverse-engineer** | Reverse engineers the NXT client binary via Ghidra MCP | Ghidra project refactoring | Protocol docs, format specs, packet layouts, struct definitions |
| **networking-protocol-engineer** | Implements server networking layer | `org.darkan.core.net`, packet codecs, handlers, login, JS5 serving | Working network code that is byte-compatible with the NXT client |
| **cache-library-engineer** | Cache reading/writing/serving | `world.gregs.voidps` (buffer, cache, type packages) | Cache library, definition decoders, JS5 file provider, compression |
| **client-launcher-engineer** | Launcher app, client patching, binary management | `client/launcher/` (Rust crate `bolt-rs3`) | Launcher UI, Jagex OAuth login, client download/update, LD_PRELOAD/DLL injection patching for RSA keys and server URLs |

### The Documentation Pipeline (MANDATORY)

```
┌─────────────────────────┐
│  NXT Client Binary      │
│  (rs2client)            │
└──────────┬──────────────┘
           │ Ghidra MCP tools
           ▼
┌─────────────────────────┐
│  ghidra-reverse-engineer│──────► Ghidra project (renamed symbols, structs, types)
│  (analysis + refactor)  │──────► docs/protocol/  (packet formats, login flow, opcodes)
│                         │──────► docs/js5/       (JS5 protocol, connection lifecycle)
│                         │──────► docs/cache/     (SQLite schema, archive format, compression)
│                         │──────► docs/binary/    (RSA key locations, patch targets, memory layout)
└──────────┬──────────────┘
           │ Documentation handoff
           ▼
┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
│ networking-protocol- │  │ cache-library-       │  │ client-launcher-     │
│ engineer             │  │ engineer             │  │ engineer             │
│ (docs/protocol/,    │  │ (docs/cache/,        │  │ (docs/binary/,       │
│  docs/js5/)          │  │  docs/js5/)          │  │  docs/protocol/)     │
│                      │  │                      │  │                      │
│ Implements:          │  │ Implements:          │  │ Implements:          │
│ - Login protocol     │  │ - Cache reader/writer│  │ - Launcher UI (wry)  │
│ - Game packets       │  │ - SQLite cache access│  │ - Jagex OAuth flow   │
│ - ISAAC cipher       │  │ - Definition decoders│  │ - Client download    │
│ - Session management │  │ - JS5 file provider  │  │ - LD_PRELOAD patching│
│ - JS5 connection     │  │ - Compression/crypto │  │ - DLL injection      │
│   protocol           │  │ - Buffer operations  │  │ - RSA key replacement│
└──────────────────────┘  └──────────────────────┘  └──────────────────────┘
```

### Workflow Rules (STRICTLY ENFORCED)

1. **No protocol implementation without documentation.** If you need to implement a packet, login step, or cache format and no documentation exists in `docs/`, you MUST first invoke the ghidra-reverse-engineer agent to analyze the relevant client code and produce documentation. NEVER guess at packet formats, opcodes, or byte layouts.

2. **No cross-domain code changes.** Each agent owns its code exclusively:
   - Only `cache-library-engineer` modifies `world.gregs.voidps.*`
   - Only `networking-protocol-engineer` modifies `org.darkan.core.net.*`
   - The ghidra-reverse-engineer agent NEVER modifies server source code — it only produces documentation and refactors the Ghidra project

3. **Documentation is the contract.** The `docs/` directory is the handoff point between the RE agent and the implementation agents. Documents must be:
   - Byte-level precise (exact offsets, sizes, types, endianness)
   - Self-contained (implementation agents should not need Ghidra access)
   - Kept in sync (when the RE agent discovers corrections, docs are updated, and implementation agents update their code)

4. **When in doubt, RE first.** If any implementation detail is uncertain — a packet field's type, an opcode's meaning, a smart encoding boundary, an encryption step — stop and invoke the ghidra-reverse-engineer agent to confirm from the binary. Wrong assumptions compound into hard-to-debug protocol mismatches.

5. **Agents collaborate via documentation, not direct code sharing.** The RE agent writes docs. The implementation agents read docs and write code. If an implementation agent discovers a discrepancy (e.g., client disconnects on a packet they implemented per the docs), escalate back to the RE agent for re-analysis.

### Invoking the Correct Agent

**Before starting ANY task, determine which agent owns it:**

- "Analyze this function in Ghidra" → `ghidra-reverse-engineer`
- "Document the login protocol" → `ghidra-reverse-engineer`
- "What does opcode 0x42 do?" → `ghidra-reverse-engineer`
- "Find the RSA modulus in the binary" → `ghidra-reverse-engineer`
- "Implement the login handshake" → `networking-protocol-engineer`
- "Add a new server packet" → `networking-protocol-engineer`
- "Debug ISAAC cipher mismatch" → `networking-protocol-engineer`
- "Add a definition decoder for NPC types" → `cache-library-engineer`
- "Implement JS5 file serving" → `cache-library-engineer` (file provider) + `networking-protocol-engineer` (connection/protocol)
- "Fix buffer read/write operations" → `cache-library-engineer`
- "Download the full cache" → `cache-library-engineer` (cache format) + task may span both agents
- "Patch the RSA keys in the client" → `client-launcher-engineer`
- "Add a server toggle to the launcher UI" → `client-launcher-engineer`
- "Fix the OAuth login flow" → `client-launcher-engineer`
- "Make the launcher point to our local server" → `client-launcher-engineer`
- "Update the client binary auto-updater" → `client-launcher-engineer`

**Multi-agent tasks:** Some tasks span agents. For example, JS5 serving requires the cache-library-engineer to implement the file provider (reading/compressing cache data) and the networking-protocol-engineer to implement the JS5 connection protocol (framing, chunk separators, prefetch handling). Client patching requires the ghidra-reverse-engineer to locate patch targets (RSA key addresses, server URL strings) and the client-launcher-engineer to implement the runtime patches. Coordinate via documentation — agents document their APIs and findings, other agents consume them.

---

## Key Technical Details

### RS3 NXT Client Specifics
- RS3 NXT client uses **SQLite** for cache storage (NOT legacy `.idx`/`.dat2`)
- Buffer operations follow `jag::Packet` naming: `gT<type>` for reads, `pT<type>` for writes
- Most buffer functions are **inlined** in the modern binary — the RE agent must recognize assembly patterns
- Encryption: **ISAAC** (opcode cipher), **RSA** (login), **XTEA/tinyKey** (data blocks)

### Client Launcher & Patching
- The launcher is a **Rust application** (`bolt-rs3`) at `client/launcher/` using **tao** + **wry** (webview UI)
- Handles **Jagex OAuth2/PKCE** login flow, session management, client binary download/update from Jagex CDN
- Supports **LD_PRELOAD** (Linux) and **DLL injection** (Windows) for runtime binary patching
- Patches include: **RSA key replacement**, **server URL redirection**, **JS5 URL redirection**, **configURI override**
- The same binary must work for both live Jagex servers and local private server — controlled by `ServerMode` config (Live vs Custom)
- The ghidra-reverse-engineer agent produces documentation of patch target addresses/patterns in `docs/binary/`
- A **Panama-based** in-process framework is also planned for deeper runtime hooks (future)

### Project Structure
```
darkan-3/
├── core/                          # Shared library (cache, buffer, networking, types)
│   └── src/main/kotlin/
│       ├── org/darkan/core/net/    # Networking layer (networking-protocol-engineer)
│       └── world/gregs/voidps/    # Cache library (cache-library-engineer)
│           ├── buffer/            #   Reader/Writer interfaces
│           ├── cache/             #   Cache, compression, definitions, crypto, JS5
│           └── type/              #   Coordinate, Region, Area, Direction, etc.
├── lobby/                         # Lobby server module
├── world/                         # World/game server module
├── tools/                         # Standalone tools (cache downloader, etc.)
├── client/                        # NXT client binaries and launcher
│   ├── launcher/                  #   Rust launcher app (client-launcher-engineer)
│   │   ├── src/                   #     auth/, config, game/, ui/ modules
│   │   ├── ui/                    #     HTML/CSS/JS for webview UI
│   │   └── Cargo.toml             #     bolt-rs3 crate
│   ├── rs2client                  #   Linux NXT binary
│   ├── rs2client.exe              #   Windows NXT binary
│   ├── rs3linux                   #   Linux launcher binary
│   └── rs3windows.exe             #   Windows launcher binary
├── docs/                          # RE-produced documentation (protocol, cache, JS5, binary)
│   ├── protocol/                  #   Packet formats, login flow, opcodes
│   ├── js5/                       #   JS5 protocol, connection lifecycle
│   ├── cache/                     #   SQLite schema, archive format, compression
│   └── binary/                    #   Patch targets, RSA key locations, memory layout
└── .Codex/agents/                # Agent definitions
```

---

## Development Phases

### Phase 1: JS5 Server & Cache Downloader Tool
**Status: Not started**

**Goal:** Stand up a JS5 file server and build a cache downloader tool to obtain a complete 100% downloaded cache.

**Prerequisites — RE Documentation Required:**
- [ ] JS5 connection handshake protocol (version exchange, key validation)
- [ ] JS5 request/response packet format (archive ID, group ID, priority flags)
- [ ] JS5 response framing (512-byte chunks, 0xFF separators, compression flags, prefetch markers)
- [ ] JS5 master index format
- [ ] Cache SQLite schema (table structure, blob format, addressing)
- [ ] Archive/group compression header format (type byte, compressed size, decompressed size)

**Implementation Tasks:**

#### Cache Downloader Tool (`tools/cache-downloader/`)
Owner: Primarily `cache-library-engineer`, with `networking-protocol-engineer` for the JS5 connection protocol

- [ ] JS5 TCP connection to Jagex servers (handshake, version exchange)
- [ ] Master index download and parsing
- [ ] Per-index iteration: request all groups
- [ ] Response parsing: strip framing, decompress, store
- [ ] SQLite cache storage matching NXT client format
- [ ] Progress tracking, resume capability, integrity verification (CRC)
- [ ] Handle all compression types (GZIP, BZIP2, LZMA)

#### JS5 File Server
Owner: `networking-protocol-engineer` (connection/protocol) + `cache-library-engineer` (file provider)

- [ ] JS5 connection listener (separate from game/login connections)
- [ ] Version handshake validation
- [ ] Request queue processing (urgent vs. prefetch priority)
- [ ] Response encoding: compression flags, prefetch markers (0x80), 512-byte chunk framing with 0xFF separators
- [ ] Master index serving
- [ ] Index/archive/group serving from local SQLite cache
- [ ] Concurrent client support

### Phase 2: Lobby Login
**Status: Not started**

**Goal:** Get the NXT client through the full login handshake and into the lobby screen.

**Prerequisites — RE Documentation Required:**
- [ ] Login protocol state machine (`jag::LoginManager` — all `LoginStep*` methods)
- [ ] Initial connection request type byte and routing (login vs JS5 vs other)
- [ ] RSA block format (ISAAC seeds, credential encoding, token format)
- [ ] Login response codes (success, banned, update required, queue, TOTP, etc.)
- [ ] ISAAC cipher seeding procedure
- [ ] Post-login data packets (server client vars, player data, session config)
- [ ] Lobby-state packet set (what packets keep the client alive in lobby)

**Implementation Tasks:**

#### Login Protocol
Owner: `networking-protocol-engineer`

- [ ] Connection type router (distinguish login, JS5, reconnect requests)
- [ ] Login state machine matching `jag::LoginManager` steps
- [ ] RSA decryption of client login block
- [ ] ISAAC cipher initialization from exchanged seeds
- [ ] Credential validation (initially stub/accept-all for development)
- [ ] Login response encoding (all response code paths)
- [ ] JS5 CRC table construction and validation
- [ ] Session object creation and lifecycle management
- [ ] Server client var transmission
- [ ] Player data serialization for initial login

#### Lobby Server (`lobby/`)
Owner: `networking-protocol-engineer` (packets) + game logic (future)

- [ ] Lobby session keepalive
- [ ] Lobby packet handler registration
- [ ] Minimal lobby UI data (enough for the client to render lobby screen)
- [ ] Logout handling

### Phase 3: Worldlist Management & Social Features
**Status: Not started**

**Goal:** Serve the world list to the lobby client and support basic social systems.

**Prerequisites — RE Documentation Required:**
- [ ] World list packet format (world ID, address, flags, player count, region, activity)
- [ ] Friends list packets (add, remove, status update, world tracking)
- [ ] Ignore list packets
- [ ] Clan system packets (clan chat, clan settings)
- [ ] Friend chat channel packets

**Implementation Tasks:**

- [ ] World list definition and management (world ID, address, capacity, flags)
- [ ] World list packet encoding and serving to lobby clients
- [ ] World status broadcasting (player counts, online/offline)
- [ ] Friends system — add/remove, online status tracking across worlds
- [ ] Ignore system — add/remove, message filtering
- [ ] Clan chat — channel join/leave, messaging, ranks
- [ ] Friend chat — channel creation, join/leave, messaging
- [ ] Cross-world social state synchronization (if multi-world)

### Phase 4: World Login
**Status: Not started**

**Goal:** Get the client from the lobby into a game world with a rendered scene and controllable player.

**Prerequisites — RE Documentation Required:**
- [ ] Lobby-to-world transfer protocol
- [ ] World login handshake (session transfer, player data loading)
- [ ] Map/scene build packet format (region coordinates, XTEA keys, build area)
- [ ] Player info (synchronization) protocol — full documentation of the player update mechanism
  - Position encoding, movement types, appearance mask, update flags
- [ ] NPC info (synchronization) protocol — full NPC update mechanism documentation
- [ ] Core game packets: walk/move, interface interaction, chat, basic actions

**Implementation Tasks:**

#### World Login
Owner: `networking-protocol-engineer`

- [ ] Lobby-to-world transfer handling
- [ ] Player data loading from persistence
- [ ] Initial world state transmission (map regions, interfaces, vars, skills, inventory, etc.)
- [ ] XTEA key management for map regions

#### Player & NPC Synchronization
Owner: `networking-protocol-engineer`

- [ ] Player info protocol implementation (position, movement, appearance, update masks)
- [ ] NPC info protocol implementation (spawn, movement, update masks)
- [ ] Viewport/build area management

#### Core Game Packets
Owner: `networking-protocol-engineer`

- [ ] Walking/movement packets (client→server)
- [ ] Interface interaction packets
- [ ] Chat packets
- [ ] Basic action packets (enough to prove the world is interactive)

---

## Documentation Standards

### RE Documentation (`docs/`)

All protocol documentation produced by the ghidra-reverse-engineer agent must follow these standards:

**Packet documentation (`docs/protocol/`):**
- One file per system or packet group (e.g., `login.md`, `player-info.md`, `interfaces.md`)
- Every packet must specify: opcode, direction, size type (fixed/varByte/varShort), and a field table
- Field tables must include: offset, size, type (using jag::Packet shorthand), field name, description
- Include encoding notes (ISAAC application, special transforms)
- Include pseudocode for complex encoding/decoding logic

**JS5 documentation (`docs/js5/`):**
- Connection lifecycle with sequence diagrams
- Request/response byte-level formats
- Chunk framing specification
- Master index and per-archive index formats

**Cache documentation (`docs/cache/`):**
- SQLite table schemas with column types
- Blob encoding format (compression header, data layout)
- Definition type opcode tables (from client `DecodeType` functions)
- Archive/group addressing

### Code Conventions

- **Packet types:** `PascalCase` (e.g., `AddFriend`, `IfSetText`)
- **Handlers:** `<PacketName>Handler`
- **Definition data classes:** `<Type>Definition` (e.g., `NpcDefinition`)
- **Definition decoders:** `<Type>Decoder` (e.g., `NpcDecoder`)
- **Constants:** `SCREAMING_SNAKE_CASE`
- **Packages:** `org.darkan.core.net.prot` for protocol, `org.darkan.lobby.server.packet` / `org.darkan.world.server.packet` for handlers
