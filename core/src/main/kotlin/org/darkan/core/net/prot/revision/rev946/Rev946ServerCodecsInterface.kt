package org.darkan.core.net.prot.revision.rev946

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev946 server encoders for interface packets.
 * Wire formats verified from rs2client binary and Jagex live capture.
 */
internal fun Codec.registerRev946ServerCodecsInterface() {
    // IF_SETGRAPHIC used as lobby top-open (opcode 126, 19B fixed)
    // Wire: [12B zeros][2B LE ifId][5B zeros]
    serverProt<IfOpenTopLobby>(opcode = 126, size = 19) { out ->
        out.skip(12)
        out.writeShortLittle(interfaceId)
        out.skip(5)
    }

    // IF_SETPOSITION used as lobby sub-open (opcode 38, 23B fixed)
    // Wire: [4B zeros][4B LE componentHash][8B zeros][1B 0x7F][2B LE subIfId][4B zeros]
    serverProt<IfOpenSubLobby>(opcode = 38, size = 23) { out ->
        val hash = (parentIfId shl 16) or parentComp
        out.skip(4)
        out.writeIntLittle(hash)
        out.skip(8)
        out.writeByte(0x7F)
        out.writeShortLittle(subIfId)
        out.skip(4)
    }
}
