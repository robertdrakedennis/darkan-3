# Handler decompilations from the binary (headless RE)


## `jag::packethandlers::Interfaces::BindHandlers` @ 000ab888
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: void BindHandlers(undefined8 thisPtr) */

void jag::packethandlers::Interfaces::BindHandlers(undefined8 thisPtr)

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
  undefined8 uVar10;
  undefined8 uVar11;
  undefined8 uVar12;
  undefined8 uVar13;
  undefined8 uVar14;
  undefined8 uVar15;
  undefined8 uVar16;
  undefined8 uVar17;
  undefined8 uVar18;
  undefined8 uVar19;
  undefined8 uVar20;
  code *pcVar21;
  undefined4 uVar22;
  undefined4 uVar23;
  bool bVar24;
  undefined8 local_28;
  undefined8 uStack_20;
  code *local_18;
  undefined8 local_10;
  
                    /* jag::packethandlers::Interfaces::BindHandlers — binds the IF_* interface
                       packet handlers into ServerProt entries (first data-slot 0x015c0ca0,
                       unchanged from 948-2-2). References the IF_SET* handlers at their 948-5
                       addresses incl. IF_SETHIDE(0x194140), IF_SETANIM_ACTIVE(0x185980),
                       IF_SETOBJECT_ACTIVE(0x185ca0), IF_SETPLAYERHEAD_ACTIVE(0x1936b0). Address
                       unchanged 948-2-2 -> 948-5. */
  local_18 = DAT_015c0ca0;
  DAT_015c0ca0 = FUN_00174fd0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  local_10 = DAT_015c0ca8;
  DAT_015c0ca8 = IF_OPENSUB;
  local_28 = CONCAT44(DAT_015c0c90_4,_DAT_015c0c90);
  uStack_20._0_4_ = uRam00000000015c0c98;
  uStack_20._4_4_ = uRam00000000015c0c9c;
  uRam00000000015c0c98 = uVar22;
  uRam00000000015c0c9c = uVar23;
  _DAT_015c0c90 = thisPtr;
  if (local_18 != (code *)0x0) {
    (*local_18)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0c60;
  bVar24 = DAT_015c0c60 != (code *)0x0;
  local_18 = DAT_015c0c60;
  DAT_015c0c60 = FUN_00174fa0;
  local_10 = DAT_015c0c68;
  DAT_015c0c68 = IF_OPENTOP;
  local_28 = CONCAT44(DAT_015c0c50_4,_DAT_015c0c50);
  uStack_20._0_4_ = uRam00000000015c0c58;
  uStack_20._4_4_ = uRam00000000015c0c5c;
  uRam00000000015c0c58 = uVar22;
  uRam00000000015c0c5c = uVar23;
  _DAT_015c0c50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0c20;
  bVar24 = DAT_015c0c20 != (code *)0x0;
  local_18 = DAT_015c0c20;
  DAT_015c0c20 = FUN_00174f70;
  local_10 = DAT_015c0c28;
  DAT_015c0c28 = IF_SETTOPLEVELINTERFACE;
  local_28 = CONCAT44(DAT_015c0c10_4,_DAT_015c0c10);
  uStack_20._0_4_ = uRam00000000015c0c18;
  uStack_20._4_4_ = uRam00000000015c0c1c;
  uRam00000000015c0c18 = uVar22;
  uRam00000000015c0c1c = uVar23;
  _DAT_015c0c10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0be0;
  bVar24 = DAT_015c0be0 != (code *)0x0;
  local_18 = DAT_015c0be0;
  DAT_015c0be0 = FUN_00174f40;
  local_10 = DAT_015c0be8;
  DAT_015c0be8 = IF_SETPOSITION;
  local_28 = CONCAT44(DAT_015c0bd0_4,_DAT_015c0bd0);
  uStack_20 = CONCAT44(uRam00000000015c0bdc,uRam00000000015c0bd8);
  uRam00000000015c0bd8 = uVar22;
  uRam00000000015c0bdc = uVar23;
  _DAT_015c0bd0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0ba0;
  bVar24 = DAT_015c0ba0 != (code *)0x0;
  local_18 = DAT_015c0ba0;
  DAT_015c0ba0 = FUN_00174f10;
  local_10 = DAT_015c0ba8;
  DAT_015c0ba8 = IF_SETPLAYERMODEL_OTHER;
  local_28 = _DAT_015c0b90;
  uStack_20 = uRam00000000015c0b98;
  uRam00000000015c0b98 = uVar1;
  _DAT_015c0b90 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0b60;
  bVar24 = DAT_015c0b60 != (code *)0x0;
  local_18 = DAT_015c0b60;
  DAT_015c0b60 = FUN_00174ee0;
  local_10 = DAT_015c0b68;
  DAT_015c0b68 = IF_SETPLAYERMODEL_SELF;
  local_28 = _DAT_015c0b50;
  uStack_20 = uRam00000000015c0b58;
  uRam00000000015c0b58 = uVar1;
  _DAT_015c0b50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0b20;
  bVar24 = DAT_015c0b20 != (code *)0x0;
  local_18 = DAT_015c0b20;
  DAT_015c0b20 = FUN_00174eb0;
  local_10 = DAT_015c0b28;
  DAT_015c0b28 = IF_SETANGLE;
  local_28 = _DAT_015c0b10;
  uStack_20 = uRam00000000015c0b18;
  uRam00000000015c0b18 = uVar1;
  _DAT_015c0b10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0ae0;
  bVar24 = DAT_015c0ae0 != (code *)0x0;
  local_18 = DAT_015c0ae0;
  DAT_015c0ae0 = FUN_00174e80;
  local_10 = DAT_015c0ae8;
  DAT_015c0ae8 = IF_SETPLAYERMODEL_SNAPSHOT;
  local_28 = _DAT_015c0ad0;
  uStack_20 = uRam00000000015c0ad8;
  uRam00000000015c0ad8 = uVar1;
  _DAT_015c0ad0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c0aa0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c0aa0 != (code *)0x0;
  local_18 = DAT_015c0aa0;
  DAT_015c0aa0 = FUN_00174e50;
  local_10 = DAT_015c0aa8;
  DAT_015c0aa8 = IF_CLOSESUB_ACTIVE;
  local_28 = CONCAT44(DAT_015c0a90_4,_DAT_015c0a90);
  uStack_20._0_4_ = uRam00000000015c0a98;
  uStack_20._4_4_ = uRam00000000015c0a9c;
  uRam00000000015c0a98 = uVar22;
  uRam00000000015c0a9c = uVar23;
  _DAT_015c0a90 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0a60;
  bVar24 = DAT_015c0a60 != (code *)0x0;
  local_18 = DAT_015c0a60;
  DAT_015c0a60 = FUN_00174e20;
  local_10 = DAT_015c0a68;
  DAT_015c0a68 = IF_SUBSWAP;
  local_28 = CONCAT44(DAT_015c0a50_4,_DAT_015c0a50);
  uStack_20._0_4_ = uRam00000000015c0a58;
  uStack_20._4_4_ = uRam00000000015c0a5c;
  uRam00000000015c0a58 = uVar22;
  uRam00000000015c0a5c = uVar23;
  _DAT_015c0a50 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0a20;
  bVar24 = DAT_015c0a20 != (code *)0x0;
  local_18 = DAT_015c0a20;
  DAT_015c0a20 = FUN_00174df0;
  local_10 = DAT_015c0a28;
  DAT_015c0a28 = IF_SETEVENTS2;
  local_28 = CONCAT44(DAT_015c0a10_4,_DAT_015c0a10);
  uStack_20._0_4_ = uRam00000000015c0a18;
  uStack_20._4_4_ = uRam00000000015c0a1c;
  uRam00000000015c0a18 = uVar22;
  uRam00000000015c0a1c = uVar23;
  _DAT_015c0a10 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c09e0;
  bVar24 = DAT_015c09e0 != (code *)0x0;
  local_18 = DAT_015c09e0;
  DAT_015c09e0 = FUN_00174dc0;
  local_10 = DAT_015c09e8;
  DAT_015c09e8 = IF_SETEVENTS;
  local_28 = CONCAT44(DAT_015c09d0_4,_DAT_015c09d0);
  uStack_20 = CONCAT44(uRam00000000015c09dc,uRam00000000015c09d8);
  uRam00000000015c09d8 = uVar22;
  uRam00000000015c09dc = uVar23;
  _DAT_015c09d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c09a0;
  bVar24 = DAT_015c09a0 != (code *)0x0;
  local_18 = DAT_015c09a0;
  DAT_015c09a0 = FUN_00174d90;
  local_10 = DAT_015c09a8;
  DAT_015c09a8 = IF_SETTEXT;
  local_28 = _DAT_015c0990;
  uStack_20 = uRam00000000015c0998;
  uRam00000000015c0998 = uVar1;
  _DAT_015c0990 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0960;
  bVar24 = DAT_015c0960 != (code *)0x0;
  local_18 = DAT_015c0960;
  DAT_015c0960 = FUN_00174d60;
  local_10 = DAT_015c0968;
  DAT_015c0968 = IF_SETHIDE;
  local_28 = _DAT_015c0950;
  uStack_20 = uRam00000000015c0958;
  uRam00000000015c0958 = uVar1;
  _DAT_015c0950 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0920;
  bVar24 = DAT_015c0920 != (code *)0x0;
  local_18 = DAT_015c0920;
  DAT_015c0920 = FUN_00174d30;
  local_10 = DAT_015c0928;
  DAT_015c0928 = IF_SET2DANGLE;
  local_28 = _DAT_015c0910;
  uStack_20 = uRam00000000015c0918;
  uRam00000000015c0918 = uVar1;
  _DAT_015c0910 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c08e0;
  bVar24 = DAT_015c08e0 != (code *)0x0;
  local_18 = DAT_015c08e0;
  DAT_015c08e0 = FUN_00174d00;
  local_10 = DAT_015c08e8;
  DAT_015c08e8 = IF_SET_MODEL_FRAME;
  local_28 = _DAT_015c08d0;
  uStack_20 = uRam00000000015c08d8;
  uRam00000000015c08d8 = uVar1;
  _DAT_015c08d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c08a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c08a0 != (code *)0x0;
  local_18 = DAT_015c08a0;
  DAT_015c08a0 = FUN_00174cd0;
  local_10 = DAT_015c08a8;
  DAT_015c08a8 = IF_SETOBJECT;
  local_28 = CONCAT44(DAT_015c0890_4,_DAT_015c0890);
  uStack_20._0_4_ = uRam00000000015c0898;
  uStack_20._4_4_ = uRam00000000015c089c;
  uRam00000000015c0898 = uVar22;
  uRam00000000015c089c = uVar23;
  _DAT_015c0890 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0860;
  bVar24 = DAT_015c0860 != (code *)0x0;
  local_18 = DAT_015c0860;
  DAT_015c0860 = FUN_00174ca0;
  local_10 = DAT_015c0868;
  DAT_015c0868 = IF_SETOBJECT_ACTIVE;
  local_28 = CONCAT44(DAT_015c0850_4,_DAT_015c0850);
  uStack_20._0_4_ = uRam00000000015c0858;
  uStack_20._4_4_ = uRam00000000015c085c;
  uRam00000000015c0858 = uVar22;
  uRam00000000015c085c = uVar23;
  _DAT_015c0850 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0820;
  bVar24 = DAT_015c0820 != (code *)0x0;
  local_18 = DAT_015c0820;
  DAT_015c0820 = FUN_00174c70;
  local_10 = DAT_015c0828;
  DAT_015c0828 = IF_SETOBJECT_SMALL;
  local_28 = CONCAT44(DAT_015c0810_4,_DAT_015c0810);
  uStack_20._0_4_ = uRam00000000015c0818;
  uStack_20._4_4_ = uRam00000000015c081c;
  uRam00000000015c0818 = uVar22;
  uRam00000000015c081c = uVar23;
  _DAT_015c0810 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c07e0;
  bVar24 = DAT_015c07e0 != (code *)0x0;
  local_18 = DAT_015c07e0;
  DAT_015c07e0 = FUN_00174c40;
  local_10 = DAT_015c07e8;
  DAT_015c07e8 = IF_SETMODEL;
  local_28 = CONCAT44(DAT_015c07d0_4,_DAT_015c07d0);
  uStack_20 = CONCAT44(uRam00000000015c07dc,uRam00000000015c07d8);
  uRam00000000015c07d8 = uVar22;
  uRam00000000015c07dc = uVar23;
  _DAT_015c07d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c07a0;
  bVar24 = DAT_015c07a0 != (code *)0x0;
  local_18 = DAT_015c07a0;
  DAT_015c07a0 = FUN_00174c10;
  local_10 = DAT_015c07a8;
  DAT_015c07a8 = IF_SETGRAPHIC;
  local_28 = _DAT_015c0790;
  uStack_20 = uRam00000000015c0798;
  uRam00000000015c0798 = uVar1;
  _DAT_015c0790 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0760;
  bVar24 = DAT_015c0760 != (code *)0x0;
  local_18 = DAT_015c0760;
  DAT_015c0760 = FUN_00174be0;
  local_10 = DAT_015c0768;
  DAT_015c0768 = IF_SETCOLOUR;
  local_28 = _DAT_015c0750;
  uStack_20 = uRam00000000015c0758;
  uRam00000000015c0758 = uVar1;
  _DAT_015c0750 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0720;
  bVar24 = DAT_015c0720 != (code *)0x0;
  local_18 = DAT_015c0720;
  DAT_015c0720 = FUN_00174bb0;
  local_10 = DAT_015c0728;
  DAT_015c0728 = IF_SETANIM_ACTIVE;
  local_28 = _DAT_015c0710;
  uStack_20 = uRam00000000015c0718;
  uRam00000000015c0718 = uVar1;
  _DAT_015c0710 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c06e0;
  bVar24 = DAT_015c06e0 != (code *)0x0;
  local_18 = DAT_015c06e0;
  DAT_015c06e0 = FUN_00174b80;
  local_10 = DAT_015c06e8;
  DAT_015c06e8 = IF_SETANIM;
  local_28 = _DAT_015c06d0;
  uStack_20 = uRam00000000015c06d8;
  uRam00000000015c06d8 = uVar1;
  _DAT_015c06d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c06a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c06a0 != (code *)0x0;
  local_18 = DAT_015c06a0;
  DAT_015c06a0 = FUN_00174b50;
  local_10 = DAT_015c06a8;
  DAT_015c06a8 = IF_SETNPCHEAD;
  local_28 = CONCAT44(DAT_015c0690_4,_DAT_015c0690);
  uStack_20._0_4_ = uRam00000000015c0698;
  uStack_20._4_4_ = uRam00000000015c069c;
  uRam00000000015c0698 = uVar22;
  uRam00000000015c069c = uVar23;
  _DAT_015c0690 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0660;
  bVar24 = DAT_015c0660 != (code *)0x0;
  local_18 = DAT_015c0660;
  DAT_015c0660 = FUN_00174b20;
  local_10 = DAT_015c0668;
  DAT_015c0668 = IF_SETANIM_SMALL;
  local_28 = CONCAT44(DAT_015c0650_4,_DAT_015c0650);
  uStack_20._0_4_ = uRam00000000015c0658;
  uStack_20._4_4_ = uRam00000000015c065c;
  uRam00000000015c0658 = uVar22;
  uRam00000000015c065c = uVar23;
  _DAT_015c0650 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0620;
  bVar24 = DAT_015c0620 != (code *)0x0;
  local_18 = DAT_015c0620;
  DAT_015c0620 = FUN_00174af0;
  local_10 = DAT_015c0628;
  DAT_015c0628 = IF_SETNPCMODEL;
  local_28 = CONCAT44(DAT_015c0610_4,_DAT_015c0610);
  uStack_20._0_4_ = uRam00000000015c0618;
  uStack_20._4_4_ = uRam00000000015c061c;
  uRam00000000015c0618 = uVar22;
  uRam00000000015c061c = uVar23;
  _DAT_015c0610 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c05e0;
  bVar24 = DAT_015c05e0 != (code *)0x0;
  local_18 = DAT_015c05e0;
  DAT_015c05e0 = FUN_00174ac0;
  local_10 = DAT_015c05e8;
  DAT_015c05e8 = IF_SETMODEL_COORD;
  local_28 = CONCAT44(DAT_015c05d0_4,_DAT_015c05d0);
  uStack_20 = CONCAT44(uRam00000000015c05dc,uRam00000000015c05d8);
  uRam00000000015c05d8 = uVar22;
  uRam00000000015c05dc = uVar23;
  _DAT_015c05d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c05a0;
  bVar24 = DAT_015c05a0 != (code *)0x0;
  local_18 = DAT_015c05a0;
  DAT_015c05a0 = FUN_00174a90;
  local_10 = DAT_015c05a8;
  DAT_015c05a8 = IF_SETSPRITE;
  local_28 = _DAT_015c0590;
  uStack_20 = uRam00000000015c0598;
  uRam00000000015c0598 = uVar1;
  _DAT_015c0590 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0560;
  bVar24 = DAT_015c0560 != (code *)0x0;
  local_18 = DAT_015c0560;
  DAT_015c0560 = FUN_00174a60;
  local_10 = DAT_015c0568;
  DAT_015c0568 = IF_SETRECOL;
  local_28 = _DAT_015c0550;
  uStack_20 = uRam00000000015c0558;
  uRam00000000015c0558 = uVar1;
  _DAT_015c0550 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c0520;
  bVar24 = DAT_015c0520 != (code *)0x0;
  local_18 = DAT_015c0520;
  DAT_015c0520 = FUN_00174a30;
  local_10 = DAT_015c0528;
  DAT_015c0528 = IF_SETSCROLLPOS;
  local_28 = _DAT_015c0510;
  uStack_20 = uRam00000000015c0518;
  uRam00000000015c0518 = uVar1;
  _DAT_015c0510 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c04e0;
  bVar24 = DAT_015c04e0 != (code *)0x0;
  local_18 = DAT_015c04e0;
  DAT_015c04e0 = FUN_00174a00;
  local_10 = DAT_015c04e8;
  DAT_015c04e8 = IF_SETSCROLLSIZE;
  local_28 = _DAT_015c04d0;
  uStack_20 = uRam00000000015c04d8;
  uRam00000000015c04d8 = uVar1;
  _DAT_015c04d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  pcVar21 = DAT_015c04a0;
  uVar22 = (undefined4)uStack_20;
  uVar23 = uStack_20._4_4_;
  bVar24 = DAT_015c04a0 != (code *)0x0;
  local_18 = DAT_015c04a0;
  DAT_015c04a0 = FUN_001749d0;
  local_10 = DAT_015c04a8;
  DAT_015c04a8 = IF_SETNPCHEAD_ACTIVE;
  local_28 = CONCAT44(DAT_015c0490_4,_DAT_015c0490);
  uStack_20._0_4_ = uRam00000000015c0498;
  uStack_20._4_4_ = uRam00000000015c049c;
  uRam00000000015c0498 = uVar22;
  uRam00000000015c049c = uVar23;
  _DAT_015c0490 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0460;
  bVar24 = DAT_015c0460 != (code *)0x0;
  local_18 = DAT_015c0460;
  DAT_015c0460 = FUN_001749a0;
  local_10 = DAT_015c0468;
  DAT_015c0468 = IF_SETPLAYERHEAD_ACTIVE;
  local_28 = CONCAT44(DAT_015c0450_4,_DAT_015c0450);
  uStack_20._0_4_ = uRam00000000015c0458;
  uStack_20._4_4_ = uRam00000000015c045c;
  uRam00000000015c0458 = uVar22;
  uRam00000000015c045c = uVar23;
  _DAT_015c0450 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c0420;
  bVar24 = DAT_015c0420 != (code *)0x0;
  local_18 = DAT_015c0420;
  DAT_015c0420 = FUN_00174970;
  local_10 = DAT_015c0428;
  DAT_015c0428 = IF_TRIGGER_CLOSE;
  local_28 = CONCAT44(DAT_015c0410_4,_DAT_015c0410);
  uStack_20._0_4_ = uRam00000000015c0418;
  uStack_20._4_4_ = uRam00000000015c041c;
  uRam00000000015c0418 = uVar22;
  uRam00000000015c041c = uVar23;
  _DAT_015c0410 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar23 = uStack_20._4_4_;
  uVar22 = (undefined4)uStack_20;
  pcVar21 = DAT_015c03e0;
  bVar24 = DAT_015c03e0 != (code *)0x0;
  local_18 = DAT_015c03e0;
  DAT_015c03e0 = FUN_00174940;
  local_10 = DAT_015c03e8;
  DAT_015c03e8 = IF_SETMODELORIGIN;
  local_28 = CONCAT44(DAT_015c03d0_4,_DAT_015c03d0);
  uStack_20 = CONCAT44(uRam00000000015c03dc,uRam00000000015c03d8);
  uRam00000000015c03d8 = uVar22;
  uRam00000000015c03dc = uVar23;
  _DAT_015c03d0 = thisPtr;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
  }
  uVar1 = uStack_20;
  pcVar21 = DAT_015c03a0;
  bVar24 = DAT_015c03a0 != (code *)0x0;
  local_18 = DAT_015c03a0;
  DAT_015c03a0 = FUN_00174910;
  local_10 = DAT_015c03a8;
  DAT_015c03a8 = IF_CLOSESUB_BY_ID;
  local_28 = _DAT_015c0390;
  uStack_20 = uRam00000000015c0398;
  uRam00000000015c0398 = uVar1;
  _DAT_015c0390 = thisPtr;
  uVar1 = _DAT_015c0c90;
  uVar2 = _DAT_015c0c50;
  uVar3 = _DAT_015c0c10;
  uVar4 = _DAT_015c0bd0;
  uVar5 = _DAT_015c0a90;
  uVar6 = _DAT_015c0a50;
  uVar7 = _DAT_015c0a10;
  uVar8 = _DAT_015c09d0;
  uVar9 = _DAT_015c0890;
  uVar10 = _DAT_015c0850;
  uVar11 = _DAT_015c0810;
  uVar12 = _DAT_015c07d0;
  uVar13 = _DAT_015c0690;
  uVar14 = _DAT_015c0650;
  uVar15 = _DAT_015c0610;
  uVar16 = _DAT_015c05d0;
  uVar17 = _DAT_015c0490;
  uVar18 = _DAT_015c0450;
  uVar19 = _DAT_015c0410;
  uVar20 = _DAT_015c03d0;
  if (bVar24) {
    (*pcVar21)(&local_28,&local_28,3);
    uVar1 = _DAT_015c0c90;
    uVar2 = _DAT_015c0c50;
    uVar3 = _DAT_015c0c10;
    uVar4 = _DAT_015c0bd0;
    uVar5 = _DAT_015c0a90;
    uVar6 = _DAT_015c0a50;
    uVar7 = _DAT_015c0a10;
    uVar8 = _DAT_015c09d0;
    uVar9 = _DAT_015c0890;
    uVar10 = _DAT_015c0850;
    uVar11 = _DAT_015c0810;
    uVar12 = _DAT_015c07d0;
    uVar13 = _DAT_015c0690;
    uVar14 = _DAT_015c0650;
    uVar15 = _DAT_015c0610;
    uVar16 = _DAT_015c05d0;
    uVar17 = _DAT_015c0490;
    uVar18 = _DAT_015c0450;
    uVar19 = _DAT_015c0410;
    uVar20 = _DAT_015c03d0;
  }
  DAT_015c03d0_4 = (undefined4)((ulong)uVar20 >> 0x20);
  _DAT_015c03d0 = (undefined4)uVar20;
  DAT_015c0410_4 = (undefined4)((ulong)uVar19 >> 0x20);
  _DAT_015c0410 = (undefined4)uVar19;
  DAT_015c0450_4 = (undefined4)((ulong)uVar18 >> 0x20);
  _DAT_015c0450 = (undefined4)uVar18;
  DAT_015c0490_4 = (undefined4)((ulong)uVar17 >> 0x20);
  _DAT_015c0490 = (undefined4)uVar17;
  DAT_015c05d0_4 = (undefined4)((ulong)uVar16 >> 0x20);
  _DAT_015c05d0 = (undefined4)uVar16;
  DAT_015c0610_4 = (undefined4)((ulong)uVar15 >> 0x20);
  _DAT_015c0610 = (undefined4)uVar15;
  DAT_015c0650_4 = (undefined4)((ulong)uVar14 >> 0x20);
  _DAT_015c0650 = (undefined4)uVar14;
  DAT_015c0690_4 = (undefined4)((ulong)uVar13 >> 0x20);
  _DAT_015c0690 = (undefined4)uVar13;
  DAT_015c07d0_4 = (undefined4)((ulong)uVar12 >> 0x20);
  _DAT_015c07d0 = (undefined4)uVar12;
  DAT_015c0810_4 = (undefined4)((ulong)uVar11 >> 0x20);
  _DAT_015c0810 = (undefined4)uVar11;
  DAT_015c0850_4 = (undefined4)((ulong)uVar10 >> 0x20);
  _DAT_015c0850 = (undefined4)uVar10;
  DAT_015c0890_4 = (undefined4)((ulong)uVar9 >> 0x20);
  _DAT_015c0890 = (undefined4)uVar9;
  DAT_015c09d0_4 = (undefined4)((ulong)uVar8 >> 0x20);
  _DAT_015c09d0 = (undefined4)uVar8;
  DAT_015c0a10_4 = (undefined4)((ulong)uVar7 >> 0x20);
  _DAT_015c0a10 = (undefined4)uVar7;
  DAT_015c0a50_4 = (undefined4)((ulong)uVar6 >> 0x20);
  _DAT_015c0a50 = (undefined4)uVar6;
  DAT_015c0a90_4 = (undefined4)((ulong)uVar5 >> 0x20);
  _DAT_015c0a90 = (undefined4)uVar5;
  DAT_015c0bd0_4 = (undefined4)((ulong)uVar4 >> 0x20);
  _DAT_015c0bd0 = (undefined4)uVar4;
  DAT_015c0c10_4 = (undefined4)((ulong)uVar3 >> 0x20);
  _DAT_015c0c10 = (undefined4)uVar3;
  DAT_015c0c50_4 = (undefined4)((ulong)uVar2 >> 0x20);
  _DAT_015c0c50 = (undefined4)uVar2;
  DAT_015c0c90_4 = (undefined4)((ulong)uVar1 >> 0x20);
  _DAT_015c0c90 = (undefined4)uVar1;
  return;
}


```

## `jag::packethandlers::Interfaces::IF_CLOSESUB_BY_ID` @ 00185750
```c

/* Setting prototype: undefined * IF_CLOSESUB_BY_ID(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_CLOSESUB_BY_ID(long *thisPtr,long packet)

{
  long lVar1;
  ushort uVar2;
  bool bVar3;
  
                    /* jag::packethandlers::Interfaces::IF_CLOSESUB_BY_ID — Reads a 2-byte BE
                       ushort interface id, calls jag::InterfaceManager::CloseInterface with (id,
                       1). Position 37 in Interfaces::BindHandlers binder. Behavioral signature:
                       (long*, long), 2-byte BE read, call with constant 1 as second arg. */
  lVar1 = *(long *)(packet + 0x18);
  bVar3 = DAT_01050dc0 == 0x3020100;
  *(long *)(packet + 0x18) = lVar1 + 2;
  uVar2 = *(ushort *)(*(long *)(packet + 0x10) + lVar1);
  if (bVar3) {
    uVar2 = uVar2 << 8 | uVar2 >> 8;
  }
  FUN_003fae80(*(undefined8 *)((long)&__DT_RELA[0xd05].r_info + *thisPtr),uVar2,1);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_TRIGGER_CLOSE` @ 001857a0
```c

undefined * jag::packethandlers::Interfaces::IF_TRIGGER_CLOSE(long *param_1)

{
  int iVar1;
  long lVar2;
  
  lVar2 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *param_1);
  iVar1 = *(int *)(lVar2 + 0xd8);
  if (iVar1 != -1) {
    FUN_00295f80(lVar2,iVar1,0x29);
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETNPCHEAD` @ 001857e0
```c

/* Setting prototype: undefined * IF_SETNPCHEAD(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETNPCHEAD(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  ushort uVar8;
  bool bVar9;
  
                    /* jag::packethandlers::Interfaces::IF_SETNPCHEAD — Reads NPC head packet
                       (component-id, NPC id, head model variations) and calls
                       InterfaceManager::SetComponentProperty with kind=7. Position 25 in
                       Interfaces::BindHandlers binder. 10-byte packet read with shifts and +0x80
                       transforms. */
  lVar6 = *(long *)(packet + 0x18);
  bVar9 = DAT_01050dc0 == 0x3020100;
  lVar7 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar6 + 2;
  uVar8 = *(ushort *)(lVar7 + lVar6);
  if (bVar9) {
    uVar8 = uVar8 << 8 | uVar8 >> 8;
  }
  *(long *)(packet + 0x18) = lVar6 + 4;
  bVar2 = *(byte *)(lVar7 + 3 + lVar6);
  bVar3 = *(byte *)(lVar7 + 2 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 6;
  bVar4 = *(byte *)(lVar7 + 5 + lVar6);
  bVar5 = *(byte *)(lVar7 + 4 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 10;
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),
             (uint)*(byte *)(lVar7 + 7 + lVar6) * 0x100 +
             (uint)*(byte *)(lVar7 + 9 + lVar6) * 0x1000000 +
             (uint)*(byte *)(lVar7 + 8 + lVar6) * 0x10000 + (uint)*(byte *)(lVar7 + 6 + lVar6),7,
             ((uint)bVar4 * 0x100 + (uint)bVar5) * 0x10000 |
             (uint)bVar2 * 0x100 + (uint)bVar3 & 0xffff,uVar8);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar6 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar6 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETANIM` @ 001858c0
```c

/* Setting prototype: undefined * IF_SETANIM(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETANIM(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  ushort uVar8;
  uint uVar9;
  uint *puVar10;
  
  lVar6 = *(long *)(packet + 0x18);
  lVar7 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar6 + 4;
  bVar2 = *(byte *)(lVar7 + 3 + lVar6);
  bVar3 = *(byte *)(lVar7 + 2 + lVar6);
  bVar4 = *(byte *)(lVar7 + lVar6);
  bVar5 = *(byte *)(lVar7 + 1 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 6;
  uVar8 = *(ushort *)(lVar7 + 4 + lVar6);
  puVar10 = (uint *)(lVar7 + lVar6 + 6);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(packet + 0x18) = lVar6 + 10;
    uVar9 = *puVar10;
    uVar8 = uVar8 << 8 | uVar8 >> 8;
    uVar9 = uVar9 >> 0x18 | (uVar9 & 0xff0000) >> 8 | (uVar9 & 0xff00) << 8 | uVar9 << 0x18;
  }
  else {
    *(long *)(packet + 0x18) = lVar6 + 10;
    uVar9 = *puVar10;
  }
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),uVar9,3,uVar8,
             (uint)bVar5 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar4);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar6 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar6 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETANIM_ACTIVE` @ 00185980
```c

/* Setting prototype: undefined * IF_SETANIM_ACTIVE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETANIM_ACTIVE(long *thisPtr,long packet)

{
  int *piVar1;
  long lVar2;
  undefined4 uVar3;
  
                    /* jag::packethandlers::Interfaces::IF_SETANIM_ACTIVE — Reads alt-int
                       component-id, calls SetComponentProperty with kind=3 (animation),
                       value=active-component-id (looked up via *param_1+0x19b30.0x48). 948-2
                       anchor: position 23 in Interfaces::BindHandlers binder. Behavioral match vs
                       947-3 IF_SETANIM_ACTIVE @ 0x0022b770: identical SetComponentProperty
                       signature with kind=3. */
  uVar3 = Packet::g4_alt3(packet);
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),uVar3,3,
             *(undefined4 *)(*(long *)((long)&__DT_RELA[0xd40].r_addend + *thisPtr) + 0x48),0);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar2 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETCOLOUR` @ 001859d0
```c

/* Setting prototype: undefined * IF_SETCOLOUR(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETCOLOUR(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  undefined4 uVar8;
  
  lVar6 = *(long *)(packet + 0x18);
  lVar7 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar6 + 4;
  bVar2 = *(byte *)(lVar7 + 2 + lVar6);
  bVar3 = *(byte *)(lVar7 + 3 + lVar6);
  bVar4 = *(byte *)(lVar7 + 1 + lVar6);
  bVar5 = *(byte *)(lVar7 + lVar6);
  uVar8 = Packet::g4_alt3(packet);
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),uVar8,2,
             (uint)bVar5 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar4,
             0xffffffff);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar6 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar6 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETMODEL` @ 00185a60
```c

/* Setting prototype: undefined * IF_SETMODEL(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETMODEL(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  uint uVar6;
  long lVar7;
  long lVar8;
  
  lVar7 = *(long *)(packet + 0x18);
  lVar8 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar7 + 4;
  bVar2 = *(byte *)(lVar8 + 2 + lVar7);
  bVar3 = *(byte *)(lVar8 + 3 + lVar7);
  bVar4 = *(byte *)(lVar8 + 1 + lVar7);
  bVar5 = *(byte *)(lVar8 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 8;
  uVar6 = *(uint *)(lVar8 + 4 + lVar7);
  if (DAT_01050dc0 == 0x3020100) {
    uVar6 = uVar6 >> 0x18 | (uVar6 & 0xff0000) >> 8 | (uVar6 & 0xff00) << 8 | uVar6 << 0x18;
  }
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),uVar6,1,
             (uint)bVar5 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar4,
             0xffffffff);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar7 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar7 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETOBJECT_SMALL` @ 00185b00
```c

/* Setting prototype: undefined * IF_SETOBJECT_SMALL(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETOBJECT_SMALL(long *thisPtr,long packet)

{
  int *piVar1;
  uint uVar2;
  long lVar3;
  long lVar4;
  bool bVar5;
  
  lVar3 = *(long *)(packet + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  lVar4 = *thisPtr;
  *(long *)(packet + 0x18) = lVar3 + 4;
  uVar2 = *(uint *)(*(long *)(packet + 0x10) + lVar3);
  *(long *)(packet + 0x18) = lVar3 + 5;
  if (bVar5) {
    uVar2 = uVar2 >> 0x18 | (uVar2 & 0xff0000) >> 8 | (uVar2 & 0xff00) << 8 | uVar2 << 0x18;
  }
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + lVar4),uVar2,5,
             -2 - (uint)(byte)(0x80 - *(char *)(*(long *)(packet + 0x10) + 4 + lVar3)),0);
  lVar3 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar3 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar3 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETOBJECT` @ 00185b80
```c

/* Setting prototype: undefined * IF_SETOBJECT(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETOBJECT(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  long lVar7;
  long lVar8;
  undefined4 uVar9;
  
  lVar7 = *(long *)(packet + 0x18);
  lVar8 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar7 + 4;
  bVar1 = *(byte *)(lVar8 + 3 + lVar7);
  bVar2 = *(byte *)(lVar8 + 2 + lVar7);
  bVar3 = *(byte *)(lVar8 + lVar7);
  bVar4 = *(byte *)(lVar8 + 1 + lVar7);
  uVar9 = Packet::g4_alt3(packet);
  lVar7 = *(long *)(packet + 0x18);
  lVar8 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(long *)(packet + 0x18) = lVar7 + 2;
  bVar5 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar7);
  bVar6 = *(byte *)(*(long *)(packet + 0x10) + lVar7);
  *(int *)(lVar8 + 0x10) = *(int *)(lVar8 + 0x10) + 1;
  *(undefined1 *)(lVar8 + 0x14) = 1;
  InterfaceManager::SetComponentProperty
            (lVar8,(uint)bVar4 * 0x100 + (uint)bVar1 * 0x1000000 + (uint)bVar2 * 0x10000 +
                   (uint)bVar3,5,(ushort)bVar5 * 0x100 + (ushort)bVar6,uVar9);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETANIM_SMALL` @ 00185c20
```c

/* Setting prototype: undefined * IF_SETANIM_SMALL(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETANIM_SMALL(long *thisPtr,long packet)

{
  int *piVar1;
  long lVar2;
  long lVar3;
  undefined4 uVar4;
  
  uVar4 = Packet::g4_alt2(packet);
  lVar2 = *(long *)(packet + 0x18);
  lVar3 = *thisPtr;
  *(long *)(packet + 0x18) = lVar2 + 1;
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + lVar3),uVar4,3,
             -2 - (uint)(byte)(0x80 - *(char *)(*(long *)(packet + 0x10) + lVar2)),0);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar2 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETOBJECT_ACTIVE` @ 00185ca0
```c

/* Setting prototype: undefined * IF_SETOBJECT_ACTIVE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETOBJECT_ACTIVE(long *thisPtr,long packet)

{
  int *piVar1;
  long lVar2;
  undefined4 uVar3;
  
                    /* jag::packethandlers::Interfaces::IF_SETOBJECT_ACTIVE — Sets active obj/item
                       on an interface component, kind=5. 948-2 anchor: position 18 in
                       Interfaces::BindHandlers binder. Behavioral match vs 947-3
                       IF_SETOBJECT_ACTIVE @ 0x0022b950. */
  uVar3 = Packet::g4_alt2(packet);
  InterfaceManager::SetComponentProperty
            (*(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr),uVar3,5,
             *(undefined4 *)(*(long *)((long)&__DT_RELA[0xd40].r_addend + *thisPtr) + 0x48),0);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar2 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SET_MODEL_FRAME` @ 00185cf0
```c

/* Setting prototype: undefined * IF_SET_MODEL_FRAME(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SET_MODEL_FRAME(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  uint uVar7;
  long lVar8;
  undefined8 *puVar9;
  
  puVar9 = &DAT_015db690;
  lVar8 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar8 + 4;
  bVar2 = *(byte *)(lVar6 + 1 + lVar8);
  bVar3 = *(byte *)(lVar6 + lVar8);
  bVar4 = *(byte *)(lVar6 + 2 + lVar8);
  bVar5 = *(byte *)(lVar6 + 3 + lVar8);
  uVar7 = Packet::g4_alt3(packet);
  lVar8 = *thisPtr;
  if (uVar7 != 0xffffffff) {
    lVar8 = game::InterfaceList::GetInterface
                      (*(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar8) + 0x30,uVar7 >> 0x10,0);
    if (*(long *)(lVar8 + 8) != 0) {
      puVar9 = (undefined8 *)
               (*(long *)(*(long *)(lVar8 + 8) + 8) + 8 + ((ulong)uVar7 & 0xffff) * 0x18);
    }
    lVar8 = *thisPtr;
  }
  lVar8 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar8);
  lVar6 = puVar9[1];
  piVar1 = (int *)(lVar8 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar8 + 0x14) = 1;
  *(uint *)(lVar6 + 0x194) =
       (uint)bVar5 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar4;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETTEXT` @ 00186040
```c

/* Setting prototype: undefined * IF_SETTEXT(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETTEXT(long *thisPtr,long packet)

{
  long lVar1;
  undefined4 uVar2;
  undefined1 *puVar3;
  undefined1 local_38;
  undefined7 uStack_37;
  char local_21;
  
  local_38 = 0;
  local_21 = '\x17';
  FUN_00ad80e0(packet,&local_38);
  uVar2 = Packet::g4_alt3(packet);
  lVar1 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar1 + 0x10) = *(int *)(lVar1 + 0x10) + 1;
  *(undefined1 *)(lVar1 + 0x14) = 1;
  puVar3 = (undefined1 *)CONCAT71(uStack_37,local_38);
  if (-1 < local_21) {
    puVar3 = &local_38;
  }
  InterfaceManager::SetComponentText(lVar1,1,uVar2,puVar3);
  if ((local_21 < '\0') && (CONCAT71(uStack_37,local_38) != 0)) {
    eastl__basic_string();
    return &DAT_015d3620;
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETEVENTS` @ 001860e0
```c

/* Setting prototype: undefined * IF_SETEVENTS(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETEVENTS(long *thisPtr,long packet)

{
  int *piVar1;
  char cVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  byte bVar6;
  char cVar7;
  long lVar8;
  long lVar9;
  long lVar10;
  long lVar11;
  uint uVar12;
  undefined4 uVar13;
  ulong uVar14;
  ushort uVar15;
  ushort uVar16;
  
  uVar13 = Packet::g4_alt3(packet);
  lVar8 = *(long *)(packet + 0x18);
  lVar9 = *(long *)(packet + 0x10);
  lVar10 = *thisPtr;
  *(long *)(packet + 0x18) = lVar8 + 2;
  cVar2 = *(char *)(lVar9 + lVar8);
  bVar3 = *(byte *)(lVar9 + 1 + lVar8);
  *(long *)(packet + 0x18) = lVar8 + 4;
  bVar4 = *(byte *)(lVar9 + 2 + lVar8);
  bVar5 = *(byte *)(lVar9 + 3 + lVar8);
  lVar11 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar10);
  *(long *)(packet + 0x18) = lVar8 + 6;
  bVar6 = *(byte *)(lVar9 + 5 + lVar8);
  uVar16 = (ushort)bVar5 * 0x100 + (ushort)bVar4;
  cVar7 = *(char *)(lVar9 + 4 + lVar8);
  *(undefined1 *)(lVar11 + 0x14) = 1;
  uVar15 = (ushort)bVar6 * 0x100 + (ushort)(byte)(cVar7 + 0x80);
  uVar14 = (ulong)uVar16;
  if (uVar16 == 0xffff) {
    uVar14 = 0xffffffff;
  }
  uVar12 = (uint)uVar15;
  if (uVar15 == 0xffff) {
    uVar12 = 0xffffffff;
  }
  piVar1 = (int *)(lVar11 + 0x10);
  *piVar1 = *piVar1 + 1;
  InterfaceManager::SetServerActiveProperties
            (*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + lVar10),uVar13,uVar14,uVar12,0,
             (ushort)bVar3 * 0x100 + (ushort)(byte)(cVar2 + 0x80),1);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETEVENTS2` @ 001861c0
```c

/* Setting prototype: undefined * IF_SETEVENTS2(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETEVENTS2(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  uint uVar6;
  long lVar7;
  long lVar8;
  long lVar9;
  uint uVar10;
  undefined4 uVar11;
  ushort uVar12;
  ulong uVar13;
  ushort uVar14;
  bool bVar15;
  
  uVar11 = Packet::g4_alt2(packet);
  lVar7 = *(long *)(packet + 0x18);
  lVar8 = *(long *)(packet + 0x10);
  lVar9 = *thisPtr;
  *(long *)(packet + 0x18) = lVar7 + 2;
  bVar2 = *(byte *)(lVar8 + lVar7);
  bVar3 = *(byte *)(lVar8 + 1 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 4;
  bVar4 = *(byte *)(lVar8 + 3 + lVar7);
  bVar5 = *(byte *)(lVar8 + 2 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 8;
  uVar6 = *(uint *)(lVar8 + 4 + lVar7);
  uVar12 = (ushort)bVar3 * 0x100 + (ushort)bVar2;
  uVar14 = (ushort)bVar4 * 0x100 + (ushort)bVar5;
  bVar15 = DAT_01050dc0 == 0x3020100;
  lVar7 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar9);
  *(undefined1 *)(lVar7 + 0x14) = 1;
  if (bVar15) {
    uVar6 = uVar6 >> 0x18 | (uVar6 & 0xff0000) >> 8 | (uVar6 & 0xff00) << 8 | uVar6 << 0x18;
  }
  uVar13 = (ulong)uVar12;
  if (uVar12 == 0xffff) {
    uVar13 = 0xffffffff;
  }
  uVar10 = (uint)uVar14;
  if (uVar14 == 0xffff) {
    uVar10 = 0xffffffff;
  }
  piVar1 = (int *)(lVar7 + 0x10);
  *piVar1 = *piVar1 + 1;
  InterfaceManager::SetServerActiveProperties
            (*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + lVar9),uVar6,uVar13,uVar10,uVar11,
             0xffffffff,0);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SUBSWAP` @ 00186280
```c

/* Setting prototype: undefined * IF_SUBSWAP(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SUBSWAP(long *thisPtr,long packet)

{
  int *piVar1;
  uint *puVar2;
  undefined8 uVar3;
  code *pcVar4;
  uint uVar5;
  long lVar6;
  long lVar7;
  ulong uVar8;
  long lVar9;
  ulong uVar10;
  uint uVar11;
  long lVar12;
  long lVar13;
  long local_48;
  long lStack_40;
  
  uVar5 = Packet::g4_alt2(packet);
  lVar6 = *(long *)(packet + 0x18);
  lVar13 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar6 + 4;
  lVar9 = *thisPtr;
  lVar7 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9);
  lVar12 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar9);
  uVar11 = (uint)*(byte *)(lVar13 + 1 + lVar6) * 0x100 +
           (uint)*(byte *)(lVar13 + 3 + lVar6) * 0x1000000 +
           (uint)*(byte *)(lVar13 + 2 + lVar6) * 0x10000 + (uint)*(byte *)(lVar13 + lVar6);
  uVar10 = *(ulong *)(lVar7 + 0x100);
  lVar6 = *(long *)(lVar7 + 0xf8);
  piVar1 = (int *)(lVar12 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar12 + 0x14) = 1;
  uVar8 = uVar10 & 0xffffffff;
  for (puVar2 = *(uint **)(lVar6 + ((ulong)uVar11 % uVar8) * 8); puVar2 != (uint *)0x0;
      puVar2 = *(uint **)(puVar2 + 6)) {
    if (uVar11 == *puVar2) {
      if ((puVar2 != *(uint **)(lVar6 + uVar10 * 8)) &&
         (lVar13 = *(long *)(puVar2 + 4), *(int *)(lVar13 + 8) != 2)) {
        lVar7 = *(long *)(puVar2 + 2);
        if (lVar7 != 0) {
          LOCK();
          *(int *)(lVar7 + 8) = *(int *)(lVar7 + 8) + 1;
          UNLOCK();
          lVar9 = *thisPtr;
          lVar6 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar9);
          uVar10 = *(ulong *)(lVar6 + 0x100);
          lVar6 = *(long *)(lVar6 + 0xf8);
          uVar8 = uVar10 & 0xffffffff;
        }
        goto LAB_00186376;
      }
      break;
    }
  }
  lVar13 = 0;
  lVar7 = 0;
LAB_00186376:
  puVar2 = *(uint **)(lVar6 + ((ulong)uVar5 % uVar8) * 8);
  do {
    if (puVar2 == (uint *)0x0) {
LAB_001864c0:
      if (lVar13 != 0) {
        lVar12 = 0;
LAB_0018642a:
        uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + lVar9);
        if (lVar7 != 0) {
          LOCK();
          *(int *)(lVar7 + 8) = *(int *)(lVar7 + 8) + 1;
          UNLOCK();
        }
        local_48 = lVar7;
        lStack_40 = lVar13;
        FUN_0029fdb0(uVar3,&local_48,(ulong)uVar5);
        if (local_48 != 0) {
          ref_counter_base::DecRef();
        }
        if (lVar12 != 0) {
          ref_counter_base::DecRef(lVar12);
        }
      }
      if (lVar7 != 0) {
        ref_counter_base::DecRef(lVar7);
      }
      return &DAT_015d3620;
    }
    if (uVar5 == *puVar2) {
      if ((*(uint **)(lVar6 + uVar10 * 8) != puVar2) &&
         (lVar6 = *(long *)(puVar2 + 4), *(int *)(lVar6 + 8) != 2)) {
        lVar12 = *(long *)(puVar2 + 2);
        if (lVar12 != 0) {
          LOCK();
          *(int *)(lVar12 + 8) = *(int *)(lVar12 + 8) + 1;
          UNLOCK();
        }
        if (lVar13 == 0) {
                    /* WARNING: Does not return */
          pcVar4 = (code *)invalidInstructionException();
          (*pcVar4)();
        }
        uVar3 = *(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
        if (lVar12 != 0) {
          LOCK();
          *(int *)(lVar12 + 8) = *(int *)(lVar12 + 8) + 1;
          UNLOCK();
        }
        local_48 = lVar12;
        lStack_40 = lVar6;
        FUN_002b41f0(uVar3,&local_48,1,0);
        if (local_48 != 0) {
          ref_counter_base::DecRef();
        }
        lVar9 = *thisPtr;
        goto LAB_0018642a;
      }
      goto LAB_001864c0;
    }
    puVar2 = *(uint **)(puVar2 + 6);
  } while( true );
}


```

## `jag::packethandlers::Interfaces::IF_CLOSESUB_ACTIVE` @ 001864e0
```c

/* Setting prototype: undefined * IF_CLOSESUB_ACTIVE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_CLOSESUB_ACTIVE(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  uint *puVar8;
  uint uVar9;
  long lVar10;
  long lVar11;
  long local_28;
  long lStack_20;
  
  lVar11 = *(long *)(packet + 0x18);
  lVar6 = *thisPtr;
  lVar10 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar6);
  *(long *)(packet + 0x18) = lVar11 + 4;
  lVar7 = *(long *)(packet + 0x10);
  bVar2 = *(byte *)(lVar7 + 3 + lVar11);
  bVar3 = *(byte *)(lVar7 + 2 + lVar11);
  bVar4 = *(byte *)(lVar7 + 1 + lVar11);
  bVar5 = *(byte *)(lVar7 + lVar11);
  piVar1 = (int *)(lVar10 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar10 + 0x14) = 1;
  uVar9 = (uint)bVar4 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar5;
  lVar11 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar6);
  puVar8 = *(uint **)(*(long *)(lVar11 + 0xf8) +
                     ((ulong)uVar9 % (*(ulong *)(lVar11 + 0x100) & 0xffffffff)) * 8);
  do {
    if (puVar8 == (uint *)0x0) {
LAB_00186640:
      lVar6 = *(long *)(lVar11 + 0x158);
      lVar10 = 0;
      *(undefined8 *)(lVar11 + 0x158) = 0;
      *(undefined8 *)(lVar11 + 0x160) = 0;
      if (lVar6 == 0) {
        return &DAT_015d3620;
      }
LAB_00186618:
      ref_counter_base::DecRef();
LAB_0018661d:
      if (lVar10 != 0) {
        ref_counter_base::DecRef(lVar10);
      }
      return &DAT_015d3620;
    }
    if (uVar9 == *puVar8) {
      if ((*(uint **)(*(long *)(lVar11 + 0xf8) + *(ulong *)(lVar11 + 0x100) * 8) != puVar8) &&
         (lVar6 = *(long *)(puVar8 + 4), *(int *)(lVar6 + 8) != 2)) {
        lVar10 = *(long *)(puVar8 + 2);
        if (lVar10 != 0) {
          piVar1 = (int *)(lVar10 + 8);
          LOCK();
          *piVar1 = *piVar1 + 1;
          UNLOCK();
          lVar11 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
          LOCK();
          *piVar1 = *piVar1 + 1;
          UNLOCK();
        }
        local_28 = lVar10;
        lStack_20 = lVar6;
        FUN_002b41f0(lVar11,&local_28,1,0);
        if (local_28 != 0) {
          ref_counter_base::DecRef();
        }
        lVar11 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
        lVar6 = *(long *)(lVar11 + 0x158);
        *(undefined8 *)(lVar11 + 0x160) = 0;
        *(undefined8 *)(lVar11 + 0x158) = 0;
        if (lVar6 != 0) goto LAB_00186618;
        goto LAB_0018661d;
      }
      goto LAB_00186640;
    }
    puVar8 = *(uint **)(puVar8 + 6);
  } while( true );
}


```

## `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SELF` @ 001866a0
```c

/* Setting prototype: undefined * IF_SETPLAYERMODEL_SELF(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SELF(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  undefined *puVar6;
  long lVar7;
  undefined8 *puVar8;
  ushort uVar9;
  ushort *puVar10;
  uint uVar11;
  uint uVar12;
  undefined8 *local_48;
  undefined8 *puStack_40;
  
  lVar7 = *(long *)(packet + 0x18);
  lVar5 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar7 + 2;
  bVar2 = *(byte *)(lVar5 + lVar7);
  bVar3 = *(byte *)(lVar5 + 1 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 6;
  uVar11 = *(uint *)(lVar5 + 2 + lVar7);
  puVar10 = (ushort *)(lVar5 + lVar7 + 6);
  if (DAT_01050dc0 == 0x3020100) {
    *(long *)(packet + 0x18) = lVar7 + 8;
    uVar9 = *puVar10;
    uVar11 = uVar11 >> 0x18 | (uVar11 & 0xff0000) >> 8 | (uVar11 & 0xff00) << 8 | uVar11 << 0x18;
    uVar9 = uVar9 << 8 | uVar9 >> 8;
  }
  else {
    *(long *)(packet + 0x18) = lVar7 + 8;
    uVar9 = *puVar10;
  }
  Packet::g4_alt3(packet);
  lVar7 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar7 + 1;
  bVar4 = *(byte *)(*(long *)(packet + 0x10) + lVar7);
  Packet::gT_unsigned_int((PacketCore *)packet);
  Packet::g4_alt1((PacketCore *)packet);
  Packet::g4_alt3(packet);
  puVar6 = &DAT_015d3600;
  lVar7 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  if (*(long *)(lVar7 + 0xe8) != 0) {
    uVar12 = (uint)bVar2 * 0x100 + (bVar3 - 0x80 & 0xff) & 0xffff;
    lVar7 = game::InterfaceList::GetInterface(lVar7 + 0x30,uVar12,0);
    puVar6 = &DAT_015d35c0;
    if (*(long *)(lVar7 + 8) != 0) {
      lVar7 = *thisPtr;
      puVar8 = (undefined8 *)0x0;
      lVar5 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar7);
      piVar1 = (int *)(lVar5 + 0x10);
      *piVar1 = *piVar1 + 1;
      *(undefined1 *)(lVar5 + 0x14) = 1;
      local_48 = (undefined8 *)FUN_00c29430(0x50);
      if (local_48 != (undefined8 *)0x0) {
        puVar8 = local_48 + 4;
        *(uint *)(local_48 + 6) = uVar11;
        *(undefined4 *)(local_48 + 5) = 1;
        *(uint *)((long)local_48 + 0x2c) = uVar12;
        *(undefined1 *)(local_48 + 7) = 0;
        local_48[4] = &PTR_FUN_01363bd8;
        local_48[8] = lVar7;
        *(uint *)(local_48 + 9) = (uint)uVar9;
        *local_48 = &PTR_FUN_01363ba8;
        local_48[1] = 0x100000001;
        *(uint *)((long)local_48 + 0x34) = -(uint)bVar4 & 0xff;
        local_48[2] = 1;
        local_48[3] = puVar8;
      }
      puStack_40 = puVar8;
      FUN_002bc520(*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr),&local_48,0);
      puVar6 = &DAT_015d3620;
      if (local_48 != (undefined8 *)0x0) {
        ref_counter_base::DecRef();
        puVar6 = &DAT_015d3620;
      }
    }
  }
  return puVar6;
}


```

## `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_OTHER` @ 00186890
```c

/* Setting prototype: undefined * IF_SETPLAYERMODEL_OTHER(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_OTHER(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  uint uVar5;
  long lVar6;
  long lVar7;
  undefined *puVar8;
  long lVar9;
  uint uVar10;
  bool bVar11;
  undefined8 *local_48;
  undefined8 *puStack_40;
  
  lVar9 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar9 + 2;
  bVar2 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar9);
  bVar3 = *(byte *)(*(long *)(packet + 0x10) + lVar9);
  Packet::gT_unsigned_int((PacketCore *)packet);
  lVar9 = *(long *)(packet + 0x18);
  bVar11 = DAT_01050dc0 == 0x3020100;
  *(long *)(packet + 0x18) = lVar9 + 4;
  uVar5 = *(uint *)(*(long *)(packet + 0x10) + lVar9);
  if (bVar11) {
    uVar5 = uVar5 >> 0x18 | (uVar5 & 0xff0000) >> 8 | (uVar5 & 0xff00) << 8 | uVar5 << 0x18;
  }
  Packet::gT_unsigned_int((PacketCore *)packet);
  Packet::g4_alt2(packet);
  Packet::gT_unsigned_int((PacketCore *)packet);
  lVar9 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar9 + 1;
  bVar4 = *(byte *)(lVar6 + lVar9);
  *(long *)(packet + 0x18) = lVar9 + 3;
  puVar8 = &DAT_015d3600;
  lVar7 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  if (*(long *)(lVar7 + 0xe8) != 0) {
    uVar10 = (uint)*(byte *)(lVar6 + 1 + lVar9) * 0x100 +
             (*(byte *)(lVar6 + 2 + lVar9) - 0x80 & 0xff) & 0xffff;
    lVar9 = game::InterfaceList::GetInterface(lVar7 + 0x30,uVar10,0);
    puVar8 = &DAT_015d35c0;
    if (*(long *)(lVar9 + 8) != 0) {
      lVar9 = *thisPtr;
      lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar9);
      piVar1 = (int *)(lVar6 + 0x10);
      *piVar1 = *piVar1 + 1;
      *(undefined1 *)(lVar6 + 0x14) = 1;
      local_48 = (undefined8 *)FUN_00c29430(0x50);
      puStack_40 = (undefined8 *)0x0;
      if (local_48 != (undefined8 *)0x0) {
        *(uint *)(local_48 + 6) = uVar5;
        puStack_40 = local_48 + 4;
        *(undefined4 *)(local_48 + 5) = 1;
        *(uint *)((long)local_48 + 0x2c) = uVar10;
        *(uint *)((long)local_48 + 0x34) = -(uint)bVar4 - 0x80 & 0xff;
        *(undefined1 *)(local_48 + 7) = 0;
        local_48[4] = &PTR_FUN_01363b80;
        local_48[8] = lVar9;
        *(uint *)(local_48 + 9) = (uint)bVar2 * 0x100 + (uint)bVar3 & 0xffff;
        *local_48 = &PTR_FUN_01363b50;
        local_48[1] = 0x100000001;
        local_48[2] = 1;
        local_48[3] = puStack_40;
      }
      FUN_002bc520(*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr),&local_48,0);
      puVar8 = &DAT_015d3620;
      if (local_48 != (undefined8 *)0x0) {
        ref_counter_base::DecRef();
        puVar8 = &DAT_015d3620;
      }
    }
  }
  return puVar8;
}


```

## `jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE` @ 00186a80
```c

/* Setting prototype: undefined * IF_SETTOPLEVELINTERFACE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETTOPLEVELINTERFACE(long *thisPtr,long packet)

{
  int *piVar1;
  char cVar2;
  byte bVar3;
  long lVar4;
  undefined8 uVar5;
  long lVar6;
  uint uVar7;
  
  Packet::gT_unsigned_int((PacketCore *)packet);
  *(long *)(packet + 0x18) = *(long *)(packet + 0x18) + 1;
  Packet::g4_alt2(packet);
  lVar4 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar4 + 2;
  cVar2 = *(char *)(*(long *)(packet + 0x10) + lVar4);
  bVar3 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar4);
  Packet::g4_alt1((PacketCore *)packet);
  uVar7 = (uint)bVar3 * 0x100 + (uint)(byte)(cVar2 + 0x80) & 0xffff;
  Packet::g4_alt1((PacketCore *)packet);
  lVar4 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  uVar5 = *(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  piVar1 = (int *)(lVar4 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar4 + 0x14) = 1;
  InterfaceManager::CloseInterface(uVar5,uVar7);
  lVar4 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  lVar6 = *(long *)(lVar4 + 0xe0);
  *(uint *)(lVar4 + 0xd8) = uVar7;
  *(undefined8 *)(lVar4 + 0xe0) = 0;
  *(undefined8 *)(lVar4 + 0xe8) = 0;
  if (lVar6 != 0) {
    ref_counter_base::DecRef();
  }
  lVar6 = *(long *)(lVar4 + 0x170);
  *(undefined8 *)(lVar4 + 0x178) = 0;
  *(undefined8 *)(lVar4 + 0x170) = 0;
  if (lVar6 != 0) {
    ref_counter_base::DecRef();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETPOSITION` @ 00189300
```c

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */
/* Setting prototype: undefined * IF_SETPOSITION(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETPOSITION(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  long lVar3;
  undefined8 uVar4;
  undefined8 uVar5;
  undefined8 uVar6;
  undefined4 uVar7;
  int iVar8;
  long lVar9;
  undefined8 *puVar10;
  undefined *puVar11;
  undefined8 *puVar12;
  undefined8 *puVar13;
  uint uVar14;
  ulong uVar15;
  uint uVar16;
  undefined8 *puVar17;
  undefined8 *puVar18;
  undefined8 *puVar19;
  bool bVar20;
  undefined8 *local_58;
  undefined8 *puStack_50;
  pthread_mutex_t *local_48;
  char local_40;
  
  lVar9 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar9 + 1;
  bVar2 = *(byte *)(*(long *)(packet + 0x10) + lVar9);
  Packet::gT_unsigned_int((PacketCore *)packet);
  uVar7 = Packet::g4_alt3(packet);
  Packet::gT_unsigned_int((PacketCore *)packet);
  Packet::g4_alt2(packet);
  Packet::gT_unsigned_int((PacketCore *)packet);
  lVar9 = *(long *)(packet + 0x18);
  lVar3 = *thisPtr;
  *(long *)(packet + 0x18) = lVar9 + 2;
  lVar3 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + lVar3);
  if (*(long *)(lVar3 + 0xe8) == 0) {
    return &DAT_015d3600;
  }
  uVar14 = (uint)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar9) * 0x100 +
           (uint)*(byte *)(*(long *)(packet + 0x10) + lVar9) & 0xffff;
  lVar9 = game::InterfaceList::GetInterface(lVar3 + 0x30,uVar14,0);
  if (*(long *)(lVar9 + 8) == 0) {
    puVar11 = &DAT_015d35c0;
  }
  else {
    local_40 = 0;
    local_48 = (pthread_mutex_t *)&DAT_01396280;
    lVar9 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
    piVar1 = (int *)(lVar9 + 0x10);
    *piVar1 = *piVar1 + 1;
    bVar20 = PTR___pthread_key_create_01390dc0 != (undefined *)0x0;
    *(undefined1 *)(lVar9 + 0x14) = 1;
    if ((bVar20) && (iVar8 = pthread_mutex_lock((pthread_mutex_t *)&DAT_01396280), iVar8 != 0)) {
                    /* WARNING: Subroutine does not return */
      FUN_00c8e5c0(iVar8);
    }
    local_40 = '\x01';
    if ((DAT_01396270 == '\0') && (iVar8 = __cxa_guard_acquire(&DAT_01396270), iVar8 != 0)) {
      DAT_01396250 = (undefined8 *)0x0;
      DAT_01396258 = (undefined8 *)0x0;
      _DAT_01396260 = (undefined8 *)0x0;
      _DAT_01396268 = 0x10;
      puVar12 = (undefined8 *)thunk_FUN_00c29480(0x28);
      puVar10 = DAT_01396250;
      puVar17 = puVar12;
      if (DAT_01396258 != DAT_01396250) {
        puVar17 = DAT_01396250 + 5;
        uVar15 = ((ulong)((long)DAT_01396258 - (long)puVar17) >> 3) * 0xccccccccccccccd &
                 0x1fffffffffffffff;
        uVar16 = (int)((ulong)((long)(puVar17 + uVar15 * 5) + (-0x28 - (long)DAT_01396250)) >> 3) *
                 -0x33333333 + 1U & 3;
        puVar18 = puVar12;
        if (uVar16 == 0) goto LAB_00189717;
        if (uVar16 != 1) {
          puVar13 = DAT_01396250;
          puVar19 = puVar12;
          if (uVar16 != 2) {
            uVar4 = DAT_01396250[3];
            puVar19 = puVar12 + 5;
            uVar5 = *DAT_01396250;
            puVar12[1] = DAT_01396250[1];
            uVar6 = puVar10[2];
            puVar12[3] = uVar4;
            uVar4 = puVar10[4];
            *puVar12 = uVar5;
            puVar12[2] = uVar6;
            puVar12[4] = uVar4;
            *puVar10 = 0;
            puVar10[1] = 0;
            puVar10[2] = 0;
            puVar10[3] = 0;
            puVar10[4] = 0;
            puVar13 = puVar17;
          }
          uVar4 = puVar13[3];
          puVar18 = puVar19 + 5;
          uVar5 = *puVar13;
          puVar10 = puVar13 + 5;
          puVar19[1] = puVar13[1];
          uVar6 = puVar13[2];
          puVar19[3] = uVar4;
          uVar4 = puVar13[4];
          *puVar19 = uVar5;
          puVar19[2] = uVar6;
          puVar19[4] = uVar4;
          *puVar13 = 0;
          puVar13[1] = 0;
          puVar13[2] = 0;
          puVar13[3] = 0;
          puVar13[4] = 0;
        }
        uVar4 = puVar10[3];
        uVar5 = *puVar10;
        puVar18[1] = puVar10[1];
        uVar6 = puVar10[2];
        puVar18[3] = uVar4;
        uVar4 = puVar10[4];
        *puVar18 = uVar5;
        puVar18[2] = uVar6;
        puVar18[4] = uVar4;
        *puVar10 = 0;
        puVar10[1] = 0;
        puVar10[2] = 0;
        puVar10[3] = 0;
        puVar10[4] = 0;
        puVar18 = puVar18 + 5;
        for (puVar10 = puVar10 + 5; puVar10 != puVar17 + uVar15 * 5; puVar10 = puVar10 + 0x14) {
LAB_00189717:
          uVar4 = puVar10[3];
          uVar5 = *puVar10;
          puVar18[1] = puVar10[1];
          uVar6 = puVar10[2];
          puVar18[3] = uVar4;
          uVar4 = puVar10[4];
          *puVar18 = uVar5;
          uVar5 = puVar10[5];
          puVar18[2] = uVar6;
          uVar6 = puVar10[6];
          puVar18[4] = uVar4;
          uVar4 = puVar10[8];
          *puVar10 = 0;
          puVar10[1] = 0;
          puVar10[2] = 0;
          puVar10[3] = 0;
          puVar10[4] = 0;
          puVar18[5] = uVar5;
          puVar18[6] = uVar6;
          uVar5 = puVar10[10];
          uVar6 = puVar10[7];
          puVar18[8] = uVar4;
          uVar4 = puVar10[9];
          puVar18[7] = uVar6;
          puVar18[9] = uVar4;
          puVar10[5] = 0;
          puVar10[6] = 0;
          puVar10[7] = 0;
          puVar10[8] = 0;
          puVar10[9] = 0;
          puVar18[10] = uVar5;
          uVar4 = puVar10[0xd];
          uVar5 = puVar10[0xf];
          puVar18[0xb] = puVar10[0xb];
          uVar6 = puVar10[0xc];
          puVar18[0xd] = uVar4;
          uVar4 = puVar10[0xe];
          puVar18[0xc] = uVar6;
          uVar6 = puVar10[0x10];
          puVar18[0xe] = uVar4;
          uVar4 = puVar10[0x12];
          puVar10[10] = 0;
          puVar10[0xb] = 0;
          puVar10[0xc] = 0;
          puVar10[0xd] = 0;
          puVar10[0xe] = 0;
          puVar18[0x10] = uVar6;
          uVar6 = puVar10[0x11];
          puVar18[0x12] = uVar4;
          uVar4 = puVar10[0x13];
          puVar18[0xf] = uVar5;
          puVar18[0x11] = uVar6;
          puVar18[0x13] = uVar4;
          puVar10[0xf] = 0;
          puVar10[0x10] = 0;
          puVar10[0x11] = 0;
          puVar10[0x12] = 0;
          puVar10[0x13] = 0;
          puVar18 = puVar18 + 0x14;
        }
        puVar17 = puVar12 + uVar15 * 5 + 5;
      }
      *puVar17 = 0;
      puVar17[1] = 0;
      puVar17[2] = 0;
      puVar17[3] = 0;
      puVar17[4] = 0;
      FUN_00b07420(puVar17,0x80,0x10);
      if (DAT_01396258 != DAT_01396250) {
        puVar10 = (undefined8 *)
                  ((long)DAT_01396250 +
                  ((long)DAT_01396258 - (long)(DAT_01396250 + 5) & 0xfffffffffffffff8U) + 0x28);
        puVar18 = DAT_01396250 + 5;
        puVar19 = DAT_01396250;
        while( true ) {
          puVar13 = puVar18;
          if (puVar19[4] != 0) {
            lVar9 = puVar19[1];
            uVar15 = 0;
            do {
              *(ulong *)(lVar9 + uVar15 * 8) = uVar15;
              uVar15 = uVar15 + 1;
            } while (uVar15 < (ulong)puVar19[4]);
          }
          puVar19[3] = 0;
          if (puVar19[2] != 0) {
            HeapInterface::Free();
          }
          puVar19[2] = 0;
          if (puVar19[1] == 0) {
            HeapInterface::Free(*puVar19);
          }
          else {
            HeapInterface::Free();
            puVar19[1] = 0;
            HeapInterface::Free(*puVar19);
          }
          if (puVar13 == puVar10) break;
          puVar18 = puVar13 + 5;
          puVar19 = puVar13;
        }
      }
      if (DAT_01396250 != (undefined8 *)0x0) {
        HeapInterface::Free(DAT_01396250);
      }
      _DAT_01396260 = puVar12 + 5;
      DAT_01396250 = puVar12;
      DAT_01396258 = puVar17 + 5;
      __cxa_guard_release(&DAT_01396270);
      __cxa_atexit(FUN_001241c0,&DAT_01396250,&PTR_LOOP_01391000);
    }
    puVar10 = (undefined8 *)FUN_00122850(&DAT_01396250);
    if (local_40 == '\0') {
                    /* WARNING: Subroutine does not return */
      FUN_00c8e5c0(1);
    }
    if (local_48 != (pthread_mutex_t *)0x0) {
      if (PTR___pthread_key_create_01390dc0 != (undefined *)0x0) {
        pthread_mutex_unlock(local_48);
      }
      local_40 = '\0';
    }
    puStack_50 = puVar10 + 4;
    *(uint *)((long)puVar10 + 0x2c) = uVar14;
    puVar10[4] = &PTR_FUN_01365cb8;
    puVar10[1] = 0x100000001;
    *puVar10 = &PTR_FUN_01365c88;
    *(undefined4 *)(puVar10 + 5) = 1;
    *(undefined4 *)(puVar10 + 6) = uVar7;
    *(uint *)((long)puVar10 + 0x34) = -(uint)bVar2 - 0x80 & 0xff;
    *(undefined1 *)(puVar10 + 7) = 0;
    puVar10[2] = puVar10;
    puVar10[3] = puStack_50;
    local_58 = puVar10;
    if (local_40 != '\0') {
      FUN_0047e230(&local_48);
    }
    FUN_002bc520(*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr),&local_58,0);
    puVar11 = &DAT_015d3620;
    if (local_58 != (undefined8 *)0x0) {
      ref_counter_base::DecRef();
      return &DAT_015d3620;
    }
  }
  return puVar11;
}


```

## `jag::packethandlers::Interfaces::IF_SETMODELORIGIN` @ 001935a0
```c

/* Setting prototype: undefined * IF_SETMODELORIGIN(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETMODELORIGIN(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  char cVar6;
  long lVar7;
  undefined4 uVar8;
  long lVar9;
  uint local_34;
  uint local_30;
  uint local_2c;
  
  lVar7 = *(long *)(packet + 0x18);
  lVar9 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar7 + 2;
  bVar1 = *(byte *)(lVar9 + lVar7);
  bVar2 = *(byte *)(lVar9 + 1 + lVar7);
  *(long *)(packet + 0x18) = lVar7 + 4;
  bVar3 = *(byte *)(lVar9 + 3 + lVar7);
  bVar4 = *(byte *)(lVar9 + 2 + lVar7);
  uVar8 = Packet::g4_alt3(packet);
  lVar7 = *(long *)(packet + 0x18);
  lVar9 = *thisPtr;
  *(long *)(packet + 0x18) = lVar7 + 2;
  bVar5 = *(byte *)(*(long *)(packet + 0x10) + 1 + lVar7);
  cVar6 = *(char *)(*(long *)(packet + 0x10) + lVar7);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar9);
  *(int *)(lVar7 + 0x10) = *(int *)(lVar7 + 0x10) + 1;
  *(undefined1 *)(lVar7 + 0x14) = 1;
  lVar9 = InterfaceManager::CreateOrFindUpdateEntry(lVar7,8,uVar8);
  InterfaceManager::MarkUpdateEntryDirty(lVar7,lVar9);
  local_2c = (uint)bVar5 * 0x100 + (uint)(byte)(cVar6 + 0x80) & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar9 + 0x20,&local_2c);
  local_30 = (uint)bVar3 * 0x100 + (uint)bVar4 & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar9 + 0x40,&local_30);
  local_34 = (uint)bVar2 * 0x100 + (bVar1 - 0x80 & 0xff) & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar9 + 0x60,&local_34);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETPLAYERHEAD_ACTIVE` @ 001936b0
```c

/* Setting prototype: undefined * IF_SETPLAYERHEAD_ACTIVE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETPLAYERHEAD_ACTIVE(long *thisPtr,long packet)

{
  char cVar1;
  long lVar2;
  undefined4 uVar3;
  long lVar4;
  uint local_1c;
  
                    /* jag::packethandlers::Interfaces::IF_SETPLAYERHEAD_ACTIVE — Reads 1 char +
                       4-byte alt-int component-id, calls CreateOrFindUpdateEntry with kind=0x15,
                       sets update slot value to (cVar1 == 1). 948-2 anchor: position 34 in
                       Interfaces::BindHandlers binder. Behavioral match vs 947-3
                       IF_SETPLAYERHEAD_ACTIVE @ 0x0023dea0: identical signature, identical
                       kind=0x15, identical 1-char read followed by alt-int read, identical cVar1==1
                       boolean check, identical single update slot write at +0x20. */
  lVar2 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar2 + 1;
  cVar1 = *(char *)(*(long *)(packet + 0x10) + lVar2);
  uVar3 = Packet::g4_alt3(packet);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  lVar4 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,0x15,uVar3);
  InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar4);
  local_1c = (uint)(cVar1 == '\x01');
  InterfaceManager::SetUpdateSlotValue(lVar4 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETNPCHEAD_ACTIVE` @ 00193740
```c

/* Setting prototype: undefined * IF_SETNPCHEAD_ACTIVE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETNPCHEAD_ACTIVE(long *thisPtr,long packet)

{
  char cVar1;
  long lVar2;
  long lVar3;
  uint uVar4;
  bool bVar5;
  uint local_1c;
  
  lVar3 = *(long *)(packet + 0x18);
  bVar5 = DAT_01050dc0 == 0x3020100;
  *(long *)(packet + 0x18) = lVar3 + 4;
  uVar4 = *(uint *)(*(long *)(packet + 0x10) + lVar3);
  *(long *)(packet + 0x18) = lVar3 + 5;
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  if (bVar5) {
    uVar4 = uVar4 >> 0x18 | (uVar4 & 0xff0000) >> 8 | (uVar4 & 0xff00) << 8 | uVar4 << 0x18;
  }
  cVar1 = *(char *)(*(long *)(packet + 0x10) + 4 + lVar3);
  *(undefined1 *)(lVar2 + 0x14) = 1;
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  lVar3 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,0x14,uVar4);
  InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar3);
  local_1c = (uint)(cVar1 == '\x01');
  InterfaceManager::SetUpdateSlotValue(lVar3 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETSCROLLSIZE` @ 001937e0
```c

/* Setting prototype: undefined * IF_SETSCROLLSIZE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETSCROLLSIZE(long *thisPtr,long packet)

{
  char cVar1;
  byte bVar2;
  char cVar3;
  long lVar4;
  uint uVar5;
  long lVar6;
  byte bVar7;
  ushort uVar8;
  uint local_34;
  uint local_30;
  uint local_2c;
  
  lVar4 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar4 + 2;
  cVar1 = *(char *)(lVar6 + lVar4);
  bVar2 = *(byte *)(lVar6 + 1 + lVar4);
  *(long *)(packet + 0x18) = lVar4 + 3;
  cVar3 = *(char *)(lVar6 + 2 + lVar4);
  *(long *)(packet + 0x18) = lVar4 + 5;
  uVar8 = *(ushort *)(lVar6 + 3 + lVar4);
  bVar7 = cVar3 + 0x80;
  if (DAT_01050dc0 == 0x3020100) {
    uVar8 = uVar8 << 8 | uVar8 >> 8;
  }
  uVar5 = Packet::g4_alt3(packet);
  lVar4 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar4 + 0x10) = *(int *)(lVar4 + 0x10) + 1;
  *(undefined1 *)(lVar4 + 0x14) = 1;
  lVar6 = InterfaceManager::CreateOrFindUpdateEntry
                    (lVar4,0x12,(uint)bVar7 + (uint)bVar7 * 4 | uVar5);
  InterfaceManager::MarkUpdateEntryDirty(lVar4,lVar6);
  lVar6 = lVar6 + 0x20;
  local_2c = (uint)bVar7;
  InterfaceManager::SetUpdateSlotValue(lVar6,&local_2c);
  local_30 = (uint)uVar8;
  InterfaceManager::SetUpdateSlotValue(lVar6,&local_30);
  local_34 = (uint)bVar2 * 0x100 + (uint)(byte)(cVar1 + 0x80) & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar6,&local_34);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETSCROLLPOS` @ 001938e0
```c

/* Setting prototype: undefined * IF_SETSCROLLPOS(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETSCROLLPOS(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  uint uVar6;
  long lVar7;
  byte bVar8;
  uint local_34;
  uint local_30;
  uint local_2c;
  
  lVar5 = *(long *)(packet + 0x18);
  lVar7 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar5 + 2;
  bVar1 = *(byte *)(lVar7 + 1 + lVar5);
  bVar2 = *(byte *)(lVar7 + lVar5);
  *(long *)(packet + 0x18) = lVar5 + 3;
  bVar8 = 0x80 - *(char *)(lVar7 + 2 + lVar5);
  *(long *)(packet + 0x18) = lVar5 + 5;
  bVar3 = *(byte *)(lVar7 + 4 + lVar5);
  bVar4 = *(byte *)(lVar7 + 3 + lVar5);
  uVar6 = Packet::g4_alt3(packet);
  lVar5 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar5 + 0x10) = *(int *)(lVar5 + 0x10) + 1;
  *(undefined1 *)(lVar5 + 0x14) = 1;
  lVar7 = InterfaceManager::CreateOrFindUpdateEntry
                    (lVar5,0x11,(uint)bVar8 + (uint)bVar8 * 4 | uVar6);
  InterfaceManager::MarkUpdateEntryDirty(lVar5,lVar7);
  lVar7 = lVar7 + 0x20;
  local_2c = (uint)bVar8;
  InterfaceManager::SetUpdateSlotValue(lVar7,&local_2c);
  local_30 = (uint)bVar3 * 0x100 + (uint)bVar4 & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar7,&local_30);
  local_34 = (uint)bVar1 * 0x100 + (bVar2 - 0x80 & 0xff) & 0xffff;
  InterfaceManager::SetUpdateSlotValue(lVar7,&local_34);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETRECOL` @ 001939e0
```c

/* Setting prototype: undefined * IF_SETRECOL(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETRECOL(long *thisPtr,long packet)

{
  char cVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  uint uVar9;
  int local_1c;
  
  lVar8 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  lVar7 = *thisPtr;
  *(long *)(packet + 0x18) = lVar8 + 2;
  cVar1 = *(char *)(lVar6 + lVar8);
  bVar2 = *(byte *)(lVar6 + 1 + lVar8);
  *(long *)(packet + 0x18) = lVar8 + 6;
  bVar3 = *(byte *)(lVar6 + 4 + lVar8);
  lVar7 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar7);
  bVar4 = *(byte *)(lVar6 + 3 + lVar8);
  uVar9 = (uint)bVar2 * 0x100 + (uint)(byte)(cVar1 + 0x80);
  bVar2 = *(byte *)(lVar6 + 5 + lVar8);
  bVar5 = *(byte *)(lVar6 + 2 + lVar8);
  *(int *)(lVar7 + 0x10) = *(int *)(lVar7 + 0x10) + 1;
  *(undefined1 *)(lVar7 + 0x14) = 1;
  lVar8 = InterfaceManager::CreateOrFindUpdateEntry
                    (lVar7,6,(uint)bVar4 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 +
                             (uint)bVar5);
  InterfaceManager::MarkUpdateEntryDirty(lVar7,lVar8);
  local_1c = ((uVar9 & 0x3e0) << 6 | (uVar9 & 0x7c00) << 9) + (uVar9 & 0x1f) * 8;
  InterfaceManager::SetUpdateSlotValue(lVar8 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETSPRITE` @ 00193ac0
```c

/* Setting prototype: undefined * IF_SETSPRITE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETSPRITE(long *thisPtr,long packet)

{
  uint uVar1;
  long lVar2;
  uint uVar3;
  undefined4 uVar4;
  long lVar5;
  bool bVar6;
  undefined4 local_1c;
  
  lVar2 = *(long *)(packet + 0x18);
  bVar6 = DAT_01050dc0 != 0x3020100;
  *(long *)(packet + 0x18) = lVar2 + 4;
  uVar1 = *(uint *)(*(long *)(packet + 0x10) + lVar2);
  uVar3 = uVar1 >> 0x18 | (uVar1 & 0xff0000) >> 8 | (uVar1 & 0xff00) << 8 | uVar1 << 0x18;
  if (bVar6) {
    uVar3 = uVar1;
  }
  uVar4 = Packet::g4_alt3(packet);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  lVar5 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,0xf,uVar3);
  InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar5);
  local_1c = uVar4;
  InterfaceManager::SetUpdateSlotValue(lVar5 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETMODEL_COORD` @ 00193b50
```c

/* Setting prototype: undefined * IF_SETMODEL_COORD(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETMODEL_COORD(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  short sVar3;
  short sVar4;
  short sVar5;
  uint uVar6;
  long lVar7;
  undefined8 uVar8;
  uint uVar9;
  undefined4 uVar10;
  undefined4 uVar11;
  long lVar12;
  undefined8 *puVar13;
  ushort uVar14;
  bool bVar15;
  uint local_54;
  int local_50;
  undefined4 local_4c;
  undefined4 local_48;
  uint local_44;
  int local_40;
  int local_3c [3];
  
  lVar12 = *(long *)(packet + 0x18);
  bVar15 = DAT_01050dc0 == 0x3020100;
  *(long *)(packet + 0x18) = lVar12 + 4;
  uVar6 = *(uint *)(*(long *)(packet + 0x10) + lVar12);
  if (bVar15) {
    uVar6 = uVar6 >> 0x18 | (uVar6 & 0xff0000) >> 8 | (uVar6 & 0xff00) << 8 | uVar6 << 0x18;
  }
  uVar10 = Packet::g4_alt3(packet);
  uVar11 = Packet::g4_alt3(packet);
  lVar12 = *(long *)(packet + 0x18);
  lVar7 = *thisPtr;
  *(long *)(packet + 0x18) = lVar12 + 2;
  uVar14 = (ushort)*(byte *)(*(long *)(packet + 0x10) + lVar12) * 0x100 +
           (ushort)(byte)(*(char *)(*(long *)(packet + 0x10) + 1 + lVar12) + 0x80);
  uVar8 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + lVar7);
  uVar9 = (uint)uVar14;
  if (uVar14 == 0xffff) {
    uVar9 = 0xffffffff;
  }
  lVar12 = InterfaceManager::CreateOrFindUpdateEntry(uVar8,0x16,uVar6);
  InterfaceManager::MarkUpdateEntryDirty(uVar8,lVar12);
  local_54 = uVar9;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x20,&local_54);
  bVar2 = *(byte *)(lVar12 + 0x58);
  if (bVar2 != 0xff) {
    if (bVar2 == 1) {
      *(ulong *)(lVar12 + 0x40) = CONCAT44(uVar10,uVar11);
      goto LAB_00193c47;
    }
    (*(code *)(&PTR_FUN_01384ba0)[bVar2])(lVar12 + 0x40);
  }
  *(ulong *)(lVar12 + 0x40) = CONCAT44(uVar10,uVar11);
  *(undefined1 *)(lVar12 + 0x58) = 1;
LAB_00193c47:
  lVar12 = *thisPtr;
  puVar13 = &DAT_015c7b20;
  lVar7 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar12);
  if (*(int *)(lVar7 + 0x3c) == 4) {
    puVar13 = (undefined8 *)FUN_00ad81b0(lVar7,uVar9);
    lVar12 = *thisPtr;
  }
  lVar7 = puVar13[1];
  uVar8 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + lVar12);
  sVar3 = *(short *)(lVar7 + 0x2a8);
  sVar4 = *(short *)(lVar7 + 0x2aa);
  uVar14 = *(ushort *)(lVar7 + 0x2a6);
  sVar5 = *(short *)(lVar7 + 0x2ac);
  lVar12 = InterfaceManager::CreateOrFindUpdateEntry(uVar8,8,uVar6);
  InterfaceManager::MarkUpdateEntryDirty(uVar8,lVar12);
  local_3c[0] = (int)sVar3;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x20,local_3c);
  local_40 = (int)sVar4;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x40,&local_40);
  local_44 = (uint)uVar14;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x60,&local_44);
  uVar10 = *(undefined4 *)(lVar7 + 0x2b4);
  uVar11 = *(undefined4 *)(lVar7 + 0x2b0);
  uVar8 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  lVar12 = InterfaceManager::CreateOrFindUpdateEntry(uVar8,10,uVar6);
  InterfaceManager::MarkUpdateEntryDirty(uVar8,lVar12);
  local_48 = uVar11;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x20,&local_48);
  local_4c = uVar10;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x40,&local_4c);
  local_50 = (int)sVar5;
  InterfaceManager::SetUpdateSlotValue(lVar12 + 0x60,&local_50);
  lVar12 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar12 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar12 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETNPCMODEL` @ 00193dd0
```c

/* Setting prototype: undefined * IF_SETNPCMODEL(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETNPCMODEL(long *thisPtr,long packet)

{
  int *piVar1;
  short sVar2;
  short sVar3;
  short sVar4;
  undefined4 uVar5;
  undefined8 uVar6;
  long lVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  long lVar10;
  undefined8 *puVar11;
  ushort uVar12;
  uint uVar13;
  undefined4 local_58;
  uint local_54;
  int local_50;
  undefined4 local_4c;
  undefined4 local_48;
  uint local_44;
  int local_40;
  int local_3c [3];
  
  uVar8 = Packet::g4_alt3(packet);
  lVar10 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar10 + 2;
  uVar12 = (ushort)*(byte *)(*(long *)(packet + 0x10) + 1 + lVar10) * 0x100 +
           (ushort)*(byte *)(*(long *)(packet + 0x10) + lVar10);
  uVar9 = Packet::g4_alt3(packet);
  uVar13 = (uint)uVar12;
  if (uVar12 == 0xffff) {
    uVar13 = 0xffffffff;
  }
  uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  lVar10 = InterfaceManager::CreateOrFindUpdateEntry(uVar6,9,uVar9);
  InterfaceManager::MarkUpdateEntryDirty(uVar6,lVar10);
  local_54 = uVar13;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x20,&local_54);
  local_58 = uVar8;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x40,&local_58);
  lVar10 = *thisPtr;
  puVar11 = &DAT_015c7b20;
  lVar7 = *(long *)((long)&__DT_RELA[0xca6].r_info + lVar10);
  if (*(int *)(lVar7 + 0x3c) == 4) {
    puVar11 = (undefined8 *)FUN_00ad81b0(lVar7,uVar13);
    lVar10 = *thisPtr;
  }
  lVar7 = puVar11[1];
  uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + lVar10);
  sVar2 = *(short *)(lVar7 + 0x2a8);
  sVar3 = *(short *)(lVar7 + 0x2aa);
  uVar12 = *(ushort *)(lVar7 + 0x2a6);
  sVar4 = *(short *)(lVar7 + 0x2ac);
  lVar10 = InterfaceManager::CreateOrFindUpdateEntry(uVar6,8,uVar9);
  InterfaceManager::MarkUpdateEntryDirty(uVar6,lVar10);
  local_3c[0] = (int)sVar2;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x20,local_3c);
  local_40 = (int)sVar3;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x40,&local_40);
  local_44 = (uint)uVar12;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x60,&local_44);
  uVar8 = *(undefined4 *)(lVar7 + 0x2b0);
  uVar5 = *(undefined4 *)(lVar7 + 0x2b4);
  uVar6 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  lVar10 = InterfaceManager::CreateOrFindUpdateEntry(uVar6,10,uVar9);
  InterfaceManager::MarkUpdateEntryDirty(uVar6,lVar10);
  local_48 = uVar8;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x20,&local_48);
  local_4c = uVar5;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x40,&local_4c);
  local_50 = (int)sVar4;
  InterfaceManager::SetUpdateSlotValue(lVar10 + 0x60,&local_50);
  lVar10 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  piVar1 = (int *)(lVar10 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar10 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETGRAPHIC` @ 00193fe0
```c

undefined * jag::packethandlers::Interfaces::IF_SETGRAPHIC(long *param_1,long param_2)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  byte bVar5;
  long lVar6;
  undefined8 uVar7;
  undefined4 uVar8;
  long lVar9;
  int local_2c [3];
  
  lVar9 = *(long *)(param_2 + 0x18);
  lVar6 = *(long *)(param_2 + 0x10);
  *(long *)(param_2 + 0x18) = lVar9 + 4;
  bVar2 = *(byte *)(lVar6 + 1 + lVar9);
  bVar3 = *(byte *)(lVar6 + lVar9);
  bVar4 = *(byte *)(lVar6 + 2 + lVar9);
  bVar5 = *(byte *)(lVar6 + 3 + lVar9);
  uVar8 = Packet::g4_alt3(param_2);
  uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  lVar9 = InterfaceManager::CreateOrFindUpdateEntry(uVar7,5,uVar8);
  InterfaceManager::MarkUpdateEntryDirty(uVar7,lVar9);
  local_2c[0] = (uint)bVar5 * 0x100 + (uint)bVar2 * 0x1000000 + (uint)bVar3 * 0x10000 + (uint)bVar4;
  InterfaceManager::SetUpdateSlotValue(lVar9 + 0x20,local_2c);
  lVar9 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *param_1);
  piVar1 = (int *)(lVar9 + 0x10);
  *piVar1 = *piVar1 + 1;
  *(undefined1 *)(lVar9 + 0x14) = 1;
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SET2DANGLE` @ 00194090
```c

/* Setting prototype: undefined * IF_SET2DANGLE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SET2DANGLE(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long lVar6;
  undefined4 uVar7;
  long lVar8;
  int local_1c;
  
  uVar7 = Packet::g4_alt3(packet);
  lVar8 = *(long *)(packet + 0x18);
  lVar5 = *(long *)(packet + 0x10);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(long *)(packet + 0x18) = lVar8 + 4;
  bVar1 = *(byte *)(lVar5 + 1 + lVar8);
  bVar2 = *(byte *)(lVar5 + lVar8);
  bVar3 = *(byte *)(lVar5 + 3 + lVar8);
  bVar4 = *(byte *)(lVar5 + 2 + lVar8);
  *(int *)(lVar6 + 0x10) = *(int *)(lVar6 + 0x10) + 1;
  *(undefined1 *)(lVar6 + 0x14) = 1;
  lVar8 = InterfaceManager::CreateOrFindUpdateEntry(lVar6,0xd,uVar7);
  InterfaceManager::MarkUpdateEntryDirty(lVar6,lVar8);
  local_1c = (uint)bVar3 * 0x100 + (uint)bVar1 * 0x1000000 + (uint)bVar2 * 0x10000 + (uint)bVar4;
  InterfaceManager::SetUpdateSlotValue(lVar8 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETHIDE` @ 00194140
```c

/* Setting prototype: undefined * IF_SETHIDE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETHIDE(long *thisPtr,long packet)

{
  char cVar1;
  long lVar2;
  undefined4 uVar3;
  long lVar4;
  uint local_1c;
  
                    /* jag::packethandlers::Interfaces::IF_SETHIDE — Sets hide flag on interface
                       component, kind=7, compare byte 0x81. 948-2 anchor: position 14 in
                       Interfaces::BindHandlers binder. Behavioral match vs 947-3 IF_SETHIDE @
                       0x0023e920. */
  lVar2 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar2 + 1;
  cVar1 = *(char *)(*(long *)(packet + 0x10) + lVar2);
  uVar3 = Packet::g4_alt2(packet);
  lVar2 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar2 + 0x10) = *(int *)(lVar2 + 0x10) + 1;
  *(undefined1 *)(lVar2 + 0x14) = 1;
  lVar4 = InterfaceManager::CreateOrFindUpdateEntry(lVar2,7,uVar3);
  InterfaceManager::MarkUpdateEntryDirty(lVar2,lVar4);
  local_1c = (uint)(cVar1 == -0x7f);
  InterfaceManager::SetUpdateSlotValue(lVar4 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_OPENTOP` @ 001941d0
```c

/* Setting prototype: undefined * IF_OPENTOP(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_OPENTOP(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  long lVar5;
  long lVar6;
  ushort uVar7;
  uint local_1c;
  
  lVar5 = *(long *)(packet + 0x18);
  lVar6 = *(long *)(packet + 0x10);
  *(long *)(packet + 0x18) = lVar5 + 4;
  bVar1 = *(byte *)(lVar6 + 3 + lVar5);
  bVar2 = *(byte *)(lVar6 + 2 + lVar5);
  bVar3 = *(byte *)(lVar6 + lVar5);
  bVar4 = *(byte *)(lVar6 + 1 + lVar5);
  *(long *)(packet + 0x18) = lVar5 + 6;
  uVar7 = *(ushort *)(lVar6 + 4 + lVar5);
  if (DAT_01050dc0 == 0x3020100) {
    uVar7 = uVar7 << 8 | uVar7 >> 8;
  }
  lVar5 = *(long *)((long)&__DT_RELA[0xcf7].r_info + *thisPtr);
  *(int *)(lVar5 + 0x10) = *(int *)(lVar5 + 0x10) + 1;
  *(undefined1 *)(lVar5 + 0x14) = 1;
  lVar6 = InterfaceManager::CreateOrFindUpdateEntry
                    (lVar5,0xc,
                     (uint)bVar4 * 0x100 + (uint)bVar1 * 0x1000000 + (uint)bVar2 * 0x10000 +
                     (uint)bVar3);
  InterfaceManager::MarkUpdateEntryDirty(lVar5,lVar6);
  local_1c = (uint)uVar7;
  InterfaceManager::SetUpdateSlotValue(lVar6 + 0x20,&local_1c);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_OPENSUB` @ 00194280
```c

/* Setting prototype: undefined * IF_OPENSUB(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_OPENSUB(long *thisPtr,long packet)

{
  byte bVar1;
  byte bVar2;
  byte bVar3;
  byte bVar4;
  uint uVar5;
  long lVar6;
  long lVar7;
  long lVar8;
  bool bVar9;
  int local_30;
  int local_2c [3];
  
  lVar6 = *(long *)(packet + 0x18);
  lVar8 = *(long *)(packet + 0x10);
  lVar7 = *thisPtr;
  *(long *)(packet + 0x18) = lVar6 + 2;
  bVar1 = *(byte *)(lVar8 + lVar6);
  bVar2 = *(byte *)(lVar8 + 1 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 4;
  bVar3 = *(byte *)(lVar8 + 3 + lVar6);
  bVar4 = *(byte *)(lVar8 + 2 + lVar6);
  *(long *)(packet + 0x18) = lVar6 + 8;
  uVar5 = *(uint *)(lVar8 + 4 + lVar6);
  lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar7);
  bVar9 = DAT_01050dc0 == 0x3020100;
  *(undefined1 *)(lVar6 + 0x14) = 1;
  if (bVar9) {
    uVar5 = uVar5 >> 0x18 | (uVar5 & 0xff0000) >> 8 | (uVar5 & 0xff00) << 8 | uVar5 << 0x18;
  }
  *(int *)(lVar6 + 0x10) = *(int *)(lVar6 + 0x10) + 1;
  lVar8 = InterfaceManager::CreateOrFindUpdateEntry(lVar6,0xb,uVar5);
  InterfaceManager::MarkUpdateEntryDirty(lVar6,lVar8);
  local_2c[0] = (int)(short)((ushort)bVar2 * 0x100 + (ushort)bVar1);
  InterfaceManager::SetUpdateSlotValue(lVar8 + 0x20,local_2c);
  local_30 = (int)(short)((ushort)bVar3 * 0x100 + (ushort)bVar4);
  InterfaceManager::SetUpdateSlotValue(lVar8 + 0x40,&local_30);
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SET_HTTP_IMAGE` @ 001babb0
```c

undefined * jag::packethandlers::Interfaces::IF_SET_HTTP_IMAGE(long *param_1,long param_2)

{
  byte *pbVar1;
  long lVar2;
  size_t sVar3;
  uint uVar4;
  uint uVar5;
  undefined8 extraout_RDX;
  byte bVar6;
  byte *__s;
  undefined8 uVar7;
  char ****ppppcVar8;
  long lVar9;
  ulong uVar10;
  size_t local_108;
  char ***local_100;
  undefined8 local_f8;
  undefined8 local_f0;
  char ***local_e8;
  long local_e0;
  undefined8 local_d8;
  char ***local_c8;
  undefined1 local_c0 [144];
  
  lVar9 = *(long *)(param_2 + 0x18);
  local_d8 = -0x7fffffffffffff81;
  local_e0 = 0;
  local_c0[0] = 0;
  local_e8 = (char ***)local_c0;
  __s = (byte *)(*(long *)(param_2 + 0x10) + lVar9);
  local_c8 = local_e8;
  sVar3 = strlen((char *)__s);
  ppppcVar8 = (char ****)local_e8;
  if (sVar3 == 0) {
    lVar2 = *param_1;
    *(long *)(param_2 + 0x18) = lVar9 + 1;
    uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcfe].r_addend + lVar2);
  }
  else {
    pbVar1 = __s + (sVar3 & 0xffffffff);
    if ((sVar3 & 0xffffffff) == 0) {
      *(size_t *)(param_2 + 0x18) = lVar9 + 1 + sVar3;
      uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
    }
    else {
      if (((int)pbVar1 - (int)__s & 1U) != 0) {
        uVar10 = (ulong)*__s;
        if ((byte)(*__s + 0x80) < 0x20) {
          uVar10 = (ulong)*(ushort *)(&DAT_0102a600 + uVar10 * 2);
        }
        uVar5 = (uint)uVar10;
        if (uVar5 != 0) {
          if (uVar5 < 0x80) {
            FUN_00c29370(&local_e8,(int)(char)uVar10);
          }
          else {
            if (uVar5 < 0x800) {
              lVar9 = 0x17 - (long)local_d8._7_1_;
              if (local_d8 < 0) {
                lVar9 = local_e0;
              }
              bVar6 = (byte)(uVar5 >> 6) | 0xc0;
              FUN_006b28b0(&local_e8,lVar9 + 2);
            }
            else {
              lVar9 = 0x17 - (long)local_d8._7_1_;
              if (local_d8 < 0) {
                lVar9 = local_e0;
              }
              FUN_006b28b0(&local_e8,lVar9 + 3);
              bVar6 = (byte)(uVar5 >> 6) & 0x3f | 0x80;
              FUN_00c29370(&local_e8,(int)(char)((byte)(uVar10 >> 0xc) | 0xe0));
            }
            FUN_00c29370(&local_e8,(int)(char)bVar6);
            uVar5 = uVar5 & 0x3f | 0xffffff80;
            FUN_00c29370(&local_e8,(int)(char)uVar5,uVar5);
          }
        }
        __s = __s + 1;
        goto joined_r0x001baf6d;
      }
      do {
        while( true ) {
          uVar10 = (ulong)*__s;
          if ((byte)(*__s + 0x80) < 0x20) {
            uVar10 = (ulong)*(ushort *)(&DAT_0102a600 + uVar10 * 2);
          }
          uVar5 = (uint)uVar10;
          if (uVar5 != 0) {
            bVar6 = (byte)uVar10;
            if (uVar5 < 0x80) {
              FUN_00c29370(&local_e8,(int)(char)bVar6);
            }
            else if (uVar5 < 0x800) {
              lVar9 = local_e0;
              if (-1 < local_d8) {
                lVar9 = 0x17 - (long)local_d8._7_1_;
              }
              FUN_006b28b0(&local_e8,lVar9 + 2);
              FUN_00c29370(&local_e8,(int)(char)((byte)(uVar5 >> 6) | 0xc0));
              FUN_00c29370(&local_e8,(int)(char)(bVar6 & 0x3f | 0x80));
            }
            else {
              lVar9 = local_e0;
              if (-1 < local_d8) {
                lVar9 = 0x17 - (long)local_d8._7_1_;
              }
              FUN_006b28b0(&local_e8,lVar9 + 3);
              uVar4 = (uint)(uVar10 >> 0xc) | 0xffffffe0;
              FUN_00c29370(&local_e8,(int)(char)uVar4,extraout_RDX,uVar4);
              FUN_00c29370(&local_e8,(int)(char)((byte)(uVar5 >> 6) & 0x3f | 0x80));
              FUN_00c29370(&local_e8,(int)(char)(bVar6 & 0x3f | 0x80));
            }
          }
          uVar10 = (ulong)__s[1];
          if ((byte)(__s[1] + 0x80) < 0x20) {
            uVar10 = (ulong)*(ushort *)(&DAT_0102a600 + uVar10 * 2);
          }
          uVar5 = (uint)uVar10;
          if (uVar5 != 0) break;
LAB_001bae35:
          __s = __s + 2;
          if (__s == pbVar1) goto LAB_001bae42;
        }
        if (0x7f < uVar5) {
          if (uVar5 < 0x800) {
            lVar9 = 0x17 - (long)local_d8._7_1_;
            if (local_d8 < 0) {
              lVar9 = local_e0;
            }
            bVar6 = (byte)(uVar5 >> 6) | 0xc0;
            FUN_006b28b0(&local_e8,lVar9 + 2);
          }
          else {
            lVar9 = 0x17 - (long)local_d8._7_1_;
            if (local_d8 < 0) {
              lVar9 = local_e0;
            }
            FUN_006b28b0(&local_e8,lVar9 + 3);
            bVar6 = (byte)(uVar5 >> 6) & 0x3f | 0x80;
            FUN_00c29370(&local_e8,(int)(char)((byte)(uVar10 >> 0xc) | 0xe0));
          }
          FUN_00c29370(&local_e8,(int)(char)bVar6);
          FUN_00c29370(&local_e8,(int)(char)((byte)uVar10 & 0x3f | 0x80));
          goto LAB_001bae35;
        }
        FUN_00c29370(&local_e8,(int)(char)(byte)uVar10);
        __s = __s + 2;
joined_r0x001baf6d:
      } while (__s != pbVar1);
LAB_001bae42:
      *(long *)(param_2 + 0x18) = *(long *)(param_2 + 0x18) + sVar3 + 1;
      uVar7 = *(undefined8 *)((long)&__DT_RELA[0xcfe].r_addend + *param_1);
      ppppcVar8 = &local_e8;
      if ((local_d8 < 0) &&
         (local_108 = 0, local_100 = local_e8, ppppcVar8 = (char ****)local_e8,
         (char ****)local_e8 == (char ****)0x0)) goto LAB_001bac33;
    }
  }
  local_108 = strlen((char *)ppppcVar8);
  local_100 = (char ***)ppppcVar8;
LAB_001bac33:
  local_f8 = 0;
  local_f0 = 0;
  jag__console__Console__ExecuteCommand_nxt216sa(uVar7,&local_108,0,&local_f8,0xffffffff,0);
  if (((local_d8 < 0) && ((char ****)local_e8 != (char ****)0x0)) && (local_e8 != local_c8)) {
    HeapInterface::Free();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_OPENSUB_thunk` @ 001d9fb0
```c

undefined * jag::packethandlers::Interfaces::IF_OPENSUB_thunk(long *param_1,long param_2)

{
  long lVar1;
  long lVar2;
  undefined8 uVar3;
  undefined8 *puVar4;
  short sVar5;
  ushort uVar6;
  size_t sVar7;
  long *plVar8;
  undefined8 uVar9;
  undefined8 *puVar10;
  long lVar11;
  undefined8 *puVar12;
  undefined1 *puVar13;
  undefined8 ****ppppuVar14;
  char *pcVar15;
  undefined8 *unaff_RBP;
  byte *__s;
  long *unaff_R12;
  undefined8 uStack_a0;
  long *local_88;
  long *local_80;
  undefined1 local_78;
  undefined7 uStack_77;
  char local_61;
  undefined8 ***local_58;
  long local_50;
  char local_41 [17];
  
  lVar1 = *(long *)(param_2 + 0x18);
  lVar2 = *(long *)(param_2 + 0x10);
  local_78 = 0;
  local_61 = '\x17';
  lVar11 = lVar1 + 2;
  *(long *)(param_2 + 0x18) = lVar11;
  __s = (byte *)(lVar2 + lVar11);
  if (*(char *)(lVar2 + 1 + lVar1) == '\0') {
    uStack_a0 = 0x1d9ff7;
    sVar7 = strlen((char *)__s);
    if (sVar7 == 0) {
      lVar11 = lVar1 + 3;
      __s = (byte *)(lVar2 + lVar11);
      *(long *)(param_2 + 0x18) = lVar11;
    }
    else {
      uStack_a0 = 0x1da354;
      FUN_001398d0(__s,sVar7 & 0xffffffff,&local_78);
      lVar11 = sVar7 + 1 + *(long *)(param_2 + 0x18);
      *(long *)(param_2 + 0x18) = lVar11;
      __s = (byte *)(*(long *)(param_2 + 0x10) + lVar11);
    }
  }
  uVar6 = (ushort)*__s;
  uVar9 = *(undefined8 *)((long)&__DT_RELA[0xcf7].r_offset + *param_1);
  uVar3 = *(undefined8 *)((long)&__DT_RELA[0xca6].r_info + *param_1);
  if ((char)*__s < '\0') {
    uStack_a0 = 0x1da2f8;
    sVar5 = FUN_00121a30(param_2);
    uVar6 = sVar5 + 0x8000;
  }
  else {
    *(long *)(param_2 + 0x18) = lVar11 + 1;
  }
  if (DAT_013960b0 != (undefined8 *)0x0) {
    puVar4 = DAT_013960b0;
    puVar12 = &DAT_013960a0;
    do {
      while( true ) {
        puVar10 = puVar4;
        puVar4 = (undefined8 *)*puVar10;
        if (uVar6 <= *(ushort *)(puVar10 + 4)) break;
        puVar10 = puVar12;
        if (puVar4 == (undefined8 *)0x0) goto LAB_001da090;
      }
      puVar4 = (undefined8 *)puVar10[1];
      puVar12 = puVar10;
    } while ((undefined8 *)puVar10[1] != (undefined8 *)0x0);
LAB_001da090:
    if ((puVar10 != &DAT_013960a0) && (*(ushort *)(puVar10 + 4) <= uVar6)) {
      uStack_a0 = 0x1da0be;
      (**(code **)(puVar10[6] + 0x20))(&local_88,uVar3,uVar9,0,param_2);
      plVar8 = local_88;
      if (local_88 != (long *)0x0) {
        uStack_a0 = 0x1da0dc;
        unaff_RBP = (undefined8 *)operator_new(0x18);
        unaff_RBP[1] = 0x100000001;
        unaff_RBP[2] = local_88;
        local_88 = (long *)0x0;
        *unaff_RBP = &PTR_FUN_01365a40;
        unaff_R12 = plVar8;
      }
      goto LAB_001da0fd;
    }
  }
  local_88 = (long *)0x0;
LAB_001da0fd:
  lVar11 = *param_1;
  if (DAT_015a5388 == 0) {
    uStack_a0 = 0x1da38c;
    FUN_0047e830(&DAT_013965a0);
    if (DAT_015a5388 == 0) {
      uVar9 = operator_new(0xba8);
      uStack_a0 = 0x1da3e0;
      FUN_003854b0(uVar9);
    }
    if (PTR___pthread_key_create_01390dc0 != (undefined *)0x0) {
      uStack_a0 = 0x1da3ac;
      pthread_mutex_unlock((pthread_mutex_t *)&DAT_013965a0);
    }
  }
  lVar1 = DAT_015a5388;
  puVar13 = &local_78;
  if (local_61 < '\0') {
    puVar13 = (undefined1 *)CONCAT71(uStack_77,local_78);
  }
  FUN_00224570(&local_58,"DBFilter Debug: %s",puVar13);
  plVar8 = (long *)FUN_00c29480(0xc0);
  *plVar8 = (long)&DAT_0138a880;
  plVar8[5] = -0x7fffffffffffffd1;
  *(undefined1 *)(plVar8 + 1) = 0;
  *(undefined1 *)((long)plVar8 + 9) = 0;
  *(undefined2 *)((long)plVar8 + 10) = 0;
  plVar8[2] = 0;
  plVar8[7] = (long)(plVar8 + 8);
  plVar8[3] = (long)(plVar8 + 8);
  plVar8[4] = 0;
  *(undefined1 *)(plVar8 + 8) = 0;
  FUN_00aa8010(plVar8 + 3,&DAT_00fba3bd);
  if ((char)plVar8[1] == '\0') {
    uStack_a0 = 0x1da1b6;
    FUN_003895b0(plVar8);
  }
  *plVar8 = (long)&DAT_0138a748;
  ppppuVar14 = &local_58;
  if (local_41[0] < '\0') {
    ppppuVar14 = (undefined8 ****)local_58;
  }
  FUN_00224570(plVar8 + 0xe,&DAT_00cb7232,ppppuVar14,plVar8);
  *(undefined1 *)(plVar8 + 0x11) = 1;
  plVar8[0x12] = lVar11;
  *(undefined1 *)(plVar8 + 0x13) = 0;
  *(undefined1 *)((long)plVar8 + 0xaf) = 0x17;
  *plVar8 = (long)&PTR_FUN_01365a70;
  if (local_41[0] < '\0') {
    pcVar15 = (char *)(local_50 + (long)local_58);
    ppppuVar14 = (undefined8 ****)local_58;
  }
  else {
    pcVar15 = local_41 + -(long)local_41[0];
    ppppuVar14 = &local_58;
  }
  FUN_0048d1a0(plVar8 + 0x13,ppppuVar14,pcVar15,uStack_a0);
  plVar8[0x16] = (long)unaff_RBP;
  plVar8[0x17] = (long)unaff_R12;
  LOCK();
  *(int *)(unaff_RBP + 1) = *(int *)(unaff_RBP + 1) + 1;
  UNLOCK();
  local_80 = plVar8;
  FUN_003719f0(lVar1,&local_80);
  if (local_80 != (long *)0x0) {
    (**(code **)(*local_80 + 8))();
  }
  if ((local_41[0] < '\0') && ((undefined8 ****)local_58 != (undefined8 ****)0x0)) {
    eastl__basic_string();
  }
  ref_counter_base::DecRef(unaff_RBP);
  if (local_88 != (long *)0x0) {
    (**(code **)(*local_88 + 8))();
  }
  if ((local_61 < '\0') && (CONCAT71(uStack_77,local_78) != 0)) {
    eastl__basic_string();
  }
  return &DAT_015d3620;
}


```

## `jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SNAPSHOT` @ 001da3f0
```c

/* Setting prototype: undefined * IF_SETPLAYERMODEL_SNAPSHOT(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SNAPSHOT(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  byte bVar3;
  char cVar4;
  byte bVar5;
  long lVar6;
  undefined4 uVar7;
  undefined4 uVar8;
  undefined *puVar9;
  long lVar10;
  undefined8 *puVar11;
  uint uVar12;
  undefined8 *puVar13;
  ushort local_60;
  undefined8 local_48;
  undefined8 *puStack_40;
  
                    /* jag::packethandlers::Interfaces::IF_SETPLAYERMODEL_SNAPSHOT — Reads player
                       snapshot packet (component id + matrix data). Position 8 in
                       Interfaces::BindHandlers binder. */
  Packet::g4_alt3(packet);
  uVar7 = Packet::g4_alt3(packet);
  lVar10 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar10 + 2;
  bVar2 = *(byte *)(*(long *)(packet + 0x10) + lVar10);
  _local_60 = CONCAT71(stack0xffffffffffffffa1,
                       *(undefined1 *)(*(long *)(packet + 0x10) + 1 + lVar10));
  uVar8 = Packet::g4_alt2(packet);
  game::BuildArea::DecodePackedCoord(&local_48,uVar8);
  Packet::g4_alt1((PacketCore *)packet);
  lVar10 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar10 + 2;
  bVar3 = *(byte *)(*(long *)(packet + 0x10) + lVar10);
  cVar4 = *(char *)(*(long *)(packet + 0x10) + 1 + lVar10);
  Packet::g4_alt3(packet);
  lVar10 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar10 + 1;
  bVar5 = *(byte *)(*(long *)(packet + 0x10) + lVar10);
  Packet::g4_alt2(packet);
  puVar9 = &DAT_015d3600;
  lVar10 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  if (*(long *)(lVar10 + 0xe8) != 0) {
    uVar12 = (uint)bVar3 * 0x100 + (uint)(byte)(cVar4 + 0x80) & 0xffff;
    lVar10 = game::InterfaceList::GetInterface(lVar10 + 0x30,uVar12,0);
    puVar9 = &DAT_015d35c0;
    if (*(long *)(lVar10 + 8) != 0) {
      lVar10 = *thisPtr;
      lVar6 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar10);
      piVar1 = (int *)(lVar6 + 0x10);
      *piVar1 = *piVar1 + 1;
      *(undefined1 *)(lVar6 + 0x14) = 1;
      puVar11 = (undefined8 *)FUN_00c29430(0x60);
      puVar13 = (undefined8 *)0x0;
      if (puVar11 != (undefined8 *)0x0) {
        puVar11[8] = lVar10;
        puVar13 = puVar11 + 4;
        *(undefined4 *)(puVar11 + 6) = uVar7;
        *(uint *)((long)puVar11 + 0x2c) = uVar12;
        *(undefined4 *)(puVar11 + 5) = 1;
        *(uint *)((long)puVar11 + 0x34) = -(uint)bVar5 & 0xff;
        *(undefined1 *)(puVar11 + 7) = 0;
        puVar11[4] = &PTR_FUN_01363c30;
        *(uint *)(puVar11 + 9) = (uint)local_60 * 0x100 + (uint)bVar2 & 0xffff;
        *(undefined4 *)((long)puVar11 + 0x4c) = (undefined4)local_48;
        *(undefined4 *)((long)puVar11 + 0x54) = 0;
        *puVar11 = &PTR_FUN_01363c58;
        puVar11[1] = 0x100000001;
        puVar11[2] = 1;
        puVar11[3] = puVar13;
        *(float *)(puVar11 + 10) = (float)(uint)(local_48._4_4_ << 9);
        *(float *)(puVar11 + 0xb) = (float)(uint)((int)puStack_40 << 9);
      }
      local_48 = puVar11;
      puStack_40 = puVar13;
      FUN_002bc520(*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr),&local_48,0);
      puVar9 = &DAT_015d3620;
      if (local_48 != (undefined8 *)0x0) {
        ref_counter_base::DecRef();
        puVar9 = &DAT_015d3620;
      }
    }
  }
  return puVar9;
}


```

## `jag::packethandlers::Interfaces::IF_SETANGLE` @ 001da640
```c

/* Setting prototype: undefined * IF_SETANGLE(long * thisPtr, long packet) */

undefined * jag::packethandlers::Interfaces::IF_SETANGLE(long *thisPtr,long packet)

{
  int *piVar1;
  byte bVar2;
  char cVar3;
  long lVar4;
  undefined1 uVar5;
  uint uVar6;
  undefined1 uVar7;
  undefined4 uVar8;
  undefined4 uVar9;
  undefined4 uVar10;
  undefined4 uVar11;
  undefined4 uVar12;
  undefined4 uVar13;
  undefined4 uVar14;
  undefined4 uVar15;
  undefined4 uVar16;
  undefined4 uVar17;
  undefined4 uVar18;
  undefined4 uVar19;
  undefined4 uVar20;
  undefined4 uVar21;
  undefined4 uVar22;
  undefined4 uVar23;
  undefined *puVar24;
  long lVar25;
  ushort uVar26;
  bool bVar27;
  undefined8 *local_88;
  undefined8 *puStack_80;
  undefined4 local_78;
  undefined8 local_74;
  undefined8 local_6c;
  undefined8 local_64;
  undefined8 local_5c;
  undefined8 local_54;
  undefined8 local_4c;
  
                    /* jag::packethandlers::Interfaces::IF_SETANGLE — Reads packed-coord via
                       BuildArea::DecodePackedCoord, multiple ints, calls FUN_00af9ae0 (cs2 hook
                       init), then allocates a 0xe0-byte dispatch entry with full identity
                       matrix/quaternion init (float constants 0x3f80000000000000, 0x3f800000) and
                       dispatches via FUN_002bc330. Position 7 in Interfaces::BindHandlers binder.
                        */
  uVar21 = Packet::g4_alt2(packet);
  game::BuildArea::DecodePackedCoord(&local_78,uVar21);
  uVar6 = local_78;
  lVar25 = *(long *)(packet + 0x18);
  uVar9 = local_74._4_4_;
  uVar21 = (undefined4)local_74;
  *(long *)(packet + 0x18) = lVar25 + 1;
  bVar2 = *(byte *)(*(long *)(packet + 0x10) + lVar25);
  Packet::g4_alt2(packet);
  Packet::g4_alt2(packet);
  lVar25 = *(long *)(packet + 0x18);
  bVar27 = DAT_01050dc0 == 0x3020100;
  *(long *)(packet + 0x18) = lVar25 + 2;
  uVar26 = *(ushort *)(*(long *)(packet + 0x10) + lVar25);
  if (bVar27) {
    uVar26 = uVar26 << 8 | uVar26 >> 8;
  }
  uVar22 = Packet::g4_alt3(packet);
  lVar25 = *(long *)(packet + 0x18);
  *(long *)(packet + 0x18) = lVar25 + 1;
  cVar3 = *(char *)(*(long *)(packet + 0x10) + lVar25);
  Packet::gT_unsigned_int((PacketCore *)packet);
  uVar23 = Packet::g4_alt2(packet);
  Packet::g4_alt1((PacketCore *)packet);
  local_6c = 0x3f80000000000000;
  local_54 = 0x3f8000003f800000;
  local_78 = local_78 & 0xffff0000;
  local_74 = 0;
  local_64 = 0;
  local_5c = 0;
  local_4c = 0x3f800000;
  FUN_00af9220(&local_78,cVar3 + -0x80,packet);
  puVar24 = &DAT_015d3600;
  lVar25 = *(long *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr);
  if (*(long *)(lVar25 + 0xe8) != 0) {
    lVar25 = game::InterfaceList::GetInterface(lVar25 + 0x30,(uint)uVar26,0);
    puVar24 = &DAT_015d35c0;
    if (*(long *)(lVar25 + 8) != 0) {
      lVar25 = *thisPtr;
      uVar8 = (undefined4)local_74;
      uVar10 = local_74._4_4_;
      uVar11 = (undefined4)local_6c;
      uVar5 = (undefined1)local_78;
      lVar4 = *(long *)((long)&__DT_RELA[0xcf7].r_info + lVar25);
      uVar12 = local_6c._4_4_;
      uVar13 = (undefined4)local_64;
      uVar14 = local_64._4_4_;
      uVar7 = local_78._1_1_;
      piVar1 = (int *)(lVar4 + 0x10);
      *piVar1 = *piVar1 + 1;
      uVar15 = (undefined4)local_5c;
      uVar16 = local_5c._4_4_;
      *(undefined1 *)(lVar4 + 0x14) = 1;
      uVar17 = (undefined4)local_54;
      uVar18 = local_54._4_4_;
      uVar19 = (undefined4)local_4c;
      uVar20 = local_4c._4_4_;
      local_88 = (undefined8 *)FUN_00c29430(0xe0);
      puStack_80 = (undefined8 *)0x0;
      if (local_88 != (undefined8 *)0x0) {
        *(uint *)((long)local_88 + 0x2c) = (uint)uVar26;
        *(uint *)(local_88 + 10) = uVar6;
        *(undefined4 *)((long)local_88 + 0x54) = uVar21;
        *(undefined4 *)(local_88 + 5) = 1;
        *(undefined4 *)(local_88 + 6) = uVar22;
        *(uint *)((long)local_88 + 0x34) = -(uint)bVar2 & 0xff;
        *(undefined1 *)(local_88 + 7) = 0;
        local_88[4] = &PTR_FUN_01363ec8;
        local_88[8] = lVar25;
        local_88[9] = &PTR_FUN_01384b48;
        *(undefined4 *)(local_88 + 0xb) = uVar9;
        *(undefined1 *)((long)local_88 + 0x5c) = uVar5;
        *(undefined1 *)((long)local_88 + 0x5d) = uVar7;
        puStack_80 = local_88 + 4;
        *(undefined4 *)(local_88 + 0xc) = uVar8;
        *(undefined4 *)((long)local_88 + 100) = uVar10;
        *(undefined4 *)(local_88 + 0xd) = uVar11;
        *(undefined4 *)((long)local_88 + 0x6c) = uVar12;
        *(undefined4 *)(local_88 + 0xe) = uVar13;
        *(undefined4 *)((long)local_88 + 0x74) = uVar14;
        *(undefined4 *)(local_88 + 0xf) = uVar15;
        *(undefined4 *)((long)local_88 + 0x7c) = uVar16;
        *(undefined4 *)(local_88 + 0x10) = uVar17;
        *(undefined4 *)((long)local_88 + 0x84) = uVar18;
        *(undefined4 *)(local_88 + 0x11) = uVar19;
        *(undefined4 *)((long)local_88 + 0x8c) = uVar20;
        *(undefined1 *)(local_88 + 0x12) = 0;
        *(uint *)(local_88 + 0x1a) = uVar6;
        *(undefined4 *)((long)local_88 + 0xd4) = uVar21;
        *(undefined1 (*) [16])(local_88 + 0x13) = (undefined1  [16])0x0;
        *(undefined4 *)((long)local_88 + 0x94) = uVar23;
        *(undefined4 *)(local_88 + 0x15) = 0xffffffff;
        *(undefined4 *)((long)local_88 + 0xac) = 0;
        *(undefined4 *)(local_88 + 0x16) = 2;
        *(undefined8 *)((long)local_88 + 0xb4) = 0;
        local_88[0x18] = 0;
        *(undefined1 *)(local_88 + 0x19) = 1;
        *(undefined4 *)(local_88 + 0x1b) = uVar9;
        *local_88 = &PTR_FUN_01363c00;
        local_88[1] = 0x100000001;
        local_88[2] = 1;
        local_88[3] = puStack_80;
      }
      FUN_002bc520(*(undefined8 *)((long)&__DT_RELA[0xcf9].r_addend + *thisPtr),&local_88,0);
      if (local_88 != (undefined8 *)0x0) {
        ref_counter_base::DecRef();
      }
      puVar24 = &DAT_015d3620;
    }
  }
  return puVar24;
}


```
