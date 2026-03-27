# Account Creation Protocol

> **Verified from**: librs2client.so (NXT_BETA_UNSTRIPPED, rev ~890, Ghidra port 8080) for names/structure.
> **Cross-referenced**: rs2client (rev 947-1, Ghidra port 8083) for 947-1 opcode verification.

## Overview

Account creation in the NXT client is handled by `jag::AccountCreationManager`, a standalone subsystem that opens its own TCP connection to the server (separate from the main login connection). It uses a dedicated `LoginProt` opcode (`CREATE_ACCOUNT_CONNECT`, opcode 28 / 0x1C) for the initial handshake and then transitions to the standard `ClientProt`/`ServerProt` packet system (with ISAAC encryption) for the create-time packets.

The client-side UI is driven by CS2 (ClientScript) via the `jag::opcode::AccountCreation` opcode set. Scripts call these opcodes to send packets and read replies.

---

## 1. Connection Handshake

### 1.1 State Machine (`AccountCreationManager::MainLogic`)

The `AccountCreationManager` has an internal state machine at `this+0x28`:

| State | Hex | Description |
|-------|-----|-------------|
| 0 | 0x00 | Idle / not started |
| 10 | 0x0A | Reconnect — opens TCP connection |
| 15 | 0x0F | Connected — sends `CREATE_ACCOUNT_CONNECT` packet |
| 20 | 0x14 | Waiting for server response (1 byte) |
| 30 | 0x1E | Active — ISAAC initialized, ready for ClientProt/ServerProt |

Other fields:
- `this+0x10`: XTEA keys array (4 x int32, 16 bytes)
- `this+0x20`: Tick counter (timeout at 2000)
- `this+0x24`: Retry counter (max 3 retries)
- `this+0x2c`: Result/error code

### 1.2 AccountCreationManager Activation

The `AccountCreationManager` is activated when `MainState` transitions to `0x17` (23). The `ChangeMainState` callback checks `param_2 == 0x17` and resets the internal state fields.

### 1.3 Connection Type: `LoginProt::CREATE_ACCOUNT_CONNECT`

| Field | Value |
|-------|-------|
| Jagex Name | `CREATE_ACCOUNT_CONNECT` |
| Opcode | 28 (0x1C) |
| Size | varShort (-2) |
| LoginProt Label | `0x00a355cc` (unstripped), registered via `LoginProt::InitEntry` in 947-1 |

This is the same opcode value in both revisions (LoginProt opcodes are not scrambled).

### 1.4 Client -> Server: CREATE_ACCOUNT_CONNECT Packet

Sent in state 0x0F. The wire format mirrors the login packet structure:

```
[LoginProt header: opcode 28 + varShort length prefix]
  [short]  0x0000                -- padding/reserved (literal 0)
  [short]  majorVersion          -- e.g. 0x0361 (865) in unstripped, will be server's version
  [short]  1                     -- sub-version (hardcoded to 1)
  [--- RSA block start ---]
  [byte]   10                    -- RSA block magic (0x0A)
  [int]    XTEA key 0            -- 4 random ISAAC seed ints (16 bytes total)
  [int]    XTEA key 1
  [int]    XTEA key 2
  [int]    XTEA key 3
  [int][int][int]...[int]        -- 10 random ints (40 bytes), padding/obfuscation
  [short]  random short          -- additional random padding
  [--- RSA encrypt above ---]
  [RSA-encrypted blob]           -- RSA(above), written as array buffer with length
  [--- XTEA-encrypted section start (after RSA block) ---]
  [string] ssoToken              -- SSO/session token (pStringNoConversion)
  [short]  clientType            -- from client+0xa1c
  [long]   machineInfo           -- two int32s packed as one int64 (from client+0xaec, 0xaf4)
  [string] launcherVersion       -- launcher version string (pStringNoConversion)
  [byte]   affiliateId           -- from client+0xa10 (single byte)
  [byte]   js5ModeWhere          -- from client+0x990 (single byte)
  [bytes]  CRC table             -- array buffer (JS5 CRC validation data)
  [byte]   hasDisplayName        -- 0 if no display name, 1 if present
  [string] displayName           -- only if hasDisplayName=1 (pStringNoConversion)
  [bytes]  hostPlatformStats     -- serialized HostPlatformStats
  [7 bytes padding]              -- alignment padding before XTEA encryption
  [--- XTEA encrypt from after RSA to here ---]
  [short]  encryptedLength       -- big-endian, written at position before XTEA block
```

### 1.5 Server -> Client: Response (1 byte)

In state 0x14, the client reads exactly 1 byte:

| Response | Meaning |
|----------|---------|
| 2 | **Success** — proceed to ISAAC setup |
| Any other | **Failure** — close connection, set error code |

### 1.6 ISAAC Initialization (on success response = 2)

After receiving response byte 2:

1. **Client ISAAC** (for encoding outgoing ClientProt opcodes): seeded with the 4 XTEA keys directly (`keys[0..3]`)
2. **Server ISAAC** (for decoding incoming ServerProt opcodes): seeded with `keys[0]+0x32, keys[1]+0x32, keys[2]+0x32, keys[3]+0x32`

Both ISAAC ciphers are set on the `ServerConnection`. The connection transitions to state 0x1E (active), and the client begins processing ServerProt packets.

---

## 2. ClientProt Packets (Client -> Server)

These are standard game `ClientProt` packets sent over the account creation connection, ISAAC-encrypted.

### 2.1 Packet Table

All names from `jag::ClientProt` namespace in the unstripped binary:

| Jagex Name | Unstripped Address | Description | Payload |
|------------|-------------------|-------------|---------|
| **CREATE_CHECK_EMAIL** | `0x00a393f8` | Check if email is available | XTEA-encrypted: `[short 0x0000][string email][7B pad]` with BE short length prefix |
| **CREATE_CHECK_NAME** | `0x00a392e8` | Check if display name is available | XTEA-encrypted: `[byte 0x00][string name][7B pad]` with byte length prefix |
| **CREATE_ACCOUNT** | `0x00a39510` | Submit account creation | XTEA-encrypted: `[short 0x0000][string email][string password][byte age][bool subscribe][string displayName][7B pad]` with BE short length prefix |
| **CREATE_SUGGEST_NAMES** | `0x00a395b8` | Request suggested display names | No payload (opcode only) |
| **CREATE_LOG_PROGRESS** | `0x00a39430` | Log creation step progress | `[byte stepId]` |
| **SEND_EMAIL_VALIDATION_CODE** | `0x00a39470` | Submit email validation code | `[byte codeLength][string code]` |
| **CHANGE_EMAIL_ADDRESS** | `0x00a39320` | Change email during creation | `[short totalLength BE][string newEmail][string oldEmail]` |
| **ADD_NEW_EMAIL_ADDRESS** | `0x00a39348` | Add email with preferences | `[short totalLength BE][string email][byte flags]` where flags = bit0:opt1, bit1:opt2, bit2:opt3 |

### 2.2 Packet Format Details

#### CREATE_CHECK_EMAIL

Triggered by CS2 opcode `_create_availablerequest` (lambda #5).

Preconditions: `mainState == 0x17` AND `accountCreationState == 0x1E`

```
[opcode] (ISAAC-encrypted)
[short 0x0000]                  -- XTEA length prefix placeholder (overwritten after encryption)
[string email]                  -- pStringUTF8ToCP1252
[7 bytes padding]               -- alignment for XTEA
-- XTEA-encrypted from offset 2 to end --
-- Length prefix (BE short) written at offset 0 = encryptedLength --
```

After sending, sets `acm+0x34 = 0xFFFFFFFD` (-3, "pending").

#### CREATE_CHECK_NAME

Triggered by CS2 opcode `_create_name_availablerequest` (lambda #11).

Preconditions: `mainState == 0x17` AND `accountCreationState == 0x1E`

```
[opcode] (ISAAC-encrypted)
[byte 0x00]                     -- varByte length prefix placeholder
[string name]                   -- pStringUTF8ToCP1252
[7 bytes padding]               -- alignment for XTEA
-- XTEA-encrypted from offset 1 to end --
-- Length prefix (byte) written at offset 0 = encryptedLength --
```

After sending, sets `acm+0x38 = 0xFFFFFFFD` (-3, "pending").

#### CREATE_ACCOUNT

Triggered by CS2 opcode `_create_createrequest` (lambda #7).

Preconditions: `mainState == 0x17` AND `accountCreationState == 0x1E`

Pops from CS2 stack: 2 ints (age, subscribe) + 3 strings (email, password, displayName).

```
[opcode] (ISAAC-encrypted)
[short 0x0000]                  -- XTEA length prefix placeholder
[string email]                  -- pStringUTF8ToCP1252
[string password]               -- pStringUTF8ToCP1252
[byte age]                      -- age value (if < 13, sets under13 flag)
[bool subscribe]                -- 1 if subscribe==1, else 0
[string displayName]            -- pStringUTF8ToCP1252
[7 bytes padding]
-- XTEA-encrypted, BE short length prefix --
```

After sending, if age < 13, sets `acm+0x60 = 1` (under13 flag).
Sets `acm+0x30 = 0xFFFFFFFD` (-3, "pending").

#### CREATE_SUGGEST_NAMES

Triggered by CS2 opcode `_create_suggest_name_request` (lambda #12).

No payload — opcode only. No encryption.

After sending, sets `acm+0x3c = 0xFFFFFFFD` (-3, "pending") and clears the suggestion string.

#### CREATE_LOG_PROGRESS

Triggered by CS2 opcode `_create_step_reached` (lambda #9).

Pops 1 int from CS2 stack (step ID).

```
[opcode] (ISAAC-encrypted)
[byte stepId]                   -- the creation step reached
```

#### SEND_EMAIL_VALIDATION_CODE

Triggered by CS2 opcode `_email_validation_submit_code` (lambda #17).

Pops 1 string from CS2 stack (validation code).

Preconditions: `mainState == 0x14 OR 0x17`

```
[opcode] (ISAAC-encrypted)
[byte codeLength]               -- number of UTF8 characters + 1 (or 1 if empty)
[string code]                   -- pStringUTF8ToCP1252
```

#### CHANGE_EMAIL_ADDRESS

Triggered by CS2 opcode `_email_validation_change_address` (lambda #18).

Pops 2 strings from CS2 stack (new email, old email).

Preconditions: `mainState == 0x14 OR 0x17`

```
[opcode] (ISAAC-encrypted)
[short totalLength BE]          -- combined char count of both strings
[string newEmail]               -- pStringUTF8ToCP1252
[string oldEmail]               -- pStringUTF8ToCP1252
```

#### ADD_NEW_EMAIL_ADDRESS

Triggered by CS2 opcode `_email_validation_add_new_address` (lambda #19).

Pops 3 ints + 1 string from CS2 stack (opt1, opt2, opt3, email).

Preconditions: `mainState == 0x14 OR 0x17`

```
[opcode] (ISAAC-encrypted)
[short totalLength BE]          -- char count + overhead
[string email]                  -- pStringUTF8ToCP1252
[byte flags]                    -- bitmask: bit0 = opt1==1, bit1 = opt2==1, bit2 = opt3==1
```

---

## 3. ServerProt Packets (Server -> Client)

These are received by the packet handlers registered in `jag::packethandlers::Lobby::Lobby`.

### 3.1 Packet Table (Unstripped Binary, rev ~890)

From `jag::ServerProt` namespace labels and Lobby handler lambdas:

| Jagex Name | Unstripped Address | Lambda | Old Opcode | Size | Description |
|------------|-------------------|--------|------------|------|-------------|
| **CREATE_CHECK_EMAIL_REPLY** | `0x00a36fc0` | #2 | 0x60 | 1 (fixed) | Email availability response |
| **CREATE_ACCOUNT_REPLY** | `0x00a36800` | #3 | 0x80 | 1 (fixed) | Account creation result |
| **CREATE_CHECK_NAME_REPLY** | `0x00a36cc0` | #4 | 0x6C | 1 (fixed) | Name availability response |
| **CREATE_SUGGEST_NAME_ERROR** | `0x00a35f40` | #5 | 0xA3 | 1 (fixed) | Name suggestion error |
| **CREATE_SUGGEST_NAME_REPLY** | `0x00a35840` | #6 | 0xBF | varShort | Suggested name response |

### 3.2 Rev 947-1 Opcodes

From `lobby-serverprot-table.md`, verified handlers in the stripped binary:

| Jagex Name | 947-1 Opcode | Size | Handler Address |
|------------|-------------|------|-----------------|
| **CREATE_CHECK_EMAIL_REPLY** | 0x85 (133) | 1 (fixed) | `FUN_00212d90` |
| **CREATE_CHECK_NAME_REPLY** | 0xCC (204) | 1 (fixed) | `FUN_00212c80` |
| **CREATE_ACCOUNT_REPLY** | NOT FOUND | 1 (fixed) | No matching handler in 947-1 |
| **CREATE_SUGGEST_NAME_ERROR** | NOT FOUND | 1 (fixed) | No matching handler in 947-1 |
| **CREATE_SUGGEST_NAME_REPLY** | NOT FOUND | varShort | No matching handler in 947-1 |

**NOTE**: Only 2 of the 5 account creation ServerProt handlers were found in rev 947-1. The other 3 (CREATE_ACCOUNT_REPLY, CREATE_SUGGEST_NAME_ERROR, CREATE_SUGGEST_NAME_REPLY) appear to have been removed, possibly because Jagex moved account creation to a web-based flow by rev 946+.

### 3.3 Handler Payload Formats

#### CREATE_CHECK_EMAIL_REPLY

Reads 1 byte, validates it against bitmask `0x1800063`, stores result at `acm+0x34`.

```
[byte result]                   -- email check result code
```

Result validation: `value + 3 < 25` AND `(1 << (value+3)) & 0x1800063 != 0`

Valid result values (where the bitmask check passes):
- 0: Available / OK
- 2: Already taken / in use
- 3: Invalid format
- 18: Rate limited / too many requests
- 19: Server error
- Anything failing the bitmask check: stored as 3 (default/error)

The client stores the result at `acm+0x34`, which is read by CS2 opcode `_create_email_validate_reply` (lambda #3).

#### CREATE_CHECK_NAME_REPLY

Reads 1 byte, validates against bitmask `0xFE3`, stores result at `acm+0x38`.

```
[byte result]                   -- name check result code
```

Validation: `value + 3 < 12` AND `(1 << (value+3)) & 0xFE3 != 0`

Valid result values:
- 0: Available / OK
- 2: Already taken
- 3: Invalid name
- 4: Inappropriate
- 5-8: Various specific errors
- Anything failing: stored as 3 (default/error)

Read by CS2 opcode `_create_name_validate_reply` (lambda #14).

#### CREATE_ACCOUNT_REPLY

Reads 1 byte, validates against bitmask `0x23E01803FE3`, stores result at `acm+0x30`.

```
[byte result]                   -- account creation result code
```

Validation: `value + 3 < 42` AND `(1 << (value+3)) & 0x23E01803FE3 != 0`

Valid result values (bitmask set bits, shifted by -3):
- 0: Success
- 2-8: Various validation errors
- 21: Email already registered
- 33-37: Additional error codes
- Anything failing: stored as 3 (default/error)

Read by CS2 opcode `_create_reply` (lambda #2).

#### CREATE_SUGGEST_NAME_ERROR

Reads 1 byte, validates against bitmask `0xE3`, stores result at `acm+0x3c`, clears suggestion string at `acm+0x40`.

```
[byte result]                   -- error code
```

Validation: `value + 3 < 8` AND `(1 << (value+3)) & 0xE3 != 0`

Valid values: 0, 2, 4 (other values stored as 3).

#### CREATE_SUGGEST_NAME_REPLY

Reads a CP1252 string. Sets `acm+0x3c = 2`, stores the string at `acm+0x40`.

```
[string suggestedName]          -- CP1252 null-terminated string
```

Read by CS2 opcode `_create_suggest_name_reply` (lambda #13), which pushes both the result code (field +0x3c) and the suggestion string (field +0x40) onto the CS2 stack.

---

## 4. CS2 (ClientScript) Opcode Summary

All opcodes registered in `jag::opcode::AccountCreation::AccountCreation(jag::Client&)`:

| Lambda | CS2 Opcode Name | Direction | Description |
|--------|----------------|-----------|-------------|
| #1 | `_create_get_email` | Read | Push SSO email string onto CS2 stack |
| #2 | `_create_reply` | Read | Push CREATE_ACCOUNT_REPLY result (int) |
| #3 | `_create_email_validate_reply` | Read | Push CREATE_CHECK_EMAIL_REPLY result (int) |
| #4 | `_create_connect_reply` | Read | Push connection result code (int) |
| #5 | `_create_availablerequest` | Send | Send CREATE_CHECK_EMAIL packet |
| #6 | `_create_connectrequest` | Action | Trigger connection to server (state transition to 0x17) |
| #7 | `_create_createrequest` | Send | Send CREATE_ACCOUNT packet |
| #8 | `_create_setunder13` | Action | Set under-13 flag + log telemetry |
| #9 | `_create_step_reached` | Send | Send CREATE_LOG_PROGRESS packet |
| #10 | `_create_under13` | Read | Push under-13 flag (bool as int) |
| #11 | `_create_name_availablerequest` | Send | Send CREATE_CHECK_NAME packet |
| #12 | `_create_suggest_name_request` | Send | Send CREATE_SUGGEST_NAMES packet |
| #13 | `_create_suggest_name_reply` | Read | Push suggestion result (int) + name (string) |
| #14 | `_create_name_validate_reply` | Read | Push CREATE_CHECK_NAME_REPLY result (int) |
| #15 | `_notify_accountcreated` | Action | No-op in the decompiled code |
| #16 | `_notify_accountcreatestarted` | Action | No-op in the decompiled code |
| #17 | `_email_validation_submit_code` | Send | Send SEND_EMAIL_VALIDATION_CODE packet |
| #18 | `_email_validation_change_address` | Send | Send CHANGE_EMAIL_ADDRESS packet |
| #19 | `_email_validation_add_new_address` | Send | Send ADD_NEW_EMAIL_ADDRESS packet |

---

## 5. State Machine: Account Creation Flow

```
[User clicks "Create Account" in lobby UI]
        |
        v
CS2: _create_connectrequest (lambda #6)
  -> Sets MainState = 0x17 (23)
  -> AccountCreationManager activates
        |
        v
State 0x0A: Open TCP connection to lobby server
        |
        v
State 0x0F: Send CREATE_ACCOUNT_CONNECT (LoginProt opcode 28)
  Packet: version fields + RSA block (XTEA keys) + XTEA-encrypted section
        |
        v
State 0x14: Wait for server response
  Server sends: [1 byte]
    2 = success -> init ISAAC -> state 0x1E
    other = failure -> retry or error
        |
        v
State 0x1E: Active (ISAAC-encrypted ClientProt/ServerProt)
  Client drives creation via CS2 scripts:
        |
        +-- CS2: _create_availablerequest -> CREATE_CHECK_EMAIL
        |   Server replies: CREATE_CHECK_EMAIL_REPLY (1 byte result)
        |   CS2: _create_email_validate_reply reads result
        |
        +-- CS2: _create_name_availablerequest -> CREATE_CHECK_NAME
        |   Server replies: CREATE_CHECK_NAME_REPLY (1 byte result)
        |   CS2: _create_name_validate_reply reads result
        |
        +-- CS2: _create_suggest_name_request -> CREATE_SUGGEST_NAMES
        |   Server replies: CREATE_SUGGEST_NAME_REPLY (string) or CREATE_SUGGEST_NAME_ERROR (byte)
        |   CS2: _create_suggest_name_reply reads result
        |
        +-- CS2: _create_createrequest -> CREATE_ACCOUNT (email + password + age + subscribe + name)
        |   Server replies: CREATE_ACCOUNT_REPLY (1 byte result)
        |   CS2: _create_reply reads result
        |
        +-- CS2: _create_step_reached -> CREATE_LOG_PROGRESS (telemetry only)
        |
        +-- CS2: _email_validation_submit_code -> SEND_EMAIL_VALIDATION_CODE
        |
        +-- CS2: _email_validation_change_address -> CHANGE_EMAIL_ADDRESS
        |
        +-- CS2: _email_validation_add_new_address -> ADD_NEW_EMAIL_ADDRESS
```

---

## 6. Rev 947-1 Specific Notes

### 6.1 LoginProt Opcodes (Unchanged)

LoginProt opcodes are NOT scrambled between revisions. `CREATE_ACCOUNT_CONNECT` remains opcode 28 (0x1C) with varShort size in 947-1.

947-1 LoginProt registration (from `RegisterAll` at `0x00181e10`):

| Opcode | Size | Name |
|--------|------|------|
| 14 (0x0E) | 0 | INIT_GAME_CONNECTION |
| 15 (0x0F) | varByte | Legacy/StartRSA |
| 16 (0x10) | varShort | GAMELOGIN |
| 19 (0x13) | varShort | LOBBYLOGIN |
| 23 (0x17) | 4 | Unknown |
| 24 (0x18) | varByte | Unknown |
| 26 (0x1A) | 0 | GAMELOGIN_CONTINUE |
| 27 (0x1B) | 0 | Unknown |
| 28 (0x1C) | varShort | **CREATE_ACCOUNT_CONNECT** |
| 29 (0x1D) | varShort | Unknown |
| 30 (0x1E) | varShort | SOCIAL_NETWORK_LOGIN / Reconnect |
| 31 (0x1F) | 4 | Unknown |

### 6.2 ClientProt Opcodes (Scrambled, NOT YET IDENTIFIED)

The 8 account creation ClientProt packets exist in 947-1 but their opcodes have been scrambled. They have not been individually identified in the 947-1 ClientProt table. The client sends them via `MakeClientMessage<jag::ClientProt>`, referencing global label addresses. To find the 947-1 opcodes, one must locate the data addresses referenced in the account creation lambda functions within the stripped binary and read the opcode int32 at each address.

Known unstripped -> 947-1 correspondences to find:
- `CREATE_CHECK_EMAIL` (0x00a393f8) -> ?
- `CREATE_CHECK_NAME` (0x00a392e8) -> ?
- `CREATE_ACCOUNT` (0x00a39510) -> ?
- `CREATE_SUGGEST_NAMES` (0x00a395b8) -> ?
- `CREATE_LOG_PROGRESS` (0x00a39430) -> ?
- `SEND_EMAIL_VALIDATION_CODE` (0x00a39470) -> ?
- `CHANGE_EMAIL_ADDRESS` (0x00a39320) -> ?
- `ADD_NEW_EMAIL_ADDRESS` (0x00a39348) -> ?

### 6.3 Missing ServerProt Handlers in 947-1

Only 2 of the 5 account creation ServerProt handlers were found in 947-1:
- **CREATE_CHECK_EMAIL_REPLY**: opcode 0x85 (133), size 1
- **CREATE_CHECK_NAME_REPLY**: opcode 0xCC (204), size 1

The following 3 handlers are ABSENT from the 947-1 `BindHandlers`:
- **CREATE_ACCOUNT_REPLY**
- **CREATE_SUGGEST_NAME_ERROR**
- **CREATE_SUGGEST_NAME_REPLY**

This strongly suggests that by rev 947, Jagex had partially migrated account creation to a web-based flow, retaining only the email/name validation checks in-client.

### 6.4 Timeout and Retry Behavior

- The `AccountCreationManager` has a **2000-tick timeout** (field `this+0x20`). If no response is received within 2000 ticks, the connection is closed.
- On connection failure, the manager **retries up to 3 times** (field `this+0x24`). After 3 failures, it sets error code `0xFFFFFFFB` (-5) and returns to idle state.
- The `ConnectFailure` function handles both timeout and connection-drop scenarios identically.

---

## 7. Server Implementation Notes

### 7.1 Minimal Implementation for Login Flow

If the server does NOT need to support in-client account creation (e.g., accounts are created via a web portal), the server only needs to:

1. **Recognize connection type 28 (0x1C)** in the connection router
2. **Respond with byte value 2** (success) after reading the handshake packet
3. **Initialize ISAAC ciphers** for the connection
4. **Handle CREATE_CHECK_EMAIL and CREATE_CHECK_NAME** ClientProt packets (reply with result byte 0 = "available" or appropriate error)
5. **Ignore other account creation ClientProt packets** gracefully

### 7.2 Full Implementation

For full in-client account creation:

1. Parse the `CREATE_ACCOUNT_CONNECT` handshake (RSA + XTEA, same structure as login)
2. Set up ISAAC ciphers
3. Handle all 8 ClientProt packets
4. Send appropriate ServerProt reply packets
5. On successful CREATE_ACCOUNT, create the account in the database and reply with result 0

### 7.3 XTEA Encryption on Account Creation Packets

Several ClientProt packets (CREATE_CHECK_EMAIL, CREATE_CHECK_NAME, CREATE_ACCOUNT) use **XTEA encryption** on their payload. The XTEA keys are the 4 random ints generated during the handshake and stored at `acm+0x10`. The server must decrypt these payloads using the same keys from the RSA block.

The encrypted section starts after a length prefix (short for CREATE_CHECK_EMAIL/CREATE_ACCOUNT, byte for CREATE_CHECK_NAME) and extends to the end of the packet. 7 bytes of padding are added before encryption to ensure alignment.
