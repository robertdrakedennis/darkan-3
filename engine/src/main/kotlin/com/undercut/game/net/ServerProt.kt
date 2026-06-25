package com.undercut.game.net

/**
 * All 218 server-to-client protocol opcodes. Valid for Build 948-5 (a sub-revision of
 * 948-2: opcode numbers + payload sizes are stable; only `.text`/FUN_ addresses shifted).
 * Originally regenerated from Phase 4a extraction at `re-resources/948-2-phase4a/serverprot_948-2_phase4a.csv`;
 * names + FUN_ comments below corrected against 948-5 Ghidra analysis and the
 * librs2client.so `jag::ServerProt` name oracle (see memory/serverprot-name-oracle.md).
 *
 * Source-of-truth chain in 948-2:
 *   jag::ServerProt::RegisterAll @ 0x000c4700
 *   jag::ServerProt::InitEntry   @ 0x000e5e30  (218 calls)
 *   jag::ServerProt::InitSubEntry @ 0x000e5e90 (18 zone sub-prot calls)
 *   jag::ServerProt::BindHandlers @ 0x0007509a
 *   g_serverProtVector @ DAT_015d4640
 *   max opcode 0xD9 (217).
 *
 * `FUN_*` handlers whose semantic name is unconfirmed are exposed as `UNKNOWN_<dec>`
 * per the project's existing convention; the underlying opcode + size is correct,
 * only the semantic label is missing. Several CSV names were found WRONG and corrected
 * (PROJANIM_FIXED->LOC_ADD_CHANGE, SET_HUD_VARIANT_A/B->CREATE_CHECK_NAME/EMAIL_REPLY,
 * NPC_SET_* at 0x81/0x99/0x9a/0xb6/0xcb/0xd7 are world effect-emitter ops not NPCs,
 * LOGOUT_STEP_8/NOOP_LOGIN_PING->Audio); see per-line NOTE comments for evidence.
 *
 * @param opcode Wire opcode (0-217)
 * @param size Fixed payload size in bytes, -1 = var_byte, -2 = var_short, 0 = no payload
 * @param category Functional grouping for filtering
 */
enum class ServerProt(val opcode: Int, val size: Int, val category: Category) {
    MESSAGE_QUICKCHAT_CLANCHANNEL(0x00, -1, Category.CHAT),
    SET_NPC_OP(0x01, -1, Category.NPC_INFO),
    MESSAGE_QUICKCHAT_CLANCHAT(0x02, -1, Category.CHAT),
    IF_SETTOPLEVELINTERFACE(0x03, 19, Category.INTERFACES),
    IF_SETANGLE(0x04, 32, Category.INTERFACES),
    RESET_ALL_VARPS(0x05, 0, Category.CLIENT_STATE),
    LOC_PREFETCH(0x06, 7, Category.ZONE_UPDATES),
    RESET_ENTITY_LISTS(0x07, 0, Category.MISC),
    IF_SETPLAYERHEAD_ACTIVE(0x08, 5, Category.INTERFACES),
    CLANCHANNEL_FULL_CHAT(0x09, -2, Category.CHAT),
    UNKNOWN_10(0x0a, 3, Category.MISC),                     // FUN_00119870 — player varbit/varp setter family
    MESSAGE_PUBLIC(0x0b, -2, Category.CHAT),
    SET_PLAYER_OP_2(0x0c, 2, Category.MISC),
    SET_PLAYER_OP_3(0x0d, 1, Category.MISC),
    IF_SETSPRITE(0x0e, 8, Category.INTERFACES),
    UNKNOWN_15(0x0f, 11, Category.MISC),                    // FUN_001dade0 — animated location / world flag
    LOC_DEL(0x10, 2, Category.ZONE_UPDATES),
    UNKNOWN_17(0x11, -1, Category.MISC),                    // FUN_0013eec0 — FRIENDLIST_ADD-style update
    CAM_TARGET(0x12, 1, Category.CAMERA),
    URL_OPEN(0x13, -2, Category.MISC),                     // FUN_000f7080 — two-part WordPack URL -> system(xdg-open); ref binds WebPage lambda#2 -> URL_OPEN. NOTE: disabled decoder modeled URL_OPEN as 3B urlId; 948-5 is a var-length string
    UNKNOWN_20(0x14, 3, Category.MISC),                     // FUN_001741b0 — SET_AREA_FLAG candidate
    LOC_ANIM_SPECIFIC(0x15, 10, Category.ZONE_UPDATES),
    PLAYER_INFO(0x16, -2, Category.PLAYER_INFO),            // NOTE: CSV said PlayerList::ProcessPlayerInfo but the actual handler at 0x001a39d0 is CLANCHANNEL_DELTA — real ProcessPlayerInfo is at 0x00161720. Opcode 0x16 dispatches PlayerInfo regardless.
    PLAYER_OP(0x17, -2, Category.PLAYER_GROUP),
    UPDATE_ZONE_PARTIAL(0x18, -1, Category.CLIENT_STATE),
    MESSAGE_BROADCAST(0x19, -2, Category.CHAT),             // FUN_001898a0 per Phase 4b-5
    UPDATE_SITESETTINGS_THUNK(0x1a, -2, Category.SITE_SETTINGS), // FUN_001a63e0 — thunk to SiteSettings::UPDATE_SITESETTINGS
    NPC_HITMARK(0x1b, 10, Category.COMBAT),                 // FUN_00187490 — Hitmark factory via FUN_00b0c5d0
    SET_VARP_INT(0x1c, 6, Category.VARIABLES),              // FUN_00119910
    CLANSETTINGS_FULL(0x1d, -2, Category.CLANS),
    IF_SET2DANGLE(0x1e, 8, Category.INTERFACES),
    MINIMAP_HIGHLIGHT_UPDATE(0x1f, 6, Category.MISC),       // FUN_00186f40
    IF_SETCOLOUR(0x20, 8, Category.INTERFACES),
    MESSAGE_CHANNEL_GAME(0x21, -1, Category.CHAT),          // FUN_001a0f40
    SOUND_AREA_SYNTH(0x22, 8, Category.AUDIO),
    IF_SETEVENTS2(0x23, 12, Category.INTERFACES),
    MAP_HIT_OVERLAY(0x24, 10, Category.MISC),               // FUN_00186c60
    MESSAGE_FRIENDCHANNEL(0x25, -1, Category.CHAT),
    IF_SET_MODEL_FRAME(0x26, 8, Category.INTERFACES),
    IF_OPENTOP(0x27, 6, Category.INTERFACES),
    IF_SUBSWAP(0x28, 8, Category.INTERFACES),
    UPDATE_ZONE_PARTIAL_FOLLOWS(0x29, 3, Category.ZONE_UPDATES),
    PLAYER_HITMARK(0x2a, 10, Category.COMBAT),              // FUN_001875f0 — Hitmark factory (player variant)
    PROJANIM_AT_TARGET(0x2b, 12, Category.SPOT_ANIM),       // FUN_00157080
    UPDATE_STAT(0x2c, 6, Category.MISC),                    // jag::game::StatTable::UpdateStat
    SET_MULTIWAY_STATE(0x2d, 1, Category.MISC),
    OBJ_ADD(0x2e, 5, Category.ZONE_UPDATES),
    SET_VARP_SMALL(0x2f, 3, Category.VARIABLES),            // FUN_001196f0
    SET_VARBIT_SMALL(0x30, 3, Category.VARIABLES),          // FUN_00119560
    CHANGE_LOBBY(0x31, -2, Category.MISC),
    LOC_CUSTOMISE(0x32, -1, Category.ZONE_UPDATES),
    SET_VARBIT_INT_ALT(0x33, 6, Category.VARIABLES),        // FUN_001197b0
    UNKNOWN_52(0x34, -2, Category.MISC),                    // 3-line vtable trampoline stub
    UNBOUND_53(0x35, -2, Category.MISC),                    // CSV says UNBOUND — no handler registered
    NOOP_VAR_B(0x36, -1, Category.MISC),                    // FUN_000ef260 — empty NOOP
    DESTROY_ZONE_DATA(0x37, 0, Category.CLIENT_STATE),
    MESSAGE_QUICKCHAT_PRIVATE(0x38, -1, Category.CHAT),
    MINIMAP_HIGHLIGHT_ADD(0x39, 6, Category.MISC),          // FUN_00186e60
    RESET_CLIENT_STATE(0x3a, 0, Category.CLIENT_STATE),
    IF_SETNPCMODEL(0x3b, 10, Category.INTERFACES),
    IF_SETPLAYERMODEL_SELF(0x3c, 25, Category.INTERFACES),
    SET_VARBIT_NEG(0x3d, 3, Category.VARIABLES),            // FUN_001199b0
    IF_CLOSESUB_ACTIVE(0x3e, 4, Category.INTERFACES),
    LOC_ADD_CHANGE(0x3f, 10, Category.ZONE_UPDATES),       // FUN_0014ed40 — loc spawn at absolute coord via jag::game::LocationContainer::Add (was PROJANIM_FIXED, wrong: not a projectile). NOTE: 948-5 layout [shape/flag/u32 id/u32 coord] differs from the disabled decoder's packed LOC_ADD_CHANGE — confirm exact loc opcode in Phase 3
    SET_VARP_INT_VARIANT(0x40, 6, Category.VARIABLES),      // FUN_00119600
    MAP_PROJANIM(0x41, 20, Category.ZONE_UPDATES),
    TARGET_SET_COORD(0x42, 5, Category.MISC),               // FUN_00187160
    CLANCHANNEL_FULL(0x43, -2, Category.CLANS),
    IF_SETMODELORIGIN(0x44, 10, Category.INTERFACES),
    SET_VARBIT_INT(0x45, 6, Category.VARIABLES),            // FUN_001194b0
    IF_SETPLAYERMODEL_OTHER(0x46, 25, Category.INTERFACES),
    OBJ_REVEAL(0x47, 7, Category.ZONE_UPDATES),
    CAM_FORCEANGLE(0x48, 1, Category.CAMERA),
    UNKNOWN_73(0x49, 2, Category.CHAT),                    // FUN_00175990 — writes ChatHistory(Client+0x19408)+0x10/+0x14; not camera (was SET_2X1_OFFSET/CAMERA)
    SET_DISPLAY_INT(0x4a, 4, Category.MISC),
    SET_READY_FLAG(0x4b, 0, Category.CLIENT_STATE),
    UPDATE_ZONE_PARTIAL_ENCLOSED(0x4c, -2, Category.ZONE_UPDATES),
    CAM_UPDATE(0x4d, -2, Category.CAMERA),
    UPDATE_ZONE_FULL_FOLLOWS(0x4e, 3, Category.ZONE_UPDATES),
    NPC_HEADICON_SPECIFIC(0x4f, -1, Category.NPC_INFO),
    SET_RUN_ENERGY(0x50, 1, Category.MISC),
    REBUILD_NORMAL_SIMPLE(0x51, -2, Category.CLIENT_STATE),
    IF_SETPOSITION(0x52, 23, Category.INTERFACES),
    REBUILD_INSTANCE(0x53, -2, Category.REBUILD),           // FUN_001def40 per Phase 4b-5
    IF_SETOBJECT(0x54, 10, Category.INTERFACES),
    UPDATE_INV_FULL(0x55, -2, Category.INVENTORY),          // packethandlers::Inventory::UPDATE_INV_FULL_impl
    IF_SETANIM(0x56, 10, Category.INTERFACES),
    CAM_RESET(0x57, 0, Category.CAMERA),
    CAM_LOOKAT(0x58, 4, Category.CAMERA),                  // FUN_001daf00 — set/clear camera look-at target coord (writes jag::game::Camera +0x5e8/+0x5ec/+0x5f4, -1 clears). NOTE: disabled decoder had CAM_LOOKAT at 6B; op0x6f (6B controller call) is the likely CAM_MOVETO
    NPC_OPERATION_BLOCK(0x59, 19, Category.MISC),           // FUN_000f1f70
    LOC_ADD(0x5a, -1, Category.ZONE_UPDATES),
    IF_SETHIDE(0x5b, 5, Category.INTERFACES),
    SET_VARC_STR_SMALL(0x5c, -1, Category.MISC),
    MESSAGE_GAME(0x5d, -1, Category.CHAT),
    IF_OPENSUB(0x5e, 8, Category.INTERFACES),
    MIDI_SONG(0x5f, 5, Category.AUDIO),
    IF_SETANIM_ACTIVE(0x60, 4, Category.INTERFACES),
    IF_SETEVENTS(0x61, 10, Category.INTERFACES),
    PROJECTILE_LARGE(0x62, 25, Category.SPOT_ANIM),         // FUN_000f25d0
    IF_SETRECOL(0x63, 6, Category.INTERFACES),
    RUNCLIENTSCRIPT_BY_VARC(0x64, 4, Category.MISC),        // FUN_00187020
    IF_SETOBJECT_ACTIVE(0x65, 4, Category.INTERFACES),
    IF_SETMODEL(0x66, 8, Category.INTERFACES),
    IF_SETGRAPHIC(0x67, 8, Category.INTERFACES),
    PLAYER_INFO_DECODE(0x68, 14, Category.PLAYER_INFO),
    MESSAGE_CLANCHANNEL(0x69, -1, Category.CHAT),
    SET_NPC_UPDATE_ORIGIN(0x6a, 1, Category.NPC_INFO),
    OBJ_DEL(0x6b, 3, Category.ZONE_UPDATES),
    CLANSETTINGS_DELTA(0x6c, -2, Category.CLANS),
    HANDSHAKE_UID(0x6d, 28, Category.PLAYER_INFO),
    RUNCLIENTSCRIPT(0x6e, -2, Category.MISC),               // thunk_FUN_001d2060 — verified via librs2client.so ref match
    CAMERA_MOVETO_VARIANT(0x6f, 6, Category.CAMERA),        // FUN_00175960
    SET_TICK_TIMER(0x70, 2, Category.CLIENT_STATE),
    MAP_ANIM(0x71, 11, Category.ZONE_UPDATES),
    CHAT_FILTER_SETTINGS(0x72, -1, Category.CHAT),
    IF_SETNPCHEAD(0x73, 10, Category.INTERFACES),
    SET_VARP_STRING(0x74, -2, Category.VARIABLES),          // FUN_001500a0
    CLANCHANNEL_DELTA(0x75, -1, Category.CLANS),
    IF_SETPLAYERMODEL_SNAPSHOT(0x76, 29, Category.INTERFACES),
    CUTSCENE_DATA(0x77, 35, Category.MISC),
    CAM_SMOOTHRESET(0x78, 0, Category.CAMERA),
    UPDATE_INV_PARTIAL(0x79, -2, Category.INVENTORY),
    IF_SETTEXT(0x7a, -2, Category.INTERFACES),
    IF_TRIGGER_CLOSE(0x7b, 0, Category.INTERFACES),
    CLANSETTINGS_DELTA_CHAT(0x7c, -2, Category.CHAT),
    OBJ_COUNT(0x7d, 7, Category.ZONE_UPDATES),
    MESSAGE_FRIENDCHAT(0x7e, -1, Category.CHAT),
    UNKNOWN_127(0x7f, 0, Category.AUDIO),                  // FUN_00182790 — mass sound fade via AudioManager(Client+0x195b8, shared w/ SOUND_STOP); the "8" is a call-arg, not a login state
    NOOP_VAR_A(0x80, 0, Category.MISC),                     // FUN_00173e40 — empty NOOP
    UNKNOWN_129(0x81, 3, Category.MISC),                   // FUN_001dc7f0 — world effect-emitter override (+0x152/+0x153) via SceneManager->World registry; NOT an NPC (was NPC_SET_VIS_FLAG_A)
    UPDATE_IGNORELIST(0x82, -1, Category.SOCIAL),
    INVENTORY_MOVE_ENTRY(0x83, 3, Category.INVENTORY),      // FUN_000f9600
    SOUND_STOP(0x84, 2, Category.AUDIO),
    SET_NPC_UPDATE_FLAG(0x85, 1, Category.NPC_INFO),
    NPC_OVERHEAD_MESSAGE(0x86, -2, Category.NPC_INFO),      // FUN_00179460
    NOOP_VAR_C(0x87, -2, Category.MISC),                    // FUN_001758f0 — empty NOOP
    IF_SETANIM_SMALL(0x88, 5, Category.INTERFACES),
    SET_CHAT_FILTER_A(0x89, 1, Category.CHAT),
    SKIP_2_BYTES(0x8a, 2, Category.MISC),
    LOGOUT_TRANSFER(0x8b, 0, Category.MISC),
    PROJECTILE_HOME(0x8c, 17, Category.SPOT_ANIM),          // FUN_000f2280
    IF_OPENSUB_THUNK(0x8d, -2, Category.INTERFACES),
    SET_URL_STRING(0x8e, -1, Category.MISC),
    INVENTORY_SLOT_INSERT(0x8f, 6, Category.INVENTORY),     // FUN_00119e20
    INVENTORY_SLOT_REMOVE(0x90, 2, Category.INVENTORY),     // FUN_00119c30
    UI_DISPATCH_ACTION(0x91, 2, Category.MISC),             // FUN_00187890
    INVENTORY_REMOVE_SLOT_2(0x92, 2, Category.INVENTORY),   // FUN_000f98f0
    SET_VARP_LONG_ALT(0x93, 10, Category.VARIABLES),        // FUN_00141510
    IF_CLOSESUB_BY_ID(0x94, 2, Category.INTERFACES),
    INVENTORY_SET_SLOT(0x95, 6, Category.INVENTORY),        // FUN_000f9a20
    UNKNOWN_150(0x96, 3, Category.MISC),                   // FUN_001db7e0 — world effect-emitter override (+0x156/+0x157) via SceneManager; NOT an NPC
    PROJANIM_SPECIFIC(0x97, 21, Category.ZONE_UPDATES),
    IF_SET_HTTP_IMAGE(0x98, -1, Category.INTERFACES),
    UNKNOWN_153(0x99, 4, Category.MISC),                   // FUN_001dc2c0 — world effect-emitter float (+0x158) via SceneManager; NOT an NPC (was NPC_SET_OVERLAY_SCALE)
    UNKNOWN_154(0x9a, 5, Category.MISC),                   // FUN_001de870 — world effect-emitter float tween (+0x68..+0x7C) via SceneManager; NOT an NPC (was NPC_SET_RUN_PROGRESS)
    LOGOUT(0x9b, 0, Category.MISC),
    SET_CHAT_FILTER_B(0x9c, 1, Category.CHAT),
    UNKNOWN_157(0x9d, 1, Category.CAMERA),                 // FUN_00173bd0 — writes a bool to jag::game::Camera+0x620; not a run flag (was SET_RUN_FLAG)
    IF_SETSCROLLSIZE(0x9e, 9, Category.INTERFACES),
    NOOP_VAR_D(0x9f, -2, Category.MISC),                    // FUN_000ec3a0 — empty NOOP
    NPC_SET_EXTENDED_ANIM(0xa0, 9, Category.NPC_INFO),      // FUN_001d76a0
    UPDATE_PLAYER_GROUP(0xa1, -2, Category.PLAYER_GROUP),
    TRIGGER_ONDIALOGABORT(0xa2, 0, Category.CLIENT_STATE),
    SPOTANIM_AT_TARGET(0xa3, 15, Category.SPOT_ANIM),       // FUN_001db050
    MAP_PROJANIM_HALT(0xa4, 28, Category.ZONE_UPDATES),
    IF_SETMODEL_COORD(0xa5, 14, Category.INTERFACES),
    WORLDLIST_INSERT_OR_REPLACE(0xa6, 5, Category.WORLD_ENTITY), // FUN_00120150
    SYNTH_SOUND(0xa7, 12, Category.AUDIO),
    SOUND_AREA(0xa8, -1, Category.ZONE_UPDATES),
    SERVER_TICK_END(0xa9, 8, Category.MISC),
    LOC_MERGE(0xaa, 5, Category.ZONE_UPDATES),
    INVENTORY_CLEAR_SLOT(0xab, 3, Category.INVENTORY),      // FUN_000ef800
    SET_PLAYER_FLAG_A(0xac, 1, Category.MISC),              // FUN_000f03f0
    UNKNOWN_OP173(0xad, -2, Category.ZONE_UPDATES),         // packethandlers::ZoneUpdates::UNKNOWN_op173_handler
    UNKNOWN_174(0xae, 8, Category.MISC),                    // 3-line vtable trampoline stub (NOT HandleAntiCheatChallenge despite CSV label)
    MESSAGE_PRIVATE_ECHO(0xaf, -1, Category.CHAT),
    SET_INTERACTION_FLAG_D(0xb0, 3, Category.MISC),
    PROJANIM_SPECIFIC_HALT(0xb1, 29, Category.ZONE_UPDATES),
    UPDATE_PLAYER_CHAT(0xb2, -2, Category.PLAYER_INFO),
    IF_SETSCROLLPOS(0xb3, 9, Category.INTERFACES),
    IF_SETOBJECT_SMALL(0xb4, 5, Category.INTERFACES),
    SCRIPT_TRIGGER_A(0xb5, 4, Category.MISC),               // FUN_00186b60
    UNKNOWN_182(0xb6, 3, Category.MISC),                   // FUN_001dbd40 — world effect-emitter bool override (+0x154/+0x155) via SceneManager->World registry; NOT an NPC (verified; was NPC_SET_DRAW_QUEUE_FLAG)
    MAP_ANIM_SPECIFIC(0xb7, 14, Category.ZONE_UPDATES),
    SET_SYSUPDATE_TIMER(0xb8, 4, Category.MISC),
    MESSAGE_PRIVATE(0xb9, -1, Category.CHAT),
    REBUILD_WORLDENTITY(0xba, -2, Category.CLIENT_STATE),
    CREATE_CHECK_NAME_REPLY(0xbb, 1, Category.MISC),       // FUN_00173e50 — account-creation name-availability reply (validate mask 0xfe3 -> field +0x38). Ref binding chain + disabled decoder agree
    SOCIAL_NETWORK_LOGOUT(0xbc, -2, Category.SOCIAL),      // FUN_000f7090 — single WordPack URL -> system(xdg-open); ref binds WebPage lambda#3 -> SOCIAL_NETWORK_LOGOUT
    VORBIS_PRELOAD(0xbd, 4, Category.AUDIO),
    CLEAR_PENDING_UPDATES(0xbe, 0, Category.CLIENT_STATE),
    SCRIPT_TRIGGER_B(0xbf, 4, Category.MISC),               // FUN_00186be0
    CREATE_CHECK_EMAIL_REPLY(0xc0, 1, Category.MISC),      // FUN_00173f60 — account-creation email-availability reply (validate mask 0x1800063 -> field +0x34). Ref binding chain + disabled decoder agree
    SET_INTERACTION_FLAG_C(0xc1, 1, Category.MISC),
    REMOVE_INVENTORY(0xc2, 1, Category.INVENTORY),          // FUN_000efa80
    SOUND_AREA_SYNTH_2(0xc3, 4, Category.AUDIO),
    SET_VARP_LONG(0xc4, 10, Category.VARIABLES),            // FUN_00119350
    SKIP_DATA(0xc5, -2, Category.MISC),
    UPDATE_URL_STRING(0xc6, -1, Category.MISC),
    REBUILD_NORMAL(0xc7, -2, Category.CLIENT_STATE),
    SOUND_GROUP_STOP(0xc8, 2, Category.AUDIO),
    INVENTORY_SET_NEGATIVE(0xc9, 3, Category.INVENTORY),    // FUN_000ef970
    PLAYER_INFO_DECODE_2(0xca, -2, Category.PLAYER_INFO),
    UNKNOWN_203(0xcb, 3, Category.MISC),                   // FUN_001dd5a0 — world effect-emitter bool override (+0x150/+0x151) via SceneManager; NOT an NPC (was NPC_SET_VIS_FLAG_B)
    SET_PLAYER_FLAG_B(0xcc, 1, Category.MISC),              // FUN_000f0380
    SOUND_GROUP_SPEED(0xcd, 6, Category.AUDIO),
    IF_SETNPCHEAD_ACTIVE(0xce, 5, Category.INTERFACES),
    INVENTORY_SLOT_SWAP(0xcf, 3, Category.INVENTORY),       // FUN_00119b80
    UPDATE_INV_GROUP(0xd0, -2, Category.INVENTORY),
    NPC_INFO_THUNK(0xd1, -2, Category.NPC_INFO),
    VORBIS_SONG(0xd2, 6, Category.AUDIO),
    PROJANIM_NPC_TARGETED(0xd3, 33, Category.SPOT_ANIM),    // FUN_000f1420
    SET_WORLD_TARGET(0xd4, -1, Category.WORLD_DATA),
    SWITCH_WORLD(0xd5, -1, Category.WORLD_DATA),
    UNKNOWN_214(0xd6, 0, Category.AUDIO),                  // FUN_001827d0 — stop current sound/song via AudioManager(Client+0x195b8); not a login ping
    UNKNOWN_215(0xd7, 8, Category.MISC),                   // FUN_001dec60 — world effect-emitter u32 tween (+0x4C..+0x64) via SceneManager; NOT an NPC (was NPC_SET_COLOR_TINT)
    UNKNOWN_216(0xd8, -2, Category.MISC),                   // FUN_0018fea0
    SOUND_MODIFY(0xd9, 4, Category.AUDIO);

    enum class Category {
        CHAT, VARIABLES, ZONE_UPDATES, INTERFACES, AUDIO, MISC, NPC_INFO,
        PLAYER_INFO, SOCIAL, CAMERA, CLIENT_STATE, CLANS, INVENTORY,
        PLAYER_GROUP, WORLD_DATA, WORLD_ENTITY, REBUILD, SPOT_ANIM, COMBAT, SITE_SETTINGS
    }

    companion object {
        private val BY_OPCODE = entries.associateBy { it.opcode }

        fun forOpcode(opcode: Int): ServerProt? = BY_OPCODE[opcode]
    }
}
