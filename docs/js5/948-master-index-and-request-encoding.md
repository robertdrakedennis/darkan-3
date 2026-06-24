# JS5 948-5 — Master Index Validation, TCP/HTTP Routing, Request Encoding, and Idle Timeout

**Primary source:** `rs2client.948-5` (macOS, the authority binary per project memory).
**Key function addresses are 948-5 image-base-0 offsets unless labelled otherwise.**
**Older docs referenced:** `docs/cache/master-index-format.md` (analysed against ~946),
`docs/net/js5-post-master-index-flow.md` (946), `docs/net/js5-response-parsing.md` (946).

---

## Summary Lead (answer first)

**Q1 — RSA magic byte:** `byte[0]` of the 65-byte RSA-decrypted block is **NOT explicitly
checked against any constant in the 948-5 constructor**. The only checks are (a) decoded
length == 0x41, and (b) Whirlpool over the 64-byte tail (`decoded[1..64]`). Byte[0] is
skipped entirely in the comparison loop. Therefore **0x0A and 0x01 are both valid**; the
client does not reject on byte[0] value. The `0x01` magic check documented in the older
`master-index-format.md` (§ "Decrypted Signature Format") was **inferred from librs2client.so
(~rev 890) convention — it does not appear as an explicit compare in 948-5**. Your server
signing with `0x0A` is not the cause of the master-index rejection.

**Q2 — TCP vs HTTP:** After the master index is received, **content requests (255/N indices
and N/M groups) go over the SAME persistent TCP socket, not HTTP**, as long as `httpMode`
is false and `diskCacheEnabled` is true. `httpMode` is the flag that routes to the HTTP
queue; it is set from `jav_config.ws` param `param=25` / `ModeWhere`. In a private server
deployment, where that param is absent or set to the TCP path, the client uses TCP for
everything. Zero further TCP requests after the master index is therefore **not expected
behaviour** — it is a failure symptom, and the root cause is almost certainly the
**diskCacheEnabled flag being false** (no disk cache → `GetFile_ArchiveGroup` silently
drops every request) OR a Whirlpool mismatch in the master index.

---

## Q1 — RSA Magic Byte in 948-5

### Function: `Js5MasterIndex::Js5MasterIndex` @ `0x8f35d0`

From `docs/binary/boot/01-cache-bootstrap.md` §2 (948-5 verified):

```c
// §2.2 — RSA decryption (when param_2 and param_3 are non-null)
BigInteger sig;  FromBytes(&sig, body, len);
BigInteger out;  ModPow(&sig, *param_2, *param_3, &out);
decoded = out_bytes;   // byte-reversed big-endian result

// §2.3 — Only two checks:
if (local_120 == 0x41) {           // decoded length must be exactly 65 bytes
    // Whirlpool comparison: 9-byte strides over decoded[1..0x40]
    // "9-byte strides up to 0x40" against local_118[1..0x40]
    // On mismatch → bail to joined_r0x008f3903 (cleanup)
    ...
    // On success → parse per-archive table
}
// If local_120 != 0x41 → fall through to cleanup (archive table left empty)
```

**There is no instruction of the form `cmp byte[decoded], 0x01` or `cmp byte[decoded], 0x0A`.**
The Whirlpool loop starts at `local_118 + 1` (i.e. `decoded[1]`), skipping `decoded[0]`
without ever comparing it. The byte at offset 0 of the decrypted block is **silently ignored**.

### Implication for your server

- Your server signing with `byte[0] = 0x0A` is **not causing master index rejection** in 948-5.
- Signing with `byte[0] = 0x01` works equally. Both are transparent.
- FLAG: The older `docs/cache/master-index-format.md` §"Decrypted Signature Format" states
  `byte[0] must be 0x01` — **this is STALE**. It was inferred from librs2client.so (~rev 890)
  where the byte may have been checked. In 948-5 that check is absent.

### Required length

Still 0x41 (65 bytes). That check is explicit and unchanged.

### Whirlpool scope

Bytes `[5 .. 5 + 1 + archiveCount*80)` of the container — the archiveCount byte plus all
80-byte per-archive entries. Same as documented in `master-index-format.md`.

---

## Q2 — TCP vs HTTP: Which Transport Carries 255/N and N/M

### `GetFile_ArchiveGroup` in 948-5 (addr: `0x963740` area, via `cache-js5.md`)

The dispatch logic (unchanged between rev 946 and 948-5 per the delta doc):

```c
GetFile_ArchiveGroup(this, archiveId, groupId, priority):
    // Path 1: HTTP mode
    if (this->httpMode) {          // flag at provider+0x70
        GetFile(this, archiveId, groupId, priority);
        return 0;
    }
    // Path 2: disk cache (= TCP path in steady state)
    if (this->diskCacheEnabled) {  // flag at provider+0xF628 (946 offset)
        return RequestGroupFromDisk(this, archiveSettings, groupId, ...);
    }
    // Path 3: DEAD END
    return 0;   // request silently dropped
```

### The `httpMode` flag

- Set from `jav_config.ws` `param=25` (`ModeWhere`), NOT from whether the master index
  arrived over TCP.
- A private server's `jav_config.ws` either omits `param=25` entirely or sets it to the
  TCP-mode value. In that case `httpMode` is **false** and ALL content (255/N indices, N/M
  groups) goes over the persistent TCP socket.
- The master index arriving over TCP does NOT set `httpMode` to true. The two transports
  are independent.

### Can master-index-over-TCP coexist with content-over-HTTP?

Yes — architecturally they are independent queues (`Js5NetQueue` for TCP, `Js5HTTPQueue`
for HTTP). But the default private-server path has `httpMode = false`, so both master
index and content travel on TCP.

### If content WERE going over HTTP (not expected in your setup)

The HTTP URL format (from `js5-http-group-download.md`):
```
http://{host}:{port}/ms?m=0&a={archive}&g={group}&c={crc}&v={version}
```
Base URL comes from `WorldLobbyData::GetHTTPURL` (called at `Construct` §1.3) which is
derived from the world's lobby data, not from `jav_config.ws` directly.

**For your diagnosis:** if the client is routing through your `ConfigServer`'s `/ms` endpoint
you would see HTTP GET requests there. If you see nothing on `/ms` and nothing on the TCP
socket after the master index, the traffic is not going to HTTP — the client is silently
dropping requests at Path 3 (the dead end).

---

## Q3 — Master Index: Requested Once or Re-Requested on Reconnect

### Initial request (TCP message type 0x0A)

The master index is requested exactly **once at startup** over the TCP socket.
`Js5MasterIndex::Construct` @ `0x496bc0` (boot doc §1) is guarded by a singleton flag
(`DAT_015a76b8` / `*(context + …d41…+1)`) — it runs exactly once. The TCP handshake
packet is built once and the master index request is implicit in the connection handshake
(`leading opcode byte = 2` in the handshake; the server responds with the master index
on the same connection).

### After a reconnect

On TCP reconnect, the client sends the handshake packet again (opcode 2 + URL strings +
world byte + token). The server MUST respond to this with the master index again (the
handshake is the trigger). Once the client has a valid master index in memory (non-NULL
`masterIndex` pointer), WorkerOnMessage case 10 enters the "update" branch:

```c
// WorkerOnMessage case 10 (js5-post-master-index-flow.md §WorkerOnMessage case 10):
if (this->memoryCache != NULL) {   // existing session
    // Compare old vs new master index entries — detect changed CRC/version
    // Resets affected Js5Index objects
    // Then jumps to step 8 (return, no DiskCache re-create)
} else {
    // First-time init: create MemoryCache, DiskCache, etc.
}
```

So on reconnect the client DOES re-send the handshake and DOES need the server to
return the master index again. After the master index is received, indices and groups are
re-requested only if the CRC/version changed.

### Is the 30s reconnect loop healthy or a failure sign?

**A reconnect loop with zero index/group requests after each master index = failure.**

A healthy reconnect cycle sends the master index, then immediately follows with index
requests (archive 255, group N for each needed archive). If the master index was accepted,
`IndexReady` sends message type 0x14 to the worker thread which then sends TCP requests
for 255/N. If you observe:
- Handshake → master index delivered → 30s pause → disconnect → repeat
with NO 255/N requests in between, the gate is either:
1. Whirlpool mismatch (master index rejected silently), or
2. `diskCacheEnabled = false` (GetFile_ArchiveGroup hits path 3 and drops everything), or
3. Game systems haven't asked for any files yet (unlikely — login screen triggers requests
   almost immediately).

### Message type 0x28 (periodic HTTP verify)

This is an alternative master index re-verification path over HTTP (not the initial fetch).
It is a lower-priority background check. It does NOT replace the 0x0A TCP request and does
NOT run on the typical private-server path where HTTP re-verify is not configured.

---

## Q4 — JS5 TCP Request Wire Encoding in 948-5

### Function: `Js5NetQueue::RequestData` @ `0x8b4360` (948-5, from `cache-js5.md`)

The request-sending Packet is allocated with **10 bytes** capacity (from `CreateQueues`
in both 946 and 948-5; this did not change per the delta doc). Only 6 bytes carry meaning:

```
Byte 0:     flags   = (priority << 4) | isUrgent
Byte 1:     archive = archiveId (u8)
Bytes 2-5:  group   = groupId (u32, big-endian)
Bytes 6-9:  padding = uninitialized / zeros
```

`ClientStream::Write` sends all 10 bytes. Your server reads all 10 bytes:
`flags(1) + index(1) + group(4) + padding(4) = 10 bytes`.

**This layout is confirmed unchanged between 946 and 948-5** (the 948-5-delta-from-948-2
doc confirms the `Js5NetQueue::RequestData` fn at `0x8b4360` in 948-5 with the same
prototype and no size change).

### Flags byte detail

| bit(s) | meaning |
|--------|---------|
| bit 3-7 | `priority >> 0` (the queue priority level) |
| bit 0 | `isUrgent`: 1 = urgent queue, 0 = prefetch |
| bits 1-2 | unused |

For the urgent/normal distinction the client uses bit 0 only when building the flags byte,
so effectively `flags = isUrgent` for the simple case (priority = 0 in the urgent queue,
priority > 0 in prefetch). The exact flags value your server sees in practice for
non-prefetch index requests is typically `0x00` (urgent, priority 0).

### Are opcodes C2S different here?

The game-protocol C2S opcodes (240/156/218 etc.) are ClientProt opcodes — completely
separate from JS5. The JS5 request wire format does NOT use any ClientProt opcode encoding.
The 10-byte request above is the complete JS5 request; your `JS5Server.reader` reading 10
bytes is correct.

---

## Q5 — 30-Second Idle Timeout Semantics

### From `ConnectionManager::MainLogic` (946 body, `js5-response-parsing.md` §4)

```c
if (uVar16 < 0x7531) goto process;              // time delta < 30001ms: skip timeout check
if (uVar16 - 30000 <= (ulong)plVar4[10]) goto process;  // last-receive within 30s: skip
// Otherwise: close TCP connection, reconnect
```

`plVar4[10]` = **last-data-received timestamp** (written when bytes arrive from TCP).
`uVar16` = current time.

### Timer reset by RECEIVE only, not SEND

The timer (`plVar4[10]`) is updated when bytes are read from the socket — not when the
client writes requests. A client that sent no requests (or whose requests received no
response) will see the timer expire after 30s of silence from the server side, and will
disconnect and reconnect.

### Does HTTP traffic reset the TCP timer?

**No.** HTTP requests and responses travel through a completely separate path
(`Js5HTTPQueue`, not `ClientStream`). HTTP activity does not update `plVar4[10]`.
The TCP timer is reset only when the TCP socket delivers bytes.

### Diagnosis for your scenario

The macOS 948-5 client:
1. Connects over TCP
2. Sends handshake, receives master index (this resets the TCP timer)
3. Makes zero further TCP requests (no 255/N follows)
4. 30 seconds after the master index was received, the timer fires → disconnect → reconnect

This is a **failure loop, not a healthy keepalive**. A healthy idle client would be
sending `NO_TIMEOUT` (ClientProt op 51, 0-byte packet) to keep the GAME TCP connection
alive — but the JS5 TCP connection has no such keepalive; it relies entirely on
data flowing. The 30s reconnect IS evidence of failure (no data flowing from the server
in response to requests that were never sent).

### If content went over HTTP (not your case)

If `httpMode` were true, the client would route all content over HTTP and the TCP socket
would genuinely be idle after the master index. In that scenario the 30s reconnect would
be a healthy background re-check cycle, not a bug. But since your private server sets up
the TCP path (`httpMode = false`), this does not apply.

---

## Root Cause Diagnosis Summary

Given:
- Master index delivered over TCP ✓
- Zero 255/N requests after master index
- 30-second cycle

The byte[0] = 0x0A magic byte is **not** the cause (see Q1).

**Most likely cause: the `diskCacheEnabled` flag is false.** Checklist:

1. Does the cache folder exist and is it writable?
   ```
   mkdir -p ~/.darkan3 && chmod 755 ~/.darkan3
   ```
2. Is `cache_folder=` present and correct in `preferences.cfg`?
3. Do `.jcache` SQLite files appear in the cache folder after the client connects?
   If not, `Js5DiskCache::Startup` @ `0x9009c0` (948-5) is failing or being skipped.

**Second most likely: Whirlpool mismatch.** The Whirlpool hash in your RSA block
must match `Whirlpool(container[5 .. 6 + archiveCount*80])`. Verify your
`VersionTableBuilder` computes the Whirlpool over exactly that range.

**Not the cause:**
- byte[0] value (0x0A vs 0x01) — not checked in 948-5
- TCP request encoding — unchanged from 946, your 10-byte reader is correct
- 30s reconnect interval — expected once the client goes idle due to failed gate

---

## Appendix: 948-5 Address Index for JS5 (verified from reclass-data)

| Function | 948-5 address | Source doc |
|---|---|---|
| `Js5MasterIndex::Construct` | `0x496bc0` | `boot/01-cache-bootstrap.md` |
| `Js5MasterIndex::Js5MasterIndex` (parser/ctor) | `0x8f35d0` | `boot/01-cache-bootstrap.md` |
| `Js5ResourceProvider::GetFile` | `0x963740` | `cache-js5.md` |
| `Js5ResourceProvider::RequestGroupFromDisk` | `0x8f76e0` | `cache-js5.md` |
| `Js5ResourceProvider::RequestGroupFromServer` | `0x9642c0` | `cache-js5.md` |
| `Js5ResourceProvider::FileReady_CheckIndex` | `0xc31050` | `cache-js5.md` |
| `Js5ResourceProvider::HandleDownloadResult` | `0x962960` | `cache-js5.md` |
| `Js5MemoryCache::ProvideGroup` | `0x8f1fb0` | `cache-js5.md` |
| `Js5DiskCache::ProvideGroup` | `0x8f2580` | `cache-js5.md` |
| `Js5DiskCache::Startup` | `0x9009c0` | `cache-js5.md` |
| `Js5DiskCache::GetDatabaseFilename` | `0x8f9bb0` | `cache-js5.md` |
| `Js5NetQueue::RequestData` | `0x8b4360` | `cache-js5.md` |
| `Js5HTTPQueue::RequestData` | `0x8f5af0` | `cache-js5.md` |
| `Js5WorkerThread::OnMessage` | `0x8f6360` | `cache-js5.md` |
| `Js5WorkerThread::GroupDownloaded` | `0x8f0a50` | `cache-js5.md` |
| `Js5WorkerThread::IndexDownloaded` | `0x8f0350` | `cache-js5.md` |
| `Js5Index::LoadIndex` | `0x8ea410` | `cache-js5.md` |

All older-binary addresses (946 `0x00988e80`, `0x00a1db50`, etc.) from
`master-index-format.md` and `js5-post-master-index-flow.md` are STALE; use the table
above for 948-5.
