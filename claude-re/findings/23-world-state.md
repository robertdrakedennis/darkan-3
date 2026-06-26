# World/state handler decompilations (binary)


## `jag::packethandlers::ClientState::BindHandlers` @ 000aa85e
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

## `jag::packethandlers::ClientState::BindHandlers_extra` @ 000aaf4c
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

## `jag::packethandlers::ZoneUpdates::BindHandlers` @ 000ae1a8
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

## `jag::packethandlers::ZoneUpdates::LOC_MERGE` @ 000eeba0
```c

undefined * jag::packethandlers::ZoneUpdates::LOC_MERGE(long *param_1,long param_2)

{
  undefined4 *puVar1;
  char cVar2;
  long lVar3;
  long *plVar4;
  int iVar5;
  undefined8 *puVar6;
  uint uVar7;
  long lVar8;
  undefined8 uVar9;
  long lVar10;
  int iVar11;
  char cVar12;
  long lVar13;
  bool bVar14;
  
  lVar3 = *(long *)(param_2 + 0x18);
  bVar14 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar3 + 4;
  uVar7 = *(uint *)(*(long *)(param_2 + 0x10) + lVar3);
  *(long *)(param_2 + 0x18) = lVar3 + 5;
  cVar2 = *(char *)(*(long *)(param_2 + 0x10) + 4 + lVar3);
  puVar6 = &DAT_015da030;
  lVar3 = *(long *)((long)&__DT_RELA[0xca6].r_info + *param_1);
  if (bVar14) {
    uVar7 = uVar7 >> 0x18 | (uVar7 & 0xff0000) >> 8 | (uVar7 & 0xff00) << 8 | uVar7 << 0x18;
  }
  if ((*(int *)(lVar3 + 0x3c) == 4) &&
     (lVar3 = *(long *)(*(long *)(lVar3 + 0x80) + (long)DAT_015dece8 * 8), lVar3 != 0)) {
    plVar4 = *(long **)(lVar3 + 0x38);
    puVar6 = (undefined8 *)(**(code **)(*plVar4 + 0x40))(plVar4,uVar7,0);
  }
  lVar3 = puVar6[1];
  if (*(long *)(lVar3 + 0x250) != 0) {
    cVar12 = '\x02';
    if ((cVar2 != '\x17') && (cVar12 = '\b', cVar2 != '\x18')) {
      cVar12 = cVar2;
    }
    uVar9 = *(undefined8 *)(lVar3 + 0x238);
    if (0 < (int)uVar9) {
      lVar8 = 0;
      do {
        while (iVar5 = (int)lVar8, *(char *)(*(long *)(lVar3 + 0x240) + lVar8) == cVar12) {
          lVar10 = *(long *)(lVar3 + 600) + lVar8 * 0x18;
          if (*(int *)(lVar10 + 8) < 1) break;
          lVar13 = 0;
          iVar11 = 0;
          do {
            iVar11 = iVar11 + 1;
            plVar4 = *(long **)(*(long *)(lVar3 + 0x30) + 0x10);
            puVar1 = (undefined4 *)(*(long *)(lVar10 + 0x10) + lVar13);
            lVar13 = lVar13 + 4;
            (**(code **)(*plVar4 + 0x38))(plVar4,DAT_013a3508,*puVar1,0,1,1);
            lVar10 = *(long *)(lVar3 + 600) + lVar8 * 0x18;
          } while (iVar11 < *(int *)(lVar10 + 8));
          uVar9 = *(undefined8 *)(lVar3 + 0x238);
          lVar8 = lVar8 + 1;
          if ((int)uVar9 <= iVar5 + 1) {
            return &DAT_015d3620;
          }
        }
        lVar8 = lVar8 + 1;
      } while (iVar5 + 1 < (int)uVar9);
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_ENCLOSED` @ 000eefc0
```c

/* Setting prototype: undefined * UPDATE_ZONE_PARTIAL_ENCLOSED(long * thisPtr, long packet, int *
   sizeRef, undefined8 * userDataPtr) */

undefined *
jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_ENCLOSED
          (long *thisPtr,long packet,int *sizeRef,undefined8 *userDataPtr)

{
  byte bVar1;
  char cVar2;
  int iVar3;
  int iVar4;
  int iVar5;
  long lVar6;
  undefined8 uVar7;
  long lVar8;
  long lVar9;
  long lVar10;
  int local_34;
  undefined8 local_30;
  
                    /* UPDATE_ZONE_PARTIAL_ENCLOSED handler: opcode 0x4c (76), size -2 (varShort).
                       Reads 3-byte zone header (level, baseY, baseX) into globals, then loops over
                       sub-opcodes via g_zoneSubProtVector (DAT_015d4580). Sub-op > 0x11 -> error.
                       Sub-op with handlerTarget=0 -> skip payload via registered size. */
  iVar3 = *sizeRef;
  lVar6 = *(long *)(packet + 0x18);
  uVar7 = *userDataPtr;
  lVar8 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *thisPtr);
  lVar10 = lVar6 + 3;
  *(long *)(packet + 0x18) = lVar6 + 1;
  lVar9 = *(long *)(packet + 0x10);
  iVar4 = *(int *)(lVar8 + 0x60c);
  iVar5 = *(int *)(lVar8 + 0x608);
  bVar1 = *(byte *)(lVar9 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 2;
  cVar2 = *(char *)(lVar9 + 1 + lVar6);
  *(long *)(packet + 0x18) = lVar10;
  DAT_013942b0 = iVar4 + cVar2 * 8;
  DAT_013942a8 = -(uint)bVar1 & 0xff;
  DAT_013942ac = iVar5 + (char)(-0x80 - *(char *)(lVar9 + 2 + lVar6)) * 8;
  do {
    if (iVar3 <= (int)lVar10) {
      return &DAT_015d3620;
    }
    while( true ) {
      *(long *)(packet + 0x18) = lVar10 + 1;
      bVar1 = *(byte *)(*(long *)(packet + 0x10) + lVar10);
      if ((0x11 < bVar1) || (lVar6 = *(long *)(DAT_015d4580 + (ulong)bVar1 * 8), lVar6 == 0)) {
        return &DAT_015d35c0;
      }
      local_34 = *(int *)(lVar6 + 4);
      if (*(long *)(lVar6 + 0x20) != 0) break;
      lVar10 = (long)local_34 + lVar10 + 1;
      *(long *)(packet + 0x18) = lVar10;
      if (iVar3 <= (int)lVar10) {
        return &DAT_015d3620;
      }
    }
    local_30 = uVar7;
    (**(code **)(lVar6 + 0x28))(lVar6 + 0x10,packet,&local_34,&local_30);
    lVar10 = *(long *)(packet + 0x18);
  } while( true );
}


```

## `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_FOLLOWS` @ 000ef150
```c

/* Setting prototype: undefined * UPDATE_ZONE_PARTIAL_FOLLOWS(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::UPDATE_ZONE_PARTIAL_FOLLOWS(long *thisPtr,long packet)

{
  byte bVar1;
  char cVar2;
  int iVar3;
  int iVar4;
  long lVar5;
  long lVar6;
  long lVar7;
  
                    /* UPDATE_ZONE_PARTIAL_FOLLOWS (948 op 41, 3B). asm-verified header wire:
                       zoneX(raw byte) + level(byteAdd) + zoneY(raw signed byte). The '3-byte
                       reorder' flagged earlier = level is the MIDDLE byte (not last). Encoder fixed
                       in Rev948ServerCodecsZone.kt. */
  lVar5 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *thisPtr);
  *(long *)(packet + 0x18) = lVar5 + 1;
  bVar1 = *(byte *)(lVar6 + lVar5);
  iVar3 = *(int *)(lVar7 + 0x608);
  *(long *)(packet + 0x18) = lVar5 + 2;
  cVar2 = *(char *)(lVar6 + 1 + lVar5);
  iVar4 = *(int *)(lVar7 + 0x60c);
  *(long *)(packet + 0x18) = lVar5 + 3;
  DAT_013942ac = iVar3 + -0x400 + (uint)bVar1 * 8;
  DAT_013942a8 = (uint)(byte)(cVar2 + 0x80);
  DAT_013942b0 = iVar4 + *(char *)(lVar6 + 2 + lVar5) * 8;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::NO_TIMEOUT` @ 000ef260
```c

/* Setting prototype: undefined * NO_TIMEOUT(void) */

undefined * jag::packethandlers::Misc::NO_TIMEOUT(void)

{
                    /* NO_TIMEOUT keepalive handler — empty body, returns success sentinel
                       (&DAT_015d3620) immediately. Bound at ServerProt op 54 (varByte, payload
                       always 0 bytes). 947-3 equivalent was op 216 with size=0; in 948 the wire
                       format changed to varByte with 0-length payload (2 bytes total: op + length).
                       Address unchanged 948-2-2 -> 948-5. */
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::DESTROY_ZONE_DATA` @ 000ef4e0
```c

undefined * jag::packethandlers::ClientState::DESTROY_ZONE_DATA(long *param_1)

{
  long lVar1;
  long *plVar2;
  
  lVar1 = *(long *)((long)&__DT_RELA[0xcfb].r_offset + *param_1);
  plVar2 = *(long **)((long)&__DT_RELA[0xff].r_addend + lVar1);
  *(undefined8 *)((long)&__DT_RELA[0xff].r_addend + lVar1) = 0;
  if (plVar2 != (long *)0x0) {
    (**(code **)(*plVar2 + 8))();
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::REBUILD_WORLDENTITY` @ 000efd80
```c

undefined * jag::packethandlers::ClientState::REBUILD_WORLDENTITY(long *param_1,long param_2)

{
  undefined4 *puVar1;
  char cVar2;
  uint uVar3;
  long lVar4;
  int iVar5;
  long lVar6;
  char cVar7;
  char cVar8;
  long lVar9;
  
                    /* REBUILD_WORLDENTITY handler: opcode 0xba (186), size -2 (varShort).
                       Multi-loop decode: read byte until 0xff for world ID, then byte until 0xff
                       for npc-index-set ID, then byte until 0xff for slot, then 4B int writes per
                       slot. */
  lVar9 = *(long *)(param_2 + 0x18);
  lVar4 = *(long *)(param_2 + 0x10);
  lVar6 = lVar9 + 1;
  *(long *)(param_2 + 0x18) = lVar6;
  iVar5 = DAT_01050dc0;
  cVar2 = *(char *)(lVar4 + lVar9);
  while (cVar2 != -1) {
    lVar9 = lVar6 + 1;
    *(long *)(param_2 + 0x18) = lVar9;
    cVar7 = *(char *)(lVar4 + lVar6);
    if (cVar7 != -1) {
      do {
        lVar6 = lVar9 + 1;
        *(long *)(param_2 + 0x18) = lVar6;
        cVar8 = *(char *)(lVar4 + lVar9);
        if (cVar8 != -1) {
          lVar9 = *(long *)((ulong)(uint)(int)cVar7 * 0x18 +
                           *(long *)(**(long **)((long)&__DT_RELA[0xcf2].r_info + *param_1) + 0x50 +
                                    (ulong)(uint)(int)cVar2 * 0x68));
          if (iVar5 == 0x3020100) {
            do {
              *(long *)(param_2 + 0x18) = lVar6 + 4;
              uVar3 = *(uint *)(lVar4 + lVar6);
              lVar6 = lVar6 + 5;
              *(uint *)(lVar9 + (ulong)(uint)(int)cVar8 * 4) =
                   uVar3 >> 0x18 | (uVar3 & 0xff0000) >> 8 | (uVar3 & 0xff00) << 8 | uVar3 << 0x18;
              *(long *)(param_2 + 0x18) = lVar6;
              cVar8 = *(char *)(lVar4 + -1 + lVar6);
            } while (cVar8 != -1);
          }
          else {
            do {
              *(long *)(param_2 + 0x18) = lVar6 + 4;
              puVar1 = (undefined4 *)(lVar4 + lVar6);
              lVar6 = lVar6 + 5;
              *(undefined4 *)(lVar9 + (ulong)(uint)(int)cVar8 * 4) = *puVar1;
              *(long *)(param_2 + 0x18) = lVar6;
              cVar8 = *(char *)(lVar4 + -1 + lVar6);
            } while (cVar8 != -1);
          }
        }
        lVar9 = lVar6 + 1;
        *(long *)(param_2 + 0x18) = lVar9;
        cVar7 = *(char *)(lVar4 + lVar6);
      } while (cVar7 != -1);
    }
    lVar6 = lVar9 + 1;
    *(long *)(param_2 + 0x18) = lVar6;
    cVar2 = *(char *)(lVar4 + lVar9);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_SYSUPDATE_TIMER` @ 000eff40
```c

undefined * jag::packethandlers::Misc::SET_SYSUPDATE_TIMER(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  uint uVar4;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::UPDATE_REBOOT_TIMER (op 184, size 4).
                       beta sz2 -> 948 sz4. signed g3 ticks + bool flag -> system-update timer. The
                       Ghidra function name 'SET_SYSUPDATE_TIMER' is a fabricated rename, NOT the
                       official packet name. */
  lVar1 = *(long *)((long)&__DT_RELA[0xd40].r_addend + *param_1);
  if (lVar1 != 0) {
    lVar2 = *(long *)(param_2 + 0x18);
    lVar3 = *(long *)(param_2 + 0x10);
    *(long *)(param_2 + 0x18) = lVar2 + 3;
    uVar4 = (uint)*(byte *)(lVar3 + lVar2) * 0x10000 + (uint)*(byte *)(lVar3 + 1 + lVar2) * 0x100 +
            (uint)*(byte *)(lVar3 + 2 + lVar2);
    if (0x7fffff < uVar4) {
      uVar4 = uVar4 - 0x1000000;
    }
    *(uint *)(lVar1 + 0x14) = uVar4;
    *(long *)(param_2 + 0x18) = lVar2 + 4;
    *(bool *)(lVar1 + 0x10) = *(char *)(lVar3 + 3 + lVar2) == '\x01';
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_INTERACTION_FLAG_C` @ 000effe0
```c

undefined * jag::packethandlers::Misc::SET_INTERACTION_FLAG_C(long *param_1,long param_2)

{
  int iVar1;
  long lVar2;
  int *piVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  long lVar6;
  long lVar7;
  int *piVar8;
  int *piVar9;
  int *piVar10;
  int iVar11;
  long lVar12;
  uint uVar13;
  
  lVar6 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar6 + 1;
  lVar2 = *(long *)((long)&__DT_RELA[0xcfd].r_offset + *param_1);
  iVar11 = -2 - (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar6);
  piVar8 = *(int **)((long)&__DT_RELA[0x429].r_addend + lVar2);
  piVar3 = *(int **)((long)&__DT_RELA[0x429].r_info + lVar2);
  lVar6 = ((long)piVar8 - (long)piVar3 >> 3) * -0x5555555555555555;
  lVar12 = (long)piVar8 - (long)piVar3;
  while (0 < lVar12) {
    lVar12 = lVar6 >> 1;
    piVar10 = piVar3 + lVar12 * 6;
    if (iVar11 <= *piVar10) {
      if (lVar12 == 0) break;
      lVar7 = lVar6 >> 2;
      piVar10 = piVar3 + lVar7 * 6;
      iVar1 = *piVar10;
      lVar6 = lVar12;
      lVar12 = lVar7;
      while (iVar11 <= iVar1) {
        if (lVar12 == 0) goto LAB_000f008b;
        piVar10 = piVar3 + (lVar12 >> 1) * 6;
        lVar6 = lVar12;
        lVar12 = lVar12 >> 1;
        iVar1 = *piVar10;
      }
    }
    piVar3 = piVar10 + 6;
    lVar6 = lVar6 - (lVar12 + 1);
    lVar12 = lVar6;
  }
LAB_000f008b:
  if ((piVar8 != piVar3) && (*piVar3 <= iVar11)) {
    piVar10 = piVar3 + 6;
    if ((piVar10 < piVar8) &&
       (lVar6 = ((long)piVar8 - (long)piVar10 >> 3) * -0x5555555555555555,
       0 < (long)piVar8 - (long)piVar10)) {
      uVar13 = (int)lVar6 - 1U & 3;
      piVar9 = piVar10;
      piVar8 = piVar3;
      lVar12 = lVar6;
      if (uVar13 != 0) {
        lVar12 = *(long *)(piVar3 + 2);
        uVar4 = *(undefined8 *)(piVar3 + 8);
        uVar5 = *(undefined8 *)(piVar3 + 10);
        piVar3[8] = 0;
        piVar3[9] = 0;
        piVar3[10] = 0;
        piVar3[0xb] = 0;
        *(undefined8 *)(piVar3 + 2) = uVar4;
        *piVar3 = piVar3[6];
        *(undefined8 *)(piVar3 + 4) = uVar5;
        if (lVar12 != 0) {
          ref_counter_base::DecRef();
        }
        piVar9 = piVar3 + 0xc;
        lVar12 = lVar6 + -1;
        piVar8 = piVar10;
        if (uVar13 != 1) {
          piVar8 = piVar9;
          if (uVar13 != 2) {
            uVar4 = *(undefined8 *)(piVar3 + 0xe);
            uVar5 = *(undefined8 *)(piVar3 + 0x10);
            piVar3[0xe] = 0;
            piVar3[0xf] = 0;
            piVar3[0x10] = 0;
            piVar3[0x11] = 0;
            *piVar10 = piVar3[0xc];
            lVar12 = *(long *)(piVar3 + 8);
            *(undefined8 *)(piVar3 + 10) = uVar5;
            *(undefined8 *)(piVar3 + 8) = uVar4;
            if (lVar12 != 0) {
              ref_counter_base::DecRef();
            }
            lVar12 = lVar6 + -2;
            piVar8 = piVar3 + 0x12;
            piVar10 = piVar9;
          }
          lVar6 = *(long *)(piVar10 + 2);
          uVar4 = *(undefined8 *)(piVar10 + 8);
          uVar5 = *(undefined8 *)(piVar10 + 10);
          piVar10[8] = 0;
          piVar10[9] = 0;
          piVar10[10] = 0;
          piVar10[0xb] = 0;
          *(undefined8 *)(piVar10 + 2) = uVar4;
          *piVar10 = piVar10[6];
          *(undefined8 *)(piVar10 + 4) = uVar5;
          if (lVar6 != 0) {
            ref_counter_base::DecRef();
          }
          lVar12 = lVar12 + -1;
          piVar9 = piVar8 + 6;
        }
      }
      while( true ) {
        lVar6 = *(long *)(piVar8 + 2);
        uVar4 = *(undefined8 *)(piVar8 + 8);
        uVar5 = *(undefined8 *)(piVar8 + 10);
        piVar8[8] = 0;
        piVar8[9] = 0;
        piVar8[10] = 0;
        piVar8[0xb] = 0;
        *(undefined8 *)(piVar8 + 2) = uVar4;
        *piVar8 = piVar8[6];
        *(undefined8 *)(piVar8 + 4) = uVar5;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
        }
        if (lVar12 == 1) break;
        lVar6 = *(long *)(piVar9 + 2);
        uVar4 = *(undefined8 *)(piVar9 + 8);
        uVar5 = *(undefined8 *)(piVar9 + 10);
        piVar9[8] = 0;
        piVar9[9] = 0;
        piVar9[10] = 0;
        piVar9[0xb] = 0;
        *(undefined8 *)(piVar9 + 2) = uVar4;
        *piVar9 = piVar9[6];
        *(undefined8 *)(piVar9 + 4) = uVar5;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
        }
        uVar4 = *(undefined8 *)(piVar9 + 0xe);
        uVar5 = *(undefined8 *)(piVar9 + 0x10);
        piVar9[0xe] = 0;
        piVar9[0xf] = 0;
        piVar9[0x10] = 0;
        piVar9[0x11] = 0;
        piVar9[6] = piVar9[0xc];
        lVar6 = *(long *)(piVar9 + 8);
        *(undefined8 *)(piVar9 + 10) = uVar5;
        *(undefined8 *)(piVar9 + 8) = uVar4;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
        }
        lVar6 = *(long *)(piVar9 + 0xe);
        uVar4 = *(undefined8 *)(piVar9 + 0x14);
        piVar8 = piVar9 + 0x12;
        uVar5 = *(undefined8 *)(piVar9 + 0x16);
        piVar9[0xc] = piVar9[0x12];
        piVar9[0x14] = 0;
        piVar9[0x15] = 0;
        piVar9[0x16] = 0;
        piVar9[0x17] = 0;
        *(undefined8 *)(piVar9 + 0xe) = uVar4;
        *(undefined8 *)(piVar9 + 0x10) = uVar5;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
        }
        piVar9 = piVar9 + 0x18;
        lVar12 = lVar12 + -4;
      }
      piVar8 = *(int **)((long)&__DT_RELA[0x429].r_addend + lVar2);
    }
    lVar6 = *(long *)(piVar8 + -4);
    *(int **)((long)&__DT_RELA[0x429].r_addend + lVar2) = piVar8 + -6;
    if (lVar6 != 0) {
      ref_counter_base::DecRef();
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_INTERACTION_FLAG_D` @ 000f0460
```c

undefined * jag::packethandlers::Misc::SET_INTERACTION_FLAG_D(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)((long)&__DT_RELA[0xd40].r_addend + *param_1);
  *(long *)(param_2 + 0x18) = lVar1 + 3;
  if (lVar2 != 0) {
    lVar3 = *(long *)(param_2 + 0x10);
    *(uint *)(lVar2 + 0x98) =
         (uint)*(byte *)(lVar3 + lVar1) * 0x10000 + (uint)*(byte *)(lVar3 + 2 + lVar1) * 0x100 +
         (uint)*(byte *)(lVar3 + 1 + lVar1);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_PLAYER_OP_2` @ 000f0510
```c

undefined * jag::packethandlers::Misc::SET_PLAYER_OP_2(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  ushort uVar4;
  bool bVar5;
  
  lVar1 = *param_1;
  lVar2 = *(long *)(param_2 + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  lVar3 = *(long *)((long)&__DT_RELA[0xf0].r_offset +
                   *(long *)((long)&__DT_RELA[0xcfb].r_offset + lVar1));
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar4 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar2);
  if (bVar5) {
    uVar4 = uVar4 << 8 | uVar4 >> 8;
  }
  lVar1 = *(long *)((long)&__DT_RELA[0xd00].r_offset + lVar1);
  *(int *)(lVar3 + 0x1c) = (int)(short)uVar4;
  *(undefined4 *)(lVar1 + 0x100) = *(undefined4 *)(*(long *)(lVar1 + 0xf8) + 0x10);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_PLAYER_OP_3` @ 000f05a0
```c

undefined * jag::packethandlers::Misc::SET_PLAYER_OP_3(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar3 = *(long *)((long)&__DT_RELA[0xf0].r_offset +
                   *(long *)((long)&__DT_RELA[0xcfb].r_offset + *param_1));
  *(long *)(param_2 + 0x18) = lVar1 + 1;
  lVar4 = *(long *)(lVar2 + 0xf8);
  *(uint *)(lVar3 + 0x18) = (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar1);
  *(undefined4 *)(lVar2 + 0x100) = *(undefined4 *)(lVar4 + 0x10);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL_2` @ 000f1420
```c

undefined * jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL_2(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  byte bVar7;
  char cVar8;
  undefined1 uVar9;
  byte bVar10;
  byte bVar11;
  byte bVar12;
  byte bVar13;
  byte bVar14;
  byte bVar15;
  byte bVar16;
  byte bVar17;
  char cVar18;
  char cVar19;
  byte bVar20;
  byte bVar21;
  byte bVar22;
  byte bVar23;
  char cVar24;
  long lVar25;
  long lVar26;
  ushort uVar27;
  uint uVar28;
  ushort uVar29;
  ushort uVar30;
  uint uVar31;
  int iVar32;
  uint uVar33;
  byte bVar34;
  uint alpha;
  uint uVar35;
  ushort local_72;
  uint local_70;
  ushort local_64;
  int local_50;
  int local_4c;
  undefined1 local_48;
  int local_44;
  int local_40;
  undefined1 local_3c;
  
  iVar32 = DAT_01050dc0;
  lVar25 = *(long *)(param_2 + 0x18);
  lVar26 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar25 + 2;
  uVar29 = *(ushort *)(lVar26 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 5;
  bVar1 = *(byte *)(lVar26 + 3 + lVar25);
  bVar2 = *(byte *)(lVar26 + 4 + lVar25);
  bVar3 = *(byte *)(lVar26 + 2 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 7;
  if (iVar32 == 0x3020100) {
    uVar29 = uVar29 << 8 | uVar29 >> 8;
  }
  bVar4 = *(byte *)(lVar26 + 6 + lVar25);
  bVar5 = *(byte *)(lVar26 + 5 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 8;
  bVar6 = *(byte *)(lVar26 + 7 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 9;
  bVar7 = *(byte *)(lVar26 + 8 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0xb;
  cVar8 = *(char *)(lVar26 + 10 + lVar25);
  local_72 = CONCAT11(local_72._1_1_,*(undefined1 *)(lVar26 + 9 + lVar25));
  *(long *)(param_2 + 0x18) = lVar25 + 0xd;
  uVar9 = *(undefined1 *)(lVar26 + 0xc + lVar25);
  bVar10 = *(byte *)(lVar26 + 0xb + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0xe;
  bVar11 = *(byte *)(lVar26 + 0xd + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x11;
  bVar12 = *(byte *)(lVar26 + 0xe + lVar25);
  bVar13 = *(byte *)(lVar26 + 0x10 + lVar25);
  bVar14 = *(byte *)(lVar26 + 0xf + lVar25);
  local_70 = CONCAT31(local_70._1_3_,uVar9);
  *(long *)(param_2 + 0x18) = lVar25 + 0x14;
  bVar15 = *(byte *)(lVar26 + 0x13 + lVar25);
  bVar16 = *(byte *)(lVar26 + 0x11 + lVar25);
  bVar17 = *(byte *)(lVar26 + 0x12 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x16;
  uVar9 = *(undefined1 *)(lVar26 + 0x15 + lVar25);
  cVar18 = *(char *)(lVar26 + 0x14 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x18;
  uVar30 = *(ushort *)(lVar26 + 0x16 + lVar25);
  local_64 = CONCAT11(local_64._1_1_,uVar9);
  uVar27 = uVar30 << 8 | uVar30 >> 8;
  if (iVar32 != 0x3020100) {
    uVar27 = uVar30;
  }
  iVar32 = (int)(short)uVar27;
  *(long *)(param_2 + 0x18) = lVar25 + 0x1a;
  cVar19 = *(char *)(lVar26 + 0x19 + lVar25);
  bVar20 = *(byte *)(lVar26 + 0x18 + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x1c;
  uVar30 = (ushort)bVar20 * 0x100 + (ushort)(byte)(cVar19 + 0x80);
  bVar20 = *(byte *)(lVar26 + 0x1a + lVar25);
  cVar19 = *(char *)(lVar26 + 0x1b + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x1f;
  bVar21 = *(byte *)(lVar26 + 0x1c + lVar25);
  bVar22 = *(byte *)(lVar26 + 0x1e + lVar25);
  bVar23 = *(byte *)(lVar26 + 0x1d + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x20;
  cVar24 = *(char *)(lVar26 + 0x1f + lVar25);
  *(long *)(param_2 + 0x18) = lVar25 + 0x21;
  bVar34 = -cVar24;
  alpha = 0xffffffff;
  if (bVar34 != 0xff) {
    alpha = (uint)bVar34;
  }
  if (uVar30 != 0xffff) {
    if ((bVar6 & 2) != 0) {
      if (iVar32 < 0) {
        iVar32 = iVar32 + 3;
      }
      iVar32 = iVar32 >> 2;
    }
    uVar35 = (local_70 & 0xffff) * 0x100 + (uint)bVar10 & 0xffff;
    uVar28 = (uint)bVar14 * 0x100 + (uint)bVar13 * 0x10000 + (uint)bVar12;
    uVar31 = (uint)bVar15 * 0x100 + (uint)bVar16 * 0x10000 + (uint)bVar17;
    uVar33 = (uint)bVar4 * 0x100 + (uint)bVar5 & 0xffff;
    local_50 = (uVar28 & 0x7ff) - 0x3ff;
    local_4c = ((int)uVar28 >> 0xb & 0x7ffU) - 0x3ff;
    local_48 = (int)uVar28 >> 0x16 == 1;
    local_44 = (uVar31 & 0x7ff) - 0x3ff;
    local_40 = ((int)uVar31 >> 0xb & 0x7ffU) - 0x3ff;
    local_3c = (int)uVar31 >> 0x16 == 1;
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar30,
                        (uint)(byte)(cVar8 + 0x80) + (uint)local_72 * 0x100 & 0xffff,(uint)uVar29,
                        (uint)bVar21 * 0x10000 + (uint)bVar22 * 0x100 + (uint)bVar23,
                        (uint)bVar1 * 0x10000 + (uint)bVar3 * 0x100 + (uint)bVar2,bVar6 & 1,
                        iVar32 << 2,
                        (int)(short)((ushort)bVar20 * 0x100 + (ushort)(byte)(cVar19 + 0x80)) << 2,
                        uVar33 << 8,uVar35 << 8,
                        ((int)-*(char *)(lVar26 + 0x20 + lVar25) + uVar33) * 0x100,
                        ((uVar35 - 0x80) + (uint)bVar7) * 0x100,&local_50,&local_44,
                        ((uint)(byte)(cVar18 + 0x80) + (uint)local_64 * 0x100 & 0xffff) << 2,alpha,
                        (uint)bVar11);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC_HALT` @ 000f1840
```c

undefined * jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC_HALT(long *param_1,long param_2)

{
  long lVar1;
  int iVar2;
  ushort *puVar3;
  int iVar4;
  byte bVar5;
  byte bVar6;
  char cVar7;
  char cVar8;
  byte bVar9;
  byte bVar10;
  byte bVar11;
  byte bVar12;
  byte bVar13;
  byte bVar14;
  byte bVar15;
  byte bVar16;
  long lVar17;
  long lVar18;
  int iVar19;
  ushort uVar20;
  int iVar21;
  ushort uVar22;
  ushort uVar23;
  uint alpha;
  uint uVar24;
  long lVar25;
  ushort uVar26;
  byte bVar27;
  uint uVar28;
  ushort *puVar29;
  int local_5c;
  int local_50;
  int local_4c;
  undefined1 local_48;
  int local_44;
  int local_40;
  undefined1 local_3c;
  
  iVar19 = DAT_013942b0;
  iVar4 = DAT_013942ac;
  lVar17 = *(long *)(param_2 + 0x18);
  lVar18 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar17 + 1;
  bVar5 = *(byte *)(lVar18 + lVar17);
  *(long *)(param_2 + 0x18) = lVar17 + 2;
  bVar6 = *(byte *)(lVar18 + 1 + lVar17);
  *(long *)(param_2 + 0x18) = lVar17 + 3;
  cVar7 = *(char *)(lVar18 + 2 + lVar17);
  *(long *)(param_2 + 0x18) = lVar17 + 4;
  cVar8 = *(char *)(lVar18 + 3 + lVar17);
  *(long *)(param_2 + 0x18) = lVar17 + 7;
  bVar9 = *(byte *)(lVar18 + 5 + lVar17);
  bVar10 = *(byte *)(lVar18 + 4 + lVar17);
  bVar11 = *(byte *)(lVar18 + 6 + lVar17);
  bVar27 = bVar6 & 2;
  *(long *)(param_2 + 0x18) = lVar17 + 10;
  bVar12 = *(byte *)(lVar18 + 8 + lVar17);
  bVar13 = *(byte *)(lVar18 + 7 + lVar17);
  lVar25 = lVar17 + 0xe;
  bVar14 = *(byte *)(lVar18 + 9 + lVar17);
  *(long *)(param_2 + 0x18) = lVar17 + 0xc;
  iVar2 = DAT_01050dc0;
  puVar29 = (ushort *)(lVar17 + 0xc + lVar18);
  uVar23 = *(ushort *)(lVar18 + 10 + lVar17);
  puVar3 = (ushort *)(lVar18 + lVar25);
  lVar1 = lVar17 + 0x10;
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar25;
    uVar20 = *puVar29;
    uVar23 = uVar23 << 8 | uVar23 >> 8;
    uVar20 = uVar20 << 8 | uVar20 >> 8;
    iVar21 = (int)(short)uVar20;
    if (bVar27 != 0) {
      if ((short)uVar20 < 0) {
        iVar21 = iVar21 + 3;
      }
      iVar21 = iVar21 >> 2;
    }
    *(long *)(param_2 + 0x18) = lVar1;
    uVar20 = *puVar3;
    *(long *)(param_2 + 0x18) = lVar17 + 0x12;
    uVar26 = *(ushort *)(lVar18 + 0x10 + lVar17);
    *(long *)(param_2 + 0x18) = lVar17 + 0x14;
    uVar22 = *(ushort *)(lVar18 + 0x12 + lVar17);
    uVar20 = uVar20 << 8 | uVar20 >> 8;
    uVar26 = uVar26 << 8 | uVar26 >> 8;
    uVar22 = uVar22 << 8 | uVar22 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar25;
    iVar21 = (int)(short)*puVar29;
    if (bVar27 == 0) {
      *(long *)(param_2 + 0x18) = lVar1;
      uVar20 = *puVar3;
    }
    else {
      *(long *)(param_2 + 0x18) = lVar1;
      iVar21 = iVar21 / 4;
      uVar20 = *puVar3;
    }
    *(long *)(param_2 + 0x18) = lVar17 + 0x12;
    uVar26 = *(ushort *)(lVar18 + 0x10 + lVar17);
    *(long *)(param_2 + 0x18) = lVar17 + 0x14;
    uVar22 = *(ushort *)(lVar18 + 0x12 + lVar17);
  }
  local_5c = (int)(short)uVar20;
  lVar25 = lVar17 + 0x14;
  *(long *)(param_2 + 0x18) = lVar17 + 0x15;
  bVar27 = *(byte *)(lVar18 + lVar25);
  *(long *)(param_2 + 0x18) = lVar17 + 0x17;
  uVar20 = *(ushort *)(lVar18 + 1 + lVar25);
  alpha = (uint)bVar27;
  if (alpha == 0xff) {
    alpha = 0xffffffff;
  }
  if (iVar2 == 0x3020100) {
    uVar20 = uVar20 << 8 | uVar20 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar17 + 0x1a;
  bVar27 = *(byte *)(lVar18 + 3 + lVar25);
  bVar15 = *(byte *)(lVar18 + 4 + lVar25);
  bVar16 = *(byte *)(lVar18 + 5 + lVar25);
  *(long *)(param_2 + 0x18) = lVar17 + 0x1d;
  if (uVar23 != 0xffff) {
    uVar28 = (uint)bVar16 + (uint)bVar27 * 0x10000 + (uint)bVar15 * 0x100;
    iVar2 = ((int)(uint)bVar5 >> 4) + iVar4 * 2;
    iVar4 = (bVar5 & 0xf) + iVar19 * 2;
    uVar24 = (uint)*(byte *)(lVar18 + 8 + lVar25) +
             (uint)*(byte *)(lVar18 + 7 + lVar25) * 0x100 +
             (uint)*(byte *)(lVar18 + 6 + lVar25) * 0x10000;
    local_50 = (uVar28 & 0x7ff) - 0x3ff;
    local_4c = ((int)uVar28 >> 0xb & 0x7ffU) - 0x3ff;
    local_48 = (int)uVar28 >> 0x16 == 1;
    local_44 = (uVar24 & 0x7ff) - 0x3ff;
    local_40 = ((int)uVar24 >> 0xb & 0x7ffU) - 0x3ff;
    local_3c = (int)uVar24 >> 0x16 == 1;
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar23,
                        (uint)uVar26,(uint)uVar22,
                        (uint)bVar11 + (uint)bVar10 * 0x10000 + (uint)bVar9 * 0x100,
                        (uint)bVar14 + (uint)bVar13 * 0x10000 + (uint)bVar12 * 0x100,bVar6 & 1,
                        iVar21 << 2,local_5c << 2,iVar2 * 0x100,iVar4 * 0x100,
                        (cVar7 + iVar2) * 0x100,(cVar8 + iVar4) * 0x100,&local_50,&local_44,
                        (uint)uVar20 << 2,alpha,DAT_013942a8);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_HALT` @ 000f1bc0
```c

undefined * jag::packethandlers::ZoneUpdates::MAP_PROJANIM_HALT(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  char cVar4;
  char cVar5;
  char cVar6;
  byte bVar7;
  byte bVar8;
  byte bVar9;
  byte bVar10;
  byte bVar11;
  byte bVar12;
  long lVar13;
  long lVar14;
  ushort uVar15;
  ushort *puVar16;
  uint alpha;
  uint uVar17;
  ushort uVar18;
  ushort uVar19;
  int iVar20;
  ushort uVar21;
  uint uVar22;
  ushort uVar23;
  int iVar24;
  int local_60;
  int local_5c;
  int local_50;
  int local_4c;
  undefined1 local_48;
  int local_44;
  int local_40;
  undefined1 local_3c;
  
  iVar24 = DAT_013942b0;
  iVar20 = DAT_013942ac;
  lVar13 = *(long *)(param_2 + 0x18);
  lVar14 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar13 + 1;
  cVar4 = *(char *)(lVar14 + lVar13);
  *(long *)(param_2 + 0x18) = lVar13 + 2;
  cVar5 = *(char *)(lVar14 + 1 + lVar13);
  *(long *)(param_2 + 0x18) = lVar13 + 3;
  cVar6 = *(char *)(lVar14 + 2 + lVar13);
  lVar1 = lVar13 + 10;
  *(long *)(param_2 + 0x18) = lVar13 + 6;
  bVar7 = *(byte *)(lVar14 + 4 + lVar13);
  bVar8 = *(byte *)(lVar14 + 3 + lVar13);
  bVar9 = *(byte *)(lVar14 + 5 + lVar13);
  lVar2 = lVar13 + 0xc;
  *(long *)(param_2 + 0x18) = lVar13 + 8;
  puVar16 = (ushort *)(lVar13 + 8 + lVar14);
  lVar3 = lVar13 + 0xe;
  uVar19 = *(ushort *)(lVar14 + 6 + lVar13);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar18 = *puVar16;
    uVar19 = uVar19 << 8 | uVar19 >> 8;
    *(long *)(param_2 + 0x18) = lVar2;
    uVar23 = *(ushort *)(lVar14 + lVar1);
    *(long *)(param_2 + 0x18) = lVar3;
    uVar21 = *(ushort *)(lVar14 + lVar2);
    *(long *)(param_2 + 0x18) = lVar13 + 0x10;
    uVar15 = *(ushort *)(lVar14 + lVar3);
    uVar18 = uVar18 << 8 | uVar18 >> 8;
    uVar23 = uVar23 << 8 | uVar23 >> 8;
    uVar21 = uVar21 << 8 | uVar21 >> 8;
    uVar15 = uVar15 << 8 | uVar15 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar18 = *puVar16;
    *(long *)(param_2 + 0x18) = lVar2;
    uVar23 = *(ushort *)(lVar14 + lVar1);
    *(long *)(param_2 + 0x18) = lVar3;
    uVar21 = *(ushort *)(lVar14 + lVar2);
    *(long *)(param_2 + 0x18) = lVar13 + 0x10;
    uVar15 = *(ushort *)(lVar14 + lVar3);
  }
  local_5c = (int)(short)uVar18;
  local_60 = (int)(short)uVar23;
  *(long *)(param_2 + 0x18) = lVar13 + 0x11;
  bVar10 = *(byte *)(lVar14 + 0x10 + lVar13);
  *(long *)(param_2 + 0x18) = lVar13 + 0x13;
  uVar18 = *(ushort *)(lVar14 + 0x11 + lVar13);
  *(long *)(param_2 + 0x18) = lVar13 + 0x19;
  bVar11 = *(byte *)(lVar14 + 0x16 + lVar13);
  bVar12 = *(byte *)(lVar14 + 0x18 + lVar13);
  alpha = (uint)bVar10;
  if (alpha == 0xff) {
    alpha = 0xffffffff;
  }
  uVar23 = uVar18 << 8 | uVar18 >> 8;
  if (DAT_01050dc0 != 0x3020100) {
    uVar23 = uVar18;
  }
  bVar10 = *(byte *)(lVar14 + 0x17 + lVar13);
  *(long *)(param_2 + 0x18) = lVar13 + 0x1c;
  if (uVar19 != 0xffff) {
    uVar22 = (uint)bVar12 + (uint)bVar11 * 0x10000 + (uint)bVar10 * 0x100;
    iVar20 = iVar20 + ((uint)(int)cVar4 >> 3 & 7);
    iVar24 = ((int)cVar4 & 7U) + iVar24;
    uVar17 = (uint)*(byte *)(lVar14 + 0x1a + lVar13) * 0x100 +
             (uint)*(byte *)(lVar14 + 0x19 + lVar13) * 0x10000 +
             (uint)*(byte *)(lVar14 + 0x1b + lVar13);
    local_50 = (uVar22 & 0x7ff) - 0x3ff;
    local_4c = ((int)uVar22 >> 0xb & 0x7ffU) - 0x3ff;
    local_48 = (int)uVar22 >> 0x16 == 1;
    local_44 = (uVar17 & 0x7ff) - 0x3ff;
    local_40 = ((int)uVar17 >> 0xb & 0x7ffU) - 0x3ff;
    local_3c = (int)uVar17 >> 0x16 == 1;
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar19,
                        (uint)uVar21,(uint)uVar15,DAT_015bf224,
                        (uint)bVar8 * 0x10000 + (uint)bVar7 * 0x100 + (uint)bVar9,
                        (byte)(cVar4 >> 7) >> 7,local_5c << 2,local_60 << 2,
                        (int)(((float)iVar20 + DAT_00cb6ae0) * DAT_00cb6ae4),
                        (int)(((float)iVar24 + DAT_00cb6ae0) * DAT_00cb6ae4),
                        (int)(((float)(iVar20 + cVar5) + DAT_00cb6ae0) * DAT_00cb6ae4),
                        (int)(((float)(iVar24 + cVar6) + DAT_00cb6ae0) * DAT_00cb6ae4),&local_50,
                        &local_44,(uint)uVar23 << 2,alpha,DAT_013942a8);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT` @ 000f1f30
```c

undefined * jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT(long *param_1)

{
  int *piVar1;
  
  piVar1 = (int *)((long)&__DT_RELA[0x548].r_addend + DAT_015c7b48);
  *piVar1 = *piVar1 + 1;
  game::SceneManager::PruneWorlds();
  *(undefined1 *)(*(long *)((long)&__DT_RELA[0xcfa].r_addend + *param_1) + 0x154) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::PLAYER_SPOTANIM` @ 000f2280
```c

undefined * jag::packethandlers::ZoneUpdates::PLAYER_SPOTANIM(long *param_1,long param_2)

{
  undefined4 uVar1;
  undefined4 uVar2;
  uint uVar3;
  long lVar4;
  undefined8 uVar5;
  long lVar6;
  byte bVar7;
  long *plVar8;
  bool bVar9;
  uint local_68;
  uint uStack_64;
  uint uStack_60;
  uint uStack_5c;
  undefined1 local_58 [8];
  undefined8 *local_50;
  
  lVar6 = *(long *)(param_2 + 0x18);
  lVar4 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar6 + 1;
  bVar7 = 0x80 - *(char *)(lVar4 + lVar6);
  bVar9 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar6 + 5;
  local_68 = *(uint *)(lVar4 + 1 + lVar6);
  if (bVar9) {
    local_68 = local_68 >> 0x18 | (local_68 & 0xff0000) >> 8 | (local_68 & 0xff00) << 8 |
               local_68 << 0x18;
    *(long *)(param_2 + 0x18) = lVar6 + 9;
    uVar3 = *(uint *)(lVar4 + 5 + lVar6);
    *(long *)(param_2 + 0x18) = lVar6 + 0xd;
    uStack_64 = uVar3 >> 0x18 | (uVar3 & 0xff0000) >> 8 | (uVar3 & 0xff00) << 8 | uVar3 << 0x18;
    uVar3 = *(uint *)(lVar4 + 9 + lVar6);
    *(long *)(param_2 + 0x18) = lVar6 + 0x11;
    uStack_60 = uVar3 >> 0x18 | (uVar3 & 0xff0000) >> 8 | (uVar3 & 0xff00) << 8 | uVar3 << 0x18;
    uVar3 = *(uint *)(lVar4 + 0xd + lVar6);
    uStack_5c = uVar3 >> 0x18 | (uVar3 & 0xff0000) >> 8 | (uVar3 & 0xff00) << 8 | uVar3 << 0x18;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar6 + 9;
    uStack_64 = *(uint *)(lVar4 + 5 + lVar6);
    *(long *)(param_2 + 0x18) = lVar6 + 0xd;
    uStack_60 = *(uint *)(lVar4 + 9 + lVar6);
    *(long *)(param_2 + 0x18) = lVar6 + 0x11;
    uStack_5c = *(uint *)(lVar4 + 0xd + lVar6);
  }
  lVar6 = *param_1;
  uVar1 = *(undefined4 *)(lVar6 + 0x500);
  uVar5 = *DAT_015da128;
  uVar2 = *(undefined4 *)
           ((long)&__DT_RELA[0x548].r_addend +
           *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar6) + 0x2f0));
  if (*(long *)((long)&__DT_RELA[0xd40].r_addend + lVar6) != 0) {
    lVar6 = FUN_00198b40();
    plVar8 = *(long **)(lVar6 + 8);
    if (*(long **)(lVar6 + 8) != (long *)0x0) goto LAB_000f233d;
  }
  plVar8 = DAT_013962b8;
LAB_000f233d:
  if (*(code **)(*plVar8 + 0x1e0) == FUN_000f21b0) {
    if (plVar8[0x20d] == 0) {
      FUN_00c29ca0(local_58,0x28,0);
      lVar6 = plVar8[0x255];
      *(uint *)(local_50 + 2) = (uint)bVar7;
      *(undefined1 *)((long)local_50 + 0x14) = 0;
      *local_50 = CONCAT44(uStack_64,local_68);
      local_50[1] = CONCAT44(uStack_5c,uStack_60);
      local_50[3] = uVar5;
      *(undefined4 *)(local_50 + 4) = uVar1;
      *(undefined4 *)((long)local_50 + 0x24) = uVar2;
      plVar8[0x255] = (long)local_50;
      if (lVar6 != 0) {
        eastl__basic_string();
      }
    }
    else {
      FUN_00b0d5b0(plVar8,&local_68,bVar7,0,uVar5,uVar1,uVar2,0);
    }
  }
  else {
    (**(code **)(*plVar8 + 0x1e0))(plVar8,&local_68,bVar7,0,uVar5,uVar1,uVar2,0);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL` @ 000f25d0
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::ZoneUpdates::MAP_PROJANIM_FULL(long *param_1,long param_2)

{
  undefined1 uVar1;
  char cVar2;
  char cVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  byte bVar7;
  char cVar8;
  byte bVar9;
  byte bVar10;
  char cVar11;
  char cVar12;
  char cVar13;
  char cVar14;
  char cVar15;
  byte bVar16;
  byte bVar17;
  byte bVar18;
  long lVar19;
  long lVar20;
  int iVar21;
  byte bVar22;
  ushort uVar23;
  uint uVar24;
  uint alpha;
  int iVar25;
  ushort uVar26;
  bool bVar27;
  ushort local_58;
  ushort local_56;
  ushort local_4e;
  
                    /* ServerProt op 98 (size 25), CONF:MEDIUM. ProjectileList::Add with full
                       src/dst coords, speeds, height, alpha. Projectile/proj-anim spawn (large
                       form). */
  lVar19 = *(long *)(param_2 + 0x18);
  bVar27 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar19 + 2;
  lVar20 = *(long *)(param_2 + 0x10);
  uVar1 = *(undefined1 *)(lVar20 + 1 + lVar19);
  cVar2 = *(char *)(lVar20 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 4;
  uVar23 = *(ushort *)(lVar20 + 2 + lVar19);
  local_58 = CONCAT11(local_58._1_1_,uVar1);
  if (bVar27) {
    uVar23 = uVar23 << 8 | uVar23 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar19 + 5;
  cVar3 = *(char *)(lVar20 + 4 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 7;
  uVar1 = *(undefined1 *)(lVar20 + 6 + lVar19);
  bVar4 = *(byte *)(lVar20 + 5 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 10;
  bVar5 = *(byte *)(lVar20 + 8 + lVar19);
  bVar6 = *(byte *)(lVar20 + 7 + lVar19);
  bVar7 = *(byte *)(lVar20 + 9 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0xb;
  cVar8 = *(char *)(lVar20 + 10 + lVar19);
  local_56 = CONCAT11(local_56._1_1_,uVar1);
  *(long *)(param_2 + 0x18) = lVar19 + 0xd;
  bVar9 = *(byte *)(lVar20 + 0xc + lVar19);
  bVar10 = *(byte *)(lVar20 + 0xb + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0xe;
  cVar11 = *(char *)(lVar20 + 0xd + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x10;
  uVar26 = (ushort)bVar9 * 0x100 + (ushort)bVar10;
  uVar1 = *(undefined1 *)(lVar20 + 0xf + lVar19);
  cVar12 = *(char *)(lVar20 + 0xe + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x12;
  cVar13 = *(char *)(lVar20 + 0x10 + lVar19);
  local_4e = CONCAT11(local_4e._1_1_,uVar1);
  bVar9 = *(byte *)(lVar20 + 0x11 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x13;
  cVar14 = *(char *)(lVar20 + 0x12 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x14;
  cVar15 = *(char *)(lVar20 + 0x13 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x15;
  bVar10 = *(byte *)(lVar20 + 0x14 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x16;
  bVar22 = *(byte *)(lVar20 + 0x15 + lVar19);
  *(long *)(param_2 + 0x18) = lVar19 + 0x19;
  alpha = (uint)bVar22;
  if (alpha == 0xff) {
    alpha = 0xffffffff;
  }
  if (uVar26 != 0xffff) {
    bVar22 = 0x80 - cVar14;
    iVar25 = (int)cVar3;
    iVar21 = iVar25 << 4;
    if ((bVar22 & 2) != 0) {
      iVar21 = iVar25 * 4;
    }
    bVar16 = *(byte *)(lVar20 + 0x17 + lVar19);
    bVar17 = *(byte *)(lVar20 + 0x16 + lVar19);
    bVar18 = *(byte *)(lVar20 + 0x18 + lVar19);
    uVar24 = (uint)bVar9 * 0x100 + (uint)(byte)(cVar13 + 0x80) & 0xffff;
    if (DAT_013942d0 == '\0') {
      iVar25 = __cxa_guard_acquire(&DAT_013942d0);
      if (iVar25 != 0) {
        _DAT_013942c0 = 0;
        DAT_013942c8 = 0;
        __cxa_guard_release(&DAT_013942d0);
      }
    }
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar26,
                        (uint)(byte)(cVar2 + 0x80) + (uint)local_58 * 0x100 & 0xffff,
                        (uint)local_4e * 0x100 + (uint)(byte)(cVar12 + 0x80) & 0xffff,
                        (uint)bVar5 * 0x10000 + (uint)bVar6 * 0x100 + (uint)bVar7,
                        (uint)bVar16 * 0x100 + (uint)bVar17 * 0x10000 + (uint)bVar18,bVar22 & 1,
                        iVar21,(int)(char)(-0x80 - cVar11) << 4,(uint)uVar23 << 8,uVar24 << 8,
                        ((uVar23 - 0x80) + (uint)bVar10) * 0x100,((int)cVar8 + uVar24) * 0x100,
                        (undefined4 *)&DAT_013942c0,(undefined4 *)&DAT_013942c0,
                        ((uint)local_56 * 0x100 + (uint)bVar4 & 0xffff) << 2,alpha,
                        (uint)(byte)(0x80 - cVar15));
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC` @ 000f2900
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::ZoneUpdates::PROJANIM_SPECIFIC(long *param_1,long param_2)

{
  int iVar1;
  int iVar2;
  byte bVar3;
  byte bVar4;
  char cVar5;
  char cVar6;
  byte bVar7;
  byte bVar8;
  byte bVar9;
  byte bVar10;
  byte bVar11;
  byte bVar12;
  byte bVar13;
  byte bVar14;
  ushort uVar15;
  long lVar16;
  long lVar17;
  int iVar18;
  uint uVar19;
  ushort *puVar20;
  uint alpha;
  ushort uVar21;
  uint uVar22;
  uint sourceZ;
  ushort uVar23;
  
  iVar18 = DAT_013942b0;
  iVar2 = DAT_013942ac;
  lVar16 = *(long *)(param_2 + 0x18);
  lVar17 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar16 + 1;
  bVar3 = *(byte *)(lVar17 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 2;
  bVar4 = *(byte *)(lVar17 + 1 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 3;
  cVar5 = *(char *)(lVar17 + 2 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 4;
  cVar6 = *(char *)(lVar17 + 3 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 7;
  bVar7 = *(byte *)(lVar17 + 5 + lVar16);
  bVar8 = *(byte *)(lVar17 + 4 + lVar16);
  bVar9 = *(byte *)(lVar17 + 6 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 10;
  bVar10 = *(byte *)(lVar17 + 8 + lVar16);
  bVar11 = *(byte *)(lVar17 + 7 + lVar16);
  bVar12 = *(byte *)(lVar17 + 9 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 0xc;
  iVar1 = DAT_01050dc0;
  uVar23 = *(ushort *)(lVar17 + 10 + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 0xd;
  bVar13 = *(byte *)(lVar17 + 0xc + lVar16);
  uVar21 = uVar23 << 8 | uVar23 >> 8;
  if (iVar1 != 0x3020100) {
    uVar21 = uVar23;
  }
  *(long *)(param_2 + 0x18) = lVar16 + 0xe;
  uVar19 = (uint)bVar13;
  if ((bVar4 & 2) == 0) {
    uVar19 = (uint)bVar13 * 4;
  }
  bVar13 = *(byte *)(lVar17 + 0xd + lVar16);
  *(long *)(param_2 + 0x18) = lVar16 + 0x10;
  puVar20 = (ushort *)(lVar16 + 0x10 + lVar17);
  uVar23 = *(ushort *)(lVar17 + 0xe + lVar16);
  sourceZ = (uint)uVar23;
  if (iVar1 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar16 + 0x12;
    uVar15 = *puVar20;
    *(long *)(param_2 + 0x18) = lVar16 + 0x13;
    bVar14 = *(byte *)(lVar17 + 0x12 + lVar16);
    alpha = (uint)bVar14;
    sourceZ = (uint)(ushort)(uVar23 << 8 | uVar23 >> 8);
    uVar23 = uVar15 << 8 | uVar15 >> 8;
    if (bVar14 == 0xff) {
      *(long *)(param_2 + 0x18) = lVar16 + 0x15;
      alpha = 0xffffffff;
      uVar15 = *(ushort *)(lVar17 + 0x13 + lVar16);
    }
    else {
      *(long *)(param_2 + 0x18) = lVar16 + 0x15;
      uVar15 = *(ushort *)(lVar17 + 0x13 + lVar16);
    }
    uVar22 = (uint)(ushort)(uVar15 << 8 | uVar15 >> 8);
  }
  else {
    *(long *)(param_2 + 0x18) = lVar16 + 0x12;
    uVar23 = *puVar20;
    *(long *)(param_2 + 0x18) = lVar16 + 0x13;
    bVar14 = *(byte *)(lVar17 + 0x12 + lVar16);
    alpha = (uint)bVar14;
    if (bVar14 == 0xff) {
      *(long *)(param_2 + 0x18) = lVar16 + 0x15;
      alpha = 0xffffffff;
      uVar22 = (uint)*(ushort *)(lVar17 + 0x13 + lVar16);
    }
    else {
      *(long *)(param_2 + 0x18) = lVar16 + 0x15;
      uVar22 = (uint)*(ushort *)(lVar17 + 0x13 + lVar16);
    }
  }
  if (uVar21 != 0xffff) {
    iVar1 = (bVar3 & 0xf) + iVar18 * 2;
    iVar2 = ((int)(uint)bVar3 >> 4) + iVar2 * 2;
    if (DAT_01393f50 == '\0') {
      iVar18 = __cxa_guard_acquire(&DAT_01393f50);
      if (iVar18 != 0) {
        _DAT_01393f40 = 0;
        DAT_01393f48 = 0;
        __cxa_guard_release(&DAT_01393f50);
      }
    }
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar21,sourceZ,
                        (uint)uVar23,(uint)bVar8 * 0x10000 + (uint)bVar7 * 0x100 + (uint)bVar9,
                        (uint)bVar11 * 0x10000 + (uint)bVar10 * 0x100 + (uint)bVar12,bVar4 & 1,
                        uVar19 << 2,(uint)bVar13 << 4,iVar2 * 0x100,iVar1 * 0x100,
                        (cVar5 + iVar2) * 0x100,(cVar6 + iVar1) * 0x100,(undefined4 *)&DAT_01393f40,
                        (undefined4 *)&DAT_01393f40,uVar22 << 2,alpha,DAT_013942a8);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_PROJANIM` @ 000f2c30
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::ZoneUpdates::MAP_PROJANIM(long *param_1,long param_2)

{
  char cVar1;
  char cVar2;
  char cVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  char cVar7;
  char cVar8;
  byte bVar9;
  long lVar10;
  long lVar11;
  int iVar12;
  int iVar13;
  uint alpha;
  int iVar14;
  ushort uVar15;
  ushort uVar16;
  ushort *puVar17;
  uint sourceZ;
  ushort uVar18;
  float fVar19;
  float fVar20;
  float fVar21;
  float fVar22;
  
  iVar13 = DAT_013942b0;
  iVar14 = DAT_013942ac;
  lVar10 = *(long *)(param_2 + 0x18);
  lVar11 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar10 + 1;
  cVar1 = *(char *)(lVar11 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 2;
  cVar2 = *(char *)(lVar11 + 1 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 3;
  cVar3 = *(char *)(lVar11 + 2 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 6;
  bVar4 = *(byte *)(lVar11 + 4 + lVar10);
  bVar5 = *(byte *)(lVar11 + 3 + lVar10);
  bVar6 = *(byte *)(lVar11 + 5 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 8;
  iVar12 = DAT_01050dc0;
  uVar18 = *(ushort *)(lVar11 + 6 + lVar10);
  uVar15 = uVar18 << 8 | uVar18 >> 8;
  if (DAT_01050dc0 != 0x3020100) {
    uVar15 = uVar18;
  }
  *(long *)(param_2 + 0x18) = lVar10 + 9;
  cVar7 = *(char *)(lVar11 + 8 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 10;
  cVar8 = *(char *)(lVar11 + 9 + lVar10);
  *(long *)(param_2 + 0x18) = lVar10 + 0xc;
  puVar17 = (ushort *)(lVar10 + 0xc + lVar11);
  uVar18 = *(ushort *)(lVar11 + 10 + lVar10);
  sourceZ = (uint)uVar18;
  if (iVar12 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar10 + 0xe;
    uVar16 = *puVar17;
    *(long *)(param_2 + 0x18) = lVar10 + 0xf;
    bVar9 = *(byte *)(lVar11 + 0xe + lVar10);
    alpha = (uint)bVar9;
    sourceZ = (uint)(ushort)(uVar18 << 8 | uVar18 >> 8);
    uVar18 = uVar16 << 8 | uVar16 >> 8;
    if (bVar9 == 0xff) {
      *(long *)(param_2 + 0x18) = lVar10 + 0x11;
      alpha = 0xffffffff;
      uVar16 = *(ushort *)(lVar11 + 0xf + lVar10);
    }
    else {
      *(long *)(param_2 + 0x18) = lVar10 + 0x11;
      uVar16 = *(ushort *)(lVar11 + 0xf + lVar10);
    }
    uVar16 = uVar16 << 8 | uVar16 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar10 + 0xe;
    uVar18 = *puVar17;
    *(long *)(param_2 + 0x18) = lVar10 + 0xf;
    bVar9 = *(byte *)(lVar11 + 0xe + lVar10);
    alpha = (uint)bVar9;
    if (bVar9 == 0xff) {
      *(long *)(param_2 + 0x18) = lVar10 + 0x11;
      alpha = 0xffffffff;
      uVar16 = *(ushort *)(lVar11 + 0xf + lVar10);
    }
    else {
      *(long *)(param_2 + 0x18) = lVar10 + 0x11;
      uVar16 = *(ushort *)(lVar11 + 0xf + lVar10);
    }
  }
  *(long *)(param_2 + 0x18) = lVar10 + 0x14;
  if (uVar15 != 0xffff) {
    iVar13 = iVar13 + ((int)cVar1 & 7U);
    iVar14 = iVar14 + ((uint)(int)cVar1 >> 3 & 7);
    fVar19 = ((float)iVar13 + DAT_00cb6ae0) * DAT_00cb6ae4;
    fVar20 = ((float)iVar14 + DAT_00cb6ae0) * DAT_00cb6ae4;
    fVar21 = ((float)(iVar14 + cVar2) + DAT_00cb6ae0) * DAT_00cb6ae4;
    fVar22 = ((float)(iVar13 + cVar3) + DAT_00cb6ae0) * DAT_00cb6ae4;
    if (DAT_01393f38 == '\0') {
      iVar12 = __cxa_guard_acquire(&DAT_01393f38);
      if (iVar12 != 0) {
        _DAT_01393f28 = 0;
        DAT_01393f30 = 0;
        __cxa_guard_release(&DAT_01393f38);
      }
    }
    ProjectileList::Add(*(long *)((long)&__DT_RELA[0xcfd].r_addend + *param_1),(uint)uVar15,sourceZ,
                        (uint)uVar18,DAT_015bf224,
                        (uint)bVar6 + (uint)bVar5 * 0x10000 + (uint)bVar4 * 0x100,
                        (byte)(cVar1 >> 7) >> 7,(int)cVar7 << 4,(int)cVar8 << 4,(int)fVar20,
                        (int)fVar19,(int)fVar21,(int)fVar22,(undefined4 *)&DAT_01393f28,
                        (undefined4 *)&DAT_01393f28,(uint)uVar16 << 2,alpha,DAT_013942a8);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::OBJ_REVEAL` @ 000f37b0
```c

undefined * jag::packethandlers::ZoneUpdates::OBJ_REVEAL(long *param_1,long param_2)

{
  long lVar1;
  undefined8 *puVar2;
  uint *puVar3;
  byte bVar4;
  long lVar5;
  long lVar6;
  uint *puVar7;
  int iVar8;
  undefined8 *puVar9;
  undefined8 *puVar10;
  uint *puVar11;
  ushort uVar12;
  ushort *puVar13;
  uint uVar14;
  ushort uVar15;
  ushort uVar16;
  uint uVar17;
  uint uVar18;
  uint uVar19;
  undefined8 *puVar20;
  
  iVar8 = DAT_013942a8;
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  lVar1 = lVar5 + 5;
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  bVar4 = *(byte *)(lVar6 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  puVar13 = (ushort *)(lVar5 + 3 + lVar6);
  uVar16 = *(ushort *)(lVar6 + 1 + lVar5);
  uVar14 = (bVar4 & 7) + DAT_013942b0;
  uVar18 = (bVar4 >> 4 & 7) + DAT_013942ac;
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar12 = *puVar13;
    uVar16 = uVar16 << 8 | uVar16 >> 8;
    *(long *)(param_2 + 0x18) = lVar5 + 7;
    uVar15 = *(ushort *)(lVar6 + lVar1);
    uVar12 = uVar12 << 8 | uVar12 >> 8;
    uVar15 = uVar15 << 8 | uVar15 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1;
    uVar12 = *puVar13;
    *(long *)(param_2 + 0x18) = lVar5 + 7;
    uVar15 = *(ushort *)(lVar6 + lVar1);
  }
  uVar19 = (uint)uVar12;
  uVar17 = (uint)uVar16;
  lVar1 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + *param_1);
  puVar9 = *(undefined8 **)(lVar1 + 0x18);
  puVar2 = (undefined8 *)(lVar1 + 8);
  puVar20 = puVar2;
  if (puVar9 != (undefined8 *)0x0) {
    do {
      if ((*(int *)(puVar9 + 4) < iVar8) ||
         ((*(int *)(puVar9 + 4) <= iVar8 &&
          ((*(uint *)((long)puVar9 + 0x24) < uVar18 ||
           ((*(uint *)((long)puVar9 + 0x24) <= uVar18 && (*(uint *)(puVar9 + 5) < uVar14)))))))) {
        puVar10 = (undefined8 *)*puVar9;
      }
      else {
        puVar10 = (undefined8 *)puVar9[1];
        puVar20 = puVar9;
      }
      puVar9 = puVar10;
    } while (puVar10 != (undefined8 *)0x0);
    if (((puVar2 != puVar20) && (*(int *)(puVar20 + 4) <= iVar8)) &&
       ((*(int *)(puVar20 + 4) < iVar8 ||
        ((*(uint *)((long)puVar20 + 0x24) <= uVar18 &&
         ((uVar18 != *(uint *)((long)puVar20 + 0x24) || (*(uint *)(puVar20 + 5) <= uVar14)))))))) {
      lVar1 = puVar20[7];
      puVar7 = *(uint **)(lVar1 + 0x70);
      if (puVar7 != *(uint **)(lVar1 + 0x78)) {
        puVar11 = puVar7 + 1;
        puVar3 = puVar7 + (((ulong)((long)*(uint **)(lVar1 + 0x78) - (long)(puVar7 + 0x24)) >> 4) *
                           0xe38e38e38e38e39 & 0xfffffffffffffff) * 0x24 + 0x25;
        uVar14 = (int)((ulong)((long)puVar3 + (-0x90 - (long)puVar11)) >> 4) * 0x38e38e39 + 1U & 7;
        uVar18 = (uint)uVar15;
        if (uVar14 != 0) {
          if (uVar14 != 1) {
            if (uVar14 != 2) {
              if (uVar14 != 3) {
                if (uVar14 != 4) {
                  if (uVar14 != 5) {
                    if (uVar14 != 6) {
                      if ((*puVar7 == uVar17) && (*puVar11 == uVar19)) {
                        *puVar11 = uVar18;
                        *(undefined1 *)(lVar1 + 0xb8) = 0;
                      }
                      puVar11 = puVar7 + 0x25;
                    }
                    if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
                      *puVar11 = uVar18;
                      *(undefined1 *)(lVar1 + 0xb8) = 0;
                    }
                    puVar11 = puVar11 + 0x24;
                  }
                  if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
                    *puVar11 = uVar18;
                    *(undefined1 *)(lVar1 + 0xb8) = 0;
                  }
                  puVar11 = puVar11 + 0x24;
                }
                if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
                  *puVar11 = uVar18;
                  *(undefined1 *)(lVar1 + 0xb8) = 0;
                }
                puVar11 = puVar11 + 0x24;
              }
              if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
                *puVar11 = uVar18;
                *(undefined1 *)(lVar1 + 0xb8) = 0;
              }
              puVar11 = puVar11 + 0x24;
            }
            if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
              *puVar11 = uVar18;
              *(undefined1 *)(lVar1 + 0xb8) = 0;
            }
            puVar11 = puVar11 + 0x24;
          }
          if ((puVar11[-1] == uVar17) && (*puVar11 == uVar19)) {
            *puVar11 = uVar18;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          puVar11 = puVar11 + 0x24;
          if (puVar3 == puVar11) goto LAB_000f39da;
        }
        do {
          if (puVar11[-1] == uVar17) {
            if (*puVar11 == uVar19) {
              *puVar11 = uVar18;
              *(undefined1 *)(lVar1 + 0xb8) = 0;
            }
            uVar14 = puVar11[0x23];
          }
          else {
            uVar14 = puVar11[0x23];
          }
          if ((uVar14 == uVar17) && (puVar11[0x24] == uVar19)) {
            puVar11[0x24] = uVar18;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          if ((puVar11[0x47] == uVar17) && (puVar11[0x48] == uVar19)) {
            puVar11[0x48] = uVar18;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          uVar14 = (uint)uVar15;
          if ((puVar11[0x6b] == uVar17) && (puVar11[0x6c] == uVar19)) {
            puVar11[0x6c] = uVar14;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          if ((puVar11[0x8f] == uVar17) && (puVar11[0x90] == uVar19)) {
            puVar11[0x90] = uVar14;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          if ((puVar11[0xb3] == uVar17) && (puVar11[0xb4] == uVar19)) {
            puVar11[0xb4] = uVar14;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          if ((puVar11[0xd7] == uVar17) && (puVar11[0xd8] == uVar19)) {
            puVar11[0xd8] = uVar14;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          if ((puVar11[0xfb] == uVar17) && (puVar11[0xfc] == uVar19)) {
            puVar11[0xfc] = uVar18;
            *(undefined1 *)(lVar1 + 0xb8) = 0;
          }
          puVar11 = puVar11 + 0x120;
        } while (puVar3 != puVar11);
      }
    }
  }
LAB_000f39da:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::RESET_CLIENT_STATE` @ 000f4080
```c

undefined * jag::packethandlers::ClientState::RESET_CLIENT_STATE(long *param_1)

{
  long lVar1;
  long *plVar2;
  undefined1 (*pauVar3) [16];
  
  lVar1 = *(long *)((long)&__DT_RELA[0xcfb].r_offset + *param_1);
  pauVar3 = (undefined1 (*) [16])FUN_00c29480(0x38);
  *(undefined8 *)pauVar3[3] = 0;
  *(undefined8 *)pauVar3[2] = 0;
  *(undefined8 *)(pauVar3[2] + 8) = 0x400000003f800000;
  *pauVar3 = (undefined1  [16])0x0;
  *(undefined4 *)pauVar3[3] = 0;
  *(undefined ***)*pauVar3 = &PTR_FUN_0136b598;
  *(undefined8 **)pauVar3[1] = &DAT_01393d40;
  plVar2 = *(long **)((long)&__DT_RELA[0xff].r_addend + lVar1);
  *(undefined8 *)(pauVar3[1] + 8) = 1;
  *(undefined1 (**) [16])((long)&__DT_RELA[0xff].r_addend + lVar1) = pauVar3;
  if (plVar2 != (long *)0x0) {
    (**(code **)(*plVar2 + 8))();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLEAR_PENDING_UPDATES` @ 000f4110
```c

undefined * jag::packethandlers::ClientState::CLEAR_PENDING_UPDATES(long *param_1)

{
  long lVar1;
  byte bVar2;
  long lVar3;
  long lVar4;
  undefined8 uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  uint uVar9;
  ulong uVar10;
  
  lVar3 = *(long *)((long)&__DT_RELA[0xcfb].r_offset + *param_1);
  lVar4 = *(long *)((long)&__DT_RELA[0xf7].r_info + lVar3);
  lVar8 = *(long *)((long)&__DT_RELA[0xf7].r_addend + lVar3);
  if (lVar4 == lVar8) {
    return &DAT_015d3620;
  }
  if ((ulong)*(uint *)((long)&__DT_RELA[0xf8].r_info + lVar3) <
      (ulong)((lVar8 - lVar4 >> 3) * -0x3333333333333333)) {
    return &DAT_015d3620;
  }
  lVar6 = lVar4 + 0x28;
  lVar1 = lVar4 + 0x28 + (lVar8 - lVar6 & 0xfffffffffffffff8U);
  uVar10 = (ulong)(lVar1 - lVar6) >> 3;
  uVar9 = (uint)uVar10 & 3;
  lVar8 = lVar4;
  lVar7 = lVar6;
  if ((uVar10 & 3) == 0) goto LAB_000f425e;
  bVar2 = *(byte *)(lVar4 + 0x20);
  if (bVar2 == 0xff) {
LAB_000f41ae:
    *(undefined1 *)(lVar4 + 0x20) = 4;
  }
  else if (bVar2 != 4) {
    (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar4 + 8);
    goto LAB_000f41ae;
  }
  lVar7 = lVar4 + 0x50;
  lVar8 = lVar6;
  if (uVar9 == 1) goto LAB_000f425e;
  lVar8 = lVar7;
  if (uVar9 != 2) {
    bVar2 = *(byte *)(lVar4 + 0x48);
    if (bVar2 == 0xff) {
LAB_000f42d9:
      *(undefined1 *)(lVar4 + 0x48) = 4;
    }
    else if (bVar2 != 4) {
      (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar4 + 0x30);
      goto LAB_000f42d9;
    }
    lVar8 = lVar4 + 0x78;
    lVar6 = lVar7;
  }
  bVar2 = *(byte *)(lVar6 + 0x20);
  if (bVar2 == 0xff) {
LAB_000f41e6:
    *(undefined1 *)(lVar6 + 0x20) = 4;
  }
  else if (bVar2 != 4) {
    (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar6 + 8);
    goto LAB_000f41e6;
  }
  lVar7 = lVar8 + 0x28;
LAB_000f425e:
  do {
    bVar2 = *(byte *)(lVar8 + 0x20);
    if (bVar2 == 0xff) {
LAB_000f4277:
      *(undefined1 *)(lVar8 + 0x20) = 4;
    }
    else if (bVar2 != 4) {
      (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar8 + 8);
      goto LAB_000f4277;
    }
    if (lVar7 == lVar1) {
      uVar5 = *(undefined8 *)((long)&__DT_RELA[0xf7].r_info + lVar3);
      *(undefined4 *)((long)&__DT_RELA[0xf8].r_info + lVar3) = 0;
      *(undefined8 *)((long)&__DT_RELA[0xf7].r_addend + lVar3) = uVar5;
      return &DAT_015d3620;
    }
    bVar2 = *(byte *)(lVar7 + 0x20);
    if (bVar2 == 0xff) {
LAB_000f421a:
      *(undefined1 *)(lVar7 + 0x20) = 4;
    }
    else if (bVar2 != 4) {
      (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar7 + 8);
      goto LAB_000f421a;
    }
    bVar2 = *(byte *)(lVar7 + 0x48);
    if (bVar2 == 0xff) {
LAB_000f4237:
      *(undefined1 *)(lVar7 + 0x48) = 4;
    }
    else if (bVar2 != 4) {
      (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar7 + 0x30);
      goto LAB_000f4237;
    }
    bVar2 = *(byte *)(lVar7 + 0x70);
    lVar8 = lVar7 + 0x78;
    if (bVar2 == 0xff) {
LAB_000f4256:
      *(undefined1 *)(lVar7 + 0x70) = 4;
    }
    else if (bVar2 != 4) {
      (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar7 + 0x58);
      goto LAB_000f4256;
    }
    lVar7 = lVar7 + 0xa0;
  } while( true );
}


```

## `jag::packethandlers::Rebuild::REBUILD_REGION_HANDLER` @ 000f6fe0
```c

void jag::packethandlers::Rebuild::REBUILD_REGION_HANDLER(long param_1,int param_2,int param_3)

{
  long lVar1;
  long lVar2;
  
  if (param_3 == 10) {
    game::ServerConnection::CloseConnection(*(undefined8 *)(param_1 + 0x28));
    game::ServerConnection::CloseConnection(*(undefined8 *)(param_1 + 0x18));
    return;
  }
  if (param_3 != 0x14) {
    if (param_3 == 0x1e) {
      game::ServerConnection::CloseConnection(*(undefined8 *)(param_1 + 0x28));
      if (param_2 == 0x23) {
        lVar1 = *(long *)(param_1 + 0x18);
        lVar2 = *(long *)(lVar1 + 0x10);
        *(undefined8 *)(lVar1 + 0x18) = 0;
        *(undefined8 *)(lVar1 + 0x10) = 0;
        if (lVar2 != 0) {
          ref_counter_base::DecRef();
          return;
        }
      }
      else {
        *(undefined1 *)(param_1 + 0x49) = 1;
      }
    }
    return;
  }
  game::ServerConnection::CloseConnection(*(undefined8 *)(param_1 + 0x18));
  return;
}


```

## `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS` @ 000f7bb0
```c

/* Setting prototype: undefined * UPDATE_ZONE_FULL_FOLLOWS(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS(long *thisPtr,long packet)

{
  long lVar1;
  uint uVar2;
  uint uVar3;
  int *piVar4;
  long **pplVar5;
  char cVar6;
  char cVar7;
  uint uVar8;
  long lVar9;
  long *plVar10;
  long *plVar11;
  long *plVar12;
  long lVar13;
  long *plVar14;
  long *plVar15;
  uint uVar16;
  long lVar17;
  byte bVar18;
  long lVar19;
  uint uVar20;
  long lVar21;
  long lVar22;
  long **pplVar23;
  size_t __n;
  uint uVar24;
  long *plVar25;
  ulong uVar26;
  uint uVar27;
  int iVar28;
  undefined1 *puVar29;
  long lVar30;
  long *plVar31;
  long lVar32;
  bool bVar33;
  bool bVar34;
  long **local_3d0;
  long **local_3c8;
  undefined4 local_388;
  uint local_384;
  uint local_380;
  uint local_37c;
  undefined1 local_378;
  undefined8 local_368;
  long **pplStack_360;
  long **local_358;
  long **local_348;
  long *local_340 [96];
  long *local_40 [2];
  
  lVar13 = *(long *)(packet + 0x18);
  lVar32 = *(long *)(packet + 0x10);
  pplVar23 = local_340;
  lVar17 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *thisPtr);
  lVar9 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + *thisPtr);
  *(long *)(packet + 0x18) = lVar13 + 1;
  cVar6 = *(char *)(lVar32 + lVar13);
  *(long *)(packet + 0x18) = lVar13 + 2;
  cVar7 = *(char *)(lVar32 + 1 + lVar13);
  iVar28 = *(int *)(lVar17 + 0x60c);
  *(long *)(packet + 0x18) = lVar13 + 3;
  local_358 = local_40;
  bVar18 = cVar6 + 0x80;
  plVar14 = (long *)(lVar9 + 8);
  uVar2 = iVar28 + (char)(-0x80 - cVar7) * 8;
  uVar20 = (uint)bVar18;
  uVar3 = *(int *)(lVar17 + 0x608) + *(char *)(lVar32 + 2 + lVar13) * 8;
  uVar16 = uVar2 + 8;
  uVar27 = uVar3 + 8;
  plVar10 = *(long **)(lVar9 + 0x10);
  DAT_013942a8 = uVar20;
  DAT_013942ac = uVar3;
  DAT_013942b0 = uVar2;
  local_368 = pplVar23;
  pplStack_360 = pplVar23;
  local_348 = pplVar23;
  local_40[0] = plVar14;
  if (plVar14 == plVar10) {
LAB_000f7f9a:
    if (pplVar23 != local_348) {
      HeapInterface::Free();
    }
  }
  else {
LAB_000f7ce4:
    do {
      if (((((uint)bVar18 == *(uint *)(plVar10 + 4)) && (uVar3 <= *(uint *)((long)plVar10 + 0x24)))
          && (uVar2 <= *(uint *)(plVar10 + 5) && *(uint *)((long)plVar10 + 0x24) < uVar27)) &&
         (*(uint *)(plVar10 + 5) < uVar16)) {
        (**(code **)(*(long *)plVar10[7] + 0xe0))();
        lVar13 = plVar10[4];
        iVar28 = *(int *)((long)plVar10 + 0x24);
        lVar32 = plVar10[5];
        if (pplStack_360 < local_358) {
          *(int *)pplStack_360 = (int)lVar13;
          *(int *)((long)pplStack_360 + 4) = iVar28;
          *(int *)(pplStack_360 + 1) = (int)lVar32;
          pplStack_360 = (long **)((long)pplStack_360 + 0xc);
          plVar10 = (long *)eastl::rbtree::next(plVar10);
          if (plVar14 == plVar10) break;
          goto LAB_000f7ce4;
        }
        lVar17 = (long)pplStack_360 - (long)local_368 >> 2;
        if (lVar17 * -0x5555555555555555 == 0) {
          lVar17 = 0xc;
LAB_000f8cca:
          local_3d0 = (long **)FUN_00c29480(lVar17);
          local_3c8 = (long **)(lVar17 + (long)local_3d0);
        }
        else {
          if (lVar17 * 0x5555555555555556 != 0) {
            lVar17 = lVar17 << 3;
            goto LAB_000f8cca;
          }
          local_3c8 = (long **)0x0;
          local_3d0 = (long **)0x0;
        }
        pplVar23 = local_3d0;
        if (pplStack_360 != local_368) {
          __n = (long)pplStack_360 - (long)local_368;
          memmove(local_3d0,local_368,__n);
          pplVar23 = (long **)((long)local_3d0 + __n);
        }
        *(int *)((long)pplVar23 + 4) = iVar28;
        *(int *)pplVar23 = (int)lVar13;
        *(int *)(pplVar23 + 1) = (int)lVar32;
        if ((local_368 != (long **)0x0) && (local_348 != local_368)) {
          HeapInterface::Free(local_368);
        }
        local_368 = local_3d0;
        local_358 = local_3c8;
        pplStack_360 = (long **)((long)pplVar23 + 0xc);
      }
      plVar10 = (long *)eastl::rbtree::next(plVar10);
    } while (plVar14 != plVar10);
    pplVar23 = pplStack_360;
    if (local_368 != pplStack_360) {
      pplVar5 = (long **)((long)local_368 +
                         ((long)pplStack_360 - ((long)local_368 + 0xc) & 0xfffffffffffffffcU) + 0xc)
      ;
      pplVar23 = local_368;
      local_3c8 = (long **)((long)local_368 + 0xc);
      do {
        plVar10 = local_40[0];
        plVar14 = (long *)local_40[0][2];
        if (plVar14 == (long *)0x0) {
          bVar34 = (long *)local_40[0][1] != local_40[0];
LAB_000f7f58:
          if (!bVar34) {
LAB_000f7f5d:
            FUN_00487de0(plVar10,plVar14);
            *plVar10 = (long)plVar10;
            plVar10[1] = (long)plVar10;
            plVar10[2] = 0;
            *(undefined1 *)(plVar10 + 3) = 0;
            plVar10[4] = 0;
          }
        }
        else {
          iVar28 = *(int *)pplVar23;
          plVar15 = plVar14;
          plVar12 = local_40[0];
          do {
            while ((plVar25 = plVar15, plVar31 = plVar14, plVar11 = local_40[0],
                   iVar28 < (int)plVar25[4] ||
                   ((iVar28 <= (int)plVar25[4] &&
                    ((*(uint *)((long)pplVar23 + 4) < *(uint *)((long)plVar25 + 0x24) ||
                     ((*(uint *)((long)pplVar23 + 4) == *(uint *)((long)plVar25 + 0x24) &&
                      (*(uint *)(pplVar23 + 1) < *(uint *)(plVar25 + 5)))))))))) {
              plVar15 = (long *)plVar25[1];
              plVar12 = plVar25;
              if ((long *)plVar25[1] == (long *)0x0) goto LAB_000f7e5c;
            }
            plVar15 = (long *)*plVar25;
          } while ((long *)*plVar25 != (long *)0x0);
LAB_000f7e5c:
          do {
            plVar15 = plVar31;
            if (((int)plVar15[4] < iVar28) ||
               (((int)plVar15[4] <= iVar28 &&
                ((*(uint *)((long)plVar15 + 0x24) < *(uint *)((long)pplVar23 + 4) ||
                 ((*(uint *)((long)plVar15 + 0x24) == *(uint *)((long)pplVar23 + 4) &&
                  (*(uint *)(plVar15 + 5) < *(uint *)(pplVar23 + 1))))))))) {
              plVar31 = (long *)*plVar15;
              plVar15 = plVar11;
              if (plVar31 == (long *)0x0) break;
              goto LAB_000f7e5c;
            }
            plVar31 = (long *)plVar15[1];
            plVar11 = plVar15;
          } while ((long *)plVar15[1] != (long *)0x0);
          plVar31 = (long *)local_40[0][1];
          bVar33 = local_40[0] != plVar12;
          bVar34 = plVar31 != plVar15 || bVar33;
          plVar11 = plVar15;
          if (plVar12 == plVar15) goto LAB_000f7f58;
          do {
            plVar11 = (long *)eastl::rbtree::next(plVar11);
          } while (plVar11 != plVar12);
          if (plVar31 == plVar15 && !bVar33) goto LAB_000f7f5d;
          do {
            while( true ) {
              plVar10[4] = plVar10[4] + -1;
              plVar12 = (long *)eastl::rbtree::next(plVar15);
              FUN_00aa4780(plVar15,plVar10);
              plVar14 = (long *)plVar15[6];
              if (plVar14 != (long *)0x0) break;
LAB_000f7eb0:
              HeapInterface::Free(plVar15);
              plVar15 = plVar12;
              if (plVar11 == plVar12) goto LAB_000f7f30;
            }
            LOCK();
            plVar31 = plVar14 + 1;
            lVar13 = *plVar31;
            *(int *)plVar31 = (int)*plVar31 + -1;
            UNLOCK();
            if ((int)lVar13 != 1) goto LAB_000f7eb0;
            (**(code **)(*plVar14 + 0x10))(plVar14);
            LOCK();
            piVar4 = (int *)((long)plVar14 + 0xc);
            iVar28 = *piVar4;
            *piVar4 = *piVar4 + -1;
            UNLOCK();
            if (iVar28 != 1) goto LAB_000f7eb0;
            (**(code **)(*plVar14 + 0x18))(plVar14);
            HeapInterface::Free(plVar15);
            plVar15 = plVar12;
          } while (plVar11 != plVar12);
        }
LAB_000f7f30:
        pplVar23 = local_368;
        if (local_3c8 == pplVar5) break;
        pplVar23 = local_3c8;
        local_3c8 = (long **)((long)local_3c8 + 0xc);
      } while( true );
    }
    if (pplVar23 != (long **)0x0) goto LAB_000f7f9a;
  }
  local_388 = 0xffffffff;
  local_378 = 0;
  local_368 = (long **)CONCAT44(uVar20,0xffffffff);
  pplStack_360 = (long **)CONCAT44(uVar16,uVar27);
  local_358 = (long **)((ulong)local_358 & 0xffffffffffffff00);
  local_384 = uVar20;
  local_380 = uVar3;
  local_37c = uVar2;
  FUN_00af8340(*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_388,&local_368);
  lVar13 = *thisPtr;
  plVar14 = *(long **)((long)&__DT_RELA[0xcfa].r_offset + lVar13);
  plVar10 = (long *)plVar14[0xc];
  plVar15 = (long *)plVar14[0xd];
  if (plVar10 != plVar15) {
    uVar26 = 0;
    do {
      iVar28 = (int)uVar26;
      plVar12 = plVar10 + uVar26 * 2;
      lVar13 = plVar12[1];
      if ((((((int)uVar20 <= *(int *)(lVar13 + 8)) && (*(int *)(lVar13 + 8) <= (int)uVar20)) &&
           (uVar3 <= *(uint *)(lVar13 + 0xc))) &&
          ((uVar2 <= *(uint *)(lVar13 + 0x10) && (*(uint *)(lVar13 + 0xc) < uVar27)))) &&
         (*(uint *)(lVar13 + 0x10) < uVar16)) {
        plVar10 = plVar12 + 2;
        if ((plVar10 < plVar15) &&
           (lVar13 = (long)plVar15 - (long)plVar10 >> 4, 0 < (long)plVar15 - (long)plVar10)) {
          uVar24 = (int)lVar13 - 1U & 3;
          plVar15 = plVar12;
          plVar31 = plVar10;
          lVar32 = lVar13;
          if (uVar24 != 0) {
            lVar32 = *plVar12;
            lVar17 = plVar12[2];
            lVar9 = plVar12[3];
            plVar12[2] = 0;
            plVar12[3] = 0;
            *plVar12 = lVar17;
            plVar12[1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            plVar31 = plVar12 + 4;
            lVar32 = lVar13 + -1;
            plVar15 = plVar10;
            if (uVar24 != 1) {
              plVar15 = plVar31;
              if (uVar24 != 2) {
                lVar32 = *plVar10;
                lVar17 = plVar12[4];
                lVar9 = plVar12[5];
                plVar12[4] = 0;
                plVar12[5] = 0;
                *plVar10 = lVar17;
                plVar12[3] = lVar9;
                if (lVar32 != 0) {
                  ref_counter_base::DecRef();
                }
                lVar32 = lVar13 + -2;
                plVar15 = plVar12 + 6;
                plVar10 = plVar31;
              }
              lVar13 = *plVar10;
              lVar17 = plVar10[2];
              lVar9 = plVar10[3];
              plVar10[2] = 0;
              plVar10[3] = 0;
              *plVar10 = lVar17;
              plVar10[1] = lVar9;
              if (lVar13 != 0) {
                ref_counter_base::DecRef();
              }
              lVar32 = lVar32 + -1;
              plVar31 = plVar15 + 2;
            }
          }
          while( true ) {
            lVar13 = *plVar15;
            lVar17 = plVar15[2];
            lVar9 = plVar15[3];
            plVar15[2] = 0;
            plVar15[3] = 0;
            *plVar15 = lVar17;
            plVar15[1] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            if (lVar32 == 1) break;
            lVar13 = *plVar31;
            lVar17 = plVar31[2];
            lVar9 = plVar31[3];
            plVar31[2] = 0;
            plVar31[3] = 0;
            *plVar31 = lVar17;
            plVar31[1] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            lVar13 = plVar31[2];
            lVar17 = plVar31[4];
            lVar9 = plVar31[5];
            plVar31[4] = 0;
            plVar31[5] = 0;
            plVar31[2] = lVar17;
            plVar31[3] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            lVar13 = plVar31[6];
            plVar31[6] = 0;
            plVar15 = plVar31 + 6;
            lVar17 = plVar31[4];
            lVar9 = plVar31[7];
            plVar31[7] = 0;
            plVar31[4] = lVar13;
            plVar31[5] = lVar9;
            if (lVar17 != 0) {
              ref_counter_base::DecRef();
            }
            plVar31 = plVar31 + 8;
            lVar32 = lVar32 + -4;
          }
          plVar15 = (long *)plVar14[0xd];
        }
        plVar14[0xd] = (long)(plVar15 + -2);
        if (plVar15[-2] != 0) {
          ref_counter_base::DecRef();
        }
        plVar10 = (long *)plVar14[0xc];
        plVar15 = (long *)plVar14[0xd];
        iVar28 = iVar28 + -1;
      }
      uVar26 = (ulong)(iVar28 + 1);
    } while (uVar26 < (ulong)((long)plVar15 - (long)plVar10 >> 4));
    lVar13 = *thisPtr;
    plVar14 = *(long **)((long)&__DT_RELA[0xcfa].r_offset + lVar13);
  }
  lVar13 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar13);
  (**(code **)(*plVar14 + 0x10))(plVar14,lVar13 + 0x608,*(undefined4 *)(lVar13 + 0x610));
  lVar13 = game::SceneManager::GetActiveWorld
                     (*(undefined8 *)((long)&__DT_RELA[0xd02].r_info + *thisPtr));
  uVar8 = DAT_013942b0;
  uVar24 = DAT_013942ac;
  if (*(long *)(lVar13 + 8) == 0) goto LAB_000f8c81;
  lVar13 = FUN_0061c2c0(*(long *)(lVar13 + 8),DAT_013942ac >> 6,DAT_013942b0 >> 6);
  if (*(long *)(lVar13 + 8) == 0) goto LAB_000f8c81;
  plVar14 = game::MapSquare::GetLocationContainerAt
                      (*(long *)(lVar13 + 8),uVar24 & 0x3f,(ulong)(uVar8 & 0x3f));
  lVar13 = *plVar14;
  if (lVar13 == 0) goto LAB_000f8c81;
  lVar17 = *(long *)(lVar13 + 0xd0);
  lVar32 = *(long *)(lVar13 + 200);
  if (0 < (int)(lVar17 - lVar32 >> 4)) {
    iVar28 = 0;
    do {
      plVar14 = *(long **)(lVar32 + 8 + (long)iVar28 * 0x10);
      if (((((int)plVar14[8] <= (int)uVar20) && ((int)uVar20 <= (int)plVar14[8])) &&
          (uVar3 <= *(uint *)((long)plVar14 + 0x16c))) &&
         (((*(uint *)((long)plVar14 + 0x16c) < uVar27 && (uVar2 <= *(uint *)(plVar14 + 0x2e))) &&
          (*(uint *)(plVar14 + 0x2e) < uVar16)))) {
        (**(code **)(*plVar14 + 0xe0))(plVar14);
        lVar32 = plVar14[9];
        plVar14[10] = 0;
        plVar14[9] = 0;
        if (lVar32 != 0) {
          ref_counter_base::DecRef();
        }
        graphics::GraphNode::RemoveChild(*(void **)(lVar13 + 8),(void *)plVar14[1]);
        plVar14 = (long *)(*(long *)(lVar13 + 200) + 0x10 + (long)iVar28 * 0x10);
        plVar10 = *(long **)(lVar13 + 0xd0);
        if ((plVar14 < plVar10) &&
           (uVar26 = (long)plVar10 - (long)plVar14 >> 4, 0 < (long)plVar10 - (long)plVar14)) {
          uVar24 = (uint)uVar26 & 3;
          if ((uVar26 & 3) == 0) goto LAB_000f93c3;
          if (uVar24 != 1) {
            if (uVar24 != 2) {
              lVar32 = plVar14[-2];
              lVar17 = *plVar14;
              lVar9 = plVar14[1];
              *plVar14 = 0;
              plVar14[1] = 0;
              plVar14[-2] = lVar17;
              plVar14[-1] = lVar9;
              if (lVar32 != 0) {
                ref_counter_base::DecRef();
              }
              uVar26 = uVar26 - 1;
              plVar14 = plVar14 + 2;
            }
            lVar32 = plVar14[-2];
            lVar17 = *plVar14;
            lVar9 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar17;
            plVar14[-1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            uVar26 = uVar26 - 1;
            plVar14 = plVar14 + 2;
          }
          lVar32 = plVar14[-2];
          lVar17 = *plVar14;
          lVar9 = plVar14[1];
          *plVar14 = 0;
          plVar14[1] = 0;
          plVar14[-2] = lVar17;
          plVar14[-1] = lVar9;
          if (lVar32 != 0) {
            ref_counter_base::DecRef();
          }
          plVar14 = plVar14 + 2;
          for (uVar26 = uVar26 - 1; uVar26 != 0; uVar26 = uVar26 - 4) {
LAB_000f93c3:
            lVar32 = plVar14[-2];
            lVar17 = *plVar14;
            lVar9 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar17;
            plVar14[-1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[2];
            lVar17 = plVar14[3];
            plVar14[2] = 0;
            lVar9 = *plVar14;
            plVar14[3] = 0;
            *plVar14 = lVar32;
            plVar14[1] = lVar17;
            if (lVar9 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[2];
            lVar17 = plVar14[4];
            lVar9 = plVar14[5];
            plVar14[4] = 0;
            plVar14[5] = 0;
            plVar14[2] = lVar17;
            plVar14[3] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[4];
            lVar17 = plVar14[6];
            lVar9 = plVar14[7];
            plVar14[6] = 0;
            plVar14[7] = 0;
            plVar14[4] = lVar17;
            plVar14[5] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            plVar14 = plVar14 + 8;
          }
          plVar10 = *(long **)(lVar13 + 0xd0);
        }
        lVar32 = plVar10[-2];
        *(long **)(lVar13 + 0xd0) = plVar10 + -2;
        if (lVar32 != 0) {
          ref_counter_base::DecRef();
        }
        iVar28 = iVar28 + -1;
        lVar32 = *(long *)(lVar13 + 200);
        lVar17 = *(long *)(lVar13 + 0xd0);
      }
      iVar28 = iVar28 + 1;
    } while (iVar28 < (int)(lVar17 - lVar32 >> 4));
  }
  lVar32 = *(long *)(lVar13 + 0xe0);
  if (lVar32 != *(long *)(lVar13 + 0xe8)) {
    plVar14 = (long *)(*(long *)(lVar13 + 0xe8) + 8);
    plVar10 = (long *)(lVar32 + 8);
    uVar24 = (int)((ulong)((long)plVar14 + (-0x10 - (long)plVar10)) >> 4) + 1U & 3;
    if (uVar24 != 0) {
      if (uVar24 != 1) {
        if (uVar24 != 2) {
          lVar17 = *plVar10;
          if ((((*(char *)(lVar17 + 0x18d) != '\0') && (*(int *)(lVar17 + 0x40) <= (int)uVar20)) &&
              ((int)uVar20 <= *(int *)(lVar17 + 0x40))) &&
             (((uVar3 <= *(uint *)(lVar17 + 0x16c) && (*(uint *)(lVar17 + 0x16c) < uVar27)) &&
              ((uVar2 <= *(uint *)(lVar17 + 0x170) && (*(uint *)(lVar17 + 0x170) < uVar16)))))) {
            *(undefined1 *)(lVar17 + 0x18d) = 0;
            if ((*(int *)(lVar17 + 0x194) != 0) && (*(long *)(lVar17 + 200) != 0)) {
              FUN_00620c50(lVar17,1);
            }
            FUN_00624270(lVar17);
          }
          plVar10 = (long *)(lVar32 + 0x18);
        }
        lVar32 = *plVar10;
        if ((((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
              ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
             ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
            (uVar2 <= *(uint *)(lVar32 + 0x170))) && (*(uint *)(lVar32 + 0x170) < uVar16)) {
          *(undefined1 *)(lVar32 + 0x18d) = 0;
          if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
            FUN_00620c50(lVar32,1);
          }
          FUN_00624270(lVar32);
        }
        plVar10 = plVar10 + 2;
      }
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 2;
      if (plVar14 == plVar10) goto LAB_000f855e;
    }
    do {
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[2];
      if (((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
           ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
          ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
         ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[4];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[6];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 8;
    } while (plVar14 != plVar10);
  }
LAB_000f855e:
  lVar32 = *(long *)(lVar13 + 0xb0);
  if (lVar32 != *(long *)(lVar13 + 0xb8)) {
    plVar14 = (long *)(*(long *)(lVar13 + 0xb8) + 8);
    plVar10 = (long *)(lVar32 + 8);
    uVar24 = (int)((ulong)((long)plVar14 + (-0x10 - (long)plVar10)) >> 4) + 1U & 3;
    if (uVar24 != 0) {
      if (uVar24 != 1) {
        if (uVar24 != 2) {
          lVar17 = *plVar10;
          if (((((*(char *)(lVar17 + 0x18d) != '\0') && (*(int *)(lVar17 + 0x40) <= (int)uVar20)) &&
               ((int)uVar20 <= *(int *)(lVar17 + 0x40))) &&
              ((uVar3 <= *(uint *)(lVar17 + 0x16c) && (*(uint *)(lVar17 + 0x16c) < uVar27)))) &&
             ((uVar2 <= *(uint *)(lVar17 + 0x170) && (*(uint *)(lVar17 + 0x170) < uVar16)))) {
            *(undefined1 *)(lVar17 + 0x18d) = 0;
            if ((*(int *)(lVar17 + 0x194) != 0) && (*(long *)(lVar17 + 200) != 0)) {
              FUN_00620c50(lVar17,1);
            }
            FUN_00624270(lVar17);
          }
          plVar10 = (long *)(lVar32 + 0x18);
        }
        lVar32 = *plVar10;
        if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
            ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
           (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
            ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
          *(undefined1 *)(lVar32 + 0x18d) = 0;
          if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
            FUN_00620c50(lVar32,1);
          }
          FUN_00624270(lVar32);
        }
        plVar10 = plVar10 + 2;
      }
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 2;
      if (plVar14 == plVar10) goto LAB_000f88d6;
    }
    do {
      lVar32 = *plVar10;
      if ((((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
            ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
           ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
          (uVar2 <= *(uint *)(lVar32 + 0x170))) && (*(uint *)(lVar32 + 0x170) < uVar16)) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[2];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[4];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[6];
      if (((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
           ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
          ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
         ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 8;
    } while (plVar14 != plVar10);
  }
LAB_000f88d6:
  puVar29 = &DAT_01391630;
  do {
    lVar32 = lVar13 + 0xf8 + (ulong)(byte)puVar29[4] * 0x48;
    lVar17 = *(long *)(lVar32 + 0x28);
    lVar32 = *(long *)(lVar32 + 0x30);
    if (lVar17 != lVar32) {
      plVar14 = (long *)(lVar17 + 8);
      do {
        lVar17 = *plVar14;
        lVar9 = *(long *)(lVar17 + 0xa0);
        if (lVar9 != *(long *)(lVar17 + 0xa8)) {
          lVar19 = lVar9 + 0xc0;
          lVar1 = lVar17 + 0x188;
          lVar30 = ((((ulong)(*(long *)(lVar17 + 0xa8) - lVar19) >> 6) * 0x2aaaaaaaaaaaaab &
                    0x3ffffffffffffff) * 3 + 3) * 0x40 + lVar9;
          uVar26 = ((ulong)(lVar30 - lVar19) >> 6) * 3;
          uVar24 = (uint)uVar26 & 3;
          lVar22 = lVar9;
          lVar21 = lVar19;
          if ((uVar26 & 3) != 0) {
            if (*(char *)(lVar9 + 0x50) != '\0') {
              uVar8 = *(uint *)(*(long *)(lVar9 + 0xa8) + 0x9c);
              if ((((uVar3 <= uVar8) && (uVar8 < uVar27)) &&
                  (uVar8 = *(uint *)(*(long *)(lVar9 + 0xa8) + 0xa0), uVar2 <= uVar8)) &&
                 ((uVar8 < uVar16 && (uVar20 == *(uint *)(lVar9 + 4))))) {
                FUN_00617da0(lVar17,lVar1,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar9,1);
              }
            }
            lVar21 = lVar9 + 0x180;
            lVar22 = lVar19;
            if (uVar24 != 1) {
              lVar22 = lVar21;
              if (uVar24 != 2) {
                if (*(char *)(lVar9 + 0x110) != '\0') {
                  uVar24 = *(uint *)(*(long *)(lVar9 + 0x168) + 0x9c);
                  if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                     ((uVar24 = *(uint *)(*(long *)(lVar9 + 0x168) + 0xa0), uVar2 <= uVar24 &&
                      ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar9 + 0xc4))))))) {
                    FUN_00617da0(lVar17,lVar1,lVar19,1);
                    FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                    FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
                  }
                }
                lVar22 = lVar9 + 0x240;
                lVar19 = lVar21;
              }
              if (*(char *)(lVar19 + 0x50) != '\0') {
                uVar24 = *(uint *)(*(long *)(lVar19 + 0xa8) + 0x9c);
                if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                    (uVar24 = *(uint *)(*(long *)(lVar19 + 0xa8) + 0xa0), uVar2 <= uVar24)) &&
                   ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar19 + 4))))) {
                  FUN_00617da0(lVar17,lVar1,lVar19,1);
                  FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                  FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
                }
              }
              lVar21 = lVar22 + 0xc0;
            }
          }
          while( true ) {
            if (*(char *)(lVar22 + 0x50) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar22 + 0xa8) + 0x9c);
              if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                 ((uVar24 = *(uint *)(*(long *)(lVar22 + 0xa8) + 0xa0), uVar2 <= uVar24 &&
                  ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar22 + 4))))))) {
                FUN_00617da0(lVar17,lVar1,lVar22,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar22,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar22,1);
              }
            }
            if (lVar21 == lVar30) break;
            lVar9 = lVar21 + 0xc0;
            if (*(char *)(lVar21 + 0x50) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0xa8) + 0x9c);
              if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                  (uVar24 = *(uint *)(*(long *)(lVar21 + 0xa8) + 0xa0), uVar2 <= uVar24)) &&
                 ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 4))))) {
                FUN_00617da0(lVar17,lVar1,lVar21,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar21,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar21,1);
              }
            }
            lVar19 = lVar21 + 0x180;
            if (*(char *)(lVar21 + 0x110) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0x168) + 0x9c);
              if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                 ((uVar24 = *(uint *)(*(long *)(lVar21 + 0x168) + 0xa0), uVar2 <= uVar24 &&
                  ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 0xc4))))))) {
                FUN_00617da0(lVar17,lVar1,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar9,1);
              }
            }
            lVar22 = lVar21 + 0x240;
            if (*(char *)(lVar21 + 0x1d0) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0x228) + 0x9c);
              if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                  (uVar24 = *(uint *)(*(long *)(lVar21 + 0x228) + 0xa0), uVar2 <= uVar24)) &&
                 ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 0x184))))) {
                FUN_00617da0(lVar17,lVar1,lVar19,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
              }
            }
            lVar21 = lVar21 + 0x300;
          }
        }
        plVar14 = plVar14 + 2;
      } while ((long *)(lVar32 + 8) != plVar14);
    }
    puVar29 = puVar29 + -1;
  } while (puVar29 != (undefined1 *)0x139162b);
LAB_000f8c81:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS` @ 000f9510
```c

undefined * jag::packethandlers::ZoneUpdates::UPDATE_ZONE_FULL_FOLLOWS(long *thisPtr,long packet)

{
  long lVar1;
  uint uVar2;
  uint uVar3;
  int *piVar4;
  long **pplVar5;
  char cVar6;
  char cVar7;
  uint uVar8;
  long lVar9;
  long *plVar10;
  long *plVar11;
  long *plVar12;
  long lVar13;
  long *plVar14;
  long *plVar15;
  uint uVar16;
  long lVar17;
  byte bVar18;
  long lVar19;
  uint uVar20;
  long lVar21;
  long lVar22;
  long **pplVar23;
  size_t __n;
  uint uVar24;
  long *plVar25;
  ulong uVar26;
  uint uVar27;
  int iVar28;
  undefined1 *puVar29;
  long lVar30;
  long *plVar31;
  long lVar32;
  bool bVar33;
  bool bVar34;
  long **pplStack_3d0;
  long **pplStack_3c8;
  undefined4 uStack_388;
  uint uStack_384;
  uint uStack_380;
  uint uStack_37c;
  undefined1 uStack_378;
  undefined8 uStack_368;
  long **pplStack_360;
  long **pplStack_358;
  long **pplStack_348;
  long *aplStack_340 [96];
  long *aplStack_40 [2];
  
  lVar13 = *(long *)(packet + 0x18);
  lVar32 = *(long *)(packet + 0x10);
  pplVar23 = aplStack_340;
  lVar17 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *thisPtr);
  lVar9 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + *thisPtr);
  *(long *)(packet + 0x18) = lVar13 + 1;
  cVar6 = *(char *)(lVar32 + lVar13);
  *(long *)(packet + 0x18) = lVar13 + 2;
  cVar7 = *(char *)(lVar32 + 1 + lVar13);
  iVar28 = *(int *)(lVar17 + 0x60c);
  *(long *)(packet + 0x18) = lVar13 + 3;
  pplStack_358 = aplStack_40;
  bVar18 = cVar6 + 0x80;
  plVar14 = (long *)(lVar9 + 8);
  uVar2 = iVar28 + (char)(-0x80 - cVar7) * 8;
  uVar20 = (uint)bVar18;
  uVar3 = *(int *)(lVar17 + 0x608) + *(char *)(lVar32 + 2 + lVar13) * 8;
  uVar16 = uVar2 + 8;
  uVar27 = uVar3 + 8;
  plVar10 = *(long **)(lVar9 + 0x10);
  DAT_013942a8 = uVar20;
  DAT_013942ac = uVar3;
  DAT_013942b0 = uVar2;
  uStack_368 = pplVar23;
  pplStack_360 = pplVar23;
  pplStack_348 = pplVar23;
  aplStack_40[0] = plVar14;
  if (plVar14 == plVar10) {
LAB_000f7f9a:
    if (pplVar23 != pplStack_348) {
      HeapInterface::Free();
    }
  }
  else {
LAB_000f7ce4:
    do {
      if (((((uint)bVar18 == *(uint *)(plVar10 + 4)) && (uVar3 <= *(uint *)((long)plVar10 + 0x24)))
          && (uVar2 <= *(uint *)(plVar10 + 5) && *(uint *)((long)plVar10 + 0x24) < uVar27)) &&
         (*(uint *)(plVar10 + 5) < uVar16)) {
        (**(code **)(*(long *)plVar10[7] + 0xe0))();
        lVar13 = plVar10[4];
        iVar28 = *(int *)((long)plVar10 + 0x24);
        lVar32 = plVar10[5];
        if (pplStack_360 < pplStack_358) {
          *(int *)pplStack_360 = (int)lVar13;
          *(int *)((long)pplStack_360 + 4) = iVar28;
          *(int *)(pplStack_360 + 1) = (int)lVar32;
          pplStack_360 = (long **)((long)pplStack_360 + 0xc);
          plVar10 = (long *)eastl::rbtree::next(plVar10);
          if (plVar14 == plVar10) break;
          goto LAB_000f7ce4;
        }
        lVar17 = (long)pplStack_360 - (long)uStack_368 >> 2;
        if (lVar17 * -0x5555555555555555 == 0) {
          lVar17 = 0xc;
LAB_000f8cca:
          pplStack_3d0 = (long **)FUN_00c29480(lVar17);
          pplStack_3c8 = (long **)(lVar17 + (long)pplStack_3d0);
        }
        else {
          if (lVar17 * 0x5555555555555556 != 0) {
            lVar17 = lVar17 << 3;
            goto LAB_000f8cca;
          }
          pplStack_3c8 = (long **)0x0;
          pplStack_3d0 = (long **)0x0;
        }
        pplVar23 = pplStack_3d0;
        if (pplStack_360 != uStack_368) {
          __n = (long)pplStack_360 - (long)uStack_368;
          memmove(pplStack_3d0,uStack_368,__n);
          pplVar23 = (long **)((long)pplStack_3d0 + __n);
        }
        *(int *)((long)pplVar23 + 4) = iVar28;
        *(int *)pplVar23 = (int)lVar13;
        *(int *)(pplVar23 + 1) = (int)lVar32;
        if ((uStack_368 != (long **)0x0) && (pplStack_348 != uStack_368)) {
          HeapInterface::Free(uStack_368);
        }
        uStack_368 = pplStack_3d0;
        pplStack_358 = pplStack_3c8;
        pplStack_360 = (long **)((long)pplVar23 + 0xc);
      }
      plVar10 = (long *)eastl::rbtree::next(plVar10);
    } while (plVar14 != plVar10);
    pplVar23 = pplStack_360;
    if (uStack_368 != pplStack_360) {
      pplVar5 = (long **)((long)uStack_368 +
                         ((long)pplStack_360 - ((long)uStack_368 + 0xc) & 0xfffffffffffffffcU) + 0xc
                         );
      pplVar23 = uStack_368;
      pplStack_3c8 = (long **)((long)uStack_368 + 0xc);
      do {
        plVar10 = aplStack_40[0];
        plVar14 = (long *)aplStack_40[0][2];
        if (plVar14 == (long *)0x0) {
          bVar34 = (long *)aplStack_40[0][1] != aplStack_40[0];
LAB_000f7f58:
          if (!bVar34) {
LAB_000f7f5d:
            FUN_00487de0(plVar10,plVar14);
            *plVar10 = (long)plVar10;
            plVar10[1] = (long)plVar10;
            plVar10[2] = 0;
            *(undefined1 *)(plVar10 + 3) = 0;
            plVar10[4] = 0;
          }
        }
        else {
          iVar28 = *(int *)pplVar23;
          plVar15 = plVar14;
          plVar12 = aplStack_40[0];
          do {
            while ((plVar25 = plVar15, plVar31 = plVar14, plVar11 = aplStack_40[0],
                   iVar28 < (int)plVar25[4] ||
                   ((iVar28 <= (int)plVar25[4] &&
                    ((*(uint *)((long)pplVar23 + 4) < *(uint *)((long)plVar25 + 0x24) ||
                     ((*(uint *)((long)pplVar23 + 4) == *(uint *)((long)plVar25 + 0x24) &&
                      (*(uint *)(pplVar23 + 1) < *(uint *)(plVar25 + 5)))))))))) {
              plVar15 = (long *)plVar25[1];
              plVar12 = plVar25;
              if ((long *)plVar25[1] == (long *)0x0) goto LAB_000f7e5c;
            }
            plVar15 = (long *)*plVar25;
          } while ((long *)*plVar25 != (long *)0x0);
LAB_000f7e5c:
          do {
            plVar15 = plVar31;
            if (((int)plVar15[4] < iVar28) ||
               (((int)plVar15[4] <= iVar28 &&
                ((*(uint *)((long)plVar15 + 0x24) < *(uint *)((long)pplVar23 + 4) ||
                 ((*(uint *)((long)plVar15 + 0x24) == *(uint *)((long)pplVar23 + 4) &&
                  (*(uint *)(plVar15 + 5) < *(uint *)(pplVar23 + 1))))))))) {
              plVar31 = (long *)*plVar15;
              plVar15 = plVar11;
              if (plVar31 == (long *)0x0) break;
              goto LAB_000f7e5c;
            }
            plVar31 = (long *)plVar15[1];
            plVar11 = plVar15;
          } while ((long *)plVar15[1] != (long *)0x0);
          plVar31 = (long *)aplStack_40[0][1];
          bVar33 = aplStack_40[0] != plVar12;
          bVar34 = plVar31 != plVar15 || bVar33;
          plVar11 = plVar15;
          if (plVar12 == plVar15) goto LAB_000f7f58;
          do {
            plVar11 = (long *)eastl::rbtree::next(plVar11);
          } while (plVar11 != plVar12);
          if (plVar31 == plVar15 && !bVar33) goto LAB_000f7f5d;
          do {
            while( true ) {
              plVar10[4] = plVar10[4] + -1;
              plVar12 = (long *)eastl::rbtree::next(plVar15);
              FUN_00aa4780(plVar15,plVar10);
              plVar14 = (long *)plVar15[6];
              if (plVar14 != (long *)0x0) break;
LAB_000f7eb0:
              HeapInterface::Free(plVar15);
              plVar15 = plVar12;
              if (plVar11 == plVar12) goto LAB_000f7f30;
            }
            LOCK();
            plVar31 = plVar14 + 1;
            lVar13 = *plVar31;
            *(int *)plVar31 = (int)*plVar31 + -1;
            UNLOCK();
            if ((int)lVar13 != 1) goto LAB_000f7eb0;
            (**(code **)(*plVar14 + 0x10))(plVar14);
            LOCK();
            piVar4 = (int *)((long)plVar14 + 0xc);
            iVar28 = *piVar4;
            *piVar4 = *piVar4 + -1;
            UNLOCK();
            if (iVar28 != 1) goto LAB_000f7eb0;
            (**(code **)(*plVar14 + 0x18))(plVar14);
            HeapInterface::Free(plVar15);
            plVar15 = plVar12;
          } while (plVar11 != plVar12);
        }
LAB_000f7f30:
        pplVar23 = uStack_368;
        if (pplStack_3c8 == pplVar5) break;
        pplVar23 = pplStack_3c8;
        pplStack_3c8 = (long **)((long)pplStack_3c8 + 0xc);
      } while( true );
    }
    if (pplVar23 != (long **)0x0) goto LAB_000f7f9a;
  }
  uStack_388 = 0xffffffff;
  uStack_378 = 0;
  uStack_368 = (long **)CONCAT44(uVar20,0xffffffff);
  pplStack_360 = (long **)CONCAT44(uVar16,uVar27);
  pplStack_358 = (long **)((ulong)pplStack_358 & 0xffffffffffffff00);
  uStack_384 = uVar20;
  uStack_380 = uVar3;
  uStack_37c = uVar2;
  FUN_00af8340(*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&uStack_388,&uStack_368)
  ;
  lVar13 = *thisPtr;
  plVar14 = *(long **)((long)&__DT_RELA[0xcfa].r_offset + lVar13);
  plVar10 = (long *)plVar14[0xc];
  plVar15 = (long *)plVar14[0xd];
  if (plVar10 != plVar15) {
    uVar26 = 0;
    do {
      iVar28 = (int)uVar26;
      plVar12 = plVar10 + uVar26 * 2;
      lVar13 = plVar12[1];
      if ((((((int)uVar20 <= *(int *)(lVar13 + 8)) && (*(int *)(lVar13 + 8) <= (int)uVar20)) &&
           (uVar3 <= *(uint *)(lVar13 + 0xc))) &&
          ((uVar2 <= *(uint *)(lVar13 + 0x10) && (*(uint *)(lVar13 + 0xc) < uVar27)))) &&
         (*(uint *)(lVar13 + 0x10) < uVar16)) {
        plVar10 = plVar12 + 2;
        if ((plVar10 < plVar15) &&
           (lVar13 = (long)plVar15 - (long)plVar10 >> 4, 0 < (long)plVar15 - (long)plVar10)) {
          uVar24 = (int)lVar13 - 1U & 3;
          plVar15 = plVar12;
          plVar31 = plVar10;
          lVar32 = lVar13;
          if (uVar24 != 0) {
            lVar32 = *plVar12;
            lVar17 = plVar12[2];
            lVar9 = plVar12[3];
            plVar12[2] = 0;
            plVar12[3] = 0;
            *plVar12 = lVar17;
            plVar12[1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            plVar31 = plVar12 + 4;
            lVar32 = lVar13 + -1;
            plVar15 = plVar10;
            if (uVar24 != 1) {
              plVar15 = plVar31;
              if (uVar24 != 2) {
                lVar32 = *plVar10;
                lVar17 = plVar12[4];
                lVar9 = plVar12[5];
                plVar12[4] = 0;
                plVar12[5] = 0;
                *plVar10 = lVar17;
                plVar12[3] = lVar9;
                if (lVar32 != 0) {
                  ref_counter_base::DecRef();
                }
                lVar32 = lVar13 + -2;
                plVar15 = plVar12 + 6;
                plVar10 = plVar31;
              }
              lVar13 = *plVar10;
              lVar17 = plVar10[2];
              lVar9 = plVar10[3];
              plVar10[2] = 0;
              plVar10[3] = 0;
              *plVar10 = lVar17;
              plVar10[1] = lVar9;
              if (lVar13 != 0) {
                ref_counter_base::DecRef();
              }
              lVar32 = lVar32 + -1;
              plVar31 = plVar15 + 2;
            }
          }
          while( true ) {
            lVar13 = *plVar15;
            lVar17 = plVar15[2];
            lVar9 = plVar15[3];
            plVar15[2] = 0;
            plVar15[3] = 0;
            *plVar15 = lVar17;
            plVar15[1] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            if (lVar32 == 1) break;
            lVar13 = *plVar31;
            lVar17 = plVar31[2];
            lVar9 = plVar31[3];
            plVar31[2] = 0;
            plVar31[3] = 0;
            *plVar31 = lVar17;
            plVar31[1] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            lVar13 = plVar31[2];
            lVar17 = plVar31[4];
            lVar9 = plVar31[5];
            plVar31[4] = 0;
            plVar31[5] = 0;
            plVar31[2] = lVar17;
            plVar31[3] = lVar9;
            if (lVar13 != 0) {
              ref_counter_base::DecRef();
            }
            lVar13 = plVar31[6];
            plVar31[6] = 0;
            plVar15 = plVar31 + 6;
            lVar17 = plVar31[4];
            lVar9 = plVar31[7];
            plVar31[7] = 0;
            plVar31[4] = lVar13;
            plVar31[5] = lVar9;
            if (lVar17 != 0) {
              ref_counter_base::DecRef();
            }
            plVar31 = plVar31 + 8;
            lVar32 = lVar32 + -4;
          }
          plVar15 = (long *)plVar14[0xd];
        }
        plVar14[0xd] = (long)(plVar15 + -2);
        if (plVar15[-2] != 0) {
          ref_counter_base::DecRef();
        }
        plVar10 = (long *)plVar14[0xc];
        plVar15 = (long *)plVar14[0xd];
        iVar28 = iVar28 + -1;
      }
      uVar26 = (ulong)(iVar28 + 1);
    } while (uVar26 < (ulong)((long)plVar15 - (long)plVar10 >> 4));
    lVar13 = *thisPtr;
    plVar14 = *(long **)((long)&__DT_RELA[0xcfa].r_offset + lVar13);
  }
  lVar13 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar13);
  (**(code **)(*plVar14 + 0x10))(plVar14,lVar13 + 0x608,*(undefined4 *)(lVar13 + 0x610));
  lVar13 = game::SceneManager::GetActiveWorld
                     (*(undefined8 *)((long)&__DT_RELA[0xd02].r_info + *thisPtr));
  uVar8 = DAT_013942b0;
  uVar24 = DAT_013942ac;
  if (*(long *)(lVar13 + 8) == 0) goto LAB_000f8c81;
  lVar13 = FUN_0061c2c0(*(long *)(lVar13 + 8),DAT_013942ac >> 6,DAT_013942b0 >> 6);
  if (*(long *)(lVar13 + 8) == 0) goto LAB_000f8c81;
  plVar14 = game::MapSquare::GetLocationContainerAt
                      (*(long *)(lVar13 + 8),uVar24 & 0x3f,(ulong)(uVar8 & 0x3f));
  lVar13 = *plVar14;
  if (lVar13 == 0) goto LAB_000f8c81;
  lVar17 = *(long *)(lVar13 + 0xd0);
  lVar32 = *(long *)(lVar13 + 200);
  if (0 < (int)(lVar17 - lVar32 >> 4)) {
    iVar28 = 0;
    do {
      plVar14 = *(long **)(lVar32 + 8 + (long)iVar28 * 0x10);
      if (((((int)plVar14[8] <= (int)uVar20) && ((int)uVar20 <= (int)plVar14[8])) &&
          (uVar3 <= *(uint *)((long)plVar14 + 0x16c))) &&
         (((*(uint *)((long)plVar14 + 0x16c) < uVar27 && (uVar2 <= *(uint *)(plVar14 + 0x2e))) &&
          (*(uint *)(plVar14 + 0x2e) < uVar16)))) {
        (**(code **)(*plVar14 + 0xe0))(plVar14);
        lVar32 = plVar14[9];
        plVar14[10] = 0;
        plVar14[9] = 0;
        if (lVar32 != 0) {
          ref_counter_base::DecRef();
        }
        graphics::GraphNode::RemoveChild(*(void **)(lVar13 + 8),(void *)plVar14[1]);
        plVar14 = (long *)(*(long *)(lVar13 + 200) + 0x10 + (long)iVar28 * 0x10);
        plVar10 = *(long **)(lVar13 + 0xd0);
        if ((plVar14 < plVar10) &&
           (uVar26 = (long)plVar10 - (long)plVar14 >> 4, 0 < (long)plVar10 - (long)plVar14)) {
          uVar24 = (uint)uVar26 & 3;
          if ((uVar26 & 3) == 0) goto LAB_000f93c3;
          if (uVar24 != 1) {
            if (uVar24 != 2) {
              lVar32 = plVar14[-2];
              lVar17 = *plVar14;
              lVar9 = plVar14[1];
              *plVar14 = 0;
              plVar14[1] = 0;
              plVar14[-2] = lVar17;
              plVar14[-1] = lVar9;
              if (lVar32 != 0) {
                ref_counter_base::DecRef();
              }
              uVar26 = uVar26 - 1;
              plVar14 = plVar14 + 2;
            }
            lVar32 = plVar14[-2];
            lVar17 = *plVar14;
            lVar9 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar17;
            plVar14[-1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            uVar26 = uVar26 - 1;
            plVar14 = plVar14 + 2;
          }
          lVar32 = plVar14[-2];
          lVar17 = *plVar14;
          lVar9 = plVar14[1];
          *plVar14 = 0;
          plVar14[1] = 0;
          plVar14[-2] = lVar17;
          plVar14[-1] = lVar9;
          if (lVar32 != 0) {
            ref_counter_base::DecRef();
          }
          plVar14 = plVar14 + 2;
          for (uVar26 = uVar26 - 1; uVar26 != 0; uVar26 = uVar26 - 4) {
LAB_000f93c3:
            lVar32 = plVar14[-2];
            lVar17 = *plVar14;
            lVar9 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar17;
            plVar14[-1] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[2];
            lVar17 = plVar14[3];
            plVar14[2] = 0;
            lVar9 = *plVar14;
            plVar14[3] = 0;
            *plVar14 = lVar32;
            plVar14[1] = lVar17;
            if (lVar9 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[2];
            lVar17 = plVar14[4];
            lVar9 = plVar14[5];
            plVar14[4] = 0;
            plVar14[5] = 0;
            plVar14[2] = lVar17;
            plVar14[3] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            lVar32 = plVar14[4];
            lVar17 = plVar14[6];
            lVar9 = plVar14[7];
            plVar14[6] = 0;
            plVar14[7] = 0;
            plVar14[4] = lVar17;
            plVar14[5] = lVar9;
            if (lVar32 != 0) {
              ref_counter_base::DecRef();
            }
            plVar14 = plVar14 + 8;
          }
          plVar10 = *(long **)(lVar13 + 0xd0);
        }
        lVar32 = plVar10[-2];
        *(long **)(lVar13 + 0xd0) = plVar10 + -2;
        if (lVar32 != 0) {
          ref_counter_base::DecRef();
        }
        iVar28 = iVar28 + -1;
        lVar32 = *(long *)(lVar13 + 200);
        lVar17 = *(long *)(lVar13 + 0xd0);
      }
      iVar28 = iVar28 + 1;
    } while (iVar28 < (int)(lVar17 - lVar32 >> 4));
  }
  lVar32 = *(long *)(lVar13 + 0xe0);
  if (lVar32 != *(long *)(lVar13 + 0xe8)) {
    plVar14 = (long *)(*(long *)(lVar13 + 0xe8) + 8);
    plVar10 = (long *)(lVar32 + 8);
    uVar24 = (int)((ulong)((long)plVar14 + (-0x10 - (long)plVar10)) >> 4) + 1U & 3;
    if (uVar24 != 0) {
      if (uVar24 != 1) {
        if (uVar24 != 2) {
          lVar17 = *plVar10;
          if ((((*(char *)(lVar17 + 0x18d) != '\0') && (*(int *)(lVar17 + 0x40) <= (int)uVar20)) &&
              ((int)uVar20 <= *(int *)(lVar17 + 0x40))) &&
             (((uVar3 <= *(uint *)(lVar17 + 0x16c) && (*(uint *)(lVar17 + 0x16c) < uVar27)) &&
              ((uVar2 <= *(uint *)(lVar17 + 0x170) && (*(uint *)(lVar17 + 0x170) < uVar16)))))) {
            *(undefined1 *)(lVar17 + 0x18d) = 0;
            if ((*(int *)(lVar17 + 0x194) != 0) && (*(long *)(lVar17 + 200) != 0)) {
              FUN_00620c50(lVar17,1);
            }
            FUN_00624270(lVar17);
          }
          plVar10 = (long *)(lVar32 + 0x18);
        }
        lVar32 = *plVar10;
        if ((((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
              ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
             ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
            (uVar2 <= *(uint *)(lVar32 + 0x170))) && (*(uint *)(lVar32 + 0x170) < uVar16)) {
          *(undefined1 *)(lVar32 + 0x18d) = 0;
          if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
            FUN_00620c50(lVar32,1);
          }
          FUN_00624270(lVar32);
        }
        plVar10 = plVar10 + 2;
      }
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 2;
      if (plVar14 == plVar10) goto LAB_000f855e;
    }
    do {
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[2];
      if (((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
           ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
          ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
         ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[4];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[6];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 8;
    } while (plVar14 != plVar10);
  }
LAB_000f855e:
  lVar32 = *(long *)(lVar13 + 0xb0);
  if (lVar32 != *(long *)(lVar13 + 0xb8)) {
    plVar14 = (long *)(*(long *)(lVar13 + 0xb8) + 8);
    plVar10 = (long *)(lVar32 + 8);
    uVar24 = (int)((ulong)((long)plVar14 + (-0x10 - (long)plVar10)) >> 4) + 1U & 3;
    if (uVar24 != 0) {
      if (uVar24 != 1) {
        if (uVar24 != 2) {
          lVar17 = *plVar10;
          if (((((*(char *)(lVar17 + 0x18d) != '\0') && (*(int *)(lVar17 + 0x40) <= (int)uVar20)) &&
               ((int)uVar20 <= *(int *)(lVar17 + 0x40))) &&
              ((uVar3 <= *(uint *)(lVar17 + 0x16c) && (*(uint *)(lVar17 + 0x16c) < uVar27)))) &&
             ((uVar2 <= *(uint *)(lVar17 + 0x170) && (*(uint *)(lVar17 + 0x170) < uVar16)))) {
            *(undefined1 *)(lVar17 + 0x18d) = 0;
            if ((*(int *)(lVar17 + 0x194) != 0) && (*(long *)(lVar17 + 200) != 0)) {
              FUN_00620c50(lVar17,1);
            }
            FUN_00624270(lVar17);
          }
          plVar10 = (long *)(lVar32 + 0x18);
        }
        lVar32 = *plVar10;
        if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
            ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
           (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
            ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
          *(undefined1 *)(lVar32 + 0x18d) = 0;
          if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
            FUN_00620c50(lVar32,1);
          }
          FUN_00624270(lVar32);
        }
        plVar10 = plVar10 + 2;
      }
      lVar32 = *plVar10;
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 2;
      if (plVar14 == plVar10) goto LAB_000f88d6;
    }
    do {
      lVar32 = *plVar10;
      if ((((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
            ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
           ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
          (uVar2 <= *(uint *)(lVar32 + 0x170))) && (*(uint *)(lVar32 + 0x170) < uVar16)) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[2];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[4];
      if ((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
          ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
         (((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)) &&
          ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      lVar32 = plVar10[6];
      if (((((*(char *)(lVar32 + 0x18d) != '\0') && (*(int *)(lVar32 + 0x40) <= (int)uVar20)) &&
           ((int)uVar20 <= *(int *)(lVar32 + 0x40))) &&
          ((uVar3 <= *(uint *)(lVar32 + 0x16c) && (*(uint *)(lVar32 + 0x16c) < uVar27)))) &&
         ((uVar2 <= *(uint *)(lVar32 + 0x170) && (*(uint *)(lVar32 + 0x170) < uVar16)))) {
        *(undefined1 *)(lVar32 + 0x18d) = 0;
        if ((*(int *)(lVar32 + 0x194) != 0) && (*(long *)(lVar32 + 200) != 0)) {
          FUN_00620c50(lVar32,1);
        }
        FUN_00624270(lVar32);
      }
      plVar10 = plVar10 + 8;
    } while (plVar14 != plVar10);
  }
LAB_000f88d6:
  puVar29 = &DAT_01391630;
  do {
    lVar32 = lVar13 + 0xf8 + (ulong)(byte)puVar29[4] * 0x48;
    lVar17 = *(long *)(lVar32 + 0x28);
    lVar32 = *(long *)(lVar32 + 0x30);
    if (lVar17 != lVar32) {
      plVar14 = (long *)(lVar17 + 8);
      do {
        lVar17 = *plVar14;
        lVar9 = *(long *)(lVar17 + 0xa0);
        if (lVar9 != *(long *)(lVar17 + 0xa8)) {
          lVar19 = lVar9 + 0xc0;
          lVar1 = lVar17 + 0x188;
          lVar30 = ((((ulong)(*(long *)(lVar17 + 0xa8) - lVar19) >> 6) * 0x2aaaaaaaaaaaaab &
                    0x3ffffffffffffff) * 3 + 3) * 0x40 + lVar9;
          uVar26 = ((ulong)(lVar30 - lVar19) >> 6) * 3;
          uVar24 = (uint)uVar26 & 3;
          lVar22 = lVar9;
          lVar21 = lVar19;
          if ((uVar26 & 3) != 0) {
            if (*(char *)(lVar9 + 0x50) != '\0') {
              uVar8 = *(uint *)(*(long *)(lVar9 + 0xa8) + 0x9c);
              if ((((uVar3 <= uVar8) && (uVar8 < uVar27)) &&
                  (uVar8 = *(uint *)(*(long *)(lVar9 + 0xa8) + 0xa0), uVar2 <= uVar8)) &&
                 ((uVar8 < uVar16 && (uVar20 == *(uint *)(lVar9 + 4))))) {
                FUN_00617da0(lVar17,lVar1,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar9,1);
              }
            }
            lVar21 = lVar9 + 0x180;
            lVar22 = lVar19;
            if (uVar24 != 1) {
              lVar22 = lVar21;
              if (uVar24 != 2) {
                if (*(char *)(lVar9 + 0x110) != '\0') {
                  uVar24 = *(uint *)(*(long *)(lVar9 + 0x168) + 0x9c);
                  if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                     ((uVar24 = *(uint *)(*(long *)(lVar9 + 0x168) + 0xa0), uVar2 <= uVar24 &&
                      ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar9 + 0xc4))))))) {
                    FUN_00617da0(lVar17,lVar1,lVar19,1);
                    FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                    FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
                  }
                }
                lVar22 = lVar9 + 0x240;
                lVar19 = lVar21;
              }
              if (*(char *)(lVar19 + 0x50) != '\0') {
                uVar24 = *(uint *)(*(long *)(lVar19 + 0xa8) + 0x9c);
                if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                    (uVar24 = *(uint *)(*(long *)(lVar19 + 0xa8) + 0xa0), uVar2 <= uVar24)) &&
                   ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar19 + 4))))) {
                  FUN_00617da0(lVar17,lVar1,lVar19,1);
                  FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                  FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
                }
              }
              lVar21 = lVar22 + 0xc0;
            }
          }
          while( true ) {
            if (*(char *)(lVar22 + 0x50) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar22 + 0xa8) + 0x9c);
              if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                 ((uVar24 = *(uint *)(*(long *)(lVar22 + 0xa8) + 0xa0), uVar2 <= uVar24 &&
                  ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar22 + 4))))))) {
                FUN_00617da0(lVar17,lVar1,lVar22,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar22,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar22,1);
              }
            }
            if (lVar21 == lVar30) break;
            lVar9 = lVar21 + 0xc0;
            if (*(char *)(lVar21 + 0x50) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0xa8) + 0x9c);
              if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                  (uVar24 = *(uint *)(*(long *)(lVar21 + 0xa8) + 0xa0), uVar2 <= uVar24)) &&
                 ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 4))))) {
                FUN_00617da0(lVar17,lVar1,lVar21,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar21,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar21,1);
              }
            }
            lVar19 = lVar21 + 0x180;
            if (*(char *)(lVar21 + 0x110) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0x168) + 0x9c);
              if (((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                 ((uVar24 = *(uint *)(*(long *)(lVar21 + 0x168) + 0xa0), uVar2 <= uVar24 &&
                  ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 0xc4))))))) {
                FUN_00617da0(lVar17,lVar1,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar9,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar9,1);
              }
            }
            lVar22 = lVar21 + 0x240;
            if (*(char *)(lVar21 + 0x1d0) != '\0') {
              uVar24 = *(uint *)(*(long *)(lVar21 + 0x228) + 0x9c);
              if ((((uVar3 <= uVar24) && (uVar24 < uVar27)) &&
                  (uVar24 = *(uint *)(*(long *)(lVar21 + 0x228) + 0xa0), uVar2 <= uVar24)) &&
                 ((uVar24 < uVar16 && (uVar20 == *(uint *)(lVar21 + 0x184))))) {
                FUN_00617da0(lVar17,lVar1,lVar19,1);
                FUN_00617da0(lVar17,lVar17 + 0x1b0,lVar19,1);
                FUN_00617da0(lVar17,lVar17 + 0x1d8,lVar19,1);
              }
            }
            lVar21 = lVar21 + 0x300;
          }
        }
        plVar14 = plVar14 + 2;
      } while ((long *)(lVar32 + 8) != plVar14);
    }
    puVar29 = puVar29 + -1;
  } while (puVar29 != (undefined1 *)0x139162b);
LAB_000f8c81:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_ANIM` @ 00118e60
```c

undefined * jag::packethandlers::ZoneUpdates::LOC_ANIM(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  undefined1 uVar3;
  undefined1 uVar4;
  char cVar5;
  ushort uVar6;
  int iVar7;
  long lVar8;
  long lVar9;
  uint uVar10;
  undefined *puVar11;
  uint uVar12;
  ushort uVar13;
  uint uVar14;
  int local_4c;
  undefined4 local_48;
  float local_44;
  float local_40;
  float local_3c;
  
  iVar7 = DAT_01050dc0;
  lVar8 = *(long *)(param_2 + 0x18);
  lVar9 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar8 + 1;
  bVar1 = *(byte *)(lVar9 + lVar8);
  *(long *)(param_2 + 0x18) = lVar8 + 5;
  uVar12 = *(uint *)(lVar9 + 1 + lVar8);
  *(long *)(param_2 + 0x18) = lVar8 + 6;
  bVar2 = *(byte *)(lVar9 + 5 + lVar8);
  *(long *)(param_2 + 0x18) = lVar8 + 7;
  uVar3 = *(undefined1 *)(lVar9 + 6 + lVar8);
  *(long *)(param_2 + 0x18) = lVar8 + 8;
  uVar4 = *(undefined1 *)(lVar9 + 7 + lVar8);
  *(long *)(param_2 + 0x18) = lVar8 + 10;
  uVar6 = *(ushort *)(lVar9 + 8 + lVar8);
  uVar10 = uVar12 >> 0x18 | (uVar12 & 0xff0000) >> 8 | (uVar12 & 0xff00) << 8 | uVar12 << 0x18;
  if (iVar7 != 0x3020100) {
    uVar10 = uVar12;
  }
  *(long *)(param_2 + 0x18) = lVar8 + 0xb;
  cVar5 = *(char *)(lVar9 + 10 + lVar8);
  lVar8 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + *param_1);
  uVar13 = uVar6 << 8 | uVar6 >> 8;
  if (iVar7 != 0x3020100) {
    uVar13 = uVar6;
  }
  uVar12 = (bVar1 >> 4 & 7) + DAT_013942ac;
  if ((*(int *)(lVar8 + 0x40) <= (int)uVar12) && ((int)uVar12 <= *(int *)(lVar8 + 0x44))) {
    uVar14 = (bVar1 & 7) + DAT_013942b0;
    if ((*(int *)(lVar8 + 0x48) <= (int)uVar14) && ((int)uVar14 <= *(int *)(lVar8 + 0x4c))) {
      lVar8 = *(long *)((long)&__DT_RELA[0xd02].r_info + *param_1);
      puVar11 = &DAT_015df2b0;
      iVar7 = *(int *)(lVar8 + 0x70);
      if (iVar7 != -1) {
        puVar11 = (undefined *)((long)iVar7 * 0x10 + *(long *)(lVar8 + 0x58));
      }
      if (*(long *)(puVar11 + 8) != 0) {
        local_40 = 0.0;
        local_48 = DAT_013942a8;
        local_44 = (float)uVar12 * DAT_00cb6ae4;
        local_3c = (float)uVar14 * DAT_00cb6ae4;
        game::HeightMap::GetFineHeight(*(long *)(puVar11 + 8) + 0x14038,&local_4c);
        local_40 = (float)local_4c;
        FUN_00b0d140(*(undefined8 *)((long)&__DT_RELA[0xd06].r_info + *param_1),DAT_00fb4043,uVar10,
                     bVar2 & 7,uVar4,(cVar5 == '\x01') * '\x02' + '\x06',0,(bVar2 & 0xf0) << 5,
                     &local_44,DAT_013942a8,uVar13,uVar3);
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_ANIM_SPECIFIC` @ 00119070
```c

/* Setting prototype: undefined * OBJ_ADD(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::ZoneUpdates::LOC_ANIM_SPECIFIC(long *thisPtr,long packetPtr)

{
  byte bVar1;
  byte bVar2;
  undefined1 uVar3;
  undefined1 uVar4;
  ushort uVar5;
  int iVar6;
  long lVar7;
  long lVar8;
  long lVar9;
  uint uVar10;
  undefined *puVar11;
  uint uVar12;
  uint uVar13;
  ushort uVar14;
  undefined8 uVar15;
  int local_4c;
  undefined4 local_48;
  float local_44;
  float local_40;
  float local_3c;
  
  iVar6 = DAT_01050dc0;
  lVar7 = *(long *)(packetPtr + 0x18);
  lVar8 = *(long *)(packetPtr + 0x10);
  lVar9 = *thisPtr;
  *(long *)(packetPtr + 0x18) = lVar7 + 1;
  bVar1 = *(byte *)(lVar8 + lVar7);
  *(long *)(packetPtr + 0x18) = lVar7 + 5;
  uVar12 = *(uint *)(lVar8 + 1 + lVar7);
  *(long *)(packetPtr + 0x18) = lVar7 + 6;
  bVar2 = *(byte *)(lVar8 + 5 + lVar7);
  *(long *)(packetPtr + 0x18) = lVar7 + 7;
  uVar3 = *(undefined1 *)(lVar8 + 6 + lVar7);
  *(long *)(packetPtr + 0x18) = lVar7 + 8;
  uVar4 = *(undefined1 *)(lVar8 + 7 + lVar7);
  *(long *)(packetPtr + 0x18) = lVar7 + 10;
  uVar5 = *(ushort *)(lVar8 + 8 + lVar7);
  uVar10 = uVar12 >> 0x18 | (uVar12 & 0xff0000) >> 8 | (uVar12 & 0xff00) << 8 | uVar12 << 0x18;
  if (iVar6 != 0x3020100) {
    uVar10 = uVar12;
  }
  lVar7 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + lVar9);
  uVar14 = uVar5 << 8 | uVar5 >> 8;
  if (iVar6 != 0x3020100) {
    uVar14 = uVar5;
  }
  uVar12 = (bVar1 >> 4 & 7) + DAT_013942ac;
  if ((*(int *)(lVar7 + 0x40) <= (int)uVar12) && ((int)uVar12 <= *(int *)(lVar7 + 0x44))) {
    uVar13 = (bVar1 & 7) + DAT_013942b0;
    if ((*(int *)(lVar7 + 0x48) <= (int)uVar13) && ((int)uVar13 <= *(int *)(lVar7 + 0x4c))) {
      lVar7 = *(long *)((long)&__DT_RELA[0xd02].r_info + lVar9);
      puVar11 = &DAT_015df2b0;
      iVar6 = *(int *)(lVar7 + 0x70);
      if (iVar6 != -1) {
        puVar11 = (undefined *)((long)iVar6 * 0x10 + *(long *)(lVar7 + 0x58));
      }
      if (*(long *)(puVar11 + 8) != 0) {
        local_48 = DAT_013942a8;
        local_40 = 0.0;
        local_44 = (float)uVar12 * DAT_00cb6ae4;
        local_3c = (float)uVar13 * DAT_00cb6ae4;
        uVar15 = 0;
        game::HeightMap::GetFineHeight(*(long *)(puVar11 + 8) + 0x14038,&local_4c);
        local_40 = (float)local_4c;
        FUN_00b0d140(*(undefined8 *)((long)&__DT_RELA[0xd06].r_info + *thisPtr),DAT_00fb4043,uVar10,
                     bVar2 & 7,uVar4,6,0,(bVar2 & 0xf0) << 5,&local_44,DAT_013942a8,uVar14,uVar3,
                     uVar15);
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::OBJ_COUNT` @ 00119250
```c

undefined * jag::packethandlers::ZoneUpdates::OBJ_COUNT(long *param_1,long param_2)

{
  char cVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  byte bVar5;
  int iVar6;
  long lVar7;
  long lVar8;
  long lVar9;
  long thisPtr;
  long lVar10;
  undefined *obj;
  undefined4 local_14;
  int local_10;
  int local_c;
  
                    /* jag::packethandlers::ZoneUpdates::OBJ_COUNT -- Reads 7 bytes (objId BE
                       ushort, zoneXY byte, count hi/lo bytes, count2 hi/lo bytes), checks objId !=
                       localPlayer.objId, bounds-checks against zone area, then calls
                       ObjStackList::AddObjStack to update obj-count on the ground. Behavioral
                       marker vs OBJ_ADD: the objId-not-equal check against the local player's slot.
                       Count low byte uses +0x80 transform; count2 uses both bytes with +0x80 on
                       low. */
  lVar7 = *(long *)(param_2 + 0x18);
  lVar8 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar7 + 2;
  cVar1 = *(char *)(lVar8 + 1 + lVar7);
  bVar2 = *(byte *)(lVar8 + lVar7);
  *(long *)(param_2 + 0x18) = lVar7 + 3;
  bVar3 = *(byte *)(lVar8 + 2 + lVar7);
  *(long *)(param_2 + 0x18) = lVar7 + 5;
  cVar4 = *(char *)(lVar8 + 3 + lVar7);
  bVar5 = *(byte *)(lVar8 + 4 + lVar7);
  *(long *)(param_2 + 0x18) = lVar7 + 7;
  lVar9 = *param_1;
  local_c = (bVar3 & 7) + DAT_013942b0;
  local_10 = (bVar3 >> 4 & 7) + DAT_013942ac;
  local_14 = DAT_013942a8;
  if ((((uint)bVar2 * 0x100 + (uint)(byte)(cVar1 + 0x80) & 0xffff) !=
       *(uint *)(*(long *)((long)&__DT_RELA[0xd40].r_addend + lVar9) + 0x48)) &&
     (((((thisPtr = *(long *)((long)&__DT_RELA[0xcfc].r_offset + lVar9),
         *(int *)(thisPtr + 0x40) <= local_10 && (local_10 <= *(int *)(thisPtr + 0x44))) &&
        (*(int *)(thisPtr + 0x48) <= local_c)) && (local_c <= *(int *)(thisPtr + 0x4c))) ||
      (*(int *)(*(long *)((long)&__DT_RELA[0xcf5].r_info + lVar9) + 0x604) - 6U < 2)))) {
    lVar10 = *(long *)((long)&__DT_RELA[0xd02].r_info + lVar9);
    obj = &DAT_015df2b0;
    iVar6 = *(int *)(lVar10 + 0x70);
    if (iVar6 != -1) {
      obj = (undefined *)((long)iVar6 * 0x10 + *(long *)(lVar10 + 0x58));
    }
    ObjStackList::AddObjStack
              (thisPtr,obj,*(undefined8 *)((long)&__DT_RELA[0xca6].r_info + lVar9),&local_14,
               (uint)*(byte *)(lVar8 + 5 + lVar7) * 0x100 +
               (uint)(byte)(*(char *)(lVar8 + 6 + lVar7) + 0x80) & 0xffff,
               (uint)bVar5 * 0x100 + (uint)(byte)(cVar4 + 0x80) & 0xffff);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::OBJ_ADD` @ 001193c0
```c

undefined * jag::packethandlers::ZoneUpdates::OBJ_ADD(long *param_1,long param_2)

{
  byte bVar1;
  int iVar2;
  long lVar3;
  long lVar4;
  long lVar5;
  long thisPtr;
  long lVar6;
  undefined *obj;
  ushort uVar7;
  bool bVar8;
  undefined4 local_14;
  int local_10;
  int local_c;
  
                    /* OBJ_ADD packet handler - reads zoneXY+objId+count. Bounds-checks zone area,
                       then calls ObjStackList::AddObjStack. Byte order vs older protocol: now
                       zoneXY, objId_BE, count_hi, count_lo+0x80; logic identical. */
  lVar3 = *(long *)(param_2 + 0x18);
  bVar8 = DAT_01050dc0 == 0x3020100;
  lVar4 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar3 + 1;
  bVar1 = *(byte *)(lVar4 + lVar3);
  *(long *)(param_2 + 0x18) = lVar3 + 3;
  uVar7 = *(ushort *)(lVar4 + 1 + lVar3);
  if (bVar8) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar3 + 5;
  lVar5 = *param_1;
  local_c = (bVar1 & 7) + DAT_013942b0;
  local_10 = (bVar1 >> 4 & 7) + DAT_013942ac;
  thisPtr = *(long *)((long)&__DT_RELA[0xcfc].r_offset + lVar5);
  local_14 = DAT_013942a8;
  if (((((*(int *)(thisPtr + 0x40) <= local_10) && (local_10 <= *(int *)(thisPtr + 0x44))) &&
       (*(int *)(thisPtr + 0x48) <= local_c)) && (local_c <= *(int *)(thisPtr + 0x4c))) ||
     (*(int *)(*(long *)((long)&__DT_RELA[0xcf5].r_info + lVar5) + 0x604) - 6U < 2)) {
    lVar6 = *(long *)((long)&__DT_RELA[0xd02].r_info + lVar5);
    obj = &DAT_015df2b0;
    iVar2 = *(int *)(lVar6 + 0x70);
    if (iVar2 != -1) {
      obj = (undefined *)((long)iVar2 * 0x10 + *(long *)(lVar6 + 0x58));
    }
    ObjStackList::AddObjStack
              (thisPtr,obj,*(undefined8 *)((long)&__DT_RELA[0xca6].r_info + lVar5),&local_14,
               (uint)*(byte *)(lVar4 + 3 + lVar3) * 0x100 +
               (uint)(byte)(*(char *)(lVar4 + 4 + lVar3) + 0x80) & 0xffff,(uint)uVar7);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLIENT_SETVARC_LONG` @ 001194d0
```c

undefined * jag::packethandlers::ClientState::CLIENT_SETVARC_LONG(long *param_1,long param_2)

{
  byte bVar1;
  long lVar2;
  undefined *puVar3;
  long lVar4;
  ushort *puVar5;
  ushort uVar6;
  ulong uVar7;
  
  lVar4 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar4 + 8;
  puVar5 = (ushort *)(lVar4 + 8 + *(long *)(param_2 + 0x10));
  uVar7 = *(ulong *)(*(long *)(param_2 + 0x10) + lVar4);
  if (DAT_01050dc0 == 0x3020100) {
    uVar7 = (uVar7 & 0xff00ff00ff00ff) << 8 | (long)uVar7 >> 8 & 0xff00ff00ff00ffU;
    *(long *)(param_2 + 0x18) = lVar4 + 10;
    uVar6 = *puVar5;
    uVar7 = (long)uVar7 >> 0x10 & 0xffff0000ffffU | (uVar7 & 0xffff0000ffff) << 0x10;
    uVar6 = uVar6 << 8 | uVar6 >> 8;
    uVar7 = uVar7 << 0x20 | uVar7 >> 0x20;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar4 + 10;
    uVar6 = *puVar5;
  }
  puVar3 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1),DAT_013a3a40,(uint)uVar6)
  ;
  lVar4 = *(long *)(puVar3 + 8);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  if (lVar4 != 0) {
    lVar4 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,1,*(undefined4 *)(lVar4 + 8));
    InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar4);
    bVar1 = *(byte *)(lVar4 + 0x38);
    if (bVar1 != 0xff) {
      if (bVar1 == 1) {
        *(ulong *)(lVar4 + 0x20) = uVar7;
        return &DAT_015d3620;
      }
      (*(code *)(&PTR_FUN_01384ba0)[bVar1])(lVar4 + 0x20);
    }
    *(ulong *)(lVar4 + 0x20) = uVar7;
    *(undefined1 *)(lVar4 + 0x38) = 1;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLIENT_SETVARCBIT_LARGE` @ 00119630
```c

undefined * jag::packethandlers::ClientState::CLIENT_SETVARCBIT_LARGE(long *param_1,long param_2)

{
  long lVar1;
  long *plVar2;
  ushort uVar3;
  uint *puVar4;
  uint uVar5;
  undefined8 *puVar6;
  long lVar7;
  
                    /* CLIENT_SETVARCBIT_LARGE (948 op 69, 6B). Client VARBIT via bit-range XOR
                       helper. Wire: id(BE u16) + value(BE int, BSWAP). Family vs beta Variables
                       lambda #9. UNREGISTERED: no ClientSetVarcBitLarge Kotlin class. */
  lVar7 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar7 + 2;
  uVar3 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar7);
  puVar4 = (uint *)(*(long *)(param_2 + 0x10) + lVar7 + 2);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar7 + 6;
    uVar5 = *puVar4;
    uVar3 = uVar3 << 8 | uVar3 >> 8;
    uVar5 = uVar5 >> 0x18 | (uVar5 & 0xff0000) >> 8 | (uVar5 & 0xff00) << 8 | uVar5 << 0x18;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar7 + 6;
    uVar5 = *puVar4;
  }
  lVar7 = *param_1;
  puVar6 = &DAT_013a2fd0;
  lVar1 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar7);
  if (*(int *)(lVar1 + 0x3c) == 4) {
    plVar2 = *(long **)(*(long *)(lVar1 + 0x230) + 0x38);
    puVar6 = (undefined8 *)(**(code **)(*plVar2 + 0x40))(plVar2,uVar3,0);
    lVar7 = *param_1;
  }
  lVar7 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar7);
  *(int *)(lVar7 + 0x10) = *(int *)(lVar7 + 0x10) + 1;
  *(undefined1 *)(lVar7 + 0x14) = 1;
  FUN_00af8090(lVar7,puVar6,uVar5);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLIENT_SETVARCBIT_SMALL` @ 001196e0
```c

undefined * jag::packethandlers::ClientState::CLIENT_SETVARCBIT_SMALL(long *param_1,long param_2)

{
  char cVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  long *plVar5;
  long lVar6;
  undefined8 *puVar7;
  
                    /* CLIENT_SETVARCBIT_SMALL (948 op 48, 3B). Client VARBIT via bit-range XOR into
                       InterfaceManager update slot. Wire: value(1B raw signed) + id(BE u16,
                       low-byte byteAdd). Family vs beta Variables lambda #8 (DelayedStateChange bit
                       XOR). UNREGISTERED: no ClientSetVarcBitSmall Kotlin class. */
  lVar2 = *(long *)(param_2 + 0x18);
  lVar3 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar2 + 1;
  cVar1 = *(char *)(lVar3 + lVar2);
  *(long *)(param_2 + 0x18) = lVar2 + 3;
  lVar6 = *param_1;
  puVar7 = &DAT_013a2fd0;
  lVar4 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar6);
  if (*(int *)(lVar4 + 0x3c) == 4) {
    plVar5 = *(long **)(*(long *)(lVar4 + 0x230) + 0x38);
    puVar7 = (undefined8 *)
             (**(code **)(*plVar5 + 0x40))
                       (plVar5,(ushort)*(byte *)(lVar3 + 1 + lVar2) * 0x100 +
                               (ushort)(byte)(*(char *)(lVar3 + 2 + lVar2) + 0x80),0);
    lVar6 = *param_1;
  }
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar6);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  FUN_00af8090(lVar2,puVar7,(int)cVar1);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLIENT_SETVARC_LARGE` @ 00119780
```c

undefined * jag::packethandlers::ClientState::CLIENT_SETVARC_LARGE(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long configProvider;
  undefined *puVar6;
  long lVar7;
  int local_3c [3];
  
                    /* CLIENT_SETVARC_LARGE (948 op 64, 6B). Plain client var via InterfaceManager.
                       Wire: value(writeIntMiddle, wire bytes [B1,B0,B3,B2]) + id(LE u16, low-byte
                       byteAdd = writeShortAddLittle). Family vs beta Variables lambda #7 (CLIENT,
                       direct set). CHANGED vs older op 112. Registered ClientSetVarcLarge @ op 64.
                        */
  lVar7 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  configProvider = *(long *)((long)&__DT_RELA[0xca6].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar7 + 4;
  bVar1 = *(byte *)(lVar5 + 2 + lVar7);
  bVar2 = *(byte *)(lVar5 + 3 + lVar7);
  bVar3 = *(byte *)(lVar5 + lVar7);
  bVar4 = *(byte *)(lVar5 + 1 + lVar7);
  *(long *)(param_2 + 0x18) = lVar7 + 6;
  puVar6 = game::ConfigProvider::GetVarType
                     (configProvider,DAT_013a3a40,
                      (uint)*(byte *)(lVar5 + 5 + lVar7) * 0x100 +
                      (uint)(byte)(*(char *)(lVar5 + 4 + lVar7) + 0x80) & 0xffff);
  lVar7 = *(long *)(puVar6 + 8);
  lVar5 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  *(int *)(lVar5 + 0x10) = *(int *)(lVar5 + 0x10) + 1;
  *(undefined1 *)(lVar5 + 0x14) = 1;
  if (lVar7 != 0) {
    lVar7 = InterfaceManager::CreateOrFindUpdateEntry(lVar5,1,*(undefined4 *)(lVar7 + 8));
    InterfaceManager::MarkUpdateEntryDirty(lVar5,lVar7);
    local_3c[0] = (uint)bVar4 +
                  (uint)bVar3 * 0x100 + (uint)bVar1 * 0x1000000 + (uint)bVar2 * 0x10000;
    InterfaceManager::SetUpdateSlotValue(lVar7 + 0x20,local_3c);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::CLIENT_SETVARC_SMALL` @ 00119870
```c

undefined * jag::packethandlers::ClientState::CLIENT_SETVARC_SMALL(long *param_1,long param_2)

{
  byte bVar1;
  long lVar2;
  long configProvider;
  undefined *puVar3;
  long lVar4;
  int local_1c;
  
                    /* CLIENT_SETVARC_SMALL (948 op 47, 3B). Plain client var via InterfaceManager
                       (no bit XOR). Wire: value(byteAdd, read=byte-0x80) + id(LE u16). asm: MOVZX
                       byte[+0]; id=byte[+2]<<8+byte[+1]; ADD val,-0x80. Family vs beta Variables
                       lambda #6 (VarDomainType::CLIENT, direct set). CHANGED vs older op 1 (value
                       byteSubtract->byteAdd; id LE unchanged). Registered ClientSetVarcSmall @ op
                       47. */
  lVar4 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)(param_2 + 0x10);
  configProvider = *(long *)((long)&__DT_RELA[0xca6].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar4 + 1;
  bVar1 = *(byte *)(lVar2 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 3;
  puVar3 = game::ConfigProvider::GetVarType
                     (configProvider,DAT_013a3a40,
                      (uint)*(byte *)(lVar2 + 2 + lVar4) * 0x100 +
                      (uint)*(byte *)(lVar2 + 1 + lVar4) & 0xffff);
  lVar4 = *(long *)(puVar3 + 8);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  if (lVar4 != 0) {
    lVar4 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,1,*(undefined4 *)(lVar4 + 8));
    InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar4);
    local_1c = bVar1 - 0x80;
    InterfaceManager::SetUpdateSlotValue(lVar4 + 0x20,&local_1c);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::VARP_BIT_LARGE` @ 00119930
```c

undefined * jag::packethandlers::ClientState::VARP_BIT_LARGE(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long *plVar6;
  undefined8 *puVar7;
  long lVar8;
  ushort uVar9;
  bool bVar10;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::VARBIT_LARGE (op 51, size 6). Player
                       varbit, 4-byte value (id BE u16 + value intInverseMiddle). The prior
                       'VARP_BIT_LARGE' was a non-official descriptor; the official enum name is
                       VARBIT_LARGE. */
  lVar8 = *(long *)(param_2 + 0x18);
  bVar10 = DAT_01050dc0 == 0x3020100;
  lVar5 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar8 + 2;
  uVar9 = *(ushort *)(lVar5 + lVar8);
  if (bVar10) {
    uVar9 = uVar9 << 8 | uVar9 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar8 + 6;
  bVar1 = *(byte *)(lVar5 + 3 + lVar8);
  puVar7 = &DAT_013a2fd0;
  bVar2 = *(byte *)(lVar5 + 2 + lVar8);
  bVar3 = *(byte *)(lVar5 + 4 + lVar8);
  bVar4 = *(byte *)(lVar5 + 5 + lVar8);
  lVar8 = *param_1;
  lVar5 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar8);
  if (*(int *)(lVar5 + 0x3c) == 4) {
    plVar6 = *(long **)(*(long *)(lVar5 + 0x230) + 0x38);
    puVar7 = (undefined8 *)(**(code **)(*plVar6 + 0x40))(plVar6,uVar9,0);
    lVar8 = *param_1;
  }
  game::PlayerVarDomain::setBitFromPacket
            (lVar8 + 0x19b40,puVar7,
             (uint)bVar4 * 0x100 + (uint)bVar1 * 0x1000000 + (uint)bVar2 * 0x10000 + (uint)bVar3);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::VARP_BIT_SMALL` @ 001199f0
```c

undefined * jag::packethandlers::ClientState::VARP_BIT_SMALL(long *param_1,long param_2)

{
  char cVar1;
  long lVar2;
  long *plVar3;
  undefined8 *puVar4;
  long lVar5;
  ushort uVar6;
  bool bVar7;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::VARBIT_SMALL (op 10, size 3). Player
                       varbit, 1-byte value (id BE u16 + value -128-byte). The prior
                       'VARP_BIT_SMALL' was a non-official descriptor; the official enum name is
                       VARBIT_SMALL. */
  lVar2 = *(long *)(param_2 + 0x18);
  bVar7 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar6 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar2);
  if (bVar7) {
    uVar6 = uVar6 << 8 | uVar6 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar2 + 3;
  lVar5 = *param_1;
  puVar4 = &DAT_013a2fd0;
  cVar1 = *(char *)(*(long *)(param_2 + 0x10) + 2 + lVar2);
  lVar2 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar5);
  if (*(int *)(lVar2 + 0x3c) == 4) {
    plVar3 = *(long **)(*(long *)(lVar2 + 0x230) + 0x38);
    puVar4 = (undefined8 *)(**(code **)(*plVar3 + 0x40))(plVar3,uVar6,0);
    lVar5 = *param_1;
  }
  game::PlayerVarDomain::setBitFromPacket(lVar5 + 0x19b40,puVar4,-0x80 - cVar1);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::VARP_LARGE` @ 00119a90
```c

undefined * jag::packethandlers::ClientState::VARP_LARGE(long *param_1,long param_2)

{
  long lVar1;
  undefined *puVar2;
  ushort *puVar3;
  uint uVar4;
  ushort uVar5;
  uint local_38 [6];
  undefined1 local_20;
  
                    /* VARP_LARGE (948 op 28, 6B). Plain player var, 4-byte int. Wire: value(BE int,
                       BSWAP) + id(BE u16). -> PlayerVarDomain::set. Family confirmed vs beta
                       Variables lambda #3 (SetVarValueFromServer). CHANGED vs older op 111 (was
                       writeIntMiddle+idLE). Registered as VarpLarge @ op 28 in
                       Rev948ServerCodecsVariable.kt. */
  lVar1 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar1 + 4;
  uVar4 = *(uint *)(*(long *)(param_2 + 0x10) + lVar1);
  puVar3 = (ushort *)(*(long *)(param_2 + 0x10) + lVar1 + 4);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar1 + 6;
    uVar5 = *puVar3;
    uVar4 = uVar4 >> 0x18 | (uVar4 & 0xff0000) >> 8 | (uVar4 & 0xff00) << 8 | uVar4 << 0x18;
    uVar5 = uVar5 << 8 | uVar5 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1 + 6;
    uVar5 = *puVar3;
  }
  puVar2 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1),DAT_013a2fe0,(uint)uVar5)
  ;
  local_20 = 0;
  local_38[0] = uVar4;
  game::PlayerVarDomain::set(*param_1 + 0x19b40,puVar2,local_38);
  FUN_0047fe00(local_38);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::VARP_SMALL` @ 00119b30
```c

undefined * jag::packethandlers::ClientState::VARP_SMALL(long *param_1,long param_2)

{
  char cVar1;
  long lVar2;
  undefined *puVar3;
  ushort uVar4;
  bool bVar5;
  int local_38 [6];
  undefined1 local_20;
  
                    /* ServerProt op 61 (size 3), CONF:CERTAIN. ConfigProvider::GetVarType +
                       PlayerVarDomain::set, 1-byte byteInverse value. VARP_SMALL (matches CONFIRMED
                       stub). */
  lVar2 = *(long *)(param_2 + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar4 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar2);
  if (bVar5) {
    uVar4 = uVar4 << 8 | uVar4 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar2 + 3;
  cVar1 = *(char *)(*(long *)(param_2 + 0x10) + 2 + lVar2);
  puVar3 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1),DAT_013a2fe0,(uint)uVar4)
  ;
  local_20 = 0;
  local_38[0] = (int)(char)(-0x80 - cVar1);
  game::PlayerVarDomain::set(*param_1 + 0x19b40,puVar3,local_38);
  FUN_0047fe00(local_38);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::RESET_ALL_VARPS` @ 00119bd0
```c

undefined * jag::packethandlers::ClientState::RESET_ALL_VARPS(long *param_1)

{
  int *piVar1;
  undefined8 *puVar2;
  long lVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  long lVar6;
  undefined8 *puVar7;
  undefined8 *puVar8;
  undefined8 *puVar9;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::RESET_CLIENT_VARCACHE (948 op 5, size
                       0). Handler resets BOTH player-varp domain (+0x19b60) AND varc/interface
                       domain (+0x35c08) -- i.e. clears the entire client var cache. CONFIRMED:
                       behavior-match to RESET_CLIENT_VARCACHE + live capture label
                       'ResetClientVarcache' + size-sanity (sz0). The Ghidra fn-name
                       'RESET_ALL_VARPS' is a fabricated rename, NOT the official enum. */
  lVar3 = *param_1;
  FUN_0047fca0(lVar3 + 0x19b60,*(undefined8 *)((long)&__DT_RELA[0xd43].r_offset + lVar3),
               *(undefined8 *)((long)&__DT_RELA[0xd43].r_info + lVar3));
  uVar4 = *(undefined8 *)((long)&__DT_RELA[0x1ff5].r_offset + lVar3);
  uVar5 = *(undefined8 *)((long)&__DT_RELA[0x1ff4].r_addend + lVar3);
  *(undefined8 *)((long)&__DT_RELA[0xd43].r_addend + lVar3) = 0;
  FUN_0047fca0(lVar3 + 0x35c08,uVar5,uVar4);
  lVar6 = *(long *)((long)&__DT_RELA[0x32a6].r_addend + lVar3);
  *(undefined8 *)((long)&__DT_RELA[0x1ff5].r_info + lVar3) = 0;
  *(undefined4 *)((long)&__DT_RELA[0x3381].r_addend + lVar3) = 0xffffffff;
  puVar9 = *(undefined8 **)((long)&__DT_RELA[0x32a6].r_info + lVar3);
  if (lVar6 == 0) {
LAB_00119cc1:
    lVar6 = *param_1;
    *(undefined8 *)((long)&__DT_RELA[0x32a7].r_offset + lVar3) = 0;
    lVar3 = *(long *)((long)&__DT_RELA[0xd00].r_info + lVar6);
    piVar1 = (int *)(*(long *)((long)&__DT_RELA[0xd00].r_offset + lVar6) + 0x30);
    *piVar1 = *piVar1 + 0x41;
    *(undefined1 *)(lVar3 + 0x14) = 1;
    *(undefined1 *)(lVar3 + 0x15) = 0;
    return &DAT_015d3620;
  }
  puVar2 = puVar9 + lVar6;
  do {
    puVar7 = (undefined8 *)*puVar9;
joined_r0x00119c57:
    puVar8 = puVar7;
    if (puVar7 != (undefined8 *)0x0) {
      do {
        puVar7 = (undefined8 *)puVar8[3];
        if (*(undefined8 **)((long)&__DT_RELA[0x32aa].r_offset + lVar3) != puVar8) {
          if ((*(undefined8 **)((long)&__DT_RELA[0x32a9].r_addend + lVar3) <= puVar8) &&
             (puVar8 < *(undefined8 **)((long)&__DT_RELA[0x32a8].r_addend + lVar3)))
          goto code_r0x00119c8c;
          HeapInterface::Free();
        }
        puVar8 = puVar7;
        if (puVar7 == (undefined8 *)0x0) break;
      } while( true );
    }
    *puVar9 = 0;
    puVar9 = puVar9 + 1;
    if (puVar9 == puVar2) goto LAB_00119cc1;
  } while( true );
code_r0x00119c8c:
  *puVar8 = *(undefined8 *)((long)&__DT_RELA[0x32a8].r_offset + lVar3);
  *(undefined8 **)((long)&__DT_RELA[0x32a8].r_offset + lVar3) = puVar8;
  goto joined_r0x00119c57;
}


```

## `jag::packethandlers::ClientState::WORLDENTITY_ADD` @ 001202d0
```c

undefined * jag::packethandlers::ClientState::WORLDENTITY_ADD(long *param_1,long param_2)

{
  long *plVar1;
  byte bVar2;
  long lVar3;
  long lVar4;
  long *plVar5;
  long *plVar6;
  long *plVar7;
  int aiStack_88 [2];
  long local_80;
  long local_68;
  long local_50;
  long *local_38;
  long *local_30;
  
  lVar3 = *(long *)(param_2 + 0x18);
  lVar4 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar3 + 1;
  bVar2 = *(byte *)(lVar4 + lVar3);
  *(long *)(param_2 + 0x18) = lVar3 + 5;
  game::RebuildSceneEntry::Reset
            (aiStack_88,
             (uint)*(byte *)(lVar4 + 1 + lVar3) * 0x100 +
             (uint)*(byte *)(lVar4 + 3 + lVar3) * 0x1000000 +
             (uint)*(byte *)(lVar4 + 4 + lVar3) * 0x10000 + (uint)*(byte *)(lVar4 + 2 + lVar3));
  game::WorldList::InsertOrReplace
            (*(void **)((long)&__DT_RELA[0xcf2].r_info + *param_1),aiStack_88,bVar2 - 0x80);
  if (local_30 != local_38) {
    plVar1 = (long *)((long)local_38 +
                     ((long)local_30 - (long)(local_38 + 3) & 0xfffffffffffffff8U) + 0x18);
    plVar5 = local_38 + 3;
    plVar7 = local_38;
    while( true ) {
      plVar6 = plVar5;
      if (*plVar7 != 0) {
        HeapInterface::Free();
      }
      if (plVar1 == plVar6) break;
      plVar5 = plVar6 + 3;
      plVar7 = plVar6;
    }
  }
  if (local_38 != (long *)0x0) {
    HeapInterface::Free();
  }
  if (local_50 != 0) {
    HeapInterface::Free();
  }
  if (local_68 != 0) {
    HeapInterface::Free();
  }
  if (local_80 != 0) {
    HeapInterface::Free();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::REBUILD_NORMAL` @ 001203e0
```c

undefined * jag::packethandlers::ClientState::REBUILD_NORMAL(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  long *plVar3;
  uint uVar4;
  void *__dest;
  ulong uVar5;
  long lVar6;
  long lVar7;
  uint *puVar8;
  long lVar9;
  size_t __n;
  long lVar10;
  uint uVar11;
  ulong uVar12;
  uint uVar13;
  uint uVar14;
  int iVar15;
  long *plVar16;
  long *plVar17;
  undefined4 *puVar18;
  undefined4 *puVar19;
  long *plVar20;
  long *plVar21;
  long lVar22;
  uint *puVar23;
  long *plVar24;
  bool bVar25;
  void *local_118;
  int local_e0;
  uint local_d0;
  uint local_cc;
  undefined1 local_c8 [16];
  long local_b8;
  int local_a8 [2];
  uint *local_a0;
  uint *local_98;
  uint *local_90;
  uint *local_88;
  uint *local_80;
  uint *local_78;
  long local_70;
  undefined4 *local_68;
  undefined4 *local_60;
  long *local_58;
  long *local_50;
  long *local_48;
  
                    /* REBUILD_NORMAL handler: opcode 0xc7 (199), size -2 (varShort). Multi-scene
                       grid form for INSTANCED regions (the equivalent of REBUILD_REGION). NOT the
                       world-login rebuild -- that is REBUILD_NORMAL_SIMPLE at op 0x51 (81). */
  plVar24 = *(long **)((long)&__DT_RELA[0xcf2].r_info + *param_1);
  lVar22 = plVar24[1];
  lVar7 = *plVar24;
  if (lVar22 != lVar7) {
    lVar10 = lVar7;
    lVar9 = lVar7 + 0x68;
    while( true ) {
      plVar3 = *(long **)(lVar10 + 0x58);
      plVar21 = *(long **)(lVar10 + 0x50);
      if (plVar3 != plVar21) {
        plVar17 = plVar21 + 3;
        plVar20 = plVar21;
        while( true ) {
          plVar16 = plVar17;
          if (*plVar20 != 0) {
            HeapInterface::Free();
          }
          if (plVar21 + (((ulong)((long)plVar3 - (long)(plVar21 + 3)) >> 3) * 0xaaaaaaaaaaaaaab &
                        0x1fffffffffffffff) * 3 + 3 == plVar16) break;
          plVar17 = plVar16 + 3;
          plVar20 = plVar16;
        }
        plVar21 = *(long **)(lVar10 + 0x50);
      }
      if (plVar21 != (long *)0x0) {
        HeapInterface::Free();
      }
      if (*(long *)(lVar10 + 0x38) != 0) {
        HeapInterface::Free();
      }
      if (*(long *)(lVar10 + 0x20) != 0) {
        HeapInterface::Free();
      }
      if (*(long *)(lVar10 + 8) != 0) {
        HeapInterface::Free();
      }
      if (lVar9 == lVar7 + ((((ulong)(lVar22 - (lVar7 + 0x68)) >> 3) * 0xec4ec4ec4ec4ec5 &
                            0x1fffffffffffffff) + 1) * 0x68) break;
      lVar10 = lVar9;
      lVar9 = lVar9 + 0x68;
    }
    lVar7 = *plVar24;
  }
  lVar10 = *(long *)(param_2 + 0x18);
  lVar9 = *(long *)(param_2 + 0x10);
  plVar24[1] = lVar7;
  lVar22 = lVar10 + 1;
  *(long *)(param_2 + 0x18) = lVar22;
  bVar1 = *(byte *)(lVar9 + lVar10);
  if (bVar1 == 0) {
LAB_00120dc3:
    return &DAT_015d3620;
  }
  local_e0 = 0;
LAB_0012056d:
  bVar25 = DAT_01050dc0 != 0x3020100;
  *(long *)(param_2 + 0x18) = lVar22 + 4;
  uVar13 = *(uint *)(lVar9 + lVar22);
  uVar11 = uVar13 >> 0x18 | (uVar13 & 0xff0000) >> 8 | (uVar13 & 0xff00) << 8 | uVar13 << 0x18;
  if (bVar25) {
    uVar11 = uVar13;
  }
  game::RebuildSceneEntry::Reset(local_a8,uVar11);
  lVar7 = *(long *)(param_2 + 0x18);
  lVar10 = *(long *)(param_2 + 0x10);
  lVar22 = lVar7 + 1;
  *(long *)(param_2 + 0x18) = lVar22;
  uVar13 = (uint)*(byte *)(lVar10 + lVar7);
  lVar9 = lVar10;
  if (*(byte *)(lVar10 + lVar7) != 0) {
    uVar11 = 0;
    do {
      puVar8 = (uint *)(lVar10 + lVar22);
      lVar22 = lVar22 + 4;
      bVar25 = DAT_01050dc0 != 0x3020100;
      *(long *)(param_2 + 0x18) = lVar22;
      uVar14 = *puVar8;
      local_d0 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 | uVar14 << 0x18
      ;
      if (bVar25) {
        local_d0 = uVar14;
      }
      uVar12 = (long)local_80 - (long)local_88 >> 2;
      if ((long)local_80 - (long)local_88 != 0xa0) {
        if (uVar12 == 0) {
LAB_00120656:
          lVar22 = (long)(int)uVar12;
          if ((local_78 == local_80) || (local_80 != local_88 + lVar22)) {
            FUN_006d7220(&local_88,local_88 + lVar22,&local_d0);
          }
          else {
            *local_80 = local_d0;
            local_80 = local_80 + 1;
          }
          puVar18 = (undefined4 *)(local_70 + lVar22 * 4);
          if ((local_60 == local_68) || (puVar18 != local_68)) {
            FUN_006d7220(&local_70,puVar18,&DAT_00fd781c);
          }
          else {
            *local_68 = 0xffffffff;
            local_68 = local_68 + 1;
          }
          local_b8 = 0;
          local_c8 = (undefined1  [16])0x0;
          if (DAT_015ed790 == 0) {
            __dest = (void *)memalign(0x10,0x10);
          }
          else {
            __dest = (void *)HeapInterface::AllocDispatch(DAT_015ed790,0x10,0x10);
          }
          if (__dest == (void *)0x0) {
                    /* WARNING: Subroutine does not return */
            HeapInterface::AllocFailed(0x10,0x10);
          }
          local_118 = __dest;
          if (local_c8._8_8_ != local_c8._0_8_) {
            __n = local_c8._8_8_ - local_c8._0_8_;
            memmove(__dest,(void *)local_c8._0_8_,__n);
            local_118 = (void *)((long)__dest + __n);
          }
          if ((void *)local_c8._0_8_ != (void *)0x0) {
            HeapInterface::Free(local_c8._0_8_);
          }
          local_b8 = (long)__dest + 0x10;
          local_c8._8_8_ = local_118;
          local_c8._0_8_ = __dest;
          FUN_00af7770(local_c8,__dest,(long)local_98 - (long)local_a0 >> 2);
          if ((local_48 == local_50) || (local_58 + lVar22 * 3 != local_50)) {
            FUN_006d6950(&local_58,local_58 + lVar22 * 3,local_c8);
          }
          else {
            *local_50 = 0;
            local_50[1] = 0;
            local_50[2] = 0;
            *local_50 = local_c8._0_8_;
            local_50[1] = local_c8._8_8_;
            local_c8 = ZEXT816(0);
            local_50[2] = local_b8;
            local_b8 = 0;
            local_50 = local_50 + 3;
          }
          uVar12 = (ulong)((int)uVar12 + 1);
          uVar5 = (long)local_80 - (long)local_88 >> 2;
          puVar8 = local_88;
          puVar23 = local_80;
          lVar22 = local_70;
          if (uVar12 < uVar5) {
            do {
              uVar14 = *(uint *)(lVar22 + uVar12 * 4);
              while ((uVar4 = (uint)uVar12, uVar14 != 0xffffffff && (uVar14 < uVar4))) {
                FUN_006d8970(local_a8,uVar12,uVar4 - 1);
                uVar12 = (ulong)(uVar4 + 1);
                uVar5 = (long)local_80 - (long)local_88 >> 2;
                puVar8 = local_88;
                if (uVar5 <= uVar12) goto LAB_00120855;
                puVar23 = local_80;
                lVar22 = local_70;
                uVar14 = *(uint *)(local_70 + uVar12 * 4);
              }
              uVar12 = (ulong)(uVar4 + 1);
              uVar5 = (long)puVar23 - (long)puVar8 >> 2;
            } while (uVar12 < uVar5);
          }
LAB_00120855:
          if ((uVar5 != 0) && (local_d0 != *puVar8)) {
            uVar12 = 0;
            do {
              uVar12 = (ulong)((int)uVar12 + 1);
              if (uVar5 <= uVar12) break;
            } while (local_d0 != puVar8[uVar12]);
          }
          if (local_c8._0_8_ != 0) {
            HeapInterface::Free();
          }
          lVar10 = *(long *)(param_2 + 0x10);
          lVar22 = *(long *)(param_2 + 0x18);
          lVar9 = lVar10;
        }
        else if (*local_88 != local_d0) {
          uVar5 = 0;
          do {
            uVar14 = (int)uVar5 + 1;
            uVar5 = (ulong)uVar14;
            if (uVar12 <= uVar5) goto LAB_00120656;
          } while (local_88[uVar5] != local_d0);
          if (uVar14 == 0xffffffff) goto LAB_00120656;
        }
      }
      uVar11 = uVar11 + 1;
    } while (uVar11 != uVar13);
    lVar6 = lVar22 + 1;
    *(long *)(param_2 + 0x18) = lVar6;
    uVar11 = (uint)*(byte *)(lVar10 + lVar22);
    if (*(byte *)(lVar10 + lVar22) != 0) goto LAB_001208ca;
    goto LAB_00120a30;
  }
  lVar6 = lVar7 + 2;
  *(long *)(param_2 + 0x18) = lVar6;
  bVar2 = *(byte *)(lVar10 + 1 + lVar7);
  uVar11 = (uint)bVar2;
  if (bVar2 != 0) {
LAB_001208ca:
    iVar15 = 0;
    do {
      bVar25 = DAT_01050dc0 != 0x3020100;
      *(long *)(param_2 + 0x18) = lVar6 + 4;
      uVar14 = *(uint *)(lVar9 + lVar6);
      local_cc = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 | uVar14 << 0x18
      ;
      if (bVar25) {
        local_cc = uVar14;
      }
      uVar12 = (long)local_98 - (long)local_a0 >> 2;
      if ((long)local_98 - (long)local_a0 != 0x20) {
        if (uVar12 == 0) {
LAB_00120956:
          if ((local_90 == local_98) || (local_98 != local_a0 + (int)uVar12)) {
            FUN_006d7220(&local_a0,local_a0 + (int)uVar12,&local_cc);
          }
          else {
            *local_98 = local_cc;
            local_98 = local_98 + 1;
          }
          uVar5 = 0;
          plVar24 = local_58;
          if (local_50 != local_58) {
            do {
              plVar3 = plVar24 + uVar5 * 3;
              puVar18 = (undefined4 *)plVar3[1];
              puVar19 = (undefined4 *)(*plVar3 + (long)(int)uVar12 * 4);
              if (((undefined4 *)plVar3[2] == puVar18) || (puVar19 != puVar18)) {
                FUN_006d7220(plVar3,puVar19,&DAT_010291e4);
                plVar24 = local_58;
              }
              else {
                *puVar18 = 0x80000000;
                plVar3[1] = (long)(puVar18 + 1);
              }
              uVar5 = (ulong)((int)uVar5 + 1);
            } while (uVar5 < (ulong)(((long)local_50 - (long)plVar24 >> 3) * -0x5555555555555555));
          }
        }
        else if (*local_a0 != local_cc) {
          uVar5 = 0;
          do {
            uVar14 = (int)uVar5 + 1;
            uVar5 = (ulong)uVar14;
            if (uVar12 <= uVar5) goto LAB_00120956;
          } while (local_a0[uVar5] != local_cc);
          if (uVar14 == 0xffffffff) goto LAB_00120956;
        }
      }
      iVar15 = iVar15 + 1;
      if ((int)uVar11 <= iVar15) goto LAB_00120a10;
      lVar9 = *(long *)(param_2 + 0x10);
      lVar6 = *(long *)(param_2 + 0x18);
    } while( true );
  }
  goto LAB_00120bf8;
LAB_00120a10:
  if (uVar13 != 0) {
    lVar10 = *(long *)(param_2 + 0x10);
    lVar6 = *(long *)(param_2 + 0x18);
LAB_00120a30:
    lVar22 = 0;
    plVar24 = local_58;
    do {
      lVar7 = lVar6 + 1;
      *(long *)(param_2 + 0x18) = lVar7;
      *(int *)(local_70 + lVar22 * 4) = (int)*(char *)(lVar10 + lVar6);
      if (uVar11 != 0) {
        puVar8 = (uint *)*plVar24;
        puVar23 = puVar8 + (ulong)(uVar11 - 1) + 1;
        uVar14 = uVar11 & 3;
        if (uVar14 != 0) {
          if (uVar14 != 1) {
            if (uVar14 != 2) {
              lVar9 = lVar6 + 2;
              *(long *)(param_2 + 0x18) = lVar9;
              if (*(char *)(lVar10 + lVar7) == '\0') {
                *puVar8 = 0x80000000;
                lVar7 = lVar9;
              }
              else {
                lVar7 = lVar6 + 6;
                bVar25 = DAT_01050dc0 != 0x3020100;
                *(long *)(param_2 + 0x18) = lVar7;
                uVar14 = *(uint *)(lVar10 + lVar9);
                uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                        uVar14 << 0x18;
                if (bVar25) {
                  uVar4 = uVar14;
                }
                *puVar8 = uVar4;
              }
              puVar8 = puVar8 + 1;
            }
            lVar9 = lVar7 + 1;
            *(long *)(param_2 + 0x18) = lVar9;
            if (*(char *)(lVar10 + lVar7) == '\0') {
              *puVar8 = 0x80000000;
              lVar7 = lVar9;
            }
            else {
              lVar7 = lVar7 + 5;
              bVar25 = DAT_01050dc0 != 0x3020100;
              *(long *)(param_2 + 0x18) = lVar7;
              uVar14 = *(uint *)(lVar10 + lVar9);
              uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                      uVar14 << 0x18;
              if (bVar25) {
                uVar4 = uVar14;
              }
              *puVar8 = uVar4;
            }
            puVar8 = puVar8 + 1;
          }
          lVar9 = lVar7 + 1;
          *(long *)(param_2 + 0x18) = lVar9;
          if (*(char *)(lVar10 + lVar7) == '\0') {
            *puVar8 = 0x80000000;
            lVar7 = lVar9;
          }
          else {
            lVar7 = lVar7 + 5;
            bVar25 = DAT_01050dc0 != 0x3020100;
            *(long *)(param_2 + 0x18) = lVar7;
            uVar14 = *(uint *)(lVar10 + lVar9);
            uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                    uVar14 << 0x18;
            if (bVar25) {
              uVar4 = uVar14;
            }
            *puVar8 = uVar4;
          }
          puVar8 = puVar8 + 1;
          if (puVar8 == puVar23) goto LAB_00120be3;
        }
        do {
          lVar9 = lVar7 + 1;
          *(long *)(param_2 + 0x18) = lVar9;
          if (*(char *)(lVar10 + lVar7) == '\0') {
            *puVar8 = 0x80000000;
          }
          else {
            lVar9 = lVar7 + 5;
            bVar25 = DAT_01050dc0 != 0x3020100;
            *(long *)(param_2 + 0x18) = lVar9;
            uVar14 = *(uint *)(lVar10 + 1 + lVar7);
            uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                    uVar14 << 0x18;
            if (bVar25) {
              uVar4 = uVar14;
            }
            *puVar8 = uVar4;
          }
          lVar7 = lVar9 + 1;
          *(long *)(param_2 + 0x18) = lVar7;
          if (*(char *)(lVar10 + lVar9) == '\0') {
            puVar8[1] = 0x80000000;
          }
          else {
            lVar7 = lVar9 + 5;
            bVar25 = DAT_01050dc0 != 0x3020100;
            *(long *)(param_2 + 0x18) = lVar7;
            uVar14 = *(uint *)(lVar10 + 1 + lVar9);
            uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                    uVar14 << 0x18;
            if (bVar25) {
              uVar4 = uVar14;
            }
            puVar8[1] = uVar4;
          }
          lVar9 = lVar7 + 1;
          *(long *)(param_2 + 0x18) = lVar9;
          if (*(char *)(lVar10 + lVar7) == '\0') {
            puVar8[2] = 0x80000000;
          }
          else {
            lVar9 = lVar7 + 5;
            bVar25 = DAT_01050dc0 != 0x3020100;
            *(long *)(param_2 + 0x18) = lVar9;
            uVar14 = *(uint *)(lVar10 + 1 + lVar7);
            uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                    uVar14 << 0x18;
            if (bVar25) {
              uVar4 = uVar14;
            }
            puVar8[2] = uVar4;
          }
          lVar7 = lVar9 + 1;
          *(long *)(param_2 + 0x18) = lVar7;
          if (*(char *)(lVar10 + lVar9) == '\0') {
            puVar8[3] = 0x80000000;
          }
          else {
            lVar7 = lVar9 + 5;
            bVar25 = DAT_01050dc0 != 0x3020100;
            *(long *)(param_2 + 0x18) = lVar7;
            uVar14 = *(uint *)(lVar10 + 1 + lVar9);
            uVar4 = uVar14 >> 0x18 | (uVar14 & 0xff0000) >> 8 | (uVar14 & 0xff00) << 8 |
                    uVar14 << 0x18;
            if (bVar25) {
              uVar4 = uVar14;
            }
            puVar8[3] = uVar4;
          }
          puVar8 = puVar8 + 4;
        } while (puVar8 != puVar23);
      }
LAB_00120be3:
      lVar22 = lVar22 + 1;
      plVar24 = plVar24 + 3;
      lVar6 = lVar7;
    } while ((int)lVar22 < (int)uVar13);
  }
LAB_00120bf8:
  game::WorldList::InsertOrReplace
            (*(void **)((long)&__DT_RELA[0xcf2].r_info + *param_1),local_a8,-1);
  if (local_50 != local_58) {
    plVar24 = (long *)((long)local_58 +
                      ((long)local_50 - (long)(local_58 + 3) & 0xfffffffffffffff8U) + 0x18);
    plVar3 = local_58 + 3;
    plVar21 = local_58;
    while( true ) {
      plVar17 = plVar3;
      if (*plVar21 != 0) {
        HeapInterface::Free();
      }
      if (plVar17 == plVar24) break;
      plVar3 = plVar17 + 3;
      plVar21 = plVar17;
    }
  }
  if (local_58 != (long *)0x0) {
    HeapInterface::Free();
  }
  if (local_70 != 0) {
    HeapInterface::Free();
  }
  if (local_88 != (uint *)0x0) {
    HeapInterface::Free();
  }
  if (local_a0 != (uint *)0x0) {
    HeapInterface::Free();
  }
  if (local_e0 + 1U == (uint)bVar1) goto LAB_00120dc3;
  local_e0 = local_e0 + 1;
  lVar9 = *(long *)(param_2 + 0x10);
  lVar22 = *(long *)(param_2 + 0x18);
  goto LAB_0012056d;
}


```

## `jag::packethandlers::Misc::SET_DISPLAY_INT` @ 00121a90
```c

/* Setting prototype: undefined * SET_DISPLAY_INT(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Misc::SET_DISPLAY_INT(long *thisPtr,long packetPtr)

{
  long lVar1;
  uint uVar2;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::JCOINS_UPDATE (op 74, size 4). The
                       Ghidra function name 'SET_DISPLAY_INT' is a fabricated rename, NOT the
                       official packet name. Reads a uint and stores it at client+0x64. */
  uVar2 = Packet::gT_unsigned_int((PacketCore *)packetPtr);
  lVar1 = *(long *)((long)&__DT_RELA[0xd40].r_addend + *thisPtr);
  if (lVar1 != 0) {
    *(uint *)(lVar1 + 100) = uVar2;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::UPDATE_ZONE_PARTIAL` @ 00125640
```c

/* Setting prototype: undefined * UPDATE_ZONE_PARTIAL(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::ClientState::UPDATE_ZONE_PARTIAL(long *thisPtr,long packetPtr)

{
  uint uVar1;
  long lVar2;
  long *plVar3;
  long lVar4;
  undefined1 (*pauVar5) [16];
  ushort uVar6;
  bool bVar7;
  uint local_6c;
  long local_68 [4];
  uint local_48;
  undefined1 local_40 [24];
  undefined1 local_28;
  
                    /* UPDATE_ZONE_PARTIAL packet handler. Reads a u16 zone/loc id, resolves the loc
                       via the world list, decodes the partial update into a pending update entry,
                       and records the id in the client update ring buffer. */
  lVar4 = *thisPtr;
  lVar2 = *(long *)((long)&__DT_RELA[0xcfb].r_offset + lVar4);
  if (*(long *)((long)&__DT_RELA[0xff].r_addend + lVar2) == 0) {
    pauVar5 = (undefined1 (*) [16])operator_new(0x38);
    *(undefined8 *)pauVar5[3] = 0;
    *(undefined8 *)pauVar5[2] = 0;
    *(undefined8 *)(pauVar5[2] + 8) = 0x400000003f800000;
    *pauVar5 = (undefined1  [16])0x0;
    *(undefined4 *)pauVar5[3] = 0;
    *(undefined ***)*pauVar5 = &PTR_FUN_0136b598;
    *(undefined8 **)pauVar5[1] = &DAT_01393d40;
    plVar3 = *(long **)((long)&__DT_RELA[0xff].r_addend + lVar2);
    *(undefined8 *)(pauVar5[1] + 8) = 1;
    *(undefined1 (**) [16])((long)&__DT_RELA[0xff].r_addend + lVar2) = pauVar5;
    if (plVar3 != (long *)0x0) {
      (**(code **)(*plVar3 + 8))();
    }
    lVar4 = *thisPtr;
  }
  lVar2 = *(long *)(packetPtr + 0x18);
  local_48 = 0;
  local_28 = 4;
  bVar7 = DAT_01050dc0 == 0x3020100;
  lVar4 = *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar4) + 0x218);
  *(long *)(packetPtr + 0x18) = lVar2 + 2;
  uVar6 = *(ushort *)(*(long *)(packetPtr + 0x10) + lVar2);
  if (bVar7) {
    uVar6 = uVar6 << 8 | uVar6 >> 8;
  }
  plVar3 = *(long **)(lVar4 + 0x38);
  lVar4 = (**(code **)(*plVar3 + 0x40))(plVar3,(uint)uVar6,0);
  plVar3 = *(long **)(*(long *)(*(long *)(lVar4 + 8) + 0x40) + 8);
  local_48 = (uint)uVar6;
  (**(code **)(*plVar3 + 0x20))(plVar3,local_40,packetPtr);
  local_6c = local_48;
  FUN_00ae4c30(local_68,*(long *)((long)&__DT_RELA[0xff].r_addend +
                                 *(long *)((long)&__DT_RELA[0xcfb].r_offset + *thisPtr)) + 8,
               &local_6c);
  FUN_0048f080(local_68[0] + 8,local_40);
  lVar4 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *thisPtr);
  uVar1 = *(uint *)(lVar4 + 0x90);
  *(uint *)(lVar4 + 0x90) = uVar1 + 1;
  *(uint *)(*(long *)(lVar4 + 0x88) + (ulong)(uVar1 & 0x3f) * 4) = local_48;
  FUN_0047fe00(local_40);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_PREFETCH` @ 00138290
```c

/* WARNING: Type propagation algorithm not settling */
/* Setting prototype: undefined * LOC_PREFETCH(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::LOC_PREFETCH(long *thisPtr,long packet)

{
  char cVar1;
  char cVar2;
  undefined4 uVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;
  undefined8 uVar7;
  undefined8 *puVar8;
  ulong uVar9;
  int local_90 [4];
  int local_80;
  int local_7c;
  long local_78;
  long local_70;
  undefined1 local_68;
  undefined1 local_67;
  undefined8 local_64;
  undefined8 local_5c;
  undefined8 local_54;
  undefined8 local_4c;
  undefined8 local_44;
  undefined8 local_3c;
  
                    /* jag::packethandlers::ZoneUpdates::LOC_PREFETCH -- Reads 7 bytes (3 bytes
                       shape/zone-coord + 4-byte LE locId), bounds-checks against zone, allocates a
                       Location entry, then calls LocationContainer::Add. The combinator flag value
                       1 is THE distinguishing marker vs LOC_ADD (which uses 2). The byte at packet
                       offset +1 gets a +0x80 transform (standard 'decode signed-byte from packet'
                       pattern) and is stored at location+0x64. */
  lVar5 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  local_5c = 0x3f80000000000000;
  local_68 = 0;
  local_67 = 0;
  local_64 = 0;
  local_54 = 0;
  local_4c = 0;
  *(long *)(packet + 0x18) = lVar5 + 1;
  local_44 = 0x3f8000003f800000;
  local_3c = 0x3f800000;
  cVar1 = *(char *)(lVar6 + lVar5);
  *(long *)(packet + 0x18) = lVar5 + 2;
  cVar2 = *(char *)(lVar6 + 1 + lVar5);
  *(long *)(packet + 0x18) = lVar5 + 3;
  uVar9 = CONCAT71(0xffffff,-0x80 - *(char *)(lVar6 + 2 + lVar5));
  *(long *)(packet + 0x18) = lVar5 + 7;
  local_90[0] = (uint)*(byte *)(lVar6 + 3 + lVar5) +
                (uint)*(byte *)(lVar6 + 4 + lVar5) * 0x100 +
                (uint)*(byte *)(lVar6 + 6 + lVar5) * 0x1000000 +
                (uint)*(byte *)(lVar6 + 5 + lVar5) * 0x10000;
  FUN_00af9220(&local_68,cVar1 + -0x80);
  local_7c = ((uint)uVar9 & 7) + DAT_013942b0;
  local_80 = ((uint)(uVar9 >> 4) & 7) + DAT_013942ac;
  local_90[1] = 0xffffffff;
  local_90[2] = 1;
  local_90[3] = DAT_013942a8;
  FUN_00137b90(&local_78,local_90 + 2,local_90 + 1,local_90,local_90 + 3,&local_68);
  puVar8 = DAT_015da128;
  lVar5 = *thisPtr;
  lVar6 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar5);
  *(uint *)(local_70 + 100) = (uint)(byte)(cVar2 + 0x80);
  uVar3 = *(undefined4 *)(lVar5 + 0x500);
  uVar4 = *(undefined4 *)((long)&__DT_RELA[0x548].r_addend + *(long *)(lVar6 + 0x2f0));
  uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + lVar5);
  *(undefined8 *)(local_70 + 0x78) = *puVar8;
  *(undefined4 *)(local_70 + 0x70) = uVar3;
  *(undefined4 *)(local_70 + 0x6c) = uVar4;
  game::LocationContainer::Add(uVar7,&local_78);
  if (local_78 != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_DEL` @ 00138450
```c

/* Setting prototype: undefined * LOC_DEL(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::LOC_DEL(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  undefined4 *puVar9;
  undefined4 *puVar10;
  char cVar11;
  long *plVar12;
  long lVar13;
  long *plVar14;
  long lVar15;
  int iVar16;
  long *plVar17;
  long *plVar18;
  long **pplVar19;
  size_t __n;
  undefined4 *puVar20;
  long **__src;
  ulong uVar21;
  ulong uVar22;
  long *plVar23;
  uint uVar24;
  long *plVar25;
  long *plVar26;
  bool bVar27;
  undefined4 *local_630;
  long **local_628;
  undefined4 local_5d0;
  undefined4 local_5cc;
  undefined4 local_5c8;
  undefined4 local_5c4;
  int local_5c0;
  int local_5bc;
  long local_5b8;
  long local_5b0;
  char local_5a8;
  char local_5a7;
  undefined8 local_5a4;
  undefined8 uStack_59c;
  undefined8 local_594;
  undefined8 uStack_58c;
  undefined8 local_584;
  undefined8 uStack_57c;
  undefined1 local_568;
  byte bStack_567;
  undefined2 uStack_566;
  undefined4 uStack_564;
  undefined8 uStack_560;
  undefined8 local_558;
  undefined4 uStack_550;
  undefined4 uStack_54c;
  undefined8 uStack_548;
  undefined4 uStack_540;
  undefined8 uStack_53c;
  long *local_40 [2];
  
                    /* jag::packethandlers::ZoneUpdates::LOC_DEL -- Reads 2-byte loc deletion query
                       (char zoneXY + signed shape/rotation byte), computes zone-relative position,
                       then removes all matching locations (shape, rotation, position-match) from
                       the active world. Handles secondary spawn variants (local flag == 0x02 for
                       respawn, == 0x08 for additional variant).
                       
                       Removes Location(s) from a zone by iterating BOTH containers in the
                       LocationContainer at Client+0x19490:
                         1. STATIC TREE at LC+0x8..LC+0x10 (rbtree sentinel; walk via
                       eastl::rbtree::next): match by shape/coord/locId at node+0x14/+0xc/+0x10.
                       This is where cache-loaded static map locations live.
                         2. DYNAMIC VECTOR at LC+0x60..LC+0x68
                       (eastl::vector<shared_ptr<MapSquareLocation>>, 16B stride; shared_ptr.ptr at
                       entry+0x8): match by shape/coord/locId at ptr+0x14/+0xc/+0x10. This is where
                       LOC_ADD-spawned dynamic locations live (writer is LocationContainer::Add).
                       Engine-side: BOTH containers must be walked to enumerate every scene object
                       in a zone (there are NOT 11 fixed slots at LC+0x40..+0x220). */
  lVar15 = *(long *)(packet + 0x18);
  uStack_59c = 0x3f80000000000000;
  local_5a8 = '\0';
  local_584 = 0x3f8000003f800000;
  local_5a7 = '\0';
  local_5a4 = 0;
  *(long *)(packet + 0x18) = lVar15 + 1;
  local_594 = 0;
  uStack_58c = 0;
  uStack_57c = 0x3f800000;
  cVar11 = *(char *)(*(long *)(packet + 0x10) + lVar15);
  *(long *)(packet + 0x18) = lVar15 + 2;
  uVar24 = -(uint)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar15);
  FUN_00af9220(&local_5a8,-0x80 - cVar11);
  local_5c0 = (uVar24 >> 4 & 7) + DAT_013942ac;
  local_5bc = DAT_013942b0 + (uVar24 & 7);
  local_5c4 = DAT_013942a8;
  local_568 = 0xff;
  bStack_567 = 0xff;
  uStack_566 = 0xffff;
  local_5c8 = 0xffffffff;
  local_5cc = 3;
  FUN_00137b90(&local_5b8);
  lVar15 = *thisPtr;
  if (*(long *)(*(long *)((long)&__DT_RELA[0xcf5].r_offset + lVar15) + 0x20) != 0) {
    FUN_00436790();
    lVar15 = *thisPtr;
  }
  lVar15 = *(long *)((long)&__DT_RELA[0xcfa].r_offset + lVar15);
  puVar9 = &uStack_540;
  local_558 = local_40;
  plVar14 = (long *)(lVar15 + 8);
  uStack_548._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
  plVar12 = *(long **)(lVar15 + 0x10);
  local_568 = SUB81(puVar9,0);
  bStack_567 = (byte)((ulong)puVar9 >> 8);
  uStack_566 = (undefined2)((ulong)puVar9 >> 0x10);
  uStack_560 = puVar9;
  puVar10 = puVar9;
  puVar20 = puVar9;
  local_40[0] = plVar14;
  uStack_564 = uStack_548._4_4_;
joined_r0x0013861a:
  uStack_548 = puVar20;
  if (plVar14 != plVar12) {
    do {
      uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
      uStack_560._0_4_ = SUB84(puVar10,0);
      uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
      uStack_548._0_4_ = SUB84(puVar9,0);
      bVar3 = *(byte *)(local_5b0 + 0x14);
      if ((bVar3 < 4) || (bVar3 == 0x17)) {
        uVar22 = 0;
      }
      else if ((bVar3 < 9) || (bVar3 == 0x18)) {
        uVar22 = 0x40000000;
      }
      else {
        uVar22 = 0xc0000000;
        if (bVar3 < 0x16) {
          uVar22 = 0x80000000;
        }
      }
      if (((long)(int)plVar12[4] << 0x1e | (ulong)*(uint *)(plVar12 + 5) << 0xe |
           (ulong)*(uint *)((long)plVar12 + 0x2c) |
           (ulong)((*(uint *)((long)plVar12 + 0x24) & 3) << 0x1c) |
          (ulong)((char)plVar12[6] == '\0') << 0x20) ==
          ((ulong)*(uint *)(local_5b0 + 0xc) << 0xe | (ulong)*(uint *)(local_5b0 + 0x10) |
           (ulong)((*(uint *)(local_5b0 + 8) & 3) << 0x1c) |
           (ulong)(1 < (byte)(bVar3 - 0x17)) << 0x20 | uVar22)) {
        lVar6 = plVar12[8];
        lVar7 = *(long *)(lVar6 + 0x50);
        *(undefined8 *)(lVar6 + 8) = 0xffffffff;
        *(undefined4 *)(lVar6 + 0x10) = 0;
        *(undefined1 *)(lVar6 + 0x15) = 0;
        *(undefined4 *)(lVar6 + 0x4c) = 0xffffffff;
        *(undefined8 *)(lVar6 + 0x50) = 0;
        *(undefined8 *)(lVar6 + 0x58) = 0;
        if (lVar7 != 0) {
          ref_counter_base::DecRef();
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        *(undefined4 *)(lVar6 + 0x60) = 0xffffffff;
        pplVar19 = (long **)CONCAT44(uStack_560._4_4_,(undefined4)uStack_560);
        lVar6 = plVar12[4];
        uVar4 = *(undefined4 *)((long)plVar12 + 0x24);
        lVar7 = plVar12[5];
        uVar5 = *(undefined4 *)((long)plVar12 + 0x2c);
        lVar8 = plVar12[6];
        if (pplVar19 < local_558) goto code_r0x00138788;
        __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,local_568)));
        lVar13 = (long)pplVar19 - (long)__src >> 2;
        if (lVar13 * -0x3333333333333333 == 0) {
          lVar13 = 0x14;
LAB_001390b6:
          local_630 = (undefined4 *)FUN_00c29480(lVar13);
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
          pplVar19 = (long **)CONCAT44(uStack_560._4_4_,(undefined4)uStack_560);
          __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,local_568)));
          local_628 = (long **)(lVar13 + (long)local_630);
        }
        else {
          if (lVar13 * -0x6666666666666666 != 0) {
            lVar13 = lVar13 << 3;
            goto LAB_001390b6;
          }
          local_628 = (long **)0x0;
          local_630 = (undefined4 *)0x0;
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        puVar20 = local_630;
        if (pplVar19 != __src) {
          __n = (long)pplVar19 - (long)__src;
          memmove(local_630,__src,__n);
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
          __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,local_568)));
          puVar20 = (undefined4 *)(__n + (long)local_630);
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        *puVar20 = (int)lVar6;
        puVar20[1] = uVar4;
        puVar20[2] = (int)lVar7;
        puVar20[3] = uVar5;
        *(char *)(puVar20 + 4) = (char)lVar8;
        if ((__src != (long **)0x0) &&
           ((long **)CONCAT44(uStack_548._4_4_,(undefined4)uStack_548) != __src)) {
          HeapInterface::Free(__src);
        }
        local_568 = SUB81(local_630,0);
        bStack_567 = (byte)((ulong)local_630 >> 8);
        uStack_566 = (undefined2)((ulong)local_630 >> 0x10);
        uStack_564 = (undefined4)((ulong)local_630 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar20 + 5,0);
        puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
        uStack_560._4_4_ = (undefined4)((ulong)(puVar20 + 5) >> 0x20);
        puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
        local_558 = local_628;
      }
      uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
      uStack_560._0_4_ = SUB84(puVar10,0);
      uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
      uStack_548._0_4_ = SUB84(puVar9,0);
      plVar12 = (long *)eastl::rbtree::next(plVar12);
      puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
      puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
      if (plVar14 == plVar12) break;
    } while( true );
  }
  plVar14 = *(long **)(lVar15 + 0x60);
  uVar22 = 0;
  plVar12 = *(long **)(lVar15 + 0x68);
  if (plVar14 != *(long **)(lVar15 + 0x68)) {
    do {
      iVar16 = (int)uVar22;
      plVar17 = plVar14 + uVar22 * 2;
      lVar6 = plVar17[1];
      bVar3 = *(byte *)(lVar6 + 0x14);
      if ((bVar3 < 4) || (bVar3 == 0x17)) {
        uVar22 = 0;
      }
      else if ((bVar3 < 9) || (bVar3 == 0x18)) {
        uVar22 = 0x40000000;
      }
      else {
        uVar22 = 0x80000000;
        if (0x15 < bVar3) {
          uVar22 = 0xc0000000;
        }
      }
      bVar2 = *(byte *)(local_5b0 + 0x14);
      if ((bVar2 < 4) || (bVar2 == 0x17)) {
        uVar21 = 0;
      }
      else if ((bVar2 < 9) || (bVar2 == 0x18)) {
        uVar21 = 0x40000000;
      }
      else {
        uVar21 = 0x80000000;
        if (0x15 < bVar2) {
          uVar21 = 0xc0000000;
        }
      }
      plVar18 = plVar12;
      if (((ulong)*(uint *)(local_5b0 + 0xc) << 0xe | (ulong)*(uint *)(local_5b0 + 0x10) |
           (ulong)((*(uint *)(local_5b0 + 8) & 3) << 0x1c) |
           (ulong)(1 < (byte)(bVar2 - 0x17)) << 0x20 | uVar21) ==
          ((ulong)*(uint *)(lVar6 + 0xc) << 0xe | (ulong)*(uint *)(lVar6 + 0x10) |
           (ulong)((*(uint *)(lVar6 + 8) & 3) << 0x1c) | (ulong)(1 < (byte)(bVar3 - 0x17)) << 0x20 |
          uVar22)) {
        plVar14 = plVar17 + 2;
        if ((plVar14 < plVar12) &&
           (uVar22 = (long)plVar12 - (long)plVar14 >> 4, 0 < (long)plVar12 - (long)plVar14)) {
          uVar24 = (uint)uVar22 & 3;
          if ((uVar22 & 3) == 0) goto LAB_00138e45;
          if (uVar24 != 1) {
            if (uVar24 != 2) {
              lVar6 = plVar17[2];
              lVar7 = plVar17[3];
              plVar17[2] = 0;
              lVar8 = *plVar17;
              plVar17[3] = 0;
              *plVar17 = lVar6;
              plVar17[1] = lVar7;
              if (lVar8 != 0) {
                ref_counter_base::DecRef();
              }
              uVar22 = uVar22 - 1;
              plVar14 = plVar17 + 4;
            }
            lVar6 = plVar14[-2];
            lVar7 = *plVar14;
            lVar8 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar7;
            plVar14[-1] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            uVar22 = uVar22 - 1;
            plVar14 = plVar14 + 2;
          }
          lVar6 = plVar14[-2];
          lVar7 = *plVar14;
          lVar8 = plVar14[1];
          *plVar14 = 0;
          plVar14[1] = 0;
          plVar14[-2] = lVar7;
          plVar14[-1] = lVar8;
          if (lVar6 != 0) {
            ref_counter_base::DecRef();
          }
          plVar14 = plVar14 + 2;
          for (uVar22 = uVar22 - 1; uVar22 != 0; uVar22 = uVar22 - 4) {
LAB_00138e45:
            lVar6 = plVar14[-2];
            lVar7 = *plVar14;
            lVar8 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar7;
            plVar14[-1] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[2];
            lVar7 = plVar14[3];
            plVar14[2] = 0;
            lVar8 = *plVar14;
            plVar14[3] = 0;
            *plVar14 = lVar6;
            plVar14[1] = lVar7;
            if (lVar8 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[2];
            lVar7 = plVar14[4];
            lVar8 = plVar14[5];
            plVar14[4] = 0;
            plVar14[5] = 0;
            plVar14[2] = lVar7;
            plVar14[3] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[4];
            lVar7 = plVar14[6];
            lVar8 = plVar14[7];
            plVar14[6] = 0;
            plVar14[7] = 0;
            plVar14[4] = lVar7;
            plVar14[5] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            plVar14 = plVar14 + 8;
          }
          plVar12 = *(long **)(lVar15 + 0x68);
        }
        plVar18 = plVar12 + -2;
        lVar6 = plVar12[-2];
        *(long **)(lVar15 + 0x68) = plVar18;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
          plVar18 = *(long **)(lVar15 + 0x68);
        }
        iVar16 = iVar16 + -1;
        plVar14 = *(long **)(lVar15 + 0x60);
      }
      uVar22 = (ulong)(iVar16 + 1);
      plVar12 = plVar18;
    } while (uVar22 < (ulong)((long)plVar18 - (long)plVar14 >> 4));
  }
  puVar9 = (undefined4 *)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,local_568)));
  puVar10 = uStack_560;
  if (puVar9 == uStack_560) goto LAB_00138b0f;
  puVar20 = puVar9 + 1;
  uVar22 = (long)uStack_560 - (long)(puVar9 + 5);
  do {
    plVar12 = local_40[0];
    plVar14 = (long *)local_40[0][2];
    if (plVar14 == (long *)0x0) {
      bVar27 = (long *)local_40[0][1] != local_40[0];
LAB_00138ccd:
      if (!bVar27) {
LAB_00138cd6:
        uStack_560 = puVar10;
        FUN_00486760(plVar12,plVar14);
        *plVar12 = (long)plVar12;
        plVar12[1] = (long)plVar12;
        plVar12[2] = 0;
        *(undefined1 *)(plVar12 + 3) = 0;
        plVar12[4] = 0;
        puVar10 = uStack_560;
      }
    }
    else {
      iVar16 = puVar20[-1];
      plVar17 = plVar14;
      plVar18 = local_40[0];
      do {
        while (plVar25 = plVar17, plVar23 = plVar14, plVar26 = plVar12, iVar16 < (int)plVar25[4]) {
LAB_001389e6:
          plVar17 = (long *)plVar25[1];
LAB_001389ed:
          plVar18 = plVar25;
          if (plVar17 == (long *)0x0) goto LAB_00138a10;
        }
        if (iVar16 <= (int)plVar25[4]) {
          if (*(byte *)(puVar20 + 3) < *(byte *)(plVar25 + 6)) goto LAB_001389e6;
          if (*(byte *)(puVar20 + 3) <= *(byte *)(plVar25 + 6)) {
            uStack_560 = puVar10;
            cVar11 = FUN_006d42e0(puVar20,(long)plVar25 + 0x24);
            puVar10 = uStack_560;
            if (cVar11 != '\0') {
              plVar17 = (long *)plVar25[1];
              goto LAB_001389ed;
            }
          }
        }
        plVar17 = (long *)*plVar25;
      } while ((long *)*plVar25 != (long *)0x0);
LAB_00138a10:
      do {
        plVar17 = plVar23;
        if ((int)plVar17[4] < iVar16) {
LAB_00138a00:
          plVar23 = (long *)*plVar17;
          if ((long *)*plVar17 == (long *)0x0) break;
          goto LAB_00138a10;
        }
        if ((int)plVar17[4] <= iVar16) {
          if (*(byte *)(plVar17 + 6) < *(byte *)(puVar20 + 3)) goto LAB_00138a00;
          if (*(byte *)(plVar17 + 6) == *(byte *)(puVar20 + 3)) {
            uStack_560 = puVar10;
            cVar11 = FUN_006d42e0((long)plVar17 + 0x24,puVar20);
            puVar10 = uStack_560;
            if (cVar11 != '\0') goto LAB_00138a00;
          }
        }
        plVar23 = (long *)plVar17[1];
        plVar26 = plVar17;
      } while ((long *)plVar17[1] != (long *)0x0);
      bVar27 = (long *)plVar12[1] != plVar26 || plVar12 != plVar18;
      plVar17 = plVar26;
      if (plVar18 == plVar26) goto LAB_00138ccd;
      do {
        plVar23 = (long *)*plVar17;
        if ((long *)*plVar17 == (long *)0x0) {
          plVar25 = (long *)plVar17[2];
          if (plVar17 == (long *)*plVar25) {
            do {
              plVar23 = plVar17;
              plVar17 = plVar25;
              plVar25 = (long *)plVar17[2];
            } while ((long *)*plVar25 == plVar17);
            if (plVar25 == plVar23) {
              plVar25 = plVar17;
            }
          }
        }
        else {
          do {
            plVar25 = plVar23;
            plVar23 = (long *)plVar25[1];
          } while ((long *)plVar25[1] != (long *)0x0);
        }
        plVar17 = plVar25;
      } while (plVar25 != plVar18);
      if ((long *)plVar12[1] == plVar26 && plVar12 == plVar18) goto LAB_00138cd6;
      do {
        plVar14 = (long *)*plVar26;
        plVar12[4] = plVar12[4] + -1;
        if (plVar14 == (long *)0x0) {
          plVar18 = (long *)plVar26[2];
          plVar14 = plVar26;
          if ((long *)*plVar18 == plVar26) {
            do {
              plVar23 = plVar14;
              plVar14 = plVar18;
              plVar17 = (long *)plVar14[2];
              plVar18 = plVar17;
            } while (plVar14 == (long *)*plVar17);
            plVar18 = plVar14;
            if (plVar17 != plVar23) {
              plVar18 = plVar17;
            }
          }
        }
        else {
          do {
            plVar17 = plVar14 + 1;
            plVar18 = plVar14;
            plVar14 = (long *)*plVar17;
          } while ((long *)*plVar17 != (long *)0x0);
        }
        uStack_560 = puVar10;
        FUN_00aa4780(plVar26,plVar12);
        plVar14 = (long *)plVar26[7];
        if (plVar14 != (long *)0x0) {
          LOCK();
          plVar17 = plVar14 + 1;
          lVar15 = *plVar17;
          *(int *)plVar17 = (int)*plVar17 + -1;
          UNLOCK();
          if ((int)lVar15 == 1) {
            (**(code **)(*plVar14 + 0x10))(plVar14);
            LOCK();
            piVar1 = (int *)((long)plVar14 + 0xc);
            iVar16 = *piVar1;
            *piVar1 = *piVar1 + -1;
            UNLOCK();
            if (iVar16 == 1) {
              (**(code **)(*plVar14 + 0x18))(plVar14);
            }
          }
        }
        HeapInterface::Free(plVar26);
        plVar26 = plVar18;
        puVar10 = uStack_560;
      } while (plVar25 != plVar18);
    }
    puVar20 = puVar20 + 5;
    if (puVar20 == (undefined4 *)((long)puVar9 + (uVar22 & 0xfffffffffffffffc) + 0x18)) {
      uStack_560 = (undefined4 *)
                   CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,local_568)));
LAB_00138b0f:
      if ((uStack_560 != (undefined4 *)0x0) && (uStack_548 != uStack_560)) {
        uStack_560 = puVar10;
        HeapInterface::Free();
        puVar10 = uStack_560;
      }
      uStack_560 = puVar10;
      game::LocationContainer::Add
                (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_5b8);
      if (local_5b8 != 0) {
        ref_counter_base::DecRef();
      }
      if (local_5a8 == '\x02') {
        local_568 = 0x17;
        local_5c8 = 0xffffffff;
        bStack_567 = local_5a7 + 1U & 3;
        local_5cc = 0xffffffff;
        local_5d0 = 3;
        uStack_564 = (undefined4)local_5a4;
        uStack_560._0_4_ = (undefined4)((ulong)local_5a4 >> 0x20);
        uStack_560._4_4_ = (undefined4)uStack_59c;
        local_558._0_4_ = (undefined4)((ulong)uStack_59c >> 0x20);
        local_558._4_4_ = (undefined4)local_594;
        uStack_550 = (undefined4)((ulong)local_594 >> 0x20);
        uStack_54c = (undefined4)uStack_58c;
        uStack_548._0_4_ = (undefined4)((ulong)uStack_58c >> 0x20);
        uStack_548._4_4_ = (undefined4)local_584;
        uStack_540 = (undefined4)((ulong)local_584 >> 0x20);
        uStack_53c = uStack_57c;
        FUN_00137b90(&local_5b8,&local_5d0,&local_5cc,&local_5c8,&local_5c4,&local_568);
        game::LocationContainer::Add
                  (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_5b8);
        if (local_5b8 != 0) {
          ref_counter_base::DecRef();
        }
      }
      if (local_5a8 == '\b') {
        local_568 = 0x18;
        local_5c8 = 0xffffffff;
        local_5cc = 0xffffffff;
        bStack_567 = local_5a7 + 2U & 3;
        local_5d0 = 3;
        uStack_564 = (undefined4)local_5a4;
        uStack_560._0_4_ = (undefined4)((ulong)local_5a4 >> 0x20);
        uStack_560._4_4_ = (undefined4)uStack_59c;
        local_558._0_4_ = (undefined4)((ulong)uStack_59c >> 0x20);
        local_558._4_4_ = (undefined4)local_594;
        uStack_550 = (undefined4)((ulong)local_594 >> 0x20);
        uStack_54c = (undefined4)uStack_58c;
        uStack_548._0_4_ = (undefined4)((ulong)uStack_58c >> 0x20);
        uStack_548._4_4_ = (undefined4)local_584;
        uStack_540 = (undefined4)((ulong)local_584 >> 0x20);
        uStack_53c = uStack_57c;
        FUN_00137b90(&local_5b8,&local_5d0,&local_5cc,&local_5c8,&local_5c4,&local_568);
        game::LocationContainer::Add
                  (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_5b8);
        if (local_5b8 != 0) {
          ref_counter_base::DecRef();
        }
      }
      return &DAT_015d3620;
    }
  } while( true );
code_r0x00138788:
  uStack_560 = (undefined4 *)((long)pplVar19 + 0x14);
  *(int *)pplVar19 = (int)lVar6;
  *(undefined4 *)((long)pplVar19 + 4) = uVar4;
  *(int *)(pplVar19 + 1) = (int)lVar7;
  *(undefined4 *)((long)pplVar19 + 0xc) = uVar5;
  *(char *)(pplVar19 + 2) = (char)lVar8;
  plVar12 = (long *)eastl::rbtree::next(plVar12);
  puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
  puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
  puVar20 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_548);
  goto joined_r0x0013861a;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_DEL` @ 001391f0
```c

undefined * jag::packethandlers::ZoneUpdates::LOC_DEL(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  undefined4 uVar4;
  undefined4 uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  undefined4 *puVar9;
  undefined4 *puVar10;
  char cVar11;
  long *plVar12;
  long lVar13;
  long *plVar14;
  long lVar15;
  int iVar16;
  long *plVar17;
  long *plVar18;
  long **pplVar19;
  size_t __n;
  undefined4 *puVar20;
  long **__src;
  ulong uVar21;
  ulong uVar22;
  long *plVar23;
  uint uVar24;
  long *plVar25;
  long *plVar26;
  bool bVar27;
  undefined4 *puStack_630;
  long **pplStack_628;
  undefined4 uStack_5d0;
  undefined4 uStack_5cc;
  undefined4 uStack_5c8;
  undefined4 uStack_5c4;
  int iStack_5c0;
  int iStack_5bc;
  long lStack_5b8;
  long lStack_5b0;
  char cStack_5a8;
  char cStack_5a7;
  undefined8 uStack_5a4;
  undefined8 uStack_59c;
  undefined8 uStack_594;
  undefined8 uStack_58c;
  undefined8 uStack_584;
  undefined8 uStack_57c;
  undefined1 uStack_568;
  byte bStack_567;
  undefined2 uStack_566;
  undefined4 uStack_564;
  undefined8 uStack_560;
  undefined8 uStack_558;
  undefined4 uStack_550;
  undefined4 uStack_54c;
  undefined8 uStack_548;
  undefined4 uStack_540;
  undefined8 uStack_53c;
  long *aplStack_40 [2];
  
  lVar15 = *(long *)(packet + 0x18);
  uStack_59c = 0x3f80000000000000;
  cStack_5a8 = '\0';
  uStack_584 = 0x3f8000003f800000;
  cStack_5a7 = '\0';
  uStack_5a4 = 0;
  *(long *)(packet + 0x18) = lVar15 + 1;
  uStack_594 = 0;
  uStack_58c = 0;
  uStack_57c = 0x3f800000;
  cVar11 = *(char *)(*(long *)(packet + 0x10) + lVar15);
  *(long *)(packet + 0x18) = lVar15 + 2;
  uVar24 = -(uint)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar15);
  FUN_00af9220(&cStack_5a8,-0x80 - cVar11);
  iStack_5c0 = (uVar24 >> 4 & 7) + DAT_013942ac;
  iStack_5bc = DAT_013942b0 + (uVar24 & 7);
  uStack_5c4 = DAT_013942a8;
  uStack_568 = 0xff;
  bStack_567 = 0xff;
  uStack_566 = 0xffff;
  uStack_5c8 = 0xffffffff;
  uStack_5cc = 3;
  FUN_00137b90(&lStack_5b8);
  lVar15 = *thisPtr;
  if (*(long *)(*(long *)((long)&__DT_RELA[0xcf5].r_offset + lVar15) + 0x20) != 0) {
    FUN_00436790();
    lVar15 = *thisPtr;
  }
  lVar15 = *(long *)((long)&__DT_RELA[0xcfa].r_offset + lVar15);
  puVar9 = &uStack_540;
  uStack_558 = aplStack_40;
  plVar14 = (long *)(lVar15 + 8);
  uStack_548._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
  plVar12 = *(long **)(lVar15 + 0x10);
  uStack_568 = SUB81(puVar9,0);
  bStack_567 = (byte)((ulong)puVar9 >> 8);
  uStack_566 = (undefined2)((ulong)puVar9 >> 0x10);
  uStack_560 = puVar9;
  puVar10 = puVar9;
  puVar20 = puVar9;
  aplStack_40[0] = plVar14;
  uStack_564 = uStack_548._4_4_;
joined_r0x0013861a:
  uStack_548 = puVar20;
  if (plVar14 != plVar12) {
    do {
      uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
      uStack_560._0_4_ = SUB84(puVar10,0);
      uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
      uStack_548._0_4_ = SUB84(puVar9,0);
      bVar3 = *(byte *)(lStack_5b0 + 0x14);
      if ((bVar3 < 4) || (bVar3 == 0x17)) {
        uVar22 = 0;
      }
      else if ((bVar3 < 9) || (bVar3 == 0x18)) {
        uVar22 = 0x40000000;
      }
      else {
        uVar22 = 0xc0000000;
        if (bVar3 < 0x16) {
          uVar22 = 0x80000000;
        }
      }
      if (((long)(int)plVar12[4] << 0x1e | (ulong)*(uint *)(plVar12 + 5) << 0xe |
           (ulong)*(uint *)((long)plVar12 + 0x2c) |
           (ulong)((*(uint *)((long)plVar12 + 0x24) & 3) << 0x1c) |
          (ulong)((char)plVar12[6] == '\0') << 0x20) ==
          ((ulong)*(uint *)(lStack_5b0 + 0xc) << 0xe | (ulong)*(uint *)(lStack_5b0 + 0x10) |
           (ulong)((*(uint *)(lStack_5b0 + 8) & 3) << 0x1c) |
           (ulong)(1 < (byte)(bVar3 - 0x17)) << 0x20 | uVar22)) {
        lVar6 = plVar12[8];
        lVar7 = *(long *)(lVar6 + 0x50);
        *(undefined8 *)(lVar6 + 8) = 0xffffffff;
        *(undefined4 *)(lVar6 + 0x10) = 0;
        *(undefined1 *)(lVar6 + 0x15) = 0;
        *(undefined4 *)(lVar6 + 0x4c) = 0xffffffff;
        *(undefined8 *)(lVar6 + 0x50) = 0;
        *(undefined8 *)(lVar6 + 0x58) = 0;
        if (lVar7 != 0) {
          ref_counter_base::DecRef();
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        *(undefined4 *)(lVar6 + 0x60) = 0xffffffff;
        pplVar19 = (long **)CONCAT44(uStack_560._4_4_,(undefined4)uStack_560);
        lVar6 = plVar12[4];
        uVar4 = *(undefined4 *)((long)plVar12 + 0x24);
        lVar7 = plVar12[5];
        uVar5 = *(undefined4 *)((long)plVar12 + 0x2c);
        lVar8 = plVar12[6];
        if (pplVar19 < uStack_558) goto code_r0x00138788;
        __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,uStack_568)));
        lVar13 = (long)pplVar19 - (long)__src >> 2;
        if (lVar13 * -0x3333333333333333 == 0) {
          lVar13 = 0x14;
LAB_001390b6:
          puStack_630 = (undefined4 *)FUN_00c29480(lVar13);
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
          pplVar19 = (long **)CONCAT44(uStack_560._4_4_,(undefined4)uStack_560);
          __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,uStack_568)))
          ;
          pplStack_628 = (long **)(lVar13 + (long)puStack_630);
        }
        else {
          if (lVar13 * -0x6666666666666666 != 0) {
            lVar13 = lVar13 << 3;
            goto LAB_001390b6;
          }
          pplStack_628 = (long **)0x0;
          puStack_630 = (undefined4 *)0x0;
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        puVar20 = puStack_630;
        if (pplVar19 != __src) {
          __n = (long)pplVar19 - (long)__src;
          memmove(puStack_630,__src,__n);
          puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
          puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
          __src = (long **)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,uStack_568)))
          ;
          puVar20 = (undefined4 *)(__n + (long)puStack_630);
        }
        uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar10,0);
        uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
        uStack_548._0_4_ = SUB84(puVar9,0);
        *puVar20 = (int)lVar6;
        puVar20[1] = uVar4;
        puVar20[2] = (int)lVar7;
        puVar20[3] = uVar5;
        *(char *)(puVar20 + 4) = (char)lVar8;
        if ((__src != (long **)0x0) &&
           ((long **)CONCAT44(uStack_548._4_4_,(undefined4)uStack_548) != __src)) {
          HeapInterface::Free(__src);
        }
        uStack_568 = SUB81(puStack_630,0);
        bStack_567 = (byte)((ulong)puStack_630 >> 8);
        uStack_566 = (undefined2)((ulong)puStack_630 >> 0x10);
        uStack_564 = (undefined4)((ulong)puStack_630 >> 0x20);
        uStack_560._0_4_ = SUB84(puVar20 + 5,0);
        puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
        uStack_560._4_4_ = (undefined4)((ulong)(puVar20 + 5) >> 0x20);
        puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
        uStack_558 = pplStack_628;
      }
      uStack_548._4_4_ = (undefined4)((ulong)puVar10 >> 0x20);
      uStack_560._0_4_ = SUB84(puVar10,0);
      uStack_560._4_4_ = (undefined4)((ulong)puVar9 >> 0x20);
      uStack_548._0_4_ = SUB84(puVar9,0);
      plVar12 = (long *)eastl::rbtree::next(plVar12);
      puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
      puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
      if (plVar14 == plVar12) break;
    } while( true );
  }
  plVar14 = *(long **)(lVar15 + 0x60);
  uVar22 = 0;
  plVar12 = *(long **)(lVar15 + 0x68);
  if (plVar14 != *(long **)(lVar15 + 0x68)) {
    do {
      iVar16 = (int)uVar22;
      plVar17 = plVar14 + uVar22 * 2;
      lVar6 = plVar17[1];
      bVar3 = *(byte *)(lVar6 + 0x14);
      if ((bVar3 < 4) || (bVar3 == 0x17)) {
        uVar22 = 0;
      }
      else if ((bVar3 < 9) || (bVar3 == 0x18)) {
        uVar22 = 0x40000000;
      }
      else {
        uVar22 = 0x80000000;
        if (0x15 < bVar3) {
          uVar22 = 0xc0000000;
        }
      }
      bVar2 = *(byte *)(lStack_5b0 + 0x14);
      if ((bVar2 < 4) || (bVar2 == 0x17)) {
        uVar21 = 0;
      }
      else if ((bVar2 < 9) || (bVar2 == 0x18)) {
        uVar21 = 0x40000000;
      }
      else {
        uVar21 = 0x80000000;
        if (0x15 < bVar2) {
          uVar21 = 0xc0000000;
        }
      }
      plVar18 = plVar12;
      if (((ulong)*(uint *)(lStack_5b0 + 0xc) << 0xe | (ulong)*(uint *)(lStack_5b0 + 0x10) |
           (ulong)((*(uint *)(lStack_5b0 + 8) & 3) << 0x1c) |
           (ulong)(1 < (byte)(bVar2 - 0x17)) << 0x20 | uVar21) ==
          ((ulong)*(uint *)(lVar6 + 0xc) << 0xe | (ulong)*(uint *)(lVar6 + 0x10) |
           (ulong)((*(uint *)(lVar6 + 8) & 3) << 0x1c) | (ulong)(1 < (byte)(bVar3 - 0x17)) << 0x20 |
          uVar22)) {
        plVar14 = plVar17 + 2;
        if ((plVar14 < plVar12) &&
           (uVar22 = (long)plVar12 - (long)plVar14 >> 4, 0 < (long)plVar12 - (long)plVar14)) {
          uVar24 = (uint)uVar22 & 3;
          if ((uVar22 & 3) == 0) goto LAB_00138e45;
          if (uVar24 != 1) {
            if (uVar24 != 2) {
              lVar6 = plVar17[2];
              lVar7 = plVar17[3];
              plVar17[2] = 0;
              lVar8 = *plVar17;
              plVar17[3] = 0;
              *plVar17 = lVar6;
              plVar17[1] = lVar7;
              if (lVar8 != 0) {
                ref_counter_base::DecRef();
              }
              uVar22 = uVar22 - 1;
              plVar14 = plVar17 + 4;
            }
            lVar6 = plVar14[-2];
            lVar7 = *plVar14;
            lVar8 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar7;
            plVar14[-1] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            uVar22 = uVar22 - 1;
            plVar14 = plVar14 + 2;
          }
          lVar6 = plVar14[-2];
          lVar7 = *plVar14;
          lVar8 = plVar14[1];
          *plVar14 = 0;
          plVar14[1] = 0;
          plVar14[-2] = lVar7;
          plVar14[-1] = lVar8;
          if (lVar6 != 0) {
            ref_counter_base::DecRef();
          }
          plVar14 = plVar14 + 2;
          for (uVar22 = uVar22 - 1; uVar22 != 0; uVar22 = uVar22 - 4) {
LAB_00138e45:
            lVar6 = plVar14[-2];
            lVar7 = *plVar14;
            lVar8 = plVar14[1];
            *plVar14 = 0;
            plVar14[1] = 0;
            plVar14[-2] = lVar7;
            plVar14[-1] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[2];
            lVar7 = plVar14[3];
            plVar14[2] = 0;
            lVar8 = *plVar14;
            plVar14[3] = 0;
            *plVar14 = lVar6;
            plVar14[1] = lVar7;
            if (lVar8 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[2];
            lVar7 = plVar14[4];
            lVar8 = plVar14[5];
            plVar14[4] = 0;
            plVar14[5] = 0;
            plVar14[2] = lVar7;
            plVar14[3] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = plVar14[4];
            lVar7 = plVar14[6];
            lVar8 = plVar14[7];
            plVar14[6] = 0;
            plVar14[7] = 0;
            plVar14[4] = lVar7;
            plVar14[5] = lVar8;
            if (lVar6 != 0) {
              ref_counter_base::DecRef();
            }
            plVar14 = plVar14 + 8;
          }
          plVar12 = *(long **)(lVar15 + 0x68);
        }
        plVar18 = plVar12 + -2;
        lVar6 = plVar12[-2];
        *(long **)(lVar15 + 0x68) = plVar18;
        if (lVar6 != 0) {
          ref_counter_base::DecRef();
          plVar18 = *(long **)(lVar15 + 0x68);
        }
        iVar16 = iVar16 + -1;
        plVar14 = *(long **)(lVar15 + 0x60);
      }
      uVar22 = (ulong)(iVar16 + 1);
      plVar12 = plVar18;
    } while (uVar22 < (ulong)((long)plVar18 - (long)plVar14 >> 4));
  }
  puVar9 = (undefined4 *)CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,uStack_568)));
  puVar10 = uStack_560;
  if (puVar9 == uStack_560) goto LAB_00138b0f;
  puVar20 = puVar9 + 1;
  uVar22 = (long)uStack_560 - (long)(puVar9 + 5);
  do {
    plVar12 = aplStack_40[0];
    plVar14 = (long *)aplStack_40[0][2];
    if (plVar14 == (long *)0x0) {
      bVar27 = (long *)aplStack_40[0][1] != aplStack_40[0];
LAB_00138ccd:
      if (!bVar27) {
LAB_00138cd6:
        uStack_560 = puVar10;
        FUN_00486760(plVar12,plVar14);
        *plVar12 = (long)plVar12;
        plVar12[1] = (long)plVar12;
        plVar12[2] = 0;
        *(undefined1 *)(plVar12 + 3) = 0;
        plVar12[4] = 0;
        puVar10 = uStack_560;
      }
    }
    else {
      iVar16 = puVar20[-1];
      plVar17 = plVar14;
      plVar18 = aplStack_40[0];
      do {
        while (plVar25 = plVar17, plVar23 = plVar14, plVar26 = plVar12, iVar16 < (int)plVar25[4]) {
LAB_001389e6:
          plVar17 = (long *)plVar25[1];
LAB_001389ed:
          plVar18 = plVar25;
          if (plVar17 == (long *)0x0) goto LAB_00138a10;
        }
        if (iVar16 <= (int)plVar25[4]) {
          if (*(byte *)(puVar20 + 3) < *(byte *)(plVar25 + 6)) goto LAB_001389e6;
          if (*(byte *)(puVar20 + 3) <= *(byte *)(plVar25 + 6)) {
            uStack_560 = puVar10;
            cVar11 = FUN_006d42e0(puVar20,(long)plVar25 + 0x24);
            puVar10 = uStack_560;
            if (cVar11 != '\0') {
              plVar17 = (long *)plVar25[1];
              goto LAB_001389ed;
            }
          }
        }
        plVar17 = (long *)*plVar25;
      } while ((long *)*plVar25 != (long *)0x0);
LAB_00138a10:
      do {
        plVar17 = plVar23;
        if ((int)plVar17[4] < iVar16) {
LAB_00138a00:
          plVar23 = (long *)*plVar17;
          if ((long *)*plVar17 == (long *)0x0) break;
          goto LAB_00138a10;
        }
        if ((int)plVar17[4] <= iVar16) {
          if (*(byte *)(plVar17 + 6) < *(byte *)(puVar20 + 3)) goto LAB_00138a00;
          if (*(byte *)(plVar17 + 6) == *(byte *)(puVar20 + 3)) {
            uStack_560 = puVar10;
            cVar11 = FUN_006d42e0((long)plVar17 + 0x24,puVar20);
            puVar10 = uStack_560;
            if (cVar11 != '\0') goto LAB_00138a00;
          }
        }
        plVar23 = (long *)plVar17[1];
        plVar26 = plVar17;
      } while ((long *)plVar17[1] != (long *)0x0);
      bVar27 = (long *)plVar12[1] != plVar26 || plVar12 != plVar18;
      plVar17 = plVar26;
      if (plVar18 == plVar26) goto LAB_00138ccd;
      do {
        plVar23 = (long *)*plVar17;
        if ((long *)*plVar17 == (long *)0x0) {
          plVar25 = (long *)plVar17[2];
          if (plVar17 == (long *)*plVar25) {
            do {
              plVar23 = plVar17;
              plVar17 = plVar25;
              plVar25 = (long *)plVar17[2];
            } while ((long *)*plVar25 == plVar17);
            if (plVar25 == plVar23) {
              plVar25 = plVar17;
            }
          }
        }
        else {
          do {
            plVar25 = plVar23;
            plVar23 = (long *)plVar25[1];
          } while ((long *)plVar25[1] != (long *)0x0);
        }
        plVar17 = plVar25;
      } while (plVar25 != plVar18);
      if ((long *)plVar12[1] == plVar26 && plVar12 == plVar18) goto LAB_00138cd6;
      do {
        plVar14 = (long *)*plVar26;
        plVar12[4] = plVar12[4] + -1;
        if (plVar14 == (long *)0x0) {
          plVar18 = (long *)plVar26[2];
          plVar14 = plVar26;
          if ((long *)*plVar18 == plVar26) {
            do {
              plVar23 = plVar14;
              plVar14 = plVar18;
              plVar17 = (long *)plVar14[2];
              plVar18 = plVar17;
            } while (plVar14 == (long *)*plVar17);
            plVar18 = plVar14;
            if (plVar17 != plVar23) {
              plVar18 = plVar17;
            }
          }
        }
        else {
          do {
            plVar17 = plVar14 + 1;
            plVar18 = plVar14;
            plVar14 = (long *)*plVar17;
          } while ((long *)*plVar17 != (long *)0x0);
        }
        uStack_560 = puVar10;
        FUN_00aa4780(plVar26,plVar12);
        plVar14 = (long *)plVar26[7];
        if (plVar14 != (long *)0x0) {
          LOCK();
          plVar17 = plVar14 + 1;
          lVar15 = *plVar17;
          *(int *)plVar17 = (int)*plVar17 + -1;
          UNLOCK();
          if ((int)lVar15 == 1) {
            (**(code **)(*plVar14 + 0x10))(plVar14);
            LOCK();
            piVar1 = (int *)((long)plVar14 + 0xc);
            iVar16 = *piVar1;
            *piVar1 = *piVar1 + -1;
            UNLOCK();
            if (iVar16 == 1) {
              (**(code **)(*plVar14 + 0x18))(plVar14);
            }
          }
        }
        HeapInterface::Free(plVar26);
        plVar26 = plVar18;
        puVar10 = uStack_560;
      } while (plVar25 != plVar18);
    }
    puVar20 = puVar20 + 5;
    if (puVar20 == (undefined4 *)((long)puVar9 + (uVar22 & 0xfffffffffffffffc) + 0x18)) {
      uStack_560 = (undefined4 *)
                   CONCAT44(uStack_564,CONCAT22(uStack_566,CONCAT11(bStack_567,uStack_568)));
LAB_00138b0f:
      if ((uStack_560 != (undefined4 *)0x0) && (uStack_548 != uStack_560)) {
        uStack_560 = puVar10;
        HeapInterface::Free();
        puVar10 = uStack_560;
      }
      uStack_560 = puVar10;
      game::LocationContainer::Add
                (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&lStack_5b8);
      if (lStack_5b8 != 0) {
        ref_counter_base::DecRef();
      }
      if (cStack_5a8 == '\x02') {
        uStack_568 = 0x17;
        uStack_5c8 = 0xffffffff;
        bStack_567 = cStack_5a7 + 1U & 3;
        uStack_5cc = 0xffffffff;
        uStack_5d0 = 3;
        uStack_564 = (undefined4)uStack_5a4;
        uStack_560._0_4_ = (undefined4)((ulong)uStack_5a4 >> 0x20);
        uStack_560._4_4_ = (undefined4)uStack_59c;
        uStack_558._0_4_ = (undefined4)((ulong)uStack_59c >> 0x20);
        uStack_558._4_4_ = (undefined4)uStack_594;
        uStack_550 = (undefined4)((ulong)uStack_594 >> 0x20);
        uStack_54c = (undefined4)uStack_58c;
        uStack_548._0_4_ = (undefined4)((ulong)uStack_58c >> 0x20);
        uStack_548._4_4_ = (undefined4)uStack_584;
        uStack_540 = (undefined4)((ulong)uStack_584 >> 0x20);
        uStack_53c = uStack_57c;
        FUN_00137b90(&lStack_5b8,&uStack_5d0,&uStack_5cc,&uStack_5c8,&uStack_5c4,&uStack_568);
        game::LocationContainer::Add
                  (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&lStack_5b8);
        if (lStack_5b8 != 0) {
          ref_counter_base::DecRef();
        }
      }
      if (cStack_5a8 == '\b') {
        uStack_568 = 0x18;
        uStack_5c8 = 0xffffffff;
        uStack_5cc = 0xffffffff;
        bStack_567 = cStack_5a7 + 2U & 3;
        uStack_5d0 = 3;
        uStack_564 = (undefined4)uStack_5a4;
        uStack_560._0_4_ = (undefined4)((ulong)uStack_5a4 >> 0x20);
        uStack_560._4_4_ = (undefined4)uStack_59c;
        uStack_558._0_4_ = (undefined4)((ulong)uStack_59c >> 0x20);
        uStack_558._4_4_ = (undefined4)uStack_594;
        uStack_550 = (undefined4)((ulong)uStack_594 >> 0x20);
        uStack_54c = (undefined4)uStack_58c;
        uStack_548._0_4_ = (undefined4)((ulong)uStack_58c >> 0x20);
        uStack_548._4_4_ = (undefined4)uStack_584;
        uStack_540 = (undefined4)((ulong)uStack_584 >> 0x20);
        uStack_53c = uStack_57c;
        FUN_00137b90(&lStack_5b8,&uStack_5d0,&uStack_5cc,&uStack_5c8,&uStack_5c4,&uStack_568);
        game::LocationContainer::Add
                  (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&lStack_5b8);
        if (lStack_5b8 != 0) {
          ref_counter_base::DecRef();
        }
      }
      return &DAT_015d3620;
    }
  } while( true );
code_r0x00138788:
  uStack_560 = (undefined4 *)((long)pplVar19 + 0x14);
  *(int *)pplVar19 = (int)lVar6;
  *(undefined4 *)((long)pplVar19 + 4) = uVar4;
  *(int *)(pplVar19 + 1) = (int)lVar7;
  *(undefined4 *)((long)pplVar19 + 0xc) = uVar5;
  *(char *)(pplVar19 + 2) = (char)lVar8;
  plVar12 = (long *)eastl::rbtree::next(plVar12);
  puVar10 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_560);
  puVar9 = (undefined4 *)CONCAT44(uStack_560._4_4_,(undefined4)uStack_548);
  puVar20 = (undefined4 *)CONCAT44(uStack_548._4_4_,(undefined4)uStack_548);
  goto joined_r0x0013861a;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_ADD` @ 00139200
```c

/* Setting prototype: undefined * LOC_ADD(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::LOC_ADD(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  undefined8 uVar6;
  long lVar7;
  byte bVar8;
  int local_f0;
  undefined4 local_ec;
  undefined4 local_e8;
  undefined4 local_e4;
  int local_e0;
  int local_dc;
  long local_d8 [2];
  undefined4 local_c8;
  undefined4 uStack_c4;
  char local_b8;
  char local_b7;
  undefined8 local_b4;
  undefined8 uStack_ac;
  undefined8 local_a4;
  undefined8 uStack_9c;
  undefined8 local_94;
  undefined8 uStack_8c;
  undefined4 local_78;
  undefined8 local_74;
  undefined8 uStack_6c;
  undefined8 local_64;
  undefined8 uStack_5c;
  undefined8 local_54;
  undefined8 uStack_4c;
  
                    /* jag::packethandlers::ZoneUpdates::LOC_ADD -- adds a Location to a zone via
                       shared_ptr<MapSquareLocation>. Key call chain: 1. allocate MapSquareLocation
                       from FastPool (size 0xA8); returns shared_ptr {ctrl@+0, ptr@+8}. 2. if
                       scene-graph attachment ptr non-null, stage location into the GraphNode bucket
                       linked list. 3. LocationContainer::Add(*(Client+0x19490), shared_ptr) --
                       pushes shared_ptr into LC's primary/dynamic vector at LC+0x60..+0x68 (this is
                       the writer side of the engine's allSceneObjects walker). 4. For shapes 2 and
                       8 (multi-tile spans), additionally build secondary locations and add them
                       too. LocationContainer pointer lives at Client+0x19490. MapSquareLocation
                       pool stride = 0xa8 bytes (grew from 0x60 in the reference binary). */
  lVar7 = *(long *)(packet + 0x18);
  uStack_ac = 0x3f80000000000000;
  local_b8 = '\0';
  local_94 = 0x3f8000003f800000;
  local_b7 = '\0';
  local_b4 = 0;
  local_a4 = 0;
  uStack_9c = 0;
  *(long *)(packet + 0x18) = lVar7 + 1;
  lVar5 = *(long *)(packet + 0x10);
  uStack_8c = 0x3f800000;
  bVar1 = *(byte *)(lVar5 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 5;
  bVar8 = *(byte *)(lVar5 + 4 + lVar7);
  bVar2 = *(byte *)(lVar5 + 3 + lVar7);
  bVar3 = *(byte *)(lVar5 + 2 + lVar7);
  bVar4 = *(byte *)(lVar5 + 1 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 6;
  local_f0 = (uint)bVar3 * 0x100 + (uint)bVar8 * 0x1000000 + (uint)bVar2 * 0x10000 + (uint)bVar4;
  bVar8 = *(char *)(lVar5 + 5 + lVar7) + 0x80;
  FUN_00af9220(&local_b8,bVar8);
  local_dc = (bVar1 & 7) + DAT_013942b0;
  local_e8 = CONCAT31(local_e8._1_3_,bVar8 >> 7);
  local_e0 = (bVar1 >> 4 & 7) + DAT_013942ac;
  local_c8 = 0xffffffff;
  local_e4 = DAT_013942a8;
  local_78 = 2;
  FUN_00b02aa0(local_d8,&local_78,&local_f0,&local_c8,&local_e4,&local_b8);
  lVar7 = *thisPtr;
  lVar5 = *(long *)(*(long *)((long)&__DT_RELA[0xcf5].r_offset + lVar7) + 0x20);
  if (lVar5 != 0) {
    FUN_00436790(lVar5,local_d8,&local_e8,0x1392e6);
    lVar7 = *thisPtr;
  }
  game::LocationContainer::Add(*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + lVar7),local_d8);
  if (local_b8 == '\x02') {
    uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr);
    local_ec = 0xffffffff;
    local_e8 = 2;
    local_78 = CONCAT22(local_78._2_2_,CONCAT11(local_b7 + '\x01',0x17)) & 0xffff03ff;
    local_74 = local_b4;
    uStack_6c = uStack_ac;
    local_64 = local_a4;
    uStack_5c = uStack_9c;
    local_54 = local_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&local_c8,&local_e8,&local_f0,&local_ec,&local_e4,&local_78);
    game::LocationContainer::Add(uVar6,&local_c8);
    if (CONCAT44(uStack_c4,local_c8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (local_b8 == '\b') {
    uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr);
    local_ec = 0xffffffff;
    local_e8 = 2;
    local_78._2_2_ = (undefined2)(local_78 >> 0x10);
    local_78 = CONCAT22(local_78._2_2_,CONCAT11(local_b7 + '\x02',0x18)) & 0xffff03ff;
    local_74 = local_b4;
    uStack_6c = uStack_ac;
    local_64 = local_a4;
    uStack_5c = uStack_9c;
    local_54 = local_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&local_c8,&local_e8,&local_f0,&local_ec,&local_e4,&local_78);
    game::LocationContainer::Add(uVar6,&local_c8);
    if (CONCAT44(uStack_c4,local_c8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (local_d8[0] != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::URL_OPEN` @ 0013eb60
```c

double * jag::packethandlers::ClientState::URL_OPEN(double *param_1,double *param_2,double *param_3)

{
  double dVar1;
  double dVar2;
  double dVar3;
  double dVar4;
  double dVar5;
  double dVar6;
  double dVar7;
  double dVar8;
  double dVar9;
  double dVar10;
  double dVar11;
  
  dVar10 = param_3[1];
  dVar6 = param_3[2];
  dVar1 = param_2[1];
  dVar2 = *param_2;
  dVar3 = *param_3;
  dVar4 = param_2[2];
  dVar5 = param_3[3];
  dVar7 = dVar10 * dVar4 - dVar6 * dVar1;
  dVar9 = dVar1 * dVar3 - dVar10 * dVar2;
  dVar8 = dVar6 * dVar2 - dVar4 * dVar3;
  dVar11 = (dVar5 * dVar7 + dVar10 * dVar9) - dVar6 * dVar8;
  *param_1 = dVar11 + dVar11 + dVar2;
  dVar6 = (dVar5 * dVar8 + dVar6 * dVar7) - dVar3 * dVar9;
  dVar10 = (dVar5 * dVar9 + dVar3 * dVar8) - dVar10 * dVar7;
  param_1[1] = dVar6 + dVar6 + dVar1;
  param_1[2] = dVar10 + dVar10 + dVar4;
  return param_1;
}


```

## `jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT_thunk` @ 0013ec60
```c

double * jag::packethandlers::ClientState::TRIGGER_ONDIALOGABORT_thunk
                   (double param_1,double *param_2,double *param_3)

{
  double dVar1;
  double dVar2;
  double local_28;
  double local_20 [2];
  
  sincos(param_1 * DAT_00cb6bd0,local_20,&local_28);
  dVar1 = *param_3;
  dVar2 = param_3[1];
  param_2[2] = param_3[2] * local_20[0];
  param_2[3] = local_28;
  *param_2 = local_20[0] * dVar1;
  param_2[1] = local_20[0] * dVar2;
  return param_2;
}


```

## `jag::packethandlers::ClientState::SET_HEATMAP` @ 0013eea0
```c

undefined * jag::packethandlers::ClientState::SET_HEATMAP(long *param_1,undefined8 param_2)

{
  ulong uVar1;
  char cVar2;
  undefined *puVar3;
  
  puVar3 = &DAT_015df0b4;
  if ((param_1[0x20d] != 0) &&
     (puVar3 = (undefined *)
               FUN_00afcc00(param_1[0x20d],
                            *(undefined8 *)((long)&__DT_RELA[0xca7].r_info + param_1[0x20b]),
                            *(undefined8 *)((long)&__DT_RELA[0xd01].r_info + param_1[0x20b]),param_2
                           ), puVar3 == &DAT_015df0b8)) {
    uVar1 = *(ulong *)(param_1[0x20d] + 0x90);
    if (((ulong)param_1[0x256] < uVar1) && (cVar2 = FUN_0013edf0(param_2), cVar2 != '\0')) {
      (**(code **)(*param_1 + 0x1f8))(param_1);
      param_1[0x256] = uVar1;
      return puVar3;
    }
    puVar3 = &DAT_015df0b8;
  }
  return puVar3;
}


```

## `jag::packethandlers::Misc::SET_PLAYER_OP` @ 0013f040
```c

undefined * jag::packethandlers::Misc::SET_PLAYER_OP(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  long lVar5;
  long lVar6;
  int iVar7;
  int iVar8;
  int iVar9;
  char *pcVar10;
  undefined1 *puVar11;
  ushort uVar12;
  uint uVar13;
  undefined1 *puVar14;
  uint uVar15;
  char cVar16;
  undefined1 local_58;
  undefined7 uStack_57;
  long local_50;
  char local_41 [17];
  
                    /* SET_PLAYER_OP -- handler body for ServerProt op 17 (varByte). Wire format: 1.
                       u16 LE worldId (0xFFFF sentinel for no world); 2. CP1252 string text (e.g.
                       'Follow', 'Trade with'); 3. byte slot transformed via byteSubtract (server
                       writes 0x80-slot, client reads -byte); 4. byte priority/cursor-visible
                       boolean. Replaces the older op 0 SetPlayerOp which had a different wire
                       format (padding+text+slot+0x80+prio+prio2). rev948 codec must use this new
                       wire format. */
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  local_58 = 0;
  local_41[0] = '\x17';
  puVar14 = &local_58;
  *(long *)(param_2 + 0x18) = lVar5 + 2;
  bVar1 = *(byte *)(lVar6 + 1 + lVar5);
  bVar2 = *(byte *)(lVar6 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  bVar3 = *(byte *)(lVar6 + 2 + lVar5);
  uVar12 = (ushort)bVar1 * 0x100 + (ushort)bVar2;
  FUN_00ad80e0(param_2,puVar14);
  cVar16 = local_41[0];
  lVar5 = *(long *)(param_2 + 0x18);
  uVar15 = (uint)uVar12;
  uVar13 = -(uint)bVar3 & 0xff;
  if (uVar12 == 0xffff) {
    uVar15 = 0xffffffff;
  }
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  if (uVar13 - 1 < 8) {
    cVar4 = *(char *)(*(long *)(param_2 + 0x10) + lVar5);
    iVar7 = eastl_string_compare_cstr(puVar14,"null");
    if (iVar7 == 0) {
      if (cVar16 < '\0') {
        local_50 = 0;
        puVar11 = (undefined1 *)CONCAT71(uStack_57,local_58);
      }
      else {
        local_41[0] = '\x17';
        puVar11 = puVar14;
      }
      *puVar11 = 0;
      cVar16 = local_41[0];
    }
    iVar7 = uVar13 - 1;
    lVar5 = *(long *)((long)&__DT_RELA[0xcfb].r_info + *param_1);
    puVar11 = (undefined1 *)
              (*(long *)((long)&__DT_SYMTAB[0xe6].st_name + lVar5) + (long)iVar7 * 0x20);
    if (puVar11 != puVar14) {
      if (cVar16 < '\0') {
        puVar14 = (undefined1 *)CONCAT71(uStack_57,local_58);
        pcVar10 = puVar14 + local_50;
      }
      else {
        pcVar10 = local_41 + -(long)cVar16;
      }
      FUN_00495ef0(puVar11,puVar14,pcVar10);
      puVar11 = (undefined1 *)
                (*(long *)((long)&__DT_SYMTAB[0xe6].st_name + lVar5) + (long)iVar7 * 0x20);
      cVar16 = local_41[0];
    }
    iVar8 = *(int *)((long)&__DT_SYMTAB[0xe6].st_value + lVar5);
    *(uint *)(puVar11 + 0x18) = uVar15;
    puVar11[0x1c] = cVar4 == '\0';
    if (iVar7 == iVar8) {
      iVar9 = eastl_string_compare_cstr(puVar11,*(undefined8 *)(lVar5 + 0x50));
      if (iVar9 != 0) {
        *(undefined4 *)((long)&__DT_SYMTAB[0xe6].st_value + lVar5) = 0xffffffff;
        goto LAB_0013f0ce;
      }
    }
    if (iVar8 == -1) {
      iVar8 = eastl_string_compare_cstr(puVar11,*(undefined8 *)(lVar5 + 0x50));
      if (iVar8 == 0) {
        *(int *)((long)&__DT_SYMTAB[0xe6].st_value + lVar5) = iVar7;
      }
    }
  }
LAB_0013f0ce:
  if ((cVar16 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::MINIMAP_FLAG_SET` @ 0013f4f0
```c

undefined1
jag::packethandlers::ClientState::MINIMAP_FLAG_SET
          (long param_1,undefined8 param_2,uint param_3,undefined8 param_4)

{
  int *piVar1;
  long lVar2;
  undefined4 uVar3;
  undefined8 uVar4;
  long lVar5;
  bool bVar6;
  char cVar7;
  undefined1 uVar8;
  float *pfVar9;
  long local_48;
  undefined8 uStack_40;
  
  pfVar9 = graphics::GraphNode::GetWorldTranslation(*(void **)(param_1 + 8));
  FUN_006d0b50(param_1 + 0x168,pfVar9);
  bVar6 = *(uint *)(param_1 + 100) <= param_3;
  *(bool *)(param_1 + 0x1a9) = bVar6;
  if (*(char *)(param_1 + 400) == '\0') {
    return bVar6;
  }
  if (!bVar6) {
    return bVar6;
  }
  if ((*(long *)(param_1 + 0x130) != 0) && (*(long *)(*(long *)(param_1 + 0x130) + 0x18) != 0)) {
    lVar2 = param_1 + 0x1ac;
    uVar3 = *(undefined4 *)(Client::Client + 0x508);
    FUN_00ad79b0(lVar2);
    cVar7 = game::SceneManager::IsWorldReady(lVar2,uVar3);
    if (cVar7 != '\0') {
      game::WorldReference::Release(lVar2);
      game::AnimationWrapper::Update(*(undefined8 *)(param_1 + 0x130),param_3);
      FUN_00702050(*(undefined8 *)(param_1 + 0x130),0,*(undefined8 *)(param_1 + 0x150),0);
      lVar2 = *(long *)(param_1 + 0x148);
      uVar4 = *(undefined8 *)(param_1 + 0x150);
      if (lVar2 != 0) {
        LOCK();
        *(int *)(lVar2 + 8) = *(int *)(lVar2 + 8) + 1;
        UNLOCK();
      }
      lVar5 = *(long *)(param_1 + 0x158);
      *(undefined8 *)(param_1 + 0x160) = uVar4;
      *(long *)(param_1 + 0x158) = lVar2;
      if (lVar5 != 0) {
        ref_counter_base::DecRef();
      }
      lVar2 = *(long *)(param_1 + 0xd0);
      if ((lVar2 != 0) && (3 < *(byte *)(lVar2 + 0x170))) {
        local_48 = *(long *)(param_1 + 0x158);
        uStack_40 = *(undefined8 *)(param_1 + 0x160);
        if (*(long *)(param_1 + 0x158) != 0) {
          LOCK();
          piVar1 = (int *)(*(long *)(param_1 + 0x158) + 8);
          *piVar1 = *piVar1 + 1;
          UNLOCK();
        }
        FUN_00bfcba0(lVar2,&local_48);
        if (local_48 != 0) {
          ref_counter_base::DecRef();
        }
      }
      lVar2 = *(long *)(*(long *)(param_1 + 0x130) + 0x18);
      if ((*(char *)(lVar2 + 0x84) != '\0') || (*(char *)(lVar2 + 0x85) != '\0')) {
        FUN_0096ffb0(lVar2,param_3,0);
      }
      goto LAB_0013f5ba;
    }
  }
  lVar2 = *(long *)(param_1 + 0x158);
  *(undefined8 *)(param_1 + 0x160) = 0;
  *(undefined8 *)(param_1 + 0x158) = 0;
  if (lVar2 != 0) {
    ref_counter_base::DecRef();
  }
LAB_0013f5ba:
  uVar8 = FUN_0013f490(DAT_015d4db8,param_1 + 200);
  if (*(char *)(param_1 + 0x1a8) != '\0') {
    uVar8 = FUN_00afb510(*(undefined8 *)(param_1 + 0xd0),param_2,param_1 + 0xd8,param_4,
                         *(undefined8 *)(param_1 + 0x160));
    lVar2 = *(long *)(param_1 + 0xd0);
    if ((*(char *)(lVar2 + 0x3d9) != '\0') && (*(long *)(lVar2 + 0x2e8) != *(long *)(lVar2 + 0x2f0))
       ) {
      uVar8 = 0;
      FUN_00bdfbb0(lVar2,param_2,param_1 + 0x110,param_4,*(undefined8 *)(param_1 + 0x160),0);
    }
  }
  return uVar8;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE` @ 0013f770
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: undefined * LOC_CUSTOMISE(long * thisPtr, long packet) */

undefined * jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE(long *thisPtr,long packet)

{
  ulong *puVar1;
  long lVar2;
  long *plVar3;
  byte bVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  undefined2 *puVar8;
  byte bVar9;
  undefined2 uVar10;
  int iVar11;
  long *plVar12;
  long *plVar13;
  long *plVar14;
  undefined8 *puVar15;
  undefined8 *puVar16;
  ulong uVar17;
  undefined8 *puVar18;
  undefined8 *puVar19;
  long lVar20;
  uint uVar21;
  PacketCore *pPVar22;
  ulong uVar23;
  ulong uVar24;
  ulong uVar25;
  ulong uVar26;
  long lVar27;
  undefined8 *puVar28;
  long *plVar29;
  undefined8 *puVar30;
  long local_158;
  undefined4 local_134;
  long local_130;
  undefined4 local_124;
  int local_120;
  int local_11c;
  long local_118;
  long local_110;
  undefined *local_108;
  char local_100;
  undefined4 local_f8 [2];
  long local_f0;
  undefined2 *local_e8;
  undefined4 local_d8;
  undefined4 uStack_d4;
  long local_d0;
  undefined2 *local_c8;
  char local_b8;
  char local_b7;
  undefined8 local_b4;
  undefined8 uStack_ac;
  undefined8 local_a4;
  undefined8 uStack_9c;
  undefined8 local_94;
  undefined8 uStack_8c;
  undefined4 local_78;
  undefined4 local_74;
  undefined4 uStack_70;
  undefined4 uStack_6c;
  undefined4 uStack_68;
  undefined4 uStack_64;
  undefined4 uStack_60;
  undefined8 uStack_5c;
  undefined8 local_54;
  undefined8 uStack_4c;
  
                    /* jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE -- Reads variable-length loc
                       customization packet starting with a 4-byte alt-int locId
                       (jag::Packet::g4_alt3), then zone-packed byte, signed shape/rotation byte,
                       flag byte. Based on the flag bits, conditionally reads recolor arrays (g4
                       ints), retexture arrays (gSmart1or2 shorts), and animation override arrays.
                       Calls the Location ctor and LocationContainer::Add, with the same respawn
                       variants for shape 0x17/0x18 as LOC_ADD. */
  lVar20 = *(long *)(packet + 0x18);
  uStack_ac = 0x3f80000000000000;
  local_b8 = '\0';
  local_b7 = '\0';
  local_b4 = 0;
  local_a4 = 0;
  uStack_9c = 0;
  local_94 = 0x3f8000003f800000;
  uStack_8c = 0x3f800000;
  *(long *)(packet + 0x18) = lVar20 + 1;
  bVar9 = -*(char *)(*(long *)(packet + 0x10) + lVar20);
  local_134 = Packet::g4_alt3(packet);
  lVar20 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar20 + 1;
  uVar23 = CONCAT71(0xffffff,-0x80 - *(char *)(*(long *)(packet + 0x10) + lVar20));
  *(long *)(packet + 0x18) = lVar20 + 2;
  bVar4 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar20);
  FUN_00af9220(&local_b8,bVar4,packet);
  local_11c = ((uint)uVar23 & 7) + DAT_013942b0;
  local_120 = ((uint)(uVar23 >> 4) & 7) + DAT_013942ac;
  local_78 = 0xffffffff;
  local_d8 = 4;
  local_f8[0] = CONCAT31(local_f8[0]._1_3_,bVar4 >> 7);
  local_124 = DAT_013942a8;
  FUN_00b02aa0(&local_118);
  if ((bVar9 & 1) != 0) goto LAB_0013fcc0;
  uStack_70 = 0;
  uStack_6c = 0;
  uStack_68 = 0;
  uStack_64 = 0;
  local_d0 = 0;
  local_c8 = (undefined2 *)0x0;
  local_f0 = 0;
  local_e8 = (undefined2 *)0x0;
  if ((bVar9 & 2) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    FUN_00c2cba0(&local_78);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 4 + 4;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                    lVar27 = 4;
                    *(uint *)CONCAT44(uStack_64,uStack_68) = uVar21;
                  }
                  uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                  *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
                  lVar27 = lVar27 + 4;
                }
                uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
                lVar27 = lVar27 + 4;
              }
              uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
              *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
              lVar27 = lVar27 + 4;
            }
            uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
            *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
            lVar27 = lVar27 + 4;
          }
          uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
          *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
          lVar27 = lVar27 + 4;
        }
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
        lVar27 = lVar27 + 4;
        if (lVar27 == lVar20) goto LAB_0013f945;
      }
      do {
        pPVar22 = (PacketCore *)packet;
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 4 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 8 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0xc + lVar27) = uVar21;
        pPVar22 = (PacketCore *)packet;
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x10 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x14 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x18 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x1c + lVar27) = uVar21;
        lVar27 = lVar27 + 0x20;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f945:
  if ((bVar9 & 4) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    game::ClientScript::ReadOpcodeArray(&local_d8);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 2 + 2;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar10 = FUN_00121a30(packet);
                    lVar27 = 2;
                    *local_c8 = uVar10;
                  }
                  uVar10 = FUN_00121a30(packet);
                  *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
                  lVar27 = lVar27 + 2;
                }
                uVar10 = FUN_00121a30(packet);
                *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
                lVar27 = lVar27 + 2;
              }
              uVar10 = FUN_00121a30(packet);
              *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
              lVar27 = lVar27 + 2;
            }
            uVar10 = FUN_00121a30(packet);
            *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
            lVar27 = lVar27 + 2;
          }
          uVar10 = FUN_00121a30(packet);
          *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
          lVar27 = lVar27 + 2;
        }
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
        lVar27 = lVar27 + 2;
        if (lVar27 == lVar20) goto LAB_0013f94f;
      }
      do {
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_c8 + lVar27) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 2) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 4) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 6) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 8) = uVar10;
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_c8 + lVar27 + 10) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 0xc) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_c8 + lVar27 + 0xe) = uVar10;
        lVar27 = lVar27 + 0x10;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f94f:
  if ((bVar9 & 8) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    game::ClientScript::ReadOpcodeArray(local_f8);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 2 + 2;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    lVar27 = 2;
                    uVar10 = FUN_00121a30(packet);
                    *local_e8 = uVar10;
                  }
                  uVar10 = FUN_00121a30(packet);
                  *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
                  lVar27 = lVar27 + 2;
                }
                uVar10 = FUN_00121a30(packet);
                *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
                lVar27 = lVar27 + 2;
              }
              uVar10 = FUN_00121a30(packet);
              *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
              lVar27 = lVar27 + 2;
            }
            uVar10 = FUN_00121a30(packet);
            *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
            lVar27 = lVar27 + 2;
          }
          uVar10 = FUN_00121a30(packet);
          *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
          lVar27 = lVar27 + 2;
        }
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
        lVar27 = lVar27 + 2;
        if (lVar27 == lVar20) goto LAB_0013f959;
      }
      do {
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_e8 + lVar27) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 2) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 4) = uVar10;
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)local_e8 + lVar27 + 6) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 8) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 10) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 0xc) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)local_e8 + lVar27 + 0xe) = uVar10;
        lVar27 = lVar27 + 0x10;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f959:
  if (((CONCAT44(uStack_6c,uStack_70) != 0) || (local_d0 != 0)) || (local_f0 != 0)) {
    local_100 = 0;
    local_108 = &DAT_01394220;
    FUN_00c2a9a0();
    local_100 = '\x01';
    if ((DAT_01394210 == '\0') && (iVar11 = __cxa_guard_acquire(&DAT_01394210), iVar11 != 0)) {
      DAT_01394200 = (long *)0x0;
      DAT_01394208 = 0x10;
      _DAT_013941f0 = (undefined1  [16])0x0;
      puVar15 = (undefined8 *)thunk_FUN_00c29480(0x28);
      puVar30 = DAT_013941f0;
      puVar28 = puVar15;
      if (DAT_013941f8 != DAT_013941f0) {
        puVar28 = DAT_013941f0 + 5;
        uVar23 = ((ulong)((long)DAT_013941f8 - (long)puVar28) >> 3) * 0xccccccccccccccd &
                 0x1fffffffffffffff;
        uVar21 = (int)((ulong)((long)(puVar28 + uVar23 * 5) + (-0x28 - (long)DAT_013941f0)) >> 3) *
                 -0x33333333 + 1U & 3;
        puVar19 = puVar15;
        if (uVar21 == 0) goto LAB_00140810;
        if (uVar21 != 1) {
          puVar16 = DAT_013941f0;
          puVar18 = puVar15;
          if (uVar21 != 2) {
            uVar5 = DAT_013941f0[3];
            uVar6 = *DAT_013941f0;
            puVar15[1] = DAT_013941f0[1];
            uVar7 = puVar30[2];
            puVar15[3] = uVar5;
            uVar5 = puVar30[4];
            *puVar15 = uVar6;
            puVar18 = puVar15 + 5;
            puVar15[2] = uVar7;
            puVar15[4] = uVar5;
            *puVar30 = 0;
            puVar30[1] = 0;
            puVar30[2] = 0;
            puVar30[3] = 0;
            puVar30[4] = 0;
            puVar16 = puVar28;
          }
          uVar5 = puVar16[3];
          puVar19 = puVar18 + 5;
          uVar6 = *puVar16;
          puVar30 = puVar16 + 5;
          puVar18[1] = puVar16[1];
          uVar7 = puVar16[2];
          puVar18[3] = uVar5;
          uVar5 = puVar16[4];
          *puVar18 = uVar6;
          puVar18[2] = uVar7;
          puVar18[4] = uVar5;
          *puVar16 = 0;
          puVar16[1] = 0;
          puVar16[2] = 0;
          puVar16[3] = 0;
          puVar16[4] = 0;
        }
        uVar5 = puVar30[3];
        uVar6 = *puVar30;
        puVar19[1] = puVar30[1];
        uVar7 = puVar30[2];
        puVar19[3] = uVar5;
        uVar5 = puVar30[4];
        *puVar19 = uVar6;
        puVar19[2] = uVar7;
        puVar19[4] = uVar5;
        *puVar30 = 0;
        puVar30[1] = 0;
        puVar30[2] = 0;
        puVar30[3] = 0;
        puVar30[4] = 0;
        puVar19 = puVar19 + 5;
        for (puVar30 = puVar30 + 5; puVar30 != puVar28 + uVar23 * 5; puVar30 = puVar30 + 0x14) {
LAB_00140810:
          uVar5 = puVar30[3];
          uVar6 = *puVar30;
          puVar19[1] = puVar30[1];
          uVar7 = puVar30[2];
          puVar19[3] = uVar5;
          uVar5 = puVar30[4];
          *puVar19 = uVar6;
          uVar6 = puVar30[5];
          puVar19[2] = uVar7;
          uVar7 = puVar30[6];
          puVar19[4] = uVar5;
          uVar5 = puVar30[8];
          *puVar30 = 0;
          puVar30[1] = 0;
          puVar30[2] = 0;
          puVar30[3] = 0;
          puVar30[4] = 0;
          puVar19[5] = uVar6;
          puVar19[6] = uVar7;
          uVar6 = puVar30[10];
          uVar7 = puVar30[7];
          puVar19[8] = uVar5;
          uVar5 = puVar30[9];
          puVar19[7] = uVar7;
          puVar19[9] = uVar5;
          puVar30[5] = 0;
          puVar30[6] = 0;
          puVar30[7] = 0;
          puVar30[8] = 0;
          puVar30[9] = 0;
          puVar19[10] = uVar6;
          uVar5 = puVar30[0xd];
          uVar6 = puVar30[0xf];
          puVar19[0xb] = puVar30[0xb];
          uVar7 = puVar30[0xc];
          puVar19[0xd] = uVar5;
          uVar5 = puVar30[0xe];
          puVar19[0xc] = uVar7;
          uVar7 = puVar30[0x10];
          puVar19[0xe] = uVar5;
          uVar5 = puVar30[0x12];
          puVar30[10] = 0;
          puVar30[0xb] = 0;
          puVar30[0xc] = 0;
          puVar30[0xd] = 0;
          puVar30[0xe] = 0;
          puVar19[0x10] = uVar7;
          uVar7 = puVar30[0x11];
          puVar19[0x12] = uVar5;
          uVar5 = puVar30[0x13];
          puVar19[0xf] = uVar6;
          puVar19[0x11] = uVar7;
          puVar19[0x13] = uVar5;
          puVar30[0xf] = 0;
          puVar30[0x10] = 0;
          puVar30[0x11] = 0;
          puVar30[0x12] = 0;
          puVar30[0x13] = 0;
          puVar19 = puVar19 + 0x14;
        }
        puVar28 = puVar15 + uVar23 * 5 + 5;
      }
      *puVar28 = 0;
      puVar28[1] = 0;
      puVar28[2] = 0;
      puVar28[3] = 0;
      puVar28[4] = 0;
      FUN_00b06190(puVar28,0x20,0x10);
      if (DAT_013941f8 != DAT_013941f0) {
        plVar3 = (long *)((long)DAT_013941f0 +
                         ((long)DAT_013941f8 - (long)(DAT_013941f0 + 5) & 0xfffffffffffffff8U) +
                         0x28);
        plVar14 = DAT_013941f0;
        plVar29 = DAT_013941f0 + 5;
        do {
          uVar23 = 0;
          if (plVar14[3] != 0) {
            do {
              while( true ) {
                lVar20 = *(long *)(plVar14[1] + uVar23 * 8) * 0x70 + *plVar14;
                if ((*(long *)(lVar20 + 0x60) != 0) && (*(long *)(lVar20 + 0x68) != 0)) {
                  HeapInterface::Free();
                }
                if ((*(long *)(lVar20 + 0x48) != 0) && (*(long *)(lVar20 + 0x50) != 0)) {
                  HeapInterface::Free();
                }
                if ((*(long *)(lVar20 + 0x30) != 0) && (*(long *)(lVar20 + 0x38) != 0)) break;
                uVar23 = uVar23 + 1;
                if ((ulong)plVar14[3] <= uVar23) goto LAB_00141248;
              }
              HeapInterface::Free();
              uVar23 = uVar23 + 1;
            } while (uVar23 < (ulong)plVar14[3]);
          }
LAB_00141248:
          if (plVar14[4] != 0) {
            lVar20 = plVar14[1];
            uVar23 = 0;
            do {
              *(ulong *)(lVar20 + uVar23 * 8) = uVar23;
              uVar23 = uVar23 + 1;
            } while (uVar23 < (ulong)plVar14[4]);
          }
          plVar14[3] = 0;
          if (plVar14[2] != 0) {
            HeapInterface::Free();
          }
          plVar14[2] = 0;
          if (plVar14[1] != 0) {
            HeapInterface::Free();
            plVar14[1] = 0;
          }
          HeapInterface::Free(*plVar14);
          if (plVar29 == plVar3) goto LAB_00141310;
          plVar14 = plVar29;
          plVar29 = plVar29 + 5;
        } while( true );
      }
      goto LAB_00141326;
    }
    goto LAB_0013f9b3;
  }
  goto LAB_0013fc9d;
LAB_00141310:
LAB_00141326:
  if (DAT_013941f0 != (long *)0x0) {
    HeapInterface::Free(DAT_013941f0);
  }
  DAT_01394200 = puVar15 + 5;
  DAT_013941f8 = puVar28 + 5;
  DAT_013941f0 = puVar15;
  __cxa_guard_release(&DAT_01394210);
  __cxa_atexit(FUN_000f09d0,&DAT_013941f0,&PTR_LOOP_01391000);
LAB_0013f9b3:
  uVar5 = DAT_01394208;
  plVar3 = DAT_013941f8;
  if (DAT_013941f0 == DAT_013941f8) {
    local_130 = 0;
LAB_001405d3:
    if (DAT_013941f8 < DAT_01394200) {
      DAT_013941f8[4] = 0;
      *plVar3 = 0;
      plVar3[1] = 0;
      plVar3[2] = 0;
      plVar3[3] = 0;
      FUN_00b06190(plVar3,local_130,uVar5);
      lVar20 = (long)DAT_013941f8 + 0x28;
      DAT_013941f8 = (long *)lVar20;
    }
    else {
      FUN_000f9f00(&DAT_013941f0,&local_130);
      lVar20 = (long)DAT_013941f8;
    }
    uVar23 = *(ulong *)(lVar20 + -0x10);
    puVar30 = (undefined8 *)0x0;
    if (uVar23 < *(ulong *)(lVar20 + -8)) {
      lVar27 = *(long *)(*(long *)(lVar20 + -0x20) + uVar23 * 8);
      puVar30 = (undefined8 *)(lVar27 * 0x70 + *(long *)(lVar20 + -0x28));
      *(ulong *)(*(long *)(lVar20 + -0x18) + lVar27 * 8) = uVar23;
      *(long *)(lVar20 + -0x10) = *(long *)(lVar20 + -0x10) + 1;
    }
  }
  else {
    uVar23 = DAT_013941f0[3];
    plVar14 = DAT_013941f0;
    if ((ulong)DAT_013941f0[4] <= uVar23) {
      plVar29 = DAT_013941f0 + 5;
      uVar23 = ((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) * 5;
      uVar21 = (uint)uVar23 & 7;
      plVar13 = plVar29;
      if ((uVar23 & 7) != 0) {
        plVar13 = DAT_013941f0 + 10;
        uVar23 = DAT_013941f0[8];
        plVar14 = plVar29;
        if (uVar23 < (ulong)DAT_013941f0[9]) goto LAB_0013fb60;
        if (uVar21 != 1) {
          plVar12 = plVar13;
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar23 = DAT_013941f0[0xd];
                    plVar12 = DAT_013941f0 + 0xf;
                    plVar14 = plVar13;
                    if (uVar23 < (ulong)DAT_013941f0[0xe]) goto LAB_0013fb60;
                  }
                  uVar23 = plVar12[3];
                  plVar13 = plVar12 + 5;
                  plVar14 = plVar12;
                  if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
                }
                uVar23 = plVar13[3];
                plVar12 = plVar13 + 5;
                plVar14 = plVar13;
                if (uVar23 < (ulong)plVar13[4]) goto LAB_0013fb60;
              }
              uVar23 = plVar12[3];
              plVar13 = plVar12 + 5;
              plVar14 = plVar12;
              if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
            }
            uVar23 = plVar13[3];
            plVar12 = plVar13 + 5;
            plVar14 = plVar13;
            if (uVar23 < (ulong)plVar13[4]) goto LAB_0013fb60;
          }
          uVar23 = plVar12[3];
          plVar13 = plVar12 + 5;
          plVar14 = plVar12;
          if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
        }
      }
      do {
        if (DAT_013941f8 == plVar13) {
          plVar14 = DAT_013941f0 + 4;
          local_130 = 0;
          uVar21 = (int)((ulong)((long)(DAT_013941f0 +
                                       (((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) *
                                        0xccccccccccccccd & 0x1fffffffffffffff) * 5 + 9) +
                                (-0x28 - (long)plVar14)) >> 3) * -0x33333333 + 1U & 7;
          if (uVar21 == 0) goto LAB_0014059d;
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      local_130 = *plVar14;
                      plVar14 = DAT_013941f0 + 9;
                    }
                    local_130 = local_130 + *plVar14;
                    plVar14 = plVar14 + 5;
                  }
                  local_130 = local_130 + *plVar14;
                  plVar14 = plVar14 + 5;
                }
                local_130 = local_130 + *plVar14;
                plVar14 = plVar14 + 5;
              }
              local_130 = local_130 + *plVar14;
              plVar14 = plVar14 + 5;
            }
            local_130 = local_130 + *plVar14;
            plVar14 = plVar14 + 5;
          }
          local_130 = local_130 + *plVar14;
          for (plVar14 = plVar14 + 5;
              plVar14 !=
              DAT_013941f0 +
              (((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) * 0xccccccccccccccd &
              0x1fffffffffffffff) * 5 + 9; plVar14 = plVar14 + 0x28) {
LAB_0014059d:
            local_130 = local_130 + *plVar14 + plVar14[5] + plVar14[10] + plVar14[0xf] +
                        plVar14[0x14] + plVar14[0x19] + plVar14[0x1e] + plVar14[0x23];
          }
          goto LAB_001405d3;
        }
        uVar23 = plVar13[3];
        plVar14 = plVar13;
        if (uVar23 < (ulong)plVar13[4]) break;
        uVar23 = plVar13[8];
        plVar14 = plVar13 + 5;
        if (uVar23 < (ulong)plVar13[9]) break;
        uVar23 = plVar13[0xd];
        plVar14 = plVar13 + 10;
        if (uVar23 < (ulong)plVar13[0xe]) break;
        uVar23 = plVar13[0x12];
        plVar14 = plVar13 + 0xf;
        if (uVar23 < (ulong)plVar13[0x13]) break;
        uVar23 = plVar13[0x17];
        plVar14 = plVar13 + 0x14;
        if (uVar23 < (ulong)plVar13[0x18]) break;
        uVar23 = plVar13[0x1c];
        plVar14 = plVar13 + 0x19;
        if (uVar23 < (ulong)plVar13[0x1d]) break;
        uVar23 = plVar13[0x21];
        plVar12 = plVar13 + 0x23;
        plVar14 = plVar13 + 0x1e;
        if (uVar23 < (ulong)plVar13[0x22]) break;
        uVar23 = plVar13[0x26];
        puVar1 = (ulong *)(plVar13 + 0x27);
        plVar13 = plVar13 + 0x28;
        plVar14 = plVar12;
      } while (*puVar1 <= uVar23);
    }
LAB_0013fb60:
    lVar20 = *(long *)(plVar14[1] + uVar23 * 8);
    puVar30 = (undefined8 *)(lVar20 * 0x70 + *plVar14);
    *(ulong *)(plVar14[2] + lVar20 * 8) = uVar23;
    plVar14[3] = plVar14[3] + 1;
  }
  FUN_0047e230(&local_108);
  puVar30[6] = 0;
  puVar30[7] = 0;
  *(int *)(puVar30 + 4) = DAT_01391008;
  DAT_01391008 = DAT_01391008 + 1;
  FUN_00139520(puVar30 + 5,&local_78);
  puVar30[9] = 0;
  puVar30[10] = 0;
  if (local_d0 != 0) {
    game::ClientScript::ReadOpcodeArray(puVar30 + 8);
    uVar23 = puVar30[9];
    if (uVar23 != 0) {
      puVar8 = (undefined2 *)puVar30[10];
      if ((local_c8 < puVar8 + 8 && puVar8 < local_c8 + 8) || (uVar23 < 0xf)) {
        uVar17 = 0;
        uVar21 = (uint)uVar23 & 7;
        if ((uVar23 & 7) != 0) {
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      *puVar8 = *local_c8;
                    }
                    uVar17 = (ulong)(uVar21 != 6);
                    puVar8[uVar17] = local_c8[uVar17];
                    uVar17 = uVar17 + 1;
                  }
                  puVar8[uVar17] = local_c8[uVar17];
                  uVar17 = uVar17 + 1;
                }
                puVar8[uVar17] = local_c8[uVar17];
                uVar17 = uVar17 + 1;
              }
              puVar8[uVar17] = local_c8[uVar17];
              uVar17 = uVar17 + 1;
            }
            puVar8[uVar17] = local_c8[uVar17];
            uVar17 = uVar17 + 1;
          }
          puVar8[uVar17] = local_c8[uVar17];
          uVar17 = uVar17 + 1;
          if (uVar23 == uVar17) goto LAB_0013fbe5;
        }
        do {
          puVar8[uVar17] = local_c8[uVar17];
          puVar8[uVar17 + 1] = local_c8[uVar17 + 1];
          puVar8[uVar17 + 2] = local_c8[uVar17 + 2];
          lVar20 = uVar17 + 5;
          puVar8[uVar17 + 3] = local_c8[uVar17 + 3];
          puVar8[uVar17 + 4] = local_c8[uVar17 + 4];
          lVar27 = uVar17 + 6;
          lVar2 = uVar17 + 7;
          uVar17 = uVar17 + 8;
          puVar8[lVar20] = local_c8[lVar20];
          puVar8[lVar27] = local_c8[lVar27];
          puVar8[lVar2] = local_c8[lVar2];
        } while (uVar23 != uVar17);
      }
      else {
        uVar21 = -(int)((ulong)local_c8 >> 1) & 7;
        uVar17 = (ulong)uVar21;
        if (uVar21 == 0) {
          local_158 = 0;
        }
        else {
          *puVar8 = *local_c8;
          if (uVar17 == 1) {
            local_158 = 1;
          }
          else {
            puVar8[1] = local_c8[1];
            if (uVar17 == 2) {
              local_158 = 2;
            }
            else {
              puVar8[2] = local_c8[2];
              if (uVar17 == 3) {
                local_158 = 3;
              }
              else {
                puVar8[3] = local_c8[3];
                if (uVar17 == 4) {
                  local_158 = 4;
                }
                else {
                  puVar8[4] = local_c8[4];
                  if (uVar17 == 5) {
                    local_158 = 5;
                  }
                  else {
                    puVar8[5] = local_c8[5];
                    if (uVar17 == 6) {
                      local_158 = 6;
                    }
                    else {
                      local_158 = 7;
                      puVar8[6] = local_c8[6];
                    }
                  }
                }
              }
            }
          }
        }
        uVar24 = 1;
        lVar20 = 0x10;
        uVar25 = uVar23 - uVar17;
        puVar28 = (undefined8 *)(local_c8 + uVar17);
        uVar26 = uVar25 >> 3;
        puVar15 = (undefined8 *)(puVar8 + uVar17);
        uVar5 = puVar28[1];
        uVar21 = (int)uVar26 - 1U & 7;
        *puVar15 = *puVar28;
        puVar15[1] = uVar5;
        if (1 < uVar26) {
          if (uVar21 != 0) {
            if (uVar21 != 1) {
              if (uVar21 != 2) {
                if (uVar21 != 3) {
                  if (uVar21 != 4) {
                    if (uVar21 != 5) {
                      if (uVar21 != 6) {
                        uVar5 = puVar28[3];
                        uVar24 = 2;
                        lVar20 = 0x20;
                        puVar15[2] = puVar28[2];
                        puVar15[3] = uVar5;
                      }
                      uVar24 = uVar24 + 1;
                      uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                      *(undefined8 *)((long)puVar15 + lVar20) =
                           *(undefined8 *)((long)puVar28 + lVar20);
                      ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                      lVar20 = lVar20 + 0x10;
                    }
                    uVar24 = uVar24 + 1;
                    uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                    *(undefined8 *)((long)puVar15 + lVar20) =
                         *(undefined8 *)((long)puVar28 + lVar20);
                    ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                    lVar20 = lVar20 + 0x10;
                  }
                  uVar24 = uVar24 + 1;
                  uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                  *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                  ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                  lVar20 = lVar20 + 0x10;
                }
                uVar24 = uVar24 + 1;
                uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                lVar20 = lVar20 + 0x10;
              }
              uVar24 = uVar24 + 1;
              uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
              *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
              ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
              lVar20 = lVar20 + 0x10;
            }
            uVar24 = uVar24 + 1;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            lVar20 = lVar20 + 0x10;
            if (uVar26 <= uVar24) goto LAB_00140beb;
          }
          do {
            uVar24 = uVar24 + 8;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x10);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x10);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x20);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x20);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x30);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x30);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x40);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x40);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x50);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x50);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x60);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x60);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x70);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x70);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            lVar20 = lVar20 + 0x80;
          } while (uVar24 < uVar26);
        }
LAB_00140beb:
        local_158 = local_158 + (uVar25 & 0xfffffffffffffff8);
        if ((uVar25 & 0xfffffffffffffff8) != uVar25) {
          uVar17 = local_158 + 1;
          puVar8[local_158] = local_c8[local_158];
          if (uVar17 < uVar23) {
            uVar24 = local_158 + 2;
            puVar8[uVar17] = local_c8[uVar17];
            if (uVar24 < uVar23) {
              uVar17 = local_158 + 3;
              puVar8[uVar24] = local_c8[uVar24];
              if (uVar17 < uVar23) {
                uVar24 = local_158 + 4;
                puVar8[uVar17] = local_c8[uVar17];
                if (uVar24 < uVar23) {
                  uVar17 = local_158 + 5;
                  puVar8[uVar24] = local_c8[uVar24];
                  if (uVar17 < uVar23) {
                    uVar24 = local_158 + 6;
                    puVar8[uVar17] = local_c8[uVar17];
                    if (uVar24 < uVar23) {
                      uVar17 = local_158 + 7;
                      puVar8[uVar24] = local_c8[uVar24];
                      if (uVar17 < uVar23) {
                        uVar24 = local_158 + 8;
                        puVar8[uVar17] = local_c8[uVar17];
                        if (uVar24 < uVar23) {
                          uVar17 = local_158 + 9;
                          puVar8[uVar24] = local_c8[uVar24];
                          if (uVar17 < uVar23) {
                            uVar24 = local_158 + 10;
                            puVar8[uVar17] = local_c8[uVar17];
                            if (uVar24 < uVar23) {
                              uVar17 = local_158 + 0xb;
                              puVar8[uVar24] = local_c8[uVar24];
                              if (uVar17 < uVar23) {
                                uVar24 = local_158 + 0xc;
                                puVar8[uVar17] = local_c8[uVar17];
                                if (uVar24 < uVar23) {
                                  uVar17 = local_158 + 0xd;
                                  puVar8[uVar24] = local_c8[uVar24];
                                  if (uVar17 < uVar23) {
                                    puVar8[uVar17] = local_c8[uVar17];
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
LAB_0013fbe5:
  puVar30[0xc] = 0;
  puVar30[0xd] = 0;
  if (local_f0 != 0) {
    game::ClientScript::ReadOpcodeArray(puVar30 + 0xb);
    uVar23 = puVar30[0xc];
    if (uVar23 != 0) {
      puVar8 = (undefined2 *)puVar30[0xd];
      if ((local_e8 < puVar8 + 8 && puVar8 < local_e8 + 8) || (uVar23 < 0xf)) {
        uVar17 = 0;
        uVar21 = (uint)uVar23 & 7;
        if ((uVar23 & 7) != 0) {
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      *puVar8 = *local_e8;
                    }
                    uVar17 = (ulong)(uVar21 != 6);
                    puVar8[uVar17] = local_e8[uVar17];
                    uVar17 = uVar17 + 1;
                  }
                  puVar8[uVar17] = local_e8[uVar17];
                  uVar17 = uVar17 + 1;
                }
                puVar8[uVar17] = local_e8[uVar17];
                uVar17 = uVar17 + 1;
              }
              puVar8[uVar17] = local_e8[uVar17];
              uVar17 = uVar17 + 1;
            }
            puVar8[uVar17] = local_e8[uVar17];
            uVar17 = uVar17 + 1;
          }
          puVar8[uVar17] = local_e8[uVar17];
          uVar17 = uVar17 + 1;
          if (uVar23 == uVar17) goto LAB_0013fc06;
        }
        do {
          puVar8[uVar17] = local_e8[uVar17];
          puVar8[uVar17 + 1] = local_e8[uVar17 + 1];
          puVar8[uVar17 + 2] = local_e8[uVar17 + 2];
          puVar8[uVar17 + 3] = local_e8[uVar17 + 3];
          lVar20 = uVar17 + 6;
          puVar8[uVar17 + 4] = local_e8[uVar17 + 4];
          puVar8[uVar17 + 5] = local_e8[uVar17 + 5];
          lVar27 = uVar17 + 7;
          uVar17 = uVar17 + 8;
          puVar8[lVar20] = local_e8[lVar20];
          puVar8[lVar27] = local_e8[lVar27];
        } while (uVar23 != uVar17);
      }
      else {
        uVar21 = -(int)((ulong)local_e8 >> 1) & 7;
        uVar17 = (ulong)uVar21;
        if (uVar21 == 0) {
          local_158 = 0;
        }
        else {
          *puVar8 = *local_e8;
          if (uVar17 == 1) {
            local_158 = 1;
          }
          else {
            puVar8[1] = local_e8[1];
            if (uVar17 == 2) {
              local_158 = 2;
            }
            else {
              puVar8[2] = local_e8[2];
              if (uVar17 == 3) {
                local_158 = 3;
              }
              else {
                puVar8[3] = local_e8[3];
                if (uVar17 == 4) {
                  local_158 = 4;
                }
                else {
                  puVar8[4] = local_e8[4];
                  if (uVar17 == 5) {
                    local_158 = 5;
                  }
                  else {
                    puVar8[5] = local_e8[5];
                    if (uVar17 == 6) {
                      local_158 = 6;
                    }
                    else {
                      local_158 = 7;
                      puVar8[6] = local_e8[6];
                    }
                  }
                }
              }
            }
          }
        }
        uVar24 = 1;
        lVar20 = 0x10;
        uVar25 = uVar23 - uVar17;
        puVar28 = (undefined8 *)(local_e8 + uVar17);
        uVar26 = uVar25 >> 3;
        puVar15 = (undefined8 *)(puVar8 + uVar17);
        uVar5 = puVar28[1];
        uVar21 = (int)uVar26 - 1U & 7;
        *puVar15 = *puVar28;
        puVar15[1] = uVar5;
        if (1 < uVar26) {
          if (uVar21 != 0) {
            if (uVar21 != 1) {
              if (uVar21 != 2) {
                if (uVar21 != 3) {
                  if (uVar21 != 4) {
                    if (uVar21 != 5) {
                      if (uVar21 != 6) {
                        uVar5 = puVar28[3];
                        uVar24 = 2;
                        lVar20 = 0x20;
                        puVar15[2] = puVar28[2];
                        puVar15[3] = uVar5;
                      }
                      uVar24 = uVar24 + 1;
                      uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                      *(undefined8 *)((long)puVar15 + lVar20) =
                           *(undefined8 *)((long)puVar28 + lVar20);
                      ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                      lVar20 = lVar20 + 0x10;
                    }
                    uVar24 = uVar24 + 1;
                    uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                    *(undefined8 *)((long)puVar15 + lVar20) =
                         *(undefined8 *)((long)puVar28 + lVar20);
                    ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                    lVar20 = lVar20 + 0x10;
                  }
                  uVar24 = uVar24 + 1;
                  uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                  *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                  ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                  lVar20 = lVar20 + 0x10;
                }
                uVar24 = uVar24 + 1;
                uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                lVar20 = lVar20 + 0x10;
              }
              uVar24 = uVar24 + 1;
              uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
              *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
              ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
              lVar20 = lVar20 + 0x10;
            }
            uVar24 = uVar24 + 1;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            lVar20 = lVar20 + 0x10;
            if (uVar26 <= uVar24) goto LAB_00140f89;
          }
          do {
            uVar24 = uVar24 + 8;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x10);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x10);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x20);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x20);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x30);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x30);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x40);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x40);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x50);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x50);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x60);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x60);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x70);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x70);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            lVar20 = lVar20 + 0x80;
          } while (uVar24 < uVar26);
        }
LAB_00140f89:
        local_158 = local_158 + (uVar25 & 0xfffffffffffffff8);
        if ((uVar25 & 0xfffffffffffffff8) != uVar25) {
          uVar17 = local_158 + 1;
          puVar8[local_158] = local_e8[local_158];
          if (uVar17 < uVar23) {
            uVar24 = local_158 + 2;
            puVar8[uVar17] = local_e8[uVar17];
            if (uVar24 < uVar23) {
              uVar17 = local_158 + 3;
              puVar8[uVar24] = local_e8[uVar24];
              if (uVar17 < uVar23) {
                uVar24 = local_158 + 4;
                puVar8[uVar17] = local_e8[uVar17];
                if (uVar24 < uVar23) {
                  uVar17 = local_158 + 5;
                  puVar8[uVar24] = local_e8[uVar24];
                  if (uVar17 < uVar23) {
                    uVar24 = local_158 + 6;
                    puVar8[uVar17] = local_e8[uVar17];
                    if (uVar24 < uVar23) {
                      uVar17 = local_158 + 7;
                      puVar8[uVar24] = local_e8[uVar24];
                      if (uVar17 < uVar23) {
                        uVar24 = local_158 + 8;
                        puVar8[uVar17] = local_e8[uVar17];
                        if (uVar24 < uVar23) {
                          uVar17 = local_158 + 9;
                          puVar8[uVar24] = local_e8[uVar24];
                          if (uVar17 < uVar23) {
                            uVar24 = local_158 + 10;
                            puVar8[uVar17] = local_e8[uVar17];
                            if (uVar24 < uVar23) {
                              uVar17 = local_158 + 0xb;
                              puVar8[uVar24] = local_e8[uVar24];
                              if (uVar17 < uVar23) {
                                uVar24 = local_158 + 0xc;
                                puVar8[uVar17] = local_e8[uVar17];
                                if (uVar24 < uVar23) {
                                  uVar17 = local_158 + 0xd;
                                  puVar8[uVar24] = local_e8[uVar24];
                                  if (uVar17 < uVar23) {
                                    puVar8[uVar17] = local_e8[uVar17];
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
LAB_0013fc06:
  puVar30[1] = 0x100000001;
  puVar30[2] = puVar30;
  *puVar30 = &PTR_FUN_01363828;
  puVar30[3] = puVar30 + 4;
  if (local_100 != '\0') {
    FUN_0047e230(&local_108);
  }
  LOCK();
  *(int *)(puVar30 + 1) = *(int *)(puVar30 + 1) + 1;
  UNLOCK();
  lVar20 = *(long *)(local_110 + 0x50);
  *(undefined8 **)(local_110 + 0x58) = puVar30 + 4;
  *(undefined8 **)(local_110 + 0x50) = puVar30;
  if (lVar20 != 0) {
    ref_counter_base::DecRef();
  }
  ref_counter_base::DecRef(puVar30);
  if ((local_f0 != 0) && (local_e8 != (undefined2 *)0x0)) {
    HeapInterface::Free();
  }
  if ((local_d0 != 0) && (local_c8 != (undefined2 *)0x0)) {
    HeapInterface::Free();
  }
LAB_0013fc9d:
  if ((CONCAT44(uStack_6c,uStack_70) != 0) && (CONCAT44(uStack_64,uStack_68) != 0)) {
    HeapInterface::Free();
  }
LAB_0013fcc0:
  game::LocationContainer::Add
            (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_118);
  if (local_b8 == '\x02') {
    local_f8[0] = 0xffffffff;
    local_108 = (undefined *)CONCAT44(local_108._4_4_,4);
    local_78 = CONCAT22(local_78._2_2_,CONCAT11(local_b7 + '\x01',0x17)) & 0xffff03ff;
    local_74 = (undefined4)local_b4;
    uStack_70 = (undefined4)((ulong)local_b4 >> 0x20);
    uStack_6c = (undefined4)uStack_ac;
    uStack_68 = (undefined4)((ulong)uStack_ac >> 0x20);
    uStack_64 = (undefined4)local_a4;
    uStack_60 = (undefined4)((ulong)local_a4 >> 0x20);
    uStack_5c = uStack_9c;
    local_54 = local_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&local_d8,&local_108,&local_134,local_f8,&local_124,&local_78);
    lVar20 = *(long *)(local_110 + 0x50);
    uVar5 = *(undefined8 *)(local_110 + 0x58);
    if (lVar20 != 0) {
      LOCK();
      *(int *)(lVar20 + 8) = *(int *)(lVar20 + 8) + 1;
      UNLOCK();
    }
    lVar27 = *(long *)(local_d0 + 0x50);
    *(undefined8 *)(local_d0 + 0x58) = uVar5;
    *(long *)(local_d0 + 0x50) = lVar20;
    if (lVar27 != 0) {
      ref_counter_base::DecRef();
    }
    game::LocationContainer::Add
              (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_d8);
    if (CONCAT44(uStack_d4,local_d8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (local_b8 == '\b') {
    local_f8[0] = 0xffffffff;
    local_108 = (undefined *)CONCAT44(local_108._4_4_,4);
    local_78 = CONCAT22(local_78._2_2_,CONCAT11(local_b7 + '\x02',0x18)) & 0xffff03ff;
    local_74 = (undefined4)local_b4;
    uStack_70 = (undefined4)((ulong)local_b4 >> 0x20);
    uStack_6c = (undefined4)uStack_ac;
    uStack_68 = (undefined4)((ulong)uStack_ac >> 0x20);
    uStack_64 = (undefined4)local_a4;
    uStack_60 = (undefined4)((ulong)local_a4 >> 0x20);
    uStack_5c = uStack_9c;
    local_54 = local_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&local_d8,&local_108,&local_134,local_f8,&local_124,&local_78);
    lVar20 = *(long *)(local_110 + 0x50);
    uVar5 = *(undefined8 *)(local_110 + 0x58);
    if (lVar20 != 0) {
      LOCK();
      *(int *)(lVar20 + 8) = *(int *)(lVar20 + 8) + 1;
      UNLOCK();
    }
    lVar27 = *(long *)(local_d0 + 0x50);
    *(undefined8 *)(local_d0 + 0x58) = uVar5;
    *(long *)(local_d0 + 0x50) = lVar20;
    if (lVar27 != 0) {
      ref_counter_base::DecRef();
    }
    game::LocationContainer::Add
              (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&local_d8);
    if (CONCAT44(uStack_d4,local_d8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (local_118 != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE` @ 00141680
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined * jag::packethandlers::ZoneUpdates::LOC_CUSTOMISE(long *thisPtr,long packet)

{
  ulong *puVar1;
  long lVar2;
  long *plVar3;
  byte bVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined8 uVar7;
  undefined2 *puVar8;
  byte bVar9;
  undefined2 uVar10;
  int iVar11;
  long *plVar12;
  long *plVar13;
  long *plVar14;
  undefined8 *puVar15;
  undefined8 *puVar16;
  ulong uVar17;
  undefined8 *puVar18;
  undefined8 *puVar19;
  long lVar20;
  uint uVar21;
  PacketCore *pPVar22;
  ulong uVar23;
  ulong uVar24;
  ulong uVar25;
  ulong uVar26;
  long lVar27;
  undefined8 *puVar28;
  long *plVar29;
  undefined8 *puVar30;
  long lStack_158;
  undefined4 uStack_134;
  long lStack_130;
  undefined4 uStack_124;
  int iStack_120;
  int iStack_11c;
  long lStack_118;
  long lStack_110;
  undefined *puStack_108;
  char cStack_100;
  undefined4 auStack_f8 [2];
  long lStack_f0;
  undefined2 *puStack_e8;
  undefined4 uStack_d8;
  undefined4 uStack_d4;
  long lStack_d0;
  undefined2 *puStack_c8;
  char cStack_b8;
  char cStack_b7;
  undefined8 uStack_b4;
  undefined8 uStack_ac;
  undefined8 uStack_a4;
  undefined8 uStack_9c;
  undefined8 uStack_94;
  undefined8 uStack_8c;
  undefined4 uStack_78;
  undefined4 uStack_74;
  undefined4 uStack_70;
  undefined4 uStack_6c;
  undefined4 uStack_68;
  undefined4 uStack_64;
  undefined4 uStack_60;
  undefined8 uStack_5c;
  undefined8 uStack_54;
  undefined8 uStack_4c;
  
  lVar20 = *(long *)(packet + 0x18);
  uStack_ac = 0x3f80000000000000;
  cStack_b8 = '\0';
  cStack_b7 = '\0';
  uStack_b4 = 0;
  uStack_a4 = 0;
  uStack_9c = 0;
  uStack_94 = 0x3f8000003f800000;
  uStack_8c = 0x3f800000;
  *(long *)(packet + 0x18) = lVar20 + 1;
  bVar9 = -*(char *)(*(long *)(packet + 0x10) + lVar20);
  uStack_134 = Packet::g4_alt3(packet);
  lVar20 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar20 + 1;
  uVar23 = CONCAT71(0xffffff,-0x80 - *(char *)(*(long *)(packet + 0x10) + lVar20));
  *(long *)(packet + 0x18) = lVar20 + 2;
  bVar4 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar20);
  FUN_00af9220(&cStack_b8,bVar4,packet);
  iStack_11c = ((uint)uVar23 & 7) + DAT_013942b0;
  iStack_120 = ((uint)(uVar23 >> 4) & 7) + DAT_013942ac;
  uStack_78 = 0xffffffff;
  uStack_d8 = 4;
  auStack_f8[0] = CONCAT31(auStack_f8[0]._1_3_,bVar4 >> 7);
  uStack_124 = DAT_013942a8;
  FUN_00b02aa0(&lStack_118);
  if ((bVar9 & 1) != 0) goto LAB_0013fcc0;
  uStack_70 = 0;
  uStack_6c = 0;
  uStack_68 = 0;
  uStack_64 = 0;
  lStack_d0 = 0;
  puStack_c8 = (undefined2 *)0x0;
  lStack_f0 = 0;
  puStack_e8 = (undefined2 *)0x0;
  if ((bVar9 & 2) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    FUN_00c2cba0(&uStack_78);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 4 + 4;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                    lVar27 = 4;
                    *(uint *)CONCAT44(uStack_64,uStack_68) = uVar21;
                  }
                  uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                  *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
                  lVar27 = lVar27 + 4;
                }
                uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
                *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
                lVar27 = lVar27 + 4;
              }
              uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
              *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
              lVar27 = lVar27 + 4;
            }
            uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
            *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
            lVar27 = lVar27 + 4;
          }
          uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
          *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
          lVar27 = lVar27 + 4;
        }
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
        lVar27 = lVar27 + 4;
        if (lVar27 == lVar20) goto LAB_0013f945;
      }
      do {
        pPVar22 = (PacketCore *)packet;
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 4 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 8 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0xc + lVar27) = uVar21;
        pPVar22 = (PacketCore *)packet;
        uVar21 = Packet::gT_unsigned_int((PacketCore *)packet);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x10 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x14 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x18 + lVar27) = uVar21;
        uVar21 = Packet::gT_unsigned_int(pPVar22);
        *(uint *)(CONCAT44(uStack_64,uStack_68) + 0x1c + lVar27) = uVar21;
        lVar27 = lVar27 + 0x20;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f945:
  if ((bVar9 & 4) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    game::ClientScript::ReadOpcodeArray(&uStack_d8);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 2 + 2;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar10 = FUN_00121a30(packet);
                    lVar27 = 2;
                    *puStack_c8 = uVar10;
                  }
                  uVar10 = FUN_00121a30(packet);
                  *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
                  lVar27 = lVar27 + 2;
                }
                uVar10 = FUN_00121a30(packet);
                *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
                lVar27 = lVar27 + 2;
              }
              uVar10 = FUN_00121a30(packet);
              *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
              lVar27 = lVar27 + 2;
            }
            uVar10 = FUN_00121a30(packet);
            *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
            lVar27 = lVar27 + 2;
          }
          uVar10 = FUN_00121a30(packet);
          *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
          lVar27 = lVar27 + 2;
        }
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
        lVar27 = lVar27 + 2;
        if (lVar27 == lVar20) goto LAB_0013f94f;
      }
      do {
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_c8 + lVar27) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 2) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 4) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 6) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 8) = uVar10;
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_c8 + lVar27 + 10) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 0xc) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_c8 + lVar27 + 0xe) = uVar10;
        lVar27 = lVar27 + 0x10;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f94f:
  if ((bVar9 & 8) != 0) {
    lVar20 = *(long *)(packet + 0x18);
    *(long *)(packet + 0x18) = lVar20 + 1;
    bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar20);
    game::ClientScript::ReadOpcodeArray(auStack_f8);
    if (bVar4 != 0) {
      lVar27 = 0;
      lVar20 = (ulong)(bVar4 - 1) * 2 + 2;
      uVar21 = bVar4 & 7;
      if ((bVar4 & 7) != 0) {
        if (uVar21 != 1) {
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    lVar27 = 2;
                    uVar10 = FUN_00121a30(packet);
                    *puStack_e8 = uVar10;
                  }
                  uVar10 = FUN_00121a30(packet);
                  *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
                  lVar27 = lVar27 + 2;
                }
                uVar10 = FUN_00121a30(packet);
                *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
                lVar27 = lVar27 + 2;
              }
              uVar10 = FUN_00121a30(packet);
              *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
              lVar27 = lVar27 + 2;
            }
            uVar10 = FUN_00121a30(packet);
            *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
            lVar27 = lVar27 + 2;
          }
          uVar10 = FUN_00121a30(packet);
          *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
          lVar27 = lVar27 + 2;
        }
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
        lVar27 = lVar27 + 2;
        if (lVar27 == lVar20) goto LAB_0013f959;
      }
      do {
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_e8 + lVar27) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 2) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 4) = uVar10;
        uVar10 = FUN_00121a30(packet);
        *(undefined2 *)((long)puStack_e8 + lVar27 + 6) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 8) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 10) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 0xc) = uVar10;
        uVar10 = FUN_00121a30();
        *(undefined2 *)((long)puStack_e8 + lVar27 + 0xe) = uVar10;
        lVar27 = lVar27 + 0x10;
      } while (lVar27 != lVar20);
    }
  }
LAB_0013f959:
  if (((CONCAT44(uStack_6c,uStack_70) != 0) || (lStack_d0 != 0)) || (lStack_f0 != 0)) {
    cStack_100 = 0;
    puStack_108 = &DAT_01394220;
    FUN_00c2a9a0();
    cStack_100 = '\x01';
    if ((DAT_01394210 == '\0') && (iVar11 = __cxa_guard_acquire(&DAT_01394210), iVar11 != 0)) {
      DAT_01394200 = (long *)0x0;
      DAT_01394208 = 0x10;
      _DAT_013941f0 = (undefined1  [16])0x0;
      puVar15 = (undefined8 *)thunk_FUN_00c29480(0x28);
      puVar30 = DAT_013941f0;
      puVar28 = puVar15;
      if (DAT_013941f8 != DAT_013941f0) {
        puVar28 = DAT_013941f0 + 5;
        uVar23 = ((ulong)((long)DAT_013941f8 - (long)puVar28) >> 3) * 0xccccccccccccccd &
                 0x1fffffffffffffff;
        uVar21 = (int)((ulong)((long)(puVar28 + uVar23 * 5) + (-0x28 - (long)DAT_013941f0)) >> 3) *
                 -0x33333333 + 1U & 3;
        puVar19 = puVar15;
        if (uVar21 == 0) goto LAB_00140810;
        if (uVar21 != 1) {
          puVar16 = DAT_013941f0;
          puVar18 = puVar15;
          if (uVar21 != 2) {
            uVar5 = DAT_013941f0[3];
            uVar6 = *DAT_013941f0;
            puVar15[1] = DAT_013941f0[1];
            uVar7 = puVar30[2];
            puVar15[3] = uVar5;
            uVar5 = puVar30[4];
            *puVar15 = uVar6;
            puVar18 = puVar15 + 5;
            puVar15[2] = uVar7;
            puVar15[4] = uVar5;
            *puVar30 = 0;
            puVar30[1] = 0;
            puVar30[2] = 0;
            puVar30[3] = 0;
            puVar30[4] = 0;
            puVar16 = puVar28;
          }
          uVar5 = puVar16[3];
          puVar19 = puVar18 + 5;
          uVar6 = *puVar16;
          puVar30 = puVar16 + 5;
          puVar18[1] = puVar16[1];
          uVar7 = puVar16[2];
          puVar18[3] = uVar5;
          uVar5 = puVar16[4];
          *puVar18 = uVar6;
          puVar18[2] = uVar7;
          puVar18[4] = uVar5;
          *puVar16 = 0;
          puVar16[1] = 0;
          puVar16[2] = 0;
          puVar16[3] = 0;
          puVar16[4] = 0;
        }
        uVar5 = puVar30[3];
        uVar6 = *puVar30;
        puVar19[1] = puVar30[1];
        uVar7 = puVar30[2];
        puVar19[3] = uVar5;
        uVar5 = puVar30[4];
        *puVar19 = uVar6;
        puVar19[2] = uVar7;
        puVar19[4] = uVar5;
        *puVar30 = 0;
        puVar30[1] = 0;
        puVar30[2] = 0;
        puVar30[3] = 0;
        puVar30[4] = 0;
        puVar19 = puVar19 + 5;
        for (puVar30 = puVar30 + 5; puVar30 != puVar28 + uVar23 * 5; puVar30 = puVar30 + 0x14) {
LAB_00140810:
          uVar5 = puVar30[3];
          uVar6 = *puVar30;
          puVar19[1] = puVar30[1];
          uVar7 = puVar30[2];
          puVar19[3] = uVar5;
          uVar5 = puVar30[4];
          *puVar19 = uVar6;
          uVar6 = puVar30[5];
          puVar19[2] = uVar7;
          uVar7 = puVar30[6];
          puVar19[4] = uVar5;
          uVar5 = puVar30[8];
          *puVar30 = 0;
          puVar30[1] = 0;
          puVar30[2] = 0;
          puVar30[3] = 0;
          puVar30[4] = 0;
          puVar19[5] = uVar6;
          puVar19[6] = uVar7;
          uVar6 = puVar30[10];
          uVar7 = puVar30[7];
          puVar19[8] = uVar5;
          uVar5 = puVar30[9];
          puVar19[7] = uVar7;
          puVar19[9] = uVar5;
          puVar30[5] = 0;
          puVar30[6] = 0;
          puVar30[7] = 0;
          puVar30[8] = 0;
          puVar30[9] = 0;
          puVar19[10] = uVar6;
          uVar5 = puVar30[0xd];
          uVar6 = puVar30[0xf];
          puVar19[0xb] = puVar30[0xb];
          uVar7 = puVar30[0xc];
          puVar19[0xd] = uVar5;
          uVar5 = puVar30[0xe];
          puVar19[0xc] = uVar7;
          uVar7 = puVar30[0x10];
          puVar19[0xe] = uVar5;
          uVar5 = puVar30[0x12];
          puVar30[10] = 0;
          puVar30[0xb] = 0;
          puVar30[0xc] = 0;
          puVar30[0xd] = 0;
          puVar30[0xe] = 0;
          puVar19[0x10] = uVar7;
          uVar7 = puVar30[0x11];
          puVar19[0x12] = uVar5;
          uVar5 = puVar30[0x13];
          puVar19[0xf] = uVar6;
          puVar19[0x11] = uVar7;
          puVar19[0x13] = uVar5;
          puVar30[0xf] = 0;
          puVar30[0x10] = 0;
          puVar30[0x11] = 0;
          puVar30[0x12] = 0;
          puVar30[0x13] = 0;
          puVar19 = puVar19 + 0x14;
        }
        puVar28 = puVar15 + uVar23 * 5 + 5;
      }
      *puVar28 = 0;
      puVar28[1] = 0;
      puVar28[2] = 0;
      puVar28[3] = 0;
      puVar28[4] = 0;
      FUN_00b06190(puVar28,0x20,0x10);
      if (DAT_013941f8 != DAT_013941f0) {
        plVar3 = (long *)((long)DAT_013941f0 +
                         ((long)DAT_013941f8 - (long)(DAT_013941f0 + 5) & 0xfffffffffffffff8U) +
                         0x28);
        plVar14 = DAT_013941f0;
        plVar29 = DAT_013941f0 + 5;
        do {
          uVar23 = 0;
          if (plVar14[3] != 0) {
            do {
              while( true ) {
                lVar20 = *(long *)(plVar14[1] + uVar23 * 8) * 0x70 + *plVar14;
                if ((*(long *)(lVar20 + 0x60) != 0) && (*(long *)(lVar20 + 0x68) != 0)) {
                  HeapInterface::Free();
                }
                if ((*(long *)(lVar20 + 0x48) != 0) && (*(long *)(lVar20 + 0x50) != 0)) {
                  HeapInterface::Free();
                }
                if ((*(long *)(lVar20 + 0x30) != 0) && (*(long *)(lVar20 + 0x38) != 0)) break;
                uVar23 = uVar23 + 1;
                if ((ulong)plVar14[3] <= uVar23) goto LAB_00141248;
              }
              HeapInterface::Free();
              uVar23 = uVar23 + 1;
            } while (uVar23 < (ulong)plVar14[3]);
          }
LAB_00141248:
          if (plVar14[4] != 0) {
            lVar20 = plVar14[1];
            uVar23 = 0;
            do {
              *(ulong *)(lVar20 + uVar23 * 8) = uVar23;
              uVar23 = uVar23 + 1;
            } while (uVar23 < (ulong)plVar14[4]);
          }
          plVar14[3] = 0;
          if (plVar14[2] != 0) {
            HeapInterface::Free();
          }
          plVar14[2] = 0;
          if (plVar14[1] != 0) {
            HeapInterface::Free();
            plVar14[1] = 0;
          }
          HeapInterface::Free(*plVar14);
          if (plVar29 == plVar3) goto LAB_00141310;
          plVar14 = plVar29;
          plVar29 = plVar29 + 5;
        } while( true );
      }
      goto LAB_00141326;
    }
    goto LAB_0013f9b3;
  }
  goto LAB_0013fc9d;
LAB_00141310:
LAB_00141326:
  if (DAT_013941f0 != (long *)0x0) {
    HeapInterface::Free(DAT_013941f0);
  }
  DAT_01394200 = puVar15 + 5;
  DAT_013941f8 = puVar28 + 5;
  DAT_013941f0 = puVar15;
  __cxa_guard_release(&DAT_01394210);
  __cxa_atexit(FUN_000f09d0,&DAT_013941f0,&PTR_LOOP_01391000);
LAB_0013f9b3:
  uVar5 = DAT_01394208;
  plVar3 = DAT_013941f8;
  if (DAT_013941f0 == DAT_013941f8) {
    lStack_130 = 0;
LAB_001405d3:
    if (DAT_013941f8 < DAT_01394200) {
      DAT_013941f8[4] = 0;
      *plVar3 = 0;
      plVar3[1] = 0;
      plVar3[2] = 0;
      plVar3[3] = 0;
      FUN_00b06190(plVar3,lStack_130,uVar5);
      lVar20 = (long)DAT_013941f8 + 0x28;
      DAT_013941f8 = (long *)lVar20;
    }
    else {
      FUN_000f9f00(&DAT_013941f0,&lStack_130);
      lVar20 = (long)DAT_013941f8;
    }
    uVar23 = *(ulong *)(lVar20 + -0x10);
    puVar30 = (undefined8 *)0x0;
    if (uVar23 < *(ulong *)(lVar20 + -8)) {
      lVar27 = *(long *)(*(long *)(lVar20 + -0x20) + uVar23 * 8);
      puVar30 = (undefined8 *)(lVar27 * 0x70 + *(long *)(lVar20 + -0x28));
      *(ulong *)(*(long *)(lVar20 + -0x18) + lVar27 * 8) = uVar23;
      *(long *)(lVar20 + -0x10) = *(long *)(lVar20 + -0x10) + 1;
    }
  }
  else {
    uVar23 = DAT_013941f0[3];
    plVar14 = DAT_013941f0;
    if ((ulong)DAT_013941f0[4] <= uVar23) {
      plVar29 = DAT_013941f0 + 5;
      uVar23 = ((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) * 5;
      uVar21 = (uint)uVar23 & 7;
      plVar13 = plVar29;
      if ((uVar23 & 7) != 0) {
        plVar13 = DAT_013941f0 + 10;
        uVar23 = DAT_013941f0[8];
        plVar14 = plVar29;
        if (uVar23 < (ulong)DAT_013941f0[9]) goto LAB_0013fb60;
        if (uVar21 != 1) {
          plVar12 = plVar13;
          if (uVar21 != 2) {
            if (uVar21 != 3) {
              if (uVar21 != 4) {
                if (uVar21 != 5) {
                  if (uVar21 != 6) {
                    uVar23 = DAT_013941f0[0xd];
                    plVar12 = DAT_013941f0 + 0xf;
                    plVar14 = plVar13;
                    if (uVar23 < (ulong)DAT_013941f0[0xe]) goto LAB_0013fb60;
                  }
                  uVar23 = plVar12[3];
                  plVar13 = plVar12 + 5;
                  plVar14 = plVar12;
                  if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
                }
                uVar23 = plVar13[3];
                plVar12 = plVar13 + 5;
                plVar14 = plVar13;
                if (uVar23 < (ulong)plVar13[4]) goto LAB_0013fb60;
              }
              uVar23 = plVar12[3];
              plVar13 = plVar12 + 5;
              plVar14 = plVar12;
              if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
            }
            uVar23 = plVar13[3];
            plVar12 = plVar13 + 5;
            plVar14 = plVar13;
            if (uVar23 < (ulong)plVar13[4]) goto LAB_0013fb60;
          }
          uVar23 = plVar12[3];
          plVar13 = plVar12 + 5;
          plVar14 = plVar12;
          if (uVar23 < (ulong)plVar12[4]) goto LAB_0013fb60;
        }
      }
      do {
        if (DAT_013941f8 == plVar13) {
          plVar14 = DAT_013941f0 + 4;
          lStack_130 = 0;
          uVar21 = (int)((ulong)((long)(DAT_013941f0 +
                                       (((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) *
                                        0xccccccccccccccd & 0x1fffffffffffffff) * 5 + 9) +
                                (-0x28 - (long)plVar14)) >> 3) * -0x33333333 + 1U & 7;
          if (uVar21 == 0) goto LAB_0014059d;
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      lStack_130 = *plVar14;
                      plVar14 = DAT_013941f0 + 9;
                    }
                    lStack_130 = lStack_130 + *plVar14;
                    plVar14 = plVar14 + 5;
                  }
                  lStack_130 = lStack_130 + *plVar14;
                  plVar14 = plVar14 + 5;
                }
                lStack_130 = lStack_130 + *plVar14;
                plVar14 = plVar14 + 5;
              }
              lStack_130 = lStack_130 + *plVar14;
              plVar14 = plVar14 + 5;
            }
            lStack_130 = lStack_130 + *plVar14;
            plVar14 = plVar14 + 5;
          }
          lStack_130 = lStack_130 + *plVar14;
          for (plVar14 = plVar14 + 5;
              plVar14 !=
              DAT_013941f0 +
              (((ulong)((long)DAT_013941f8 - (long)plVar29) >> 3) * 0xccccccccccccccd &
              0x1fffffffffffffff) * 5 + 9; plVar14 = plVar14 + 0x28) {
LAB_0014059d:
            lStack_130 = lStack_130 + *plVar14 + plVar14[5] + plVar14[10] + plVar14[0xf] +
                         plVar14[0x14] + plVar14[0x19] + plVar14[0x1e] + plVar14[0x23];
          }
          goto LAB_001405d3;
        }
        uVar23 = plVar13[3];
        plVar14 = plVar13;
        if (uVar23 < (ulong)plVar13[4]) break;
        uVar23 = plVar13[8];
        plVar14 = plVar13 + 5;
        if (uVar23 < (ulong)plVar13[9]) break;
        uVar23 = plVar13[0xd];
        plVar14 = plVar13 + 10;
        if (uVar23 < (ulong)plVar13[0xe]) break;
        uVar23 = plVar13[0x12];
        plVar14 = plVar13 + 0xf;
        if (uVar23 < (ulong)plVar13[0x13]) break;
        uVar23 = plVar13[0x17];
        plVar14 = plVar13 + 0x14;
        if (uVar23 < (ulong)plVar13[0x18]) break;
        uVar23 = plVar13[0x1c];
        plVar14 = plVar13 + 0x19;
        if (uVar23 < (ulong)plVar13[0x1d]) break;
        uVar23 = plVar13[0x21];
        plVar12 = plVar13 + 0x23;
        plVar14 = plVar13 + 0x1e;
        if (uVar23 < (ulong)plVar13[0x22]) break;
        uVar23 = plVar13[0x26];
        puVar1 = (ulong *)(plVar13 + 0x27);
        plVar13 = plVar13 + 0x28;
        plVar14 = plVar12;
      } while (*puVar1 <= uVar23);
    }
LAB_0013fb60:
    lVar20 = *(long *)(plVar14[1] + uVar23 * 8);
    puVar30 = (undefined8 *)(lVar20 * 0x70 + *plVar14);
    *(ulong *)(plVar14[2] + lVar20 * 8) = uVar23;
    plVar14[3] = plVar14[3] + 1;
  }
  FUN_0047e230(&puStack_108);
  puVar30[6] = 0;
  puVar30[7] = 0;
  *(int *)(puVar30 + 4) = DAT_01391008;
  DAT_01391008 = DAT_01391008 + 1;
  FUN_00139520(puVar30 + 5,&uStack_78);
  puVar30[9] = 0;
  puVar30[10] = 0;
  if (lStack_d0 != 0) {
    game::ClientScript::ReadOpcodeArray(puVar30 + 8);
    uVar23 = puVar30[9];
    if (uVar23 != 0) {
      puVar8 = (undefined2 *)puVar30[10];
      if ((puStack_c8 < puVar8 + 8 && puVar8 < puStack_c8 + 8) || (uVar23 < 0xf)) {
        uVar17 = 0;
        uVar21 = (uint)uVar23 & 7;
        if ((uVar23 & 7) != 0) {
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      *puVar8 = *puStack_c8;
                    }
                    uVar17 = (ulong)(uVar21 != 6);
                    puVar8[uVar17] = puStack_c8[uVar17];
                    uVar17 = uVar17 + 1;
                  }
                  puVar8[uVar17] = puStack_c8[uVar17];
                  uVar17 = uVar17 + 1;
                }
                puVar8[uVar17] = puStack_c8[uVar17];
                uVar17 = uVar17 + 1;
              }
              puVar8[uVar17] = puStack_c8[uVar17];
              uVar17 = uVar17 + 1;
            }
            puVar8[uVar17] = puStack_c8[uVar17];
            uVar17 = uVar17 + 1;
          }
          puVar8[uVar17] = puStack_c8[uVar17];
          uVar17 = uVar17 + 1;
          if (uVar23 == uVar17) goto LAB_0013fbe5;
        }
        do {
          puVar8[uVar17] = puStack_c8[uVar17];
          puVar8[uVar17 + 1] = puStack_c8[uVar17 + 1];
          puVar8[uVar17 + 2] = puStack_c8[uVar17 + 2];
          lVar20 = uVar17 + 5;
          puVar8[uVar17 + 3] = puStack_c8[uVar17 + 3];
          puVar8[uVar17 + 4] = puStack_c8[uVar17 + 4];
          lVar27 = uVar17 + 6;
          lVar2 = uVar17 + 7;
          uVar17 = uVar17 + 8;
          puVar8[lVar20] = puStack_c8[lVar20];
          puVar8[lVar27] = puStack_c8[lVar27];
          puVar8[lVar2] = puStack_c8[lVar2];
        } while (uVar23 != uVar17);
      }
      else {
        uVar21 = -(int)((ulong)puStack_c8 >> 1) & 7;
        uVar17 = (ulong)uVar21;
        if (uVar21 == 0) {
          lStack_158 = 0;
        }
        else {
          *puVar8 = *puStack_c8;
          if (uVar17 == 1) {
            lStack_158 = 1;
          }
          else {
            puVar8[1] = puStack_c8[1];
            if (uVar17 == 2) {
              lStack_158 = 2;
            }
            else {
              puVar8[2] = puStack_c8[2];
              if (uVar17 == 3) {
                lStack_158 = 3;
              }
              else {
                puVar8[3] = puStack_c8[3];
                if (uVar17 == 4) {
                  lStack_158 = 4;
                }
                else {
                  puVar8[4] = puStack_c8[4];
                  if (uVar17 == 5) {
                    lStack_158 = 5;
                  }
                  else {
                    puVar8[5] = puStack_c8[5];
                    if (uVar17 == 6) {
                      lStack_158 = 6;
                    }
                    else {
                      lStack_158 = 7;
                      puVar8[6] = puStack_c8[6];
                    }
                  }
                }
              }
            }
          }
        }
        uVar24 = 1;
        lVar20 = 0x10;
        uVar25 = uVar23 - uVar17;
        puVar28 = (undefined8 *)(puStack_c8 + uVar17);
        uVar26 = uVar25 >> 3;
        puVar15 = (undefined8 *)(puVar8 + uVar17);
        uVar5 = puVar28[1];
        uVar21 = (int)uVar26 - 1U & 7;
        *puVar15 = *puVar28;
        puVar15[1] = uVar5;
        if (1 < uVar26) {
          if (uVar21 != 0) {
            if (uVar21 != 1) {
              if (uVar21 != 2) {
                if (uVar21 != 3) {
                  if (uVar21 != 4) {
                    if (uVar21 != 5) {
                      if (uVar21 != 6) {
                        uVar5 = puVar28[3];
                        uVar24 = 2;
                        lVar20 = 0x20;
                        puVar15[2] = puVar28[2];
                        puVar15[3] = uVar5;
                      }
                      uVar24 = uVar24 + 1;
                      uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                      *(undefined8 *)((long)puVar15 + lVar20) =
                           *(undefined8 *)((long)puVar28 + lVar20);
                      ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                      lVar20 = lVar20 + 0x10;
                    }
                    uVar24 = uVar24 + 1;
                    uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                    *(undefined8 *)((long)puVar15 + lVar20) =
                         *(undefined8 *)((long)puVar28 + lVar20);
                    ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                    lVar20 = lVar20 + 0x10;
                  }
                  uVar24 = uVar24 + 1;
                  uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                  *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                  ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                  lVar20 = lVar20 + 0x10;
                }
                uVar24 = uVar24 + 1;
                uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                lVar20 = lVar20 + 0x10;
              }
              uVar24 = uVar24 + 1;
              uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
              *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
              ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
              lVar20 = lVar20 + 0x10;
            }
            uVar24 = uVar24 + 1;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            lVar20 = lVar20 + 0x10;
            if (uVar26 <= uVar24) goto LAB_00140beb;
          }
          do {
            uVar24 = uVar24 + 8;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x10);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x10);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x20);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x20);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x30);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x30);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x40);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x40);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x50);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x50);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x60);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x60);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x70);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x70);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            lVar20 = lVar20 + 0x80;
          } while (uVar24 < uVar26);
        }
LAB_00140beb:
        lStack_158 = lStack_158 + (uVar25 & 0xfffffffffffffff8);
        if ((uVar25 & 0xfffffffffffffff8) != uVar25) {
          uVar17 = lStack_158 + 1;
          puVar8[lStack_158] = puStack_c8[lStack_158];
          if (uVar17 < uVar23) {
            uVar24 = lStack_158 + 2;
            puVar8[uVar17] = puStack_c8[uVar17];
            if (uVar24 < uVar23) {
              uVar17 = lStack_158 + 3;
              puVar8[uVar24] = puStack_c8[uVar24];
              if (uVar17 < uVar23) {
                uVar24 = lStack_158 + 4;
                puVar8[uVar17] = puStack_c8[uVar17];
                if (uVar24 < uVar23) {
                  uVar17 = lStack_158 + 5;
                  puVar8[uVar24] = puStack_c8[uVar24];
                  if (uVar17 < uVar23) {
                    uVar24 = lStack_158 + 6;
                    puVar8[uVar17] = puStack_c8[uVar17];
                    if (uVar24 < uVar23) {
                      uVar17 = lStack_158 + 7;
                      puVar8[uVar24] = puStack_c8[uVar24];
                      if (uVar17 < uVar23) {
                        uVar24 = lStack_158 + 8;
                        puVar8[uVar17] = puStack_c8[uVar17];
                        if (uVar24 < uVar23) {
                          uVar17 = lStack_158 + 9;
                          puVar8[uVar24] = puStack_c8[uVar24];
                          if (uVar17 < uVar23) {
                            uVar24 = lStack_158 + 10;
                            puVar8[uVar17] = puStack_c8[uVar17];
                            if (uVar24 < uVar23) {
                              uVar17 = lStack_158 + 0xb;
                              puVar8[uVar24] = puStack_c8[uVar24];
                              if (uVar17 < uVar23) {
                                uVar24 = lStack_158 + 0xc;
                                puVar8[uVar17] = puStack_c8[uVar17];
                                if (uVar24 < uVar23) {
                                  uVar17 = lStack_158 + 0xd;
                                  puVar8[uVar24] = puStack_c8[uVar24];
                                  if (uVar17 < uVar23) {
                                    puVar8[uVar17] = puStack_c8[uVar17];
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
LAB_0013fbe5:
  puVar30[0xc] = 0;
  puVar30[0xd] = 0;
  if (lStack_f0 != 0) {
    game::ClientScript::ReadOpcodeArray(puVar30 + 0xb);
    uVar23 = puVar30[0xc];
    if (uVar23 != 0) {
      puVar8 = (undefined2 *)puVar30[0xd];
      if ((puStack_e8 < puVar8 + 8 && puVar8 < puStack_e8 + 8) || (uVar23 < 0xf)) {
        uVar17 = 0;
        uVar21 = (uint)uVar23 & 7;
        if ((uVar23 & 7) != 0) {
          if (uVar21 != 1) {
            if (uVar21 != 2) {
              if (uVar21 != 3) {
                if (uVar21 != 4) {
                  if (uVar21 != 5) {
                    if (uVar21 != 6) {
                      *puVar8 = *puStack_e8;
                    }
                    uVar17 = (ulong)(uVar21 != 6);
                    puVar8[uVar17] = puStack_e8[uVar17];
                    uVar17 = uVar17 + 1;
                  }
                  puVar8[uVar17] = puStack_e8[uVar17];
                  uVar17 = uVar17 + 1;
                }
                puVar8[uVar17] = puStack_e8[uVar17];
                uVar17 = uVar17 + 1;
              }
              puVar8[uVar17] = puStack_e8[uVar17];
              uVar17 = uVar17 + 1;
            }
            puVar8[uVar17] = puStack_e8[uVar17];
            uVar17 = uVar17 + 1;
          }
          puVar8[uVar17] = puStack_e8[uVar17];
          uVar17 = uVar17 + 1;
          if (uVar23 == uVar17) goto LAB_0013fc06;
        }
        do {
          puVar8[uVar17] = puStack_e8[uVar17];
          puVar8[uVar17 + 1] = puStack_e8[uVar17 + 1];
          puVar8[uVar17 + 2] = puStack_e8[uVar17 + 2];
          puVar8[uVar17 + 3] = puStack_e8[uVar17 + 3];
          lVar20 = uVar17 + 6;
          puVar8[uVar17 + 4] = puStack_e8[uVar17 + 4];
          puVar8[uVar17 + 5] = puStack_e8[uVar17 + 5];
          lVar27 = uVar17 + 7;
          uVar17 = uVar17 + 8;
          puVar8[lVar20] = puStack_e8[lVar20];
          puVar8[lVar27] = puStack_e8[lVar27];
        } while (uVar23 != uVar17);
      }
      else {
        uVar21 = -(int)((ulong)puStack_e8 >> 1) & 7;
        uVar17 = (ulong)uVar21;
        if (uVar21 == 0) {
          lStack_158 = 0;
        }
        else {
          *puVar8 = *puStack_e8;
          if (uVar17 == 1) {
            lStack_158 = 1;
          }
          else {
            puVar8[1] = puStack_e8[1];
            if (uVar17 == 2) {
              lStack_158 = 2;
            }
            else {
              puVar8[2] = puStack_e8[2];
              if (uVar17 == 3) {
                lStack_158 = 3;
              }
              else {
                puVar8[3] = puStack_e8[3];
                if (uVar17 == 4) {
                  lStack_158 = 4;
                }
                else {
                  puVar8[4] = puStack_e8[4];
                  if (uVar17 == 5) {
                    lStack_158 = 5;
                  }
                  else {
                    puVar8[5] = puStack_e8[5];
                    if (uVar17 == 6) {
                      lStack_158 = 6;
                    }
                    else {
                      lStack_158 = 7;
                      puVar8[6] = puStack_e8[6];
                    }
                  }
                }
              }
            }
          }
        }
        uVar24 = 1;
        lVar20 = 0x10;
        uVar25 = uVar23 - uVar17;
        puVar28 = (undefined8 *)(puStack_e8 + uVar17);
        uVar26 = uVar25 >> 3;
        puVar15 = (undefined8 *)(puVar8 + uVar17);
        uVar5 = puVar28[1];
        uVar21 = (int)uVar26 - 1U & 7;
        *puVar15 = *puVar28;
        puVar15[1] = uVar5;
        if (1 < uVar26) {
          if (uVar21 != 0) {
            if (uVar21 != 1) {
              if (uVar21 != 2) {
                if (uVar21 != 3) {
                  if (uVar21 != 4) {
                    if (uVar21 != 5) {
                      if (uVar21 != 6) {
                        uVar5 = puVar28[3];
                        uVar24 = 2;
                        lVar20 = 0x20;
                        puVar15[2] = puVar28[2];
                        puVar15[3] = uVar5;
                      }
                      uVar24 = uVar24 + 1;
                      uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                      *(undefined8 *)((long)puVar15 + lVar20) =
                           *(undefined8 *)((long)puVar28 + lVar20);
                      ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                      lVar20 = lVar20 + 0x10;
                    }
                    uVar24 = uVar24 + 1;
                    uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                    *(undefined8 *)((long)puVar15 + lVar20) =
                         *(undefined8 *)((long)puVar28 + lVar20);
                    ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                    lVar20 = lVar20 + 0x10;
                  }
                  uVar24 = uVar24 + 1;
                  uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                  *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                  ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                  lVar20 = lVar20 + 0x10;
                }
                uVar24 = uVar24 + 1;
                uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
                *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
                ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
                lVar20 = lVar20 + 0x10;
              }
              uVar24 = uVar24 + 1;
              uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
              *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
              ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
              lVar20 = lVar20 + 0x10;
            }
            uVar24 = uVar24 + 1;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            lVar20 = lVar20 + 0x10;
            if (uVar26 <= uVar24) goto LAB_00140f89;
          }
          do {
            uVar24 = uVar24 + 8;
            uVar5 = ((undefined8 *)((long)puVar28 + lVar20))[1];
            *(undefined8 *)((long)puVar15 + lVar20) = *(undefined8 *)((long)puVar28 + lVar20);
            ((undefined8 *)((long)puVar15 + lVar20))[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x10);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x10);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x20);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x20);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x30);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x30);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x40);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x40);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x50);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x50);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x60);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x60);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            puVar19 = (undefined8 *)((long)puVar28 + lVar20 + 0x70);
            uVar5 = puVar19[1];
            puVar18 = (undefined8 *)((long)puVar15 + lVar20 + 0x70);
            *puVar18 = *puVar19;
            puVar18[1] = uVar5;
            lVar20 = lVar20 + 0x80;
          } while (uVar24 < uVar26);
        }
LAB_00140f89:
        lStack_158 = lStack_158 + (uVar25 & 0xfffffffffffffff8);
        if ((uVar25 & 0xfffffffffffffff8) != uVar25) {
          uVar17 = lStack_158 + 1;
          puVar8[lStack_158] = puStack_e8[lStack_158];
          if (uVar17 < uVar23) {
            uVar24 = lStack_158 + 2;
            puVar8[uVar17] = puStack_e8[uVar17];
            if (uVar24 < uVar23) {
              uVar17 = lStack_158 + 3;
              puVar8[uVar24] = puStack_e8[uVar24];
              if (uVar17 < uVar23) {
                uVar24 = lStack_158 + 4;
                puVar8[uVar17] = puStack_e8[uVar17];
                if (uVar24 < uVar23) {
                  uVar17 = lStack_158 + 5;
                  puVar8[uVar24] = puStack_e8[uVar24];
                  if (uVar17 < uVar23) {
                    uVar24 = lStack_158 + 6;
                    puVar8[uVar17] = puStack_e8[uVar17];
                    if (uVar24 < uVar23) {
                      uVar17 = lStack_158 + 7;
                      puVar8[uVar24] = puStack_e8[uVar24];
                      if (uVar17 < uVar23) {
                        uVar24 = lStack_158 + 8;
                        puVar8[uVar17] = puStack_e8[uVar17];
                        if (uVar24 < uVar23) {
                          uVar17 = lStack_158 + 9;
                          puVar8[uVar24] = puStack_e8[uVar24];
                          if (uVar17 < uVar23) {
                            uVar24 = lStack_158 + 10;
                            puVar8[uVar17] = puStack_e8[uVar17];
                            if (uVar24 < uVar23) {
                              uVar17 = lStack_158 + 0xb;
                              puVar8[uVar24] = puStack_e8[uVar24];
                              if (uVar17 < uVar23) {
                                uVar24 = lStack_158 + 0xc;
                                puVar8[uVar17] = puStack_e8[uVar17];
                                if (uVar24 < uVar23) {
                                  uVar17 = lStack_158 + 0xd;
                                  puVar8[uVar24] = puStack_e8[uVar24];
                                  if (uVar17 < uVar23) {
                                    puVar8[uVar17] = puStack_e8[uVar17];
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
LAB_0013fc06:
  puVar30[1] = 0x100000001;
  puVar30[2] = puVar30;
  *puVar30 = &PTR_FUN_01363828;
  puVar30[3] = puVar30 + 4;
  if (cStack_100 != '\0') {
    FUN_0047e230(&puStack_108);
  }
  LOCK();
  *(int *)(puVar30 + 1) = *(int *)(puVar30 + 1) + 1;
  UNLOCK();
  lVar20 = *(long *)(lStack_110 + 0x50);
  *(undefined8 **)(lStack_110 + 0x58) = puVar30 + 4;
  *(undefined8 **)(lStack_110 + 0x50) = puVar30;
  if (lVar20 != 0) {
    ref_counter_base::DecRef();
  }
  ref_counter_base::DecRef(puVar30);
  if ((lStack_f0 != 0) && (puStack_e8 != (undefined2 *)0x0)) {
    HeapInterface::Free();
  }
  if ((lStack_d0 != 0) && (puStack_c8 != (undefined2 *)0x0)) {
    HeapInterface::Free();
  }
LAB_0013fc9d:
  if ((CONCAT44(uStack_6c,uStack_70) != 0) && (CONCAT44(uStack_64,uStack_68) != 0)) {
    HeapInterface::Free();
  }
LAB_0013fcc0:
  game::LocationContainer::Add
            (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&lStack_118);
  if (cStack_b8 == '\x02') {
    auStack_f8[0] = 0xffffffff;
    puStack_108 = (undefined *)CONCAT44(puStack_108._4_4_,4);
    uStack_78 = CONCAT22(uStack_78._2_2_,CONCAT11(cStack_b7 + '\x01',0x17)) & 0xffff03ff;
    uStack_74 = (undefined4)uStack_b4;
    uStack_70 = (undefined4)((ulong)uStack_b4 >> 0x20);
    uStack_6c = (undefined4)uStack_ac;
    uStack_68 = (undefined4)((ulong)uStack_ac >> 0x20);
    uStack_64 = (undefined4)uStack_a4;
    uStack_60 = (undefined4)((ulong)uStack_a4 >> 0x20);
    uStack_5c = uStack_9c;
    uStack_54 = uStack_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&uStack_d8,&puStack_108,&uStack_134,auStack_f8,&uStack_124,&uStack_78);
    lVar20 = *(long *)(lStack_110 + 0x50);
    uVar5 = *(undefined8 *)(lStack_110 + 0x58);
    if (lVar20 != 0) {
      LOCK();
      *(int *)(lVar20 + 8) = *(int *)(lVar20 + 8) + 1;
      UNLOCK();
    }
    lVar27 = *(long *)(lStack_d0 + 0x50);
    *(undefined8 *)(lStack_d0 + 0x58) = uVar5;
    *(long *)(lStack_d0 + 0x50) = lVar20;
    if (lVar27 != 0) {
      ref_counter_base::DecRef();
    }
    game::LocationContainer::Add
              (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&uStack_d8);
    if (CONCAT44(uStack_d4,uStack_d8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (cStack_b8 == '\b') {
    auStack_f8[0] = 0xffffffff;
    puStack_108 = (undefined *)CONCAT44(puStack_108._4_4_,4);
    uStack_78 = CONCAT22(uStack_78._2_2_,CONCAT11(cStack_b7 + '\x02',0x18)) & 0xffff03ff;
    uStack_74 = (undefined4)uStack_b4;
    uStack_70 = (undefined4)((ulong)uStack_b4 >> 0x20);
    uStack_6c = (undefined4)uStack_ac;
    uStack_68 = (undefined4)((ulong)uStack_ac >> 0x20);
    uStack_64 = (undefined4)uStack_a4;
    uStack_60 = (undefined4)((ulong)uStack_a4 >> 0x20);
    uStack_5c = uStack_9c;
    uStack_54 = uStack_94;
    uStack_4c = uStack_8c;
    FUN_00137b90(&uStack_d8,&puStack_108,&uStack_134,auStack_f8,&uStack_124,&uStack_78);
    lVar20 = *(long *)(lStack_110 + 0x50);
    uVar5 = *(undefined8 *)(lStack_110 + 0x58);
    if (lVar20 != 0) {
      LOCK();
      *(int *)(lVar20 + 8) = *(int *)(lVar20 + 8) + 1;
      UNLOCK();
    }
    lVar27 = *(long *)(lStack_d0 + 0x50);
    *(undefined8 *)(lStack_d0 + 0x58) = uVar5;
    *(long *)(lStack_d0 + 0x50) = lVar20;
    if (lVar27 != 0) {
      ref_counter_base::DecRef();
    }
    game::LocationContainer::Add
              (*(undefined8 *)((long)&__DT_RELA[0xcfa].r_offset + *thisPtr),&uStack_d8);
    if (CONCAT44(uStack_d4,uStack_d8) != 0) {
      ref_counter_base::DecRef();
    }
  }
  if (lStack_118 != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::VARP_LONG` @ 00141690
```c

undefined * jag::packethandlers::ClientState::VARP_LONG(long *param_1,long param_2)

{
  long lVar1;
  undefined4 uVar2;
  undefined4 uVar3;
  undefined *puVar4;
  uint varId;
  undefined8 local_38 [3];
  undefined1 local_20;
  
  lVar1 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar1 + 2;
  varId = (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar1) * 0x100 +
          (uint)(byte)(*(char *)(*(long *)(param_2 + 0x10) + 1 + lVar1) + 0x80) & 0xffff;
  uVar2 = Packet::g4_alt3(param_2);
  uVar3 = Packet::g4_alt3(param_2);
  puVar4 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + *param_1),DAT_013a2fe0,varId);
  local_20 = 1;
  local_38[0] = CONCAT44(uVar2,uVar3);
  game::PlayerVarDomain::set(*param_1 + 0x19b40,puVar4,local_38);
  FUN_0047fe00(local_38);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::RESET_ENTITY_LISTS` @ 00143d50
```c

undefined * jag::packethandlers::Misc::RESET_ENTITY_LISTS(long *param_1)

{
  undefined1 local_38 [16];
  code *local_28;
  code *pcStack_20;
  
  local_28 = FUN_000ec100;
  pcStack_20 = FUN_000eb6f0;
  FUN_00143a20(*(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + *param_1),local_38);
  if (local_28 != (code *)0x0) {
    (*local_28)(local_38,local_38,3);
  }
  local_28 = FUN_000ec0f0;
  pcStack_20 = FUN_000f3f90;
  FUN_00143c90(*(undefined8 *)((long)&__DT_RELA[0xcfb].r_addend + *param_1),local_38);
  if (local_28 != (code *)0x0) {
    (*local_28)(local_38,local_38,3);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::RUNCLIENTSCRIPT` @ 00145370
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void jag::packethandlers::ClientState::RUNCLIENTSCRIPT(long param_1,int param_2,int param_3)

{
  long lVar1;
  ulong uVar2;
  char cVar3;
  int iVar4;
  long *plVar5;
  long lVar6;
  double dVar7;
  double dVar8;
  double dVar9;
  double dVar10;
  double dVar11;
  undefined1 local_48 [16];
  undefined8 local_38;
  undefined8 uStack_30;
  
  plVar5 = (long *)(param_1 + 0x248);
  if (*(int *)(param_1 + 0x418) == 6) {
    plVar5 = (long *)(param_1 + 0x78);
  }
  if (*(code **)(*plVar5 + 0xd8) == FUN_006bcc90) {
    if ((plVar5[0xb] != 0) && ((int)plVar5[9] == 1)) {
      cVar3 = *(char *)(plVar5[0xb] + 0xc);
      goto LAB_00145464;
    }
  }
  else {
    cVar3 = (**(code **)(*plVar5 + 0xd8))();
LAB_00145464:
    if (cVar3 != '\0') {
      if (param_2 < 0x81) {
        iVar4 = 0x400;
        param_2 = 0x80;
      }
      else if (param_2 < 0x180) {
        iVar4 = param_2 * 8;
      }
      else {
        iVar4 = 0xc00;
        param_2 = 0x180;
      }
      param_3 = param_3 % 0x800;
      if (param_3 < 0) {
        param_3 = param_3 + 0x800;
      }
      goto LAB_001453c9;
    }
  }
  iVar4 = param_2 * 8;
LAB_001453c9:
  param_3 = 0x400 - param_3;
  plVar5 = (long *)(param_1 + 0x248);
  local_48 = (undefined1  [16])0x0;
  local_38 = _DAT_00cb6c60;
  uStack_30 = _UNK_00cb6c68;
  FUN_00ae6380(local_48,iVar4,param_3 * 8);
  if (*(int *)(param_1 + 0x418) == 6) {
    plVar5 = (long *)(param_1 + 0x78);
  }
  if (*(code **)(*plVar5 + 0xb0) == FUN_006eae50) {
    cVar3 = FUN_006ea100(plVar5);
    if (((cVar3 != '\0') && ((char)plVar5[5] != '\0')) && ((int)plVar5[9] == 1)) {
      if (*(code **)(*plVar5 + 0xc0) == FUN_00474f40) {
        plVar5 = plVar5 + 10;
      }
      else {
        plVar5 = (long *)(**(code **)(*plVar5 + 0xc0))(plVar5);
      }
      lVar1 = plVar5[1];
      if (*(int *)(lVar1 + 0xd4) < 1) {
        *(undefined8 *)(lVar1 + 0x98) = 0;
        *(undefined8 *)(lVar1 + 0xa0) = local_48._0_8_;
        *(undefined8 *)(lVar1 + 0xa8) = local_48._8_8_;
        *(undefined8 *)(lVar1 + 0xb0) = local_38;
        *(undefined8 *)(lVar1 + 0xb8) = uStack_30;
      }
      else {
        if (DAT_015bf228 < *(ulong *)(lVar1 + 0x98)) {
          uVar2 = *(ulong *)(lVar1 + 0x70);
          dVar10 = *(double *)(lVar1 + 0x90);
          dVar7 = *(double *)(lVar1 + 0x88);
          dVar8 = *(double *)(lVar1 + 0x80);
          dVar9 = *(double *)(lVar1 + 0x78);
          if (uVar2 <= DAT_015bf228) {
            dVar11 = (double)(float)((double)(DAT_015bf228 - uVar2) /
                                    (double)(*(ulong *)(lVar1 + 0x98) - uVar2));
            dVar10 = dVar10 + (*(double *)(lVar1 + 0xb8) - dVar10) * dVar11;
            dVar7 = dVar7 + (*(double *)(lVar1 + 0xb0) - dVar7) * dVar11;
            dVar8 = dVar8 + (*(double *)(lVar1 + 0xa8) - dVar8) * dVar11;
            dVar9 = dVar9 + dVar11 * (*(double *)(lVar1 + 0xa0) - dVar9);
          }
        }
        else {
          dVar9 = *(double *)(lVar1 + 0xa0);
          dVar8 = *(double *)(lVar1 + 0xa8);
          dVar7 = *(double *)(lVar1 + 0xb0);
          dVar10 = *(double *)(lVar1 + 0xb8);
        }
        lVar6 = (long)*(int *)(lVar1 + 0xd4) + DAT_015bf228;
        *(ulong *)(lVar1 + 0x70) = DAT_015bf228;
        *(long *)(lVar1 + 0x98) = lVar6;
        *(undefined8 *)(lVar1 + 0xa0) = local_48._0_8_;
        *(undefined8 *)(lVar1 + 0xa8) = local_48._8_8_;
        *(undefined8 *)(lVar1 + 0xb0) = local_38;
        *(undefined8 *)(lVar1 + 0xb8) = uStack_30;
        *(double *)(lVar1 + 0x78) = dVar9;
        *(double *)(lVar1 + 0x80) = dVar8;
        *(double *)(lVar1 + 0x88) = dVar7;
        *(double *)(lVar1 + 0x90) = dVar10;
      }
      *(int *)(lVar1 + 0xc4) = param_2;
      *(int *)(lVar1 + 200) = param_3;
      *(undefined1 *)(lVar1 + 0xc0) = 1;
    }
  }
  else {
    (**(code **)(*plVar5 + 0xb0))(plVar5,local_48,param_2,param_3);
  }
  *(undefined1 *)(param_1 + 0x538) = 1;
  return;
}


```

## `jag::packethandlers::ClientState::SET_VARC_STR_LARGE` @ 00150220
```c

undefined * jag::packethandlers::ClientState::SET_VARC_STR_LARGE(long *param_1,long param_2)

{
  long lVar1;
  undefined *puVar2;
  long lVar3;
  undefined1 local_58;
  undefined7 uStack_57;
  char local_41;
  long local_38 [2];
  char local_21;
  
                    /* SET_VARC_STR_LARGE (948 op 116, varShort). VARC-string, STRING-FIRST variant.
                       Wire: string(value, CP1252) + id(BE u16, low-byte byteAdd). ->
                       InterfaceManager::CreateOrFindUpdateEntry type=2 (string slot). DISTINCT from
                       op 92 SET_VARC_STR_SMALL which is id-FIRST then string. UNREGISTERED:
                       ClientSetVarcStr Kotlin class is bound to op 92 (id-first); op 116 needs its
                       own class. */
  local_58 = 0;
  local_41 = '\x17';
  FUN_00ad80e0(param_2,&local_58);
  lVar3 = *(long *)(param_2 + 0x18);
  lVar1 = *param_1;
  *(long *)(param_2 + 0x18) = lVar3 + 2;
  puVar2 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + lVar1),DAT_013a3a40,
                      (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar3) * 0x100 +
                      (uint)(byte)(*(char *)(*(long *)(param_2 + 0x10) + 1 + lVar3) + 0x80) & 0xffff
                     );
  lVar3 = *(long *)(puVar2 + 8);
  lVar1 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  *(int *)(lVar1 + 0x10) = *(int *)(lVar1 + 0x10) + 1;
  *(undefined1 *)(lVar1 + 0x14) = 1;
  if (lVar3 != 0) {
    lVar3 = InterfaceManager::CreateOrFindUpdateEntry(lVar1,2,*(undefined4 *)(lVar3 + 8));
    InterfaceManager::MarkUpdateEntryDirty(lVar1,lVar3);
    FUN_0048d480(local_38,&local_58);
    FUN_0022f4a0(lVar3 + 0x20,local_38);
    if ((local_21 < '\0') && (local_38[0] != 0)) {
      eastl__basic_string();
    }
  }
  if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    eastl__basic_string();
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_VARC_STR_SMALL` @ 00150340
```c

/* Setting prototype: undefined * SET_VARC_STR_SMALL(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::Misc::SET_VARC_STR_SMALL(long *thisPtr,long packetPtr)

{
  byte bVar1;
  byte bVar2;
  long lVar3;
  undefined *puVar4;
  long lVar5;
  undefined1 local_58;
  undefined7 uStack_57;
  char local_41;
  long local_38 [2];
  char local_21;
  
                    /* SET_VARC_STR_SMALL packet handler. Reads varc ID + string, then writes the
                       string into an InterfaceManager update entry (type=2). id-FIRST variant (vs
                       SET_VARC_STR_LARGE which is string-first). */
  lVar5 = *(long *)(packetPtr + 0x18);
  local_58 = 0;
  local_41 = '\x17';
  *(long *)(packetPtr + 0x18) = lVar5 + 2;
  bVar1 = *(byte *)(*(long *)(packetPtr + 0x10) + 1 + lVar5);
  bVar2 = *(byte *)(*(long *)(packetPtr + 0x10) + lVar5);
  FUN_00ad80e0(packetPtr,&local_58);
  puVar4 = game::ConfigProvider::GetVarType
                     (*(long *)((long)&__DT_RELA[0xca6].r_info + *thisPtr),DAT_013a3a40,
                      (uint)bVar1 * 0x100 + (uint)bVar2 & 0xffff);
  lVar5 = *(long *)(puVar4 + 8);
  lVar3 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar3 + 0x10) = *(int *)(lVar3 + 0x10) + 1;
  *(undefined1 *)(lVar3 + 0x14) = 1;
  if (lVar5 != 0) {
    lVar5 = InterfaceManager::CreateOrFindUpdateEntry(lVar3,2,*(undefined4 *)(lVar5 + 8));
    InterfaceManager::MarkUpdateEntryDirty(lVar3,lVar5);
    FUN_0048d480(local_38,&local_58);
    FUN_0022f4a0(lVar5 + 0x20,local_38);
    if ((local_21 < '\0') && (local_38[0] != 0)) {
      eastl__basic_string();
    }
  }
  if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    eastl__basic_string();
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SOUND_AREA` @ 001516d0
```c

undefined * jag::packethandlers::ZoneUpdates::SOUND_AREA(long *param_1,long param_2)

{
  byte *pbVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  long lVar7;
  long lVar8;
  undefined8 *puVar9;
  undefined4 uVar10;
  ushort uVar11;
  size_t sVar12;
  long *plVar13;
  uint uVar14;
  char cVar15;
  uint uVar16;
  char ****ppppcVar17;
  undefined8 extraout_RDX;
  long lVar18;
  ulong uVar19;
  byte bVar20;
  byte *__s;
  float fVar21;
  float fVar22;
  char ***local_168;
  long local_160;
  undefined8 local_158;
  char ***local_148;
  undefined1 local_140 [272];
  
  lVar18 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar18 + 2;
  bVar2 = *(byte *)(*(long *)(param_2 + 0x10) + 1 + lVar18);
  uVar11 = FUN_00121a30(param_2);
  lVar7 = *(long *)(param_2 + 0x18);
  lVar8 = *(long *)(param_2 + 0x10);
  local_168 = (char ***)local_140;
  lVar18 = lVar7 + 4;
  *(long *)(param_2 + 0x18) = lVar7 + 1;
  bVar3 = *(byte *)(lVar8 + lVar7);
  *(long *)(param_2 + 0x18) = lVar18;
  bVar4 = *(byte *)(lVar8 + 2 + lVar7);
  bVar5 = *(byte *)(lVar8 + 3 + lVar7);
  bVar6 = *(byte *)(lVar8 + 1 + lVar7);
  local_158 = -0x7fffffffffffff01;
  __s = (byte *)(lVar8 + lVar18);
  local_160 = 0;
  local_140[0] = 0;
  local_148 = local_168;
  sVar12 = strlen((char *)__s);
  if (sVar12 == 0) {
    cVar15 = -0x80;
    *(long *)(param_2 + 0x18) = lVar7 + 5;
  }
  else {
    cVar15 = -0x80;
    if ((int)sVar12 != 0) {
      pbVar1 = __s + (sVar12 & 0xffffffff);
      if ((sVar12 & 1) != 0) {
        uVar19 = (ulong)*__s;
        if ((byte)(*__s + 0x80) < 0x20) {
          uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
        }
        uVar14 = (uint)uVar19;
        if (uVar14 != 0) {
          if (uVar14 < 0x80) {
            FUN_00c29370(&local_168,(int)(char)(byte)uVar19);
          }
          else if (uVar14 < 0x800) {
            lVar18 = local_160;
            if (-1 < local_158) {
              lVar18 = 0x17 - (long)local_158._7_1_;
            }
            FUN_006b28b0(&local_168,lVar18 + 2);
            FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
            FUN_00c29370(&local_168,(int)(char)((byte)uVar19 & 0x3f | 0x80));
          }
          else {
            lVar18 = local_160;
            if (-1 < local_158) {
              lVar18 = 0x17 - (long)local_158._7_1_;
            }
            FUN_006b28b0(&local_168,lVar18 + 3);
            FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
            uVar16 = uVar14 >> 6 & 0x3f | 0xffffff80;
            FUN_00c29370(&local_168,(int)(char)uVar16,uVar16);
            uVar14 = uVar14 & 0x3f | 0xffffff80;
            FUN_00c29370(&local_168,(int)(char)uVar14,extraout_RDX,uVar14);
          }
        }
        __s = __s + 1;
        goto joined_r0x00151be9;
      }
      do {
        while( true ) {
          uVar19 = (ulong)*__s;
          if ((byte)(*__s + 0x80) < 0x20) {
            uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
          }
          uVar14 = (uint)uVar19;
          if (uVar14 != 0) {
            if (uVar14 < 0x80) {
              FUN_00c29370(&local_168,(int)(char)(byte)uVar19);
            }
            else {
              if (uVar14 < 0x800) {
                lVar18 = local_160;
                if (-1 < local_158) {
                  lVar18 = 0x17 - (long)local_158._7_1_;
                }
                FUN_006b28b0(&local_168,lVar18 + 2);
                FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
              }
              else {
                lVar18 = local_160;
                if (-1 < local_158) {
                  lVar18 = 0x17 - (long)local_158._7_1_;
                }
                FUN_006b28b0(&local_168,lVar18 + 3);
                FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
                FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 6) & 0x3f | 0x80));
              }
              FUN_00c29370(&local_168,(int)(char)((byte)uVar19 & 0x3f | 0x80));
            }
          }
          uVar19 = (ulong)__s[1];
          if ((byte)(__s[1] + 0x80) < 0x20) {
            uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
          }
          uVar14 = (uint)uVar19;
          if (uVar14 != 0) break;
LAB_00151ae0:
          __s = __s + 2;
          if (__s == pbVar1) goto LAB_00151aef;
        }
        bVar20 = (byte)uVar19;
        if (0x7f < uVar14) {
          if (uVar14 < 0x800) {
            lVar18 = local_160;
            if (-1 < local_158) {
              lVar18 = 0x17 - (long)local_158._7_1_;
            }
            FUN_006b28b0(&local_168,lVar18 + 2);
            FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
            FUN_00c29370(&local_168,(int)(char)(bVar20 & 0x3f | 0x80));
          }
          else {
            lVar18 = local_160;
            if (-1 < local_158) {
              lVar18 = 0x17 - (long)local_158._7_1_;
            }
            FUN_006b28b0(&local_168,lVar18 + 3);
            FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
            FUN_00c29370(&local_168,(int)(char)((byte)(uVar14 >> 6) & 0x3f | 0x80));
            FUN_00c29370(&local_168,(int)(char)(bVar20 & 0x3f | 0x80));
          }
          goto LAB_00151ae0;
        }
        FUN_00c29370(&local_168,(int)(char)bVar20);
        __s = __s + 2;
joined_r0x00151be9:
      } while (__s != pbVar1);
LAB_00151aef:
      lVar18 = *(long *)(param_2 + 0x18);
      cVar15 = local_158._7_1_;
    }
    *(size_t *)(param_2 + 0x18) = lVar18 + 1 + sVar12;
  }
  uVar10 = DAT_013942a8;
  lVar18 = *(long *)((long)&__DT_RELA[0xcf6].r_offset + *param_1);
  fVar22 = (float)((bVar2 & 7) + DAT_013942b0) * DAT_00cb6ae4;
  fVar21 = (float)((bVar2 >> 4 & 7) + DAT_013942ac) * DAT_00cb6ae4;
  ppppcVar17 = &local_168;
  if (cVar15 < '\0') {
    ppppcVar17 = (char ****)local_168;
  }
  plVar13 = (long *)FUN_0012b820(lVar18 + 0x20);
  *(undefined1 *)(plVar13 + 2) = 0;
  *(undefined1 *)((long)plVar13 + 0x27) = 0x17;
  cVar15 = *(char *)ppppcVar17;
  while (cVar15 != '\0') {
    ppppcVar17 = (char ****)((long)ppppcVar17 + 1);
    cVar15 = *(char *)ppppcVar17;
  }
  FUN_0048d690(plVar13 + 2);
  *(int *)((long)plVar13 + 0x3c) =
       (int)CONCAT71((uint7)(uint3)((uint3)bVar6 * 0x10000 + (uint3)bVar4 * 0x100 + (uint3)bVar5),
                     0xff);
  lVar7 = *(long *)(lVar18 + 0x18);
  *plVar13 = lVar18 + 0x10;
  plVar13[1] = lVar7;
  puVar9 = *(undefined8 **)(lVar18 + 0x18);
  *(float *)((long)plVar13 + 0x2c) = fVar21;
  *(undefined4 *)(plVar13 + 5) = uVar10;
  *(float *)(plVar13 + 6) = (float)bVar3;
  *(uint *)(plVar13 + 7) = (uint)uVar11;
  *(float *)((long)plVar13 + 0x34) = fVar22;
  *puVar9 = plVar13;
  *(long *)(lVar18 + 0x50) = *(long *)(lVar18 + 0x50) + 1;
  *(long **)(lVar18 + 0x18) = plVar13;
  if (local_158 < 0) {
    FUN_00aa7d70(&local_168);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SOUND_AREA` @ 00151d90
```c

undefined * jag::packethandlers::ZoneUpdates::SOUND_AREA(long *param_1,long param_2)

{
  byte *pbVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  long lVar7;
  long lVar8;
  undefined8 *puVar9;
  undefined4 uVar10;
  ushort uVar11;
  size_t sVar12;
  long *plVar13;
  uint uVar14;
  char cVar15;
  uint uVar16;
  char ****ppppcVar17;
  undefined8 extraout_RDX;
  long lVar18;
  ulong uVar19;
  byte bVar20;
  byte *__s;
  float fVar21;
  float fVar22;
  char ***pppcStack_168;
  long lStack_160;
  undefined8 uStack_158;
  char ***pppcStack_148;
  undefined1 auStack_140 [272];
  
  lVar18 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar18 + 2;
  bVar2 = *(byte *)(*(long *)(param_2 + 0x10) + 1 + lVar18);
  uVar11 = FUN_00121a30(param_2);
  lVar7 = *(long *)(param_2 + 0x18);
  lVar8 = *(long *)(param_2 + 0x10);
  pppcStack_168 = (char ***)auStack_140;
  lVar18 = lVar7 + 4;
  *(long *)(param_2 + 0x18) = lVar7 + 1;
  bVar3 = *(byte *)(lVar8 + lVar7);
  *(long *)(param_2 + 0x18) = lVar18;
  bVar4 = *(byte *)(lVar8 + 2 + lVar7);
  bVar5 = *(byte *)(lVar8 + 3 + lVar7);
  bVar6 = *(byte *)(lVar8 + 1 + lVar7);
  uStack_158 = -0x7fffffffffffff01;
  __s = (byte *)(lVar8 + lVar18);
  lStack_160 = 0;
  auStack_140[0] = 0;
  pppcStack_148 = pppcStack_168;
  sVar12 = strlen((char *)__s);
  if (sVar12 == 0) {
    cVar15 = -0x80;
    *(long *)(param_2 + 0x18) = lVar7 + 5;
  }
  else {
    cVar15 = -0x80;
    if ((int)sVar12 != 0) {
      pbVar1 = __s + (sVar12 & 0xffffffff);
      if ((sVar12 & 1) != 0) {
        uVar19 = (ulong)*__s;
        if ((byte)(*__s + 0x80) < 0x20) {
          uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
        }
        uVar14 = (uint)uVar19;
        if (uVar14 != 0) {
          if (uVar14 < 0x80) {
            FUN_00c29370(&pppcStack_168,(int)(char)(byte)uVar19);
          }
          else if (uVar14 < 0x800) {
            lVar18 = lStack_160;
            if (-1 < uStack_158) {
              lVar18 = 0x17 - (long)uStack_158._7_1_;
            }
            FUN_006b28b0(&pppcStack_168,lVar18 + 2);
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)uVar19 & 0x3f | 0x80));
          }
          else {
            lVar18 = lStack_160;
            if (-1 < uStack_158) {
              lVar18 = 0x17 - (long)uStack_158._7_1_;
            }
            FUN_006b28b0(&pppcStack_168,lVar18 + 3);
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
            uVar16 = uVar14 >> 6 & 0x3f | 0xffffff80;
            FUN_00c29370(&pppcStack_168,(int)(char)uVar16,uVar16);
            uVar14 = uVar14 & 0x3f | 0xffffff80;
            FUN_00c29370(&pppcStack_168,(int)(char)uVar14,extraout_RDX,uVar14);
          }
        }
        __s = __s + 1;
        goto joined_r0x00151be9;
      }
      do {
        while( true ) {
          uVar19 = (ulong)*__s;
          if ((byte)(*__s + 0x80) < 0x20) {
            uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
          }
          uVar14 = (uint)uVar19;
          if (uVar14 != 0) {
            if (uVar14 < 0x80) {
              FUN_00c29370(&pppcStack_168,(int)(char)(byte)uVar19);
            }
            else {
              if (uVar14 < 0x800) {
                lVar18 = lStack_160;
                if (-1 < uStack_158) {
                  lVar18 = 0x17 - (long)uStack_158._7_1_;
                }
                FUN_006b28b0(&pppcStack_168,lVar18 + 2);
                FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
              }
              else {
                lVar18 = lStack_160;
                if (-1 < uStack_158) {
                  lVar18 = 0x17 - (long)uStack_158._7_1_;
                }
                FUN_006b28b0(&pppcStack_168,lVar18 + 3);
                FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
                FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 6) & 0x3f | 0x80));
              }
              FUN_00c29370(&pppcStack_168,(int)(char)((byte)uVar19 & 0x3f | 0x80));
            }
          }
          uVar19 = (ulong)__s[1];
          if ((byte)(__s[1] + 0x80) < 0x20) {
            uVar19 = (ulong)*(ushort *)(&DAT_0102a600 + uVar19 * 2);
          }
          uVar14 = (uint)uVar19;
          if (uVar14 != 0) break;
LAB_00151ae0:
          __s = __s + 2;
          if (__s == pbVar1) goto LAB_00151aef;
        }
        bVar20 = (byte)uVar19;
        if (0x7f < uVar14) {
          if (uVar14 < 0x800) {
            lVar18 = lStack_160;
            if (-1 < uStack_158) {
              lVar18 = 0x17 - (long)uStack_158._7_1_;
            }
            FUN_006b28b0(&pppcStack_168,lVar18 + 2);
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 6) | 0xc0));
            FUN_00c29370(&pppcStack_168,(int)(char)(bVar20 & 0x3f | 0x80));
          }
          else {
            lVar18 = lStack_160;
            if (-1 < uStack_158) {
              lVar18 = 0x17 - (long)uStack_158._7_1_;
            }
            FUN_006b28b0(&pppcStack_168,lVar18 + 3);
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 0xc) | 0xe0));
            FUN_00c29370(&pppcStack_168,(int)(char)((byte)(uVar14 >> 6) & 0x3f | 0x80));
            FUN_00c29370(&pppcStack_168,(int)(char)(bVar20 & 0x3f | 0x80));
          }
          goto LAB_00151ae0;
        }
        FUN_00c29370(&pppcStack_168,(int)(char)bVar20);
        __s = __s + 2;
joined_r0x00151be9:
      } while (__s != pbVar1);
LAB_00151aef:
      lVar18 = *(long *)(param_2 + 0x18);
      cVar15 = uStack_158._7_1_;
    }
    *(size_t *)(param_2 + 0x18) = lVar18 + 1 + sVar12;
  }
  uVar10 = DAT_013942a8;
  lVar18 = *(long *)((long)&__DT_RELA[0xcf6].r_offset + *param_1);
  fVar22 = (float)((bVar2 & 7) + DAT_013942b0) * DAT_00cb6ae4;
  fVar21 = (float)((bVar2 >> 4 & 7) + DAT_013942ac) * DAT_00cb6ae4;
  ppppcVar17 = &pppcStack_168;
  if (cVar15 < '\0') {
    ppppcVar17 = (char ****)pppcStack_168;
  }
  plVar13 = (long *)FUN_0012b820(lVar18 + 0x20);
  *(undefined1 *)(plVar13 + 2) = 0;
  *(undefined1 *)((long)plVar13 + 0x27) = 0x17;
  cVar15 = *(char *)ppppcVar17;
  while (cVar15 != '\0') {
    ppppcVar17 = (char ****)((long)ppppcVar17 + 1);
    cVar15 = *(char *)ppppcVar17;
  }
  FUN_0048d690(plVar13 + 2);
  *(int *)((long)plVar13 + 0x3c) =
       (int)CONCAT71((uint7)(uint3)((uint3)bVar6 * 0x10000 + (uint3)bVar4 * 0x100 + (uint3)bVar5),
                     0xff);
  lVar7 = *(long *)(lVar18 + 0x18);
  *plVar13 = lVar18 + 0x10;
  plVar13[1] = lVar7;
  puVar9 = *(undefined8 **)(lVar18 + 0x18);
  *(float *)((long)plVar13 + 0x2c) = fVar21;
  *(undefined4 *)(plVar13 + 5) = uVar10;
  *(float *)(plVar13 + 6) = (float)bVar3;
  *(uint *)(plVar13 + 7) = (uint)uVar11;
  *(float *)((long)plVar13 + 0x34) = fVar22;
  *puVar9 = plVar13;
  *(long *)(lVar18 + 0x50) = *(long *)(lVar18 + 0x50) + 1;
  *(long **)(lVar18 + 0x18) = plVar13;
  if (uStack_158 < 0) {
    FUN_00aa7d70(&pppcStack_168);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::OBJ_DEL` @ 00152c80
```c

/* Setting prototype: undefined * OBJ_DEL(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::ZoneUpdates::OBJ_DEL(long *thisPtr,long packetPtr)

{
  undefined8 *puVar1;
  byte bVar2;
  long lVar3;
  long *plVar4;
  uint *puVar5;
  long lVar6;
  undefined8 uVar7;
  long lVar8;
  int iVar9;
  ulong uVar10;
  uint *puVar11;
  uint *puVar12;
  undefined8 *puVar13;
  uint uVar14;
  uint *puVar15;
  ushort uVar16;
  undefined8 *puVar17;
  undefined8 *puVar18;
  uint uVar19;
  long lVar20;
  bool bVar21;
  
                    /* OBJ_DEL packet handler. Tree-iterates the zone ObjStack list and removes the
                       matching obj-stack entry. */
  lVar3 = *(long *)(packetPtr + 0x18);
  bVar21 = DAT_01050dc0 == 0x3020100;
  *(long *)(packetPtr + 0x18) = lVar3 + 2;
  iVar9 = DAT_013942a8;
  uVar16 = *(ushort *)(*(long *)(packetPtr + 0x10) + lVar3);
  if (bVar21) {
    uVar16 = uVar16 << 8 | uVar16 >> 8;
  }
  lVar20 = *thisPtr;
  *(long *)(packetPtr + 0x18) = lVar3 + 3;
  bVar2 = *(byte *)(*(long *)(packetPtr + 0x10) + 2 + lVar3);
  lVar3 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + lVar20);
  puVar17 = *(undefined8 **)(lVar3 + 0x18);
  uVar14 = (bVar2 & 7) + DAT_013942b0;
  puVar1 = (undefined8 *)(lVar3 + 8);
  uVar19 = (bVar2 >> 4 & 7) + DAT_013942ac;
  puVar13 = puVar1;
  if (puVar17 != (undefined8 *)0x0) {
    do {
      if ((*(int *)(puVar17 + 4) < iVar9) ||
         ((*(int *)(puVar17 + 4) <= iVar9 &&
          ((*(uint *)((long)puVar17 + 0x24) < uVar19 ||
           ((*(uint *)((long)puVar17 + 0x24) <= uVar19 && (*(uint *)(puVar17 + 5) < uVar14)))))))) {
        puVar18 = (undefined8 *)*puVar17;
      }
      else {
        puVar18 = (undefined8 *)puVar17[1];
        puVar13 = puVar17;
      }
      puVar17 = puVar18;
    } while (puVar18 != (undefined8 *)0x0);
    if (((puVar1 != puVar13) && (*(int *)(puVar13 + 4) <= iVar9)) &&
       ((*(int *)(puVar13 + 4) < iVar9 ||
        ((*(uint *)((long)puVar13 + 0x24) <= uVar19 &&
         ((uVar19 != *(uint *)((long)puVar13 + 0x24) || (*(uint *)(puVar13 + 5) <= uVar14)))))))) {
      plVar4 = (long *)puVar13[7];
      puVar15 = (uint *)plVar4[0xf];
      puVar5 = (uint *)plVar4[0xe];
      if (puVar15 != puVar5) {
        puVar12 = puVar5;
        if (((uint)uVar16 != *puVar5) &&
           ((uVar10 = (ulong)((long)puVar15 + (-0x90 - (long)puVar5)) >> 4,
            uVar14 = (uint)uVar10 & 7, puVar11 = puVar5, (uVar10 & 7) == 0 ||
            ((puVar11 = puVar5 + 0x24, puVar12 = puVar11, (uint)uVar16 != *puVar11 &&
             ((uVar14 == 1 ||
              (((uVar19 = (uint)uVar16, uVar14 == 2 ||
                (((uVar14 == 3 ||
                  (((uVar14 == 4 ||
                    (((uVar14 == 5 ||
                      (((uVar14 == 6 ||
                        (puVar11 = puVar5 + 0x48, puVar12 = puVar11, uVar19 != *puVar11)) &&
                       (puVar11 = puVar11 + 0x24, puVar12 = puVar11, uVar19 != *puVar11)))) &&
                     (puVar11 = puVar11 + 0x24, puVar12 = puVar11, uVar19 != *puVar11)))) &&
                   (puVar11 = puVar11 + 0x24, puVar12 = puVar11, uVar19 != *puVar11)))) &&
                 (puVar11 = puVar11 + 0x24, puVar12 = puVar11, uVar19 != *puVar11)))) &&
               (puVar11 = puVar11 + 0x24, puVar12 = puVar11, uVar19 != *puVar11)))))))))) {
          do {
            puVar12 = puVar11 + 0x24;
            if (puVar15 == puVar12) goto LAB_00153062;
            uVar14 = (uint)uVar16;
          } while ((((uVar14 != *puVar12) && (puVar12 = puVar11 + 0x48, uVar14 != puVar11[0x48])) &&
                   ((puVar12 = puVar11 + 0x6c, uVar14 != puVar11[0x6c] &&
                    ((puVar12 = puVar11 + 0x90, uVar14 != puVar11[0x90] &&
                     (puVar12 = puVar11 + 0xb4, uVar14 != puVar11[0xb4])))))) &&
                  ((puVar12 = puVar11 + 0xd8, uVar14 != puVar11[0xd8] &&
                   ((puVar12 = puVar11 + 0xfc, uVar14 != puVar11[0xfc] &&
                    (puVar5 = puVar11 + 0x120, puVar11 = puVar11 + 0x120, puVar12 = puVar11,
                    uVar14 != *puVar5))))));
        }
        puVar12 = puVar12 + 0x24;
        if ((puVar12 < puVar15) &&
           (lVar20 = ((long)puVar15 - (long)puVar12 >> 4) * -0x71c71c71c71c71c7,
           0 < (long)puVar15 - (long)puVar12)) {
          do {
            lVar6 = *(long *)(puVar12 + 2);
            uVar7 = *(undefined8 *)(puVar12 + 4);
            puVar12[-0x24] = *puVar12;
            puVar12[-0x23] = puVar12[1];
            if (lVar6 != 0) {
              LOCK();
              *(int *)(lVar6 + 8) = *(int *)(lVar6 + 8) + 1;
              UNLOCK();
            }
            lVar8 = *(long *)(puVar12 + -0x22);
            *(undefined8 *)(puVar12 + -0x20) = uVar7;
            *(long *)(puVar12 + -0x22) = lVar6;
            if (lVar8 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = *(long *)(puVar12 + 6);
            uVar7 = *(undefined8 *)(puVar12 + 8);
            if (lVar6 != 0) {
              LOCK();
              *(int *)(lVar6 + 8) = *(int *)(lVar6 + 8) + 1;
              UNLOCK();
            }
            lVar8 = *(long *)(puVar12 + -0x1e);
            *(undefined8 *)(puVar12 + -0x1c) = uVar7;
            *(long *)(puVar12 + -0x1e) = lVar6;
            if (lVar8 != 0) {
              ref_counter_base::DecRef();
            }
            lVar6 = *(long *)(puVar12 + 10);
            uVar7 = *(undefined8 *)(puVar12 + 0xc);
            if (lVar6 != 0) {
              LOCK();
              *(int *)(lVar6 + 8) = *(int *)(lVar6 + 8) + 1;
              UNLOCK();
            }
            lVar8 = *(long *)(puVar12 + -0x1a);
            *(undefined8 *)(puVar12 + -0x18) = uVar7;
            *(long *)(puVar12 + -0x1a) = lVar6;
            if (lVar8 != 0) {
              ref_counter_base::DecRef();
            }
            puVar12[-4] = puVar12[0x20];
            *(undefined8 *)(puVar12 + -0x14) = *(undefined8 *)(puVar12 + 0x10);
            *(undefined8 *)(puVar12 + -0x12) = *(undefined8 *)(puVar12 + 0x12);
            *(undefined8 *)(puVar12 + -0x10) = *(undefined8 *)(puVar12 + 0x14);
            *(undefined8 *)(puVar12 + -0xe) = *(undefined8 *)(puVar12 + 0x16);
            *(undefined8 *)(puVar12 + -0xc) = *(undefined8 *)(puVar12 + 0x18);
            *(undefined8 *)(puVar12 + -10) = *(undefined8 *)(puVar12 + 0x1a);
            *(undefined8 *)(puVar12 + -8) = *(undefined8 *)(puVar12 + 0x1c);
            *(undefined8 *)(puVar12 + -6) = *(undefined8 *)(puVar12 + 0x1e);
            lVar20 = lVar20 + -1;
            puVar12 = puVar12 + 0x24;
          } while (lVar20 != 0);
          puVar15 = (uint *)plVar4[0xf];
        }
        plVar4[0xf] = (long)(puVar15 + -0x24);
        FUN_0060bc40();
        FUN_0062cd40(plVar4);
        if (plVar4[0xf] != plVar4[0xe]) goto LAB_00153062;
      }
      (**(code **)(*plVar4 + 0xe0))(plVar4);
      *(long *)(lVar3 + 0x28) = *(long *)(lVar3 + 0x28) + -1;
      eastl::rbtree::next(puVar13);
      FUN_00aa4780(puVar13,puVar1);
      if (puVar13[6] != 0) {
        ref_counter_base::DecRef();
      }
      HeapInterface::Free(puVar13);
    }
  }
LAB_00153062:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY` @ 00157200
```c

undefined * jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY(long *param_1,long param_2)

{
  undefined1 uVar1;
  char cVar2;
  byte bVar3;
  long lVar4;
  long lVar5;
  long lVar6;
  long *plVar7;
  undefined1 auVar8 [16];
  int iVar9;
  char cVar10;
  uint uVar11;
  long *plVar12;
  long *plVar13;
  int iVar14;
  uint uVar15;
  long *plVar16;
  int iVar17;
  undefined *puVar18;
  int iVar19;
  ushort uVar20;
  int iVar21;
  byte bVar22;
  byte bVar23;
  byte bVar24;
  float fVar25;
  int local_a8;
  int local_a4;
  uint local_a0;
  uint local_9c;
  int local_98;
  int local_94;
  int local_90;
  undefined4 uStack_8c;
  undefined4 local_88;
  undefined1 local_84;
  float local_80;
  int local_7c;
  byte local_78;
  undefined1 local_68 [16];
  undefined1 local_58 [16];
  undefined8 local_48;
  
  uVar11 = Packet::g4_alt3(param_2);
  lVar4 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar4 + 2;
  bVar23 = *(byte *)(lVar5 + 1 + lVar4);
  bVar24 = *(byte *)(lVar5 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 3;
  cVar10 = *(char *)(lVar5 + 2 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 4;
  uVar1 = *(undefined1 *)(lVar5 + 3 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 6;
  cVar2 = *(char *)(lVar5 + 5 + lVar4);
  bVar3 = *(byte *)(lVar5 + 4 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 8;
  bVar22 = -cVar10;
  iVar21 = (int)(short)((ushort)bVar23 * 0x100 + (ushort)bVar24);
  uVar20 = *(ushort *)(lVar5 + 6 + lVar4);
  iVar14 = (uint)bVar3 * 0x100 + (uint)(byte)(cVar2 + 0x80);
  if (DAT_01050dc0 == 0x3020100) {
    uVar20 = uVar20 << 8 | uVar20 >> 8;
  }
  uVar15 = (uint)uVar20;
  if (uVar15 == 0xffff) {
    uVar15 = 0xffffffff;
  }
  bVar23 = bVar22 >> 7;
  fVar25 = (float)(bVar22 & 7) * DAT_00cb6b3c;
  bVar24 = (byte)((uint)iVar14 >> 8) >> 7;
  if (uVar11 >> 0x1e == 0) {
    if (uVar11 >> 0x1d == 0) {
      lVar4 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + *param_1) + 0x10) +
                       (ulong)(uVar11 & 0xffff) * 8);
      plVar12 = (long *)(lVar4 + 0x30);
      if (lVar4 == 0) {
        plVar12 = &DAT_013962b0;
      }
      lVar4 = *plVar12;
      lVar5 = plVar12[1];
    }
    else {
      plVar12 = (long *)NPCList::GetNPCNode(*(undefined8 *)
                                             ((long)&__DT_RELA[0xcfb].r_addend + *param_1));
      lVar4 = *plVar12;
      lVar5 = plVar12[1];
    }
    if (lVar4 != 0) {
      LOCK();
      *(int *)(lVar4 + 8) = *(int *)(lVar4 + 8) + 1;
      UNLOCK();
    }
    if (lVar5 != 0) {
      lVar6 = *param_1;
      local_90 = 0;
      uStack_8c = 0;
      local_98 = 0;
      local_88 = 0;
      local_7c = *(undefined4 *)(lVar5 + 0x40);
      local_84 = 0;
      local_48 = 0;
      auVar8[8] = bVar23;
      auVar8._0_8_ = CONCAT44(iVar14,uVar15) & 0x7fffffffffff;
      auVar8._9_7_ = 0;
      local_68 = auVar8 << 0x20;
      local_58._4_4_ =
           *(undefined4 *)
            ((long)&__DT_RELA[0x548].r_addend +
            *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar6) + 0x2f0));
      local_58._0_4_ = *(undefined4 *)(lVar6 + 0x500);
      local_58._8_8_ = 0;
      local_94 = iVar21;
      local_80 = fVar25;
      local_78 = bVar24;
      FUN_00155a80(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar6),uVar1,lVar5,&local_98,
                   local_68);
    }
    if (lVar4 != 0) {
      ref_counter_base::DecRef();
    }
  }
  else {
    game::BuildArea::DecodePackedCoord(&local_a4,uVar11);
    iVar9 = local_a4;
    puVar18 = &DAT_015df2b0;
    lVar4 = *(long *)((long)&__DT_RELA[0xd02].r_info + *param_1);
    iVar17 = *(int *)(lVar4 + 0x70);
    if (iVar17 != -1) {
      puVar18 = (undefined *)((long)iVar17 * 0x10 + *(long *)(lVar4 + 0x58));
    }
    lVar4 = *(long *)(puVar18 + 8);
    if (lVar4 != 0) {
      cVar10 = (**(code **)(*(long *)((long)&__DT_RELA[0x977].r_offset + lVar4) + 0x28))
                         (lVar4 + 0x14048,local_a0,local_9c);
      if (cVar10 != '\0') {
        local_a4 = local_a4 + (uint)(local_a4 < 3);
      }
      iVar17 = (int)((float)local_9c * DAT_00cb6ae4 + DAT_00cb6b2c);
      iVar19 = (int)((float)local_a0 * DAT_00cb6ae4 + DAT_00cb6b2c);
      game::HeightMap::GetFineHeight
                (*(long *)(puVar18 + 8) + 0x14038,&local_a8,local_a4,iVar19,iVar17,1,0);
      if (uVar15 == 0xffffffff) {
        lVar4 = *(long *)((long)&__DT_RELA[0xcff].r_info + *param_1);
        if ((long *)(lVar4 + 0x10) != *(long **)(lVar4 + 0x10)) {
          plVar12 = *(long **)(lVar4 + 0x10);
          do {
            plVar7 = (long *)*plVar12;
            if (((*(char *)((long)plVar12 + 0x192) != '\0') && (iVar19 == (int)plVar12[0x11])) &&
               (iVar17 == (int)plVar12[0x12])) {
              plVar13 = plVar7;
              if ((*(int *)((long)plVar12 + 0xac) == 1) &&
                 (plVar16 = *(long **)(lVar4 + 0x88da0),
                 (long *)(lVar4 + 0x88da0) != *(long **)(lVar4 + 0x88da0))) {
                do {
                  plVar13 = (long *)*plVar16;
                  if (plVar12 + 2 == (long *)plVar16[2]) {
                    plVar13[1] = plVar16[1];
                    *(long **)plVar16[1] = plVar13;
                    FUN_0047fd60(lVar4 + 0x88db0);
                    *(long *)(lVar4 + 0x88de0) = *(long *)(lVar4 + 0x88de0) + -1;
                  }
                  plVar16 = plVar13;
                } while ((long *)(lVar4 + 0x88da0) != plVar13);
                plVar13 = (long *)*plVar12;
              }
              *(long *)((long)plVar13 + 8) = plVar12[1];
              *(long **)plVar12[1] = plVar13;
              FUN_00454480(plVar12 + 2);
              FUN_0047fd60(lVar4 + 0x20,plVar12);
              *(long *)(lVar4 + 0x50) = *(long *)(lVar4 + 0x50) + -1;
            }
            plVar12 = plVar7;
          } while ((long *)(lVar4 + 0x10) != plVar7);
        }
      }
      else {
        local_94 = iVar21 + local_a8;
        lVar4 = *param_1;
        uStack_8c = 0;
        local_88 = 0;
        local_84 = 0;
        local_7c = iVar9;
        local_48 = 0;
        local_68._4_5_ = CONCAT14(bVar23,iVar14) & 0xff00007fff;
        local_68._0_4_ = uVar15;
        local_68._9_7_ = 0;
        local_68 = local_68 << 0x20;
        local_58._4_4_ =
             *(undefined4 *)
              ((long)&__DT_RELA[0x548].r_addend +
              *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar4) + 0x2f0));
        local_58._0_4_ = *(undefined4 *)(lVar4 + 0x500);
        local_58._8_8_ = 0;
        local_98 = iVar19;
        local_90 = iVar17;
        local_80 = fVar25;
        local_78 = bVar24;
        FUN_00156f20(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar4),&local_98,local_68);
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_ANIM_SPECIFIC` @ 00157740
```c

undefined * jag::packethandlers::ZoneUpdates::MAP_ANIM_SPECIFIC(long *param_1,long param_2)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  long *plVar8;
  long *plVar9;
  int iVar10;
  uint uVar11;
  undefined *puVar12;
  ushort *puVar13;
  uint uVar14;
  int iVar15;
  uint uVar16;
  long *plVar17;
  long lVar18;
  ushort uVar19;
  uint uVar20;
  int iVar21;
  long *plVar22;
  ushort uVar23;
  int local_9c;
  int local_98;
  int local_94;
  int local_90;
  int local_8c;
  int local_88;
  undefined1 local_84;
  float local_80;
  int local_7c;
  byte local_78;
  undefined1 local_68 [16];
  undefined1 local_58 [16];
  undefined8 local_48;
  
  lVar6 = *(long *)(param_2 + 0x18);
  lVar7 = *(long *)(param_2 + 0x10);
  lVar18 = lVar6 + 5;
  *(long *)(param_2 + 0x18) = lVar6 + 1;
  bVar1 = *(byte *)(lVar7 + lVar6);
  *(long *)(param_2 + 0x18) = lVar6 + 3;
  puVar13 = (ushort *)(lVar6 + 3 + lVar7);
  uVar19 = *(ushort *)(lVar7 + 1 + lVar6);
  uVar20 = (uint)uVar19;
  if (DAT_01050dc0 == 0x3020100) {
    uVar19 = uVar19 << 8 | uVar19 >> 8;
    uVar20 = (uint)uVar19;
    if (uVar19 == 0xffff) {
      *(long *)(param_2 + 0x18) = lVar18;
      uVar20 = 0xffffffff;
      uVar19 = *puVar13;
    }
    else {
      *(long *)(param_2 + 0x18) = lVar18;
      uVar19 = *puVar13;
    }
    uVar23 = uVar19 << 8 | uVar19 >> 8;
    *(long *)(param_2 + 0x18) = lVar6 + 7;
    uVar19 = *(ushort *)(lVar7 + 5 + lVar6);
    uVar19 = uVar19 << 8 | uVar19 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar18;
    uVar23 = *puVar13;
    if (uVar19 == 0xffff) {
      uVar20 = 0xffffffff;
    }
    *(long *)(param_2 + 0x18) = lVar6 + 7;
    uVar19 = *(ushort *)(lVar7 + 5 + lVar6);
  }
  iVar10 = DAT_013942ac;
  iVar21 = DAT_013942a8;
  lVar18 = lVar6 + 7;
  *(long *)(param_2 + 0x18) = lVar6 + 8;
  bVar2 = *(byte *)(lVar7 + lVar18);
  *(long *)(param_2 + 0x18) = lVar6 + 0xe;
  puVar12 = &DAT_015df2b0;
  bVar3 = *(byte *)(lVar7 + 4 + lVar18);
  bVar4 = *(byte *)(lVar7 + 5 + lVar18);
  bVar5 = *(byte *)(lVar7 + 6 + lVar18);
  lVar18 = *(long *)((long)&__DT_RELA[0xd02].r_info + *param_1);
  iVar15 = *(int *)(lVar18 + 0x70);
  if (iVar15 != -1) {
    puVar12 = (undefined *)((long)iVar15 * 0x10 + *(long *)(lVar18 + 0x58));
  }
  lVar18 = *(long *)(puVar12 + 8);
  if (lVar18 != 0) {
    uVar14 = (bVar1 & 7) + DAT_013942b0;
    uVar16 = (bVar1 >> 4 & 7) + iVar10;
    uVar11 = game::LinkMap::GetTileLink(lVar18 + 0x14048,1,uVar16,uVar14);
    if (uVar11 != 0xffffffff) {
      iVar21 = iVar21 + (uVar11 >> 1 & (uint)(iVar21 < 3));
    }
    game::HeightMap::GetFineHeight
              (lVar18 + 0x14038,&local_9c,iVar21,uVar16 * 0x200,uVar14 * 0x200,1,1);
    iVar21 = (int)((float)uVar16 * DAT_00cb6ae4 + DAT_00cb6b2c);
    iVar15 = (int)((float)uVar14 * DAT_00cb6ae4 + DAT_00cb6b2c);
    if (uVar20 == 0xffffffff) {
      lVar18 = *(long *)((long)&__DT_RELA[0xcff].r_info + *param_1);
      plVar8 = *(long **)(lVar18 + 0x10);
      while (plVar9 = plVar8, (long *)(lVar18 + 0x10) != plVar9) {
        plVar8 = (long *)*plVar9;
        if (((*(char *)((long)plVar9 + 0x192) != '\0') && ((int)plVar9[0x11] == iVar21)) &&
           ((int)plVar9[0x12] == iVar15)) {
          plVar22 = plVar8;
          if ((*(int *)((long)plVar9 + 0xac) == 1) &&
             (plVar17 = *(long **)(lVar18 + 0x88da0),
             (long *)(lVar18 + 0x88da0) != *(long **)(lVar18 + 0x88da0))) {
            do {
              plVar22 = (long *)*plVar17;
              if (plVar9 + 2 == (long *)plVar17[2]) {
                plVar22[1] = plVar17[1];
                *(long **)plVar17[1] = plVar22;
                FUN_0047fd60(lVar18 + 0x88db0);
                *(long *)(lVar18 + 0x88de0) = *(long *)(lVar18 + 0x88de0) + -1;
              }
              plVar17 = plVar22;
            } while ((long *)(lVar18 + 0x88da0) != plVar22);
            plVar22 = (long *)*plVar9;
          }
          plVar22[1] = plVar9[1];
          *(long **)plVar9[1] = plVar22;
          FUN_00454480(plVar9 + 2);
          if ((plVar9 < *(long **)(lVar18 + 0x48)) || (*(long **)(lVar18 + 0x30) <= plVar9)) {
            HeapInterface::Free(plVar9);
          }
          else {
            *plVar9 = *(long *)(lVar18 + 0x20);
            *(long **)(lVar18 + 0x20) = plVar9;
          }
          *(long *)(lVar18 + 0x50) = *(long *)(lVar18 + 0x50) + -1;
        }
      }
    }
    else {
      uVar11 = (uint)bVar5 + (uint)bVar3 * 0x10000 + (uint)bVar4 * 0x100;
      local_88 = ((int)uVar11 >> 0xb & 0x7ffU) - 0x3ff;
      local_8c = (uVar11 & 0x7ff) - 0x3ff;
      local_84 = (int)uVar11 >> 0x16 == 1;
      local_7c = DAT_013942a8;
      lVar18 = *param_1;
      local_78 = (byte)(uVar19 >> 0xf);
      local_48 = 0;
      local_68._8_8_ = 0;
      local_68._0_8_ = (ulong)CONCAT24(uVar19,uVar20) & 0x7fffffffffff;
      local_68 = local_68 << 0x20;
      local_94 = (int)((float)(int)(short)uVar23 + (float)local_9c);
      local_80 = (float)bVar2 * DAT_00cb6b3c;
      local_58._4_4_ =
           *(undefined4 *)
            ((long)&__DT_RELA[0x548].r_addend +
            *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar18) + 0x2f0));
      local_58._0_4_ = *(undefined4 *)(lVar18 + 0x500);
      local_58._8_8_ = 0;
      local_98 = iVar21;
      local_90 = iVar15;
      FUN_00156f20(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar18),&local_98,local_68);
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::MAP_ANIM` @ 00157bd0
```c

undefined * jag::packethandlers::ZoneUpdates::MAP_ANIM(long *param_1,long param_2)

{
  long lVar1;
  byte bVar2;
  byte bVar3;
  long lVar4;
  long lVar5;
  long *plVar6;
  long *plVar7;
  int iVar8;
  uint uVar9;
  undefined *puVar10;
  long *plVar11;
  uint uVar12;
  uint uVar13;
  int iVar14;
  ushort *puVar15;
  long *plVar16;
  ushort uVar17;
  int iVar18;
  ushort uVar19;
  uint uVar20;
  int local_9c;
  int local_98;
  int local_94;
  int local_90;
  undefined8 local_8c;
  undefined1 local_84;
  float local_80;
  int local_7c;
  byte local_78;
  undefined1 local_68 [16];
  undefined1 local_58 [16];
  undefined8 local_48;
  
  lVar4 = *(long *)(param_2 + 0x18);
  lVar5 = *(long *)(param_2 + 0x10);
  lVar1 = lVar4 + 5;
  *(long *)(param_2 + 0x18) = lVar4 + 1;
  bVar2 = *(byte *)(lVar5 + lVar4);
  *(long *)(param_2 + 0x18) = lVar4 + 3;
  puVar15 = (ushort *)(lVar4 + 3 + lVar5);
  uVar17 = *(ushort *)(lVar5 + 1 + lVar4);
  if (DAT_01050dc0 == 0x3020100) {
    uVar17 = uVar17 << 8 | uVar17 >> 8;
    uVar20 = (uint)uVar17;
    if (uVar17 == 0xffff) {
      *(long *)(param_2 + 0x18) = lVar1;
      uVar20 = 0xffffffff;
      uVar17 = *puVar15;
    }
    else {
      *(long *)(param_2 + 0x18) = lVar1;
      uVar17 = *puVar15;
    }
    uVar19 = uVar17 << 8 | uVar17 >> 8;
    *(long *)(param_2 + 0x18) = lVar4 + 7;
    uVar17 = *(ushort *)(lVar5 + 5 + lVar4);
    uVar17 = uVar17 << 8 | uVar17 >> 8;
  }
  else {
    uVar20 = (uint)uVar17;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar19 = *puVar15;
    if (uVar17 == 0xffff) {
      uVar20 = 0xffffffff;
    }
    *(long *)(param_2 + 0x18) = lVar4 + 7;
    uVar17 = *(ushort *)(lVar5 + 5 + lVar4);
  }
  iVar8 = DAT_013942b0;
  iVar18 = DAT_013942a8;
  lVar1 = *param_1;
  *(long *)(param_2 + 0x18) = lVar4 + 8;
  bVar3 = *(byte *)(lVar5 + lVar4 + 7);
  lVar1 = *(long *)((long)&__DT_RELA[0xd02].r_info + lVar1);
  *(long *)(param_2 + 0x18) = lVar4 + 0xb;
  puVar10 = &DAT_015df2b0;
  iVar14 = *(int *)(lVar1 + 0x70);
  if (iVar14 != -1) {
    puVar10 = (undefined *)((long)iVar14 * 0x10 + *(long *)(lVar1 + 0x58));
  }
  lVar1 = *(long *)(puVar10 + 8);
  if (lVar1 != 0) {
    uVar12 = (bVar2 & 7) + iVar8;
    uVar13 = (bVar2 >> 4 & 7) + DAT_013942ac;
    uVar9 = game::LinkMap::GetTileLink(lVar1 + 0x14048,1,uVar13,uVar12);
    if (uVar9 != 0xffffffff) {
      iVar18 = iVar18 + (uVar9 >> 1 & (uint)(iVar18 < 3));
    }
    game::HeightMap::GetFineHeight
              (lVar1 + 0x14038,&local_9c,iVar18,uVar13 * 0x200,uVar12 * 0x200,1,1);
    iVar14 = (int)((float)uVar13 * DAT_00cb6ae4 + DAT_00cb6b2c);
    iVar18 = (int)((float)uVar12 * DAT_00cb6ae4 + DAT_00cb6b2c);
    if (uVar20 == 0xffffffff) {
      lVar1 = *(long *)((long)&__DT_RELA[0xcff].r_info + *param_1);
      plVar6 = *(long **)(lVar1 + 0x10);
      while (plVar7 = plVar6, (long *)(lVar1 + 0x10) != plVar7) {
        plVar6 = (long *)*plVar7;
        if (((*(char *)((long)plVar7 + 0x192) != '\0') && ((int)plVar7[0x11] == iVar14)) &&
           ((int)plVar7[0x12] == iVar18)) {
          plVar11 = plVar6;
          if ((*(int *)((long)plVar7 + 0xac) == 1) &&
             (plVar16 = *(long **)(lVar1 + 0x88da0),
             (long *)(lVar1 + 0x88da0) != *(long **)(lVar1 + 0x88da0))) {
            do {
              plVar11 = (long *)*plVar16;
              if (plVar7 + 2 == (long *)plVar16[2]) {
                plVar11[1] = plVar16[1];
                *(long **)plVar16[1] = plVar11;
                FUN_0047fd60(lVar1 + 0x88db0);
                *(long *)(lVar1 + 0x88de0) = *(long *)(lVar1 + 0x88de0) + -1;
              }
              plVar16 = plVar11;
            } while ((long *)(lVar1 + 0x88da0) != plVar11);
            plVar11 = (long *)*plVar7;
          }
          plVar11[1] = plVar7[1];
          *(long **)plVar7[1] = plVar11;
          FUN_00454480(plVar7 + 2);
          if ((plVar7 < *(long **)(lVar1 + 0x48)) || (*(long **)(lVar1 + 0x30) <= plVar7)) {
            HeapInterface::Free(plVar7);
          }
          else {
            *plVar7 = *(long *)(lVar1 + 0x20);
            *(long **)(lVar1 + 0x20) = plVar7;
          }
          *(long *)(lVar1 + 0x50) = *(long *)(lVar1 + 0x50) + -1;
        }
      }
    }
    else {
      local_8c = 0;
      local_78 = (byte)(uVar17 >> 0xf);
      local_84 = 0;
      local_48 = 0;
      local_7c = DAT_013942a8;
      local_68._8_8_ = 0;
      local_68._0_8_ = (ulong)CONCAT24(uVar17,uVar20) & 0x7fffffffffff;
      local_68 = local_68 << 0x20;
      lVar1 = *param_1;
      local_80 = (float)bVar3 * DAT_00cb6b3c;
      local_94 = (int)((float)(int)(short)uVar19 + (float)local_9c);
      local_58._4_4_ =
           *(undefined4 *)
            ((long)&__DT_RELA[0x548].r_addend +
            *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar1) + 0x2f0));
      local_58._0_4_ = *(undefined4 *)(lVar1 + 0x500);
      local_58._8_8_ = 0;
      local_98 = iVar14;
      local_90 = iVar18;
      FUN_00156f20(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar1),&local_98,local_68);
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SKIP_DATA` @ 00173980
```c

undefined * jag::packethandlers::Misc::SKIP_DATA(undefined8 param_1,PacketCore *param_2)

{
  long lVar1;
  uint uVar2;
  bool bVar3;
  
  lVar1 = param_2->position;
  bVar3 = DAT_01050dc0 == 0x3020100;
  param_2->position = lVar1 + 4;
  uVar2 = *(uint *)((long)param_2->bufData + lVar1);
  if (bVar3) {
    uVar2 = uVar2 >> 0x18 | (uVar2 & 0xff0000) >> 8 | (uVar2 & 0xff00) << 8 | uVar2 << 0x18;
  }
  if (0 < (int)uVar2) {
    param_2->position = lVar1 + 8 + (ulong)(uVar2 - 1) * 4;
  }
  Packet::gT_unsigned_int(param_2);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_MULTIWAY_STATE` @ 00173c50
```c

undefined * jag::packethandlers::Misc::SET_MULTIWAY_STATE(long *param_1,long param_2)

{
  byte bVar1;
  long lVar2;
  
  lVar2 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar2 + 1;
  bVar1 = *(byte *)(*(long *)(param_2 + 0x10) + lVar2);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *param_1);
  *(uint *)(*(long *)((long)&__DT_RELA[0xcf5].r_offset + *param_1) + 0x10) =
       (uint)bVar1 + (bVar1 / 3) * -3;
  *(bool *)(lVar2 + 0x168) = bVar1 < 3;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::CUTSCENE_DATA` @ 00174000
```c

undefined * jag::packethandlers::Misc::CUTSCENE_DATA(long *param_1,PacketCore *param_2)

{
  uint *puVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  void *pvVar5;
  long lVar6;
  ushort uVar7;
  uint uVar8;
  uint uVar9;
  long lVar10;
  ulong uVar11;
  uint uVar12;
  ulong uVar13;
  bool bVar14;
  uint local_64;
  uint local_60;
  uint local_5c;
  
  lVar10 = param_2->position;
  pvVar5 = param_2->bufData;
  lVar6 = *(long *)((long)&__DT_RELA[0xcff].r_addend + *param_1);
  param_2->position = lVar10 + 1;
  bVar2 = *(byte *)((long)pvVar5 + lVar10);
  param_2->position = lVar10 + 2;
  bVar3 = *(byte *)((long)pvVar5 + lVar10 + 1);
  param_2->position = lVar10 + 3;
  bVar4 = *(byte *)((long)pvVar5 + lVar10 + 2);
  puVar1 = (uint *)(lVar6 + 0x10 + (ulong)bVar2 * 0x140 + (ulong)bVar3 * 0x28);
  if (bVar4 == 0) {
    *puVar1 = 0;
    puVar1[1] = 0;
    puVar1[2] = 0xffffffff;
    puVar1[4] = 0;
    puVar1[5] = 0;
    puVar1[6] = 0;
    puVar1[7] = 0;
    puVar1[8] = 0;
    puVar1[9] = 0;
    goto LAB_00174092;
  }
  if ((bVar4 & 7) == 7) {
    param_2->position = lVar10 + 4;
    bVar2 = *(byte *)((long)pvVar5 + lVar10 + 3);
    param_2->position = lVar10 + 5;
    bVar3 = *(byte *)((long)pvVar5 + lVar10 + 4);
    local_64 = bVar3 & 7;
    uVar12 = bVar3 >> 3 & 1;
    uVar7 = FUN_00121a30(param_2);
    local_60 = (uint)uVar7;
    if (bVar2 < 2) goto LAB_001740ff;
    lVar10 = param_2->position;
    bVar14 = DAT_01050dc0 == 0x3020100;
    param_2->position = lVar10 + 8;
    uVar11 = *(ulong *)((long)param_2->bufData + lVar10);
    if (bVar14) {
      uVar11 = (uVar11 & 0xff00ff00ff00ff) << 8 | (long)uVar11 >> 8 & 0xff00ff00ff00ffU;
      uVar11 = (long)uVar11 >> 0x10 & 0xffff0000ffffU | (uVar11 & 0xffff0000ffff) << 0x10;
      uVar11 = uVar11 << 0x20 | uVar11 >> 0x20;
      local_5c = Packet::gT_unsigned_int(param_2);
      uVar8 = Packet::gT_unsigned_int(param_2);
      lVar10 = param_2->position;
      param_2->position = lVar10 + 8;
      uVar13 = *(ulong *)((long)param_2->bufData + lVar10);
      uVar13 = (long)uVar13 >> 8 & 0xff00ff00ff00ffU | (uVar13 & 0xff00ff00ff00ff) << 8;
      uVar13 = (uVar13 & 0xffff0000ffff) << 0x10 | (long)uVar13 >> 0x10 & 0xffff0000ffffU;
      uVar13 = uVar13 << 0x20 | uVar13 >> 0x20;
    }
    else {
      local_5c = Packet::gT_unsigned_int(param_2);
      uVar8 = Packet::gT_unsigned_int(param_2);
      lVar10 = param_2->position;
      param_2->position = lVar10 + 8;
      uVar13 = *(ulong *)((long)param_2->bufData + lVar10);
    }
    uVar9 = Packet::gT_unsigned_int(param_2);
    lVar10 = (long)(int)uVar9;
  }
  else {
    local_64 = (uint)(char)(bVar4 & 7);
    uVar12 = bVar4 >> 3 & 1;
    uVar7 = FUN_00121a30(param_2);
    local_60 = (uint)uVar7;
LAB_001740ff:
    uVar8 = Packet::gT_unsigned_int(param_2);
    uVar11 = (ulong)uVar8;
    local_5c = Packet::gT_unsigned_int(param_2);
    uVar8 = Packet::gT_unsigned_int(param_2);
    uVar9 = Packet::gT_unsigned_int(param_2);
    uVar13 = (ulong)uVar9;
    lVar10 = 0;
  }
  param_2->position = param_2->position + lVar10;
  puVar1[1] = uVar12;
  *(ulong *)(puVar1 + 4) = uVar11;
  *puVar1 = local_64;
  puVar1[7] = uVar8;
  puVar1[2] = local_60;
  puVar1[6] = local_5c;
  *(ulong *)(puVar1 + 8) = uVar13;
LAB_00174092:
  lVar10 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *(long *)(lVar6 + 8));
  *(undefined4 *)(lVar10 + 0x140) = *(undefined4 *)(*(long *)(lVar10 + 0x138) + 0x10);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_RUN_ENERGY` @ 001750c0
```c

undefined * jag::packethandlers::Misc::SET_RUN_ENERGY(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  
                    /* OFFICIAL PACKET NAME: jag::ServerProt::UPDATE_RUNENERGY (op 80, size 1).
                       Reads 1 byte -> player +0x60 (run energy). The Ghidra function name
                       'SET_RUN_ENERGY' is a fabricated rename, NOT the official packet name. */
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)((long)&__DT_RELA[0xcfe].r_info + *param_1);
  *(long *)(param_2 + 0x18) = lVar1 + 1;
  *(uint *)(lVar2 + 0x60) = (uint)*(byte *)(*(long *)(param_2 + 0x10) + lVar1);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::SET_READY_FLAG` @ 00175120
```c

undefined * jag::packethandlers::ClientState::SET_READY_FLAG(long *param_1)

{
  long lVar1;
  long lVar2;
  
  lVar1 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  lVar2 = *(long *)(lVar1 + 0xe8);
  *(undefined4 *)(*(long *)((long)&__DT_RELA[0xcfe].r_info + *param_1) + 0x10) = 1;
  *(undefined4 *)(lVar1 + 0xf0) = *(undefined4 *)(lVar2 + 0x10);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::SET_TICK_TIMER` @ 00175450
```c

undefined * jag::packethandlers::ClientState::SET_TICK_TIMER(long *param_1,long param_2)

{
  long lVar1;
  long *plVar2;
  ushort uVar3;
  uint uVar4;
  bool bVar5;
  
  lVar1 = *(long *)(param_2 + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar1 + 2;
  uVar3 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar1);
  if (bVar5) {
    uVar3 = uVar3 << 8 | uVar3 >> 8;
  }
  if (*(int *)((long)&__DT_RELA[0xd40].r_info + *param_1) == 0x14) {
    uVar4 = (((uint)uVar3 + (uint)uVar3 * 4) * 5) / 10;
  }
  else {
    uVar4 = (uint)uVar3 * 0x1e;
  }
  plVar2 = *(long **)((long)&__DT_RELA[0x3382].r_offset + *param_1);
  *(uint *)((long)plVar2 + 0xc) = uVar4;
  lVar1 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *plVar2);
  *(undefined4 *)(lVar1 + 0x100) = *(undefined4 *)(*(long *)(lVar1 + 0xf8) + 0x10);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SKIP_2_BYTES` @ 00176110
```c

undefined * jag::packethandlers::Misc::SKIP_2_BYTES(undefined8 param_1,long param_2)

{
  *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + 2;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::UPDATE_URL_STRING` @ 0017a560
```c

undefined * jag::packethandlers::Misc::UPDATE_URL_STRING(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  size_t sVar4;
  char *__s;
  undefined1 local_48;
  undefined7 uStack_47;
  char local_31;
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)(param_2 + 0x10);
  local_48 = 0;
  lVar3 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
  local_31 = '\x17';
  *(undefined4 *)(lVar3 + 0x130) = *(undefined4 *)(*(long *)(lVar3 + 0x128) + 0x10);
  *(long *)(param_2 + 0x18) = lVar1 + 4;
  if (*(char *)(lVar2 + 3 + lVar1) == '\0') {
    __s = (char *)(lVar1 + 4 + lVar2);
    sVar4 = strlen(__s);
    if (sVar4 == 0) {
      *(long *)(param_2 + 0x18) = lVar1 + 5;
    }
    else {
      FUN_001398d0(__s,sVar4 & 0xffffffff,&local_48);
      *(size_t *)(param_2 + 0x18) = sVar4 + 1 + *(long *)(param_2 + 0x18);
      if ((local_31 < '\0') && (CONCAT71(uStack_47,local_48) != 0)) {
        eastl__basic_string();
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::WorldData::SET_WORLD_TARGET` @ 0017fc70
```c

undefined * jag::packethandlers::WorldData::SET_WORLD_TARGET(long *param_1,long param_2)

{
  long lVar1;
  undefined8 *puVar2;
  char *pcVar3;
  long lVar4;
  undefined8 *puVar5;
  undefined1 *puVar6;
  ushort uVar7;
  ushort uVar8;
  ushort uVar9;
  bool bVar10;
  undefined1 local_58;
  undefined7 uStack_57;
  long local_50;
  char local_41 [17];
  
                    /* op212 handler
                       jag::packethandlers::WorldData::SET_WORLD_TARGET(client,packet). Wire: gStr
                       host, g2 worldId, g2 portA, g2 portB. Writes a 0x50-byte target (vtable
                       PTR_FUN_013657a0) into the WorldSwitcher EXPLICIT slot: wrapper@+0x28,
                       struct@+0x30 (host@+0x10, worldId@+8, portA@+0x28, portB@+0x2a). Does NOT
                       call SetMainState - no state transition, pure store. This +0x30 slot is the
                       one LoginStepWaitingConnectionOpened reads ONLY for NON-world login types
                       (lobby/reconnect, loginMgr+0x20 != 2); it is NOT read on a cold-lobby WORLD
                       login (type 2, which reads +0x20). [VERIFIED rs2client.948-5 @0x0017fc70] */
  puVar6 = &local_58;
  local_58 = 0;
  local_41[0] = '\x17';
  FUN_00afd8d0(param_2,puVar6);
  lVar1 = *(long *)(param_2 + 0x18);
  bVar10 = DAT_01050dc0 == 0x3020100;
  lVar4 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar1 + 2;
  uVar7 = *(ushort *)(lVar4 + lVar1);
  if (bVar10) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
    *(long *)(param_2 + 0x18) = lVar1 + 4;
    uVar8 = *(ushort *)(lVar4 + 2 + lVar1);
    *(long *)(param_2 + 0x18) = lVar1 + 6;
    uVar9 = *(ushort *)(lVar4 + 4 + lVar1);
    uVar8 = uVar8 << 8 | uVar8 >> 8;
    uVar9 = uVar9 << 8 | uVar9 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar1 + 4;
    uVar8 = *(ushort *)(lVar4 + 2 + lVar1);
    *(long *)(param_2 + 0x18) = lVar1 + 6;
    uVar9 = *(ushort *)(lVar4 + 4 + lVar1);
  }
  lVar1 = *(long *)((long)&__DT_RELA[0xd01].r_offset + *param_1);
  lVar4 = *(long *)(lVar1 + 0x30);
  if (lVar4 == 0) {
    lVar4 = *(long *)(lVar1 + 8);
    puVar2 = (undefined8 *)FUN_00c29430(0x50);
    puVar5 = (undefined8 *)0x0;
    if (puVar2 != (undefined8 *)0x0) {
      *(uint *)(puVar2 + 5) = (uint)uVar7;
      puVar5 = puVar2 + 4;
      *(undefined4 *)((long)puVar2 + 0x2c) = 1;
      *(undefined1 *)(puVar2 + 6) = 0;
      *(undefined1 *)((long)puVar2 + 0x47) = 0x17;
      puVar2[4] = lVar4 + 0x195e8;
      *(ushort *)((long)puVar2 + 0x4a) = uVar9;
      *(undefined1 *)((long)puVar2 + 0x4c) = 1;
      *(ushort *)(puVar2 + 9) = uVar8;
      FUN_00493e00(puVar2 + 6,puVar6);
      *puVar2 = &PTR_FUN_013657a0;
      puVar2[2] = 1;
      puVar2[1] = 0x100000001;
      puVar2[3] = puVar5;
    }
    lVar4 = *(long *)(lVar1 + 0x28);
    *(undefined8 **)(lVar1 + 0x30) = puVar5;
    *(undefined8 **)(lVar1 + 0x28) = puVar2;
    if (lVar4 != 0) {
      ref_counter_base::DecRef();
    }
  }
  else {
    if ((undefined1 *)(lVar4 + 0x10) != puVar6) {
      if (local_41[0] < '\0') {
        puVar6 = (undefined1 *)CONCAT71(uStack_57,local_58);
        pcVar3 = puVar6 + local_50;
      }
      else {
        pcVar3 = local_41 + -(long)local_41[0];
      }
      FUN_00493d20((undefined1 *)(lVar4 + 0x10),puVar6,pcVar3);
      lVar4 = *(long *)(lVar1 + 0x30);
    }
    *(uint *)(lVar4 + 8) = (uint)uVar7;
    *(ushort *)(lVar4 + 0x28) = uVar8;
    *(ushort *)(lVar4 + 0x2a) = uVar9;
  }
  if ((local_41[0] < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    HeapInterface::Free();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SET_URL_STRING` @ 0017fe90
```c

undefined * jag::packethandlers::Misc::SET_URL_STRING(long *param_1,undefined8 param_2)

{
  long lVar1;
  char *pcVar2;
  undefined1 *puVar3;
  undefined1 *puVar4;
  undefined1 local_38;
  undefined7 uStack_37;
  long local_30;
  char local_21 [17];
  
  puVar3 = &local_38;
  local_38 = 0;
  local_21[0] = '\x17';
  FUN_00afd8d0(param_2,&local_38);
  lVar1 = *(long *)((long)&__DT_RELA[0xcf4].r_offset + *param_1);
  *(undefined4 *)(lVar1 + 0x3c) = 2;
  puVar4 = (undefined1 *)(lVar1 + 0x40);
  if (puVar4 != &local_38) {
    if (local_21[0] < '\0') {
      puVar3 = (undefined1 *)CONCAT71(uStack_37,local_38);
      pcVar2 = puVar3 + local_30;
    }
    else {
      pcVar2 = local_21 + -(long)local_21[0];
    }
    FUN_00493d20(puVar4,puVar3,pcVar2);
  }
  if ((local_21[0] < '\0') && (CONCAT71(uStack_37,local_38) != 0)) {
    HeapInterface::Free();
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::SERVER_TICK_END` @ 00181880
```c

undefined * jag::packethandlers::Misc::SERVER_TICK_END(long *param_1,undefined8 param_2)

{
  long lVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  long lVar5;
  
  lVar4 = Packet::gT_ulong(param_2);
  lVar1 = *(long *)((long)&__DT_RELA[0xd40].r_addend + *param_1);
  if (lVar1 != 0) {
    lVar5 = std__chrono___V2__system_clock__now();
    lVar2 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *param_1);
    lVar3 = *(long *)(lVar2 + 0x188);
    *(long *)(lVar1 + 0x90) = lVar4 - lVar5 / 1000000;
    *(undefined4 *)(lVar2 + 400) = *(undefined4 *)(lVar3 + 0x10);
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::RUNCLIENTSCRIPT_SHORT` @ 001871a0
```c

undefined * jag::packethandlers::ClientState::RUNCLIENTSCRIPT_SHORT(long *param_1,long param_2)

{
  int *piVar1;
  long lVar2;
  long lVar3;
  long lVar4;
  ushort uVar5;
  bool bVar6;
  
                    /* ServerProt op 100 (size 4). Reads u16 + byteAdd value, calls
                       ClientState::RUNCLIENTSCRIPT (2-arg short form). Short form of
                       RUNCLIENTSCRIPT (full form is op 110). */
  lVar2 = *(long *)(param_2 + 0x18);
  bVar6 = DAT_01050dc0 == 0x3020100;
  lVar3 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar2 + 2;
  uVar5 = *(ushort *)(lVar3 + lVar2);
  if (bVar6) {
    uVar5 = uVar5 << 8 | uVar5 >> 8;
  }
  lVar4 = *param_1;
  *(long *)(param_2 + 0x18) = lVar2 + 4;
  RUNCLIENTSCRIPT(*(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + lVar4),uVar5,
                  (ushort)*(byte *)(lVar3 + 3 + lVar2) * 0x100 +
                  (*(byte *)(lVar3 + 2 + lVar2) - 0x80 & 0xff),uVar5);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  piVar1 = (int *)(lVar2 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC` @ 00187610
```c

undefined * jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC(long *param_1,PacketCore *param_2)

{
  undefined1 uVar1;
  undefined1 uVar2;
  void *pvVar3;
  long lVar4;
  uint uVar5;
  long lVar6;
  ushort uVar7;
  ushort uVar8;
  bool bVar9;
  
  uVar5 = Packet::gT_unsigned_int(param_2);
  lVar6 = param_2->position;
  bVar9 = DAT_01050dc0 == 0x3020100;
  pvVar3 = param_2->bufData;
  param_2->position = lVar6 + 1;
  uVar1 = *(undefined1 *)((long)pvVar3 + lVar6);
  param_2->position = lVar6 + 3;
  uVar7 = *(ushort *)((long)pvVar3 + lVar6 + 1);
  if (bVar9) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
    param_2->position = lVar6 + 4;
    uVar2 = *(undefined1 *)((long)pvVar3 + lVar6 + 3);
    param_2->position = lVar6 + 6;
    uVar8 = *(ushort *)((long)pvVar3 + lVar6 + 4);
    uVar8 = uVar8 << 8 | uVar8 >> 8;
  }
  else {
    param_2->position = lVar6 + 4;
    uVar2 = *(undefined1 *)((long)pvVar3 + lVar6 + 3);
    param_2->position = lVar6 + 6;
    uVar8 = *(ushort *)((long)pvVar3 + lVar6 + 4);
  }
  lVar6 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if (lVar6 != 0) {
    lVar6 = FUN_00b0cd30(lVar6,lVar6,DAT_00fb4043,uVar5,uVar1,uVar2,6,4,0,0,&DAT_015c01c8,
                         0xffffffffffffffff,uVar8,0);
    if (lVar6 != 0) {
      if (uVar7 == 0) {
        if (((*(int *)(lVar6 + 0x94) < 1) && (*(char *)(lVar6 + 0x98) == '\0')) &&
           (*(int *)(lVar6 + 0x14) < 4)) {
          if ((*(int *)(lVar6 + 0x14) == 3) && (lVar4 = *(long *)(lVar6 + 0x28), lVar4 != 0)) {
            if ((*(int *)(lVar4 + 0x30) - 2U & 0xfffffffd) == 0) {
              if (*(int *)(lVar4 + 0x30) == 2) {
                *(undefined4 *)(lVar4 + 0x24) = *(undefined4 *)(lVar4 + 0x20);
              }
              *(undefined4 *)(lVar4 + 0x30) = 3;
            }
            *(undefined4 *)(lVar6 + 0x14) = 4;
          }
          *(undefined1 *)(lVar6 + 0x10) = 1;
        }
      }
      else {
        *(uint *)(lVar6 + 0x94) = (uint)uVar7;
        *(undefined1 *)(lVar6 + 0x98) = 0;
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC_PACKED` @ 00187770
```c

undefined * jag::packethandlers::ZoneUpdates::SPOTANIM_SPECIFIC_PACKED(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  undefined1 uVar3;
  undefined1 uVar4;
  long lVar5;
  long lVar6;
  long lVar7;
  uint uVar8;
  ushort uVar9;
  undefined1 *puVar10;
  ushort uVar11;
  
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  lVar7 = lVar5 + 5;
  lVar1 = lVar5 + 7;
  lVar2 = lVar5 + 8;
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  uVar8 = *(uint *)(lVar6 + lVar5);
  puVar10 = (undefined1 *)(lVar5 + 4 + lVar6);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(param_2 + 0x18) = lVar7;
    uVar3 = *puVar10;
    uVar8 = uVar8 >> 0x18 | (uVar8 & 0xff0000) >> 8 | (uVar8 & 0xff00) << 8 | uVar8 << 0x18;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar11 = *(ushort *)(lVar6 + lVar7);
    *(long *)(param_2 + 0x18) = lVar2;
    uVar4 = *(undefined1 *)(lVar6 + lVar1);
    *(long *)(param_2 + 0x18) = lVar5 + 10;
    uVar9 = *(ushort *)(lVar6 + lVar2);
    uVar11 = uVar11 << 8 | uVar11 >> 8;
    uVar9 = uVar9 << 8 | uVar9 >> 8;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar7;
    uVar3 = *puVar10;
    *(long *)(param_2 + 0x18) = lVar1;
    uVar11 = *(ushort *)(lVar6 + lVar7);
    *(long *)(param_2 + 0x18) = lVar2;
    uVar4 = *(undefined1 *)(lVar6 + lVar1);
    *(long *)(param_2 + 0x18) = lVar5 + 10;
    uVar9 = *(ushort *)(lVar6 + lVar2);
  }
  lVar7 = *(long *)((long)&__DT_RELA[0xd06].r_info + *param_1);
  if (lVar7 != 0) {
    lVar7 = FUN_00b0cd30(lVar7,lVar7,DAT_00fb4043,uVar8,uVar3,uVar4,6,4,0,0,&DAT_015c01c8,
                         0xffffffffffffffff,uVar9,0);
    if (lVar7 != 0) {
      if (uVar11 == 0) {
        if (((*(int *)(lVar7 + 0x94) < 1) && (*(char *)(lVar7 + 0x98) == '\0')) &&
           (*(int *)(lVar7 + 0x14) < 4)) {
          if ((*(int *)(lVar7 + 0x14) == 3) && (lVar1 = *(long *)(lVar7 + 0x28), lVar1 != 0)) {
            if ((*(int *)(lVar1 + 0x30) - 2U & 0xfffffffd) == 0) {
              if (*(int *)(lVar1 + 0x30) == 2) {
                *(undefined4 *)(lVar1 + 0x24) = *(undefined4 *)(lVar1 + 0x20);
              }
              *(undefined4 *)(lVar1 + 0x30) = 3;
            }
            *(undefined4 *)(lVar7 + 0x14) = 4;
          }
          *(undefined1 *)(lVar7 + 0x10) = 1;
        }
      }
      else {
        *(uint *)(lVar7 + 0x94) = (uint)uVar11;
        *(undefined1 *)(lVar7 + 0x98) = 0;
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY` @ 00190020
```c

undefined *
jag::packethandlers::WorldData::WORLDLIST_FETCH_REPLY(long *param_1,long param_2,int *param_3)

{
  undefined1 *puVar1;
  long *plVar2;
  char cVar3;
  byte bVar4;
  uint uVar5;
  long lVar6;
  undefined1 *puVar7;
  ushort uVar8;
  ushort uVar9;
  int iVar10;
  undefined4 uVar11;
  long lVar12;
  void *pvVar13;
  undefined8 *__dest;
  long lVar14;
  void *pvVar15;
  undefined4 *puVar16;
  undefined8 *puVar17;
  uint *puVar18;
  undefined8 *puVar19;
  size_t __n;
  undefined4 *puVar20;
  ulong uVar21;
  void *pvVar22;
  undefined4 *puVar23;
  char *pcVar24;
  undefined8 *__src;
  uint *puVar25;
  ulong uVar26;
  undefined8 uVar27;
  ulong uVar28;
  long lVar29;
  long *plVar30;
  int iVar31;
  uint uVar32;
  undefined1 local_128;
  undefined7 uStack_127;
  char local_111;
  undefined1 local_108;
  undefined7 uStack_107;
  char local_f1;
  undefined1 local_e8;
  undefined7 uStack_e7;
  char local_d1;
  uint local_c8;
  undefined4 uStack_c4;
  long local_c0;
  undefined8 local_b8;
  long local_b0;
  char local_a1;
  uint local_a0;
  uint local_9c;
  long local_98 [2];
  char local_81;
  uint local_80;
  long local_78 [2];
  char local_61;
  long local_60 [2];
  char local_49;
  undefined8 local_48;
  
                    /* WORLDLIST_FETCH_REPLY — handler body for ServerProt op 216 (varShort).
                       Reads:
                         1. byte = frame indicator (1 = last segment)
                         2. If 1: builds worldlist hashtable from packet data:
                            - smart worldCount, per-world: smart worldId, smart country, smart
                       flagsLE, string hostname/path, smart playerCount
                            - smart revision
                            - per-world player count updates: smart worldId, u16 count (0xFFFF = -1
                       = offline)
                       Confirmed by behavior: getUnsignedSmart loop populating client+0x10
                       hashtable. */
  lVar14 = *(long *)(param_2 + 0x18);
  iVar31 = *param_3;
  lVar6 = *(long *)((long)&__DT_RELA[0xd01].r_offset + *param_1);
  lVar29 = lVar14 + 1;
  *(long *)(param_2 + 0x18) = lVar29;
  lVar12 = *(long *)(param_2 + 0x10);
  cVar3 = *(char *)(lVar12 + lVar14);
  if (*(long *)(lVar6 + 0xd8) == 0) {
    local_c0 = 0;
    local_b8 = 0;
    local_b0 = 0;
    Packet::ResizeBuffer(&local_c8,"d");
    lVar29 = *(long *)(lVar6 + 0xe8);
    *(long *)(lVar6 + 0xe8) = local_b0;
    lVar14 = *(long *)(lVar6 + 0xd8);
    *(long *)(lVar6 + 0xd8) = local_c0;
    lVar12 = *(long *)(lVar6 + 0xe0);
    *(long *)(lVar6 + 0xe0) = local_b8;
    local_c0 = lVar14;
    local_b8 = lVar12;
    local_b0 = lVar29;
    if ((lVar12 != 0) && (lVar14 != 0)) {
      HeapInterface::Free();
    }
    lVar29 = *(long *)(param_2 + 0x18);
    lVar12 = *(long *)(param_2 + 0x10);
  }
  uVar21 = (long)iVar31 - 1;
  if (uVar21 != 0) {
    lVar14 = *(long *)(lVar6 + 0xe8);
    if (lVar12 != 0) {
      uVar28 = *(long *)(lVar6 + 0xd8) - lVar14;
      if (uVar28 < uVar21) {
        uVar21 = uVar28;
      }
      memcpy((void *)(lVar14 + *(long *)(lVar6 + 0xe0)),(void *)(lVar12 + lVar29),uVar21);
      lVar14 = *(long *)(lVar6 + 0xe8);
    }
    *(ulong *)(lVar6 + 0xe8) = lVar14 + uVar21;
  }
  if (cVar3 != '\x01') goto LAB_001900b0;
  pvVar15 = *(void **)(lVar6 + 0x120);
  *(undefined8 *)(lVar6 + 0xe8) = 1;
  puVar7 = *(undefined1 **)(lVar6 + 0x10);
  *(void **)(lVar6 + 0x128) = pvVar15;
  if (**(char **)(lVar6 + 0xe0) == '\x02') {
    *(undefined8 *)(lVar6 + 0xe8) = 2;
    lVar29 = lVar6 + 0xd0;
    if ((*(char **)(lVar6 + 0xe0))[1] == '\x01') {
      plVar30 = *(long **)(puVar7 + 0x10);
      if (*(long *)(puVar7 + 0x18) != 0) {
        plVar2 = plVar30 + *(long *)(puVar7 + 0x18);
        do {
          lVar14 = *plVar30;
          while (lVar14 != 0) {
            lVar12 = *(long *)(lVar14 + 0x90);
            if ((*(char *)(lVar14 + 0x87) < '\0') && (*(long *)(lVar14 + 0x70) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)(lVar14 + 0x6f) < '\0') && (*(long *)(lVar14 + 0x58) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)(lVar14 + 0x4f) < '\0') && (*(long *)(lVar14 + 0x38) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)(lVar14 + 0x2f) < '\0') && (*(long *)(lVar14 + 0x18) != 0)) {
              HeapInterface::Free();
            }
            HeapInterface::Free(lVar14);
            lVar14 = lVar12;
          }
          *plVar30 = 0;
          plVar30 = plVar30 + 1;
        } while (plVar2 != plVar30);
      }
      *(undefined8 *)(puVar7 + 0x20) = 0;
      puVar1 = puVar7 + 0x48;
      if (*(long *)(puVar7 + 0x50) != 0) {
        FUN_00482850(puVar1);
      }
      *puVar7 = 0;
      uVar8 = Packet::getUnsignedSmart(lVar29);
      uVar21 = (ulong)uVar8;
      if (uVar21 == *(ulong *)(puVar7 + 0x50)) {
        puVar23 = *(undefined4 **)(puVar7 + 0x58);
        if (puVar23 == (undefined4 *)0x0) {
          if (uVar21 != 0) goto LAB_001911bf;
        }
        else if (uVar21 != 0) {
          uVar32 = (int)(uVar21 * 0x20 - 0x20 >> 5) + 1U & 7;
          puVar16 = puVar23;
          if (uVar32 != 0) {
            if (uVar32 != 1) {
              if (uVar32 != 2) {
                if (uVar32 != 3) {
                  if (uVar32 != 4) {
                    if (uVar32 != 5) {
                      if (uVar32 != 6) {
                        if ((*(char *)((long)puVar23 + 0x1f) < '\0') &&
                           (*(long *)(puVar23 + 2) != 0)) {
                          HeapInterface::Free();
                        }
                        puVar16 = puVar23 + 8;
                      }
                      if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0))
                      {
                        HeapInterface::Free();
                      }
                      puVar16 = puVar16 + 8;
                    }
                    if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
                      HeapInterface::Free();
                    }
                    puVar16 = puVar16 + 8;
                  }
                  if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
                    HeapInterface::Free();
                  }
                  puVar16 = puVar16 + 8;
                }
                if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
                  HeapInterface::Free();
                }
                puVar16 = puVar16 + 8;
              }
              if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
                HeapInterface::Free();
              }
              puVar16 = puVar16 + 8;
            }
            if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
              HeapInterface::Free();
            }
            puVar16 = puVar16 + 8;
            if (puVar23 + uVar21 * 8 == puVar16) goto LAB_001911bf;
          }
          do {
            if ((*(char *)((long)puVar16 + 0x1f) < '\0') && (*(long *)(puVar16 + 2) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0x3f) < '\0') && (*(long *)(puVar16 + 10) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0x5f) < '\0') && (*(long *)(puVar16 + 0x12) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0x7f) < '\0') && (*(long *)(puVar16 + 0x1a) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0x9f) < '\0') && (*(long *)(puVar16 + 0x22) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0xbf) < '\0') && (*(long *)(puVar16 + 0x2a) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0xdf) < '\0') && (*(long *)(puVar16 + 0x32) != 0)) {
              HeapInterface::Free();
            }
            if ((*(char *)((long)puVar16 + 0xff) < '\0') && (*(long *)(puVar16 + 0x3a) != 0)) {
              HeapInterface::Free();
            }
            puVar16 = puVar16 + 0x40;
          } while (puVar23 + uVar21 * 8 != puVar16);
LAB_001911bf:
          *(undefined1 *)(puVar23 + 2) = 0;
          *(undefined1 *)((long)puVar23 + 0x1f) = 0x17;
          *puVar23 = 0xffffffff;
          uVar28 = 1;
          puVar16 = puVar23 + 8;
          uVar9 = uVar8 - 1 & 7;
          if (1 < uVar21) {
            if (uVar9 != 0) {
              if (uVar9 != 1) {
                if (uVar9 != 2) {
                  if (uVar9 != 3) {
                    if (uVar9 != 4) {
                      if (uVar9 != 5) {
                        if (uVar9 != 6) {
                          *puVar16 = 0xffffffff;
                          *(undefined1 *)(puVar23 + 10) = 0;
                          uVar28 = 2;
                          *(undefined1 *)((long)puVar23 + 0x3f) = 0x17;
                          puVar16 = puVar23 + 0x10;
                        }
                        *puVar16 = 0xffffffff;
                        *(undefined1 *)(puVar16 + 2) = 0;
                        uVar28 = uVar28 + 1;
                        *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                        puVar16 = puVar16 + 8;
                      }
                      *puVar16 = 0xffffffff;
                      *(undefined1 *)(puVar16 + 2) = 0;
                      uVar28 = uVar28 + 1;
                      *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                      puVar16 = puVar16 + 8;
                    }
                    *puVar16 = 0xffffffff;
                    *(undefined1 *)(puVar16 + 2) = 0;
                    uVar28 = uVar28 + 1;
                    *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                    puVar16 = puVar16 + 8;
                  }
                  *puVar16 = 0xffffffff;
                  *(undefined1 *)(puVar16 + 2) = 0;
                  uVar28 = uVar28 + 1;
                  *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                  puVar16 = puVar16 + 8;
                }
                *puVar16 = 0xffffffff;
                *(undefined1 *)(puVar16 + 2) = 0;
                uVar28 = uVar28 + 1;
                *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                puVar16 = puVar16 + 8;
              }
              uVar28 = uVar28 + 1;
              *puVar16 = 0xffffffff;
              *(undefined1 *)(puVar16 + 2) = 0;
              *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
              puVar16 = puVar16 + 8;
              if (uVar21 <= uVar28) goto LAB_00190715;
            }
            do {
              uVar28 = uVar28 + 8;
              *puVar16 = 0xffffffff;
              *(undefined1 *)(puVar16 + 2) = 0;
              *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
              puVar16[8] = 0xffffffff;
              *(undefined1 *)(puVar16 + 10) = 0;
              *(undefined1 *)((long)puVar16 + 0x3f) = 0x17;
              puVar16[0x10] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x12) = 0;
              *(undefined1 *)((long)puVar16 + 0x5f) = 0x17;
              puVar16[0x18] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x1a) = 0;
              *(undefined1 *)((long)puVar16 + 0x7f) = 0x17;
              puVar16[0x20] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x22) = 0;
              *(undefined1 *)((long)puVar16 + 0x9f) = 0x17;
              puVar16[0x28] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x2a) = 0;
              *(undefined1 *)((long)puVar16 + 0xbf) = 0x17;
              puVar16[0x30] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x32) = 0;
              *(undefined1 *)((long)puVar16 + 0xdf) = 0x17;
              puVar16[0x38] = 0xffffffff;
              *(undefined1 *)(puVar16 + 0x3a) = 0;
              *(undefined1 *)((long)puVar16 + 0xff) = 0x17;
              puVar16 = puVar16 + 0x40;
            } while (uVar28 < uVar21);
          }
        }
      }
      else if (uVar21 == 0) {
        FUN_00482850(puVar1);
      }
      else if (*(long *)(puVar7 + 0x58) == 0) {
        puVar16 = (undefined4 *)FUN_00c29480(uVar21 * 0x20);
        uVar32 = (int)(uVar21 * 0x20 - 0x20 >> 5) + 1U & 7;
        puVar23 = puVar16;
        if (uVar32 == 0) goto LAB_0019137e;
        if (uVar32 != 1) {
          puVar20 = puVar16;
          if (uVar32 != 2) {
            if (uVar32 != 3) {
              if (uVar32 != 4) {
                if (uVar32 != 5) {
                  if (uVar32 != 6) {
                    puVar20 = puVar16 + 8;
                    *puVar16 = 0xffffffff;
                    *(undefined1 *)(puVar16 + 2) = 0;
                    *(undefined1 *)((long)puVar16 + 0x1f) = 0x17;
                  }
                  *puVar20 = 0xffffffff;
                  *(undefined1 *)(puVar20 + 2) = 0;
                  puVar23 = puVar20 + 8;
                  *(undefined1 *)((long)puVar20 + 0x1f) = 0x17;
                }
                *puVar23 = 0xffffffff;
                *(undefined1 *)(puVar23 + 2) = 0;
                puVar20 = puVar23 + 8;
                *(undefined1 *)((long)puVar23 + 0x1f) = 0x17;
              }
              *puVar20 = 0xffffffff;
              *(undefined1 *)(puVar20 + 2) = 0;
              puVar23 = puVar20 + 8;
              *(undefined1 *)((long)puVar20 + 0x1f) = 0x17;
            }
            *puVar23 = 0xffffffff;
            *(undefined1 *)(puVar23 + 2) = 0;
            puVar20 = puVar23 + 8;
            *(undefined1 *)((long)puVar23 + 0x1f) = 0x17;
          }
          *puVar20 = 0xffffffff;
          *(undefined1 *)(puVar20 + 2) = 0;
          puVar23 = puVar20 + 8;
          *(undefined1 *)((long)puVar20 + 0x1f) = 0x17;
        }
        *puVar23 = 0xffffffff;
        *(undefined1 *)(puVar23 + 2) = 0;
        *(undefined1 *)((long)puVar23 + 0x1f) = 0x17;
        for (puVar23 = puVar23 + 8; puVar16 + uVar21 * 8 != puVar23; puVar23 = puVar23 + 0x40) {
LAB_0019137e:
          *puVar23 = 0xffffffff;
          *(undefined1 *)(puVar23 + 2) = 0;
          *(undefined1 *)((long)puVar23 + 0x1f) = 0x17;
          puVar23[8] = 0xffffffff;
          *(undefined1 *)(puVar23 + 10) = 0;
          *(undefined1 *)((long)puVar23 + 0x3f) = 0x17;
          puVar23[0x10] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x12) = 0;
          *(undefined1 *)((long)puVar23 + 0x5f) = 0x17;
          puVar23[0x18] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x1a) = 0;
          *(undefined1 *)((long)puVar23 + 0x7f) = 0x17;
          puVar23[0x20] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x22) = 0;
          *(undefined1 *)((long)puVar23 + 0x9f) = 0x17;
          puVar23[0x28] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x2a) = 0;
          *(undefined1 *)((long)puVar23 + 0xbf) = 0x17;
          puVar23[0x30] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x32) = 0;
          *(undefined1 *)((long)puVar23 + 0xdf) = 0x17;
          puVar23[0x38] = 0xffffffff;
          *(undefined1 *)(puVar23 + 0x3a) = 0;
          *(undefined1 *)((long)puVar23 + 0xff) = 0x17;
        }
        FUN_00482850(puVar1);
        *(ulong *)(puVar7 + 0x50) = uVar21;
        *(undefined4 **)(puVar7 + 0x58) = puVar16;
      }
LAB_00190715:
      if (uVar8 != 0) {
        uVar28 = 0;
        lVar14 = (ulong)(uVar8 - 1) + 1;
        uVar21 = lVar14 * 0x20;
        if ((uVar21 & 0x20) != 0) {
          uVar8 = Packet::getUnsignedSmart(lVar29);
          local_e8 = 0;
          local_d1 = '\x17';
          FUN_00ae71a0(lVar29,&local_e8);
          local_c8 = (uint)uVar8;
          FUN_0048dcd0(&local_c0,&local_e8);
          puVar25 = *(uint **)(puVar7 + 0x58);
          *puVar25 = local_c8;
          FUN_00126110(puVar25 + 2,&local_c0);
          if ((local_b0 < 0) && (local_c0 != 0)) {
            HeapInterface::Free();
          }
          if ((local_d1 < '\0') && (CONCAT71(uStack_e7,local_e8) != 0)) {
            HeapInterface::Free();
          }
          uVar28 = 0x20;
          if (lVar14 == 1) goto LAB_001908e3;
        }
        do {
          uVar8 = Packet::getUnsignedSmart(lVar29);
          local_e8 = 0;
          local_d1 = '\x17';
          FUN_00ae71a0(lVar29,&local_e8);
          local_c8 = (uint)uVar8;
          FUN_0048dcd0(&local_c0,&local_e8);
          lVar14 = *(long *)(puVar7 + 0x58);
          *(uint *)(lVar14 + uVar28) = local_c8;
          FUN_00126110((uint *)(lVar14 + uVar28) + 2,&local_c0);
          if ((local_b0 < 0) && (local_c0 != 0)) {
            HeapInterface::Free();
          }
          if ((local_d1 < '\0') && (CONCAT71(uStack_e7,local_e8) != 0)) {
            HeapInterface::Free();
          }
          uVar8 = Packet::getUnsignedSmart(lVar29);
          local_e8 = 0;
          local_d1 = '\x17';
          FUN_00ae71a0(lVar29,&local_e8);
          local_c8 = (uint)uVar8;
          FUN_0048dcd0(&local_c0,&local_e8);
          puVar25 = (uint *)(*(long *)(puVar7 + 0x58) + uVar28 + 0x20);
          *puVar25 = local_c8;
          FUN_00126110(puVar25 + 2,&local_c0);
          if ((local_b0 < 0) && (local_c0 != 0)) {
            HeapInterface::Free();
          }
          if ((local_d1 < '\0') && (CONCAT71(uStack_e7,local_e8) != 0)) {
            HeapInterface::Free();
          }
          uVar28 = uVar28 + 0x40;
        } while (uVar21 != uVar28);
      }
LAB_001908e3:
      uVar8 = Packet::getUnsignedSmart(lVar29);
      *(uint *)(puVar7 + 0x38) = (uint)uVar8;
      uVar8 = Packet::getUnsignedSmart(lVar29);
      *(uint *)(puVar7 + 0x3c) = (uint)uVar8;
      uVar8 = Packet::getUnsignedSmart(lVar29);
      *(uint *)(puVar7 + 0x40) = (uint)uVar8;
      if (uVar8 != 0) {
        iVar31 = 0;
        do {
          uVar8 = Packet::getUnsignedSmart(lVar29);
          lVar14 = *(long *)(lVar6 + 0xe8);
          *(long *)(lVar6 + 0xe8) = lVar14 + 1;
          bVar4 = *(byte *)(*(long *)(lVar6 + 0xe0) + lVar14);
          uVar32 = FUN_0018fff0(lVar29);
          uVar9 = Packet::getUnsignedSmart();
          local_128 = 0;
          local_111 = '\x17';
          if (uVar9 != 0) {
            local_c8 = local_c8 & 0xffffff00;
            local_b8 = CONCAT17(0x17,(undefined7)local_b8);
            FUN_00ae71a0(lVar29,&local_c8);
            FUN_00126110(&local_128,&local_c8);
            if ((local_b8 < 0) && (CONCAT44(uStack_c4,local_c8) != 0)) {
              HeapInterface::Free();
            }
          }
          local_108 = 0;
          local_f1 = '\x17';
          FUN_00ae71a0(lVar29,&local_108);
          local_e8 = 0;
          local_d1 = '\x17';
          FUN_00ae71a0(lVar29,&local_e8);
          puVar23 = (undefined4 *)((ulong)bVar4 * 0x20 + *(long *)(puVar7 + 0x58));
          iVar10 = *(int *)(puVar7 + 0x38);
          local_c0 = CONCAT44(local_c0._4_4_,*puVar23);
          local_c8 = (uint)bVar4;
          FUN_0048dcd0(&local_b8,puVar23 + 2);
          local_9c = (uint)uVar9;
          local_a0 = uVar32;
          FUN_0048dcd0(local_98,&local_128);
          local_80 = iVar10 + (uint)uVar8;
          FUN_0048dcd0(local_78,&local_108);
          FUN_0048dcd0(local_60,&local_e8);
          local_48 = 0xffffffff;
          uVar21 = (ulong)uVar8 % (*(ulong *)(puVar7 + 0x18) & 0xffffffff);
          lVar14 = uVar21 * 8;
          for (puVar25 = *(uint **)(*(long *)(puVar7 + 0x10) + uVar21 * 8); puVar25 != (uint *)0x0;
              puVar25 = *(uint **)(puVar25 + 0x24)) {
            if ((uint)uVar8 == *puVar25) goto LAB_00190ae5;
          }
          uVar21 = FUN_00aa5fe0(puVar7 + 0x28,*(ulong *)(puVar7 + 0x18),
                                *(undefined4 *)(puVar7 + 0x20),1);
          uVar28 = uVar21 >> 0x20;
          puVar25 = (uint *)thunk_FUN_00c29480(0x98);
          *puVar25 = (uint)uVar8;
          puVar25[2] = 0xffffffff;
          puVar25[4] = 0xffffffff;
          *(undefined1 *)(puVar25 + 6) = 0;
          *(undefined8 *)((long)puVar25 + 0x2f) = 0xffffff0000000017;
          *(undefined2 *)((long)puVar25 + 0x37) = 0xff;
          *(undefined1 *)((long)puVar25 + 0x4f) = 0x17;
          puVar25[0x14] = 0;
          *(undefined1 *)(puVar25 + 0x16) = 0;
          *(undefined2 *)((long)puVar25 + 0x6f) = 0x17;
          *(undefined8 *)((long)puVar25 + 0x87) = 0xffffffff17;
          *(undefined8 *)((long)puVar25 + 0x8f) = 0;
          *(undefined1 *)((long)puVar25 + 0x97) = 0;
          if ((char)uVar21 == '\0') {
            pvVar15 = *(void **)(puVar7 + 0x10);
          }
          else {
            pvVar15 = (void *)FUN_00c29430();
            memset(pvVar15,0,uVar28 * 8);
            uVar21 = *(ulong *)(puVar7 + 0x18);
            *(undefined8 *)((long)pvVar15 + uVar28 * 8) = 0xffffffffffffffff;
            if (uVar21 != 0) {
              lVar14 = *(long *)(puVar7 + 0x10);
              uVar26 = 0;
              do {
                puVar19 = (undefined8 *)(lVar14 + uVar26 * 8);
                puVar18 = (uint *)*puVar19;
                if (puVar18 != (uint *)0x0) {
                  do {
                    uVar32 = *puVar18;
                    *puVar19 = *(undefined8 *)(puVar18 + 0x24);
                    puVar19 = (undefined8 *)((long)pvVar15 + ((ulong)uVar32 % uVar28) * 8);
                    *(undefined8 *)(puVar18 + 0x24) = *puVar19;
                    lVar14 = *(long *)(puVar7 + 0x10);
                    *puVar19 = puVar18;
                    puVar19 = (undefined8 *)(lVar14 + uVar26 * 8);
                    puVar18 = (uint *)*puVar19;
                  } while (puVar18 != (uint *)0x0);
                  uVar21 = *(ulong *)(puVar7 + 0x18);
                }
                uVar26 = uVar26 + 1;
              } while (uVar26 < uVar21);
              if ((1 < uVar21) && (lVar14 != 0)) {
                HeapInterface::Free();
              }
            }
            *(ulong *)(puVar7 + 0x18) = uVar28;
            *(void **)(puVar7 + 0x10) = pvVar15;
            lVar14 = (ulong)uVar8 % uVar28 << 3;
          }
          *(undefined8 *)(puVar25 + 0x24) = *(undefined8 *)((long)pvVar15 + lVar14);
          *(uint **)(*(long *)(puVar7 + 0x10) + lVar14) = puVar25;
          *(long *)(puVar7 + 0x20) = *(long *)(puVar7 + 0x20) + 1;
LAB_00190ae5:
          puVar25[2] = local_c8;
          puVar25[4] = (uint)local_c0;
          FUN_00126110(puVar25 + 6,&local_b8);
          puVar25[0xc] = local_a0;
          puVar25[0xd] = local_9c;
          FUN_00126110(puVar25 + 0xe,local_98);
          puVar25[0x14] = local_80;
          FUN_00126110(puVar25 + 0x16,local_78);
          FUN_00126110(puVar25 + 0x1c,local_60);
          puVar25[0x22] = (uint)local_48;
          puVar25[0x23] = local_48._4_4_;
          if ((local_49 < '\0') && (local_60[0] != 0)) {
            HeapInterface::Free();
          }
          if ((local_61 < '\0') && (local_78[0] != 0)) {
            HeapInterface::Free();
          }
          if ((local_81 < '\0') && (local_98[0] != 0)) {
            HeapInterface::Free();
          }
          if ((local_a1 < '\0') && (local_b8 != 0)) {
            HeapInterface::Free();
          }
          if ((local_d1 < '\0') && (CONCAT71(uStack_e7,local_e8) != 0)) {
            HeapInterface::Free();
          }
          if ((local_f1 < '\0') && (CONCAT71(uStack_107,local_108) != 0)) {
            HeapInterface::Free();
          }
          if ((local_111 < '\0') && (CONCAT71(uStack_127,local_128) != 0)) {
            HeapInterface::Free();
          }
          iVar31 = iVar31 + 1;
        } while (iVar31 < *(int *)(puVar7 + 0x40));
      }
      uVar11 = FUN_0018fff0(lVar29);
      *puVar7 = 1;
      *(undefined4 *)(puVar7 + 0x60) = uVar11;
    }
    iVar31 = 0;
    uVar27 = 0xffffffff;
    if (0 < *(int *)(puVar7 + 0x40)) {
      do {
        bVar4 = *(byte *)(*(long *)(lVar6 + 0xe0) + *(long *)(lVar6 + 0xe8));
        uVar21 = (ulong)bVar4;
        if ((char)bVar4 < '\0') {
          iVar10 = FUN_0018f870(lVar29);
          uVar21 = (ulong)(iVar10 - 0x8000);
        }
        else {
          *(long *)(lVar6 + 0xe8) = *(long *)(lVar6 + 0xe8) + 1;
        }
        uVar8 = FUN_0018f870(lVar29);
        uVar32 = (uint)uVar8;
        if (uVar8 == 0xffff) {
          uVar32 = (uint)uVar27;
        }
        puVar25 = *(uint **)(*(long *)(puVar7 + 0x10) +
                            ((uVar21 & 0xffff) % (*(ulong *)(puVar7 + 0x18) & 0xffffffff)) * 8);
        if (puVar25 != (uint *)0x0) {
          uVar5 = *puVar25;
          while (uVar5 != ((uint)uVar21 & 0xffff)) {
            puVar25 = *(uint **)(puVar25 + 0x24);
            if (puVar25 == (uint *)0x0) goto LAB_001903c7;
            uVar5 = *puVar25;
          }
          if (*(uint **)(*(long *)(puVar7 + 0x10) + *(ulong *)(puVar7 + 0x18) * 8) != puVar25) {
            puVar25[0x22] = uVar32;
          }
        }
LAB_001903c7:
        iVar31 = iVar31 + 1;
      } while (iVar31 < *(int *)(puVar7 + 0x40));
    }
    pvVar22 = *(void **)(lVar6 + 0x128);
    pvVar15 = *(void **)(lVar6 + 0x120);
    uVar21 = (ulong)*(int *)(*(long *)(lVar6 + 0x10) + 0x40);
    lVar29 = (long)pvVar22 - (long)pvVar15;
    if (uVar21 != 0xffffffffffffffff) goto LAB_001901c8;
LAB_001903fe:
    if (lVar29 == 0) {
      pvVar13 = (void *)0x0;
    }
    else {
      pvVar13 = (void *)FUN_00c29480(lVar29);
    }
    lVar29 = lVar29 + (long)pvVar13;
    if (pvVar22 == pvVar15) {
      pvVar15 = *(void **)(lVar6 + 0x120);
    }
    else {
      pvVar13 = memmove(pvVar13,pvVar15,(long)pvVar22 - (long)pvVar15);
      pvVar15 = *(void **)(lVar6 + 0x120);
    }
LAB_001901e6:
    *(void **)(lVar6 + 0x120) = pvVar13;
    *(long *)(lVar6 + 0x128) = lVar29;
    *(long *)(lVar6 + 0x130) = lVar29;
    if (pvVar15 != (void *)0x0) {
      HeapInterface::Free(pvVar15);
    }
  }
  else {
    uVar21 = (ulong)*(int *)(puVar7 + 0x40);
    if (uVar21 == 0xffffffffffffffff) {
      lVar29 = 0;
      pvVar13 = (void *)0x0;
      goto LAB_001901e6;
    }
    lVar29 = 0;
    pvVar22 = pvVar15;
LAB_001901c8:
    if (uVar21 <= (ulong)(lVar29 >> 3)) {
      if (uVar21 != 0) {
        if (uVar21 < (ulong)(lVar29 >> 3)) {
          pvVar22 = (void *)((long)pvVar15 + uVar21 * 8);
          *(void **)(lVar6 + 0x128) = pvVar22;
          lVar29 = (long)pvVar22 - (long)pvVar15;
        }
        goto LAB_001903fe;
      }
      lVar29 = 0;
      pvVar13 = (void *)0x0;
      goto LAB_001901e6;
    }
    if (uVar21 == 0) {
      pvVar13 = (void *)0x0;
    }
    else {
      pvVar13 = (void *)FUN_00c29480(uVar21 * 8);
    }
    if (pvVar15 != pvVar22) {
      memmove(pvVar13,pvVar15,(long)pvVar22 - (long)pvVar15);
    }
    lVar29 = 0;
    if (*(long *)(lVar6 + 0x120) != 0) {
      HeapInterface::Free();
      lVar29 = *(long *)(lVar6 + 0x120);
    }
    *(void **)(lVar6 + 0x120) = pvVar13;
    *(long *)(lVar6 + 0x128) = (*(long *)(lVar6 + 0x128) - lVar29) + (long)pvVar13;
    *(void **)(lVar6 + 0x130) = (void *)((long)pvVar13 + uVar21 * 8);
  }
  pcVar24 = *(char **)(lVar6 + 0x10);
  iVar31 = *(int *)(pcVar24 + 0x38);
  iVar10 = *(int *)(pcVar24 + 0x3c);
  if (iVar31 <= iVar10) {
    do {
      if ((*pcVar24 != '\0') && (*(int *)(pcVar24 + 0x38) <= iVar31)) {
        uVar32 = iVar31 - *(int *)(pcVar24 + 0x38);
        puVar25 = *(uint **)(*(long *)(pcVar24 + 0x10) +
                            ((ulong)uVar32 % (*(ulong *)(pcVar24 + 0x18) & 0xffffffff)) * 8);
        if (puVar25 != (uint *)0x0) {
          uVar5 = *puVar25;
          while (uVar5 != uVar32) {
            puVar25 = *(uint **)(puVar25 + 0x24);
            if (puVar25 == (uint *)0x0) goto LAB_00190275;
            uVar5 = *puVar25;
          }
          if ((puVar25 != *(uint **)(*(long *)(pcVar24 + 0x10) + *(ulong *)(pcVar24 + 0x18) * 8)) &&
             (puVar25 = puVar25 + 2, puVar25 != (uint *)0x0)) {
            puVar19 = *(undefined8 **)(lVar6 + 0x128);
            if (puVar19 < *(undefined8 **)(lVar6 + 0x130)) {
              *(undefined8 **)(lVar6 + 0x128) = puVar19 + 1;
              *puVar19 = puVar25;
            }
            else {
              __src = *(undefined8 **)(lVar6 + 0x120);
              uVar21 = (long)puVar19 - (long)__src >> 3;
              if (uVar21 == 0) {
                lVar29 = 8;
LAB_001904ba:
                __dest = (undefined8 *)FUN_00c29480(lVar29);
                puVar19 = *(undefined8 **)(lVar6 + 0x128);
                __src = *(undefined8 **)(lVar6 + 0x120);
                lVar29 = lVar29 + (long)__dest;
              }
              else {
                if ((uVar21 & 0x7fffffffffffffff) != 0) {
                  lVar29 = uVar21 << 4;
                  goto LAB_001904ba;
                }
                lVar29 = 0;
                __dest = (undefined8 *)0x0;
              }
              puVar17 = __dest;
              if (puVar19 != __src) {
                __n = (long)puVar19 - (long)__src;
                memmove(__dest,__src,__n);
                __src = *(undefined8 **)(lVar6 + 0x120);
                puVar17 = (undefined8 *)((long)__dest + __n);
              }
              *puVar17 = puVar25;
              if (__src != (undefined8 *)0x0) {
                HeapInterface::Free(__src);
              }
              pcVar24 = *(char **)(lVar6 + 0x10);
              *(undefined8 **)(lVar6 + 0x120) = __dest;
              *(undefined8 **)(lVar6 + 0x128) = puVar17 + 1;
              *(long *)(lVar6 + 0x130) = lVar29;
              iVar10 = *(int *)(pcVar24 + 0x3c);
            }
          }
        }
      }
LAB_00190275:
      iVar31 = iVar31 + 1;
    } while (iVar31 <= iVar10);
  }
  *(undefined1 *)(lVar6 + 0xb0) = 0;
  lVar12 = std__chrono___V2__steady_clock__now();
  lVar29 = *(long *)(lVar6 + 0xe0);
  lVar14 = *(long *)(lVar6 + 0xd8);
  *(undefined8 *)(lVar6 + 0xe8) = 0;
  *(undefined8 *)(lVar6 + 0xd8) = 0;
  *(undefined8 *)(lVar6 + 0xe0) = 0;
  *(long *)(lVar6 + 0xb8) = lVar12 / 1000000;
  if ((lVar29 != 0) && (lVar14 != 0)) {
    HeapInterface::Free();
  }
LAB_001900b0:
  return &DAT_015d3620;
}


```

## `jag::packethandlers::SiteSettings::UPDATE_SITESETTINGS_thunk` @ 001a65a0
```c

/* Setting prototype: void UPDATE_SITESETTINGS_thunk(undefined8 param_1, undefined8 param_2,
   undefined4 * param_3) */

void jag::packethandlers::SiteSettings::UPDATE_SITESETTINGS_thunk
               (undefined8 param_1,undefined8 param_2,undefined4 *param_3)

{
                    /* 948 ServerProt op26 thunk -> jag::packethandlers::Friends::UPDATE_FRIENDLIST
                       @ 0x001a46a0 (948-5). CONFIRMED friend-list packet (NOT UPDATE_SITESETTINGS).
                       See docs/net/serverprot/948-friends-social.md. Thunk: MOV EDX,[RDX]; JMP
                       UPDATE_FRIENDLIST. */
  Friends::UPDATE_FRIENDLIST(param_1,param_2,*param_3);
  return;
}


```

## `jag::packethandlers::ZoneUpdates::UNKNOWN_op173_handler` @ 001ae2c0
```c

/* Setting prototype: undefined * UNKNOWN_op173_handler(long * thisPtr, long packetPtr) */

undefined * jag::packethandlers::ZoneUpdates::UNKNOWN_op173_handler(long *thisPtr,long packetPtr)

{
  char cVar1;
  uint uVar2;
  undefined1 (*pauVar3) [16];
  long *__dest;
  long *plVar4;
  long *plVar5;
  uint uVar6;
  undefined1 (*pauVar7) [16];
  code *pcVar8;
  undefined *puVar9;
  long *plVar10;
  long lVar11;
  ulong uVar12;
  long lVar13;
  long *plVar14;
  bool bVar15;
  byte bVar16;
  
                    /* UNKNOWN op173 packet handler. Reads ulong + worldId, then a
                       byte-discriminated (switch 1..14) loop allocating per-type delta objects,
                       applies them to a target, and bumps a counter at +0x94. */
  bVar16 = 0;
  lVar13 = *(long *)((long)&__DT_RELA[0xd00].r_offset + *thisPtr);
  lVar11 = *(long *)((long)&__DT_RELA[0xca6].r_info + *thisPtr);
  *(undefined4 *)(lVar13 + 0x150) = *(undefined4 *)(*(long *)(lVar13 + 0x148) + 0x10);
  Packet::gT_ulong(packetPtr);
  lVar13 = *(long *)(packetPtr + 0x18);
  bVar15 = DAT_01050dc0 != 0x3020100;
  *(long *)(packetPtr + 0x18) = lVar13 + 4;
  uVar6 = *(uint *)(*(long *)(packetPtr + 0x10) + lVar13);
  *(long *)(packetPtr + 0x18) = lVar13 + 5;
  cVar1 = *(char *)(*(long *)(packetPtr + 0x10) + 4 + lVar13);
  uVar2 = uVar6 >> 0x18 | (uVar6 & 0xff0000) >> 8 | (uVar6 & 0xff00) << 8 | uVar6 << 0x18;
  if (bVar15) {
    uVar2 = uVar6;
  }
  if (cVar1 == '\0') {
    lVar13 = *(long *)(*(long *)((long)&__DT_RELA[0xcfc].r_addend + *thisPtr) + 8);
    if (lVar13 == 0) {
      return &DAT_015d35c0;
    }
    uVar6 = *(uint *)(lVar13 + 0x94);
    plVar4 = (long *)0x0;
    __dest = (long *)0x0;
    if (uVar6 != uVar2) {
      return &DAT_015d3620;
    }
LAB_001aea89:
    puVar9 = &DAT_015d3620;
    *(uint *)(lVar13 + 0x94) = uVar6 + 1;
  }
  else {
    plVar14 = (long *)0x0;
    plVar4 = (long *)0x0;
    plVar10 = (long *)0x0;
    do {
      __dest = plVar10;
                    /* WARNING: Could not find normalized switch variable to match jumptable */
      switch(cVar1) {
      case '\x01':
        pauVar3 = (undefined1 (*) [16])operator_new(0xb8);
        pcVar8 = (code *)PTR_FUN_0136b6a8;
        pauVar7 = pauVar3;
        for (lVar13 = 0x17; lVar13 != 0; lVar13 = lVar13 + -1) {
          *(undefined8 *)*pauVar7 = 0;
          pauVar7 = (undefined1 (*) [16])(pauVar7[-(ulong)bVar16] + 8);
        }
        *(undefined ***)*pauVar3 = &PTR_FUN_0136b698;
        pauVar3[3][7] = 0x17;
        *(undefined ***)(pauVar3[4] + 8) = &PTR_FUN_0136b598;
        *(undefined8 *)pauVar3[6] = 1;
        *(undefined8 *)pauVar3[7] = 0x400000003f800000;
        *(undefined8 **)(pauVar3[5] + 8) = &DAT_01393d40;
        *(undefined ***)pauVar3[8] = &PTR_FUN_0136b598;
        *(undefined8 *)(pauVar3[9] + 8) = 1;
        *(undefined8 *)(pauVar3[10] + 8) = 0x400000003f800000;
        *(undefined8 **)pauVar3[9] = &DAT_01393d40;
        break;
      case '\x02':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined2 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004bdb30;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363e98;
        break;
      case '\x03':
        pauVar3 = (undefined1 (*) [16])operator_new(0x20);
        (*pauVar3)[8] = 0;
        pauVar3[1][0xf] = 0x17;
        pcVar8 = (code *)PTR_FUN_0136b678;
        *(undefined ***)*pauVar3 = &PTR_FUN_0136b668;
        break;
      case '\x04':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined2 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004bdb00;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363e68;
        break;
      case '\x05':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined2 *)(*pauVar3 + 8) = 0;
        (*pauVar3)[10] = 0;
        pcVar8 = FUN_004bd6e0;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363e38;
        break;
      case '\x06':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined4 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004bd670;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363e08;
        break;
      case '\a':
        pauVar3 = (undefined1 (*) [16])operator_new();
        *(undefined2 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004bd610;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363dd8;
        break;
      case '\b':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        (*pauVar3)[10] = 0;
        *(undefined2 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004bd5b0;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363da8;
        break;
      case '\t':
        pauVar3 = (undefined1 (*) [16])operator_new(8);
        pcVar8 = FUN_009a3b30;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363d78;
        break;
      case '\n':
        pauVar3 = (undefined1 (*) [16])operator_new(8);
        *(undefined ***)*pauVar3 = &PTR_FUN_01363d48;
        pcVar8 = FUN_009a3b30;
        break;
      case '\v':
        pauVar3 = (undefined1 (*) [16])operator_new(0xc0);
        pcVar8 = (code *)PTR_FUN_0136b648;
        pauVar7 = pauVar3;
        for (lVar13 = 0x18; lVar13 != 0; lVar13 = lVar13 + -1) {
          *(undefined8 *)*pauVar7 = 0;
          pauVar7 = (undefined1 (*) [16])(pauVar7[-(ulong)bVar16] + 8);
        }
        *(undefined ***)*pauVar3 = &PTR_FUN_0136b638;
        pauVar3[3][0xf] = 0x17;
        *(undefined ***)pauVar3[5] = &PTR_FUN_0136b598;
        *(undefined8 *)(pauVar3[6] + 8) = 1;
        *(undefined8 *)(pauVar3[7] + 8) = 0x400000003f800000;
        *(undefined8 **)pauVar3[6] = &DAT_01393d40;
        *(undefined ***)(pauVar3[8] + 8) = &PTR_FUN_0136b598;
        *(undefined8 *)pauVar3[10] = 1;
        *(undefined8 *)pauVar3[0xb] = 0x400000003f800000;
        *(undefined8 **)(pauVar3[9] + 8) = &DAT_01393d40;
        break;
      case '\f':
        pauVar3 = (undefined1 (*) [16])operator_new(0x30);
        pcVar8 = (code *)PTR_FUN_0136b618;
        *pauVar3 = (undefined1  [16])0x0;
        *(undefined ***)*pauVar3 = &PTR_FUN_0136b608;
        *(undefined4 *)(*pauVar3 + 8) = 0;
        pauVar3[2] = (undefined1  [16])0x0;
        pauVar3[1] = (undefined1  [16])0x0;
        pauVar3[2][8] = 4;
        break;
      case '\r':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        *(undefined8 *)(*pauVar3 + 8) = 0;
        pcVar8 = FUN_004cf9d0;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363d18;
        break;
      case '\x0e':
        pauVar3 = (undefined1 (*) [16])operator_new(0x10);
        pcVar8 = FUN_004bd550;
        *(undefined2 *)(*pauVar3 + 8) = 0;
        (*pauVar3)[10] = 0;
        *(undefined ***)*pauVar3 = &PTR_FUN_01363ce8;
        break;
      default:
        goto switchD_002ae37c_default;
      }
      (*pcVar8)(pauVar3,packetPtr,lVar11 + 0xd0);
      if (plVar4 < plVar14) {
        *plVar4 = (long)pauVar3;
        plVar5 = plVar4;
      }
      else {
        uVar12 = (long)plVar4 - (long)plVar10 >> 3;
        if (uVar12 == 0) {
          lVar13 = 8;
LAB_001aeac0:
          __dest = (long *)FUN_00c29480(lVar13);
          plVar14 = (long *)(lVar13 + (long)__dest);
        }
        else {
          if ((uVar12 & 0x7fffffffffffffff) != 0) {
            lVar13 = uVar12 << 4;
            goto LAB_001aeac0;
          }
          plVar14 = (long *)0x0;
          __dest = (long *)0x0;
        }
        plVar5 = __dest;
        if (plVar4 != plVar10) {
          __dest = memmove(__dest,plVar10,(long)plVar4 - (long)plVar10);
          plVar5 = (long *)(((long)plVar4 - (long)plVar10) + (long)__dest);
        }
        *plVar5 = (long)pauVar3;
        if (plVar10 != (long *)0x0) {
          HeapInterface::Free(plVar10);
        }
      }
      plVar4 = plVar5 + 1;
      lVar13 = *(long *)(packetPtr + 0x18);
      *(long *)(packetPtr + 0x18) = lVar13 + 1;
      cVar1 = *(char *)(*(long *)(packetPtr + 0x10) + lVar13);
      plVar10 = __dest;
    } while (cVar1 != '\0');
switchD_002ae37c_default:
    puVar9 = &DAT_015d35c0;
    lVar13 = *(long *)(*(long *)((long)&__DT_RELA[0xcfc].r_addend + *thisPtr) + 8);
    if (lVar13 != 0) {
      uVar6 = *(uint *)(lVar13 + 0x94);
      puVar9 = &DAT_015d3620;
      if (uVar6 == uVar2) {
        if (__dest != plVar4) {
          lVar11 = *(long *)((long)&__DT_RELA[0xca6].r_info + *thisPtr) + 0xd0;
          uVar6 = (int)((ulong)((long)plVar4 + (-8 - (long)__dest)) >> 3) + 1U & 7;
          plVar14 = __dest;
          if (uVar6 == 0) goto LAB_001ae9ea;
          if (uVar6 != 1) {
            plVar10 = __dest;
            if (uVar6 != 2) {
              if (uVar6 != 3) {
                if (uVar6 != 4) {
                  if (uVar6 != 5) {
                    if (uVar6 != 6) {
                      plVar10 = __dest + 1;
                      (**(code **)(*(long *)*__dest + 0x18))((long *)*__dest,lVar13,lVar11);
                    }
                    plVar14 = plVar10 + 1;
                    (**(code **)(*(long *)*plVar10 + 0x18))((long *)*plVar10,lVar13,lVar11);
                  }
                  plVar10 = plVar14 + 1;
                  (**(code **)(*(long *)*plVar14 + 0x18))((long *)*plVar14,lVar13,lVar11);
                }
                plVar14 = plVar10 + 1;
                (**(code **)(*(long *)*plVar10 + 0x18))((long *)*plVar10,lVar13,lVar11);
              }
              plVar10 = plVar14 + 1;
              (**(code **)(*(long *)*plVar14 + 0x18))((long *)*plVar14,lVar13,lVar11);
            }
            plVar14 = plVar10 + 1;
            (**(code **)(*(long *)*plVar10 + 0x18))((long *)*plVar10,lVar13,lVar11);
          }
          (**(code **)(*(long *)*plVar14 + 0x18))((long *)*plVar14,lVar13,lVar11);
          for (plVar14 = plVar14 + 1; plVar4 != plVar14; plVar14 = plVar14 + 8) {
LAB_001ae9ea:
            (**(code **)(*(long *)*plVar14 + 0x18))((long *)*plVar14,lVar13,lVar11);
            (**(code **)(*(long *)plVar14[1] + 0x18))((long *)plVar14[1],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[2] + 0x18))((long *)plVar14[2],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[3] + 0x18))((long *)plVar14[3],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[4] + 0x18))((long *)plVar14[4],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[5] + 0x18))((long *)plVar14[5],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[6] + 0x18))((long *)plVar14[6],lVar13,lVar11);
            (**(code **)(*(long *)plVar14[7] + 0x18))((long *)plVar14[7],lVar13,lVar11);
          }
          uVar6 = *(uint *)(lVar13 + 0x94);
        }
        goto LAB_001aea89;
      }
    }
  }
  if (plVar4 != __dest) {
    uVar6 = (int)((ulong)((long)plVar4 + (-8 - (long)__dest)) >> 3) + 1U & 7;
    plVar14 = __dest;
    if (uVar6 != 0) {
      if (uVar6 != 1) {
        if (uVar6 != 2) {
          if (uVar6 != 3) {
            if (uVar6 != 4) {
              if (uVar6 != 5) {
                if (uVar6 != 6) {
                  if ((long *)*__dest != (long *)0x0) {
                    (**(code **)(*(long *)*__dest + 8))();
                  }
                  plVar14 = __dest + 1;
                }
                if ((long *)*plVar14 != (long *)0x0) {
                  (**(code **)(*(long *)*plVar14 + 8))();
                }
                plVar14 = plVar14 + 1;
              }
              if ((long *)*plVar14 != (long *)0x0) {
                (**(code **)(*(long *)*plVar14 + 8))();
              }
              plVar14 = plVar14 + 1;
            }
            if ((long *)*plVar14 != (long *)0x0) {
              (**(code **)(*(long *)*plVar14 + 8))();
            }
            plVar14 = plVar14 + 1;
          }
          if ((long *)*plVar14 != (long *)0x0) {
            (**(code **)(*(long *)*plVar14 + 8))();
          }
          plVar14 = plVar14 + 1;
        }
        if ((long *)*plVar14 != (long *)0x0) {
          (**(code **)(*(long *)*plVar14 + 8))();
        }
        plVar14 = plVar14 + 1;
      }
      if ((long *)*plVar14 != (long *)0x0) {
        (**(code **)(*(long *)*plVar14 + 8))();
      }
      plVar14 = plVar14 + 1;
      if (plVar14 == plVar4) goto LAB_001ae58d;
    }
    do {
      if ((long *)*plVar14 != (long *)0x0) {
        (**(code **)(*(long *)*plVar14 + 8))();
      }
      if ((long *)plVar14[1] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[1] + 8))();
      }
      if ((long *)plVar14[2] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[2] + 8))();
      }
      if ((long *)plVar14[3] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[3] + 8))();
      }
      if ((long *)plVar14[4] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[4] + 8))();
      }
      if ((long *)plVar14[5] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[5] + 8))();
      }
      if ((long *)plVar14[6] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[6] + 8))();
      }
      if ((long *)plVar14[7] != (long *)0x0) {
        (**(code **)(*(long *)plVar14[7] + 8))();
      }
      plVar14 = plVar14 + 8;
    } while (plVar14 != plVar4);
  }
LAB_001ae58d:
  if (__dest != (long *)0x0) {
    HeapInterface::Free(__dest);
  }
  return puVar9;
}


```

## `jag::packethandlers::WorldData::SWITCH_WORLD` @ 001aeba0
```c

undefined * jag::packethandlers::WorldData::SWITCH_WORLD(long *param_1,long param_2)

{
  ushort uVar1;
  undefined2 uVar2;
  undefined2 uVar3;
  undefined4 uVar4;
  long lVar5;
  long lVar6;
  long *plVar7;
  undefined8 *puVar8;
  long *plVar9;
  long lVar10;
  char *pcVar11;
  long lVar12;
  ushort uVar13;
  undefined1 *puVar14;
  long lVar15;
  ushort uVar16;
  bool bVar17;
  ushort local_62;
  undefined1 local_58;
  undefined7 uStack_57;
  long local_50;
  char local_41 [17];
  
                    /* op213 handler jag::packethandlers::WorldData::SWITCH_WORLD(client,packet).
                       Wire: g2 worldId, gStr host, g2 portA, g2 portB, p1 reconnectFlag. (1) Saves
                       CURRENT target(+0x20) into the 'previous' slot wrapper@+0x48/struct@+0x50,
                       and stores reconnectFlag at worldSwitcher+0x58. (2) Builds a new CURRENT
                       target from the packet fields -> wrapper@+0x18/struct@+0x20. (3) Calls
                       Client::SetMainState(client, 0x25). State 0x25 -> OnMainStateTransition ->
                       StartWorldLogin (login type 2) -> connect using CURRENT target(+0x20). This
                       is the IN-GAME WORLD-HOP path; it overwrites the login-response target with
                       packet-supplied host:port and forces the world-switch transition. [VERIFIED
                       rs2client.948-5 @0x001aeba0] */
  lVar10 = *(long *)(param_2 + 0x18);
  bVar17 = DAT_01050dc0 == 0x3020100;
  *(long *)(param_2 + 0x18) = lVar10 + 2;
  uVar13 = *(ushort *)(*(long *)(param_2 + 0x10) + lVar10);
  if (bVar17) {
    local_58 = 0;
    local_41[0] = '\x17';
    uVar13 = uVar13 << 8 | uVar13 >> 8;
    FUN_00afd8d0(param_2,&local_58);
    lVar12 = *(long *)(param_2 + 0x18);
    lVar15 = *(long *)(param_2 + 0x10);
    lVar10 = lVar12 + 4;
    *(long *)(param_2 + 0x18) = lVar12 + 2;
    uVar16 = *(ushort *)(lVar15 + lVar12);
    *(long *)(param_2 + 0x18) = lVar10;
    uVar1 = *(ushort *)(lVar15 + 2 + lVar12);
    local_62 = uVar16 << 8 | uVar16 >> 8;
    uVar16 = uVar1 << 8 | uVar1 >> 8;
  }
  else {
    local_58 = 0;
    local_41[0] = '\x17';
    FUN_00afd8d0(param_2,&local_58);
    lVar12 = *(long *)(param_2 + 0x18);
    lVar15 = *(long *)(param_2 + 0x10);
    lVar10 = lVar12 + 4;
    *(long *)(param_2 + 0x18) = lVar12 + 2;
    local_62 = *(ushort *)(lVar15 + lVar12);
    *(long *)(param_2 + 0x18) = lVar10;
    uVar16 = *(ushort *)(lVar15 + 2 + lVar12);
  }
  puVar14 = &local_58;
  *(long *)(param_2 + 0x18) = lVar10 + 1;
  lVar12 = *(long *)((long)&__DT_RELA[0xd01].r_offset + *param_1);
  lVar5 = *(long *)(lVar12 + 0x20);
  lVar6 = *(long *)(lVar12 + 8);
  *(bool *)(lVar12 + 0x58) = *(char *)(lVar15 + lVar10) == '\x01';
  uVar2 = *(undefined2 *)(lVar5 + 0x28);
  uVar3 = *(undefined2 *)(lVar5 + 0x2a);
  uVar4 = *(undefined4 *)(lVar5 + 8);
  plVar9 = (long *)(lVar5 + 0x10);
  plVar7 = (long *)FUN_00c29480(0x30);
  *plVar7 = lVar6 + 0x195e8;
  *(undefined4 *)((long)plVar7 + 0xc) = 0;
  *(undefined1 *)(plVar7 + 2) = 0;
  *(undefined1 *)((long)plVar7 + 0x27) = 0x17;
  *(undefined2 *)(plVar7 + 5) = uVar2;
  *(undefined4 *)(plVar7 + 1) = uVar4;
  *(undefined2 *)((long)plVar7 + 0x2a) = uVar3;
  *(undefined1 *)((long)plVar7 + 0x2c) = 1;
  if (plVar9 != plVar7 + 2) {
    if (*(char *)(lVar5 + 0x27) < '\0') {
      plVar9 = *(long **)(lVar5 + 0x10);
      lVar10 = *(long *)(lVar5 + 0x18) + (long)plVar9;
    }
    else {
      lVar10 = (long)plVar9 + (0x17 - (long)*(char *)(lVar5 + 0x27));
    }
    FUN_00493d20(plVar7 + 2,plVar9,lVar10);
  }
  puVar8 = (undefined8 *)FUN_00c29480(0x18);
  puVar8[2] = plVar7;
  puVar8[1] = 0x100000001;
  *puVar8 = &PTR_FUN_01365980;
  lVar10 = *(long *)(lVar12 + 0x48);
  *(long **)(lVar12 + 0x50) = plVar7;
  *(undefined8 **)(lVar12 + 0x48) = puVar8;
  if (lVar10 != 0) {
    ref_counter_base::DecRef();
  }
  lVar10 = *param_1;
  lVar15 = *(long *)((long)&__DT_RELA[0xd01].r_offset + lVar10);
  lVar12 = *(long *)(lVar15 + 0x20);
  if (lVar12 == 0) {
    lVar10 = *(long *)(lVar15 + 8);
    plVar9 = (long *)operator_new(0x30);
    *(ushort *)((long)plVar9 + 0x2a) = uVar16;
    *(uint *)(plVar9 + 1) = (uint)uVar13;
    *(undefined4 *)((long)plVar9 + 0xc) = 0;
    *(undefined1 *)(plVar9 + 2) = 0;
    *plVar9 = lVar10 + 0x195e8;
    *(undefined1 *)((long)plVar9 + 0x27) = 0x17;
    *(ushort *)(plVar9 + 5) = local_62;
    *(undefined1 *)((long)plVar9 + 0x2c) = 1;
    FUN_00493e00(plVar9 + 2,puVar14);
    puVar8 = (undefined8 *)operator_new(0x18);
    puVar8[1] = 0x100000001;
    puVar8[2] = plVar9;
    *puVar8 = &PTR_FUN_01365980;
    lVar10 = *(long *)(lVar15 + 0x18);
    *(long **)(lVar15 + 0x20) = plVar9;
    *(undefined8 **)(lVar15 + 0x18) = puVar8;
    if (lVar10 != 0) {
      ref_counter_base::DecRef();
    }
    lVar10 = *param_1;
  }
  else {
    *(uint *)(lVar12 + 8) = (uint)uVar13;
    if ((undefined1 *)(lVar12 + 0x10) != puVar14) {
      if (local_41[0] < '\0') {
        puVar14 = (undefined1 *)CONCAT71(uStack_57,local_58);
        pcVar11 = puVar14 + local_50;
      }
      else {
        pcVar11 = local_41 + -(long)local_41[0];
      }
      FUN_00493d20((undefined1 *)(lVar12 + 0x10),puVar14,pcVar11);
      lVar10 = *param_1;
      lVar12 = *(long *)(lVar15 + 0x20);
    }
    *(ushort *)(lVar12 + 0x2a) = uVar16;
    *(ushort *)(lVar12 + 0x28) = local_62;
  }
  Client::SetMainState(lVar10,0x25);
  if ((local_41[0] < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
    HeapInterface::Free();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Misc::LOGOUT` @ 001ba410
```c

undefined * jag::packethandlers::Misc::LOGOUT(long *param_1)

{
  long *plVar1;
  int *piVar2;
  int iVar3;
  undefined4 uVar4;
  undefined4 *puVar5;
  long lVar6;
  long lVar7;
  long *plVar8;
  long *plVar9;
  undefined8 uVar10;
  long *plVar11;
  long lVar12;
  uint uVar13;
  undefined1 local_58;
  undefined7 uStack_57;
  char local_41;
  
  lVar12 = *param_1;
  puVar5 = *(undefined4 **)
            (*(long *)(*(long *)((long)&__DT_RELA[0xca5].r_addend + lVar12) + 0x10) + 0x1a8);
  if (puVar5 != (undefined4 *)0x0) {
    ClientStream::Close(puVar5 + 2);
    *puVar5 = 0;
    lVar12 = *param_1;
    *(undefined8 *)(puVar5 + 0x28) = 0;
    *(undefined8 *)(puVar5 + 0x24) = *(undefined8 *)(puVar5 + 0x1e);
    *(undefined8 *)(puVar5 + 0x26) = *(undefined8 *)(puVar5 + 0x1e);
    *(undefined8 *)(puVar5 + 0x34) = *(undefined8 *)(puVar5 + 0x32);
  }
  LoginManager::ResetLoginState(*(undefined8 *)((long)&__DT_RELA[0xcfa].r_info + lVar12));
  Client::SetMainState(*param_1,0);
  lVar12 = *(long *)((long)&__DT_RELA[0xca5].r_addend + *param_1);
  lVar6 = *(long *)((long)&__DT_SYMTAB[0x171].st_value + lVar12);
  *(undefined8 *)((long)&__DT_SYMTAB[0x171].st_value + lVar12) = 0;
  if (lVar6 != 0) {
    plVar11 = *(long **)(lVar6 + 0x18);
    if ((plVar11 != (long *)0x0) && (lVar7 = *(long *)(lVar6 + 0x10), lVar7 != 0)) {
      plVar8 = plVar11 + lVar7;
      uVar13 = (int)(lVar7 * 8 - 8U >> 3) + 1U & 7;
      if (uVar13 == 0) goto LAB_001ba679;
      if (uVar13 != 1) {
        if (uVar13 != 2) {
          if (uVar13 != 3) {
            if (uVar13 != 4) {
              if (uVar13 != 5) {
                if (uVar13 != 6) {
                  lVar7 = *plVar11;
                  if (lVar7 != 0) {
                    if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
                      HeapInterface::Free();
                    }
                    HeapInterface::Free(lVar7,0x28);
                  }
                  plVar11 = plVar11 + 1;
                }
                lVar7 = *plVar11;
                if (lVar7 != 0) {
                  if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
                    HeapInterface::Free();
                  }
                  HeapInterface::Free(lVar7,0x28);
                }
                plVar11 = plVar11 + 1;
              }
              lVar7 = *plVar11;
              if (lVar7 != 0) {
                if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
                  HeapInterface::Free();
                }
                HeapInterface::Free(lVar7,0x28);
              }
              plVar11 = plVar11 + 1;
            }
            lVar7 = *plVar11;
            if (lVar7 != 0) {
              if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
                HeapInterface::Free();
              }
              HeapInterface::Free(lVar7,0x28);
            }
            plVar11 = plVar11 + 1;
          }
          lVar7 = *plVar11;
          if (lVar7 != 0) {
            if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
              HeapInterface::Free();
            }
            HeapInterface::Free(lVar7,0x28);
          }
          plVar11 = plVar11 + 1;
        }
        lVar7 = *plVar11;
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        plVar11 = plVar11 + 1;
      }
      lVar7 = *plVar11;
      if (lVar7 != 0) {
        if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
          HeapInterface::Free();
        }
        HeapInterface::Free(lVar7,0x28);
      }
      for (plVar11 = plVar11 + 1; plVar8 != plVar11; plVar11 = plVar11 + 8) {
LAB_001ba679:
        lVar7 = *plVar11;
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[1];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[2];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[3];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[4];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[5];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[6];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
        lVar7 = plVar11[7];
        if (lVar7 != 0) {
          if ((*(long *)(lVar7 + 0x10) != 0) && (*(long *)(lVar7 + 0x18) != 0)) {
            HeapInterface::Free();
          }
          HeapInterface::Free(lVar7,0x28);
        }
      }
      if ((*(long *)(lVar6 + 0x18) != 0) && (*(long *)(lVar6 + 0x10) != 0)) {
        HeapInterface::Free();
      }
    }
    HeapInterface::Free(lVar6,0x20);
  }
  (&__DT_SYMTAB[0x171].st_info)[lVar12] = 0;
  lVar12 = *param_1;
  lVar6 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar12);
  plVar8 = *(long **)(lVar6 + 0x70);
  plVar11 = *(long **)(lVar6 + 0x68);
  if (plVar8 == plVar11) {
LAB_001ba961:
    lVar12 = *(long *)((long)&__DT_RELA[0xcff].r_offset + lVar12);
    *(long **)(lVar6 + 0x70) = plVar11;
    lVar12 = *(long *)((long)&__DT_RELA[0xcf5].r_addend + *(long *)(lVar12 + 8));
    FUN_007b2050(lVar12 + 0x18,*(undefined8 *)(lVar12 + 0x20),*(undefined8 *)(lVar12 + 0x28));
    lVar6 = *param_1;
    uVar4 = *(undefined4 *)(lVar12 + 0x10);
    *(undefined8 *)(lVar12 + 0x30) = 0;
    *(long *)((long)&__DT_RELA[0x97e].r_info + lVar12) = lVar12 + 0x140c8;
    *(long *)((long)&__DT_RELA[0x97e].r_offset + lVar12) = lVar12 + 0x140c8;
    plVar11 = *(long **)((long)&__DT_RELA[0xca6].r_info + lVar6);
    *(undefined4 *)(lVar12 + 0x14) = uVar4;
    (**(code **)(*plVar11 + 0x20))();
    local_58 = 0;
    local_41 = '\x17';
    uVar10 = *(undefined8 *)((long)&__DT_RELA[0xcfa].r_info + *param_1);
    FUN_00c294d0(&local_58,&DAT_00fba3bd);
    LoginManager::StartWorldLogin(uVar10,0,&local_58,0);
    if ((local_41 < '\0') && (CONCAT71(uStack_57,local_58) != 0)) {
      HeapInterface::Free();
    }
    *(undefined1 *)
     (*(long *)(*(long *)((long)&__DT_RELA[0xca5].r_addend + *param_1) + 0x10) + 0x260) = 1;
    return &DAT_015d3620;
  }
  uVar13 = (int)((ulong)((long)plVar8 + (-0x10 - (long)plVar11)) >> 4) + 1U & 3;
  if (uVar13 == 0) goto LAB_001ba8da;
  if (uVar13 != 1) {
    if (uVar13 != 2) {
      plVar9 = (long *)*plVar11;
      if (plVar9 != (long *)0x0) {
        LOCK();
        plVar1 = plVar9 + 1;
        lVar12 = *plVar1;
        *(int *)plVar1 = (int)*plVar1 + -1;
        UNLOCK();
        if ((int)lVar12 == 1) {
          (**(code **)(*plVar9 + 0x10))(plVar9);
          LOCK();
          piVar2 = (int *)((long)plVar9 + 0xc);
          iVar3 = *piVar2;
          *piVar2 = *piVar2 + -1;
          UNLOCK();
          if (iVar3 == 1) {
            (**(code **)(*plVar9 + 0x18))(plVar9);
          }
        }
      }
      plVar11 = plVar11 + 2;
    }
    plVar9 = (long *)*plVar11;
    if (plVar9 != (long *)0x0) {
      LOCK();
      plVar1 = plVar9 + 1;
      lVar12 = *plVar1;
      *(int *)plVar1 = (int)*plVar1 + -1;
      UNLOCK();
      if ((int)lVar12 == 1) {
        (**(code **)(*plVar9 + 0x10))(plVar9);
        LOCK();
        piVar2 = (int *)((long)plVar9 + 0xc);
        iVar3 = *piVar2;
        *piVar2 = *piVar2 + -1;
        UNLOCK();
        if (iVar3 == 1) {
          (**(code **)(*plVar9 + 0x18))(plVar9);
        }
      }
    }
    plVar11 = plVar11 + 2;
  }
  plVar9 = (long *)*plVar11;
  if (plVar9 != (long *)0x0) {
    LOCK();
    plVar1 = plVar9 + 1;
    lVar12 = *plVar1;
    *(int *)plVar1 = (int)*plVar1 + -1;
    UNLOCK();
    if ((int)lVar12 == 1) {
      (**(code **)(*plVar9 + 0x10))(plVar9);
      LOCK();
      piVar2 = (int *)((long)plVar9 + 0xc);
      iVar3 = *piVar2;
      *piVar2 = *piVar2 + -1;
      UNLOCK();
      if (iVar3 == 1) {
        (**(code **)(*plVar9 + 0x18))(plVar9);
      }
    }
  }
  plVar11 = plVar11 + 2;
joined_r0x001ba8d8:
  if (plVar8 != plVar11) {
LAB_001ba8da:
    do {
      plVar9 = (long *)*plVar11;
      if (plVar9 != (long *)0x0) {
        LOCK();
        plVar1 = plVar9 + 1;
        lVar12 = *plVar1;
        *(int *)plVar1 = (int)*plVar1 + -1;
        UNLOCK();
        if ((int)lVar12 == 1) {
          (**(code **)(*plVar9 + 0x10))(plVar9);
          LOCK();
          piVar2 = (int *)((long)plVar9 + 0xc);
          iVar3 = *piVar2;
          *piVar2 = *piVar2 + -1;
          UNLOCK();
          if (iVar3 == 1) {
            (**(code **)(*plVar9 + 0x18))(plVar9);
          }
        }
      }
      plVar9 = (long *)plVar11[2];
      if (plVar9 != (long *)0x0) {
        LOCK();
        plVar1 = plVar9 + 1;
        lVar12 = *plVar1;
        *(int *)plVar1 = (int)*plVar1 + -1;
        UNLOCK();
        if ((int)lVar12 == 1) {
          (**(code **)(*plVar9 + 0x10))(plVar9);
          LOCK();
          piVar2 = (int *)((long)plVar9 + 0xc);
          iVar3 = *piVar2;
          *piVar2 = *piVar2 + -1;
          UNLOCK();
          if (iVar3 == 1) {
            (**(code **)(*plVar9 + 0x18))(plVar9);
          }
        }
      }
      plVar9 = (long *)plVar11[4];
      if (plVar9 != (long *)0x0) {
        LOCK();
        plVar1 = plVar9 + 1;
        lVar12 = *plVar1;
        *(int *)plVar1 = (int)*plVar1 + -1;
        UNLOCK();
        if ((int)lVar12 == 1) {
          (**(code **)(*plVar9 + 0x10))(plVar9);
          LOCK();
          piVar2 = (int *)((long)plVar9 + 0xc);
          iVar3 = *piVar2;
          *piVar2 = *piVar2 + -1;
          UNLOCK();
          if (iVar3 == 1) {
            (**(code **)(*plVar9 + 0x18))(plVar9);
          }
        }
      }
      plVar9 = (long *)plVar11[6];
      if (plVar9 != (long *)0x0) {
        LOCK();
        plVar1 = plVar9 + 1;
        lVar12 = *plVar1;
        *(int *)plVar1 = (int)*plVar1 + -1;
        UNLOCK();
        if ((int)lVar12 == 1) {
          (**(code **)(*plVar9 + 0x10))(plVar9);
          LOCK();
          piVar2 = (int *)((long)plVar9 + 0xc);
          iVar3 = *piVar2;
          *piVar2 = *piVar2 + -1;
          UNLOCK();
          if (iVar3 == 1) goto code_r0x001baa4f;
        }
      }
      plVar11 = plVar11 + 8;
      if (plVar8 == plVar11) break;
    } while( true );
  }
  lVar12 = *param_1;
  plVar11 = *(long **)(lVar6 + 0x68);
  goto LAB_001ba961;
code_r0x001baa4f:
  plVar11 = plVar11 + 8;
  (**(code **)(*plVar9 + 0x18))(plVar9);
  goto joined_r0x001ba8d8;
}


```

## `jag::packethandlers::Misc::LOGOUT_TRANSFER` @ 001c3c00
```c

undefined * jag::packethandlers::Misc::LOGOUT_TRANSFER(long *param_1)

{
  long lVar1;
  long lVar2;
  
  lVar2 = *(long *)((long)&__DT_RELA[0xd00].r_addend + *param_1);
  if (lVar2 != 0) {
    if (*(long *)(lVar2 + 0xc0) != 0) {
      *(undefined1 *)(*(long *)(lVar2 + 0xc0) + 0x240) = 1;
      lVar1 = *(long *)(lVar2 + 0xc0);
      *(undefined8 *)(lVar2 + 0xc0) = 0;
      if (lVar1 != 0) {
        FUN_0089cc50(lVar1);
        HeapInterface::Free(lVar1,0x280);
      }
      lVar1 = *param_1;
      *(undefined4 *)(lVar2 + 0x10) = 0;
      *(undefined4 *)(lVar2 + 0x98) = 3;
      lVar2 = *(long *)((long)&__DT_RELA[0xd00].r_addend + lVar1);
    }
    lVar1 = std__chrono___V2__system_clock__now();
    *(long *)(lVar2 + 0x90) = lVar1 / 1000000 + 30000;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::RUNCLIENTSCRIPT_impl` @ 001d2220
```c

undefined *
jag::packethandlers::ClientState::RUNCLIENTSCRIPT_impl(long *param_1,PacketCore *param_2)

{
  int *piVar1;
  byte *pbVar2;
  undefined8 uVar3;
  byte bVar4;
  undefined4 uVar5;
  size_t sVar6;
  long lVar7;
  uint uVar8;
  undefined8 extraout_RDX;
  byte *__s;
  long lVar9;
  long lVar10;
  ulong uVar11;
  ulong uVar12;
  long lVar13;
  int iVar14;
  undefined1 *puVar15;
  bool bVar16;
  ulong *local_260;
  ulong local_248;
  undefined8 uStack_240;
  undefined8 local_238;
  ulong local_228;
  undefined8 uStack_220;
  undefined8 local_218;
  byte local_210;
  ulong local_208;
  undefined8 uStack_200;
  long local_1f8;
  byte local_1f0;
  undefined1 local_1e8;
  undefined7 uStack_1e7;
  ulong local_1e0;
  char local_1d1;
  undefined1 *local_1c8;
  undefined1 local_1c0 [24];
  uint local_1a8 [2];
  undefined1 *local_1a0;
  undefined1 *local_198;
  long *local_190;
  undefined1 *local_180;
  undefined1 local_178 [192];
  long local_b8;
  undefined8 local_b0;
  undefined8 local_a8;
  undefined8 local_a0;
  undefined8 local_98;
  undefined4 local_90;
  undefined4 local_8c;
  undefined1 local_88 [16];
  undefined4 local_78;
  undefined1 local_70;
  undefined1 local_59;
  undefined4 local_58;
  undefined1 local_54;
  undefined1 local_53;
  undefined1 local_50;
  undefined1 local_39;
  
                    /* RUNCLIENTSCRIPT_impl — handler body for ServerProt op 110 (varShort).
                       Reads:
                         1. Null-terminated CP1252 type-descriptor string (e.g. "sssi")
                         2. For each char from END to START: 's' -> CP1252 string, 'l' -> 8B BE
                       long, else int (4B BE)
                         3. Final: gT<unsigned_int> = scriptId (4B BE)
                       Calls jag::ScriptRunner::ExecuteHookInner(...). Bound by the main
                       ServerProt::BindHandlers at op 110. */
  lVar9 = param_2->position;
  local_1c8 = local_1c0;
  local_1e8 = 0;
  local_1d1 = '\x17';
  __s = (byte *)((long)param_2->bufData + lVar9);
  sVar6 = strlen((char *)__s);
  if (sVar6 == 0) {
    local_1e0 = 0;
    iVar14 = -1;
    param_2->position = lVar9 + 1;
  }
  else {
    pbVar2 = __s + (sVar6 & 0xffffffff);
    if ((sVar6 & 0xffffffff) == 0) {
      lVar13 = 0x17;
      param_2->position = lVar9 + 1 + sVar6;
    }
    else {
      if (((long)pbVar2 - (long)__s & 1U) != 0) {
        uVar12 = (ulong)*__s;
        if ((byte)(*__s + 0x80) < 0x20) {
          uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
        }
        uVar8 = (uint)uVar12;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&local_1e8,(int)(char)uVar12);
          }
          else {
            if (uVar8 < 0x800) {
              uVar12 = local_1e0;
              if (-1 < local_1d1) {
                uVar12 = 0x17 - (long)local_1d1;
              }
              bVar4 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&local_1e8,uVar12 + 2);
            }
            else {
              uVar11 = local_1e0;
              if (-1 < local_1d1) {
                uVar11 = 0x17 - (long)local_1d1;
              }
              FUN_006b28b0(&local_1e8,uVar11 + 3);
              bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&local_1e8,(int)(char)((byte)(uVar12 >> 0xc) | 0xe0));
            }
            FUN_00c29370(&local_1e8,(int)(char)bVar4);
            uVar8 = uVar8 & 0x3f | 0xffffff80;
            FUN_00c29370(&local_1e8,(int)(char)uVar8,extraout_RDX,uVar8);
          }
        }
        __s = __s + 1;
        goto joined_r0x001d2a7c;
      }
      do {
        while( true ) {
          uVar12 = (ulong)*__s;
          if ((byte)(*__s + 0x80) < 0x20) {
            uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
          }
          uVar8 = (uint)uVar12;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&local_1e8,(int)(char)(byte)uVar12);
            }
            else {
              if (uVar8 < 0x800) {
                uVar11 = local_1e0;
                if (-1 < local_1d1) {
                  uVar11 = 0x17 - (long)local_1d1;
                }
                FUN_006b28b0(&local_1e8,uVar11 + 2);
                bVar4 = (byte)(uVar8 >> 6) | 0xc0;
              }
              else {
                uVar11 = local_1e0;
                if (-1 < local_1d1) {
                  uVar11 = 0x17 - (long)local_1d1;
                }
                FUN_006b28b0(&local_1e8,uVar11 + 3);
                FUN_00c29370(&local_1e8,(int)(char)((byte)(uVar8 >> 0xc) | 0xe0));
                bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              }
              FUN_00c29370(&local_1e8,(int)(char)bVar4);
              FUN_00c29370(&local_1e8,(int)(char)((byte)uVar12 & 0x3f | 0x80));
            }
          }
          uVar12 = (ulong)__s[1];
          if ((byte)(__s[1] + 0x80) < 0x20) {
            uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
          }
          uVar8 = (uint)uVar12;
          if (uVar8 != 0) break;
LAB_001d296b:
          __s = __s + 2;
          if (__s == pbVar2) goto LAB_001d297e;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            uVar11 = local_1e0;
            if (-1 < local_1d1) {
              uVar11 = 0x17 - (long)local_1d1;
            }
            bVar4 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&local_1e8,uVar11 + 2);
          }
          else {
            uVar11 = local_1e0;
            if (-1 < local_1d1) {
              uVar11 = 0x17 - (long)local_1d1;
            }
            FUN_006b28b0(&local_1e8,uVar11 + 3);
            bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&local_1e8,(int)(char)((byte)(uVar12 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&local_1e8,(int)(char)bVar4);
          FUN_00c29370(&local_1e8,(int)(char)((byte)uVar12 & 0x3f | 0x80));
          goto LAB_001d296b;
        }
        __s = __s + 2;
        FUN_00c29370(&local_1e8,(int)(char)(byte)uVar12);
joined_r0x001d2a7c:
      } while (__s != pbVar2);
LAB_001d297e:
      lVar13 = (long)local_1d1;
      param_2->position = param_2->position + sVar6 + 1;
      if (local_1d1 < '\0') {
        iVar14 = (int)local_1e0 + -1;
        local_1e0 = local_1e0 & 0xffffffff;
        goto LAB_001d2293;
      }
    }
    iVar14 = (int)(0x17U - lVar13) + -1;
    local_1e0 = 0x17U - lVar13 & 0xffffffff;
  }
LAB_001d2293:
  local_1a0 = local_178;
  local_190 = &local_b8;
  local_1a8[0] = 0xfffffffe;
  DAT_013945e4 = DAT_013945e4 + 1;
  lVar9 = (long)DAT_013945e4;
  local_198 = local_1a0;
  local_180 = local_1a0;
  if (local_1e0 != 0) {
    FUN_00122230(&local_1a0);
  }
  local_b0 = 0;
  local_a8 = 0;
  local_a0 = 0;
  local_98 = 0;
  local_90 = 0;
  local_8c = 0;
  local_88 = (undefined1  [16])0x0;
  lVar13 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  local_78 = 0;
  local_70 = 0;
  local_59 = 0x17;
  local_58 = 0;
  local_54 = 0;
  local_53 = 0;
  local_50 = 0;
  local_39 = 0x17;
  piVar1 = (int *)(lVar13 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar13 + 0x14) = 1;
  local_b8 = lVar9;
  if (iVar14 < 0) {
LAB_001d26e0:
    local_1a8[0] = Packet::gT_unsigned_int(param_2);
    ScriptRunner::ExecuteHookInner
              (*(undefined8 *)((long)&__DT_RELA[0xcff].r_offset + *param_1),local_1a8,500000);
    FUN_00127e40(local_1a8);
    if (local_1d1 < '\0') {
      FUN_00aa7d70(&local_1e8);
    }
    return &DAT_015d3620;
  }
  lVar7 = (long)iVar14;
  lVar10 = lVar7 << 5;
  lVar9 = lVar7;
  lVar13 = lVar7 + -1;
  do {
    puVar15 = &local_1e8;
    if (local_1d1 < '\0') {
      puVar15 = (undefined1 *)CONCAT71(uStack_1e7,local_1e8);
    }
    if (puVar15[lVar9] == 's') {
      local_248 = local_248 & 0xffffffffffffff00;
      local_238 = CONCAT17(0x17,(undefined7)local_238);
      FUN_00ad80e0(param_2,&local_248);
      uVar3 = uStack_220;
      local_228 = local_228 & 0xffffffffffffff00;
      uVar12 = local_228;
      local_218 = CONCAT17(0x17,(undefined7)local_218);
      local_1f8 = local_218;
      local_210 = 2;
      puVar15 = local_1a0 + lVar10;
      local_218 = local_238;
      local_238 = local_1f8;
      local_208 = local_228;
      uStack_200 = uStack_220;
      local_228 = local_248;
      uStack_220 = uStack_240;
      local_248 = uVar12;
      uStack_240 = uVar3;
      if (puVar15[0x18] == '\x02') {
        FUN_00496010(puVar15,&local_228);
      }
      else {
        local_1f0 = 0xff;
        FUN_0048d4c0(&local_208,&local_228);
        local_1f0 = local_210;
        if (puVar15[0x18] != 0xff) {
          (*(code *)(&PTR_FUN_01384ba0)[(byte)puVar15[0x18]])(puVar15);
        }
        puVar15[0x18] = 0xff;
        if (local_1f0 != 0xff) {
          (*(code *)(&PTR_std____detail____variant____erased_ctor<int&,int&&>_01384c20)[local_1f0])
                    (puVar15,&local_208);
          puVar15[0x18] = local_1f0;
          if (local_1f0 != 0xff) {
            (*(code *)(&PTR_FUN_01384ba0)[local_1f0])(&local_208);
          }
        }
      }
      FUN_0047fe00(&local_228);
      if ((local_238 < 0) && (local_248 != 0)) {
        eastl__basic_string();
      }
    }
    else {
      if (puVar15[lVar9] == 'l') {
        lVar9 = param_2->position;
        bVar16 = DAT_01050dc0 == 0x3020100;
        param_2->position = lVar9 + 8;
        local_228 = *(ulong *)((long)param_2->bufData + lVar9);
        if (bVar16) {
          uVar12 = (local_228 & 0xff00ff00ff00ff) << 8 | (long)local_228 >> 8 & 0xff00ff00ff00ffU;
          uVar12 = (uVar12 & 0xffff0000ffff) << 0x10 | (long)uVar12 >> 0x10 & 0xffff0000ffffU;
          local_228 = uVar12 << 0x20 | uVar12 >> 0x20;
        }
        local_210 = 1;
        puVar15 = local_1a0 + lVar10;
        if (puVar15[0x18] == '\x01') {
          FUN_00474720(puVar15,&local_228);
        }
        else {
          local_1f0 = 0xff;
          FUN_00474690(&local_208,&local_228);
LAB_001d2431:
          local_260 = &local_208;
          local_1f0 = local_210;
          if (puVar15[0x18] != 0xff) {
            (*(code *)(&PTR_FUN_01384ba0)[(byte)puVar15[0x18]])(puVar15);
          }
          puVar15[0x18] = 0xff;
          if (local_1f0 != 0xff) {
            (*(code *)(&PTR_std____detail____variant____erased_ctor<int&,int&&>_01384c20)[local_1f0]
            )(puVar15,local_260);
            puVar15[0x18] = local_1f0;
            if (local_1f0 != 0xff) {
              (*(code *)(&PTR_FUN_01384ba0)[local_1f0])(local_260);
            }
          }
        }
      }
      else {
        uVar5 = FUN_00133390(param_2);
        local_228 = CONCAT44(local_228._4_4_,uVar5);
        local_210 = 0;
        puVar15 = local_1a0 + lVar10;
        if (puVar15[0x18] != '\0') {
          local_1f0 = 0xff;
          FUN_004746b0(&local_208,&local_228);
          goto LAB_001d2431;
        }
        FUN_00474730(puVar15,&local_228);
      }
      FUN_0047fe00(&local_228);
    }
    if (lVar13 == (lVar7 + -1) - lVar7) goto LAB_001d26e0;
    lVar10 = lVar10 + -0x20;
    lVar9 = lVar13;
    lVar13 = lVar13 + -1;
  } while( true );
}


```

## `jag::packethandlers::ClientState::RUNCLIENTSCRIPT_impl` @ 001d2b70
```c

undefined *
jag::packethandlers::ClientState::RUNCLIENTSCRIPT_impl(long *param_1,PacketCore *param_2)

{
  int *piVar1;
  byte *pbVar2;
  undefined8 uVar3;
  byte bVar4;
  undefined4 uVar5;
  size_t sVar6;
  long lVar7;
  uint uVar8;
  undefined8 extraout_RDX;
  byte *__s;
  long lVar9;
  long lVar10;
  ulong uVar11;
  ulong uVar12;
  long lVar13;
  int iVar14;
  undefined1 *puVar15;
  bool bVar16;
  ulong *puStack_260;
  ulong uStack_248;
  undefined8 uStack_240;
  undefined8 uStack_238;
  ulong uStack_228;
  undefined8 uStack_220;
  undefined8 uStack_218;
  byte bStack_210;
  ulong uStack_208;
  undefined8 uStack_200;
  long lStack_1f8;
  byte bStack_1f0;
  undefined1 uStack_1e8;
  undefined7 uStack_1e7;
  ulong uStack_1e0;
  char cStack_1d1;
  undefined1 *puStack_1c8;
  undefined1 auStack_1c0 [24];
  uint auStack_1a8 [2];
  undefined1 *puStack_1a0;
  undefined1 *puStack_198;
  long *plStack_190;
  undefined1 *puStack_180;
  undefined1 auStack_178 [192];
  long lStack_b8;
  undefined8 uStack_b0;
  undefined8 uStack_a8;
  undefined8 uStack_a0;
  undefined8 uStack_98;
  undefined4 uStack_90;
  undefined4 uStack_8c;
  undefined1 auStack_88 [16];
  undefined4 uStack_78;
  undefined1 uStack_70;
  undefined1 uStack_59;
  undefined4 uStack_58;
  undefined1 uStack_54;
  undefined1 uStack_53;
  undefined1 uStack_50;
  undefined1 uStack_39;
  
  lVar9 = param_2->position;
  puStack_1c8 = auStack_1c0;
  uStack_1e8 = 0;
  cStack_1d1 = '\x17';
  __s = (byte *)((long)param_2->bufData + lVar9);
  sVar6 = strlen((char *)__s);
  if (sVar6 == 0) {
    uStack_1e0 = 0;
    iVar14 = -1;
    param_2->position = lVar9 + 1;
  }
  else {
    pbVar2 = __s + (sVar6 & 0xffffffff);
    if ((sVar6 & 0xffffffff) == 0) {
      lVar13 = 0x17;
      param_2->position = lVar9 + 1 + sVar6;
    }
    else {
      if (((long)pbVar2 - (long)__s & 1U) != 0) {
        uVar12 = (ulong)*__s;
        if ((byte)(*__s + 0x80) < 0x20) {
          uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
        }
        uVar8 = (uint)uVar12;
        if (uVar8 != 0) {
          if (uVar8 < 0x80) {
            FUN_00c29370(&uStack_1e8,(int)(char)uVar12);
          }
          else {
            if (uVar8 < 0x800) {
              uVar12 = uStack_1e0;
              if (-1 < cStack_1d1) {
                uVar12 = 0x17 - (long)cStack_1d1;
              }
              bVar4 = (byte)(uVar8 >> 6) | 0xc0;
              FUN_006b28b0(&uStack_1e8,uVar12 + 2);
            }
            else {
              uVar11 = uStack_1e0;
              if (-1 < cStack_1d1) {
                uVar11 = 0x17 - (long)cStack_1d1;
              }
              FUN_006b28b0(&uStack_1e8,uVar11 + 3);
              bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&uStack_1e8,(int)(char)((byte)(uVar12 >> 0xc) | 0xe0));
            }
            FUN_00c29370(&uStack_1e8,(int)(char)bVar4);
            uVar8 = uVar8 & 0x3f | 0xffffff80;
            FUN_00c29370(&uStack_1e8,(int)(char)uVar8,extraout_RDX,uVar8);
          }
        }
        __s = __s + 1;
        goto joined_r0x001d2a7c;
      }
      do {
        while( true ) {
          uVar12 = (ulong)*__s;
          if ((byte)(*__s + 0x80) < 0x20) {
            uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
          }
          uVar8 = (uint)uVar12;
          if (uVar8 != 0) {
            if (uVar8 < 0x80) {
              FUN_00c29370(&uStack_1e8,(int)(char)(byte)uVar12);
            }
            else {
              if (uVar8 < 0x800) {
                uVar11 = uStack_1e0;
                if (-1 < cStack_1d1) {
                  uVar11 = 0x17 - (long)cStack_1d1;
                }
                FUN_006b28b0(&uStack_1e8,uVar11 + 2);
                bVar4 = (byte)(uVar8 >> 6) | 0xc0;
              }
              else {
                uVar11 = uStack_1e0;
                if (-1 < cStack_1d1) {
                  uVar11 = 0x17 - (long)cStack_1d1;
                }
                FUN_006b28b0(&uStack_1e8,uVar11 + 3);
                FUN_00c29370(&uStack_1e8,(int)(char)((byte)(uVar8 >> 0xc) | 0xe0));
                bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
              }
              FUN_00c29370(&uStack_1e8,(int)(char)bVar4);
              FUN_00c29370(&uStack_1e8,(int)(char)((byte)uVar12 & 0x3f | 0x80));
            }
          }
          uVar12 = (ulong)__s[1];
          if ((byte)(__s[1] + 0x80) < 0x20) {
            uVar12 = (ulong)*(ushort *)(&DAT_0102a600 + uVar12 * 2);
          }
          uVar8 = (uint)uVar12;
          if (uVar8 != 0) break;
LAB_001d296b:
          __s = __s + 2;
          if (__s == pbVar2) goto LAB_001d297e;
        }
        if (0x7f < uVar8) {
          if (uVar8 < 0x800) {
            uVar11 = uStack_1e0;
            if (-1 < cStack_1d1) {
              uVar11 = 0x17 - (long)cStack_1d1;
            }
            bVar4 = (byte)(uVar8 >> 6) | 0xc0;
            FUN_006b28b0(&uStack_1e8,uVar11 + 2);
          }
          else {
            uVar11 = uStack_1e0;
            if (-1 < cStack_1d1) {
              uVar11 = 0x17 - (long)cStack_1d1;
            }
            FUN_006b28b0(&uStack_1e8,uVar11 + 3);
            bVar4 = (byte)(uVar8 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&uStack_1e8,(int)(char)((byte)(uVar12 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&uStack_1e8,(int)(char)bVar4);
          FUN_00c29370(&uStack_1e8,(int)(char)((byte)uVar12 & 0x3f | 0x80));
          goto LAB_001d296b;
        }
        __s = __s + 2;
        FUN_00c29370(&uStack_1e8,(int)(char)(byte)uVar12);
joined_r0x001d2a7c:
      } while (__s != pbVar2);
LAB_001d297e:
      lVar13 = (long)cStack_1d1;
      param_2->position = param_2->position + sVar6 + 1;
      if (cStack_1d1 < '\0') {
        iVar14 = (int)uStack_1e0 + -1;
        uStack_1e0 = uStack_1e0 & 0xffffffff;
        goto LAB_001d2293;
      }
    }
    iVar14 = (int)(0x17U - lVar13) + -1;
    uStack_1e0 = 0x17U - lVar13 & 0xffffffff;
  }
LAB_001d2293:
  puStack_1a0 = auStack_178;
  plStack_190 = &lStack_b8;
  auStack_1a8[0] = 0xfffffffe;
  DAT_013945e4 = DAT_013945e4 + 1;
  lVar9 = (long)DAT_013945e4;
  puStack_198 = puStack_1a0;
  puStack_180 = puStack_1a0;
  if (uStack_1e0 != 0) {
    FUN_00122230(&puStack_1a0);
  }
  uStack_b0 = 0;
  uStack_a8 = 0;
  uStack_a0 = 0;
  uStack_98 = 0;
  uStack_90 = 0;
  uStack_8c = 0;
  auStack_88 = (undefined1  [16])0x0;
  lVar13 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  uStack_78 = 0;
  uStack_70 = 0;
  uStack_59 = 0x17;
  uStack_58 = 0;
  uStack_54 = 0;
  uStack_53 = 0;
  uStack_50 = 0;
  uStack_39 = 0x17;
  piVar1 = (int *)(lVar13 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar13 + 0x14) = 1;
  lStack_b8 = lVar9;
  if (iVar14 < 0) {
LAB_001d26e0:
    auStack_1a8[0] = Packet::gT_unsigned_int(param_2);
    ScriptRunner::ExecuteHookInner
              (*(undefined8 *)((long)&__DT_RELA[0xcff].r_offset + *param_1),auStack_1a8,500000);
    FUN_00127e40(auStack_1a8);
    if (cStack_1d1 < '\0') {
      FUN_00aa7d70(&uStack_1e8);
    }
    return &DAT_015d3620;
  }
  lVar7 = (long)iVar14;
  lVar10 = lVar7 << 5;
  lVar9 = lVar7;
  lVar13 = lVar7 + -1;
  do {
    puVar15 = &uStack_1e8;
    if (cStack_1d1 < '\0') {
      puVar15 = (undefined1 *)CONCAT71(uStack_1e7,uStack_1e8);
    }
    if (puVar15[lVar9] == 's') {
      uStack_248 = uStack_248 & 0xffffffffffffff00;
      uStack_238 = CONCAT17(0x17,(undefined7)uStack_238);
      FUN_00ad80e0(param_2,&uStack_248);
      uVar3 = uStack_220;
      uStack_228 = uStack_228 & 0xffffffffffffff00;
      uVar12 = uStack_228;
      uStack_218 = CONCAT17(0x17,(undefined7)uStack_218);
      lStack_1f8 = uStack_218;
      bStack_210 = 2;
      puVar15 = puStack_1a0 + lVar10;
      uStack_218 = uStack_238;
      uStack_238 = lStack_1f8;
      uStack_208 = uStack_228;
      uStack_200 = uStack_220;
      uStack_228 = uStack_248;
      uStack_220 = uStack_240;
      uStack_248 = uVar12;
      uStack_240 = uVar3;
      if (puVar15[0x18] == '\x02') {
        FUN_00496010(puVar15,&uStack_228);
      }
      else {
        bStack_1f0 = 0xff;
        FUN_0048d4c0(&uStack_208,&uStack_228);
        bStack_1f0 = bStack_210;
        if (puVar15[0x18] != 0xff) {
          (*(code *)(&PTR_FUN_01384ba0)[(byte)puVar15[0x18]])(puVar15);
        }
        puVar15[0x18] = 0xff;
        if (bStack_1f0 != 0xff) {
          (*(code *)(&PTR_std____detail____variant____erased_ctor<int&,int&&>_01384c20)[bStack_1f0])
                    (puVar15,&uStack_208);
          puVar15[0x18] = bStack_1f0;
          if (bStack_1f0 != 0xff) {
            (*(code *)(&PTR_FUN_01384ba0)[bStack_1f0])(&uStack_208);
          }
        }
      }
      FUN_0047fe00(&uStack_228);
      if ((uStack_238 < 0) && (uStack_248 != 0)) {
        eastl__basic_string();
      }
    }
    else {
      if (puVar15[lVar9] == 'l') {
        lVar9 = param_2->position;
        bVar16 = DAT_01050dc0 == 0x3020100;
        param_2->position = lVar9 + 8;
        uStack_228 = *(ulong *)((long)param_2->bufData + lVar9);
        if (bVar16) {
          uVar12 = (uStack_228 & 0xff00ff00ff00ff) << 8 | (long)uStack_228 >> 8 & 0xff00ff00ff00ffU;
          uVar12 = (uVar12 & 0xffff0000ffff) << 0x10 | (long)uVar12 >> 0x10 & 0xffff0000ffffU;
          uStack_228 = uVar12 << 0x20 | uVar12 >> 0x20;
        }
        bStack_210 = 1;
        puVar15 = puStack_1a0 + lVar10;
        if (puVar15[0x18] == '\x01') {
          FUN_00474720(puVar15,&uStack_228);
        }
        else {
          bStack_1f0 = 0xff;
          FUN_00474690(&uStack_208,&uStack_228);
LAB_001d2431:
          puStack_260 = &uStack_208;
          bStack_1f0 = bStack_210;
          if (puVar15[0x18] != 0xff) {
            (*(code *)(&PTR_FUN_01384ba0)[(byte)puVar15[0x18]])(puVar15);
          }
          puVar15[0x18] = 0xff;
          if (bStack_1f0 != 0xff) {
            (*(code *)(&PTR_std____detail____variant____erased_ctor<int&,int&&>_01384c20)
                      [bStack_1f0])(puVar15,puStack_260);
            puVar15[0x18] = bStack_1f0;
            if (bStack_1f0 != 0xff) {
              (*(code *)(&PTR_FUN_01384ba0)[bStack_1f0])(puStack_260);
            }
          }
        }
      }
      else {
        uVar5 = FUN_00133390(param_2);
        uStack_228 = CONCAT44(uStack_228._4_4_,uVar5);
        bStack_210 = 0;
        puVar15 = puStack_1a0 + lVar10;
        if (puVar15[0x18] != '\0') {
          bStack_1f0 = 0xff;
          FUN_004746b0(&uStack_208,&uStack_228);
          goto LAB_001d2431;
        }
        FUN_00474730(puVar15,&uStack_228);
      }
      FUN_0047fe00(&uStack_228);
    }
    if (lVar13 == (lVar7 + -1) - lVar7) goto LAB_001d26e0;
    lVar10 = lVar10 + -0x20;
    lVar9 = lVar13;
    lVar13 = lVar13 + -1;
  } while( true );
}


```

## `jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE` @ 001daa70
```c

/* Setting prototype: undefined * REBUILD_NORMAL_SIMPLE(long * thisPtr, PacketCore * packetPtr) */

undefined *
jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE(long *thisPtr,PacketCore *packetPtr)

{
  ushort *puVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  char cVar5;
  ushort uVar6;
  void *pvVar7;
  long lVar8;
  long lVar9;
  uint uVar10;
  long lVar11;
  long lVar12;
  undefined8 uVar13;
  undefined *puVar14;
  ushort uVar15;
  long *plVar16;
  long lVar17;
  char *pcVar18;
  int iVar19;
  ushort local_6a;
  int local_68;
  int local_64;
  undefined1 local_60 [4];
  uint local_5c;
  uint local_58;
  undefined1 local_54 [4];
  uint local_50;
  uint local_4c;
  long local_48;
  long local_40;
  
                    /* op81 REBUILD_NORMAL_SIMPLE. TAIL (verified): builds BuildArea via
                       FUN_00c55e80, then
                       calls jag::game::Camera::ProcessCameraReset(worldState=client[RELA 0xcf5],
                       anchorByte)
                       UNLESS worldState+0x418 == 6 already. anchorByte = *(*(client[RELA
                       0xca6])+0x240)+0xd8
                       (scene-root byte, NOT the op81 wire 'cameraRotation' byte which goes to
                       buildState+0x428).
                       ProcessCameraReset is THE thing that positions the camera + sets
                       worldState+0x418=6
                       (RENDER). op22/op77 do NOT position it. [RE 2026-06-23, headless 948-5] */
                    /* op81 GPI-prefix gate: if worldState+0x49 != 0 -> ParseGpiPrefix_op81
                       (FUN_00b254a0)
                       consumes the GPI prefix (places local player at gBit(30) tile + 2046 other
                       slots),
                       advancing packet cursor to 5119, THEN the coord header is read at the cursor.
                       +0x49 is set by the state->30 transition (REBUILD_REGION_HANDLER
                       @0x000f7036), so on
                       world entry op81 ALWAYS expects the prefix. [VERIFIED 948-5 2026-06-23] */
  if (*(char *)(*(long *)((long)&__DT_RELA[0xcf6].r_addend + *thisPtr) + 0x49) != '\0') {
    jag__PlayerList__ParseGpiPrefix_op81
              (*(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + *thisPtr));
  }
  lVar17 = packetPtr->position;
  pvVar7 = packetPtr->bufData;
  puVar1 = (ushort *)((long)pvVar7 + lVar17 + 8);
  packetPtr->position = lVar17 + 3;
  bVar2 = *(byte *)((long)pvVar7 + lVar17 + 1);
  bVar3 = *(byte *)((long)pvVar7 + lVar17 + 2);
  packetPtr->position = lVar17 + 4;
  cVar4 = *(char *)((long)pvVar7 + lVar17 + 3);
  packetPtr->position = lVar17 + 6;
  pcVar18 = (char *)(lVar17 + 6 + (long)pvVar7);
  uVar15 = *(ushort *)((long)pvVar7 + lVar17 + 4);
  if (DAT_01050dc0 == 0x3020100) {
    packetPtr->position = lVar17 + 7;
    cVar5 = *pcVar18;
    uVar15 = uVar15 << 8 | uVar15 >> 8;
    packetPtr->position = lVar17 + 10;
    uVar6 = *puVar1;
    local_6a = uVar6 << 8 | uVar6 >> 8;
  }
  else {
    packetPtr->position = lVar17 + 7;
    cVar5 = *pcVar18;
    packetPtr->position = lVar17 + 10;
    local_6a = *puVar1;
  }
  uVar10 = Packet::gT_unsigned_int(packetPtr);
  game::BuildArea::DecodePackedCoord(local_60,uVar10);
  uVar10 = Packet::gT_unsigned_int(packetPtr);
  game::BuildArea::DecodePackedCoord(local_54,uVar10);
  if (cVar4 != -0x7b) {
    return &DAT_015d35c0;
  }
  lVar12 = *thisPtr;
  lVar17 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar12);
  *(uint *)((long)&__DT_RELA[0x428].r_info + *(long *)((long)&__DT_RELA[0xcfb].r_addend + lVar12)) =
       (uint)(byte)(cVar5 + 0x80);
  puVar14 = &DAT_015df2b0;
  iVar19 = *(int *)(lVar17 + 0x610) >> 4;
  local_64 = ((uint)uVar15 - iVar19) * 8;
  local_68 = (((uint)bVar2 + (uint)bVar3 * 0x100 & 0xffff) - iVar19) * 8;
  lVar17 = *(long *)((long)&__DT_RELA[0xd02].r_info + lVar12);
  iVar19 = *(int *)(lVar17 + 0x70);
  if (iVar19 != -1) {
    puVar14 = (undefined *)((long)iVar19 * 0x10 + *(long *)(lVar17 + 0x58));
  }
  lVar17 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar12);
  plVar16 = &DAT_015d4d60;
  if ((*(int *)(lVar17 + 0x3c) == 4) &&
     (lVar17 = *(long *)(*(long *)(lVar17 + 0x80) + (long)DAT_015df084 * 8), lVar17 != 0)) {
    plVar16 = *(long **)(lVar17 + 0x38);
    plVar16 = (long *)(**(code **)(*plVar16 + 0x40))(plVar16,local_6a,0);
    lVar12 = *thisPtr;
  }
  lVar17 = plVar16[1];
  if (*(long *)(puVar14 + 8) != 0) {
    if (*(long *)((long)&__DT_RELA[0x6d4].r_info + *(long *)(puVar14 + 8)) == lVar17)
    goto LAB_001dae48;
    FUN_0048f9e0();
    lVar12 = *(long *)((long)&__DT_RELA[0xcfc].r_offset + *thisPtr);
    lVar11 = *(long *)(lVar12 + 0x10);
    lVar17 = lVar12 + 8;
    *(undefined1 *)
     ((long)&__DT_RELA[0x428].r_offset + *(long *)((long)&__DT_RELA[0xcfb].r_addend + *thisPtr)) = 1
    ;
    for (; lVar11 != lVar17; lVar11 = eastl::rbtree::next(lVar11)) {
      (**(code **)(**(long **)(lVar11 + 0x38) + 0xe0))();
    }
    FUN_00487de0(lVar17,*(undefined8 *)(lVar12 + 0x18));
    *(undefined1 *)(lVar12 + 0x20) = 0;
    lVar11 = *thisPtr;
    *(undefined8 *)(lVar12 + 0x18) = 0;
    *(undefined8 *)(lVar12 + 0x28) = 0;
    lVar11 = *(long *)((long)&__DT_RELA[0xcfa].r_offset + lVar11);
    *(long *)(lVar12 + 8) = lVar17;
    *(long *)(lVar12 + 0x10) = lVar17;
    *(undefined1 (*) [16])(lVar12 + 0x40) = (undefined1  [16])0x0;
    lVar17 = lVar11 + 8;
    for (lVar12 = *(long *)(lVar11 + 0x10); lVar12 != lVar17; lVar12 = eastl::rbtree::next(lVar12))
    {
      lVar8 = *(long *)(lVar12 + 0x40);
      lVar9 = *(long *)(lVar8 + 0x50);
      *(undefined8 *)(lVar8 + 8) = 0xffffffff;
      *(undefined4 *)(lVar8 + 0x10) = 0;
      *(undefined1 *)(lVar8 + 0x15) = 0;
      *(undefined4 *)(lVar8 + 0x4c) = 0xffffffff;
      *(undefined8 *)(lVar8 + 0x50) = 0;
      *(undefined8 *)(lVar8 + 0x58) = 0;
      if (lVar9 != 0) {
        ref_counter_base::DecRef();
      }
      *(undefined4 *)(lVar8 + 0x60) = 0xffffffff;
    }
    FUN_00486760(lVar17,*(undefined8 *)(lVar11 + 0x18));
    *(undefined1 *)(lVar11 + 0x20) = 0;
    lVar12 = *thisPtr;
    *(long *)(lVar11 + 8) = lVar17;
    *(long *)(lVar11 + 0x10) = lVar17;
    lVar17 = plVar16[1];
    *(undefined8 *)(lVar11 + 0x18) = 0;
    *(undefined1 (*) [16])(lVar11 + 0x40) = (undefined1  [16])0x0;
    *(undefined8 *)(lVar11 + 0x28) = 0;
  }
  local_48 = *plVar16;
  uVar13 = *(undefined8 *)((long)&__DT_RELA[0xd02].r_info + lVar12);
  if (local_48 != 0) {
    LOCK();
    *(int *)(local_48 + 8) = *(int *)(local_48 + 8) + 1;
    UNLOCK();
  }
  local_40 = lVar17;
  game::SceneManager::FUN_00c55e80
            (uVar13,&local_48,local_5c >> 6,local_58 >> 6,local_50 >> 6,local_4c >> 6);
  if (local_48 != 0) {
    ref_counter_base::DecRef();
  }
  lVar17 = *(long *)((long)&__DT_RELA[0xca7].r_info + *thisPtr);
  uVar13 = std__chrono___V2__steady_clock__now();
  lVar12 = *thisPtr;
  *(undefined8 *)("gmtime_r" + lVar17 + 3) = uVar13;
  *(long *)("gmtime_r" + lVar17 + 3) = *(long *)("gmtime_r" + lVar17 + 3) + 300000000000;
  lVar17 = *(long *)((long)&__DT_RELA[0xcf5].r_offset + lVar12);
  if ((lVar17 != 0) && (lVar17 = *(long *)(lVar17 + 0x20), lVar17 != 0)) {
    FUN_00436c50(lVar17);
    lVar12 = *thisPtr;
  }
LAB_001dae48:
  FUN_00143fa0(*(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + lVar12),3,&local_68);
  lVar17 = *thisPtr;
  lVar12 = *(long *)((long)&__DT_RELA[0xcf6].r_addend + lVar17);
  if (*(char *)(lVar12 + 0x49) != '\0') {
    *(undefined1 *)(lVar12 + 0x49) = 0;
  }
  lVar12 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar17);
  if (*(int *)(lVar12 + 0x418) == 6) {
    return &DAT_015d3620;
  }
  game::Camera::ProcessCameraReset
            (lVar12,*(undefined1 *)
                     (*(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar17) + 0x240) + 0xd8))
  ;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY_2` @ 001db210
```c

undefined * jag::packethandlers::ZoneUpdates::SPOTANIM_ENTITY_2(long *param_1,long param_2)

{
  undefined1 uVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long lVar6;
  long lVar7;
  long *plVar8;
  uint uVar9;
  undefined1 auVar10 [16];
  int iVar11;
  char cVar12;
  uint uVar13;
  long *plVar14;
  long *plVar15;
  ushort uVar16;
  uint uVar17;
  int iVar18;
  int iVar19;
  long *plVar20;
  undefined *puVar21;
  int iVar22;
  byte bVar23;
  byte bVar24;
  int iVar25;
  byte bVar26;
  float fVar27;
  int local_a8;
  int local_a4;
  uint local_a0;
  uint local_9c;
  int local_98;
  int local_94;
  int local_90;
  int local_8c;
  int local_88;
  undefined1 local_84;
  float local_80;
  int local_7c;
  byte local_78;
  undefined1 local_68 [16];
  undefined1 local_58 [16];
  undefined8 local_48;
  
  lVar5 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar5 + 1;
  uVar1 = *(undefined1 *)(*(long *)(param_2 + 0x10) + lVar5);
  uVar13 = Packet::g4_alt3(param_2);
  lVar5 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar5 + 3;
  bVar24 = *(byte *)(lVar6 + 2 + lVar5);
  bVar26 = *(byte *)(lVar6 + 1 + lVar5);
  bVar2 = *(byte *)(lVar6 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 4;
  bVar23 = 0x80 - *(char *)(lVar6 + 3 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 6;
  bVar3 = *(byte *)(lVar6 + 5 + lVar5);
  bVar4 = *(byte *)(lVar6 + 4 + lVar5);
  uVar17 = (uint)bVar26 * 0x100 + (uint)bVar24 * 0x10000 + (uint)bVar2;
  *(long *)(param_2 + 0x18) = lVar5 + 8;
  bVar24 = *(byte *)(lVar6 + 7 + lVar5);
  uVar16 = (ushort)bVar3 * 0x100 + (ushort)bVar4;
  bVar26 = *(byte *)(lVar6 + 6 + lVar5);
  *(long *)(param_2 + 0x18) = lVar5 + 10;
  iVar18 = (int)(short)((ushort)bVar24 * 0x100 + (ushort)bVar26);
  iVar19 = (*(byte *)(lVar6 + 8 + lVar5) - 0x80 & 0xff) + (uint)*(byte *)(lVar6 + 9 + lVar5) * 0x100
  ;
  uVar9 = (uint)uVar16;
  if (uVar16 == 0xffff) {
    uVar9 = 0xffffffff;
  }
  bVar24 = bVar23 >> 7;
  fVar27 = (float)(bVar23 & 7) * DAT_00cb6b3c;
  bVar26 = (byte)((uint)iVar19 >> 8) >> 7;
  if (uVar13 >> 0x1e == 0) {
    if (uVar13 >> 0x1d == 0) {
      lVar5 = *(long *)(*(long *)(*(long *)((long)&__DT_RELA[0xcfd].r_offset + *param_1) + 0x10) +
                       (ulong)(uVar13 & 0xffff) * 8);
      plVar14 = (long *)(lVar5 + 0x30);
      if (lVar5 == 0) {
        plVar14 = &DAT_013962b0;
      }
      lVar5 = *plVar14;
      lVar6 = plVar14[1];
    }
    else {
      plVar14 = (long *)NPCList::GetNPCNode(*(undefined8 *)
                                             ((long)&__DT_RELA[0xcfb].r_addend + *param_1));
      lVar5 = *plVar14;
      lVar6 = plVar14[1];
    }
    if (lVar5 != 0) {
      LOCK();
      *(int *)(lVar5 + 8) = *(int *)(lVar5 + 8) + 1;
      UNLOCK();
    }
    if (lVar6 != 0) {
      lVar7 = *param_1;
      local_88 = ((int)uVar17 >> 0xb & 0x7ffU) - 0x3ff;
      local_8c = (uVar17 & 0x7ff) - 0x3ff;
      local_98 = 0;
      local_90 = 0;
      local_84 = (int)uVar17 >> 0x16 == 1;
      local_7c = *(undefined4 *)(lVar6 + 0x40);
      local_48 = 0;
      local_68[8] = bVar24;
      local_68._0_8_ = CONCAT44(iVar19,uVar9) & 0x7fffffffffff;
      local_68._9_7_ = 0;
      local_68 = local_68 << 0x20;
      local_58._4_4_ =
           *(undefined4 *)
            ((long)&__DT_RELA[0x548].r_addend +
            *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar7) + 0x2f0));
      local_58._0_4_ = *(undefined4 *)(lVar7 + 0x500);
      local_58._8_8_ = 0;
      local_94 = iVar18;
      local_80 = fVar27;
      local_78 = bVar26;
      FUN_00155a80(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar7),uVar1,lVar6,&local_98,
                   local_68);
    }
    if (lVar5 != 0) {
      ref_counter_base::DecRef(lVar5);
    }
  }
  else {
    game::BuildArea::DecodePackedCoord(&local_a4,uVar13);
    iVar11 = local_a4;
    puVar21 = &DAT_015df2b0;
    lVar5 = *(long *)((long)&__DT_RELA[0xd02].r_info + *param_1);
    iVar22 = *(int *)(lVar5 + 0x70);
    if (iVar22 != -1) {
      puVar21 = (undefined *)((long)iVar22 * 0x10 + *(long *)(lVar5 + 0x58));
    }
    lVar5 = *(long *)(puVar21 + 8);
    if (lVar5 != 0) {
      cVar12 = (**(code **)(*(long *)((long)&__DT_RELA[0x977].r_offset + lVar5) + 0x28))
                         (lVar5 + 0x14048,local_a0,local_9c);
      if (cVar12 != '\0') {
        local_a4 = local_a4 + (uint)(local_a4 < 3);
      }
      iVar22 = (int)((float)local_9c * DAT_00cb6ae4 + DAT_00cb6b2c);
      iVar25 = (int)((float)local_a0 * DAT_00cb6ae4 + DAT_00cb6b2c);
      game::HeightMap::GetFineHeight
                (*(long *)(puVar21 + 8) + 0x14038,&local_a8,local_a4,iVar25,iVar22,1,0);
      if (uVar9 == 0xffffffff) {
        lVar5 = *(long *)((long)&__DT_RELA[0xcff].r_info + *param_1);
        if ((long *)(lVar5 + 0x10) != *(long **)(lVar5 + 0x10)) {
          plVar14 = *(long **)(lVar5 + 0x10);
          do {
            plVar8 = (long *)*plVar14;
            if (((*(char *)((long)plVar14 + 0x192) != '\0') && (iVar25 == (int)plVar14[0x11])) &&
               (iVar22 == (int)plVar14[0x12])) {
              plVar15 = plVar8;
              if ((*(int *)((long)plVar14 + 0xac) == 1) &&
                 (plVar20 = *(long **)(lVar5 + 0x88da0),
                 (long *)(lVar5 + 0x88da0) != *(long **)(lVar5 + 0x88da0))) {
                do {
                  plVar15 = (long *)*plVar20;
                  if (plVar14 + 2 == (long *)plVar20[2]) {
                    plVar15[1] = plVar20[1];
                    *(long **)plVar20[1] = plVar15;
                    FUN_0047fd60(lVar5 + 0x88db0);
                    *(long *)(lVar5 + 0x88de0) = *(long *)(lVar5 + 0x88de0) + -1;
                  }
                  plVar20 = plVar15;
                } while ((long *)(lVar5 + 0x88da0) != plVar15);
                plVar15 = (long *)*plVar14;
              }
              *(long *)((long)plVar15 + 8) = plVar14[1];
              *(long **)plVar14[1] = plVar15;
              FUN_00454480(plVar14 + 2);
              FUN_0047fd60(lVar5 + 0x20,plVar14);
              *(long *)(lVar5 + 0x50) = *(long *)(lVar5 + 0x50) + -1;
            }
            plVar14 = plVar8;
          } while ((long *)(lVar5 + 0x10) != plVar8);
        }
      }
      else {
        local_94 = iVar18 + local_a8;
        lVar5 = *param_1;
        local_8c = (uVar17 & 0x7ff) - 0x3ff;
        local_88 = ((int)uVar17 >> 0xb & 0x7ffU) - 0x3ff;
        local_84 = (int)uVar17 >> 0x16 == 1;
        local_7c = iVar11;
        local_48 = 0;
        auVar10[8] = bVar24;
        auVar10._0_8_ = CONCAT44(iVar19,uVar9) & 0x7fffffffffff;
        auVar10._9_7_ = 0;
        local_68 = auVar10 << 0x20;
        local_58._4_4_ =
             *(undefined4 *)
              ((long)&__DT_RELA[0x548].r_addend +
              *(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar5) + 0x2f0));
        local_58._0_4_ = *(undefined4 *)(lVar5 + 0x500);
        local_58._8_8_ = 0;
        local_98 = iVar25;
        local_90 = iVar22;
        local_80 = fVar27;
        local_78 = bVar26;
        FUN_00156f20(*(undefined8 *)((long)&__DT_RELA[0xcff].r_info + lVar5),&local_98,local_68);
      }
    }
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::ClientState::REBUILD_REGION_ALT` @ 001df100
```c

undefined * jag::packethandlers::ClientState::REBUILD_REGION_ALT(long *param_1,long param_2)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  char cVar5;
  long lVar6;
  long lVar7;
  undefined8 *puVar8;
  char cVar9;
  ushort uVar10;
  long lVar11;
  undefined8 uVar12;
  long *plVar13;
  long *plVar14;
  undefined8 *puVar15;
  undefined *puVar16;
  long *plVar17;
  uint uVar18;
  long *plVar19;
  uint uVar21;
  ulong uVar22;
  long *plVar23;
  long *plVar24;
  long *plVar25;
  qword *pqVar26;
  long lVar27;
  ulong uVar28;
  int iVar29;
  int *piVar30;
  uint uVar31;
  uint uVar32;
  int iVar33;
  ulong uVar34;
  ushort uVar35;
  undefined8 *puVar36;
  uint uVar37;
  ulong uVar38;
  long *plVar39;
  bool bVar40;
  undefined8 *local_c8;
  undefined4 local_b4;
  long *local_b0;
  int local_9c;
  long local_88;
  int local_78;
  int local_60;
  int local_5c;
  undefined1 local_58 [16];
  undefined1 local_48 [24];
  ulong uVar20;
  
  if (*(char *)(*(long *)((long)&__DT_RELA[0xcf6].r_addend + *param_1) + 0x49) != '\0') {
    jag__PlayerList__ParseGpiPrefix_op81
              (*(undefined8 *)((long)&__DT_RELA[0xcfd].r_offset + *param_1));
  }
  lVar27 = *(long *)(param_2 + 0x18);
  bVar40 = DAT_01050dc0 == 0x3020100;
  lVar11 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar27 + 1;
  bVar2 = *(byte *)(lVar11 + lVar27);
  *(long *)(param_2 + 0x18) = lVar27 + 2;
  cVar9 = *(char *)(lVar11 + 1 + lVar27);
  *(long *)(param_2 + 0x18) = lVar27 + 4;
  uVar35 = *(ushort *)(lVar11 + 2 + lVar27);
  if (bVar40) {
    uVar35 = uVar35 << 8 | uVar35 >> 8;
  }
  *(long *)(param_2 + 0x18) = lVar27 + 6;
  bVar3 = *(byte *)(lVar11 + 5 + lVar27);
  bVar4 = *(byte *)(lVar11 + 4 + lVar27);
  *(long *)(param_2 + 0x18) = lVar27 + 8;
  cVar5 = *(char *)(lVar11 + 7 + lVar27);
  if (cVar5 == -1) {
    local_9c = 4;
    local_b4 = 4;
  }
  else if (cVar5 == -2) {
    local_9c = 5;
    local_b4 = 5;
  }
  else if (cVar5 == -3) {
    local_9c = 4;
    local_b4 = 6;
  }
  else {
    if (cVar5 != -4) {
      return &DAT_015d35c0;
    }
    local_9c = 5;
    local_b4 = 7;
  }
  if (cVar9 != '\x05') {
    return &DAT_015d35c0;
  }
  lVar27 = *(long *)((long)&__DT_RELA[0xcf5].r_info + *param_1);
  *(uint *)((long)&__DT_RELA[0x428].r_info + *(long *)((long)&__DT_RELA[0xcfb].r_addend + *param_1))
       = -(uint)bVar2 - 0x80 & 0xff;
  iVar29 = *(int *)(lVar27 + 0x610);
  iVar33 = iVar29 + 0xf;
  if (-1 < iVar29) {
    iVar33 = iVar29;
  }
  local_5c = ((uint)uVar35 + -(iVar33 >> 4)) * 8;
  local_60 = (((uint)bVar3 * 0x100 + (uint)bVar4 & 0xffff) + -(iVar33 >> 4)) * 8;
  uVar35 = FUN_00121a30(param_2);
  uVar10 = FUN_00121a30(param_2);
  lVar27 = *(long *)(param_2 + 0x18);
  *(long *)(param_2 + 0x18) = lVar27 + 1;
  bVar2 = *(byte *)(*(long *)(param_2 + 0x10) + lVar27);
  *(long *)(param_2 + 0x18) = lVar27 + 2;
  uVar21 = (uint)bVar2;
  uVar31 = (uint)*(byte *)(*(long *)(param_2 + 0x10) + 1 + lVar27);
  local_58._0_8_ = FUN_00c29430(0xa8);
  if ((undefined8 *)local_58._0_8_ == (undefined8 *)0x0) {
    local_c8 = (undefined8 *)0x0;
    local_58 = (undefined1  [16])0x0;
  }
  else {
    local_c8 = (undefined8 *)(local_58._0_8_ + 0x20);
    puVar15 = (undefined8 *)(local_58._0_8_ + 0x48);
    *(undefined ***)local_58._0_8_ = &PTR_FUN_01363b20;
    *(undefined8 *)(local_58._0_8_ + 8) = 0x100000001;
    *(undefined8 **)(local_58._0_8_ + 0x40) = puVar15;
    *(undefined8 **)(local_58._0_8_ + 0x28) = puVar15;
    local_58._8_8_ = local_c8;
    *(undefined8 **)(local_58._0_8_ + 0x20) = puVar15;
    *(undefined8 **)(local_58._0_8_ + 0x30) = (undefined8 *)(local_58._0_8_ + 0xa8);
    *(undefined8 *)(local_58._0_8_ + 0x10) = 1;
    *(undefined8 **)(local_58._0_8_ + 0x18) = local_c8;
  }
  uVar38 = (ulong)uVar21;
  uVar22 = (ulong)uVar31;
  uVar20 = (ulong)(uint)(*(int *)(param_2 + 0x18) * 8);
  local_78 = 4;
  do {
    plVar17 = (long *)local_c8[1];
    if (plVar17 < (long *)local_c8[2]) {
      uVar34 = 0;
      lVar27 = 0;
      plVar19 = (long *)0x0;
      pqVar26 = (qword *)(plVar17 + 3);
      local_c8[1] = pqVar26;
      *plVar17 = 0;
      plVar17[1] = 0;
      plVar17[2] = 0;
      if (uVar38 != 0) goto LAB_001dfb8b;
LAB_001df3c0:
      plVar17 = (long *)(lVar27 + uVar38 * 0x18);
      if (plVar17 != plVar19) {
        plVar14 = plVar17;
        plVar39 = plVar17 + 3;
        while( true ) {
          if (*plVar14 != 0) {
            HeapInterface::Free();
          }
          if (plVar39 ==
              (long *)((long)plVar17 +
                      ((long)plVar19 - (long)(plVar17 + 3) & 0xfffffffffffffff8U) + 0x18)) break;
          plVar14 = plVar39;
          plVar39 = plVar39 + 3;
        }
        plVar19 = (long *)(uVar38 * 0x18 + *(long *)(pqVar26 + -3));
      }
      pqVar26[-2] = (qword)plVar19;
    }
    else {
      plVar19 = (long *)*local_c8;
      local_88 = (long)plVar17 - (long)plVar19 >> 3;
      if (local_88 * -0x5555555555555555 == 0) {
        local_88 = 0x18;
LAB_001df779:
        local_b0 = (long *)FUN_00c29480(local_88);
        local_88 = local_88 + (long)local_b0;
        pqVar26 = (qword *)(local_b0 + 3);
        plVar19 = (long *)*local_c8;
        plVar14 = (long *)local_c8[1];
      }
      else {
        if (local_88 * 0x5555555555555556 != 0) {
          local_88 = local_88 << 4;
          goto LAB_001df779;
        }
        local_88 = 0;
        pqVar26 = &Elf64_Ehdr_00000000.e_entry;
        local_b0 = (long *)0x0;
        plVar14 = plVar17;
      }
      if (plVar14 == plVar19) {
        *local_b0 = 0;
        local_b0[1] = 0;
        local_b0[2] = 0;
        plVar17 = local_b0;
      }
      else {
        plVar39 = plVar19 + 3;
        plVar17 = local_b0 + 3;
        uVar34 = ((ulong)((long)plVar14 - (long)plVar39) >> 3) * 0xaaaaaaaaaaaaaab &
                 0x1fffffffffffffff;
        uVar18 = (int)((ulong)((long)(plVar17 + uVar34 * 3) + (-0x18 - (long)local_b0)) >> 3) *
                 -0x55555555 + 1U & 3;
        plVar14 = local_b0;
        plVar24 = plVar19;
        if (uVar18 != 0) {
          if (uVar18 != 1) {
            plVar13 = local_b0;
            plVar23 = plVar19;
            if (uVar18 != 2) {
              local_b0[1] = 0;
              local_b0[2] = 0;
              *local_b0 = 0;
              *local_b0 = *plVar19;
              *plVar19 = 0;
              lVar27 = local_b0[1];
              local_b0[1] = plVar19[1];
              plVar19[1] = lVar27;
              lVar27 = local_b0[2];
              local_b0[2] = plVar19[2];
              plVar19[2] = lVar27;
              plVar13 = plVar17;
              plVar23 = plVar39;
            }
            plVar13[1] = 0;
            plVar13[2] = 0;
            plVar24 = plVar23 + 3;
            *plVar13 = 0;
            plVar14 = plVar13 + 3;
            *plVar13 = *plVar23;
            *plVar23 = 0;
            lVar27 = plVar13[1];
            plVar13[1] = plVar23[1];
            plVar23[1] = lVar27;
            lVar27 = plVar13[2];
            plVar13[2] = plVar23[2];
            plVar23[2] = lVar27;
          }
          plVar14[1] = 0;
          plVar14[2] = 0;
          *plVar14 = 0;
          *plVar14 = *plVar24;
          *plVar24 = 0;
          lVar27 = plVar14[1];
          plVar14[1] = plVar24[1];
          plVar24[1] = lVar27;
          lVar27 = plVar14[2];
          plVar14[2] = plVar24[2];
          plVar24[2] = lVar27;
          plVar14 = plVar14 + 3;
          plVar24 = plVar24 + 3;
          goto joined_r0x001df90f;
        }
        do {
          plVar14[1] = 0;
          plVar14[2] = 0;
          *plVar14 = 0;
          *plVar14 = *plVar24;
          *plVar24 = 0;
          lVar27 = plVar14[1];
          plVar14[1] = plVar24[1];
          plVar24[1] = lVar27;
          lVar27 = plVar14[2];
          plVar14[2] = plVar24[2];
          plVar24[2] = lVar27;
          plVar14[4] = 0;
          plVar14[5] = 0;
          plVar14[3] = 0;
          plVar14[3] = plVar24[3];
          plVar24[3] = 0;
          lVar27 = plVar14[4];
          plVar14[4] = plVar24[4];
          plVar24[4] = lVar27;
          lVar27 = plVar14[5];
          plVar14[5] = plVar24[5];
          plVar24[5] = lVar27;
          plVar14[6] = 0;
          plVar14[7] = 0;
          plVar14[8] = 0;
          plVar14[6] = plVar24[6];
          plVar24[6] = 0;
          lVar27 = plVar14[7];
          plVar14[7] = plVar24[7];
          plVar24[7] = lVar27;
          lVar27 = plVar14[8];
          plVar14[8] = plVar24[8];
          plVar24[8] = lVar27;
          plVar14[10] = 0;
          plVar14[0xb] = 0;
          plVar14[9] = 0;
          plVar14[9] = plVar24[9];
          plVar24[9] = 0;
          lVar27 = plVar14[10];
          plVar14[10] = plVar24[10];
          plVar24[10] = lVar27;
          lVar27 = plVar14[0xb];
          plVar14[0xb] = plVar24[0xb];
          plVar24[0xb] = lVar27;
          plVar14 = plVar14 + 0xc;
          plVar24 = plVar24 + 0xc;
joined_r0x001df90f:
        } while (plVar17 + uVar34 * 3 != plVar14);
        lVar27 = uVar34 * 3 + 3;
        plVar17 = local_b0 + lVar27;
        pqVar26 = (qword *)(local_b0 + uVar34 * 3 + 6);
        *plVar17 = 0;
        plVar17[1] = 0;
        plVar17[2] = 0;
        plVar17 = plVar19;
        while( true ) {
          plVar14 = (long *)plVar17[1];
          plVar24 = (long *)*plVar17;
          if (plVar14 != plVar24) {
            plVar13 = plVar24 + 3;
            plVar23 = plVar24;
            while( true ) {
              plVar25 = plVar13;
              if (*plVar23 != 0) {
                HeapInterface::Free();
              }
              if (plVar25 ==
                  plVar24 + (((ulong)((long)plVar14 - (long)(plVar24 + 3)) >> 3) * 0xaaaaaaaaaaaaaab
                            & 0x1fffffffffffffff) * 3 + 3) break;
              plVar13 = plVar25 + 3;
              plVar23 = plVar25;
            }
            plVar24 = (long *)*plVar17;
          }
          if (plVar24 != (long *)0x0) {
            HeapInterface::Free();
          }
          if (plVar39 == plVar19 + lVar27) break;
          plVar17 = plVar39;
          plVar39 = plVar39 + 3;
        }
        plVar14 = (long *)*local_c8;
        plVar17 = local_b0 + uVar34 * 3 + 3;
      }
      if ((plVar14 != (long *)0x0) && ((long *)local_c8[4] != plVar14)) {
        HeapInterface::Free();
      }
      local_c8[2] = local_88;
      plVar19 = (long *)pqVar26[-2];
      lVar27 = *(long *)(pqVar26 + -3);
      *local_c8 = local_b0;
      local_c8[1] = pqVar26;
      uVar34 = ((long)plVar19 - lVar27 >> 3) * -0x5555555555555555;
      if (uVar38 <= uVar34) goto LAB_001df3c0;
LAB_001dfb8b:
      FUN_00135200(plVar17,uVar38 - uVar34);
    }
    if (uVar21 != 0) {
      lVar27 = 0;
      do {
        plVar17 = (long *)(*(long *)(pqVar26 + -3) + lVar27);
        uVar34 = plVar17[1] - *plVar17 >> 2;
        if (uVar34 < uVar22) {
          FUN_00135b40(plVar17,uVar22 - uVar34);
        }
        else {
          plVar17[1] = *plVar17 + uVar22 * 4;
        }
        if (uVar31 != 0) {
          piVar30 = *(int **)(*(long *)(pqVar26 + -3) + lVar27);
          lVar11 = *(long *)(param_2 + 0x10);
          piVar1 = piVar30 + (ulong)(uVar31 - 1) + 1;
          uVar34 = uVar20;
          do {
            while( true ) {
              uVar37 = (uint)uVar34;
              uVar18 = uVar37 + 1;
              uVar20 = (ulong)uVar18;
              bVar2 = *(byte *)(lVar11 + (uVar34 >> 3));
              uVar32 = (uint)bVar2;
              if ((uVar37 & 7) != 7) {
                uVar32 = (int)(uint)bVar2 >> (~(byte)uVar34 & 7);
              }
              if ((uVar32 & 1) != 1) break;
              uVar34 = (ulong)(uVar18 >> 3);
              uVar20 = (ulong)(uVar37 + 0x1b);
              iVar29 = 8 - (uVar18 & 7);
              iVar33 = 0;
              uVar18 = 0x1a;
              uVar28 = (1L << ((byte)iVar29 & 0x3f)) - 1;
              do {
                uVar34 = uVar34 + 1;
                uVar18 = uVar18 - iVar29;
                iVar33 = iVar33 + (int)((*(byte *)(lVar11 + -1 + uVar34) & uVar28) <<
                                       ((byte)uVar18 & 0x3f));
                bVar40 = iVar29 != 8;
                iVar29 = 8;
                if (bVar40) {
                  uVar28 = 0xff;
                }
              } while (8 < uVar18);
              if (uVar18 == 8) {
                uVar18 = (uint)uVar28 & (uint)*(byte *)(lVar11 + uVar34);
              }
              else {
                uVar18 = (int)(uint)*(byte *)(lVar11 + uVar34) >> (8 - (byte)uVar18 & 0x1f);
              }
              *piVar30 = iVar33 + uVar18;
              piVar30 = piVar30 + 1;
              uVar34 = uVar20;
              if (piVar1 == piVar30) goto LAB_001df550;
            }
            *piVar30 = -1;
            piVar30 = piVar30 + 1;
            uVar34 = uVar20;
          } while (piVar1 != piVar30);
        }
LAB_001df550:
        lVar27 = lVar27 + 0x18;
      } while (((ulong)(uVar21 - 1) * 3 + 3) * 8 != lVar27);
    }
    local_78 = local_78 + -1;
  } while (local_78 != 0);
  lVar27 = *param_1;
  *(ulong *)(param_2 + 0x18) = (ulong)((int)uVar20 + 7U >> 3);
  lVar11 = game::SceneManager::GetActiveWorld
                     (*(undefined8 *)((long)&__DT_RELA[0xd02].r_info + lVar27));
  if ((local_9c == 4) || (*(long *)(lVar11 + 8) == 0)) {
    FUN_0048f9e0(lVar27);
    lVar27 = *(long *)((long)&__DT_RELA[0xd02].r_info + *param_1);
    *(undefined1 *)
     ((long)&__DT_RELA[0x428].r_offset + *(long *)((long)&__DT_RELA[0xcfb].r_addend + *param_1)) = 1
    ;
    FUN_006379c0(lVar27);
    lVar11 = *(long *)(lVar27 + 0x60);
    lVar6 = *(long *)(lVar27 + 0x58);
    puVar15 = (undefined8 *)FUN_00c29480(0xe8340);
    if (puVar15 == (undefined8 *)0x0) {
      puVar36 = (undefined8 *)0x0;
      puVar15 = (undefined8 *)0x0;
      local_48._0_16_ = (undefined1  [16])0x0;
    }
    else {
      puVar36 = puVar15 + 4;
      local_48._0_16_ = (undefined1  [16])0x0;
      FUN_00643d10(puVar36,lVar27,*(undefined8 *)(lVar27 + 0x28),*(undefined8 *)(lVar27 + 8),
                   *(undefined8 *)(lVar27 + 0x10),*(undefined8 *)(lVar27 + 0x18),
                   *(undefined8 *)(lVar27 + 0x20),local_48,local_b4,(uint)(uVar35 >> 3),
                   (uint)(uVar10 >> 3),((int)uVar21 >> 3) + (uint)(uVar35 >> 3),
                   ((int)uVar31 >> 3) + (uint)(uVar10 >> 3),local_58);
      if (local_48._0_8_ != 0) {
        ref_counter_base::DecRef();
      }
      puVar15[1] = 0x100000001;
      puVar15[2] = 1;
      *puVar15 = &PTR_FUN_01389368;
      puVar15[3] = puVar36;
      LOCK();
      *(int *)(puVar15 + 1) = *(int *)(puVar15 + 1) + 1;
      UNLOCK();
      piVar1 = (int *)((long)puVar15 + 0xc);
      LOCK();
      *piVar1 = *piVar1 + 1;
      UNLOCK();
      LOCK();
      *piVar1 = *piVar1 + 1;
      UNLOCK();
      lVar7 = puVar15[5];
      puVar15[6] = puVar36;
      puVar15[5] = puVar15;
      if (lVar7 != 0) {
        FUN_0047fe30();
      }
      FUN_0047fe30(puVar15);
      ref_counter_base::DecRef(puVar15);
      local_48._8_8_ = puVar36;
      local_48._0_8_ = puVar15;
    }
    puVar8 = *(undefined8 **)(lVar27 + 0x60);
    if (puVar8 < *(undefined8 **)(lVar27 + 0x68)) {
      *puVar8 = puVar15;
      puVar8[1] = puVar36;
      *(undefined8 **)(lVar27 + 0x60) = puVar8 + 2;
    }
    else {
      FUN_00608d90(lVar27 + 0x58,local_48);
      if (local_48._0_8_ != 0) {
        ref_counter_base::DecRef();
      }
    }
    iVar29 = (int)(lVar11 - lVar6 >> 4);
    *(int *)(lVar27 + 0x70) = iVar29;
    if (*(long *)(lVar27 + 0x30) != 0) {
      puVar16 = &DAT_015df2b0;
      if (iVar29 != -1) {
        puVar16 = (undefined *)((long)iVar29 * 0x10 + *(long *)(lVar27 + 0x58));
      }
      lVar11 = *(long *)(puVar16 + 8);
      lVar6 = *(long *)(lVar11 + 0xc01a8);
      *(float *)(lVar11 + 0xe82e8) =
           (float)(*(int *)(*(long *)(lVar27 + 0x30) + 0x318) + -2) * DAT_00cb6b10;
      *(undefined1 *)(*(long *)(lVar11 + 0x20) + 0xa0) = 1;
      *(undefined8 *)(lVar11 + 0xc01a8) = 0;
      *(undefined8 *)(lVar11 + 0xc01b0) = 0;
      if (lVar6 != 0) {
        ref_counter_base::DecRef();
      }
    }
  }
  else {
    cVar9 = FUN_00636240(*(long *)(lVar11 + 8),local_58,local_b4,0);
    if (cVar9 == '\0') {
      lVar27 = *param_1;
      goto LAB_001df5ec;
    }
  }
  lVar27 = *param_1;
  lVar11 = *(long *)((long)&__DT_RELA[0xcf5].r_offset + lVar27);
  if ((lVar11 != 0) && (*(long *)(lVar11 + 0x20) != 0)) {
    FUN_00436c50();
    lVar27 = *param_1;
  }
LAB_001df5ec:
  lVar27 = *(long *)((long)&__DT_RELA[0xca7].r_info + lVar27);
  uVar12 = std__chrono___V2__steady_clock__now();
  *(undefined8 *)("gmtime_r" + lVar27 + 3) = uVar12;
  *(long *)("gmtime_r" + lVar27 + 3) = *(long *)("gmtime_r" + lVar27 + 3) + 300000000000;
  FUN_00143fa0(*(undefined8 *)((long)&__DT_RELA[0xcf5].r_info + *param_1),local_b4,&local_60);
  lVar27 = *param_1;
  lVar11 = *(long *)((long)&__DT_RELA[0xcf6].r_addend + lVar27);
  if (*(char *)(lVar11 + 0x49) != '\0') {
    *(undefined1 *)(lVar11 + 0x49) = 0;
  }
  lVar11 = *(long *)((long)&__DT_RELA[0xcf5].r_info + lVar27);
  if (*(int *)(lVar11 + 0x418) != 6) {
    game::Camera::ProcessCameraReset
              (lVar11,*(undefined1 *)
                       (*(long *)(*(long *)((long)&__DT_RELA[0xca6].r_info + lVar27) + 0x240) + 0xd8
                       ));
  }
  if (local_58._0_8_ != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```
