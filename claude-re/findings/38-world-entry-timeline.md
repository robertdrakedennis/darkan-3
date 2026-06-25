# World-entry choreography — packet-type order by cycle (from flow-session cycle ranges)

Cycles relative to LOGGED_IN (state 30) @ absolute cycle 14927. This is the ORDER the server sends the world-entry stream
(directly useful for replicating the lobby->world handoff + world entry). first..last cycle = the active window of each opcode.

## +0 — scene + session setup (one burst at login)
RebuildNormalSimple (scene build), HashedWorldToken (session auth), MinimapState/MinimapFlagA/B, MidiSong, JcoinsUpdate,
ResetClientVarcache, SetPlayerOp (player menu x6). VarpSmall(1176) + VarpLarge(491) var-flood BEGINS here (runs 0..1498).

## +15..16 — zone build + entity spawn
VarpLong(5)@15. Then @16: UpdateZoneFullFollows(616, tight window 16..22 = the zone tiles), PlayerInfo GPI begins (16..1618),
ObjAdd(21 ground items, 16..20), CAMERA_UPDATE, DestroyZoneData, UpdateIgnoreListRaw, SetNpcOp. @17: LocDel(24)+LocAdd(2) scene objs.

## +22..26 — HUD build + wiring
@22: RunClientScript(86 CS2 scripts, 22..1498), IfSetTopLevelInterface(1477 HUD root), UPDATE_INV_FULL(8 inventory, 22..33).
@23: IfSetEvents(871, 23..298 = event wiring), IfSetPosition(56 sub-interfaces, 23..115), ClientSetVarcSmall(61)/Str(35), IfSetText(7).
@24..26: IfSetHide(11), IfCloseSub(2), IfSet2DAngle(2), CAM_SMOOTHRESET, SceneFlag.

## +33..34 — skills + NPCs + dialog + state
@33: UpdateStat(29 = the skills), NpcInfo GPI begins (33..1618), GameMessage(MOTD), UPDATE_INV_PARTIAL, EntityAnimAtTile,
TRIGGER_ONDIALOGABORT(55, 33..1618), ClearPendingUpdates, NpcInfoThunk.
@34: PlayerInfoDecode(8 viewport init), CutsceneData(8), ResetEntityLists, SetPlayerOp2/3, SetMultiwayState, ClanChannelFull, RebuildRegion.

## +49+ — finalize + ongoing
@49: UpdateZonePartialEnclosed(66, ongoing 49..1618), ChangeLobby, SetReadyFlag, UpdateRunenergy, WorldListPacket.
@88: FriendStatus. @298: AntiCheatChallenge(5, periodic 298..1498).

## Summary sequence (server send order)
RebuildNormal -> session/minimap/music/menu -> var-flood -> zone-build -> player/npc GPI + ground objs -> HUD(1477)+scripts+events+positions+inv
-> skills -> MOTD/dialog -> ready/runenergy -> [ongoing: GPI, zone-partial, varps, anti-cheat]. ~140s session (97s login, 10s lobby, 32s in-game).
