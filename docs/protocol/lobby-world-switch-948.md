# Lobby → World transfer & world-login flow (RS3 948)

> **Scope.** This unblocks the path: patched NXT client reaches the **lobby** but
> cannot **enter a world**. It is a darkan-targeted implementation spec sourced
> from the 948-5 RE docs under `~/projects/reclass-data/docs/binary/boot/` plus a
> live cache decode of the lobby interfaces. Every claim cites its source.
>
> **Target build:** Linux `rs2client.948-5` (the only live build). Opcodes are
> 948-specific — bind by behaviour, not number.
>
> **Owner for implementation:** `networking-protocol-engineer`.
> **Source docs:**
> - `boot/05-worldlist-switch.md` — worldlist (op 216), SET_WORLD_TARGET (op 212), SWITCH_WORLD (op 213), `SetMainState(0x25)` hop.
> - `boot/02-login-statemachine.md`, `03-login-auth-packets.md`, `04-connection.md` — login handshake the world server must re-handle.
> - `boot/06-scene-worldinit.md`, `07-state-sync.md`, `08-flows.md`, `00-overview.md` — post-login world-init.
> - `ui-interfaces.md`, `cs2-graph/02-interface-flows.md` — lobby interface flow.

---

## 0. TL;DR for the implementer

1. **Trigger verdict (CQ1):** the lobby "enter world" action is **interface 906,
   component 81 — `useOption = "Play Now"`** (decoded from the live cache, §1). The
   code's hardcoded `906 / 32` is wrong (32 is a world-list "Select" row). Fire
   `SET_WORLD_TARGET` (212) **then** `SWITCH_WORLD` (213) on a click of `906/81`.
   The world-row "Select" clicks (`906/3`, sub-list `820/13`) are world *picking*,
   not *entering* — do not switch on them.
2. **World-login handshake (CQ2):** `SWITCH_WORLD` drives `SetMainState(0x25)`,
   which **re-runs the entire login handshake** against the new host:port in
   **world mode** (`mode = 2`). It is byte-identical to the lobby login except (a)
   the inner login opcode is **LOGIN(16)/RECONNECT(18)** not LOBBY(19), and (b)
   world mode writes **one extra leading byte** (`has-extra`, `LoginManager+0xec`)
   before the RSA block. The darkan `WorldServer` already implements this.
3. **Minimal world-init (CQ3):** after login result `2`, the smallest set to leave
   the loading screen is: login-data block → **REBUILD_NORMAL_SIMPLE (op 81)** with
   a region + packed corner coords → mandatory varps/varcs → **SET_READY_FLAG (op
   75)** → first **PLAYER_INFO** (and NPC_INFO). `WorldServer.sendWorldLoginCore`
   already sends a superset; the only correctness risk is the rebuild opcode/byte
   layout (§4, flagged).

**Top 3 implementation steps** are in §6.

---

## 1. CQ1 — The world-select / "Play" trigger (VERDICT)

### Verdict

> **The "Play / enter world" action is interface `906`, component `81`, whose
> `useOption` is `"Play Now"`.** Fire the lobby→world switch when an `IF_BUTTON`
> click arrives with `interfaceId == 906 && componentId == 81`.

### How this was determined (cache decode — direct evidence)

Decoded interfaces 906 (lobby parent) and 820 (a lobby sub-interface) straight from
the live cache at `data/cache` via the darkan `InterfaceDecoder`
(index 3 = INTERFACES; each component is a file in archive = interfaceId). Key rows
(`useOption` is the right-click/primary action label baked into the component def):

```
=== interface 906 : 174 components ===
  [906:62] type=0 useOpt='World select'  clickable=true     <- opens the world picker
  [906:81] type=0 useOpt='Play Now'      clickable=true     <- ENTER WORLD  ◀──────
  [906:94] type=5 info=[8799,"Your selected favourite worlds",...]  <- favourites panel
  [906:2/3/31..36/95..110] type=0 useOpt='Select' clickable=true    <- world-list ROW hotspots
  [906:122/125/128.. ]      type=10 (list)                          <- world-list list widgets
=== interface 820 : 29 components ===
  [820:12],[820:13] type=10 (list) setting=0x10001                  <- sub-list rows
```

The live capture's click sequence for an account in lobby interface 906 was
**`906/81`, then `906/3`, then `820/13`**. Mapping against the decode:

| Click | Component | Decoded meaning | Role |
|---|---|---|---|
| `906/81` | `useOption="Play Now"` | the enter-world button | **the trigger** |
| `906/3` | `useOption="Select"`, one of the world-list row hotspots | pick/highlight a world row | world *selection* (client-side) |
| `820/13` | `type=10` list row inside sub-interface 820 | sub-list row select/confirm | world *selection* (client-side) |

So the user **picks** a world (the `Select` rows / `820` sub-list) and **enters**
it with **Play Now** (`906/81`). The exact ordering in the capture (`81` first) is
incidental UI behaviour; the load-bearing fact is that `906/81` is the only
component labelled with an enter-world action.

### Why the *server* must drive the switch (not the client)

All of `906/81`, `906/3`, `906/62`, `820/13` decode with **empty CS2 handler
arrays** — no `mousePressedHandler` / `mouseReleasedHandler` / `stateChange` /
`onop` script is baked into the component. (Verified: re-dumping every one of the
20 handler slots for these components returned all-empty.) The only thing they
carry is the `useOption` label. Therefore:

- the click is **delivered to the server** as an `IF_BUTTON` packet (the size-8
  click family), and
- the world target is **not** selected by a self-contained client CS2 script.

This matches `ui-interfaces.md §5`: clickable components route the click to the
server via `IfButtonX`/`IfButtonXInner`, and the server replies. The runtime
click-enable on these components is supplied by the server-pushed `IF_SETEVENTS`
the lobby init already sends. **Conclusion: the server owns the
SET_WORLD_TARGET+SWITCH_WORLD response to a `906/81` click.**

### The IF_BUTTON the server receives

The 948 click decoder (`Rev948ClientCodecs.kt::ifButtonClick`) decodes the size-8
click as:

```
interfaceHash = readUIntLittle()          // (interfaceId << 16) | componentId
slotId        = readUShortAddLittle()
itemId        = readUShortAdd()
```

So a "Play Now" click arrives as `interfaceHash = (906 << 16) | 81 = 0x038A0051`.
`buttonId` is the option index 1..7/10 carried by the *opcode* (op127→1, …); for a
single-`useOption` component the relevant click is option 1 (op 127). The handler
must key on **interfaceHash**, not on buttonId.

### Required code change (`IfButtonHandler.kt`)

`lobby/src/main/kotlin/org/darkan/lobby/server/packet/IfButtonHandler.kt` currently
gates on `componentId == 32`. Change the constant:

```kotlin
private const val LOBBY_INTERFACE_ID = 906
private const val WORLD_ENTER_COMPONENT = 81   // "Play Now"  (was 32 — wrong, a Select row)
```

and gate the switch on `interfaceId == 906 && componentId == 81`. Everything else
in that handler (send `SetWorldTarget` then `SwitchWorld`, then `flush()`) is
correct.

> **Optional hardening (not required to unblock):** if you also want to honour the
> user's *selected* world rather than always `getDefault()`, track the last
> `906/Select`-row (or `820` sub-list) click per session and map the row index →
> world. That requires the row→world index mapping the lobby pushed via
> `RUNCLIENTSCRIPT` when it populated the list; it is **not** needed to get the
> client into a world. For first light, `getDefault()` on the `906/81` click is
> sufficient.

### Empirical confirmation method (if you want to re-verify on a future build)

1. Log every `IF_BUTTON` in the lobby with `interfaceId`/`componentId` (the handler
   already does — see its `logInfo`). Click **Play Now** in the live client.
2. The trigger is the `interfaceId=906` click that immediately precedes the client
   tearing down the lobby socket and opening a TCP connection to the world host.
3. Cross-check the component's `useOption` by re-decoding interface 906 from the
   shipping cache (the throwaway decoder used here read `cache.data(3, 906, ci)`
   per component and printed `useOption` + handler arrays). On 948 it is `81`.
   On a future build, re-decode — interface component indices can shift.

---

## 2. CQ2 — The world-login handshake (what the WORLD server must accept)

### How the hop happens (client side)

1. Server sends `SWITCH_WORLD` (op 213) to the **lobby** connection
   (`WorldData::SWITCH_WORLD` @ `0x001aeba0`). It stores the pending `WorldTarget`
   (id/host/portA/portB) and calls **`Client::SetMainState(client, 0x25)`**
   (`boot/05 §4.2`).
2. Main-state `0x25` = world-switch/reconnect. `LoginManager::OnMainStateTransition`
   on `toState == 0x25` calls **`StartWorldLogin(this, …)`** (`boot/02`, "OnMainStateTransition"),
   which calls `StartLogin(mode = 2)` against the new host:port held in the
   pending `WorldTarget` (WorldData `+0x18/+0x20`).
3. The login state machine **re-runs from step 10** against the world endpoint:
   `LoginStepWaitingConnectionOpened` (0xe) → `OpenConnection(host, port)` → send
   handshake hello (0xf) → read 9-byte first response (0x1e) → `SendLoginPacket`
   (0x50) → ISAAC install → result byte (0x5a/0x60) → login-data (0x96)
   (`boot/02`, "happy path"; `boot/04 §12` lifecycle).

So **the world server speaks the same login protocol as the lobby server.** That is
why `WorldServer.initWorldLogin` mirrors `LoginServer.handleLogin`.

### Connection-type byte and routing

| Stage | First byte client sends after socket opens | darkan const |
|---|---|---|
| Lobby login (observed) | `14` (CONNECT_LOGIN, 0x0E) | `RequestOpcode.CONNECT_LOGIN` |
| World login | **`14` (CONNECT_LOGIN)** — same login-connection selector | `RequestOpcode.CONNECT_LOGIN` |

`boot/04 §2.1` (login sub-state 10) shows the world path differs from the lobby
path only in *which endpoint struct* is read (`client d01→0x20` world vs `→0x30`
lobby), then the same `OpenConnection` + handshake hello. The handshake hello
(`boot/02`, `LoginStepWaitingSSOKeyResponse` 0xf; `boot/03 §SSO-key request`) is
built from the same proto template (`DAT_015d3698`) for both — i.e. **the
connection-type byte is identical (14)**; the *login service* is distinguished by
the inner login opcode below, not by the connection byte.

`WorldServer.connectClient` already routes `RequestOpcode.CONNECT_LOGIN` →
`initWorldLogin`. ✅ No change needed here.

### Inner login opcode (LOGIN vs RECONNECT vs LOBBY)

After the 9-byte first response, the client sends the login packet. The **inner
login opcode** (the 1-byte opcode darkan reads at the head of the login packet)
distinguishes the service:

| Login kind | Inner opcode | darkan const | Notes |
|---|---|---|---|
| Lobby login | `19` | `RequestOpcode.LOBBY` | lobby server |
| World login (fresh) | `16` | `RequestOpcode.LOGIN` | world server, `mode=2` |
| World login (reconnect/hop) | `18` | `RequestOpcode.RECONNECT` | world server, `mode=2`, reuses lobby reconnect handle |

> **Doc/impl abstraction note (flagged).** `boot/03` documents the login opcode as
> the raw header int **`0x3b4`** written via `FUN_00136900`, *not* a 1-byte
> opcode. darkan's servers instead read a **1-byte** inner opcode (16/18/19). These
> are two views of the same handshake: `boot/03` describes the inner RSA-service
> opcode space (`0x3b4` + sub-version `1`), while darkan reads the outer
> login-connection opcode byte that the *connection* layer (`boot/04`) routes on.
> The darkan lobby login already interoperates with the live client using the
> 1-byte model, so it is empirically correct; **`WorldServer` accepts `16` and
> `18`** and is consistent with the lobby. Do not "fix" this to `0x3b4` — match the
> working lobby model.

`WorldServer.initWorldLogin` already accepts `LOGIN(16)` and `RECONNECT(18)` and
rejects others. ✅

### First server response (9 bytes) — what the WORLD server sends

Identical to lobby (`boot/02`, `LoginStepWaitingFirstResponse` step 0x1e):

```
+0  u8   status        ; 0 = OK (any nonzero = result code, client aborts)
+1  u64  serverSeed    ; big-endian; client stores at LoginManager+0x40
```

`WorldServer` sends `ResponseOpcode.JS5_SYNC` (=0) then must follow with the seed.
**FLAG:** `boot/02` says the first response is **9 bytes (1 status + 8 seed)**.
`WorldServer.initWorldLogin` currently sends only `respond(JS5_SYNC)` (1 byte) for
the exchange — confirm the world handshake sends the full 9 bytes if the client's
world login expects the same 9-byte first response as the lobby. (The lobby server
sends 8 random session-key bytes after the `0` — `LoginServer` step 1. The world
path must match whatever the lobby did, since the client runs the *same* state
machine.) **Action: make `WorldServer` send the same 9-byte first response the
lobby sends.** This is the most likely silent-stall point.

### RSA login block (what the WORLD server must decrypt)

Same RSA block as lobby (`boot/03`, `CreateLoginRSAPacket`). World mode adds **one
extra leading byte before the RSA block** in the outer packet:

```
outer:  u16 size (patched)              ; pSizeMarker
        u32 opcode (0x3b4)              ; inner login service id
        u32 sub/version (1)
        u8  has-extra                   ; ONLY in world mode (mode==2): LoginManager+0xec
        <RSA block>                     ; RSA-wrapped inner block
        ...username/prefs/XTEA tail...
```

`WorldServer.initWorldLogin` reads this extra byte as `gameStateIsUnk10`
(`packet.readUByte()`), which matches the world-mode `has-extra` byte. ✅

RSA inner block (after RSA decrypt; `boot/03 CreateLoginRSAPacket`,
`LoginServer`/`WorldServer` impl):

```
+0   u8    magic = 10                   ; RSA marker (reject if != 10)
+1   16    isaacKeys[4]  (4× BE u32)    ; session symmetric key = ISAAC C2S seed
+17  u64   sessionCheck                 ; world: must be 0
     ...   auth method / TOTP / SSO selector (boot/03 §CreateLoginRSAPacket fields 1-3)
     str   authToken / password slot    ; world: carries the lobby-issued login token
     u64   sessionToken (+0x130)
     u64   (long)
```

> **World-login token (CQ2 specifics).** Per `boot/02` (`StartWorldLogin`,
> `OnMainStateTransition`) and `boot/03 §4`, the world login **reuses the
> reconnect handle / session tokens issued during the lobby login** (the third
> login response's reconnect handle at `LoginManager+0x120/+0x130`, and the two
> session ids `+0xf0/+0xf8` parsed from the lobby login-data block). In darkan the
> lobby embeds an HMAC `LoginToken` in the RSA password/token slot, and the world
> server verifies it (`LoginToken.verify` in `WorldServer`). For the *client* the
> token is opaque — it just forwards whatever the lobby gave it. **Action:** make
> the lobby actually issue a `LoginToken` and embed it where the client will carry
> it into the world RSA block (currently `WorldServer` logs "token not issued by
> lobby yet — allowing anyway" and proceeds; acceptable for first light, harden
> later).

### ISAAC + XTEA (identical to lobby)

- ISAAC C2S seed = the 4 RSA `isaacKeys`; S2C seed = each key **+ 0x32 (50)**
  (`boot/03 §Step 6`, `boot/04 §2.1`). darkan uses `EnvVars.ISAAC_DELTA` (= 50). ✅
- The credentials/prefs tail is XTEA-encrypted with the same 4 keys
  (`tinyKeyEncrypt`, 32-round XTEA, `boot/03 §Step 4`). `WorldServer` XTEA-decrypts
  it. ✅

### Login result byte (what the WORLD server sends to proceed)

After `SendLoginPacket`, the client reads a 1-byte result
(`LoginStepWaitingDisallowResult` 0x5a → `DealWithFirstResponse` 0x60). **`2` =
success** (world mode → `SetMainState(0x1e)` in-game). `WorldServer` sends
`ResponseOpcode.SUCCESS` (=2). ✅ Then a 1-byte login-data length and the login-data
block (`boot/02 §LoginStepHandleLoginData`, `boot/03 §LoginStepHandleLoginData`),
which `WorldServer` sends as `WorldLoginDetails` (world layout, `mode==2`). ✅

> The connection layer's own contract is simply: *"read 1 status byte; `2` =
> proceed and install ISAAC ciphers"* (`boot/04 §2.1`, wire-format box).

---

## 3. SET_WORLD_TARGET / SWITCH_WORLD / WORLDLIST wire formats

All three are server→lobby ServerProt packets. **darkan's 948 encoders match the
948-5 RE doc byte-for-byte** (verified against `boot/05` and
`Rev948ServerCodecsMisc.kt`).

### 3.1 SET_WORLD_TARGET — ServerProt op **212**, varByte (`boot/05 §4.1`)

Populates the pending `WorldTarget` (does **not** itself hop). Read order in the
client handler `WorldData::SET_WORLD_TARGET` @ `0x0017fc70`:

| # | Field | Type / transform | Notes |
|---|---|---|---|
| 1 | host | `gJagString` style jstr (`FUN_00afd8d0`) | server hostname/IP, NUL-terminated CP1252 |
| 2 | worldId | `g2` (BE u16) | stored as u32 at target `+0x08` |
| 3 | portA | `g2` (BE u16) | target `+0x28` |
| 4 | portB / tlsPort | `g2` (BE u16) | target `+0x2a` |

darkan encoder (`Rev948ServerCodecsMisc.kt`, op 212):
`writeRSString(host); writeShort(worldId); writeShort(portA); writeShort(portB)`.
**Match.** ✅

> ⚠️ `boot/05` notes the string reader is `FUN_00afd8d0` ("jstr"). darkan emits
> `writeRSString` (NUL-terminated CP1252, no version byte). The lobby login data
> uses `writePrefixedString` for the world host (`#27`) — that is a *different*
> field in a *different* packet. For 212/213 the host is a plain NUL-terminated
> string (`gStr`/`writeRSString`); confirm against a capture if the client rejects
> it, but the existing encoder matches the doc's reader.

### 3.2 SWITCH_WORLD — ServerProt op **213**, varByte (`boot/05 §4.2`)

Commits the hop: builds the active+pending targets and calls
`Client::SetMainState(0x25)`. Read order in `WorldData::SWITCH_WORLD` @ `0x001aeba0`:

| # | Field | Type / transform | Notes |
|---|---|---|---|
| 1 | worldId | `g2` (BE u16) | `uVar13` |
| 2 | host | jstr (`FUN_00afd8d0`) | `local_58` |
| 3 | portA | `g2` (BE u16) | `local_62` |
| 4 | portB / tlsPort | `g2` (BE u16) | `uVar16` |
| 5 | reconnectFlag | `g1` (u8) | `WorldData+0x58 = (byte == 1)` — "is reconnect/hop" |

darkan encoder (op 213):
`writeRSString(host); writeShort(worldId); writeShort(portA); writeShort(portB); writeByte(pendingFlag)`.

**Field-order mismatch — FLAG (verify).** The doc reads **worldId FIRST, then
host**; the darkan encoder writes **host FIRST, then worldId**. The darkan
`SwitchWorld` data class doc-comment claims the 947-3 order was
`host, worldId, portA, portB, flag`, but `boot/05 §4.2` for 948-5 reads
`worldId, host, portA, portB, flag`. These disagree.

> **Action (high priority):** re-verify the 948 `SWITCH_WORLD` field order against
> a capture or the 948-5 handler before relying on it. If `boot/05` is correct
> (worldId before host), the darkan op-213 encoder must be reordered to
> `writeShort(worldId); writeRSString(host); writeShort(portA); writeShort(portB);
> writeByte(reconnectFlag)`. A wrong order here makes the client parse the worldId
> out of the host bytes and connect to a garbage host:port — a silent failure to
> open the world socket. **Note:** SET_WORLD_TARGET (212) and SWITCH_WORLD (213)
> genuinely have *different* field orders in 948-5 (212 = host-first, 213 =
> id-first per `boot/05`); do not assume they share a layout.
>
> `reconnectFlag`/`pendingFlag`: send `0` for a fresh lobby→world entry
> (`byte == 1` marks a reconnect/hop). darkan defaults `pendingFlag = 0`. ✅ for
> first login.

### 3.3 WORLDLIST_FETCH_REPLY — ServerProt op **216**, varShort (`boot/05 §2`)

Fragment-reassembled world list. Not on the hop critical path, but the lobby must
send it so the user has worlds to pick. darkan already implements it
(`Rev948ServerCodecsMisc.kt`, op 216) with the frame byte + `buf[0]=2` /
`buf[1]=1` full-rebuild header + smart-encoded country/world tables matching
`boot/05 §2.2–2.4`. The handler requires `buf[0]==2` (format) and `buf[1]==1`
(full rebuild) or it drops the packet. ✅

> The lobby already sends `SET_WORLD_TARGET`/`SWITCH_WORLD` only from
> `IfButtonHandler`; the worldlist is sent in `sendLobbyInitPackets`. No change to
> 216 needed.

---

## 4. CQ3 — Minimal world-init packet list (ordered)

After the world login succeeds (result `2`, ISAAC installed), this is the smallest
ordered set of server→client packets that takes the client off the loading screen
and renders a world. Citations are to `boot/06` (scene) and `boot/07` (state-sync).

### Ordered minimal sequence

| # | Packet | Op | Size | Purpose | Source |
|---|---|---|---|---|---|
| 1 | **login-data block** | (login proto) | 1-byte len + body | post-login player/account block; client parses world-mode layout, then `SetMainState(0x1e)` in-game | `boot/02/03 §LoginStepHandleLoginData` (world path) |
| 2 | RESET_CLIENT_VARCACHE | 5 | 0 | clear varp+varc domains for a clean slate | `boot/07 §2.6` |
| 3 | (optional) RESET_CLIENT_STATE | 58 | 0 | fresh pending-zone-update container | `boot/06 §16.1` |
| 4 | **REBUILD_NORMAL_SIMPLE** | **81** | varShort | **the world-login scene build** — allocates+installs the BuildArea; without it the client has no scene | `boot/06 §13` |
| 5 | mandatory varps/varbits | 5/10/28/51/61/147 | — | character state the UI/CS2 needs (run energy, settings) | `boot/07 §2` |
| 6 | mandatory varcs | 47/48/64/69/116/196 | — | interface/client vars | `boot/07 §1` |
| 7 | SET_TICK_TIMER | 112 | 2 | server tick interval (times the rebuild deadline) | `boot/06 §16.4`, `boot/07 §4.1` |
| 8 | (as needed) RUNCLIENTSCRIPT | 110 | varShort | open the game HUD / run onload scripts | `boot/07 §3.1` |
| 9 | **SET_READY_FLAG** | **75** | 0 | **flips the global ready flag; client stops gating on `IsWorldReady` and renders** | `boot/06 §16.3`, `boot/07 §4.2` |
| 10 | **PLAYER_INFO (GPI)** | (player-info op) | varShort | local-player init (position prefix) so the avatar renders; then per-tick | `boot/06 §Hands off`, `boot/08 (c)` |
| 11 | NPC_INFO | (npc-info op) | varShort | NPC sync; empty is fine for first light | `boot/06 §14.3 / §Hands off` |

### REBUILD_NORMAL_SIMPLE (op 81) — wire format (`boot/06 §13`)

This is the **world-login rebuild** (`ClientState::REBUILD_NORMAL_SIMPLE` @
`0x001daa70`). The handler reads (cursor starts at `position`):

| # | Field | Offset | Size | Decode | Meaning |
|---|---|---|---|---|---|
| 1 | (route/len byte) | +0 | u8 | consumed by framing (`position += 3`) | — |
| 2 | centreZoneZ low | +1 | u8 | raw | low byte of centre zone Z |
| 3 | centreZoneZ high | +2 | u8 | raw | high byte of centre zone Z |
| 4 | magic | +3 | u8 | **must equal `0x85`** (`-0x7b`) else abort | format magic |
| 5 | centreZoneX | +4 | u16 BE | swap | centre zone X |
| 6 | cameraRotation | +6 | u8 | `cVar5 + 0x80` (byteAdd) | written to build state +0x428 |
| 7 | targetWorldId | +8 | u16 BE | swap | instanced source lookup (0 for normal) |
| 8 | packedCoordA | next | u32 BE | `gT_unsigned_int` → `DecodePackedCoord` | build-area corner A |
| 9 | packedCoordB | next | u32 BE | `gT_unsigned_int` → `DecodePackedCoord` | build-area corner B |

> **FLAG — darkan's `RebuildNormalSimple` encoder does not match `boot/06 §13`.**
> The darkan `ServerProt.RebuildNormalSimple` (op 90, from the older 947-3 A2
> research) encodes:
> `chunkX BE u16, forceRefresh u8, regionLow LE u16, magic 0x7B, chunkZ BE u16,
> packedCoordA, packedCoordB`. The 948-5 doc says **op 81** (not 90), **magic
> `0x85`** (not `0x7B`), and field order **`Zlo, Zhi, magic, Xbe, camRot, worldId,
> coordA, coordB`**. These are materially different. **Action (high priority):**
> the `networking-protocol-engineer` must add/repoint a 948 op-81 encoder matching
> `boot/06 §13` exactly:
> - magic byte **`0x85`**,
> - centre **Z** as two raw bytes (lo, hi) at +1/+2,
> - centre **X** as BE u16 at +4,
> - camera-rotation byte = `value + 0x80` at +6,
> - target world id BE u16 at +8 (use `0` for a normal non-instanced login),
> - two BE u32 packed corner coords.
>
> `DecodePackedCoord`'s exact bit layout is **not** in the 948 evidence
> (`boot/06 Open questions`) — the handler passes the decoded coords `>> 6` to the
> BuildArea allocator (zone granularity). For first light, encode the player's
> build-area corner coords; if the scene is offset, the packed-coord bit layout is
> the thing to nail down (decode `BuildArea::DecodePackedCoord` @ in the 948 image,
> or capture a live op-81 and match bytes). The 947-3 `(plane<<28)|(y<<14)|x`
> packing in darkan's current encoder is the right *shape* but unverified for 948.

### SET_READY_FLAG (op 75) — the render gate (`boot/06 §16.3`)

Size 0, no body. Sets the client's global ready flag and snapshots the ready cycle.
**This is the gate the boot sequence waits on before handing to gameplay** — must
be sent **after** the rebuild and the var burst. darkan's `SetReadyFlag` (op 75,
size 0) is correct. ✅

### What is NOT strictly required for first light

- Full inventory/skills/social — `boot/07` shows them streamed but the client
  renders an empty world without them. `WorldServer` already sends 29
  `UPDATE_STAT`, player options, etc. (a superset) — fine.
- Instanced rebuilds (op 83 `REBUILD_REGION_ALT`, op 199 `REBUILD_NORMAL`) — only
  for instances; a normal Lumbridge login uses op 81 only.

---

## 5. End-to-end sequence (server view)

```
LOBBY connection (already working):
  client --[14 CONNECT_LOGIN]--> lobby
  lobby  --[0 + 8-byte seed]-->  client
  client --[19 LOBBY + RSA + XTEA]--> lobby
  lobby  --[2 SUCCESS + lobby-data]--> client
  lobby  --[lobby init burst incl. WORLDLIST(216), IF_SETEVENTS, SET_READY_FLAG]--> client
  client renders lobby; user clicks "Play Now"
  client --[IF_BUTTON op127, interfaceHash=(906<<16)|81]--> lobby   ◀── THE TRIGGER (§1)
  lobby  --[SET_WORLD_TARGET(212) host/world/portA/portB]--> client
  lobby  --[SWITCH_WORLD(213) world/host/portA/portB/flag=0]--> client
  client: SetMainState(0x25) -> tears down lobby socket, opens WORLD socket

WORLD connection (the part to get right):
  client --[14 CONNECT_LOGIN]--> world
  world  --[0 + 8-byte seed (9 bytes total)]--> client          ◀── ensure full 9 bytes (§2 FLAG)
  client --[16 LOGIN (or 18 RECONNECT) + has-extra byte + RSA + XTEA, mode=2]--> world
  world  --[2 SUCCESS + login-data (world layout)]--> client     ◀── installs ISAAC
  world  --[RESET_CLIENT_VARCACHE(5)]-->
  world  --[REBUILD_NORMAL_SIMPLE(81) magic 0x85 + coords]-->     ◀── op/magic/order must match §4 FLAG
  world  --[varps/varcs, SET_TICK_TIMER(112)]-->
  world  --[SET_READY_FLAG(75)]-->                                ◀── render gate
  world  --[PLAYER_INFO + NPC_INFO]-->
  client leaves loading screen, renders world
```

---

## 6. Concise summary + top-3 implementation steps

### Summary

- **Trigger:** interface **906 / component 81 = "Play Now"** (proven by cache
  decode of `useOption`; components carry no client-side switch script, so the
  server must respond to the click). Confirmation method: log `IF_BUTTON`
  interfaceHash on the live "Play Now" click, and re-decode interface 906 from the
  cache — the enter-world component is the one with `useOption="Play Now"`.
- **World login:** `SWITCH_WORLD(213)` → `SetMainState(0x25)` → the client re-runs
  the **full login handshake in world mode** against the new host:port. Connection
  byte = **14** (same as lobby); inner opcode = **16/18** (not 19); world mode adds
  **one `has-extra` byte** before the RSA block; ISAAC S2C seed = C2S + 50; success
  byte = **2**. `WorldServer` already implements this shape.
- **Minimal world-init:** login-data → `RESET_CLIENT_VARCACHE(5)` →
  **`REBUILD_NORMAL_SIMPLE(81)`** → varps/varcs → `SET_TICK_TIMER(112)` →
  **`SET_READY_FLAG(75)`** → **PLAYER_INFO** (+ NPC_INFO).

### Top 3 implementation steps (for `networking-protocol-engineer`)

1. **Fix the lobby trigger.** In `IfButtonHandler.kt`, change
   `WORLD_SELECT_COMPONENT = 32` → `WORLD_ENTER_COMPONENT = 81` ("Play Now") and
   gate the `SetWorldTarget`+`SwitchWorld` send on `interfaceId==906 &&
   componentId==81`. (Single-line constant change; the send logic is already
   correct.)
2. **Verify/repair the `SWITCH_WORLD(213)` field order and the
   `REBUILD_NORMAL_SIMPLE` encoder.** (a) Re-check op 213 against `boot/05 §4.2`:
   the doc reads **worldId before host**; darkan currently writes **host before
   worldId** — reorder if the doc is right (wrong order → client connects to a
   garbage host). (b) Add a **948 op-81** `REBUILD_NORMAL_SIMPLE` encoder with
   **magic `0x85`** and the §4 byte order (current darkan encoder is op 90 / magic
   `0x7B` from 947-3 — wrong build).
3. **Make the world handshake's first response a full 9 bytes** (1 status + 8
   serverSeed), matching what the lobby sends, in `WorldServer.initWorldLogin`
   (it currently sends only the 1-byte `JS5_SYNC`). The client runs the same
   9-byte-first-response state machine for the world login; a short response is the
   most likely silent stall. Also wire the lobby to issue a real `LoginToken` and
   embed it where the client carries it into the world RSA block (currently the
   world server allows an absent token — fine for first light, harden after).

### Flagged uncertainties (do not treat as settled)

- **op 213 field order** (host-first vs worldId-first) — `boot/05 §4.2` vs darkan
  encoder disagree; capture-verify. (High impact.)
- **op 81 rebuild** — opcode (81 vs darkan's 90), magic (`0x85` vs `0x7B`), and
  field order must be brought to `boot/06 §13`. `DecodePackedCoord` bit layout is
  unresolved in the 948 evidence; capture-verify the packed coords if the scene is
  offset. (High impact.)
- **9-byte world first response** — confirm the world server emits the same
  9-byte first response as the lobby. (High impact — likely the current stall.)
- **Inner login opcode model** (`0x3b4` raw int per `boot/03` vs darkan's 1-byte
  16/18/19) — darkan's model is empirically working for the lobby; documented as an
  abstraction difference, not a bug.
- **World-login token** — the client forwards the lobby-issued token opaquely;
  darkan currently proceeds without verifying it. (Low impact for first light.)
```

---

## 7. World-init ordering + DecodePackedCoord (RESOLVED — 948-5 decompile)

> **Status: the two §4/§6 "high-impact flagged uncertainties" for op 81 are now
> resolved against the live `rs2client.948-5` binary** (project
> `~/projects/reclass-data/gzf-947-3`, program `rs2client.948-5`, opened
> read-only). Every claim below cites a decompiled/disassembled address.
> Source handlers: `BuildArea::DecodePackedCoord` @ `0x006d4320`,
> `ClientState::REBUILD_NORMAL_SIMPLE` @ `0x001daa70`,
> `SceneManager::FUN_00c55e80` @ `0x00c56160`, `PlayerList::ProcessPlayerInfo`
> @ `0x001618a0`, `ClientState::SET_READY_FLAG` @ `0x00175120`,
> `ClientProt::SendMapBuildComplete` @ `0x002bc200`.

### 7.0 VERDICT (read this first)

> **The loading→logout is caused by the garbage build-area coordinates in
> `WorldServer.sendWorldLoginCore`, NOT by PLAYER_INFO ordering.**
>
> 1. **PLAYER_INFO ordering is a non-issue.** `ProcessPlayerInfo` (ServerProt
>    op **22**, `@0x001618a0`) parses **unconditionally** — it has *no*
>    scene-ready / map-build / ready-flag gate, and returns the OK sentinel
>    `&DAT_015d3620` on every path. Sending PLAYER_INFO in the same burst as
>    REBUILD, before any client round-trip, is **not** rejected and is **not**
>    fatal. There is **no must-wait-for-MAP_BUILD_COMPLETE(107) rule.** (§7.3)
> 2. **The op-81 wire layout darkan already encodes is correct** (magic `0x85`,
>    18-byte body, field order) — verified byte-for-byte against the handler.
>    The codec `Rev948ServerCodecsRebuild.kt` and `WorldSwitchEncoderTest`
>    match the binary exactly. (§7.4)
> 3. **The bug is the packed-coord *values*** `WorldServer.kt` feeds into op 81.
>    It (a) bypasses `RebuildNormalSimple.packZoneCoord` and packs raw tile
>    coords straight into the 14-bit fields (so after the handler's mandatory
>    `>>6` the origin becomes `tile>>6`, e.g. `3204>>6 = 50` instead of zone
>    `400`), and (b) sets `packedCoordB = packedCoordA`, so the **build-area
>    span** is read as `{50,50}` zones instead of a sane scene size. The result
>    is a build area at the wrong origin with an absurd span → the map-squares
>    the client tries to load never line up, the scene never becomes "ready",
>    and the 5-minute load deadline / scene-load failure drops the client back
>    to login. (§7.1, §7.2)
>
> **Exact change the `networking-protocol-engineer` must make:** in
> `world/src/main/kotlin/org/darkan/world/server/WorldServer.kt`
> (`sendWorldLoginCore`, ~lines 452-468), stop packing raw tiles and stop
> reusing the origin word for the size. Use `packZoneCoord` for **both** words,
> put the **build-area origin zone** in A and the **build-area span in zones**
> in B:
>
> ```kotlin
> // Player at Lumbridge tile (3204, 3204) → zone (400, 400), plane 0.
> // The build area is the classic 104×104-tile window = 13×13 zones, with the
> // player centred: originZone = centreZone - sceneZones/2.
> val centreZoneX = 400
> val centreZoneZ = 400
> val sceneZones  = 13                         // 104 tiles / 8; see §7.2 note
> val originZoneX = centreZoneX - sceneZones / 2   // 394
> val originZoneZ = centreZoneZ - sceneZones / 2   // 394
> session.send(
>     RebuildNormalSimple(
>         zoneX = centreZoneX,                  // +4  centre zone X (absolute, BE u16)
>         zoneZ = centreZoneZ,                  // +1/+2 centre zone Z (absolute, LE u16)
>         packedCoordA = RebuildNormalSimple.packZoneCoord(fieldX = originZoneX, fieldZ = originZoneZ),
>         packedCoordB = RebuildNormalSimple.packZoneCoord(fieldX = sceneZones,  fieldZ = sceneZones),
>         cameraRotation = 0,
>         targetWorldId  = 0,
>     )
> )
> ```
>
> The player's **absolute world position is carried by `centreZoneX`/`zoneZ`**
> (already correct at 400/400) — *not* by the packed words. The packed words are
> the local build-area geometry only (see §7.2 for why they cannot hold an
> absolute coordinate). `originZoneX/Z` is the lower-left corner of the build
> window; `sceneZones`×`sceneZones` is its span. If the rendered scene still
> looks offset by a fixed amount, the only residual unknown is whether A is the
> window **origin** (corner) or the window **centre** — capture one live op 81
> to settle it (§7.2). Either way the current `tile>>6` / `B==A` values are
> definitively wrong.

### 7.1 `BuildArea::DecodePackedCoord` @ `0x006d4320` — exact bit layout (DEFINITIVE)

Full decompile (the entire function — only 55 bytes):

```c
void jag::game::BuildArea::DecodePackedCoord(uint *out, uint packed) {
  out[2] = 0;            // +0x08
  out[0] = 0xffffffff;   // +0x00  (default-invalid plane)
  out[1] = 0;            // +0x04
  if (packed != 0xffffffff) {
    out[2] = packed & 0x3fff;          // +0x08  field0  (bits 0..13,  14 bits)
    out[0] = packed >> 0x1c & 3;       // +0x00  plane   (bits 28..29,  2 bits)
    out[1] = packed >> 0xe & 0x3fff;   // +0x04  field1  (bits 14..27, 14 bits)
  }
  return;
}
```

Confirmed at the instruction level (`@0x006d4338 AND ESI,0x3fff` → `[RDI+8]`;
`@0x006d433e SHR EDX,0x1c` + `@0x006d4347 AND EDX,3` → `[RDI]`;
`@0x006d4341 SHR ECX,0xe` + `@0x006d434a AND ECX,0x3fff` → `[RDI+4]`).

**The 32-bit packed word is exactly:**

```
 bits 31 30 | 29 28 | 27 ........ 14 | 13 ............ 0
            |  plane |    field1     |     field0
            (unused) | (14 bits)     |   (14 bits)
```

i.e. `word = (plane << 28) | (field1 << 14) | field0`. A word of
`0xFFFFFFFF` is the "no coord" sentinel (sets plane = -1, fields = 0).

The output struct in memory is `{ uint plane@+0; uint field1@+4; uint field0@+8 }`.
(The decompiler's variable names happen to call field1 "Z" and field0 "X", but
do not over-read that — what matters is the byte offsets and the call-site
mapping in §7.2. `packZoneCoord(fieldX, fieldZ)` in the darkan data class names
them fieldX = field0, fieldZ = field1, consistent with the call-site mapping.)

### 7.2 How REBUILD_NORMAL_SIMPLE consumes the two words (DEFINITIVE call mapping)

In `REBUILD_NORMAL_SIMPLE` @ `0x001daa70`:

```c
uVar10 = Packet::gT_unsigned_int(packet);          // packedA  (BE u32, +10)
game::BuildArea::DecodePackedCoord(local_60, uVar10);   // out array A @ RSP+0x28
uVar10 = Packet::gT_unsigned_int(packet);          // packedB  (BE u32, +14)
game::BuildArea::DecodePackedCoord(local_54, uVar10);   // out array B @ RSP+0x34
...
game::SceneManager::FUN_00c55e80(
     sceneMgr, &worldRef,
     local_5c >> 6,   // arg3
     local_58 >> 6,   // arg4
     local_50 >> 6,   // arg5
     local_4c >> 6);  // arg6
```

The disassembly pins each argument to a struct field (SysV: EDX, ECX, R8D, R9D
= args 3,4,5,6), and each is shifted `>> 6` immediately before the call
(`@0x001dadc0/ce/d2/d5 SHR ...,0x6`):

| FUN_00c55e80 arg | source slot | which DecodePackedCoord field | value |
|---|---|---|---|
| arg3 | `RSP+0x2c` = A.field1 (+0x04) | packedA **field1** (bits 14-27) | `>> 6` |
| arg4 | `RSP+0x30` = A.field0 (+0x08) | packedA **field0** (bits 0-13)  | `>> 6` |
| arg5 | `RSP+0x38` = B.field1 (+0x04) | packedB **field1** (bits 14-27) | `>> 6` |
| arg6 | `RSP+0x3c` = B.field0 (+0x08) | packedB **field0** (bits 0-13)  | `>> 6` |

So **packedA supplies args 3+4 (the build-area ORIGIN pair)** and **packedB
supplies args 5+6 (the build-area SIZE/SPAN pair)**. `FUN_00c55e80`
(`@0x00c56160`) forwards these four values unchanged into the BuildArea
constructor `FUN_00643d10(..., form=3, arg3, arg4, arg5, arg6, ...)` alongside
seven world params copied from `sceneMgr+8..+0x28`. The doc `boot/06 §7` names
the four `FUN_00c55e80` params `(zoneX, zoneZ, sizeX, sizeZ)`; the
structurally-identical `REBUILD_REGION_ALT` path (`boot/06 §11`) feeds the
**same** constructor size-slots from its two `gSmart` *width-in-zones* counts,
confirming **args 5/6 = span in zones** and therefore **packedB = build-area
span in zones**, **packedA = build-area origin in zones**.

**Units — why these fields are zones, and cannot be absolute world coords:**
each field is 14 bits (max `0x3FFF` = 16383); after the mandatory `>>6` the
value is **0..255**. The RS world is far wider than 255 zones, so the packed
fields physically cannot encode an absolute world position — they are
**build-area-local zone values** (origin offset + span). The **absolute world
position travels separately** in `centreZoneX` (+4, BE u16, can be ≥256:
`@0x001daad6 MOVZX EBP, word[..+4]` with no 0xFF mask) and `centreZoneZ`
(+1/+2). The handler turns the centre into local tiles via
`local_64 = (centreZoneX - (camCentre>>4)) * 8` (`@0x001dabde/e1 SUB; SHL 3`).

Because the consumer does `zone = field >> 6`, the server must encode
`field = (zone << 6) & 0x3FFF` — **exactly what `RebuildNormalSimple.packZoneCoord`
already does.** `WorldServer.kt`'s hand-rolled `((tileY & 0x3FFF) << 14) |
(tileX & 0x3FFF)` skips the `<<6`, so the recovered "zone" is `tile >> 6`
(garbage), and reusing that word for B makes the span garbage too. That is the
bug (§7.0).

> **Residual capture-only ambiguity (does NOT block first light):** the evidence
> proves packedA = origin-pair, packedB = span-pair, both in zones. What the
> binary does *not* tell us (it lives inside the 9 KB `FUN_00643d10`
> constructor, out of scope) is whether the origin pair is the build-area
> **corner** or its **centre**, and the precise field0↔X / field1↔Z axis
> assignment relative to `centreZoneX/Z`. For a normal Lumbridge login this only
> shifts the scene by at most half a window; the loading→logout is fixed
> regardless. If the rendered scene is offset by a fixed delta, capture one live
> op 81 from a stock server and match the two u32s — that pins corner-vs-centre
> and the axis order. Everything else here is byte-exact.

### 7.3 PLAYER_INFO ordering rule (RESOLVED): no MAP_BUILD_COMPLETE gate

**Question:** must the client have built the scene / sent MAP_BUILD_COMPLETE
(ClientProt op 107) before it can accept PLAYER_INFO? **Answer: NO.**

`PlayerList::ProcessPlayerInfo` (ServerProt op **22**, `@0x001618a0`, the local
GPI packet) was fully decompiled. Its control flow:

- Immediately reads the bitstream (`Packet::Bit::gBit(packet, 1/2/5/8/0xb)`) for
  the high-resolution player block, then the low-resolution block, then loops
  `ProcessExtendedInfo` over the dirty-player list.
- There is **no read of any "scene ready" / "map built" / `+0xcfe` ready-flag /
  `IsWorldReady` predicate anywhere in the handler.** It does not consult
  `clientState+0xcfe`, `+0xd00`, or the BuildArea at all before parsing.
- Every exit returns `&DAT_015d3620` (the universal handler-OK sentinel). There
  is **no reject/abort/disconnect path** keyed on ordering.

Therefore PLAYER_INFO arriving in the same burst as REBUILD — before any client
round-trip, before the client has sent MAP_BUILD_COMPLETE(107) — is parsed
normally and is **not** the cause of the logout. **The correct trigger for the
server to start sending PLAYER_INFO is "immediately in the world-init burst";
there is no requirement to wait for ClientProt op 107.**

**What MAP_BUILD_COMPLETE (op 107) actually is** — `ClientProt::SendMapBuildComplete`
(`@0x002bc200`, emitter for ClientProt op **107**, size 0; confirmed via the
948-5 `ClientProt::RegisterAll` table: op 107 → ProtEntry `0x015d3800`, emitter
`SendMapBuildComplete`, and the function builds its packet from `DAT_015d3800`):

1. Sends the op-107 packet to the server (`Packet::SendClientMessage` with the
   `0x015d3800` ProtEntry) — a **client→server notification** "I finished
   building the map", carrying the loaded-map CRC list `[lVar10+0x18]+0x40`.
2. Then **drains the client's pending zone-update map** (`param_1+0xf8`, the
   same container `RESET_CLIENT_STATE`/`UPDATE_ZONE_PARTIAL` use), committing
   every queued entry via `FUN_002b3990(..,1,1,..)` and clearing `+0x158/+0x160`.

It is invoked by handler `FUN_002def20` (`@0x002def20`, returns the handler
sentinel `&DAT_015db2a0`), which is bound into the client's state/event dispatch
tables (referenced from the data tables at `0x0105ce08`/`0x010a10b4`, called
inside the main dispatch megafunction `FUN_000851ae`). It is a **client-side
reaction to the scene-build completing**, emitted by the client on its own
schedule. The server **does not need to do anything with op 107** to get the
client off the loading screen — it is informational + a client-internal zone
flush. (A production server uses it to know when to start streaming zone
updates, but it is not a precondition for PLAYER_INFO.)

### 7.4 SET_READY_FLAG (op 75) vs PLAYER_INFO, and the op-81 wire confirmation

**`SET_READY_FLAG`** (ServerProt op **75**, `@0x00175120`, size 0) — full body:

```c
A = clientState[+0xd00];                 // frame/scene-state object
B = *(A + 0xe8);                          // its cycle source
*( clientState[+0xcfe] + 0x10 ) = 1;      // set the global READY flag
*(A + 0xf0) = *(B + 0x10);                // snapshot the ready cycle/tick
return &DAT_015d3620;
```

It only flips the renderer's ready flag and snapshots a tick. It does **not**
touch the player list and is **independent of PLAYER_INFO** — neither gates the
other in the handlers. Ordering rules that actually matter:

- **SET_READY_FLAG must come *after* REBUILD** (the scene must be installed
  before the renderer is told it's ready). This is the render gate `boot/06 §16.3`
  describes; sending it before REBUILD flips "ready" on a scene that does not
  exist.
- **PLAYER_INFO (op 22) and SET_READY_FLAG (op 75) have no ordering dependency
  on each other** at the handler level. Sending PLAYER_INFO before *or* after
  op 75 is accepted. (Conventional order is REBUILD → varps/varcs → PLAYER_INFO/
  NPC_INFO → SET_READY_FLAG, but the handlers do not enforce it.)
- **MAP_BUILD_COMPLETE (op 107) is sent by the *client*, in reaction to its own
  scene build** — it is a response to REBUILD having produced a buildable scene,
  *not* a response to SET_READY_FLAG, and the server neither waits for it nor is
  blocked by its absence (§7.3).

**op-81 wire layout — confirmed byte-for-byte** against the handler prologue
(`@0x001daa98`-`0x001dab44`). The 18-byte `varShort` body:

| Offset | Field | Decode (proven in disasm) |
|---|---|---|
| +0 | route/filler | cursor bumps `position += 3`; byte never read |
| +1 | centreZoneZ **low** | `@0x001daab9 MOVZX EDX,[base+1]` → `Z = lo + hi*0x100` (**LE u16**) |
| +2 | centreZoneZ **high** | `@0x001daabf MOVZX R12D,[base+2]` |
| +3 | **magic = 0x85** | `@0x001daac9` load, `@0x001dab40 CMP R14B,0x85`; `!= 0x85` → abort `&DAT_015d35c0` |
| +4 | centreZoneX | `@0x001daad6 MOVZX EBP,word[base+4]` + `@0x001daaf7 ROL BP,8` (**BE u16**) |
| +6 | cameraRotation | `@0x001daaf3 MOVZX ESI,[base+6]` + `@0x001dab06 ADD ESI,-0x80` → stored `(raw-0x80)&0xFF` ≡ `(value+0x80)&0xFF` (writeByteAdd) |
| +7 | filler | **genuine gap** — cursor goes +6→+7→+10; the +7 byte is never read |
| +8 | targetWorldId | `@0x001daaff MOVZX R8D,word[base+8]` + `@0x001dab09 ROL R8W,8` (**BE u16**) |
| +10 | packedCoordA | `Packet::gT_unsigned_int` (**BE u32**) → DecodePackedCoord (origin pair) |
| +14 | packedCoordB | `Packet::gT_unsigned_int` (**BE u32**) → DecodePackedCoord (span pair) |

> **No discrepancy with the §4 table.** The +7 gap is real (confirmed). +6 is
> the camera byte (writeByteAdd) and +8 is the BE u16 targetWorldId — both
> correct. The `(value+0x80)` and `(raw-0x80)` renderings are identical mod 256
> (they differ by exactly 256), so `writeByteAdd(cameraRotation)` is the right
> encoder helper. The "magic 0x7B" text in some older notes/in-binary comment is
> stale: the compare instruction is `CMP …,0x85` — **the wire magic is `0x85`**,
> which is what darkan's op-81 codec already writes.

### 7.5 What changes / what does not

| Item | Status |
|---|---|
| op-81 opcode (81), size (varShort, 18-byte body) | **correct** — no change |
| op-81 magic `0x85`, field order, +7 gap, transforms | **correct** — `Rev948ServerCodecsRebuild.kt` + `WorldSwitchEncoderTest` match the binary |
| `RebuildNormalSimple.packZoneCoord` helper | **correct** — `field = (zone<<6)&0x3FFF`, plane bits 28-29 |
| `WorldServer.sendWorldLoginCore` packed-coord values | **WRONG → fix per §7.0** (use `packZoneCoord` for both; A=origin zones, B=span zones; do not pack raw tiles; do not set B=A) |
| PLAYER_INFO trigger ("wait for op 107?") | **NO wait** — send PLAYER_INFO in the burst (§7.3) |
| SET_READY_FLAG ordering | after REBUILD; independent of PLAYER_INFO (§7.4) |

---

## 8. Client crash @ 0x100123189 (world-init segfault) — PARTIALLY SUPERSEDED BY §9

> **⚠️ SUPERSEDED ATTRIBUTION.** §8 correctly identifies the *faulting instruction*
> (the NULL `GetVarcType` deref in `LoginStepWaitingServerClientVar`, step 0x10e)
> and that analysis is sound. But §8.4/§8.5 **wrongly attribute the crashing varc
> bytes to in-game ServerProt op 5 (`ResetClientVarcache`)**. That is DISPROVEN:
> op 5 was changed to send an empty length-prefixed block AND a wire probe confirms
> it is sent empty, yet the client still crashes at the identical instruction. The
> real source is the **world-login RESPONSE stream** (`WorldLoginDetails`, the
> post-SUCCESS op-2 block) being mis-framed so the LoginManager reads it as a varc
> block. **Read §9 for the correct root cause and fix.** Keep §8.5's empty-op-5
> change (it is still correct for the in-game varp/varc reset) but do NOT expect it
> to fix the crash — §9.6 is the fix.

Real macOS crash: `rs2client.948-5-mac-2026-06-21-131321.ips`. Client reaches
world-login + world-init, screen goes black (world view transition), then
**SIGSEGV ~1s later**. Exception `EXC_BAD_ACCESS / KERN_INVALID_ADDRESS @ 0x40`,
faulting thread index 6.

> Analysis binary: Ghidra project `~/rs-re-staging/macproj`, program `mac-9485`,
> image base `0x100000000`. (The `.ips` slid the binary to `0x1023b7000`, but
> Ghidra normalizes Mach-O to `0x100000000`; **`imageOffset` is identical**, so
> the prompt's `0x100000000 + offset` VAs are the correct Ghidra addresses.)
> Linux ref `rs2client.948-5` in `~/projects/reclass-data/gzf-947-3` (named
> `jag::LoginManager::*`).

### 8.0 VERDICT (read this first)

The crash is **NOT** in the scene/SceneManager build worker, the model/cache
loader, the char-creation avatar renderer, or PLAYER_INFO. It is in the
**`jag::LoginManager` state machine**, in the step that decodes the server's
**ServerClientVar (varc) config block**. The client calls
`ConfigProvider::GetVarcType(varcId)`; that returns **NULL** because the varc id
the server sent has **no VarcType in the client's cache** (or the varc block is
mis-sized so the 2-byte-at-a-time id iteration reads garbage ids). The next
dereference walks `[NULL+8]` then `[…+0x40]` → fault at **`0x40`**.

The `r15 = 22` register in the crash dump is a **red herring** — r15 is reloaded
at `0x1001231a3` (`MOVZX R15D,[RBP-0x44]`) as setup for the *next* statement
(`FUN_10000a120` / `SendIfButtonD`), which never executed. The fault is in the
**call before it**, `CALL 0x1000d31e0` at `0x100123184` (return address
`0x100123189`).

### 8.1 Faulting call chain (thread 6 = main client/engine thread)

Thread-entry `0x100abbe11` → `FUN_100abbda0` (pthread trampoline, sigaltstack +
`(*param[0])(param[1])`) → `FUN_100006bb0` (std::thread bootstrap) →
`FUN_100006470` (engine thread fn: gets engine singleton `DAT_100ee6238`, loops
vtable `+0x38` = the per-frame tick). So **thread 6 is the primary client/engine
thread** (game tick + render + packet pump), not a separate scene/cache worker.
On this Rosetta build it is a spawned pthread, hence index 6 rather than thread 0.

| Frame VA (Ghidra) | imageOffset | Function | Role |
|---|---|---|---|
| `0x100abbe11` | +0xabbe11 | `FUN_100abbda0` | pthread trampoline (thread entry) |
| `0x100006bdc` | +0x6bdc | `FUN_100006bb0` | std::thread bootstrap |
| `0x100006508` | +0x6508 | `FUN_100006470` | **engine thread fn** (per-frame tick loop) |
| `0x1008d867e` | +0x8d867e | `FUN_1008d7fe0` | client frame loop (fps/sleep + task-queue drain + tick vtable `+0x100`) |
| `0x100123189` | +0x123189 | `jag::ConnectionManager::LoginProtocolHandler_xplat948` @ `0x100120ed0` | **connection/game pump** — return addr of `CALL FUN_1000d31e0` |
| (`img[8] +0x0`) | — | *null/garbage* | tail-jumped LoginStep handler ran here (see §8.3) |
| `0x102e84e6e` | +0xacde6e | client crash handler (`raise`) | ignore |

Despite its name, `LoginProtocolHandler` is the **per-tick connection processor**
for the whole session (not just login). Crash region (`0x100123150`–`0x100123189`):

```
100123150  CMP  [RSI+0x19db0], 0x1e        ; client MAIN-STATE == 0x1e (30 = in-world)?  -> TRUE
...
10012317d  MOV  RDI, [RSI+0x19720]         ; RDI = jag::LoginManager  (client+0x19720)
100123184  CALL 0x1000d31e0                ; RunLoginStateMachine(LoginManager)  <-- crashes inside
100123189  MOV  RAX, [0x100ecf048]         ; <-- RETURN ADDRESS = crash-report PC (not the faulting insn)
```

`[client+0x19720]` is the **`jag::LoginManager`** instance (only caller of
`FUN_1000d31e0` is `LoginProtocolHandler`; the Linux twin is
`jag::LoginManager::RunLoginStateMachine @ 0x1b2480`).

### 8.2 Why the state machine is even running (the trigger transition)

This block executes on the `MAIN-STATE == 0x1e` (30 = in-world) path. The client
main-state values (from `jag::LoginManager::OnMainStateTransition @ Linux 0x1b2b30`):
`0x1e` = in-world/playing, `0x23` (35) = world-login-pending, `0x24` (36) =
world-login committed, `0x25` (37) = lobby. On a **Play-Now / world re-entry**
(main-state 0x1e → 0x23), `OnMainStateTransition` calls `WorldSwitcher::SetWorldTarget`,
`Client::SetMainState(0x24)`, then **`StartWorldLogin`** → **`StartLogin`**, which
re-arms the `LoginManager` step machine. On the next tick `RunLoginStateMachine`
dispatches the current LoginStep handler — and that handler faults. This matches
the report: world session entered, then a subsequent transition drives the login
machine over **leftover / not-fully-initialized varc state**.

### 8.3 The dispatch and the exact faulting instruction

`FUN_1000d31e0` (`RunLoginStateMachine`) ends in a **C++ pointer-to-member-fn
dispatch** (Itanium ABI) keyed on the LoginStep `[LoginManager+0x10]`, looked up
in the step→handler map at `[LoginManager+0x1e8]`. Mac tail-call: `JMP RCX` @
`0x1000d3328` (so the handler shares the `0x100123189` return address → it is the
`img[8] +0x0` null frame in the stack). All registered handlers have
`this`-adjust `[6]=0` and are **non-virtual** → called as `handler(LoginManager*)`;
the `+0x40` fault is **inside the handler body**, not in the member-fn resolution.

The current step is **`0x10e` (270)**, whose handler is mac `FUN_1000cd4c0` =
**`jag::LoginManager::LoginStepWaitingServerClientVar`** (Linux twin
`@0x18f280`, both guard `if ([LoginManager+0x10] != 0x10e) return 1`). Faulting
sequence (`FUN_1000cd4c0`, decompile + disasm):

```c
// iterate the buffered varc block 2 bytes at a time
uVar3   = *(ushort*)([LoginManager+0x1a8] + i);     // varc id (BE-swapped)
lVar5   = [[client+0x18d00]+0x208];                 // ConfigProvider (VarcType registry)
plVar9  = [lVar5+0x38];
lVar8   = (**(code**)(*plVar9 + 0x40))(plVar9, id, 0); // GetVarcType(id) -> VarcType* OR **NULL**
plVar9  = [[ [lVar8+8] +0x40 ] +8];                  // <-- if lVar8==NULL: [NULL+8] then [+0x40] => FAULT
(**(code**)(*plVar9 + 0x18))(plVar9, &out, LoginManager+0x198);
```

```
1000cd582  MOV  RDI,[R15+0x38]          ; ConfigProvider->varTypeTable
1000cd586  MOV  RAX,[RDI]               ; vtable
1000cd58d  CALL [RAX+0x40]              ; GetVarcType(id)  -> RAX = VarcType* (or NULL)
1000cd593  MOV  RAX,[RAX+0x8]           ; VarcType->[+8]    (NULL -> reads [0x8])
1000cd597  MOV  RAX,[RAX+0x40]          ; <-- FAULTS at 0x40  (KERN_INVALID_ADDRESS @ 0x40)
1000cd59b  MOV  RDI,[RAX+0x8]
1000cd5a9  CALL [RAX+0x18]
```

`GetVarcType(varcId)` returns **NULL** (varc id absent from the client cache);
`MOV RAX,[RAX+0x8]` reads near-null garbage; `MOV RAX,[RAX+0x40]` dereferences
**`0x40`** → the exact crash subtype.

### 8.4 The server data that feeds this step (ServerClientVar block)

The varc block parsed at step `0x10e` is filled by two earlier steps (Linux
names; mac twins are `FUN_1000cd350`/step 0x104 and `FUN_1000cd2a0`/step 0xfa):

1. **`LoginStepWaitingServerClientVarLength`** (step `0xfa`/250): reads **2-byte
   BE length** → `[LoginManager+0x1c8]`; → step `0x104`.
2. **`LoginStepWaitingServerClientVarConfigData`** (step `0x104`/260): reads
   `[+0x1c8]` bytes into a Packet, stores `dataPtr→[+0x1a8]`, `len→[+0x1a0]`; → step `0x10e`.
3. **`LoginStepWaitingServerClientVar`** (step `0x10e`/270): walks the block **2
   bytes per entry** (each = a varc id), calls `GetVarcType(id)` per id → **NULL
   → crash**.

This is the **server-client-var / `ResetClientVarcache` (op 5)** family in the
world-init burst. The crash fires when that block:
- **(a)** contains a varc id that has **no VarcType in the client's cache** (the
  varc definition was never loaded / doesn't exist in the cache the client has), or
- **(b)** is **mis-sized** — the declared 2-byte length does not equal the body
  written — so the 2-byte-stride iteration reads partial/garbage ids that miss in
  the registry.

Either way `GetVarcType` returns NULL and the `+0x40` chain faults. **There is no
NULL guard** on the lookup result in this step — the client trusts that every
varc id in a ServerClientVar block resolves to a loaded VarcType.

### 8.5 Server-side fix (for `networking-protocol-engineer`)

Root cause is darkan's world-init varc burst (the `ResetClientVarcache`/
ServerClientVar block in `WorldServer.sendWorldInitPackets`), **not** the scene
build, **not** PLAYER_INFO, **not** char-creation interface 1349 directly.

Do the following, in priority order:

1. **Stop sending unknown/empty ServerClientVar entries.** Only emit varc ids that
   exist as `VarcType` defs in the cache the client actually loaded (config index
   for varclient/varbit-client). For a **fresh account routed to character
   creation**, the safe burst is `ResetClientVarcache` (op 5) **with zero varc
   entries** (length `0x0000`, no body). An empty block makes step `0x10e` skip the
   loop entirely (`uVar11 = [+0x1a0] = 0`, the `1 < uVar11` guard is false) — no
   lookup, no crash.

2. **Make the length field exact.** If you do send entries, the 2-byte BE length
   prefix (consumed at step `0xfa`) MUST equal the body byte count, and the body
   MUST be an integral number of **2-byte varc-id entries** (the step strides by 2
   and treats each pair as a `gT<unsigned_short>` id). An off-by-N length desyncs
   the id stream and triggers the same NULL lookup.

3. **Skip character creation for first light (recommended).** Route the fresh
   account straight to the in-game HUD instead of the char-creation flow. That
   means: do NOT push the char-creation varcs/varps the avatar UI expects, send the
   normal world-init burst (per §4 / §7) with a **valid, properly-sized scene** and
   an **empty (or fully cache-backed) ServerClientVar block**, then `IfOpenTop` the
   game HUD root rather than interface 1349. This removes both the unknown-varc risk
   and the avatar-object init the char-creation screen needs.

**One-line verdict + exact change:** The world-init segfault is the client's
`jag::LoginManager::LoginStepWaitingServerClientVar` step (mac `FUN_1000cd4c0`,
called via `RunLoginStateMachine`=`FUN_1000d31e0` from `LoginProtocolHandler`
`0x100123184`→ret `0x100123189`) dereferencing a **NULL `VarcType`** at `+0x40`
because the server's ServerClientVar/`ResetClientVarcache` (op 5) block carries a
varc id with no client-side VarcType (or a mis-sized body). **Fix:** in
`WorldServer.sendWorldInitPackets`, send `ResetClientVarcache` with an **empty,
correctly length-prefixed (`0x0000`) varc block** (or only cache-backed varc ids
with an exact 2-byte length), and for the fresh-account path route to the game HUD
instead of char-creation interface 1349.

---

## 9. World-login RESPONSE byte layout — the REAL varc-crash source (DEFINITIVE)

> **Status: §8's attribution of the varc crash to in-game ServerProt op 5
> (`ResetClientVarcache`) is DISPROVEN and superseded by this section.** The op-5
> fix in §8.5 was correct *as far as it went* (op 5 is now sent empty and a wire
> probe confirms it), but the client **still crashes at the identical instruction**
> because the crashing varc list does **not** come from op 5 at all. It comes from
> the **world-login RESPONSE stream** that `jag::LoginManager` parses *before* the
> in-game packet pump ever runs. New crash report:
> `rs2client.948-5-mac-2026-06-21-140832.ips` (same fault site, client "loaded
> longer" — it now waits for *more* bytes before crashing, see §9.3).
>
> Every claim below cites a decompiled address. **Response-side decompiles are from
> the Linux `rs2client.948-5`** (project `~/projects/reclass-data/gzf-947-3`,
> program `rs2client.948-5`, named `jag::LoginManager::*` symbols — image base 0).
> The Mac twins (`mac-9485`, base `0x100000000`) were verified to share the same
> field offsets and control flow (the three varc steps decompiled identically;
> §8's `FUN_1000cd2a0`/`FUN_1000cd350`/`FUN_1000cd4c0` are the Mac twins of the
> Linux `0x180d00`/`0x180bf0`/`0x18f280`). Decompiled with the read-only headless
> helper `ghidra-scripts/RS3DecompMulti.java`.

### 9.0 VERDICT (read this first)

> **In WORLD mode the post-SUCCESS response is NOT a single `WorldLoginDetails`
> blob. The client parses a fixed THREE-PART stream, and the player/account block
> darkan calls `WorldLoginDetails` is the LAST part, not the first.** darkan sends
> only the third part (mis-framed as a VarByte packet), so the client reads that
> block's leading bytes as the FIRST part (a server-client-var block) and crashes.
>
> The exact world-mode state path after the result byte `2` (proven in §9.1):
>
> ```
> 0x60 DealWithFirstResponse  (result==2 && mode==2)  →  0xfa
> 0xfa WaitingServerClientVarLength   read u16 BE length        →  0x104
> 0x104 WaitingServerClientVarConfigData  read <length> bytes   →  0x10e
> 0x10e WaitingServerClientVar   walk varc block; ackFlag==1?   →  0x82  (else loop → 0xfa)
> 0x82 WaitingPlayersPacketReconnect   read 1 result/players byte (==2) →  0x88
> 0x88 DealWithThirdResult   (byte==2)                          →  0x8c
> 0x8c WaitingLoginCredentialsLength   read 1-byte login-data length →  0x96
> 0x96 HandleLoginData   parse the world-path player/account block  →  SetMainState(0x1e)
> ```
>
> So the **complete world-login response wire stream** the WORLD server must send,
> in order, is:
>
> ```
> [1]  result byte = 0x02                                  (read by step 0x5a, before 0x60)
> --- PART A: server-client-var block (steps 0xfa→0x104→0x10e) ---
> [2]  u16 BE  varcBlockLen                                 ; length of the bytes that follow in part A
> [3]  u8      ackFlag                                       ; MUST be 1 to advance to part B
> [4]  (varcBlockLen-1 bytes) zero or more {u16 BE varcId, typed value}
> --- PART B: players/result byte (step 0x82) ---
> [5]  u8      playersByte = 0x02                            ; MUST be 2 to advance to part C
> --- PART C: login-data block (steps 0x8c→0x96) — THIS is darkan's WorldLoginDetails ---
> [6]  u8      loginDataLen                                  ; 1-byte length of part C body
> [7]  (loginDataLen bytes) the world-path HandleLoginData body (§9.4 table)
> ```
>
> **The safe empty Part A is `00 01 01`** — a 2-byte length `0x0001`, then a single
> ackFlag byte `0x01`. Length 1 makes step 0x10e's loop guard `1 < 1` false (zero
> varc-id iterations → zero `GetVarcType` calls → no NULL deref), and `ackFlag==1`
> advances cleanly to step 0x82. (A length of `0x0000` would *also* skip the loop,
> but then `buf[0]` is read out of bounds / is whatever byte follows, and ackFlag
> would not reliably be 1 → the client would loop back to 0xfa and read the *next*
> bytes as another varc length. **Use `00 01 01`, not `00 00`.**)
>
> **The precise change for `networking-protocol-engineer` is in §9.6.** In one
> line: darkan must send `[02][00 01 01][02][len][WorldLoginDetails body]` after the
> SUCCESS byte — NOT `[02-as-smart-opcode][varByte-len][WorldLoginDetails body]`.
> The `WorldLoginDetails` field list is correct and stays; it just needs the varc
> prefix (`00 01 01`), the players byte (`02`), a 1-byte (not varByte) length, must
> NOT be framed as a smart-opcode packet, and needs **5 trailing fields appended**
> (§9.4).

### 9.1 Proof: world-mode result-2 routes to the varc block, not HandleLoginData

`jag::LoginManager::LoginStepDealWithFirstResponse` @ **`0x1b1e80`** (step `0x60`,
the result dispatcher), result byte `this+0x184 == 2`:

```c
if (iVar12 == 2) {                                  // result == SUCCESS
    if (*(int *)(param_1 + 0x20) != 2) {            // mode != 2  → LOBBY
      *(undefined4 *)(param_1 + 0x10) = 0x8c;        //   lobby: → 0x8c (login-data length) → 0x96
      return 1;
    }
    // mode == 2  → WORLD:
    if (RELA[0xd40] == 0x23) Client::SetMainState(lVar11, 0x24);   // world-login-pending → committed
    ... free world-target + reconnect lists (RELA[0xf1..0xf8]) ...
    *(undefined4 *)(param_1 + 0x10) = 0xfa;          //   WORLD: → 0xfa (ServerClientVarLength)  ◀── PROOF
    return 1;
}
```

This is the load-bearing fact: **lobby login goes `0x60 → 0x8c → 0x96`
(HandleLoginData directly); world login goes `0x60 → 0xfa` (the varc block
first).** That is why the lobby login has worked all along while the world login
crashes — they run *different* post-SUCCESS parsers. The `WorldLoginDetails` field
set darkan reverse-engineered IS the correct `HandleLoginData` world body (it
matches step 0x96 field-for-field, §9.4) — but in world mode the client refuses to
reach 0x96 until it has first consumed Part A (varc) and Part B (players byte).

### 9.2 Part A — the server-client-var block (the bytes that crash)

Three steps, all verified in the Linux 948-5 binary (and identically in the Mac
twin per §8):

**Step 0xfa — `LoginStepWaitingServerClientVarLength` @ `0x180d00`:**
```c
if (step == 0xfa && GetBytesAvailable(conn, 2)) {
    p = ReadBytes(conn, 2);
    this->varcLen[+0x1c8] = FUN_00121a30(p);   // u16 BE  (FUN_00121a30 = read u16 big-endian)
    step = 0x104;
}
```
→ **reads a 2-byte big-endian length** into `+0x1c8`.

**Step 0x104 — `LoginStepWaitingServerClientVarConfigData` @ `0x180bf0`:**
```c
if (step == 0x104 && GetBytesAvailable(conn, this->varcLen)) {
    p = ReadBytes(conn, this->varcLen);
    Packet::ResizeBuffer(tmp, varcLen);  memcpy(tmp.data, p->bufData, varcLen);
    this->varcData[+0x1a8] = tmp.data;   this->varcDataLen[+0x1a0] = varcLen;   // len/data stored
    step = 0x10e;
}
```
→ **reads exactly `varcLen` bytes** into the varc buffer (`+0x1a8` data, `+0x1a0`
len).

**Step 0x10e — `LoginStepWaitingServerClientVar` @ `0x18f280` (the crash site):**
```c
if (step != 0x10e) return 1;
buf   = this->varcData[+0x1a8];
ack   = buf[0];                               // buf[0] = the ackFlag byte
len   = this->varcDataLen[+0x1a0];
cfg   = client->ConfigProvider[RELA 0xca6 → +0x208];
if (1 < len) {                                // LOOP GUARD: only iterate if len > 1
    cursor = 1;                               // start AFTER the ackFlag byte
    do {
        this->cursor[+0x1b0] = cursor + 2;
        id16 = *(u16*)(buf + cursor);
        id   = bswap16(id16);                  // varc id, big-endian
        reg  = *(long**)(cfg + 0x38);          // VarcType registry
        vt   = (**(reg + 0x40))(reg, id, 0);   // GetVarcType(id)  →  NULL if absent  ◀── returns NULL
        deref = *(long**)( *(long*)(*(long*)(vt + 8) + 0x40) + 8 );  // [[ [vt+8] +0x40 ] +8]  ◀── NULL→FAULT at +0x40
        (**(deref + 0x18))(deref, &out, this+0x198);                 // decode value via type reader
        ... store value into client serverclientvar table (lVar3 + 0x73a8) ...
        cursor = this->cursor[+0x1b0];         // advanced by entry size
        len    = this->varcDataLen[+0x1a0];
    } while (cursor < len);
}
this->varcDataLen[+0x1a0] = 0; this->varcData[+0x1a8] = 0;   // free buffer
if (ack == 1) {  ...send "continue" hello (DAT_015d3668)...  step = 0x82;  }   // ◀── advance to Part B
else          {  step = 0xfa;  }                                                // ◀── loop: read ANOTHER block
```

**Block format: `[1 byte ackFlag][repeat: u16 BE varcId + typed value]`.** The
crash is `(**(reg+0x40))(reg,id,0)` (= `ConfigProvider::GetVarcType`) returning
NULL for a varc id with no `VarcType` in the client cache, then the unconditional
`[[ [NULL+8] +0x40 ] +8]` chain faulting at `0x40`. **There is no NULL guard.**
(Mac twin `FUN_1000cd4c0`: `lVar8 = (**(*plVar9+0x40))(plVar9,id,0); plVar9 =
[[ [lVar8+8] +0x40 ] +8];` — byte-identical chain, fault `MOV RAX,[RAX+0x40]` at
`0x1000cd597`, the §8 disasm.)

### 9.3 Why darkan's op-2 makes the client read varc id `0x6803` / 26627

darkan sends `WorldLoginDetails` via `Session.encodePacket` with `noIsaac=true`,
opcode 2, `ProtSize.VarByte` (`Session.kt:261-268`, `writeOpcode(2, null)` →
`writeSmart(2)` → single byte `0x02`; then `writeByte(payloadLen)`). With
`EnvVars.debug=false`, `worldName="Darkan"`, `worldMembers=true`, the body is **20
bytes**:

```
field            bytes
rights (=0/2)    02            (debug → 2; prod → account.rights, often 00)
modLevel (=0)    00
quickChat        00
verifiedEmail    00
aBool7322        00
quickChatOnly    00
playerIndex      HH LL         (BE u16, e.g. slot 1 → 00 01)
members (=1)     01
dob (=0)         00 00 00      (writeMedium, 3 bytes)
memberWorld(=1)  01
worldName        44 61 72 6B 61 6E 00   ("Darkan" CP1252 + NUL)
                 ───────────────────────  = 20 bytes
```

**Wire bytes after the SUCCESS byte:**
`02 14  02 00 00 00 00 00 HH LL 01 00 00 00 01 44 61 72 6B 61 6E 00 …`
       └ op └len└──────────────── 20-byte body ───────────────────┘ └ ISAAC burst…

The client (world mode, step 0xfa) reads the first 2 bytes as the **u16 BE varc
block length**:

```
[02][14]  →  varcLen = 0x0214 = 532
```

Step 0x104 then waits for **532 bytes** and copies them into the varc buffer —
that swallows the 20-byte body remainder **plus the first ~514 bytes of the
ISAAC-encrypted world-init burst** (`RESET_CLIENT_VARCACHE`, stats, REBUILD,
PLAYER_INFO, …). This is exactly the user's observation that the client "loaded
longer before crashing": it is blocked in step 0x104 waiting for 532 bytes, so it
consumes far more of the stream than the previous (mis-sized-differently) attempt
before step 0x10e runs.

Step 0x10e then walks that 532-byte buffer:
- `buf[0] = 0x02` (the `rights` byte) → `ackFlag = 2` (≠ 1 → it would loop back to
  0xfa *if* it survived; but it crashes first).
- It reads 2-byte BE ids from offset 1: `[00 00]`(id 0)`[00 00]`(id 0)`[HH LL]`
  (playerIndex)`[01 00]`… then, once past the 20-byte body, **raw
  ISAAC-ciphertext bytes**. Those random bytes contain a pair `03 68` at some even
  offset, which byte-swaps to **`0x6803` = 26627** — the `r14` value in the crash
  dump and the varc id that `GetVarcType` can't resolve → NULL → fault at `0x40`.
  (`rsi=0xb`/`r15=0x16` in the dump are the loop/stack scratch values, not ids —
  the §8.0 "r15 red herring" note still holds; the live id is in `r14`.)

This nails task 2: the **mis-sized field that shifts the cursor is the framing
itself** — darkan emits a 1-byte VarByte length where the client reads a 2-byte BE
length, and prefixes a smart-opcode byte the client never expects. The opcode byte
`0x02` and the length byte `0x14` together become the bogus 532-byte length, after
which every subsequent read is from the wrong offset.

### 9.4 Part C — the world-path `HandleLoginData` body (= darkan's WorldLoginDetails)

`jag::LoginManager::LoginStepHandleLoginData` @ **`0x1cd360`**, world branch
(`this+0x20 == 2`). Read into the freshly-allocated `0xf0` record (`lVar29`) and
the small `0x18` player record (`plVar13`). Cursor = `packet->position`. This is
reached only after Part A + Part B + the 1-byte length step (0x8c). Field-by-field
from the decompile:

| # | Field | Reader (proven) | Bytes | darkan `WorldLoginDetails` field | Dest |
|---|-------|-----------------|-------|----------------------------------|------|
| 0 | **leadFlag** | `if (buf[pos]==1) FUN_0017a620(...)` then `pos+=1` | 1 | **MISSING** (darkan's first body byte is `rights`, value 2≠1, so this consumes `rights` as the leadFlag and shifts everything by one) | — |
| 1 | rights | `u8` | 1 | `rights` | rec+0x08 |
| 2 | modLevel | `u8` | 1 | `modLevel` | rec+0x0c |
| 3 | bool C | `u8 == 1` | 1 | `quickChat` | rec+0x10 |
| 4 | bool D | `u8 == 1` | 1 | `verifiedEmail` | rec+0x19 |
| 5 | bool E | `u8 == 1` | 1 | `aBool7322` | rec+0x1a |
| 6 | bool F | `u8 == 1` | 1 | `quickChatOnly` | playerRec+0x08 |
| 7 | playerIndex | `u16 BE` (`FUN_00121a30`) | 2 | `playerIndex` | rec+0x48 |
| 8 | bool G | `u8 == 1` | 1 | `members` | rec+0x28 |
| 9 | dob/days | **s24 BE** sign-extended (`>0x7fffff → −0x1000000`) | 3 | `dob` | rec+0x14 |
| 10 | membersToggle | `u8 == 1` | 1 | `memberWorld` | propagated to `Client RELA[0xca6]+0xe0` (members flag) |
| 11 | worldName/host | **jstr** = NUL-terminated CP1252 (`FUN_00afd8d0`) | var | `worldName` | client+0x197b0 |
| 12 | (discard) | `u16 BE` (`FUN_00121a30`) | 2 | **MISSING** | folds into rec+0x90 hi |
| 13 | serverTimeDelta | `u32 BE` (`gT_unsigned_int`) | 4 | **MISSING** | rec+0x90 = `(u32 − now_ms/1e6) + (u16<<32)` |
| 14 | sessionId1 | `u64 BE` (`gT_ulong`) | 8 | **MISSING** | LoginManager+0xf0 |
| 15 | sessionId2 | `u64 BE` (`gT_ulong`) | 8 | **MISSING** | LoginManager+0xf8 |

**darkan's `WorldLoginDetails` matches fields 1–11 EXACTLY** (this is where that
field list came from — it is the genuine world `HandleLoginData` body). The diff:

- **Missing field 0** (`leadFlag`): there is no separate lead byte in darkan's
  encoder. In practice darkan's `rights` byte *is* the byte the client tests at
  field 0. As long as `rights != 1` the client treats it as the first real field
  (`rights`) — but to be layout-exact and future-proof, the body should begin with
  an explicit `leadFlag = 0` byte, then `rights`. **However** — see the important
  note below: because field 0 and field 1 are read from adjacent bytes and field 0
  is only special when `==1`, sending `rights` directly (with rights ∈ {0,2}) is
  observationally equivalent to `leadFlag=0` followed by `rights`, *provided the
  total field count downstream is right*. The cleanest fix is to prepend an
  explicit `00` leadFlag.
- **Missing fields 12–15** (`u16`, `u32`, `u64`, `u64` = 22 bytes): darkan stops
  after `worldName`. The client unconditionally reads 22 more bytes here. Without
  them the client reads `GetBytesAvailable(conn, loginDataLen)` for whatever
  `loginDataLen` darkan declared, then reads past the body into following bytes for
  fields 12–15 — corrupting the session ids and the post-login stream. **These four
  fields must be appended.**

> `FUN_0017a620` (field 0's gated sub-reader) was decompiled: when `leadFlag==1` it
> pulls **4 bytes deciphered through the S2C ISAAC stream** (`conn+0x2b8`, the
> inlined 256-word ISAAC refill is visible), then rewinds the cursor by 4 twice (a
> keystream-sync peek used by the high-detail reconnect path). darkan must send
> `leadFlag = 0` so this branch never runs (it has no ISAAC keystream to satisfy it
> at this pre-burst point).

### 9.5 The corrected world-login RESPONSE — byte-for-byte

Everything after the `02` SUCCESS byte, **sent pre-ISAAC** (no opcode framing, no
ISAAC on any of these bytes — the world-init ISAAC burst starts only *after* Part C
completes and `SetMainState(0x1e)` hands off):

```
OFFSET  BYTES                       FIELD                       NOTES
------  --------------------------  --------------------------  ----------------------------------
            (the 0x02 SUCCESS byte precedes this, already sent by WorldServer step 11)

=== PART A: server-client-var block ===
+0      00 01                       varcBlockLen = 0x0001       u16 BE; length of part-A body below
+2      01                          ackFlag = 0x01              MUST be 1 → advances to Part B
                                                                (no varc-id entries: len 1 ⇒ loop guard 1<1 false)

=== PART B: players/result byte ===
+3      02                          playersByte = 0x02          MUST be 2 → advances to Part C

=== PART C: login-data block (the WorldLoginDetails body) ===
+4      LL                          loginDataLen                u8; = number of bytes in part-C body
+5      00                          leadFlag = 0x00             field 0; 0 ⇒ skip FUN_0017a620
+6      RR                          rights                      u8
+7      MM                          modLevel                    u8
+8      00/01                       quickChat                   u8 bool
+9      00/01                       verifiedEmail               u8 bool
+10     00/01                       aBool7322                   u8 bool
+11     00/01                       quickChatOnly               u8 bool
+12     HH LL                       playerIndex                 u16 BE  (MUST equal the GPI slot)
+14     00/01                       members                     u8 bool
+15     dd dd dd                    dob / member days           s24 BE (sign-extended)
+18     00/01                       memberWorld                 u8 bool
+19     <name> 00                   worldName                   NUL-terminated CP1252 jstr
+..     uu uu                       reserved16                  u16 BE  (client discards; send 00 00)
+..     tt tt tt tt                 serverTimeDelta             u32 BE  (server epoch seconds; send now/1000 or 0)
+..     s1 s1 s1 s1 s1 s1 s1 s1     sessionId1                  u64 BE  (any 64-bit session token; 0 ok)
+..     s2 s2 s2 s2 s2 s2 s2 s2     sessionId2                  u64 BE  (any 64-bit session token; 0 ok)
```

`loginDataLen` (the `LL` at +4) = the byte count of Part C **from +5 through the
end** (leadFlag … sessionId2). For `worldName="Darkan"` (7 bytes incl. NUL) it is:
`1(lead) + 1(rights) + 1(modLevel) + 4(bools) + 2(playerIndex) + 1(members) +
3(dob) + 1(memberWorld) + 7(name) + 2(reserved16) + 4(time) + 8(sid1) + 8(sid2)
= 43 bytes`. (Recompute if `worldName` changes — it is the only variable-length
field.)

> **Endianness / transform mapping for the implementer (JagExtensions):**
> `varcBlockLen`/`playerIndex`/`reserved16` = `writeShort` (BE u16, `p2`);
> `serverTimeDelta` = `writeInt` (BE u32, `p4`); `dob` = `writeMedium` (BE s24,
> `p3`); `sessionId1/2` = `writeLong` (BE u64, `p8`); `worldName` =
> `writeRSString` (NUL-terminated CP1252, `pStr`); all single bytes = `writeByte`
> (`p1`). All standard helpers — no exotic transforms.

### 9.6 EXACT change for `networking-protocol-engineer`

The fix is **not** in the in-game ServerProt table (op 5 is fine, leave §8.5's
empty op-5 in place — it is still correct for the in-game varp/varc reset and is
unrelated to this crash). The fix is in the **world-login response framing** in
`world/.../WorldServer.kt` and the `WorldLoginDetails` encoder.

There are two equally-valid implementation shapes; pick one:

**Option 1 (recommended — model the three parts explicitly).** Replace the single
`session.send(WorldLoginDetails(...), noIsaac=true)` (WorldServer.kt ~line 330)
with a single pre-ISAAC raw write of the full Part A + Part B + Part C stream:

```
// after output.writeByte(ResponseOpcode.SUCCESS):
// PART A — empty server-client-var block
writeShort(0x0001)        // varcBlockLen (BE u16)
writeByte(0x01)           // ackFlag = 1  → client advances past the varc loop
// PART B — players/result byte
writeByte(0x02)           // playersByte = 2 → client advances to login-data
// PART C — login-data block (1-byte length + body)
//   build the body first to measure its length:
val body = buffer {
    writeByte(0x00)                       // leadFlag (0 ⇒ no extended ISAAC prefix)
    writeByte(rights); writeByte(modLevel)
    writeByte(quickChat); writeByte(verifiedEmail)
    writeByte(aBool7322); writeByte(quickChatOnly)
    writeShort(playerIndex)               // BE u16
    writeByte(members)
    writeMedium(dob)                      // BE s24
    writeByte(memberWorld)
    writeRSString(worldName)              // NUL-terminated CP1252
    writeShort(0)                         // reserved16  (client discards)
    writeInt((System.currentTimeMillis()/1000).toInt())  // serverTimeDelta
    writeLong(sessionId1)                 // 0 ok for first light
    writeLong(sessionId2)                 // 0 ok for first light
}
writeByte(body.size)      // loginDataLen (1 byte — body must be ≤ 255)
writeBytes(body)
flush()
```

All of the above is **pre-ISAAC** (raw bytes, exactly as the current
`noIsaac=true` send is). The ISAAC-ciphered world-init burst
(`sendWorldInitPackets`) follows unchanged.

**Option 2 (keep the `WorldLoginDetails` data class).** If you prefer to keep
sending `WorldLoginDetails` through the codec, then (a) the encoder must NOT be a
smart-opcode VarByte packet — it must be a raw pre-ISAAC write with a **1-byte**
length and a **`00` leadFlag** prefix and the **4 trailing fields**; and (b) you
must still emit the `00 01 01 02` (Part A + Part B) prefix *before* it. The
data class gains four fields: `reserved16:Int=0, serverTimeDelta:Int,
sessionId1:Long=0, sessionId2:Long=0`, and the encoder appends
`writeShort(reserved16); writeInt(serverTimeDelta); writeLong(sessionId1);
writeLong(sessionId2)` after `writeRSString(worldName)`, and prepends
`writeByte(0)` (leadFlag). Because the client reads this block with a plain 1-byte
length (step 0x8c) and NO opcode, sending it as a normal `ServerProt` through
`Session.encodePacket` (which writes `writeSmart(opcode)` + length) is **wrong** —
that smart-opcode byte is the very thing that becomes the bogus varc length. So
Option 1 (raw write) is cleaner.

**Checklist (both options):**

1. After SUCCESS(2), send Part A = `00 01 01` (varc len `0x0001`, ackFlag `0x01`).
   This is the empty-but-valid server-client-var block that skips the crashing loop
   (§9.2) and advances the state machine.
2. Send Part B = `02` (players byte). The client requires `==2` (step 0x82/0x88) to
   proceed to the login-data block.
3. Send Part C = `[1-byte len][00 leadFlag][WorldLoginDetails body][reserved16 u16]
   [serverTimeDelta u32][sessionId1 u64][sessionId2 u64]`. Length is 1 byte (≤255).
4. Do **not** smart-opcode-frame any of this and do **not** ISAAC-encrypt it. It is
   all pre-ISAAC, read by the LoginManager state machine, not the game pump.
5. `WorldLoginProbe.kt` must be updated to match: after the SUCCESS byte it should
   read `00 01 01 02` then the 1-byte login-data length + body, NOT a smart opcode
   `0x02` + varByte len. (The current probe asserts the *old* wrong framing, so it
   "passes" against darkan's wrong output — it has been validating the bug.)

This eliminates the NULL `GetVarcType` deref two ways at once: the varc loop never
iterates (length 1), and the `WorldLoginDetails` bytes are no longer misread as a
varc block.

### 9.7 SECONDARY — the client→server world-login block (machine-info flag reads 9, expects 6)

This is a *different* drift on the **request** side and is **not** the crash
cause (the crash is purely the response). darkan's `WorldServer.initWorldLogin`
(WorldServer.kt ~lines 232-249) parses the XTEA-decrypted login tail as:

```
stringUsername(bool) → username(jstr or u64) → displayMode(u8) → screenWidth(u16)
→ screenHeight(u16) → unknown2(u8) → randomDat(24) → settings(jstr) → affid(u32)
→ prefSize(u8) + prefs[prefSize] → machineInfoFlag(u8)   ← darkan expects 6
```

The real client's `SendLoginPacket` @ `0x1c19c0` writes a **richer tail** between
`username` and the prefs/machine block than darkan parses (boot doc 03 §"WIRE
FORMAT — full credentials login", fields 6–18). Per doc 03 the client emits, after
the RSA block: a reconnect-token-present byte, username-or-token, locale/scope
flags, a **24-byte UID/affiliate field**, a **site-settings string**, a
**version/build u32**, a **window-mode descriptor** (1 byte + buffer), a
**serialized preferences blob**, a **count u32 + int[] capability list**, two more
strings, and assorted flag bytes — *then* the per-client tail (`FUN_00179d90`).
darkan's parser collapses much of this, so after `prefSize`/prefs the cursor lands
on a byte the client wrote as something else, and darkan reads `9` where it expects
the machine-info marker `6`. The reads still "succeed" enough that the RSA magic
(10) and ISAAC keys are recovered correctly (which is why login proceeds to
SUCCESS at all), but the username/tail parse is misaligned.

**Status:** the exact corrected C→S widths require a full field-by-field decompile
of `SendLoginPacket` @ `0x1c19c0` (4400+ bytes; not completed in this pass — the
response-side crash was the blocker and is fully resolved above). What is certain:

- The misalignment is **after the username**, in the window-mode / settings /
  capability-list region (doc 03 fields 6–14), where darkan reads
  `displayMode/width/height/unknown2/randomDat(24)/settings/affid/prefSize` but the
  client wrote additional locale flags, a 24-byte UID block, a version int, a
  window-mode descriptor, a prefs blob, and a count+int[] list.
- The reads darkan *does* make are individually plausible (it lands on a `6` vs `9`
  rather than garbage), so the realignment is a matter of **inserting the missing
  intermediate fields**, not a wholesale rewrite.

**Recommended action:** commission a focused RE pass to decompile `SendLoginPacket`
@ `0x1c19c0` (world branch, `+0x20==2`, non-reconnect) and produce the exact
post-username field table, then realign `WorldServer.initWorldLogin`'s XTEA-tail
reads to match. Because the username is recovered correctly today (XTEA + RSA keys
are right), this drift is **non-fatal for first light** — the crash fix in §9.6 is
what unblocks world entry. Track the C→S realignment as a follow-up.

### 9.8 Evidence index (addresses cited)

All response-side from Linux `rs2client.948-5` (`gzf-947-3` project), verified
against Mac `mac-9485` twins where noted:

| Function | Linux @ | Mac twin @ | Role |
|----------|---------|------------|------|
| `LoginStepDealWithFirstResponse` | `0x1b1e80` | — | step 0x60; result-2/world → 0xfa (§9.1) |
| `LoginStepWaitingServerClientVarLength` | `0x180d00` | `0x1000cd2a0` | step 0xfa; u16 BE length (§9.2) |
| `LoginStepWaitingServerClientVarConfigData` | `0x180bf0` | `0x1000cd350` | step 0x104; read length bytes (§9.2) |
| `LoginStepWaitingServerClientVar` | `0x18f280` | `0x1000cd4c0` | step 0x10e; varc walk + crash (§9.2) |
| `LoginStepWaitingPlayersPacketReconnect` | `0x180b40` | — | step 0x82; players byte (==2) |
| `LoginStepDealWithThirdResult` | `0x1b1c70` | — | step 0x88; byte==2 → 0x8c |
| `LoginStepWaitingLoginCredentialsLength` | `0x180ac0` | — | step 0x8c; 1-byte login-data length |
| `LoginStepHandleLoginData` | `0x1cd360` | — | step 0x96; world-path body (§9.4) |
| `FUN_0017a620` | `0x17a620` | — | leadFlag==1 extended 4-byte ISAAC prefix |
| `FUN_00afd8d0` | `0xafd8d0` | — | jstr (NUL-terminated CP1252) reader |

> **No Ghidra modifications were made.** Both projects are read-only references and
> the flow was established with full certainty from named-symbol decompiles; per
> the project's documentation-handoff model the finding is delivered here, not as
> renames in the binary DB.

---

## 10. Play-Now world-connect target resolution (RE pass — 2026-06-23)

> **Status: definitive, decompiled against the live `rs2client.948-5`** (Ghidra MCP,
> image base `0x0`, absolute addresses). This pass answers the open question the
> §1/§5 plan left implicit: **where the world TCP `connect()` actually gets its
> host:port on a "Play Now" click, and whether op212/op213 are required.** It was
> commissioned because a production (golden, conn-tagged) capture shows the
> **production lobby socket sends NEITHER op212 (`SET_WORLD_TARGET`) NOR op213
> (`SWITCH_WORLD`) — ever** (only `op49 ChangeLobby` and `op216 WorldListPacket`),
> yet the live client still enters a world on port 443 per-world IP. That directly
> contradicts §1's "fire 212 then 213 on the `906/81` click" prescription. This
> section resolves the contradiction from the binary. Ghidra DB was annotated
> (renames left intact; decompiler comments added on every function cited).
>
> **This SUPERSEDES §1's claim that the server must drive `SET_WORLD_TARGET` +
> `SWITCH_WORLD` on a cold-lobby Play-Now click. It does NOT.** §1's cache decode
> of *which component* is "Play Now" (906/81) is still correct and useful; what is
> wrong is the conclusion that the cold-lobby world connect needs op212/op213.

### 10.0 VERDICT for the server team (read this first)

> **The world connect host:port for a cold-lobby "Play Now" is delivered IN THE
> LOBBY LOGIN RESPONSE** (the same post-SUCCESS login-data block parsed by
> `LoginStepHandleLoginData`, §9.4 / §10.3), NOT by op212, op213, or op216.
> `op216` is **display-only** (the world list the user browses). `op212`/`op213`
> are for the **in-game world-SWITCH/hop** flow (state 0x23/0x25), a *different*
> client transition than the cold-lobby Play-Now path.
>
> **darkan's current `op212 SetWorldTarget` + `op213 SwitchWorld` handoff on a
> 906/81 click is the WRONG client path for cold-lobby entry.** It forces the
> client through the in-game world-SWITCH transition (`SetMainState(0x25)`), which
> is the reconnect/hop machine — not the clean lobby→world login the production
> client uses. That mismatch is consistent with the reported black-screen.
>
> **The faithful private-server mechanism (what production does):**
> 1. The **lobby** server embeds the **world host + portA + portB + worldId** in
>    the lobby login-data block (the LOBBY-mode `HandleLoginData` body, §10.3).
> 2. The client parses them in `LoginStepHandleLoginData`, builds a `WorldTarget`
>    struct, stores it as the **PENDING** target at `WorldSwitcher+0xa8`, and calls
>    `WorldSwitcher::CommitWorldTargetFromLogin` to promote it to the **CURRENT**
>    target at `WorldSwitcher+0x20`.
> 3. On the `906/81` click (delivered as `IF_BUTTON`), the lobby tells the client
>    to begin world login by driving a **main-state transition to 0x23** (the
>    clean lobby→world path) — **OR**, if you keep using op213, accept that op213's
>    `SetMainState(0x25)` *also* funnels into the same `StartWorldLogin`, just via
>    the hop machine. Either way the client connects to the **CURRENT** target
>    (`WorldSwitcher+0x20`), i.e. **the host:port from the login response** unless
>    op213 overwrote it with its own packet fields.
> 4. The world login (login type 2) connects via
>    `LoginStepWaitingConnectionOpened` → `OpenConnection(host, port)` reading
>    `WorldSwitcher+0x20`.
>
> **There is NO hardcoded port 443 anywhere** in the connect path — the port is a
> plain `u16` taken from the target struct (`+0x28` or `+0x2a`, selected by the
> `+0x2c` byte). A non-443 world port IS expressible without op212 — just put it in
> the lobby login response's portA/portB fields. Production uses 443 only because
> its login response carries 443.
>
> **Concrete options for darkan (same-host, different-port lobby vs world):**
> - **Option A (faithful):** put the world's host + world-port (e.g. 43597) in the
>   lobby login-data block's world-target fields. Drop op212/op213 from the
>   Play-Now handler. On the 906/81 click, drive the world login by the same means
>   production does (state→0x23; see §10.5 for the trigger question — the click may
>   already be sufficient, see INFERRED note). The client connects to
>   `host:43597` from the login-response target. This matches production's "no
>   per-click handoff packet" behaviour.
> - **Option B (op213 only, host-first-corrected):** keep sending **only op213**
>   `SWITCH_WORLD` (host + worldId + portA + portB + reconnectFlag=0) on the click.
>   op213 writes its packet host:port into the CURRENT target AND drives
>   `SetMainState(0x25)` → `StartWorldLogin`. This *works* (the connect reads the
>   same `+0x20` slot op213 just wrote), but it routes through the hop machine, not
>   the cold-lobby path — slightly off-faithful, and op213's field order must be
>   correct (worldId-first per §3.2 FLAG / §10.4). **op212 is redundant in this
>   option** — op213 sets the target itself.
> - **op212 is never needed for a normal cold-lobby entry.** Its slot
>   (`WorldSwitcher+0x30`) is only read on **non-world** login types (lobby /
>   reconnect, `loginMgr+0x20 != 2`), not on the world login (type 2) that
>   Play-Now triggers. Sending op212 before op213 is harmless but pointless for the
>   connect.

### 10.1 The connect site — `LoginStepWaitingConnectionOpened` (VERIFIED)

`jag::LoginManager::LoginStepWaitingConnectionOpened` @ **`0x001825a0`** (login
step `0xe` → `0xf`) is the world/lobby connect site. Full decompile:

```c
undefined8 LoginStepWaitingConnectionOpened(long loginMgr) {
  if (*(int *)(loginMgr + 0x10) != 0xe) return 1;          // login step must be 0xe
  worldSwitcher = *(long *)(__DT_RELA[0xd01] + *(long *)(loginMgr + 0x18));  // client+0x18 -> d01
  if (*(int *)(loginMgr + 0x20) == 2)                       // LOGIN TYPE == 2  (WORLD)
       target = *(long *)(worldSwitcher + 0x20);            //   -> CURRENT target  ◀── login-response/op213
  else                                                       // type 0/1 (lobby/reconnect)
       target = *(long *)(worldSwitcher + 0x30);            //   -> EXPLICIT target (op212 slot)
  port = *(undefined2 *)(target + 0x2a);                    // portB (default)
  if (*(char *)(target + 0x2c) == '\0')
       port = *(undefined2 *)(target + 0x28);               // portA  (when select byte +0x2c == 0)
  ServerConnection::OpenConnection(*(undefined8 *)(loginMgr + 0x30), target + 0x10 /*host*/, port);
  *(int *)(loginMgr + 0x10) = 0xf;
  return 1;
}
```

**Load-bearing facts:**
- The connect target slot is chosen by **login type** (`loginMgr+0x20`): **type 2
  (WORLD) reads `WorldSwitcher+0x20`** (the CURRENT target); any other type reads
  `WorldSwitcher+0x30` (the EXPLICIT/op212 target).
- **Play-Now world login is type 2** (it reaches here via `StartWorldLogin` →
  `StartLogin(mode=2)`, §10.2), so the connect uses **`WorldSwitcher+0x20`**.
- The host is the EASTL string at `target+0x10`; the port is `target+0x28` (portA)
  or `target+0x2a` (portB), selected by `target+0x2c`.

`jag::game::ServerConnection::OpenConnection` @ **`0x00b21d70`** (VERIFIED): formats
`"%s:%u"` from (host, port) and hands it to `ClientStream::ClientStream(...)` to
open the TCP socket. **The port is a plain `u16` argument — no `443`/`0x1bb`
literal anywhere.** (Cross-checked: the megafunction `LoginProtocolHandler`
@`0x0024e8c0` contains the identical `OpenConnection` call inside its step-10
branch, reading `WorldSwitcher+0x30` for the non-world login type; the two are the
same logic selected by login type. No `443` constant appears in either.)

### 10.2 How world login is reached — the state machine (VERIFIED)

```
op213 SWITCH_WORLD handler (0x001aeba0)  ── SetMainState(client, 0x25) ──┐
                                                                          │
RunLoginStateMachine (0x001b27f5): if mainState==0x25 && lobby result OK  │
                                   ── SetMainState(client, 0x23) ─────────┤
                                                                          ▼
SetMainState (0x004900d0) walks listener vtable[0x10] ──► LoginManager::OnMainStateTransition (0x001b2b30)
    newState == 0x25 ──► StartWorldLogin(loginMgr, 0, ...)
    newState == 0x23 ──► (0x25->0x23 promotion: copies 'previous' target +0x50 -> current via
                          WorldSwitcher::SetWorldTarget if +0x58 reconnect flag; -> 0x24) then
                          StartWorldLogin(loginMgr, loginMgr+0xec, ...)
                                                                          ▼
StartWorldLogin (0x001b1760) ──► StartLogin(loginMgr, loginType = 2 /*WORLD*/, sessionToken@cf6, ...)
    sets login step (loginMgr+0x10) = 10
                                                                          ▼
[per tick] login step 10 -> LoginStepInit (0x00182610) builds handshake hello, step -> 0xc -> 0xd ...
           -> LoginStepWaitingConnectionOpened step 0xe (0x001825a0)
              ──► OpenConnection(host, port) reading WorldSwitcher+0x20  (login type 2)  ◀── CONNECT
              step -> 0xf
```

- **`StartWorldLogin` @ `0x001b1760` is called ONLY from `OnMainStateTransition`**
  (xrefs: `0x1b2cc5`, `0x1b2d1a`). It always calls `StartLogin(..., 2, ...)` —
  **login type 2 = WORLD** — which is exactly what makes the connect read
  `WorldSwitcher+0x20`. (VERIFIED.)
- **`OnMainStateTransition` is a Client main-state listener** (invoked via
  `SetMainState`'s `vtable[0x10]` listener walk — hence it has no direct call
  xrefs). Both `newState==0x23` and `newState==0x25` funnel into `StartWorldLogin`.
  (VERIFIED.)
- **State meanings (VERIFIED from the transition code + handlers):** `0x14` (20) =
  lobby-idle (set at the end of `LoginStepHandleLoginData` for a cold lobby login,
  type 0); `0x23` (35) = world-login-in-progress (drives `StartWorldLogin`; a
  successful world login result-2 promotes 0x23→0x24 in
  `LoginStepDealWithFirstResponse` @ `0x001b237b`); `0x24` (36) =
  world-login-committed; `0x25` (37) = world-switch/reconnect (op213 sets this;
  `RunLoginStateMachine` promotes 0x25→0x23 once the lobby result is OK); `0x1e`
  (30) = in-world/playing.

### 10.3 The lobby login response carries the world target (VERIFIED)

`jag::LoginManager::LoginStepHandleLoginData` @ **`0x001cd360`**, **LOBBY branch**
(`loginMgr+0x20 != 2`; the `else` of the world `+0x20==2` branch). Near the tail,
after the player/account block, it parses the **world-connect target** and stages
it on the WorldSwitcher. Verified field reads (cursor = `packet->position`,
`loginMgr+0x18 = client`, helpers: `FUN_00121a30 = g2` BE u16, `FUN_00126e90 = gStr`
NUL-terminated CP1252, `0x00122820 = gT_ulong`, `0x00121a60 = gT_unsigned_int`):

| Field (lobby tail) | Reader | Type | Dest | Notes |
|---|---|---|---|---|
| (player/account block) | — | — | record `lVar29` `+0x08..+0x88` | rights/modLevel/bools/dob/ids etc. (the §9.4-style body, lobby layout) |
| host-or-name string A | `FUN_00126e90` | gStr | record `+0x68` | a NUL-terminated string read mid-block (`@0x001cd7c7`) |
| (several g2/g4 fields) | `FUN_00121a30`/`0x00121a60` | — | `+0x1c..+0x84` | interface/skill/misc counters |
| extra byte | g1 | u8 | record `+0x84` | `@0x001cd80d` |
| (u32) | `gT_unsigned_int` | u32 | record `+0x80` | `@0x001cd82c` |
| **worldId** | `FUN_00121a30` | **g2 (BE u16)** | `local_60` | `@0x001cd83a`; **`0xffff` → `-1`** (no world) |
| **host** | `FUN_00126e90` | **gStr (NUL CP1252)** | `local_88` | `@0x001cd878`; the world connect host/IP |
| **portA** | `FUN_00121a30` | **g2 (BE u16)** | `local_40` (uVar9) | `@0x001cd880` |
| **portB** | `FUN_00121a30` | **g2 (BE u16)** | `local_3e` (uVar10) | `@0x001cd88e` |
| sessionId1 | `gT_ulong` | u64 BE | `loginMgr+0xf0` | `@0x001cd89a` |
| sessionId2 | `gT_ulong` | u64 BE | `loginMgr+0xf8` | `@0x001cd8a9` |

It then (`@0x001cd916`-`0x001cd9a9`) builds a **0x30-byte `WorldTarget`** and stores
it as the **PENDING** target:

```c
WorldTarget {  // operator_new(0x30); vtable wrapper PTR_FUN_01365980
  +0x00  base   = client + 0x195e8;       // string-pool / vtable base
  +0x08  worldId (int)  = local_60;        // 0xffff -> -1
  +0x10  host (EASTL string) = local_88;   // copied via FUN_00493e00
  +0x28  portA (u16) = local_40;
  +0x2a  portB (u16) = local_3e;
  +0x2c  flag = 1;                          // the OpenConnection port-select byte
}
worldSwitcher = client[+0x19538];           // == __DT_RELA[0xd01] base
worldSwitcher[+0xa8] = WorldTarget;          // PENDING target struct
worldSwitcher[+0xa0] = refcount wrapper;     // wraps it
```

Then it calls **`WorldSwitcher::CommitWorldTargetFromLogin(worldSwitcher)`** (at
`0x001ce102`, and a struct-compare branch at `0x001ce4c5`).

> **Note (VERIFIED):** the **display `worldName` string** read into `client+0x197b0`
> (`@0x001ce308 LEA RSI,[RDI+0x197b0]` → `FUN_00afd8d0`) is in the **WORLD branch**
> (`+0x20==2`, §9.4 field 11), NOT the lobby branch. The lobby branch's `local_88`
> string is the **connect host**, a different field. So the lobby login response
> carries the *connect host:port*; the world login response carries the *display
> world name*. Do not conflate them.

### 10.4 `CommitWorldTargetFromLogin` — pending → current (VERIFIED)

`jag::WorldSwitcher::CommitWorldTargetFromLogin` @ **`0x001acdf0`**:

```c
void CommitWorldTargetFromLogin(long worldSwitcher) {
  pending = worldSwitcher[+0xa8];            // the login-parsed PENDING target
  if (pending->worldId[+8] == -1) return;     // no world id -> nothing to commit
  current = worldSwitcher[+0x20];             // the CURRENT target (what the connect reads)
  if (current == 0) {
    // allocate a fresh 0x30 current target + wrapper@+0x18, copy id/host/portA/portB
    ...
    worldSwitcher[+0x20] = newCurrent; worldSwitcher[+0x18] = wrapper;
  } else {
    current->worldId[+8] = pending->worldId[+8];
    copy current->host[+0x10] = pending->host[+0x10];   // FUN_00493e00
    current[+0x28] = pending[+0x28];  current[+0x2a] = pending[+0x2a];  // portA/portB
  }
}
```

It **promotes the PENDING target (`+0xa8`) into the CURRENT target (`+0x20`)** —
copying worldId, host, portA, portB. **It does NOT open a connection.** It only
stages the `+0x20` slot that `LoginStepWaitingConnectionOpened` later reads for the
world (type-2) connect. (VERIFIED — called only from `LoginStepHandleLoginData`.)

So the data path for a faithful cold-lobby entry is:

```
lobby login response (host/portA/portB/worldId)
   └─► LoginStepHandleLoginData builds WorldTarget @ WorldSwitcher+0xa8 (PENDING)
         └─► CommitWorldTargetFromLogin: +0xa8 ──copy──► WorldSwitcher+0x20 (CURRENT)
               └─► [on world login, type 2] LoginStepWaitingConnectionOpened reads +0x20
                     └─► OpenConnection(host, portA|portB) ── TCP connect
```

### 10.5 What op212 and op213 each do (VERIFIED), and the Play-Now click (INFERRED)

**op212 `SET_WORLD_TARGET` handler @ `0x0017fc70` (VERIFIED).** Wire (matches §3.1):
`gStr host, g2 worldId, g2 portA, g2 portB`. Writes a **0x50-byte** target (vtable
`PTR_FUN_013657a0`) into the WorldSwitcher **EXPLICIT** slot: wrapper `+0x28`,
struct `+0x30` (host `+0x10`, worldId `+0x8`, portA `+0x28`, portB `+0x2a`). **It
does NOT call `SetMainState`** — pure store, no transition. The `+0x30` slot it
writes is read by `LoginStepWaitingConnectionOpened` **only for NON-world login
types** (`loginMgr+0x20 != 2`). **It is NOT read by the cold-lobby world login
(type 2).** Hence op212 is irrelevant to a normal Play-Now connect.

**op213 `SWITCH_WORLD` handler @ `0x001aeba0` (VERIFIED).** Wire (per §3.2; note the
FLAG there): `g2 worldId, gStr host, g2 portA, g2 portB, g1 reconnectFlag`. It:
1. Copies the CURRENT target (`+0x20`) into the **previous** slot (wrapper `+0x48`,
   struct `+0x50`), and stores `reconnectFlag` at `worldSwitcher+0x58`.
2. Builds a new CURRENT target from the **packet** fields → wrapper `+0x18`, struct
   `+0x20` (overwriting whatever `CommitWorldTargetFromLogin` put there).
3. Calls **`Client::SetMainState(client, 0x25)`** → `OnMainStateTransition` →
   `StartWorldLogin` (type 2) → connect using `+0x20`.

So **op213 both sets the connect target (from its own packet) AND drives the
world-switch transition.** It is the **in-game world-hop** path. It *does* connect
the cold-lobby client too (because the connect reads the `+0x20` it just wrote),
but via the 0x25 hop machine rather than the clean lobby→world path — and only if
its field order is correct (§3.2 FLAG: doc reads worldId-first; verify the darkan
encoder matches, or the client parses a garbage host).

**Play-Now click (906/81) — what it does client-side (INFERRED, partially):**
- §1 established (cache decode) that `906/81` "Play Now" carries **no client-side
  CS2 handler** — the click is delivered to the server as `IF_BUTTON`. (Consistent
  with this RE; no contradicting evidence found.)
- The client reaches world login **only via a main-state transition to 0x23 or
  0x25** (§10.2, VERIFIED). Nothing else opens a world socket — there is no
  autonomous "connect on click" path that bypasses the state machine.
- **What is NOT yet pinned to a single instruction:** which exact server action (or
  client-internal CS2/event) drives the cold-lobby `0x14 → 0x23` transition on a
  Play-Now click in the *production* (no-op212/op213) flow. The state-transition
  machinery is fully mapped (0x23 and 0x25 both → `StartWorldLogin`), and the
  connect target for a 0x23-driven world login is unambiguously the
  login-response-derived CURRENT target (`+0x20`). But the precise *trigger* for
  0x23 in production (a CS2 `SetMainState` opcode fired by the lobby UI on the
  click, vs. a lobby S2C packet handler) was not isolated in this pass — the
  `SetMainState(0x23)` sites found are `LoginStepDealWithFirstResponse` (the
  0x23→0x24 success promotion) and `RunLoginStateMachine` (the 0x25→0x23
  promotion). **This is the one INFERRED gap.** It does not change the verdict
  (target comes from the login response either way), but if Option A (drop
  op212/op213) does not trigger the connect, the lobby must drive `SetMainState`
  to 0x23/0x25 by *some* means — and the simplest faithful-ish lever that is fully
  VERIFIED to work is op213 (Option B). A follow-up pass should decompile the lobby
  CS2/IF_BUTTON dispatch for the 906/81 component to find production's exact 0x23
  trigger.

### 10.6 WorldSwitcher struct fields touched (VERIFIED)

`WorldSwitcher` is reached via `client + 0x19538` (the resolved `__DT_RELA[0xd01]`
value; confirmed at `@0x001cd91f` and in every handler above).

| Offset | Meaning | Written by | Read by |
|---|---|---|---|
| `+0x18` | refcount wrapper for CURRENT target | Commit / op213 / SetWorldTarget(MainStateTransition) | — |
| `+0x20` | **CURRENT WorldTarget*** (the world-login connect target) | `CommitWorldTargetFromLogin` (from login resp), `SWITCH_WORLD`/op213 (from packet), `WorldSwitcher::SetWorldTarget` (from 'previous' on 0x25→0x23) | **`LoginStepWaitingConnectionOpened` for login type 2** |
| `+0x28` | refcount wrapper for EXPLICIT target | `SET_WORLD_TARGET`/op212 | — |
| `+0x30` | **EXPLICIT WorldTarget*** (op212 slot, 0x50-byte struct) | `SET_WORLD_TARGET`/op212 | `LoginStepWaitingConnectionOpened` for login type != 2 |
| `+0x48` | refcount wrapper for PREVIOUS target | `SWITCH_WORLD`/op213 | — |
| `+0x50` | **PREVIOUS WorldTarget*** (saved current, for hop-back) | `SWITCH_WORLD`/op213 | `OnMainStateTransition` (0x25→0x23, if +0x58 set) |
| `+0x58` | reconnect/hop flag (`byte == 1`) | `SWITCH_WORLD`/op213 | `OnMainStateTransition` |
| `+0xa0` | refcount wrapper for PENDING (login) target | `LoginStepHandleLoginData` | — |
| `+0xa8` | **PENDING WorldTarget*** (parsed from lobby login response) | `LoginStepHandleLoginData` | `CommitWorldTargetFromLogin` (promotes to `+0x20`) |

**`WorldTarget` struct (0x30 bytes; the `+0x20`/`+0xa8` variant):**

| Offset | Type | Field |
|---|---|---|
| `+0x00` | ptr | base (`client+0x195e8`, string-pool/vtable base) |
| `+0x08` | int | worldId (`-1` = none; `0xffff` from wire maps to `-1`) |
| `+0x10` | EASTL string (24 bytes, SSO, `0x17` marker at `+0x27`) | host / IP |
| `+0x28` | u16 | portA |
| `+0x2a` | u16 | portB |
| `+0x2c` | u8 | port-select flag (`OpenConnection`: `==0` → portA, else portB); set to `1` by the login-response builder |

(op212's EXPLICIT target is 0x50 bytes with a different vtable but the same
host/worldId/portA/portB field offsets at `+0x10`/`+0x08`/`+0x28`/`+0x2a`.)

### 10.7 Answers to the four questions (summary)

1. **Where does the world `connect()` get host/port on Play-Now?**
   From `WorldSwitcher+0x20` (the **CURRENT** target), read by
   `LoginStepWaitingConnectionOpened` @ `0x001825a0` for login type 2, passed to
   `OpenConnection` @ `0x00b21d70` which formats `"host:port"`. The CURRENT target
   is populated **from the lobby login response** via
   `CommitWorldTargetFromLogin` (candidate (a) in the prompt — **confirmed**). op216
   supplies NO port and is display-only; op212 writes a *different* slot (`+0x30`)
   read only for non-world logins. A **non-443 port IS expressible without op212** —
   put it in the login response portA/portB. (VERIFIED.)
2. **What does the 906/81 click do client-side?**
   It routes to the server as `IF_BUTTON` (no client CS2 handler, §1). The client
   reaches the world socket **only through a main-state transition to 0x23/0x25**,
   both of which call `StartWorldLogin` (type 2) → connect from `WorldSwitcher+0x20`
   (VERIFIED). The client does **not** connect autonomously bypassing the state
   machine. The *exact production trigger* for the cold-lobby 0x14→0x23 transition
   on the click is the one **INFERRED gap** (§10.5) — but the connect target is the
   login-response CURRENT target regardless.
3. **What do op212/op213 do; which transition?**
   op212 `SET_WORLD_TARGET` = pure store into the EXPLICIT slot (`+0x30`), **no
   state transition**, read only for non-world logins → **irrelevant to Play-Now**.
   op213 `SWITCH_WORLD` = stores a new CURRENT target from its packet AND calls
   `SetMainState(0x25)` → the **in-game world-SWITCH/hop** transition (0x25, which
   `RunLoginStateMachine` promotes to 0x23) → `StartWorldLogin`. op213 IS the
   in-game hop path, **different** from the clean cold-lobby login path; it is not
   *required* for the cold-lobby connect (the login response already staged the
   target), and using it routes the client through the hop machine. (VERIFIED.)
4. **Net conclusion for darkan.**
   The faithful mechanism: **deliver the world host:port (and worldId) in the lobby
   login response** (LOBBY-mode `HandleLoginData` body — the host=`local_88`,
   portA=`local_40`, portB=`local_3e`, worldId=`local_60` fields, §10.3). op216 is
   display-only; op212/op213 belong to the in-game world-SWITCH flow, not cold
   lobby Play-Now. **Port 443 is NOT special/hardcoded** anywhere; it is whatever
   the login response carries. **darkan's op212+op213 handoff is wrong-path for
   cold-lobby entry** (forces the 0x25 hop transition) and op212 is redundant; the
   correct fix is Option A (§10.0). If the click alone does not trigger 0x23 on
   darkan, Option B (op213 only, worldId-first order) is the VERIFIED-to-connect
   fallback.

### 10.8 Function / offset reference (this pass)

All addresses are `rs2client.948-5`, image base `0x0` (Ghidra MCP, annotated):

| Function / data | @ | Role |
|---|---|---|
| `LoginManager::LoginStepWaitingConnectionOpened` | `0x001825a0` | **the connect site** — reads `WorldSwitcher+0x20` (type 2) / `+0x30` (else), calls `OpenConnection` |
| `game::ServerConnection::OpenConnection` | `0x00b21d70` | formats `"%s:%u"` host:port, opens TCP via `ClientStream` (no 443 literal) |
| `LoginManager::LoginStepHandleLoginData` | `0x001cd360` | lobby branch parses world host/portA/portB/worldId, builds PENDING target `+0xa8`, calls Commit |
| `WorldSwitcher::CommitWorldTargetFromLogin` | `0x001acdf0` | promotes PENDING (`+0xa8`) → CURRENT (`+0x20`); no connect |
| `LoginManager::StartWorldLogin` | `0x001b1760` | `StartLogin(mode=2)`; called only from `OnMainStateTransition` |
| `LoginManager::StartLogin` | `0x001b1130` | sets login step (`+0x10`) = 10; stashes host/token; arms step machine |
| `LoginManager::OnMainStateTransition` | `0x001b2b30` | listener `vtable[0x10]`; 0x23 & 0x25 → `StartWorldLogin` |
| `Client::SetMainState` | `0x004900d0` | walks listener vtable[0x10] (oldState,newState); drives transitions |
| `LoginManager::RunLoginStateMachine` | `0x001b27f5` | promotes mainState 0x25 → 0x23 when lobby result OK |
| `LoginManager::LoginStepDealWithFirstResponse` | `0x001b237b` | world result-2 promotes 0x23 → 0x24 |
| `WorldData::SET_WORLD_TARGET` (op212) | `0x0017fc70` | stores EXPLICIT target `+0x30`; no transition |
| `WorldData::SWITCH_WORLD` (op213) | `0x001aeba0` | stores CURRENT `+0x20` from packet + `SetMainState(0x25)` |
| `WorldSwitcher::SetWorldTarget` | `0x001acd00` | copies a target into CURRENT (used on 0x25→0x23 hop-back) |
| `Packet::gT<unsigned_short>` (g2 BE) | `0x00121a30` | 2-byte BE read helper |
| `Packet` gStr (NUL CP1252) | `0x00126e90` | string read helper |
| `WorldSwitcher` base | `client + 0x19538` | resolved `__DT_RELA[0xd01]` |

> **Ghidra DB changes this pass:** decompiler comments added on `0x001825a0`,
> `0x00b21d70`, `0x001acdf0`, `0x0017fc70`, `0x001aeba0`, `0x001b2b30`,
> `0x001b1760`, `0x001b27f5`, `0x00121a30`, `0x00126e90`, and the lobby-tail parse
> site `0x001cdf2f`. `Client::SetMainState` prototype set to
> `void SetMainState(long client, int newState)`. No renames were changed (the
> `WorldSwitcher`/`LoginManager`/`LoginStep*` names were already present in the DB).
