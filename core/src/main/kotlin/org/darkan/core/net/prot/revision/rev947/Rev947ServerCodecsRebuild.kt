package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for the rebuild packet family.
 *
 * Byte layouts derived from rs2client.947-3 decompilation per
 * `docs/net/serverprot/rebuild-947-3.md`:
 * - opcode 90 = REBUILD_NORMAL simple form (used for world login)
 * - opcode 172 = REBUILD_REGION (multi-scene grid for instanced regions)
 * - opcode 188 = REBUILD_WORLDENTITY (triple-nested -1-terminated XTEA tree)
 *
 * REBUILD_REGION and REBUILD_WORLDENTITY are emitted as opaque ByteArray payloads from the
 * caller — the per-scene grid (op 172) and the level/regionX/regionY tree (op 188) are
 * heavy enough that a structured API will be added in a follow-up once a downstream
 * consumer requires it.
 */
internal fun Codec.registerRev947ServerCodecsRebuild() {
    // REBUILD_NORMAL (op 90, varShort) — A2 §3 "simple form" for world login.
    // Wire (16 bytes total, distinct from the 948 18-byte form):
    //   g2 (BE) chunkX; g1 forceRefresh; g2LE regionLow; g1 magic=0x7B;
    //   g2 (BE) chunkZ; g4 (BE) packedCoordA; g4 (BE) packedCoordB.
    // Magic byte MUST be exactly 0x7B or the handler returns PacketError::MESSAGE.
    //
    // NOTE: RebuildNormalSimple carries the OURS'/948 field names (the active build is 948-5). This
    // legacy 947-3 encoder maps them onto the older 16-byte layout — wire bytes are UNCHANGED from
    // the original 947-3 RE; only the source field identifiers were remapped to ours' class:
    //   zoneX        -> chunkX (BE u16)        (was playerCoordX)
    //   zoneZ        -> chunkZ (BE u16)        (was playerCoordY)
    //   regionLow    -> regionLow (LE u16)     (was worldAreaTypeId; ours keeps a `regionLow` slot
    //                                           defaulting to 0, so 948 construction is unaffected)
    //   packedCoordA -> packedCoordA (BE u32)  (was srcPackedCoord1)
    //   packedCoordB -> packedCoordB (BE u32)  (was srcPackedCoord2)
    // 947 has no cameraRotation/sceneRootId slot at this position and always force-refreshes on
    // login; ours' cameraRotation/sceneRootId/rebuildPrefix fields are intentionally ignored here.
    serverProt<RebuildNormalSimple>(opcode = 90, size = ProtSize.VarShort) { out ->
        out.writeShort(zoneX)                   // chunkX (BE u16)
        out.writeByte(1)                        // forceRefresh — always refresh on login
        out.writeShortLittle(regionLow)         // regionLow (LE u16)
        out.writeByte(0x7B)                     // magic (947 = 0x7B)
        out.writeShort(zoneZ)                   // chunkZ (BE u16)
        out.writeInt(packedCoordA)              // packedCoordA (BE u32)
        out.writeInt(packedCoordB)              // packedCoordB (BE u32)
    }

    // Multi-scene grid rebuild (op 172, varShort) — A2 §2 for INSTANCED regions.
    // Opaque payload until a structured API is needed; the caller is expected to assemble:
    //   g1 sceneCount; for each scene: g4_alt1 seed + descriptor lists + N×M XTEA grid.
    // Data class renamed RebuildRegion -> RebuildNormalMultiScene (see ServerProt.kt) to avoid a
    // display-name collision with the canonical op83 REBUILD_REGION in rev948.
    serverProt<RebuildNormalMultiScene>(opcode = 172, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }

    // REBUILD_WORLDENTITY (op 188, varShort) — A2 §4 triple-nested -1-terminated XTEA stream.
    // Opaque payload until a structured API is needed; minimum 1-byte form is `0xFF`.
    serverProt<RebuildWorldEntity>(opcode = 188, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }
}
