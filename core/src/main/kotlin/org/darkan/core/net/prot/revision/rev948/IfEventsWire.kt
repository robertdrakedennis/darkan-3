package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.model.IFEvents

/**
 * Rev948 wire decoder for the 12-byte IF_SETEVENTS2 payload (op 35).
 *
 * Wire layout per `docs/net/serverprot/948-research-B-interface-social.md`
 * (handler @ 0x1000a5910, "IF_SETEVENTS2 (op 35, 12B)"):
 * `[0..3] g4_alt2 settings` `[4..5] LE i16 fromSlot (0xFFFF -> -1)`
 * `[6..7] LE i16 toSlot (0xFFFF -> -1)` `[8..11] BE u32 componentHash`
 *
 * g4_alt2 (middle-endian) bytes on the wire are `[shr8, shr0, shr24, shr16]` =
 * `[CC, DD, AA, BB]` for value `AABBCCDD` — the inverse of [world.gregs.voidps.buffer.write.Writer.writeIntMiddle],
 * matching the registered op-35 encoder in Rev948ServerCodecsInterface.
 *
 * NOTE: the 10-byte IF_SETEVENTS variant (op 97) is NOT decoded here — the research doc flags
 * its two g2_alt2 field semantics as ambiguous ("do NOT guess"); add it only once the RE doc
 * resolves the field mapping.
 */
object IfEventsWire {

    fun decode(data: ByteArray, offset: Int = 0): IFEvents {
        fun u8(i: Int) = data[offset + i].toInt() and 0xFF
        // g4_alt2: wire [CC, DD, AA, BB] -> reconstruct AABBCCDD
        val settings = (u8(2) shl 24) or (u8(3) shl 16) or (u8(0) shl 8) or u8(1)
        var fromSlot = u8(4) or (u8(5) shl 8)
        if (fromSlot == 0xFFFF) fromSlot = -1
        var toSlot = u8(6) or (u8(7) shl 8)
        if (toSlot == 0xFFFF) toSlot = -1
        val hash = (u8(8) shl 24) or (u8(9) shl 16) or (u8(10) shl 8) or u8(11)
        val interfaceId = hash ushr 16
        val componentId = hash and 0xFFFF
        return IFEvents(interfaceId, componentId, fromSlot, toSlot, settings)
    }
}
