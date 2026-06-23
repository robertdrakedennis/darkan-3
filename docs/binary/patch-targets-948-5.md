# Patcher Targets - Rev 948-5 (Windows)

Binary: rs2client.exe (Windows PE32+, Rev 948-5)
SHA1: `26652399900bedf64ff076f117b712e764a865a0`
Size: 14,097,008 bytes
Image base: `0x140000000`
PE BuildID (CodeView GUID + age): `ab282051d1e073478195b05a455d9bcd00000001`
Original PDB path (from CodeView record): `D:\bb-b-01\NXT-RSC384-BWXR\build\nxt\bin\rs2client\FINAL\rs2client.pdb`
Cross-reference: rs2client.948-2-2 (previous target — **Linux** ELF build)
Launcher binary: rs3windows.exe (NOT analyzed in this revision; archive has rs2client.exe only — see "Open Items")

## Summary

This is the **Windows PE** build of rev 948-5. The prior reference (`patch-targets-948.md`) targets the **Linux ELF** build of 948-2. Three things have changed:

1. **The binary format itself is different** — Windows PE32+ vs Linux ELF. File-offset/VA conventions, segment layout, and calling convention all shift. The Linux LD_PRELOAD patcher (`libdarkan_patcher.so`) cannot run against this binary directly; a Windows DLL-injection patcher is needed.
2. **Both rs2client RSA keys (login + JS5) were RELOCATED, not ROTATED.** The 256-char login modulus and 1024-char JS5 modulus are **byte-identical** to 948-2 — same first 32 hex chars, same full value. Only their `.rodata` addresses moved. No `DARKAN_*_RSA_MODULUS` env-var change is required.
3. **The HTTP port-80 patch pattern changed** because MSVC emits different instructions than gcc/clang. The Linux 948-2 pattern `41 b8 50 00 00 00 74` (MOV R8D, 0x50 + JZ short) is gone; the Windows 948-5 equivalent is `b8 50 00 00 00 eb` (MOV EAX, 0x50 + JMP short). The patcher needs a Windows-specific port pattern alongside the Linux one.

### Required Patcher Changes (Windows DLL build)
- **Login modulus prefix:** `aad4a7804c34bb788d52dbd5f70e5721` — **UNCHANGED** vs Linux 948-2. The current `RS2CLIENT_MODULUS_PREFIX` is correct; only the resolved address changes (handled automatically by pattern search).
- **JS5 modulus prefix:** `a6400fbcbd9dd09f48045caf3f543dd6` — **UNCHANGED** vs Linux 948-2. The current `RS2CLIENT_JS5_MODULUS_PREFIX` is correct.
- **HTTP port pattern:** add a new Windows-specific pattern. The current Linux pattern `41 b8 50 00 00 00 74` will not match.
  - **New pattern:** `b8 50 00 00 00 eb` (6 bytes).
  - **Patch offset:** `+1` (was `+2` on Linux because of the REX `41` prefix).
  - **Patch length:** 4 bytes LE port immediate (unchanged).
  - Uniqueness verified: scanning the entire binary, `b8 50 00 00 00 eb` matches exactly 1 site.
- **ISAAC delta:** **NO patch site exists.** MSVC inlined the +50 step as `ADD reg, 0x32` instructions in the SendLoginPacket flow rather than emitting the 4x int32 packed constant the Linux build had. The server contract is unchanged — Darkan's ISAAC must still derive S2C seed by `client_seed + 50`. No patcher action.

### Required EnvVars Changes
- `JAGEX_LOGIN_RSA_MODULUS_HEX` — **UNCHANGED** from 948-2.
- `JAGEX_JS5_RSA_MODULUS_HEX` — **UNCHANGED** from 948-2.

### Required Doc Changes Downstream
- `client/launcher/patcher/src/lib.rs` (or a new Windows-side equivalent) needs the new HTTP port pattern. If the patcher is extended to support Windows targets, gate the pattern on platform / binary detection.
- All `[Verified in rs2client.948-2 @ 0xADDRESS]` evidence tags for code symbols cannot be re-used here verbatim — the Windows build has different addresses, different inlining, and different register allocations. Each verified site below uses `[Verified in rs2client.948-5 Windows PE @ 0xADDRESS]`.

---

## Patch Target P1: Login RSA Modulus (1024-bit, 256 hex chars)

- **Binary:** rs2client.exe (948-5, Windows PE32+)
- **Address:** `.rdata @ VA 0x140bb7800` (RVA `0xbb7800`, file offset `0x00bb6a00` = 12281856)
- **Status:** **RELOCATED** vs Linux 948-2 (was at VA `0x0104a428`)
- **Old prefix (948-2):** `aad4a7804c34bb788d52dbd5f70e5721`
- **New prefix (948-5):** `aad4a7804c34bb788d52dbd5f70e5721` — **IDENTICAL**
- **Full value (256 hex chars, byte-identical to 948-2):**
  ```
  aad4a7804c34bb788d52dbd5f70e5721528d7f01c6aa1a93b7322ea0127f4068
  2f2d766a26728f0758ce47c9cde8003a170381352a143320ae3cc884a9116008
  ec09104ecdbafbcd0f537dfba67c7340ea3ca30caf91c20f8d98ac9b9a613b25
  cd23f586d3fae88823f1ea48aeeb31c9897c17c45aeb0771521a5c2df3cb0799
  ```
- **Surrounding bytes:** padded with NULs on both sides (16 bytes of `00` before, 16 bytes of `00` after — standard alignment between `.rdata` constants).
- **Ghidra/IDA symbol (proposed):** `jag::LoginManager::RSA_LOGIN_MODULUS_HEX` @ `0x140bb7800`
- **Init function:** `jag::GlobalRSAKeys::LoginInit` @ VA `0x14000b910` — file offset `0x0000ad10`. Behaviour:
  1. `mov edx, 0x10; lea ecx, [rdx+8]` (allocate `BigInteger` with capacity 0x18 bytes)
  2. `CALL 0x1407a8f80` (likely `jag::HeapInterface_Alloc` — matches functions.h offset `jag_HeapInterface_Alloc = 0x7A8F80`)
  3. `MOV [rip+disp32], rax` storing the new pointer at the global `g_LoginRSAModulus` @ `0x140ed4998`
  4. `CALL 0x14079d250` (BigInteger constructor)
  5. `LEA rdx, [rip+disp32]` loading the hex string pointer at `0x140bb7800` (THIS PATCH TARGET)
  6. `mov r8d, 0x10` (radix = 16)
  7. `CALL 0x14079b240` (BigInteger hex-string parser)
- **Global pointer:** `jag::LoginManager::g_LoginRSAModulus` @ `0x140ed4998` (RVA `0xed4998`, in `.data`)
- **Consumers (LEA xrefs to `g_LoginRSAModulus` @ `0x140ed4998`):**
  - `0x140152af8` inside function at `0x140152390` — likely `jag::LoginManager::CreateLoginRSAPacket` (RSA encryption of the login block)
  - `0x1401567f5` inside function at `0x140156740` — login flow (near `jag_LoginManager_StartLogin = 0x155FF0` per functions.h)
  - `0x140159257` inside function at `0x140159040` — login flow (near `jag_LoginManager_LoginStep80 = 0x159A80` per functions.h)
  - `0x14082eaa0` (destructor — not relevant for patching)
- **Methodology:**
  1. Open `rs2client.exe`, parse PE headers to find `.rdata` (RVA `0x837000`, file offset `0x836200`, size `0x474ebc`).
  2. Search the entire binary for any maximal lowercase-hex run of exactly 256 chars: `re.compile(rb'(?<![0-9a-f])[0-9a-f]{256}(?![0-9a-f])')`. Only one match in this build.
  3. Confirm by xref chain: LEA to the string is followed by `MOV r8d, 0x10` and `CALL` to the BigInteger hex-parser at `0x14079b240`. This is the unique parser signature.
  4. Confirm the resulting global pointer is consumed by three functions in the address range 0x140150000-0x140160000 (the LoginManager).
- **Patcher behavior:**
  - Search for prefix `aad4a7804c34bb788d52dbd5f70e5721` across mapped pages of `rs2client.exe`.
  - Overwrite 256 hex chars with `DARKAN_RSA_MODULUS` (left-padded to 256 chars with `'0'`).
  - The patcher uses `VirtualProtect` on Windows (vs `mprotect` on Linux) to temporarily make the page writable.

---

## Patch Target P2: JS5 Master Index RSA Modulus (4096-bit, 1024 hex chars)

- **Binary:** rs2client.exe (948-5, Windows PE32+)
- **Address:** `.rdata @ VA 0x140bb73f0` (RVA `0xbb73f0`, file offset `0x00bb65f0` = 12280816)
- **Status:** **RELOCATED** vs Linux 948-2 (was at VA `0x0104a530`)
- **Old prefix (948-2):** `a6400fbcbd9dd09f48045caf3f543dd6`
- **New prefix (948-5):** `a6400fbcbd9dd09f48045caf3f543dd6` — **IDENTICAL**
- **Full value (1024 hex chars, byte-identical to 948-2, formatted in 64-char blocks):**
  ```
  a6400fbcbd9dd09f48045caf3f543dd6b1c4da6ac89e13e17df3627ddb8a23bf
  7726849525ee28f7cacca19433a774e859bfbdbb3ee26cf5cf8006bb0eec29e2
  addc66031ff5fc7a05408772047c5f40bc967539e2423d27dbb655f4f9e94266
  ec7a9d0386930c001e6a81cf0a0e2881a6bdf3c5c135f8339ce6a012093c9864
  3c1727d18960c4b64aae59364fd0b981ea3899a39bbf5e1c6c2b489537aa4df4
  2800f52be33b73bcdc8379948b0f3a85a3d143aca429e562abe5dd7ef2822c7d
  90aa23082e2ef901abc92cc80e5bbe40d29894ea8c8c97819debcd219234ad4a
  a670c23000a443533664126f4861d460ffe3a396237b77fe7ef291b7e21f5352
  6448b0b9483fd703e0465748fc97c7d9fcd9617ea2f8228fa4c2170312705c6a
  556bdfefd009dedc059e00ca8073cd2bb58d69ab4b84253ed7f78a5384e1b801
  9b46955d1fa5ec4e41cc43402253a78469b3b30d973e089f186eaa6e691a38c5
  336c8cee64f1733dcd37e3a6bd962905b68b57d3a9c24a7839bb9aec5a47a8a7
  78616d777f1f99e19e30f825b1177e00d970f3a2ba2c2b3b166bcbb2fbd14ea1
  bb3b012989ed30e574c7c53bf0cf82d59b9cfaa9e5a0d46591cddb9eedc8e276
  804c1063f06f377e388363984ed85b2eb500973b943ee4ff68e7de028eb3e106
  41803acf95a6b358e7fa0175bfda660e492e6a5b901efe99bf55561743dc8299
  ```
- **Surrounding bytes:** preceded by 16 bytes of pointer-like data (`50 5d 2e 40 01 00 00 00 10 d0 02 40 01 00 00 00` — two QWORDs `0x140000002e5d50` and `0x14000002d010`, both `.text` callable thunks not modulus-related); followed by 16 bytes of `00` padding.
- **Ghidra/IDA symbol (proposed):** `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX` @ `0x140bb73f0`
- **Init function:** `jag::GlobalRSAKeys::Js5Init` @ VA `0x14000b9b0` — file offset `0x0000adb0`. Same shape as Login init: alloc BigInteger, store at global, parse hex string with radix 16 via `CALL 0x14079b240`.
- **Global pointer:** `jag::Js5MasterIndex::g_JS5RSAModulus` @ `0x140ed49a8` (`.data`, 0x10 bytes after `g_LoginRSAModulus`)
- **Consumers (LEA xrefs to `g_JS5RSAModulus` @ `0x140ed49a8`):**
  - `0x1400209c2` inside function at `0x1400206e0` — `jag::Js5MasterIndex::Construct` equivalent. This function also calls `GetHTTPURL` @ `0x14015ee70` (the function whose port literal we patch in P3), which is the expected Js5MasterIndex callsite shape for constructing the master-index URL.
  - `0x14082eac0` (destructor — not relevant)
- **Methodology:**
  1. Search the binary for any maximal lowercase-hex run of exactly 1024 chars: `re.compile(rb'(?<![0-9a-f])[0-9a-f]{1024}(?![0-9a-f])')`. Only one match.
  2. Cross-confirm by xref to a function (`0x1400206e0`) that also calls `GetHTTPURL`. That co-occurrence is the master-index downloader signature.
- **Patcher behavior:**
  - Search for prefix `a6400fbcbd9dd09f48045caf3f543dd6` across mapped pages.
  - Overwrite 1024 hex chars with `DARKAN_JS5_RSA_MODULUS` (left-padded to 1024 chars with `'0'`).

---

## Patch Target P3: HTTP Port 80 (single inlined immediate)

- **Binary:** rs2client.exe (948-5, Windows PE32+)
- **Address:** `.text @ VA 0x14015eec2` (RVA `0x15eec2`, file offset `0x0015e2c2`)
- **Status:** **PATTERN CHANGED vs Linux 948-2.** Linux used `41 b8 50 00 00 00 74` (REX-prefixed MOV R8D + JZ short). MSVC on Windows emits `b8 50 00 00 00 eb` (MOV EAX + JMP short) — no REX prefix, JMP instead of JZ (the if/else fall-through is structured differently). Same number of sites (1).
- **Old pattern (Linux 948-2):** `41 b8 50 00 00 00 74` (7 bytes)
- **New pattern (Windows 948-5):** `b8 50 00 00 00 eb` (6 bytes) — verified uniquely 1 match in the entire binary.
- **Patch offset within pattern:** `+1` (the 4 bytes `50 00 00 00` that immediately follow the `b8` opcode).
- **Patch length:** 4 bytes (LE 32-bit port immediate). Treat the high 2 bytes as zero; ports beyond 0xFFFF are rejected.
- **Containing function:** `jag::WorldLobbyData::GetHTTPURL` equivalent @ VA `0x14015ee70` — file `0x0015e270`. Size `0xc1` bytes.
- **Function decompiled shape (verified by LEA-string anchors):**
  - References strings `"https://"` (@ `0x140b97c08`), `"http://"` (@ `0x140b971e0`), and `"%s%s:%u"` (@ `0x140ba2e68`) — the URL builder format string.
  - Branch structure: pointer compare → if-branch `MOV EAX, 0x50` (the patch site) → `JMP short` over the world-port branch → else-branch reads `[r9+8]` (worldID) then adds `0x2ee0` (12000 — game port base) or `0x1b58` (7000 — JS5 HTTP port base) depending on protocol flag. This is the exact same logical shape as Linux 948-2's `GetHTTPURL`, just emitted in MSVC dialect.
- **Callers (CALL xrefs to `0x14015ee70`):**
  - `0x1400209f6` inside function at `0x1400206e0` — the Js5MasterIndex::Construct equivalent (also consumes `g_JS5RSAModulus`).
  - `0x140032dea` inside function at `0x140032dc0`
  - `0x1401986aa` inside function at `0x140198670` — matches `jag_InterfaceManager_MainLogic = 0x198670` from `functions.h`.
- **Methodology:**
  1. Search `.text` for `b8 50 00 00 00`. Many matches (23) — this is just any `MOV EAX, 0x50`.
  2. Filter to those followed by `eb` (JMP short) or `74` (JZ short) or `0f` (long JCC). Only **one** survives: VA `0x14015eec2` with `eb` follow.
  3. Disassemble the containing function. Confirm presence of LEAs to the URL-related strings `"https://"`, `"http://"`, `"%s%s:%u"`.
  4. Confirm exactly 3 call sites point to the function entry — one of which is the Js5MasterIndex constructor (the canonical caller of `GetHTTPURL`).
- **Patcher behavior:**
  - Search for `b8 50 00 00 00 eb` (6 bytes) across mapped pages.
  - For each match (there should be exactly 1, but the Linux patcher patches every match to stay correct across revisions — do the same here):
    - Overwrite 4 bytes at `match + 1` with the 4-byte LE port immediate from `DARKAN_HTTP_PORT`.
  - The `0x22` displacement byte after `eb` (the JMP short distance) is **not** part of the search pattern (this gives some safety margin against build-to-build minor code reshuffles in the same function).
- **False positives surveyed:** 22 other `b8 50 00 00 00` sites exist in `.text`. Every one of them has a non-conditional follow byte (e.g., `b9`, `bf`, `48`, `f3`, `4c`) — `mov ecx, ...`, `mov edi, ...`, REX prefix, SSE op. None are followed by `eb`/`74`/`0f`. The `b8 50 00 00 00 eb` signature is therefore conservatively unique and safe to patch.

---

## Patch Target P4: ISAAC Delta (Sanity Check — NO PATCH SITE)

- **Binary:** rs2client.exe (948-5, Windows PE32+)
- **Status:** **NO MATERIAL PATCH SITE — VERIFIED by exhaustive search.**
- **Search performed:**
  - Pattern: `32 00 00 00 32 00 00 00 32 00 00 00 32 00 00 00` (4x int32 0x32) — the Linux 948-2 packed constant for the +50 ISAAC delta. **0 matches.**
  - Pattern: `32 00 00 00 32 00 00 00` (2x int32 0x32) — 1 match in `.data` (file `0x00cb070c`, VA `0x140cb150c`) but surrounded by pointer-like data, not the packed-int signature.
  - `add reg, 0x32` instructions (`83 c0 32`, `83 c1 32`) — present in `.text` but spread across many functions (not part of a packed `.rdata` constant).
- **Interpretation:** MSVC inlined the `+50` step as four `ADD reg, 0x32` (or `LEA reg, [reg+0x32]`) instructions inside the LoginManager's SendLoginPacket-equivalent function rather than loading a 4x int32 packed constant via SSE2 PADDD. There is **no centralised data site to rewrite** — the `0x32` immediate is spread across multiple instructions, and the right way to retarget it would be to NOP-patch each ADD (high risk) or to relocate the ADDs to a single helper. Neither is necessary because:
- **Server-side contract is unchanged.** The Darkan ISAAC server-to-client cipher seed must still be `client_seed[i] + 50` for `i = 0..3`. This contract is independent of how the client emits the addition. Darkan continues using `+50`.
- **No patcher action required.** Do not attempt to patch ISAAC delta in the Windows build — it would be both fragile and unnecessary.

---

## Patch Target P5: rs3windows Launcher Patch Sites

- **Status:** **NOT ANALYZED IN THIS REVISION.**
- **Reason:** The provided archive at `C:\Users\david\RuneScape Archive\948-5\` contains only `rs2client.exe`. The Windows launcher binary (`rs3windows.exe`) was not included. The previous 948-2 doc analyzed `rs3linux.948`, which is a different launcher running on a different platform and cannot be assumed to share patch site addresses or patterns with the Windows launcher.
- **Action for future migration:** When `rs3windows.exe` is added to the archive, three patches need to be located and documented:
  1. **Launcher RSA modulus** — a 1024-char hex string used for download-hash verification. Method: search the binary for any standalone 1024-char hex run. There should be exactly one. Document its prefix (the prior Linux launcher prefix was `a49962fc0737fddcd94c0daf84e5d214` — may or may not be the same key on Windows).
  2. **Codebase URL regex** — currently on Linux is `^https?://[a-z0-9\-]*\.?runescape.com(:[0-9]+)?/`. On Windows the regex engine and string encoding may differ (UTF-16 vs UTF-8); search for `runescape.com` substrings inside regex-like patterns.
  3. **LZMA decompression flag** — currently on Linux is a 21-byte sequence of three consecutive `MOV byte ptr [RAX+0x18*], imm` instructions; the patch flips the middle immediate from `0x01` to `0x00`. MSVC on Windows likely emits the same structural pattern but with different register allocation; the python regex `\xC6\x80\x81\x01\x00\x00.\xC6\x80\x82\x01\x00\x00.\xC6\x80\x83\x01\x00\x00.` is the natural starting point but may need adjusting if MSVC uses RCX/RDX instead of RAX.

---

## Appendix A: Items NEW in 948-5

| Item | Note |
|---|---|
| Windows PE32+ binary format | The 948-2 doc targeted a Linux ELF. The Windows build is what 948-5 introduces in this doc. All file-offset→VA mappings now follow PE convention (`VA = ImageBase + RVA`, `file = SectionRawPtr + (RVA - SectionRVA)`). |
| HTTP port pattern (`b8 50 00 00 00 eb`) | NEW. MSVC's emit shape for port-80 inlining differs from gcc/clang's. Linux pattern `41 b8 50 00 00 00 74` does not match. |
| No 4x int32 packed ISAAC delta constant | NEW. MSVC inlined as scattered `ADD reg, 0x32` instead of packed PADDD constant. No patch site. |

## Appendix B: Items REMOVED vs 948-2

| Item | Note |
|---|---|
| Linux LD_PRELOAD support | The current patcher (`client/launcher/patcher/`) is Linux-only. To support the Windows build, a new DLL-injection patcher is required. Symbol- and pattern-search logic transfers directly; only the platform syscall layer (`VirtualProtect`, image-file-handle enumeration via `GetModuleHandle`, etc.) needs to change. |
| HTTP_PORT_PATTERN_2 (`41 b8 50 00 00 00 0f`) | Already noted as dead in 948-2 doc. Still dead here. The legacy Linux pattern shouldn't be added to a Windows patcher. |

## Appendix C: PE Section Layout (for file-offset / VA conversions)

| Section | RVA       | VSize     | RawPtr    | RawSize   |
|---------|-----------|-----------|-----------|-----------|
| .text   | 0x001000  | 0x835c20  | 0x000400  | 0x835e00  |
| .rdata  | 0x837000  | 0x474ebc  | 0x836200  | 0x475000  |
| .data   | 0xcac000  | 0x26cf54  | 0xcab200  | 0x01f600  |
| .pdata  | 0xf19000  | 0x066ee8  | 0xcca800  | 0x067000  |
| _RDATA  | 0xf80000  | 0x000400  | 0xd31800  | 0x000400  |
| .rsrc   | 0xf81000  | 0x032ca0  | 0xd31c00  | 0x032e00  |
| .reloc  | 0xfb4000  | 0x00a7b8  | 0xd64a00  | 0x00a800  |

Conversion formulas:
- `RVA = VA - 0x140000000`
- `file_offset = section.RawPtr + (RVA - section.RVA)` for whichever section contains the RVA.

Example: VA `0x140bb7800` (login modulus). RVA = `0xbb7800`. `.rdata` covers RVA `0x837000`..`0xcabebc`, so file = `0x836200 + (0xbb7800 - 0x837000) = 0x836200 + 0x380800 = 0xbb6a00`. Matches the documented file offset.

---

## Methodology Notes (Diff vs 948-2 Doc)

### Finding RSA moduli in a PE binary
Identical to the Linux methodology: search the entire binary for maximal lowercase-hex runs of length 256 (login) and 1024 (JS5), and verify each is parsed by the BigInteger hex-parser via the LEA + `MOV R8D, 0x10` + CALL signature. The init-function shape (alloc → store global → call BI-ctor → load hex ptr → call BI-parser) is the same; only the call targets and global addresses differ between builds. Both moduli in 948-5 had a 1-match unique search, confirming no other RSA moduli or look-alike hex strings exist in `.rdata`.

### Finding the HTTP port site in a PE binary
The Linux methodology of "search for `MOV R8D, 0x50` followed by conditional" maps to "search for `MOV EAX, 0x50` followed by `JMP short` or `JZ short`" on MSVC PE. The change of destination register (R8D → EAX) is driven by Windows x64 calling convention: MSVC keeps the first 4 args in RCX/RDX/R8/R9 and uses EAX as a scratch return value, so the port immediate often lands in EAX before being stored into the URL builder's output struct. Use the 6-byte `b8 50 00 00 00 eb` as the canonical search; it is uniquely 1 site in 948-5. The patch offset shifts from `+2` (Linux, after REX `41`) to `+1` (Windows, no REX).

### Finding ISAAC delta in a PE binary
Search for the 4x int32 packed constant first (`32 00 00 00 32 00 00 00 32 00 00 00 32 00 00 00`). If 0 matches, the compiler inlined the +50 step as scattered `ADD reg, 0x32` instructions. There is no single patch site — the server-side contract is the relevant artifact, and it is unchanged.

### Verifying "RELOCATED, not ROTATED"
For each candidate RSA modulus address:
1. Read the 256 (or 1024) bytes at the new location.
2. Compare byte-for-byte against the previous build's documented hex string.
3. If identical, mark **RELOCATED** (Jagex moved the constant within `.rdata` without rotating the key — common across build re-links).
4. If different, mark **ROTATED** and capture the new full value and prefix; this triggers an env-var update for `JAGEX_*_RSA_MODULUS_HEX` server-side.

In 948-5, both moduli are RELOCATED-only. No env-var change required.

---

## Open Items

1. **rs3windows.exe patch targets** — not analyzed (binary not in archive). When available, document the launcher RSA modulus, codebase regex, and LZMA flag equivalent.
2. **Windows DLL-injection patcher implementation** — the current Rust crate at `client/launcher/patcher/` is Linux-only (`.so`, `LD_PRELOAD`, `/proc/self/maps`). For the Windows target a parallel implementation is needed using `VirtualProtect`, `EnumProcessModules`, and either DLL injection or a launcher-side `WriteProcessMemory` patch over CreateProcess-SUSPENDED.
3. **Init function naming in Ghidra/IDA** — the LOGIN and JS5 init functions at VA `0x14000b910` and `0x14000b9b0` should be renamed to `jag::GlobalRSAKeys::LoginInit` / `jag::GlobalRSAKeys::Js5Init` (or a combined `jag::GlobalRSAKeys_Init` if they share a single parent constructor — to be confirmed by tracing the `__cxa_atexit`-style initializer list, which in PE32+ lives in `.rdata` as a function-pointer table referenced by the C++ runtime startup code).
