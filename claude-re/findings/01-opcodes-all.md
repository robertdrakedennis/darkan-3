# Every opcode in the production session (per conn / direction)

## lobby S2C — 17 distinct opcodes, 1670 packets

| opcode | name | size_kind | size | count | sample payload |
|---:|---|---|---:|---:|---|
| 61 | VarpSmall | fixed | 3 | 1132 | `001b81` |
| 28 | VarpLarge | fixed | 6 | 438 | `944000840003` |
| 44 | UpdateStat | fixed | 6 | 29 | `000000000100` |
| 82 | IfSetPosition | fixed | 23 | 21 | `7f000000008a032c000000000000000000000000008b03` |
| 110 | RunClientScript | varShort | 15 | 15 | `6969000321000501c539d100001d3e` |
| 47 | ClientSetVarcSmall | fixed | 3 | 12 | `80a80d` |
| 147 | VarpLong | fixed | 10 | 5 | `2e90ffffffffffffffff` |
| 35 | IfSetEvents | fixed | 12 | 4 | `0002010000000000038b0027` |
| 216 | WorldListPacket | varShort | 3001 | 4 | `0002010d80e100556e697465642053746174657320284561` |
| 64 | ClientSetVarcLarge | fixed | 6 | 2 | `cca20328530a` |
| 128 | NoopVarA | fixed | 0 | 2 | `` |
| 5 | ResetClientVarcache | fixed | 0 | 1 | `` |
| 3 | IfSetTopLevelInterface | fixed | 19 | 1 | `0000000000000000000a030000000000000000` |
| 80 | UpdateRunenergy | fixed | 1 | 1 | `01` |
| 75 | SetReadyFlag | fixed | 0 | 1 | `` |
| 49 | ChangeLobby | varShort | 0 | 1 | `` |
| 26 | FriendStatus | varShort | 0 | 1 | `` |

## lobby C2S — 4 distinct opcodes, 13 packets

| opcode | name | size_kind | size | count | sample payload |
|---:|---|---|---:|---:|---|
| 52 | UNKNOWN_52 | fixed | 6 | 9 | `020d80083801` |
| 5 | SceneGraphReport | fixed | 4 | 2 | `00000005` |
| 54 | RequestWorldList | fixed | 4 | 1 | `ffffffff` |
| 218 | UNKNOWN_218 | fixed | 70 | 1 | `0000000512ffffffff9f00000037eb020d80083801bb020d` |

## world S2C — 58 distinct opcodes, 3790 packets

| opcode | name | size_kind | size | count | sample payload |
|---:|---|---|---:|---:|---|
| 61 | VarpSmall | fixed | 3 | 1176 | `001b81` |
| 35 | IfSetEvents | fixed | 12 | 871 | `001e000000001d0005ba0007` |
| 78 | UpdateZoneFullFollowsV2 | fixed | 3 | 616 | `80760a` |
| 28 | VarpLarge | fixed | 6 | 491 | `944000840003` |
| 110 | RunClientScript | varShort | 10 | 86 | `69000000000000003fac` |
| 76 | UpdateZonePartialEnclosed | varShort | 10 | 64 | `001473000085040100ac` |
| 47 | ClientSetVarcSmall | fixed | 3 | 61 | `80b500` |
| 82 | IfSetPosition | fixed | 23 | 56 | `7f00000000c5051f00000000000000000000000000ca05` |
| 22 | PlayerInfo | varShort | 677 | 54 | `c05d90ce5465168b2a827f9dc6944de700000856bd281c00` |
| 162 | TRIGGER_ONDIALOGABORT | fixed | 0 | 54 | `` |
| 52 | NpcInfo | varShort | 707 | 53 | `0016d766c6005f20b6b34e3002f985b441f08f90c02db427` |
| 92 | ClientSetVarcStr | varByte | 3 | 35 | `cc0900` |
| 44 | UpdateStat | fixed | 6 | 29 | `000000000100` |
| 16 | LocDel | fixed | 2 | 24 | `5890` |
| 46 | ObjAdd | fixed | 5 | 21 | `33000102d0` |
| 91 | IfSetHide | fixed | 5 | 11 | `8000050589` |
| 85 | UPDATE_INV_FULL | varShort | 5 | 8 | `0313000000` |
| 119 | CutsceneData | fixed | 35 | 8 | `000007020000000000000000000000000000000000000000` |
| 104 | PlayerInfoDecode | fixed | 14 | 8 | `0000000000000000000000000000` |
| 122 | IfSetText | varShort | 14 | 7 | `416476656e747572650088050600` |
| 17 | SetPlayerOp | varByte | 11 | 6 | `fffffd466f6c6c6f770000` |
| 147 | VarpLong | fixed | 10 | 5 | `2e90ffffffffffffffff` |
| 174 | AntiCheatChallenge | fixed | 8 | 5 | `22647c1230cef232` |
| 172 | MinimapFlagA | fixed | 1 | 2 | `01` |
| 204 | MinimapFlagB | fixed | 1 | 2 | `ff` |
| 90 | LocAdd | varByte | 6 | 2 | `018b90000083` |
| 62 | IfCloseSub | fixed | 4 | 2 | `5f00c505` |
| 30 | IfSet2DAngle | fixed | 8 | 2 | `800707000000aa0a` |
| 64 | ClientSetVarcLarge | fixed | 6 | 2 | `0a8b00008b03` |
| 81 | RebuildNormalSimple | varShort | 5137 | 1 | `0ca33288000000b4d40ccc40b0d40b0d40bcd00b0d40b0d4` |
| 54 | HashedWorldToken | varByte | 44 | 1 | `7777476c725a484635674a57706e4f6a687a47696b75384c` |
| 73 | MinimapState | fixed | 2 | 1 | `8080` |
| 74 | JcoinsUpdate | fixed | 4 | 1 | `ada90a4a` |
| 95 | MidiSong | fixed | 5 | 1 | `7494660000` |
| 5 | ResetClientVarcache | fixed | 0 | 1 | `` |
| 55 | DestroyZoneData | fixed | 0 | 1 | `` |
| 1 | SetNpcOp | varByte | 0 | 1 | `` |
| 77 | CAMERA_UPDATE | varShort | 121 | 1 | `817fff42c8000042c8000042c8000042c8000042c8000042` |
| 130 | UpdateIgnoreListRaw | varByte | 10 | 1 | `00000000000000000000` |
| 3 | IfSetTopLevelInterface | fixed | 19 | 1 | `00000000000000000045050000000000000000` |
| 120 | CAM_SMOOTHRESET | fixed | 0 | 1 | `` |
| 157 | SceneFlag | fixed | 1 | 1 | `00` |
| 209 | NpcInfoThunk | varShort | 0 | 1 | `` |
| 190 | ClearPendingUpdates | fixed | 0 | 1 | `` |
| 93 | GameMessage | varByte | 73 | 1 | `80880000000000506179207468726f75676820796f757220` |
| 154 | EntityAnimAtTile | fixed | 5 | 1 | `8010008100` |
| 121 | UPDATE_INV_PARTIAL | varShort | 11 | 1 | `031b0000cd4cff000003e8` |
| 12 | SetPlayerOp2 | fixed | 2 | 1 | `0000` |
| 13 | SetPlayerOp3 | fixed | 1 | 1 | `64` |
| 7 | ResetEntityLists | fixed | 0 | 1 | `` |
| 45 | SetMultiwayState | fixed | 1 | 1 | `00` |
| 67 | ClanChannelFull | varShort | 0 | 1 | `` |
| 199 | RebuildRegion | varShort | 93 | 1 | `040000000400010000000100000001010000000003000000` |
| 80 | UpdateRunenergy | fixed | 1 | 1 | `01` |
| 75 | SetReadyFlag | fixed | 0 | 1 | `` |
| 49 | ChangeLobby | varShort | 0 | 1 | `` |
| 216 | WorldListPacket | varShort | 430 | 1 | `0102000000690100ad0201570300800400e205006f0600f5` |
| 26 | FriendStatus | varShort | 0 | 1 | `` |

## world C2S — 14 distinct opcodes, 80 packets

| opcode | name | size_kind | size | count | sample payload |
|---:|---|---|---:|---:|---|
| 51 | Ping | fixed | 0 | 31 | `` |
| 5 | SceneGraphReport | fixed | 4 | 18 | `00000004` |
| 52 | UNKNOWN_52 | fixed | 6 | 10 | `020d80083801` |
| 3 | AntiCheatChallengeResponse | fixed | 9 | 5 | `22647c1232f2ce3091` |
| 106 | UNKNOWN_106 | fixed | 1 | 4 | `00` |
| 12 | UNKNOWN_12 | varByte | 58 | 3 | `260100020302050301010101020102020300020201010001` |
| 98 | UNKNOWN_98 | varByte | 247 | 2 | `081fffff03c30787076006e00761076106e2072207620721` |
| 240 | UNKNOWN_240 | fixed | 7 | 1 | `5e020d80083801` |
| 105 | EVENT_APPLET_FOCUS | varShort | 1321 | 1 | `010b2f29ffffff1ff500ffffff0b30264445350b31ff3728` |
| 94 | WINDOW_STATUS | fixed | 3 | 1 | `000100` |
| 54 | RequestWorldList | fixed | 4 | 1 | `27b8926d` |
| 8 | UNKNOWN_8 | fixed | 4 | 1 | `07007700` |
| 76 | UNKNOWN_76 | fixed | 4 | 1 | `000002ee` |
| 127 | IfButton | fixed | 8 | 1 | `0c0001057fffff7f` |

