package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for interface packets.
 * Wire formats reverse-engineered from live Jagex 947-1 packet capture (2026-03-23).
 */
internal fun Codec.registerRev947ServerCodecsInterface() {
    // IF_SETTOPLEVELINTERFACE (94, 19B) — Ghidra-verified direct top-level interface setter
    // Capture: 00 03 0a 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
    // Layout: [1B unused][1B ifId high][1B ifId low + 0x80][16B zeros]
    serverProt<IfOpenTopLobby>(opcode = 94, size = 19) { out ->
        out.writeByte(0)
        out.writeByte((interfaceId shr 8) and 0xFF)
        out.writeByteAdd(interfaceId and 0xFF)
        out.skip(16)
    }

    // IF_SETPOSITION (8, 23B) — sub-interface open
    // Capture: 00 00 00 00 8b 03 00 00 00 00 00 00 00 00 ff 00 00 00 00 00 2c 03 8a
    // Layout (from capture analysis):
    //   [4B zeros][4B LE subIfId][6B zeros][1B 0xFF][5B zeros][1B parentComp][2B BE parentIfId]
    serverProt<IfOpenSubLobby>(opcode = 8, size = 23) { out ->
        out.skip(4)                                          // bytes 0-3
        out.writeIntLittle(subIfId)                          // bytes 4-7: sub-interface ID
        out.skip(6)                                          // bytes 8-13
        out.writeByte(0xFF)                                  // byte 14
        out.skip(5)                                          // bytes 15-19
        out.writeByte(parentComp)                            // byte 20: parent component
        out.writeShort(parentIfId)                           // bytes 21-22: parent interface (BE)
    }

    // IF_SETEVENTS (35, 12B) — interface event masks
    // Capture: 00 00 00 00 27 00 8b 03 00 01 02 00
    // All 4 entries share: settings=0, comp varies, ifId=907, last4=00 01 02 00
    // Layout: [4B LE zeros][2B LE compId][2B LE ifId][2B fromSlot?][2B settings?]
    serverProt<IfSetEvents>(opcode = 35, size = 12) { out ->
        out.writeIntLittle(0)                                // bytes 0-3: zeros (observed)
        out.writeShortLittle(events.componentId)             // bytes 4-5: component ID
        out.writeShortLittle(events.interfaceId)             // bytes 6-7: interface ID
        out.writeShort(events.fromSlot)                      // bytes 8-9
        out.writeShortLittle(events.settings)                // bytes 10-11: settings (truncated to 16-bit)
    }
}
