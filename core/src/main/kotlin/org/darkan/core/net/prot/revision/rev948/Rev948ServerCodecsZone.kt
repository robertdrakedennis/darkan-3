package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.darkan.core.Logger.logWarn
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*
import kotlin.reflect.KClass

/**
 * Rev948 server encoders for zone updates.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md` §"World-Login Critical Opcode Migration"
 * and §4, all frame and sub-packet opcodes MOVED in 948:
 *
 * Frame opcodes:
 *  - UPDATE_ZONE_FULL_FOLLOWS:       947-3 op 18 → **948 op 78** (3B, handler @ 0x10004c6d0)
 *  - UPDATE_ZONE_PARTIAL_FOLLOWS:    947-3 op 57 → **948 op 41** (3B, handler @ 0x10004c4e0)
 *  - UPDATE_ZONE_PARTIAL_ENCLOSED:   947-3 op 126 → **948 op 76** (varShort, handler @ 0x10004dad0)
 *
 * Standalone sub-packet opcodes (all M-confidence per Phase 1):
 *  - LOC_ADD:                        947-3 op 79 → **948 op 90** (varByte, @ 0x00139080)
 *  - LOC_DEL:                        947-3 op 37 → **948 op 16** (2B, @ 0x100051e40)
 *  - LOC_CUSTOMISE:                  947-3 op 41 → **948 op 50** (varByte, @ 0x0013f5f0)
 *  - LOC_PREFETCH:                   947-3 op 51 → **948 op 6** (7B, @ 0x00138110)
 *  - LOC_ANIM_SPECIFIC:              947-3 op 56 → **948 op 21** (10B, @ 0x00118ef0)
 *  - LOC_MERGE:                      947-3 op 197 → **948 op 170** (5B, @ 0x000eeba0)
 *  - OBJ_ADD:                        947-3 op 38 → **948 op 46** (5B, @ 0x100053f20)
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
 * Current 948-5 Ghidra verification resolves the op 41 reorder: the header order is zoneX+128,
 * level byteAdd, zoneY. op 76/op78 frame headers are likewise current-binary verified. Standalone
 * sub-packet bodies remain documented individually below.
 */
internal fun Codec.registerRev948ServerCodecsZone() {

    // -----------------------------------------------------------------------
    // Frame packets
    // -----------------------------------------------------------------------

    // UPDATE_ZONE_FULL_FOLLOWS (op 78, 3B) — handler @ 0x10004c6d0 (Ghidra 948-5).
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
    //   UPDATE_ZONE_PARTIAL_FOLLOWS_OP41 @ 0x10004c4e0 (Ghidra 948-5).
    //   Wire: [+0]=zoneX+128(raw unsigned byte), [+1]=level(byteAdd), [+2]=zoneY(raw signed byte).
    //   Handler: DAT_ac=base608-0x400+byte*8=zoneX ; DAT_a8=(byte+0x80)=level ;
    //            DAT_b0=base60c+(char)byte*8=zoneY.
    //   The 3-byte header order is: zoneX+128, level in the middle byte, zoneY last.
    serverProt<UpdateZonePartialFollows>(opcode = 41, size = 3) { out ->
        out.writeByte(zoneX + 128)
        out.writeByteAdd(level)
        out.writeByte(zoneY)
    }

    // UPDATE_ZONE_PARTIAL_ENCLOSED (op 76, varShort) — handler @ 0x10004dad0 (Ghidra 948-5).
    //   Header wire: [+0]=level(byteInverse), [+1]=zoneY(raw signed byte), [+2]=zoneX(byteSubtract).
    //   Handler: DAT_a8=(-byte)&0xff=level ; DAT_b0=base60c+(char)byte*8=zoneY ;
    //            DAT_ac=base608+(char)(-0x80-byte)*8=zoneX.
    //   Then loops sub-opcodes via g_zoneSubProtVector @ DAT_100f12830 (sub-op>0x11 => error).
    //   FIXED from prior placeholder (byte1/byte2 transforms were wrong).
    serverProt<UpdateZonePartialEnclosed>(opcode = 76, size = ProtSize.VarShort) { out ->
        out.writeByteInverse(level)
        out.writeByte(zoneY)
        out.writeByteSubtract(zoneX)
        for (packet in subPackets) {
            if (!out.writeEnclosedZoneSubPacket(packet)) {
                logWarn(
                    "UPDATE_ZONE_PARTIAL_ENCLOSED (op 76) cannot encode ${packet::class.simpleName}; " +
                        "send it as a standalone zone update or add its rev948 sub-op mapping."
                )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Standalone sub-packet encoders (presumed byte-equivalent to 947-3)
    // -----------------------------------------------------------------------

    // LOC_ADD (op 90 in 948, was op 79). Wire: g1 packedCoord; g4_alt1 locId; g1 shapeFlags-128.
    serverProt<LocAdd>(opcode = 90, size = ProtSize.VarByte) { out ->
        out.writeByte(packedCoord)
        out.writeIntLittle(locId)
        out.writeByte(shapeFlags - 128)
        extra?.let { out.writeByte(it) }
    }

    // LOC_DEL (op 16, 2B) — Ghidra handler jag::packethandlers::ZoneUpdates::LOC_DEL_OP16
    // @ 0x100051e40. Wire: g1(-128-shapeFlags); g1_neg packedCoord.
    serverProt<LocDel>(opcode = 16, size = 2) { out ->
        out.writeByte(-128 - shapeFlags)
        out.writeByteInverse(packedCoord)
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

    // OBJ_ADD (op 46, 5B). Wire: g1 packedCoord; g2 objId; g1 countHi; g1 countLo-128.
    serverProt<ObjAdd>(opcode = 46, size = 5) { out ->
        out.writeByte(packedCoord)
        out.writeShort(objId)
        out.writeByte(count ushr 8)
        out.writeByte((count and 0xFF) - 128)
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

private suspend fun ByteWriteChannel.writeEnclosedZoneSubPacket(packet: ServerProt): Boolean =
    when (val subPacket = VERIFIED_ENCLOSED_ZONE_SUBPACKETS[packet::class]) {
        null -> false
        else -> {
            val payload = subPacket.encode(packet)
            writeByte(subPacket.subOpcode)
            when (val size = subPacket.size) {
                ProtSize.VarByte -> writeByte(payload.size)
                ProtSize.VarShort -> writeShort(payload.size)
                is ProtSize.Fixed -> require(payload.size == size.length) {
                    "UPDATE_ZONE_PARTIAL_ENCLOSED sub-op ${subPacket.subOpcode} ${subPacket.name} " +
                        "encoded ${payload.size} bytes, expected ${size.length}"
                }
            }
            writeFully(payload)
            true
        }
    }

private data class EnclosedZoneSubPacket(
    val subOpcode: Int,
    val name: String,
    val size: ProtSize,
    val writeBody: suspend ByteWriteChannel.(ServerProt) -> Unit,
) {
    suspend fun encode(packet: ServerProt): ByteArray {
        val payload = Buffer()
        val channel = payload.asByteWriteChannel()
        writeBody.invoke(channel, packet)
        channel.flush()
        return payload.readByteArray()
    }
}

private val VERIFIED_ENCLOSED_ZONE_SUBPACKETS: Map<KClass<out ServerProt>, EnclosedZoneSubPacket> = mapOf(
    // VERIFIED: docs/kb/glossary/decoded-rebuild-zone.md has 14 captured op76 subop-13 packets
    // and RE-4 descriptor pairing names the 11-byte body LOC_ANIM.
    LocAnim::class to EnclosedZoneSubPacket(
        subOpcode = 13,
        name = "LOC_ANIM",
        size = ProtSize.Fixed(11),
    ) { packet ->
        writeLocAnimBody(packet as LocAnim)
    },
)

/*
 * HYPOTHESIS/TODO rows from our current docs. They are deliberately NOT registered above until the
 * opcode-identity adjudication work confirms them beyond binary descriptor pairing:
 *   0 LocAdd varByte, 1 ProjAnimSpecificHalt 29, 2 LocDel 2, 3 ObjReveal 7,
 *   4 unresolved docs conflict: decoded-rebuild-zone says SoundArea varByte; 948-opcode-tables says
 *     LOC_MERGE varByte, 5 ObjDel 3, 6 MapAnimSpecific 14, 7 MapProjAnim 20, 8 ObjCount 7,
 *   9 ObjAdd 5, 10 LocCustomise varByte, 11 LocPrefetch 7, 12 LocMerge 5, 14 MapAnim 11,
 *   15 ProjAnimSpecific 21, 16 LocAnimSpecific 10, 17 MapProjAnimHalt 28.
 */

private suspend fun ByteWriteChannel.writeLocAnimBody(packet: LocAnim) {
    writeByte(packet.packedCoord)
    writeInt(packet.animId)
    writeByte(packet.shapeFlags)
    writeByte(packet.unknown1)
    writeByte(packet.delay)
    writeShort(packet.speed)
    writeByte(packet.mode)
}
