# ServerProt Master Opcode Table (Build 946-3)

Complete table of all 216 ServerProt opcodes (0x01–0xD8). All 216 are registered in `RegisterAll`.
129 handlers are bound in `BindHandlers`; the remaining 87 are bound by category constructors
(Interfaces, ZoneUpdates, Variables, ClientState) or remain unbound (5 opcodes with no handler).

Size: positive = fixed bytes, `var_byte` = variable 1-byte length prefix, `var_short` = variable 2-byte BE length prefix, `0` = no payload.

Handler addresses are the actual packet handler function, not the manager wrapper.

Sources: `jag::ServerProt::RegisterAll` (0x00182860), `jag::ServerProt::BindHandlers` (0x0011852a),
`jag::packethandlers::Variables` (0x0014ce1e), `jag::packethandlers::ZoneUpdates` (0x0014c4c8),
`jag::packethandlers::Interfaces`, `jag::packethandlers::ClientState`,
and handler documentation in `docs/net/serverprot/`.

| Opcode | Hex | Size | Name | Category | Handler Address |
|--------|-----|------|------|----------|-----------------|
|   1 | 0x01 | var_byte | MESSAGE_GAME | Chat | `0x001e6640` |
|   2 | 0x02 | var_short | SET_VARC_INT | Variables | `0x001b97d0` |
|   3 | 0x03 | 7 | OBJ_COUNT | ZoneUpdates | `0x001b9410` |
|   4 | 0x04 | 10 | IF_SETHIDE | Interfaces | `0x0027e8a0` |
|   5 | 0x05 | 10 | MIDI_JINGLE | Audio | `0x00191af0` |
|   6 | 0x06 | 10 | LOC_ANIM_SPECIFIC | ZoneUpdates | `0x00192ac0` |
|   7 | 0x07 | 29 | IF_SETMODEL | Interfaces | `0x0027dd10` |
|   8 | 0x08 | 6 | IF_MOVESUB | Interfaces | `0x0027ce10` |
|   9 | 0x09 | var_byte | SET_NPC_OP | NpcInfo | `0x00219010` |
|  10 | 0x0A | var_byte | MESSAGE_QUICKCHAT_CLANCHAT | Chat | `0x001ed5a0` |
|  11 | 0x0B | var_short | CLANCHANNEL_FULL_CHAT | Chat | `0x001be8b0` |
|  12 | 0x0C | 6 | SET_VARC_SMALL | Variables | `0x001b98c0` |
|  13 | 0x0D | 1 | SET_PLAYER_OP_3 | Misc | `0x002124a0` |
|  14 | 0x0E | 3 | SET_VARP_SMALL | Variables | `0x001b9c70` |
|  15 | 0x0F | var_short | MESSAGE_PUBLIC | Chat | `0x001ee310` |
|  16 | 0x10 | var_short | RUN_CLIENTSCRIPT | Chat | `0x001e6460` |
|  17 | 0x11 | var_short | UPDATE_IGNORELIST | Social | `0x00235b20` |
|  18 | 0x12 | var_short | UPDATE_SITESETTINGS | SiteSettings | `0x00242840` |
|  19 | 0x13 | 3 | SET_VARC_COORD | Variables | `0x001c2220` |
|  20 | 0x14 | var_short | IF_OPENSUB_ACTIVE | Interfaces | `0x0024ade0` |
|  21 | 0x15 | 32 | IF_SETANGLE | Interfaces | `0x0027e350` |
|  22 | 0x16 | 2 | SET_TICK_TIMER | ClientState | `0x002141c0` |
|  23 | 0x17 | 1 | CAM_TARGET | Camera | `0x001c6a20` |
|  24 | 0x18 | 20 | MAP_PROJANIM | ZoneUpdates | `0x00193e10` |
|  25 | 0x19 | 0 | RESET_ENTITY_LISTS | Misc | `0x001e7480` |
|  26 | 0x1A | 8 | IF_SETOBJECT_NONUM | Interfaces | `0x0027ef90` |
|  27 | 0x1B | 1 | SET_RUN_ENERGY | Misc | `0x00213e30` |
|  28 | 0x1C | var_short | UPDATE_FRIENDLIST_2 | Social | `0x0027a030` |
|  29 | 0x1D | var_byte | CLANSETTINGS_FULL_2 | Clans | `0x0023fab0` |
|  30 | 0x1E | var_byte | LOC_ADD | ZoneUpdates | `0x001e46b0` |
|  31 | 0x1F | var_byte | MESSAGE_QUICKCHAT_CLANCHANNEL | Chat | `0x001ecf90` |
|  32 | 0x20 | 0 | RESET_ALL_VARPS | ClientState | — |
|  33 | 0x21 | 0 | SOUND_STOP_ALL | Audio | `0x00192870` |
|  34 | 0x22 | 5 | SOUND_MIXBUSS_SETLEVEL | Audio | `0x001917e0` |
|  35 | 0x23 | 0 | SET_READY_FLAG | ClientState | `0x00213e90` |
|  36 | 0x24 | var_short | IF_SETPLAYERMODEL_OTHER | Interfaces | `0x00268960` |
|  37 | 0x25 | var_byte | MESSAGE_QUICKCHAT_PRIVATE | Chat | `0x001ec340` |
|  38 | 0x26 | 23 | IF_SETPOSITION | Interfaces | `0x0027db70` |
|  39 | 0x27 | var_short | CLANSETTINGS_FULL | Clans | `0x001911c0` |
|  40 | 0x28 | var_byte | MESSAGE_FRIENDCHAT | Chat | `0x001edc90` |
|  41 | 0x29 | 10 | LOC_ADD_CHANGE | ZoneUpdates | `0x001e4320` |
|  42 | 0x2A | var_short | PLAYER_INFO_DECODE | PlayerInfo | `0x002228f0` |
|  43 | 0x2B | var_short | UPDATE_ZONE_PARTIAL | ZoneUpdates | `0x0018f370` |
|  44 | 0x2C | 6 | CAM_MOVETO | Camera | `0x001ea800` |
|  45 | 0x2D | 5 | IF_SETSCROLLPOS | Interfaces | `0x0027e790` |
|  46 | 0x2E | var_byte | MESSAGE_CLANCHANNEL | Chat | `0x001d70f0` |
|  47 | 0x2F | 14 | MAP_FLAG_SET_PLAYER | PlayerInfo | `0x002224a0` |
|  48 | 0x30 | 5 | IF_SETCOLOUR | Interfaces | `0x0027ea20` |
|  49 | 0x31 | 10 | MIDI_SONG | Audio | `0x00191990` |
|  50 | 0x32 | 4 | IF_SETOBJECT | Interfaces | `0x0027ebf0` |
|  51 | 0x33 | 6 | SET_VARBIT_SMALL | Variables | `0x001b9a30` |
|  52 | 0x34 | 1 | CAM_FORCEANGLE | Camera | `0x001ea570` |
|  53 | 0x35 | 35 | CUTSCENE_DATA | Misc | `0x00212d80` |
|  54 | 0x36 | 25 | PROJANIM | Combat | `0x001930a0` |
|  55 | 0x37 | 4 | CAM_SHAKE | Camera | `0x001eaf30` |
|  56 | 0x38 | var_byte | MESSAGE_PRIVATE_SYSTEM | Chat | `0x001917d0` |
|  57 | 0x39 | var_short | IF_SETTARGETPARAM | Interfaces | `0x0027cd10` |
|  58 | 0x3A | 6 | CAM_LOOKAT | Camera | `0x001eaa90` |
|  59 | 0x3B | 12 | IF_SETTEXT | Interfaces | `0x0027d170` |
|  60 | 0x3C | var_byte | RESET_VARC_SMALL | Variables | `0x001b9730` |
|  61 | 0x3D | 8 | IF_SETTEXTFONT | Interfaces | `0x0027f1f0` |
|  62 | 0x3E | 10 | IF_SETRECOL | Interfaces | `0x0027d440` |
|  63 | 0x3F | var_short | CLANSETTINGS_DELTA | Clans | `0x001c2bc0` |
|  64 | 0x40 | 2 | LOC_DEL | ZoneUpdates | `0x001dc500` |
|  65 | 0x41 | var_byte | UPDATE_ZONE_FULL_FOLLOWS_2 | ClientState | `0x001e42b0` |
|  66 | 0x42 | var_short | FRIENDLIST_LOADED | Social | `0x00225bf0` |
|  67 | 0x43 | 25 | IF_SETMODEL_BODYTYPE | Interfaces | `0x0027d790` |
|  68 | 0x44 | var_byte | MESSAGE_QUICKCHAT_FRIENDCHAT | Chat | `0x001ec9a0` |
|  69 | 0x45 | 0 | CAM_RESET | Camera | `0x001e9a50` |
|  70 | 0x46 | 5 | MIDI_SWAP | Audio | `0x001e4180` |
|  71 | 0x47 | 8 | IF_SETPLAYERMODEL_BASECOLOUR | Interfaces | `0x0027de90` |
|  72 | 0x48 | 3 | SET_VARBIT_INT | Variables | `0x001b9980` |
|  73 | 0x49 | 2 | SET_PLAYER_CHAT_EFFECTS | PlayerInfo | `0x0018cc40` |
|  74 | 0x4A | 8 | IF_SETPLAYERMODEL_BODYTYPE | Interfaces | `0x0027e050` |
|  75 | 0x4B | 10 | IF_SETRETEX | Interfaces | `0x0027d510` |
|  76 | 0x4C | 8 | SOUND_AREA_SYNTH | Audio | `0x0018da50` |
|  77 | 0x4D | 4 | IF_SETMODEL_ANIMATION | Interfaces | `0x0027d860` |
|  78 | 0x4E | 7 | LOC_PREFETCH | ZoneUpdates | `0x001e4520` |
|  79 | 0x4F | 6 | CAM_LOOKAT_ARC | Camera | `0x0018cda0` |
|  80 | 0x50 | 1 | SET_MULTIWAY_STATE | Misc | `0x002129d0` |
|  81 | 0x51 | var_short | UPDATE_INV_PARTIAL | Inventory | `0x00223390` |
|  82 | 0x52 | var_byte | LOC_CUSTOMISE | ZoneUpdates | `0x001dd650` |
|  83 | 0x53 | var_short | CLANCHANNEL_FULL | Clans | `0x00238aa0` |
|  84 | 0x54 | var_short | DETAIL_OPTIONS | Misc | `0x00280210` |
|  85 | 0x55 | 4 | SET_DISPLAY_INT | Misc | `0x00212580` |
|  86 | 0x56 | var_byte | NPC_HEADICON_SPECIFIC | NpcInfo | `0x00219210` |
|  87 | 0x57 | 5 | OBJ_ADD | ZoneUpdates | `0x001b9550` |
|  88 | 0x58 | 3 | *(no handler)* | — | — |
|  89 | 0x59 | 8 | IF_SETCLICKMASK | Interfaces | `0x0027e150` |
|  90 | 0x5A | 3 | UPDATE_ZONE_FULL_FOLLOWS | ZoneUpdates | `0x001989b0` |
|  91 | 0x5B | 12 | SPOTANIM_SPECIFIC | SpotAnim | `0x002060a0` |
|  92 | 0x5C | var_short | *(no handler)* | — | — |
|  93 | 0x5D | 10 | IF_SETPLAYERMODEL | Interfaces | `0x0027e250` |
|  94 | 0x5E | 19 | NPC_HITMARKS_AND_HEADBARS | Combat | `0x001941b0` |
|  95 | 0x5F | var_short | CLANSETTINGS_DELTA_CHAT | Chat | `0x001c2f80` |
|  96 | 0x60 | 4 | CAM_MOVETO_ARC | Camera | `0x001c6220` |
|  97 | 0x61 | 2 | SET_PLAYER_OP_2 | Misc | `0x00212410` |
|  98 | 0x62 | 3 | OBJ_DEL | ZoneUpdates | `0x001fa050` |
|  99 | 0x63 | 0 | *(no handler)* | — | — |
| 100 | 0x64 | 10 | IF_SETNPCMODEL | Interfaces | `0x0027e520` |
| 101 | 0x65 | 3 | REMOVE_TRACKED_ENTRY | Misc | `0x002130b0` |
| 102 | 0x66 | 1 | SET_NPC_UPDATE_ORIGIN | NpcInfo | `0x00191030` |
| 103 | 0x67 | 10 | SET_PLAYER_GROUP | PlayerGroup | `0x00225e80` |
| 104 | 0x68 | 0 | RESET_CLIENT_STATE | ClientState | — |
| 105 | 0x69 | 8 | IF_SETPLAYERMODEL_SELF | Interfaces | `0x0027e620` |
| 106 | 0x6A | 4 | IF_SETANIM | Interfaces | `0x0027f110` |
| 107 | 0x6B | var_byte | CHAT_FILTER_SETTINGS | Chat | `0x00191650` |
| 108 | 0x6C | 6 | IF_SETOBJECT_ALWAYSNUM | Interfaces | `0x0027ed90` |
| 109 | 0x6D | var_short | REBUILD_NORMAL_HANDLER | Rebuild | `0x00198080` |
| 110 | 0x6E | var_short | CAM_UPDATE | Camera | `0x001f7900` |
| 111 | 0x6F | var_byte | NOOP_VAR | Misc | `0x0018cd20` |
| 112 | 0x70 | 0 | UPDATE_STAT | Variables | `0x0018f650` |
| 113 | 0x71 | 10 | IF_SETMODEL_COLOUR | Interfaces | `0x0027d5f0` |
| 114 | 0x72 | 6 | SET_VARC_STR_SMALL | Variables | `0x001f8d20` |
| 115 | 0x73 | 6 | *(no handler)* | — | — |
| 116 | 0x74 | 25 | IF_SETMODEL_RECOLOUR | Interfaces | `0x0027d9b0` |
| 117 | 0x75 | 11 | SOUND_GROUP | Audio | `0x00191c90` |
| 118 | 0x76 | 0 | CAM_SMOOTHRESET | Camera | `0x001ea520` |
| 119 | 0x77 | 8 | IF_SETPLAYERMODEL_ANIM | Interfaces | `0x0027e450` |
| 120 | 0x78 | 3 | UPDATE_ZONE_PARTIAL_FOLLOWS | ZoneUpdates | `0x0018f500` |
| 121 | 0x79 | var_short | UPDATE_INV_FULL | Inventory | `0x00244d60` |
| 122 | 0x7A | 11 | MAP_ANIM | ZoneUpdates | `0x00206a70` |
| 123 | 0x7B | var_byte | CLANCHANNEL_DELTA | Clans | `0x0023fcd0` |
| 124 | 0x7C | 6 | SET_VARP_INT | Variables | `0x001b9bc0` |
| 125 | 0x7D | 8 | IF_SETTEXT2 | Interfaces | `0x0027d2f0` |
| 126 | 0x7E | 19 | IF_SETGRAPHIC | Interfaces | `0x0027c8b0` |
| 127 | 0x7F | 7 | OBJ_REVEAL | ZoneUpdates | `0x00194cd0` |
| 128 | 0x80 | 3 | IF_SETANGLE_ACTIVE | Interfaces | `0x0027e6f0` |
| 129 | 0x81 | 6 | SET_MAP_FLAG | ClientState | `0x001ead00` |
| 130 | 0x82 | var_byte | MESSAGE_PRIVATE | Chat | `0x001eb6c0` |
| 131 | 0x83 | 0 | LOGOUT_TRANSFER | Misc | `0x00260440` |
| 132 | 0x84 | 5 | LOC_MERGE | ZoneUpdates | `0x0018ef50` |
| 133 | 0x85 | 1 | SET_CHAT_FILTER_D | Chat | `0x00212ce0` |
| 134 | 0x86 | 0 | LOGOUT | Misc | `0x00255960` |
| 135 | 0x87 | 1 | SET_WEIGHT | Misc | `0x00212950` |
| 136 | 0x88 | 2 | SOUND_GROUP_STOP | Audio | `0x0018d450` |
| 137 | 0x89 | 9 | IF_SETNPCMODEL_ANIM | Interfaces | `0x0027e710` |
| 138 | 0x8A | 10 | SET_VARP_LONG | Variables | `0x001b9ac0` |
| 139 | 0x8B | 4 | PLAYER_OP | PlayerGroup | `0x00225d60` |
| 140 | 0x8C | 5 | IF_SETOBJECT_NONUM_2 | Interfaces | `0x0027f070` |
| 141 | 0x8D | var_short | UPDATE_ZONE_FULL_FOLLOWS_3 | ZoneUpdates | `0x0024a510` |
| 142 | 0x8E | 1 | MINIMAP_FLAG_SET | ClientState | `0x001e3e20` |
| 143 | 0x8F | 8 | SET_CAMERA_TARGET | WorldData | `0x0021ec60` |
| 144 | 0x90 | 4 | SET_SYSUPDATE_TIMER | Misc | `0x00211e40` |
| 145 | 0x91 | 8 | IF_SETHIDE_ACTIVE | Interfaces | `0x0027fd70` |
| 146 | 0x92 | 0 | NOOP | Misc | `0x00212d40` |
| 147 | 0x93 | 29 | PROJANIM_SPECIFIC_HALT | ZoneUpdates | `0x001933f0` |
| 148 | 0x94 | var_short | UPDATE_PLAYER_CHAT | PlayerInfo | `0x00222340` |
| 149 | 0x95 | var_byte | SET_WORLD_TARGET | WorldData | `0x0021aae0` |
| 150 | 0x96 | var_short | UPDATE_FRIENDCHAT_CHANNEL | Social | `0x0022f660` |
| 151 | 0x97 | 3 | RESET_ANIMS | ClientState | `0x001e41c0` |
| 152 | 0x98 | var_byte | UPDATE_IGNORELIST | Social | `0x00276e20` |
| 153 | 0x99 | 2 | RUNCLIENTSCRIPT | ClientState | `0x001ead30` |
| 154 | 0x9A | var_short | UPDATE_PLAYER_GROUP | PlayerGroup | `0x002384f0` |
| 155 | 0x9B | var_byte | UPDATE_URL_STRING | Misc | `0x001e02a0` |
| 156 | 0x9C | 6 | CLANCHANNEL_DELTA_CS | ClientState | — |
| 157 | 0x9D | 3 | SET_PLAYER_OP | ClientState | `0x001e3af0` |
| 158 | 0x9E | 12 | SYNTH_SOUND | Audio | `0x00191850` |
| 159 | 0x9F | 28 | MAP_PROJANIM_HALT | ZoneUpdates | `0x00193770` |
| 160 | 0xA0 | 6 | SOUND_GROUP_SPEED | Audio | `0x00192470` |
| 161 | 0xA1 | 1 | SET_INTERACTION_FLAG_B | Misc | `0x002122f0` |
| 162 | 0xA2 | var_byte | IF_SETGRAPHIC_ACTIVE | Interfaces | `0x00256100` |
| 163 | 0xA3 | var_short | REBUILD_PLAYERINFO_POSITIONS | PlayerInfo | `0x00222080` |
| 164 | 0xA4 | 1 | SET_CHAT_FILTER_C | Chat | `0x00212b20` |
| 165 | 0xA5 | 9 | NPC_INFO | NpcInfo | `0x00279ce0` |
| 166 | 0xA6 | 0 | MIDI_STOP | Audio | `0x00191c50` |
| 167 | 0xA7 | 2 | SKIP_2_BYTES | Misc | `0x0018d3e0` |
| 168 | 0xA8 | var_short | UPDATE_INV_GROUP | Inventory | `0x00244a00` |
| 169 | 0xA9 | 1 | SET_INTERACTION_FLAG_C | Misc | `0x00212280` |
| 170 | 0xAA | var_short | NOOP_VAR | Misc | `0x00211e00` |
| 171 | 0xAB | var_byte | SET_URL_STRING | Misc | `0x00218f70` |
| 172 | 0xAC | 5 | IF_SETNPCMODEL_ACTIVE | Interfaces | `0x0027f990` |
| 173 | 0xAD | var_short | IF_SETMODEL_ACTIVE | Interfaces | `0x0027c4c0` |
| 174 | 0xAE | var_short | UPDATE_FRIENDLIST | Social | `0x00277a40` |
| 175 | 0xAF | 3 | SET_INTERACTION_FLAG_D | Misc | `0x00212360` |
| 176 | 0xB0 | 14 | IF_SETPLAYERMODEL_EXACTMOVE | Interfaces | `0x0027c9b0` |
| 177 | 0xB1 | var_short | REBUILD_REGION_HANDLER | Rebuild | `0x00198090` |
| 178 | 0xB2 | var_short | REBUILD_REGION | ClientState | `0x001e43a0` |
| 179 | 0xB3 | 2 | FRIENDCHAT_SYSUPDATE | Social | `0x00238140` |
| 180 | 0xB4 | 5 | IF_CLOSESUB | Interfaces | `0x0027c680` |
| 181 | 0xB5 | 1 | SET_NPC_UPDATE_FLAG | NpcInfo | `0x00212880` |
| 182 | 0xB6 | 5 | IF_OPENSUB | Interfaces | `0x0027c5a0` |
| 183 | 0xB7 | 10 | *(no handler)* | — | — |
| 184 | 0xB8 | 4 | SOUND_AREA_SYNTH | Audio | `0x001c1d20` |
| 185 | 0xB9 | 33 | MAP_PROJANIM | ZoneUpdates | `0x00192ca0` |
| 186 | 0xBA | var_short | REBUILD_NORMAL | ClientState | `0x001e4390` |
| 187 | 0xBB | 4 | PLAYER_GROUP_DELTA | PlayerGroup | `0x00225df0` |
| 188 | 0xBC | 1 | SET_CHAT_FILTER_B | Chat | `0x00212c50` |
| 189 | 0xBD | var_short | PLAYER_INFO_DECODE | PlayerInfo | `0x0025b000` |
| 190 | 0xBE | 3 | IF_MOVESUB_ACTIVE | Interfaces | `0x0027cea0` |
| 191 | 0xBF | 2 | TRIGGER_ONDIALOGABORT | ClientState | `0x001e3a00` |
| 192 | 0xC0 | var_short | SKIP_DATA | Misc | `0x00212700` |
| 193 | 0xC1 | 15 | MAP_FLAG_SET | ClientState | `0x00205ac0` |
| 194 | 0xC2 | 14 | MAP_ANIM_SPECIFIC | ZoneUpdates | `0x002065e0` |
| 195 | 0xC3 | 3 | IF_SETPOSITION_ACTIVE | Interfaces | `0x0027d930` |
| 196 | 0xC4 | var_short | OCULUS_SYNC | Camera | `0x0018cd30` |
| 197 | 0xC5 | 3 | IF_SETCLICKMASK_ACTIVE | Interfaces | `0x0027c910` |
| 198 | 0xC6 | var_byte | SOUND_AREA | ZoneUpdates | `0x001cc090` |
| 199 | 0xC7 | var_byte | FRIENDCHAT_JOIN | Chat | `0x001c1470` |
| 200 | 0xC8 | 4 | SOUND_MODIFY | Audio | `0x0018d0e0` |
| 201 | 0xC9 | var_byte | MESSAGE_PRIVATE_ECHO | Chat | `0x001ebcd0` |
| 202 | 0xCA | 9 | IF_SETEVENTS | Interfaces | `0x0027cb50` |
| 203 | 0xCB | 8 | SERVER_TICK_END | Misc | `0x00221020` |
| 204 | 0xCC | 1 | SET_CHAT_FILTER_A | Chat | `0x00212bd0` |
| 205 | 0xCD | 1 | REMOVE_PLAYER_FROM_LIST | Misc | `0x00211ee0` |
| 206 | 0xCE | 0 | DESTROY_ZONE_DATA | ClientState | — |
| 207 | 0xCF | 2 | IF_OPENTOP | Interfaces | `0x0027c470` |
| 208 | 0xD0 | 3 | UPDATE_REBOOT_TIMER | ClientState | `0x001e3c80` |
| 209 | 0xD1 | 4 | VORBIS_PRELOAD | Audio | `0x001e4110` |
| 210 | 0xD2 | 4 | IF_SETRECOL_ACTIVE | Interfaces | `0x0027d400` |
| 211 | 0xD3 | 5 | SET_HEATMAP | ClientState | `0x001e3c40` |
| 212 | 0xD4 | 0 | TRIGGER_ONDIALOGABORT_2 | Misc | `0x00194170` |
| 213 | 0xD5 | 6 | VORBIS_SONG | Audio | `0x00192360` |
| 214 | 0xD6 | 21 | PROJANIM_SPECIFIC | ZoneUpdates | `0x00193ae0` |
| 215 | 0xD7 | 3 | URL_OPEN | ClientState | `0x001e3960` |
| 216 | 0xD8 | 2 | SOUND_STOP | Audio | `0x00191f10` |

**Statistics:** 216 registered opcodes (0x01-0xD8), 211 with bound handlers, 5 with no handler (88, 92, 99, 115, 183).

## Category Summary

| Category | Count |
|----------|-------|
| Interfaces | 47 |
| Chat | 21 |
| ZoneUpdates | 20 |
| Misc | 21 |
| ClientState | 16 |
| Audio | 17 |
| Variables | 11 |
| Camera | 11 |
| Social | 7 |
| NpcInfo | 6 |
| PlayerInfo | 5 |
| Clans | 5 |
| Inventory | 5 |
| PlayerGroup | 3 |
| WorldData | 2 |
| Rebuild | 2 |
| SpotAnim | 1 |
| Combat | 2 |
| SiteSettings | 1 |
| *(no handler)* | 5 |

## Notes

### Opcode 0
Opcode 0 is not registered in `RegisterAll`. The opcode range is 1-216 (0x01-0xD8).

### No Handler Opcodes
Five opcodes (88, 92, 99, 115, 183) are registered in `RegisterAll` with valid sizes but never get a handler bound. These are likely deprecated or reserved opcodes — the dispatch will find a ServerProt entry but the handler function pointer is null.

### ClientState Opcodes Without Visible Handler
Four ClientState opcodes (32, 104, 156, 206) are registered through a separate code path that writes handler function pointers at runtime. Their handler addresses in BindHandlers are not visible in the static disassembly.

### Dual Registration
18 ZoneUpdates handlers are registered in both the main ServerProt protocol AND a zone sub-protocol. The zone sub-protocol opcodes (0-17) are used within UPDATE_ZONE_FULL_FOLLOWS and UPDATE_ZONE_PARTIAL_FOLLOWS payloads. See `docs/net/serverprot/zoneupdates.md` for the sub-protocol opcode table.

### Isaac Encryption
Wire opcodes are Isaac-encrypted. Opcodes 0-127 use 1 byte; opcodes 128-216 use 2 bytes. Payload bytes are NOT encrypted. See `docs/net/framing.md` for the full encoding scheme.
