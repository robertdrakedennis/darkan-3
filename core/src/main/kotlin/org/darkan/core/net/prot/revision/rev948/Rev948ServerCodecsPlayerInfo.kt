package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoder for PLAYER_INFO.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration":
 *  - 947-3 op 27 → **948 op 22** (varShort), handler @ 0x00161720 (`jag::PlayerList::ProcessPlayerInfo`)
 *
 * The packet wire format is byte-equivalent to 947-3 — the codec body is identical except for
 * the opcode number. The internal bit-block + 2-byte length-prefixed ext-info blocks structure
 * is the same.
 *
 * **NOTE on ext-info bit positions:** the bit positions inside each player's ext-info block
 * DID change in 948 — the world-side builder in `world.../PlayerInfoBuilder.kt` reads from
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
