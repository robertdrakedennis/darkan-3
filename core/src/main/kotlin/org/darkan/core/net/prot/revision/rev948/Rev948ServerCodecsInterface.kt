package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for the interface packet family.
 *
 * Per `docs/net/serverprot/948-delta-from-947-3.md`, every IF_* opcode MOVED in 948. The wire
 * formats are presumed byte-equivalent to their 947-3 counterparts (the Phase 1 walk did not
 * flag any IF_* transforms changing). Cross-verify against captures before relying on any of
 * these for production traffic.
 *
 * Opcode migration (947-3 → 948):
 *  - IF_OPENTOP:                  68 → 39
 *  - IF_SETTOPLEVELINTERFACE:     94 → 3
 *  - IF_OPENSUB:                  17 → 94
 *  - IF_SETPOSITION:               8 → 82
 *  - IF_CLOSESUB → IF_CLOSESUB_ACTIVE: 33 → 62
 *  - IF_MOVESUB:                 189 → REMOVED (no identified 948 destination; see stub UNKNOWN_<op>)
 *  - IF_SETEVENTS (10B / IF_SETEVENTS1): 34 → 97
 *  - IF_SETEVENTS2 (12B):         35 → 35 (IDENTICAL)
 *  - IF_SETHIDE:                 103 → 91
 *  - IF_SETANGLE:                117 → 4
 *  - IF_SET_HTTP_IMAGE:          146 → 152
 *  - IF_SETOBJECT_ACTIVE:         16 → 101
 *  - IF_SETMODEL:                 74 → 102
 *  - IF_SETANIM_ACTIVE:           81 → 96
 *  - IF_SETNPCHEAD:               98 → 115
 *  - IF_SETOBJECT:               100 → 84
 *  - IF_SETANIM:                 106 → 86
 *  - IF_SETCOLOUR:               122 → 32
 *  - IF_SETOBJECT_SMALL:         141 → 180
 *  - IF_SETANIM_SMALL:           193 → 136
 *  - IF_SETPLAYERHEAD_ACTIVE:     14 → 8
 *  - IF_SETRECOL:                 44 → 99
 *  - IF_SET2DANGLE:               53 → 30
 *  - IF_SET_MODEL_FRAME:          64 → 38
 *  - IF_SETNPCMODEL:              76 → 59
 *  - IF_SETMODELORIGIN:           88 → 68
 *  - IF_SETGRAPHIC:               92 → 103
 *  - IF_SETSPRITE:               123 → 14
 *  - IF_SETSCROLLSIZE:           136 → 158
 *  - IF_SETNPCHEAD_ACTIVE:       150 → 206
 *  - IF_SETMODEL_COORD:          208 → 165
 *  - IF_SETSCROLLPOS:            210 → 179
 *  - IF_SETPLAYERMODEL_OTHER:     97 → 70
 *  - IF_SETPLAYERMODEL_SELF:     107 → 60
 *  - IF_SETPLAYERMODEL_SNAPSHOT: 110 → 118
 *  - IF_SUBSWAP:                  85 → 40
 *  - IF_TRIGGER_CLOSE:            49 → 123
 *  - IF_CLOSESUB_BY_ID:          169 → 148
 *  - IF_SETTEXT:                   2 → 122
 *
 * Reader/writer transform glossary (recap from 947-3 codec — same JagExtensions helpers):
 *  - `g1+128` reader == `writeByteAdd(v)`
 *  - `g1-128` reader == `writeByteSubtract(v)`
 *  - `g1_neg` reader == `writeByteInverse(v)`
 *  - `g2_alt2` reader == high-byte BE + `writeByteAdd(low)`
 *  - `g4_alt1` reader (LE u32) == `writeIntLittle(v)`
 *  - `g4_alt2` reader (INT2 middle-endian) == `writeIntMiddle(v)`
 *  - `g4_alt3` reader (INT3 middle-endian) == `writeIntInverseMiddle(v)`
 */
internal fun Codec.registerRev948ServerCodecsInterface() {
    // ---------------------------------------------------------------------
    // Critical world-login packets (mirrored from 947 A1 §1)
    // ---------------------------------------------------------------------

    // IF_OPENTOP (op 39, 6B). 948 wire (0x00194050 disasm): topLevelId = g4_alt1 (LE u32);
    //   subId = BE u16 (MOVZX word + ROL8). CHANGED: 947-3 codec wrote subId LE — login works
    //   because the full-screen root subId is 0 (byte order irrelevant). For non-zero subId this
    //   must be BE.
    serverProt<IfOpenTop>(opcode = 39, size = 6) { out ->
        out.writeIntLittle(topLevelId)
        out.writeShort(subId)
    }

    // IF_SETTOPLEVELINTERFACE (op 3, 19B). 948 wire (handler 0x00186900, disasm @ 0x0018692f):
    //   [0..3] gT_uint DISCARD; [4] skip; [5..8] g4_alt2 DISCARD;
    //   [9..10] packed id: byte9 = LOW byte (client reads (byte9+0x80)&0xFF), byte10 = HIGH byte
    //     (client computes id = byte10*0x100 + ((byte9+0x80)&0xFF)); [11..14] g4_alt1 DISCARD;
    //   [15..18] g4_alt1 DISCARD. The id lives at offset 9-10, low-byte FIRST. Verified against
    //   live 948 capture: id=906 → bytes `0A 03` at offset 9-10 (0x0A = (906&0xFF)+0x80 mod 256).
    serverProt<IfSetTopLevelInterface>(opcode = 3, size = 19) { out ->
        out.skip(9)
        out.writeByteAdd(topLevelId and 0xFF)         // offset 9: low byte, client re-adds 0x80
        out.writeByte((topLevelId ushr 8) and 0xFF)   // offset 10: high byte
        out.skip(8)
    }

    // IF_OPENSUB (op 94, 8B). 948 wire (re-derived from handler 0x00194100 disasm):
    //   subId = LE i16 (no transform); walkable = LE i16 (no transform); parentHash = BE u32.
    // CHANGED from 947-3 (which used g2_alt2/g2_alt2/g4_alt1). See 948-research-B doc.
    // Opcode/size are AUTHORITATIVE from the deterministic prot dump (948-prot-tables-dump.csv):
    //   SERVER op94 = IF_OPENSUB size 8; op82 = IF_SETPOSITION size 23 (below). NOT swapped — a
    //   prior handler-id "oracle" swap got these backwards and broke lobby login.
    serverProt<IfOpenSub>(opcode = 94, size = 8) { out ->
        out.writeShortLittle(subId)
        out.writeShortLittle(walkable)
        out.writeInt(parentHash)
    }

    // IF_SETPOSITION (op 82, 23B). Opcode/size AUTHORITATIVE from the deterministic prot dump
    //   (948-prot-tables-dump.csv): SERVER op82 = IF_SETPOSITION size 23. (Confirmed by the live
    //   948 lobby capture, which the lobby login depends on.) Wire (handler 0x00189180):
    //   [0]      byte LAYER. Client computes ((-byte) - 0x80) & 0xFF, so byte = writeByteSubtract(layer).
    //   [1..4]   gT_unsigned_int DISCARD.
    //   [5..8]   g4_alt3 POSITION = packed parent component hash ((parentInterface<<16)|slot).
    //            g4_alt3 reads b1<<24|b0<<16|b3<<8|b2 == writeIntInverseMiddle(position).
    //   [9..12]  gT_unsigned_int DISCARD.
    //   [13..16] g4_alt2 DISCARD.
    //   [17..20] gT_unsigned_int DISCARD.
    //   [21..22] LE u16 COMPONENTID = child interface id placed at the slot (handler reads
    //            byte22*0x100 + byte21 == writeShortLittle(componentId)) → passed to GetInterface.
    //   Capture: layer=1→0x7F, position=(906<<16)|44=0x038A002C → `8A 03 2C 00`, componentId=907 → `8B 03`.
    serverProt<IfSetPosition>(opcode = 82, size = 23) { out ->
        out.writeByteSubtract(layer)         // [0]
        out.skip(4)                          // [1..4]
        out.writeIntInverseMiddle(position)  // [5..8]
        out.skip(4)                          // [9..12]
        out.skip(4)                          // [13..16] (g4_alt2, value 0 → bytes identical)
        out.skip(4)                          // [17..20]
        out.writeShortLittle(componentId)    // [21..22]
    }

    // IF_CLOSESUB_ACTIVE (op 62, 4B). Wire: g4_alt1 componentHash.
    serverProt<IfCloseSub>(opcode = 62, size = 4) { out ->
        out.writeIntLittle(componentHash)
    }

    // IF_MOVESUB has no identified 948 destination per Phase 1 delta — skipping registration.
    // TODO: identify and register the 948 IF_MOVESUB opcode once the missing subsystem walk lands.

    // IF_SETEVENTS1 (op 97, 10B). 948 wire (0x00185f60): componentHash = g4_alt3 FIRST, then 3
    //   16-bit fields: [4..5] g2_alt2, [6..7] LE u16, [8..9] g2_alt2 — mapped to
    //   SetServerActiveProperties(props, componentHash, fromSlot=@6-7, toSlot=@8-9, 0,
    //   slotRange=@4-5, 1). componentHash transform CHANGED to g4_alt3 (was g4_alt1).
    //   FLAGGED: the per-field (eventsMask/endSlot/startSlot) mapping is not capture-confirmed —
    //   verify field assignment against a live capture before relying on this packet. The 3
    //   16-bit field byte transforms below are best-effort from the decompile.
    serverProt<IfSetEvents1>(opcode = 97, size = 10) { out ->
        out.writeIntInverseMiddle(componentHash)
        // [4..5] g2_alt2 slotRange-ish (hi plain, lo +0x80)
        out.writeByte((eventsMask ushr 8) and 0xFF)
        out.writeByteAdd(eventsMask and 0xFF)
        // [6..7] LE u16 fromSlot
        out.writeShortLittle(if (endSlot == -1) 0xFFFF else endSlot)
        // [8..9] g2_alt2 toSlot (hi plain, lo +0x80)
        val startVal = if (startSlot == -1) 0xFFFF else startSlot
        out.writeByte((startVal ushr 8) and 0xFF)
        out.writeByteAdd(startVal and 0xFF)
    }

    // IF_SETEVENTS2 (op 35, 12B). 948 wire (0x00186040), mapped to
    //   SetServerActiveProperties(props, componentHash, fromSlot, toSlot, settings, -1, 0):
    //   [0..3] g4_alt2 settings; [4..5] LE i16 fromSlot (0xFFFF=-1); [6..7] LE i16 toSlot
    //   (0xFFFF=-1); [8..11] BE u32 componentHash. CHANGED from 947-3 (field order + transforms).
    serverProt<IfSetEvents>(opcode = 35, size = 12) { out ->
        val componentHash = (events.interfaceId shl 16) or (events.componentId and 0xFFFF)
        out.writeIntMiddle(events.settings)
        out.writeShortLittle(if (events.fromSlot == -1) 0xFFFF else events.fromSlot)
        out.writeShortLittle(if (events.toSlot == -1) 0xFFFF else events.toSlot)
        out.writeInt(componentHash)
    }

    // IF_SETHIDE (op 91, 5B). 948 wire (handler 0x00193fc0): byte hideFlag (==0x81 → hidden);
    //   componentHash = g4_alt2. CHANGED from 947-3 (was g4_alt1).
    serverProt<IfSetHide>(opcode = 91, size = 5) { out ->
        out.writeByte(if (hide) 0x81 else 0x00)
        out.writeIntMiddle(componentHash)
    }

    // IF_SETANGLE (op 4, 32B).
    serverProt<IfSetAngle>(opcode = 4, size = 32) { out ->
        out.writeInt(0)
        out.writeInt(angle3)
        out.writeIntMiddle(0)
        out.writeIntMiddle(0)
        out.writeByte(colourIndex)
        out.writeInt(0)
        out.writeInt(angle1)
        out.writeByteSubtract(componentId and 0xFF)
        out.writeByte((componentId ushr 8) and 0xFF)
        out.writeByte(angleZoom)
        out.writeIntMiddle(packedAngle2)
    }

    // IF_SET_HTTP_IMAGE (op 152, varByte).
    serverProt<IfSetHttpImage>(opcode = 152, size = ProtSize.VarByte) { out ->
        out.writeRSString(imageUrl)
    }

    // ---------------------------------------------------------------------
    // Property setters
    // ---------------------------------------------------------------------

    // IF_SETOBJECT_ACTIVE (op 101, 4B). 948 wire (0x00185b20): componentHash = g4_alt2.
    // CHANGED from 947-3 (was g4_alt3).
    serverProt<IfSetObjectActive>(opcode = 101, size = 4) { out ->
        out.writeIntMiddle(componentHash)
    }

    // IF_SETMODEL (op 102, 8B). 948 wire (0x001858e0): modelId = g4_alt2; componentHash = BE u32.
    // CHANGED from 947-3 (was g4_alt3 / g4_alt3).
    serverProt<IfSetModel>(opcode = 102, size = 8) { out ->
        out.writeIntMiddle(value)
        out.writeInt(componentHash)
    }

    // IF_SETANIM_ACTIVE (op 96, 4B).
    serverProt<IfSetAnimActive>(opcode = 96, size = 4) { out ->
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETNPCHEAD (op 115, 10B).
    serverProt<IfSetNpcHead>(opcode = 115, size = 10) { out ->
        out.writeByte((scale ushr 8) and 0xFF)
        out.writeByteAdd(scale and 0xFF)
        out.writeInt(componentHash)
        out.writeShort(partA)
        out.writeByte((partB ushr 8) and 0xFF)
        out.writeByteAdd(partB and 0xFF)
    }

    // IF_SETOBJECT (op 84, 10B). 948 wire (0x00185a00): componentHash = g4_alt1;
    //   objectCount = g4_alt3; objectSlot = LE u16. CHANGED from 947-3 (order + transforms).
    serverProt<IfSetObject>(opcode = 84, size = 10) { out ->
        out.writeIntLittle(componentHash)
        out.writeIntInverseMiddle(objectCount)
        out.writeShortLittle(objectSlot)
    }

    // UNKNOWN_86 — SetComponentProperty propType 3 (op 86, 10B). 948 wire (0x00185740):
    //   componentHash = g4_alt1; frame = BE u16; animId = BE u32. CHANGED from 947-3.
    //   NOT IF_SETANIM (that is op103). Class renamed IfSetAnim → IfSetComponentProp3; wire UNCHANGED.
    serverProt<IfSetComponentProp3>(opcode = 86, size = 10) { out ->
        out.writeIntLittle(componentHash)
        out.writeShort(frame)
        out.writeInt(animId)
    }

    // IF_SETCOLOUR (op 32, 8B). 948 wire (0x00185850): colour24 = g4_alt2; componentHash = g4_alt3.
    // CHANGED from 947-3 (the two transforms were swapped).
    serverProt<IfSetColour>(opcode = 32, size = 8) { out ->
        out.writeIntMiddle(colour24)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETOBJECT_SMALL (op 180, 5B). 948 wire (0x00185980): componentHash = BE u32;
    //   smallIdx via byteSubtract (client: -2 - (0x80 - b)). CHANGED from 947-3 (was g4_alt3 + g1).
    serverProt<IfSetObjectSmall>(opcode = 180, size = 5) { out ->
        out.writeInt(componentHash)
        out.writeByteSubtract(smallIdx)
    }

    // IF_SETANIM_SMALL (op 136, 5B). 948 wire (0x00185aa0): componentHash = g4_alt2;
    //   smallIdx via byteSubtract. CHANGED from 947-3 (was g1 + g4_alt1).
    serverProt<IfSetAnimSmall>(opcode = 136, size = 5) { out ->
        out.writeIntMiddle(componentHash)
        out.writeByteSubtract(smallIdx)
    }

    // ---------------------------------------------------------------------
    // Direct update-entry setters
    // ---------------------------------------------------------------------

    // IF_SETPLAYERHEAD_ACTIVE (op 8, 5B). 948 wire (0x00193530): byte flag (==1);
    //   componentHash = g4_alt3. CHANGED from 947-3 (was g4_alt1).
    serverProt<IfSetPlayerHeadActive>(opcode = 8, size = 5) { out ->
        out.writeByte(flag)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETRECOL (op 99, 6B). 948 wire (0x00193860): rgb555 = g2_alt2 (hi plain, lo +0x80);
    //   componentHash = g4_alt1. CHANGED from 947-3 (was g2LE / g4_alt2).
    serverProt<IfSetRecol>(opcode = 99, size = 6) { out ->
        out.writeByte((rgb555 ushr 8) and 0xFF)
        out.writeByteAdd(rgb555 and 0xFF)
        out.writeIntLittle(componentHash)
    }

    // IF_SETGRAPHIC (op 30, 8B). 948 wire (handler 0x00193f10, binary-verified): update-type 0xd,
    //   HASH-then-VALUE — componentHash [0..3] g4_alt3, graphicId [4..7] g4_alt3.
    //   Class renamed IfSet2DAngle → IfSetGraphic: the handler the DB labeled IF_SET2DANGLE is the
    //   official IF_SETGRAPHIC. (Wire layout was already correct; only the class/field names changed.)
    serverProt<IfSetGraphic>(opcode = 30, size = 8) { out ->
        out.writeIntInverseMiddle(componentHash)
        out.writeIntInverseMiddle(graphicId)
    }

    // IF_SET_MODEL_FRAME (op 38, 8B). 948 wire (0x00185b70): frame = g4_alt3; componentHash = g4_alt3.
    // CHANGED from 947-3 (frame was g4_alt2).
    serverProt<IfSetModelFrame>(opcode = 38, size = 8) { out ->
        out.writeIntInverseMiddle(frame)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETNPCMODEL (op 59, 10B).
    serverProt<IfSetNpcModel>(opcode = 59, size = 10) { out ->
        out.writeInt(componentHash)
        out.writeInt(modelId)
        out.writeShort(npcId)
    }

    // IF_SETMODELORIGIN (op 68, 10B).
    serverProt<IfSetModelOrigin>(opcode = 68, size = 10) { out ->
        out.writeIntMiddle(componentHash)
        out.writeByte((x ushr 8) and 0xFF)
        out.writeByteAdd(x and 0xFF)
        out.writeByte((y ushr 8) and 0xFF)
        out.writeByteSubtract(y and 0xFF)
        out.writeShortLittle(z)
    }

    // IF_SETANIM (op 103, 8B). 948 wire (handler 0x00193fe0, binary-verified): update-type 5,
    //   VALUE-then-HASH — animationId [0..3] g4_alt3, componentHash [4..7] g4_alt3.
    //   Canonical IF_SETANIM. op86 is a distinct propType-3 packet (IfSetComponentProp3), NOT this.
    serverProt<IfSetAnim>(opcode = 103, size = 8) { out ->
        out.writeIntInverseMiddle(animationId)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETSPRITE (op 14, 8B). 948 wire (0x00193940): spriteValue = BE u32 FIRST;
    //   componentHash = g4_alt3. CHANGED from 947-3 (order + componentHash transform).
    serverProt<IfSetSprite>(opcode = 14, size = 8) { out ->
        out.writeInt(spriteValue)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETSCROLLSIZE (op 158, 9B).
    serverProt<IfSetScrollSize>(opcode = 158, size = 9) { out ->
        out.writeByte((scrollW ushr 8) and 0xFF)
        out.writeByteAdd(scrollW and 0xFF)
        out.writeByte((scrollH ushr 8) and 0xFF)
        out.writeByteSubtract(scrollH and 0xFF)
        out.writeIntInverseMiddle(componentHash)
        out.writeByteSubtract(subSlot)
    }

    // IF_SETNPCHEAD_ACTIVE (op 206, 5B). 948 wire (0x001935c0): componentHash = BE u32 FIRST;
    //   byte flag (==1). CHANGED from 947-3 (order + componentHash transform).
    serverProt<IfSetNpcHeadActive>(opcode = 206, size = 5) { out ->
        out.writeInt(componentHash)
        out.writeByte(flag)
    }

    // IF_SETMODEL_COORD (op 165, 14B).
    serverProt<IfSetModelCoord>(opcode = 165, size = 14) { out ->
        out.writeByte((npcId ushr 8) and 0xFF)
        out.writeByteAdd(npcId and 0xFF)
        out.writeInt(componentHash)
        out.writeIntMiddle(part1)
        out.writeIntMiddle(part2)
    }

    // IF_SETSCROLLPOS (op 179, 9B). 948 wire (handler 0x001938e0, binary-verified). Field order is
    //   scrollY [0..1] g2_add_le (low byte first, carries +128); subSlot [2] g1_sub (128 - v);
    //   scrollX [3..4] g2_le; componentHash [5..8] g4_alt3. CHANGED: prior encoder wrote scrollY BE,
    //   componentHash BE, and subSlot byteAdd in the wrong slot order.
    serverProt<IfSetScrollPos>(opcode = 179, size = 9) { out ->
        out.writeShortAddLittle(scrollY)          // [0..1] g2_add_le
        out.writeByteSubtract(subSlot)            // [2]    g1_sub
        out.writeShortLittle(scrollX)             // [3..4] g2_le
        out.writeIntInverseMiddle(componentHash)  // [5..8] g4_alt3
    }

    // ---------------------------------------------------------------------
    // Complex / direct-allocation setters (opaque payload)
    // ---------------------------------------------------------------------

    serverProt<IfSetPlayerModelOther>(opcode = 70, size = 25) { out ->
        out.writeFully(payload)
    }
    serverProt<IfSetPlayerModelSelf>(opcode = 60, size = 25) { out ->
        out.writeFully(payload)
    }
    serverProt<IfSetPlayerModelSnapshot>(opcode = 118, size = 29) { out ->
        out.writeFully(payload)
    }

    // IF_SUBSWAP (op 40, 8B). 948 wire (0x00186100): componentA = g4_alt2; componentB = g4_alt1.
    // CHANGED from 947-3 (componentB was g4_alt2).
    serverProt<IfSubSwap>(opcode = 40, size = 8) { out ->
        out.writeIntMiddle(componentA)
        out.writeIntLittle(componentB)
    }

    // ---------------------------------------------------------------------
    // Trigger / close variants
    // ---------------------------------------------------------------------

    // IF_TRIGGER_CLOSE (op 123, 0B).
    serverProt<IfTriggerClose>(opcode = 123, size = 0)

    // IF_CLOSESUB_BY_ID (op 148, 2B). 948 wire (0x001855d0): id = BE u16.
    // CHANGED from 947-3 (was g2LE).
    serverProt<IfCloseSubById>(opcode = 148, size = 2) { out ->
        out.writeShort(id)
    }

    // ---------------------------------------------------------------------
    // Text setter (op 122, varShort)
    // ---------------------------------------------------------------------

    // IF_SETTEXT (op 122, VarShort). 948 wire (0x00185ec0): string text FIRST, then
    //   componentHash = g4_alt3. CHANGED from 947-3 (order reversed + componentHash was BE).
    serverProt<IfSetText>(opcode = 122, size = ProtSize.VarShort) { out ->
        out.writeRSString(text)
        out.writeIntInverseMiddle(componentHash)
    }
}
