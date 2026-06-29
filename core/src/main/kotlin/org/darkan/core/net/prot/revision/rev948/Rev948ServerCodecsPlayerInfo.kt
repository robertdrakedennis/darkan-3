package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoder for PLAYER_INFO.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration":
 *  - 947-3 op 27 → **948 op 22** (varShort), handler @ 0x100043500
 *    (`jag::packethandlers::PlayerInfo::S2C_PLAYER_INFO_OP22`)
 *
 * Current 948-5 Ghidra proof: four bit-packed player-list passes, then the handler advances
 * `packet.pos += 2` before each `DecodePlayerExtendedInfo` call. The outer codec still writes
 * pre-built bit blocks followed by 2-byte length-prefixed ext-info blocks.
 *
 * **NOTE on ext-info bit positions:** the bit positions inside each player's ext-info block
 * DID change in 948 — the world-side builder in `world.../PlayerInfoEncoder.kt` reads from
 * `PendingUpdates` which is keyed by [PlayerUpdateMaskKey] (the interface), and the active
 * codec registers its revision-specific encoders against [PlayerUpdateMaskEncoder]. So when
 * the active revision is 948 the world produces bit-position-correct ext-info bytes via
 * [Rev948PlayerUpdateMaskKey] / `Rev948ServerCodecsUpdateMasks`. The CODEC here doesn't need
 * to know about mask bits — it just concatenates pre-built blocks with the 2B length prefix.
 */
internal fun Codec.registerRev948ServerCodecsPlayerInfo() {
    serverProt<PlayerInfo>(opcode = 22, size = ProtSize.VarShort) { out ->
        out.writeFully(bitBlock)
        for (block in extendedInfo) {
            out.writeShort(block.size)
            out.writeFully(block)
        }
    }
}
