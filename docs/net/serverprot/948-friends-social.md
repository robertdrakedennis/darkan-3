# 948 Friends / Social ServerProt — Definitive RE (friends-list regression fix)

Target binary: `rs2client.948-2-2` (Ghidra, STRIPPED) — AUTHORITY for opcode/size/handler.
Official enum source: `librs2client.so` beta (rev ~890) — `jag::ServerProt::*` verbatim symbols + 947-equivalent handler bodies.
Sizes: `docs/net/948-prot-tables-dump.csv` (RegisterAll/InitEntry).
Verified: 2026-06-02.

> **TL;DR for the networking agent.** The migration's premise was WRONG. `op26` is **NOT** UPDATE_SITESETTINGS — it **IS** the friend-list packet (UPDATE_FRIENDLIST / `FriendStatus`). The existing `serverProt<FriendStatus>(opcode = 26, ...)` registration in `Rev948ServerCodecsMisc.kt:188` **already has the correct format and the correct opcode**. `op26` size=0 = "empty friend list, mark tab loaded". `op130` is a different packet (a flag-masked single-relationship delta), it is **NOT** UPDATE_IGNORELIST and **NOT** UPDATE_FRIENDLIST — keep its encoder disabled. FRIENDLIST_LOADED has **no separate 948 handler** that I could prove; `op26` alone marks the tab loaded.

---

## 1. UPDATE_FRIENDLIST  (a.k.a. FriendStatus / FRIEND_STATUS)

| field | value |
|---|---|
| **948 opcode** | **26** (0x1A) |
| **size** | **VarShort** (-2) |
| **handler addr** | **0x001a44e0** (thunk 0x001a63e0; binder entry 0x013a19e0) |
| **beta enum (verbatim)** | `jag::ServerProt::UPDATE_FRIENDLIST` (mangled `_ZN3jag10ServerProt17UPDATE_FRIENDLISTE`) |
| **CONFIDENCE** | **CONFIRMED** |

### Gate evidence
- **(B) beta enum** `UPDATE_FRIENDLIST` exists verbatim.
- **(B) beta handler match** — 948 op26 body is byte-for-byte the beta `jag::packethandlers::Friends::Friends(Client&)::{lambda(Packet&,int)#1}` reader (inlined body @ beta `0x0056b660`; thunk `0x0056cdd0`). Same read order, same per-record store stride, same `cVar!=1` branch, same `flags>>1&1` bit split, same world-block gate on `worldId>0`, same final `SortFriendsList` + `PlayerList::IteratePlayers → RelationshipManager::IsFriend`.
- **(947) behavioral equivalent** — identical to `Rev947ServerCodecsMisc.kt:98` (947 op102) and `docs/net/serverprot/update-friendlist.md`.
- **(D) size-sanity** — list packet ⇒ VarShort. PASS.
- **(C) capture** — `op26 size=0` (empty list) in `capture/login-20260531-191837_s1/decoded.log:3462`. Consistent: empty payload ⇒ record loop skipped, still sets LOADED=2.

### Why the prior pass got it wrong
The matrix/research-B (`948-research-B-interface-social.md:228`, `948-serverprot-matrix.md:59`) called op26 "UPDATE_SITESETTINGS" from an INCOMPLETE read ("2 strings + smart + int"). The FULL handler reads **four** strings + the warnMessage/fcRank/flags bytes + the conditional world block. The beta `UPDATE_SITESETTINGS@0x00a371c0` cited as evidence does not even resolve to a function (it is a ProtEntry data global). The friend-record loop + `IsFriend` refresh is unmistakably the friend list, not site settings.

### Wire format — per-friend record, looped `while (pos < payloadLen)`
| # | shorthand | jag::Packet | field | notes |
|---|---|---|---|---|
| 1 | g1   | gT<unsigned_char>  | warnMessage / flags | read first; client branches on `!= 1` |
| 2 | gStr | gStringCP1252ToUTF8 | displayName | null-terminated CP1252 (`FUN_00ad89a0`) |
| 3 | gStr | gStringCP1252ToUTF8 | previousName | "" if no rename |
| 4 | g2   | gT<unsigned_short> | worldId | BE (`FUN_001218b0`) |
| 5 | g1   | gT<unsigned_char>  | fcRank / chatRank | byte |
| 6 | g1   | gT<unsigned_char>  | flags | bit0 → field A, bit1 → field B (client splits `&1`, `>>1&1`) |
| 7a| gStr | gStringCP1252ToUTF8 | worldName | **only if worldId > 0** |
| 7b| g1   | gT<unsigned_char>  | platform | **only if worldId > 0** |
| 7c| g4   | gT<unsigned_int>   | worldFlags | **only if worldId > 0**, BE (`FUN_001218b0`→`gT_unsigned_int`) |
| 8 | gStr | gStringCP1252ToUTF8 | notes | personal note string |

After the loop the client sorts the friend vector and sets `RelationshipManager(*(Client+0x194f8)) + 0x10 = 2` (LOADED_WITH_DATA), then runs `IteratePlayers → IsFriend`. **A size-0 op26 marks the friend tab loaded with an empty list** (the behavior seen live).

### Ghidra-determinable?  **YES — fully.** No capture needed; format is byte-exact from the handler (and matches 947 + beta). The existing Kotlin encoder is correct.

---

## 2. FRIENDLIST_LOADED

| field | value |
|---|---|
| **948 opcode** | **UNKNOWN** (not isolated) |
| **size** | unknown (947 was VarShort) |
| **handler addr** | not found as a distinct 948 handler |
| **beta enum (verbatim)** | `jag::ServerProt::FRIENDLIST_LOADED` (`_ZN3jag10ServerProt17FRIENDLIST_LOADEDE`) — exists |
| **CONFIDENCE** | **UNKNOWN** |

### Findings
- The beta enum `FRIENDLIST_LOADED` exists. In the beta it maps to `Friends::Friends lambda#2` (`0x001a6ae0`), a **size-0** handler that reads nothing and sets `RelationshipManager+0x10 = 1` (a *lesser* loaded state than UPDATE_FRIENDLIST's `=2`).
- 947 had it as a **distinct** packet: op66, VarShort, handler `0x00225bf0` (`docs/net/serverprot/misc.md:164`, "was FRIEND_STATUS").
- In **948 I could not isolate a separate handler** that sets the RelationshipManager loaded-state to 1. Every 948 handler that touches the RelationshipManager (`*(Client+0x194f8)`) was enumerated (sig-scan of `mov reg,[reg+0x194f8]`): the only record-reader is op26; the rest are chat/clan/site handlers. No tiny size-0/VarShort "loaded=1" handler appeared.
- **Practical consequence:** `op26` (UPDATE_FRIENDLIST) **already sets LOADED=2 unconditionally** (even for an empty payload), so the friends tab is marked loaded by op26 alone. A separate FRIENDLIST_LOADED packet appears **not required** to populate/flag the tab in 948.

### Ghidra-determinable?  **Opcode: NO (not provable from Ghidra alone).** A **lobby capture with a friended account** would show whether the live server emits a second small friend packet besides op26. Recommendation: do not register a FRIENDLIST_LOADED encoder; rely on op26 (size-0 when empty). Revisit only if a friended-account capture shows an unhandled small social opcode after op26.

---

## 3. UPDATE_IGNORELIST

| field | value |
|---|---|
| **948 opcode** | **UNKNOWN** as a distinct ignore-pairs list (op130 is NOT it) |
| **size (op130)** | VarByte (-1) |
| **op130 handler addr** | 0x001d29c0 |
| **beta enum (verbatim)** | `jag::ServerProt::UPDATE_IGNORELIST` (`_ZN3jag10ServerProt17UPDATE_IGNORELISTE`) — exists |
| **CONFIDENCE** | **UNKNOWN** (op130 ≠ ignore-pairs; true ignore handler not found) |

### Findings
- Beta `UPDATE_IGNORELIST` maps to `jag::packethandlers::Ignores::Ignores lambda#1` (`0x0068de90`): a loop of **{1 flag byte + 3 CP1252 strings}** ignore records stored into a 0x68-stride vector. That is the classic 947 ignore-pairs format (`Rev947ServerCodecsSocial.kt`, 947 op211 / also 947 op17).
- **948 op130 (`0x001d29c0`) does NOT match that.** It reads a **64-bit flag mask** (`gT_ulong`) then ~48 conditionally-gated fields — `g4` uints (`FUN_00135040`), `g2` (`FUN_001218b0`), coord triples of 3×`g4` (`FUN_00545ef0`, xor `DAT_00cb6930`), and packed-uint→3-float colour fields — and stores a **single 0x1e0-byte relationship entry** to `*(RelationshipManager+0x98)` with dirty flag `+0xa0=1`. This is a per-entry **RELATIONSHIP DELTA**, not a list of (displayName, previousName) ignore pairs.
- **No 948 handler reads a 3-string ignore-record loop.** Exhaustive xref of the CP1252 string reader (`0x00ad89a0`) shows only op26 (4 strings) and the chat/clan handlers (1–2 strings). So the 947-style ignore-pairs packet has no 948 destination I can prove.

### op130 flag-mask field map (partial, for future capture-driven work)
`mask = gT_ulong`. If `mask==0`: empty/clear entry. Else, per set bit (low→high; `0x2d`+ are extension bits encoded in the high dword):
`bit0`→g4 id; `bit1..bit5,0x20,0x23..0x2a,0x2f`→g4 values; `bit4(0x10),0x20,0x21,0x22`→coord-triple(3×g4, xor 0x..cb6930); `bit6(0x40)`→g4; `bit7(0x80),0x8000`→g2; `bit0x100..0x800,0x1000..0x4000`→g4; `bit0x10000`→g2 then skip 8B; `bit0x17/18/19000`→g2+g4 pairs; `bit0x100000/0x8000000`→g4 bool(==1); `bit0x2b/0x2c`→packed-uint→3 floats×`DAT_00cb682c`. Trailing: g2. (Decompile @ 0x001d29c0; bit→field mapping is read-order-exact but field *semantics* are not byte-confirmable without a capture.)

### Ghidra-determinable?  **NO for the ignore list.** op130's *read order* is Ghidra-determinable, but its identity (is it UPDATE_IGNORELIST? UPDATE_FRIENDLIST-delta? a new RELATIONSHIP packet?) and field semantics need a **live lobby capture with a friended/ignored account**. **Keep the op130 encoder DISABLED** (as it currently is). Do not emit the 947 ignore-pairs bytes at op130 — the client would desync.

---

## 4. UPDATE_SITESETTINGS  (op26 identity correction)

| field | value |
|---|---|
| **948 opcode** | **UNKNOWN** (it is NOT op26) |
| **beta enum (verbatim)** | `jag::ServerProt::UPDATE_SITESETTINGS` (`_ZN3jag10ServerProt19UPDATE_SITESETTINGSE`) — exists |
| **CONFIDENCE** | **UNKNOWN** for its 948 opcode; **CONFIRMED** that it is NOT op26 |

### Findings
- The beta enum `UPDATE_SITESETTINGS` exists, but **op26 is not it** — op26 is the friend list (§1). The "UPDATE_SITESETTINGS" label on op26 in the dump/matrix/research-B is a misidentification.
- The live capture's `op26 size=0` is therefore an **empty friend list, not** an empty site-settings packet. The "size-0 = site-settings init" reading in the matrix was based on the wrong identity.
- I did not locate a separate 948 handler for the genuine UPDATE_SITESETTINGS in this pass. If the live server sends a small/empty packet during lobby init that is NOT op26, that is the candidate — but the op26/size0 we have is the friend list.

### Ghidra-determinable?  **Opcode: NO** without a capture that distinguishes it from op26. The only "empty during lobby init" packet observed (op26 size=0) is the friend list, not site-settings.

---

## Ghidra changes applied (rs2client.948-2-2)
- `0x001a44e0` renamed → `jag::packethandlers::Friends::UPDATE_FRIENDLIST` (+ full wire-format decompiler comment correcting the UPDATE_SITESETTINGS mislabel).
- `0x001d29c0` renamed → `jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED` (was the false `UPDATE_IGNORELIST_thunk` name) (+ decompiler comment with the flag-mask field map and "NOT ignore-pairs" warning).
- No edits to `librs2client.so` (read-only reference).

## Net guidance for the networking pass (no code written here)
1. **op26 = `FriendStatus` is correct as-registered.** Keep `serverProt<FriendStatus>(opcode = 26, size = VarShort)` (`Rev948ServerCodecsMisc.kt:188`). The format there is byte-exact.
2. To populate the friends list: send **op26** with the per-friend records above; send **op26 size=0** for an empty list (this also marks the tab loaded). No separate FRIENDLIST_LOADED packet is needed in 948 as far as the binary shows.
3. **Do NOT** register an encoder at **op130** with the 947 ignore-pairs format — op130 is a flag-masked relationship delta (encoder stays disabled until a friended-account capture).
4. The current `s(130, "UPDATE_IGNORELIST", ...)` stub name is misleading; treat op130 as UNKNOWN/relationship-delta.
5. If a live test with a friended account still fails to populate, capture it — the only remaining unknowns (true UPDATE_IGNORELIST opcode, whether a distinct FRIENDLIST_LOADED exists, true UPDATE_SITESETTINGS opcode) require a friended/ignored-account lobby capture.
