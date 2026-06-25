package com.undercut.game.net.model

/**
 * Sealed interface covering all 130 client-to-server packet types.
 * Each ClientProt opcode maps to one data class here.
 *
 * Field encodings verified against binary senders (build 946-3).
 * See docs/net/clientprot/ for full per-packet documentation.
 */
sealed interface ClientPacket {

    // -- Movement ----------------------------------------------------------------

    /** MOVE_GAME (opcode 102, VAR_SHORT) -- variable-length pathfinding walk. */
    data class MoveGame(val type: String, val payloadSize: Int) : ClientPacket

    /**
     * MOVE_GAME_MINIMENU (opcode 33, 5B) / MOVE_GAME_EXTENDED (opcode 92, 18B).
     * Binary sender: 0x001fe300.
     * Wire: g2leAdd128(destX) + g1neg(ctrlRun) + g2add128(destY) [+ 13B extended]
     */
    data class MoveGameClick(
        val variant: String,
        val destX: Int,
        val destY: Int,
        val ctrlRun: Boolean
    ) : ClientPacket

    /**
     * MOVE_SCRIPTED (opcode 89, 5B). Binary sender: 0x0040cfc0.
     * Wire: g2(coordData1) + g1neg(moveSpeed) + g2le(coordData2)
     */
    data class MoveScripted(val coordData1: Int, val moveSpeed: Int, val coordData2: Int) : ClientPacket

    // -- NPC Actions -------------------------------------------------------------

    /**
     * OPNPC1-6 (opcodes 26/25/23/90/77/103, 7B each).
     * Binary sender: 0x003d4d70 (DoOpNPC dispatch switch).
     * Wire: g2add128(coordY) + g2le(npcIndex) + g1sub128(ctrlRun) + g2leAdd128(coordX)
     */
    data class OpNpc(
        val opNum: Int,
        val npcIndex: Int,
        val ctrlRun: Boolean,
        val coordX: Int,
        val coordY: Int
    ) : ClientPacket

    /**
     * OPNPC_T1-6 (opcodes 9/19/125/69/35/36, 3B each).
     * Binary sender: SendOpLocTLong else-branch.
     * Wire: g2(locId) + g1sub128(ctrlRun)
     */
    data class OpNpcT(val opNum: Int, val locId: Int, val ctrlRun: Boolean) : ClientPacket

    /** OPNPC_T_LONG (opcode 5, 15B). No sender found; raw payload only. */
    data class OpNpcTLong(val payloadSize: Int) : ClientPacket
    data class OpNpcCs2(val payloadSize: Int) : ClientPacket

    // -- Location Actions --------------------------------------------------------

    /**
     * OPLOC1 (opcode 70, 4B). Binary sender: 0x001da9e0 (DoOpLoc).
     * Wire: g2le(coordX) + g2leAdd128(coordY)
     */
    data class OpLoc1(val coordX: Int, val coordY: Int) : ClientPacket

    /**
     * OPLOC_T1-6 (opcodes 38/68/40/101/111/43, 9B each).
     * Binary sender: SendOpPlayerTExtended else-branch.
     * Wire: g2leAdd128(destY) + g1sub128(ctrlRun) + g2leAdd128(destX) + g4alt1(locId)
     */
    data class OpLocT(
        val opNum: Int,
        val destY: Int,
        val ctrlRun: Boolean,
        val destX: Int,
        val locId: Int
    ) : ClientPacket

    /**
     * OPLOC_T (opcode 27, 12B). Binary sender: SendOpLocT at 0x001fe150.
     * Wire: g4alt1(componentHash) + g2le(selectedItemId) + g2leAdd128(coordX) + g2leAdd128(coordY) + g2add128(slot)
     */
    data class OpLocTargeted(
        val componentHash: Int,
        val selectedItemId: Int,
        val coordX: Int,
        val coordY: Int,
        val slot: Int
    ) : ClientPacket

    /**
     * OPLOC_T_LONG (opcode 16, 11B). Binary sender: SendOpLocTLong if-branch at 0x001fed50.
     * Wire: g2add128(locId) + g4alt1(componentHash) + g2(selectedItemId) + g1sub128(ctrlRun) + g2(slot)
     */
    data class OpLocTLong(
        val locId: Int,
        val componentHash: Int,
        val selectedItemId: Int,
        val ctrlRun: Boolean,
        val slot: Int
    ) : ClientPacket

    /**
     * OPLOC_T_EXTENDED (opcode 37, 15B). Binary sender: SendOpLocTExtended if-branch at 0x001fe8f0.
     * Wire: g1(flags) + g2le(coordY) + g2leAdd128(selectedItemId) + g4le(componentHash) + g2le(slot) + g2(locId) + g2le(coordX)
     */
    data class OpLocTExtended(
        val flags: Int,
        val coordY: Int,
        val selectedItemId: Int,
        val componentHash: Int,
        val slot: Int,
        val locId: Int,
        val coordX: Int
    ) : ClientPacket

    data class OpLocCs2(val payloadSize: Int) : ClientPacket

    // -- Object Actions ----------------------------------------------------------

    /**
     * OPOBJ1-10 (opcodes 20/46/115/96/6/60/14/59/91/30, 3B each).
     * Binary sender: 0x0037c940 (DoOpObj dispatch switch).
     * Wire: g1sub128(ctrlRun) + g2add128(packedCoord)
     */
    data class OpObj(val opNum: Int, val ctrlRun: Boolean, val packedCoord: Int) : ClientPacket

    /**
     * OPOBJ_T (opcode 105, 11B). Binary sender: SendOpObjT at 0x001fe6e0.
     * Wire: g1sub128(ctrlRun) + g2le(slot) + g4(componentHash) + g2add128(selectedItemId) + g2add128(packedCoord)
     */
    data class OpObjT(
        val ctrlRun: Boolean,
        val slot: Int,
        val componentHash: Int,
        val selectedItemId: Int,
        val packedCoord: Int
    ) : ClientPacket
    data class OpObjCs2(val payloadSize: Int) : ClientPacket
    data class OpObjCs2_2(val payloadSize: Int) : ClientPacket

    // -- Player Actions ----------------------------------------------------------

    /**
     * OPPLAYER_T (opcode 120, 11B). Binary sender: DoOpPlayer at 0x00ba5bfd.
     * Wire: g1add128(ctrlRun) + g4(componentHash) + g4le(selectedHash) + g2leAdd128(coordY)
     * Note: register-based calling convention; field names inferred from protocol context.
     */
    data class OpPlayerT(
        val ctrlRun: Boolean,
        val componentHash: Int,
        val selectedHash: Int,
        val coordY: Int
    ) : ClientPacket

    /**
     * OPPLAYER_T_EXTENDED (opcode 58, 17B). Binary sender: SendOpPlayerTExtended if-branch at 0x001ff0a0.
     * Wire: g2leAdd128(selectedItemId) + g2add128(coordX) + g1(ctrlRun) + g2le(coordY) + g4alt2(componentHash) + g4le(locId) + g2le(slot)
     */
    data class OpPlayerTExtended(
        val selectedItemId: Int,
        val coordX: Int,
        val ctrlRun: Boolean,
        val coordY: Int,
        val componentHash: Int,
        val locId: Int,
        val slot: Int
    ) : ClientPacket
    data class OpPlayerCs2(val payloadSize: Int) : ClientPacket

    // -- Interface Buttons -------------------------------------------------------

    /**
     * IF_BUTTON1-10 (opcodes 97/118/54/128/18/64/61/124/63/47, 8B each).
     * Binary sender: IfButtonXInner (0x003fdd00), short path.
     * Wire: g2(componentHash) + g2le(slot) + g4alt1(itemId)
     */
    data class IfButton(val buttonNum: Int, val componentHash: Int, val slot: Int, val itemId: Int) : ClientPacket

    /**
     * IF_BUTTON_T (opcode 12, 16B). Binary sender: embedded in FUN_002e2af0.
     * Wire: g2(targetType) + g2(selectedSlot) + g4(componentHash) + g4alt1(selectedComponentHash) + g2(selectedSlot2) + g2add128(targetId)
     */
    data class IfButtonT(
        val targetType: Int,
        val selectedSlot: Int,
        val componentHash: Int,
        val selectedComponentHash: Int,
        val selectedSlot2: Int,
        val targetId: Int
    ) : ClientPacket

    /** IF_BUTTONT (opcode 107, VAR_BYTE). Variable-length button with target string; raw payload only. */
    data class IfButtonTVar(val payloadSize: Int) : ClientPacket

    data class IfButtonTargetMenu(val payloadSize: Int) : ClientPacket

    /**
     * IF_BUTTON_TARGETMENU_SEND (opcode 114, 22B). Binary sender: SendIfButtonTargetMenu at 0x0039c5e9.
     * Wire: g4alt2(param1) + g4alt1(param2) + g4le(componentHash1) + g4alt1(param3) + g4le(componentHash2) + g2leAdd128(slot)
     */
    data class IfButtonTargetMenuSend(
        val param1: Int,
        val param2: Int,
        val componentHash1: Int,
        val param3: Int,
        val componentHash2: Int,
        val slot: Int
    ) : ClientPacket

    /**
     * INTERFACE_INTERACTION (opcode 122, 16B). Binary sender: SendInterfaceInteraction at 0x00267db0.
     * Wire: g2add128(selectedSlot) + g2le(coordX) + g4le(coordY) + g4alt1(componentHash) + g2leAdd128(targetType) + g2(selectedItemId)
     */
    data class InterfaceInteraction(
        val selectedSlot: Int,
        val coordX: Int,
        val coordY: Int,
        val componentHash: Int,
        val targetType: Int,
        val selectedItemId: Int
    ) : ClientPacket
    data class CloseModal(val dummy: Unit = Unit) : ClientPacket

    /**
     * CLOSE_MODAL_COMPONENT (opcode 117, 6B).
     * Binary sender: 0x003fab50.
     * Wire: g4alt1(componentHash) + g2leAdd128(slot)
     */
    data class CloseModalComponent(val componentHash: Int, val slot: Int) : ClientPacket

    // -- Chat --------------------------------------------------------------------

    data class MessagePublic(val message: String) : ClientPacket
    data class MessagePublicEffects(val payloadSize: Int) : ClientPacket
    data class MessagePrivate(val payloadSize: Int) : ClientPacket
    data class MessageClanChat(val payloadSize: Int) : ClientPacket
    data class ClanJoinChat(val payloadSize: Int) : ClientPacket
    data class ClanLeaveChat(val payloadSize: Int) : ClientPacket
    data class ActiveChatPhraseSend(val payloadSize: Int) : ClientPacket
    data class ActiveChatPhraseSendPrivate(val payloadSize: Int) : ClientPacket

    // -- Social ------------------------------------------------------------------

    data class FriendlistAdd(val payloadSize: Int) : ClientPacket
    data class FriendlistDel(val payloadSize: Int) : ClientPacket
    data class IgnorelistAdd(val payloadSize: Int) : ClientPacket
    data class SocialRequest(val payloadSize: Int) : ClientPacket

    // -- Dialog Resumption -------------------------------------------------------

    /**
     * RESUME_COUNTDIALOG (opcode 7, VAR_SHORT) -- CS2 state serialization, not simple int.
     * RESUME_NAMEDIALOG (opcode 56, VAR_BYTE) -- CS2 state serialization, not simple string.
     * RESUME_PAUSEBUTTON (opcode 10, VAR_SHORT) -- CS2 state serialization + flags byte.
     * All three use FUN_00bb0660 for CS2 stack serialization; raw payload only.
     */
    data class ResumeCountDialog(val payloadSize: Int) : ClientPacket
    data class ResumeNameDialog(val payloadSize: Int) : ClientPacket
    data class ResumePauseButton(val payloadSize: Int) : ClientPacket

    // -- Events / Telemetry ------------------------------------------------------

    data class EventAppletFocus(val payloadSize: Int) : ClientPacket
    data class EventAppletFocus2(val flags: Int) : ClientPacket
    data class EventCameraPosition(val payloadSize: Int) : ClientPacket
    data class EventCameraPosition2(val payloadSize: Int) : ClientPacket
    data class EventMouseClick(val payloadSize: Int) : ClientPacket
    data class EventMouseMove(val payloadSize: Int) : ClientPacket
    data class EventKeyboard(val payloadSize: Int) : ClientPacket
    data class EventTelemetry(val payloadSize: Int) : ClientPacket
    data class CameraDirection(val yaw: Int, val pitch: Int) : ClientPacket
    data class CameraAngle(val payloadSize: Int) : ClientPacket
    data class SceneInteraction(val type: Int) : ClientPacket
    data class FocusChanged(val focused: Boolean) : ClientPacket

    // -- Display / Device --------------------------------------------------------

    data class DeviceInfo(val payloadSize: Int) : ClientPacket
    /**
     * DISPLAY_INFO (opcode 106, 6B). Binary sender: SendDisplayInfo at 0x002c47b0.
     * Wire: g1(displayMode) + g2(screenWidth) + g2(screenHeight) + g1(antialiasFlag)
     */
    data class DisplayInfo(val displayMode: Int, val screenWidth: Int, val screenHeight: Int, val antialiasFlag: Int) : ClientPacket
    data class RenderReport(val flags: Int) : ClientPacket
    data class SceneGraphReport(val value: Int) : ClientPacket

    // -- System / Keepalive ------------------------------------------------------

    data class NoTimeout(val dummy: Unit = Unit) : ClientPacket
    data class NoTimeout2(val dummy: Unit = Unit) : ClientPacket
    data class WindowStatus(val mode: Int, val width: Int, val height: Int) : ClientPacket
    data class MapBuildComplete(val dummy: Unit = Unit) : ClientPacket
    data class QueuedPacket(val dummy: Unit = Unit) : ClientPacket
    data class DetectModifiedClient(val value: Int) : ClientPacket
    data class WorldlistFetch(val value: Int) : ClientPacket
    data class BugReport(val type: Int) : ClientPacket

    // -- CS2 / String Encoding ---------------------------------------------------

    data class ClientCheat(val command: String) : ClientPacket
    data class Cs2Callback(val payloadSize: Int) : ClientPacket
    data class VerifiedStringSend(val payloadSize: Int) : ClientPacket
    data class EncryptedStringSend(val payloadSize: Int) : ClientPacket
    data class EncryptedStringSend2(val payloadSize: Int) : ClientPacket
    data class EncodedStringSend(val payloadSize: Int) : ClientPacket
    data class EncodedStringSend2(val payloadSize: Int) : ClientPacket
    data class StrtolSend(val value: Int) : ClientPacket
    data class StrtollSend(val value: Long) : ClientPacket
    data class AffinedtransformSet(val value: Int) : ClientPacket
    data class SoundSongend(val songId: Int) : ClientPacket
    data class SoundSongselect(val songId: Int) : ClientPacket
    data class DataReport(val payloadSize: Int) : ClientPacket

    // -- Unknown -----------------------------------------------------------------

    data class Unknown(val opcode: Int, val payloadSize: Int) : ClientPacket

    // -- Fallback ----------------------------------------------------------------

    data class Raw(val hexDump: String) : ClientPacket
}
