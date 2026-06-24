# JS5 HTTP Group Download: Response Format & Processing

> **SUPERSEDED for world-entry analysis (2026-06-23).** The rev-946 content below
> (CRC / 2-byte version suffix) is retained as history. The authoritative analysis
> for the macOS world-entry loop (index 40 / `AUDIO_STREAMS` group 38557) is the new
> **[Section 11: 948-5 — httpMode / flag1 / MemoryCache control flow & the index-40
> world-entry loop](#11-948-5--httpmode--flag1--memorycache-control-flow--the-index-40-world-entry-loop)**.
> All offsets/addresses in Section 11 are from `rs2client.948-5` (base `0x0`), the only
> source of truth.

Reverse-engineered from the NXT client binary (`rs2client` rev 946) via Ghidra MCP.

**Binary:** rs2client (port 8080) -- authoritative for rev 946

**Key Functions Analyzed:**
- `jag::Js5WorkerThread::GroupDownloaded` @ `0x00a1b200` -- CRC validation + response packaging
- `jag::Js5WorkerThread::OnMessage` @ `0x00a20970` -- Worker thread message handler (case 0x1e dispatches to HTTPQueue or NetQueue)
- `jag::Js5ResourceProvider::WorkerOnMessage` @ `0x00a22dc0` -- Main thread handler (case 0x1e receives group data)
- `jag::Js5HTTPQueue::RequestData` @ `0x00a20100` -- Enqueues HTTP download request
- `jag::Js5WorkerThread::CreateQueues` @ `0x00a1d3b0` -- Creates NetQueue + HTTPQueue
- `jag::Js5NetQueue::GroupDownloadedCallback` @ `0x00299e90` -- Callback wrapper (used by BOTH TCP and HTTP)
- `jag::Js5ResourceProvider::GetFile` @ `0x009889b0` -- httpMode path: decompress + provide to MemoryCache
- `jag::Js5ResourceProvider::GetFile_ArchiveGroup` @ `0x00988e80` -- Dispatcher: httpMode vs diskCache
- `jag::Js5ResourceProvider::RequestGroupFromServer` @ `0x009b3070` -- Request scheduling (also checks httpMode)
- `jag::Js5MemoryCache::ProvideGroup` @ `0x00a1c760` -- Stores data in LRU memory cache
- `jag::Js5MemoryCache::LookupGroup` @ `0x00a1c470` -- Hash table lookup (crash site)

---

## 1. Executive Summary: The Bug

**Root cause: The HTTP response body must include a 2-byte version suffix after the container data.**

The client's `GroupDownloaded` function computes CRC32 over the response body **minus the last 2 bytes**. Those last 2 bytes are a **little-endian version suffix** that Jagex's HTTP server appends. Our server sends raw container bytes without this suffix, causing:

1. CRC computation reads past the actual data (or computes over wrong range)
2. CRC mismatch -> `FailGetGroupResponse` is called
3. The group download is marked as failed
4. On retry with certain conditions, corrupt data may reach `MemoryCache::ProvideGroup`
5. Corrupted hash table entries in MemoryCache lead to SIGSEGV in `LookupGroup`

**Fix: Append a 2-byte version suffix to each HTTP response body.**

---

## 2. HTTP Response Body Format (What The Client Expects)

The HTTP URL is: `http://{host}:{port}/ms?m=0&a={archive}&g={group}&c={crc}&v={version}`

The expected response body is:

```
Offset  Size  Field              Description
------  ----  -----              -----------
0       1     compression        Compression type (0=none, 1=bzip2, 2=gzip, 3=lzma)
1       4     compressedSize     Compressed payload size (big-endian)
5       4     decompressedSize   Only present if compression != 0
5/9     N     compressedPayload  The compressed (or raw) data
5+N/9+N 2     version            Version suffix (big-endian uint16)
```

**Total response size = container_data_bytes + 2**

The version suffix is the **group version** from the archive index, truncated to 16 bits (unsigned short). This is the same version that appears in the `v=` query parameter.

### How CRC Is Computed

From `GroupDownloaded` @ `0x00a1b200`:

```c
// data[1] = total HTTP response body length
// data[2] = pointer to response body bytes
uVar10 = (int)data[1] - 2;        // <-- SUBTRACT 2 for the version suffix
lVar9 = (long)(int)uVar10;

if (lVar9 == 0) {
    uVar6 = 0;
} else {
    // Standard CRC32 over (responseBody[0..length-3])
    // i.e., everything EXCEPT the last 2 bytes
    uVar6 = CRC32(data[2], uVar10);  // init=0xFFFFFFFF, final=~crc
}

// Compare against expected CRC from the request metadata
if (uVar5 != uVar6) {
    FailGetGroupResponse(this, archive, group);
    return;
}
```

**The CRC is computed over the container bytes WITHOUT the 2-byte version suffix.** This matches how CRCs are stored in the archive index -- they cover `compression(1) + compressedSize(4) + [decompressedSize(4)] + compressedPayload(N)`.

### CRC32 Algorithm

- **Polynomial:** 0xEDB88320 (reflected, standard CRC-32)
- **Init value:** 0xFFFFFFFF
- **Final XOR:** `~crc` (bitwise NOT)
- **Lookup table:** At address `0x010fe0c0` (256 entries)
- **Equivalent Java:** `java.util.zip.CRC32` -- `update(bytes, 0, length-2)` then `(int)getValue()`

---

## 3. GroupDownloaded Processing After CRC Check

After CRC validation passes, `GroupDownloaded` reads a flags byte from the request metadata:

```c
bVar2 = readByte(requestMetadata);  // flags
bVar12 = bVar2 & 1;                // bit 0: has decompressed data
// bit 1: has whirlpool/encryption
```

### Path A: Plain Group (flags & 2 == 0, flags & 1 == 0)

This is the typical HTTP download path for groups without encryption:

```c
// Build response message to main thread
responseSize = data[1] + 0xC;   // HTTP response body + 12 bytes overhead
allocatePacket(&response, responseSize);

Packet::pT_uchar(response, 0x1E);          // opcode: group downloaded
Packet::pT_uchar(response, archive);       // archive ID
Packet::pT_int(response, group);           // group ID
Packet::pT_uchar(response, 0);            // flag1 = 0 (no decompressed data inline)
Packet::pT_uchar(response, 0);            // flag2 = 0 (success)
Packet::pT_int(response, data[1]);         // full response body size (INCLUDING version suffix)
Packet::pArrayBuffer(response, data[2], data[1]);  // full response body bytes
Task::Response(this, response);
```

**Key insight:** The FULL response body (including the 2-byte version suffix) is passed to the main thread. The main thread later strips or ignores the version suffix when storing to DiskCache/MemoryCache.

### Path B: Encrypted Group (flags & 2 != 0)

For encrypted groups, additional whirlpool validation is performed against a 64-byte hash in the request metadata. This path is more complex but follows the same basic structure.

---

## 4. Main Thread: WorkerOnMessage Case 0x1E

When the main thread receives the group downloaded message:

```c
case 0x1E:
    archiveId = readByte(packet);
    groupId = readUint(packet);
    flag1 = readByte(packet);      // cVar2: 0=raw container, 1=has decompressed data, 2=encrypted
    flag2 = readByte(packet);      // cVar3: 0=success, non-zero=failure

    if (flag2 == 0):  // success
        dataSize = readUint(packet);
        AllocFromPool(&containerPacket, &dataSize);
        readArrayBuffer(packet, containerPacket, dataSize);

        // Look up CRC and version from the loaded archive index
        crc = archiveIndex[archiveId].crcs[groupId];
        version = archiveIndex[archiveId].versions[groupId];

        // Write to DiskCache (if enabled)
        if (diskCacheEnabled && diskCache != NULL):
            Js5DiskCache::ProvideGroup(diskCache, archiveId, groupId,
                &containerPacket, crc, version, writeMode);

        // If flag1 == 1: decompressed data follows inline
        if (flag1 == 1):
            decompressedSize = readUint(packet);
            AllocFromPool(&decompressedPacket, &decompressedSize);
            readArrayBuffer(packet, decompressedPacket, decompressedSize);
            MemoryCache::ProvideGroup(memoryCache, archiveId, groupId,
                version, crc, &decompressedPacket);
```

**DiskCache receives the raw container data (with version suffix).** MemoryCache receives decompressed data (if flag1==1) or nothing (flag1==0, in which case it's loaded from disk on demand).

---

## 5. The httpMode Flag (offset 0x70, this[0x0E])

### What It Does

When `httpMode` is true (non-zero), the `GetFile_ArchiveGroup` function takes a completely different path:

```c
GetFile_ArchiveGroup(this, archiveId, groupId, priority):
    if (this->httpMode):           // this[0x0E], offset 0x70
        GetFile(this, archiveId, groupId, priority);
        return 0;

    if (this->diskCacheEnabled):   // this[0x1EC5], offset 0xF628
        return RequestGroupFromDisk(this, archiveSettings, groupId, priority, urgent);

    return 0;  // dead end
```

In httpMode, `GetFile()` directly:
1. Reads from the loaded archive index to find the group entry
2. Calls `Js5Compression::Decompress` on the cached container data
3. Handles multi-version splits if needed
4. Creates a decompressed data packet
5. Calls `MemoryCache::ProvideGroup` directly with the decompressed data

**httpMode is a fast path** where container data is already available (downloaded via HTTP and stored in DiskCache), and `GetFile` decompresses it in-place and provides it to the MemoryCache.

### How It Is Set

`httpMode` is set based on the `ModeWhere` parameter (`param=25` in jav_config.ws) and the `iVar27` check in `WorkerOnMessage` case 10. In the case 10 handler:

```c
if ((iVar27 != 0) || (*(char *)(this + 0x70) != '\0')) {
    // Set disk cache flags to enable HTTP-aware mode
    *(puVar19 + 0x2209) = 1;     // disk cache HTTP mode
    *(puVar19 + 0x2207) = 0;     // disable auto-write
    *((long)puVar19 + 0x111e9) = 0;
}
```

The `this + 0x70` byte is read but not written here. It must be set by the Js5ResourceProvider constructor based on configuration parameters.

### RequestGroupFromServer Also Checks httpMode

```c
RequestGroupFromServer(this, archiveSettings, groupId, priority, urgent):
    if (this->httpMode):
        GetFile(this, archiveId, groupId, priority);
        return;
    // ... normal TCP request path ...
```

When httpMode is true, `RequestGroupFromServer` short-circuits to `GetFile` instead of queueing a TCP request.

---

## 6. The Two Group Download Paths

### Path 1: TCP (NetQueue) -- Archive Indices + Groups via JS5 protocol

- Requests go through `Js5NetQueue::RequestData` on the worker thread
- Response arrives as framed JS5 TCP data (512-byte blocks with 0xFF separators)
- Worker thread strips framing, calls `GroupDownloadedCallback`
- `GroupDownloaded` validates CRC, builds response message, posts to main thread

### Path 2: HTTP (HTTPQueue) -- Groups via HTTP GET

- Requests go through `Js5HTTPQueue::RequestData` on the worker thread
- HTTP GET to `http://{host}:{port}/ms?m=0&a={archive}&g={group}&c={crc}&v={version}`
- Response body is the raw container + 2-byte version suffix
- Same `GroupDownloadedCallback` is called with the HTTP response body
- Same `GroupDownloaded` validation + packaging to main thread

**Both paths converge at `GroupDownloadedCallback` -> `GroupDownloaded`**, which is why the CRC validation and version suffix handling is identical.

### How The Client Decides TCP vs HTTP

In `Js5WorkerThread::OnMessage` case 0x1E (group request from main thread):

```c
if (flag_useHTTP == 0):
    // Use TCP (NetQueue)
    Js5NetQueue::RequestData(netQueue, archiveId, groupId, ...)
else:
    // Use HTTP (HTTPQueue)
    // But only if HTTPQueue has capacity (< 20 concurrent requests)
    if (activeHTTPRequests + pendingHTTPRequests < 20):
        Js5HTTPQueue::RequestData(httpQueue, archiveId, groupId, version, crc, ...)
```

The `flag_useHTTP` comes from the request metadata and determines per-group whether TCP or HTTP is used.

---

## 7. Js5MemoryCache::LookupGroup -- The Crash Site

The crash at `LookupGroup` @ `0x00a1c470` occurs because:

```c
LookupGroup(this, archiveId, groupId):
    key = groupId << 8 | archiveId;
    bucketIndex = key % (this->bucketCount & 0xFFFFFFFF);

    for (entry = hashTable[bucketIndex]; entry != NULL; entry = entry[8]):
        if (key == entry[0]):
            // Found! Update LRU chain...
            // CRASH: entry[6] or entry[7] contains garbage pointer
            uVar2 = entry[6];          // LRU prev
            uVar3 = *(this + 0x50108); // LRU sentinel
            *(uVar2 + 0x30) = entry[7];  // SIGSEGV here if uVar2 is garbage
```

The hash table entry at `entry[6]` (LRU prev pointer) contains an invalid address. This happens when `ProvideGroup` inserted an entry with corrupt pointers, which traces back to corrupt data reaching the MemoryCache due to CRC validation failures on improperly formatted HTTP responses.

---

## 8. Required Server Fix

### Current (BROKEN) HTTP Response

```
[compression(1)] [compressedSize(4)] [payload(N)]
```

Total: 5 + N bytes (raw container only)

### Correct HTTP Response

```
[compression(1)] [compressedSize(4)] [payload(N)] [version(2)]
```

Total: 5 + N + 2 bytes = container + 2-byte version suffix

### Version Suffix Details

- **Size:** 2 bytes
- **Encoding:** Big-endian unsigned short (matching `Packet::gT_ushort`)
- **Value:** The group version from the archive index, masked to 16 bits (`version & 0xFFFF`)
- **Source:** The `v=` parameter in the HTTP URL contains this value; it should match

### CRC Validation

The CRC in the `c=` query parameter and in the archive index covers the container data **without** the version suffix:
- CRC32 of `compression(1) + compressedSize(4) + [decompressedSize(4) if compressed] + compressedPayload(N)`
- This is the standard container CRC that Jagex stores in the archive index

The server must:
1. Read raw container from cache: `compression(1) + compressedSize(4) + payload(N)`
2. Append 2-byte version suffix: `(version >> 8) & 0xFF, version & 0xFF`
3. Serve the combined bytes as the HTTP response body

---

## 9. Implementation Checklist

- [ ] Modify `ConfigServer.serveJs5Http()` to append 2-byte version suffix to HTTP responses
- [ ] Get the group version from the archive index (need `FileProvider` to expose version data)
- [ ] Alternatively, use the `v=` query parameter value from the client's request URL
- [ ] Verify CRC matches: `CRC32(containerBytes) == c_parameter`
- [ ] Total response = `containerBytes + [version >> 8, version & 0xFF]`

### Using the `v=` Parameter as Version Source

The simplest fix: the client passes `v={version}` in the HTTP URL. The server can read this and append it:

```kotlin
val version = call.request.queryParameters["v"]?.toIntOrNull() ?: 0
val versionSuffix = byteArrayOf(
    ((version shr 8) and 0xFF).toByte(),
    (version and 0xFF).toByte()
)
call.respondBytes(data + versionSuffix, ContentType.Application.OctetStream)
```

---

## 10. TCP vs HTTP: Key Differences in Data Format

| Aspect | TCP (JS5 Protocol) | HTTP (/ms endpoint) |
|--------|-------------------|---------------------|
| Framing | 10-byte header + 102KB blocks + continuation headers | None (raw HTTP body) |
| Version suffix | 2-byte version appended by Jagex server | 2-byte version appended by Jagex server |
| CRC scope | Container bytes only (excl. version suffix) | Container bytes only (excl. version suffix) |
| Processing | `GroupDownloaded` strips framing first | `GroupDownloaded` receives raw HTTP body |
| Callback | `GroupDownloadedCallback` | `GroupDownloadedCallback` (same!) |

Both paths expect the version suffix. The TCP path gets it from the JS5 server's response framing. The HTTP path gets it directly in the response body.

---

# 11. 948-5 — httpMode / flag1 / MemoryCache control flow & the index-40 world-entry loop

**Dated:** 2026-06-23. **Binary:** `rs2client.948-5`, base `0x0` — the only source of truth. Every
address below is verified by decompilation/disassembly of 948-5 unless marked `INFERRED`. This section
supersedes the rev-946 material above for the world-entry analysis; the bug here is **structural
(cache-population control flow)**, not the rev-946 CRC/version-suffix issue (which is fixed — the
served bytes already pass CRC).

---

> ## ⛔ 11.-1 PINPOINT CORRECTION (2026-06-23, second pass) — READ FIRST
>
> A focused RE pass aimed at pinning the EXACT `provider+0x70` / `iVar27` source **disproved the
> central INFERRED hypothesis of §11.0–§11.5**. The findings below are **VERIFIED by decompilation +
> disassembly of 948-5**; where §11.0–§11.7 conflict with this box, **this box wins.**
>
> **1. `provider+0x70` is a HARDCODED CONSTANT `1`, not a jav_config-derived flag.**
> The provider (`Js5ResourceProvider`, `operator_new(0x4f70)`) is built in
> `jag::ConnectionManager::RecreateJs5ResourceProvider @0x0009b258` (renamed; was `FUN_0009b258`,
> called once per connection from `ConnectionManager::MainLogic @0x004b3e82`). It writes
> `MOV byte ptr [R15+0x70], 1` **unconditionally** at `0x0009b481`. `R15` is the `operator_new(0x4f70)`
> result (alloc `@0x0009b3e8`) and is **never reassigned** before the store to `client+0x18cd0`
> `@0x0009e70e` (verified: zero `MOV R15,*` between the two). **No URL/host/port/scheme string is read,
> compared, or parsed anywhere in that function** (verified by full decompile sweep: no `http`,
> `localhost`, `loopback`, `scheme`, `strncmp`, no port `443`). → `provider+0x70` is **identical (=1)
> for retail and for a private server**, so it is **NOT** the world-entry-loop differentiator. The
> jav_config-diff premise (param 35/37/40/49/25 set `+0x70`) is **false**.
>
> **2. `iVar27` in `WorkerOnMessage` case 10 is the launcher INSTANCE NUMBER, not a URL property.**
> `iVar27 = DAT_01393568` = number of held byte-locks on `launcher/instance.lock`
> (`FUN_008333e0` = `fcntl(F_GETLK)` loop; forced 0 when `DAT_015a6791 != 0`). For a single client
> it is `0`. So the case-10 disk-auto-write kill `if ((iVar27 != 0) || (provider+0x70 != 0))` is driven
> **only** by `provider+0x70` — which is hardcoded `1` — so the auto-write-OFF branch **fires
> identically for retail and private server.** `iVar27` is **NOT** the differentiator either.
>
> **3. THE ACTUAL TRIGGER — HTTP response `Content-Type` must be EXACTLY `application/octet-stream`.**
> In the HTTP-queue drain of `ConnectionManager::MainLogic @0x008f8250` (the compare is at
> `0x008f8e03`, commented in the Ghidra DB), an HTTP-2xx group response is accepted **only** if its
> `Content-Type` header (parsed into the request object at `request+0xb8`) matches the literal
> `"application/octet-stream"` (string `@0x00ffc943`) via `eastl_string_compare_cstr @0x0013ef80` —
> which is `eastl::basic_string::compare(const char*)`, an **EXACT full-length** comparison (returns 0
> only when lengths AND bytes are equal). On exact match → success cb (`GroupDownloadedCallback` →
> `GroupDownloaded @0x008f0a50` → CRC → `WorkerOnMessage` case `0x1e` → DiskCache/MemoryCache). On **any**
> mismatch → sets request `+0xa9 = 1` **FAILURE** → success cb is **skipped** → the group never reaches
> `GroupDownloaded`, never reaches `Js5MemoryCache` → `FileReady_WithCheck` `LookupGroup` keeps missing →
> **"Verifying Cache" loops forever.** This is exactly the index-40 / group-38557 symptom.
>
> **THE FIX (server-side, init-safe, no TLS required): make the `/ms` HTTP endpoint return the response
> header `Content-Type: application/octet-stream` with NOTHING appended** — no `; charset=...`, no
> trailing whitespace, no alternate casing, no other MIME type. Keep the content-server advertised in
> `jav_config` as-is (`http://localhost:8829`, host `localhost`, etc. are all fine — the scheme/host/port
> are **not** checked by this path; libcurl handles `http://` and `https://` identically here, and there
> is **no** literal `"https"`/`"localhost"`/`:443` check). **HTTPS is NOT required.**
>
> See §11.8 for the full verified chain.

## 11.0 TL;DR — the chosen fix lever

**Drop the HTTP content-server advertisement from `jav_config` (params 35 / 37 / 40 / 49, and the
httpMode param 25).** This makes the client register cache archives in **disk-backed/TCP mode** instead
of HTTP-streamed mode, which restores the **DiskCache read-back → `Js5MemoryCache`** round-trip that
satisfies the "Verifying Cache" gate for index 40 (and every other archive). This is the only lever
that is reachable from the server/config side.

The `flag1` lever is **NOT reachable from the server**: `flag1` is a pure client-side classification
(`flag1 = (archiveStorageType == 6)`) computed from the in-memory archive config, never from served
wire bytes and never from the master index. So it is the TCP-routing fix or nothing.

**Important correction to the prior pass:** index 40 (flag1=0) does **not actually need flag1=1**.
Retail serves flag1=0 archives through a **DiskCache read-back** path that the prior pass missed
(`HandleDownloadResult @0x00962960` → `Js5MemoryCache::ProvideGroup`, which has **no flag1 gate**). The
loop happens because HTTP-content-server mode **bypasses that DiskCache round-trip**, not because flag1
is 0.

## 11.1 Function map (948-5, renamed/commented in the Ghidra DB)

| Address | Name | Role |
|---|---|---|
| `0x008f8250` | `jag::ConnectionManager::MainLogic` | Worker-thread per-tick driver. Runs **two independent queues every tick**: the TCP `Js5NetQueue` (state machine `[worker+0x2a4]`) and the `Js5HTTPQueue` (drain at `LAB_008f8326`). |
| `0x008f2e30` | `jag::Js5WorkerThread::CreateQueues` | Creates **both** queues: NetQueue `[worker+0x1b0]`, HTTPQueue `[worker+0x1b8]`. Resets `[worker+0x2a4]=0`. |
| `0x008f6360` | `jag::Js5WorkerThread::OnMessage` | Worker message dispatcher (jump table `@0x00ffc878`). op `0x1e` = group request. |
| `0x008b4360` | `jag::Js5NetQueue::RequestData` | Enqueue a **TCP** group/index request. |
| `0x008f5af0` | `jag::Js5HTTPQueue::RequestData` | Enqueue an **HTTP** group request (sorted list `[httpQueue+0x18]`, cap 20). |
| `0x008f7560` | `BuildHTTPRequestURL` | Builds `%s/ms?m=0&a=%u&k=%d&g=%u&c=%d&v=%d` (`@0x00ffc920`) → libcurl GET. |
| `0x001fc730` | `jag::Js5NetQueue::GroupDownloadedCallback` | Shared success cb for **both** TCP and HTTP completed downloads → `GroupDownloaded`. |
| `0x008f0a50` | `jag::Js5WorkerThread::GroupDownloaded` | CRC32 check; posts op `0x1e` msg to main thread. Sets **message-flag1** from `descriptor[+0x11] & 1`. |
| `0x0098dee0` | `jag::Js5ResourceProvider::WorkerOnMessage` | Main-thread handler. case `0x1e` = group downloaded. |
| `0x009751e0` | `jag::Js5ResourceProvider::RebuildArchiveDescriptors_flag1FromType` | Builds runtime per-archive descriptors. **Sets `flag1` = `desc[+0x11] = (srcType@+0x30 == 6)`**. |
| `0x009642c0` | `jag::Js5ResourceProvider::RequestGroupFromServer` | Reads `descriptor[+0x11]` (`@0x0096496c`), copies to request `+0x34`. In synchronous-httpMode (`provider+0x70 != 0`) short-circuits to `GetFile`. |
| `0x008f76e0` | `jag::Js5ResourceProvider::RequestGroupFromDisk` | Enqueues a `Js5DiskCache` read (gated on `provider+0x90 != 0`). |
| `0x00963740` | `jag::Js5ResourceProvider::GetFile` | Synchronous-httpMode consumer: reads `provider+0x80` container store; if empty → provides **empty** group to MemoryCache. |
| `0x00963c10` | `jag::Js5ResourceProvider::GetFile_ArchiveGroup` | If `provider+0x70 != 0` → `GetFile` (returns 0). Else if disk enabled → `RequestGroupFromDisk`. |
| `0x00962960` | `jag::Js5ResourceProvider::HandleDownloadResult_DiskRead_to_MemoryCache` | **Disk-read completion** → `Js5MemoryCache::ProvideGroup` (flag1-INDEPENDENT). The retail mechanism for flag1=0 archives. |
| `0x008f27d0` | `jag::Js5ResourceProvider::ProcessDownloadResults` | Main-thread drain of disk-cache read results → `HandleDownloadResult`. |
| `0x00965880` | `jag::Js5ResourceProvider::FileReady_WithCheck` | The "Verifying Cache" gate / re-request loop. `LookupGroup(provider+0x88 = MemoryCache)`; exits only when the group's decompressed data is in MemoryCache. |
| `0x008f1fb0` | `jag::Js5MemoryCache::ProvideGroup` | Populate LRU memory cache. **Only 3 callers**: `WorkerOnMessage`@0x98fcf9 (flag1==1), `GetFile`@0x963836 (sync-httpMode), `HandleDownloadResult`@0x962ac1 (disk read). |
| `0x008f2580` | `jag::Js5DiskCache::ProvideGroup` | Queue raw container to SQLite write queue. param_7 = flag1. Does **not** touch MemoryCache. |
| `0x008b5a10` | `jag::Js5DiskCache::EnqueueRequest` | Queue a disk read/write (param_5 = flag1). |

Key provider object offsets (948-5): `+0x70` = synchronous-httpMode byte; `+0x80` = raw-container store
(read by `GetFile`); `+0x88` = `Js5MemoryCache`; `+0x90` = `Js5DiskCache`; `+0x48` = registered-archive
list (entries carry `flag1` at `+0x11`); `+0x26e0 + archive*0x108` = per-archive `Js5Index`.

## 11.2 The re-request loop and its exit condition (VERIFIED)

`FileReady_WithCheck @0x00965880`:

```c
lookup = Js5MemoryCache::LookupGroup(provider[0x11] /* = provider+0x88, the MemoryCache */, archive, group, idx);
if (lookup->data /* +8 */ == 0 || !DoesFileExist(idx, group)) {     // group NOT in memory cache
    GetFile_ArchiveGroup(provider, req, group, prio, 1);            // try to provide it
    if (returned 0) RequestGroupFromServer(provider, req, group, prio, urgent);
    return DAT_015df0b4;                                            // STILL WAITING -> re-poll
}
```

The gate is **the memory cache** (`provider+0x88`). The loop ends **only** when the group's
*decompressed* data is present there (`lookup->data != 0`). Nothing else satisfies it. This is the
"Verifying Cache" wait.

## 11.3 The three (and only three) ways a group reaches `Js5MemoryCache` (VERIFIED)

`xrefs_to Js5MemoryCache::ProvideGroup @0x008f1fb0` →

1. **`WorkerOnMessage` case 0x1e @0x0098fcf9** — fires **only when the op-0x1e message's flag1 byte (`cVar2`) == 1**. That byte was set by `GroupDownloaded` from `descriptor[+0x11] & 1`. For flag1=0 archives this branch is skipped; only `Js5DiskCache::ProvideGroup` runs (raw container → SQLite write queue).
2. **`GetFile` @0x00963836** — the synchronous-httpMode consumer. Reads the **`provider+0x80`** raw-container store. If `entry->data (+8) == 0` (nothing populated that store) it provides an **empty 0-byte** group → `LookupGroup` returns an entry whose `data` is 0 → loop does **not** exit.
3. **`HandleDownloadResult` @0x00962ac1** — **disk-cache read completion**, **flag1-INDEPENDENT**. For a group read (`param_3 != 0xffffffff`, success) it calls `Js5MemoryCache::ProvideGroup(provider+0x88, archive, group, ...)` with no flag1 gate.

So in **normal disk-backed operation**, a flag1=0 group is served by path **(3)**:

```
download (TCP or HTTP) -> GroupDownloaded (CRC ok) -> WorkerOnMessage case 0x1e
   -> Js5DiskCache::ProvideGroup  (writes raw container to SQLite write queue, state byte = 3)
[Js5DiskCache worker reads it back]
   -> ProcessDownloadResults -> HandleDownloadResult -> Js5MemoryCache::ProvideGroup
      -> next FileReady_WithCheck poll: LookupGroup HITS -> loop ENDS, world-connect proceeds.
```

## 11.4 Why OUR setup loops forever (VERIFIED structure + INFERRED config trigger)

In HTTP-content-server mode the **DiskCache round-trip of 11.3(3) is bypassed**, so a flag1=0 archive
never reaches MemoryCache:

- `GetFile_ArchiveGroup @0x00963c10`: `if (provider+0x70 /* synchronous-httpMode */ != 0) { GetFile(); return 0; }`. When set, it **never calls `RequestGroupFromDisk`** — it short-circuits to the synchronous `GetFile` (path (2)), which reads the empty `provider+0x80` store and yields an **empty** group. `RequestGroupFromServer @0x009642c0` has the **same** short-circuit at its top.
- The HTTP download path still runs (the HTTP queue drains every tick in `MainLogic` `@LAB_008f8326`) and `WorkerOnMessage` case 0x1e still writes the container to `Js5DiskCache (+0x90)`. But with disk writes disabled in HTTP mode (`diskCache+0x2207 = 0`, set in `WorkerOnMessage` case 10) and/or the synchronous `GetFile` reading the never-populated `+0x80` store, the container **never makes it back into `+0x88`**.
- Net: every ~40 ms poll re-runs `GetFile` → empty group → `LookupGroup` still misses → re-request → forever. (User's "3000× ~40 ms" symptom.)

**`WorkerOnMessage` case 10 (disk-cache setup) @0x0098dee0, VERIFIED:**
```c
iVar28 = DAT_01393568;                          // launcher instance index (instance.lock fcntl count)
*(uint *)(diskCache+0x10a74) = (iVar28 == 0);
if ((iVar28 != 0) || (provider+0x70 != 0)) {    // not-first-instance OR synchronous-httpMode
    diskCache+0x2209 = 1;                        // disk-cache HTTP mode ON
    diskCache+0x2207 = 0;                        // disk auto-write OFF
    diskCache+0x111e9 = 0;
}
```
`DAT_01393568` is the **launcher instance number** (`FUN_008333e0` = `fcntl(F_GETLK)` loop over
`launcher/instance.lock`), forced to 0 when `DAT_015a6791 != 0`. So secondary client instances *also*
run disk-cache HTTP mode. `provider+0x70` is the per-session synchronous-httpMode flag, set from the
jav_config content-server / httpMode params at provider construction.

> **INFERRED (high confidence):** `provider+0x70` (and therefore the synchronous-httpMode short-circuit)
> is enabled by the jav_config content-server advertisement (`param 35` URL, `param 37`/`param 49` host,
> `param 40`, httpMode `param 25`). The provider constructor that reads these and writes `+0x70` was not
> fully decompiled (it sets `+0x88`/`+0x90` via registers, not immediate stores, so it didn't surface in
> offset scans). The behavioral chain — content-server configured → synchronous-httpMode/disk-HTTP mode
> → DiskCache round-trip bypassed → loop — is fully consistent with every VERIFIED function above and
> with the empirical fact that index 40 is currently fetched over `/ms` HTTP.

## 11.5 Q1 — HTTP-vs-TCP routing per archive (VERIFIED)

**Two independent queues, both created by `CreateQueues` and both drained every `MainLogic` tick:**

- **TCP `Js5NetQueue`** (`worker+0x1b0`): uses a `ClientStream` TCP socket (`worker+0x1a8`). Connect
  state machine in `MainLogic`: `[worker+0x2a4]` 0 (send hello) → 1 (read reply) → 2 (read seed) →
  **3 (connected/streaming)**.
- **HTTP `Js5HTTPQueue`** (`worker+0x1b8`): drained at `MainLogic` `LAB_008f8326`, **independent of TCP
  state**; pending requests → `BuildHTTPRequestURL` → libcurl GET; completion (Content-Type **exactly**
  `application/octet-stream`) → `GroupDownloadedCallback`.

**Group-request routing in `OnMessage` case 0x1e (jump table → block `@0x008f6b10`), VERIFIED disasm
`@0x008f6c67`:**
```
CMP [worker+0x2a4], 3 ; JNZ fail            ; TCP socket must be in CONNECTED state 3
MOV rbp, [worker+0x1a8] ; TEST rbp,rbp ; JZ fail
CMP [rbp], 2 ; JZ 0x008f7180               ; socket type==2 -> dispatch
... else -> FailGetGroupResponse(reason 6 = "not connected")
```
- **Simple request form** (`[rsp+0x2f]=0` `@0x008f6bb2`) → always `Js5NetQueue::RequestData` (TCP).
- **Extended form** with per-request flag `[rsp+0x2f]` (`= flagByte & 1`) set → `Js5HTTPQueue::RequestData`.

So **which queue a group lands on is decided by the worker connection mode set up from the jav_config
content-server params**, not by any per-archive server field.

**Q1 concrete answers:**

- **"If jav_config does NOT advertise an HTTP content server, will index 40 be fetched over TCP
  `Js5NetQueue`?"** — **YES (INFERRED, high confidence).** With no content server, archives register in
  disk/TCP mode: `provider+0x70 == 0`, so `GetFile_ArchiveGroup`/`RequestGroupFromServer` no longer
  short-circuit, and group requests go through the normal TCP `Js5NetQueue` path (`OnMessage` case 0x1e,
  state==3). The TCP JS5 socket must be up (state 3); a private-server JS5 TCP listener satisfies that.
- **"On the TCP path, does the group reach `Js5MemoryCache` for index 40?"** — **YES (VERIFIED).** It
  reaches MemoryCache via the **DiskCache read-back** (11.3 path 3 → `HandleDownloadResult`), which is
  **flag1-independent**. (The message-flag1 written by `GroupDownloaded` is identical on TCP and HTTP —
  the worker doesn't know which queue delivered the bytes — so flag1 alone is *not* what makes TCP work;
  the disk round-trip is. TCP just keeps `provider+0x70 == 0`, which is what *enables* that round-trip.)

## 11.6 Q2 — `flag1` (`descriptor[+0x11]`) input source (VERIFIED)

`flag1` is assigned in `RebuildArchiveDescriptors_flag1FromType @0x009751e0` (line ~517 of the
decompile), inside the per-archive descriptor build loop (0xE0-byte source record → 0xC0-byte runtime
descriptor):

```c
iVar30 = *(int *)(srcRecord + 0x30);     // archive storage/source TYPE enum
desc[0x11] = (iVar30 == 6);              // <-- flag1
desc[0x12] = (iVar30 == 1);
```

- **Never read from any `Packet`/buffer** — there are **no** `gT*`/`g4`/`g2`/`gSmart` calls in this
  function; every populated field is a fixed-offset copy from the in-memory source record or a constant.
- **Not a master-index field.** `Js5MasterIndex::Js5MasterIndex @0x008f35d0` builds 0x28-byte archive
  entries with only `{crc, version, fileCount, size, whirlpool}` (offsets 0/4/0x20/0x24/+8). There is
  **no `+0x11` flag1 byte** sourced from the master index, and **no reference-table flag** feeds it.
- It is a **pure boolean of the archive's client-side storage-type enum** (`srcRecord+0x30`). `type == 6`
  ⇒ `flag1 = 1`; any other type ⇒ `flag1 = 0`. (The sibling `desc[+0x12] = (type==1)` and provider-level
  `provider+0x3191 = (type==6)` / `+0x3192 = (type==1)` confirm `+0x30` is the storage-class enum.)

**Why retail gets `flag1=1` for index 40 but we get `flag1=0`:** retail classifies index 40
(`AUDIO_STREAMS`) with storage-type **6** (the streamed/memory-resident class), so `flag1 = 1`. Our
setup classifies it with a different type, so `flag1 = 0`. The storage-type is assigned **client-side**
when the provider builds the archive config (from cache/jav_config configuration), and is **not
reachable from the server or the served wire bytes**.

**Q2 concrete answer:** **`flag1=1` is a client-side, type-derived classification — NOT reachable from
the server.** There is no master-index field, reference-table flag, jav_config param, or per-group wire
byte that sets `descriptor[+0x11]`. → **The flag1 lever is ruled out; the TCP-routing fix (11.0 / 11.5)
is the only option.**

## 11.7 What else changes if you drop the content server

- **All JS5 traffic moves to the TCP `Js5NetQueue`** (not just index 40). That is fine and is the normal
  RS retail-style transport: the client must connect to a **JS5 TCP listener** (the version handshake +
  per-archive group/index requests over the framed JS5 protocol documented elsewhere). Ensure the
  private server exposes that listener and the client's connect target points at it.
- The **DiskCache write/read round-trip is re-enabled** (`diskCache+0x2207` no longer forced 0 by the
  `provider+0x70 != 0` condition; the only remaining force-off is `DAT_01393568 != 0`, i.e. a **second
  concurrent client instance** — so test with a single client instance, or expect secondary instances to
  fall back to HTTP/memory mode regardless).
- `FileReady_WithCheck` for index 40 then completes via 11.3(3), and world-connect fires.

> **INFERRED caveat:** the exact jav_config params and the provider-constructor offset that set
> `provider+0x70` were not byte-confirmed (see 11.4). If, after dropping params 35/37/40/49 + 25, the
> client still routes index 40 over HTTP, the residual driver is the `provider+0x70` setter — re-RE the
> provider constructor (reachable via the `RebuildArchiveDescriptors` vtable group near
> `0x0111f8d4`/`0x01070618`, and from `LoginProtocolHandler @0x0024ecaf`) to find the precise param it
> reads, then clear that param.

> **RESOLVED (§11.8, second pass):** the caveat above has now been chased to ground. The
> provider-constructor that sets `provider+0x70` is `RecreateJs5ResourceProvider @0x0009b258`, and it sets
> `+0x70 = 1` **unconditionally** (no jav_config param involved). The dropping-content-server lever in
> §11.0 may still WORK (it re-routes to TCP), but the **minimal, init-safe fix is the Content-Type fix in
> §11.8 / §11.-1** — it does not require dropping the content server or standing up a TCP JS5 listener.

---

# 11.8 — VERIFIED root cause: HTTP `Content-Type` exact-match gate (second pass, 2026-06-23)

**Binary:** `rs2client.948-5`, base `0x0`. All addresses VERIFIED by decompile + disassembly.

## 11.8.1 What was pinned (and what the prior pass got wrong)

| Claim under test | Verdict | Evidence |
|---|---|---|
| `provider+0x70` is set from jav_config content-server params (scheme/host/port) | **FALSE (VERIFIED)** | `RecreateJs5ResourceProvider @0x0009b258` writes `MOV byte ptr [R15+0x70],1` **unconditionally** `@0x0009b481`; `R15 = operator_new(0x4f70)` (`@0x0009b3e8`) never reassigned before store to `client+0x18cd0` (`@0x0009e70e`). No URL/host/port/`https`/`localhost` string is read or compared anywhere in the function. |
| `iVar27` (case-10) is a URL/transport property | **FALSE (VERIFIED)** | `iVar27 = DAT_01393568` = launcher instance number (`FUN_008333e0` = `fcntl(F_GETLK)` lock-count on `launcher/instance.lock`; forced 0 when `DAT_015a6791 != 0`). `0` for a single client. |
| Tripping the case-10 disk-auto-write-OFF branch is the differentiator | **FALSE (VERIFIED)** | With `iVar27=0` and `provider+0x70=1` (hardcoded), `if ((iVar27 != 0) \|\| (provider+0x70 != 0))` is **always true** → auto-write OFF for **both** retail and private server. Not a differentiator. |
| **The HTTP response `Content-Type` must be exactly `application/octet-stream`** | **TRUE (VERIFIED) — this is the trigger** | `ConnectionManager::MainLogic @0x008f8410`: `eastl_string_compare_cstr(resp.ContentType, "application/octet-stream")` (exact full-length compare, `@0x0013ef80`). Mismatch → request `+0xa9=1` FAILURE → success cb skipped → no `GroupDownloaded` → no MemoryCache → loop. |

## 11.8.2 The decisive code (`ConnectionManager::MainLogic @0x008f8250`; compare site `@0x008f8e03`)

The HTTP queue (`worker+0x1b8` = `Js5HTTPQueue`) is drained **every tick**, independent of the TCP
state machine. For each completed libcurl request whose state is `1` (HTTP 2xx), exact disassembly:

```asm
008f8df0: LEA  RDI,[R15 + 0xb8]            ; R15+0xb8 = parsed response Content-Type (eastl::string)
008f8df7: LEA  RSI,[0x00ffc943]           ; "application/octet-stream"
008f8e03: CALL 0x0013ef80                 ; eastl_string_compare_cstr (basic_string::compare, EXACT)
008f8e08: TEST EAX,EAX
008f8e0f: JZ   0x008f8ed1                  ; ==0 (EXACT match) -> SUCCESS: invoke callbacks
008f8e15: MOV  byte ptr [R12 + 0xa9],1     ; !=0 -> FAILURE marker, success cb SKIPPED
```

Decompiled shape (the decompiler renders `request+0xb8` as `piVar21+0x2e` in its int-array view,
`0x2e*4 = 0xb8`):

```c
iVar8 = eastl_string_compare_cstr(&req->ContentType /*+0xb8*/, "application/octet-stream"); // @0x0013ef80
if (iVar8 == 0) {                        // EXACT full-length match (success path @0x008f8ed1)
    ... copy body, then ...
    (**(code**)(req + 0x16))(...);        // success cb = GroupDownloadedCallback
                                          //   -> GroupDownloaded@0x008f0a50 (CRC32)
                                          //   -> post op 0x1e -> WorkerOnMessage case 0x1e
                                          //   -> Js5DiskCache::ProvideGroup (+ MemoryCache if flag1)
} else {
    *(byte*)((long)req + 0xa9) = 1;       // FAILURE marker @0x008f8e15 -> success cb NEVER fires
}
```

`eastl_string_compare_cstr @0x0013ef80` is `eastl::basic_string::compare(const char*)`: it computes
`strlen(literal)` and the string's length, `memcmp`s the common prefix, and returns **0 only when the
two lengths are equal and all bytes match**. So:

- `application/octet-stream` → **0 (PASS)**
- `application/octet-stream; charset=binary` → length differs → **non-zero (FAIL)**
- `application/octet-stream ` (trailing space) → length differs → **FAIL**
- `Application/Octet-Stream` → byte differs → **FAIL**
- `application/octet-stream;` (bare trailing `;`) → length differs → **FAIL**

## 11.8.3 Why this exactly produces the index-40 / group-38557 loop

1. `provider+0x70 = 1` (always) ⇒ `GetFile_ArchiveGroup`/`RequestGroupFromServer` short-circuit to the
   synchronous `GetFile @0x00963740`, which serves from the in-memory index store at `provider+0x80`.
2. For that store to hold group 38557's container, the HTTP download must complete **successfully** so
   that `GroupDownloaded → WorkerOnMessage case 0x1e` writes it (and the disk round-trip /
   `HandleDownloadResult @0x00962960` re-populates `provider+0x88` MemoryCache).
3. The HTTP download **completes at the transport level** (2xx, body downloaded — which is why the prior
   pass observed "downloads index-40 over HTTP") but is **rejected at the Content-Type gate** if the
   header isn't byte-exact. The success callback never fires → `GroupDownloaded` never runs → nothing is
   handed to DiskCache/MemoryCache.
4. `FileReady_WithCheck @0x00965880` polls `Js5MemoryCache::LookupGroup(provider+0x88, 40, 38557)`,
   misses forever → re-requests every ~40 ms → **"Verifying Cache" loop** (the user's "3000× ~40 ms").

This is consistent with **every** VERIFIED function in §11.1–§11.6 and additionally explains the one
fact §11.4 could not: *why the container, demonstrably downloaded over HTTP, never reaches MemoryCache.*

## 11.8.4 THE FIX (server-side, init-safe, no TLS)

Make the private server's JS5 `/ms` HTTP endpoint return:

```
Content-Type: application/octet-stream
```

with **nothing appended** — no `; charset=...`, no trailing space, no parameters, exact casing.

- **Keep the content server advertised in `jav_config` unchanged** (`http://localhost:8829`, host
  `localhost`, ports `43596`). The scheme/host/port are **not** examined by this path; there is **no**
  `"https"` / `"localhost"` / `:443` check. **HTTPS is NOT required.** (This is why emptying the params
  hangs at "initializing resources" — the content server must stay advertised — but the advertised form
  is irrelevant to the gate; only the response header matters.)
- This is strictly **less invasive** than the §11.0 "drop the content server → TCP" lever: it needs no
  TCP JS5 listener and no jav_config change, and it makes the existing HTTP path complete.

### Necessary vs sufficient (be honest)

- **VERIFIED necessary.** The Content-Type exact-match gate is a **confirmed hard blocker**: with a
  non-exact header, the success callback never fires, so `GroupDownloaded`/CRC/`WorkerOnMessage case
  0x1e` never run and **nothing** is handed to DiskCache or MemoryCache. The group's bytes are
  discarded at `0x008f8e15`. Fixing the header is required for any further progress, full stop.
- **Sufficiency — apply and observe.** Once the header is exact, the HTTP download completes →
  `GroupDownloaded` → `WorkerOnMessage case 0x1e`. For `flag1==1` archives that path calls
  `Js5MemoryCache::ProvideGroup` directly (loop ends immediately). For a `flag1==0` archive (index 40)
  the bytes go to `Js5DiskCache::ProvideGroup` and reach MemoryCache via the disk read-back
  (`HandleDownloadResult @0x00962960`) **or** via the synchronous `GetFile @0x00963740` reading
  `provider+0x80`. **Retail demonstrably resolves index 40 with `provider+0x70 == 1` (disk auto-write
  forced off), so a working population path for flag1=0 exists under exactly these conditions** — the
  Content-Type gate is the only thing observed to break it. If, after the header fix, index 40 *still*
  loops, the residual work is to trace how the completed-download container reaches `provider+0x80` /
  MemoryCache for a flag1=0 archive when `diskCache+0x2207 == 0` (start at `WorkerOnMessage case 0x1e`
  @0x0098dee0's `Js5DiskCache::ProvideGroup` call and `ProcessDownloadResults @0x008f27d0` →
  `HandleDownloadResult`). That is a follow-on; it does **not** change the fact that the header fix is
  required first.

### Implementation note for the JS5/server team

Whatever serves `/ms` (Ktor `ConfigServer`/`JS5Server` or the proxy in front of it) must set the header
to the bare value. Common footguns:
- Ktor `call.respondBytes(bytes, ContentType.Application.OctetStream)` emits `application/octet-stream`
  **without** charset for `OctetStream` (octet-stream has no charset) — this is correct. But
  `call.respondBytes(bytes, ContentType.parse("application/octet-stream; charset=..."))` or any
  `ContentType` with parameters, or a reverse proxy that rewrites/appends, will FAIL the exact compare.
- Verify the wire bytes with `curl -sI 'http://localhost:8829/ms?m=0&a=40&k=0&g=38557&c=...&v=...'`
  and confirm the header is literally `Content-Type: application/octet-stream` (no trailing tokens).

## 11.8.5 Ghidra DB changes (this pass)

- `FUN_0009b258` → **`jag::ConnectionManager::RecreateJs5ResourceProvider`** (renamed) + entry comment;
  comment at `0x0009b481` documenting the hardcoded `provider+0x70 = 1`.
- `ConnectionManager::MainLogic @0x008f8410` — decompiler comment: the Content-Type exact-match gate is
  the world-entry-loop trigger.
- String `"application/octet-stream" @0x00ffc943` — comment: required exact response Content-Type.
- `WorkerOnMessage` case 10 `@0x0098fc2d` — comment: `iVar27 = DAT_01393568` is the launcher instance
  number, not a URL property.

