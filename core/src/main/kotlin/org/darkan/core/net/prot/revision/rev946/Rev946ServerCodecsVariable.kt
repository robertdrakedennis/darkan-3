package org.darkan.core.net.prot.revision.rev946

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev946 server encoders for variable packets (varps, varcs, stats).
 * Byte transforms verified from rs2client binary.
 */
internal fun Codec.registerRev946ServerCodecsVariable() {
    // SET_VARP_SMALL (14, 3B): g2(hi+lo-128) id + g1(-128) value
    serverProt<VarpSmall>(opcode = 14, size = 3) { out ->
        out.writeShortAdd(id)
        out.writeByteAdd(value)
    }

    // SET_VARP_INT (124, 6B): g2LE id + g4_alt1 value
    serverProt<VarpLarge>(opcode = 124, size = 6) { out ->
        out.writeShortLittle(id)
        out.writeIntMiddle(value)
    }

    // SET_VARP_LONG (138, 10B): g8 value + g2(hi+lo-128) id
    serverProt<VarpLong>(opcode = 138, size = 10) { out ->
        out.writeLong(value)
        out.writeShortAdd(id)
    }

    // SET_VARC_SMALL_2 (19, 3B): g1(0x80-raw) value + g2BE key
    serverProt<ClientSetVarcSmall>(opcode = 19, size = 3) { out ->
        out.writeByteSubtract(value)
        out.writeShort(id)
    }

    // SET_VARC_INT_2 (12, 6B): g2BE key + g4LE value
    serverProt<ClientSetVarcLarge>(opcode = 12, size = 6) { out ->
        out.writeShort(id)
        out.writeIntLittle(value)
    }

    // UPDATE_STAT (114, 6B): g4_alt1 xp + g1(-128) boosted + g1(negate) statId
    serverProt<UpdateStat>(opcode = 114, size = 6) { out ->
        out.writeIntMiddle(xp)
        out.writeByteAdd(level)
        out.writeByteInverse(skillId)
    }
}
