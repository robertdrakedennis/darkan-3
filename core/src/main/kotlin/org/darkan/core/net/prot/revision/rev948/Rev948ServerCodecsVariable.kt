package org.darkan.core.net.prot.revision.rev948

import io.ktor.utils.io.*
import org.darkan.core.net.prot.*
import world.gregs.voidps.buffer.*

/**
 * Rev948 server encoders for variable packets (varps, varcs, stats).
 *
 * ALL layouts below were derived by disassembling the 948 handlers in rs2client.948-2-2
 * (binary_name="rs2client") and confirming the packet FAMILY (plain varp / varp-bit /
 * plain varc / varc-bit / varc-string) against the unstripped beta (librs2client.so,
 * jag::packethandlers::Variables::Variables lambdas #2-#9). Wire byte orders / transforms
 * come ONLY from the 948 disassembly — the beta (rev ~890) layouts differ and are NOT used
 * for bytes, only for family identity.
 *
 * Handlers are bound by jag::packethandlers::ClientState::BindHandlers @ 0x000aa85e.
 *
 * 948 ClientState variable-family handler map (op -> handler -> family):
 *   op 10  FUN_00119870 (3B)  setBitFromPacket  VARP-BIT small  -> VarpBitSmall
 *   op 28  FUN_00119910 (6B)  PlayerVarDomain::set  VARP int     -> VarpLarge
 *   op 44  0x000ef2a0   (6B)  StatTable::UpdateStat               -> UpdateStat
 *   op 47  FUN_001196f0 (3B)  IfaceMgr SetUpdateSlotValue  VARC small  -> ClientSetVarcSmall
 *   op 48  FUN_00119560 (3B)  FUN_00af8950 (bit-range XOR)  VARC-BIT small -> ClientSetVarcBitSmall
 *   op 51  FUN_001197b0 (6B)  setBitFromPacket  VARP-BIT large  -> VarpBitLarge
 *   op 61  FUN_001199b0 (3B)  PlayerVarDomain::set  VARP small (1B) -> VarpSmall
 *   op 64  FUN_00119600 (6B)  IfaceMgr SetUpdateSlotValue  VARC large  -> ClientSetVarcLarge
 *   op 69  FUN_001194b0 (6B)  FUN_00af8950 (bit-range XOR)  VARC-BIT large -> ClientSetVarcBitLarge
 *   op 92  FUN_001501c0       Misc::SET_VARC_STR_SMALL  VARC-string (id-first) -> ClientSetVarcStr
 *   op 116 FUN_001500a0 (-2)  IfaceMgr type=2  VARC-string (string-first) -> ClientSetVarcStrLarge
 *   op 147 FUN_00141510 (10B) PlayerVarDomain::set  VARP long (8B)  -> VarpLong
 *
 * CRASH-BLOCKER RESOLVED: the heaviest lobby-login traffic in the rev947 captures is the PLAIN
 * VARP-SMALL packet (947 op 10 "VarpSmall", 1-byte value). In 948 op 10 became VARP_BIT_SMALL and
 * the plain 1-byte player var MOVED to op 61 (handler FUN_001199b0, calling the SAME plain
 * PlayerVarDomain::set as op 28). Plain VarpLong likewise moved to op 147 (FUN_00141510, 8-byte
 * value packed from two g4_alt3 halves). Both were located + opcode/size confirmed via RegisterAll
 * InitEntry this pass and are now registered below.
 */
internal fun Codec.registerRev948ServerCodecsVariable() {

    // ---------------------------------------------------------------------------------------
    // CONFIRMED — registered (asm-verified wire format, family confirmed vs beta Variables).
    // ---------------------------------------------------------------------------------------

    // VARP_LARGE (op 28, 6B) — plain player var, 4-byte int. Handler FUN_00119910 @ 0x00119910.
    //   asm: MOV EBX,[+0..+3]; BSWAP EBX (value = BE int) ; MOVZX word[+4..+5]; ROL (id = BE u16)
    //   -> calls jag::game::PlayerVarDomain::set (0x004e1a40).
    //   CHANGED vs 947-3 (op 111): value writeIntMiddle -> writeInt (BE); id LE -> BE. Order same.
    serverProt<VarpLarge>(opcode = 28, size = 6) { out ->
        out.writeInt(value)
        out.writeShort(id)
    }

    // VARP_SMALL (op 61, 3B) — plain player var, 1-byte value. Handler FUN_001199b0 @ 0x001199b0.
    //   LOCATED this pass: this is the dominant lobby-login packet (947 op10 = plain VarpSmall;
    //   948 moved it to op 61 while op 10 became VARP_BIT_SMALL). Opcode + size verified
    //   independently via RegisterAll InitEntry @ 0x000c537b (ESI=0x3d=61, EDX=3) against entry
    //   base 0x015c16c0 (= invokePtr 0x015c16e8 - 0x28). Bound by ClientState::BindHandlers.
    //   asm: id = ushort[+0..+1] BSWAP (BE u16) ; value = (signed char)(-0x80 - byte[+2]) ;
    //        local_20=0 (plain, not bit) -> jag::game::PlayerVarDomain::set (0x4e1a40, the SAME
    //        plain setter as VARP_LARGE — NOT setBitFromPacket).
    //   The client recovers value as (-128 - wireByte), so the wire byte = (-128 - value).
    serverProt<VarpSmall>(opcode = 61, size = 3) { out ->
        out.writeShort(id)
        out.writeByte(-128 - value)
    }

    // VARP_LONG (op 147, 10B) — plain player var, 8-byte value. Handler FUN_00141510 @ 0x00141510.
    //   LOCATED this pass. Opcode + size verified via RegisterAll InitEntry @ 0x000c6386
    //   (ESI=0x93=147, EDX=0xa=10) against entry base 0x015c1640. Bound by ClientState::BindHandlers.
    //   asm: id = (byte[+0]<<8) | ((byte[+1]+0x80) & 0xFF) = BE u16 low-byte byteAdd = shortAdd ;
    //        value = CONCAT44(g4_alt3(), g4_alt3()) — first g4_alt3 = HIGH 32 bits, second = LOW
    //        32 bits ; local_20=1 (long) -> jag::game::PlayerVarDomain::set (plain setter).
    //   g4_alt3 (verified @ 0x0013f5b0: wire [B16,B24,B0,B8]) -> writeIntInverseMiddle.
    serverProt<VarpLong>(opcode = 147, size = 10) { out ->
        out.writeShortAdd(id)
        out.writeIntInverseMiddle((value ushr 32).toInt())
        out.writeIntInverseMiddle(value.toInt())
    }

    // UPDATE_STAT (op 44, 6B) — handler jag::game::StatTable::UpdateStat @ 0x000ef2a0
    //   (NOT 0x001b1830 — that address is jag::LoginManager::ResetLoginState; prior stub wrong).
    //   asm: xp = byte[+0]|byte[+1]<<8|byte[+2]<<16|byte[+3]<<24 (LE int) ;
    //        currentLevel = raw byte[+4] ; skillId = NEG byte[+5] (byteInverse).
    //   CHANGED vs 947-3 (op 66) in ALL three fields:
    //        xp     writeInt (BE)        -> writeIntLittle
    //        level  writeByteSubtract    -> writeByte (raw)
    //        skillId writeByteAdd        -> writeByteInverse
    serverProt<UpdateStat>(opcode = 44, size = 6) { out ->
        out.writeIntLittle(xp)
        out.writeByte(level)
        out.writeByteInverse(skillId)
    }

    // CLIENT_SETVARC_SMALL (op 47, 3B) — plain client var, 1-byte. Handler FUN_001196f0 @ 0x001196f0.
    //   asm: value byte[+0], transform ADD -0x80 (= readByteAdd) ;
    //        id = byte[+2]<<8 + byte[+1] (wire low-first -> LE u16) ;
    //   -> InterfaceManager::CreateOrFindUpdateEntry + SetUpdateSlotValue (plain varc, no bit XOR).
    //   CHANGED vs 947-3 (op 1): value writeByteSubtract -> writeByteAdd. id LE UNCHANGED.
    serverProt<ClientSetVarcSmall>(opcode = 47, size = 3) { out ->
        out.writeByteAdd(value)
        out.writeShortLittle(id)
    }

    // CLIENT_SETVARC_LARGE (op 64, 6B) — plain client var, 4-byte. Handler FUN_00119600 @ 0x00119600.
    //   asm value bytes (wire pos -> value bits): [+0]->8..15(B1), [+1]->0..7(B0),
    //        [+2]->24..31(B3), [+3]->16..23(B2) => wire emits [B1,B0,B3,B2] = writeIntMiddle ;
    //   asm id: byte[+4] ADD -0x80 (low, byteAdd) + byte[+5]<<8 (high) -> LE u16 low-byte-add
    //        = writeShortAddLittle ;
    //   -> InterfaceManager plain varc (no bit XOR).
    //   CHANGED vs 947-3 (op 112): value writeInt(BE) -> writeIntMiddle;
    //        id writeShortLittle -> writeShortAddLittle.
    serverProt<ClientSetVarcLarge>(opcode = 64, size = 6) { out ->
        out.writeIntMiddle(value)
        out.writeShortAddLittle(id)
    }

    // CLIENT_SETVARC_STR (op 92, varByte) — handler jag::packethandlers::Misc::SET_VARC_STR_SMALL
    //   @ 0x001501c0. asm: id = byte[+0] + byte[+1]<<8 (LE u16, NO transform), advance +2, THEN
    //   FUN_00ad89a0 reads CP1252 null-terminated string. -> InterfaceManager type=2 (string slot).
    //   Wire: shortLE(id) + string(value).  (was op 67 in 947-3; field order id-first matches 948.)
    serverProt<ClientSetVarcStr>(opcode = 92, size = ProtSize.VarByte) { out ->
        out.writeShortLittle(id)
        out.writeRSString(value)
    }

    // ---------------------------------------------------------------------------------------
    // VARBIT / VARC-BIT / VARC-string-large family — newly registered. Wire formats re-verified
    // against the 948 binary (rs2client.948-2-2) this pass, decompiling each handler; they now
    // have dedicated Kotlin data classes in core/.../prot/ServerProt.kt.
    // ---------------------------------------------------------------------------------------

    // VARP_BIT_SMALL (op 10, 3B) — player varbit, 1-byte value. Handler VARP_BIT_SMALL @ 0x00119870.
    //   asm: id = ushort[+0..+1] BSWAP (BE u16) ; value = -0x80 - (signed char)byte[+2]
    //        -> jag::game::PlayerVarDomain::setBitFromPacket (0x4e1b40).
    //   The client recovers value as (-128 - wireByte), so the wire byte = (-128 - value).
    serverProt<VarpBitSmall>(opcode = 10, size = 3) { out ->
        out.writeShort(id)
        out.writeByte(-128 - value)
    }

    // VARP_BIT_LARGE (op 51, 6B) — player varbit, 4-byte value. Handler VARP_BIT_LARGE @ 0x001197b0.
    //   asm: id = ushort[+0..+1] BSWAP (BE u16) ; value bits B24=byte[+3] B16=byte[+2] B8=byte[+5]
    //        B0=byte[+4] => wire order [B16,B24,B0,B8] = writeIntInverseMiddle.
    //        -> PlayerVarDomain::setBitFromPacket.
    serverProt<VarpBitLarge>(opcode = 51, size = 6) { out ->
        out.writeShort(id)
        out.writeIntInverseMiddle(value)
    }

    // CLIENT_SETVARCBIT_SMALL (op 48, 3B) — client varbit, 1-byte value. Handler @ 0x00119560.
    //   asm: value = raw signed byte[+0] ; id = (byte[+1] << 8) | ((byte[+2] + 0x80) & 0xFF)
    //        = BE u16 with low byte byteAdd = writeShortAdd. -> FUN_00af8950 (bit-range XOR slot).
    serverProt<ClientSetVarcBitSmall>(opcode = 48, size = 3) { out ->
        out.writeByte(value)
        out.writeShortAdd(id)
    }

    // CLIENT_SETVARCBIT_LARGE (op 69, 6B) — client varbit, 4-byte value. Handler @ 0x001194b0.
    //   asm: id = ushort[+0..+1] BSWAP (BE u16) ; value = int[+2..+5] BSWAP (BE int).
    //        -> FUN_00af8950 (bit-range XOR slot).
    serverProt<ClientSetVarcBitLarge>(opcode = 69, size = 6) { out ->
        out.writeShort(id)
        out.writeInt(value)
    }

    // CLIENT_SETVARC_STR_LARGE (op 116, varShort) — client string var, STRING-FIRST. Handler
    //   SET_VARC_STR_LARGE @ 0x001500a0. asm: FUN_00ad89a0 reads CP1252 null-terminated string
    //   FIRST, then id = (byte[+0] << 8) | ((byte[+1] + 0x80) & 0xFF) = BE u16 low-byte byteAdd
    //   = writeShortAdd. -> InterfaceManager type=2 (string slot). DISTINCT from op 92 (id-first).
    serverProt<ClientSetVarcStrLarge>(opcode = 116, size = ProtSize.VarShort) { out ->
        out.writeRSString(value)
        out.writeShortAdd(id)
    }
}
