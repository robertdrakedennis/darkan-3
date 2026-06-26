package org.darkan.core.net.prot.revision.rev948

import kotlinx.io.readByteArray
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.readRSString
import world.gregs.voidps.buffer.readUIntLittle
import world.gregs.voidps.buffer.readUShortAdd
import world.gregs.voidps.buffer.readUShortAddLittle

/**
 * Rev948 ClientProt decoders.
 *
 * **KEEPALIVE FIX:** the timer keepalive the NXT client floods (size-0, every ~1s) is
 * **NO_TIMEOUT = op 51**, NOT op 14. Proven CERTAIN: the beta `jag::ConnectionManager::MainLogic`
 * builds `MakeClientMessage<jag::ClientProt>(&ClientProt::NO_TIMEOUT)` on a 0x32-tick counter; the
 * 948 `jag::ConnectionManager::ProcessConnections @ 0x0013e6c0` is its byte-identical port and
 * builds the packet at ProtEntry base 0x015d3b80 = op 51. (The function is named ProcessConnections
 * — that is NOT the packet name; the packet is NO_TIMEOUT.) op 14 is a DISTINCT packet:
 * **ABORT_P_DIALOG** (also size 0; beta `Resume::Resume` lambda#7 → `&ClientProt::ABORT_P_DIALOG`;
 * 948 emitter @ 0x002d0040 resets dialog state, NOT a timer keepalive). Registering Ping at op 14
 * was the bug that made the lobby warn-spam "Unhandled ClientProt" — fixed to op 51 below.
 *
 * Opcode→emitter bindings re-verified in rs2client.948-2-2 via get_xrefs_to on each opcode's
 * ProtEntry base (emitter references &DAT_(0x015d3ea0 - idx*0x10) via FUN_001367a0/
 * TcpConnectionMessage::Init). Sizes from jag::ClientProt::RegisterAll @ 0x000c45b0.
 *  - WORLDLIST_FETCH:        948 op 54 (SendWorldlistFetch @ 0x00194a80)
 *  - FRIENDLIST_DEL:         948 op 70 (SendFriendlistDel @ 0x0026b1f0)
 *  - IGNORELIST_ADD:         948 op 80 (SendIgnorelistAdd @ 0x0030f4b0, varByte)
 *  - FRIENDLIST_ADD:         948 op 100 (SendFriendlistAdd @ 0x0026a530)
 *  - CLANCHANNEL_KICKUSER:   948 op 89 (SendSocialRequest @ 0x0026aef0, varByte)
 *  - RESUME_P_NAMEDIALOG:    948 op 71 (Resume::Resume lambda#4, varByte length-prefixed UTF8)
 *  - IF_BUTTON1..10:         948 op 127/103/92/45/30/68/43/21/13/23 (IfButtonXInner short path, 8B click)
 *  - MESSAGE_PUBLIC:         948 op 124 (SendMessagePublic @ 0x0037a9c0)
 *  - MESSAGE_PRIVATE:        948 op 38 (SendMessagePrivate @ 0x00306dd0)
 *
 * **IF_BUTTON fix (2026-06-25 capture):** the size-8 interface CLICK opcodes are
 * IF_BUTTON1=op127, IF_BUTTON2=op103, IF_BUTTON3=op92, IF_BUTTON4=op45, IF_BUTTON5=op30,
 * IF_BUTTON6=op68, IF_BUTTON7=op43, IF_BUTTON8=op21, IF_BUTTON9=op13, IF_BUTTON10=op23
 * (dispatched by IfButtonXInner @0x002978d0 via the CS2 ProtEntry table @0x01365920, indexed by
 * (button-1); slot order 127,103,92,45,30,68,43,21,13,23). op35 is IF_PLAYER (the long-path
 * use-button-on-player, varByte) and is NOT registered as IfButton here. The size-3 ops
 * 39/73/47/33/108/56/91/50/16/40 are SendIfButtonN_CS2 component-presses (NOT clicks) and remain UNKNOWN.
 */
internal fun Codec.registerRev948ClientProts() {
    // NO_TIMEOUT keepalive — 948 dedicated emitter is op 51 (ProcessConnections/MainLogic timer).
    // op 14 (ABORT_P_DIALOG) is a different size-0 packet; do NOT register Ping there.
    // 948-5 cross-confirm: jag::ConnectionManager::MainLogic recorded as the descriptor's owning
    // sender (×2 emitters) — see docs/net/948-5-delta-from-948-2.md §3 op 51.
    clientProt<Ping>(opcode = 51, size = 0)

    clientProt<SceneGraphReport>(opcode = 5, size = 4) {
        SceneGraphReport(value = readInt())
    }

    // MAP_BUILD_COMPLETE — 948 SendMapBuildComplete @ 0x002bc030, entry 0x015d3800.
    clientProt<MapBuildComplete>(opcode = 107, size = 0)

    // Response emitted by ServerProt::HandleAntiCheatChallenge @ 0x00180920.
    // The final byte is `*(uint *)(*client + 0x534)` clamped to 0xff, then biased by -128.
    clientProt<AntiCheatChallengeResponse>(opcode = 3, size = 9) {
        AntiCheatChallengeResponse(
            challengeA = readInt(),
            challengeB = readUIntLittle(),
            sequence = (readByte().toInt() + 128) and 0xFF,
        )
    }

    // WORLDLIST_FETCH
    clientProt<RequestWorldList>(opcode = 54, size = 4) {
        RequestWorldList(worldlistVersion = readInt())
    }

    // FRIENDLIST_DEL
    clientProt<FriendListDel>(opcode = 70, size = ProtSize.VarByte) {
        FriendListDel(displayName = readRSString())
    }

    // IGNORELIST_ADD — 948 op 80 (varByte). CONFIRMED via get_xrefs_to(entry 0x015d39b0)=
    // SendIgnorelistAdd @ 0x0030f4b0 + self-id strings "Your ignore list is full". The prior
    // CSV mapping (op 81 / varShort) was WRONG (+1 drift); op 81 has no dedicated emitter and
    // a varShort decoder there would desync. SendIgnorelistAdd writes a single readRSString
    // (varByte name).
    clientProt<IgnoreListAdd>(opcode = 80, size = ProtSize.VarByte) {
        IgnoreListAdd(displayName = readRSString())
    }

    // FRIENDLIST_ADD
    clientProt<FriendListAdd>(opcode = 100, size = ProtSize.VarByte) {
        FriendListAdd(displayName = readRSString())
    }

    // CLANCHANNEL_KICKUSER — 948 op 89 (varByte). Official enum name (beta
    // &ClientProt::CLANCHANNEL_KICKUSER). Confirmed via get_xrefs_to(entry 0x015d3920)=
    // SendSocialRequest @ 0x0026aef0 + "That user is not in this channel".
    clientProt<ClanChannelKickUser>(opcode = 89, size = ProtSize.VarByte) {
        ClanChannelKickUser(username = readRSString())
    }

    // RESUME_P_NAMEDIALOG — 948 op 71 (varByte). CONFIRMED via get_xrefs_to(entry 0x015d3a40)=
    // SendResumeNameDialog @ 0x002cff20. Prior CSV mapping (op 72 / varShort) was WRONG (+1
    // drift); SendResumeNameDialog writes a single readRSString (varByte name), op 72 is UNBOUND.
    clientProt<ResumePNameDialog>(opcode = 71, size = ProtSize.VarByte) {
        ResumePNameDialog(name = readRSString())
    }

    // IF_BUTTON1..IF_BUTTON10 — the canonical interface CLICK family (size 8).
    // RESOLVED (2026-06-25 capture): the size-8 click opcodes are dispatched by
    // jag::InterfaceManager::IfButtonXInner @0x002978d0, which indexes the CS2 ProtEntry pointer
    // table @0x01365920 by [option-1]. The full slot order (slot0..slot9) is
    //   127, 103, 92, 45, 30, 68, 43, 21, 13, 23  ==  IF_BUTTON1..IF_BUTTON10  (xref + capture-verified):
    //   opt1->op127, opt2->op103, opt3->op92, opt4->op45, opt5->op30, opt6->op68, opt7->op43,
    //   opt8->op21, opt9->op13, opt10->op23.
    // CORRECTION: the prior mapping had op21=IF_BUTTON10 and treated op13/op23 as unbound. The CS2
    //   table is dense across all 10 slots; op21=IF_BUTTON8, op13=IF_BUTTON9, op23=IF_BUTTON10.
    // 8-byte wire layout: interfaceHash = WriteUInt32LE (readUIntLittle, bytes0-3 LE);
    //   slotId = uShortAddLittle (byte4=lo-128, byte5=hi); itemId = uShortAdd (byte6=hi, byte7=lo-128).
    // buttonId carries the option index (1..10) per opcode.
    // NOTE: op35 is NOT a click — it is IF_PLAYER (use-button-on-player, varByte, the IfButtonXInner
    //   long-path where the component carries a string). Do NOT register IfButton at op35.
    ifButtonClick(opcode = 127, buttonId = 1)
    ifButtonClick(opcode = 103, buttonId = 2)
    ifButtonClick(opcode = 92, buttonId = 3)
    ifButtonClick(opcode = 45, buttonId = 4)
    ifButtonClick(opcode = 30, buttonId = 5)
    ifButtonClick(opcode = 68, buttonId = 6)
    ifButtonClick(opcode = 43, buttonId = 7)
    ifButtonClick(opcode = 21, buttonId = 8)
    ifButtonClick(opcode = 13, buttonId = 9)
    ifButtonClick(opcode = 23, buttonId = 10)

    clientProt<MacOsLobbyHandoff>(opcode = 218, size = MAC_OS_LOBBY_HANDOFF_SIZE) {
        MacOsLobbyHandoff(readByteArray(MAC_OS_LOBBY_HANDOFF_SIZE).findEmbeddedPlayNowClick())
    }

    // MESSAGE_PUBLIC
    clientProt<MessagePublicSend>(
        opcodes = intArrayOf(124),
        size = ProtSize.VarByte
    ) { packetSize ->
        val color = readByte().toInt() and 0xFF
        val effect = readByte().toInt() and 0xFF
        val message = readByteArray(packetSize - 2)
        MessagePublicSend(color = color, effect = effect, message = message)
    }

    // MESSAGE_PRIVATE
    clientProt<MessagePrivateSend>(
        opcodes = intArrayOf(38),
        size = ProtSize.VarShort
    ) { packetSize ->
        val toDisplayName = readRSString()
        val nameLen = toDisplayName.length + 1
        val message = readByteArray(packetSize - nameLen)
        MessagePrivateSend(toDisplayName = toDisplayName, message = message)
    }
}

/**
 * Registers a size-8 IF_BUTTON click decoder for [opcode], stamping [buttonId] as the option index.
 * 8-byte layout (jag::InterfaceManager::IfButtonXInner short path):
 *   interfaceHash = readUIntLittle  (WriteUInt32LE, bytes 0-3 little-endian)
 *   slotId        = readUShortAddLittle (byte4 = lo-128, byte5 = hi)
 *   itemId        = readUShortAdd       (byte6 = hi, byte7 = lo-128)
 */
private fun Codec.ifButtonClick(opcode: Int, buttonId: Int) {
    clientProt<IfButton>(opcode = opcode, size = 8) {
        val interfaceHash = readUIntLittle()
        val slotId = readUShortAddLittle()
        val itemId = readUShortAdd()
        IfButton(buttonId = buttonId, interfaceHash = interfaceHash, slotId = slotId, itemId = itemId)
    }
}

private const val MAC_OS_LOBBY_HANDOFF_SIZE = 70
private const val PLAY_NOW_INTERFACE_HASH = (906 shl 16) or 81

private fun ByteArray.findEmbeddedPlayNowClick(): IfButton? {
    for (offset in 0..size - 8) {
        val interfaceHash = readIntLittle(offset)
        if (interfaceHash == PLAY_NOW_INTERFACE_HASH) {
            return IfButton(
                buttonId = 1,
                interfaceHash = interfaceHash,
                slotId = readUShortAddLittle(offset + 4),
                itemId = readUShortAdd(offset + 6),
            )
        }
    }
    return null
}

private fun ByteArray.readIntLittle(offset: Int): Int =
    (this[offset].toInt() and 0xff) or
        ((this[offset + 1].toInt() and 0xff) shl 8) or
        ((this[offset + 2].toInt() and 0xff) shl 16) or
        ((this[offset + 3].toInt() and 0xff) shl 24)

private fun ByteArray.readUShortAddLittle(offset: Int): Int =
    ((this[offset].toInt() - 128) and 0xff) or
        ((this[offset + 1].toInt() and 0xff) shl 8)

private fun ByteArray.readUShortAdd(offset: Int): Int =
    ((this[offset].toInt() and 0xff) shl 8) or
        ((this[offset + 1].toInt() - 128) and 0xff)
