package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoder for PLAYER_INFO (opcode 27, varShort) per
 * `docs/net/serverprot/player-info-947-3.md` §4A and §"Extended-info dispatch".
 *
 * The packet is a contiguous stream of:
 *  1. Pre-built bit block (4 passes — high-res-active, high-res-inactive, low-res-active,
 *     low-res-inactive — built by B6's `PlayerInfoBuilder`). The bit-block is byte-aligned
 *     on entry to the ext-info section: A4 §"Extended-info dispatch" notes the dispatcher
 *     restores byte offset with `(bitOffset + 7) >> 3` before extended-info processing.
 *  2. For each player whose hasExtendedInfo bit was set during the bit passes, the
 *     dispatcher does `*(packet+0x18) += 2; ProcessExtendedInfo(...)` — the `+= 2` skip
 *     corresponds to a 2-byte BE length header per A4 §"Extended-info dispatch". The
 *     server writes this length header followed by the extended-info bytes.
 *
 * The data class carries pre-encoded blobs; this codec only concatenates them with the
 * 2-byte BE length prefix. Building those blobs lives in B6.
 */
internal fun Codec.registerRev947ServerCodecsPlayerInfo() {
    serverProt<PlayerInfo>(opcode = 27, size = ProtSize.VarShort) { out ->
        out.writeFully(bitBlock)
        for (block in extendedInfo) {
            out.writeShort(block.size)
            out.writeFully(block)
        }
    }
}
