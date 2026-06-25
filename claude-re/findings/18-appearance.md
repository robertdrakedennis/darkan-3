
## `jag::PlayerEntity::SetAppearanceAsPlayer` @ 0014d5b0
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void jag::PlayerEntity::SetAppearanceAsPlayer(uint *param_1)

{
  byte bVar1;
  char cVar2;
  short sVar3;
  short sVar4;
  short sVar5;
  undefined4 uVar6;
  undefined8 uVar7;
  undefined8 uVar8;
  uint uVar9;
  uint uVar10;
  ushort uVar11;
  undefined2 uVar12;
  byte *pbVar13;
  undefined8 *puVar14;
  ulong uVar15;
  undefined8 *puVar16;
  char *pcVar17;
  undefined8 uVar18;
  undefined8 *puVar19;
  char *pcVar20;
  uint *puVar21;
  uint *puVar22;
  uint uVar23;
  ulong uVar24;
  uint *puVar25;
  long lVar26;
  long lVar27;
  long lVar28;
  undefined8 *puVar29;
  undefined8 *puVar30;
  undefined8 *puVar31;
  long lVar32;
  long lVar33;
  undefined8 *puVar34;
  bool bVar35;
  ulong uVar36;
  undefined8 *puVar37;
  ulong local_70;
  undefined1 local_68 [16];
  ulong *local_58;
  
                    /* jag::PlayerEntity::SetAppearanceAsPlayer -- decodes the (already
                       un-transformed) PLAYER_INFO APPEARANCE payload. Field order: (1) flags g1;
                       (2) title gSmart1or2 if flags&0x40; (3) extra-model list [g1 count then
                       count*(g2 value + g1 type)] if flags&0x2; (4) gender/bodytype g1 signed; (5)
                       body/equipment model slots via FUN_00141b40 (0/kit/item encoding); (6)
                       base/model-override [g1, then g2 if flags&4 else g1+g1]; (7) colours [g1
                       flag; if !=0 -> 4*g2 + 1*g1]. flags bits: 0x1 visible, 0x2 extras, 0x4
                       npc-morph form, (>>3 &7)+1 = scale, 0x40 title, 0x80 gender-variant. NO
                       username/combat-level/icons here. Full spec:
                       docs/protocol/player-appearance-948.md */
  lVar32 = 2;
  lVar27 = 1;
  puVar21 = param_1 + 0x16;
  pbVar13 = *(byte **)(param_1 + 0x1a);
  param_1[0x1c] = 1;
  param_1[0x1d] = 0;
  bVar1 = *pbVar13;
  uVar23 = (uint)bVar1;
  *param_1 = (uint)bVar1;
  if ((bVar1 & 0x40) != 0) {
    uVar12 = Packet::gSmart1or2(puVar21);
    lVar27 = *(long *)(param_1 + 0x1c);
    *(undefined2 *)(param_1 + 1) = uVar12;
    uVar23 = *param_1;
    pbVar13 = *(byte **)(param_1 + 0x1a);
    lVar32 = lVar27 + 1;
  }
  lVar33 = *(long *)(param_1 + 0x14);
  pbVar13 = pbVar13 + lVar27;
  *(undefined8 *)((long)&__DT_SYMTAB[0xa4].st_name + lVar33) =
       *(undefined8 *)((long)&__DT_SYMTAB[0xa3].st_size + lVar33);
  if ((uVar23 & 2) == 0) goto LAB_0014d958;
  local_58 = (ulong *)0x0;
  *(long *)(param_1 + 0x1c) = lVar32;
  local_68 = (undefined1  [16])0x0;
  bVar1 = *pbVar13;
  if ((bVar1 != 0) && (FUN_00133ac0(local_68,bVar1), bVar1 != 0)) {
    uVar23 = 0;
    do {
      while( true ) {
        local_70 = 0;
        uVar11 = FUN_00121a30(puVar21);
        lVar32 = *(long *)(param_1 + 0x1c);
        uVar18 = local_68._8_8_;
        *(long *)(param_1 + 0x1c) = lVar32 + 1;
        local_70 = (ulong)CONCAT14(*(undefined1 *)(*(long *)(param_1 + 0x1a) + lVar32),(uint)uVar11)
        ;
        if (local_58 <= (ulong)local_68._8_8_) break;
        uVar23 = uVar23 + 1;
        local_68._8_8_ = (ulong *)(local_68._8_8_ + 8);
        *(ulong *)uVar18 = local_70;
        if (bVar1 == uVar23) goto LAB_0014d6ab;
      }
      uVar23 = uVar23 + 1;
      FUN_00124e10(local_68,&local_70);
    } while (bVar1 != uVar23);
  }
LAB_0014d6ab:
  lVar32 = *(long *)(param_1 + 0x14);
  if (local_68._8_8_ == local_68._0_8_) {
    puVar14 = (undefined8 *)0x0;
  }
  else {
    puVar14 = (undefined8 *)thunk_FUN_00c29480();
  }
  if (local_68._8_8_ == local_68._0_8_) {
    puVar37 = *(undefined8 **)((long)&__DT_SYMTAB[0xa3].st_size + lVar32);
    goto LAB_0014e057;
  }
  uVar24 = 0;
  uVar15 = (ulong)(local_68._8_8_ - (long)(local_68._0_8_ + 8)) >> 3;
  uVar23 = (uint)uVar15 & 7;
  if ((uVar15 & 7) != 0) {
    uVar24 = 8;
    *puVar14 = *(undefined8 *)local_68._0_8_;
    if (uVar23 != 1) {
      if (uVar23 != 2) {
        if (uVar23 != 3) {
          if (uVar23 != 4) {
            if (uVar23 != 5) {
              if (uVar23 != 6) {
                puVar14[1] = *(undefined8 *)(local_68._0_8_ + 8);
                uVar24 = 0x10;
              }
              *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24);
              uVar24 = uVar24 + 8;
            }
            *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24);
            uVar24 = uVar24 + 8;
          }
          *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24);
          uVar24 = uVar24 + 8;
        }
        *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24);
        uVar24 = uVar24 + 8;
      }
      *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24);
      uVar24 = uVar24 + 8;
    }
  }
  for (; *(undefined8 *)((long)puVar14 + uVar24) = *(undefined8 *)(local_68._0_8_ + uVar24),
      lVar33 = _UNK_00cb6c88, lVar27 = _UNK_00cb6c78, uVar24 + 8 != uVar15 * 8 + 8;
      uVar24 = uVar24 + 0x40) {
    *(undefined8 *)((long)puVar14 + uVar24 + 8) = *(undefined8 *)(local_68._0_8_ + uVar24 + 8);
    *(undefined8 *)((long)puVar14 + uVar24 + 0x10) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x10)
    ;
    *(undefined8 *)((long)puVar14 + uVar24 + 0x18) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x18)
    ;
    *(undefined8 *)((long)puVar14 + uVar24 + 0x20) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x20)
    ;
    *(undefined8 *)((long)puVar14 + uVar24 + 0x28) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x28)
    ;
    *(undefined8 *)((long)puVar14 + uVar24 + 0x30) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x30)
    ;
    *(undefined8 *)((long)puVar14 + uVar24 + 0x38) = *(undefined8 *)(local_68._0_8_ + uVar24 + 0x38)
    ;
  }
  puVar37 = puVar14 + uVar15 + 1;
  lVar28 = (long)puVar37 - (long)puVar14;
  puVar31 = *(undefined8 **)((long)&__DT_SYMTAB[0xa3].st_size + lVar32);
  uVar15 = lVar28 >> 3;
  if ((ulong)(*(long *)((long)&__DT_SYMTAB[0xa4].st_value + lVar32) - (long)puVar31 >> 3) < uVar15)
  {
    if (uVar15 == 0) {
      puVar16 = (undefined8 *)0x0;
    }
    else {
      puVar16 = (undefined8 *)thunk_FUN_00c29480(lVar28);
      puVar31 = *(undefined8 **)((long)&__DT_SYMTAB[0xa3].st_size + lVar32);
    }
    if (puVar14 != puVar37) {
      lVar33 = 0;
      lVar27 = (uVar24 >> 3) * 8 + 8;
      uVar23 = (int)(uVar24 >> 3) + 1U & 7;
      if (uVar23 != 0) {
        if (uVar23 != 1) {
          if (uVar23 != 2) {
            if (uVar23 != 3) {
              if (uVar23 != 4) {
                if (uVar23 != 5) {
                  if (uVar23 != 6) {
                    lVar33 = 8;
                    *puVar16 = *puVar14;
                  }
                  *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
                  lVar33 = lVar33 + 8;
                }
                *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
                lVar33 = lVar33 + 8;
              }
              *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
              lVar33 = lVar33 + 8;
            }
            *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
            lVar33 = lVar33 + 8;
          }
          *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
          lVar33 = lVar33 + 8;
        }
        *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
        lVar33 = lVar33 + 8;
        if (lVar33 == lVar27) goto LAB_0014d8ee;
      }
      do {
        *(undefined8 *)((long)puVar16 + lVar33) = *(undefined8 *)((long)puVar14 + lVar33);
        *(undefined8 *)((long)puVar16 + lVar33 + 8) = *(undefined8 *)((long)puVar14 + lVar33 + 8);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x10) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x10);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x18) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x18);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x20) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x20);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x28) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x28);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x30) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x30);
        *(undefined8 *)((long)puVar16 + lVar33 + 0x38) =
             *(undefined8 *)((long)puVar14 + lVar33 + 0x38);
        lVar33 = lVar33 + 0x40;
      } while (lVar33 != lVar27);
    }
LAB_0014d8ee:
    if (puVar31 != (undefined8 *)0x0) {
      HeapInterface::Free();
    }
    *(undefined8 **)((long)&__DT_SYMTAB[0xa3].st_size + lVar32) = puVar16;
    *(long *)((long)&__DT_SYMTAB[0xa4].st_name + lVar32) = (long)puVar16 + lVar28;
    *(long *)((long)&__DT_SYMTAB[0xa4].st_value + lVar32) = (long)puVar16 + lVar28;
LAB_0014d92e:
    HeapInterface::Free(puVar14);
  }
  else {
    puVar16 = *(undefined8 **)((long)&__DT_SYMTAB[0xa4].st_name + lVar32);
    lVar26 = (long)puVar16 - (long)puVar31;
    uVar24 = lVar26 >> 3;
    if (uVar24 < uVar15) {
      puVar19 = (undefined8 *)((long)puVar14 + lVar26);
      if (0 < lVar26) {
        if ((puVar14 < puVar31 + 2 && puVar31 < puVar14 + 2) || (lVar26 < 0x29)) {
          uVar23 = (uint)uVar24 & 7;
          puVar34 = puVar14;
          if ((uVar24 & 7) != 0) {
            if (uVar23 != 1) {
              puVar30 = puVar31;
              puVar29 = puVar14;
              if (uVar23 != 2) {
                if (uVar23 != 3) {
                  if (uVar23 != 4) {
                    if (uVar23 != 5) {
                      puVar29 = puVar31;
                      puVar30 = puVar14;
                      if (uVar23 != 6) {
                        puVar30 = puVar14 + 1;
                        puVar29 = puVar31 + 1;
                        *(undefined4 *)puVar31 = *(undefined4 *)puVar14;
                        *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar14 + 4);
                        uVar24 = uVar24 - 1;
                      }
                      uVar24 = uVar24 - 1;
                      puVar34 = puVar30 + 1;
                      puVar31 = puVar29 + 1;
                      *(undefined4 *)puVar29 = *(undefined4 *)puVar30;
                      *(undefined4 *)((long)puVar29 + 4) = *(undefined4 *)((long)puVar30 + 4);
                    }
                    uVar24 = uVar24 - 1;
                    puVar29 = puVar34 + 1;
                    puVar30 = puVar31 + 1;
                    *(undefined4 *)puVar31 = *(undefined4 *)puVar34;
                    *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar34 + 4);
                  }
                  uVar24 = uVar24 - 1;
                  puVar34 = puVar29 + 1;
                  puVar31 = puVar30 + 1;
                  *(undefined4 *)puVar30 = *(undefined4 *)puVar29;
                  *(undefined4 *)((long)puVar30 + 4) = *(undefined4 *)((long)puVar29 + 4);
                }
                uVar24 = uVar24 - 1;
                puVar29 = puVar34 + 1;
                puVar30 = puVar31 + 1;
                *(undefined4 *)puVar31 = *(undefined4 *)puVar34;
                *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar34 + 4);
              }
              uVar24 = uVar24 - 1;
              puVar34 = puVar29 + 1;
              puVar31 = puVar30 + 1;
              *(undefined4 *)puVar30 = *(undefined4 *)puVar29;
              *(undefined4 *)((long)puVar30 + 4) = *(undefined4 *)((long)puVar29 + 4);
            }
            *(undefined4 *)puVar31 = *(undefined4 *)puVar34;
            *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar34 + 4);
            uVar24 = uVar24 - 1;
            puVar31 = puVar31 + 1;
            puVar34 = puVar34 + 1;
            if (uVar24 == 0) goto LAB_0014e23d;
          }
          do {
            *(undefined4 *)puVar31 = *(undefined4 *)puVar34;
            *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar34 + 4);
            *(undefined4 *)(puVar31 + 1) = *(undefined4 *)(puVar34 + 1);
            *(undefined4 *)((long)puVar31 + 0xc) = *(undefined4 *)((long)puVar34 + 0xc);
            *(undefined4 *)(puVar31 + 2) = *(undefined4 *)(puVar34 + 2);
            *(undefined4 *)((long)puVar31 + 0x14) = *(undefined4 *)((long)puVar34 + 0x14);
            *(undefined4 *)(puVar31 + 3) = *(undefined4 *)(puVar34 + 3);
            *(undefined4 *)((long)puVar31 + 0x1c) = *(undefined4 *)((long)puVar34 + 0x1c);
            *(undefined4 *)(puVar31 + 4) = *(undefined4 *)(puVar34 + 4);
            *(undefined4 *)((long)puVar31 + 0x24) = *(undefined4 *)((long)puVar34 + 0x24);
            *(undefined4 *)(puVar31 + 5) = *(undefined4 *)(puVar34 + 5);
            *(undefined4 *)((long)puVar31 + 0x2c) = *(undefined4 *)((long)puVar34 + 0x2c);
            *(undefined4 *)(puVar31 + 6) = *(undefined4 *)(puVar34 + 6);
            *(undefined4 *)((long)puVar31 + 0x34) = *(undefined4 *)((long)puVar34 + 0x34);
            *(undefined4 *)(puVar31 + 7) = *(undefined4 *)(puVar34 + 7);
            *(undefined4 *)((long)puVar31 + 0x3c) = *(undefined4 *)((long)puVar34 + 0x3c);
            uVar24 = uVar24 - 8;
            puVar31 = puVar31 + 8;
            puVar34 = puVar34 + 8;
          } while (uVar24 != 0);
        }
        else {
          uVar36 = uVar24 >> 1;
          uVar18 = puVar14[1];
          uVar15 = 1;
          lVar27 = 0x10;
          *puVar31 = *puVar14;
          puVar31[1] = uVar18;
          uVar23 = (int)uVar36 - 1U & 7;
          if (1 < uVar36) {
            if (uVar23 != 0) {
              if (uVar23 != 1) {
                if (uVar23 != 2) {
                  if (uVar23 != 3) {
                    if (uVar23 != 4) {
                      if (uVar23 != 5) {
                        if (uVar23 != 6) {
                          uVar18 = puVar14[3];
                          uVar15 = 2;
                          lVar27 = 0x20;
                          puVar31[2] = puVar14[2];
                          puVar31[3] = uVar18;
                        }
                        uVar15 = uVar15 + 1;
                        uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
                        *(undefined8 *)((long)puVar31 + lVar27) =
                             *(undefined8 *)((long)puVar14 + lVar27);
                        ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
                        lVar27 = lVar27 + 0x10;
                      }
                      uVar15 = uVar15 + 1;
                      uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
                      *(undefined8 *)((long)puVar31 + lVar27) =
                           *(undefined8 *)((long)puVar14 + lVar27);
                      ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
                      lVar27 = lVar27 + 0x10;
                    }
                    uVar15 = uVar15 + 1;
                    uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
                    *(undefined8 *)((long)puVar31 + lVar27) =
                         *(undefined8 *)((long)puVar14 + lVar27);
                    ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
                    lVar27 = lVar27 + 0x10;
                  }
                  uVar15 = uVar15 + 1;
                  uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
                  *(undefined8 *)((long)puVar31 + lVar27) = *(undefined8 *)((long)puVar14 + lVar27);
                  ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
                  lVar27 = lVar27 + 0x10;
                }
                uVar15 = uVar15 + 1;
                uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
                *(undefined8 *)((long)puVar31 + lVar27) = *(undefined8 *)((long)puVar14 + lVar27);
                ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
                lVar27 = lVar27 + 0x10;
              }
              uVar15 = uVar15 + 1;
              uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
              *(undefined8 *)((long)puVar31 + lVar27) = *(undefined8 *)((long)puVar14 + lVar27);
              ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
              lVar27 = lVar27 + 0x10;
              if (uVar36 <= uVar15) goto LAB_0014e216;
            }
            do {
              uVar15 = uVar15 + 8;
              uVar18 = ((undefined8 *)((long)puVar14 + lVar27))[1];
              *(undefined8 *)((long)puVar31 + lVar27) = *(undefined8 *)((long)puVar14 + lVar27);
              ((undefined8 *)((long)puVar31 + lVar27))[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x10);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x10);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x20);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x20);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x30);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x30);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x40);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x40);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x50);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x50);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x60);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x60);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              puVar34 = (undefined8 *)((long)puVar14 + lVar27 + 0x70);
              uVar18 = puVar34[1];
              puVar30 = (undefined8 *)((long)puVar31 + lVar27 + 0x70);
              *puVar30 = *puVar34;
              puVar30[1] = uVar18;
              lVar27 = lVar27 + 0x80;
            } while (uVar15 < uVar36);
          }
LAB_0014e216:
          uVar15 = uVar24 & 0xfffffffffffffffe;
          if (uVar24 != uVar15) {
            uVar6 = *(undefined4 *)((long)(puVar14 + uVar15) + 4);
            *(undefined4 *)(puVar31 + uVar15) = *(undefined4 *)(puVar14 + uVar15);
            *(undefined4 *)((long)(puVar31 + uVar15) + 4) = uVar6;
          }
        }
      }
LAB_0014e23d:
      if (puVar19 != puVar37) {
        lVar27 = 0;
        uVar15 = (ulong)((long)puVar37 - (long)(puVar19 + 1)) >> 3;
        uVar23 = (int)uVar15 + 1U & 7;
        if (uVar23 == 0) goto LAB_0014e2e1;
        if (uVar23 != 1) {
          if (uVar23 != 2) {
            if (uVar23 != 3) {
              if (uVar23 != 4) {
                if (uVar23 != 5) {
                  if (uVar23 != 6) {
                    lVar27 = 8;
                    *puVar16 = *puVar19;
                  }
                  *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
                  lVar27 = lVar27 + 8;
                }
                *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
                lVar27 = lVar27 + 8;
              }
              *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
              lVar27 = lVar27 + 8;
            }
            *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
            lVar27 = lVar27 + 8;
          }
          *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
          lVar27 = lVar27 + 8;
        }
        *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
        for (lVar27 = lVar27 + 8; lVar27 != uVar15 * 8 + 8; lVar27 = lVar27 + 0x40) {
LAB_0014e2e1:
          *(undefined8 *)((long)puVar16 + lVar27) = *(undefined8 *)((long)puVar19 + lVar27);
          *(undefined8 *)((long)puVar16 + lVar27 + 8) = *(undefined8 *)((long)puVar19 + lVar27 + 8);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x10) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x10);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x18) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x18);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x20) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x20);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x28) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x28);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x30) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x30);
          *(undefined8 *)((long)puVar16 + lVar27 + 0x38) =
               *(undefined8 *)((long)puVar19 + lVar27 + 0x38);
        }
        puVar16 = (undefined8 *)((long)puVar16 + lVar27);
      }
      *(undefined8 **)((long)&__DT_SYMTAB[0xa4].st_name + lVar32) = puVar16;
    }
    else {
      puVar37 = puVar31;
      if (0 < lVar28) {
        puVar16 = puVar31 + 4;
        puVar19 = puVar14 + 4;
        if ((puVar14 < puVar16 && puVar31 < puVar19) || (lVar28 < 0x41)) {
          uVar23 = (uint)uVar15 & 7;
          puVar16 = puVar14;
          if ((uVar15 & 7) != 0) {
            if (uVar23 != 1) {
              puVar19 = puVar14;
              if (uVar23 != 2) {
                if (uVar23 != 3) {
                  if (uVar23 != 4) {
                    if (uVar23 != 5) {
                      puVar19 = puVar31;
                      puVar37 = puVar14;
                      if (uVar23 != 6) {
                        puVar37 = puVar14 + 1;
                        puVar19 = puVar31 + 1;
                        *(undefined4 *)puVar31 = *(undefined4 *)puVar14;
                        *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar14 + 4);
                        uVar15 = uVar15 - 1;
                      }
                      uVar15 = uVar15 - 1;
                      puVar16 = puVar37 + 1;
                      puVar31 = puVar19 + 1;
                      *(undefined4 *)puVar19 = *(undefined4 *)puVar37;
                      *(undefined4 *)((long)puVar19 + 4) = *(undefined4 *)((long)puVar37 + 4);
                    }
                    uVar15 = uVar15 - 1;
                    puVar19 = puVar16 + 1;
                    puVar37 = puVar31 + 1;
                    *(undefined4 *)puVar31 = *(undefined4 *)puVar16;
                    *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar16 + 4);
                  }
                  uVar15 = uVar15 - 1;
                  puVar16 = puVar19 + 1;
                  puVar31 = puVar37 + 1;
                  *(undefined4 *)puVar37 = *(undefined4 *)puVar19;
                  *(undefined4 *)((long)puVar37 + 4) = *(undefined4 *)((long)puVar19 + 4);
                }
                uVar15 = uVar15 - 1;
                puVar19 = puVar16 + 1;
                puVar37 = puVar31 + 1;
                *(undefined4 *)puVar31 = *(undefined4 *)puVar16;
                *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar16 + 4);
              }
              uVar15 = uVar15 - 1;
              puVar16 = puVar19 + 1;
              puVar31 = puVar37 + 1;
              *(undefined4 *)puVar37 = *(undefined4 *)puVar19;
              *(undefined4 *)((long)puVar37 + 4) = *(undefined4 *)((long)puVar19 + 4);
            }
            puVar37 = puVar31 + 1;
            *(undefined4 *)puVar31 = *(undefined4 *)puVar16;
            *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar16 + 4);
            uVar15 = uVar15 - 1;
            puVar31 = puVar37;
            puVar16 = puVar16 + 1;
            if (uVar15 == 0) goto LAB_0014e057;
          }
          do {
            puVar37 = puVar31 + 8;
            *(undefined4 *)puVar31 = *(undefined4 *)puVar16;
            *(undefined4 *)((long)puVar31 + 4) = *(undefined4 *)((long)puVar16 + 4);
            *(undefined4 *)(puVar31 + 1) = *(undefined4 *)(puVar16 + 1);
            *(undefined4 *)((long)puVar31 + 0xc) = *(undefined4 *)((long)puVar16 + 0xc);
            *(undefined4 *)(puVar31 + 2) = *(undefined4 *)(puVar16 + 2);
            *(undefined4 *)((long)puVar31 + 0x14) = *(undefined4 *)((long)puVar16 + 0x14);
            *(undefined4 *)(puVar31 + 3) = *(undefined4 *)(puVar16 + 3);
            *(undefined4 *)((long)puVar31 + 0x1c) = *(undefined4 *)((long)puVar16 + 0x1c);
            *(undefined4 *)(puVar31 + 4) = *(undefined4 *)(puVar16 + 4);
            *(undefined4 *)((long)puVar31 + 0x24) = *(undefined4 *)((long)puVar16 + 0x24);
            *(undefined4 *)(puVar31 + 5) = *(undefined4 *)(puVar16 + 5);
            *(undefined4 *)((long)puVar31 + 0x2c) = *(undefined4 *)((long)puVar16 + 0x2c);
            *(undefined4 *)(puVar31 + 6) = *(undefined4 *)(puVar16 + 6);
            *(undefined4 *)((long)puVar31 + 0x34) = *(undefined4 *)((long)puVar16 + 0x34);
            *(undefined4 *)(puVar31 + 7) = *(undefined4 *)(puVar16 + 7);
            *(undefined4 *)((long)puVar31 + 0x3c) = *(undefined4 *)((long)puVar16 + 0x3c);
            uVar15 = uVar15 - 8;
            puVar31 = puVar37;
            puVar16 = puVar16 + 8;
          } while (uVar15 != 0);
        }
        else {
          uVar36 = uVar15 >> 2;
          uVar24 = 1;
          uVar18 = puVar14[1];
          uVar23 = (int)uVar36 - 1U & 3;
          uVar7 = puVar14[2];
          uVar8 = puVar14[3];
          lVar28 = (long)puVar31 + _UNK_00cb6c78 + 8;
          *puVar31 = *puVar14;
          puVar31[1] = uVar18;
          puVar37 = (undefined8 *)((long)puVar31 + lVar33 + 8);
          puVar31[2] = uVar7;
          puVar31[3] = uVar8;
          if (1 < uVar36) {
            if (uVar23 != 0) {
              lVar26 = lVar28;
              if (uVar23 != 1) {
                puVar37 = puVar19;
                puVar34 = puVar16;
                if (uVar23 != 2) {
                  uVar18 = puVar14[5];
                  lVar28 = lVar28 + lVar27;
                  uVar24 = 2;
                  puVar34 = puVar31 + 8;
                  puVar37 = puVar14 + 8;
                  uVar7 = puVar14[6];
                  uVar8 = puVar14[7];
                  *puVar16 = *puVar19;
                  puVar31[5] = uVar18;
                  puVar31[6] = uVar7;
                  puVar31[7] = uVar8;
                }
                uVar24 = uVar24 + 1;
                puVar16 = puVar34 + 4;
                puVar19 = puVar37 + 4;
                lVar26 = lVar28 + lVar27;
                uVar18 = puVar37[1];
                uVar7 = puVar37[2];
                uVar8 = puVar37[3];
                *puVar34 = *puVar37;
                puVar34[1] = uVar18;
                puVar34[2] = uVar7;
                puVar34[3] = uVar8;
              }
              uVar24 = uVar24 + 1;
              lVar28 = lVar26 + lVar27;
              puVar37 = (undefined8 *)(lVar26 + lVar33);
              uVar18 = puVar19[1];
              uVar7 = puVar19[2];
              uVar8 = puVar19[3];
              *puVar16 = *puVar19;
              puVar16[1] = uVar18;
              puVar16[2] = uVar7;
              puVar16[3] = uVar8;
              puVar19 = puVar19 + 4;
              puVar16 = puVar16 + 4;
              if (uVar36 <= uVar24) goto LAB_0014e504;
            }
            do {
              lVar26 = lVar28 + lVar27 * 3;
              uVar24 = uVar24 + 4;
              uVar18 = puVar19[1];
              lVar28 = lVar26 + lVar27;
              uVar7 = puVar19[2];
              uVar8 = puVar19[3];
              puVar37 = (undefined8 *)(lVar26 + lVar33);
              *puVar16 = *puVar19;
              puVar16[1] = uVar18;
              puVar16[2] = uVar7;
              puVar16[3] = uVar8;
              uVar18 = puVar19[5];
              uVar7 = puVar19[6];
              uVar8 = puVar19[7];
              puVar16[4] = puVar19[4];
              puVar16[5] = uVar18;
              puVar16[6] = uVar7;
              puVar16[7] = uVar8;
              uVar18 = puVar19[9];
              uVar7 = puVar19[10];
              uVar8 = puVar19[0xb];
              puVar16[8] = puVar19[8];
              puVar16[9] = uVar18;
              puVar16[10] = uVar7;
              puVar16[0xb] = uVar8;
              uVar18 = puVar19[0xd];
              uVar7 = puVar19[0xe];
              uVar8 = puVar19[0xf];
              puVar16[0xc] = puVar19[0xc];
              puVar16[0xd] = uVar18;
              puVar16[0xe] = uVar7;
              puVar16[0xf] = uVar8;
              puVar19 = puVar19 + 0x10;
              puVar16 = puVar16 + 0x10;
            } while (uVar24 < uVar36);
          }
LAB_0014e504:
          uVar24 = uVar15 & 0xfffffffffffffffc;
          puVar16 = puVar14 + uVar24;
          puVar31 = puVar31 + uVar24;
          if (uVar24 != uVar15) {
            uVar6 = *(undefined4 *)((long)puVar16 + 4);
            *(undefined4 *)puVar31 = *(undefined4 *)puVar16;
            *(undefined4 *)((long)puVar31 + 4) = uVar6;
            puVar37 = puVar31 + 1;
            if (uVar15 - uVar24 != 1) {
              uVar6 = *(undefined4 *)((long)puVar16 + 0xc);
              puVar37 = puVar31 + 2;
              *(undefined4 *)(puVar31 + 1) = *(undefined4 *)(puVar16 + 1);
              *(undefined4 *)((long)puVar31 + 0xc) = uVar6;
              if (uVar15 - uVar24 != 2) {
                uVar6 = *(undefined4 *)((long)puVar16 + 0x14);
                puVar37 = puVar31 + 3;
                *(undefined4 *)(puVar31 + 2) = *(undefined4 *)(puVar16 + 2);
                *(undefined4 *)((long)puVar31 + 0x14) = uVar6;
              }
            }
          }
        }
      }
LAB_0014e057:
      *(undefined8 **)((long)&__DT_SYMTAB[0xa4].st_name + lVar32) = puVar37;
    }
    if (puVar14 != (undefined8 *)0x0) goto LAB_0014d92e;
  }
  if (local_68._0_8_ != 0) {
    HeapInterface::Free();
  }
  lVar33 = *(long *)(param_1 + 0x14);
  uVar23 = *param_1;
  lVar32 = *(long *)(param_1 + 0x1c) + 1;
  pbVar13 = (byte *)(*(long *)(param_1 + 0x1c) + *(long *)(param_1 + 0x1a));
LAB_0014d958:
  lVar27 = *(long *)(param_1 + 0x12);
  lVar28 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar27);
  *(long *)(param_1 + 0x1c) = lVar32;
  param_1[2] = (int)(char)*pbVar13;
  if ((*(undefined **)((long)&__DT_RELA[0xd09].r_offset + lVar27) == &DAT_015bf1c0) &&
     (1 < *(int *)(lVar28 + 8))) {
    param_1[2] = 0;
  }
  FUN_00c29ca0(local_68,0xe8,0);
  uVar18 = local_68._8_8_;
  puVar14 = (undefined8 *)0x0;
  if ((undefined8 *)local_68._8_8_ != (undefined8 *)0x0) {
    *(undefined8 *)(local_68._8_8_ + 0x20) = 0x80000000100;
    *(undefined8 *)(local_68._8_8_ + 0x28) = 0xffffffff00000000;
    puVar14 = (undefined8 *)(local_68._8_8_ + 0x20);
    uVar7 = *(undefined8 *)(param_1 + 0x1c);
    *(undefined8 *)(local_68._8_8_ + 0x50) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x68) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x80) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x98) = 0;
    *(undefined8 *)(local_68._8_8_ + 0xd0) = uVar7;
    *(long *)(local_68._8_8_ + 0xa8) = lVar27;
    *(undefined8 *)(local_68._8_8_ + 0xc0) = 0;
    uVar7 = *(undefined8 *)(param_1 + 0x18);
    *(long *)(local_68._8_8_ + 0xb0) = lVar33;
    *(undefined4 *)(local_68._8_8_ + 0x30) = 0xffffffff;
    *(undefined8 *)(local_68._8_8_ + 0x38) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x40) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x58) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x70) = 0;
    *(undefined8 *)(local_68._8_8_ + 0x88) = 0;
    *(undefined8 *)(local_68._8_8_ + 0xa0) = 0;
    *(undefined8 *)(local_68._8_8_ + 200) = 0;
    FUN_00c45fe0((undefined8 *)(local_68._8_8_ + 0xb8),uVar7);
    memcpy(*(void **)(uVar18 + 200),*(void **)(param_1 + 0x1a),*(size_t *)(param_1 + 0x18));
    uVar7 = *(undefined8 *)(param_1 + 0x1c);
    *(uint *)(uVar18 + 0xe0) = uVar23 & 1;
    *(undefined4 *)(uVar18 + 0xdc) = 0xffffffff;
    *(undefined8 *)(uVar18 + 8) = 0x100000001;
    *(undefined ***)uVar18 = &PTR_FUN_01363708;
    *(int *)(uVar18 + 0xd8) = (int)uVar7;
    *(undefined8 *)(uVar18 + 0x10) = uVar18;
    *(undefined8 **)(uVar18 + 0x18) = puVar14;
  }
  lVar32 = *(long *)(param_1 + 0x24);
  *(undefined8 **)(param_1 + 0x26) = puVar14;
  *(undefined8 *)(param_1 + 0x24) = uVar18;
  if (lVar32 != 0) {
    ref_counter_base::DecRef();
    puVar14 = *(undefined8 **)(param_1 + 0x26);
  }
  FUN_00141b40(puVar14);
  puVar25 = param_1 + 4;
  *(long *)(param_1 + 0x1c) =
       *(long *)(param_1 + 0x1c) + (long)*(int *)(*(long *)(param_1 + 0x26) + 0xbc);
  FUN_00ad80e0(puVar21,puVar25);
  lVar32 = FUN_00198b40(lVar28);
  if (*(long *)(param_1 + 0x14) == *(long *)(lVar32 + 8)) {
    puVar22 = puVar25;
    if ((char)*(byte *)((long)param_1 + 0x27) < '\0') {
      puVar22 = *(uint **)(param_1 + 4);
    }
    bVar1 = (byte)*puVar22;
    while (bVar1 != 0) {
      puVar22 = (uint *)((long)puVar22 + 1);
      bVar1 = *(byte *)puVar22;
    }
    FUN_00493d20(lVar28 + 0x68);
    uVar18 = *(undefined8 *)((long)&__DT_RELA[0xcfa].r_info + *(long *)(param_1 + 0x12));
    if ((char)*(byte *)((long)param_1 + 0x27) < '\0') {
      FUN_00142910(uVar18,*(undefined8 *)(param_1 + 4));
    }
    else {
      FUN_00142910(uVar18,puVar25);
    }
  }
  lVar32 = *(long *)(param_1 + 0x1c);
  lVar27 = *(long *)(param_1 + 0x1a);
  *(long *)(param_1 + 0x1c) = lVar32 + 1;
  param_1[0xb] = (uint)*(byte *)(lVar27 + lVar32);
  if ((*param_1 & 4) == 0) {
    param_1[10] = 0;
    lVar33 = lVar32 + 3;
    *(long *)(param_1 + 0x1c) = lVar32 + 2;
    bVar1 = *(byte *)(lVar27 + 1 + lVar32);
    *(long *)(param_1 + 0x1c) = lVar33;
    param_1[0xc] = (uint)bVar1;
    uVar23 = (uint)*(byte *)(lVar27 + 2 + lVar32);
    if (uVar23 == 0xff) {
      uVar23 = 0xffffffff;
    }
    param_1[0xd] = uVar23;
  }
  else {
    uVar11 = FUN_00121a30(puVar21);
    param_1[0xd] = 0xffffffff;
    uVar23 = (uint)uVar11;
    lVar33 = *(long *)(param_1 + 0x1c);
    lVar27 = *(long *)(param_1 + 0x1a);
    if (uVar23 == 0xffff) {
      uVar23 = 0xffffffff;
    }
    param_1[10] = uVar23;
    param_1[0xc] = param_1[0xb];
  }
  *(long *)(param_1 + 0x1c) = lVar33 + 1;
  bVar1 = *(byte *)(lVar27 + lVar33);
  *(byte *)(param_1 + 0xe) = bVar1;
  if (bVar1 == 0) {
    pbVar13 = (byte *)((long)param_1 + 0x3a);
    pbVar13[0] = 0xff;
    pbVar13[1] = 0xff;
    pbVar13[2] = 0xff;
    pbVar13[3] = 0xff;
    pbVar13[4] = 0xff;
    pbVar13[5] = 0xff;
    pbVar13[6] = 0xff;
    pbVar13[7] = 0xff;
    ((byte *)((long)param_1 + 0x42))[0] = 0xff;
    ((byte *)((long)param_1 + 0x42))[1] = 0xff;
  }
  else {
    uVar12 = FUN_00121a30(puVar21);
    *(undefined2 *)((long)param_1 + 0x3a) = uVar12;
    uVar12 = FUN_00121a30();
    *(undefined2 *)(param_1 + 0xf) = uVar12;
    uVar12 = FUN_00121a30();
    *(undefined2 *)((long)param_1 + 0x3e) = uVar12;
    uVar12 = FUN_00121a30();
    *(undefined2 *)(param_1 + 0x10) = uVar12;
    lVar32 = *(long *)(param_1 + 0x1c);
    *(long *)(param_1 + 0x1c) = lVar32 + 1;
    *(ushort *)((long)param_1 + 0x42) = (ushort)*(byte *)(*(long *)(param_1 + 0x1a) + lVar32);
  }
  uVar23 = param_1[1];
  lVar32 = *(long *)(param_1 + 0x14);
  if ((short)uVar23 == -1) {
    FUN_00493d20(lVar32 + 0x110,&DAT_00fba3bd);
    *(undefined8 *)(lVar32 + 0x128) = 0;
  }
  else {
    lVar27 = *(long *)(lVar32 + 0x78);
    uVar6 = **(undefined4 **)(lVar27 + 0x270);
    if ((*param_1 & 0x80) != 0) {
      uVar6 = (*(undefined4 **)(lVar27 + 0x270))[1];
    }
    puVar14 = &DAT_015c7b60;
    if (*(int *)(lVar27 + 0x3c) == 4) {
      puVar14 = (undefined8 *)FUN_00ad7690(lVar27,uVar6);
    }
    lVar27 = puVar14[1];
    pcVar17 = (char *)FUN_005b5bb0(lVar27,local_68,(int)(short)uVar23);
    if (local_68[0] == '\0') {
      pcVar17 = (char *)(lVar27 + 0x68);
    }
    if (pcVar17[0x18] != '\x02') {
                    /* WARNING: Subroutine does not return */
      abort();
    }
    if (pcVar17[0x17] < '\0') {
      pcVar17 = *(char **)pcVar17;
    }
    cVar2 = *pcVar17;
    while (cVar2 != '\0') {
      pcVar17 = pcVar17 + 1;
      cVar2 = *pcVar17;
    }
    FUN_00493d20(lVar32 + 0x110);
    pcVar17 = "<name>";
    do {
      pcVar20 = pcVar17;
      pcVar17 = pcVar20 + 1;
    } while (*pcVar17 != '\0');
    uVar18 = FUN_00ad8050(lVar32 + 0x110,"<name>",pcVar20 + -0xcb691b);
    *(undefined8 *)(lVar32 + 0x128) = uVar18;
  }
  lVar32 = *(long *)(param_1 + 0x14);
  *(uint *)((long)&__DT_SYMTAB[0x90].st_size + lVar32 + 4) = param_1[2];
  FUN_0042b380(lVar32,(*param_1 >> 3 & 7) + 1);
  puVar21 = puVar25;
  if ((char)*(byte *)((long)param_1 + 0x27) < '\0') {
    puVar21 = *(uint **)(param_1 + 4);
  }
  FUN_0014d550(*(long *)(param_1 + 0x14) + 0x90,puVar21);
  if ((char)*(byte *)((long)param_1 + 0x27) < '\0') {
    puVar25 = *(uint **)(param_1 + 4);
  }
  bVar1 = (byte)*puVar25;
  puVar21 = puVar25;
  while (bVar1 != 0) {
    puVar21 = (uint *)((long)puVar21 + 1);
    bVar1 = *(byte *)puVar21;
  }
  FUN_00493d20(*(long *)(param_1 + 0x14) + 0xf8,puVar25);
  FUN_00142e20(*(undefined8 *)(param_1 + 0x14));
  lVar32 = *(long *)(param_1 + 0x14);
  uVar23 = param_1[0x10];
  sVar3 = *(short *)((long)param_1 + 0x3e);
  uVar9 = param_1[0xf];
  sVar4 = *(short *)((long)param_1 + 0x3a);
  *(uint *)(&__DT_SYMTAB[0x90].st_info + lVar32) = param_1[0xb];
  cVar2 = *(char *)((long)&__DT_SYMTAB[0x8e].st_size + lVar32 + 4);
  sVar5 = *(short *)((long)param_1 + 0x42);
  *(uint *)((long)&__DT_SYMTAB[0x90].st_size + lVar32) = param_1[10];
  *(uint *)((long)&__DT_SYMTAB[0x90].st_value + lVar32) = param_1[0xc];
  *(uint *)((long)&__DT_SYMTAB[0x90].st_value + lVar32 + 4) = param_1[0xd];
  uVar10 = param_1[0xe];
  *(int *)((long)&__DT_SYMTAB[0x8f].st_name + lVar32) = (int)sVar4;
  *(int *)(&__DT_SYMTAB[0x8f].st_info + lVar32) = (int)(short)uVar9;
  *(int *)((long)&__DT_SYMTAB[0x8f].st_value + lVar32) = (int)sVar3;
  *(int *)((long)&__DT_SYMTAB[0x8f].st_value + lVar32 + 4) = (int)(short)uVar23;
  *(int *)((long)&__DT_SYMTAB[0x8f].st_size + lVar32) = (int)(char)(byte)uVar10;
  if (cVar2 != '\0') {
    FUN_00ad7a40(lVar32 + 0x240,DAT_00cb723b);
  }
  bVar35 = true;
  if (*(long *)((long)&__DT_SYMTAB[0x8f].st_name + lVar32) == -1) {
    bVar35 = *(int *)((long)&__DT_SYMTAB[0x8f].st_value + lVar32) != -1 || (short)uVar23 != -1;
  }
  *(int *)((long)&__DT_SYMTAB[0x90].st_name + lVar32) = (int)sVar5;
  *(bool *)(lVar32 + 0x107c) = bVar35;
  *(byte *)((long)param_1 + 0x89) = 1;
  FUN_00ad79b0(param_1 + 0x1e);
  return;
}


```

## `jag::PlayerEntity::ProcessExtendedInfo` @ 0015e290
```c

/* Setting prototype: void ProcessExtendedInfo(long * player, int updateFlagsMask, long packet) */

void jag::PlayerEntity::ProcessExtendedInfo(long *player,int updateFlagsMask,long packet)

{
  int *piVar1;
  int iVar2;
  byte bVar3;
  byte bVar4;
  undefined4 uVar5;
  void *__src;
  undefined8 uVar6;
  undefined8 uVar7;
  long lVar8;
  undefined8 *puVar9;
  undefined8 *puVar10;
  uint3 uVar11;
  undefined1 auVar12 [16];
  undefined1 auVar13 [13];
  undefined *puVar14;
  undefined *puVar15;
  short sVar16;
  ushort uVar17;
  undefined2 uVar18;
  short sVar19;
  ushort uVar20;
  undefined2 uVar21;
  undefined2 uVar22;
  uint uVar23;
  uint uVar24;
  float fVar25;
  uint uVar26;
  uint uVar27;
  undefined4 uVar28;
  int iVar29;
  undefined4 uVar30;
  ulong uVar31;
  ulong uVar32;
  byte *pbVar33;
  long lVar34;
  long *plVar35;
  ulong uVar36;
  long *plVar37;
  uint uVar38;
  undefined1 auVar39 [8];
  long *plVar40;
  uint uVar41;
  undefined8 *puVar42;
  long lVar43;
  undefined8 *puVar44;
  undefined1 *puVar45;
  uint uVar46;
  undefined8 *puVar47;
  undefined8 *puVar48;
  size_t sVar49;
  char cVar50;
  int iVar51;
  int iVar52;
  undefined4 uVar53;
  float fVar54;
  float fVar55;
  float fVar56;
  float fVar57;
  float fVar58;
  float fVar59;
  float fVar60;
  float fVar61;
  float fVar62;
  float fVar63;
  float fVar64;
  undefined1 auVar65 [16];
  uint *puVar66;
  float local_228;
  undefined4 uStack_224;
  uint local_20c;
  ulong local_208;
  ulong local_200;
  ulong local_1f8;
  uint local_1f0;
  ulong local_1e8;
  undefined1 *local_1a8;
  uint local_190;
  uint local_18c;
  uint local_188;
  undefined4 uStack_184;
  char local_171;
  float local_168;
  float fStack_164;
  float local_160;
  float local_15c;
  float local_158;
  undefined4 local_154;
  float local_150;
  float local_14c;
  float local_148;
  undefined1 local_138 [8];
  size_t local_130;
  void *local_128;
  ulong local_120;
  long local_118;
  undefined *local_110;
  float local_108;
  float fStack_104;
  undefined8 uStack_100;
  undefined8 local_f8;
  undefined8 uStack_f0;
  float local_e8;
  float fStack_e4;
  float fStack_e0;
  float fStack_dc;
  float local_d8;
  float fStack_d4;
  float fStack_d0;
  float fStack_cc;
  float local_c8 [2];
  undefined8 uStack_c0;
  undefined8 local_b8;
  undefined8 uStack_b0;
  float local_a8;
  float fStack_a4;
  float fStack_a0;
  float fStack_9c;
  undefined8 local_98;
  undefined8 uStack_90;
  undefined1 local_88 [8];
  undefined8 uStack_80;
  undefined1 local_78 [7];
  char cStack_71;
  undefined8 uStack_70;
  undefined1 *local_68;
  undefined1 local_60 [32];
  undefined1 local_40 [16];
  
                    /* jag::PlayerEntity::ProcessExtendedInfo -- player ext-info dispatcher (948-2-2
                       0x0015e110 drifted here in 948-5). 1..4 byte LE mask header (expansion bits
                       {0,13,22}), then 23 blocks in fixed source order. APPEARANCE = bit 3 (mask
                       0x8), ord 4 -> gScrambledByte(len) + FUN_0047ec70(body) +
                       QueueExtendedInfoPacket -> SetAppearanceAsPlayer. CRITICAL: the scrambled
                       mode cursor (packet+0x28) points at a FIXED .rodata table @0x00cb6a80 (NOT
                       the wire), reset per-block (36 resets). APPEARANCE base = 0x00cb6ac0 ->
                       length mode=3, body mode=2. Modes are NOT transmitted; server applies the
                       table-dictated transform. See docs/protocol/player-appearance-948.md */
  __src = *(void **)(packet + 0x10);
  iVar2 = (int)player[0x11];
  lVar43 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar43 + 1;
  bVar3 = *(byte *)((long)__src + lVar43);
  uVar38 = (uint)bVar3;
  if ((bVar3 & 1) != 0) {
    *(long *)(packet + 0x18) = lVar43 + 2;
    bVar4 = *(byte *)((long)__src + lVar43 + 1);
    uVar17 = CONCAT11(bVar4,bVar3);
    uVar38 = (uint)uVar17;
    if ((bVar4 & 0x20) != 0) {
      *(long *)(packet + 0x18) = lVar43 + 3;
      bVar3 = *(byte *)((long)__src + lVar43 + 2);
      uVar11 = CONCAT12(bVar3,uVar17);
      uVar38 = (uint)uVar11;
      if ((bVar3 & 0x40) != 0) {
        *(long *)(packet + 0x18) = lVar43 + 4;
        uVar38 = CONCAT13(*(undefined1 *)((long)__src + lVar43 + 3),uVar11);
      }
    }
  }
  sVar49 = *(size_t *)(packet + 8);
  uVar6 = *DAT_015da128;
  local_130 = 0;
  local_128 = (void *)0x0;
  local_120 = 0;
  uVar5 = *(undefined4 *)
           ((long)&__DT_RELA[0x548].r_addend +
           *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + player[0x20b]) + 0x2f0));
  FUN_00c45fe0(local_138,sVar49);
  memcpy(local_128,__src,sVar49);
  uVar31 = *(ulong *)(packet + 0x18);
  local_110 = &DAT_00cb6ac5;
  local_120 = uVar31;
  local_118 = packet;
  if ((uVar38 & 0x4000000) != 0) {
    local_120 = uVar31 + 1;
    bVar3 = *(byte *)((long)local_128 + uVar31);
    if (bVar3 != 0) {
      plVar40 = player + 0x201;
      plVar35 = player + 0x205;
      uVar41 = 0;
      do {
        sVar16 = FUN_00121a00(local_138);
        iVar51 = (int)sVar16;
        if (iVar51 == -1) {
          FUN_0044f490(player);
          FUN_0044f280(player);
          break;
        }
        lVar43 = player[0x201];
        lVar34 = player[0x202];
        if (lVar43 != lVar34) {
          uVar46 = (int)((lVar34 - lVar43) - 0x10U >> 4) + 1U & 7;
          if (uVar46 != 0) {
            if (uVar46 != 1) {
              if (uVar46 != 2) {
                if (uVar46 != 3) {
                  if (uVar46 != 4) {
                    if (uVar46 != 5) {
                      if (uVar46 != 6) {
                        if ((*(long *)(lVar43 + 8) != 0) &&
                           (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                          plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                          lVar8 = *plVar37;
                          *plVar37 = 0;
                          if (lVar8 != 0) {
                            FUN_0044e0e0();
                          }
                        }
                        lVar43 = lVar43 + 0x10;
                      }
                      if ((*(long *)(lVar43 + 8) != 0) &&
                         (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                        plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                        lVar8 = *plVar37;
                        *plVar37 = 0;
                        if (lVar8 != 0) {
                          FUN_0044e0e0();
                        }
                      }
                      lVar43 = lVar43 + 0x10;
                    }
                    if ((*(long *)(lVar43 + 8) != 0) &&
                       (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                      plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                      lVar8 = *plVar37;
                      *plVar37 = 0;
                      if (lVar8 != 0) {
                        FUN_0044e0e0();
                      }
                    }
                    lVar43 = lVar43 + 0x10;
                  }
                  if ((*(long *)(lVar43 + 8) != 0) &&
                     (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                    plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                    lVar8 = *plVar37;
                    *plVar37 = 0;
                    if (lVar8 != 0) {
                      FUN_0044e0e0();
                    }
                  }
                  lVar43 = lVar43 + 0x10;
                }
                if ((*(long *)(lVar43 + 8) != 0) &&
                   (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                  plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                  lVar8 = *plVar37;
                  *plVar37 = 0;
                  if (lVar8 != 0) {
                    FUN_0044e0e0();
                  }
                }
                lVar43 = lVar43 + 0x10;
              }
              if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))
                 ) {
                plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
                lVar8 = *plVar37;
                *plVar37 = 0;
                if (lVar8 != 0) {
                  FUN_0044e0e0();
                }
              }
              lVar43 = lVar43 + 0x10;
            }
            if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74)))
            {
              plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            lVar43 = lVar43 + 0x10;
            if (lVar34 == lVar43) goto LAB_0015e600;
          }
          do {
            if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74)))
            {
              plVar37 = (long *)FUN_0044f170(plVar40,lVar43);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x18) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x18) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40,lVar43 + 0x10);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x28) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x28) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x38) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x38) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x48) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x48) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x58) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x58) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x68) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x68) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x78) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x78) + 0x74))) {
              plVar37 = (long *)FUN_0044f170(plVar40);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            lVar43 = lVar43 + 0x80;
          } while (lVar34 != lVar43);
        }
LAB_0015e600:
        lVar43 = player[0x205];
        lVar34 = player[0x206];
        if (lVar43 != lVar34) {
          uVar46 = (int)((lVar34 - lVar43) - 0x20U >> 5) + 1U & 7;
          if (uVar46 != 0) {
            if (uVar46 != 1) {
              if (uVar46 != 2) {
                if (uVar46 != 3) {
                  if (uVar46 != 4) {
                    if (uVar46 != 5) {
                      if (uVar46 != 6) {
                        if ((*(long *)(lVar43 + 8) != 0) &&
                           (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                          plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                          lVar8 = *plVar37;
                          *plVar37 = 0;
                          if (lVar8 != 0) {
                            FUN_0044e0e0();
                          }
                        }
                        lVar43 = lVar43 + 0x20;
                      }
                      if ((*(long *)(lVar43 + 8) != 0) &&
                         (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                        plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                        lVar8 = *plVar37;
                        *plVar37 = 0;
                        if (lVar8 != 0) {
                          FUN_0044e0e0();
                        }
                      }
                      lVar43 = lVar43 + 0x20;
                    }
                    if ((*(long *)(lVar43 + 8) != 0) &&
                       (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                      plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                      lVar8 = *plVar37;
                      *plVar37 = 0;
                      if (lVar8 != 0) {
                        FUN_0044e0e0();
                      }
                    }
                    lVar43 = lVar43 + 0x20;
                  }
                  if ((*(long *)(lVar43 + 8) != 0) &&
                     (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                    plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                    lVar8 = *plVar37;
                    *plVar37 = 0;
                    if (lVar8 != 0) {
                      FUN_0044e0e0();
                    }
                  }
                  lVar43 = lVar43 + 0x20;
                }
                if ((*(long *)(lVar43 + 8) != 0) &&
                   (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))) {
                  plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                  lVar8 = *plVar37;
                  *plVar37 = 0;
                  if (lVar8 != 0) {
                    FUN_0044e0e0();
                  }
                }
                lVar43 = lVar43 + 0x20;
              }
              if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74))
                 ) {
                plVar37 = (long *)FUN_00454080(plVar35,lVar43);
                lVar8 = *plVar37;
                *plVar37 = 0;
                if (lVar8 != 0) {
                  FUN_0044e0e0();
                }
              }
              lVar43 = lVar43 + 0x20;
            }
            if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74)))
            {
              plVar37 = (long *)FUN_00454080(plVar35,lVar43);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            lVar43 = lVar43 + 0x20;
            if (lVar34 == lVar43) goto LAB_0015e7d5;
          }
          do {
            if ((*(long *)(lVar43 + 8) != 0) && (iVar51 == *(int *)(*(long *)(lVar43 + 8) + 0x74)))
            {
              plVar37 = (long *)FUN_00454080(plVar35,lVar43);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x28) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x28) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35,lVar43 + 0x20);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x48) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x48) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x68) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x68) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0x88) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0x88) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0xa8) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0xa8) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 200) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 200) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            if ((*(long *)(lVar43 + 0xe8) != 0) &&
               (iVar51 == *(int *)(*(long *)(lVar43 + 0xe8) + 0x74))) {
              plVar37 = (long *)FUN_00454080(plVar35);
              lVar8 = *plVar37;
              *plVar37 = 0;
              if (lVar8 != 0) {
                FUN_0044e0e0();
              }
            }
            lVar43 = lVar43 + 0x100;
          } while (lVar34 != lVar43);
        }
LAB_0015e7d5:
        uVar41 = uVar41 + 1;
      } while (bVar3 != uVar41);
    }
    puVar14 = local_110;
    uVar31 = local_120 + 1;
    bVar3 = *(byte *)((long)local_128 + local_120);
    local_120 = uVar31;
    if (bVar3 != 0) {
      uVar41 = 0;
      do {
        local_110 = puVar14;
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar17 = FUN_0047f6c0(local_138);
        uVar46 = 0xffffffff;
        if (uVar17 != 0xffff) {
          uVar46 = (uint)uVar17;
        }
        uVar23 = Packet::gScrambledUint(local_138);
        uVar32 = Packet::gScrambledByte((long)local_138);
        uVar24 = Packet::gScrambledMedium(local_138);
        local_c8[1] = (float)(((int)uVar23 >> 0x10) << 2);
        local_b8 = (void *)CONCAT44(CONCAT31(local_b8._5_3_,(int)uVar24 >> 0x16 == 1),
                                    ((int)uVar24 >> 0xb & 0x7ffU) - 0x3ff);
        uVar41 = uVar41 + 1;
        local_a8 = (float)(CONCAT31(local_a8._1_3_,(char)(uVar23 >> 0xf)) & 0xffffff01);
        uStack_70 = *DAT_015da128;
        stack0xffffffffffffff8c = uVar5;
        local_c8[0] = 0.0;
        uStack_c0 = (ulong)((uVar24 & 0x7ff) - 0x3ff) << 0x20;
        auVar13[8] = (char)((int)(uint)(byte)uVar32 >> 7);
        auVar13._0_8_ = CONCAT44(uVar23,uVar46) & 0x7fffffffffff;
        auVar13._9_4_ = 0;
        uStack_80._5_3_ = 0;
        stack0xffffffffffffff79 = SUB1312(auVar13 << 0x20,1);
        local_88[0] = 1;
        local_78._0_4_ = updateFlagsMask;
        uStack_b0 = CONCAT44((int)player[8],(float)((byte)uVar32 & 7) * DAT_00cb6b3c);
        local_68 = (undefined1 *)(ulong)(player[0x20d] == 0);
        FUN_00155a80(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + player[0x20b]),uVar31 & 0xff,
                     player,local_c8,local_88);
      } while (bVar3 != uVar41);
    }
  }
  local_110 = &DAT_00cb6ac4;
  if ((uVar38 & 0x40000) != 0) {
    uVar31 = Packet::gScrambledByte((long)local_138);
    *(bool *)((long)player + 0x1071) = (char)uVar31 == '\x01';
  }
  local_110 = &DAT_00cb6ac2;
  if ((uVar38 & 0x100000) != 0) {
    auVar65[0xf] = 0;
    auVar65._0_15_ = stack0xffffffffffffff79;
    _local_88 = auVar65 << 8;
    cStack_71 = 0x17;
    FUN_00ad80e0(local_138,local_88);
    uVar31 = Packet::gScrambledByte((long)local_138);
    if ((uVar31 & 1) != 0) {
      uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + player[0x20b]);
      auVar39 = (undefined1  [8])local_88;
      if (cStack_71 < '\0') {
        auVar39 = local_88;
      }
      local_188 = local_188 & 0xffffff00;
      local_171 = '\x17';
      cVar50 = *(char *)auVar39;
      while (cVar50 != '\0') {
        auVar39 = (undefined1  [8])((long)auVar39 + 1);
        cVar50 = *(char *)auVar39;
      }
      FUN_0048d1a0(&local_188);
      plVar40 = player + 0x12;
      if (*(char *)((long)player + 0xa7) < '\0') {
        plVar40 = (long *)player[0x12];
      }
      local_168 = (float)((uint)local_168 & 0xffffff00);
      local_154 = (float)CONCAT13(0x17,(undefined3)local_154);
      cVar50 = (char)*plVar40;
      while (cVar50 != '\0') {
        plVar40 = (long *)((long)plVar40 + 1);
        cVar50 = *(char *)plVar40;
      }
      FUN_0048d1a0();
      plVar40 = player + 0x1f;
      if (*(char *)((long)player + 0x10f) < '\0') {
        plVar40 = (long *)player[0x1f];
      }
      local_108 = (float)((uint)local_108 & 0xffffff00);
      local_f8 = (void *)CONCAT17(0x17,(undefined7)local_f8);
      cVar50 = (char)*plVar40;
      while (cVar50 != '\0') {
        plVar40 = (long *)((long)plVar40 + 1);
        cVar50 = *(char *)plVar40;
      }
      FUN_0048d1a0(&local_108);
      FUN_00af72e0(local_c8,player);
      ChatHistory::AddChat(uVar7,2,uVar31 & 0xff,local_c8,&local_108,&local_168,&local_188,0);
      if (((long)local_b8 < 0) && (CONCAT44(local_c8[1],local_c8[0]) != 0)) {
        eastl__basic_string();
      }
      if (((long)local_f8 < 0) && (CONCAT44(fStack_104,local_108) != 0)) {
        eastl__basic_string();
      }
      if (((int)local_154 < 0) && (CONCAT44(fStack_164,local_168) != 0)) {
        eastl__basic_string();
      }
      if ((local_171 < '\0') && (CONCAT44(uStack_184,local_188) != 0)) {
        eastl__basic_string();
      }
    }
    (**(code **)(*player + 0x158))(player,local_88,0,0);
    if ((cStack_71 < '\0') && (local_88 != (undefined1  [8])0x0)) {
      eastl__basic_string();
    }
  }
  local_110 = &DAT_00cb6ac0;
  if ((uVar38 & 8) != 0) {
    uVar31 = Packet::gScrambledByte((long)local_138);
    uStack_80 = (uint *)0x0;
    _local_78 = ZEXT816(0);
    FUN_00c45fe0(local_88,uVar31 & 0xff);
    FUN_0047ec70(local_138,local_88,uVar31 & 0xff);
    PathingEntity::QueueExtendedInfoPacket(player,local_88,updateFlagsMask,uVar5);
    if (iVar2 == -1) {
LAB_0015f7bf:
    }
    else {
      lVar43 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + player[0x20b]) +
                                  0x10) + (long)iVar2 * 8);
      if (lVar43 != 0) {
        *(undefined8 *)(lVar43 + 0x58) = uStack_70;
        FUN_00c45fe0(lVar43 + 0x40);
        memcpy(*(void **)(lVar43 + 0x50),_local_78,(size_t)uStack_80);
        goto LAB_0015f7bf;
      }
    }
    if ((uStack_80 != (uint *)0x0) && (_local_78 != (uint *)0x0)) {
      eastl__basic_string();
    }
  }
  local_110 = &DAT_00cb6abf;
  if ((uVar38 & 0x40) != 0) {
    uVar41 = Packet::gScrambledMedium(local_138);
    cVar50 = (char)(uVar41 >> 0x10);
    if (cVar50 == '\x01') {
      FUN_0042e740(player + 0x35,uVar41 & 0xffff,DAT_00fd48a6);
    }
    else if (cVar50 < '\x02') {
      if (cVar50 == -1) {
LAB_00160267:
        FUN_0042e740(player + 0x35,0xffffffff,DAT_00cb6abe);
      }
    }
    else if (cVar50 == '\x02') {
      FUN_0042e740(player + 0x35,uVar41 & 0xffff,DAT_010291e8);
    }
    else if (cVar50 == '\x7f') goto LAB_00160267;
  }
  uVar31 = local_120;
  local_110 = &DAT_00cb6ab0;
  if ((uVar38 & 0x1000000) != 0) {
    uVar41 = Packet::gScrambledUbyte((long)local_138);
    iVar51 = (int)(char)(byte)uVar41;
    if (iVar51 == 0) {
      lVar43 = player[0x19f];
      player[0x19f] = 0;
      if (lVar43 != 0) {
        FUN_00451630();
        local_68 = local_60;
        local_1e8 = player[0x19f];
        local_228 = SUB84(local_68,0);
        uStack_224 = (undefined4)((ulong)local_68 >> 0x20);
        _local_78 = (uint *)local_40;
        uStack_80._0_4_ = local_228;
        local_88 = (undefined1  [8])local_68;
        uStack_80._4_4_ = uStack_224;
        goto LAB_0016005c;
      }
      local_68 = local_60;
      _local_78 = (uint *)local_40;
      uStack_80 = (uint *)local_68;
      local_88 = (undefined1  [8])local_68;
    }
    else {
      local_1e8 = player[0x19f];
      if (local_1e8 == 0) {
        FUN_0047e270(player);
        local_1e8 = player[0x19f];
      }
      local_68 = local_60;
      _local_78 = (uint *)local_40;
      uStack_80 = (uint *)local_68;
      local_88 = (undefined1  [8])local_68;
      if (8 < (byte)uVar41) {
        FUN_000f9d10(local_88);
      }
      if (0 < iVar51) {
        iVar52 = 0;
LAB_0015faf0:
        do {
          local_110 = &DAT_00cb6ab1;
          uVar32 = Packet::gScrambledUshort((long)local_138);
          uVar36 = Packet::gScrambledUshort((long)local_138);
          local_188 = (uint)(short)uVar36;
          if ((uVar32 & 0x400) == 0) {
            uVar30 = 0xffffffff;
            uVar53 = 0xffffffff;
            if ((uVar32 & 0x800) != 0) {
              local_110 = &DAT_00cb6ab3;
              uVar30 = Packet::gScrambledUint(local_138);
              uVar53 = 0xffffffff;
            }
          }
          else {
            local_110 = &DAT_00cb6ab3;
            uVar53 = Packet::gScrambledUint(local_138);
            uVar30 = 0xffffffff;
          }
          local_228 = 0.0;
          if ((uVar32 & 1) != 0) {
            local_110 = &DAT_00cb6ab4;
            iVar29 = Packet::gScrambledUint(local_138);
            local_228 = (float)iVar29;
          }
          fVar25 = 0.0;
          if ((uVar32 & 2) == 0) {
            if ((uVar32 & 4) != 0) goto LAB_00160e56;
LAB_0015fb7b:
            fVar61 = 0.0;
            if ((uVar32 & 8) != 0) goto LAB_00160e8c;
LAB_0015fb8c:
            fVar54 = 0.0;
            if ((uVar32 & 0x10) != 0) goto LAB_00160ec1;
LAB_0015fb9d:
            fVar55 = 0.0;
          }
          else {
            local_110 = &DAT_00cb6ab5;
            iVar29 = Packet::gScrambledUint(local_138);
            fVar25 = (float)iVar29;
            if ((uVar32 & 4) == 0) goto LAB_0015fb7b;
LAB_00160e56:
            local_110 = &DAT_00cb6ab6;
            iVar29 = Packet::gScrambledUint(local_138);
            fVar61 = (float)iVar29;
            if ((uVar32 & 8) == 0) goto LAB_0015fb8c;
LAB_00160e8c:
            local_110 = &DAT_00cb6ab7;
            uVar28 = Packet::gScrambledUint(local_138);
            fVar54 = (float)game::Conversion::JagexAngleToRadians(uVar28);
            if ((uVar32 & 0x10) == 0) goto LAB_0015fb9d;
LAB_00160ec1:
            local_110 = &DAT_00cb6ab8;
            uVar28 = Packet::gScrambledUint(local_138);
            fVar55 = (float)game::Conversion::JagexAngleToRadians(uVar28);
          }
          fVar56 = 0.0;
          if ((uVar32 & 0x20) != 0) {
            local_110 = &DAT_00cb6ab9;
            uVar28 = Packet::gScrambledUint(local_138);
            fVar56 = (float)game::Conversion::JagexAngleToRadians(uVar28);
          }
          fVar62 = 0.0;
          fVar57 = DAT_00cb6ad0;
          if ((uVar32 & 0x80) != 0) {
            local_110 = &DAT_00cb6aba;
            iVar29 = Packet::gScrambledUint(local_138);
            fVar57 = (float)iVar29 * DAT_00cb6b30;
            fVar62 = fVar57 * 0.0;
          }
          fVar64 = 0.0;
          fVar63 = DAT_00cb6ad0;
          if ((uVar32 & 0x100) != 0) {
            local_110 = &DAT_00cb6abb;
            iVar29 = Packet::gScrambledUint(local_138);
            fVar63 = (float)iVar29 * DAT_00cb6b30;
            fVar64 = fVar63 * 0.0;
          }
          fVar59 = 0.0;
          fVar58 = DAT_00cb6ad0;
          if ((uVar32 & 0x200) != 0) {
            local_110 = &DAT_00cb6abc;
            iVar29 = Packet::gScrambledUint(local_138);
            fVar58 = (float)iVar29 * DAT_00cb6b30;
            fVar59 = fVar58 * 0.0;
          }
          fStack_dc = 0.0;
          fStack_cc = 1.0;
          uStack_c0 = uStack_c0 & 0xffffffff;
          uStack_b0 = uStack_b0 & 0xffffffff;
          fStack_9c = 0.0;
          uStack_100._4_4_ = 0.0;
          local_98 = 0;
          uStack_90 = 0x3f80000000000000;
          uStack_f0._4_4_ = 0.0;
          local_108 = fVar57;
          fStack_104 = fVar64;
          local_e8 = fVar62;
          fStack_e4 = fVar64;
          fStack_e0 = fVar58;
          local_d8 = fVar62;
          fStack_d4 = fVar64;
          fStack_d0 = fVar59;
          uStack_100._0_4_ = fVar59;
          uStack_f0._0_4_ = fVar59;
          local_f8._0_4_ = fVar62;
          local_f8._4_4_ = fVar63;
          math::EulerToRotationMatrix(fVar55,fVar54,fVar56,&local_168);
          local_c8[0] = local_168;
          local_c8[1] = fStack_164;
          uStack_c0 = CONCAT44(uStack_c0._4_4_,local_160);
          local_b8 = (void *)CONCAT44(local_158,local_15c);
          uStack_b0 = CONCAT44(uStack_b0._4_4_,local_154);
          local_a8 = local_150;
          fStack_a4 = local_14c;
          fStack_a0 = local_148;
          if ((uVar32 & 0x40) == 0) {
            fVar56 = fStack_104 * local_154;
            fVar57 = fStack_104 * uStack_b0._4_4_;
            fVar54 = local_f8._4_4_ * local_154;
            fVar55 = local_f8._4_4_ * uStack_b0._4_4_;
            fVar62 = local_108 * fStack_164;
            fVar63 = local_108 * local_160;
            fVar64 = local_108 * uStack_c0._4_4_;
            local_108 = uStack_100._4_4_ * (float)local_98 + (float)uStack_100 * local_150 +
                        fStack_104 * local_15c + local_108 * local_168;
            fStack_104 = uStack_100._4_4_ * local_98._4_4_ + (float)uStack_100 * local_14c +
                         fStack_104 * local_158 + fVar62;
            fVar62 = fStack_e0 * fStack_9c;
            uStack_100 = CONCAT44(uStack_100._4_4_ * uStack_90._4_4_ + (float)uStack_100 * fStack_9c
                                  + fVar57 + fVar64,
                                  uStack_100._4_4_ * (float)uStack_90 +
                                  (float)uStack_100 * local_148 + fVar56 + fVar63);
            fVar64 = (float)local_f8 * local_160;
            fVar58 = (float)local_f8 * uStack_c0._4_4_;
            fVar59 = fStack_e4 * local_154;
            fVar60 = fStack_e4 * uStack_b0._4_4_;
            fVar56 = local_e8 * fStack_164;
            fVar57 = local_e8 * local_160;
            fVar63 = local_e8 * uStack_c0._4_4_;
            local_f8 = (void *)CONCAT44(uStack_f0._4_4_ * local_98._4_4_ +
                                        (float)uStack_f0 * local_14c + local_f8._4_4_ * local_158 +
                                        (float)local_f8 * fStack_164,
                                        uStack_f0._4_4_ * (float)local_98 +
                                        (float)uStack_f0 * local_150 + local_f8._4_4_ * local_15c +
                                        (float)local_f8 * local_168);
            uStack_f0 = CONCAT44(uStack_f0._4_4_ * uStack_90._4_4_ + (float)uStack_f0 * fStack_9c +
                                 fVar55 + fVar58,
                                 uStack_f0._4_4_ * (float)uStack_90 + (float)uStack_f0 * local_148 +
                                 fVar54 + fVar64);
            local_e8 = fStack_e0 * local_150 + fStack_dc * (float)local_98 + fStack_e4 * local_15c +
                       local_e8 * local_168;
            fStack_e4 = fStack_e0 * local_14c + fStack_dc * local_98._4_4_ + fStack_e4 * local_158 +
                        fVar56;
            fStack_e0 = fStack_e0 * local_148 + fStack_dc * (float)uStack_90 + fVar59 + fVar57;
            fStack_dc = fVar62 + fStack_dc * uStack_90._4_4_ + fVar60 + fVar63;
            fStack_cc = uStack_c0._4_4_ * local_d8 +
                        uStack_b0._4_4_ * fStack_d4 +
                        fStack_9c * fStack_d0 + uStack_90._4_4_ * fStack_cc;
            local_d8 = local_228;
            fStack_d4 = fVar25;
            fStack_d0 = fVar61;
            if ((uVar32 & 0x400) != 0) goto LAB_00160dad;
LAB_0015ff79:
            if ((uVar32 & 0x800) != 0) {
              FUN_001535e0(local_1e8,local_188,uVar30,&local_108);
            }
            lVar43 = player[0x155];
            if (lVar43 == 0) goto LAB_00160dda;
LAB_0015ff92:
            if (*(long *)(lVar43 + 0x18) != 0) {
              lVar43 = *(long *)(*(long *)(*(long *)(lVar43 + 0x18) + 0x68) + 0xe0);
              auVar65 = FUN_000f08b0(lVar43 + 0xd0,&local_188);
              lVar34 = auVar65._0_8_;
              if ((lVar34 != auVar65._8_8_) && (*(long *)(lVar43 + 0xd8) != lVar34)) {
                FUN_00130e30(local_1e8,local_188,lVar34 + 8);
              }
            }
          }
          else {
            fVar56 = fStack_104 * local_154;
            fVar57 = fStack_104 * uStack_b0._4_4_;
            fVar62 = local_108 * fStack_164;
            fVar63 = local_108 * local_160;
            fVar64 = local_108 * uStack_c0._4_4_;
            fVar54 = local_f8._4_4_ * local_154;
            fVar55 = local_f8._4_4_ * uStack_b0._4_4_;
            local_108 = (float)uStack_100 * local_150 + uStack_100._4_4_ * (float)local_98 +
                        fStack_104 * local_15c + local_108 * local_168;
            fStack_104 = (float)uStack_100 * local_14c + uStack_100._4_4_ * local_98._4_4_ +
                         fStack_104 * local_158 + fVar62;
            uStack_100 = CONCAT44((float)uStack_100 * fStack_9c + uStack_100._4_4_ * uStack_90._4_4_
                                  + fVar57 + fVar64,
                                  (float)uStack_100 * local_148 +
                                  uStack_100._4_4_ * (float)uStack_90 + fVar56 + fVar63);
            fVar56 = (float)local_f8 * local_160;
            fVar57 = (float)local_f8 * uStack_c0._4_4_;
            fVar62 = local_e8 * fStack_164;
            fVar63 = local_e8 * local_160;
            fVar64 = local_e8 * uStack_c0._4_4_;
            fVar58 = fStack_e4 * local_154;
            fVar59 = fStack_e4 * uStack_b0._4_4_;
            local_f8 = (void *)CONCAT44((float)uStack_f0 * local_14c +
                                        uStack_f0._4_4_ * local_98._4_4_ +
                                        local_f8._4_4_ * local_158 + (float)local_f8 * fStack_164,
                                        (float)uStack_f0 * local_150 +
                                        uStack_f0._4_4_ * (float)local_98 +
                                        local_f8._4_4_ * local_15c + (float)local_f8 * local_168);
            uStack_f0 = CONCAT44((float)uStack_f0 * fStack_9c + uStack_f0._4_4_ * uStack_90._4_4_ +
                                 fVar55 + fVar57,
                                 (float)uStack_f0 * local_148 + uStack_f0._4_4_ * (float)uStack_90 +
                                 fVar54 + fVar56);
            fVar54 = fStack_e0 * fStack_9c;
            local_e8 = fStack_e0 * local_150 + fStack_dc * (float)local_98 + fStack_e4 * local_15c +
                       local_e8 * local_168;
            fStack_e4 = fStack_e0 * local_14c + fStack_dc * local_98._4_4_ + fStack_e4 * local_158 +
                        fVar62;
            fStack_e0 = fStack_e0 * local_148 + fStack_dc * (float)uStack_90 + fVar58 + fVar63;
            fStack_dc = fVar54 + fStack_dc * uStack_90._4_4_ + fVar59 + fVar64;
            local_d8 = local_150 * fVar61 +
                       local_15c * fVar25 + local_168 * local_228 + (float)local_98 * fStack_cc;
            fStack_d4 = local_14c * fVar61 +
                        local_158 * fVar25 + fStack_164 * local_228 + local_98._4_4_ * fStack_cc;
            fStack_d0 = local_148 * fVar61 +
                        local_154 * fVar25 + local_160 * local_228 + (float)uStack_90 * fStack_cc;
            fStack_cc = fStack_9c * fVar61 +
                        uStack_b0._4_4_ * fVar25 +
                        uStack_c0._4_4_ * local_228 + uStack_90._4_4_ * fStack_cc;
            if ((uVar32 & 0x400) == 0) goto LAB_0015ff79;
LAB_00160dad:
            FUN_00153740(local_1e8,local_188,uVar53,&local_108);
            lVar43 = player[0x155];
            if (lVar43 != 0) goto LAB_0015ff92;
LAB_00160dda:
            lVar43 = player[0x12f];
            if (lVar43 != 0) goto LAB_0015ff92;
          }
          lVar43 = player[0x18b];
          if (lVar43 != 0) {
            auVar65 = FUN_000f07d0(lVar43 + 0x3e0,&local_188);
            lVar34 = auVar65._0_8_;
            if ((lVar34 != auVar65._8_8_) && (*(long *)(lVar43 + 1000) != lVar34)) {
              FUN_00130c40(local_1e8,local_188,lVar34 + 8);
            }
          }
          puVar66 = uStack_80;
          if (_local_78 <= uStack_80) {
            iVar52 = iVar52 + 1;
            FUN_000f9520(local_88,&local_188);
            if (iVar51 == iVar52) break;
            goto LAB_0015faf0;
          }
          iVar52 = iVar52 + 1;
          uStack_80 = uStack_80 + 1;
          *puVar66 = local_188;
        } while (iVar51 != iVar52);
      }
LAB_0016005c:
      local_1a8 = local_88;
      if (local_1e8 != 0) {
        FUN_001476b0(local_1e8,local_1a8);
      }
    }
    uVar32 = local_120;
    if (((int)player[0x11] != -1) &&
       (lVar43 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + player[0x20b]) +
                                    0x10) + (long)(int)player[0x11] * 8), lVar43 != 0)) {
      uStack_100 = 0;
      sVar49 = local_120 - uVar31;
      local_f8 = (void *)0x0;
      uStack_f0 = 0;
      local_120 = uVar31;
      FUN_00c45fe0(&local_108,sVar49);
      uStack_c0 = 0;
      local_b8 = (void *)0x0;
      uStack_b0 = local_120;
      FUN_00c45fe0(local_c8,local_130);
      memcpy(local_b8,local_128,local_130);
      if (sVar49 != 0) {
        memcpy(local_f8,(void *)(uStack_b0 + (long)local_b8),sVar49);
        uStack_b0 = uStack_b0 + sVar49;
        uStack_f0 = uStack_f0 + sVar49;
      }
      if ((uStack_c0 != 0) && (local_b8 != (void *)0x0)) {
        eastl__basic_string();
      }
      uVar7 = *(undefined8 *)(lVar43 + 0x98);
      *(long *)(lVar43 + 0x98) = uStack_f0;
      lVar34 = *(long *)(lVar43 + 0x88);
      *(long *)(lVar43 + 0x88) = uStack_100;
      lVar8 = *(long *)(lVar43 + 0x90);
      *(void **)(lVar43 + 0x90) = local_f8;
      *(undefined **)(lVar43 + 0xa0) = &DAT_00cb6ab0;
      uStack_100 = lVar34;
      local_f8 = (void *)lVar8;
      uStack_f0 = uVar7;
      if ((lVar8 != 0) && (lVar34 != 0)) {
        eastl__basic_string();
      }
    }
    local_120 = uVar32;
    if ((local_88 != (undefined1  [8])0x0) && (local_88 != (undefined1  [8])local_68)) {
      eastl__basic_string();
    }
  }
  local_110 = &DAT_00cb6aac;
  if ((uVar38 & 0x800) != 0) {
    FUN_0047f6c0(local_138);
    Packet::gScrambledUint(local_138);
    Packet::gScrambledByte((long)local_138);
  }
  local_110 = &DAT_00cb6aa9;
  if ((uVar38 & 0x8000) != 0) {
    FUN_0047f6c0(local_138);
    Packet::gScrambledUint(local_138);
    Packet::gScrambledByte((long)local_138);
  }
  local_110 = &DAT_00cb6aa0;
  if ((uVar38 & 0x80) != 0) {
    uVar41 = Packet::gScrambledUbyte((long)local_138);
    uVar46 = Packet::gScrambledUbyte((long)local_138);
    uVar23 = Packet::gScrambledUbyte((long)local_138);
    uVar24 = Packet::gScrambledUbyte((long)local_138);
    uVar26 = Packet::gScrambledUbyte((long)local_138);
    uVar27 = Packet::gScrambledUbyte((long)local_138);
    uVar18 = FUN_0047f6c0(local_138);
    uVar21 = FUN_0047f6c0(local_138);
    uVar22 = FUN_0047f6c0(local_138);
    game::Conversion::JagexAngleToRadians(uVar22);
    PathingEntity::SetForcedMovement
              (player,(int)(char)uVar41,(int)(char)uVar46,(int)(char)uVar23,(int)(char)uVar24,
               (int)(char)uVar26,(int)(char)uVar27,uVar18,uVar21,updateFlagsMask);
  }
  local_110 = &DAT_00cb6a97;
  if (((uVar38 & 0x4000) != 0) &&
     (uVar31 = Packet::gScrambledByte((long)local_138), (char)uVar31 != '\0')) {
    uStack_80 = (uint *)0x0;
    _local_78 = ZEXT816(0);
    FUN_00c45fe0(local_88,uVar31 & 0xff);
    FUN_0047ec70(local_138,local_88,uVar31 & 0xff);
    FUN_00121b00(player,local_88);
    if (iVar2 == -1) {
LAB_0015f670:
    }
    else {
      lVar43 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + player[0x20b]) +
                                  0x10) + (long)iVar2 * 8);
      if (lVar43 != 0) {
        *(undefined8 *)(lVar43 + 0x78) = uStack_70;
        FUN_00c45fe0(lVar43 + 0x60);
        memcpy(*(void **)(lVar43 + 0x70),_local_78,(size_t)uStack_80);
        goto LAB_0015f670;
      }
    }
    if ((_local_78 != (uint *)0x0) && (uStack_80 != (uint *)0x0)) {
      eastl__basic_string();
    }
  }
  local_110 = &DAT_00cb6a94;
  if ((uVar38 & 0x2000000) != 0) {
    FUN_0047f6c0(local_138);
    Packet::gScrambledUint(local_138);
    Packet::gScrambledByte((long)local_138);
  }
  local_110 = &DAT_00cb6a93;
  if ((uVar38 & 0x10000) != 0) {
    FUN_00121a30(local_138);
    puVar14 = local_110;
    uVar31 = local_120 + 1;
    bVar3 = *(byte *)((long)local_128 + local_120);
    local_120 = uVar31;
    if (bVar3 != 0) {
      plVar40 = player + 0x29;
      if ((bVar3 & 1) == 0) {
        uVar41 = 0;
      }
      else {
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_190 = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_190,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
        if (bVar3 == 1) goto LAB_0015eb56;
        uVar41 = 1;
      }
      do {
        uVar41 = uVar41 + 2;
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_190 = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_190,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_190 = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_190,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
      } while (bVar3 != uVar41);
    }
  }
LAB_0015eb56:
  local_110 = &DAT_00cb6a90;
  if ((uVar38 & 0x200) != 0) {
    Packet::gScrambledByte((long)local_138);
    Packet::gScrambledByte((long)local_138);
    FUN_0047f6c0(local_138);
  }
  local_110 = &DAT_00cb6a8f;
  if ((uVar38 & 2) != 0) {
    uVar18 = FUN_0047f6c0(local_138);
    uVar53 = game::Conversion::JagexAngleToRadians(uVar18);
    *(undefined4 *)((long)player + 0x23c) = uVar53;
  }
  local_110 = &DAT_00cb6a8e;
  if ((uVar38 & 0x800000) != 0) {
    FUN_00121a30(local_138);
    FUN_004e4500(player[0x2a],player[0x2b]);
    puVar14 = local_110;
    player[0x2c] = 0;
    uVar31 = local_120 + 1;
    bVar3 = *(byte *)((long)local_128 + local_120);
    local_120 = uVar31;
    if (bVar3 != 0) {
      plVar40 = player + 0x29;
      if ((bVar3 & 1) == 0) {
        uVar41 = 0;
      }
      else {
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_18c = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_18c,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
        if (bVar3 == 1) goto LAB_0015ebac;
        uVar41 = 1;
      }
      do {
        uVar41 = uVar41 + 2;
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_18c = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_18c,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
        uVar31 = Packet::gScrambledByte((long)local_138);
        uVar31 = uVar31 & 0xffffffff;
        uVar17 = FUN_00121a30(local_138,uVar31);
        plVar35 = (long *)SpotAnim::GetTypeBySlot(uVar31 & 0xff);
        uStack_70._0_1_ = 4;
        (**(code **)(*plVar35 + 0x20))(plVar35,local_88,local_138);
        local_18c = (uint)uVar17;
        FUN_00ae4c30(local_c8,plVar40,&local_18c,uVar17);
        FUN_0048f080(CONCAT44(local_c8[1],local_c8[0]) + 8,local_88);
        local_110 = puVar14;
        FUN_0047fe00(local_88);
      } while (bVar3 != uVar41);
    }
  }
LAB_0015ebac:
  local_110 = &DAT_00cb6a8d;
  if ((uVar38 & 0x20) != 0) {
    lVar43 = 0;
    do {
      puVar45 = local_138;
      fVar25 = (float)Packet::gSmart2or4s();
      local_c8[lVar43] = fVar25;
      lVar43 = lVar43 + 1;
    } while (lVar43 != 4);
    uVar31 = Packet::gScrambledByte((long)puVar45);
    uVar31 = uVar31 & 0xff;
    if (*(code **)(*player + 0x1e0) == FUN_000f21b0) {
      if (player[0x20d] == 0) {
        FUN_00c29ca0(local_88,0x28,0);
        *(undefined4 *)((long)uStack_80 + 0x24) = uVar5;
        lVar43 = player[0x255];
        *(ulong *)uStack_80 = CONCAT44(local_c8[1],local_c8[0]);
        *(ulong *)((long)uStack_80 + 8) = uStack_c0;
        *(int *)((long)uStack_80 + 0x10) = (int)uVar31;
        *(undefined1 *)((long)uStack_80 + 0x14) = 0;
        *(undefined8 *)((long)uStack_80 + 0x18) = uVar6;
        *(int *)((long)uStack_80 + 0x20) = updateFlagsMask;
        player[0x255] = (long)uStack_80;
        if (lVar43 != 0) {
          eastl__basic_string();
        }
      }
      else {
        FUN_00b0d5b0(player,local_c8,uVar31,0,uVar6,updateFlagsMask,uVar5,0);
      }
    }
    else {
      (**(code **)(*player + 0x1e0))(player,local_c8,uVar31,0,uVar6,updateFlagsMask,uVar5,0);
    }
  }
  local_110 = &DAT_00cb6a8a;
  if ((uVar38 & 0x80000) != 0) {
    FUN_0047f6c0(local_138);
    Packet::gScrambledUint(local_138);
    Packet::gScrambledByte((long)local_138);
  }
  local_110 = &DAT_00cb6a89;
  if ((uVar38 & 0x1000) != 0) {
    uVar31 = Packet::gScrambledByte((long)local_138);
    *(uint *)((long)player + 0x1074) = (uint)uVar31 & 0xff;
  }
  local_110 = &DAT_00cb6a86;
  if ((uVar38 & 4) != 0) {
    FUN_0047f6c0(local_138);
    Packet::gScrambledUint(local_138);
    Packet::gScrambledByte((long)local_138);
  }
  local_110 = &DAT_00cb6a85;
  if ((uVar38 & 0x400) != 0) {
    auVar12[0xf] = 0;
    auVar12._0_15_ = stack0xffffffffffffff79;
    _local_88 = auVar12 << 8;
    cStack_71 = 0x17;
    FUN_00ad80e0(local_138,local_88);
    lVar43 = player[0x20b];
    lVar34 = FUN_00198b40(*(undefined8 *)((long)&__DT_RELA[0xd40].r_addend + lVar43));
    if (player == *(long **)(lVar34 + 8)) {
      uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + lVar43);
      auVar39 = (undefined1  [8])local_88;
      if (cStack_71 < '\0') {
        auVar39 = local_88;
      }
      local_188 = local_188 & 0xffffff00;
      local_171 = '\x17';
      cVar50 = *(char *)auVar39;
      while (cVar50 != '\0') {
        auVar39 = (undefined1  [8])((long)auVar39 + 1);
        cVar50 = *(char *)auVar39;
      }
      puVar66 = &local_188;
      FUN_0048d1a0(puVar66);
      plVar40 = player + 0x12;
      if (*(char *)((long)player + 0xa7) < '\0') {
        plVar40 = (long *)player[0x12];
      }
      local_168 = (float)((uint)local_168 & 0xffffff00);
      local_154 = (float)CONCAT13(0x17,(undefined3)local_154);
      cVar50 = (char)*plVar40;
      while (cVar50 != '\0') {
        plVar40 = (long *)((long)plVar40 + 1);
        cVar50 = *(char *)plVar40;
      }
      FUN_0048d1a0();
      plVar40 = player + 0x1f;
      if (*(char *)((long)player + 0x10f) < '\0') {
        plVar40 = (long *)player[0x1f];
      }
      local_108 = (float)((uint)local_108 & 0xffffff00);
      local_f8 = (void *)CONCAT17(0x17,(undefined7)local_f8);
      cVar50 = (char)*plVar40;
      while (cVar50 != '\0') {
        plVar40 = (long *)((long)plVar40 + 1);
        cVar50 = *(char *)plVar40;
      }
      FUN_0048d1a0(&local_108);
      FUN_00af72e0(local_c8,player);
      ChatHistory::AddChat(uVar6,2,0,local_c8,&local_108,&local_168,puVar66,0);
      if (((long)local_b8 < 0) && (CONCAT44(local_c8[1],local_c8[0]) != 0)) {
        eastl__basic_string(CONCAT44(local_c8[1],local_c8[0]),puVar66);
      }
      if (((long)local_f8 < 0) && (CONCAT44(fStack_104,local_108) != 0)) {
        eastl__basic_string();
      }
      if (((int)local_154 < 0) && (CONCAT44(fStack_164,local_168) != 0)) {
        eastl__basic_string();
      }
      if ((local_171 < '\0') && (CONCAT44(uStack_184,local_188) != 0)) {
        eastl__basic_string();
      }
    }
    (**(code **)(*player + 0x158))(player,local_88,0,0);
    if ((cStack_71 < '\0') && (local_88 != (undefined1  [8])0x0)) {
      eastl__basic_string();
    }
  }
  local_110 = &DAT_00cb6a7f;
  if ((uVar38 & 0x200000) != 0) {
    uVar41 = Packet::gScrambledUbyte((long)local_138);
    uVar46 = Packet::gScrambledUbyte((long)local_138);
    uVar23 = Packet::gScrambledUbyte((long)local_138);
    uVar31 = Packet::gScrambledByte((long)local_138);
    uVar17 = FUN_0047f6c0(local_138);
    iVar2 = *(int *)(player[0x20b] + 0x500);
    uVar20 = FUN_0047f6c0(local_138);
    iVar51 = *(int *)(player[0x20b] + 0x500);
    uVar41 = math::ColourUtils::HSLToRGBLookup
                       (uVar23 & 0x7f | (uVar46 & 7) << 7 | (uVar41 & 0x3f) << 10);
    *(uint *)(player + 0x34) = (uint)uVar17 + iVar2;
    *(uint *)((long)player + 0x1a4) = (uint)uVar20 + iVar51;
    fVar25 = (float)((uint)uVar31 & 0xff) * DAT_00cb6b50;
    fVar61 = (float)(uVar41 & 0xff) * DAT_00cb6b4c;
    player[0x32] = CONCAT44((float)(uVar41 >> 8 & 0xff) * DAT_00cb6b4c,
                            (float)(uVar41 >> 0x10 & 0xff) * DAT_00cb6b4c);
    player[0x33] = CONCAT44(fVar25,fVar61);
  }
  local_110 = &DAT_00cb6a78;
  if ((uVar38 & 0x10) != 0) {
    uVar31 = Packet::gScrambledByte((long)local_138);
    puVar14 = local_110;
    local_1f0 = (uint)uVar31 & 0xff;
    if ((uVar31 & 0xff) != 0) {
      uVar41 = 0;
LAB_0015eeb8:
      local_110 = puVar14;
      uVar17 = Packet::gSmart1or2(local_138);
      uVar46 = (uint)uVar17;
      if (uVar17 == 0x7fff) {
        uVar17 = Packet::gSmart1or2(local_138);
        uVar46 = (uint)uVar17;
        uVar17 = Packet::gSmart1or2();
        local_200 = (ulong)uVar17;
        uVar17 = Packet::gSmart1or2();
        local_208 = (ulong)uVar17;
        uVar17 = Packet::gSmart1or2();
        local_1f8 = (ulong)uVar17;
      }
      else if (uVar17 == 0x7ffe) {
        local_200 = Packet::gScrambledByte((long)local_138);
        local_1f8 = 0xffffffff;
        local_208 = 0xffffffff;
        local_200 = local_200 & 0xff;
        uVar46 = 0xffffffff;
      }
      else {
        uVar17 = Packet::gSmart1or2();
        local_1f8 = 0xffffffff;
        local_208 = 0xffffffff;
        local_200 = (ulong)uVar17;
      }
      uVar18 = Packet::gSmart1or2(local_138);
      lVar43 = player[0x1df];
      if (lVar43 == 0) {
        lVar34 = player[0xf];
        lVar43 = operator_new(0x40);
        FUN_00627480(lVar43,lVar34);
        lVar34 = player[0x1df];
        player[0x1df] = lVar43;
        if (lVar34 == 0) goto LAB_0015ee6b;
        puVar9 = *(undefined8 **)(lVar34 + 0x30);
        puVar44 = *(undefined8 **)(lVar34 + 0x28);
        if (puVar9 != puVar44) {
          puVar42 = puVar44;
          puVar47 = puVar44 + 0x36;
          do {
            puVar10 = (undefined8 *)*puVar42;
            while (puVar48 = puVar10, puVar10 != puVar42) {
              while( true ) {
                plVar40 = (long *)puVar48[4];
                puVar10 = (undefined8 *)*puVar48;
                if (plVar40 != (long *)0x0) {
                  LOCK();
                  plVar35 = plVar40 + 1;
                  lVar43 = *plVar35;
                  *(int *)plVar35 = (int)*plVar35 + -1;
                  UNLOCK();
                  if ((int)lVar43 == 1) {
                    (**(code **)(*plVar40 + 0x10))(plVar40);
                    LOCK();
                    piVar1 = (int *)((long)plVar40 + 0xc);
                    iVar2 = *piVar1;
                    *piVar1 = *piVar1 + -1;
                    UNLOCK();
                    if (iVar2 == 1) {
                      (**(code **)(*plVar40 + 0x18))(plVar40);
                    }
                  }
                }
                plVar40 = (long *)puVar48[2];
                if (plVar40 != (long *)0x0) {
                  LOCK();
                  plVar35 = plVar40 + 1;
                  lVar43 = *plVar35;
                  *(int *)plVar35 = (int)*plVar35 + -1;
                  UNLOCK();
                  if ((int)lVar43 == 1) {
                    (**(code **)(*plVar40 + 0x10))(plVar40);
                    LOCK();
                    piVar1 = (int *)((long)plVar40 + 0xc);
                    iVar2 = *piVar1;
                    *piVar1 = *piVar1 + -1;
                    UNLOCK();
                    if (iVar2 == 1) {
                      (**(code **)(*plVar40 + 0x18))(plVar40);
                    }
                  }
                }
                if ((puVar48 < (undefined8 *)puVar42[7]) || ((undefined8 *)puVar42[4] <= puVar48))
                break;
                *puVar48 = puVar42[2];
                puVar42[2] = puVar48;
                puVar48 = puVar10;
                if (puVar10 == puVar42) goto LAB_001603ac;
              }
              HeapInterface::Free(puVar48);
            }
LAB_001603ac:
            if (puVar47 ==
                puVar44 + ((((ulong)((long)puVar9 - (long)(puVar44 + 0x36)) >> 4) *
                            0x4bda12f684bda13 & 0xfffffffffffffff) * 3 + 3) * 0x12)
            goto LAB_00161190;
            puVar42 = puVar47;
            puVar47 = puVar47 + 0x36;
          } while( true );
        }
        goto LAB_001611a2;
      }
      goto LAB_0015ee6b;
    }
LAB_0015ef55:
    local_110 = puVar14 + 1;
    uVar31 = Packet::gScrambledByte((long)local_138);
    puVar15 = local_110;
    local_208._0_4_ = (uint)uVar31 & 0xff;
    if ((uVar31 & 0xff) != 0) {
      uVar41 = 0;
LAB_0015f0a6:
      puVar45 = local_138;
      local_110 = puVar15;
      uVar18 = Packet::gSmart1or2();
      sVar16 = Packet::gSmart1or2();
      if (sVar16 != 0x7fff) {
        uVar17 = Packet::gSmart1or2();
        local_110 = puVar14 + 2;
        uVar31 = Packet::gScrambledByte((long)puVar45);
        local_20c = (uint)uVar31;
        if (sVar16 != 0) {
          local_110 = puVar14 + 3;
          uVar32 = Packet::gScrambledByte((long)local_138);
          local_20c = (uint)uVar32;
        }
        local_20c = local_20c & 0xff;
        pbVar33 = (byte *)((long)local_128 + local_120);
        if ((char)*pbVar33 < '\0') {
          sVar19 = FUN_00121a30(local_138);
          sVar19 = sVar19 + 0x7fff;
        }
        else {
          local_120 = local_120 + 1;
          sVar19 = *pbVar33 - 1;
        }
        if (sVar19 < 0) {
          local_1f8 = 0;
          local_1e8 = 0;
        }
        else {
          local_110 = puVar14 + 4;
          local_1f8 = Packet::gScrambledByte((long)local_138);
          local_1e8 = local_1f8 & 0xff;
          local_1f8 = local_1f8 & 0xff;
          if (sVar16 != 0) {
            local_110 = puVar14 + 5;
            local_1f8 = Packet::gScrambledByte((long)local_138);
            local_1f8 = local_1f8 & 0xff;
          }
        }
        lVar43 = player[0x1df];
        lVar34 = player[0xf];
        if (lVar43 == 0) {
          lVar43 = operator_new(0x40);
          FUN_00627480(lVar43,lVar34);
          lVar34 = player[0x1df];
          player[0x1df] = lVar43;
          if (lVar34 == 0) {
            lVar34 = player[0xf];
            goto LAB_0015f04e;
          }
          puVar9 = *(undefined8 **)(lVar34 + 0x30);
          puVar44 = *(undefined8 **)(lVar34 + 0x28);
          if (puVar9 != puVar44) {
            puVar42 = puVar44;
            puVar47 = puVar44 + 0x36;
            do {
              puVar10 = (undefined8 *)*puVar42;
              while (puVar48 = puVar10, puVar10 != puVar42) {
                while( true ) {
                  plVar40 = (long *)puVar48[4];
                  puVar10 = (undefined8 *)*puVar48;
                  if (plVar40 != (long *)0x0) {
                    LOCK();
                    plVar35 = plVar40 + 1;
                    *(int *)plVar35 = (int)*plVar35 + -1;
                    UNLOCK();
                    if ((int)*plVar35 == 0) {
                      (**(code **)(*plVar40 + 0x10))(plVar40);
                      LOCK();
                      piVar1 = (int *)((long)plVar40 + 0xc);
                      *piVar1 = *piVar1 + -1;
                      UNLOCK();
                      if (*piVar1 == 0) {
                        (**(code **)(*plVar40 + 0x18))(plVar40);
                      }
                    }
                  }
                  plVar40 = (long *)puVar48[2];
                  if (plVar40 != (long *)0x0) {
                    LOCK();
                    plVar35 = plVar40 + 1;
                    *(int *)plVar35 = (int)*plVar35 + -1;
                    UNLOCK();
                    if ((int)*plVar35 == 0) {
                      (**(code **)(*plVar40 + 0x10))(plVar40);
                      LOCK();
                      piVar1 = (int *)((long)plVar40 + 0xc);
                      *piVar1 = *piVar1 + -1;
                      UNLOCK();
                      if (*piVar1 == 0) {
                        (**(code **)(*plVar40 + 0x18))(plVar40);
                      }
                    }
                  }
                  if ((puVar48 < (undefined8 *)puVar42[7]) || ((undefined8 *)puVar42[4] <= puVar48))
                  break;
                  *puVar48 = puVar42[2];
                  puVar42[2] = puVar48;
                  puVar48 = puVar10;
                  if (puVar10 == puVar42) goto LAB_00161308;
                }
                HeapInterface::Free(puVar48);
              }
LAB_00161308:
              if (puVar47 ==
                  puVar44 + ((((ulong)((long)puVar9 - (long)(puVar44 + 0x36)) >> 4) *
                              0x4bda12f684bda13 & 0xfffffffffffffff) * 3 + 3) * 0x12)
              goto LAB_00161640;
              puVar42 = puVar47;
              puVar47 = puVar47 + 0x36;
            } while( true );
          }
          goto LAB_0016165a;
        }
        goto LAB_0015f04e;
      }
      if (player[0x1df] != 0) {
        FUN_00626a40(player[0x1df],uVar18);
      }
      goto LAB_0015f08a;
    }
  }
LAB_0015ece7:
  local_110 = &DAT_00cb6a74;
  if ((uVar38 & 0x20000) != 0) {
    Packet::gScrambledByte((long)local_138);
    FUN_0047f6c0(local_138);
    FUN_0047f6c0(local_138);
    FUN_0047f6c0(local_138);
  }
  *(ulong *)(local_118 + 0x18) = local_120;
  if ((local_130 != 0) && (local_128 != (void *)0x0)) {
    eastl__basic_string();
  }
  return;
LAB_00161190:
  puVar44 = *(undefined8 **)(lVar34 + 0x28);
LAB_001611a2:
  if (puVar44 != (undefined8 *)0x0) {
    HeapInterface::Free(puVar44);
  }
  if ((*(long *)(lVar34 + 0x18) != 0) && (*(long *)(lVar34 + 0x20) != 0)) {
    HeapInterface::Free();
  }
  HeapInterface::Free(lVar34,0x40);
  lVar43 = player[0x1df];
LAB_0015ee6b:
  HitmarksAndHeadbars::AddHitmark
            (lVar43,player[0xf],uVar46,local_200,local_208,local_1f8,updateFlagsMask,uVar18);
  uVar41 = uVar41 + 1;
  if (local_1f0 == uVar41) goto LAB_0015ef55;
  goto LAB_0015eeb8;
LAB_00161640:
  puVar44 = *(undefined8 **)(lVar34 + 0x28);
LAB_0016165a:
  if (puVar44 != (undefined8 *)0x0) {
    HeapInterface::Free(puVar44);
  }
  if ((*(long *)(lVar34 + 0x18) != 0) && (*(long *)(lVar34 + 0x20) != 0)) {
    HeapInterface::Free();
  }
  HeapInterface::Free(lVar34,0x40);
  lVar34 = player[0xf];
  lVar43 = player[0x1df];
LAB_0015f04e:
  HitmarksAndHeadbars::AddHeadbar
            (lVar43,lVar34,uVar18,(int)sVar19,(uint)uVar17 + updateFlagsMask,uVar31 & 0xff,local_20c
             ,local_1e8,local_1f8,sVar16);
LAB_0015f08a:
  uVar41 = uVar41 + 1;
  if ((uint)local_208 == uVar41) goto LAB_0015ece7;
  goto LAB_0015f0a6;
}


```

## `jag::PathingEntity::ApplyDeferredAppearance` @ 0014eaf0
```c

void jag::PathingEntity::ApplyDeferredAppearance(long param_1,ulong param_2,char param_3)

{
  long lVar1;
  long lVar2;
  char cVar3;
  long lVar4;
  
  lVar4 = *(long *)((long)&__DT_SYMTAB[0xa5].st_value + param_1);
  lVar1 = *(long *)((long)&__DT_SYMTAB[0xa6].st_name + param_1);
  if (lVar4 == 0) {
    if (*(long *)((long)&__DT_SYMTAB[0x8e].st_name + param_1) == 0) {
      if (param_3 == '\0') {
        return;
      }
      lVar4 = *(long *)((long)&__DT_SYMTAB[0xa5].st_size + param_1);
      if (lVar4 != 0) {
        FUN_001418e0(*(undefined8 *)(lVar4 + 0x98));
      }
    }
    goto LAB_0014ec2e;
  }
  if (*(char *)(lVar4 + 0x89) == '\0') {
    PlayerEntity::SetAppearanceAsPlayer(lVar4);
    param_2 = param_2 & 0xffffffff;
    if (*(char *)(lVar4 + 0x89) != '\0') goto LAB_0014eb26;
  }
  else {
LAB_0014eb26:
    if (*(char *)(lVar4 + 0x8a) == '\0') {
      if (*(char *)(lVar4 + 0x88) == '\0') {
        *(undefined1 *)(lVar4 + 0x8a) = 1;
      }
      else {
        cVar3 = game::SceneManager::IsWorldReady(lVar4 + 0x78,param_2);
        if (cVar3 != '\0') {
          *(undefined1 *)(lVar4 + 0x8a) = 1;
          game::WorldReference::Release(lVar4 + 0x78);
        }
      }
    }
  }
  if (param_3 == '\0') {
    return;
  }
  lVar4 = *(long *)((long)&__DT_SYMTAB[0xa5].st_value + param_1);
  if (*(char *)(lVar4 + 0x8a) == '\0') {
    return;
  }
  lVar2 = *(long *)((long)&__DT_RELA[0x2d1].r_addend +
                   *(long *)((long)&__DT_RELA[0xcfd].r_offset +
                            *(long *)((long)&__DT_SYMTAB[0x8d].st_value + param_1)));
  if ((lVar2 == 0) || (*(long *)((long)&__DT_SYMTAB[0x8e].st_name + param_1) != lVar2)) {
    FUN_001418e0(*(undefined8 *)(lVar4 + 0x98));
    lVar4 = *(long *)((long)&__DT_SYMTAB[0xa5].st_value + param_1);
  }
  lVar2 = *(long *)((long)&__DT_SYMTAB[0xa5].st_size + param_1);
  *(undefined8 *)((long)&__DT_SYMTAB[0xa5].st_value + param_1) = 0;
  *(long *)((long)&__DT_SYMTAB[0xa5].st_size + param_1) = lVar4;
  if (lVar2 != 0) {
    if (*(long *)(lVar2 + 0x90) != 0) {
      ref_counter_base::DecRef();
    }
    game::WorldReference::Detach(lVar2 + 0x78);
    if ((*(long *)(lVar2 + 0x60) != 0) && (*(long *)(lVar2 + 0x68) != 0)) {
      eastl__basic_string();
    }
    if ((*(char *)(lVar2 + 0x27) < '\0') && (*(long *)(lVar2 + 0x10) != 0)) {
      eastl__basic_string();
    }
    eastl__basic_string(lVar2);
  }
LAB_0014ec2e:
  if ((lVar1 != 0) && (param_3 != '\0')) {
    lVar4 = *(long *)((long)&__DT_SYMTAB[0xa6].st_name + param_1);
    FUN_00b0d5b0(param_1,lVar4,*(undefined4 *)(lVar4 + 0x10),*(undefined1 *)(lVar4 + 0x14),
                 *(undefined8 *)(lVar4 + 0x18),*(undefined4 *)(lVar4 + 0x20),
                 *(undefined4 *)(lVar4 + 0x24));
    lVar4 = *(long *)((long)&__DT_SYMTAB[0xa6].st_name + param_1);
    *(undefined8 *)((long)&__DT_SYMTAB[0xa6].st_name + param_1) = 0;
    if (lVar4 != 0) {
      eastl__basic_string();
      return;
    }
  }
  return;
}


```
