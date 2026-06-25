# ServerProt opcode table — jag::ServerProt::RegisterAll @0xc4700 (DEFINITIVE, binary-derived)

InitEntry(entry,opcode,size): size>=0 fixed bytes; -1=var-byte(g1 len); -2=var-short(g2 len). slot=ServerProt entry obj addr.

| op | size | kind | entry slot | capture name | cap sizes | ok |
|--:|--:|---|---|---|---|---|
| 0 | -1 | var-byte | DAT_013a1ce0 |  |  |  |
| 1 | -1 | var-byte | DAT_013a1ca0 | SetNpcOp | [0] | (var) |
| 2 | -1 | var-byte | DAT_013a1c60 |  |  |  |
| 3 | 19 | fixed | DAT_015c0c00 | IfSetTopLevelInterface | [19] | OK |
| 4 | 32 | fixed | DAT_015c0b00 |  |  |  |
| 5 | 0 | fixed | DAT_015c1700 | ResetClientVarcache | [0] | OK |
| 6 | 7 | fixed | DAT_015c1e40 |  |  |  |
| 7 | 0 | fixed | DAT_013a1c20 | ResetEntityLists | [0] | OK |
| 8 | 5 | fixed | DAT_015c0440 |  |  |  |
| 9 | -2 | var-short | DAT_013a1be0 |  |  |  |
| 10 | 3 | fixed | DAT_015c1600 |  |  |  |
| 11 | -2 | var-short | DAT_013a1ba0 |  |  |  |
| 12 | 2 | fixed | DAT_015c0e80 | SetPlayerOp2 | [2] | OK |
| 13 | 1 | fixed | DAT_015c0ec0 | SetPlayerOp3 | [1] | OK |
| 14 | 8 | fixed | DAT_015c0580 |  |  |  |
| 15 | 11 | fixed | DAT_013a1b60 |  |  |  |
| 16 | 2 | fixed | DAT_015c1ec0 | LocDel | [2] | OK |
| 17 | -1 | var-byte | DAT_015c0f00 | SetPlayerOp | [9, 11, 12, 15] | (var) |
| 18 | 1 | fixed | DAT_013a1b20 |  |  |  |
| 19 | -2 | var-short | DAT_013a1ae0 |  |  |  |
| 20 | 3 | fixed | DAT_013a1aa0 |  |  |  |
| 21 | 10 | fixed | DAT_015c1a40 |  |  |  |
| 22 | -2 | var-short | DAT_015c0f40 | PlayerInfo | [19, 20, 21, 24, 35, 36, 38, 44, 50, 52, 54, 55, 60, 62, 64, 65, 66, 67, 68, 72, 73, 78, 79, 80, 82, 99, 105, 114, 122, 132, 147, 160, 176, 179, 180, 183, 195, 297, 677] | (var) |
| 23 | -2 | var-short | DAT_013a1a60 |  |  |  |
| 24 | -1 | var-byte | DAT_015c1300 |  |  |  |
| 25 | -2 | var-short | DAT_013a1a20 |  |  |  |
| 26 | -2 | var-short | DAT_013a19e0 | FriendStatus | [0] | (var) |
| 27 | 10 | fixed | DAT_013a19a0 |  |  |  |
| 28 | 6 | fixed | DAT_015c1680 | VarpLarge | [6] | OK |
| 29 | -2 | var-short | DAT_013a1960 |  |  |  |
| 30 | 8 | fixed | DAT_015c0900 | IfSet2DAngle | [8] | OK |
| 31 | 6 | fixed | DAT_013a1920 |  |  |  |
| 32 | 8 | fixed | DAT_015c0740 |  |  |  |
| 33 | -1 | var-byte | DAT_013a18e0 |  |  |  |
| 34 | 8 | fixed | DAT_013a18a0 |  |  |  |
| 35 | 12 | fixed | DAT_015c0a00 | IfSetEvents | [12] | OK |
| 36 | 10 | fixed | DAT_013a1860 |  |  |  |
| 37 | -1 | var-byte | DAT_013a1820 |  |  |  |
| 38 | 8 | fixed | DAT_015c08c0 |  |  |  |
| 39 | 6 | fixed | DAT_015c0c40 |  |  |  |
| 40 | 8 | fixed | DAT_015c0a40 |  |  |  |
| 41 | 3 | fixed | DAT_015c2080 |  |  |  |
| 42 | 10 | fixed | DAT_013a17e0 |  |  |  |
| 43 | 12 | fixed | DAT_013a17a0 |  |  |  |
| 44 | 6 | fixed | DAT_015c12c0 | UpdateStat | [6] | OK |
| 45 | 1 | fixed | DAT_013a1760 | SetMultiwayState | [1] | OK |
| 46 | 5 | fixed | DAT_015c1c40 | ObjAdd | [5] | OK |
| 47 | 3 | fixed | DAT_015c1580 | ClientSetVarcSmall | [3] | OK |
| 48 | 3 | fixed | DAT_015c14c0 |  |  |  |
| 49 | -2 | var-short | DAT_013a1720 | ChangeLobby | [0] | (var) |
| 50 | -1 | var-byte | DAT_015c1f40 |  |  |  |
| 51 | 6 | fixed | DAT_015c15c0 |  |  |  |
| 52 | -2 | var-short | DAT_013a16e0 | NpcInfo | [28, 29, 32, 33, 34, 35, 36, 39, 44, 50, 51, 52, 61, 65, 69, 73, 74, 76, 79, 80, 83, 86, 87, 89, 90, 91, 92, 94, 98, 104, 106, 109, 111, 115, 120, 129, 155, 164, 167, 703, 707, 803] | (var) |
| 53 | -2 | var-short | DAT_013a16a0 |  |  |  |
| 54 | -1 | var-byte | DAT_013a1660 | HashedWorldToken | [44] | (var) |
| 55 | 0 | fixed | DAT_015c1380 | DestroyZoneData | [0] | OK |
| 56 | -1 | var-byte | DAT_013a1620 |  |  |  |
| 57 | 6 | fixed | DAT_013a15e0 |  |  |  |
| 58 | 0 | fixed | DAT_015c1340 |  |  |  |
| 59 | 10 | fixed | DAT_015c0600 |  |  |  |
| 60 | 25 | fixed | DAT_015c0b40 |  |  |  |
| 61 | 3 | fixed | DAT_015c16c0 | VarpSmall | [3] | OK |
| 62 | 4 | fixed | DAT_015c0a80 | IfCloseSub | [4] | OK |
| 63 | 10 | fixed | DAT_013a15a0 |  |  |  |
| 64 | 6 | fixed | DAT_015c1540 | ClientSetVarcLarge | [6] | OK |
| 65 | 20 | fixed | DAT_015c1dc0 |  |  |  |
| 66 | 5 | fixed | DAT_013a1560 |  |  |  |
| 67 | -2 | var-short | DAT_013a1520 | ClanChannelFull | [0] | (var) |
| 68 | 10 | fixed | DAT_015c03c0 |  |  |  |
| 69 | 6 | fixed | DAT_015c1480 |  |  |  |
| 70 | 25 | fixed | DAT_015c0b80 |  |  |  |
| 71 | 7 | fixed | DAT_015c1ac0 |  |  |  |
| 72 | 1 | fixed | DAT_013a14e0 |  |  |  |
| 73 | 2 | fixed | DAT_013a14a0 | MinimapState | [2] | OK |
| 74 | 4 | fixed | DAT_015c0f80 | JcoinsUpdate | [4] | OK |
| 75 | 0 | fixed | DAT_013a1460 | SetReadyFlag | [0] | OK |
| 76 | -2 | var-short | DAT_015c2000 | UpdateZonePartialEnclosed | [6, 7, 10, 15, 21, 24, 27, 33, 34, 39] | (var) |
| 77 | -2 | var-short | DAT_013a1420 | CAMERA_UPDATE | [121] | (var) |
| 78 | 3 | fixed | DAT_015c2040 | UpdateZoneFullFollowsV2 | [3] | OK |
| 79 | -1 | var-byte | DAT_013a13e0 |  |  |  |
| 80 | 1 | fixed | DAT_013a13a0 | UpdateRunenergy | [1] | OK |
| 81 | -2 | var-short | DAT_013a1360 | RebuildNormalSimple | [5137] | (var) |
| 82 | 23 | fixed | DAT_015c0bc0 | IfSetPosition | [23] | OK |
| 83 | -2 | var-short | DAT_013a1320 |  |  |  |
| 84 | 10 | fixed | DAT_015c0880 |  |  |  |
| 85 | -2 | var-short | DAT_013a12e0 | UPDATE_INV_FULL | [5, 9, 12, 17, 21, 35] | (var) |
| 86 | 10 | fixed | DAT_015c06c0 |  |  |  |
| 87 | 0 | fixed | DAT_013a12a0 |  |  |  |
| 88 | 4 | fixed | DAT_013a1260 |  |  |  |
| 89 | 19 | fixed | DAT_013a1220 |  |  |  |
| 90 | -1 | var-byte | DAT_015c1fc0 | LocAdd | [6] | (var) |
| 91 | 5 | fixed | DAT_015c0940 | IfSetHide | [5] | OK |
| 92 | -1 | var-byte | DAT_015c1440 | ClientSetVarcStr | [3, 27, 40] | (var) |
| 93 | -1 | var-byte | DAT_013a11e0 | GameMessage | [73] | (var) |
| 94 | 8 | fixed | DAT_015c0c80 |  |  |  |
| 95 | 5 | fixed | DAT_013a11a0 | MidiSong | [5] | OK |
| 96 | 4 | fixed | DAT_015c0700 |  |  |  |
| 97 | 10 | fixed | DAT_015c09c0 |  |  |  |
| 98 | 25 | fixed | DAT_013a1160 |  |  |  |
| 99 | 6 | fixed | DAT_015c0540 |  |  |  |
| 100 | 4 | fixed | DAT_013a1120 |  |  |  |
| 101 | 4 | fixed | DAT_015c0840 |  |  |  |
| 102 | 8 | fixed | DAT_015c07c0 |  |  |  |
| 103 | 8 | fixed | DAT_015c0780 |  |  |  |
| 104 | 14 | fixed | DAT_013a10e0 | PlayerInfoDecode | [14] | OK |
| 105 | -1 | var-byte | DAT_013a10a0 |  |  |  |
| 106 | 1 | fixed | DAT_013a1060 |  |  |  |
| 107 | 3 | fixed | DAT_015c1bc0 |  |  |  |
| 108 | -2 | var-short | DAT_013a1020 |  |  |  |
| 109 | 28 | fixed | DAT_015c0e40 |  |  |  |
| 110 | -2 | var-short | DAT_013a0fe0 | RunClientScript | [5, 10, 14, 15, 30, 35, 37, 158, 167, 186, 200, 213, 215, 223, 236, 246, 249, 292] | (var) |
| 111 | 6 | fixed | DAT_013a0fa0 |  |  |  |
| 112 | 2 | fixed | DAT_013a0f60 |  |  |  |
| 113 | 11 | fixed | DAT_015c1cc0 |  |  |  |
| 114 | -1 | var-byte | DAT_013a0f20 |  |  |  |
| 115 | 10 | fixed | DAT_015c0680 |  |  |  |
| 116 | -2 | var-short | DAT_015c1400 |  |  |  |
| 117 | -1 | var-byte | DAT_013a0ee0 |  |  |  |
| 118 | 29 | fixed | DAT_015c0ac0 |  |  |  |
| 119 | 35 | fixed | DAT_013a0ea0 | CutsceneData | [35] | OK |
| 120 | 0 | fixed | DAT_013a0e60 | CAM_SMOOTHRESET | [0] | OK |
| 121 | -2 | var-short | DAT_013a0e20 | UPDATE_INV_PARTIAL | [11] | (var) |
| 122 | -2 | var-short | DAT_015c0980 | IfSetText | [5, 14, 18, 36, 205] | (var) |
| 123 | 0 | fixed | DAT_015c0400 |  |  |  |
| 124 | -2 | var-short | DAT_013a0de0 |  |  |  |
| 125 | 7 | fixed | DAT_015c1b40 |  |  |  |
| 126 | -1 | var-byte | DAT_013a0da0 |  |  |  |
| 127 | 0 | fixed | DAT_013a0d60 |  |  |  |
| 128 | 0 | fixed | DAT_013a0d20 | NoopVarA | [0] | OK |
| 129 | 3 | fixed | DAT_013a0ce0 |  |  |  |
| 130 | -1 | var-byte | DAT_013a0ca0 | UpdateIgnoreListRaw | [10] | (var) |
| 131 | 3 | fixed | DAT_015c1000 |  |  |  |
| 132 | 2 | fixed | DAT_013a0c60 |  |  |  |
| 133 | 1 | fixed | DAT_013a0c20 |  |  |  |
| 134 | -2 | var-short | DAT_013a0be0 |  |  |  |
| 135 | -2 | var-short | DAT_013a0ba0 |  |  |  |
| 136 | 5 | fixed | DAT_015c0640 |  |  |  |
| 137 | 1 | fixed | DAT_013a0b60 |  |  |  |
| 138 | 2 | fixed | DAT_013a0b20 |  |  |  |
| 139 | 0 | fixed | DAT_013a0ae0 |  |  |  |
| 140 | 17 | fixed | DAT_013a0aa0 |  |  |  |
| 141 | -2 | var-short | DAT_013a0a60 |  |  |  |
| 142 | -1 | var-byte | DAT_013a0a20 |  |  |  |
| 143 | 6 | fixed | DAT_015c1180 |  |  |  |
| 144 | 2 | fixed | DAT_015c1140 |  |  |  |
| 145 | 2 | fixed | DAT_013a09e0 |  |  |  |
| 146 | 2 | fixed | DAT_015c1040 |  |  |  |
| 147 | 10 | fixed | DAT_015c1640 | VarpLong | [10] | OK |
| 148 | 2 | fixed | DAT_015c0380 |  |  |  |
| 149 | 6 | fixed | DAT_015c1080 |  |  |  |
| 150 | 3 | fixed | DAT_013a09a0 |  |  |  |
| 151 | 21 | fixed | DAT_015c1d40 |  |  |  |
| 152 | -1 | var-byte | DAT_013a0960 |  |  |  |
| 153 | 4 | fixed | DAT_013a0920 |  |  |  |
| 154 | 5 | fixed | DAT_013a08e0 | EntityAnimAtTile | [5] | OK |
| 155 | 0 | fixed | DAT_013a08a0 |  |  |  |
| 156 | 1 | fixed | DAT_013a0860 |  |  |  |
| 157 | 1 | fixed | DAT_013a0820 | SceneFlag | [1] | OK |
| 158 | 9 | fixed | DAT_015c04c0 |  |  |  |
| 159 | -2 | var-short | DAT_013a07e0 |  |  |  |
| 160 | 9 | fixed | DAT_013a07a0 |  |  |  |
| 161 | -2 | var-short | DAT_013a0760 |  |  |  |
| 162 | 0 | fixed | DAT_013a0720 | TRIGGER_ONDIALOGABORT | [0] | OK |
| 163 | 15 | fixed | DAT_013a06e0 |  |  |  |
| 164 | 28 | fixed | DAT_015c1880 |  |  |  |
| 165 | 14 | fixed | DAT_015c05c0 |  |  |  |
| 166 | 5 | fixed | DAT_015c1200 |  |  |  |
| 167 | 12 | fixed | DAT_013a06a0 |  |  |  |
| 168 | -1 | var-byte | DAT_015c1900 |  |  |  |
| 169 | 8 | fixed | DAT_013a0660 |  |  |  |
| 170 | 5 | fixed | DAT_015c1980 |  |  |  |
| 171 | 3 | fixed | DAT_015c0fc0 |  |  |  |
| 172 | 1 | fixed | DAT_015c0dc0 | MinimapFlagA | [1] | OK |
| 173 | -2 | var-short | DAT_013a0620 |  |  |  |
| 174 | 8 | fixed | DAT_013a05e0 | AntiCheatChallenge | [8] | OK |
| 175 | -1 | var-byte | DAT_013a05a0 |  |  |  |
| 176 | 3 | fixed | DAT_015c0e00 |  |  |  |
| 177 | 29 | fixed | DAT_015c1800 |  |  |  |
| 178 | -2 | var-short | DAT_015c0d40 |  |  |  |
| 179 | 9 | fixed | DAT_015c0500 |  |  |  |
| 180 | 5 | fixed | DAT_015c0800 |  |  |  |
| 181 | 4 | fixed | DAT_013a0560 |  |  |  |
| 182 | 3 | fixed | DAT_013a0520 |  |  |  |
| 183 | 14 | fixed | DAT_015c1780 |  |  |  |
| 184 | 4 | fixed | DAT_015c0cc0 |  |  |  |
| 185 | -1 | var-byte | DAT_013a04e0 |  |  |  |
| 186 | -2 | var-short | DAT_015c1240 |  |  |  |
| 187 | 1 | fixed | DAT_013a04a0 |  |  |  |
| 188 | -2 | var-short | DAT_013a0460 |  |  |  |
| 189 | 4 | fixed | DAT_013a0420 |  |  |  |
| 190 | 0 | fixed | DAT_015c13c0 | ClearPendingUpdates | [0] | OK |
| 191 | 4 | fixed | DAT_013a03e0 |  |  |  |
| 192 | 1 | fixed | DAT_013a03a0 |  |  |  |
| 193 | 1 | fixed | DAT_015c0d00 |  |  |  |
| 194 | 1 | fixed | DAT_015c11c0 |  |  |  |
| 195 | 4 | fixed | DAT_013a0360 |  |  |  |
| 196 | 10 | fixed | DAT_015c1500 |  |  |  |
| 197 | -2 | var-short | DAT_013a0320 |  |  |  |
| 198 | -1 | var-byte | DAT_013a02e0 |  |  |  |
| 199 | -2 | var-short | DAT_015c1280 | RebuildRegion | [93] | (var) |
| 200 | 2 | fixed | DAT_013a02a0 |  |  |  |
| 201 | 3 | fixed | DAT_015c1100 |  |  |  |
| 202 | -2 | var-short | DAT_013a0260 |  |  |  |
| 203 | 3 | fixed | DAT_013a0220 |  |  |  |
| 204 | 1 | fixed | DAT_015c0d80 | MinimapFlagB | [1] | OK |
| 205 | 6 | fixed | DAT_013a01e0 |  |  |  |
| 206 | 5 | fixed | DAT_015c0480 |  |  |  |
| 207 | 3 | fixed | DAT_015c10c0 |  |  |  |
| 208 | -2 | var-short | DAT_013a01a0 |  |  |  |
| 209 | -2 | var-short | DAT_013a0160 | NpcInfoThunk | [0] | (var) |
| 210 | 6 | fixed | DAT_013a0120 |  |  |  |
| 211 | 33 | fixed | DAT_013a00e0 |  |  |  |
| 212 | -1 | var-byte | DAT_013a00a0 |  |  |  |
| 213 | -1 | var-byte | DAT_013a0060 |  |  |  |
| 214 | 0 | fixed | DAT_013a0020 |  |  |  |
| 215 | 8 | fixed | DAT_0139ffe0 |  |  |  |
| 216 | -2 | var-short | DAT_0139ffa0 | WorldListPacket | [430, 595, 3001] | (var) |
| 217 | 4 | fixed | DAT_0139ff60 |  |  |  |

**218 opcodes registered. Fixed-size validation vs capture: 37 OK, 0 mismatch.**
