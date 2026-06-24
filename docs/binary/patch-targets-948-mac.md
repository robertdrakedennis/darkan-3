# Patcher Targets - Rev 948-5 (macOS)

Binary: `rs2client.948-5-mac` (Mach-O 64-bit executable, x86_64, unsigned, PIE)
Size: 15,649,624 bytes
Image base (`__TEXT` vmaddr): `0x100000000`
Mach header flags: `0x00a18085` (includes `MH_PIE 0x200000`, `MH_TWOLEVEL`, `MH_NOUNDEFS`, `MH_DYLDLINK`)
Cross-reference programs:
- `rs2client.exe` rev 948-5 (Windows PE32+) — see `patch-targets-948-5.md`
- `rs2client.948-2-2` / `rs2client.948-5` (Linux ELF) — see `patch-targets-948.md`
Analysis project: Ghidra `mac-9485` at `/Users/robert/rs-re-staging/macproj`
Reference project: Ghidra `gzf-947-3` program `rs2client.948-5` (Linux, named symbols) at `/Users/robert/projects/reclass-data`

## Summary

This is the **macOS Mach-O x86_64** build of rev 948-5, compiled with Apple clang. It is the third platform variant; the prior docs cover Windows PE (`patch-targets-948-5.md`) and Linux ELF (`patch-targets-948.md`). The runtime patcher needs a **`DYLD_INSERT_LIBRARIES` dylib** — the macOS analogue of the Linux `LD_PRELOAD` `.so`. Pattern/symbol-search logic transfers directly from the existing patchers; only the platform layer changes (`mach_vm_protect` instead of `mprotect`/`VirtualProtect`, `_dyld_*` image enumeration instead of `/proc/self/maps`).

Four things differ from the prior platforms. **Read these before writing the patcher — the prompt's pre-verified baseline for the port pattern was WRONG and is corrected here:**

1. **Binary format is Mach-O, PIE.** All addresses are subject to ASLR slide at runtime. The dylib must compute the slide via `_dyld_get_image_vmaddr_slide` and add it to every static VA below. File-offset/VA conversion is *linear* for the whole `__TEXT` segment (file offset 0 ↔ vmaddr `0x100000000`), so `VA = 0x100000000 + file_offset` for everything in `__TEXT` (which includes code AND the RSA cstrings).

2. **Both rs2client RSA keys (login + JS5) were RELOCATED, not ROTATED** — byte-identical to Windows 948-5 and Linux 948. Confirmed by full 256/1024-char value compare. No `DARKAN_*_RSA_MODULUS` env-var change required. Only their addresses moved.

3. **The HTTP port-80 pattern is `66 b8 50 00` (`MOV AX, 0x50`), NOT `b8 50 00 00 00 eb`.** The prompt's pre-verified baseline named two `b8 50 00 00 00 eb` sites (`0x1008E9168`, `0x1008F3605`) as candidate port sites. **Both are FALSE POSITIVES** — they are vtable method-offset computations inside message-dispatch loops, not HTTP ports (full evidence in P3 below). clang inlined `GetHTTPURL` into one function and emitted the port-80 store as a **16-bit `MOV AX, 0x50`** (operand-size prefix `0x66`), because the port field is a `short`. There is exactly **ONE** genuine port site.

4. **The ISAAC +50 delta DOES exist as a packed `4×int32 = 0x32` constant** (unlike Windows, which scattered it as `ADD reg,0x32`). It is in `__TEXT,__const` at VA `0x100ad7b50` and consumed via SSE2 `PADDD` by `SendLoginPacket`. As on every platform, **no patch is needed** — the server contract (S2C seed = client_seed + 50) is unchanged.

### Required Patcher Changes (macOS dylib build)
- **Login modulus prefix:** `aad4a7804c34bb788d52dbd5f70e5721` — UNCHANGED. Resolve by pattern search; address differs.
- **JS5 modulus prefix:** `a6400fbcbd9dd09f48045caf3f543dd6` — UNCHANGED.
- **HTTP port pattern:** add a macOS-specific pattern `66 b8 50 00` (4 bytes). **Patch offset +2, patch length 2 bytes** (LE u16). The Linux pattern `41 b8 50 00 00 00 74` and Windows pattern `b8 50 00 00 00 eb` both fail to match on macOS. Uniqueness: `66 b8 50 00` matches exactly **1** site in the whole binary.
- **ISAAC delta:** no patch site action (constant present, contract unchanged).
- **Host / configURI:** **NO host-acceptance guard exists** (see P5). Launching `rs2client.948-5-mac` with a localhost `rs-launch://` argv[1] needs **no** host patch. P5 is documented as "verified absent," not a required patch.

### Required EnvVars Changes
- `JAGEX_LOGIN_RSA_MODULUS_HEX` — UNCHANGED from 948-2/948-5.
- `JAGEX_JS5_RSA_MODULUS_HEX` — UNCHANGED from 948-2/948-5.

---

## IMPORTANT: file-offset correction vs the task baseline

The task's "pre-verified baseline" gave the RSA string offsets in two forms that disagree with each other; the **decimal** values are correct and the **hex** values were mistyped (off by `0x100`). Forensics on the actual binary:

| Target | Task hex (WRONG) | Task decimal (correct) | **Verified file offset** | **Verified VA** |
|---|---|---|---|---|
| P1 login modulus | `0xE2336D` | 14824557 | `0xE2346D` (= 14824557) | `0x100E2346D` |
| P2 JS5 modulus | `0xE2346E` | 14824814 | `0xE2356E` (= 14824814) | `0x100E2356E` |

Use the **decimal** offsets (14824557 / 14824814) or the corrected hex (`0xE2346D` / `0xE2356E`). The patcher should search by prefix anyway, so this only matters for direct-offset fallback and documentation accuracy.

---

## Mach-O Segment / Section Table (for file-offset ↔ VA conversion)

Full `otool -l` segment table. **Conversion rule: for any address in a segment, `file_offset = seg.fileoff + (VA − seg.vmaddr)`.** For `__TEXT` this simplifies to `file_offset = VA − 0x100000000` (fileoff 0, vmaddr `0x100000000`).

| Segment | vmaddr | vmsize | fileoff | filesize | initprot | maxprot | r/w/x |
|---|---|---|---|---|---|---|---|
| `__PAGEZERO` | `0x0` | `0x100000000` | 0 | 0 | `0x0` | `0x0` | --- |
| `__TEXT` | `0x100000000` | `0xe98000` | 0 | 15302656 | `0x5` | `0x5` | **r-x** |
| `__DATA_CONST` | `0x100e98000` | `0x34000` | 15302656 | 212992 | `0x3` | `0x3` | rw- |
| `__DATA` | `0x100ecc000` | `0x273000` | 15515648 | 45056 | `0x3` | `0x3` | rw- |
| `__LINKEDIT` | `0x10113f000` | `0x18000` | 15560704 | 88920 | `0x1` | `0x1` | r-- |

### `__TEXT` sections (all r-x — code AND read-only cstrings/consts live here)

| Section | addr | size | notes |
|---|---|---|---|
| `__text` | `0x1000056c0` | `0xad029a` | executable code (all 4 patch targets' *code* sites) |
| `__stubs` | `0x100ad595a` | `0x8b2` | PLT-style stubs |
| `__stub_helper` | `0x100ad620c` | `0x1166` | lazy-bind helpers |
| `__const` | `0x100ad7380` | `0x341600` | read-only constants — **holds P4 ISAAC delta @ `0x100ad7b50`** |
| `__cstring` | `0x100e18980` | `0x38557` | C string literals — **holds P1 login + P2 JS5 RSA moduli, and URL format strings** |
| `__gcc_except_tab` | `0x100e50ed8` | `0x2d290` | EH tables |
| `__objc_methname` | `0x100e7e168` | `0x1ac6` | (minimal ObjC, from system libs) |
| `__objc_classname` | `0x100e7fc2e` | `0x7d` | |
| `__objc_methtype` | `0x100e7fcab` | `0xae2` | |
| `__unwind_info` | `0x100e80790` | `0x17648` | compact unwind |

### `__DATA_CONST` sections (r-x at rest, but written by dyld during bind → effectively rw via COW)

| Section | addr | size | notes |
|---|---|---|---|
| `__got` | `0x100e98000` | `0x108` | |
| `__mod_init_func` | `0x100e98108` | `0x4068` | C++ static initializers (run the RSA BigInteger init) |
| `__const` | `0x100e9c170` | `0x2fb50` | |
| `__cfstring` | `0x100ecbcc0` | `0x60` | |
| `__objc_classlist` / `__objc_protolist` / `__objc_imageinfo` | `0x100ecbd20`+ | small | |

Note: `__DATA_CONST` initprot is `0x3` (rw-) per the dump's pairing, but Apple marks it read-only after fixups; it is **not** a patch target here so it does not matter.

### CRITICAL consequence: the RSA cstrings are in an EXECUTABLE segment

On Linux/Windows the RSA moduli sit in `.rodata`/`.rdata` (plain read-only data). **On macOS they sit in `__TEXT,__cstring`, inside the r-x `__TEXT` segment.** This changes the runtime-patch guidance:
- You cannot simply `mach_vm_protect(..., VM_PROT_READ | VM_PROT_WRITE)` a private dirty page — `__TEXT` is shared, code-signed-page-mapped, copy-on-write from the dyld shared cache mapping path.
- You **must** pass `VM_PROT_COPY` (`0x10`) OR'd into the protection on the first `mach_vm_protect` call so the kernel makes a private writable copy (breaks COW) before you write. Then write, then restore to `VM_PROT_READ | VM_PROT_EXECUTE`.
- Because the binary is **unsigned** (no `LC_CODE_SIGNATURE`, confirmed — see load-command list), there is no code-signing enforcement to fight; the COW break is the only requirement. (If a future build is signed, the same `VM_PROT_COPY` path still works for an unsigned-or-adhoc binary, but a hard-signed binary on hardened-runtime would additionally need the page to not be validated — out of scope here.)

---

## Patch Target P1: Login RSA Modulus (1024-bit, 256 hex chars)

- **Binary:** `rs2client.948-5-mac` (Mach-O x86_64)
- **Section:** `__TEXT,__cstring`
- **File offset:** `0xE2346D` (decimal **14824557**)
- **VA:** `0x100E2346D`
- **Status:** **RELOCATED** vs Windows 948-5 (`0x140bb7800`) and Linux 948 (`0x0104a428`). **Byte-identical value.**
- **Prefix (32 chars):** `aad4a7804c34bb788d52dbd5f70e5721` — IDENTICAL to Windows/Linux 948.
- **Full value (256 hex chars, verified byte-identical to Windows 948-5 & Linux 948):**
  ```
  aad4a7804c34bb788d52dbd5f70e5721528d7f01c6aa1a93b7322ea0127f4068
  2f2d766a26728f0758ce47c9cde8003a170381352a143320ae3cc884a9116008
  ec09104ecdbafbcd0f537dfba67c7340ea3ca30caf91c20f8d98ac9b9a613b25
  cd23f586d3fae88823f1ea48aeeb31c9897c17c45aeb0771521a5c2df3cb0799
  ```
- **Surrounding bytes:** NUL before (`00`) and NUL after (`00`) — standard C-string boundaries. Exactly **1** maximal 256-char lowercase-hex run exists in the entire binary (uniqueness verified).
- **Adjacency:** P2 (JS5 modulus) begins 1 byte after P1's terminating NUL (gap = 1, i.e. `…0799` `00` `a6400f…`).
- **Proposed Ghidra symbol:** `jag::LoginManager::RSA_LOGIN_MODULUS_HEX` @ `0x100E2346D`
- **Parse chain (clang inlines the BigInteger init through the C++ static initializers in `__mod_init_func`):** the string is loaded via `LEA RDX, [rip+disp]` → passed with radix `0x10` to the BigInteger hex parser → result stored in the global login-modulus BigInteger. The same shape as Linux's `jag::GlobalRSAKeys_Init` (`FUN_000e8f90`) calling `FUN_000cef60(&g_LoginRSAModulus, RSA_LOGIN_MODULUS_HEX, 0x10)`. Consumers are the `jag::LoginManager` RSA-packet builders (`CreateLoginRSAPacket` / `StartRSAPacket` equivalents).
- **Methodology:** regex `(?<![0-9a-f])[0-9a-f]{256}(?![0-9a-f])` over the whole file → exactly 1 match at file `0xE2346D`. Prefix-match `aad4a7804c34bb788d52dbd5f70e5721` confirms identity.
- **Patcher behavior:** search mapped pages for prefix `aad4a7804c34bb788d52dbd5f70e5721`; overwrite 256 hex chars with `DARKAN_RSA_MODULUS` (left-padded to 256 chars with `'0'`). Use `mach_vm_protect` + `VM_PROT_COPY` (see Runtime section) because this lives in r-x `__TEXT`.

---

## Patch Target P2: JS5 Master Index RSA Modulus (4096-bit, 1024 hex chars)

- **Binary:** `rs2client.948-5-mac` (Mach-O x86_64)
- **Section:** `__TEXT,__cstring`
- **File offset:** `0xE2356E` (decimal **14824814**)
- **VA:** `0x100E2356E`
- **Status:** **RELOCATED** vs Windows 948-5 (`0x140bb73f0`) and Linux 948 (`0x0104a530`). **Byte-identical value.**
- **Prefix (32 chars):** `a6400fbcbd9dd09f48045caf3f543dd6` — IDENTICAL to Windows/Linux 948.
- **Full value (1024 hex chars, verified byte-identical to Windows 948-5 & Linux 948, formatted in 64-char blocks):**
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
- **Surrounding bytes:** NUL before and NUL after. Exactly **1** maximal 1024-char lowercase-hex run in the binary (uniqueness verified).
- **Proposed Ghidra symbol:** `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX` @ `0x100E2356E`
- **Consumer:** the JS5 master-index / version-table signature verifier (`Js5MasterIndex` ctor / `Js5WorkerThread`), which stores the parsed BigInteger and uses it to RSA-verify the Whirlpool-signed version table (same flow documented in `rs2client-rsa-keys.md`).
- **Methodology:** regex `(?<![0-9a-f])[0-9a-f]{1024}(?![0-9a-f])` over the whole file → exactly 1 match at file `0xE2356E`.
- **Patcher behavior:** search for prefix `a6400fbcbd9dd09f48045caf3f543dd6`; overwrite 1024 hex chars with `DARKAN_JS5_RSA_MODULUS` (left-padded to 1024 chars with `'0'`). r-x segment → `VM_PROT_COPY` required.

---

## Patch Target P3: HTTP Port 80 (inlined immediate) — CORRECTED PATTERN, 1 SITE

> **The prompt's pre-verified baseline for this target was incorrect.** It proposed two `b8 50 00 00 00 eb` sites as port candidates. Both are vtable-offset red herrings (proof below). The real site uses a 16-bit `MOV AX, 0x50` = `66 b8 50 00`. This section documents the corrected, definitive finding.

- **Binary:** `rs2client.948-5-mac` (Mach-O x86_64)
- **Section:** `__TEXT,__text`
- **VA:** `0x100273834`
- **File offset:** `0x273834`
- **Status:** **PATTERN CHANGED vs both Linux and Windows.**
  - Linux 948: `41 b8 50 00 00 00 74` (`MOV R8D, 0x50` + JZ) — 0 matches on macOS.
  - Windows 948-5: `b8 50 00 00 00 eb` (`MOV EAX, 0x50` + JMP) — matches 2 sites on macOS, **both false positives** (see below).
  - macOS 948-5: `66 b8 50 00` (`MOV AX, 0x50`, operand-size prefix `0x66`). The port field is a `short`, so clang stores it with a 16-bit move.
- **Definitive pattern:** `66 b8 50 00` (4 bytes). With the trailing `eb` it is `66 b8 50 00 eb`; the bare 4-byte form is already unique.
- **Bytes at the site:** `66 b8 50 00 eb 05` (`MOV AX, 0x50` ; `JMP +5`).
- **Patch offset within pattern:** **`+2`** (the 2 bytes `50 00` immediately following the `66 b8` opcode).
- **Patch length:** **2 bytes** (LE u16 port immediate). Patch lands at VA `0x100273836` / file `0x273836`. (Reject ports > 0xFFFF; this is a 16-bit field — the high bytes do not exist here, unlike the 4-byte Linux/Windows immediate.)
- **Containing function:** `FUN_1002733c0` @ `0x1002733c0`. This is the **lobby/world connect-URL builder** with `jag::WorldLobbyData::GetHTTPURL` **inlined** into it (on Linux/Windows GetHTTPURL is a standalone 177/0xc1-byte function; clang inlined it here).
- **Uniqueness (all verified by full-binary scan):**
  - `66 b8 50 00` → **1** match (`0x100273834`).
  - `66 b8 50 00 eb` → **1** match.
  - `ADD EAX, 0x2ee0` (`05 e0 2e 00 00`, = +12000 game port) → **1** match (`0x1002737c2`, same function).
  - `ADD EAX, 0x1b58` (`05 58 1b 00 00`, = +7000 JS5 port) → **1** match (`0x10027383a`, same function).
  - The two other functions that reference the URL-builder strings (`FUN_100215430`, `jag::InterfaceManager::SendIfButtonD_xplat948`) contain **no** `MOV AX,0x50` and **no** 7000/12000 adds — they build non-world URLs. So there is exactly **one** GetHTTPURL port site total.
- **Disassembly of the inlined port-selection block (the heart of GetHTTPURL):**
  ```
  1002737a2  48 8b 35 6f 20 c6 00   MOV  RSI, [0x100ed5818]      ; default-host sentinel (DAT_100ed5818)
  1002737a9  48 8d 15 a5 56 ba 00   LEA  RDX, [0x100e18e55]      ; "http://"
  1002737b0  48 3b 70 10            CMP  RSI, [RAX + 0x10]       ; host == default sentinel?
  1002737b4  74 7e                  JZ   0x100273834             ;   yes -> LIVE branch (port 80)
  1002737b6  41 0f b7 47 08         MOVZX EAX, word [R15 + 0x8]  ;   no  -> worldId
  1002737bb  41 83 7f 0c 01         CMP  dword [R15 + 0xc], 0x1  ; protocol == 1 ?
  1002737c0  75 78                  JNZ  0x10027383a             ;   no  -> +7000
  1002737c2  05 e0 2e 00 00         ADD  EAX, 0x2ee0             ;   yes -> worldId + 12000 (game)
  1002737c7  eb 76                  JMP  0x10027383f
  ...
  100273834  66 b8 50 00            MOV  AX, 0x50                ; <-- PATCH SITE: PORT 80 (LIVE)
  100273838  eb 05                  JMP  0x10027383f
  10027383a  05 58 1b 00 00         ADD  EAX, 0x1b58             ; worldId + 7000 (JS5 HTTP)
  10027383f  44 0f b7 c0            MOVZX R8D, AX                ; port -> arg for formatter
  100273843  48 8d 35 fa 55 ba 00   LEA  RSI, [0x100e18e44]      ; "%s%s:%u"  (URL builder fmt)
  ```
  Decompiled (Ghidra), matching the Linux reference exactly:
  ```c
  pcVar20 = "http://";
  if (DAT_100ed5818 == *(long *)(*plVar4 + 0x10)) {  // host is the default sentinel -> LIVE
      sVar10 = 0x50;                                  // PORT 80
  } else if (*(int *)((long)plVar4 + 0xc) == 1) {     // protocol == 1
      sVar10 = (short)plVar4[1] + 12000;              // worldId + 12000 (game)
  } else {
      sVar10 = (short)plVar4[1] + 7000;               // worldId + 7000 (JS5)
  }
  // custom-host (non-LIVE) branch sets sVar10 = customPort, scheme = "https://"
  FUN_100069540(&out, "%s%s:%u", pcVar20, host, sVar10);
  ```
- **Cross-check against the Linux 948-5 reference** (`jag::WorldLobbyData::GetHTTPURL` @ `0x001ba350`, decompiled): identical logic — `sVar6 = 0x50` for LIVE (`*(char)(host+0x468)==0` and host `!= &DAT_015bf1c0`), `worldId + 7000`, `worldId + 12000` for `protocol==1`, schemes `http://`/`https://`, format `%s%s:%u`. The macOS field offsets (`+0x468` mode flag, `+0xc` protocol, world-data layout) match the Linux struct. This confirms `FUN_1002733c0`'s block is genuine GetHTTPURL and the `0x50` is the port-80 literal.
- **Why the two `b8 50 00 00 00 eb` sites are FALSE POSITIVES (rebuttal of the baseline):**
  - **Site A `0x1008E9168`** lives in `FUN_1008e8f60` (1004 bytes), a mutex-guarded message-dispatch loop. The `MOV EAX, 0x50` is one arm of a `switch(messageType)` that selects a **vtable method offset**, then calls `(**(code **)(*param_1 + offset))(...)`. The neighbouring arms produce `0x58/0x60/0x68/0x70/0x78` — these are vtable slot displacements, not ports. There are no URL strings, no `%s%s:%u`, no 7000/12000 adds anywhere in the function. Its only inbound reference is a **DATA** reference from `0x10114c8f0` (a vtable/`__mod_init` slot), not a call.
  - **Site B `0x1008F3605`** lives in `FUN_1008f2930` (4068 bytes), the same dispatch idiom inlined a second time (byte-identical surrounding `MOV EAX,0x60/0x78/0x50/0x68 + JMP` chain). Same conclusion: vtable-offset switch, no URL/port semantics.
  - Both sites are the compiler's lowering of a `switch → method-pointer-offset` table; the `0x50` collides numerically with port 80 but is semantically a `vtable_base + 0x50` offset.
- **The lone `MOV R8D, 0x50` at `0x9B2188`** (`41 b8 50 00 00 00 45 31…`) is followed by `45` (a REX prefix for the next instruction), not a jump → not a port. Confirmed ignore (matches the prompt's own note).
- **Patcher rule (definitive):** search for `66 b8 50 00`. There is exactly **1** match. Overwrite the **2 bytes at offset +2** (VA `…+2`) with the LE u16 of `DARKAN_HTTP_PORT`. **Do NOT** add the Windows `b8 50 00 00 00 eb` pattern to the macOS patcher — it would match the two vtable-dispatch sites and corrupt method-offset computations (the client would call the wrong virtual methods and crash). The 16-bit `66 b8 50 00` form does not collide with the vtable sites (those use the 32-bit `b8 .. 00 00 00` form).
- **Disambiguating longer signature (optional belt-and-braces):** if you want a signature that cannot match anything but GetHTTPURL even across minor rebuilds, anchor on the structurally-unique sequence
  `44 0f b7 c0 48 8d 35 ?? ?? ?? ?? ` (`MOVZX R8D, AX` ; `LEA RSI, ["%s%s:%u"]`) which immediately follows the port merge, then walk back to the `66 b8 50 00`. But the bare `66 b8 50 00` is already unique in 948-5, so this is not required.

---

## Patch Target P4: ISAAC Delta (Sanity Check — NO PATCH, but constant present)

- **Binary:** `rs2client.948-5-mac` (Mach-O x86_64)
- **Section:** `__TEXT,__const`
- **VA:** `0x100ad7b50` (file offset `0xad7b50`)
- **Status:** **Packed constant PRESENT** (unlike Windows, which scattered `ADD reg,0x32`). **No patch required** — same as every platform.
- **Value:** four consecutive int32 = `0x32 0x00 0x00 0x00` × 4 = `[50, 50, 50, 50]`. Exactly **1** such 16-byte run in the binary.
- **Proposed Ghidra symbol:** `jag::LoginManager::ISAAC_DELTA_50` @ `0x100ad7b50`
- **Consumer:** `jag::LoginManager::SendLoginPacket_xplat948` (reads it at `0x1000cb894`). Disassembly of the seed-derivation:
  ```
  1000cb88e  f3 41 0f 6f 46 48      MOVDQU XMM0, [R14 + 0x48]      ; 4x client ISAAC seeds
  1000cb894  66 0f fe 05 b4c2a000   PADDD  XMM0, [0x100ad7b50]     ; += 50 each  (ISAAC delta)
  1000cb89c  66 0f 7f 45 b0         MOVDQA [RBP - 0x50], XMM0      ; server seeds = client + 50
  ...                                                              ; then jag::Isaac::Init(server_seeds)
  ```
  i.e. `server_seed[i] = client_seed[i] + 50` for `i = 0..3`, computed in one SSE2 `PADDD`. (A second reader, `FUN_1000c6630` @ `0x1000c67c8`, is in the same SendLoginPacket region.)
- **Implication for server:** Darkan's ISAAC must continue to derive the S2C cipher seed as `client_seed + 50`. Contract unchanged. **No patcher action.**

---

## Patch Target P5: In-client Host / configURI Acceptance — NO GUARD (verified absent)

- **Binary:** `rs2client.948-5-mac` (Mach-O x86_64)
- **Status:** **No host-acceptance guard exists. No patch needed.** Launching `rs2client.948-5-mac` directly with a localhost `rs-launch://` argv[1] is accepted.
- **Argv shape:** there is **no `--configURI` string** and **no `configURI` / `config_uri` string** anywhere in the binary (verified by full-file string scan). The client takes the config URI as **argv[1]**, consistent with the Linux/launch-chain analysis. There is also **no `rs-launch://` literal** — the scheme is matched by integer comparisons (below).
- **configURI parser:** `FUN_10026ea30` (references the `rs-launch` @ `0x100e1f3ef` and `rs-launchs` @ `0x100e1f3f9` strings). It:
  1. Validates the **scheme** of argv[1] via `FUN_10026f690`.
  2. If scheme is `rs-launchs` → rewrites to `https`; if `rs-launch` → rewrites to `http`. It then strips the custom scheme and prepends `http://`/`https://`, appends `binaryType=3`, and uses the result as the jav_config URL.
  3. If argv[1] is absent/invalid, falls back to the default `https://rs.config.runescape.com/l=%i/jav_config.ws?binaryType=%s`. (This is the **only** place `runescape.com` appears on the launch path, and it is a *default*, not a *filter*.)
- **Scheme validator `FUN_10026f690` — the entire acceptance logic (decompiled to integer compares):**
  - `0x636e75616c2d7273` = `"rs-launc"` (LE) + trailing `h` → accepts `rs-launch` / `rs-launchs`
  - `0x70747468` = `"http"` → accepts `http` / `https`
  - `0x6c726f77` = `"worl"` (LE) + `d` → accepts `world`
  - It checks **only the scheme token and minimum length**. There is **no** `strstr(host, "runescape.com")`, no host substring compare, no domain whitelist. The host/authority portion of the URI is passed through verbatim.
- **`%.*s.runescape.com`** (`0x100e1981f`) is referenced **only** by `jag::Console::cmd_setworld_xplat948` (`0x100173a20`) and `jag::Console::cmd_setlobby_xplat948` (`0x100173020`). These are **developer console commands** that *construct* a default world hostname from a short world name (e.g. typing `setworld 1` → builds `world1.runescape.com`). They are not on the launch/connect path and do not validate or reject `localhost`.
- **Conclusion:** the launcher (or a manual launch) can pass `rs-launch://localhost:43594/jav_config.ws` (or `rs-launchs://…` for TLS) as argv[1] and the client will fetch the jav_config from `http://localhost:43594/jav_config.ws` with no host rejection. **No mac-specific host patch (no "P5 patch") is required.** The world/lobby *connection* port still comes from `GetHTTPURL` (P3) and from the jav_config contents, so P3 remains the relevant patch for redirecting the HTTP/JS5 endpoints.

---

## Runtime patching on macOS (PIE + Mach-O guidance for the dylib)

The dylib is injected via `DYLD_INSERT_LIBRARIES=/path/to/libdarkan_patcher.dylib` (the macOS analogue of Linux `LD_PRELOAD`). A constructor (`__attribute__((constructor))`) runs the patch after the main image and its initializers are mapped.

### 1. Find the rs2client image and its ASLR slide
The binary is PIE, so every static VA above is offset by a per-launch slide. Enumerate loaded images:
```c
#include <mach-o/dyld.h>
uint32_t n = _dyld_image_count();
intptr_t slide = 0;
const struct mach_header_64 *hdr = NULL;
for (uint32_t i = 0; i < n; i++) {
    const char *name = _dyld_get_image_name(i);
    if (name && strstr(name, "rs2client")) {          // match the main executable image
        hdr   = (const struct mach_header_64 *)_dyld_get_image_header(i);
        slide = _dyld_get_image_vmaddr_slide(i);
        break;
    }
}
// Runtime address of any documented VA:  runtime = VA + slide
// (equivalently: runtime = (static_VA - 0x100000000) + (uintptr_t)hdr, since __TEXT vmaddr == image base)
```
Image index 0 is usually the main executable; matching by name (`strstr(..., "rs2client")`) is robust regardless.

### 2. Parse `LC_SEGMENT_64` to get segment ranges (for bounds + protection restore)
Walk the load commands from `hdr` to record each segment's `[vmaddr+slide, vmaddr+slide+vmsize)`, `initprot`, and `maxprot`. You need this to (a) bound the prefix search to the right segment, and (b) restore the exact original protection after writing.
```c
const struct load_command *lc = (const struct load_command *)(hdr + 1);
for (uint32_t i = 0; i < hdr->ncmds; i++) {
    if (lc->cmd == LC_SEGMENT_64) {
        const struct segment_command_64 *sg = (const struct segment_command_64 *)lc;
        // sg->segname, sg->vmaddr + slide, sg->vmsize, sg->initprot, sg->maxprot
    }
    lc = (const struct load_command *)((uint8_t *)lc + lc->cmdsize);
}
```
All four patch targets are in `__TEXT` (`initprot = VM_PROT_READ | VM_PROT_EXECUTE = 0x5`).

### 3. Break COW on r-x `__TEXT` before writing, then restore
`__TEXT` is r-x and copy-on-write shared. To write, make a private writable copy via `VM_PROT_COPY`, write, then restore r-x. Use `mach_vm_protect` on `mach_task_self()`:
```c
#include <mach/mach.h>
#include <mach/mach_vm.h>

// page-align the [addr, addr+len) region
mach_vm_address_t a = trunc_page(addr);
mach_vm_size_t    sz = round_page(addr + len) - a;

// 1) make writable + break COW (VM_PROT_COPY = 0x10)
kern_return_t kr = mach_vm_protect(mach_task_self(), a, sz, FALSE,
                                   VM_PROT_READ | VM_PROT_WRITE | VM_PROT_COPY);
// 2) write the patch bytes at the (slid) runtime address
memcpy((void *)addr, patch_bytes, len);
// 3) restore original __TEXT protection
mach_vm_protect(mach_task_self(), a, sz, FALSE,
                VM_PROT_READ | VM_PROT_EXECUTE);
```
Notes:
- `VM_PROT_COPY` (`0x10`) is the critical flag — without it the kernel refuses to make a shared, executable, file-backed page writable and you get `KERN_PROTECTION_FAILURE`. With it, the kernel substitutes a private dirty copy.
- The binary is **unsigned** (no `LC_CODE_SIGNATURE` load command present), so there is no code-signing/`CS_HARD` enforcement to defeat. The COW break alone is sufficient.
- For the RSA strings (P1/P2) in `__TEXT,__cstring`, the same r-x → COW → restore-r-x sequence applies (they are not separate from `__TEXT`). Restore to `VM_PROT_READ | VM_PROT_EXECUTE` to match the original `__TEXT` protection even though the cstring bytes are not executed.
- Flush the instruction cache for code patches (P3) after writing on the off-chance of cache staleness; on x86_64 this is generally unnecessary (coherent I-cache) but `sys_icache_invalidate((void*)addr, len)` is cheap insurance.

### 4. Patch application order / pattern search
- Prefer **prefix/pattern search within the `__TEXT` segment range** (more robust to relocations than hardcoded VAs). Search the slid `__TEXT` byte range for: login prefix, JS5 prefix, and `66 b8 50 00`.
- Each is unique (1 match). For P3, after finding `66 b8 50 00`, write the 2-byte LE port at match+2.
- For P1/P2, write the replacement hex over the matched 256/1024 bytes (left-padded with `'0'`).

---

## What changed vs Linux 948 / Windows 948-5 (summary table)

| Aspect | Linux 948 | Windows 948-5 | **macOS 948-5** |
|---|---|---|---|
| Binary format | ELF (ET_EXEC, non-PIE) | PE32+ | **Mach-O x86_64, PIE** |
| Runtime patch mechanism | `LD_PRELOAD` `.so` + `mprotect` | DLL injection + `VirtualProtect` | **`DYLD_INSERT_LIBRARIES` dylib + `mach_vm_protect` (VM_PROT_COPY)** |
| ASLR | none (fixed VAs) | image-relative (RVA) | **PIE slide via `_dyld_get_image_vmaddr_slide`** |
| RSA moduli location | `.rodata` (r--) | `.rdata` (r--) | **`__TEXT,__cstring` (r-x)** ← in executable segment |
| Login modulus | ROTATED at 948-2 → `aad4a780…` | RELOCATED-identical | **RELOCATED-identical** (`0x100E2346D`) |
| JS5 modulus | ROTATED at 948-2 → `a6400fbc…` | RELOCATED-identical | **RELOCATED-identical** (`0x100E2356E`) |
| HTTP port pattern | `41 b8 50 00 00 00 74` (MOV R8D,0x50 + JZ) | `b8 50 00 00 00 eb` (MOV EAX,0x50 + JMP) | **`66 b8 50 00` (MOV AX,0x50)** ← 16-bit |
| Port patch offset / length | +2 / 4 bytes | +1 / 4 bytes | **+2 / 2 bytes** (16-bit field) |
| Port site count | 1 (standalone GetHTTPURL) | 1 (standalone GetHTTPURL) | **1 (GetHTTPURL inlined into `FUN_1002733c0`)** |
| GetHTTPURL | standalone `@0x1ba190` | standalone `@0x14015ee70` | **inlined** (no standalone fn) |
| ISAAC +50 delta | packed `4×int32` const + `PADDD` | scattered `ADD reg,0x32` (no patch site) | **packed `4×int32` const + `PADDD`** (`0x100ad7b50`) |
| ISAAC patch needed | no | no | **no** |
| Codebase/host regex in client | none (only launcher had it) | none | **none** (`%.*s.runescape.com` is console-only; scheme validator has no host guard) |
| configURI delivery | argv (no `--configURI`) | argv | **argv[1] (no `--configURI`, no `rs-launch://` literal)** |

---

## Proposed Ghidra symbols (mac-9485 project) — NOT yet applied

The mac project was browsed `-readOnly`; no labels were written. If/when annotating the `mac-9485` program, these are the high-confidence renames (all verified above):

### Functions
| Address | Proposed name | Evidence |
|---|---|---|
| `0x1002733c0` | `jag::WorldLobbyData::BuildConnectURL_inlGetHTTPURL` (or `jag::ConnectionManager::BuildHTTPRequestURL`) | inlined GetHTTPURL block + `%s%s:%u` builder; matches Linux GetHTTPURL shape |
| `0x10026ea30` | `jag::ConfigUriParser::ParseRsLaunch` | references `rs-launch`/`rs-launchs`, builds jav_config URL |
| `0x10026f690` | `jag::ConfigUriParser::ValidateScheme` | scheme-only validator (rs-launch/http/world) |
| `0x1000cXXXX` (SendLoginPacket, already named `_xplat948`) | keep `jag::LoginManager::SendLoginPacket_xplat948` | reads ISAAC delta via PADDD @ `0x1000cb894` |

### Data labels
| Address | Proposed name | Meaning |
|---|---|---|
| `0x100E2346D` | `jag::LoginManager::RSA_LOGIN_MODULUS_HEX` | 256-char login modulus |
| `0x100E2356E` | `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX` | 1024-char JS5 modulus |
| `0x100ad7b50` | `jag::LoginManager::ISAAC_DELTA_50` | 4×int32 = 50 (PADDD source) |
| `0x100ed5818` | `jag::WorldLobbyData::DEFAULT_HOST_SENTINEL` | LIVE-mode host sentinel (CMP in GetHTTPURL) |

### Decompiler comments to add
- `0x100273834` — "Patcher target P3 (macOS): MOV AX,0x50 = port 80 for LIVE mode. Pattern `66 b8 50 00`, patch 2 bytes at +2. Inlined GetHTTPURL."
- `0x1000cb894` — "ISAAC +50 delta via PADDD from `__TEXT,__const` @ 0x100ad7b50. Server S2C seed = client seed + 50."

---

## Methodology Notes (macOS specifics)

### Finding the RSA moduli
Identical to Linux/Windows: regex `(?<![0-9a-f])[0-9a-f]{256}(?![0-9a-f])` (login) and `{1024}` (JS5) over the whole file. Each returns exactly 1 match. Prefix-compare against `aad4a780…` / `a6400fbc…` to confirm RELOCATED-identical. Note they live in `__TEXT,__cstring`, so `VA = 0x100000000 + file_offset`.

### Finding the HTTP port site (the trap)
**Do not search for `b8 50 00 00 00`.** On clang/macOS the port is a 16-bit `short`, lowered to `MOV AX, 0x50` = `66 b8 50 00` (operand-size prefix `0x66`). Search for `66 b8 50 00` (unique). Cross-validate with the `ADD EAX,0x2ee0` (12000) and `ADD EAX,0x1b58` (7000) world-port adds in the same function, and confirm the `%s%s:%u` (`0x100e18e44`), `http://` (`0x100e18e55`), `https://` (`0x100e18e4c`) string LEAs nearby. The 32-bit `b8 50 00 00 00` forms in this binary are vtable method-offset switches — never patch those.

### Finding the ISAAC delta
Search `32 00 00 00` × 4 — exactly 1 match in `__TEXT,__const` at `0x100ad7b50`. Confirm the consumer is a `PADDD XMM0, [0x100ad7b50]` inside SendLoginPacket. No patch.

### Confirming no host guard
String-scan for `--configURI`/`configURI` (absent → argv-based) and `rs-launch://` (absent → scheme matched by integer compares). Decompile the parser (`FUN_10026ea30`) and its scheme validator (`FUN_10026f690`); confirm only scheme tokens (`rs-launch`/`http`/`world`) are checked and the host is passed through. Confirm `%.*s.runescape.com` is referenced only by `cmd_setworld`/`cmd_setlobby` (console, off-path).

---

## Open Items

1. **macOS dylib implementation** — the current Rust patcher (`client/launcher/patcher/`) is Linux-only. A macOS target needs the `_dyld_*` image enumeration + `mach_vm_protect`/`VM_PROT_COPY` layer described above. Pattern/prefix search logic is portable; only add the macOS port pattern `66 b8 50 00` (offset +2, len 2).
2. **macOS launcher (`rs3` macOS) patch sites** — not in scope here (only `rs2client.948-5-mac` was analyzed). If a macOS launcher binary exists, its RSA modulus / codebase-regex / LZMA-flag equivalents would need separate analysis (cf. the Linux `rs3linux` P3/P5/P6 targets).
3. **Signed future builds** — this build is unsigned; the `VM_PROT_COPY` COW break is sufficient. A hardened-runtime, hard-signed future build would additionally require disabling library validation / re-signing — revisit if Jagex starts signing the mac client.
