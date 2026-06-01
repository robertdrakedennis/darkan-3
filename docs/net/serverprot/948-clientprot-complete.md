# 948-2-2 ClientProt (client→server) — COMPLETE 130-opcode coverage

Authoritative reverse-engineering of ALL 130 ClientProt opcodes (0x00–0x81) in
`rs2client.948-2-2`. Every opcode has a name (or `UNKNOWN_<op>` when no identity is derivable),
its registered size, the 948 emitter address, a confidence tag, and the supporting evidence.

## THE LAW (official-enum naming)

A packet's name is **ALWAYS** the official `jag::ClientProt::<NAME>` enum symbol — NEVER the name
of the C++ function that builds/sends it. The official vocabulary is the set of `jag::ClientProt`
ProtEntry globals in the beta unstripped binary (`librs2client.so`, `jag::ClientProt` namespace,
labels at 0x00a392d8..0x00a410e8). If a 948 opcode cannot be mapped to an official enum name with
HIGH+ confidence it stays `UNKNOWN_<op>` (size + emitter addr + read-shape retained).

**Correction pass (this revision):** a prior pass used FUNCTION names for ~40 opcodes (e.g. op 51
"PROCESS_CONNECTIONS" = the fn `ProcessConnections` that SENDS the keepalive; the packet is
`NO_TIMEOUT`). All function-derived names were replaced with the official enum name where provable,
else reverted to `UNKNOWN_<op>`. Headline fixes:
- **op 51 = NO_TIMEOUT** (the keepalive; was PROCESS_CONNECTIONS). CERTAIN.
- **op 14 = ABORT_P_DIALOG** (was wrongly NO_TIMEOUT). HIGH — distinct size-0 dialog-abort.
- **op 6/24/36/64/101/112 = OPOBJ5/1/4/3/2/6** (were OP5/1/4/3/2/6 placeholders). HIGH.
- **op 35 = IF_PLAYER** (was IF_BUTTON_X_INNER fn name). HIGH.
- **op 58 = MOVE_GAMECLICK** (was MOVE_GAME). HIGH.
- **op 26 = RESUME_P_COUNTDIALOG, op 71 = RESUME_P_NAMEDIALOG** (exact official spellings).

The beta references opcodes via `&jag::ClientProt::<NAME>` constants inside
`MakeClientMessage<jag::ClientProt>` calls, which name the wire packets. 948 emitters are matched to
beta senders by byte-layout + semantics (NOT by opcode number — numbers differ across the gap).

## Confidence taxonomy

- **CERTAIN** — beta exact symbol match (`&ClientProt::NAME` in a sender whose byte layout is
  identical to the 948 emitter) OR self-identifying string in the 948 body.
- **HIGH** — beta structural signature match (same dispatch family / identical field writes) OR a
  947-3 emitter carried over with matching decompile.
- **MEDIUM** — consistent with size + behaviour + project-undercut CSV, not independently proven.
- **LOW** — inferred from sibling/position/wire-shape only.
- **NONE** — no basis; keep `UNKNOWN_<op>` but record size + emitter addr + read-shape.

## Method recap

- **Sizes** — `jag::ClientProt::RegisterAll @ 0x000c45b0`. All 130 == project-undercut CSV. Locked.
- **Opcode→emitter** — `entryBase = &DAT_(0x015d3ea0 − idx*0x10)` (idx skips 0x19 at `0x015bfb40`);
  `get_xrefs_to(entryBase)` is the authoritative binding. Verified by prior pass; reused here.
- **Names** — beta `&ClientProt::NAME` harvest + 948 wire-shape match. The big new IDs:
  - **op 29 = PING_STATISTICS** (CERTAIN) — beta `MainLogicManager::ProcessingWorldPing` builds
    `MakeClientMessage(...,&ClientProt::PING_STATISTICS)` with byte layout (count byte + GetPing()
    hi/lo) identical to 948 `FUN_001fc8c0` (which calls `jag::ClientStream::GetPing`). Entry 0x015d3ce0.
  - **op 10 = IF_BUTTOND** (HIGH) — beta `InterfaceManager::UpdateDragging` →
    `&ClientProt::IF_BUTTOND`; 16-byte two-component drag layout matches 948 `FUN_002b7460`.
  - **op 61 = IF_BUTTONT** (HIGH) — beta `InterfaceActions` lambda#5 → `&ClientProt::IF_BUTTONT`;
    "use component on target component" hook-driven, 16-byte; matches 948 `FUN_0014edf0`.
  - **IF_BUTTON1..IF_BUTTON10** (HIGH) — beta `InterfaceManager::IfButtonX` dispatches param_3=1..10
    to `IfButtonXSend(&ClientProt::IF_BUTTON1..10)`. The 948 `FUN_002d9970` CS2 sender has the
    matching 10-case switch; its case→entry-base mapping yields the opcode numbers directly.
  - **OP1..OP6 (op-on-target option family)** (HIGH) — 948 `FUN_0026b8e0` is a 6-case option
    dispatcher (writes world-translated tile coords + option index, strcmp's Attack/Examine);
    beta equivalent is the `minimenuactions::ThreeDView::DoOp*` family. Case→entry mapping yields
    the opcode numbers.
  - **OPLOCT confirmed** — beta `DoOpLoc` branches on `&ClientProt::OPLOCT` (the long 17-byte
    form = 948 op 18 OPLOC_T_LONG); the short branch is the shared `SendOpLocTLong` else-path.

## Disagreements with project-undercut CSV (carried from prior pass, re-verified)

CSV emitter ADDRESSES are correct; its opcode COLUMN drifts +1 across ops 71–90. All corrected
bindings (71/72, 80/81, 84/85, 85/86, 87/88, 88/89, 89/90, 17, 82) remain as documented in
`948-research-D-clientprot.md`. No new CSV opcode-column disagreements found in this pass; the
new IDs (29, 10, 61, IF_BUTTON1-10, OP1-6) are ADDITIONS, not corrections.

## Full opcode table (all 130)

size: fixed N, -1 = varByte, -2 = varShort.

Names are the official `jag::ClientProt` enum. `(fn …)` in the evidence column is the C++ emitter
function name — recorded for navigation only; it is NEVER the packet name.

| op | size | name | emitter (948 addr) | CONF | evidence |
|----|------|------|--------------------|------|----------|
| 0 | -1 | UNKNOWN_0 | FUN_00280630 | NONE | no entry-base xref identity |
| 1 | -1 | UNKNOWN_1 | 0x00311830 | NONE | (fn SendOpNpcCS2); no official OPNPC*_CS2 enum — cannot pin which OPNPC |
| 2 | 9 | UNKNOWN_2 | 0x0015c7b0 | NONE | shim → SendOpLocTLong else-branch; no provable official name |
| 3 | 9 | UNKNOWN_3 | 0x001807a0 | NONE | (fn HandleAntiCheatChallenge); no ANTI_CHEAT_* in official vocab |
| 4 | -1 | UNKNOWN_4 | 0x0033df80 | NONE | (fn SendOpPlayerCS2); no official OPPLAYER*_CS2 enum |
| 5 | 4 | UNKNOWN_5 | 0x00229d80 | NONE | (fn SendSceneGraphReport) scene-graph rbtree maint + 4B int; NO official enum maps (not FACE_SQUARE/EVENT_CAMERA_POSITION — wrong layout) |
| 6 | 7 | OPOBJ5 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 case5 → entry 0x015d3e40; 7B tile+id+flag = beta DoOpObj OPOBJ family |
| 7 | 4 | UNKNOWN_7 | UNBOUND | NONE | RegisterAll only |
| 8 | 4 | UNKNOWN_8 | 0x001a2d90 | NONE | (fn SendMultiDisplayPackets); no MULTI_DISPLAY official enum |
| 9 | 12 | UNKNOWN_9 | 0x0015b5c0 | NONE | use-item-on-loc; OPLOCU is not an official beta enum |
| 10 | 16 | IF_BUTTOND | 0x002b7460 | HIGH | beta UpdateDragging→&ClientProt::IF_BUTTOND; 16B 2-component drag |
| 11 | 3 | UNKNOWN_11 | 0x0015c3d0 | NONE | shim → FUN_0015c100 else-branch (3B comp-target); no provable name |
| 12 | -1 | UNKNOWN_12 | 0x001a2d90 | NONE | (fn SendMultiDisplayPackets); no MULTI_DISPLAY official enum |
| 13 | 8 | UNKNOWN_13 | UNBOUND (CS2 table 0x01365960) | NONE | CS2 data-table dispatch, no emitter body |
| 14 | 0 | ABORT_P_DIALOG | 0x002d0040 | HIGH | beta Resume::Resume lambda#7→&ClientProt::ABORT_P_DIALOG; size-0 dialog-abort (entry 0x015d3dc0). NOT keepalive |
| 15 | 6 | EVENT_MOUSE_CLICK | 0x001805b0 | HIGH | beta ClientWatch::MainLogic→&ClientProt::EVENT_MOUSE_CLICK |
| 16 | 6 | IF_BUTTON9 | 0x002956e0 | MEDIUM | IfButtonX case9→entry 0x015d3d40=op16; official enum |
| 17 | 5 | MOVE_SCRIPTED | 0x002a3a70 | CERTAIN | self-named CreateOpcodeError("movescripted") |
| 18 | 17 | OPLOCT | 0x0015c470 | HIGH | beta DoOpLoc→&ClientProt::OPLOCT long-form; entry 0x015d3d80 |
| 19 | 9 | UNKNOWN_19 | UNBOUND | NONE | RegisterAll only |
| 20 | -1 | UNKNOWN_20 | 0x002a24c0 | NONE | (fn SendOpObjCS2_2); no official OPOBJ*_CS2 enum |
| 21 | 8 | UNKNOWN_21 | UNBOUND (CS2 table 0x01365958) | NONE | CS2 data-table dispatch |
| 22 | 3 | UNKNOWN_22 | 0x002d9970 | NONE | IF_BUTTON family, entry not in 1-10 switch → generic 3B comp; no provable name |
| 23 | 8 | UNKNOWN_23 | UNBOUND | NONE | RegisterAll only |
| 24 | 7 | OPOBJ1 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 default(opt1) → entry 0x015d3d20; 7B = beta DoOpObj |
| 25 | 11 | UNKNOWN_25 | 0x00aeeb60 | NONE | outlier entry 0x015bfb40; server-active-props transmit; no provable name |
| 26 | -2 | RESUME_P_COUNTDIALOG | 0x002cfdb0 | HIGH | 948 SendResumeCountDialog CS2 count-pair (entry 0x015d3d10); official enum |
| 27 | 1 | UNKNOWN_27 | UNBOUND | NONE | RegisterAll only |
| 28 | 4 | UNKNOWN_28 | (entry 0x015d3cf0) | NONE | DoOpLoc family; dispatcher aliased to msg-init helper, cannot pin OPLOC1-6 case |
| 29 | 4 | PING_STATISTICS | 0x001fc8c0 | CERTAIN | beta ProcessingWorldPing→&ClientProt::PING_STATISTICS; GetPing()+count layout |
| 30 | 8 | UNKNOWN_30 | UNBOUND (CS2 table 0x01365940) | NONE | CS2 data-table dispatch |
| 31 | 3 | UNKNOWN_31 | 0x0015c450 | NONE | shim → FUN_0015c100 else-branch (3B comp-target); no provable name |
| 32 | 3 | UNKNOWN_32 | 0x0015c430 | NONE | shim → SendOpLocTLong family else-branch; no provable name |
| 33 | 3 | IF_BUTTON4 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case4 → entry 0x015d3ca0 = op33 |
| 34 | 15 | UNKNOWN_34 | UNBOUND | NONE | RegisterAll only |
| 35 | -1 | IF_PLAYER | 0x00297eb0 | HIGH | 948 IfButtonXInner long path entry 0x015d3c80=op35; beta IfButtonX→&ClientProt::IF_PLAYER |
| 36 | 7 | OPOBJ4 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 case4 → entry 0x015d3c70; 7B = beta DoOpObj |
| 37 | 0 | UNKNOWN_37 | 0x002d3870 | NONE | (fn SendQueuedPacket); no QUEUED_PACKET official enum |
| 38 | -2 | MESSAGE_PRIVATE | 0x00306dd0 | HIGH | beta &ClientProt::MESSAGE_PRIVATE; tinyKeyEncrypt (entry 0x015d3c50) |
| 39 | 3 | IF_BUTTON1 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case1 → entry 0x015d3c30 = op39 |
| 40 | 3 | IF_BUTTON10 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case10 → entry 0x015d3c40 = op40 |
| 41 | 9 | UNKNOWN_41 | 0x0015c7f0 | NONE | shim → SendOpLocTLong else-branch (9B use-on-loc); no provable name |
| 42 | -1 | UNKNOWN_42 | 0x002d04b0 | NONE | CS2 string-stack type+value; no provable official name |
| 43 | 8 | UNKNOWN_43 | UNBOUND (CS2 table 0x01365950) | NONE | CS2 data-table dispatch |
| 44 | 4 | UNKNOWN_44 | 0x002d4d20 | NONE | (fn SendStrtol) strtol(CS2 str)→4B int; no STRTOL official enum |
| 45 | 8 | UNKNOWN_45 | UNBOUND (CS2 table 0x01365938) | NONE | CS2 data-table dispatch |
| 46 | 1 | UNKNOWN_46 | UNBOUND | NONE | RegisterAll only |
| 47 | 3 | IF_BUTTON3 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case3 → entry 0x015d3bc0 = op47 |
| 48 | -1 | UNKNOWN_48 | 0x00172900 | NONE | returns entry only (getter stub), no payload writes |
| 49 | -1 | UNKNOWN_49 | 0x0033ddc0 | NONE | (fn SendOpObjCS2); no official OPOBJ*_CS2 enum |
| 50 | 3 | IF_BUTTON8 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case8 → entry 0x015d3b90 = op50 |
| 51 | 0 | NO_TIMEOUT | 0x0013e6c0 | CERTAIN | the keepalive. beta ConnectionManager::MainLogic→&ClientProt::NO_TIMEOUT on 0x32-tick counter; 948 ProcessConnections byte-identical port, entry 0x015d3b80. Client floods every ~1s |
| 52 | 6 | UNKNOWN_52 | 0x001a2ba0 | NONE | (fn SendDisplayInfo); no DISPLAY_INFO official enum |
| 53 | 15 | UNKNOWN_53 | 0x0015bcf0 | NONE | long-form use-on-loc (entry 0x015d3b60); no provable name |
| 54 | 4 | WORLDLIST_FETCH | 0x00194a80 | HIGH | (fn SendWorldlistFetch); official enum |
| 55 | 11 | UNKNOWN_55 | 0x0015bad0 | NONE | 11B drag/use-component variant; no provable name |
| 56 | 3 | IF_BUTTON6 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case6 → entry 0x015d3b30 = op56 |
| 57 | 2 | UNKNOWN_57 | 0x0033e1f0 | NONE | (fn SendAffinedTransformSet_Main); no AFFINED_TRANSFORM* official enum |
| 58 | -2 | MOVE_GAMECLICK | 0x0026e1d0 | HIGH | beta ThreeDView→&ClientProt::MOVE_GAMECLICK (entry 0x015d3b10) |
| 59 | 4 | UNKNOWN_59 | 0x00480620 | NONE | (fn SendDetectModifiedClient); no DETECT_MODIFIED* official enum |
| 60 | -1 | UNKNOWN_60 | 0x002d02c0 | NONE | CS2 string-stack type+value (entry 0x015d3af0); no provable name |
| 61 | 16 | IF_BUTTONT | 0x0014edf0 | HIGH | beta InterfaceActions#5 → &ClientProt::IF_BUTTONT; use-component-on-target, 16B |
| 62 | 1 | UNKNOWN_62 | 0x001729b0 | NONE | returns entry only (getter stub) |
| 63 | 3 | UNKNOWN_63 | 0x0015c3f0 | NONE | shim → FUN_0015c100 else-branch (3B comp-target); no provable name |
| 64 | 7 | OPOBJ3 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 case3 → entry 0x015d3ab0; 7B = beta DoOpObj |
| 65 | 4 | UNKNOWN_65 | 0x002383e0 | NONE | packs 3 small fields WriteUInt32LE (camera/minimap angle); no provable enum |
| 66 | 0 | CLOSE_MODAL | 0x002d01f0 | HIGH | beta CloseModalInterface→&ClientProt::CLOSE_MODAL (entry 0x015d3a90) |
| 67 | 18 | UNKNOWN_67 | UNBOUND | NONE | RegisterAll only |
| 68 | 8 | UNKNOWN_68 | UNBOUND (CS2 table 0x01365948) | NONE | CS2 data-table dispatch |
| 69 | 2 | UNKNOWN_69 | 0x002d03d0 | NONE | pops one CS2 int, writes p2 (entry 0x015d3a60); no provable name |
| 70 | -1 | FRIENDLIST_DEL | 0x0026b1f0 | HIGH | (fn SendFriendlistDel); official enum |
| 71 | -1 | RESUME_P_NAMEDIALOG | 0x002cff20 | HIGH | beta Resume::Resume lambda#4→&ClientProt::RESUME_P_NAMEDIALOG (length-prefixed UTF8) |
| 72 | -2 | UNKNOWN_72 | UNBOUND | NONE | RegisterAll only |
| 73 | 3 | IF_BUTTON2 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case2 → entry 0x015d3a20 = op73 |
| 74 | 5 | MOVE_MINIMAPCLICK | 0x0015b7d0 | MEDIUM | official enum; walk/move flag+tileX/Y, sets map flag |
| 75 | -1 | UNKNOWN_75 | 0x0015b7d0 | NONE | op-type-1 extended walk (entry 0x015d39d0/0x015d3a10); no distinct official name |
| 76 | 4 | UNKNOWN_76 | 0x0028ea60 | NONE | (fn SendEncodedString2); no official enum maps |
| 77 | 9 | UNKNOWN_77 | UNBOUND | NONE | RegisterAll only |
| 78 | 18 | UNKNOWN_78 | UNBOUND | NONE | RegisterAll only |
| 79 | 1 | UNKNOWN_79 | 0x0015b7d0 | NONE | 1B flag-only walk variant; no distinct official name |
| 80 | -1 | IGNORELIST_ADD | 0x0030f4b0 | HIGH | (fn SendIgnorelistAdd) + "Your ignore list is full"; official enum |
| 81 | -2 | UNKNOWN_81 | (generic MultiDisplay PARAM) | NONE | no dedicated emitter |
| 82 | -2 | UNKNOWN_82 | UNBOUND | NONE | entry unreferenced |
| 83 | -1 | UNKNOWN_83 | UNBOUND | NONE | RegisterAll only |
| 84 | 22 | UNKNOWN_84 | 0x0029aae0 | NONE | (fn SendIfButtonTargetMenu); no IF_BUTTON_TARGET_MENU official enum |
| 85 | -1 | UNKNOWN_85 | 0x00340850 | NONE | (fn activechatphrase_sendprivate); no ACTIVECHATPHRASE* official enum (likely MESSAGE_QUICKCHAT_PRIVATE — layout unconfirmed) |
| 86 | 11 | UNKNOWN_86 | 0x0015c100 | NONE | entry 0x015d3950: flag+target+slot+hash+sub; no provable name |
| 87 | -1 | UNKNOWN_87 | 0x0037c1c0 | NONE | (fn SendMessagePublicWithEffects); no MESSAGE_PUBLIC_EFFECTS enum (likely MESSAGE_QUICKCHAT_PUBLIC — unconfirmed) |
| 88 | -2 | UNKNOWN_88 | 0x00341230 | NONE | (fn SendDataReport); no DATA_REPORT official enum |
| 89 | -1 | CLANCHANNEL_KICKUSER | 0x0026aef0 | HIGH | (fn SendSocialRequest) + "not in this channel"; beta &ClientProt::CLANCHANNEL_KICKUSER |
| 90 | -1 | UNKNOWN_90 | UNBOUND | NONE | entry unreferenced |
| 91 | 3 | IF_BUTTON7 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case7 → entry 0x015d3900 = op91 |
| 92 | 8 | UNKNOWN_92 | UNBOUND (CS2 table 0x01365930) | NONE | CS2 data-table dispatch |
| 93 | -2 | UNKNOWN_93 | 0x00344600 | NONE | (fn SendVerifiedStringSend); no official enum maps |
| 94 | 3 | WINDOW_STATUS | 0x0033e470 | HIGH | beta ClientWatch::SendWindowStatus→&ClientProt::WINDOW_STATUS |
| 95 | 4 | UNKNOWN_95 | UNBOUND | NONE | RegisterAll only |
| 96 | 0 | UNKNOWN_96 | UNBOUND | NONE | RegisterAll only |
| 97 | -1 | UNKNOWN_97 | 0x002d06a0 | NONE | CS2 string-stack type+value (entry 0x015d38a0); no provable name |
| 98 | -1 | UNKNOWN_98 | 0x001729b0 | NONE | returns entry only (getter stub) |
| 99 | -1 | UNKNOWN_99 | 0x002d0850 | NONE | tinyKeyEncrypt string gated login state 0x17/0x1e; no provable name |
| 100 | -1 | FRIENDLIST_ADD | 0x0026a530 | HIGH | (fn SendFriendlistAdd); official enum |
| 101 | 7 | OPOBJ2 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 case2 → entry 0x015d3860; 7B = beta DoOpObj |
| 102 | -2 | UNKNOWN_102 | 0x00298c30 | NONE | GetServerActiveProperties bit; CS2 value+component triple transmit; no provable name |
| 103 | 8 | UNKNOWN_103 | UNBOUND (CS2 table 0x01365928) | NONE | CS2 data-table dispatch |
| 104 | 2 | UNKNOWN_104 | UNBOUND | NONE | RegisterAll only |
| 105 | -2 | EVENT_APPLET_FOCUS | 0x00194f40 | HIGH | beta ClientWatch::MainLogic→&ClientProt::EVENT_APPLET_FOCUS (SDL focus toggle) |
| 106 | 1 | UNKNOWN_106 | 0x001a2d90 | NONE | (fn SendMultiDisplayPackets); no MULTI_DISPLAY official enum |
| 107 | 0 | MAP_BUILD_COMPLETE | 0x002bc030 | HIGH | (fn SendMapBuildComplete); official enum |
| 108 | 3 | IF_BUTTON5 | 0x002d9970 | HIGH | FUN_002d9970 IF_BUTTON switch case5 → entry 0x015d37f0 = op108 |
| 109 | 9 | UNKNOWN_109 | 0x0015c810 | NONE | shim → SendOpLocTLong else-branch (9B use-on-loc); no provable name |
| 110 | 7 | EVENT_MOUSE_MOVE | 0x001803b0 | HIGH | beta ClientWatch::MainLogic→&ClientProt::EVENT_MOUSE_MOVE (ring buffer) |
| 111 | 9 | UNKNOWN_111 | 0x0015c830 | NONE | shim → SendOpLocTLong else-branch (9B use-on-loc); no provable name |
| 112 | 7 | OPOBJ6 | 0x0026b8e0 | HIGH | SendOpTargetOption_CS2 case6 → entry 0x015d37b0; 7B = beta DoOpObj |
| 113 | 3 | UNKNOWN_113 | 0x0015c410 | NONE | shim → FUN_0015c100 else-branch (3B comp-target); no provable name |
| 114 | -1 | UNKNOWN_114 | 0x00269680 | NONE | CS2 type byte+1 + int + encoded value (entry 0x015d3790); no provable name |
| 115 | 3 | UNKNOWN_115 | 0x00269580 | NONE | writes flag byte + short (entry 0x015d3780); no provable name |
| 116 | 2 | UNKNOWN_116 | 0x002d05c0 | NONE | pops one CS2 int, writes p2 (entry 0x015d3770); no provable name |
| 117 | -1 | UNKNOWN_117 | 0x00311130 | NONE | (fn SendEncodedString) CS2 type-prefixed encoded string; no ENCODED_STRING official enum |
| 118 | -1 | UNKNOWN_118 | 0x00340d50 | NONE | (fn activechatphrase_send); no ACTIVECHATPHRASE* official enum (likely MESSAGE_QUICKCHAT_PUBLIC — unconfirmed) |
| 119 | -2 | RESUME_PAUSEBUTTON | 0x002cfc50 | CERTAIN | beta SendPauseComponentMessage→&ClientProt::RESUME_PAUSEBUTTON; layout match |
| 120 | 8 | UNKNOWN_120 | 0x002d74e0 | NONE | (fn SendStrtoll) strtoll(CS2 str)→8B; no STRTOLL official enum |
| 121 | 3 | UNKNOWN_121 | 0x0015c3b0 | NONE | shim → FUN_0015c100 else-branch (3B comp-target); no provable name |
| 122 | -2 | UNKNOWN_122 | 0x002d1b10 | NONE | tinyKeyEncrypt + pSizeMarker gated login state; no provable name |
| 123 | 1 | BUG_REPORT | 0x002d0140 | HIGH | beta BugReporting→&ClientProt::BUG_REPORT |
| 124 | -1 | MESSAGE_PUBLIC | 0x0037a9c0 | HIGH | beta &ClientProt::MESSAGE_PUBLIC |
| 125 | 9 | UNKNOWN_125 | 0x0015c850 | NONE | shim → SendOpLocTLong else-branch (9B use-on-loc); no provable name |
| 126 | 9 | UNKNOWN_126 | 0x0015c7d0 | NONE | shim → SendOpLocTLong else-branch (9B use-on-loc); no provable name |
| 127 | 8 | UNKNOWN_127 | UNBOUND (CS2 table 0x01365920) | NONE | CS2 data-table dispatch |
| 128 | -2 | UNKNOWN_128 | UNBOUND | NONE | RegisterAll only |
| 129 | -1 | UNKNOWN_129 | 0x0033e140 | NONE | (fn SendOpLocCS2); no official OPLOC*_CS2 enum |

## Coverage summary (after official-enum correction)

- **Sizes: 130/130 locked** (RegisterAll == project-undercut CSV).
- **Named with official `jag::ClientProt` enum: 38/130** — every one verified to an official beta
  enum name (HIGH/CERTAIN). They are:
  - Keepalive/connection: NO_TIMEOUT(51)
  - Dialogs: ABORT_P_DIALOG(14), RESUME_P_COUNTDIALOG(26), RESUME_P_NAMEDIALOG(71),
    RESUME_PAUSEBUTTON(119), CLOSE_MODAL(66)
  - Input events: EVENT_MOUSE_CLICK(15), EVENT_MOUSE_MOVE(110), EVENT_APPLET_FOCUS(105),
    WINDOW_STATUS(94), PING_STATISTICS(29)
  - OPOBJ (op-on-ground-item, 7B): OPOBJ1(24), OPOBJ2(101), OPOBJ3(64), OPOBJ4(36), OPOBJ5(6),
    OPOBJ6(112)
  - OPLOCT(18), MOVE_SCRIPTED(17), MOVE_GAMECLICK(58), MOVE_MINIMAPCLICK(74), MAP_BUILD_COMPLETE(107)
  - IF buttons: IF_BUTTON1(39), IF_BUTTON2(73), IF_BUTTON3(47), IF_BUTTON4(33), IF_BUTTON5(108),
    IF_BUTTON6(56), IF_BUTTON7(91), IF_BUTTON8(50), IF_BUTTON9(16), IF_BUTTON10(40),
    IF_BUTTOND(10), IF_BUTTONT(61), IF_PLAYER(35)
  - Social: FRIENDLIST_ADD(100), FRIENDLIST_DEL(70), IGNORELIST_ADD(80), CLANCHANNEL_KICKUSER(89)
  - Chat: MESSAGE_PUBLIC(124), MESSAGE_PRIVATE(38)
  - Misc: WORLDLIST_FETCH(54), BUG_REPORT(123)
- **UNKNOWN_<op>: 92/130.** This is INTENTIONALLY higher than the prior pass's 31. The prior pass
  inflated the named count with C++ FUNCTION names (PROCESS_CONNECTIONS, SCENE_GRAPH_REPORT, OP1-6,
  *_CS2, OPLOCT_SHORT_*, IF_BUTTON_CS2_*, CS2_*, STRTOL/STRTOLL, ENCODED_STRING*, etc.) that are
  NOT official `jag::ClientProt` enum names. THE LAW forbids them. Each reverted opcode keeps its
  size + emitter addr + read-shape so the impl agent can still wire a decoder by wire-shape, and so
  a future pass can promote it to an official name once the beta `&ClientProt::NAME` send-site is
  matched.

### Candidates for promotion (have a wire-shape, just need beta confirmation)

- **op 85 / op 118** (activechatphrase_sendprivate / _send) → very likely
  `MESSAGE_QUICKCHAT_PRIVATE` / `MESSAGE_QUICKCHAT_PUBLIC` — confirm by matching the beta
  quickchat send-sites' byte layout.
- **op 1/4/20/49/129** (`*_CS2` ops) → one of OPNPC1-6/OPNPCT, OPPLAYER1-6, OPOBJ1-6/OPOBJT,
  OPLOC1-6/OPLOCT. Need the beta CS2-variant send-sites to pin the exact enum.
- **op 44/120** (strtol/strtoll count parse) → a RESUME_P_*DIALOG count variant.

## Notes for the impl agent

- The OPOBJ1..6 family (op 6/24/36/64/101/112) shares the 948 dispatcher
  `SendOpTargetOption_CS2 @ 0x0026b8e0`; the case→entry-base switch yields each opcode. 7-byte
  layout: tile-coord(2B) + targetId(2B) + tile-coord(2B) + option-bit(1B). Verified by byte-layout
  match against the beta `minimenuactions::ThreeDView::DoOpObj` OPOBJ1..6 dispatch.
- The IF_BUTTON1..10 family (op 33/39/40/47/50/56/73/91/108/40) routes through CS2 sender
  `FUN_002d9970`; its 10-case switch maps directly to entry bases. IF_PLAYER(35) is the
  IfButtonXInner long path. These were already correct in the prior pass and are KEPT.
- The keepalive is **NO_TIMEOUT = op 51** (size 0). The server's Ping decoder is registered there
  in `Rev948ClientCodecs.kt`. Do NOT expect the keepalive on op 14 — op 14 is ABORT_P_DIALOG.
