# Bit-parse helper decompiles


## `jag::game::RebuildSceneEntry::Reset` @ 006d8be0
```c

/* Setting prototype: void Reset(undefined4 * entry, undefined4 seed) */

void jag::game::RebuildSceneEntry::Reset(undefined4 *entry,undefined4 seed)

{
  long lVar1;
  void *__dest;
  long *plVar2;
  long *plVar3;
  long *plVar4;
  size_t __n;
  ulong uVar5;
  void *pvVar6;
  uint uVar7;
  void *__src;
  long *plVar8;
  long *plVar9;
  long *plVar10;
  long *plVar11;
  
  *entry = seed;
  *(undefined8 *)(entry + 2) = 0;
  *(undefined8 *)(entry + 4) = 0;
  *(undefined8 *)(entry + 6) = 0;
  *(undefined8 *)(entry + 8) = 0;
  *(undefined8 *)(entry + 10) = 0;
  *(undefined8 *)(entry + 0xc) = 0;
  *(undefined8 *)(entry + 0xe) = 0;
  *(undefined8 *)(entry + 0x10) = 0;
  *(undefined8 *)(entry + 0x12) = 0;
  *(undefined8 *)(entry + 0x14) = 0;
  *(undefined8 *)(entry + 0x16) = 0;
  *(undefined8 *)(entry + 0x18) = 0;
  __dest = (void *)FUN_00c29480(0x10);
  __src = *(void **)(entry + 2);
  pvVar6 = __dest;
  if (*(void **)(entry + 4) != __src) {
    __n = (long)*(void **)(entry + 4) - (long)__src;
    memmove(__dest,__src,__n);
    __src = *(void **)(entry + 2);
    pvVar6 = (void *)(__n + (long)__dest);
  }
  if (__src != (void *)0x0) {
    HeapInterface::Free(__src);
  }
  *(void **)(entry + 2) = __dest;
  *(void **)(entry + 4) = pvVar6;
  *(long *)(entry + 6) = (long)__dest + 0x10;
  if ((ulong)(*(long *)(entry + 0xc) - *(long *)(entry + 8)) < 0x50) {
    FUN_0018f970(entry + 8,0x14);
    uVar5 = *(long *)(entry + 0x12) - *(long *)(entry + 0xe);
  }
  else {
    uVar5 = *(long *)(entry + 0x12) - *(long *)(entry + 0xe);
  }
  if (uVar5 < 0x50) {
    FUN_0018f970(entry + 0xe,0x14);
  }
  if (0x13 < (ulong)((*(long *)(entry + 0x18) - *(long *)(entry + 0x14) >> 3) * -0x5555555555555555)
     ) {
    return;
  }
  plVar2 = (long *)FUN_00c29480(0x1e0);
  plVar9 = *(long **)(entry + 0x14);
  plVar11 = plVar2;
  if (*(long **)(entry + 0x16) != plVar9) {
    plVar11 = plVar9 + 3;
    uVar5 = ((ulong)((long)*(long **)(entry + 0x16) - (long)plVar11) >> 3) * 0xaaaaaaaaaaaaaab &
            0x1fffffffffffffff;
    uVar7 = (int)((ulong)((long)(plVar11 + uVar5 * 3) + (-0x18 - (long)plVar9)) >> 3) * -0x55555555
            + 1U & 3;
    plVar4 = plVar2;
    plVar8 = plVar9;
    if (uVar7 != 0) {
      if (uVar7 != 1) {
        plVar3 = plVar2;
        plVar10 = plVar9;
        if (uVar7 != 2) {
          *plVar2 = 0;
          plVar2[1] = 0;
          plVar2[2] = 0;
          *plVar2 = *plVar9;
          *plVar9 = 0;
          plVar3 = plVar2 + 3;
          lVar1 = plVar2[1];
          plVar2[1] = plVar9[1];
          plVar9[1] = lVar1;
          lVar1 = plVar2[2];
          plVar2[2] = plVar9[2];
          plVar9[2] = lVar1;
          plVar10 = plVar11;
        }
        plVar3[1] = 0;
        plVar3[2] = 0;
        plVar8 = plVar10 + 3;
        *plVar3 = 0;
        plVar4 = plVar3 + 3;
        *plVar3 = *plVar10;
        *plVar10 = 0;
        lVar1 = plVar3[1];
        plVar3[1] = plVar10[1];
        plVar10[1] = lVar1;
        lVar1 = plVar3[2];
        plVar3[2] = plVar10[2];
        plVar10[2] = lVar1;
      }
      plVar4[1] = 0;
      plVar4[2] = 0;
      *plVar4 = 0;
      *plVar4 = *plVar8;
      *plVar8 = 0;
      lVar1 = plVar4[1];
      plVar4[1] = plVar8[1];
      plVar8[1] = lVar1;
      lVar1 = plVar4[2];
      plVar4[2] = plVar8[2];
      plVar8[2] = lVar1;
      plVar8 = plVar8 + 3;
      plVar4 = plVar4 + 3;
      goto joined_r0x006d8e64;
    }
    do {
      plVar4[1] = 0;
      plVar4[2] = 0;
      *plVar4 = 0;
      *plVar4 = *plVar8;
      *plVar8 = 0;
      lVar1 = plVar4[1];
      plVar4[1] = plVar8[1];
      plVar8[1] = lVar1;
      lVar1 = plVar4[2];
      plVar4[2] = plVar8[2];
      plVar8[2] = lVar1;
      plVar4[4] = 0;
      plVar4[5] = 0;
      plVar4[3] = 0;
      plVar4[3] = plVar8[3];
      plVar8[3] = 0;
      lVar1 = plVar4[4];
      plVar4[4] = plVar8[4];
      plVar8[4] = lVar1;
      lVar1 = plVar4[5];
      plVar4[5] = plVar8[5];
      plVar8[5] = lVar1;
      plVar4[6] = 0;
      plVar4[7] = 0;
      plVar4[8] = 0;
      plVar4[6] = plVar8[6];
      plVar8[6] = 0;
      lVar1 = plVar4[7];
      plVar4[7] = plVar8[7];
      plVar8[7] = lVar1;
      lVar1 = plVar4[8];
      plVar4[8] = plVar8[8];
      plVar8[8] = lVar1;
      plVar4[10] = 0;
      plVar4[0xb] = 0;
      plVar4[9] = 0;
      plVar4[9] = plVar8[9];
      plVar8[9] = 0;
      lVar1 = plVar4[10];
      plVar4[10] = plVar8[10];
      plVar8[10] = lVar1;
      lVar1 = plVar4[0xb];
      plVar4[0xb] = plVar8[0xb];
      plVar8[0xb] = lVar1;
      plVar8 = plVar8 + 0xc;
      plVar4 = plVar4 + 0xc;
joined_r0x006d8e64:
    } while (plVar11 + uVar5 * 3 != plVar8);
    lVar1 = uVar5 * 3 + 3;
    plVar8 = plVar9;
    while( true ) {
      plVar4 = plVar11;
      if (*plVar8 != 0) {
        HeapInterface::Free();
      }
      if (plVar4 == plVar9 + lVar1) break;
      plVar11 = plVar4 + 3;
      plVar8 = plVar4;
    }
    plVar9 = *(long **)(entry + 0x14);
    plVar11 = plVar2 + lVar1;
  }
  if (plVar9 != (long *)0x0) {
    HeapInterface::Free();
  }
  *(long **)(entry + 0x14) = plVar2;
  *(long **)(entry + 0x16) = plVar11;
  *(long **)(entry + 0x18) = plVar2 + 0x3c;
  return;
}


```
