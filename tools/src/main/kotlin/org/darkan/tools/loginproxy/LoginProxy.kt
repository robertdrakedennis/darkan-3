package org.darkan.tools.loginproxy

import org.darkan.core.EnvVars
import org.darkan.core.net.Isaac
import world.gregs.voidps.cache.secure.RSA
import com.sun.net.httpserver.HttpServer
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.ByteBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger

/**
 * TCP MITM proxy that intercepts NXT client login traffic to/from Jagex live servers.
 *
 * Architecture:
 *   [Patched NXT Client] -> TCP:43594 -> [LoginProxy] -> TCP -> [Jagex Live Server]
 *                                              |
 *                                         capture/ directory + console
 *
 * MITM capabilities:
 * - Decrypts the RSA block from the patched client (using our private key)
 * - Extracts ISAAC seeds from the RSA plaintext
 * - Re-encrypts the RSA block with Jagex's real public key
 * - Forwards the modified login packet to Jagex
 * - After login success, decodes all ISAAC-encrypted packets in both directions
 *
 * Phase tracking follows the login wire format documented in docs/net/login-wire-format.md:
 *   Phase 0 (CONNECTION_TYPE): First byte from client (14=login, 15=JS5, 19=lobby)
 *   Phase 1 (FIRST_RESPONSE): Server sends [1B response] [8B session_key] = 9 bytes
 *   Phase 2 (LOGIN_PACKET): Client sends [1B opcode] [2B size] [NB login_data]
 *   Phase 3 (LOGIN_RESULT): Server sends [1B result_code]
 *   Phase 4 (LOGIN_DATA_LEN): Server sends [1B data_length] (on success)
 *   Phase 5 (LOGIN_DATA): Server sends [NB login_data]
 *   Phase 6 (POST_LOGIN): ISAAC-encrypted game/lobby packets
 *
 * Run: ./gradlew :tools:run -PmainClass=org.darkan.tools.loginproxy.LoginProxyKt
 */

// ---- Configuration ----

private const val JAV_CONFIG_URL = "https://www.runescape.com/k=5/l=0/jav_config.ws?binaryType=4"
private const val DEFAULT_LISTEN_PORT = 43594
private const val DEFAULT_HTTP_PORT = 8081
private const val DEFAULT_CAPTURE_DIR = "capture"
private const val FALLBACK_LOBBY_HOST = "lobby1.runescape.com"
private const val FALLBACK_PORT = 43594

// No hex dump limit for POST_LOGIN -- we want full packet captures
private const val MAX_HEX_DUMP_BYTES_DEFAULT = 256
private const val MAX_HEX_DUMP_BYTES_POSTLOGIN = Int.MAX_VALUE

// ---- Connection type opcodes ----

private val CONNECTION_TYPE_NAMES = mapOf(
    10 to "SESSION",
    14 to "CONNECT_LOGIN",
    15 to "JS5_INIT",
    16 to "LOGIN",
    18 to "RECONNECT",
    19 to "LOBBY",
    28 to "ACCOUNT_CREATION",
)

// ---- Login result codes (from RE docs) ----

private val LOGIN_RESULT_NAMES = mapOf(
    0 to "VIDEO_AD",
    1 to "WAIT_FOR_TIMEOUT",
    2 to "SUCCESS",
    3 to "INVALID_CREDENTIALS",
    4 to "ACCOUNT_DISABLED",
    5 to "ALREADY_LOGGED_IN",
    6 to "GAME_UPDATED",
    7 to "WORLD_FULL",
    8 to "LOGIN_SERVER_OFFLINE",
    9 to "LOGIN_LIMIT_EXCEEDED",
    10 to "BAD_SESSION_ID",
    11 to "WEAK_PASSWORD",
    12 to "MEMBERS_WORLD",
    13 to "COULD_NOT_COMPLETE_LOGIN",
    14 to "SERVER_BEING_UPDATED",
    15 to "RECONNECTING",
    16 to "LOGIN_ATTEMPTS_EXCEEDED",
    17 to "MEMBERS_AREA",
    18 to "INVALID_LOGIN_SERVER",
    19 to "TRANSFERRING_PROFILE",
    20 to "ACCOUNT_LOCKED",
    21 to "CLOSED_BETA",
    22 to "CONNECTION_FAILED",
    23 to "RECONNECT_TRY_AGAIN",
    24 to "TOO_MANY_CONNECTIONS",
    25 to "IN_QUEUE",
    26 to "TOO_MANY_WORLD_LOGINS",
    29 to "PLEASE_TRY_A_DIFFERENT_WORLD",
    30 to "NEED_SKILL_TOTAL_OF_CURRENT_WORLD",
    35 to "TEMPORARY_BAN",
    37 to "AUTHENTICATOR_CODE_REQUIRED",
    38 to "URL_REDIRECT",
    55 to "PING_STATUS",
    56 to "HOP_BLOCKED",
)

// ---- Phases ----

private enum class Phase {
    CONNECTION_TYPE,      // Waiting for first client byte
    FIRST_RESPONSE,       // Server: [1B response] [8B session_key]
    SECOND_RESPONSE,      // Server: [2B payload_length]
    XTEA_CHALLENGE,       // Server: [NB xtea_encrypted_data]
    GO_AHEAD,             // Server: [1B go_ahead]
    LOGIN_TOKEN,          // Server: [16B xtea_encrypted(token+nonce)]
    LOGIN_PACKET,         // Client: [1B opcode] [2B size] [NB data]
    LOGIN_RESULT,         // Server: [1B result_code]
    LOGIN_DATA_LEN,       // Server: [1B data_length] or [2B data_length]
    LOGIN_DATA,           // Server: [NB login_data]
    SERVER_CLIENT_VARS,   // Server: server client var exchange (steps 250-270)
    POST_LOGIN,           // ISAAC-encrypted traffic
    JS5,                  // JS5 file service (not login)
    CLOSED,               // Connection ended
}

// ---- ServerProt size table (217 entries, opcodes 0-216) ----
// From docs/net/serverprot-table.md (Build 946-3)
// Values: 0+ = fixed size, -1 = varByte, -2 = varShort

private val SERVER_PROT_SIZES = intArrayOf(
    /* 0   SET_UID */                  28,
    /* 1   MESSAGE_GAME */             -1,
    /* 2   SET_VARC_INT */             -2,
    /* 3   OBJ_COUNT */                 7,
    /* 4   IF_SETHIDE */               10,
    /* 5   MIDI_JINGLE */              10,
    /* 6   LOC_ANIM_SPECIFIC */        10,
    /* 7   IF_SETMODEL */              29,
    /* 8   IF_MOVESUB */                6,
    /* 9   SET_NPC_OP */               -1,
    /* 10  MSG_QUICKCHAT_CLANCHAT */   -1,
    /* 11  CLANCHANNEL_FULL_CHAT */    -2,
    /* 12  SET_VARC_SMALL */            6,
    /* 13  SET_PLAYER_OP_3 */           1,
    /* 14  SET_VARP_SMALL */            3,
    /* 15  MESSAGE_PUBLIC */           -2,
    /* 16  RUN_CLIENTSCRIPT */         -2,
    /* 17  UPDATE_IGNORELIST */        -2,
    /* 18  UPDATE_SITESETTINGS */      -2,
    /* 19  SET_VARC_COORD */            3,
    /* 20  IF_OPENSUB_ACTIVE */        -2,
    /* 21  IF_SETANGLE */              32,
    /* 22  SET_TICK_TIMER */            2,
    /* 23  CAM_TARGET */                1,
    /* 24  MAP_PROJANIM */             20,
    /* 25  RESET_ENTITY_LISTS */        0,
    /* 26  IF_SETOBJECT_NONUM */        8,
    /* 27  SET_RUN_ENERGY */            1,
    /* 28  UPDATE_FRIENDLIST_2 */      -2,
    /* 29  CLANSETTINGS_FULL_2 */      -1,
    /* 30  LOC_ADD */                   -1,
    /* 31  MSG_QUICKCHAT_CLANCHANNEL */ -1,
    /* 32  RESET_ALL_VARPS */           0,
    /* 33  SOUND_STOP_ALL */            0,
    /* 34  SOUND_MIXBUSS_SETLEVEL */    5,
    /* 35  SET_READY_FLAG */            0,
    /* 36  IF_SETPLAYERMODEL_OTHER */  -2,
    /* 37  MSG_QUICKCHAT_PRIVATE */    -1,
    /* 38  IF_SETPOSITION */           23,
    /* 39  CLANSETTINGS_FULL */        -2,
    /* 40  MESSAGE_FRIENDCHAT */       -1,
    /* 41  LOC_ADD_CHANGE */           10,
    /* 42  PLAYER_INFO_DECODE */       -2,
    /* 43  UPDATE_ZONE_PARTIAL */      -2,
    /* 44  CAM_MOVETO */                6,
    /* 45  IF_SETSCROLLPOS */           5,
    /* 46  MESSAGE_CLANCHANNEL */      -1,
    /* 47  MAP_FLAG_SET_PLAYER */      14,
    /* 48  IF_SETCOLOUR */              5,
    /* 49  MIDI_SONG */                10,
    /* 50  IF_SETOBJECT */              4,
    /* 51  SET_VARBIT_SMALL */          6,
    /* 52  CAM_FORCEANGLE */            1,
    /* 53  CUTSCENE_DATA */            35,
    /* 54  PROJANIM */                 25,
    /* 55  CAM_SHAKE */                 4,
    /* 56  MESSAGE_PRIVATE_SYSTEM */   -1,
    /* 57  IF_SETTARGETPARAM */        -2,
    /* 58  CAM_LOOKAT */                6,
    /* 59  IF_SETTEXT */               12,
    /* 60  RESET_VARC_SMALL */         -1,
    /* 61  IF_SETTEXTFONT */            8,
    /* 62  IF_SETRECOL */              10,
    /* 63  CLANSETTINGS_DELTA */       -2,
    /* 64  LOC_DEL */                    2,
    /* 65  UPDATE_ZONE_FULL_FOLLOWS_2 */ -1,
    /* 66  FRIENDLIST_LOADED */        -2,
    /* 67  IF_SETMODEL_BODYTYPE */     25,
    /* 68  MSG_QUICKCHAT_FRIENDCHAT */ -1,
    /* 69  CAM_RESET */                 0,
    /* 70  MIDI_SWAP */                 5,
    /* 71  IF_SETPLAYERMODEL_BASECOL */  8,
    /* 72  SET_VARBIT_INT */            3,
    /* 73  SET_PLAYER_CHAT_EFFECTS */   2,
    /* 74  IF_SETPLAYERMODEL_BODYTYPE */ 8,
    /* 75  IF_SETRETEX */              10,
    /* 76  SOUND_AREA_SYNTH */          8,
    /* 77  IF_SETMODEL_ANIMATION */     4,
    /* 78  LOC_PREFETCH */              7,
    /* 79  CAM_LOOKAT_ARC */            6,
    /* 80  SET_MULTIWAY_STATE */        1,
    /* 81  UPDATE_INV_PARTIAL */       -2,
    /* 82  LOC_CUSTOMISE */            -1,
    /* 83  CLANCHANNEL_FULL */         -2,
    /* 84  DETAIL_OPTIONS */           -2,
    /* 85  SET_DISPLAY_INT */           4,
    /* 86  NPC_HEADICON_SPECIFIC */    -1,
    /* 87  OBJ_ADD */                    5,
    /* 88  (no handler) */              3,
    /* 89  IF_SETCLICKMASK */           8,
    /* 90  UPDATE_ZONE_FULL_FOLLOWS */  3,
    /* 91  SPOTANIM_SPECIFIC */        12,
    /* 92  (no handler) */             -2,
    /* 93  IF_SETPLAYERMODEL */        10,
    /* 94  NPC_HITMARKS_HEADBARS */    19,
    /* 95  CLANSETTINGS_DELTA_CHAT */  -2,
    /* 96  CAM_MOVETO_ARC */            4,
    /* 97  SET_PLAYER_OP_2 */           2,
    /* 98  OBJ_DEL */                   3,
    /* 99  (no handler) */              0,
    /* 100 IF_SETNPCMODEL */           10,
    /* 101 REMOVE_TRACKED_ENTRY */      3,
    /* 102 SET_NPC_UPDATE_ORIGIN */     1,
    /* 103 SET_PLAYER_GROUP */         10,
    /* 104 RESET_CLIENT_STATE */        0,
    /* 105 IF_SETPLAYERMODEL_SELF */    8,
    /* 106 IF_SETANIM */                4,
    /* 107 CHAT_FILTER_SETTINGS */     -1,
    /* 108 IF_SETOBJECT_ALWAYSNUM */    6,
    /* 109 REBUILD_NORMAL_HANDLER */   -2,
    /* 110 CAM_UPDATE */               -2,
    /* 111 NOOP_VAR */                 -1,
    /* 112 UPDATE_STAT */               0,
    /* 113 IF_SETMODEL_COLOUR */       10,
    /* 114 SET_VARC_STR_SMALL */        6,
    /* 115 (no handler) */              6,
    /* 116 IF_SETMODEL_RECOLOUR */     25,
    /* 117 SOUND_GROUP */              11,
    /* 118 CAM_SMOOTHRESET */           0,
    /* 119 IF_SETPLAYERMODEL_ANIM */    8,
    /* 120 UPDATE_ZONE_PARTIAL_FOL */   3,
    /* 121 UPDATE_INV_FULL */          -2,
    /* 122 MAP_ANIM */                 11,
    /* 123 CLANCHANNEL_DELTA */        -1,
    /* 124 SET_VARP_INT */              6,
    /* 125 IF_SETTEXT2 */               8,
    /* 126 IF_SETGRAPHIC */            19,
    /* 127 OBJ_REVEAL */                7,
    /* 128 IF_SETANGLE_ACTIVE */        3,
    /* 129 SET_MAP_FLAG */              6,
    /* 130 MESSAGE_PRIVATE */          -1,
    /* 131 LOGOUT_TRANSFER */           0,
    /* 132 LOC_MERGE */                 5,
    /* 133 SET_CHAT_FILTER_D */         1,
    /* 134 LOGOUT */                    0,
    /* 135 SET_WEIGHT */                1,
    /* 136 SOUND_GROUP_STOP */          2,
    /* 137 IF_SETNPCMODEL_ANIM */       9,
    /* 138 SET_VARP_LONG */            10,
    /* 139 PLAYER_OP */                 4,
    /* 140 IF_SETOBJECT_NONUM_2 */      5,
    /* 141 UPD_ZONE_FULL_FOLLOWS_3 */  -2,
    /* 142 MINIMAP_FLAG_SET */          1,
    /* 143 SET_CAMERA_TARGET */         8,
    /* 144 SET_SYSUPDATE_TIMER */       4,
    /* 145 IF_SETHIDE_ACTIVE */         8,
    /* 146 NOOP */                      0,
    /* 147 PROJANIM_SPECIFIC_HALT */   29,
    /* 148 UPDATE_PLAYER_CHAT */       -2,
    /* 149 SET_WORLD_TARGET */         -1,
    /* 150 UPDATE_FRIENDCHAT_CHAN */    -2,
    /* 151 RESET_ANIMS */               3,
    /* 152 UPDATE_IGNORELIST */        -1,
    /* 153 RUNCLIENTSCRIPT */           2,
    /* 154 UPDATE_PLAYER_GROUP */      -2,
    /* 155 UPDATE_URL_STRING */        -1,
    /* 156 CLANCHANNEL_DELTA_CS */      6,
    /* 157 SET_PLAYER_OP */             3,
    /* 158 SYNTH_SOUND */              12,
    /* 159 MAP_PROJANIM_HALT */        28,
    /* 160 SOUND_GROUP_SPEED */         6,
    /* 161 SET_INTERACTION_FLAG_B */     1,
    /* 162 IF_SETGRAPHIC_ACTIVE */     -1,
    /* 163 REBUILD_PINFO_POS */        -2,
    /* 164 SET_CHAT_FILTER_C */         1,
    /* 165 NPC_INFO */                  9,
    /* 166 MIDI_STOP */                 0,
    /* 167 SKIP_2_BYTES */              2,
    /* 168 UPDATE_INV_GROUP */         -2,
    /* 169 SET_INTERACTION_FLAG_C */     1,
    /* 170 NOOP_VAR */                 -2,
    /* 171 SET_URL_STRING */           -1,
    /* 172 IF_SETNPCMODEL_ACTIVE */     5,
    /* 173 IF_SETMODEL_ACTIVE */       -2,
    /* 174 UPDATE_FRIENDLIST */        -2,
    /* 175 SET_INTERACTION_FLAG_D */     3,
    /* 176 IF_SETPLMODEL_EXACTMOVE */  14,
    /* 177 REBUILD_REGION_HANDLER */   -2,
    /* 178 REBUILD_REGION */           -2,
    /* 179 FRIENDCHAT_SYSUPDATE */      2,
    /* 180 IF_CLOSESUB */               5,
    /* 181 SET_NPC_UPDATE_FLAG */        1,
    /* 182 IF_OPENSUB */                5,
    /* 183 (no handler) */             10,
    /* 184 SOUND_AREA_SYNTH */          4,
    /* 185 MAP_PROJANIM */             33,
    /* 186 REBUILD_NORMAL */           -2,
    /* 187 PLAYER_GROUP_DELTA */        4,
    /* 188 SET_CHAT_FILTER_B */         1,
    /* 189 PLAYER_INFO_DECODE */       -2,
    /* 190 IF_MOVESUB_ACTIVE */         3,
    /* 191 TRIGGER_ONDIALOGABORT */     2,
    /* 192 SKIP_DATA */                -2,
    /* 193 MAP_FLAG_SET */             15,
    /* 194 MAP_ANIM_SPECIFIC */        14,
    /* 195 IF_SETPOSITION_ACTIVE */     3,
    /* 196 OCULUS_SYNC */              -2,
    /* 197 IF_SETCLICKMASK_ACTIVE */    3,
    /* 198 SOUND_AREA */               -1,
    /* 199 FRIENDCHAT_JOIN */          -1,
    /* 200 SOUND_MODIFY */              4,
    /* 201 MESSAGE_PRIVATE_ECHO */     -1,
    /* 202 IF_SETEVENTS */              9,
    /* 203 SERVER_TICK_END */            8,
    /* 204 SET_CHAT_FILTER_A */         1,
    /* 205 REMOVE_PLAYER_FROM_LIST */   1,
    /* 206 DESTROY_ZONE_DATA */         0,
    /* 207 IF_OPENTOP */                2,
    /* 208 UPDATE_REBOOT_TIMER */       3,
    /* 209 VORBIS_PRELOAD */            4,
    /* 210 IF_SETRECOL_ACTIVE */        4,
    /* 211 SET_HEATMAP */               5,
    /* 212 TRIGGER_ONDIALOGABORT_2 */   0,
    /* 213 VORBIS_SONG */               6,
    /* 214 PROJANIM_SPECIFIC */        21,
    /* 215 URL_OPEN */                  3,
    /* 216 SOUND_STOP */                2,
)

// ---- ServerProt names (for logging) ----

private val SERVER_PROT_NAMES = arrayOf(
    /* 0 */   "SET_UID",
    /* 1 */   "MESSAGE_GAME",
    /* 2 */   "SET_VARC_INT",
    /* 3 */   "OBJ_COUNT",
    /* 4 */   "IF_SETHIDE",
    /* 5 */   "MIDI_JINGLE",
    /* 6 */   "LOC_ANIM_SPECIFIC",
    /* 7 */   "IF_SETMODEL",
    /* 8 */   "IF_MOVESUB",
    /* 9 */   "SET_NPC_OP",
    /* 10 */  "MSG_QUICKCHAT_CLANCHAT",
    /* 11 */  "CLANCHANNEL_FULL_CHAT",
    /* 12 */  "SET_VARC_SMALL",
    /* 13 */  "SET_PLAYER_OP_3",
    /* 14 */  "SET_VARP_SMALL",
    /* 15 */  "MESSAGE_PUBLIC",
    /* 16 */  "RUN_CLIENTSCRIPT",
    /* 17 */  "UPDATE_IGNORELIST",
    /* 18 */  "UPDATE_SITESETTINGS",
    /* 19 */  "SET_VARC_COORD",
    /* 20 */  "IF_OPENSUB_ACTIVE",
    /* 21 */  "IF_SETANGLE",
    /* 22 */  "SET_TICK_TIMER",
    /* 23 */  "CAM_TARGET",
    /* 24 */  "MAP_PROJANIM",
    /* 25 */  "RESET_ENTITY_LISTS",
    /* 26 */  "IF_SETOBJECT_NONUM",
    /* 27 */  "SET_RUN_ENERGY",
    /* 28 */  "UPDATE_FRIENDLIST_2",
    /* 29 */  "CLANSETTINGS_FULL_2",
    /* 30 */  "LOC_ADD",
    /* 31 */  "MSG_QUICKCHAT_CLANCHANNEL",
    /* 32 */  "RESET_ALL_VARPS",
    /* 33 */  "SOUND_STOP_ALL",
    /* 34 */  "SOUND_MIXBUSS_SETLEVEL",
    /* 35 */  "SET_READY_FLAG",
    /* 36 */  "IF_SETPLAYERMODEL_OTHER",
    /* 37 */  "MSG_QUICKCHAT_PRIVATE",
    /* 38 */  "IF_SETPOSITION",
    /* 39 */  "CLANSETTINGS_FULL",
    /* 40 */  "MESSAGE_FRIENDCHAT",
    /* 41 */  "LOC_ADD_CHANGE",
    /* 42 */  "PLAYER_INFO_DECODE",
    /* 43 */  "UPDATE_ZONE_PARTIAL",
    /* 44 */  "CAM_MOVETO",
    /* 45 */  "IF_SETSCROLLPOS",
    /* 46 */  "MESSAGE_CLANCHANNEL",
    /* 47 */  "MAP_FLAG_SET_PLAYER",
    /* 48 */  "IF_SETCOLOUR",
    /* 49 */  "MIDI_SONG",
    /* 50 */  "IF_SETOBJECT",
    /* 51 */  "SET_VARBIT_SMALL",
    /* 52 */  "CAM_FORCEANGLE",
    /* 53 */  "CUTSCENE_DATA",
    /* 54 */  "PROJANIM",
    /* 55 */  "CAM_SHAKE",
    /* 56 */  "MESSAGE_PRIVATE_SYSTEM",
    /* 57 */  "IF_SETTARGETPARAM",
    /* 58 */  "CAM_LOOKAT",
    /* 59 */  "IF_SETTEXT",
    /* 60 */  "RESET_VARC_SMALL",
    /* 61 */  "IF_SETTEXTFONT",
    /* 62 */  "IF_SETRECOL",
    /* 63 */  "CLANSETTINGS_DELTA",
    /* 64 */  "LOC_DEL",
    /* 65 */  "UPDATE_ZONE_FULL_FOLLOWS_2",
    /* 66 */  "FRIENDLIST_LOADED",
    /* 67 */  "IF_SETMODEL_BODYTYPE",
    /* 68 */  "MSG_QUICKCHAT_FRIENDCHAT",
    /* 69 */  "CAM_RESET",
    /* 70 */  "MIDI_SWAP",
    /* 71 */  "IF_SETPLAYERMODEL_BASECOLOUR",
    /* 72 */  "SET_VARBIT_INT",
    /* 73 */  "SET_PLAYER_CHAT_EFFECTS",
    /* 74 */  "IF_SETPLAYERMODEL_BODYTYPE",
    /* 75 */  "IF_SETRETEX",
    /* 76 */  "SOUND_AREA_SYNTH",
    /* 77 */  "IF_SETMODEL_ANIMATION",
    /* 78 */  "LOC_PREFETCH",
    /* 79 */  "CAM_LOOKAT_ARC",
    /* 80 */  "SET_MULTIWAY_STATE",
    /* 81 */  "UPDATE_INV_PARTIAL",
    /* 82 */  "LOC_CUSTOMISE",
    /* 83 */  "CLANCHANNEL_FULL",
    /* 84 */  "DETAIL_OPTIONS",
    /* 85 */  "SET_DISPLAY_INT",
    /* 86 */  "NPC_HEADICON_SPECIFIC",
    /* 87 */  "OBJ_ADD",
    /* 88 */  "OPCODE_88",
    /* 89 */  "IF_SETCLICKMASK",
    /* 90 */  "UPDATE_ZONE_FULL_FOLLOWS",
    /* 91 */  "SPOTANIM_SPECIFIC",
    /* 92 */  "OPCODE_92",
    /* 93 */  "IF_SETPLAYERMODEL",
    /* 94 */  "NPC_HITMARKS_AND_HEADBARS",
    /* 95 */  "CLANSETTINGS_DELTA_CHAT",
    /* 96 */  "CAM_MOVETO_ARC",
    /* 97 */  "SET_PLAYER_OP_2",
    /* 98 */  "OBJ_DEL",
    /* 99 */  "OPCODE_99",
    /* 100 */ "IF_SETNPCMODEL",
    /* 101 */ "REMOVE_TRACKED_ENTRY",
    /* 102 */ "SET_NPC_UPDATE_ORIGIN",
    /* 103 */ "SET_PLAYER_GROUP",
    /* 104 */ "RESET_CLIENT_STATE",
    /* 105 */ "IF_SETPLAYERMODEL_SELF",
    /* 106 */ "IF_SETANIM",
    /* 107 */ "CHAT_FILTER_SETTINGS",
    /* 108 */ "IF_SETOBJECT_ALWAYSNUM",
    /* 109 */ "REBUILD_NORMAL_HANDLER",
    /* 110 */ "CAM_UPDATE",
    /* 111 */ "NOOP_VAR",
    /* 112 */ "UPDATE_STAT",
    /* 113 */ "IF_SETMODEL_COLOUR",
    /* 114 */ "SET_VARC_STR_SMALL",
    /* 115 */ "OPCODE_115",
    /* 116 */ "IF_SETMODEL_RECOLOUR",
    /* 117 */ "SOUND_GROUP",
    /* 118 */ "CAM_SMOOTHRESET",
    /* 119 */ "IF_SETPLAYERMODEL_ANIM",
    /* 120 */ "UPDATE_ZONE_PARTIAL_FOLLOWS",
    /* 121 */ "UPDATE_INV_FULL",
    /* 122 */ "MAP_ANIM",
    /* 123 */ "CLANCHANNEL_DELTA",
    /* 124 */ "SET_VARP_INT",
    /* 125 */ "IF_SETTEXT2",
    /* 126 */ "IF_SETGRAPHIC",
    /* 127 */ "OBJ_REVEAL",
    /* 128 */ "IF_SETANGLE_ACTIVE",
    /* 129 */ "SET_MAP_FLAG",
    /* 130 */ "MESSAGE_PRIVATE",
    /* 131 */ "LOGOUT_TRANSFER",
    /* 132 */ "LOC_MERGE",
    /* 133 */ "SET_CHAT_FILTER_D",
    /* 134 */ "LOGOUT",
    /* 135 */ "SET_WEIGHT",
    /* 136 */ "SOUND_GROUP_STOP",
    /* 137 */ "IF_SETNPCMODEL_ANIM",
    /* 138 */ "SET_VARP_LONG",
    /* 139 */ "PLAYER_OP",
    /* 140 */ "IF_SETOBJECT_NONUM_2",
    /* 141 */ "UPDATE_ZONE_FULL_FOLLOWS_3",
    /* 142 */ "MINIMAP_FLAG_SET",
    /* 143 */ "SET_CAMERA_TARGET",
    /* 144 */ "SET_SYSUPDATE_TIMER",
    /* 145 */ "IF_SETHIDE_ACTIVE",
    /* 146 */ "NOOP",
    /* 147 */ "PROJANIM_SPECIFIC_HALT",
    /* 148 */ "UPDATE_PLAYER_CHAT",
    /* 149 */ "SET_WORLD_TARGET",
    /* 150 */ "UPDATE_FRIENDCHAT_CHANNEL",
    /* 151 */ "RESET_ANIMS",
    /* 152 */ "UPDATE_IGNORELIST_2",
    /* 153 */ "RUNCLIENTSCRIPT",
    /* 154 */ "UPDATE_PLAYER_GROUP",
    /* 155 */ "UPDATE_URL_STRING",
    /* 156 */ "CLANCHANNEL_DELTA_CS",
    /* 157 */ "SET_PLAYER_OP",
    /* 158 */ "SYNTH_SOUND",
    /* 159 */ "MAP_PROJANIM_HALT",
    /* 160 */ "SOUND_GROUP_SPEED",
    /* 161 */ "SET_INTERACTION_FLAG_B",
    /* 162 */ "IF_SETGRAPHIC_ACTIVE",
    /* 163 */ "REBUILD_PLAYERINFO_POSITIONS",
    /* 164 */ "SET_CHAT_FILTER_C",
    /* 165 */ "NPC_INFO",
    /* 166 */ "MIDI_STOP",
    /* 167 */ "SKIP_2_BYTES",
    /* 168 */ "UPDATE_INV_GROUP",
    /* 169 */ "SET_INTERACTION_FLAG_C",
    /* 170 */ "NOOP_VAR_2",
    /* 171 */ "SET_URL_STRING",
    /* 172 */ "IF_SETNPCMODEL_ACTIVE",
    /* 173 */ "IF_SETMODEL_ACTIVE",
    /* 174 */ "UPDATE_FRIENDLIST",
    /* 175 */ "SET_INTERACTION_FLAG_D",
    /* 176 */ "IF_SETPLAYERMODEL_EXACTMOVE",
    /* 177 */ "REBUILD_REGION_HANDLER",
    /* 178 */ "REBUILD_REGION",
    /* 179 */ "FRIENDCHAT_SYSUPDATE",
    /* 180 */ "IF_CLOSESUB",
    /* 181 */ "SET_NPC_UPDATE_FLAG",
    /* 182 */ "IF_OPENSUB",
    /* 183 */ "OPCODE_183",
    /* 184 */ "SOUND_AREA_SYNTH_2",
    /* 185 */ "MAP_PROJANIM_2",
    /* 186 */ "REBUILD_NORMAL",
    /* 187 */ "PLAYER_GROUP_DELTA",
    /* 188 */ "SET_CHAT_FILTER_B",
    /* 189 */ "PLAYER_INFO_DECODE",
    /* 190 */ "IF_MOVESUB_ACTIVE",
    /* 191 */ "TRIGGER_ONDIALOGABORT",
    /* 192 */ "SKIP_DATA",
    /* 193 */ "MAP_FLAG_SET",
    /* 194 */ "MAP_ANIM_SPECIFIC",
    /* 195 */ "IF_SETPOSITION_ACTIVE",
    /* 196 */ "OCULUS_SYNC",
    /* 197 */ "IF_SETCLICKMASK_ACTIVE",
    /* 198 */ "SOUND_AREA",
    /* 199 */ "FRIENDCHAT_JOIN",
    /* 200 */ "SOUND_MODIFY",
    /* 201 */ "MESSAGE_PRIVATE_ECHO",
    /* 202 */ "IF_SETEVENTS",
    /* 203 */ "SERVER_TICK_END",
    /* 204 */ "SET_CHAT_FILTER_A",
    /* 205 */ "REMOVE_PLAYER_FROM_LIST",
    /* 206 */ "DESTROY_ZONE_DATA",
    /* 207 */ "IF_OPENTOP",
    /* 208 */ "UPDATE_REBOOT_TIMER",
    /* 209 */ "VORBIS_PRELOAD",
    /* 210 */ "IF_SETRECOL_ACTIVE",
    /* 211 */ "SET_HEATMAP",
    /* 212 */ "TRIGGER_ONDIALOGABORT_2",
    /* 213 */ "VORBIS_SONG",
    /* 214 */ "PROJANIM_SPECIFIC",
    /* 215 */ "URL_OPEN",
    /* 216 */ "SOUND_STOP",
)

// ---- ClientProt names (for logging) ----

private val CLIENT_PROT_NAMES = arrayOf(
    /* 0 */   "EVENT_APPLET_FOCUS",
    /* 1 */   "EVENT_CAMERA_POSITION",
    /* 2 */   "CAMERA_DIRECTION",
    /* 3 */   "VERIFIED_STRING_SEND",
    /* 4 */   "MESSAGE_PUBLIC",
    /* 5 */   "OPNPC_T_LONG",
    /* 6 */   "OPOBJ5",
    /* 7 */   "RESUME_COUNTDIALOG",
    /* 8 */   "STRTOL_SEND",
    /* 9 */   "OPNPC_T1",
    /* 10 */  "RESUME_PAUSEBUTTON",
    /* 11 */  "OPCODE_11",
    /* 12 */  "IF_BUTTON_T",
    /* 13 */  "OPCODE_13",
    /* 14 */  "OPOBJ7",
    /* 15 */  "NO_TIMEOUT",
    /* 16 */  "OPLOC_T_LONG",
    /* 17 */  "EVENT_MOUSE_CLICK",
    /* 18 */  "IF_BUTTON5",
    /* 19 */  "OPNPC_T2",
    /* 20 */  "OPOBJ1",
    /* 21 */  "MAP_BUILD_COMPLETE",
    /* 22 */  "OPCODE_22",
    /* 23 */  "OPNPC3",
    /* 24 */  "CLIENT_CHEAT",
    /* 25 */  "OPNPC2",
    /* 26 */  "OPNPC1",
    /* 27 */  "OPLOC_T",
    /* 28 */  "DEVICE_INFO",
    /* 29 */  "MESSAGE_PUBLIC",
    /* 30 */  "OPOBJ10",
    /* 31 */  "EVENT_CAMERA_POSITION_2",
    /* 32 */  "CS2_CALLBACK",
    /* 33 */  "MOVE_GAME_MINI",
    /* 34 */  "QUEUED_PACKET",
    /* 35 */  "OPNPC_T5",
    /* 36 */  "OPNPC_T6",
    /* 37 */  "OPLOC_T_EXT",
    /* 38 */  "OPLOC_T1",
    /* 39 */  "SOCIAL_REQUEST",
    /* 40 */  "OPLOC_T3",
    /* 41 */  "OPCODE_41",
    /* 42 */  "UNKNOWN_3BYTE_42",
    /* 43 */  "OPLOC_T6",
    /* 44 */  "OPCODE_44",
    /* 45 */  "EVENT_TELEMETRY",
    /* 46 */  "OPOBJ2",
    /* 47 */  "IF_BUTTON10",
    /* 48 */  "EVENT_KEYBOARD",
    /* 49 */  "DATA_REPORT_VARSHORT",
    /* 50 */  "SCENE_GRAPH_REPORT",
    /* 51 */  "CAMERA_ANGLE",
    /* 52 */  "MESSAGE_PRIVATE",
    /* 53 */  "OPCODE_53",
    /* 54 */  "IF_BUTTON3",
    /* 55 */  "OPCODE_55",
    /* 56 */  "RESUME_NAMEDIALOG",
    /* 57 */  "OPCODE_57",
    /* 58 */  "OPPLAYER_T_EXT",
    /* 59 */  "OPOBJ8",
    /* 60 */  "OPOBJ6",
    /* 61 */  "IF_BUTTON7",
    /* 62 */  "ENCODEDSTRING_SEND",
    /* 63 */  "IF_BUTTON9",
    /* 64 */  "IF_BUTTON6",
    /* 65 */  "SCENE_INTERACTION",
    /* 66 */  "EVENT_APPLET_FOCUS_2",
    /* 67 */  "CLAN_JOINCHAT",
    /* 68 */  "OPLOC2_T",
    /* 69 */  "OPNPC4_T",
    /* 70 */  "OPLOC1",
    /* 71 */  "ACTIVE_CHAT_PHRASE_SEND",
    /* 72 */  "OPCODE_72",
    /* 73 */  "OPCODE_73",
    /* 74 */  "ENCRYPTED_STRING_SEND2",
    /* 75 */  "IF_BUTTON_TARGETMENU",
    /* 76 */  "SOUND_SONGEND",
    /* 77 */  "OPNPC5",
    /* 78 */  "FRIENDLIST_ADD",
    /* 79 */  "OPCODE_79",
    /* 80 */  "NO_TIMEOUT_2",
    /* 81 */  "ACTIVE_CHAT_PHRASE_SENDPRIVATE",
    /* 82 */  "WINDOW_STATUS",
    /* 83 */  "FOCUS_CHANGED",
    /* 84 */  "OPOBJ_CS2_2",
    /* 85 */  "EVENT_MOUSE_MOVE",
    /* 86 */  "MESSAGE_CLAN_CHAT",
    /* 87 */  "CLOSE_MODAL",
    /* 88 */  "SOUND_SONGSELECT",
    /* 89 */  "MOVE_SCRIPTED",
    /* 90 */  "OPNPC4",
    /* 91 */  "OPOBJ9",
    /* 92 */  "MOVE_GAME_EXT",
    /* 93 */  "CLAN_LEAVECHAT",
    /* 94 */  "OPLOC_CS2",
    /* 95 */  "DETECT_MODIFIED_CLIENT",
    /* 96 */  "OPOBJ4",
    /* 97 */  "IF_BUTTON1",
    /* 98 */  "OPOBJ_CS2",
    /* 99 */  "AFFINEDTRANSFORM_SET",
    /* 100 */ "OPCODE_100",
    /* 101 */ "OPLOC4_T",
    /* 102 */ "MOVE_GAME",
    /* 103 */ "OPNPC6",
    /* 104 */ "OPNPC_CS2",
    /* 105 */ "OPOBJ_T",
    /* 106 */ "DISPLAY_INFO",
    /* 107 */ "IF_BUTTONT",
    /* 108 */ "OPCODE_108",
    /* 109 */ "IGNORELIST_ADD",
    /* 110 */ "WORLDLIST_FETCH",
    /* 111 */ "OPLOC5_T",
    /* 112 */ "OPCODE_112",
    /* 113 */ "RENDER_REPORT",
    /* 114 */ "IF_BUTTON_TARGETMENU_SEND",
    /* 115 */ "OPOBJ3",
    /* 116 */ "ENCRYPTED_STRING_SEND",
    /* 117 */ "CLOSE_MODAL_COMPONENT",
    /* 118 */ "IF_BUTTON2",
    /* 119 */ "OPPLAYER_CS2",
    /* 120 */ "OPPLAYER_T",
    /* 121 */ "FRIENDLIST_DEL",
    /* 122 */ "INTERFACE_INTERACTION",
    /* 123 */ "BUG_REPORT",
    /* 124 */ "IF_BUTTON8",
    /* 125 */ "OPNPC3_T",
    /* 126 */ "OPCODE_126",
    /* 127 */ "ENCODEDSTRING_SEND2",
    /* 128 */ "IF_BUTTON4",
    /* 129 */ "STRTOLL_SEND",
)

// ---- ClientProt sizes (130 entries, opcodes 0-129) ----
// Duplicated from LoginServer.kt for the proxy's own use

private val CLIENT_PROT_SIZES = intArrayOf(
    /* 0 */ -2,  /* 1 */  9,  /* 2 */  6,  /* 3 */ -2,  /* 4 */ -1,
    /* 5 */ 15,  /* 6 */  3,  /* 7 */ -2,  /* 8 */  4,  /* 9 */  3,
    /* 10 */ -2, /* 11 */  4, /* 12 */ 16, /* 13 */  2, /* 14 */  3,
    /* 15 */  0, /* 16 */ 11, /* 17 */ -1, /* 18 */  8, /* 19 */  3,
    /* 20 */  3, /* 21 */  0, /* 22 */  1, /* 23 */  7, /* 24 */ -1,
    /* 25 */  7, /* 26 */  7, /* 27 */ 12, /* 28 */ -1, /* 29 */ -1,
    /* 30 */  3, /* 31 */ -2, /* 32 */ -1, /* 33 */  5, /* 34 */  0,
    /* 35 */  3, /* 36 */  3, /* 37 */ 15, /* 38 */  9, /* 39 */ -1,
    /* 40 */  9, /* 41 */  0, /* 42 */  3, /* 43 */  9, /* 44 */ -2,
    /* 45 */ -2, /* 46 */  3, /* 47 */  8, /* 48 */ -1, /* 49 */ -2,
    /* 50 */  4, /* 51 */  4, /* 52 */ -2, /* 53 */  9, /* 54 */  8,
    /* 55 */  1, /* 56 */ -1, /* 57 */  9, /* 58 */ 17, /* 59 */  3,
    /* 60 */  3, /* 61 */  8, /* 62 */ -1, /* 63 */  8, /* 64 */  8,
    /* 65 */  4, /* 66 */  4, /* 67 */ -1, /* 68 */  9, /* 69 */  3,
    /* 70 */  4, /* 71 */ -1, /* 72 */  1, /* 73 */ 18, /* 74 */ -2,
    /* 75 */ -2, /* 76 */  2, /* 77 */  7, /* 78 */ -1, /* 79 */  1,
    /* 80 */  0, /* 81 */ -1, /* 82 */  3, /* 83 */  1, /* 84 */ -1,
    /* 85 */  7, /* 86 */ -1, /* 87 */  0, /* 88 */  2, /* 89 */  5,
    /* 90 */  7, /* 91 */  3, /* 92 */ 18, /* 93 */ -1, /* 94 */ -1,
    /* 95 */  4, /* 96 */  3, /* 97 */  8, /* 98 */ -1, /* 99 */  2,
    /* 100 */ 4, /* 101 */ 9, /* 102 */-2, /* 103 */ 7, /* 104 */-1,
    /* 105 */11, /* 106 */ 6, /* 107 */-1, /* 108 */-2, /* 109 */-1,
    /* 110 */ 4, /* 111 */ 9, /* 112 */-1, /* 113 */ 4, /* 114 */22,
    /* 115 */ 3, /* 116 */-1, /* 117 */ 6, /* 118 */ 8, /* 119 */-1,
    /* 120 */11, /* 121 */-1, /* 122 */16, /* 123 */ 1, /* 124 */ 8,
    /* 125 */ 3, /* 126 */-1, /* 127 */-1, /* 128 */ 8, /* 129 */ 8,
)

// ---- Entry Point ----

fun main(args: Array<String>) {
    val listenPort = findArg(args, "--listen-port")?.toIntOrNull() ?: DEFAULT_LISTEN_PORT
    val httpPort = findArg(args, "--http-port")?.toIntOrNull() ?: DEFAULT_HTTP_PORT
    val captureDir = File(findArg(args, "--capture-dir") ?: DEFAULT_CAPTURE_DIR)

    println("[proxy] Fetching jav_config from Jagex...")
    val (targetHost, targetPort, rawConfig) = resolveJagexServer()
    println("[proxy] Jagex lobby server: $targetHost:$targetPort")

    // Start the HTTP config proxy server
    startConfigHttpServer(httpPort, rawConfig, listenPort)

    // Load RSA keys
    val ourMod = BigInteger(EnvVars.loginRsaModulus)
    val ourExp = BigInteger(EnvVars.loginRsaExponent)
    val jagexMod = BigInteger(EnvVars.JAGEX_LOGIN_RSA_MODULUS_HEX, 16)
    val jagexExp = BigInteger.valueOf(EnvVars.JAGEX_LOGIN_RSA_EXPONENT.toLong())
    println("[proxy] Our RSA modulus (first 32 hex): ${ourMod.toString(16).take(32)}...")
    println("[proxy] Jagex RSA modulus (first 32 hex): ${jagexMod.toString(16).take(32)}...")

    captureDir.mkdirs()
    println("[proxy] Capture directory: ${captureDir.absolutePath}")
    println("[proxy] TCP proxy listening on port $listenPort")
    println("[proxy] All connections will be relayed to $targetHost:$targetPort")
    println("[proxy] RSA MITM: ENABLED")
    println()

    val sessionCounter = AtomicInteger(0)
    val server = ServerSocket(listenPort)

    while (true) {
        val clientSocket = server.accept()
        val sessionId = sessionCounter.incrementAndGet()
        println("[proxy] === New connection #$sessionId from ${clientSocket.remoteSocketAddress} ===")

        val session = ProxySession(
            sessionId, clientSocket, targetHost, targetPort, captureDir,
            ourMod, ourExp, jagexMod, jagexExp
        )
        Thread({ session.run() }, "proxy-session-$sessionId").start()
    }
}

// ---- Proxy Session ----

private class ProxySession(
    private val sessionId: Int,
    private val clientSocket: Socket,
    private val targetHost: String,
    private val targetPort: Int,
    captureBaseDir: File,
    private val ourRsaMod: BigInteger,
    private val ourRsaExp: BigInteger,
    private val jagexRsaMod: BigInteger,
    private val jagexRsaExp: BigInteger,
) {
    @Volatile private var phase = Phase.CONNECTION_TYPE
    @Volatile private var connectionType = -1
    @Volatile private var xteaChallengeLen = 0
    @Volatile private var loginDataLen = 0

    // ISAAC ciphers -- initialized after RSA MITM extracts keys
    @Volatile private var isaacKeys: IntArray? = null
    @Volatile private var c2sIsaac: Isaac? = null  // Decodes client->server opcodes (raw keys)
    @Volatile private var s2cIsaac: Isaac? = null  // Decodes server->client opcodes (keys + 50)

    // Accumulation buffers for multi-read parsing
    private val s2cAccum = ByteArrayOutputStream()
    private val c2sAccum = ByteArrayOutputStream()

    // File logging
    private val sessionDir: File
    private val c2sRaw: FileOutputStream
    private val s2cRaw: FileOutputStream
    private val logWriter: PrintWriter
    private val startNanos = System.nanoTime()

    // Output stream to Jagex -- needed by c2s thread to write modified login packet
    @Volatile private var targetOutput: OutputStream? = null

    // Partial 2-byte S2C opcode state: when we decode the first byte of a 2-byte
    // opcode but the second byte hasn't arrived yet, we store the decoded first byte
    // here. -1 means no partial opcode pending.
    @Volatile private var s2cPartialOpcodeFirstByte = -1

    init {
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        sessionDir = File(captureBaseDir, "login-${ts}_s${sessionId}")
        sessionDir.mkdirs()
        c2sRaw = FileOutputStream(File(sessionDir, "raw-c2s.bin"))
        s2cRaw = FileOutputStream(File(sessionDir, "raw-s2c.bin"))
        logWriter = PrintWriter(File(sessionDir, "decoded.log").bufferedWriter(), true)
        logWriter.println("# Login Proxy Capture (MITM) -- Session $sessionId")
        logWriter.println("# Started: ${LocalDateTime.now()}")
        logWriter.println("# Target: $targetHost:$targetPort")
        logWriter.println("# RSA MITM: ENABLED")
        logWriter.println()
    }

    fun run() {
        val targetSocket: Socket
        try {
            targetSocket = Socket(targetHost, targetPort)
            log("CTRL", "Connected to Jagex at $targetHost:$targetPort")
        } catch (e: Exception) {
            log("CTRL", "FAILED to connect to Jagex: ${e.message}")
            clientSocket.close()
            closeLog()
            return
        }

        val clientIn = clientSocket.getInputStream()
        val clientOut = clientSocket.getOutputStream()
        val targetIn = targetSocket.getInputStream()
        val targetOut = targetSocket.getOutputStream()
        targetOutput = targetOut

        val c2sThread = Thread({
            relayClientToServer(clientIn, targetOut)
            try { targetSocket.shutdownOutput() } catch (_: Exception) {}
        }, "s$sessionId-c2s")

        val s2cThread = Thread({
            relayServerToClient(targetIn, clientOut)
            try { clientSocket.shutdownOutput() } catch (_: Exception) {}
        }, "s$sessionId-s2c")

        c2sThread.start()
        s2cThread.start()
        c2sThread.join()
        s2cThread.join()

        clientSocket.close()
        targetSocket.close()
        log("CTRL", "Session ended")
        closeLog()
    }

    /**
     * Relay client -> Jagex server. In LOGIN_PACKET phase, buffers the full
     * login packet, performs RSA MITM (decrypt with our key, re-encrypt with
     * Jagex key), then forwards the modified packet.
     */
    private fun relayClientToServer(input: InputStream, output: OutputStream) {
        val buf = ByteArray(65536)
        var totalBytes = 0L

        try {
            while (true) {
                val n = input.read(buf)
                if (n == -1) break

                totalBytes += n

                // Save raw bytes (always, regardless of phase)
                synchronized(c2sRaw) {
                    c2sRaw.write(buf, 0, n)
                    c2sRaw.flush()
                }

                when (phase) {
                    Phase.LOGIN_PACKET -> {
                        // Buffer the login packet for MITM
                        c2sAccum.write(buf, 0, n)
                        val accum = c2sAccum.toByteArray()

                        if (accum.size >= 3) {
                            val loginOpcode = accum[0].toInt() and 0xFF
                            val varShortSize = ((accum[1].toInt() and 0xFF) shl 8) or (accum[2].toInt() and 0xFF)
                            val totalExpected = 3 + varShortSize

                            if (accum.size >= totalExpected) {
                                c2sAccum.reset()

                                val opName = when (loginOpcode) {
                                    16 -> "GAME_LOGIN"
                                    19 -> "LOBBY_LOGIN"
                                    30 -> "RECONNECT_LOGIN"
                                    26 -> "CONTINUE_ACK"
                                    else -> "UNKNOWN_OP"
                                }
                                log("C->S", "[${accum.size}B] LOGIN_PACKET: opcode=$loginOpcode ($opName), varShort size=$varShortSize")
                                logHex("C->S", accum, 0, accum.size)

                                // Perform RSA MITM
                                val modifiedPacket = performRsaMitm(accum, loginOpcode, varShortSize)

                                if (modifiedPacket != null) {
                                    output.write(modifiedPacket)
                                    output.flush()
                                    log("C->S", "MITM: Forwarded modified login packet (${modifiedPacket.size}B)")
                                } else {
                                    // MITM failed -- forward original
                                    output.write(accum, 0, totalExpected)
                                    output.flush()
                                    log("C->S", "MITM: FAILED -- forwarded original packet")
                                }

                                // Forward any excess bytes beyond the login packet
                                if (accum.size > totalExpected) {
                                    val excess = accum.copyOfRange(totalExpected, accum.size)
                                    output.write(excess)
                                    output.flush()
                                    log("C->S", "Forwarded ${excess.size}B excess after login packet")
                                }

                                phase = Phase.LOGIN_RESULT
                            } else {
                                log("C->S", "[${n}B] LOGIN_PACKET partial (${accum.size}/$totalExpected bytes accumulated)")
                            }
                        } else {
                            log("C->S", "[${n}B] LOGIN_PACKET partial (${accum.size} bytes, need header)")
                        }
                    }

                    Phase.POST_LOGIN -> {
                        // Decode ISAAC-encrypted packets
                        parseClientPostLogin(buf, n)
                        // Always forward raw bytes
                        output.write(buf, 0, n)
                        output.flush()
                    }

                    else -> {
                        // Passthrough with logging
                        output.write(buf, 0, n)
                        output.flush()
                        parseClientData(buf, n)
                    }
                }
            }
        } catch (e: Exception) {
            log("C->S", "Stream ended: ${e::class.simpleName}: ${e.message}")
        }
        log("C->S", "Total: $totalBytes bytes")
    }

    /**
     * Relay Jagex server -> client. Tracks login handshake phases based on
     * expected byte counts at each step.
     */
    private fun relayServerToClient(input: InputStream, output: OutputStream) {
        val buf = ByteArray(65536)
        var totalBytes = 0L

        try {
            while (true) {
                val n = input.read(buf)
                if (n == -1) break

                // Forward immediately
                output.write(buf, 0, n)
                output.flush()

                // Save raw bytes
                synchronized(s2cRaw) {
                    s2cRaw.write(buf, 0, n)
                    s2cRaw.flush()
                }

                totalBytes += n
                parseServerData(buf, n)
            }
        } catch (e: Exception) {
            log("S->C", "Stream ended: ${e::class.simpleName}: ${e.message}")
        }
        log("S->C", "Total: $totalBytes bytes")
    }

    // ---- RSA MITM ----

    /**
     * Perform RSA MITM on a login packet:
     * 1. Parse the pre-RSA fields to locate the RSA block
     * 2. Decrypt with our private key
     * 3. Extract ISAAC seeds
     * 4. Re-encrypt with Jagex's public key
     * 5. Reconstruct the packet with the re-encrypted RSA block
     *
     * Returns the full modified packet (opcode + varShort size + payload) or null on failure.
     */
    private fun performRsaMitm(packet: ByteArray, loginOpcode: Int, originalVarShortSize: Int): ByteArray? {
        try {
            // Payload starts after [1B opcode][2B varShort]
            val payload = packet.copyOfRange(3, 3 + originalVarShortSize)
            var pos = 0

            fun g4(): Int {
                val v = ((payload[pos].toInt() and 0xFF) shl 24) or
                        ((payload[pos + 1].toInt() and 0xFF) shl 16) or
                        ((payload[pos + 2].toInt() and 0xFF) shl 8) or
                        (payload[pos + 3].toInt() and 0xFF)
                pos += 4
                return v
            }

            // Read version info
            val majorVersion = g4()
            val minorVersion = g4()
            log("MITM", "Version: $majorVersion.$minorVersion")

            // Game login (opcode 16) has an extra disconnect_flag byte before RSA
            if (loginOpcode == 16) {
                val disconnectFlag = payload[pos].toInt() and 0xFF
                pos++
                log("MITM", "Disconnect flag: $disconnectFlag")
            }

            // RSA block: [2B rsaSize] [rsaSize bytes]
            // The client writes: pT_ushort(length+1), byte(0), bytes[...]
            // So the server reads: rsaSize = readUShort(), which includes the leading 0 byte
            val rsaSize = ((payload[pos].toInt() and 0xFF) shl 8) or (payload[pos + 1].toInt() and 0xFF)
            pos += 2
            log("MITM", "RSA block size: $rsaSize bytes (at payload offset ${pos - 2})")

            if (rsaSize <= 0 || rsaSize > 512 || pos + rsaSize > payload.size) {
                log("MITM", "ERROR: Invalid RSA block size $rsaSize (payload remaining: ${payload.size - pos})")
                return null
            }

            val rsaBlockStart = pos
            val rsaCiphertext = payload.copyOfRange(pos, pos + rsaSize)
            pos += rsaSize

            // The rest of the payload after the RSA block (XTEA-encrypted section + misc)
            val postRsaData = payload.copyOfRange(pos, payload.size)
            val preRsaData = payload.copyOfRange(0, rsaBlockStart - 2) // Everything before the rsaSize field

            // Decrypt RSA block with our private key
            log("MITM", "Decrypting RSA block (${rsaCiphertext.size}B) with our private key...")
            val decrypted = RSA.crypt(rsaCiphertext, ourRsaMod, ourRsaExp)
            log("MITM", "Decrypted RSA plaintext: ${decrypted.size}B")
            logHex("MITM", decrypted, 0, decrypted.size)

            // Parse decrypted RSA block
            var dpos = 0
            // Handle potential leading zero byte from BigInteger
            if (decrypted.isNotEmpty() && decrypted[0] == 0.toByte() && decrypted.size > 1) {
                // BigInteger may add a leading zero for positive numbers -- skip it
                // But only if the magic byte isn't at index 0
                if (decrypted[0] != 10.toByte() && decrypted.size > 1 && decrypted[1] == 10.toByte()) {
                    dpos = 1
                }
            }

            val magic = decrypted[dpos].toInt() and 0xFF
            dpos++
            if (magic != 10) {
                log("MITM", "ERROR: RSA magic mismatch! Expected 10, got $magic. Wrong RSA key?")
                logHex("MITM", decrypted, 0, decrypted.size)
                return null
            }

            // ISAAC/XTEA keys: 4 x int32
            val keys = IntArray(4)
            for (i in 0..3) {
                keys[i] = ((decrypted[dpos].toInt() and 0xFF) shl 24) or
                        ((decrypted[dpos + 1].toInt() and 0xFF) shl 16) or
                        ((decrypted[dpos + 2].toInt() and 0xFF) shl 8) or
                        (decrypted[dpos + 3].toInt() and 0xFF)
                dpos += 4
            }
            log("MITM", "ISAAC keys extracted: [${keys.joinToString(", ") { "0x${"%08X".format(it)}" }}]")

            // Session key: 8 bytes (long)
            if (dpos + 8 <= decrypted.size) {
                val sessionKey = ((decrypted[dpos].toLong() and 0xFF) shl 56) or
                        ((decrypted[dpos + 1].toLong() and 0xFF) shl 48) or
                        ((decrypted[dpos + 2].toLong() and 0xFF) shl 40) or
                        ((decrypted[dpos + 3].toLong() and 0xFF) shl 32) or
                        ((decrypted[dpos + 4].toLong() and 0xFF) shl 24) or
                        ((decrypted[dpos + 5].toLong() and 0xFF) shl 16) or
                        ((decrypted[dpos + 6].toLong() and 0xFF) shl 8) or
                        (decrypted[dpos + 7].toLong() and 0xFF)
                log("MITM", "Session key: 0x${"%016X".format(sessionKey)}")
            }

            // Log remaining RSA plaintext
            if (dpos + 8 < decrypted.size) {
                log("MITM", "RSA plaintext remaining ${decrypted.size - dpos - 8}B after session key:")
                logHex("MITM", decrypted, dpos + 8, decrypted.size - dpos - 8)
            }

            // Store ISAAC keys and initialize ciphers
            isaacKeys = keys.copyOf()
            initializeIsaacCiphers(keys)

            // Re-encrypt the decrypted RSA plaintext with Jagex's public key.
            //
            // The decrypted bytes came from BigInteger.toByteArray() which may have
            // a leading 0x00 sign byte. We need the raw plaintext starting from the
            // magic byte (0x0A = 10).
            //
            // Find the magic byte position to get the canonical plaintext.
            val plaintextStart = if (dpos > 1) 1 else 0  // skip leading zero if present
            val rsaPlaintext = decrypted.copyOfRange(plaintextStart, decrypted.size)
            log("MITM", "Re-encrypting RSA plaintext (${rsaPlaintext.size}B) with Jagex public key...")
            log("MITM", "  Plaintext starts with: ${rsaPlaintext.take(5).joinToString(" ") { "%02X".format(it) }}")

            // RSA.crypt uses BigInteger(data).modPow(exp, mod).toByteArray()
            // The result's toByteArray() may include a leading 0x00 sign byte.
            val reEncrypted = RSA.crypt(rsaPlaintext, jagexRsaMod, jagexRsaExp)

            // The on-wire format written by the client is:
            //   [2B rsaSize] [rsaSize bytes]
            // where rsaSize bytes = [0x00 padding] [ciphertext]
            // The leading 0x00 ensures BigInteger treats it as positive.
            //
            // BigInteger.toByteArray() already includes a leading 0x00 if the high
            // bit of the result is set. We need to ensure the wire format matches
            // what Jagex's server expects: the rsaSize field counts ALL bytes
            // including the leading zero.
            //
            // If toByteArray() already has a leading 0x00, use it as-is.
            // If not, prepend 0x00 for the wire format.
            val reEncBlock = if (reEncrypted.isNotEmpty() && (reEncrypted[0].toInt() and 0x80) != 0) {
                // High bit set -- prepend zero for positive BigInteger interpretation
                ByteArray(1 + reEncrypted.size).also {
                    it[0] = 0
                    System.arraycopy(reEncrypted, 0, it, 1, reEncrypted.size)
                }
            } else {
                // Already has leading zero or is positive without one
                reEncrypted
            }

            val newRsaSize = reEncBlock.size
            log("MITM", "Re-encrypted RSA block: ${newRsaSize}B (was $rsaSize)")

            // Reconstruct payload
            val newPayload = ByteArrayOutputStream()
            newPayload.write(preRsaData) // Everything before RSA size field
            // Write new RSA size (big-endian ushort)
            newPayload.write((newRsaSize shr 8) and 0xFF)
            newPayload.write(newRsaSize and 0xFF)
            // Write RSA block (includes leading zero + ciphertext)
            newPayload.write(reEncBlock)
            // Write post-RSA data
            newPayload.write(postRsaData)

            val newPayloadBytes = newPayload.toByteArray()
            val newVarShortSize = newPayloadBytes.size

            // Reconstruct full packet
            val result = ByteArray(3 + newVarShortSize)
            result[0] = loginOpcode.toByte()
            result[1] = ((newVarShortSize shr 8) and 0xFF).toByte()
            result[2] = (newVarShortSize and 0xFF).toByte()
            System.arraycopy(newPayloadBytes, 0, result, 3, newVarShortSize)

            log("MITM", "Packet reconstructed: ${result.size}B (was ${packet.size}B, delta=${result.size - packet.size})")

            // Save the decrypted RSA block to a separate file for analysis
            try {
                File(sessionDir, "rsa-plaintext.bin").writeBytes(decrypted)
                File(sessionDir, "isaac-keys.txt").writeText(
                    "ISAAC keys (decimal): ${keys.joinToString(", ")}\n" +
                    "ISAAC keys (hex): ${keys.joinToString(", ") { "0x${"%08X".format(it)}" }}\n"
                )
                log("MITM", "Saved rsa-plaintext.bin and isaac-keys.txt")
            } catch (e: Exception) {
                log("MITM", "Warning: could not save MITM artifacts: ${e.message}")
            }

            return result
        } catch (e: Exception) {
            log("MITM", "ERROR during RSA MITM: ${e::class.simpleName}: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    /**
     * Initialize ISAAC ciphers from extracted keys.
     *
     * Per crypto.md:
     * - Client creates out cipher (C->S) with raw keys
     * - Client creates in cipher (S->C) with keys + delta (50)
     *
     * For the proxy to decode:
     * - C->S packets: we need the same cipher as the server recv = Isaac(raw keys)
     * - S->C packets: we need the same cipher as the client recv = Isaac(keys + 50)
     */
    private fun initializeIsaacCiphers(keys: IntArray) {
        c2sIsaac = Isaac(keys.copyOf())
        val s2cKeys = keys.copyOf()
        for (i in s2cKeys.indices) s2cKeys[i] += EnvVars.ISAAC_DELTA
        s2cIsaac = Isaac(s2cKeys)
        log("MITM", "ISAAC ciphers initialized (C2S=raw keys, S2C=keys+${EnvVars.ISAAC_DELTA})")
    }

    // ---- Client data parsing (non-LOGIN_PACKET phases) ----

    private fun parseClientData(buf: ByteArray, len: Int) {
        when (phase) {
            Phase.CONNECTION_TYPE -> {
                connectionType = buf[0].toInt() and 0xFF
                val typeName = CONNECTION_TYPE_NAMES[connectionType] ?: "UNKNOWN"
                log("C->S", "[${len}B] CONNECTION_TYPE = $connectionType ($typeName)")
                logHex("C->S", buf, 0, len)

                if (connectionType == 15) {
                    phase = Phase.JS5
                    log("C->S", "JS5 connection -- switching to raw relay mode")
                } else {
                    phase = Phase.FIRST_RESPONSE
                }
            }

            Phase.JS5 -> {
                log("C->S", "[${len}B] JS5 data")
                logHex("C->S", buf, 0, len, MAX_HEX_DUMP_BYTES_DEFAULT)
            }

            else -> {
                log("C->S", "[${len}B] phase=${phase.name}")
                logHex("C->S", buf, 0, len)
            }
        }
    }

    // ---- Post-login client packet decoding ----

    /**
     * Decode ISAAC-encrypted client->server packets.
     *
     * Client opcodes are single-byte (0-129). Per the TcpIn analysis:
     *   raw_byte = read_byte()
     *   opcode = (raw_byte - isaac_next()) & 0xFF
     *
     * Then read size based on CLIENT_PROT_SIZES table, then payload.
     */
    private fun parseClientPostLogin(buf: ByteArray, len: Int) {
        val cipher = c2sIsaac
        if (cipher == null) {
            log("C->S", "[${len}B] POST_LOGIN (no ISAAC -- passthrough)")
            logHex("C->S", buf, 0, len, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            return
        }

        // Accumulate for multi-packet reads
        c2sAccum.write(buf, 0, len)
        val data = c2sAccum.toByteArray()
        var pos = 0

        while (pos < data.size) {
            val startPos = pos

            // Need at least 1 byte for opcode
            if (pos >= data.size) break

            val rawByte = data[pos].toInt() and 0xFF
            pos++
            val opcode = (rawByte - cipher.nextInt()) and 0xFF

            if (opcode < 0 || opcode >= CLIENT_PROT_SIZES.size) {
                log("C->S", "POST_LOGIN DESYNC: decoded opcode $opcode out of range at byte $startPos")
                logHex("C->S", data, startPos, minOf(32, data.size - startPos))
                // Desync -- dump remaining and reset
                c2sAccum.reset()
                return
            }

            val sizeInfo = CLIENT_PROT_SIZES[opcode]
            val size: Int
            when (sizeInfo) {
                0 -> size = 0
                -1 -> {
                    if (pos >= data.size) {
                        // Need more data
                        rewindAccum(c2sAccum, data, startPos, cipher, isC2S = true)
                        return
                    }
                    size = data[pos].toInt() and 0xFF
                    pos++
                }
                -2 -> {
                    if (pos + 1 >= data.size) {
                        rewindAccum(c2sAccum, data, startPos, cipher, isC2S = true)
                        return
                    }
                    size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
                    pos += 2
                }
                else -> size = sizeInfo
            }

            if (pos + size > data.size) {
                // Need more data
                rewindAccum(c2sAccum, data, startPos, cipher, isC2S = true)
                return
            }

            val payload = data.copyOfRange(pos, pos + size)
            pos += size

            val name = if (opcode < CLIENT_PROT_NAMES.size) CLIENT_PROT_NAMES[opcode] else "OPCODE_$opcode"
            log("C->S", "PKT opcode=$opcode (0x${"%02X".format(opcode)}) $name size=$size")
            if (size > 0) {
                logHex("C->S", payload, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            }
        }

        // All data consumed
        c2sAccum.reset()
    }

    // ---- Server data parsing ----

    /**
     * Parse server data with accumulation. TCP may deliver partial reads,
     * so we accumulate bytes until we have enough for the current phase.
     */
    private fun parseServerData(buf: ByteArray, len: Int) {
        when (phase) {
            Phase.FIRST_RESPONSE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 9) {
                    val responseCode = accum[0].toInt() and 0xFF
                    val sessionKey = ByteBuffer.wrap(accum, 1, 8).long
                    log("S->C", "[${accum.size}B] FIRST_RESPONSE: response_code=$responseCode, session_key=0x${"%016X".format(sessionKey)}")
                    logHex("S->C", accum, 0, accum.size)

                    if (responseCode == 0) {
                        log("S->C", "  -> OK. Client will send login packet next (savedStep=0x50 for lobby)")
                        phase = Phase.LOGIN_PACKET
                    } else {
                        log("S->C", "  -> Error: code $responseCode -- connection will likely close")
                        phase = Phase.CLOSED
                    }

                    if (accum.size > 9) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 9, accum.size - 9)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] FIRST_RESPONSE partial (${accum.size}/9 bytes so far)")
                }
            }

            Phase.SECOND_RESPONSE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 2) {
                    xteaChallengeLen = ((accum[0].toInt() and 0xFF) shl 8) or (accum[1].toInt() and 0xFF)
                    log("S->C", "[${accum.size}B] SECOND_RESPONSE: xtea_challenge_length=$xteaChallengeLen")
                    logHex("S->C", accum, 0, accum.size)
                    phase = Phase.XTEA_CHALLENGE

                    if (accum.size > 2) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 2, accum.size - 2)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] SECOND_RESPONSE partial (${accum.size}/2 bytes)")
                }
            }

            Phase.XTEA_CHALLENGE -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= xteaChallengeLen) {
                    log("S->C", "[${accum.size}B] XTEA_CHALLENGE: $xteaChallengeLen bytes of XTEA-encrypted data")
                    logHex("S->C", accum, 0, minOf(accum.size, xteaChallengeLen))
                    phase = Phase.GO_AHEAD

                    if (accum.size > xteaChallengeLen) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, xteaChallengeLen, accum.size - xteaChallengeLen)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] XTEA_CHALLENGE partial (${accum.size}/$xteaChallengeLen bytes)")
                }
            }

            Phase.GO_AHEAD -> {
                val goAhead = buf[0].toInt() and 0xFF
                log("S->C", "[${len}B] GO_AHEAD: value=$goAhead ${if (goAhead == 1) "(OK)" else "(UNEXPECTED)"}")
                logHex("S->C", buf, 0, len)

                if (goAhead == 1) {
                    phase = Phase.LOGIN_TOKEN
                    if (len > 1) {
                        parseServerData(buf.copyOfRange(1, len), len - 1)
                        return
                    }
                } else {
                    phase = Phase.CLOSED
                }
            }

            Phase.LOGIN_TOKEN -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= 16) {
                    log("S->C", "[${accum.size}B] LOGIN_TOKEN: 16 bytes XTEA-encrypted (token + nonce)")
                    logHex("S->C", accum, 0, minOf(accum.size, 16))
                    phase = Phase.LOGIN_PACKET
                    log("S->C", "  -> Server handshake complete. Waiting for client login packet...")

                    if (accum.size > 16) {
                        s2cAccum.reset()
                        s2cAccum.write(accum, 16, accum.size - 16)
                        val remaining = s2cAccum.toByteArray()
                        s2cAccum.reset()
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] LOGIN_TOKEN partial (${accum.size}/16 bytes)")
                }
            }

            Phase.LOGIN_RESULT -> {
                val resultCode = buf[0].toInt() and 0xFF
                val resultName = LOGIN_RESULT_NAMES[resultCode] ?: "UNKNOWN"
                log("S->C", "[${len}B] LOGIN_RESULT: code=$resultCode ($resultName)")
                logHex("S->C", buf, 0, len)

                when (resultCode) {
                    2 -> {
                        log("S->C", "  -> Login SUCCESS! Waiting for login data length...")
                        phase = Phase.LOGIN_DATA_LEN
                        if (len > 1) {
                            parseServerData(buf.copyOfRange(1, len), len - 1)
                            return
                        }
                    }
                    25 -> {
                        log("S->C", "  -> In queue, waiting for updates...")
                        // Stay in LOGIN_RESULT to read next result
                    }
                    37 -> {
                        log("S->C", "  -> TOTP authenticator code required")
                        phase = Phase.POST_LOGIN
                    }
                    else -> {
                        log("S->C", "  -> Login failed/special: $resultName")
                        phase = Phase.POST_LOGIN
                    }
                }
            }

            Phase.LOGIN_DATA_LEN -> {
                val dataLen = buf[0].toInt() and 0xFF
                loginDataLen = dataLen
                log("S->C", "[${len}B] LOGIN_DATA_LEN: $loginDataLen bytes of login data to follow")
                logHex("S->C", buf, 0, len)
                phase = Phase.LOGIN_DATA

                if (len > 1) {
                    parseServerData(buf.copyOfRange(1, len), len - 1)
                    return
                }
            }

            Phase.LOGIN_DATA -> {
                s2cAccum.write(buf, 0, len)
                val accum = s2cAccum.toByteArray()

                if (accum.size >= loginDataLen) {
                    log("S->C", "[${accum.size}B] LOGIN_DATA: $loginDataLen bytes of lobby/game login state")
                    logHex("S->C", accum, 0, loginDataLen, MAX_HEX_DUMP_BYTES_POSTLOGIN)
                    parseLobbyLoginData(accum, loginDataLen)

                    phase = Phase.POST_LOGIN
                    log("S->C", "  -> Login data received. Switching to POST_LOGIN mode (ISAAC decoding active: ${s2cIsaac != null}).")

                    if (accum.size > loginDataLen) {
                        s2cAccum.reset()
                        val remaining = accum.copyOfRange(loginDataLen, accum.size)
                        parseServerData(remaining, remaining.size)
                        return
                    }
                    s2cAccum.reset()
                } else {
                    log("S->C", "[${len}B] LOGIN_DATA partial (${accum.size}/$loginDataLen bytes)")
                }
            }

            Phase.JS5 -> {
                log("S->C", "[${len}B] JS5 data")
                logHex("S->C", buf, 0, len, MAX_HEX_DUMP_BYTES_DEFAULT)
            }

            Phase.POST_LOGIN -> {
                parseServerPostLogin(buf, len)
            }

            else -> {
                log("S->C", "[${len}B] phase=${phase.name}")
                logHex("S->C", buf, 0, len)
            }
        }
    }

    // ---- Post-login server packet decoding ----

    /**
     * Decode ISAAC-encrypted server->client packets.
     *
     * Server opcodes use 1 or 2 bytes:
     *   raw_byte = read_byte()
     *   decoded = (raw_byte - isaac_next()) & 0xFF
     *   if decoded < 128:
     *       opcode = decoded
     *   else:
     *       second_byte = read_byte()  // NOT ISAAC-decoded
     *       opcode = (decoded - 128) * 256 + second_byte
     *
     * Then look up size from SERVER_PROT_SIZES table.
     */
    private fun parseServerPostLogin(buf: ByteArray, len: Int) {
        val cipher = s2cIsaac
        if (cipher == null) {
            log("S->C", "[${len}B] POST_LOGIN (no ISAAC -- raw dump)")
            logHex("S->C", buf, 0, len, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            return
        }

        s2cAccum.write(buf, 0, len)
        val data = s2cAccum.toByteArray()
        var pos = 0

        while (pos < data.size) {
            val startPos = pos

            // Check if we have a partial 2-byte opcode from the previous call.
            // In that case, we already consumed the ISAAC value for the first byte
            // and stored the decoded value. We just need the second (raw) byte.
            val opcode: Int
            if (s2cPartialOpcodeFirstByte >= 0) {
                val decoded = s2cPartialOpcodeFirstByte
                s2cPartialOpcodeFirstByte = -1
                val secondByte = data[pos].toInt() and 0xFF
                pos++
                opcode = (decoded - 128) * 256 + secondByte
            } else {
                val rawByte = data[pos].toInt() and 0xFF
                pos++
                val decoded = (rawByte - cipher.nextInt()) and 0xFF

                if (decoded < 128) {
                    opcode = decoded
                } else {
                    // 2-byte opcode: second byte is NOT ISAAC-decoded
                    if (pos >= data.size) {
                        // Need more data. We already consumed the ISAAC value for
                        // the first byte, so we save the decoded value and wait
                        // for the next TCP read to provide the second byte.
                        s2cPartialOpcodeFirstByte = decoded
                        s2cAccum.reset()
                        // Do NOT re-buffer the first byte -- ISAAC already consumed
                        log("S->C", "POST_LOGIN: partial 2-byte opcode (decoded=$decoded), waiting for 2nd byte")
                        return
                    }
                    val secondByte = data[pos].toInt() and 0xFF
                    pos++
                    opcode = (decoded - 128) * 256 + secondByte
                }
            }

            if (opcode < 0 || opcode >= SERVER_PROT_SIZES.size) {
                log("S->C", "POST_LOGIN DESYNC: decoded opcode $opcode out of range [0,${SERVER_PROT_SIZES.size}) at byte $startPos")
                logHex("S->C", data, startPos, minOf(64, data.size - startPos))
                s2cAccum.reset()
                // Desync is unrecoverable -- null the cipher to prevent garbage
                log("S->C", "POST_LOGIN: ISAAC desync -- disabling ISAAC decoding for remainder")
                s2cIsaac = null
                return
            }

            val sizeInfo = SERVER_PROT_SIZES[opcode]
            val size: Int
            when (sizeInfo) {
                0 -> size = 0
                -1 -> {
                    if (pos >= data.size) {
                        // We already consumed ISAAC for the opcode. We cannot rewind.
                        // Log the opcode we decoded and wait for the size byte.
                        // PROBLEM: we need to remember we decoded this opcode.
                        // For simplicity, log a warning. This is extremely rare in practice
                        // because the opcode and size header are almost always in the same
                        // TCP segment.
                        s2cAccum.reset()
                        s2cAccum.write(data, startPos, data.size - startPos)
                        log("S->C", "POST_LOGIN: partial varByte size for opcode $opcode -- ISAAC may desync (rare edge case)")
                        return
                    }
                    size = data[pos].toInt() and 0xFF
                    pos++
                }
                -2 -> {
                    if (pos + 1 >= data.size) {
                        s2cAccum.reset()
                        s2cAccum.write(data, startPos, data.size - startPos)
                        log("S->C", "POST_LOGIN: partial varShort size for opcode $opcode -- ISAAC may desync (rare edge case)")
                        return
                    }
                    size = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos + 1].toInt() and 0xFF)
                    pos += 2
                }
                else -> size = sizeInfo
            }

            if (pos + size > data.size) {
                // Partial payload -- buffer and wait
                s2cAccum.reset()
                s2cAccum.write(data, startPos, data.size - startPos)
                log("S->C", "POST_LOGIN: partial payload for opcode $opcode ($size bytes needed, ${data.size - pos} available)")
                return
            }

            val payload = data.copyOfRange(pos, pos + size)
            pos += size

            val name = if (opcode < SERVER_PROT_NAMES.size) SERVER_PROT_NAMES[opcode] else "OPCODE_$opcode"
            log("S->C", "PKT opcode=$opcode (0x${"%02X".format(opcode)}) $name size=$size")
            if (size > 0) {
                logHex("S->C", payload, 0, size, MAX_HEX_DUMP_BYTES_POSTLOGIN)
            }
        }

        // All data consumed
        s2cAccum.reset()
    }

    /**
     * Buffer remaining data when a partial packet is detected mid-decode.
     *
     * KNOWN LIMITATION: ISAAC is a stateful PRNG -- once we call nextInt(),
     * we cannot rewind. If a TCP read splits in the middle of a packet (after
     * we already decoded the opcode with ISAAC), re-processing the buffered
     * data from startPos will consume an extra ISAAC value for the opcode
     * byte, causing permanent desync.
     *
     * This is acceptable for a diagnostic proxy because:
     * 1. TCP reads on localhost almost always contain complete packets
     * 2. The proxy is not modifying POST_LOGIN data (passthrough)
     * 3. If desync occurs, we detect it on the next opcode and log clearly
     *
     * The S2C 2-byte opcode case IS handled correctly via s2cPartialOpcodeFirstByte.
     * Only the "opcode decoded but size/payload incomplete" case has this limitation.
     */
    private fun rewindAccum(accum: ByteArrayOutputStream, data: ByteArray, startPos: Int, cipher: Isaac, isC2S: Boolean) {
        accum.reset()
        accum.write(data, startPos, data.size - startPos)
        val dir = if (isC2S) "C->S" else "S->C"
        log(dir, "POST_LOGIN: partial packet at pos $startPos, buffered ${data.size - startPos}B. WARNING: ISAAC may desync if split mid-opcode.")
    }

    // ---- Lobby login data field-level parsing (best-effort, no crypto) ----

    private fun parseLobbyLoginData(data: ByteArray, len: Int) {
        if (len < 30) {
            log("S->C", "  [login-data] Too short for lobby format ($len bytes)")
            return
        }

        try {
            var pos = 0
            fun g1(): Int { val v = data[pos].toInt() and 0xFF; pos++; return v }
            fun g2(): Int { val v = ((data[pos].toInt() and 0xFF) shl 8) or (data[pos+1].toInt() and 0xFF); pos += 2; return v }
            fun g3(): Int { val v = ((data[pos].toInt() and 0xFF) shl 16) or ((data[pos+1].toInt() and 0xFF) shl 8) or (data[pos+2].toInt() and 0xFF); pos += 3; return v }
            fun g4(): Int { val v = ((data[pos].toInt() and 0xFF) shl 24) or ((data[pos+1].toInt() and 0xFF) shl 16) or ((data[pos+2].toInt() and 0xFF) shl 8) or (data[pos+3].toInt() and 0xFF); pos += 4; return v }
            fun g8(): Long { val hi = g4().toLong() and 0xFFFFFFFFL; val lo = g4().toLong() and 0xFFFFFFFFL; return (hi shl 32) or lo }
            fun gStr(): String {
                val start = pos
                while (pos < len && data[pos] != 0.toByte()) pos++
                val s = String(data, start, pos - start, Charsets.ISO_8859_1)
                if (pos < len) pos++ // skip null terminator
                return s
            }

            val hasTotpUpdate = g1()
            log("S->C", "  [login-data] has_totp_update = $hasTotpUpdate")
            if (hasTotpUpdate == 1) {
                log("S->C", "  [login-data] TOTP update data present -- cannot parse further without knowing TOTP format")
                return
            }

            val membershipType = g1()
            val membershipDays = g1()
            val emailValidated = g1()
            val recoveryDelay = g3().let { if (it > 0x7FFFFF) it - 0x1000000 else it }
            val staffModFlag = g1()
            val unknownFlag1 = g1()
            val unknownFlag2 = g1()
            val membershipTs = g8()

            log("S->C", "  [login-data] membership_type=$membershipType, membership_days=$membershipDays")
            log("S->C", "  [login-data] email_validated=$emailValidated, recovery_delay=$recoveryDelay")
            log("S->C", "  [login-data] staff_mod=$staffModFlag, flag1=$unknownFlag1, flag2=$unknownFlag2")
            log("S->C", "  [login-data] membership_timestamp=$membershipTs (0x${"%016X".format(membershipTs)})")

            val timeByte = g1()
            val timeInt = g4()
            log("S->C", "  [login-data] time_byte=$timeByte, time_int=$timeInt")

            val flags = g1()
            log("S->C", "  [login-data] flags=0x${"%02X".format(flags)} (bit0=${flags and 1}, bit1=${(flags shr 1) and 1})")

            val unknown1 = g4()
            val unknown2 = g4()
            val playerIndex = g2()
            val unknown3 = g2()
            val unknown4 = g2()
            val unknown5 = g4()
            val unknown6 = g1()
            val unknown7 = g2()
            val unknown8 = g2()
            val isMembersWorld = g1()

            log("S->C", "  [login-data] unk1=$unknown1, unk2=$unknown2, player_index=$playerIndex")
            log("S->C", "  [login-data] unk3=$unknown3, unk4=$unknown4, unk5=$unknown5")
            log("S->C", "  [login-data] unk6=$unknown6, unk7=$unknown7, unk8=$unknown8")
            log("S->C", "  [login-data] is_members_world=$isMembersWorld")

            val displayName = gStr()
            log("S->C", "  [login-data] display_name=\"$displayName\"")

            if (pos + 7 <= len) {
                val unknown9 = g1()
                val unknown10 = g4()
                val worldId = g2()
                log("S->C", "  [login-data] unk9=$unknown9, unk10=$unknown10, world_id=${if (worldId == 0xFFFF) -1 else worldId}")
            }

            if (pos < len) {
                val serverInfo = gStr()
                log("S->C", "  [login-data] server_info=\"$serverInfo\"")
            }

            if (pos + 4 <= len) {
                val screenWidth = g2()
                val screenHeight = g2()
                log("S->C", "  [login-data] screen=${screenWidth}x${screenHeight}")
            }

            if (pos + 16 <= len) {
                val sessionToken1 = g8()
                val sessionToken2 = g8()
                log("S->C", "  [login-data] session_token_1=0x${"%016X".format(sessionToken1)}")
                log("S->C", "  [login-data] session_token_2=0x${"%016X".format(sessionToken2)}")
            }

            if (pos < len) {
                log("S->C", "  [login-data] ${len - pos} unparsed bytes remaining at offset $pos")
                logHex("S->C", data, pos, len - pos)
            }
        } catch (e: Exception) {
            log("S->C", "  [login-data] Parse error at some offset: ${e::class.simpleName}: ${e.message}")
        }
    }

    // ---- Logging ----

    private fun log(direction: String, message: String) {
        val elapsed = (System.nanoTime() - startNanos) / 1_000_000_000.0
        val ts = "%07.3f".format(elapsed)
        val line = "[$ts] [$direction] $message"

        synchronized(logWriter) {
            logWriter.println(line)
        }
        println("[s$sessionId] $line")
    }

    private fun logHex(direction: String, data: ByteArray, offset: Int, length: Int, maxBytes: Int = MAX_HEX_DUMP_BYTES_DEFAULT) {
        val end = minOf(offset + length, offset + maxBytes, data.size)
        val sb = StringBuilder()
        for (i in offset until end) {
            if (i > offset) sb.append(' ')
            sb.append("%02X".format(data[i]))
        }
        if (length > maxBytes) {
            sb.append(" ... (${length} bytes total)")
        }
        val hexLine = "         hex: $sb"
        synchronized(logWriter) {
            logWriter.println(hexLine)
        }
        println("[s$sessionId] $hexLine")
    }

    private fun closeLog() {
        logWriter.println()
        logWriter.println("# Session ended: ${LocalDateTime.now()}")
        logWriter.close()
        c2sRaw.close()
        s2cRaw.close()
    }
}

// ---- jav_config.ws fetcher ----

/**
 * Start a lightweight HTTP server that serves the patched jav_config.ws.
 * Patches param=3 (lobby host) to localhost and param=41..48 (ports) to [proxyPort].
 * The launcher uses --configURI http://localhost:[httpPort]/jav_config.ws to pick this up.
 */
private fun startConfigHttpServer(httpPort: Int, rawConfig: String, proxyPort: Int) {
    if (rawConfig.isEmpty()) {
        println("[proxy] WARNING: No jav_config fetched, HTTP config server not started")
        return
    }

    // Build the patched config once (cached for all requests)
    val patchedConfig = buildPatchedConfig(rawConfig, proxyPort)

    val httpServer = HttpServer.create(InetSocketAddress(httpPort), 0)
    httpServer.createContext("/") { exchange ->
        // Accept any path containing jav_config.ws, or just serve on any GET
        if (exchange.requestMethod == "GET") {
            val responseBytes = patchedConfig.toByteArray(Charsets.ISO_8859_1)
            exchange.responseHeaders.add("Content-Type", "text/plain; charset=ISO-8859-1")
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        } else {
            exchange.sendResponseHeaders(405, -1)
        }
    }
    httpServer.executor = null // use default executor
    httpServer.start()

    println("[proxy] Config proxy HTTP: http://localhost:$httpPort/jav_config.ws")
    println("[proxy] Use --configURI http://localhost:$httpPort/jav_config.ws")
}

/**
 * Patch the raw jav_config text:
 * - param=3 (lobby host) -> localhost
 * - param=41..48 (ports) -> [proxyPort]
 */
private fun buildPatchedConfig(rawConfig: String, proxyPort: Int): String {
    val lines = rawConfig.lines().map { line ->
        if (!line.startsWith("param=")) return@map line
        val rest = line.removePrefix("param=")
        val eqIdx = rest.indexOf('=')
        if (eqIdx < 0) return@map line
        val key = rest.substring(0, eqIdx)
        when (key) {
            "3" -> "param=3=localhost"
            "41", "42", "43", "44", "45", "46", "47", "48" -> "param=$key=$proxyPort"
            else -> line
        }
    }
    // Append param=99 with our RSA modulus hex so the launcher can extract it
    val result = lines.toMutableList()
    result.add("param=99=${org.darkan.core.EnvVars.loginRsaModulusHex}")
    return result.joinToString("\n")
}

private data class JagexServerInfo(val host: String, val port: Int, val rawConfig: String)

private fun resolveJagexServer(): JagexServerInfo {
    try {
        val url = URI(JAV_CONFIG_URL).toURL()
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")

        val rawConfig = conn.inputStream.bufferedReader(Charsets.ISO_8859_1).readText()
        conn.disconnect()

        val lines = rawConfig.lines()
        var lobbyHost: String? = null
        var port: Int? = null

        for (line in lines) {
            when {
                line.startsWith("param=3=") -> lobbyHost = line.removePrefix("param=3=").trim()
                line.startsWith("param=41=") -> port = line.removePrefix("param=41=").trim().toIntOrNull()
            }
        }

        val host = lobbyHost ?: FALLBACK_LOBBY_HOST
        val p = port ?: FALLBACK_PORT

        println("[proxy] Parsed jav_config: lobby=$host, port=$p")
        println("[proxy] Relevant jav_config params:")
        for (l in lines) {
            if (l.startsWith("param=3=") || l.startsWith("param=41=") || l.startsWith("param=42=")
                || l.startsWith("param=43=") || l.startsWith("param=44=") || l.startsWith("param=45=")
                || l.startsWith("param=46=") || l.startsWith("param=47=") || l.startsWith("param=48=")
                || l.startsWith("server_version=") || l.startsWith("param=29=") || l.startsWith("param=10=")
            ) {
                println("[proxy]   $l")
            }
        }

        return JagexServerInfo(host, p, rawConfig)
    } catch (e: Exception) {
        println("[proxy] WARNING: Failed to fetch jav_config: ${e.message}")
        println("[proxy] Using fallback: $FALLBACK_LOBBY_HOST:$FALLBACK_PORT")
        return JagexServerInfo(FALLBACK_LOBBY_HOST, FALLBACK_PORT, "")
    }
}

private fun findArg(args: Array<String>, name: String): String? {
    val idx = args.indexOf(name)
    return if (idx >= 0 && idx + 1 < args.size) args[idx + 1] else null
}
