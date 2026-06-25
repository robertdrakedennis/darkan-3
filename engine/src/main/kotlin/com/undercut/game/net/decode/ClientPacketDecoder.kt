package com.undercut.game.net.decode

import com.undercut.game.net.ClientProt
import com.undercut.game.net.PacketReader
import com.undercut.game.net.model.ClientPacket

/**
 * Decodes raw client-to-server packet bytes into typed [ClientPacket] data classes.
 *
 * All byte-scramble sequences verified against the binary's sender functions (build 946-5)
 * unless marked with "TODO: verify 946-5 wire format" (1 remaining: OPNPC_T_LONG — no sender xref).
 * Each decoder is annotated with the binary sender address and the exact write sequence
 * so future audits can re-verify against the decompiled code.
 */
object ClientPacketDecoder {
    private val decoders = HashMap<ClientProt, (PacketReader) -> ClientPacket>()

    init {
        // TODO Phase 3: Re-register all decoders with 947-3 byte transforms.
        // All 946-5 decoders removed — opcode assignments and byte-scramble
        // sequences changed completely in the 946→947 revision update.
    }

    /* ══════════════════════════════════════════════════════════════════════════
     * DISABLED: All 946-5 decoders below need rewriting for 947-3.
     * Uncomment and update per-packet as Phase 3 progresses.
     * ══════════════════════════════════════════════════════════════════════════

    private fun registerMovement() {
        // MOVE_GAME (opcode 102, VAR_SHORT). Sender: 0x003d68a0.
        // Wire: pT_ushort(pathLen) + bb0660[waypoints...] + p1(runFlag)
        // Complex variable-length; just record size for now.
        register(ClientProt.MOVE_GAME) { r ->
            ClientPacket.MoveGame("MOVE_GAME", r.remaining)
        }

        // MOVE_GAME_MINIMENU (opcode 33, 5B). Sender: 0x001fe300.
        // Write: p1sub128(destX_lo) + p1(destX_hi) + p1neg(ctrlRun) + p1(destY_hi) + p1sub128(destY_lo)
        // Read:  g2leAdd128(destX) + g1neg(ctrlRun) + g2add128(destY)
        register(ClientProt.MOVE_GAME_MINIMENU) { r ->
            val destX = r.g2leAdd128()
            val ctrlRun = r.g1neg() != 0
            val destY = r.g2add128()
            ClientPacket.MoveGameClick("MOVE_GAME_MINIMENU", destX, destY, ctrlRun)
        }

        // MOVE_GAME_EXTENDED (opcode 92, 18B). Sender: 0x001fe300 (same function, extended path).
        // Write: same first 5 bytes as MINIMENU + 13B extended entity data.
        // Read:  g2leAdd128(destX) + g1neg(ctrlRun) + g2add128(destY) + skip(13)
        register(ClientProt.MOVE_GAME_EXTENDED) { r ->
            val destX = r.g2leAdd128()
            val ctrlRun = r.g1neg() != 0
            val destY = r.g2add128()
            // 13 remaining bytes are extended target context (entity info)
            ClientPacket.MoveGameClick("MOVE_GAME_EXTENDED", destX, destY, ctrlRun)
        }

        // MOVE_SCRIPTED (opcode 89, 5B). Sender: 0x0040cfc0.
        // Write: pT_ushort(coordData1) + p1neg(moveSpeed) + p1(coordData2_lo) + p1(coordData2_hi)
        // Read:  g2(coordData1) + g1neg(moveSpeed) + g2le(coordData2)
        register(ClientProt.MOVE_SCRIPTED) { r ->
            val coordData1 = r.g2()
            val moveSpeed = r.g1neg()
            val coordData2 = r.g2le()
            ClientPacket.MoveScripted(coordData1, moveSpeed, coordData2)
        }
    }

    // ── NPC Actions ─────────────────────────────────────────────────────────

    private fun registerNpcActions() {
        // OPNPC1-6 (opcodes 26/25/23/90/77/103, 7B each). Sender: 0x003d4d70.
        // Write: p1(coordY_hi) + p1sub128(coordY_lo) + p1(npcIdx_lo) + p1(npcIdx_hi)
        //      + p1_neg_sub128(ctrlRun) + p1sub128(coordX_lo) + p1(coordX_hi)
        // Read:  g2add128(coordY) + g2le(npcIndex) + g1sub128(ctrlRun) + g2leAdd128(coordX)
        for ((prot, opNum) in listOf(
            ClientProt.OPNPC1 to 1, ClientProt.OPNPC2 to 2, ClientProt.OPNPC3 to 3,
            ClientProt.OPNPC4 to 4, ClientProt.OPNPC5 to 5, ClientProt.OPNPC6 to 6
        )) {
            register(prot) { r ->
                val coordY = r.g2add128()
                val npcIndex = r.g2le()
                val ctrlRun = r.g1sub128() != 0
                val coordX = r.g2leAdd128()
                ClientPacket.OpNpc(opNum, npcIndex, ctrlRun, coordX, coordY)
            }
        }

        // OPNPC_T1-6 (opcodes 9/19/125/69/35/36, 3B each). "Use item on NPC" variants.
        // Sender: SendOpLocTLong else-branch. Wire: g2(locId) + g1sub128(ctrlRun)
        for ((prot, opNum) in listOf(
            ClientProt.OPNPC_T1 to 1, ClientProt.OPNPC_T2 to 2, ClientProt.OPNPC_T3 to 3,
            ClientProt.OPNPC_T4 to 4, ClientProt.OPNPC_T5 to 5, ClientProt.OPNPC_T6 to 6
        )) {
            register(prot) { r ->
                val locId = r.g2()
                val ctrlRun = r.g1sub128() != 0
                ClientPacket.OpNpcT(opNum, locId, ctrlRun)
            }
        }

        // OPNPC_T_LONG (opcode 5, 15B)
        // TODO: verify 946-5 wire format
        register(ClientProt.OPNPC_T_LONG) { r -> ClientPacket.OpNpcTLong(r.remaining) }

        // OPNPC_CS2 (opcode 104, var_byte)
        register(ClientProt.OPNPC_CS2) { r -> ClientPacket.OpNpcCs2(r.remaining) }
    }

    // ── Location Actions ────────────────────────────────────────────────────

    private fun registerLocationActions() {
        // OPLOC1 (opcode 70, 4B). Sender: 0x001da9e0 (DoOpLoc).
        // Write: p1(coordX_lo) + p1(coordX_hi) + p1sub128(coordY_lo) + p1(coordY_hi)
        // Read:  g2le(coordX) + g2leAdd128(coordY)
        register(ClientProt.OPLOC1) { r ->
            val coordX = r.g2le()
            val coordY = r.g2leAdd128()
            ClientPacket.OpLoc1(coordX, coordY)
        }

        // OPLOC_T1-6 (opcodes 38/68/40/101/111/43, 9B each).
        // Sender: SendOpPlayerTExtended else-branch.
        // Write: p1sub128(destY_lo) + p1(destY_hi) + p1add128(ctrlRun) + p1sub128(destX_lo)
        //      + p1(destX_hi) + p4_alt1(locId)
        // Read:  g2leAdd128(destY) + g1sub128(ctrlRun) + g2leAdd128(destX) + g4alt1(locId)
        for ((prot, opNum) in listOf(
            ClientProt.OPLOC_T1 to 1, ClientProt.OPLOC_T2 to 2, ClientProt.OPLOC_T3 to 3,
            ClientProt.OPLOC_T4 to 4, ClientProt.OPLOC_T5 to 5, ClientProt.OPLOC_T6 to 6
        )) {
            register(prot) { r ->
                val destY = r.g2leAdd128()
                val ctrlRun = r.g1sub128() != 0
                val destX = r.g2leAdd128()
                val locId = r.g4alt1()
                ClientPacket.OpLocT(opNum, destY, ctrlRun, destX, locId)
            }
        }

        // OPLOC_T (opcode 27, 12B). Sender: SendOpLocT @ 0x001fe150
        // Wire: g4alt1(componentHash) + g2le(selectedItemId) + g2leAdd128(coordX) + g2leAdd128(coordY) + g2add128(slot)
        register(ClientProt.OPLOC_T) { r ->
            val componentHash = r.g4alt1()
            val selectedItemId = r.g2le()
            val coordX = r.g2leAdd128()
            val coordY = r.g2leAdd128()
            val slot = r.g2add128()
            ClientPacket.OpLocTargeted(componentHash, selectedItemId, coordX, coordY, slot)
        }

        // OPLOC_T_LONG (opcode 16, 11B). Sender: SendOpLocTLong @ 0x001fed50 (if-branch)
        // Wire: g2add128(locId) + g4alt1(componentHash) + g2(selectedItemId) + g1sub128(ctrlRun) + g2(slot)
        register(ClientProt.OPLOC_T_LONG) { r ->
            val locId = r.g2add128()
            val componentHash = r.g4alt1()
            val selectedItemId = r.g2()
            val ctrlRun = r.g1sub128() != 0
            val slot = r.g2()
            ClientPacket.OpLocTLong(locId, componentHash, selectedItemId, ctrlRun, slot)
        }

        // OPLOC_T_EXTENDED (opcode 37, 15B). Sender: SendOpLocTExtended @ 0x001fe8f0 (if-branch)
        // Wire: g1(flags) + g2le(coordY) + g2leAdd128(selectedItemId) + g4le(componentHash) + g2le(slot) + g2(locId) + g2le(coordX)
        register(ClientProt.OPLOC_T_EXTENDED) { r ->
            val flags = r.g1()
            val coordY = r.g2le()
            val selectedItemId = r.g2leAdd128()
            val componentHash = r.g4le()
            val slot = r.g2le()
            val locId = r.g2()
            val coordX = r.g2le()
            ClientPacket.OpLocTExtended(flags, coordY, selectedItemId, componentHash, slot, locId, coordX)
        }

        // OPLOC_CS2 (opcode 94, var_byte)
        register(ClientProt.OPLOC_CS2) { r -> ClientPacket.OpLocCs2(r.remaining) }
    }

    // ── Object Actions ──────────────────────────────────────────────────────

    private fun registerObjectActions() {
        // OPOBJ1-10 (opcodes 20/46/115/96/6/60/14/59/91/30, 3B each). Sender: 0x0037c940.
        // Write: p1_neg_sub128(ctrlRun) + p1(packed_hi) + p1sub128(packed_lo)
        // Read:  g1sub128(ctrlRun) + g2add128(packedCoord)
        for ((prot, opNum) in listOf(
            ClientProt.OPOBJ1 to 1, ClientProt.OPOBJ2 to 2, ClientProt.OPOBJ3 to 3,
            ClientProt.OPOBJ4 to 4, ClientProt.OPOBJ5 to 5, ClientProt.OPOBJ6 to 6,
            ClientProt.OPOBJ7 to 7, ClientProt.OPOBJ8 to 8, ClientProt.OPOBJ9 to 9,
            ClientProt.OPOBJ10 to 10
        )) {
            register(prot) { r ->
                val ctrlRun = r.g1sub128() != 0
                val packedCoord = r.g2add128()
                ClientPacket.OpObj(opNum, ctrlRun, packedCoord)
            }
        }

        // OPOBJ_T (opcode 105, 11B). Sender: SendOpObjT @ 0x001fe6e0
        // Wire: g1sub128(ctrlRun) + g2le(slot) + g4(componentHash) + g2add128(selectedItemId) + g2add128(packedCoord)
        register(ClientProt.OPOBJ_T) { r ->
            val ctrlRun = r.g1sub128() != 0
            val slot = r.g2le()
            val componentHash = r.g4()
            val selectedItemId = r.g2add128()
            val packedCoord = r.g2add128()
            ClientPacket.OpObjT(ctrlRun, slot, componentHash, selectedItemId, packedCoord)
        }

        // OPOBJ_CS2 (opcode 98, var_byte)
        register(ClientProt.OPOBJ_CS2) { r -> ClientPacket.OpObjCs2(r.remaining) }

        // OPOBJ_CS2_2 (opcode 84, var_byte)
        register(ClientProt.OPOBJ_CS2_2) { r -> ClientPacket.OpObjCs2_2(r.remaining) }
    }

    // ── Player Actions ──────────────────────────────────────────────────────

    private fun registerPlayerActions() {
        // OPPLAYER_T (opcode 120, 11B). Sender: DoOpPlayer @ 0x00ba5bfd
        // Wire: g1add128(ctrlRun) + g4(componentHash) + g4le(selectedHash) + g2leAdd128(coordY)
        register(ClientProt.OPPLAYER_T) { r ->
            val ctrlRun = r.g1add128() != 0
            val componentHash = r.g4()
            val selectedHash = r.g4le()
            val coordY = r.g2leAdd128()
            ClientPacket.OpPlayerT(ctrlRun, componentHash, selectedHash, coordY)
        }

        // OPPLAYER_T_EXTENDED (opcode 58, 17B). Sender: SendOpPlayerTExtended @ 0x001ff0a0
        // Wire: g2leAdd128(selectedItemId) + g2add128(coordX) + g1(ctrlRun) + g2le(coordY) + g4alt2(componentHash) + g4le(locId) + g2le(slot)
        register(ClientProt.OPPLAYER_T_EXTENDED) { r ->
            val selectedItemId = r.g2leAdd128()
            val coordX = r.g2add128()
            val ctrlRun = r.g1() != 0
            val coordY = r.g2le()
            val componentHash = r.g4alt2()
            val locId = r.g4le()
            val slot = r.g2le()
            ClientPacket.OpPlayerTExtended(selectedItemId, coordX, ctrlRun, coordY, componentHash, locId, slot)
        }

        // OPPLAYER_CS2 (opcode 119, var_byte)
        register(ClientProt.OPPLAYER_CS2) { r -> ClientPacket.OpPlayerCs2(r.remaining) }
    }

    // ── Interface Buttons ───────────────────────────────────────────────────

    private fun registerInterfaceButtons() {
        // IF_BUTTON1-10 (opcodes 97/118/54/128/18/64/61/124/63/47, 8B each).
        // Sender: IfButtonXInner (0x003fdd00), short path (no target string).
        // Write: pT_ushort(componentHash) + p1(slot_lo) + p1(slot_hi) + p4_alt1(itemId)
        // Read:  g2(componentHash) + g2le(slot) + g4alt1(itemId)
        for (btn in listOf(
            ClientProt.IF_BUTTON1, ClientProt.IF_BUTTON2, ClientProt.IF_BUTTON3,
            ClientProt.IF_BUTTON4, ClientProt.IF_BUTTON5, ClientProt.IF_BUTTON6,
            ClientProt.IF_BUTTON7, ClientProt.IF_BUTTON8, ClientProt.IF_BUTTON9,
            ClientProt.IF_BUTTON10
        )) {
            val num = btn.name.removePrefix("IF_BUTTON").toIntOrNull() ?: 0
            register(btn) { r ->
                val componentHash = r.g2()
                val slot = r.g2le()
                val itemId = r.g4alt1()
                ClientPacket.IfButton(num, componentHash, slot, itemId)
            }
        }

        // IF_BUTTON_T (opcode 12, 16B). Sender: embedded in FUN_002e2af0
        // Wire: g2(targetType) + g2(selectedSlot) + g4(componentHash) + g4alt1(selectedComponentHash) + g2(selectedSlot2) + g2add128(targetId)
        register(ClientProt.IF_BUTTON_T) { r ->
            val targetType = r.g2()
            val selectedSlot = r.g2()
            val componentHash = r.g4()
            val selectedComponentHash = r.g4alt1()
            val selectedSlot2 = r.g2()
            val targetId = r.g2add128()
            ClientPacket.IfButtonT(targetType, selectedSlot, componentHash, selectedComponentHash, selectedSlot2, targetId)
        }

        // IF_BUTTONT (opcode 107, var_byte). Variable-length interface button with target string.
        // Wire format unverified — payload size varies; raw capture only.
        register(ClientProt.IF_BUTTONT) { r -> ClientPacket.IfButtonTVar(r.remaining) }

        // IF_BUTTON_TARGETMENU (opcode 75, var_short)
        register(ClientProt.IF_BUTTON_TARGETMENU) { r -> ClientPacket.IfButtonTargetMenu(r.remaining) }

        // IF_BUTTON_TARGETMENU_SEND (opcode 114, 22B). Sender: SendIfButtonTargetMenu @ 0x0039c5e9
        // Wire: g4alt2(param1) + g4alt1(param2) + g4le(componentHash1) + g4alt1(param3) + g4le(componentHash2) + g2leAdd128(slot)
        register(ClientProt.IF_BUTTON_TARGETMENU_SEND) { r ->
            val param1 = r.g4alt2()
            val param2 = r.g4alt1()
            val componentHash1 = r.g4le()
            val param3 = r.g4alt1()
            val componentHash2 = r.g4le()
            val slot = r.g2leAdd128()
            ClientPacket.IfButtonTargetMenuSend(param1, param2, componentHash1, param3, componentHash2, slot)
        }

        // INTERFACE_INTERACTION (opcode 122, 16B). Sender: SendInterfaceInteraction @ 0x00267db0
        // Wire: g2add128(selectedSlot) + g2le(coordX) + g4le(coordY) + g4alt1(componentHash) + g2leAdd128(targetType) + g2(selectedItemId)
        register(ClientProt.INTERFACE_INTERACTION) { r ->
            val selectedSlot = r.g2add128()
            val coordX = r.g2le()
            val coordY = r.g4le()
            val componentHash = r.g4alt1()
            val targetType = r.g2leAdd128()
            val selectedItemId = r.g2()
            ClientPacket.InterfaceInteraction(selectedSlot, coordX, coordY, componentHash, targetType, selectedItemId)
        }

        // CLOSE_MODAL (opcode 87, 0B). No payload.
        register(ClientProt.CLOSE_MODAL) { _ -> ClientPacket.CloseModal() }

        // CLOSE_MODAL_COMPONENT (opcode 117, 6B). Sender: 0x003fab50.
        // Write: p4_alt1(componentHash) + p1sub128(slot_lo) + p1(slot_hi)
        // Read:  g4alt1(componentHash) + g2leAdd128(slot)
        register(ClientProt.CLOSE_MODAL_COMPONENT) { r ->
            val componentHash = r.g4alt1()
            val slot = r.g2leAdd128()
            ClientPacket.CloseModalComponent(componentHash, slot)
        }
    }

    // ── Chat ────────────────────────────────────────────────────────────────

    private fun registerChat() {
        // MESSAGE_PUBLIC (opcode 29, var_byte). Sender: 0x003a4890.
        register(ClientProt.MESSAGE_PUBLIC) { r -> ClientPacket.MessagePublic(r.gstr()) }

        // MESSAGE_PUBLIC_EFFECTS (opcode 4, var_byte)
        register(ClientProt.MESSAGE_PUBLIC_EFFECTS) { r -> ClientPacket.MessagePublicEffects(r.remaining) }

        // MESSAGE_PRIVATE (opcode 52, var_short)
        register(ClientProt.MESSAGE_PRIVATE) { r -> ClientPacket.MessagePrivate(r.remaining) }

        // MESSAGE_CLAN_CHAT (opcode 86, var_byte)
        register(ClientProt.MESSAGE_CLAN_CHAT) { r -> ClientPacket.MessageClanChat(r.remaining) }

        // CLAN_JOINCHAT (opcode 67, var_byte)
        register(ClientProt.CLAN_JOINCHAT) { r -> ClientPacket.ClanJoinChat(r.remaining) }

        // CLAN_LEAVECHAT (opcode 93, var_byte)
        register(ClientProt.CLAN_LEAVECHAT) { r -> ClientPacket.ClanLeaveChat(r.remaining) }

        // ACTIVE_CHAT_PHRASE_SEND (opcode 71, var_byte)
        register(ClientProt.ACTIVE_CHAT_PHRASE_SEND) { r -> ClientPacket.ActiveChatPhraseSend(r.remaining) }

        // ACTIVE_CHAT_PHRASE_SENDPRIVATE (opcode 81, var_byte)
        register(ClientProt.ACTIVE_CHAT_PHRASE_SENDPRIVATE) { r ->
            ClientPacket.ActiveChatPhraseSendPrivate(r.remaining)
        }
    }

    // ── Social ──────────────────────────────────────────────────────────────

    private fun registerSocial() {
        // SOCIAL_REQUEST (opcode 39, var_byte)
        register(ClientProt.SOCIAL_REQUEST) { r -> ClientPacket.SocialRequest(r.remaining) }

        // FRIENDLIST_ADD (opcode 78, var_byte)
        register(ClientProt.FRIENDLIST_ADD) { r -> ClientPacket.FriendlistAdd(r.remaining) }

        // FRIENDLIST_DEL (opcode 121, var_byte)
        register(ClientProt.FRIENDLIST_DEL) { r -> ClientPacket.FriendlistDel(r.remaining) }

        // IGNORELIST_ADD (opcode 109, var_byte)
        register(ClientProt.IGNORELIST_ADD) { r -> ClientPacket.IgnorelistAdd(r.remaining) }
    }

    // ── Dialog Resumption ───────────────────────────────────────────────────

    private fun registerDialogResumption() {
        // All three use FUN_00bb0660 for CS2 stack serialization; raw payload only.

        // RESUME_COUNTDIALOG (opcode 7, var_short). Sender: 0x003604a0.
        register(ClientProt.RESUME_COUNTDIALOG) { r -> ClientPacket.ResumeCountDialog(r.remaining) }

        // RESUME_NAMEDIALOG (opcode 56, var_byte). Sender: 0x00360610.
        register(ClientProt.RESUME_NAMEDIALOG) { r -> ClientPacket.ResumeNameDialog(r.remaining) }

        // RESUME_PAUSEBUTTON (opcode 10, var_short). Sender: 0x00360300.
        register(ClientProt.RESUME_PAUSEBUTTON) { r -> ClientPacket.ResumePauseButton(r.remaining) }
    }

    // ── Events / Telemetry ──────────────────────────────────────────────────

    private fun registerEvents() {
        // EVENT_APPLET_FOCUS (opcode 0, var_short)
        register(ClientProt.EVENT_APPLET_FOCUS) { r -> ClientPacket.EventAppletFocus(r.remaining) }

        // EVENT_CAMERA_POSITION (opcode 1, 9B)
        register(ClientProt.EVENT_CAMERA_POSITION) { r -> ClientPacket.EventCameraPosition(r.remaining) }

        // CAMERA_DIRECTION (opcode 2, 6B): likely yaw(2) + pitch(2) + extra(2)
        register(ClientProt.CAMERA_DIRECTION) { r ->
            val yaw = r.g2()
            val pitch = r.g2()
            ClientPacket.CameraDirection(yaw, pitch)
        }

        // EVENT_CAMERA_POSITION_2 (opcode 31, var_short)
        register(ClientProt.EVENT_CAMERA_POSITION_2) { r -> ClientPacket.EventCameraPosition2(r.remaining) }

        // CAMERA_ANGLE (opcode 51, 4B)
        register(ClientProt.CAMERA_ANGLE) { r -> ClientPacket.CameraAngle(r.remaining) }

        // EVENT_MOUSE_CLICK (opcode 17, var_byte)
        register(ClientProt.EVENT_MOUSE_CLICK) { r -> ClientPacket.EventMouseClick(r.remaining) }

        // EVENT_KEYBOARD (opcode 48, var_byte)
        register(ClientProt.EVENT_KEYBOARD) { r -> ClientPacket.EventKeyboard(r.remaining) }

        // EVENT_MOUSE_MOVE (opcode 85, 7B)
        register(ClientProt.EVENT_MOUSE_MOVE) { r -> ClientPacket.EventMouseMove(r.remaining) }

        // EVENT_TELEMETRY (opcode 45, var_short)
        register(ClientProt.EVENT_TELEMETRY) { r -> ClientPacket.EventTelemetry(r.remaining) }

        // EVENT_APPLET_FOCUS_2 (opcode 66, 4B)
        register(ClientProt.EVENT_APPLET_FOCUS_2) { r -> ClientPacket.EventAppletFocus2(r.g4()) }

        // SCENE_INTERACTION (opcode 65, 4B)
        register(ClientProt.SCENE_INTERACTION) { r -> ClientPacket.SceneInteraction(r.g4()) }

        // FOCUS_CHANGED (opcode 83, 1B)
        register(ClientProt.FOCUS_CHANGED) { r -> ClientPacket.FocusChanged(r.g1() != 0) }
    }

    // ── Display / Device ────────────────────────────────────────────────────

    private fun registerDisplay() {
        // DEVICE_INFO (opcode 28, var_byte)
        register(ClientProt.DEVICE_INFO) { r -> ClientPacket.DeviceInfo(r.remaining) }

        // DISPLAY_INFO (opcode 106, 6B). Sender: SendDisplayInfo @ 0x002c47b0
        // Wire: g1(displayMode) + g2(screenWidth) + g2(screenHeight) + g1(antialiasFlag)
        register(ClientProt.DISPLAY_INFO) { r ->
            val displayMode = r.g1()
            val screenWidth = r.g2()
            val screenHeight = r.g2()
            val antialiasFlag = r.g1()
            ClientPacket.DisplayInfo(displayMode, screenWidth, screenHeight, antialiasFlag)
        }

        // RENDER_REPORT (opcode 113, 4B)
        register(ClientProt.RENDER_REPORT) { r -> ClientPacket.RenderReport(r.g4()) }

        // SCENE_GRAPH_REPORT (opcode 50, 4B)
        register(ClientProt.SCENE_GRAPH_REPORT) { r -> ClientPacket.SceneGraphReport(r.g4()) }
    }

    // ── System / Keepalive ──────────────────────────────────────────────────

    private fun registerSystem() {
        // NO_TIMEOUT (opcode 15, 0B). No payload (XOR EDX,EDX at RegisterAll 0x001822f3).
        register(ClientProt.NO_TIMEOUT) { _ -> ClientPacket.NoTimeout() }

        // NO_TIMEOUT_2 (opcode 80, 0B). No payload (XOR EDX,EDX at RegisterAll 0x0018287d).
        register(ClientProt.NO_TIMEOUT_2) { _ -> ClientPacket.NoTimeout2() }

        // WINDOW_STATUS (opcode 82, 3B). Sender: SendWindowStatus @ 0x003c5150
        // Wire: g1(displayMode) + g1(canvasWidth) + g1(canvasHeight) — all plain p1 bytes
        register(ClientProt.WINDOW_STATUS) { r ->
            val mode = r.g1()
            val width = r.g1()
            val height = r.g1()
            ClientPacket.WindowStatus(mode, width, height)
        }

        // MAP_BUILD_COMPLETE (opcode 21, 0B). No payload (XOR EDX,EDX at RegisterAll 0x00182374).
        register(ClientProt.MAP_BUILD_COMPLETE) { _ -> ClientPacket.MapBuildComplete() }

        // QUEUED_PACKET (opcode 34, 0B). No payload (XOR EDX,EDX at RegisterAll 0x0018248f).
        register(ClientProt.QUEUED_PACKET) { _ -> ClientPacket.QueuedPacket() }

        // DETECT_MODIFIED_CLIENT (opcode 95, 4B)
        register(ClientProt.DETECT_MODIFIED_CLIENT) { r -> ClientPacket.DetectModifiedClient(r.g4()) }

        // WORLDLIST_FETCH (opcode 110, 4B)
        register(ClientProt.WORLDLIST_FETCH) { r -> ClientPacket.WorldlistFetch(r.g4()) }

        // BUG_REPORT (opcode 123, 1B)
        register(ClientProt.BUG_REPORT) { r -> ClientPacket.BugReport(r.g1()) }

        // DATA_REPORT (opcode 49, var_short)
        register(ClientProt.DATA_REPORT) { r -> ClientPacket.DataReport(r.remaining) }

        // CS2_CALLBACK (opcode 32, var_byte)
        register(ClientProt.CS2_CALLBACK) { r -> ClientPacket.Cs2Callback(r.remaining) }

        // CLIENT_CHEAT (opcode 24, var_byte)
        register(ClientProt.CLIENT_CHEAT) { r -> ClientPacket.ClientCheat(r.gstr()) }
    }

    // ── String Encoding / Misc ──────────────────────────────────────────────

    private fun registerStringEncoding() {
        // VERIFIED_STRING_SEND (opcode 3, var_short)
        register(ClientProt.VERIFIED_STRING_SEND) { r -> ClientPacket.VerifiedStringSend(r.remaining) }

        // STRTOL_SEND (opcode 8, 4B)
        register(ClientProt.STRTOL_SEND) { r -> ClientPacket.StrtolSend(r.g4()) }

        // STRTOLL_SEND (opcode 129, 8B)
        register(ClientProt.STRTOLL_SEND) { r -> ClientPacket.StrtollSend(r.g8()) }

        // AFFINEDTRANSFORM_SET (opcode 99, 2B)
        register(ClientProt.AFFINEDTRANSFORM_SET) { r -> ClientPacket.AffinedtransformSet(r.g2()) }

        // SOUND_SONGEND (opcode 76, 2B)
        register(ClientProt.SOUND_SONGEND) { r -> ClientPacket.SoundSongend(r.g2()) }

        // SOUND_SONGSELECT (opcode 88, 2B)
        register(ClientProt.SOUND_SONGSELECT) { r -> ClientPacket.SoundSongselect(r.g2()) }

        // ENCRYPTED_STRING_SEND (opcode 116, var_byte)
        register(ClientProt.ENCRYPTED_STRING_SEND) { r -> ClientPacket.EncryptedStringSend(r.remaining) }

        // ENCRYPTED_STRING_SEND2 (opcode 74, var_short)
        register(ClientProt.ENCRYPTED_STRING_SEND2) { r -> ClientPacket.EncryptedStringSend2(r.remaining) }

        // ENCODEDSTRING_SEND (opcode 62, var_byte)
        register(ClientProt.ENCODEDSTRING_SEND) { r -> ClientPacket.EncodedStringSend(r.remaining) }

        // ENCODEDSTRING_SEND2 (opcode 127, var_byte)
        register(ClientProt.ENCODEDSTRING_SEND2) { r -> ClientPacket.EncodedStringSend2(r.remaining) }
    }

    // ── Unknown Opcodes ─────────────────────────────────────────────────────

    private fun registerUnknown() {
        for (prot in listOf(
            ClientProt.UNKNOWN_11, ClientProt.UNKNOWN_13, ClientProt.UNKNOWN_22,
            ClientProt.UNKNOWN_41, ClientProt.UNKNOWN_42, ClientProt.UNKNOWN_44,
            ClientProt.UNKNOWN_53, ClientProt.UNKNOWN_55, ClientProt.UNKNOWN_57,
            ClientProt.UNKNOWN_72, ClientProt.UNKNOWN_73, ClientProt.UNKNOWN_79,
            ClientProt.UNKNOWN_100, ClientProt.UNKNOWN_108, ClientProt.UNKNOWN_112,
            ClientProt.UNKNOWN_126
        )) {
            register(prot) { r -> ClientPacket.Unknown(prot.opcode, r.remaining) }
        }
    }
    ══════════════════════════════════════════════════════════════════════════ */

    private fun register(prot: ClientProt, decoder: (PacketReader) -> ClientPacket) {
        decoders[prot] = decoder
    }

    fun decode(prot: ClientProt, data: ByteArray): ClientPacket? {
        val decoder = decoders[prot] ?: return null
        return try {
            decoder(PacketReader(data))
        } catch (e: Exception) {
            null
        }
    }
}
