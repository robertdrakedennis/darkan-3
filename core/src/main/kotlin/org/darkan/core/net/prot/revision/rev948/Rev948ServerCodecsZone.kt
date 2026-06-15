package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for zone updates.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration"
 * and §4, all frame and sub-packet opcodes MOVED in 948:
 *
 * Frame opcodes:
 *  - UPDATE_ZONE_FULL_FOLLOWS:       947-3 op 18 → **948 op 78** (3B, handler @ 0x000f9510)
 *  - UPDATE_ZONE_PARTIAL_FOLLOWS:    947-3 op 57 → **948 op 41** (3B, handler @ 0x000ef150)
 *  - UPDATE_ZONE_PARTIAL_ENCLOSED:   947-3 op 126 → **948 op 76** (varShort, handler @ 0x000eefc0)
 *
 * Standalone sub-packet opcodes (all M-confidence per Phase 1):
 *  - LOC_ADD:                        947-3 op 79 → **948 op 90** (varByte, @ 0x00139080)
 *  - LOC_DEL:                        947-3 op 37 → **948 op 16** (2B, @ 0x001382d0)
 *  - LOC_CUSTOMISE:                  947-3 op 41 → **948 op 50** (varByte, @ 0x0013f5f0)
 *  - LOC_PREFETCH:                   947-3 op 51 → **948 op 6** (7B, @ 0x00138110)
 *  - LOC_ANIM_SPECIFIC:              947-3 op 56 → **948 op 21** (10B, @ 0x00118ef0)
 *  - LOC_MERGE:                      947-3 op 197 → **948 op 170** (5B, @ 0x000eeba0)
 *  - OBJ_ADD:                        947-3 op 38 → **948 op 46** (5B, @ 0x00119240)
 *  - OBJ_DEL:                        947-3 op 42 → **948 op 107** (3B, @ 0x00152b00)
 *  - OBJ_COUNT:                      947-3 op 60 → **948 op 125** (7B, @ 0x001190d0)
 *  - OBJ_REVEAL:                     947-3 op 20 → **948 op 71** (7B, @ 0x000f37b0)
 *  - MAP_ANIM:                       947-3 op 62 → **948 op 113** (11B, @ 0x00157a50)
 *  - MAP_ANIM_SPECIFIC:              947-3 op 145 → **948 op 183** (14B, @ 0x001575c0)
 *  - MAP_PROJANIM:                   947-3 op 47 → **948 op 65** (20B, @ 0x000f2c30)
 *  - MAP_PROJANIM_HALT:              947-3 op 199 → **948 op 164** (28B, @ 0x000f1bc0)
 *  - PROJANIM_SPECIFIC:              947-3 op 196 → **948 op 151** (21B, @ 0x000f2900)
 *  - PROJANIM_SPECIFIC_HALT:         947-3 op 192 → **948 op 177** (29B, @ 0x000f1840)
 *  - SOUND_AREA:                     947-3 op 167 → **948 op 168** (varByte, @ 0x00151c10)
 *
 * **IMPORTANT byte-format flag from delta doc:** UPDATE_ZONE_PARTIAL_FOLLOWS (948 op 41)'s
 * 3-byte zone header is "slightly reordered" vs 947-3 per the delta doc's §2 note. The exact
 * reorder was NOT extracted in Phase 1 — a focused decompile of `@ 0x000ef150` is needed
 * before relying on this encoder for production traffic. The encoder below uses the 947-3
 * order as a starting point; flag this as TODO for follow-up RE.
 *
 * For all other zone packets the byte layouts are presumed byte-equivalent to their 947-3
 * counterparts (the Phase 1 walk did not flag transform changes for them). Cross-verify
 * against captures before production use.
 */
internal fun Codec.registerRev948ServerCodecsZone() {

    // -----------------------------------------------------------------------
    // Frame packets
    // -----------------------------------------------------------------------

    // UPDATE_ZONE_FULL_FOLLOWS (op 78, 3B) — handler @ 0x000f9510 (asm-verified).
    //   Wire: [+0]=level(byteAdd), [+1]=zoneY(byteSubtract), [+2]=zoneX(raw signed byte).
    //   Handler: DAT_a8=(byte+0x80)=level ; DAT_b0=base60c+(char)(-0x80-byte)*8=zoneY ;
    //            DAT_ac=base608+byte*8=zoneX.
    //   FIXED from prior placeholder (was zoneY,zoneX,byteAdd level — wrong order+transforms).
    serverProt<UpdateZoneFullFollowsV2>(opcode = 78, size = 3) { out ->
        out.writeByteAdd(level)
        out.writeByteSubtract(zoneY)
        out.writeByte(zoneX)
    }

    // UPDATE_ZONE_PARTIAL_FOLLOWS (op 41, 3B) — handler jag::packethandlers::ZoneUpdates::
    //   UPDATE_ZONE_PARTIAL_FOLLOWS @ 0x000ef150 (asm-verified — resolves the prior TODO).
    //   Wire: [+0]=zoneX(raw unsigned byte), [+1]=level(byteAdd), [+2]=zoneY(raw signed byte).
    //   Handler: DAT_ac=base608-0x400+byte*8=zoneX ; DAT_a8=(byte+0x80)=level ;
    //            DAT_b0=base60c+(char)byte*8=zoneY.
    //   The "3-byte header reorder" flagged by the delta doc is: level moved to the MIDDLE byte
    //   (it is NOT last). zoneX raw first, zoneY raw last.
    serverProt<UpdateZonePartialFollows>(opcode = 41, size = 3) { out ->
        out.writeByte(zoneX)
        out.writeByteAdd(level)
        out.writeByte(zoneY)
    }

    // UPDATE_ZONE_PARTIAL_ENCLOSED (op 76, varShort) — handler @ 0x000eefc0 (asm-verified).
    //   Header wire: [+0]=level(byteInverse), [+1]=zoneY(raw signed byte), [+2]=zoneX(byteSubtract).
    //   Handler: DAT_a8=(-byte)&0xff=level ; DAT_b0=base60c+(char)byte*8=zoneY ;
    //            DAT_ac=base608+(char)(-0x80-byte)*8=zoneX.
    //   Then loops sub-opcodes via g_zoneSubProtVector @ DAT_015d4580 (sub-op>0x11 => error).
    //   FIXED from prior placeholder (byte1/byte2 transforms were wrong).
    serverProt<UpdateZonePartialEnclosed>(opcode = 76, size = ProtSize.VarShort) { out ->
        out.writeByteInverse(level)
        out.writeByte(zoneY)
        out.writeByteSubtract(zoneX)
        if (subPackets.isNotEmpty()) {
            // The 948 enclosed sub-opcode table (g_zoneSubProtVector @ DAT_015d4580) has not
            // been RE'd, so sub-packet payloads CANNOT be encoded yet. Dropping them silently
            // would lose zone state — warn loudly until the table is documented.
            logWarn(
                "UPDATE_ZONE_PARTIAL_ENCLOSED (op 76) dropped ${subPackets.size} sub-packet(s) — " +
                    "948 enclosed sub-opcode table not yet RE'd; only the 3-byte zone header was sent. " +
                    "TODO: document g_zoneSubProtVector in docs/net/serverprot/ and implement sub-packet encoding."
            )
        }
    }

    // -----------------------------------------------------------------------
    // Standalone sub-packet encoders (presumed byte-equivalent to 947-3)
    // -----------------------------------------------------------------------

    // LOC_ADD (op 90 in 948, was op 79). Wire: g1_neg packedCoord; g4_alt1 locId; g1 shapeFlags.
    serverProt<LocAdd>(opcode = 90, size = ProtSize.VarByte) { out ->
        out.writeByteInverse(packedCoord)
        out.writeIntLittle(locId)
        out.writeByte(shapeFlags)
    }

    // LOC_DEL (op 16, 2B). Wire: g1s shapeFlags_signed; g1_sub128 packedCoord.
    serverProt<LocDel>(opcode = 16, size = 2) { out ->
        out.writeByte(shapeFlags)
        out.writeByteInverse(packedCoord + 128)
    }

    // LOC_CUSTOMISE (op 50, varByte). Opaque payload.
    serverProt<LocCustomise>(opcode = 50, size = ProtSize.VarByte) { out ->
        out.writeFully(payload)
    }

    // LOC_PREFETCH (op 6, 7B).
    // Wire: g1 visTime; g1_neg packedCoord; g1s shapeFlags_signed; g4_alt1 locId.
    serverProt<LocPrefetch>(opcode = 6, size = 7) { out ->
        out.writeByte(visTime)
        out.writeByteInverse(packedCoord)
        out.writeByte(shapeFlags)
        out.writeIntLittle(locId)
    }

    // LOC_ANIM_SPECIFIC (op 21, 10B).
    serverProt<LocAnimSpecific>(opcode = 21, size = 10) { out ->
        out.writeByte(packedCoord)
        out.writeIntLittle(animId)
        out.writeByte(shapeFlags)
        out.writeByte(unknown1)
        out.writeByte(delay)
        out.writeShort(speed)
    }

    // LOC_MERGE (op 170, 5B). Wire: g4_alt1 entityServerIndex; g1 packedCoord+shape.
    serverProt<LocMerge>(opcode = 170, size = 5) { out ->
        out.writeIntLittle(entityServerIndex)
        out.writeByte(packedCoordAndShape)
    }

    // OBJ_ADD (op 46, 5B). Wire: g1 objIdHi; g1+128 objIdLo; g1 countHi; g1 countLo; g1 packedCoord.
    serverProt<ObjAdd>(opcode = 46, size = 5) { out ->
        out.writeByte(objIdHi)
        out.writeByteAdd(objIdLo)
        out.writeByte(countHi)
        out.writeByte(countLo)
        out.writeByte(packedCoord)
    }

    // OBJ_DEL (op 107, 3B). Wire: g1_neg packedCoord; g1+128 objIdLo; g1 objIdHi.
    serverProt<ObjDel>(opcode = 107, size = 3) { out ->
        out.writeByteInverse(packedCoord)
        out.writeByteAdd(objIdLo)
        out.writeByte(objIdHi)
    }

    // OBJ_COUNT (op 125, 7B).
    serverProt<ObjCount>(opcode = 125, size = 7) { out ->
        out.writeShort(playerIndex)
        out.writeByteAdd(objIdLo)
        out.writeByte(objIdHi)
        out.writeByteInverse(packedCoord)
        out.writeShort(count)
    }

    // OBJ_REVEAL (op 71, 7B).
    serverProt<ObjReveal>(opcode = 71, size = 7) { out ->
        out.writeByte(packedCoord)
        out.writeShort(objId)
        out.writeShort(oldCount)
        out.writeShort(newCount)
    }

    // MAP_ANIM (op 113, 11B).
    serverProt<MapAnim>(opcode = 113, size = 11) { out ->
        out.writeByte(packedCoord)
        out.writeShort(entityIdLow)
        out.writeShort(entityIdHigh)
        out.writeShort(heightOffset)
        out.writeByte(angleHeight)
        out.skip(3)
    }

    // MAP_ANIM_SPECIFIC (op 183, 14B).
    serverProt<MapAnimSpecific>(opcode = 183, size = 14) { out ->
        out.writeByte(packedCoord)
        out.writeShort(entityIdLow)
        out.writeShort(entityIdHigh)
        out.writeShort(heightOffset)
        out.writeByte(angleHeight)
        out.writeByte(unknown)
        out.writeMedium(fineOffset)
        out.skip(2)
    }

    // MAP_PROJANIM (op 65, 20B). Opaque payload.
    serverProt<MapProjAnim>(opcode = 65, size = 20) { out ->
        out.writeFully(payload)
    }

    // MAP_PROJANIM_HALT (op 164, 28B). Opaque payload.
    serverProt<MapProjAnimHalt>(opcode = 164, size = 28) { out ->
        out.writeFully(payload)
    }

    // PROJANIM_SPECIFIC (op 151, 21B). Opaque payload.
    serverProt<ProjAnimSpecific>(opcode = 151, size = 21) { out ->
        out.writeFully(payload)
    }

    // PROJANIM_SPECIFIC_HALT (op 177, 29B). Opaque payload.
    serverProt<ProjAnimSpecificHalt>(opcode = 177, size = 29) { out ->
        out.writeFully(payload)
    }

    // SOUND_AREA (op 168, varByte).
    serverProt<SoundArea>(opcode = 168, size = ProtSize.VarByte) { out ->
        out.writeByte(0)
        out.writeByte(packedCoord)
        out.writeShort(soundId)
        out.writeByte(volume)
        out.writeByte(paramA)
        out.writeByte(paramB)
        out.writeByte(0)
        out.writeRSString("")
    }
}
