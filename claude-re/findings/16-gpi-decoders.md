# GPI / NPC-info bit-loop decoders


## `jag::packethandlers::NPCInfo::NPC_INFO_thunk_worldentity` @ 001d54f0
```c

undefined *
jag::packethandlers::NPCInfo::NPC_INFO_thunk_worldentity
          (long *param_1,PacketCore *param_2,int *param_3)

{
  byte *pbVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined1 uVar4;
  byte bVar5;
  int iVar6;
  undefined8 uVar7;
  undefined8 uVar8;
  code *pcVar9;
  bool bVar10;
  undefined2 uVar11;
  ushort uVar12;
  ushort uVar13;
  uint uVar14;
  undefined8 *puVar15;
  undefined8 uVar16;
  long lVar17;
  undefined1 *puVar18;
  qword *pqVar19;
  ulong uVar20;
  ulong uVar21;
  byte *pbVar22;
  qword *pqVar23;
  undefined8 *puVar24;
  long *plVar25;
  byte bVar26;
  long lVar27;
  qword *pqVar28;
  qword *pqVar29;
  ulong uVar30;
  long *plVar31;
  long *plVar32;
  uint uVar33;
  void *pvVar34;
  ulong uVar35;
  long *plVar36;
  undefined1 *puVar37;
  long lVar38;
  long lVar39;
  undefined1 *puVar40;
  undefined1 *puVar41;
  long *plVar42;
  long lVar43;
  long lVar44;
  long lVar45;
  undefined8 *puVar46;
  uint local_e8;
  ulong local_e0;
  uint local_d8;
  undefined1 *local_d0;
  long local_c8;
  long local_b8;
  undefined4 local_8c;
  long local_88 [4];
  ulong local_68;
  undefined8 uStack_60;
  undefined8 local_58;
  undefined1 local_48;
  
  iVar6 = *param_3;
  lVar27 = *param_1;
  lVar17 = *(long *)((long)&__DT_RELA[0xd00].r_offset + lVar27);
  *(undefined4 *)(lVar17 + 0x150) = *(undefined4 *)(*(long *)(lVar17 + 0x148) + 0x10);
  if (iVar6 == 0) {
    puVar46 = *(undefined8 **)((long)&__DT_RELA[0xcfc].r_addend + lVar27);
    puVar15 = (undefined8 *)*puVar46;
    puVar46[1] = 0;
    *puVar46 = 0;
joined_r0x001d56db:
    if (puVar15 == (undefined8 *)0x0) goto LAB_001d554c;
  }
  else {
    puVar15 = (undefined8 *)FUN_00c29430(200);
    if (puVar15 == (undefined8 *)0x0) {
      lVar17 = param_2->position;
      pvVar34 = param_2->bufData;
      lVar38 = *param_1;
      lVar27 = lVar17 + 1;
      puVar46 = (undefined8 *)0x0;
      local_c8 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar38);
      param_2->position = lVar27;
      if (*(byte *)((long)pvVar34 + lVar17) < 2) goto LAB_001d56f0;
      plVar31 = *(long **)((long)&__DT_RELA[0xcfc].r_addend + lVar38);
      puVar46 = (undefined8 *)0x0;
LAB_001d56c8:
      puVar15 = (undefined8 *)*plVar31;
      plVar31[1] = (long)puVar46;
      *plVar31 = 0;
      goto joined_r0x001d56db;
    }
    puVar46 = puVar15 + 4;
    *(undefined1 *)(puVar15 + 5) = 0;
    *(undefined1 *)((long)puVar15 + 0x3f) = 0x17;
    puVar15[9] = &PTR_FUN_0136b598;
    puVar15[0xd] = 0;
    puVar15[0xe] = 0x400000003f800000;
    *(undefined4 *)(puVar15 + 0xf) = 0;
    puVar15[0xb] = &DAT_01393d40;
    puVar15[1] = 0x100000001;
    puVar15[2] = 1;
    *puVar15 = &PTR_FUN_01363cb8;
    puVar15[0xc] = 1;
    puVar15[0x10] = 0;
    puVar15[0x11] = 0;
    puVar15[0x12] = 0;
    puVar15[0x13] = 0;
    puVar15[0x14] = 0;
    puVar15[0x15] = 0;
    puVar15[3] = puVar46;
    lVar17 = param_2->position;
    lVar38 = *param_1;
    pvVar34 = param_2->bufData;
    lVar27 = lVar17 + 1;
    local_c8 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar38);
    param_2->position = lVar27;
    if (*(byte *)((long)pvVar34 + lVar17) < 2) {
LAB_001d56f0:
      param_2->position = lVar17 + 2;
      bVar26 = *(byte *)((long)pvVar34 + lVar27);
      lVar27 = local_c8 + 0xd0;
      *(byte *)(puVar46 + 4) = bVar26 & 1;
      uVar14 = Packet::gT_unsigned_int(param_2);
      *(uint *)((long)puVar46 + 0x94) = uVar14;
      uVar16 = Packet::gT_ulong(param_2);
      *puVar46 = uVar16;
      FUN_00afd8d0(param_2,puVar46 + 1);
      uVar11 = FUN_00121a00(param_2);
      *(undefined2 *)((long)puVar46 + 0x22) = uVar11;
      uVar14 = Packet::gT_unsigned_int(param_2);
      *(uint *)(puVar46 + 0x13) = uVar14;
      uVar16 = Packet::gT_ulong(param_2);
      puVar46[0x14] = uVar16;
      uVar12 = FUN_00121a30(param_2);
      if (uVar12 != 0) {
        local_e0 = puVar46[0xc];
        if (puVar46[0xd] != local_e0) {
          uVar30 = local_e0 + 0xb0;
          uVar35 = (((puVar46[0xd] - uVar30 >> 4) * 0xe8ba2e8ba2e8ba3 & 0xfffffffffffffff) + 1) *
                   0xb0 + local_e0;
          uVar14 = (int)(uVar35 - uVar30 >> 4) * -0x45d1745d & 7;
          uVar20 = uVar30;
          uVar21 = local_e0;
          if (uVar14 != 0) {
            FUN_001ab480(local_e0);
            uVar20 = local_e0 + 0x160;
            uVar21 = uVar30;
            if (uVar14 != 1) {
              uVar21 = uVar20;
              if (uVar14 != 2) {
                uVar21 = uVar30;
                if (uVar14 != 3) {
                  uVar21 = uVar20;
                  if (uVar14 != 4) {
                    uVar21 = uVar30;
                    if (uVar14 != 5) {
                      uVar21 = uVar20;
                      if (uVar14 != 6) {
                        FUN_001ab480(uVar30);
                        uVar21 = local_e0 + 0x210;
                        uVar30 = uVar20;
                      }
                      FUN_001ab480(uVar30);
                      uVar20 = uVar21 + 0xb0;
                    }
                    FUN_001ab480(uVar21);
                    uVar21 = uVar20 + 0xb0;
                    uVar30 = uVar20;
                  }
                  FUN_001ab480(uVar30);
                  uVar20 = uVar21 + 0xb0;
                }
                FUN_001ab480(uVar21);
                uVar21 = uVar20 + 0xb0;
                uVar30 = uVar20;
              }
              FUN_001ab480(uVar30);
              uVar20 = uVar21 + 0xb0;
            }
          }
          for (; FUN_001ab480(uVar21), uVar20 != uVar35; uVar20 = uVar20 + 0x580) {
            FUN_001ab480(uVar20);
            FUN_001ab480(uVar20 + 0xb0);
            FUN_001ab480(uVar20 + 0x160);
            FUN_001ab480(uVar20 + 0x210);
            FUN_001ab480(uVar20 + 0x2c0);
            uVar21 = uVar20 + 0x4d0;
            FUN_001ab480(uVar20 + 0x370);
            FUN_001ab480(uVar20 + 0x420);
          }
          local_e0 = puVar46[0xc];
        }
        uVar30 = puVar46[0xe];
        puVar46[0xd] = local_e0;
        if ((ulong)(((long)(uVar30 - local_e0) >> 4) * 0x2e8ba2e8ba2e8ba3) < (ulong)uVar12) {
          uVar30 = thunk_FUN_00c29480();
          lVar17 = puVar46[0xc];
          local_e0 = uVar30;
          if (puVar46[0xd] != lVar17) {
            uVar20 = ((ulong)(puVar46[0xd] - (lVar17 + 0xb0)) >> 4) * 0xe8ba2e8ba2e8ba3 &
                     0xfffffffffffffff;
            uVar21 = uVar30;
            puVar24 = (undefined8 *)(lVar17 + 8);
            do {
              *(undefined8 *)(uVar21 + 8) = 0;
              *(undefined8 *)(uVar21 + 0x10) = 0;
              uVar35 = uVar21 + 0xb0;
              uVar16 = puVar24[1];
              *(undefined8 *)(uVar21 + 8) = *puVar24;
              *(undefined8 *)(uVar21 + 0x10) = uVar16;
              puVar24[1] = 0;
              *puVar24 = 0;
              *(undefined1 *)(uVar21 + 0x18) = 0;
              *(undefined1 *)(uVar21 + 0x2f) = 0x17;
              local_68 = *(ulong *)(uVar21 + 0x18);
              uStack_60 = *(undefined8 *)(uVar21 + 0x20);
              local_58 = *(undefined8 *)(uVar21 + 0x28);
              uVar16 = puVar24[3];
              *(undefined8 *)(uVar21 + 0x18) = puVar24[2];
              *(undefined8 *)(uVar21 + 0x20) = uVar16;
              *(undefined8 *)(uVar21 + 0x28) = puVar24[4];
              uVar11 = *(undefined2 *)((long)puVar24 + 0x2a);
              puVar24[2] = local_68;
              puVar24[3] = uStack_60;
              puVar24[4] = local_58;
              *(undefined1 *)((long)puVar24 + 0x27) = 0x17;
              *(undefined1 *)(uVar21 + 0x30) = *(undefined1 *)(puVar24 + 5);
              uVar4 = *(undefined1 *)((long)puVar24 + 0x2c);
              *(undefined2 *)(uVar21 + 0x32) = uVar11;
              *(undefined1 *)(uVar21 + 0x34) = uVar4;
              *(undefined1 *)(uVar21 + 0x35) = *(undefined1 *)((long)puVar24 + 0x2d);
              *(undefined4 *)(uVar21 + 0x38) = *(undefined4 *)(puVar24 + 6);
              uVar4 = *(undefined1 *)((long)puVar24 + 0x34);
              *(undefined8 *)(uVar21 + 0x60) = 0;
              *(undefined ***)(uVar21 + 0x40) = &PTR_FUN_0136b598;
              *(undefined1 *)(uVar21 + 0x3c) = uVar4;
              uVar16 = puVar24[0xc];
              *(undefined8 **)(uVar21 + 0x50) = &DAT_01393d40;
              *(undefined4 *)(uVar21 + 0x70) = 0;
              *(undefined8 *)(uVar21 + 0x58) = 1;
              uVar7 = puVar24[9];
              *(undefined8 *)(uVar21 + 0x68) = uVar16;
              uVar2 = *(undefined4 *)(uVar21 + 0x68);
              uVar16 = puVar24[10];
              uVar3 = *(undefined4 *)(uVar21 + 0x6c);
              *(undefined8 *)(uVar21 + 0x68) = puVar24[0xc];
              *(undefined4 *)(uVar21 + 0x70) = *(undefined4 *)(puVar24 + 0xd);
              *(undefined4 *)(puVar24 + 0xc) = uVar2;
              *(undefined4 *)(puVar24 + 0xd) = 0;
              *(undefined4 *)((long)puVar24 + 100) = uVar3;
              uVar8 = puVar24[0x13];
              *(undefined8 *)(uVar21 + 0x50) = uVar7;
              puVar24[9] = &DAT_01393d40;
              *(undefined8 *)(uVar21 + 0x58) = uVar16;
              puVar24[10] = 1;
              uVar16 = *(undefined8 *)(uVar21 + 0x60);
              *(undefined8 *)(uVar21 + 0x60) = puVar24[0xb];
              puVar24[0xb] = uVar16;
              *(undefined8 *)(uVar21 + 0xa0) = uVar8;
              *(undefined ***)(uVar21 + 0x78) = &PTR_FUN_0136b598;
              *(undefined8 *)(uVar21 + 0x98) = 0;
              *(undefined8 *)(uVar21 + 0x90) = 1;
              *(undefined8 **)(uVar21 + 0x88) = &DAT_01393d40;
              *(undefined4 *)(uVar21 + 0xa8) = 0;
              uVar2 = *(undefined4 *)(uVar21 + 0xa0);
              uVar16 = puVar24[0x10];
              uVar3 = *(undefined4 *)(uVar21 + 0xa4);
              uVar7 = puVar24[0x11];
              *(undefined8 *)(uVar21 + 0xa0) = puVar24[0x13];
              *(undefined4 *)(uVar21 + 0xa8) = *(undefined4 *)(puVar24 + 0x14);
              *(undefined4 *)(puVar24 + 0x13) = uVar2;
              *(undefined4 *)(puVar24 + 0x14) = 0;
              *(undefined4 *)((long)puVar24 + 0x9c) = uVar3;
              *(undefined8 *)(uVar21 + 0x88) = uVar16;
              puVar24[0x10] = &DAT_01393d40;
              *(undefined8 *)(uVar21 + 0x90) = uVar7;
              puVar24[0x11] = 1;
              uVar16 = *(undefined8 *)(uVar21 + 0x98);
              *(undefined8 *)(uVar21 + 0x98) = puVar24[0x12];
              puVar24[0x12] = uVar16;
              uVar21 = uVar35;
              puVar24 = puVar24 + 0x16;
            } while (uVar35 != uVar30 + 0xb0 + uVar20 * 0xb0);
            lVar17 = puVar46[0xc];
            local_e0 = (uVar20 + 1) * 0xb0 + uVar30;
            if (puVar46[0xd] != lVar17) {
              lVar38 = lVar17 + 0xb0;
              lVar45 = ((((ulong)(puVar46[0xd] - lVar38) >> 4) * 0xe8ba2e8ba2e8ba3 &
                        0xfffffffffffffff) + 1) * 0xb0 + lVar17;
              uVar14 = (int)((ulong)(lVar45 - lVar38) >> 4) * -0x45d1745d & 7;
              lVar39 = lVar17;
              lVar43 = lVar38;
              if (uVar14 != 0) {
                FUN_001ab480();
                lVar43 = lVar17 + 0x160;
                lVar39 = lVar38;
                if (uVar14 != 1) {
                  lVar44 = lVar38;
                  lVar39 = lVar43;
                  if (uVar14 != 2) {
                    lVar44 = lVar43;
                    if (uVar14 != 3) {
                      lVar39 = lVar38;
                      if (uVar14 != 4) {
                        lVar39 = lVar43;
                        if (uVar14 != 5) {
                          lVar39 = lVar38;
                          if (uVar14 != 6) {
                            FUN_001ab480(lVar38);
                            lVar39 = lVar43;
                            lVar43 = lVar17 + 0x210;
                          }
                          lVar38 = lVar43;
                          FUN_001ab480(lVar39);
                          lVar39 = lVar38 + 0xb0;
                        }
                        FUN_001ab480(lVar38);
                        lVar43 = lVar39 + 0xb0;
                      }
                      lVar38 = lVar43;
                      FUN_001ab480(lVar39);
                      lVar44 = lVar38 + 0xb0;
                    }
                    FUN_001ab480(lVar38);
                    lVar39 = lVar44 + 0xb0;
                  }
                  FUN_001ab480(lVar44);
                  lVar43 = lVar39 + 0xb0;
                }
              }
              for (; FUN_001ab480(lVar39), lVar43 != lVar45; lVar43 = lVar43 + 0x580) {
                FUN_001ab480(lVar43);
                FUN_001ab480(lVar43 + 0xb0);
                FUN_001ab480(lVar43 + 0x160);
                FUN_001ab480(lVar43 + 0x210);
                FUN_001ab480(lVar43 + 0x2c0);
                lVar39 = lVar43 + 0x4d0;
                FUN_001ab480(lVar43 + 0x370);
                FUN_001ab480(lVar43 + 0x420);
              }
              lVar17 = puVar46[0xc];
            }
          }
          if (lVar17 != 0) {
            HeapInterface::Free();
          }
          puVar46[0xc] = uVar30;
          uVar30 = uVar30 + (ulong)uVar12 * 0xb0;
          puVar46[0xd] = local_e0;
          puVar46[0xe] = uVar30;
        }
        uVar14 = 0;
        bVar26 = bVar26 >> 2;
        do {
          if (local_e0 < uVar30) {
            FUN_004e5ab0(local_e0,param_2,bVar26 & 1,lVar27);
            uVar21 = puVar46[0xd] + 0xb0;
            puVar46[0xd] = uVar21;
          }
          else {
            uVar30 = puVar46[0xc];
            local_b8 = (long)(local_e0 - uVar30) >> 4;
            if (local_b8 * 0x2e8ba2e8ba2e8ba3 == 0) {
              local_b8 = 0xb0;
LAB_001d5a46:
              lVar17 = FUN_00c29480(local_b8);
              local_e0 = puVar46[0xd];
              local_b8 = lVar17 + local_b8;
              uVar30 = puVar46[0xc];
              uVar21 = lVar17 + 0xb0;
            }
            else {
              if (local_b8 * 0x5d1745d1745d1746 != 0) {
                local_b8 = local_b8 << 5;
                goto LAB_001d5a46;
              }
              uVar21 = 0xb0;
              local_b8 = 0;
              lVar17 = 0;
            }
            lVar38 = lVar17;
            if (uVar30 != local_e0) {
              uVar21 = (local_e0 - (uVar30 + 0xb0) >> 4) * 0xe8ba2e8ba2e8ba3 & 0xfffffffffffffff;
              puVar24 = (undefined8 *)(uVar30 + 8);
              do {
                *(undefined8 *)(lVar38 + 8) = 0;
                *(undefined8 *)(lVar38 + 0x10) = 0;
                lVar39 = lVar38 + 0xb0;
                uVar16 = puVar24[1];
                *(undefined8 *)(lVar38 + 8) = *puVar24;
                *(undefined8 *)(lVar38 + 0x10) = uVar16;
                puVar24[1] = 0;
                *puVar24 = 0;
                *(undefined1 *)(lVar38 + 0x18) = 0;
                *(undefined1 *)(lVar38 + 0x2f) = 0x17;
                local_68 = *(ulong *)(lVar38 + 0x18);
                uStack_60 = *(undefined8 *)(lVar38 + 0x20);
                local_58 = *(undefined8 *)(lVar38 + 0x28);
                uVar16 = puVar24[3];
                *(undefined8 *)(lVar38 + 0x18) = puVar24[2];
                *(undefined8 *)(lVar38 + 0x20) = uVar16;
                *(undefined8 *)(lVar38 + 0x28) = puVar24[4];
                uVar11 = *(undefined2 *)((long)puVar24 + 0x2a);
                puVar24[2] = local_68;
                puVar24[3] = uStack_60;
                puVar24[4] = local_58;
                *(undefined1 *)((long)puVar24 + 0x27) = 0x17;
                *(undefined1 *)(lVar38 + 0x30) = *(undefined1 *)(puVar24 + 5);
                uVar4 = *(undefined1 *)((long)puVar24 + 0x2c);
                *(undefined2 *)(lVar38 + 0x32) = uVar11;
                *(undefined1 *)(lVar38 + 0x34) = uVar4;
                *(undefined1 *)(lVar38 + 0x35) = *(undefined1 *)((long)puVar24 + 0x2d);
                *(undefined4 *)(lVar38 + 0x38) = *(undefined4 *)(puVar24 + 6);
                uVar4 = *(undefined1 *)((long)puVar24 + 0x34);
                *(undefined8 *)(lVar38 + 0x60) = 0;
                *(undefined ***)(lVar38 + 0x40) = &PTR_FUN_0136b598;
                *(undefined1 *)(lVar38 + 0x3c) = uVar4;
                uVar16 = puVar24[0xc];
                *(undefined8 **)(lVar38 + 0x50) = &DAT_01393d40;
                *(undefined4 *)(lVar38 + 0x70) = 0;
                *(undefined8 *)(lVar38 + 0x58) = 1;
                uVar7 = puVar24[9];
                *(undefined8 *)(lVar38 + 0x68) = uVar16;
                uVar2 = *(undefined4 *)(lVar38 + 0x68);
                uVar16 = puVar24[10];
                uVar3 = *(undefined4 *)(lVar38 + 0x6c);
                *(undefined8 *)(lVar38 + 0x68) = puVar24[0xc];
                *(undefined4 *)(lVar38 + 0x70) = *(undefined4 *)(puVar24 + 0xd);
                *(undefined4 *)(puVar24 + 0xc) = uVar2;
                *(undefined4 *)(puVar24 + 0xd) = 0;
                *(undefined4 *)((long)puVar24 + 100) = uVar3;
                uVar8 = puVar24[0x13];
                *(undefined8 *)(lVar38 + 0x50) = uVar7;
                puVar24[9] = &DAT_01393d40;
                *(undefined8 *)(lVar38 + 0x58) = uVar16;
                puVar24[10] = 1;
                uVar16 = *(undefined8 *)(lVar38 + 0x60);
                *(undefined8 *)(lVar38 + 0x60) = puVar24[0xb];
                puVar24[0xb] = uVar16;
                *(undefined ***)(lVar38 + 0x78) = &PTR_FUN_0136b598;
                *(undefined8 *)(lVar38 + 0x98) = 0;
                *(undefined8 *)(lVar38 + 0xa0) = uVar8;
                *(undefined8 *)(lVar38 + 0x90) = 1;
                *(undefined8 **)(lVar38 + 0x88) = &DAT_01393d40;
                *(undefined4 *)(lVar38 + 0xa8) = 0;
                uVar2 = *(undefined4 *)(lVar38 + 0xa0);
                uVar16 = puVar24[0x10];
                uVar3 = *(undefined4 *)(lVar38 + 0xa4);
                uVar7 = puVar24[0x11];
                *(undefined8 *)(lVar38 + 0xa0) = puVar24[0x13];
                *(undefined4 *)(lVar38 + 0xa8) = *(undefined4 *)(puVar24 + 0x14);
                *(undefined4 *)(puVar24 + 0x13) = uVar2;
                *(undefined4 *)(puVar24 + 0x14) = 0;
                *(undefined4 *)((long)puVar24 + 0x9c) = uVar3;
                *(undefined8 *)(lVar38 + 0x88) = uVar16;
                puVar24[0x10] = &DAT_01393d40;
                *(undefined8 *)(lVar38 + 0x90) = uVar7;
                puVar24[0x11] = 1;
                uVar16 = *(undefined8 *)(lVar38 + 0x98);
                *(undefined8 *)(lVar38 + 0x98) = puVar24[0x12];
                puVar24[0x12] = uVar16;
                puVar24 = puVar24 + 0x16;
                lVar38 = lVar39;
              } while (lVar39 != lVar17 + 0xb0 + uVar21 * 0xb0);
              lVar38 = (uVar21 + 1) * 0xb0 + lVar17;
              uVar21 = lVar38 + 0xb0;
            }
            FUN_004e5ab0(lVar38,param_2,bVar26 & 1,lVar27);
            lVar38 = puVar46[0xc];
            if (puVar46[0xd] != lVar38) {
              lVar39 = lVar38 + 0xb0;
              lVar43 = lVar38 + ((((ulong)(puVar46[0xd] - lVar39) >> 4) * 0xe8ba2e8ba2e8ba3 &
                                 0xfffffffffffffff) + 1) * 0xb0;
              uVar30 = ((ulong)(lVar43 - lVar39) >> 4) * 3;
              uVar33 = (uint)uVar30 & 7;
              lVar45 = lVar38;
              lVar44 = lVar39;
              if ((uVar30 & 7) != 0) {
                FUN_001ab480();
                lVar44 = lVar38 + 0x160;
                lVar45 = lVar39;
                if (uVar33 != 1) {
                  if (uVar33 != 2) {
                    lVar45 = lVar44;
                    if (uVar33 != 3) {
                      lVar45 = lVar39;
                      if (uVar33 != 4) {
                        lVar45 = lVar44;
                        if (uVar33 != 5) {
                          lVar45 = lVar39;
                          if (uVar33 != 6) {
                            FUN_001ab480(lVar39);
                            lVar45 = lVar44;
                            lVar44 = lVar38 + 0x210;
                          }
                          lVar39 = lVar44;
                          FUN_001ab480(lVar45);
                          lVar45 = lVar39 + 0xb0;
                        }
                        FUN_001ab480(lVar39);
                        lVar44 = lVar45 + 0xb0;
                      }
                      lVar39 = lVar44;
                      FUN_001ab480(lVar45);
                      lVar45 = lVar39 + 0xb0;
                    }
                    FUN_001ab480(lVar39);
                    lVar44 = lVar45 + 0xb0;
                  }
                  FUN_001ab480(lVar45);
                  lVar45 = lVar44;
                  lVar44 = lVar44 + 0xb0;
                }
              }
              for (; FUN_001ab480(lVar45), lVar43 != lVar44; lVar44 = lVar44 + 0x580) {
                FUN_001ab480(lVar44);
                FUN_001ab480(lVar44 + 0xb0);
                FUN_001ab480(lVar44 + 0x160);
                FUN_001ab480(lVar44 + 0x210);
                FUN_001ab480(lVar44 + 0x2c0);
                FUN_001ab480(lVar44 + 0x370);
                FUN_001ab480(lVar44 + 0x420);
                lVar45 = lVar44 + 0x4d0;
              }
              lVar38 = puVar46[0xc];
            }
            if (lVar38 != 0) {
              HeapInterface::Free();
            }
            puVar46[0xc] = lVar17;
            puVar46[0xd] = uVar21;
            puVar46[0xe] = local_b8;
          }
          uVar14 = uVar14 + 1;
          if (uVar14 == uVar12) break;
          uVar30 = puVar46[0xe];
          local_e0 = uVar21;
        } while( true );
      }
      uVar12 = FUN_00121a30(param_2);
      if (uVar12 != 0) {
        pqVar29 = (qword *)puVar46[0xf];
        if ((qword *)puVar46[0x10] != pqVar29) {
          pqVar19 = pqVar29 + 3;
          plVar31 = (long *)((long)pqVar29 +
                            ((long)puVar46[0x10] - (long)pqVar19 & 0xfffffffffffffff8U) + 0x18);
          uVar30 = ((ulong)((long)plVar31 - (long)pqVar19) >> 3) * 3;
          uVar14 = (uint)uVar30 & 7;
          pqVar23 = pqVar19;
          pqVar28 = pqVar29;
          if ((uVar30 & 7) != 0) {
            if ((*(char *)((long)pqVar29 + 0x17) < '\0') && (*pqVar29 != 0)) {
              HeapInterface::Free();
            }
            pqVar23 = pqVar29 + 6;
            pqVar28 = pqVar19;
            if (uVar14 != 1) {
              pqVar28 = pqVar23;
              if (uVar14 != 2) {
                pqVar28 = pqVar19;
                if (uVar14 != 3) {
                  pqVar28 = pqVar23;
                  if (uVar14 != 4) {
                    pqVar28 = pqVar19;
                    if (uVar14 != 5) {
                      pqVar28 = pqVar23;
                      if (uVar14 != 6) {
                        if ((*(char *)((long)pqVar29 + 0x2f) < '\0') && (*pqVar19 != 0)) {
                          HeapInterface::Free();
                        }
                        pqVar28 = pqVar29 + 9;
                        pqVar19 = pqVar23;
                      }
                      if ((*(char *)((long)pqVar19 + 0x17) < '\0') && (*pqVar19 != 0)) {
                        HeapInterface::Free();
                      }
                      pqVar23 = pqVar28 + 3;
                    }
                    if ((*(char *)((long)pqVar28 + 0x17) < '\0') && (*pqVar28 != 0)) {
                      HeapInterface::Free();
                    }
                    pqVar28 = pqVar23 + 3;
                    pqVar19 = pqVar23;
                  }
                  if ((*(char *)((long)pqVar19 + 0x17) < '\0') && (*pqVar19 != 0)) {
                    HeapInterface::Free();
                  }
                  pqVar23 = pqVar28 + 3;
                }
                if ((*(char *)((long)pqVar28 + 0x17) < '\0') && (*pqVar28 != 0)) {
                  HeapInterface::Free();
                }
                pqVar28 = pqVar23 + 3;
                pqVar19 = pqVar23;
              }
              if ((*(char *)((long)pqVar19 + 0x17) < '\0') && (*pqVar19 != 0)) {
                HeapInterface::Free();
              }
              pqVar23 = pqVar28 + 3;
            }
          }
          while( true ) {
            if ((*(char *)((long)pqVar28 + 0x17) < '\0') && (*pqVar28 != 0)) {
              HeapInterface::Free();
            }
            if (pqVar23 == (qword *)plVar31) break;
            if ((*(char *)((long)pqVar23 + 0x17) < '\0') && (*pqVar23 != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)pqVar23 + 0x2f) < '\0') && (pqVar23[3] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)pqVar23 + 0x47) < '\0') && (pqVar23[6] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)pqVar23 + 0x5f) < '\0') && (pqVar23[9] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)pqVar23 + 0x77) < '\0') && (pqVar23[0xc] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)pqVar23 + 0x8f) < '\0') && (pqVar23[0xf] != 0)) {
              HeapInterface::Free();
            }
            pqVar28 = pqVar23 + 0x15;
            if ((*(char *)((long)pqVar23 + 0xa7) < '\0') && (pqVar23[0x12] != 0)) {
              HeapInterface::Free();
            }
            pqVar23 = pqVar23 + 0x18;
          }
          pqVar29 = (qword *)puVar46[0xf];
        }
        pqVar19 = (qword *)puVar46[0x11];
        puVar46[0x10] = pqVar29;
        if ((ulong)(((long)pqVar19 - (long)pqVar29 >> 3) * -0x5555555555555555) < (ulong)uVar12) {
          pqVar19 = (qword *)thunk_FUN_00c29480();
          plVar31 = (long *)puVar46[0xf];
          pqVar29 = pqVar19;
          if ((long *)puVar46[0x10] != plVar31) {
            uVar30 = ((ulong)((long)puVar46[0x10] - (long)(plVar31 + 3)) >> 3) * 0xaaaaaaaaaaaaaab &
                     0x1fffffffffffffff;
            uVar14 = (int)uVar30 + 1U & 7;
            if (uVar14 == 0) goto LAB_001d71b8;
            if (uVar14 != 1) {
              if (uVar14 != 2) {
                if (uVar14 != 3) {
                  if (uVar14 != 4) {
                    if (uVar14 != 5) {
                      if (uVar14 != 6) {
                        pqVar29 = pqVar19 + 3;
                        FUN_0048dcd0(pqVar19,plVar31);
                        plVar31 = plVar31 + 3;
                      }
                      FUN_0048dcd0(pqVar29,plVar31);
                      pqVar29 = pqVar29 + 3;
                      plVar31 = plVar31 + 3;
                    }
                    FUN_0048dcd0(pqVar29,plVar31);
                    pqVar29 = pqVar29 + 3;
                    plVar31 = plVar31 + 3;
                  }
                  FUN_0048dcd0(pqVar29,plVar31);
                  pqVar29 = pqVar29 + 3;
                  plVar31 = plVar31 + 3;
                }
                FUN_0048dcd0(pqVar29,plVar31);
                pqVar29 = pqVar29 + 3;
                plVar31 = plVar31 + 3;
              }
              FUN_0048dcd0(pqVar29,plVar31);
              pqVar29 = pqVar29 + 3;
              plVar31 = plVar31 + 3;
            }
            FUN_0048dcd0(pqVar29,plVar31);
            plVar31 = plVar31 + 3;
            for (pqVar29 = pqVar29 + 3; pqVar29 != pqVar19 + uVar30 * 3 + 3;
                pqVar29 = pqVar29 + 0x18) {
LAB_001d71b8:
              FUN_0048dcd0(pqVar29,plVar31);
              FUN_0048dcd0(pqVar29 + 3,plVar31 + 3);
              FUN_0048dcd0(pqVar29 + 6,plVar31 + 6);
              FUN_0048dcd0(pqVar29 + 9,plVar31 + 9);
              FUN_0048dcd0(pqVar29 + 0xc,plVar31 + 0xc);
              FUN_0048dcd0(pqVar29 + 0xf,plVar31 + 0xf);
              FUN_0048dcd0(pqVar29 + 0x12,plVar31 + 0x12);
              plVar36 = plVar31 + 0x15;
              plVar31 = plVar31 + 0x18;
              FUN_0048dcd0(pqVar29 + 0x15,plVar36);
            }
            plVar31 = (long *)puVar46[0xf];
            pqVar29 = pqVar19 + uVar30 * 3 + 3;
            if ((long *)puVar46[0x10] != plVar31) {
              plVar36 = plVar31 + 3;
              plVar42 = (long *)((long)plVar31 +
                                ((long)puVar46[0x10] - (long)plVar36 & 0xfffffffffffffff8U) + 0x18);
              uVar30 = ((ulong)((long)plVar42 - (long)plVar36) >> 3) * 3;
              uVar14 = (uint)uVar30 & 7;
              plVar25 = plVar36;
              plVar32 = plVar31;
              if ((uVar30 & 7) != 0) {
                if ((*(char *)((long)plVar31 + 0x17) < '\0') && (*plVar31 != 0)) {
                  HeapInterface::Free();
                }
                plVar25 = plVar31 + 6;
                plVar32 = plVar36;
                if (uVar14 != 1) {
                  plVar32 = plVar25;
                  if (uVar14 != 2) {
                    plVar32 = plVar36;
                    if (uVar14 != 3) {
                      plVar32 = plVar25;
                      if (uVar14 != 4) {
                        plVar32 = plVar36;
                        if (uVar14 != 5) {
                          plVar32 = plVar25;
                          if (uVar14 != 6) {
                            if ((*(char *)((long)plVar31 + 0x2f) < '\0') && (*plVar36 != 0)) {
                              HeapInterface::Free();
                            }
                            plVar32 = plVar31 + 9;
                            plVar36 = plVar25;
                          }
                          if ((*(char *)((long)plVar36 + 0x17) < '\0') && (*plVar36 != 0)) {
                            HeapInterface::Free();
                          }
                          plVar25 = plVar32 + 3;
                        }
                        if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                          HeapInterface::Free();
                        }
                        plVar32 = plVar25 + 3;
                        plVar36 = plVar25;
                      }
                      if ((*(char *)((long)plVar36 + 0x17) < '\0') && (*plVar36 != 0)) {
                        HeapInterface::Free();
                      }
                      plVar25 = plVar32 + 3;
                    }
                    if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                      HeapInterface::Free();
                    }
                    plVar32 = plVar25 + 3;
                    plVar36 = plVar25;
                  }
                  if ((*(char *)((long)plVar36 + 0x17) < '\0') && (*plVar36 != 0)) {
                    HeapInterface::Free();
                  }
                  plVar25 = plVar32 + 3;
                }
              }
              while( true ) {
                if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                  HeapInterface::Free();
                }
                if (plVar42 == plVar25) break;
                if ((*(char *)((long)plVar25 + 0x17) < '\0') && (*plVar25 != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar25 + 0x2f) < '\0') && (plVar25[3] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar25 + 0x47) < '\0') && (plVar25[6] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar25 + 0x5f) < '\0') && (plVar25[9] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar25 + 0x77) < '\0') && (plVar25[0xc] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar25 + 0x8f) < '\0') && (plVar25[0xf] != 0)) {
                  HeapInterface::Free();
                }
                plVar32 = plVar25 + 0x15;
                if ((*(char *)((long)plVar25 + 0xa7) < '\0') && (plVar25[0x12] != 0)) {
                  HeapInterface::Free();
                }
                plVar25 = plVar25 + 0x18;
              }
              plVar31 = (long *)puVar46[0xf];
            }
          }
          if (plVar31 != (long *)0x0) {
            HeapInterface::Free();
          }
          puVar46[0xf] = pqVar19;
          pqVar19 = pqVar19 + (ulong)uVar12 * 3;
          puVar46[0x10] = pqVar29;
          puVar46[0x11] = pqVar19;
        }
        local_e8 = 0;
        local_d8 = (uint)uVar12;
        do {
          if (pqVar29 < pqVar19) {
            *(undefined1 *)pqVar29 = 0;
            *(undefined1 *)((long)pqVar29 + 0x17) = 0x17;
            FUN_00afd8d0(param_2,pqVar29);
            pqVar28 = (qword *)(puVar46[0x10] + 0x18);
            puVar46[0x10] = pqVar28;
          }
          else {
            plVar31 = (long *)puVar46[0xf];
            lVar17 = (long)pqVar29 - (long)plVar31 >> 3;
            if (lVar17 * -0x5555555555555555 == 0) {
              lVar17 = 0x18;
LAB_001d6552:
              puVar18 = (undefined1 *)FUN_00c29480(lVar17);
              pqVar29 = (qword *)puVar46[0x10];
              local_d0 = puVar18 + lVar17;
              plVar31 = (long *)puVar46[0xf];
              pqVar28 = (qword *)(puVar18 + 0x18);
            }
            else {
              if (lVar17 * 0x5555555555555556 != 0) {
                lVar17 = lVar17 << 4;
                goto LAB_001d6552;
              }
              pqVar28 = &Elf64_Ehdr_00000000.e_entry;
              local_d0 = (undefined1 *)0x0;
              puVar18 = (undefined1 *)0x0;
            }
            puVar37 = puVar18;
            if ((qword *)plVar31 != pqVar29) {
              puVar37 = puVar18 + 0x18;
              uVar30 = ((ulong)((long)pqVar29 - (long)(plVar31 + 3)) >> 3) * 0xaaaaaaaaaaaaaab &
                       0x1fffffffffffffff;
              uVar14 = (int)((ulong)(puVar37 + uVar30 * 0x18 + (-0x18 - (long)puVar18)) >> 3) *
                       -0x55555555 + 1U & 7;
              puVar40 = puVar18;
              if (uVar14 == 0) goto LAB_001d66a2;
              if (uVar14 != 1) {
                plVar36 = plVar31;
                puVar41 = puVar18;
                if (uVar14 != 2) {
                  if (uVar14 != 3) {
                    if (uVar14 != 4) {
                      if (uVar14 != 5) {
                        if (uVar14 != 6) {
                          FUN_0048dcd0(puVar18,plVar31);
                          plVar36 = plVar31 + 3;
                          puVar41 = puVar37;
                        }
                        plVar31 = plVar36 + 3;
                        puVar40 = puVar41 + 0x18;
                        FUN_0048dcd0(puVar41,plVar36);
                      }
                      plVar36 = plVar31 + 3;
                      puVar41 = puVar40 + 0x18;
                      FUN_0048dcd0(puVar40,plVar31);
                    }
                    plVar31 = plVar36 + 3;
                    puVar40 = puVar41 + 0x18;
                    FUN_0048dcd0(puVar41,plVar36);
                  }
                  plVar36 = plVar31 + 3;
                  puVar41 = puVar40 + 0x18;
                  FUN_0048dcd0(puVar40,plVar31);
                }
                plVar31 = plVar36 + 3;
                puVar40 = puVar41 + 0x18;
                FUN_0048dcd0(puVar41,plVar36);
              }
              FUN_0048dcd0(puVar40,plVar31);
              plVar31 = plVar31 + 3;
              for (puVar40 = puVar40 + 0x18; puVar37 + uVar30 * 0x18 != puVar40;
                  puVar40 = puVar40 + 0xc0) {
LAB_001d66a2:
                FUN_0048dcd0(puVar40,plVar31);
                FUN_0048dcd0(puVar40 + 0x18,plVar31 + 3);
                FUN_0048dcd0(puVar40 + 0x30,plVar31 + 6);
                FUN_0048dcd0(puVar40 + 0x48,plVar31 + 9);
                FUN_0048dcd0(puVar40 + 0x60,plVar31 + 0xc);
                FUN_0048dcd0(puVar40 + 0x78,plVar31 + 0xf);
                FUN_0048dcd0(puVar40 + 0x90,plVar31 + 0x12);
                plVar36 = plVar31 + 0x15;
                plVar31 = plVar31 + 0x18;
                FUN_0048dcd0(puVar40 + 0xa8,plVar36);
              }
              pqVar28 = (qword *)(puVar18 + (uVar30 * 3 + 3) * 8 + 0x18);
              puVar37 = puVar18 + (uVar30 * 3 + 3) * 8;
            }
            *puVar37 = 0;
            puVar37[0x17] = 0x17;
            FUN_00afd8d0(param_2);
            plVar31 = (long *)puVar46[0xf];
            if ((long *)puVar46[0x10] != plVar31) {
              plVar36 = plVar31 + 3;
              uVar21 = (long)puVar46[0x10] - (long)plVar36;
              uVar30 = ((ulong)((long)(plVar31 +
                                      ((uVar21 >> 3) * 0xaaaaaaaaaaaaaab & 0x1fffffffffffffff) * 3 +
                                      3) - (long)plVar36) >> 3) * 3;
              uVar14 = (uint)uVar30 & 7;
              plVar32 = plVar31;
              plVar42 = plVar36;
              if ((uVar30 & 7) != 0) {
                if ((*(char *)((long)plVar31 + 0x17) < '\0') && (*plVar31 != 0)) {
                  HeapInterface::Free();
                }
                plVar42 = plVar31 + 6;
                plVar32 = plVar36;
                if (uVar14 != 1) {
                  plVar25 = plVar36;
                  plVar32 = plVar42;
                  if (uVar14 != 2) {
                    plVar25 = plVar42;
                    if (uVar14 != 3) {
                      plVar32 = plVar36;
                      if (uVar14 != 4) {
                        plVar32 = plVar42;
                        if (uVar14 != 5) {
                          plVar32 = plVar36;
                          if (uVar14 != 6) {
                            if ((*(char *)((long)plVar31 + 0x2f) < '\0') && (*plVar36 != 0)) {
                              HeapInterface::Free();
                            }
                            plVar32 = plVar42;
                            plVar42 = plVar31 + 9;
                          }
                          plVar36 = plVar42;
                          if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                            HeapInterface::Free();
                          }
                          plVar32 = plVar36 + 3;
                        }
                        if ((*(char *)((long)plVar36 + 0x17) < '\0') && (*plVar36 != 0)) {
                          HeapInterface::Free();
                        }
                        plVar42 = plVar32 + 3;
                      }
                      plVar36 = plVar42;
                      if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                        HeapInterface::Free();
                      }
                      plVar25 = plVar36 + 3;
                    }
                    if ((*(char *)((long)plVar36 + 0x17) < '\0') && (*plVar36 != 0)) {
                      HeapInterface::Free();
                    }
                    plVar32 = plVar25 + 3;
                  }
                  if ((*(char *)((long)plVar25 + 0x17) < '\0') && (*plVar25 != 0)) {
                    HeapInterface::Free();
                  }
                  plVar42 = plVar32 + 3;
                }
              }
              while( true ) {
                if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
                  HeapInterface::Free();
                }
                if (plVar31 + ((uVar21 >> 3) * 0xaaaaaaaaaaaaaab & 0x1fffffffffffffff) * 3 + 3 ==
                    plVar42) break;
                if ((*(char *)((long)plVar42 + 0x17) < '\0') && (*plVar42 != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar42 + 0x2f) < '\0') && (plVar42[3] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar42 + 0x47) < '\0') && (plVar42[6] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar42 + 0x5f) < '\0') && (plVar42[9] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar42 + 0x77) < '\0') && (plVar42[0xc] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar42 + 0x8f) < '\0') && (plVar42[0xf] != 0)) {
                  HeapInterface::Free();
                }
                plVar32 = plVar42 + 0x15;
                if ((*(char *)((long)plVar42 + 0xa7) < '\0') && (plVar42[0x12] != 0)) {
                  HeapInterface::Free();
                }
                plVar42 = plVar42 + 0x18;
              }
              plVar31 = (long *)puVar46[0xf];
            }
            if (plVar31 != (long *)0x0) {
              HeapInterface::Free();
            }
            puVar46[0xf] = puVar18;
            puVar46[0x10] = pqVar28;
            puVar46[0x11] = local_d0;
          }
          local_e8 = local_e8 + 1;
          if (local_e8 == local_d8) break;
          pqVar19 = (qword *)puVar46[0x11];
          pqVar29 = pqVar28;
        } while( true );
      }
      uVar12 = FUN_00121a30(param_2);
      if (uVar12 != 0) {
        uVar14 = 0;
        do {
          local_48 = 4;
          local_68 = local_68 & 0xffffffff00000000;
          pcVar9 = *(code **)(*(long *)(local_c8 + 0xd0) + 0x10);
          if (pcVar9 == FUN_004bd170) {
            lVar17 = FUN_004bd170(lVar27);
          }
          else {
            lVar17 = (*pcVar9)();
          }
          uVar13 = FUN_00121a30(param_2);
          lVar17 = (**(code **)(**(long **)(lVar17 + 0x38) + 0x40))
                             (*(long **)(lVar17 + 0x38),uVar13,0);
          local_68 = CONCAT44(local_68._4_4_,(uint)uVar13);
          plVar31 = *(long **)(*(long *)(*(long *)(lVar17 + 8) + 0x40) + 8);
          (**(code **)(*plVar31 + 0x20))(plVar31,&uStack_60,param_2);
          pcVar9 = *(code **)(*(long *)(local_c8 + 0xd0) + 0x10);
          if (pcVar9 == FUN_004bd170) {
            lVar17 = FUN_004bd170();
          }
          else {
            lVar17 = (*pcVar9)(lVar27);
          }
          uVar14 = uVar14 + 1;
          lVar17 = (**(code **)(**(long **)(lVar17 + 0x38) + 0x40))
                             (*(long **)(lVar17 + 0x38),local_68 & 0xffffffff,0);
          local_8c = *(undefined4 *)(*(long *)(lVar17 + 8) + 8);
          FUN_00ae4c30(local_88,puVar46 + 6,&local_8c);
          FUN_0048f080(local_88[0] + 8,&uStack_60);
          FUN_0047fe00(&uStack_60);
        } while (uVar14 != uVar12);
      }
      lVar27 = puVar46[0xc];
      lVar17 = puVar46[0xd];
      uVar12 = 0xffff;
      if (lVar27 != lVar17) {
        bVar26 = *(byte *)(lVar27 + 0x35);
        if (lVar17 == lVar27 + 0xb0) {
          uVar12 = 0;
        }
        else {
          pbVar22 = (byte *)(lVar27 + 0xe5);
          uVar13 = 1;
          uVar12 = 0;
          pbVar1 = (byte *)(lVar27 + 0x195 +
                           (((ulong)(lVar17 - (lVar27 + 0x160)) >> 4) * 0xe8ba2e8ba2e8ba3 &
                           0xfffffffffffffff) * 0xb0);
          uVar14 = (int)((ulong)(pbVar1 + (-0xb0 - (long)pbVar22)) >> 4) * -0x45d1745d + 1U & 7;
          if (uVar14 != 0) {
            if (uVar14 != 1) {
              if (uVar14 != 2) {
                if (uVar14 != 3) {
                  if (uVar14 != 4) {
                    if (uVar14 != 5) {
                      if (uVar14 != 6) {
                        bVar5 = *pbVar22;
                        bVar10 = bVar26 < bVar5;
                        if (bVar10) {
                          bVar26 = bVar5;
                        }
                        uVar12 = (ushort)bVar10;
                        uVar13 = 2;
                        pbVar22 = (byte *)(lVar27 + 0x195);
                      }
                      if (bVar26 < *pbVar22) {
                        bVar26 = *pbVar22;
                        uVar12 = uVar13;
                      }
                      uVar13 = uVar13 + 1;
                      pbVar22 = pbVar22 + 0xb0;
                    }
                    if (bVar26 < *pbVar22) {
                      bVar26 = *pbVar22;
                      uVar12 = uVar13;
                    }
                    uVar13 = uVar13 + 1;
                    pbVar22 = pbVar22 + 0xb0;
                  }
                  if (bVar26 < *pbVar22) {
                    bVar26 = *pbVar22;
                    uVar12 = uVar13;
                  }
                  uVar13 = uVar13 + 1;
                  pbVar22 = pbVar22 + 0xb0;
                }
                if (bVar26 < *pbVar22) {
                  bVar26 = *pbVar22;
                  uVar12 = uVar13;
                }
                uVar13 = uVar13 + 1;
                pbVar22 = pbVar22 + 0xb0;
              }
              if (bVar26 < *pbVar22) {
                bVar26 = *pbVar22;
                uVar12 = uVar13;
              }
              uVar13 = uVar13 + 1;
              pbVar22 = pbVar22 + 0xb0;
            }
            if (bVar26 < *pbVar22) {
              bVar26 = *pbVar22;
              uVar12 = uVar13;
            }
            pbVar22 = pbVar22 + 0xb0;
            uVar13 = uVar13 + 1;
            if (pbVar1 == pbVar22) goto LAB_001d6290;
          }
          do {
            if (bVar26 < *pbVar22) {
              bVar26 = *pbVar22;
              uVar12 = uVar13;
            }
            if (bVar26 < pbVar22[0xb0]) {
              bVar26 = pbVar22[0xb0];
              uVar12 = uVar13 + 1;
            }
            if (bVar26 < pbVar22[0x160]) {
              bVar26 = pbVar22[0x160];
              uVar12 = uVar13 + 2;
            }
            if (bVar26 < pbVar22[0x210]) {
              bVar26 = pbVar22[0x210];
              uVar12 = uVar13 + 3;
            }
            if (bVar26 < pbVar22[0x2c0]) {
              bVar26 = pbVar22[0x2c0];
              uVar12 = uVar13 + 4;
            }
            if (bVar26 < pbVar22[0x370]) {
              bVar26 = pbVar22[0x370];
              uVar12 = uVar13 + 5;
            }
            if (bVar26 < pbVar22[0x420]) {
              bVar26 = pbVar22[0x420];
              uVar12 = uVar13 + 6;
            }
            if (bVar26 < pbVar22[0x4d0]) {
              bVar26 = pbVar22[0x4d0];
              uVar12 = uVar13 + 7;
            }
            pbVar22 = pbVar22 + 0x580;
            uVar13 = uVar13 + 8;
          } while (pbVar1 != pbVar22);
        }
      }
LAB_001d6290:
      *(ushort *)(puVar46 + 0x12) = uVar12;
      plVar31 = *(long **)((long)&__DT_RELA[0xcfc].r_addend + *param_1);
      if (puVar15 == (undefined8 *)0x0) goto LAB_001d56c8;
    }
    else {
      plVar31 = *(long **)((long)&__DT_RELA[0xcfc].r_addend + lVar38);
    }
    LOCK();
    *(int *)(puVar15 + 1) = *(int *)(puVar15 + 1) + 1;
    UNLOCK();
    lVar27 = *plVar31;
    plVar31[1] = (long)puVar46;
    *plVar31 = (long)puVar15;
    if (lVar27 != 0) {
      ref_counter_base::DecRef();
    }
  }
  ref_counter_base::DecRef(puVar15);
LAB_001d554c:
  return &DAT_015d3620;
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
