# Ghidra 947-3 Data Type Application Summary

Binary: `rs2client.947-3` (port 8081)
Date: 2026-04-13

## Data Types Available

95 structs and 6 enums were imported from the 947-1 Ghidra project into the 947-3 database.

### Enums Verified (all correct, no changes needed)

| Enum | Size | Values | Status |
|------|------|--------|--------|
| `jag::LoginStep` | 4B | 37 values (IDLE=0 through WAITING_SOCIAL_TOKEN_LENGTH=310) | OK |
| `jag::LoginResult` | 4B | 15 values (OK_EXCHANGE_DATA=0 through LOGIN_ERROR_66=66) | OK |
| `jag::PlayerExtendedInfoFlags` | 4B | 23 values (APPEARANCE=1 through UNKNOWN_0x4000000=67108864) | OK |
| `jag::MainState` | 4B | 6 values (INITIAL=0 through RECONNECT=35) | OK |
| `jag::CS2ReturnCode` | 4B | Present | OK |
| `jag::GraphNodeFlags` | 2B | Present | OK |

## Struct Applications

### Highest Priority (Protocol-Critical)

#### 1. jag::ProtEntry (64B) -- applied at vector control structure
- Applied `ProtEntry` at `0x016ee0a0` (g_serverProtVector control block)
- Also applied to subsequent vector slots (entries 0-27 at the vector address range)
- Named data label: `jag::ServerProt::g_serverProtVector`
- Note: The vector is `eastl::vector<ServerProt*>`, so the actual entries are scattered in .bss

#### 2. jag::ServerProt (48B) -- applied to ALL 218 entry instances
- All 218 ServerProt entry addresses typed with `ServerProt` struct
- Opcodes 0x00-0xD9 (main game/lobby packets) at addresses throughout `0x016eb520`-`0x016ed460` and `0x01701020`-`0x01702d60`
- 18 additional WorldData/LoginProt response entries also typed (at `0x017024a0`-`0x01702c60`)

#### 3. jag::LoginMgr (3456B) -- struct verified, not applied to memory
- Struct definition verified correct with 20 fields
- The LoginMgr is dynamically allocated (not at a fixed global address)
- Constructor at `0x0011e3aa` creates it with `param_2` as the Client pointer
- ISAAC cipher allocated at offset 0x68 (isaacPtr), 0x810 bytes = 2064B matching `Isaac.conflict`

#### 4. jag::Isaac.conflict (2064B) -- struct verified
- 6 fields: randcnt, randrsl[256], randmem[256], randa, randb, randc
- Dynamically allocated in LoginManager constructor via `operator_new(0x810)`
- Isaac::Init at `0x00c275e0`, Generate at `0x00c26fc0`, TakeNextValue at `0x00c272b0`

#### 5. jag::Client (105280B) -- struct verified, not applied to memory
- 19 fields verified including mainState at offset 0x19B28, loggedInPlayer at 0x19B30
- Client is dynamically allocated, no fixed global address found
- Accessed through indirect pointer chains from handler context

### High Priority (Game Systems)

#### 6. jag::InterfaceComponent (448B) -- struct verified
- 14 fields: interfaceId, componentId, slotId, componentType, text, spriteId, children, etc.

#### 7. jag::InterfaceManager (1112B) -- struct verified
- 6 fields: vtable, clientPtr, interfaceListPtr, buttonQueue vector

#### 8. jag::Friend (120B) -- struct verified
- 7 fields: displayName, previousName, notes, timestamp, world, rank, fcRank

#### 9. jag::FriendIgnore (80B) -- struct verified
- 3 fields: displayName, previousName, notes

#### 10. jag::ClanSettings (240B) -- struct verified
- 16 fields: settingsId, clanName, creationDate, members vector, banned vector, etc.

#### 11. jag::ClanChannelUser (80B) -- struct verified
- 5 fields: displayName, previousDisplayName, world, rank, clanName

#### 12. jag::ServerConnection.conflict (752B) -- struct verified
- 22 fields: vtable, socketPtr, currentOpcode, payloadSize, isaac, packet buffer, etc.

#### 13. jag::StatEntry (24B) / jag::StatTable (24B) -- structs verified
- StatEntry: infoPtr, experience, realLevel, currentLevel
- StatTable: clientPtr, tableSize, tableBegin

## Function Signatures Applied

### Core Infrastructure
| Function | Address | Prototype |
|----------|---------|-----------|
| RegisterAll | `0x00181e10` | `void RegisterAll(void)` |
| BindHandlers | `0x001185aa` | `void BindHandlers(void)` |
| LoginManager (ctor) | `0x0011e3aa` | `void LoginManager(void* thisPtr, long clientPtr)` |
| SetMainState | `0x002a8350` | `void SetMainState(long clientPtr, int newState)` |
| TcpIn | `0x001cd240` | `void TcpIn(long thisPtr, long connectionParam)` |

### Login Step Functions (all 32 take `long loginMgrPtr`)
| Function | Address |
|----------|---------|
| LoginStepInit | `0x00228910` |
| LoginStepWaitingSSOKey | `0x0027f3e0` |
| LoginStepWaitingConnectionOpened | `0x002288a0` |
| LoginStepWaitingSSOKeyResponse | `0x00239b30` |
| LoginStepWaitingFirstResponse | `0x00227f30` |
| LoginStepWaitingSecondResponse | `0x00227ed0` |
| LoginStepDealWithSecondResponse | `0x00227bc0` |
| LoginStepWaitingThirdResponse | (via DealWith) |
| LoginStepDealWithThirdResponse | `0x002278c0` |
| LoginStepWaitingLoginCredentials | `0x00217010` |
| LoginStepSendLoginPacket | `0x00270940` |
| LoginStepWaitingDisallowResult | `0x002277f0` |
| LoginStepWaitingReasonLength | `0x00255cc0` |
| LoginStepDealWithFirstResponse | `0x0025b640` |
| LoginStepWaitingSendContinue | `0x00239980` |
| LoginStepWaitingInQueue | `0x002276e0` |
| LoginStepWaitingPlayersPacketReconnect | `0x00226e40` |
| LoginStepWaitingThirdReasonLength | `0x00255b90` |
| LoginStepDealWithThirdResult | `0x0025b430` |
| LoginStepWaitingLoginCredentialsLength | `0x00226dc0` |
| LoginStepHandleLoginData | `0x0027e250` |
| LoginStepWaitingDisallowResult2 | `0x0025b500` |
| LoginStepWaitingTempBanned | `0x00228110` |
| LoginStepWaitingForTOTPPassCode | `0x00227680` |
| LoginStepWaitingURLLength | `0x00227570` |
| LoginStepWaitingURL | `0x00227060` |
| LoginStepWaitingHopBlockTime | `0x002275d0` |
| LoginStepWaitingServerClientVarLength | `0x00227000` |
| LoginStepWaitingServerClientVarConfigData | `0x00226ef0` |
| LoginStepWaitingServerClientVar | `0x00239650` |
| LoginStepWaitingReasonLength2 | `0x00227780` |
| LoginStepWaitingSocialNetworkTokenLength | `0x00271a70` |
| LoginStepWaitingSocialNetworkToken | `0x002502e0` |

### RSA / Crypto
| Function | Address | Prototype |
|----------|---------|-----------|
| CreateLoginRSAPacket | `0x0024fe10` | `undefined8 CreateLoginRSAPacket(long loginMgrPtr)` |
| StartRSAPacket | `0x0024f700` | `undefined8 StartRSAPacket(long loginMgrPtr)` |
| CheckLoaded | `0x0023f5c0` | `undefined8 CheckLoaded(long loginMgrPtr)` |
| Isaac::Init | `0x00c275e0` | `void Init(void* thisPtr, void* seeds)` |
| Isaac::Generate | `0x00c26fc0` | `void Generate(void* thisPtr)` |
| Isaac::TakeNextValue | `0x00c272b0` | `int TakeNextValue(void* thisPtr)` |

### Packet Handlers
| Function | Address | Prototype |
|----------|---------|-----------|
| UpdateStat | `0x0018f8f0` | `undefined* UpdateStat(long* thisPtr, long packetPtr)` |

## Decompiler Comments Added

| Address | Comment Summary |
|---------|----------------|
| `0x016ee0a0` | g_serverProtVector: 218 ProtEntry structs, used by TcpIn |
| `0x00181e10` | RegisterAll: Initializes ClientProt (130) and ServerProt (218) entries |
| `0x001185aa` | BindHandlers: Assigns handler function pointers to ServerProt entries |
| `0x00270940` | LoginStepSendLoginPacket: step 0x50, calls RSA packet builder |
| `0x0027e250` | LoginStepHandleLoginData: step 0x96, processes login response data |
| `0x0024fe10` | CreateLoginRSAPacket: Builds RSA-encrypted login block |
| `0x0018f8f0` | UpdateStat: Reads xp, stat index, boosted level from packet |
| `0x0011e3aa` | LoginManager ctor: Creates LoginMgr, Isaac cipher, registers all login steps |
| `0x00c275e0` | Isaac::Init: Initialize ISAAC PRNG from 4 seed values |
| `0x00c272b0` | Isaac::TakeNextValue: Returns next random, calls Generate when exhausted |

## Key Findings

1. **Address parity**: All function addresses from 947-1 are identical in 947-3 (same binary layout, functions already named)
2. **ServerProt vector**: At `0x016ee0a0` is an `eastl::vector<ServerProt*>`, not a flat array. Individual entries are scattered across .bss
3. **Client global**: No single fixed global pointer to jag::Client found. The Client instance is accessed through indirect pointer chains (e.g., handler context -> connection -> client offset). The Client struct is dynamically allocated.
4. **LoginMgr**: Also dynamically allocated. Constructor takes a Client pointer at param_2, stores it at offsets 0x08 and 0x18.
5. **Isaac allocation**: 0x810 bytes = 2064, matching the Isaac.conflict struct exactly. Allocated in LoginManager ctor, stored at LoginMgr offset 0x68.

## Not Applied (no fixed address found)

These structs are correct but could not be applied to fixed memory addresses because instances are dynamically allocated:
- `jag::Client` (105280B) -- heap allocated
- `jag::LoginMgr` (3456B) -- heap allocated
- `jag::Isaac.conflict` (2064B) -- heap allocated
- `jag::InterfaceManager` (1112B) -- heap allocated
- `jag::InterfaceComponent` (448B) -- heap allocated
- `jag::ServerConnection.conflict` (752B) -- heap allocated
- `jag::Friend` (120B), `jag::FriendIgnore` (80B) -- vector elements
- `jag::ClanSettings` (240B), `jag::ClanChannelUser` (80B) -- vector elements
- `jag::StatEntry` (24B), `jag::StatTable` (24B) -- dynamic

These types are still useful as parameter/variable types in function signatures and for manual annotation during decompilation.
