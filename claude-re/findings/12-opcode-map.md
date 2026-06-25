# Wire opcode -> binary handler map (CORRECTED counts; per conn/dir)

## lobby S2C (17 opcodes, 1670 pkts)

| op | wire name | count | size | binary handler | cat | addr |
|--:|---|--:|--:|---|---|---|
| 3 | IfSetTopLevelInterface | 1 | 19 | `IF_SETTOPLEVELINTERFACE` | Interfaces | 00186a80 |
| 5 | ResetClientVarcache | 1 | 0 | (unmatched) | | |
| 26 | FriendStatus | 1 | 0 | (unmatched) | | |
| 28 | VarpLarge | 438 | 6 | `VARP_LARGE` | ClientState | 00119a90 |
| 35 | IfSetEvents | 4 | 12 | `IF_SETEVENTS` | Interfaces | 001860e0 |
| 44 | UpdateStat | 29 | 6 | (unmatched) | | |
| 47 | ClientSetVarcSmall | 12 | 3 | `CLIENT_SETVARC_SMALL` | ClientState | 00119870 |
| 49 | ChangeLobby | 1 | 0 | `CHANGE_LOBBY` | Lobby | 00196170 |
| 61 | VarpSmall | 1132 | 3 | `VARP_SMALL` | ClientState | 00119b30 |
| 64 | ClientSetVarcLarge | 2 | 6 | `CLIENT_SETVARC_LARGE` | ClientState | 00119780 |
| 75 | SetReadyFlag | 1 | 0 | `SET_READY_FLAG` | ClientState | 00175120 |
| 80 | UpdateRunenergy | 1 | 1 | (unmatched) | | |
| 82 | IfSetPosition | 21 | 23 | `IF_SETPOSITION` | Interfaces | 00189300 |
| 110 | RunClientScript | 15 | 5 | `RUNCLIENTSCRIPT` | ClientState | 00145370 |
| 128 | NoopVarA | 2 | 0 | (unmatched) | | |
| 147 | VarpLong | 5 | 10 | `VARP_LONG` | ClientState | 00141690 |
| 216 | WorldListPacket | 4 | 430 | (unmatched) | | |

## lobby C2S (4 opcodes, 13 pkts)

| op | wire name | count | size | binary handler | cat | addr |
|--:|---|--:|--:|---|---|---|
| 5 | SceneGraphReport | 2 | 4 | (unmatched) | | |
| 52 | UNKNOWN_52 | 9 | 6 | (unmatched) | | |
| 54 | RequestWorldList | 1 | 4 | (unmatched) | | |
| 218 | UNKNOWN_218 | 1 | 70 | (unmatched) | | |

## world S2C (58 opcodes, 3790 pkts)

| op | wire name | count | size | binary handler | cat | addr |
|--:|---|--:|--:|---|---|---|
| 1 | SetNpcOp | 1 | 0 | `SET_NPC_OP` | NPCInfo | 0017ff30 |
| 3 | IfSetTopLevelInterface | 1 | 19 | `IF_SETTOPLEVELINTERFACE` | Interfaces | 00186a80 |
| 5 | ResetClientVarcache | 1 | 0 | (unmatched) | | |
| 7 | ResetEntityLists | 1 | 0 | `RESET_ENTITY_LISTS` | Misc | 00143d50 |
| 12 | SetPlayerOp2 | 1 | 2 | `SET_PLAYER_OP_2` | Misc | 000f0510 |
| 13 | SetPlayerOp3 | 1 | 1 | `SET_PLAYER_OP_3` | Misc | 000f05a0 |
| 16 | LocDel | 24 | 2 | `LOC_DEL` | ZoneUpdates | 001391f0 |
| 17 | SetPlayerOp | 6 | 9 | `SET_PLAYER_OP` | Misc | 0013f040 |
| 22 | PlayerInfo | 54 | 68 | `PLAYER_INFO_DECODE` | PlayerInfo | 00183ed0 |
| 26 | FriendStatus | 1 | 0 | (unmatched) | | |
| 28 | VarpLarge | 491 | 6 | `VARP_LARGE` | ClientState | 00119a90 |
| 30 | IfSet2DAngle | 2 | 8 | `IF_SET2DANGLE` | Interfaces | 00194090 |
| 35 | IfSetEvents | 871 | 12 | `IF_SETEVENTS` | Interfaces | 001860e0 |
| 44 | UpdateStat | 29 | 6 | (unmatched) | | |
| 45 | SetMultiwayState | 1 | 1 | `SET_MULTIWAY_STATE` | Misc | 00173c50 |
| 46 | ObjAdd | 21 | 5 | `OBJ_ADD` | ZoneUpdates | 001193c0 |
| 47 | ClientSetVarcSmall | 61 | 3 | `CLIENT_SETVARC_SMALL` | ClientState | 00119870 |
| 49 | ChangeLobby | 1 | 0 | `CHANGE_LOBBY` | Lobby | 00196170 |
| 52 | NpcInfo | 53 | 76 | `NPC_INFO_thunk_worldentity` | NPCInfo | 001d54f0 |
| 54 | HashedWorldToken | 1 | 44 | (unmatched) | | |
| 55 | DestroyZoneData | 1 | 0 | `DESTROY_ZONE_DATA` | ClientState | 000ef4e0 |
| 61 | VarpSmall | 1176 | 3 | `VARP_SMALL` | ClientState | 00119b30 |
| 62 | IfCloseSub | 2 | 4 | `IF_CLOSESUB_ACTIVE` | Interfaces | 001864e0 |
| 64 | ClientSetVarcLarge | 2 | 6 | `CLIENT_SETVARC_LARGE` | ClientState | 00119780 |
| 67 | ClanChannelFull | 1 | 0 | `CLANCHANNEL_FULL` | Clans | 00199130 |
| 73 | MinimapState | 1 | 2 | (unmatched) | | |
| 74 | JcoinsUpdate | 1 | 4 | (unmatched) | | |
| 75 | SetReadyFlag | 1 | 0 | `SET_READY_FLAG` | ClientState | 00175120 |
| 76 | UpdateZonePartialEnclosed | 64 | 10 | `UPDATE_ZONE_PARTIAL_ENCLOSED` | ZoneUpdates | 000eefc0 |
| 77 | CAMERA_UPDATE | 1 | 121 | (unmatched) | | |
| 78 | UpdateZoneFullFollowsV2 | 616 | 3 | `UPDATE_ZONE_FULL_FOLLOWS` | ZoneUpdates | 000f9510 |
| 80 | UpdateRunenergy | 1 | 1 | (unmatched) | | |
| 81 | RebuildNormalSimple | 1 | 5137 | `REBUILD_NORMAL_SIMPLE` | ClientState | 001daa70 |
| 82 | IfSetPosition | 56 | 23 | `IF_SETPOSITION` | Interfaces | 00189300 |
| 85 | UPDATE_INV_FULL | 8 | 17 | `UPDATE_INV_FULL_impl` | Inventory | 001a8d30 |
| 90 | LocAdd | 2 | 6 | `LOC_ADD` | ZoneUpdates | 00139200 |
| 91 | IfSetHide | 11 | 5 | `IF_SETHIDE` | Interfaces | 00194140 |
| 92 | ClientSetVarcStr | 35 | 3 | (unmatched) | | |
| 93 | GameMessage | 1 | 73 | (unmatched) | | |
| 95 | MidiSong | 1 | 5 | `MIDI_SONG` | Audio | 00187330 |
| 104 | PlayerInfoDecode | 8 | 14 | `PLAYER_INFO_DECODE` | PlayerInfo | 00183ed0 |
| 110 | RunClientScript | 86 | 15 | `RUNCLIENTSCRIPT` | ClientState | 00145370 |
| 119 | CutsceneData | 8 | 35 | `CUTSCENE_DATA` | Misc | 00174000 |
| 120 | CAM_SMOOTHRESET | 1 | 0 | `CAM_SMOOTHRESET` | Camera | 00186f10 |
| 121 | UPDATE_INV_PARTIAL | 1 | 11 | `UPDATE_INV_PARTIAL` | Inventory | 00184320 |
| 122 | IfSetText | 7 | 205 | `IF_SETTEXT` | Interfaces | 00186040 |
| 130 | UpdateIgnoreListRaw | 1 | 10 | (unmatched) | | |
| 147 | VarpLong | 5 | 10 | `VARP_LONG` | ClientState | 00141690 |
| 154 | EntityAnimAtTile | 1 | 5 | (unmatched) | | |
| 157 | SceneFlag | 1 | 1 | (unmatched) | | |
| 162 | TRIGGER_ONDIALOGABORT | 54 | 0 | `TRIGGER_ONDIALOGABORT` | ClientState | 000f1f30 |
| 172 | MinimapFlagA | 2 | 1 | (unmatched) | | |
| 174 | AntiCheatChallenge | 5 | 8 | (unmatched) | | |
| 190 | ClearPendingUpdates | 1 | 0 | `CLEAR_PENDING_UPDATES` | ClientState | 000f4110 |
| 199 | RebuildRegion | 1 | 93 | `REBUILD_REGION_ALT` | ClientState | 001df100 |
| 204 | MinimapFlagB | 2 | 1 | (unmatched) | | |
| 209 | NpcInfoThunk | 1 | 0 | `NPC_INFO_thunk_worldentity` | NPCInfo | 001d54f0 |
| 216 | WorldListPacket | 1 | 430 | (unmatched) | | |

## world C2S (14 opcodes, 80 pkts)

| op | wire name | count | size | binary handler | cat | addr |
|--:|---|--:|--:|---|---|---|
| 3 | AntiCheatChallengeResponse | 5 | 9 | (unmatched) | | |
| 5 | SceneGraphReport | 18 | 4 | (unmatched) | | |
| 8 | UNKNOWN_8 | 1 | 4 | (unmatched) | | |
| 12 | UNKNOWN_12 | 3 | 58 | (unmatched) | | |
| 51 | Ping | 31 | 0 | (unmatched) | | |
| 52 | UNKNOWN_52 | 10 | 6 | (unmatched) | | |
| 54 | RequestWorldList | 1 | 4 | (unmatched) | | |
| 76 | UNKNOWN_76 | 1 | 4 | (unmatched) | | |
| 94 | WINDOW_STATUS | 1 | 3 | (unmatched) | | |
| 98 | UNKNOWN_98 | 2 | 91 | (unmatched) | | |
| 105 | EVENT_APPLET_FOCUS | 1 | 1321 | (unmatched) | | |
| 106 | UNKNOWN_106 | 4 | 1 | (unmatched) | | |
| 127 | IfButton | 1 | 8 | (unmatched) | | |
| 240 | UNKNOWN_240 | 1 | 7 | (unmatched) | | |


**matched 52, unmatched 41**
