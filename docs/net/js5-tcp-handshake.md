# JS5 TCP Handshake Protocol (Byte-Level Specification)

Reverse-engineered from the NXT client binary (`rs2client`) via Ghidra MCP.

**Key Functions Analyzed:**
- `FUN_00323af0` -- Builds the JS5 init packet
- `jag::ConnectionManager::MainLogic` @ `0x00a2ba60` -- JS5 TCP connection state machine
- `jag::Js5WorkerThread::OnMessage` @ `0x00a20970` -- Worker thread message handler
- `jag::Js5WorkerThread::CreateQueues` @ `0x00a1d3b0` -- Connection/queue setup
- `FUN_009d6b90` -- Sends individual file requests over TCP

---

## Connection State Machine

The JS5 TCP connection runs on a dedicated `Js5WorkerThread` and uses a 4-state machine stored at `this+0x2a4`:

```
State 0 (CONNECTING)
    |  Send handshake bytes, transition immediately
    v
State 1 (AWAITING_RESPONSE)
    |  Read 1 byte response
    |  If 0 (SYNC) -> State 2
    |  Otherwise -> disconnect, reconnect with backoff
    v
State 2 (READING_PREFETCH_KEYS)
    |  Read N prefetch key ints (N * 4 bytes)
    |  Send encryption ACK (opcode 6)
    |  Send connection ready (opcode 3)
    |  Transition to State 3
    v
State 3 (CONNECTED)
    |  Process file request queues
    |  Read file responses
```

A 30-second timeout applies in State 3. If no data is received within 30 seconds, the connection is closed.

---

## Step 1: Client Sends Handshake (State 0)

Built by `FUN_00323af0`. The complete byte layout:

```
Offset  Size   Type           Value/Description
------  ----   ----           -----------------
0       1      uint8          JS5_INIT opcode = 15 (0x0F)
1       1      uint8          Payload size = token_length + 10
2       4      int32 (BE)     Major version (946 = 0x000003B2)
6       4      int32 (BE)     Minor version (1 = 0x00000001)
10      N      string         JS5 token (NOT null-terminated here, raw bytes)
10+N    1      uint8          Null terminator (0x00)
11+N    1      uint8          Platform/language byte (read from client config)
```

**Total packet size** = 12 + token_length bytes.

**Size byte calculation**: The size byte at offset 1 covers everything after itself: `4 (major) + 4 (minor) + token_length + 1 (null) + 1 (platform) = token_length + 10`.

### Critical Finding: Trailing Byte

The existing `JS5Connection.kt` and `Diagnostic.kt` send `payloadSize = 4 + 4 + tokenBytes.size + 1`, which equals `token_length + 9`. This is **off by 1** because they omit the trailing platform/language byte.

The correct size should be `token_length + 10`, and a trailing byte must be appended after the null terminator.

**Source evidence** from `FUN_00323af0`:
```c
// Write opcode (15)
*(char *)(buf + pos) = (char)DAT_016e9550;  // DAT_016e9550 = 0x0F
pos++;

// Write size byte
*(char *)(buf + pos) = (char)iVar3 + '\n';  // '\n' = 10
pos++;

// Write major version as big-endian int
Packet::pT_int(buf, 0x3b2);  // 946

// Write minor version as big-endian int
Packet::pT_int(buf, 1);

// Write token string + null terminator
memcpy(buf + pos, token_str, token_len);
*(buf + pos + token_len) = 0;  // null terminator
pos += token_len + 1;

// Write trailing byte (platform/config value)
*(char *)(buf + pos) = (char)**(uint32_t **)(config_ptr);
pos++;
```

---

## Step 2: Server Response (State 1)

The client reads exactly **1 byte**:

| Value | Meaning | Client Action |
|-------|---------|---------------|
| 0 | JS5_SYNC (success) | Transition to State 2 |
| 6 | GAME_UPDATE (version mismatch) | Disconnect, show `js5connect_outofdate` |
| 7 | WORLD_FULL | Disconnect, show `js5connect_full` |
| 26 (0x1A) | Also WORLD_FULL | Same as 7 |
| 48 (0x30) | SESSION_EXPIRED | Disconnect, show `sessionexpired` |
| Other | Generic error | Disconnect, show `js5connect` |

**From `LoginProtocolHandler` at `0x002f6fa0`:**
- Response code is stored at `this_01->field_0x2c`
- Code 6 = `js5connect_outofdate` (line 666)
- Codes 7, 0x1A = `js5connect_full` (line 735)
- Code 0x30 = `sessionexpired` (line 706)
- All other non-zero = generic `js5connect` error (line 756)

---

## Step 3: Read Prefetch Keys (State 2)

After receiving SYNC (0), the client reads **N * 4 bytes** where N is the prefetch key count.

The prefetch key count `N` is stored at `Js5WorkerThread+0x250` and is set during worker thread initialization via a `gT_ushort` read from the init message (see `OnMessage` case 2). This value is passed from the main thread when the JS5 system is initialized.

Each key is read as a **big-endian 4-byte int** via `Packet::gT_uint`.

**Typical value**: N = 27 (for 27 archive indices in the current cache).

**Server implementation**: The server must send exactly N int32 values after the SYNC byte. The number of keys must match what the client expects. If the server sends 0 bytes (as observed in our bug), the client will wait forever or timeout.

```
Offset  Size      Type         Description
------  ----      ----         -----------
0       N * 4     int32[] (BE) Prefetch keys (one per archive index)
```

---

## Step 4: Client Sends Acknowledgments (State 2 -> State 3)

After reading all prefetch keys, the client sends **two** TCP writes:

### Write 1: Encryption/Version ACK (8 bytes)

```
Offset  Size  Type       Value
------  ----  ----       -----
0       1     uint8      6 (opcode: ENCRYPTION_ACK / CONNECTION_INFO)
1       1     uint8      0
2       1     uint8      0
3       1     uint8      5
4       4     int32 (BE) Major version (946 = 0x000003B2)
```

**Source evidence** from `MainLogic` State 2:
```c
Packet::pT_uchar(plVar17, 6);       // opcode 6
// 3 manually written bytes:
*(buf + pos++) = 0;
*(buf + pos++) = 0;
*(buf + pos++) = 5;
Packet::pT_int(plVar17, iVar10);    // iVar10 = field_0x308 = 0x3B2 (946)
ClientStream::Write(...);
```

The bytes `[0, 0, 5]` may encode: `padding(1) + padding(1) + encryption_type(1)` where 5 indicates no encryption, or they form a 3-byte medium value of `0x000005`.

### Write 2: Connection Ready (1 byte)

```
Offset  Size  Type       Value
------  ----  ----       -----
0       1     uint8      3 (opcode: CONNECTION_READY)
```

After this, `ClientStream::Flush` is called and the connection transitions to **State 3 (CONNECTED)**.

---

## Step 5: File Requests (State 3)

Once connected, the client sends file requests using `FUN_009d6b90`. Each request is **6 bytes**:

```
Offset  Size  Type       Description
------  ----  ----       -----------
0       1     uint8      Flags: (priority_enum << 4) | is_urgent
1       1     uint8      Archive ID
2       4     int32 (BE) Group ID
```

**Flags byte breakdown:**
- Bits 0-3: `is_urgent` flag (1 = urgent/high priority, 0 = prefetch)
- Bits 4-7: `priority_enum` value (request priority level)

**Request for master index**: archive=255 (0xFF), group=255 (0xFF).

---

## File Response Format (State 3)

Each response from the server follows the standard JS5 response format:

```
Offset  Size  Type       Description
------  ----  ----       -----------
0       1     uint8      Archive ID
1       4     int32 (BE) Group ID (top bit: 0 = urgent, 1 = prefetch)
5       1     uint8      Compression type (0=none, 1=bzip2, 2=gzip, 3=lzma)
6       4     int32 (BE) Compressed data length
10      ...   bytes      Data payload (chunked into 512-byte blocks with 0xFF separators)
```

The response data is **chunked**: every 512 bytes, a `0xFF` separator byte is inserted. The client reads the first 5-byte header, determines the total size, then reads data in 512-byte chunks, skipping the separator bytes.

---

## Complete Handshake Byte Sequence (Example)

Using token `j4mhSS8dVoheay-bfgy69x*nmrCTmAEk` (32 chars), platform byte 0:

### Client -> Server (44 bytes):
```
0F                          -- JS5_INIT opcode (15)
2A                          -- Size byte (32 + 10 = 42)
00 00 03 B2                 -- Major version (946)
00 00 00 01                 -- Minor version (1)
6A 34 6D 68 53 53 38 64     -- "j4mhSS8d"
56 6F 68 65 61 79 2D 62     -- "Voheay-b"
66 67 79 36 39 78 2A 6E     -- "fgy69x*n"
6D 72 43 54 6D 41 45 6B     -- "mrCTmAEk"
00                          -- Null terminator
00                          -- Platform byte (0)
```

### Server -> Client (1 + 108 bytes):
```
00                          -- JS5_SYNC (success)
XX XX XX XX                 -- Prefetch key 0 (int32 BE)
XX XX XX XX                 -- Prefetch key 1
...                         -- (27 total keys)
XX XX XX XX                 -- Prefetch key 26
```

### Client -> Server (8 + 1 = 9 bytes):
```
06                          -- Encryption ACK opcode
00 00 05                    -- Padding/encryption type
00 00 03 B2                 -- Major version (946)

03                          -- Connection Ready opcode
```

### Client -> Server (file requests, 6 bytes each):
```
11                          -- Flags: priority=1, urgent=1
FF                          -- Archive 255 (master index)
00 00 00 FF                 -- Group 255 (master index)
```

---

## Bugs in Current Implementation

### Bug 1: Missing Trailing Byte in Handshake

`JS5Connection.kt` line 87-94 and `Diagnostic.kt` line 59-65 both:
1. Calculate `payloadSize = 4 + 4 + tokenBytes.size + 1` (should be `+ 2`)
2. Omit the trailing platform/language byte after the null terminator

**Fix**: Add 1 to payloadSize and write an extra byte (value 0) after the null terminator.

### Bug 2: ACK Format Mismatch

`JS5Server.kt` expects the client ACK as:
```kotlin
val opcode = input.readByte()   // expects ACKNOWLEDGE
val id = input.readMedium()     // expects 3
val endAck = input.readUShort() // expects 0
```

But the client actually sends TWO separate writes:
1. `[6, 0, 0, 5, <version_int>]` -- 8 bytes
2. `[3]` -- 1 byte

The server incorrectly combines these into a single read sequence. The opcode 6 packet is 8 bytes (not 4), and opcode 3 is a separate 1-byte packet.

### Bug 3: Hardcoded Prefetch Key Count

The current `CacheDownloader.kt` hardcodes 27 prefetch keys. The actual count should match the number of archive indices in the cache. The client receives this count from the server (it's implicit -- the server sends however many keys it wants, and the client knows how many to expect based on initialization).

---

## Server Implementation Notes

The JS5 server should:

1. Read the handshake: opcode(1) + size(1) + major(4) + minor(4) + token(N) + null(1) + platform(1)
2. Validate version and token
3. Send: SYNC byte (0) + prefetch keys (N * 4 bytes)
4. Read: opcode 6 ACK (8 bytes total: `06 00 00 05 XX XX XX XX`)
5. Read: opcode 3 ready (1 byte: `03`)
6. Enter file request/response loop

The `content.runescape.com:43594` server likely expects the full correct handshake including the trailing platform byte and correct size. If the size byte is wrong by 1, the server may read the wrong number of bytes, consume the first byte of the next message, and close the connection.
