## `FUN_00121a30` @ 00121a30
```c

ushort FUN_00121a30(long param_1)

{
  long lVar1;
  ushort uVar2;
  bool bVar3;
  
                    /* jag::Packet::gT<unsigned_short> (g2, big-endian). Reads 2 bytes at +0x10
                       cursor, byteswaps when host is little-endian (DAT_01050dc0 == 0x3020100).
                       Confirmed by inlined-pattern semantics. [VERIFIED rs2client.948-5
                       @0x00121a30] */
  lVar1 = *(long *)(param_1 + 0x18);
  bVar3 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_1 + 0x18) = lVar1 + 2;
  uVar2 = *(ushort *)(*(long *)(param_1 + 0x10) + lVar1);
  if (bVar3) {
    uVar2 = uVar2 << 8 | uVar2 >> 8;
  }
  return uVar2;
}


```
