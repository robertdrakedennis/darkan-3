# Login Initiation Flow (rs2client rev 946)

**Binary**: rs2client (Ghidra port 8080)
**Verified against**: rs2client rev 946 (stripped)

## Overview

The NXT client does NOT automatically attempt login after JS5 cache downloading.
Login is triggered by one of three paths, all of which ultimately call the internal
function `InitiateLogin` (`FUN_0024c7e0` at `0x0024c7e0`), which sets `loginStep = 10`
to start the login state machine.

## Login Trigger Paths

### Path 1: SSO Auto-Login (JX_* Environment Variables)

This is the path used when the Jagex launcher (rs3linux) passes OAuth credentials
to rs2client via environment variables.

**Trigger chain**:
1. `FUN_002f68a0` (event handler at `0x002f68a0`) receives an event with code `0x400000003`
2. It checks `mainState == 10` (initial loaded state)
3. If true, it queues `FUN_00299fc0` (at `0x00299fc0`) for execution
4. `FUN_00299fc0` calls `FUN_0034b040` (auto-login function at `0x0034b040`)
5. `FUN_0034b040` calls `FUN_004b7310` (HasSSOCredentials check at `0x004b7310`)
6. If SSO credentials exist, logs "Attempting direct login with sso credentials"
   and calls `FUN_0024ce10` -> `FUN_0024c7e0` which sets `loginStep = 10`

**HasSSOCredentials check** (`FUN_004b7310` at `0x004b7310`):
Returns true if ANY of these global strings are non-empty:
- `DAT_016fec90` = access_token (set from `JX_ACCESS_TOKEN` env var)
- `DAT_016fecc8` = session_id (set from `JX_SESSION_ID` env var)
- `DAT_016fece0` = character_id (set from `JX_CHARACTER_ID` env var)

**Environment variables read** (in `LoginManager` constructor at `0x0011e552`):
- `JX_ACCESS_TOKEN` (string at `0x00dc7c52`) -> stored to `DAT_016fec90`
- `JX_REFRESH_TOKEN` (string at `0x00dc7c62`) -> stored to `DAT_016feca8`
- `JX_SESSION_ID` (string at `0x00dc7c73`) -> stored to `DAT_016fecc8`
- `JX_CHARACTER_ID` (string at `0x00dc7c81`) -> stored to `DAT_016fece0`
- `JX_DISPLAY_NAME` (string at `0x00dc7c42`) -> stored via `FUN_001e5c40`

The constructor reads these via `getenv()` wrapper (`FUN_004b75d0` at `0x004b75d0`).
If access_token/refresh_token are present but session_id is empty, it sets
`hasSessionTokens = 1` and uses the OAuth token path.
If session_id/character_id are present but access_token is empty, it sets
`hasSessionTokens = 1` and uses the session token path.

### Path 2: ClientScript-Triggered Login

The NXT client uses CS2 (ClientScript) commands to trigger login from the UI.
Two CS2 commands exist:

- `FUN_003e2a90` at `0x003e2a90`: Lobby login (loginType=1). Pops 3 strings + 1 int
  from the CS2 operand stack (username, password, auth code) and calls
  `FUN_0024c7e0` with loginType=1.

- `FUN_003e3860` at `0x003e3860`: World login (loginType=2). Same signature,
  calls `FUN_0024e3f0` -> `FUN_0024c7e0` with loginType=2.

### Path 3: MainState Observer (State Transition)

`FUN_0024e1e0` at `0x0024e1e0` is registered as a subsystem observer on the Client.
It is called whenever `SetMainState` changes the client's main state.

- When `mainState` transitions TO `0x23` (35): Triggers lobby login via `FUN_0024ce10`
- When `mainState` transitions TO `0x25` (37): Triggers world login via `FUN_0024ce10`

This observer also checks `FUN_0022c3b0` (CanLogin) which verifies:
- The JS5 system state allows login (download state > 0x1e aka 30)
- `loginStep == 0` (no login already in progress)

### Path 4: Direct Login (Username/Password Fallback)

If SSO credentials are absent, `FUN_0034b040` falls back to looking up stored
"username" and "password" from a key-value store. If found, it logs
"Attempting direct login as: %s" and calls `FUN_0024e3f0`.

If neither SSO nor stored credentials exist, it logs:
"No previous direct login attempt found - please login manually the first time"

## The InitiateLogin Function (`FUN_0024c7e0` at `0x0024c7e0`)

This is the central login entry point. All paths converge here.

**Parameters**:
- `param_1`: LoginManager pointer
- `param_2`: loginType (1=lobby, 2=world)
- `param_3`: ServerConnection ref
- `param_4`: username/token string
- `param_5`: password/auth string
- `param_6`: extra auth string
- `param_7`: flag (TOTP related)
- `param_8`: flag
- `param_9`: reconnect flag
- `param_10`: world hop flag
- `param_11`: world ID (-1 for none)

**Key actions**:
1. Sets loginType (field at +0x20)
2. Copies credentials to LoginManager fields
3. Computes a hash of the username for server connection routing
4. Closes any existing server connection
5. Sets `loginStep = 10` (field at +0x10) -- THIS STARTS THE STATE MACHINE
6. Sets `loginDelay = 0` (field at +0xc4)

## Login State Machine Steps

| Step | Hex  | Name | Function Address | Description |
|------|------|------|------------------|-------------|
| 10   | 0x0a | LoginStepInit | 0x00221950 | Cleanup old connection, initiate SSO key request, advance to step 12 then 13 |
| 12   | 0x0c | LoginStepWaitingLoginCredentials | 0x0020f730 | Pauses state machine (returns false) until credentials are set |
| 13   | 0x0d | LoginStepWaitingSSOKey | 0x00272010 | Waits for SSO key/game session token. Makes HTTP requests to Jagex APIs |
| 14   | 0x0e | LoginStepWaitingConnectionOpened | 0x002218e0 | Opens TCP connection to lobby/world server |
| 15   | 0x0f | LoginStepWaitingSSOKeyResponse | 0x0022ee10 | Sends initial login packet (opcode 14) over TCP |
| 30   | 0x1e | LoginStepWaitingFirstResponse | 0x00220f70 | Waits for server's first response byte |
| 40   | 0x28 | LoginStepWaitingSecondResponse | 0x00220f10 | Waits for server's second response |
| 50   | 0x32 | LoginStepDealWithSecondResponse | 0x00220c00 | Processes second response |
| 60   | 0x3c | LoginStepWaitingThirdResponse | 0x00220b90 | Waits for third response |
| 70   | 0x46 | LoginStepDealWithThirdResponse | 0x00220900 | Processes third response |
| 80   | 0x50 | LoginStepSendLoginPacket | 0x00263f20 | Sends the main login packet |
| 90   | 0x5a | LoginStepWaitingDisallowResult | 0x00220830 | Handles disallow result |
| 92   | 0x5c | LoginStepWaitingReasonLength | 0x00252270 | Reads reason string length |
| 96   | 0x60 | LoginStepDealWithFirstResponse | 0x0024d530 | Main response handler |
| 110  | 0x6e | LoginStepWaitingSendContinue | 0x0022ec60 | Waits for continue signal |
| 120  | 0x78 | LoginStepWaitingInQueue | 0x00220720 | Login queue handling |
| 130  | 0x82 | LoginStepWaitingPlayersPacketReconnect | 0x0021fe80 | Reconnect-specific |
| 132  | 0x84 | LoginStepWaitingThirdReasonLength | 0x00252140 | Third reason length |
| 136  | 0x88 | LoginStepDealWithThirdResult | 0x0024d320 | Third result handler |
| 140  | 0x8c | LoginStepWaitingLoginCredentialsLength | 0x0021fe00 | Credentials length |
| 150  | 0x96 | LoginStepHandleLoginData | 0x00270e80 | Processes login data |
| 190  | 0xbe | LoginStepWaitingDisallowResult2 | 0x0024d3f0 | Second disallow |
| 200  | 0xc8 | LoginStepWaitingTempBanned | 0x00221150 | Temp ban handler |
| 210  | 0xd2 | LoginStepWaitingForTOTPPassCode | 0x002206c0 | TOTP auth |
| 220  | 0xdc | LoginStepWaitingURLLength | 0x002205b0 | URL redirect length |
| 230  | 0xe6 | LoginStepWaitingURL | 0x002200a0 | URL redirect data |
| 240  | 0xf0 | LoginStepWaitingHopBlockTime | 0x00220610 | World hop cooldown |
| 250  | 0xfa | LoginStepWaitingServerClientVarLength | 0x00220040 | Server vars length |
| 260  | 0x104 | LoginStepWaitingServerClientVarConfigData | 0x0021ff30 | Server vars data |
| 270  | 0x10e | LoginStepWaitingServerClientVar | 0x0022e930 | Server vars processing |
| 280  | 0x118 | StartRSAPacket | 0x00248610 | Constructs RSA-encrypted login block |
| 290  | 0x122 | CheckLoaded | 0x002348b0 | Checks SSO/game-session token result |
| 300  | 0x12c | LoginStepWaitingReasonLength2 | 0x002207c0 | Second reason length |
| 310  | 0x136 | LoginStepWaitingSocialNetworkTokenLength | 0x00265050 | Social token length |

## SSO Key Flow (Step 13: LoginStepWaitingSSOKey)

This is the most complex step. The client contacts Jagex OAuth APIs before
connecting to the game server.

**Two credential paths**:

### OAuth Access Token Path (JX_ACCESS_TOKEN present)
When `DAT_016fec90` (access_token) is non-empty:
1. Checks if token has expired (compares `DAT_016fecc0` expiry timestamp with current time + 60s)
2. If expired: calls `FUN_00bddf00` (likely token refresh)
3. If valid: calls `FUN_00bddc70` (likely uses existing token)
4. Transitions to step 0x122 (CheckLoaded)

### Session ID Path (JX_SESSION_ID present)
When `DAT_016fecc8` (session_id) and `DAT_016fece0` (character_id) are non-empty:
1. Constructs `"Bearer " + access_token` Authorization header
2. Sets `Content-Type: application/json`, `Accept: application/json`
3. Makes POST to `{base_url}/game-session/v1/tokens` with JSON body `{"accountId": character_id}`
4. Sets `DAT_016fec60 = 5` (request type identifier)
5. Transitions to step 0x122 (CheckLoaded)

### No Credentials Path
When neither is present:
1. Constructs Basic auth from client_id:client_secret (via `FUN_00bae8d0`)
2. Makes POST to `{base_url}/shield/oauth/check_token` with form body `&token={access_token}`
3. Sets `DAT_016fec60 = 1` (request type identifier)
4. Transitions to step 0x122 (CheckLoaded)

### CheckLoaded (Step 0x122) Result Handling
- Result `0` (success): Copies the game session token to LoginManager, advances to step 0x0e (open connection)
- Result `-2` (pending): Wait/retry
- Result `0x57f` (1407): Marks `field_0xd64 = 1` then continues
- Result `-4`: Sets login result to -5 (error)
- Other non-zero: Sets login result to the error code

## Why the Client is NOT Attempting Login

Based on this analysis, the client is not attempting login because:

1. **No JX_* environment variables**: The `run-client.sh` script does NOT set
   `JX_ACCESS_TOKEN`, `JX_SESSION_ID`, or `JX_CHARACTER_ID`. The LoginManager
   constructor reads these via `getenv()` at startup. Without them,
   `hasSessionTokens` stays 0 and `FUN_004b7310` (HasSSOCredentials) returns false.

2. **No stored username/password**: The fallback path looks for previously-stored
   credentials in a key-value store. Since no login has ever succeeded, there are
   no stored credentials.

3. **ClientScript UI not loaded**: The CS2 script login commands require the
   login UI to be rendered and interactive. Without proper lobby interfaces loaded
   from the cache, no CS2 script runs to trigger login.

4. **MainState never reaches 0x23**: The state observer that triggers lobby login
   requires `mainState == 0x23` (35), which requires the client to have completed
   enough initialization (cache loading, UI setup) to enter the lobby state.

## What Needs to Happen for Login

To get the client to attempt login to our server, we need ONE of:

### Option A: Set JX_* Environment Variables (Recommended for Private Server)
Set these env vars before launching rs2client:
```
JX_ACCESS_TOKEN=<any-non-empty-string>
JX_SESSION_ID=<any-non-empty-string>
JX_CHARACTER_ID=<any-non-empty-string>
JX_DISPLAY_NAME=<player-name>
```

The SSO token validation (step 13) will make HTTP requests to Jagex OAuth APIs
(`game-session/v1/tokens` or `shield/oauth/check_token`). For a private server,
we need to either:
- Intercept/redirect these requests to our own auth endpoint, OR
- Patch the client to skip SSO validation, OR
- Serve enough cache data that the client's CS2 scripts can render a login UI

### Option B: Sufficient Cache + CS2 Scripts
If enough cache is downloaded (especially interface definitions and CS2 scripts),
the client may render a login screen that triggers login via CS2 commands.
This requires the JS5 server to serve the interface/script archives correctly.

### Option C: Fake SSO Response
The OAuth HTTP requests go to URLs constructed from global strings:
- `DAT_016fed58` = base URL for `shield/oauth/check_token`
- `DAT_016fed70` = base URL for `game-session/v1/tokens`

These are set during LoginManager construction from client data. If we can
redirect these URLs (via patcher or DNS), we can serve fake OAuth responses
that satisfy the token validation.

## Key Global Variables

| Address | Name | Set From | Purpose |
|---------|------|----------|---------|
| `DAT_016fec60` | ssoRequestType | Constructor/SSO flow | 0=none, 1=check_token, 5=game-session |
| `DAT_016fec68` | httpClient | Constructor | HTTP client for OAuth requests |
| `DAT_016fec70` | gameSessionToken | OAuth response | The game session token string |
| `DAT_016fec88` | tokenResult | OAuth response | Result code from token validation |
| `DAT_016fec90` | accessToken | JX_ACCESS_TOKEN | OAuth access token |
| `DAT_016feca8` | refreshToken | JX_REFRESH_TOKEN | OAuth refresh token |
| `DAT_016fecc0` | tokenExpiry | OAuth response | Token expiry timestamp (epoch seconds) |
| `DAT_016fecc8` | sessionId | JX_SESSION_ID | Jagex session ID |
| `DAT_016fece0` | characterId | JX_CHARACTER_ID | Jagex character/account ID |
| `DAT_016fecf8` | clientId | Constructor | OAuth client ID ("com_jagex_auth_desktop_rs") |
| `DAT_016fed10` | clientSecret | Constructor | OAuth client secret ("public") |
| `DAT_016fed28` | scope | Constructor | OAuth scope ("gamesso.token.create") |
| `DAT_016fed58` | shieldBaseUrl | Constructor | Base URL for shield/oauth endpoints |
| `DAT_016fed70` | gameSessionBaseUrl | Constructor | Base URL for game-session endpoints |
| `DAT_016fed88` | ssoState | Constructor/flow | SSO processing state |
