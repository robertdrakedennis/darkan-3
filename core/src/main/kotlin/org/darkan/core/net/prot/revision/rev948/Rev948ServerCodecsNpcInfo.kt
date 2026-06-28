package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoder for NPC_INFO.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration":
 *  - 947-3 op 12 → **948 op 52** (varShort), handler @ 0x1000adba0
 *    (`jag::packethandlers::NpcInfo::S2C_NPC_INFO_OP52`)
 *
 * Current 948-5 Ghidra proof: wrapper calls `DecodeNpcInfo @ 0x100018e00`, which bit-parses
 * existing/new NPC entries and advances `packet.pos += 2` before each `DecodeNpcExtendedInfo`
 * call. The outer codec still writes pre-built bit blocks followed by 2-byte length-prefixed
 * ext-info blocks.
 *
 * **NOTE on ext-info bit positions:** the per-NPC ext-info block bit positions and dispatch
 * order are different in 948 (see [Rev948NpcUpdateMaskKey]). The world-side builder reads from
 * `PendingUpdates` (keyed by the [NpcUpdateMaskKey] interface), so when 948 is active the
 * world produces bit-position-correct ext-info bytes via `Rev948ServerCodecsUpdateMasks`. This
 * codec just concatenates blocks with the 2B length prefix.
 */
internal fun Codec.registerRev948ServerCodecsNpcInfo() {
    serverProt<NpcInfo>(opcode = 52, size = ProtSize.VarShort) { out ->
        out.writeFully(bitBlock)
        for (block in extendedInfo) {
            out.writeShort(block.size)
            out.writeFully(block)
        }
    }
}
