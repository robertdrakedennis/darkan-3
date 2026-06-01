# 948 Lobby SIGSEGV — Crash Diagnosis (post-WORLDLIST_FETCH burst)

**Date:** 2026-05-31
**Binary:** `rs2client.948-2-2` (Ghidra port 8081). Reference: beta `librs2client.so` (port 8080, rev ~890).
**Symptom:** NXT client sends a crash report ("Signal Segmentation fault (Invalid pointer access)")
while in the **lobby**, immediately after the server replies to `WORLDLIST_FETCH`.

> **TL;DR.** The crash is **NOT** in the WORLDLIST_FETCH_REPLY decode. WORLDLIST_FETCH_REPLY (op
> 216 @ `0x0018fea0`) decodes byte-for-byte compatibly with our encoder. The crash is in the
> per-frame **interface-component event flush** (`FUN_00229d80`, called once per lobby frame from
> `LoginProtocolHandler`), which applies the update entries that IF_OPEN* packets queued. The
> primary root cause is that **our lobby sends the wrong interface-setup packets**: generic
> `IF_OPENTOP` (6B) + `IF_OPENSUB` (8B), whereas the real Jagex lobby uses
> `IF_SETTOPLEVELINTERFACE` (19B) + N× `IF_SETPOSITION` (23B). The crash timing (right after the
> worldlist) is incidental — it is just the next frame tick after the lobby-init packet burst.

---

## 1. Resolved backtrace

Crash-report code addresses (rs2client.948-2-2 address space, match Ghidra directly):

```
0x000c04a0  0x00250aba  0x004b386c  0x0090bf81  0x000c19c0  0x00027741  0x00027879  0x000eb5aa
```

| Addr | Resolves to | Role |
|------|-------------|------|
| `0x000eb5aa` | `_start` (`HLT` after `__libc_start_main`, calls main `0xc04a0`) | C runtime bottom of stack — machinery |
| `0x000c04a0` / `0x000c19c0` | `FUN_000c04a0` = `jag::Client` main loop (`0xc19c0` is the return after it calls the frame tick) | main loop — machinery |
| `0x004b386c` | crash-report / signal-handler path (also in prior crash) | machinery |
| `0x00027741`, `0x00027879` | **In `.rela.dyn` (`0x5d20`–`0x6f9f7`), NOT `.text` (`.text` starts `0x74ec0`)** | **NOT code — not real frames** (report list padding / non-code values) |
| `0x0090bf81` | inside `FUN_0090bcc0` — per-frame **frame-tick / FPS update**; return addr after `CALL [R8+0xf8]` (active GameShell state's per-frame vtable method). Called from `FUN_000c04a0 @ 0x000c19bc`. | per-frame state tick — the lobby state's update |
| **`0x00250aba`** | **inside `jag::ConnectionManager::LoginProtocolHandler` (`0x0024e700`–`0x00253741`)**; return addr right after `CALL 0x00229d80` at `0x00250ab5` | **the lobby per-frame pump; the call that faulted** |

**Faulting call site** (disasm of `LoginProtocolHandler`):
```
00250aaa  MOV  RBP, [RBX+0x8]          ; RBP = connection-state object
00250aae  MOV  RDI, [RBP+0x19450]      ; RDI = per-frame event-queue sub-object (the param to 0x229d80)
00250ab5  CALL 0x00229d80              ; <-- FAULT occurs inside this call
00250aba  MOV  RSI, [RBX+0x8]          ; <-- the reported return address
```

**Deepest non-machinery frame = `0x00229d80`** (return addr `0x00250aba`). The "other prior-crash"
addresses (`0x000c04a0`, `0x004b386c`) and `0x000c19c0` are the crash/main machinery, exactly as
the task anticipated. The two sub-`.text` values are not stack frames.

### `0x00229d80` — the fault function (per-frame interface-event flush)

Renamed in Ghidra only with a HYPOTHESIS comment (left as `FUN_` per the certainty rule). Role:
- Walks **two `eastl::rbtree` event queues** (`param_1+0x20`, `param_1+0x28`) plus a dynamic vector.
- For each pending event calls `FUN_00228630` (the per-event applier).
- Then, when connection state ∈ {20, 22, **30**} (bitmask `0x40900000`, `BT`), conditionally builds
  and sends a client message (`Packet::SendClientMessage`).
- First instruction dereferences `param_1 = *(state+0x19450)` then `*(param_1+0x40)`.

Beta `librs2client.so` has `jag::InterfaceManager::RunCollectedEventList @ 0x005b1140` with the same
role (drain a per-frame collected-event list), but a drifted body (it runs `ScriptRunner` hooks over
`HookRequest` shared_ptrs). Behavioral match is **partial** → not renamed (Cardinal Rule 1).

### `0x00228630` — per-event applier (the actual deref that faults)

`*param_2` = event opcode (1..0x16). Almost every case does:
```c
comp = jag::game::InterfaceList::GetStaticComponent(interfaceList+0x30, param_2[1]); // param_2[1] = packed component id
... dereference *(comp + 8) ...   // component data object; vtable calls / field writes
```
Event kinds are created by `InterfaceManager::CreateOrFindUpdateEntry`:
- `IF_OPENTOP` → kind **0xc**
- `IF_OPENSUB` → kind **0xb**

If a queued event references a component whose **parent interface was opened with a bad packed id**,
`GetStaticComponent` returns a partially/incorrectly resolved component and the subsequent deref of
`*(comp+8)` (or a vtable call through it) faults → **SIGSEGV here**.

---

## 2. Culprit packet

**Primary (high confidence): the lobby interface-open packets are the WRONG packets.**

Live Jagex 947 lobby capture (`capture/login-20260326-170439_s1/decoded.log`, lines 2291–2331) shows
the real lobby interface setup uses:

| Real lobby packet (decoder label) | Size | Identified 948 packet | 948 op | 948 handler |
|-----------------------------------|------|------------------------|--------|-------------|
| `IfOpenTopLobby` | **19B** | **`IF_SETTOPLEVELINTERFACE`** | 3 | `0x00186900` |
| `IfOpenSubLobby` (×N) | **23B** | **`IF_SETPOSITION`** | 82 | (23B handler) |

Our lobby (`lobby/.../LoginServer.kt` lines 320–335) instead sends:
- `IfOpenTop` (op 39, **6B**) — handler `0x00194050`
- `IfOpenSub` (op 94, **8B**) ×21 — handler `0x00194100`

These are the **generic in-game** interface-open packets, not the lobby variants. They queue update
entries (kinds 0xc/0xb) for components/parents that don't match what the lobby UI script expects,
leaving dangling/half-initialised component entries. The very next frame, `FUN_00229d80` →
`FUN_00228630` walks those entries and `GetStaticComponent` dereferences a bad pointer → crash. The
crash lands "after WORLDLIST_FETCH_REPLY" only because that reply is the last packet in our lobby
init burst, so the first frame flush after it is where the bad entries are processed.

**Decode evidence — real `IfOpenSubLobby` (23B) == IF_SETPOSITION:**
```
00 00 00 00 | 8B 03 | 00 00 00 00 | 00 00 00 00 | FF | 00 00 00 00 | 00 2C 03 8A
[0-3] int    [4-5]   [6-9] iim     [10-13] LE     [14] [15-18] iim   [19-22] mid
writeIntLittle(0) writeShortLittle(componentId=0x038B=907) ... writeByteInverse(layer) ... writeIntMiddle(position)
```
This matches the `IF_SETPOSITION` (op 82) encoder field-for-field.

**Decode evidence — real `IfOpenTopLobby` (19B) == IF_SETTOPLEVELINTERFACE:**
handler `0x00186900` reads: `gT_uint`(4B discard), skip 1B, `g4_alt2`(4B discard),
2B id at offset 9-10 (`id = byte10*0x100 + (byte9+0x80) & 0xFFFF`), `g4_alt1`(4B discard),
`g4_alt1`(4B discard) = 19 bytes. Our `IF_SETTOPLEVELINTERFACE` (op 3) encoder already matches this.

**Secondary (lower confidence): IF_OPENTOP topLevelId packing.** Even if the lobby is meant to use
IF_OPENTOP, our `IfOpenTop(topLevelId = 906)` sends a bare interface id. The handler stores it as the
`CreateOrFindUpdateEntry` key; the real protocol top-level key is generally a packed component hash.
If 906 should be `(906<<16)` or similar, the IF_OPENSUB `parentHash = (906<<16)|component` will never
match the top entry → same dangling-component class of crash. This is subsumed by the primary finding
(we should not be sending IF_OPENTOP/IF_OPENSUB for the lobby at all).

**Ruled out: WORLDLIST_FETCH_REPLY (op 216).** See §4. Its handler is not on the backtrace, and its
wire format matches our encoder.

---

## 3. WORLDLIST_FETCH_REPLY (op 216) — exact 948 wire format (verified)

Handler `jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY @ 0x0018fea0`, bound by main
`ServerProt::BindHandlers @ 0x00077b00`. It is a **fragment reassembler**: every segment begins with
a 1-byte `frame` flag (`1` = last); the remaining bytes accumulate into a buffer; when `frame==1` the
full body is parsed. Helper identifications (decompiled):
`FUN_0018fe70` = g4 (BE u32), `FUN_0018f6f0` = g2 (BE u16), `FUN_00ae7a60` = jag-string read
(`gjstr2`: leading 0x00 marker then null-terminated, else empty), `Packet::getUnsignedSmart` =
`gSmart1or2`.

Reassembled body layout:
```
g1     frame                 (per segment; 1 = last)   [stripped during reassembly]
--- body ---
g1     0x02                  version marker            (handler requires == 0x02)
g1     0x01                  "has world defs" marker   (handler requires == 0x01)
gSmart countryCount
  per country:
    gSmart countryId
    gjstr2 countryName
gSmart minWorldId
gSmart maxWorldId
gSmart worldCount
  per world:
    gSmart worldId offset                 (worldNum = minWorldId + offset)
    g1     countryIndex                    (index into country table; MUST be < countryCount)
    g4     flags  (u32, BE)                (0x1 members, 0x2 quickchat, 0x4 pvp, 0x8 lootshare, 0x10 highlighted, 0x40000000 nonstd port)
    gSmart activityPresent
       if activityPresent != 0: gjstr2 activityName
    gjstr2 displayName/hostname
    gjstr2 address
g4     revision (u32, BE)
--- player-count section (remaining bytes) ---
  per world (worldCount entries):
    gSmart worldId offset
    g2     playerCount  (0xFFFF = unchanged/offline)
```
Verified against the live Jagex 947 worldlist (`capture/login-20260326-220937_s1`): 9 countries, 133
worlds (range 1–259), 266 strings; structural decode consumed every byte. 947 op was 159; 948 op is
216 (NoTimeout moved off 216). Protocol body is byte-identical 947→948.

---

## 4. Our encoder vs the verified format

File: `core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/Rev948ServerCodecsMisc.kt`
(`serverProt<WorldListPacket>(opcode = 216 …)`, lines 127–179).

| Field | Verified | Our encoder | Verdict |
|-------|----------|-------------|---------|
| frame=1, marker 0x02, marker 0x01 | yes | `writeByte(1)`,`writeByte(2)`,`writeByte(1)` | OK |
| countryCount + (id,name)* | smart + (smart, gjstr2)* | matches | OK |
| minWorldId / maxWorldId / worldCount | smart×3 | matches | OK |
| per-world: offset, countryIdx(g1), flags(g4), actPresent(smart), strings, revision(g4) | as above | matches field-for-field | OK (string count matches: actPresent=0 ⇒ 2 strings) |
| player counts | smart + g2 | matches | OK |

**Conclusion: the WORLDLIST_FETCH_REPLY encoder is byte-compatible.** It does not cause the crash with
the current single-world lobby data. Two latent (non-crashing-now) issues to fix opportunistically:

- **Latent A — `countries.indexOf(world.country)` can return `-1`** → `writeByte(-1)` = `0xFF` →
  out-of-bounds country index → would crash the op-216 decode once a world's country isn't in the
  deduped list. Cannot happen today (`countries` is built from the same worlds), but guard it.
  *Fix:* coerce to `0` / assert it is `>= 0` (`Rev948ServerCodecsMisc.kt:154`).
- **Latent B — field semantics:** our two unconditional strings are `(activity, hostname)`; the client
  reads them as `(displayName, address)`. Byte-count is fine; display/connect mapping is slightly off.
  *Fix (cosmetic):* write `(world.displayName, world.hostname)` so the connect address lands in the
  client's address slot (`Rev948ServerCodecsMisc.kt:165-166`).

---

## 5. The fix (for networking-protocol-engineer — do NOT let RE edit encoder code)

**Primary fix — replace the lobby interface-open packets.**
File: `lobby/src/main/kotlin/org/darkan/lobby/server/LoginServer.kt`, lines **317–336**.

Replace the `IfOpenTop` + `IfOpenSub` loop with the lobby variants that the real Jagex lobby uses and
that the 948 client expects:

1. **`IfSetTopLevelInterface(topLevelId = 906)`** (op 3, 19B, handler `0x00186900`). Encoder already
   exists in `Rev948ServerCodecsInterface.kt` (op 3). This replaces the `IfOpenTop(906, 0)` call.
2. For each lobby sub-interface, **`IfSetPosition(componentId, layer, position)`** (op 82, 23B,
   encoder exists in `Rev948ServerCodecsInterface.kt`). This replaces each `IfOpenSub(...)`.
   - Per the live capture, each sub uses: `componentId` = the sub-interface component (e.g. 0x038B),
     `layer = 1` (byte `FF` = `writeByteInverse(1)`), `position` packed (the trailing `00 2C 03 8A`
     middle-endian field). Map our `LOBBY_SUB_INTERFACES` (parentComponent → subIfId) onto these.
   - **A fresh lobby capture from the 948 client is required to get the exact 23B field values per
     sub-interface** (component ids, layer, packed position). The 947 capture gives the structure;
     the 948 component ids may differ.

**Also stop sending the wrong opcodes:** confirm the lobby init no longer emits any `IfOpenTop` /
`IfOpenSub` during lobby setup (those are in-game-only).

**Stale comments to fix** (non-functional, but misleading): `LoginServer.kt:318` says "IF_OPENTOP: op
68" and line 321 "IF_OPENSUB: op 17" — those are 947-3 opcodes; the 948 codec remaps to 39/94. Once
the packets are swapped to IF_SETTOPLEVELINTERFACE/IF_SETPOSITION these comments are replaced anyway.

**Opportunistic worldlist guards** (Latent A/B above) in `Rev948ServerCodecsMisc.kt:154,165-166`.

---

## 6. Confidence & whether a live capture is needed

- **Backtrace resolution: conclusive** (decompile + disasm; the two sub-`.text` "addresses" are
  provably non-code).
- **WORLDLIST_FETCH_REPLY is NOT the culprit: conclusive** (encoder is byte-compatible; the op-216
  handler is absent from the stack; the fault is in the per-frame interface flush).
- **Primary root cause (wrong lobby interface-open packets): high confidence** from the live Jagex
  capture (lobby uses 19B IF_SETTOPLEVELINTERFACE + 23B IF_SETPOSITION) + 948 handler decompiles +
  the fault path being the interface-component event applier. 
- **A fresh 948-client lobby capture is needed to be 100% sure of the exact IF_SETPOSITION (23B) field
  values** (component ids / layer / packed position per sub-interface). The crash *cause* (we send the
  wrong packets) is established without it; the *exact replacement byte values* should be confirmed
  against a 948 capture before shipping. Recommend capturing a real Jagex 948 lobby session (the
  LoginProxy in `tools/.../loginproxy` already supports this) and decoding ops 3 / 82.

## 7. Ghidra changes made (target `rs2client.948-2-2`)
- `0x00229d80`: decompiler comment marking it as the **crash fault site** + HYPOTHESIS (per-frame
  interface-event flush; partial match to beta `RunCollectedEventList`). **Not renamed** (uncertain).
- `0x00228630`: decompiler comment documenting it as the per-event interface-component applier and the
  immediate deref that faults. **Not renamed** (uncertain).
- No other renames/prototypes committed (the IF_* and WORLDLIST handlers were already named/commented
  in this DB and verified correct against the wire format).
