package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for the rebuild packet family.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration",
 * all three rebuild packets MOVED to new opcodes in 948:
 *
 *  - 947-3 op 90 (REBUILD_NORMAL simple form) → **948 op 81** — handler @ 0x001daa70.
 *    The body was RE-LAID-OUT in 948: an 18-byte fixed body with magic 0x85 at offset 3 (was a
 *    16-byte body with magic 0x7B in 947-3). Full layout + decompile evidence on the encoder below.
 *  - 947-3 op 172 (REBUILD_REGION — multi-scene grid) → **948 op 199** — handler @ 0x001203e0
 *    (multi-entry WorldList builder; semantically the multi-scene form). The doc keeps the
 *    structural name REBUILD_REGION for the data class.
 *  - 947-3 op 188 (REBUILD_WORLDENTITY) → **948 op 186** — handler @ 0x000efd80
 *    (0xff-terminator loop pattern confirmed).
 */
internal fun Codec.registerRev948ServerCodecsRebuild() {
    // REBUILD_NORMAL (op 81 in 948, was op 90 in 947-3) — simple single-scene world-login form.
    // Handler jag::packethandlers::ClientState::REBUILD_NORMAL @ 0x001daa70 (decompile-verified
    // 948-5), beta oracle = ClientState lambda#5 @ librs2client.so 0x548fa0 (CreateWorldFromWorldArea).
    //
    // FIXED 18-byte body (NOT the 16-byte 947-3 layout). Handler read order (body offset -> field):
    //   [+0]       reserved0   — read, value discarded. Send 0.
    //   [+1,+2]    playerCoordX — LITTLE-endian u16 (lo=[+1], hi=[+2]).  writeShortLittle == LE.
    //   [+3]       magic       — MUST be 0x85 (asm `if (cVar4 != -0x7b) return PacketError`; -0x7b == 0x85).
    //   [+4,+5]    playerCoordY — BIG-endian u16.  writeShort == BE.
    //   [+6]       cameraAngle — client stores (wireByte + 0x80) & 0xff, so to send angle A write
    //              (A - 0x80) & 0xff (A=0 -> 0x80).
    //   [+7]       reserved7   — read, discarded. Send 0.
    //   [+8,+9]    worldAreaTypeId — BIG-endian u16. WorldAreaType cache config id; only consulted
    //              when the world-entity build manager (Client+0xca6, state field +0x3c == 4) is
    //              active. For a normal overworld build the gate is false and this is IGNORED — the
    //              scene is built from the default world area (NullConfigType @0x015d4d60) using the
    //              two packed coords. See WorldServer.sendWorldLoginCore.
    //   [+10..13]  srcPackedCoord1 — BIG-endian u32 (BuildArea::DecodePackedCoord).
    //   [+14..17]  srcPackedCoord2 — BIG-endian u32 (BuildArea::DecodePackedCoord).
    // Packed coords: (plane << 28) | (y << 14) | x  (tile units; 0xFFFFFFFF = unset). Client uses
    // x>>6 / y>>6 (map-square units) to instance the scene. NO XTEA / map payload in this packet.
    serverProt<RebuildNormalSimple>(opcode = 81, size = ProtSize.VarShort) { out ->
        out.writeByte(0)                              // [+0]    reserved0
        out.writeShortLittle(playerCoordX)            // [+1,+2] playerCoordX (LE u16)
        out.writeByte(0x85)                           // [+3]    magic — MANDATORY
        out.writeShort(playerCoordY)                  // [+4,+5] playerCoordY (BE u16)
        out.writeByte((cameraAngle - 0x80) and 0xff)  // [+6]    cameraAngle (inverse of client +0x80)
        out.writeByte(0)                              // [+7]    reserved7
        out.writeShort(worldAreaTypeId)               // [+8,+9] worldAreaTypeId (BE u16)
        out.writeInt(srcPackedCoord1)                 // [+10..13] srcPackedCoord1 (BE u32)
        out.writeInt(srcPackedCoord2)                 // [+14..17] srcPackedCoord2 (BE u32)
    }

    // Multi-scene grid rebuild (op 199 in 948, was op 172 in 947-3) — for INSTANCED regions.
    // Opaque payload until a structured API is needed. Binary-confirmed (RegisterAll + handler
    // ClientState::REBUILD_NORMAL @0x001daa70): op199 is the multi-scene REBUILD_NORMAL form; the
    // canonical op83 REBUILD_REGION (REBUILD_REGION_ALT @0x001df100) is a DIFFERENT bit-packed
    // handler and is NOT encoded here. Data class renamed RebuildRegion -> RebuildNormalMultiScene
    // to avoid a display-name collision with op83's canonical REBUILD_REGION. Encoder STAYS at op199.
    serverProt<RebuildNormalMultiScene>(opcode = 199, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }

    // REBUILD_WORLDENTITY (op 186 in 948, was op 188 in 947-3) — triple-nested -1-terminated XTEA stream.
    // Opaque payload until a structured API is needed; minimum 1-byte form is `0xFF`.
    serverProt<RebuildWorldEntity>(opcode = 186, size = ProtSize.VarShort) { out ->
        out.writeFully(payload)
    }
}
