# Prod transition capture 2026-06-24 19:11:39

This is the current evidence library for the latest production login -> lobby -> Play Now -> world transition.
It is intentionally capture-first. Older protocol docs are useful for history, but this file should be read as the
latest observation baseline until disproven by a newer production run.

## Source artifacts

| Artifact | Path | Use |
|---|---|---|
| Recorder events | `/Users/robert/.undercut/recordings/session-20260624-191139-prod-transition-20260624-191126/events.jsonl` | Client-side packet hook, main-state timeline, ISAAC seed hook status |
| Raw pcap | `/Users/robert/.undercut/recordings/session-20260624-191139-prod-transition-20260624-191126/network.pcapng` | TCP conversation proof and byte timing |
| Decoded socket session | `/Users/robert/projects/darkan3-server/build/undercut-socket-session-prod-transition-20260624-191139-reassembled.jsonl` | De-ISAAC packet stream with payloads |
| Strict decoded socket session | `/Users/robert/projects/darkan3-server/build/undercut-socket-session-prod-transition-20260624-191139-strict-check.jsonl` | Current aligned verifier output; no packet desync or truncation |
| ISAAC seed note | `/Users/robert/.undercut/recordings/session-20260624-191139-prod-transition-20260624-191126/isaac-keys.txt` | Raw world C2S seeds for second login |
| Ghidra target | open 948 client in CodeBrowser | Handler/emitter confirmation |
| Server-client-var page | `/Users/robert/projects/darkan3-server/docs/protocol/world-login-server-client-var-20260624.md` | Full 220-entry part-A table and parser adversary review |

Trust model:

- `events.jsonl` is most complete for full-session S2C opcode coverage because it is emitted by the injected client packet hook after client decode.
- `undercut-socket-session-*.jsonl` is best for payload examples, login handshakes, and C2S framing. Current strict output has `5859` rows: `5622` framed packet rows, `15` login-event rows, and `220` server-client-var entries. It has `0` errors, `0` desyncs, and `0` truncations.
- Counting JSON rows with an `opcode` gives `5624`, because the two C2S login-event rows carry login opcodes `19` and `16`.
- `network.pcapng` proves TCP conversations and payload lengths, but does not label application packets without decoder.
- Ghidra confirms handler/emitter behavior. It does not prove a packet was present in this production run unless paired with capture evidence.

## TCP conversations

Relevant game-protocol conversations from pcap:

| Role | TCP conversation | Relative start | Duration | Payload notes |
|---|---|---:|---:|---|
| Lobby | `192.168.68.57:50121 <-> 8.42.17.232:443` | `63.666329s` | `11.0567s` | 1-byte connection type, 9-byte first response, 647-byte login packet, 18.3 KB S2C |
| World | `192.168.68.57:50129 <-> 8.26.16.159:443` | `71.670667s` | `45.4751s` | 1-byte connection type, 9-byte first response, 667-byte login packet, 52.5 KB S2C |

Other large 443 conversations in the pcap are launcher/content/noise and are not used for game protocol conclusions.

## Main-state timeline

Recorder main-state transitions:

| State | Relative ms from recorder start | Client cycle |
|---|---:|---:|
| `LOGIN_SCREEN` | `2961` | `1791` |
| `LOBBY_SCREEN` | `70759` | `5228` |
| `LOGGED_IN` | `78168` | `5595` |

ISAAC seed observations:

| Sequence | Client state when captured | Relative ms | Raw C2S seeds |
|---:|---|---:|---|
| 1 | `LOGIN_SCREEN` | `66324` | `0xb2292e8c,0x491178fd,0x63f825dc,0x7652ae5b` |
| 2 | `LOBBY_SCREEN` | `74471` | `0x8b0403bd,0xb8edfa49,0xe4831c2d,0x822089da` |

The production lobby-to-world UI handoff completes in about `7409ms` between `LOBBY_SCREEN` and `LOGGED_IN`.

## Login handshakes

Decoded socket login events:

| Role | Direction | Event | Value |
|---|---|---|---|
| Lobby | C2S | connection type | `14` |
| Lobby | C2S | login packet | opcode `19`, block size `644` |
| Lobby | S2C | first response | code `0`, session key `0x005e279d620f3460` |
| Lobby | S2C | login result | `2` |
| Lobby | S2C | login data length | `110` |
| World | C2S | connection type | `14` |
| World | C2S | login packet | opcode `16`, block size `664` |
| World | S2C | first response | code `0`, session key `0x0052f6ec9ad2d70a` |
| World | S2C | login result | `2` |
| World | S2C | server-client-var block length | `1321` |
| World | S2C | server-client-var ack | `1` |
| World | S2C | server-client-var entries | `220` int entries, no decode errors |
| World | S2C | players byte | `2` |
| World | S2C | world login data length | `37` |

World raw pre-ISAAC response begins as a 1324-byte TCP payload from server after client sends the 667-byte game-login packet. That aligns with:

- 1 byte login result (`2`)
- 2-byte server-client-var length (`1321`)
- 1321-byte server-client-var body, whose first byte is ack `1`

The 1321-byte body is now decoded as `220` typed int entries. See
`docs/protocol/world-login-server-client-var-20260624.md`.

## Ordered production flow

### Lobby init tail

The final aligned lobby S2C sequence in decoded socket stream:

| Seq | Opcode | Name | Size | Meaning |
|---:|---:|---|---:|---|
| 1610 | 3 | `IfSetTopLevelInterface` | 19 | Opens lobby root `906` (`0x038a`) |
| 1611..1633 | 82 | `IfSetPosition` | 23 each | Builds lobby subinterfaces |
| 1630..1660 | 110 | `RunClientScript` | varShort | Timer/news/tab scripts, including lobby news list |
| 1661 | 80 | `UpdateRunenergy` | 1 | UI value |
| 1662 | 75 | `SetReadyFlag` | 0 | Client render gate |
| 1663 | 49 | `ChangeLobby` | 0 | Lobby refresh/list handler |
| 1664 | 128 | `NoopVarA` | 0 | Unknown no-op/marker |
| 1665 | 26 | `FriendStatus` / hook `UPDATE_SITESETTINGS_THUNK` | 0 | Empty social/settings packet |
| 1666..1669 | 216 | `WorldListPacket` | varShort | Four production world-list chunks |
| 1670 | 128 | `NoopVarA` | 0 | Tail marker |

Ghidra 948 confirmation:

- `CHANGE_LOBBY @ 00196170` parses lobby list/display data and does not enter world.
- `SWITCH_WORLD @ 001aeba0` is explicit world-hop path and sets main state `0x25`; cold lobby Play Now uses login-response target instead.
- `SET_READY_FLAG @ 00175120` is a render/client-ready gate.

### World first-light burst

World S2C first aligned packets:

| Seq | Opcode | Name | Size | Meaning |
|---:|---:|---|---:|---|
| 0 | 81 | `RebuildNormalSimple` | 5137 | Build area + GPI prefix |
| 1 | 54 | `HashedWorldToken` | 44 | World session token string |
| 2 | 73 | `MinimapState` | 2 | Minimap/display state |
| 3 | 74 | `JcoinsUpdate` | 4 | RuneCoins/JCoins value |
| 4 | 172 | `MinimapFlagA` | 1 | Minimap/player flag |
| 5 | 204 | `MinimapFlagB` | 1 | Minimap/player flag |
| 6..12 | 17 | `SetPlayerOp` | varByte | Follow, Trade with, Req Assist, null, Examine, Duel |
| 11 | 95 | `MidiSong` | 5 | Initial music |
| 13 | 5 | `ResetClientVarcache` | 0 | Var cache reset |
| 14..1617 | 28/61/147 | varp/varbit baseline | mixed | Large first-light variable baseline |

Scene bootstrap:

| Seq | Opcode | Name | Size | Meaning |
|---:|---:|---|---:|---|
| 1618 | 55 | `DestroyZoneData` | 0 | Zone reset |
| 1619 | 1 | `SetNpcOp` | 0 | Empty NPC op set |
| 1620 | 22 | `PlayerInfo` | 965 | First player info; local player and many slots |
| 1621 | 77 | `CamUpdate` | 121 | Production camera block |
| 1622 | 130 | `UpdateIgnoreListRaw` | 10 | Empty ignore list raw |
| 1623..2264 | 78 | `UpdateZoneFullFollowsV2` | 3 each | 606 zone-follow packets |

HUD commit:

| Seq | Opcode | Name | Size | Meaning |
|---:|---:|---|---:|---|
| 2265..2271 | 85 | `UpdateInvFull` | varShort | Initial inventories |
| 2272..2274 | 110 | `RunClientScript` | varShort | Pre-HUD scripts |
| 2275 | 3 | `IfSetTopLevelInterface` | 19 | Opens in-game HUD root `1477` |
| 2276..3311 | 82/35/110/etc | interface build | mixed | Full HUD subinterfaces/events/scripts |

Ready and post-ready tail:

| Seq | Opcode | Name | Size | Meaning |
|---:|---:|---|---:|---|
| 3312 | 209 | `NpcInfoThunk` | 0 | NPC info prelude |
| 3313 | 162 | `TriggerOnDialogAbort` | 0 | Dialog abort/reset |
| 3314 | 190 | `ClearPendingUpdates` | 0 | Client update clear |
| 3327 | 22 | `PlayerInfo` | 147 | Follow-up player info |
| 3328 | 52 | `NpcInfo` | 778 | Large NPC info |
| 3373..3401 | 44 | `UpdateStat` | 6 each | 29 stats |
| 3402 | 12 | `SetPlayerOp2` | 2 | Player-op related tail |
| 3403 | 13 | `SetPlayerOp3` | 1 | Player-op related tail |
| 3404 | 7 | `ResetEntityLists` | 0 | Entity reset |
| 3405 | 45 | `SetMultiwayState` | 1 | Multiway state |
| 3406 | 67 | `ClanChannelFull` | 0 | Empty clan channel |
| 3407..3414 | 119 | `CutsceneData` | 35 each | Eight zero-ish cutscene slots |
| 3415..3422 | 104 | `PlayerInfoDecode` | 14 each | Eight player-info decode descriptors |
| 3423 | 199 | `RebuildRegion` | 93 | Region/rebuild tail |
| 3424..3425 | 204/172 | minimap flags | 1 each | Repeat flags |
| 3428 | 80 | `UpdateRunenergy` | 1 | Energy |
| 3429 | 75 | `SetReadyFlag` | 0 | Final first-light render gate |
| 3430.. | 22/52/etc | continuing world updates | mixed | Player/NPC/interface/social/worldlist follow-ups |

Ghidra 948 confirmation:

- `ProcessPlayerInfo @ 001618a0` handles server `op22`.
- `ProcessNpcInfo @ 001d7b60` handles server `op52`.
- `REBUILD_NORMAL_SIMPLE @ 001daa70` handles server `op81`. It consumes the op81 GPI prefix when the client world state demands it, then reads the coordinate header/build-area packed coords and calls camera reset if render state is not already `6`.
- `CAM_UPDATE @ 001d3d10` handles server `op77`. It is a bitflag-driven camera modifier on the already reset camera, not the packet that enters render state.
- `UPDATE_ZONE_FULL_FOLLOWS @ 000f7bb0` handles server `op78`. It reads the 3-byte zone-follow payload and clears/populates the matching 8x8 tile window.
- `UPDATE_ZONE_PARTIAL_ENCLOSED @ 000eefc0` handles server `op76`. It reads a 3-byte zone header and then loops zone sub-opcodes through the zone sub-protocol table.
- `REBUILD_NORMAL @ 001203e0` handles server `op199`. It is the later region/grid rebuild form, not the first world-entry rebuild.

## Packet library from latest production run

S2C rows use client-hook packet events for full-session coverage. C2S rows use strict socket transcript rows because the hook only emits long-running keepalive C2S after the world transition. Names are recorder/client current labels, not always final official enum names.

| Phase | Dir | Op | Name | Category | Count | First ms | Last ms | Size summary | Example |
|---|---:|---:|---|---|---:|---:|---:|---|---|
| Lobby | S | 3 | `IF_SETTOPLEVELINTERFACE` | INTERFACES | 1 | 71129 | 71129 | 19 | `... 0A 03 ...` = root 906 |
| Lobby | S | 5 | `RESET_ALL_VARPS` | CLIENT_STATE | 1 | 70849 | 70849 | 0 | empty |
| Lobby | S | 26 | `UPDATE_SITESETTINGS_THUNK` | SITE_SETTINGS | 1 | 71266 | 71266 | 0 | empty; decoded socket names `FriendStatus` |
| Lobby | S | 28 | `SET_VARP_INT` | VARIABLES | 438 | 70850 | 71126 | 6 | `94 40 00 84 00 03` |
| Lobby | S | 35 | `IF_SETEVENTS2` | INTERFACES | 4 | 71164 | 71164 | 12 | `00 02 01 00 ... 03 8B 00 27` |
| Lobby | S | 44 | `UPDATE_STAT` | MISC | 29 | 70776 | 70847 | 6 | `00 00 00 00 01 00` |
| Lobby | S | 47 | `SET_VARP_SMALL` | VARIABLES | 12 | 71126 | 71165 | 3 | `80 A8 0D` |
| Lobby | S | 49 | `CHANGE_LOBBY` | MISC | 1 | 71193 | 71193 | 0 | empty |
| Lobby | S | 61 | `SET_VARBIT_NEG` | VARIABLES | 1133 | 70851 | 71125 | 3 | `00 1B 81` |
| Lobby | S | 64 | `SET_VARP_INT_VARIANT` | VARIABLES | 2 | 71126 | 71126 | 6 | `8C 98 03 25 53 0A` |
| Lobby | S | 75 | `SET_READY_FLAG` | CLIENT_STATE | 1 | 71191 | 71191 | 0 | empty |
| Lobby | S | 80 | `SET_RUN_ENERGY` | MISC | 1 | 71189 | 71189 | 1 | `01` |
| Lobby | S | 82 | `IF_SETPOSITION` | INTERFACES | 21 | 71143 | 71164 | 23 | `7F 00 00 00 00 8A 03 ...` |
| Lobby | S | 110 | `RUNCLIENTSCRIPT` | MISC | 15 | 71163 | 71187 | varShort | timer/news/tab scripts |
| Lobby | S | 128 | `NOOP_VAR_A` | MISC | 2 | 71193 | 76153 | 0 | empty |
| Lobby | S | 147 | `SET_VARP_LONG_ALT` | VARIABLES | 5 | 71116 | 71119 | 10 | `2E 90 FF FF FF FF FF FF FF FF` |
| Lobby | S | 216 | `WORLDLIST_PACKET` | WORLD_DATA | 4 | 71300 | 73872 | varShort | four chunks |
| Lobby | C | 5 | `SceneGraphReport` | C2S | 2 | socket seq 0,2 | | 4 | `00000005`, `00000037` |
| Lobby | C | 51 | `NO_TIMEOUT` / `Ping` | C2S | 6 | socket seq 12,13,16..19 | | 0 | keepalive before and after Play Now click |
| Lobby | C | 52 | `UNKNOWN_52` -> `SendDisplayInfo` | C2S | 9 | socket seq 3..11 | | 6 | `020bb0071401` |
| Lobby | C | 54 | `RequestWorldList` | C2S | 2 | socket seq 1,14 | | 4 | `ffffffff`, then `27b8926d` |
| Lobby | C | 127 | `IF_BUTTON1` / `IfButton` | C2S | 1 | socket seq 15 | | 8 | `51008a037fffff7f` = interface hash `906:81`, slot `65535`, item `-1`/sentinel |
| World | S | 1 | `SET_NPC_OP` | NPC_INFO | 1 | 78457 | 78457 | 0 | empty |
| World | S | 3 | `IF_SETTOPLEVELINTERFACE` | INTERFACES | 1 | 78617 | 78617 | 19 | root 1477 |
| World | S | 5 | `RESET_ALL_VARPS` | CLIENT_STATE | 1 | 78192 | 78192 | 0 | empty |
| World | S | 7 | `RESET_ENTITY_LISTS` | MISC | 1 | 78969 | 78969 | 0 | empty |
| World | S | 10 | `VARP_BIT_SMALL` | VARIABLES | 14 | 138046 | 198072 | 3 | later session varbits |
| World | S | 12 | `SET_PLAYER_OP_2` | MISC | 1 | 78969 | 78969 | 2 | `00 00` |
| World | S | 13 | `SET_PLAYER_OP_3` | MISC | 1 | 78969 | 78969 | 1 | `64` |
| World | S | 16 | `LOC_DEL` | ZONE_UPDATES | 14 | 78489 | 78546 | 2 | `56 AA` |
| World | S | 17 | `SET_PLAYER_OP` | MISC | 6 | 78190 | 78192 | varByte | Follow/Trade/Assist/null/Examine/Duel |
| World | S | 22 | `PLAYER_INFO` | PLAYER_INFO | 231 | 78469 | 216070 | varShort | first socket-aligned size 965 |
| World | S | 26 | `UPDATE_SITESETTINGS_THUNK` | SITE_SETTINGS | 1 | 79826 | 79826 | 0 | empty |
| World | S | 28 | `SET_VARP_INT` | VARIABLES | 495 | 78192 | 201071 | 6 | `94 40 00 84 00 03` |
| World | S | 30 | `IF_SET2DANGLE` | INTERFACES | 4 | 78905 | 198072 | 8 | `80 07 07 00 00 00 AA 0A` |
| World | S | 35 | `IF_SETEVENTS2` | INTERFACES | 871 | 78689 | 84062 | 12 | HUD events |
| World | S | 44 | `UPDATE_STAT` | MISC | 29 | 78958 | 78969 | 6 | stat baseline |
| World | S | 45 | `SET_MULTIWAY_STATE` | MISC | 1 | 78969 | 78969 | 1 | `00` |
| World | S | 46 | `OBJ_ADD` | ZONE_UPDATES | 16 | 78477 | 78551 | 5 | `54 00 01 03 32` |
| World | S | 47 | `SET_VARP_SMALL` | VARIABLES | 61 | 78694 | 81070 | 3 | varc/varp small |
| World | S | 49 | `CHANGE_LOBBY` | MISC | 1 | 79798 | 79798 | 0 | worldlist/lobby refresh packet also appears after login |
| World | S | 52 | `NPC_INFO` | NPC_INFO | 230 | 78950 | 216070 | varShort | first socket-aligned size 778 |
| World | S | 54 | `HASHED_WORLD_TOKEN` | MISC | 1 | 78186 | 78186 | 44 | token string |
| World | S | 55 | `DESTROY_ZONE_DATA` | CLIENT_STATE | 1 | 78457 | 78457 | 0 | empty |
| World | S | 61 | `SET_VARBIT_NEG` | VARIABLES | 1178 | 78192 | 198072 | 3 | varbit baseline |
| World | S | 62 | `IF_CLOSESUB_ACTIVE` | INTERFACES | 2 | 78871 | 81069 | 4 | close subinterface |
| World | S | 64 | `SET_VARP_INT_VARIANT` | VARIABLES | 2 | 78908 | 78908 | 6 | var int variant |
| World | S | 67 | `CLANCHANNEL_FULL` | CLANS | 1 | 78970 | 78970 | 0 | empty clan channel |
| World | S | 73 | `MINIMAP_STATE` | MISC | 1 | 78187 | 78187 | 2 | `80 80` |
| World | S | 74 | `SET_DISPLAY_INT` | MISC | 1 | 78187 | 78187 | 4 | `AD A9 0A 4A` |
| World | S | 75 | `SET_READY_FLAG` | CLIENT_STATE | 1 | 79796 | 79796 | 0 | final first-light ready gate |
| World | S | 76 | `UPDATE_ZONE_PARTIAL_ENCLOSED` | ZONE_UPDATES | 219 | 81660 | 216070 | varShort | partial zone updates after first-light |
| World | S | 77 | `CAM_UPDATE` | CAMERA | 1 | 78472 | 78472 | 121 | initial camera payload |
| World | S | 78 | `UPDATE_ZONE_FULL_FOLLOWS` | ZONE_UPDATES | 606 | 78474 | 78597 | 3 | full zone sweep |
| World | S | 80 | `SET_RUN_ENERGY` | MISC | 1 | 79796 | 79796 | 1 | `01` |
| World | S | 81 | `REBUILD_NORMAL_SIMPLE` | CLIENT_STATE | 1 | 78179 | 78179 | 5137 | first world packet |
| World | S | 82 | `IF_SETPOSITION` | INTERFACES | 56 | 78684 | 80466 | 23 | HUD positions |
| World | S | 85 | `UPDATE_INV_FULL` | INVENTORY | 8 | 78597 | 78956 | varShort | initial inventories |
| World | S | 90 | `LOC_ADD` | ZONE_UPDATES | 6 | 78484 | 78539 | varByte | local loc additions |
| World | S | 91 | `IF_SETHIDE` | INTERFACES | 15 | 78799 | 198072 | 5 | hide/show components |
| World | S | 92 | `SET_VARC_STR_SMALL` | MISC | 35 | 78728 | 84063 | varByte | varc strings |
| World | S | 93 | `MESSAGE_GAME` | CHAT | 2 | 78942 | 187287 | varByte | membership/news messages |
| World | S | 95 | `MIDI_SONG` | AUDIO | 1 | 78191 | 78191 | 5 | initial song |
| World | S | 104 | `PLAYER_INFO_DECODE` | PLAYER_INFO | 8 | 78970 | 78970 | 14 | slots 0..7 prefixes |
| World | S | 110 | `RUNCLIENTSCRIPT` | MISC | 88 | 78599 | 198072 | varShort | full script runner |
| World | S | 119 | `CUTSCENE_DATA` | MISC | 8 | 78970 | 78970 | 35 | eight zero slots |
| World | S | 120 | `CAM_SMOOTHRESET` | CAMERA | 1 | 78895 | 78895 | 0 | camera smoothing reset |
| World | S | 121 | `UPDATE_INV_PARTIAL` | INVENTORY | 1 | 78954 | 78954 | varShort | one partial inventory |
| World | S | 122 | `IF_SETTEXT` | INTERFACES | 7 | 78770 | 79797 | varShort | labels/loading/clan text |
| World | S | 130 | `UPDATE_IGNORELIST` | SOCIAL | 1 | 78473 | 78473 | 10 | all zero empty list |
| World | S | 147 | `SET_VARP_LONG_ALT` | VARIABLES | 5 | 78448 | 78450 | 10 | long varps |
| World | S | 154 | `ENTITY_ANIM_AT_TILE` | MISC | 1 | 78943 | 78943 | 5 | tile anim |
| World | S | 157 | `SCENE_FLAG` | MISC | 1 | 78907 | 78907 | 1 | `00` |
| World | S | 162 | `TRIGGER_ONDIALOGABORT` | CLIENT_STATE | 231 | 78941 | 216070 | 0 | repeated before player/NPC updates |
| World | S | 172 | `SET_PLAYER_FLAG_A` | MISC | 2 | 78188 | 78972 | 1 | `01` |
| World | S | 174 | `ANTI_CHEAT_CHALLENGE` | MISC | 23 | 84060 | 216069 | 8 | challenge stream |
| World | S | 190 | `CLEAR_PENDING_UPDATES` | CLIENT_STATE | 1 | 78941 | 78941 | 0 | clear update queue |
| World | S | 199 | `REBUILD_NORMAL` | CLIENT_STATE | 1 | 78971 | 78971 | 93 | region/rebuild tail |
| World | S | 204 | `SET_PLAYER_FLAG_B` | MISC | 2 | 78189 | 78972 | 1 | `FF` |
| World | S | 209 | `NPC_INFO_THUNK` | NPC_INFO | 1 | 78940 | 78940 | 0 | NPC prelude |
| World | S | 216 | `WORLDLIST_PACKET` | WORLD_DATA | 1 | 79798 | 79798 | 430 | compact worldlist |
| World | C | 3 | `AntiCheatChallengeResponse` | C2S | 6 | socket seq 43..85 | | 9 | first response `3ab4bbcfac9919f08b` |
| World | C | 5 | `SceneGraphReport` | C2S | 18 | socket seq 4..78 | | 4 | scene-progress counters, first `00000004` |
| World | C | 8 | `UNKNOWN_8` | C2S | 1 | socket seq 17 | | 4 | `07007700` |
| World | C | 12 | `UNKNOWN_12` | C2S | 3 | socket seq 10..24 | | varByte size 58 | repeated client capability/settings block |
| World | C | 51 | `Ping` / hook `KEEPALIVE_NUDGE` | C2S | 40 socket / 135 hook | socket seq 34..90 | | 0 | keepalive; hook continues longer than socket transcript |
| World | C | 52 | `UNKNOWN_52` -> `SendDisplayInfo` | C2S | 11 | socket seq 0..22 | | 6 | `020bb0071401` |
| World | C | 54 | `RequestWorldList` | C2S | 1 | socket seq 11 | | 4 | `27b8926d` |
| World | C | 76 | `UNKNOWN_76` | C2S | 1 | socket seq 32 | | 4 | `0000033e` |
| World | C | 94 | `WINDOW_STATUS` | C2S | 1 | socket seq 9 | | 3 | `000100` |
| World | C | 98 | `UNKNOWN_98` | C2S | 3 | socket seq 1,54,58 | | varByte sizes 239,10,10 | large first client environment block, then two small updates |
| World | C | 105 | `EVENT_APPLET_FOCUS` | C2S | 1 | socket seq 3 | | varShort size 1321 | typed client/app-state batch; not simple focus bool |
| World | C | 106 | `UNKNOWN_106` | C2S | 4 | socket seq 2,46,48,52 | | 1 | `00`, then `01/00/01` toggles |
| World | C | 127 | `IfButton` | C2S | 1 | socket seq 38 | | 8 | `0c0001057fffff7f` |

## Ghidra confirmations added in this pass

| Packet | Ghidra evidence | Confidence |
|---|---|---|
| C2S lobby `op52` | `jag::ClientProt::SendDisplayInfo @ 001a2d60` uses ProtEntry `DAT_015d3b70`, writes 1 byte display mode, two 16-bit dimensions, and 1 byte flag. Capture payload `02 0b b0 07 14 01` decodes as mode-ish `2`, width-ish `2992`, height-ish `1812`, flag `1` depending transform naming. | High for emitter and 6-byte role; field names still medium |
| S2C lobby `op49` | `CHANGE_LOBBY @ 00196170`; lobby refresh/list handler, not world connect. | High |
| S2C world switch `op213` | `SWITCH_WORLD @ 001aeba0`; explicit world-hop path that sets main state `0x25`. Not present in cold lobby Play Now flow. | High |
| World-login server-client-var block | `LoginStepWaitingServerClientVarLength @ 00180d00`, `LoginStepWaitingServerClientVarConfigData @ 00180bf0`, `LoginStepWaitingServerClientVar @ 0018f280`; raw pre-ISAAC block, big-endian length, byte-0 ack, then cache-typed `{u16 varcId, value}` entries. Ack `1` advances to players-byte state `0x82`. | High |
| S2C world `op81` | `REBUILD_NORMAL_SIMPLE @ 001daa70`; if world-state flag `+0x49` is set, it first consumes the GPI prefix, then reads the op81 coordinate/build-area header, updates the scene build area, clears the GPI-prefix flag, and calls camera reset unless render state is already `6`. | High |
| S2C world `op22` | `ProcessPlayerInfo @ 001618a0`; four player-list passes, bit-access movement/update decode, then byte-aligns and advances packet cursor by 2 bytes before each extended-info block. First production payload begins `c0`, matching high-res local update with extended info and movement type 0. | High |
| S2C world `op52` | `ProcessNpcInfo @ 001d7b60`; active NPC bit-list update, new-NPC loop until sentinel `0xffff`, movement/spawn bits, byte-align, then cursor skips 2 bytes before each NPC extended-info block. | High |
| S2C world `op77` | `CAM_UPDATE @ 001d3d10`; bitflag-driven modifier for the already reset in-render camera. It updates camera vectors/modes/scalars but does not write render-state `+0x418`; production sends it after op81. | High |
| S2C world `op78` | `UPDATE_ZONE_FULL_FOLLOWS @ 000f7bb0`; reads a 3-byte zone-follow header and clears/repopulates objects in that 8x8 tile zone window. Production sends 606 of these before HUD commit. | High |
| S2C world `op76` | `UPDATE_ZONE_PARTIAL_ENCLOSED @ 000eefc0`; reads a 3-byte zone header, then loops sub-opcodes through `DAT_015d4580`; sub-opcode > `0x11` is invalid. | High |
| S2C world `op199` | `REBUILD_NORMAL @ 001203e0`; later multi-scene/region rebuild form that parses rebuild scene entries and inserts/replaces world-list rebuild data. It is not the first-light op81 rebuild. | High |
| C2S world `op105` | `ClientProt::SendAppletFocusEvents @ 00195100`; emits a size-marked typed client/app-state batch. The 1321-byte production payload is not a desync and not a simple boolean focus event. | High |

## Decoder gaps and adversary review

Current weak points:

1. Current strict socket output has no desyncs, no truncations, and no decoder errors. Old notes about world S2C opcode `24571` or world C2S opcode `214` are disproven by the current strict transcript.
2. Socket output is now reliable for the captured byte window, but hook events remain the complete source for long-running repeated S2C counts after the socket transcript ends. Example: socket has 70 `PlayerInfo` packets, while hook events show 231 over the full logged-in session.
3. `events.jsonl` packet names are useful but can be stale aliases. Example: event labels lobby/world `op26` as `UPDATE_SITESETTINGS_THUNK`, while decoded socket path and code currently model it as `FriendStatus`.
4. Server-client-var block is decoded in `world-login-server-client-var-20260624.md`, but varc names/ownership are still unknown; entries are only ids, types, and values.
5. `PlayerInfo`, `NpcInfo`, and zone update payloads now have handler-level shape from Ghidra, but their bit-level entity/update-mask fields still need full field decode against production payloads.
6. C2S world packets `op8`, `op12`, `op76`, `op98`, and `op106` are cleanly framed but still need emitter/semantic identification.
7. The production lobby sends `SetReadyFlag` before `ChangeLobby` and worldlist chunks. Local server may still need different ordering while synthetic lobby systems are incomplete. Do not treat production order alone as a local fix without matching surrounding systems.

Adversary review:

- A plausible but wrong conclusion is “send `SWITCH_WORLD` on Play Now.” Ghidra and capture contradict it: no `op213` appears in cold lobby Play Now; login response target drives world connection.
- A plausible but wrong conclusion is “socket JSONL proves only 70 player-info packets.” Client-hook events show 231 `PLAYER_INFO` packets over the full logged-in session; use socket for payload framing and hook for complete repeated-count coverage.
- A plausible but wrong conclusion is “worldlist is irrelevant after world login.” Capture shows a compact `WorldListPacket` after ready in logged-in state, likely maintaining world-switcher/lobby metadata.
- A plausible but wrong conclusion is “black screen is cache-only.” Capture’s required systems include first-light var baseline, `op81`, GPI/player info, camera, zone stream, inventory, HUD, ready gate, and continuing entity updates.
- A plausible but wrong conclusion is “archive 40/resource loading explains everything.” This capture reaches `LOGGED_IN`, so transition success requires network/world systems beyond resource download.
- A plausible but wrong conclusion is “reintroduce world C2S `op92`, `op100`, or `op108` because older notes had them.” Current strict transcript does not contain those C2S rows. Treat them as stale desync artifacts unless a newer clean capture reintroduces them.
- A plausible but wrong conclusion is “op105 is just focus.” Ghidra and payload size show a typed applet/client-state batch; modelling it as a single boolean would lose most of the data the client emitted after entering world.

Next required work before goal can be called complete:

- Name and group the 220 server-client-var entries by interface/system ownership.
- Field-decode first `PlayerInfo` size-965 payload and first `NpcInfo` size-778 payload against Ghidra handlers.
- Build opcode-specific docs for high-risk packets: `op77`, `op81`, `op199`, `op22`, `op52`, `op76`, `op78`, `op104`, `op119`, `op162`, `op190`, plus clean-framed C2S `op8`, `op12`, `op76`, `op98`, `op106`.
- Compare local server emitted flow against this library by phase, not by raw byte replay.
