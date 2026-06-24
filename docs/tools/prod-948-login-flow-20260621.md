# Production 948-5 Login Flow Capture

Source capture:

`/Users/robert/.undercut/recordings/login-20260621-203432-prod-login-mac-rawsend-50761/events.jsonl`

Generated redacted oracle:

```bash
./gradlew :tools:undercutLoginFlowImport \
  -PundercutFlowArgs="/Users/robert/.undercut/recordings/login-20260621-203432-prod-login-mac-rawsend-50761/events.jsonl --out build/undercut-flow-prod-948-5.jsonl --require-full-login --require-world-traffic --require-no-unresolved-labels"
```

The import intentionally does not copy login payload bytes. It keeps phase boundaries, decoded packet labels, opcode coverage, payload sizes, raw socket leading-byte histograms, and `observed_label` fields that prefer strict codec names when known and capture-derived behavior names when the codec name must remain `UNKNOWN_*`.

## Phase Summary

| Phase | Cycles | Duration | Events | S packets | C packets | Raw C socket |
|---|---:|---:|---:|---:|---:|---:|
| LOGIN | 5280-17057 | 235.494s | 11781 | 0 | 0 | 2 sends, 648 B |
| WORLD_SELECT | 17058-19002 | 38.881s | 3660 | 1673 | 0 | 41 sends, 130 B |
| WORLD_LOGIN_HANDOFF | 19003-19208 | 3.989s | 212 | 0 | 0 | 6 sends, 672 B |
| IN_GAME | 19209-21627 | 48.473s | 4250 | 3878 | 47 | 82 sends, 2280 B |

Main-state transitions:

| State | Cycle |
|---|---:|
| LOGIN_SCREEN | 5280 |
| LOBBY_SCREEN | 17058 |
| LOGGED_IN | 19217 |

## Codec Coverage

Importer result:

| Metric | Count |
|---|---:|
| Packet events | 5598 |
| Socket events | 131 |
| Unregistered codec packet kinds | 0 |
| Capture `UNKNOWN_*` packet kinds | 8 |
| Capture `UNKNOWN_*` packet events | 103 |
| Codec `UNKNOWN_*` packet kinds | 0 |
| Codec `UNKNOWN_*` packet events | 0 |
| Codec-resolved capture `UNKNOWN_*` packet kinds | 8 |
| Codec-resolved capture `UNKNOWN_*` packet events | 103 |
| Unresolved packet kinds | 0 |
| Unresolved packet events | 0 |

Meaning: Darkan 948 has size metadata and runtime codec labels for every decoded server/client packet seen in this capture. Remaining work is field-level payload decoding for raw carriers, not opcode framing or packet identity.

## Important Label Fixes

Historical capture labels still unknown in this saved `events.jsonl`, but Darkan codec resolves them and current Undercut 948-5 labels these opcodes directly:

| Phase | Opcode | Capture label | Darkan label | Count | Size |
|---|---:|---|---|---:|---|
| IN_GAME | 52 | UNKNOWN_52 | NpcInfo | 81 | varShort |
| IN_GAME | 174 | UNKNOWN_174 | AntiCheatChallenge | 8 | fixed:8 |
| IN_GAME | 17 | UNKNOWN_17 | SetPlayerOp | 6 | varByte |
| WORLD_SELECT | 216 | UNKNOWN_216 | WorldListPacket | 4 | varShort |
| IN_GAME | 216 | UNKNOWN_216 | WorldListPacket | 1 | varShort |
| IN_GAME | 73 | UNKNOWN_73 | MinimapState | 1 | fixed:2 |
| IN_GAME | 154 | UNKNOWN_154 | EntityAnimAtTile | 1 | fixed:5 |
| IN_GAME | 157 | UNKNOWN_157 | SceneFlag | 1 | fixed:1 |

## World Traffic Gate

The generated oracle now includes a `world_traffic` record. The production capture passes
`--require-world-traffic` with:

| Evidence | Count |
|---|---:|
| IN_GAME `PLAYER_INFO` op 22 | 82 |
| IN_GAME `NPC_INFO` op 52 | 81 |
| IN_GAME `UPDATE_ZONE_PARTIAL_ENCLOSED` op 76 | 89 |
| IN_GAME `ANTI_CHEAT_CHALLENGE` op 174 | 8 |
| IN_GAME C2S `KEEPALIVE_NUDGE` op 51 | 47 |
| IN_GAME raw C socket events | 82 |
| IN_GAME raw C socket bytes | 2280 |

Capture labels known and now matched by Darkan codec labels:

| Opcode | Capture label | Darkan label | Count | Size |
|---:|---|---|---:|---|
| 104 | PLAYER_INFO_DECODE | PlayerInfoDecode | 8 | fixed:14 |
| 119 | CUTSCENE_DATA | CutsceneData | 8 | fixed:35 |
| 128 | NOOP_VAR_A | NoopVarA | 8 | fixed:0 |
| 172 | SET_PLAYER_FLAG_A | MinimapFlagA | 2 | fixed:1 |
| 190 | CLEAR_PENDING_UPDATES | ClearPendingUpdates | 2 | fixed:0 |
| 204 | SET_PLAYER_FLAG_B | MinimapFlagB | 2 | fixed:1 |
| 1 | SET_NPC_OP | SetNpcOp | 1 | varByte |
| 7 | RESET_ENTITY_LISTS | ResetEntityLists | 1 | fixed:0 |
| 12 | SET_PLAYER_OP_2 | SetPlayerOp2 | 1 | fixed:2 |
| 13 | SET_PLAYER_OP_3 | SetPlayerOp3 | 1 | fixed:1 |
| 45 | SET_MULTIWAY_STATE | SetMultiwayState | 1 | fixed:1 |
| 55 | DESTROY_ZONE_DATA | DestroyZoneData | 1 | fixed:0 |
| 130 | UPDATE_IGNORELIST | UpdateIgnoreListRaw | 1 | varByte |
| 209 | NPC_INFO_THUNK | NpcInfoThunk | 1 | varShort |

Unknown on both sides: none in this capture (`unresolved_packet_kinds=0`, `observed_label_source=unknown` has zero rows).

## Flow Notes

- Lobby socket fd was `89`; world handoff starts at cycle `19003` when fd `91` appears.
- LOGIN raw C leading bytes are `0x0E` and `0x13`.
- WORLD_LOGIN_HANDOFF fd `91` leading bytes are `0x0E`, `0x10`, `0x1A`.
- The decoded C2S packet stream currently contains only in-game `KEEPALIVE_NUDGE` op `51`, count `47`.
- Full C2S login/world decode still needs seed capture or pre-ISAAC send hook parity. The current Undercut macOS raw send hook proves socket chronology but cannot deframe encrypted C2S streams by itself.
- `MAP_BUILD_COMPLETE` is client op `107`, fixed size `0`. Darkan now decodes it as `MapBuildComplete` and consumes it with a world no-op handler; world init remains ungated on this packet per `docs/protocol/lobby-world-switch-948.md` §7.3.
- `ANTI_CHEAT_CHALLENGE` behavior is server op `174`, fixed size `8`. 948-5 Ghidra names the handler `HandleAntiCheatChallenge @ 0x00180920`; Darkan and current Undercut both label it. Live tail sample: server body `7C FD AE 5F BE B5 E7 31`; client response body after encrypted opcode is `7C FD AE 5F 31 E7 B5 BE 98`, i.e. first challenge BE, second challenge LE, sequence byte biased by `-128`. 948-5 decompile proves the raw sequence source is `*(uint *)(*client + 0x534)`, clamped to `0xff` before biasing; it is client-side state, not derived from the challenge body. Darkan now sends op `174` about every 6s while in world mode when no challenge is pending, verifies the echoed challenge values in client op `3`, disconnects if a pending challenge remains unanswered for 20s, and preserves the sequence byte as decoded telemetry.
- `WORLDLIST_FETCH_REPLY` is server op `216`, varShort. 948-5 handler `WorldData::WORLDLIST_FETCH_REPLY @ 0x00190020` reassembles fragments: packet byte `0` means continuation, `1` means final segment, and the accumulated buffer begins with `[2, mode]`. Production full list starts `00 02 01 ...`, continues with `00 ...`, then final `01 ...`; count-only updates start `01 02 00 <revision:u32> ...`. Darkan's 948 encoder now keeps that count-only revision word and writes full world records as `activityGate, [activity], hostname, hostname` so the proven host slot is not filled with display activity text.
- Logged-in world-state traffic includes steady `PLAYER_INFO` op `22`, `NPC_INFO` op `52`, and `UPDATE_ZONE_PARTIAL_ENCLOSED` op `76` packets. `UpdateMaskHeaderTest` now guards the revision-specific player and NPC extended-info header bytes so 948 keeps player expansion bits `{0,13,22}` and NPC expansion bits `{6,8,19,25}` instead of drifting back to 947 literals.
- `:tools:undercutLoginFlowImport --require-world-traffic` now fails unless the capture proves in-game `PLAYER_INFO`, `NPC_INFO`, zone updates, anti-cheat challenges, C2S keepalives, and raw client socket traffic. This keeps the production oracle from treating a login-state transition without real world traffic as full parity.
- Remaining capture-unknown labels are now behavior-labelled by the Darkan codec: op `73` (`MinimapState`, bytes decoded as `128 - wire`), op `154` (`EntityAnimAtTile`, two little-low-byte-add128 fields plus a value byte), and op `157` (`SceneFlag`, decoded as `(-wire) & 0xff`).
- `SetNpcOp` now exposes the proven 948-5 varByte body: empty payload keeps the default `Walk here` op and cursor `-1`; non-empty payload is RS string text plus a 16-bit cursor id where `0xffff` maps to `-1`.
- `PlayerInfoDecode` now exposes the proven 948-5 header fields: first byte packs `slot = byte >> 5` and `mode = byte & 0x1f`. This capture uses mode `0` for slots `0..7`, followed by 13 zero filler bytes.
- `CutsceneData` now exposes the fixed 35-byte branch seen in this capture: group, slot, mode, extended mode, shape/flag byte, id, two 64-bit values, three 32-bit values, and trailing skip length. The capture sends eight entries for slots `0..7` with `mode=7`, `extendedMode=2`, and zero numeric fields.
- `UpdateIgnoreListRaw` now preserves the proven relationship-delta envelope: 8-byte mask, optional encoded field bytes, and trailing 16-bit entry id. The production sample is the zero-mask envelope (`00 00 00 00 00 00 00 00 00 00`).
- `NpcInfoThunk` op `209` now models the zero-length path captured in production as `ResetWorldEntityNpcs`. The 948-5 Mach-O handler `jag::packethandlers::NPCInfo::NPC_INFO_thunk_worldentity @ 0x001d54f0` clears the client's world-entity NPC state when packet size is `0` and reads no payload bytes. Non-empty op209 payloads are a separate world-entity NPC envelope with a leading mode byte, flags byte, scalar fields, counted vectors, and config-typed values; that branch remains preserved as `RawWorldEntityPayload` until a non-empty production sample or full field split proves the encoder.
- The macOS binary recorder seed hook is now implemented and deployed. The trampoline return path preserves GPRs with an absolute indirect jump, and the current live 948 client `/Users/robert/Jagex/launcher/rs2client` contains the expected seed-hook anchor: one `[50,50,50,50]` constant, two PADDD readers, and the login-builder candidate at file offset `0xcb88e` immediately preceded by `f3 41 0f 6f 46 48` (`MOVDQU XMM0,[R14+0x48]`). A fresh `DARKAN_RECORD=1` run should now produce `Seeds` records if the inline hook survives Rosetta at runtime.
- `:tools:recorderSelfTest` now proves the offline seed-driven deframer in both directions. Synthetic S2C covers fixed, varByte, varShort, and 2-byte opcodes; synthetic C2S covers `RequestWorldList`, `MapBuildComplete`, `FriendListDel`, and `MessagePrivateSend`. The remaining production gate is live seed capture, not the offline C2S/S2C deframe path.

## Next Work

1. Run a fresh `run-client-mac-record.sh` capture against the current 948 client with the fixed seed hook and Undercut 948 labels enabled, then verify the `.bin` contains a `Seeds` record and capture-side `UNKNOWN_*` count stays zero for the known opcodes above.
2. Feed seed-bearing captures through `:tools:recorderDeframe -PdeframeArgs="<capture.bin> --out build/recorder-transcript.jsonl --strict"` for full C2S and S2C byte framing. `--strict` requires seeds and exits non-zero on any opcode desync or truncated packet frame.
3. Decode non-empty `NpcInfoThunk.RawWorldEntityPayload` once a production payload or complete handler field split proves the world-entity NPC envelope.
4. Re-inject/re-record once an `rs2client` process is visible; current Codex shell cannot see a live client process or Undercut MCP listener after the 20:34 capture stopped.
5. Keep stub-table official names conservative; runtime packet labels can come from typed encoder classes when the beta enum source has no official symbol.
