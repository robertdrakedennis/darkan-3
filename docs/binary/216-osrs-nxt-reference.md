# The NXT 216 OSRS Debug Build — Reference-Use Policy

**TL;DR:** `osclient-216-mac` is an **OSRS (Old School RuneScape) DEBUG build**. OSRS and RS3 are
**completely separate games**. Use 216 **strictly as an NXT-ENGINE reference** to name/understand
948's stripped *engine* functions. **NEVER** take opcodes, packet formats, protocol sequences, or
any game-layer / networking detail from 216 — those are OSRS-specific and do **not** apply to RS3
948. The **948-5 binary + 948-5 production captures are the only source of truth.**

## What 216 is

- **`osclient-216-mac`** (in the `rs2client-948` Ghidra project, folder `osrs-debug-build-reference`)
  is the **OSRS NXT client, build 216** — a Mach-O x86_64 **debug build**, so it ships a full symbol
  table (demangled C++ names). Imported purely as a reference. Ghidra image base **`0x100000000`**.
- The target, **`rs2client.948-5`** (Ghidra image base **`0x0`**), is the **RS3** NXT client and is
  **stripped** (auto-named `FUN_xxxxxxxx`).

## Why it is reference-only — the two layers

An NXT client = **a shared C++ engine lib** + **a game-specific layer** on top.

1. **Game layer — NOT transferable.** OSRS (`jag::oldscape::*`) and RS3 are separate games with
   separate content and **separate, incompatible network protocols**. OSRS has **far fewer opcodes**;
   its ServerProt/ClientProt opcode numbers, packet formats, packet handlers, and protocol sequences
   are **OSRS-specific**. RS3 948's protocol (e.g. `jag::packethandlers::*`, the op78/op81/etc.
   numbering, the C2S opcode set) is **completely different** and must be RE'd on the 948 itself.
   **Never port an opcode, packet layout, handler, or protocol sequence from 216 to RS3** — it will
   desync/crash the real 948 client.

2. **Engine layer — referenceable (with drift).** 216 embeds an **NXT engine lib at ~935** (per the
   project owner; ~1–2 years older than 948) — the **same engine family** RS3 948 is built on. The
   engine internals — rendering, scene graph (`graphics::GraphNode`, `game::SceneManager`,
   `game::MapSquare`), map/terrain loading, cache/JS5 engine plumbing, camera, the `jag::Packet`
   buffer primitives — are **largely common**. Because 216 is a debug build, its **symbols name these
   engine functions**, and that is the *only* reason to keep it loaded: when a 948 engine function is
   an unnamed `FUN_…`, its named twin in 216 is how we identify it. Treat **~935 → 948 as drift** —
   it is a starting point/orientation; **verify the exact behavior against 948.**

## How to tell which layer you are looking at (216)

- **`jag::oldscape::*`** → OSRS game layer → **do NOT reference** for RS3.
- Engine namespaces (`graphics::`, `game::SceneManager` / `MapSquare`, cache, `jag::Packet`, …) → the
  embedded NXT engine → **referenceable** for 948's engine (still verify against 948).
- RS3 948 game/protocol (`jag::packethandlers::*`, opcode tables, C2S/S2C) → RE on **948 only**.

## Debug vs release build — INLINING (critical)

216 is a **debug build**; 948 is a **release / optimized build**. The biggest practical consequence:
**948 aggressively INLINES** small functions (buffer accessors / `jag::Packet` `gT*`/`pT*` ops,
getters, short helpers/methods), so in 948 they have **no standalone function** — the logic is
expanded inline at each call site (the "inlined blobs" CLAUDE.md warns the RE agent to pattern-match).
In 216 (debug) those same helpers are compiled as **separate, named functions.**

Implications when cross-referencing 216 → 948:

- **It is NOT a 1:1 function map.** A named 216 helper usually corresponds to an **inlined code
  pattern inside a larger 948 function**, not to its own 948 function. One 948 function may fold in
  several 216 helpers; some 216 functions have **no standalone 948 counterpart at all** (inlined away).
- **This is exactly why 216 is valuable:** it exposes the discrete, *named* operations — with their
  signatures and semantics — that 948 inlines into hard-to-read blobs. Read the 216 function to learn
  *what* an operation is and does, then **recognize that pattern** in the 948 decompile.
- Do **not** expect matching function boundaries, call graphs, or "this function exists in both."
  Conversely, a 948 helper that *is* still its own function may be much terser than its 216 twin
  (release codegen), so reconcile semantics, not instruction-for-instruction.

## Hierarchy of truth (darkan3)

1. **948-5 binary (`rs2client.948-5`) + 948-5 production captures** — the ONLY source of truth.
2. **216 (`osclient-216-mac`)** — **NXT-engine reference only** (~935); names 948's stripped engine
   functions. Never protocol/opcodes/formats.
3. **alerion (910)** — concept/structure/service-architecture + protocol *shape* only; never byte
   formats. (See the `client-948-5-is-truth` memory.)

## Switching the MCP between programs

GhidraMCP serves the tool's **active** program. Switch it with `run_java_class` running a
`GhidraScript` that calls `ProgramManager.openProgram(domainFile)` for `/rs2client.948-5` (target) or
the 216 (reference). Remember the base difference: **948-5 = `0x0`** (doc RVAs are absolute), **216 =
`0x100000000`** (add the base when applying a 948 doc address against 216, and vice-versa).
