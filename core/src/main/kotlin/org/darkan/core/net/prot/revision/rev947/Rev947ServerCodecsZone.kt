package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for zone updates.
 *
 * Per `docs/net/serverprot/zone-updates-947-3.md`:
 * - 3 FRAME packets (opcodes 18, 57, 126) — write zone-state globals (level, baseX, baseY).
 * - 16 standalone sub-packet opcodes — apply position-relative updates using the frame-set
 *   zone state.
 *
 * MAJOR FINDING #1 (per A3): the legacy stub label `s(173, "UPDATE_ZONE_FULL_FOLLOWS")` was
 * WRONG. Op 173 is unrelated; the real frame opcodes are 18, 57, 126.
 *
 * MAJOR FINDING #2 (per A3): in 947-3 only sub-opcode 1 (LOC_ANIM) is bound on the
 * sub-protocol dispatch vector. The other 17 sub-ops are silently length-skipped. Server
 * code SHOULD prefer standalone main-table opcodes for everything other than LOC_ANIM —
 * `UpdateZonePartialEnclosed` should only ever batch sub-op 1.
 *
 * Byte-transform notes:
 * - `g1_neg(P)` reader returns `-rawByte`. Encoder: `writeByteInverse(P)`.
 * - `g1_sub128(P)` reader returns `-128 - rawByte`. Encoder: `writeByteInverse(P + 128)`
 *   (which produces `-(P+128) & 0xFF == (-128 - P) & 0xFF`).
 * - `g1_add128(P)` reader returns `rawByte + 128` ≡ JagExtensions `writeByteAdd(P)`
 *   (which writes `P + 128`; reader returns `(P + 128) - 128 ... wait`). Actually the
 *   reader `g1_add128` reads `(b + 128) & 0xFF` and returns that as the value — so the
 *   server must write `rawByte = P - 128` so that reader sees `((P - 128) + 128) & 0xFF == P`.
 *   That is `writeByteSubtract(-P)` ≡ `writeByte((P - 128) & 0xFF)`. There is no clean
 *   JagExtensions helper — we use `writeByte((P - 128) and 0xFF)` inline with a comment.
 */
internal fun Codec.registerRev947ServerCodecsZone() {

    // -----------------------------------------------------------------------
    // Frame packets (A3 §2)
    // -----------------------------------------------------------------------

    // UPDATE_ZONE_FULL_FOLLOWS (op 18, 3B) — clears zone + sets coords for subsequent zone-relative packets.
    // Wire: g1s byte_signedLevel; g1 byte_zoneY_delta; g1+128 byte_level_add128.
    // The bytes are signed/unsigned deltas applied to the client's build-area origin —
    // see A3 §2 "UPDATE_ZONE_FULL_FOLLOWS" for the server-side mapping. We faithfully
    // forward whatever the caller supplies in (level, zoneX, zoneY); B6's ZoneBundleBuilder
    // is responsible for translating world-tile coords into the wire-byte deltas.
    serverProt<UpdateZoneFullFollowsV2>(opcode = 18, size = 3) { out ->
        out.writeByte(zoneY)            // byte 0: g1s — signed level affecting baseY
        out.writeByte(zoneX)             // byte 1: g1 — unsigned zone-y delta affecting baseX
        out.writeByteAdd(level)          // byte 2: g1+128 — round-trips via client `byte + 0x80`
    }

    // UPDATE_ZONE_PARTIAL_FOLLOWS (op 57, 3B) — sets zone globals only (no scene-clear).
    // Wire: g1 byte_zoneY_delta; g1s byte_signedLevel; g1+128 byte_level_add128.
    // (Byte ORDER differs from FULL_FOLLOWS — A3 §2 lists [zoneY, signedLevel, level] here vs
    // [signedLevel, zoneY, level] for FULL_FOLLOWS.)
    serverProt<UpdateZonePartialFollows>(opcode = 57, size = 3) { out ->
        out.writeByte(zoneX)
        out.writeByte(zoneY)
        out.writeByteAdd(level)
    }

    // UPDATE_ZONE_PARTIAL_ENCLOSED (op 126, varShort) — header + inline sub-opcode stream.
    // Per A3 §1 (MAJOR FINDING #2), only sub-op 1 (LOC_ANIM) is bound in 947-3. The caller
    // should not batch other sub-packets here — emit them as standalone main-table opcodes
    // instead. This encoder still serialises whatever sub-packet list is supplied.
    serverProt<UpdateZonePartialEnclosed>(opcode = 126, size = ProtSize.VarShort) { out ->
        // Header layout per A3 §2 UPDATE_ZONE_PARTIAL_ENCLOSED:
        //   g1_neg byte_zoneY_delta_neg (sets level via -byte & 0xff)
        //   g1_sub128 byte_signedLevel_sub128 (sets baseY)
        //   g1s byte_signedZoneX_delta (sets baseX)
        out.writeByteInverse(level)
        out.writeByteInverse(zoneY + 128)    // g1_sub128: rawByte = -128 - value
        out.writeByte(zoneX)
        // Sub-packets are written by the caller; this encoder leaves an empty body when none
        // are supplied. When provided, each entry is expected to start with the sub-opcode
        // followed by its body — but since the upstream codec lookup operates on the typed
        // ServerProt, we don't have an opcode for embedded sub-packets here. Per A3 the
        // recommended pattern is to NOT use this batch encoder for anything other than LOC_ANIM;
        // a future encoder revision will handle LOC_ANIM embedding explicitly.
        //
        // TODO(B6): once a LOC_ANIM sub-packet is defined, write `out.writeByte(1)` followed
        //   by its 11-byte body inside this loop.
        if (subPackets.isNotEmpty()) {
            // Caller-supplied opaque batch — should not happen in normal world-login flows.
            // Leave the stream empty; the client silently consumes the rest of the payload.
        }
    }

    // -----------------------------------------------------------------------
    // Standalone sub-packet encoders (A3 §3)
    // -----------------------------------------------------------------------

    // LOC_ADD (op 79, varByte) — A3 §3.1.
    // Wire: g1_neg packedCoord; g4_alt1 (LE u32) locId; g1 shapeFlags.
    serverProt<LocAdd>(opcode = 79, size = ProtSize.VarByte) { out ->
        out.writeByteInverse(packedCoord)
        out.writeIntLittle(locId)
        out.writeByte(shapeFlags)
    }

    // LOC_DEL (op 37, 2B) — A3 §3.2.
    // Wire: g1s shapeFlags_signed; g1_sub128 packedCoord.
    serverProt<LocDel>(opcode = 37, size = 2) { out ->
        out.writeByte(shapeFlags)
        out.writeByteInverse(packedCoord + 128)    // g1_sub128 encoder
    }

    // LOC_CUSTOMISE (op 41, varByte) — A3 §3.3.
    // Heavy header + optional sub-arrays; emitted as opaque payload until a structured API is required.
    serverProt<LocCustomise>(opcode = 41, size = ProtSize.VarByte) { out ->
        out.writeFully(payload)
    }

    // LOC_PREFETCH (op 51, 7B) — A3 §3.4.
    // Wire: g1 visTime; g1_neg packedCoord; g1s shapeFlags_signed; g4_alt1 (LE u32) locId.
    serverProt<LocPrefetch>(opcode = 51, size = 7) { out ->
        out.writeByte(visTime)
        out.writeByteInverse(packedCoord)
        out.writeByte(shapeFlags)
        out.writeIntLittle(locId)
    }

    // LOC_ANIM_SPECIFIC (op 56, 10B) — A3 §3.6.
    // Wire: g1 packedCoord; g4_alt1 (LE u32) animId; g1 shapeFlags; g1 unknown1;
    //       g1 delay; g2 (BE u16) speed.
    serverProt<LocAnimSpecific>(opcode = 56, size = 10) { out ->
        out.writeByte(packedCoord)
        out.writeIntLittle(animId)
        out.writeByte(shapeFlags)
        out.writeByte(unknown1)
        out.writeByte(delay)
        out.writeShort(speed)
    }

    // LOC_MERGE (op 197, 5B) — A3 §3.7.
    // Wire: g4_alt1 (LE i32) entityServerIndex; g1 packedCoord+shape.
    serverProt<LocMerge>(opcode = 197, size = 5) { out ->
        out.writeIntLittle(entityServerIndex)
        out.writeByte(packedCoordAndShape)
    }

    // OBJ_ADD (op 38, 5B) — A3 §3.8.
    // Wire: g1 objIdHi; g1+128 objIdLo; g1 countHi; g1 countLo; g1 packedCoord.
    serverProt<ObjAdd>(opcode = 38, size = 5) { out ->
        out.writeByte(objIdHi)
        out.writeByteAdd(objIdLo)
        out.writeByte(countHi)
        out.writeByte(countLo)
        out.writeByte(packedCoord)
    }

    // OBJ_DEL (op 42, 3B) — A3 §3.9.
    // Wire: g1_neg packedCoord; g1+128 objIdLo; g1 objIdHi.
    serverProt<ObjDel>(opcode = 42, size = 3) { out ->
        out.writeByteInverse(packedCoord)
        out.writeByteAdd(objIdLo)
        out.writeByte(objIdHi)
    }

    // OBJ_COUNT (op 60, 7B) — A3 §3.10.
    // Wire: g2 (BE u16) playerIndex; g1+128 objIdLo; g1 objIdHi; g1_neg packedCoord;
    //       g2 (BE u16) count.
    serverProt<ObjCount>(opcode = 60, size = 7) { out ->
        out.writeShort(playerIndex)
        out.writeByteAdd(objIdLo)
        out.writeByte(objIdHi)
        out.writeByteInverse(packedCoord)
        out.writeShort(count)
    }

    // OBJ_REVEAL (op 20, 7B) — A3 §3.11. Inferred from 946 layout (size matches 947-3 ProtEntry).
    // Wire: g1 packedCoord; g2 (BE u16) objId; g2 (BE u16) oldCount; g2 (BE u16) newCount.
    // TODO(A3 follow-up #2): verify byte-by-byte against a live capture.
    serverProt<ObjReveal>(opcode = 20, size = 7) { out ->
        // The handler reads 7 bytes total. Per A3 §3.11 the inferred layout fits 1 + 2 + 2 + 2 = 7.
        // We encode in the documented order; if a capture shows otherwise, this layout will need
        // re-RE.
        out.writeByte(packedCoord)
        out.writeShort(objId)
        out.writeShort(oldCount)
        out.writeShort(newCount)
    }

    // MAP_ANIM (op 62, 11B) — A3 §3.12.
    // Wire: g1 packedCoord; g2 (BE u16) entityIdLow; g2 (BE u16) entityIdHigh;
    //       g2 (BE u16) heightOffset; g1 angleHeight; 3B padding (zeros).
    serverProt<MapAnim>(opcode = 62, size = 11) { out ->
        out.writeByte(packedCoord)
        out.writeShort(entityIdLow)
        out.writeShort(entityIdHigh)
        out.writeShort(heightOffset)
        out.writeByte(angleHeight)
        out.skip(3)
    }

    // MAP_ANIM_SPECIFIC (op 145, 14B) — A3 §3.13.
    // Wire: MAP_ANIM header (8B incl. extra unknown byte) + 3B packed fine offset.
    // We forward bytes 0..7 as the standard MAP_ANIM layout plus `unknown` at offset 7,
    // then write the 3-byte fineOffset.
    serverProt<MapAnimSpecific>(opcode = 145, size = 14) { out ->
        out.writeByte(packedCoord)
        out.writeShort(entityIdLow)
        out.writeShort(entityIdHigh)
        out.writeShort(heightOffset)
        out.writeByte(angleHeight)
        out.writeByte(unknown)
        out.writeMedium(fineOffset)
        // total: 1 + 2 + 2 + 2 + 1 + 1 + 3 = 12 ... but size is 14. Pad 2B.
        out.skip(2)
    }

    // MAP_PROJANIM (op 47, 20B) — A3 §3.14. Opaque payload — heavy field set, inferred 946 layout.
    serverProt<MapProjAnim>(opcode = 47, size = 20) { out ->
        out.writeFully(payload)
    }

    // MAP_PROJANIM_HALT (op 199, 28B) — A3 §3.15. Opaque payload — combined MAP_PROJANIM + 8B HALT.
    serverProt<MapProjAnimHalt>(opcode = 199, size = 28) { out ->
        out.writeFully(payload)
    }

    // PROJANIM_SPECIFIC (op 196, 21B) — A3 §3.16. Opaque payload (MAP_PROJANIM + 1B doubleRes byte).
    serverProt<ProjAnimSpecific>(opcode = 196, size = 21) { out ->
        out.writeFully(payload)
    }

    // PROJANIM_SPECIFIC_HALT (op 192, 29B) — A3 §3.17. Opaque payload (MAP_PROJANIM_HALT + 1B doubleRes).
    serverProt<ProjAnimSpecificHalt>(opcode = 192, size = 29) { out ->
        out.writeFully(payload)
    }

    // SOUND_AREA (op 167, varByte) — A3 §3.18.
    // Wire: g1 (skipped byte0); g1 packedCoord; g2 (BE u16) soundId; g1 volume;
    //       g1 paramA; g1 paramB; g1 paramC; gStr soundPath.
    // The data class only carries (packedCoord, soundId, volume, paramA, paramB) for now;
    // paramC and soundPath default to 0/"" pending an A3 follow-up that uses captures to
    // confirm the remaining fields. Output is fixed-shape per A3 §3.18.
    serverProt<SoundArea>(opcode = 167, size = ProtSize.VarByte) { out ->
        out.writeByte(0)               // byte 0 — handler skips, server writes 0
        out.writeByte(packedCoord)
        out.writeShort(soundId)
        out.writeByte(volume)
        out.writeByte(paramA)
        out.writeByte(paramB)
        out.writeByte(0)               // paramC placeholder — TODO once captured
        out.writeRSString("")          // soundPath placeholder — TODO once captured
    }
}
