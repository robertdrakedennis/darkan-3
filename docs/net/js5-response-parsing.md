# JS5 TCP Response Parsing — NXT Client (rs2client rev 946)

Reverse-engineered from the NXT client binary (`rs2client`) via Ghidra MCP, cross-referenced
against the symbolic binary (`librs2client.so` rev ~890) for function naming.

**Binary verified against:** rs2client (stripped, rev 946) on Ghidra port 8080

**Functions Analyzed:**

| Function | rs2client Address | librs2client.so Address | Purpose |
|----------|------------------|------------------------|---------|
| `jag::ConnectionManager::MainLogic` | `0x00a2ba60` | N/A (was `Js5WorkerThread::OnInterval` at `0x004a84f0`) | TCP response reader + request sender |
| `jag::Js5WorkerThread::GroupDownloaded` | `0x00a1b200` | `0x0051c4c0` | Processes completed group downloads |
| `jag::Js5WorkerThread::IndexDownloaded` | `0x00a1ab00` | N/A | Processes completed index downloads |
| `jag::Js5WorkerThread::OnMessage` | `0x00a20970` | `0x00686c90` (OnMessage) + `0x004a84f0` (OnInterval) | Message dispatcher (merged OnInterval+OnMessage in rs2client) |
| `jag::Js5NetQueue::RequestData` | `0x009d6790` | `0x003e66a0` | Enqueues TCP download request |
| `jag::Js5NetQueue::GroupDownloadedCallback` | `0x00299e90` | N/A | Thin wrapper calling GroupDownloaded |
| `jag::Js5NetQueue::IndexDownloadedCallback` | `0xa1b110` | N/A | Thin wrapper calling IndexDownloaded |
| `jag::Js5WorkerThread::CreateResponsePacket` | `0xa1a100` | N/A | Thread-safe Packet pool allocator |
| `FUN_00b73740` (response complete handler) | `0x00b73740` | N/A | Invokes download callback, removes request from queue |
| `FUN_009d6b90` (request sender) | `0x009d6b90` | N/A | Writes request packet to TCP socket |

---

## Architecture Change: rs2client vs librs2client.so

**Critical finding:** In `librs2client.so` (rev ~890), the TCP response reading loop lived in
`Js5WorkerThread::OnInterval` and the Js5NetQueue state was accessed via `plVar33 = this->field_0x138`
(the queue object). In `rs2client` (rev 946), this code has been **moved into
`jag::ConnectionManager::MainLogic`** at `0x00a2ba60`. The ConnectionManager now owns the JS5
TCP connection lifecycle, the request sending loop, and the response reading loop.

The `Js5WorkerThread::OnMessage` in rs2client is a merged OnInterval+OnMessage handler that only
handles message dispatch (opcodes 2, 7, 10, 0x14, 0x1e, 0x28) for requesting downloads. The actual
TCP I/O is in `ConnectionManager::MainLogic`.

---

## TCP Response Reading State Machine (from `ConnectionManager::MainLogic`)

The response reader operates on the Js5NetQueue object (`plVar4 = this->field_0x1b0`). Key fields:

| Queue field offset | Purpose |
|-------------------|---------|
| `plVar4[0]` / `plVar4[1]` | Urgent request vector (start/end pointers) |
| `plVar4[4]` / `plVar4[5]` | Prefetch request vector (start/end pointers) |
| `plVar4[8]` / `plVar4[9]` | ClientStream ref-counter / ClientStream pointer |
| `plVar4[10]` | Last data received timestamp (ms) |
| `plVar4[0xf]` (offset 0x78) | Phase 1 header read Packet |
| `plVar4[0x11]` (offset 0x88) | Phase 1 header buffer pointer |
| `plVar4[0x12]` (offset 0x90) | Phase 1 bytes read so far |
| `plVar4[0x13]` (offset 0x98) | Phase 2 header read Packet |
| `plVar4[0x15]` (offset 0xa8) | Phase 2 header buffer pointer |
| `plVar4[0x16]` (offset 0xb0) | Phase 2 bytes read so far |
| `plVar4[0x17]` (offset 0xb8) | Block bytes read counter (uint32) |
| `plVar4[0x18]` (offset 0xc0) | Current queue pointer (urgent or prefetch vector) |
| `(long)plVar4 + 0xbc` | Current response ID (int32, -1 = no active response) |

### State Machine

```
State: currentResponseId == -1 (no active response)
    |
    | Read up to (5 - phase1BytesRead) bytes into phase1 buffer
    | If phase1BytesRead < 5: return (partial read, try again next tick)
    |
    | Parse: archive = buffer[0], group = gT<uint>(buffer[1..4])
    | Compute: combinedKey = (group << 8) | archive
    |
    | If (int)group < 0:
    |     Binary search PREFETCH queue for combinedKey
    |     Set currentQueue = &prefetchVector
    | Else:
    |     Binary search URGENT queue for combinedKey
    |     Set currentQueue = &urgentVector
    |
    | If NOT FOUND: CLOSE CONNECTION (disconnect)
    |
    | Set currentResponseId = combinedKey
    | Set blockBytesRead = 5
    | Set phase1BytesRead = 0
    | Set phase2BytesRead = 0
    |
    v
State: currentResponseId set, responsePacket == null (need Phase 2)
    |
    | Read up to (5 - phase2BytesRead) bytes into phase2 buffer
    | If phase2BytesRead < 5: return (partial read)
    |
    | Parse: compression = buffer[0], compressedSize = gT<int>(buffer[1..4])
    |
    | VALIDATION:
    |   compression > 3 → CLOSE CONNECTION
    |   compressedSize < 0 (signed) → CLOSE CONNECTION
    |
    | Compute totalPayloadSize:
    |   = compressedSize + 9 + (compression == 0 ? 0 : 4) + extraDataSize
    |   where extraDataSize = request.field_0x14 (from the matched request entry)
    |
    | Allocate Packet of totalPayloadSize bytes
    | Write compression(1) + compressedSize(4) into Packet (5 bytes)
    | Set blockBytesRead += 5 (now = 10 total)
    |
    | If compressedSize == 0:
    |     Invoke FUN_00b73740 (response complete - triggers callback)
    |
    v
State: Reading data payload
    |
    | Compute:
    |   bytesRemaining = packetTotalSize - extraDataSize - bytesWrittenToPacket
    |   blockRemaining = 0x19000 - blockBytesRead
    |   toRead = min(available, blockRemaining, bytesRemaining)
    |
    | Read toRead bytes from socket directly into Packet buffer
    | Update bytesWrittenToPacket += toRead
    | Update blockBytesRead += toRead
    |
    +-- If bytesWrittenToPacket == packetTotalSize - extraDataSize:
    |       RESPONSE COMPLETE
    |       Invoke FUN_00b73740 (callback + cleanup)
    |       Reset: currentResponseId = -1, currentQueue = 0
    |
    +-- If blockBytesRead >= 0x19000 (102,400):
    |       Reset: currentQueue = 0 (forces re-lookup next iteration)
    |       Reset: blockBytesRead = NEGATIVE 0x100000000 (special sentinel!)
    |       ** THIS IS NOT -1 or 0. It's stored as: plVar4[0x17] = -0x100000000 **
    |       On next iteration, this causes the reader to re-enter Phase 1
    |       to read a 5-byte continuation header
    |
    +-- Otherwise: continue reading next tick
```

### Block Boundary Handling Detail

**Critical finding from rs2client (differs from librs2client.so):**

In `ConnectionManager::MainLogic` at `0xa2c9dc`:
```c
if (0x18fff < uVar23) {  // uVar23 = blockBytesRead after this read
    plVar4[0x18] = 0;                     // Reset currentQueue to null
    plVar4[0x17] = -0x100000000;          // Reset blockBytesRead (64-bit sentinel)
}
```

The block reset sets `blockBytesRead` to a value that will cause the next iteration to enter
the `currentResponseId == -1` path (because `currentQueue` is null). This reads a new 5-byte
header (the continuation header). The continuation header is parsed identically to Phase 1:
- archive(1) + group(4)
- Binary search for the matching request
- Resume reading into the same response Packet

In `librs2client.so` the equivalent code was:
```c
if (iVar30 == 0x19000) {
    *(undefined4 *)(plVar33 + 0x1c) = 0;  // blockBytesRead = 0
}
```

The logic is equivalent — both reset the block counter and force a continuation header read.

---

## Answer to Critical Questions

### 1. Does the client expect a VERSION SUFFIX after group response payload?

**NO.** There is no version suffix appended after the container payload for TCP responses.

Evidence from `GroupDownloaded` (`0xa1b200`):
- The function receives `data` which is the response Packet
- It reads `data[1]` as the total data length (this is the Packet's stored size)
- CRC is computed over `data[2]` (the buffer pointer) for `(int)data[1] - 2` bytes
- The `-2` is NOT a version suffix strip — it's part of the CRC computation convention

The container data passed to `GroupDownloaded` is exactly what was read from TCP after the
10-byte response header: `[compression(1)][compressedSize(4)][payload...]`

The "extra data" mentioned in the allocation formula is `request.field_0x14` which is the
**extraDataSize** that was attached to the request when it was enqueued. For group requests,
this comes from the `OnMessage` case 0x1e handler. Looking at that code:

```c
// From OnMessage case 0x1e - RequestGroup
Packet::ResizeBuffer((long *)&local_3e8, lVar14);  // lVar14 = local_138 + 0xb
Packet::pT_int(/*...*/, uVar8);      // CRC (4 bytes)
Packet::pT_uchar(/*...*/, bVar12);   // flags (1 byte)
Packet::pArrayBuffer(/*...*/, local_130, local_138);  // whirlpool hash (0 or 64 bytes)
```

So the "extra data" is the **request metadata** (CRC + flags + optional whirlpool) that the
Js5WorkerThread prepends when enqueuing the request. This metadata is stored in the request
entry's extra packet, NOT appended to the TCP response.

**Bottom line: The server should send ONLY the raw container data (compression + compressedSize +
payload) with NO version suffix. Our `FileProvider.kt` is correct in not appending a version
suffix.**

### 2. How does the client calculate total response size?

From the Phase 2 handler in `ConnectionManager::MainLogic`:
```c
// Allocation size for response Packet
// CRITICAL: the expression (-(uint)(bVar1 == 0) & 0xfffffffc) evaluates to:
//   -4 (0xFFFFFFFC) when compression == 0   (subtracts 4)
//    0             when compression != 0     (adds nothing)
size = compressedSize + 9 + (-(uint)(compression == 0) & 0xfffffffc) + requestType
```

Which simplifies to:
- **Uncompressed** (compression == 0): `compressedSize + 9 - 4 + requestType = compressedSize + 5 + requestType`
- **Compressed** (compression != 0): `compressedSize + 9 + 0 + requestType = compressedSize + 9 + requestType`

This is exactly: `containerSize + requestType`, where:
- `containerSize = compression(1) + compressedSize(4) + payload`
- For uncompressed: `containerSize = 1 + 4 + compressedSize = compressedSize + 5`
- For compressed: `containerSize = 1 + 4 + decompressedSize(4) + compressedSize = compressedSize + 9`

**The `requestType` field** comes from offset 0x14 in the request entry (verified: it's `puVar2[3]`
in `Js5NetQueue::RequestData`, set to 2 for group requests via `OnMessage` case 0x1e, and 0 for
index/master-index requests via cases 0x14 and 0x10).

**TCP bytes read from wire after the 10-byte response header:**

The completion condition is: `bytesWrittenToPacket == packetTotalSize - requestType`

After locally writing 5 bytes (compression + compressedSize):
```
tcpBytesToRead = (containerSize + requestType) - requestType - 5 = containerSize - 5
```

For uncompressed: `tcpBytesToRead = compressedSize`
For compressed: `tcpBytesToRead = compressedSize + 4`

**This matches our `FileProvider.kt` implementation exactly.** Our server sends
`compressedSize + (compression != 0 ? 4 : 0)` bytes of TCP payload, which equals `containerSize - 5`.

**There is NO extra 4-byte suffix.** The earlier analysis suggesting a 4-byte discrepancy was
based on misreading `(-(uint)(bVar1 == 0) & 0xfffffffc)` as `+4` instead of `-4`. In C, when
`bVar1 == 0`: `-(uint)(1) = 0xFFFFFFFF`, and `0xFFFFFFFF & 0xFFFFFFFC = 0xFFFFFFFC = -4` in
32-bit unsigned addition wrapping.

### 2a. What is `requestType` and why does it add 2 bytes for groups?

The `requestType` field at request entry offset 0x14 (uint32) is set to:
- **0** for master index requests (case 0x10) and index requests (case 0x14)
- **2** for group requests (case 0x1e)

For group requests, 2 extra bytes are allocated in the Packet beyond the container data.
These bytes are **never written from TCP** — they remain as uninitialized heap memory.
Their purpose is to provide buffer space: the `GroupDownloaded` function computes CRC
over `data[1] - 2` bytes, where `data[1]` = `containerSize + 2`. This gives CRC over
exactly `containerSize` bytes — the full raw container.

For index requests (requestType=0), `IndexDownloaded` computes CRC over `data[1]` bytes
= `containerSize + 0` = the full raw container.

**Both CRC checks compute over the identical scope: the complete raw container data.**

### 2b. Request-sending Packet capacity

The Js5NetQueue's request-sending Packet is allocated with **10 bytes** capacity
(`Packet::ResizeBuffer(plVar6 + 0xb, 10)` in `CreateQueues`). The client writes 6 bytes
of meaningful data (`flags(1) + archive(1) + group(4)`) and sends all 10 bytes via
`ClientStream::Write`. The last 4 bytes are buffer garbage/zeros.

Our server correctly reads 10 bytes per request: `opcode(1) + index(1) + group(4) + padding(4)`.

### 2c. Summary: NO version suffix in JS5 TCP responses

The Jagex SQLite cache stores containers as exactly:
`compression(1) + compressedSize(4) + [decompressedSize(4) if compressed] + compressedData(compressedSize)`

Verified: js5-2.jcache key 1 has DATA length 4166, compression=0x02 (GZIP),
compressedSize=0x103D (4157). Expected: 1+4+4+4157 = 4166. Matches exactly.

The JS5 TCP server sends exactly this container (minus the 5-byte header which is split
into the response header). No version suffix, no padding, no extra bytes.

### 3. How does the client handle the "hash" field?

The "hash" field in Phase 1 is the raw `group` int32 read from the wire. The client uses it as:

```c
// From ConnectionManager::MainLogic Phase 1 parsing:
bVar1 = *(byte *)plVar4[0x11];           // archive = first byte
uVar23 = Packet::gT_uint(plVar4 + 0xf);  // group = next 4 bytes (unsigned int)
combinedKey = uVar23 << 8 | (uint)bVar1;  // lookup key

if ((int)uVar23 < 0) {
    // group has top bit set → search PREFETCH queue
    FUN_009ce840(plVar4 + 4, &combinedKey);
} else {
    // group is positive → search URGENT queue
    FUN_009ce840(plVar4, &combinedKey);
}
```

**Key detail:** The combined key shifts `group` left by 8 and ORs with archive. Since the
prefetch flag is in bit 31 of group, after `group << 8` it would be at bit 39 — which is
**discarded** in 32-bit arithmetic. So the lookup key is the same regardless of the prefetch flag.

The prefetch flag (bit 31 of group) is used **ONLY** to select which queue to search:
- `(int)group < 0` → prefetch queue
- `(int)group >= 0` → urgent queue

**Our server implementation is correct:** `hash = if (prefetch) archive or (1 shl 31) else archive`

### 4. After reading the complete response, what CRC does the client compute and over what data?

#### For Index responses (`IndexDownloaded` at `0xa1ab00`):

```c
in_RCX[3] = 0;  // Reset packet position to 0
uVar6 = Packet::gT_uint(in_RCX);  // Read 4-byte CRC from request metadata packet

// CRC32 computed over the ENTIRE container data: data[2] for data[1] bytes
// data[1] = packet length, data[2] = packet buffer pointer
pbVar8 = (byte *)data[2];
uVar3 = data[1];  // Full length, NO subtraction

crc = CRC32(pbVar8, uVar3);
if (uVar6 != crc) { FailGetIndexResponse(); return; }
```

**Index CRC is computed over the full container data with NO bytes subtracted.**

The CRC table is at `DAT_010fe0c0` — standard CRC32 (polynomial 0xEDB88320, init 0xFFFFFFFF,
final XOR 0xFFFFFFFF).

#### For Group responses (`GroupDownloaded` at `0xa1b200`):

```c
in_R8[3] = 0;
Packet::gT_uint(in_R8);  // Skip first 4 bytes (format version)
lVar9 = in_R8[3];
in_R8[3] = lVar9 + 1;   // Skip 1 byte (unknown flag byte)
in_R8[3] = lVar9 + 2;   // +2 total = skip 6 bytes from start of metadata
uVar5 = Packet::gT_uint(in_R8);  // Read 4-byte expected CRC

pbVar11 = (byte *)data[2];
uVar10 = (int)data[1] - 2;  // *** Length MINUS 2 ***

crc = CRC32(pbVar11, uVar10);
if (uVar5 != crc) { FailGetGroupResponse(); return; }
```

**Group CRC is computed over `data[1] - 2` bytes.**

`data[1]` here is the Packet's **allocation size** = `containerSize + requestType` =
`containerSize + 2` for group requests. So `data[1] - 2 = containerSize` = the full raw
container.

**CORRECTED (2026-03-11):** The `-2` does NOT mean there is a 2-byte version suffix at the
end of the container data. The 2 extra bytes are from the `requestType` field (always 2 for
groups) being added to the Packet allocation. These extra bytes are uninitialized heap memory
and are NOT read from TCP.

For index requests (requestType=0), `data[1] = containerSize + 0`, and CRC is over `data[1]`
bytes = the full container. No subtraction needed.

**Both index and group CRC checks compute over exactly the same scope: the complete raw
container data** (`compression(1) + compressedSize(4) + payload`).

The `GroupDownloadedCallback` wrapper at `0x299e90` also checks:
```c
if ((ulong)data[1] < 3) return;  // Minimum 3 bytes required
```

### 5. How does block framing work — per-response or globally?

**Block framing is per-connection, NOT per-response.** There is a single block byte counter
(`plVar4[0x17]`) in the Js5NetQueue object that tracks bytes across all responses on that
TCP connection.

However, because the client reads responses **serially** (one at a time — it finishes one
response completely before starting the next), the block counter effectively operates
per-response in practice. At response completion, the counter is reset to -1/0 via
`FUN_00b73740`:
```c
*(undefined4 *)(param_1 + 0xbc) = 0xffffffff;  // currentResponseId = -1
*(undefined8 *)(param_1 + 0xc0) = 0;            // currentQueue = null
```

And the block counter is implicitly reset because the next response starts from the Phase 1
state where `blockBytesRead` is set to 5 after reading the first header.

**Our server's per-response block tracking is correct** — since responses are serial, each
response can independently track its own block offsets.

---

## Continuation Header Verification

At block boundaries, the client reads a 5-byte continuation header with the same Phase 1 format.
It then does a full binary search lookup in the queue to verify the response matches an active
request. If the lookup fails, the connection is closed.

This means the continuation headers MUST contain the same `archive` and `group` values
(including the prefetch flag) as the original response header.

**Our server's continuation header implementation is correct** — it uses the same `index` and
`hash` values as the original response header.

---

## Server Implementation Status (CORRECTED 2026-03-11)

Based on detailed Ghidra analysis, our server's JS5 TCP response implementation is **correct**:

### 1. No Version Suffix Required

**CORRECTED:** There is no 2-byte version suffix appended to JS5 TCP responses. The earlier
analysis claiming `data[1] - 2` implied a version suffix was wrong. The `-2` compensates for
the `requestType` field (always 2 for group requests) which adds 2 bytes to the Packet
allocation. The CRC for both index and group responses is computed over the exact raw container
data, with no bytes excluded.

Our SQLite cache stores containers without any suffix. Verified: js5-2.jcache key 1 has
DATA length exactly matching `1 + 4 + 4 + compressedSize` with no extra bytes.

### 2. Total Payload Size Matches

The server calculates:
```kotlin
val payloadSize = compressedSize + if (compression != 0) 4 else 0
```

This matches the client's TCP read expectation exactly:
- Compressed: `compressedSize + 4` bytes (decompressedSize + compressedData)
- Uncompressed: `compressedSize` bytes (rawData)

The client's Packet allocation includes `requestType` (2 for groups, 0 for indices) extra
bytes, but these are never filled from TCP. They are allocated buffer headroom only.

### 3. CRC Computation is Consistent

Both the server's `VersionTableBuilder` and the client compute standard CRC32 (polynomial
`0xEDB88320`, init `0xFFFFFFFF`, final XOR) over the complete raw container bytes. The CRCs
stored in the master index match the data served for each index.

### 4. Connection Timeout

The client closes the JS5 connection if no data is received for 30 seconds:
```c
if (uVar16 < 0x7531) goto process;  // Skip timeout if time < 30001ms
if (uVar16 - 30000 <= (ulong)plVar4[10]) goto process;  // Skip if data within 30s
// Otherwise: close connection
```

Our server should ensure responses are sent promptly.

---

## Summary of Verified Wire Format

```
Response for archive A, group G (urgent):

Bytes on wire:
[A(1)][G(4)][compression(1)][compressedSize(4)][payload...]

Where payload = compressedSize + (compression != 0 ? 4 : 0) bytes
  For compression != 0: [decompressedSize(4)][compressedData(compressedSize)]
  For compression == 0: [rawData(compressedSize)]

Block framing:
  After every 102,400 bytes (counted from start of response):
    Insert [A(1)][G(4)] continuation header (5 bytes)
    The 5-byte continuation counts toward the NEXT block

For prefetch: G has bit 31 set → G | 0x80000000

NO version suffix. NO extra bytes. The server sends exactly the raw container
data (minus the 5-byte header that's split into the response header fields).
```

## Request Wire Format (from `FUN_009d6b90` + `CreateQueues`)

The client's request-sending Packet is allocated with **10 bytes** capacity by
`Packet::ResizeBuffer(plVar6 + 0xb, 10)` in `CreateQueues`. The actual data written is:

```
[flags(1)][archive(1)][group(4)] = 6 bytes of meaningful data
```

But `ClientStream::Write` sends the full 10-byte capacity. The last 4 bytes are
uninitialized buffer (typically zeros). Our server reads all 10 bytes:

```
[opcode(1)][index(1)][group(4)][padding(4)] = 10 bytes
```

This is correct.

## Key Field: `requestType` (request entry offset 0x14)

| Request Type | `requestType` Value | Packet Headroom | CRC Formula |
|-------------|--------------------|-----------------|----|
| Master index (255/255) | 0 | 0 extra bytes | `CRC32(data[2], data[1])` — full container |
| Index (255/N) | 0 | 0 extra bytes | `CRC32(data[2], data[1])` — full container |
| Group (N/M) | 2 | 2 extra bytes | `CRC32(data[2], data[1] - 2)` — still full container |
