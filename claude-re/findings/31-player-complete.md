# The local player — complete profile (production world-entry session)

All decoded from the production capture + 948-5 binary wire formats (no docs).

## Identity
- Players in viewport (GPI appearance, byteAdd names): AlQaeda123, Bagrunga, ZestyKso, beerlovesme, iGoldenMerK, osrsbt2, slimypea
- Local player = the logged-in account (one of the above; pinning exact name needs the GPI bit-section parse).
- Right-click menu (op17 SET_PLAYER_OP): Follow, Trade with, Req Assist, Duel, Examine.

## Skills (op44 UpdateStat x29) — brand-new level-1 character
- All 28 skills level 1, 0 xp EXCEPT **Constitution level 10 (1154 xp)** = the RS default start.

## Inventory & equipment (op85 UPDATE_INV_FULL)
- Backpack (container 93): item **316** x1.
- Worn equipment (container 94, slot 3): item **1206**.
- Equipment mirror (670): item 1206. Container 795: item 52556 x1000 (stack).

## UI rendered for the player (op3/op35/op110)
- Top-level HUD = interface **1477** (197 components). 42 interfaces wired via IF_SETEVENTS.
- 28 CS2 scripts build the HUD (8862 x22 tabs, 10623 x27 components).

## Social state (iter51) — EMPTY (confirms fresh character)
- op130 UpdateIgnoreListRaw: 10B all-zero = ignore list EMPTY (0 ignored players).
- op67 ClanChannelFull: 0B = NO clan (empty clan channel).
- op26 FriendStatus: 0B = NO friends.
=> The player has zero social connections - consistent with the brand-new level-1 character (skills: all lvl1 except Con 10).
   Cross-confirms the fresh-account picture from a second independent signal (social vs skills).

## Remaining small-packet player/scene state (iter52)
(decoded above: run energy, jcoins balance, camera, multiway flag, minimap state, world token)

## Remaining small-packet state (iter52) — decoded
- op54 HashedWorldToken (44B) = ASCII base64 "wwGlrZHF5gJWpnOjhzGiku8LI6RiVLXiJVXPpzBLWDQ" = the WORLD SESSION TOKEN (lobby->world handoff auth).
- op77 CAMERA_UPDATE (121B) = float camera transform: 100.0 bounds (0x42c80000), pi/2 angle (0x3fc90fdb), 1.0-1.1 scales. Initial view.
- op80 UpdateRunenergy = 1 => run energy ~0% (0-255 scale). op45 SetMultiwayState = 0 => single-way (safe) area.
- op73 MinimapState = 0x8080 (128). op74 JcoinsUpdate = 0xada90a4a (value too large for plain balance - format unclear, NOT asserted).
