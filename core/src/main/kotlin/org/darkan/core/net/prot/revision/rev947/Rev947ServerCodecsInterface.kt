package org.darkan.core.net.prot.revision.rev947

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev947 server encoders for the interface packet family.
 *
 * Byte layouts derived from rs2client.947-3 decompilation per
 * `docs/net/serverprot/interfaces-947-3.md` (40 IF_* opcodes). All transforms use
 * `JagExtensions` helpers — never inline byte ops.
 *
 * Reader vs writer mapping (recap from A1 §0 glossary; verified against
 * `world.gregs.voidps.buffer.JagExtensions`):
 * - `g1+128` reader (`b + 0x80`) == server `writeByteAdd(v)` (writes `v + 128`).
 * - `g1-128` reader (`b - 0x80`) == server `writeByteSubtract(v)` (writes `-v + 128`).
 *     Note: the JagExtensions name is somewhat counterintuitive — its READ counterpart
 *     `readByteSubtract` returns `(-readByte + 128).toByte` so that `writeByteSubtract(v)`
 *     round-trips to `v`. Verified by inspecting the read/write helper pair.
 * - `g1_neg` reader (`-rawByte`) == server `writeByteInverse(v)` (writes `-v`).
 * - `g2_alt2` reader (BE u16 with low-byte `+0x80`) == server `writeShortAdd(v)`
 *   (writes high byte then low byte with `+0x80`).
 * - `g4_alt1` reader (LE u32) == server `writeIntLittle(v)`.
 * - `g4_alt2` reader (INT2 mid-endian: result `b[2]<<24 | b[3]<<16 | b[0]<<8 | b[1]`)
 *      == server `writeIntMiddle(v)`.
 * - `g4_alt3` reader (INT3 mid-endian: result `b[1]<<24 | b[0]<<16 | b[3]<<8 | b[2]`)
 *      == server `writeIntInverseMiddle(v)`.
 *
 * (Confirm by comparing the byte-order against `JagExtensions.readUIntMiddle` /
 * `readUIntInverseMiddle` — the read order is the inverse of the write order.)
 */
internal fun Codec.registerRev947ServerCodecsInterface() {
    // ---------------------------------------------------------------------
    // Critical world-login packets (A1 §1)
    // ---------------------------------------------------------------------

    // IF_OPENTOP (op 68, 6B) — per A1 §1.1.
    // Wire: g4_alt1 topLevelId; g1 subIdLow; g1 subIdHigh (LE u16 subId across bytes 4..5).
    serverProt<IfOpenTop>(opcode = 68, size = 6) { out ->
        out.writeIntLittle(topLevelId)
        out.writeByte(subId and 0xFF)
        out.writeByte((subId ushr 8) and 0xFF)
    }

    // IF_SETTOPLEVELINTERFACE (op 94, 19B) — per A1 §1.2.
    // Wire: g1 pad0; g1 topLevelIdHigh; g1+128 topLevelIdLow; 16B of unused fields (zeros).
    serverProt<IfSetTopLevelInterface>(opcode = 94, size = 19) { out ->
        out.writeByte(0)
        out.writeByte((topLevelId ushr 8) and 0xFF)
        out.writeByteAdd(topLevelId and 0xFF)
        out.skip(16)
    }

    // IF_OPENSUB (op 17, 8B) — per A1 §1.3.
    // Wire: g2_alt2 subId; g2_alt2 walkable; g4_alt1 parentHash.
    serverProt<IfOpenSub>(opcode = 17, size = 8) { out ->
        out.writeByte((subId ushr 8) and 0xFF)
        out.writeByteAdd(subId and 0xFF)
        out.writeByte((walkable ushr 8) and 0xFF)
        out.writeByteAdd(walkable and 0xFF)
        out.writeIntLittle(parentHash)
    }

    // IF_SETPOSITION (op 8, 23B) — per A1 §1.4.
    // Wire: g4_alt1 _unused1; g2LE componentId; g4_alt3 _unused2; g4_alt1 _unused3;
    //       g1_neg layer; g4_alt3 _unused4; g4_alt2 position.
    serverProt<IfSetPosition>(opcode = 8, size = 23) { out ->
        out.writeIntLittle(0)
        out.writeShortLittle(componentId)
        out.writeIntInverseMiddle(0)
        out.writeIntLittle(0)
        out.writeByteInverse(layer)
        out.writeIntInverseMiddle(0)
        out.writeIntMiddle(position)
    }

    // IF_CLOSESUB (op 33, 4B) — per A1 §1.6. Wire: g4_alt1 componentHash.
    serverProt<IfCloseSub>(opcode = 33, size = 4) { out ->
        out.writeIntLittle(componentHash)
    }

    // IF_MOVESUB (op 189, 3B) — per A1 §1.7. Wire: g2 (BE) topInterfaceId; g1 mode.
    serverProt<IfMoveSub>(opcode = 189, size = 3) { out ->
        out.writeShort(topId)
        out.writeByte(mode)
    }

    // IF_SETEVENTS1 (op 34, 10B) — per A1 §1.8 IF_SETEVENTS.
    // Wire: g4_alt1 componentHash; g2 (BE) eventsMask; g2LE endSlot; g2LE startSlot.
    serverProt<IfSetEvents1>(opcode = 34, size = 10) { out ->
        out.writeIntLittle(componentHash)
        out.writeShort(eventsMask)
        out.writeShortLittle(if (endSlot == -1) 0xFFFF else endSlot)
        out.writeShortLittle(if (startSlot == -1) 0xFFFF else startSlot)
    }

    // IF_SETEVENTS2 (op 35, 12B) — per A1 §1.8 IF_SETEVENTS2.
    // Wire: g2LE startSlot; g2 (BE) endSlot; g4_alt1 componentHash; g4_alt3 extraFlags.
    // The data class carries the legacy IFEvents builder; map its fields to the A1 layout.
    serverProt<IfSetEvents>(opcode = 35, size = 12) { out ->
        val componentHash = (events.interfaceId shl 16) or (events.componentId and 0xFFFF)
        out.writeShortLittle(if (events.fromSlot == -1) 0xFFFF else events.fromSlot)
        out.writeShort(if (events.toSlot == -1) 0xFFFF else events.toSlot)
        out.writeIntLittle(componentHash)
        out.writeIntInverseMiddle(events.settings)
    }

    // IF_SETHIDE (op 103, 5B) — per A1 §1.9.
    // Wire: g1s hideFlagRaw (0x81 == hide=true, anything else == hide=false); g4_alt1 componentHash.
    serverProt<IfSetHide>(opcode = 103, size = 5) { out ->
        out.writeByte(if (hide) 0x81 else 0x00)
        out.writeIntLittle(componentHash)
    }

    // IF_SETANGLE (op 117, 32B) — per A1 §1.10.
    // Wire: g4 _discard0; g4 angle3; g4_alt2 _discard1; g4_alt2 _discard2; g1 colourIndex;
    //       g4 _discard3; g4 angle1; g1+128 componentIdLow; g1 componentIdHigh; g1 angleZoom;
    //       g4_alt2 packedAngle2.
    serverProt<IfSetAngle>(opcode = 117, size = 32) { out ->
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

    // IF_SET_HTTP_IMAGE (op 146, varByte) — per A1 §1.11. (Stub name: IF_SETGRAPHIC_ACTIVE.)
    // Wire: gStr imageUrl (CP1252, null-terminated).
    serverProt<IfSetHttpImage>(opcode = 146, size = ProtSize.VarByte) { out ->
        out.writeRSString(imageUrl)
    }

    // ---------------------------------------------------------------------
    // Property setters (A1 §2.1)
    // ---------------------------------------------------------------------

    // IF_SETOBJECT_ACTIVE (op 16, 4B) — wire: g4_alt3 componentHash.
    serverProt<IfSetObjectActive>(opcode = 16, size = 4) { out ->
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETMODEL (op 74, 8B) — wire: g4_alt3 value; g4_alt3 componentHash.
    serverProt<IfSetModel>(opcode = 74, size = 8) { out ->
        out.writeIntInverseMiddle(value)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETANIM_ACTIVE (op 81, 4B) — wire: g4_alt3 componentHash.
    serverProt<IfSetAnimActive>(opcode = 81, size = 4) { out ->
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETNPCHEAD (op 98, 10B) — wire: g2_alt2 scale; g4 (BE) componentHash; g2 (BE) partA;
    //                                     g2_alt2 partB.
    serverProt<IfSetNpcHead>(opcode = 98, size = 10) { out ->
        out.writeByte((scale ushr 8) and 0xFF)
        out.writeByteAdd(scale and 0xFF)
        out.writeInt(componentHash)
        out.writeShort(partA)
        out.writeByte((partB ushr 8) and 0xFF)
        out.writeByteAdd(partB and 0xFF)
    }

    // IF_SETOBJECT (op 100, 10B) — wire: g2_alt2 objectSlot; g4_alt2 objectCount;
    //                                      g4_alt3 componentHash.
    serverProt<IfSetObject>(opcode = 100, size = 10) { out ->
        out.writeByte((objectSlot ushr 8) and 0xFF)
        out.writeByteAdd(objectSlot and 0xFF)
        out.writeIntMiddle(objectCount)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETANIM (op 106, 10B) — wire: g4_alt2 componentHash; g4_alt3 frame; g2_alt2 animId.
    serverProt<IfSetAnim>(opcode = 106, size = 10) { out ->
        out.writeIntMiddle(componentHash)
        out.writeIntInverseMiddle(frame)
        out.writeByte((animId ushr 8) and 0xFF)
        out.writeByteAdd(animId and 0xFF)
    }

    // IF_SETCOLOUR (op 122, 8B) — wire: g4_alt3 colour24; g4_alt2 componentHash.
    serverProt<IfSetColour>(opcode = 122, size = 8) { out ->
        out.writeIntInverseMiddle(colour24)
        out.writeIntMiddle(componentHash)
    }

    // IF_SETOBJECT_SMALL (op 141, 5B) — wire: g4_alt3 componentHash; g1 smallIdx.
    serverProt<IfSetObjectSmall>(opcode = 141, size = 5) { out ->
        out.writeIntInverseMiddle(componentHash)
        out.writeByte(smallIdx)
    }

    // IF_SETANIM_SMALL (op 193, 5B) — wire: g1 smallIdx; g4_alt1 componentHash.
    serverProt<IfSetAnimSmall>(opcode = 193, size = 5) { out ->
        out.writeByte(smallIdx)
        out.writeIntLittle(componentHash)
    }

    // ---------------------------------------------------------------------
    // Direct update-entry setters (A1 §2.2)
    // ---------------------------------------------------------------------

    // IF_SETPLAYERHEAD_ACTIVE (op 14, 5B) — wire: g1 flag (==0x01); g4_alt1 componentHash.
    serverProt<IfSetPlayerHeadActive>(opcode = 14, size = 5) { out ->
        out.writeByte(flag)
        out.writeIntLittle(componentHash)
    }

    // IF_SETRECOL (op 44, 6B) — wire: g2LE rgb555; g4_alt2 componentHash.
    serverProt<IfSetRecol>(opcode = 44, size = 6) { out ->
        out.writeShortLittle(rgb555)
        out.writeIntMiddle(componentHash)
    }

    // IF_SET2DANGLE (op 53, 8B) — wire: g4_alt3 angle; g4_alt1 componentHash.
    serverProt<IfSet2DAngle>(opcode = 53, size = 8) { out ->
        out.writeIntInverseMiddle(angle)
        out.writeIntLittle(componentHash)
    }

    // IF_SET_MODEL_FRAME (op 64, 8B) — wire: g4_alt2 frame; g4_alt3 componentHash.
    serverProt<IfSetModelFrame>(opcode = 64, size = 8) { out ->
        out.writeIntMiddle(frame)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETNPCMODEL (op 76, 10B) — wire: g4 componentHash; g4 modelId; g2 npcId.
    serverProt<IfSetNpcModel>(opcode = 76, size = 10) { out ->
        out.writeInt(componentHash)
        out.writeInt(modelId)
        out.writeShort(npcId)
    }

    // IF_SETMODELORIGIN (op 88, 10B) — wire: g4_alt2 componentHash; g2_alt2 x; g2_alt2_neg y;
    //                                          g2LE z.
    // g2_alt2 reader: BE-order with low-byte `+0x80` transform — server writes high then byteAdd low.
    // g2_alt2_neg: same BE order but low byte is "negated then offset" — equivalent to g2_alt2's
    // counterpart for negative values; emit byteSubtract on the low byte.
    serverProt<IfSetModelOrigin>(opcode = 88, size = 10) { out ->
        out.writeIntMiddle(componentHash)
        out.writeByte((x ushr 8) and 0xFF)
        out.writeByteAdd(x and 0xFF)
        out.writeByte((y ushr 8) and 0xFF)
        out.writeByteSubtract(y and 0xFF)
        out.writeShortLittle(z)
    }

    // IF_SETGRAPHIC (op 92, 8B) — wire: g4_alt3 graphicId; g4_alt3 componentHash.
    serverProt<IfSetGraphic>(opcode = 92, size = 8) { out ->
        out.writeIntInverseMiddle(graphicId)
        out.writeIntInverseMiddle(componentHash)
    }

    // IF_SETSPRITE (op 123, 8B) — wire: g4 componentHash; g4 spriteValue.
    serverProt<IfSetSprite>(opcode = 123, size = 8) { out ->
        out.writeInt(componentHash)
        out.writeInt(spriteValue)
    }

    // IF_SETSCROLLSIZE (op 136, 9B) — wire: g2_alt2 scrollW; g2_alt2_neg scrollH;
    //                                         g4_alt3 componentHash; g1_inv subSlot.
    // g1_inv reader: `0x80 - rawByte` ≡ `writeByteSubtract(v)` (writes `-v + 128`, reader
    // recovers `v`).
    serverProt<IfSetScrollSize>(opcode = 136, size = 9) { out ->
        out.writeByte((scrollW ushr 8) and 0xFF)
        out.writeByteAdd(scrollW and 0xFF)
        out.writeByte((scrollH ushr 8) and 0xFF)
        out.writeByteSubtract(scrollH and 0xFF)
        out.writeIntInverseMiddle(componentHash)
        out.writeByteSubtract(subSlot)
    }

    // IF_SETNPCHEAD_ACTIVE (op 150, 5B) — wire: g1 flag (==0x7F); g4_alt1 componentHash.
    serverProt<IfSetNpcHeadActive>(opcode = 150, size = 5) { out ->
        out.writeByte(flag)
        out.writeIntLittle(componentHash)
    }

    // IF_SETMODEL_COORD (op 208, 14B) — wire: g2_alt2 npcId; g4 componentHash; g4_alt2 part1;
    //                                            g4_alt2 part2.
    serverProt<IfSetModelCoord>(opcode = 208, size = 14) { out ->
        out.writeByte((npcId ushr 8) and 0xFF)
        out.writeByteAdd(npcId and 0xFF)
        out.writeInt(componentHash)
        out.writeIntMiddle(part1)
        out.writeIntMiddle(part2)
    }

    // IF_SETSCROLLPOS (op 210, 9B) — wire: g2 (BE) scrollY; g4 (BE) componentHash;
    //                                         g2LE scrollX; g1+128 subSlot.
    serverProt<IfSetScrollPos>(opcode = 210, size = 9) { out ->
        out.writeShort(scrollY)
        out.writeInt(componentHash)
        out.writeShortLittle(scrollX)
        out.writeByteAdd(subSlot)
    }

    // ---------------------------------------------------------------------
    // Complex / direct-allocation setters (A1 §2.3) — opaque payload
    // ---------------------------------------------------------------------

    serverProt<IfSetPlayerModelOther>(opcode = 97, size = 25) { out ->
        out.writeFully(payload)
    }
    serverProt<IfSetPlayerModelSelf>(opcode = 107, size = 25) { out ->
        out.writeFully(payload)
    }
    serverProt<IfSetPlayerModelSnapshot>(opcode = 110, size = 29) { out ->
        out.writeFully(payload)
    }

    // IF_SUBSWAP (op 85, 8B) — wire: g4_alt2 componentA; g4_alt2 componentB.
    serverProt<IfSubSwap>(opcode = 85, size = 8) { out ->
        out.writeIntMiddle(componentA)
        out.writeIntMiddle(componentB)
    }

    // ---------------------------------------------------------------------
    // Trigger / close variants (A1 §2.4)
    // ---------------------------------------------------------------------

    // IF_TRIGGER_CLOSE (op 49, 0B).
    serverProt<IfTriggerClose>(opcode = 49, size = 0)

    // IF_CLOSESUB_BY_ID (op 169, 2B) — wire: g2LE id.
    serverProt<IfCloseSubById>(opcode = 169, size = 2) { out ->
        out.writeShortLittle(id)
    }

    // ---------------------------------------------------------------------
    // Text setter (A1 §3 row IF_SETTEXT, op 2, varShort)
    // ---------------------------------------------------------------------

    // Wire: g4 (BE) componentHash; gStr text. The handler at 0x0022ba20 reads a BE u32 hash
    // followed by a CP1252-null-terminated string and dispatches into SetComponentText.
    serverProt<IfSetText>(opcode = 2, size = ProtSize.VarShort) { out ->
        out.writeInt(componentHash)
        out.writeRSString(text)
    }
}
