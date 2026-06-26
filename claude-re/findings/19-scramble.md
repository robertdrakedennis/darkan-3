## `jag::Packet::gScrambledUint` @ 0047f3e0
```c

ulong jag::Packet::gScrambledUint(long param_1)

{
  char cVar1;
  char *pcVar2;
  long lVar3;
  long lVar4;
  ulong uVar5;
  
  pcVar2 = *(char **)(param_1 + 0x28);
  *(char **)(param_1 + 0x28) = pcVar2 + 1;
  cVar1 = *pcVar2;
  if (cVar1 == '\x01') {
    lVar3 = *(long *)(param_1 + 0x18);
    lVar4 = *(long *)(param_1 + 0x10);
    *(long *)(param_1 + 0x18) = lVar3 + 4;
    return (ulong)((uint)*(byte *)(lVar4 + 1 + lVar3) * 0x100 +
                   (uint)*(byte *)(lVar4 + 3 + lVar3) * 0x1000000 +
                   (uint)*(byte *)(lVar4 + 2 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + lVar3));
  }
  if (cVar1 == '\0') {
    uVar5 = FUN_00133390();
    return uVar5;
  }
  if (cVar1 == '\x02') {
    lVar3 = *(long *)(param_1 + 0x18);
    lVar4 = *(long *)(param_1 + 0x10);
    *(long *)(param_1 + 0x18) = lVar3 + 4;
    return (ulong)((uint)*(byte *)(lVar4 + lVar3) * 0x100 +
                   (uint)*(byte *)(lVar4 + 2 + lVar3) * 0x1000000 +
                   (uint)*(byte *)(lVar4 + 3 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + 1 + lVar3)
                  );
  }
  if (cVar1 == '\x03') {
    lVar3 = *(long *)(param_1 + 0x18);
    *(long *)(param_1 + 0x18) = lVar3 + 4;
    lVar4 = *(long *)(param_1 + 0x10);
    return (ulong)((uint)*(byte *)(lVar4 + 3 + lVar3) * 0x100 +
                   (uint)*(byte *)(lVar4 + 1 + lVar3) * 0x1000000 +
                   (uint)*(byte *)(lVar4 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + 2 + lVar3));
  }
  return 0;
}


```
## `jag::Packet::gScrambledMedium` @ 0047f4f0
```c

int jag::Packet::gScrambledMedium(long param_1)

{
  char cVar1;
  char *pcVar2;
  long lVar3;
  long lVar4;
  
  pcVar2 = *(char **)(param_1 + 0x28);
  *(char **)(param_1 + 0x28) = pcVar2 + 1;
  cVar1 = *pcVar2;
  if (cVar1 == '\x01') {
    lVar3 = *(long *)(param_1 + 0x18);
    lVar4 = *(long *)(param_1 + 0x10);
    *(long *)(param_1 + 0x18) = lVar3 + 3;
    return (uint)*(byte *)(lVar4 + 1 + lVar3) * 0x100 + (uint)*(byte *)(lVar4 + 2 + lVar3) * 0x10000
           + (uint)*(byte *)(lVar4 + lVar3);
  }
  if (cVar1 == '\0') {
    lVar3 = *(long *)(param_1 + 0x18);
    *(long *)(param_1 + 0x18) = lVar3 + 3;
    lVar4 = *(long *)(param_1 + 0x10);
    return (uint)*(byte *)(lVar4 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + 1 + lVar3) * 0x100 +
           (uint)*(byte *)(lVar4 + 2 + lVar3);
  }
  if (cVar1 == '\x02') {
    lVar3 = *(long *)(param_1 + 0x18);
    lVar4 = *(long *)(param_1 + 0x10);
    *(long *)(param_1 + 0x18) = lVar3 + 3;
    return (uint)*(byte *)(lVar4 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + 2 + lVar3) * 0x100 +
           (uint)*(byte *)(lVar4 + 1 + lVar3);
  }
  if (cVar1 == '\x03') {
    lVar3 = *(long *)(param_1 + 0x18);
    *(long *)(param_1 + 0x18) = lVar3 + 3;
    lVar4 = *(long *)(param_1 + 0x10);
    return (uint)*(byte *)(lVar4 + lVar3) * 0x100 + (uint)*(byte *)(lVar4 + 1 + lVar3) * 0x10000 +
           (uint)*(byte *)(lVar4 + 2 + lVar3);
  }
  return 0;
}


```
## `jag::Packet::gScrambledUshort` @ 0047f5f0
```c

/* Setting prototype: ulong gScrambledUshort(long packet) */

ulong jag::Packet::gScrambledUshort(long packet)

{
  char cVar1;
  char *pcVar2;
  long lVar3;
  ulong uVar4;
  
                    /* jag::Packet::gScrambledUshort -- 16-bit read with per-byte cipher transform.
                       cipher byte (packet+0x28, post-incremented): 0 -> raw BE ushort (delegates to
                       p2 helper at 0x00121a00), 1 -> LE order (b1<<8 | b0), 2 -> BE with low byte
                       +128, 3 -> LE with low byte +128. Ported 948-2-2 (0x0047f170) -> 948-5
                       (0x0047f5f0): exact structural match (body 0xc1 bytes), JMP-to-p2-helper
                       target shifted 0x00121880 -> 0x00121a00 (same +0x180 Packet block shift).
                       Note: a sibling with identical body may be gScrambledShort (signed variant).
                        */
  pcVar2 = *(char **)(packet + 0x28);
  *(char **)(packet + 0x28) = pcVar2 + 1;
  cVar1 = *pcVar2;
  if (cVar1 == '\x01') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 2;
    return (ulong)((uint)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar3) * 0x100 +
                  (uint)*(byte *)(*(long *)(packet + 0x10) + lVar3));
  }
  if (cVar1 == '\0') {
    uVar4 = FUN_00121a00();
    return uVar4;
  }
  if (cVar1 == '\x02') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 2;
    return (ulong)((uint)*(byte *)(*(long *)(packet + 0x10) + lVar3) * 0x100 +
                  (uint)(byte)(*(char *)(*(long *)(packet + 0x10) + 1 + lVar3) + 0x80));
  }
  if (cVar1 == '\x03') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 2;
    return (ulong)((uint)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar3) * 0x100 +
                  (uint)(byte)(*(char *)(*(long *)(packet + 0x10) + lVar3) + 0x80));
  }
  return 0;
}


```
## `jag::Packet::gScrambledUbyte` @ 0047f790
```c

/* Setting prototype: uint gScrambledUbyte(long packet) */

uint jag::Packet::gScrambledUbyte(long packet)

{
  char cVar1;
  char *pcVar2;
  long lVar3;
  
                    /* jag::Packet::gScrambledUbyte -- unsigned-byte read with per-byte cipher
                       transform. cipher byte (packet+0x28, post-incremented): 0 -> raw, 1 -> b-128,
                       2 -> -b, 3 -> 128-b. Discriminator vs gScrambledByte: 3-case uses MOV
                       EAX,0x80 (unsigned). Ported 948-2-2 (0x0047f310) -> 948-5 (0x0047f790): exact
                       structural match, body 0xa7 bytes, no relocated immediates. */
  pcVar2 = *(char **)(packet + 0x28);
  *(char **)(packet + 0x28) = pcVar2 + 1;
  cVar1 = *pcVar2;
  if (cVar1 == '\x01') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return *(byte *)(*(long *)(packet + 0x10) + lVar3) - 0x80;
  }
  if (cVar1 == '\0') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return (uint)*(byte *)(*(long *)(packet + 0x10) + lVar3);
  }
  if (cVar1 == '\x02') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return -(uint)*(byte *)(*(long *)(packet + 0x10) + lVar3);
  }
  if (cVar1 == '\x03') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return (uint)(byte)(0x80 - *(char *)(*(long *)(packet + 0x10) + lVar3));
  }
  return 0;
}


```
## `jag::Packet::gScrambledByte` @ 0047f840
```c

/* Setting prototype: ulong gScrambledByte(long packet) */

ulong jag::Packet::gScrambledByte(long packet)

{
  char cVar1;
  char *pcVar2;
  long lVar3;
  
                    /* jag::Packet::gScrambledByte -- signed-byte read with per-byte cipher
                       transform. cipher byte (packet+0x28, post-incremented): 0 -> raw, 1 -> b-128,
                       2 -> -b, 3 -> sign-extended via CONCAT71(0xffffff, -128-b). Discriminator vs
                       gScrambledUbyte: 3-case uses MOV EAX,0xffffff80 (signed). Ported 948-2-2
                       (0x0047f3c0) -> 948-5 (0x0047f840): exact structural match, body 0xa7 bytes,
                       no relocated immediates. */
  pcVar2 = *(char **)(packet + 0x28);
  *(char **)(packet + 0x28) = pcVar2 + 1;
  cVar1 = *pcVar2;
  if (cVar1 == '\x01') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return (ulong)(*(byte *)(*(long *)(packet + 0x10) + lVar3) - 0x80);
  }
  if (cVar1 == '\0') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return (ulong)*(byte *)(*(long *)(packet + 0x10) + lVar3);
  }
  if (cVar1 == '\x02') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return (ulong)-(uint)*(byte *)(*(long *)(packet + 0x10) + lVar3);
  }
  if (cVar1 == '\x03') {
    lVar3 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar3 + 1;
    return CONCAT71(0xffffff,-0x80 - *(char *)(*(long *)(packet + 0x10) + lVar3));
  }
  return 0;
}


```
