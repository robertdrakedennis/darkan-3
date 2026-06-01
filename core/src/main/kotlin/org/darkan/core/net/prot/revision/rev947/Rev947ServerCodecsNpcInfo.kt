package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoder for NPC_INFO (opcode 12, varShort) per
 * `docs/net/serverprot/npc-info-947-3.md` §"Phase 3 — Extended info".
 *
 * Mirrors `PLAYER_INFO`'s wire format: bit block + per-NPC extended-info blocks where each
 * block is prefixed by a 2-byte BE length (A5 §"Phase 3" notes the dispatcher does
 * `*(packet+0x18) += 2` before each `ProcessExtendedInfoNPC` call — the 2-byte stride is
 * the block-length header used for stream resynchronisation).
 *
 * Builder ownership: per A5 §"Phase 1/2/3", the bit block AND the ext-info bytes are
 * pre-built by B6's `NpcInfoBuilder`; this codec only serialises the supplied DTO.
 */
internal fun Codec.registerRev947ServerCodecsNpcInfo() {
    serverProt<NpcInfo>(opcode = 12, size = ProtSize.VarShort) { out ->
        out.writeFully(bitBlock)
        for (block in extendedInfo) {
            out.writeShort(block.size)
            out.writeFully(block)
        }
    }
}
