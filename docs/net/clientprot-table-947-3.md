# ClientProt Opcode Table - Rev 947-3

Extracted from rs2client.947-3 binary via Ghidra.
- RegisterAll at `0x00181e10` (ClientProt::ClientProt calls)
- Total entries: **130** (unchanged from 947-1)
- Size changes from 947-1: **NONE** (all 130 opcodes have identical sizes)

## Complete Table

| Opcode | Hex  | Size | Name (947-1)                  |
|--------|------|------|-------------------------------|
| 0      | 0x00 | 5    | MOVE_GAME_MINIMENU            |
| 1      | 0x01 | 3    | UNKNOWN_1                     |
| 2      | 0x02 | 4    | UNKNOWN_2                     |
| 3      | 0x03 | 5    | MOVE_SCRIPTED                 |
| 4      | 0x04 | 8    | IF_BUTTON3                    |
| 5      | 0x05 | 8    | IF_BUTTON7                    |
| 6      | 0x06 | 15   | OPNPC_T_EXTENDED              |
| 7      | 0x07 | 6    | EVENT_MOUSE_CLICK             |
| 8      | 0x08 | 4    | UNKNOWN_8                     |
| 9      | 0x09 | 12   | UNKNOWN_9                     |
| 10     | 0x0A | 9    | UNKNOWN_10                    |
| 11     | 0x0B | 9    | UNKNOWN_11                    |
| 12     | 0x0C | -1   | UNKNOWN_12                    |
| 13     | 0x0D | 16   | UNKNOWN_13                    |
| 14     | 0x0E | -2   | UNKNOWN_14                    |
| 15     | 0x0F | 6    | UNKNOWN_15                    |
| 16     | 0x10 | 8    | UNKNOWN_16                    |
| 17     | 0x11 | 7    | UNKNOWN_17                    |
| 18     | 0x12 | 8    | IF_BUTTON9                    |
| 19     | 0x13 | -1   | UNKNOWN_19                    |
| 20     | 0x14 | 1    | UNKNOWN_20                    |
| 21     | 0x15 | 8    | IF_BUTTON8                    |
| 22     | 0x16 | -1   | FRIENDLIST_DEL                |
| 23     | 0x17 | 4    | WORLDLIST_FETCH               |
| 24     | 0x18 | -1   | CLANCHANNEL_KICKUSER          |
| 25     | 0x19 | -1   | UNKNOWN_25                    |
| 26     | 0x1A | -1   | UNKNOWN_26                    |
| 27     | 0x1B | 0    | NO_TIMEOUT                    |
| 28     | 0x1C | 18   | MOVE_GAME_EXTENDED            |
| 29     | 0x1D | 8    | IF_BUTTON5                    |
| 30     | 0x1E | 4    | TRANSMITVAR_VERIFYID          |
| 31     | 0x1F | -1   | UNKNOWN_31                    |
| 32     | 0x20 | 4    | UNKNOWN_32                    |
| 33     | 0x21 | 3    | UNKNOWN_33                    |
| 34     | 0x22 | -1   | CLIENT_DETAILOPTIONS_STATUS   |
| 35     | 0x23 | -1   | UNKNOWN_35                    |
| 36     | 0x24 | 8    | IF_BUTTON10                   |
| 37     | 0x25 | 1    | UNKNOWN_37                    |
| 38     | 0x26 | 1    | UNKNOWN_38                    |
| 39     | 0x27 | -2   | UNKNOWN_39                    |
| 40     | 0x28 | 16   | UNKNOWN_40                    |
| 41     | 0x29 | 0    | UNKNOWN_41                    |
| 42     | 0x2A | -1   | UNKNOWN_42                    |
| 43     | 0x2B | 0    | UNKNOWN_43                    |
| 44     | 0x2C | 7    | UNKNOWN_44                    |
| 45     | 0x2D | 9    | UNKNOWN_45                    |
| 46     | 0x2E | 3    | UNKNOWN_46                    |
| 47     | 0x2F | 4    | EVENT_CAMERA_POSITION         |
| 48     | 0x30 | -1   | IGNORELIST_ADD                |
| 49     | 0x31 | 17   | UNKNOWN_49                    |
| 50     | 0x32 | 3    | UNKNOWN_50                    |
| 51     | 0x33 | 8    | IF_BUTTON6                    |
| 52     | 0x34 | 1    | UNKNOWN_52                    |
| 53     | 0x35 | -2   | EVENT_KEYBOARD                |
| 54     | 0x36 | 3    | UNKNOWN_54                    |
| 55     | 0x37 | 0    | MAP_BUILD_COMPLETE            |
| 56     | 0x38 | 1    | EVENT_APPLET_FOCUS            |
| 57     | 0x39 | 1    | UNKNOWN_57                    |
| 58     | 0x3A | 15   | OPLOC_T_EXTENDED              |
| 59     | 0x3B | 4    | DETECT_MODIFIED_CLIENT        |
| 60     | 0x3C | 3    | UNKNOWN_60                    |
| 61     | 0x3D | 4    | UNKNOWN_61                    |
| 62     | 0x3E | 2    | UNKNOWN_62                    |
| 63     | 0x3F | 4    | UNKNOWN_63                    |
| 64     | 0x40 | -1   | UNKNOWN_64                    |
| 65     | 0x41 | -1   | UNKNOWN_65                    |
| 66     | 0x42 | -1   | UNKNOWN_66                    |
| 67     | 0x43 | 7    | UNKNOWN_67                    |
| 68     | 0x44 | -2   | UNKNOWN_68                    |
| 69     | 0x45 | -1   | MESSAGE_PUBLIC_EFFECTS         |
| 70     | 0x46 | -2   | UNKNOWN_70                    |
| 71     | 0x47 | 0    | UNKNOWN_71                    |
| 72     | 0x48 | 9    | UNKNOWN_72                    |
| 73     | 0x49 | 9    | UNKNOWN_73                    |
| 74     | 0x4A | -2   | EVENT_TELEMETRY               |
| 75     | 0x4B | -1   | UNKNOWN_75                    |
| 76     | 0x4C | -2   | UNKNOWN_76                    |
| 77     | 0x4D | 8    | IF_BUTTON2                    |
| 78     | 0x4E | -2   | MOVE_GAME                     |
| 79     | 0x4F | 9    | UNKNOWN_79                    |
| 80     | 0x50 | -1   | UNKNOWN_80                    |
| 81     | 0x51 | 11   | OPLOC_T2                      |
| 82     | 0x52 | 4    | UNKNOWN_82                    |
| 83     | 0x53 | 18   | UNKNOWN_83                    |
| 84     | 0x54 | -1   | RESUME_P_NAMEDIALOG           |
| 85     | 0x55 | 11   | OPNPC_T2_EXTENDED             |
| 86     | 0x56 | -1   | UNKNOWN_86                    |
| 87     | 0x57 | -1   | UNKNOWN_87                    |
| 88     | 0x58 | 3    | UNKNOWN_88                    |
| 89     | 0x59 | 3    | UNKNOWN_89                    |
| 90     | 0x5A | -2   | UNKNOWN_90                    |
| 91     | 0x5B | 2    | UNKNOWN_91                    |
| 92     | 0x5C | 22   | UNKNOWN_92                    |
| 93     | 0x5D | -1   | FRIENDLIST_ADD                |
| 94     | 0x5E | -2   | UNKNOWN_94                    |
| 95     | 0x5F | 8    | IF_BUTTON4                    |
| 96     | 0x60 | 8    | IF_BUTTON1                    |
| 97     | 0x61 | 9    | ANTI_CHEAT_REPLY              |
| 98     | 0x62 | 7    | UNKNOWN_98                    |
| 99     | 0x63 | 2    | UNKNOWN_99                    |
| 100    | 0x64 | -2   | UNKNOWN_100                   |
| 101    | 0x65 | 2    | UNKNOWN_101                   |
| 102    | 0x66 | 7    | UNKNOWN_102                   |
| 103    | 0x67 | -1   | UNKNOWN_103                   |
| 104    | 0x68 | 3    | UNKNOWN_104                   |
| 105    | 0x69 | -1   | EVENT_MOUSE_MOVE              |
| 106    | 0x6A | 4    | UNKNOWN_106                   |
| 107    | 0x6B | 3    | UNKNOWN_107                   |
| 108    | 0x6C | 6    | WINDOW_STATUS                 |
| 109    | 0x6D | 9    | UNKNOWN_109                   |
| 110    | 0x6E | 3    | UNKNOWN_110                   |
| 111    | 0x6F | -1   | UNKNOWN_111                   |
| 112    | 0x70 | 11   | OPLOC_T3                      |
| 113    | 0x71 | 3    | UNKNOWN_113                   |
| 114    | 0x72 | 3    | UNKNOWN_114                   |
| 115    | 0x73 | 3    | UNKNOWN_115                   |
| 116    | 0x74 | -2   | UNKNOWN_116                   |
| 117    | 0x75 | -1   | UNKNOWN_117                   |
| 118    | 0x76 | 7    | UNKNOWN_118                   |
| 119    | 0x77 | -1   | UNKNOWN_119                   |
| 120    | 0x78 | -1   | MESSAGE_PUBLIC                |
| 121    | 0x79 | -2   | MESSAGE_PRIVATE               |
| 122    | 0x7A | 7    | UNKNOWN_122                   |
| 123    | 0x7B | 3    | UNKNOWN_123                   |
| 124    | 0x7C | -1   | IF_BUTTON_D                   |
| 125    | 0x7D | 3    | UNKNOWN_125                   |
| 126    | 0x7E | 9    | UNKNOWN_126                   |
| 127    | 0x7F | 3    | UNKNOWN_127                   |
| 128    | 0x80 | 0    | UNKNOWN_128                   |
| 129    | 0x81 | 3    | UNKNOWN_129                   |

## Send Function Address Mapping (947-3)

| Function                    | Address      | ClientProt DAT    | Opcode |
|-----------------------------|--------------|-------------------|--------|
| SendWorldlistFetch          | 0x0023f340   | DAT_016edc10      | 23     |
| SendFriendlistAdd           | 0x003e2e80   | DAT_016ed7b0      | 93     |
| SendFriendlistDel           | 0x003e3650   | (to verify)       | 22     |
| SendIgnorelistAdd           | 0x003b6bf0   | (to verify)       | 48     |
| SendMessagePublic           | 0x003ab740   | (to verify)       | 120    |
| SendMessagePrivate          | 0x003ae110   | (to verify)       | 121    |

## Size Legend
- Positive number = fixed size in bytes
- -1 = VarByte (1 byte length prefix)
- -2 = VarShort (2 byte length prefix)
