# Handler decompilations from the binary (headless RE)


## `jag::packethandlers::PlayerList::BindHandlers` @ 000ab3ea
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

## `jag::packethandlers::PlayerInfo::HANDSHAKE_UID` @ 00133860
```c

undefined * jag::packethandlers::PlayerInfo::HANDSHAKE_UID(long *param_1,long param_2)

{
  byte *pbVar1;
  long lVar2;
  undefined8 uVar3;
  uint uVar4;
  byte *pbVar5;
  byte *pbVar6;
  undefined1 *puVar7;
  undefined1 *puVar8;
  undefined1 *puVar9;
  uint uVar10;
  bool bVar11;
  undefined1 local_58;
  undefined7 uStack_57;
  long local_50;
  char local_41;
  
  pbVar1 = *(byte **)(param_2 + 0x10);
  uVar4 = 0xffffffff;
  pbVar5 = pbVar1;
  do {
    pbVar6 = pbVar5 + 8;
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((*pbVar5 ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[1] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[2] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[3] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[4] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[5] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[6] ^ uVar4) & 0xff) * 4);
    uVar4 = uVar4 >> 8 ^ *(uint *)(&DAT_00ffcaa0 + (ulong)((pbVar5[7] ^ uVar4) & 0xff) * 4);
    pbVar5 = pbVar6;
  } while (pbVar6 != pbVar1 + 0x18);
  bVar11 = DAT_01050dc0 == 0x3020100;
  *(undefined8 *)(param_2 + 0x18) = 0x1c;
  uVar10 = *(uint *)(pbVar1 + 0x18);
  if (bVar11) {
    uVar10 = uVar10 >> 0x18 | (uVar10 & 0xff0000) >> 8 | (uVar10 & 0xff00) << 8 | uVar10 << 0x18;
  }
  if (~uVar4 != uVar10) {
    return &DAT_015d3620;
  }
  puVar7 = &local_58;
  lVar2 = *param_1;
  *(undefined8 *)(param_2 + 0x18) = 0;
  uVar3 = *(undefined8 *)(pbVar1 + 8);
  local_58 = 0;
  lVar2 = *(long *)((long)&__DT_RELA[0xd3d].r_addend + lVar2);
  *(undefined8 *)(lVar2 + 0xa8) = *(undefined8 *)pbVar1;
  *(undefined8 *)(lVar2 + 0xb0) = uVar3;
  *(undefined8 *)(lVar2 + 0xb8) = *(undefined8 *)(pbVar1 + 0x10);
  *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 0x18;
  local_41 = '\x17';
  FUN_001335d0(&local_58,0x31);
  puVar8 = (undefined1 *)(lVar2 + 0xa8);
  do {
    puVar9 = puVar8 + 8;
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,*puVar8);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[1]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[2]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[3]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[4]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[5]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[6]);
    FUN_00ae1bd0(&local_58,&DAT_0100dd55,puVar8[7]);
    puVar8 = puVar9;
  } while (puVar9 != (undefined1 *)(lVar2 + 0xc0));
  if (local_41 < '\0') {
    puVar7 = (undefined1 *)CONCAT71(uStack_57,local_58);
  }
  else {
    local_50 = 0x17 - (long)local_41;
  }
  FUN_00a84fc0(lVar2,"uid",puVar7,local_50);
  if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    eastl__basic_string();
  }
  *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 4;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::PlayerInfo::UPDATE_PLAYER_CHAT` @ 001583c0
```c

undefined * jag::packethandlers::PlayerInfo::UPDATE_PLAYER_CHAT(long *param_1,long param_2)

{
  byte bVar1;
  char cVar2;
  undefined8 uVar3;
  long lVar4;
  undefined *puVar5;
  undefined8 local_e8;
  undefined8 local_e0;
  undefined4 local_d8;
  long local_d0;
  undefined8 local_c8;
  long local_b8;
  long local_b0;
  long local_a0;
  long local_98;
  long local_88;
  long local_80;
  long local_70;
  long local_68;
  undefined8 local_60;
  long local_58;
  undefined1 local_50 [8];
  long local_48;
  void *local_40;
  undefined8 local_38;
  undefined4 local_30;
  undefined4 local_2c;
  int local_28;
  
  lVar4 = *(long *)(param_2 + 0x18);
  uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + *param_1);
  *(long *)(param_2 + 0x18) = lVar4 + 1;
  bVar1 = *(byte *)(*(long *)(param_2 + 0x10) + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 2;
  cVar2 = *(char *)(*(long *)(param_2 + 0x10) + 1 + lVar4);
  lVar4 = FUN_00157ff0(uVar3,-2 - (uint)bVar1,1);
  local_58 = *(long *)(lVar4 + 8);
  puVar5 = &DAT_015d3600;
  if (local_58 != 0) {
    local_38 = *(undefined8 *)(param_2 + 0x18);
    local_60 = *(undefined8 *)((long)&__DT_SYMTAB[0x8d].st_value + local_58);
    local_e8 = 0x80000000100;
    local_e0 = 0xffffffff00000000;
    local_d8 = 0xffffffff;
    local_d0 = 0;
    local_c8 = 0;
    local_b8 = 0;
    local_b0 = 0;
    local_a0 = 0;
    local_98 = 0;
    local_88 = 0;
    local_80 = 0;
    local_70 = 0;
    local_68 = 0;
    local_48 = 0;
    local_40 = (void *)0x0;
    FUN_00c45fe0(local_50,*(undefined8 *)(param_2 + 8));
    memcpy(local_40,*(void **)(param_2 + 0x10),*(size_t *)(param_2 + 8));
    local_2c = 0xffffffff;
    local_30 = (undefined4)*(undefined8 *)(param_2 + 0x18);
    local_28 = (int)cVar2;
    FUN_00141b40(&local_e8);
    FUN_001418e0(&local_e8);
    if ((local_48 != 0) && (local_40 != (void *)0x0)) {
      eastl__basic_string();
    }
    if ((local_70 != 0) && (local_68 != 0)) {
      eastl__basic_string();
    }
    if ((local_88 != 0) && (local_80 != 0)) {
      eastl__basic_string();
    }
    if (((local_98 != 0) && (FUN_00964000(local_98,local_a0), local_98 != 0)) && (local_a0 != 0)) {
      eastl__basic_string();
    }
    if ((local_b8 != 0) && (local_b0 != 0)) {
      eastl__basic_string();
    }
    if (local_d0 != 0) {
      ref_counter_base::DecRef();
    }
    puVar5 = &DAT_015d3620;
  }
  return puVar5;
}


```

## `jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE` @ 00183ed0
```c

undefined * jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE(long *param_1,long param_2)

{
  long *plVar1;
  uint *puVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long lVar6;
  ushort uVar7;
  ushort uVar8;
  undefined *puVar9;
  uint uVar10;
  uint uVar11;
  int iVar12;
  long *plVar13;
  uint uVar14;
  uint uVar15;
  float fVar16;
  uint local_70;
  uint local_58;
  float local_54;
  float local_50;
  uint local_4c;
  long local_48;
  long local_40;
  
  lVar5 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  bVar3 = *(byte *)(*(long *)(param_2 + 0x10) + lVar5);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf9].r_offset + *param_1);
  uVar15 = (uint)(bVar3 >> 5);
  uVar10 = bVar3 & 0x1f;
  lVar5 = (ulong)uVar15 * 8;
  plVar1 = (long *)(lVar6 + 0x10 + lVar5);
  plVar13 = (long *)*plVar1;
  if (plVar13 != (long *)0x0) {
    if (*(code **)(*plVar13 + 0xe0) == FUN_00a861b0) {
      *plVar1 = 0;
    }
    else {
      (**(code **)(*plVar13 + 0xe0))();
      plVar13 = (long *)*plVar1;
      *plVar1 = 0;
      if (plVar13 == (long *)0x0) goto LAB_00183f56;
    }
    (**(code **)(*plVar13 + 8))();
  }
LAB_00183f56:
  plVar1 = (long *)(lVar6 + 0x50 + lVar5);
  plVar13 = (long *)*plVar1;
  if (plVar13 != (long *)0x0) {
    if (*(code **)(*plVar13 + 0xe0) == HintArrowPointer::Destruct) {
      lVar5 = plVar13[0xd];
      plVar13[0xe] = 0;
      plVar13[0xd] = 0;
      if (lVar5 != 0) {
        ref_counter_base::DecRef();
      }
      graphics::GraphNode::Detach((void *)plVar13[1]);
    }
    else {
      (**(code **)(*plVar13 + 0xe0))(plVar13);
    }
    plVar13 = (long *)*plVar1;
    *plVar1 = 0;
    if (plVar13 != (long *)0x0) {
      (**(code **)(*plVar13 + 8))();
    }
  }
  puVar9 = &DAT_015d3620;
  if ((char)uVar10 != '\0') {
    lVar5 = *(long *)((long)&__DT_RELA[0xcf8].r_addend + *param_1);
    FUN_0051be80(&local_48,lVar5,lVar5 + 0x1a8);
    puVar9 = &DAT_015d3600;
    if ((local_40 != 0) && (*(long *)(local_40 + 8) != 0)) {
      lVar5 = *(long *)(param_2 + 0x18);
      puVar9 = &DAT_015d3620;
      *(long *)(param_2 + 0x18) = lVar5 + 1;
      bVar4 = *(byte *)(*(long *)(param_2 + 0x10) + lVar5);
      if ((int)(uint)bVar4 < *(int *)(local_40 + 8)) {
        if ((uVar10 == 1) || (uVar10 == 10)) {
          local_70 = bVar3 & 0x1f;
          uVar7 = FUN_00121a30(param_2);
          local_58 = (uint)uVar7;
          uVar7 = FUN_00121a30(param_2);
          uVar14 = 0xffffffff;
          uVar11 = (uint)uVar7;
          *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 4;
          local_4c = 0xffffffff;
          fVar16 = 0.0;
          local_54 = 0.0;
          local_50 = 0.0;
        }
        else if (uVar10 - 2 < 5) {
          *(long *)(param_2 + 0x18) = lVar5 + 2;
          if (uVar10 == 2) {
            local_70 = 0x100;
            iVar12 = 0x100;
          }
          else if (uVar10 == 3) {
            local_70 = 0x100;
            iVar12 = 0;
          }
          else if (uVar10 == 4) {
            local_70 = 0x100;
            iVar12 = 0x200;
          }
          else {
            local_70 = 0;
            iVar12 = 0x100;
            if (uVar10 != 5) {
              uVar11 = 0x200;
              if (uVar10 != 6) {
                uVar11 = local_70;
              }
              iVar12 = 0x100;
              local_70 = uVar11;
              if (uVar10 != 6) {
                iVar12 = 0;
              }
            }
          }
          uVar14 = (uint)*(byte *)(*(long *)(param_2 + 0x10) + 1 + lVar5);
          uVar7 = FUN_00121a30(param_2);
          uVar8 = FUN_00121a30(param_2);
          lVar5 = *(long *)(param_2 + 0x18);
          *(long *)(param_2 + 0x18) = lVar5 + 1;
          fVar16 = (float)((uint)uVar8 * 0x200 + local_70);
          local_50 = (float)(iVar12 + (uint)uVar7 * 0x200);
          uVar11 = 0xffffffff;
          local_54 = (float)((uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar5) * 8);
          uVar7 = FUN_00121a30(param_2);
          local_4c = (uint)uVar7;
          local_58 = 0xffffffff;
          local_70 = 2;
        }
        else {
          uVar14 = 0xffffffff;
          local_4c = 0xffffffff;
          uVar11 = 0xffffffff;
          local_58 = 0xffffffff;
          fVar16 = 0.0;
          local_54 = 0.0;
          local_50 = 0.0;
          local_70 = uVar10;
        }
        uVar10 = FUN_00133390(param_2);
        puVar2 = (uint *)(*(long *)((long)&__DT_RELA[0xcf9].r_offset + *param_1) + 0x90 +
                         (ulong)uVar15 * 0x2c);
        *puVar2 = uVar15;
        puVar2[2] = (uint)bVar4;
        puVar2[7] = (uint)local_50;
        puVar2[8] = (uint)local_54;
        puVar2[5] = uVar11;
        puVar2[9] = (uint)fVar16;
        puVar2[3] = uVar10;
        puVar9 = &DAT_015d3620;
        puVar2[1] = local_70;
        puVar2[4] = local_58;
        puVar2[6] = uVar14;
        puVar2[10] = local_4c;
      }
    }
    if (local_48 != 0) {
      ref_counter_base::DecRef();
      return puVar9;
    }
  }
  return puVar9;
}


```

## `jag::packethandlers::PlayerGroup::PLAYER_OP` @ 00186b70
```c

undefined * jag::packethandlers::PlayerGroup::PLAYER_OP(long *param_1,PacketCore *param_2)

{
  long *plVar1;
  byte bVar2;
  char cVar3;
  uint uVar4;
  ushort *puVar5;
  long *plVar6;
  ushort uVar7;
  long lVar8;
  void *this;
  bool bVar9;
  
  lVar8 = param_2->position;
  param_2->position = lVar8 + 1;
  bVar2 = *(byte *)((long)param_2->bufData + lVar8);
  puVar5 = (ushort *)((long)param_2->bufData + lVar8 + 1);
  if ((char)*puVar5 < '\0') {
    uVar4 = Packet::gT_unsigned_int(param_2);
    uVar4 = uVar4 & 0x7fffffff;
    lVar8 = *(long *)((long)&__DT_RELA[0xcf9].r_info + *param_1);
LAB_00186bcc:
    cVar3 = FUN_0015d230(lVar8,param_2,bVar2,uVar4);
    if (cVar3 == '\0') {
      return &DAT_015d3620;
    }
    return &DAT_015d3600;
  }
  bVar9 = DAT_01050dc0 == 0x3020100;
  param_2->position = lVar8 + 3;
  uVar7 = *puVar5;
  if (bVar9) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  uVar4 = (uint)uVar7;
  lVar8 = *(long *)((long)&__DT_RELA[0xcf9].r_info + *param_1);
  if (uVar7 != 0x7fff) goto LAB_00186bcc;
  plVar1 = (long *)(lVar8 + 0x10 + (ulong)bVar2 * 8);
  plVar6 = (long *)*plVar1;
  if (plVar6 == (long *)0x0) {
    return &DAT_015d3620;
  }
  if (*(code **)(*plVar6 + 0xe0) == FUN_000f0fa0) {
    lVar8 = plVar6[0x293];
    plVar6[0x294] = 0;
    plVar6[0x293] = 0;
    if (lVar8 == 0) {
      this = (void *)plVar6[1];
      if (this == (void *)0x0) {
        *plVar1 = 0;
        goto LAB_00186c58;
      }
    }
    else {
      ref_counter_base::DecRef();
      this = (void *)plVar6[1];
      if (this == (void *)0x0) goto LAB_00186c47;
    }
    graphics::GraphNode::Detach(this);
  }
  else {
    (**(code **)(*plVar6 + 0xe0))(plVar6);
  }
LAB_00186c47:
  plVar6 = (long *)*plVar1;
  *plVar1 = 0;
  if (plVar6 == (long *)0x0) {
    return &DAT_015d3620;
  }
LAB_00186c58:
  (**(code **)(*plVar6 + 8))(plVar6);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::PlayerGroup::UPDATE_PLAYER_GROUP` @ 00198b80
```c

undefined * jag::packethandlers::PlayerGroup::UPDATE_PLAYER_GROUP(long *param_1,long param_2)

{
  char cVar1;
  undefined8 uVar2;
  long lVar3;
  undefined *puVar4;
  long lVar5;
  long lVar6;
  undefined8 local_e8;
  undefined8 local_e0;
  undefined4 local_d8;
  long local_d0;
  undefined8 local_c8;
  long local_b8;
  long local_b0;
  long local_a0;
  long local_98;
  long local_88;
  long local_80;
  long local_70;
  long local_68;
  undefined8 local_60;
  long local_58;
  undefined1 local_50 [8];
  long local_48;
  void *local_40;
  long local_38;
  undefined4 local_30;
  undefined4 local_2c;
  int local_28;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::PLAYER_GROUP_DELTA (op 161, varShort).
                       byte + payload -> player-group delta record. The Ghidra function name
                       'UPDATE_PLAYER_GROUP' is a fabricated rename, NOT the official packet name.
                        */
  lVar6 = *(long *)(param_2 + 0x18);
  lVar5 = lVar6 + 1;
  uVar2 = *(undefined8 *)((long)&__DT_RELA[0xd40].r_addend + *param_1);
  *(long *)(param_2 + 0x18) = lVar5;
  lVar3 = FUN_00198b40(uVar2);
  local_58 = *(long *)(lVar3 + 8);
  puVar4 = &DAT_015d3600;
  if (local_58 != 0) {
    local_60 = *(undefined8 *)((long)&__DT_SYMTAB[0x8d].st_value + local_58);
    cVar1 = *(char *)(*(long *)(param_2 + 0x10) + lVar6);
    local_e0 = 0xffffffff00000000;
    local_e8 = 0x80000000100;
    local_d8 = 0xffffffff;
    local_d0 = 0;
    local_c8 = 0;
    local_b8 = 0;
    local_b0 = 0;
    local_a0 = 0;
    local_98 = 0;
    local_88 = 0;
    local_80 = 0;
    local_70 = 0;
    local_68 = 0;
    local_48 = 0;
    local_40 = (void *)0x0;
    local_38 = lVar5;
    FUN_00c45fe0(local_50,*(undefined8 *)(param_2 + 8));
    memcpy(local_40,*(void **)(param_2 + 0x10),*(size_t *)(param_2 + 8));
    local_2c = 0xffffffff;
    local_30 = (undefined4)*(undefined8 *)(param_2 + 0x18);
    local_28 = (int)cVar1;
    FUN_00141b40(&local_e8);
    FUN_001418e0(&local_e8);
    if ((local_48 != 0) && (local_40 != (void *)0x0)) {
      eastl__basic_string();
    }
    if ((local_70 != 0) && (local_68 != 0)) {
      eastl__basic_string();
    }
    if ((local_88 != 0) && (local_80 != 0)) {
      eastl__basic_string();
    }
    if (((local_98 != 0) && (FUN_00964000(local_98,local_a0), local_98 != 0)) && (local_a0 != 0)) {
      eastl__basic_string();
    }
    if ((local_b8 != 0) && (local_b0 != 0)) {
      eastl__basic_string();
    }
    if (local_d0 != 0) {
      ref_counter_base::DecRef();
    }
    puVar4 = &DAT_015d3620;
  }
  return puVar4;
}


```

## `jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2` @ 001bcf40
```c

/* WARNING: Type propagation algorithm not settling */

undefined * jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2(long *param_1,PacketCore *param_2)

{
  int *piVar1;
  char *pcVar2;
  char cVar3;
  int iVar4;
  long lVar5;
  undefined8 uVar6;
  ushort uVar7;
  uint uVar8;
  int iVar9;
  size_t sVar10;
  ulong uVar11;
  ulong uVar12;
  undefined8 uVar13;
  undefined8 *puVar14;
  undefined1 *puVar15;
  undefined8 *puVar16;
  void *pvVar17;
  undefined8 *******pppppppuVar18;
  byte *******pppppppbVar19;
  undefined8 extraout_RDX;
  uint uVar20;
  byte *pbVar21;
  undefined8 *puVar22;
  char *pcVar23;
  long *******ppppppplVar24;
  undefined8 *puVar25;
  long *******ppppppplVar26;
  byte *pbVar27;
  byte bVar28;
  char *pcVar29;
  ulong uVar30;
  byte bVar31;
  long lVar32;
  ulong uVar33;
  long *plVar34;
  long *******ppppppplVar35;
  long *******ppppppplVar36;
  long *******ppppppplVar37;
  char *pcVar38;
  long lVar39;
  char cVar40;
  int iVar41;
  byte *******__n;
  undefined1 auVar42 [16];
  ulong local_8f0;
  long local_8c8;
  long local_8b8;
  ulong local_888;
  undefined8 *******local_880;
  long local_878;
  long *******local_868;
  long *******local_860;
  undefined8 *local_858;
  undefined8 *puStack_850;
  undefined8 *local_848;
  undefined1 local_838 [16];
  undefined8 local_828;
  undefined1 local_818;
  undefined7 uStack_817;
  undefined8 local_810;
  char local_801;
  undefined8 *******local_7f8;
  long local_7f0;
  undefined7 local_7e8;
  char acStack_7e1 [9];
  undefined8 *******local_7d8;
  undefined1 local_7d0 [72];
  undefined1 *local_788;
  long local_780;
  undefined8 local_778;
  undefined1 *local_768;
  undefined1 local_760 [72];
  byte *******local_718;
  byte *******pppppppbStack_710;
  undefined8 local_708;
  byte *******local_6f8;
  byte ******local_6f0 [104];
  byte ******ppppppbStack_3b0;
  long *******local_3a8;
  long *******ppppppplStack_3a0;
  undefined8 local_398;
  long *******local_388;
  undefined1 local_380 [832];
  undefined1 local_40 [16];
  
  lVar32 = param_2->position;
  pvVar17 = param_2->bufData;
  local_7f8 = (undefined8 *******)local_7d0;
  local_788 = local_760;
  local_778 = -0x7fffffffffffffc1;
  pbVar21 = (byte *)((long)pvVar17 + lVar32);
  local_780 = 0;
  local_760[0] = 0;
  local_7e8 = 0x3f;
  acStack_7e1[0] = -0x80;
  local_7f0 = 0;
  local_7d0[0] = 0;
  local_7d8 = local_7f8;
  local_768 = local_788;
  sVar10 = strlen((char *)pbVar21);
  if (sVar10 == 0) {
    pbVar21 = (byte *)((long)pvVar17 + lVar32 + 1);
    param_2->position = lVar32 + 1;
    sVar10 = strlen((char *)pbVar21);
    if (sVar10 != 0) {
LAB_001bf1a2:
      local_7f0 = 0;
      pppppppuVar18 = local_7f8;
      goto LAB_001be81b;
    }
    param_2->position = lVar32 + 2;
LAB_001bd02a:
    local_7f0 = 0;
    pppppppuVar18 = local_7f8;
LAB_001bd03e:
    *(undefined1 *)pppppppuVar18 = 0;
  }
  else {
    cVar40 = -0x80;
    if ((int)sVar10 != 0) {
      pbVar27 = pbVar21 + (sVar10 & 0xffffffff);
      if ((sVar10 & 1) != 0) {
        uVar33 = (ulong)*pbVar21;
        if ((byte)(*pbVar21 + 0x80) < 0x20) {
          uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
        }
        uVar8 = (uint)uVar33;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&local_788,(int)(char)(byte)uVar33);
          }
          else {
            if (uVar8 < 0x800) {
              lVar32 = local_780;
              if (-1 < local_778) {
                lVar32 = 0x17 - (long)local_778._7_1_;
              }
              bVar31 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&local_788,lVar32 + 2);
            }
            else {
              lVar32 = local_780;
              if (-1 < local_778) {
                lVar32 = 0x17 - (long)local_778._7_1_;
              }
              FUN_006b28b0(&local_788,lVar32 + 3);
              bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&local_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
            }
            FUN_00c29370(&local_788,(int)(char)bVar31);
            FUN_00c29370(&local_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          }
        }
        pbVar21 = pbVar21 + 1;
        goto joined_r0x001bee9c;
      }
      do {
        while( true ) {
          uVar33 = (ulong)*pbVar21;
          if ((byte)(*pbVar21 + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&local_788,(int)(char)(byte)uVar33);
            }
            else {
              if (uVar8 < 0x800) {
                lVar32 = local_780;
                if (-1 < local_778) {
                  lVar32 = 0x17 - (long)local_778._7_1_;
                }
                bVar31 = (byte)(uVar8 >> 6) | 0xc0;
                FUN_006b28b0(&local_788,lVar32 + 2);
              }
              else {
                lVar32 = local_780;
                if (-1 < local_778) {
                  lVar32 = 0x17 - (long)local_778._7_1_;
                }
                FUN_006b28b0(&local_788,lVar32 + 3);
                bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
                FUN_00c29370(&local_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
              }
              FUN_00c29370(&local_788,(int)(char)bVar31);
              FUN_00c29370(&local_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
            }
          }
          uVar33 = (ulong)pbVar21[1];
          if ((byte)(pbVar21[1] + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) break;
LAB_001be7c0:
          pbVar21 = pbVar21 + 2;
          if (pbVar21 == pbVar27) goto LAB_001be7d4;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            lVar32 = local_780;
            if (-1 < local_778) {
              lVar32 = 0x17 - (long)local_778._7_1_;
            }
            bVar31 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&local_788,lVar32 + 2);
          }
          else {
            lVar32 = local_780;
            if (-1 < local_778) {
              lVar32 = 0x17 - (long)local_778._7_1_;
            }
            FUN_006b28b0(&local_788,lVar32 + 3);
            bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&local_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&local_788,(int)(char)bVar31);
          FUN_00c29370(&local_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          goto LAB_001be7c0;
        }
        pbVar21 = pbVar21 + 2;
        FUN_00c29370(&local_788,(int)(char)(byte)uVar33);
joined_r0x001bee9c:
      } while (pbVar21 != pbVar27);
LAB_001be7d4:
      lVar32 = param_2->position;
      pvVar17 = param_2->bufData;
      cVar40 = acStack_7e1[0];
    }
    lVar32 = lVar32 + 1 + sVar10;
    pbVar21 = (byte *)((long)pvVar17 + lVar32);
    param_2->position = lVar32;
    sVar10 = strlen((char *)pbVar21);
    if (sVar10 == 0) {
      param_2->position = lVar32 + 1;
      if (cVar40 < '\0') goto LAB_001bd02a;
      acStack_7e1[0] = '\x17';
      pppppppuVar18 = &local_7f8;
      goto LAB_001bd03e;
    }
    if (cVar40 < '\0') goto LAB_001bf1a2;
    acStack_7e1[0] = '\x17';
    pppppppuVar18 = &local_7f8;
LAB_001be81b:
    *(undefined1 *)pppppppuVar18 = 0;
    pbVar27 = pbVar21 + (sVar10 & 0xffffffff);
    if ((sVar10 & 0xffffffff) != 0) {
      if (((long)pbVar27 - (long)pbVar21 & 1U) != 0) {
        uVar33 = (ulong)*pbVar21;
        if ((byte)(*pbVar21 + 0x80) < 0x20) {
          uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
        }
        uVar8 = (uint)uVar33;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&local_7f8,(int)(char)(byte)uVar33);
          }
          else {
            if (uVar8 < 0x800) {
              lVar32 = local_7f0;
              if (-1 < acStack_7e1[0]) {
                lVar32 = 0x17 - (long)acStack_7e1[0];
              }
              bVar31 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&local_7f8,lVar32 + 2);
            }
            else {
              lVar32 = local_7f0;
              if (-1 < acStack_7e1[0]) {
                lVar32 = 0x17 - (long)acStack_7e1[0];
              }
              FUN_006b28b0(&local_7f8,lVar32 + 3);
              uVar20 = (uint)(uVar33 >> 0xc) | 0xffffffe0;
              bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&local_7f8,(int)(char)uVar20,extraout_RDX,uVar20);
            }
            FUN_00c29370(&local_7f8,(int)(char)bVar31);
            FUN_00c29370(&local_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          }
        }
        pbVar21 = pbVar21 + 1;
        goto joined_r0x001bede1;
      }
      do {
        while( true ) {
          uVar33 = (ulong)*pbVar21;
          if ((byte)(*pbVar21 + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&local_7f8,(int)(char)(byte)uVar33);
            }
            else {
              if (uVar8 < 0x800) {
                lVar32 = local_7f0;
                if (-1 < acStack_7e1[0]) {
                  lVar32 = 0x17 - (long)acStack_7e1[0];
                }
                bVar31 = (byte)(uVar8 >> 6) | 0xc0;
                FUN_006b28b0(&local_7f8,lVar32 + 2);
              }
              else {
                lVar32 = local_7f0;
                if (-1 < acStack_7e1[0]) {
                  lVar32 = 0x17 - (long)acStack_7e1[0];
                }
                FUN_006b28b0(&local_7f8,lVar32 + 3);
                bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
                FUN_00c29370(&local_7f8,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
              }
              FUN_00c29370(&local_7f8,(int)(char)bVar31);
              FUN_00c29370(&local_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
            }
          }
          uVar33 = (ulong)pbVar21[1];
          if ((byte)(pbVar21[1] + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) break;
LAB_001be98c:
          pbVar21 = pbVar21 + 2;
joined_r0x001bede1:
          if (pbVar21 == pbVar27) goto LAB_001be9a0;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            lVar32 = local_7f0;
            if (-1 < acStack_7e1[0]) {
              lVar32 = 0x17 - (long)acStack_7e1[0];
            }
            bVar31 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&local_7f8,lVar32 + 2);
          }
          else {
            lVar32 = local_7f0;
            if (-1 < acStack_7e1[0]) {
              lVar32 = 0x17 - (long)acStack_7e1[0];
            }
            FUN_006b28b0(&local_7f8,lVar32 + 3);
            bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&local_7f8,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&local_7f8,(int)(char)bVar31);
          FUN_00c29370(&local_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          goto LAB_001be98c;
        }
        pbVar21 = pbVar21 + 2;
        FUN_00c29370(&local_7f8,(int)(char)(byte)uVar33);
      } while (pbVar21 != pbVar27);
    }
LAB_001be9a0:
    param_2->position = sVar10 + 1 + param_2->position;
  }
  if (acStack_7e1[0] < '\0') {
    if (local_7f0 != 0) {
      puVar15 = (undefined1 *)(local_7f0 + (long)local_7f8);
      goto LAB_001bdc0e;
    }
  }
  else if (0x17 - (long)acStack_7e1[0] != 0) {
    puVar15 = (undefined1 *)((0x17 - (long)acStack_7e1[0]) + (long)&local_7f8);
LAB_001bdc0e:
    bVar31 = puVar15[-1];
    bVar28 = 1;
    if (bVar31 < 0x2d) {
      bVar28 = ~(byte)(0x100900000000 >> (bVar31 & 0x3f)) & 1;
    }
    if ((bVar31 != 0x7e) && (bVar28 != 0)) {
      FUN_00c29370(&local_7f8,0x20);
    }
  }
  uVar8 = Packet::gT_unsigned_int(param_2);
  uVar7 = FUN_00121a30(param_2);
  local_848 = (undefined8 *)0x0;
  local_858 = (undefined8 *)0x0;
  puStack_850 = (undefined8 *)0x0;
  if (uVar7 != 0) {
    FUN_004890f0(&local_858);
    uVar20 = 0;
    do {
      while( true ) {
        local_718 = (byte *******)((ulong)local_718 & 0xffffffffffffff00);
        local_708 = (byte *******)CONCAT17(0x17,(undefined7)local_708);
        FUN_00afd8d0(param_2,&local_718);
        pppppppbVar19 = (byte *******)(0x17 - (long)local_708._7_1_);
        if ((long)local_708 < 0) {
          pppppppbVar19 = pppppppbStack_710;
        }
        if (pppppppbVar19 == (byte *******)0x0) {
          if (((long)local_708 < 0) && (local_718 != (byte *******)0x0)) {
            HeapInterface::Free();
          }
          goto LAB_001bd286;
        }
        if (puStack_850 < local_848) {
          *(undefined1 *)((long)puStack_850 + 0x17) = 0x17;
          *(undefined1 *)puStack_850 = 0;
          puStack_850 = puStack_850 + 3;
        }
        else {
          local_3a8 = (long *******)((ulong)local_3a8 & 0xffffffffffffff00);
          local_398 = (undefined1 *)CONCAT17(0x17,(undefined7)local_398);
          FUN_00125c60(&local_858,&local_3a8);
          if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
            HeapInterface::Free();
          }
        }
        lVar32 = 0x17 - (long)acStack_7e1[0];
        if (acStack_7e1[0] < '\0') {
          lVar32 = local_7f0;
        }
        if (lVar32 == 0) {
          FUN_00126110(puStack_850 + -3,&local_718);
        }
        else {
          pppppppbVar19 = (byte *******)(0x17 - (long)local_708._7_1_);
          if ((long)local_708 < 0) {
            pppppppbVar19 = pppppppbStack_710;
          }
          puVar14 = puStack_850 + -3;
          FUN_0048f440(puVar14,(byte *)((long)pppppppbVar19 + lVar32));
          pppppppbVar19 = (byte *******)&local_718;
          if ((long)local_708 < 0) {
            pppppppbVar19 = local_718;
          }
          pppppppuVar18 = &local_7f8;
          if (acStack_7e1[0] < '\0') {
            pppppppuVar18 = local_7f8;
          }
          FUN_00495e30(puVar14,"%s%s",pppppppuVar18,pppppppbVar19);
        }
        if (((long)local_708 < 0) && (local_718 != (byte *******)0x0)) break;
        uVar20 = uVar20 + 1;
        if (uVar7 == uVar20) goto LAB_001bd286;
      }
      uVar20 = uVar20 + 1;
      HeapInterface::Free();
    } while (uVar7 != uVar20);
  }
LAB_001bd286:
  local_8c8 = *(long *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
  if (acStack_7e1[0] < '\0') {
    pppppppuVar18 = local_7f8;
    lVar32 = local_7f0;
    if (local_7f0 == 0) {
      bVar31 = 0;
      local_880 = (undefined8 *******)0x0;
      goto LAB_001bd2e3;
    }
LAB_001bdc65:
    local_3a8 = (long *******)((ulong)local_3a8 & 0xffffffffffffff00);
    local_398 = (undefined1 *)CONCAT17(0x17,(undefined7)local_398);
    FUN_0048d690(&local_3a8,pppppppuVar18,(undefined1 *)((long)pppppppuVar18 + lVar32));
    FUN_00145d90(&local_3a8);
    auVar42 = FUN_00440aa0(local_8c8 + 8,&local_3a8);
    lVar32 = auVar42._0_8_;
    if ((lVar32 == auVar42._8_8_) || (lVar32 == *(long *)(local_8c8 + 0x10))) {
      bVar31 = 0;
    }
    else {
      bVar31 = *(byte *)(lVar32 + 0xf8) >> 2 & 1;
    }
    if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
      HeapInterface::Free();
    }
    local_8c8 = *(long *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
    if (acStack_7e1[0] < '\0') {
      if (local_7f0 == 0) {
        local_880 = (undefined8 *******)0x0;
      }
      else {
        local_880 = local_7f8;
      }
      goto LAB_001bd2e3;
    }
  }
  else {
    lVar32 = 0x17 - (long)acStack_7e1[0];
    pppppppuVar18 = &local_7f8;
    if (lVar32 != 0) goto LAB_001bdc65;
    bVar31 = 0;
  }
  local_880 = (undefined8 *******)0x0;
  if (acStack_7e1[0] != '\x17') {
    local_880 = &local_7f8;
  }
LAB_001bd2e3:
  FUN_00696c80(*(undefined8 *)(local_8c8 + 0xde8),*(undefined8 *)(local_8c8 + 0xdf0));
  *(undefined8 *)(local_8c8 + 0xdf8) = 0;
  if (local_858 != puStack_850) {
    puVar14 = local_858 + 3;
    uVar33 = (long)puStack_850 - (long)puVar14;
    puVar22 = local_858;
    do {
      uVar11 = FUN_00ae2750(puVar22,0x3a);
      if (uVar11 != 0xffffffffffffffff) {
        if (*(char *)((long)puVar22 + 0x17) < '\0') {
          puVar16 = (undefined8 *)*puVar22;
          uVar12 = puVar22[1];
        }
        else {
          uVar12 = 0x17 - (long)*(char *)((long)puVar22 + 0x17);
          puVar16 = puVar22;
        }
        local_718 = (byte *******)((ulong)local_718 & 0xffffffffffffff00);
        if (uVar11 <= uVar12) {
          uVar12 = uVar11;
        }
        local_708 = (byte *******)CONCAT17(0x17,(undefined7)local_708);
        FUN_0048d690(&local_718,puVar16,(long)puVar16 + uVar12);
        if (*(char *)((long)puVar22 + 0x17) < '\0') {
          puVar16 = (undefined8 *)*puVar22;
          lVar32 = puVar22[1];
        }
        else {
          lVar32 = 0x17 - (long)*(char *)((long)puVar22 + 0x17);
          puVar16 = puVar22;
        }
        local_3a8 = (long *******)((ulong)local_3a8 & 0xffffffffffffff00);
        local_398 = (undefined1 *)CONCAT17(0x17,(undefined7)local_398);
        FUN_0048d690(&local_3a8,(long)puVar16 + uVar11 + 2,lVar32 + (long)puVar16);
        pppppppbVar19 = (byte *******)&local_718;
        if ((long)local_708 < 0) {
          pppppppbVar19 = local_718;
        }
        bVar28 = *(byte *)pppppppbVar19;
        local_8f0 = 0x811c9dc5;
        if (bVar28 != 0) {
          uVar20 = 0x811c9dc5;
          do {
            pppppppbVar19 = (byte *******)((long)pppppppbVar19 + 1);
            uVar20 = uVar20 * 0x1000193 ^ (uint)bVar28;
            bVar28 = *(byte *)pppppppbVar19;
          } while (bVar28 != 0);
          local_8f0 = (ulong)uVar20;
        }
        uVar11 = *(ulong *)(local_8c8 + 0xdf0);
        uVar12 = local_8f0 % (uVar11 & 0xffffffff);
        puVar16 = *(undefined8 **)(*(long *)(local_8c8 + 0xde8) + uVar12 * 8);
        local_8b8 = uVar12 * 8;
        if (puVar16 != (undefined8 *)0x0) {
          pppppppbVar19 = (byte *******)&local_718;
          __n = (byte *******)(0x17 - (long)local_708._7_1_);
          if ((long)local_708 < 0) {
            pppppppbVar19 = local_718;
            __n = pppppppbStack_710;
          }
          do {
            if (*(char *)((long)puVar16 + 0x17) < '\0') {
              if ((byte *******)puVar16[1] == __n) {
                puVar25 = (undefined8 *)*puVar16;
LAB_001bd4df:
                iVar9 = memcmp(pppppppbVar19,puVar25,(size_t)__n);
                if (iVar9 == 0) goto LAB_001bd4ef;
              }
            }
            else {
              puVar25 = puVar16;
              if ((byte *******)(0x17 - (long)*(char *)((long)puVar16 + 0x17)) == __n)
              goto LAB_001bd4df;
            }
            puVar16 = (undefined8 *)puVar16[6];
          } while (puVar16 != (undefined8 *)0x0);
        }
        uVar11 = FUN_00aa5fe0(local_8c8 + 0xe00,uVar11 & 0xffffffff,
                              *(undefined4 *)(local_8c8 + 0xdf8),1);
        puVar16 = (undefined8 *)FUN_00c29480(0x38);
        uVar12 = uVar11 >> 0x20;
        FUN_0048dcd0(puVar16);
        *(undefined1 *)(puVar16 + 3) = 0;
        *(undefined1 *)((long)puVar16 + 0x2f) = 0x17;
        puVar16[6] = 0;
        if ((char)uVar11 == '\0') {
          pvVar17 = *(void **)(local_8c8 + 0xde8);
        }
        else {
          pvVar17 = (void *)FUN_00c29430();
          memset(pvVar17,0,uVar12 * 8);
          uVar11 = *(ulong *)(local_8c8 + 0xdf0);
          *(undefined8 *)((long)pvVar17 + uVar12 * 8) = 0xffffffffffffffff;
          if (uVar11 != 0) {
            lVar32 = *(long *)(local_8c8 + 0xde8);
            uVar30 = 0;
            do {
              puVar25 = (undefined8 *)(lVar32 + uVar30 * 8);
              pbVar21 = (byte *)*puVar25;
              if (pbVar21 != (byte *)0x0) {
                do {
                  pbVar27 = pbVar21;
                  if ((char)pbVar21[0x17] < '\0') {
                    pbVar27 = *(byte **)pbVar21;
                  }
                  bVar28 = *pbVar27;
                  uVar11 = 0x811c9dc5;
                  if (bVar28 != 0) {
                    uVar11 = 0x811c9dc5;
                    do {
                      pbVar27 = pbVar27 + 1;
                      uVar11 = (ulong)((int)uVar11 * 0x1000193 ^ (uint)bVar28);
                      bVar28 = *pbVar27;
                    } while (bVar28 != 0);
                  }
                  *puVar25 = *(undefined8 *)(pbVar21 + 0x30);
                  puVar25 = (undefined8 *)((long)pvVar17 + (uVar11 % uVar12) * 8);
                  *(undefined8 *)(pbVar21 + 0x30) = *puVar25;
                  lVar32 = *(long *)(local_8c8 + 0xde8);
                  *puVar25 = pbVar21;
                  puVar25 = (undefined8 *)(lVar32 + uVar30 * 8);
                  pbVar21 = (byte *)*puVar25;
                } while (pbVar21 != (byte *)0x0);
                uVar11 = *(ulong *)(local_8c8 + 0xdf0);
              }
              uVar30 = uVar30 + 1;
            } while (uVar30 < uVar11);
            if ((1 < uVar11) && (lVar32 != 0)) {
              HeapInterface::Free();
            }
          }
          *(ulong *)(local_8c8 + 0xdf0) = uVar12;
          *(void **)(local_8c8 + 0xde8) = pvVar17;
          local_8b8 = local_8f0 % uVar12 << 3;
        }
        puVar16[6] = *(undefined8 *)((long)pvVar17 + local_8b8);
        *(undefined8 **)(*(long *)(local_8c8 + 0xde8) + local_8b8) = puVar16;
        *(long *)(local_8c8 + 0xdf8) = *(long *)(local_8c8 + 0xdf8) + 1;
LAB_001bd4ef:
        FUN_00126110(puVar16 + 3,&local_3a8);
        FUN_00126110(puVar22,&local_718);
        if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
          HeapInterface::Free();
        }
        if (((long)local_708 < 0) && (local_718 != (byte *******)0x0)) {
          HeapInterface::Free();
        }
      }
      puVar22 = puVar22 + 3;
    } while ((undefined8 *)((uVar33 & 0xfffffffffffffff8) + (long)puVar14) != puVar22);
  }
  local_828 = 0;
  lVar32 = *(long *)(local_8c8 + 0x2a8);
  local_838 = (undefined1  [16])0x0;
  if (*(char *)(lVar32 + 0xaf) < '\0') {
    local_888 = *(ulong *)(lVar32 + 0xa0);
    local_878 = *(long *)(lVar32 + 0x98);
  }
  else {
    local_878 = lVar32 + 0x98;
    local_888 = 0x17 - (long)*(char *)(lVar32 + 0xaf);
  }
  if (*(char *)(local_8c8 + 0x9ff) < '\0') {
    uVar33 = *(ulong *)(local_8c8 + 0x9f0);
    lVar39 = *(long *)(local_8c8 + 0x9e8);
  }
  else {
    uVar33 = 0x17 - (long)*(char *)(local_8c8 + 0x9ff);
    lVar39 = local_8c8 + 0x9e8;
  }
  if (uVar33 < local_888) {
    ppppppplStack_3a0 = (long *******)0x0;
    local_3a8 = (long *******)0x0;
  }
  else {
    ppppppplStack_3a0 = (long *******)(lVar39 + local_888);
    local_3a8 = (long *******)(uVar33 - local_888);
  }
  FUN_004412f0(lVar32,local_838,&local_3a8);
  uVar6 = local_838._8_8_;
  if (local_838._0_8_ != local_838._8_8_) {
    plVar34 = (long *)local_838._0_8_;
LAB_001bd650:
    pcVar23 = (char *)plVar34[1];
    local_708 = &ppppppbStack_3b0;
    pcVar38 = pcVar23 + *plVar34;
    pcVar29 = pcVar23;
    local_718 = local_6f0;
    pppppppbStack_710 = local_6f0;
    local_6f8 = local_6f0;
    puVar14 = local_858;
    puVar22 = puStack_850;
    if (pcVar23 != pcVar38) {
      do {
        if (*pcVar23 == ' ') {
          if (pcVar29 != pcVar23) {
            if (pppppppbStack_710 < local_708) {
              pppppppbStack_710[4] = (byte ******)(pppppppbStack_710 + 5);
              *pppppppbStack_710 = (byte ******)(pppppppbStack_710 + 5);
              pppppppbStack_710[2] = (byte ******)0x800000000000003f;
              pppppppbStack_710[1] = (byte ******)0x0;
              *(byte *)(pppppppbStack_710 + 5) = 0;
              pppppppbStack_710 = pppppppbStack_710 + 0xd;
            }
            else {
              local_398 = (undefined1 *)0x800000000000003f;
              ppppppplStack_3a0 = (long *******)0x0;
              local_380[0] = 0;
              local_3a8 = (long *******)local_380;
              local_388 = (long *******)local_380;
              FUN_00b08ef0(&local_718,&local_3a8);
              if ((long)local_398 < 0) {
                FUN_00aa7d70(&local_3a8);
              }
            }
            FUN_003e9e60(pppppppbStack_710 + -0xd,pcVar29,pcVar23);
          }
          uVar20 = (int)pcVar38 - (int)pcVar23 & 7;
          puVar14 = local_858;
          puVar22 = puStack_850;
          if (uVar20 != 0) {
            if (uVar20 != 1) {
              if (uVar20 != 2) {
                if (uVar20 != 3) {
                  if (uVar20 != 4) {
                    if (uVar20 != 5) {
                      if (uVar20 != 6) {
                        pcVar29 = pcVar23;
                        if (*pcVar23 != ' ') goto LAB_001bd6ae;
                        pcVar23 = pcVar23 + 1;
                      }
                      pcVar29 = pcVar23;
                      if (*pcVar23 != ' ') goto LAB_001bd6ae;
                      pcVar23 = pcVar23 + 1;
                    }
                    pcVar29 = pcVar23;
                    if (*pcVar23 != ' ') goto LAB_001bd6ae;
                    pcVar23 = pcVar23 + 1;
                  }
                  pcVar29 = pcVar23;
                  if (*pcVar23 != ' ') goto LAB_001bd6ae;
                  pcVar23 = pcVar23 + 1;
                }
                pcVar29 = pcVar23;
                if (*pcVar23 != ' ') goto LAB_001bd6ae;
                pcVar23 = pcVar23 + 1;
              }
              pcVar29 = pcVar23;
              if (*pcVar23 != ' ') goto LAB_001bd6ae;
              pcVar23 = pcVar23 + 1;
            }
            pcVar29 = pcVar23;
            if (*pcVar23 != ' ') goto LAB_001bd6ae;
            pcVar23 = pcVar23 + 1;
            if (pcVar38 == pcVar23) goto joined_r0x001bd6e8;
          }
          cVar40 = *pcVar23;
          pcVar2 = pcVar23;
          while ((((((pcVar23 = pcVar2, pcVar29 = pcVar2, cVar40 == ' ' &&
                     (pcVar23 = pcVar2 + 1, pcVar29 = pcVar23, *pcVar23 == ' ')) &&
                    (pcVar23 = pcVar2 + 2, pcVar29 = pcVar23, *pcVar23 == ' ')) &&
                   ((pcVar23 = pcVar2 + 3, pcVar29 = pcVar23, pcVar2[3] == ' ' &&
                    (pcVar23 = pcVar2 + 4, pcVar29 = pcVar23, pcVar2[4] == ' ')))) &&
                  (pcVar23 = pcVar2 + 5, pcVar29 = pcVar23, pcVar2[5] == ' ')) &&
                 ((pcVar23 = pcVar2 + 6, pcVar29 = pcVar23, pcVar2[6] == ' ' &&
                  (pcVar23 = pcVar2 + 7, pcVar29 = pcVar23, pcVar2[7] == ' '))))) {
            pcVar2 = pcVar2 + 8;
            if (pcVar38 == pcVar2) goto joined_r0x001bd6e8;
            cVar40 = *pcVar2;
          }
        }
        else {
          pcVar23 = pcVar23 + 1;
        }
LAB_001bd6ae:
      } while (pcVar23 != pcVar38);
      puVar14 = local_858;
      puVar22 = puStack_850;
      if (pcVar29 != pcVar23) {
        uVar13 = FUN_00489050(&local_718);
        FUN_003e9e60(uVar13,pcVar29,pcVar23);
        puVar14 = local_858;
        puVar22 = puStack_850;
      }
    }
joined_r0x001bd6e8:
    do {
      puVar16 = puStack_850;
      if (puVar14 == puStack_850) goto LAB_001be092;
      local_398 = local_40;
      local_3a8 = (long *******)local_380;
      ppppppplStack_3a0 = (long *******)local_380;
      local_388 = (long *******)local_380;
      puStack_850 = puVar22;
      FUN_00b08970(&local_3a8,puVar14,&DAT_00fb3ff6);
      ppppppplVar24 = ppppppplStack_3a0;
      ppppppplVar37 = local_3a8;
      if ((ppppppplStack_3a0 != local_3a8) && (pppppppbStack_710 != local_718)) {
        if ((char)*(byte *)((long)local_718 + 0x17) < '\0') {
          pppppppbVar19 = (byte *******)*local_718;
          pbVar21 = (byte *)((long)pppppppbVar19 + (long)local_718[1]);
        }
        else {
          pbVar21 = (byte *)((long)local_718 +
                            (0x17 - (long)(char)*(byte *)((long)local_718 + 0x17)));
          pppppppbVar19 = local_718;
        }
        if (*(char *)((long)local_3a8 + 0x17) < '\0') {
          ppppppplVar26 = (long *******)*local_3a8;
          lVar32 = (long)local_3a8[1] + (long)ppppppplVar26;
        }
        else {
          lVar32 = (long)local_3a8 + (0x17 - (long)*(char *)((long)local_3a8 + 0x17));
          ppppppplVar26 = local_3a8;
        }
        sVar10 = lVar32 - (long)ppppppplVar26;
        if ((((long)sVar10 <= (long)pbVar21 - (long)pppppppbVar19) &&
            (iVar9 = memcmp(ppppppplVar26,pppppppbVar19,sVar10),
            (long)pbVar21 - (long)pppppppbVar19 <= (long)sVar10)) && (iVar9 == 0)) {
          FUN_00493d20(puVar14,plVar34[1],*plVar34 + plVar34[1]);
          FUN_00488b10(&local_3a8);
          FUN_00488b10(&local_718);
          goto LAB_001bd7fa;
        }
      }
      if (ppppppplVar37 != ppppppplVar24) {
        ppppppplVar26 = ppppppplVar37 + 0xd;
        uVar11 = (long)ppppppplVar24 - (long)ppppppplVar26;
        uVar33 = ((ulong)((long)(ppppppplVar37 +
                                (((uVar11 >> 3) * 0xec4ec4ec4ec4ec5 & 0x1fffffffffffffff) + 1) * 0xd
                                ) - (long)ppppppplVar26) >> 3) * 5;
        uVar20 = (uint)uVar33 & 7;
        ppppppplVar36 = ppppppplVar26;
        ppppppplVar35 = ppppppplVar37;
        if ((uVar33 & 7) != 0) {
          if (((*(char *)((long)ppppppplVar37 + 0x17) < '\0') &&
              (*ppppppplVar37 != (long ******)0x0)) && (*ppppppplVar37 != ppppppplVar37[4])) {
            HeapInterface::Free();
          }
          ppppppplVar36 = ppppppplVar37 + 0x1a;
          ppppppplVar35 = ppppppplVar26;
          if (uVar20 != 1) {
            ppppppplVar35 = ppppppplVar36;
            if (uVar20 != 2) {
              ppppppplVar24 = ppppppplVar26;
              if (uVar20 != 3) {
                ppppppplVar24 = ppppppplVar36;
                if (uVar20 != 4) {
                  ppppppplVar24 = ppppppplVar26;
                  if (uVar20 != 5) {
                    ppppppplVar24 = ppppppplVar36;
                    if (uVar20 != 6) {
                      if (((*(char *)((long)ppppppplVar37 + 0x7f) < '\0') &&
                          (*ppppppplVar26 != (long ******)0x0)) &&
                         (*ppppppplVar26 != ppppppplVar37[0x11])) {
                        HeapInterface::Free();
                      }
                      ppppppplVar24 = ppppppplVar37 + 0x27;
                      ppppppplVar26 = ppppppplVar36;
                    }
                    if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                        (*ppppppplVar26 != (long ******)0x0)) &&
                       (*ppppppplVar26 != ppppppplVar26[4])) {
                      HeapInterface::Free();
                    }
                    ppppppplVar36 = ppppppplVar24 + 0xd;
                  }
                  if (((*(char *)((long)ppppppplVar24 + 0x17) < '\0') &&
                      (*ppppppplVar24 != (long ******)0x0)) && (*ppppppplVar24 != ppppppplVar24[4]))
                  {
                    HeapInterface::Free();
                  }
                  ppppppplVar24 = ppppppplVar36 + 0xd;
                  ppppppplVar26 = ppppppplVar36;
                }
                if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                    (*ppppppplVar26 != (long ******)0x0)) && (*ppppppplVar26 != ppppppplVar26[4])) {
                  HeapInterface::Free();
                }
                ppppppplVar36 = ppppppplVar24 + 0xd;
              }
              if (((*(char *)((long)ppppppplVar24 + 0x17) < '\0') &&
                  (*ppppppplVar24 != (long ******)0x0)) && (*ppppppplVar24 != ppppppplVar24[4])) {
                HeapInterface::Free();
              }
              ppppppplVar35 = ppppppplVar36 + 0xd;
              ppppppplVar26 = ppppppplVar36;
            }
            if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                (*ppppppplVar26 != (long ******)0x0)) && (*ppppppplVar26 != ppppppplVar26[4])) {
              HeapInterface::Free();
            }
            ppppppplVar36 = ppppppplVar35 + 0xd;
          }
        }
        while( true ) {
          ppppppplVar24 = local_3a8;
          if (((*(char *)((long)ppppppplVar35 + 0x17) < '\0') &&
              (*ppppppplVar35 != (long ******)0x0)) && (*ppppppplVar35 != ppppppplVar35[4])) {
            HeapInterface::Free();
            ppppppplVar24 = local_3a8;
          }
          local_3a8 = ppppppplVar24;
          if (ppppppplVar36 ==
              ppppppplVar37 + (((uVar11 >> 3) * 0xec4ec4ec4ec4ec5 & 0x1fffffffffffffff) + 1) * 0xd)
          break;
          if (((*(char *)((long)ppppppplVar36 + 0x17) < '\0') &&
              (*ppppppplVar36 != (long ******)0x0)) && (*ppppppplVar36 != ppppppplVar36[4])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x7f) < '\0') &&
              (ppppppplVar36[0xd] != (long ******)0x0)) &&
             (ppppppplVar36[0xd] != ppppppplVar36[0x11])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0xe7) < '\0') &&
              (ppppppplVar36[0x1a] != (long ******)0x0)) &&
             (ppppppplVar36[0x1a] != ppppppplVar36[0x1e])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x14f) < '\0') &&
              (ppppppplVar36[0x27] != (long ******)0x0)) &&
             (ppppppplVar36[0x27] != ppppppplVar36[0x2b])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x1b7) < '\0') &&
              (ppppppplVar36[0x34] != (long ******)0x0)) &&
             (ppppppplVar36[0x34] != ppppppplVar36[0x38])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x21f) < '\0') &&
              (ppppppplVar36[0x41] != (long ******)0x0)) &&
             (ppppppplVar36[0x41] != ppppppplVar36[0x45])) {
            HeapInterface::Free();
          }
          ppppppplVar35 = ppppppplVar36 + 0x5b;
          if (((*(char *)((long)ppppppplVar36 + 0x287) < '\0') &&
              (ppppppplVar36[0x4e] != (long ******)0x0)) &&
             (ppppppplVar36[0x4e] != ppppppplVar36[0x52])) {
            HeapInterface::Free();
          }
          ppppppplVar36 = ppppppplVar36 + 0x68;
        }
      }
      if ((ppppppplVar24 != (long *******)0x0) && (local_388 != ppppppplVar24)) {
        HeapInterface::Free(ppppppplVar24);
      }
      puVar14 = puVar14 + 3;
      puVar22 = puStack_850;
      puStack_850 = puVar16;
    } while( true );
  }
LAB_001bd809:
  if (local_858 == puStack_850) {
    lVar32 = *(long *)(local_8c8 + 0xb58);
    *(undefined8 *)(local_8c8 + 0xb60) = 0;
    *(undefined8 *)(local_8c8 + 0xb58) = 0;
    if (lVar32 != 0) {
      ref_counter_base::DecRef();
    }
  }
  else {
    uVar33 = 0;
    lVar32 = (long)puStack_850 - (long)local_858;
    if (lVar32 == 0x18) {
      FUN_0048dcd0(&local_818);
      FUN_0044afe0(&local_818,bVar31);
      FUN_00c31330(&local_818,0x20);
      local_718 = (byte *******)((ulong)local_718 & 0xffffffffffffff00);
      local_708 = (byte *******)CONCAT17(0x17,(undefined7)local_708);
      FUN_0048d690(&local_718,local_878,local_888 + local_878);
      FUN_003a8720(&local_3a8,&local_718,&local_818);
      local_868 = ppppppplStack_3a0;
      local_860 = local_3a8;
      if (-1 < (long)local_398) {
        local_868 = (long *******)(0x17 - (long)local_398._7_1_);
        local_860 = (long *******)&local_3a8;
      }
      FUN_003eaeb0(local_8c8 + 0x9e0,&local_868);
      if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
        HeapInterface::Free();
      }
      if (((long)local_708 < 0) && (local_718 != (byte *******)0x0)) {
        HeapInterface::Free();
      }
      if (-1 < local_801) {
        local_810._0_4_ = 0x17 - local_801;
      }
      if (*(char *)(local_8c8 + 0x9ff) < '\0') {
        iVar9 = (int)*(undefined8 *)(local_8c8 + 0x9f0);
      }
      else {
        iVar9 = 0x17 - *(char *)(local_8c8 + 0x9ff);
      }
      iVar41 = (int)local_888 + (int)local_810;
      if (iVar9 < (int)local_888 + (int)local_810) {
        iVar41 = iVar9;
      }
      iVar4 = *(int *)(local_8c8 + 0xb10);
      if (iVar41 < 0) {
        iVar41 = 0;
      }
      *(int *)(local_8c8 + 0x9e4) = iVar41;
      if (iVar4 != -1) {
        if (iVar4 < iVar9) {
          iVar9 = iVar4;
        }
        if (iVar9 < 0) {
          iVar9 = 0;
        }
        *(int *)(local_8c8 + 0xb10) = iVar9;
      }
      lVar32 = *(long *)(local_8c8 + 0xb58);
      *(undefined8 *)(local_8c8 + 0xb60) = 0;
      *(undefined8 *)(local_8c8 + 0xb58) = 0;
      if (lVar32 != 0) {
        ref_counter_base::DecRef();
      }
      if ((local_801 < '\0') && (CONCAT71(uStack_817,local_818) != 0)) {
        HeapInterface::Free();
      }
    }
    else {
      do {
        cVar40 = '\0';
        uVar20 = (int)((ulong)((long)puStack_850 + (-0x18 - (long)local_858)) >> 3) * -0x55555555 +
                 1U & 3;
        puVar14 = local_858;
        if (uVar20 == 0) goto LAB_001bd912;
        if (uVar20 != 1) {
          if (uVar20 != 2) {
            if (*(char *)((long)local_858 + 0x17) < '\0') {
              if ((ulong)(long)*(int *)(local_858 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar14 = (undefined8 *)*local_858;
            }
            else if (0x17U - (long)*(char *)((long)local_858 + 0x17) <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar14 + uVar33);
            puVar14 = local_858 + 3;
          }
          cVar3 = *(char *)((long)puVar14 + 0x17);
          if (cVar40 == '\0') {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            cVar40 = *(char *)((long)puVar22 + uVar33);
          }
          else {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
          }
          puVar14 = puVar14 + 3;
        }
        cVar3 = *(char *)((long)puVar14 + 0x17);
        if (cVar40 == '\0') {
          if (cVar3 < '\0') {
            if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
            puVar22 = (undefined8 *)*puVar14;
          }
          else {
            puVar22 = puVar14;
            if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
          }
          cVar40 = *(char *)((long)puVar22 + uVar33);
        }
        else {
          if (cVar3 < '\0') {
            if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
            puVar22 = (undefined8 *)*puVar14;
          }
          else {
            puVar22 = puVar14;
            if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
          }
          if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
        }
        for (puVar14 = puVar14 + 3; puStack_850 != puVar14; puVar14 = puVar14 + 0xc) {
LAB_001bd912:
          cVar3 = *(char *)((long)puVar14 + 0x17);
          if (cVar40 == '\0') {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x2f);
            if (cVar40 != '\0') goto LAB_001be540;
LAB_001bd954:
            puVar22 = puVar14 + 3;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 4) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar22;
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x47);
            if (cVar40 == '\0') goto LAB_001be576;
LAB_001bd985:
            puVar22 = puVar14 + 6;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 7) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[6];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x5f);
            if (cVar40 == '\0') goto LAB_001be5a7;
LAB_001bd9b3:
            puVar22 = puVar14 + 9;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 10) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[9];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
          }
          else {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x2f);
            if (cVar40 == '\0') goto LAB_001bd954;
LAB_001be540:
            puVar22 = puVar14 + 3;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 4) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar22;
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x47);
            if (cVar40 != '\0') goto LAB_001bd985;
LAB_001be576:
            puVar22 = puVar14 + 6;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 7) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[6];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x5f);
            if (cVar40 != '\0') goto LAB_001bd9b3;
LAB_001be5a7:
            puVar22 = puVar14 + 9;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 10) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[9];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
          }
        }
        uVar33 = uVar33 + 1;
      } while (uVar33 != 0x32);
      uVar33 = 0;
LAB_001bd9f0:
      if (*(char *)(local_8c8 + 0x9ff) < '\0') {
        lVar39 = *(long *)(local_8c8 + 0x9f0);
      }
      else {
        lVar39 = 0x17 - (long)*(char *)(local_8c8 + 0x9ff);
      }
      if (lVar39 - local_888 < uVar33) {
        FUN_0048ed50(&local_718,local_858,0,uVar33);
        local_818 = 0;
        local_801 = '\x17';
        FUN_0048d690(&local_818,local_878,local_888 + local_878);
        FUN_003a8720(&local_3a8,&local_818,&local_718);
        local_868 = ppppppplStack_3a0;
        local_860 = local_3a8;
        if (-1 < (long)local_398) {
          local_868 = (long *******)(0x17 - (long)local_398._7_1_);
          local_860 = (long *******)&local_3a8;
        }
        FUN_003eaeb0(local_8c8 + 0x9e0,&local_868);
        if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
          HeapInterface::Free();
        }
        if ((local_801 < '\0') && (CONCAT71(uStack_817,local_818) != 0)) {
          HeapInterface::Free();
        }
        if (((long)local_708 < 0) && (local_718 != (byte *******)0x0)) {
          HeapInterface::Free();
        }
        local_888._0_4_ = (int)local_888 + (int)uVar33;
        if (*(char *)(local_8c8 + 0x9ff) < '\0') {
          iVar9 = (int)*(undefined8 *)(local_8c8 + 0x9f0);
        }
        else {
          iVar9 = 0x17 - *(char *)(local_8c8 + 0x9ff);
        }
        if (iVar9 < (int)local_888) {
          local_888._0_4_ = iVar9;
        }
        iVar41 = *(int *)(local_8c8 + 0xb10);
        if ((int)local_888 < 0) {
          local_888._0_4_ = 0;
        }
        *(int *)(local_8c8 + 0x9e4) = (int)local_888;
        if (iVar41 != -1) {
          if (iVar41 < iVar9) {
            iVar9 = iVar41;
          }
          if (iVar9 < 0) {
            iVar9 = 0;
          }
          *(int *)(local_8c8 + 0xb10) = iVar9;
        }
        lVar32 = (long)puStack_850 - (long)local_858;
      }
      FUN_003ea130(local_8c8 + 0x2a0,&local_858,local_880 == (undefined8 *******)0x0,bVar31);
      if ((local_880 != (undefined8 *******)0x0) && (*(long *)(local_8c8 + 0xdd0) != 0)) {
        piVar1 = (int *)(*(long *)(local_8c8 + 0xdd0) + 8);
        do {
          iVar9 = *piVar1;
          if (iVar9 == 0) goto LAB_001bdb70;
          LOCK();
          iVar41 = *piVar1;
          if (iVar9 == iVar41) {
            *piVar1 = iVar9 + 1;
          }
          UNLOCK();
        } while (iVar9 != iVar41);
        lVar39 = *(long *)(local_8c8 + 0xdd8);
        lVar5 = *(long *)(local_8c8 + 0xdd0);
        if (lVar39 != 0) {
          local_3a8 = (long *******)local_380;
          ppppppplStack_3a0 = (long *******)0x0;
          local_398 = (undefined1 *)0x80000000000000ff;
          local_380[0] = 0;
          local_388 = local_3a8;
          FUN_00acf960(&local_3a8,"%s%d%s matches, %s%d sent","<col=ffff80>",uVar8,"</col>",
                       "<col=ff8080>",(lVar32 >> 3) * -0x5555555555555555);
          ppppppplVar24 = local_3a8;
          ppppppplVar37 = ppppppplStack_3a0;
          if (-1 < (long)local_398) {
            ppppppplVar24 = (long *******)&local_3a8;
            ppppppplVar37 = (long *******)(0x17 - (long)local_398._7_1_);
          }
          FUN_003e9e60(lVar39 + 0x30,ppppppplVar24,(long)ppppppplVar24 + (long)ppppppplVar37,
                       0x1bda4b);
          if ((long)local_398 < 0) {
            FUN_00aa7d70(&local_3a8);
          }
        }
        if (lVar5 != 0) {
          ref_counter_base::DecRef(lVar5);
        }
      }
    }
  }
LAB_001bdb70:
  if (local_838._0_8_ != 0) {
    HeapInterface::Free();
  }
  FUN_00491e00(&local_858);
  if (acStack_7e1[0] < '\0') {
    FUN_00aa7d70(&local_7f8);
  }
  if (local_778 < 0) {
    FUN_00aa7d70(&local_788);
  }
  return &DAT_015d3620;
LAB_001be092:
  if (puVar22 < local_848) {
    puStack_850 = puVar22 + 3;
    *(undefined1 *)((long)puVar22 + 0x17) = 0x17;
    *(undefined1 *)puVar22 = 0;
  }
  else {
    local_3a8 = (long *******)((ulong)local_3a8 & 0xffffffffffffff00);
    local_398 = (undefined1 *)CONCAT17(0x17,(undefined7)local_398);
    puStack_850 = puVar22;
    FUN_00125c60(&local_858,&local_3a8);
    if (((long)local_398 < 0) && (local_3a8 != (long *******)0x0)) {
      HeapInterface::Free();
    }
  }
  FUN_00493d20(puStack_850 + -3,plVar34[1],*plVar34 + plVar34[1]);
  FUN_00488b10(&local_718);
LAB_001bd7fa:
  plVar34 = plVar34 + 2;
  if ((long *)uVar6 == plVar34) goto LAB_001bd809;
  goto LAB_001bd650;
}


```

## `jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2` @ 001bf4c0
```c

/* WARNING: Type propagation algorithm not settling */

undefined * jag::packethandlers::PlayerInfo::PLAYER_INFO_DECODE_2(long *param_1,PacketCore *param_2)

{
  int *piVar1;
  char *pcVar2;
  char cVar3;
  int iVar4;
  long lVar5;
  undefined8 uVar6;
  ushort uVar7;
  uint uVar8;
  int iVar9;
  size_t sVar10;
  ulong uVar11;
  ulong uVar12;
  undefined8 uVar13;
  undefined8 *puVar14;
  undefined1 *puVar15;
  undefined8 *puVar16;
  void *pvVar17;
  undefined8 *******pppppppuVar18;
  byte *******pppppppbVar19;
  undefined8 extraout_RDX;
  uint uVar20;
  byte *pbVar21;
  undefined8 *puVar22;
  char *pcVar23;
  long *******ppppppplVar24;
  undefined8 *puVar25;
  long *******ppppppplVar26;
  byte *pbVar27;
  byte bVar28;
  char *pcVar29;
  ulong uVar30;
  byte bVar31;
  long lVar32;
  ulong uVar33;
  long *plVar34;
  long *******ppppppplVar35;
  long *******ppppppplVar36;
  long *******ppppppplVar37;
  char *pcVar38;
  long lVar39;
  char cVar40;
  int iVar41;
  byte *******__n;
  undefined1 auVar42 [16];
  ulong uStack_8f0;
  long lStack_8c8;
  long lStack_8b8;
  ulong uStack_888;
  undefined8 *******pppppppuStack_880;
  long lStack_878;
  long *******ppppppplStack_868;
  long *******ppppppplStack_860;
  undefined8 *puStack_858;
  undefined8 *puStack_850;
  undefined8 *puStack_848;
  undefined1 auStack_838 [16];
  undefined8 uStack_828;
  undefined1 uStack_818;
  undefined7 uStack_817;
  undefined8 uStack_810;
  char cStack_801;
  undefined8 *******pppppppuStack_7f8;
  long lStack_7f0;
  undefined7 uStack_7e8;
  char acStack_7e1 [9];
  undefined8 *******pppppppuStack_7d8;
  undefined1 auStack_7d0 [72];
  undefined1 *puStack_788;
  long lStack_780;
  undefined8 uStack_778;
  undefined1 *puStack_768;
  undefined1 auStack_760 [72];
  byte *******pppppppbStack_718;
  byte *******pppppppbStack_710;
  undefined8 uStack_708;
  byte *******pppppppbStack_6f8;
  byte ******appppppbStack_6f0 [104];
  byte ******ppppppbStack_3b0;
  long *******ppppppplStack_3a8;
  long *******ppppppplStack_3a0;
  undefined8 uStack_398;
  long *******ppppppplStack_388;
  undefined1 auStack_380 [832];
  undefined1 auStack_40 [16];
  
  lVar32 = param_2->position;
  pvVar17 = param_2->bufData;
  pppppppuStack_7f8 = (undefined8 *******)auStack_7d0;
  puStack_788 = auStack_760;
  uStack_778 = -0x7fffffffffffffc1;
  pbVar21 = (byte *)((long)pvVar17 + lVar32);
  lStack_780 = 0;
  auStack_760[0] = 0;
  uStack_7e8 = 0x3f;
  acStack_7e1[0] = -0x80;
  lStack_7f0 = 0;
  auStack_7d0[0] = 0;
  pppppppuStack_7d8 = pppppppuStack_7f8;
  puStack_768 = puStack_788;
  sVar10 = strlen((char *)pbVar21);
  if (sVar10 == 0) {
    pbVar21 = (byte *)((long)pvVar17 + lVar32 + 1);
    param_2->position = lVar32 + 1;
    sVar10 = strlen((char *)pbVar21);
    if (sVar10 != 0) {
LAB_001bf1a2:
      lStack_7f0 = 0;
      pppppppuVar18 = pppppppuStack_7f8;
      goto LAB_001be81b;
    }
    param_2->position = lVar32 + 2;
LAB_001bd02a:
    lStack_7f0 = 0;
    pppppppuVar18 = pppppppuStack_7f8;
LAB_001bd03e:
    *(undefined1 *)pppppppuVar18 = 0;
  }
  else {
    cVar40 = -0x80;
    if ((int)sVar10 != 0) {
      pbVar27 = pbVar21 + (sVar10 & 0xffffffff);
      if ((sVar10 & 1) != 0) {
        uVar33 = (ulong)*pbVar21;
        if ((byte)(*pbVar21 + 0x80) < 0x20) {
          uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
        }
        uVar8 = (uint)uVar33;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&puStack_788,(int)(char)(byte)uVar33);
          }
          else {
            if (uVar8 < 0x800) {
              lVar32 = lStack_780;
              if (-1 < uStack_778) {
                lVar32 = 0x17 - (long)uStack_778._7_1_;
              }
              bVar31 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&puStack_788,lVar32 + 2);
            }
            else {
              lVar32 = lStack_780;
              if (-1 < uStack_778) {
                lVar32 = 0x17 - (long)uStack_778._7_1_;
              }
              FUN_006b28b0(&puStack_788,lVar32 + 3);
              bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&puStack_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
            }
            FUN_00c29370(&puStack_788,(int)(char)bVar31);
            FUN_00c29370(&puStack_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          }
        }
        pbVar21 = pbVar21 + 1;
        goto joined_r0x001bee9c;
      }
      do {
        while( true ) {
          uVar33 = (ulong)*pbVar21;
          if ((byte)(*pbVar21 + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&puStack_788,(int)(char)(byte)uVar33);
            }
            else {
              if (uVar8 < 0x800) {
                lVar32 = lStack_780;
                if (-1 < uStack_778) {
                  lVar32 = 0x17 - (long)uStack_778._7_1_;
                }
                bVar31 = (byte)(uVar8 >> 6) | 0xc0;
                FUN_006b28b0(&puStack_788,lVar32 + 2);
              }
              else {
                lVar32 = lStack_780;
                if (-1 < uStack_778) {
                  lVar32 = 0x17 - (long)uStack_778._7_1_;
                }
                FUN_006b28b0(&puStack_788,lVar32 + 3);
                bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
                FUN_00c29370(&puStack_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
              }
              FUN_00c29370(&puStack_788,(int)(char)bVar31);
              FUN_00c29370(&puStack_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
            }
          }
          uVar33 = (ulong)pbVar21[1];
          if ((byte)(pbVar21[1] + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) break;
LAB_001be7c0:
          pbVar21 = pbVar21 + 2;
          if (pbVar21 == pbVar27) goto LAB_001be7d4;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            lVar32 = lStack_780;
            if (-1 < uStack_778) {
              lVar32 = 0x17 - (long)uStack_778._7_1_;
            }
            bVar31 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&puStack_788,lVar32 + 2);
          }
          else {
            lVar32 = lStack_780;
            if (-1 < uStack_778) {
              lVar32 = 0x17 - (long)uStack_778._7_1_;
            }
            FUN_006b28b0(&puStack_788,lVar32 + 3);
            bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&puStack_788,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&puStack_788,(int)(char)bVar31);
          FUN_00c29370(&puStack_788,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          goto LAB_001be7c0;
        }
        pbVar21 = pbVar21 + 2;
        FUN_00c29370(&puStack_788,(int)(char)(byte)uVar33);
joined_r0x001bee9c:
      } while (pbVar21 != pbVar27);
LAB_001be7d4:
      lVar32 = param_2->position;
      pvVar17 = param_2->bufData;
      cVar40 = acStack_7e1[0];
    }
    lVar32 = lVar32 + 1 + sVar10;
    pbVar21 = (byte *)((long)pvVar17 + lVar32);
    param_2->position = lVar32;
    sVar10 = strlen((char *)pbVar21);
    if (sVar10 == 0) {
      param_2->position = lVar32 + 1;
      if (cVar40 < '\0') goto LAB_001bd02a;
      acStack_7e1[0] = '\x17';
      pppppppuVar18 = &pppppppuStack_7f8;
      goto LAB_001bd03e;
    }
    if (cVar40 < '\0') goto LAB_001bf1a2;
    acStack_7e1[0] = '\x17';
    pppppppuVar18 = &pppppppuStack_7f8;
LAB_001be81b:
    *(undefined1 *)pppppppuVar18 = 0;
    pbVar27 = pbVar21 + (sVar10 & 0xffffffff);
    if ((sVar10 & 0xffffffff) != 0) {
      if (((long)pbVar27 - (long)pbVar21 & 1U) != 0) {
        uVar33 = (ulong)*pbVar21;
        if ((byte)(*pbVar21 + 0x80) < 0x20) {
          uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
        }
        uVar8 = (uint)uVar33;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&pppppppuStack_7f8,(int)(char)(byte)uVar33);
          }
          else {
            if (uVar8 < 0x800) {
              lVar32 = lStack_7f0;
              if (-1 < acStack_7e1[0]) {
                lVar32 = 0x17 - (long)acStack_7e1[0];
              }
              bVar31 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 2);
            }
            else {
              lVar32 = lStack_7f0;
              if (-1 < acStack_7e1[0]) {
                lVar32 = 0x17 - (long)acStack_7e1[0];
              }
              FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 3);
              uVar20 = (uint)(uVar33 >> 0xc) | 0xffffffe0;
              bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&pppppppuStack_7f8,(int)(char)uVar20,extraout_RDX,uVar20);
            }
            FUN_00c29370(&pppppppuStack_7f8,(int)(char)bVar31);
            FUN_00c29370(&pppppppuStack_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          }
        }
        pbVar21 = pbVar21 + 1;
        goto joined_r0x001bede1;
      }
      do {
        while( true ) {
          uVar33 = (ulong)*pbVar21;
          if ((byte)(*pbVar21 + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&pppppppuStack_7f8,(int)(char)(byte)uVar33);
            }
            else {
              if (uVar8 < 0x800) {
                lVar32 = lStack_7f0;
                if (-1 < acStack_7e1[0]) {
                  lVar32 = 0x17 - (long)acStack_7e1[0];
                }
                bVar31 = (byte)(uVar8 >> 6) | 0xc0;
                FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 2);
              }
              else {
                lVar32 = lStack_7f0;
                if (-1 < acStack_7e1[0]) {
                  lVar32 = 0x17 - (long)acStack_7e1[0];
                }
                FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 3);
                bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
                FUN_00c29370(&pppppppuStack_7f8,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
              }
              FUN_00c29370(&pppppppuStack_7f8,(int)(char)bVar31);
              FUN_00c29370(&pppppppuStack_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
            }
          }
          uVar33 = (ulong)pbVar21[1];
          if ((byte)(pbVar21[1] + 0x80) < 0x20) {
            uVar33 = (ulong)*(ushort *)(&DAT_0102a600 + uVar33 * 2);
          }
          uVar8 = (uint)uVar33;
          if (uVar8 != 0) break;
LAB_001be98c:
          pbVar21 = pbVar21 + 2;
joined_r0x001bede1:
          if (pbVar21 == pbVar27) goto LAB_001be9a0;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            lVar32 = lStack_7f0;
            if (-1 < acStack_7e1[0]) {
              lVar32 = 0x17 - (long)acStack_7e1[0];
            }
            bVar31 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 2);
          }
          else {
            lVar32 = lStack_7f0;
            if (-1 < acStack_7e1[0]) {
              lVar32 = 0x17 - (long)acStack_7e1[0];
            }
            FUN_006b28b0(&pppppppuStack_7f8,lVar32 + 3);
            bVar31 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&pppppppuStack_7f8,(int)(char)((byte)(uVar33 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&pppppppuStack_7f8,(int)(char)bVar31);
          FUN_00c29370(&pppppppuStack_7f8,(int)(char)((byte)uVar33 & 0x3f | 0x80));
          goto LAB_001be98c;
        }
        pbVar21 = pbVar21 + 2;
        FUN_00c29370(&pppppppuStack_7f8,(int)(char)(byte)uVar33);
      } while (pbVar21 != pbVar27);
    }
LAB_001be9a0:
    param_2->position = sVar10 + 1 + param_2->position;
  }
  if (acStack_7e1[0] < '\0') {
    if (lStack_7f0 != 0) {
      puVar15 = (undefined1 *)(lStack_7f0 + (long)pppppppuStack_7f8);
      goto LAB_001bdc0e;
    }
  }
  else if (0x17 - (long)acStack_7e1[0] != 0) {
    puVar15 = (undefined1 *)((0x17 - (long)acStack_7e1[0]) + (long)&pppppppuStack_7f8);
LAB_001bdc0e:
    bVar31 = puVar15[-1];
    bVar28 = 1;
    if (bVar31 < 0x2d) {
      bVar28 = ~(byte)(0x100900000000 >> (bVar31 & 0x3f)) & 1;
    }
    if ((bVar31 != 0x7e) && (bVar28 != 0)) {
      FUN_00c29370(&pppppppuStack_7f8,0x20);
    }
  }
  uVar8 = Packet::gT_unsigned_int(param_2);
  uVar7 = FUN_00121a30(param_2);
  puStack_848 = (undefined8 *)0x0;
  puStack_858 = (undefined8 *)0x0;
  puStack_850 = (undefined8 *)0x0;
  if (uVar7 != 0) {
    FUN_004890f0(&puStack_858);
    uVar20 = 0;
    do {
      while( true ) {
        pppppppbStack_718 = (byte *******)((ulong)pppppppbStack_718 & 0xffffffffffffff00);
        uStack_708 = (byte *******)CONCAT17(0x17,(undefined7)uStack_708);
        FUN_00afd8d0(param_2,&pppppppbStack_718);
        pppppppbVar19 = (byte *******)(0x17 - (long)uStack_708._7_1_);
        if ((long)uStack_708 < 0) {
          pppppppbVar19 = pppppppbStack_710;
        }
        if (pppppppbVar19 == (byte *******)0x0) {
          if (((long)uStack_708 < 0) && (pppppppbStack_718 != (byte *******)0x0)) {
            HeapInterface::Free();
          }
          goto LAB_001bd286;
        }
        if (puStack_850 < puStack_848) {
          *(undefined1 *)((long)puStack_850 + 0x17) = 0x17;
          *(undefined1 *)puStack_850 = 0;
          puStack_850 = puStack_850 + 3;
        }
        else {
          ppppppplStack_3a8 = (long *******)((ulong)ppppppplStack_3a8 & 0xffffffffffffff00);
          uStack_398 = (undefined1 *)CONCAT17(0x17,(undefined7)uStack_398);
          FUN_00125c60(&puStack_858,&ppppppplStack_3a8);
          if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
            HeapInterface::Free();
          }
        }
        lVar32 = 0x17 - (long)acStack_7e1[0];
        if (acStack_7e1[0] < '\0') {
          lVar32 = lStack_7f0;
        }
        if (lVar32 == 0) {
          FUN_00126110(puStack_850 + -3,&pppppppbStack_718);
        }
        else {
          pppppppbVar19 = (byte *******)(0x17 - (long)uStack_708._7_1_);
          if ((long)uStack_708 < 0) {
            pppppppbVar19 = pppppppbStack_710;
          }
          puVar14 = puStack_850 + -3;
          FUN_0048f440(puVar14,(byte *)((long)pppppppbVar19 + lVar32));
          pppppppbVar19 = (byte *******)&pppppppbStack_718;
          if ((long)uStack_708 < 0) {
            pppppppbVar19 = pppppppbStack_718;
          }
          pppppppuVar18 = &pppppppuStack_7f8;
          if (acStack_7e1[0] < '\0') {
            pppppppuVar18 = pppppppuStack_7f8;
          }
          FUN_00495e30(puVar14,"%s%s",pppppppuVar18,pppppppbVar19);
        }
        if (((long)uStack_708 < 0) && (pppppppbStack_718 != (byte *******)0x0)) break;
        uVar20 = uVar20 + 1;
        if (uVar7 == uVar20) goto LAB_001bd286;
      }
      uVar20 = uVar20 + 1;
      HeapInterface::Free();
    } while (uVar7 != uVar20);
  }
LAB_001bd286:
  lStack_8c8 = *(long *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
  if (acStack_7e1[0] < '\0') {
    pppppppuVar18 = pppppppuStack_7f8;
    lVar32 = lStack_7f0;
    if (lStack_7f0 == 0) {
      bVar31 = 0;
      pppppppuStack_880 = (undefined8 *******)0x0;
      goto LAB_001bd2e3;
    }
LAB_001bdc65:
    ppppppplStack_3a8 = (long *******)((ulong)ppppppplStack_3a8 & 0xffffffffffffff00);
    uStack_398 = (undefined1 *)CONCAT17(0x17,(undefined7)uStack_398);
    FUN_0048d690(&ppppppplStack_3a8,pppppppuVar18,(undefined1 *)((long)pppppppuVar18 + lVar32));
    FUN_00145d90(&ppppppplStack_3a8);
    auVar42 = FUN_00440aa0(lStack_8c8 + 8,&ppppppplStack_3a8);
    lVar32 = auVar42._0_8_;
    if ((lVar32 == auVar42._8_8_) || (lVar32 == *(long *)(lStack_8c8 + 0x10))) {
      bVar31 = 0;
    }
    else {
      bVar31 = *(byte *)(lVar32 + 0xf8) >> 2 & 1;
    }
    if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
      HeapInterface::Free();
    }
    lStack_8c8 = *(long *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
    if (acStack_7e1[0] < '\0') {
      if (lStack_7f0 == 0) {
        pppppppuStack_880 = (undefined8 *******)0x0;
      }
      else {
        pppppppuStack_880 = pppppppuStack_7f8;
      }
      goto LAB_001bd2e3;
    }
  }
  else {
    lVar32 = 0x17 - (long)acStack_7e1[0];
    pppppppuVar18 = &pppppppuStack_7f8;
    if (lVar32 != 0) goto LAB_001bdc65;
    bVar31 = 0;
  }
  pppppppuStack_880 = (undefined8 *******)0x0;
  if (acStack_7e1[0] != '\x17') {
    pppppppuStack_880 = &pppppppuStack_7f8;
  }
LAB_001bd2e3:
  FUN_00696c80(*(undefined8 *)(lStack_8c8 + 0xde8),*(undefined8 *)(lStack_8c8 + 0xdf0));
  *(undefined8 *)(lStack_8c8 + 0xdf8) = 0;
  if (puStack_858 != puStack_850) {
    puVar14 = puStack_858 + 3;
    uVar33 = (long)puStack_850 - (long)puVar14;
    puVar22 = puStack_858;
    do {
      uVar11 = FUN_00ae2750(puVar22,0x3a);
      if (uVar11 != 0xffffffffffffffff) {
        if (*(char *)((long)puVar22 + 0x17) < '\0') {
          puVar16 = (undefined8 *)*puVar22;
          uVar12 = puVar22[1];
        }
        else {
          uVar12 = 0x17 - (long)*(char *)((long)puVar22 + 0x17);
          puVar16 = puVar22;
        }
        pppppppbStack_718 = (byte *******)((ulong)pppppppbStack_718 & 0xffffffffffffff00);
        if (uVar11 <= uVar12) {
          uVar12 = uVar11;
        }
        uStack_708 = (byte *******)CONCAT17(0x17,(undefined7)uStack_708);
        FUN_0048d690(&pppppppbStack_718,puVar16,(long)puVar16 + uVar12);
        if (*(char *)((long)puVar22 + 0x17) < '\0') {
          puVar16 = (undefined8 *)*puVar22;
          lVar32 = puVar22[1];
        }
        else {
          lVar32 = 0x17 - (long)*(char *)((long)puVar22 + 0x17);
          puVar16 = puVar22;
        }
        ppppppplStack_3a8 = (long *******)((ulong)ppppppplStack_3a8 & 0xffffffffffffff00);
        uStack_398 = (undefined1 *)CONCAT17(0x17,(undefined7)uStack_398);
        FUN_0048d690(&ppppppplStack_3a8,(long)puVar16 + uVar11 + 2,lVar32 + (long)puVar16);
        pppppppbVar19 = (byte *******)&pppppppbStack_718;
        if ((long)uStack_708 < 0) {
          pppppppbVar19 = pppppppbStack_718;
        }
        bVar28 = *(byte *)pppppppbVar19;
        uStack_8f0 = 0x811c9dc5;
        if (bVar28 != 0) {
          uVar20 = 0x811c9dc5;
          do {
            pppppppbVar19 = (byte *******)((long)pppppppbVar19 + 1);
            uVar20 = uVar20 * 0x1000193 ^ (uint)bVar28;
            bVar28 = *(byte *)pppppppbVar19;
          } while (bVar28 != 0);
          uStack_8f0 = (ulong)uVar20;
        }
        uVar11 = *(ulong *)(lStack_8c8 + 0xdf0);
        uVar12 = uStack_8f0 % (uVar11 & 0xffffffff);
        puVar16 = *(undefined8 **)(*(long *)(lStack_8c8 + 0xde8) + uVar12 * 8);
        lStack_8b8 = uVar12 * 8;
        if (puVar16 != (undefined8 *)0x0) {
          pppppppbVar19 = (byte *******)&pppppppbStack_718;
          __n = (byte *******)(0x17 - (long)uStack_708._7_1_);
          if ((long)uStack_708 < 0) {
            pppppppbVar19 = pppppppbStack_718;
            __n = pppppppbStack_710;
          }
          do {
            if (*(char *)((long)puVar16 + 0x17) < '\0') {
              if ((byte *******)puVar16[1] == __n) {
                puVar25 = (undefined8 *)*puVar16;
LAB_001bd4df:
                iVar9 = memcmp(pppppppbVar19,puVar25,(size_t)__n);
                if (iVar9 == 0) goto LAB_001bd4ef;
              }
            }
            else {
              puVar25 = puVar16;
              if ((byte *******)(0x17 - (long)*(char *)((long)puVar16 + 0x17)) == __n)
              goto LAB_001bd4df;
            }
            puVar16 = (undefined8 *)puVar16[6];
          } while (puVar16 != (undefined8 *)0x0);
        }
        uVar11 = FUN_00aa5fe0(lStack_8c8 + 0xe00,uVar11 & 0xffffffff,
                              *(undefined4 *)(lStack_8c8 + 0xdf8),1);
        puVar16 = (undefined8 *)FUN_00c29480(0x38);
        uVar12 = uVar11 >> 0x20;
        FUN_0048dcd0(puVar16);
        *(undefined1 *)(puVar16 + 3) = 0;
        *(undefined1 *)((long)puVar16 + 0x2f) = 0x17;
        puVar16[6] = 0;
        if ((char)uVar11 == '\0') {
          pvVar17 = *(void **)(lStack_8c8 + 0xde8);
        }
        else {
          pvVar17 = (void *)FUN_00c29430();
          memset(pvVar17,0,uVar12 * 8);
          uVar11 = *(ulong *)(lStack_8c8 + 0xdf0);
          *(undefined8 *)((long)pvVar17 + uVar12 * 8) = 0xffffffffffffffff;
          if (uVar11 != 0) {
            lVar32 = *(long *)(lStack_8c8 + 0xde8);
            uVar30 = 0;
            do {
              puVar25 = (undefined8 *)(lVar32 + uVar30 * 8);
              pbVar21 = (byte *)*puVar25;
              if (pbVar21 != (byte *)0x0) {
                do {
                  pbVar27 = pbVar21;
                  if ((char)pbVar21[0x17] < '\0') {
                    pbVar27 = *(byte **)pbVar21;
                  }
                  bVar28 = *pbVar27;
                  uVar11 = 0x811c9dc5;
                  if (bVar28 != 0) {
                    uVar11 = 0x811c9dc5;
                    do {
                      pbVar27 = pbVar27 + 1;
                      uVar11 = (ulong)((int)uVar11 * 0x1000193 ^ (uint)bVar28);
                      bVar28 = *pbVar27;
                    } while (bVar28 != 0);
                  }
                  *puVar25 = *(undefined8 *)(pbVar21 + 0x30);
                  puVar25 = (undefined8 *)((long)pvVar17 + (uVar11 % uVar12) * 8);
                  *(undefined8 *)(pbVar21 + 0x30) = *puVar25;
                  lVar32 = *(long *)(lStack_8c8 + 0xde8);
                  *puVar25 = pbVar21;
                  puVar25 = (undefined8 *)(lVar32 + uVar30 * 8);
                  pbVar21 = (byte *)*puVar25;
                } while (pbVar21 != (byte *)0x0);
                uVar11 = *(ulong *)(lStack_8c8 + 0xdf0);
              }
              uVar30 = uVar30 + 1;
            } while (uVar30 < uVar11);
            if ((1 < uVar11) && (lVar32 != 0)) {
              HeapInterface::Free();
            }
          }
          *(ulong *)(lStack_8c8 + 0xdf0) = uVar12;
          *(void **)(lStack_8c8 + 0xde8) = pvVar17;
          lStack_8b8 = uStack_8f0 % uVar12 << 3;
        }
        puVar16[6] = *(undefined8 *)((long)pvVar17 + lStack_8b8);
        *(undefined8 **)(*(long *)(lStack_8c8 + 0xde8) + lStack_8b8) = puVar16;
        *(long *)(lStack_8c8 + 0xdf8) = *(long *)(lStack_8c8 + 0xdf8) + 1;
LAB_001bd4ef:
        FUN_00126110(puVar16 + 3,&ppppppplStack_3a8);
        FUN_00126110(puVar22,&pppppppbStack_718);
        if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
          HeapInterface::Free();
        }
        if (((long)uStack_708 < 0) && (pppppppbStack_718 != (byte *******)0x0)) {
          HeapInterface::Free();
        }
      }
      puVar22 = puVar22 + 3;
    } while ((undefined8 *)((uVar33 & 0xfffffffffffffff8) + (long)puVar14) != puVar22);
  }
  uStack_828 = 0;
  lVar32 = *(long *)(lStack_8c8 + 0x2a8);
  auStack_838 = (undefined1  [16])0x0;
  if (*(char *)(lVar32 + 0xaf) < '\0') {
    uStack_888 = *(ulong *)(lVar32 + 0xa0);
    lStack_878 = *(long *)(lVar32 + 0x98);
  }
  else {
    lStack_878 = lVar32 + 0x98;
    uStack_888 = 0x17 - (long)*(char *)(lVar32 + 0xaf);
  }
  if (*(char *)(lStack_8c8 + 0x9ff) < '\0') {
    uVar33 = *(ulong *)(lStack_8c8 + 0x9f0);
    lVar39 = *(long *)(lStack_8c8 + 0x9e8);
  }
  else {
    uVar33 = 0x17 - (long)*(char *)(lStack_8c8 + 0x9ff);
    lVar39 = lStack_8c8 + 0x9e8;
  }
  if (uVar33 < uStack_888) {
    ppppppplStack_3a0 = (long *******)0x0;
    ppppppplStack_3a8 = (long *******)0x0;
  }
  else {
    ppppppplStack_3a0 = (long *******)(lVar39 + uStack_888);
    ppppppplStack_3a8 = (long *******)(uVar33 - uStack_888);
  }
  FUN_004412f0(lVar32,auStack_838,&ppppppplStack_3a8);
  uVar6 = auStack_838._8_8_;
  if (auStack_838._0_8_ != auStack_838._8_8_) {
    plVar34 = (long *)auStack_838._0_8_;
LAB_001bd650:
    pcVar23 = (char *)plVar34[1];
    uStack_708 = &ppppppbStack_3b0;
    pcVar38 = pcVar23 + *plVar34;
    pcVar29 = pcVar23;
    pppppppbStack_718 = appppppbStack_6f0;
    pppppppbStack_710 = appppppbStack_6f0;
    pppppppbStack_6f8 = appppppbStack_6f0;
    puVar14 = puStack_858;
    puVar22 = puStack_850;
    if (pcVar23 != pcVar38) {
      do {
        if (*pcVar23 == ' ') {
          if (pcVar29 != pcVar23) {
            if (pppppppbStack_710 < uStack_708) {
              pppppppbStack_710[4] = (byte ******)(pppppppbStack_710 + 5);
              *pppppppbStack_710 = (byte ******)(pppppppbStack_710 + 5);
              pppppppbStack_710[2] = (byte ******)0x800000000000003f;
              pppppppbStack_710[1] = (byte ******)0x0;
              *(byte *)(pppppppbStack_710 + 5) = 0;
              pppppppbStack_710 = pppppppbStack_710 + 0xd;
            }
            else {
              uStack_398 = (undefined1 *)0x800000000000003f;
              ppppppplStack_3a0 = (long *******)0x0;
              auStack_380[0] = 0;
              ppppppplStack_3a8 = (long *******)auStack_380;
              ppppppplStack_388 = (long *******)auStack_380;
              FUN_00b08ef0(&pppppppbStack_718,&ppppppplStack_3a8);
              if ((long)uStack_398 < 0) {
                FUN_00aa7d70(&ppppppplStack_3a8);
              }
            }
            FUN_003e9e60(pppppppbStack_710 + -0xd,pcVar29,pcVar23);
          }
          uVar20 = (int)pcVar38 - (int)pcVar23 & 7;
          puVar14 = puStack_858;
          puVar22 = puStack_850;
          if (uVar20 != 0) {
            if (uVar20 != 1) {
              if (uVar20 != 2) {
                if (uVar20 != 3) {
                  if (uVar20 != 4) {
                    if (uVar20 != 5) {
                      if (uVar20 != 6) {
                        pcVar29 = pcVar23;
                        if (*pcVar23 != ' ') goto LAB_001bd6ae;
                        pcVar23 = pcVar23 + 1;
                      }
                      pcVar29 = pcVar23;
                      if (*pcVar23 != ' ') goto LAB_001bd6ae;
                      pcVar23 = pcVar23 + 1;
                    }
                    pcVar29 = pcVar23;
                    if (*pcVar23 != ' ') goto LAB_001bd6ae;
                    pcVar23 = pcVar23 + 1;
                  }
                  pcVar29 = pcVar23;
                  if (*pcVar23 != ' ') goto LAB_001bd6ae;
                  pcVar23 = pcVar23 + 1;
                }
                pcVar29 = pcVar23;
                if (*pcVar23 != ' ') goto LAB_001bd6ae;
                pcVar23 = pcVar23 + 1;
              }
              pcVar29 = pcVar23;
              if (*pcVar23 != ' ') goto LAB_001bd6ae;
              pcVar23 = pcVar23 + 1;
            }
            pcVar29 = pcVar23;
            if (*pcVar23 != ' ') goto LAB_001bd6ae;
            pcVar23 = pcVar23 + 1;
            if (pcVar38 == pcVar23) goto joined_r0x001bd6e8;
          }
          cVar40 = *pcVar23;
          pcVar2 = pcVar23;
          while ((((((pcVar23 = pcVar2, pcVar29 = pcVar2, cVar40 == ' ' &&
                     (pcVar23 = pcVar2 + 1, pcVar29 = pcVar23, *pcVar23 == ' ')) &&
                    (pcVar23 = pcVar2 + 2, pcVar29 = pcVar23, *pcVar23 == ' ')) &&
                   ((pcVar23 = pcVar2 + 3, pcVar29 = pcVar23, pcVar2[3] == ' ' &&
                    (pcVar23 = pcVar2 + 4, pcVar29 = pcVar23, pcVar2[4] == ' ')))) &&
                  (pcVar23 = pcVar2 + 5, pcVar29 = pcVar23, pcVar2[5] == ' ')) &&
                 ((pcVar23 = pcVar2 + 6, pcVar29 = pcVar23, pcVar2[6] == ' ' &&
                  (pcVar23 = pcVar2 + 7, pcVar29 = pcVar23, pcVar2[7] == ' '))))) {
            pcVar2 = pcVar2 + 8;
            if (pcVar38 == pcVar2) goto joined_r0x001bd6e8;
            cVar40 = *pcVar2;
          }
        }
        else {
          pcVar23 = pcVar23 + 1;
        }
LAB_001bd6ae:
      } while (pcVar23 != pcVar38);
      puVar14 = puStack_858;
      puVar22 = puStack_850;
      if (pcVar29 != pcVar23) {
        uVar13 = FUN_00489050(&pppppppbStack_718);
        FUN_003e9e60(uVar13,pcVar29,pcVar23);
        puVar14 = puStack_858;
        puVar22 = puStack_850;
      }
    }
joined_r0x001bd6e8:
    do {
      puVar16 = puStack_850;
      if (puVar14 == puStack_850) goto LAB_001be092;
      uStack_398 = auStack_40;
      ppppppplStack_3a8 = (long *******)auStack_380;
      ppppppplStack_3a0 = (long *******)auStack_380;
      ppppppplStack_388 = (long *******)auStack_380;
      puStack_850 = puVar22;
      FUN_00b08970(&ppppppplStack_3a8,puVar14,&DAT_00fb3ff6);
      ppppppplVar24 = ppppppplStack_3a0;
      ppppppplVar37 = ppppppplStack_3a8;
      if ((ppppppplStack_3a0 != ppppppplStack_3a8) && (pppppppbStack_710 != pppppppbStack_718)) {
        if ((char)*(byte *)((long)pppppppbStack_718 + 0x17) < '\0') {
          pppppppbVar19 = (byte *******)*pppppppbStack_718;
          pbVar21 = (byte *)((long)pppppppbVar19 + (long)pppppppbStack_718[1]);
        }
        else {
          pbVar21 = (byte *)((long)pppppppbStack_718 +
                            (0x17 - (long)(char)*(byte *)((long)pppppppbStack_718 + 0x17)));
          pppppppbVar19 = pppppppbStack_718;
        }
        if (*(char *)((long)ppppppplStack_3a8 + 0x17) < '\0') {
          ppppppplVar26 = (long *******)*ppppppplStack_3a8;
          lVar32 = (long)ppppppplStack_3a8[1] + (long)ppppppplVar26;
        }
        else {
          lVar32 = (long)ppppppplStack_3a8 +
                   (0x17 - (long)*(char *)((long)ppppppplStack_3a8 + 0x17));
          ppppppplVar26 = ppppppplStack_3a8;
        }
        sVar10 = lVar32 - (long)ppppppplVar26;
        if ((((long)sVar10 <= (long)pbVar21 - (long)pppppppbVar19) &&
            (iVar9 = memcmp(ppppppplVar26,pppppppbVar19,sVar10),
            (long)pbVar21 - (long)pppppppbVar19 <= (long)sVar10)) && (iVar9 == 0)) {
          FUN_00493d20(puVar14,plVar34[1],*plVar34 + plVar34[1]);
          FUN_00488b10(&ppppppplStack_3a8);
          FUN_00488b10(&pppppppbStack_718);
          goto LAB_001bd7fa;
        }
      }
      if (ppppppplVar37 != ppppppplVar24) {
        ppppppplVar26 = ppppppplVar37 + 0xd;
        uVar11 = (long)ppppppplVar24 - (long)ppppppplVar26;
        uVar33 = ((ulong)((long)(ppppppplVar37 +
                                (((uVar11 >> 3) * 0xec4ec4ec4ec4ec5 & 0x1fffffffffffffff) + 1) * 0xd
                                ) - (long)ppppppplVar26) >> 3) * 5;
        uVar20 = (uint)uVar33 & 7;
        ppppppplVar36 = ppppppplVar26;
        ppppppplVar35 = ppppppplVar37;
        if ((uVar33 & 7) != 0) {
          if (((*(char *)((long)ppppppplVar37 + 0x17) < '\0') &&
              (*ppppppplVar37 != (long ******)0x0)) && (*ppppppplVar37 != ppppppplVar37[4])) {
            HeapInterface::Free();
          }
          ppppppplVar36 = ppppppplVar37 + 0x1a;
          ppppppplVar35 = ppppppplVar26;
          if (uVar20 != 1) {
            ppppppplVar35 = ppppppplVar36;
            if (uVar20 != 2) {
              ppppppplVar24 = ppppppplVar26;
              if (uVar20 != 3) {
                ppppppplVar24 = ppppppplVar36;
                if (uVar20 != 4) {
                  ppppppplVar24 = ppppppplVar26;
                  if (uVar20 != 5) {
                    ppppppplVar24 = ppppppplVar36;
                    if (uVar20 != 6) {
                      if (((*(char *)((long)ppppppplVar37 + 0x7f) < '\0') &&
                          (*ppppppplVar26 != (long ******)0x0)) &&
                         (*ppppppplVar26 != ppppppplVar37[0x11])) {
                        HeapInterface::Free();
                      }
                      ppppppplVar24 = ppppppplVar37 + 0x27;
                      ppppppplVar26 = ppppppplVar36;
                    }
                    if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                        (*ppppppplVar26 != (long ******)0x0)) &&
                       (*ppppppplVar26 != ppppppplVar26[4])) {
                      HeapInterface::Free();
                    }
                    ppppppplVar36 = ppppppplVar24 + 0xd;
                  }
                  if (((*(char *)((long)ppppppplVar24 + 0x17) < '\0') &&
                      (*ppppppplVar24 != (long ******)0x0)) && (*ppppppplVar24 != ppppppplVar24[4]))
                  {
                    HeapInterface::Free();
                  }
                  ppppppplVar24 = ppppppplVar36 + 0xd;
                  ppppppplVar26 = ppppppplVar36;
                }
                if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                    (*ppppppplVar26 != (long ******)0x0)) && (*ppppppplVar26 != ppppppplVar26[4])) {
                  HeapInterface::Free();
                }
                ppppppplVar36 = ppppppplVar24 + 0xd;
              }
              if (((*(char *)((long)ppppppplVar24 + 0x17) < '\0') &&
                  (*ppppppplVar24 != (long ******)0x0)) && (*ppppppplVar24 != ppppppplVar24[4])) {
                HeapInterface::Free();
              }
              ppppppplVar35 = ppppppplVar36 + 0xd;
              ppppppplVar26 = ppppppplVar36;
            }
            if (((*(char *)((long)ppppppplVar26 + 0x17) < '\0') &&
                (*ppppppplVar26 != (long ******)0x0)) && (*ppppppplVar26 != ppppppplVar26[4])) {
              HeapInterface::Free();
            }
            ppppppplVar36 = ppppppplVar35 + 0xd;
          }
        }
        while( true ) {
          ppppppplVar24 = ppppppplStack_3a8;
          if (((*(char *)((long)ppppppplVar35 + 0x17) < '\0') &&
              (*ppppppplVar35 != (long ******)0x0)) && (*ppppppplVar35 != ppppppplVar35[4])) {
            HeapInterface::Free();
            ppppppplVar24 = ppppppplStack_3a8;
          }
          ppppppplStack_3a8 = ppppppplVar24;
          if (ppppppplVar36 ==
              ppppppplVar37 + (((uVar11 >> 3) * 0xec4ec4ec4ec4ec5 & 0x1fffffffffffffff) + 1) * 0xd)
          break;
          if (((*(char *)((long)ppppppplVar36 + 0x17) < '\0') &&
              (*ppppppplVar36 != (long ******)0x0)) && (*ppppppplVar36 != ppppppplVar36[4])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x7f) < '\0') &&
              (ppppppplVar36[0xd] != (long ******)0x0)) &&
             (ppppppplVar36[0xd] != ppppppplVar36[0x11])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0xe7) < '\0') &&
              (ppppppplVar36[0x1a] != (long ******)0x0)) &&
             (ppppppplVar36[0x1a] != ppppppplVar36[0x1e])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x14f) < '\0') &&
              (ppppppplVar36[0x27] != (long ******)0x0)) &&
             (ppppppplVar36[0x27] != ppppppplVar36[0x2b])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x1b7) < '\0') &&
              (ppppppplVar36[0x34] != (long ******)0x0)) &&
             (ppppppplVar36[0x34] != ppppppplVar36[0x38])) {
            HeapInterface::Free();
          }
          if (((*(char *)((long)ppppppplVar36 + 0x21f) < '\0') &&
              (ppppppplVar36[0x41] != (long ******)0x0)) &&
             (ppppppplVar36[0x41] != ppppppplVar36[0x45])) {
            HeapInterface::Free();
          }
          ppppppplVar35 = ppppppplVar36 + 0x5b;
          if (((*(char *)((long)ppppppplVar36 + 0x287) < '\0') &&
              (ppppppplVar36[0x4e] != (long ******)0x0)) &&
             (ppppppplVar36[0x4e] != ppppppplVar36[0x52])) {
            HeapInterface::Free();
          }
          ppppppplVar36 = ppppppplVar36 + 0x68;
        }
      }
      if ((ppppppplVar24 != (long *******)0x0) && (ppppppplStack_388 != ppppppplVar24)) {
        HeapInterface::Free(ppppppplVar24);
      }
      puVar14 = puVar14 + 3;
      puVar22 = puStack_850;
      puStack_850 = puVar16;
    } while( true );
  }
LAB_001bd809:
  if (puStack_858 == puStack_850) {
    lVar32 = *(long *)(lStack_8c8 + 0xb58);
    *(undefined8 *)(lStack_8c8 + 0xb60) = 0;
    *(undefined8 *)(lStack_8c8 + 0xb58) = 0;
    if (lVar32 != 0) {
      ref_counter_base::DecRef();
    }
  }
  else {
    uVar33 = 0;
    lVar32 = (long)puStack_850 - (long)puStack_858;
    if (lVar32 == 0x18) {
      FUN_0048dcd0(&uStack_818);
      FUN_0044afe0(&uStack_818,bVar31);
      FUN_00c31330(&uStack_818,0x20);
      pppppppbStack_718 = (byte *******)((ulong)pppppppbStack_718 & 0xffffffffffffff00);
      uStack_708 = (byte *******)CONCAT17(0x17,(undefined7)uStack_708);
      FUN_0048d690(&pppppppbStack_718,lStack_878,uStack_888 + lStack_878);
      FUN_003a8720(&ppppppplStack_3a8,&pppppppbStack_718,&uStack_818);
      ppppppplStack_868 = ppppppplStack_3a0;
      ppppppplStack_860 = ppppppplStack_3a8;
      if (-1 < (long)uStack_398) {
        ppppppplStack_868 = (long *******)(0x17 - (long)uStack_398._7_1_);
        ppppppplStack_860 = (long *******)&ppppppplStack_3a8;
      }
      FUN_003eaeb0(lStack_8c8 + 0x9e0,&ppppppplStack_868);
      if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
        HeapInterface::Free();
      }
      if (((long)uStack_708 < 0) && (pppppppbStack_718 != (byte *******)0x0)) {
        HeapInterface::Free();
      }
      if (-1 < cStack_801) {
        uStack_810._0_4_ = 0x17 - cStack_801;
      }
      if (*(char *)(lStack_8c8 + 0x9ff) < '\0') {
        iVar9 = (int)*(undefined8 *)(lStack_8c8 + 0x9f0);
      }
      else {
        iVar9 = 0x17 - *(char *)(lStack_8c8 + 0x9ff);
      }
      iVar41 = (int)uStack_888 + (int)uStack_810;
      if (iVar9 < (int)uStack_888 + (int)uStack_810) {
        iVar41 = iVar9;
      }
      iVar4 = *(int *)(lStack_8c8 + 0xb10);
      if (iVar41 < 0) {
        iVar41 = 0;
      }
      *(int *)(lStack_8c8 + 0x9e4) = iVar41;
      if (iVar4 != -1) {
        if (iVar4 < iVar9) {
          iVar9 = iVar4;
        }
        if (iVar9 < 0) {
          iVar9 = 0;
        }
        *(int *)(lStack_8c8 + 0xb10) = iVar9;
      }
      lVar32 = *(long *)(lStack_8c8 + 0xb58);
      *(undefined8 *)(lStack_8c8 + 0xb60) = 0;
      *(undefined8 *)(lStack_8c8 + 0xb58) = 0;
      if (lVar32 != 0) {
        ref_counter_base::DecRef();
      }
      if ((cStack_801 < '\0') && (CONCAT71(uStack_817,uStack_818) != 0)) {
        HeapInterface::Free();
      }
    }
    else {
      do {
        cVar40 = '\0';
        uVar20 = (int)((ulong)((long)puStack_850 + (-0x18 - (long)puStack_858)) >> 3) * -0x55555555
                 + 1U & 3;
        puVar14 = puStack_858;
        if (uVar20 == 0) goto LAB_001bd912;
        if (uVar20 != 1) {
          if (uVar20 != 2) {
            if (*(char *)((long)puStack_858 + 0x17) < '\0') {
              if ((ulong)(long)*(int *)(puStack_858 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar14 = (undefined8 *)*puStack_858;
            }
            else if (0x17U - (long)*(char *)((long)puStack_858 + 0x17) <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar14 + uVar33);
            puVar14 = puStack_858 + 3;
          }
          cVar3 = *(char *)((long)puVar14 + 0x17);
          if (cVar40 == '\0') {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            cVar40 = *(char *)((long)puVar22 + uVar33);
          }
          else {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
          }
          puVar14 = puVar14 + 3;
        }
        cVar3 = *(char *)((long)puVar14 + 0x17);
        if (cVar40 == '\0') {
          if (cVar3 < '\0') {
            if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
            puVar22 = (undefined8 *)*puVar14;
          }
          else {
            puVar22 = puVar14;
            if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
          }
          cVar40 = *(char *)((long)puVar22 + uVar33);
        }
        else {
          if (cVar3 < '\0') {
            if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
            puVar22 = (undefined8 *)*puVar14;
          }
          else {
            puVar22 = puVar14;
            if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
          }
          if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
        }
        for (puVar14 = puVar14 + 3; puStack_850 != puVar14; puVar14 = puVar14 + 0xc) {
LAB_001bd912:
          cVar3 = *(char *)((long)puVar14 + 0x17);
          if (cVar40 == '\0') {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x2f);
            if (cVar40 != '\0') goto LAB_001be540;
LAB_001bd954:
            puVar22 = puVar14 + 3;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 4) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar22;
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x47);
            if (cVar40 == '\0') goto LAB_001be576;
LAB_001bd985:
            puVar22 = puVar14 + 6;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 7) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[6];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x5f);
            if (cVar40 == '\0') goto LAB_001be5a7;
LAB_001bd9b3:
            puVar22 = puVar14 + 9;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 10) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[9];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
          }
          else {
            if (cVar3 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 1) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar14;
            }
            else {
              puVar22 = puVar14;
              if (0x17U - (long)cVar3 <= uVar33) goto LAB_001bd9f0;
            }
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x2f);
            if (cVar40 == '\0') goto LAB_001bd954;
LAB_001be540:
            puVar22 = puVar14 + 3;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 4) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)*puVar22;
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            if (*(char *)((long)puVar22 + uVar33) != cVar40) goto LAB_001bd9f0;
            lVar39 = (long)*(char *)((long)puVar14 + 0x47);
            if (cVar40 != '\0') goto LAB_001bd985;
LAB_001be576:
            puVar22 = puVar14 + 6;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 7) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[6];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
            lVar39 = (long)*(char *)((long)puVar14 + 0x5f);
            if (cVar40 != '\0') goto LAB_001bd9b3;
LAB_001be5a7:
            puVar22 = puVar14 + 9;
            if ((char)lVar39 < '\0') {
              if ((ulong)(long)*(int *)(puVar14 + 10) <= uVar33) goto LAB_001bd9f0;
              puVar22 = (undefined8 *)puVar14[9];
            }
            else if (0x17U - lVar39 <= uVar33) goto LAB_001bd9f0;
            cVar40 = *(char *)((long)puVar22 + uVar33);
          }
        }
        uVar33 = uVar33 + 1;
      } while (uVar33 != 0x32);
      uVar33 = 0;
LAB_001bd9f0:
      if (*(char *)(lStack_8c8 + 0x9ff) < '\0') {
        lVar39 = *(long *)(lStack_8c8 + 0x9f0);
      }
      else {
        lVar39 = 0x17 - (long)*(char *)(lStack_8c8 + 0x9ff);
      }
      if (lVar39 - uStack_888 < uVar33) {
        FUN_0048ed50(&pppppppbStack_718,puStack_858,0,uVar33);
        uStack_818 = 0;
        cStack_801 = '\x17';
        FUN_0048d690(&uStack_818,lStack_878,uStack_888 + lStack_878);
        FUN_003a8720(&ppppppplStack_3a8,&uStack_818,&pppppppbStack_718);
        ppppppplStack_868 = ppppppplStack_3a0;
        ppppppplStack_860 = ppppppplStack_3a8;
        if (-1 < (long)uStack_398) {
          ppppppplStack_868 = (long *******)(0x17 - (long)uStack_398._7_1_);
          ppppppplStack_860 = (long *******)&ppppppplStack_3a8;
        }
        FUN_003eaeb0(lStack_8c8 + 0x9e0,&ppppppplStack_868);
        if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
          HeapInterface::Free();
        }
        if ((cStack_801 < '\0') && (CONCAT71(uStack_817,uStack_818) != 0)) {
          HeapInterface::Free();
        }
        if (((long)uStack_708 < 0) && (pppppppbStack_718 != (byte *******)0x0)) {
          HeapInterface::Free();
        }
        uStack_888._0_4_ = (int)uStack_888 + (int)uVar33;
        if (*(char *)(lStack_8c8 + 0x9ff) < '\0') {
          iVar9 = (int)*(undefined8 *)(lStack_8c8 + 0x9f0);
        }
        else {
          iVar9 = 0x17 - *(char *)(lStack_8c8 + 0x9ff);
        }
        if (iVar9 < (int)uStack_888) {
          uStack_888._0_4_ = iVar9;
        }
        iVar41 = *(int *)(lStack_8c8 + 0xb10);
        if ((int)uStack_888 < 0) {
          uStack_888._0_4_ = 0;
        }
        *(int *)(lStack_8c8 + 0x9e4) = (int)uStack_888;
        if (iVar41 != -1) {
          if (iVar41 < iVar9) {
            iVar9 = iVar41;
          }
          if (iVar9 < 0) {
            iVar9 = 0;
          }
          *(int *)(lStack_8c8 + 0xb10) = iVar9;
        }
        lVar32 = (long)puStack_850 - (long)puStack_858;
      }
      FUN_003ea130(lStack_8c8 + 0x2a0,&puStack_858,pppppppuStack_880 == (undefined8 *******)0x0,
                   bVar31);
      if ((pppppppuStack_880 != (undefined8 *******)0x0) && (*(long *)(lStack_8c8 + 0xdd0) != 0)) {
        piVar1 = (int *)(*(long *)(lStack_8c8 + 0xdd0) + 8);
        do {
          iVar9 = *piVar1;
          if (iVar9 == 0) goto LAB_001bdb70;
          LOCK();
          iVar41 = *piVar1;
          if (iVar9 == iVar41) {
            *piVar1 = iVar9 + 1;
          }
          UNLOCK();
        } while (iVar9 != iVar41);
        lVar39 = *(long *)(lStack_8c8 + 0xdd8);
        lVar5 = *(long *)(lStack_8c8 + 0xdd0);
        if (lVar39 != 0) {
          ppppppplStack_3a8 = (long *******)auStack_380;
          ppppppplStack_3a0 = (long *******)0x0;
          uStack_398 = (undefined1 *)0x80000000000000ff;
          auStack_380[0] = 0;
          ppppppplStack_388 = ppppppplStack_3a8;
          FUN_00acf960(&ppppppplStack_3a8,"%s%d%s matches, %s%d sent","<col=ffff80>",uVar8,"</col>",
                       "<col=ff8080>",(lVar32 >> 3) * -0x5555555555555555);
          ppppppplVar24 = ppppppplStack_3a8;
          ppppppplVar37 = ppppppplStack_3a0;
          if (-1 < (long)uStack_398) {
            ppppppplVar24 = (long *******)&ppppppplStack_3a8;
            ppppppplVar37 = (long *******)(0x17 - (long)uStack_398._7_1_);
          }
          FUN_003e9e60(lVar39 + 0x30,ppppppplVar24,(long)ppppppplVar24 + (long)ppppppplVar37,
                       0x1bda4b);
          if ((long)uStack_398 < 0) {
            FUN_00aa7d70(&ppppppplStack_3a8);
          }
        }
        if (lVar5 != 0) {
          ref_counter_base::DecRef(lVar5);
        }
      }
    }
  }
LAB_001bdb70:
  if (auStack_838._0_8_ != 0) {
    HeapInterface::Free();
  }
  FUN_00491e00(&puStack_858);
  if (acStack_7e1[0] < '\0') {
    FUN_00aa7d70(&pppppppuStack_7f8);
  }
  if (uStack_778 < 0) {
    FUN_00aa7d70(&puStack_788);
  }
  return &DAT_015d3620;
LAB_001be092:
  if (puVar22 < puStack_848) {
    puStack_850 = puVar22 + 3;
    *(undefined1 *)((long)puVar22 + 0x17) = 0x17;
    *(undefined1 *)puVar22 = 0;
  }
  else {
    ppppppplStack_3a8 = (long *******)((ulong)ppppppplStack_3a8 & 0xffffffffffffff00);
    uStack_398 = (undefined1 *)CONCAT17(0x17,(undefined7)uStack_398);
    puStack_850 = puVar22;
    FUN_00125c60(&puStack_858,&ppppppplStack_3a8);
    if (((long)uStack_398 < 0) && (ppppppplStack_3a8 != (long *******)0x0)) {
      HeapInterface::Free();
    }
  }
  FUN_00493d20(puStack_850 + -3,plVar34[1],*plVar34 + plVar34[1]);
  FUN_00488b10(&pppppppbStack_718);
LAB_001bd7fa:
  plVar34 = plVar34 + 2;
  if ((long *)uVar6 == plVar34) goto LAB_001bd809;
  goto LAB_001bd650;
}


```
