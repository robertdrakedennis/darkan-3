# Named packet handlers from the binary (the packet inventory)


## Audio (10)

- `jag::packethandlers::Audio::MIDI_SONG` @ 00187330
- `jag::packethandlers::Audio::SOUND_AREA_SYNTH` @ 00176780
- `jag::packethandlers::Audio::SOUND_AREA_SYNTH_2` @ 00187220
- `jag::packethandlers::Audio::SOUND_GROUP_SPEED` @ 00182810
- `jag::packethandlers::Audio::SOUND_GROUP_STOP` @ 00176180
- `jag::packethandlers::Audio::SOUND_MODIFY` @ 00175e10
- `jag::packethandlers::Audio::SOUND_STOP` @ 0017e640
- `jag::packethandlers::Audio::SYNTH_SOUND` @ 001874d0
- `jag::packethandlers::Audio::VORBIS_PRELOAD` @ 00187270
- `jag::packethandlers::Audio::VORBIS_SONG` @ 001878d0

## Camera (5)

- `jag::packethandlers::Camera::CAM_FORCEANGLE` @ 00186f60
- `jag::packethandlers::Camera::CAM_RESET` @ 00186fa0
- `jag::packethandlers::Camera::CAM_SMOOTHRESET` @ 00186f10
- `jag::packethandlers::Camera::CAM_TARGET` @ 001b2d40
- `jag::packethandlers::Camera::CAM_UPDATE` @ 001d3d10

## Chat (15)

- `jag::packethandlers::Chat::CHAT_FILTER_SETTINGS` @ 001a1d00
- `jag::packethandlers::Chat::CLANCHANNEL_FULL_CHAT` @ 00183ca0
- `jag::packethandlers::Chat::CLANSETTINGS_DELTA_CHAT` @ 001af2e0
- `jag::packethandlers::Chat::MESSAGE_CLANCHANNEL` @ 001a1e80
- `jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL` @ 0019f790
- `jag::packethandlers::Chat::MESSAGE_FRIENDCHAT` @ 0019fe10
- `jag::packethandlers::Chat::MESSAGE_GAME` @ 001983e0
- `jag::packethandlers::Chat::MESSAGE_PRIVATE` @ 001a0490
- `jag::packethandlers::Chat::MESSAGE_PRIVATE_ECHO` @ 0019f7a0
- `jag::packethandlers::Chat::MESSAGE_PUBLIC` @ 00197e00
- `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHANNEL` @ 001a16f0
- `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT` @ 001a2320
- `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE` @ 001a0aa0
- `jag::packethandlers::Chat::SET_CHAT_FILTER_A` @ 00173da0
- `jag::packethandlers::Chat::SET_CHAT_FILTER_B` @ 00173ed0

## Clans (4)

- `jag::packethandlers::Clans::CLANCHANNEL_DELTA` @ 001a3a30
- `jag::packethandlers::Clans::CLANCHANNEL_FULL` @ 00199130
- `jag::packethandlers::Clans::CLANSETTINGS_DELTA` @ 001aef20
- `jag::packethandlers::Clans::CLANSETTINGS_FULL` @ 001a86c0

## ClientState (33)

- `jag::packethandlers::ClientState::BindHandlers` @ 000aa85e
- `jag::packethandlers::ClientState::BindHandlers_extra` @ 000aaf4c
- `jag::packethandlers::ClientState::CLEAR_PENDING_UPDATES` @ 000f4110
- `jag::packethandlers::ClientState::CLIENT_SETVARCBIT_LARGE` @ 00119630
- `jag::packethandlers::ClientState::CLIENT_SETVARCBIT_SMALL` @ 001196e0
- `jag::packethandlers::ClientState::CLIENT_SETVARC_LARGE` @ 00119780
- `jag::packethandlers::ClientState::CLIENT_SETVARC_LONG` @ 001194d0
- `jag::packethandlers::ClientState::CLIENT_SETVARC_SMALL` @ 00119870
- `jag::packethandlers::ClientState::DESTROY_ZONE_DATA` @ 000ef4e0
- `jag::packethandlers::ClientState::MINIMAP_FLAG_SET` @ 0013f4f0
- `jag::packethandlers::ClientState::REBUILD_NORMAL` @ 001203e0
- `jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE` @ 001daa70
- `jag::packethandlers::ClientState::REBUILD_REGION_ALT` @ 001df100
- `jag::packethandlers::ClientState::REBUILD_WORLDENTITY` @ 000efd80
- `jag::packethandlers::ClientState::RESET_ALL_VARPS` @ 00119bd0
- `jag::packethandlers::ClientState::RESET_CLIENT_STATE` @ 000f4080
- `jag::packethandlers::ClientState::RUNCLIENTSCRIPT` @ 00145370
- `jag::packethandlers::ClientState::RUNCLIENTSCRIPT_SHORT` @ 001871a0
- `jag::packethandlers::ClientState::RUNCLIENTSCRIPT_impl` @ 001d2b70
- `jag::packethandlers::ClientState::SET_HEATMAP` @ 0013eea0
- `jag::packethandlers::ClientState::SET_READY_FLAG` @ 00175120
- `jag::packethandlers::ClientState::SET_TICK_TIMER` @ 00175450
- `jag::packethandlers::ClientState::SET_VARC_STR_LARGE` @ 00150220
- `jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT` @ 000f1f30
- `jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT_thunk` @ 0013ec60
- `jag::packethandlers::ClientState::UPDATE_ZONE_PARTIAL` @ 00125640
- `jag::packethandlers::ClientState::URL_OPEN` @ 0013eb60
- `jag::packethandlers::ClientState::VARP_BIT_LARGE` @ 00119930
- `jag::packethandlers::ClientState::VARP_BIT_SMALL` @ 001199f0
- `jag::packethandlers::ClientState::VARP_LARGE` @ 00119a90
- `jag::packethandlers::ClientState::VARP_LONG` @ 00141690
- `jag::packethandlers::ClientState::VARP_SMALL` @ 00119b30
- `jag::packethandlers::ClientState::WORLDENTITY_ADD` @ 001202d0

## Friends (1)

- `jag::packethandlers::Friends::UPDATE_FRIENDLIST` @ 001a46a0

## Interfaces (40)

- `jag::packethandlers::Interfaces::BindHandlers` @ 000ab888
- `jag::packethandlers::Interfaces::IF_CLOSESUB_ACTIVE` @ 001864e0
- `jag::packethandlers::Interfaces::IF_CLOSESUB_BY_ID` @ 00185750
- `jag::packethandlers::Interfaces::IF_OPENSUB` @ 00194280
- `jag::packethandlers::Interfaces::IF_OPENSUB_thunk` @ 001d9fb0
- `jag::packethandlers::Interfaces::IF_OPENTOP` @ 001941d0
- `jag::packethandlers::Interfaces::IF_SET2DANGLE` @ 00194090
- `jag::packethandlers::Interfaces::IF_SETANGLE` @ 001da640
- `jag::packethandlers::Interfaces::IF_SETANIM` @ 001858c0
- `jag::packethandlers::Interfaces::IF_SETANIM_ACTIVE` @ 00185980
- `jag::packethandlers::Interfaces::IF_SETANIM_SMALL` @ 00185c20
- `jag::packethandlers::Interfaces::IF_SETCOLOUR` @ 001859d0
- `jag::packethandlers::Interfaces::IF_SETEVENTS` @ 001860e0
- `jag::packethandlers::Interfaces::IF_SETEVENTS2` @ 001861c0
- `jag::packethandlers::Interfaces::IF_SETGRAPHIC` @ 00193fe0
- `jag::packethandlers::Interfaces::IF_SETHIDE` @ 00194140
- `jag::packethandlers::Interfaces::IF_SETMODEL` @ 00185a60
- `jag::packethandlers::Interfaces::IF_SETMODELORIGIN` @ 001935a0
- `jag::packethandlers::Interfaces::IF_SETMODEL_COORD` @ 00193b50
- `jag::packethandlers::Interfaces::IF_SETNPCHEAD` @ 001857e0
- `jag::packethandlers::Interfaces::IF_SETNPCHEAD_ACTIVE` @ 00193740
- `jag::packethandlers::Interfaces::IF_SETNPCMODEL` @ 00193dd0
- `jag::packethandlers::Interfaces::IF_SETOBJECT` @ 00185b80
- `jag::packethandlers::Interfaces::IF_SETOBJECT_ACTIVE` @ 00185ca0
- `jag::packethandlers::Interfaces::IF_SETOBJECT_SMALL` @ 00185b00
- `jag::packethandlers::Interfaces::IF_SETPLAYERHEAD_ACTIVE` @ 001936b0
- `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_OTHER` @ 00186890
- `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SELF` @ 001866a0
- `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SNAPSHOT` @ 001da3f0
- `jag::packethandlers::Interfaces::IF_SETPOSITION` @ 00189300
- `jag::packethandlers::Interfaces::IF_SETRECOL` @ 001939e0
- `jag::packethandlers::Interfaces::IF_SETSCROLLPOS` @ 001938e0
- `jag::packethandlers::Interfaces::IF_SETSCROLLSIZE` @ 001937e0
- `jag::packethandlers::Interfaces::IF_SETSPRITE` @ 00193ac0
- `jag::packethandlers::Interfaces::IF_SETTEXT` @ 00186040
- `jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE` @ 00186a80
- `jag::packethandlers::Interfaces::IF_SET_HTTP_IMAGE` @ 001babb0
- `jag::packethandlers::Interfaces::IF_SET_MODEL_FRAME` @ 00185cf0
- `jag::packethandlers::Interfaces::IF_SUBSWAP` @ 00186280
- `jag::packethandlers::Interfaces::IF_TRIGGER_CLOSE` @ 001857a0

## Inventory (3)

- `jag::packethandlers::Inventory::UPDATE_INV_FULL_impl` @ 001a8d30
- `jag::packethandlers::Inventory::UPDATE_INV_GROUP` @ 001a8a60
- `jag::packethandlers::Inventory::UPDATE_INV_PARTIAL` @ 00184320

## Lobby (1)

- `jag::packethandlers::Lobby::CHANGE_LOBBY` @ 00196170

## Misc (20)

- `jag::packethandlers::Misc::CUTSCENE_DATA` @ 00174000
- `jag::packethandlers::Misc::LOGOUT` @ 001ba410
- `jag::packethandlers::Misc::LOGOUT_TRANSFER` @ 001c3c00
- `jag::packethandlers::Misc::NO_TIMEOUT` @ 000ef260
- `jag::packethandlers::Misc::RESET_ENTITY_LISTS` @ 00143d50
- `jag::packethandlers::Misc::SERVER_TICK_END` @ 00181880
- `jag::packethandlers::Misc::SET_DISPLAY_INT` @ 00121a90
- `jag::packethandlers::Misc::SET_INTERACTION_FLAG_C` @ 000effe0
- `jag::packethandlers::Misc::SET_INTERACTION_FLAG_D` @ 000f0460
- `jag::packethandlers::Misc::SET_MULTIWAY_STATE` @ 00173c50
- `jag::packethandlers::Misc::SET_PLAYER_OP` @ 0013f040
- `jag::packethandlers::Misc::SET_PLAYER_OP_2` @ 000f0510
- `jag::packethandlers::Misc::SET_PLAYER_OP_3` @ 000f05a0
- `jag::packethandlers::Misc::SET_RUN_ENERGY` @ 001750c0
- `jag::packethandlers::Misc::SET_SYSUPDATE_TIMER` @ 000eff40
- `jag::packethandlers::Misc::SET_URL_STRING` @ 0017fe90
- `jag::packethandlers::Misc::SET_VARC_STR_SMALL` @ 00150340
- `jag::packethandlers::Misc::SKIP_2_BYTES` @ 00176110
- `jag::packethandlers::Misc::SKIP_DATA` @ 00173980
- `jag::packethandlers::Misc::UPDATE_URL_STRING` @ 0017a560

## NPCInfo (8)

- `jag::packethandlers::NPCInfo::NPC_HEADICON_SPECIFIC` @ 00185db0
- `jag::packethandlers::NPCInfo::NPC_INFO_thunk_worldentity` @ 001d54f0
- `jag::packethandlers::NPCInfo::NPC_SAY` @ 001795e0
- `jag::packethandlers::NPCInfo::NPC_SPOTANIM` @ 000f1f70
- `jag::packethandlers::NPCInfo::SET_NPC_OP` @ 0017ff30
- `jag::packethandlers::NPCInfo::SET_NPC_OP_impl` @ 0017f7c0
- `jag::packethandlers::NPCInfo::SET_NPC_UPDATE_FLAG` @ 00173b00
- `jag::packethandlers::NPCInfo::SET_NPC_UPDATE_ORIGIN` @ 001b2de0

## PlayerGroup (2)

- `jag::packethandlers::PlayerGroup::PLAYER_OP` @ 00186b70
- `jag::packethandlers::PlayerGroup::UPDATE_PLAYER_GROUP` @ 00198b80

## PlayerInfo (4)

- `jag::packethandlers::PlayerInfo::HANDSHAKE_UID` @ 00133860
- `jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE` @ 00183ed0
- `jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2` @ 001bf4c0
- `jag::packethandlers::PlayerInfo::UPDATE_PLAYER_CHAT` @ 001583c0

## PlayerList (1)

- `jag::packethandlers::PlayerList::BindHandlers` @ 000ab3ea

## Rebuild (1)

- `jag::packethandlers::Rebuild::REBUILD_REGION_HANDLER` @ 000f6fe0

## SiteSettings (1)

- `jag::packethandlers::SiteSettings::UPDATE_SITESETTINGS_thunk` @ 001a65a0

## Social (1)

- `jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED` @ 001d3d00

## WorldData (3)

- `jag::packethandlers::WorldData::SET_WORLD_TARGET` @ 0017fc70
- `jag::packethandlers::WorldData::SWITCH_WORLD` @ 001aeba0
- `jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY` @ 00190020

## ZoneUpdates (30)

- `jag::packethandlers::ZoneUpdates::BindHandlers` @ 000ae1a8
- `jag::packethandlers::ZoneUpdates::LOC_ADD` @ 00139200
- `jag::packethandlers::ZoneUpdates::LOC_ANIM` @ 00118e60
- `jag::packethandlers::ZoneUpdates::LOC_ANIM_SPECIFIC` @ 00119070
- `jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE` @ 00141680
- `jag::packethandlers::ZoneUpdates::LOC_DEL` @ 001391f0
- `jag::packethandlers::ZoneUpdates::LOC_MERGE` @ 000eeba0
- `jag::packethandlers::ZoneUpdates::LOC_PREFETCH` @ 00138290
- `jag::packethandlers::ZoneUpdates::MAP_ANIM` @ 00157bd0
- `jag::packethandlers::ZoneUpdates::MAP_ANIM_SPECIFIC` @ 00157740
- `jag::packethandlers::ZoneUpdates::MAP_PROJANIM` @ 000f2c30
- `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL` @ 000f25d0
- `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL_2` @ 000f1420
- `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_HALT` @ 000f1bc0
- `jag::packethandlers::ZoneUpdates::OBJ_ADD` @ 001193c0
- `jag::packethandlers::ZoneUpdates::OBJ_COUNT` @ 00119250
- `jag::packethandlers::ZoneUpdates::OBJ_DEL` @ 00152c80
- `jag::packethandlers::ZoneUpdates::OBJ_REVEAL` @ 000f37b0
- `jag::packethandlers::ZoneUpdates::PLAYER_SPOTANIM` @ 000f2280
- `jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC` @ 000f2900
- `jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC_HALT` @ 000f1840
- `jag::packethandlers::ZoneUpdates::SOUND_AREA` @ 00151d90
- `jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY` @ 00157200
- `jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY_2` @ 001db210
- `jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC` @ 00187610
- `jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC_PACKED` @ 00187770
- `jag::packethandlers::ZoneUpdates::UNKNOWN_op173_handler` @ 001ae2c0
- `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS` @ 000f9510
- `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_ENCLOSED` @ 000eefc0
- `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_FOLLOWS` @ 000ef150

**Total: 192 packethandlers functions in 19 categories**
