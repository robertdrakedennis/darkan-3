# S2C dispatch search

binders=6 handler-table slots=679

- binder `jag::ServerProt::BindHandlers` @ 0007509a
- binder `jag::packethandlers::ClientState::BindHandlers` @ 000aa85e
- binder `jag::packethandlers::ClientState::BindHandlers_extra` @ 000aaf4c
- binder `jag::packethandlers::PlayerList::BindHandlers` @ 000ab3ea
- binder `jag::packethandlers::Interfaces::BindHandlers` @ 000ab888
- binder `jag::packethandlers::ZoneUpdates::BindHandlers` @ 000ae1a8

## top reader functions (by #handler-slots referenced) — dispatch candidates

- 248 slots: `jag::ServerProt::RegisterAll` @ 000c4700
- 87 slots: `FUN_000b0a80` @ 000b0a80
- 60 slots: `FUN_000c8ef0` @ 000c8ef0
- 40 slots: `FUN_000e7620` @ 000e7620
- 25 slots: `FUN_000b0080` @ 000b0080
- 21 slots: `jag::GlobalRSAKeys_Init` @ 000e8f90
- 19 slots: `FUN_000b39a0` @ 000b39a0
- 19 slots: `FUN_000ea080` @ 000ea080
- 19 slots: `FUN_000d7c10` @ 000d7c10
- 18 slots: `FUN_000bfa10` @ 000bfa10
- 18 slots: `FUN_000cd820` @ 000cd820
- 18 slots: `FUN_000b21f0` @ 000b21f0
- 18 slots: `FUN_000e2a90` @ 000e2a90
- 17 slots: `FUN_000c2b90` @ 000c2b90
- 17 slots: `FUN_000b4280` @ 000b4280
- 17 slots: `FUN_000e8720` @ 000e8720
- 16 slots: `FUN_000e9cc0` @ 000e9cc0
- 15 slots: `FUN_000b4a80` @ 000b4a80
- 15 slots: `FUN_0015abf0` @ 0015abf0
- 15 slots: `FUN_000cb2b0` @ 000cb2b0
- 15 slots: `FUN_000b2a10` @ 000b2a10
- 15 slots: `FUN_000b3270` @ 000b3270
- 15 slots: `FUN_0015b200` @ 0015b200
- 15 slots: `FUN_002287f0` @ 002287f0
- 15 slots: `FUN_000cdf40` @ 000cdf40

## `jag::ServerProt::RegisterAll` @ 000c4700 (248 slots)
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void jag::ServerProt::RegisterAll(void)

{
  undefined *puVar1;
  uint uVar2;
  char *pcVar3;
  undefined8 local_44;
  undefined4 local_3c;
  undefined8 local_38;
  undefined4 local_30;
  undefined8 local_2c;
  undefined4 local_24;
  undefined8 local_20;
  undefined4 local_18;
  
  if (DAT_015da800 == '\0') {
    DAT_015da800 = '\x01';
    _DAT_015da7f0 = 0;
    DAT_015da7f8 = 0x3f800000;
  }
  if (DAT_015da7e8 == '\0') {
    DAT_015da7e8 = '\x01';
    DAT_015c02b0 = 0;
    DAT_015c02a8 = 0x3f80000000000000;
  }
  std__ios_base__Init__Init(&DAT_013a1d10);
  __cxa_atexit(FUN_00c69340,&DAT_013a1d10,&PTR_LOOP_01391000);
  puVar1 = PTR_DAT_0136c108;
  if (DAT_015da7d8 == '\0') {
    DAT_015da7d8 = '\x01';
    _DAT_015df2d0 = (undefined1  [16])0x0;
  }
  if (DAT_015da7d0 == '\0') {
    DAT_015da7d0 = '\x01';
    DAT_015da7c8 = 0;
  }
  _DAT_015d4660 = &DAT_015d4668;
  DAT_015d4650 = &DAT_015d4d38;
  DAT_015d4640 = &DAT_015d4668;
  DAT_015d4648 = &DAT_015d4668;
  __cxa_atexit(FUN_004bdd90,&DAT_015d4640,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1ce0,0,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1ce0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1ca0,1,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1ca0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1c60,2,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1c60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0c00,3,0x13);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0c00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0b00,4,0x20);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0b00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1700,5,0);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1700,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1e40,6,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1e40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1c20,7,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1c20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0440,8,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0440,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1be0,9,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1be0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1600,10,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1600,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1ba0,0xb,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1ba0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0e80,0xc,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0e80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0ec0,0xd,1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0ec0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0580,0xe,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0580,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1b60,0xf,0xb);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1b60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1ec0,0x10,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1ec0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0f00,0x11,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0f00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1b20,0x12,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1b20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1ae0,0x13,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1ae0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1aa0,0x14,3);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1aa0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1a40,0x15,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1a40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0f40,0x16,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0f40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1a60,0x17,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1a60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1300,0x18,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1300,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1a20,0x19,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1a20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a19e0,0x1a,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a19e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a19a0,0x1b,10);
  __cxa_atexit(FUN_004bddc0,&DAT_013a19a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1680,0x1c,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1680,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1960,0x1d,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1960,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0900,0x1e,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0900,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1920,0x1f,6);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1920,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0740,0x20,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0740,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a18e0,0x21,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a18e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a18a0,0x22,8);
  __cxa_atexit(FUN_004bddc0,&DAT_013a18a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0a00,0x23,0xc);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0a00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1860,0x24,10);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1860,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1820,0x25,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1820,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c08c0,0x26,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c08c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0c40,0x27,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0c40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0a40,0x28,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0a40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c2080,0x29,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c2080,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a17e0,0x2a,10);
  __cxa_atexit(FUN_004bddc0,&DAT_013a17e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a17a0,0x2b,0xc);
  __cxa_atexit(FUN_004bddc0,&DAT_013a17a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c12c0,0x2c,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c12c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1760,0x2d,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1760,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1c40,0x2e,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1c40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1580,0x2f,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1580,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c14c0,0x30,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c14c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1720,0x31,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1720,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1f40,0x32,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1f40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c15c0,0x33,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c15c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a16e0,0x34,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a16e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a16a0,0x35,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a16a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1660,0x36,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1660,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1380,0x37,0);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1380,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1620,0x38,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1620,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a15e0,0x39,6);
  __cxa_atexit(FUN_004bddc0,&DAT_013a15e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1340,0x3a,0);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1340,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0600,0x3b,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0600,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0b40,0x3c,0x19);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0b40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c16c0,0x3d,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c16c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0a80,0x3e,4);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0a80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a15a0,0x3f,10);
  __cxa_atexit(FUN_004bddc0,&DAT_013a15a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1540,0x40,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1540,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1dc0,0x41,0x14);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1dc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1560,0x42,5);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1560,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1520,0x43,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1520,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c03c0,0x44,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c03c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1480,0x45,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1480,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0b80,0x46,0x19);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0b80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1ac0,0x47,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1ac0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a14e0,0x48,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a14e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a14a0,0x49,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a14a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0f80,0x4a,4);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0f80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1460,0x4b,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1460,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c2000,0x4c,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c2000,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1420,0x4d,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1420,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c2040,0x4e,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c2040,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a13e0,0x4f,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a13e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a13a0,0x50,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a13a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1360,0x51,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1360,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0bc0,0x52,0x17);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0bc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1320,0x53,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1320,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0880,0x54,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0880,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a12e0,0x55,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a12e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c06c0,0x56,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c06c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a12a0,0x57,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a12a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1260,0x58,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1260,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1220,0x59,0x13);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1220,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1fc0,0x5a,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1fc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0940,0x5b,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0940,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1440,0x5c,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1440,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a11e0,0x5d,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a11e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0c80,0x5e,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0c80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a11a0,0x5f,5);
  __cxa_atexit(FUN_004bddc0,&DAT_013a11a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0700,0x60,4);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0700,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c09c0,0x61,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c09c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1160,0x62,0x19);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1160,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0540,99,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0540,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1120,100,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1120,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0840,0x65,4);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0840,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c07c0,0x66,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c07c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0780,0x67,8);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0780,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a10e0,0x68,0xe);
  __cxa_atexit(FUN_004bddc0,&DAT_013a10e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a10a0,0x69,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a10a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1060,0x6a,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1060,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1bc0,0x6b,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1bc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a1020,0x6c,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a1020,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0e40,0x6d,0x1c);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0e40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0fe0,0x6e,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0fe0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0fa0,0x6f,6);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0fa0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0f60,0x70,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0f60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1cc0,0x71,0xb);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1cc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0f20,0x72,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0f20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0680,0x73,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0680,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1400,0x74,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1400,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ee0,0x75,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ee0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0ac0,0x76,0x1d);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0ac0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ea0,0x77,0x23);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ea0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0e60,0x78,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0e60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0e20,0x79,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0e20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0980,0x7a,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0980,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0400,0x7b,0);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0400,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0de0,0x7c,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0de0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1b40,0x7d,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1b40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0da0,0x7e,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0da0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0d60,0x7f,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0d60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0d20,0x80,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0d20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ce0,0x81,3);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ce0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ca0,0x82,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ca0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1000,0x83,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1000,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0c60,0x84,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0c60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0c20,0x85,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0c20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0be0,0x86,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0be0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ba0,0x87,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ba0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0640,0x88,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0640,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0b60,0x89,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0b60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0b20,0x8a,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0b20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0ae0,0x8b,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0ae0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0aa0,0x8c,0x11);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0aa0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0a60,0x8d,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0a60,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0a20,0x8e,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0a20,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1180,0x8f,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1180,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1140,0x90,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1140,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a09e0,0x91,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a09e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1040,0x92,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1040,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1640,0x93,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1640,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0380,0x94,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0380,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1080,0x95,6);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1080,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a09a0,0x96,3);
  __cxa_atexit(FUN_004bddc0,&DAT_013a09a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1d40,0x97,0x15);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1d40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0960,0x98,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0960,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0920,0x99,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0920,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a08e0,0x9a,5);
  __cxa_atexit(FUN_004bddc0,&DAT_013a08e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a08a0,0x9b,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a08a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0860,0x9c,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0860,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0820,0x9d,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0820,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c04c0,0x9e,9);
  __cxa_atexit(FUN_004bddc0,&DAT_015c04c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a07e0,0x9f,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a07e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a07a0,0xa0,9);
  __cxa_atexit(FUN_004bddc0,&DAT_013a07a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0760,0xa1,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0760,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0720,0xa2,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0720,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a06e0,0xa3,0xf);
  __cxa_atexit(FUN_004bddc0,&DAT_013a06e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1880,0xa4,0x1c);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1880,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c05c0,0xa5,0xe);
  __cxa_atexit(FUN_004bddc0,&DAT_015c05c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1200,0xa6,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1200,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a06a0,0xa7,0xc);
  __cxa_atexit(FUN_004bddc0,&DAT_013a06a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1900,0xa8,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1900,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0660,0xa9,8);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0660,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1980,0xaa,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1980,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0fc0,0xab,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0fc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0dc0,0xac,1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0dc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0620,0xad,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0620,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a05e0,0xae,8);
  __cxa_atexit(FUN_004bddc0,&DAT_013a05e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a05a0,0xaf,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a05a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0e00,0xb0,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0e00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1800,0xb1,0x1d);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1800,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0d40,0xb2,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0d40,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0500,0xb3,9);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0500,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0800,0xb4,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0800,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0560,0xb5,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0560,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0520,0xb6,3);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0520,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1780,0xb7,0xe);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1780,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0cc0,0xb8,4);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0cc0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a04e0,0xb9,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a04e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1240,0xba,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1240,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a04a0,0xbb,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a04a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0460,0xbc,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0460,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0420,0xbd,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0420,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c13c0,0xbe,0);
  __cxa_atexit(FUN_004bddc0,&DAT_015c13c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a03e0,0xbf,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a03e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a03a0,0xc0,1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a03a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0d00,0xc1,1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0d00,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c11c0,0xc2,1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c11c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0360,0xc3,4);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0360,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1500,0xc4,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1500,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0320,0xc5,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0320,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a02e0,0xc6,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a02e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1280,199,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1280,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a02a0,200,2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a02a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c1100,0xc9,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1100,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0260,0xca,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0260,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0220,0xcb,3);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0220,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0d80,0xcc,1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0d80,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a01e0,0xcd,6);
  __cxa_atexit(FUN_004bddc0,&DAT_013a01e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c0480,0xce,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c0480,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_015c10c0,0xcf,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c10c0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a01a0,0xd0,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a01a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0160,0xd1,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0160,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0120,0xd2,6);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0120,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a00e0,0xd3,0x21);
  __cxa_atexit(FUN_004bddc0,&DAT_013a00e0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a00a0,0xd4,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a00a0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0060,0xd5,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0060,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_013a0020,0xd6,0);
  __cxa_atexit(FUN_004bddc0,&DAT_013a0020,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_0139ffe0,0xd7,8);
  __cxa_atexit(FUN_004bddc0,&DAT_0139ffe0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_0139ffa0,0xd8,-2);
  __cxa_atexit(FUN_004bddc0,&DAT_0139ffa0,&PTR_LOOP_01391000);
  InitEntry((undefined4 *)&DAT_0139ff60,0xd9,4);
  __cxa_atexit(FUN_004bddc0,&DAT_0139ff60,&PTR_LOOP_01391000);
  _DAT_015d45a0 = &DAT_015d45a8;
  DAT_015d4590 = 0x15d4638;
  DAT_015d4580 = puVar1;
  DAT_015d4588 = puVar1;
  __cxa_atexit(FUN_004bdd60,&DAT_015d4580,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1f80,0,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1f80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c17c0,1,0x1d);
  __cxa_atexit(FUN_004bddc0,&DAT_015c17c0,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1e80,2,2);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1e80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1a80,3,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1a80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c18c0,4,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c18c0,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1b80,5,3);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1b80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1740,6,0xe);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1740,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1d80,7,0x14);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1d80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1b00,8,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1b00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1c00,9,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1c00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1f00,10,-1);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1f00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1e00,0xb,7);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1e00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1940,0xc,5);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1940,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c19c0,0xd,0xb);
  __cxa_atexit(FUN_004bddc0,&DAT_015c19c0,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1c80,0xe,0xb);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1c80,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1d00,0xf,0x15);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1d00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1a00,0x10,10);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1a00,&PTR_LOOP_01391000);
  InitSubEntry((undefined4 *)&DAT_015c1840,0x11,0x1c);
  __cxa_atexit(FUN_004bddc0,&DAT_015c1840,&PTR_LOOP_01391000);
  local_2c = 0x3f80000000000000;
  local_44 = 0;
  local_3c = 0;
  local_38 = 0x3f800000;
  local_30 = 0;
  local_24 = 0;
  local_20 = 0;
  local_18 = 0xbf800000;
  FUN_0048f390(&DAT_0139ff20,&local_38,&local_44);
  if (DAT_015da6d0 == '\0') {
    DAT_015da6d0 = '\x01';
    _DAT_015da690 = 0;
    _DAT_015da698 = 0;
    _DAT_015da6a0 = 0;
    _DAT_015da6a8 = 0;
    _DAT_015da6b0 = 0;
    _DAT_015da6b8 = 0;
    _DAT_015da6c0 = 0;
    _DAT_015da6c8 = 0;
  }
  if (DAT_015da7b8 == '\0') {
    DAT_015da7b8 = '\x01';
    DAT_015c01c8 = 0;
    DAT_015c01d0 = 0;
  }
  if (DAT_015da680 == '\0') {
    DAT_015da680 = '\x01';
    DAT_015da678 = 0x3f800000;
    DAT_015da670 = 0x3f8000003f800000;
  }
  if (DAT_015da798 == '\0') {
    DAT_015da798 = '\x01';
    DAT_015da790 = 0x3f800000;
    DAT_015da788 = 0x3f8000003f800000;
  }
  if (DAT_015da720 == '\0') {
    DAT_015da720 = '\x01';
    _DAT_015bf230 = 0x3f800000;
    _DAT_015bf238 = 0;
    _DAT_015bf240 = 0x3f80000000000000;
    _DAT_015bf248 = 0;
    _DAT_015bf250 = 0;
    _DAT_015bf258 = 0x3f800000;
    _DAT_015bf260 = 0;
    _DAT_015bf268 = 0x3f80000000000000;
  }
  if (DAT_015da668 == '\0') {
    DAT_015da668 = '\x01';
    _DAT_015df148 = 0;
  }
  if (DAT_015da780 == '\0') {
    pcVar3 = "LUMINANCE_ADAPTED";
    DAT_015da780 = '\x01';
    DAT_015da778 = 0x811c9dc5;
    uVar2 = 0x4c;
    DAT_015da770 = "LUMINANCE_ADAPTED";
    do {
      pcVar3 = pcVar3 + 1;
      DAT_015da778 = (ulong)((int)DAT_015da778 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar3;
    } while (*pcVar3 != 0);
  }
  if (DAT_015da760 == '\0') {
    pcVar3 = "LUMINANCE_ADAPTED_PREVIOUS";
    DAT_015da760 = '\x01';
    DAT_015da758 = 0x811c9dc5;
    uVar2 = 0x4c;
    DAT_015da750 = "LUMINANCE_ADAPTED_PREVIOUS";
    do {
      pcVar3 = pcVar3 + 1;
      DAT_015da758 = (ulong)((int)DAT_015da758 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar3;
    } while (*pcVar3 != 0);
  }
  if (DAT_015da740 == '\0') {
    pcVar3 = "VIEWRT_LUMINANCE_AVERAGE";
    DAT_015da740 = '\x01';
    DAT_015da738 = 0x811c9dc5;
    uVar2 = 0x56;
    DAT_015da730 = "VIEWRT_LUMINANCE_AVERAGE";
    do {
      pcVar3 = pcVar3 + 1;
      DAT_015da738 = (ulong)((int)DAT_015da738 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar3;
    } while (*pcVar3 != 0);
  }
  _DAT_013a1d20 = (undefined1  [16])0x0;
  __cxa_atexit(FUN_009e8110,&DAT_013a1d20,&PTR_LOOP_01391000);
  if (DAT_015da160 == '\0') {
    DAT_015da160 = '\x01';
    __cxa_atexit(FUN_004b2370,&DAT_015da158,&PTR_LOOP_01391000);
  }
  if (DAT_015da150 == '\0') {
    DAT_015da150 = '\x01';
    __cxa_atexit(FUN_004773c0,&DAT_015bf3e8,&PTR_LOOP_01391000);
  }
  if (DAT_015da130 == '\0') {
    DAT_015da130 = '\x01';
    __cxa_atexit(FUN_00477380,&DAT_015da128,&PTR_LOOP_01391000);
  }
  if (DAT_015da148 == '\0') {
    DAT_015da148 = '\x01';
    __cxa_atexit(FUN_004abd70,&DAT_015df4f8,&PTR_LOOP_01391000);
  }
  if (DAT_015da120 == '\0') {
    DAT_015da120 = '\x01';
    __cxa_atexit(FUN_0047ce80,&DAT_015da118,&PTR_LOOP_01391000);
  }
  if (DAT_015da048 == '\0') {
    DAT_015da048 = '\x01';
    DAT_015c7b30 = 0;
    DAT_015c7b38 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c7b30,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fa0 == '\0') {
    DAT_013a2fa0 = '\x01';
    DAT_015c8540 = 0;
    DAT_015c8548 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8540,&PTR_LOOP_01391000);
  }
  if (DAT_015da168 == '\0') {
    DAT_015da168 = '\x01';
    DAT_015c7b20 = 0;
    DAT_015c7b28 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c7b20,&PTR_LOOP_01391000);
  }
  return;
}


```

## `FUN_000b0a80` @ 000b0a80 (87 slots)
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_000b0a80(void)

{
  char *pcVar1;
  uint uVar2;
  undefined8 local_44;
  undefined4 local_3c;
  undefined8 local_38;
  undefined4 local_30;
  undefined8 local_2c;
  undefined4 local_24;
  undefined8 local_20;
  undefined4 local_18;
  
  if (DAT_015da800 == '\0') {
    DAT_015da800 = '\x01';
    _DAT_015da7f0 = 0;
    DAT_015da7f8 = 0x3f800000;
  }
  if (DAT_015da7e8 == '\0') {
    DAT_015da7e8 = '\x01';
    DAT_015c02b0 = 0;
    DAT_015c02a8 = 0x3f80000000000000;
  }
  std__ios_base__Init__Init(&DAT_01395660);
  __cxa_atexit(FUN_00c69340,&DAT_01395660,&PTR_LOOP_01391000);
  if (DAT_015da7d8 == '\0') {
    DAT_015da7d8 = '\x01';
    _DAT_015df2d0 = (undefined1  [16])0x0;
  }
  if (DAT_015da7d0 == '\0') {
    DAT_015da7d0 = '\x01';
    DAT_015da7c8 = 0;
  }
  if (DAT_015da2c8 == '\0') {
    DAT_015da2c8 = '\x01';
    _DAT_015da2c0 = 0xffffffff00000000;
  }
  if (DAT_015da2b8 == '\0') {
    DAT_015da2b8 = '\x01';
    DAT_015db690 = 0;
    DAT_015db698 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015db690,&PTR_LOOP_01391000);
  }
  if (DAT_015da2b0 == '\0') {
    DAT_015da2b0 = '\x01';
    _DAT_015da2a0 = 0;
    _DAT_015da2a8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da2a0,&PTR_LOOP_01391000);
  }
  if (DAT_015da668 == '\0') {
    DAT_015da668 = '\x01';
    _DAT_015df148 = 0;
  }
  if (DAT_015da7b8 == '\0') {
    DAT_015da7b8 = '\x01';
    DAT_015c01c8 = 0;
    DAT_015c01d0 = 0;
  }
  if (DAT_015da720 == '\0') {
    DAT_015da720 = '\x01';
    _DAT_015bf230 = 0x3f800000;
    _DAT_015bf238 = 0;
    _DAT_015bf240 = 0x3f80000000000000;
    _DAT_015bf248 = 0;
    _DAT_015bf250 = 0;
    _DAT_015bf258 = 0x3f800000;
    _DAT_015bf260 = 0;
    _DAT_015bf268 = 0x3f80000000000000;
  }
  if (DAT_015da798 == '\0') {
    DAT_015da798 = '\x01';
    DAT_015da790 = 0x3f800000;
    DAT_015da788 = 0x3f8000003f800000;
  }
  if (DAT_015da780 == '\0') {
    pcVar1 = "LUMINANCE_ADAPTED";
    DAT_015da780 = '\x01';
    DAT_015da778 = 0x811c9dc5;
    uVar2 = 0x4c;
    DAT_015da770 = "LUMINANCE_ADAPTED";
    do {
      pcVar1 = pcVar1 + 1;
      DAT_015da778 = (ulong)((int)DAT_015da778 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar1;
    } while (*pcVar1 != 0);
  }
  if (DAT_015da760 == '\0') {
    pcVar1 = "LUMINANCE_ADAPTED_PREVIOUS";
    DAT_015da760 = '\x01';
    DAT_015da758 = 0x811c9dc5;
    uVar2 = 0x4c;
    DAT_015da750 = "LUMINANCE_ADAPTED_PREVIOUS";
    do {
      pcVar1 = pcVar1 + 1;
      DAT_015da758 = (ulong)((int)DAT_015da758 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar1;
    } while (*pcVar1 != 0);
  }
  if (DAT_015da740 == '\0') {
    pcVar1 = "VIEWRT_LUMINANCE_AVERAGE";
    DAT_015da740 = '\x01';
    DAT_015da738 = 0x811c9dc5;
    uVar2 = 0x56;
    DAT_015da730 = "VIEWRT_LUMINANCE_AVERAGE";
    do {
      pcVar1 = pcVar1 + 1;
      DAT_015da738 = (ulong)((int)DAT_015da738 * 0x1000193 ^ uVar2);
      uVar2 = (uint)(byte)*pcVar1;
    } while (*pcVar1 != 0);
  }
  if (DAT_015da7c0 == '\0') {
    DAT_015da7c0 = '\x01';
    _DAT_015da8d0 = 0;
    _DAT_015da8d8 = 0;
    DAT_015da8e0 = 0;
  }
  if (DAT_015da7b0 == '\0') {
    DAT_015da7b0 = '\x01';
    _DAT_015da7a0 = 0x3f800000;
    DAT_015da7a8 = 0;
  }
  local_2c = 0x3f80000000000000;
  local_44 = 0;
  local_3c = 0;
  local_38 = 0x3f800000;
  local_30 = 0;
  local_24 = 0;
  local_20 = 0;
  local_18 = 0xbf800000;
  FUN_0048f390(&DAT_01395620,&local_38,&local_44);
  if (DAT_015da6d0 == '\0') {
    DAT_015da6d0 = '\x01';
    _DAT_015da690 = 0;
    _DAT_015da698 = 0;
    _DAT_015da6a0 = 0;
    _DAT_015da6a8 = 0;
    _DAT_015da6b0 = 0;
    _DAT_015da6b8 = 0;
    _DAT_015da6c0 = 0;
    _DAT_015da6c8 = 0;
  }
  if (DAT_015da680 == '\0') {
    DAT_015da680 = '\x01';
    DAT_015da678 = 0x3f800000;
    DAT_015da670 = 0x3f8000003f800000;
  }
  if (DAT_015da660 == '\0') {
    DAT_015da660 = '\x01';
    _DAT_015da650 = _DAT_00cb6c10;
    _DAT_015da658 = _UNK_00cb6c18;
  }
  DAT_013955f0 = 0;
  _DAT_01395600 = 2;
  _DAT_01395604 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_013955e0,&PTR_LOOP_01391000);
  DAT_013955b0 = 0;
  _DAT_013955c0 = 3;
  _DAT_013955c4 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_013955a0,&PTR_LOOP_01391000);
  DAT_01395570 = 0;
  _DAT_01395580 = 4;
  _DAT_01395584 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_01395560,&PTR_LOOP_01391000);
  DAT_01395530 = 0;
  _DAT_01395540 = 5;
  _DAT_01395544 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_01395520,&PTR_LOOP_01391000);
  DAT_013954f0 = 0;
  _DAT_01395500 = 6;
  _DAT_01395504 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_013954e0,&PTR_LOOP_01391000);
  DAT_013954b0 = 0;
  _DAT_013954c0 = 8;
  _DAT_013954c4 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_013954a0,&PTR_LOOP_01391000);
  _DAT_01395470 = 0;
  _DAT_01395480 = 9;
  _DAT_01395484 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01395460,&PTR_LOOP_01391000);
  _DAT_01395430 = 0;
  _DAT_01395440 = 10;
  _DAT_01395444 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01395420,&PTR_LOOP_01391000);
  _DAT_013953f0 = 0;
  _DAT_01395400 = 0xb;
  _DAT_01395404 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_013953e0,&PTR_LOOP_01391000);
  _DAT_013953b0 = 0;
  _DAT_013953c0 = 0xc;
  _DAT_013953c4 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_013953a0,&PTR_LOOP_01391000);
  _DAT_01395370 = 0;
  _DAT_01395380 = 0xd;
  _DAT_01395384 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01395360,&PTR_LOOP_01391000);
  DAT_01395330 = 0;
  _DAT_01395340 = 0xf;
  _DAT_01395344 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01395320,&PTR_LOOP_01391000);
  DAT_013952f0 = 0;
  _DAT_01395300 = 0x10;
  _DAT_01395304 = 8;
  __cxa_atexit(FUN_00170f10,&DAT_013952e0,&PTR_LOOP_01391000);
  DAT_013952b0 = 0;
  _DAT_013952c0 = 0x11;
  _DAT_013952c4 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_013952a0,&PTR_LOOP_01391000);
  DAT_01395270 = 0;
  _DAT_01395280 = 0x12;
  _DAT_01395284 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_01395260,&PTR_LOOP_01391000);
  DAT_01395230 = 0;
  _DAT_01395240 = 0x13;
  _DAT_01395244 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_01395220,&PTR_LOOP_01391000);
  DAT_013951f0 = 0;
  _DAT_01395200 = 0x14;
  _DAT_01395204 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_013951e0,&PTR_LOOP_01391000);
  DAT_013951b0 = 0;
  _DAT_013951c0 = 0x15;
  _DAT_013951c4 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_013951a0,&PTR_LOOP_01391000);
  DAT_01395170 = 0;
  _DAT_01395180 = 0x16;
  _DAT_01395184 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_01395160,&PTR_LOOP_01391000);
  DAT_01395710 = 0;
  _DAT_01395720 = 0x17;
  _DAT_01395724 = 0;
  __cxa_atexit(FUN_00170f10,&DAT_01395700,&PTR_LOOP_01391000);
  DAT_013962d0 = 0;
  _DAT_013962e0 = 0x19;
  _DAT_013962e4 = 1;
  __cxa_atexit(FUN_00170f10,&DAT_013962c0,&PTR_LOOP_01391000);
  DAT_01395130 = 0;
  _DAT_01395140 = 0x1e;
  _DAT_01395144 = 1;
  __cxa_atexit(FUN_00170f10,&DAT_01395120,&PTR_LOOP_01391000);
  DAT_013950f0 = 0;
  _DAT_01395100 = 0x2c;
  _DAT_01395104 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013950e0,&PTR_LOOP_01391000);
  DAT_013950b0 = 0;
  _DAT_013950c0 = 0x2d;
  _DAT_013950c4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013950a0,&PTR_LOOP_01391000);
  DAT_01395070 = 0;
  _DAT_01395080 = 0x2e;
  _DAT_01395084 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01395060,&PTR_LOOP_01391000);
  DAT_01395030 = 0;
  _DAT_01395040 = 0x2f;
  _DAT_01395044 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01395020,&PTR_LOOP_01391000);
  DAT_01394ff0 = 0;
  _DAT_01395000 = 0x30;
  _DAT_01395004 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394fe0,&PTR_LOOP_01391000);
  DAT_01394fb0 = 0;
  _DAT_01394fc0 = 0x31;
  _DAT_01394fc4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394fa0,&PTR_LOOP_01391000);
  DAT_01394f70 = 0;
  _DAT_01394f80 = 0x32;
  _DAT_01394f84 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394f60,&PTR_LOOP_01391000);
  DAT_01394f30 = 0;
  _DAT_01394f40 = 0x33;
  _DAT_01394f44 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394f20,&PTR_LOOP_01391000);
  DAT_01394ef0 = 0;
  _DAT_01394f00 = 0x34;
  _DAT_01394f04 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394ee0,&PTR_LOOP_01391000);
  DAT_01394eb0 = 0;
  _DAT_01394ec0 = 0x35;
  _DAT_01394ec4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394ea0,&PTR_LOOP_01391000);
  DAT_01394e70 = 0;
  _DAT_01394e80 = 0x39;
  _DAT_01394e84 = 1;
  __cxa_atexit(FUN_00170f10,&DAT_01394e60,&PTR_LOOP_01391000);
  DAT_01394e30 = 0;
  _DAT_01394e40 = 0x3a;
  _DAT_01394e44 = 1;
  __cxa_atexit(FUN_00170f10,&DAT_01394e20,&PTR_LOOP_01391000);
  DAT_013957f0 = 0;
  _DAT_01395800 = 0x3b;
  _DAT_01395804 = 0;
  __cxa_atexit(FUN_00170f10,&DAT_013957e0,&PTR_LOOP_01391000);
  DAT_013956d0 = 0;
  _DAT_013956e0 = 0x3c;
  _DAT_013956e4 = 0;
  __cxa_atexit(FUN_00170f10,&DAT_013956c0,&PTR_LOOP_01391000);
  DAT_01394df0 = 0;
  _DAT_01394e00 = 0x3e9;
  _DAT_01394e04 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_01394de0,&PTR_LOOP_01391000);
  DAT_01394db0 = 0;
  _DAT_01394dc0 = 0x3ea;
  _DAT_01394dc4 = 3;
  __cxa_atexit(FUN_00170f10,&DAT_01394da0,&PTR_LOOP_01391000);
  _DAT_01394d70 = 0;
  _DAT_01394d80 = 0x3eb;
  _DAT_01394d84 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394d60,&PTR_LOOP_01391000);
  DAT_01394d30 = 0;
  _DAT_01394d40 = 0x3ec;
  _DAT_01394d44 = 2;
  __cxa_atexit(FUN_00170f10,&DAT_01394d20,&PTR_LOOP_01391000);
  DAT_01394cf0 = 0;
  _DAT_01394d00 = 0x3ed;
  _DAT_01394d04 = 0;
  __cxa_atexit(FUN_00170f10,&DAT_01394ce0,&PTR_LOOP_01391000);
  DAT_01395690 = 0;
  _DAT_013956a0 = 0x3ee;
  _DAT_013956a4 = 0;
  __cxa_atexit(FUN_00170f10,&DAT_01395680,&PTR_LOOP_01391000);
  _DAT_01394cb0 = 0;
  _DAT_01394cc0 = 0x3ef;
  _DAT_01394cc4 = 1;
  __cxa_atexit(FUN_00170f10,&DAT_01394ca0,&PTR_LOOP_01391000);
  DAT_01395830 = 0;
  _DAT_01395840 = 0x3f0;
  _DAT_01395844 = 9;
  __cxa_atexit(FUN_00170f10,&DAT_01395820,&PTR_LOOP_01391000);
  DAT_01395870 = 0;
  _DAT_01395880 = 0x3f1;
  _DAT_01395884 = 9;
  __cxa_atexit(FUN_00170f10,&DAT_01395860,&PTR_LOOP_01391000);
  DAT_013958b0 = 0;
  _DAT_013958c0 = 0x3f2;
  _DAT_013958c4 = 9;
  __cxa_atexit(FUN_00170f10,&DAT_013958a0,&PTR_LOOP_01391000);
  DAT_013958f0 = 0;
  _DAT_01395900 = 0x3f3;
  _DAT_01395904 = 9;
  __cxa_atexit(FUN_00170f10,&DAT_013958e0,&PTR_LOOP_01391000);
  DAT_01395930 = 0;
  _DAT_01395940 = 0x3f4;
  _DAT_01395944 = 9;
  __cxa_atexit(FUN_00170f10,&DAT_01395920,&PTR_LOOP_01391000);
  DAT_01394c70 = 0;
  _DAT_01394c80 = 0x7d9;
  _DAT_01394c84 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394c60,&PTR_LOOP_01391000);
  DAT_01394c30 = 0;
  _DAT_01394c40 = 0x7da;
  _DAT_01394c44 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394c20,&PTR_LOOP_01391000);
  DAT_01394bf0 = 0;
  _DAT_01394c00 = 0x7db;
  _DAT_01394c04 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394be0,&PTR_LOOP_01391000);
  DAT_01394bb0 = 0;
  _DAT_01394bc0 = 0x7dc;
  _DAT_01394bc4 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394ba0,&PTR_LOOP_01391000);
  DAT_01394b70 = 0;
  _DAT_01394b80 = 0x7dd;
  _DAT_01394b84 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394b60,&PTR_LOOP_01391000);
  DAT_01394b30 = 0;
  _DAT_01394b40 = 0xbbb;
  _DAT_01394b44 = 4;
  __cxa_atexit(FUN_00170f10,&DAT_01394b20,&PTR_LOOP_01391000);
  _DAT_01394af0 = 0;
  _DAT_01394b00 = 0x7fc;
  _DAT_01394b04 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394ae0,&PTR_LOOP_01391000);
  _DAT_01394ab0 = 0;
  _DAT_01394ac0 = 0x7fd;
  _DAT_01394ac4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394aa0,&PTR_LOOP_01391000);
  _DAT_01394a70 = 0;
  _DAT_01394a80 = 0x7fe;
  _DAT_01394a84 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394a60,&PTR_LOOP_01391000);
  _DAT_01394a30 = 0;
  _DAT_01394a40 = 0x7ff;
  _DAT_01394a44 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394a20,&PTR_LOOP_01391000);
  _DAT_013949f0 = 0;
  _DAT_01394a00 = 0x800;
  _DAT_01394a04 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013949e0,&PTR_LOOP_01391000);
  _DAT_013949b0 = 0;
  _DAT_013949c0 = 0x801;
  _DAT_013949c4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013949a0,&PTR_LOOP_01391000);
  _DAT_01394970 = 0;
  _DAT_01394980 = 0x802;
  _DAT_01394984 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394960,&PTR_LOOP_01391000);
  _DAT_01394930 = 0;
  _DAT_01394940 = 0x803;
  _DAT_01394944 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_01394920,&PTR_LOOP_01391000);
  _DAT_013948f0 = 0;
  _DAT_01394900 = 0x804;
  _DAT_01394904 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013948e0,&PTR_LOOP_01391000);
  _DAT_013948b0 = 0;
  _DAT_013948c0 = 0x805;
  _DAT_013948c4 = 7;
  __cxa_atexit(FUN_00170f10,&DAT_013948a0,&PTR_LOOP_01391000);
  _DAT_01394890 = (undefined1  [16])0x0;
  __cxa_atexit(FUN_009e8110,&DAT_01394890,&PTR_LOOP_01391000);
  if (DAT_015da160 == '\0') {
    DAT_015da160 = '\x01';
    __cxa_atexit(FUN_004b2370,&DAT_015da158,&PTR_LOOP_01391000);
  }
  if (DAT_015da150 == '\0') {
    DAT_015da150 = '\x01';
    __cxa_atexit(FUN_004773c0,&DAT_015bf3e8,&PTR_LOOP_01391000);
  }
  if (DAT_015da148 == '\0') {
    DAT_015da148 = '\x01';
    __cxa_atexit(FUN_004abd70,&DAT_015df4f8,&PTR_LOOP_01391000);
  }
  if (DAT_015da130 == '\0') {
    DAT_015da130 = '\x01';
    __cxa_atexit(FUN_00477380,&DAT_015da128,&PTR_LOOP_01391000);
  }
  if (DAT_015da120 == '\0') {
    DAT_015da120 = '\x01';
    __cxa_atexit(FUN_0047ce80,&DAT_015da118,&PTR_LOOP_01391000);
  }
  if (DAT_015da0b8 == '\0') {
    DAT_015da0b8 = '\x01';
    DAT_015c7b60 = 0;
    DAT_015c7b68 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c7b60,&PTR_LOOP_01391000);
  }
  if (DAT_015da168 == '\0') {
    DAT_015da168 = '\x01';
    DAT_015c7b20 = 0;
    DAT_015c7b28 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c7b20,&PTR_LOOP_01391000);
  }
  if (DAT_015da0d8 == '\0') {
    DAT_015da0d8 = '\x01';
    DAT_015da840 = 0;
    DAT_015da848 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da840,&PTR_LOOP_01391000);
  }
  if (DAT_015da0d0 == '\0') {
    DAT_015da0d0 = '\x01';
    DAT_015da0c0 = 0;
    DAT_015da0c8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da0c0,&PTR_LOOP_01391000);
  }
  if (DAT_01394880 == '\0') {
    DAT_01394880 = '\x01';
    _DAT_01394870 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_01394870,&PTR_LOOP_01391000);
  }
  if (DAT_01394860 == '\0') {
    DAT_01394860 = '\x01';
    _DAT_01394850 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_01394850,&PTR_LOOP_01391000);
  }
  if (DAT_015da140 == '\0') {
    DAT_015da140 = '\x01';
    __cxa_atexit(FUN_00488fc0,&DAT_015da138,&PTR_LOOP_01391000);
  }
  if (DAT_015a7638 == '\0') {
    DAT_015a7638 = '\x01';
    __cxa_atexit(FUN_0047cdb0,&DAT_015a76b8,&PTR_LOOP_01391000);
  }
  if (DAT_015a7630 == '\0') {
    DAT_015a7630 = '\x01';
    __cxa_atexit(FUN_001879e0,&DAT_015a7628,&PTR_LOOP_01391000);
  }
  if (DAT_015da0e0 == '\0') {
    DAT_015da0e0 = '\x01';
    __cxa_atexit(FUN_0047db80,&DAT_015c7b40,&PTR_LOOP_01391000);
  }
  if (DAT_01394840 == '\0') {
    DAT_01394840 = '\x01';
    _DAT_01394830 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_01394830,&PTR_LOOP_01391000);
  }
  if (DAT_015da000 == '\0') {
    DAT_015da000 = '\x01';
    DAT_015bfb30 = 0;
    DAT_015bfb38 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015bfb30,&PTR_LOOP_01391000);
  }
  if (DAT_015da090 == '\0') {
    DAT_015da090 = '\x01';
    DAT_015da080 = 0;
    DAT_015da088 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da080,&PTR_LOOP_01391000);
  }
  if (DAT_013a3eb0 == '\0') {
    DAT_013a3eb0 = '\x01';
    __cxa_atexit(FUN_00475e10,&DAT_013a3ea8,&PTR_LOOP_01391000);
  }
  FUN_000e5d20();
  return;
}


```

## `FUN_000c8ef0` @ 000c8ef0 (60 slots)
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_000c8ef0(void)

{
  undefined8 local_44;
  undefined4 local_3c;
  undefined8 local_38;
  undefined4 local_30;
  undefined8 local_2c;
  undefined4 local_24;
  undefined8 local_20;
  undefined4 local_18;
  
  if (DAT_015da800 == '\0') {
    DAT_015da800 = '\x01';
    _DAT_015da7f0 = 0;
    DAT_015da7f8 = 0x3f800000;
  }
  if (DAT_015da7e8 == '\0') {
    DAT_015da7e8 = '\x01';
    DAT_015c02b0 = 0;
    DAT_015c02a8 = 0x3f80000000000000;
  }
  std__ios_base__Init__Init(&DAT_013a2ca0);
  __cxa_atexit(FUN_00c69340,&DAT_013a2ca0,&PTR_LOOP_01391000);
  if (DAT_015da7d8 == '\0') {
    DAT_015da7d8 = '\x01';
    _DAT_015df2d0 = (undefined1  [16])0x0;
  }
  if (DAT_015da7d0 == '\0') {
    DAT_015da7d0 = '\x01';
    DAT_015da7c8 = 0;
  }
  if (DAT_015da7b8 == '\0') {
    DAT_015da7b8 = '\x01';
    DAT_015c01c8 = 0;
    DAT_015c01d0 = 0;
  }
  local_2c = 0x3f80000000000000;
  local_44 = 0;
  local_3c = 0;
  local_38 = 0x3f800000;
  local_30 = 0;
  local_24 = 0;
  local_20 = 0;
  local_18 = 0xbf800000;
  FUN_0048f390(&DAT_013a2c60,&local_38,&local_44);
  if (DAT_015da6d0 == '\0') {
    DAT_015da6d0 = '\x01';
    _DAT_015da690 = 0;
    _DAT_015da698 = 0;
    _DAT_015da6a0 = 0;
    _DAT_015da6a8 = 0;
    _DAT_015da6b0 = 0;
    _DAT_015da6b8 = 0;
    _DAT_015da6c0 = 0;
    _DAT_015da6c8 = 0;
  }
  if (DAT_015da680 == '\0') {
    DAT_015da680 = '\x01';
    DAT_015da678 = 0x3f800000;
    DAT_015da670 = 0x3f8000003f800000;
  }
  if (DAT_015da798 == '\0') {
    DAT_015da798 = '\x01';
    DAT_015da790 = 0x3f800000;
    DAT_015da788 = 0x3f8000003f800000;
  }
  if (DAT_015da720 == '\0') {
    DAT_015da720 = '\x01';
    _DAT_015bf230 = 0x3f800000;
    _DAT_015bf238 = 0;
    _DAT_015bf240 = 0x3f80000000000000;
    _DAT_015bf248 = 0;
    _DAT_015bf250 = 0;
    _DAT_015bf258 = 0x3f800000;
    _DAT_015bf260 = 0;
    _DAT_015bf268 = 0x3f80000000000000;
  }
  _DAT_015dece8 = 0x800000006;
  _DAT_015deca0 = 0xffffffff;
  DAT_015deca8 = 0;
  _DAT_015decac = 1;
  DAT_015decb4 = 0;
  _DAT_015decb8 = 2;
  DAT_015decc0 = 0;
  _DAT_015decc4 = 3;
  DAT_015deccc = 0;
  _DAT_015decd0 = 4;
  DAT_015decd8 = 0;
  _DAT_015decdc = 5;
  DAT_015dece4 = 0;
  DAT_015decf0 = 0;
  _DAT_015decf4 = 7;
  DAT_015decfc = 0;
  _DAT_015ded00 = 0x800000008;
  DAT_015ded08 = 0;
  _DAT_015ded0c = 0x700000009;
  DAT_015ded14 = 0;
  _DAT_015ded18 = 0x80000000a;
  DAT_015ded20 = 0;
  _DAT_015ded24 = 0xb;
  DAT_015ded2c = 0;
  _DAT_015ded30 = 0x70000000c;
  DAT_015ded38 = 0;
  _DAT_015ded3c = 0x80000000d;
  DAT_015ded44 = 0;
  _DAT_015ded48 = 0xffffffff;
  DAT_015ded50 = 0;
  _DAT_015ded54 = 0xffffffff;
  DAT_015ded5c = 0;
  _DAT_015ded60 = 0xffffffff;
  DAT_015ded68 = 0;
  _DAT_015ded6c = 0x11;
  DAT_015ded74 = 0;
  _DAT_015ded78 = 0x12;
  DAT_015ded80 = 0;
  _DAT_015ded84 = 0xffffffff;
  DAT_015ded8c = 0;
  _DAT_015ded90 = 0xffffffff;
  DAT_015ded98 = 0;
  _DAT_015ded9c = 0xffffffff;
  DAT_015deda4 = 0;
  _DAT_015deda8 = 0xffffffff;
  DAT_015dedb0 = 0;
  _DAT_015dedb4 = 0xffffffff;
  DAT_015dedbc = 0;
  _DAT_015dedc0 = 0xffffffff;
  DAT_015dedc8 = 0;
  _DAT_015dedcc = 0xffffffff;
  DAT_015dedd4 = 0;
  _DAT_015dedd8 = 0x50000001a;
  DAT_015dede0 = 0;
  _DAT_015dede4 = 0x1b;
  DAT_015dedec = 1;
  _DAT_015dedf0 = 0x1c;
  DAT_015dedf8 = 1;
  _DAT_015dedfc = 0x1d;
  DAT_015dee04 = 0;
  _DAT_015dee08 = 0x1e;
  DAT_015dee10 = 0;
  _DAT_015dee14 = 0x1f;
  DAT_015dee1c = 0;
  _DAT_015dee20 = 0x20;
  DAT_015dee28 = 0;
  _DAT_015dee2c = 0x21;
  DAT_015dee34 = 0;
  _DAT_015dee38 = 0x22;
  DAT_015dee40 = 0;
  _DAT_015dee44 = 0x23;
  DAT_015dee4c = 0;
  _DAT_015dee50 = 0x24;
  DAT_015dee58 = 0;
  _DAT_015dee5c = 0xffffffff;
  DAT_015dee64 = 0;
  _DAT_015dee68 = 0xffffffff;
  DAT_015dee70 = 0;
  _DAT_015dee74 = 0xffffffff;
  DAT_015dee7c = 0;
  _DAT_015dee80 = 0x28;
  DAT_015dee88 = 0;
  _DAT_015dee8c = 0x29;
  DAT_015dee94 = 0;
  _DAT_015dee98 = 0x2a;
  DAT_015deea0 = 0;
  _DAT_015deea4 = 0xffffffff;
  DAT_015deeac = 0;
  _DAT_015deeb0 = 0xffffffff;
  DAT_015deeb8 = 0;
  _DAT_015deebc = 0xffffffff;
  DAT_015deec4 = 0;
  _DAT_015deec8 = 0x2e;
  DAT_015deed0 = 0;
  _DAT_015deed4 = 0x2f;
  DAT_015deedc = 0;
  _DAT_015deee0 = 0x30;
  DAT_015deee8 = 0;
  _DAT_015deeec = 0x31;
  DAT_015deef4 = 0;
  _DAT_015deef8 = 0xffffffff;
  DAT_015def00 = 0;
  _DAT_015def04 = 0xffffffff;
  DAT_015def0c = 0;
  _DAT_015def10 = 0x34;
  DAT_015def18 = 0;
  _DAT_015def1c = 0xffffffff;
  DAT_015def24 = 0;
  _DAT_015def28 = 0x36;
  DAT_015def30 = 0;
  _DAT_015def34 = 0xffffffff;
  DAT_015def3c = 0;
  _DAT_015def40 = 0xffffffff;
  DAT_015def48 = 0;
  _DAT_015def4c = 0xffffffff;
  DAT_015def54 = 0;
  _DAT_015def58 = 0xffffffff;
  DAT_015def60 = 0;
  _DAT_015def64 = 0xffffffff;
  DAT_015def6c = 0;
  _DAT_015def70 = 0x3c;
  DAT_015def78 = 0;
  _DAT_015def7c = 0x3d;
  DAT_015def84 = 0;
  _DAT_015def88 = 0x3e;
  DAT_015def90 = 0;
  _DAT_015def94 = 0x3f;
  DAT_015def9c = 0;
  _DAT_015defa0 = 0x40;
  DAT_015defa8 = 0;
  _DAT_015defac = 0x41;
  DAT_015defb4 = 0;
  _DAT_015defb8 = 0x42;
  DAT_015defc0 = 0;
  _DAT_015defc4 = 0x43;
  DAT_015defcc = 0;
  _DAT_015defd0 = 0x44;
  DAT_015defd8 = 0;
  _DAT_015defdc = 0x45;
  DAT_015defe4 = 0;
  _DAT_015defe8 = 0x46;
  DAT_015deff0 = 0;
  _DAT_015deff4 = 0xffffffff;
  DAT_015deffc = 0;
  _DAT_015df000 = 0x48;
  DAT_015df008 = 0;
  _DAT_015df00c = 0xffffffff;
  DAT_015df014 = 0;
  _DAT_015df018 = 0xffffffff;
  DAT_015df020 = 0;
  _DAT_015df024 = 0x4b;
  DAT_015df02c = 0;
  _DAT_015df030 = 0x4c;
  DAT_015df038 = 0;
  _DAT_015df03c = 0x4d;
  DAT_015df044 = 0;
  _DAT_015df048 = 0xffffffff;
  DAT_015df050 = 0;
  _DAT_015df054 = 0xffffffff;
  DAT_015df05c = 0;
  _DAT_015df060 = 0x50;
  DAT_015df068 = 0;
  _DAT_015df06c = 0xffffffff;
  DAT_015df074 = 0;
  _DAT_015df078 = 0xffffffff;
  DAT_015df080 = 0;
  _DAT_015df084 = 0x53;
  DAT_015df08c = 0;
  _DAT_015df090 = 0xffffffff;
  DAT_015df098 = 0;
  _DAT_015df09c = 0x700000055;
  DAT_015df0a4 = 0;
  if (DAT_015da2d0 == '\0') {
    DAT_015da2d0 = '\x01';
    DAT_015da8f0 = 0;
    DAT_015da8f8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da8f0,&PTR_LOOP_01391000);
  }
  if (DAT_015da160 == '\0') {
    DAT_015da160 = '\x01';
    __cxa_atexit(FUN_004b2370,&DAT_015da158,&PTR_LOOP_01391000);
  }
  if (DAT_015da150 == '\0') {
    DAT_015da150 = '\x01';
    __cxa_atexit(FUN_004773c0,&DAT_015bf3e8,&PTR_LOOP_01391000);
  }
  if (DAT_013a2c50 == '\0') {
    DAT_013a2c50 = '\x01';
    _DAT_013a2c40 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2c40,&PTR_LOOP_01391000);
  }
  if (DAT_015da148 == '\0') {
    DAT_015da148 = '\x01';
    __cxa_atexit(FUN_004abd70,&DAT_015df4f8,&PTR_LOOP_01391000);
  }
  if (DAT_015da130 == '\0') {
    DAT_015da130 = '\x01';
    __cxa_atexit(FUN_00477380,&DAT_015da128,&PTR_LOOP_01391000);
  }
  if (DAT_015da120 == '\0') {
    DAT_015da120 = '\x01';
    __cxa_atexit(FUN_0047ce80,&DAT_015da118,&PTR_LOOP_01391000);
  }
  if (DAT_013a2c38 == '\0') {
    DAT_013a2c38 = '\x01';
    _DAT_015db6a0 = 0;
    DAT_015db6a8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015db6a0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2c30 == '\0') {
    DAT_013a2c30 = '\x01';
    _DAT_015db6c0 = 0;
    DAT_015db6c8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015db6c0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2c28 == '\0') {
    DAT_013a2c28 = '\x01';
    __cxa_atexit(FUN_00124900,&DAT_015c7b48,&PTR_LOOP_01391000);
  }
  if (DAT_015da000 == '\0') {
    DAT_015da000 = '\x01';
    DAT_015bfb30 = 0;
    DAT_015bfb38 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015bfb30,&PTR_LOOP_01391000);
  }
  if (DAT_015d4d70 == '\0') {
    DAT_015d4d70 = '\x01';
    DAT_015d4d60 = 0;
    DAT_015d4d68 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015d4d60,&PTR_LOOP_01391000);
  }
  if (DAT_015d4d78 == '\0') {
    DAT_015d4d78 = '\x01';
    DAT_015c0230 = 0;
    DAT_015c0238 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c0230,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fc0 == '\0') {
    DAT_013a2fc0 = '\x01';
    DAT_015db7a0 = 0;
    DAT_015db7a8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015db7a0,&PTR_LOOP_01391000);
  }
  if (DAT_015da070 == '\0') {
    DAT_015da070 = '\x01';
    DAT_015da060 = 0;
    DAT_015da068 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da060,&PTR_LOOP_01391000);
  }
  if (DAT_015d35a8 == '\0') {
    DAT_015d35a8 = '\x01';
    DAT_015df1b0 = 0;
    DAT_015df1b8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015df1b0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fb8 == '\0') {
    DAT_013a2fb8 = '\x01';
    DAT_015c8590 = 0;
    DAT_015c8598 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8590,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fb0 == '\0') {
    DAT_013a2fb0 = '\x01';
    DAT_015c8560 = 0;
    DAT_015c8568 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8560,&PTR_LOOP_01391000);
  }
  if (DAT_015da0b0 == '\0') {
    DAT_015da0b0 = '\x01';
    DAT_015da0a0 = 0;
    DAT_015da0a8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da0a0,&PTR_LOOP_01391000);
  }
  if (DAT_015d4db0 == '\0') {
    DAT_015d4db0 = '\x01';
    DAT_015d4da0 = 0;
    DAT_015d4da8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015d4da0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fa8 == '\0') {
    DAT_013a2fa8 = '\x01';
    DAT_015c8550 = 0;
    DAT_015c8558 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8550,&PTR_LOOP_01391000);
  }
  if (DAT_013a2fa0 == '\0') {
    DAT_013a2fa0 = '\x01';
    DAT_015c8540 = 0;
    DAT_015c8548 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8540,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f98 == '\0') {
    DAT_013a2f98 = '\x01';
    DAT_015c8580 = 0;
    DAT_015c8588 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8580,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f90 == '\0') {
    DAT_013a2f90 = '\x01';
    DAT_015c8530 = 0;
    DAT_015c8538 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8530,&PTR_LOOP_01391000);
  }
  if (DAT_015a4408 == '\0') {
    DAT_015a4408 = '\x01';
    DAT_015c8520 = 0;
    DAT_015c8528 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8520,&PTR_LOOP_01391000);
  }
  if (DAT_015a4410 == '\0') {
    DAT_015a4410 = '\x01';
    DAT_015c8510 = 0;
    DAT_015c8518 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8510,&PTR_LOOP_01391000);
  }
  if (DAT_015da0b8 == '\0') {
    DAT_015da0b8 = '\x01';
    DAT_015c7b60 = 0;
    DAT_015c7b68 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c7b60,&PTR_LOOP_01391000);
  }
  if (DAT_015d4d90 == '\0') {
    DAT_015d4d90 = '\x01';
    DAT_015d4d80 = 0;
    DAT_015d4d88 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015d4d80,&PTR_LOOP_01391000);
  }
  if (DAT_015da0d8 == '\0') {
    DAT_015da0d8 = '\x01';
    DAT_015da840 = 0;
    DAT_015da848 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da840,&PTR_LOOP_01391000);
  }
  if (DAT_015da0d0 == '\0') {
    DAT_015da0d0 = '\x01';
    DAT_015da0c0 = 0;
    DAT_015da0c8 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da0c0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f88 == '\0') {
    DAT_013a2f88 = '\x01';
    DAT_015c8500 = 0;
    DAT_015c8508 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015c8500,&PTR_LOOP_01391000);
  }
  if (DAT_015da020 == '\0') {
    DAT_015da020 = '\x01';
    DAT_015da010 = 0;
    DAT_015da018 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da010,&PTR_LOOP_01391000);
  }
  if (DAT_015db798 == '\0') {
    DAT_015db798 = '\x01';
    DAT_015bf200 = 0;
    DAT_015bf208 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015bf200,&PTR_LOOP_01391000);
  }
  if (DAT_015da090 == '\0') {
    DAT_015da090 = '\x01';
    DAT_015da080 = 0;
    DAT_015da088 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015da080,&PTR_LOOP_01391000);
  }
  if (DAT_015d4d50 == '\0') {
    DAT_015d4d50 = '\x01';
    _DAT_015d4d40 = 0;
    DAT_015d4d48 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015d4d40,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f80 == '\0') {
    DAT_013a2f80 = '\x01';
    _DAT_013a2f70 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2f70,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f60 == '\0') {
    DAT_013a2f60 = '\x01';
    _DAT_013a2f50 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2f50,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f40 == '\0') {
    DAT_013a2f40 = '\x01';
    _DAT_013a2f30 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2f30,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f20 == '\0') {
    DAT_013a2f20 = '\x01';
    _DAT_013a2f10 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2f10,&PTR_LOOP_01391000);
  }
  if (DAT_013a2f00 == '\0') {
    DAT_013a2f00 = '\x01';
    _DAT_013a2ef0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2ef0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2ee0 == '\0') {
    DAT_013a2ee0 = '\x01';
    _DAT_013a2ed0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2ed0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2ec0 == '\0') {
    DAT_013a2ec0 = '\x01';
    _DAT_013a2eb0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2eb0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2ea0 == '\0') {
    DAT_013a2ea0 = '\x01';
    _DAT_013a2e90 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2e90,&PTR_LOOP_01391000);
  }
  if (DAT_013a2e80 == '\0') {
    DAT_013a2e80 = '\x01';
    _DAT_013a2e70 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2e70,&PTR_LOOP_01391000);
  }
  if (DAT_013a2e60 == '\0') {
    DAT_013a2e60 = '\x01';
    _DAT_013a2e50 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2e50,&PTR_LOOP_01391000);
  }
  if (DAT_013a2e40 == '\0') {
    DAT_013a2e40 = '\x01';
    _DAT_013a2e30 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2e30,&PTR_LOOP_01391000);
  }
  if (DAT_013a2e20 == '\0') {
    DAT_013a2e20 = '\x01';
    _DAT_013a2e10 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2e10,&PTR_LOOP_01391000);
  }
  if (DAT_013a2e00 == '\0') {
    DAT_013a2e00 = '\x01';
    _DAT_013a2df0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2df0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2de0 == '\0') {
    DAT_013a2de0 = '\x01';
    _DAT_013a2dd0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2dd0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2dc0 == '\0') {
    DAT_013a2dc0 = '\x01';
    _DAT_013a2db0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2db0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2da0 == '\0') {
    DAT_013a2da0 = '\x01';
    _DAT_013a2d90 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2d90,&PTR_LOOP_01391000);
  }
  if (DAT_013a2d80 == '\0') {
    DAT_013a2d80 = '\x01';
    _DAT_013a2d70 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2d70,&PTR_LOOP_01391000);
  }
  if (DAT_013a2d60 == '\0') {
    DAT_013a2d60 = '\x01';
    _DAT_013a2d50 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2d50,&PTR_LOOP_01391000);
  }
  if (DAT_013a2d40 == '\0') {
    DAT_013a2d40 = '\x01';
    _DAT_013a2d30 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2d30,&PTR_LOOP_01391000);
  }
  if (DAT_013a2d20 == '\0') {
    DAT_013a2d20 = '\x01';
    _DAT_013a2d10 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2d10,&PTR_LOOP_01391000);
  }
  if (DAT_013a2d00 == '\0') {
    DAT_013a2d00 = '\x01';
    _DAT_013a2cf0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2cf0,&PTR_LOOP_01391000);
  }
  if (DAT_013a2ce0 == '\0') {
    DAT_013a2ce0 = '\x01';
    _DAT_013a2cd0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2cd0,&PTR_LOOP_01391000);
  }
  if (DAT_015db790 == '\0') {
    DAT_015db790 = '\x01';
    _DAT_015db780 = 0;
    DAT_015db788 = 0;
    __cxa_atexit(FUN_009e8110,&DAT_015db780,&PTR_LOOP_01391000);
  }
  if (DAT_013a2cc0 == '\0') {
    DAT_013a2cc0 = '\x01';
    _DAT_013a2cb0 = (undefined1  [16])0x0;
    __cxa_atexit(FUN_009e8110,&DAT_013a2cb0,&PTR_LOOP_01391000);
  }
  return;
}


```
