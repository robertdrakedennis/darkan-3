# Patcher Targets - Rev 948-2-2

Binary: rs2client.948-2-2 (Ghidra port 8082, BuildID `34676a96c8362904deecaf49b691e7b5ef1aa9a3`)
Cross-reference: rs2client.947-3 (previous target)
Launcher binary: rs3linux.948 (BuildID `527d1d13181e94a685d6e8978bb20f55196f30c6`)

## Summary

Both rs2client RSA keys (login + JS5) were **ROTATED** between 947-3 and 948. The HTTP port pattern lost its second inlined site — only one site needs patching in 948. All three rs3linux targets (launcher RSA modulus, codebase regex, LZMA flag) are **UNCHANGED** from 947-3.

### Required Patcher Changes
- `RS2CLIENT_MODULUS_PREFIX`: `8f389edb...` → `aad4a7804c34bb788d52dbd5f70e5721`
- `RS2CLIENT_JS5_MODULUS_PREFIX`: `87300cce...` → `a6400fbcbd9dd09f48045caf3f543dd6`
- `HTTP_PORT_PATTERN_2` (the `... 0f` variant) no longer matches — pattern can be left in place (it just won't find a match) or removed.

### Required EnvVars Changes
- `JAGEX_LOGIN_RSA_MODULUS_HEX` must be updated to the new 256-char hex (`aad4a780...`).
- `JAGEX_JS5_RSA_MODULUS_HEX` must be updated to the new 1024-char hex (`a6400fbc...`).

---

## Patch Target P1: Login RSA Modulus (1024-bit, 256 hex chars)

- **Binary:** rs2client.948-2-2
- **Address:** `.rodata @ 0x0104a428` (file offset 17081384)
- **Status:** **ROTATED** vs 947-3 (was at `0x01120898`)
- **Old prefix (947-3):** `8f389edb4b56fdafc410be11bd0b4dd2`
- **New prefix (948):** `aad4a7804c34bb788d52dbd5f70e5721`
- **Full new value (256 hex chars):**
  ```
  aad4a7804c34bb788d52dbd5f70e5721528d7f01c6aa1a93b7322ea0127f4068
  2f2d766a26728f0758ce47c9cde8003a170381352a143320ae3cc884a9116008
  ec09104ecdbafbcd0f537dfba67c7340ea3ca30caf91c20f8d98ac9b9a613b25
  cd23f586d3fae88823f1ea48aeeb31c9897c17c45aeb0771521a5c2df3cb0799
  ```
- **Ghidra symbol:** `jag::LoginManager::RSA_LOGIN_MODULUS_HEX` @ 0x0104a428
- **Init function:** `jag::GlobalRSAKeys_Init` @ 0x000e8f90 — `__cxa_atexit`-registered constructor calls `FUN_000cef60(&jag::LoginManager::g_LoginRSAModulus, RSA_LOGIN_MODULUS_HEX, 0x10)` which parses the hex string into a `jag::math::BigInteger`.
- **Consumers (xrefs to `g_LoginRSAModulus` @ 0x015c84e0):**
  - `jag::LoginManager::CreateLoginRSAPacket` @ 0x001ac905 (DATA xref)
  - `jag::LoginManager::StartRSAPacket` @ 0x001ac2cd (DATA xref)
  - `jag::LoginManager::LoginStepWaitingSocialNetworkToken` @ 0x001ad499 (DATA xref)
- **Methodology:** Search `.rodata` for any 256-char lowercase hex string via `grep -aob '[0-9a-f]\{256\}'`. The login modulus is the only such standalone string (the 1024-char JS5 modulus appears separately, no shorter strings of 256+ chars exist except the login modulus and substrings of JS5). Then trace its xrefs back through the `GlobalRSAKeys_Init` constructor.
- **Patcher behavior:** Search for prefix `aad4a7804c34bb788d52dbd5f70e5721`, replace 256 hex chars with `DARKAN_RSA_MODULUS` (left-padded to 256 chars with `0`).

---

## Patch Target P2: JS5 Master Index RSA Modulus (4096-bit, 1024 hex chars)

- **Binary:** rs2client.948-2-2
- **Address:** `.rodata @ 0x0104a530` (file offset 17081648)
- **Status:** **ROTATED** vs 947-3 (was at `0x011209a0`)
- **Old prefix (947-3):** `87300ccecc0674194a79ac92a9e18f10`
- **New prefix (948):** `a6400fbcbd9dd09f48045caf3f543dd6`
- **Full new value (1024 hex chars, formatted in 64-char blocks):**
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
- **Ghidra symbol:** `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX` @ 0x0104a530
- **Init function:** `jag::GlobalRSAKeys_Init` @ 0x000e8f90 — calls `FUN_000cef60(&jag::Js5MasterIndex::g_JS5RSAModulus, RSA_JS5_MODULUS_HEX, 0x10)`.
- **Consumers (xrefs to `g_JS5RSAModulus` @ 0x015c84d0):**
  - `jag::Js5MasterIndex::Construct` @ 0x00496a10 (stores ptr at offset +0x88 in master index struct, used for signature verification)
- **Methodology:** Search `.rodata` for any 1024-char lowercase hex string via `grep -aob '[0-9a-f]\{1024\}'`. Only one match. Verify the immediately-preceding 8 bytes of NULs separate it from the login modulus. Trace xrefs.
- **Patcher behavior:** Search for prefix `a6400fbcbd9dd09f48045caf3f543dd6`, replace 1024 hex chars with `DARKAN_JS5_RSA_MODULUS` (left-padded to 1024 chars with `0`).

---

## Patch Target P3: rs3linux Launcher RSA Modulus (4096-bit, 1024 hex chars)

- **Binary:** rs3linux.948 (BuildID `527d1d13...`)
- **File offset:** 6236912 (0x5f2af0)
- **Status:** **UNCHANGED** vs 947-3
- **Prefix:** `a49962fc0737fddcd94c0daf84e5d214`
- **Full value (1024 hex chars, formatted in 64-char blocks):**
  ```
  a49962fc0737fddcd94c0daf84e5d214a130b4eda82167e0e9507e1ca421b500
  175bf6b09c9ea9a4f3cdba0c8da28c696877e13244aa3baa53e522865db2b214
  8e1a69d7e95567dcb88cbce828658ea0487cbd85d348aa48457f3f968d101ba2
  5254a43e1b2b5051e9f5a3184a2dec6c59eb5b2ace3719d46feec4a82801b0f7
  1b79c36e6547cf0e22093a519703c9becdc829c749610b3fac188ac9811eba02
  9fbaf13aec78513957f7d4d3fd57c90bfcce0922cdce549e6dec3de0ce7c6b3b
  40fefd54f6ebd844c852100174f0e4b43386e49d8f1283b74940a9a7915681ff
  dc7853d37be466926aa1aa3a4940aed39b5a9b767b85035c28802dc82b391b37
  97240bb2c57522498f4ef0801e6a28147f4c6d47b506417926e07e53bf9ef5a9
  7a517cc7cf2b00cceb1dc4842175a86710cd5dae9b89114bbc41842d92a8c7dc
  20d3994511fe60f03c8137f2b9c25082f3ceaf80307187c785ec1d3f1750b290
  82f42ce4d999279cbb9f196436c61913d46c4242f939dc52d1f31e1ed5a1058c
  ad63ebd6f33114eb48b343ef6ed3a2dcd2d2e02ab1c94ee2f504a14c612f4151
  81d8e5958197da90223bd577cebf948a5adc79a642c1c437365d49f4ac170526
  ca36d9c2057e02aa96a3ade6bc8d1d0dce8f5d9205e361094009adcd14783932
  74d0c6b71453aa0b27f5ed187c01b9f5d9573d1253f2b90099ddda628838fee9
  ```
- **Methodology:** rs3linux is not in Ghidra. Direct binary search: `grep -aob '[0-9a-f]\{1024\}' rs3linux`. Only one match.
- **Patcher behavior:** Search for prefix `a49962fc0737fddcd94c0daf84e5d214`, replace 1024 hex chars with `DARKAN_RSA_MODULUS` (left-padded to 1024 chars with `0`).

---

## Patch Target P4: HTTP Port 80 (inlined immediate) — REDUCED TO 1 SITE

- **Binary:** rs2client.948-2-2
- **Status:** **PATTERN UNCHANGED but only 1 inlined site (was 2 in 947-3)**

### Site 1 (only site in 948)
- **Address:** `0x001ba223` (inside `jag::WorldLobbyData::GetHTTPURL` @ 0x001ba190)
- **Pattern:** `41 b8 50 00 00 00 74` — `MOV R8D, 0x50` followed by `JZ rel8`
- **Patch:** Replace `0x50 00 00 00` at offset +2 with the 4-byte LE port immediate.

### Site 2 (REMOVED in 948)
- **947-3 had** a second inlined copy at `0x003badd3` with pattern `41 b8 50 00 00 00 0f` (MOV + 2-byte JCC).
- **948 has no such site.** The compiler no longer inlines `GetHTTPURL`; instead it's a standalone function called by 3 callers (`FUN_002b748c`, `FUN_0030886b`, `FUN_00497190`). The single `MOV R8D, 0x50` literal lives inside the function body.
- **Implication for patcher:** The existing `HTTP_PORT_PATTERN_2 = "41 b8 50 00 00 00 0f"` simply won't find a match — that's harmless. The existing patcher code (which logs "pattern not found" but doesn't abort) handles this gracefully. **No required patcher change**, but `HTTP_PORT_PATTERN_2` could be removed for clarity.

### Other matches (false positives — verified not GetHTTPURL)
- `0x007de339` — inside a graphics function (`FUN_007de290` — texture setup). Byte+6 = `0xb9` (`MOV ECX, ...`).
- `0x009ef2e0` — inside a matrix-copy function (`FUN_009eef10`). Byte+6 = `0x41` (REX prefix for SSE ops).

These do NOT match the existing pattern (`41 b8 50 00 00 00 74` or `... 0f`), so they're safely skipped.

### Decompiled GetHTTPURL (for context)
```c
long * jag::WorldLobbyData::GetHTTPURL(long *result, long *worldData) {
    char isLive = *(char *)(*worldData + 0x468);  // ModeWhere flag
    short port;
    if (isLive == 0) {
        port = 0x50;  // <-- THE PATCH SITE: hardcoded port 80 for LIVE mode
        if (worldData->host != &DAT_015bf1c0) {
            short worldId = (short)(int)worldData[1];
            port = worldId + 7000;
            if (worldData->protocol == 1) {
                port = worldId + 12000;
            }
        }
    } else {
        port = worldData->customPort;  // already-set port
    }
    // ...
    sprintf(result, "%s://%s:%d/", scheme, host, port);
    return result;
}
```

---

## Patch Target P5: rs3linux LZMA Flag (21-byte pattern, patch byte at offset 13)

- **Binary:** rs3linux.948
- **File offset:** 0x17136a (1511786)
- **Status:** **UNCHANGED** vs 947-3 (same 21-byte pattern)
- **Pattern (21 bytes):**
  ```
  C6 80 81 01 00 00 00     ; MOV byte ptr [RAX+0x181], 0x00  (written flag = 0)
  C6 80 82 01 00 00 01     ; MOV byte ptr [RAX+0x182], 0x01  <-- patch byte 13 (0x01 → 0x00)
  C6 80 83 01 00 00 01     ; MOV byte ptr [RAX+0x183], 0x01  (post-write check = 1)
  ```
- **Patch:** Change byte at offset +13 (the immediate at the end of the second MOV) from `0x01` to `0x00`. This disables LZMA decompression of the downloaded rs2client binary so it's saved as-is.
- **Methodology:** Direct binary search with Python regex `\xC6\x80\x81\x01\x00\x00.\xC6\x80\x82\x01\x00\x00.\xC6\x80\x83\x01\x00\x00.` to allow the three immediate bytes to vary. Only one match.

---

## Patch Target P6: rs3linux Codebase URL Regex

- **Binary:** rs3linux.948
- **File offset:** 0x5f254e (6231374)
- **Status:** **UNCHANGED** vs 947-3
- **Pattern (50 chars including null terminator):**
  ```
  ^https?://[a-z0-9\-]*\.?runescape.com(:[0-9]+)?/\0
  ```
- **Replacement (14 chars + null + null padding):**
  ```
  ^https?://.*/\0\0\0\0\0\0\0\0\0\0...
  ```
- **Methodology:** `grep -aob 'runescape\.com' rs3linux` returns 2 hits. The first (at file offset 6235496) is in the regex literal; context bytes around show `^https?://[a-z0-9\\-]*\\.?runescape.com(:[0-9]+)?/`. The second (at file offset 7036770) is part of the Jagex default JS5 config URL `https://rs.config.runescape.com/k=5/l=$(Language:0)/jav_config.ws` — NOT a patch target.
- **Patcher behavior:** Search for `^https?://[a-z0-9\-]*\.?runescape.com(:[0-9]+)?/`, write `^https?://.*/` + zero padding to fill the original 49 chars.

---

## Patch Target P7 (Sanity Check): ISAAC Delta

- **Binary:** rs2client.948-2-2
- **Address:** `.rodata @ 0x00cb72d0` (4 consecutive int32s)
- **Value:** `50` (decimal) — 4 ints all `0x32 0x00 0x00 0x00`
- **Status:** **UNCHANGED** vs 947-3 (was at `0x00dcc0d0`)
- **Ghidra symbol:** `jag::LoginManager::ISAAC_DELTA_50` @ 0x00cb72d0
- **Consumer:** `jag::LoginManager::SendLoginPacket` @ 0x001c1800 — at instruction `0x001c2692` it reads these 4 ints to derive the server-to-client ISAAC seed:
  ```c
  jag::Isaac::Init(uVar10, &client_keys);  // C2S cipher = ISAAC(client_keys)
  server_seed[0] = client_keys[0] + 50;
  server_seed[1] = client_keys[1] + 50;
  server_seed[2] = client_keys[2] + 50;
  server_seed[3] = client_keys[3] + 50;
  jag::Isaac::Init(uVar6, &server_seed);  // S2C cipher = ISAAC(client_keys + 50)
  ```
- **Implication for server:** The Darkan ISAAC implementation must continue using `+50` for the server-to-client cipher seed (unchanged contract).

---

## Methodology Notes for Future Migrations

### Finding RSA moduli
1. Both rs2client RSA moduli are stored as lowercase ASCII hex strings in `.rodata`.
2. Login modulus: exactly 256 chars; JS5 modulus: exactly 1024 chars.
3. Search via `grep -aob '[0-9a-f]\{256\}'` (login) and `grep -aob '[0-9a-f]\{1024\}'` (JS5) — the former returns exactly 1 match for the login modulus PLUS overlapping windows on the JS5 modulus; the 1024-char search returns exactly 1 unique match for JS5.
4. Confirm by following xrefs to the byte address back through the global init constructor (currently `FUN_000e8f90` aka `jag::GlobalRSAKeys_Init`). The constructor calls `FUN_000cef60(&global_modulus, "...hex...", 0x10)` which parses the hex into a `BigInteger`.
5. Verify the login modulus is consumed by `CreateLoginRSAPacket` / `StartRSAPacket`, and the JS5 modulus is consumed by `Js5MasterIndex::Construct`.

### Finding HTTP port inline sites
1. Search executable region for `41 b8 50 00 00 00` (`MOV R8D, 0x50`).
2. For each match, inspect the byte at offset +6:
   - `0x74` (JZ short) or `0x0F` (JCC long) — likely GetHTTPURL inline.
   - `0xb9`, `0xba`, `0x41`, `0x49`, etc. — false positive (graphics code with consecutive MOVs).
3. Verify each candidate is in or near a function that constructs URL strings (xref to the `%s/ms?m=0&a=%u&k=%d&g=%u&c=%d&v=%d` format string in `.rodata`, currently at `0x00ffc620`).
4. In 948, the compiler de-inlined GetHTTPURL — only 1 literal site exists in the standalone function. Future builds may re-inline or re-expand.

### Finding the ISAAC delta
1. Search for 4 consecutive int32s of value 50: `32 00 00 00 32 00 00 00 32 00 00 00 32 00 00 00`.
2. There should be exactly one match in `.rodata`.
3. Confirm by xref — the consumer is `SendLoginPacket`'s ISAAC seed derivation.

### Finding rs3linux patches (no Ghidra)
1. `grep -aob` for the prefix of the previous patch (RSA prefix, regex text).
2. For binary code patches (LZMA flag), use a Python regex with wildcards for variable bytes.
3. If a prefix no longer matches, the key was rotated — search for any new 1024-char hex string in `.rodata`.

### File offset → .rodata address mapping (rs2client.948-2-2)
- `.rodata` starts at virtual `0x00cb65a0` (file offset varies by ELF segment mapping; Ghidra loads the binary with file-to-virtual offset of 0 in 948, i.e. file offset == virtual address for `.text` and `.rodata`).
- For 948 specifically: file offset 17081384 = virtual `0x0104a428` (verified via `search_memory_pattern`).

---

## Renamed Ghidra Symbols (948)

In the Ghidra port 8082 project (rs2client.948-2-2), the following symbols were renamed during this migration:

### Functions
| Address | New name |
|---|---|
| 0x000e8f90 | `jag::GlobalRSAKeys_Init` |
| 0x00496a10 | `jag::Js5MasterIndex::Construct` |
| 0x001ba190 | `jag::WorldLobbyData::GetHTTPURL` |
| 0x008f7f10 | `jag::ConnectionManager::BuildHTTPRequestURL` |
| 0x001c1800 | `jag::LoginManager::SendLoginPacket` |

### Data labels
| Address | New name | Meaning |
|---|---|---|
| 0x0104a428 | `jag::LoginManager::RSA_LOGIN_MODULUS_HEX` | 256-char hex string |
| 0x0104a530 | `jag::Js5MasterIndex::RSA_JS5_MODULUS_HEX` | 1024-char hex string |
| 0x015c84e0 | `jag::LoginManager::g_LoginRSAModulus` | parsed BigInteger |
| 0x015c84e8 | `jag::LoginManager::g_LoginRSAExponent` | parsed BigInteger ("10001") |
| 0x015c84d0 | `jag::Js5MasterIndex::g_JS5RSAModulus` | parsed BigInteger |
| 0x015c84d8 | `jag::Js5MasterIndex::g_JS5RSAExponent` | parsed BigInteger ("10001") |
| 0x00cb72d0 | `jag::LoginManager::ISAAC_DELTA_50` | 4x int32, value 50 |

### Decompiler comments added
- 0x000e907f — Patcher target P1 description
- 0x000e90e3 — Patcher target P2 description
- 0x001ba223 — Patcher target P4 description (single-site note)
- 0x001c2692 — ISAAC delta usage explanation
