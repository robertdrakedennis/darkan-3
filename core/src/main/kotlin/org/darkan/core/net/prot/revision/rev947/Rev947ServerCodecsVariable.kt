package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for variable packets (varps, varcs, stats).
 * Byte transforms verified from live Jagex 947-1 packet capture (2026-03-24).
 * ALL id fields are little-endian shorts. Ghidra RE was wrong about BE — capture is truth.
 */
internal fun Codec.registerRev947ServerCodecsVariable() {
    // VARP_SMALL (10, 3B) — capture: b4 00 ff = id=180 LE, value=-1 raw
    serverProt<VarpSmall>(opcode = 10, size = 3) { out ->
        out.writeShortLittle(id)
        out.writeByte(value)
    }

    // VARP_LARGE (111, 6B) — capture: 00 00 20 00 95 04 = middleVal + id LE
    serverProt<VarpLarge>(opcode = 111, size = 6) { out ->
        out.writeIntMiddle(value)
        out.writeShortLittle(id)
    }

    // VARP_LONG (170, 10B) — capture: ff×8 + 90 2e = long(-1) + id=11920 LE
    serverProt<VarpLong>(opcode = 170, size = 10) { out ->
        out.writeLong(value)
        out.writeShortLittle(id)
    }

    // CLIENT_SETVARC_SMALL (1, 3B) — capture: 80 a8 0d = byteSubtract(val) + id LE
    serverProt<ClientSetVarcSmall>(opcode = 1, size = 3) { out ->
        out.writeByteSubtract(value)
        out.writeShortLittle(id)
    }

    // CLIENT_SETVARC_LARGE (112, 6B) — capture: 03 34 cd e4 53 0a = intBE(val) + id LE
    serverProt<ClientSetVarcLarge>(opcode = 112, size = 6) { out ->
        out.writeInt(value)
        out.writeShortLittle(id)
    }

    // CLIENT_SETVARC_STR (67, varByte) — capture: "Yehp\0" 09 4c = string + id LE
    serverProt<ClientSetVarcStr>(opcode = 67, size = ProtSize.VarByte) { out ->
        out.writeRSString(value)
        out.writeShortLittle(id)
    }

    // UPDATE_STAT (66, 6B) — capture: 00 5d 74 a4 25 80 = intBE(xp) + byteSub(level) + byteAdd(skillId)
    serverProt<UpdateStat>(opcode = 66, size = 6) { out ->
        out.writeInt(xp)
        out.writeByteSubtract(level)
        out.writeByteAdd(skillId)
    }
}
