# Handler decompilations from the binary (headless RE)


## `jag::packethandlers::Inventory::UPDATE_INV_PARTIAL` @ 00184320
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined *
jag::packethandlers::Inventory::UPDATE_INV_PARTIAL(long *param_1,long param_2,int *param_3)

{
  uint *puVar1;
  long *plVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  byte bVar5;
  ushort uVar6;
  int iVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined8 uVar10;
  undefined8 uVar11;
  undefined8 uVar12;
  undefined8 uVar13;
  undefined8 uVar14;
  int iVar15;
  ulong uVar16;
  undefined8 *puVar17;
  long lVar18;
  undefined8 *puVar19;
  undefined1 (*pauVar20) [16];
  undefined1 (*pauVar21) [16];
  ushort uVar22;
  code *pcVar23;
  undefined8 *puVar24;
  ushort uVar25;
  long lVar26;
  long *plVar27;
  void *pvVar28;
  int *piVar29;
  long lVar30;
  undefined8 *puVar31;
  long *plVar32;
  ushort uVar33;
  uint uVar34;
  ulong uVar35;
  uint uVar36;
  undefined4 uVar37;
  ulong uVar38;
  undefined8 uVar39;
  byte bVar40;
  void *pvVar41;
  bool bVar42;
  undefined4 uVar43;
  long *local_118;
  uint local_10c;
  uint local_f0;
  undefined1 (*local_b0) [16];
  long *local_a8;
  undefined1 *local_a0;
  uint local_7c;
  long local_78 [4];
  uint local_58 [6];
  byte local_40;
  
  bVar42 = DAT_01050dc0 == 0x3020100;
  iVar7 = *param_3;
  lVar26 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar26 + 2;
  uVar25 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar26);
  if (bVar42) {
    uVar25 = uVar25 << 8 | uVar25 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar26 + 3;
  bVar5 = *(byte *)(*(long *)(param_2 + 0x10) + 2 + lVar26);
  bVar40 = bVar5 & 1;
  local_118 = (long *)game::InventoryManager::GetInventory
                                (*(undefined8 *)((long)&__DT_RELA[0xd02].r_offset + *param_1),uVar25
                                 ,bVar40);
  if (local_118 == (long *)0x0) {
    local_118 = (long *)game::InventoryManager::CreateInventory
                                  (*(undefined8 *)((long)&__DT_RELA[0xd02].r_offset + *param_1),
                                   (uint)uVar25,bVar40);
  }
LAB_00184525:
  lVar26 = *(long *)(param_2 + 0x18);
  iVar15 = (int)lVar26;
  do {
    if (iVar7 <= iVar15) {
      lVar26 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
      uVar36 = *(uint *)(lVar26 + 0xb0);
      *(uint *)(lVar26 + 0xb0) = uVar36 + 1;
      *(uint *)(*(long *)(lVar26 + 0xa8) + (ulong)(uVar36 & 0x3f) * 4) = (uint)uVar25;
      return &DAT_015d3620;
    }
    lVar30 = *(long *)(param_2 + 0x10);
    bVar40 = (byte)*(ushort *)(lVar30 + lVar26);
    if ((char)bVar40 < '\0') {
      lVar18 = lVar26 + 2;
      *(long *)(param_2 + 0x18) = lVar18;
      uVar6 = *(ushort *)(lVar30 + lVar26);
      uVar33 = uVar6 << 8 | uVar6 >> 8;
      if (DAT_01050dc0 != 0x3020100) {
        uVar33 = uVar6;
      }
      uVar33 = uVar33 + 0x8000;
    }
    else {
      lVar18 = lVar26 + 1;
      uVar33 = (ushort)bVar40;
    }
    *(long *)(param_2 + 0x18) = lVar18 + 2;
    uVar6 = *(ushort *)(lVar30 + lVar18);
    uVar22 = uVar6 << 8 | uVar6 >> 8;
    if (DAT_01050dc0 != 0x3020100) {
      uVar22 = uVar6;
    }
    if (uVar22 == 0) {
      local_f0 = 0;
      local_10c = 0;
    }
    else {
      lVar26 = lVar18 + 3;
      *(long *)(param_2 + 0x18) = lVar26;
      local_f0 = (uint)*(byte *)(lVar30 + 2 + lVar18);
      if (local_f0 == 0xff) {
        puVar1 = (uint *)(lVar30 + lVar26);
        lVar26 = lVar18 + 7;
        bVar42 = DAT_01050dc0 == 0x3020100;
        *(long *)(param_2 + 0x18) = lVar26;
        local_f0 = *puVar1;
        if (bVar42) {
          local_f0 = local_f0 >> 0x18 | (local_f0 & 0xff0000) >> 8 | (local_f0 & 0xff00) << 8 |
                     local_f0 << 0x18;
        }
      }
      local_10c = 0;
      if ((bVar5 & 2) != 0) {
        *(long *)(param_2 + 0x18) = lVar26 + 1;
        local_10c = (uint)*(byte *)(lVar30 + lVar26);
      }
    }
    lVar30 = local_118[2];
    lVar26 = local_118[3];
    if ((lVar30 == lVar26) || ((int)(lVar26 - lVar30 >> 3) <= (int)(uint)uVar33)) {
LAB_00184480:
      puVar17 = &DAT_015c8550;
      if ((*(int *)(*local_118 + 0x3c) == 4) &&
         (lVar18 = *(long *)(*(long *)(*local_118 + 0x80) + (long)DAT_015decdc * 8), lVar18 != 0)) {
        plVar27 = *(long **)(lVar18 + 0x38);
        puVar17 = (undefined8 *)(**(code **)(*plVar27 + 0x40))(plVar27,(int)local_118[1],0);
        lVar30 = local_118[2];
        lVar26 = local_118[3];
      }
      uVar36 = uVar33 + 1;
      if ((int)(uVar33 + 1) <= *(int *)(puVar17[1] + 0x38)) {
        uVar36 = *(uint *)(puVar17[1] + 0x38);
      }
      uVar16 = lVar26 - lVar30 >> 3;
      if ((int)uVar16 <= (int)uVar36) {
        lVar26 = (long)(int)uVar36;
        if (lVar26 == 0) {
          puVar17 = (undefined8 *)0x0;
          puVar19 = (undefined8 *)0x0;
        }
        else {
          puVar19 = (undefined8 *)FUN_00c29480(lVar26 * 8);
          uVar11 = _UNK_00cb7538;
          uVar10 = _DAT_00cb7530;
          puVar17 = puVar19 + lVar26;
          puVar31 = puVar19;
          if (uVar36 == 1) {
LAB_00184ca6:
            *puVar31 = 0xffffffff;
          }
          else {
            uVar38 = 1;
            puVar31 = puVar19 + 2;
            uVar35 = lVar26 - 2U >> 1;
            uVar16 = uVar35 + 1;
            uVar34 = (uint)uVar35 & 7;
            *puVar19 = _DAT_00cb7530;
            puVar19[1] = uVar11;
            if (1 < uVar16) {
              if ((uVar35 & 7) != 0) {
                if (uVar34 != 1) {
                  if (uVar34 != 2) {
                    if (uVar34 != 3) {
                      if (uVar34 != 4) {
                        if (uVar34 != 5) {
                          if (uVar34 != 6) {
                            *puVar31 = uVar10;
                            puVar19[3] = uVar11;
                            uVar38 = 2;
                            puVar31 = puVar19 + 4;
                          }
                          *puVar31 = uVar10;
                          puVar31[1] = uVar11;
                          uVar38 = uVar38 + 1;
                          puVar31 = puVar31 + 2;
                        }
                        *puVar31 = uVar10;
                        puVar31[1] = uVar11;
                        uVar38 = uVar38 + 1;
                        puVar31 = puVar31 + 2;
                      }
                      *puVar31 = uVar10;
                      puVar31[1] = uVar11;
                      uVar38 = uVar38 + 1;
                      puVar31 = puVar31 + 2;
                    }
                    *puVar31 = uVar10;
                    puVar31[1] = uVar11;
                    uVar38 = uVar38 + 1;
                    puVar31 = puVar31 + 2;
                  }
                  *puVar31 = uVar10;
                  puVar31[1] = uVar11;
                  uVar38 = uVar38 + 1;
                  puVar31 = puVar31 + 2;
                }
                uVar38 = uVar38 + 1;
                *puVar31 = uVar10;
                puVar31[1] = uVar11;
                puVar31 = puVar31 + 2;
                if (uVar16 <= uVar38) goto LAB_00184c95;
              }
              do {
                uVar38 = uVar38 + 8;
                *puVar31 = uVar10;
                puVar31[1] = uVar11;
                puVar31[2] = uVar10;
                puVar31[3] = uVar11;
                puVar31[4] = uVar10;
                puVar31[5] = uVar11;
                puVar31[6] = uVar10;
                puVar31[7] = uVar11;
                puVar31[8] = uVar10;
                puVar31[9] = uVar11;
                puVar31[10] = uVar10;
                puVar31[0xb] = uVar11;
                puVar31[0xc] = uVar10;
                puVar31[0xd] = uVar11;
                puVar31[0xe] = uVar10;
                puVar31[0xf] = uVar11;
                puVar31 = puVar31 + 0x10;
              } while (uVar38 < uVar16);
            }
LAB_00184c95:
            puVar31 = puVar19 + uVar16 * 2;
            if (lVar26 != uVar16 * 2) goto LAB_00184ca6;
          }
          lVar30 = local_118[2];
          uVar16 = local_118[3] - lVar30 >> 3;
        }
        uVar35 = 0;
        if (uVar16 != 0) {
          do {
            puVar31 = (undefined8 *)(lVar30 + uVar35 * 8);
            puVar24 = puVar19 + uVar35;
            uVar8 = *(undefined4 *)puVar24;
            uVar3 = *(undefined4 *)((long)puVar24 + 4);
            *puVar24 = *puVar31;
            uVar35 = (ulong)((int)uVar35 + 1);
            *(undefined4 *)puVar31 = uVar8;
            *(undefined4 *)((long)puVar31 + 4) = uVar3;
          } while (uVar35 < uVar16);
        }
        local_118[2] = (long)puVar19;
        local_118[3] = (long)puVar17;
        local_118[4] = (long)puVar17;
        if (lVar30 != 0) {
          HeapInterface::Free();
        }
      }
      puVar19 = (undefined8 *)local_118[6];
      puVar17 = (undefined8 *)local_118[5];
      uVar16 = ((long)puVar19 - (long)puVar17 >> 3) * 0x6db6db6db6db6db7;
      if ((local_10c == 0) || ((int)uVar36 < (int)uVar16)) {
        lVar30 = local_118[2];
      }
      else {
        lVar26 = (long)(int)uVar36;
        if (lVar26 == 0) {
          local_a0 = (undefined1 *)0x0;
          local_b0 = (undefined1 (*) [16])0x0;
        }
        else {
          local_b0 = (undefined1 (*) [16])FUN_00c29480(lVar26 * 0x38);
          uVar3 = DAT_00cb74f8;
          uVar8 = DAT_00cb6ad0;
          local_a0 = (undefined1 *)(lVar26 * 0x38 + (long)local_b0);
          uVar36 = uVar36 & 3;
          pauVar21 = local_b0;
          if (uVar36 == 0) goto LAB_00184e49;
          if (uVar36 != 1) {
            pauVar20 = local_b0;
            if (uVar36 != 2) {
              *(undefined8 *)local_b0[3] = 0;
              *(undefined8 *)local_b0[2] = 0;
              *(undefined4 *)(local_b0[2] + 8) = uVar8;
              *(undefined8 *)(local_b0[1] + 8) = 1;
              *(undefined4 *)(local_b0[2] + 0xc) = uVar3;
              *(void ***)local_b0[1] = &DAT_01393d40;
              *local_b0 = (undefined1  [16])0x0;
              *(undefined ***)*local_b0 = &PTR_FUN_0136b598;
              pauVar20 = (undefined1 (*) [16])(local_b0[3] + 8);
              lVar26 = lVar26 + -1;
            }
            *(undefined8 *)pauVar20[3] = 0;
            *(undefined8 *)pauVar20[2] = 0;
            *(undefined4 *)(pauVar20[2] + 8) = uVar8;
            *(undefined8 *)(pauVar20[1] + 8) = 1;
            *(void ***)pauVar20[1] = &DAT_01393d40;
            *(undefined4 *)(pauVar20[2] + 0xc) = uVar3;
            lVar26 = lVar26 + -1;
            pauVar21 = (undefined1 (*) [16])(pauVar20[3] + 8);
            *pauVar20 = (undefined1  [16])0x0;
            *(undefined ***)*pauVar20 = &PTR_FUN_0136b598;
          }
          *(undefined4 *)(pauVar21[2] + 8) = uVar8;
          *(undefined4 *)(pauVar21[2] + 0xc) = uVar3;
          *(undefined8 *)pauVar21[3] = 0;
          *pauVar21 = (undefined1  [16])0x0;
          *(undefined8 *)pauVar21[2] = 0;
          *(undefined ***)*pauVar21 = &PTR_FUN_0136b598;
          *(undefined8 *)(pauVar21[1] + 8) = 1;
          *(void ***)pauVar21[1] = &DAT_01393d40;
          pauVar21 = (undefined1 (*) [16])(pauVar21[3] + 8);
          for (lVar26 = lVar26 + -1; lVar26 != 0; lVar26 = lVar26 + -4) {
LAB_00184e49:
            *(undefined8 *)pauVar21[3] = 0;
            *(undefined8 *)pauVar21[2] = 0;
            *(undefined4 *)(pauVar21[2] + 8) = uVar8;
            *(undefined8 *)(pauVar21[1] + 8) = 1;
            *(void ***)pauVar21[1] = &DAT_01393d40;
            *(undefined4 *)(pauVar21[2] + 0xc) = uVar3;
            *(undefined8 *)(pauVar21[6] + 8) = 0;
            *(undefined8 *)(pauVar21[5] + 8) = 0;
            *pauVar21 = (undefined1  [16])0x0;
            *(undefined8 *)pauVar21[5] = 1;
            *(undefined1 (*) [16])(pauVar21[3] + 8) = (undefined1  [16])0x0;
            pauVar21[7] = (undefined1  [16])0x0;
            *(undefined1 (*) [16])(pauVar21[10] + 8) = (undefined1  [16])0x0;
            *(undefined4 *)pauVar21[6] = uVar8;
            *(undefined4 *)(pauVar21[6] + 4) = uVar3;
            *(undefined ***)*pauVar21 = &PTR_FUN_0136b598;
            *(undefined4 *)(pauVar21[9] + 8) = uVar8;
            *(undefined ***)(pauVar21[3] + 8) = &PTR_FUN_0136b598;
            *(undefined4 *)(pauVar21[9] + 0xc) = uVar3;
            *(void ***)(pauVar21[4] + 8) = &DAT_01393d40;
            *(undefined4 *)pauVar21[0xd] = uVar8;
            *(undefined8 *)pauVar21[10] = 0;
            *(undefined4 *)(pauVar21[0xd] + 4) = uVar3;
            *(undefined ***)pauVar21[7] = &PTR_FUN_0136b598;
            *(undefined8 *)pauVar21[9] = 0;
            *(undefined8 *)(pauVar21[8] + 8) = 1;
            *(void ***)pauVar21[8] = &DAT_01393d40;
            *(undefined8 *)(pauVar21[0xd] + 8) = 0;
            *(undefined ***)(pauVar21[10] + 8) = &PTR_FUN_0136b598;
            *(undefined8 *)(pauVar21[0xc] + 8) = 0;
            *(undefined8 *)pauVar21[0xc] = 1;
            *(void ***)(pauVar21[0xb] + 8) = &DAT_01393d40;
            pauVar21 = pauVar21 + 0xe;
          }
          puVar17 = (undefined8 *)local_118[5];
          puVar19 = (undefined8 *)local_118[6];
        }
        uVar16 = 0;
        uVar36 = 0;
        local_a8 = (long *)&DAT_01393d40;
        if (puVar17 != puVar19) {
          do {
            lVar26 = uVar16 * 0x38;
            uVar9 = *(undefined4 *)((long)local_b0 + lVar26 + 0x30);
            uVar10 = *(undefined8 *)((long)local_b0 + lVar26 + 0x10);
            uVar11 = *(undefined8 *)((long)local_b0 + lVar26 + 0x18);
            uVar12 = *(undefined8 *)((long)local_b0 + lVar26 + 0x20);
            uVar8 = *(undefined4 *)((long)local_b0 + lVar26 + 0x28);
            *(undefined4 *)((long)local_b0 + lVar26 + 0x30) = 0;
            uVar3 = *(undefined4 *)((long)local_b0 + lVar26 + 0x2c);
            *(void ***)((long)local_b0 + lVar26 + 0x10) = &DAT_01393d40;
            *(undefined8 *)((long)local_b0 + lVar26 + 0x18) = 1;
            *(undefined8 *)((long)local_b0 + lVar26 + 0x20) = 0;
            if ((undefined8 *)((long)local_b0 + lVar26 + 8) == puVar17 + uVar16 * 7 + 1) {
              lVar30 = puVar17[uVar16 * 7 + 3];
              plVar27 = (long *)puVar17[uVar16 * 7 + 2];
            }
            else {
              pvVar41 = DAT_01393d40;
              if (DAT_01393d40 == (void *)0x0) {
                uVar39 = 1;
                uVar37 = 0;
                plVar27 = local_a8;
                uVar43 = uVar8;
                uVar4 = uVar3;
              }
              else {
                do {
                  while( true ) {
                    bVar40 = *(byte *)((long)pvVar41 + 0x20);
                    pvVar28 = *(void **)((long)pvVar41 + 0x28);
                    if ((bVar40 != 0xff) && (bVar40 != 4)) {
                      (*(code *)(&PTR_FUN_01384ba0)[bVar40])((long)pvVar41 + 8);
                    }
                    if (DAT_015ed790 != 0) break;
                    free(pvVar41);
LAB_00185023:
                    pvVar41 = pvVar28;
                    if (pvVar28 == (void *)0x0) goto LAB_00185090;
                  }
                  if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108)) {
                    plVar27 = *(long **)(DAT_015ed790 + 0x198);
                  }
                  else {
                    plVar27 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                  }
                  if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8)) {
                    uVar43 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                  }
                  else {
                    uVar43 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                  }
                  if (*(code **)(*plVar27 + 0x28) == FUN_00a87200) {
                    _DAT_015da900 = _DAT_015da900 + 1;
                    free(pvVar41);
                    goto LAB_00185023;
                  }
                  (**(code **)(*plVar27 + 0x28))(plVar27,pvVar41,uVar43);
                  pvVar41 = pvVar28;
                } while (pvVar28 != (void *)0x0);
LAB_00185090:
                uVar37 = *(undefined4 *)((long)local_b0 + lVar26 + 0x30);
                plVar27 = *(long **)((long)local_b0 + lVar26 + 0x10);
                uVar39 = *(undefined8 *)((long)local_b0 + lVar26 + 0x18);
                uVar43 = *(undefined4 *)((long)local_b0 + lVar26 + 0x28);
                uVar4 = *(undefined4 *)((long)local_b0 + lVar26 + 0x2c);
              }
              uVar13 = puVar17[uVar16 * 7 + 5];
              *(undefined8 *)((long)local_b0 + lVar26 + 0x20) = 0;
              uVar14 = puVar17[uVar16 * 7 + 3];
              DAT_01393d40 = (void *)0x0;
              *(undefined8 *)((long)local_b0 + lVar26 + 0x28) = uVar13;
              *(undefined4 *)((long)local_b0 + lVar26 + 0x30) =
                   *(undefined4 *)(puVar17 + uVar16 * 7 + 6);
              *(undefined4 *)(puVar17 + uVar16 * 7 + 6) = uVar37;
              *(undefined4 *)(puVar17 + uVar16 * 7 + 5) = uVar43;
              uVar13 = puVar17[uVar16 * 7 + 2];
              *(undefined4 *)((long)puVar17 + lVar26 + 0x2c) = uVar4;
              *(undefined8 *)((long)local_b0 + lVar26 + 0x10) = uVar13;
              puVar17[uVar16 * 7 + 2] = plVar27;
              *(undefined8 *)((long)local_b0 + lVar26 + 0x18) = uVar14;
              puVar17[uVar16 * 7 + 3] = uVar39;
              uVar39 = *(undefined8 *)((long)local_b0 + lVar26 + 0x20);
              *(undefined8 *)((long)local_b0 + lVar26 + 0x20) = puVar17[uVar16 * 7 + 4];
              lVar30 = puVar17[uVar16 * 7 + 3];
              puVar17[uVar16 * 7 + 4] = uVar39;
            }
            if (lVar30 != 0) {
              plVar2 = plVar27 + lVar30;
              do {
                pvVar41 = (void *)*plVar27;
joined_r0x00185127:
                pvVar28 = pvVar41;
                if (pvVar41 != (void *)0x0) {
                  do {
                    bVar40 = *(byte *)((long)pvVar28 + 0x20);
                    pvVar41 = *(void **)((long)pvVar28 + 0x28);
                    if ((bVar40 != 0xff) && (bVar40 != 4)) {
                      (*(code *)(&PTR_FUN_01384ba0)[bVar40])((long)pvVar28 + 8);
                    }
                    if (DAT_015ed790 == 0) {
                      free(pvVar28);
                    }
                    else {
                      if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108)) {
                        plVar32 = *(long **)(DAT_015ed790 + 0x198);
                      }
                      else {
                        plVar32 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                      }
                      if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8)) {
                        uVar43 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                      }
                      else {
                        uVar43 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                      }
                      if (*(code **)(*plVar32 + 0x28) != FUN_00a87200) goto LAB_001851cb;
                      _DAT_015da900 = _DAT_015da900 + 1;
                      free(pvVar28);
                    }
                    pvVar28 = pvVar41;
                    if (pvVar41 == (void *)0x0) break;
                  } while( true );
                }
                *plVar27 = 0;
                plVar27 = plVar27 + 1;
                if (plVar2 == plVar27) {
                  uVar35 = puVar17[uVar16 * 7 + 3];
                  puVar19 = (undefined8 *)puVar17[uVar16 * 7 + 2];
                  *(undefined4 *)(puVar17 + uVar16 * 7 + 5) = uVar8;
                  *(undefined4 *)(puVar17 + uVar16 * 7 + 6) = uVar9;
                  *(undefined4 *)((long)puVar17 + lVar26 + 0x2c) = uVar3;
                  puVar17[uVar16 * 7 + 2] = uVar10;
                  puVar17[uVar16 * 7 + 3] = uVar11;
                  puVar17[uVar16 * 7 + 4] = uVar12;
                  if (uVar35 != 0) {
                    puVar17 = puVar19;
                    do {
                      pvVar41 = (void *)*puVar17;
                      while (pvVar41 != (void *)0x0) {
                        bVar40 = *(byte *)((long)pvVar41 + 0x20);
                        pvVar28 = *(void **)((long)pvVar41 + 0x28);
                        if ((bVar40 != 0xff) && (bVar40 != 4)) {
                          (*(code *)(&PTR_FUN_01384ba0)[bVar40])((long)pvVar41 + 8);
                        }
                        if (DAT_015ed790 == 0) {
                          free(pvVar41);
                          pvVar41 = pvVar28;
                        }
                        else {
                          if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108)) {
                            plVar27 = *(long **)(DAT_015ed790 + 0x198);
                          }
                          else {
                            plVar27 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                          }
                          if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8)) {
                            uVar8 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                          }
                          else {
                            uVar8 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                          }
                          if (*(code **)(*plVar27 + 0x28) == FUN_00a87200) {
                            _DAT_015da900 = _DAT_015da900 + 1;
                            free(pvVar41);
                            pvVar41 = pvVar28;
                          }
                          else {
                            (**(code **)(*plVar27 + 0x28))(plVar27,pvVar41,uVar8);
                            pvVar41 = pvVar28;
                          }
                        }
                      }
                      *puVar17 = 0;
                      puVar17 = puVar17 + 1;
                    } while (puVar19 + uVar35 != puVar17);
                    if ((puVar19 != (undefined8 *)0x0) && (1 < uVar35)) {
                      HeapInterface::Free();
                    }
                  }
                  goto LAB_001849fc;
                }
              } while( true );
            }
            *(undefined4 *)(puVar17 + uVar16 * 7 + 6) = uVar9;
            *(undefined4 *)(puVar17 + uVar16 * 7 + 5) = uVar8;
            puVar17[uVar16 * 7 + 2] = uVar10;
            puVar17[uVar16 * 7 + 3] = uVar11;
            *(undefined4 *)((long)puVar17 + lVar26 + 0x2c) = uVar3;
            puVar17[uVar16 * 7 + 4] = uVar12;
LAB_001849fc:
            uVar36 = uVar36 + 1;
            uVar16 = (ulong)uVar36;
            puVar19 = (undefined8 *)local_118[6];
            puVar17 = (undefined8 *)local_118[5];
          } while (uVar16 < (ulong)(((long)puVar19 - (long)puVar17 >> 3) * 0x6db6db6db6db6db7));
        }
        local_118[6] = (long)local_a0;
        local_118[7] = (long)local_a0;
        local_118[5] = (long)local_b0;
        if (puVar17 != puVar19) {
          lVar26 = (((ulong)((long)puVar19 - (long)(puVar17 + 7)) >> 3) * 0xdb6db6db6db6db7 &
                   0x1fffffffffffffff) + 1;
          uVar36 = (int)(lVar26 * 0x38 - 0x38U >> 3) * -0x49249249 + 1U & 7;
          puVar19 = puVar17;
          if (uVar36 != 0) {
            puVar31 = puVar17;
            if (uVar36 != 1) {
              if (uVar36 != 2) {
                if (uVar36 != 3) {
                  if (uVar36 != 4) {
                    if (uVar36 != 5) {
                      if (uVar36 != 6) {
                        (**(code **)*puVar17)(puVar17);
                        puVar19 = puVar17 + 7;
                      }
                      puVar31 = puVar19 + 7;
                      (**(code **)*puVar19)(puVar19);
                    }
                    puVar19 = puVar31 + 7;
                    (**(code **)*puVar31)(puVar31);
                  }
                  puVar31 = puVar19 + 7;
                  (**(code **)*puVar19)(puVar19);
                }
                puVar19 = puVar31 + 7;
                (**(code **)*puVar31)(puVar31);
              }
              puVar31 = puVar19 + 7;
              (**(code **)*puVar19)(puVar19);
            }
            puVar19 = puVar31 + 7;
            (**(code **)*puVar31)(puVar31);
            if (puVar19 == puVar17 + lVar26 * 7) goto LAB_00184b41;
          }
          do {
            (**(code **)*puVar19)(puVar19);
            (**(code **)puVar19[7])(puVar19 + 7);
            (**(code **)puVar19[0xe])(puVar19 + 0xe);
            (**(code **)puVar19[0x15])(puVar19 + 0x15);
            (**(code **)puVar19[0x1c])(puVar19 + 0x1c);
            (**(code **)puVar19[0x23])(puVar19 + 0x23);
            (**(code **)puVar19[0x2a])(puVar19 + 0x2a);
            puVar31 = puVar19 + 0x31;
            puVar24 = puVar19 + 0x31;
            puVar19 = puVar19 + 0x38;
            (**(code **)*puVar31)(puVar24);
          } while (puVar19 != puVar17 + lVar26 * 7);
        }
LAB_00184b41:
        if (puVar17 != (undefined8 *)0x0) {
          HeapInterface::Free(puVar17);
        }
        puVar19 = (undefined8 *)local_118[6];
        lVar30 = local_118[2];
        puVar17 = (undefined8 *)local_118[5];
LAB_00184b66:
        uVar16 = ((long)puVar19 - (long)puVar17 >> 3) * 0x6db6db6db6db6db7;
      }
    }
    else {
      puVar17 = (undefined8 *)local_118[5];
      puVar19 = (undefined8 *)local_118[6];
      if (local_10c == 0) goto LAB_00184b66;
      if ((puVar17 == puVar19) ||
         (uVar16 = ((long)puVar19 - (long)puVar17 >> 3) * 0x6db6db6db6db6db7,
         (int)uVar16 <= (int)(uint)uVar33)) goto LAB_00184480;
    }
    uVar35 = (ulong)uVar33;
    piVar29 = (int *)(lVar30 + uVar35 * 8);
    *piVar29 = uVar22 - 1;
    piVar29[1] = local_f0;
    if (uVar35 < uVar16) {
      plVar27 = (long *)puVar17[uVar35 * 7 + 2];
      if (puVar17[uVar35 * 7 + 3] != 0) {
        plVar2 = plVar27 + puVar17[uVar35 * 7 + 3];
        do {
          pvVar41 = (void *)*plVar27;
joined_r0x00184607:
          pvVar28 = pvVar41;
          if (pvVar41 != (void *)0x0) {
            do {
              bVar40 = *(byte *)((long)pvVar28 + 0x20);
              pvVar41 = *(void **)((long)pvVar28 + 0x28);
              if ((bVar40 != 0xff) && (bVar40 != 4)) {
                (*(code *)(&PTR_FUN_01384ba0)[bVar40])((long)pvVar28 + 8);
              }
              if (DAT_015ed790 == 0) {
                free(pvVar28);
              }
              else {
                if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108)) {
                  plVar32 = *(long **)(DAT_015ed790 + 0x198);
                }
                else {
                  plVar32 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                }
                if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8)) {
                  uVar8 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                }
                else {
                  uVar8 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                }
                if (*(code **)(*plVar32 + 0x28) != FUN_00a87200) goto LAB_001846ab;
                _DAT_015da900 = _DAT_015da900 + 1;
                free(pvVar28);
              }
              pvVar28 = pvVar41;
              if (pvVar41 == (void *)0x0) break;
            } while( true );
          }
          *plVar27 = 0;
          plVar27 = plVar27 + 1;
          if (plVar2 == plVar27) break;
        } while( true );
      }
      puVar17[uVar35 * 7 + 4] = 0;
    }
    if (local_10c == 0) goto LAB_00184525;
    lVar26 = local_118[5] + 8 + uVar35 * 0x38;
    if (DAT_01050dc0 != 0x3020100) {
      if ((local_10c & 1) == 0) goto LAB_0018555a;
      lVar30 = *(long *)(param_2 + 0x18);
      *(long *)(param_2 + 0x18) = lVar30 + 2;
      uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
      *(long *)(param_2 + 0x18) = lVar30 + 6;
      local_58[0] = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
      local_40 = 0;
      local_7c = (uint)uVar33;
      FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
      FUN_0048f080(local_78[0] + 8,local_58);
      if (local_40 == 0xff) {
LAB_00185535:
        local_40 = 4;
        pcVar23 = FUN_004732c0;
      }
      else {
        pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
        if (local_40 != 4) {
          (*pcVar23)(local_58);
          goto LAB_00185535;
        }
      }
      (*pcVar23)(local_58);
      local_10c = local_10c - 1;
      if (local_10c != 0) goto LAB_0018555a;
      goto LAB_00184525;
    }
    if ((local_10c & 1) != 0) {
      lVar30 = *(long *)(param_2 + 0x18);
      *(long *)(param_2 + 0x18) = lVar30 + 2;
      uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
      *(long *)(param_2 + 0x18) = lVar30 + 6;
      uVar36 = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
      local_40 = 0;
      uVar33 = uVar33 << 8 | uVar33 >> 8;
      local_58[0] = uVar36 >> 0x18 | (uVar36 & 0xff0000) >> 8 | (uVar36 & 0xff00) << 8 |
                    uVar36 << 0x18;
      local_7c = (uint)uVar33;
      FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
      FUN_0048f080(local_78[0] + 8,local_58);
      if (local_40 == 0xff) {
LAB_001847d5:
        pcVar23 = FUN_004732c0;
        local_40 = 4;
      }
      else {
        pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
        if (local_40 != 4) {
          (*pcVar23)(local_58);
          goto LAB_001847d5;
        }
      }
      (*pcVar23)(local_58);
      local_10c = local_10c - 1;
      if (local_10c == 0) goto LAB_00184525;
    }
    do {
      lVar30 = *(long *)(param_2 + 0x18);
      *(long *)(param_2 + 0x18) = lVar30 + 2;
      uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
      *(long *)(param_2 + 0x18) = lVar30 + 6;
      uVar36 = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
      local_40 = 0;
      local_58[0] = uVar36 >> 0x18 | (uVar36 & 0xff0000) >> 8 | (uVar36 & 0xff00) << 8 |
                    uVar36 << 0x18;
      uVar33 = uVar33 << 8 | uVar33 >> 8;
      local_7c = (uint)uVar33;
      FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
      FUN_0048f080(local_78[0] + 8,local_58);
      if (local_40 == 0xff) {
LAB_0018488b:
        pcVar23 = FUN_004732c0;
        local_40 = 4;
      }
      else {
        pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
        if (local_40 != 4) {
          (*pcVar23)(local_58);
          goto LAB_0018488b;
        }
      }
      (*pcVar23)(local_58);
      lVar30 = *(long *)(param_2 + 0x18);
      *(long *)(param_2 + 0x18) = lVar30 + 2;
      uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
      *(long *)(param_2 + 0x18) = lVar30 + 6;
      uVar36 = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
      local_40 = 0;
      uVar33 = uVar33 << 8 | uVar33 >> 8;
      local_58[0] = uVar36 >> 0x18 | (uVar36 & 0xff0000) >> 8 | (uVar36 & 0xff00) << 8 |
                    uVar36 << 0x18;
      local_7c = (uint)uVar33;
      FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
      FUN_0048f080(local_78[0] + 8,local_58);
      if (local_40 == 0xff) {
LAB_00184935:
        pcVar23 = FUN_004732c0;
        local_40 = 4;
      }
      else {
        pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
        if (local_40 != 4) {
          (*pcVar23)(local_58);
          goto LAB_00184935;
        }
      }
      (*pcVar23)(local_58);
      local_10c = local_10c - 2;
    } while (local_10c != 0);
    lVar26 = *(long *)(param_2 + 0x18);
    iVar15 = (int)lVar26;
  } while( true );
LAB_001851cb:
  (**(code **)(*plVar32 + 0x28))(plVar32,pvVar28,uVar43);
  goto joined_r0x00185127;
LAB_001846ab:
  (**(code **)(*plVar32 + 0x28))(plVar32,pvVar28,uVar8);
  goto joined_r0x00184607;
  while( true ) {
    (*pcVar23)(local_58);
    local_10c = local_10c - 2;
    if (local_10c == 0) break;
LAB_0018555a:
    lVar30 = *(long *)(param_2 + 0x18);
    *(long *)(param_2 + 0x18) = lVar30 + 2;
    uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
    *(long *)(param_2 + 0x18) = lVar30 + 6;
    local_58[0] = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
    local_40 = 0;
    local_7c = (uint)uVar33;
    FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
    FUN_0048f080(local_78[0] + 8,local_58);
    if (local_40 == 0xff) {
LAB_001855e5:
      local_40 = 4;
      pcVar23 = FUN_004732c0;
    }
    else {
      pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
      if (local_40 != 4) {
        (*pcVar23)(local_58);
        goto LAB_001855e5;
      }
    }
    (*pcVar23)(local_58);
    lVar30 = *(long *)(param_2 + 0x18);
    *(long *)(param_2 + 0x18) = lVar30 + 2;
    uVar33 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar30);
    *(long *)(param_2 + 0x18) = lVar30 + 6;
    local_58[0] = *(uint *)(*(long *)(param_2 + 0x10) + 2 + lVar30);
    local_40 = 0;
    local_7c = (uint)uVar33;
    FUN_00ae4c30(local_78,lVar26,&local_7c,uVar33);
    FUN_0048f080(local_78[0] + 8,local_58);
    if (local_40 == 0xff) {
LAB_00185691:
      local_40 = 4;
      pcVar23 = FUN_004732c0;
    }
    else {
      pcVar23 = (code *)(&PTR_FUN_01384ba0)[local_40];
      if (local_40 != 4) {
        (*pcVar23)(local_58);
        goto LAB_00185691;
      }
    }
  }
  goto LAB_00184525;
}


```

## `jag::packethandlers::Inventory::UPDATE_INV_GROUP` @ 001a73e0
```c

void jag::packethandlers::Inventory::UPDATE_INV_GROUP(long param_1,PacketCore *param_2)

{
  byte bVar1;
  byte bVar2;
  char cVar3;
  undefined1 uVar4;
  long lVar5;
  void *pvVar6;
  ulong uVar7;
  ulong uVar8;
  ulong uVar9;
  ulong uVar10;
  ushort uVar11;
  ushort uVar12;
  uint uVar13;
  uint uVar14;
  ulong *puVar15;
  ulong *puVar16;
  char ******ppppppcVar17;
  ulong *puVar18;
  ulong *puVar19;
  int iVar20;
  ulong *puVar21;
  ulong *puVar22;
  ulong uVar23;
  ulong uVar24;
  bool bVar25;
  ulong *local_f0;
  uint local_e0;
  uint local_c8;
  byte local_c1;
  uint local_b0;
  uint local_ac;
  char *****local_a8 [2];
  char local_91;
  ulong local_88;
  ulong uStack_80;
  undefined8 local_78;
  ulong local_68;
  ulong uStack_60;
  undefined8 local_58;
  int local_50;
  uint local_4c;
  uint local_48;
  undefined1 local_44;
  
  lVar5 = param_2->position;
  param_2->position = lVar5 + 1;
  bVar1 = *(byte *)((long)param_2->bufData + lVar5);
  if (5 < bVar1 - 1) {
    return;
  }
  param_2->position = lVar5 + 2;
  bVar2 = *(byte *)((long)param_2->bufData + lVar5 + 1);
  if ((bVar2 & 1) != 0) {
    return;
  }
  if ((bVar2 & 2) == 0) {
    return;
  }
  uVar13 = Packet::gT_unsigned_int(param_2);
  *(ulong *)(param_1 + 8) = (ulong)uVar13;
  uVar13 = Packet::gT_unsigned_int(param_2);
  *(uint *)(param_1 + 0x28) = uVar13;
  if (bVar1 < 4) {
    if (uVar13 != 0) {
      *(uint *)(param_1 + 0x28) = uVar13 + 0x10211a0;
    }
    uVar11 = FUN_00121a30(param_2);
    lVar5 = param_2->position;
    param_2->position = lVar5 + 1;
    local_c1 = *(byte *)((long)param_2->bufData + lVar5);
    FUN_00afd8d0(param_2,param_1 + 0x10);
    *(undefined4 *)(param_1 + 0x2c) = 0;
  }
  else {
    uVar11 = FUN_00121a30(param_2);
    lVar5 = param_2->position;
    param_2->position = lVar5 + 1;
    local_c1 = *(byte *)((long)param_2->bufData + lVar5);
    FUN_00afd8d0(param_2,param_1 + 0x10);
    uVar13 = Packet::gT_unsigned_int(param_2);
    *(uint *)(param_1 + 0x2c) = uVar13;
  }
  local_c8 = (uint)local_c1;
  uVar24 = (ulong)uVar11;
  lVar5 = param_2->position;
  pvVar6 = param_2->bufData;
  param_2->position = lVar5 + 1;
  *(bool *)(param_1 + 0x30) = *(char *)((long)pvVar6 + lVar5) == '\x01';
  param_2->position = lVar5 + 2;
  *(int *)(param_1 + 0x34) = (int)*(char *)((long)pvVar6 + lVar5 + 1);
  param_2->position = lVar5 + 3;
  *(int *)(param_1 + 0x38) = (int)*(char *)((long)pvVar6 + lVar5 + 2);
  param_2->position = lVar5 + 4;
  *(int *)(param_1 + 0x3c) = (int)*(char *)((long)pvVar6 + lVar5 + 3);
  param_2->position = lVar5 + 5;
  *(bool *)(param_1 + 0x40) = *(char *)((long)pvVar6 + lVar5 + 4) == '\x01';
  if (uVar11 != 0) {
    puVar16 = *(ulong **)(param_1 + 0x48);
    if (*(ulong **)(param_1 + 0x50) != puVar16) {
      puVar15 = puVar16 + 5;
      puVar18 = (ulong *)((long)puVar16 +
                         ((long)*(ulong **)(param_1 + 0x50) - (long)puVar15 & 0xfffffffffffffff8U) +
                         0x28);
      uVar23 = ((ulong)((long)puVar18 - (long)puVar15) >> 3) * 5;
      uVar13 = (uint)uVar23 & 7;
      puVar21 = puVar16;
      puVar22 = puVar15;
      if ((uVar23 & 7) != 0) {
        if ((*(char *)((long)puVar16 + 0x17) < '\0') && (*puVar16 != 0)) {
          HeapInterface::Free();
        }
        puVar22 = puVar16 + 10;
        puVar21 = puVar15;
        if (uVar13 != 1) {
          if (uVar13 != 2) {
            puVar21 = puVar22;
            if (uVar13 != 3) {
              puVar21 = puVar15;
              if (uVar13 != 4) {
                puVar21 = puVar22;
                if (uVar13 != 5) {
                  puVar21 = puVar15;
                  if (uVar13 != 6) {
                    if ((*(char *)((long)puVar16 + 0x3f) < '\0') && (*puVar15 != 0)) {
                      HeapInterface::Free();
                    }
                    puVar21 = puVar22;
                    puVar22 = puVar16 + 0xf;
                  }
                  puVar15 = puVar22;
                  if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
                    HeapInterface::Free();
                  }
                  puVar21 = puVar15 + 5;
                }
                if ((*(char *)((long)puVar15 + 0x17) < '\0') && (*puVar15 != 0)) {
                  HeapInterface::Free();
                }
                puVar22 = puVar21 + 5;
              }
              puVar15 = puVar22;
              if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
                HeapInterface::Free();
              }
              puVar21 = puVar15 + 5;
            }
            if ((*(char *)((long)puVar15 + 0x17) < '\0') && (*puVar15 != 0)) {
              HeapInterface::Free();
            }
            puVar22 = puVar21 + 5;
          }
          if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
            HeapInterface::Free();
          }
          puVar21 = puVar22;
          puVar22 = puVar22 + 5;
        }
      }
      while( true ) {
        if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
          HeapInterface::Free();
        }
        if (puVar18 == puVar22) break;
        if ((*(char *)((long)puVar22 + 0x17) < '\0') && (*puVar22 != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0x3f) < '\0') && (puVar22[5] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0x67) < '\0') && (puVar22[10] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0x8f) < '\0') && (puVar22[0xf] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0xb7) < '\0') && (puVar22[0x14] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0xdf) < '\0') && (puVar22[0x19] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)puVar22 + 0x107) < '\0') && (puVar22[0x1e] != 0)) {
          HeapInterface::Free();
        }
        puVar21 = puVar22 + 0x23;
        puVar22 = puVar22 + 0x28;
      }
      puVar16 = *(ulong **)(param_1 + 0x48);
    }
    *(ulong **)(param_1 + 0x50) = puVar16;
    if ((ulong)((*(long *)(param_1 + 0x58) - (long)puVar16 >> 3) * -0x3333333333333333) < uVar24) {
      if (uVar24 == 0) {
        local_f0 = (ulong *)0x0;
        puVar15 = (ulong *)0x0;
      }
      else {
        puVar15 = (ulong *)thunk_FUN_00c29480(uVar24 * 0x28);
        puVar16 = *(ulong **)(param_1 + 0x50);
        puVar18 = *(ulong **)(param_1 + 0x48);
        local_f0 = puVar15;
        if (puVar18 != puVar16) {
          puVar21 = puVar18 + 5;
          uVar23 = ((ulong)((long)puVar16 - (long)puVar21) >> 3) * 0xccccccccccccccd &
                   0x1fffffffffffffff;
          puVar16 = puVar15;
          if (((int)((ulong)((long)(puVar21 + uVar23 * 5) + (-0x28 - (long)puVar18)) >> 3) *
               -0x33333333 & 1U) != 0) goto LAB_001a7f40;
          *(undefined1 *)puVar15 = 0;
          *(undefined1 *)((long)puVar15 + 0x17) = 0x17;
          local_68 = *puVar15;
          uStack_60 = puVar15[1];
          local_58 = puVar15[2];
          uVar7 = puVar18[1];
          *puVar15 = *puVar18;
          puVar15[1] = uVar7;
          puVar15[2] = puVar18[2];
          *puVar18 = local_68;
          puVar18[1] = uStack_60;
          puVar18[2] = local_58;
          *(undefined1 *)((long)puVar18 + 0x17) = 0x17;
          *(int *)(puVar15 + 3) = (int)puVar18[3];
          *(undefined4 *)((long)puVar15 + 0x1c) = *(undefined4 *)((long)puVar18 + 0x1c);
          uVar4 = *(undefined1 *)((long)puVar18 + 0x24);
          *(int *)(puVar15 + 4) = (int)puVar18[4];
          *(undefined1 *)((long)puVar15 + 0x24) = uVar4;
          puVar16 = puVar15 + 5;
          for (puVar18 = puVar21; puVar21 + uVar23 * 5 != puVar18; puVar18 = puVar18 + 10) {
LAB_001a7f40:
            *(undefined1 *)puVar16 = 0;
            *(undefined1 *)((long)puVar16 + 0x17) = 0x17;
            uVar8 = *puVar16;
            uVar9 = puVar16[1];
            uVar7 = puVar16[2];
            uVar10 = puVar18[1];
            *puVar16 = *puVar18;
            puVar16[1] = uVar10;
            puVar16[2] = puVar18[2];
            uVar4 = *(undefined1 *)((long)puVar18 + 0x24);
            *puVar18 = uVar8;
            puVar18[1] = uVar9;
            puVar18[2] = uVar7;
            *(undefined1 *)((long)puVar18 + 0x17) = 0x17;
            *(int *)(puVar16 + 3) = (int)puVar18[3];
            *(undefined4 *)((long)puVar16 + 0x1c) = *(undefined4 *)((long)puVar18 + 0x1c);
            uVar7 = puVar18[4];
            *(undefined1 *)(puVar16 + 5) = 0;
            *(undefined1 *)((long)puVar16 + 0x24) = uVar4;
            local_68 = puVar16[5];
            uStack_60 = puVar16[6];
            *(int *)(puVar16 + 4) = (int)uVar7;
            *(undefined1 *)((long)puVar16 + 0x3f) = 0x17;
            local_58 = puVar16[7];
            uVar7 = puVar18[6];
            puVar16[5] = puVar18[5];
            puVar16[6] = uVar7;
            puVar16[7] = puVar18[7];
            uVar4 = *(undefined1 *)((long)puVar18 + 0x4c);
            puVar18[5] = local_68;
            puVar18[6] = uStack_60;
            puVar18[7] = local_58;
            *(undefined1 *)((long)puVar18 + 0x3f) = 0x17;
            *(int *)(puVar16 + 8) = (int)puVar18[8];
            *(undefined4 *)((long)puVar16 + 0x44) = *(undefined4 *)((long)puVar18 + 0x44);
            uVar7 = puVar18[9];
            *(undefined1 *)((long)puVar16 + 0x4c) = uVar4;
            *(int *)(puVar16 + 9) = (int)uVar7;
            puVar16 = puVar16 + 10;
          }
          puVar16 = *(ulong **)(param_1 + 0x50);
          puVar18 = *(ulong **)(param_1 + 0x48);
          local_f0 = puVar15 + uVar23 * 5 + 5;
          if (puVar18 != puVar16) {
            puVar21 = puVar18 + 5;
            puVar22 = (ulong *)((long)puVar18 +
                               ((long)puVar16 - (long)puVar21 & 0xfffffffffffffff8U) + 0x28);
            uVar23 = ((ulong)((long)puVar22 - (long)puVar21) >> 3) * 5;
            uVar13 = (uint)uVar23 & 7;
            puVar19 = puVar21;
            puVar16 = puVar18;
            if ((uVar23 & 7) != 0) {
              if ((*(char *)((long)puVar18 + 0x17) < '\0') && (*puVar18 != 0)) {
                HeapInterface::Free();
              }
              puVar19 = puVar18 + 10;
              puVar16 = puVar21;
              if (uVar13 != 1) {
                puVar16 = puVar19;
                if (uVar13 != 2) {
                  puVar16 = puVar21;
                  if (uVar13 != 3) {
                    puVar16 = puVar19;
                    if (uVar13 != 4) {
                      puVar16 = puVar21;
                      if (uVar13 != 5) {
                        puVar16 = puVar19;
                        if (uVar13 != 6) {
                          if ((*(char *)((long)puVar18 + 0x3f) < '\0') && (*puVar21 != 0)) {
                            HeapInterface::Free();
                          }
                          puVar16 = puVar18 + 0xf;
                          puVar21 = puVar19;
                        }
                        if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
                          HeapInterface::Free();
                        }
                        puVar19 = puVar16 + 5;
                      }
                      if ((*(char *)((long)puVar16 + 0x17) < '\0') && (*puVar16 != 0)) {
                        HeapInterface::Free();
                      }
                      puVar16 = puVar19 + 5;
                      puVar21 = puVar19;
                    }
                    if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
                      HeapInterface::Free();
                    }
                    puVar19 = puVar16 + 5;
                  }
                  if ((*(char *)((long)puVar16 + 0x17) < '\0') && (*puVar16 != 0)) {
                    HeapInterface::Free();
                  }
                  puVar16 = puVar19 + 5;
                  puVar21 = puVar19;
                }
                if ((*(char *)((long)puVar21 + 0x17) < '\0') && (*puVar21 != 0)) {
                  HeapInterface::Free();
                }
                puVar19 = puVar16 + 5;
              }
            }
            while( true ) {
              if ((*(char *)((long)puVar16 + 0x17) < '\0') && (*puVar16 != 0)) {
                HeapInterface::Free();
              }
              if (puVar19 == puVar22) break;
              if ((*(char *)((long)puVar19 + 0x17) < '\0') && (*puVar19 != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)puVar19 + 0x3f) < '\0') && (puVar19[5] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)puVar19 + 0x67) < '\0') && (puVar19[10] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)puVar19 + 0x8f) < '\0') && (puVar19[0xf] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)puVar19 + 0xb7) < '\0') && (puVar19[0x14] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)puVar19 + 0xdf) < '\0') && (puVar19[0x19] != 0)) {
                HeapInterface::Free();
              }
              puVar16 = puVar19 + 0x23;
              if ((*(char *)((long)puVar19 + 0x107) < '\0') && (puVar19[0x1e] != 0)) {
                HeapInterface::Free();
              }
              puVar19 = puVar19 + 0x28;
            }
            puVar16 = *(ulong **)(param_1 + 0x48);
          }
        }
      }
      if (puVar16 != (ulong *)0x0) {
        HeapInterface::Free();
      }
      *(ulong **)(param_1 + 0x48) = puVar15;
      *(ulong **)(param_1 + 0x58) = puVar15 + uVar24 * 5;
      *(ulong **)(param_1 + 0x50) = local_f0;
    }
    uVar13 = 0;
    do {
      local_a8[0] = (char *****)((ulong)local_a8[0] & 0xffffffffffffff00);
      local_91 = '\x17';
      uVar14 = 0;
      FUN_00ad80e0(param_2,local_a8);
      lVar5 = param_2->position;
      param_2->position = lVar5 + 1;
      iVar20 = (int)*(char *)((long)param_2->bufData + lVar5);
      if ((bVar1 < 2) || (uVar14 = Packet::gT_unsigned_int(param_2), bVar1 < 5)) {
        local_e0 = 0;
        bVar25 = false;
      }
      else {
        uVar12 = FUN_00121a30(param_2);
        local_e0 = (uint)uVar12;
        if (bVar1 == 5) {
          bVar25 = false;
        }
        else {
          lVar5 = param_2->position;
          param_2->position = lVar5 + 1;
          bVar25 = *(char *)((long)param_2->bufData + lVar5) == '\x01';
        }
      }
      ppppppcVar17 = local_a8;
      if (local_91 < '\0') {
        ppppppcVar17 = (char ******)local_a8[0];
      }
      local_68 = local_68 & 0xffffffffffffff00;
      local_58 = CONCAT17(0x17,(undefined7)local_58);
      cVar3 = *(char *)ppppppcVar17;
      while (cVar3 != '\0') {
        ppppppcVar17 = (char ******)((long)ppppppcVar17 + 1);
        cVar3 = *(char *)ppppppcVar17;
      }
      FUN_0048d690(&local_68);
      puVar16 = *(ulong **)(param_1 + 0x50);
      local_48 = local_e0;
      local_50 = iVar20;
      local_4c = uVar14;
      local_44 = bVar25;
      if (puVar16 < *(ulong **)(param_1 + 0x58)) {
        *(ulong **)(param_1 + 0x50) = puVar16 + 5;
        *(undefined1 *)puVar16 = 0;
        local_88 = *puVar16;
        uStack_80 = puVar16[1];
        *(undefined1 *)((long)puVar16 + 0x17) = 0x17;
        local_78 = puVar16[2];
        *puVar16 = local_68;
        puVar16[1] = uStack_60;
        puVar16[2] = local_58;
        *(int *)(puVar16 + 3) = iVar20;
        *(uint *)((long)puVar16 + 0x1c) = uVar14;
        *(bool *)((long)puVar16 + 0x24) = bVar25;
        *(uint *)(puVar16 + 4) = local_e0;
        local_68 = local_88;
        uStack_60 = uStack_80;
        local_58 = local_78;
      }
      else {
        FUN_00177bd0(param_1 + 0x48,&local_68);
      }
      if (((long)local_58 < 0) && (local_68 != 0)) {
        HeapInterface::Free();
      }
      if ((local_91 < '\0') && ((char ******)local_a8[0] != (char ******)0x0)) {
        eastl__basic_string();
      }
      uVar13 = uVar13 + 1;
    } while (uVar13 != uVar11);
    FUN_00171ee0(param_1);
  }
  if (local_c8 != 0) {
    lVar5 = param_1 + 0x80;
    FUN_00ab7b20(*(undefined8 *)(param_1 + 0x80),*(undefined8 *)(param_1 + 0x88));
    *(long *)(param_1 + 0x88) = *(long *)(param_1 + 0x80);
    if ((ulong)((*(long *)(param_1 + 0x90) - *(long *)(param_1 + 0x80) >> 3) * -0x5555555555555555)
        < (ulong)local_c1) {
      FUN_004890f0(lVar5);
    }
    uVar13 = 0;
    if ((local_c1 & 1) != 0) {
      local_88 = local_88 & 0xffffffffffffff00;
      local_78 = CONCAT17(0x17,(undefined7)local_78);
      FUN_00afd8d0(param_2,&local_88);
      puVar16 = *(ulong **)(param_1 + 0x88);
      if (puVar16 < *(ulong **)(param_1 + 0x90)) {
        *(ulong **)(param_1 + 0x88) = puVar16 + 3;
        *(undefined1 *)puVar16 = 0;
        local_68 = *puVar16;
        uStack_60 = puVar16[1];
        *(undefined1 *)((long)puVar16 + 0x17) = 0x17;
        local_58 = puVar16[2];
        *puVar16 = local_88;
        puVar16[1] = uStack_80;
        puVar16[2] = local_78;
        local_88 = local_68;
        uStack_80 = uStack_60;
        local_78 = local_58;
      }
      else {
        FUN_00125c60(lVar5,&local_88);
      }
      if (((long)local_78 < 0) && (local_88 != 0)) {
        HeapInterface::Free();
      }
      uVar13 = 1;
      if (local_c8 == 1) goto LAB_001a7533;
    }
    do {
      local_88 = local_88 & 0xffffffffffffff00;
      local_78 = CONCAT17(0x17,(undefined7)local_78);
      FUN_00afd8d0(param_2,&local_88);
      puVar16 = *(ulong **)(param_1 + 0x88);
      if (puVar16 < *(ulong **)(param_1 + 0x90)) {
        *(ulong **)(param_1 + 0x88) = puVar16 + 3;
        *(undefined1 *)puVar16 = 0;
        local_68 = *puVar16;
        uStack_60 = puVar16[1];
        *(undefined1 *)((long)puVar16 + 0x17) = 0x17;
        local_58 = puVar16[2];
        *puVar16 = local_88;
        puVar16[1] = uStack_80;
        puVar16[2] = local_78;
        local_88 = local_68;
        uStack_80 = uStack_60;
        local_78 = local_58;
      }
      else {
        FUN_00125c60(lVar5,&local_88);
      }
      if (((long)local_78 < 0) && (local_88 != 0)) {
        HeapInterface::Free();
      }
      local_88 = local_88 & 0xffffffffffffff00;
      local_78 = CONCAT17(0x17,(undefined7)local_78);
      FUN_00afd8d0(param_2,&local_88);
      puVar16 = *(ulong **)(param_1 + 0x88);
      if (puVar16 < *(ulong **)(param_1 + 0x90)) {
        *(ulong **)(param_1 + 0x88) = puVar16 + 3;
        *(undefined1 *)puVar16 = 0;
        local_68 = *puVar16;
        uStack_60 = puVar16[1];
        *(undefined1 *)((long)puVar16 + 0x17) = 0x17;
        local_58 = puVar16[2];
        *puVar16 = local_88;
        puVar16[1] = uStack_80;
        puVar16[2] = local_78;
        local_88 = local_68;
        uStack_80 = uStack_60;
        local_78 = local_58;
      }
      else {
        FUN_00125c60(lVar5,&local_88);
      }
      if (((long)local_78 < 0) && (local_88 != 0)) {
        HeapInterface::Free();
      }
      uVar13 = uVar13 + 2;
    } while (uVar13 != local_c8);
  }
LAB_001a7533:
  if ((2 < bVar1) && (uVar11 = FUN_00121a30(param_2), uVar11 != 0)) {
    FUN_004e4500(*(undefined8 *)(param_1 + 0xa0),*(undefined8 *)(param_1 + 0xa8));
    *(undefined8 *)(param_1 + 0xb0) = 0;
    iVar20 = uVar11 - 1;
    if ((uVar11 & 1) != 0) goto LAB_001a766c;
    do {
      uVar13 = Packet::gT_unsigned_int(param_2);
      local_50 = CONCAT31(local_50._1_3_,4);
      if (uVar13 >> 0x1e == 0) {
        local_ac = Packet::gT_unsigned_int(param_2);
        InterfaceManager::SetUpdateSlotValue(&local_68,&local_ac);
      }
      else if (uVar13 >> 0x1e == 1) {
        local_a8[0] = (char *****)Packet::gT_ulong(param_2);
        FUN_00193400(&local_68,local_a8);
      }
      else if (uVar13 >> 0x1e == 2) {
        local_88 = local_88 & 0xffffffffffffff00;
        local_78 = CONCAT17(0x17,(undefined7)local_78);
        FUN_00afd8d0(param_2,&local_88);
        FUN_00193190(&local_68,&local_88);
        if (((long)local_78 < 0) && (local_88 != 0)) {
          HeapInterface::Free();
        }
      }
      local_b0 = uVar13 & 0x3fffffff;
      iVar20 = iVar20 + -1;
      FUN_00ae4c30(&local_88,param_1 + 0x98,&local_b0);
      FUN_0048f080(local_88 + 8,&local_68);
      FUN_0047fe00(&local_68);
LAB_001a766c:
      uVar13 = Packet::gT_unsigned_int(param_2);
      uVar14 = uVar13 >> 0x1e;
      local_50 = CONCAT31(local_50._1_3_,4);
      if (uVar14 == 0) {
        local_ac = Packet::gT_unsigned_int(param_2);
        InterfaceManager::SetUpdateSlotValue(&local_68,&local_ac);
      }
      else if (uVar14 == 1) {
        local_a8[0] = (char *****)Packet::gT_ulong(param_2);
        FUN_00193400(&local_68,local_a8);
      }
      else if (uVar14 == 2) {
        local_88 = local_88 & 0xffffffffffffff00;
        local_78 = CONCAT17(0x17,(undefined7)local_78);
        FUN_00afd8d0(param_2,&local_88);
        FUN_00193190(&local_68,&local_88);
        if (((long)local_78 < 0) && (local_88 != 0)) {
          HeapInterface::Free();
        }
      }
      local_b0 = uVar13 & 0x3fffffff;
      iVar20 = iVar20 + -1;
      FUN_00ae4c30(&local_88,param_1 + 0x98,&local_b0);
      FUN_0048f080(local_88 + 8,&local_68);
      FUN_0047fe00(&local_68);
    } while (iVar20 != -1);
  }
  return;
}


```

## `jag::packethandlers::Inventory::UPDATE_INV_GROUP` @ 001a8a60
```c

undefined *
jag::packethandlers::Inventory::UPDATE_INV_GROUP(long *param_1,long param_2,int *param_3)

{
  int iVar1;
  long lVar2;
  long *plVar3;
  ushort uVar4;
  undefined *puVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  code *pcVar9;
  ushort uVar10;
  long lVar11;
  undefined4 local_8c;
  long local_88 [4];
  uint local_68;
  undefined1 local_60 [24];
  byte local_48;
  
  iVar1 = *param_3;
  lVar2 = *(long *)(param_2 + 0x18);
  lVar8 = *(long *)(param_2 + 0x10);
  lVar6 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar11 = *(long *)(*(long *)((long)&__DT_RELA[0xcfc].r_addend + *param_1) + 8);
  lVar7 = lVar2 + 3;
  *(undefined4 *)(lVar6 + 0x160) = *(undefined4 *)(*(long *)(lVar6 + 0x158) + 0x10);
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  puVar5 = &DAT_015d35c0;
  uVar10 = *(ushort *)(lVar8 + lVar2);
  *(long *)(param_2 + 0x18) = lVar7;
  if (DAT_01050dc0 == 0x3020100) {
    uVar10 = uVar10 << 8 | uVar10 >> 8;
  }
  if (lVar11 != 0) {
    lVar6 = -3;
    lVar11 = (ulong)uVar10 * 0xb0 + *(long *)(lVar11 + 0x60);
    if (*(char *)(lVar8 + 2 + lVar2) == '\x01') {
      FUN_004e4500(*(undefined8 *)(lVar11 + 0x50),*(undefined8 *)(lVar11 + 0x58));
      *(undefined8 *)(lVar11 + 0x60) = 0;
      lVar7 = *(long *)(param_2 + 0x18);
      lVar6 = lVar2 - lVar7;
    }
    if (lVar6 + iVar1 != 0) {
      do {
        local_48 = 4;
        local_68 = 0;
        plVar3 = *(long **)((long)&__DT_RELA[0xca6].r_info + *param_1);
        pcVar9 = *(code **)(*plVar3 + 0x50);
        if (pcVar9 == FUN_000ec430) {
          lVar8 = plVar3[0x3f];
        }
        else {
          lVar8 = (*pcVar9)();
          lVar7 = *(long *)(param_2 + 0x18);
        }
        plVar3 = *(long **)(lVar8 + 0x38);
        lVar8 = *plVar3;
        *(long *)(param_2 + 0x18) = lVar7 + 2;
        uVar10 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar7);
        uVar4 = uVar10 << 8 | uVar10 >> 8;
        if (DAT_01050dc0 != 0x3020100) {
          uVar4 = uVar10;
        }
        lVar7 = (**(code **)(lVar8 + 0x40))(plVar3,uVar4,0);
        plVar3 = *(long **)(*(long *)(*(long *)(lVar7 + 8) + 0x40) + 8);
        local_68 = (uint)uVar4;
        (**(code **)(*plVar3 + 0x20))(plVar3,local_60,param_2);
        plVar3 = *(long **)((long)&__DT_RELA[0xca6].r_info + *param_1);
        pcVar9 = *(code **)(*plVar3 + 0x50);
        if (pcVar9 == FUN_000ec430) {
          lVar7 = plVar3[0x3f];
        }
        else {
          lVar7 = (*pcVar9)();
        }
        lVar7 = (**(code **)(**(long **)(lVar7 + 0x38) + 0x40))(*(long **)(lVar7 + 0x38),local_68,0)
        ;
        pcVar9 = *(code **)(*(long *)(lVar11 + 0x40) + 0x18);
        if (pcVar9 == FUN_0048f160) {
          local_8c = *(undefined4 *)(*(long *)(lVar7 + 8) + 8);
          FUN_00ae4c30(local_88,lVar11 + 0x48,&local_8c);
          FUN_0048f080(local_88[0] + 8,local_60);
        }
        else {
          (*pcVar9)(lVar11 + 0x40,lVar7,local_60);
        }
        if (local_48 == 0xff) {
LAB_001a8c49:
          pcVar9 = FUN_004732c0;
          local_48 = 4;
        }
        else {
          pcVar9 = (code *)(&PTR_FUN_01384ba0)[local_48];
          if (local_48 != 4) {
            (*pcVar9)(local_60);
            goto LAB_001a8c49;
          }
        }
        (*pcVar9)(local_60);
        lVar7 = *(long *)(param_2 + 0x18);
      } while ((lVar2 - lVar7) + (long)iVar1 != 0);
    }
    puVar5 = &DAT_015d3620;
  }
  return puVar5;
}


```

## `jag::packethandlers::Inventory::UPDATE_INV_FULL_impl` @ 001a8d30
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::Inventory::UPDATE_INV_FULL_impl(long *param_1,long param_2)

{
  long *plVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  byte bVar6;
  byte bVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined8 uVar10;
  undefined8 uVar11;
  undefined8 uVar12;
  undefined8 uVar13;
  undefined8 uVar14;
  undefined8 uVar15;
  void *pvVar16;
  ushort uVar17;
  long *plVar18;
  ulong uVar19;
  undefined8 *puVar20;
  undefined8 *puVar21;
  undefined1 (*pauVar22) [16];
  undefined1 (*pauVar23) [16];
  ushort uVar24;
  uint uVar25;
  undefined8 *puVar26;
  undefined8 *puVar27;
  ushort uVar28;
  long lVar29;
  long lVar30;
  void *pvVar31;
  int *piVar32;
  long lVar33;
  long *plVar34;
  long *plVar35;
  uint uVar36;
  ulong uVar37;
  uint uVar38;
  ulong uVar39;
  uint *puVar40;
  code *pcVar41;
  byte *pbVar42;
  uint uVar43;
  ulong local_118;
  uint local_d0;
  undefined1 (*local_b0) [16];
  long local_90;
  uint local_7c;
  uint local_78 [6];
  byte local_60;
  long local_58 [3];
  byte local_40;
  
  lVar29 = *(long *)(param_2 + 0x18);
  uVar10 = *(undefined8 *)((long)&__DT_RELA[0xd02].r_offset + *param_1);
  *(long *)(param_2 + 0x18) = lVar29 + 2;
  uVar24 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar29);
  *(long *)(param_2 + 0x18) = lVar29 + 3;
  bVar6 = *(byte *)(*(long *)(param_2 + 0x10) + 2 + lVar29);
  uVar28 = uVar24 << 8 | uVar24 >> 8;
  if (DAT_01050dc0 != 0x3020100) {
    uVar28 = uVar24;
  }
  plVar18 = (long *)game::InventoryManager::CreateInventory(uVar10,uVar28,bVar6 & 1);
  lVar33 = *(long *)(param_2 + 0x18);
  lVar30 = *(long *)(param_2 + 0x10);
  lVar29 = lVar33 + 2;
  *(long *)(param_2 + 0x18) = lVar29;
  uVar24 = *(ushort *)(lVar30 + lVar33);
  uVar17 = uVar24 << 8 | uVar24 >> 8;
  if (DAT_01050dc0 != 0x3020100) {
    uVar17 = uVar24;
  }
  if (uVar17 != 0) {
    local_118 = 0;
    do {
      lVar33 = lVar29 + 3;
      *(long *)(param_2 + 0x18) = lVar29 + 2;
      pbVar42 = (byte *)(lVar29 + 2 + lVar30);
      uVar24 = *(ushort *)(lVar30 + lVar29);
      uVar43 = (uint)uVar24;
      if (DAT_01050dc0 == 0x3020100) {
        *(long *)(param_2 + 0x18) = lVar33;
        uVar43 = (uint)(ushort)(uVar24 << 8 | uVar24 >> 8);
        uVar25 = (uint)*pbVar42;
        if (uVar25 == 0xff) {
          *(long *)(param_2 + 0x18) = lVar29 + 7;
          uVar25 = *(uint *)(lVar30 + lVar33);
          uVar25 = uVar25 >> 0x18 | (uVar25 & 0xff0000) >> 8 | (uVar25 & 0xff00) << 8 |
                   uVar25 << 0x18;
        }
      }
      else {
        *(long *)(param_2 + 0x18) = lVar33;
        uVar25 = (uint)*pbVar42;
        if (uVar25 == 0xff) {
          *(long *)(param_2 + 0x18) = lVar29 + 7;
          uVar25 = *(uint *)(lVar30 + lVar33);
        }
      }
      local_d0 = 0;
      if ((bVar6 & 2) != 0) {
        lVar29 = *(long *)(param_2 + 0x18);
        *(long *)(param_2 + 0x18) = lVar29 + 1;
        local_d0 = (uint)*(byte *)(lVar30 + lVar29);
      }
      lVar33 = plVar18[2];
      lVar29 = plVar18[3];
      if ((lVar33 == lVar29) || ((int)(lVar29 - lVar33 >> 3) <= (int)local_118)) {
LAB_001a8ef0:
        puVar20 = &DAT_015c8550;
        if ((*(int *)(*plVar18 + 0x3c) == 4) &&
           (lVar30 = *(long *)(*(long *)(*plVar18 + 0x80) + (long)DAT_015decdc * 8), lVar30 != 0)) {
          plVar35 = *(long **)(lVar30 + 0x38);
          puVar20 = (undefined8 *)(**(code **)(*plVar35 + 0x40))(plVar35,(int)plVar18[1],0);
          lVar33 = plVar18[2];
          lVar29 = plVar18[3];
        }
        uVar38 = (int)local_118 + 1U;
        if ((int)((int)local_118 + 1U) <= *(int *)(puVar20[1] + 0x38)) {
          uVar38 = *(uint *)(puVar20[1] + 0x38);
        }
        uVar19 = lVar29 - lVar33 >> 3;
        if ((int)uVar19 <= (int)uVar38) {
          lVar29 = (long)(int)uVar38;
          if (lVar29 == 0) {
            puVar20 = (undefined8 *)0x0;
            puVar21 = (undefined8 *)0x0;
          }
          else {
            puVar21 = (undefined8 *)FUN_00c29480(lVar29 * 8);
            uVar11 = _UNK_00cb7538;
            uVar10 = _DAT_00cb7530;
            puVar20 = puVar21 + lVar29;
            puVar26 = puVar21;
            if (uVar38 == 1) {
LAB_001a94ba:
              *puVar26 = 0xffffffff;
            }
            else {
              uVar39 = 1;
              puVar26 = puVar21 + 2;
              uVar37 = lVar29 - 2U >> 1;
              uVar19 = uVar37 + 1;
              uVar36 = (uint)uVar37 & 7;
              *puVar21 = _DAT_00cb7530;
              puVar21[1] = uVar11;
              if (1 < uVar19) {
                if ((uVar37 & 7) != 0) {
                  if (uVar36 != 1) {
                    if (uVar36 != 2) {
                      if (uVar36 != 3) {
                        if (uVar36 != 4) {
                          if (uVar36 != 5) {
                            if (uVar36 != 6) {
                              *puVar26 = uVar10;
                              puVar21[3] = uVar11;
                              uVar39 = 2;
                              puVar26 = puVar21 + 4;
                            }
                            *puVar26 = uVar10;
                            puVar26[1] = uVar11;
                            uVar39 = uVar39 + 1;
                            puVar26 = puVar26 + 2;
                          }
                          *puVar26 = uVar10;
                          puVar26[1] = uVar11;
                          uVar39 = uVar39 + 1;
                          puVar26 = puVar26 + 2;
                        }
                        *puVar26 = uVar10;
                        puVar26[1] = uVar11;
                        uVar39 = uVar39 + 1;
                        puVar26 = puVar26 + 2;
                      }
                      *puVar26 = uVar10;
                      puVar26[1] = uVar11;
                      uVar39 = uVar39 + 1;
                      puVar26 = puVar26 + 2;
                    }
                    *puVar26 = uVar10;
                    puVar26[1] = uVar11;
                    uVar39 = uVar39 + 1;
                    puVar26 = puVar26 + 2;
                  }
                  uVar39 = uVar39 + 1;
                  *puVar26 = uVar10;
                  puVar26[1] = uVar11;
                  puVar26 = puVar26 + 2;
                  if (uVar19 <= uVar39) goto LAB_001a94a9;
                }
                do {
                  uVar39 = uVar39 + 8;
                  *puVar26 = uVar10;
                  puVar26[1] = uVar11;
                  puVar26[2] = uVar10;
                  puVar26[3] = uVar11;
                  puVar26[4] = uVar10;
                  puVar26[5] = uVar11;
                  puVar26[6] = uVar10;
                  puVar26[7] = uVar11;
                  puVar26[8] = uVar10;
                  puVar26[9] = uVar11;
                  puVar26[10] = uVar10;
                  puVar26[0xb] = uVar11;
                  puVar26[0xc] = uVar10;
                  puVar26[0xd] = uVar11;
                  puVar26[0xe] = uVar10;
                  puVar26[0xf] = uVar11;
                  puVar26 = puVar26 + 0x10;
                } while (uVar39 < uVar19);
              }
LAB_001a94a9:
              puVar26 = puVar21 + uVar19 * 2;
              if (lVar29 != uVar19 * 2) goto LAB_001a94ba;
            }
            lVar33 = plVar18[2];
            uVar19 = plVar18[3] - lVar33 >> 3;
          }
          uVar37 = 0;
          if (uVar19 != 0) {
            do {
              puVar26 = (undefined8 *)(lVar33 + uVar37 * 8);
              puVar27 = puVar21 + uVar37;
              uVar2 = *(undefined4 *)puVar27;
              uVar3 = *(undefined4 *)((long)puVar27 + 4);
              *puVar27 = *puVar26;
              uVar37 = (ulong)((int)uVar37 + 1);
              *(undefined4 *)puVar26 = uVar2;
              *(undefined4 *)((long)puVar26 + 4) = uVar3;
            } while (uVar37 < uVar19);
          }
          plVar18[2] = (long)puVar21;
          plVar18[3] = (long)puVar20;
          plVar18[4] = (long)puVar20;
          if (lVar33 != 0) {
            HeapInterface::Free();
          }
        }
        puVar21 = (undefined8 *)plVar18[6];
        puVar20 = (undefined8 *)plVar18[5];
        uVar19 = ((long)puVar21 - (long)puVar20 >> 3) * 0x6db6db6db6db6db7;
        if ((local_d0 == 0) || ((int)uVar38 < (int)uVar19)) {
          lVar33 = plVar18[2];
        }
        else {
          lVar29 = (long)(int)uVar38;
          if (lVar29 == 0) {
            local_90 = 0;
            local_b0 = (undefined1 (*) [16])0x0;
          }
          else {
            local_b0 = (undefined1 (*) [16])FUN_00c29480(lVar29 * 0x38);
            uVar3 = DAT_00cb74f8;
            uVar2 = DAT_00cb6ad0;
            local_90 = lVar29 * 0x38 + (long)local_b0;
            uVar38 = uVar38 & 3;
            pauVar23 = local_b0;
            if (uVar38 == 0) goto LAB_001a96f1;
            if (uVar38 != 1) {
              pauVar22 = local_b0;
              if (uVar38 != 2) {
                *(undefined8 *)local_b0[3] = 0;
                *(undefined8 *)local_b0[2] = 0;
                *(undefined4 *)(local_b0[2] + 8) = uVar2;
                *(undefined8 *)(local_b0[1] + 8) = 1;
                *(undefined8 **)local_b0[1] = &DAT_01393d40;
                *(undefined4 *)(local_b0[2] + 0xc) = uVar3;
                pauVar22 = (undefined1 (*) [16])(local_b0[3] + 8);
                *local_b0 = (undefined1  [16])0x0;
                *(undefined ***)*local_b0 = &PTR_FUN_0136b598;
                lVar29 = lVar29 + -1;
              }
              *(undefined4 *)(pauVar22[2] + 8) = uVar2;
              lVar29 = lVar29 + -1;
              *(undefined4 *)(pauVar22[2] + 0xc) = uVar3;
              pauVar23 = (undefined1 (*) [16])(pauVar22[3] + 8);
              *(undefined8 *)pauVar22[3] = 0;
              *pauVar22 = (undefined1  [16])0x0;
              *(undefined8 *)pauVar22[2] = 0;
              *(undefined ***)*pauVar22 = &PTR_FUN_0136b598;
              *(undefined8 *)(pauVar22[1] + 8) = 1;
              *(undefined8 **)pauVar22[1] = &DAT_01393d40;
            }
            *(undefined8 *)pauVar23[3] = 0;
            *(undefined8 *)pauVar23[2] = 0;
            *(undefined4 *)(pauVar23[2] + 8) = uVar2;
            *(undefined8 *)(pauVar23[1] + 8) = 1;
            *(undefined8 **)pauVar23[1] = &DAT_01393d40;
            *(undefined4 *)(pauVar23[2] + 0xc) = uVar3;
            *pauVar23 = (undefined1  [16])0x0;
            *(undefined ***)*pauVar23 = &PTR_FUN_0136b598;
            pauVar23 = (undefined1 (*) [16])(pauVar23[3] + 8);
            for (lVar29 = lVar29 + -1; lVar29 != 0; lVar29 = lVar29 + -4) {
LAB_001a96f1:
              *(undefined4 *)(pauVar23[2] + 8) = uVar2;
              *(undefined8 *)pauVar23[3] = 0;
              *(undefined8 *)pauVar23[2] = 0;
              *(undefined4 *)(pauVar23[2] + 0xc) = uVar3;
              *(undefined8 *)(pauVar23[1] + 8) = 1;
              *(undefined4 *)pauVar23[6] = uVar2;
              *pauVar23 = (undefined1  [16])0x0;
              *(undefined8 **)pauVar23[1] = &DAT_01393d40;
              *(undefined1 (*) [16])(pauVar23[3] + 8) = (undefined1  [16])0x0;
              pauVar23[7] = (undefined1  [16])0x0;
              *(undefined1 (*) [16])(pauVar23[10] + 8) = (undefined1  [16])0x0;
              *(undefined4 *)(pauVar23[6] + 4) = uVar3;
              *(undefined4 *)(pauVar23[9] + 8) = uVar2;
              *(undefined ***)*pauVar23 = &PTR_FUN_0136b598;
              *(undefined4 *)(pauVar23[9] + 0xc) = uVar3;
              *(undefined8 *)(pauVar23[6] + 8) = 0;
              *(undefined4 *)pauVar23[0xd] = uVar2;
              *(undefined ***)(pauVar23[3] + 8) = &PTR_FUN_0136b598;
              *(undefined4 *)(pauVar23[0xd] + 4) = uVar3;
              *(undefined8 *)(pauVar23[5] + 8) = 0;
              *(undefined8 *)pauVar23[5] = 1;
              *(undefined8 **)(pauVar23[4] + 8) = &DAT_01393d40;
              *(undefined8 *)pauVar23[10] = 0;
              *(undefined ***)pauVar23[7] = &PTR_FUN_0136b598;
              *(undefined8 *)pauVar23[9] = 0;
              *(undefined8 *)(pauVar23[8] + 8) = 1;
              *(undefined8 **)pauVar23[8] = &DAT_01393d40;
              *(undefined8 *)(pauVar23[0xd] + 8) = 0;
              *(undefined ***)(pauVar23[10] + 8) = &PTR_FUN_0136b598;
              *(undefined8 *)(pauVar23[0xc] + 8) = 0;
              *(undefined8 *)pauVar23[0xc] = 1;
              *(undefined8 **)(pauVar23[0xb] + 8) = &DAT_01393d40;
              pauVar23 = pauVar23 + 0xe;
            }
            puVar21 = (undefined8 *)plVar18[6];
            puVar20 = (undefined8 *)plVar18[5];
          }
          uVar19 = 0;
          uVar38 = 0;
          if (puVar21 != puVar20) {
            do {
              lVar29 = uVar19 * 0x38;
              uVar8 = *(undefined4 *)((long)local_b0 + lVar29 + 0x30);
              uVar10 = *(undefined8 *)((long)local_b0 + lVar29 + 0x10);
              uVar11 = *(undefined8 *)((long)local_b0 + lVar29 + 0x18);
              uVar12 = *(undefined8 *)((long)local_b0 + lVar29 + 0x20);
              uVar2 = *(undefined4 *)((long)local_b0 + lVar29 + 0x28);
              *(undefined4 *)((long)local_b0 + lVar29 + 0x30) = 0;
              uVar3 = *(undefined4 *)((long)local_b0 + lVar29 + 0x2c);
              *(undefined8 **)((long)local_b0 + lVar29 + 0x10) = &DAT_01393d40;
              *(undefined8 *)((long)local_b0 + lVar29 + 0x18) = 1;
              *(undefined8 *)((long)local_b0 + lVar29 + 0x20) = 0;
              if ((undefined8 *)((long)local_b0 + lVar29 + 8) == puVar20 + uVar19 * 7 + 1) {
                lVar33 = puVar20[uVar19 * 7 + 3];
                plVar35 = (long *)puVar20[uVar19 * 7 + 2];
              }
              else {
                FUN_004e4500(&DAT_01393d40,1);
                uVar13 = puVar20[uVar19 * 7 + 5];
                uVar9 = *(undefined4 *)((long)local_b0 + lVar29 + 0x30);
                uVar4 = *(undefined4 *)((long)local_b0 + lVar29 + 0x28);
                *(undefined8 *)((long)local_b0 + lVar29 + 0x20) = 0;
                uVar5 = *(undefined4 *)((long)local_b0 + lVar29 + 0x2c);
                uVar14 = puVar20[uVar19 * 7 + 2];
                *(undefined8 *)((long)local_b0 + lVar29 + 0x28) = uVar13;
                uVar13 = puVar20[uVar19 * 7 + 3];
                uVar15 = *(undefined8 *)((long)local_b0 + lVar29 + 0x18);
                *(undefined4 *)((long)local_b0 + lVar29 + 0x30) =
                     *(undefined4 *)(puVar20 + uVar19 * 7 + 6);
                *(undefined4 *)(puVar20 + uVar19 * 7 + 6) = uVar9;
                *(undefined4 *)(puVar20 + uVar19 * 7 + 5) = uVar4;
                plVar35 = *(long **)((long)local_b0 + lVar29 + 0x10);
                *(undefined4 *)((long)puVar20 + lVar29 + 0x2c) = uVar5;
                *(undefined8 *)((long)local_b0 + lVar29 + 0x10) = uVar14;
                puVar20[uVar19 * 7 + 2] = plVar35;
                *(undefined8 *)((long)local_b0 + lVar29 + 0x18) = uVar13;
                puVar20[uVar19 * 7 + 3] = uVar15;
                uVar13 = *(undefined8 *)((long)local_b0 + lVar29 + 0x20);
                *(undefined8 *)((long)local_b0 + lVar29 + 0x20) = puVar20[uVar19 * 7 + 4];
                lVar33 = puVar20[uVar19 * 7 + 3];
                puVar20[uVar19 * 7 + 4] = uVar13;
              }
              if (lVar33 != 0) {
                plVar1 = plVar35 + lVar33;
                do {
                  pvVar16 = (void *)*plVar35;
joined_r0x001a9937:
                  pvVar31 = pvVar16;
                  if (pvVar16 != (void *)0x0) {
                    do {
                      bVar7 = *(byte *)((long)pvVar31 + 0x20);
                      pvVar16 = *(void **)((long)pvVar31 + 0x28);
                      if ((bVar7 != 0xff) && (bVar7 != 4)) {
                        (*(code *)(&PTR_FUN_01384ba0)[bVar7])((long)pvVar31 + 8);
                      }
                      if (DAT_015ed790 == 0) {
                        free(pvVar31);
                      }
                      else {
                        if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108)) {
                          plVar34 = *(long **)(DAT_015ed790 + 0x198);
                        }
                        else {
                          plVar34 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                        }
                        if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8)) {
                          uVar4 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                        }
                        else {
                          uVar4 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                        }
                        if (*(code **)(*plVar34 + 0x28) != FUN_00a87200) goto LAB_001a99dc;
                        _DAT_015da900 = _DAT_015da900 + 1;
                        free(pvVar31);
                      }
                      pvVar31 = pvVar16;
                      if (pvVar16 == (void *)0x0) break;
                    } while( true );
                  }
                  *plVar35 = 0;
                  plVar35 = plVar35 + 1;
                  if (plVar1 == plVar35) {
                    uVar37 = puVar20[uVar19 * 7 + 3];
                    puVar21 = (undefined8 *)puVar20[uVar19 * 7 + 2];
                    *(undefined4 *)(puVar20 + uVar19 * 7 + 5) = uVar2;
                    *(undefined4 *)(puVar20 + uVar19 * 7 + 6) = uVar8;
                    *(undefined4 *)((long)puVar20 + lVar29 + 0x2c) = uVar3;
                    puVar20[uVar19 * 7 + 2] = uVar10;
                    puVar20[uVar19 * 7 + 3] = uVar11;
                    puVar20[uVar19 * 7 + 4] = uVar12;
                    if (uVar37 != 0) {
                      puVar20 = puVar21;
                      do {
                        pvVar16 = (void *)*puVar20;
                        while (pvVar16 != (void *)0x0) {
                          bVar7 = *(byte *)((long)pvVar16 + 0x20);
                          pvVar31 = *(void **)((long)pvVar16 + 0x28);
                          if ((bVar7 != 0xff) && (bVar7 != 4)) {
                            (*(code *)(&PTR_FUN_01384ba0)[bVar7])((long)pvVar16 + 8);
                          }
                          if (DAT_015ed790 == 0) {
                            free(pvVar16);
                            pvVar16 = pvVar31;
                          }
                          else {
                            if (*(long *)(DAT_015ed790 + 0x100) == *(long *)(DAT_015ed790 + 0x108))
                            {
                              plVar35 = *(long **)(DAT_015ed790 + 0x198);
                            }
                            else {
                              plVar35 = *(long **)(*(long *)(DAT_015ed790 + 0x108) + -8);
                            }
                            if (*(long *)(DAT_015ed790 + 0x1a0) == *(long *)(DAT_015ed790 + 0x1a8))
                            {
                              uVar2 = *(undefined4 *)(DAT_015ed790 + 0x1f8);
                            }
                            else {
                              uVar2 = *(undefined4 *)(*(long *)(DAT_015ed790 + 0x1a8) + -4);
                            }
                            if (*(code **)(*plVar35 + 0x28) == FUN_00a87200) {
                              _DAT_015da900 = _DAT_015da900 + 1;
                              free(pvVar16);
                              pvVar16 = pvVar31;
                            }
                            else {
                              (**(code **)(*plVar35 + 0x28))(plVar35,pvVar16,uVar2);
                              pvVar16 = pvVar31;
                            }
                          }
                        }
                        *puVar20 = 0;
                        puVar20 = puVar20 + 1;
                      } while (puVar20 != puVar21 + uVar37);
                      if ((puVar21 != (undefined8 *)0x0) && (1 < uVar37)) {
                        HeapInterface::Free();
                      }
                    }
                    goto LAB_001a922c;
                  }
                } while( true );
              }
              *(undefined4 *)(puVar20 + uVar19 * 7 + 6) = uVar8;
              *(undefined4 *)(puVar20 + uVar19 * 7 + 5) = uVar2;
              puVar20[uVar19 * 7 + 2] = uVar10;
              puVar20[uVar19 * 7 + 3] = uVar11;
              *(undefined4 *)((long)puVar20 + lVar29 + 0x2c) = uVar3;
              puVar20[uVar19 * 7 + 4] = uVar12;
LAB_001a922c:
              uVar38 = uVar38 + 1;
              uVar19 = (ulong)uVar38;
              puVar21 = (undefined8 *)plVar18[6];
              puVar20 = (undefined8 *)plVar18[5];
            } while (uVar19 < (ulong)(((long)puVar21 - (long)puVar20 >> 3) * 0x6db6db6db6db6db7));
          }
          plVar18[6] = local_90;
          plVar18[7] = local_90;
          plVar18[5] = (long)local_b0;
          if (puVar20 != puVar21) {
            lVar29 = (((ulong)((long)puVar21 - (long)(puVar20 + 7)) >> 3) * 0xdb6db6db6db6db7 &
                     0x1fffffffffffffff) + 1;
            uVar38 = (int)(lVar29 * 0x38 - 0x38U >> 3) * -0x49249249 + 1U & 7;
            puVar21 = puVar20;
            if (uVar38 != 0) {
              puVar26 = puVar20;
              if (uVar38 != 1) {
                if (uVar38 != 2) {
                  if (uVar38 != 3) {
                    if (uVar38 != 4) {
                      if (uVar38 != 5) {
                        if (uVar38 != 6) {
                          (**(code **)*puVar20)(puVar20);
                          puVar21 = puVar20 + 7;
                        }
                        puVar26 = puVar21 + 7;
                        (**(code **)*puVar21)(puVar21);
                      }
                      puVar21 = puVar26 + 7;
                      (**(code **)*puVar26)(puVar26);
                    }
                    puVar26 = puVar21 + 7;
                    (**(code **)*puVar21)(puVar21);
                  }
                  puVar21 = puVar26 + 7;
                  (**(code **)*puVar26)(puVar26);
                }
                puVar26 = puVar21 + 7;
                (**(code **)*puVar21)(puVar21);
              }
              puVar21 = puVar26 + 7;
              (**(code **)*puVar26)(puVar26);
              if (puVar21 == puVar20 + lVar29 * 7) goto LAB_001a9373;
            }
            do {
              (**(code **)*puVar21)(puVar21);
              (**(code **)puVar21[7])(puVar21 + 7);
              (**(code **)puVar21[0xe])(puVar21 + 0xe);
              (**(code **)puVar21[0x15])(puVar21 + 0x15);
              (**(code **)puVar21[0x1c])(puVar21 + 0x1c);
              (**(code **)puVar21[0x23])(puVar21 + 0x23);
              (**(code **)puVar21[0x2a])(puVar21 + 0x2a);
              puVar26 = puVar21 + 0x31;
              puVar27 = puVar21 + 0x31;
              puVar21 = puVar21 + 0x38;
              (**(code **)*puVar26)(puVar27);
            } while (puVar21 != puVar20 + lVar29 * 7);
          }
LAB_001a9373:
          if (puVar20 != (undefined8 *)0x0) {
            HeapInterface::Free(puVar20);
          }
          puVar21 = (undefined8 *)plVar18[6];
          lVar33 = plVar18[2];
          puVar20 = (undefined8 *)plVar18[5];
LAB_001a9396:
          uVar19 = ((long)puVar21 - (long)puVar20 >> 3) * 0x6db6db6db6db6db7;
        }
      }
      else {
        puVar20 = (undefined8 *)plVar18[5];
        puVar21 = (undefined8 *)plVar18[6];
        if (local_d0 == 0) goto LAB_001a9396;
        if ((puVar20 == puVar21) ||
           (uVar19 = ((long)puVar21 - (long)puVar20 >> 3) * 0x6db6db6db6db6db7,
           (int)uVar19 <= (int)local_118)) goto LAB_001a8ef0;
      }
      piVar32 = (int *)(lVar33 + local_118 * 8);
      *piVar32 = uVar43 - 1;
      piVar32[1] = uVar25;
      if (local_118 < uVar19) {
        FUN_004e4500(puVar20[local_118 * 7 + 2],puVar20[local_118 * 7 + 3]);
        puVar20[local_118 * 7 + 4] = 0;
      }
      if (local_d0 != 0) {
        lVar29 = plVar18[5];
        do {
          lVar33 = *(long *)(param_2 + 0x18);
          *(long *)(param_2 + 0x18) = lVar33 + 2;
          uVar24 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar33);
          puVar40 = (uint *)(*(long *)(param_2 + 0x10) + lVar33 + 2);
          if (DAT_01050dc0 == 0x3020100) {
            *(long *)(param_2 + 0x18) = lVar33 + 6;
            uVar43 = *puVar40;
            uVar24 = uVar24 << 8 | uVar24 >> 8;
            local_78[0] = uVar43 >> 0x18 | (uVar43 & 0xff0000) >> 8 | (uVar43 & 0xff00) << 8 |
                          uVar43 << 0x18;
          }
          else {
            *(long *)(param_2 + 0x18) = lVar33 + 6;
            local_78[0] = *puVar40;
          }
          local_60 = 0;
          local_7c = (uint)uVar24;
          FUN_00ae4c30(local_58,lVar29 + 8 + local_118 * 0x38,&local_7c);
          lVar33 = local_58[0];
          bVar7 = *(byte *)(local_58[0] + 0x20);
          uVar19 = (ulong)bVar7;
          if (bVar7 == local_60) {
            if (bVar7 != 0xff) {
              (*(code *)(&PTR_FUN_013853e0)[uVar19])(local_58[0] + 8,local_78);
              goto LAB_001a90e0;
            }
LAB_001a9103:
            pcVar41 = FUN_004732c0;
            local_60 = 4;
          }
          else {
            local_40 = 0xff;
            if (local_60 != 0xff) {
              (*(code *)(&PTR_FUN_013847c0)[local_60])(local_58,local_78);
              local_40 = local_60;
              uVar19 = (ulong)*(byte *)(lVar33 + 0x20);
            }
            if ((char)uVar19 != -1) {
              (*(code *)(&PTR_FUN_01384ba0)[uVar19])(lVar33 + 8);
            }
            *(undefined1 *)(lVar33 + 0x20) = 0xff;
            if (local_40 != 0xff) {
              (*(code *)(&PTR_std____detail____variant____erased_ctor<int&,int&&>_01384c20)
                        [local_40])(lVar33 + 8,local_58);
              *(byte *)(lVar33 + 0x20) = local_40;
              if (local_40 != 0xff) {
                (*(code *)(&PTR_FUN_01384ba0)[local_40])(local_58);
              }
            }
LAB_001a90e0:
            if (local_60 == 0xff) goto LAB_001a9103;
            pcVar41 = (code *)(&PTR_FUN_01384ba0)[local_60];
            if (local_60 != 4) {
              (*pcVar41)(local_78);
              goto LAB_001a9103;
            }
          }
          (*pcVar41)(local_78);
          local_d0 = local_d0 - 1;
        } while (local_d0 != 0);
      }
      local_118 = local_118 + 1;
      if ((ulong)(uVar17 - 1) + 1 == local_118) break;
      lVar30 = *(long *)(param_2 + 0x10);
      lVar29 = *(long *)(param_2 + 0x18);
    } while( true );
  }
  lVar29 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  uVar43 = *(uint *)(lVar29 + 0xb0);
  *(uint *)(lVar29 + 0xb0) = uVar43 + 1;
  *(uint *)(*(long *)(lVar29 + 0xa8) + (ulong)(uVar43 & 0x3f) * 4) = (uint)uVar28;
  return &DAT_015d3620;
LAB_001a99dc:
  (**(code **)(*plVar34 + 0x28))(plVar34,pvVar31,uVar4);
  goto joined_r0x001a9937;
}


```
