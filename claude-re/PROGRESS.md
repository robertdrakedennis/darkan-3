# PROGRESS — production packet-session RE loop (workspace: claude-re/)

## Task (5-min loop, cron job 358d80f1)
Fully understand the last production packet session. Discover EVERY packet, opcode, UI piece created,
everything about the player + inventory. Use **headless Ghidra** (NOT the MCP — another agent is using it;
be targeted/careful). Isolated workspace; read NO external docs — only this folder.

## Sources
- Capture: `build/undercut-socket-session-production-isaac.jsonl` (deframed lobby+world session;
  fields: dir, conn, opcode, name, size_kind, size, isaac_index, payload_hex).
- Binary for headless RE: `rs2client.948-5` (RS3 macOS client). MCP project lives at
  `~/projects/reclass-data/rs2client-948` — DO NOT lock it; copy it or fresh-import the binary for headless.

## Session shape (iter1)
- 2 connections (lobby + world, both :443). Lobby S2C 17 ops/1670 pkts, lobby C2S 4/13;
  world S2C **58 ops/3790 pkts**, world C2S 14/80. See findings/00,01,02.

## Done
- [x] iter1: workspace + full opcode inventory + world S2C run-length sequence
      → findings/00-OVERVIEW.md, 01-opcodes-all.md, 02-world-s2c-sequence.md

## Next (do in order; one+ per iteration; record findings/ + tick here)
- [ ] Set up headless Ghidra: find analyzeHeadless; COPY the project (or import binary fresh) into claude-re/ghidra
      so we never touch the MCP's locked project. Write a GhidraScript to batch-dump opcode handlers.
- [ ] Per S2C opcode (world 58 + lobby 17): handler addr + wire format + semantics from the binary → findings/1x-*.md
- [ ] UI: decode op3 IfSetTopLevelInterface / op82 IfSetPosition / op35 IfSetEvents / op110 RunClientScript /
      op122 IfSetText / op91 IfSetHide → exact interface+component IDs created → findings/20-ui.md
- [ ] Player: op17 SetPlayerOp, op22 PlayerInfo(GPI)+appearance, op44 UpdateStat (skills) → findings/30-player.md
- [ ] Inventory: op85 UPDATE_INV_FULL → container id + item list → findings/40-inventory.md
- [ ] Varps: op5 reset + op28/op61/op147 baseline → findings/50-varps.md
- [ ] C2S: world 14 (op240…) + lobby 4 → findings/60-c2s.md

## Ghidra setup status
(locating analyzeHeadless — see top of this run's output)

## Ghidra setup (RESOLVED iter1)
- analyzeHeadless: `/Users/robert/Applications/ghidra_12.0.4_PUBLIC/support/analyzeHeadless`
- MCP project (LOCKED by GUI pid 35094, port :9080 — another agent uses it): `~/projects/reclass-data/rs2client-948`
- Headless plan (iter2): copy the project dir to `claude-re/ghidra/proj-copy` (it's already analyzed → no re-analysis),
  then: `analyzeHeadless claude-re/ghidra <projname> -process rs2client.948-5 -scriptPath claude-re/scripts -postScript <dump.java> -readOnly`
  (or `-import` the raw binary if copy is unclean). Binary base 0x0 (Ghidra addrs are absolute file offsets).
- Targeted-only: dump specific handler funcs per opcode; never bulk-reanalyze; keep runs short to not contend with the MCP.

## Headless VALIDATED ✅ (iter1)
Working, lock-free, no MCP contention. Program name "rs2client", IMAGE BASE 0x00000000 (Ghidra addrs are absolute), 20471 functions.

Command template (fill in <Script>.java in claude-re/scripts/):
```
/Users/robert/Applications/ghidra_12.0.4_PUBLIC/support/analyzeHeadless \
  /Users/robert/projects/darkan3-server/claude-re/ghidra rs2client-948 \
  -process 'rs2client.948-5' -readOnly \
  -scriptPath /Users/robert/projects/darkan3-server/claude-re/scripts \
  -postScript <Script>.java 2>&1 | grep -E '^<TAG>|GhidraScript'
```
- Prefix every script output line with a unique TAG (e.g. OPC=, UI=) and grep for it; ignore benign noise
  ("Module manifest ... GhidraMCP", "Invalid PNG data").
- Each headless run is ~15-30s startup; batch many queries into ONE script per run (don't relaunch per function).

## ITER2 START HERE
Write claude-re/scripts/DumpProt.java: locate ClientProt/ServerProt opcode->handler registration in the binary,
dump the opcode->function map, then per S2C opcode decompile the handler and record the wire read sequence.
Cross-reference against findings/01-opcodes-all.md (the 58 world + 17 lobby S2C ops seen on the wire).

## iter2 DONE
- findings/10-prot-registration.md: registration fns — jag::ServerProt::BindHandlers (S2C, @0x7509a etc),
  jag::ClientProt::RegisterAll (C2S, @0xe6170). BindHandlers binds handlers by setting global fn-ptrs to
  named jag::packethandlers::* functions (the prior-RE renames are usable; the binary IS the truth).
- findings/11-handlers-all.md: full jag::packethandlers::* inventory (categorized) = every packet handler.

## iter3 START HERE
1. Map wire opcode -> handler: find the S2C DISPATCH (reads opcode byte, indexes a handler table/global).
   Likely in ServerProt/Session read loop. Get opcode->packethandlers::* mapping.
2. Cross-ref with findings/01-opcodes-all.md (58 world + 17 lobby S2C ops on the wire) -> name each wire opcode.
3. Then decompile the UI handlers (Interfaces::IF_SET*), player (PlayerInfo/appearance/SetPlayerOp),
   inventory (UPDATE_INV*), stats (UpdateStat) -> wire format + what UI/data they build. findings/2x-*.md

## iter3 DONE
- Decompiled 52 handlers from the binary with full wire formats:
  - findings/20-interfaces.md (40 UI handlers: IF_SETTOPLEVELINTERFACE, IF_OPENTOP/OPENSUB/CLOSESUB,
    IF_SETPOSITION/SETEVENTS/SETTEXT/SETHIDE, IF_SETMODEL/SETOBJECT/SETNPCHEAD/SETPLAYERMODEL_*,
    IF_SETSPRITE/SETGRAPHIC/SETCOLOUR/SETRECOL/SET2DANGLE/SETSCROLLPOS, ...)
  - findings/21-player.md (PlayerInfo/PlayerList/PlayerGroup)
  - findings/22-inventory.md (Inventory)

## iter4 START HERE
1. Build the wire opcode -> handler map: the S2C dispatch (reads opcode, calls bound handler). Find it
   (ServerProt/Session read loop) and emit opcode#->packethandlers::* . Write findings/12-opcode-map.md.
2. Cross-ref findings/01-opcodes-all.md (actual wire opcodes) with 12-opcode-map + the 20/21/22 decomps:
   for EACH packet seen in production, decode payload_hex via the handler's wire reads -> the SPECIFIC
   interfaces opened, component text/positions/events, player appearance+stats, inventory container+items.
   Write findings/30-session-decoded.md (the human story of the production session).
3. Sweep remaining categories into 23-*.md: ClientState(33), ZoneUpdates(30), Misc(20), Chat(15),
   NPCInfo(8), Camera(5), Audio(10), WorldData(3), Rebuild, SiteSettings, Clans, Friends, Social.

## iter4 DONE
- findings/12-opcode-map.md: wire opcode -> binary handler @ addr (52 matched, 41 unmatched).
  Confirmed e.g. op3 IF_SETTOPLEVELINTERFACE@0x186a80, op22 PLAYER_INFO_DECODE@0x183ed0,
  op52 NPC_INFO@0x1d54f0, op78 UPDATE_ZONE_FULL_FOLLOWS@0xf9510, op28/61 VARP_*, op16/46/76 ZoneUpdates.
- findings/23-world-state.md: 93 handler decompiles (ZoneUpdates/ClientState/Misc/WorldData/Rebuild/SiteSettings).

## iter5 START HERE
1. Fix the 41 unmatched in 12-opcode-map.md: op5 ResetClientVarcache, op44 UpdateStat, op54 HashedWorldToken,
   op73 MinimapState, op74 JcoinsUpdate, op77 CAMERA_UPDATE(121x!), op49/26 lobby. Search those categories
   (Camera/Misc/ClientState/WorldData) by addr; some may live under different binary names — decompile to confirm.
2. DECODE THE SESSION payloads -> findings/30-session-decoded.md:
   - Interfaces actually opened: every op3 payload (19x) -> interface id (e.g. 1477 HUD), op82 positions,
     op35 events, op122 text, op62 closes. Build the UI tree the session creates.
   - Player: op22 PLAYER_INFO_DECODE wire format -> local player spawn/appearance; op17/12/13 SetPlayerOp ops;
     op44 UpdateStat (6x) -> skills. 
   - Inventory: find the UPDATE_INV opcode in the world stream + decode container id + item list.
3. Sweep last categories: Chat(15) NPCInfo(8) Audio(10) Camera(5) Clans(4) PlayerGroup Friends Social -> 24-*.md

## iter5 DONE
- findings/29-session-payloads.md: every world S2C opcode's actual payloads from the session (full for low-count).
- findings/30-session-decoded.md: started — op3 IF_SETTOPLEVELINTERFACE payload decoded -> interface **1477** (in-game HUD).
- BUG to fix in 12-opcode-map.md: the "count" column is actually the SIZE (parse the 5th col of 01-opcodes-all.md, not 4th).
- NOTE: 24-other.md sweep (Audio/Camera/Chat/NPCInfo/Clans/...) may not have completed — verify + retry.

## iter6 START HERE
1. If 24-other.md missing/short, re-run DumpRest2.java (categories Audio,Camera,Chat,Clans,Friends,NPCInfo,PlayerGroup,Social).
2. Fix 12-opcode-map count column (use the real per-opcode counts from the jsonl, not 01's size col).
3. Decode the rest of the session into 30-session-decoded.md using 29-session-payloads.md + the handler wire formats
   in 20/21/22/23/24: interfaces (op82/35/122/62), player (op17/12/13/44 + op22 appearance), inventory (UPDATE_INV),
   camera (op77). Produce the full "what the production session builds" narrative.

## iter6 DONE
- 24-other.md: 49 handlers (Audio/Camera/Chat/Clans/Friends/NPCInfo/PlayerGroup/Social/Lobby).
  => ALL 192 handlers now decompiled (20-interfaces, 21-player, 22-inventory, 23-world-state, 24-other).
- 12-opcode-map.md regenerated with CORRECT per-opcode counts + sizes (was showing size as count).

## iter7 START HERE — full session decode into 30-session-decoded.md
Use 29-session-payloads.md (payloads) + handler wire formats (20-24). Decode, in order:
- op77 CAMERA_UPDATE (121x): camera pos/angle per tick (find handler in 24-other Camera).
- op17 SET_PLAYER_OP@0x13f040 (11x), op12/13 SET_PLAYER_OP_2/3: player right-click option strings.
- op44 UpdateStat (6x): skill id + level + xp -> the player's skills.
- op22 PLAYER_INFO_DECODE@0x183ed0: local player spawn coord + appearance block.
- Inventory: which world opcode = UPDATE_INV? (check 12-opcode-map Inventory) -> container id + item ids/counts.
- op82 IF_SETPOSITION / op35 IF_SETEVENTS / op122 IF_SETTEXT: the HUD(1477) component tree built.

## iter7 DONE
- findings/30-session-decoded.md (appended): 
  - PLAYER right-click ops (op17): Follow, Trade with, Req Assist, Duel, Examine (+null). Format approx [0xff 0xff][slot][string].
  - HUD text (op122 IF_SETTEXT): partial — string offset off ("nture"->Adventure?, "mn Voyage"); hash/length needs the IF_SETTEXT decomp.
  - Skills (op44): payloads dumped but [skill,level,xp] doesn't map cleanly; need UpdateStat decomp.

## iter8 START HERE
1. Read IF_SETTEXT decomp (in 20-interfaces.md) -> exact wire (interface hash size? string length prefix?) -> re-decode op122 text correctly.
2. Find UpdateStat handler decomp (search 23-world-state/24-other for "UpdateStat"/"STAT") -> decode op44 skill/level/xp.
3. op22 PLAYER_INFO_DECODE@0x183ed0: read decomp -> local player spawn tile + appearance block (gender, body parts, colors, equipment).
4. Inventory: confirm world inv opcode (UPDATE_INV*) from 12-opcode-map Inventory rows -> decode container id + item ids+counts.
5. op82 IF_SETPOSITION + op35 IF_SETEVENTS: decode the HUD(1477) component tree (which components positioned + event masks).
6. op77 CAMERA_UPDATE (read Camera decomp) -> camera angle/pos sequence.

## iter8 DONE
- Inventory decoded -> findings/30-session-decoded.md: op85 UPDATE_INV_FULL x8 containers
  [u16-BE id][u8][u16-BE size][per-slot u16 item, u8 count]. Containers: 93=inventory, 94=worn equip, +787/795/891/895/623/670.
- Full world S2C opcode list w/ correct counts now in 12-opcode-map.md (op81 entry, op22/104 player, op44 stats x29,
  op85 inv, op78 zone x616, op35 events x871, op82 pos x56, op110 cs2 x86, op77/119/120 camera, op199 rebuild...).

## iter9 START HERE
1. Verify the op85 item layout against the UPDATE_INV_FULL_impl loop decomp (count may be g1/g4; item may be +1/bigsmart) -> fix counts.
2. op44 UpdateStat: read its decomp (find addr) -> decode the 29 skill updates (skill id/level/xp).
3. op22 PLAYER_INFO_DECODE@0x183ed0 + op104: local player appearance block (gender/bodyparts/colors/equipment).
4. op82 IF_SETPOSITION(56) + op35 IF_SETEVENTS(871): the HUD-1477 component tree (which components, positions, event masks).
5. op110 RunClientScript(86): the CS2 script ids run (the HUD onload scripts).

## iter9 DONE — inventory format NAILED (byte-exact, validated)
- Read UPDATE_INV_PARTIAL @0x184320 loop -> exact slot encoding (via read-cursor +N tracing):
  FULL: [u16 container][u8 flag][u16 size] then size x { [u16 item, 0=empty][g1 count, 0xFF->g4] [+u8 if flag&0x02] }.
  PARTIAL adds a leading smart slot-index per entry (1B if <0x80 else u16&0x7fff); item==0 => empty (no count).
  Item field = u16 (likely id+1, RS convention => actual id = value-1).
- Re-decoded all 8 op85 containers, ALL consume exactly (used==total): 93=item316x1, 94/670=item1206 slot3,
  795=item52556 x1000 (g4 escape!), 895=10 display ids x0, 787/891/623 empty. -> 30-session-decoded.md.
- TECHNIQUE that works: trace *(param_2+0x18) increments (+2=u16,+1=byte,+4=u32) + byteswap(DAT_01050dc0) for BE.

## iter10 START HERE
1. S2C DISPATCH (resolves all 41 unmatched at once): find the fn that reads the opcode byte and calls the handler
   table (look near ClientProt@0xe5a70 / the BindHandlers globals 0x13a1xxx; trace where the opcode indexes the table).
   -> definitive opcode->handler for op44/op77/op54/op73/op74 etc. Write findings/13-dispatch.md.
2. op44 UpdateStat (29x): payloads are [xp g4=0][level g1=1][last byte 0x00,0xff,0xfe..0xe4]. Last byte as signed = 0,-1..-27.
   Find the real handler (via dispatch) + decode skill/level/xp properly. (Our deframer name may be wrong.)
3. op22 PLAYER_INFO_DECODE@0x183ed0 + op104(8x): local player spawn tile + appearance block.
4. op82 IF_SETPOSITION(56) + op35 IF_SETEVENTS(871): HUD-1477 component tree (apply read-cursor technique to their decomps).

## iter10 DONE — DEFINITIVE opcode table found + validated
- Found jag::ServerProt::RegisterAll @0xc4700 via handler-table xref analysis (DumpDispatch.java).
  Pattern: InitEntry(entry_slot, opcode, size). size>=0 fixed, -1 var-byte(g1), -2 var-short(g2).
- Parsed 218 opcodes -> findings/14-serverprot-table.md: op -> size + entry-slot addr.
- VALIDATED vs production capture: 37/37 fixed-size opcodes match, 0 mismatch. var opcodes correctly var (op17 SetPlayerOp=var-byte, op22 PlayerInfo=var-short).
- findings/13-dispatch.md: top slot-readers (RegisterAll=248, then FUN_000b0a80=87, FUN_000c8ef0=60... = dispatch/decode candidates).
- opcode->entry-slot captured (DAT_xxx per op) -> enables the handler-name join next.

## iter11 START HERE
1. HANDLER JOIN (verify the 41 unmatched incl op44): RegisterAll gives op->entry slot. Each entry is a C++ obj;
   its vtable/decode method = the packethandler. Either (a) find the dispatch (FUN_000b0a80/c8ef0) that calls
   entry->decode and read which packethandlers it routes to, or (b) match BindHandlers writes (slot+offset=handler)
   to RegisterAll slots. Confirms op44's REAL identity (deframer "UpdateStat" may be wrong - payloads don't decode as skill/level/xp).
2. op22 PLAYER_INFO_DECODE@0x183ed0 + op104(8x): local player spawn tile + appearance (gender/bodyparts/colors/equipment).
3. op82 IF_SETPOSITION(56) + op35 IF_SETEVENTS(871): HUD-1477 component tree. Use read-cursor +N technique.
4. op110 RunClientScript(86): CS2 script ids (HUD onload).

## iter11 DONE — HUD component tree mapped
- IF_SETEVENTS (op35 x871) layout NAILED empirically: [u16 fromSlot][u16 toSlot][u32 eventMask][u16 iface][u16 comp].
  -> findings/30-session-decoded.md: 42 interfaces configured. iface 1477=main HUD (197 comps), 1430(38), 1588(16), 653(10),
  action-bar widgets 1219-1221 & 1882-1887. Event masks 0xffffffff(all)/0x0(none)/0x3000400 etc = per-comp option bits.
- Confirmed sizes (def table): op35=12, op82 IF_SETPOSITION=23, op44 UpdateStat=6, op91 IfSetHide=5, op110/op122 var-short.
- Handler-join via static vtable read is blocked (.bss entries read 0 statically; vtables set at runtime). Name-correlation + size table suffice.

## iter12 START HERE
1. op82 IF_SETPOSITION x56 (23B): iface @ bytes[1:3] (GetInterface). Decode component POSITIONS (x,y placement) -> where each HUD piece sits.
2. op22 PLAYER_INFO_DECODE@0x183ed0 (var-short): local player appearance (find appearance-block reads: gender, head/body/colors, equipment ids).
3. op44 (6B fixed, "UpdateStat"): payloads 0000000001XX + 820400000afd. Re-examine as [xp?][level?][skill?] or find real identity. 29x at login.
4. op110 RunClientScript x86 (var-short): CS2 script ids run (which scripts assemble the HUD). Decode the int/string args.

## iter12 DONE — CS2 scripts (HUD assembly) decoded
- op110 RunClientScript x86 format NAILED: [type-desc str null-term ('i'=int,'s'=string)][args][scriptId g4 LAST].
  -> 30-session-decoded.md: 28 distinct CS2 scripts. Bulk: 8862 x22 (ii, HUD tabs/windows), 10623 x27 (ii, components),
  18951 x8 (iiiiii). script0 x3 (iiiisii: colors+string), 11145 args[...,600,1067]=screen dims. +23 singletons.
- (op110's 'RUNCLIENTSCRIPT' decomp in 23-world-state.md is mislabeled - real handler differs - but payload decode is solid/validated.)

## iter13 START HERE (last session pieces)
1. op82 IF_SETPOSITION x56 (23B fixed): iface@bytes[1:3]. Find comp + x + y fields (scan u16 offsets for plausible coords). -> HUD layout positions.
2. op22 PLAYER_INFO_DECODE@0x183ed0 (var-short, 54x): decode local-player appearance block (the BIG remaining player item: gender, body parts, colors, equipped item ids, name, combat level).
3. op44 (6B, deframer 'UpdateStat'): payloads 0000000001XX (XX desc 00,ff..e4) + 820400000afd. Doesn't fit skill/lvl/xp. Find real handler/meaning.
4. Optional sweep: op92 ClientSetVarcStr x35 (string varcs - names/labels), op47/64 varc, op28/61 varps - the var state set at login.

## iter13 DONE
- op44 UpdateStat SOLVED+validated: [g4 xp LE][g1 level][g1 skill byteNeg=(-b)&0xFF]. Player=fresh lvl1 char,
  all skills lvl1/0xp EXCEPT Constitution(skill3) lvl10/1154xp (RS default). -> 30-session-decoded.md.
- op92 ClientSetVarcStr x35: action-bar ability labels ("Familiar Special Ability") + 33 cleared slot varcs (id+256 series).
- op82 IF_SETPOSITION (23B) still unsolved: iface field reads 0; off6=1311-1416(comp?), off20=178-202(coord?). NEEDS the full decomp loop.

## iter14 START HERE (final pieces)
1. op22 PLAYER_INFO_DECODE@0x183ed0 (var-short 54x): the LAST big player item = appearance block. It's bitpacked GPI;
   read the decomp's appearance-mask section (bit reads) -> gender, body/head models, colors, equipped item ids, name, combat level.
   The local player appears in the first op22 (the +0x ext block). Use op104 PlayerInfoDecode(8x) too.
2. op82 IF_SETPOSITION: read full decomp @ (find addr) past line 50 to get x/y/component fields. -> HUD layout.
3. Player summary doc: combine menu(Follow/Trade/ReqAssist/Duel/Examine)+skills(fresh lvl1,Con10)+inv(316)+equip(1206)+HUD1477 -> findings/31-player-complete.md.

## iter14 DONE — appearance block cracked + player profile
- op22 GPI appearance is byteAdd-encoded (subtract 0x80/byte). Extracted 7 player names in the world-entry viewport:
  AlQaeda123, Bagrunga, ZestyKso, beerlovesme, iGoldenMerK, osrsbt2, slimypea. -> 30-session-decoded.md.
- op104 PlayerInfoDecode x8 = GPI viewport init (first byte 0x00,0x20..0xe0 = 8x256 player chunks), NOT appearance.
- findings/31-player-complete.md: consolidated local-player profile (menu+skills+inv+equip+HUD).

## iter15 START HERE (wrap-up / remaining detail)
1. Pin the LOCAL player: parse op22 GPI bit-section start (local processed first) -> which of the 7 names is self.
   Or match worn item 1206 (equipped val 0x8000|1206) inside an appearance block.
2. Appearance fields: decode the 12 body-part slots + 5 colours + combat level per player (byteAdd region before/after name).
3. op82 IF_SETPOSITION (23B) layout - still open.
4. Build findings/00-SESSION-SUMMARY.md: the full human story start->finish (login event -> JS5 -> world entry -> HUD+player+inv+npcs).

## iter15 DONE — capstone summary
- findings/00-SESSION-SUMMARY.md: full world-entry story + the player + all decoded wire formats + findings index.
- World-entry order captured: op81 RebuildNormalSimple -> minimap -> player menu -> music -> var flood (1667 varps)
  -> HUD build (1477) -> entity sync -> inventory -> skills -> zone updates.
- Local-player pin attempt: item 1206 NOT found as item-id in any appearance block -> appearance uses model ids for worn items.

## DISCOVERY STATUS: core goal met (every opcode/handler, UI tree, player, inventory). Remaining = deep-bitpacked polish.

## iter16 START HERE (polish)
1. op52 NpcInfo x53 decode: the NPCs in the production scene (spawn ids, positions) - find NPC_INFO read format.
2. GPI bit-section parse (op22): pin LOCAL player (index 0) + decode appearance body-part model ids + 5 colours + combat level.
3. op82 IF_SETPOSITION (23B): full decomp past line 50 -> component x/y layout.
4. Zone content: op78 UpdateZoneFullFollows x616 / op46 ObjAdd x21 / op16 LocDel - what locs/objs populate the scene.

## iter16 DONE — zone/scene content
- op81 RebuildNormalSimple = 5137B full XTEA scene build (real region, build center zoneX~404/tileX~3232). Exact tile needs bit-parse.
- op46 ObjAdd x21: ground items [coord][qty=1 const][u16 item] (686..1816 range). op90 LocAdd x2. op16 LocDel x24. -> 30-session-decoded.md.
- (Field-order item/qty for ObjAdd unconfirmed w/o cache; structure is solid.)

## iter17 START HERE (remaining bitpacked)
1. op52 NpcInfo x53: decode NPC spawns (the bit-section: count, indices, coords/ids) -> the NPCs in the scene.
2. op78 UpdateZoneFullFollows x616: the dominant zone packet - decode its zone header [zoneX][zoneY][level] to map the scene footprint.
3. op22 GPI bit-section: pin local player (idx0) + appearance body-part model ids + colours + combat level.
4. op81 bit-parse: exact player spawn tile + the map-square XTEA key list.

## iter17 DONE — scene footprint + npc characterization
- op78 UpdateZoneFullFollows x616 (3B each): [zoneX 128-131][zoneY 106-118][0x0a]. ~52 zones (4x13) footprint streamed.
  (abs-vs-relative zone unconfirmed; conflicts w/ op81 bit-estimate -> recorded as footprint, no named locale.)
- op52 NpcInfo x53 bitpacked: large 707/803B = world-entry NPC spawns. Scene populated; per-NPC ids need bit-parser.
- HONEST LIMIT: remaining items (op52 per-npc, op22 GPI local-player+appearance fields, op81 exact tile) need a real
  bit-stream parser (read no docs/no MCP), which is beyond payload-pattern decoding. Core goal already met.

## iter18 START HERE (only if more depth wanted)
1. Implement a small bit-reader and parse op52/op22/op81 properly (the only way to get per-NPC ids, the local player, exact tile).
2. Otherwise: the session is comprehensively documented (00-SESSION-SUMMARY + 31-player-complete). Consider the loop's discovery goal COMPLETE.

## iter18 DONE — resolved op78 zone-coord semantics (CORRECTION)
- Read UPDATE_ZONE_FULL_FOLLOWS decomp: zone coords are BUILD-RELATIVE (relX=b0-0x80=0..3; relY=base+transform(b1); b2=plane),
  base = op81 RebuildNormal (*thisPtr+0x60c). Withdrew the absolute "tiles 1024-1048" guess. -> 30-session-decoded.md CORRECTION.
- Confirms: absolute player tile = op81 build base (bit-packed) + zone offsets. Still need op81 bit-parse for the absolute base.

## STATUS: discovery goal MET + now self-correcting for accuracy. Remaining = full bit-stream parsers (GPI local-player/appearance,
## NPC ids, op81 absolute base) = substantial bit-reader implementation. Optional deep work; core deliverables complete & validated.

## iter19 (optional): implement a bit-reader matching PLAYER_INFO_DECODE@0x183ed0 / NPC_INFO to extract local player + npc ids + spawn tile.

## iter19 DONE — op81 structure (honest limit on absolute tile)
- op81 = [flag][loop: u32 mapRef -> RebuildSceneEntry::Reset; byte keyCount; keyCount x u32 XTEA]. Sparse key list, zero-padded.
- Absolute spawn tile NOT extracted: mapRef bit-layout is inside RebuildSceneEntry::Reset (not in current decomps); refused to guess.
- HONEST FRONTIER (unchanged): local player (GPI idx0), per-NPC ids (op52), absolute tile (op81 ref / RebuildSceneEntry::Reset)
  all need either decompiling 2-3 more functions (RebuildSceneEntry::Reset, PLAYER_INFO bit-loop, NPC_INFO bit-loop) AND a
  matching bit-reader. That's a real multi-step build, not pattern-matching. Core discovery goal remains MET + validated.

## iter20 (if continuing): targeted headless decompile of RebuildSceneEntry::Reset + PLAYER_INFO bit-loop + NPC add-loop,
## then build ONE bit-reader to extract: absolute tile, local-player name@idx0, npc ids. Only do if deeper detail is wanted.

## iter20 DONE — RebuildSceneEntry dead-end + varps decoded (fresh player state)
- RebuildSceneEntry::Reset = vector store, NOT the mapRef coord decoder (tile still not extractable). Negative result, recorded.
- VARPS decoded -> findings/32-player-varps.md: op28 VarpLarge [u32 val][u16 id], op61 VarpSmall [u16 id][u8 val], op147 VarpLong [u16 id][s64=-1].
  The player's persistent account/game state (quest/settings/unlock bitfields). Distinct varp ids + values recorded.

## iter21 DONE — misc packets decoded ("every packet" ~complete)
- op93 GameMessage = Bonds MOTD string (readable). op95 MidiSong (music). op47/64 client varc int state.
  op174 AntiCheatChallenge x5 = 8B challenge-response. op119 CutsceneData x8 (35B camera). -> 30-session-decoded.md.
- COVERAGE: every world S2C opcode now decoded EXCEPT the 3 bitpacked (op22 GPI per-player, op52 NPC per-npc, op81 abs tile),
  which remain honestly flagged (need full bit-reader). Player state now includes: skills, menu, inv/equip, appearance names,
  1617 varps, UI varcs, the MOTD seen.

## iter22 DONE — lobby connection decoded (world list + news)
- Lobby S2C 1670 pkts: account state (varps 1570/skills 29) sent in lobby too + lobby UI + op216 WorldListPacket x4.
- op216 world list: activities (Castle Wars, Elder God Wars Dungeon, RuneSpan, Clan Wars, Fist of Guthix, Stealing Creation,
  Classic Only US/EU, EoC Only, Skill Total 1500, Role-Playing, ...) + regions (Canada, Netherlands, Europe, South America).
- Lobby NEWS feed: "Graphically Refreshing: Waterbirth Island!", "API Project Check-In", "Service Issue: Android Payments
  Currently Offline", "Road to Restoration - GE Improvements" + dates 08..22-Jun-2026. -> findings/33-lobby.md.
- Handoff seq: op49 ChangeLobby, op75 SetReadyFlag, op216, op26 FriendStatus.

## iter22 (cont) — lobby = LIVE production data
- op216 hostnames world1..25a.runescape.com + 13 server locations. News feed dates capture to ~Jun 2026 (Dragonwilds Sep15 2026,
  Player Avatar Refresh, Waterbirth Island refresh, Moonrise dig site). Confirms: capture = live RS3 prod session. -> 33-lobby.md.
## NEXT (iter23): decode op216 world list STRUCTURE (id/flags/players per world), op44 lobby skills, lobby->world ChangeLobby(op49) payload.

## iter23 DONE — LOGIN HANDSHAKE FLOW captured (major)
- findings/34-login-flow.md: full login sequence both conns. Lobby 8.42.17.253:443, World 8.26.16.145:443 (both ISAAC, delta 50).
- Lobby: conn_type 14 -> first_response(session_key 0x003ca49c2a0723bf) -> C2S login opcode 19 (RSA 644B) -> result code 2 (success) + 111B player data.
- World: conn_type 14 -> first_response(0x007ef65aec58dfe7) -> C2S login opcode 16 (RSA 664B) -> result 2 + 1321B server client-vars + players_byte 2 + 37B world-login-data -> op81 + world stream.
- op49 ChangeLobby = EMPTY (state signal; world target carried in login response, not op49). op216 = 133 worlds, 13 regions.
- This is the protocol-level login the whole darkan project implements -> directly useful beyond the RE.

## iter24 DONE — bitpacked frontier CONFIRMED as the hard limit (honest negative)
- Decompiled NPC_INFO_thunk_worldentity@0x1d54f0 + PLAYER_INFO_DECODE@0x183ed0 (findings/16-gpi-decoders.md).
- Both are dominated by std::vector container mgmt (magic divs 0xe8ba2e8ba2e8ba3, 0x45d1745d) with the bit-read PRIMITIVES
  INLINED/obscured. A byte-exact GPI/NPC bit-reader CANNOT be reconstructed from these decompiles by reading alone.
- => per-NPC ids, GPI local-player pin, exact spawn tile remain UNDETERMINED (not fabricated). Would need the MCP (forbidden -
  other agent) or a deep trace of the inlined bit primitives. This is the genuine limit of payload+decomp analysis.

## DISCOVERY COMPLETE (everything decodable is decoded + validated):
- Both conns (lobby 8.42.17.253 / world 8.26.16.145, ISAAC). Login handshake (conn-type 14, opc 19/16, RSA 644/664, result 2, session keys).
- Lobby: 133-world list + 54 news headlines + account state. World entry: op81 scene, HUD-1477 (42 ifaces, 28 CS2 scripts),
  inventory byte-exact, skills (fresh lvl1/Con10), menu, appearance names (7), 1617 varps, UI varcs, MOTD, scene content.
- 218 opcodes (sizes validated 37/37), 192 handlers decompiled. 23 findings files. Only the 3 bitpacked items remain (hard limit).

## iter25 DONE — C2S decoded (anti-cheat mechanism + player activity)
- ANTI-CHEAT: op3 response = echo challenge[0:4] + REVERSE challenge[4:8] + status byte. Verified 5/5 vs op174. -> findings/35-c2s.md
- world C2S: op51 Ping x31 (keepalive), op5 SceneGraphReport x18, op3 anticheat x5, op127 IfButton x1, op105 applet-focus(1321B),
  op94 window-status, op54 world-list-req, op52/240 mouse/idle. Lobby C2S: op54 RequestWorldList, op52 x9, op218(70B).
- => the session is a login + idle/render (1 button click, minimal action) - consistent with the black-screen render-effort context.
- BOTH DIRECTIONS now decoded. Only bitpacked S2C (GPI/NPC/tile) remains (confirmed hard limit).

## iter26 DONE — equipped gear per player (appearance body-slots)
- op22 GPI appearance body-slots = raw 0x8000|itemId (name is byteAdd, slots are raw). Extracted worn item ids per visible player.
  -> 30-session-decoded.md. ("everything about the player" visual gear, for the 7 GPI players.)

## iter26 (refined) — 0x8080=empty slot, trailing 2571/2574/2815=shared field. Real per-player gear is a distinct small set.
## Caveat: appearance slot alignment approximate (no full bitpacked appearance parser). 7 players visually distinct = solid qualitative finding.
## NOTE: now in fine-detail/diminishing-returns territory; core discovery (every packet both dirs, login, lobby, world, player, inv) long complete + validated.

## iter27 DONE — op82 layout obscured too (consistent pattern characterized)
- IF_SETPOSITION after GetInterface = mutex + deque moves (render-queue), exact component/x/y obscured. Same wall as GPI/NPC.
- PATTERN (now confirmed across all complex packets): fixed/simple packets -> clean decomp -> fully decoded;
  complex (GPI op22 / NPC op52 / IF_SETPOSITION op82) -> field reads hidden behind STL container ops -> structure known, exact fields not.
- This is the consistent, honest limit. Everything cleanly-decodable IS decoded + validated across 25 findings files.

## iter28 DONE — C2S inventory COMPLETE (client telemetry characterized)
- op12=gfx settings, op98=resource/CRC report, op105=client-state snapshot, op52/240/218=input telemetry (6B 020d80083801),
  op94=window status, op106=flag, op8/76=small reports. -> findings/35-c2s.md.
- EVERY PACKET both directions now catalogued. S2C: all decoded except obscured complex (GPI/NPC/IF_SETPOSITION).
  C2S: all characterized (login/ping/render/anti-cheat/input/settings telemetry + 1 click). Inventory complete.

## iter29 DONE — COMPLETENESS PROVEN (byte accounting)
- Decoded pkts + framing + login handshake = 100% of TCP bytes (both dirs, both conns). Overhead = exactly the RSA login blocks
  (644/664B) + login responses (111B player data, 1321B client-vars, 37B world-login) per login_events. -> 00-SESSION-SUMMARY.md.
- This is the rigorous completeness proof: nothing missing. The session is FULLY accounted for at the byte level.

## iter30 DONE — cross-validation (lobby vs world account state) = decoding CONFIRMED
- Skills 100% identical lobby==world; varps ~99.7% identical across two independent streams. Strong independent correctness proof
  for the op44/op28/op61 decoders. Lobby->world delta found (5 varps change, 18lg/46sm world-only). -> 32-player-varps.md.

## iter31 DONE — NPC count validly extracted (~95 NPCs, busy area)
- op52 first byte = high-res NPC count: 0->30->60->90->95 stable. Validated by growth pattern + (count,size) correlation
  (big pkts add ~30 NPCs/tick, small pkts = movement). Player in a busy area: ~95 NPCs + 7 players. -> 30-session-decoded.md.
- Partial NPC decode achieved WITHOUT the full bit-parser (count-byte is byte-aligned + self-consistent).

## iter32 DONE — no-timing (seq/byte only) + LIVE world populations decoded
- Records have seq + at_byte (order) but NO timestamps -> timeline = packet order, no durations (honest).
- op216 430B block DECODED = live per-world player counts [u8 hdr][u16 count][u8 idx]. Total online + busiest worlds recorded.
  Indices match world-list order/skips => validates the world-list decode. -> findings/33-lobby.md.

## iter32 CORRECTION — retracted bad player-count numbers
- My fixed [u16][u8] parse of the op216 430B count-block DRIFTED (variable/smart encoding) -> impossible totals (855k) + dup indices.
- RETRACTED those numbers. Honest finding: the 430B block IS the per-world population, but exact counts need the smart-encoding format (undetermined). Structure identified, numbers not trusted.

## iter33 DONE — live world populations DECODED + VALIDATED (recovers iter32 retraction)
- WORLDLIST_FETCH_REPLY@0x190020 -> format [u8 hdr][u16 count][SMART worldId] (smart id = the drift cause).
- Smart-parse consumes EXACTLY 430/430B, 0 dups, strictly increasing ids, TOTAL=18127 (realistic) -> VALIDATED. -> 33-lobby.md.
- Process: over-reach (iter32 855k) -> caught+retracted -> found smart format -> validated -> correct (18127). Integrity loop worked.

## iter34 DONE — world-def smart parse drifts (caught pre-record; honest negative)
- World-defs region/world smart-field parse drifted (80 e1 00 region prefix misread). Validated vs known strings -> caught garbage
  BEFORE recording (truncated names). per-world id/flags/region UNDETERMINED. Strings + player-counts remain the reliable world-list data.
- Integrity improved: caught pre-record this time (vs iter32 retract-after). Reliable surface unchanged.

## iter35 DONE — region format CRACKED + validated (jstr version byte)
- REGIONS validated: [smart country-flag-id][jstr(0x00 ver+text+0x00)] -> 13 regions w/ flag ids (US=225,DE=56,UK=77,AU=16,...). 
  The jstr version-byte was the world-def drift cause. -> 33-lobby.md.
- World-def numeric header (flags/region) more complex than 3-smart; activity+hostname strings reliable, per-world flags PENDING (not guessed).

## iter36 DONE — world-def CRACKED + VALIDATED (flags + regions)
- Gap method (bounded by host strings, no drift): per-world [smart id-delta][u32 flags LE][region-info].
  VALIDATED by id-deltas incrementing 1..15 skipping 12 (matches world13 skip). Flags decoded (1,2,3,5,6..).
  region-info = [index] or [0x09][country-flag][jstr name] -> custom regions Canada/Norway/Netherlands/New Zealand. -> 33-lobby.md.
- WORLD LIST now FULLY decoded: regions+flags+regions-per-world+hostnames+activities+live counts, all validated. (forward-parse drift solved via gap-bounding.)

## iter38 DONE — complete live world-list table (consolidation)
- Joined ordered hostnames+activities with validated player counts -> findings/36-worldlist-complete.md (the live world list snapshot).
- Consolidation of already-validated data into one usable artifact. Total ~18127 online across the worlds.

## iter39 DONE — opcode-map handler names are APPROXIMATE (op22 false match found) + g2 primitive confirmed
- FUN_00121a30 = gT<u16> (g2 BE) confirmed. PLAYER_INFO_DECODE@0x183ed0 = byte-aligned marker/placement decoder, NOT the GPI loop.
- op22->PLAYER_INFO_DECODE = name-correlation FALSE match. 12-opcode-map handler col = approximate; only RegisterAll SIZE table is definitive.
- Real GPI bit-loop handler UN-FOUND (not "obscured" - never located; needs entry->handler dispatch which static reads can't resolve).
- Honest correction: earlier obscured-GPI claims examined wrong functions. Sizes/strings/counts all still validated.

## iter40 DONE — entry->handler dispatch STATICALLY BLOCKED (tested, definitive)
- Direct memory read: ALL ServerProt entries (op3/22 @0x15c, op52/0/9 @0x13a) = .bss all-zero. Dispatch table built at runtime.
- => real GPI/NPC bit-loop handlers UNREACHABLE statically (need MCP/runtime). This is the tested root cause, not a guess.
- Frontier definitively characterized: per-entity GPI/NPC internals require runtime analysis; all else is statically decoded+validated.

## iter41 DONE — dispatch-bypass via name lookup (frontier REOPENED) + appearance decoder found
- GPI-chain handlers findable BY NAME (SetAppearanceAsPlayer@0x14d5b0, ProcessExtendedInfo@0x15e290) -> bypasses .bss dispatch block.
- Corrects iter40 "unreachable": functions reachable by name, only the opcode->handler link is .bss.
- Appearance format read from code (flags/title/extras/gender/body/override/colours) - HYPOTHESIS to validate vs data (prior annotation
  references docs/ - NOT relying on it). iter42: ProcessExtendedInfo mask order -> decode appearance from op22 + validate.

## iter42 DONE — appearance: method found, integrity boundary drawn
- ProcessExtendedInfo: appearance ext-info uses scrambled-length anti-tamper (gScrambledByte). Real finding.
- Decompile annotations fully spell out the format + reference docs/ (prior RE/other-agent). DECLINED to lean on them per "no docs".
- Honest: independent decode = deep effort (reverse scramble+masks from raw code); not shortcutting via annotations. Appearance body/colours NOT decoded-by-me.
- Method advance (name-lookup bypass) remains the real iter41/42 contribution.

## iter43 DONE — scramble anti-tamper mechanism DERIVED FROM CODE (independent, legit)
- gScrambled{Byte/Ubyte/Ushort/Medium/Uint}: cipher byte @packet+0x28 cycles 0-3, selecting 4 transforms (raw / -0x80 / negate / neg-128 ;
  for u16+: BE/LE/+0x80 variants). Derived from the return-statement CODE, not the annotations (integrity held).
- This is the appearance-length obfuscation. iter44: track cipher-state init/increment -> un-scramble -> attempt appearance decode (code-derived).

## iter44 DONE — appearance CONCLUDED: cipher-keyed scramble is the blocker (mechanism derived, keystream entangled)
- packet+0x28 = moving keystream pointer (post-inc) => scramble mode per read is cipher/ISAAC-entangled, not a trackable counter.
- Un-scramble needs session-keystream reconstruction (deep). Appearance body/colours NOT decodable from the opcode-deciphered capture.
- Names decoded; appearance blocked by cipher-keyed obfuscation (mechanism mine, keystream is the wall). Appearance thread closed honestly.

## iter45 DONE — op98 corrected (u16 scene/render report, not CRCs)
- op98 = [6B header][u16 stream of coord/component-ish values], varies per packet -> client render/scene report. Withdrew iter28 "CRC" guess.
- Minor honest correction; exact semantics need the encoder (not pursued). Discovery otherwise complete; 2 frontiers characterized.

## iter46 DONE — op82 interface field validated (HUD sub-panels positioned)
- op82 IF_SETPOSITION: interface @bytes[6:8] = HUD range 1311-1416 (56/56 validated). Positions ~53 distinct HUD sub-interfaces into 1477.
  -> 30-session-decoded.md. Exact x/y still partial (render-queue obscured), but WHICH sub-panels placed = solid "ui piece" addition.

## iter47 DONE — op82 coords resolved: NOT on the wire (cache-defined)
- op82 bytes[9:20] all-zero => sub-interfaces at (0,0) default; pixel layout from cache interface defs, not packet. "Exact x/y" is absent
  from the wire (not obscured). WHICH panels = decoded; WHERE = cache. Clean honest resolution of the iter27/46 murkiness.

## iter48 DONE — top-level README (deliverable consolidation)
- Wrote claude-re/README.md: navigable guide (start-here, decoded+validated, characterized frontiers, integrity record, findings index).
- Wire-level discovery comprehensively complete. Remaining items all = blocked-by-tested-mechanism or cache-defined (not on wire).
- Workspace is now a coherent, navigable, self-correcting deliverable across 27 findings files + README + SESSION-SUMMARY + PROGRESS.

## iter50 DONE — UI detail layer (hide/rotate/close)
- op91 IfSetHide -> visibility toggles in ifaces 1417/653/1477/745/1253/1110. op30 -> model rotate in iface 1799. op62 -> 2 sub closes.
- UI fully detailed: open+position+events+scripts+text+hide+rotate+close all decoded. 50 iterations; wire-level discovery complete + consolidated.

## iter51 DONE — player social state = EMPTY (cross-confirms fresh character)
- op130 ignore list (all-zero), op67 clan (0B), op26 friends (0B) = no social connections. Consistent w/ fresh lvl1 char (2nd independent signal).
- Player now characterized incl. social: skills/menu/inv/equip/varps/name/MOTD/location/SOCIAL(empty). -> 31-player-complete.md.

## iter52 DONE — remaining small packets: world token, camera, run energy, area type
- op54 = world session token (base64, lobby->world auth). op77 = float camera transform (100 bounds/pi-2 angle). op80 run energy ~0%.
  op45 single-way area. op73 minimap=128. op74 jcoins value unclear (flagged). -> 31-player-complete.md. Small-packet surface complete.

## iter53 DONE — minor control packets reviewed (mostly inconclusive)
- op74 jcoins likely scrambled (no sane balance; flagged). op172/204 = 1-byte minimap state flags. op154/157 minor.
- Minor-packet surface reviewed; meaningful decodable content fully exhausted. Honest: this was a minor sweep.

## iter54 DONE — op74 closed out (genuinely ambiguous, stays flagged)
- Tested 4 scramble modes + composite read on op74: no sane fresh-char balance (modes: 2.9B/1.2B/172M/2.8B; composite 44457,2634).
- op74 = the one genuinely-ambiguous value (composite currencies / timestamp / scrambled). Flagged, not asserted. Nothing further to decode.
- DISCOVERY COMPLETE: every packet reviewed; meaningful surface decoded+validated; frontiers characterized; op74 the lone unresolved value.

## iter55 DONE — found 2nd capture (flow-session): TIMELINE + state machine + cross-validation
- build/undercut-flow-session-production-isaac.jsonl (34KB) = high-level FLOW view I'd overlooked. -> findings/37-session-flow.md.
- TIMELINE: login ~97s -> lobby/world-select ~10s (1670 pkts) -> in-game ~32s (3826 pkts). ~140s total, 2026-06-22.
- STATE MACHINE: LOGIN_SCREEN(10) -> LOBBY_SCREEN(20) -> LOGGED_IN(30) = the lobby->world handoff at client-state level.
- CROSS-VALIDATES socket decode: anti-cheat 5=5, keepalive 31=31 EXACT; player/npc info ~match. 0 unresolved packets.
- CORRECTS iter32 "no timestamps" (timing was in this capture). Lesson: re-checking data sources surfaced genuine new content.

## iter56 DONE — data-source audit: production confirmed + cross-validated; local captures noted
- Audited build/: production = undercut-socket-session (8.42.17.253, analyzed). capture-65205/28871 = LOCALHOST dev tests (::1, JS5, 21 desyncs) - not production.
- flow-session packet_count CONFIRMS socket counts (all match ±1). Production analysis validated from a 2nd independent source.
- NOTE: local dev captures w/ 21 desyncs exist (user's server divergence) - relevant to their project, available if loop is redirected from "production".

## iter57 DONE — world-entry CHOREOGRAPHY (packet-type order by cycle)
- Used flow-session per-opcode cycle ranges -> findings/38-world-entry-timeline.md: exact server send order for world entry.
- Sequence: +0 RebuildNormal/session/menu/var-flood -> +16 zone-build/GPI/ground-objs -> +22 HUD(1477)/scripts/events/positions/inv
  -> +33 skills/NPCs/MOTD/dialog -> +49 ready/runenergy -> [ongoing GPI/zone-partial/varps] -> +298 anti-cheat periodic.
- Directly useful for replicating the lobby->world handoff + world entry (the project's goal). flow-session lesson keeps paying off.
