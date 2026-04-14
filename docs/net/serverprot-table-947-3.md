# ServerProt Opcode Table - Rev 947-3

Extracted from rs2client.947-3 binary via Ghidra.
- RegisterAll at `0x00181e10`, BindHandlers at `0x001185aa`
- g_serverProtVector at `0x016ee0a0`, max opcode 0xD9 (217)
- Total entries: **218** (unchanged from 947-1)
- Size changes from 947-1: **NONE** (all 218 opcodes have identical sizes)
- Handler matches: 67 named via packethandlers:: namespace, 55 FUN_ unnamed handlers, 96 unassigned in BindHandlers

## Complete Table

| Opcode | Hex  | Size | Handler (947-3)                              | Name (947-1)                  |
|--------|------|------|----------------------------------------------|-------------------------------|
| 0      | 0x00 | -1   | FUN_002478d0                                 | UNKNOWN_0                     |
| 1      | 0x01 | 3    |                                              | CLIENT_SETVARC_SMALL          |
| 2      | 0x02 | -2   |                                              | UNKNOWN_2                     |
| 3      | 0x03 | 1    | Camera::CAM_FORCEANGLE                       | CAM_FORCEANGLE                |
| 4      | 0x04 | 4    | FUN_00193340                                 | UNKNOWN_4                     |
| 5      | 0x05 | -2   | Inventory::UPDATE_INV_PARTIAL                | UPDATE_INV_PARTIAL            |
| 6      | 0x06 | -1   | FUN_0018cf90                                 | HASHED_WORLD_TOKEN            |
| 7      | 0x07 | -1   | Chat::MESSAGE_FRIENDCHANNEL                  | MESSAGE_FRIENDCHANNEL         |
| 8      | 0x08 | 23   |                                              | IF_SETPOSITION                |
| 9      | 0x09 | -2   |                                              | UNKNOWN_9                     |
| 10     | 0x0A | 3    |                                              | VARP_SMALL                    |
| 11     | 0x0B | -2   | Chat::CLANSETTINGS_DELTA_CHAT                | CLANSETTINGS_DELTA_CHAT       |
| 12     | 0x0C | -2   | NPCInfo::NPC_INFO                            | NPC_INFO                      |
| 13     | 0x0D | 6    | FUN_00193250                                 | UNKNOWN_13                    |
| 14     | 0x0E | 5    |                                              | UNKNOWN_14                    |
| 15     | 0x0F | -2   |                                              | UNKNOWN_15                    |
| 16     | 0x10 | 4    |                                              | UNKNOWN_16                    |
| 17     | 0x11 | 8    |                                              | IF_OPENSUB                    |
| 18     | 0x12 | 3    |                                              | UNKNOWN_18                    |
| 19     | 0x13 | 1    | Misc::SET_RUN_ENERGY                         | UPDATE_RUNENERGY              |
| 20     | 0x14 | 7    |                                              | UNKNOWN_20                    |
| 21     | 0x15 | -1   | Chat::MESSAGE_FRIENDCHAT                     | UNKNOWN_21                    |
| 22     | 0x16 | 6    | FUN_0018d010                                 | UNKNOWN_22                    |
| 23     | 0x17 | 4    | FUN_001c2320                                 | UNKNOWN_23                    |
| 24     | 0x18 | -1   | NPCInfo::SET_NPC_OP                          | NPC_OP                        |
| 25     | 0x19 | 0    |                                              | UNKNOWN_25                    |
| 26     | 0x1A | 6    | FUN_00193170                                 | UNKNOWN_26                    |
| 27     | 0x1B | -2   |                                              | PLAYER_INFO                   |
| 28     | 0x1C | -2   | Clans::CLANCHANNEL_FULL                      | CLANCHANNEL_FULL              |
| 29     | 0x1D | 3    | FUN_0021aa40                                 | UNKNOWN_29                    |
| 30     | 0x1E | -2   | Lobby::CHANGE_LOBBY                          | CHANGE_LOBBY                  |
| 31     | 0x1F | -2   | FUN_00198a30                                 | UNKNOWN_31                    |
| 32     | 0x20 | 10   |                                              | UNKNOWN_32                    |
| 33     | 0x21 | 4    |                                              | IF_CLOSESUB                   |
| 34     | 0x22 | 10   |                                              | IF_SETEVENTS                  |
| 35     | 0x23 | 12   |                                              | IF_SETEVENTS2                 |
| 36     | 0x24 | 28   | FUN_00228d70                                 | UPDATE_UID192                 |
| 37     | 0x25 | 2    |                                              | UNKNOWN_37                    |
| 38     | 0x26 | 5    |                                              | UNKNOWN_38                    |
| 39     | 0x27 | -1   | Chat::CHAT_FILTER_SETTINGS                   | CHAT_FILTER_SETTINGS          |
| 40     | 0x28 | 19   | FUN_00195660                                 | UNKNOWN_40                    |
| 41     | 0x29 | -1   |                                              | UNKNOWN_41                    |
| 42     | 0x2A | 3    |                                              | UNKNOWN_42                    |
| 43     | 0x2B | 0    | Misc::RESET_ENTITY_LISTS                     | RESET_ENTITY_LISTS            |
| 44     | 0x2C | 6    |                                              | UNKNOWN_44                    |
| 45     | 0x2D | -2   | Chat::MESSAGE_PUBLIC                         | MESSAGE_PUBLIC                |
| 46     | 0x2E | -2   | Camera::CAM_UPDATE                           | CAMERA_UPDATE                 |
| 47     | 0x2F | 20   |                                              | MAP_PROJANIM                  |
| 48     | 0x30 | 0    |                                              | RESET_CLIENT_VARCACHE         |
| 49     | 0x31 | 0    |                                              | UNKNOWN_49                    |
| 50     | 0x32 | 3    |                                              | UNKNOWN_50                    |
| 51     | 0x33 | 7    |                                              | UNKNOWN_51                    |
| 52     | 0x34 | 2    | ClientState::SET_TICK_TIMER                  | SET_TICK_TIMER                |
| 53     | 0x35 | 8    |                                              | UNKNOWN_53                    |
| 54     | 0x36 | -1   | NPCInfo::NPC_HEADICON_SPECIFIC               | NPC_HEADICON_SPECIFIC         |
| 55     | 0x37 | 6    |                                              | CLIENT_SETVARCBIT_LARGE       |
| 56     | 0x38 | 10   |                                              | UNKNOWN_56                    |
| 57     | 0x39 | 3    |                                              | UNKNOWN_57                    |
| 58     | 0x3A | -1   | Chat::MESSAGE_QUICKCHAT_CLANCHAT             | MESSAGE_QUICKCHAT_CLANCHAT    |
| 59     | 0x3B | 4    | Misc::SET_DISPLAY_INT                        | JCOINS_UPDATE                 |
| 60     | 0x3C | 7    |                                              | UNKNOWN_60                    |
| 61     | 0x3D | 1    | NPCInfo::SET_NPC_UPDATE_ORIGIN               | NPC_UPDATE_ORIGIN             |
| 62     | 0x3E | 11   |                                              | UNKNOWN_62                    |
| 63     | 0x3F | -2   | Clans::CLANSETTINGS_DELTA                    | CLANSETTINGS_DELTA            |
| 64     | 0x40 | 8    |                                              | UNKNOWN_64                    |
| 65     | 0x41 | 0    | ClientState::SET_READY_FLAG                  | SET_READY_FLAG                |
| 66     | 0x42 | 6    |                                              | UPDATE_STAT                   |
| 67     | 0x43 | -1   |                                              | CLIENT_SETVARC_STR            |
| 68     | 0x44 | 6    |                                              | IF_OPENTOP                    |
| 69     | 0x45 | -2   | Inventory::UPDATE_INV_FULL_impl              | UPDATE_INV_FULL               |
| 70     | 0x46 | 0    |                                              | UNKNOWN_70                    |
| 71     | 0x47 | 6    |                                              | UNKNOWN_71                    |
| 72     | 0x48 | 5    |                                              | UNKNOWN_72                    |
| 73     | 0x49 | -1   | Chat::MESSAGE_QUICKCHAT_PRIVATE              | MESSAGE_QUICKCHAT_PRIVATE     |
| 74     | 0x4A | 8    |                                              | UNKNOWN_74                    |
| 75     | 0x4B | 10   |                                              | UNKNOWN_75                    |
| 76     | 0x4C | 10   |                                              | UNKNOWN_76                    |
| 77     | 0x4D | 1    | Misc::SET_MULTIWAY_STATE                     | SET_MULTIWAY_STATE            |
| 78     | 0x4E | 14   | PlayerInfo::PLAYER_INFO_DECODE               | PLAYER_INFO_DECODE            |
| 79     | 0x4F | -1   |                                              | UNKNOWN_79                    |
| 80     | 0x50 | 0    | Camera::CAM_SMOOTHRESET                      | CAM_SMOOTHRESET               |
| 81     | 0x51 | 4    |                                              | UNKNOWN_81                    |
| 82     | 0x52 | -1   | Chat::MESSAGE_QUICKCHAT_CLANCHANNEL          | MESSAGE_QUICKCHAT_CLANCHANNEL |
| 83     | 0x53 | -1   | Clans::CLANCHANNEL_DELTA                     | CLANCHANNEL_DELTA             |
| 84     | 0x54 | -2   | Chat::CLANCHANNEL_FULL_CHAT                  | CLANCHANNEL_FULL_CHAT         |
| 85     | 0x55 | 8    |                                              | UNKNOWN_85                    |
| 86     | 0x56 | -2   | FUN_00214580                                 | UNKNOWN_86                    |
| 87     | 0x57 | 5    |                                              | MIDI_SONG                     |
| 88     | 0x58 | 10   |                                              | UNKNOWN_88                    |
| 89     | 0x59 | 0    | Camera::CAM_RESET                            | CAM_RESET                     |
| 90     | 0x5A | -2   | FUN_002140c0                                 | UNKNOWN_90                    |
| 91     | 0x5B | 35   | Misc::CUTSCENE_DATA                          | CUTSCENE                      |
| 92     | 0x5C | 8    |                                              | UNKNOWN_92                    |
| 93     | 0x5D | 10   | FUN_00192780                                 | UNKNOWN_93                    |
| 94     | 0x5E | 19   |                                              | IF_SETTOPLEVELINTERFACE       |
| 95     | 0x5F | 10   | FUN_001de160                                 | UNKNOWN_95                    |
| 96     | 0x60 | 8    |                                              | UNKNOWN_96                    |
| 97     | 0x61 | 25   |                                              | UNKNOWN_97                    |
| 98     | 0x62 | 10   |                                              | UNKNOWN_98                    |
| 99     | 0x63 | -1   |                                              | UNKNOWN_99                    |
| 100    | 0x64 | 10   |                                              | UNKNOWN_100                   |
| 101    | 0x65 | -2   | FUN_001e9290                                 | MESSAGE_TYPE6                 |
| 102    | 0x66 | -2   | FUN_00191190                                 | UPDATE_FRIENDLIST             |
| 103    | 0x67 | 5    |                                              | IF_SETHIDE                    |
| 104    | 0x68 | -2   | Clans::CLANSETTINGS_FULL                     | CLANSETTINGS_FULL             |
| 105    | 0x69 | -1   | Chat::MESSAGE_GAME                           | MESSAGE_GAME                  |
| 106    | 0x6A | 10   |                                              | UNKNOWN_106                   |
| 107    | 0x6B | 25   |                                              | UNKNOWN_107                   |
| 108    | 0x6C | 2    | Misc::SET_PLAYER_OP_2                        | SET_PLAYER_OP_2               |
| 109    | 0x6D | -2   | PlayerGroup::PLAYER_OP                       | PLAYER_GROUP_FULL             |
| 110    | 0x6E | 29   |                                              | UNKNOWN_110                   |
| 111    | 0x6F | 6    |                                              | VARP_LARGE                    |
| 112    | 0x70 | 6    |                                              | CLIENT_SETVARC_LARGE          |
| 113    | 0x71 | 1    | Camera::CAM_TARGET                           | CAM_TARGET                    |
| 114    | 0x72 | 2    | FUN_0018ceb0                                 | UNKNOWN_114                   |
| 115    | 0x73 | 3    |                                              | CLIENT_SETVARCBIT_SMALL       |
| 116    | 0x74 | 1    | Misc::SET_PLAYER_OP_3                        | SET_PLAYER_OP_3               |
| 117    | 0x75 | 32   |                                              | IF_SETANGLE                   |
| 118    | 0x76 | 25   | FUN_00194550                                 | UNKNOWN_118                   |
| 119    | 0x77 | -1   | FUN_001edfb0                                 | UNKNOWN_119                   |
| 120    | 0x78 | 0    |                                              | UNKNOWN_120                   |
| 121    | 0x79 | -2   |                                              | RUNCLIENTSCRIPT               |
| 122    | 0x7A | 8    |                                              | UNKNOWN_122                   |
| 123    | 0x7B | 8    |                                              | UNKNOWN_123                   |
| 124    | 0x7C | 12   | FUN_0020bca0                                 | UNKNOWN_124                   |
| 125    | 0x7D | -1   | Chat::MESSAGE_CLANCHANNEL                    | MESSAGE_CLANCHANNEL_SYSTEM    |
| 126    | 0x7E | -2   |                                              | UNKNOWN_126                   |
| 127    | 0x7F | 11   |                                              | UNKNOWN_127                   |
| 128    | 0x80 | 6    |                                              | UNKNOWN_128                   |
| 129    | 0x81 | -1   | Chat::MESSAGE_PRIVATE_ECHO                   | MESSAGE_PRIVATE_ECHO          |
| 130    | 0x82 | 0    |                                              | UNKNOWN_130                   |
| 131    | 0x83 | 3    | FUN_0019add0                                 | UNKNOWN_131                   |
| 132    | 0x84 | 4    | Misc::SET_SYSUPDATE_TIMER                    | UPDATE_REBOOT_TIMER           |
| 133    | 0x85 | 2    |                                              | UNKNOWN_133                   |
| 134    | 0x86 | -2   | FUN_00219790                                 | UNKNOWN_134                   |
| 135    | 0x87 | 3    | FUN_001ba5b0                                 | UNKNOWN_135                   |
| 136    | 0x88 | 9    |                                              | UNKNOWN_136                   |
| 137    | 0x89 | 8    | FUN_001fabf0                                 | UNKNOWN_137                   |
| 138    | 0x8A | 1    | NPCInfo::SET_NPC_UPDATE_FLAG                 | NPC_UPDATE_FLAG               |
| 139    | 0x8B | -2   | FUN_0018cfa0                                 | UNKNOWN_139                   |
| 140    | 0x8C | 5    | FUN_001c0370                                 | UNKNOWN_140                   |
| 141    | 0x8D | 5    |                                              | UNKNOWN_141                   |
| 142    | 0x8E | 0    |                                              | UNKNOWN_142                   |
| 143    | 0x8F | 1    | FUN_0021a560                                 | UNKNOWN_143                   |
| 144    | 0x90 | 3    | Misc::SET_INTERACTION_FLAG_D                 | SET_INTERACTION_FLAG_D        |
| 145    | 0x91 | 14   |                                              | UNKNOWN_145                   |
| 146    | 0x92 | -1   | Interfaces::IF_SETGRAPHIC_ACTIVE_handler     | IF_SETGRAPHIC_ACTIVE          |
| 147    | 0x93 | 0    | Misc::LOGOUT                                 | LOGOUT                        |
| 148    | 0x94 | 9    | FUN_00287150                                 | UNKNOWN_148                   |
| 149    | 0x95 | 17   | FUN_001957f0                                 | NEW_PACKET_149                |
| 150    | 0x96 | 5    |                                              | UNKNOWN_150                   |
| 151    | 0x97 | -1   | Chat::MESSAGE_PRIVATE                        | MESSAGE_PRIVATE               |
| 152    | 0x98 | 4    | FUN_001926e0                                 | UNKNOWN_152                   |
| 153    | 0x99 | 4    | FUN_00192650                                 | UNKNOWN_153                   |
| 154    | 0x9A | 1    | FUN_00219c10                                 | UNKNOWN_154                   |
| 155    | 0x9B | 1    | Chat::SET_CHAT_FILTER_B                      | SET_CHAT_FILTER_B             |
| 156    | 0x9C | 1    | FUN_0021a670                                 | UNKNOWN_156                   |
| 157    | 0x9D | 2    | FUN_0022cfb0                                 | UNKNOWN_157                   |
| 158    | 0x9E | 12   |                                              | UNKNOWN_158                   |
| 159    | 0x9F | -2   | FUN_0023a430                                 | WORLDLIST_FETCH_REPLY         |
| 160    | 0xA0 | 3    | FUN_0018fe30                                 | UNKNOWN_160                   |
| 161    | 0xA1 | 10   |                                              | UNKNOWN_161                   |
| 162    | 0xA2 | 2    |                                              | UNKNOWN_162                   |
| 163    | 0xA3 | 2    | FUN_001ba660                                 | UNKNOWN_163                   |
| 164    | 0xA4 | 1    | FUN_0021a2e0                                 | UNKNOWN_164                   |
| 165    | 0xA5 | 4    |                                              | UNKNOWN_165                   |
| 166    | 0xA6 | -2   | Misc::SKIP_DATA                              | SKIP_DATA                     |
| 167    | 0xA7 | -1   |                                              | UNKNOWN_167                   |
| 168    | 0xA8 | 2    |                                              | UNKNOWN_168                   |
| 169    | 0xA9 | 2    |                                              | UNKNOWN_169                   |
| 170    | 0xAA | 10   |                                              | VARP_LONG                     |
| 171    | 0xAB | 8    | Misc::SERVER_TICK_END                        | SERVER_TICK_END               |
| 172    | 0xAC | -2   | ClientState::REBUILD_REGION                  | REBUILD_NORMAL                |
| 173    | 0xAD | -2   | ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS_handler| UPDATE_ZONE_FULL_FOLLOWS      |
| 174    | 0xAE | 1    | Misc::SET_INTERACTION_FLAG_C                 | SET_INTERACTION_FLAG_C        |
| 175    | 0xAF | 6    | FUN_0019b1f0                                 | UNKNOWN_175                   |
| 176    | 0xB0 | -2   | FUN_00198a40                                 | UNKNOWN_176                   |
| 177    | 0xB1 | -2   | Inventory::UPDATE_INV_GROUP                  | UPDATE_INV_GROUP              |
| 178    | 0xB2 | -2   | PlayerInfo::UPDATE_PLAYER_CHAT               | UPDATE_PLAYER_CHAT            |
| 179    | 0xB3 | -1   | Chat::FRIENDCHAT_JOIN                        | FRIENDCHAT_JOIN               |
| 180    | 0xB4 | -2   | PlayerInfo::PLAYER_INFO_DECODE_2             | PLAYER_INFO_DECODE_2          |
| 181    | 0xB5 | 6    |                                              | UNKNOWN_181                   |
| 182    | 0xB6 | 5    | FUN_001fa800                                 | UNKNOWN_182                   |
| 183    | 0xB7 | 4    |                                              | UNKNOWN_183                   |
| 184    | 0xB8 | 4    | FUN_001f8280                                 | UNKNOWN_184                   |
| 185    | 0xB9 | 1    | FUN_001900b0                                 | UNKNOWN_185                   |
| 186    | 0xBA | -2   | Interfaces::IF_OPENSUB_thunk                 | IF_OPENSUB_THUNK              |
| 187    | 0xBB | -1   | WorldData::SET_WORLD_TARGET                  | SET_WORLD_TARGET              |
| 188    | 0xBC | -2   | ClientState::REBUILD_WORLDENTITY             | UNKNOWN_188                   |
| 189    | 0xBD | 3    | Interfaces::IF_MOVESUB_thunk                 | IF_MOVESUB                    |
| 190    | 0xBE | 15   | FUN_0020b6b0                                 | UNKNOWN_190                   |
| 191    | 0xBF | 2    | FUN_0019b0c0                                 | UNKNOWN_191                   |
| 192    | 0xC0 | 29   |                                              | UNKNOWN_192                   |
| 193    | 0xC1 | 5    |                                              | UNKNOWN_193                   |
| 194    | 0xC2 | 3    | FUN_001f9550                                 | UNKNOWN_194                   |
| 195    | 0xC3 | 0    | ClientState::TRIGGER_ONDIALOGABORT           | TRIGGER_ONDIALOGABORT         |
| 196    | 0xC4 | 21   |                                              | PROJANIM_SPECIFIC             |
| 197    | 0xC5 | 5    |                                              | UNKNOWN_197                   |
| 198    | 0xC6 | 8    |                                              | ANTI_CHEAT_CHALLENGE          |
| 199    | 0xC7 | 28   |                                              | UNKNOWN_199                   |
| 200    | 0xC8 | -1   | Misc::SET_URL_STRING                         | SET_URL_STRING                |
| 201    | 0xC9 | 3    | FUN_0018ffa0                                 | UNKNOWN_201                   |
| 202    | 0xCA | 3    | FUN_001f7770                                 | UNKNOWN_202                   |
| 203    | 0xCB | 4    |                                              | UNKNOWN_203                   |
| 204    | 0xCC | 3    | FUN_001f87b0                                 | UNKNOWN_204                   |
| 205    | 0xCD | -2   | NPCInfo::NPC_INFO_thunk                      | NPC_INFO_THUNK                |
| 206    | 0xCE | 33   | FUN_00194140                                 | UNKNOWN_206                   |
| 207    | 0xCF | -2   | PlayerGroup::UPDATE_PLAYER_GROUP             | PLAYER_GROUP_DELTA            |
| 208    | 0xD0 | 14   |                                              | UNKNOWN_208                   |
| 209    | 0xD1 | 0    | Misc::LOGOUT_TRANSFER                        | LOGOUT_TRANSFER               |
| 210    | 0xD2 | 9    |                                              | UNKNOWN_210                   |
| 211    | 0xD3 | -1   | Social::UPDATE_IGNORELIST_thunk              | UPDATE_IGNORELIST             |
| 212    | 0xD4 | -2   | FUN_00229290                                 | UNKNOWN_212                   |
| 213    | 0xD5 | 1    | FUN_00219c80                                 | UNKNOWN_213                   |
| 214    | 0xD6 | -1   | Misc::UPDATE_URL_STRING                      | UPDATE_URL_STRING             |
| 215    | 0xD7 | 6    | FUN_001ba850                                 | UNKNOWN_215                   |
| 216    | 0xD8 | 0    | FUN_0021a6d0                                 | NO_TIMEOUT                    |
| 217    | 0xD9 | 1    | Chat::SET_CHAT_FILTER_A                      | SET_CHAT_FILTER_A             |

## New Handler Identifications in 947-3

The following opcodes gained new handler name identifications in 947-3 that were unknown in 947-1:

| Opcode | Handler                          | Notes                          |
|--------|----------------------------------|--------------------------------|
| 21     | Chat::MESSAGE_FRIENDCHAT         | Was UNKNOWN_21 in 947-1        |
| 188    | ClientState::REBUILD_WORLDENTITY | Was UNKNOWN_188 in 947-1       |
| 91     | Misc::CUTSCENE_DATA              | Was just "CUTSCENE" in 947-1   |

## Size Legend
- Positive number = fixed size in bytes
- -1 = VarByte (1 byte length prefix)
- -2 = VarShort (2 byte length prefix)
