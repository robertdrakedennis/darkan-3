# Production world-entry session — full summary (binary-derived, no docs)

Source: `build/undercut-socket-session-production-isaac.jsonl` (ISAAC-deciphered).
2 connections: **lobby** + **world** (both :443). 13 login_events. Everything below is decoded from the
capture + the 948-5 client wire formats read out of Ghidra (headless), cross-validated against the bytes.

## The story — world-entry packet order
1. **op81 RebuildNormalSimple** — world scene/map build (region + coords).
2. Minimap: op54 HashedWorldToken, op73 MinimapState, op74 JcoinsUpdate, op172/204 MinimapFlag.
3. **op17 SET_PLAYER_OP x6** — player right-click menu: **Follow, Trade with, Req Assist, Duel, Examine**.
4. op95 MidiSong (music), op5 ResetClientVarcache.
5. **Var flood** — the account/game state: VarpLarge x491, VarpSmall x1176, VarpLong x5,
   ClientSetVarcSmall x61, ClientSetVarcStr x35, ClientSetVarcLarge x2.
6. **HUD build** — op3 IF_SETTOPLEVELINTERFACE -> **interface 1477** (197 components);
   op35 IF_SETEVENTS x871 (**42 interfaces** wired); op82 IF_SETPOSITION x56; op122 IfSetText x7;
   op91 IfSetHide x11; op110 RunClientScript x86 (**28 CS2 scripts**: 8862 x22 = tabs, 10623 x27 = components).
7. **Entity sync** — op22 PlayerInfo x54 (GPI; **7 players** in viewport), op104 viewport-init x8,
   op52 NpcInfo x53, op1 SetNpcOp / op209 NpcInfoThunk.
8. **Inventory** — op85 UPDATE_INV_FULL x8: backpack(93)=item316, worn(94)=item1206 slot3, 795=item52556 x1000.
9. **Skills** — op44 UpdateStat x29: fresh level-1 character, Constitution lvl10/1154xp (RS default).
10. **Zone/world** — op78 UpdateZoneFullFollows x616, op76 PartialEnclosed x64, op16 LocDel x24,
    op46 ObjAdd x21, op90 LocAdd, op154 EntityAnimAtTile, op157 SceneFlag.
11. Misc — op93 GameMessage, op162 TRIGGER_ONDIALOGABORT x54, op174 AntiCheatChallenge x5,
    op130 ignore list, op67 ClanChannelFull, op26 FriendStatus.

## The player (everything found)
- **Brand-new level-1 character**: all skills lvl1/0xp except **Constitution lvl10 (1154xp)**.
- **Backpack**: item 316. **Worn**: item 1206 (slot 3). Stack container 795: item 52556 x1000.
- **Menu**: Follow / Trade with / Req Assist / Duel / Examine.
- **HUD**: interface 1477 (197 interactive components) + 41 sub-interfaces.
- **Viewport players** (GPI byteAdd names): AlQaeda123, Bagrunga, ZestyKso, beerlovesme, iGoldenMerK,
  osrsbt2, slimypea. Local = one of these (equipped item 1206 not present as item-id in any appearance
  block -> appearance encodes worn items as model ids; pinning self needs the GPI bit-section parse).

## Wire formats decoded (validated)
- **ServerProt framing**: jag::ServerProt::RegisterAll @0xc4700 -> 218 opcodes (op->size). 37/37 fixed sizes match capture.
- **UPDATE_INV_FULL**: [u16 container][u8 flag][u16 size] then size x { u16 item(0=empty) ; g1 count(0xFF->g4) ; +u8 if flag&2 }.
- **IF_SETEVENTS**: [u16 fromSlot][u16 toSlot][u32 eventMask][u16 interface][u16 component].
- **RunClientScript**: [type-desc str null-term ('i'=int,'s'=string)][args][scriptId g4 LAST].
- **UpdateStat**: [g4 xp LE][g1 level][g1 skill = (-wireByte)&0xFF].
- **IF_SETTOPLEVELINTERFACE**: interfaceId = (b[10]<<8) | ((b[9]-0x80)&0xFF) = 1477.
- **GPI appearance**: byteAdd per byte (name char = wireByte-0x80); body parts = model ids.

## Detailed findings index
01 opcodes-all · 02 world-s2c-sequence · 10 prot-registration · 11 handlers-all (192) ·
12 opcode-map · 13 dispatch · 14 serverprot-table (definitive sizes) · 20 interfaces · 21 player ·
22 inventory · 23 world-state · 24 other · 29 session-payloads · 30 session-decoded · 31 player-complete

## COMPLETENESS PROOF — byte accounting (iter29)
Decoded packets + framing (opcode + len bytes per size-type) + login handshake = 100% of TCP bytes:
- lobby S2C: 18206B pkts + 133B (login resp+111B player data) = 18339B TCP. EXACT.
- lobby C2S: 170B pkts + 627B (RSA login block 644B) = 797B TCP. EXACT.
- world S2C: 45903B pkts + 1447B (1321B server-client-vars + 37B world-login + hs) = 47350B TCP. EXACT.
- world C2S: 2150B pkts + 649B (RSA login block 664B) = 2799B TCP. EXACT.
=> Every wire byte accounted for; overhead matches login_events. The packet inventory is PROVABLY COMPLETE (no missing packets).
