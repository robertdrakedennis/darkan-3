package org.darkan.lobby.server.packet.encoders

import org.darkan.core.net.packet.ServerPacketEncoder
import org.darkan.core.net.prot.ServerProt
import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Opens the lobby top-level interface via opcode 126 (IF_SETGRAPHIC, 19B).
 *
 * Wire format: [12B zeros][2B LE ifId][5B zeros]
 * Verified from Jagex live capture — lobby uses this opcode rather than IF_OPENTOP (207, 2B).
 */
class IfOpenTopLobbyEncoder(private val ifId: Int) :
    ServerPacketEncoder(ServerProt.IF_SETGRAPHIC) {
    override fun encodeBody(buf: BufferWriter) {
        buf.skip(12)
        buf.writeShortLittle(ifId)
        buf.skip(5)
    }
}

/**
 * Opens a sub-interface on a parent component via opcode 38 (IF_SETPOSITION, 23B).
 *
 * Wire format:
 *   [4B zeros][4B g4LE componentHash][8B zeros][1B 0x7F][2B g2LE subIfId][4B zeros]
 *
 * componentHash = (parentIfId << 16) | parentComponent
 * Verified from Jagex live capture and rs2client handler at 0x226e00.
 */
class IfOpenSubEncoder(
    private val parentIfId: Int,
    private val parentComponent: Int,
    private val subIfId: Int
) : ServerPacketEncoder(ServerProt.IF_SETPOSITION) {
    override fun encodeBody(buf: BufferWriter) {
        val componentHash = (parentIfId shl 16) or parentComponent
        buf.skip(4)
        buf.writeIntLittle(componentHash)
        buf.skip(8)
        buf.writeByte(0x7F)
        buf.writeShortLittle(subIfId)
        buf.skip(4)
    }
}
