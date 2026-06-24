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
    // REBUILD_NORMAL_SIMPLE (op 81 in 948, was op 90 in 947-3) — the world-login scene build.
    // Handler jag::packethandlers::ClientState::REBUILD_NORMAL_SIMPLE @ 0x001daa70.
    //
    // **Shape B (docs/protocol/world-bootstrap-948.md §"⚠️ CORRECTION (2026-06-23)"): the
    // WORLD-LOGIN body is `[5119-byte GPI prefix][18-byte coordinate header]`.** On world entry the
    // client sets worldState+0x49=1, so the handler UNCONDITIONALLY runs the gBit-based GPI-prefix
    // parser (FUN_00b254a0, NO bounds check) BEFORE the coord read: it consumes 30 + 2046×20 =
    // 40950 bits = 5119 bytes and advances the packet cursor, so the handler then reads the coord
    // header at `position == 5119` (magic 0x85 lands at body offset 5122). The prefix is generated
    // from local state by world's `Op81GpiPrefix` (local player's 30-bit tile == the coord-header
    // centre-zone tile; the other 2046 slots all-zero = absent, fine for a solo spawn). Shipping
    // the bare 18-byte header (Shape A) is FATAL: the parser still runs (flag set), over-reads 5101
    // bytes of heap, places the player at a garbage tile, and reads the header out-of-bounds →
    // magic ≠ 0x85 → op81 aborts BEFORE the BuildArea alloc and BEFORE ProcessCameraReset → black
    // screen. `rebuildPrefix` is written first (below) then the 18-byte header; the world bootstrap
    // passes the generated 5119-byte prefix, making the body 5119 + 18 = 5137. (`rebuildPrefix`
    // still defaults to empty for non-world callers/tests that want the bare header.)
    //   +0  u8   ignored filler            (read cursor bumps past it; handler never reads it)
    //   +1  u8   centreZoneZ low           ┐ Z reconstructs as lo + hi*0x100 → LE u16 (may exceed 255)
    //   +2  u8   centreZoneZ high          ┘
    //   +3  u8   magic == 0x85             (CMP / ADD -0x7b; abort if != 0x85)
    //   +4  u16  centreZoneX               BE (MOVZX word then ROL 8)
    //   +6  u8   cameraRotation            writeByteAdd: wire = (value + 0x80) & 0xFF
    //   +7  u8   ignored filler            (second skipped byte the §13 table omitted)
    //   +8  u16  targetWorldId             BE; 0 for a normal non-instanced login
    //   +10 u32  packedCoordA              BE → DecodePackedCoord SW corner {minRegionX, minRegionZ}
    //   +14 u32  packedCoordB              BE → DecodePackedCoord NE corner {maxRegionX, maxRegionZ}
    //
    // packedCoordA/B are the two CORNERS of the build-area map-square grid, as TILE coords
    // (hi14 = X-tile, lo14 = Z-tile); the client `>>6`s each field to a REGION and allocates a
    // (maxRegion-minRegion+1)² grid it JS5-pulls index-5 map groups into. They are NOT origin+span
    // and NOT zone<<6. Bounds MUST be non-inverted (min ≤ max) and contain the spawn region, else
    // the grid is empty and the client requests zero map squares (the black screen). The world
    // BuildArea service computes them; see docs/protocol/packed-coord-buildarea-948.md.
    //
    // The VarShort length is rebuildPrefix.size + 18 (= 5119 + 18 = 5137 for Shape B world login).
    serverProt<RebuildNormalSimple>(opcode = 81, size = ProtSize.VarShort) { out ->
        out.writeFully(rebuildPrefix)
        out.writeByte(0)                         // +0 ignored filler
        out.writeByte(zoneZ and 0xFF)            // +1 centreZoneZ low
        out.writeByte((zoneZ ushr 8) and 0xFF)   // +2 centreZoneZ high (LE u16 with +1)
        out.writeByte(0x85)                      // +3 magic
        out.writeShort(zoneX)                    // +4 centreZoneX (BE)
        out.writeByteAdd(cameraRotation)         // +6 cameraRotation (wire = value + 0x80)
        out.writeByte(0)                         // +7 ignored filler
        out.writeShort(targetWorldId)            // +8 targetWorldId (BE)
        out.writeInt(packedCoordA)               // +10 SW-corner packed coord (BE)
        out.writeInt(packedCoordB)               // +14 NE-corner packed coord (BE)
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
