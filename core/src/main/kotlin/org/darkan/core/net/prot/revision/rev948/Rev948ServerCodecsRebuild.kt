package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for the rebuild packet family.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration",
 * all three rebuild packets MOVED to new opcodes in 948 but their wire formats are
 * byte-equivalent to their 947-3 ancestors (verified by decompile of each 948 handler):
 *
 *  - 947-3 op 90 (REBUILD_NORMAL simple form) → **948 op 81** — handler @ 0x001da8b0
 *    (0x7B magic at offset 3 confirmed in 948 decompile).
 *  - 947-3 op 172 (REBUILD_REGION — multi-scene grid) → **948 op 199** — handler @ 0x00120260
 *    (renamed to "REBUILD_NORMAL" in 948 stub names but semantically the same multi-scene
 *    grid form). The doc keeps the structural name REBUILD_REGION for the data class.
 *  - 947-3 op 188 (REBUILD_WORLDENTITY) → **948 op 186** — handler @ 0x000efd80
 *    (0xff-terminator loop pattern confirmed).
 *
 * Body encoding is unchanged vs `Rev947ServerCodecsRebuild.kt` — only the opcode numbers differ.
 */
internal fun Codec.registerRev948ServerCodecsRebuild() {
    // REBUILD_NORMAL_SIMPLE (op 81 in 948, was op 90 in 947-3) — world-login form.
    // Handler jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE @ 0x001da8b0 (asm-verified).
    //
    // TWO confirmed changes vs 947-3, plus a header REORGANISATION:
    //  1) MAGIC BYTE CHANGED 0x7B -> 0x85. asm @ 0x001da980: `CMP R14B,0x85; JZ ok` — the byte at
    //     body offset +3 MUST equal 0x85 or the handler returns PacketError (DAT_015d35c0). FIXED below.
    //  2) Field layout reorganised. asm read order (body offset -> field):
    //       [+0]       leading byte, CONSUMED but never read (send 0)
    //       [+1,+2]    coordX as LITTLE-endian u16  (lo=[+1], hi=[+2])
    //       [+3]       magic == 0x85
    //       [+4,+5]    coordY as BIG-endian u16
    //       [+6]       level (byteAdd: read = byte-0x80)
    //       [+7]       CONSUMED but never read (send 0)
    //       [+8,+9]    region/plane key as BIG-endian u16
    //       [+10..13]  packedCoordA BE u32 (DecodePackedCoord)
    //       [+14..17]  packedCoordB BE u32 (DecodePackedCoord)
    //     => 18 bytes, NOT the 16-byte 947-3 layout.
    //
    // The existing RebuildNormalSimple data class (chunkX, forceRefresh, regionLow, chunkZ,
    // packedCoordA, packedCoordB) does NOT cleanly map to this reorganised header (e.g. there is
    // no `forceRefresh` byte here; there are two ignored bytes; coordX is LE while coordY is BE).
    // Only the magic-byte fix is applied below. The full re-mapping needs a 948 live capture to
    // bind the data-class fields to the [+1/+2]=X(LE) / [+4/+5]=Y(BE) / [+8/+9]=key slots before
    // production world-login. Until then the body order remains the 947-3 placeholder.
    // TODO(948 world-login): re-map fields per the asm offsets above + verify against capture.
    serverProt<RebuildNormalSimple>(opcode = 81, size = ProtSize.VarShort) { out ->
        out.writeShort(chunkX)
        out.writeByte(if (forceRefresh) 1 else 0)
        out.writeShortLittle(regionLow)
        out.writeByte(0x85)
        out.writeShort(chunkZ)
        out.writeInt(packedCoordA)
        out.writeInt(packedCoordB)
    }

    // REBUILD_REGION (op 199 in 948, was op 172 in 947-3) — multi-scene grid for INSTANCED regions.
    // Opaque payload until a structured API is needed. Note: 948 stub names call this REBUILD_NORMAL
    // but the Kotlin data class keeps the structural name RebuildRegion.
    serverProt<RebuildRegion>(opcode = 199, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }

    // REBUILD_WORLDENTITY (op 186 in 948, was op 188 in 947-3) — triple-nested -1-terminated XTEA stream.
    // Opaque payload until a structured API is needed; minimum 1-byte form is `0xFF`.
    serverProt<RebuildWorldEntity>(opcode = 186, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }
}
