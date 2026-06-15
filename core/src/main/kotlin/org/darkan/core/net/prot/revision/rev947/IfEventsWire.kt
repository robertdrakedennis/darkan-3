package org.darkan.core.net.prot.revision.rev947

import org.darkan.core.model.IFEvents

/**
 * Rev947 wire decoder for the 12-byte IF_SETEVENTS payload.
 *
 * Wire layout (rev 947, verified against rs2client 947 decompilation):
 * `[0..3] LE settings` `[4..5] LE fromSlot (0xFFFF -> -1)` `[6..9] inverse-middle componentHash`
 * `[10..11] (byte+0x80) toSlotLo, toSlotHi (0xFFFF -> -1)`
 *
 * The componentHash bytes are produced by writeIntInverseMiddle, i.e. on the wire as
 * `[shr16, shr24, shr0, shr8]` = `[CC, AA, DD, BB]` for value `AABBCCDD`.
 */
object IfEventsWire {

    fun decode(data: ByteArray, offset: Int = 0): IFEvents {
        fun u8(i: Int) = data[offset + i].toInt() and 0xFF
        val settings = u8(0) or (u8(1) shl 8) or (u8(2) shl 16) or (u8(3) shl 24)
        var fromSlot = u8(4) or (u8(5) shl 8)
        if (fromSlot == 0xFFFF) fromSlot = -1
        // writeIntInverseMiddle writes [shr16, shr24, shr0, shr8] = [CC, AA, DD, BB]
        // bytes [6]=CC [7]=AA [8]=DD [9]=BB -> reconstruct AABBCCDD
        val hash = (u8(7) shl 24) or (u8(6) shl 16) or (u8(9) shl 8) or u8(8)
        val interfaceId = hash ushr 16
        val componentId = hash and 0xFFFF
        var toSlot = ((u8(10) + 0x80) and 0xFF) or (u8(11) shl 8)
        if (toSlot == 0xFFFF) toSlot = -1
        return IFEvents(interfaceId, componentId, fromSlot, toSlot, settings)
    }
}
