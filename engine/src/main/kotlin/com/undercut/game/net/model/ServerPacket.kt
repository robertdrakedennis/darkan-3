package com.undercut.game.net.model

/**
 * Sealed interface covering all 217 server-to-client packet types.
 * Each ServerProt opcode maps to one data class here.
 */
sealed interface ServerPacket {

    // ── Chat ──────────────────────────────────────────────────────────────
    data class MessageGame(val type: Int, val sender: String, val message: String) : ServerPacket
    data class MessagePublic(val senderSize: Int, val payloadSize: Int) : ServerPacket
    data class MessagePrivate(val payloadSize: Int) : ServerPacket
    data class MessagePrivateSystem(val payloadSize: Int) : ServerPacket
    data class MessagePrivateEcho(val payloadSize: Int) : ServerPacket
    data class MessageFriendchat(val payloadSize: Int) : ServerPacket
    data class MessageFriendchannel(val payloadSize: Int) : ServerPacket
    data class MessageClanchannel(val payloadSize: Int) : ServerPacket
    data class MessageQuickchatClanchat(val payloadSize: Int) : ServerPacket
    data class MessageQuickchatClanchannel(val payloadSize: Int) : ServerPacket
    data class MessageQuickchatFriendchat(val payloadSize: Int) : ServerPacket
    data class MessageQuickchatPrivate(val payloadSize: Int) : ServerPacket
    data class RunClientscript(val payloadSize: Int) : ServerPacket
    data class ChatFilterSettings(val payloadSize: Int) : ServerPacket
    data class SetChatFilterA(val value: Int) : ServerPacket
    data class SetChatFilterB(val value: Int) : ServerPacket
    // SetChatFilterC/D removed: op 164→SET_CHAT_FILTER_A, op 133→CREATE_CHECK_EMAIL_REPLY
    data class CreateCheckEmailReply(val status: Int) : ServerPacket
    data class CreateCheckNameReply(val status: Int) : ServerPacket
    data class ClansettingsDeltaChat(val payloadSize: Int) : ServerPacket
    data class FriendchatJoin(val payloadSize: Int) : ServerPacket
    data class ClanchannelFullChat(val payloadSize: Int) : ServerPacket

    // ── Variables ─────────────────────────────────────────────────────────
    data class SetVarcInt(val varId: Int, val value: Int) : ServerPacket
    data class SetVarcSmall(val varId: Int, val value: Int) : ServerPacket
    data class SetVarcCoord(val varId: Int, val coordX: Int, val coordY: Int) : ServerPacket
    data class SetVarcStrSmall(val varId: Int, val value: String) : ServerPacket
    data class ResetVarcSmall(val payloadSize: Int) : ServerPacket
    data class ResetVarcSmall2(val payloadSize: Int) : ServerPacket
    data class ResetVarcInt(val payloadSize: Int) : ServerPacket
    data class SetVarcCoord2(val payloadSize: Int) : ServerPacket
    data class SetVarpSmall(val varId: Int, val value: Int) : ServerPacket
    data class SetVarpInt(val varId: Int, val value: Int) : ServerPacket
    data class SetVarpLong(val varId: Int, val value: Long) : ServerPacket
    data class SetVarbitSmall(val varId: Int, val value: Int) : ServerPacket
    data class SetVarbitInt(val varId: Int, val value: Int) : ServerPacket
    data class SetVarcInt2(val varId: Int, val value: Int) : ServerPacket
    data class SetVarcSmall2(val varId: Int, val value: Int) : ServerPacket
    data class SetVarbitInt2(val varId: Int, val value: Int) : ServerPacket
    data class SetVarbitSmall2(val varId: Int, val value: Int) : ServerPacket
    data class UpdateStat(val statId: Int, val experience: Int, val currentLevel: Int) : ServerPacket
    data class ResetAllVarps(val dummy: Unit = Unit) : ServerPacket

    // ── Interfaces ────────────────────────────────────────────────────────
    data class IfOpenTop(val interfaceId: Int) : ServerPacket
    data class IfOpenSub(val componentHash: Int, val type: Int) : ServerPacket
    data class IfOpenSubActive(val payloadSize: Int) : ServerPacket
    data class IfCloseSub(val componentHash: Int, val type: Int) : ServerPacket
    data class IfMoveSub(val srcHash: Int, val dstHash: Int) : ServerPacket
    data class IfMoveSubActive(val componentHash: Int) : ServerPacket
    data class IfSetText(val componentHash: Int, val textSize: Int) : ServerPacket
    data class IfSetText2(val componentHash: Int, val textSize: Int) : ServerPacket
    data class IfSetHide(val componentHash: Int, val hidden: Boolean) : ServerPacket
    data class IfSetHideActive(val componentHash: Int, val hidden: Boolean) : ServerPacket
    data class IfSetColour(val componentHash: Int, val colour: Int) : ServerPacket
    data class IfSetPosition(val componentHash: Int, val x: Int, val y: Int) : ServerPacket
    data class IfSetModelActive(val componentHash: Int) : ServerPacket
    data class IfSetScrollPos(val componentHash: Int, val scrollPos: Int) : ServerPacket
    data class IfSetAngle(val componentHash: Int) : ServerPacket
    data class IfSetObjectActive(val componentHash: Int) : ServerPacket
    data class IfSetClickMask(val componentHash: Int) : ServerPacket
    data class IfSetTextActive(val componentHash: Int, val flag: Int) : ServerPacket
    data class IfSetTargetParam(val payloadSize: Int) : ServerPacket
    data class IfSetEvents(val componentHash: Int, val fromSlot: Int, val toSlot: Int) : ServerPacket
    data class IfSetObject(val componentHash: Int, val objId: Int) : ServerPacket
    data class IfSetObjectNoNum(val componentHash: Int, val objId: Int) : ServerPacket
    data class IfSetObjectNoNum2(val componentHash: Int) : ServerPacket
    data class IfSetObjectAlwaysNum(val componentHash: Int, val objId: Int) : ServerPacket
    data class IfSetModel(val componentHash: Int) : ServerPacket
    data class IfOpensub2(val payloadSize: Int) : ServerPacket
    data class IfSetModelAnimation(val componentHash: Int, val animId: Int) : ServerPacket
    data class IfSetModelBodyType(val componentHash: Int) : ServerPacket
    data class IfSetModelColour(val componentHash: Int) : ServerPacket
    data class IfSetModelRecolour(val componentHash: Int) : ServerPacket
    data class IfSetGraphic(val componentHash: Int) : ServerPacket
    data class IfSetGraphicActive(val payloadSize: Int) : ServerPacket
    data class IfSetAnim(val componentHash: Int, val animId: Int) : ServerPacket
    data class IfSetTextFont(val componentHash: Int, val fontId: Int) : ServerPacket
    data class IfSetRecol(val componentHash: Int) : ServerPacket
    data class IfSetRecolActive(val componentHash: Int) : ServerPacket
    data class IfSetRetex(val componentHash: Int) : ServerPacket
    data class IfSetPlayerModel(val componentHash: Int) : ServerPacket
    data class IfSetPlayerModelSelf(val componentHash: Int) : ServerPacket
    data class IfSetPlayerModelOther(val payloadSize: Int) : ServerPacket
    data class IfSetPlayerModelAnim(val componentHash: Int, val animId: Int) : ServerPacket
    data class IfSetPlayerModelBaseColour(val componentHash: Int) : ServerPacket
    data class IfSetPlayerModelBodyType(val componentHash: Int) : ServerPacket
    data class IfSetPlayerModelExactmove(val componentHash: Int) : ServerPacket
    data class IfSetNpcModel(val componentHash: Int, val npcId: Int) : ServerPacket
    data class IfSetNpcModelActive(val componentHash: Int) : ServerPacket
    data class IfSetNpcModelAnim(val componentHash: Int, val npcId: Int, val animId: Int) : ServerPacket
    data class IfTriggerClose(val dummy: Unit = Unit) : ServerPacket

    // ── Zone Updates ──────────────────────────────────────────────────────
    data class ObjAdd(val coord: Int, val objId: Int, val count: Int) : ServerPacket
    data class ObjDel(val coord: Int, val objId: Int) : ServerPacket
    data class ObjCount(val coord: Int, val objId: Int, val oldCount: Int, val newCount: Int) : ServerPacket
    data class ObjReveal(val coord: Int, val objId: Int) : ServerPacket
    data class LocAdd(val payloadSize: Int) : ServerPacket
    data class LocAddChange(val coord: Int, val shapeAndRot: Int, val locId: Int) : ServerPacket
    data class LocDel(val coord: Int, val shapeAndRot: Int) : ServerPacket
    data class LocMerge(val coord: Int) : ServerPacket
    data class LocAnimSpecific(val coord: Int) : ServerPacket
    data class LocPrefetch(val locId: Int) : ServerPacket
    data class LocCustomise(val payloadSize: Int) : ServerPacket
    data class MapProjanim(val payloadSize: Int) : ServerPacket
    data class MapProjanimHalt(val payloadSize: Int) : ServerPacket
    data class MapProjanim2(val payloadSize: Int) : ServerPacket
    data class MapAnim(val payloadSize: Int) : ServerPacket
    data class MapAnimSpecific(val payloadSize: Int) : ServerPacket
    data class ProjanimSpecific(val payloadSize: Int) : ServerPacket
    data class ProjanimSpecificHalt(val payloadSize: Int) : ServerPacket
    data class SpotanimSpecific(val payloadSize: Int) : ServerPacket
    data class SoundArea(val payloadSize: Int) : ServerPacket
    data class UpdateZonePartial(val payloadSize: Int) : ServerPacket
    data class UpdateZonePartialFollows(val baseX: Int, val baseY: Int, val level: Int) : ServerPacket
    data class UpdateZoneFullFollows(val baseX: Int, val baseY: Int, val level: Int) : ServerPacket
    data class UpdateZoneFullFollows2(val payloadSize: Int) : ServerPacket
    data class UpdateZoneFullFollows3(val payloadSize: Int) : ServerPacket

    // ── Camera ────────────────────────────────────────────────────────────
    data class CamTarget(val type: Int) : ServerPacket
    data class CamMoveto(val x: Int, val y: Int, val height: Int) : ServerPacket
    data class CamMovetoArc(val speed: Int) : ServerPacket
    data class CamLookat(val x: Int, val y: Int, val height: Int) : ServerPacket
    data class CamLookatArc(val speed: Int) : ServerPacket
    data class CamForceAngle(val type: Int) : ServerPacket
    data class CamShake(val type: Int) : ServerPacket
    data class CamReset(val dummy: Unit = Unit) : ServerPacket
    data class CamSmoothReset(val dummy: Unit = Unit) : ServerPacket
    data class CamUpdate(val payloadSize: Int) : ServerPacket
    data class UnusedNoop(val payloadSize: Int) : ServerPacket

    // ── Audio ─────────────────────────────────────────────────────────────
    data class MidiJingle(val soundId: Int) : ServerPacket
    data class MidiSong(val songId: Int) : ServerPacket
    data class MidiSwap(val fromId: Int) : ServerPacket
    data class MidiStop(val dummy: Unit = Unit) : ServerPacket
    data class SoundStopAll(val dummy: Unit = Unit) : ServerPacket
    data class SoundStop(val soundId: Int) : ServerPacket
    data class SoundMixbussSetLevel(val bus: Int) : ServerPacket
    data class SoundAreaSynth(val soundId: Int) : ServerPacket
    data class SoundAreaSynth2(val soundId: Int) : ServerPacket
    data class SoundGroup(val groupId: Int) : ServerPacket
    data class SoundGroupStop(val groupId: Int) : ServerPacket
    data class SoundGroupSpeed(val groupId: Int) : ServerPacket
    data class SoundModify(val soundId: Int) : ServerPacket
    data class SynthSound(val soundId: Int) : ServerPacket
    data class VorbisPreload(val songId: Int) : ServerPacket
    data class VorbisSong(val songId: Int) : ServerPacket

    // ── Inventory ─────────────────────────────────────────────────────────
    data class UpdateInvPartial(val interfaceHash: Int, val invId: Int, val slotCount: Int) : ServerPacket
    data class UpdateInvFull(val interfaceHash: Int, val invId: Int, val slotCount: Int) : ServerPacket
    data class UpdateInvGroup(val payloadSize: Int) : ServerPacket

    // ── Combat ────────────────────────────────────────────────────────────
    data class Projanim(val payloadSize: Int) : ServerPacket
    data class NpcHitmarksAndHeadbars(val payloadSize: Int) : ServerPacket

    // ── NPC Info ──────────────────────────────────────────────────────────
    data class NpcAnimSpecific(val payloadSize: Int) : ServerPacket
    data class NpcInfoDecode(val payloadSize: Int) : ServerPacket
    data class NpcInfo2(val payloadSize: Int) : ServerPacket
    data class SetNpcOp(val payloadSize: Int) : ServerPacket
    data class NpcHeadiconSpecific(val payloadSize: Int) : ServerPacket
    data class SetNpcUpdateOrigin(val value: Int) : ServerPacket
    data class SetNpcUpdateFlag(val value: Int) : ServerPacket

    // ── Player Info ───────────────────────────────────────────────────────
    data class PlayerInfoDecode(val payloadSize: Int) : ServerPacket
    data class PlayerInfoDecode2(val payloadSize: Int) : ServerPacket
    data class MapFlagSetPlayer(val payloadSize: Int) : ServerPacket
    data class SetPlayerChatEffects(val effects: Int) : ServerPacket
    data class UpdatePlayerChat(val payloadSize: Int) : ServerPacket
    data class RebuildPlayerinfoPositions(val payloadSize: Int) : ServerPacket

    // ── Player Group ──────────────────────────────────────────────────────
    data class SetPlayerGroup(val payloadSize: Int) : ServerPacket
    data class PlayerOp(val payloadSize: Int) : ServerPacket
    data class SetPlayerGroup2(val value: Int) : ServerPacket
    data class SetTriggerVar(val payloadSize: Int) : ServerPacket
    data class UpdatePlayerGroup(val payloadSize: Int) : ServerPacket

    // ── Social ────────────────────────────────────────────────────────────
    data class UpdateIgnorelist(val payloadSize: Int) : ServerPacket
    data class UpdateIgnorelist2(val payloadSize: Int) : ServerPacket
    data class UpdateFriendlist(val payloadSize: Int) : ServerPacket
    data class UpdateFriendlist2(val payloadSize: Int) : ServerPacket
    data class FriendlistLoaded(val payloadSize: Int) : ServerPacket
    data class UpdateFriendchatChannel(val payloadSize: Int) : ServerPacket
    data class FriendchatSysupdate(val timer: Int) : ServerPacket

    // ── Clans ─────────────────────────────────────────────────────────────
    data class ClansettingsFull(val payloadSize: Int) : ServerPacket
    data class ClansettingsFull2(val payloadSize: Int) : ServerPacket
    data class ClansettingsDelta(val payloadSize: Int) : ServerPacket
    data class ClanchannelFull(val payloadSize: Int) : ServerPacket
    data class ClanchannelDelta(val payloadSize: Int) : ServerPacket
    data class ClanchannelDeltaCs(val payloadSize: Int) : ServerPacket

    // ── Client State ──────────────────────────────────────────────────────
    data class SetTickTimer(val ticks: Int) : ServerPacket
    data class SetMapFlag(val x: Int, val y: Int) : ServerPacket
    data class SpotanimSpecific2(val payloadSize: Int) : ServerPacket
    data class MinimapFlagSet(val value: Int) : ServerPacket
    data class SetReadyFlag(val dummy: Unit = Unit) : ServerPacket
    data class ResetClientState(val dummy: Unit = Unit) : ServerPacket
    data class DestroyZoneData(val dummy: Unit = Unit) : ServerPacket
    data class ResetAnims(val payloadSize: Int) : ServerPacket
    data class Runclientscript(val scriptId: Int) : ServerPacket
    data class SetPlayerOp(val slot: Int, val priority: Int) : ServerPacket
    data class TriggerOndialogabort(val componentHash: Int) : ServerPacket
    data class TriggerOndialogabort2(val dummy: Unit = Unit) : ServerPacket
    data class RebuildNormal(val payload: ByteArray) : ServerPacket
    data class RebuildRegion(val payloadSize: Int) : ServerPacket
    data class UpdateRebootTimer(val ticks: Int) : ServerPacket
    data class SetHeatmap(val payloadSize: Int) : ServerPacket
    data class UrlOpen(val urlId: Int) : ServerPacket

    // ── Rebuild ───────────────────────────────────────────────────────────
    data class RebuildNormalHandler(val payloadSize: Int) : ServerPacket
    data class RebuildRegionHandler(val payloadSize: Int) : ServerPacket

    // ── World Data ────────────────────────────────────────────────────────
    data class SetCameraTarget(val targetId: Int) : ServerPacket
    data class SetWorldTarget(val payloadSize: Int) : ServerPacket

    // ── Site Settings ─────────────────────────────────────────────────────
    data class UpdateSiteSettings(val payloadSize: Int) : ServerPacket

    // ── Misc ──────────────────────────────────────────────────────────────
    data class SetUid(val payloadSize: Int) : ServerPacket
    data class Noop(val dummy: Unit = Unit) : ServerPacket
    data class NoopUnhandled(val payloadSize: Int) : ServerPacket
    data class NoopVar(val payloadSize: Int) : ServerPacket
    data class ServerTickEnd(val serverCycle: Int, val clientCycle: Int) : ServerPacket
    data class Logout(val transfer: Boolean) : ServerPacket
    data class ResetEntityLists(val dummy: Unit = Unit) : ServerPacket
    data class SetRunEnergy(val energy: Int) : ServerPacket
    data class SetWeight(val weight: Int) : ServerPacket
    data class SetMultiwayState(val state: Int) : ServerPacket
    data class SetDisplayInt(val value: Int) : ServerPacket
    data class SetSysupdateTimer(val ticks: Int) : ServerPacket
    data class CutsceneData(val payloadSize: Int) : ServerPacket
    data class DetailOptions(val payloadSize: Int) : ServerPacket
    data class RemoveTrackedEntry(val entry: Int) : ServerPacket
    data class RemovePlayerFromList(val index: Int) : ServerPacket
    data class SetPlayerOp2(val slot: Int) : ServerPacket
    data class SetPlayerOp3(val slot: Int) : ServerPacket
    data class SetInteractionFlagB(val value: Int) : ServerPacket
    data class SetInteractionFlagC(val value: Int) : ServerPacket
    data class SetInteractionFlagD(val value: Int) : ServerPacket
    data class SkipData(val payloadSize: Int) : ServerPacket
    data class Skip2Bytes(val value: Int) : ServerPacket
    data class UpdateUrlString(val payloadSize: Int) : ServerPacket
    data class SetUrlString(val payloadSize: Int) : ServerPacket
    data class Unknown(val opcode: Int, val payloadSize: Int) : ServerPacket

    // ── World Entity ─────────────────────────────────────────────────────
    data class WorldentityInfoV1(val payloadSize: Int) : ServerPacket
    data class WorldentityInfoV2(val payloadSize: Int) : ServerPacket
    data class WorldentityInfoV3(val payloadSize: Int) : ServerPacket
    data class WorldentityInfoV4(val payloadSize: Int) : ServerPacket
    data class WorldentityInfoV5(val payloadSize: Int) : ServerPacket
    data class RebuildWorldentity(val payloadSize: Int) : ServerPacket
    data class MinimapToggle(val payloadSize: Int) : ServerPacket
    data class ClearMapFlag(val payloadSize: Int) : ServerPacket
    data class ClearPendingUpdates(val dummy: Unit = Unit) : ServerPacket

    // ── Fallback ──────────────────────────────────────────────────────────
    data class Raw(val hexDump: String) : ServerPacket
}
