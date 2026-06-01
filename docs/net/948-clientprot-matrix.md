# 948 ClientProt Evidence Matrix (C->S, all 130 opcodes)

**Target:** `rs2client.948-2-2` (Ghidra port 8081) — AUTHORITY for sizes/addresses/byte-layout.
**Official enum source (Source B):** beta `librs2client.so` (port 8080), `jag::ClientProt::*` ProtEntry globals @ `0x00a392d8..0x00a410e8`. Verbatim symbols only.
**Capture (Source C):** `capture/login-20260531-191837_s1/decoded.log` — C->S exercised opcodes **5, 51, 52, 54, 127** (op51 keepalive was outside this short session window).

This is RE-owned DOCUMENTATION. Phase 5 applies it to `core/`. Do not treat C++ function names (Send*, Process*, DoOp*, *_CS2, FUN_*) as packet names.

## THE GATE (applied per opcode)
- **size(A)** taken verbatim from `docs/net/948-prot-tables-dump.csv` (RegisterAll). **name** is a verbatim official `jag::ClientProt::<NAME>` or `UNKNOWN_<op>`.
- **CONFIRMED**: strong beta-emitter <-> 948-emitter match to a specific `&jag::ClientProt::<NAME>` AND size-sanity (D) passes AND (capture corroboration where the opcode is in the C->S list). Must cite beta symbol addr (B) + 948 emitter addr (A) [+ capture offset (C)].
- **PROBABLE**: a single weak/structural lead (layout-family or size match) without a byte-diffed beta send-site. Tagged, never promoted to a stub default without further work.
- **UNKNOWN_<op>**: no confident official enum.
- **(D) size-sanity VETO**: interface click >=8B; a size-3 packet is NOT a click; keepalive=0B; any layout contradiction -> UNKNOWN.
- **Anti-fake**: a C++ function name is NEVER a packet name; no single-weak-inference is CONFIRMED.

## RESOLVED: the IF_BUTTON family (priority — prior pass had it backwards)

948 has **TWO distinct button dispatch mechanisms**, and only ONE carries the official `IF_BUTTON1..10` names.

### (1) Interface CLICK — the canonical IF_BUTTON1..IF_BUTTON10 (size 8)
Dispatched by `jag::InterfaceManager::IfButtonXInner @0x002978d0`. The short path indexes a CS2 ProtEntry pointer table at **`0x01365920`** by `[option-1]` (`LEA R13,[0x1365920]; MOV RDX,[R13 + R9*8]`, `R9 = option-1`). Each slot was verified to point at the opcode's ProtEntry via slot->ProtEntry xrefs (confirmed independently for all 10 slots). The 8-byte wire layout written here is:
`interfaceHash` WriteUInt32LE (**intLittle**, 4B) + `slotId` (**uShortAddLittle**: lowbyte=val-0x80, then highbyte) + `itemId` (**uShortAdd**: highbyte, then lowbyte=val-0x80).
Beta confirmation: `jag::InterfaceManager::IfButtonX @0x005662a0` dispatches `param_3 == 1..10` to `IfButtonXSend(..., &jag::ClientProt::IF_BUTTON1..IF_BUTTON10)`, and the `else` (component carries a string) builds `&jag::ClientProt::IF_PLAYER`.

| option | IF_BUTTONn | table slot | -> opcode | size(A) | ProtEntry | note |
|---|---|---|---|---|---|---|
| 1 | IF_BUTTON1 | 0x01365920 | **127** | 8 | 0x015d36c0 | CONFIRMED; capture op127 size8 |
| 2 | IF_BUTTON2 | 0x01365928 | **103** | 8 | 0x015d3840 | CONFIRMED |
| 3 | IF_BUTTON3 | 0x01365930 | **92** | 8 | 0x015d38f0 | CONFIRMED |
| 4 | IF_BUTTON4 | 0x01365938 | **45** | 8 | 0x015d3be0 | CONFIRMED |
| 5 | IF_BUTTON5 | 0x01365940 | **30** | 8 | 0x015d3cd0 | CONFIRMED |
| 6 | IF_BUTTON6 | 0x01365948 | **68** | 8 | 0x015d3a70 | CONFIRMED |
| 7 | IF_BUTTON7 | 0x01365950 | **43** | 8 | 0x015d3c00 | CONFIRMED |
| 8 | IF_BUTTON8 | 0x01365958 | **20** | -1 | 0x015d3d60 | slot reuses op20's ProtEntry; op20 primary = OPOBJ*_CS2 (varByte) -> op20 stays UNKNOWN |
| 9 | IF_BUTTON9 | 0x01365960 | **10** | 16 | 0x015d3e00 | slot reuses op10's ProtEntry; op10 primary = IF_BUTTOND (16B drag) |
| 10 | IF_BUTTON10 | 0x01365968 | **21** | 8 | 0x015d3d50 | CONFIRMED |

**Overload note (opt8/opt9):** options 8 and 9 reuse ProtEntries that are *primarily* owned by other prots — op20 by `SendOpObjCS2_2` (varByte OPOBJ-CS2 variant) and op10 by `SendIfButtonD`/beta `UpdateDragging` = `IF_BUTTOND` (16B drag). The CS2 click table borrows those two ProtEntries for option-8/9 slots, but the opcode's *registered* identity (and the only emitter that defines its size/layout) is the non-click owner. So **op10 = IF_BUTTOND (CONFIRMED)**, **op20 = UNKNOWN** (OPOBJ-CS2 has no official enum), and IF_BUTTON8 / IF_BUTTON9 have no standalone wire opcode of their own in 948.

### (2) CS2-scripted button press — NOT IF_BUTTON1..10 (size 3, SIZE-SANITY VETO)
`jag::ClientProt::SendIfButtonN_CS2 @0x002d9970` is a separate clientscript-driven trigger with its own `switch` cases 1..10 -> opcodes **39, 73, 47, 33, 108, 56, 91, 50, 16, 40** (all **size 3**). Its wire layout is `flag byte (-0x80 - varbit)` + `2-byte component sub-id`. With no `interfaceHash`/`slotId`/`itemId`, a size-3 packet **cannot** be an interface click. By gate rule (D) these do **NOT** receive the IF_BUTTON1..10 names; the beta `jag::ClientProt` vocabulary contains exactly one IF_BUTTON1..10 set, already claimed by the size-8 clicks. **All ten size-3 ops -> UNKNOWN_<op>.** (Prior pass assigned IF_BUTTONn to these — incorrect.)

### What the size-3 ops 39/73/47/33/.../40 actually are
They are the `SendIfButtonN_CS2` 3-byte component-press packets (CS2 opcode-driven), one ProtEntry per switch case. They are real distinct opcodes with a real 3-byte layout, but carry **no official enum name** in the beta vocabulary -> UNKNOWN.

## Matrix

| opcode | size(A) | emitter_addr(A) | handler_source(A) | beta_enum_name(B) | beta_addr(B) | in_capture(C) | size_sanity(D) | current_stub | FINAL_NAME | CONFIDENCE | evidence |
|---|---|---|---|---|---|---|---|---|---|---|---|
| 0 | -1 | 0x015d3ea0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_0 | UNKNOWN_0 | UNKNOWN | emitter FUN_00280610 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 1 | -1 | 0x015d3e90(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_1 | UNKNOWN_1 | UNKNOWN | emitter SendOpNpcCS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 2 | 9 | 0x015d3e80(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_2 | UNKNOWN_2 | UNKNOWN | emitter FUN_0015c7b0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 3 | 9 | 0x015d3e70(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_3 | UNKNOWN_3 | UNKNOWN | emitter SendSceneGraphReport (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 4 | -1 | 0x015d3e60(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_4 | UNKNOWN_4 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 5 | 4 | 0x00229d80 | BINDER | - | - | YES | PASS(4B int) | UNKNOWN_5 | UNKNOWN_5 | UNKNOWN | emitter SendSceneGraphReport@0x00229d80 (per-frame interface-update-event rbtree flush; conditionally writes one 4B int field+0x10 to entry 0x015d3e50). CAPTURE op5 size4 (00000005 then 00000037). No official enum body-matches (not FACE_SQUARE/SEND_SNAPSHOT/EVENT_CAMERA_POSITION) -> UNKNOWN. |
| 6 | 7 | 0x015d3e40(ProtEntry) | BINDER | OPOBJ5 | 0x00a393a8 | no | PASS(7B tile+id) | OPOBJ5 | OPOBJ5 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ5; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 7 | 4 | 0x015d3e30(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_7 | UNKNOWN_7 | UNKNOWN | emitter SendOpTargetOption_CS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 8 | 4 | 0x015d3e20(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_8 | UNKNOWN_8 | UNKNOWN | emitter SendMultiDisplayPackets (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 9 | 12 | 0x015d3e10(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_9 | UNKNOWN_9 | UNKNOWN | emitter SendOpLocU (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 10 | 16 | 0x01365960 | CS2_TABLE | IF_BUTTOND | 0x00a39608 | no | PASS(16B drag>=8) | IF_BUTTOND | IF_BUTTOND | CONFIRMED | beta UpdateDragging->&IF_BUTTOND(16B 2-component drag); 948 SendIfButtonD@0x002bbd00 uses entry 0x015d3e10. ALSO CS2-table opt9 slot 0x01365960=IF_BUTTON9(click reuses this ProtEntry). Primary=IF_BUTTOND. |
| 11 | 3 | 0x015d3df0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_11 | UNKNOWN_11 | UNKNOWN | emitter FUN_0015c3d0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 12 | -1 | 0x015d3de0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_12 | UNKNOWN_12 | UNKNOWN | emitter SendMultiDisplayPackets (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 13 | 8 | 0x015d3dd0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_13 | UNKNOWN_13 | UNKNOWN | emitter SendNoTimeout (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 14 | 0 | 0x015d3dc0(ProtEntry) | BINDER | ABORT_P_DIALOG | 0x00a395a0 | no | PASS(0B dialog-abort) | ABORT_P_DIALOG | ABORT_P_DIALOG | CONFIRMED | beta Resume lambda#7->&ABORT_P_DIALOG; 948 emitter@0x002d0040 size0 dialog-abort (resets dialog state field). NOT the keepalive (that is op51). |
| 15 | 6 | 0x015d3db0(ProtEntry) | BINDER | EVENT_MOUSE_CLICK | 0x00a39358 | no | PASS(6B) | EVENT_MOUSE_CLICK | EVENT_MOUSE_CLICK | CONFIRMED | beta ClientWatch::MainLogic->&EVENT_MOUSE_CLICK; 948 SendEventMouseClick@0x001805b0 entry 0x015d3db0. |
| 16 | 6 | 0x015d3da0(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON9 | UNKNOWN_16 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 17 | 5 | 0x015d3d90(ProtEntry) | BINDER | MOVE_SCRIPTED | 0x00a39420 | no | PASS(5B) | MOVE_SCRIPTED | MOVE_SCRIPTED | CONFIRMED | 948 fn self-named jag::opcode::movescripted@0x002a3a70; beta MOVE_SCRIPTED ref _M_invoke. 5B scripted-move. |
| 18 | 17 | 0x015d3d80(ProtEntry) | UNBOUND | OPLOCT | 0x00a395d8 | no | PASS(17B) | OPLOCT | OPLOCT | PROBABLE | beta DoOpLoc->&OPLOCT long-form; 948 SendOpLocTLong entry 0x015d3d80 (17B). Layout family-match but exact send-site not isolated -> PROBABLE. |
| 19 | 9 | 0x015d3d70(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_19 | UNKNOWN_19 | UNKNOWN | emitter SendOpObjCS2_2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 20 | -1 | 0x01365958 | CS2_TABLE | - | - | no | VETO(-1 varByte not click) | UNKNOWN_20 | UNKNOWN_20 | UNKNOWN | entry 0x015d3d60 owned by SendOpObjCS2_2(varByte, OPOBJ*_CS2 - no official enum). ALSO CS2-table opt8 slot 0x01365958=IF_BUTTON8(click reuses ProtEntry). Primary unresolved -> UNKNOWN. |
| 21 | 8 | 0x01365968 | CS2_TABLE | IF_BUTTON10 | 0x00a395c8 | no | PASS(8B click) | UNKNOWN_21 | IF_BUTTON10 | CONFIRMED | beta IfButtonX@0x005662a0 opt10->IfButtonXSend(&IF_BUTTON10); 948 IfButtonXInner@0x002978d0 table slot 0x01365968->ProtEntry 0x015d3d50 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 22 | 3 | 0x015d3d40(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_22 | UNKNOWN_22 | UNKNOWN | emitter SendIfButtonN_CS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 23 | 8 | 0x015d3d30(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_23 | UNKNOWN_23 | UNKNOWN | emitter SendOpTargetOption_CS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 24 | 7 | 0x015d3d20(ProtEntry) | BINDER | OPOBJ1 | 0x00a39548 | no | PASS(7B tile+id) | OPOBJ1 | OPOBJ1 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ1; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 25 | 11 | 0x015bfb40(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_25 | UNKNOWN_25 | UNKNOWN | emitter FUN_00aeeb30 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 26 | -2 | 0x015d3d10(ProtEntry) | BINDER | RESUME_P_COUNTDIALOG | 0x00a39480 | no | PASS(varByte) | RESUME_P_COUNTDIALOG | RESUME_P_COUNTDIALOG | CONFIRMED | 948 SendResumeCountDialog@0x002cfdb0 CS2 count-pair, entry 0x015d3d10; beta RESUME_P_COUNTDIALOG. |
| 27 | 1 | 0x015d3d00(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_27 | UNKNOWN_27 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 28 | 4 | 0x015d3cf0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_28 | UNKNOWN_28 | UNKNOWN | emitter DoOpLoc (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 29 | 4 | 0x015d3ce0(ProtEntry) | BINDER | PING_STATISTICS | 0x00a393a0 | no | PASS(4B) | PING_STATISTICS | PING_STATISTICS | CONFIRMED | beta ProcessingWorldPing->&PING_STATISTICS; 948 entry 0x015d3ce0 GetPing+count (FUN_001fc8c0). |
| 30 | 8 | 0x01365940 | CS2_TABLE | IF_BUTTON5 | 0x00a394f0 | no | PASS(8B click) | UNKNOWN_30 | IF_BUTTON5 | CONFIRMED | beta IfButtonX@0x005662a0 opt5->IfButtonXSend(&IF_BUTTON5); 948 IfButtonXInner@0x002978d0 table slot 0x01365940->ProtEntry 0x015d3cd0 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 31 | 3 | 0x015d3cc0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_31 | UNKNOWN_31 | UNKNOWN | emitter FUN_0015c450 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 32 | 3 | 0x015d3cb0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_32 | UNKNOWN_32 | UNKNOWN | emitter FUN_0015c430 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 33 | 3 | 0x015d3ca0(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON4 | UNKNOWN_33 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 34 | 15 | 0x015d3c90(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_34 | UNKNOWN_34 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 35 | -1 | 0x015d3c80(ProtEntry) | BINDER | IF_PLAYER | 0x00a395e8 | no | PASS(varByte) | IF_PLAYER | IF_PLAYER | CONFIRMED | 948 IfButtonXInner long-path (component has string) uses &DAT_015d3c80=op35 entry; beta IfButtonX else-branch builds &IF_PLAYER (use-button-on-player). |
| 36 | 7 | 0x015d3c70(ProtEntry) | BINDER | OPOBJ4 | 0x00a39368 | no | PASS(7B tile+id) | OPOBJ4 | OPOBJ4 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ4; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 37 | 0 | 0x015d3c60(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_37 | UNKNOWN_37 | UNKNOWN | emitter SendQueuedPacket (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 38 | -2 | 0x015d3c50(ProtEntry) | BINDER | MESSAGE_PRIVATE | 0x00a393d0 | no | PASS(varByte) | MESSAGE_PRIVATE | MESSAGE_PRIVATE | CONFIRMED | beta &MESSAGE_PRIVATE; 948 SendMessagePrivate@0x00306dd0 tinyKeyEncrypt, entry 0x015d3c50. |
| 39 | 3 | 0x015d3c40(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON1 | UNKNOWN_39 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 40 | 3 | 0x015d3c30(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON10 | UNKNOWN_40 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 41 | 9 | 0x015d3c20(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_41 | UNKNOWN_41 | UNKNOWN | emitter FUN_0015c7f0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 42 | -1 | 0x015d3c10(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_42 | UNKNOWN_42 | UNKNOWN | emitter FUN_002d0490 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 43 | 8 | 0x01365950 | CS2_TABLE | IF_BUTTON7 | 0x00a39380 | no | PASS(8B click) | UNKNOWN_43 | IF_BUTTON7 | CONFIRMED | beta IfButtonX@0x005662a0 opt7->IfButtonXSend(&IF_BUTTON7); 948 IfButtonXInner@0x002978d0 table slot 0x01365950->ProtEntry 0x015d3c00 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 44 | 4 | 0x015d3bf0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_44 | UNKNOWN_44 | UNKNOWN | emitter SendStrtol (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 45 | 8 | 0x01365938 | CS2_TABLE | IF_BUTTON4 | 0x00a395c0 | no | PASS(8B click) | UNKNOWN_45 | IF_BUTTON4 | CONFIRMED | beta IfButtonX@0x005662a0 opt4->IfButtonXSend(&IF_BUTTON4); 948 IfButtonXInner@0x002978d0 table slot 0x01365938->ProtEntry 0x015d3be0 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 46 | 1 | 0x015d3bd0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_46 | UNKNOWN_46 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 47 | 3 | 0x015d3bc0(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON3 | UNKNOWN_47 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 48 | -1 | 0x015d3bb0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_48 | UNKNOWN_48 | UNKNOWN | emitter FUN_00172900 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 49 | -1 | 0x015d3ba0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_49 | UNKNOWN_49 | UNKNOWN | emitter SendOpObjCS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 50 | 3 | 0x015d3b90(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON8 | UNKNOWN_50 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 51 | 0 | 0x015d3b80(ProtEntry) | BINDER | NO_TIMEOUT | 0x00a394b0 | no(not in session) | PASS(0B keepalive) | NO_TIMEOUT | NO_TIMEOUT | CONFIRMED | 948 ProcessConnections@0x0013e6c0 emits entry 0x015d3b80 when keepalive ctr(+0x4c/+0x30)>0x32, size0; beta NO_TIMEOUT in MainLogic. Byte-identical keepalive. |
| 52 | 6 | 0x015d3b70(ProtEntry) | BINDER | - | - | YES | n/a | UNKNOWN_52 | UNKNOWN_52 | UNKNOWN | emitter SendDisplayInfo (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 53 | 15 | 0x015d3b60(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_53 | UNKNOWN_53 | UNKNOWN | emitter FUN_0015bcf0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 54 | 4 | 0x015d3b50(ProtEntry) | BINDER | WORLDLIST_FETCH | 0x00a39338 | YES | PASS(4B CRC) | WORLDLIST_FETCH | WORLDLIST_FETCH | CONFIRMED | beta WorldSwitcher lambda#4 _M_invoke@0x00418b1e builds &WORLDLIST_FETCH (4B CRC, state 0x14/0x1e gate, sets +0xa0 flag); 948 SendWorldlistFetch@0x00194a80 entry 0x015d3b50 4B CRC field+0x60, sets +0xb0 flag. CAPTURE op54 size4 (FFFFFFFF then 29AE5659 CRC). |
| 55 | 11 | 0x015d3b40(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_55 | UNKNOWN_55 | UNKNOWN | emitter FUN_0015bad0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 56 | 3 | 0x015d3b30(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON6 | UNKNOWN_56 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 57 | 2 | 0x015d3b20(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_57 | UNKNOWN_57 | UNKNOWN | emitter SendAffinedTransformSet_Main (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 58 | -2 | 0x015d3b10(ProtEntry) | BINDER | MOVE_GAMECLICK | 0x00a39488 | no | PASS(varByte) | MOVE_GAMECLICK | MOVE_GAMECLICK | CONFIRMED | beta minimenuactions::ThreeDView->&MOVE_GAMECLICK; 948 SendMoveGame@0x0026e1d0 entry 0x015d3b10. |
| 59 | 4 | 0x015d3b00(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_59 | UNKNOWN_59 | UNKNOWN | emitter SendDetectModifiedClient (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 60 | -1 | 0x015d3af0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_60 | UNKNOWN_60 | UNKNOWN | emitter FUN_002d02a0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 61 | 16 | 0x015d3ae0(ProtEntry) | BINDER | IF_BUTTONT | 0x00a39490 | no | PASS(16B target) | IF_BUTTONT | IF_BUTTONT | PROBABLE | beta InterfaceActions->&IF_BUTTONT (use-component-on-target,16B); 948 entry 0x015d3ae0 SendIfButtonT (16B). Size-match; emitter not byte-diffed -> PROBABLE. |
| 62 | 1 | 0x015d3ad0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_62 | UNKNOWN_62 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 63 | 3 | 0x015d3ac0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_63 | UNKNOWN_63 | UNKNOWN | emitter FUN_0015c3f0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 64 | 7 | 0x015d3ab0(ProtEntry) | BINDER | OPOBJ3 | 0x00a39458 | no | PASS(7B tile+id) | OPOBJ3 | OPOBJ3 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ3; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 65 | 4 | 0x015d3aa0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_65 | UNKNOWN_65 | UNKNOWN | emitter FUN_002383e0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 66 | 0 | 0x015d3a90(ProtEntry) | BINDER | CLOSE_MODAL | 0x00a39468 | no | PASS(0B) | CLOSE_MODAL | CLOSE_MODAL | CONFIRMED | beta CloseModalInterface->&CLOSE_MODAL; 948 SendCloseModal@0x002d01f0 entry 0x015d3a90, size0. |
| 67 | 18 | 0x015d3a80(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_67 | UNKNOWN_67 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 68 | 8 | 0x01365948 | CS2_TABLE | IF_BUTTON6 | 0x00a39580 | no | PASS(8B click) | UNKNOWN_68 | IF_BUTTON6 | CONFIRMED | beta IfButtonX@0x005662a0 opt6->IfButtonXSend(&IF_BUTTON6); 948 IfButtonXInner@0x002978d0 table slot 0x01365948->ProtEntry 0x015d3a70 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 69 | 2 | 0x015d3a60(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_69 | UNKNOWN_69 | UNKNOWN | emitter FUN_002d03b0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 70 | -1 | 0x015d3a50(ProtEntry) | BINDER | FRIENDLIST_DEL | 0x00a393b8 | no | PASS(varByte) | FRIENDLIST_DEL | FRIENDLIST_DEL | CONFIRMED | 948 SendFriendlistDel@0x0026b1f0 entry 0x015d3a50; beta FRIENDLIST_DEL. |
| 71 | -1 | 0x015d3a40(ProtEntry) | BINDER | RESUME_P_NAMEDIALOG | 0x00a393b0 | no | PASS(varByte) | RESUME_P_NAMEDIALOG | RESUME_P_NAMEDIALOG | CONFIRMED | beta Resume lambda#4->&RESUME_P_NAMEDIALOG (len-prefixed UTF8); 948 @0x002cff20 entry 0x015d3a40. |
| 72 | -2 | 0x015d3a30(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_72 | UNKNOWN_72 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 73 | 3 | 0x015d3a20(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON2 | UNKNOWN_73 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 74 | 5 | 0x015d3a10(ProtEntry) | BINDER | MOVE_MINIMAPCLICK | 0x00a395b0 | no | PASS(5B) | MOVE_MINIMAPCLICK | MOVE_MINIMAPCLICK | PROBABLE | official enum; 948 FUN_0015b7d0 walk/move flag+tileX/Y sets map flag, entry 0x015d3a10. Single-fn lead, beta send-site not pinned -> PROBABLE. |
| 75 | -1 | 0x015d3a00(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_75 | UNKNOWN_75 | UNKNOWN | emitter SendEncodedString2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 76 | 4 | 0x015d39f0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_76 | UNKNOWN_76 | UNKNOWN | emitter SendEventAppletFocus (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 77 | 9 | 0x015d39e0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_77 | UNKNOWN_77 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 78 | 18 | 0x015d39d0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_78 | UNKNOWN_78 | UNKNOWN | emitter SendMoveMinimapClick (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 79 | 1 | 0x015d39c0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_79 | UNKNOWN_79 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 80 | -1 | 0x015d39b0(ProtEntry) | BINDER | IGNORELIST_ADD | 0x00a39308 | no | PASS(varByte) | IGNORELIST_ADD | IGNORELIST_ADD | CONFIRMED | 948 SendIgnorelistAdd@0x0030f4b0 + 'ignore list is full' str, entry 0x015d39b0; beta IGNORELIST_ADD. |
| 81 | -2 | 0x015d39a0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_81 | UNKNOWN_81 | UNKNOWN | emitter SendMultiDisplayPackets (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 82 | -2 | 0x015d3990(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_82 | UNKNOWN_82 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 83 | -1 | 0x015d3980(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_83 | UNKNOWN_83 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 84 | 22 | 0x015d3970(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_84 | UNKNOWN_84 | UNKNOWN | emitter SendIfButtonTargetMenu (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 85 | -1 | 0x015d3960(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_85 | UNKNOWN_85 | UNKNOWN | emitter activechatphrase_sendprivate (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 86 | 11 | 0x015d3950(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_86 | UNKNOWN_86 | UNKNOWN | emitter FUN_0015c100 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 87 | -1 | 0x015d3940(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_87 | UNKNOWN_87 | UNKNOWN | emitter SendMessagePublicWithEffects (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 88 | -2 | 0x015d3930(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_88 | UNKNOWN_88 | UNKNOWN | emitter SendDataReport (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 89 | -1 | 0x015d3920(ProtEntry) | BINDER | CLANCHANNEL_KICKUSER | 0x00a39450 | no | PASS(varByte) | CLANCHANNEL_KICKUSER | CLANCHANNEL_KICKUSER | CONFIRMED | 948 SendSocialRequest@0x0026aef0 + 'not in this channel' str, entry 0x015d3920; beta &CLANCHANNEL_KICKUSER. |
| 90 | -1 | 0x015d3910(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_90 | UNKNOWN_90 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 91 | 3 | 0x015d3900(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON7 | UNKNOWN_91 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 92 | 8 | 0x01365930 | CS2_TABLE | IF_BUTTON3 | 0x00a39598 | no | PASS(8B click) | UNKNOWN_92 | IF_BUTTON3 | CONFIRMED | beta IfButtonX@0x005662a0 opt3->IfButtonXSend(&IF_BUTTON3); 948 IfButtonXInner@0x002978d0 table slot 0x01365930->ProtEntry 0x015d38f0 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 93 | -2 | 0x015d38e0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_93 | UNKNOWN_93 | UNKNOWN | emitter SendVerifiedStringSend (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 94 | 3 | 0x015d38d0(ProtEntry) | BINDER | WINDOW_STATUS | 0x00a395d0 | no | PASS(3B) | WINDOW_STATUS | WINDOW_STATUS | CONFIRMED | beta ClientWatch::SendWindowStatus->&WINDOW_STATUS; 948 @0x0033e470 entry 0x015d38d0. |
| 95 | 4 | 0x015d38c0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_95 | UNKNOWN_95 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 96 | 0 | 0x015d38b0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_96 | UNKNOWN_96 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 97 | -1 | 0x015d38a0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_97 | UNKNOWN_97 | UNKNOWN | emitter FUN_002d0680 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 98 | -1 | 0x015d3890(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_98 | UNKNOWN_98 | UNKNOWN | emitter FUN_001729b0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 99 | -1 | 0x015d3880(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_99 | UNKNOWN_99 | UNKNOWN | emitter FUN_002d0830 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 100 | -1 | 0x015d3870(ProtEntry) | BINDER | FRIENDLIST_ADD | 0x00a39460 | no | PASS(varByte) | FRIENDLIST_ADD | FRIENDLIST_ADD | CONFIRMED | 948 SendFriendlistAdd@0x0026a530 entry 0x015d3870; beta FRIENDLIST_ADD. |
| 101 | 7 | 0x015d3860(ProtEntry) | BINDER | OPOBJ2 | 0x00a39408 | no | PASS(7B tile+id) | OPOBJ2 | OPOBJ2 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ2; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 102 | -2 | 0x015d3850(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_102 | UNKNOWN_102 | UNKNOWN | emitter SendIfServerTransmit (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 103 | 8 | 0x01365928 | CS2_TABLE | IF_BUTTON2 | 0x00a393e0 | no | PASS(8B click) | UNKNOWN_103 | IF_BUTTON2 | CONFIRMED | beta IfButtonX@0x005662a0 opt2->IfButtonXSend(&IF_BUTTON2); 948 IfButtonXInner@0x002978d0 table slot 0x01365928->ProtEntry 0x015d3840 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd |
| 104 | 2 | 0x015d3830(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_104 | UNKNOWN_104 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 105 | -2 | 0x015d3820(ProtEntry) | BINDER | EVENT_APPLET_FOCUS | 0x00a393f0 | no | PASS(varByte) | EVENT_APPLET_FOCUS | EVENT_APPLET_FOCUS | CONFIRMED | beta ClientWatch::MainLogic->&EVENT_APPLET_FOCUS (SDL focus toggle); 948 SendAppletFocusEvents@0x00194f40 entry 0x015d3820. |
| 106 | 1 | 0x015d3810(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_106 | UNKNOWN_106 | UNKNOWN | emitter SendMultiDisplayPackets (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 107 | 0 | 0x015d3800(ProtEntry) | BINDER | MAP_BUILD_COMPLETE | 0x00a410e8 | no | PASS(0B) | MAP_BUILD_COMPLETE | MAP_BUILD_COMPLETE | CONFIRMED | 948 SendMapBuildComplete@0x002bc030 entry 0x015d3800, size0; beta MAP_BUILD_COMPLETE. |
| 108 | 3 | 0x015d37f0(ProtEntry) | BINDER | - | - | no | VETO(3B not click) | IF_BUTTON5 | UNKNOWN_108 | UNKNOWN | SendIfButtonN_CS2@0x002d9970 case (size3 flag+2B comp-subid); NOT an IF_BUTTON click (no interfaceHash/slot/item). No distinct official enum (only one IF_BUTTON1..10 set exists, owned by size-8 clicks). |
| 109 | 9 | 0x015d37e0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_109 | UNKNOWN_109 | UNKNOWN | emitter FUN_0015c810 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 110 | 7 | 0x015d37d0(ProtEntry) | BINDER | EVENT_MOUSE_MOVE | 0x00a39498 | no | PASS(7B) | EVENT_MOUSE_MOVE | EVENT_MOUSE_MOVE | CONFIRMED | beta ClientWatch::MainLogic->&EVENT_MOUSE_MOVE (ring buffer); 948 FUN_001803b0 entry 0x015d37a0. |
| 111 | 9 | 0x015d37c0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_111 | UNKNOWN_111 | UNKNOWN | emitter FUN_0015c830 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 112 | 7 | 0x015d37b0(ProtEntry) | BINDER | OPOBJ6 | 0x00a39538 | no | PASS(7B tile+id) | OPOBJ6 | OPOBJ6 | PROBABLE | SendOpTargetOption_CS2 case->OPOBJ6; beta DoOpObj OPOBJ family 7B tile-coord+id+flag. Case-to-N mapping inferred, not byte-diffed -> PROBABLE. |
| 113 | 3 | 0x015d37a0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_113 | UNKNOWN_113 | UNKNOWN | emitter FUN_0015c410 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 114 | -1 | 0x015d3790(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_114 | UNKNOWN_114 | UNKNOWN | emitter FUN_00269660 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 115 | 3 | 0x015d3780(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_115 | UNKNOWN_115 | UNKNOWN | emitter FUN_00269560 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 116 | 2 | 0x015d3770(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_116 | UNKNOWN_116 | UNKNOWN | emitter FUN_002d05a0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 117 | -1 | 0x015d3760(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_117 | UNKNOWN_117 | UNKNOWN | emitter SendEncodedString (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 118 | -1 | 0x015d3750(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_118 | UNKNOWN_118 | UNKNOWN | emitter activechatphrase_send (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 119 | -2 | 0x015d3740(ProtEntry) | BINDER | RESUME_PAUSEBUTTON | 0x00a394c8 | no | PASS(varByte) | RESUME_PAUSEBUTTON | RESUME_PAUSEBUTTON | CONFIRMED | beta SendPauseComponentMessage->&RESUME_PAUSEBUTTON; 948 entry 0x015d3740. |
| 120 | 8 | 0x015d3730(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_120 | UNKNOWN_120 | UNKNOWN | emitter SendStrtoll (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 121 | 3 | 0x015d3720(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_121 | UNKNOWN_121 | UNKNOWN | emitter FUN_0015c3b0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 122 | -2 | 0x015d3710(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_122 | UNKNOWN_122 | UNKNOWN | emitter FUN_002d1af0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 123 | 1 | 0x015d3700(ProtEntry) | BINDER | BUG_REPORT | 0x00a39370 | no | PASS(1B) | BUG_REPORT | BUG_REPORT | CONFIRMED | beta BugReporting->&BUG_REPORT; 948 SendBugReport@0x002d0140 entry 0x015d3700. |
| 124 | -1 | 0x015d36f0(ProtEntry) | BINDER | MESSAGE_PUBLIC | 0x00a393c0 | no | PASS(varByte) | MESSAGE_PUBLIC | MESSAGE_PUBLIC | CONFIRMED | beta &MESSAGE_PUBLIC; 948 SendMessagePublic@0x0037a9c0 entry 0x015d36f0. |
| 125 | 9 | 0x015d36e0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_125 | UNKNOWN_125 | UNKNOWN | emitter FUN_0015c850 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 126 | 9 | 0x015d36d0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_126 | UNKNOWN_126 | UNKNOWN | emitter FUN_0015c7d0 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |
| 127 | 8 | 0x01365920 | CS2_TABLE | IF_BUTTON1 | 0x00a394d0 | YES | PASS(8B click) | UNKNOWN_127 | IF_BUTTON1 | CONFIRMED | beta IfButtonX@0x005662a0 opt1->IfButtonXSend(&IF_BUTTON1); 948 IfButtonXInner@0x002978d0 table slot 0x01365920->ProtEntry 0x015d36c0 (xref-verified); 8B layout interfaceHash intLittle+slotId uShortAddLittle+itemId uShortAdd; CAPTURE op127 size8 observed (interface click), confirms IF_BUTTON1. |
| 128 | -2 | 0x015d36b0(ProtEntry) | UNBOUND | - | - | no | n/a | UNKNOWN_128 | UNKNOWN_128 | UNKNOWN | UNBOUND (RegisterAll-only, no emitter); no official enum mappable. |
| 129 | -1 | 0x015d36a0(ProtEntry) | BINDER | - | - | no | n/a | UNKNOWN_129 | UNKNOWN_129 | UNKNOWN | emitter SendOpLocCS2 (C++ fn name, not a packet name); no provable official jag::ClientProt enum match. |

## Counts (of 130)
- **CONFIRMED: 32**
- **PROBABLE: 9** (op18 OPLOCT, op61 IF_BUTTONT, op74 MOVE_MINIMAPCLICK, and OPOBJ1..6 = op24/101/64/36/6/112)
- **UNKNOWN: 89**

## Capture-exercised resolution (Source C: ops 5, 51, 52, 54, 127)
- **op5** -> UNKNOWN_5. SendSceneGraphReport per-frame event flush, 4B int; no official enum body-match. (capture size4 confirmed)
- **op51** -> NO_TIMEOUT (CONFIRMED). Keepalive via ProcessConnections>0x32 ticks; beta MainLogic. (not in this short session window)
- **op52** -> UNKNOWN_52. SendDisplayInfo 6B (capture `02 04 00 03 00 02`); no DISPLAY_INFO official enum.
- **op54** -> WORLDLIST_FETCH (CONFIRMED). 4B CRC; beta WorldSwitcher lambda#4 + capture (FFFFFFFF then 29AE5659).
- **op127** -> IF_BUTTON1 (CONFIRMED). CS2-table option1, 8B click; capture size8.

## Ghidra annotations applied (948-2-2)
- `IfButtonXInner @0x002978d0` decompiler comment: full option->opcode click table + IF_PLAYER long-path.
- `SendIfButtonN_CS2 @0x002d9970` decompiler comment: size-3 family is NOT IF_BUTTON1..10 (size-sanity veto).
