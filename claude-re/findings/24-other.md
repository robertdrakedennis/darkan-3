# Remaining handler decompilations (binary)


## `jag::packethandlers::NPCInfo::NPC_SPOTANIM` @ 000f1f70
```c

undefined * jag::packethandlers::NPCInfo::NPC_SPOTANIM(long *param_1,long param_2)

{
  char cVar1;
  byte bVar2;
  char cVar3;
  uint uVar4;
  long lVar5;
  long *plVar6;
  long lVar7;
  uint local_28;
  uint local_24;
  uint local_20;
  uint local_1c;
  
  lVar7 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar7 + 2;
  cVar1 = *(char *)(lVar5 + lVar7);
  bVar2 = *(byte *)(lVar5 + 1 + lVar7);
  *(long *)(param_2 + 0x18) = lVar7 + 6;
  local_28 = *(uint *)(lVar5 + 2 + lVar7);
  if (DAT_01050dc0 == 0x3020100) {
    local_28 = local_28 >> 0x18 | (local_28 & 0xff0000) >> 8 | (local_28 & 0xff00) << 8 |
               local_28 << 0x18;
    *(long *)(param_2 + 0x18) = lVar7 + 10;
    uVar4 = *(uint *)(lVar5 + 6 + lVar7);
    *(long *)(param_2 + 0x18) = lVar7 + 0xe;
    local_24 = uVar4 >> 0x18 | (uVar4 & 0xff0000) >> 8 | (uVar4 & 0xff00) << 8 | uVar4 << 0x18;
    uVar4 = *(uint *)(lVar5 + 10 + lVar7);
    *(long *)(param_2 + 0x18) = lVar7 + 0x12;
    local_20 = uVar4 >> 0x18 | (uVar4 & 0xff0000) >> 8 | (uVar4 & 0xff00) << 8 | uVar4 << 0x18;
    uVar4 = *(uint *)(lVar5 + 0xe + lVar7);
    local_1c = uVar4 >> 0x18 | (uVar4 & 0xff0000) >> 8 | (uVar4 & 0xff00) << 8 | uVar4 << 0x18;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar7 + 10;
    local_24 = *(uint *)(lVar5 + 6 + lVar7);
    *(long *)(param_2 + 0x18) = lVar7 + 0xe;
    local_20 = *(uint *)(lVar5 + 10 + lVar7);
    *(long *)(param_2 + 0x18) = lVar7 + 0x12;
    local_1c = *(uint *)(lVar5 + 0xe + lVar7);
  }
  *(long *)(param_2 + 0x18) = lVar7 + 0x13;
  cVar3 = *(char *)(lVar5 + lVar7 + 0x12);
  lVar7 = NPCList::GetNPCNode(*(undefined8 *)((long)&__DT_RELA[0xcfb].r_addend + *param_1),
                              (ushort)bVar2 * 0x100 + (ushort)(byte)(cVar1 + 0x80));
  plVar6 = *(long **)(lVar7 + 8);
  if (plVar6 != (long *)0x0) {
    if (*(code **)(*plVar6 + 0x1e0) != FUN_00461730) {
      (**(code **)(*plVar6 + 0x1e0))
                (plVar6,&local_28,-0x80 - cVar3,1,*DAT_015da128,*(undefined4 *)(*param_1 + 0x500),
                 *(undefined4 *)
                  ((long)&__DT_RELA[0x548].r_addend +
                  *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1) + 0x2f0)),0);
      return &DAT_015d3620;
    }
    FUN_00b0d1c0();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::NPCInfo::SET_NPC_UPDATE_FLAG` @ 00173b00
```c

undefined * jag::packethandlers::NPCInfo::SET_NPC_UPDATE_FLAG(long *param_1,long param_2)

{
  int *piVar1;
  char cVar2;
  long lVar3;
  long lVar4;
  long lVar5;
  
  lVar3 = *(long *)(param_2 + 0x18);
  lVar4 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  lVar5 = *(long *)((long)&__DT_RELA[0xcfb].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar3 + 1;
  cVar2 = *(char *)(*(long *)(param_2 + 0x10) + lVar3);
  *(undefined1 *)(lVar4 + 0x14) = 1;
  piVar1 = (int *)(lVar4 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(bool *)(lVar5 + 0x6a) = cVar2 == -1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::SET_CHAT_FILTER_A` @ 00173da0
```c

undefined * jag::packethandlers::Chat::SET_CHAT_FILTER_A(long *param_1,long param_2)

{
  byte bVar1;
  long lVar2;
  uint uVar3;
  uint uVar4;
  
  lVar2 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar2 + 1;
  bVar1 = *(byte *)(*(long *)(param_2 + 0x10) + lVar2);
  uVar3 = bVar1 + 3;
  uVar4 = 3;
  if (uVar3 < 8) {
    uVar4 = 3;
    if ((1L << ((byte)uVar3 & 0x3f) & 0xe3U) != 0) {
      uVar4 = (uint)bVar1;
    }
  }
  lVar2 = *(long *)((long)&__DT_RELA[0xcf4].r_offset + *param_1);
  *(uint *)(lVar2 + 0x3c) = uVar4;
  if (-1 < *(char *)(lVar2 + 0x57)) {
    *(undefined1 *)(lVar2 + 0x57) = 0x17;
    *(undefined1 *)(lVar2 + 0x40) = 0;
    return &DAT_015d3620;
  }
  *(undefined8 *)(lVar2 + 0x48) = 0;
  **(undefined1 **)(lVar2 + 0x40) = 0;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::SET_CHAT_FILTER_B` @ 00173ed0
```c

undefined * jag::packethandlers::Chat::SET_CHAT_FILTER_B(long *param_1,long param_2)

{
  byte bVar1;
  long lVar2;
  uint uVar3;
  uint uVar4;
  
  lVar2 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar2 + 1;
  bVar1 = *(byte *)(*(long *)(param_2 + 0x10) + lVar2);
  uVar3 = bVar1 + 3;
  uVar4 = 3;
  if (uVar3 < 0x2a) {
    uVar4 = 3;
    if ((1L << ((byte)uVar3 & 0x3f) & 0x23e01803fe3U) != 0) {
      uVar4 = (uint)bVar1;
    }
  }
  *(uint *)(*(long *)((long)&__DT_RELA[0xcf4].r_offset + *param_1) + 0x30) = uVar4;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SOUND_MODIFY` @ 00175e10
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::Audio::SOUND_MODIFY(long *param_1,long param_2)

{
  long lVar1;
  long *plVar2;
  uint uVar3;
  ushort uVar4;
  long lVar5;
  ushort *puVar6;
  uint uVar7;
  ushort uVar8;
  long lVar9;
  float fVar10;
  
  lVar1 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar1 + 2;
  uVar4 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar1);
  puVar6 = (ushort *)(*(long *)(param_2 + 0x10) + lVar1 + 2);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar1 + 4;
    uVar8 = *puVar6;
    uVar4 = uVar4 << 8 | uVar4 >> 8;
    uVar8 = uVar8 << 8 | uVar8 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1 + 4;
    uVar8 = *puVar6;
  }
  plVar2 = *(long **)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if (plVar2 == (long *)0x0) goto LAB_00176058;
  lVar1 = *(long *)(*plVar2 + 8);
  if ((lVar1 == 0) || (lVar1 = *(long *)(lVar1 + 8), lVar1 == 0)) {
LAB_00176020:
    lVar9 = DAT_015df4e8;
    if (DAT_015df4e8 == 0) goto LAB_00176058;
  }
  else {
    lVar5 = *(long *)(lVar1 + 0x18);
    lVar1 = *(long *)(lVar1 + 0x20);
    if (lVar5 == lVar1) goto LAB_00176020;
    uVar3 = (int)((lVar1 - lVar5) - 0x10U >> 4) + 1U & 7;
    uVar7 = (uint)uVar4;
    if (uVar3 == 0) {
LAB_00175f84:
      do {
        lVar9 = *(long *)(lVar5 + 8);
        if (((((((lVar9 != 0) && (uVar7 == *(uint *)(lVar9 + 0x34))) ||
               ((lVar9 = *(long *)(lVar5 + 0x18), lVar9 != 0 && (uVar7 == *(uint *)(lVar9 + 0x34))))
               ) || ((lVar9 = *(long *)(lVar5 + 0x28), lVar9 != 0 &&
                     (uVar7 == *(uint *)(lVar9 + 0x34))))) ||
             ((lVar9 = *(long *)(lVar5 + 0x38), lVar9 != 0 && (uVar7 == *(uint *)(lVar9 + 0x34)))))
            || ((((lVar9 = *(long *)(lVar5 + 0x48), lVar9 != 0 && (uVar7 == *(uint *)(lVar9 + 0x34))
                  ) || ((lVar9 = *(long *)(lVar5 + 0x58), lVar9 != 0 &&
                        (uVar7 == *(uint *)(lVar9 + 0x34))))) ||
                ((lVar9 = *(long *)(lVar5 + 0x68), lVar9 != 0 && (uVar7 == *(uint *)(lVar9 + 0x34)))
                )))) ||
           ((lVar9 = *(long *)(lVar5 + 0x78), lVar9 != 0 && (uVar7 == *(uint *)(lVar9 + 0x34)))))
        goto LAB_0017602c;
        lVar5 = lVar5 + 0x80;
      } while (lVar1 != lVar5);
      goto LAB_00176020;
    }
    if (uVar3 == 1) {
LAB_00175f64:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        if (lVar1 == lVar5) goto LAB_00176020;
        goto LAB_00175f84;
      }
    }
    else if (uVar3 == 2) {
LAB_00175f4d:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175f64;
      }
    }
    else if (uVar3 == 3) {
LAB_00175f36:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175f4d;
      }
    }
    else if (uVar3 == 4) {
LAB_00175f1f:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175f36;
      }
    }
    else if (uVar3 == 5) {
LAB_00175f08:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175f1f;
      }
    }
    else if (uVar3 == 6) {
LAB_00175ef1:
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175f08;
      }
    }
    else {
      lVar9 = *(long *)(lVar5 + 8);
      if ((lVar9 == 0) || (uVar7 != *(uint *)(lVar9 + 0x34))) {
        lVar5 = lVar5 + 0x10;
        goto LAB_00175ef1;
      }
    }
  }
LAB_0017602c:
  fVar10 = (float)uVar8 * _DAT_00cb74f0;
  if (fVar10 <= 0.0) {
    fVar10 = 0.0;
  }
  else if (DAT_00cb6ad0 <= fVar10) {
    fVar10 = DAT_00cb6ad0;
  }
  *(float *)(lVar9 + 0x1c) = fVar10;
LAB_00176058:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SOUND_GROUP_STOP` @ 00176180
```c

undefined * jag::packethandlers::Audio::SOUND_GROUP_STOP(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  uint uVar5;
  ushort uVar6;
  uint uVar7;
  long lVar8;
  bool bVar9;
  
  lVar2 = *(long *)(param_2 + 0x18);
  bVar9 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar6 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar2);
  if (bVar9) {
    uVar6 = uVar6 << 8 | uVar6 >> 8;
  }
  lVar2 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if ((lVar2 != 0) && (uVar7 = *(uint *)(lVar2 + 0x28), uVar7 != 0)) {
    lVar4 = 0;
    lVar1 = (ulong)(uVar7 - 1) * 4 + 4;
    uVar7 = uVar7 & 3;
    uVar5 = (uint)uVar6;
    if (uVar7 != 0) {
      if (uVar7 != 1) {
        if (uVar7 != 2) {
          lVar4 = (long)**(int **)(lVar2 + 0x18) * 0xb0 + *(long *)(lVar2 + 0x10);
          if (((uVar5 == *(uint *)(lVar4 + 0x9c)) &&
              (*(undefined1 *)(lVar4 + 0x98) = 0, *(int *)(lVar4 + 0x94) == 0)) &&
             (*(int *)(lVar4 + 0x14) < 4)) {
            if ((*(int *)(lVar4 + 0x14) == 3) && (lVar8 = *(long *)(lVar4 + 0x28), lVar8 != 0)) {
              if ((*(int *)(lVar8 + 0x30) - 2U & 0xfffffffd) == 0) {
                if (*(int *)(lVar8 + 0x30) == 2) {
                  *(undefined4 *)(lVar8 + 0x24) = *(undefined4 *)(lVar8 + 0x20);
                }
                *(undefined4 *)(lVar8 + 0x30) = 3;
              }
              *(undefined4 *)(lVar4 + 0x14) = 4;
            }
            *(undefined1 *)(lVar4 + 0x10) = 1;
          }
          lVar4 = 4;
        }
        lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + lVar4) * 0xb0 + *(long *)(lVar2 + 0x10);
        if (((uVar5 == *(uint *)(lVar8 + 0x9c)) &&
            (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
           (*(int *)(lVar8 + 0x14) < 4)) {
          if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
            if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
              if (*(int *)(lVar3 + 0x30) == 2) {
                *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
              }
              *(undefined4 *)(lVar3 + 0x30) = 3;
            }
            *(undefined4 *)(lVar8 + 0x14) = 4;
          }
          *(undefined1 *)(lVar8 + 0x10) = 1;
        }
        lVar4 = lVar4 + 4;
      }
      lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + lVar4) * 0xb0 + *(long *)(lVar2 + 0x10);
      if (((uVar5 == *(uint *)(lVar8 + 0x9c)) &&
          (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
         (*(int *)(lVar8 + 0x14) < 4)) {
        if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
          if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
            if (*(int *)(lVar3 + 0x30) == 2) {
              *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
            }
            *(undefined4 *)(lVar3 + 0x30) = 3;
          }
          *(undefined4 *)(lVar8 + 0x14) = 4;
        }
        *(undefined1 *)(lVar8 + 0x10) = 1;
      }
      lVar4 = lVar4 + 4;
      if (lVar4 == lVar1) {
        return &DAT_015d3620;
      }
    }
    do {
      lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + lVar4) * 0xb0 + *(long *)(lVar2 + 0x10);
      if (((uVar5 == *(uint *)(lVar8 + 0x9c)) &&
          (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
         (*(int *)(lVar8 + 0x14) < 4)) {
        if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
          if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
            if (*(int *)(lVar3 + 0x30) == 2) {
              *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
            }
            *(undefined4 *)(lVar3 + 0x30) = 3;
          }
          *(undefined4 *)(lVar8 + 0x14) = 4;
        }
        *(undefined1 *)(lVar8 + 0x10) = 1;
      }
      lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + 4 + lVar4) * 0xb0 + *(long *)(lVar2 + 0x10);
      uVar7 = (uint)uVar6;
      if (((uVar7 == *(uint *)(lVar8 + 0x9c)) &&
          (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
         (*(int *)(lVar8 + 0x14) < 4)) {
        if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
          if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
            if (*(int *)(lVar3 + 0x30) == 2) {
              *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
            }
            *(undefined4 *)(lVar3 + 0x30) = 3;
          }
          *(undefined4 *)(lVar8 + 0x14) = 4;
        }
        *(undefined1 *)(lVar8 + 0x10) = 1;
      }
      lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + 4 + lVar4 + 4) * 0xb0 +
              *(long *)(lVar2 + 0x10);
      if (((uVar7 == *(uint *)(lVar8 + 0x9c)) &&
          (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
         (*(int *)(lVar8 + 0x14) < 4)) {
        if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
          if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
            if (*(int *)(lVar3 + 0x30) == 2) {
              *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
            }
            *(undefined4 *)(lVar3 + 0x30) = 3;
          }
          *(undefined4 *)(lVar8 + 0x14) = 4;
        }
        *(undefined1 *)(lVar8 + 0x10) = 1;
      }
      lVar8 = (long)*(int *)(*(long *)(lVar2 + 0x18) + 8 + lVar4 + 4) * 0xb0 +
              *(long *)(lVar2 + 0x10);
      if (((uVar7 == *(uint *)(lVar8 + 0x9c)) &&
          (*(undefined1 *)(lVar8 + 0x98) = 0, *(int *)(lVar8 + 0x94) == 0)) &&
         (*(int *)(lVar8 + 0x14) < 4)) {
        if ((*(int *)(lVar8 + 0x14) == 3) && (lVar3 = *(long *)(lVar8 + 0x28), lVar3 != 0)) {
          if ((*(int *)(lVar3 + 0x30) - 2U & 0xfffffffd) == 0) {
            if (*(int *)(lVar3 + 0x30) == 2) {
              *(undefined4 *)(lVar3 + 0x24) = *(undefined4 *)(lVar3 + 0x20);
            }
            *(undefined4 *)(lVar3 + 0x30) = 3;
          }
          *(undefined4 *)(lVar8 + 0x14) = 4;
        }
        *(undefined1 *)(lVar8 + 0x10) = 1;
      }
      lVar4 = lVar4 + 0x10;
    } while (lVar4 != lVar1);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SOUND_AREA_SYNTH` @ 00176780
```c

undefined * jag::packethandlers::Audio::SOUND_AREA_SYNTH(long *param_1,long param_2)

{
  long lVar1;
  undefined1 uVar2;
  long lVar3;
  long lVar4;
  undefined1 *puVar5;
  uint uVar6;
  ushort uVar7;
  
  lVar3 = *(long *)(param_2 + 0x18);
  lVar4 = *(long *)(param_2 + 0x10);
  lVar1 = lVar3 + 5;
  *(long *)(param_2 + 0x18) = lVar3 + 4;
  puVar5 = (undefined1 *)(lVar3 + 4 + lVar4);
  uVar6 = *(uint *)(lVar4 + lVar3);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar2 = *puVar5;
    uVar6 = uVar6 >> 0x18 | (uVar6 & 0xff0000) >> 8 | (uVar6 & 0xff00) << 8 | uVar6 << 0x18;
    *(long *)(param_2 + 0x18) = lVar3 + 7;
    uVar7 = *(ushort *)(lVar4 + lVar1);
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar2 = *puVar5;
    *(long *)(param_2 + 0x18) = lVar3 + 7;
    uVar7 = *(ushort *)(lVar4 + lVar1);
  }
  *(long *)(param_2 + 0x18) = lVar3 + 8;
  lVar1 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if (lVar1 != 0) {
    FUN_00b0d140(lVar1,DAT_00fb4043,uVar6,uVar2,*(undefined1 *)(lVar4 + 7 + lVar3),8,4,0,
                 &DAT_015c01c8,0xffffffffffffffff,0xff,uVar7);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::NPCInfo::NPC_SAY` @ 001795e0
```c

undefined * jag::packethandlers::NPCInfo::NPC_SAY(long *param_1,long param_2)

{
  byte bVar1;
  char cVar2;
  int iVar3;
  long lVar4;
  long *plVar5;
  long lVar6;
  ushort uVar7;
  undefined1 *puVar8;
  bool bVar9;
  undefined1 local_48;
  undefined7 uStack_47;
  long local_40;
  char local_31;
  
  puVar8 = &local_48;
  lVar6 = *(long *)(param_2 + 0x18);
  bVar9 = DAT_01050dc0 == 0x3020100;
  lVar4 = *(long *)(param_2 + 0x10);
  local_48 = 0;
  local_31 = '\x17';
  *(long *)(param_2 + 0x18) = lVar6 + 2;
  uVar7 = *(ushort *)(lVar4 + lVar6);
  if (bVar9) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar6 + 3;
  bVar1 = *(byte *)(lVar4 + 2 + lVar6);
  *(long *)(param_2 + 0x18) = lVar6 + 4;
  cVar2 = *(char *)(lVar4 + 3 + lVar6);
  FUN_00ad80e0(param_2,&local_48);
  lVar6 = NPCList::GetNPCNode(*(undefined8 *)((long)&__DT_RELA[0xcfb].r_addend + *param_1),uVar7);
  plVar5 = *(long **)(lVar6 + 8);
  if (plVar5 != (long *)0x0) {
    if (*(code **)(*plVar5 + 0x158) == FUN_006083c0) {
      iVar3 = *(int *)(*(long *)(plVar5[0xf] + 0x240) + 0xcc);
      *(uint *)((long)plVar5 + 0xf1c) = -(uint)bVar1 - 0x80 & 0xff;
      *(uint *)(plVar5 + 0x1e3) = (uint)(byte)(cVar2 + 0x80);
      plVar5[0x1e4] = (ulong)(uint)(iVar3 * 1000) + DAT_015bf228;
      if (local_31 < '\0') {
        puVar8 = (undefined1 *)CONCAT71(uStack_47,local_48);
      }
      else {
        local_40 = 0x17 - (long)local_31;
      }
      FUN_00495ef0(plVar5 + 0x1e0,puVar8,puVar8 + local_40);
    }
    else {
      (**(code **)(*plVar5 + 0x158))(plVar5,&local_48);
    }
  }
  if ((local_31 < '\0') && (CONCAT71(uStack_47,local_48) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SOUND_STOP` @ 0017e640
```c

undefined * jag::packethandlers::Audio::SOUND_STOP(long *param_1,long param_2)

{
  uint uVar1;
  int *piVar2;
  int iVar3;
  long lVar4;
  int *piVar5;
  ushort uVar6;
  long lVar7;
  int *piVar8;
  int *piVar9;
  int *piVar10;
  uint uVar11;
  bool bVar12;
  
  lVar4 = *(long *)(param_2 + 0x18);
  bVar12 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar4 + 2;
  uVar6 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar4);
  if (bVar12) {
    uVar6 = uVar6 << 8 | uVar6 >> 8;
  }
  lVar4 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if ((lVar4 != 0) && (*(int *)(lVar4 + 0x28) != 0)) {
    piVar5 = *(int **)(lVar4 + 0x18);
    uVar1 = *(int *)(lVar4 + 0x28) - 1;
    lVar4 = *(long *)(lVar4 + 0x10);
    piVar2 = piVar5 + 1;
    uVar11 = uVar1 & 3;
    piVar9 = piVar5;
    piVar10 = piVar2;
    if (uVar11 != 0) {
      lVar7 = (long)*piVar5 * 0xb0 + lVar4;
      if ((uint)uVar6 == *(uint *)(lVar7 + 0x9c)) {
        if (*(long *)(lVar7 + 0x28) == 0) {
          *(undefined4 *)(lVar7 + 0x14) = 6;
        }
        else {
          iVar3 = *(int *)(lVar7 + 0x14);
          if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
            *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
            *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
            *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
            *(undefined4 *)(lVar7 + 0x14) = 5;
          }
        }
        *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
      }
      piVar10 = piVar5 + 2;
      piVar9 = piVar2;
      if (uVar11 != 1) {
        piVar8 = piVar2;
        piVar9 = piVar10;
        if (uVar11 != 2) {
          lVar7 = (long)*piVar2 * 0xb0 + lVar4;
          if ((uint)uVar6 == *(uint *)(lVar7 + 0x9c)) {
            if (*(long *)(lVar7 + 0x28) == 0) {
              *(undefined4 *)(lVar7 + 0x14) = 6;
            }
            else {
              iVar3 = *(int *)(lVar7 + 0x14);
              if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
                *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
                *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
                *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
                *(undefined4 *)(lVar7 + 0x14) = 5;
              }
            }
            *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
          }
          piVar9 = piVar5 + 3;
          piVar8 = piVar10;
        }
        lVar7 = (long)*piVar8 * 0xb0 + lVar4;
        if ((uint)uVar6 == *(uint *)(lVar7 + 0x9c)) {
          if (*(long *)(lVar7 + 0x28) == 0) {
            *(undefined4 *)(lVar7 + 0x14) = 6;
          }
          else {
            iVar3 = *(int *)(lVar7 + 0x14);
            if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
              *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
              *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
              *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
              *(undefined4 *)(lVar7 + 0x14) = 5;
            }
          }
          *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
        }
        piVar10 = piVar9 + 1;
      }
    }
    while( true ) {
      lVar7 = (long)*piVar9 * 0xb0 + lVar4;
      if ((uint)uVar6 == *(uint *)(lVar7 + 0x9c)) {
        if (*(long *)(lVar7 + 0x28) == 0) {
          *(undefined4 *)(lVar7 + 0x14) = 6;
        }
        else {
          iVar3 = *(int *)(lVar7 + 0x14);
          if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
            *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
            *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
            *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
            *(undefined4 *)(lVar7 + 0x14) = 5;
          }
        }
        *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
      }
      if (piVar10 == piVar2 + uVar1) break;
      lVar7 = (long)*piVar10 * 0xb0 + lVar4;
      uVar11 = (uint)uVar6;
      if (uVar11 == *(uint *)(lVar7 + 0x9c)) {
        if (*(long *)(lVar7 + 0x28) == 0) {
          *(undefined4 *)(lVar7 + 0x14) = 6;
        }
        else {
          iVar3 = *(int *)(lVar7 + 0x14);
          if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
            *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
            *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
            *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
            *(undefined4 *)(lVar7 + 0x14) = 5;
          }
        }
        *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
      }
      lVar7 = (long)piVar10[1] * 0xb0 + lVar4;
      if (uVar11 == *(uint *)(lVar7 + 0x9c)) {
        if (*(long *)(lVar7 + 0x28) == 0) {
          *(undefined4 *)(lVar7 + 0x14) = 6;
        }
        else {
          iVar3 = *(int *)(lVar7 + 0x14);
          if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
            *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
            *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
            *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
            *(undefined4 *)(lVar7 + 0x14) = 5;
          }
        }
        *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
      }
      piVar9 = piVar10 + 3;
      lVar7 = (long)piVar10[2] * 0xb0 + lVar4;
      if (uVar11 == *(uint *)(lVar7 + 0x9c)) {
        if (*(long *)(lVar7 + 0x28) == 0) {
          *(undefined4 *)(lVar7 + 0x14) = 6;
        }
        else {
          iVar3 = *(int *)(lVar7 + 0x14);
          if (((iVar3 != 5) && (iVar3 != 1)) && (iVar3 != 7)) {
            *(undefined8 *)(lVar7 + 0x3c) = 0x3e19999a00000000;
            *(undefined4 *)(lVar7 + 0x38) = *(undefined4 *)(lVar7 + 0x34);
            *(undefined4 *)(lVar7 + 0x44) = 0x3e19999a;
            *(undefined4 *)(lVar7 + 0x14) = 5;
          }
        }
        *(undefined4 *)(lVar7 + 0x9c) = 0xffffffff;
      }
      piVar10 = piVar10 + 4;
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::NPCInfo::SET_NPC_OP_impl` @ 0017f7c0
```c

void jag::packethandlers::NPCInfo::SET_NPC_OP_impl(long param_1,long param_2)

{
  char cVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined8 uVar4;
  undefined4 *puVar5;
  long *plVar6;
  ulong uVar7;
  long lVar8;
  long *plVar9;
  long lVar10;
  uint uVar11;
  undefined4 local_58;
  undefined4 local_54;
  undefined8 local_50;
  ulong local_48;
  undefined8 uStack_40;
  undefined8 local_38;
  undefined1 local_30;
  
  uVar4 = *(undefined8 *)(param_1 + 0x28);
  uVar2 = *(undefined4 *)(param_1 + 0x20);
  uVar3 = *(undefined4 *)(param_1 + 0x24);
  local_48 = local_48 & 0xffffffffffffff00;
  local_38 = CONCAT17(0x17,(undefined7)local_38);
  if (*(char *)(param_1 + 0x1f) < '\0') {
    lVar10 = *(long *)(param_1 + 8);
    lVar8 = *(long *)(param_1 + 0x10) + lVar10;
  }
  else {
    lVar10 = param_1 + 8;
    lVar8 = (lVar10 - *(char *)(param_1 + 0x1f)) + 0x17;
  }
  FUN_00493d20(&local_48,lVar10,lVar8);
  puVar5 = *(undefined4 **)(param_2 + 8);
  local_30 = 0;
  local_58 = uVar3;
  local_54 = uVar2;
  local_50 = uVar4;
  if (puVar5 < *(undefined4 **)(param_2 + 0x10)) {
    *(undefined4 **)(param_2 + 8) = puVar5 + 0xc;
    *(undefined1 *)(puVar5 + 4) = 0;
    *(undefined1 *)((long)puVar5 + 0x27) = 0x17;
    *puVar5 = uVar3;
    puVar5[1] = uVar2;
    *(undefined8 *)(puVar5 + 2) = uVar4;
    lVar10 = *(long *)(puVar5 + 8);
    uVar7 = *(ulong *)(puVar5 + 4);
    uVar4 = *(undefined8 *)(puVar5 + 6);
    *(ulong *)(puVar5 + 4) = local_48;
    *(undefined8 *)(puVar5 + 6) = uStack_40;
    *(long *)(puVar5 + 8) = local_38;
    *(undefined1 *)(puVar5 + 10) = 0;
    local_48 = uVar7;
    uStack_40 = uVar4;
    local_38 = lVar10;
  }
  else {
    FUN_00178280(param_2,&local_58);
  }
  plVar6 = *(long **)(param_2 + 0x20);
  plVar9 = *(long **)(param_2 + 0x18);
  if (plVar6 == plVar9) {
LAB_0017f9ba:
    *(long **)(param_2 + 0x20) = plVar9;
    if ((local_38 < 0) && (local_48 != 0)) {
      HeapInterface::Free();
      return;
    }
    return;
  }
  uVar11 = (int)((ulong)((long)plVar6 + (-0x20 - (long)plVar9)) >> 5) + 1U & 7;
  if (uVar11 == 0) goto LAB_0017f942;
  if (uVar11 != 1) {
    if (uVar11 != 2) {
      if (uVar11 != 3) {
        if (uVar11 != 4) {
          if (uVar11 != 5) {
            if (uVar11 != 6) {
              if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
                HeapInterface::Free();
              }
              plVar9 = plVar9 + 4;
            }
            if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
              HeapInterface::Free();
            }
            plVar9 = plVar9 + 4;
          }
          if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
            HeapInterface::Free();
          }
          plVar9 = plVar9 + 4;
        }
        if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
          HeapInterface::Free();
        }
        plVar9 = plVar9 + 4;
      }
      if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
        HeapInterface::Free();
      }
      plVar9 = plVar9 + 4;
    }
    if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
      HeapInterface::Free();
    }
    plVar9 = plVar9 + 4;
  }
  if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
    HeapInterface::Free();
  }
  plVar9 = plVar9 + 4;
joined_r0x0017f940:
  if (plVar6 != plVar9) {
LAB_0017f942:
    do {
      if ((*(char *)((long)plVar9 + 0x17) < '\0') && (*plVar9 != 0)) {
        HeapInterface::Free();
        if (-1 < *(char *)((long)plVar9 + 0x37)) goto LAB_0017f95c;
LAB_0017fb40:
        if (plVar9[4] == 0) goto LAB_0017f95c;
        HeapInterface::Free();
        if (*(char *)((long)plVar9 + 0x57) < '\0') goto LAB_0017fb10;
LAB_0017f968:
        if (-1 < *(char *)((long)plVar9 + 0x77)) goto LAB_0017f974;
LAB_0017fae0:
        if (plVar9[0xc] == 0) goto LAB_0017f974;
        HeapInterface::Free();
        if (*(char *)((long)plVar9 + 0x97) < '\0') goto LAB_0017fab0;
LAB_0017f980:
        if (-1 < *(char *)((long)plVar9 + 0xb7)) goto LAB_0017f98f;
LAB_0017fa80:
        if (plVar9[0x14] == 0) goto LAB_0017f98f;
        HeapInterface::Free();
        if (*(char *)((long)plVar9 + 0xd7) < '\0') goto LAB_0017fa50;
LAB_0017f99e:
        cVar1 = *(char *)((long)plVar9 + 0xf7);
      }
      else {
        if (*(char *)((long)plVar9 + 0x37) < '\0') goto LAB_0017fb40;
LAB_0017f95c:
        if (-1 < *(char *)((long)plVar9 + 0x57)) goto LAB_0017f968;
LAB_0017fb10:
        if (plVar9[8] == 0) goto LAB_0017f968;
        HeapInterface::Free();
        if (*(char *)((long)plVar9 + 0x77) < '\0') goto LAB_0017fae0;
LAB_0017f974:
        if (-1 < *(char *)((long)plVar9 + 0x97)) goto LAB_0017f980;
LAB_0017fab0:
        if (plVar9[0x10] == 0) goto LAB_0017f980;
        HeapInterface::Free();
        if (*(char *)((long)plVar9 + 0xb7) < '\0') goto LAB_0017fa80;
LAB_0017f98f:
        if (-1 < *(char *)((long)plVar9 + 0xd7)) goto LAB_0017f99e;
LAB_0017fa50:
        if (plVar9[0x18] == 0) goto LAB_0017f99e;
        HeapInterface::Free();
        cVar1 = *(char *)((long)plVar9 + 0xf7);
      }
      if ((cVar1 < '\0') && (plVar9[0x1c] != 0)) goto code_r0x0017fa31;
      plVar9 = plVar9 + 0x20;
      if (plVar6 == plVar9) break;
    } while( true );
  }
  plVar9 = *(long **)(param_2 + 0x18);
  goto LAB_0017f9ba;
code_r0x0017fa31:
  plVar9 = plVar9 + 0x20;
  HeapInterface::Free();
  goto joined_r0x0017f940;
}


```

## `jag::packethandlers::NPCInfo::SET_NPC_OP` @ 0017ff30
```c

undefined * jag::packethandlers::NPCInfo::SET_NPC_OP(long *param_1,undefined8 param_2,int *param_3)

{
  char cVar1;
  int iVar2;
  long lVar3;
  ushort uVar4;
  char *pcVar5;
  char *pcVar6;
  uint uVar7;
  char local_48;
  undefined7 uStack_47;
  char local_31;
  
  pcVar6 = &local_48;
  iVar2 = *param_3;
  if (iVar2 < 3) {
    local_48 = '\0';
    local_31 = '\x17';
    pcVar5 = (&PTR_s_Walk_here_013641a0)[**(int **)((long)&__DT_RELA[0xd0f].r_addend + *param_1)];
    cVar1 = *pcVar5;
    while (cVar1 != '\0') {
      pcVar5 = pcVar5 + 1;
      cVar1 = *pcVar5;
    }
    FUN_0048d1a0(&local_48);
    if (0 < iVar2) goto LAB_00180017;
  }
  else {
    local_48 = '\0';
    local_31 = '\x17';
    FUN_00ad80e0(param_2,&local_48);
LAB_00180017:
    uVar4 = FUN_00121a30(param_2);
    uVar7 = (uint)uVar4;
    if (uVar7 != 0xffff) goto LAB_0017ff9e;
  }
  uVar7 = 0xffffffff;
LAB_0017ff9e:
  lVar3 = *(long *)((long)&__DT_RELA[0xcfb].r_info + *param_1);
  if (local_31 < '\0') {
    pcVar6 = (char *)CONCAT71(uStack_47,local_48);
  }
  cVar1 = *pcVar6;
  while (cVar1 != '\0') {
    pcVar6 = pcVar6 + 1;
    cVar1 = *pcVar6;
  }
  FUN_00493d20(lVar3 + 0x1968);
  *(uint *)((long)&__DT_SYMTAB[0xef].st_name + lVar3) = uVar7;
  if ((local_31 < '\0') && (CONCAT71(uStack_47,local_48) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SOUND_GROUP_SPEED` @ 00182810
```c

undefined * jag::packethandlers::Audio::SOUND_GROUP_SPEED(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  code *pcVar3;
  uint uVar4;
  ushort *puVar5;
  long lVar6;
  long lVar7;
  int iVar8;
  ushort uVar9;
  uint uVar10;
  
  lVar2 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar2 + 4;
  uVar10 = *(uint *)(*(long *)(param_2 + 0x10) + lVar2);
  puVar5 = (ushort *)(*(long *)(param_2 + 0x10) + lVar2 + 4);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar2 + 6;
    uVar9 = *puVar5;
    uVar10 = uVar10 >> 0x18 | (uVar10 & 0xff0000) >> 8 | (uVar10 & 0xff00) << 8 | uVar10 << 0x18;
    uVar9 = uVar9 << 8 | uVar9 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar2 + 6;
    uVar9 = *puVar5;
  }
  lVar2 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if ((lVar2 != 0) && (*(int *)(lVar2 + 0x28) != 0)) {
    uVar4 = *(int *)(lVar2 + 0x28) - 1;
    iVar8 = uVar9 - 2;
    if (iVar8 == -2) {
      lVar6 = 0;
      lVar1 = (ulong)uVar4 * 4 + 4;
      if ((uVar4 & 1) == 0) {
        lVar6 = (long)**(int **)(lVar2 + 0x18) * 0xb0 + *(long *)(lVar2 + 0x10);
        pcVar3 = *(code **)(**(long **)(lVar6 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar6 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if ((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar6 + 0xa4) - 9U))) {
          FUN_00c2ac80(lVar6);
        }
        lVar6 = 4;
        if (lVar1 == 4) goto LAB_001828de;
      }
      do {
        lVar7 = (long)*(int *)(*(long *)(lVar2 + 0x18) + lVar6) * 0xb0 + *(long *)(lVar2 + 0x10);
        pcVar3 = *(code **)(**(long **)(lVar7 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar7 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if ((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar7 + 0xa4) - 9U))) {
          FUN_00c2ac80(lVar7);
        }
        lVar7 = (long)*(int *)(*(long *)(lVar2 + 0x18) + 4 + lVar6) * 0xb0 + *(long *)(lVar2 + 0x10)
        ;
        pcVar3 = *(code **)(**(long **)(lVar7 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar7 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if ((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar7 + 0xa4) - 9U))) {
          FUN_00c2ac80(lVar7);
        }
        lVar6 = lVar6 + 8;
      } while (lVar1 != lVar6);
    }
    else {
      lVar6 = 0;
      lVar1 = (ulong)uVar4 * 4 + 4;
      if ((uVar4 & 1) == 0) {
        lVar6 = (long)**(int **)(lVar2 + 0x18) * 0xb0 + *(long *)(lVar2 + 0x10);
        pcVar3 = *(code **)(**(long **)(lVar6 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar6 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if (((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar6 + 0xa4) - 9U))) &&
           (iVar8 == *(int *)(lVar6 + 0x9c))) {
          FUN_00c2ac80(lVar6);
        }
        lVar6 = 4;
        if (lVar1 == 4) goto LAB_001828de;
      }
      do {
        lVar7 = (long)*(int *)(*(long *)(lVar2 + 0x18) + lVar6) * 0xb0 + *(long *)(lVar2 + 0x10);
        pcVar3 = *(code **)(**(long **)(lVar7 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar7 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if (((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar7 + 0xa4) - 9U))) &&
           (iVar8 == *(int *)(lVar7 + 0x9c))) {
          FUN_00c2ac80(lVar7);
        }
        lVar7 = (long)*(int *)(*(long *)(lVar2 + 0x18) + 4 + lVar6) * 0xb0 + *(long *)(lVar2 + 0x10)
        ;
        pcVar3 = *(code **)(**(long **)(lVar7 + 0x20) + 0x40);
        if (pcVar3 == FUN_006b7450) {
          uVar4 = *(uint *)(*(long **)(lVar7 + 0x20) + 10);
        }
        else {
          uVar4 = (*pcVar3)();
        }
        if (((uVar4 == uVar10) && (1 < (byte)(*(char *)(lVar7 + 0xa4) - 9U))) &&
           (iVar8 == *(int *)(lVar7 + 0x9c))) {
          FUN_00c2ac80(lVar7);
        }
        lVar6 = lVar6 + 8;
      } while (lVar1 != lVar6);
    }
  }
LAB_001828de:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::CLANCHANNEL_FULL_CHAT` @ 00183ca0
```c

undefined *
jag::packethandlers::Chat::CLANCHANNEL_FULL_CHAT(long *param_1,long param_2,int *param_3)

{
  long *plVar1;
  char cVar2;
  int iVar3;
  long lVar4;
  long lVar5;
  long lVar6;
  long lVar7;
  code *pcVar8;
  undefined8 *puVar9;
  undefined8 *puVar10;
  
  iVar3 = *param_3;
  lVar4 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  lVar6 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *param_1);
  *(undefined4 *)(lVar6 + 0x130) = *(undefined4 *)(*(long *)(lVar6 + 0x128) + 0x10);
  *(long *)(param_2 + 0x18) = lVar4 + 1;
  cVar2 = *(char *)(lVar5 + lVar4);
  if (iVar3 == 1) {
    if (cVar2 < '\0') {
      puVar9 = *(undefined8 **)(lVar7 + 0x50);
      *(undefined8 *)(lVar7 + 0x58) = 0;
      *(undefined8 *)(lVar7 + 0x50) = 0;
    }
    else {
      if ('\x01' < cVar2) goto LAB_00183d54;
      puVar10 = (undefined8 *)(lVar7 + 0x20 + (long)cVar2 * 0x10);
      puVar9 = (undefined8 *)*puVar10;
      puVar10[1] = 0;
      *puVar10 = 0;
    }
    if (puVar9 == (undefined8 *)0x0) goto LAB_00183d54;
  }
  else {
    if (cVar2 < '\0') {
      puVar10 = (undefined8 *)FUN_00c29430(0x80);
      if (puVar10 != (undefined8 *)0x0) {
        puVar9 = puVar10 + 4;
        FUN_00182ee0(puVar9,param_2,0xffffffff);
        puVar10[1] = 0x100000001;
        puVar10[2] = 1;
        *puVar10 = &PTR_FUN_01363ac0;
        puVar10[3] = puVar9;
        if (*(char *)((long)puVar10 + 0x7c) == -1) {
          LOCK();
          *(int *)(puVar10 + 1) = *(int *)(puVar10 + 1) + 1;
          UNLOCK();
          lVar4 = *(long *)(lVar7 + 0x50);
          *(undefined8 **)(lVar7 + 0x58) = puVar9;
          *(undefined8 **)(lVar7 + 0x50) = puVar10;
          if (lVar4 != 0) {
            ref_counter_base::DecRef();
          }
        }
        ref_counter_base::DecRef(puVar10);
        return &DAT_015d3620;
      }
LAB_00183de0:
                    /* WARNING: Does not return */
      pcVar8 = (code *)invalidInstructionException();
      (*pcVar8)();
    }
    puVar9 = (undefined8 *)FUN_00c29430(0x80);
    if (puVar9 == (undefined8 *)0x0) goto LAB_00183de0;
    puVar10 = puVar9 + 4;
    FUN_00182ee0(puVar10,param_2,(int)cVar2);
    *puVar9 = &PTR_FUN_01363ac0;
    puVar9[1] = 0x100000001;
    puVar9[2] = 1;
    puVar9[3] = puVar10;
    if (*(byte *)((long)puVar9 + 0x7c) < 2) {
      plVar1 = (long *)(lVar7 + 0x20 + (long)(char)*(byte *)((long)puVar9 + 0x7c) * 0x10);
      LOCK();
      *(int *)(puVar9 + 1) = *(int *)(puVar9 + 1) + 1;
      UNLOCK();
      lVar4 = *plVar1;
      plVar1[1] = (long)puVar10;
      *plVar1 = (long)puVar9;
      if (lVar4 != 0) {
        ref_counter_base::DecRef();
      }
      if (*(long *)(lVar7 + 0x70) != 0) {
        (**(code **)(lVar7 + 0x78))(lVar7 + 0x60);
      }
    }
  }
  ref_counter_base::DecRef(puVar9);
LAB_00183d54:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::NPCInfo::NPC_HEADICON_SPECIFIC` @ 00185db0
```c

undefined * jag::packethandlers::NPCInfo::NPC_HEADICON_SPECIFIC(long *param_1,long param_2)

{
  int *piVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  ushort *puVar5;
  uint uVar6;
  ushort uVar7;
  uint uVar8;
  long lVar9;
  ushort uVar10;
  uint uVar11;
  ushort uVar12;
  uint uVar13;
  uint uVar14;
  
  lVar2 = *(long *)(param_2 + 0x18);
  lVar3 = *(long *)(param_2 + 0x10);
  lVar9 = lVar2 + 4;
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar10 = *(ushort *)(lVar3 + lVar2);
  puVar5 = (ushort *)(lVar3 + lVar2 + 2);
  lVar4 = lVar2 + 6;
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar9;
    uVar7 = *puVar5;
    uVar10 = uVar10 << 8 | uVar10 >> 8;
    *(long *)(param_2 + 0x18) = lVar4;
    uVar12 = *(ushort *)(lVar3 + lVar9);
    uVar7 = uVar7 << 8 | uVar7 >> 8;
    uVar12 = uVar12 << 8 | uVar12 >> 8;
    uVar8 = (uint)uVar7;
    uVar13 = (uint)uVar12;
    if (uVar12 <= uVar7) {
LAB_00185ffc:
      lVar9 = *param_1;
      goto LAB_00185f64;
    }
  }
  else {
    *(long *)(param_2 + 0x18) = lVar9;
    uVar8 = (uint)*puVar5;
    *(long *)(param_2 + 0x18) = lVar4;
    uVar13 = (uint)*(ushort *)(lVar3 + lVar9);
    if (uVar13 <= uVar8) goto LAB_00185ffc;
  }
  uVar11 = (uint)uVar10 << 0x10;
  lVar9 = *param_1;
  uVar14 = ~(uVar8 - uVar13) & 3;
  uVar6 = uVar8;
  if (uVar14 != 0) {
    lVar4 = lVar2 + 9;
    *(long *)(param_2 + 0x18) = lVar4;
    if ((uVar8 | uVar11) != 0xffffffff) {
      game::InterfaceList::GetInterface
                (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,uVar10,0);
      lVar9 = *param_1;
      lVar4 = *(long *)(param_2 + 0x18);
    }
    uVar6 = uVar8 + 1;
    if (uVar14 != 1) {
      if (uVar14 != 2) {
        lVar4 = lVar4 + 3;
        uVar14 = uVar6 | uVar11;
        *(long *)(param_2 + 0x18) = lVar4;
        if (uVar14 == 0xffffffff) {
          uVar6 = uVar8 + 2;
        }
        else {
          uVar6 = uVar8 + 2;
          game::InterfaceList::GetInterface
                    (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,uVar14 >> 0x10,0);
          lVar9 = *param_1;
          lVar4 = *(long *)(param_2 + 0x18);
        }
      }
      lVar4 = lVar4 + 3;
      *(long *)(param_2 + 0x18) = lVar4;
      if ((uVar6 | uVar11) != 0xffffffff) {
        game::InterfaceList::GetInterface
                  (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,
                   (uVar6 | uVar11) >> 0x10,0);
        lVar9 = *param_1;
        lVar4 = *(long *)(param_2 + 0x18);
      }
      uVar6 = uVar6 + 1;
    }
  }
  while( true ) {
    *(long *)(param_2 + 0x18) = lVar4 + 3;
    if ((uVar6 | uVar11) != 0xffffffff) {
      game::InterfaceList::GetInterface
                (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,(uVar6 | uVar11) >> 0x10
                 ,0);
      lVar9 = *param_1;
    }
    if (uVar13 == uVar6 + 1) break;
    *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 3;
    uVar8 = uVar6 + 1 | uVar11;
    if (uVar8 != 0xffffffff) {
      game::InterfaceList::GetInterface
                (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,uVar8 >> 0x10,0);
      lVar9 = *param_1;
    }
    *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 3;
    uVar8 = uVar6 + 2 | uVar11;
    if (uVar8 != 0xffffffff) {
      game::InterfaceList::GetInterface
                (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,uVar8 >> 0x10,0);
      lVar9 = *param_1;
    }
    *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 3;
    uVar8 = uVar6 + 3 | uVar11;
    if (uVar8 != 0xffffffff) {
      game::InterfaceList::GetInterface
                (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9) + 0x30,uVar8 >> 0x10,0);
      lVar9 = *param_1;
    }
    lVar4 = *(long *)(param_2 + 0x18);
    uVar6 = uVar6 + 4;
  }
LAB_00185f64:
  lVar9 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar9);
  piVar1 = (int *)(lVar9 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar9 + 0x14) = 1;
  return &DAT_015d3620;
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

## `jag::packethandlers::Camera::CAM_SMOOTHRESET` @ 00186f10
```c

undefined * jag::packethandlers::Camera::CAM_SMOOTHRESET(long *param_1)

{
  int *piVar1;
  long lVar2;
  
  game::Camera::ProcessCameraReset
            (*(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + *param_1),
             *(undefined1 *)
              (*(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1) + 0x240) + 0xd8));
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  piVar1 = (int *)(lVar2 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Camera::CAM_FORCEANGLE` @ 00186f60
```c

undefined * jag::packethandlers::Camera::CAM_FORCEANGLE(long *param_1,long param_2)

{
  long lVar1;
  undefined8 uVar2;
  
  lVar1 = *(long *)(param_2 + 0x18);
  uVar2 = *(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar1 + 1;
  game::Camera::ProcessCameraReset(uVar2,*(char *)(*(long *)(param_2 + 0x10) + lVar1) == '\x01');
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Camera::CAM_RESET` @ 00186fa0
```c

undefined * jag::packethandlers::Camera::CAM_RESET(long *param_1)

{
  int *piVar1;
  long lVar2;
  char cVar3;
  undefined *puVar4;
  
  cVar3 = FUN_00144050(*(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + *param_1));
  puVar4 = &DAT_015d3600;
  if (cVar3 != '\0') {
    puVar4 = &DAT_015d3620;
    lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
    piVar1 = (int *)(lVar2 + 0x10);
    *piVar1 = *piVar1 + 1;
    *(undefined1 *)(lVar2 + 0x14) = 1;
  }
  return puVar4;
}


```

## `jag::packethandlers::Audio::SOUND_AREA_SYNTH_2` @ 00187220
```c

/* Setting prototype: undefined * SOUND_AREA_SYNTH_2(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Audio::SOUND_AREA_SYNTH_2(long *thisPtr,long packetPtr)

{
  long lVar1;
  long *plVar2;
  uint uVar3;
  
  lVar1 = *(long *)((long)&__DT_RELA[0xd06].r_info + *thisPtr);
  if (lVar1 != 0) {
    uVar3 = Packet::gT_unsigned_int((PacketCore *)packetPtr);
    plVar2 = *(long **)((long)&__DT_SYMTAB[0x197].st_size + lVar1);
    (**(code **)(*plVar2 + 0x38))
              (plVar2,*(undefined8 *)((long)&__DT_SYMTAB[0x198].st_value + lVar1),uVar3,0,2,0);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::VORBIS_PRELOAD` @ 00187270
```c

/* Setting prototype: undefined * VORBIS_PRELOAD(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Audio::VORBIS_PRELOAD(long *thisPtr,long packetPtr)

{
  code *pcVar1;
  int iVar2;
  long lVar3;
  
  iVar2 = Packet::g4_alt2(packetPtr);
  lVar3 = *(long *)((long)&__DT_RELA[0xd06].r_info + *thisPtr);
  if (((lVar3 != 0) && (*(int *)((long)&__DT_SYMTAB[0x19e].st_size + lVar3) == 1)) && (-1 < iVar2))
  {
    lVar3 = FUN_006d0fe0(lVar3,iVar2,1);
    if ((*(long **)(lVar3 + 8) != (long *)0x0) &&
       (pcVar1 = *(code **)(**(long **)(lVar3 + 8) + 0x10), pcVar1 != FUN_0065ae90)) {
      (*pcVar1)();
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::MIDI_SONG` @ 00187330
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: undefined * MIDI_SONG(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Audio::MIDI_SONG(long *thisPtr,long packetPtr)

{
  char cVar1;
  long lVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  undefined4 uVar6;
  int iVar7;
  long lVar8;
  
  lVar2 = *(long *)(packetPtr + 0x18);
  *(long *)(packetPtr + 0x18) = lVar2 + 1;
  cVar1 = *(char *)(*(long *)(packetPtr + 0x10) + lVar2);
  iVar7 = Packet::g4_alt2(packetPtr);
  lVar2 = *(long *)((long)&__DT_RELA[0xd06].r_info + *thisPtr);
  if (lVar2 != 0) {
    if (iVar7 < 0) {
LAB_00187470:
      FUN_00c2acb0(lVar2,0);
      return &DAT_015d3620;
    }
    if (iVar7 != *(int *)((long)&__DT_SYMTAB[0x199].st_value + lVar2)) {
      lVar8 = FUN_00b0cd30(lVar2,lVar2,DAT_00fb4045,iVar7,0,-cVar1,7,4,0,0,&DAT_015c01c8,
                           0xffffffffffffffff,0x100,1);
      if (lVar8 == 0) goto LAB_00187470;
      if (*(int *)((long)&__DT_SYMTAB[0x199].st_value + lVar2) < 0) {
        if ((*(int *)(lVar8 + 0x94) < 1) && (*(char *)(lVar8 + 0x98) == '\0')) {
          FUN_007f99c0(lVar8);
        }
      }
      else {
        FUN_00c2acb0(lVar2,0);
        uVar6 = _UNK_00cb754c;
        uVar5 = _UNK_00cb7548;
        uVar4 = _UNK_00cb7544;
        uVar3 = _DAT_00cb7540;
        *(undefined4 *)(lVar8 + 0x44) = 0x3fc00000;
        *(undefined4 *)(lVar8 + 0x94) = 0x19;
        *(undefined4 *)(lVar8 + 0x34) = uVar3;
        *(undefined4 *)(lVar8 + 0x38) = uVar4;
        *(undefined4 *)(lVar8 + 0x3c) = uVar5;
        *(undefined4 *)(lVar8 + 0x40) = uVar6;
        *(undefined1 *)(lVar8 + 0x98) = 0;
      }
      cVar1 = *(char *)((long)&__DT_SYMTAB[0x199].st_size + lVar2 + 4);
      *(int *)((long)&__DT_SYMTAB[0x199].st_value + lVar2) = iVar7;
      *(long *)((long)&__DT_SYMTAB[0x19a].st_name + lVar2) = lVar8;
      if (cVar1 != '\0') {
        lVar2 = *(long *)(lVar8 + 0x28);
        if (lVar2 != 0) {
          FUN_0047e830(&DAT_015a65c0);
          if (*(int *)(lVar2 + 0x30) == 3) {
            *(undefined1 *)(lVar2 + 0x138) = 1;
          }
          if (PTR___pthread_key_create_01390dc0 != (undefined *)0x0) {
            pthread_mutex_unlock((pthread_mutex_t *)&DAT_015a65c0);
          }
        }
        *(undefined1 *)(lVar8 + 0xa6) = 1;
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::SYNTH_SOUND` @ 001874d0
```c

undefined * jag::packethandlers::Audio::SYNTH_SOUND(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  undefined1 uVar4;
  undefined1 uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  uint uVar9;
  ushort uVar10;
  undefined1 *puVar11;
  ushort uVar12;
  ushort uVar13;
  
  lVar6 = *(long *)(param_2 + 0x18);
  lVar7 = *(long *)(param_2 + 0x10);
  lVar8 = lVar6 + 5;
  lVar1 = lVar6 + 7;
  lVar2 = lVar6 + 10;
  lVar3 = lVar6 + 8;
  *(long *)(param_2 + 0x18) = lVar6 + 4;
  uVar9 = *(uint *)(lVar7 + lVar6);
  puVar11 = (undefined1 *)(lVar6 + 4 + lVar7);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar8;
    uVar4 = *puVar11;
    uVar9 = uVar9 >> 0x18 | (uVar9 & 0xff0000) >> 8 | (uVar9 & 0xff00) << 8 | uVar9 << 0x18;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar12 = *(ushort *)(lVar7 + lVar8);
    *(long *)(param_2 + 0x18) = lVar3;
    uVar12 = uVar12 << 8 | uVar12 >> 8;
    uVar5 = *(undefined1 *)(lVar7 + lVar1);
    *(long *)(param_2 + 0x18) = lVar2;
    uVar13 = *(ushort *)(lVar7 + lVar3);
    *(long *)(param_2 + 0x18) = lVar6 + 0xc;
    uVar10 = *(ushort *)(lVar7 + lVar2);
    uVar13 = uVar13 << 8 | uVar13 >> 8;
    uVar10 = uVar10 << 8 | uVar10 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar8;
    uVar4 = *puVar11;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar12 = *(ushort *)(lVar7 + lVar8);
    *(long *)(param_2 + 0x18) = lVar3;
    uVar5 = *(undefined1 *)(lVar7 + lVar1);
    *(long *)(param_2 + 0x18) = lVar2;
    uVar13 = *(ushort *)(lVar7 + lVar3);
    *(long *)(param_2 + 0x18) = lVar6 + 0xc;
    uVar10 = *(ushort *)(lVar7 + lVar2);
  }
  lVar8 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if (lVar8 != 0) {
    lVar8 = FUN_00b0cd30(lVar8,DAT_015c7b40,DAT_00fb4043,uVar9,uVar4,uVar5,6,4,0,0,&DAT_015c01c8,
                         0xffffffffffffffff,uVar13,0);
    if (lVar8 != 0) {
      *(uint *)(lVar8 + 0x94) = (uint)uVar12;
      *(undefined1 *)(lVar8 + 0x98) = 1;
      *(uint *)(lVar8 + 0x9c) = (uint)uVar10;
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Audio::VORBIS_SONG` @ 001878d0
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::Audio::VORBIS_SONG(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  long *plVar5;
  ushort *puVar6;
  ushort uVar7;
  ushort uVar8;
  ushort uVar9;
  undefined1 auVar10 [16];
  undefined1 auStack_28 [16];
  code *local_18;
  
  lVar2 = *(long *)(param_2 + 0x18);
  lVar3 = *(long *)(param_2 + 0x10);
  lVar1 = lVar2 + 4;
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar8 = *(ushort *)(lVar3 + lVar2);
  puVar6 = (ushort *)(lVar3 + lVar2 + 2);
  if (DAT_01050dc0 == 0x3020100) {
    lVar4 = *param_1;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar8 = uVar8 << 8 | uVar8 >> 8;
    uVar9 = *puVar6;
    *(long *)(param_2 + 0x18) = lVar2 + 6;
    uVar7 = *(ushort *)(lVar3 + lVar1);
    plVar5 = *(long **)((long)&__DT_RELA[0xd06].r_info + lVar4);
    uVar9 = uVar9 << 8 | uVar9 >> 8;
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  else {
    lVar4 = *param_1;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar9 = *puVar6;
    *(long *)(param_2 + 0x18) = lVar2 + 6;
    uVar7 = *(ushort *)(lVar3 + lVar1);
    plVar5 = *(long **)((long)&__DT_RELA[0xd06].r_info + lVar4);
  }
  if (plVar5 != (long *)0x0) {
    local_18 = (code *)0x0;
    lVar1 = *(long *)(*(long *)(*plVar5 + 8) + 8);
    if ((lVar1 != 0) &&
       (auVar10._4_12_ = SUB1612((undefined1  [16])0x0,4),
       auVar10._0_4_ = (float)uVar7 * _DAT_00cb74f0,
       FUN_00c2ad90(auVar10._0_8_,lVar1,uVar8,*(undefined4 *)(*(long *)(*plVar5 + 8) + 0x14),uVar9,
                    auStack_28), local_18 != (code *)0x0)) {
      (*local_18)(auStack_28,auStack_28,3);
    }
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Lobby::CHANGE_LOBBY` @ 00196170
```c

/* WARNING: Type propagation algorithm not settling */

undefined * jag::packethandlers::Lobby::CHANGE_LOBBY(long *param_1,long param_2,int *param_3)

{
  byte *pbVar1;
  byte bVar2;
  int iVar3;
  long lVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  undefined8 uVar8;
  char cVar9;
  size_t sVar10;
  undefined8 *puVar11;
  uint uVar12;
  undefined8 extraout_RDX;
  undefined8 extraout_RDX_00;
  uint uVar13;
  long lVar14;
  char *pcVar15;
  long lVar16;
  int iVar17;
  char *pcVar18;
  char *******pppppppcVar19;
  char *******pppppppcVar20;
  char *pcVar21;
  undefined8 *puVar22;
  undefined8 *puVar23;
  undefined8 *puVar24;
  undefined8 *puVar25;
  int iVar26;
  ulong uVar27;
  long *plVar28;
  long *plVar29;
  byte *pbVar30;
  long *plVar31;
  long *plVar32;
  byte bVar33;
  long *plVar34;
  qword *local_138;
  undefined1 *local_120;
  char *******local_108;
  char *******pppppppcStack_100;
  undefined8 local_f8;
  char *******local_e8;
  char *******pppppppcStack_e0;
  undefined8 local_d8;
  char *******local_c8;
  char *******pppppppcStack_c0;
  undefined8 local_b8;
  char *******local_a8;
  char *******pppppppcStack_a0;
  undefined8 local_98;
  char *******local_88;
  char *******pppppppcStack_80;
  undefined8 local_78;
  code *apcStack_70 [2];
  char local_59;
  undefined1 local_58;
  undefined7 uStack_57;
  char local_41;
  byte local_40;
  
                    /* op49 handler jag::packethandlers::Lobby::CHANGE_LOBBY(client,packet,end).
                       Parses a repeating record of THREE NUL-terminated CP1252 strings + a flag
                       byte (bit1) per entry until packet end, building/replacing a 0x50-stride
                       vector at (client __DT_RELA[0xcfe])+0x30 (the lobby display list), then
                       snapshots a frame cycle (RELA[0xd00]+0xf0) and runs a UI refresh
                       (FUN_00143a20). Does NOT call SetMainState and does NOT touch the
                       WorldSwitcher target/WorldTarget - it is lobby UI data only, NOT a
                       world-connect trigger. Confirms: the cold-lobby world connect target is
                       delivered in the lobby LOGIN RESPONSE (see LoginStepHandleLoginData /
                       CommitWorldTargetFromLogin), not via op49. [VERIFIED rs2client.948-5
                       @0x00196170] */
  iVar3 = *param_3;
  lVar14 = *(long *)(param_2 + 0x18);
  lVar16 = *param_1;
  lVar4 = *(long *)((long)&__DT_RELA[0xcfe].r_info + lVar16);
  if ((int)lVar14 < iVar3) {
    do {
      lVar16 = *(long *)(param_2 + 0x10);
      *(long *)(param_2 + 0x18) = lVar14 + 1;
      bVar2 = *(byte *)(lVar16 + lVar14);
      pbVar30 = (byte *)(lVar14 + 1 + lVar16);
      local_108 = (char *******)((ulong)local_108 & 0xffffffffffffff00);
      local_f8 = CONCAT17(0x17,(undefined7)local_f8);
      local_e8 = (char *******)((ulong)local_e8 & 0xffffffffffffff00);
      local_d8 = CONCAT17(0x17,(undefined7)local_d8);
      local_c8 = (char *******)((ulong)local_c8 & 0xffffffffffffff00);
      local_b8 = CONCAT17(0x17,(undefined7)local_b8);
      sVar10 = strlen((char *)pbVar30);
      if (sVar10 == 0) {
        lVar14 = lVar14 + 2;
        *(long *)(param_2 + 0x18) = lVar14;
      }
      else {
        uVar27 = sVar10 & 0xffffffff;
        local_a8 = (char *******)((ulong)local_a8 & 0xffffffffffffff00);
        local_98 = CONCAT17(0x17,(undefined7)local_98);
        FUN_001335d0(&local_a8,uVar27);
        if (uVar27 != 0) {
          pbVar1 = pbVar30 + uVar27;
          if ((sVar10 & 1) != 0) {
            uVar27 = (ulong)*pbVar30;
            if ((byte)(*pbVar30 + 0x80) < 0x20) {
              uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
            }
            uVar13 = (uint)uVar27;
            if (uVar13 != 0) {
              if (uVar13 < 0x80) {
                FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
              }
              else {
                if (uVar13 < 0x800) {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                }
                else {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                  bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
                }
                FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              }
            }
            pbVar30 = pbVar30 + 1;
            goto joined_r0x00196e9e;
          }
          do {
            while( true ) {
              uVar27 = (ulong)*pbVar30;
              if ((byte)(*pbVar30 + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) {
                if (uVar13 < 0x80) {
                  FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
                }
                else {
                  if (uVar13 < 0x800) {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                    bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                  }
                  else {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                    uVar12 = (uint)(uVar27 >> 0xc) | 0xffffffe0;
                    bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                    FUN_00ad7af0(&local_a8,(int)(char)uVar12,extraout_RDX_00,uVar12);
                  }
                  FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
                }
              }
              uVar27 = (ulong)pbVar30[1];
              if ((byte)(pbVar30[1] + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) break;
LAB_00197007:
              pbVar30 = pbVar30 + 2;
joined_r0x00196e9e:
              if (pbVar30 == pbVar1) goto LAB_00197019;
            }
            if (0x7f < uVar13) {
              if (uVar13 < 0x800) {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
              }
              else {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
              }
              FUN_00ad7af0(&local_a8,(int)(char)bVar33);
              FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              goto LAB_00197007;
            }
            pbVar30 = pbVar30 + 2;
            FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
          } while (pbVar30 != pbVar1);
        }
LAB_00197019:
        uVar27 = local_f8;
        pppppppcVar20 = pppppppcStack_100;
        pppppppcVar19 = local_108;
        local_78 = (code *)local_f8;
        local_f8 = local_98;
        local_98 = uVar27;
        local_88 = local_108;
        pppppppcStack_80 = pppppppcStack_100;
        local_108 = local_a8;
        pppppppcStack_100 = pppppppcStack_a0;
        local_a8 = pppppppcVar19;
        pppppppcStack_a0 = pppppppcVar20;
        if (((long)uVar27 < 0) && (pppppppcVar19 != (char *******)0x0)) {
          eastl__basic_string();
        }
        lVar16 = *(long *)(param_2 + 0x10);
        lVar14 = sVar10 + 1 + *(long *)(param_2 + 0x18);
        *(long *)(param_2 + 0x18) = lVar14;
      }
      pbVar30 = (byte *)(lVar16 + lVar14);
      sVar10 = strlen((char *)pbVar30);
      if (sVar10 == 0) {
        *(long *)(param_2 + 0x18) = lVar14 + 1;
        if ((long)local_d8 < 0) {
          pppppppcStack_e0 = (char *******)0x0;
          pppppppcVar19 = local_e8;
        }
        else {
          local_d8 = CONCAT17(0x17,(undefined7)local_d8);
          pppppppcVar19 = (char *******)&local_e8;
        }
        *(char *)pppppppcVar19 = '\0';
        lVar14 = *(long *)(param_2 + 0x18);
      }
      else {
        uVar27 = sVar10 & 0xffffffff;
        local_a8 = (char *******)((ulong)local_a8 & 0xffffffffffffff00);
        local_98 = CONCAT17(0x17,(undefined7)local_98);
        FUN_001335d0(&local_a8,uVar27);
        if (uVar27 != 0) {
          pbVar1 = pbVar30 + uVar27;
          if ((sVar10 & 1) != 0) {
            uVar27 = (ulong)*pbVar30;
            if ((byte)(*pbVar30 + 0x80) < 0x20) {
              uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
            }
            uVar13 = (uint)uVar27;
            if (uVar13 != 0) {
              if (uVar13 < 0x80) {
                FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
              }
              else {
                if (uVar13 < 0x800) {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                  bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                }
                else {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                  bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
                }
                FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              }
            }
            pbVar30 = pbVar30 + 1;
            goto joined_r0x00197a59;
          }
          do {
            while( true ) {
              uVar27 = (ulong)*pbVar30;
              if ((byte)(*pbVar30 + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) {
                if (uVar13 < 0x80) {
                  FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
                }
                else {
                  if (uVar13 < 0x800) {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                  }
                  else {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                    bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                    FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
                  }
                  FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
                }
              }
              uVar27 = (ulong)pbVar30[1];
              if ((byte)(pbVar30[1] + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) break;
LAB_00196d2c:
              pbVar30 = pbVar30 + 2;
joined_r0x00197a59:
              if (pbVar30 == pbVar1) goto LAB_00196d39;
            }
            if (0x7f < uVar13) {
              if (uVar13 < 0x800) {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
              }
              else {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                uVar12 = (uint)(uVar27 >> 0xc) | 0xffffffe0;
                bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                FUN_00ad7af0(&local_a8,(int)(char)uVar12,uVar12);
              }
              FUN_00ad7af0(&local_a8,(int)(char)bVar33);
              FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              goto LAB_00196d2c;
            }
            pbVar30 = pbVar30 + 2;
            FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
          } while (pbVar30 != pbVar1);
        }
LAB_00196d39:
        uVar27 = local_d8;
        pppppppcVar20 = pppppppcStack_e0;
        pppppppcVar19 = local_e8;
        local_78 = (code *)local_d8;
        local_d8 = local_98;
        local_98 = uVar27;
        local_88 = local_e8;
        pppppppcStack_80 = pppppppcStack_e0;
        local_e8 = local_a8;
        pppppppcStack_e0 = pppppppcStack_a0;
        local_a8 = pppppppcVar19;
        pppppppcStack_a0 = pppppppcVar20;
        if (((long)uVar27 < 0) && (pppppppcVar19 != (char *******)0x0)) {
          eastl__basic_string();
        }
        lVar14 = sVar10 + 1 + *(long *)(param_2 + 0x18);
        *(long *)(param_2 + 0x18) = lVar14;
      }
      pbVar30 = (byte *)(*(long *)(param_2 + 0x10) + lVar14);
      sVar10 = strlen((char *)pbVar30);
      if (sVar10 == 0) {
        *(long *)(param_2 + 0x18) = lVar14 + 1;
        if ((long)local_b8 < 0) {
          pppppppcStack_c0 = (char *******)0x0;
          pppppppcVar19 = local_c8;
        }
        else {
          local_b8 = CONCAT17(0x17,(undefined7)local_b8);
          pppppppcVar19 = (char *******)&local_c8;
        }
        *(char *)pppppppcVar19 = '\0';
      }
      else {
        uVar27 = sVar10 & 0xffffffff;
        local_a8 = (char *******)((ulong)local_a8 & 0xffffffffffffff00);
        local_98 = CONCAT17(0x17,(undefined7)local_98);
        FUN_001335d0(&local_a8,uVar27);
        if (uVar27 != 0) {
          pbVar1 = pbVar30 + uVar27;
          if ((sVar10 & 1) != 0) {
            uVar27 = (ulong)*pbVar30;
            if ((byte)(*pbVar30 + 0x80) < 0x20) {
              uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
            }
            uVar13 = (uint)uVar27;
            if (uVar13 != 0) {
              if (uVar13 < 0x80) {
                FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
              }
              else {
                if (uVar13 < 0x800) {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                }
                else {
                  pppppppcVar19 = pppppppcStack_a0;
                  if (-1 < (long)local_98) {
                    pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                  }
                  FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                  bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
                }
                FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              }
            }
            pbVar30 = pbVar30 + 1;
            goto joined_r0x00197a88;
          }
          do {
            while( true ) {
              uVar27 = (ulong)*pbVar30;
              if ((byte)(*pbVar30 + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) {
                if (uVar13 < 0x80) {
                  FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
                }
                else {
                  if (uVar13 < 0x800) {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
                  }
                  else {
                    pppppppcVar19 = pppppppcStack_a0;
                    if (-1 < (long)local_98) {
                      pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                    }
                    FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                    bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                    FUN_00ad7af0(&local_a8,(int)(char)((byte)(uVar27 >> 0xc) | 0xe0));
                  }
                  FUN_00ad7af0(&local_a8,(int)(char)bVar33);
                  FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
                }
              }
              uVar27 = (ulong)pbVar30[1];
              if ((byte)(pbVar30[1] + 0x80) < 0x20) {
                uVar27 = (ulong)*(ushort *)(&DAT_0102a600 + uVar27 * 2);
              }
              uVar13 = (uint)uVar27;
              if (uVar13 != 0) break;
LAB_00196a3f:
              pbVar30 = pbVar30 + 2;
joined_r0x00197a88:
              if (pbVar30 == pbVar1) goto LAB_00196a4c;
            }
            if (0x7f < uVar13) {
              if (uVar13 < 0x800) {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                bVar33 = (byte)(uVar13 >> 6) | 0xc0;
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 2));
              }
              else {
                pppppppcVar19 = pppppppcStack_a0;
                if (-1 < (long)local_98) {
                  pppppppcVar19 = (char *******)(0x17 - (long)local_98._7_1_);
                }
                FUN_001335d0(&local_a8,(char *)((long)pppppppcVar19 + 3));
                uVar12 = (uint)(uVar27 >> 0xc) | 0xffffffe0;
                bVar33 = (byte)(uVar13 >> 6) & 0x3f | 0x80;
                FUN_00ad7af0(&local_a8,(int)(char)uVar12,extraout_RDX,uVar12);
              }
              FUN_00ad7af0(&local_a8,(int)(char)bVar33);
              FUN_00ad7af0(&local_a8,(int)(char)((byte)uVar27 & 0x3f | 0x80));
              goto LAB_00196a3f;
            }
            pbVar30 = pbVar30 + 2;
            FUN_00ad7af0(&local_a8,(int)(char)(byte)uVar27);
          } while (pbVar30 != pbVar1);
        }
LAB_00196a4c:
        uVar27 = local_b8;
        pppppppcVar20 = pppppppcStack_c0;
        pppppppcVar19 = local_c8;
        local_78 = (code *)local_b8;
        local_b8 = local_98;
        local_98 = uVar27;
        local_88 = local_c8;
        pppppppcStack_80 = pppppppcStack_c0;
        local_c8 = local_a8;
        pppppppcStack_c0 = pppppppcStack_a0;
        local_a8 = pppppppcVar19;
        pppppppcStack_a0 = pppppppcVar20;
        if (((long)uVar27 < 0) && (pppppppcVar19 != (char *******)0x0)) {
          eastl__basic_string();
        }
        *(size_t *)(param_2 + 0x18) = sVar10 + 1 + *(long *)(param_2 + 0x18);
      }
      lVar14 = *(long *)(lVar4 + 0x30);
      iVar26 = 0;
      lVar16 = 0;
      if ((int)(*(long *)(lVar4 + 0x38) - lVar14 >> 4) * -0x33333333 < 1) {
LAB_001970af:
        local_88 = (char *******)((ulong)local_88 & 0xffffffffffffff00);
        local_78 = (code *)CONCAT17(0x17,(undefined7)local_78);
        apcStack_70[0] = (code *)((ulong)apcStack_70[0] & 0xffffffffffffff00);
        local_59 = '\x17';
        local_58 = 0;
        local_41 = '\x17';
        pppppppcVar19 = (char *******)&local_108;
        if ((long)local_f8 < 0) {
          pppppppcVar19 = local_108;
        }
        cVar9 = *(char *)pppppppcVar19;
        while (cVar9 != '\0') {
          pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
          cVar9 = *(char *)pppppppcVar19;
        }
        FUN_00493d20(&local_88);
        pppppppcVar19 = (char *******)&local_e8;
        if ((long)local_d8 < 0) {
          pppppppcVar19 = local_e8;
        }
        cVar9 = *(char *)pppppppcVar19;
        while (cVar9 != '\0') {
          pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
          cVar9 = *(char *)pppppppcVar19;
        }
        FUN_00493d20(apcStack_70);
        pppppppcVar19 = (char *******)&local_c8;
        if ((long)local_b8 < 0) {
          pppppppcVar19 = local_c8;
        }
        cVar9 = *(char *)pppppppcVar19;
        while (cVar9 != '\0') {
          pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
          cVar9 = *(char *)pppppppcVar19;
        }
        FUN_00493d20(&local_58);
        local_40 = bVar2 >> 1 & 1;
        puVar22 = *(undefined8 **)(lVar4 + 0x38);
        if (puVar22 < *(undefined8 **)(lVar4 + 0x40)) {
          *(undefined8 **)(lVar4 + 0x38) = puVar22 + 10;
          FUN_0048dcd0(puVar22,&local_88);
          FUN_0048dcd0(puVar22 + 3,apcStack_70);
          FUN_0048dcd0(puVar22 + 6,&local_58);
          *(byte *)(puVar22 + 9) = local_40;
        }
        else {
          puVar23 = *(undefined8 **)(lVar4 + 0x30);
          lVar14 = (long)puVar22 - (long)puVar23 >> 4;
          if (lVar14 * -0x3333333333333333 == 0) {
            lVar14 = 0x50;
LAB_001974c7:
            puVar11 = (undefined8 *)FUN_00c29480(lVar14);
            puVar22 = *(undefined8 **)(lVar4 + 0x38);
            local_120 = (undefined1 *)((long)puVar11 + lVar14);
            local_138 = puVar11 + 10;
            puVar23 = *(undefined8 **)(lVar4 + 0x30);
          }
          else {
            if (lVar14 * -0x6666666666666666 != 0) {
              lVar14 = lVar14 << 5;
              goto LAB_001974c7;
            }
            local_138 = &Elf64_Phdr_ARRAY_00000040[0].p_vaddr;
            local_120 = (undefined1 *)0x0;
            puVar11 = (undefined8 *)0x0;
          }
          puVar24 = puVar11;
          if (puVar22 != puVar23) {
            uVar27 = ((ulong)((long)puVar22 - (long)(puVar23 + 10)) >> 4) * 0xccccccccccccccd &
                     0xfffffffffffffff;
            puVar22 = puVar11;
            puVar24 = puVar23;
            do {
              *(undefined1 *)puVar22 = 0;
              *(undefined1 *)((long)puVar22 + 0x17) = 0x17;
              puVar25 = puVar24 + 10;
              uVar6 = *puVar22;
              uVar7 = puVar22[1];
              uVar5 = puVar22[2];
              uVar8 = puVar24[1];
              *puVar22 = *puVar24;
              puVar22[1] = uVar8;
              puVar22[2] = puVar24[2];
              *puVar24 = uVar6;
              puVar24[1] = uVar7;
              puVar24[2] = uVar5;
              *(undefined1 *)((long)puVar24 + 0x17) = 0x17;
              *(undefined1 *)(puVar22 + 3) = 0;
              *(undefined1 *)((long)puVar22 + 0x2f) = 0x17;
              uVar6 = puVar22[3];
              uVar7 = puVar22[4];
              uVar5 = puVar22[5];
              uVar8 = puVar24[4];
              puVar22[3] = puVar24[3];
              puVar22[4] = uVar8;
              puVar22[5] = puVar24[5];
              puVar24[3] = uVar6;
              puVar24[4] = uVar7;
              puVar24[5] = uVar5;
              *(undefined1 *)((long)puVar24 + 0x2f) = 0x17;
              *(undefined1 *)(puVar22 + 6) = 0;
              *(undefined1 *)((long)puVar22 + 0x47) = 0x17;
              local_a8 = (char *******)puVar22[6];
              pppppppcStack_a0 = (char *******)puVar22[7];
              local_98 = puVar22[8];
              uVar5 = puVar24[7];
              puVar22[6] = puVar24[6];
              puVar22[7] = uVar5;
              puVar22[8] = puVar24[8];
              puVar24[6] = local_a8;
              puVar24[7] = pppppppcStack_a0;
              puVar24[8] = local_98;
              *(undefined1 *)((long)puVar24 + 0x47) = 0x17;
              *(undefined1 *)(puVar22 + 9) = *(undefined1 *)(puVar24 + 9);
              puVar22 = puVar22 + 10;
              puVar24 = puVar25;
            } while (puVar23 + 10 + uVar27 * 10 != puVar25);
            local_138 = puVar11 + (uVar27 * 5 + 5) * 2 + 10;
            puVar24 = puVar11 + (uVar27 * 5 + 5) * 2;
          }
          FUN_0048dcd0(puVar24,&local_88);
          FUN_0048dcd0(puVar24 + 3,apcStack_70);
          FUN_0048dcd0(puVar24 + 6,&local_58);
          plVar29 = *(long **)(lVar4 + 0x38);
          *(byte *)(puVar24 + 9) = local_40;
          plVar32 = *(long **)(lVar4 + 0x30);
          if (plVar29 != plVar32) {
            plVar31 = plVar32 + 10;
            plVar34 = plVar32 + ((((ulong)((long)plVar29 - (long)plVar31) >> 4) * 0xccccccccccccccd
                                 & 0xfffffffffffffff) * 5 + 5) * 2;
            uVar13 = (int)((ulong)((long)plVar34 - (long)plVar31) >> 4) * -0x33333333 & 3;
            plVar29 = plVar31;
            plVar28 = plVar32;
            if (uVar13 == 0) goto LAB_00197843;
            if ((*(char *)((long)plVar32 + 0x47) < '\0') && (plVar32[6] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)plVar32 + 0x2f) < '\0') && (plVar32[3] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)plVar32 + 0x17) < '\0') && (*plVar32 != 0)) {
              HeapInterface::Free();
            }
            plVar29 = plVar32 + 0x14;
            plVar28 = plVar31;
            if (uVar13 == 1) goto LAB_00197843;
            plVar28 = plVar29;
            if (uVar13 != 2) {
              if ((*(char *)((long)plVar32 + 0x97) < '\0') && (plVar32[0x10] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)plVar32 + 0x7f) < '\0') && (plVar32[0xd] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)plVar32 + 0x67) < '\0') && (*plVar31 != 0)) {
                HeapInterface::Free();
              }
              plVar28 = plVar32 + 0x1e;
              plVar31 = plVar29;
            }
            if ((*(char *)((long)plVar31 + 0x47) < '\0') && (plVar31[6] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)plVar31 + 0x2f) < '\0') && (plVar31[3] != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)plVar31 + 0x17) < '\0') && (*plVar31 != 0)) {
              HeapInterface::Free();
            }
            plVar29 = plVar28 + 10;
            if (-1 < *(char *)((long)plVar28 + 0x47)) goto LAB_0019784e;
LAB_00197787:
            if (plVar28[6] == 0) goto LAB_0019784e;
            HeapInterface::Free();
            if (-1 < *(char *)((long)plVar28 + 0x2f)) goto LAB_00197859;
LAB_001977a4:
            if (plVar28[3] == 0) goto LAB_00197859;
            HeapInterface::Free();
            if (*(char *)((long)plVar28 + 0x17) < '\0') goto LAB_00197864;
LAB_001977c1:
            if (plVar29 != plVar34) {
              do {
                if ((*(char *)((long)plVar29 + 0x47) < '\0') && (plVar29[6] != 0)) {
                  HeapInterface::Free();
                  cVar9 = *(char *)((long)plVar29 + 0x2f);
                }
                else {
                  cVar9 = *(char *)((long)plVar29 + 0x2f);
                }
                if ((cVar9 < '\0') && (plVar29[3] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar29 + 0x17) < '\0') && (*plVar29 != 0)) {
                  HeapInterface::Free();
                  if (-1 < *(char *)((long)plVar29 + 0x97)) goto LAB_00197801;
LAB_001978da:
                  if (plVar29[0x10] == 0) goto LAB_00197801;
                  HeapInterface::Free();
                  if (*(char *)((long)plVar29 + 0x7f) < '\0') goto LAB_001978f6;
LAB_0019780b:
                  if (-1 < *(char *)((long)plVar29 + 0x67)) goto LAB_00197815;
LAB_00197912:
                  if (plVar29[10] == 0) goto LAB_00197815;
                  HeapInterface::Free();
                  if (*(char *)((long)plVar29 + 0xe7) < '\0') goto LAB_00197936;
LAB_00197828:
                  cVar9 = *(char *)((long)plVar29 + 0xcf);
                }
                else {
                  if (*(char *)((long)plVar29 + 0x97) < '\0') goto LAB_001978da;
LAB_00197801:
                  if (-1 < *(char *)((long)plVar29 + 0x7f)) goto LAB_0019780b;
LAB_001978f6:
                  if (plVar29[0xd] == 0) goto LAB_0019780b;
                  HeapInterface::Free();
                  if (*(char *)((long)plVar29 + 0x67) < '\0') goto LAB_00197912;
LAB_00197815:
                  if (-1 < *(char *)((long)plVar29 + 0xe7)) goto LAB_00197828;
LAB_00197936:
                  if (plVar29[0x1a] == 0) goto LAB_00197828;
                  HeapInterface::Free();
                  cVar9 = *(char *)((long)plVar29 + 0xcf);
                }
                if ((cVar9 < '\0') && (plVar29[0x17] != 0)) {
                  HeapInterface::Free();
                }
                plVar28 = plVar29 + 0x1e;
                if ((*(char *)((long)plVar29 + 0xb7) < '\0') && (plVar29[0x14] != 0)) {
                  HeapInterface::Free();
                }
                plVar29 = plVar29 + 0x28;
LAB_00197843:
                if (*(char *)((long)plVar28 + 0x47) < '\0') goto LAB_00197787;
LAB_0019784e:
                if (*(char *)((long)plVar28 + 0x2f) < '\0') goto LAB_001977a4;
LAB_00197859:
                if (-1 < *(char *)((long)plVar28 + 0x17)) goto LAB_001977c1;
LAB_00197864:
                if (*plVar28 == 0) goto LAB_001977c1;
                HeapInterface::Free();
                if (plVar29 == plVar34) break;
              } while( true );
            }
            plVar32 = *(long **)(lVar4 + 0x30);
          }
          if (plVar32 != (long *)0x0) {
            HeapInterface::Free(plVar32);
          }
          *(undefined8 **)(lVar4 + 0x30) = puVar11;
          *(undefined1 **)(lVar4 + 0x40) = local_120;
          *(qword **)(lVar4 + 0x38) = local_138;
        }
        if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
          HeapInterface::Free();
        }
        if ((local_59 < '\0') && (apcStack_70[0] != (code *)0x0)) {
          HeapInterface::Free();
        }
        if (((long)local_78 < 0) && (local_88 != (char *******)0x0)) {
          HeapInterface::Free();
        }
      }
      else {
        do {
          pcVar15 = (char *)(lVar14 + lVar16);
          if ((bVar2 & 1) != 0) {
            pcVar21 = pcVar15;
            if (pcVar15[0x17] < '\0') {
              pcVar21 = *(char **)pcVar15;
            }
            local_88 = (char *******)((ulong)local_88 & 0xffffffffffffff00);
            local_78 = (code *)CONCAT17(0x17,(undefined7)local_78);
            cVar9 = *pcVar21;
            while (cVar9 != '\0') {
              pcVar21 = pcVar21 + 1;
              cVar9 = *pcVar21;
            }
            FUN_0048d1a0(&local_88);
            cVar9 = FUN_00196080(&local_e8,&local_88);
            if (cVar9 == '\0') {
              if (((long)local_78 < 0) && (local_88 != (char *******)0x0)) {
                eastl__basic_string();
              }
              goto LAB_001962ed;
            }
            if (((long)local_78 < 0) && (local_88 != (char *******)0x0)) {
              eastl__basic_string();
            }
            pppppppcVar19 = (char *******)&local_108;
            if ((long)local_f8 < 0) {
              pppppppcVar19 = local_108;
            }
            cVar9 = *(char *)pppppppcVar19;
            while (cVar9 != '\0') {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
              cVar9 = *(char *)pppppppcVar19;
            }
            FUN_00493d20(pcVar15);
            pppppppcVar19 = (char *******)&local_e8;
            if ((long)local_d8 < 0) {
              pppppppcVar19 = local_e8;
            }
            cVar9 = *(char *)pppppppcVar19;
            while (cVar9 != '\0') {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
              cVar9 = *(char *)pppppppcVar19;
            }
            FUN_00493d20(pcVar15 + 0x18);
            pppppppcVar19 = (char *******)&local_c8;
            if ((long)local_b8 < 0) {
              pppppppcVar19 = local_c8;
            }
            if (*(char *)pppppppcVar19 == '\0') goto LAB_00196559;
            do {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
            } while (*(char *)pppppppcVar19 != '\0');
            FUN_00493d20(pcVar15 + 0x30);
            goto joined_r0x00196566;
          }
LAB_001962ed:
          pcVar21 = pcVar15;
          if (pcVar15[0x17] < '\0') {
            pcVar21 = *(char **)pcVar15;
          }
          local_a8 = (char *******)((ulong)local_a8 & 0xffffffffffffff00);
          local_98 = CONCAT17(0x17,(undefined7)local_98);
          pcVar18 = pcVar21;
          if (*pcVar21 == '\0') {
            pppppppcVar19 = (char *******)0x0;
LAB_00196339:
            local_98 = CONCAT17('\x17' - (char)pppppppcVar19,(undefined7)local_98);
            pppppppcVar20 = (char *******)&local_a8;
          }
          else {
            do {
              pcVar18 = pcVar18 + 1;
            } while (*pcVar18 != '\0');
            pppppppcVar19 = (char *******)(pcVar18 + -(long)pcVar21);
            if (pppppppcVar19 < &Elf64_Ehdr_00000000.e_entry) goto LAB_00196339;
            FUN_00c29ca0(&local_88,(char *)((long)pppppppcVar19 + 1),0);
            local_a8 = pppppppcStack_80;
            local_98 = (ulong)pppppppcVar19 | 0x8000000000000000;
            pppppppcVar20 = pppppppcStack_80;
            pppppppcStack_a0 = pppppppcVar19;
            if (-1 < (long)local_98) {
              pppppppcVar20 = (char *******)&local_a8;
            }
          }
          memmove(pppppppcVar20,pcVar21,(size_t)pppppppcVar19);
          if ((long)local_98 < 0) {
            pcVar21 = (char *)((long)pppppppcStack_a0 + (long)local_a8);
          }
          else {
            pcVar21 = (char *)((long)&local_98 + (7 - (long)local_98._7_1_));
          }
          *pcVar21 = '\0';
          cVar9 = FUN_00196080(&local_108,&local_a8);
          if (((long)local_98 < 0) && (local_a8 != (char *******)0x0)) {
            eastl__basic_string();
          }
          if (cVar9 != '\0') {
            pppppppcVar19 = (char *******)&local_108;
            if ((long)local_f8 < 0) {
              pppppppcVar19 = local_108;
            }
            cVar9 = *(char *)pppppppcVar19;
            while (cVar9 != '\0') {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
              cVar9 = *(char *)pppppppcVar19;
            }
            FUN_00493d20(pcVar15);
            pppppppcVar19 = (char *******)&local_e8;
            if ((long)local_d8 < 0) {
              pppppppcVar19 = local_e8;
            }
            cVar9 = *(char *)pppppppcVar19;
            while (cVar9 != '\0') {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
              cVar9 = *(char *)pppppppcVar19;
            }
            FUN_00493d20(pcVar15 + 0x18);
            pppppppcVar19 = (char *******)&local_c8;
            if ((long)local_b8 < 0) {
              pppppppcVar19 = local_c8;
            }
            cVar9 = *(char *)pppppppcVar19;
            while (cVar9 != '\0') {
              pppppppcVar19 = (char *******)((long)pppppppcVar19 + 1);
              cVar9 = *(char *)pppppppcVar19;
            }
LAB_00196559:
            FUN_00493d20(pcVar15 + 0x30);
            goto joined_r0x00196566;
          }
          lVar14 = *(long *)(lVar4 + 0x30);
          iVar26 = iVar26 + 1;
          lVar16 = lVar16 + 0x50;
          iVar17 = (int)(*(long *)(lVar4 + 0x38) - lVar14 >> 4) * -0x33333333;
        } while (iVar26 < iVar17);
        if (iVar17 < 400) goto LAB_001970af;
      }
joined_r0x00196566:
      if (((long)local_b8 < 0) && (local_c8 != (char *******)0x0)) {
        eastl__basic_string();
      }
      if (((long)local_d8 < 0) && (local_e8 != (char *******)0x0)) {
        eastl__basic_string();
      }
      if (((long)local_f8 < 0) && (local_108 != (char *******)0x0)) {
        eastl__basic_string();
      }
      lVar14 = *(long *)(param_2 + 0x18);
    } while ((int)lVar14 < iVar3);
    lVar16 = *param_1;
  }
  lVar14 = *(long *)((long)&__DT_RELA[0xd00].r_offset + lVar16);
  uVar5 = *(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + lVar16);
  *(undefined4 *)(lVar14 + 0xf0) = *(undefined4 *)(*(long *)(lVar14 + 0xe8) + 0x10);
  local_78 = FUN_000ec0d0;
  apcStack_70[0] = FUN_00143110;
  FUN_00143a20(uVar5,&local_88);
  if (local_78 != (code *)0x0) {
    (*local_78)(&local_88,&local_88,3);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::MESSAGE_PUBLIC` @ 00197e00
```c

undefined * jag::packethandlers::Chat::MESSAGE_PUBLIC(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;
  undefined8 uVar7;
  int *piVar8;
  char cVar9;
  int iVar10;
  ulong uVar11;
  undefined *puVar12;
  char *pcVar13;
  undefined8 extraout_RDX;
  undefined1 *puVar14;
  int iVar15;
  ulong uVar16;
  int local_13c;
  undefined1 local_138;
  undefined7 uStack_137;
  long local_130;
  char local_121 [9];
  undefined1 local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  ulong local_f0;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  long local_b8 [2];
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  long local_78 [2];
  char local_61;
  ulong local_58;
  undefined1 *local_50;
  char local_41;
  
  lVar5 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar5 + 0x38) == *(long *)(lVar5 + 0x40)) &&
     (cVar9 = FUN_006a53c0(), cVar9 == '\0')) {
    return &DAT_015d3600;
  }
  lVar5 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  cVar9 = *(char *)(*(long *)(param_2 + 0x10) + lVar5);
  local_138 = 0;
  local_121[0] = '\x17';
  FUN_00ad80e0(param_2,&local_138);
  local_118 = 0;
  local_101 = '\x17';
  if (cVar9 == '\x01') {
    FUN_00ad80e0(param_2,&local_118);
  }
  else {
    if (local_121[0] < '\0') {
      puVar14 = (undefined1 *)CONCAT71(uStack_137,local_138);
      pcVar13 = puVar14 + local_130;
    }
    else {
      pcVar13 = local_121 + -(long)local_121[0];
      puVar14 = &local_138;
    }
    FUN_00495ef0(&local_118,puVar14,pcVar13);
  }
  iVar10 = FUN_00121a30(param_2);
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  bVar1 = *(byte *)(lVar6 + 1 + lVar5);
  bVar2 = *(byte *)(lVar6 + lVar5);
  bVar3 = *(byte *)(lVar6 + 2 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  local_13c = (uint)bVar2 * 0x10000 + (uint)bVar1 * 0x100 + (uint)bVar3 + iVar10 * 0x1000000;
  uVar16 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
  if (uVar16 == 0) {
LAB_00197f72:
    uVar11 = (ulong)*(byte *)(lVar6 + 3 + lVar5);
    if ((&DAT_015a4aa9)[uVar11 * 0xc] != '\0') {
      lVar5 = *param_1;
      lVar6 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar5);
      if (((lVar6 != 0) &&
          (((*(char *)(lVar6 + 0x10) != '\0' && (*(char *)(lVar6 + 0x19) == '\0')) ||
           (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar5) + 8) != '\0')))) ||
         (cVar9 = RelationshipManager::IsOnIgnoreList
                            (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar5),&local_118),
         cVar9 != '\0')) goto LAB_00197fb0;
      uVar16 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
    }
    if ((int)uVar16 == DAT_013942d8) {
      if (DAT_013942e8 < DAT_013942f0) {
        piVar8 = DAT_013942e8 + 1;
        *DAT_013942e8 = local_13c;
        DAT_013942e8 = piVar8;
      }
      else {
        FUN_00baa7b0(&DAT_013942e0,&local_13c);
      }
    }
    else {
      DAT_013942e0[DAT_013942d8] = local_13c;
    }
    local_f8 = 0;
    local_e1 = '\x17';
    DAT_013942d8 = (DAT_013942d8 + 1) % 100;
    puVar14 = &local_f8;
    cVar9 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),puVar14,
                         param_2);
    puVar12 = &DAT_015d3600;
    if (cVar9 != '\0') {
      local_d8 = 0;
      local_c1 = '\x17';
      if (local_e1 < '\0') {
        local_50 = (undefined1 *)CONCAT71(uStack_f7,local_f8);
      }
      else {
        local_f0 = 0x17 - (long)local_e1;
        local_50 = puVar14;
      }
      local_58 = local_f0;
      FUN_00133770(&local_d8,&local_58);
      lVar5 = uVar11 * 0xc;
      FUN_00126110(puVar14,&local_d8);
      iVar10 = *(int *)(&DAT_015a4aa4 + lVar5);
      uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
      iVar15 = (-(uint)((&DAT_015a4aa8)[lVar5] == '\0') & 0xfffffffc) + 7;
      if (iVar10 == -1) {
        ChatHistory::AddChat(uVar7,iVar15,0,&local_138,&local_118,&local_138,puVar14);
      }
      else {
        local_98 = 0;
        local_81 = '\x17';
        FUN_001335d0(&local_98,8);
        FUN_00146960(&local_98,"<img=%d>",iVar10);
        FUN_001261f0(local_b8,&local_98,&local_118);
        uVar4 = *(undefined4 *)(&DAT_015a4aa4 + lVar5);
        local_58 = local_58 & 0xffffffffffffff00;
        local_41 = '\x17';
        FUN_001335d0(&local_58,8);
        FUN_00146960(&local_58,"<img=%d>",uVar4);
        FUN_001261f0(local_78,&local_58,&local_138);
        ChatHistory::AddChat(uVar7,iVar15,0,local_78,local_b8,&local_138);
        if ((local_61 < '\0') && (local_78[0] != 0)) {
          eastl__basic_string(local_78[0],&DAT_015a4aa0 + lVar5,extraout_RDX,puVar14);
        }
        if ((local_41 < '\0') && (local_58 != 0)) {
          eastl__basic_string();
        }
        if ((local_a1 < '\0') && (local_b8[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
          eastl__basic_string();
        }
      }
      if ((local_c1 < '\0') && (CONCAT71(uStack_d7,local_d8) != 0)) {
        eastl__basic_string();
      }
      puVar12 = &DAT_015d3620;
    }
    if ((local_e1 < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
      eastl__basic_string();
    }
  }
  else {
    uVar11 = 0;
    iVar10 = *DAT_013942e0;
    while (local_13c != iVar10) {
      uVar11 = (ulong)((int)uVar11 + 1);
      if (uVar16 <= uVar11) goto LAB_00197f72;
      iVar10 = DAT_013942e0[uVar11];
    }
LAB_00197fb0:
    puVar12 = &DAT_015d3620;
  }
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  if ((local_121[0] < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  return puVar12;
}


```

## `jag::packethandlers::Chat::MESSAGE_GAME` @ 001983e0
```c

undefined * jag::packethandlers::Chat::MESSAGE_GAME(long *param_1,long param_2)

{
  byte bVar1;
  uint uVar2;
  char cVar3;
  short sVar4;
  long lVar5;
  undefined1 *puVar6;
  long lVar7;
  char *pcVar8;
  ushort uVar9;
  bool bVar10;
  long local_a8;
  undefined1 *local_a0;
  undefined1 local_98;
  undefined7 uStack_97;
  long local_90;
  char local_81 [9];
  undefined1 local_78;
  undefined7 uStack_77;
  long local_70;
  char local_61;
  undefined1 local_58;
  undefined7 uStack_57;
  long local_50;
  char local_41;
  
  lVar7 = *(long *)(param_2 + 0x10);
  bVar1 = *(byte *)(lVar7 + *(long *)(param_2 + 0x18));
  uVar9 = (ushort)bVar1;
  lVar5 = *(long *)(param_2 + 0x18) + 1;
  if ((char)bVar1 < '\0') {
    sVar4 = FUN_00121a30(param_2);
    lVar7 = *(long *)(param_2 + 0x10);
    lVar5 = *(long *)(param_2 + 0x18);
    uVar9 = sVar4 + 0x8000;
  }
  bVar10 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  uVar2 = *(uint *)(lVar7 + lVar5);
  if (bVar10) {
    uVar2 = uVar2 >> 0x18 | (uVar2 & 0xff0000) >> 8 | (uVar2 & 0xff00) << 8 | uVar2 << 0x18;
  }
  *(long *)(param_2 + 0x18) = lVar5 + 5;
  bVar1 = *(byte *)(lVar7 + lVar5 + 4);
  local_98 = 0;
  local_81[0] = '\x17';
  local_78 = 0;
  local_61 = '\x17';
  if ((bVar1 & 1) != 0) {
    puVar6 = &local_98;
    FUN_00ad80e0(param_2,puVar6);
    if ((bVar1 & 2) == 0) {
      if (local_81[0] < '\0') {
        puVar6 = (undefined1 *)CONCAT71(uStack_97,local_98);
        pcVar8 = puVar6 + local_90;
      }
      else {
        pcVar8 = local_81 + -(long)local_81[0];
      }
      FUN_00495ef0(&local_78,puVar6,pcVar8);
    }
    else {
      FUN_00ad80e0(param_2,&local_78);
    }
  }
  puVar6 = &local_58;
  local_58 = 0;
  local_41 = '\x17';
  FUN_00ad80e0(param_2,puVar6);
  if (uVar9 == 99) {
    if (local_41 < '\0') {
      puVar6 = (undefined1 *)CONCAT71(uStack_57,local_58);
    }
    FUN_00442210(*(undefined8 *)((long)&__DT_RELA[0xcfe].r_addend + *param_1),1,&DAT_00fb4038,puVar6
                );
  }
  else {
    lVar7 = *param_1;
    if (uVar9 == 0x60) {
      if (local_41 < '\0') {
        puVar6 = (undefined1 *)CONCAT71(uStack_57,local_58);
      }
      FUN_00442210(*(undefined8 *)((long)&__DT_RELA[0xcfe].r_addend + lVar7),5,&DAT_00fb4038,puVar6)
      ;
    }
    else if (uVar9 == 0x62) {
      if (local_41 < '\0') {
        puVar6 = (undefined1 *)CONCAT71(uStack_57,local_58);
      }
      else {
        local_50 = 0x17 - (long)local_41;
      }
      local_a8 = local_50;
      local_a0 = puVar6;
      FUN_003eb240(*(long *)((long)&__DT_RELA[0xcfe].r_addend + lVar7) + 0x2a0,&local_a8,1);
    }
    else {
      if (-1 < local_61) {
        local_70 = 0x17 - (long)local_61;
      }
      if (local_70 != 0) {
        cVar3 = RelationshipManager::IsOnIgnoreList
                          (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar7),&local_78);
        if (cVar3 != '\0') goto joined_r0x0019859b;
        lVar7 = *param_1;
      }
      ChatHistory::AddChat
                (*(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + lVar7),uVar9,uVar2,&local_98,
                 &local_78,&local_98,puVar6);
    }
  }
joined_r0x0019859b:
  if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    eastl__basic_string();
  }
  if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
    eastl__basic_string();
  }
  if ((local_81[0] < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
    eastl__basic_string();
  }
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

## `jag::packethandlers::Clans::CLANCHANNEL_FULL` @ 00199130
```c

undefined * jag::packethandlers::Clans::CLANCHANNEL_FULL(long *param_1,long param_2,int *param_3)

{
  undefined4 uVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  short sVar5;
  int iVar6;
  byte *pbVar7;
  char *pcVar8;
  char *pcVar9;
  size_t __n;
  qword *pqVar10;
  long *plVar11;
  long *plVar12;
  long *plVar13;
  long *plVar14;
  undefined1 *puVar15;
  undefined1 *puVar16;
  long *plVar17;
  ulong uVar18;
  ulong uVar19;
  uint uVar20;
  long lVar21;
  int iVar22;
  long *plVar23;
  int iVar24;
  long lVar25;
  char cVar26;
  ushort uVar27;
  bool bVar28;
  long *local_178;
  long *local_170;
  qword *local_168;
  long *local_160;
  long *local_158;
  undefined1 *local_150;
  long local_148;
  long *local_140;
  long *local_138;
  char local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  size_t local_f0;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  long local_b8;
  long *plStack_b0;
  long local_a8;
  char local_88;
  undefined7 uStack_87;
  char local_71;
  long local_70;
  char local_59;
  long local_50;
  char local_39;
  
  iVar22 = *param_3;
  lVar21 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf8].r_info + *param_1);
  *(undefined4 *)(lVar21 + 0x110) = *(undefined4 *)(*(long *)(lVar21 + 0x108) + 0x10);
  if (iVar22 == 0) {
    if (*(char *)(lVar2 + 0x1f) < '\0') {
      *(undefined8 *)(lVar2 + 0x10) = 0;
      puVar15 = *(undefined1 **)(lVar2 + 8);
    }
    else {
      puVar15 = (undefined1 *)(lVar2 + 8);
      *(undefined1 *)(lVar2 + 0x1f) = 0x17;
    }
    *puVar15 = 0;
    if (*(char *)(lVar2 + 0x37) < '\0') {
      *(undefined8 *)(lVar2 + 0x28) = 0;
      puVar15 = *(undefined1 **)(lVar2 + 0x20);
    }
    else {
      puVar15 = (undefined1 *)(lVar2 + 0x20);
      *(undefined1 *)(lVar2 + 0x37) = 0x17;
    }
    *puVar15 = 0;
    plVar17 = *(long **)(lVar2 + 0x40);
    if (*(long **)(lVar2 + 0x48) != plVar17) {
      plVar23 = plVar17 + 10;
      plVar13 = plVar17 + ((((ulong)((long)*(long **)(lVar2 + 0x48) - (long)plVar23) >> 4) *
                            0xccccccccccccccd & 0xfffffffffffffff) * 5 + 5) * 2;
      uVar20 = (int)((ulong)((long)plVar13 - (long)plVar23) >> 4) * -0x33333333 & 3;
      plVar14 = plVar17;
      plVar11 = plVar23;
      if (uVar20 == 0) goto LAB_00199eaf;
      if ((*(char *)((long)plVar17 + 0x4f) < '\0') && (plVar17[7] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar17 + 0x2f) < '\0') && (plVar17[3] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar17 + 0x17) < '\0') && (*plVar17 != 0)) {
        HeapInterface::Free();
      }
      plVar11 = plVar17 + 0x14;
      plVar14 = plVar23;
      if (uVar20 == 1) goto LAB_00199eaf;
      plVar14 = plVar11;
      if (uVar20 != 2) {
        if ((*(char *)((long)plVar17 + 0x9f) < '\0') && (plVar17[0x11] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar17 + 0x7f) < '\0') && (plVar17[0xd] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar17 + 0x67) < '\0') && (*plVar23 != 0)) {
          HeapInterface::Free();
        }
        plVar14 = plVar17 + 0x1e;
        plVar23 = plVar11;
      }
      if ((*(char *)((long)plVar23 + 0x4f) < '\0') && (plVar23[7] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x2f) < '\0') && (plVar23[3] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x17) < '\0') && (*plVar23 != 0)) {
        HeapInterface::Free();
      }
      plVar11 = plVar14 + 10;
      if (-1 < *(char *)((long)plVar14 + 0x4f)) goto LAB_00199eb9;
LAB_00199de5:
      if (plVar14[7] == 0) goto LAB_00199eb9;
      HeapInterface::Free();
      if (-1 < *(char *)((long)plVar14 + 0x2f)) goto LAB_00199ec3;
LAB_00199e10:
      if (plVar14[3] == 0) goto LAB_00199ec3;
      HeapInterface::Free();
      if (*(char *)((long)plVar14 + 0x17) < '\0') goto LAB_00199ecd;
LAB_00199e30:
      if (plVar11 != plVar13) {
        do {
          if ((*(char *)((long)plVar11 + 0x4f) < '\0') && (plVar11[7] != 0)) {
            HeapInterface::Free();
            cVar26 = *(char *)((long)plVar11 + 0x2f);
          }
          else {
            cVar26 = *(char *)((long)plVar11 + 0x2f);
          }
          if ((cVar26 < '\0') && (plVar11[3] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar11 + 0x17) < '\0') && (*plVar11 != 0)) {
            HeapInterface::Free();
            if (-1 < *(char *)((long)plVar11 + 0x9f)) goto LAB_00199e6c;
LAB_0019a000:
            if (plVar11[0x11] == 0) goto LAB_00199e6c;
            HeapInterface::Free();
            if (*(char *)((long)plVar11 + 0x7f) < '\0') goto LAB_0019a020;
LAB_00199e76:
            if (-1 < *(char *)((long)plVar11 + 0x67)) goto LAB_00199e80;
LAB_0019a040:
            if (plVar11[10] == 0) goto LAB_00199e80;
            HeapInterface::Free();
            if (*(char *)((long)plVar11 + 0xef) < '\0') goto LAB_0019a064;
LAB_00199e92:
            if (-1 < *(char *)((long)plVar11 + 0xcf)) goto LAB_00199e9d;
LAB_0019a090:
            if (plVar11[0x17] == 0) goto LAB_00199e9d;
            HeapInterface::Free();
            cVar26 = *(char *)((long)plVar11 + 0xb7);
          }
          else {
            if (*(char *)((long)plVar11 + 0x9f) < '\0') goto LAB_0019a000;
LAB_00199e6c:
            if (-1 < *(char *)((long)plVar11 + 0x7f)) goto LAB_00199e76;
LAB_0019a020:
            if (plVar11[0xd] == 0) goto LAB_00199e76;
            HeapInterface::Free();
            if (*(char *)((long)plVar11 + 0x67) < '\0') goto LAB_0019a040;
LAB_00199e80:
            if (-1 < *(char *)((long)plVar11 + 0xef)) goto LAB_00199e92;
LAB_0019a064:
            if (plVar11[0x1b] == 0) goto LAB_00199e92;
            HeapInterface::Free();
            if (*(char *)((long)plVar11 + 0xcf) < '\0') goto LAB_0019a090;
LAB_00199e9d:
            cVar26 = *(char *)((long)plVar11 + 0xb7);
          }
          if ((cVar26 < '\0') && (plVar11[0x14] != 0)) {
            HeapInterface::Free();
          }
          plVar14 = plVar11 + 0x1e;
          plVar11 = plVar11 + 0x28;
LAB_00199eaf:
          if (*(char *)((long)plVar14 + 0x4f) < '\0') goto LAB_00199de5;
LAB_00199eb9:
          if (*(char *)((long)plVar14 + 0x2f) < '\0') goto LAB_00199e10;
LAB_00199ec3:
          if (-1 < *(char *)((long)plVar14 + 0x17)) goto LAB_00199e30;
LAB_00199ecd:
          if (*plVar14 == 0) goto LAB_00199e30;
          HeapInterface::Free();
          if (plVar11 == plVar13) break;
        } while( true );
      }
      plVar17 = *(long **)(lVar2 + 0x40);
    }
    *(long **)(lVar2 + 0x48) = plVar17;
    *(undefined8 *)(lVar2 + 0x38) = 0xffffffffffffffff;
    goto LAB_00199c70;
  }
  local_88 = '\0';
  local_71 = '\x17';
  FUN_00afd8d0(param_2,&local_88);
  pcVar8 = &local_88;
  cVar26 = local_88;
  if (local_71 < '\0') {
    pcVar8 = (char *)CONCAT71(uStack_87,local_88);
    cVar26 = *pcVar8;
  }
  while (cVar26 != '\0') {
    pcVar8 = pcVar8 + 1;
    cVar26 = *pcVar8;
  }
  FUN_00493d20(lVar2 + 0x20);
  if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
    HeapInterface::Free();
  }
  lVar21 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar21 + 1;
  if (*(char *)(*(long *)(param_2 + 0x10) + lVar21) == '\x01') {
    local_88 = '\0';
    local_71 = '\x17';
    FUN_00afd8d0(param_2,&local_88);
    if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
      HeapInterface::Free();
    }
  }
  pcVar8 = &local_118;
  local_118 = '\0';
  local_101 = '\x17';
  FUN_00ad80e0(param_2,pcVar8);
  cVar26 = local_118;
  if (local_101 < '\0') {
    pcVar8 = (char *)CONCAT71(uStack_117,local_118);
    cVar26 = *pcVar8;
  }
  while (cVar26 != '\0') {
    pcVar8 = pcVar8 + 1;
    cVar26 = *pcVar8;
  }
  FUN_00493d20(lVar2 + 8);
  lVar21 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar21 + 1;
  pbVar7 = (byte *)(*(long *)(param_2 + 0x10) + lVar21 + 1);
  *(int *)(lVar2 + 0x3c) = (int)*(char *)(*(long *)(param_2 + 0x10) + lVar21);
  if ((char)*pbVar7 < '\0') {
    sVar5 = FUN_00121a30(param_2);
    sVar5 = sVar5 + 0x7fff;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar21 + 2;
    sVar5 = *pbVar7 - 1;
  }
  if (sVar5 != -1) {
    local_168 = (qword *)0x0;
    local_160 = (long *)0x0;
    if (sVar5 < 1) {
      local_158 = (long *)0x0;
      local_168 = (qword *)0x0;
      local_178 = (long *)0x0;
    }
    else {
      local_178 = (long *)0x0;
      iVar22 = 0;
      do {
        local_f8 = 0;
        local_e1 = '\x17';
        FUN_00ad80e0(param_2,&local_f8);
        lVar3 = *(long *)(param_2 + 0x18);
        lVar25 = *(long *)(param_2 + 0x10);
        lVar21 = lVar3 + 1;
        *(long *)(param_2 + 0x18) = lVar21;
        if (*(char *)(lVar25 + lVar3) == '\x01') {
          local_88 = '\0';
          local_71 = '\x17';
          FUN_00afd8d0(param_2,&local_88);
          if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
            HeapInterface::Free();
          }
          lVar25 = *(long *)(param_2 + 0x10);
          lVar21 = *(long *)(param_2 + 0x18);
        }
        bVar28 = DAT_01050dc0 == 0x3020100;
        *(long *)(param_2 + 0x18) = lVar21 + 2;
        uVar27 = *(ushort *)(lVar25 + lVar21);
        if (bVar28) {
          uVar27 = uVar27 << 8 | uVar27 >> 8;
        }
        *(long *)(param_2 + 0x18) = lVar21 + 3;
        iVar24 = (int)*(char *)(lVar25 + lVar21 + 2);
        local_d8 = 0;
        local_c1 = '\x17';
        FUN_00ad80e0(param_2,&local_d8);
        ClanChannelUser::ClanChannelUser(&local_88,&local_f8,uVar27,iVar24,&local_d8);
        if (local_168 < local_160) {
          FUN_00126150(local_168,&local_88);
          local_168 = local_168 + 10;
          plVar17 = local_178;
        }
        else {
          lVar21 = (long)local_168 - (long)local_178 >> 4;
          if (lVar21 * -0x3333333333333333 == 0) {
            lVar21 = 0x50;
LAB_001993af:
            FUN_00c29ca0(&local_b8,lVar21,0);
            local_160 = (long *)(lVar21 + (long)plStack_b0);
            pqVar10 = (qword *)(plStack_b0 + 10);
            local_158 = plStack_b0;
            if ((qword *)local_178 != local_168) goto LAB_001993ea;
LAB_0019a920:
            FUN_00126150(local_158,&local_88);
          }
          else {
            if (lVar21 * -0x6666666666666666 != 0) {
              lVar21 = lVar21 << 5;
              goto LAB_001993af;
            }
            local_158 = (long *)0x0;
            pqVar10 = &Elf64_Phdr_ARRAY_00000040[0].p_vaddr;
            local_160 = (long *)0x0;
            if ((qword *)local_178 == local_168) goto LAB_0019a920;
LAB_001993ea:
            uVar19 = ((ulong)((long)local_168 - (long)(local_178 + 10)) >> 4) * 0xccccccccccccccd &
                     0xfffffffffffffff;
            plVar17 = local_178;
            plVar23 = local_158;
            do {
              *(undefined1 *)plVar23 = 0;
              *(undefined1 *)((long)plVar23 + 0x17) = 0x17;
              plVar11 = plVar17 + 10;
              lVar3 = plVar17[1];
              lVar21 = plVar23[2];
              lVar25 = *plVar23;
              lVar4 = plVar23[1];
              *plVar23 = *plVar17;
              plVar23[1] = lVar3;
              plVar23[2] = plVar17[2];
              plVar17[2] = lVar21;
              *plVar17 = lVar25;
              plVar17[1] = lVar4;
              *(undefined1 *)((long)plVar17 + 0x17) = 0x17;
              *(undefined1 *)(plVar23 + 3) = 0;
              *(undefined1 *)((long)plVar23 + 0x2f) = 0x17;
              lVar21 = plVar23[5];
              lVar3 = plVar17[4];
              lVar25 = plVar23[3];
              lVar4 = plVar23[4];
              plVar23[3] = plVar17[3];
              plVar23[4] = lVar3;
              plVar23[5] = plVar17[5];
              plVar17[3] = lVar25;
              plVar17[4] = lVar4;
              plVar17[5] = lVar21;
              *(undefined1 *)((long)plVar17 + 0x2f) = 0x17;
              *(int *)(plVar23 + 6) = (int)plVar17[6];
              uVar1 = *(undefined4 *)((long)plVar17 + 0x34);
              *(undefined1 *)(plVar23 + 7) = 0;
              *(undefined1 *)((long)plVar23 + 0x4f) = 0x17;
              local_a8 = plVar23[9];
              local_b8 = plVar23[7];
              plStack_b0 = (long *)plVar23[8];
              *(undefined4 *)((long)plVar23 + 0x34) = uVar1;
              lVar21 = plVar17[8];
              plVar23[7] = plVar17[7];
              plVar23[8] = lVar21;
              plVar23[9] = plVar17[9];
              plVar17[9] = local_a8;
              plVar17[7] = local_b8;
              plVar17[8] = (long)plStack_b0;
              *(undefined1 *)((long)plVar17 + 0x4f) = 0x17;
              plVar17 = plVar11;
              plVar23 = plVar23 + 10;
            } while (plVar11 != local_178 + 10 + uVar19 * 10);
            lVar21 = uVar19 * 5 + 5;
            pqVar10 = (qword *)(local_158 + uVar19 * 10 + 0x14);
            FUN_00126150(local_158 + lVar21 * 2,&local_88);
            uVar20 = (int)(lVar21 * 0x10 - 0x50U >> 4) + 1U & 3;
            plVar17 = local_178;
            if (uVar20 != 0) {
              if (uVar20 != 1) {
                if (uVar20 != 2) {
                  if ((*(char *)((long)local_178 + 0x4f) < '\0') && (local_178[7] != 0)) {
                    HeapInterface::Free();
                  }
                  if ((*(char *)((long)local_178 + 0x2f) < '\0') && (local_178[3] != 0)) {
                    HeapInterface::Free();
                  }
                  if ((*(char *)((long)local_178 + 0x17) < '\0') && (*local_178 != 0)) {
                    HeapInterface::Free();
                  }
                  plVar17 = local_178 + 10;
                }
                if ((*(char *)((long)plVar17 + 0x4f) < '\0') && (plVar17[7] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar17 + 0x2f) < '\0') && (plVar17[3] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar17 + 0x17) < '\0') && (*plVar17 != 0)) {
                  HeapInterface::Free();
                }
                plVar17 = plVar17 + 10;
              }
              if ((*(char *)((long)plVar17 + 0x4f) < '\0') && (plVar17[7] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)plVar17 + 0x2f) < '\0') && (plVar17[3] != 0)) {
                HeapInterface::Free();
              }
              if ((*(char *)((long)plVar17 + 0x17) < '\0') && (*plVar17 != 0)) {
                HeapInterface::Free();
              }
              plVar17 = plVar17 + 10;
              goto joined_r0x001995c4;
            }
            do {
              while( true ) {
                if ((*(char *)((long)plVar17 + 0x4f) < '\0') && (plVar17[7] != 0)) {
                  HeapInterface::Free();
                  cVar26 = *(char *)((long)plVar17 + 0x2f);
                }
                else {
                  cVar26 = *(char *)((long)plVar17 + 0x2f);
                }
                if ((cVar26 < '\0') && (plVar17[3] != 0)) {
                  HeapInterface::Free();
                }
                if ((*(char *)((long)plVar17 + 0x17) < '\0') && (*plVar17 != 0)) break;
                if (*(char *)((long)plVar17 + 0x9f) < '\0') goto LAB_00199698;
LAB_001995ee:
                if (-1 < *(char *)((long)plVar17 + 0x7f)) goto LAB_001995f8;
LAB_001996c0:
                if (plVar17[0xd] == 0) goto LAB_001995f8;
                HeapInterface::Free();
                if (*(char *)((long)plVar17 + 0x67) < '\0') goto LAB_001996e0;
LAB_00199602:
                if (-1 < *(char *)((long)plVar17 + 0xef)) goto LAB_00199610;
LAB_001996ff:
                if (plVar17[0x1b] == 0) goto LAB_00199610;
                HeapInterface::Free();
                if (*(char *)((long)plVar17 + 0xcf) < '\0') goto LAB_00199730;
LAB_0019961a:
                if (-1 < *(char *)((long)plVar17 + 0xb7)) goto LAB_00199624;
LAB_00199760:
                if (plVar17[0x14] == 0) goto LAB_00199624;
                HeapInterface::Free();
                if (*(char *)((long)plVar17 + 0x13f) < '\0') goto LAB_00199783;
LAB_00199635:
                if (-1 < *(char *)((long)plVar17 + 0x11f)) goto LAB_0019963f;
LAB_001997b0:
                if (plVar17[0x21] == 0) goto LAB_0019963f;
                HeapInterface::Free();
                if (*(char *)((long)plVar17 + 0x107) < '\0') goto LAB_001997e0;
LAB_00199649:
                plVar17 = plVar17 + 0x28;
joined_r0x001995c4:
                if (plVar17 == local_178 + lVar21 * 2) goto LAB_0019980c;
              }
              HeapInterface::Free();
              if (-1 < *(char *)((long)plVar17 + 0x9f)) goto LAB_001995ee;
LAB_00199698:
              if (plVar17[0x11] == 0) goto LAB_001995ee;
              HeapInterface::Free();
              if (*(char *)((long)plVar17 + 0x7f) < '\0') goto LAB_001996c0;
LAB_001995f8:
              if (-1 < *(char *)((long)plVar17 + 0x67)) goto LAB_00199602;
LAB_001996e0:
              if (plVar17[10] == 0) goto LAB_00199602;
              HeapInterface::Free();
              if (*(char *)((long)plVar17 + 0xef) < '\0') goto LAB_001996ff;
LAB_00199610:
              if (-1 < *(char *)((long)plVar17 + 0xcf)) goto LAB_0019961a;
LAB_00199730:
              if (plVar17[0x17] == 0) goto LAB_0019961a;
              HeapInterface::Free();
              if (*(char *)((long)plVar17 + 0xb7) < '\0') goto LAB_00199760;
LAB_00199624:
              if (-1 < *(char *)((long)plVar17 + 0x13f)) goto LAB_00199635;
LAB_00199783:
              if (plVar17[0x25] == 0) goto LAB_00199635;
              HeapInterface::Free();
              if (*(char *)((long)plVar17 + 0x11f) < '\0') goto LAB_001997b0;
LAB_0019963f:
              if (-1 < *(char *)((long)plVar17 + 0x107)) goto LAB_00199649;
LAB_001997e0:
              if (plVar17[0x1e] == 0) goto LAB_00199649;
              HeapInterface::Free();
              plVar17 = plVar17 + 0x28;
            } while (plVar17 != local_178 + lVar21 * 2);
          }
LAB_0019980c:
          local_168 = pqVar10;
          plVar17 = local_158;
          if (local_178 != (long *)0x0) {
            eastl__basic_string(local_178);
            plVar17 = local_158;
          }
        }
        local_178 = plVar17;
        if ((local_39 < '\0') && (local_50 != 0)) {
          HeapInterface::Free();
        }
        if ((local_59 < '\0') && (local_70 != 0)) {
          HeapInterface::Free();
        }
        if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
          HeapInterface::Free();
        }
        pcVar8 = (char *)FUN_00198e20(*(undefined8 *)((long)&__DT_RELA[0xd40].r_addend + *param_1));
        cVar26 = local_e1;
        if (pcVar8[0x17] < '\0') {
          pcVar8 = *(char **)pcVar8;
        }
        pcVar9 = pcVar8;
        if (*pcVar8 == '\0') {
          __n = 0;
        }
        else {
          do {
            pcVar9 = pcVar9 + 1;
          } while (*pcVar9 != '\0');
          __n = (long)pcVar9 - (long)pcVar8;
        }
        if (local_e1 < '\0') {
          if (__n == local_f0) {
            puVar15 = (undefined1 *)CONCAT71(uStack_f7,local_f8);
            goto LAB_00199b65;
          }
          if (local_c1 < '\0') goto LAB_00199b84;
LAB_00199ba8:
          if (CONCAT71(uStack_f7,local_f8) != 0) {
            eastl__basic_string();
          }
        }
        else {
          puVar15 = &local_f8;
          if (__n == 0x17U - (long)local_e1) {
LAB_00199b65:
            iVar6 = memcmp(puVar15,pcVar8,__n);
            if (iVar6 == 0) {
              *(int *)(lVar2 + 0x38) = iVar24;
            }
            if (local_c1 < '\0') goto LAB_00199b84;
LAB_00199b9f:
            if (cVar26 < '\0') goto LAB_00199ba8;
          }
          else if (local_c1 < '\0') {
LAB_00199b84:
            if (CONCAT71(uStack_d7,local_d8) != 0) {
              eastl__basic_string();
              cVar26 = local_e1;
            }
            goto LAB_00199b9f;
          }
        }
        iVar22 = iVar22 + 1;
      } while (sVar5 != iVar22);
      local_158 = (long *)((long)local_168 - (long)local_178);
      local_160 = (long *)(((long)local_158 >> 4) * -0x3333333333333333);
    }
    plVar17 = *(long **)(lVar2 + 0x48);
    plVar23 = *(long **)(lVar2 + 0x40);
    if (plVar17 != plVar23) {
      plVar11 = plVar23 + 10;
      uVar20 = (int)((ulong)((long)(plVar23 +
                                   ((((ulong)((long)plVar17 - (long)plVar11) >> 4) *
                                     0xccccccccccccccd & 0xfffffffffffffff) * 5 + 5) * 2) -
                            (long)plVar11) >> 4) * -0x33333333 & 3;
      plVar14 = plVar11;
      plVar13 = plVar23;
      if (uVar20 == 0) goto LAB_00199a89;
      if ((*(char *)((long)plVar23 + 0x4f) < '\0') && (plVar23[7] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x2f) < '\0') && (plVar23[3] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x17) < '\0') && (*plVar23 != 0)) {
        HeapInterface::Free();
      }
      plVar14 = plVar23 + 0x14;
      plVar13 = plVar11;
      if (uVar20 == 1) goto LAB_00199a89;
      plVar13 = plVar14;
      plVar12 = plVar11;
      if (uVar20 != 2) {
        if ((*(char *)((long)plVar23 + 0x9f) < '\0') && (plVar23[0x11] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar23 + 0x7f) < '\0') && (plVar23[0xd] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar23 + 0x67) < '\0') && (*plVar11 != 0)) {
          HeapInterface::Free();
        }
        plVar13 = plVar23 + 0x1e;
        plVar12 = plVar14;
      }
      if ((*(char *)((long)plVar12 + 0x4f) < '\0') && (plVar12[7] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar12 + 0x2f) < '\0') && (plVar12[3] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar12 + 0x17) < '\0') && (*plVar12 != 0)) {
        HeapInterface::Free();
      }
      plVar14 = plVar13 + 10;
      if (-1 < *(char *)((long)plVar13 + 0x4f)) goto LAB_00199a94;
LAB_001999ba:
      if (plVar13[7] == 0) goto LAB_00199a94;
      HeapInterface::Free();
      if (-1 < *(char *)((long)plVar13 + 0x2f)) goto LAB_00199a9f;
      do {
        if (plVar13[3] == 0) goto LAB_00199a9f;
        HeapInterface::Free();
        cVar26 = *(char *)((long)plVar13 + 0x17);
        while( true ) {
          if ((cVar26 < '\0') && (*plVar13 != 0)) {
            HeapInterface::Free();
          }
          if (plVar14 ==
              plVar23 + ((((ulong)((long)plVar17 - (long)plVar11) >> 4) * 0xccccccccccccccd &
                         0xfffffffffffffff) * 5 + 5) * 2) {
            plVar23 = *(long **)(lVar2 + 0x40);
            goto LAB_00199bfe;
          }
          if ((*(char *)((long)plVar14 + 0x4f) < '\0') && (plVar14[7] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar14 + 0x2f) < '\0') && (plVar14[3] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar14 + 0x17) < '\0') && (*plVar14 != 0)) {
            HeapInterface::Free();
            cVar26 = *(char *)((long)plVar14 + 0x9f);
          }
          else {
            cVar26 = *(char *)((long)plVar14 + 0x9f);
          }
          if ((cVar26 < '\0') && (plVar14[0x11] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar14 + 0x7f) < '\0') && (plVar14[0xd] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar14 + 0x67) < '\0') && (plVar14[10] != 0)) {
            HeapInterface::Free();
            cVar26 = *(char *)((long)plVar14 + 0xef);
          }
          else {
            cVar26 = *(char *)((long)plVar14 + 0xef);
          }
          if ((cVar26 < '\0') && (plVar14[0x1b] != 0)) {
            HeapInterface::Free();
          }
          plVar13 = plVar14 + 0x1e;
          if ((*(char *)((long)plVar14 + 0xcf) < '\0') && (plVar14[0x17] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar14 + 0xb7) < '\0') && (plVar14[0x14] != 0)) {
            HeapInterface::Free();
          }
          plVar14 = plVar14 + 0x28;
LAB_00199a89:
          if (*(char *)((long)plVar13 + 0x4f) < '\0') goto LAB_001999ba;
LAB_00199a94:
          if (*(char *)((long)plVar13 + 0x2f) < '\0') break;
LAB_00199a9f:
          cVar26 = *(char *)((long)plVar13 + 0x17);
        }
      } while( true );
    }
LAB_00199bfe:
    *(long **)(lVar2 + 0x48) = plVar23;
    if ((long *)((*(long *)(lVar2 + 0x50) - (long)plVar23 >> 4) * -0x3333333333333333) < local_160)
    {
      if (local_160 == (long *)0x0) {
        local_170 = (long *)0x0;
        local_140 = (long *)0x0;
      }
      else {
        local_170 = (long *)FUN_00c29480(local_158);
        plVar23 = *(long **)(lVar2 + 0x40);
        local_140 = local_170;
        if (*(long **)(lVar2 + 0x48) != plVar23) {
          uVar19 = ((ulong)((long)*(long **)(lVar2 + 0x48) - (long)(plVar23 + 10)) >> 4) *
                   0xccccccccccccccd & 0xfffffffffffffff;
          uVar20 = (int)uVar19 + 1U & 7;
          plVar17 = local_170;
          if (uVar20 == 0) goto LAB_0019ab40;
          if (uVar20 != 1) {
            plVar11 = local_170;
            plVar14 = plVar23;
            if (uVar20 != 2) {
              if (uVar20 != 3) {
                if (uVar20 != 4) {
                  if (uVar20 != 5) {
                    if (uVar20 != 6) {
                      plVar11 = local_170 + 10;
                      plVar14 = plVar23 + 10;
                      FUN_00126150(local_170,plVar23);
                    }
                    plVar23 = plVar14 + 10;
                    plVar17 = plVar11 + 10;
                    FUN_00126150(plVar11,plVar14);
                  }
                  plVar14 = plVar23 + 10;
                  plVar11 = plVar17 + 10;
                  FUN_00126150(plVar17,plVar23);
                }
                plVar23 = plVar14 + 10;
                plVar17 = plVar11 + 10;
                FUN_00126150(plVar11,plVar14);
              }
              plVar14 = plVar23 + 10;
              plVar11 = plVar17 + 10;
              FUN_00126150(plVar17,plVar23);
            }
            plVar23 = plVar14 + 10;
            plVar17 = plVar11 + 10;
            FUN_00126150(plVar11,plVar14);
          }
          FUN_00126150(plVar17,plVar23);
          plVar23 = plVar23 + 10;
          for (plVar17 = plVar17 + 10; plVar17 != local_170 + uVar19 * 10 + 10;
              plVar17 = plVar17 + 0x50) {
LAB_0019ab40:
            FUN_00126150(plVar17,plVar23);
            FUN_00126150(plVar17 + 10,plVar23 + 10);
            FUN_00126150(plVar17 + 0x14,plVar23 + 0x14);
            FUN_00126150(plVar17 + 0x1e,plVar23 + 0x1e);
            FUN_00126150(plVar17 + 0x28,plVar23 + 0x28);
            FUN_00126150(plVar17 + 0x32,plVar23 + 0x32);
            FUN_00126150(plVar17 + 0x3c,plVar23 + 0x3c);
            plVar11 = plVar23 + 0x46;
            plVar23 = plVar23 + 0x50;
            FUN_00126150(plVar17 + 0x46,plVar11);
          }
          local_140 = local_170 + (uVar19 * 5 + 5) * 2;
          plVar23 = *(long **)(lVar2 + 0x40);
          if (*(long **)(lVar2 + 0x48) != plVar23) {
            plVar17 = plVar23 + 10;
            uVar19 = (long)*(long **)(lVar2 + 0x48) - (long)plVar17;
            uVar20 = (int)((ulong)((long)(plVar23 +
                                         (((uVar19 >> 4) * 0xccccccccccccccd & 0xfffffffffffffff) *
                                          5 + 5) * 2) - (long)plVar17) >> 4) * -0x33333333 & 7;
            plVar14 = plVar17;
            plVar11 = plVar23;
            if (uVar20 != 0) {
              FUN_0047fb60(plVar23);
              plVar13 = plVar23 + 0x14;
              plVar14 = plVar13;
              plVar11 = plVar17;
              if (uVar20 != 1) {
                if (uVar20 != 2) {
                  plVar11 = plVar13;
                  if (uVar20 != 3) {
                    plVar11 = plVar17;
                    if (uVar20 != 4) {
                      plVar11 = plVar13;
                      if (uVar20 != 5) {
                        plVar11 = plVar17;
                        if (uVar20 != 6) {
                          FUN_0047fb60(plVar17);
                          plVar11 = plVar13;
                          plVar13 = plVar23 + 0x1e;
                        }
                        plVar17 = plVar13;
                        FUN_0047fb60(plVar11);
                        plVar11 = plVar17 + 10;
                      }
                      FUN_0047fb60(plVar17);
                      plVar13 = plVar11 + 10;
                    }
                    plVar17 = plVar13;
                    FUN_0047fb60(plVar11);
                    plVar11 = plVar17 + 10;
                  }
                  FUN_0047fb60(plVar17);
                  plVar13 = plVar11 + 10;
                }
                FUN_0047fb60(plVar11);
                plVar14 = plVar13 + 10;
                plVar11 = plVar13;
              }
            }
            for (; FUN_0047fb60(plVar11),
                plVar14 !=
                plVar23 + (((uVar19 >> 4) * 0xccccccccccccccd & 0xfffffffffffffff) * 5 + 5) * 2;
                plVar14 = plVar14 + 0x50) {
              FUN_0047fb60(plVar14);
              FUN_0047fb60(plVar14 + 10);
              FUN_0047fb60(plVar14 + 0x14);
              FUN_0047fb60(plVar14 + 0x1e);
              FUN_0047fb60(plVar14 + 0x28);
              plVar11 = plVar14 + 0x46;
              FUN_0047fb60(plVar14 + 0x32);
              FUN_0047fb60(plVar14 + 0x3c);
            }
            plVar23 = *(long **)(lVar2 + 0x40);
          }
        }
      }
      if (plVar23 != (long *)0x0) {
        HeapInterface::Free(plVar23);
      }
      *(long **)(lVar2 + 0x40) = local_170;
      *(long **)(lVar2 + 0x48) = local_140;
      *(long *)(lVar2 + 0x50) = (long)local_158 + (long)local_170;
      plVar23 = local_170;
      if (local_168 != (qword *)local_178) {
        local_148 = (long)local_140 - (long)local_170;
        local_138 = (long *)((local_148 >> 4) * -0x3333333333333333);
        if (local_160 <=
            (long *)((((long)local_158 + (long)local_170) - (long)local_140 >> 4) *
                    -0x3333333333333333)) {
          if (local_138 <= local_160) goto LAB_0019a1b0;
          plVar17 = (long *)((long)local_140 + -(long)local_158);
          if (plVar17 != local_140) {
            uVar19 = ((ulong)((long)local_140 - (long)(plVar17 + 10)) >> 4) * 0xccccccccccccccd &
                     0xfffffffffffffff;
            plVar23 = local_140 + uVar19 * 10 + 10;
            uVar20 = (int)uVar19 + 1U & 7;
            if (uVar20 == 0) goto LAB_0019bb3e;
            if (uVar20 != 1) {
              plVar11 = local_140;
              plVar14 = plVar17;
              if (uVar20 != 2) {
                if (uVar20 != 3) {
                  if (uVar20 != 4) {
                    if (uVar20 != 5) {
                      plVar14 = local_140;
                      plVar11 = plVar17;
                      if (uVar20 != 6) {
                        plVar11 = plVar17 + 10;
                        plVar14 = local_140 + 10;
                        FUN_00126150(local_140,plVar17);
                      }
                      plVar17 = plVar11 + 10;
                      local_140 = plVar14 + 10;
                      FUN_00126150(plVar14,plVar11);
                    }
                    plVar14 = plVar17 + 10;
                    plVar11 = local_140 + 10;
                    FUN_00126150(local_140,plVar17);
                  }
                  plVar17 = plVar14 + 10;
                  local_140 = plVar11 + 10;
                  FUN_00126150(plVar11,plVar14);
                }
                plVar14 = plVar17 + 10;
                plVar11 = local_140 + 10;
                FUN_00126150(local_140,plVar17);
              }
              plVar17 = plVar14 + 10;
              local_140 = plVar11 + 10;
              FUN_00126150(plVar11,plVar14);
            }
            FUN_00126150(local_140,plVar17);
            plVar17 = plVar17 + 10;
            for (local_140 = local_140 + 10; plVar23 != local_140; local_140 = local_140 + 0x50) {
LAB_0019bb3e:
              FUN_00126150(local_140,plVar17);
              FUN_00126150(local_140 + 10,plVar17 + 10);
              FUN_00126150(local_140 + 0x14,plVar17 + 0x14);
              FUN_00126150(local_140 + 0x1e,plVar17 + 0x1e);
              FUN_00126150(local_140 + 0x28,plVar17 + 0x28);
              FUN_00126150(local_140 + 0x32,plVar17 + 0x32);
              FUN_00126150(local_140 + 0x3c,plVar17 + 0x3c);
              plVar11 = plVar17 + 0x46;
              plVar17 = plVar17 + 0x50;
              FUN_00126150(local_140 + 0x46,plVar11);
            }
            plVar17 = *(long **)(lVar2 + 0x48);
            local_140 = (long *)(-(long)local_158 + (long)plVar17);
          }
          uVar19 = ((long)local_140 - (long)local_170 >> 4) * -0x3333333333333333;
          if (0 < (long)local_140 - (long)local_170) {
            plVar23 = local_140;
            uVar18 = uVar19;
            plVar11 = plVar17;
            if ((uVar19 & 1) != 0) {
              plVar23 = local_140 + -10;
              plVar11 = plVar17 + -10;
              FUN_00126110(plVar11,plVar23);
              FUN_00126110(plVar17 + -7,local_140 + -7);
              *(int *)(plVar17 + -4) = (int)local_140[-4];
              *(undefined4 *)((long)plVar17 + -0x1c) = *(undefined4 *)((long)local_140 + -0x1c);
              FUN_00126110(plVar17 + -3,local_140 + -3);
              uVar18 = uVar19 - 1;
              if (uVar19 - 1 == 0) goto LAB_0019bd07;
            }
            do {
              FUN_00126110(plVar11 + -10,plVar23 + -10);
              FUN_00126110(plVar11 + -7,plVar23 + -7);
              *(int *)(plVar11 + -4) = (int)plVar23[-4];
              *(undefined4 *)((long)plVar11 + -0x1c) = *(undefined4 *)((long)plVar23 + -0x1c);
              FUN_00126110(plVar11 + -3,plVar23 + -3);
              FUN_00126110(plVar11 + -0x14,plVar23 + -0x14);
              FUN_00126110(plVar11 + -0x11,plVar23 + -0x11);
              *(int *)(plVar11 + -0xe) = (int)plVar23[-0xe];
              *(undefined4 *)((long)plVar11 + -0x6c) = *(undefined4 *)((long)plVar23 + -0x6c);
              FUN_00126110(plVar11 + -0xd,plVar23 + -0xd);
              uVar18 = uVar18 - 2;
              plVar23 = plVar23 + -0x14;
              plVar11 = plVar11 + -0x14;
            } while (uVar18 != 0);
          }
LAB_0019bd07:
          if (0 < (long)local_158) {
            plVar17 = (long *)((long)local_160 + -1);
            plVar23 = local_178;
            if (((ulong)local_160 & 1) != 0) {
              FUN_00493e00(local_170,local_178);
              FUN_00493e00(local_170 + 3,local_178 + 3);
              *(int *)(local_170 + 6) = (int)local_178[6];
              *(undefined4 *)((long)local_170 + 0x34) = *(undefined4 *)((long)local_178 + 0x34);
              FUN_00493e00(local_170 + 7,local_178 + 7);
              plVar23 = local_178 + 10;
              local_160 = plVar17;
              local_170 = local_170 + 10;
              if (plVar17 == (long *)0x0) goto LAB_0019a555;
            }
            do {
              FUN_00493e00(local_170,plVar23);
              FUN_00493e00(local_170 + 3,plVar23 + 3);
              *(int *)(local_170 + 6) = (int)plVar23[6];
              *(undefined4 *)((long)local_170 + 0x34) = *(undefined4 *)((long)plVar23 + 0x34);
              FUN_00493e00(local_170 + 7,plVar23 + 7);
              FUN_00493e00(local_170 + 10,plVar23 + 10);
              FUN_00493e00(local_170 + 0xd,plVar23 + 0xd);
              *(int *)(local_170 + 0x10) = (int)plVar23[0x10];
              *(undefined4 *)((long)local_170 + 0x84) = *(undefined4 *)((long)plVar23 + 0x84);
              FUN_00493e00(local_170 + 0x11,plVar23 + 0x11);
              local_160 = (long *)((long)local_160 + -2);
              plVar23 = plVar23 + 0x14;
              local_170 = local_170 + 0x14;
            } while (local_160 != (long *)0x0);
          }
          goto LAB_0019a555;
        }
        pcVar8 = (char *)((local_148 >> 4) * -0x6666666666666666);
        if (local_138 == (long *)0x0) {
          pcVar8 = Elf64_Ehdr_00000000.e_ident_magic_str;
        }
        pcVar9 = (char *)((long)local_138 + (long)local_160);
        if ((char *)((long)local_138 + (long)local_160) < pcVar8) {
          pcVar9 = pcVar8;
        }
        if (pcVar9 == (char *)0x0) {
          local_150 = (undefined1 *)0x0;
          local_160 = (long *)0x0;
        }
        else {
          local_160 = (long *)FUN_00c29480((long)pcVar9 * 0x50);
          plVar17 = *(long **)(lVar2 + 0x40);
          local_150 = (undefined1 *)local_160;
          if (plVar17 != local_170) {
            uVar19 = ((ulong)((long)local_170 - (long)(plVar17 + 10)) >> 4) * 0xccccccccccccccd &
                     0xfffffffffffffff;
            uVar20 = (int)uVar19 + 1U & 7;
            puVar15 = (undefined1 *)local_160;
            if (uVar20 == 0) goto LAB_0019af39;
            if (uVar20 != 1) {
              puVar16 = (undefined1 *)local_160;
              plVar23 = plVar17;
              if (uVar20 != 2) {
                if (uVar20 != 3) {
                  if (uVar20 != 4) {
                    if (uVar20 != 5) {
                      if (uVar20 != 6) {
                        puVar16 = (undefined1 *)((long)local_160 + 0x50);
                        plVar23 = plVar17 + 10;
                        FUN_00126150(local_160,plVar17);
                      }
                      plVar17 = plVar23 + 10;
                      puVar15 = puVar16 + 0x50;
                      FUN_00126150(puVar16,plVar23);
                    }
                    plVar23 = plVar17 + 10;
                    puVar16 = puVar15 + 0x50;
                    FUN_00126150(puVar15,plVar17);
                  }
                  plVar17 = plVar23 + 10;
                  puVar15 = puVar16 + 0x50;
                  FUN_00126150(puVar16,plVar23);
                }
                plVar23 = plVar17 + 10;
                puVar16 = puVar15 + 0x50;
                FUN_00126150(puVar15,plVar17);
              }
              plVar17 = plVar23 + 10;
              puVar15 = puVar16 + 0x50;
              FUN_00126150(puVar16,plVar23);
            }
            FUN_00126150(puVar15,plVar17);
            plVar17 = plVar17 + 10;
            for (puVar15 = puVar15 + 0x50;
                puVar15 != (undefined1 *)((long)local_160 + uVar19 * 0x50 + 0x50);
                puVar15 = puVar15 + 0x280) {
LAB_0019af39:
              FUN_00126150(puVar15,plVar17);
              FUN_00126150(puVar15 + 0x50,plVar17 + 10);
              FUN_00126150(puVar15 + 0xa0,plVar17 + 0x14);
              FUN_00126150(puVar15 + 0xf0,plVar17 + 0x1e);
              FUN_00126150(puVar15 + 0x140,plVar17 + 0x28);
              FUN_00126150(puVar15 + 400,plVar17 + 0x32);
              FUN_00126150(puVar15 + 0x1e0,plVar17 + 0x3c);
              plVar23 = plVar17 + 0x46;
              plVar17 = plVar17 + 0x50;
              FUN_00126150(puVar15 + 0x230,plVar23);
            }
            local_150 = (undefined1 *)((long)local_160 + (uVar19 * 5 + 5) * 0x10);
          }
        }
        uVar19 = ((ulong)((long)local_168 - (long)(local_178 + 10)) >> 4) * 0xccccccccccccccd &
                 0xfffffffffffffff;
        puVar15 = local_150;
        plVar17 = local_178;
        do {
          *puVar15 = 0;
          puVar15[0x17] = 0x17;
          if (*(char *)((long)plVar17 + 0x17) < '\0') {
            puVar16 = (undefined1 *)(plVar17[1] + *plVar17);
            plVar23 = (long *)*plVar17;
          }
          else {
            puVar16 = (undefined1 *)((0x17 - (long)*(char *)((long)plVar17 + 0x17)) + (long)plVar17)
            ;
            plVar23 = plVar17;
          }
          FUN_0048d690(puVar15,plVar23,puVar16);
          puVar15[0x18] = 0;
          puVar15[0x2f] = 0x17;
          if (*(char *)((long)plVar17 + 0x2f) < '\0') {
            plVar23 = (long *)plVar17[3];
            lVar21 = plVar17[4] + (long)plVar23;
          }
          else {
            plVar23 = plVar17 + 3;
            lVar21 = (0x17 - (long)*(char *)((long)plVar17 + 0x2f)) + (long)plVar23;
          }
          FUN_0048d690(puVar15 + 0x18,plVar23,lVar21);
          puVar16 = puVar15 + 0x38;
          *(int *)(puVar15 + 0x30) = (int)plVar17[6];
          uVar1 = *(undefined4 *)((long)plVar17 + 0x34);
          puVar15[0x38] = 0;
          puVar15[0x4f] = 0x17;
          *(undefined4 *)(puVar15 + 0x34) = uVar1;
          if (*(char *)((long)plVar17 + 0x4f) < '\0') {
            plVar23 = (long *)plVar17[7];
            lVar21 = plVar17[8] + (long)plVar23;
          }
          else {
            plVar23 = plVar17 + 7;
            lVar21 = (0x17 - (long)*(char *)((long)plVar17 + 0x4f)) + (long)plVar23;
          }
          plVar17 = plVar17 + 10;
          puVar15 = puVar15 + 0x50;
          FUN_0048d690(puVar16,plVar23,lVar21);
        } while (plVar17 != local_178 + 10 + uVar19 * 10);
        plVar17 = *(long **)(lVar2 + 0x48);
        local_150 = local_150 + (uVar19 * 5 + 5) * 0x10;
        if (plVar17 != local_170) {
          uVar19 = ((ulong)((long)plVar17 - (long)(local_170 + 10)) >> 4) * 0xccccccccccccccd &
                   0xfffffffffffffff;
          uVar20 = (int)uVar19 + 1U & 7;
          puVar15 = local_150;
          if (uVar20 == 0) goto LAB_0019b241;
          plVar17 = local_170;
          if (uVar20 != 1) {
            puVar16 = local_150;
            if (uVar20 != 2) {
              if (uVar20 != 3) {
                if (uVar20 != 4) {
                  if (uVar20 != 5) {
                    if (uVar20 != 6) {
                      FUN_00126150(local_150,local_170);
                      puVar16 = local_150 + 0x50;
                      local_170 = local_170 + 10;
                    }
                    plVar17 = local_170 + 10;
                    puVar15 = puVar16 + 0x50;
                    FUN_00126150(puVar16,local_170);
                  }
                  local_170 = plVar17 + 10;
                  puVar16 = puVar15 + 0x50;
                  FUN_00126150(puVar15,plVar17);
                }
                plVar17 = local_170 + 10;
                puVar15 = puVar16 + 0x50;
                FUN_00126150(puVar16,local_170);
              }
              local_170 = plVar17 + 10;
              puVar16 = puVar15 + 0x50;
              FUN_00126150(puVar15,plVar17);
            }
            plVar17 = local_170 + 10;
            puVar15 = puVar16 + 0x50;
            FUN_00126150(puVar16,local_170);
          }
          FUN_00126150(puVar15,plVar17);
          local_170 = plVar17 + 10;
          for (puVar15 = puVar15 + 0x50; puVar15 != local_150 + uVar19 * 0x50 + 0x50;
              puVar15 = puVar15 + 0x280) {
LAB_0019b241:
            FUN_00126150(puVar15,local_170);
            FUN_00126150(puVar15 + 0x50,local_170 + 10);
            FUN_00126150(puVar15 + 0xa0,local_170 + 0x14);
            FUN_00126150(puVar15 + 0xf0,local_170 + 0x1e);
            FUN_00126150(puVar15 + 0x140,local_170 + 0x28);
            FUN_00126150(puVar15 + 400,local_170 + 0x32);
            FUN_00126150(puVar15 + 0x1e0,local_170 + 0x3c);
            plVar17 = local_170 + 0x46;
            local_170 = local_170 + 0x50;
            FUN_00126150(puVar15 + 0x230,plVar17);
          }
          plVar17 = *(long **)(lVar2 + 0x48);
          local_150 = local_150 + (uVar19 * 5 + 5) * 0x10;
        }
        plVar23 = *(long **)(lVar2 + 0x40);
        if (plVar23 != plVar17) {
          plVar11 = plVar23 + 10;
          uVar19 = (long)plVar17 - (long)plVar11;
          uVar20 = (int)((ulong)((long)(plVar23 +
                                       (((uVar19 >> 4) * 0xccccccccccccccd & 0xfffffffffffffff) * 5
                                       + 5) * 2) - (long)plVar11) >> 4) * -0x33333333 & 7;
          plVar17 = plVar23;
          plVar14 = plVar11;
          if (uVar20 != 0) {
            FUN_0047fb60();
            plVar14 = plVar23 + 0x14;
            plVar17 = plVar11;
            if (uVar20 != 1) {
              plVar13 = plVar11;
              plVar17 = plVar14;
              if (uVar20 != 2) {
                plVar13 = plVar14;
                if (uVar20 != 3) {
                  plVar17 = plVar11;
                  if (uVar20 != 4) {
                    plVar17 = plVar14;
                    if (uVar20 != 5) {
                      plVar17 = plVar11;
                      if (uVar20 != 6) {
                        FUN_0047fb60(plVar11);
                        plVar17 = plVar14;
                        plVar14 = plVar23 + 0x1e;
                      }
                      plVar11 = plVar14;
                      FUN_0047fb60(plVar17);
                      plVar17 = plVar11 + 10;
                    }
                    FUN_0047fb60(plVar11);
                    plVar14 = plVar17 + 10;
                  }
                  plVar11 = plVar14;
                  FUN_0047fb60(plVar17);
                  plVar13 = plVar11 + 10;
                }
                FUN_0047fb60(plVar11);
                plVar17 = plVar13 + 10;
              }
              FUN_0047fb60(plVar13);
              plVar14 = plVar17 + 10;
            }
          }
          for (; FUN_0047fb60(plVar17),
              plVar14 !=
              plVar23 + (((uVar19 >> 4) * 0xccccccccccccccd & 0xfffffffffffffff) * 5 + 5) * 2;
              plVar14 = plVar14 + 0x50) {
            FUN_0047fb60(plVar14);
            FUN_0047fb60(plVar14 + 10);
            FUN_0047fb60(plVar14 + 0x14);
            FUN_0047fb60(plVar14 + 0x1e);
            FUN_0047fb60(plVar14 + 0x28);
            plVar17 = plVar14 + 0x46;
            FUN_0047fb60(plVar14 + 0x32);
            FUN_0047fb60(plVar14 + 0x3c);
          }
          plVar17 = *(long **)(lVar2 + 0x40);
        }
        if (plVar17 != (long *)0x0) {
          HeapInterface::Free(plVar17);
        }
        *(undefined1 **)(lVar2 + 0x48) = local_150;
        *(long **)(lVar2 + 0x40) = local_160;
        *(undefined1 **)(lVar2 + 0x50) = (undefined1 *)((long)local_160 + (long)pcVar9 * 0x50);
        goto LAB_0019b47c;
      }
LAB_00199c3d:
      FUN_0018c7e0(plVar23,local_140);
      if (*(long *)(lVar2 + 0x68) != 0) {
        (**(code **)(lVar2 + 0x70))(lVar2 + 0x58);
      }
    }
    else {
      local_140 = plVar23;
      if (local_168 == (qword *)local_178) goto LAB_00199c3d;
      local_148 = 0;
      local_138 = (long *)0x0;
      local_170 = plVar23;
LAB_0019a1b0:
      plVar23 = (long *)((long)local_178 + local_148);
      plVar17 = local_140;
      if (local_168 != (qword *)plVar23) {
        plVar17 = plVar23 + 10;
        plVar11 = local_140 + 10;
        plVar12 = plVar11 + (((ulong)((long)local_168 - (long)plVar17) >> 4) * 0xccccccccccccccd &
                            0xfffffffffffffff) * 10;
        plVar14 = local_140;
        plVar13 = plVar23;
        if ((((ulong)((long)plVar12 + (-0x50 - (long)local_140)) >> 4) * 0xccccccccccccccd & 1) != 0
           ) goto LAB_0019a695;
        FUN_0048dcd0(local_140,plVar23);
        FUN_0048dcd0(local_140 + 3,plVar23 + 3);
        *(int *)(local_140 + 6) = (int)plVar23[6];
        *(undefined4 *)((long)local_140 + 0x34) = *(undefined4 *)((long)plVar23 + 0x34);
        FUN_0048dcd0(local_140 + 7,plVar23 + 7);
        while (plVar14 = plVar11, plVar13 = plVar17, plVar11 != plVar12) {
LAB_0019a695:
          FUN_0048dcd0(plVar14,plVar13);
          FUN_0048dcd0(plVar14 + 3,plVar13 + 3);
          *(int *)(plVar14 + 6) = (int)plVar13[6];
          *(undefined4 *)((long)plVar14 + 0x34) = *(undefined4 *)((long)plVar13 + 0x34);
          FUN_0048dcd0(plVar14 + 7,plVar13 + 7);
          FUN_0048dcd0(plVar14 + 10,plVar13 + 10);
          FUN_0048dcd0(plVar14 + 0xd,plVar13 + 0xd);
          plVar17 = plVar13 + 0x14;
          *(int *)(plVar14 + 0x10) = (int)plVar13[0x10];
          *(undefined4 *)((long)plVar14 + 0x84) = *(undefined4 *)((long)plVar13 + 0x84);
          FUN_0048dcd0(plVar14 + 0x11,plVar13 + 0x11);
          plVar11 = plVar14 + 0x14;
        }
        plVar17 = *(long **)(lVar2 + 0x48);
      }
      plVar11 = plVar17 + ((long)local_160 - (long)local_138) * 10;
      if (plVar17 != local_170) {
        plVar14 = local_170 + 10;
        plVar17 = plVar14 + (((ulong)((long)plVar17 - (long)plVar14) >> 4) * 0xccccccccccccccd &
                            0xfffffffffffffff) * 10;
        uVar20 = (int)((ulong)((long)plVar17 + (-0x50 - (long)local_170)) >> 4) * -0x33333333 + 1U &
                 7;
        if (uVar20 != 0) {
          plVar13 = local_170;
          if (uVar20 != 1) {
            plVar12 = plVar11;
            if (uVar20 != 2) {
              if (uVar20 != 3) {
                if (uVar20 != 4) {
                  if (uVar20 != 5) {
                    if (uVar20 != 6) {
                      FUN_00126150(plVar11,local_170);
                      plVar12 = plVar11 + 10;
                      local_170 = plVar14;
                    }
                    plVar13 = local_170 + 10;
                    plVar11 = plVar12 + 10;
                    FUN_00126150(plVar12,local_170);
                  }
                  local_170 = plVar13 + 10;
                  plVar12 = plVar11 + 10;
                  FUN_00126150(plVar11,plVar13);
                }
                plVar13 = local_170 + 10;
                plVar11 = plVar12 + 10;
                FUN_00126150(plVar12,local_170);
              }
              local_170 = plVar13 + 10;
              plVar12 = plVar11 + 10;
              FUN_00126150(plVar11,plVar13);
            }
            plVar13 = local_170 + 10;
            plVar11 = plVar12 + 10;
            FUN_00126150(plVar12,local_170);
          }
          local_170 = plVar13 + 10;
          FUN_00126150(plVar11,plVar13);
          plVar11 = plVar11 + 10;
          if (plVar17 == local_170) goto LAB_0019a453;
        }
        do {
          FUN_00126150(plVar11,local_170);
          FUN_00126150(plVar11 + 10,local_170 + 10);
          FUN_00126150(plVar11 + 0x14,local_170 + 0x14);
          FUN_00126150(plVar11 + 0x1e,local_170 + 0x1e);
          FUN_00126150(plVar11 + 0x28,local_170 + 0x28);
          FUN_00126150(plVar11 + 0x32,local_170 + 0x32);
          FUN_00126150(plVar11 + 0x3c,local_170 + 0x3c);
          plVar13 = local_170 + 0x46;
          plVar14 = plVar11 + 0x46;
          local_170 = local_170 + 0x50;
          plVar11 = plVar11 + 0x50;
          FUN_00126150(plVar14,plVar13);
        } while (plVar17 != local_170);
      }
LAB_0019a453:
      if (0 < local_148) {
        plVar17 = local_138;
        plVar11 = plVar23;
        plVar14 = local_140;
        if (((ulong)local_138 & 1) != 0) {
          plVar11 = plVar23 + -10;
          plVar14 = local_140 + -10;
          FUN_00493e00(plVar14,plVar11);
          FUN_00493e00(local_140 + -7,plVar23 + -7);
          *(int *)(local_140 + -4) = (int)plVar23[-4];
          *(undefined4 *)((long)local_140 + -0x1c) = *(undefined4 *)((long)plVar23 + -0x1c);
          FUN_00493e00(local_140 + -3,plVar23 + -3);
          plVar17 = (long *)((long)local_138 + -1);
          if ((long *)((long)local_138 + -1) == (long *)0x0) goto LAB_0019a555;
        }
        do {
          FUN_00493e00(plVar14 + -10,plVar11 + -10);
          FUN_00493e00(plVar14 + -7,plVar11 + -7);
          *(int *)(plVar14 + -4) = (int)plVar11[-4];
          *(undefined4 *)((long)plVar14 + -0x1c) = *(undefined4 *)((long)plVar11 + -0x1c);
          FUN_00493e00(plVar14 + -3,plVar11 + -3);
          FUN_00493e00(plVar14 + -0x14,plVar11 + -0x14);
          FUN_00493e00(plVar14 + -0x11,plVar11 + -0x11);
          *(int *)(plVar14 + -0xe) = (int)plVar11[-0xe];
          *(undefined4 *)((long)plVar14 + -0x6c) = *(undefined4 *)((long)plVar11 + -0x6c);
          FUN_00493e00(plVar14 + -0xd,plVar11 + -0xd);
          plVar17 = (long *)((long)plVar17 + -2);
          plVar11 = plVar11 + -0x14;
          plVar14 = plVar14 + -0x14;
        } while (plVar17 != (long *)0x0);
      }
LAB_0019a555:
      local_150 = (undefined1 *)((long)local_158 + *(long *)(lVar2 + 0x48));
      local_160 = *(long **)(lVar2 + 0x40);
      *(undefined1 **)(lVar2 + 0x48) = local_150;
LAB_0019b47c:
      local_158 = local_178 + 10;
      FUN_0018c7e0(local_160,local_150);
      if (*(long *)(lVar2 + 0x68) != 0) {
        (**(code **)(lVar2 + 0x70))(lVar2 + 0x58);
      }
      lVar21 = (((ulong)((long)local_168 - (long)local_158) >> 4) * 0xccccccccccccccd &
               0xfffffffffffffff) * 5 + 5;
      uVar20 = (int)(lVar21 * 0x10 - 0x50U >> 4) * -0x33333333 + 1U & 3;
      plVar17 = local_178;
      if (uVar20 == 0) goto LAB_0019b62b;
      plVar23 = local_178;
      if (uVar20 != 1) {
        if (uVar20 != 2) {
          if ((*(char *)((long)local_178 + 0x4f) < '\0') && (local_178[7] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)local_178 + 0x2f) < '\0') && (local_178[3] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)local_178 + 0x17) < '\0') && (*local_178 != 0)) {
            HeapInterface::Free();
          }
          plVar23 = local_178 + 10;
        }
        if ((*(char *)((long)plVar23 + 0x4f) < '\0') && (plVar23[7] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar23 + 0x2f) < '\0') && (plVar23[3] != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)((long)plVar23 + 0x17) < '\0') && (*plVar23 != 0)) {
          HeapInterface::Free();
        }
        plVar23 = plVar23 + 10;
      }
      if ((*(char *)((long)plVar23 + 0x4f) < '\0') && (plVar23[7] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x2f) < '\0') && (plVar23[3] != 0)) {
        HeapInterface::Free();
      }
      if ((*(char *)((long)plVar23 + 0x17) < '\0') && (*plVar23 != 0)) {
        HeapInterface::Free();
      }
      plVar17 = plVar23 + 10;
      if (plVar17 != local_178 + lVar21 * 2) {
        cVar26 = *(char *)((long)plVar23 + 0x9f);
        while( true ) {
          if ((cVar26 < '\0') && (plVar17[7] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x2f) < '\0') && (plVar17[3] != 0)) {
            HeapInterface::Free();
          }
          if (*(char *)((long)plVar17 + 0x17) < '\0') {
            if (*plVar17 != 0) {
              HeapInterface::Free();
            }
            cVar26 = *(char *)((long)plVar17 + 0x9f);
          }
          else {
            cVar26 = *(char *)((long)plVar17 + 0x9f);
          }
          if ((cVar26 < '\0') && (plVar17[0x11] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x7f) < '\0') && (plVar17[0xd] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x67) < '\0') && (plVar17[10] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0xef) < '\0') && (plVar17[0x1b] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0xcf) < '\0') && (plVar17[0x17] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0xb7) < '\0') && (plVar17[0x14] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x13f) < '\0') && (plVar17[0x25] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x11f) < '\0') && (plVar17[0x21] != 0)) {
            HeapInterface::Free();
          }
          if ((*(char *)((long)plVar17 + 0x107) < '\0') && (plVar17[0x1e] != 0)) {
            HeapInterface::Free();
          }
          plVar17 = plVar17 + 0x28;
          if (plVar17 == local_178 + lVar21 * 2) break;
LAB_0019b62b:
          cVar26 = *(char *)((long)plVar17 + 0x4f);
        }
      }
    }
    if (local_178 != (long *)0x0) {
      eastl__basic_string(local_178);
    }
  }
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
LAB_00199c70:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL` @ 0019eb50
```c

undefined * jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL(long *param_1,long param_2)

{
  undefined4 uVar1;
  long lVar2;
  undefined8 uVar3;
  undefined4 *puVar4;
  char cVar5;
  ushort uVar6;
  short sVar7;
  uint uVar8;
  long lVar9;
  undefined *puVar10;
  undefined4 *puVar11;
  long lVar12;
  char *pcVar13;
  undefined8 *puVar14;
  ulong uVar15;
  uint uVar16;
  char *local_1e8;
  ulong *local_1e0;
  undefined1 *local_1d8;
  uint local_1d0;
  int local_1cc;
  undefined1 local_1b8;
  undefined7 uStack_1b7;
  char local_1a1;
  undefined1 local_198;
  undefined7 uStack_197;
  char local_181;
  char local_178;
  undefined7 uStack_177;
  ulong local_170;
  char local_161;
  undefined1 local_158;
  undefined7 uStack_157;
  char local_141;
  long local_138 [2];
  char local_121;
  long local_118 [2];
  char local_101;
  long local_f8 [2];
  char local_e1;
  long local_d8 [2];
  char local_c1;
  undefined1 local_b8;
  undefined7 uStack_b7;
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  ulong local_78;
  char *local_70;
  char local_61;
  undefined1 local_58;
  undefined7 uStack_57;
  char local_41;
  
  lVar12 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar12 + 0x38) == *(long *)(lVar12 + 0x40)) &&
     (cVar5 = FUN_006a53c0(), cVar5 == '\0')) {
LAB_0019f2dd:
    return &DAT_015d3600;
  }
  uVar6 = FUN_00121a30(param_2);
  lVar12 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + *param_1) + 0x10) +
                    (ulong)uVar6 * 8);
  puVar14 = &DAT_013962b0;
  if (lVar12 != 0) {
    puVar14 = (undefined8 *)(lVar12 + 0x30);
  }
  if (puVar14[1] == 0) goto LAB_0019f2dd;
  uVar8 = FUN_00121a30(param_2);
  lVar12 = *(long *)(param_2 + 0x18);
  sVar7 = (short)uVar8;
  *(long *)(param_2 + 0x18) = lVar12 + 1;
  uVar15 = (ulong)*(byte *)(*(long *)(param_2 + 0x10) + lVar12);
  local_178 = '\0';
  local_161 = '\x17';
  if (sVar7 < 0) {
    uVar6 = FUN_00121a30(param_2);
    local_1d0 = (uint)uVar6;
    FUN_00506d10(&local_78,
                 *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1) + 0x1b0),uVar6
                );
    if (local_70 == (char *)0x0) {
      if (local_78 != 0) {
        ref_counter_base::DecRef();
      }
LAB_0019f3c2:
      puVar10 = &DAT_015d3600;
      goto LAB_0019f2a7;
    }
    FUN_004e0580(&local_58,local_70,param_2);
    FUN_00126110(&local_178,&local_58);
    if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
      eastl__basic_string();
    }
    if (local_78 != 0) {
      ref_counter_base::DecRef();
    }
    uVar16 = uVar8 & 0x7fff;
  }
  else {
    cVar5 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&local_178,
                         param_2);
    if (cVar5 == '\0') goto LAB_0019f3c2;
    local_58 = 0;
    local_41 = '\x17';
    if (local_161 < '\0') {
      local_70 = (char *)CONCAT71(uStack_177,local_178);
    }
    else {
      local_170 = 0x17 - (long)local_161;
      local_70 = &local_178;
    }
    local_78 = local_170;
    FUN_00133770(&local_58);
    FUN_00126110(&local_58,&local_178);
    if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
      eastl__basic_string();
    }
    uVar16 = uVar8 & 0xffff;
    local_1d0 = 0xffffffff;
  }
  local_1d8 = &local_58;
  local_1e0 = &local_78;
  local_1e8 = &local_178;
  lVar12 = puVar14[1];
  if (*(char *)(lVar12 + 0xa7) < '\0') {
    lVar9 = *(long *)(lVar12 + 0x98);
  }
  else {
    lVar9 = 0x17 - (long)*(char *)(lVar12 + 0xa7);
  }
  if (lVar9 != 0) {
    if ((&DAT_015a4aa9)[uVar15 * 0xc] == '\0') {
LAB_0019ed76:
      FUN_00145f30(local_1e8);
      FUN_00145d90(local_1e8);
      pcVar13 = local_1e8;
      if (local_161 < '\0') {
        pcVar13 = (char *)CONCAT71(uStack_177,local_178);
      }
      local_198 = 0;
      local_181 = '\x17';
      cVar5 = *pcVar13;
      while (cVar5 != '\0') {
        pcVar13 = pcVar13 + 1;
        cVar5 = *pcVar13;
      }
      FUN_0048d1a0(&local_198);
      (**(code **)(*(long *)puVar14[1] + 0x158))
                ((long *)puVar14[1],&local_198,uVar16 >> 8,uVar16 & 0xff);
      if ((&DAT_015a4aa8)[uVar15 * 0xc] == '\0') {
        local_1cc = ((int)sVar7 >> 0x1f & 0xfU) + 2;
      }
      else {
        local_1cc = (uVar8 >> 0xb & 0x10) + 1;
      }
      local_1a1 = '\x17';
      local_1b8 = 0;
      puVar11 = *(undefined4 **)((long)&__DT_SYMTAB[0xa3].st_size + puVar14[1]);
      puVar4 = *(undefined4 **)((long)&__DT_SYMTAB[0xa4].st_name + puVar14[1]);
      if (puVar11 != puVar4) {
        uVar8 = (int)((ulong)((long)puVar4 + (-8 - (long)puVar11)) >> 3) + 1U & 7;
        if (uVar8 != 0) {
          if (uVar8 != 1) {
            if (uVar8 != 2) {
              if (uVar8 != 3) {
                if (uVar8 != 4) {
                  if (uVar8 != 5) {
                    if (uVar8 != 6) {
                      if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                        FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
                      }
                      puVar11 = puVar11 + 2;
                    }
                    if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                      FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
                    }
                    puVar11 = puVar11 + 2;
                  }
                  if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                    FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
                  }
                  puVar11 = puVar11 + 2;
                }
                if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                  FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
                }
                puVar11 = puVar11 + 2;
              }
              if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
              }
              puVar11 = puVar11 + 2;
            }
            if ((*(byte *)(puVar11 + 1) & 2) != 0) {
              FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
            }
            puVar11 = puVar11 + 2;
          }
          if ((*(byte *)(puVar11 + 1) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
          }
          puVar11 = puVar11 + 2;
          if (puVar11 == puVar4) goto LAB_0019f010;
        }
        do {
          if ((*(byte *)(puVar11 + 1) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",*puVar11);
          }
          if ((*(byte *)(puVar11 + 3) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[2]);
          }
          if ((*(byte *)(puVar11 + 5) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[4]);
          }
          if ((*(byte *)(puVar11 + 7) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[6]);
          }
          if ((*(byte *)(puVar11 + 9) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[8]);
          }
          if ((*(byte *)(puVar11 + 0xb) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[10]);
          }
          if ((*(byte *)(puVar11 + 0xd) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[0xc]);
          }
          if ((*(byte *)(puVar11 + 0xf) & 2) != 0) {
            FUN_00146a20(&local_1b8,"<sprite=%d> ",puVar11[0xe]);
          }
          puVar11 = puVar11 + 0x10;
        } while (puVar11 != puVar4);
      }
LAB_0019f010:
      uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
      local_58 = 0;
      local_41 = '\x17';
      if (*(int *)(&DAT_015a4aa4 + uVar15 * 0xc) == -1) {
        FUN_00c313f0(local_1d8,&DAT_00fba3bd);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        local_78 = local_78 & 0xffffffffffffff00;
        local_61 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(local_1e0);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        local_98 = 0;
        local_81 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(&local_98);
        FUN_00af72e0(local_d8,puVar14[1]);
        FUN_00126320(&local_b8,&local_1b8,local_d8);
        ChatHistory::AddChat
                  (uVar3,local_1cc,0,&local_b8,&local_98,local_1e0,local_1e8,
                   &DAT_015a4aa0 + uVar15 * 0xc,local_1d8,local_1d0);
        if ((local_a1 < '\0') && (CONCAT71(uStack_b7,local_b8) != 0)) {
          eastl__basic_string();
        }
        if ((local_c1 < '\0') && (local_d8[0] != 0)) {
          eastl__basic_string();
        }
        if (local_81 < '\0') {
          lVar12 = CONCAT71(uStack_97,local_98);
          goto joined_r0x0019f5ec;
        }
      }
      else {
        FUN_00c313f0(local_1d8,&DAT_00fba3bd);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        local_78 = local_78 & 0xffffffffffffff00;
        local_61 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(local_1e0);
        lVar9 = puVar14[1];
        lVar12 = lVar9 + 0x90;
        if (*(char *)(lVar9 + 0xa7) < '\0') {
          lVar12 = *(long *)(lVar9 + 0x90);
        }
        local_b8 = 0;
        local_a1 = '\x17';
        uVar1 = *(undefined4 *)(&DAT_015a4aa4 + uVar15 * 0xc);
        FUN_001335d0(&local_b8,8);
        FUN_00146960(&local_b8,"<img=%d>",uVar1);
        FUN_0048d130(&local_98,&local_b8,lVar12);
        FUN_00af72e0(local_f8,puVar14[1]);
        local_158 = 0;
        local_141 = '\x17';
        uVar1 = *(undefined4 *)(&DAT_015a4aa4 + uVar15 * 0xc);
        FUN_001335d0(&local_158,8);
        FUN_00146960(&local_158,"<img=%d>",uVar1);
        FUN_0048d130(local_138,&local_158,&DAT_00fb3ff6);
        FUN_001261f0(local_118,local_138,&local_1b8);
        FUN_001261f0(local_d8,local_118,local_f8);
        ChatHistory::AddChat
                  (uVar3,local_1cc,0,local_d8,&local_98,local_1e0,local_1e8,
                   &DAT_015a4aa0 + uVar15 * 0xc,local_1d8,local_1d0);
        if ((local_c1 < '\0') && (local_d8[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_101 < '\0') && (local_118[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_121 < '\0') && (local_138[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_141 < '\0') && (CONCAT71(uStack_157,local_158) != 0)) {
          eastl__basic_string();
        }
        if ((local_e1 < '\0') && (local_f8[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
          eastl__basic_string();
        }
        if (local_a1 < '\0') {
          lVar12 = CONCAT71(uStack_b7,local_b8);
joined_r0x0019f5ec:
          if (lVar12 != 0) {
            eastl__basic_string();
          }
        }
      }
      if ((local_61 < '\0') && (local_78 != 0)) {
        eastl__basic_string();
      }
      if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
        eastl__basic_string();
      }
      if ((local_1a1 < '\0') && (CONCAT71(uStack_1b7,local_1b8) != 0)) {
        eastl__basic_string();
      }
      if ((local_181 < '\0') && (CONCAT71(uStack_197,local_198) != 0)) {
        eastl__basic_string();
      }
    }
    else {
      lVar9 = *param_1;
      if ((sVar7 < 0) ||
         ((lVar2 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar9), lVar2 != 0 &&
          (((*(char *)(lVar2 + 0x10) == '\0' || (*(char *)(lVar2 + 0x19) != '\0')) &&
           (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar9) + 8) == '\0')))))) {
        uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar9);
        pcVar13 = (char *)(lVar12 + 0xf8);
        if (*(char *)(lVar12 + 0x10f) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0xf8);
        }
        local_58 = 0;
        local_41 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(local_1d8);
        cVar5 = RelationshipManager::IsOnIgnoreList(uVar3,local_1d8);
        if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
          eastl__basic_string();
        }
        if (cVar5 == '\0') goto LAB_0019ed76;
      }
    }
  }
  puVar10 = &DAT_015d3620;
LAB_0019f2a7:
  if (-1 < local_161) {
    return puVar10;
  }
  if (CONCAT71(uStack_177,local_178) == 0) {
    return puVar10;
  }
  eastl__basic_string();
  return puVar10;
}


```

## `jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL` @ 0019f790
```c

undefined * jag::packethandlers::Chat::MESSAGE_FRIENDCHANNEL(long *param_1,long param_2)

{
  undefined4 uVar1;
  long lVar2;
  undefined8 uVar3;
  undefined4 *puVar4;
  char cVar5;
  ushort uVar6;
  short sVar7;
  uint uVar8;
  long lVar9;
  undefined *puVar10;
  undefined4 *puVar11;
  long lVar12;
  char *pcVar13;
  undefined8 *puVar14;
  ulong uVar15;
  uint uVar16;
  char *pcStack_1e8;
  ulong *puStack_1e0;
  undefined1 *puStack_1d8;
  uint uStack_1d0;
  int iStack_1cc;
  undefined1 uStack_1b8;
  undefined7 uStack_1b7;
  char cStack_1a1;
  undefined1 uStack_198;
  undefined7 uStack_197;
  char cStack_181;
  char cStack_178;
  undefined7 uStack_177;
  ulong uStack_170;
  char cStack_161;
  undefined1 uStack_158;
  undefined7 uStack_157;
  char cStack_141;
  long alStack_138 [2];
  char cStack_121;
  long alStack_118 [2];
  char cStack_101;
  long alStack_f8 [2];
  char cStack_e1;
  long alStack_d8 [2];
  char cStack_c1;
  undefined1 uStack_b8;
  undefined7 uStack_b7;
  char cStack_a1;
  undefined1 uStack_98;
  undefined7 uStack_97;
  char cStack_81;
  ulong uStack_78;
  char *pcStack_70;
  char cStack_61;
  undefined1 uStack_58;
  undefined7 uStack_57;
  char cStack_41;
  
  lVar12 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar12 + 0x38) == *(long *)(lVar12 + 0x40)) &&
     (cVar5 = FUN_006a53c0(), cVar5 == '\0')) {
LAB_0019f2dd:
    return &DAT_015d3600;
  }
  uVar6 = FUN_00121a30(param_2);
  lVar12 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + *param_1) + 0x10) +
                    (ulong)uVar6 * 8);
  puVar14 = &DAT_013962b0;
  if (lVar12 != 0) {
    puVar14 = (undefined8 *)(lVar12 + 0x30);
  }
  if (puVar14[1] == 0) goto LAB_0019f2dd;
  uVar8 = FUN_00121a30(param_2);
  lVar12 = *(long *)(param_2 + 0x18);
  sVar7 = (short)uVar8;
  *(long *)(param_2 + 0x18) = lVar12 + 1;
  uVar15 = (ulong)*(byte *)(*(long *)(param_2 + 0x10) + lVar12);
  cStack_178 = '\0';
  cStack_161 = '\x17';
  if (sVar7 < 0) {
    uVar6 = FUN_00121a30(param_2);
    uStack_1d0 = (uint)uVar6;
    FUN_00506d10(&uStack_78,
                 *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1) + 0x1b0),uVar6
                );
    if (pcStack_70 == (char *)0x0) {
      if (uStack_78 != 0) {
        ref_counter_base::DecRef();
      }
LAB_0019f3c2:
      puVar10 = &DAT_015d3600;
      goto LAB_0019f2a7;
    }
    FUN_004e0580(&uStack_58,pcStack_70,param_2);
    FUN_00126110(&cStack_178,&uStack_58);
    if ((cStack_41 < '\0') && (CONCAT71(uStack_57,uStack_58) != 0)) {
      eastl__basic_string();
    }
    if (uStack_78 != 0) {
      ref_counter_base::DecRef();
    }
    uVar16 = uVar8 & 0x7fff;
  }
  else {
    cVar5 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&cStack_178,
                         param_2);
    if (cVar5 == '\0') goto LAB_0019f3c2;
    uStack_58 = 0;
    cStack_41 = '\x17';
    if (cStack_161 < '\0') {
      pcStack_70 = (char *)CONCAT71(uStack_177,cStack_178);
    }
    else {
      uStack_170 = 0x17 - (long)cStack_161;
      pcStack_70 = &cStack_178;
    }
    uStack_78 = uStack_170;
    FUN_00133770(&uStack_58);
    FUN_00126110(&uStack_58,&cStack_178);
    if ((cStack_41 < '\0') && (CONCAT71(uStack_57,uStack_58) != 0)) {
      eastl__basic_string();
    }
    uVar16 = uVar8 & 0xffff;
    uStack_1d0 = 0xffffffff;
  }
  puStack_1d8 = &uStack_58;
  puStack_1e0 = &uStack_78;
  pcStack_1e8 = &cStack_178;
  lVar12 = puVar14[1];
  if (*(char *)(lVar12 + 0xa7) < '\0') {
    lVar9 = *(long *)(lVar12 + 0x98);
  }
  else {
    lVar9 = 0x17 - (long)*(char *)(lVar12 + 0xa7);
  }
  if (lVar9 != 0) {
    if ((&DAT_015a4aa9)[uVar15 * 0xc] == '\0') {
LAB_0019ed76:
      FUN_00145f30(pcStack_1e8);
      FUN_00145d90(pcStack_1e8);
      pcVar13 = pcStack_1e8;
      if (cStack_161 < '\0') {
        pcVar13 = (char *)CONCAT71(uStack_177,cStack_178);
      }
      uStack_198 = 0;
      cStack_181 = '\x17';
      cVar5 = *pcVar13;
      while (cVar5 != '\0') {
        pcVar13 = pcVar13 + 1;
        cVar5 = *pcVar13;
      }
      FUN_0048d1a0(&uStack_198);
      (**(code **)(*(long *)puVar14[1] + 0x158))
                ((long *)puVar14[1],&uStack_198,uVar16 >> 8,uVar16 & 0xff);
      if ((&DAT_015a4aa8)[uVar15 * 0xc] == '\0') {
        iStack_1cc = ((int)sVar7 >> 0x1f & 0xfU) + 2;
      }
      else {
        iStack_1cc = (uVar8 >> 0xb & 0x10) + 1;
      }
      cStack_1a1 = '\x17';
      uStack_1b8 = 0;
      puVar11 = *(undefined4 **)((long)&__DT_SYMTAB[0xa3].st_size + puVar14[1]);
      puVar4 = *(undefined4 **)((long)&__DT_SYMTAB[0xa4].st_name + puVar14[1]);
      if (puVar11 != puVar4) {
        uVar8 = (int)((ulong)((long)puVar4 + (-8 - (long)puVar11)) >> 3) + 1U & 7;
        if (uVar8 != 0) {
          if (uVar8 != 1) {
            if (uVar8 != 2) {
              if (uVar8 != 3) {
                if (uVar8 != 4) {
                  if (uVar8 != 5) {
                    if (uVar8 != 6) {
                      if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                        FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
                      }
                      puVar11 = puVar11 + 2;
                    }
                    if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                      FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
                    }
                    puVar11 = puVar11 + 2;
                  }
                  if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                    FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
                  }
                  puVar11 = puVar11 + 2;
                }
                if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                  FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
                }
                puVar11 = puVar11 + 2;
              }
              if ((*(byte *)(puVar11 + 1) & 2) != 0) {
                FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
              }
              puVar11 = puVar11 + 2;
            }
            if ((*(byte *)(puVar11 + 1) & 2) != 0) {
              FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
            }
            puVar11 = puVar11 + 2;
          }
          if ((*(byte *)(puVar11 + 1) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
          }
          puVar11 = puVar11 + 2;
          if (puVar11 == puVar4) goto LAB_0019f010;
        }
        do {
          if ((*(byte *)(puVar11 + 1) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",*puVar11);
          }
          if ((*(byte *)(puVar11 + 3) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[2]);
          }
          if ((*(byte *)(puVar11 + 5) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[4]);
          }
          if ((*(byte *)(puVar11 + 7) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[6]);
          }
          if ((*(byte *)(puVar11 + 9) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[8]);
          }
          if ((*(byte *)(puVar11 + 0xb) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[10]);
          }
          if ((*(byte *)(puVar11 + 0xd) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[0xc]);
          }
          if ((*(byte *)(puVar11 + 0xf) & 2) != 0) {
            FUN_00146a20(&uStack_1b8,"<sprite=%d> ",puVar11[0xe]);
          }
          puVar11 = puVar11 + 0x10;
        } while (puVar11 != puVar4);
      }
LAB_0019f010:
      uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
      uStack_58 = 0;
      cStack_41 = '\x17';
      if (*(int *)(&DAT_015a4aa4 + uVar15 * 0xc) == -1) {
        FUN_00c313f0(puStack_1d8,&DAT_00fba3bd);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        uStack_78 = uStack_78 & 0xffffffffffffff00;
        cStack_61 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(puStack_1e0);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        uStack_98 = 0;
        cStack_81 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(&uStack_98);
        FUN_00af72e0(alStack_d8,puVar14[1]);
        FUN_00126320(&uStack_b8,&uStack_1b8,alStack_d8);
        ChatHistory::AddChat
                  (uVar3,iStack_1cc,0,&uStack_b8,&uStack_98,puStack_1e0,pcStack_1e8,
                   &DAT_015a4aa0 + uVar15 * 0xc,puStack_1d8,uStack_1d0);
        if ((cStack_a1 < '\0') && (CONCAT71(uStack_b7,uStack_b8) != 0)) {
          eastl__basic_string();
        }
        if ((cStack_c1 < '\0') && (alStack_d8[0] != 0)) {
          eastl__basic_string();
        }
        if (cStack_81 < '\0') {
          lVar12 = CONCAT71(uStack_97,uStack_98);
          goto joined_r0x0019f5ec;
        }
      }
      else {
        FUN_00c313f0(puStack_1d8,&DAT_00fba3bd);
        lVar12 = puVar14[1];
        pcVar13 = (char *)(lVar12 + 0x90);
        if (*(char *)(lVar12 + 0xa7) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0x90);
        }
        uStack_78 = uStack_78 & 0xffffffffffffff00;
        cStack_61 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(puStack_1e0);
        lVar9 = puVar14[1];
        lVar12 = lVar9 + 0x90;
        if (*(char *)(lVar9 + 0xa7) < '\0') {
          lVar12 = *(long *)(lVar9 + 0x90);
        }
        uStack_b8 = 0;
        cStack_a1 = '\x17';
        uVar1 = *(undefined4 *)(&DAT_015a4aa4 + uVar15 * 0xc);
        FUN_001335d0(&uStack_b8,8);
        FUN_00146960(&uStack_b8,"<img=%d>",uVar1);
        FUN_0048d130(&uStack_98,&uStack_b8,lVar12);
        FUN_00af72e0(alStack_f8,puVar14[1]);
        uStack_158 = 0;
        cStack_141 = '\x17';
        uVar1 = *(undefined4 *)(&DAT_015a4aa4 + uVar15 * 0xc);
        FUN_001335d0(&uStack_158,8);
        FUN_00146960(&uStack_158,"<img=%d>",uVar1);
        FUN_0048d130(alStack_138,&uStack_158,&DAT_00fb3ff6);
        FUN_001261f0(alStack_118,alStack_138,&uStack_1b8);
        FUN_001261f0(alStack_d8,alStack_118,alStack_f8);
        ChatHistory::AddChat
                  (uVar3,iStack_1cc,0,alStack_d8,&uStack_98,puStack_1e0,pcStack_1e8,
                   &DAT_015a4aa0 + uVar15 * 0xc,puStack_1d8,uStack_1d0);
        if ((cStack_c1 < '\0') && (alStack_d8[0] != 0)) {
          eastl__basic_string();
        }
        if ((cStack_101 < '\0') && (alStack_118[0] != 0)) {
          eastl__basic_string();
        }
        if ((cStack_121 < '\0') && (alStack_138[0] != 0)) {
          eastl__basic_string();
        }
        if ((cStack_141 < '\0') && (CONCAT71(uStack_157,uStack_158) != 0)) {
          eastl__basic_string();
        }
        if ((cStack_e1 < '\0') && (alStack_f8[0] != 0)) {
          eastl__basic_string();
        }
        if ((cStack_81 < '\0') && (CONCAT71(uStack_97,uStack_98) != 0)) {
          eastl__basic_string();
        }
        if (cStack_a1 < '\0') {
          lVar12 = CONCAT71(uStack_b7,uStack_b8);
joined_r0x0019f5ec:
          if (lVar12 != 0) {
            eastl__basic_string();
          }
        }
      }
      if ((cStack_61 < '\0') && (uStack_78 != 0)) {
        eastl__basic_string();
      }
      if ((cStack_41 < '\0') && (CONCAT71(uStack_57,uStack_58) != 0)) {
        eastl__basic_string();
      }
      if ((cStack_1a1 < '\0') && (CONCAT71(uStack_1b7,uStack_1b8) != 0)) {
        eastl__basic_string();
      }
      if ((cStack_181 < '\0') && (CONCAT71(uStack_197,uStack_198) != 0)) {
        eastl__basic_string();
      }
    }
    else {
      lVar9 = *param_1;
      if ((sVar7 < 0) ||
         ((lVar2 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar9), lVar2 != 0 &&
          (((*(char *)(lVar2 + 0x10) == '\0' || (*(char *)(lVar2 + 0x19) != '\0')) &&
           (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar9) + 8) == '\0')))))) {
        uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar9);
        pcVar13 = (char *)(lVar12 + 0xf8);
        if (*(char *)(lVar12 + 0x10f) < '\0') {
          pcVar13 = *(char **)(lVar12 + 0xf8);
        }
        uStack_58 = 0;
        cStack_41 = '\x17';
        cVar5 = *pcVar13;
        while (cVar5 != '\0') {
          pcVar13 = pcVar13 + 1;
          cVar5 = *pcVar13;
        }
        FUN_0048d1a0(puStack_1d8);
        cVar5 = RelationshipManager::IsOnIgnoreList(uVar3,puStack_1d8);
        if ((cStack_41 < '\0') && (CONCAT71(uStack_57,uStack_58) != 0)) {
          eastl__basic_string();
        }
        if (cVar5 == '\0') goto LAB_0019ed76;
      }
    }
  }
  puVar10 = &DAT_015d3620;
LAB_0019f2a7:
  if (-1 < cStack_161) {
    return puVar10;
  }
  if (CONCAT71(uStack_177,cStack_178) == 0) {
    return puVar10;
  }
  eastl__basic_string();
  return puVar10;
}


```

## `jag::packethandlers::Chat::MESSAGE_PRIVATE_ECHO` @ 0019f7a0
```c

undefined * jag::packethandlers::Chat::MESSAGE_PRIVATE_ECHO(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  undefined4 uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  long lVar9;
  undefined8 uVar10;
  int *piVar11;
  char cVar12;
  char cVar13;
  int iVar14;
  undefined *puVar15;
  char *pcVar16;
  ulong uVar17;
  ulong uVar18;
  int local_13c;
  undefined1 local_138;
  undefined7 uStack_137;
  char local_121;
  undefined1 local_118;
  undefined7 uStack_117;
  ulong local_110;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  long local_b8 [2];
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  long local_78 [2];
  char local_61;
  ulong local_58;
  undefined1 *local_50;
  char local_41;
  
  lVar6 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar6 + 0x38) == *(long *)(lVar6 + 0x40)) &&
     (cVar13 = FUN_006a53c0(), cVar13 == '\0')) {
    return &DAT_015d3600;
  }
  local_138 = 0;
  local_121 = '\x17';
  FUN_00ad80e0(param_2,&local_138);
  iVar14 = FUN_00121a30(param_2);
  lVar6 = *(long *)(param_2 + 0x18);
  lVar7 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar6 + 3;
  bVar1 = *(byte *)(lVar7 + 1 + lVar6);
  bVar2 = *(byte *)(lVar7 + lVar6);
  bVar3 = *(byte *)(lVar7 + 2 + lVar6);
  *(long *)(param_2 + 0x18) = lVar6 + 4;
  bVar4 = *(byte *)(lVar7 + 3 + lVar6);
  *(long *)(param_2 + 0x18) = lVar6 + 5;
  lVar8 = *param_1;
  local_13c = iVar14 * 0x1000000 + (uint)bVar2 * 0x10000 + (uint)bVar1 * 0x100 + (uint)bVar3;
  lVar9 = *(long *)((long)&__DT_RELA[0xcfc].r_addend + lVar8);
  if (*(long *)(lVar9 + 8) != 0) {
    uVar18 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
    if (uVar18 == 0) {
LAB_0019f8a6:
      uVar17 = (ulong)bVar4;
      cVar13 = *(char *)(lVar7 + 4 + lVar6);
      if ((&DAT_015a4aa9)[uVar17 * 0xc] != '\0') {
        lVar6 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar8);
        if (((lVar6 != 0) &&
            (((*(char *)(lVar6 + 0x10) != '\0' && (*(char *)(lVar6 + 0x19) == '\0')) ||
             (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar8) + 8) != '\0')))) ||
           (cVar12 = RelationshipManager::IsOnIgnoreList
                               (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar8),&local_138),
           cVar12 != '\0')) goto LAB_0019fc30;
        uVar18 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
      }
      if ((int)uVar18 == DAT_013942d8) {
        if (DAT_013942e8 < DAT_013942f0) {
          piVar11 = DAT_013942e8 + 1;
          *DAT_013942e8 = local_13c;
          DAT_013942e8 = piVar11;
        }
        else {
          FUN_00baa7b0(&DAT_013942e0,&local_13c);
        }
      }
      else {
        DAT_013942e0[DAT_013942d8] = local_13c;
      }
      local_118 = 0;
      local_101 = '\x17';
      DAT_013942d8 = (DAT_013942d8 + 1) % 100;
      cVar12 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&local_118,
                            param_2);
      puVar15 = &DAT_015d3600;
      if (cVar12 != '\0') {
        local_f8 = 0;
        local_e1 = '\x17';
        if (local_101 < '\0') {
          local_50 = (undefined1 *)CONCAT71(uStack_117,local_118);
        }
        else {
          local_110 = 0x17 - (long)local_101;
          local_50 = &local_118;
        }
        local_58 = local_110;
        FUN_00133770(&local_f8,&local_58);
        FUN_00126110(&local_118,&local_f8);
        lVar6 = *(long *)(lVar9 + 8);
        cVar13 = (cVar13 != '\x01') * '\x02' + '\x16';
        uVar10 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
        pcVar16 = (char *)(lVar6 + 8);
        if (*(int *)(&DAT_015a4aa4 + uVar17 * 0xc) == -1) {
          if (*(char *)(lVar6 + 0x1f) < '\0') {
            pcVar16 = *(char **)(lVar6 + 8);
          }
          local_58 = local_58 & 0xffffffffffffff00;
          local_41 = '\x17';
          cVar12 = *pcVar16;
          while (cVar12 != '\0') {
            pcVar16 = pcVar16 + 1;
            cVar12 = *pcVar16;
          }
          FUN_0048d1a0(&local_58);
          ChatHistory::AddChat
                    (uVar10,cVar13,0,&local_138,&local_138,&local_138,&local_118,
                     &DAT_015a4aa0 + uVar17 * 0xc,&local_58,0xffffffffffffffff);
          uVar18 = local_58;
          if (local_41 < '\0') goto joined_r0x0019fd56;
        }
        else {
          if (*(char *)(lVar6 + 0x1f) < '\0') {
            pcVar16 = *(char **)(lVar6 + 8);
          }
          local_d8 = 0;
          local_c1 = '\x17';
          cVar12 = *pcVar16;
          while (cVar12 != '\0') {
            pcVar16 = pcVar16 + 1;
            cVar12 = *pcVar16;
          }
          FUN_0048d1a0(&local_d8);
          local_98 = 0;
          local_81 = '\x17';
          uVar5 = *(undefined4 *)(&DAT_015a4aa4 + uVar17 * 0xc);
          FUN_001335d0(&local_98,8);
          FUN_00146960(&local_98,"<img=%d>",uVar5);
          FUN_001261f0(local_b8,&local_98,&local_138);
          local_58 = local_58 & 0xffffffffffffff00;
          local_41 = '\x17';
          uVar5 = *(undefined4 *)(&DAT_015a4aa4 + uVar17 * 0xc);
          FUN_001335d0(&local_58,8);
          FUN_00146960(&local_58,"<img=%d>",uVar5);
          FUN_001261f0(local_78,&local_58,&local_138);
          ChatHistory::AddChat
                    (uVar10,cVar13,0,local_78,local_b8,&local_138,&local_118,
                     &DAT_015a4aa0 + uVar17 * 0xc,&local_d8,0xffffffffffffffff);
          if ((local_61 < '\0') && (local_78[0] != 0)) {
            eastl__basic_string();
          }
          if ((local_41 < '\0') && (local_58 != 0)) {
            eastl__basic_string();
          }
          if ((local_a1 < '\0') && (local_b8[0] != 0)) {
            eastl__basic_string();
          }
          if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
            eastl__basic_string();
          }
          if (local_c1 < '\0') {
            uVar18 = CONCAT71(uStack_d7,local_d8);
joined_r0x0019fd56:
            if (uVar18 != 0) {
              eastl__basic_string();
            }
          }
        }
        if ((local_e1 < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
          eastl__basic_string();
        }
        puVar15 = &DAT_015d3620;
      }
      if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
        eastl__basic_string();
      }
      goto LAB_0019fc37;
    }
    if (local_13c != *DAT_013942e0) {
      uVar17 = 0;
      do {
        uVar17 = (ulong)((int)uVar17 + 1);
        if (uVar18 <= uVar17) goto LAB_0019f8a6;
      } while (local_13c != DAT_013942e0[uVar17]);
    }
  }
LAB_0019fc30:
  puVar15 = &DAT_015d3620;
LAB_0019fc37:
  if ((local_121 < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  return puVar15;
}


```

## `jag::packethandlers::Chat::MESSAGE_FRIENDCHAT` @ 0019fe10
```c

undefined * jag::packethandlers::Chat::MESSAGE_FRIENDCHAT(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;
  undefined8 uVar7;
  char cVar8;
  int iVar9;
  ulong uVar10;
  undefined *puVar11;
  ulong uVar12;
  char *pcVar13;
  undefined1 *puVar14;
  ulong uVar15;
  int *piVar16;
  int local_15c;
  undefined1 local_158;
  undefined7 uStack_157;
  long local_150;
  char local_141 [9];
  undefined1 local_138;
  undefined7 uStack_137;
  char local_121;
  undefined1 local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  ulong local_f0;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  long local_b8 [2];
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  long local_78 [2];
  char local_61;
  ulong local_58;
  undefined1 *local_50;
  char local_41;
  
  lVar5 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar5 + 0x38) == *(long *)(lVar5 + 0x40)) &&
     (cVar8 = FUN_006a53c0(), cVar8 == '\0')) {
    return &DAT_015d3600;
  }
  lVar5 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  cVar8 = *(char *)(*(long *)(param_2 + 0x10) + lVar5);
  local_158 = 0;
  local_141[0] = '\x17';
  FUN_00ad80e0(param_2,&local_158);
  local_138 = 0;
  local_121 = '\x17';
  if (cVar8 == '\x01') {
    FUN_00ad80e0(param_2,&local_138);
  }
  else {
    if (local_141[0] < '\0') {
      puVar14 = (undefined1 *)CONCAT71(uStack_157,local_158);
      pcVar13 = puVar14 + local_150;
    }
    else {
      pcVar13 = local_141 + -(long)local_141[0];
      puVar14 = &local_158;
    }
    FUN_00495ef0(&local_138,puVar14,pcVar13);
  }
  local_118 = 0;
  local_101 = '\x17';
  FUN_00ad80e0(param_2,&local_118);
  iVar9 = FUN_00121a30(param_2);
  piVar16 = DAT_013942e8;
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  bVar1 = *(byte *)(lVar6 + 1 + lVar5);
  bVar2 = *(byte *)(lVar6 + lVar5);
  bVar3 = *(byte *)(lVar6 + 2 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  uVar15 = (ulong)*(byte *)(lVar6 + 3 + lVar5);
  local_15c = (uint)bVar2 * 0x10000 + (uint)bVar1 * 0x100 + (uint)bVar3 + iVar9 * 0x1000000;
  uVar12 = (long)piVar16 - (long)DAT_013942e0 >> 2;
  if (uVar12 == 0) {
LAB_0019ff92:
    if ((&DAT_015a4aa9)[uVar15 * 0xc] != '\0') {
      lVar5 = *param_1;
      lVar6 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar5);
      if (((lVar6 != 0) &&
          (((*(char *)(lVar6 + 0x10) != '\0' && (*(char *)(lVar6 + 0x19) == '\0')) ||
           (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar5) + 8) != '\0')))) ||
         (cVar8 = RelationshipManager::IsOnIgnoreList
                            (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar5),&local_138),
         cVar8 != '\0')) goto LAB_0019ffd0;
      uVar12 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
      piVar16 = DAT_013942e8;
    }
    if ((int)uVar12 == DAT_013942d8) {
      if (piVar16 < DAT_013942f0) {
        DAT_013942e8 = piVar16 + 1;
        *piVar16 = local_15c;
      }
      else {
        FUN_00baa7b0(&DAT_013942e0,&local_15c);
      }
    }
    else {
      DAT_013942e0[DAT_013942d8] = local_15c;
    }
    local_f8 = 0;
    local_e1 = '\x17';
    DAT_013942d8 = (DAT_013942d8 + 1) % 100;
    cVar8 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&local_f8,
                         param_2);
    puVar11 = &DAT_015d3600;
    if (cVar8 != '\0') {
      local_d8 = 0;
      local_c1 = '\x17';
      if (local_e1 < '\0') {
        local_50 = (undefined1 *)CONCAT71(uStack_f7,local_f8);
      }
      else {
        local_f0 = 0x17 - (long)local_e1;
        local_50 = &local_f8;
      }
      lVar5 = uVar15 * 0xc;
      local_58 = local_f0;
      FUN_00133770(&local_d8,&local_58);
      FUN_00126110(&local_f8,&local_d8);
      iVar9 = *(int *)(&DAT_015a4aa4 + lVar5);
      if (iVar9 == -1) {
        ChatHistory::AddChat
                  (*(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1),9,0,&local_158,
                   &local_138,&local_158,&local_f8,&DAT_015a4aa0 + lVar5,&local_118,0xffffffff);
      }
      else {
        local_98 = 0;
        local_81 = '\x17';
        uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
        FUN_001335d0(&local_98,8);
        FUN_00146960(&local_98,"<img=%d>",iVar9);
        FUN_001261f0(local_b8,&local_98,&local_138);
        uVar4 = *(undefined4 *)(&DAT_015a4aa4 + lVar5);
        local_58 = local_58 & 0xffffffffffffff00;
        local_41 = '\x17';
        FUN_001335d0(&local_58,8);
        FUN_00146960(&local_58,"<img=%d>",uVar4);
        FUN_001261f0(local_78,&local_58,&local_158);
        ChatHistory::AddChat
                  (uVar7,9,0,local_78,local_b8,&local_158,&local_f8,&DAT_015a4aa0 + lVar5,&local_118
                   ,0xffffffffffffffff);
        if ((local_61 < '\0') && (local_78[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_41 < '\0') && (local_58 != 0)) {
          eastl__basic_string();
        }
        if ((local_a1 < '\0') && (local_b8[0] != 0)) {
          eastl__basic_string();
        }
        if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
          eastl__basic_string();
        }
      }
      if ((local_c1 < '\0') && (CONCAT71(uStack_d7,local_d8) != 0)) {
        eastl__basic_string();
      }
      puVar11 = &DAT_015d3620;
    }
    if ((local_e1 < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
      eastl__basic_string();
    }
  }
  else {
    uVar10 = 0;
    iVar9 = *DAT_013942e0;
    while (local_15c != iVar9) {
      uVar10 = (ulong)((int)uVar10 + 1);
      if (uVar12 <= uVar10) goto LAB_0019ff92;
      iVar9 = DAT_013942e0[uVar10];
    }
LAB_0019ffd0:
    puVar11 = &DAT_015d3620;
  }
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  if ((local_121 < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  if ((local_141[0] < '\0') && (CONCAT71(uStack_157,local_158) != 0)) {
    eastl__basic_string();
  }
  return puVar11;
}


```

## `jag::packethandlers::Chat::MESSAGE_PRIVATE` @ 001a0490
```c

undefined * jag::packethandlers::Chat::MESSAGE_PRIVATE(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  char cVar5;
  ushort uVar6;
  ushort uVar7;
  undefined4 uVar8;
  long lVar9;
  undefined8 uVar10;
  int iVar11;
  int *piVar12;
  char cVar13;
  undefined8 uVar14;
  ulong uVar15;
  ulong uVar16;
  char *pcVar17;
  long lVar18;
  ushort uVar19;
  int local_12c;
  long local_128;
  undefined8 local_120;
  undefined1 local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  char local_e1;
  long local_d8 [2];
  char local_c1;
  undefined1 local_b8;
  undefined7 uStack_b7;
  char local_a1;
  long local_98 [2];
  char local_81;
  undefined1 local_78;
  undefined7 uStack_77;
  char local_61;
  long local_58 [2];
  char local_41;
  
  local_118 = 0;
  local_101 = '\x17';
  FUN_00ad80e0(param_2,&local_118);
  iVar11 = DAT_01050dc0;
  lVar18 = *(long *)(param_2 + 0x18);
  lVar9 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar18 + 2;
  uVar6 = *(ushort *)(lVar9 + lVar18);
  *(long *)(param_2 + 0x18) = lVar18 + 5;
  bVar1 = *(byte *)(lVar9 + 2 + lVar18);
  bVar2 = *(byte *)(lVar9 + 3 + lVar18);
  bVar3 = *(byte *)(lVar9 + 4 + lVar18);
  *(long *)(param_2 + 0x18) = lVar18 + 6;
  bVar4 = *(byte *)(lVar9 + 5 + lVar18);
  *(long *)(param_2 + 0x18) = lVar18 + 7;
  cVar5 = *(char *)(lVar9 + 6 + lVar18);
  *(long *)(param_2 + 0x18) = lVar18 + 9;
  uVar7 = *(ushort *)(lVar9 + 7 + lVar18);
  if (iVar11 == 0x3020100) {
    uVar6 = uVar6 >> 8;
  }
  uVar19 = uVar7 << 8 | uVar7 >> 8;
  if (iVar11 != 0x3020100) {
    uVar19 = uVar7;
  }
  lVar18 = *param_1;
  lVar9 = *(long *)((long)&__DT_RELA[0xcfc].r_addend + lVar18);
  if (*(long *)(lVar9 + 8) == 0) goto LAB_001a08a0;
  uVar16 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
  local_12c = (uint)uVar6 * 0x1000000 + (uint)bVar2 * 0x100 + (uint)bVar1 * 0x10000 + (uint)bVar3;
  if (uVar16 != 0) {
    if (*DAT_013942e0 != local_12c) {
      uVar15 = 0;
      do {
        uVar15 = (ulong)((int)uVar15 + 1);
        if (uVar16 <= uVar15) goto LAB_001a05b6;
      } while (local_12c != DAT_013942e0[uVar15]);
    }
    goto LAB_001a08a0;
  }
LAB_001a05b6:
  uVar16 = (ulong)bVar4;
  if ((&DAT_015a4aa9)[uVar16 * 0xc] != '\0') {
    cVar13 = RelationshipManager::IsOnIgnoreList
                       (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar18),&local_118);
    if (cVar13 != '\0') goto LAB_001a08a0;
    lVar18 = *param_1;
  }
  FUN_00506d10(&local_128,
               *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar18) + 0x1b0),uVar19);
  if ((int)((long)DAT_013942e8 - (long)DAT_013942e0 >> 2) == DAT_013942d8) {
    if (DAT_013942e8 < DAT_013942f0) {
      piVar12 = DAT_013942e8 + 1;
      *DAT_013942e8 = local_12c;
      DAT_013942e8 = piVar12;
    }
    else {
      FUN_00baa7b0(&DAT_013942e0,&local_12c);
    }
  }
  else {
    DAT_013942e0[DAT_013942d8] = local_12c;
  }
  DAT_013942d8 = (DAT_013942d8 + 1) % 100;
  FUN_004e0580(local_58,local_120,param_2);
  lVar18 = *(long *)(lVar9 + 8);
  cVar5 = (cVar5 != '\x01') * '\x02' + '\x17';
  uVar10 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
  pcVar17 = (char *)(lVar18 + 8);
  if (*(int *)(&DAT_015a4aa4 + uVar16 * 0xc) == -1) {
    if (*(char *)(lVar18 + 0x1f) < '\0') {
      pcVar17 = *(char **)(lVar18 + 8);
    }
    local_78 = 0;
    local_61 = '\x17';
    cVar13 = *pcVar17;
    while (cVar13 != '\0') {
      pcVar17 = pcVar17 + 1;
      cVar13 = *pcVar17;
    }
    FUN_0048d1a0(&local_78);
    uVar14 = ChatHistory::AddChat_hookable
                       (uVar10,cVar5,0,0,&local_118,&local_118,&local_118,local_58,
                        &DAT_015a4aa0 + uVar16 * 0xc,&local_78,uVar19);
    FUN_0019e140(uVar10,uVar14);
    if (local_61 < '\0') {
      lVar18 = CONCAT71(uStack_77,local_78);
      goto joined_r0x001a09c5;
    }
  }
  else {
    if (*(char *)(lVar18 + 0x1f) < '\0') {
      pcVar17 = *(char **)(lVar18 + 8);
    }
    local_f8 = 0;
    local_e1 = '\x17';
    cVar13 = *pcVar17;
    while (cVar13 != '\0') {
      pcVar17 = pcVar17 + 1;
      cVar13 = *pcVar17;
    }
    FUN_0048d1a0(&local_f8);
    local_b8 = 0;
    local_a1 = '\x17';
    uVar8 = *(undefined4 *)(&DAT_015a4aa4 + uVar16 * 0xc);
    FUN_001335d0(&local_b8,8);
    FUN_00146960(&local_b8,"<img=%d>",uVar8);
    FUN_001261f0(local_d8,&local_b8,&local_118);
    local_78 = 0;
    local_61 = '\x17';
    uVar8 = *(undefined4 *)(&DAT_015a4aa4 + uVar16 * 0xc);
    FUN_001335d0(&local_78,8);
    FUN_00146960(&local_78,"<img=%d>",uVar8);
    FUN_001261f0(local_98,&local_78,&local_118);
    uVar14 = ChatHistory::AddChat_hookable
                       (uVar10,cVar5,0,0,local_98,local_d8,&local_118,local_58,
                        &DAT_015a4aa0 + uVar16 * 0xc,&local_f8,uVar19);
    FUN_0019e140(uVar10,uVar14);
    if ((local_81 < '\0') && (local_98[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
      eastl__basic_string();
    }
    if ((local_c1 < '\0') && (local_d8[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_a1 < '\0') && (CONCAT71(uStack_b7,local_b8) != 0)) {
      eastl__basic_string();
    }
    if (local_e1 < '\0') {
      lVar18 = CONCAT71(uStack_f7,local_f8);
joined_r0x001a09c5:
      if (lVar18 != 0) {
        eastl__basic_string();
      }
    }
  }
  if ((local_41 < '\0') && (local_58[0] != 0)) {
    eastl__basic_string();
  }
  if (local_128 != 0) {
    ref_counter_base::DecRef();
  }
LAB_001a08a0:
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE` @ 001a0aa0
```c

undefined * jag::packethandlers::Chat::MESSAGE_QUICKCHAT_PRIVATE(long *param_1,long param_2)

{
  char cVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  ushort uVar5;
  undefined4 uVar6;
  long lVar7;
  undefined8 uVar8;
  ushort uVar9;
  int *piVar10;
  char cVar11;
  long lVar12;
  long lVar13;
  undefined8 uVar14;
  ulong uVar15;
  char *pcVar16;
  ushort uVar17;
  ulong uVar18;
  int iVar19;
  int iVar20;
  ulong uVar21;
  int local_12c;
  long local_128;
  undefined8 local_120;
  undefined1 local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  char local_e1;
  long local_d8 [2];
  char local_c1;
  undefined1 local_b8;
  undefined7 uStack_b7;
  char local_a1;
  long local_98 [2];
  char local_81;
  undefined1 local_78;
  undefined7 uStack_77;
  char local_61;
  long local_58 [2];
  char local_41;
  
  lVar12 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar12 + 1;
  cVar1 = *(char *)(*(long *)(param_2 + 0x10) + lVar12);
  local_118 = 0;
  local_101 = '\x17';
  FUN_00ad80e0(param_2);
  iVar19 = DAT_01050dc0;
  lVar12 = *(long *)(param_2 + 0x18);
  lVar13 = *(long *)(param_2 + 0x10);
  lVar7 = *param_1;
  *(long *)(param_2 + 0x18) = lVar12 + 2;
  uVar5 = *(ushort *)(lVar13 + lVar12);
  *(long *)(param_2 + 0x18) = lVar12 + 5;
  bVar2 = *(byte *)(lVar13 + 2 + lVar12);
  bVar3 = *(byte *)(lVar13 + 3 + lVar12);
  uVar9 = uVar5 >> 8;
  if (iVar19 != 0x3020100) {
    uVar9 = uVar5;
  }
  bVar4 = *(byte *)(lVar13 + 4 + lVar12);
  *(long *)(param_2 + 0x18) = lVar12 + 6;
  uVar21 = (ulong)*(byte *)(lVar13 + 5 + lVar12);
  *(long *)(param_2 + 0x18) = lVar12 + 8;
  uVar5 = *(ushort *)(lVar13 + 6 + lVar12);
  lVar13 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + lVar7);
  lVar12 = lVar13 + 0x50;
  uVar17 = uVar5 << 8 | uVar5 >> 8;
  if (iVar19 != 0x3020100) {
    uVar17 = uVar5;
  }
  if (-1 < cVar1) {
    lVar12 = FUN_0019be40(lVar13,cVar1);
  }
  if (*(long *)(lVar12 + 8) == 0) goto LAB_001a0ef0;
  local_12c = (uint)bVar3 * 0x100 + (uint)bVar2 * 0x10000 + (uint)bVar4 + (uint)uVar9 * 0x1000000;
  uVar18 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
  if (uVar18 != 0) {
    if (local_12c != *DAT_013942e0) {
      uVar15 = 0;
      do {
        uVar15 = (ulong)((int)uVar15 + 1);
        if (uVar18 <= uVar15) goto LAB_001a0c06;
      } while (local_12c != DAT_013942e0[uVar15]);
    }
    goto LAB_001a0ef0;
  }
LAB_001a0c06:
  lVar13 = *param_1;
  if ((&DAT_015a4aa9)[uVar21 * 0xc] != '\0') {
    cVar11 = RelationshipManager::IsOnIgnoreList
                       (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar13),&local_118);
    if (cVar11 != '\0') goto LAB_001a0ef0;
    lVar13 = *param_1;
  }
  FUN_00506d10(&local_128,
               *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar13) + 0x1b0),uVar17);
  if ((int)((long)DAT_013942e8 - (long)DAT_013942e0 >> 2) == DAT_013942d8) {
    if (DAT_013942e8 < DAT_013942f0) {
      piVar10 = DAT_013942e8 + 1;
      *DAT_013942e8 = local_12c;
      DAT_013942e8 = piVar10;
    }
    else {
      FUN_00baa7b0(&DAT_013942e0,&local_12c);
    }
  }
  else {
    DAT_013942e0[DAT_013942d8] = local_12c;
  }
  iVar20 = (int)cVar1;
  DAT_013942d8 = (DAT_013942d8 + 1) % 100;
  FUN_004e0580(local_58,local_120,param_2);
  iVar19 = (iVar20 >> 0x1f & 3U) + 0x2a;
  lVar12 = *(long *)(lVar12 + 8);
  uVar8 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
  pcVar16 = (char *)(lVar12 + 0x38);
  if (*(int *)(&DAT_015a4aa4 + uVar21 * 0xc) == -1) {
    if (*(char *)(lVar12 + 0x4f) < '\0') {
      pcVar16 = *(char **)(lVar12 + 0x38);
    }
    local_78 = 0;
    local_61 = '\x17';
    cVar11 = *pcVar16;
    while (cVar11 != '\0') {
      pcVar16 = pcVar16 + 1;
      cVar11 = *pcVar16;
    }
    FUN_0048d1a0(&local_78);
    if (-2 < cVar1) {
      uVar14 = ChatHistory::AddChat_hookable
                         (uVar8,iVar19,iVar20,0,&local_118,&local_118,&local_118,local_58,
                          &DAT_015a4aa0 + uVar21 * 0xc,&local_78,uVar17);
      FUN_0019e140(uVar8,uVar14);
    }
    if (local_61 < '\0') {
      lVar12 = CONCAT71(uStack_77,local_78);
      goto joined_r0x001a1018;
    }
  }
  else {
    if (*(char *)(lVar12 + 0x4f) < '\0') {
      pcVar16 = *(char **)(lVar12 + 0x38);
    }
    local_f8 = 0;
    local_e1 = '\x17';
    cVar11 = *pcVar16;
    while (cVar11 != '\0') {
      pcVar16 = pcVar16 + 1;
      cVar11 = *pcVar16;
    }
    FUN_0048d1a0(&local_f8);
    local_b8 = 0;
    local_a1 = '\x17';
    uVar6 = *(undefined4 *)(&DAT_015a4aa4 + uVar21 * 0xc);
    FUN_001335d0(&local_b8,8);
    FUN_00146960(&local_b8,"<img=%d>",uVar6);
    FUN_001261f0(local_d8,&local_b8,&local_118);
    local_78 = 0;
    local_61 = '\x17';
    uVar6 = *(undefined4 *)(&DAT_015a4aa4 + uVar21 * 0xc);
    FUN_001335d0(&local_78,8);
    FUN_00146960(&local_78,"<img=%d>",uVar6);
    FUN_001261f0(local_98,&local_78,&local_118);
    if (-2 < cVar1) {
      uVar14 = ChatHistory::AddChat_hookable
                         (uVar8,iVar19,iVar20,0,local_98,local_d8,&local_118,local_58,
                          &DAT_015a4aa0 + uVar21 * 0xc,&local_f8,uVar17);
      FUN_0019e140(uVar8,uVar14);
    }
    if ((local_81 < '\0') && (local_98[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
      eastl__basic_string();
    }
    if ((local_c1 < '\0') && (local_d8[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_a1 < '\0') && (CONCAT71(uStack_b7,local_b8) != 0)) {
      eastl__basic_string();
    }
    if (local_e1 < '\0') {
      lVar12 = CONCAT71(uStack_f7,local_f8);
joined_r0x001a1018:
      if (lVar12 != 0) {
        eastl__basic_string();
      }
    }
  }
  if ((local_41 < '\0') && (local_58[0] != 0)) {
    eastl__basic_string();
  }
  if (local_128 != 0) {
    ref_counter_base::DecRef();
  }
LAB_001a0ef0:
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHANNEL` @ 001a16f0
```c

undefined * jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHANNEL(long *param_1,long param_2)

{
  int iVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  ushort uVar6;
  undefined4 uVar7;
  long lVar8;
  undefined8 uVar9;
  ushort uVar10;
  int *piVar11;
  char cVar12;
  undefined8 uVar13;
  char *pcVar14;
  long lVar15;
  ushort uVar16;
  undefined1 *puVar17;
  ulong uVar18;
  ulong uVar19;
  int local_14c;
  long local_148;
  undefined8 local_140;
  undefined1 local_138;
  undefined7 uStack_137;
  long local_130;
  char local_121 [9];
  undefined1 local_118;
  undefined7 uStack_117;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  char local_e1;
  long local_d8 [2];
  char local_c1;
  undefined1 local_b8;
  undefined7 uStack_b7;
  char local_a1;
  long local_98 [2];
  char local_81;
  undefined1 local_78;
  undefined7 uStack_77;
  char local_61;
  long local_58 [2];
  char local_41;
  
  lVar15 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar15 + 1;
  cVar12 = *(char *)(*(long *)(param_2 + 0x10) + lVar15);
  local_138 = 0;
  local_121[0] = '\x17';
  FUN_00ad80e0(param_2,&local_138);
  local_118 = 0;
  local_101 = '\x17';
  if (cVar12 == '\x01') {
    FUN_00ad80e0(param_2,&local_118);
  }
  else {
    if (local_121[0] < '\0') {
      puVar17 = (undefined1 *)CONCAT71(uStack_137,local_138);
      pcVar14 = puVar17 + local_130;
    }
    else {
      pcVar14 = local_121 + -(long)local_121[0];
      puVar17 = &local_138;
    }
    FUN_00495ef0(&local_118,puVar17,pcVar14);
  }
  iVar1 = DAT_01050dc0;
  lVar15 = *(long *)(param_2 + 0x18);
  lVar8 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar15 + 2;
  uVar6 = *(ushort *)(lVar8 + lVar15);
  *(long *)(param_2 + 0x18) = lVar15 + 5;
  bVar2 = *(byte *)(lVar8 + 3 + lVar15);
  bVar3 = *(byte *)(lVar8 + 2 + lVar15);
  bVar4 = *(byte *)(lVar8 + 4 + lVar15);
  *(long *)(param_2 + 0x18) = lVar15 + 6;
  piVar11 = DAT_013942e0;
  bVar5 = *(byte *)(lVar8 + 5 + lVar15);
  uVar10 = uVar6 >> 8;
  if (iVar1 != 0x3020100) {
    uVar10 = uVar6;
  }
  *(long *)(param_2 + 0x18) = lVar15 + 8;
  uVar6 = *(ushort *)(lVar8 + 6 + lVar15);
  uVar16 = uVar6 << 8 | uVar6 >> 8;
  if (iVar1 != 0x3020100) {
    uVar16 = uVar6;
  }
  uVar19 = (long)DAT_013942e8 - (long)piVar11 >> 2;
  local_14c = (uint)uVar10 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar2 * 0x100 + (uint)bVar4;
  if (uVar19 != 0) {
    uVar18 = 0;
    iVar1 = *piVar11;
    while (local_14c != iVar1) {
      uVar18 = (ulong)((int)uVar18 + 1);
      if (uVar19 <= uVar18) goto LAB_001a1842;
      iVar1 = piVar11[uVar18];
    }
    goto joined_r0x001a1b32;
  }
LAB_001a1842:
  uVar19 = (ulong)bVar5;
  lVar15 = *param_1;
  if ((&DAT_015a4aa9)[uVar19 * 0xc] != '\0') {
    cVar12 = RelationshipManager::IsOnIgnoreList
                       (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar15),&local_118);
    if (cVar12 != '\0') goto joined_r0x001a1b32;
    lVar15 = *param_1;
  }
  FUN_00506d10(&local_148,
               *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar15) + 0x1b0),uVar16);
  if ((int)((long)DAT_013942e8 - (long)DAT_013942e0 >> 2) == DAT_013942d8) {
    if (DAT_013942e8 < DAT_013942f0) {
      piVar11 = DAT_013942e8 + 1;
      *DAT_013942e8 = local_14c;
      DAT_013942e8 = piVar11;
    }
    else {
      FUN_00baa7b0(&DAT_013942e0,&local_14c);
    }
  }
  else {
    DAT_013942e0[DAT_013942d8] = local_14c;
  }
  lVar15 = uVar19 * 0xc;
  DAT_013942d8 = (DAT_013942d8 + 1) % 100;
  FUN_004e0580(local_58,local_140,param_2);
  if (*(int *)(&DAT_015a4aa4 + lVar15) == -1) {
    uVar9 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
    local_78 = 0;
    local_61 = '\x17';
    FUN_00c313f0(&local_78,&DAT_00fba3bd);
    uVar13 = ChatHistory::AddChat_hookable
                       (uVar9,0x12,0,0,&local_138,&local_118,&local_138,local_58,
                        &DAT_015a4aa0 + lVar15,&local_78,uVar16);
    FUN_0019e140(uVar9,uVar13);
    if (local_61 < '\0') {
      lVar15 = CONCAT71(uStack_77,local_78);
      goto joined_r0x001a1cb2;
    }
  }
  else {
    uVar9 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
    local_f8 = 0;
    local_e1 = '\x17';
    FUN_00c313f0(&local_f8,&DAT_00fba3bd);
    uVar7 = *(undefined4 *)(&DAT_015a4aa4 + lVar15);
    local_b8 = 0;
    local_a1 = '\x17';
    FUN_001335d0(&local_b8,8);
    FUN_00146960(&local_b8,"<img=%d>",uVar7);
    FUN_001261f0(local_d8,&local_b8,&local_118);
    uVar7 = *(undefined4 *)(&DAT_015a4aa4 + lVar15);
    local_78 = 0;
    local_61 = '\x17';
    FUN_001335d0(&local_78,8);
    FUN_00146960(&local_78,"<img=%d>",uVar7);
    FUN_001261f0(local_98,&local_78,&local_138);
    uVar13 = ChatHistory::AddChat_hookable
                       (uVar9,0x12,0,0,local_98,local_d8,&local_138,local_58,&DAT_015a4aa0 + lVar15,
                        &local_f8,uVar16);
    FUN_0019e140(uVar9,uVar13);
    if ((local_81 < '\0') && (local_98[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
      eastl__basic_string();
    }
    if ((local_c1 < '\0') && (local_d8[0] != 0)) {
      eastl__basic_string();
    }
    if ((local_a1 < '\0') && (CONCAT71(uStack_b7,local_b8) != 0)) {
      eastl__basic_string();
    }
    if (local_e1 < '\0') {
      lVar15 = CONCAT71(uStack_f7,local_f8);
joined_r0x001a1cb2:
      if (lVar15 != 0) {
        eastl__basic_string();
      }
    }
  }
  if ((local_41 < '\0') && (local_58[0] != 0)) {
    eastl__basic_string();
  }
  if (local_148 != 0) {
    ref_counter_base::DecRef();
  }
joined_r0x001a1b32:
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  if ((local_121[0] < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::CHAT_FILTER_SETTINGS` @ 001a1d00
```c

undefined * jag::packethandlers::Chat::CHAT_FILTER_SETTINGS(long *param_1,long param_2)

{
  long lVar1;
  undefined8 uVar2;
  undefined8 uVar3;
  ushort uVar4;
  bool bVar5;
  long local_98;
  undefined8 local_90;
  undefined1 local_88;
  undefined7 uStack_87;
  char local_71;
  undefined1 local_68;
  undefined7 uStack_67;
  char local_51;
  long local_48 [2];
  char local_31;
  
  local_88 = 0;
  local_71 = '\x17';
  FUN_00ad80e0(param_2,&local_88);
  lVar1 = *(long *)(param_2 + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar1 + 2;
  uVar4 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar1);
  if (bVar5) {
    uVar4 = uVar4 << 8 | uVar4 >> 8;
  }
  FUN_00506d10(&local_98,
               *(undefined8 *)(*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1) + 0x1b0),uVar4);
  FUN_004e0580(local_48,local_90,param_2);
  local_68 = 0;
  local_51 = '\x17';
  uVar2 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
  FUN_00c313f0(&local_68,&DAT_00fba3bd);
  uVar3 = ChatHistory::AddChat_hookable
                    (uVar2,0x13,0,0,&local_88,&local_88,&local_88,local_48,0,&local_68,uVar4);
  FUN_0019e140(uVar2,uVar3);
  if ((local_51 < '\0') && (CONCAT71(uStack_67,local_68) != 0)) {
    eastl__basic_string();
  }
  if ((local_31 < '\0') && (local_48[0] != 0)) {
    eastl__basic_string();
  }
  if (local_98 != 0) {
    ref_counter_base::DecRef();
  }
  if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::MESSAGE_CLANCHANNEL` @ 001a1e80
```c

undefined * jag::packethandlers::Chat::MESSAGE_CLANCHANNEL(long *param_1,long param_2)

{
  long lVar1;
  undefined8 uVar2;
  int *piVar3;
  char cVar4;
  char cVar5;
  int iVar6;
  undefined *puVar7;
  ulong uVar8;
  undefined8 uVar9;
  char *pcVar10;
  long lVar11;
  ulong uVar12;
  int local_fc;
  undefined1 local_f8;
  undefined7 uStack_f7;
  ulong local_f0;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  undefined1 local_b8;
  undefined7 uStack_b7;
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  undefined1 local_78;
  undefined7 uStack_77;
  char local_61;
  ulong local_58;
  undefined1 *local_50;
  char local_41;
  
  lVar11 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar11 + 0x38) == *(long *)(lVar11 + 0x40)) &&
     (cVar5 = FUN_006a53c0(), cVar5 == '\0')) {
    puVar7 = &DAT_015d3600;
  }
  else {
    lVar11 = *(long *)(param_2 + 0x18);
    *(long *)(param_2 + 0x18) = lVar11 + 1;
    cVar5 = *(char *)(*(long *)(param_2 + 0x10) + lVar11);
    iVar6 = FUN_00121a30(param_2);
    lVar11 = *(long *)(param_2 + 0x18);
    lVar1 = *(long *)(param_2 + 0x10);
    *(long *)(param_2 + 0x18) = lVar11 + 3;
    local_fc = iVar6 * 0x1000000 +
               (uint)*(byte *)(lVar1 + lVar11) * 0x10000 +
               (uint)*(byte *)(lVar1 + 1 + lVar11) * 0x100 + (uint)*(byte *)(lVar1 + 2 + lVar11);
    if (cVar5 < '\0') {
      lVar11 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *param_1) + 0x50;
    }
    else {
      lVar11 = FUN_0019be40(*(undefined8 *)((long)&__DT_RELA[0xcf4].r_addend + *param_1),cVar5);
    }
    puVar7 = &DAT_015d3620;
    if (*(long *)(lVar11 + 8) != 0) {
      uVar12 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
      if (uVar12 == 0) {
LAB_001a1f75:
        if ((int)uVar12 == DAT_013942d8) {
          if (DAT_013942e8 < DAT_013942f0) {
            piVar3 = DAT_013942e8 + 1;
            *DAT_013942e8 = local_fc;
            DAT_013942e8 = piVar3;
          }
          else {
            FUN_00baa7b0(&DAT_013942e0,&local_fc);
          }
        }
        else {
          DAT_013942e0[DAT_013942d8] = local_fc;
        }
        local_f8 = 0;
        local_e1 = '\x17';
        DAT_013942d8 = (DAT_013942d8 + 1) % 100;
        cVar4 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&local_f8,
                             param_2);
        puVar7 = &DAT_015d3600;
        if (cVar4 != '\0') {
          local_d8 = 0;
          local_c1 = '\x17';
          if (local_e1 < '\0') {
            local_50 = (undefined1 *)CONCAT71(uStack_f7,local_f8);
          }
          else {
            local_f0 = 0x17 - (long)local_e1;
            local_50 = &local_f8;
          }
          local_58 = local_f0;
          FUN_00133770(&local_d8,&local_58);
          FUN_00126110(&local_f8,&local_d8);
          lVar11 = *(long *)(lVar11 + 8);
          uVar2 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
          pcVar10 = (char *)(lVar11 + 0x38);
          if (*(char *)(lVar11 + 0x4f) < '\0') {
            pcVar10 = *(char **)(lVar11 + 0x38);
          }
          local_b8 = 0;
          local_a1 = '\x17';
          cVar4 = *pcVar10;
          while (cVar4 != '\0') {
            pcVar10 = pcVar10 + 1;
            cVar4 = *pcVar10;
          }
          FUN_0048d1a0(&local_b8);
          local_98 = 0;
          local_81 = '\x17';
          FUN_00c313f0(&local_98,&DAT_00fba3bd);
          local_78 = 0;
          local_61 = '\x17';
          FUN_00c313f0(&local_78,&DAT_00fba3bd);
          local_58 = local_58 & 0xffffffffffffff00;
          local_41 = '\x17';
          FUN_00c313f0(&local_58,&DAT_00fba3bd);
          if (-2 < cVar5) {
            uVar9 = ChatHistory::AddChat_hookable
                              (uVar2,((int)cVar5 >> 0x1f & 3U) + 0x2b,(int)cVar5,0,&local_58,
                               &local_78,&local_98,&local_f8,0,&local_b8,0xffffffffffffffff);
            FUN_0019e140(uVar2,uVar9);
          }
          if ((local_41 < '\0') && (local_58 != 0)) {
            eastl__basic_string();
          }
          if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
            eastl__basic_string();
          }
          if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
            eastl__basic_string();
          }
          if ((local_a1 < '\0') && (CONCAT71(uStack_b7,local_b8) != 0)) {
            eastl__basic_string();
          }
          if ((local_c1 < '\0') && (CONCAT71(uStack_d7,local_d8) != 0)) {
            eastl__basic_string();
          }
          puVar7 = &DAT_015d3620;
        }
        if ((local_e1 < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
          eastl__basic_string();
        }
      }
      else if (local_fc != *DAT_013942e0) {
        uVar8 = 0;
        do {
          uVar8 = (ulong)((int)uVar8 + 1);
          if (uVar12 <= uVar8) goto LAB_001a1f75;
        } while (DAT_013942e0[uVar8] != local_fc);
        puVar7 = &DAT_015d3620;
      }
    }
  }
  return puVar7;
}


```

## `jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT` @ 001a2320
```c

undefined * jag::packethandlers::Chat::MESSAGE_QUICKCHAT_CLANCHAT(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;
  undefined8 uVar7;
  int *piVar8;
  char cVar9;
  char cVar10;
  int iVar11;
  ulong uVar12;
  undefined *puVar13;
  undefined8 uVar14;
  long lVar15;
  char *pcVar16;
  int iVar17;
  ulong uVar18;
  ulong uVar19;
  int local_13c;
  undefined1 local_138;
  undefined7 uStack_137;
  char local_121;
  undefined1 local_118;
  undefined7 uStack_117;
  ulong local_110;
  char local_101;
  undefined1 local_f8;
  undefined7 uStack_f7;
  char local_e1;
  undefined1 local_d8;
  undefined7 uStack_d7;
  char local_c1;
  long local_b8 [2];
  char local_a1;
  undefined1 local_98;
  undefined7 uStack_97;
  char local_81;
  long local_78 [2];
  char local_61;
  ulong local_58;
  undefined1 *local_50;
  char local_41;
  
  lVar5 = *(long *)((long)&__DT_RELA[0xd02].r_addend + *param_1);
  if ((*(long *)(lVar5 + 0x38) == *(long *)(lVar5 + 0x40)) &&
     (cVar10 = FUN_006a53c0(), cVar10 == '\0')) {
    return &DAT_015d3600;
  }
  lVar5 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  cVar10 = *(char *)(*(long *)(param_2 + 0x10) + lVar5);
  local_138 = 0;
  local_121 = '\x17';
  FUN_00ad80e0(param_2,&local_138);
  iVar11 = FUN_00121a30(param_2);
  lVar5 = *(long *)(param_2 + 0x18);
  lVar15 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  bVar1 = *(byte *)(lVar15 + 1 + lVar5);
  bVar2 = *(byte *)(lVar15 + lVar5);
  bVar3 = *(byte *)(lVar15 + 2 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  uVar19 = (ulong)*(byte *)(lVar15 + 3 + lVar5);
  local_13c = (uint)bVar2 * 0x10000 + (uint)bVar1 * 0x100 + (uint)bVar3 + iVar11 * 0x1000000;
  if (cVar10 < '\0') {
    lVar5 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *param_1);
    lVar15 = lVar5 + 0x50;
    lVar5 = *(long *)(lVar5 + 0x58);
  }
  else {
    lVar15 = FUN_0019be40(*(undefined8 *)((long)&__DT_RELA[0xcf4].r_addend + *param_1),cVar10);
    lVar5 = *(long *)(lVar15 + 8);
  }
  if (lVar5 != 0) {
    uVar12 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
    if (uVar12 == 0) {
LAB_001a2447:
      if ((&DAT_015a4aa9)[uVar19 * 0xc] != '\0') {
        lVar5 = *param_1;
        lVar6 = *(long *)((long)&__DT_RELA[0xd40].r_addend + lVar5);
        if (((lVar6 != 0) &&
            (((*(char *)(lVar6 + 0x10) != '\0' && (*(char *)(lVar6 + 0x19) == '\0')) ||
             (*(char *)(*(long *)((long)&__DT_RELA[0x3382].r_offset + lVar5) + 8) != '\0')))) ||
           (cVar9 = RelationshipManager::IsOnIgnoreList
                              (*(undefined8 *)((long)&__DT_RELA[0xcfe].r_info + lVar5),&local_138),
           cVar9 != '\0')) goto LAB_001a2830;
        uVar12 = (long)DAT_013942e8 - (long)DAT_013942e0 >> 2;
      }
      if ((int)uVar12 == DAT_013942d8) {
        if (DAT_013942e8 < DAT_013942f0) {
          piVar8 = DAT_013942e8 + 1;
          *DAT_013942e8 = local_13c;
          DAT_013942e8 = piVar8;
        }
        else {
          FUN_00baa7b0(&DAT_013942e0,&local_13c);
        }
      }
      else {
        DAT_013942e0[DAT_013942d8] = local_13c;
      }
      local_118 = 0;
      local_101 = '\x17';
      DAT_013942d8 = (DAT_013942d8 + 1) % 100;
      cVar9 = FUN_006a5d70(*(undefined8 *)((long)&__DT_RELA[0xd02].r_addend + *param_1),&local_118,
                           param_2);
      puVar13 = &DAT_015d3600;
      if (cVar9 != '\0') {
        local_f8 = 0;
        local_e1 = '\x17';
        if (local_101 < '\0') {
          local_50 = (undefined1 *)CONCAT71(uStack_117,local_118);
        }
        else {
          local_110 = 0x17 - (long)local_101;
          local_50 = &local_118;
        }
        local_58 = local_110;
        FUN_00133770(&local_f8,&local_58);
        FUN_00126110(&local_118,&local_f8);
        iVar11 = (int)cVar10;
        lVar5 = *(long *)(lVar15 + 8);
        iVar17 = (iVar11 >> 0x1f & 3U) + 0x29;
        uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcf4].r_info + *param_1);
        pcVar16 = (char *)(lVar5 + 0x38);
        if (*(int *)(&DAT_015a4aa4 + uVar19 * 0xc) == -1) {
          if (*(char *)(lVar5 + 0x4f) < '\0') {
            pcVar16 = *(char **)(lVar5 + 0x38);
          }
          local_58 = local_58 & 0xffffffffffffff00;
          local_41 = '\x17';
          cVar9 = *pcVar16;
          while (cVar9 != '\0') {
            pcVar16 = pcVar16 + 1;
            cVar9 = *pcVar16;
          }
          FUN_0048d1a0(&local_58);
          if (-2 < cVar10) {
            uVar14 = ChatHistory::AddChat_hookable
                               (uVar7,iVar17,iVar11,0,&local_138,&local_138,&local_138,&local_118,
                                &DAT_015a4aa0 + uVar19 * 0xc,&local_58,0xffffffffffffffff);
            FUN_0019e140(uVar7,uVar14);
          }
          uVar19 = local_58;
          if (local_41 < '\0') goto joined_r0x001a294e;
        }
        else {
          if (*(char *)(lVar5 + 0x4f) < '\0') {
            pcVar16 = *(char **)(lVar5 + 0x38);
          }
          local_d8 = 0;
          local_c1 = '\x17';
          cVar9 = *pcVar16;
          while (cVar9 != '\0') {
            pcVar16 = pcVar16 + 1;
            cVar9 = *pcVar16;
          }
          FUN_0048d1a0(&local_d8);
          uVar4 = *(undefined4 *)(&DAT_015a4aa4 + uVar19 * 0xc);
          local_98 = 0;
          local_81 = '\x17';
          FUN_001335d0(&local_98,8);
          FUN_00146960(&local_98,"<img=%d>",uVar4);
          FUN_001261f0(local_b8,&local_98,&local_138);
          local_58 = local_58 & 0xffffffffffffff00;
          local_41 = '\x17';
          uVar4 = *(undefined4 *)(&DAT_015a4aa4 + uVar19 * 0xc);
          FUN_001335d0(&local_58,8);
          FUN_00146960(&local_58,"<img=%d>",uVar4);
          FUN_001261f0(local_78,&local_58,&local_138);
          if (-2 < cVar10) {
            uVar14 = ChatHistory::AddChat_hookable
                               (uVar7,iVar17,iVar11,0,local_78,local_b8,&local_138,&local_118,
                                &DAT_015a4aa0 + uVar19 * 0xc,&local_d8,0xffffffffffffffff);
            FUN_0019e140(uVar7,uVar14);
          }
          if ((local_61 < '\0') && (local_78[0] != 0)) {
            eastl__basic_string();
          }
          if ((local_41 < '\0') && (local_58 != 0)) {
            eastl__basic_string();
          }
          if ((local_a1 < '\0') && (local_b8[0] != 0)) {
            eastl__basic_string();
          }
          if ((local_81 < '\0') && (CONCAT71(uStack_97,local_98) != 0)) {
            eastl__basic_string();
          }
          if (local_c1 < '\0') {
            uVar19 = CONCAT71(uStack_d7,local_d8);
joined_r0x001a294e:
            if (uVar19 != 0) {
              eastl__basic_string();
            }
          }
        }
        if ((local_e1 < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
          eastl__basic_string();
        }
        puVar13 = &DAT_015d3620;
      }
      if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
        eastl__basic_string();
      }
      goto LAB_001a2837;
    }
    if (local_13c != *DAT_013942e0) {
      uVar18 = 0;
      do {
        uVar18 = (ulong)((int)uVar18 + 1);
        if (uVar12 <= uVar18) goto LAB_001a2447;
      } while (DAT_013942e0[uVar18] != local_13c);
    }
  }
LAB_001a2830:
  puVar13 = &DAT_015d3620;
LAB_001a2837:
  if ((local_121 < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  return puVar13;
}


```

## `jag::packethandlers::Clans::CLANCHANNEL_DELTA` @ 001a3a30
```c

undefined * jag::packethandlers::Clans::CLANCHANNEL_DELTA(long *param_1,long param_2)

{
  long *plVar1;
  char cVar2;
  long lVar3;
  int iVar4;
  uint uVar5;
  undefined8 *puVar6;
  qword *pqVar7;
  char *pcVar8;
  long lVar9;
  long *plVar10;
  undefined1 *puVar11;
  long lVar12;
  size_t sVar13;
  size_t sVar14;
  char *__s2;
  undefined8 *__s1;
  long *plVar15;
  undefined8 *puVar16;
  qword *pqVar17;
  ushort uVar18;
  undefined8 *puVar19;
  ulong uVar20;
  int iVar21;
  long lVar22;
  long lVar23;
  int iVar24;
  long lVar25;
  long lVar26;
  bool bVar27;
  long local_f0;
  long local_e8;
  long *local_d0;
  char local_c8;
  undefined7 uStack_c7;
  char local_b1;
  undefined1 local_a8;
  undefined7 uStack_a7;
  char local_91;
  undefined1 local_88;
  undefined7 uStack_87;
  size_t local_80;
  char local_71;
  undefined1 local_70 [24];
  undefined4 local_58;
  undefined4 local_54;
  undefined1 local_50 [32];
  
  local_c8 = '\0';
  lVar9 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar3 = *(long *)((long)&__DT_RELA[0xcf8].r_info + *param_1);
  local_b1 = '\x17';
  *(undefined4 *)(lVar9 + 0x110) = *(undefined4 *)(*(long *)(lVar9 + 0x108) + 0x10);
  __s2 = &local_c8;
  FUN_00ad80e0(param_2,__s2);
  lVar12 = *(long *)(param_2 + 0x18);
  lVar22 = *(long *)(param_2 + 0x10);
  lVar9 = lVar12 + 1;
  *(long *)(param_2 + 0x18) = lVar9;
  if (*(char *)(lVar22 + lVar12) == '\x01') {
    local_88 = 0;
    local_71 = '\x17';
    FUN_00afd8d0(param_2,&local_88);
    if ((local_71 < '\0') && (CONCAT71(uStack_87,local_88) != 0)) {
      HeapInterface::Free();
    }
    lVar22 = *(long *)(param_2 + 0x10);
    lVar9 = *(long *)(param_2 + 0x18);
  }
  bVar27 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar9 + 2;
  uVar18 = *(ushort *)(lVar22 + lVar9);
  if (bVar27) {
    uVar18 = uVar18 << 8 | uVar18 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar9 + 3;
  iVar24 = (int)*(char *)(lVar22 + lVar9 + 2);
  if (iVar24 != -0x80) {
    local_a8 = 0;
    local_91 = '\x17';
    FUN_00ad80e0(param_2,&local_a8);
    ClanChannelUser::ClanChannelUser(&local_88,__s2,(uint)uVar18,iVar24,&local_a8);
    puVar6 = (undefined8 *)
             FUN_00198e20(*(undefined8 *)((long)&__DT_RELA[0xd40].r_addend + *param_1));
    if (*(char *)((long)puVar6 + 0x17) < '\0') {
      puVar6 = (undefined8 *)*puVar6;
    }
    iVar21 = eastl_string_compare_cstr(__s2,puVar6);
    if (iVar21 == 0) {
      *(int *)(lVar3 + 0x38) = iVar24;
    }
    lVar9 = (long)local_71;
    sVar13 = local_80;
    if (-1 < local_71) {
      sVar13 = 0x17 - (long)local_71;
    }
    if (sVar13 != 0) {
      puVar6 = *(undefined8 **)(lVar3 + 0x40);
      pqVar7 = *(qword **)(lVar3 + 0x48);
      if (puVar6 != pqVar7) {
        bVar27 = true;
        puVar19 = (undefined8 *)
                  ((long)(puVar6 + 10) + ((long)pqVar7 - (long)(puVar6 + 10) & 0xfffffffffffffff0U))
        ;
        if (((int)puVar19 - (int)puVar6 & 0x10U) == 0) {
          cVar2 = *(char *)((long)puVar6 + 0x17);
          sVar13 = 0x17 - (long)cVar2;
          if (cVar2 < '\0') {
            sVar13 = puVar6[1];
          }
          if (local_71 < '\0') {
            puVar11 = (undefined1 *)CONCAT71(uStack_87,local_88);
            sVar14 = local_80;
          }
          else {
            sVar14 = 0x17 - lVar9;
            puVar11 = &local_88;
          }
          if (sVar14 == sVar13) {
            puVar16 = puVar6;
            if (cVar2 < '\0') {
              puVar16 = (undefined8 *)*puVar6;
            }
            iVar24 = memcmp(puVar16,puVar11,sVar13);
            if (iVar24 == 0) {
              FUN_00493e00(puVar6,&local_88);
              FUN_00493e00(puVar6 + 3,local_70);
              *(undefined4 *)(puVar6 + 6) = local_58;
              bVar27 = false;
              *(undefined4 *)((long)puVar6 + 0x34) = local_54;
              FUN_00493e00(puVar6 + 7,local_50);
            }
          }
          puVar6 = puVar6 + 10;
          lVar9 = (long)local_71;
        }
        do {
          cVar2 = *(char *)((long)puVar6 + 0x17);
          sVar13 = 0x17 - (long)cVar2;
          if (cVar2 < '\0') {
            sVar13 = puVar6[1];
          }
          if ((char)lVar9 < '\0') {
            if (local_80 == sVar13) {
              puVar11 = (undefined1 *)CONCAT71(uStack_87,local_88);
LAB_001a3ccc:
              puVar16 = puVar6;
              if (cVar2 < '\0') {
                puVar16 = (undefined8 *)*puVar6;
              }
              iVar24 = memcmp(puVar16,puVar11,sVar13);
              if (iVar24 == 0) {
                bVar27 = false;
                FUN_00493e00(puVar6,&local_88);
                FUN_00493e00(puVar6 + 3,local_70);
                *(undefined4 *)(puVar6 + 6) = local_58;
                *(undefined4 *)((long)puVar6 + 0x34) = local_54;
                FUN_00493e00(puVar6 + 7,local_50);
              }
            }
          }
          else {
            puVar11 = &local_88;
            if (0x17U - lVar9 == sVar13) goto LAB_001a3ccc;
          }
          if (puVar19 == puVar6 + 10) goto LAB_001a3d2d;
          puVar16 = puVar6 + 10;
          cVar2 = *(char *)((long)puVar6 + 0x67);
          sVar13 = 0x17 - (long)cVar2;
          if (cVar2 < '\0') {
            sVar13 = puVar6[0xb];
          }
          if (local_71 < '\0') {
            puVar11 = (undefined1 *)CONCAT71(uStack_87,local_88);
            sVar14 = local_80;
          }
          else {
            sVar14 = 0x17 - (long)local_71;
            puVar11 = &local_88;
          }
          if (sVar14 == sVar13) {
            __s1 = puVar16;
            if (cVar2 < '\0') {
              __s1 = (undefined8 *)*puVar16;
            }
            iVar24 = memcmp(__s1,puVar11,sVar13);
            if (iVar24 == 0) {
              bVar27 = false;
              FUN_00493e00(puVar16,&local_88);
              FUN_00493e00(puVar6 + 0xd,local_70);
              *(undefined4 *)(puVar6 + 0x10) = local_58;
              *(undefined4 *)((long)puVar6 + 0x84) = local_54;
              FUN_00493e00(puVar6 + 0x11,local_50);
            }
          }
          lVar9 = (long)local_71;
          puVar6 = puVar6 + 0x14;
        } while( true );
      }
      goto LAB_001a3ffc;
    }
    goto LAB_001a3d75;
  }
  if (-1 < local_b1) {
    local_d0 = *(long **)(lVar3 + 0x48);
    plVar10 = *(long **)(lVar3 + 0x40);
    iVar24 = (int)((long)local_d0 - (long)plVar10 >> 4) * -0x33333333;
    if (iVar24 < 1) goto LAB_001a3b11;
LAB_001a3e98:
    cVar2 = *__s2;
    iVar21 = 0;
    do {
      pcVar8 = __s2;
      if (cVar2 == '\0') {
        lVar9 = 0;
      }
      else {
        do {
          pcVar8 = pcVar8 + 1;
        } while (*pcVar8 != '\0');
        lVar9 = (long)pcVar8 - (long)__s2;
      }
      if (*(char *)((long)plVar10 + 0x17) < '\0') {
        plVar15 = (long *)*plVar10;
        lVar12 = plVar10[1] + (long)plVar15;
      }
      else {
        lVar12 = (0x17 - (long)*(char *)((long)plVar10 + 0x17)) + (long)plVar10;
        plVar15 = plVar10;
      }
      sVar13 = lVar12 - (long)plVar15;
      if (((((long)sVar13 <= lVar9) && (iVar4 = memcmp(plVar15,__s2,sVar13), iVar4 == 0)) &&
          (lVar9 <= (long)sVar13)) && ((uint)uVar18 == *(uint *)(plVar10 + 6))) {
        if (iVar21 < iVar24) {
          plVar15 = plVar10 + 10;
          if ((plVar15 < local_d0) &&
             (uVar20 = ((long)local_d0 - (long)plVar15 >> 4) * -0x3333333333333333,
             0 < (long)local_d0 - (long)plVar15)) {
            if ((uVar20 & 1) == 0) goto LAB_001a4540;
            FUN_00126110(plVar10,plVar15);
            FUN_00126110(plVar10 + 3,plVar10 + 0xd);
            *(undefined4 *)((long)plVar10 + 0x34) = *(undefined4 *)((long)plVar10 + 0x84);
            *(int *)(plVar10 + 6) = (int)plVar10[0x10];
            FUN_00126110(plVar10 + 7,plVar10 + 0x11);
            plVar10 = plVar15;
            for (uVar20 = uVar20 - 1; uVar20 != 0; uVar20 = uVar20 - 2) {
LAB_001a4540:
              FUN_00126110(plVar10,plVar10 + 10);
              FUN_00126110(plVar10 + 3,plVar10 + 0xd);
              *(int *)(plVar10 + 6) = (int)plVar10[0x10];
              plVar15 = plVar10 + 0x11;
              *(undefined4 *)((long)plVar10 + 0x34) = *(undefined4 *)((long)plVar10 + 0x84);
              FUN_00126110(plVar10 + 7,plVar15);
              FUN_00126110(plVar10 + 10,plVar10 + 0x14);
              FUN_00126110(plVar10 + 0xd,plVar10 + 0x17);
              *(undefined4 *)((long)plVar10 + 0x84) = *(undefined4 *)((long)plVar10 + 0xd4);
              plVar1 = plVar10 + 0x1b;
              *(int *)(plVar10 + 0x10) = (int)plVar10[0x1a];
              plVar10 = plVar10 + 0x14;
              FUN_00126110(plVar15,plVar1);
            }
            local_d0 = *(long **)(lVar3 + 0x48);
          }
          *(long **)(lVar3 + 0x48) = local_d0 + -10;
          FUN_0047fb60();
        }
        break;
      }
      iVar21 = iVar21 + 1;
      plVar10 = plVar10 + 10;
    } while (iVar21 != iVar24);
    if (*(long *)(lVar3 + 0x68) != 0) {
      (**(code **)(lVar3 + 0x70))(lVar3 + 0x58);
    }
    goto LAB_001a3d8e;
  }
  local_d0 = *(long **)(lVar3 + 0x48);
  plVar10 = *(long **)(lVar3 + 0x40);
  __s2 = (char *)CONCAT71(uStack_c7,local_c8);
  iVar24 = (int)((long)local_d0 - (long)plVar10 >> 4) * -0x33333333;
  if (0 < iVar24) goto LAB_001a3e98;
LAB_001a3d9c:
  if (__s2 != (char *)0x0) {
    eastl__basic_string();
  }
LAB_001a3b11:
  return &DAT_015d3620;
LAB_001a3d2d:
  pqVar7 = *(qword **)(lVar3 + 0x48);
  if (bVar27) {
LAB_001a3ffc:
    if (pqVar7 < *(undefined8 **)(lVar3 + 0x50)) {
      *(qword **)(lVar3 + 0x48) = pqVar7 + 10;
      FUN_0048dcd0(pqVar7,&local_88);
      FUN_0048dcd0(pqVar7 + 3,local_70);
      *(undefined4 *)(pqVar7 + 6) = local_58;
      *(undefined4 *)((long)pqVar7 + 0x34) = local_54;
      FUN_0048dcd0(pqVar7 + 7,local_50);
      local_f0 = *(long *)(lVar3 + 0x40);
      pqVar7 = *(qword **)(lVar3 + 0x48);
    }
    else {
      puVar6 = *(undefined8 **)(lVar3 + 0x40);
      local_e8 = (long)pqVar7 - (long)puVar6 >> 4;
      if (local_e8 * -0x3333333333333333 == 0) {
        local_e8 = 0x50;
LAB_001a403f:
        local_f0 = FUN_00c29480(local_e8);
        local_e8 = local_f0 + local_e8;
        puVar6 = *(undefined8 **)(lVar3 + 0x40);
        pqVar17 = *(qword **)(lVar3 + 0x48);
        pqVar7 = (qword *)(local_f0 + 0x50);
      }
      else {
        if (local_e8 * -0x6666666666666666 != 0) {
          local_e8 = local_e8 << 5;
          goto LAB_001a403f;
        }
        local_f0 = 0;
        local_e8 = 0;
        pqVar17 = pqVar7;
        pqVar7 = &Elf64_Phdr_ARRAY_00000040[0].p_vaddr;
      }
      lVar9 = local_f0;
      if (pqVar17 != puVar6) {
        uVar20 = ((ulong)((long)pqVar17 - (long)(puVar6 + 10)) >> 4) * 0xccccccccccccccd &
                 0xfffffffffffffff;
        lVar9 = uVar20 * 0x50 + local_f0 + 0x50;
        uVar5 = (int)((lVar9 - local_f0) - 0x50U >> 4) * -0x33333333 + 1U & 7;
        lVar12 = local_f0;
        if (uVar5 == 0) goto LAB_001a419c;
        if (uVar5 != 1) {
          lVar22 = local_f0;
          puVar19 = puVar6;
          if (uVar5 != 2) {
            if (uVar5 != 3) {
              if (uVar5 != 4) {
                if (uVar5 != 5) {
                  if (uVar5 != 6) {
                    FUN_00126150(local_f0,puVar6);
                    lVar22 = local_f0 + 0x50;
                    puVar19 = puVar6 + 10;
                  }
                  puVar6 = puVar19 + 10;
                  lVar12 = lVar22 + 0x50;
                  FUN_00126150(lVar22,puVar19);
                }
                puVar19 = puVar6 + 10;
                lVar22 = lVar12 + 0x50;
                FUN_00126150(lVar12,puVar6);
              }
              puVar6 = puVar19 + 10;
              lVar12 = lVar22 + 0x50;
              FUN_00126150(lVar22,puVar19);
            }
            puVar19 = puVar6 + 10;
            lVar22 = lVar12 + 0x50;
            FUN_00126150(lVar12,puVar6);
          }
          puVar6 = puVar19 + 10;
          lVar12 = lVar22 + 0x50;
          FUN_00126150(lVar22,puVar19);
        }
        FUN_00126150(lVar12,puVar6);
        puVar6 = puVar6 + 10;
        for (lVar12 = lVar12 + 0x50; lVar9 != lVar12; lVar12 = lVar12 + 0x280) {
LAB_001a419c:
          FUN_00126150(lVar12,puVar6);
          FUN_00126150(lVar12 + 0x50,puVar6 + 10);
          FUN_00126150(lVar12 + 0xa0,puVar6 + 0x14);
          FUN_00126150(lVar12 + 0xf0,puVar6 + 0x1e);
          FUN_00126150(lVar12 + 0x140,puVar6 + 0x28);
          FUN_00126150(lVar12 + 400,puVar6 + 0x32);
          FUN_00126150(lVar12 + 0x1e0,puVar6 + 0x3c);
          puVar19 = puVar6 + 0x46;
          puVar6 = puVar6 + 0x50;
          FUN_00126150(lVar12 + 0x230,puVar19);
        }
        lVar9 = (uVar20 * 5 + 5) * 0x10 + local_f0;
        pqVar7 = (qword *)(lVar9 + 0x50);
      }
      FUN_0048dcd0(lVar9,&local_88);
      FUN_0048dcd0(lVar9 + 0x18,local_70);
      *(undefined4 *)(lVar9 + 0x30) = local_58;
      *(undefined4 *)(lVar9 + 0x34) = local_54;
      FUN_0048dcd0(lVar9 + 0x38,local_50);
      lVar9 = *(long *)(lVar3 + 0x40);
      if (*(long *)(lVar3 + 0x48) != lVar9) {
        lVar12 = lVar9 + 0x50;
        lVar23 = ((((ulong)(*(long *)(lVar3 + 0x48) - lVar12) >> 4) * 0xccccccccccccccd &
                  0xfffffffffffffff) * 5 + 5) * 0x10 + lVar9;
        uVar5 = (int)((ulong)(lVar23 - lVar12) >> 4) * -0x33333333 & 7;
        lVar22 = lVar9;
        lVar25 = lVar12;
        if (uVar5 != 0) {
          FUN_0047fb60();
          lVar25 = lVar9 + 0xa0;
          lVar22 = lVar12;
          if (uVar5 != 1) {
            lVar26 = lVar12;
            lVar22 = lVar25;
            if (uVar5 != 2) {
              lVar26 = lVar25;
              if (uVar5 != 3) {
                lVar22 = lVar12;
                if (uVar5 != 4) {
                  lVar22 = lVar25;
                  if (uVar5 != 5) {
                    lVar22 = lVar12;
                    if (uVar5 != 6) {
                      FUN_0047fb60(lVar12);
                      lVar22 = lVar25;
                      lVar25 = lVar9 + 0xf0;
                    }
                    lVar12 = lVar25;
                    FUN_0047fb60(lVar22);
                    lVar22 = lVar12 + 0x50;
                  }
                  FUN_0047fb60(lVar12);
                  lVar25 = lVar22 + 0x50;
                }
                lVar12 = lVar25;
                FUN_0047fb60(lVar22);
                lVar26 = lVar12 + 0x50;
              }
              FUN_0047fb60(lVar12);
              lVar22 = lVar26 + 0x50;
            }
            FUN_0047fb60(lVar26);
            lVar25 = lVar22 + 0x50;
          }
        }
        for (; FUN_0047fb60(lVar22), lVar25 != lVar23; lVar25 = lVar25 + 0x280) {
          FUN_0047fb60(lVar25);
          FUN_0047fb60(lVar25 + 0x50);
          FUN_0047fb60(lVar25 + 0xa0);
          FUN_0047fb60(lVar25 + 0xf0);
          FUN_0047fb60(lVar25 + 0x140);
          lVar22 = lVar25 + 0x230;
          FUN_0047fb60(lVar25 + 400);
          FUN_0047fb60(lVar25 + 0x1e0);
        }
        lVar9 = *(long *)(lVar3 + 0x40);
      }
      if (lVar9 != 0) {
        HeapInterface::Free();
      }
      *(long *)(lVar3 + 0x40) = local_f0;
      *(qword **)(lVar3 + 0x48) = pqVar7;
      *(long *)(lVar3 + 0x50) = local_e8;
    }
  }
  else {
    local_f0 = *(long *)(lVar3 + 0x40);
  }
  if (1 < (int)((long)pqVar7 - local_f0 >> 4) * -0x33333333) {
    FUN_0018c7e0(local_f0,pqVar7);
  }
  if (*(long *)(lVar3 + 0x68) != 0) {
    (**(code **)(lVar3 + 0x70))(lVar3 + 0x58);
  }
LAB_001a3d75:
  FUN_0047fb60(&local_88);
  if ((local_91 < '\0') && (CONCAT71(uStack_a7,local_a8) != 0)) {
    eastl__basic_string();
  }
LAB_001a3d8e:
  if (-1 < local_b1) goto LAB_001a3b11;
  __s2 = (char *)CONCAT71(uStack_c7,local_c8);
  goto LAB_001a3d9c;
}


```

## `jag::packethandlers::Friends::UPDATE_FRIENDLIST` @ 001a46a0
```c

undefined *
jag::packethandlers::Friends::UPDATE_FRIENDLIST(long *param_1,PacketCore *param_2,int param_3)

{
  ulong *puVar1;
  char cVar2;
  byte bVar3;
  undefined1 uVar4;
  undefined4 uVar5;
  long lVar6;
  ulong *puVar7;
  ulong *puVar8;
  ulong uVar9;
  ulong uVar10;
  ulong uVar11;
  ulong uVar12;
  undefined8 uVar13;
  ulong uVar14;
  undefined8 uVar15;
  ulong uVar16;
  ulong uVar17;
  ulong uVar18;
  ulong uVar19;
  ulong uVar20;
  ulong uVar21;
  ulong uVar22;
  ulong uVar23;
  undefined8 uVar24;
  undefined8 uVar25;
  bool bVar26;
  ulong *puVar27;
  ushort uVar28;
  int iVar29;
  long *plVar30;
  undefined8 *puVar31;
  undefined8 *puVar32;
  undefined8 *puVar33;
  byte bVar34;
  long *plVar35;
  byte bVar36;
  char *pcVar37;
  long *plVar38;
  char *pcVar39;
  uint uVar40;
  ulong *puVar41;
  ulong uVar42;
  long lVar43;
  long lVar44;
  long lVar45;
  long lVar46;
  undefined8 *puVar47;
  undefined8 *puVar48;
  undefined8 *****pppppuVar49;
  char *__s1;
  undefined8 *****__s1_00;
  size_t sVar50;
  longlong lVar51;
  char cVar52;
  uint uVar53;
  ulong *puVar54;
  long *plVar55;
  long lVar56;
  Elf64_Phdr *pEVar57;
  int iVar58;
  char *pcVar59;
  long lVar60;
  size_t __n;
  qword *pqVar61;
  undefined8 ****local_1e0;
  undefined1 *local_1d8;
  uint local_198;
  uint local_194;
  undefined8 ****local_178;
  long local_170;
  char local_161 [9];
  char local_158;
  undefined7 uStack_157;
  char local_141;
  char local_138;
  undefined7 uStack_137;
  char local_121;
  char local_118;
  undefined7 uStack_117;
  char local_101;
  char local_f8;
  undefined7 uStack_f7;
  long local_f0;
  char local_e1 [9];
  ulong local_d8;
  ulong uStack_d0;
  undefined8 local_c8;
  undefined8 ****local_b8;
  ulong uStack_b0;
  undefined8 local_a8;
  undefined8 uStack_a0;
  ulong uStack_98;
  undefined8 local_90;
  uint local_88;
  ulong local_80;
  ulong uStack_78;
  undefined8 local_70;
  uint local_68;
  uint local_64;
  byte local_60;
  byte local_5f;
  uint local_5c;
  ulong local_58;
  ulong uStack_50;
  undefined8 local_48;
  
                    /* ServerProt op26 (0x1A), VarShort. CONFIRMED =
                       jag::ServerProt::UPDATE_FRIENDLIST (friend-list-update packet), NOT
                       UPDATE_SITESETTINGS.
                       
                       Per-friend record (loop while pos<len):
                         1. g1   warnMessage/flags byte (cVar2; later cVar2!=1 branch)
                         2. gStr displayName  (null-term CP1252 reader)
                         3. gStr previousName
                         4. g2   worldId (gT<unsigned_short>, BE)
                         5. g1   fcRank/chatRank byte
                         6. g1   flags byte: bit0 -> referredByYou, bit1 -> referredByThem
                         7. if worldId>0: { gStr worldName; g1 platform; g4 worldFlags BE }
                         8. gStr notes
                       Stores each record into a 0x78-stride friend vector at RelMgr+0x18/0x20/0x28
                       (RelMgr = *(Client+PLAYER_MANAGER region)), then sorts and at end sets
                       RelMgr+0x10 = 2 (LOADED_WITH_DATA) and runs PlayerList iterate -> IsFriend to
                       refresh per-player isFriend flags. Empty payload (size 0) => loop skipped,
                       still sets state=2 (friends tab loaded, empty). */
  lVar51 = param_2->position;
  lVar60 = *param_1;
  lVar6 = *(long *)((long)&__DT_RELA[0xcfe].r_info + lVar60);
  if ((int)lVar51 < param_3) {
LAB_001a4710:
    param_2->position = lVar51 + 1;
    cVar2 = *(char *)((long)param_2->bufData + lVar51);
    local_f8 = '\0';
    local_e1[0] = '\x17';
    local_118 = '\0';
    local_101 = '\x17';
    FUN_00ad80e0(param_2,&local_f8);
    FUN_00ad80e0(param_2,&local_118);
    uVar28 = FUN_00121a30(param_2);
    lVar60 = param_2->position;
    uVar40 = (uint)uVar28;
    local_194 = 0;
    local_198 = 0xffffffff;
    param_2->position = lVar60 + 1;
    bVar3 = *(byte *)((long)param_2->bufData + lVar60);
    param_2->position = lVar60 + 2;
    bVar36 = *(byte *)((long)param_2->bufData + lVar60 + 1);
    local_138 = '\0';
    local_121 = '\x17';
    bVar34 = bVar36 & 1;
    bVar36 = bVar36 >> 1 & 1;
    if (uVar40 != 0) {
      FUN_00ad80e0(param_2,&local_138);
      lVar60 = param_2->position;
      param_2->position = lVar60 + 1;
      local_198 = (uint)*(byte *)((long)param_2->bufData + lVar60);
      local_194 = Packet::gT_unsigned_int(param_2);
    }
    local_158 = '\0';
    local_141 = '\x17';
    FUN_00ad80e0(param_2,&local_158);
    lVar60 = *(long *)(lVar6 + 0x18);
    if ((int)(*(long *)(lVar6 + 0x20) - lVar60 >> 3) * -0x11111111 < 1) {
LAB_001a50ca:
      local_b8 = (undefined8 ****)((ulong)local_b8 & 0xffffffffffffff00);
      local_a8 = (code *)CONCAT17(0x17,(undefined7)local_a8);
      uStack_a0 = (code *)((ulong)uStack_a0 & 0xffffffffffffff00);
      local_90 = CONCAT17(0x17,(undefined7)local_90);
      local_80 = local_80 & 0xffffffffffffff00;
      local_70 = CONCAT17(0x17,(undefined7)local_70);
      local_58 = local_58 & 0xffffffffffffff00;
      local_48 = CONCAT17(0x17,(undefined7)local_48);
      pcVar39 = &local_f8;
      if (local_e1[0] < '\0') {
        pcVar39 = (char *)CONCAT71(uStack_f7,local_f8);
      }
      cVar2 = *pcVar39;
      while (cVar2 != '\0') {
        pcVar39 = pcVar39 + 1;
        cVar2 = *pcVar39;
      }
      FUN_00493d20(&local_b8);
      pcVar39 = &local_118;
      if (local_101 < '\0') {
        pcVar39 = (char *)CONCAT71(uStack_117,local_118);
      }
      cVar2 = *pcVar39;
      while (cVar2 != '\0') {
        pcVar39 = pcVar39 + 1;
        cVar2 = *pcVar39;
      }
      FUN_00493d20(&uStack_a0);
      pcVar39 = &local_138;
      local_88 = uVar40;
      if (local_121 < '\0') {
        pcVar39 = (char *)CONCAT71(uStack_137,local_138);
      }
      cVar2 = *pcVar39;
      while (cVar2 != '\0') {
        pcVar39 = pcVar39 + 1;
        cVar2 = *pcVar39;
      }
      FUN_00493d20(&local_80);
      local_68 = (uint)bVar3;
      local_64 = local_198;
      local_5c = local_194;
      pcVar39 = &local_158;
      if (local_141 < '\0') {
        pcVar39 = (char *)CONCAT71(uStack_157,local_158);
      }
      cVar2 = *pcVar39;
      while (cVar2 != '\0') {
        pcVar39 = pcVar39 + 1;
        cVar2 = *pcVar39;
      }
      local_60 = bVar36;
      local_5f = bVar34;
      FUN_00493d20(&local_58);
      puVar33 = *(undefined8 **)(lVar6 + 0x20);
      if (puVar33 < *(undefined8 **)(lVar6 + 0x28)) {
        *(undefined8 **)(lVar6 + 0x20) = puVar33 + 0xf;
        FUN_0048dcd0(puVar33,&local_b8);
        FUN_0048dcd0(puVar33 + 3,&uStack_a0);
        *(uint *)(puVar33 + 6) = local_88;
        FUN_0048dcd0(puVar33 + 7,&local_80);
        *(uint *)(puVar33 + 10) = local_68;
        *(byte *)(puVar33 + 0xb) = local_60;
        *(uint *)((long)puVar33 + 0x54) = local_64;
        *(byte *)((long)puVar33 + 0x59) = local_5f;
        *(uint *)((long)puVar33 + 0x5c) = local_5c;
        FUN_0048dcd0(puVar33 + 0xc,&local_58);
      }
      else {
        puVar48 = *(undefined8 **)(lVar6 + 0x18);
        lVar60 = (long)puVar33 - (long)puVar48 >> 3;
        if (lVar60 * -0x1111111111111111 == 0) {
          lVar60 = 0x78;
LAB_001a5d61:
          puVar31 = (undefined8 *)thunk_FUN_00c29480(lVar60);
          puVar33 = *(undefined8 **)(lVar6 + 0x20);
          local_1d8 = (undefined1 *)((long)puVar31 + lVar60);
          puVar48 = *(undefined8 **)(lVar6 + 0x18);
          pEVar57 = (Elf64_Phdr *)(puVar31 + 0xf);
        }
        else {
          if (lVar60 * -0x2222222222222222 != 0) {
            lVar60 = lVar60 << 4;
            goto LAB_001a5d61;
          }
          pEVar57 = Elf64_Phdr_ARRAY_00000040 + 1;
          local_1d8 = (undefined1 *)0x0;
          puVar31 = (undefined8 *)0x0;
        }
        puVar32 = puVar31;
        if (puVar33 != puVar48) {
          uVar42 = ((ulong)((long)puVar33 - (long)(puVar48 + 0xf)) >> 3) * 0xeeeeeeeeeeeeeef &
                   0x1fffffffffffffff;
          puVar33 = puVar31;
          do {
            *(undefined1 *)puVar33 = 0;
            *(undefined1 *)((long)puVar33 + 0x17) = 0x17;
            puVar32 = puVar33 + 0xf;
            uVar15 = *puVar33;
            uVar24 = puVar33[1];
            uVar13 = puVar33[2];
            uVar25 = puVar48[1];
            *puVar33 = *puVar48;
            puVar33[1] = uVar25;
            puVar33[2] = puVar48[2];
            *puVar48 = uVar15;
            puVar48[1] = uVar24;
            puVar48[2] = uVar13;
            *(undefined1 *)((long)puVar48 + 0x17) = 0x17;
            *(undefined1 *)(puVar33 + 3) = 0;
            *(undefined1 *)((long)puVar33 + 0x2f) = 0x17;
            uVar15 = puVar33[3];
            uVar24 = puVar33[4];
            uVar13 = puVar33[5];
            uVar25 = puVar48[4];
            puVar33[3] = puVar48[3];
            puVar33[4] = uVar25;
            puVar33[5] = puVar48[5];
            puVar48[3] = uVar15;
            puVar48[4] = uVar24;
            puVar48[5] = uVar13;
            *(undefined1 *)((long)puVar48 + 0x2f) = 0x17;
            *(undefined4 *)(puVar33 + 6) = *(undefined4 *)(puVar48 + 6);
            *(undefined1 *)(puVar33 + 7) = 0;
            uVar24 = puVar33[7];
            uVar25 = puVar33[8];
            *(undefined1 *)((long)puVar33 + 0x4f) = 0x17;
            uVar13 = puVar33[9];
            uVar15 = puVar48[8];
            puVar33[7] = puVar48[7];
            puVar33[8] = uVar15;
            puVar33[9] = puVar48[9];
            uVar4 = *(undefined1 *)(puVar48 + 0xb);
            puVar48[7] = uVar24;
            puVar48[8] = uVar25;
            puVar48[9] = uVar13;
            *(undefined1 *)((long)puVar48 + 0x4f) = 0x17;
            *(undefined4 *)(puVar33 + 10) = *(undefined4 *)(puVar48 + 10);
            uVar5 = *(undefined4 *)((long)puVar48 + 0x54);
            *(undefined1 *)(puVar33 + 0xb) = uVar4;
            uVar4 = *(undefined1 *)((long)puVar48 + 0x59);
            *(undefined4 *)((long)puVar33 + 0x54) = uVar5;
            uVar5 = *(undefined4 *)((long)puVar48 + 0x5c);
            *(undefined1 *)(puVar33 + 0xc) = 0;
            *(undefined1 *)((long)puVar33 + 0x77) = 0x17;
            local_d8 = puVar33[0xc];
            uStack_d0 = puVar33[0xd];
            *(undefined1 *)((long)puVar33 + 0x59) = uVar4;
            *(undefined4 *)((long)puVar33 + 0x5c) = uVar5;
            local_c8 = puVar33[0xe];
            uVar13 = puVar48[0xd];
            puVar33[0xc] = puVar48[0xc];
            puVar33[0xd] = uVar13;
            puVar33[0xe] = puVar48[0xe];
            puVar48[0xc] = local_d8;
            puVar48[0xd] = uStack_d0;
            puVar48[0xe] = local_c8;
            *(undefined1 *)((long)puVar48 + 0x77) = 0x17;
            puVar33 = puVar32;
            puVar48 = puVar48 + 0xf;
          } while (puVar31 + uVar42 * 0xf + 0xf != puVar32);
          pEVar57 = (Elf64_Phdr *)(puVar31 + (uVar42 + 1) * 0xf + 0xf);
          puVar32 = puVar31 + (uVar42 + 1) * 0xf;
        }
        FUN_0048dcd0(puVar32,&local_b8);
        FUN_0048dcd0(puVar32 + 3,&uStack_a0);
        *(uint *)(puVar32 + 6) = local_88;
        FUN_0048dcd0(puVar32 + 7,&local_80);
        *(uint *)(puVar32 + 10) = local_68;
        *(byte *)(puVar32 + 0xb) = local_60;
        *(uint *)((long)puVar32 + 0x54) = local_64;
        *(byte *)((long)puVar32 + 0x59) = local_5f;
        *(uint *)((long)puVar32 + 0x5c) = local_5c;
        FUN_0048dcd0(puVar32 + 0xc,&local_58);
        lVar60 = *(long *)(lVar6 + 0x18);
        if (*(long *)(lVar6 + 0x20) != lVar60) {
          lVar56 = lVar60 + 0x78;
          lVar45 = lVar60 + ((((ulong)(*(long *)(lVar6 + 0x20) - lVar56) >> 3) * 0xeeeeeeeeeeeeeef &
                             0x1fffffffffffffff) + 1) * 0x78;
          uVar40 = (int)((ulong)(lVar45 - lVar56) >> 3) * 7 & 7;
          lVar44 = lVar60;
          lVar43 = lVar56;
          if (uVar40 == 0) goto LAB_001a6151;
          FUN_00489ab0();
          lVar43 = lVar60 + 0xf0;
          lVar44 = lVar56;
          if (uVar40 == 1) goto LAB_001a6151;
          if (uVar40 != 2) {
            lVar44 = lVar43;
            if (uVar40 != 3) {
              lVar44 = lVar56;
              if (uVar40 != 4) {
                lVar44 = lVar43;
                if (uVar40 != 5) {
                  lVar44 = lVar56;
                  if (uVar40 != 6) {
                    FUN_00489ab0(lVar56);
                    lVar44 = lVar43;
                    lVar43 = lVar60 + 0x168;
                  }
                  lVar56 = lVar43;
                  FUN_00489ab0(lVar44);
                  lVar44 = lVar56 + 0x78;
                }
                FUN_00489ab0(lVar56);
                lVar43 = lVar44 + 0x78;
              }
              lVar56 = lVar43;
              FUN_00489ab0(lVar44);
              lVar44 = lVar56 + 0x78;
            }
            FUN_00489ab0(lVar56);
            lVar43 = lVar44 + 0x78;
          }
          FUN_00489ab0(lVar44);
          FUN_00489ab0(lVar43);
          lVar43 = lVar43 + 0x78;
          while (lVar43 != lVar45) {
            FUN_00489ab0(lVar43);
            FUN_00489ab0(lVar43 + 0x78);
            FUN_00489ab0(lVar43 + 0xf0);
            lVar60 = lVar43 + 600;
            FUN_00489ab0(lVar43 + 0x168);
            lVar56 = lVar43 + 0x2d0;
            FUN_00489ab0(lVar43 + 0x1e0);
            lVar44 = lVar43 + 0x348;
            lVar43 = lVar43 + 0x3c0;
            FUN_00489ab0(lVar60);
            FUN_00489ab0(lVar56);
LAB_001a6151:
            FUN_00489ab0(lVar44);
          }
          lVar60 = *(long *)(lVar6 + 0x18);
        }
        if (lVar60 != 0) {
          HeapInterface::Free();
        }
        *(undefined8 **)(lVar6 + 0x18) = puVar31;
        *(Elf64_Phdr **)(lVar6 + 0x20) = pEVar57;
        *(undefined1 **)(lVar6 + 0x28) = local_1d8;
      }
      FUN_00489ab0(&local_b8);
    }
    else {
      lVar56 = 0;
      iVar58 = 0;
      do {
        cVar52 = local_e1[0];
        pcVar39 = (char *)(lVar60 + lVar56);
        if (cVar2 != '\x01') {
          pcVar37 = pcVar39;
          if (pcVar39[0x17] < '\0') {
            pcVar37 = *(char **)pcVar39;
          }
          pcVar59 = pcVar37;
          if (*pcVar37 == '\0') {
            lVar60 = 0;
          }
          else {
            do {
              pcVar59 = pcVar59 + 1;
            } while (*pcVar59 != '\0');
            lVar60 = (long)pcVar59 - (long)pcVar37;
          }
          if (local_e1[0] < '\0') {
            __s1 = (char *)CONCAT71(uStack_f7,local_f8);
            pcVar59 = __s1 + local_f0;
          }
          else {
            pcVar59 = local_e1 + -(long)local_e1[0];
            __s1 = &local_f8;
          }
          sVar50 = (long)pcVar59 - (long)__s1;
          if ((((long)sVar50 <= lVar60) && (iVar29 = memcmp(__s1,pcVar37,sVar50), iVar29 == 0)) &&
             (lVar60 <= (long)sVar50)) {
            if (uVar40 == *(uint *)(pcVar39 + 0x30)) goto LAB_001a48df;
            puVar33 = *(undefined8 **)(lVar6 + 0x50);
            lVar60 = *(long *)(lVar6 + 0x48);
            if ((int)((long)puVar33 - lVar60 >> 3) * -0x33333333 < 1) goto LAB_001a5697;
            lVar56 = 0;
            bVar26 = true;
            iVar58 = 0;
            goto LAB_001a556a;
          }
        }
        FUN_006d4360(&local_178,&local_118,DAT_01391258);
        uVar5 = DAT_01391258;
        pcVar37 = pcVar39;
        if (pcVar39[0x17] < '\0') {
          pcVar37 = *(char **)pcVar39;
        }
        local_d8 = local_d8 & 0xffffffffffffff00;
        local_c8 = CONCAT17(0x17,(undefined7)local_c8);
        cVar52 = *pcVar37;
        while (cVar52 != '\0') {
          pcVar37 = pcVar37 + 1;
          cVar52 = *pcVar37;
        }
        FUN_0048d1a0(&local_d8);
        FUN_006d4360(&local_b8,&local_d8,uVar5);
        uVar42 = (ulong)local_a8;
        if ((long)local_a8 < 0) {
          lVar60 = uStack_b0 + (long)local_b8;
          pppppuVar49 = (undefined8 *****)local_b8;
        }
        else {
          lVar60 = (long)&local_a8 + (7 - (long)local_a8._7_1_);
          pppppuVar49 = &local_b8;
        }
        if (local_161[0] < '\0') {
          pcVar37 = (char *)(local_170 + (long)local_178);
          __s1_00 = (undefined8 *****)local_178;
        }
        else {
          pcVar37 = local_161 + -(long)local_161[0];
          __s1_00 = &local_178;
        }
        sVar50 = (long)pcVar37 - (long)__s1_00;
        __n = lVar60 - (long)pppppuVar49;
        if ((long)sVar50 <= (long)__n) {
          uVar53 = memcmp(__s1_00,pppppuVar49,sVar50);
          if (uVar53 == 0) {
            if ((long)__n <= (long)sVar50) goto LAB_001a505a;
            uVar53 = 0xffffffff;
          }
LAB_001a5062:
          if (-1 < (long)uVar42) goto LAB_001a506b;
LAB_001a52b3:
          if ((undefined8 *****)local_b8 == (undefined8 *****)0x0) goto LAB_001a506b;
          eastl__basic_string();
          if ((long)local_c8 < 0) goto LAB_001a52e0;
LAB_001a5079:
          if (uVar53 != 0) goto LAB_001a5081;
LAB_001a5306:
          pcVar37 = &local_f8;
          if (local_e1[0] < '\0') {
            pcVar37 = (char *)CONCAT71(uStack_f7,local_f8);
          }
          cVar2 = *pcVar37;
          while (cVar2 != '\0') {
            pcVar37 = pcVar37 + 1;
            cVar2 = *pcVar37;
          }
          FUN_00493d20(pcVar39);
          pcVar37 = &local_118;
          if (local_101 < '\0') {
            pcVar37 = (char *)CONCAT71(uStack_117,local_118);
          }
          cVar2 = *pcVar37;
          while (cVar2 != '\0') {
            pcVar37 = pcVar37 + 1;
            cVar2 = *pcVar37;
          }
          FUN_00493d20(pcVar39 + 0x18);
          if ((local_161[0] < '\0') && ((undefined8 *****)local_178 != (undefined8 *****)0x0)) {
            eastl__basic_string();
          }
          goto joined_r0x001a53a7;
        }
        uVar53 = memcmp(__s1_00,pppppuVar49,__n);
        if (uVar53 == 0) {
LAB_001a505a:
          uVar53 = (uint)((long)__n < (long)sVar50);
          goto LAB_001a5062;
        }
        if ((long)uVar42 < 0) goto LAB_001a52b3;
LAB_001a506b:
        if (-1 < (long)local_c8) goto LAB_001a5079;
LAB_001a52e0:
        if (local_d8 == 0) goto LAB_001a5079;
        eastl__basic_string();
        if (uVar53 == 0) goto LAB_001a5306;
LAB_001a5081:
        if ((local_161[0] < '\0') && ((undefined8 *****)local_178 != (undefined8 *****)0x0)) {
          eastl__basic_string();
        }
        lVar60 = *(long *)(lVar6 + 0x18);
        iVar58 = iVar58 + 1;
        lVar56 = lVar56 + 0x78;
        iVar29 = (int)(*(long *)(lVar6 + 0x20) - lVar60 >> 3) * -0x11111111;
      } while (iVar58 < iVar29);
      if (iVar29 < 400) goto LAB_001a50ca;
    }
    goto joined_r0x001a53a7;
  }
LAB_001a4a42:
  local_1e0 = &local_b8;
  lVar56 = *(long *)((long)&__DT_RELA[0xd00].r_offset + lVar60);
  puVar7 = *(ulong **)(lVar6 + 0x20);
  puVar8 = *(ulong **)(lVar6 + 0x18);
  *(undefined4 *)(lVar6 + 0x10) = 2;
  *(undefined4 *)(lVar56 + 0xf0) = *(undefined4 *)(*(long *)(lVar56 + 0xe8) + 0x10);
  if ((puVar7 != puVar8) && (puVar54 = puVar8 + 0xf, puVar7 != puVar54)) {
    do {
      local_b8 = (undefined8 ****)((ulong)local_b8 & 0xffffffffffffff00);
      local_a8 = (code *)CONCAT17(0x17,(undefined7)local_a8);
      pppppuVar49 = (undefined8 *****)*puVar54;
      uVar14 = puVar54[1];
      uVar42 = puVar54[2];
      *puVar54 = (ulong)local_b8;
      puVar54[1] = uStack_b0;
      puVar54[2] = (ulong)local_a8;
      *(undefined1 *)((long)puVar54 + 0x17) = 0x17;
      uStack_a0 = (code *)((ulong)uStack_a0 & 0xffffffffffffff00);
      local_90 = CONCAT17(0x17,(undefined7)local_90);
      uVar16 = puVar54[3];
      uVar17 = puVar54[4];
      uVar9 = puVar54[5];
      local_88 = (uint)puVar54[6];
      puVar54[3] = (ulong)uStack_a0;
      puVar54[4] = uStack_98;
      puVar54[5] = local_90;
      *(undefined1 *)((long)puVar54 + 0x2f) = 0x17;
      local_80 = local_80 & 0xffffffffffffff00;
      local_70 = CONCAT17(0x17,(undefined7)local_70);
      uVar18 = puVar54[7];
      uVar19 = puVar54[8];
      uVar10 = puVar54[9];
      local_68 = (uint)puVar54[10];
      puVar54[7] = local_80;
      puVar54[8] = uStack_78;
      local_60 = (byte)puVar54[0xb];
      puVar54[9] = local_70;
      *(undefined1 *)((long)puVar54 + 0x4f) = 0x17;
      local_64 = *(uint *)((long)puVar54 + 0x54);
      local_5f = *(byte *)((long)puVar54 + 0x59);
      local_5c = *(uint *)((long)puVar54 + 0x5c);
      local_58 = local_58 & 0xffffffffffffff00;
      local_48 = CONCAT17(0x17,(undefined7)local_48);
      uVar20 = puVar54[0xc];
      uVar21 = puVar54[0xd];
      uVar11 = puVar54[0xe];
      puVar54[0xc] = local_58;
      puVar54[0xd] = uStack_50;
      puVar54[0xe] = local_48;
      *(undefined1 *)((long)puVar54 + 0x77) = 0x17;
      puVar27 = puVar54;
      local_d8 = local_58;
      uStack_d0 = uStack_50;
      local_c8 = local_48;
      while (puVar41 = puVar8, puVar8 != puVar27) {
        puVar1 = puVar27 + -0xf;
        uVar40 = (uint)puVar27[-9];
        uVar53 = *(uint *)(*(long *)(*(long *)((long)&__DT_RELA[0xd01].r_offset +
                                              *(long *)(lVar6 + 8)) + 0x20) + 8);
        puVar41 = puVar27;
        if (uVar53 == local_88) {
          if (local_88 == uVar40) {
            if (uVar40 != 0) goto LAB_001a4d9e;
            goto LAB_001a4da3;
          }
        }
        else {
          if (uVar53 == uVar40) break;
          if (local_88 == 0) {
            if (uVar40 != 0) break;
          }
          else {
LAB_001a4d9e:
            if (uVar40 == 0) goto LAB_001a4dd3;
          }
LAB_001a4da3:
          if (local_60 == 0) {
            if ((char)puVar27[-4] != '\0') break;
          }
          else if ((char)puVar27[-4] == '\0') goto LAB_001a4dd3;
          if ((local_5f == 0) || (*(char *)((long)puVar27 + -0x1f) != '\0')) break;
        }
LAB_001a4dd3:
        uVar22 = *puVar27;
        uVar23 = puVar27[1];
        uVar12 = puVar27[2];
        *puVar27 = *puVar1;
        puVar27[1] = puVar27[-0xe];
        puVar27[2] = puVar27[-0xd];
        *puVar1 = uVar22;
        puVar27[-0xe] = uVar23;
        uVar22 = puVar27[3];
        uVar23 = puVar27[4];
        puVar27[-0xd] = uVar12;
        uVar12 = puVar27[5];
        puVar27[3] = puVar27[-0xc];
        puVar27[4] = puVar27[-0xb];
        puVar27[5] = puVar27[-10];
        puVar27[-0xc] = uVar22;
        puVar27[-0xb] = uVar23;
        *(uint *)(puVar27 + 6) = uVar40;
        uVar22 = puVar27[7];
        uVar23 = puVar27[8];
        puVar27[-10] = uVar12;
        uVar12 = puVar27[9];
        puVar27[7] = puVar27[-8];
        puVar27[8] = puVar27[-7];
        puVar27[9] = puVar27[-6];
        puVar27[-8] = uVar22;
        puVar27[-7] = uVar23;
        puVar27[-6] = uVar12;
        local_d8 = puVar27[0xc];
        uStack_d0 = puVar27[0xd];
        *(int *)(puVar27 + 10) = (int)puVar27[-5];
        *(undefined4 *)((long)puVar27 + 0x54) = *(undefined4 *)((long)puVar27 + -0x24);
        *(char *)(puVar27 + 0xb) = (char)puVar27[-4];
        *(undefined1 *)((long)puVar27 + 0x59) = *(undefined1 *)((long)puVar27 + -0x1f);
        *(undefined4 *)((long)puVar27 + 0x5c) = *(undefined4 *)((long)puVar27 + -0x1c);
        local_c8 = puVar27[0xe];
        puVar27[0xc] = puVar27[-3];
        puVar27[0xd] = puVar27[-2];
        puVar27[0xe] = puVar27[-1];
        puVar27[-3] = local_d8;
        puVar27[-2] = uStack_d0;
        puVar27[-1] = local_c8;
        puVar27 = puVar1;
      }
      puVar54 = puVar54 + 0xf;
      local_b8 = pppppuVar49;
      uStack_b0 = uVar14;
      local_a8 = (code *)uVar42;
      uStack_a0 = (code *)uVar16;
      uStack_98 = uVar17;
      local_90 = uVar9;
      local_80 = uVar18;
      uStack_78 = uVar19;
      local_70 = uVar10;
      local_58 = uVar20;
      uStack_50 = uVar21;
      local_48 = uVar11;
      FUN_00126110(puVar41,local_1e0);
      FUN_00126110(puVar41 + 3,&uStack_a0);
      *(uint *)(puVar41 + 6) = local_88;
      FUN_00126110(puVar41 + 7,&local_80);
      *(uint *)(puVar41 + 10) = local_68;
      *(byte *)(puVar41 + 0xb) = local_60;
      *(uint *)((long)puVar41 + 0x54) = local_64;
      *(byte *)((long)puVar41 + 0x59) = local_5f;
      *(uint *)((long)puVar41 + 0x5c) = local_5c;
      FUN_00126110(puVar41 + 0xc,&local_58);
      FUN_00489ab0(local_1e0);
    } while (puVar54 !=
             puVar8 + (((ulong)((long)puVar7 - (long)(puVar8 + 0x1e)) >> 3) * 0xeeeeeeeeeeeeeef &
                      0x1fffffffffffffff) * 0xf + 0x1e);
    lVar60 = *param_1;
  }
  local_a8 = FUN_000ec0e0;
  uStack_a0 = FUN_00143120;
  FUN_00143a20(*(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + lVar60),local_1e0);
  if (local_a8 != (code *)0x0) {
    (*local_a8)(local_1e0,local_1e0,3);
  }
  return &DAT_015d3620;
LAB_001a556a:
  do {
    puVar31 = (undefined8 *)(lVar60 + lVar56);
    puVar48 = puVar31 + 1;
    pcVar37 = &local_f8;
    if (cVar52 < '\0') {
      pcVar37 = (char *)CONCAT71(uStack_f7,local_f8);
    }
    iVar29 = eastl_string_compare_cstr(puVar48,pcVar37);
    if (iVar29 == 0) {
      if (uVar40 == 0) {
        if (*(int *)(puVar31 + 4) != 0) {
          if ((puVar31 + 5 < puVar33) &&
             (lVar60 = (long)puVar33 - (long)(puVar31 + 5),
             uVar42 = (lVar60 >> 3) * -0x3333333333333333, 0 < lVar60)) {
            puVar33 = puVar31 + 6;
            uVar53 = (uint)uVar42 & 3;
            puVar32 = puVar48;
            if ((uVar42 & 3) != 0) {
              puVar47 = puVar33;
              if (uVar53 != 1) {
                if (uVar53 != 2) {
                  puVar32 = puVar31 + 6;
                  *puVar31 = puVar31[5];
                  FUN_00126110(puVar48,puVar33);
                  puVar33 = puVar31 + 0xb;
                  *(undefined4 *)(puVar31 + 4) = *(undefined4 *)(puVar31 + 9);
                  uVar42 = uVar42 - 1;
                }
                puVar48 = puVar32 + 5;
                puVar33[-6] = puVar33[-1];
                FUN_00126110(puVar32,puVar33);
                uVar42 = uVar42 - 1;
                puVar47 = puVar33 + 5;
                *(undefined4 *)(puVar33 + -2) = *(undefined4 *)(puVar33 + 3);
              }
              puVar47[-6] = puVar47[-1];
              puVar32 = puVar48 + 5;
              FUN_00126110(puVar48,puVar47);
              puVar33 = puVar47 + 5;
              *(undefined4 *)(puVar47 + -2) = *(undefined4 *)(puVar47 + 3);
              uVar42 = uVar42 - 1;
              if (uVar42 == 0) goto LAB_001a55b2;
            }
            do {
              puVar33[-6] = puVar33[-1];
              FUN_00126110(puVar32,puVar33);
              puVar33[-1] = puVar33[4];
              *(undefined4 *)(puVar33 + -2) = *(undefined4 *)(puVar33 + 3);
              FUN_00126110(puVar32 + 5,puVar33 + 5);
              *(undefined4 *)(puVar33 + 3) = *(undefined4 *)(puVar33 + 8);
              puVar33[4] = puVar33[9];
              FUN_00126110(puVar32 + 10,puVar33 + 10);
              *(undefined4 *)(puVar33 + 8) = *(undefined4 *)(puVar33 + 0xd);
              puVar33[9] = puVar33[0xe];
              FUN_00126110(puVar32 + 0xf,puVar33 + 0xf);
              *(undefined4 *)(puVar33 + 0xd) = *(undefined4 *)(puVar33 + 0x12);
              uVar42 = uVar42 - 4;
              puVar33 = puVar33 + 0x14;
              puVar32 = puVar32 + 0x14;
            } while (uVar42 != 0);
          }
          goto LAB_001a55b2;
        }
        goto LAB_001a5530;
      }
      if (*(int *)(puVar31 + 4) != 0) goto LAB_001a5530;
      if ((puVar31 + 5 < puVar33) &&
         (lVar60 = (long)puVar33 - (long)(puVar31 + 5), uVar42 = (lVar60 >> 3) * -0x3333333333333333
         , 0 < lVar60)) {
        puVar33 = puVar31 + 6;
        uVar53 = (uint)uVar42 & 3;
        puVar32 = puVar33;
        puVar47 = puVar48;
        if ((uVar42 & 3) != 0) {
          if (uVar53 != 1) {
            if (uVar53 != 2) {
              puVar47 = puVar31 + 6;
              *puVar31 = puVar31[5];
              FUN_00126110(puVar48,puVar33);
              puVar32 = puVar31 + 0xb;
              *(undefined4 *)(puVar31 + 4) = *(undefined4 *)(puVar31 + 9);
              uVar42 = uVar42 - 1;
            }
            puVar48 = puVar47 + 5;
            puVar32[-6] = puVar32[-1];
            FUN_00126110(puVar47,puVar32);
            uVar42 = uVar42 - 1;
            puVar33 = puVar32 + 5;
            *(undefined4 *)(puVar32 + -2) = *(undefined4 *)(puVar32 + 3);
          }
          puVar47 = puVar48 + 5;
          puVar33[-6] = puVar33[-1];
          FUN_00126110(puVar48,puVar33);
          puVar32 = puVar33 + 5;
          *(undefined4 *)(puVar33 + -2) = *(undefined4 *)(puVar33 + 3);
          uVar42 = uVar42 - 1;
          if (uVar42 == 0) goto LAB_001a55b2;
        }
        do {
          puVar32[-6] = puVar32[-1];
          FUN_00126110(puVar47,puVar32);
          *(undefined4 *)(puVar32 + -2) = *(undefined4 *)(puVar32 + 3);
          puVar32[-1] = puVar32[4];
          FUN_00126110(puVar47 + 5,puVar32 + 5);
          *(undefined4 *)(puVar32 + 3) = *(undefined4 *)(puVar32 + 8);
          puVar32[4] = puVar32[9];
          FUN_00126110(puVar47 + 10,puVar32 + 10);
          *(undefined4 *)(puVar32 + 8) = *(undefined4 *)(puVar32 + 0xd);
          puVar32[9] = puVar32[0xe];
          FUN_00126110(puVar47 + 0xf,puVar32 + 0xf);
          *(undefined4 *)(puVar32 + 0xd) = *(undefined4 *)(puVar32 + 0x12);
          uVar42 = uVar42 - 4;
          puVar32 = puVar32 + 0x14;
          puVar47 = puVar47 + 0x14;
        } while (uVar42 != 0);
      }
LAB_001a55b2:
      lVar60 = *(long *)(lVar6 + 0x50);
      puVar33 = (undefined8 *)(lVar60 + -0x28);
      *(undefined8 **)(lVar6 + 0x50) = puVar33;
      if ((*(char *)(lVar60 + -9) < '\0') && (*(long *)(lVar60 + -0x20) != 0)) {
        HeapInterface::Free();
        puVar33 = *(undefined8 **)(lVar6 + 0x50);
      }
      bVar26 = false;
    }
    else {
LAB_001a5530:
      puVar33 = *(undefined8 **)(lVar6 + 0x50);
    }
    lVar60 = *(long *)(lVar6 + 0x48);
    iVar58 = iVar58 + 1;
    lVar56 = lVar56 + 0x28;
    cVar52 = local_e1[0];
  } while (iVar58 < (int)((long)puVar33 - lVar60 >> 3) * -0x33333333);
  if (!bVar26) goto LAB_001a48df;
LAB_001a5697:
  uStack_b0 = uStack_b0 & 0xffffffffffffff00;
  uStack_a0 = (code *)CONCAT17(0x17,(undefined7)uStack_a0);
  lVar60 = std__chrono___V2__steady_clock__now();
  local_b8 = (undefined8 ****)(lVar60 / 1000000000);
  pcVar37 = &local_f8;
  if (local_e1[0] < '\0') {
    pcVar37 = (char *)CONCAT71(uStack_f7,local_f8);
  }
  cVar2 = *pcVar37;
  while (cVar2 != '\0') {
    pcVar37 = pcVar37 + 1;
    cVar2 = *pcVar37;
  }
  FUN_00493d20(&uStack_b0);
  plVar35 = *(long **)(lVar6 + 0x50);
  uStack_98 = CONCAT44(uStack_98._4_4_,uVar40);
  if (plVar35 < *(long **)(lVar6 + 0x58)) {
    *(long **)(lVar6 + 0x50) = plVar35 + 5;
    *plVar35 = (long)local_b8;
    FUN_0048dcd0(plVar35 + 1,&uStack_b0);
    *(undefined4 *)(plVar35 + 4) = (undefined4)uStack_98;
  }
  else {
    plVar38 = *(long **)(lVar6 + 0x48);
    lVar60 = (long)plVar35 - (long)plVar38 >> 3;
    if (lVar60 * -0x3333333333333333 == 0) {
      lVar60 = 0x28;
LAB_001a5758:
      plVar30 = (long *)thunk_FUN_00c29480(lVar60);
      plVar35 = *(long **)(lVar6 + 0x50);
      local_1d8 = (undefined1 *)((long)plVar30 + lVar60);
      plVar38 = *(long **)(lVar6 + 0x48);
      pqVar61 = (qword *)(plVar30 + 5);
    }
    else {
      if (lVar60 * -0x6666666666666666 != 0) {
        lVar60 = lVar60 << 4;
        goto LAB_001a5758;
      }
      pqVar61 = &Elf64_Ehdr_00000000.e_shoff;
      local_1d8 = (undefined1 *)0x0;
      plVar30 = (long *)0x0;
    }
    plVar55 = plVar30;
    if (plVar35 != plVar38) {
      plVar55 = plVar38 + 5;
      uVar42 = ((ulong)((long)plVar35 - (long)plVar55) >> 3) * 0xccccccccccccccd &
               0x1fffffffffffffff;
      plVar35 = plVar30;
      if (((int)((ulong)((long)(plVar55 + uVar42 * 5) + (-0x28 - (long)plVar38)) >> 3) * -0x33333333
          & 1U) != 0) goto LAB_001a5841;
      lVar60 = *plVar38;
      *(undefined1 *)(plVar30 + 1) = 0;
      local_d8 = plVar30[1];
      uStack_d0 = plVar30[2];
      *(undefined1 *)((long)plVar30 + 0x1f) = 0x17;
      *plVar30 = lVar60;
      local_c8 = plVar30[3];
      lVar60 = plVar38[2];
      plVar30[1] = plVar38[1];
      plVar30[2] = lVar60;
      plVar30[3] = plVar38[3];
      plVar38[1] = local_d8;
      plVar38[2] = uStack_d0;
      plVar38[3] = local_c8;
      *(undefined1 *)((long)plVar38 + 0x1f) = 0x17;
      *(int *)(plVar30 + 4) = (int)plVar38[4];
      plVar35 = plVar30 + 5;
      for (plVar38 = plVar55; plVar55 + uVar42 * 5 != plVar38; plVar38 = plVar38 + 10) {
LAB_001a5841:
        lVar60 = *plVar38;
        *(undefined1 *)(plVar35 + 1) = 0;
        lVar44 = plVar35[1];
        lVar45 = plVar35[2];
        *(undefined1 *)((long)plVar35 + 0x1f) = 0x17;
        *plVar35 = lVar60;
        lVar60 = plVar35[3];
        lVar56 = plVar38[2];
        plVar35[1] = plVar38[1];
        plVar35[2] = lVar56;
        plVar35[3] = plVar38[3];
        lVar43 = plVar38[4];
        lVar56 = plVar38[5];
        plVar38[1] = lVar44;
        plVar38[2] = lVar45;
        plVar38[3] = lVar60;
        *(undefined1 *)((long)plVar38 + 0x1f) = 0x17;
        *(undefined1 *)(plVar35 + 6) = 0;
        *(undefined1 *)((long)plVar35 + 0x47) = 0x17;
        local_d8 = plVar35[6];
        uStack_d0 = plVar35[7];
        *(int *)(plVar35 + 4) = (int)lVar43;
        plVar35[5] = lVar56;
        local_c8 = plVar35[8];
        lVar60 = plVar38[7];
        plVar35[6] = plVar38[6];
        plVar35[7] = lVar60;
        plVar35[8] = plVar38[8];
        plVar38[6] = local_d8;
        plVar38[7] = uStack_d0;
        plVar38[8] = local_c8;
        *(undefined1 *)((long)plVar38 + 0x47) = 0x17;
        *(int *)(plVar35 + 9) = (int)plVar38[9];
        plVar35 = plVar35 + 10;
      }
      pqVar61 = (qword *)(plVar30 + uVar42 * 5 + 5 + 5);
      plVar55 = plVar30 + uVar42 * 5 + 5;
    }
    *plVar55 = (long)local_b8;
    FUN_0048dcd0(plVar55 + 1,&uStack_b0);
    lVar60 = *(long *)(lVar6 + 0x48);
    lVar56 = *(long *)(lVar6 + 0x50);
    *(undefined4 *)(plVar55 + 4) = (undefined4)uStack_98;
    if (lVar56 != lVar60) {
      lVar44 = lVar60 + 0x28;
      lVar56 = lVar60 + 0x28 + (lVar56 - lVar44 & 0xfffffffffffffff8U);
      uVar42 = ((ulong)(lVar56 - lVar44) >> 3) * 5;
      uVar53 = (uint)uVar42 & 7;
      lVar45 = lVar60;
      lVar43 = lVar44;
      if ((uVar42 & 7) != 0) {
        if ((*(char *)(lVar60 + 0x1f) < '\0') && (*(long *)(lVar60 + 8) != 0)) {
          HeapInterface::Free();
        }
        lVar43 = lVar60 + 0x50;
        lVar45 = lVar44;
        if (uVar53 != 1) {
          lVar46 = lVar44;
          lVar45 = lVar43;
          if (uVar53 != 2) {
            lVar46 = lVar43;
            if (uVar53 != 3) {
              lVar45 = lVar44;
              if (uVar53 != 4) {
                lVar45 = lVar43;
                if (uVar53 != 5) {
                  lVar45 = lVar44;
                  lVar44 = lVar43;
                  if (uVar53 != 6) {
                    if ((*(char *)(lVar60 + 0x47) < '\0') && (*(long *)(lVar60 + 0x30) != 0)) {
                      HeapInterface::Free();
                    }
                    lVar44 = lVar60 + 0x78;
                    lVar45 = lVar43;
                  }
                  if ((*(char *)(lVar45 + 0x1f) < '\0') && (*(long *)(lVar45 + 8) != 0)) {
                    HeapInterface::Free();
                  }
                  lVar45 = lVar44 + 0x28;
                }
                if ((*(char *)(lVar44 + 0x1f) < '\0') && (*(long *)(lVar44 + 8) != 0)) {
                  HeapInterface::Free();
                }
                lVar43 = lVar45 + 0x28;
              }
              lVar44 = lVar43;
              if ((*(char *)(lVar45 + 0x1f) < '\0') && (*(long *)(lVar45 + 8) != 0)) {
                HeapInterface::Free();
              }
              lVar46 = lVar44 + 0x28;
            }
            if ((*(char *)(lVar44 + 0x1f) < '\0') && (*(long *)(lVar44 + 8) != 0)) {
              HeapInterface::Free();
            }
            lVar45 = lVar46 + 0x28;
          }
          if ((*(char *)(lVar46 + 0x1f) < '\0') && (*(long *)(lVar46 + 8) != 0)) {
            HeapInterface::Free();
          }
          lVar43 = lVar45 + 0x28;
        }
      }
      cVar2 = *(char *)(lVar45 + 0x1f);
      while( true ) {
        if ((cVar2 < '\0') && (*(long *)(lVar45 + 8) != 0)) {
          HeapInterface::Free();
        }
        if (lVar43 == lVar56) break;
        if ((*(char *)(lVar43 + 0x1f) < '\0') && (*(long *)(lVar43 + 8) != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)(lVar43 + 0x47) < '\0') && (*(long *)(lVar43 + 0x30) != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)(lVar43 + 0x6f) < '\0') && (*(long *)(lVar43 + 0x58) != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)(lVar43 + 0x97) < '\0') && (*(long *)(lVar43 + 0x80) != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)(lVar43 + 0xbf) < '\0') && (*(long *)(lVar43 + 0xa8) != 0)) {
          HeapInterface::Free();
        }
        if ((*(char *)(lVar43 + 0xe7) < '\0') && (*(long *)(lVar43 + 0xd0) != 0)) {
          HeapInterface::Free();
        }
        lVar45 = lVar43 + 0x118;
        if ((*(char *)(lVar43 + 0x10f) < '\0') && (*(long *)(lVar43 + 0xf8) != 0)) {
          HeapInterface::Free();
        }
        cVar2 = *(char *)(lVar43 + 0x137);
        lVar43 = lVar43 + 0x140;
      }
      lVar60 = *(long *)(lVar6 + 0x48);
    }
    if (lVar60 != 0) {
      HeapInterface::Free();
    }
    *(long **)(lVar6 + 0x48) = plVar30;
    *(qword **)(lVar6 + 0x50) = pqVar61;
    *(undefined1 **)(lVar6 + 0x58) = local_1d8;
  }
  cVar52 = local_e1[0];
  if (((long)uStack_a0 < 0) && (uStack_b0 != 0)) {
    HeapInterface::Free();
    cVar52 = local_e1[0];
  }
LAB_001a48df:
  pcVar37 = &local_f8;
  if (cVar52 < '\0') {
    pcVar37 = (char *)CONCAT71(uStack_f7,local_f8);
  }
  cVar2 = *pcVar37;
  while (cVar2 != '\0') {
    pcVar37 = pcVar37 + 1;
    cVar2 = *pcVar37;
  }
  FUN_00493d20(pcVar39);
  pcVar37 = &local_118;
  if (local_101 < '\0') {
    pcVar37 = (char *)CONCAT71(uStack_117,local_118);
  }
  cVar2 = *pcVar37;
  while (cVar2 != '\0') {
    pcVar37 = pcVar37 + 1;
    cVar2 = *pcVar37;
  }
  FUN_00493d20(pcVar39 + 0x18);
  pcVar37 = &local_138;
  *(uint *)(pcVar39 + 0x30) = uVar40;
  if (local_121 < '\0') {
    pcVar37 = (char *)CONCAT71(uStack_137,local_138);
  }
  cVar2 = *pcVar37;
  while (cVar2 != '\0') {
    pcVar37 = pcVar37 + 1;
    cVar2 = *pcVar37;
  }
  FUN_00493d20(pcVar39 + 0x38);
  pcVar39[0x59] = bVar34;
  *(uint *)(pcVar39 + 0x50) = (uint)bVar3;
  *(uint *)(pcVar39 + 0x54) = local_198;
  pcVar39[0x58] = bVar36;
  *(uint *)(pcVar39 + 0x5c) = local_194;
  pcVar37 = &local_158;
  if (local_141 < '\0') {
    pcVar37 = (char *)CONCAT71(uStack_157,local_158);
  }
  cVar2 = *pcVar37;
  while (cVar2 != '\0') {
    pcVar37 = pcVar37 + 1;
    cVar2 = *pcVar37;
  }
  FUN_00493d20(pcVar39 + 0x60);
joined_r0x001a53a7:
  if ((local_141 < '\0') && (CONCAT71(uStack_157,local_158) != 0)) {
    eastl__basic_string();
  }
  if ((local_121 < '\0') && (CONCAT71(uStack_137,local_138) != 0)) {
    eastl__basic_string();
  }
  if ((local_101 < '\0') && (CONCAT71(uStack_117,local_118) != 0)) {
    eastl__basic_string();
  }
  if ((local_e1[0] < '\0') && (CONCAT71(uStack_f7,local_f8) != 0)) {
    eastl__basic_string();
  }
  lVar51 = param_2->position;
  if (param_3 <= (int)lVar51) goto code_r0x001a4a3a;
  goto LAB_001a4710;
code_r0x001a4a3a:
  lVar60 = *param_1;
  goto LAB_001a4a42;
}


```

## `jag::packethandlers::Clans::CLANSETTINGS_FULL` @ 001a86c0
```c

undefined * jag::packethandlers::Clans::CLANSETTINGS_FULL(long *param_1,long param_2,int *param_3)

{
  undefined8 *puVar1;
  char cVar2;
  int iVar3;
  long lVar4;
  long lVar5;
  long lVar6;
  long lVar7;
  code *pcVar8;
  undefined8 *puVar9;
  long *plVar10;
  
  iVar3 = *param_3;
  lVar4 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  lVar6 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *param_1);
  *(undefined4 *)(lVar6 + 0x120) = *(undefined4 *)(*(long *)(lVar6 + 0x118) + 0x10);
  *(long *)(param_2 + 0x18) = lVar4 + 1;
  cVar2 = *(char *)(lVar5 + lVar4);
  if (iVar3 == 1) {
    if (cVar2 < '\0') {
      puVar9 = *(undefined8 **)(lVar7 + 0x40);
      *(undefined8 *)(lVar7 + 0x48) = 0;
      *(undefined8 *)(lVar7 + 0x40) = 0;
    }
    else {
      if ('\x01' < cVar2) goto LAB_001a8846;
      puVar1 = (undefined8 *)(lVar7 + (long)cVar2 * 0x10);
      puVar9 = (undefined8 *)*puVar1;
      puVar1[1] = 0;
      *puVar1 = 0;
    }
    if (puVar9 == (undefined8 *)0x0) goto LAB_001a8846;
  }
  else if (cVar2 < '\0') {
    puVar9 = (undefined8 *)FUN_00c29430(0xf0);
    if (puVar9 == (undefined8 *)0x0) goto LAB_001a89b0;
    puVar9[5] = 0;
    *(undefined1 *)(puVar9 + 6) = 0;
    puVar1 = puVar9 + 4;
    puVar9[4] = &PTR_FUN_01363ef0;
    *(undefined8 *)((long)puVar9 + 0x47) = 0x17;
    *(undefined1 *)((long)puVar9 + 0x4f) = 0;
    puVar9[0xd] = 0;
    puVar9[0xe] = 0;
    puVar9[0xf] = 0;
    puVar9[0x10] = 0;
    puVar9[0x11] = 0;
    puVar9[0x12] = 0;
    puVar9[0x13] = 0xffffffffffffffff;
    puVar9[0x14] = 0;
    puVar9[0x15] = 0;
    puVar9[0x16] = 0;
    puVar9[0x19] = 1;
    puVar9[0x1a] = 0;
    puVar9[0x1b] = 0x400000003f800000;
    *(undefined4 *)(puVar9 + 0x1c) = 0;
    *(undefined1 *)(puVar9 + 0x1d) = 0xff;
    puVar9[0x18] = &DAT_01393d40;
    Inventory::UPDATE_INV_GROUP(puVar1,param_2);
    *puVar9 = &PTR_FUN_013638e0;
    puVar9[1] = 0x100000001;
    puVar9[2] = 1;
    puVar9[3] = puVar1;
    if (*(char *)(puVar9 + 0x1d) == -1) {
      LOCK();
      *(int *)(puVar9 + 1) = *(int *)(puVar9 + 1) + 1;
      UNLOCK();
      lVar4 = *(long *)(lVar7 + 0x40);
      *(undefined8 **)(lVar7 + 0x48) = puVar1;
      *(undefined8 **)(lVar7 + 0x40) = puVar9;
      goto joined_r0x001a8996;
    }
  }
  else {
    puVar9 = (undefined8 *)FUN_00c29430(0xf0);
    if (puVar9 == (undefined8 *)0x0) {
LAB_001a89b0:
                    /* WARNING: Does not return */
      pcVar8 = (code *)invalidInstructionException();
      (*pcVar8)();
    }
    puVar1 = puVar9 + 4;
    *(char *)(puVar9 + 0x1d) = cVar2;
    puVar9[5] = 0;
    puVar9[4] = &PTR_FUN_01363ef0;
    puVar9[0x1b] = 0x400000003f800000;
    *(undefined1 *)(puVar9 + 6) = 0;
    *(undefined8 *)((long)puVar9 + 0x47) = 0x17;
    *(undefined1 *)((long)puVar9 + 0x4f) = 0;
    puVar9[0xd] = 0;
    puVar9[0xe] = 0;
    puVar9[0xf] = 0;
    puVar9[0x10] = 0;
    puVar9[0x11] = 0;
    puVar9[0x12] = 0;
    puVar9[0x13] = 0xffffffffffffffff;
    puVar9[0x14] = 0;
    puVar9[0x15] = 0;
    puVar9[0x16] = 0;
    puVar9[0x19] = 1;
    puVar9[0x1a] = 0;
    *(undefined4 *)(puVar9 + 0x1c) = 0;
    puVar9[0x18] = &DAT_01393d40;
    Inventory::UPDATE_INV_GROUP(puVar1,param_2);
    puVar9[1] = 0x100000001;
    puVar9[2] = 1;
    puVar9[3] = puVar1;
    *puVar9 = &PTR_FUN_013638e0;
    if (*(byte *)(puVar9 + 0x1d) < 2) {
      plVar10 = (long *)((long)(char)*(byte *)(puVar9 + 0x1d) * 0x10 + lVar7);
      LOCK();
      *(int *)(puVar9 + 1) = *(int *)(puVar9 + 1) + 1;
      UNLOCK();
      lVar4 = *plVar10;
      plVar10[1] = (long)puVar1;
      *plVar10 = (long)puVar9;
joined_r0x001a8996:
      if (lVar4 != 0) {
        ref_counter_base::DecRef();
      }
    }
  }
  ref_counter_base::DecRef(puVar9);
LAB_001a8846:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Clans::CLANSETTINGS_DELTA` @ 001aef20
```c

/* Setting prototype: undefined * CLANSETTINGS_DELTA(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Clans::CLANSETTINGS_DELTA(long *thisPtr,long packetPtr)

{
  char cVar1;
  long lVar2;
  long *plVar3;
  char cVar4;
  long lVar5;
  byte bVar6;
  code *pcVar7;
  long lVar8;
  undefined1 (*local_60) [16];
  long local_58;
  undefined8 *local_50;
  undefined8 *local_48;
  undefined8 *local_40;
  
                    /* CLANSETTINGS_DELTA - reads clan ulong + byte-discriminated deltas (switch
                       1/3/4/5 -> AddMemberV1/SmallDelta/StringDelta/AddMemberV2 Decode). Applies
                       the delta list to the matching clan settings object and bumps its version
                       counter at +0x30. */
  lVar5 = *(long *)(packetPtr + 0x18);
  lVar8 = *(long *)(packetPtr + 0x10);
  lVar2 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *thisPtr);
  *(undefined4 *)(lVar2 + 0x130) = *(undefined4 *)(*(long *)(lVar2 + 0x128) + 0x10);
  *(long *)(packetPtr + 0x18) = lVar5 + 1;
  cVar1 = *(char *)(lVar8 + lVar5);
  local_58 = 0;
  local_50 = (undefined8 *)0x0;
  local_48 = (undefined8 *)0x0;
  local_40 = (undefined8 *)0x0;
  Packet::gT_ulong(packetPtr);
  local_58 = Packet::gT_ulong(packetPtr);
  lVar5 = *(long *)(packetPtr + 0x18);
  local_48 = local_50;
  *(long *)(packetPtr + 0x18) = lVar5 + 1;
  bVar6 = *(byte *)(*(long *)(packetPtr + 0x10) + lVar5);
  if (bVar6 != 0) {
    do {
      local_60 = (undefined1 (*) [16])0x0;
      if (bVar6 == 3) {
        local_60 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined8 *)(*local_60 + 8) = 0;
        *(undefined ***)*local_60 = &PTR_Decode_01363af0;
        pcVar7 = ClanSettingsDelta::SmallDelta::Decode;
      }
      else if (bVar6 < 4) {
        if (bVar6 != 1) break;
        local_60 = (undefined1 (*) [16])operator_new(0x30);
        *local_60 = (undefined1  [16])0x0;
        *(undefined ***)*local_60 = &PTR_Decode_01364668;
        (*local_60)[8] = 0;
        local_60[1] = (undefined1  [16])0x0;
        local_60[2] = (undefined1  [16])0x0;
        local_60[1][0xf] = 0x17;
        pcVar7 = ClanSettingsDelta::AddMemberV1::Decode;
      }
      else if (bVar6 == 4) {
        local_60 = (undefined1 (*) [16])operator_new(0x30);
        *local_60 = (undefined1  [16])0x0;
        *(undefined ***)*local_60 = &PTR_Decode_01364608;
        (*local_60)[8] = 0;
        local_60[1] = (undefined1  [16])0x0;
        local_60[2] = (undefined1  [16])0x0;
        local_60[1][0xf] = 0x17;
        pcVar7 = ClanSettingsDelta::StringDelta::Decode;
      }
      else {
        if (bVar6 != 5) break;
        local_60 = (undefined1 (*) [16])operator_new(0x40);
        local_60[1] = (undefined1  [16])0x0;
        local_60[2] = (undefined1  [16])0x0;
        *local_60 = (undefined1  [16])0x0;
        local_60[3] = (undefined1  [16])0x0;
        *(undefined ***)*local_60 = &PTR_Decode_01364638;
        local_60[3][7] = 0x17;
        pcVar7 = ClanSettingsDelta::AddMemberV2::Decode;
      }
      cVar4 = (*pcVar7)(local_60,packetPtr);
      if (cVar4 == '\0') {
        if (local_60 != (undefined1 (*) [16])0x0) {
          (**(code **)(*(long *)*local_60 + 0x18))();
        }
      }
      else if (local_48 < local_40) {
        *local_48 = local_60;
        local_48 = local_48 + 1;
      }
      else {
        FUN_00679b80(&local_50,&local_60);
      }
      lVar5 = *(long *)(packetPtr + 0x18);
      *(long *)(packetPtr + 0x18) = lVar5 + 1;
      bVar6 = *(byte *)(*(long *)(packetPtr + 0x10) + lVar5);
    } while (bVar6 != 0);
  }
  lVar5 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *thisPtr);
  if (cVar1 < '\0') {
    lVar5 = *(long *)(lVar5 + 0x58);
    if ((lVar5 == 0) || (lVar8 = *(long *)(lVar5 + 0x30), lVar8 != local_58)) goto LAB_001af0c0;
    if (local_48 != local_50) {
      do {
        plVar3 = (long *)local_48[-1];
        local_48 = local_48 + -1;
        (**(code **)(*plVar3 + 8))(plVar3,lVar5);
        (**(code **)(*plVar3 + 0x18))(plVar3);
      } while (local_50 != local_48);
LAB_001af262:
      lVar8 = *(long *)(lVar5 + 0x30);
    }
  }
  else {
    lVar5 = FUN_0019be40(lVar5,cVar1);
    if (*(long *)(lVar5 + 8) == 0) goto LAB_001af0c0;
    lVar5 = FUN_0019be40(*(undefined8 *)((long)&__DT_RELA[0xcf4].r_addend + *thisPtr),cVar1);
    lVar5 = *(long *)(lVar5 + 8);
    lVar8 = *(long *)(lVar5 + 0x30);
    if (lVar8 != local_58) goto LAB_001af0c0;
    if (local_48 != local_50) {
      do {
        plVar3 = (long *)local_48[-1];
        local_48 = local_48 + -1;
        (**(code **)(*plVar3 + 8))(plVar3,lVar5);
        (**(code **)(*plVar3 + 0x18))(plVar3);
      } while (local_50 != local_48);
      goto LAB_001af262;
    }
  }
  *(long *)(lVar5 + 0x30) = lVar8 + 1;
LAB_001af0c0:
  if (local_50 != (undefined8 *)0x0) {
    HeapInterface::Free();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Chat::CLANSETTINGS_DELTA_CHAT` @ 001af2e0
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: undefined * CLANSETTINGS_DELTA_CHAT(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Chat::CLANSETTINGS_DELTA_CHAT(long *thisPtr,long packetPtr)

{
  byte bVar1;
  long lVar2;
  long lVar3;
  char cVar4;
  int iVar5;
  code *pcVar6;
  long *plVar7;
  undefined *puVar8;
  long *plVar9;
  long lVar10;
  long *plVar11;
  long *plVar12;
  uint uVar13;
  bool bVar14;
  long *local_60;
  uint local_58;
  long *local_50;
  long *local_48;
  long *local_40;
  
                    /* CLANSETTINGS_DELTA_CHAT clan chat delta packet handler. Reads clan ulong +
                       u32 version + byte-discriminated delta loop (switch 1..14), applies the delta
                       list to the matching clan-chat settings object and bumps its version counter
                       at +0x8. */
  lVar2 = *(long *)(packetPtr + 0x18);
  lVar10 = *(long *)(packetPtr + 0x10);
  lVar3 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *thisPtr);
  *(undefined4 *)(lVar3 + 0x120) = *(undefined4 *)(*(long *)(lVar3 + 0x118) + 0x10);
  *(long *)(packetPtr + 0x18) = lVar2 + 1;
  bVar1 = *(byte *)(lVar10 + lVar2);
  local_58 = 0;
  local_50 = (long *)0x0;
  local_48 = (long *)0x0;
  local_40 = (long *)0x0;
  Packet::gT_ulong(packetPtr);
  lVar2 = *(long *)(packetPtr + 0x18);
  bVar14 = DAT_01050dc0 == 0x3020100;
  *(long *)(packetPtr + 0x18) = lVar2 + 4;
  local_58 = *(uint *)(*(long *)(packetPtr + 0x10) + lVar2);
  *(long *)(packetPtr + 0x18) = lVar2 + 5;
  if (bVar14) {
    local_58 = local_58 >> 0x18 | (local_58 & 0xff0000) >> 8 | (local_58 & 0xff00) << 8 |
               local_58 << 0x18;
  }
  cVar4 = *(char *)(*(long *)(packetPtr + 0x10) + 4 + lVar2);
  if (cVar4 != '\0') {
    do {
      local_60 = (long *)0x0;
                    /* WARNING: Could not find normalized switch variable to match jumptable */
      switch(cVar4) {
      case '\x01':
        local_60 = (long *)operator_new(0x20);
        *(undefined1 *)(local_60 + 1) = 0;
        *(undefined1 *)((long)local_60 + 0x1f) = 0x17;
        *local_60 = (long)&PTR_FUN_013645d8;
        pcVar6 = FUN_00172720;
        break;
      case '\x02':
        local_60 = (long *)operator_new(0x10);
        *(undefined4 *)(local_60 + 1) = 0;
        *(undefined4 *)((long)local_60 + 0xc) = 0;
        *local_60 = (long)&PTR_FUN_01363a00;
        pcVar6 = FUN_00171df0;
        break;
      case '\x03':
        local_60 = (long *)operator_new(0x20);
        *(undefined1 *)(local_60 + 1) = 0;
        *(undefined1 *)((long)local_60 + 0x1f) = 0x17;
        *local_60 = (long)&PTR_FUN_01364578;
        pcVar6 = FUN_00172680;
        break;
      case '\x04':
        local_60 = (long *)operator_new(0x20);
        *(undefined1 *)(local_60 + 1) = 0;
        *(undefined4 *)((long)local_60 + 0xc) = 0;
        *(undefined4 *)(local_60 + 2) = 0;
        *(undefined4 *)((long)local_60 + 0x14) = 0;
        *(undefined1 *)(local_60 + 3) = 0;
        *local_60 = (long)&PTR_FUN_01363a90;
        pcVar6 = FUN_00171bc0;
        break;
      case '\x05':
        local_60 = (long *)operator_new(0x10);
        *(undefined4 *)(local_60 + 1) = 0;
        *local_60 = (long)&PTR_FUN_01363a30;
        pcVar6 = FUN_00171e40;
        break;
      case '\x06':
        local_60 = (long *)operator_new(0x10);
        *(undefined4 *)(local_60 + 1) = 0;
        *local_60 = (long)&PTR_FUN_01363a60;
        pcVar6 = FUN_00171c30;
        break;
      case '\a':
        local_60 = (long *)operator_new(0x18);
        *local_60 = (long)&PTR_FUN_013639d0;
        *(undefined1 (*) [16])(local_60 + 1) = (undefined1  [16])0x0;
        pcVar6 = FUN_00171d70;
        break;
      case '\b':
        local_60 = (long *)operator_new(0x10);
        local_60[1] = 0;
        *local_60 = (long)&PTR_FUN_01363970;
        pcVar6 = FUN_00171b60;
        break;
      case '\t':
        local_60 = (long *)operator_new(0x18);
        *(undefined4 *)(local_60 + 1) = 0;
        local_60[2] = 0;
        *local_60 = (long)&PTR_FUN_01363940;
        pcVar6 = FUN_0017a420;
        break;
      case '\n':
        local_60 = (long *)operator_new(0x28);
        *(undefined4 *)(local_60 + 1) = 0;
        *(undefined1 *)(local_60 + 2) = 0;
        *(undefined1 *)((long)local_60 + 0x27) = 0x17;
        *local_60 = (long)&PTR_FUN_01364518;
        pcVar6 = FUN_00172620;
        break;
      case '\v':
        local_60 = (long *)operator_new(0x18);
        *local_60 = (long)&PTR_FUN_01363910;
        *(undefined1 (*) [16])(local_60 + 1) = (undefined1  [16])0x0;
        pcVar6 = FUN_00171b00;
        break;
      case '\f':
        local_60 = (long *)operator_new(0x28);
        *(undefined4 *)(local_60 + 4) = 0;
        *local_60 = (long)&PTR_FUN_01364548;
        *(undefined1 *)(local_60 + 1) = 0;
        *(undefined1 *)((long)local_60 + 0x1f) = 0x17;
        pcVar6 = FUN_00172650;
        break;
      case '\r':
        local_60 = (long *)operator_new(0x28);
        *(undefined4 *)(local_60 + 1) = 0;
        *(undefined1 *)(local_60 + 2) = 0;
        *(undefined1 *)((long)local_60 + 0x27) = 0x17;
        *local_60 = (long)&PTR_FUN_013645a8;
        cVar4 = FUN_001726c0(local_60,packetPtr);
        goto joined_r0x001af669;
      case '\x0e':
        local_60 = (long *)operator_new(0x10);
        *(undefined4 *)(local_60 + 1) = 0;
        *(undefined1 *)((long)local_60 + 0xc) = 0;
        *local_60 = (long)&PTR_FUN_013639a0;
        pcVar6 = FUN_00171c90;
        break;
      default:
        goto switchD_002af3ca_default;
      }
      cVar4 = (*pcVar6)(local_60,packetPtr);
joined_r0x001af669:
      if (cVar4 == '\0') {
        if (local_60 != (long *)0x0) {
          (**(code **)(*local_60 + 0x18))();
        }
      }
      else if (local_48 < local_40) {
        *local_48 = (long)local_60;
        local_48 = local_48 + 1;
      }
      else {
        FUN_00679b80(&local_50,&local_60);
      }
      lVar2 = *(long *)(packetPtr + 0x18);
      *(long *)(packetPtr + 0x18) = lVar2 + 1;
      cVar4 = *(char *)(*(long *)(packetPtr + 0x10) + lVar2);
    } while (cVar4 != '\0');
  }
switchD_002af3ca_default:
  plVar7 = local_48;
  lVar2 = *(long *)((long)&__DT_RELA[0xcf4].r_addend + *thisPtr);
  if ((char)bVar1 < '\0') {
    lVar2 = *(long *)(lVar2 + 0x48);
    lVar10 = *(long *)(lVar2 + 8);
    if (lVar10 == (int)local_58) {
      if (local_50 != local_48) {
        uVar13 = (int)((ulong)((long)local_48 + (-8 - (long)local_50)) >> 3) + 1U & 7;
        plVar9 = local_50;
        if (uVar13 == 0) goto LAB_001afa4b;
        if (uVar13 != 1) {
          plVar12 = local_50;
          if (uVar13 != 2) {
            if (uVar13 != 3) {
              if (uVar13 != 4) {
                if (uVar13 != 5) {
                  if (uVar13 != 6) {
                    plVar12 = local_50 + 1;
                    (**(code **)(*(long *)*local_50 + 8))((long *)*local_50,lVar2);
                  }
                  plVar9 = plVar12 + 1;
                  (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
                }
                plVar12 = plVar9 + 1;
                (**(code **)(*(long *)*plVar9 + 8))((long *)*plVar9,lVar2);
              }
              plVar9 = plVar12 + 1;
              (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
            }
            plVar12 = plVar9 + 1;
            (**(code **)(*(long *)*plVar9 + 8))((long *)*plVar9,lVar2);
          }
          plVar9 = plVar12 + 1;
          (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
        }
        (**(code **)(*(long *)*plVar9 + 8))((long *)*plVar9,lVar2);
        for (plVar9 = plVar9 + 1; plVar7 != plVar9; plVar9 = plVar9 + 8) {
LAB_001afa4b:
          (**(code **)(*(long *)*plVar9 + 8))((long *)*plVar9,lVar2);
          (**(code **)(*(long *)plVar9[1] + 8))((long *)plVar9[1],lVar2);
          (**(code **)(*(long *)plVar9[2] + 8))((long *)plVar9[2],lVar2);
          (**(code **)(*(long *)plVar9[3] + 8))((long *)plVar9[3],lVar2);
          (**(code **)(*(long *)plVar9[4] + 8))((long *)plVar9[4],lVar2);
          (**(code **)(*(long *)plVar9[5] + 8))((long *)plVar9[5],lVar2);
          (**(code **)(*(long *)plVar9[6] + 8))((long *)plVar9[6],lVar2);
          (**(code **)(*(long *)plVar9[7] + 8))((long *)plVar9[7],lVar2);
        }
        lVar10 = *(long *)(lVar2 + 8);
      }
      *(long *)(lVar2 + 8) = lVar10 + 1;
    }
  }
  else {
    if (bVar1 < 2) {
      puVar8 = (undefined *)((ulong)bVar1 * 0x10 + lVar2);
    }
    else {
      if ((DAT_013963d0 == '\0') && (iVar5 = __cxa_guard_acquire(&DAT_013963d0), iVar5 != 0)) {
        _DAT_013963c0 = (undefined1  [16])0x0;
        __cxa_guard_release(&DAT_013963d0);
        __cxa_atexit(FUN_009e8110,&DAT_013963c0,&PTR_LOOP_01391000);
      }
      puVar8 = &DAT_013963c0;
    }
    plVar7 = local_48;
    lVar2 = *(long *)(puVar8 + 8);
    lVar10 = *(long *)(lVar2 + 8);
    if (lVar10 == (int)local_58) {
      if (local_50 != local_48) {
        plVar9 = local_50 + 1;
        uVar13 = (int)((ulong)((long)local_48 + (-8 - (long)local_50)) >> 3) + 1U & 7;
        plVar12 = local_50;
        if (uVar13 == 0) goto LAB_001afbb1;
        if (uVar13 != 1) {
          plVar11 = local_50;
          if (uVar13 != 2) {
            if (uVar13 != 3) {
              if (uVar13 != 4) {
                if (uVar13 != 5) {
                  if (uVar13 != 6) {
                    (**(code **)(*(long *)*local_50 + 8))((long *)*local_50,lVar2);
                    plVar11 = plVar9;
                  }
                  plVar12 = plVar11 + 1;
                  (**(code **)(*(long *)*plVar11 + 8))((long *)*plVar11,lVar2);
                }
                plVar11 = plVar12 + 1;
                (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
              }
              plVar12 = plVar11 + 1;
              (**(code **)(*(long *)*plVar11 + 8))((long *)*plVar11,lVar2);
            }
            plVar11 = plVar12 + 1;
            (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
          }
          plVar12 = plVar11 + 1;
          (**(code **)(*(long *)*plVar11 + 8))((long *)*plVar11,lVar2);
        }
        (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
        for (plVar12 = plVar12 + 1; plVar7 != plVar12; plVar12 = plVar12 + 8) {
LAB_001afbb1:
          (**(code **)(*(long *)*plVar12 + 8))((long *)*plVar12,lVar2);
          (**(code **)(*(long *)plVar12[1] + 8))((long *)plVar12[1],lVar2);
          (**(code **)(*(long *)plVar12[2] + 8))((long *)plVar12[2],lVar2);
          (**(code **)(*(long *)plVar12[3] + 8))((long *)plVar12[3],lVar2);
          (**(code **)(*(long *)plVar12[4] + 8))((long *)plVar12[4],lVar2);
          (**(code **)(*(long *)plVar12[5] + 8))((long *)plVar12[5],lVar2);
          (**(code **)(*(long *)plVar12[6] + 8))((long *)plVar12[6],lVar2);
          (**(code **)(*(long *)plVar12[7] + 8))((long *)plVar12[7],lVar2);
        }
        lVar10 = *(long *)(lVar2 + 8);
      }
      *(long *)(lVar2 + 8) = lVar10 + 1;
    }
  }
  plVar12 = local_48;
  plVar7 = local_50;
  plVar9 = local_48;
  if (local_50 != local_48) {
    uVar13 = (int)((ulong)((long)local_48 + (-8 - (long)local_50)) >> 3) + 1U & 7;
    if (uVar13 == 0) goto LAB_001af570;
    if (uVar13 != 1) {
      if (uVar13 != 2) {
        if (uVar13 != 3) {
          if (uVar13 != 4) {
            if (uVar13 != 5) {
              if (uVar13 != 6) {
                if ((long *)*local_50 != (long *)0x0) {
                  (**(code **)(*(long *)*local_50 + 0x18))();
                }
                plVar7 = plVar7 + 1;
              }
              if ((long *)*plVar7 != (long *)0x0) {
                (**(code **)(*(long *)*plVar7 + 0x18))();
              }
              plVar7 = plVar7 + 1;
            }
            if ((long *)*plVar7 != (long *)0x0) {
              (**(code **)(*(long *)*plVar7 + 0x18))();
            }
            plVar7 = plVar7 + 1;
          }
          if ((long *)*plVar7 != (long *)0x0) {
            (**(code **)(*(long *)*plVar7 + 0x18))();
          }
          plVar7 = plVar7 + 1;
        }
        if ((long *)*plVar7 != (long *)0x0) {
          (**(code **)(*(long *)*plVar7 + 0x18))();
        }
        plVar7 = plVar7 + 1;
      }
      if ((long *)*plVar7 != (long *)0x0) {
        (**(code **)(*(long *)*plVar7 + 0x18))();
      }
      plVar7 = plVar7 + 1;
    }
    if ((long *)*plVar7 != (long *)0x0) {
      (**(code **)(*(long *)*plVar7 + 0x18))();
    }
    plVar9 = local_50;
    for (plVar7 = plVar7 + 1; local_50 = plVar9, plVar7 != plVar12; plVar7 = plVar7 + 8) {
LAB_001af570:
      if ((long *)*plVar7 != (long *)0x0) {
        (**(code **)(*(long *)*plVar7 + 0x18))();
      }
      if ((long *)plVar7[1] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[1] + 0x18))();
      }
      if ((long *)plVar7[2] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[2] + 0x18))();
      }
      if ((long *)plVar7[3] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[3] + 0x18))();
      }
      if ((long *)plVar7[4] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[4] + 0x18))();
      }
      if ((long *)plVar7[5] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[5] + 0x18))();
      }
      if ((long *)plVar7[6] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[6] + 0x18))();
      }
      if ((long *)plVar7[7] != (long *)0x0) {
        (**(code **)(*(long *)plVar7[7] + 0x18))();
      }
      plVar9 = local_50;
    }
  }
  if (plVar9 != (long *)0x0) {
    HeapInterface::Free(plVar9);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Camera::CAM_TARGET` @ 001b2d40
```c

undefined * jag::packethandlers::Camera::CAM_TARGET(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  int iVar3;
  long lVar4;
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf6].r_addend + *param_1);
  lVar4 = *(long *)((long)&__DT_RELA[0xcfa].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar1 + 1;
  *(uint *)(lVar2 + 0x50) = (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar1);
  if (1 < *(int *)((long)&__DT_RELA[0xd3e].r_offset + *(long *)(lVar4 + 0x18)) - 7U) {
    iVar3 = FUN_00490150();
    if ((iVar3 != 1) && (*(char *)(lVar4 + 0x168) == '\0')) {
      FUN_00127570(lVar4);
      lVar4 = *(long *)((long)&__DT_RELA[0xcfa].r_info + *param_1);
    }
  }
  LoginManager::ResetLoginState(lVar4,0);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::NPCInfo::SET_NPC_UPDATE_ORIGIN` @ 001b2de0
```c

undefined * jag::packethandlers::NPCInfo::SET_NPC_UPDATE_ORIGIN(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  uint uVar4;
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf6].r_addend + *param_1);
  lVar3 = *(long *)((long)&__DT_RELA[0xcfa].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar1 + 1;
  uVar4 = (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar1);
  *(uint *)(lVar2 + 0x50) = uVar4;
  if (uVar4 != 0x14) {
    LoginManager::ResetLoginState(lVar3,*(undefined1 *)(lVar3 + 0x180));
    return &DAT_015d3620;
  }
  LoginManager::ResetLoginState(lVar3,0);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED` @ 001d2b80
```c

/* WARNING: Type propagation algorithm not settling */

undefined *
jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED(long *param_1,PacketCore *param_2)

{
  long lVar1;
  ushort uVar2;
  uint uVar3;
  ulong uVar4;
  undefined8 *puVar5;
  long lVar6;
  undefined8 *puVar7;
  undefined8 *puVar8;
  byte bVar9;
  undefined4 uVar10;
  uint local_214;
  uint local_210;
  uint local_20c;
  uint local_208;
  undefined4 uStack_204;
  undefined1 local_200;
  undefined4 local_1fc;
  undefined1 local_1f8;
  undefined4 local_1f4;
  undefined1 local_1f0;
  undefined4 local_1ec;
  undefined1 local_1e8;
  undefined8 local_1e4;
  uint local_1dc;
  undefined1 local_1d8;
  undefined4 local_1d4;
  undefined1 local_1d0;
  undefined4 local_1cc;
  undefined1 local_1c8;
  undefined4 local_1c4;
  undefined1 local_1c0;
  undefined1 local_1bc;
  undefined4 local_1b8;
  undefined1 local_1b4;
  uint local_1b0;
  undefined1 local_1ac;
  undefined4 local_1a8;
  undefined1 local_1a4;
  undefined4 local_1a0;
  undefined1 local_19c;
  undefined4 local_198;
  undefined1 local_194;
  undefined4 local_190;
  undefined1 local_18c;
  undefined1 local_188;
  undefined4 local_184;
  undefined1 local_180;
  undefined4 local_17c;
  undefined1 local_178;
  undefined4 local_174;
  undefined1 local_170;
  undefined1 local_16c;
  uint local_168;
  undefined1 local_164;
  uint local_160;
  undefined1 local_15c;
  undefined1 local_158;
  undefined8 local_154;
  undefined1 local_14c;
  undefined8 local_148;
  undefined1 local_140;
  undefined8 local_13c;
  undefined1 local_134;
  undefined1 local_130;
  undefined1 local_12f;
  undefined1 local_12e;
  uint local_12c;
  undefined1 local_128;
  undefined4 local_124;
  undefined1 local_120;
  undefined4 local_11c;
  undefined1 local_118;
  undefined4 local_114;
  undefined1 local_110;
  undefined4 local_10c;
  undefined1 local_108;
  undefined4 local_104;
  undefined1 local_100;
  undefined1 local_fc;
  undefined1 local_fb;
  undefined1 local_fa;
  undefined4 local_f8;
  undefined1 local_f4;
  undefined4 local_f0;
  undefined1 local_ec;
  undefined4 local_e8;
  undefined1 local_e4;
  undefined4 local_e0;
  undefined1 local_dc;
  undefined8 local_d8;
  uint local_d0;
  undefined1 local_cc;
  undefined8 local_c8;
  uint local_c0;
  undefined1 local_bc;
  undefined8 local_b8;
  uint local_b0;
  undefined1 local_ac;
  undefined1 local_a8;
  undefined4 local_a4;
  undefined1 local_a0;
  undefined4 local_9c;
  undefined1 local_98;
  undefined4 local_94;
  undefined1 local_90;
  undefined4 local_8c;
  undefined1 local_88;
  undefined4 local_84;
  undefined1 local_80;
  undefined4 local_7c;
  undefined1 local_78;
  undefined4 local_74;
  undefined1 local_70;
  undefined4 local_6c;
  undefined1 local_68;
  undefined4 local_64;
  undefined1 local_60;
  undefined8 local_5c;
  float local_54;
  undefined1 local_50;
  undefined8 local_4c;
  float local_44;
  undefined1 local_40;
  undefined1 local_3c;
  undefined4 local_38;
  undefined1 local_34;
  uint local_30;
  uint uStack_2c;
  
                    /* ServerProt op130 (0x82), VarByte. IDENTITY UNCONFIRMED — do NOT trust the
                       "UPDATE_IGNORELIST" name in any prior label/CSV.
                       
                       This is NOT the rev-947 UPDATE_IGNORELIST (a loop of {flag-byte + 3 CP1252
                       strings} ignore records). Decompile shows a SINGLE relationship/friend entry
                       built from a 64-bit flag mask:
                         - gT_ulong flagMask
                         - if mask==0: clears all fields (a remove/empty-entry form)
                         - else ~48 conditionally-gated fields keyed by individual mask bits 0..0x2f
                       (smart readers, coord triples, packed-uint->3 floats color/vector, trailing
                       g2)
                         - allocates a 0x1e0-byte entry, stores to *(RelationshipManager+0x98),
                       frees old, sets dirty flag +0xa0 = 1.
                       
                       Per-friend RELATIONSHIP DELTA packet (single entry, flag-gated), redesigned
                       in 948. CRITICAL: needs a live lobby capture with a friended/ignored account
                       to nail the flag-bit -> field mapping. LEAVE AS UNKNOWN until then. */
  bVar9 = 0;
  local_208 = local_208 & 0xffffff00;
  local_200 = 0;
  local_1f8 = 0;
  local_1f0 = 0;
  local_1e8 = 0;
  local_1e4 = 0;
  local_1dc = 0;
  local_1d8 = 0;
  local_1d0 = 0;
  local_1c8 = 0;
  local_1c0 = 0;
  local_1bc = 0;
  local_1b4 = 0;
  local_1ac = 0;
  local_1a4 = 0;
  local_19c = 0;
  local_194 = 0;
  local_18c = 0;
  local_188 = 0;
  local_180 = 0;
  local_178 = 0;
  local_170 = 0;
  local_16c = 0;
  local_164 = 0;
  local_15c = 0;
  local_158 = 0;
  local_14c = 0;
  local_140 = 0;
  local_134 = 0;
  local_130 = 0;
  local_12e = 0;
  local_128 = 0;
  local_120 = 0;
  local_118 = 0;
  local_110 = 0;
  local_108 = 0;
  local_100 = 0;
  local_fc = 0;
  local_fa = 0;
  local_f4 = 0;
  local_ec = 0;
  local_e4 = 0;
  local_dc = 0;
  local_d8 = 0;
  local_d0 = 0;
  local_cc = 0;
  local_c8 = 0;
  local_c0 = 0;
  local_bc = 0;
  local_b8 = 0;
  local_b0 = 0;
  local_ac = 0;
  local_a8 = 0;
  local_a0 = 0;
  local_98 = 0;
  local_90 = 0;
  local_88 = 0;
  local_80 = 0;
  local_78 = 0;
  local_70 = 0;
  local_68 = 0;
  local_60 = 0;
  local_5c = 0;
  local_54 = 0.0;
  local_50 = 0;
  local_4c = 0;
  local_44 = 0.0;
  local_40 = 0;
  local_3c = 0;
  local_34 = 0;
  local_30 = 0;
  uStack_2c = uStack_2c & 0xffffff00;
  uVar4 = Packet::gT_ulong(param_2);
  if (uVar4 == 0) {
    uVar2 = FUN_00121a30(param_2);
    uStack_204 = 0;
    local_1fc = 0;
    local_1f4 = 0;
    local_1ec = 0;
    local_1e4 = 0;
    local_1dc = 0;
    local_1d4 = 0;
    local_1cc = 0;
    local_1c4 = 0;
    local_1b8 = 0;
    local_1b0 = 0;
    local_1a8 = 0;
    local_1a0 = 0;
    local_198 = 0;
    local_190 = 0;
    local_184 = 0;
    local_17c = 0;
    local_174 = 0;
    local_168 = 0;
    local_160 = 0;
    local_154 = 0;
    local_148 = 0;
    local_13c = 0;
    local_12f = 0;
    local_12c = 0;
    local_124 = 0;
    local_11c = 0;
    local_114 = 0;
    local_10c = 0;
    local_104 = 0;
    local_fb = 0;
    local_f8 = 0;
    local_f0 = 0;
    local_e8 = 0;
    local_e0 = 0;
    local_d8 = 0;
    local_d0 = 0;
    local_c8 = 0;
    local_c0 = 0;
    local_b8 = 0;
    local_b0 = 0;
    local_a4 = 0;
    local_9c = 0;
    local_94 = 0;
    local_8c = 0;
    local_84 = 0;
    local_7c = 0;
    local_74 = 0;
    local_6c = 0;
    local_64 = 0;
    local_5c = 0;
    local_54 = 0.0;
    local_4c = 0;
    local_44 = 0.0;
    local_38 = 0;
  }
  else {
    uStack_2c = CONCAT31(uStack_2c._1_3_,1);
    if ((uVar4 & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_200 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
      uStack_204 = (undefined4)CONCAT71((uint7)(uint3)uVar3,0xff);
    }
    if ((uVar4 & 2) != 0) {
      local_1fc = FUN_001351c0(param_2);
      local_1f8 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 & 4) != 0) {
      local_1f4 = FUN_001351c0(param_2);
      local_1f0 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 & 8) != 0) {
      local_1ec = FUN_001351c0(param_2);
      local_1e8 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 & 0x10) != 0) {
      FUN_00546250(&local_214,param_2,0);
      local_1d8 = 1;
      local_1dc = local_20c ^ DAT_00cb6c50;
      local_208 = CONCAT31(local_208._1_3_,1);
      local_1e4 = CONCAT44(local_210 ^ DAT_00cb6c50,local_214 ^ DAT_00cb6c50);
    }
    if ((uVar4 & 0x20) != 0) {
      local_1d4 = FUN_001351c0(param_2);
      local_1d0 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 >> 0x2d & 1) != 0) {
      local_1cc = FUN_001351c0(param_2);
      local_1c8 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 >> 0x2e & 1) != 0) {
      local_1c4 = FUN_001351c0(param_2);
      local_1c0 = 1;
      local_208 = CONCAT31(local_208._1_3_,1);
    }
    if ((uVar4 & 0x40) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_1b4 = 1;
      local_1bc = 1;
      local_1b8 = (undefined4)CONCAT71((uint7)(uint3)uVar3,0xff);
    }
    if ((uVar4 & 0x80) != 0) {
      uVar2 = FUN_00121a30(param_2);
      local_1ac = 1;
      local_1b0 = (uint)uVar2;
      local_1bc = 1;
    }
    if ((uVar4 & 0x100) != 0) {
      local_1a8 = FUN_001351c0(param_2);
      local_1a4 = 1;
      local_1bc = 1;
    }
    if ((uVar4 & 0x200) != 0) {
      local_1a0 = FUN_001351c0(param_2);
      local_19c = 1;
      local_1bc = 1;
    }
    if ((uVar4 & 0x400) != 0) {
      local_198 = FUN_001351c0(param_2);
      local_194 = 1;
      local_1bc = 1;
    }
    if ((uVar4 & 0x800) != 0) {
      local_190 = FUN_001351c0(param_2);
      local_18c = 1;
      local_1bc = 1;
    }
    if ((uVar4 & 0x1000) != 0) {
      local_184 = FUN_001351c0(param_2);
      local_180 = 1;
      local_188 = 1;
    }
    if ((uVar4 & 0x2000) != 0) {
      local_17c = FUN_001351c0(param_2);
      local_178 = 1;
      local_188 = 1;
    }
    if ((uVar4 & 0x4000) != 0) {
      local_174 = FUN_001351c0(param_2);
      local_170 = 1;
      local_188 = 1;
    }
    if ((uVar4 & 0x8000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      local_164 = 1;
      local_168 = (uint)uVar2;
      local_16c = 1;
    }
    if ((uVar4 & 0x10000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      param_2->position = param_2->position + 8;
      local_160 = (uint)uVar2;
      local_15c = 1;
      local_16c = 1;
    }
    if ((uVar4 & 0x20000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      local_154 = CONCAT44(uVar10,(uint)uVar2);
      local_14c = 1;
      local_158 = 1;
    }
    if ((uVar4 & 0x40000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      local_148 = CONCAT44(uVar10,(uint)uVar2);
      local_140 = 1;
      local_158 = 1;
    }
    if ((uVar4 & 0x80000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      local_13c = CONCAT44(uVar10,(uint)uVar2);
      local_134 = 1;
      local_158 = 1;
    }
    if ((uVar4 & 0x100000) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_12f = uVar3 == 1;
      local_12e = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x200000) != 0) {
      local_12c = Packet::gT_unsigned_int(param_2);
      local_128 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x400000) != 0) {
      local_124 = FUN_001351c0(param_2);
      local_120 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x800000) != 0) {
      local_11c = FUN_001351c0(param_2);
      local_118 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x1000000) != 0) {
      local_114 = FUN_001351c0(param_2);
      local_110 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x2000000) != 0) {
      local_10c = FUN_001351c0(param_2);
      local_108 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x4000000) != 0) {
      local_104 = FUN_001351c0(param_2);
      local_100 = 1;
      local_130 = 1;
    }
    if ((uVar4 & 0x8000000) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_fb = uVar3 == 1;
      local_fa = 1;
      local_fc = 1;
    }
    if ((uVar4 & 0x10000000) != 0) {
      local_f8 = FUN_001351c0(param_2);
      local_f4 = 1;
      local_fc = 1;
    }
    if ((uVar4 & 0x20000000) != 0) {
      local_f0 = FUN_001351c0(param_2);
      local_ec = 1;
      local_fc = 1;
    }
    if ((uVar4 & 0x40000000) != 0) {
      local_e8 = FUN_001351c0(param_2);
      local_e4 = 1;
      local_fc = 1;
    }
    if ((uVar4 & 0x80000000) != 0) {
      local_e0 = FUN_001351c0(param_2);
      local_dc = 1;
      local_fc = 1;
    }
    if ((uVar4 >> 0x20 & 1) != 0) {
      FUN_00546250(&local_214,param_2,0);
      local_cc = 1;
      local_fc = 1;
      local_d0 = local_20c;
      local_d8 = CONCAT44(local_210,local_214);
    }
    if ((uVar4 >> 0x21 & 1) != 0) {
      FUN_00546250(&local_214,param_2,0);
      local_bc = 1;
      local_fc = 1;
      local_c0 = local_20c;
      local_c8 = CONCAT44(local_210,local_214);
    }
    if ((uVar4 >> 0x22 & 1) != 0) {
      FUN_00546250(&local_214,param_2,0);
      local_ac = 1;
      local_fc = 1;
      local_b8 = CONCAT44(local_210,local_214);
      local_b0 = local_20c;
    }
    if ((uVar4 >> 0x23 & 1) != 0) {
      local_a4 = FUN_001351c0(param_2);
      local_a0 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x24 & 1) != 0) {
      local_9c = FUN_001351c0(param_2);
      local_98 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x25 & 1) != 0) {
      local_8c = FUN_001351c0(param_2);
      local_88 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x26 & 1) != 0) {
      local_84 = FUN_001351c0(param_2);
      local_80 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x27 & 1) != 0) {
      local_7c = FUN_001351c0(param_2);
      local_78 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x28 & 1) != 0) {
      local_74 = FUN_001351c0(param_2);
      local_70 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x29 & 1) != 0) {
      local_6c = FUN_001351c0(param_2);
      local_68 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x2a & 1) != 0) {
      local_64 = FUN_001351c0(param_2);
      local_60 = 1;
      local_a8 = 1;
    }
    if ((uVar4 >> 0x2b & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_50 = 1;
      local_a8 = 1;
      local_54 = (float)(uVar3 & 0xff) * DAT_00cb6b4c;
      local_5c = CONCAT44((float)(uVar3 >> 8 & 0xff) * DAT_00cb6b4c,
                          (float)(uVar3 >> 0x10 & 0xff) * DAT_00cb6b4c);
    }
    if ((uVar4 >> 0x2c & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      local_40 = 1;
      local_a8 = 1;
      local_44 = (float)(uVar3 & 0xff) * DAT_00cb6b4c;
      local_4c = CONCAT44((float)(uVar3 >> 8 & 0xff) * DAT_00cb6b4c,
                          (float)(uVar3 >> 0x10 & 0xff) * DAT_00cb6b4c);
    }
    if ((uVar4 >> 0x2f & 1) != 0) {
      local_38 = FUN_001351c0(param_2);
      local_34 = 1;
      local_3c = 1;
    }
    uVar2 = FUN_00121a30(param_2);
  }
  local_30 = (uint)uVar2;
  lVar1 = *(long *)((long)&__DT_RELA[0xd02].r_info +
                   *(long *)(*(long *)((long)&__DT_RELA[0xcf5].r_info + *param_1) + 8));
  puVar5 = (undefined8 *)operator_new(0x1e0);
  *puVar5 = CONCAT44(uStack_204,local_208);
  lVar6 = (long)puVar5 - (long)((ulong)(puVar5 + 1) & 0xfffffffffffffff8);
  puVar5[0x3b] = CONCAT44(uStack_2c,local_30);
  puVar7 = (undefined8 *)((long)&local_208 - lVar6);
  puVar8 = (undefined8 *)((ulong)(puVar5 + 1) & 0xfffffffffffffff8);
  for (uVar4 = (ulong)((int)lVar6 + 0x1e0U >> 3); uVar4 != 0; uVar4 = uVar4 - 1) {
    *puVar8 = *puVar7;
    puVar7 = puVar7 + (ulong)bVar9 * -2 + 1;
    puVar8 = puVar8 + (ulong)bVar9 * -2 + 1;
  }
  lVar6 = *(long *)(lVar1 + 0x98);
  *(undefined8 **)(lVar1 + 0x98) = puVar5;
  if (lVar6 != 0) {
    HeapInterface::Free(lVar6,0x1e0);
  }
  *(undefined1 *)(lVar1 + 0xa0) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED` @ 001d3d00
```c

/* WARNING: Type propagation algorithm not settling */

undefined *
jag::packethandlers::Social::op130_RELATIONSHIP_DELTA_UNCONFIRMED(long *param_1,PacketCore *param_2)

{
  long lVar1;
  ushort uVar2;
  uint uVar3;
  ulong uVar4;
  undefined8 *puVar5;
  long lVar6;
  undefined8 *puVar7;
  undefined8 *puVar8;
  byte bVar9;
  undefined4 uVar10;
  uint uStack_214;
  uint uStack_210;
  uint uStack_20c;
  uint uStack_208;
  undefined4 uStack_204;
  undefined1 uStack_200;
  undefined4 uStack_1fc;
  undefined1 uStack_1f8;
  undefined4 uStack_1f4;
  undefined1 uStack_1f0;
  undefined4 uStack_1ec;
  undefined1 uStack_1e8;
  undefined8 uStack_1e4;
  uint uStack_1dc;
  undefined1 uStack_1d8;
  undefined4 uStack_1d4;
  undefined1 uStack_1d0;
  undefined4 uStack_1cc;
  undefined1 uStack_1c8;
  undefined4 uStack_1c4;
  undefined1 uStack_1c0;
  undefined1 uStack_1bc;
  undefined4 uStack_1b8;
  undefined1 uStack_1b4;
  uint uStack_1b0;
  undefined1 uStack_1ac;
  undefined4 uStack_1a8;
  undefined1 uStack_1a4;
  undefined4 uStack_1a0;
  undefined1 uStack_19c;
  undefined4 uStack_198;
  undefined1 uStack_194;
  undefined4 uStack_190;
  undefined1 uStack_18c;
  undefined1 uStack_188;
  undefined4 uStack_184;
  undefined1 uStack_180;
  undefined4 uStack_17c;
  undefined1 uStack_178;
  undefined4 uStack_174;
  undefined1 uStack_170;
  undefined1 uStack_16c;
  uint uStack_168;
  undefined1 uStack_164;
  uint uStack_160;
  undefined1 uStack_15c;
  undefined1 uStack_158;
  undefined8 uStack_154;
  undefined1 uStack_14c;
  undefined8 uStack_148;
  undefined1 uStack_140;
  undefined8 uStack_13c;
  undefined1 uStack_134;
  undefined1 uStack_130;
  undefined1 uStack_12f;
  undefined1 uStack_12e;
  uint uStack_12c;
  undefined1 uStack_128;
  undefined4 uStack_124;
  undefined1 uStack_120;
  undefined4 uStack_11c;
  undefined1 uStack_118;
  undefined4 uStack_114;
  undefined1 uStack_110;
  undefined4 uStack_10c;
  undefined1 uStack_108;
  undefined4 uStack_104;
  undefined1 uStack_100;
  undefined1 uStack_fc;
  undefined1 uStack_fb;
  undefined1 uStack_fa;
  undefined4 uStack_f8;
  undefined1 uStack_f4;
  undefined4 uStack_f0;
  undefined1 uStack_ec;
  undefined4 uStack_e8;
  undefined1 uStack_e4;
  undefined4 uStack_e0;
  undefined1 uStack_dc;
  undefined8 uStack_d8;
  uint uStack_d0;
  undefined1 uStack_cc;
  undefined8 uStack_c8;
  uint uStack_c0;
  undefined1 uStack_bc;
  undefined8 uStack_b8;
  uint uStack_b0;
  undefined1 uStack_ac;
  undefined1 uStack_a8;
  undefined4 uStack_a4;
  undefined1 uStack_a0;
  undefined4 uStack_9c;
  undefined1 uStack_98;
  undefined4 uStack_94;
  undefined1 uStack_90;
  undefined4 uStack_8c;
  undefined1 uStack_88;
  undefined4 uStack_84;
  undefined1 uStack_80;
  undefined4 uStack_7c;
  undefined1 uStack_78;
  undefined4 uStack_74;
  undefined1 uStack_70;
  undefined4 uStack_6c;
  undefined1 uStack_68;
  undefined4 uStack_64;
  undefined1 uStack_60;
  undefined8 uStack_5c;
  float fStack_54;
  undefined1 uStack_50;
  undefined8 uStack_4c;
  float fStack_44;
  undefined1 uStack_40;
  undefined1 uStack_3c;
  undefined4 uStack_38;
  undefined1 uStack_34;
  uint uStack_30;
  uint uStack_2c;
  
  bVar9 = 0;
  uStack_208 = uStack_208 & 0xffffff00;
  uStack_200 = 0;
  uStack_1f8 = 0;
  uStack_1f0 = 0;
  uStack_1e8 = 0;
  uStack_1e4 = 0;
  uStack_1dc = 0;
  uStack_1d8 = 0;
  uStack_1d0 = 0;
  uStack_1c8 = 0;
  uStack_1c0 = 0;
  uStack_1bc = 0;
  uStack_1b4 = 0;
  uStack_1ac = 0;
  uStack_1a4 = 0;
  uStack_19c = 0;
  uStack_194 = 0;
  uStack_18c = 0;
  uStack_188 = 0;
  uStack_180 = 0;
  uStack_178 = 0;
  uStack_170 = 0;
  uStack_16c = 0;
  uStack_164 = 0;
  uStack_15c = 0;
  uStack_158 = 0;
  uStack_14c = 0;
  uStack_140 = 0;
  uStack_134 = 0;
  uStack_130 = 0;
  uStack_12e = 0;
  uStack_128 = 0;
  uStack_120 = 0;
  uStack_118 = 0;
  uStack_110 = 0;
  uStack_108 = 0;
  uStack_100 = 0;
  uStack_fc = 0;
  uStack_fa = 0;
  uStack_f4 = 0;
  uStack_ec = 0;
  uStack_e4 = 0;
  uStack_dc = 0;
  uStack_d8 = 0;
  uStack_d0 = 0;
  uStack_cc = 0;
  uStack_c8 = 0;
  uStack_c0 = 0;
  uStack_bc = 0;
  uStack_b8 = 0;
  uStack_b0 = 0;
  uStack_ac = 0;
  uStack_a8 = 0;
  uStack_a0 = 0;
  uStack_98 = 0;
  uStack_90 = 0;
  uStack_88 = 0;
  uStack_80 = 0;
  uStack_78 = 0;
  uStack_70 = 0;
  uStack_68 = 0;
  uStack_60 = 0;
  uStack_5c = 0;
  fStack_54 = 0.0;
  uStack_50 = 0;
  uStack_4c = 0;
  fStack_44 = 0.0;
  uStack_40 = 0;
  uStack_3c = 0;
  uStack_34 = 0;
  uStack_30 = 0;
  uStack_2c = uStack_2c & 0xffffff00;
  uVar4 = Packet::gT_ulong(param_2);
  if (uVar4 == 0) {
    uVar2 = FUN_00121a30(param_2);
    uStack_204 = 0;
    uStack_1fc = 0;
    uStack_1f4 = 0;
    uStack_1ec = 0;
    uStack_1e4 = 0;
    uStack_1dc = 0;
    uStack_1d4 = 0;
    uStack_1cc = 0;
    uStack_1c4 = 0;
    uStack_1b8 = 0;
    uStack_1b0 = 0;
    uStack_1a8 = 0;
    uStack_1a0 = 0;
    uStack_198 = 0;
    uStack_190 = 0;
    uStack_184 = 0;
    uStack_17c = 0;
    uStack_174 = 0;
    uStack_168 = 0;
    uStack_160 = 0;
    uStack_154 = 0;
    uStack_148 = 0;
    uStack_13c = 0;
    uStack_12f = 0;
    uStack_12c = 0;
    uStack_124 = 0;
    uStack_11c = 0;
    uStack_114 = 0;
    uStack_10c = 0;
    uStack_104 = 0;
    uStack_fb = 0;
    uStack_f8 = 0;
    uStack_f0 = 0;
    uStack_e8 = 0;
    uStack_e0 = 0;
    uStack_d8 = 0;
    uStack_d0 = 0;
    uStack_c8 = 0;
    uStack_c0 = 0;
    uStack_b8 = 0;
    uStack_b0 = 0;
    uStack_a4 = 0;
    uStack_9c = 0;
    uStack_94 = 0;
    uStack_8c = 0;
    uStack_84 = 0;
    uStack_7c = 0;
    uStack_74 = 0;
    uStack_6c = 0;
    uStack_64 = 0;
    uStack_5c = 0;
    fStack_54 = 0.0;
    uStack_4c = 0;
    fStack_44 = 0.0;
    uStack_38 = 0;
  }
  else {
    uStack_2c = CONCAT31(uStack_2c._1_3_,1);
    if ((uVar4 & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_200 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
      uStack_204 = (undefined4)CONCAT71((uint7)(uint3)uVar3,0xff);
    }
    if ((uVar4 & 2) != 0) {
      uStack_1fc = FUN_001351c0(param_2);
      uStack_1f8 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 & 4) != 0) {
      uStack_1f4 = FUN_001351c0(param_2);
      uStack_1f0 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 & 8) != 0) {
      uStack_1ec = FUN_001351c0(param_2);
      uStack_1e8 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 & 0x10) != 0) {
      FUN_00546250(&uStack_214,param_2,0);
      uStack_1d8 = 1;
      uStack_1dc = uStack_20c ^ DAT_00cb6c50;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
      uStack_1e4 = CONCAT44(uStack_210 ^ DAT_00cb6c50,uStack_214 ^ DAT_00cb6c50);
    }
    if ((uVar4 & 0x20) != 0) {
      uStack_1d4 = FUN_001351c0(param_2);
      uStack_1d0 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 >> 0x2d & 1) != 0) {
      uStack_1cc = FUN_001351c0(param_2);
      uStack_1c8 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 >> 0x2e & 1) != 0) {
      uStack_1c4 = FUN_001351c0(param_2);
      uStack_1c0 = 1;
      uStack_208 = CONCAT31(uStack_208._1_3_,1);
    }
    if ((uVar4 & 0x40) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_1b4 = 1;
      uStack_1bc = 1;
      uStack_1b8 = (undefined4)CONCAT71((uint7)(uint3)uVar3,0xff);
    }
    if ((uVar4 & 0x80) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uStack_1ac = 1;
      uStack_1b0 = (uint)uVar2;
      uStack_1bc = 1;
    }
    if ((uVar4 & 0x100) != 0) {
      uStack_1a8 = FUN_001351c0(param_2);
      uStack_1a4 = 1;
      uStack_1bc = 1;
    }
    if ((uVar4 & 0x200) != 0) {
      uStack_1a0 = FUN_001351c0(param_2);
      uStack_19c = 1;
      uStack_1bc = 1;
    }
    if ((uVar4 & 0x400) != 0) {
      uStack_198 = FUN_001351c0(param_2);
      uStack_194 = 1;
      uStack_1bc = 1;
    }
    if ((uVar4 & 0x800) != 0) {
      uStack_190 = FUN_001351c0(param_2);
      uStack_18c = 1;
      uStack_1bc = 1;
    }
    if ((uVar4 & 0x1000) != 0) {
      uStack_184 = FUN_001351c0(param_2);
      uStack_180 = 1;
      uStack_188 = 1;
    }
    if ((uVar4 & 0x2000) != 0) {
      uStack_17c = FUN_001351c0(param_2);
      uStack_178 = 1;
      uStack_188 = 1;
    }
    if ((uVar4 & 0x4000) != 0) {
      uStack_174 = FUN_001351c0(param_2);
      uStack_170 = 1;
      uStack_188 = 1;
    }
    if ((uVar4 & 0x8000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uStack_164 = 1;
      uStack_168 = (uint)uVar2;
      uStack_16c = 1;
    }
    if ((uVar4 & 0x10000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      param_2->position = param_2->position + 8;
      uStack_160 = (uint)uVar2;
      uStack_15c = 1;
      uStack_16c = 1;
    }
    if ((uVar4 & 0x20000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      uStack_154 = CONCAT44(uVar10,(uint)uVar2);
      uStack_14c = 1;
      uStack_158 = 1;
    }
    if ((uVar4 & 0x40000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      uStack_148 = CONCAT44(uVar10,(uint)uVar2);
      uStack_140 = 1;
      uStack_158 = 1;
    }
    if ((uVar4 & 0x80000) != 0) {
      uVar2 = FUN_00121a30(param_2);
      uVar10 = FUN_001351c0(param_2);
      uStack_13c = CONCAT44(uVar10,(uint)uVar2);
      uStack_134 = 1;
      uStack_158 = 1;
    }
    if ((uVar4 & 0x100000) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_12f = uVar3 == 1;
      uStack_12e = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x200000) != 0) {
      uStack_12c = Packet::gT_unsigned_int(param_2);
      uStack_128 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x400000) != 0) {
      uStack_124 = FUN_001351c0(param_2);
      uStack_120 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x800000) != 0) {
      uStack_11c = FUN_001351c0(param_2);
      uStack_118 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x1000000) != 0) {
      uStack_114 = FUN_001351c0(param_2);
      uStack_110 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x2000000) != 0) {
      uStack_10c = FUN_001351c0(param_2);
      uStack_108 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x4000000) != 0) {
      uStack_104 = FUN_001351c0(param_2);
      uStack_100 = 1;
      uStack_130 = 1;
    }
    if ((uVar4 & 0x8000000) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_fb = uVar3 == 1;
      uStack_fa = 1;
      uStack_fc = 1;
    }
    if ((uVar4 & 0x10000000) != 0) {
      uStack_f8 = FUN_001351c0(param_2);
      uStack_f4 = 1;
      uStack_fc = 1;
    }
    if ((uVar4 & 0x20000000) != 0) {
      uStack_f0 = FUN_001351c0(param_2);
      uStack_ec = 1;
      uStack_fc = 1;
    }
    if ((uVar4 & 0x40000000) != 0) {
      uStack_e8 = FUN_001351c0(param_2);
      uStack_e4 = 1;
      uStack_fc = 1;
    }
    if ((uVar4 & 0x80000000) != 0) {
      uStack_e0 = FUN_001351c0(param_2);
      uStack_dc = 1;
      uStack_fc = 1;
    }
    if ((uVar4 >> 0x20 & 1) != 0) {
      FUN_00546250(&uStack_214,param_2,0);
      uStack_cc = 1;
      uStack_fc = 1;
      uStack_d0 = uStack_20c;
      uStack_d8 = CONCAT44(uStack_210,uStack_214);
    }
    if ((uVar4 >> 0x21 & 1) != 0) {
      FUN_00546250(&uStack_214,param_2,0);
      uStack_bc = 1;
      uStack_fc = 1;
      uStack_c0 = uStack_20c;
      uStack_c8 = CONCAT44(uStack_210,uStack_214);
    }
    if ((uVar4 >> 0x22 & 1) != 0) {
      FUN_00546250(&uStack_214,param_2,0);
      uStack_ac = 1;
      uStack_fc = 1;
      uStack_b8 = CONCAT44(uStack_210,uStack_214);
      uStack_b0 = uStack_20c;
    }
    if ((uVar4 >> 0x23 & 1) != 0) {
      uStack_a4 = FUN_001351c0(param_2);
      uStack_a0 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x24 & 1) != 0) {
      uStack_9c = FUN_001351c0(param_2);
      uStack_98 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x25 & 1) != 0) {
      uStack_8c = FUN_001351c0(param_2);
      uStack_88 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x26 & 1) != 0) {
      uStack_84 = FUN_001351c0(param_2);
      uStack_80 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x27 & 1) != 0) {
      uStack_7c = FUN_001351c0(param_2);
      uStack_78 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x28 & 1) != 0) {
      uStack_74 = FUN_001351c0(param_2);
      uStack_70 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x29 & 1) != 0) {
      uStack_6c = FUN_001351c0(param_2);
      uStack_68 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x2a & 1) != 0) {
      uStack_64 = FUN_001351c0(param_2);
      uStack_60 = 1;
      uStack_a8 = 1;
    }
    if ((uVar4 >> 0x2b & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_50 = 1;
      uStack_a8 = 1;
      fStack_54 = (float)(uVar3 & 0xff) * DAT_00cb6b4c;
      uStack_5c = CONCAT44((float)(uVar3 >> 8 & 0xff) * DAT_00cb6b4c,
                           (float)(uVar3 >> 0x10 & 0xff) * DAT_00cb6b4c);
    }
    if ((uVar4 >> 0x2c & 1) != 0) {
      uVar3 = Packet::gT_unsigned_int(param_2);
      uStack_40 = 1;
      uStack_a8 = 1;
      fStack_44 = (float)(uVar3 & 0xff) * DAT_00cb6b4c;
      uStack_4c = CONCAT44((float)(uVar3 >> 8 & 0xff) * DAT_00cb6b4c,
                           (float)(uVar3 >> 0x10 & 0xff) * DAT_00cb6b4c);
    }
    if ((uVar4 >> 0x2f & 1) != 0) {
      uStack_38 = FUN_001351c0(param_2);
      uStack_34 = 1;
      uStack_3c = 1;
    }
    uVar2 = FUN_00121a30(param_2);
  }
  uStack_30 = (uint)uVar2;
  lVar1 = *(long *)((long)&__DT_RELA[0xd02].r_info +
                   *(long *)(*(long *)((long)&__DT_RELA[0xcf5].r_info + *param_1) + 8));
  puVar5 = (undefined8 *)operator_new(0x1e0);
  *puVar5 = CONCAT44(uStack_204,uStack_208);
  lVar6 = (long)puVar5 - (long)((ulong)(puVar5 + 1) & 0xfffffffffffffff8);
  puVar5[0x3b] = CONCAT44(uStack_2c,uStack_30);
  puVar7 = (undefined8 *)((long)&uStack_208 - lVar6);
  puVar8 = (undefined8 *)((ulong)(puVar5 + 1) & 0xfffffffffffffff8);
  for (uVar4 = (ulong)((int)lVar6 + 0x1e0U >> 3); uVar4 != 0; uVar4 = uVar4 - 1) {
    *puVar8 = *puVar7;
    puVar7 = puVar7 + (ulong)bVar9 * -2 + 1;
    puVar8 = puVar8 + (ulong)bVar9 * -2 + 1;
  }
  lVar6 = *(long *)(lVar1 + 0x98);
  *(undefined8 **)(lVar1 + 0x98) = puVar5;
  if (lVar6 != 0) {
    HeapInterface::Free(lVar6,0x1e0);
  }
  *(undefined1 *)(lVar1 + 0xa0) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Camera::CAM_UPDATE` @ 001d3d10
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* op77 CAM_UPDATE [VERIFIED 948-5]. Bitflag-driven camera COMMAND on the in-render camera
   (client[RELA 0xcf5]+0x78): bit0->+0xa0, bit3->target mode (FUN_006e8b00), bit4->FUN_006e9070,
   bit7->matrix params +0x138..+0x1e8 (pos/rotate/zoom doubles via FUN_00546250).
   Does NOT write +0x418, the camera-target triple +0x634/638/63c, +0x650, or eye +0x570.
   It MODIFIES an already-reset camera; it does NOT position/arm it. NOT the streamer trigger.
   Production sends it AFTER op81 to tweak the camera. [RE 2026-06-23] */

undefined * jag::packethandlers::Camera::CAM_UPDATE(long *thisPtr,long packetPtr)

{
  int iVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  byte bVar5;
  long lVar6;
  long *plVar7;
  int *piVar8;
  code *pcVar9;
  undefined8 uVar10;
  undefined8 uVar11;
  ushort uVar12;
  ulong uVar13;
  long lVar14;
  long lVar15;
  long lVar16;
  undefined8 *puVar17;
  ulong uVar18;
  int *piVar19;
  long lVar20;
  long lVar21;
  long lVar22;
  uint uVar23;
  int *piVar24;
  undefined **ppuVar25;
  uint uVar26;
  float fVar27;
  undefined4 uVar28;
  double dVar29;
  undefined8 *local_50;
  undefined8 local_48;
  undefined8 *puStack_40;
  
  lVar21 = *(long *)(packetPtr + 0x18);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *thisPtr);
  *(long *)(packetPtr + 0x18) = lVar21 + 1;
  lVar14 = *(long *)(packetPtr + 0x10);
  lVar20 = lVar6 + 0x78;
  bVar2 = *(byte *)(lVar14 + lVar21);
  *(byte *)(lVar6 + 0xa0) = bVar2 & 1;
  if ((bVar2 & 8) != 0) {
    *(long *)(packetPtr + 0x18) = lVar21 + 2;
    bVar3 = *(byte *)(lVar14 + 1 + lVar21);
    if ((uint)bVar3 != *(uint *)(lVar6 + 0xa8)) {
      FUN_006e8b00(lVar20,bVar3,1);
    }
  }
  if ((bVar2 & 0x10) != 0) {
    lVar21 = *(long *)(packetPtr + 0x18);
    *(long *)(packetPtr + 0x18) = lVar21 + 1;
    bVar3 = *(byte *)(*(long *)(packetPtr + 0x10) + lVar21);
    if ((uint)bVar3 != *(uint *)(lVar6 + 0xc0)) {
      FUN_006e9070(lVar20,bVar3,1);
    }
  }
  if (-1 < (char)bVar2) goto LAB_001d3daa;
  uVar13 = FUN_00121a30(packetPtr);
  uVar18 = uVar13 & 0xffffffff;
  if ((uVar13 & 1) != 0) {
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x140) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x138) = (double)(float)local_48;
    *(double *)(lVar6 + 0x148) = (double)puStack_40._0_4_;
  }
  if ((uVar13 & 2) != 0) {
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x158) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x150) = (double)(float)local_48;
    *(double *)(lVar6 + 0x160) = (double)puStack_40._0_4_;
  }
  if ((uVar13 & 4) != 0) {
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x170) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x168) = (double)(float)local_48;
    *(double *)(lVar6 + 0x178) = (double)puStack_40._0_4_;
  }
  if ((uVar13 & 8) != 0) {
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x188) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x180) = (double)(float)local_48;
    *(double *)(lVar6 + 400) = (double)puStack_40._0_4_;
  }
  if ((uVar13 & 0x10) != 0) {
    fVar27 = (float)FUN_001351c0(packetPtr);
    dVar29 = (double)fVar27;
    if (fVar27 <= _DAT_00cb7500) {
      dVar29 = DAT_00cb6be8;
    }
    *(double *)(lVar6 + 0x1e8) = dVar29;
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(undefined1 *)(lVar6 + 0x1f8) = 1;
    *(double *)(lVar6 + 0x1f0) = (double)fVar27;
  }
  if ((uVar13 & 0x20) != 0) {
    uVar28 = FUN_001351c0(packetPtr);
    *(undefined4 *)(lVar6 + 0x1dc) = uVar28;
    uVar28 = FUN_001351c0(packetPtr);
    *(undefined4 *)(lVar6 + 0x1d8) = uVar28;
  }
  if ((uVar13 & 0x40) != 0) {
    lVar21 = *(long *)(packetPtr + 0x18);
    *(long *)(packetPtr + 0x18) = lVar21 + 1;
    *(uint *)(lVar6 + 0xa4) = (uint)*(byte *)(*(long *)(packetPtr + 0x10) + lVar21);
  }
  if ((uVar13 & 0x80) != 0) {
    *(long *)(packetPtr + 0x18) = *(long *)(packetPtr + 0x18) + 4;
  }
  if ((uVar13 & 0x100) != 0) {
    lVar21 = *(long *)(packetPtr + 0x18);
    *(long *)(packetPtr + 0x18) = lVar21 + 1;
    bVar3 = *(byte *)(*(long *)(packetPtr + 0x10) + lVar21);
    *(byte *)(lVar6 + 0xfc) = bVar3 & 1;
    *(byte *)(lVar6 + 0xfd) = bVar3 >> 1 & 1;
  }
  if ((uVar13 & 0x200) != 0) {
    lVar14 = *(long *)(packetPtr + 0x18);
    lVar15 = *(long *)(packetPtr + 0x10);
    lVar21 = lVar14 + 1;
    *(long *)(packetPtr + 0x18) = lVar21;
    bVar3 = *(byte *)(lVar15 + lVar14);
    if (bVar3 != 0) {
      uVar26 = 0;
      do {
        piVar24 = *(int **)(lVar6 + 0xe0);
        piVar8 = *(int **)(lVar6 + 0xd8);
        *(long *)(packetPtr + 0x18) = lVar21 + 1;
        cVar4 = *(char *)(lVar15 + lVar21);
        *(long *)(packetPtr + 0x18) = lVar21 + 2;
        bVar5 = *(byte *)(lVar15 + 1 + lVar21);
        lVar22 = (long)piVar24 - (long)piVar8;
        lVar14 = (lVar22 >> 3) * -0x5555555555555555;
        uVar23 = (uint)bVar5;
        if (cVar4 == '\0') {
          while (0 < lVar22) {
            lVar21 = lVar14 >> 1;
            piVar19 = piVar8 + lVar21 * 6;
            if ((int)(uint)bVar5 <= *piVar19) {
              if (lVar21 == 0) break;
              lVar15 = lVar14 >> 2;
              piVar19 = piVar8 + lVar15 * 6;
              iVar1 = *piVar19;
              lVar14 = lVar21;
              lVar21 = lVar15;
              while ((int)(uint)bVar5 <= iVar1) {
                if (lVar21 == 0) goto LAB_001d400f;
                piVar19 = piVar8 + (lVar21 >> 1) * 6;
                lVar14 = lVar21;
                lVar21 = lVar21 >> 1;
                iVar1 = *piVar19;
              }
            }
            piVar8 = piVar19 + 6;
            lVar14 = lVar14 - (lVar21 + 1);
            lVar22 = lVar14;
          }
LAB_001d400f:
          if ((piVar8 != piVar24) && (*piVar8 <= (int)uVar23)) {
            piVar19 = piVar8 + 6;
            if ((piVar19 < piVar24) &&
               (uVar18 = ((long)piVar24 - (long)piVar19 >> 3) * -0x5555555555555555,
               0 < (long)piVar24 - (long)piVar19)) {
              uVar23 = (uint)uVar18 & 3;
              if ((uVar18 & 3) == 0) goto LAB_001d4733;
              if (uVar23 != 1) {
                if (uVar23 != 2) {
                  lVar21 = *(long *)(piVar8 + 2);
                  uVar10 = *(undefined8 *)(piVar8 + 8);
                  uVar11 = *(undefined8 *)(piVar8 + 10);
                  piVar8[8] = 0;
                  piVar8[9] = 0;
                  piVar8[10] = 0;
                  piVar8[0xb] = 0;
                  *(undefined8 *)(piVar8 + 2) = uVar10;
                  *piVar8 = *piVar19;
                  *(undefined8 *)(piVar8 + 4) = uVar11;
                  if (lVar21 != 0) {
                    ref_counter_base::DecRef();
                  }
                  uVar18 = uVar18 - 1;
                  piVar19 = piVar8 + 0xc;
                }
                lVar21 = *(long *)(piVar19 + -4);
                uVar10 = *(undefined8 *)(piVar19 + 2);
                uVar11 = *(undefined8 *)(piVar19 + 4);
                piVar19[2] = 0;
                piVar19[3] = 0;
                piVar19[4] = 0;
                piVar19[5] = 0;
                *(undefined8 *)(piVar19 + -4) = uVar10;
                piVar19[-6] = *piVar19;
                *(undefined8 *)(piVar19 + -2) = uVar11;
                if (lVar21 != 0) {
                  ref_counter_base::DecRef();
                }
                uVar18 = uVar18 - 1;
                piVar19 = piVar19 + 6;
              }
              uVar10 = *(undefined8 *)(piVar19 + 2);
              uVar11 = *(undefined8 *)(piVar19 + 4);
              piVar19[2] = 0;
              piVar19[3] = 0;
              piVar19[4] = 0;
              piVar19[5] = 0;
              piVar19[-6] = *piVar19;
              lVar21 = *(long *)(piVar19 + -4);
              *(undefined8 *)(piVar19 + -2) = uVar11;
              *(undefined8 *)(piVar19 + -4) = uVar10;
              if (lVar21 != 0) {
                ref_counter_base::DecRef();
              }
              piVar19 = piVar19 + 6;
              for (uVar18 = uVar18 - 1; uVar18 != 0; uVar18 = uVar18 - 4) {
LAB_001d4733:
                lVar21 = *(long *)(piVar19 + -4);
                uVar10 = *(undefined8 *)(piVar19 + 2);
                uVar11 = *(undefined8 *)(piVar19 + 4);
                piVar19[2] = 0;
                piVar19[3] = 0;
                piVar19[4] = 0;
                piVar19[5] = 0;
                *(undefined8 *)(piVar19 + -4) = uVar10;
                piVar19[-6] = *piVar19;
                *(undefined8 *)(piVar19 + -2) = uVar11;
                if (lVar21 != 0) {
                  ref_counter_base::DecRef();
                }
                lVar21 = *(long *)(piVar19 + 2);
                uVar10 = *(undefined8 *)(piVar19 + 8);
                uVar11 = *(undefined8 *)(piVar19 + 10);
                piVar19[8] = 0;
                piVar19[9] = 0;
                piVar19[10] = 0;
                piVar19[0xb] = 0;
                *piVar19 = piVar19[6];
                *(undefined8 *)(piVar19 + 2) = uVar10;
                *(undefined8 *)(piVar19 + 4) = uVar11;
                if (lVar21 != 0) {
                  ref_counter_base::DecRef();
                }
                lVar21 = *(long *)(piVar19 + 8);
                uVar10 = *(undefined8 *)(piVar19 + 0xe);
                uVar11 = *(undefined8 *)(piVar19 + 0x10);
                piVar19[0xe] = 0;
                piVar19[0xf] = 0;
                piVar19[0x10] = 0;
                piVar19[0x11] = 0;
                *(undefined8 *)(piVar19 + 8) = uVar10;
                piVar19[6] = piVar19[0xc];
                *(undefined8 *)(piVar19 + 10) = uVar11;
                if (lVar21 != 0) {
                  ref_counter_base::DecRef();
                }
                uVar10 = *(undefined8 *)(piVar19 + 0x14);
                uVar11 = *(undefined8 *)(piVar19 + 0x16);
                piVar19[0x14] = 0;
                piVar19[0x15] = 0;
                piVar19[0x16] = 0;
                piVar19[0x17] = 0;
                piVar19[0xc] = piVar19[0x12];
                lVar21 = *(long *)(piVar19 + 0xe);
                *(undefined8 *)(piVar19 + 0x10) = uVar11;
                *(undefined8 *)(piVar19 + 0xe) = uVar10;
                if (lVar21 != 0) {
                  ref_counter_base::DecRef();
                }
                piVar19 = piVar19 + 0x18;
              }
              piVar24 = *(int **)(lVar6 + 0xe0);
            }
            puVar17 = *(undefined8 **)(piVar24 + -4);
            *(int **)(lVar6 + 0xe0) = piVar24 + -6;
joined_r0x001d45dd:
            if (puVar17 != (undefined8 *)0x0) {
              ref_counter_base::DecRef();
            }
          }
        }
        else {
          *(long *)(packetPtr + 0x18) = lVar21 + 3;
          while (0 < lVar22) {
            lVar22 = lVar14 >> 1;
            piVar19 = piVar8 + lVar22 * 6;
            if ((int)uVar23 <= *piVar19) {
              if (lVar22 == 0) break;
              lVar16 = lVar14 >> 2;
              piVar19 = piVar8 + lVar16 * 6;
              iVar1 = *piVar19;
              lVar14 = lVar22;
              lVar22 = lVar16;
              while ((int)uVar23 <= iVar1) {
                if (lVar22 == 0) goto LAB_001d449b;
                piVar19 = piVar8 + (lVar22 >> 1) * 6;
                lVar14 = lVar22;
                lVar22 = lVar22 >> 1;
                iVar1 = *piVar19;
              }
            }
            piVar8 = piVar19 + 6;
            lVar14 = lVar14 - (lVar22 + 1);
            lVar22 = lVar14;
          }
LAB_001d449b:
          if (((piVar8 == piVar24) || ((int)uVar23 < *piVar8)) ||
             (plVar7 = *(long **)(piVar8 + 4), plVar7 == (long *)0x0)) {
            cVar4 = *(char *)(lVar15 + lVar21 + 2);
            if (cVar4 == '\0') {
              puVar17 = (undefined8 *)FUN_00c29430(0x48);
              local_50 = (undefined8 *)0x0;
              if (puVar17 != (undefined8 *)0x0) {
                lVar21 = *(long *)(packetPtr + 0x18);
                *(uint *)((long)puVar17 + 0x2c) = uVar23;
                *(undefined4 *)(puVar17 + 5) = 0;
                *(undefined8 *)((long)puVar17 + 0x3c) = 0;
                puVar17[4] = &PTR_FUN_01366050;
                *(long *)(packetPtr + 0x18) = lVar21 + 1;
                *(uint *)(puVar17 + 6) = (uint)*(byte *)(*(long *)(packetPtr + 0x10) + lVar21);
                uVar28 = FUN_001351c0(packetPtr);
                *(undefined4 *)((long)puVar17 + 0x34) = uVar28;
                uVar28 = FUN_001351c0(packetPtr);
                ppuVar25 = &PTR_FUN_01366088;
                *(undefined4 *)(puVar17 + 7) = uVar28;
                *(undefined4 *)((long)puVar17 + 0x3c) = 0;
LAB_001d4587:
                local_50 = puVar17 + 4;
                puVar17[2] = 1;
                *puVar17 = ppuVar25;
                puVar17[1] = 0x100000001;
                puVar17[3] = local_50;
              }
            }
            else {
              if (cVar4 != '\x01') goto LAB_001d403f;
              puVar17 = (undefined8 *)FUN_00c29430(0x38);
              local_50 = (undefined8 *)0x0;
              if (puVar17 != (undefined8 *)0x0) {
                *(undefined4 *)(puVar17 + 5) = 1;
                *(uint *)((long)puVar17 + 0x2c) = uVar23;
                puVar17[4] = &PTR_FUN_013660b8;
                uVar28 = FUN_001351c0(packetPtr);
                ppuVar25 = &PTR_FUN_013660f0;
                *(undefined4 *)(puVar17 + 6) = uVar28;
                goto LAB_001d4587;
              }
            }
            local_48 = puVar17;
            puStack_40 = local_50;
            FUN_006e9d20(lVar20,&local_48);
            puVar17 = local_48;
            goto joined_r0x001d45dd;
          }
          (**(code **)(*plVar7 + 0x20))(plVar7,packetPtr);
        }
LAB_001d403f:
        uVar26 = uVar26 + 1;
        if (bVar3 == uVar26) goto LAB_001d40c0;
        lVar21 = *(long *)(packetPtr + 0x18);
        lVar15 = *(long *)(packetPtr + 0x10);
      } while( true );
    }
  }
LAB_001d40cd:
  if ((uVar18 & 0x400) != 0) {
    uVar12 = FUN_00121a30(packetPtr);
    *(uint *)(lVar6 + 0x118) = (uint)uVar12;
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(double *)(lVar6 + 0x120) = (double)fVar27;
  }
  if ((uVar18 & 0x800) != 0) {
    lVar20 = *(long *)(packetPtr + 0x18);
    *(long *)(packetPtr + 0x18) = lVar20 + 1;
    *(uint *)(lVar6 + 0x100) = (uint)*(byte *)(*(long *)(packetPtr + 0x10) + lVar20);
  }
  if ((uVar18 & 0x1000) != 0) {
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x1a0) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x198) = (double)(float)local_48;
    *(double *)(lVar6 + 0x1a8) = (double)puStack_40._0_4_;
    FUN_00546250(&local_48,packetPtr,0);
    *(double *)(lVar6 + 0x1b8) = (double)local_48._4_4_;
    *(double *)(lVar6 + 0x1b0) = (double)(float)local_48;
    *(double *)(lVar6 + 0x1c0) = (double)puStack_40._0_4_;
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(double *)(lVar6 + 0x1c8) = (double)fVar27;
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(double *)(lVar6 + 0x1d0) = (double)fVar27;
  }
  if ((uVar18 & 0x2000) != 0) {
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(double *)(lVar6 + 0x108) = (double)fVar27;
  }
  if ((uVar18 & 0x4000) != 0) {
    fVar27 = (float)FUN_001351c0(packetPtr);
    *(double *)(lVar6 + 0x110) = (double)fVar27;
  }
LAB_001d3daa:
  plVar7 = *(long **)(lVar6 + 0xb8);
  if ((plVar7 != (long *)0x0) && ((bVar2 & 0x20) != 0)) {
    (**(code **)(*plVar7 + 0x70))(plVar7,packetPtr,lVar6 + 0x80);
  }
  plVar7 = *(long **)(lVar6 + 0xd0);
  if ((plVar7 != (long *)0x0) && ((bVar2 & 0x40) != 0)) {
    (**(code **)(*plVar7 + 0x70))(plVar7,packetPtr,lVar6 + 0x80);
  }
  lVar20 = *thisPtr;
  lVar21 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar20);
  if (*(char *)(lVar21 + 0xa0) == '\0') {
    pcVar9 = *(code **)(*(long *)(lVar21 + 0x248) + 0x1e0);
    if (pcVar9 == FUN_006bc3d0) {
      *(undefined8 *)(lVar21 + 0x458) = 0;
      *(undefined8 *)(lVar21 + 0x460) = 0;
      *(undefined1 *)(lVar21 + 0x468) = 0;
      *(undefined1 *)(lVar21 + 0x469) = 1;
    }
    else {
      (*pcVar9)(lVar21 + 0x248);
      lVar20 = *thisPtr;
    }
  }
  lVar20 = *(long *)((long)&__DT_RELA[0xd00].r_offset + lVar20);
  *(undefined4 *)(lVar20 + 0x170) = *(undefined4 *)(*(long *)(lVar20 + 0x168) + 0x10);
  return &DAT_015d3620;
LAB_001d40c0:
  uVar18 = uVar13 & 0xffff;
  goto LAB_001d40cd;
}


```

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
