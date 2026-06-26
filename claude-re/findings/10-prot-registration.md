# Opcode registration / packet-handler functions (binary, headless RE)

## Candidate functions (12)

- `BindHandlers` @ 0007509a
- `BindHandlers` @ 000aa85e
- `BindHandlers_extra` @ 000aaf4c
- `BindHandlers` @ 000ab3ea
- `BindHandlers` @ 000ab888
- `BindHandlers` @ 000ae1a8
- `RegisterAll` @ 000c45b0
- `RegisterAll` @ 000c4700
- `ClientProt` @ 000e5a70
- `RegisterAll` @ 000e6170
- `CreateOpcodeError` @ 00b5e100
- `ReadOpcodeArray` @ 00c30fa0

## DECOMP `BindHandlers` @ 0007509a
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void jag::ServerProt::BindHandlers(undefined8 *param_1,undefined8 param_2)

{
  undefined8 uVar1;
  code *pcVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  int iVar9;
  bool bVar10;
  undefined8 local_38;
  undefined8 uStack_30;
  code *local_28;
  undefined8 local_20;
  
  param_1[1] = param_2;
  *param_1 = &PTR_FUN_01363858;
  FUN_00074ec0();
  FUN_00074ec0(param_1 + 4);
  pcVar2 = DAT_013a15c0;
  *(undefined4 *)(param_1 + 6) = 0;
  local_38._0_4_ = (undefined4)param_2;
  uVar3 = (undefined4)local_38;
  local_38._4_4_ = (undefined4)((ulong)param_2 >> 0x20);
  uVar5 = local_38._4_4_;
  uVar4 = (undefined4)uStack_30;
  uVar6 = uStack_30._4_4_;
  bVar10 = DAT_013a15c0 != (code *)0x0;
  param_1[7] = 0;
  *(undefined1 *)(param_1 + 8) = 0;
  *(undefined1 *)((long)param_1 + 0x41) = 0;
  *(undefined4 *)((long)param_1 + 0x44) = 0;
  *(undefined1 *)(param_1 + 9) = 0;
  *(undefined1 *)((long)param_1 + 0x49) = 0;
  *(undefined4 *)((long)param_1 + 0x4c) = 0;
  *(undefined4 *)(param_1 + 10) = 0;
  local_38 = CONCAT44(uRam00000000013a15b4,_DAT_013a15b0);
  uStack_30._0_4_ = uRam00000000013a15b8;
  uStack_30._4_4_ = uRam00000000013a15bc;
  local_28 = DAT_013a15c0;
  DAT_013a15c0 = FUN_000eb7c0;
  _DAT_013a15b0 = uVar3;
  uRam00000000013a15b4 = uVar5;
  uRam00000000013a15b8 = uVar4;
  uRam00000000013a15bc = uVar6;
  local_20 = DAT_013a15c8;
  DAT_013a15c8 = FUN_0014ed40;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1180;
  bVar10 = DAT_013a1180 != (code *)0x0;
  local_28 = DAT_013a1180;
  DAT_013a1180 = FUN_000eb790;
  local_20 = DAT_013a1188;
  DAT_013a1188 = packethandlers::ZoneUpdates::MAP_PROJANIM_FULL;
  local_38 = CONCAT44(uRam00000000013a1174,_DAT_013a1170);
  uStack_30._0_4_ = uRam00000000013a1178;
  uStack_30._4_4_ = uRam00000000013a117c;
  _DAT_013a1170 = uVar3;
  uRam00000000013a1174 = uVar5;
  uRam00000000013a1178 = uVar4;
  uRam00000000013a117c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a17c0;
  bVar10 = DAT_013a17c0 != (code *)0x0;
  local_28 = DAT_013a17c0;
  DAT_013a17c0 = FUN_000eb760;
  local_20 = DAT_013a17c8;
  DAT_013a17c8 = packethandlers::ZoneUpdates::SPOTANIM_ENTITY;
  local_38 = CONCAT44(uRam00000000013a17b4,_DAT_013a17b0);
  uStack_30._0_4_ = uRam00000000013a17b8;
  uStack_30._4_4_ = uRam00000000013a17bc;
  _DAT_013a17b0 = uVar3;
  uRam00000000013a17b4 = uVar5;
  uRam00000000013a17b8 = uVar4;
  uRam00000000013a17bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0ac0;
  bVar10 = DAT_013a0ac0 != (code *)0x0;
  local_28 = DAT_013a0ac0;
  DAT_013a0ac0 = FUN_000eb730;
  local_20 = DAT_013a0ac8;
  DAT_013a0ac8 = packethandlers::ZoneUpdates::PLAYER_SPOTANIM;
  local_38 = CONCAT44(uRam00000000013a0ab4,_DAT_013a0ab0);
  uStack_30 = CONCAT44(uRam00000000013a0abc,uRam00000000013a0ab8);
  _DAT_013a0ab0 = uVar3;
  uRam00000000013a0ab4 = uVar5;
  uRam00000000013a0ab8 = uVar4;
  uRam00000000013a0abc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1240;
  bVar10 = DAT_013a1240 != (code *)0x0;
  local_28 = DAT_013a1240;
  DAT_013a1240 = FUN_000eb700;
  local_20 = DAT_013a1248;
  DAT_013a1248 = packethandlers::NPCInfo::NPC_SPOTANIM;
  local_38 = _DAT_013a1230;
  uStack_30 = uRam00000000013a1238;
  uRam00000000013a1238 = uVar1;
  _DAT_013a1230 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1c40;
  bVar10 = DAT_013a1c40 != (code *)0x0;
  local_28 = DAT_013a1c40;
  DAT_013a1c40 = FUN_000eb6c0;
  local_20 = DAT_013a1c48;
  DAT_013a1c48 = packethandlers::Misc::RESET_ENTITY_LISTS;
  local_38 = _DAT_013a1c30;
  uStack_30 = uRam00000000013a1c38;
  uRam00000000013a1c38 = uVar1;
  _DAT_013a1c30 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0740;
  bVar10 = DAT_013a0740 != (code *)0x0;
  local_28 = DAT_013a0740;
  DAT_013a0740 = FUN_000eb690;
  local_20 = DAT_013a0748;
  DAT_013a0748 = packethandlers::ClientState::TRIGGER_ONDIALOGABORT;
  local_38 = _DAT_013a0730;
  uStack_30 = uRam00000000013a0738;
  uRam00000000013a0738 = uVar1;
  _DAT_013a0730 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0100;
  bVar10 = DAT_013a0100 != (code *)0x0;
  local_28 = DAT_013a0100;
  DAT_013a0100 = FUN_001768a0;
  local_20 = DAT_013a0108;
  DAT_013a0108 = packethandlers::ZoneUpdates::MAP_PROJANIM_FULL_2;
  local_38 = _DAT_013a00f0;
  uStack_30 = uRam00000000013a00f8;
  uRam00000000013a00f8 = uVar1;
  _DAT_013a00f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0700;
  uVar4 = (undefined4)uStack_30;
  uVar6 = uStack_30._4_4_;
  bVar10 = DAT_013a0700 != (code *)0x0;
  local_28 = DAT_013a0700;
  DAT_013a0700 = FUN_00176870;
  local_20 = DAT_013a0708;
  DAT_013a0708 = packethandlers::ZoneUpdates::SPOTANIM_ENTITY_2;
  local_38 = CONCAT44(uRam00000000013a06f4,_DAT_013a06f0);
  uStack_30._0_4_ = uRam00000000013a06f8;
  uStack_30._4_4_ = uRam00000000013a06fc;
  _DAT_013a06f0 = uVar3;
  uRam00000000013a06f4 = uVar5;
  uRam00000000013a06f8 = uVar4;
  uRam00000000013a06fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1800;
  bVar10 = DAT_013a1800 != (code *)0x0;
  local_28 = DAT_013a1800;
  DAT_013a1800 = FUN_00176840;
  local_20 = DAT_013a1808;
  DAT_013a1808 = packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC_PACKED;
  local_38 = CONCAT44(uRam00000000013a17f4,_DAT_013a17f0);
  uStack_30._0_4_ = uRam00000000013a17f8;
  uStack_30._4_4_ = uRam00000000013a17fc;
  _DAT_013a17f0 = uVar3;
  uRam00000000013a17f4 = uVar5;
  uRam00000000013a17f8 = uVar4;
  uRam00000000013a17fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a19c0;
  bVar10 = DAT_013a19c0 != (code *)0x0;
  local_28 = DAT_013a19c0;
  DAT_013a19c0 = FUN_00176750;
  local_20 = DAT_013a19c8;
  DAT_013a19c8 = packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC;
  local_38 = CONCAT44(uRam00000000013a19b4,_DAT_013a19b0);
  uStack_30._0_4_ = uRam00000000013a19b8;
  uStack_30._4_4_ = uRam00000000013a19bc;
  _DAT_013a19b0 = uVar3;
  uRam00000000013a19b4 = uVar5;
  uRam00000000013a19b8 = uVar4;
  uRam00000000013a19bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a18c0;
  bVar10 = DAT_013a18c0 != (code *)0x0;
  local_28 = DAT_013a18c0;
  DAT_013a18c0 = FUN_00176720;
  local_20 = DAT_013a18c8;
  DAT_013a18c8 = packethandlers::Audio::SOUND_AREA_SYNTH;
  local_38 = CONCAT44(uRam00000000013a18b4,_DAT_013a18b0);
  uStack_30 = CONCAT44(uRam00000000013a18bc,uRam00000000013a18b8);
  _DAT_013a18b0 = uVar3;
  uRam00000000013a18b4 = uVar5;
  uRam00000000013a18b8 = uVar4;
  uRam00000000013a18bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0d80;
  bVar10 = DAT_013a0d80 != (code *)0x0;
  local_28 = DAT_013a0d80;
  DAT_013a0d80 = FUN_001766f0;
  local_20 = DAT_013a0d88;
  DAT_013a0d88 = FUN_00182790;
  local_38 = _DAT_013a0d70;
  uStack_30 = uRam00000000013a0d78;
  uRam00000000013a0d78 = uVar1;
  _DAT_013a0d70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0380;
  bVar10 = DAT_013a0380 != (code *)0x0;
  local_28 = DAT_013a0380;
  DAT_013a0380 = FUN_001766c0;
  local_20 = DAT_013a0388;
  DAT_013a0388 = packethandlers::Audio::SOUND_AREA_SYNTH_2;
  local_38 = _DAT_013a0370;
  uStack_30 = uRam00000000013a0378;
  uRam00000000013a0378 = uVar1;
  _DAT_013a0370 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a06c0;
  bVar10 = DAT_013a06c0 != (code *)0x0;
  local_28 = DAT_013a06c0;
  DAT_013a06c0 = FUN_00176690;
  local_20 = DAT_013a06c8;
  DAT_013a06c8 = packethandlers::Audio::SYNTH_SOUND;
  local_38 = _DAT_013a06b0;
  uStack_30 = uRam00000000013a06b8;
  uRam00000000013a06b8 = uVar1;
  _DAT_013a06b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a02c0;
  bVar10 = DAT_013a02c0 != (code *)0x0;
  local_28 = DAT_013a02c0;
  DAT_013a02c0 = FUN_00176150;
  local_20 = DAT_013a02c8;
  DAT_013a02c8 = packethandlers::Audio::SOUND_GROUP_STOP;
  local_38 = _DAT_013a02b0;
  uStack_30 = uRam00000000013a02b8;
  uRam00000000013a02b8 = uVar1;
  _DAT_013a02b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0c80;
  uVar4 = (undefined4)uStack_30;
  uVar6 = uStack_30._4_4_;
  bVar10 = DAT_013a0c80 != (code *)0x0;
  local_28 = DAT_013a0c80;
  DAT_013a0c80 = FUN_00176120;
  local_20 = DAT_013a0c88;
  DAT_013a0c88 = packethandlers::Audio::SOUND_STOP;
  local_38 = CONCAT44(uRam00000000013a0c74,_DAT_013a0c70);
  uStack_30._0_4_ = uRam00000000013a0c78;
  uStack_30._4_4_ = uRam00000000013a0c7c;
  _DAT_013a0c70 = uVar3;
  uRam00000000013a0c74 = uVar5;
  uRam00000000013a0c78 = uVar4;
  uRam00000000013a0c7c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0b40;
  bVar10 = DAT_013a0b40 != (code *)0x0;
  local_28 = DAT_013a0b40;
  DAT_013a0b40 = FUN_001760e0;
  local_20 = DAT_013a0b48;
  DAT_013a0b48 = packethandlers::Misc::SKIP_2_BYTES;
  local_38 = CONCAT44(uRam00000000013a0b34,_DAT_013a0b30);
  uStack_30._0_4_ = uRam00000000013a0b38;
  uStack_30._4_4_ = uRam00000000013a0b3c;
  _DAT_013a0b30 = uVar3;
  uRam00000000013a0b34 = uVar5;
  uRam00000000013a0b38 = uVar4;
  uRam00000000013a0b3c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0200;
  bVar10 = DAT_013a0200 != (code *)0x0;
  local_28 = DAT_013a0200;
  DAT_013a0200 = FUN_001760b0;
  local_20 = DAT_013a0208;
  DAT_013a0208 = packethandlers::Audio::SOUND_GROUP_SPEED;
  local_38 = CONCAT44(uRam00000000013a01f4,_DAT_013a01f0);
  uStack_30._0_4_ = uRam00000000013a01f8;
  uStack_30._4_4_ = uRam00000000013a01fc;
  _DAT_013a01f0 = uVar3;
  uRam00000000013a01f4 = uVar5;
  uRam00000000013a01f8 = uVar4;
  uRam00000000013a01fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0140;
  bVar10 = DAT_013a0140 != (code *)0x0;
  local_28 = DAT_013a0140;
  DAT_013a0140 = FUN_00176080;
  local_20 = DAT_013a0148;
  DAT_013a0148 = packethandlers::Audio::VORBIS_SONG;
  local_38 = CONCAT44(uRam00000000013a0134,_DAT_013a0130);
  uStack_30 = CONCAT44(uRam00000000013a013c,uRam00000000013a0138);
  _DAT_013a0130 = uVar3;
  uRam00000000013a0134 = uVar5;
  uRam00000000013a0138 = uVar4;
  uRam00000000013a013c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_0139ff80;
  bVar10 = DAT_0139ff80 != (code *)0x0;
  local_28 = DAT_0139ff80;
  DAT_0139ff80 = FUN_00175de0;
  local_20 = DAT_0139ff88;
  DAT_0139ff88 = packethandlers::Audio::SOUND_MODIFY;
  local_38 = _DAT_0139ff70;
  uStack_30 = uRam000000000139ff78;
  uRam000000000139ff78 = uVar1;
  _DAT_0139ff70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a11c0;
  bVar10 = DAT_013a11c0 != (code *)0x0;
  local_28 = DAT_013a11c0;
  DAT_013a11c0 = FUN_00175db0;
  local_20 = DAT_013a11c8;
  DAT_013a11c8 = packethandlers::Audio::MIDI_SONG;
  local_38 = _DAT_013a11b0;
  uStack_30 = uRam00000000013a11b8;
  uRam00000000013a11b8 = uVar1;
  _DAT_013a11b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0040;
  bVar10 = DAT_013a0040 != (code *)0x0;
  local_28 = DAT_013a0040;
  DAT_013a0040 = FUN_00175d80;
  local_20 = DAT_013a0048;
  DAT_013a0048 = FUN_001827d0;
  local_38 = _DAT_013a0030;
  uStack_30 = uRam00000000013a0038;
  uRam00000000013a0038 = uVar1;
  _DAT_013a0030 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1b80;
  bVar10 = DAT_013a1b80 != (code *)0x0;
  local_28 = DAT_013a1b80;
  DAT_013a1b80 = FUN_00175d50;
  local_20 = DAT_013a1b88;
  DAT_013a1b88 = FUN_001dafa0;
  local_38 = _DAT_013a1b70;
  uStack_30 = uRam00000000013a1b78;
  uRam00000000013a1b78 = uVar1;
  _DAT_013a1b70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a1580;
  uVar4 = (undefined4)uStack_30;
  uVar6 = uStack_30._4_4_;
  bVar10 = DAT_013a1580 != (code *)0x0;
  local_28 = DAT_013a1580;
  DAT_013a1580 = FUN_00175d20;
  local_20 = DAT_013a1588;
  DAT_013a1588 = FUN_001872e0;
  local_38 = CONCAT44(uRam00000000013a1574,_DAT_013a1570);
  uStack_30._0_4_ = uRam00000000013a1578;
  uStack_30._4_4_ = uRam00000000013a157c;
  _DAT_013a1570 = uVar3;
  uRam00000000013a1574 = uVar5;
  uRam00000000013a1578 = uVar4;
  uRam00000000013a157c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0440;
  bVar10 = DAT_013a0440 != (code *)0x0;
  local_28 = DAT_013a0440;
  DAT_013a0440 = FUN_00175cf0;
  local_20 = DAT_013a0448;
  DAT_013a0448 = packethandlers::Audio::VORBIS_PRELOAD;
  local_38 = CONCAT44(uRam00000000013a0434,_DAT_013a0430);
  uStack_30._0_4_ = uRam00000000013a0438;
  uStack_30._4_4_ = uRam00000000013a043c;
  _DAT_013a0430 = uVar3;
  uRam00000000013a0434 = uVar5;
  uRam00000000013a0438 = uVar4;
  uRam00000000013a043c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1440;
  bVar10 = DAT_013a1440 != (code *)0x0;
  local_28 = DAT_013a1440;
  DAT_013a1440 = FUN_00175cc0;
  local_20 = DAT_013a1448;
  DAT_013a1448 = packethandlers::Camera::CAM_UPDATE;
  local_38 = CONCAT44(uRam00000000013a1434,_DAT_013a1430);
  uStack_30._0_4_ = uRam00000000013a1438;
  uStack_30._4_4_ = uRam00000000013a143c;
  _DAT_013a1430 = uVar3;
  uRam00000000013a1434 = uVar5;
  uRam00000000013a1438 = uVar4;
  uRam00000000013a143c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1500;
  bVar10 = DAT_013a1500 != (code *)0x0;
  local_28 = DAT_013a1500;
  DAT_013a1500 = FUN_00175c90;
  local_20 = DAT_013a1508;
  DAT_013a1508 = packethandlers::Camera::CAM_FORCEANGLE;
  local_38 = CONCAT44(uRam00000000013a14f4,_DAT_013a14f0);
  uStack_30 = CONCAT44(uRam00000000013a14fc,uRam00000000013a14f8);
  _DAT_013a14f0 = uVar3;
  uRam00000000013a14f4 = uVar5;
  uRam00000000013a14f8 = uVar4;
  uRam00000000013a14fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0e80;
  bVar10 = DAT_013a0e80 != (code *)0x0;
  local_28 = DAT_013a0e80;
  DAT_013a0e80 = FUN_00175c60;
  local_20 = DAT_013a0e88;
  DAT_013a0e88 = packethandlers::Camera::CAM_SMOOTHRESET;
  local_38 = _DAT_013a0e70;
  uStack_30 = uRam00000000013a0e78;
  uRam00000000013a0e78 = uVar1;
  _DAT_013a0e70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1140;
  bVar10 = DAT_013a1140 != (code *)0x0;
  local_28 = DAT_013a1140;
  DAT_013a1140 = FUN_00175c30;
  local_20 = DAT_013a1148;
  DAT_013a1148 = packethandlers::ClientState::RUNCLIENTSCRIPT_SHORT;
  local_38 = _DAT_013a1130;
  uStack_30 = uRam00000000013a1138;
  uRam00000000013a1138 = uVar1;
  _DAT_013a1130 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1940;
  bVar10 = DAT_013a1940 != (code *)0x0;
  local_28 = DAT_013a1940;
  DAT_013a1940 = FUN_00175c00;
  local_20 = DAT_013a1948;
  DAT_013a1948 = FUN_001870c0;
  local_38 = _DAT_013a1930;
  uStack_30 = uRam00000000013a1938;
  uRam00000000013a1938 = uVar1;
  _DAT_013a1930 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1600;
  bVar10 = DAT_013a1600 != (code *)0x0;
  local_28 = DAT_013a1600;
  DAT_013a1600 = FUN_00175bd0;
  local_20 = DAT_013a1608;
  DAT_013a1608 = FUN_00186fe0;
  local_38 = _DAT_013a15f0;
  uStack_30 = uRam00000000013a15f8;
  uRam00000000013a15f8 = uVar1;
  _DAT_013a15f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a12c0;
  uVar4 = (undefined4)uStack_30;
  uVar6 = uStack_30._4_4_;
  bVar10 = DAT_013a12c0 != (code *)0x0;
  local_28 = DAT_013a12c0;
  DAT_013a12c0 = FUN_00175ba0;
  local_20 = DAT_013a12c8;
  DAT_013a12c8 = packethandlers::Camera::CAM_RESET;
  local_38 = CONCAT44(uRam00000000013a12b4,_DAT_013a12b0);
  uStack_30._0_4_ = uRam00000000013a12b8;
  uStack_30._4_4_ = uRam00000000013a12bc;
  _DAT_013a12b0 = uVar3;
  uRam00000000013a12b4 = uVar5;
  uRam00000000013a12b8 = uVar4;
  uRam00000000013a12bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0fc0;
  bVar10 = DAT_013a0fc0 != (code *)0x0;
  local_28 = DAT_013a0fc0;
  DAT_013a0fc0 = FUN_00175ab0;
  local_20 = DAT_013a0fc8;
  DAT_013a0fc8 = FUN_00175ae0;
  local_38 = CONCAT44(uRam00000000013a0fb4,_DAT_013a0fb0);
  uStack_30._0_4_ = uRam00000000013a0fb8;
  uStack_30._4_4_ = uRam00000000013a0fbc;
  _DAT_013a0fb0 = uVar3;
  uRam00000000013a0fb4 = uVar5;
  uRam00000000013a0fb8 = uVar4;
  uRam00000000013a0fbc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1280;
  bVar10 = DAT_013a1280 != (code *)0x0;
  local_28 = DAT_013a1280;
  DAT_013a1280 = FUN_00175a80;
  local_20 = DAT_013a1288;
  DAT_013a1288 = FUN_001daf00;
  local_38 = CONCAT44(uRam00000000013a1274,_DAT_013a1270);
  uStack_30._0_4_ = uRam00000000013a1278;
  uStack_30._4_4_ = uRam00000000013a127c;
  _DAT_013a1270 = uVar3;
  uRam00000000013a1274 = uVar5;
  uRam00000000013a1278 = uVar4;
  uRam00000000013a127c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0bc0;
  bVar10 = DAT_013a0bc0 != (code *)0x0;
  local_28 = DAT_013a0bc0;
  DAT_013a0bc0 = FUN_00175a40;
  local_20 = DAT_013a0bc8;
  DAT_013a0bc8 = FUN_00175a70;
  local_38 = CONCAT44(uRam00000000013a0bb4,_DAT_013a0bb0);
  uStack_30 = CONCAT44(uRam00000000013a0bbc,uRam00000000013a0bb8);
  _DAT_013a0bb0 = uVar3;
  uRam00000000013a0bb4 = uVar5;
  uRam00000000013a0bb8 = uVar4;
  uRam00000000013a0bbc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1840;
  bVar10 = DAT_013a1840 != (code *)0x0;
  local_28 = DAT_013a1840;
  DAT_013a1840 = FUN_00175a10;
  local_20 = DAT_013a1848;
  DAT_013a1848 = packethandlers::Chat::MESSAGE_FRIENDCHANNEL;
  local_38 = _DAT_013a1830;
  uStack_30 = uRam00000000013a1838;
  uRam00000000013a1838 = uVar1;
  _DAT_013a1830 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1200;
  bVar10 = DAT_013a1200 != (code *)0x0;
  local_28 = DAT_013a1200;
  DAT_013a1200 = FUN_001759e0;
  local_20 = DAT_013a1208;
  DAT_013a1208 = packethandlers::Chat::MESSAGE_GAME;
  local_38 = _DAT_013a11f0;
  uStack_30 = uRam00000000013a11f8;
  uRam00000000013a11f8 = uVar1;
  _DAT_013a11f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a14c0;
  bVar10 = DAT_013a14c0 != (code *)0x0;
  local_28 = DAT_013a14c0;
  DAT_013a14c0 = FUN_00175960;
  local_20 = DAT_013a14c8;
  DAT_013a14c8 = FUN_00175990;
  local_38 = _DAT_013a14b0;
  uStack_30 = uRam00000000013a14b8;
  uRam00000000013a14b8 = uVar1;
  _DAT_013a14b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  if (DAT_013942b8 == '\0') {
    iVar9 = __cxa_guard_acquire(&DAT_013942b8);
    if (iVar9 != 0) {
      DAT_013942f0 = 0x1394498;
      _DAT_01394300 = &DAT_01394308;
      DAT_013942e0 = &DAT_01394308;
      DAT_013942e8 = &DAT_01394308;
      __cxa_guard_release(&DAT_013942b8);
      __cxa_atexit(FUN_000ec2a0,&DAT_013942e0,&PTR_LOOP_01391000);
    }
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1bc0;
  bVar10 = DAT_013a1bc0 != (code *)0x0;
  local_28 = DAT_013a1bc0;
  DAT_013a1bc0 = FUN_00175930;
  local_20 = DAT_013a1bc8;
  DAT_013a1bc8 = packethandlers::Chat::MESSAGE_PUBLIC;
  local_38 = _DAT_013a1bb0;
  uStack_30._0_4_ = (undefined4)uRam00000000013a1bb8;
  uStack_30._4_4_ = uRam00000000013a1bb8._4_4_;
  uRam00000000013a1bb8 = uVar1;
  _DAT_013a1bb0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1a40;
  bVar10 = DAT_013a1a40 != (code *)0x0;
  local_28 = DAT_013a1a40;
  DAT_013a1a40 = FUN_00175900;
  local_20 = DAT_013a1a48;
  DAT_013a1a48 = FUN_00189a20;
  local_38 = CONCAT44(uRam00000000013a1a34,_DAT_013a1a30);
  uStack_30._0_4_ = uRam00000000013a1a38;
  uStack_30._4_4_ = uRam00000000013a1a3c;
  _DAT_013a1a30 = uVar3;
  uRam00000000013a1a34 = uVar5;
  uRam00000000013a1a38 = uVar4;
  uRam00000000013a1a3c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0dc0;
  bVar10 = DAT_013a0dc0 != (code *)0x0;
  local_28 = DAT_013a0dc0;
  DAT_013a0dc0 = FUN_001758d0;
  local_20 = DAT_013a0dc8;
  DAT_013a0dc8 = packethandlers::Chat::MESSAGE_FRIENDCHAT;
  local_38 = CONCAT44(uRam00000000013a0db4,_DAT_013a0db0);
  uStack_30._0_4_ = uRam00000000013a0db8;
  uStack_30._4_4_ = uRam00000000013a0dbc;
  _DAT_013a0db0 = uVar3;
  uRam00000000013a0db4 = uVar5;
  uRam00000000013a0db8 = uVar4;
  uRam00000000013a0dbc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1c80;
  bVar10 = DAT_013a1c80 != (code *)0x0;
  local_28 = DAT_013a1c80;
  DAT_013a1c80 = FUN_001758a0;
  local_20 = DAT_013a1c88;
  DAT_013a1c88 = packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT;
  local_38 = CONCAT44(uRam00000000013a1c74,_DAT_013a1c70);
  uStack_30._0_4_ = uRam00000000013a1c78;
  uStack_30._4_4_ = uRam00000000013a1c7c;
  _DAT_013a1c70 = uVar3;
  uRam00000000013a1c74 = uVar5;
  uRam00000000013a1c78 = uVar4;
  uRam00000000013a1c7c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a10c0;
  bVar10 = DAT_013a10c0 != (code *)0x0;
  local_28 = DAT_013a10c0;
  DAT_013a10c0 = FUN_00175870;
  local_38 = CONCAT44(uRam00000000013a10b4,_DAT_013a10b0);
  uVar1 = CONCAT44(uRam00000000013a10bc,uRam00000000013a10b8);
  local_20 = DAT_013a10c8;
  DAT_013a10c8 = packethandlers::Chat::MESSAGE_CLANCHANNEL;
  _DAT_013a10b0 = uVar3;
  uRam00000000013a10b4 = uVar5;
  uRam00000000013a10b8 = (undefined4)uStack_30;
  uRam00000000013a10bc = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0f40;
  bVar10 = DAT_013a0f40 != (code *)0x0;
  local_28 = DAT_013a0f40;
  DAT_013a0f40 = FUN_00175840;
  local_20 = DAT_013a0f48;
  DAT_013a0f48 = packethandlers::Chat::CHAT_FILTER_SETTINGS;
  local_38 = _DAT_013a0f30;
  uStack_30 = uRam00000000013a0f38;
  uRam00000000013a0f38 = uVar1;
  _DAT_013a0f30 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1d00;
  bVar10 = DAT_013a1d00 != (code *)0x0;
  local_28 = DAT_013a1d00;
  DAT_013a1d00 = FUN_00175810;
  local_20 = DAT_013a1d08;
  DAT_013a1d08 = packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHANNEL;
  local_38 = _DAT_013a1cf0;
  uStack_30 = uRam00000000013a1cf8;
  uRam00000000013a1cf8 = uVar1;
  _DAT_013a1cf0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1900;
  bVar10 = DAT_013a1900 != (code *)0x0;
  local_28 = DAT_013a1900;
  DAT_013a1900 = FUN_001757e0;
  local_20 = DAT_013a1908;
  DAT_013a1908 = FUN_001a1100;
  local_38 = _DAT_013a18f0;
  uStack_30 = uRam00000000013a18f8;
  uRam00000000013a18f8 = uVar1;
  _DAT_013a18f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1640;
  bVar10 = DAT_013a1640 != (code *)0x0;
  local_28 = DAT_013a1640;
  DAT_013a1640 = FUN_001757b0;
  local_20 = DAT_013a1648;
  DAT_013a1648 = packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE;
  local_38 = _DAT_013a1630;
  uStack_30._0_4_ = (undefined4)uRam00000000013a1638;
  uStack_30._4_4_ = uRam00000000013a1638._4_4_;
  uRam00000000013a1638 = uVar1;
  _DAT_013a1630 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a05c0;
  bVar10 = DAT_013a05c0 != (code *)0x0;
  local_28 = DAT_013a05c0;
  DAT_013a05c0 = FUN_00175780;
  local_20 = DAT_013a05c8;
  DAT_013a05c8 = packethandlers::Chat::MESSAGE_PRIVATE_ECHO;
  local_38 = CONCAT44(uRam00000000013a05b4,_DAT_013a05b0);
  uStack_30._0_4_ = uRam00000000013a05b8;
  uStack_30._4_4_ = uRam00000000013a05bc;
  _DAT_013a05b0 = uVar3;
  uRam00000000013a05b4 = uVar5;
  uRam00000000013a05b8 = uVar4;
  uRam00000000013a05bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0500;
  bVar10 = DAT_013a0500 != (code *)0x0;
  local_28 = DAT_013a0500;
  DAT_013a0500 = FUN_00175750;
  local_20 = DAT_013a0508;
  DAT_013a0508 = packethandlers::Chat::MESSAGE_PRIVATE;
  local_38 = CONCAT44(uRam00000000013a04f4,_DAT_013a04f0);
  uStack_30._0_4_ = uRam00000000013a04f8;
  uStack_30._4_4_ = uRam00000000013a04fc;
  _DAT_013a04f0 = uVar3;
  uRam00000000013a04f4 = uVar5;
  uRam00000000013a04f8 = uVar4;
  uRam00000000013a04fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1980;
  bVar10 = DAT_013a1980 != (code *)0x0;
  local_28 = DAT_013a1980;
  DAT_013a1980 = FUN_00175720;
  local_20 = DAT_013a1988;
  DAT_013a1988 = packethandlers::Clans::CLANSETTINGS_FULL;
  local_38 = CONCAT44(uRam00000000013a1974,_DAT_013a1970);
  uStack_30._0_4_ = uRam00000000013a1978;
  uStack_30._4_4_ = uRam00000000013a197c;
  _DAT_013a1970 = uVar3;
  uRam00000000013a1974 = uVar5;
  uRam00000000013a1978 = uVar4;
  uRam00000000013a197c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0e00;
  bVar10 = DAT_013a0e00 != (code *)0x0;
  local_28 = DAT_013a0e00;
  DAT_013a0e00 = FUN_001756f0;
  local_38 = CONCAT44(uRam00000000013a0df4,_DAT_013a0df0);
  uVar1 = CONCAT44(uRam00000000013a0dfc,uRam00000000013a0df8);
  local_20 = DAT_013a0e08;
  DAT_013a0e08 = packethandlers::Chat::CLANSETTINGS_DELTA_CHAT;
  _DAT_013a0df0 = uVar3;
  uRam00000000013a0df4 = uVar5;
  uRam00000000013a0df8 = (undefined4)uStack_30;
  uRam00000000013a0dfc = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1c00;
  bVar10 = DAT_013a1c00 != (code *)0x0;
  local_28 = DAT_013a1c00;
  DAT_013a1c00 = FUN_001756c0;
  local_20 = DAT_013a1c08;
  DAT_013a1c08 = packethandlers::Chat::CLANCHANNEL_FULL_CHAT;
  local_38 = _DAT_013a1bf0;
  uStack_30 = uRam00000000013a1bf8;
  uRam00000000013a1bf8 = uVar1;
  _DAT_013a1bf0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1040;
  bVar10 = DAT_013a1040 != (code *)0x0;
  local_28 = DAT_013a1040;
  DAT_013a1040 = FUN_00175690;
  local_20 = DAT_013a1048;
  DAT_013a1048 = packethandlers::Clans::CLANSETTINGS_DELTA;
  local_38 = _DAT_013a1030;
  uStack_30 = uRam00000000013a1038;
  uRam00000000013a1038 = uVar1;
  _DAT_013a1030 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0300;
  bVar10 = DAT_013a0300 != (code *)0x0;
  local_28 = DAT_013a0300;
  DAT_013a0300 = FUN_00175660;
  local_20 = DAT_013a0308;
  DAT_013a0308 = packethandlers::Misc::UPDATE_URL_STRING;
  local_38 = _DAT_013a02f0;
  uStack_30 = uRam00000000013a02f8;
  uRam00000000013a02f8 = uVar1;
  _DAT_013a02f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1080;
  bVar10 = DAT_013a1080 != (code *)0x0;
  local_28 = DAT_013a1080;
  DAT_013a1080 = FUN_00175630;
  local_20 = DAT_013a1088;
  DAT_013a1088 = packethandlers::NPCInfo::SET_NPC_UPDATE_ORIGIN;
  local_38 = _DAT_013a1070;
  uStack_30._0_4_ = (undefined4)uRam00000000013a1078;
  uStack_30._4_4_ = uRam00000000013a1078._4_4_;
  uRam00000000013a1078 = uVar1;
  _DAT_013a1070 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1b40;
  bVar10 = DAT_013a1b40 != (code *)0x0;
  local_28 = DAT_013a1b40;
  DAT_013a1b40 = FUN_00175600;
  local_20 = DAT_013a1b48;
  DAT_013a1b48 = packethandlers::Camera::CAM_TARGET;
  local_38 = CONCAT44(uRam00000000013a1b34,_DAT_013a1b30);
  uStack_30._0_4_ = uRam00000000013a1b38;
  uStack_30._4_4_ = uRam00000000013a1b3c;
  _DAT_013a1b30 = uVar3;
  uRam00000000013a1b34 = uVar5;
  uRam00000000013a1b38 = uVar4;
  uRam00000000013a1b3c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0080;
  bVar10 = DAT_013a0080 != (code *)0x0;
  local_28 = DAT_013a0080;
  DAT_013a0080 = FUN_001755d0;
  local_20 = DAT_013a0088;
  DAT_013a0088 = packethandlers::WorldData::SWITCH_WORLD;
  local_38 = CONCAT44(uRam00000000013a0074,_DAT_013a0070);
  uStack_30._0_4_ = uRam00000000013a0078;
  uStack_30._4_4_ = uRam00000000013a007c;
  _DAT_013a0070 = uVar3;
  uRam00000000013a0074 = uVar5;
  uRam00000000013a0078 = uVar4;
  uRam00000000013a007c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1340;
  bVar10 = DAT_013a1340 != (code *)0x0;
  local_28 = DAT_013a1340;
  DAT_013a1340 = FUN_001755a0;
  local_20 = DAT_013a1348;
  DAT_013a1348 = packethandlers::ClientState::REBUILD_REGION_ALT;
  local_38 = CONCAT44(uRam00000000013a1334,_DAT_013a1330);
  uStack_30._0_4_ = uRam00000000013a1338;
  uStack_30._4_4_ = uRam00000000013a133c;
  _DAT_013a1330 = uVar3;
  uRam00000000013a1334 = uVar5;
  uRam00000000013a1338 = uVar4;
  uRam00000000013a133c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a1380;
  bVar10 = DAT_013a1380 != (code *)0x0;
  local_28 = DAT_013a1380;
  DAT_013a1380 = FUN_00175570;
  local_38 = CONCAT44(uRam00000000013a1374,_DAT_013a1370);
  uVar1 = CONCAT44(uRam00000000013a137c,uRam00000000013a1378);
  local_20 = DAT_013a1388;
  DAT_013a1388 = packethandlers::ClientState::REBUILD_NORMAL_SIMPLE;
  _DAT_013a1370 = uVar3;
  uRam00000000013a1374 = uVar5;
  uRam00000000013a1378 = (undefined4)uStack_30;
  uRam00000000013a137c = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1cc0;
  bVar10 = DAT_013a1cc0 != (code *)0x0;
  local_28 = DAT_013a1cc0;
  DAT_013a1cc0 = FUN_00175540;
  local_20 = DAT_013a1cc8;
  DAT_013a1cc8 = packethandlers::NPCInfo::SET_NPC_OP;
  local_38 = _DAT_013a1cb0;
  uStack_30 = uRam00000000013a1cb8;
  uRam00000000013a1cb8 = uVar1;
  _DAT_013a1cb0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1880;
  bVar10 = DAT_013a1880 != (code *)0x0;
  local_28 = DAT_013a1880;
  DAT_013a1880 = FUN_00175510;
  local_20 = DAT_013a1888;
  DAT_013a1888 = FUN_00186de0;
  local_38 = _DAT_013a1870;
  uStack_30 = uRam00000000013a1878;
  uRam00000000013a1878 = uVar1;
  _DAT_013a1870 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1000;
  bVar10 = DAT_013a1000 != (code *)0x0;
  local_28 = DAT_013a1000;
  DAT_013a1000 = FUN_001754e0;
  local_20 = DAT_013a1008;
  DAT_013a1008 = packethandlers::ClientState::RUNCLIENTSCRIPT_impl;
  local_38 = _DAT_013a0ff0;
  uStack_30 = uRam00000000013a0ff8;
  uRam00000000013a0ff8 = uVar1;
  _DAT_013a0ff0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0f80;
  bVar10 = DAT_013a0f80 != (code *)0x0;
  local_28 = DAT_013a0f80;
  DAT_013a0f80 = FUN_00175420;
  local_20 = DAT_013a0f88;
  DAT_013a0f88 = packethandlers::ClientState::SET_TICK_TIMER;
  local_38 = _DAT_013a0f70;
  uStack_30._0_4_ = (undefined4)uRam00000000013a0f78;
  uStack_30._4_4_ = uRam00000000013a0f78._4_4_;
  uRam00000000013a0f78 = uVar1;
  _DAT_013a0f70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0400;
  bVar10 = DAT_013a0400 != (code *)0x0;
  local_28 = DAT_013a0400;
  DAT_013a0400 = FUN_001753f0;
  local_20 = DAT_013a0408;
  DAT_013a0408 = FUN_00186d60;
  local_38 = CONCAT44(uRam00000000013a03f4,_DAT_013a03f0);
  uStack_30._0_4_ = uRam00000000013a03f8;
  uStack_30._4_4_ = uRam00000000013a03fc;
  _DAT_013a03f0 = uVar3;
  uRam00000000013a03f4 = uVar5;
  uRam00000000013a03f8 = uVar4;
  uRam00000000013a03fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0580;
  bVar10 = DAT_013a0580 != (code *)0x0;
  local_28 = DAT_013a0580;
  DAT_013a0580 = FUN_001753c0;
  local_20 = DAT_013a0588;
  DAT_013a0588 = FUN_00186ce0;
  local_38 = CONCAT44(uRam00000000013a0574,_DAT_013a0570);
  uStack_30._0_4_ = uRam00000000013a0578;
  uStack_30._4_4_ = uRam00000000013a057c;
  _DAT_013a0570 = uVar3;
  uRam00000000013a0574 = uVar5;
  uRam00000000013a0578 = uVar4;
  uRam00000000013a057c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1400;
  bVar10 = DAT_013a1400 != (code *)0x0;
  local_28 = DAT_013a1400;
  DAT_013a1400 = FUN_00175390;
  local_20 = DAT_013a1408;
  DAT_013a1408 = packethandlers::NPCInfo::NPC_HEADICON_SPECIFIC;
  local_38 = CONCAT44(uRam00000000013a13f4,_DAT_013a13f0);
  uStack_30._0_4_ = uRam00000000013a13f8;
  uStack_30._4_4_ = uRam00000000013a13fc;
  _DAT_013a13f0 = uVar3;
  uRam00000000013a13f4 = uVar5;
  uRam00000000013a13f8 = uVar4;
  uRam00000000013a13fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0280;
  bVar10 = DAT_013a0280 != (code *)0x0;
  local_28 = DAT_013a0280;
  DAT_013a0280 = FUN_00175360;
  local_38 = CONCAT44(uRam00000000013a0274,_DAT_013a0270);
  uVar1 = CONCAT44(uRam00000000013a027c,uRam00000000013a0278);
  local_20 = DAT_013a0288;
  DAT_013a0288 = packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2;
  _DAT_013a0270 = uVar3;
  uRam00000000013a0274 = uVar5;
  uRam00000000013a0278 = (undefined4)uStack_30;
  uRam00000000013a027c = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0cc0;
  bVar10 = DAT_013a0cc0 != (code *)0x0;
  local_28 = DAT_013a0cc0;
  DAT_013a0cc0 = FUN_00175330;
  local_20 = DAT_013a0cc8;
  DAT_013a0cc8 = packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED;
  local_38 = _DAT_013a0cb0;
  uStack_30 = uRam00000000013a0cb8;
  uRam00000000013a0cb8 = uVar1;
  _DAT_013a0cb0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0000;
  bVar10 = DAT_013a0000 != (code *)0x0;
  local_28 = DAT_013a0000;
  DAT_013a0000 = FUN_00175300;
  local_20 = DAT_013a0008;
  DAT_013a0008 = FUN_001dec60;
  local_38 = _DAT_0139fff0;
  uStack_30 = uRam000000000139fff8;
  uRam000000000139fff8 = uVar1;
  _DAT_0139fff0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0900;
  bVar10 = DAT_013a0900 != (code *)0x0;
  local_28 = DAT_013a0900;
  DAT_013a0900 = FUN_001752d0;
  local_20 = DAT_013a0908;
  DAT_013a0908 = FUN_001de870;
  local_38 = _DAT_013a08f0;
  uStack_30 = uRam00000000013a08f8;
  uRam00000000013a08f8 = uVar1;
  _DAT_013a08f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0240;
  bVar10 = DAT_013a0240 != (code *)0x0;
  local_28 = DAT_013a0240;
  DAT_013a0240 = FUN_001752a0;
  local_20 = DAT_013a0248;
  DAT_013a0248 = FUN_001dd5a0;
  local_38 = _DAT_013a0230;
  uStack_30._0_4_ = (undefined4)uRam00000000013a0238;
  uStack_30._4_4_ = uRam00000000013a0238._4_4_;
  uRam00000000013a0238 = uVar1;
  _DAT_013a0230 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0d00;
  bVar10 = DAT_013a0d00 != (code *)0x0;
  local_28 = DAT_013a0d00;
  DAT_013a0d00 = FUN_00175270;
  local_20 = DAT_013a0d08;
  DAT_013a0d08 = FUN_001dc7f0;
  local_38 = CONCAT44(uRam00000000013a0cf4,_DAT_013a0cf0);
  uStack_30._0_4_ = uRam00000000013a0cf8;
  uStack_30._4_4_ = uRam00000000013a0cfc;
  _DAT_013a0cf0 = uVar3;
  uRam00000000013a0cf4 = uVar5;
  uRam00000000013a0cf8 = uVar4;
  uRam00000000013a0cfc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0940;
  bVar10 = DAT_013a0940 != (code *)0x0;
  local_28 = DAT_013a0940;
  DAT_013a0940 = FUN_00175240;
  local_20 = DAT_013a0948;
  DAT_013a0948 = FUN_001dc2c0;
  local_38 = CONCAT44(uRam00000000013a0934,_DAT_013a0930);
  uStack_30._0_4_ = uRam00000000013a0938;
  uStack_30._4_4_ = uRam00000000013a093c;
  _DAT_013a0930 = uVar3;
  uRam00000000013a0934 = uVar5;
  uRam00000000013a0938 = uVar4;
  uRam00000000013a093c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0540;
  bVar10 = DAT_013a0540 != (code *)0x0;
  local_28 = DAT_013a0540;
  DAT_013a0540 = FUN_00175210;
  local_20 = DAT_013a0548;
  DAT_013a0548 = FUN_001dbd40;
  local_38 = CONCAT44(uRam00000000013a0534,_DAT_013a0530);
  uStack_30._0_4_ = uRam00000000013a0538;
  uStack_30._4_4_ = uRam00000000013a053c;
  _DAT_013a0530 = uVar3;
  uRam00000000013a0534 = uVar5;
  uRam00000000013a0538 = uVar4;
  uRam00000000013a053c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a09c0;
  bVar10 = DAT_013a09c0 != (code *)0x0;
  local_28 = DAT_013a09c0;
  DAT_013a09c0 = FUN_001751e0;
  local_38 = CONCAT44(uRam00000000013a09b4,_DAT_013a09b0);
  uVar1 = CONCAT44(uRam00000000013a09bc,uRam00000000013a09b8);
  local_20 = DAT_013a09c8;
  DAT_013a09c8 = FUN_001db7e0;
  _DAT_013a09b0 = uVar3;
  uRam00000000013a09b4 = uVar5;
  uRam00000000013a09b8 = (undefined4)uStack_30;
  uRam00000000013a09bc = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1540;
  bVar10 = DAT_013a1540 != (code *)0x0;
  local_28 = DAT_013a1540;
  DAT_013a1540 = FUN_001751b0;
  local_20 = DAT_013a1548;
  DAT_013a1548 = packethandlers::Clans::CLANCHANNEL_FULL;
  local_38 = _DAT_013a1530;
  uStack_30 = uRam00000000013a1538;
  uRam00000000013a1538 = uVar1;
  _DAT_013a1530 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0f00;
  bVar10 = DAT_013a0f00 != (code *)0x0;
  local_28 = DAT_013a0f00;
  DAT_013a0f00 = FUN_00175180;
  local_20 = DAT_013a0f08;
  DAT_013a0f08 = packethandlers::Clans::CLANCHANNEL_DELTA;
  local_38 = _DAT_013a0ef0;
  uStack_30 = uRam00000000013a0ef8;
  uRam00000000013a0ef8 = uVar1;
  _DAT_013a0ef0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1a00;
  bVar10 = DAT_013a1a00 != (code *)0x0;
  local_28 = DAT_013a1a00;
  DAT_013a1a00 = FUN_00175150;
  local_20 = DAT_013a1a08;
  DAT_013a1a08 = packethandlers::SiteSettings::UPDATE_SITESETTINGS_thunk;
  local_38 = _DAT_013a19f0;
  uStack_30 = uRam00000000013a19f8;
  uRam00000000013a19f8 = uVar1;
  _DAT_013a19f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1480;
  bVar10 = DAT_013a1480 != (code *)0x0;
  local_28 = DAT_013a1480;
  DAT_013a1480 = FUN_001750f0;
  local_20 = DAT_013a1488;
  DAT_013a1488 = packethandlers::ClientState::SET_READY_FLAG;
  local_38 = _DAT_013a1470;
  uStack_30._0_4_ = (undefined4)uRam00000000013a1478;
  uStack_30._4_4_ = uRam00000000013a1478._4_4_;
  uRam00000000013a1478 = uVar1;
  _DAT_013a1470 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a13c0;
  bVar10 = DAT_013a13c0 != (code *)0x0;
  local_28 = DAT_013a13c0;
  DAT_013a13c0 = FUN_00175090;
  local_20 = DAT_013a13c8;
  DAT_013a13c8 = packethandlers::Misc::SET_RUN_ENERGY;
  local_38 = CONCAT44(uRam00000000013a13b4,_DAT_013a13b0);
  uStack_30._0_4_ = uRam00000000013a13b8;
  uStack_30._4_4_ = uRam00000000013a13bc;
  _DAT_013a13b0 = uVar3;
  uRam00000000013a13b4 = uVar5;
  uRam00000000013a13b8 = uVar4;
  uRam00000000013a13bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1100;
  bVar10 = DAT_013a1100 != (code *)0x0;
  local_28 = DAT_013a1100;
  DAT_013a1100 = FUN_00175060;
  local_20 = DAT_013a1108;
  DAT_013a1108 = packethandlers::PlayerInfo::PLAYER_INFO_DECODE;
  local_38 = CONCAT44(uRam00000000013a10f4,_DAT_013a10f0);
  uStack_30._0_4_ = uRam00000000013a10f8;
  uStack_30._4_4_ = uRam00000000013a10fc;
  _DAT_013a10f0 = uVar3;
  uRam00000000013a10f4 = uVar5;
  uRam00000000013a10f8 = uVar4;
  uRam00000000013a10fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1a80;
  bVar10 = DAT_013a1a80 != (code *)0x0;
  local_28 = DAT_013a1a80;
  DAT_013a1a80 = FUN_00175030;
  local_20 = DAT_013a1a88;
  DAT_013a1a88 = packethandlers::PlayerGroup::PLAYER_OP;
  local_38 = CONCAT44(uRam00000000013a1a74,_DAT_013a1a70);
  uStack_30._0_4_ = uRam00000000013a1a78;
  uStack_30._4_4_ = uRam00000000013a1a7c;
  _DAT_013a1a70 = uVar3;
  uRam00000000013a1a74 = uVar5;
  uRam00000000013a1a78 = uVar4;
  uRam00000000013a1a7c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a1740;
  bVar10 = DAT_013a1740 != (code *)0x0;
  local_28 = DAT_013a1740;
  DAT_013a1740 = FUN_00175000;
  local_38 = CONCAT44(uRam00000000013a1734,_DAT_013a1730);
  uVar1 = CONCAT44(uRam00000000013a173c,uRam00000000013a1738);
  local_20 = DAT_013a1748;
  DAT_013a1748 = packethandlers::Lobby::CHANGE_LOBBY;
  _DAT_013a1730 = uVar3;
  uRam00000000013a1734 = uVar5;
  uRam00000000013a1738 = (undefined4)uStack_30;
  uRam00000000013a173c = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  packethandlers::Interfaces::BindHandlers(param_2);
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0e40;
  bVar10 = DAT_013a0e40 != (code *)0x0;
  local_28 = DAT_013a0e40;
  DAT_013a0e40 = FUN_001748e0;
  local_20 = DAT_013a0e48;
  DAT_013a0e48 = packethandlers::Inventory::UPDATE_INV_PARTIAL;
  local_38 = _DAT_013a0e30;
  uStack_30 = uRam00000000013a0e38;
  uRam00000000013a0e38 = uVar1;
  _DAT_013a0e30 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1300;
  bVar10 = DAT_013a1300 != (code *)0x0;
  local_28 = DAT_013a1300;
  DAT_013a1300 = FUN_001748b0;
  local_20 = DAT_013a1308;
  DAT_013a1308 = packethandlers::Inventory::UPDATE_INV_FULL_impl;
  local_38 = _DAT_013a12f0;
  uStack_30 = uRam00000000013a12f8;
  uRam00000000013a12f8 = uVar1;
  _DAT_013a12f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1ac0;
  bVar10 = DAT_013a1ac0 != (code *)0x0;
  local_28 = DAT_013a1ac0;
  DAT_013a1ac0 = FUN_00174300;
  local_20 = DAT_013a1ac8;
  DAT_013a1ac8 = FUN_00174330;
  local_38 = _DAT_013a1ab0;
  uStack_30 = uRam00000000013a1ab8;
  uRam00000000013a1ab8 = uVar1;
  _DAT_013a1ab0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0ec0;
  bVar10 = DAT_013a0ec0 != (code *)0x0;
  local_28 = DAT_013a0ec0;
  DAT_013a0ec0 = FUN_00173fd0;
  local_20 = DAT_013a0ec8;
  DAT_013a0ec8 = packethandlers::Misc::CUTSCENE_DATA;
  local_38._0_4_ = (undefined4)_DAT_013a0eb0;
  local_38._4_4_ = DAT_013a0eb0_4;
  uStack_30._0_4_ = (undefined4)uRam00000000013a0eb8;
  uStack_30._4_4_ = uRam00000000013a0eb8._4_4_;
  uRam00000000013a0eb8 = uVar1;
  _DAT_013a0eb0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar8 = uStack_30._4_4_;
  uVar7 = (undefined4)uStack_30;
  uVar6 = local_38._4_4_;
  uVar4 = (undefined4)local_38;
  local_28 = DAT_013a0d40;
  DAT_013a0d40 = FUN_00173fb0;
  local_20 = DAT_013a0d48;
  local_38 = CONCAT44(uRam00000000013a0d34,_DAT_013a0d30);
  uStack_30._0_4_ = uRam00000000013a0d38;
  uStack_30._4_4_ = uRam00000000013a0d3c;
  DAT_013a0d48 = FUN_00173fc0;
  _DAT_013a0d30 = uVar4;
  uRam00000000013a0d34 = uVar6;
  uRam00000000013a0d38 = uVar7;
  uRam00000000013a0d3c = uVar8;
  if (local_28 != (code *)0x0) {
    (*local_28)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a03c0;
  bVar10 = DAT_013a03c0 != (code *)0x0;
  local_28 = DAT_013a03c0;
  DAT_013a03c0 = FUN_00173f30;
  local_20 = DAT_013a03c8;
  DAT_013a03c8 = FUN_00173f60;
  local_38 = CONCAT44(uRam00000000013a03b4,_DAT_013a03b0);
  uStack_30._0_4_ = uRam00000000013a03b8;
  uStack_30._4_4_ = uRam00000000013a03bc;
  _DAT_013a03b0 = uVar3;
  uRam00000000013a03b4 = uVar5;
  uRam00000000013a03b8 = uVar4;
  uRam00000000013a03bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0880;
  bVar10 = DAT_013a0880 != (code *)0x0;
  local_28 = DAT_013a0880;
  DAT_013a0880 = FUN_00173ea0;
  local_20 = DAT_013a0888;
  DAT_013a0888 = packethandlers::Chat::SET_CHAT_FILTER_B;
  local_38 = CONCAT44(uRam00000000013a0874,_DAT_013a0870);
  uStack_30._0_4_ = uRam00000000013a0878;
  uStack_30._4_4_ = uRam00000000013a087c;
  _DAT_013a0870 = uVar3;
  uRam00000000013a0874 = uVar5;
  uRam00000000013a0878 = uVar4;
  uRam00000000013a087c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a04c0;
  bVar10 = DAT_013a04c0 != (code *)0x0;
  local_28 = DAT_013a04c0;
  DAT_013a04c0 = FUN_00173e20;
  local_38 = CONCAT44(uRam00000000013a04b4,_DAT_013a04b0);
  uVar1 = CONCAT44(uRam00000000013a04bc,uRam00000000013a04b8);
  local_20 = DAT_013a04c8;
  DAT_013a04c8 = FUN_00173e50;
  _DAT_013a04b0 = uVar3;
  uRam00000000013a04b4 = uVar5;
  uRam00000000013a04b8 = (undefined4)uStack_30;
  uRam00000000013a04bc = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0b80;
  bVar10 = DAT_013a0b80 != (code *)0x0;
  local_28 = DAT_013a0b80;
  DAT_013a0b80 = FUN_00173d70;
  local_20 = DAT_013a0b88;
  DAT_013a0b88 = packethandlers::Chat::SET_CHAT_FILTER_A;
  local_38 = _DAT_013a0b70;
  uStack_30 = uRam00000000013a0b78;
  uRam00000000013a0b78 = uVar1;
  _DAT_013a0b70 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0a40;
  bVar10 = DAT_013a0a40 != (code *)0x0;
  local_28 = DAT_013a0a40;
  DAT_013a0a40 = FUN_00173d40;
  local_20 = DAT_013a0a48;
  DAT_013a0a48 = packethandlers::Misc::SET_URL_STRING;
  local_38 = _DAT_013a0a30;
  uStack_30 = uRam00000000013a0a38;
  uRam00000000013a0a38 = uVar1;
  _DAT_013a0a30 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0780;
  bVar10 = DAT_013a0780 != (code *)0x0;
  local_28 = DAT_013a0780;
  DAT_013a0780 = FUN_00173d10;
  local_20 = DAT_013a0788;
  DAT_013a0788 = packethandlers::PlayerGroup::UPDATE_PLAYER_GROUP;
  local_38 = _DAT_013a0770;
  uStack_30 = uRam00000000013a0778;
  uRam00000000013a0778 = uVar1;
  _DAT_013a0770 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a00c0;
  bVar10 = DAT_013a00c0 != (code *)0x0;
  local_28 = DAT_013a00c0;
  DAT_013a00c0 = FUN_00173ce0;
  local_20 = DAT_013a00c8;
  DAT_013a00c8 = packethandlers::WorldData::SET_WORLD_TARGET;
  local_38 = _DAT_013a00b0;
  uStack_30._0_4_ = (undefined4)uRam00000000013a00b8;
  uStack_30._4_4_ = uRam00000000013a00b8._4_4_;
  uRam00000000013a00b8 = uVar1;
  _DAT_013a00b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0600;
  bVar10 = DAT_013a0600 != (code *)0x0;
  local_28 = DAT_013a0600;
  DAT_013a0600 = FUN_00173cb0;
  local_20 = DAT_013a0608;
  DAT_013a0608 = HandleAntiCheatChallenge;
  local_38 = CONCAT44(uRam00000000013a05f4,_DAT_013a05f0);
  uStack_30._0_4_ = uRam00000000013a05f8;
  uStack_30._4_4_ = uRam00000000013a05fc;
  _DAT_013a05f0 = uVar3;
  uRam00000000013a05f4 = uVar5;
  uRam00000000013a05f8 = uVar4;
  uRam00000000013a05fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a1780;
  bVar10 = DAT_013a1780 != (code *)0x0;
  local_28 = DAT_013a1780;
  DAT_013a1780 = FUN_00173c20;
  local_20 = DAT_013a1788;
  DAT_013a1788 = packethandlers::Misc::SET_MULTIWAY_STATE;
  local_38 = CONCAT44(uRam00000000013a1774,_DAT_013a1770);
  uStack_30._0_4_ = uRam00000000013a1778;
  uStack_30._4_4_ = uRam00000000013a177c;
  _DAT_013a1770 = uVar3;
  uRam00000000013a1774 = uVar5;
  uRam00000000013a1778 = uVar4;
  uRam00000000013a177c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0840;
  bVar10 = DAT_013a0840 != (code *)0x0;
  local_28 = DAT_013a0840;
  DAT_013a0840 = FUN_00173ba0;
  local_20 = DAT_013a0848;
  DAT_013a0848 = FUN_00173bd0;
  local_38 = CONCAT44(uRam00000000013a0834,_DAT_013a0830);
  uStack_30._0_4_ = uRam00000000013a0838;
  uStack_30._4_4_ = uRam00000000013a083c;
  _DAT_013a0830 = uVar3;
  uRam00000000013a0834 = uVar5;
  uRam00000000013a0838 = uVar4;
  uRam00000000013a083c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0a00;
  bVar10 = DAT_013a0a00 != (code *)0x0;
  local_28 = DAT_013a0a00;
  DAT_013a0a00 = FUN_00173b70;
  local_38 = CONCAT44(uRam00000000013a09f4,_DAT_013a09f0);
  uVar1 = CONCAT44(uRam00000000013a09fc,uRam00000000013a09f8);
  local_20 = DAT_013a0a08;
  DAT_013a0a08 = FUN_00187a10;
  _DAT_013a09f0 = uVar3;
  uRam00000000013a09f4 = uVar5;
  uRam00000000013a09f8 = (undefined4)uStack_30;
  uRam00000000013a09fc = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0980;
  bVar10 = DAT_013a0980 != (code *)0x0;
  local_28 = DAT_013a0980;
  DAT_013a0980 = FUN_00173b40;
  local_20 = DAT_013a0988;
  DAT_013a0988 = packethandlers::Interfaces::IF_SET_HTTP_IMAGE;
  local_38 = _DAT_013a0970;
  uStack_30 = uRam00000000013a0978;
  uRam00000000013a0978 = uVar1;
  _DAT_013a0970 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0c40;
  bVar10 = DAT_013a0c40 != (code *)0x0;
  local_28 = DAT_013a0c40;
  DAT_013a0c40 = FUN_00173ad0;
  local_20 = DAT_013a0c48;
  DAT_013a0c48 = packethandlers::NPCInfo::SET_NPC_UPDATE_FLAG;
  local_38 = _DAT_013a0c30;
  uStack_30 = uRam00000000013a0c38;
  uRam00000000013a0c38 = uVar1;
  _DAT_013a0c30 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a08c0;
  bVar10 = DAT_013a08c0 != (code *)0x0;
  local_28 = DAT_013a08c0;
  DAT_013a08c0 = FUN_00173aa0;
  local_20 = DAT_013a08c8;
  DAT_013a08c8 = packethandlers::Misc::LOGOUT;
  local_38 = _DAT_013a08b0;
  uStack_30 = uRam00000000013a08b8;
  uRam00000000013a08b8 = uVar1;
  _DAT_013a08b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_0139ffc0;
  bVar10 = DAT_0139ffc0 != (code *)0x0;
  local_28 = DAT_0139ffc0;
  DAT_0139ffc0 = FUN_00173a70;
  local_20 = DAT_0139ffc8;
  DAT_0139ffc8 = packethandlers::WorldData::WORLDLIST_FETCH_REPLY;
  local_38 = _DAT_0139ffb0;
  uStack_30._0_4_ = (undefined4)uRam000000000139ffb8;
  uStack_30._4_4_ = uRam000000000139ffb8._4_4_;
  uRam000000000139ffb8 = uVar1;
  _DAT_0139ffb0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0a80;
  bVar10 = DAT_013a0a80 != (code *)0x0;
  local_28 = DAT_013a0a80;
  DAT_013a0a80 = FUN_00173a40;
  local_20 = DAT_013a0a88;
  DAT_013a0a88 = packethandlers::Interfaces::IF_OPENSUB_thunk;
  local_38 = CONCAT44(uRam00000000013a0a74,_DAT_013a0a70);
  uStack_30._0_4_ = uRam00000000013a0a78;
  uStack_30._4_4_ = uRam00000000013a0a7c;
  _DAT_013a0a70 = uVar3;
  uRam00000000013a0a74 = uVar5;
  uRam00000000013a0a78 = uVar4;
  uRam00000000013a0a7c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0b00;
  bVar10 = DAT_013a0b00 != (code *)0x0;
  local_28 = DAT_013a0b00;
  DAT_013a0b00 = FUN_00173a10;
  local_20 = DAT_013a0b08;
  DAT_013a0b08 = packethandlers::Misc::LOGOUT_TRANSFER;
  local_38 = CONCAT44(uRam00000000013a0af4,_DAT_013a0af0);
  uStack_30._0_4_ = uRam00000000013a0af8;
  uStack_30._4_4_ = uRam00000000013a0afc;
  _DAT_013a0af0 = uVar3;
  uRam00000000013a0af4 = uVar5;
  uRam00000000013a0af8 = uVar4;
  uRam00000000013a0afc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0680;
  bVar10 = DAT_013a0680 != (code *)0x0;
  local_28 = DAT_013a0680;
  DAT_013a0680 = FUN_001739e0;
  local_20 = DAT_013a0688;
  DAT_013a0688 = packethandlers::Misc::SERVER_TICK_END;
  local_38 = CONCAT44(uRam00000000013a0674,_DAT_013a0670);
  uStack_30._0_4_ = uRam00000000013a0678;
  uStack_30._4_4_ = uRam00000000013a067c;
  _DAT_013a0670 = uVar3;
  uRam00000000013a0674 = uVar5;
  uRam00000000013a0678 = uVar4;
  uRam00000000013a067c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  pcVar2 = DAT_013a0340;
  bVar10 = DAT_013a0340 != (code *)0x0;
  local_28 = DAT_013a0340;
  DAT_013a0340 = FUN_00173950;
  local_38 = CONCAT44(uRam00000000013a0334,_DAT_013a0330);
  uVar1 = CONCAT44(uRam00000000013a033c,uRam00000000013a0338);
  local_20 = DAT_013a0348;
  DAT_013a0348 = packethandlers::Misc::SKIP_DATA;
  _DAT_013a0330 = uVar3;
  uRam00000000013a0334 = uVar5;
  uRam00000000013a0338 = (undefined4)uStack_30;
  uRam00000000013a033c = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1700;
  bVar10 = DAT_013a1700 != (code *)0x0;
  local_28 = DAT_013a1700;
  DAT_013a1700 = FUN_00173920;
  local_20 = DAT_013a1708;
  DAT_013a1708 = NPCList::ProcessNpcInfo;
  local_38 = _DAT_013a16f0;
  uStack_30 = uRam00000000013a16f8;
  uRam00000000013a16f8 = uVar1;
  _DAT_013a16f0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a07c0;
  bVar10 = DAT_013a07c0 != (code *)0x0;
  local_28 = DAT_013a07c0;
  DAT_013a07c0 = FUN_001738f0;
  local_20 = DAT_013a07c8;
  DAT_013a07c8 = FUN_001d7860;
  local_38 = _DAT_013a07b0;
  uStack_30 = uRam00000000013a07b8;
  uRam00000000013a07b8 = uVar1;
  _DAT_013a07b0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0c00;
  bVar10 = DAT_013a0c00 != (code *)0x0;
  local_28 = DAT_013a0c00;
  DAT_013a0c00 = FUN_001738c0;
  local_20 = DAT_013a0c08;
  DAT_013a0c08 = packethandlers::NPCInfo::NPC_SAY;
  local_38 = _DAT_013a0bf0;
  uStack_30 = uRam00000000013a0bf8;
  uRam00000000013a0bf8 = uVar1;
  _DAT_013a0bf0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0180;
  bVar10 = DAT_013a0180 != (code *)0x0;
  local_28 = DAT_013a0180;
  DAT_013a0180 = FUN_00173890;
  local_20 = DAT_013a0188;
  DAT_013a0188 = packethandlers::NPCInfo::NPC_INFO_thunk_worldentity;
  local_38 = _DAT_013a0170;
  uStack_30._0_4_ = (undefined4)uRam00000000013a0178;
  uStack_30._4_4_ = uRam00000000013a0178._4_4_;
  uRam00000000013a0178 = uVar1;
  _DAT_013a0170 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0640;
  bVar10 = DAT_013a0640 != (code *)0x0;
  local_28 = DAT_013a0640;
  DAT_013a0640 = FUN_00173860;
  local_20 = DAT_013a0648;
  DAT_013a0648 = packethandlers::ZoneUpdates::UNKNOWN_op173_handler;
  local_38 = CONCAT44(uRam00000000013a0634,_DAT_013a0630);
  uStack_30._0_4_ = uRam00000000013a0638;
  uStack_30._4_4_ = uRam00000000013a063c;
  _DAT_013a0630 = uVar3;
  uRam00000000013a0634 = uVar5;
  uRam00000000013a0638 = uVar4;
  uRam00000000013a063c = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a01c0;
  bVar10 = DAT_013a01c0 != (code *)0x0;
  local_28 = DAT_013a01c0;
  DAT_013a01c0 = FUN_00173830;
  local_20 = DAT_013a01c8;
  DAT_013a01c8 = packethandlers::Inventory::UPDATE_INV_GROUP;
  local_38 = CONCAT44(uRam00000000013a01b4,_DAT_013a01b0);
  uStack_30._0_4_ = uRam00000000013a01b8;
  uStack_30._4_4_ = uRam00000000013a01bc;
  _DAT_013a01b0 = uVar3;
  uRam00000000013a01b4 = uVar5;
  uRam00000000013a01b8 = uVar4;
  uRam00000000013a01bc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  packethandlers::PlayerList::BindHandlers(param_2);
  uVar6 = uStack_30._4_4_;
  uVar4 = (undefined4)uStack_30;
  pcVar2 = DAT_013a0800;
  bVar10 = DAT_013a0800 != (code *)0x0;
  local_28 = DAT_013a0800;
  DAT_013a0800 = FUN_000efee0;
  local_20 = DAT_013a0808;
  DAT_013a0808 = FUN_000ec3a0;
  local_38 = CONCAT44(uRam00000000013a07f4,_DAT_013a07f0);
  uStack_30._0_4_ = uRam00000000013a07f8;
  uStack_30._4_4_ = uRam00000000013a07fc;
  _DAT_013a07f0 = uVar3;
  uRam00000000013a07f4 = uVar5;
  uRam00000000013a07f8 = uVar4;
  uRam00000000013a07fc = uVar6;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  packethandlers::ClientState::BindHandlers_extra(param_2);
  packethandlers::ClientState::BindHandlers(param_2);
  pcVar2 = DAT_013a1680;
  bVar10 = DAT_013a1680 != (code *)0x0;
  local_28 = DAT_013a1680;
  DAT_013a1680 = FUN_000ef230;
  local_38 = CONCAT44(uRam00000000013a1674,_DAT_013a1670);
  uVar1 = CONCAT44(uRam00000000013a167c,uRam00000000013a1678);
  local_20 = DAT_013a1688;
  DAT_013a1688 = packethandlers::Misc::NO_TIMEOUT;
  _DAT_013a1670 = uVar3;
  uRam00000000013a1674 = uVar5;
  uRam00000000013a1678 = (undefined4)uStack_30;
  uRam00000000013a167c = uStack_30._4_4_;
  uStack_30 = uVar1;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a1b00;
  bVar10 = DAT_013a1b00 != (code *)0x0;
  local_28 = DAT_013a1b00;
  DAT_013a1b00 = FUN_000ef200;
  local_20 = DAT_013a1b08;
  DAT_013a1b08 = FUN_000f7080;
  local_38 = _DAT_013a1af0;
  uStack_30 = uRam00000000013a1af8;
  uRam00000000013a1af8 = uVar1;
  _DAT_013a1af0 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  uVar1 = uStack_30;
  pcVar2 = DAT_013a0480;
  bVar10 = DAT_013a0480 != (code *)0x0;
  local_28 = DAT_013a0480;
  DAT_013a0480 = FUN_000ef1d0;
  local_20 = DAT_013a0488;
  DAT_013a0488 = FUN_000f7090;
  local_38 = _DAT_013a0470;
  uStack_30 = uRam00000000013a0478;
  uRam00000000013a0478 = uVar1;
  _DAT_013a0470 = param_2;
  if (bVar10) {
    (*pcVar2)(&local_38,&local_38,3);
  }
  packethandlers::ZoneUpdates::BindHandlers(param_2);
  param_1[7] = "EVP_PKEY_id" + DAT_015bf228 + 10;
  return;
}


```

## DECOMP `BindHandlers` @ 000aa85e
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers(undefined8 thisPtr) */

void jag::packethandlers::ClientState::BindHandlers(undefined8 thisPtr)

{
  undefined8 uVar1;
  undefined8 uVar2;
  undefined8 uVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  undefined8 uVar8;
  undefined8 uVar9;
  code *pcVar10;
  undefined4 uVar11;
  undefined4 uVar12;
  bool bVar13;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* jag::packethandlers::ClientState::BindHandlers — binds 18
                       ClientState/Variables/StatTable subsystem handlers into ServerProt entries.
                       Each binding writes manager redirector at entry+0x20 and invoke (actual
                       packet handler) at entry+0x28. Data-slot addresses unchanged from 948-2-2
                       (first slot 0x015c1720). Bindings (entry -> opcode):
                       0x015c1720->op5(RESET_ALL_VARPS), 0x015c16c0->op61,
                       0x015c1680->op28(VARP_LARGE), 0x015c1640->op147,
                       0x015c1600->op10(VARP_SMALL), 0x015c15c0->op51(VARP_BIT_LARGE),
                       0x015c1580->op47(CLIENT_SETVARC_SMALL),
                       0x015c1540->op64(CLIENT_SETVARC_LARGE), 0x015c1500->op196, 0x015c14c0->op48,
                       0x015c1480->op69, 0x015c1440->op92(SET_VARC_STR_SMALL), 0x015c1400->op116,
                       0x015c13c0->op190(CLEAR_PENDING_UPDATES),
                       0x015c1380->op55(DESTROY_ZONE_DATA), 0x015c1340->op58(RESET_CLIENT_STATE),
                       0x015c1300->op24(UPDATE_ZONE_PARTIAL),
                       0x015c12c0->op44(StatTable::UpdateStat). Called from main
                       ServerProt::BindHandlers. Address unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c1720;
  DAT_015c1720 = FUN_000ef7a0;
  uVar11 = (undefined4)uStack_20;
  uVar12 = uStack_20._4_4_;
  local_10 = DAT_015c1728;
  DAT_015c1728 = RESET_ALL_VARPS;
  local_28 = CONCAT44(DAT_015c1710_4,_DAT_015c1710);
  uStack_20._0_4_ = uRam00000000015c1718;
  uStack_20._4_4_ = uRam00000000015c171c;
  uRam00000000015c1718 = uVar11;
  uRam00000000015c171c = uVar12;
  _DAT_015c1710 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c16e0;
  bVar13 = DAT_015c16e0 != (code *)0x0;
  local_18 = DAT_015c16e0;
  DAT_015c16e0 = FUN_000ef770;
  local_10 = DAT_015c16e8;
  DAT_015c16e8 = VARP_SMALL;
  local_28 = CONCAT44(DAT_015c16d0_4,_DAT_015c16d0);
  uStack_20._0_4_ = uRam00000000015c16d8;
  uStack_20._4_4_ = uRam00000000015c16dc;
  uRam00000000015c16d8 = uVar11;
  uRam00000000015c16dc = uVar12;
  _DAT_015c16d0 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c16a0;
  bVar13 = DAT_015c16a0 != (code *)0x0;
  local_18 = DAT_015c16a0;
  DAT_015c16a0 = FUN_000ef740;
  local_10 = DAT_015c16a8;
  DAT_015c16a8 = VARP_LARGE;
  local_28 = CONCAT44(DAT_015c1690_4,_DAT_015c1690);
  uStack_20._0_4_ = uRam00000000015c1698;
  uStack_20._4_4_ = uRam00000000015c169c;
  uRam00000000015c1698 = uVar11;
  uRam00000000015c169c = uVar12;
  _DAT_015c1690 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c1660;
  bVar13 = DAT_015c1660 != (code *)0x0;
  local_18 = DAT_015c1660;
  DAT_015c1660 = FUN_000ef710;
  local_10 = DAT_015c1668;
  DAT_015c1668 = VARP_LONG;
  local_28 = CONCAT44(DAT_015c1650_4,_DAT_015c1650);
  uStack_20 = CONCAT44(uRam00000000015c165c,uRam00000000015c1658);
  uRam00000000015c1658 = uVar11;
  uRam00000000015c165c = uVar12;
  _DAT_015c1650 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c1620;
  bVar13 = DAT_015c1620 != (code *)0x0;
  local_18 = DAT_015c1620;
  DAT_015c1620 = FUN_000ef6e0;
  local_10 = DAT_015c1628;
  DAT_015c1628 = VARP_BIT_SMALL;
  local_28 = _DAT_015c1610;
  uStack_20 = uRam00000000015c1618;
  uRam00000000015c1618 = uVar1;
  _DAT_015c1610 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c15e0;
  bVar13 = DAT_015c15e0 != (code *)0x0;
  local_18 = DAT_015c15e0;
  DAT_015c15e0 = FUN_000ef6b0;
  local_10 = DAT_015c15e8;
  DAT_015c15e8 = VARP_BIT_LARGE;
  local_28 = _DAT_015c15d0;
  uStack_20 = uRam00000000015c15d8;
  uRam00000000015c15d8 = uVar1;
  _DAT_015c15d0 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c15a0;
  bVar13 = DAT_015c15a0 != (code *)0x0;
  local_18 = DAT_015c15a0;
  DAT_015c15a0 = FUN_000ef680;
  local_10 = DAT_015c15a8;
  DAT_015c15a8 = CLIENT_SETVARC_SMALL;
  local_28 = _DAT_015c1590;
  uStack_20 = uRam00000000015c1598;
  uRam00000000015c1598 = uVar1;
  _DAT_015c1590 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c1560;
  bVar13 = DAT_015c1560 != (code *)0x0;
  local_18 = DAT_015c1560;
  DAT_015c1560 = FUN_000ef650;
  local_10 = DAT_015c1568;
  DAT_015c1568 = CLIENT_SETVARC_LARGE;
  local_28 = _DAT_015c1550;
  uStack_20 = uRam00000000015c1558;
  uRam00000000015c1558 = uVar1;
  _DAT_015c1550 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  pcVar10 = DAT_015c1520;
  uVar11 = (undefined4)uStack_20;
  uVar12 = uStack_20._4_4_;
  bVar13 = DAT_015c1520 != (code *)0x0;
  local_18 = DAT_015c1520;
  DAT_015c1520 = FUN_000ef620;
  local_10 = DAT_015c1528;
  DAT_015c1528 = CLIENT_SETVARC_LONG;
  local_28 = CONCAT44(DAT_015c1510_4,_DAT_015c1510);
  uStack_20._0_4_ = uRam00000000015c1518;
  uStack_20._4_4_ = uRam00000000015c151c;
  uRam00000000015c1518 = uVar11;
  uRam00000000015c151c = uVar12;
  _DAT_015c1510 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c14e0;
  bVar13 = DAT_015c14e0 != (code *)0x0;
  local_18 = DAT_015c14e0;
  DAT_015c14e0 = FUN_000ef5f0;
  local_10 = DAT_015c14e8;
  DAT_015c14e8 = CLIENT_SETVARCBIT_SMALL;
  local_28 = CONCAT44(DAT_015c14d0_4,_DAT_015c14d0);
  uStack_20._0_4_ = uRam00000000015c14d8;
  uStack_20._4_4_ = uRam00000000015c14dc;
  uRam00000000015c14d8 = uVar11;
  uRam00000000015c14dc = uVar12;
  _DAT_015c14d0 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c14a0;
  bVar13 = DAT_015c14a0 != (code *)0x0;
  local_18 = DAT_015c14a0;
  DAT_015c14a0 = FUN_000ef5c0;
  local_10 = DAT_015c14a8;
  DAT_015c14a8 = CLIENT_SETVARCBIT_LARGE;
  local_28 = CONCAT44(DAT_015c1490_4,_DAT_015c1490);
  uStack_20._0_4_ = uRam00000000015c1498;
  uStack_20._4_4_ = uRam00000000015c149c;
  uRam00000000015c1498 = uVar11;
  uRam00000000015c149c = uVar12;
  _DAT_015c1490 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c1460;
  bVar13 = DAT_015c1460 != (code *)0x0;
  local_18 = DAT_015c1460;
  DAT_015c1460 = FUN_000ef590;
  local_10 = DAT_015c1468;
  DAT_015c1468 = Misc::SET_VARC_STR_SMALL;
  local_28 = CONCAT44(DAT_015c1450_4,_DAT_015c1450);
  uStack_20 = CONCAT44(uRam00000000015c145c,uRam00000000015c1458);
  uRam00000000015c1458 = uVar11;
  uRam00000000015c145c = uVar12;
  _DAT_015c1450 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c1420;
  bVar13 = DAT_015c1420 != (code *)0x0;
  local_18 = DAT_015c1420;
  DAT_015c1420 = FUN_000ef560;
  local_10 = DAT_015c1428;
  DAT_015c1428 = SET_VARC_STR_LARGE;
  local_28 = _DAT_015c1410;
  uStack_20 = uRam00000000015c1418;
  uRam00000000015c1418 = uVar1;
  _DAT_015c1410 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c13e0;
  bVar13 = DAT_015c13e0 != (code *)0x0;
  local_18 = DAT_015c13e0;
  DAT_015c13e0 = FUN_000ef530;
  local_10 = DAT_015c13e8;
  DAT_015c13e8 = CLEAR_PENDING_UPDATES;
  local_28 = _DAT_015c13d0;
  uStack_20 = uRam00000000015c13d8;
  uRam00000000015c13d8 = uVar1;
  _DAT_015c13d0 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c13a0;
  bVar13 = DAT_015c13a0 != (code *)0x0;
  local_18 = DAT_015c13a0;
  DAT_015c13a0 = FUN_000ef4b0;
  local_10 = DAT_015c13a8;
  DAT_015c13a8 = DESTROY_ZONE_DATA;
  local_28 = _DAT_015c1390;
  uStack_20 = uRam00000000015c1398;
  uRam00000000015c1398 = uVar1;
  _DAT_015c1390 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar10 = DAT_015c1360;
  bVar13 = DAT_015c1360 != (code *)0x0;
  local_18 = DAT_015c1360;
  DAT_015c1360 = FUN_000ef480;
  local_10 = DAT_015c1368;
  DAT_015c1368 = RESET_CLIENT_STATE;
  local_28 = _DAT_015c1350;
  uStack_20 = uRam00000000015c1358;
  uRam00000000015c1358 = uVar1;
  _DAT_015c1350 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  pcVar10 = DAT_015c1320;
  uVar11 = (undefined4)uStack_20;
  uVar12 = uStack_20._4_4_;
  bVar13 = DAT_015c1320 != (code *)0x0;
  local_18 = DAT_015c1320;
  DAT_015c1320 = FUN_000ef450;
  local_10 = DAT_015c1328;
  DAT_015c1328 = UPDATE_ZONE_PARTIAL;
  local_28 = CONCAT44(DAT_015c1310_4,_DAT_015c1310);
  uStack_20._0_4_ = uRam00000000015c1318;
  uStack_20._4_4_ = uRam00000000015c131c;
  uRam00000000015c1318 = uVar11;
  uRam00000000015c131c = uVar12;
  _DAT_015c1310 = thisPtr;
  if (bVar13) {
    (*pcVar10)(&local_28,&local_28,3);
  }
  uVar12 = uStack_20._4_4_;
  uVar11 = (undefined4)uStack_20;
  pcVar10 = DAT_015c12e0;
  bVar13 = DAT_015c12e0 != (code *)0x0;
  local_18 = DAT_015c12e0;
  DAT_015c12e0 = FUN_000ef270;
  local_10 = DAT_015c12e8;
  DAT_015c12e8 = game::StatTable::UpdateStat;
  local_28 = CONCAT44(DAT_015c12d0_4,_DAT_015c12d0);
  uStack_20 = CONCAT44(uRam00000000015c12dc,uRam00000000015c12d8);
  uRam00000000015c12d8 = uVar11;
  uRam00000000015c12dc = uVar12;
  uVar1 = _DAT_015c1710;
  uVar2 = _DAT_015c16d0;
  uVar3 = _DAT_015c1690;
  uVar4 = _DAT_015c1650;
  uVar5 = _DAT_015c1510;
  uVar6 = _DAT_015c14d0;
  uVar7 = _DAT_015c1490;
  uVar8 = _DAT_015c1450;
  uVar9 = _DAT_015c1310;
  if (bVar13) {
    _DAT_015c12d0 = thisPtr;
    (*pcVar10)(&local_28,&local_28,3);
    uVar1 = _DAT_015c1710;
    uVar2 = _DAT_015c16d0;
    uVar3 = _DAT_015c1690;
    uVar4 = _DAT_015c1650;
    uVar5 = _DAT_015c1510;
    uVar6 = _DAT_015c14d0;
    uVar7 = _DAT_015c1490;
    uVar8 = _DAT_015c1450;
    uVar9 = _DAT_015c1310;
    thisPtr = _DAT_015c12d0;
  }
  DAT_015c12d0_4 = (undefined4)((ulong)thisPtr >> 0x20);
  _DAT_015c12d0 = (undefined4)thisPtr;
  DAT_015c1310_4 = (undefined4)((ulong)uVar9 >> 0x20);
  _DAT_015c1310 = (undefined4)uVar9;
  DAT_015c1450_4 = (undefined4)((ulong)uVar8 >> 0x20);
  _DAT_015c1450 = (undefined4)uVar8;
  DAT_015c1490_4 = (undefined4)((ulong)uVar7 >> 0x20);
  _DAT_015c1490 = (undefined4)uVar7;
  DAT_015c14d0_4 = (undefined4)((ulong)uVar6 >> 0x20);
  _DAT_015c14d0 = (undefined4)uVar6;
  DAT_015c1510_4 = (undefined4)((ulong)uVar5 >> 0x20);
  _DAT_015c1510 = (undefined4)uVar5;
  DAT_015c1650_4 = (undefined4)((ulong)uVar4 >> 0x20);
  _DAT_015c1650 = (undefined4)uVar4;
  DAT_015c1690_4 = (undefined4)((ulong)uVar3 >> 0x20);
  _DAT_015c1690 = (undefined4)uVar3;
  DAT_015c16d0_4 = (undefined4)((ulong)uVar2 >> 0x20);
  _DAT_015c16d0 = (undefined4)uVar2;
  DAT_015c1710_4 = (undefined4)((ulong)uVar1 >> 0x20);
  _DAT_015c1710 = (undefined4)uVar1;
  return;
}


```

## DECOMP `BindHandlers_extra` @ 000aaf4c
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers_extra(undefined8 thisPtr) */

void jag::packethandlers::ClientState::BindHandlers_extra(undefined8 thisPtr)

{
  undefined8 uVar1;
  undefined8 uVar2;
  undefined8 uVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  code *pcVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  bool bVar11;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* ClientState extension BindHandlers — binds 12 additional handlers for
                       Rebuild/Mini-Map/Camera subsystems. Data-slot addresses unchanged from
                       948-2-2 (first slot 0x015c12a0 = redirector FUN_000efeb0). Bindings (entry ->
                       opcode): 0x015c1280->op199(REBUILD_NORMAL),
                       0x015c1240->op186(REBUILD_WORLDENTITY), 0x015c1200->op166, 0x015c11c0->op194,
                       0x015c1180->op143, 0x015c1140->op144, 0x015c1100->op201, 0x015c10c0->op207,
                       0x015c1080->op149, 0x015c1040->op146, 0x015c1000->op131, 0x015c0fc0->op171.
                       Likely jag::packethandlers::Environment::BindHandlers or similar — pending
                       identification. Address unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c12a0;
  DAT_015c12a0 = FUN_000efeb0;
  uVar9 = (undefined4)uStack_20;
  uVar10 = uStack_20._4_4_;
  local_10 = DAT_015c12a8;
  DAT_015c12a8 = REBUILD_NORMAL;
  local_28 = CONCAT44(DAT_015c1290_4,_DAT_015c1290);
  uStack_20._0_4_ = uRam00000000015c1298;
  uStack_20._4_4_ = uRam00000000015c129c;
  uRam00000000015c1298 = uVar9;
  uRam00000000015c129c = uVar10;
  _DAT_015c1290 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c1260;
  bVar11 = DAT_015c1260 != (code *)0x0;
  local_18 = DAT_015c1260;
  DAT_015c1260 = FUN_000efd50;
  local_10 = DAT_015c1268;
  DAT_015c1268 = REBUILD_WORLDENTITY;
  local_28 = CONCAT44(DAT_015c1250_4,_DAT_015c1250);
  uStack_20._0_4_ = uRam00000000015c1258;
  uStack_20._4_4_ = uRam00000000015c125c;
  uRam00000000015c1258 = uVar9;
  uRam00000000015c125c = uVar10;
  _DAT_015c1250 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c1220;
  bVar11 = DAT_015c1220 != (code *)0x0;
  local_18 = DAT_015c1220;
  DAT_015c1220 = FUN_000efd20;
  local_10 = DAT_015c1228;
  DAT_015c1228 = WORLDENTITY_ADD;
  local_28 = CONCAT44(DAT_015c1210_4,_DAT_015c1210);
  uStack_20._0_4_ = uRam00000000015c1218;
  uStack_20._4_4_ = uRam00000000015c121c;
  uRam00000000015c1218 = uVar9;
  uRam00000000015c121c = uVar10;
  _DAT_015c1210 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c11e0;
  bVar11 = DAT_015c11e0 != (code *)0x0;
  local_18 = DAT_015c11e0;
  DAT_015c11e0 = FUN_000efa50;
  local_10 = DAT_015c11e8;
  DAT_015c11e8 = FUN_000efa80;
  local_28 = CONCAT44(DAT_015c11d0_4,_DAT_015c11d0);
  uStack_20 = CONCAT44(uRam00000000015c11dc,uRam00000000015c11d8);
  uRam00000000015c11d8 = uVar9;
  uRam00000000015c11dc = uVar10;
  _DAT_015c11d0 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c11a0;
  bVar11 = DAT_015c11a0 != (code *)0x0;
  local_18 = DAT_015c11a0;
  DAT_015c11a0 = FUN_000efa20;
  local_10 = DAT_015c11a8;
  DAT_015c11a8 = FUN_00119fa0;
  local_28 = _DAT_015c1190;
  uStack_20 = uRam00000000015c1198;
  uRam00000000015c1198 = uVar1;
  _DAT_015c1190 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c1160;
  bVar11 = DAT_015c1160 != (code *)0x0;
  local_18 = DAT_015c1160;
  DAT_015c1160 = FUN_000ef9f0;
  local_10 = DAT_015c1168;
  DAT_015c1168 = FUN_00119db0;
  local_28 = _DAT_015c1150;
  uStack_20 = uRam00000000015c1158;
  uRam00000000015c1158 = uVar1;
  _DAT_015c1150 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c1120;
  bVar11 = DAT_015c1120 != (code *)0x0;
  local_18 = DAT_015c1120;
  DAT_015c1120 = FUN_000ef940;
  local_10 = DAT_015c1128;
  DAT_015c1128 = FUN_000ef970;
  local_28 = _DAT_015c1110;
  uStack_20 = uRam00000000015c1118;
  uRam00000000015c1118 = uVar1;
  _DAT_015c1110 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c10e0;
  bVar11 = DAT_015c10e0 != (code *)0x0;
  local_18 = DAT_015c10e0;
  DAT_015c10e0 = FUN_000ef910;
  local_10 = DAT_015c10e8;
  DAT_015c10e8 = FUN_00119d00;
  local_28 = _DAT_015c10d0;
  uStack_20 = uRam00000000015c10d8;
  uRam00000000015c10d8 = uVar1;
  _DAT_015c10d0 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  pcVar8 = DAT_015c10a0;
  uVar9 = (undefined4)uStack_20;
  uVar10 = uStack_20._4_4_;
  bVar11 = DAT_015c10a0 != (code *)0x0;
  local_18 = DAT_015c10a0;
  DAT_015c10a0 = FUN_000ef8e0;
  local_10 = DAT_015c10a8;
  DAT_015c10a8 = FUN_000f9a20;
  local_28 = CONCAT44(DAT_015c1090_4,_DAT_015c1090);
  uStack_20._0_4_ = uRam00000000015c1098;
  uStack_20._4_4_ = uRam00000000015c109c;
  uRam00000000015c1098 = uVar9;
  uRam00000000015c109c = uVar10;
  _DAT_015c1090 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c1060;
  bVar11 = DAT_015c1060 != (code *)0x0;
  local_18 = DAT_015c1060;
  DAT_015c1060 = FUN_000ef8b0;
  local_10 = DAT_015c1068;
  DAT_015c1068 = FUN_000f98f0;
  local_28 = CONCAT44(DAT_015c1050_4,_DAT_015c1050);
  uStack_20._0_4_ = uRam00000000015c1058;
  uStack_20._4_4_ = uRam00000000015c105c;
  uRam00000000015c1058 = uVar9;
  uRam00000000015c105c = uVar10;
  _DAT_015c1050 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c1020;
  bVar11 = DAT_015c1020 != (code *)0x0;
  local_18 = DAT_015c1020;
  DAT_015c1020 = FUN_000ef880;
  local_10 = DAT_015c1028;
  DAT_015c1028 = FUN_000f9600;
  local_28 = CONCAT44(DAT_015c1010_4,_DAT_015c1010);
  uStack_20._0_4_ = uRam00000000015c1018;
  uStack_20._4_4_ = uRam00000000015c101c;
  uRam00000000015c1018 = uVar9;
  uRam00000000015c101c = uVar10;
  _DAT_015c1010 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0fe0;
  bVar11 = DAT_015c0fe0 != (code *)0x0;
  local_18 = DAT_015c0fe0;
  DAT_015c0fe0 = FUN_000ef7d0;
  local_10 = DAT_015c0fe8;
  DAT_015c0fe8 = FUN_000ef800;
  local_28 = CONCAT44(DAT_015c0fd0_4,_DAT_015c0fd0);
  uStack_20 = CONCAT44(uRam00000000015c0fdc,uRam00000000015c0fd8);
  uRam00000000015c0fd8 = uVar9;
  uRam00000000015c0fdc = uVar10;
  uVar1 = _DAT_015c1290;
  uVar2 = _DAT_015c1250;
  uVar3 = _DAT_015c1210;
  uVar4 = _DAT_015c11d0;
  uVar5 = _DAT_015c1090;
  uVar6 = _DAT_015c1050;
  uVar7 = _DAT_015c1010;
  if (bVar11) {
    _DAT_015c0fd0 = thisPtr;
    (*pcVar8)(&local_28,&local_28,3);
    uVar1 = _DAT_015c1290;
    uVar2 = _DAT_015c1250;
    uVar3 = _DAT_015c1210;
    uVar4 = _DAT_015c11d0;
    uVar5 = _DAT_015c1090;
    uVar6 = _DAT_015c1050;
    uVar7 = _DAT_015c1010;
    thisPtr = _DAT_015c0fd0;
  }
  DAT_015c0fd0_4 = (undefined4)((ulong)thisPtr >> 0x20);
  _DAT_015c0fd0 = (undefined4)thisPtr;
  DAT_015c1010_4 = (undefined4)((ulong)uVar7 >> 0x20);
  _DAT_015c1010 = (undefined4)uVar7;
  DAT_015c1050_4 = (undefined4)((ulong)uVar6 >> 0x20);
  _DAT_015c1050 = (undefined4)uVar6;
  DAT_015c1090_4 = (undefined4)((ulong)uVar5 >> 0x20);
  _DAT_015c1090 = (undefined4)uVar5;
  DAT_015c11d0_4 = (undefined4)((ulong)uVar4 >> 0x20);
  _DAT_015c11d0 = (undefined4)uVar4;
  DAT_015c1210_4 = (undefined4)((ulong)uVar3 >> 0x20);
  _DAT_015c1210 = (undefined4)uVar3;
  DAT_015c1250_4 = (undefined4)((ulong)uVar2 >> 0x20);
  _DAT_015c1250 = (undefined4)uVar2;
  DAT_015c1290_4 = (undefined4)((ulong)uVar1 >> 0x20);
  _DAT_015c1290 = (undefined4)uVar1;
  return;
}


```

## DECOMP `BindHandlers` @ 000ab3ea
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers(undefined8 thisPtr) */

void jag::packethandlers::PlayerList::BindHandlers(undefined8 thisPtr)

{
  undefined8 uVar1;
  undefined8 uVar2;
  undefined8 uVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  code *pcVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  bool bVar11;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* PlayerList/PlayerInfo/Misc subsystem BindHandlers. Binds ~13 handlers:
                       PLAYER_INFO (op 22/0x16) at jag::PlayerList::ProcessPlayerInfo (948-5 @
                       0x1618a0), HANDSHAKE_UID (op 109/0x6d), UPDATE_PLAYER_CHAT (op 178/0xb2),
                       SET_DISPLAY_INT/JCOINS_UPDATE (op 74/0x4a), SET_PLAYER_OP_2 (op 12),
                       SET_PLAYER_OP_3 (op 13), SET_INTERACTION_FLAG_D (op 176/0xb0),
                       SET_INTERACTION_FLAG_C (op 193/0xc1), SET_SYSUPDATE_TIMER (op 184/0xb8), plus
                       FUN_ helpers. First data-slot 0x015c0fa0 (unchanged). Binding site for
                       jag::PlayerList::ProcessPlayerInfo. Address unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c0fa0;
  DAT_015c0fa0 = FUN_000f0650;
  uVar9 = (undefined4)uStack_20;
  uVar10 = uStack_20._4_4_;
  local_10 = DAT_015c0fa8;
  DAT_015c0fa8 = Misc::SET_DISPLAY_INT;
  local_28 = CONCAT44(DAT_015c0f90_4,_DAT_015c0f90);
  uStack_20._0_4_ = uRam00000000015c0f98;
  uStack_20._4_4_ = uRam00000000015c0f9c;
  uRam00000000015c0f98 = uVar9;
  uRam00000000015c0f9c = uVar10;
  _DAT_015c0f90 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0f60;
  bVar11 = DAT_015c0f60 != (code *)0x0;
  local_18 = DAT_015c0f60;
  DAT_015c0f60 = FUN_000f0620;
  local_10 = DAT_015c0f68;
  DAT_015c0f68 = jag::PlayerList::ProcessPlayerInfo;
  local_28 = CONCAT44(DAT_015c0f50_4,_DAT_015c0f50);
  uStack_20._0_4_ = uRam00000000015c0f58;
  uStack_20._4_4_ = uRam00000000015c0f5c;
  uRam00000000015c0f58 = uVar9;
  uRam00000000015c0f5c = uVar10;
  _DAT_015c0f50 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0f20;
  bVar11 = DAT_015c0f20 != (code *)0x0;
  local_18 = DAT_015c0f20;
  DAT_015c0f20 = FUN_000f05f0;
  local_10 = DAT_015c0f28;
  DAT_015c0f28 = Misc::SET_PLAYER_OP;
  local_28 = CONCAT44(DAT_015c0f10_4,_DAT_015c0f10);
  uStack_20._0_4_ = uRam00000000015c0f18;
  uStack_20._4_4_ = uRam00000000015c0f1c;
  uRam00000000015c0f18 = uVar9;
  uRam00000000015c0f1c = uVar10;
  _DAT_015c0f10 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0ee0;
  bVar11 = DAT_015c0ee0 != (code *)0x0;
  local_18 = DAT_015c0ee0;
  DAT_015c0ee0 = FUN_000f0570;
  local_10 = DAT_015c0ee8;
  DAT_015c0ee8 = Misc::SET_PLAYER_OP_3;
  local_28 = CONCAT44(DAT_015c0ed0_4,_DAT_015c0ed0);
  uStack_20 = CONCAT44(uRam00000000015c0edc,uRam00000000015c0ed8);
  uRam00000000015c0ed8 = uVar9;
  uRam00000000015c0edc = uVar10;
  _DAT_015c0ed0 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c0ea0;
  bVar11 = DAT_015c0ea0 != (code *)0x0;
  local_18 = DAT_015c0ea0;
  DAT_015c0ea0 = FUN_000f04e0;
  local_10 = DAT_015c0ea8;
  DAT_015c0ea8 = Misc::SET_PLAYER_OP_2;
  local_28 = _DAT_015c0e90;
  uStack_20 = uRam00000000015c0e98;
  uRam00000000015c0e98 = uVar1;
  _DAT_015c0e90 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c0e60;
  bVar11 = DAT_015c0e60 != (code *)0x0;
  local_18 = DAT_015c0e60;
  DAT_015c0e60 = FUN_000f04b0;
  local_10 = DAT_015c0e68;
  DAT_015c0e68 = PlayerInfo::HANDSHAKE_UID;
  local_28 = _DAT_015c0e50;
  uStack_20 = uRam00000000015c0e58;
  uRam00000000015c0e58 = uVar1;
  _DAT_015c0e50 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c0e20;
  bVar11 = DAT_015c0e20 != (code *)0x0;
  local_18 = DAT_015c0e20;
  DAT_015c0e20 = FUN_000f0430;
  local_10 = DAT_015c0e28;
  DAT_015c0e28 = Misc::SET_INTERACTION_FLAG_D;
  local_28 = _DAT_015c0e10;
  uStack_20 = uRam00000000015c0e18;
  uRam00000000015c0e18 = uVar1;
  _DAT_015c0e10 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar8 = DAT_015c0de0;
  bVar11 = DAT_015c0de0 != (code *)0x0;
  local_18 = DAT_015c0de0;
  DAT_015c0de0 = FUN_000f03c0;
  local_10 = DAT_015c0de8;
  DAT_015c0de8 = FUN_000f03f0;
  local_28 = _DAT_015c0dd0;
  uStack_20 = uRam00000000015c0dd8;
  uRam00000000015c0dd8 = uVar1;
  _DAT_015c0dd0 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  pcVar8 = DAT_015c0da0;
  uVar9 = (undefined4)uStack_20;
  uVar10 = uStack_20._4_4_;
  bVar11 = DAT_015c0da0 != (code *)0x0;
  local_18 = DAT_015c0da0;
  DAT_015c0da0 = FUN_000f0350;
  local_10 = DAT_015c0da8;
  DAT_015c0da8 = FUN_000f0380;
  local_28 = CONCAT44(DAT_015c0d90_4,_DAT_015c0d90);
  uStack_20._0_4_ = uRam00000000015c0d98;
  uStack_20._4_4_ = uRam00000000015c0d9c;
  uRam00000000015c0d98 = uVar9;
  uRam00000000015c0d9c = uVar10;
  _DAT_015c0d90 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0d60;
  bVar11 = DAT_015c0d60 != (code *)0x0;
  local_18 = DAT_015c0d60;
  DAT_015c0d60 = FUN_000f0320;
  local_10 = DAT_015c0d68;
  DAT_015c0d68 = PlayerInfo::UPDATE_PLAYER_CHAT;
  local_28 = CONCAT44(DAT_015c0d50_4,_DAT_015c0d50);
  uStack_20._0_4_ = uRam00000000015c0d58;
  uStack_20._4_4_ = uRam00000000015c0d5c;
  uRam00000000015c0d58 = uVar9;
  uRam00000000015c0d5c = uVar10;
  _DAT_015c0d50 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0d20;
  bVar11 = DAT_015c0d20 != (code *)0x0;
  local_18 = DAT_015c0d20;
  DAT_015c0d20 = FUN_000effb0;
  local_10 = DAT_015c0d28;
  DAT_015c0d28 = Misc::SET_INTERACTION_FLAG_C;
  local_28 = CONCAT44(DAT_015c0d10_4,_DAT_015c0d10);
  uStack_20._0_4_ = uRam00000000015c0d18;
  uStack_20._4_4_ = uRam00000000015c0d1c;
  uRam00000000015c0d18 = uVar9;
  uRam00000000015c0d1c = uVar10;
  _DAT_015c0d10 = thisPtr;
  if (bVar11) {
    (*pcVar8)(&local_28,&local_28,3);
  }
  uVar10 = uStack_20._4_4_;
  uVar9 = (undefined4)uStack_20;
  pcVar8 = DAT_015c0ce0;
  bVar11 = DAT_015c0ce0 != (code *)0x0;
  local_18 = DAT_015c0ce0;
  DAT_015c0ce0 = FUN_000eff10;
  local_10 = DAT_015c0ce8;
  DAT_015c0ce8 = Misc::SET_SYSUPDATE_TIMER;
  local_28 = CONCAT44(DAT_015c0cd0_4,_DAT_015c0cd0);
  uStack_20 = CONCAT44(uRam00000000015c0cdc,uRam00000000015c0cd8);
  uRam00000000015c0cd8 = uVar9;
  uRam00000000015c0cdc = uVar10;
  uVar1 = _DAT_015c0f90;
  uVar2 = _DAT_015c0f50;
  uVar3 = _DAT_015c0f10;
  uVar4 = _DAT_015c0ed0;
  uVar5 = _DAT_015c0d90;
  uVar6 = _DAT_015c0d50;
  uVar7 = _DAT_015c0d10;
  if (bVar11) {
    _DAT_015c0cd0 = thisPtr;
    (*pcVar8)(&local_28,&local_28,3);
    uVar1 = _DAT_015c0f90;
    uVar2 = _DAT_015c0f50;
    uVar3 = _DAT_015c0f10;
    uVar4 = _DAT_015c0ed0;
    uVar5 = _DAT_015c0d90;
    uVar6 = _DAT_015c0d50;
    uVar7 = _DAT_015c0d10;
    thisPtr = _DAT_015c0cd0;
  }
  DAT_015c0cd0_4 = (undefined4)((ulong)thisPtr >> 0x20);
  _DAT_015c0cd0 = (undefined4)thisPtr;
  DAT_015c0d10_4 = (undefined4)((ulong)uVar7 >> 0x20);
  _DAT_015c0d10 = (undefined4)uVar7;
  DAT_015c0d50_4 = (undefined4)((ulong)uVar6 >> 0x20);
  _DAT_015c0d50 = (undefined4)uVar6;
  DAT_015c0d90_4 = (undefined4)((ulong)uVar5 >> 0x20);
  _DAT_015c0d90 = (undefined4)uVar5;
  DAT_015c0ed0_4 = (undefined4)((ulong)uVar4 >> 0x20);
  _DAT_015c0ed0 = (undefined4)uVar4;
  DAT_015c0f10_4 = (undefined4)((ulong)uVar3 >> 0x20);
  _DAT_015c0f10 = (undefined4)uVar3;
  DAT_015c0f50_4 = (undefined4)((ulong)uVar2 >> 0x20);
  _DAT_015c0f50 = (undefined4)uVar2;
  DAT_015c0f90_4 = (undefined4)((ulong)uVar1 >> 0x20);
  _DAT_015c0f90 = (undefined4)uVar1;
  return;
}


```

## DECOMP `BindHandlers` @ 000ab888
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers(undefined8 thisPtr) */

void jag::packethandlers::Interfaces::BindHandlers(undefined8 thisPtr)

{
  undefined8 uVar1;
  undefined8 uVar2;
  undefined8 uVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  undefined8 uVar8;
  undefined8 uVar9;
  undefined8 uVar10;
  undefined8 uVar11;
  undefined8 uVar12;
  undefined8 uVar13;
  undefined8 uVar14;
  undefined8 uVar15;
  undefined8 uVar16;
  undefined8 uVar17;
  undefined8 uVar18;
  undefined8 uVar19;
  undefined8 uVar20;
  code *pcVar21;
  undefined4 uVar22;
  undefined4 uVar23;
  bool bVar24;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* jag::packethandlers::Interfaces::BindHandlers — binds the IF_* interface
                       packet handlers into ServerProt entries (first data-slot 0x015c0ca0,
                       unchanged from 948-2-2). References the IF_SET* handlers at their 948-5
                       addresses incl. IF_SETHIDE(0x194140), IF_SETANIM_ACTIVE(0x185980),
                       IF_SETOBJECT_ACTIVE(0x185ca0), IF_SETPLAYERHEAD_ACTIVE(0x1936b0). Address
                       unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c0ca0;
  DAT_015c0ca0 = FUN_00174fd0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  local_10 = DAT_015c0ca8;
  DAT_015c0ca8 = IF_OPENSUB;
  local_28 = CONCAT44(DAT_015c0c90_4,_DAT_015c0c90);
  uStack_20._0_4_ = uRam00000000015c0c98;
  uStack_20._4_4_ = uRam00000000015c0c9c;
  uRam00000000015c0c98 = uVar22;
  uRam00000000015c0c9c = uVar23;
  _DAT_015c0c90 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0c60;
  bVar24 = DAT_015c0c60 != (code *)0x0;
  local_18 = DAT_015c0c60;
  DAT_015c0c60 = FUN_00174fa0;
  local_10 = DAT_015c0c68;
  DAT_015c0c68 = IF_OPENTOP;
  local_28 = CONCAT44(DAT_015c0c50_4,_DAT_015c0c50);
  uStack_20._0_4_ = uRam00000000015c0c58;
  uStack_20._4_4_ = uRam00000000015c0c5c;
  uRam00000000015c0c58 = uVar22;
  uRam00000000015c0c5c = uVar23;
  _DAT_015c0c50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0c20;
  bVar24 = DAT_015c0c20 != (code *)0x0;
  local_18 = DAT_015c0c20;
  DAT_015c0c20 = FUN_00174f70;
  local_10 = DAT_015c0c28;
  DAT_015c0c28 = IF_SETTOPLEVELINTERFACE;
  local_28 = CONCAT44(DAT_015c0c10_4,_DAT_015c0c10);
  uStack_20._0_4_ = uRam00000000015c0c18;
  uStack_20._4_4_ = uRam00000000015c0c1c;
  uRam00000000015c0c18 = uVar22;
  uRam00000000015c0c1c = uVar23;
  _DAT_015c0c10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0be0;
  bVar24 = DAT_015c0be0 != (code *)0x0;
  local_18 = DAT_015c0be0;
  DAT_015c0be0 = FUN_00174f40;
  local_10 = DAT_015c0be8;
  DAT_015c0be8 = IF_SETPOSITION;
  local_28 = CONCAT44(DAT_015c0bd0_4,_DAT_015c0bd0);
  uStack_20 = CONCAT44(uRam00000000015c0bdc,uRam00000000015c0bd8);
  uRam00000000015c0bd8 = uVar22;
  uRam00000000015c0bdc = uVar23;
  _DAT_015c0bd0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0ba0;
  bVar24 = DAT_015c0ba0 != (code *)0x0;
  local_18 = DAT_015c0ba0;
  DAT_015c0ba0 = FUN_00174f10;
  local_10 = DAT_015c0ba8;
  DAT_015c0ba8 = IF_SETPLAYERMODEL_OTHER;
  local_28 = _DAT_015c0b90;
  uStack_20 = uRam00000000015c0b98;
  uRam00000000015c0b98 = uVar1;
  _DAT_015c0b90 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0b60;
  bVar24 = DAT_015c0b60 != (code *)0x0;
  local_18 = DAT_015c0b60;
  DAT_015c0b60 = FUN_00174ee0;
  local_10 = DAT_015c0b68;
  DAT_015c0b68 = IF_SETPLAYERMODEL_SELF;
  local_28 = _DAT_015c0b50;
  uStack_20 = uRam00000000015c0b58;
  uRam00000000015c0b58 = uVar1;
  _DAT_015c0b50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0b20;
  bVar24 = DAT_015c0b20 != (code *)0x0;
  local_18 = DAT_015c0b20;
  DAT_015c0b20 = FUN_00174eb0;
  local_10 = DAT_015c0b28;
  DAT_015c0b28 = IF_SETANGLE;
  local_28 = _DAT_015c0b10;
  uStack_20 = uRam00000000015c0b18;
  uRam00000000015c0b18 = uVar1;
  _DAT_015c0b10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0ae0;
  bVar24 = DAT_015c0ae0 != (code *)0x0;
  local_18 = DAT_015c0ae0;
  DAT_015c0ae0 = FUN_00174e80;
  local_10 = DAT_015c0ae8;
  DAT_015c0ae8 = IF_SETPLAYERMODEL_SNAPSHOT;
  local_28 = _DAT_015c0ad0;
  uStack_20 = uRam00000000015c0ad8;
  uRam00000000015c0ad8 = uVar1;
  _DAT_015c0ad0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c0aa0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c0aa0 != (code *)0x0;
  local_18 = DAT_015c0aa0;
  DAT_015c0aa0 = FUN_00174e50;
  local_10 = DAT_015c0aa8;
  DAT_015c0aa8 = IF_CLOSESUB_ACTIVE;
  local_28 = CONCAT44(DAT_015c0a90_4,_DAT_015c0a90);
  uStack_20._0_4_ = uRam00000000015c0a98;
  uStack_20._4_4_ = uRam00000000015c0a9c;
  uRam00000000015c0a98 = uVar22;
  uRam00000000015c0a9c = uVar23;
  _DAT_015c0a90 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0a60;
  bVar24 = DAT_015c0a60 != (code *)0x0;
  local_18 = DAT_015c0a60;
  DAT_015c0a60 = FUN_00174e20;
  local_10 = DAT_015c0a68;
  DAT_015c0a68 = IF_SUBSWAP;
  local_28 = CONCAT44(DAT_015c0a50_4,_DAT_015c0a50);
  uStack_20._0_4_ = uRam00000000015c0a58;
  uStack_20._4_4_ = uRam00000000015c0a5c;
  uRam00000000015c0a58 = uVar22;
  uRam00000000015c0a5c = uVar23;
  _DAT_015c0a50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0a20;
  bVar24 = DAT_015c0a20 != (code *)0x0;
  local_18 = DAT_015c0a20;
  DAT_015c0a20 = FUN_00174df0;
  local_10 = DAT_015c0a28;
  DAT_015c0a28 = IF_SETEVENTS2;
  local_28 = CONCAT44(DAT_015c0a10_4,_DAT_015c0a10);
  uStack_20._0_4_ = uRam00000000015c0a18;
  uStack_20._4_4_ = uRam00000000015c0a1c;
  uRam00000000015c0a18 = uVar22;
  uRam00000000015c0a1c = uVar23;
  _DAT_015c0a10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c09e0;
  bVar24 = DAT_015c09e0 != (code *)0x0;
  local_18 = DAT_015c09e0;
  DAT_015c09e0 = FUN_00174dc0;
  local_10 = DAT_015c09e8;
  DAT_015c09e8 = IF_SETEVENTS;
  local_28 = CONCAT44(DAT_015c09d0_4,_DAT_015c09d0);
  uStack_20 = CONCAT44(uRam00000000015c09dc,uRam00000000015c09d8);
  uRam00000000015c09d8 = uVar22;
  uRam00000000015c09dc = uVar23;
  _DAT_015c09d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c09a0;
  bVar24 = DAT_015c09a0 != (code *)0x0;
  local_18 = DAT_015c09a0;
  DAT_015c09a0 = FUN_00174d90;
  local_10 = DAT_015c09a8;
  DAT_015c09a8 = IF_SETTEXT;
  local_28 = _DAT_015c0990;
  uStack_20 = uRam00000000015c0998;
  uRam00000000015c0998 = uVar1;
  _DAT_015c0990 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0960;
  bVar24 = DAT_015c0960 != (code *)0x0;
  local_18 = DAT_015c0960;
  DAT_015c0960 = FUN_00174d60;
  local_10 = DAT_015c0968;
  DAT_015c0968 = IF_SETHIDE;
  local_28 = _DAT_015c0950;
  uStack_20 = uRam00000000015c0958;
  uRam00000000015c0958 = uVar1;
  _DAT_015c0950 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0920;
  bVar24 = DAT_015c0920 != (code *)0x0;
  local_18 = DAT_015c0920;
  DAT_015c0920 = FUN_00174d30;
  local_10 = DAT_015c0928;
  DAT_015c0928 = IF_SET2DANGLE;
  local_28 = _DAT_015c0910;
  uStack_20 = uRam00000000015c0918;
  uRam00000000015c0918 = uVar1;
  _DAT_015c0910 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c08e0;
  bVar24 = DAT_015c08e0 != (code *)0x0;
  local_18 = DAT_015c08e0;
  DAT_015c08e0 = FUN_00174d00;
  local_10 = DAT_015c08e8;
  DAT_015c08e8 = IF_SET_MODEL_FRAME;
  local_28 = _DAT_015c08d0;
  uStack_20 = uRam00000000015c08d8;
  uRam00000000015c08d8 = uVar1;
  _DAT_015c08d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c08a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c08a0 != (code *)0x0;
  local_18 = DAT_015c08a0;
  DAT_015c08a0 = FUN_00174cd0;
  local_10 = DAT_015c08a8;
  DAT_015c08a8 = IF_SETOBJECT;
  local_28 = CONCAT44(DAT_015c0890_4,_DAT_015c0890);
  uStack_20._0_4_ = uRam00000000015c0898;
  uStack_20._4_4_ = uRam00000000015c089c;
  uRam00000000015c0898 = uVar22;
  uRam00000000015c089c = uVar23;
  _DAT_015c0890 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0860;
  bVar24 = DAT_015c0860 != (code *)0x0;
  local_18 = DAT_015c0860;
  DAT_015c0860 = FUN_00174ca0;
  local_10 = DAT_015c0868;
  DAT_015c0868 = IF_SETOBJECT_ACTIVE;
  local_28 = CONCAT44(DAT_015c0850_4,_DAT_015c0850);
  uStack_20._0_4_ = uRam00000000015c0858;
  uStack_20._4_4_ = uRam00000000015c085c;
  uRam00000000015c0858 = uVar22;
  uRam00000000015c085c = uVar23;
  _DAT_015c0850 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0820;
  bVar24 = DAT_015c0820 != (code *)0x0;
  local_18 = DAT_015c0820;
  DAT_015c0820 = FUN_00174c70;
  local_10 = DAT_015c0828;
  DAT_015c0828 = IF_SETOBJECT_SMALL;
  local_28 = CONCAT44(DAT_015c0810_4,_DAT_015c0810);
  uStack_20._0_4_ = uRam00000000015c0818;
  uStack_20._4_4_ = uRam00000000015c081c;
  uRam00000000015c0818 = uVar22;
  uRam00000000015c081c = uVar23;
  _DAT_015c0810 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c07e0;
  bVar24 = DAT_015c07e0 != (code *)0x0;
  local_18 = DAT_015c07e0;
  DAT_015c07e0 = FUN_00174c40;
  local_10 = DAT_015c07e8;
  DAT_015c07e8 = IF_SETMODEL;
  local_28 = CONCAT44(DAT_015c07d0_4,_DAT_015c07d0);
  uStack_20 = CONCAT44(uRam00000000015c07dc,uRam00000000015c07d8);
  uRam00000000015c07d8 = uVar22;
  uRam00000000015c07dc = uVar23;
  _DAT_015c07d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c07a0;
  bVar24 = DAT_015c07a0 != (code *)0x0;
  local_18 = DAT_015c07a0;
  DAT_015c07a0 = FUN_00174c10;
  local_10 = DAT_015c07a8;
  DAT_015c07a8 = IF_SETGRAPHIC;
  local_28 = _DAT_015c0790;
  uStack_20 = uRam00000000015c0798;
  uRam00000000015c0798 = uVar1;
  _DAT_015c0790 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0760;
  bVar24 = DAT_015c0760 != (code *)0x0;
  local_18 = DAT_015c0760;
  DAT_015c0760 = FUN_00174be0;
  local_10 = DAT_015c0768;
  DAT_015c0768 = IF_SETCOLOUR;
  local_28 = _DAT_015c0750;
  uStack_20 = uRam00000000015c0758;
  uRam00000000015c0758 = uVar1;
  _DAT_015c0750 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0720;
  bVar24 = DAT_015c0720 != (code *)0x0;
  local_18 = DAT_015c0720;
  DAT_015c0720 = FUN_00174bb0;
  local_10 = DAT_015c0728;
  DAT_015c0728 = IF_SETANIM_ACTIVE;
  local_28 = _DAT_015c0710;
  uStack_20 = uRam00000000015c0718;
  uRam00000000015c0718 = uVar1;
  _DAT_015c0710 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c06e0;
  bVar24 = DAT_015c06e0 != (code *)0x0;
  local_18 = DAT_015c06e0;
  DAT_015c06e0 = FUN_00174b80;
  local_10 = DAT_015c06e8;
  DAT_015c06e8 = IF_SETANIM;
  local_28 = _DAT_015c06d0;
  uStack_20 = uRam00000000015c06d8;
  uRam00000000015c06d8 = uVar1;
  _DAT_015c06d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c06a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c06a0 != (code *)0x0;
  local_18 = DAT_015c06a0;
  DAT_015c06a0 = FUN_00174b50;
  local_10 = DAT_015c06a8;
  DAT_015c06a8 = IF_SETNPCHEAD;
  local_28 = CONCAT44(DAT_015c0690_4,_DAT_015c0690);
  uStack_20._0_4_ = uRam00000000015c0698;
  uStack_20._4_4_ = uRam00000000015c069c;
  uRam00000000015c0698 = uVar22;
  uRam00000000015c069c = uVar23;
  _DAT_015c0690 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0660;
  bVar24 = DAT_015c0660 != (code *)0x0;
  local_18 = DAT_015c0660;
  DAT_015c0660 = FUN_00174b20;
  local_10 = DAT_015c0668;
  DAT_015c0668 = IF_SETANIM_SMALL;
  local_28 = CONCAT44(DAT_015c0650_4,_DAT_015c0650);
  uStack_20._0_4_ = uRam00000000015c0658;
  uStack_20._4_4_ = uRam00000000015c065c;
  uRam00000000015c0658 = uVar22;
  uRam00000000015c065c = uVar23;
  _DAT_015c0650 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0620;
  bVar24 = DAT_015c0620 != (code *)0x0;
  local_18 = DAT_015c0620;
  DAT_015c0620 = FUN_00174af0;
  local_10 = DAT_015c0628;
  DAT_015c0628 = IF_SETNPCMODEL;
  local_28 = CONCAT44(DAT_015c0610_4,_DAT_015c0610);
  uStack_20._0_4_ = uRam00000000015c0618;
  uStack_20._4_4_ = uRam00000000015c061c;
  uRam00000000015c0618 = uVar22;
  uRam00000000015c061c = uVar23;
  _DAT_015c0610 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c05e0;
  bVar24 = DAT_015c05e0 != (code *)0x0;
  local_18 = DAT_015c05e0;
  DAT_015c05e0 = FUN_00174ac0;
  local_10 = DAT_015c05e8;
  DAT_015c05e8 = IF_SETMODEL_COORD;
  local_28 = CONCAT44(DAT_015c05d0_4,_DAT_015c05d0);
  uStack_20 = CONCAT44(uRam00000000015c05dc,uRam00000000015c05d8);
  uRam00000000015c05d8 = uVar22;
  uRam00000000015c05dc = uVar23;
  _DAT_015c05d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c05a0;
  bVar24 = DAT_015c05a0 != (code *)0x0;
  local_18 = DAT_015c05a0;
  DAT_015c05a0 = FUN_00174a90;
  local_10 = DAT_015c05a8;
  DAT_015c05a8 = IF_SETSPRITE;
  local_28 = _DAT_015c0590;
  uStack_20 = uRam00000000015c0598;
  uRam00000000015c0598 = uVar1;
  _DAT_015c0590 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0560;
  bVar24 = DAT_015c0560 != (code *)0x0;
  local_18 = DAT_015c0560;
  DAT_015c0560 = FUN_00174a60;
  local_10 = DAT_015c0568;
  DAT_015c0568 = IF_SETRECOL;
  local_28 = _DAT_015c0550;
  uStack_20 = uRam00000000015c0558;
  uRam00000000015c0558 = uVar1;
  _DAT_015c0550 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0520;
  bVar24 = DAT_015c0520 != (code *)0x0;
  local_18 = DAT_015c0520;
  DAT_015c0520 = FUN_00174a30;
  local_10 = DAT_015c0528;
  DAT_015c0528 = IF_SETSCROLLPOS;
  local_28 = _DAT_015c0510;
  uStack_20 = uRam00000000015c0518;
  uRam00000000015c0518 = uVar1;
  _DAT_015c0510 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c04e0;
  bVar24 = DAT_015c04e0 != (code *)0x0;
  local_18 = DAT_015c04e0;
  DAT_015c04e0 = FUN_00174a00;
  local_10 = DAT_015c04e8;
  DAT_015c04e8 = IF_SETSCROLLSIZE;
  local_28 = _DAT_015c04d0;
  uStack_20 = uRam00000000015c04d8;
  uRam00000000015c04d8 = uVar1;
  _DAT_015c04d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c04a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c04a0 != (code *)0x0;
  local_18 = DAT_015c04a0;
  DAT_015c04a0 = FUN_001749d0;
  local_10 = DAT_015c04a8;
  DAT_015c04a8 = IF_SETNPCHEAD_ACTIVE;
  local_28 = CONCAT44(DAT_015c0490_4,_DAT_015c0490);
  uStack_20._0_4_ = uRam00000000015c0498;
  uStack_20._4_4_ = uRam00000000015c049c;
  uRam00000000015c0498 = uVar22;
  uRam00000000015c049c = uVar23;
  _DAT_015c0490 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0460;
  bVar24 = DAT_015c0460 != (code *)0x0;
  local_18 = DAT_015c0460;
  DAT_015c0460 = FUN_001749a0;
  local_10 = DAT_015c0468;
  DAT_015c0468 = IF_SETPLAYERHEAD_ACTIVE;
  local_28 = CONCAT44(DAT_015c0450_4,_DAT_015c0450);
  uStack_20._0_4_ = uRam00000000015c0458;
  uStack_20._4_4_ = uRam00000000015c045c;
  uRam00000000015c0458 = uVar22;
  uRam00000000015c045c = uVar23;
  _DAT_015c0450 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0420;
  bVar24 = DAT_015c0420 != (code *)0x0;
  local_18 = DAT_015c0420;
  DAT_015c0420 = FUN_00174970;
  local_10 = DAT_015c0428;
  DAT_015c0428 = IF_TRIGGER_CLOSE;
  local_28 = CONCAT44(DAT_015c0410_4,_DAT_015c0410);
  uStack_20._0_4_ = uRam00000000015c0418;
  uStack_20._4_4_ = uRam00000000015c041c;
  uRam00000000015c0418 = uVar22;
  uRam00000000015c041c = uVar23;
  _DAT_015c0410 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c03e0;
  bVar24 = DAT_015c03e0 != (code *)0x0;
  local_18 = DAT_015c03e0;
  DAT_015c03e0 = FUN_00174940;
  local_10 = DAT_015c03e8;
  DAT_015c03e8 = IF_SETMODELORIGIN;
  local_28 = CONCAT44(DAT_015c03d0_4,_DAT_015c03d0);
  uStack_20 = CONCAT44(uRam00000000015c03dc,uRam00000000015c03d8);
  uRam00000000015c03d8 = uVar22;
  uRam00000000015c03dc = uVar23;
  _DAT_015c03d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c03a0;
  bVar24 = DAT_015c03a0 != (code *)0x0;
  local_18 = DAT_015c03a0;
  DAT_015c03a0 = FUN_00174910;
  local_10 = DAT_015c03a8;
  DAT_015c03a8 = IF_CLOSESUB_BY_ID;
  local_28 = _DAT_015c0390;
  uStack_20 = uRam00000000015c0398;
  uRam00000000015c0398 = uVar1;
  _DAT_015c0390 = thisPtr;
  uVar1 = _DAT_015c0c90;
  uVar2 = _DAT_015c0c50;
  uVar3 = _DAT_015c0c10;
  uVar4 = _DAT_015c0bd0;
  uVar5 = _DAT_015c0a90;
  uVar6 = _DAT_015c0a50;
  uVar7 = _DAT_015c0a10;
  uVar8 = _DAT_015c09d0;
  uVar9 = _DAT_015c0890;
  uVar10 = _DAT_015c0850;
  uVar11 = _DAT_015c0810;
  uVar12 = _DAT_015c07d0;
  uVar13 = _DAT_015c0690;
  uVar14 = _DAT_015c0650;
  uVar15 = _DAT_015c0610;
  uVar16 = _DAT_015c05d0;
  uVar17 = _DAT_015c0490;
  uVar18 = _DAT_015c0450;
  uVar19 = _DAT_015c0410;
  uVar20 = _DAT_015c03d0;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
    uVar1 = _DAT_015c0c90;
    uVar2 = _DAT_015c0c50;
    uVar3 = _DAT_015c0c10;
    uVar4 = _DAT_015c0bd0;
    uVar5 = _DAT_015c0a90;
    uVar6 = _DAT_015c0a50;
    uVar7 = _DAT_015c0a10;
    uVar8 = _DAT_015c09d0;
    uVar9 = _DAT_015c0890;
    uVar10 = _DAT_015c0850;
    uVar11 = _DAT_015c0810;
    uVar12 = _DAT_015c07d0;
    uVar13 = _DAT_015c0690;
    uVar14 = _DAT_015c0650;
    uVar15 = _DAT_015c0610;
    uVar16 = _DAT_015c05d0;
    uVar17 = _DAT_015c0490;
    uVar18 = _DAT_015c0450;
    uVar19 = _DAT_015c0410;
    uVar20 = _DAT_015c03d0;
  }
  DAT_015c03d0_4 = (undefined4)((ulong)uVar20 >> 0x20);
  _DAT_015c03d0 = (undefined4)uVar20;
  DAT_015c0410_4 = (undefined4)((ulong)uVar19 >> 0x20);
  _DAT_015c0410 = (undefined4)uVar19;
  DAT_015c0450_4 = (undefined4)((ulong)uVar18 >> 0x20);
  _DAT_015c0450 = (undefined4)uVar18;
  DAT_015c0490_4 = (undefined4)((ulong)uVar17 >> 0x20);
  _DAT_015c0490 = (undefined4)uVar17;
  DAT_015c05d0_4 = (undefined4)((ulong)uVar16 >> 0x20);
  _DAT_015c05d0 = (undefined4)uVar16;
  DAT_015c0610_4 = (undefined4)((ulong)uVar15 >> 0x20);
  _DAT_015c0610 = (undefined4)uVar15;
  DAT_015c0650_4 = (undefined4)((ulong)uVar14 >> 0x20);
  _DAT_015c0650 = (undefined4)uVar14;
  DAT_015c0690_4 = (undefined4)((ulong)uVar13 >> 0x20);
  _DAT_015c0690 = (undefined4)uVar13;
  DAT_015c07d0_4 = (undefined4)((ulong)uVar12 >> 0x20);
  _DAT_015c07d0 = (undefined4)uVar12;
  DAT_015c0810_4 = (undefined4)((ulong)uVar11 >> 0x20);
  _DAT_015c0810 = (undefined4)uVar11;
  DAT_015c0850_4 = (undefined4)((ulong)uVar10 >> 0x20);
  _DAT_015c0850 = (undefined4)uVar10;
  DAT_015c0890_4 = (undefined4)((ulong)uVar9 >> 0x20);
  _DAT_015c0890 = (undefined4)uVar9;
  DAT_015c09d0_4 = (undefined4)((ulong)uVar8 >> 0x20);
  _DAT_015c09d0 = (undefined4)uVar8;
  DAT_015c0a10_4 = (undefined4)((ulong)uVar7 >> 0x20);
  _DAT_015c0a10 = (undefined4)uVar7;
  DAT_015c0a50_4 = (undefined4)((ulong)uVar6 >> 0x20);
  _DAT_015c0a50 = (undefined4)uVar6;
  DAT_015c0a90_4 = (undefined4)((ulong)uVar5 >> 0x20);
  _DAT_015c0a90 = (undefined4)uVar5;
  DAT_015c0bd0_4 = (undefined4)((ulong)uVar4 >> 0x20);
  _DAT_015c0bd0 = (undefined4)uVar4;
  DAT_015c0c10_4 = (undefined4)((ulong)uVar3 >> 0x20);
  _DAT_015c0c10 = (undefined4)uVar3;
  DAT_015c0c50_4 = (undefined4)((ulong)uVar2 >> 0x20);
  _DAT_015c0c50 = (undefined4)uVar2;
  DAT_015c0c90_4 = (undefined4)((ulong)uVar1 >> 0x20);
  _DAT_015c0c90 = (undefined4)uVar1;
  return;
}


```

## DECOMP `BindHandlers` @ 000ae1a8
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers(undefined8 thisPtr) */

void jag::packethandlers::ZoneUpdates::BindHandlers(undefined8 thisPtr)

{
  code *pcVar1;
  undefined4 uVar2;
  undefined8 uVar3;
  undefined4 uVar4;
  bool bVar5;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* jag::packethandlers::ZoneUpdates::BindHandlers — binds 21 zone-update
                       handlers into ServerProt entries (first data-slot 0x015c20a0 = redirector
                       FUN_000ef120, unchanged from 948-2-2). Bindings (entry -> opcode):
                       0x015c2080->op41(UPDATE_ZONE_PARTIAL_FOLLOWS),
                       0x015c2040->op78(UPDATE_ZONE_FULL_FOLLOWS),
                       0x015c2000->op76(UPDATE_ZONE_PARTIAL_ENCLOSED), 0x015c1fc0->op90(LOC_ADD),
                       0x015c1f40->op50(LOC_CUSTOMISE), 0x015c1ec0->op16(LOC_DEL),
                       0x015c1e40->op6(LOC_PREFETCH), 0x015c1dc0->op65(MAP_PROJANIM),
                       0x015c1d40->op151(PROJANIM_SPECIFIC), 0x015c1cc0->op113(MAP_ANIM),
                       0x015c1c40->op46(OBJ_ADD), 0x015c1bc0->op107(OBJ_DEL),
                       0x015c1b40->op125(OBJ_COUNT), 0x015c1ac0->op71(OBJ_REVEAL),
                       0x015c1a40->op21(LOC_ANIM_SPECIFIC), 0x015c1980->op170(LOC_MERGE),
                       0x015c1900->op168(SOUND_AREA), 0x015c1880->op164(MAP_PROJANIM_HALT),
                       0x015c1800->op177(PROJANIM_SPECIFIC_HALT),
                       0x015c1780->op183(MAP_ANIM_SPECIFIC). Interleaved calls to a registration
                       helper (948-5 @ 0x007ead50). Called from main ServerProt::BindHandlers after
                       ClientState. Address unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c20a0;
  DAT_015c20a0 = FUN_000ef120;
  uVar2 = (undefined4)uStack_20;
  uVar4 = uStack_20._4_4_;
  local_10 = DAT_015c20a8;
  DAT_015c20a8 = UPDATE_ZONE_PARTIAL_FOLLOWS;
  local_28 = CONCAT44(DAT_015c2090_4,_DAT_015c2090);
  uStack_20._0_4_ = uRam00000000015c2098;
  uStack_20._4_4_ = uRam00000000015c209c;
  uRam00000000015c2098 = uVar2;
  uRam00000000015c209c = uVar4;
  _DAT_015c2090 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c2060;
  bVar5 = DAT_015c2060 != (code *)0x0;
  local_18 = DAT_015c2060;
  DAT_015c2060 = FUN_000ef0f0;
  local_10 = DAT_015c2068;
  DAT_015c2068 = UPDATE_ZONE_FULL_FOLLOWS;
  local_28 = CONCAT44(DAT_015c2050_4,_DAT_015c2050);
  uStack_20._0_4_ = uRam00000000015c2058;
  uStack_20._4_4_ = uRam00000000015c205c;
  uRam00000000015c2058 = uVar2;
  uRam00000000015c205c = uVar4;
  _DAT_015c2050 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c2020;
  bVar5 = DAT_015c2020 != (code *)0x0;
  local_18 = DAT_015c2020;
  DAT_015c2020 = FUN_000eef90;
  local_10 = DAT_015c2028;
  DAT_015c2028 = UPDATE_ZONE_PARTIAL_ENCLOSED;
  local_28 = CONCAT44(DAT_015c2010_4,_DAT_015c2010);
  uStack_20._0_4_ = uRam00000000015c2018;
  uStack_20._4_4_ = uRam00000000015c201c;
  uRam00000000015c2018 = uVar2;
  uRam00000000015c201c = uVar4;
  _DAT_015c2010 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1fe0;
  bVar5 = DAT_015c1fe0 != (code *)0x0;
  local_18 = DAT_015c1fe0;
  DAT_015c1fe0 = FUN_000eef60;
  local_10 = DAT_015c1fe8;
  DAT_015c1fe8 = LOC_ADD;
  local_28 = CONCAT44(DAT_015c1fd0_4,_DAT_015c1fd0);
  uStack_20 = CONCAT44(uRam00000000015c1fdc,uRam00000000015c1fd8);
  uRam00000000015c1fd8 = uVar2;
  uRam00000000015c1fdc = uVar4;
  _DAT_015c1fd0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1f90,&DAT_015c1fd0);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1f60;
  bVar5 = DAT_015c1f60 != (code *)0x0;
  local_18 = DAT_015c1f60;
  DAT_015c1f60 = FUN_000eef30;
  local_10 = DAT_015c1f68;
  DAT_015c1f68 = LOC_CUSTOMISE;
  local_28 = _DAT_015c1f50;
  uStack_20 = uRam00000000015c1f58;
  uRam00000000015c1f58 = uVar3;
  _DAT_015c1f50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1f10,&DAT_015c1f50);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1ee0;
  bVar5 = DAT_015c1ee0 != (code *)0x0;
  local_18 = DAT_015c1ee0;
  DAT_015c1ee0 = FUN_000eef00;
  local_10 = DAT_015c1ee8;
  DAT_015c1ee8 = LOC_DEL;
  local_28 = _DAT_015c1ed0;
  uStack_20 = uRam00000000015c1ed8;
  uRam00000000015c1ed8 = uVar3;
  _DAT_015c1ed0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1e90,&DAT_015c1ed0);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1e60;
  bVar5 = DAT_015c1e60 != (code *)0x0;
  local_18 = DAT_015c1e60;
  DAT_015c1e60 = FUN_000eeed0;
  local_10 = DAT_015c1e68;
  DAT_015c1e68 = LOC_PREFETCH;
  local_28 = _DAT_015c1e50;
  uStack_20 = uRam00000000015c1e58;
  uRam00000000015c1e58 = uVar3;
  _DAT_015c1e50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1e10,&DAT_015c1e50);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1de0;
  bVar5 = DAT_015c1de0 != (code *)0x0;
  local_18 = DAT_015c1de0;
  DAT_015c1de0 = FUN_000eeea0;
  local_10 = DAT_015c1de8;
  DAT_015c1de8 = MAP_PROJANIM;
  local_28 = _DAT_015c1dd0;
  uStack_20 = uRam00000000015c1dd8;
  uRam00000000015c1dd8 = uVar3;
  _DAT_015c1dd0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1d90,&DAT_015c1dd0);
  pcVar1 = DAT_015c1d60;
  uVar2 = (undefined4)uStack_20;
  uVar4 = uStack_20._4_4_;
  bVar5 = DAT_015c1d60 != (code *)0x0;
  local_18 = DAT_015c1d60;
  DAT_015c1d60 = FUN_000eee70;
  local_10 = DAT_015c1d68;
  DAT_015c1d68 = PROJANIM_SPECIFIC;
  local_28 = CONCAT44(DAT_015c1d50_4,_DAT_015c1d50);
  uStack_20._0_4_ = uRam00000000015c1d58;
  uStack_20._4_4_ = uRam00000000015c1d5c;
  uRam00000000015c1d58 = uVar2;
  uRam00000000015c1d5c = uVar4;
  _DAT_015c1d50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1d10,&DAT_015c1d50);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1ce0;
  bVar5 = DAT_015c1ce0 != (code *)0x0;
  local_18 = DAT_015c1ce0;
  DAT_015c1ce0 = FUN_000eee40;
  local_10 = DAT_015c1ce8;
  DAT_015c1ce8 = MAP_ANIM;
  local_28 = CONCAT44(DAT_015c1cd0_4,_DAT_015c1cd0);
  uStack_20._0_4_ = uRam00000000015c1cd8;
  uStack_20._4_4_ = uRam00000000015c1cdc;
  uRam00000000015c1cd8 = uVar2;
  uRam00000000015c1cdc = uVar4;
  _DAT_015c1cd0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1c90,&DAT_015c1cd0);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1c60;
  bVar5 = DAT_015c1c60 != (code *)0x0;
  local_18 = DAT_015c1c60;
  DAT_015c1c60 = FUN_000eee10;
  local_10 = DAT_015c1c68;
  DAT_015c1c68 = OBJ_ADD;
  local_28 = CONCAT44(DAT_015c1c50_4,_DAT_015c1c50);
  uStack_20._0_4_ = uRam00000000015c1c58;
  uStack_20._4_4_ = uRam00000000015c1c5c;
  uRam00000000015c1c58 = uVar2;
  uRam00000000015c1c5c = uVar4;
  _DAT_015c1c50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1c10,&DAT_015c1c50);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1be0;
  bVar5 = DAT_015c1be0 != (code *)0x0;
  local_18 = DAT_015c1be0;
  DAT_015c1be0 = FUN_000eede0;
  local_10 = DAT_015c1be8;
  DAT_015c1be8 = OBJ_DEL;
  local_28 = CONCAT44(DAT_015c1bd0_4,_DAT_015c1bd0);
  uStack_20 = CONCAT44(uRam00000000015c1bdc,uRam00000000015c1bd8);
  uRam00000000015c1bd8 = uVar2;
  uRam00000000015c1bdc = uVar4;
  _DAT_015c1bd0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1b90,&DAT_015c1bd0);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1b60;
  bVar5 = DAT_015c1b60 != (code *)0x0;
  local_18 = DAT_015c1b60;
  DAT_015c1b60 = FUN_000eedb0;
  local_10 = DAT_015c1b68;
  DAT_015c1b68 = OBJ_COUNT;
  local_28 = _DAT_015c1b50;
  uStack_20 = uRam00000000015c1b58;
  uRam00000000015c1b58 = uVar3;
  _DAT_015c1b50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1b10,&DAT_015c1b50);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1ae0;
  bVar5 = DAT_015c1ae0 != (code *)0x0;
  local_18 = DAT_015c1ae0;
  DAT_015c1ae0 = FUN_000eed80;
  local_10 = DAT_015c1ae8;
  DAT_015c1ae8 = OBJ_REVEAL;
  local_28 = _DAT_015c1ad0;
  uStack_20 = uRam00000000015c1ad8;
  uRam00000000015c1ad8 = uVar3;
  _DAT_015c1ad0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1a90,&DAT_015c1ad0);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c1a60;
  bVar5 = DAT_015c1a60 != (code *)0x0;
  local_18 = DAT_015c1a60;
  DAT_015c1a60 = FUN_000eed50;
  local_10 = DAT_015c1a68;
  DAT_015c1a68 = LOC_ANIM_SPECIFIC;
  local_28 = _DAT_015c1a50;
  uStack_20 = uRam00000000015c1a58;
  uRam00000000015c1a58 = uVar3;
  _DAT_015c1a50 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1a10,&DAT_015c1a50);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c19e0;
  bVar5 = DAT_015c19e0 != (code *)0x0;
  local_18 = DAT_015c19e0;
  DAT_015c19e0 = FUN_000eed20;
  local_10 = DAT_015c19e8;
  DAT_015c19e8 = LOC_ANIM;
  local_28 = _DAT_015c19d0;
  uStack_20 = uRam00000000015c19d8;
  uRam00000000015c19d8 = uVar3;
  _DAT_015c19d0 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  pcVar1 = DAT_015c19a0;
  uVar2 = (undefined4)uStack_20;
  uVar4 = uStack_20._4_4_;
  bVar5 = DAT_015c19a0 != (code *)0x0;
  local_18 = DAT_015c19a0;
  DAT_015c19a0 = FUN_000eeb70;
  local_10 = DAT_015c19a8;
  DAT_015c19a8 = LOC_MERGE;
  local_28 = CONCAT44(DAT_015c1990_4,_DAT_015c1990);
  uStack_20._0_4_ = uRam00000000015c1998;
  uStack_20._4_4_ = uRam00000000015c199c;
  uRam00000000015c1998 = uVar2;
  uRam00000000015c199c = uVar4;
  _DAT_015c1990 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1950,&DAT_015c1990);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1920;
  bVar5 = DAT_015c1920 != (code *)0x0;
  local_18 = DAT_015c1920;
  DAT_015c1920 = FUN_000eeb40;
  local_10 = DAT_015c1928;
  DAT_015c1928 = SOUND_AREA;
  local_28 = CONCAT44(DAT_015c1910_4,_DAT_015c1910);
  uStack_20._0_4_ = uRam00000000015c1918;
  uStack_20._4_4_ = uRam00000000015c191c;
  uRam00000000015c1918 = uVar2;
  uRam00000000015c191c = uVar4;
  _DAT_015c1910 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c18d0,&DAT_015c1990);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c18a0;
  bVar5 = DAT_015c18a0 != (code *)0x0;
  local_18 = DAT_015c18a0;
  DAT_015c18a0 = FUN_000eeb10;
  local_10 = DAT_015c18a8;
  DAT_015c18a8 = MAP_PROJANIM_HALT;
  local_28 = CONCAT44(DAT_015c1890_4,_DAT_015c1890);
  uStack_20._0_4_ = uRam00000000015c1898;
  uStack_20._4_4_ = uRam00000000015c189c;
  uRam00000000015c1898 = uVar2;
  uRam00000000015c189c = uVar4;
  _DAT_015c1890 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1850,&DAT_015c1890);
  uVar4 = uStack_20._4_4_;
  uVar2 = (undefined4)uStack_20;
  pcVar1 = DAT_015c1820;
  bVar5 = DAT_015c1820 != (code *)0x0;
  local_18 = DAT_015c1820;
  DAT_015c1820 = FUN_000eeae0;
  local_10 = DAT_015c1828;
  DAT_015c1828 = PROJANIM_SPECIFIC_HALT;
  local_28 = CONCAT44(DAT_015c1810_4,_DAT_015c1810);
  uStack_20 = CONCAT44(uRam00000000015c181c,uRam00000000015c1818);
  uRam00000000015c1818 = uVar2;
  uRam00000000015c181c = uVar4;
  _DAT_015c1810 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c17d0,&DAT_015c1810);
  uVar3 = uStack_20;
  pcVar1 = DAT_015c17a0;
  bVar5 = DAT_015c17a0 != (code *)0x0;
  local_18 = DAT_015c17a0;
  DAT_015c17a0 = FUN_000eeab0;
  local_10 = DAT_015c17a8;
  DAT_015c17a8 = MAP_ANIM_SPECIFIC;
  local_28 = _DAT_015c1790;
  uStack_20 = uRam00000000015c1798;
  uRam00000000015c1798 = uVar3;
  _DAT_015c1790 = thisPtr;
  if (bVar5) {
    (*pcVar1)(&local_28,&local_28,3);
  }
  FUN_007ead50(&DAT_015c1750,&DAT_015c1790);
  return;
}


```
