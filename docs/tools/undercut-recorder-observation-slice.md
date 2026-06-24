# Undercut Recorder Observation Slice

Date: 2026-06-22.

## Why This Exists

The macOS client crash loop was being debugged from decoded packet events and crash reports. That misses the exact lobby-to-world handoff area: the production Undercut JSONL capture has `WORLD_LOGIN_HANDOFF` socket bytes, but no decoded packet events before the client enters `LOGGED_IN`.

That means spinner/no-spinner failures must be observed at raw socket level, not through `ServerPacketCapture`.

## Changes Made

Undercut now records decoded packet/tick state in JSONL and starts a `network.pcapng`
sidecar with `tshark` when session recording starts.

The earlier Kotlin libc `read`/`write` hook path was unstable during macOS injection,
so `MacSocketCapture` must stay unregistered unless it is moved to a native, no-JVM
hot path. The working capture path is:

- `events.jsonl`: session start/stop, main-state changes, ticks, decoded packets.
- `network.pcapng`: raw TCP payloads captured out-of-process.
- `isaac_seeds`: raw C2S ISAAC seed events captured by Undercut's macOS seed
  hook when `SendLoginPacket` loads `[R14+0x48]`.
- `isaac-keys.txt`: latest raw C2S seed set, for tools that expect capture-dir
  seed sidecars.
- `network_capture_start` / `network_capture_stop` JSONL markers with pcap path,
  interface, filter, pid, and exit code.

On Robert's Mac, `tshark` works without sudo through Wireshark's `access_bpf`
permissions. The default capture filter is:

```sh
tcp port 443 or tcp portrange 43594-43599
```

Darkan's `UndercutLoginFlowImport` surfaces decoded flow rows and stays compatible
with old recordings.

`UndercutSocketDeframe` now turns Undercut JSONL socket streams or the sibling
`network.pcapng` into the same annotated packet transcript as `recorderDeframe`.
When JSONL has no `socket` rows, it auto-loads `network.pcapng`, runs `tshark`,
drops TLS conversations, synthesizes socket events, and groups plaintext RS
streams by local port. It consumes ISAAC seeds in this order: explicit `--seeds`,
timestamped JSONL `isaac_seeds`, then sibling `isaac-keys.txt` fallback:

```sh
./gradlew :tools:undercutSocketDeframe \
  -PundercutSocketArgs="/Users/robert/.undercut/recordings/session-.../events.jsonl --out build/undercut-socket.jsonl --seeds s0,s1,s2,s3 --strict"
```

Useful pcap controls:

- `--pcap path/to/network.pcapng`
- `--no-pcap`
- `--pcap-hosts 8.42.17.231,8.26.16.145`
- `--pcap-ports 443,43594`
- `--tshark /Applications/Wireshark.app/Contents/MacOS/tshark`

It groups socket bytes by fd/epoch, infers lobby/world roles from ports or client-state transitions, then feeds the streams through `IsaacDeframer`. Production runs without seeds still emit raw per-connection dumps; production runs with the macOS seed hook and Darkan/private runs with server-logged seeds emit framed packets.

## macOS ISAAC Seed Hook

Undercut now ports the proven Darkan `recorder-mac` seed hook into
`libundercutbootstrap.dylib`.

Runtime behavior:

1. During `SessionRecorder.init`, Kotlin calls `Undercut_MacIsaacSeedHookInstall`.
2. Native code scans loaded `rs2client` `__TEXT` for the packed `[50,50,50,50]`
   ISAAC delta constant.
3. It finds `PADDD XMM0,[rip+disp32]` readers that target that constant.
4. It selects the reader immediately preceded by `MOVDQU XMM0,[R14+0x48]`.
5. It overwrites that 6-byte `MOVDQU` with `JMP rel32` plus a NOP, snapshots the
   4 raw client seeds from `[R14+0x48]`, replays the displaced instruction, and
   jumps back to the original stream.

The hook reports status through MCP fields:

- `isaac_seed_hook_attempted`
- `isaac_seed_hook_result`
- `isaac_seed_hook_status`
- `isaac_seed_count`

Expected successful status includes `installed hook=...`. A recording that
captures a login should then include one or more `isaac_seeds` rows. Lobby and
world can each produce a seed event; `undercutSocketDeframe` chooses the seed
nearest to each connection's observed time window unless `--seeds` overrides it.

## Validation

Undercut:

```sh
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew compileKotlin
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew buildNativeBootstrap
JAVA_HOME=$(/usr/libexec/java_home -v 25) ./gradlew shadowJar
```

Passed.

`libundercutbootstrap.dylib` must export:

```text
_Undercut_MacIsaacSeedHookInstall
_Undercut_MacIsaacSeedHookDrain
_Undercut_MacIsaacSeedHookStatus
```

Build caveat: `inject` loads the fat jar from `/Users/robert/projects/undercut-engine/build/libs/com.undercut-1.0.0-all.jar`. `compileKotlin` updates classes/thin jar but does not refresh the injected fat jar. Run `shadowJar` before any production recording meant to test recorder changes.

Darkan tools:

```sh
./gradlew :tools:compileKotlin
./gradlew :tools:undercutSocketDeframeSelfTest
./gradlew :tools:recorderSelfTest
./gradlew :tools:undercutLoginFlowImport \
  -PundercutFlowArgs="/Users/robert/.undercut/recordings/login-20260622-113439-production-login/events.jsonl --out build/undercut-flow-production.jsonl --require-full-login --require-world-traffic"
./gradlew :tools:undercutSocketDeframe \
  -PundercutSocketArgs="/Users/robert/.undercut/recordings/login-20260622-113439-production-login/events.jsonl --out build/undercut-socket-production.jsonl"
```

Passed on old production JSONL.

libSystem symbol lookup:

```text
connect=true
close=true
recv=true
recvfrom=true
read=true
send=true
sendto=true
write=true
```

## Ghidra Cache

Use saved 948 project without re-analysis:

```sh
ghidra-headless-class /Users/robert/projects/reclass-data rs2client-948 rs2client.948-5 \
  /Users/robert/projects/reclass-data/ghidra-scripts/RS3ProgramInfo.java process-noanalysis
```

Do not run concurrent headless commands against the same project unless using an explicit read-only project mode; Ghidra project lock rejects parallel writers.

`RS3ExportProtTables` was re-run from the saved project and matched existing tables byte-for-byte:

- server: 218 entries
- client: 130 entries

## Production ISAAC Session

Fresh production recording:

```text
/Users/robert/.undercut/recordings/session-20260622-152729-production-isaac
```

Strict socket deframe, using pcap fallback plus timestamped macOS ISAAC seed
events, produced:

- 2 plaintext RS connections: lobby + world.
- 5553 decoded packets.
- 0 desyncs.
- 0 truncations.
- output:
  `/Users/robert/projects/darkan3-server/build/undercut-socket-session-production-isaac.jsonl`.

Strict flow import produced:

- 12486 recorder events.
- phases: `SESSION`, `UNKNOWN`, `LOGIN`, `WORLD_SELECT`, `IN_GAME`.
- 5496 overlay packet events.
- 0 codec gaps.
- required world traffic present: `PLAYER_INFO`, `NPC_INFO`, zone updates,
  anti-cheat challenges, client keepalives.
- output:
  `/Users/robert/projects/darkan3-server/build/undercut-flow-session-production-isaac.jsonl`.

Live-only C2S deltas found by the clean deframe:

```text
LOBBY op218 fixed70 payload:
0000000512ffffffff9f00000037eb020d80083801bb020d8008380184020d80083801cf020d8008380123020d80083801711fc955ee27b8926da151008a037fffff7fd04a81

WORLD op240 fixed7 payload:
5e020d80083801
```

Ghidra evidence from saved Linux 948-5 project:

- `RS3ExportProtTables` still reports 130 ClientProt RegisterAll entries.
- op218 and op240 are absent from Linux 948-5 ClientProt RegisterAll.
- `RS3DataXrefs948` confirms nearby ordinary packet senders:
  - op52 `SendDisplayInfo @ 0x001a2d60`, fixed 6.
  - op54 `SendWorldlistFetch @ 0x00194c00`, fixed 4.
  - op94 `SendWindowStatus @ 0x0033e630`, fixed 3.
  - op105 `SendAppletFocusEvents @ 0x00195100`, varShort.
  - op8/op12/op81/op106 `SendMultiDisplayPackets @ 0x001a2f50`.
- op240's payload is selector byte `0x5e` followed by the exact op52
  display-info payload body. op218 embeds repeated display-info bodies plus
  lobby handoff values. That supports treating both as macOS live-only wrapper
  packets until the macOS sender is mapped.

Do not rename op218/op240 to official ClientProt names without direct macOS
sender evidence or official enum evidence.

## C2S Opcode Rule

Production macOS 948-5 sends ClientProt opcodes above 127 as one encrypted
opcode byte. The two-byte smart opcode form applies to inbound ServerProt on the
client and outbound ServerProt from Darkan, not to Darkan's inbound ClientProt
reader.

This was a Darkan server parity bug: `Session.readOpcode` used to consume a
second byte for client opcodes >=128. With production op240 at the first world
C2S packet, that would eat the first payload byte and desync the rest of the
session. `Session.readOpcode` now consumes one ISAAC byte for all ClientProt
opcodes, and `GameSessionTest.client opcode reader accepts single-byte high
opcodes` covers op240, op218, then Ping in one stream.

## Client Facts Confirmed

`jag::ConnectionManager::TcpIn @ 0x0013d9f0`:

- Reads opcode first.
- Non-ISAAC path uses `Packet::gSmart1or2`.
- ISAAC path peeks first byte, then consumes ISAAC for byte 1.
- If decoded byte 1 is >= 128, it reads byte 2 and consumes ISAAC again.
- Resolves fixed/varByte/varShort size before payload read.
- Calls `game::TcpConnectionMessage::InitIncoming` only after a complete framed packet exists.

Therefore `ServerPacketCapture` cannot explain failures that happen before a packet fully frames.

Relevant 948-5 handlers from binary-derived table:

- op 81 `REBUILD_NORMAL_SIMPLE`, size varShort, handler `jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE @ 0x001daa70`
- op 22 `PLAYER_INFO`, size varShort, handler `jag::PlayerList::ProcessPlayerInfo @ 0x001618a0`
- op 77 `CAM_UPDATE`, size varShort, handler `jag::packethandlers::Camera::CAM_UPDATE @ 0x001d3d10`
- op 3 `IF_SETTOPLEVELINTERFACE`, size 19, handler `jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE @ 0x00186a80`

## Production Sequence Anchor

Production JSONL `/Users/robert/.undercut/recordings/login-20260622-113439-production-login/events.jsonl`:

- `WORLD_LOGIN_HANDOFF`: raw socket only, no decoded packets.
- First decoded in-game packet after `LOGGED_IN`: op 81 `REBUILD_NORMAL_SIMPLE`, payload 5137.
- First `PLAYER_INFO`: op 22, payload 289.
- Immediate scaffolding around first `PLAYER_INFO`: op 55 `DESTROY_ZONE_DATA`, op 1 `SET_NPC_OP`, op 22 `PLAYER_INFO`, op 77 `CAM_UPDATE`, op 130 `UPDATE_IGNORELIST`.
- HUD top-level interface op 3 appears later, not before first `PLAYER_INFO`.

## Next Capture

Run a fresh Undercut recording against Darkan/private client and inspect:

1. `socket_connect` rows to identify lobby and world fds.
2. Run `undercutSocketDeframe` with server-logged seeds.
3. Check whether `WORLD_LOGIN_HANDOFF` inbound raw bytes include world pre-ISAAC response before ISAAC-framed op 81.
4. Check whether `LOGGED_IN` transition occurs before any decoded packet event.
5. Compare `undercutSocketDeframe` packet order with decoded `packet` events from Undercut's in-client hooks.

This should decide if failure is in:

- world pre-ISAAC login response,
- ISAAC engagement boundary,
- first framed packet sizing,
- or later world-init packet semantics.
