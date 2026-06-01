# 948-2-2 ClientProt (client→server) — Research D

Authoritative reverse-engineering of all 130 ClientProt opcodes (0x00–0x81) in
`rs2client.948-2-2`. Sizes are the load-bearing deliverable; names are only asserted when
CONFIRMED by hard evidence.

## Method (how every row was derived)

1. **Sizes** — extracted directly from `jag::ClientProt::RegisterAll @ 0x000c45b0`. Each opcode
   is registered via `ClientProt(&entry, opcode, size)`. Encoding: `0xffffffff` = -1 (varByte),
   `0xfffffffe` = -2 (varShort), otherwise fixed N bytes. All 130 independently cross-checked
   against project-undercut CSV — 130/130 sizes match.

2. **Emitter binding (opcode↔function)** — every ClientProt opcode N owns a `ProtEntry` struct at
   `&DAT_(0x015d3ea0 - idx*0x10)` (the descending table; `idx` skips opcode 0x19 which lives at
   the outlier `0x015bfb40`). A packet for opcode N is built by
   `FUN_001367a0(&buf, &entry, &entry+4, &netId)` which calls
   `jag::game::TcpConnectionMessage::Init(packet, entry, opcodeNo, ...)`. Therefore
   `get_xrefs_to(entryBase)` returns exactly the emitter(s) that send that opcode. This is the
   authoritative binding — far stronger than name/size/position heuristics. Verified for all 130.

3. **Names** — asserted only when (a) the emitter body has a self-identifying string
   (`CreateOpcodeError("name")`, distinctive chat strings) OR (b) the emitter is a single,
   cleanly-bound named function whose behaviour matches the packet. Everything else is
   `UNKNOWN_<op>`.

## CRITICAL: project-undercut CSV had a +1 opcode drift in the social/chat/dialog region

The CSV's `emitter_948_2_addr` column was correct, but its `opcode` column was shifted +1 across
opcodes ~71–90. The entry-base xref method (Ghidra) is ground truth. Corrected bindings:

| Opcode | Was (CSV/old stub) | Corrected (Ghidra entry-base xref) | Evidence |
|--------|--------------------|------------------------------------|----------|
| 71 | UNBOUND | RESUME_NAME_DIALOG (SendResumeNameDialog @0x002cff20, varByte) | xref entry 0x015d3a40 |
| 72 | RESUME_NAME_DIALOG | UNKNOWN_72 (UNBOUND) | entry 0x015d3a30 → RegisterAll only |
| 80 | UNKNOWN_80 | IGNORELIST_ADD (SendIgnorelistAdd @0x0030f4b0, varByte) | xref entry 0x015d39b0 + "Your ignore list is full" |
| 81 | IGNORELIST_ADD | UNKNOWN_81 | entry 0x015d39a0 → generic MultiDisplay PARAM only |
| 84 | UNKNOWN_84 | IF_BUTTON_TARGET_MENU (SendIfButtonTargetMenu @0x0029aae0, fixed 22) | xref entry 0x015d3970 |
| 85 | IF_BUTTON_TARGET_MENU | ACTIVECHATPHRASE_SENDPRIVATE (@0x00340850, varByte) | xref entry 0x015d3960 + self-name |
| 86 | ACTIVECHATPHRASE_SENDPRIVATE | UNKNOWN_86 | entry 0x015d3950 → generic FUN_0015c100 |
| 87 | UNKNOWN_87 | MESSAGE_PUBLIC_EFFECTS (SendMessagePublicWithEffects @0x0037c1c0, varByte) | xref entry 0x015d3940 |
| 88 | MESSAGE_PUBLIC_EFFECTS | DATA_REPORT (SendDataReport @0x00341230, varShort) | xref entry 0x015d3930 |
| 89 | DATA_REPORT | SOCIAL_REQUEST (SendSocialRequest @0x0026aef0, varByte) | xref entry 0x015d3920 + "That user is not in this channel" |
| 90 | SOCIAL_REQUEST | UNKNOWN_90 (UNBOUND) | entry 0x015d3910 → RegisterAll only |
| 17 | UNKNOWN_17 | MOVE_SCRIPTED (movescripted @0x002a3a70, fixed 5) | xref entry 0x015d3d90 + CreateOpcodeError("movescripted") |
| 82 | MULTI_DISPLAY_3 | UNKNOWN_82 (UNBOUND) | entry 0x015d3990 → RegisterAll only |
| 76 | ENCODED_STRING_2 | ENCODED_STRING_2 (kept, MEDIUM) | entry 0x015d39f0 referenced by SendEncodedString2 AND a large dispatcher |
| 77 | EVENT_APPLET_FOCUS | UNKNOWN_77 (UNBOUND) | entry 0x015d39e0 → RegisterAll only |

Server-side decoders for IGNORELIST_ADD, RESUME_P_NAMEDIALOG and CLANCHANNEL_KICKUSER were
registered at the wrong (drifted) opcodes/sizes in `Rev948ClientCodecs.kt`; these were corrected
(80/varByte, 71/varByte, 89/varByte respectively). Left uncorrected they would desync the inbound
stream.

## "UNBOUND" classes

- **CS2 data-table dispatch** (`0x013659xx` data ref, no emitter body): opcodes 13, 21, 30, 43,
  45, 68, 92, 103, 127.
- **No referrer at all** (RegisterAll only): opcodes 7, 19, 34, 46, 62, 67, 72, 77, 78, 82, 83,
  90, 95, 96, 104, 128.
- **Generic CS2 senders** (shared `FUN_002d9950` / `FUN_0015cxxx` / `FUN_0026b8e0` families, no
  protocol-specific identity): opcodes 2, 6, 9, 10, 11, 16, 22, 24, 29, 31, 32, 33, 36, 39, 40,
  41, 42, 47, 48, 50, 53, 55, 56, 60, 61, 63, 64, 65, 69, 73, 74, 75, 86, 91, 97, 98, 99, 101,
  102, 108–116, 121, 122, 125, 126. These stay `UNKNOWN_<op>` — they are real CS2-script-driven
  opcodes but carry no derivable wire identity from the binary alone.

## Full opcode table (all 130)

size: fixed N, -1 = varByte, -2 = varShort. CONF: A=self-naming string, B=clean named emitter +
entry-base xref, U=UNKNOWN (no identity).

| op | size | name | emitter (entry base) | CONF |
|----|------|------|----------------------|------|
| 0 | -1 | UNKNOWN_0 | FUN_00280610 (0x015d3ea0) | U |
| 1 | -1 | OPNPC_CS2 | SendOpNpcCS2 0x00311830 | B |
| 2 | 9 | UNKNOWN_2 | FUN_0015c7b0 | U |
| 3 | 9 | ANTI_CHEAT_REPLY | HandleAntiCheatChallenge 0x001807a0 | B |
| 4 | -1 | OPPLAYER_CS2 | SendOpPlayerCS2 0x0033df80 | B |
| 5 | 4 | SCENE_GRAPH_REPORT | SendSceneGraphReport 0x00229d80 | B |
| 6 | 7 | UNKNOWN_6 | FUN_0026b8e0 family | U |
| 7 | 4 | UNKNOWN_7 | UNBOUND | U |
| 8 | 4 | MULTI_DISPLAY | SendMultiDisplayPackets 0x001a2d90 | B |
| 9 | 12 | UNKNOWN_9 | FUN_0015b5c0 | U |
| 10 | 16 | UNKNOWN_10 | FUN_002b7440 | U |
| 11 | 3 | UNKNOWN_11 | FUN_0015c3d0 | U |
| 12 | -1 | MULTI_DISPLAY_2 | SendMultiDisplayPackets | B |
| 13 | 8 | UNKNOWN_13 | UNBOUND (CS2 table) | U |
| 14 | 0 | NO_TIMEOUT | SendNoTimeout 0x002cffc0 | B |
| 15 | 6 | EVENT_MOUSE_CLICK | SendEventMouseClick 0x001805b0 | B |
| 16 | 6 | UNKNOWN_16 | FUN_002956e0 | U |
| 17 | 5 | MOVE_SCRIPTED | movescripted 0x002a3a70 | A |
| 18 | 17 | OPLOC_T_LONG | SendOpLocTLong 0x0015c470 | B |
| 19 | 9 | UNKNOWN_19 | UNBOUND | U |
| 20 | -1 | OPOBJ_CS2_2 | SendOpObjCS2_2 0x002a24c0 | B |
| 21 | 8 | UNKNOWN_21 | UNBOUND (CS2 table) | U |
| 22 | 3 | UNKNOWN_22 | FUN_002d9950 family | U |
| 23 | 8 | UNKNOWN_23 | UNBOUND | U |
| 24 | 7 | UNKNOWN_24 | FUN_0026b8e0 family | U |
| 25 | 11 | UNKNOWN_25 | FUN_00aeeb30 (0x015bfb40) | U |
| 26 | -2 | RESUME_COUNT_DIALOG | SendResumeCountDialog 0x002cfdb0 | B |
| 27 | 1 | UNKNOWN_27 | UNBOUND | U |
| 28 | 4 | OPLOC | DoOpLoc 0x00136900 | B |
| 29 | 4 | UNKNOWN_29 | FUN_001fc8c0 | U |
| 30 | 8 | UNKNOWN_30 | UNBOUND (CS2 table) | U |
| 31 | 3 | UNKNOWN_31 | FUN_0015c450 | U |
| 32 | 3 | UNKNOWN_32 | FUN_0015c430 | U |
| 33 | 3 | UNKNOWN_33 | FUN_002d9950 family | U |
| 34 | 15 | UNKNOWN_34 | UNBOUND | U |
| 35 | -1 | IF_BUTTON_X_INNER | IfButtonXInner 0x00297eb0 | B |
| 36 | 7 | UNKNOWN_36 | FUN_0026b8e0 family | U |
| 37 | 0 | QUEUED_PACKET | SendQueuedPacket 0x002d3870 | B |
| 38 | -2 | MESSAGE_PRIVATE | SendMessagePrivate 0x00306dd0 (tinyKeyEncrypt) | B |
| 39 | 3 | UNKNOWN_39 | FUN_002d9950 family | U |
| 40 | 3 | UNKNOWN_40 | FUN_002d9950 family | U |
| 41 | 9 | UNKNOWN_41 | FUN_0015c7f0 | U |
| 42 | -1 | UNKNOWN_42 | FUN_002d0490 | U |
| 43 | 8 | UNKNOWN_43 | UNBOUND (CS2 table) | U |
| 44 | 4 | STRTOL | SendStrtol 0x002d4d20 | B |
| 45 | 8 | UNKNOWN_45 | UNBOUND (CS2 table) | U |
| 46 | 1 | UNKNOWN_46 | UNBOUND | U |
| 47 | 3 | UNKNOWN_47 | FUN_002d9950 family | U |
| 48 | -1 | UNKNOWN_48 | FUN_00172900 | U |
| 49 | -1 | OPOBJ_CS2 | SendOpObjCS2 0x0033ddc0 | B |
| 50 | 3 | UNKNOWN_50 | FUN_002d9950 family | U |
| 51 | 0 | PROCESS_CONNECTIONS | ProcessConnections 0x0013e6c0 | B |
| 52 | 6 | DISPLAY_INFO | SendDisplayInfo 0x001a2ba0 | B |
| 53 | 15 | UNKNOWN_53 | FUN_0015bcf0 | U |
| 54 | 4 | WORLDLIST_FETCH | SendWorldlistFetch 0x00194a80 | B |
| 55 | 11 | UNKNOWN_55 | FUN_0015bad0 | U |
| 56 | 3 | UNKNOWN_56 | FUN_002d9950 family | U |
| 57 | 2 | AFFINED_TRANSFORM_SET_MAIN | SendAffinedTransformSet_Main 0x0033e1f0 | B |
| 58 | -2 | MOVE_GAME | SendMoveGame 0x0026e1d0 | B |
| 59 | 4 | DETECT_MODIFIED_CLIENT | SendDetectModifiedClient 0x00480620 | B |
| 60 | -1 | UNKNOWN_60 | FUN_002d02a0 | U |
| 61 | 16 | UNKNOWN_61 | FUN_0014edf0 | U |
| 62 | 1 | UNKNOWN_62 | UNBOUND | U |
| 63 | 3 | UNKNOWN_63 | FUN_0015c3f0 | U |
| 64 | 7 | UNKNOWN_64 | FUN_0026b8e0 family | U |
| 65 | 4 | UNKNOWN_65 | FUN_002383e0 | U |
| 66 | 0 | CLOSE_MODAL | SendCloseModal 0x002d01f0 | B |
| 67 | 18 | UNKNOWN_67 | UNBOUND | U |
| 68 | 8 | UNKNOWN_68 | UNBOUND (CS2 table) | U |
| 69 | 2 | UNKNOWN_69 | FUN_002d03b0 | U |
| 70 | -1 | FRIENDLIST_DEL | SendFriendlistDel 0x0026b1f0 | B |
| 71 | -1 | RESUME_NAME_DIALOG | SendResumeNameDialog 0x002cff20 | B |
| 72 | -2 | UNKNOWN_72 | UNBOUND | U |
| 73 | 3 | UNKNOWN_73 | FUN_002d9950 family | U |
| 74 | 5 | UNKNOWN_74 | FUN_0015b7d0 | U |
| 75 | -1 | UNKNOWN_75 | FUN_0015b7d0 | U |
| 76 | 4 | ENCODED_STRING_2 | SendEncodedString2 0x0028ea60 | B(med) |
| 77 | 9 | UNKNOWN_77 | UNBOUND | U |
| 78 | 18 | UNKNOWN_78 | UNBOUND | U |
| 79 | 1 | UNKNOWN_79 | FUN_0015b7d0 | U |
| 80 | -1 | IGNORELIST_ADD | SendIgnorelistAdd 0x0030f4b0 | B |
| 81 | -2 | UNKNOWN_81 | (generic MultiDisplay PARAM) | U |
| 82 | -2 | UNKNOWN_82 | UNBOUND | U |
| 83 | -1 | UNKNOWN_83 | UNBOUND | U |
| 84 | 22 | IF_BUTTON_TARGET_MENU | SendIfButtonTargetMenu 0x0029aae0 | B |
| 85 | -1 | ACTIVECHATPHRASE_SENDPRIVATE | activechatphrase_sendprivate 0x00340850 | A |
| 86 | 11 | UNKNOWN_86 | FUN_0015c100 | U |
| 87 | -1 | MESSAGE_PUBLIC_EFFECTS | SendMessagePublicWithEffects 0x0037c1c0 | B |
| 88 | -2 | DATA_REPORT | SendDataReport 0x00341230 | B |
| 89 | -1 | SOCIAL_REQUEST | SendSocialRequest 0x0026aef0 | B |
| 90 | -1 | UNKNOWN_90 | UNBOUND | U |
| 91 | 3 | UNKNOWN_91 | FUN_002d9950 family | U |
| 92 | 8 | UNKNOWN_92 | UNBOUND (CS2 table) | U |
| 93 | -2 | VERIFIED_STRING_SEND | SendVerifiedStringSend 0x00344600 | B |
| 94 | 3 | WINDOW_STATUS | SendWindowStatus 0x0033e470 | B |
| 95 | 4 | UNKNOWN_95 | UNBOUND | U |
| 96 | 0 | UNKNOWN_96 | UNBOUND | U |
| 97 | -1 | UNKNOWN_97 | FUN_002d0680 | U |
| 98 | -1 | UNKNOWN_98 | FUN_001729b0 | U |
| 99 | -1 | UNKNOWN_99 | FUN_002d0830 | U |
| 100 | -1 | FRIENDLIST_ADD | SendFriendlistAdd 0x0026a530 | B |
| 101 | 7 | UNKNOWN_101 | FUN_0026b8e0 family | U |
| 102 | -2 | UNKNOWN_102 | FUN_00298c10 | U |
| 103 | 8 | UNKNOWN_103 | UNBOUND (CS2 table) | U |
| 104 | 2 | UNKNOWN_104 | UNBOUND | U |
| 105 | -2 | APPLET_FOCUS_EVENTS | SendAppletFocusEvents 0x00194f40 | B |
| 106 | 1 | MULTI_DISPLAY_4 | SendMultiDisplayPackets 0x001a2d90 | B |
| 107 | 0 | MAP_BUILD_COMPLETE | SendMapBuildComplete 0x002bc030 | B |
| 108 | 3 | UNKNOWN_108 | FUN_002d9950 family | U |
| 109 | 9 | UNKNOWN_109 | FUN_0015c810 | U |
| 110 | 7 | UNKNOWN_110 | FUN_001803b0 | U |
| 111 | 9 | UNKNOWN_111 | FUN_0015c830 | U |
| 112 | 7 | UNKNOWN_112 | FUN_0026b8e0 family | U |
| 113 | 3 | UNKNOWN_113 | FUN_0015c410 | U |
| 114 | -1 | UNKNOWN_114 | FUN_00269660 | U |
| 115 | 3 | UNKNOWN_115 | FUN_00269560 | U |
| 116 | 2 | UNKNOWN_116 | FUN_002d05a0 | U |
| 117 | -1 | ENCODED_STRING | SendEncodedString 0x00311130 | B |
| 118 | -1 | ACTIVECHATPHRASE_SEND | activechatphrase_send 0x00340d50 | A |
| 119 | -2 | RESUME_PAUSE_BUTTON | SendResumePauseButton 0x002cfc50 | B |
| 120 | 8 | STRTOLL | SendStrtoll 0x002d74e0 | B |
| 121 | 3 | UNKNOWN_121 | FUN_0015c3b0 | U |
| 122 | -2 | UNKNOWN_122 | FUN_002d1af0 | U |
| 123 | 1 | BUG_REPORT | SendBugReport 0x002d0140 | B |
| 124 | -1 | MESSAGE_PUBLIC | SendMessagePublic 0x0037a9c0 | B |
| 125 | 9 | UNKNOWN_125 | FUN_0015c850 | U |
| 126 | 9 | UNKNOWN_126 | FUN_0015c7d0 | U |
| 127 | 8 | UNKNOWN_127 | UNBOUND (CS2 table) | U |
| 128 | -2 | UNKNOWN_128 | UNBOUND | U |
| 129 | -1 | OPLOC_CS2 | SendOpLocCS2 0x0033e140 | B |

## Summary

- **Sizes: 130/130 locked** from RegisterAll (independently == CSV).
- **Names CONFIRMED: 48** (3 by self-naming string A: 17/85/118; 45 by clean named-emitter + entry-base xref B; opcode 76 is B-medium).
- **UNKNOWN: 82** (UNBOUND or generic CS2 senders — no derivable wire identity).
- **Disagreements with project-undercut CSV: 14 opcode-mapping corrections** (see table above) plus the CSV-as-truth caveat: its emitter addresses are right, its opcode column drifts +1 in 71–90.
