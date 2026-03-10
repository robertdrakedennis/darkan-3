# NXT Master Index (Version Table) Format

Reverse-engineered from the NXT client binary (`rs2client` and `librs2client.so`) via Ghidra MCP.

**Key Functions Analyzed:**
- `jag::Js5MasterIndex::Js5MasterIndex` @ `0x00a1db50` (rs2client) / `0x004d2b20` (librs2client.so) -- Master index parser/constructor
- `jag::Js5ResourceProvider::WorkerOnMessage` @ `0x00a22dc0` (rs2client) -- case 10: master index dispatch, case 0x28: verify master index
- `FUN_00a1aa10` @ `0x00a1aa10` (rs2client) -- Master index download callback
- `FUN_001c1d80` @ `0x001c1d80` (rs2client) -- uint32 BE reader (container header field reader)
- `jag::Packet::gT_uint` @ `0x001d6ee0` (rs2client) -- uint32 BE reader (per-archive field reader)
- `FUN_00bc1d40` @ `0x00bc1d40` (rs2client) -- Whirlpool hash update (NESSIEadd)
- `FUN_00bc20a0` @ `0x00bc20a0` (rs2client) -- Whirlpool hash finalize (NESSIEfinalize)
- `jag::math::BigInteger::ModPow` @ `0x00c21830` (rs2client) -- RSA modular exponentiation
- `jag::math::BigInteger::FromBytes` @ `0x00c225f0` (rs2client) -- BigInteger from byte array

---

## Overview

The master index (a.k.a. "version table") is the JS5 response for archive=255, group=255. It describes all cache indices: their CRCs, versions, whirlpool hashes, file counts, and uncompressed sizes. The NXT client uses it to determine which indices need downloading/updating.

The master index is served as a **standard JS5 container** (compression byte + compressedSize + payload). The client receives the raw container bytes via the JS5 TCP protocol, then the `Js5MasterIndex` constructor parses the container in-place, performing RSA signature verification.

---

## Data Flow: Server to Js5MasterIndex Constructor

### Step 1: JS5 TCP Response

The server sends the master index as a normal JS5 file response:

```
[archive=0xFF(1B)] [group=0x000000FF(4B)] [containerData...]
```

With standard 102,400-byte block framing (see js5-response-format.md).

### Step 2: Download Callback (FUN_00a1aa10)

The JS5 worker thread receives the complete container data and posts it to the main thread:

```
Message: [opcode=0x0A(1B)] [dataSize(2B, ushort BE)] [rawContainerData(dataSize bytes)]
```

The raw container data is the **entire container** as received from the JS5 response (starting with the compression byte).

### Step 3: WorkerOnMessage Case 10

The main thread handler:
1. Reads `dataSize` as ushort (2 bytes, BE)
2. If `dataSize == 0`, master index download failed -- abort
3. Copies `dataSize` bytes of raw container data into a buffer
4. Retrieves RSA key pair from `this + 0x60` (a pointer to a struct with two BigInteger pointers: exponent and modulus)
5. Calls `Js5MasterIndex::Js5MasterIndex(masterIndexObj, rsaKeys)` with the buffer as an implicit parameter

**Critical: NO decompression occurs before passing to the constructor.** The constructor receives and parses the raw container bytes directly.

### Step 4: Js5MasterIndex Constructor

The constructor treats the buffer as a Packet (position-tracked byte array). It parses the container header fields and payload in-place.

---

## Container Wire Format (What the Server Must Send)

The master index container uses **compression = 0** (no compression). The complete byte layout:

```
Offset  Size  Type        Field              Description
------  ----  ----        -----              -----------
0       1     uint8       compression        MUST be 0 (no compression)
1       4     uint32 BE   compressedSize     Total size of everything after this field
                                              = 1 + archiveCount*80 + rsaSignatureSize
5       1     uint8       archiveCount       Number of archive index entries (N)

Per archive entry (N entries, 80 bytes each, starting at offset 6):
+0      4     uint32 BE   crc                CRC32 of the archive's raw index container
+4      4     uint32 BE   version            Archive revision/version number
+8      4     uint32 BE   fileCount          Number of files/groups in the archive
+12     4     uint32 BE   uncompressedSize   Total uncompressed size of the archive
+16     64    bytes       whirlpool          Whirlpool hash of the archive's raw index container

RSA signature block (starts at offset 6 + N*80):
...     var   bytes       rsaSignature       RSA-encrypted signature (see below)
```

### Field Details

**compression (offset 0):** The constructor sets position to 1, effectively skipping this byte. It does NOT read or validate the compression type. However, the JS5 response framing reads it as part of the 10-byte response header. It should be `0`.

**compressedSize (offset 1-4):** Read by the constructor as uint32 BE via `FUN_001c1d80()`. This value is used to calculate the RSA signature block size:

```
rsaSignatureSize = compressedSize - 1 - archiveCount * 80
```

(Where `compressedSize` is the value from container offset 1-4.)

**archiveCount (offset 5):** Read as a single unsigned byte. This is the **first byte of the payload** (at container offset 5). The constructor stores `archiveCount - 1` in the object. Maximum 255 archives.

**Per-archive entries (offset 6+):** Each entry is exactly **80 bytes**. Read order confirmed from the constructor's entry loop:

```c
// Loop starts at byte offset 6, increments by 0x50 (80) per iteration
iVar13 = Packet::gT_uint(packet);    // CRC (4B)
iVar12 = Packet::gT_uint(packet);    // version (4B)
uVar14 = FUN_001c1d80(packet);       // fileCount (4B)
uVar15 = FUN_001c1d80(packet);       // uncompressedSize (4B)
// memcpy 0x40 (64) bytes                whirlpool (64B)
```

Wire order within each 80-byte entry:

```
+0:  CRC (4B)
+4:  version (4B)
+8:  fileCount (4B)
+12: uncompressedSize (4B)
+16: whirlpool (64B)
```

The constructor stores them into the entry object (0x28 = 40 bytes) as:
```c
*piVar7 = iVar13;        // offset 0x00: CRC
piVar7[1] = iVar12;      // offset 0x04: version
// whirlpool stored at offset 0x08 (via FUN_005b7180 -- heap-allocated buffer reference)
*(lVar6 + 0x20) = uVar14;  // offset 0x20: fileCount
*(lVar6 + 0x24) = uVar15;  // offset 0x24: uncompressedSize
```

---

## RSA Signature — DETAILED ANALYSIS

### Signature Block Location

The RSA signature block starts immediately after the per-archive entries:

```
rsaBlockOffset = 6 + archiveCount * 80
rsaBlockSize   = compressedSize - 1 - archiveCount * 80
```

(Where `compressedSize` is the value from container offset 1-4.)

### How the Constructor Extracts the RSA Block

In the `rs2client` decompilation at `0x00a1db50`:

```c
// Position is set to 1 (skip compression byte)
uVar10 = FUN_001c1d80();           // Read compressedSize (4 bytes BE) from offset 1
lVar25 = *(long *)(in_RCX + 0x18); // Current position in packet = 5
lVar32 = lVar25 + 1;               // Skip archiveCount byte -> position = 6
bVar4 = *(byte *)(data + lVar25);  // Read archiveCount byte from offset 5
lVar32 = (bVar4 * 80) + lVar32;    // Skip all archive entries -> position = 6 + N*80
lVar32 = 5 - lVar32;               // Calculate: -(1 + N*80)  (this is -hashLen)
uVar35 = uVar10 + lVar32;          // rsaBlockSize = compressedSize - 1 - N*80
```

The RSA block bytes are then copied from `packet[6 + N*80]` for `rsaBlockSize` bytes.

### RSA Decryption — CRITICAL DETAILS

**In `librs2client.so` (clearer symbol info):**

```c
// pvVar9 = new BigInteger  (for the ciphertext)
mp_init(pvVar9);
mp_read_unsigned_bin(pvVar9, lVar20, sVar25 & 0xffffffff);  // Load RSA block as UNSIGNED BigInt

// puVar10 = new BigInteger  (for the result)
mp_init(puVar10);

// result = ciphertext^exponent mod modulus
iVar7 = mp_exptmod(pvVar9,
                    *(undefined8 *)param_1,    // exponent (from BigInteger* param_1)
                    *(undefined8 *)param_2,    // modulus (from BigInteger* param_2)
                    puVar10);                  // result
```

**Key observation:** The NXT client uses **libtommath** (`mp_read_unsigned_bin`, `mp_exptmod`, `mp_init`, `mp_clear`). The function `mp_read_unsigned_bin` reads a byte array as an **unsigned** big integer. This is different from Java's `BigInteger(byte[])` constructor, which interprets the byte array as **signed** (two's complement).

**In `rs2client`:**

```c
math::BigInteger::BigInteger(this_00, iVar13);
math::BigInteger::FromBytes(this_00, data_00, (int)uVar35);  // Load RSA block
math::BigInteger::ModPow(this_00, plVar17, (long *)*in_RDX, this_01);  // ModPow
```

`FromBytes` is the equivalent of `mp_read_unsigned_bin` -- it reads bytes as an unsigned integer.

### Result Size Check

After RSA decryption, the result BigInteger is converted back to a byte array. The constructor checks the result's byte length:

**In `rs2client`:**
```c
if (local_120 != 0x41)  // 0x41 = 65 decimal
    goto joined_r0x00a1de83;  // REJECT: skip Whirlpool verification, skip entry parsing
```

**In `librs2client.so`:**
```c
if ((local_140 != (ref_counter_base *)0x0) && (*(long *)local_140 == 0x41))
    // proceed with Whirlpool verification
```

**The decrypted RSA result MUST be exactly 65 bytes.** If it is any other size, the entire master index is rejected silently.

### Decrypted Signature Format

```
Offset  Size  Type    Field       Description
------  ----  ----    -----       -----------
0       1     uint8   magic       Must be 0x01 (validation prefix)
1       64    bytes   whirlpool   Whirlpool hash digest
```

Total: 65 bytes (0x41).

### Whirlpool Hash Computation

**In `librs2client.so` (clear symbol names):**

```c
// Hash is computed over the raw container bytes starting at offset 5
// (the archiveCount byte) for (1 + archiveCount * 80) bytes
lVar20 = *(long *)(param_3 + 8);  // Packet buffer pointer
if (lVar20 == 0) {
    puVar26 = (uchar *)0x5;
} else {
    puVar26 = (uchar *)(*(long *)(lVar20 + 8) + 5);  // buffer + 5
}

// lVar12 was computed earlier as: 5 - (6 + N*80) = -(1 + N*80)
// So lVar12 * -8 = (1 + N*80) * 8 = bit length of hash input
Whirlpool::NESSIEadd(&whirlpoolState, puVar26, lVar12 * -8, &whirlpoolState);

// Finalize into a 64-byte output buffer
Packet::Packet(&resultPacket, 0x40);  // Allocate 64-byte buffer
Whirlpool::NESSIEfinalize(&whirlpoolState, &whirlpoolState, resultBuffer);
```

**Hash range:**
```
Start:  container offset 5 (the archiveCount byte)
Length: 1 + archiveCount * 80  (in bytes)
End:    container offset 6 + archiveCount * 80
```

This covers the **archiveCount byte plus all per-archive entries**. It does NOT include:
- The container header (compression byte + compressedSize) -- offsets 0-4
- The RSA signature block itself

### Whirlpool Comparison

**In `librs2client.so`:**

```c
// Compare computed hash (from NESSIEfinalize) against decrypted RSA result bytes [1..64]
lVar12 = 0;
do {
    if (*(char *)(computedHash + lVar12) != *(char *)(decryptedRsa + 1 + lVar12))
        goto LAB_004d3119;  // MISMATCH: reject
    lVar12 = lVar12 + 1;
} while (lVar12 != 0x40);  // Compare all 64 bytes
```

**In `rs2client`:**

```c
// Same comparison, byte by byte, 64 bytes total
// Compares local_f8 (computed Whirlpool) against local_118 + 1 (decrypted RSA, skip byte 0)
if ((char)*local_f8 != *(char *)((long)local_118 + 1)) goto LAB_00a1e880;  // REJECT
lVar32 = 1;
do {
    // Unrolled comparison, 9 bytes per iteration
    if (*(char *)((long)local_f8 + lVar32) != *(char *)((long)local_118 + lVar32 + 1))
        goto LAB_00a1e880;  // REJECT
    // ... (7 more byte comparisons per iteration)
    lVar32 = lVar32 + 9;
} while (lVar32 != 0x40);
```

### What Happens on Verification Failure

When any verification check fails:

1. **Decrypted size != 65:** The code jumps directly to `joined_r0x00a1de83` (rs2client) or falls through to skip entry parsing. The `Js5MasterIndex` object is left in an uninitialized state (no archive entries populated).

2. **Byte[0] != 0x01 or Whirlpool mismatch:** The code jumps to `LAB_00a1e880` / `LAB_004d3119`, which skips the entry parsing loop entirely.

3. **No error is logged.** The failure is completely silent. The client simply has an empty/invalid master index, which means it never knows what archives exist, so it never requests any further files. This manifests as a **30-second timeout** (State 3 timeout) because the client sits idle waiting for something that will never happen.

4. **No RSA keys (both null):** If `param_1` and `param_2` (or `data` in rs2client) are both null, the constructor skips RSA entirely and treats the raw bytes after the entries as the "decrypted" result. This is the "unsigned" / "no RSA" path.

---

## CRITICAL BUG: Java BigInteger Signedness

### The Problem

The NXT client uses `mp_read_unsigned_bin` (libtommath) which reads byte arrays as **unsigned** integers. Java's `BigInteger(byte[])` constructor interprets byte arrays as **signed** (two's complement).

When our server does:
```kotlin
val rsa = BigInteger(output).modPow(exponent, modulus).toByteArray()
```

If `output[0]` has the high bit set (value >= 0x80), Java interprets it as a negative number. The resulting `modPow` produces a completely different (wrong) ciphertext. The client then decrypts to garbage and silently rejects the master index.

**However**, for our specific case, `output[0] = 0x01`, so the plaintext is always positive. The issue is more subtle:

### The Real Issue: `BigInteger.toByteArray()` Padding

Java's `BigInteger.toByteArray()` returns a **signed** two's complement representation. If the result of `modPow` has its highest bit set, `toByteArray()` prepends a `0x00` byte to keep the number positive. This means:

- For a 1024-bit modulus, the RSA ciphertext should be exactly 128 bytes
- But `toByteArray()` might return 129 bytes (with a leading `0x00`)
- Or it might return fewer bytes if the result has leading zero bits

The client's `mp_read_unsigned_bin` reads exactly `rsaBlockSize` bytes from the container. Our `compressedSize` field tells the client where the RSA block ends and how big it is. So if we write 129 bytes but the client reads all 129, it would do `mp_read_unsigned_bin(data, 129)` and get the right unsigned value (leading `0x00` is harmless for unsigned reads).

**The real question is:** Does the server's `RSA.crypt()` output match what the client expects to RSA-decrypt back to 65 bytes?

### Server-Side RSA Signing

Our server does:
```kotlin
// output = [0x01][64B whirlpool] = 65 bytes
val rsa = BigInteger(output).modPow(exponent, modulus).toByteArray()
```

This signs using the **private** exponent. The client then decrypts using the **public** exponent (65537).

### Client-Side RSA Decryption

```c
mp_read_unsigned_bin(ciphertext_bigint, rsaBlockBytes, rsaBlockSize);
mp_exptmod(ciphertext_bigint, publicExponent, modulus, result_bigint);
// Convert result_bigint back to bytes, check length == 65
```

The conversion back to bytes (BigInteger-to-bytes in the rs2client constructor) uses the `DivMod` loop pattern, extracting bytes via repeated division by 256, then reversing the array. The resulting byte count is the number of significant bytes in the BigInteger -- no padding. So if the mathematical result is `0x01` followed by 64 bytes, it produces exactly 65 bytes.

### Potential Issues to Check

1. **Leading zero from `toByteArray()`:** If `BigInteger(output)` produces a leading `0x00` when converting to bytes for the RSA block, the block size in the container changes. The client reads `rsaBlockSize = compressedSize - 1 - N*80`, so as long as `compressedSize` is consistent with the actual RSA block written, this is fine.

2. **RSA block smaller than modulus:** After `modPow`, if the result has leading zero bytes, `toByteArray()` returns fewer bytes. For a 4096-bit (512-byte) modulus, the RSA output could be anywhere from 1 to 513 bytes (with sign byte). We write however many bytes `toByteArray()` returns, and set `compressedSize` accordingly.

3. **Key size mismatch:** The server uses a 4096-bit RSA key. The patcher must replace the **same size** modulus in the client binary. If the client's original modulus storage is smaller than 4096 bits, the patcher might not have enough space. More importantly, the RSA ciphertext size depends on the modulus size. The client expects `rsaBlockSize` to match what was signed with a specific key size.

### Recommendation

Use `BigInteger(1, output)` (positive signum constructor) for the plaintext to ensure unsigned interpretation:
```kotlin
val rsa = BigInteger(1, output).modPow(exponent, modulus).toByteArray()
```

And strip any leading `0x00` byte from `toByteArray()` if present:
```kotlin
val rsaBytes = if (rsa[0] == 0.toByte() && rsa.size > 1) rsa.copyOfRange(1, rsa.size) else rsa
```

This ensures the RSA block written to the container has no unexpected padding.

---

## RSA Key Location in the Binary

The RSA keys for JS5 master index verification are accessed through the `Js5ResourceProvider` object at offset `this + 0x60`. This field is a pointer to a structure containing two `BigInteger*` fields:

```
struct Js5RsaKeys {
    BigInteger* exponent;   // offset 0x00 -- public exponent (typically 65537 = 0x10001)
    BigInteger* modulus;    // offset 0x08 -- RSA modulus
};
```

**In WorkerOnMessage:**
```c
plVar5 = *(long **)(this + 0x60);  // Js5RsaKeys*
Js5MasterIndex::Js5MasterIndex(obj, plVar5);
```

**In the constructor (rs2client):**
```c
// data = Js5RsaKeys* passed as second param (or via in_RDX register)
math::BigInteger::ModPow(ciphertext, *data,      // *data = exponent
                         (long *)*in_RDX,         // *in_RDX = modulus
                         result);
```

**In the constructor (librs2client.so):**
```c
// param_1 = exponent BigInteger*, param_2 = modulus BigInteger*
mp_exptmod(ciphertext,
           *(undefined8 *)param_1,  // exponent's mp_int data
           *(undefined8 *)param_2,  // modulus's mp_int data
           result);
```

The patcher must replace the modulus stored in the BigInteger pointed to by `(*(Js5RsaKeys**)(resourceProvider + 0x60))->modulus`. The actual modulus bytes are in the `mp_int`'s digit array, which is a heap allocation. The patcher typically pattern-matches the known Jagex modulus bytes in the binary's data segment and overwrites them.

---

## Verification Procedure Summary

```
1. Extract RSA block: bytes from offset (6 + N*80) to end of compressedSize data
   - rsaBlockSize = compressedSize - 1 - N*80
2. RSA decrypt: result = rsaBlock^publicExponent mod modulus
   - Uses UNSIGNED big integer interpretation (mp_read_unsigned_bin / libtommath)
3. Check decrypted byte length == 65 (0x41)
4. Check decrypted[0] == 0x01
5. Compute Whirlpool hash over container bytes [5 .. 5 + 1 + N*80)
   - Input: archiveCount byte + all 80-byte entries
   - NESSIEadd bit count = (1 + N*80) * 8
6. Compare decrypted[1..64] against computed 64-byte Whirlpool hash
   - Byte-by-byte comparison, all 64 bytes must match
7. If ANY check fails: master index is silently rejected
   - No error log, no disconnect signal
   - Client simply has no valid master index
   - Result: client sits idle, 30-second State 3 timeout
```

---

## Correct VersionTableBuilder Layout

```
Byte 0:     compression = 0x00
Bytes 1-4:  compressedSize = 1 + archiveCount * 80 + rsaBlockSize  (uint32 BE)
Byte 5:     archiveCount (uint8)

For each archive (i = 0 to archiveCount-1):
  Bytes 6+i*80+0  .. 6+i*80+3:   CRC32 of raw index container (uint32 BE)
  Bytes 6+i*80+4  .. 6+i*80+7:   version/revision (uint32 BE)
  Bytes 6+i*80+8  .. 6+i*80+11:  fileCount (uint32 BE)
  Bytes 6+i*80+12 .. 6+i*80+15:  uncompressedSize (uint32 BE)
  Bytes 6+i*80+16 .. 6+i*80+79:  Whirlpool hash (64 bytes)

After all entries:
  RSA signature block (variable size, depends on modulus size)
  - For 1024-bit key: typically 128 bytes
  - For 4096-bit key: typically 512 bytes

Whirlpool hash for RSA covers bytes [5 .. 6 + archiveCount * 80)
RSA plaintext = [0x01] [64-byte Whirlpool hash] = 65 bytes total
RSA ciphertext = plaintext^privateExponent mod modulus
```

---

## Summary of Key Differences from OSRS Format

| Aspect | OSRS Format | NXT RS3 Format |
|--------|-------------|----------------|
| Entry size | 72 bytes | **80 bytes** |
| Fields per entry | CRC + revision + whirlpool | CRC + version + **fileCount** + **uncompressedSize** + whirlpool |
| Field order | CRC, revision, whirlpool | CRC, version, **fileCount, uncompressedSize**, whirlpool |
| Format version byte | Present before entries | **Not present** (no formatVersion field) |
| Checksum field | Present (4 bytes) | **Not present** (container compressedSize serves a different purpose) |
| archiveCount position | After format headers | **Byte 5** (first payload byte, immediately after container header) |
| Whirlpool hash range | Over format + entries | Over archiveCount byte + all entries (bytes 5 to 6+N*80) |
| RSA plaintext | [0x01][64B hash] = 65 bytes | [0x01][64B hash] = 65 bytes (same) |
| BigInteger interpretation | Signed (Java) | **Unsigned** (libtommath `mp_read_unsigned_bin`) |
| RSA library | Java BigInteger | **libtommath** (`mp_exptmod`) |
