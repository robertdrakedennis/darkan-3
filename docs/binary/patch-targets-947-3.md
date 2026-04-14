# Patcher Targets - Rev 947-3

Binary: rs2client.947-3 (Ghidra port 8081)
Compared against: rs2client.947-1 (Ghidra port 8080)

## Summary

All patcher targets from 947-1 remain valid in 947-3. Neither RSA key was rotated. The patcher patterns are identical; only the byte offsets changed due to binary layout shift (~+0x340 bytes).

## Patch Target 1: Login RSA Modulus (1024-bit, 256 hex chars)

- **Address (947-3):** `0x01120898`
- **Address (947-1):** `0x01120558`
- **Key prefix:** `8f389edb4b56fdafc410be11bd0b4dd2...`
- **Key rotated?** NO -- same key as 947-1
- **Patcher behavior:** Search for prefix `8f389edb`, replace 256 hex chars with `DARKAN_RSA_MODULUS`

## Patch Target 2: JS5 RSA Modulus (4096-bit, 1024 hex chars)

- **Address (947-3):** `0x011209a0`
- **Address (947-1):** `0x01120660` (approx)
- **Key prefix:** `87300ccecc0674194a79ac92a9e18f1040f7c682...`
- **Key rotated?** NO -- same key as 947-1
- **Patcher behavior:** Search for prefix `87300cce`, replace 1024 hex chars with `DARKAN_JS5_RSA_MODULUS`
- **Note:** Ghidra `list_strings` could not find this string (likely not defined as a string data type). Verified via raw binary search (`strings -n 500`).

## Patch Target 3: HTTP Port 80 (Site 1)

- **Address (947-3):** `0x00253743`
- **Pattern:** `41 b8 50 00 00 00 74` (mov r8d, 80; je ...)
- **Patch:** Replace `0x50` (80) with target port at offset +2
- **Found?** YES (1 match)

## Patch Target 4: HTTP Port 80 (Site 2)

- **Address (947-3):** `0x003badd3`
- **Pattern:** `41 b8 50 00 00 00 0f` (mov r8d, 80; jcc ...)
- **Patch:** Replace `0x50` (80) with target port at offset +2
- **Found?** YES (1 match)

## ISAAC Delta

- **Address (947-3):** `0x00dcc0d0` (4 x int32, all value 50)
- **Address (947-1):** `0x00dcbd90`
- **Value:** 50 (unchanged)
- **Verified via:** Decompilation of `FUN_0026f8d0` (LoginStepSendLoginPacket inner function), which adds `_DAT_00dcc0d0..0x00dcc0dc` to the 4 ISAAC key ints.

## Login Protocol

- **Connection types:** 14 (CONNECT_LOGIN), 16 (LOBBY), 19 (WORLD), 30 (RECONNECT) -- all unchanged
- **LoginProt opcodes:** 14, 15, 16, 19, 23, 24, 26, 27, 28, 29, 30, 31 -- all unchanged
- **RSA block format:** Same as 947-1 (magic byte, ISAAC keys, session check)
- **XTEA encryption:** Same (`tinyKeyEncrypt` call present in login packet builder)

## Patcher Update Requirements

**No patcher changes needed for 947-3.** The existing patcher that works with 947-1 will work identically with 947-3 because:
1. Both RSA keys have the same prefix bytes (search-based patching works)
2. Both HTTP port patterns exist with the same byte sequences
3. The patcher uses byte-pattern search, not fixed addresses
