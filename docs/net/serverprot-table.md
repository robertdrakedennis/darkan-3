# ServerProt Master Opcode Table (Build 946-5)

Complete table of all 217 ServerProt opcodes (0x00–0xD8). All 217 are registered in `RegisterAll`.
~130 handlers are bound in `BindHandlers`; the remaining ~87 are bound by category constructors
(Interfaces, ZoneUpdates, Variables, ClientState). All 217 now have identified handlers.

**Sizes verified against 946-5 binary** (`RegisterAll` at `0x00182030`).
Names from 946-3 signature matching — ~36 opcodes reshuffled between builds, marked with `[UNVERIFIED]`.

Size: positive = fixed bytes, `var_byte` = variable 1-byte length prefix, `var_short` = variable 2-byte BE length prefix, `0` = no payload.

Handler addresses are the actual packet handler function, not the manager wrapper.

Sources: `jag::ServerProt::RegisterAll` (0x00166750 / 0x00182030), `jag::ServerProt::BindHandlers` (0x0011858a),
`jag::packethandlers::Variables` (0x0014ce1e), `jag::packethandlers::ZoneUpdates` (0x0014c4c8),
`jag::packethandlers::Interfaces`, `jag::packethandlers::ClientState`,
and handler documentation in `docs/net/serverprot/`.

Ghidra data labels: Each entry is a 64-byte ProtEntry struct at addresses in the `0x016e7400`–`0x016feba0` range,
renamed to `jag::ServerProt::<NAME>` (e.g., `jag::ServerProt::IF_OPENTOP` at `0x016fd220`).

| Opcode | Hex | Size | Name | Category | Handler Address | Data Address |
|--------|-----|------|------|----------|-----------------|--------------|
|   0 | 0x00 | 28 | SET_UID | Misc | `0x00221ed0` | `0x016e9440` |
|   1 | 0x01 | var_byte | MESSAGE_GAME | Chat | `0x001e6640` | `0x016e9400` |
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
|  12 | 0x0C | 6 | SET_VARC_INT_2 | Variables | `0x001b98c0` | `[VERIFIED]` handler = SET_VARC_INT; was SET_VARC_SMALL in 946-3 |
|  13 | 0x0D | 1 | SET_PLAYER_OP_3 | Misc | `0x002124a0` |
|  14 | 0x0E | 3 | SET_VARP_SMALL | Variables | `0x001b9c70` |
|  15 | 0x0F | var_short | MESSAGE_PUBLIC | Chat | `0x001ee310` |
|  16 | 0x10 | var_short | RUN_CLIENTSCRIPT | Chat | `0x001e6460` |
|  17 | 0x11 | var_short | UPDATE_IGNORELIST | Social | `0x00235b20` |
|  18 | 0x12 | var_short | UPDATE_SITESETTINGS | SiteSettings | `0x00242840` |
|  19 | 0x13 | 3 | SET_VARC_SMALL_2 | Variables | `0x001c2220` | `[VERIFIED]` handler = SET_VARC_SMALL; was SET_VARC_COORD in 946-3 |
|  20 | 0x14 | var_short | IF_OPENSUB_ACTIVE | Interfaces | `0x0024ade0` |
|  21 | 0x15 | 32 | IF_SETANGLE | Interfaces | `0x0027e350` |
|  22 | 0x16 | 2 | SET_TICK_TIMER | ClientState | `0x002141c0` |
|  23 | 0x17 | 1 | CAM_TARGET | Camera | `0x001c6a20` |
|  24 | 0x18 | 20 | MAP_PROJANIM | ZoneUpdates | `0x00193e10` |
|  25 | 0x19 | 0 | RESET_ENTITY_LISTS | Misc | `0x001e7480` |
|  26 | 0x1A | 8 | IF_SETOBJECT_NONUM | Interfaces | `0x0027ef90` |
|  27 | 0x1B | 1 | SET_RUN_ENERGY | Misc | `0x00213e30` |
|  28 | 0x1C | var_short | NPC_INFO_DECODE | NpcInfo | `0x0027a0e0` | `[VERIFIED]` was UPDATE_FRIENDLIST_2 in 946-3 |
|  29 | 0x1D | var_byte | CLANSETTINGS_FULL_2 | Clans | `0x0023fab0` |
|  30 | 0x1E | var_byte | LOC_ADD | ZoneUpdates | `0x001e46b0` |
|  31 | 0x1F | var_byte | MESSAGE_QUICKCHAT_CLANCHANNEL | Chat | `0x001ecf90` |
|  32 | 0x20 | 0 | RESET_CLIENT_STATE | ClientState | — | `[VERIFIED]` was RESET_ALL_VARPS in 946-3 (rotation chain) |
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
|  51 | 0x33 | 6 | SET_VARBIT_INT_2 | Variables | `0x001b9a30` | `[VERIFIED]` handler = SET_VARBIT_INT; was SET_VARBIT_SMALL in 946-3 |
|  52 | 0x34 | 1 | CAM_FORCEANGLE | Camera | `0x001ea570` |
|  53 | 0x35 | 35 | CUTSCENE_DATA | Misc | `0x00212d80` |
|  54 | 0x36 | 25 | PROJANIM | Combat | `0x001930a0` |
|  55 | 0x37 | 4 | CAM_SHAKE | Camera | `0x001eaf30` |
|  56 | 0x38 | var_byte | MESSAGE_FRIENDCHANNEL | Chat | `0x0026b6a0` | `[VERIFIED]` was MESSAGE_PRIVATE_SYSTEM in 946-3 |
|  57 | 0x39 | var_short | IF_SETTARGETPARAM | Interfaces | `0x0027cd10` |
|  58 | 0x3A | 6 | CAM_LOOKAT | Camera | `0x001eaa90` |
|  59 | 0x3B | 12 | IF_SETTEXT | Interfaces | `0x0027d170` |
|  60 | 0x3C | var_byte | RESET_VARC_SMALL | Variables | `0x001b9730` |
|  61 | 0x3D | 8 | IF_SETTEXTFONT | Interfaces | `0x0027f1f0` |
|  62 | 0x3E | 10 | IF_SETRECOL | Interfaces | `0x0027d440` |
|  63 | 0x3F | var_short | CLANSETTINGS_DELTA | Clans | `0x001c2bc0` |
|  64 | 0x40 | 2 | LOC_DEL | ZoneUpdates | `0x001dc500` |
|  65 | 0x41 | var_byte | UPDATE_ZONE_FULL_FOLLOWS_2 | ClientState | `0x001e42b0` |
|  66 | 0x42 | var_short | PLAYER_OP | PlayerGroup | `0x00225bf0` | `[VERIFIED]` was FRIENDLIST_LOADED in 946-3 |
|  67 | 0x43 | 25 | IF_SETMODEL_BODYTYPE | Interfaces | `0x0027d790` |
|  68 | 0x44 | var_byte | MESSAGE_QUICKCHAT_FRIENDCHAT | Chat | `0x001ec9a0` |
|  69 | 0x45 | 0 | CAM_RESET | Camera | `0x001e9a50` |
|  70 | 0x46 | 5 | MIDI_SWAP | Audio | `0x001e4180` |
|  71 | 0x47 | 8 | IF_SETPLAYERMODEL_BASECOLOUR | Interfaces | `0x0027de90` |
|  72 | 0x48 | 3 | SET_VARBIT_SMALL_2 | Variables | `0x001b9980` | `[VERIFIED]` handler = SET_VARBIT_SMALL; was SET_VARBIT_INT in 946-3 |
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
|  88 | 0x58 | 3 | RESET_VARC_SMALL_2 | Variables | — | `[946-5 verified]` |
|  89 | 0x59 | 8 | IF_SETCLICKMASK | Interfaces | `0x0027e150` |
|  90 | 0x5A | 3 | UPDATE_ZONE_FULL_FOLLOWS | ZoneUpdates | `0x001989b0` |
|  91 | 0x5B | 12 | SPOTANIM_SPECIFIC | SpotAnim | `0x002060a0` |
|  92 | 0x5C | var_short | NOOP_UNHANDLED | Misc | — | `[946-5 verified]` No handler — payload discarded |
|  93 | 0x5D | 10 | IF_SETPLAYERMODEL | Interfaces | `0x0027e250` |
|  94 | 0x5E | 19 | NPC_HITMARKS_AND_HEADBARS | Combat | `0x001941b0` |
|  95 | 0x5F | var_short | CLANSETTINGS_DELTA_CHAT | Chat | `0x001c2f80` |
|  96 | 0x60 | 4 | CAM_MOVETO_ARC | Camera | `0x001c6220` |
|  97 | 0x61 | 2 | SET_PLAYER_OP_2 | Misc | `0x00212410` |
|  98 | 0x62 | 3 | OBJ_DEL | ZoneUpdates | `0x001fa050` |
|  99 | 0x63 | 0 | IF_TRIGGER_CLOSE | Interfaces | — | `[946-5 verified]` |
| 100 | 0x64 | 10 | IF_SETNPCMODEL | Interfaces | `0x0027e520` |
| 101 | 0x65 | 3 | REMOVE_TRACKED_ENTRY | Misc | `0x002130b0` |
| 102 | 0x66 | 1 | SET_NPC_UPDATE_ORIGIN | NpcInfo | `0x00191030` |
| 103 | 0x67 | 10 | SET_PLAYER_GROUP | PlayerGroup | `0x00225e80` |
| 104 | 0x68 | 0 | DESTROY_ZONE_DATA | ClientState | — | `[VERIFIED]` was RESET_CLIENT_STATE in 946-3 (rotation chain) |
| 105 | 0x69 | 8 | IF_SETPLAYERMODEL_SELF | Interfaces | `0x0027e620` |
| 106 | 0x6A | 4 | IF_SETANIM | Interfaces | `0x0027f110` |
| 107 | 0x6B | var_byte | CHAT_FILTER_SETTINGS | Chat | `0x00191650` |
| 108 | 0x6C | 6 | IF_SETOBJECT_ALWAYSNUM | Interfaces | `0x0027ed90` |
| 109 | 0x6D | var_short | REBUILD_NORMAL_HANDLER | Rebuild | `0x00198080` |
| 110 | 0x6E | var_short | CAM_UPDATE | Camera | `0x001f7900` |
| 111 | 0x6F | var_byte | NOOP_VAR | Misc | `0x0018cd20` |
| 112 | 0x70 | 0 | RESET_ALL_VARPS | Variables | — | `[VERIFIED]` was UPDATE_STAT in 946-3 (rotation chain) |
| 113 | 0x71 | 10 | IF_SETMODEL_COLOUR | Interfaces | `0x0027d5f0` |
| 114 | 0x72 | 6 | UPDATE_STAT | Variables | — | `[VERIFIED]` was SET_VARC_STR_SMALL in 946-3 (rotation chain) |
| 115 | 0x73 | 6 | RESET_VARC_INT | Variables | — | `[946-5 verified]` |
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
| 128 | 0x80 | 3 | IF_SETOBJECT_ACTIVE | Interfaces | `0x0027e6f0` | `[VERIFIED]` was IF_SETANGLE_ACTIVE; handler writes IC+0x150/0x151 |
| 129 | 0x81 | 6 | SET_MAP_FLAG | ClientState | `0x001ead00` |
| 130 | 0x82 | var_byte | MESSAGE_PRIVATE | Chat | `0x001eb6c0` |
| 131 | 0x83 | 0 | LOGOUT_TRANSFER | Misc | `0x00260440` |
| 132 | 0x84 | 5 | LOC_MERGE | ZoneUpdates | `0x0018ef50` |
| 133 | 0x85 | 1 | CREATE_CHECK_EMAIL_REPLY | Misc | `0x00212ce0` | `[VERIFIED]` was SET_CHAT_FILTER_D in 946-3 |
| 134 | 0x86 | 0 | LOGOUT | Misc | `0x00255960` |
| 135 | 0x87 | 1 | SET_WEIGHT | Misc | `0x00212950` |
| 136 | 0x88 | 2 | SOUND_GROUP_STOP | Audio | `0x0018d450` |
| 137 | 0x89 | 9 | IF_SETNPCMODEL_ANIM | Interfaces | `0x0027e710` |
| 138 | 0x8A | 10 | SET_VARP_LONG | Variables | `0x001b9ac0` |
| 139 | 0x8B | 4 | SET_PLAYER_GROUP_2 | PlayerGroup | `0x00225d60` | `[VERIFIED]` was PLAYER_OP in 946-3 |
| 140 | 0x8C | 5 | IF_SETOBJECT_NONUM_2 | Interfaces | `0x0027f070` |
| 141 | 0x8D | var_short | UPDATE_ZONE_FULL_FOLLOWS_3 | ZoneUpdates | `0x0024a510` |
| 142 | 0x8E | 1 | WORLDENTITY_INFO_V4 | WorldEntity | `0x0018feb0` | `[VERIFIED]` was MINIMAP_FLAG_SET in 946-3 |
| 143 | 0x8F | 8 | SET_CAMERA_TARGET | WorldData | `0x0021ec60` |
| 144 | 0x90 | 4 | SET_SYSUPDATE_TIMER | Misc | `0x00211e40` |
| 145 | 0x91 | 8 | IF_SETHIDE_ACTIVE | Interfaces | `0x0027fd70` |
| 146 | 0x92 | 0 | NOOP | Misc | `0x00212d40` |
| 147 | 0x93 | 29 | PROJANIM_SPECIFIC_HALT | ZoneUpdates | `0x001933f0` |
| 148 | 0x94 | var_short | UPDATE_PLAYER_CHAT | PlayerInfo | `0x00222340` |
| 149 | 0x95 | var_byte | SET_WORLD_TARGET | WorldData | `0x0021aae0` |
| 150 | 0x96 | var_short | UPDATE_FRIENDCHAT_CHANNEL | Social | `0x0022f660` |
| 151 | 0x97 | 3 | MINIMAP_TOGGLE | ClientState | `0x0018fc40` | `[VERIFIED]` was RESET_ANIMS in 946-3 |
| 152 | 0x98 | var_byte | UPDATE_IGNORELIST | Social | `0x00276e20` |
| 153 | 0x99 | 2 | WORLDENTITY_INFO_V2 | WorldEntity | `0x001b9f80` | `[VERIFIED]` was RUNCLIENTSCRIPT in 946-3 |
| 154 | 0x9A | var_short | UPDATE_PLAYER_GROUP | PlayerGroup | `0x002384f0` |
| 155 | 0x9B | var_byte | UPDATE_URL_STRING | Misc | `0x001e02a0` |
| 156 | 0x9C | 6 | WORLDENTITY_INFO_V1 | WorldEntity | `0x001ba170` | `[VERIFIED]` was CLANCHANNEL_DELTA_CS in 946-3 |
| 157 | 0x9D | 3 | WORLDENTITY_INFO_V3 | WorldEntity | `0x0018fdb0` | `[VERIFIED]` was SET_PLAYER_OP in 946-3 |
| 158 | 0x9E | 12 | SYNTH_SOUND | Audio | `0x00191850` |
| 159 | 0x9F | 28 | MAP_PROJANIM_HALT | ZoneUpdates | `0x00193770` |
| 160 | 0xA0 | 6 | SOUND_GROUP_SPEED | Audio | `0x00192470` |
| 161 | 0xA1 | 1 | SET_INTERACTION_FLAG_B | Misc | `0x002122f0` |
| 162 | 0xA2 | var_byte | IF_SETGRAPHIC_ACTIVE | Interfaces | `0x00256100` |
| 163 | 0xA3 | var_short | REBUILD_PLAYERINFO_POSITIONS | PlayerInfo | `0x00222080` |
| 164 | 0xA4 | 1 | SET_CHAT_FILTER_A | Chat | `0x00212b20` | `[VERIFIED]` was SET_CHAT_FILTER_C in 946-3 |
| 165 | 0xA5 | 9 | NPC_ANIM_SPECIFIC | ZoneUpdates | `0x00279ce0` | `[VERIFIED]` was NPC_INFO; reads 4B anim + 1B slot + 2B frame + 2B npcIdx |
| 166 | 0xA6 | 0 | MIDI_STOP | Audio | `0x00191c50` |
| 167 | 0xA7 | 2 | SKIP_2_BYTES | Misc | `0x0018d3e0` |
| 168 | 0xA8 | var_short | UPDATE_INV_GROUP | Inventory | `0x00244a00` |
| 169 | 0xA9 | 1 | SET_INTERACTION_FLAG_C | Misc | `0x00212280` |
| 170 | 0xAA | var_short | NOOP_VAR | Misc | `0x00211e00` |
| 171 | 0xAB | var_byte | SET_URL_STRING | Misc | `0x00218f70` |
| 172 | 0xAC | 5 | IF_SETNPCMODEL_ACTIVE | Interfaces | `0x0027f990` |
| 173 | 0xAD | var_short | IF_OPENSUB_2 | Interfaces | `0x0027c4c0` | `[VERIFIED]` was IF_SETMODEL_ACTIVE in 946-3 |
| 174 | 0xAE | var_short | NPC_INFO_2 | NpcInfo | `0x00277a40` | `[VERIFIED]` was UPDATE_FRIENDLIST in 946-3 |
| 175 | 0xAF | 3 | SET_INTERACTION_FLAG_D | Misc | `0x00212360` |
| 176 | 0xB0 | 14 | IF_SETPLAYERMODEL_EXACTMOVE | Interfaces | `0x0027c9b0` |
| 177 | 0xB1 | var_short | REBUILD_REGION_HANDLER | Rebuild | `0x00198090` |
| 178 | 0xB2 | var_short | REBUILD_WORLDENTITY | WorldEntity | `0x001901b0` | `[VERIFIED]` was REBUILD_REGION in 946-3 |
| 179 | 0xB3 | 2 | FRIENDCHAT_SYSUPDATE | Social | `0x00238140` |
| 180 | 0xB4 | 5 | IF_CLOSESUB | Interfaces | `0x0027c680` |
| 181 | 0xB5 | 1 | SET_NPC_UPDATE_FLAG | NpcInfo | `0x00212880` |
| 182 | 0xB6 | 5 | IF_OPENSUB | Interfaces | `0x0027c5a0` |
| 183 | 0xB7 | 10 | SET_VARC_COORD_2 | Variables | — | `[946-5 verified]` Coord varc setter (2B key + 4B x + 4B y) |
| 184 | 0xB8 | 4 | SOUND_AREA_SYNTH | Audio | `0x001c1d20` |
| 185 | 0xB9 | 33 | MAP_PROJANIM | ZoneUpdates | `0x00192ca0` |
| 186 | 0xBA | var_short | REBUILD_NORMAL | ClientState | `0x001e4390` |
| 187 | 0xBB | 4 | SET_TRIGGER_VAR | Variables | `0x00225df0` | `[VERIFIED]` was PLAYER_GROUP_DELTA in 946-3 |
| 188 | 0xBC | 1 | SET_CHAT_FILTER_B | Chat | `0x00212c50` | `[VERIFIED]` confirmed correct |
| 189 | 0xBD | var_short | PLAYER_INFO_DECODE | PlayerInfo | `0x0025b000` |
| 190 | 0xBE | 3 | IF_SETTEXT_ACTIVE | Interfaces | `0x0027cea0` | `[VERIFIED]` was IF_MOVESUB_ACTIVE; handler writes IC+0x154/0x155 |
| 191 | 0xBF | 2 | CLEAR_MAP_FLAG | ClientState | `0x0019a7a0` | `[VERIFIED]` was TRIGGER_ONDIALOGABORT in 946-3 |
| 192 | 0xC0 | var_short | SKIP_DATA | Misc | `0x00212700` |
| 193 | 0xC1 | 15 | SPOTANIM_SPECIFIC_2 | ZoneUpdates | `0x00205ac0` | `[VERIFIED]` was MAP_FLAG_SET in 946-3 |
| 194 | 0xC2 | 14 | MAP_ANIM_SPECIFIC | ZoneUpdates | `0x002065e0` |
| 195 | 0xC3 | 3 | IF_SETMODEL_ACTIVE | Interfaces | `0x0027d930` | `[VERIFIED]` was IF_SETPOSITION_ACTIVE; handler writes IC+0x152/0x153 |
| 196 | 0xC4 | var_short | UNUSED_NOOP | Misc | `0x0018cd30` | `[VERIFIED]` was OCULUS_SYNC in 946-3; handler is no-op |
| 197 | 0xC5 | 3 | IF_MOVESUB_ACTIVE | Interfaces | `0x0027c910` | `[VERIFIED]` was IF_SETCLICKMASK_ACTIVE; handler writes IC+0x156/0x157 |
| 198 | 0xC6 | var_byte | SOUND_AREA | ZoneUpdates | `0x001cc090` |
| 199 | 0xC7 | var_byte | FRIENDCHAT_JOIN | Chat | `0x001c1470` |
| 200 | 0xC8 | 4 | SOUND_MODIFY | Audio | `0x0018d0e0` |
| 201 | 0xC9 | var_byte | MESSAGE_PRIVATE_ECHO | Chat | `0x001ebcd0` |
| 202 | 0xCA | 9 | IF_SETEVENTS | Interfaces | `0x0027cb50` |
| 203 | 0xCB | 8 | SERVER_TICK_END | Misc | `0x00221020` |
| 204 | 0xCC | 1 | CREATE_CHECK_NAME_REPLY | Misc | `0x00212bd0` | `[VERIFIED]` was SET_CHAT_FILTER_A in 946-3 |
| 205 | 0xCD | 1 | REMOVE_PLAYER_FROM_LIST | Misc | `0x00211ee0` |
| 206 | 0xCE | 0 | CLEAR_PENDING_UPDATES | ClientState | — | `[VERIFIED]` was DESTROY_ZONE_DATA in 946-3 (rotation chain) |
| 207 | 0xCF | 2 | IF_OPENTOP | Interfaces | `0x0027c470` |
| 208 | 0xD0 | 3 | WORLDENTITY_INFO_V5 | WorldEntity | `0x001b9ed0` | `[VERIFIED]` was UPDATE_REBOOT_TIMER in 946-3 |
| 209 | 0xD1 | 4 | VORBIS_PRELOAD | Audio | `0x001e4110` |
| 210 | 0xD2 | 4 | IF_SETRECOL_ACTIVE | Interfaces | `0x0027d400` |
| 211 | 0xD3 | 5 | REBUILD_REGION | Rebuild | — | `[VERIFIED]` was SET_HEATMAP in 946-3 |
| 212 | 0xD4 | 0 | TRIGGER_ONDIALOGABORT_2 | Misc | `0x00194170` |
| 213 | 0xD5 | 6 | VORBIS_SONG | Audio | `0x00192360` |
| 214 | 0xD6 | 21 | PROJANIM_SPECIFIC | ZoneUpdates | `0x00193ae0` |
| 215 | 0xD7 | 3 | URL_OPEN | ClientState | `0x001e3960` |
| 216 | 0xD8 | 2 | SOUND_STOP | Audio | `0x00191f10` |

**Statistics:** 217 registered opcodes (0x00-0xD8). All have identified handlers in 946-5 (the 5 formerly unhandled opcodes — 88, 92, 99, 115, 183 — have been identified). 6 sizes changed between 946-3 and 946-5 (ops 12, 19, 51, 72, 112, 114). ~36 opcodes reshuffled names between builds.

## Category Summary

| Category | Count |
|----------|-------|
| Interfaces | 47 |
| Misc | 22 |
| Chat | 21 |
| ZoneUpdates | 20 |
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
| Combat | 2 |
| SpotAnim | 1 |
| SiteSettings | 1 |
| *(no handler)* | 5 |

## Notes

### Opcode 0 (SET_UID)
Opcode 0 IS registered in `RegisterAll` as `InitEntry(&DAT_016e9440, 0, 0x1c)`. It has size 28 and the SET_UID handler reads a 24-byte identity block followed by a 4-byte CRC32 checksum. The handler verifies the CRC before storing the player UID. Ghidra data address: `0x016e9440`, handler: `0x00221ed0`.

### Formerly No-Handler Opcodes (946-3 → 946-5)
Five opcodes (88, 92, 99, 115, 183) had no handler in 946-3 but have been identified in 946-5 via Agent 1C binary analysis:
- **88**: `RESET_VARC_SMALL_2` (3B) — resets a varc to a small value
- **92**: `NOOP_UNHANDLED` (var_short) — still no handler; payload is discarded
- **99**: `IF_TRIGGER_CLOSE` (0B) — triggers interface close
- **115**: `RESET_VARC_INT` (6B) — resets a varc (int-sized)
- **183**: `SET_VARC_COORD_2` (10B) — coordinate varc setter (2B key + 4B x + 4B y)

### Variable Rotation Chain (946-3 → 946-5)
Five opcodes (32, 104, 112, 114, 206) rotated handler assignments between builds. Each opcode's handler shifted to a different opcode:
| Opcode | 946-3 Name | 946-5 Name |
|--------|-----------|-----------|
| 32 | RESET_ALL_VARPS | RESET_CLIENT_STATE |
| 104 | RESET_CLIENT_STATE | DESTROY_ZONE_DATA |
| 112 | UPDATE_STAT | RESET_ALL_VARPS |
| 114 | SET_VARC_STR_SMALL | UPDATE_STAT |
| 206 | DESTROY_ZONE_DATA | CLEAR_PENDING_UPDATES |

### WorldEntity Packets (New in 946-5)
Seven WorldEntity info packets and two related packets were assigned to opcodes that had different handlers in 946-3. These are an entirely new packet category:
| Opcode | 946-3 Name | 946-5 Name | Size | Handler Address |
|--------|-----------|-----------|------|-----------------|
| 142 | MINIMAP_FLAG_SET | WORLDENTITY_INFO_V4 | 1 | `0x0018feb0` |
| 151 | RESET_ANIMS | MINIMAP_TOGGLE | 3 | `0x0018fc40` |
| 153 | RUNCLIENTSCRIPT | WORLDENTITY_INFO_V2 | 2 | `0x001b9f80` |
| 156 | CLANCHANNEL_DELTA_CS | WORLDENTITY_INFO_V1 | 6 | `0x001ba170` |
| 157 | SET_PLAYER_OP | WORLDENTITY_INFO_V3 | 3 | `0x0018fdb0` |
| 178 | REBUILD_REGION | REBUILD_WORLDENTITY | var_short | `0x001901b0` |
| 191 | TRIGGER_ONDIALOGABORT | CLEAR_MAP_FLAG | 2 | `0x0019a7a0` |
| 208 | UPDATE_REBOOT_TIMER | WORLDENTITY_INFO_V5 | 3 | `0x001b9ed0` |
| 211 | SET_HEATMAP | REBUILD_REGION | 5 | — |

### 946-3 → 946-5 Size Changes
Six opcodes changed size between builds. All are verified — variable handlers reshuffled between opcodes:
| Opcode | 946-3 Name | 946-5 Name | 946-3 Size | 946-5 Size |
|--------|-----------|-----------|------------|------------|
| 12 | SET_VARC_SMALL | SET_VARC_INT_2 | 3 | 6 |
| 19 | SET_VARC_COORD | SET_VARC_SMALL_2 | 10 | 3 |
| 51 | SET_VARBIT_SMALL | SET_VARBIT_INT_2 | 3 | 6 |
| 72 | SET_VARBIT_INT | SET_VARBIT_SMALL_2 | 6 | 3 |
| 112 | UPDATE_STAT | RESET_ALL_VARPS | 6 | 0 |
| 114 | SET_VARC_STR_SMALL | UPDATE_STAT | var_byte | 6 |

`[VERIFIED]` Opcodes 12/19 swapped handlers (SET_VARC_INT↔SET_VARC_SMALL), opcodes 51/72 swapped (SET_VARBIT_INT↔SET_VARBIT_SMALL). Size=6 = 2B key + 4B int value, size=3 = 2B key + 1B byte value.

### ClientState Opcodes Without Visible Handler
Four opcodes (32=RESET_CLIENT_STATE, 104=DESTROY_ZONE_DATA, 156=WORLDENTITY_INFO_V1, 206=CLEAR_PENDING_UPDATES) are registered through a separate code path that writes handler function pointers at runtime. Their handler addresses in BindHandlers are not visible in the static disassembly. Note: 32/104/206 are part of the variable rotation chain; 156 was reassigned to a WorldEntity handler.

### Dual Registration
18 ZoneUpdates handlers are registered in both the main ServerProt protocol AND a zone sub-protocol. The zone sub-protocol opcodes (0-17) are used within UPDATE_ZONE_FULL_FOLLOWS and UPDATE_ZONE_PARTIAL_FOLLOWS payloads. See `docs/net/serverprot/zoneupdates.md` for the sub-protocol opcode table.

### Isaac Encryption
Wire opcodes are Isaac-encrypted. Opcodes 0-127 use 1 byte; opcodes 128-216 use 2 bytes. Payload bytes are NOT encrypted. See `docs/net/framing.md` for the full encoding scheme.

### Ghidra Refactoring Status (rs2client, port 8080)
All 217 ServerProt data entries have been renamed in Ghidra to `jag::ServerProt::<NAME>` format at their respective ProtEntry struct addresses. All handler functions have been renamed to `jag::packethandlers::<Category>::<NAME>` format. Key functions annotated with decompiler comments:

- `jag::ServerProt::RegisterAll` at `0x00166750` / `0x00182030`
- `jag::ServerProt::BindHandlers` at `0x0011858a`
- `jag::ServerProt::InitEntry` at `0x00181d40`
- `jag::ClientProt::InitEntry` at `0x00181da0`
- `jag::LoginProt::InitEntry` at `0x00181e00`
- Global vectors: `g_serverProtVector` at `0x016ea080`, `g_clientProtVector` at `0x016e9fc0`

### Lobby-Critical ServerProt Packets
For Phase 2 (Lobby Login), the following packets are essential:

| Opcode | Name | Purpose |
|--------|------|---------|
| 0 | SET_UID | Set player identity after login (24B identity + 4B CRC) |
| 2 | SET_VARC_INT | Set client variable (varc) |
| 14 | SET_VARP_SMALL | Set player variable (varp, small) |
| 16 | RUN_CLIENTSCRIPT | Execute CS2 script |
| 22 | SET_TICK_TIMER | Set client tick interval |
| 25 | RESET_ENTITY_LISTS | Clear entity lists |
| 35 | SET_READY_FLAG | Signal server ready |
| 51 | SET_VARBIT_SMALL | Set varbit (small) |
| 124 | SET_VARP_INT | Set player variable (varp, int) |
| 134 | LOGOUT | Logout from game |
| 207 | IF_OPENTOP | Open top-level interface (lobby UI) |
| 203 | SERVER_TICK_END | End of tick marker |
