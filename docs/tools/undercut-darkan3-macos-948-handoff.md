# Undercut + Darkan3 macOS 948 Handoff

Date: 2026-06-22

This document captures the current state of the macOS RuneScape 948-5 recording
and Darkan3 world-login parity work. It is meant as the first file to read before
continuing this project.

## Repositories

Primary repos:

- `/Users/robert/projects/undercut-engine`
- `/Users/robert/projects/darkan3-server`
- `/Users/robert/projects/reclass-data`

Runtime/client locations used in this work:

- Production RuneScape wrapper: `/Applications` or Jagex launcher controlled by
  the normal installed app. Do not open production unless explicitly asked.
- Darkan local macOS wrapper: `/Users/robert/darkan-3/macos/RuneScape.app/Contents/MacOS/RuneScape`
- Darkan local downloaded child client: `/Users/robert/.darkan3-mac/Jagex/launcher/rs2client`
- Staging direct rs2client: `/Users/robert/rs-re-staging/rs2client.948-5-mac`

The macOS wrapper is a launcher. The actual NXT client is the child `rs2client`
downloaded under the launcher data dir. The child has been unsigned in the
working setup, runs x86_64 under Rosetta, and is the process that needs protocol
patching, recording, or Undercut injection.

## Current Outcome

Working pieces:

- Undercut can record a whole production session as JSONL plus pcap sidecar.
- Undercut macOS ISAAC seed hook works against the current 948 production child
  rs2client and emits timestamped raw C2S seeds.
- Darkan tools can deframe production lobby and world socket streams offline
  using those seeds.
- A fresh production seed-bearing capture decoded with zero desyncs and zero
  truncations.
- Darkan3 can launch the patched macOS wrapper and serve local config/cache.
- Darkan3 lobby login reaches the lobby UI.
- `Play Now` on interface `906/81` sends `SET_WORLD_TARGET` then `SWITCH_WORLD`.
- The client opens the world TCP connection after that handoff.

Current blocker:

- Local Darkan3 world entry still exits or crashes after the world connection is
  opened.
- This is no longer a pure lobby `Play Now` trigger problem. Latest server logs
  prove the world socket opens, world login credentials parse, and the server
  sends early world init packets.
- The client then starts one encrypted world C2S packet and exits before a full
  packet is available. Current diagnostic example:

```text
Missing ClientProt opcode=236 wire=0x1a cipher=0x2e isaacIndex=1 available=0 tail=
```

That line is evidence of a post-handoff world bootstrap failure. It should not
be treated as proof that op236 is a real missing ClientProt.

## Search Rules

In `/Users/robert/projects/undercut-engine`, use FFF MCP first:

- File/module discovery: `mcp__fff.find_files`
- Identifier/content search: `mcp__fff.grep` or `mcp__fff.multi_grep`
- Use `rg` only for raw text, regex, freshness checks, or fallback.

In `/Users/robert/projects/darkan3-server`, FFF may still be indexed to
Undercut depending on the Codex session. If FFF returns only Undercut paths, use
known Darkan paths plus `rg` raw-text searches.

## Undercut Architecture

Undercut injects into the running NXT client and enters Kotlin at:

```text
com.undercut.game.bootstrap.Bootstrap.initialize(baseAddr: Long)
```

Important files:

- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/bootstrap/Bootstrap.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/recording/LoginSessionRecorder.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/recording/NetworkCaptureRecorder.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/recording/MacIsaacSeedCapture.kt`
- `/Users/robert/projects/undercut-engine/native-bootstrap/undercut_macos_seed_hook.cpp`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/mcp/McpServer.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/mcp/tools/RecordingTools.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/hooks/impl/ServerPacketCapture.kt`
- `/Users/robert/projects/undercut-engine/src/main/kotlin/com/undercut/game/hooks/impl/MacSocketCapture.kt`

Bootstrap behavior:

1. Loads scripts.
2. Initializes native memory access.
3. Defers macOS client pointer attachment until the main logic hook sees the
   live client.
4. Initializes `PacketLogger`.
5. Initializes `SessionRecorder`.
6. Applies native hooks.
7. Starts MCP asynchronously.

Undercut MCP listens on:

```text
127.0.0.1:7882
```

Codex global config already contains:

```toml
[mcp_servers.undercut]
url = "http://127.0.0.1:7882"
```

If Undercut MCP tools do not appear in a Codex session:

1. Confirm the client is injected and Undercut initialized.
2. In Undercut Settings, confirm MCP is enabled.
3. Confirm port `7882` is listening.
4. Restart the Codex session if the MCP tool list was loaded before Undercut
   started.

## Undercut Build Commands

Use Java 25:

```bash
cd /Users/robert/projects/undercut-engine
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew compileKotlin
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew buildNativeBootstrap
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew shadowJar
```

Important caveat:

```text
inject loads:
/Users/robert/projects/undercut-engine/build/libs/com.undercut-1.0.0-all.jar
```

`compileKotlin` is not enough for injection testing. Run `shadowJar` before any
recording meant to validate recorder changes.

Native bootstrap must export:

```text
_Undercut_MacIsaacSeedHookInstall
_Undercut_MacIsaacSeedHookDrain
_Undercut_MacIsaacSeedHookStatus
```

## Undercut Session Recorder

The recorder object is `SessionRecorder`; `LoginSessionRecorder` is kept as a
deprecated shim.

Recording output directory:

```text
~/.undercut/recordings/session-YYYYMMDD-HHMMSS[-label]/
```

Core files:

- `events.jsonl`
- `network.pcapng`
- `network-capture.log`
- `isaac-keys.txt`

JSONL event types:

- `session_start`
- `session_stop`
- `main_state`
- `tick`
- `packet`
- `socket`
- `socket_connect`
- `socket_close`
- `network_capture_start`
- `network_capture_stop`
- `isaac_seed_hook_status`
- `isaac_seeds`

Recorder fields worth checking first:

- `main_state_name`
- `client_cycle`
- `direction`
- `fd`
- `opcode`
- `name`
- `size`
- `payload_hex`
- `payload_base64`
- `seeds_raw_c2s`
- `seeds_s2c_plus_delta`

MCP recording tools:

- `start_session_recording`
- `stop_session_recording`
- `get_session_recording_status`
- `start_login_recording`
- `stop_login_recording`
- `get_login_recording_status`

The `login_*` tools are aliases for the session recorder. Prefer
`start_session_recording` / `stop_session_recording` in new work because this is
not only a login recorder anymore.

Status fields exposed by MCP:

- `recording`
- `session_id`
- `session_dir`
- `events_file`
- `event_count`
- `file_size_bytes`
- `isaac_seed_count`
- `isaac_seed_hook_attempted`
- `isaac_seed_hook_result`
- `isaac_seed_hook_status`
- `network_capture_enabled`
- `network_capture_active`
- `network_pcap_file`
- `network_capture_log_file`
- `network_capture_interfaces`
- `network_capture_filter`
- `network_capture_pid`
- `network_capture_error`
- `network_capture_exit_code`

## Network Capture

`NetworkCaptureRecorder` starts `tshark` when session recording starts.

Default capture filter:

```text
tcp port 443 or tcp portrange 43594-43599
```

Default duration:

```text
900 seconds
```

Search order for `tshark`:

1. `/Applications/Wireshark.app/Contents/MacOS/tshark`
2. `/opt/homebrew/bin/tshark`
3. `/usr/local/bin/tshark`
4. `tshark`

Useful overrides:

```bash
UNDERCUT_TSHARK=/Applications/Wireshark.app/Contents/MacOS/tshark
UNDERCUT_PCAP_INTERFACES=en0,utun0
UNDERCUT_PCAP_FILTER='tcp port 443 or tcp portrange 43594-43599'
UNDERCUT_PCAP_DURATION_SECONDS=1200
UNDERCUT_PCAP_ENABLED=true
```

On the working Mac, Wireshark's `access_bpf` permissions allow capture without
sudo. If capture fails, check `/dev/bpf*` permissions and install Wireshark's
`Install ChmodBPF.pkg`.

## ISAAC Seed Hook

`MacIsaacSeedCapture` calls native functions from
`undercut_macos_seed_hook.cpp`.

Native hook strategy:

1. Scan loaded dyld images for an image path containing `rs2client`.
2. Parse its Mach-O segments and locate `__TEXT`.
3. Find the packed `[50,50,50,50]` ISAAC delta constant:

```text
32 00 00 00 32 00 00 00 32 00 00 00 32 00 00 00
```

4. Find `PADDD XMM0, [rip+disp32]` readers targeting that delta.
5. Select the reader immediately preceded by:

```text
f3 41 0f 6f 46 48    ; MOVDQU XMM0, [R14+0x48]
```

6. Patch that 6-byte `MOVDQU` site with an absolute jump to an RWX stub.
7. Stub snapshots four raw C2S seeds from `[R14+0x48]`, sets a ready flag,
   replays the displaced instructions, and jumps back.
8. Kotlin drains the native scratch buffer during recorder ticks.

Expected successful status contains:

```text
installed hook=...
```

Seed records use raw C2S keys. S2C keys are raw keys plus `EnvVars.ISAAC_DELTA`
which is currently `50`.

Production note:

- A production run can emit separate seed records for lobby and world.
- `undercutSocketDeframe` chooses timestamped seed records nearest to each
  connection unless `--seeds` is passed.

## ServerPacketCapture Limits

`ServerPacketCapture` observes decoded incoming server packets inside the client.
On macOS it hooks `ConnectionManager::TcpIn` and reads the last decoded opcode,
resolved size, and payload out of the connection structure.

It cannot see packets that fail before a complete frame exists. Specifically:

- It does not see the pre-ISAAC world-login response bytes.
- It does not prove a socket stream is valid before the client's packet frame is
  complete.
- Spinner/no-spinner failures in the lobby-to-world handoff must be debugged at
  socket level using `events.jsonl` socket rows or `network.pcapng`, not only
  `packet` rows.

Previous macOS in-process libc `read`/`write` interception in Kotlin was
unstable. `MacSocketCapture` still exists but should stay unregistered unless it
is moved to a native no-JVM hot path. The working path is out-of-process pcap
plus ISAAC seed records.

## Darkan3 Build and Server Commands

Use Java 25:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :core:compileKotlin :lobby:compileKotlin :world:compileKotlin
```

Focused tests used during this work:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon \
  :core:test \
  --tests org.darkan.core.net.session.GameSessionTest \
  --tests org.darkan.core.net.prot.WorldSwitchEncoderTest \
  :world:compileKotlin \
  :lobby:compileKotlin
```

Tool self-tests:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:compileKotlin
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:recorderSelfTest
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:undercutSocketDeframeSelfTest
```

Start lobby and world with logs:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :lobby:run 2>&1 | tee build/lobby-current.log
```

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :world:run 2>&1 | tee build/world-current.log
```

Expected local ports:

- Config HTTP: `8829`
- Lobby game login: `43596`
- World game login: `43597`
- Social gateway: served by lobby on config server side

Useful live log tails:

```bash
tail -200 /Users/robert/projects/darkan3-server/build/lobby-current.log
tail -240 /Users/robert/projects/darkan3-server/build/world-current.log
tail -120 /Users/robert/projects/darkan3-server/build/client-current.log
```

## Darkan macOS Client Launch

Build the macOS patcher:

```bash
cd /Users/robert/projects/darkan3-server/client/launcher/patcher-mac
./build-mac.sh
```

Run the patched local wrapper:

```bash
cd /Users/robert/projects/darkan3-server
./run-client-mac.sh 2>&1 | tee build/client-current.log
```

`run-client-mac.sh` direct-execs the wrapper or direct client. It does not use
`open` because LaunchServices strips `DYLD_*`.

Important environment:

```bash
HOST=localhost
PORT=8829
MAC_CLIENT_MODE=wrapper        # wrapper or direct
WRAPPER_BINARY=/Users/robert/darkan-3/macos/RuneScape.app/Contents/MacOS/RuneScape
CLIENT_BINARY=/Users/robert/darkan-3/macos/Jagex/launcher/rs2client
DARKAN_RSA_MODULUS=<login RSA modulus hex>
DARKAN_JS5_RSA_MODULUS=<JS5 RSA modulus hex>
DARKAN_HTTP_PORT=8829
DARKAN_PROXY_MODE=1            # optional; skips JS5 + HTTP-port patches
DARKAN_DIR=$HOME/.darkan3-mac
```

Patcher behavior:

- Loads through `DYLD_INSERT_LIBRARIES`.
- Runs in both wrapper and child `rs2client` when inherited.
- Patches wrapper RSA/config path.
- Patches child login RSA modulus.
- Patches child JS5 RSA modulus.
- Patches child HTTP port literal from `80` to `DARKAN_HTTP_PORT`.
- Uses Mach VM protection changes because target strings/code live in `__TEXT`.

`run-client-mac-record.sh` is older Darkan recorder-dylib scaffolding. The
current preferred production-quality capture path is Undercut `SessionRecorder`
with pcap and macOS ISAAC seed hook. Keep `run-client-mac-record.sh` as a
fallback when validating `client/launcher/recorder-mac`.

## Darkan Protocol Work Done

### ClientProt Opcode Reader

File:

```text
/Users/robert/projects/darkan3-server/core/src/main/kotlin/org/darkan/core/net/Session.kt
```

What changed:

- Darkan inbound ClientProt opcodes are now read as one encrypted byte.
- The old two-byte smart opcode assumption was wrong for C2S ClientProt in 948.
- Two-byte smart opcode framing applies to ServerProt on the client side, not to
  Darkan's inbound ClientProt reader.

Why it matters:

- Production macOS world C2S starts with op240.
- The old reader would treat high opcodes as smart opcodes, consume the first
  payload byte as an opcode continuation, and desync the stream.

Diagnostics now included:

- Decoded opcode value.
- Raw encrypted wire byte.
- ISAAC cipher low byte.
- 1-based ISAAC opcode index.
- Available tail bytes if opcode metadata is missing.
- Truncated varByte/varShort length logs.
- Truncated fixed/variable payload logs.
- S2C packet send trace with opcode, size kind, and payload length.

Example diagnostic:

```text
Missing ClientProt opcode=236 wire=0x1a cipher=0x2e isaacIndex=1 available=0 tail=
```

### Live macOS ClientProt Additions

Files:

```text
/Users/robert/projects/darkan3-server/core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/Rev948ClientCodecs.kt
/Users/robert/projects/darkan3-server/core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/Rev948ClientProtStubs.kt
```

Added live-only packet metadata:

- op203, varShort: post-world macOS sync packet, sender not mapped yet.
- op218, fixed 70: macOS lobby handoff tail packet.
- op240, fixed 7: macOS post-world-login wrapper.

op218 decoder:

- Captures fixed 70-byte payload.
- Scans for embedded `(906 << 16) | 81` little-endian interface hash.
- If found, converts it to an `IfButton` for the lobby `Play Now` flow.

op240 production meaning:

- Payload observed: `5e020d80083801`.
- It is selector byte `0x5e` plus an op52 display-info payload body.
- Linux 948-5 `ClientProt::RegisterAll` does not contain op240.
- Do not give this an official enum name until the macOS sender is mapped.

### Lobby `Play Now` Handoff

File:

```text
/Users/robert/projects/darkan3-server/lobby/src/main/kotlin/org/darkan/lobby/server/packet/IfButtonHandler.kt
```

Current trigger:

```text
interfaceId = 906
componentId = 81
```

Ground truth:

- Interface `906/81` has use option `Play Now`.
- `906/3`, `906/32`, and `820/13` are world-row select/pick interactions, not
  enter-world.

Current response to `906/81`:

1. `SetWorldTarget` op212.
2. `SwitchWorld` op213.
3. Flush.

Latest local log example:

```text
IfButton from [0: interfaceId=906 componentId=81 slot=65535 item=-1 option=1
Play Now click (906/81) -> switching [0 to world 300 at localhost:43597
S2C SetWorldTarget opcode=212 size=VarByte payload=16
S2C SwitchWorld opcode=213 size=VarByte payload=17
```

The old commit `35fa983c6be08ec4d9a1fe420ab9899d1656ef96` only added the
op212-before-op213 behavior and verified that clicking the world opened a TCP
connection. Its own commit message says world login still hung. Do not treat
that commit as full world-entry proof.

### SET_WORLD_TARGET and SWITCH_WORLD

File:

```text
/Users/robert/projects/darkan3-server/core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/Rev948ServerCodecsMisc.kt
```

Current 948-5 encoders:

```text
SET_WORLD_TARGET op212:
host jstr, worldId u16 BE, portA u16 BE, portB u16 BE

SWITCH_WORLD op213:
worldId u16 BE, host jstr, portA u16 BE, portB u16 BE, reconnectFlag u8
```

This difference is intentional. `SET_WORLD_TARGET` is host-first, while
`SWITCH_WORLD` is world-id-first per the saved 948-5 decompile notes:

```text
/Users/robert/projects/reclass-data/docs/binary/boot/05-worldlist-switch.md
```

Regression test:

```text
/Users/robert/projects/darkan3-server/core/src/test/kotlin/org/darkan/core/net/prot/WorldSwitchEncoderTest.kt
```

### World Login Response

File:

```text
/Users/robert/projects/darkan3-server/world/src/main/kotlin/org/darkan/world/server/WorldServer.kt
```

Important fix already applied:

- In world mode, success response is not a single framed `WorldLoginDetails`
  packet.
- The client parses a raw pre-ISAAC three-part response after result byte `2`.

Required wire stream:

```text
[result byte 0x02]
[u16 BE varcBlockLen][server-client-var block bytes]
[playersByte 0x02]
[u8 loginDataLen][login-data body]
```

Current implementation writes:

- Production-derived server-client-var block from `WORLD_LOGIN_SERVER_CLIENT_VAR_BLOCK`.
- Players byte `0x02`.
- One-byte login-data length.
- Login body containing `leadFlag`, `WorldLoginDetails` fields, and trailing
  fields the client reads unconditionally.

This replaced the old wrong behavior:

```text
[smart opcode 0x02][varByte length][WorldLoginDetails body]
```

That old behavior made the client parse the `WorldLoginDetails` bytes as a
server-client-var block and crash.

### RebuildNormalSimple

Files:

```text
/Users/robert/projects/darkan3-server/core/src/main/kotlin/org/darkan/core/net/prot/revision/rev948/Rev948ServerCodecsRebuild.kt
/Users/robert/projects/darkan3-server/world/src/main/kotlin/org/darkan/world/server/WorldServer.kt
```

Current 948 op81 shape:

```text
VarShort body = productionRev948Prefix(5119 bytes) + 18-byte tail

tail:
+0  ignored filler
+1  centreZoneZ low
+2  centreZoneZ high
+3  magic 0x85
+4  centreZoneX u16 BE
+6  cameraRotation byteAdd
+7  ignored filler
+8  targetWorldId u16 BE
+10 packedCoordA u32 BE
+14 packedCoordB u32 BE
```

World server currently uses:

```kotlin
val centreZoneX = 400
val centreZoneZ = 400
val sceneZones = 13
val originZoneX = centreZoneX - sceneZones / 2
val originZoneZ = centreZoneZ - sceneZones / 2
packedCoordA = RebuildNormalSimple.packZoneCoord(fieldX = originZoneX, fieldZ = originZoneZ)
packedCoordB = RebuildNormalSimple.packZoneCoord(fieldX = sceneZones, fieldZ = sceneZones)
```

The previous broken approach packed raw tile coordinates and reused the same
word as the span. That produced garbage build-area geometry after the client's
mandatory `>> 6`.

### First World S2C Burst

Latest local Darkan burst:

```text
RebuildNormalSimple opcode=81 payload=5137
HashedWorldToken opcode=54 payload=44
MinimapState opcode=73
JcoinsUpdate opcode=74
MinimapFlagA opcode=172
MinimapFlagB opcode=204
SetPlayerOp opcode=17
SetPlayerOp opcode=17
SetPlayerOp opcode=17
SetPlayerOp opcode=17
SetPlayerOp opcode=17
MidiSong opcode=95
SetPlayerOp opcode=17
ResetClientVarcache opcode=5
DestroyZoneData opcode=55
SetNpcOp opcode=1
PlayerInfo opcode=22 payload=289
CamUpdate raw op77
UpdateIgnoreListRaw opcode=130
```

Production first world S2C is much richer. It starts similarly:

```text
RebuildNormalSimple op81 size 5137
HashedWorldToken op54 size 44
MinimapState op73 size 2
JcoinsUpdate op74 size 4
MinimapFlagA op172 size 1
MinimapFlagB op204 size 1
SetPlayerOp x6
MidiSong op95
ResetClientVarcache op5
```

Then production sends a large varp/varc/UI baseline before the first
`PlayerInfo`. Current local Darkan sends `PlayerInfo` almost immediately. That
ordering/coverage difference is a likely next investigation point.

## Production Recording Evidence

Fresh production ISAAC recording:

```text
/Users/robert/.undercut/recordings/session-20260622-152729-production-isaac
```

Strict socket deframe output:

```text
/Users/robert/projects/darkan3-server/build/undercut-socket-session-production-isaac.jsonl
```

Strict flow import output:

```text
/Users/robert/projects/darkan3-server/build/undercut-flow-session-production-isaac.jsonl
```

Results:

- 2 plaintext RS connections: lobby and world.
- 5553 decoded packets.
- 0 desyncs.
- 0 truncations.
- Flow phases: `SESSION`, `UNKNOWN`, `LOGIN`, `WORLD_SELECT`, `IN_GAME`.
- Required world traffic present:
  - `PLAYER_INFO`
  - `NPC_INFO`
  - zone updates
  - anti-cheat challenges
  - client keepalives

Live-only C2S deltas:

```text
LOBBY op218 fixed70 payload:
0000000512ffffffff9f00000037eb020d80083801bb020d8008380184020d80083801cf020d8008380123020d80083801711fc955ee27b8926da151008a037fffff7fd04a81

WORLD op240 fixed7 payload:
5e020d80083801
```

Early production world C2S:

```text
seq0 opcode=240 fixed7 payload=5e020d80083801
seq1 opcode=98 varByte size=247
seq2 opcode=106 fixed1
seq3 opcode=105 varShort size=1321
```

Static Linux 948-5 `ClientProt::RegisterAll` has 130 entries and does not
include 218 or 240. These are capture-derived macOS live additions, not official
enum names.

## Darkan Tools

### Flow Import

Summarizes Undercut session JSONL into redacted protocol phases and packet
coverage:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:undercutLoginFlowImport \
  -PundercutFlowArgs="/Users/robert/.undercut/recordings/session-YYYYMMDD-HHMMSS-label/events.jsonl --out build/undercut-flow.jsonl --require-full-login --require-world-traffic --require-no-unresolved-labels"
```

Use it to answer:

- Did the run reach `LOGIN -> WORLD_SELECT -> IN_GAME`?
- Which packets were seen by Undercut's decoded packet hooks?
- Which packet labels are still unresolved?
- Did the capture include real in-game world traffic?

### Socket Deframe

Deframes Undercut socket events or sibling `network.pcapng` using the 948 codec
and ISAAC keys:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:undercutSocketDeframe \
  -PundercutSocketArgs="/Users/robert/.undercut/recordings/session-YYYYMMDD-HHMMSS-label/events.jsonl --out build/undercut-socket.jsonl --strict"
```

Seed lookup order:

1. `--seeds s0,s1,s2,s3`
2. Timestamped JSONL `isaac_seeds`
3. Sibling `isaac-keys.txt`

Useful options:

```text
--out path
--seeds 0x...,0x...,0x...,0x...
--require-seeds
--strict
--fail-on-desync
--fail-on-truncation
--pcap path/to/network.pcapng
--no-pcap
--pcap-hosts 8.42.17.231,8.26.16.145
--pcap-ports 443,43594
--tshark /Applications/Wireshark.app/Contents/MacOS/tshark
--isaac-offset N
```

When JSONL contains no `socket` rows, the tool auto-loads `network.pcapng`,
runs `tshark`, drops TLS conversations, synthesizes socket events, and groups
plaintext RS streams by local port.

### Reading Deframe Output

Connection header example:

```json
{"record":"connection","source":"undercut-session","epoch":0,"fd":56394,"role":"world","peer":"8.26.16.145:443","port":443,"c2s_bytes":2799,"s2c_bytes":47350,"seeds_raw_c2s":"0xf37aa346,0x56d41356,0x7e614a1c,0xc7e50b9b","isaac_delta":50}
```

Packet row example:

```json
{"dir":"C2S","fd":56394,"conn":"world","seq":0,"opcode":240,"name":"UNKNOWN_240","size_kind":"fixed","size":7,"at_byte":668,"isaac_index":1,"payload_hex":"5e020d80083801"}
```

Fields:

- `dir`: `C2S` or `S2C`
- `conn`: inferred `lobby`, `world`, `js5`, or `unknown`
- `seq`: packet index in that direction for the connection
- `opcode`: decoded opcode
- `name`: codec/stub label
- `size_kind`: fixed, varByte, varShort
- `size`: payload size
- `at_byte`: stream offset
- `isaac_index`: opcode ISAAC index
- `payload_hex`: decoded payload

Quick filtering:

```bash
python3 - <<'PY'
import json
from pathlib import Path
p = Path("build/undercut-socket-session-production-isaac.jsonl")
for i, line in enumerate(p.open(), 1):
    o = json.loads(line)
    if o.get("conn") == "world" and o.get("dir") == "C2S":
        print(i, o.get("seq"), o.get("opcode"), o.get("name"), o.get("size"), o.get("payload_hex", "")[:80])
PY
```

### World Login Probe

Headless probe task:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:worldLoginProbe \
  -PworldHost=localhost -PworldPort=43597 -PprobeUser=probeplayer
```

It checks protocol milestones against a live world server. It is not a visual
client replacement, but it is useful for pre-ISAAC handshake regressions.

## Ghidra / Reclass Workflow

Do not make throwaway temp Ghidra projects for already-analyzed clients unless
there is a specific reason. Use the saved projects under:

```text
/Users/robert/projects/reclass-data
```

Useful command pattern:

```bash
ghidra-headless-class \
  /Users/robert/projects/reclass-data \
  rs2client-948 \
  rs2client.948-5 \
  /Users/robert/projects/reclass-data/ghidra-scripts/RS3ProgramInfo.java \
  process-noanalysis
```

Project notes:

- Avoid concurrent headless writers against the same Ghidra project.
- If MCP tools fail, run `ghidra-mcp-check`; HTTP health must be `ok`.
- For scripts through Ghidra MCP, use `run_java_class`, not JavaScript wording.
- Reclass scripts live under:

```text
/Users/robert/projects/reclass-data/ghidra-scripts
```

High-value docs:

- `/Users/robert/projects/reclass-data/docs/binary/boot/05-worldlist-switch.md`
- `/Users/robert/projects/darkan3-server/docs/protocol/lobby-world-switch-948.md`
- `/Users/robert/projects/darkan3-server/docs/tools/undercut-recorder-observation-slice.md`
- `/Users/robert/projects/darkan3-server/docs/tools/prod-948-login-flow-20260621.md`

Protocol table exports used:

- `/Users/robert/projects/reclass-data/clientprot_948-5_opcode_table.json`
- `/Users/robert/projects/reclass-data/build/prot-refresh/clientprot_948-5_opcode_table.json`

Both static exports still report 130 ClientProt entries. Live macOS additions
218/240 are not in those static Linux-derived tables.

## Golden Debug Workflow

Use this order for a fresh private-server test:

1. Build Darkan3:

```bash
cd /Users/robert/projects/darkan3-server
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :core:compileKotlin :lobby:compileKotlin :world:compileKotlin
```

2. Start lobby:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :lobby:run 2>&1 | tee build/lobby-current.log
```

3. Start world:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :world:run 2>&1 | tee build/world-current.log
```

4. Launch patched local client:

```bash
./run-client-mac.sh 2>&1 | tee build/client-current.log
```

5. If recording with Undercut, inject Undercut into the live child `rs2client`
   and verify MCP reports listening on `127.0.0.1:7882`.

6. Start a recording through MCP:

```text
start_session_recording {"label":"darkan-local-world-entry"}
```

7. Pilot the client:

```text
login -> lobby/world select -> Play Now -> observe spinner/world/exit
```

8. Stop recording:

```text
stop_session_recording
```

9. Deframe sockets:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:undercutSocketDeframe \
  -PUndercutSocketArgs="/Users/robert/.undercut/recordings/session-.../events.jsonl --out build/undercut-socket-local.jsonl --strict"
```

If Undercut did not capture seed events for a Darkan/private run, use server-log
ISAAC keys from `world-current.log`:

```text
World ISAAC keys: 0x..., 0x..., 0x..., 0x...
```

Then pass:

```text
--seeds 0x...,0x...,0x...,0x...
```

10. Import flow:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew --no-daemon :tools:undercutLoginFlowImport \
  -PUndercutFlowArgs="/Users/robert/.undercut/recordings/session-.../events.jsonl --out build/undercut-flow-local.jsonl --require-full-login"
```

11. Compare against golden production:

```text
build/undercut-socket-session-production-isaac.jsonl
build/undercut-flow-session-production-isaac.jsonl
```

Focus first on:

- lobby `906/81` click row
- S2C op212/op213 ordering
- world connection open time
- world login response bytes
- first 50 S2C world packets
- first 10 C2S world packets
- whether `PlayerInfo` arrives before production's baseline varp/varc/UI burst

## Reading Current Logs

Lobby evidence for spinner trigger:

```bash
grep -n "Play Now\\|SetWorldTarget\\|SwitchWorld\\|WorldlistFetch" /Users/robert/projects/darkan3-server/build/lobby-current.log
```

World evidence for login response and first crash:

```bash
grep -n "World login\\|World ISAAC keys\\|S2C\\|Missing ClientProt\\|Truncated ClientProt" /Users/robert/projects/darkan3-server/build/world-current.log
```

Crash reports:

```bash
ls -lt ~/Library/Logs/DiagnosticReports/*rs2client* ~/Library/Logs/DiagnosticReports/*RuneScape* 2>/dev/null | head -20
```

For `.ips`, inspect:

- `procName`
- `exception`
- `termination`
- `faultingThread`
- first frames of faulting thread

## Known Pitfalls

- Do not use `open` for patched client launches; `DYLD_INSERT_LIBRARIES` will be
  stripped.
- Do not assume production wrapper PID is the child rs2client PID.
- Do not open production RuneScape unless explicitly asked.
- Do not infer full world-entry parity from the old spinner/TCP commit.
- Do not register op236 from the current local failure without a complete frame.
- Do not rename op218/op240 to official names until sender evidence exists.
- Do not trust decoded packet hooks for pre-frame failures.
- Do not rerun Ghidra full analysis if a saved analyzed project exists and
  `process-noanalysis` is enough.
- Do not run parallel Ghidra headless writers against one project.
- Do not rely on `compileKotlin` alone for Undercut injection; run `shadowJar`.
- Do not treat `MacSocketCapture` as the primary recorder path. Use pcap plus
  seed hook.

## Next Technical Slice

The next productive slice is not more lobby trigger work. It should compare the
local world bootstrap stream against the production deframe:

1. Produce a fresh local Undercut recording against Darkan3.
2. Deframe with server-logged world ISAAC keys if Undercut did not emit a world
   seed.
3. Compare production and local world S2C order through the first production
   `PlayerInfo`.
4. Identify the missing baseline varp/varc/UI packets or invalid early packet
   that causes the client to exit before sending a complete first world C2S
   packet.
5. Only after the client sends a complete C2S frame should missing ClientProt
   registrations be added.

