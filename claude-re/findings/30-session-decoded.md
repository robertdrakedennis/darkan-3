# The production session, decoded (human story) — built from capture payloads + binary wire formats

## UI created this session

- **op3 IF_SETTOPLEVELINTERFACE** (1x, handler @0x186a80): payload `00000000000000000045050000000000000000` -> byte[9]=0x45 byte[10]=0x05; id = (byte10<<8)|((byte9-0x80)&0xFF) = **1477** = the in-game HUD root interface.
  -> the world session opens top-level interface 1477 (established-account in-game HUD).

## TODO (iter6): decode op82 IF_SETPOSITION (component layout), op35 IF_SETEVENTS (component event masks),
op122 IF_SETTEXT (text), op62 IF_CLOSESUB; op17/12/13 SetPlayerOp (right-click player ops); op44 UpdateStat (skills);
the inventory UPDATE_INV (container+items); op77 CAMERA_UPDATE (121x). Payloads ready in 29-session-payloads.md.


# === iter7: session payload decode ===

## Player right-click options — op17 SET_PLAYER_OP @0x13f040

- bytes[0..1]=255,255 -> option text `\xfdFollow`
- bytes[0..1]=255,255 -> option text `\xfcTrade with`
- bytes[0..1]=255,255 -> option text `\xfaReq Assist`
- bytes[0..1]=255,255 -> option text `\xf9null`
- bytes[0..1]=255,255 -> option text `\xf8Examine`
- bytes[0..1]=255,255 -> option text `\xfbDuel`

## HUD text — op122 IF_SETTEXT

- if 16740.30309: `nture`
- if 19567.24932: `ing notes<br>Please wait...`
- if 187.7: ``
- if 136.1286: ``
- if 16757.29813: `mn Voyage`
- if 16757.29813: `mn Voyage`
- if 18790.8313: `ou join a clan<br>your clan's chat channel<br>list will appear here.<br><br>You can join another clan's<br>channel as a guest through the<br>visited clan channel tab in<br>the bottom right corner.`

## Skills — op44 UpdateStat (raw + [skill,level,xp] guess)

- skill=0 level=0 xp=256 (raw `000000000100`)
- skill=0 level=0 xp=511 (raw `0000000001ff`)
- skill=0 level=0 xp=510 (raw `0000000001fe`)
- skill=130 level=4 xp=2813 (raw `820400000afd`)
- skill=0 level=0 xp=508 (raw `0000000001fc`)
- skill=0 level=0 xp=507 (raw `0000000001fb`)
- skill=0 level=0 xp=506 (raw `0000000001fa`)
- skill=0 level=0 xp=505 (raw `0000000001f9`)
- skill=0 level=0 xp=504 (raw `0000000001f8`)
- skill=0 level=0 xp=503 (raw `0000000001f7`)
- skill=0 level=0 xp=502 (raw `0000000001f6`)
- skill=0 level=0 xp=501 (raw `0000000001f5`)
- skill=0 level=0 xp=500 (raw `0000000001f4`)
- skill=0 level=0 xp=499 (raw `0000000001f3`)
- skill=0 level=0 xp=498 (raw `0000000001f2`)
- skill=0 level=0 xp=497 (raw `0000000001f1`)
- skill=0 level=0 xp=496 (raw `0000000001f0`)
- skill=0 level=0 xp=495 (raw `0000000001ef`)
- skill=0 level=0 xp=494 (raw `0000000001ee`)
- skill=0 level=0 xp=493 (raw `0000000001ed`)
- skill=0 level=0 xp=492 (raw `0000000001ec`)
- skill=0 level=0 xp=491 (raw `0000000001eb`)
- skill=0 level=0 xp=490 (raw `0000000001ea`)
- skill=0 level=0 xp=489 (raw `0000000001e9`)
- skill=0 level=0 xp=488 (raw `0000000001e8`)
- skill=0 level=0 xp=487 (raw `0000000001e7`)
- skill=0 level=0 xp=486 (raw `0000000001e6`)
- skill=0 level=0 xp=485 (raw `0000000001e5`)
- skill=0 level=0 xp=484 (raw `0000000001e4`)

## Inventory — op85 UPDATE_INV_FULL (8 containers) @0x1a8d30

Wire (from decomp): u16-BE containerId, u8 flag, u16-BE size, then per-slot [u16-BE itemId][u8 count].

- container **787**  (flag=0, size=0): empty
- container **795**  (flag=0, size=1): item52556x255
- container **891**  (flag=0, size=0): empty
- container **93** INVENTORY (backpack) (flag=2, size=1): item316x1
- container **94** WORN EQUIPMENT (flag=2, size=4): item1206x1
- container **623**  (flag=0, size=0): empty
- container **895**  (flag=0, size=10): item961x0, item8779x0, item8781x0, item8783x0, item54861x0, item54863x0, item54865x0, item54867x0, item54869x0, item54871x0
- container **670**  (flag=0, size=4): item1206x1


## Inventory — CORRECTED decode (op85 UPDATE_INV_FULL) — from UPDATE_INV_PARTIAL loop @0x184320

Header: u16-BE container, u8 flag, u16-BE size. Per slot: u16-BE item(0=empty; actual id likely item-1, RS id+1 convention), g1 count(0xFF->g4), +u8 if flag&0x02.

- **787**  flag=0x00 size=0 [5/5B]: empty
- **795**  flag=0x00 size=1 [12/12B]: slot0: item 52556(id~52555) x1000
- **891**  flag=0x00 size=0 [5/5B]: empty
- **93** INVENTORY (backpack) flag=0x02 size=1 [9/9B]: slot0: item 316(id~315) x1
- **94** WORN EQUIPMENT flag=0x02 size=4 [21/21B]: slot3: item 1206(id~1205) x1
- **623**  flag=0x00 size=0 [5/5B]: empty
- **895**  flag=0x00 size=10 [35/35B]: slot0: item 961(id~960) x0, slot1: item 8779(id~8778) x0, slot2: item 8781(id~8780) x0, slot3: item 8783(id~8782) x0, slot4: item 54861(id~54860) x0, slot5: item 54863(id~54862) x0, slot6: item 54865(id~54864) x0, slot7: item 54867(id~54866) x0, slot8: item 54869(id~54868) x0, slot9: item 54871(id~54870) x0
- **670** equipment (loadout/mirror) flag=0x00 size=4 [17/17B]: slot3: item 1206(id~1205) x1


## HUD component tree — op35 IF_SETEVENTS x871 (binary layout confirmed)

Wire (12B fixed): [u16 fromSlot][u16 toSlot][u32 eventMask][u16 interface][u16 component].
Session configures 42 interfaces (the UI built on world entry):

- iface **137**: 4 comps [59, 62, 65, 86]
- iface **190**: 1 comps [5]
- iface **464**: 4 comps [181, 184, 186, 193]
- iface **550**: 2 comps [7, 52]
- iface **568**: 1 comps [5]
- iface **590**: 3 comps [1, 11, 12]
- iface **653**: 10 comps [155, 166, 177, 188, 199, 210, 221, 232, 243, 254]
- iface **1110**: 4 comps [31, 38, 83, 85]
- iface **1219**: 2 comps [1, 7]
- iface **1220**: 2 comps [1, 7]
- iface **1221**: 2 comps [1, 7]
- iface **1281**: 2 comps [9, 12]
- iface **1416**: 2 comps [3, 11]
- iface **1417**: 2 comps [9, 13]
- iface **1427**: 1 comps [29]
- iface **1430**: 38 comps [13, 18, 19, 26, 38, 59, 66, 69, 79, 82, 92, 95, 105, 108, 118, 121, 131, 134, 144, 147, 157, 160, 170, 173, 183, 186, 196, 199, 209, 212, 222, 225, 235, 238, 258, 259, 260, 261]
- iface **1431**: 1 comps [0]
- iface **1433**: 1 comps [6]
- iface **1449**: 2 comps [1, 7]
- iface **1452**: 2 comps [1, 7]
- iface **1458**: 1 comps [40]
- iface **1460**: 2 comps [1, 5]
- iface **1461**: 2 comps [1, 7]
- iface **1464**: 2 comps [15, 19]
- iface **1465**: 1 comps [18]
- iface **1466**: 1 comps [7]
- iface **1467**: 4 comps [180, 183, 185, 192]
- iface **1470**: 4 comps [181, 184, 186, 193]
- iface **1471**: 4 comps [181, 184, 186, 193]
- iface **1472**: 4 comps [187, 190, 192, 193]
- iface **1473**: 3 comps [5, 9, 20]
- iface **1477**: 197 comps [1, 2, 17, 24, 25, 26, 28, 51, 61, 63, 65, 67, 69, 71, 72, 74, 77, 79, 82, 84, 87, 89, 92, 98, 99, 101, 106, 107, 112, 117, 118, 123, 128, 129, 134, 139, 140, 145, 150, 151, 156, 161, 162, 167, 172, 173, 178, 183, 184, 189, 194, 195, 200, 205, 206, 211, 216, 217, 222, 227, 228, 233, 238, 239, 244, 249, 250, 255, 260, 261, 266, 271, 272, 277, 282, 283, 288, 292, 294, 298, 303, 304, 309, 314, 315, 320, 325, 326, 331, 336, 341, 346, 351, 352, 357, 362, 363, 368, 373, 374, 379, 384, 385, 390, 395, 396, 401, 406, 407, 412, 413, 418, 423, 424, 429, 433, 434, 439, 443, 444, 449, 453, 454, 459, 463, 464, 469, 473, 474, 479, 483, 484, 489, 493, 494, 499, 504, 505, 510, 515, 516, 521, 526, 527, 532, 537, 538, 543, 548, 549, 554, 559, 560, 569, 573, 577, 585, 589, 593, 597, 601, 606, 610, 614, 618, 622, 628, 631, 635, 641, 645, 649, 653, 657, 665, 669, 673, 677, 681, 685, 696, 701, 706, 708, 712, 717, 721, 727, 731, 736, 737, 741, 742, 751, 822, 895, 911]
- iface **1529**: 4 comps [180, 183, 185, 192]
- iface **1588**: 16 comps [9, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29]
- iface **1854**: 2 comps [6, 7]
- iface **1882**: 2 comps [1, 7]
- iface **1883**: 2 comps [1, 7]
- iface **1884**: 2 comps [1, 7]
- iface **1885**: 2 comps [1, 7]
- iface **1886**: 2 comps [1, 7]
- iface **1887**: 2 comps [1, 7]
- iface **1894**: 3 comps [16, 18, 19]

**iface 1477 = main in-game HUD (197 interactive components) — the root opened by op3.**


## CS2 scripts run — op110 RunClientScript x86 (build the HUD)

Wire: [type-descriptor str, null-term: 'i'/int else, 's'=string][args][scriptId g4 last].

- script **0** x3: desc='iiiisii' e.g. args=[1007, 21259, 16777215, 4278568195, '\x1d\x05Å\x03\x1cÿÿÿÿ', 8420, 0]
- script **139** x1: desc='i' e.g. args=[96796699]
- script **671** x1: desc='i' e.g. args=[0]
- script **3373** x2: desc='i' e.g. args=[1014]
- script **3543** x1: desc='i' e.g. args=[1]
- script **3957** x1: desc='' e.g. args=[]
- script **4308** x2: desc='ii' e.g. args=[0, 18]
- script **4704** x1: desc='' e.g. args=[]
- script **5557** x1: desc='i' e.g. args=[1]
- script **6504** x1: desc='iiiii' e.g. args=[8881, 0, 0, 0, 0]
- script **8178** x1: desc='' e.g. args=[]
- script **8778** x1: desc='' e.g. args=[]
- script **8862** x22: desc='ii' e.g. args=[1, 0]
- script **9542** x1: desc='' e.g. args=[]
- script **9945** x1: desc='' e.g. args=[]
- script **10623** x27: desc='ii' e.g. args=[0, 30522]
- script **11145** x1: desc='iiiii' e.g. args=[96797466, 0, 0, 600, 1067]
- script **14150** x1: desc='i' e.g. args=[5]
- script **15997** x1: desc='' e.g. args=[]
- script **16300** x1: desc='i' e.g. args=[0]
- script **18468** x1: desc='' e.g. args=[]
- script **18950** x1: desc='i' e.g. args=[1]
- script **18951** x8: desc='iiiiii' e.g. args=[4, 3, 0, 0, 0, 0]
- script **18952** x1: desc='i' e.g. args=[0]
- script **18954** x1: desc='ii' e.g. args=[0, 4]
- script **20093** x1: desc='i' e.g. args=[40]
- script **20392** x1: desc='' e.g. args=[]
- script **20611** x1: desc='' e.g. args=[]


## Player skills — op44 UpdateStat x29 (SOLVED: [g4 xp LE][g1 level][g1 skill byteNeg])

Local player = brand-new level-1 character (Constitution starts at lvl10/1154xp per RS table):

- skill 0 Attack: level 1, xp 0
- skill 1 Defence: level 1, xp 0
- skill 2 Strength: level 1, xp 0
- skill 3 Constitution: level 10, xp 1154 <-- non-default
- skill 4 Ranged: level 1, xp 0
- skill 5 Prayer: level 1, xp 0
- skill 6 Magic: level 1, xp 0
- skill 7 Cooking: level 1, xp 0
- skill 8 Woodcutting: level 1, xp 0
- skill 9 Fletching: level 1, xp 0
- skill 10 Fishing: level 1, xp 0
- skill 11 Firemaking: level 1, xp 0
- skill 12 Crafting: level 1, xp 0
- skill 13 Smithing: level 1, xp 0
- skill 14 Mining: level 1, xp 0
- skill 15 Herblore: level 1, xp 0
- skill 16 Agility: level 1, xp 0
- skill 17 Thieving: level 1, xp 0
- skill 18 Slayer: level 1, xp 0
- skill 19 Farming: level 1, xp 0
- skill 20 Runecrafting: level 1, xp 0
- skill 21 Hunter: level 1, xp 0
- skill 22 Construction: level 1, xp 0
- skill 23 Summoning: level 1, xp 0
- skill 24 Dungeoneering: level 1, xp 0
- skill 25 Divination: level 1, xp 0
- skill 26 Invention: level 1, xp 0
- skill 27 Archaeology: level 1, xp 0
- skill 28 skill28: level 1, xp 0

## op92 ClientSetVarcStr x35: action-bar labels (familiar special ability) + 33 slot varcs cleared.


## Player appearance — op22 GPI (byteAdd-encoded; subtract 0x80 per byte)

World-entry GPI (677B) carries 7 players' appearances: ['AlQaeda123', 'Bagrunga', 'ZestyKso', 'beerlovesme', 'iGoldenMerK', 'osrsbt2', 'slimypea']

Appearance block (around name, byteAdd-decoded): `......................AlQaeda123...&....`
Structure: [...12 body-part slots (0=bare, else equipped item/model)][5 colours][name str][combat level][flags].


## Scene / zone content (world entry)

- **op81 RebuildNormalSimple** (5137B): full XTEA scene build; build-area center bit-packed (zoneX~404 -> tileX~3232). Real region loaded.
- **op46 ObjAdd x21**: ground items [coord][qty=1][u16 item]. Items+counts: {720: 1, 818: 2, 686: 1, 1010: 1, 1816: 4, 1795: 1, 1803: 1, 1807: 1, 1075: 1, 1797: 1, 1077: 1, 1383: 4, 1137: 2}
- **op90 LocAdd x2**: scene objects added ([coord][locInfo][id]).
- **op16 LocDel x24**: scene objects removed (scene customized around player).
- Plus op78 UpdateZoneFullFollows x616 + op76 PartialEnclosed x64 = the zone tile/loc stream filling the build area.


## Scene footprint & NPCs

- **op78 UpdateZoneFullFollows x616** (all 3B): header [zoneX][zoneY][0x0a]. 52 distinct zones, X=128-131, Y=106-118 (~4x13 zone footprint).
  If absolute zones -> scene tiles ~(1024-1056, 848-952). (Absolute-vs-build-relative unconfirmed; conflicts w/ op81 bit-estimate so treat as zone footprint, not a named locale.)
- **op52 NpcInfo x53** (bitpacked GPI, 28-803B): the large 707/803B packets = world-entry NPC spawns. Scene is populated with NPCs; per-NPC ids need a full bit-parse.

## CORRECTION (op78 zone coords) — from UPDATE_ZONE_FULL_FOLLOWS decomp
op78's [b0][b1][b2] are NOT absolute zones. The handler reads:
- b0: relX = (b0 + 0x80) & 0xFF  -> 0..3  (byteAdd; sample 0x80-0x83).
- b1: relY via iVar28 + ((char)(-0x80 - (char)b1))*8, where **iVar28 = build-area base** (*thisPtr+0x60c, set by op81 RebuildNormal).
- b2 (0x0a) = plane/level field.
=> The 52-zone footprint is RELATIVE to the op81 build base (X 0-3, Y ~0-12 zones). Absolute tile = op81 base + these offsets.
   (So the earlier "tiles ~1024-1048" guess is withdrawn; needs op81's bit-parsed base for the real world tile.)

## op81 RebuildNormalSimple structure (from decomp) — XTEA scene key list
[byte flag !=0] then a loop of scene entries:
  - u32 (BE) mapSquareRef -> game::RebuildSceneEntry::Reset(scene, ref)
  - byte keyCount
  - keyCount x u32 (BE) XTEA keys
Large zero runs = empty/keyless map squares (sparse build grid). 5137B = the loaded region's key table.
NOTE: absolute player tile NOT reliably extracted - the mapSquareRef bit-layout (x/y/plane) lives in RebuildSceneEntry::Reset,
which isn't decompiled here; first ref 0xa3328800 doesn't map cleanly. Not guessing a tile.

## Misc packets (decoded)
- **op93 GameMessage**: [type 0x80][6B hdr][string] = "Pay through your play! Bonds available now on the Grand Exchange." (login MOTD/advert).
- **op95 MidiSong**: 5B, a music track triggers (id ~0x7494).
- **op47 ClientSetVarcSmall x61**: client (UI) vars [u16 id][u8 val], ids 32523-35523. Run 33213-33237 all=5.
- **op64 ClientSetVarcLarge x2**: [u16 id][u32 val] (2699=35587, 52386=52974346).
- **op174 AntiCheatChallenge x5**: 8B (64-bit) challenge values -> client computes a response (challenge-response anti-cheat).
- **op119 CutsceneData x8**: 35B each, indexed 0-7 (camera/cutscene keyframes; mostly zero in this session).
- (op172/204 MinimapFlagA/B, op157 SceneFlag, op154 EntityAnimAtTile, op190 ClearPendingUpdates, op162 TRIGGER_ONDIALOGABORT x54 = control/flag packets.)


## Player equipped gear (op22 GPI appearance body-slots = raw 0x8000|itemId)

- **AlQaeda123**: worn item ids [415, 13708, 2441, 128, 128, 128, 128, 128, 128, 128, 2571]
- **slimypea**: worn item ids [486, 90, 1924, 128, 128, 128, 128, 128, 128, 128, 2571]
- **osrsbt2**: worn item ids [487, 1409, 1928, 128, 128, 128, 128, 128, 128, 128, 2574]
- **Bagrunga**: worn item ids [227, 25356, 385, 128, 128, 128, 128, 128, 128, 128, 2571]
- **beerlovesme**: worn item ids [128, 128, 5287, 10225, 385, 3200, 128, 128, 128, 128, 128, 128, 2815]
- **ZestyKso**: worn item ids [415, 13696, 19148, 650, 128, 128, 128, 128, 128, 128, 128, 2571]
- **iGoldenMerK**: worn item ids [128, 128, 16, 1923, 128, 128, 128, 128, 128, 128, 128, 2815]

## REFINED: equipped gear (filter 0x8080=128 empty-slots + trailing shared field 2571/2574/2815)
Each of the 7 GPI players has a distinct set of real appearance item ids (caveat: slot alignment approximate w/o full appearance parser):
- AlQaeda123: [415, 13708, 2441]
- slimypea: [486, 90, 1924]
- osrsbt2: [487, 1409, 1928]
- Bagrunga: [227, 25356, 385]
- beerlovesme: [5287, 10225, 385, 3200]
- ZestyKso: [415, 13696, 19148, 650]
- iGoldenMerK: [16, 1923]

## op82 IF_SETPOSITION (23B) — structure known, exact positions obscured
- byte[0]=flag; bytes[1:3]=interface (reads 0 = root/HUD-1477's children); remaining 20B = component + x/y + anchors/size.
- After GetInterface the decomp is mutex + std::deque element-moves (position queued to a render list) — the x/y field reads
  are buried in container mgmt, NOT cleanly readable. iter13 scan suggested component@~off6 (1311-1416), a coord@~off20 (178-202).
- => like GPI/NPC, this complex packet's exact layout is obscured in the decompile. Component-tree (op35) gives the WHAT;
  op82 gives the WHERE but only approximately.

## NPC scene density — op52 NpcInfo (validated partial decode)
- First byte of op52 = high-res NPC count. Sequence: 0,30,60,90,95,... stabilizing ~92-99.
- VALIDATED: count grows 0->95 as scene loads (NPCs added ~30/tick over 4 ticks), then steady ~95 with edge fluctuation.
  Big packets (707/703/803B) = the count-jump (adding) packets; small packets = movement-only. Classic GPI behavior.
- => ~**95 NPCs in the player's view** + 7 visible players = a BUSY/populated area (hub/city/bank).
- (Per-NPC ids/coords still need the obscured bit-parser, but the COUNT is validly extracted from the count-byte + size correlation.)

## CORRECTION: op22 handler mapping + GPI bit-loop status (iter39)
- FUN_00121a30 = jag::Packet::gT<unsigned short> (g2 big-endian u16 read). Confirms the BE-u16 primitive used throughout.
- PLAYER_INFO_DECODE @0x183ed0 is BYTE-ALIGNED ([3-bit type][5-bit subtype], g2 coords, u32, hint-arrow/graph-node mgmt) =
  a placement/marker decoder, NOT the bit-packed 2046-player GPI loop.
- => the 12-opcode-map op22->PLAYER_INFO_DECODE match is a NAME-correlation FALSE MATCH. The 12-opcode-map handler NAMES are
  approximate (name-correlated, not dispatch-validated); only the RegisterAll SIZE table (14-serverprot-table) is definitive.
- => my earlier "GPI decoder obscured" conclusion looked at the WRONG function. The REAL op22 GPI bit-loop handler is UN-FOUND
  (needs the actual entry->handler dispatch link, which static .bss reads can't resolve). GPI internals: still undetermined, but for a clearer reason.

## DEFINITIVE: GPI/NPC handler dispatch is statically blocked (iter40, TESTED)
- Read the ServerProt entry objects directly: op3/op22 (0x15c region) AND op52/op0/op9 (0x13a region) = ALL ZERO (.bss).
- => the opcode->handler table (entry vtables) is built at RUNTIME (static ctors + InitEntry). Static Ghidra sees only zeros.
- => the REAL op22 GPI / op52 NPC bit-loop handlers CANNOT be located by static analysis. Reaching them needs the MCP (in use by
  other agent) or runtime instrumentation. This is the tested, definitive reason the per-entity GPI/NPC internals are out of reach.
- Everything else (sizes, strings, counts, inventory, varps, world list, login, anti-cheat) is statically decodable and validated.

## BREAKTHROUGH: GPI-chain functions reachable BY NAME (bypasses .bss dispatch) — iter41
- Found jag::PlayerEntity::SetAppearanceAsPlayer@0x14d5b0, ProcessExtendedInfo@0x15e290, ApplyDeferredAppearance@0x14eaf0.
- CORRECTS iter40: the dispatch TABLE is .bss (opcode->handler link blocked), but the HANDLER FUNCTIONS exist + are
  decompilable by name. So the GPI internals are NOT unreachable - just not linkable via the table.
- APPEARANCE format (hypothesis from the code reads, TO VALIDATE against data; decompile also had a prior annotation
  referencing docs/ which I am NOT relying on): flags g1; title gSmart1or2 if flags&0x40; extras [g1 cnt, cnt*(g2+g1)] if
  flags&0x2; gender g1; body/equip slots; override [g1; g2 if flags&4 else g1+g1]; colours [g1 flag; if!=0 -> 4*g2+g1].
  flag bits: 0x1 visible,0x2 extras,0x4 npc-morph,(>>3&7)+1 scale,0x40 title,0x80 gender-variant. name/combat = SEPARATE ext-info masks.
- NEXT: read ProcessExtendedInfo (ext-info mask order) -> locate appearance mask in op22 payload -> decode + VALIDATE (gender/colours sane).

## Appearance decode — METHOD found, but INTEGRITY BOUNDARY (iter42)
- Method win stands: GPI-chain decoders (SetAppearanceAsPlayer, ProcessExtendedInfo) are reachable BY NAME (bypasses .bss dispatch).
- ProcessExtendedInfo shows the appearance ext-info uses an ANTI-TAMPER "scrambled byte" length (gScrambledByte) - real obfuscation finding.
- BUT: the decompile annotations spell out the full obfuscated appearance format AND reference docs/protocol/player-appearance-948.md
  (prior RE / other-agent's write-up). Per "read no docs, discover yourself", I am NOT leaning on those annotations to produce a decode.
- HONEST STATUS: a faithful, independent appearance decode requires reversing the scramble + mask order from raw code (deep effort).
  Flagging rather than shortcutting. The player's body/colours/title therefore remain NOT-decoded-by-me (method known, format off-limits-via-annotations).

## Anti-tamper "scrambled read" family — DERIVED FROM CODE (iter43)
jag::Packet::gScrambled{Byte,Ubyte,Ushort,Medium,Uint} @ 0x47f840/790/5f0/4f0/3e0. A cipher-state byte (packet+0x28,
post-incremented) cycles 0..3 and selects the transform (read from the actual return-statement code, not the annotations):
- gScrambledByte (signed):  0=raw, 1=b-0x80, 2=-b, 3=(-0x80 - b) sign-ext.
- gScrambledUbyte (unsigned):0=raw, 1=b-0x80, 2=-b, 3=(0x80 - b).
- gScrambledUshort: 0=raw BE u16, 1=LE(b1<<8|b0), 2=BE low+0x80, 3=LE low+0x80. (Medium/Uint = same over 3/4 bytes.)
=> The appearance ext-info LENGTH is read via gScrambled* => obfuscated; un-scrambling needs the per-packet cipher state.
   This is the anti-tamper; mechanism is now code-derived (mine). Full appearance decode still needs cipher-state tracking + mask order.

## Appearance decode — CONCLUDED (iters 41-44): blocked by cipher-keyed scramble, mechanism derived (mine)
- Reachable by name (method win), scramble TRANSFORM derived from code (gScrambled* 4-mode). BUT:
- packet+0x28 = a moving pointer into a KEYSTREAM (post-incremented per scrambled read) => the per-read transform mode is
  selected by a cipher byte entangled with the session cipher (ISAAC) state, NOT a trackable counter. Cipher-setup fn not name-findable.
- => un-scrambling the appearance body/colours requires reconstructing the session keystream state at each read
  (reimplement+track the cipher across all scrambled reads) - a deep effort, not a static decode from the (opcode-deciphered) capture.
- HONEST RESULT: player NAME (un-scrambled byteAdd) IS decoded; player APPEARANCE (body/colours/title, scrambled) is NOT - blocked by
  the cipher-keyed scramble. Mechanism is code-derived (mine, not the annotations); the keystream entanglement is the true blocker.

## op82 IF_SETPOSITION — interface field VALIDATED (iter46)
Interface id @ bytes[6:8] (56/56 in HUD range 1311-1416, validated). op82 positions 53 HUD sub-interfaces (panels) into the 1477 layout:
  [1280, 1281, 1291, 1292, 1303, 1309, 1311, 1313, 1317, 1323, 1324, 1326, 1335, 1344, 1350, 1367, 1375, 1376, 1378, 1381, 1383, 1385, 1389, 1394, 1400, 1402, 1411, 1416, 1422, 1423, 1427, 1433, 1436, 1438, 1444, 1449, 1455, 1459, 1460, 1465, 1471, 1475, 1482, 1483, 1485, 1493, 1494, 1495, 1504, 1505, 1515, 1525, 1526]
Exact x/y partial (off20~178-202 a coord; off4=197 const; full layout obscured by render-queue per iter27). WHICH panels = solid; WHERE = partial.

## op82 RESOLUTION: pixel coords are NOT on the wire (iter47)
- op82 IF_SETPOSITION bytes[9:20] (the x/y/size region) are ALL ZERO across all 56 packets => sub-interfaces placed at (0,0) defaults.
- => the visual HUD pixel layout comes from the CACHE interface definitions, NOT the packet. op82 just attaches sub-interface
  X to the layout at its default slot. So "exact x/y" was never obscured - it's genuinely absent from the wire (cache-defined).
- WHICH sub-interfaces (1280-1416, validated iter46) = decoded. WHERE (pixels) = in the cache, not this capture. Honest resolution, not a blocker.

## op121 UPDATE_INV_PARTIAL decoded (iter49) — the single in-session inventory change
(decoded above: container + slot + item + count of the partial update)

## UI detail packets decoded (iter50): op91 IfSetHide / op62 IfCloseSub / op30 IfSet2DAngle
(component hash = iface@validated offset; shows which HUD components are hidden/shown, which sub-interfaces closed, which models rotated)

## UI detail layer (iter50): hide / rotate / close
- op91 IfSetHide x11: visibility toggles in HUD interfaces 1417, 653, 1477, 745, 1253, 1110 (5B hash+flag; exact offset approximate, interfaces valid).
- op30 IfSet2DAngle x2: model rotation in interface 1799 (a model-display widget), angle bytes 00 aa 0a.
- op62 IfCloseSub x2: closes 2 sub-interfaces.
=> UI now detailed end-to-end: open(op3) + position(op82) + events(op35) + scripts(op110) + text(op122) + hide(op91) + rotate(op30) + close(op62).

## Minor control/state packets reviewed (iter53)
- op74 JcoinsUpdate (4B 0xada90a4a): NO interpretation = sensible balance (2.9B / 44457,2634). Fresh char => likely SCRAMBLED (anti-tamper
  on currency, like appearance). Flagged, not asserted.
- op172 MinimapFlagA=1, op204 MinimapFlagB=0xff(-1): 1-byte minimap STATE flags (not walk destinations).
- op154 EntityAnimAtTile (5B): an animation at a zone tile (coord+anim). op157 SceneFlag=0.
=> minor control surface reviewed; nothing further decodable with concrete content.
