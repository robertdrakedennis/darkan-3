# Login Wire Format - Verified from rs2client rev 946

**Binary**: rs2client (Ghidra port 8080, STRIPPED)
**Verified**: 2026-03-11

This document contains byte-level packet formats verified directly from decompiled rs2client functions. All opcodes, field types, sizes, and orderings are confirmed from the binary.

---

## LoginProt Opcodes (Verified)

Initialized in `jag::ServerProt::RegisterAll` at `0x00182d7d`.

Each LoginProt entry is an 8-byte struct: `[4B opcode (int)][4B size (int)]`
Size encoding: 0+ = fixed size, -1 (0xFFFFFFFF) = varByte, -2 (0xFFFFFFFE) = varShort.

| Global Address | Opcode (dec) | Opcode (hex) | Size | Purpose |
|---------------|-------------|-------------|------|---------|
| DAT_016e9558 | 14 | 0x0E | 0 (fixed, empty) | Handshake request |
| DAT_016e9550 | 15 | 0x0F | -1 (varByte) | *Legacy/StartRSA packet* |
| DAT_016e9548 | 16 | 0x10 | -2 (varShort) | Game login |
| DAT_016e9540 | 19 | 0x13 | -2 (varShort) | Lobby login |
| DAT_016e9538 | 23 | 0x17 | 4 (fixed) | *Unknown (4 bytes)* |
| DAT_016e9530 | 24 | 0x18 | -1 (varByte) | *Unknown (varByte)* |
| DAT_016e9528 | 26 | 0x1A | 0 (fixed, empty) | Continue/ack |
| DAT_016e9520 | 27 | 0x1B | 0 (fixed, empty) | *Unknown (no payload)* |
| DAT_016e9518 | 28 | 0x1C | -2 (varShort) | *Social network token* |
| DAT_016e9510 | 29 | 0x1D | -2 (varShort) | *Unknown (varShort)* |
| DAT_016e9508 | 30 | 0x1E | -2 (varShort) | Reconnect login |
| DAT_016e9500 | 31 | 0x1F | 4 (fixed) | *Unknown (4 bytes)* |

**NOTE**: The old docs reference DAT_016fb2b8, DAT_016fb268, etc. -- those addresses are WRONG for this binary. The correct addresses are in the table above. The old addresses may have been from the symbols binary (librs2client.so).

### Opcode Usage Map

| Function | Opcode Used | Global Reference |
|----------|------------|-----------------|
| LoginStepWaitingSSOKeyResponse | 14 | DAT_016e9558 |
| SendLoginPacketInner (lobby, non-reconnect) | 19 | DAT_016e9540 |
| SendLoginPacketInner (game, non-reconnect) | 16 | DAT_016e9548 |
| SendLoginPacketInner (reconnect, any mode) | 30 | DAT_016e9508 |
| LoginStepWaitingSendContinue | 26 | DAT_016e9528 |
| LoginStepWaitingServerClientVar (more data) | 26 | DAT_016e9528 |

---

## Step 15 (0x0F): Handshake Request

**Function**: `LoginStepWaitingSSOKeyResponse` at `0x0022ee10`
**Login step**: 15 (0x0F)
**Direction**: Client -> Server
**Transitions to**: Step 30 (0x1E) on success

### Preconditions
- TCP connection is established (step 14 opened it)
- Connection status checked: `*(*(serverConnectionPtr + 8)) == 1`

### Wire Format

The handshake packet uses opcode **14** with size **0** (no payload body).

```
Byte layout (on the wire):
  [1B] Connection type byte = 14     (the LoginProt opcode)
  -- No additional data --
```

The function:
1. Waits for connection status to be 1 (connected)
2. Clears the message queue on the ServerConnection
3. Creates a message with `FUN_0022e7a0` using opcode DAT_016e9558 (=14), size 0
4. Enqueues it via `FUN_00cb92d0` and calls `FlushClientMessages`
5. On success: sets loginStep = 0x1E (30)
6. On flush failure: sets loginStep = 0, result = CONNECTION_FAILED (0x16)

**Key insight**: The handshake is just the single byte `14` sent to the server. There is no additional data. This is the connection type discriminator -- the server uses this byte to route to the login handler (vs JS5 which uses byte `15`).

---

## Step 30 (0x1E): First Server Response

**Function**: `LoginStepWaitingFirstResponse` at `0x00220f70`
**Login step**: 30 (0x1E)
**Direction**: Server -> Client

### Wire Format

The server sends a fixed-size response. The client reads it as a single block.

```
Server response:
  [1B] response_code : byte
  [8B] session_key   : long (gT_ulong, big-endian)
Total: 9 bytes
```

### Response Code Handling

| Code | Meaning | Next Step |
|------|---------|-----------|
| 0 | OK, exchange data | Resume savedStep (if non-zero), else error 0x42 |
| Non-zero | Error code | Close connection, set LoginResult to code |

On `response_code == 0`:
- The 8-byte `session_key` is stored at `LoginManager+0x40`
- If `savedStep` (field +0x14) is non-zero, jump to that step
- For lobby login flow: savedStep = 0x50 (80 = SEND_LOGIN_PACKET)
- For legacy flow: savedStep = 0x118 (280 = START_RSA_PACKET)
- If savedStep == 0: sets LoginResult = 0x42 (error), closes connection

**CONFIRMED**: The existing docs saying "9 bytes: 1 response code + 8 session key" are correct.

---

## Step 40 (0x28): Second Server Response (Payload Size)

**Function**: `LoginStepWaitingSecondResponse` at `0x00220f10`
**Login step**: 40 (0x28)
**Direction**: Server -> Client

```
Server sends:
  [2B] payload_length : ushort (gT_ushort_)
```

Stored at `LoginManager+0x128`. Transitions to step 50 (0x32).

---

## Step 50 (0x32): XTEA-Encrypted Challenge

**Function**: `LoginStepDealWithSecondResponse` at `0x00220c00`
**Login step**: 50 (0x32)
**Direction**: Server -> Client

The server sends `payload_length` bytes (from step 40). The client:
1. Reads the raw bytes
2. XTEA-decrypts in 8-byte blocks using the key at `LoginManager+0x48`
3. Reads a string (nonce/challenge)
4. Validates via `FUN_00b9c0d0`

```
Server sends (XTEA-encrypted):
  [NB] XTEA-encrypted data, where N = payload_length

  After XTEA decryption:
    string: server challenge nonce (null-terminated)
```

**XTEA parameters**:
- Key: 4 ints at LoginManager+0x48
- Delta: 0x9E3779B9 (standard, but with 0x61c88647 = -delta in two's complement)
- Block size: 8 bytes
- Rounds: The inner loop runs until `uVar7 == 0`, starting from `0xc6ef3720` which is `32 * delta`. This is standard 32-round XTEA decryption.

On success: step = 0x3C (60). On failure: CONNECTION_FAILED.

---

## Step 60 (0x3C): Third Response (Go-Ahead)

**Function**: `LoginStepWaitingThirdResponse` at `0x00220b90`

Not decompiled in this session, but from existing docs and context:

```
Server sends:
  [1B] go_ahead : byte (must be 1)
```

If go_ahead == 1: transitions to step 70 (0x46).

---

## Step 70 (0x46): Login Token (XTEA-Encrypted)

**Function**: `LoginStepDealWithThirdResponse` at `0x00220900`
**Login step**: 70 (0x46)
**Direction**: Server -> Client

```
Server sends:
  [16B] XTEA-encrypted data (2 blocks of 8 bytes)

  After XTEA decryption (same key at +0x48):
    [8B] login_token  : long (pT_int x2, then read back as longs)
    [8B] session_nonce: long
```

- `login_token` stored at `LoginManager+0x120`
- `session_nonce` stored at `LoginManager+0x130`
- If `login_token < 0`: error, set RECONNECT_TRY_AGAIN (0x23)
- On success: transitions to step 80 (0x50 = SEND_LOGIN_PACKET)

---

## Step 80 (0x50): Send Login Packet

**Function**: `LoginStepSendLoginPacket` at `0x00263f20` -> calls `SendLoginPacketInner` at `0x00263e70`
**Login step**: 80 (0x50)
**Direction**: Client -> Server

### Opcode Selection

```
if loginType == 2 (game):
    if loginAttemptType == 1 (reconnect): opcode = 30 (DAT_016e9508), size = varShort
    else: opcode = 16 (DAT_016e9548), size = varShort
else (lobby):
    if loginAttemptType == 1 (reconnect): opcode = 30 (DAT_016e9508), size = varShort
    else: opcode = 19 (DAT_016e9540), size = varShort
```

### Size Encoding

All login packets use varShort size (-2). The packet uses `Packet::pSizeMarker` at the start to reserve 2 bytes for size, then fills in the actual size after all data is written:

```
Position 0: [1B] opcode (14/16/19/30, possibly ISAAC-ciphered for opcode 30)
Position 1: [2B] size placeholder (filled at end)
Position 3+: payload...
```

After writing all payload, the size is calculated and written back:
```
size = (endPos - startPos)
Packet::pT_ushort(packet, size)  // written at the marker position
```

### Login Packet Payload (Lobby Login, loginType != 2)

For `loginAttemptType != 1` (non-reconnect):

```
[Pre-RSA section]
  [4B] client_version    : int (pT_int) = 0x3B2 (946)
  [4B] sub_version       : int (pT_int) = 1

[RSA Block - via CreateLoginRSAPacket]
  (see RSA Block Format below)

[Post-RSA section]
  [1B] has_session_token : byte (p1) = (loginToken == -1) ? 1 : 0
  If has_session_token == 1:
    [string] password    : pStringNoConversion (FUN_00ba36f0, from +0x70)
  Else:
    [8B] login_token     : long (pT_long, from +0x120)

[Client info section]
  [1B] unknown_p1_1      : byte (from client data)
  [1B] unknown_p1_2      : byte (from client data)
  [1B] connection_type   : byte (2=WebSocket, 3=direct socket)
  [2B] screen_width      : ushort (pT_ushort)
  [2B] screen_height     : ushort (pT_ushort)
  [1B] display_mode      : byte (p1)
  [24B] machine_info     : raw bytes (0x18 bytes via FUN_00cc1c70)
  [str] hostname         : string (pStringNoConversion)

[Machine info block]
  [1B] machine_info_len  : byte (p1, computed from FUN_007c76b0)
  [NB] machine_info_data : raw bytes (N = machine_info_len)

[Build info]
  (various FUN_007d3a30 data)
  [4B] build_number      : int (pT_int)

[JS5 archive CRCs]
  [1B] crc_count         : byte (p1, = number of archives)
  [4B * N] archive_crcs  : int[] (pT_int for each archive)

[Trailing strings and flags]
  [str] client_token     : string (pStringNoConversion)
  [4B] unknown_int       : int (pT_int)
  [4B] unknown_int_2     : int (pT_int)
  [str] unknown_string   : string (pStringNoConversion)
  [1B] unknown_flag_1    : byte (p1)
  [1B] unknown_flag_2    : byte (p1)
  [1B] unknown_flag_3    : byte (p1, from FUN_00323cf0)

[Social auth]
  [1B] has_social_auth   : byte (p1, boolean)
```

### Login Packet Payload (Game Login, loginType == 2)

Same structure as lobby but with an additional field:

```
[Pre-RSA section]
  [4B] client_version    : int (pT_int) = 0x3B2 (946)
  [4B] sub_version       : int (pT_int) = 1
  [1B] disconnect_flag   : byte (p1, from LoginManager.disconnectFlag)  <-- GAME ONLY

[RSA Block]
  (same as lobby)

[Post-RSA section]
  (same as lobby)

[Client info, machine info, CRCs, strings, flags]
  (same as lobby)

[Additional game-only fields]
  [1B] unknown_flag      : byte (p1)
  [2B] unknown_ushort    : ushort (pT_ushort, signed as short)

[Social auth]
  [1B] has_social_auth   : byte (p1)
```

### Post-Send: ISAAC Initialization

After the packet is sent and flushed:

```
[Post-send processing]
  FUN_0021aee0(this, packet)    -- finalize packet metadata
  Packet::tinyKeyEncrypt(packet, xteaKey)  -- XTEA encrypt the payload section

  Size is filled in:
    size = (current_pos - start_pos)
    pT_ushort(packet, size)

  ISAAC ciphers are initialized:
    Send cipher = Isaac::Init(xteaKey[0..3])
    Recv cipher = Isaac::Init(xteaKey[0..3] + DAT_00dc8090 delta)

  Delta values (at 0x00dc8090):
    xteaKey[0] + *(int*)0x00dc8090
    xteaKey[1] + *(int*)0x00dc8094
    xteaKey[2] + *(int*)0x00dc8098
    xteaKey[3] + *(int*)0x00dc809c

  Send cipher stored at ServerConnection+0x40
  Recv cipher stored at ServerConnection+0x2B8
```

Transitions to step 90 (0x5A = WAITING_DISALLOW_RESULT).

---

## RSA Block Format (CreateLoginRSAPacket)

**Function**: `CreateLoginRSAPacket` at `0x00248d20`
**Called from**: `SendLoginPacketInner`

### RSA Inner Block (before encryption)

Built by `FUN_0022f5e0` (GenerateXteaKeyAndBuildRsaBlock):

```
[RSA plaintext block]
  [1B] magic             : byte (p1) = 10 (0x0A)
  [4B] xtea_key[0]       : int (pT_int) = Isaac::TakeNextValue() -> stored at +0x48
  [4B] xtea_key[1]       : int (pT_int) = Isaac::TakeNextValue() -> stored at +0x4C
  [4B] xtea_key[2]       : int (pT_int) = Isaac::TakeNextValue() -> stored at +0x50
  [4B] xtea_key[3]       : int (pT_int) = Isaac::TakeNextValue() -> stored at +0x54
  [8B] session_key       : long (pT_long, from LoginManager+0x40)

  [If protocol version == 0x23 (35), additional fields:]
    [4B] extra_key[0]    : int (pT_int, from +0x58)
    [4B] extra_key[1]    : int (pT_int, from +0x5C)
    [4B] extra_key[2]    : int (pT_int, from +0x60)
    [4B] extra_key[3]    : int (pT_int, from +0x64)
```

After writing the XTEA key, the old key at +0x58..0x64 is updated with the new key values.

### Credential Section (in CreateLoginRSAPacket, after XTEA key block)

The credential section depends on login type:

```
[Credential type determination]
  If string at +0xA0 has length 6 (inline) or is long string with length 6:
    If +0xB8 == 0: credential_type = 3
    Else: credential_type = 1
  Else:
    If +0xB9 == 0: credential_type = 2 (anonymous)
    Else: credential_type = 0 (social token)

[Credential data]
  [1B] credential_type   : byte (p1)

  Type 1 or 3 (SSO/token login):
    [3B] account_hash    : medium (3 bytes, big-endian, from strtol of string at +0xA0)
    [1B] padding         : 1 zero byte (position advance)

  Type 2 (anonymous):
    [4B] padding         : 4 zero bytes (position advance)

  Type 0 (social token):
    [4B] social_token    : int (pT_int, from +0xBC)

[Auth credentials -- if protocol version != 0x23]
  [1B] has_password_flag : byte (p1, from +0x140)
  If has_password_flag == 0:
    [str] password       : string (pStringNoConversion, from +0x88)
    [8B] session_nonce_1 : long (pT_long, from +0x130)
    [8B] session_nonce_2 : long (pT_long, from +0x138)
  Else (has_password_flag == 1):
    [str] token_string   : string (pStringNoConversion, from +0x148)
```

### RSA Encryption

After the plaintext block is assembled, it is RSA-encrypted via `FUN_00247e60`:

```
[RSA encryption output format]
  [2B] rsa_block_length  : ushort (pT_ushort) = length of encrypted data + 1
  [1B] zero_byte         : byte = 0
  [NB] rsa_ciphertext    : raw bytes (RSA modpow result)
```

RSA parameters:
- Modulus at `DAT_016e7348`
- Exponent at `DAT_016e7340`

---

## Step 90 (0x5A): Server Login Result

**Direction**: Server -> Client

The server sends a 1-byte result code. However, for certain login providers (protocol version 0x23/0x24), the server first sends a variable-length reason string before the result.

### Simple case (non-0x23 provider)

```
Server sends:
  [1B] result_code : byte
```

The result_code is stored at `LoginManager+0x184`, then dispatched in step 96.

### With reason string (provider 0x23/0x24)

```
Server sends:
  [1B] reason_length : byte
  [NB] reason_string : N bytes (UTF-8 text)
  [1B] result_code   : byte
```

---

## Step 150 (0x96): Handle Login Data (Success)

**Function**: `LoginStepHandleLoginData` at `0x00270e80`
**Login step**: 150 (0x96)
**Direction**: Server -> Client

### Precondition
- Step 140 read 1 byte = payload length, stored at `LoginManager+0xD0`
- That many bytes are now read from the server

### Lobby Login Data (loginType == 1)

```
[First byte]
  [1B] has_totp_update   : byte (if == 1, calls FUN_0021b6b0 to read TOTP data)

[Core login state fields]
  [1B] membership_type   : byte (-> LoginState+0x08)
  [1B] membership_days   : byte (-> LoginState+0x0C)
  [1B] email_validated   : bool (-> LoginState+0x10, == 1)
  [3B] recovery_delay    : medium, signed 24-bit (-> LoginState+0x14)
       Read as: byte*0x10000 + byte*0x100 + byte
       If > 0x7FFFFF: subtract 0x1000000
  [1B] staff_mod_flag    : byte (-> LoginState+0x88, signed)
  [1B] unknown_flag_1    : bool (-> LoginState+0x19, == 1)
  [1B] unknown_flag_2    : bool (-> LoginState+0x1A, == 1)
  [8B] membership_ts     : long (gT_ulong -> LoginState+0x30)

[Time synchronization]
  [1B] time_byte         : byte
  [4B] time_int          : int (FUN_001c1d80 = gT_int)
  -> session_time_offset at LoginState+0x90 = (membership_ts - system_clock_now/1000000)
     - (time_byte << 32) - time_int

[Flags byte]
  [1B] flags             : byte
       bit 0 -> LoginState+0x29
       bit 1 -> LoginState+0x28

[Session info]
  [4B] unknown_1         : int (gT_int -> LoginState+0x40)
  [4B] unknown_2         : int (gT_int -> LoginState+0x44)
  [2B] player_index      : ushort (gT_ushort -> LoginState+0x1C)
  [2B] unknown_3         : ushort (gT_ushort -> LoginState+0x20)
  [2B] unknown_4         : ushort (gT_ushort -> LoginState+0x60)
  [4B] unknown_5         : int (gT_int -> LoginState+0x64)
  [1B] unknown_6         : byte (-> LoginState+0x24)
  [2B] unknown_7         : ushort (gT_ushort -> LoginState+0x38)
  [2B] unknown_8         : ushort (gT_ushort -> LoginState+0x3C)
  [1B] is_members_world  : bool (-> LoginState+0x18, == 1)

[Display name]
  [str] display_name     : string (FUN_001d3900, null-terminated -> LoginState+0x68)

[More fields]
  [1B] unknown_9         : byte (-> LoginState+0x84)
  [4B] unknown_10        : int (gT_int -> LoginState+0x80)
  [2B] world_id          : ushort (gT_ushort, 0xFFFF means -1)
  [str] server_info      : string (FUN_001d3900)
  [2B] screen_width      : ushort (gT_ushort)
  [2B] screen_height     : ushort (gT_ushort)

[Session tokens]
  [8B] session_token_1   : long (gT_ulong -> LoginManager.sessionToken1)
  [8B] session_token_2   : long (gT_ulong -> LoginManager.sessionToken2)
```

### Game Login Data (loginType == 2)

```
[First byte]
  [1B] has_totp_update   : byte (if == 1, calls FUN_0021b6b0)

[Core fields]
  [1B] membership_type   : byte (-> LoginState+0x08)
  [1B] membership_days   : byte (-> LoginState+0x0C)
  [1B] email_validated   : bool (-> LoginState+0x10, == 1)
  [1B] unknown_flag_1    : bool (-> LoginState+0x19, == 1)
  [1B] unknown_flag_2    : bool (-> LoginState+0x1A, == 1)
  [1B] display_name_set  : bool (-> plVar16+1, == 1)
  [2B] unknown_ushort    : ushort (gT_ushort -> LoginState+0x48)
  [1B] is_members        : bool (-> LoginState+0x28, == 1)
  [3B] recovery_delay    : medium (same signed 24-bit as lobby)
  [1B] hd_flag           : bool (-> plVar16+2, toggles HD in ConfigProvider)

[Time sync]
  [str] display_name     : string (FUN_00bb9750, into clientPtr2+0x197b0)
  [2B] time_ushort       : ushort (gT_ushort)
  [4B] time_int          : int (gT_int)
  -> session_time_offset = (time_ushort << 32) + (time_int - system_clock_now/1000000)

[Session tokens]
  [8B] session_token_1   : long (gT_ulong -> LoginManager.sessionToken1)
  [8B] session_token_2   : long (gT_ulong -> LoginManager.sessionToken2)
```

### Post-Read (Common)

After reading login data:
1. LoginState object stored in Client
2. Server connection tick rate counter reset
3. `loginErrorCode` set from client field
4. Stat/inventory tables cleared
5. If session token changed from previous: JS5 disk cache cleared
6. `loginStep = 0` (IDLE)
7. For game login (loginType==2):
   - `normalLoginResult = 2` (SUCCESS)
   - `Client::SetMainState(0x1E)` (GAME_WORLD)
   - Initialize 8 friend chat entries
8. For lobby login (loginType==1):
   - `reconnectLoginResult = 2` or `normalLoginResult = 2`
   - `Client::SetMainState(0x14)` (LOBBY)

---

## Complete Login Flow Summary (Wire Perspective)

### Lobby Login (Happy Path)

```
Client -> Server: [1B] 14                                    (connection type = login)
Server -> Client: [1B] 0 [8B] session_key                   (OK + session key)
Server -> Client: [2B] payload_len                           (XTEA challenge size)
Server -> Client: [NB] xtea_encrypted(challenge_nonce)       (XTEA encrypted challenge)
Server -> Client: [1B] 1                                     (go-ahead)
Server -> Client: [16B] xtea_encrypted(token + nonce)        (login token + session nonce)
Client -> Server: [1B] 19 [2B] size [NB] login_packet       (lobby login packet, XTEA encrypted)
Server -> Client: [1B] result_code                           (login result)
  If result == 2 (SUCCESS):
    Server -> Client: [1B] data_length                       (login data size)
    Server -> Client: [NB] login_data                        (lobby login state)
```

### Game Login (Happy Path)

Same as lobby but:
- Login packet uses opcode **16** instead of 19
- Login packet includes `disconnect_flag` byte before RSA block
- Login data uses game format (different field order)
- After success: additional server client var exchange (steps 250-270)

### Reconnect

- Uses opcode **30** for the login packet
- Same structure as the corresponding login type

---

## Connection Type Byte Disambiguation

The very first byte sent by the client on a new TCP connection determines the protocol:

| Byte | Protocol |
|------|----------|
| 14 | Login handshake |
| 15 | JS5 file service |

The server must read this byte first to route the connection to the correct handler.

---

## XTEA Key Details

### Generation
The 4 XTEA key ints are generated by `Isaac::TakeNextValue()` from the handshake Isaac cipher (at `LoginManager+0x68`). They are stored at `LoginManager+0x48..0x54`.

### Usage
1. **Server challenge decryption** (step 50): XTEA decrypt with key at +0x48
2. **Login token decryption** (step 70): same key
3. **Login packet encryption** (step 80): `tinyKeyEncrypt` with same key
4. **ISAAC cipher seeding** (post step 80): key used directly for send cipher, key+delta for recv cipher

### ISAAC Delta
The receive cipher uses XTEA key values with a constant delta added:
```
recv_key[i] = xtea_key[i] + delta[i]
```
Where delta values are at address `0x00dc8090` (4 consecutive ints).

---

## Function Address Reference (Verified)

| Address | Function |
|---------|----------|
| 0x00221950 | LoginStepInit (step 10) |
| 0x00272010 | LoginStepWaitingSSOKey (step 13) |
| 0x002218e0 | LoginStepWaitingConnectionOpened (step 14) |
| 0x0022ee10 | LoginStepWaitingSSOKeyResponse (step 15) |
| 0x00220f70 | LoginStepWaitingFirstResponse (step 30) |
| 0x00220f10 | LoginStepWaitingSecondResponse (step 40) |
| 0x00220c00 | LoginStepDealWithSecondResponse (step 50) |
| 0x00220900 | LoginStepDealWithThirdResponse (step 70) |
| 0x00263f20 | LoginStepSendLoginPacket (step 80) |
| 0x00263e70 | SendLoginPacketInner |
| 0x00248d20 | CreateLoginRSAPacket |
| 0x0022f5e0 | GenerateXteaKeyAndBuildRsaBlock |
| 0x00247e60 | RSA encrypt (ModPow + encode) |
| 0x00270e80 | LoginStepHandleLoginData (step 150) |
| 0x0022ec60 | LoginStepWaitingSendContinue (step 110) |
| 0x0022e930 | LoginStepWaitingServerClientVar (step 270) |
| 0x0024d530 | LoginStepDealWithFirstResponse (step 96) |
| 0x00182d7d | RegisterAll (LoginProt opcode initialization) |
