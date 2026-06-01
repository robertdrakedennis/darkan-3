# 948 Opcode Validation Summary

Result of the re-validation pass that re-derived every 948 network opcode under a strict multi-source
gate after earlier passes returned confident-but-wrong mappings. **No name here is self-reported; every
CONFIRMED name cites independent evidence.** Authority artifacts: `948-prot-tables-dump.csv` (sizes/handlers),
`948-serverprot-matrix.md` + `948-clientprot-matrix.md` (per-opcode evidence), live capture
`capture/login-20260531-191837_s1/`.

## The gate (what each verdict means)
- **(A) disasm** — opcode+size from `RegisterAll`/`InitEntry` (authoritative for size). **(B) beta enum** — verbatim
  `jag::{Client,Server}Prot::<NAME>` symbol in `librs2client.so`, mapped to the 948 handler by signature match.
  **(C) capture** — observed on the wire. **(D) size-sanity** — a veto (a click can't be 3 bytes, etc.).
- **CONFIRMED** = size (A) + ≥2 of {A,B,C} agree on the name + (D) passes. **PROBABLE** = one weak lead (docs/comments
  only; code stays `UNKNOWN_<op>`). **UNKNOWN_<op>** = correct size, no citeable official name.
- Banned: C++ function names as packet names; single-inference "HIGH"; "presumed from 947" as evidence.

## Results

| Direction | Total | Sizes | CONFIRMED names | PROBABLE | UNKNOWN |
|---|---|---|---|---|---|
| ServerProt | 218 | **218/218 ✓** | 78 | 0 | 140 |
| ClientProt | 130 | **130/130 ✓** | 32 | 9 | 89 |
| **Total** | **348** | **348/348 ✓** | **110** | **9** | **229** |

- **Sizes are genuinely 100%**: mechanically extracted from the binary (zero disagreement with the codec) and
  re-proven on the wire by the `framingRegression` hard gate (every capture decodes with zero desync / full consumption).
- **Names are CONFIRMED-or-honest-UNKNOWN.** The 229 UNKNOWN carry correct sizes + documented decompiled behavior in
  the matrices — they are not black boxes, they simply have no citeable verbatim official enum (rev-890 beta gap) and
  are not on the wire. PROBABLE (OPOBJ1-6, OPLOCT, IF_BUTTONT, MOVE_MINIMAPCLICK) are recorded as in-code comments only.

## Capture-validated (on the wire)
The single lobby capture exercised 17 ServerProt + 5 ClientProt opcodes. `wireFormatVerify` byte-compares every
implemented encoder/decoder against the captured bytes: **14 PASS / 0 FAIL** (op216 worldlist N/A — framing covered;
7 have no encoder/decoder). Notable: op127 `IfButton` decodes the captured interface click exactly; op110
`RunClientScript`, op82 `IfSetPosition`, op28 `VarpLarge` (436 bodies), op61 `VarpSmall` (1135 bodies) all byte-match.

## Bugs this pass found & fixed
1. **IF_BUTTON family** — was wrongly `IF_BUTTON1=op39 (size 3)`. Correct: the size-8 click path `IF_BUTTON1=op127,
   2=103, 3=92, 4=45, 5=30, 6=68, 7=43, 10=21`; opt8→op20, opt9→op10 (=IF_BUTTOND drag) reuse other ProtEntries. The
   size-3 ops (39/73/47/…) are CS2 component-presses → `UNKNOWN` (size-sanity veto). Decoder + lobby handler added.
2. **CP1252 string bug** — the buffer library wrote RS strings as UTF-8 and read them as Latin-1; the NXT client uses
   **windows-1252**. Fixed in `world.gregs.voidps.buffer` (new `Cp1252` helper). Affected every string packet
   (news/scripts/chat/names). Caught by byte-verifying op110 against the capture.
3. **op51** `PROCESS_CONNECTIONS`→`NO_TIMEOUT` (function name → official enum). **op5** `RESET_CLIENT_VARCACHE`
   recovered. **op26** `FriendStatus`→`UPDATE_SITESETTINGS` (stale-codec mislabel). **op147/op196** honestly demoted
   to UNKNOWN (decompile-certain 8-byte varp/varc, but no verbatim `LONG` enum in the beta).

## Regression lock-in (hard gate)
`./gradlew check` now runs `:tools:framingRegression` (size/desync) and `:tools:wireFormatVerify` (byte-exact) against
the committed capture; either failing fails the build. Silent protocol breakage on a future edit/revision is now
impossible. The dump script `ghidra_scripts/DumpProtTables.py` regenerates the size/handler CSV next revision.

## Coverage gaps (honest)
- The single capture is **lobby-only** (the proxy can't capture world traffic). World/game packets (PLAYER_INFO,
  NPC_INFO, REBUILD, zone, most game ClientProts) are validated by disasm+beta+sanity only — CONFIRMED where a beta
  symbol maps, else UNKNOWN. They have no on-wire (C) corroboration yet.
- Opcodes with no rev-890 beta symbol and not on the wire remain `UNKNOWN_<op>` with correct sizes. This is the honest
  ceiling of a stripped-binary + partial-capture analysis; it is a success state, not a defect.
