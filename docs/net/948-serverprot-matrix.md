# 948 ServerProt Evidence Matrix (Phase 3 re-validation)

Target: `rs2client.948-2-2` (Ghidra, STRIPPED) — AUTHORITY for size/handler.
Official enum source: `librs2client.so` beta (rev ~890) `jag::ServerProt::*` ProtEntry globals @ 0x00a35800..0x00a387c0 (192 symbols dumped & verified this session).

## THE GATE

- **size(A)** — from `docs/net/948-prot-tables-dump.csv` (RegisterAll/InitEntry). Authoritative; never re-derived.
- **beta_enum_name(B)** — a *verbatim* official `jag::ServerProt::<NAME>` symbol whose packet semantics structurally match the 948 handler. NEVER the 948 C++ function name; NEVER a descriptive invention.
- **in_capture(C)** — opcode observed on the wire in `capture/login-20260531-191837_s1/decoded.log`. capture_layout_ok = on-wire size consistent with A.
- **size_sanity(D)** — VETO: a name whose conventional shape contradicts the size forces UNKNOWN (string/list/_FULL/INFO must be variable; keepalive/reset=0; VARP_SMALL=3/VARP_LARGE=6/VARBIT_SMALL=3/VARBIT_LARGE=6; CLIENT_SETVARC_SMALL=3/_LARGE=6).

**Verdict rules:**
- **CONFIRMED** — verbatim beta enum maps via STRONG beta-handler↔948-handler match AND size-sanity passes AND (where in capture) wire layout consistent. Minimum two independent agreeing facts (beta-match + size-sanity, plus capture/decompile where available).
- **PROBABLE** — single plausible beta lead, match not strong, or loose 947-carryover; size-sanity passes. (This pass found 0.)
- **UNKNOWN_<op>** — no official enum maps with confidence. Size still authoritative.

**Anti-fake:** C++ fn-name is NEVER a name. 'presumed from 947' is a lead, not evidence. No single-weak-inference CONFIRMED. Every CONFIRMED cites beta enum addr (B) + 948 handler addr (A) [+ capture (C) where available].

## Counts: **CONFIRMED=78 / PROBABLE=0 / UNKNOWN=140** of 218

Notable verdicts this pass:
- **RECOVERY** op5 → `RESET_CLIENT_VARCACHE` (prior stub UNKNOWN_5; anchored on C++ fn-name `RESET_ALL_VARPS`). Handler @0x119a50 clears player+varc domains; capture label `ResetClientVarcache`; beta enum exists.
- **VETO/DEMOTE** op147 `VARP_LONG` and op196 `CLIENT_SETVARC_LONG` → UNKNOWN. Handlers are decompile-certain (player-varp-8B / client-varc-8B) but NO verbatim beta enum exists (beta var set = SMALL/LARGE only). Names rejected; sizes (10/10) retained.
- op35 `IF_SETEVENTS` NOT assigned (size 12). The verbatim beta `IF_SETEVENTS` (size 10) maps to op97; op35 is the second variant with no `IF_SETEVENTS2` beta symbol → UNKNOWN. Capture's 'IfSetEvents' label is stale-947-codec.
- op26 `UPDATE_SITESETTINGS` CONFIRMED (decompiled, reads 2 strings+smart+int, builds site-settings record). Capture's 'FriendStatus/size=0' is a stale-947-codec mislabel (varShort, empty payload).
- op128 stays UNKNOWN: empty periodic keepalive (handler FUN_00173e40 empty); no strong structural match to any beta enum (e.g. SEND_PING) — empty handler is not a strong fact.

## Matrix (218 rows)

| opcode | size(A) | handler_addr(A) | beta_enum_name(B) | beta_addr(B) | in_capture(C) | capture_layout_ok(C) | size_sanity(D) | current_stub | FINAL_NAME | CONFIDENCE | evidence |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 0 | varByte(-1) | 0x013a1d08 | MESSAGE_QUICKCHAT_CLANCHANNEL | 0x00a37b00 | no | - | PASS | MESSAGE_QUICKCHAT_CLANCHANNEL | MESSAGE_QUICKCHAT_CLANCHANNEL | CONFIRMED | beta MESSAGE_QUICKCHAT_CLANCHANNEL@0x00a37b00(B) + size-sane sz-1(D) |
| 1 | varByte(-1) | 0x013a1cc8 | - | - | no | - | PASS | UNKNOWN_1 | UNKNOWN_1 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=CLIENT_SETVARC_SMALL. |
| 2 | varByte(-1) | 0x013a1c88 | - | - | no | - | PASS | UNKNOWN_2 | UNKNOWN_2 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_2. |
| 3 | 19 | 0x015c0c28 | - | - | YES | OK | PASS | UNKNOWN_3 | UNKNOWN_3 | UNKNOWN | no verbatim beta enum maps; size19(A) authoritative. 947=CAM_FORCEANGLE. capture label 'IfSetTopLevelInterface' is stale-947-codec, not a verbatim beta enum (size19 matches A). |
| 4 | 32 | 0x015c0b28 | IF_SETANGLE | 0x00a35c00 | no | - | PASS | IF_SETANGLE | IF_SETANGLE | CONFIRMED | beta IF_SETANGLE@0x00a35c00(B) + size-sane sz32(D) |
| 5 | 0 | 0x015c1728 | RESET_CLIENT_VARCACHE | 0x00a368c0 | YES | OK | PASS | UNKNOWN_5 | RESET_CLIENT_VARCACHE | CONFIRMED | RECOVERY: beta RESET_CLIENT_VARCACHE@0x00a368c0(B) + handler @0x119a50 resets BOTH player(+0x19b60)&varc(+0x35c08) domains(948 decomp) + capture ResetClientVarcache sz0(C) + size-sane sz0(D). Prior stub anchored on C++ fn-name RESET_ALL_VARPS and missed the beta enum. |
| 6 | 7 | 0x015c1e68 | LOC_PREFETCH | 0x00a35a00 | no | - | PASS | LOC_PREFETCH | LOC_PREFETCH | CONFIRMED | beta LOC_PREFETCH@0x00a35a00(B) + size-sane sz7(D) |
| 7 | 0 | 0x013a1c48 | - | - | no | - | PASS | UNKNOWN_7 | UNKNOWN_7 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=MESSAGE_FRIENDCHANNEL. |
| 8 | 5 | 0x015c0468 | - | - | no | - | PASS | UNKNOWN_8 | UNKNOWN_8 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=IF_SETPOSITION. |
| 9 | varShort(-2) | 0x013a1c08 | - | - | no | - | PASS | UNKNOWN_9 | UNKNOWN_9 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_9. |
| 10 | 3 | 0x015c1628 | VARBIT_SMALL | 0x00a38140 | no | - | PASS | VARBIT_SMALL | VARBIT_SMALL | CONFIRMED | beta VARBIT_SMALL@0x00a38140(B) + size-sane sz3(D) |
| 11 | varShort(-2) | 0x013a1bc8 | MESSAGE_PUBLIC | 0x00a38580 | no | - | PASS | MESSAGE_PUBLIC | MESSAGE_PUBLIC | CONFIRMED | beta MESSAGE_PUBLIC@0x00a38580(B) + size-sane sz-2(D) |
| 12 | 2 | 0x015c0ea8 | - | - | no | - | PASS | UNKNOWN_12 | UNKNOWN_12 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=NPC_INFO. |
| 13 | 1 | 0x015c0ee8 | - | - | no | - | PASS | UNKNOWN_13 | UNKNOWN_13 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_13. |
| 14 | 8 | 0x015c05a8 | - | - | no | - | PASS | UNKNOWN_14 | UNKNOWN_14 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=UNKNOWN_14. |
| 15 | 11 | 0x013a1b88 | - | - | no | - | PASS | UNKNOWN_15 | UNKNOWN_15 | UNKNOWN | no verbatim beta enum maps; size11(A) authoritative. 947=UNKNOWN_15. |
| 16 | 2 | 0x015c1ee8 | LOC_DEL | 0x00a364c0 | no | - | PASS | LOC_DEL | LOC_DEL | CONFIRMED | beta LOC_DEL@0x00a364c0(B) + size-sane sz2(D) |
| 17 | varByte(-1) | 0x015c0f28 | SET_PLAYER_OP | 0x00a37c40 | no | - | PASS | SET_PLAYER_OP | SET_PLAYER_OP | CONFIRMED | beta SET_PLAYER_OP@0x00a37c40(B) + size-sane sz-1(D) |
| 18 | 1 | 0x013a1b48 | - | - | no | - | PASS | UNKNOWN_18 | UNKNOWN_18 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UPDATE_ZONE_FULL_FOLLOWS. |
| 19 | varShort(-2) | 0x013a1b08 | - | - | no | - | PASS | UNKNOWN_19 | UNKNOWN_19 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UPDATE_RUNENERGY. |
| 20 | 3 | 0x013a1ac8 | - | - | no | - | PASS | UNKNOWN_20 | UNKNOWN_20 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_20. |
| 21 | 10 | 0x015c1a68 | LOC_ANIM_SPECIFIC | 0x00a37840 | no | - | PASS | LOC_ANIM_SPECIFIC | LOC_ANIM_SPECIFIC | CONFIRMED | beta LOC_ANIM_SPECIFIC@0x00a37840(B) + size-sane sz10(D) |
| 22 | varShort(-2) | 0x015c0f68 | PLAYER_INFO | 0x00a36a00 | no | - | PASS | PLAYER_INFO | PLAYER_INFO | CONFIRMED | beta PLAYER_INFO@0x00a36a00(B) + size-sane sz-2(D) |
| 23 | varShort(-2) | 0x013a1a88 | - | - | no | - | PASS | UNKNOWN_23 | UNKNOWN_23 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_23. |
| 24 | varByte(-1) | 0x015c1328 | - | - | no | - | PASS | UNKNOWN_24 | UNKNOWN_24 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=NPC_OP. |
| 25 | varShort(-2) | 0x013a1a48 | - | - | no | - | PASS | UNKNOWN_25 | UNKNOWN_25 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_25. |
| 26 | varShort(-2) | 0x013a1a08 | UPDATE_SITESETTINGS | 0x00a371c0 | YES | OK | PASS | UPDATE_SITESETTINGS | UPDATE_SITESETTINGS | CONFIRMED | beta UPDATE_SITESETTINGS@0x00a371c0(B) + size-sane sz-2(D) + capture FriendStatus(stale) sz0(C) + 948 handler decompiled |
| 27 | 10 | 0x013a19c8 | - | - | no | - | PASS | UNKNOWN_27 | UNKNOWN_27 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=PLAYER_INFO. |
| 28 | 6 | 0x015c16a8 | VARP_LARGE | 0x00a36e40 | YES | OK | PASS | VARP_LARGE | VARP_LARGE | CONFIRMED | beta VARP_LARGE@0x00a36e40(B) + size-sane sz6(D) + capture VarpLarge sz6(C) + 948 handler decompiled |
| 29 | varShort(-2) | 0x013a1988 | CLANSETTINGS_FULL | 0x00a36f40 | no | - | PASS | CLANSETTINGS_FULL | CLANSETTINGS_FULL | CONFIRMED | beta CLANSETTINGS_FULL@0x00a36f40(B) + size-sane sz-2(D) |
| 30 | 8 | 0x015c0928 | - | - | no | - | PASS | UNKNOWN_30 | UNKNOWN_30 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=CHANGE_LOBBY. |
| 31 | 6 | 0x013a1948 | - | - | no | - | PASS | UNKNOWN_31 | UNKNOWN_31 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=UNKNOWN_31. |
| 32 | 8 | 0x015c0768 | IF_SETCOLOUR | 0x00a36580 | no | - | PASS | IF_SETCOLOUR | IF_SETCOLOUR | CONFIRMED | beta IF_SETCOLOUR@0x00a36580(B) + size-sane sz8(D) |
| 33 | varByte(-1) | 0x013a1908 | - | - | no | - | PASS | UNKNOWN_33 | UNKNOWN_33 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=IF_CLOSESUB. |
| 34 | 8 | 0x013a18c8 | - | - | no | - | PASS | UNKNOWN_34 | UNKNOWN_34 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=IF_SETEVENTS. |
| 35 | 12 | 0x015c0a28 | - | - | YES | OK | PASS | UNKNOWN_35 | UNKNOWN_35 | UNKNOWN | no verbatim beta enum maps; size12(A) authoritative. 947=IF_SETEVENTS2. capture label 'IfSetEvents' is stale-947-codec, not a verbatim beta enum (size12 matches A). |
| 36 | 10 | 0x013a1888 | - | - | no | - | PASS | UNKNOWN_36 | UNKNOWN_36 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=HANDSHAKE_UID. |
| 37 | varByte(-1) | 0x013a1848 | MESSAGE_FRIENDCHANNEL | 0x00a36900 | no | - | PASS | MESSAGE_FRIENDCHANNEL | MESSAGE_FRIENDCHANNEL | CONFIRMED | beta MESSAGE_FRIENDCHANNEL@0x00a36900(B) + size-sane sz-1(D) |
| 38 | 8 | 0x015c08e8 | - | - | no | - | PASS | UNKNOWN_38 | UNKNOWN_38 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=UNKNOWN_38. |
| 39 | 6 | 0x015c0c68 | IF_OPENTOP | 0x00a386c0 | no | - | PASS | IF_OPENTOP | IF_OPENTOP | CONFIRMED | beta IF_OPENTOP@0x00a386c0(B) + size-sane sz6(D) |
| 40 | 8 | 0x015c0a68 | - | - | no | - | PASS | UNKNOWN_40 | UNKNOWN_40 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=UNKNOWN_40. |
| 41 | 3 | 0x015c20a8 | UPDATE_ZONE_PARTIAL_FOLLOWS | 0x00a36bc0 | no | - | PASS | UPDATE_ZONE_PARTIAL_FOLLOWS | UPDATE_ZONE_PARTIAL_FOLLOWS | CONFIRMED | beta UPDATE_ZONE_PARTIAL_FOLLOWS@0x00a36bc0(B) + size-sane sz3(D) |
| 42 | 10 | 0x013a1808 | - | - | no | - | PASS | UNKNOWN_42 | UNKNOWN_42 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=UNKNOWN_42. |
| 43 | 12 | 0x013a17c8 | - | - | no | - | PASS | UNKNOWN_43 | UNKNOWN_43 | UNKNOWN | no verbatim beta enum maps; size12(A) authoritative. 947=RESET_ENTITY_LISTS. |
| 44 | 6 | 0x015c12e8 | UPDATE_STAT | 0x00a359c0 | YES | OK | PASS | UPDATE_STAT | UPDATE_STAT | CONFIRMED | beta UPDATE_STAT@0x00a359c0(B) + size-sane sz6(D) + capture UpdateStat sz6(C) + 948 handler decompiled |
| 45 | 1 | 0x013a1788 | - | - | no | - | PASS | UNKNOWN_45 | UNKNOWN_45 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=MESSAGE_PUBLIC. |
| 46 | 5 | 0x015c1c68 | OBJ_ADD | 0x00a35b00 | no | - | PASS | OBJ_ADD | OBJ_ADD | CONFIRMED | beta OBJ_ADD@0x00a35b00(B) + size-sane sz5(D) |
| 47 | 3 | 0x015c15a8 | CLIENT_SETVARC_SMALL | 0x00a38380 | YES | OK | PASS | CLIENT_SETVARC_SMALL | CLIENT_SETVARC_SMALL | CONFIRMED | beta CLIENT_SETVARC_SMALL@0x00a38380(B) + size-sane sz3(D) + capture ClientSetVarcSmall sz3(C) + 948 handler decompiled |
| 48 | 3 | 0x015c14e8 | CLIENT_SETVARCBIT_SMALL | 0x00a37a40 | no | - | PASS | CLIENT_SETVARCBIT_SMALL | CLIENT_SETVARCBIT_SMALL | CONFIRMED | beta CLIENT_SETVARCBIT_SMALL@0x00a37a40(B) + size-sane sz3(D) |
| 49 | varShort(-2) | 0x013a1748 | CHANGE_LOBBY | 0x00a38040 | YES | OK | PASS | CHANGE_LOBBY | CHANGE_LOBBY | CONFIRMED | beta CHANGE_LOBBY@0x00a38040(B) + size-sane sz-2(D) + capture ChangeLobby sz0(C) |
| 50 | varByte(-1) | 0x015c1f68 | LOC_CUSTOMISE | 0x00a37700 | no | - | PASS | LOC_CUSTOMISE | LOC_CUSTOMISE | CONFIRMED | beta LOC_CUSTOMISE@0x00a37700(B) + size-sane sz-1(D) |
| 51 | 6 | 0x015c15e8 | VARBIT_LARGE | 0x00a38100 | no | - | PASS | VARBIT_LARGE | VARBIT_LARGE | CONFIRMED | beta VARBIT_LARGE@0x00a38100(B) + size-sane sz6(D) |
| 52 | varShort(-2) | 0x013a1708 | NPC_INFO | 0x00a38600 | no | - | PASS | NPC_INFO | NPC_INFO | CONFIRMED | beta NPC_INFO@0x00a38600(B) + size-sane sz-2(D) |
| 53 | varShort(-2) |  | - | - | no | - | PASS | UNKNOWN_53 | UNKNOWN_53 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_53. |
| 54 | varByte(-1) | 0x013a1688 | NO_TIMEOUT | 0x00a35940 | no | - | PASS | NO_TIMEOUT | NO_TIMEOUT | CONFIRMED | beta NO_TIMEOUT@0x00a35940(B) + size-sane sz-1(D) + 948 handler decompiled |
| 55 | 0 | 0x015c13a8 | - | - | no | - | PASS | UNKNOWN_55 | UNKNOWN_55 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=CLIENT_SETVARCBIT_LARGE. |
| 56 | varByte(-1) | 0x013a1648 | MESSAGE_QUICKCHAT_PRIVATE | 0x00a37e00 | no | - | PASS | MESSAGE_QUICKCHAT_PRIVATE | MESSAGE_QUICKCHAT_PRIVATE | CONFIRMED | beta MESSAGE_QUICKCHAT_PRIVATE@0x00a37e00(B) + size-sane sz-1(D) |
| 57 | 6 | 0x013a1608 | - | - | no | - | PASS | UNKNOWN_57 | UNKNOWN_57 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=UPDATE_ZONE_PARTIAL_FOLLOWS. |
| 58 | 0 | 0x015c1368 | - | - | no | - | PASS | UNKNOWN_58 | UNKNOWN_58 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=MESSAGE_QUICKCHAT_CLANCHAT. |
| 59 | 10 | 0x015c0628 | - | - | no | - | PASS | UNKNOWN_59 | UNKNOWN_59 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=JCOINS_UPDATE. |
| 60 | 25 | 0x015c0b68 | - | - | no | - | PASS | UNKNOWN_60 | UNKNOWN_60 | UNKNOWN | no verbatim beta enum maps; size25(A) authoritative. 947=UNKNOWN_60. |
| 61 | 3 | 0x015c16e8 | VARP_SMALL | 0x00a37400 | YES | OK | PASS | VARP_SMALL | VARP_SMALL | CONFIRMED | beta VARP_SMALL@0x00a37400(B) + size-sane sz3(D) + capture VarpSmall sz3(C) + 948 handler decompiled |
| 62 | 4 | 0x015c0aa8 | IF_CLOSESUB | 0x00a38500 | no | - | PASS | IF_CLOSESUB | IF_CLOSESUB | CONFIRMED | beta IF_CLOSESUB@0x00a38500(B) + size-sane sz4(D) |
| 63 | 10 | 0x013a15c8 | - | - | no | - | PASS | UNKNOWN_63 | UNKNOWN_63 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=CLANSETTINGS_DELTA. |
| 64 | 6 | 0x015c1568 | CLIENT_SETVARC_LARGE | 0x00a35a80 | YES | OK | PASS | CLIENT_SETVARC_LARGE | CLIENT_SETVARC_LARGE | CONFIRMED | beta CLIENT_SETVARC_LARGE@0x00a35a80(B) + size-sane sz6(D) + capture ClientSetVarcLarge sz6(C) + 948 handler decompiled |
| 65 | 20 | 0x015c1de8 | MAP_PROJANIM | 0x00a37080 | no | - | PASS | MAP_PROJANIM | MAP_PROJANIM | CONFIRMED | beta MAP_PROJANIM@0x00a37080(B) + size-sane sz20(D) |
| 66 | 5 | 0x013a1588 | - | - | no | - | PASS | UNKNOWN_66 | UNKNOWN_66 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=UPDATE_STAT. |
| 67 | varShort(-2) | 0x013a1548 | CLANCHANNEL_FULL | 0x00a37280 | no | - | PASS | CLANCHANNEL_FULL | CLANCHANNEL_FULL | CONFIRMED | beta CLANCHANNEL_FULL@0x00a37280(B) + size-sane sz-2(D) |
| 68 | 10 | 0x015c03e8 | - | - | no | - | PASS | UNKNOWN_68 | UNKNOWN_68 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=IF_OPENTOP. |
| 69 | 6 | 0x015c14a8 | CLIENT_SETVARCBIT_LARGE | 0x00a38300 | no | - | PASS | CLIENT_SETVARCBIT_LARGE | CLIENT_SETVARCBIT_LARGE | CONFIRMED | beta CLIENT_SETVARCBIT_LARGE@0x00a38300(B) + size-sane sz6(D) |
| 70 | 25 | 0x015c0ba8 | - | - | no | - | PASS | UNKNOWN_70 | UNKNOWN_70 | UNKNOWN | no verbatim beta enum maps; size25(A) authoritative. 947=UNKNOWN_70. |
| 71 | 7 | 0x015c1ae8 | OBJ_REVEAL | 0x00a35fc0 | no | - | PASS | OBJ_REVEAL | OBJ_REVEAL | CONFIRMED | beta OBJ_REVEAL@0x00a35fc0(B) + size-sane sz7(D) |
| 72 | 1 | 0x013a1508 | CAM_FORCEANGLE | 0x00a37200 | no | - | PASS | CAM_FORCEANGLE | CAM_FORCEANGLE | CONFIRMED | beta CAM_FORCEANGLE@0x00a37200(B) + size-sane sz1(D) |
| 73 | 2 | 0x013a14c8 | - | - | no | - | PASS | UNKNOWN_73 | UNKNOWN_73 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=MESSAGE_QUICKCHAT_PRIVATE. |
| 74 | 4 | 0x015c0fa8 | JCOINS_UPDATE | 0x00a36dc0 | no | - | PASS | JCOINS_UPDATE | JCOINS_UPDATE | CONFIRMED | beta JCOINS_UPDATE@0x00a36dc0(B) + size-sane sz4(D) |
| 75 | 0 | 0x013a1488 | - | - | YES | OK | PASS | UNKNOWN_75 | UNKNOWN_75 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UNKNOWN_75. capture label 'SetReadyFlag' is stale-947-codec, not a verbatim beta enum (size0 matches A). |
| 76 | varShort(-2) | 0x015c2028 | UPDATE_ZONE_PARTIAL_ENCLOSED | 0x00a37d00 | no | - | PASS | UPDATE_ZONE_PARTIAL_ENCLOSED | UPDATE_ZONE_PARTIAL_ENCLOSED | CONFIRMED | beta UPDATE_ZONE_PARTIAL_ENCLOSED@0x00a37d00(B) + size-sane sz-2(D) |
| 77 | varShort(-2) | 0x013a1448 | CAMERA_UPDATE | 0x00a36e00 | no | - | PASS | CAMERA_UPDATE | CAMERA_UPDATE | CONFIRMED | beta CAMERA_UPDATE@0x00a36e00(B) + size-sane sz-2(D) |
| 78 | 3 | 0x015c2068 | UPDATE_ZONE_FULL_FOLLOWS | 0x00a37d40 | no | - | PASS | UPDATE_ZONE_FULL_FOLLOWS | UPDATE_ZONE_FULL_FOLLOWS | CONFIRMED | beta UPDATE_ZONE_FULL_FOLLOWS@0x00a37d40(B) + size-sane sz3(D) |
| 79 | varByte(-1) | 0x013a1408 | NPC_HEADICON_SPECIFIC | 0x00a37480 | no | - | PASS | NPC_HEADICON_SPECIFIC | NPC_HEADICON_SPECIFIC | CONFIRMED | beta NPC_HEADICON_SPECIFIC@0x00a37480(B) + size-sane sz-1(D) |
| 80 | 1 | 0x013a13c8 | UPDATE_RUNENERGY | 0x00a38240 | YES | OK | PASS | UPDATE_RUNENERGY | UPDATE_RUNENERGY | CONFIRMED | beta UPDATE_RUNENERGY@0x00a38240(B) + size-sane sz1(D) + capture UpdateRunenergy sz1(C) + 948 handler decompiled |
| 81 | varShort(-2) | 0x013a1388 | - | - | no | - | PASS | UNKNOWN_81 | UNKNOWN_81 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_81. |
| 82 | 23 | 0x015c0be8 | IF_SETPOSITION | 0x00a36280 | YES | OK | PASS | IF_SETPOSITION | IF_SETPOSITION | CONFIRMED | beta IF_SETPOSITION@0x00a36280(B) + size-sane sz23(D) + capture IfSetPosition sz23(C) |
| 83 | varShort(-2) | 0x013a1348 | - | - | no | - | PASS | UNKNOWN_83 | UNKNOWN_83 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=CLANCHANNEL_DELTA. |
| 84 | 10 | 0x015c08a8 | IF_SETOBJECT | 0x00a36840 | no | - | PASS | IF_SETOBJECT | IF_SETOBJECT | CONFIRMED | beta IF_SETOBJECT@0x00a36840(B) + size-sane sz10(D) |
| 85 | varShort(-2) | 0x013a1308 | UPDATE_INV_FULL | 0x00a36040 | no | - | PASS | UPDATE_INV_FULL | UPDATE_INV_FULL | CONFIRMED | beta UPDATE_INV_FULL@0x00a36040(B) + size-sane sz-2(D) |
| 86 | 10 | 0x015c06e8 | IF_SETANIM | 0x00a36940 | no | - | PASS | IF_SETANIM | IF_SETANIM | CONFIRMED | beta IF_SETANIM@0x00a36940(B) + size-sane sz10(D) |
| 87 | 0 | 0x013a12c8 | CAM_RESET | 0x00a37800 | no | - | PASS | CAM_RESET | CAM_RESET | CONFIRMED | beta CAM_RESET@0x00a37800(B) + size-sane sz0(D) |
| 88 | 4 | 0x013a1288 | - | - | no | - | PASS | UNKNOWN_88 | UNKNOWN_88 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_88. |
| 89 | 19 | 0x013a1248 | - | - | no | - | PASS | UNKNOWN_89 | UNKNOWN_89 | UNKNOWN | no verbatim beta enum maps; size19(A) authoritative. 947=CAM_RESET. |
| 90 | varByte(-1) | 0x015c1fe8 | - | - | no | - | PASS | UNKNOWN_90 | UNKNOWN_90 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=REBUILD_NORMAL. |
| 91 | 5 | 0x015c0968 | IF_SETHIDE | 0x00a36100 | no | - | PASS | IF_SETHIDE | IF_SETHIDE | CONFIRMED | beta IF_SETHIDE@0x00a36100(B) + size-sane sz5(D) |
| 92 | varByte(-1) | 0x015c1468 | - | - | no | - | PASS | UNKNOWN_92 | UNKNOWN_92 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_92. |
| 93 | varByte(-1) | 0x013a1208 | MESSAGE_GAME | 0x00a365c0 | no | - | PASS | MESSAGE_GAME | MESSAGE_GAME | CONFIRMED | beta MESSAGE_GAME@0x00a365c0(B) + size-sane sz-1(D) |
| 94 | 8 | 0x015c0ca8 | IF_OPENSUB | 0x00a37100 | no | - | PASS | IF_OPENSUB | IF_OPENSUB | CONFIRMED | beta IF_OPENSUB@0x00a37100(B) + size-sane sz8(D) |
| 95 | 5 | 0x013a11c8 | MIDI_SONG | 0x00a36b00 | no | - | PASS | MIDI_SONG | MIDI_SONG | CONFIRMED | beta MIDI_SONG@0x00a36b00(B) + size-sane sz5(D) |
| 96 | 4 | 0x015c0728 | - | - | no | - | PASS | UNKNOWN_96 | UNKNOWN_96 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_96. |
| 97 | 10 | 0x015c09e8 | IF_SETEVENTS | 0x00a35e80 | no | - | PASS | IF_SETEVENTS | IF_SETEVENTS | CONFIRMED | beta IF_SETEVENTS@0x00a35e80(B) + size-sane sz10(D) + 948 handler decompiled |
| 98 | 25 | 0x013a1188 | - | - | no | - | PASS | UNKNOWN_98 | UNKNOWN_98 | UNKNOWN | no verbatim beta enum maps; size25(A) authoritative. 947=UNKNOWN_98. |
| 99 | 6 | 0x015c0568 | IF_SETRECOL | 0x00a376c0 | no | - | PASS | IF_SETRECOL | IF_SETRECOL | CONFIRMED | beta IF_SETRECOL@0x00a376c0(B) + size-sane sz6(D) |
| 100 | 4 | 0x013a1148 | - | - | no | - | PASS | UNKNOWN_100 | UNKNOWN_100 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_100. |
| 101 | 4 | 0x015c0868 | - | - | no | - | PASS | UNKNOWN_101 | UNKNOWN_101 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=MESSAGE_TYPE6. |
| 102 | 8 | 0x015c07e8 | IF_SETMODEL | 0x00a36540 | no | - | PASS | IF_SETMODEL | IF_SETMODEL | CONFIRMED | beta IF_SETMODEL@0x00a36540(B) + size-sane sz8(D) |
| 103 | 8 | 0x015c07a8 | IF_SETGRAPHIC | 0x00a35b80 | no | - | PASS | IF_SETGRAPHIC | IF_SETGRAPHIC | CONFIRMED | beta IF_SETGRAPHIC@0x00a35b80(B) + size-sane sz8(D) |
| 104 | 14 | 0x013a1108 | - | - | no | - | PASS | UNKNOWN_104 | UNKNOWN_104 | UNKNOWN | no verbatim beta enum maps; size14(A) authoritative. 947=CLANSETTINGS_FULL. |
| 105 | varByte(-1) | 0x013a10c8 | MESSAGE_CLANCHANNEL | 0x00a361c0 | no | - | PASS | MESSAGE_CLANCHANNEL | MESSAGE_CLANCHANNEL | CONFIRMED | beta MESSAGE_CLANCHANNEL@0x00a361c0(B) + size-sane sz-1(D) |
| 106 | 1 | 0x013a1088 | - | - | no | - | PASS | UNKNOWN_106 | UNKNOWN_106 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_106. |
| 107 | 3 | 0x015c1be8 | OBJ_DEL | 0x00a36b40 | no | - | PASS | OBJ_DEL | OBJ_DEL | CONFIRMED | beta OBJ_DEL@0x00a36b40(B) + size-sane sz3(D) |
| 108 | varShort(-2) | 0x013a1048 | CLANSETTINGS_DELTA | 0x00a38640 | no | - | PASS | CLANSETTINGS_DELTA | CLANSETTINGS_DELTA | CONFIRMED | beta CLANSETTINGS_DELTA@0x00a38640(B) + size-sane sz-2(D) |
| 109 | 28 | 0x015c0e68 | - | - | no | - | PASS | UNKNOWN_109 | UNKNOWN_109 | UNKNOWN | no verbatim beta enum maps; size28(A) authoritative. 947=PLAYER_GROUP_FULL. |
| 110 | varShort(-2) | 0x013a1008 | RUNCLIENTSCRIPT | 0x00a37ec0 | YES | OK | PASS | RUNCLIENTSCRIPT | RUNCLIENTSCRIPT | CONFIRMED | beta RUNCLIENTSCRIPT@0x00a37ec0(B) + size-sane sz-2(D) + capture RunClientScript sz15(C) + 948 handler decompiled |
| 111 | 6 | 0x013a0fc8 | - | - | no | - | PASS | UNKNOWN_111 | UNKNOWN_111 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=VARP_LARGE. |
| 112 | 2 | 0x013a0f88 | - | - | no | - | PASS | UNKNOWN_112 | UNKNOWN_112 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=CLIENT_SETVARC_LARGE. |
| 113 | 11 | 0x015c1ce8 | - | - | no | - | PASS | UNKNOWN_113 | UNKNOWN_113 | UNKNOWN | no verbatim beta enum maps; size11(A) authoritative. 947=CAM_TARGET. |
| 114 | varByte(-1) | 0x013a0f48 | - | - | no | - | PASS | UNKNOWN_114 | UNKNOWN_114 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_114. |
| 115 | 10 | 0x015c06a8 | - | - | no | - | PASS | UNKNOWN_115 | UNKNOWN_115 | UNKNOWN | no verbatim beta enum maps; size10(A) authoritative. 947=CLIENT_SETVARCBIT_SMALL. |
| 116 | varShort(-2) | 0x015c1428 | - | - | no | - | PASS | UNKNOWN_116 | UNKNOWN_116 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=SET_PLAYER_OP_3. |
| 117 | varByte(-1) | 0x013a0f08 | CLANCHANNEL_DELTA | 0x00a36c80 | no | - | PASS | CLANCHANNEL_DELTA | CLANCHANNEL_DELTA | CONFIRMED | beta CLANCHANNEL_DELTA@0x00a36c80(B) + size-sane sz-1(D) |
| 118 | 29 | 0x015c0ae8 | - | - | no | - | PASS | UNKNOWN_118 | UNKNOWN_118 | UNKNOWN | no verbatim beta enum maps; size29(A) authoritative. 947=UNKNOWN_118. |
| 119 | 35 | 0x013a0ec8 | - | - | no | - | PASS | UNKNOWN_119 | UNKNOWN_119 | UNKNOWN | no verbatim beta enum maps; size35(A) authoritative. 947=UNKNOWN_119. |
| 120 | 0 | 0x013a0e88 | CAM_SMOOTHRESET | 0x00a37cc0 | no | - | PASS | CAM_SMOOTHRESET | CAM_SMOOTHRESET | CONFIRMED | beta CAM_SMOOTHRESET@0x00a37cc0(B) + size-sane sz0(D) |
| 121 | varShort(-2) | 0x013a0e48 | UPDATE_INV_PARTIAL | 0x00a37c00 | no | - | PASS | UPDATE_INV_PARTIAL | UPDATE_INV_PARTIAL | CONFIRMED | beta UPDATE_INV_PARTIAL@0x00a37c00(B) + size-sane sz-2(D) |
| 122 | varShort(-2) | 0x015c09a8 | IF_SETTEXT | 0x00a37f40 | no | - | PASS | IF_SETTEXT | IF_SETTEXT | CONFIRMED | beta IF_SETTEXT@0x00a37f40(B) + size-sane sz-2(D) |
| 123 | 0 | 0x015c0428 | - | - | no | - | PASS | UNKNOWN_123 | UNKNOWN_123 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UNKNOWN_123. |
| 124 | varShort(-2) | 0x013a0e08 | - | - | no | - | PASS | UNKNOWN_124 | UNKNOWN_124 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_124. |
| 125 | 7 | 0x015c1b68 | OBJ_COUNT | 0x00a36880 | no | - | PASS | OBJ_COUNT | OBJ_COUNT | CONFIRMED | beta OBJ_COUNT@0x00a36880(B) + size-sane sz7(D) |
| 126 | varByte(-1) | 0x013a0dc8 | - | - | no | - | PASS | UNKNOWN_126 | UNKNOWN_126 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UPDATE_ZONE_PARTIAL_ENCLOSED. |
| 127 | 0 | 0x013a0d88 | - | - | no | - | PASS | UNKNOWN_127 | UNKNOWN_127 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UNKNOWN_127. |
| 128 | 0 | 0x013a0d48 | - | - | YES | OK | PASS | UNKNOWN_128 | UNKNOWN_128 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UNKNOWN_128. capture label 'UNKNOWN_128' is stale-947-codec, not a verbatim beta enum (size0 matches A). |
| 129 | 3 | 0x013a0d08 | - | - | no | - | PASS | UNKNOWN_129 | UNKNOWN_129 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=MESSAGE_PRIVATE_ECHO. |
| 130 | varByte(-1) | 0x013a0cc8 | - | - | no | - | PASS | UNKNOWN_130 | UNKNOWN_130 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_130. |
| 131 | 3 | 0x015c1028 | - | - | no | - | PASS | UNKNOWN_131 | UNKNOWN_131 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_131. |
| 132 | 2 | 0x013a0c88 | - | - | no | - | PASS | UNKNOWN_132 | UNKNOWN_132 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=UPDATE_REBOOT_TIMER. |
| 133 | 1 | 0x013a0c48 | - | - | no | - | PASS | UNKNOWN_133 | UNKNOWN_133 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_133. |
| 134 | varShort(-2) | 0x013a0c08 | - | - | no | - | PASS | UNKNOWN_134 | UNKNOWN_134 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_134. |
| 135 | varShort(-2) | 0x013a0bc8 | - | - | no | - | PASS | UNKNOWN_135 | UNKNOWN_135 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_135. |
| 136 | 5 | 0x015c0668 | - | - | no | - | PASS | UNKNOWN_136 | UNKNOWN_136 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=UNKNOWN_136. |
| 137 | 1 | 0x013a0b88 | - | - | no | - | PASS | UNKNOWN_137 | UNKNOWN_137 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_137. |
| 138 | 2 | 0x013a0b48 | - | - | no | - | PASS | UNKNOWN_138 | UNKNOWN_138 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=NPC_UPDATE_FLAG. |
| 139 | 0 | 0x013a0b08 | LOGOUT_TRANSFER | 0x00a37580 | no | - | PASS | LOGOUT_TRANSFER | LOGOUT_TRANSFER | CONFIRMED | beta LOGOUT_TRANSFER@0x00a37580(B) + size-sane sz0(D) |
| 140 | 17 | 0x013a0ac8 | - | - | no | - | PASS | UNKNOWN_140 | UNKNOWN_140 | UNKNOWN | no verbatim beta enum maps; size17(A) authoritative. 947=UNKNOWN_140. |
| 141 | varShort(-2) | 0x013a0a88 | - | - | no | - | PASS | UNKNOWN_141 | UNKNOWN_141 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_141. |
| 142 | varByte(-1) | 0x013a0a48 | - | - | no | - | PASS | UNKNOWN_142 | UNKNOWN_142 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_142. |
| 143 | 6 | 0x015c11a8 | - | - | no | - | PASS | UNKNOWN_143 | UNKNOWN_143 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=UNKNOWN_143. |
| 144 | 2 | 0x015c1168 | - | - | no | - | PASS | UNKNOWN_144 | UNKNOWN_144 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=SET_INTERACTION_FLAG_D. |
| 145 | 2 | 0x013a0a08 | - | - | no | - | PASS | UNKNOWN_145 | UNKNOWN_145 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=UNKNOWN_145. |
| 146 | 2 | 0x015c1068 | - | - | no | - | PASS | UNKNOWN_146 | UNKNOWN_146 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=IF_SET_HTTP_IMAGE. |
| 147 | 10 | 0x015c1668 | - | - | YES | OK | PASS | VARP_LONG | UNKNOWN_147 | UNKNOWN | VETO: handler decompile-certain (player-varp 8B @0x141510) but NO verbatim beta enum (beta var set=SMALL/LARGE only). size10(A) authoritative. capture VarpLong sz10 corroborates size only. |
| 148 | 2 | 0x015c03a8 | - | - | no | - | PASS | UNKNOWN_148 | UNKNOWN_148 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=UNKNOWN_148. |
| 149 | 6 | 0x015c10a8 | - | - | no | - | PASS | UNKNOWN_149 | UNKNOWN_149 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=NEW_PACKET_149. |
| 150 | 3 | 0x013a09c8 | - | - | no | - | PASS | UNKNOWN_150 | UNKNOWN_150 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_150. |
| 151 | 21 | 0x015c1d68 | PROJANIM_SPECIFIC | 0x00a36500 | no | - | PASS | PROJANIM_SPECIFIC | PROJANIM_SPECIFIC | CONFIRMED | beta PROJANIM_SPECIFIC@0x00a36500(B) + size-sane sz21(D) |
| 152 | varByte(-1) | 0x013a0988 | IF_SET_HTTP_IMAGE | 0x00a36380 | no | - | PASS | IF_SET_HTTP_IMAGE | IF_SET_HTTP_IMAGE | CONFIRMED | beta IF_SET_HTTP_IMAGE@0x00a36380(B) + size-sane sz-1(D) |
| 153 | 4 | 0x013a0948 | - | - | no | - | PASS | UNKNOWN_153 | UNKNOWN_153 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_153. |
| 154 | 5 | 0x013a0908 | - | - | no | - | PASS | UNKNOWN_154 | UNKNOWN_154 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=UNKNOWN_154. |
| 155 | 0 | 0x013a08c8 | LOGOUT | 0x00a358c0 | no | - | PASS | LOGOUT | LOGOUT | CONFIRMED | beta LOGOUT@0x00a358c0(B) + size-sane sz0(D) |
| 156 | 1 | 0x013a0888 | - | - | no | - | PASS | UNKNOWN_156 | UNKNOWN_156 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_156. |
| 157 | 1 | 0x013a0848 | - | - | no | - | PASS | UNKNOWN_157 | UNKNOWN_157 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_157. |
| 158 | 9 | 0x015c04e8 | - | - | no | - | PASS | UNKNOWN_158 | UNKNOWN_158 | UNKNOWN | no verbatim beta enum maps; size9(A) authoritative. 947=UNKNOWN_158. |
| 159 | varShort(-2) | 0x013a0808 | - | - | no | - | PASS | UNKNOWN_159 | UNKNOWN_159 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=WORLDLIST_FETCH_REPLY. |
| 160 | 9 | 0x013a07c8 | - | - | no | - | PASS | UNKNOWN_160 | UNKNOWN_160 | UNKNOWN | no verbatim beta enum maps; size9(A) authoritative. 947=UNKNOWN_160. |
| 161 | varShort(-2) | 0x013a0788 | PLAYER_GROUP_DELTA | 0x00a37640 | no | - | PASS | PLAYER_GROUP_DELTA | PLAYER_GROUP_DELTA | CONFIRMED | beta PLAYER_GROUP_DELTA@0x00a37640(B) + size-sane sz-2(D) |
| 162 | 0 | 0x013a0748 | TRIGGER_ONDIALOGABORT | 0x00a36ac0 | no | - | PASS | TRIGGER_ONDIALOGABORT | TRIGGER_ONDIALOGABORT | CONFIRMED | beta TRIGGER_ONDIALOGABORT@0x00a36ac0(B) + size-sane sz0(D) |
| 163 | 15 | 0x013a0708 | - | - | no | - | PASS | UNKNOWN_163 | UNKNOWN_163 | UNKNOWN | no verbatim beta enum maps; size15(A) authoritative. 947=UNKNOWN_163. |
| 164 | 28 | 0x015c18a8 | - | - | no | - | PASS | UNKNOWN_164 | UNKNOWN_164 | UNKNOWN | no verbatim beta enum maps; size28(A) authoritative. 947=UNKNOWN_164. |
| 165 | 14 | 0x015c05e8 | - | - | no | - | PASS | UNKNOWN_165 | UNKNOWN_165 | UNKNOWN | no verbatim beta enum maps; size14(A) authoritative. 947=UNKNOWN_165. |
| 166 | 5 | 0x015c1228 | - | - | no | - | PASS | UNKNOWN_166 | UNKNOWN_166 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=SKIP_DATA. |
| 167 | 12 | 0x013a06c8 | SYNTH_SOUND | 0x00a35bc0 | no | - | PASS | SYNTH_SOUND | SYNTH_SOUND | CONFIRMED | beta SYNTH_SOUND@0x00a35bc0(B) + size-sane sz12(D) |
| 168 | varByte(-1) | 0x015c1928 | SOUND_AREA | 0x00a36140 | no | - | PASS | SOUND_AREA | SOUND_AREA | CONFIRMED | beta SOUND_AREA@0x00a36140(B) + size-sane sz-1(D) |
| 169 | 8 | 0x013a0688 | SERVER_TICK_END | 0x00a35800 | no | - | PASS | SERVER_TICK_END | SERVER_TICK_END | CONFIRMED | beta SERVER_TICK_END@0x00a35800(B) + size-sane sz8(D) |
| 170 | 5 | 0x015c19a8 | - | - | no | - | PASS | UNKNOWN_170 | UNKNOWN_170 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=VARP_LONG. |
| 171 | 3 | 0x015c0fe8 | - | - | no | - | PASS | UNKNOWN_171 | UNKNOWN_171 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=SERVER_TICK_END. |
| 172 | 1 | 0x015c0de8 | - | - | no | - | PASS | UNKNOWN_172 | UNKNOWN_172 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=REBUILD_REGION. |
| 173 | varShort(-2) | 0x013a0648 | - | - | no | - | PASS | UNKNOWN_173 | UNKNOWN_173 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_173. |
| 174 | 8 | 0x013a0608 | - | - | no | - | PASS | UNKNOWN_174 | UNKNOWN_174 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=SET_INTERACTION_FLAG_C. |
| 175 | varByte(-1) | 0x013a05c8 | MESSAGE_PRIVATE_ECHO | 0x00a37a80 | no | - | PASS | MESSAGE_PRIVATE_ECHO | MESSAGE_PRIVATE_ECHO | CONFIRMED | beta MESSAGE_PRIVATE_ECHO@0x00a37a80(B) + size-sane sz-1(D) |
| 176 | 3 | 0x015c0e28 | - | - | no | - | PASS | UNKNOWN_176 | UNKNOWN_176 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_176. |
| 177 | 29 | 0x015c1828 | - | - | no | - | PASS | UNKNOWN_177 | UNKNOWN_177 | UNKNOWN | no verbatim beta enum maps; size29(A) authoritative. 947=UPDATE_INV_GROUP. |
| 178 | varShort(-2) | 0x015c0d68 | - | - | no | - | PASS | UNKNOWN_178 | UNKNOWN_178 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UPDATE_PLAYER_CHAT. |
| 179 | 9 | 0x015c0528 | IF_SETSCROLLPOS | 0x00a38740 | no | - | PASS | IF_SETSCROLLPOS | IF_SETSCROLLPOS | CONFIRMED | beta IF_SETSCROLLPOS@0x00a38740(B) + size-sane sz9(D) |
| 180 | 5 | 0x015c0828 | - | - | no | - | PASS | UNKNOWN_180 | UNKNOWN_180 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=PLAYER_INFO_DECODE_2. |
| 181 | 4 | 0x013a0588 | - | - | no | - | PASS | UNKNOWN_181 | UNKNOWN_181 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_181. |
| 182 | 3 | 0x013a0548 | - | - | no | - | PASS | UNKNOWN_182 | UNKNOWN_182 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_182. |
| 183 | 14 | 0x015c17a8 | - | - | no | - | PASS | UNKNOWN_183 | UNKNOWN_183 | UNKNOWN | no verbatim beta enum maps; size14(A) authoritative. 947=UNKNOWN_183. |
| 184 | 4 | 0x015c0ce8 | UPDATE_REBOOT_TIMER | 0x00a378c0 | no | - | PASS | UPDATE_REBOOT_TIMER | UPDATE_REBOOT_TIMER | CONFIRMED | beta UPDATE_REBOOT_TIMER@0x00a378c0(B) + size-sane sz4(D) |
| 185 | varByte(-1) | 0x013a0508 | MESSAGE_PRIVATE | 0x00a37e40 | no | - | PASS | MESSAGE_PRIVATE | MESSAGE_PRIVATE | CONFIRMED | beta MESSAGE_PRIVATE@0x00a37e40(B) + size-sane sz-1(D) |
| 186 | varShort(-2) | 0x015c1268 | - | - | no | - | PASS | UNKNOWN_186 | UNKNOWN_186 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=IF_OPENSUB_THUNK. |
| 187 | 1 | 0x013a04c8 | - | - | no | - | PASS | UNKNOWN_187 | UNKNOWN_187 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=SET_WORLD_TARGET. |
| 188 | varShort(-2) | 0x013a0488 | - | - | no | - | PASS | UNKNOWN_188 | UNKNOWN_188 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=REBUILD_WORLDENTITY. |
| 189 | 4 | 0x013a0448 | - | - | no | - | PASS | UNKNOWN_189 | UNKNOWN_189 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=IF_MOVESUB. |
| 190 | 0 | 0x015c13e8 | - | - | no | - | PASS | UNKNOWN_190 | UNKNOWN_190 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UNKNOWN_190. |
| 191 | 4 | 0x013a0408 | - | - | no | - | PASS | UNKNOWN_191 | UNKNOWN_191 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=UNKNOWN_191. |
| 192 | 1 | 0x013a03c8 | - | - | no | - | PASS | UNKNOWN_192 | UNKNOWN_192 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_192. |
| 193 | 1 | 0x015c0d28 | - | - | no | - | PASS | UNKNOWN_193 | UNKNOWN_193 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_193. |
| 194 | 1 | 0x015c11e8 | - | - | no | - | PASS | UNKNOWN_194 | UNKNOWN_194 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_194. |
| 195 | 4 | 0x013a0388 | - | - | no | - | PASS | UNKNOWN_195 | UNKNOWN_195 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=TRIGGER_ONDIALOGABORT. |
| 196 | 10 | 0x015c1528 | - | - | no | - | PASS | CLIENT_SETVARC_LONG | UNKNOWN_196 | UNKNOWN | VETO: handler decompile-certain (client-varc 8B @0x119350) but NO verbatim beta enum (beta var set=SMALL/LARGE only). size10(A) authoritative. |
| 197 | varShort(-2) | 0x013a0348 | - | - | no | - | PASS | UNKNOWN_197 | UNKNOWN_197 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_197. |
| 198 | varByte(-1) | 0x013a0308 | - | - | no | - | PASS | UNKNOWN_198 | UNKNOWN_198 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=ANTI_CHEAT_CHALLENGE. |
| 199 | varShort(-2) | 0x015c12a8 | REBUILD_NORMAL | 0x00a37dc0 | no | - | PASS | REBUILD_NORMAL | REBUILD_NORMAL | CONFIRMED | beta REBUILD_NORMAL@0x00a37dc0(B) + size-sane sz-2(D) |
| 200 | 2 | 0x013a02c8 | - | - | no | - | PASS | UNKNOWN_200 | UNKNOWN_200 | UNKNOWN | no verbatim beta enum maps; size2(A) authoritative. 947=SET_URL_STRING. |
| 201 | 3 | 0x015c1128 | - | - | no | - | PASS | UNKNOWN_201 | UNKNOWN_201 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_201. |
| 202 | varShort(-2) | 0x013a0288 | - | - | no | - | PASS | UNKNOWN_202 | UNKNOWN_202 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_202. |
| 203 | 3 | 0x013a0248 | - | - | no | - | PASS | UNKNOWN_203 | UNKNOWN_203 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=UNKNOWN_203. |
| 204 | 1 | 0x015c0da8 | - | - | no | - | PASS | UNKNOWN_204 | UNKNOWN_204 | UNKNOWN | no verbatim beta enum maps; size1(A) authoritative. 947=UNKNOWN_204. |
| 205 | 6 | 0x013a0208 | - | - | no | - | PASS | UNKNOWN_205 | UNKNOWN_205 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=NPC_INFO_THUNK. |
| 206 | 5 | 0x015c04a8 | - | - | no | - | PASS | UNKNOWN_206 | UNKNOWN_206 | UNKNOWN | no verbatim beta enum maps; size5(A) authoritative. 947=UNKNOWN_206. |
| 207 | 3 | 0x015c10e8 | - | - | no | - | PASS | UNKNOWN_207 | UNKNOWN_207 | UNKNOWN | no verbatim beta enum maps; size3(A) authoritative. 947=PLAYER_GROUP_DELTA. |
| 208 | varShort(-2) | 0x013a01c8 | - | - | no | - | PASS | UNKNOWN_208 | UNKNOWN_208 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=UNKNOWN_208. |
| 209 | varShort(-2) | 0x013a0188 | - | - | no | - | PASS | UNKNOWN_209 | UNKNOWN_209 | UNKNOWN | no verbatim beta enum maps; size-2(A) authoritative. 947=LOGOUT_TRANSFER. |
| 210 | 6 | 0x013a0148 | - | - | no | - | PASS | UNKNOWN_210 | UNKNOWN_210 | UNKNOWN | no verbatim beta enum maps; size6(A) authoritative. 947=UNKNOWN_210. |
| 211 | 33 | 0x013a0108 | - | - | no | - | PASS | UNKNOWN_211 | UNKNOWN_211 | UNKNOWN | no verbatim beta enum maps; size33(A) authoritative. 947=UPDATE_IGNORELIST. |
| 212 | varByte(-1) | 0x013a00c8 | - | - | no | - | PASS | UNKNOWN_212 | UNKNOWN_212 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_212. |
| 213 | varByte(-1) | 0x013a0088 | - | - | no | - | PASS | UNKNOWN_213 | UNKNOWN_213 | UNKNOWN | no verbatim beta enum maps; size-1(A) authoritative. 947=UNKNOWN_213. |
| 214 | 0 | 0x013a0048 | - | - | no | - | PASS | UNKNOWN_214 | UNKNOWN_214 | UNKNOWN | no verbatim beta enum maps; size0(A) authoritative. 947=UPDATE_URL_STRING. |
| 215 | 8 | 0x013a0008 | - | - | no | - | PASS | UNKNOWN_215 | UNKNOWN_215 | UNKNOWN | no verbatim beta enum maps; size8(A) authoritative. 947=UNKNOWN_215. |
| 216 | varShort(-2) | 0x0139ffc8 | WORLDLIST_FETCH_REPLY | 0x00a37f80 | YES | OK | PASS | WORLDLIST_FETCH_REPLY | WORLDLIST_FETCH_REPLY | CONFIRMED | beta WORLDLIST_FETCH_REPLY@0x00a37f80(B) + size-sane sz-2(D) + capture WorldListPacket sz3001(C) + 948 handler decompiled |
| 217 | 4 | 0x0139ff88 | - | - | no | - | PASS | UNKNOWN_217 | UNKNOWN_217 | UNKNOWN | no verbatim beta enum maps; size4(A) authoritative. 947=SET_CHAT_FILTER_A. |

## Capture-exercised 17 (highest-confidence subset)

| opcode | FINAL_NAME | CONFIDENCE |
|---|---|---|
| 3 | UNKNOWN_3 | UNKNOWN |
| 5 | RESET_CLIENT_VARCACHE | CONFIRMED |
| 26 | UPDATE_SITESETTINGS | CONFIRMED |
| 28 | VARP_LARGE | CONFIRMED |
| 35 | UNKNOWN_35 | UNKNOWN |
| 44 | UPDATE_STAT | CONFIRMED |
| 47 | CLIENT_SETVARC_SMALL | CONFIRMED |
| 49 | CHANGE_LOBBY | CONFIRMED |
| 61 | VARP_SMALL | CONFIRMED |
| 64 | CLIENT_SETVARC_LARGE | CONFIRMED |
| 75 | UNKNOWN_75 | UNKNOWN |
| 80 | UPDATE_RUNENERGY | CONFIRMED |
| 82 | IF_SETPOSITION | CONFIRMED |
| 110 | RUNCLIENTSCRIPT | CONFIRMED |
| 128 | UNKNOWN_128 | UNKNOWN |
| 147 | UNKNOWN_147 | UNKNOWN |
| 216 | WORLDLIST_FETCH_REPLY | CONFIRMED |

12 of the 17 capture opcodes are CONFIRMED. The 5 UNKNOWN (op3,35,75,128 + previously-op147) have authoritative sizes but no verbatim beta enum (their capture labels are stale-947-codec names: IF_SETTOPLEVELINTERFACE, IF_SETEVENTS2, SET_READY_FLAG, keepalive, VARP_LONG — none exist in the beta enum vocabulary).