package org.darkan.core.net.prot.revision.rev948

import org.darkan.core.net.prot.Codec
import world.gregs.voidps.buffer.*
import world.gregs.voidps.gameval.Gameval

/**
 * Rev948 server-to-client display decoders for the packet dumper.
 *
 * Each decoder is the structural INVERSE of the matching encoder in `Rev948ServerCodecs*.kt`: it
 * reads the payload off a kotlinx.io `Source` (exactly like the client decoders) using the inverse
 * buffer-reader ops and returns a readable, gameval-linked line. Every id/value is printed RAW
 * alongside its gameval name (names can be missing or wrong), e.g.
 *   `varp=lobbyscreen_account_creation_runeday(1749) value=5`  or  `varp=?(9999) value=5`.
 *
 * NOTE: componentHash / value fields written with `writeIntInverseMiddle` are read back with
 * [readUIntInverseMiddle] — the UNSIGNED variant. The signed [readIntInverseMiddle] sign-extends
 * the middle byte and corrupts any value whose bits 16..23 are >= 0x80 (e.g. interface 906 in a
 * componentHash), so it must NOT be used here.
 *
 * Bit-packed packets (REBUILD_NORMAL op81, PLAYER_INFO op22/NPC_INFO) and the multi-section
 * WORLDLIST_FETCH_REPLY (op216) are intentionally not decoded here.
 */
internal fun Codec.registerRev948ServerDecoders() {
    // ------------------------------------------------------------------ VARP (player vars)
    // VARP_SMALL (op 61, 3B): id=VARP, 1-byte value (writeByte(-128-value) == writeByteSubtract).
    serverDecode(61) {
        val id = readUShort()
        val value = readByteSubtract()
        "varp=${varpRef(id)} value=$value"
    }
    // VARP_LARGE (op 28, 6B): value=BE int FIRST, then id=VARP.
    serverDecode(28) {
        val value = readInt()
        val id = readUShort()
        "varp=${varpRef(id)} value=$value"
    }
    // VARP_LONG (op 147, 10B): id=VARP (shortAdd), value:Long = [high32, low32] each g4_alt3.
    serverDecode(147) {
        val id = readUShortAdd() and 0xFFFF
        val high = readUIntInverseMiddle()
        val low = readUIntInverseMiddle()
        val value = (high.toLong() shl 32) or (low.toLong() and 0xFFFFFFFFL)
        "varp=${varpRef(id)} value=$value"
    }

    // ------------------------------------------------------------------ VARP-BIT (player varbits)
    // VARP_BIT_SMALL (op 10, 3B): id=VARBIT, 1-byte value (byteSubtract).
    serverDecode(10) {
        val id = readUShort()
        val value = readByteSubtract()
        "varbit=${varbitRef(id)} value=$value"
    }
    // VARP_BIT_LARGE (op 51, 6B): id=VARBIT, value=g4_alt3.
    serverDecode(51) {
        val id = readUShort()
        val value = readUIntInverseMiddle()
        "varbit=${varbitRef(id)} value=$value"
    }

    // ------------------------------------------------------------------ VARC (client vars)
    // CLIENT_SETVARC_SMALL (op 47, 3B): value (byteAdd), id=VARC (LE).
    serverDecode(47) {
        val value = readByteAdd()
        val id = readUShortLittle()
        "varc=${varcRef(id)} value=$value"
    }
    // CLIENT_SETVARC_LARGE (op 64, 6B): value (intMiddle), id=VARC (shortAddLittle).
    serverDecode(64) {
        val value = readUIntMiddle()
        val id = readUShortAddLittle()
        "varc=${varcRef(id)} value=$value"
    }
    // CLIENT_SETVARCBIT_SMALL (op 48, 3B): value (raw byte), id (shortAdd) — client varbit.
    serverDecode(48) {
        val value = readByte().toInt()
        val id = readUShortAdd() and 0xFFFF
        "varcbit=${varcRef(id)} value=$value"
    }
    // CLIENT_SETVARCBIT_LARGE (op 69, 6B): id (BE u16), value (BE int) — client varbit.
    serverDecode(69) {
        val id = readUShort()
        val value = readInt()
        "varcbit=${varcRef(id)} value=$value"
    }
    // CLIENT_SETVARC_STR (op 92, varByte): id=VARC (LE) + string.
    serverDecode(92) {
        val id = readUShortLittle()
        val text = readRSString()
        "varc=${varcRef(id)} value=\"$text\""
    }

    // ------------------------------------------------------------------ STAT
    // UPDATE_STAT (op 44, 6B): xp (LE int), level (raw byte), skillId (byteInverse).
    serverDecode(44) {
        val xp = readUIntLittle()
        val level = readUByte()
        val skillId = readByteInverse()
        "skill=${skillRef(skillId)} level=$level xp=$xp"
    }

    // ------------------------------------------------------------------ INTERFACE
    // IF_SETTOPLEVELINTERFACE (op 3, 19B): topLevelId packed at offset 9..10 (low byteAdd, high plain).
    serverDecode(3) {
        skip(9L)
        val low = readByteAdd() and 0xFF
        val high = readUByte()
        skip(8L)
        "topLevelInterface=${ifaceRef((high shl 8) or low)}"
    }
    // IF_OPENTOP (op 39, 6B): topLevelId=interface (LE), subId (BE).
    serverDecode(39) {
        val topLevelId = readUIntLittle()
        val subId = readUShort()
        "topLevelInterface=${ifaceRef(topLevelId)} subId=$subId"
    }
    // IF_OPENSUB (op 94, 8B): subId=interface (LE), walkable (LE), parentHash=componentHash (BE).
    serverDecode(94) {
        val subId = readUShortLittle()
        val walkable = readUShortLittle()
        val parentHash = readInt()
        "sub=${ifaceRef(subId)} walkable=$walkable parent=${compRef(parentHash)}"
    }
    // IF_SETPOSITION (op 82, 23B): layer (byteSubtract), pad4, position=componentHash (g4_alt3),
    //   pad12, componentId=child interface (LE).
    serverDecode(82) {
        val layer = readByteSubtract()
        skip(4L)
        val position = readUIntInverseMiddle()
        skip(12L)
        val componentId = readUShortLittle()
        "componentHash=${compRef(position)} child=${ifaceRef(componentId)} layer=$layer"
    }
    // IF_SETEVENTS (op 35, 12B): settings (intMiddle), fromSlot (LE), toSlot (LE), componentHash (BE).
    serverDecode(35) {
        val settings = readUIntMiddle()
        val fromSlot = readUShortLittle()
        val toSlot = readUShortLittle()
        val componentHash = readInt()
        "componentHash=${compRef(componentHash)} settings=$settings fromSlot=${slotRef(fromSlot)} toSlot=${slotRef(toSlot)}"
    }
    // IF_SETEVENTS1 (op 97, 10B): componentHash (g4_alt3), eventsMask (shortAdd), endSlot (LE),
    //   startSlot (shortAdd).
    serverDecode(97) {
        val componentHash = readUIntInverseMiddle()
        val eventsMask = readUShortAdd() and 0xFFFF
        val endSlot = readUShortLittle()
        val startSlot = readUShortAdd() and 0xFFFF
        "componentHash=${compRef(componentHash)} mask=$eventsMask startSlot=${slotRef(startSlot)} endSlot=${slotRef(endSlot)}"
    }
    // IF_SETHIDE (op 91, 5B): hideByte (0x81=hidden), componentHash (intMiddle).
    serverDecode(91) {
        val hide = readUByte() == 0x81
        val componentHash = readUIntMiddle()
        "componentHash=${compRef(componentHash)} hide=$hide"
    }
    // IF_CLOSESUB (op 62, 4B): componentHash (LE).
    serverDecode(62) {
        val componentHash = readUIntLittle()
        "componentHash=${compRef(componentHash)}"
    }
    // IF_SET_COMPONENT_PROPERTY_TYPE3 (op 96, 4B): componentHash (g4_alt3).
    serverDecode(96) {
        val componentHash = readUIntInverseMiddle()
        "componentHash=${compRef(componentHash)} propertyType=3"
    }
    // IF_SET_COMPONENT_PROPERTY_TYPE5 (op 101, 4B): componentHash (g4_alt2).
    serverDecode(101) {
        val componentHash = readUIntMiddle()
        "componentHash=${compRef(componentHash)} propertyType=5"
    }
    // IF_SETMODEL (op 102, 8B): model id/value (g4_alt2), componentHash (BE).
    serverDecode(102) {
        val modelId = readUIntMiddle()
        val componentHash = readInt()
        "componentHash=${compRef(componentHash)} model=$modelId"
    }
    // IF_SETGRAPHIC (op 30, 8B): componentHash (g4_alt3), graphicId (g4_alt3).
    serverDecode(30) {
        val componentHash = readUIntInverseMiddle()
        val graphicId = readUIntInverseMiddle()
        "componentHash=${compRef(componentHash)} graphic=$graphicId"
    }
    // IF_SETANIM (op 103, 8B): animationId=seq (g4_alt3), componentHash (g4_alt3).
    serverDecode(103) {
        val animationId = readUIntInverseMiddle()
        val componentHash = readUIntInverseMiddle()
        "componentHash=${compRef(componentHash)} anim=${seqRef(animationId)}"
    }
    // IF_SETTEXT (op 122, varShort): text FIRST, then componentHash (g4_alt3).
    serverDecode(122) {
        val text = readRSString()
        val componentHash = readUIntInverseMiddle()
        "componentHash=${compRef(componentHash)} text=\"$text\""
    }
    // IF_SET_COMPONENT_PROPERTY_TYPE7 (op 115, 10B): field0 BE, field1/field2 LE, componentHash LE.
    serverDecode(115) {
        val field0 = readUShort()
        val field1 = readUShortLittle()
        val field2 = readUShortLittle()
        val componentHash = readUIntLittle()
        "componentHash=${compRef(componentHash)} field0=$field0 field1=$field1 field2=$field2"
    }
    // IF_SET_HTTP_IMAGE (op 152, varByte): CP1252 null-terminated image/resource path.
    serverDecode(152) {
        val imageUrl = readRSString()
        "imageUrl=\"$imageUrl\""
    }

    // ------------------------------------------------------------------ MISC
    // UPDATE_RUNENERGY (op 13, 1B, g1): run energy 0..100 RAW. The real run-energy opcode
    // (recorder-capture-points.md §10.4); handler jag::packethandlers::Misc::UPDATE_RUNENERGY.
    serverDecode(13) {
        "runEnergy=${readUByte()}"
    }
    // SETFILTER_PRIVATE (op 80, 1B, g1): private-chat filter {0=On,1=Friends,2=Off}. NOT run energy
    // (corrected 2026-06-27, §10.4); handler jag::packethandlers::Misc::SETFILTER_PRIVATE.
    serverDecode(80) {
        "filterPrivate=${readUByte()}"
    }
    // JCOINS_UPDATE (op 191, 4B): BE int balance.
    serverDecode(191) {
        "jcoins=${readInt()}"
    }
    // SCENE_TIMING_BASE (op 74, 4B): BE int stored into Client+0x19db8+0x64.
    serverDecode(74) {
        "sceneTimingBase=${readInt()}"
    }
    // SET_PLAYER_OP (op 17, varByte): worldId (LE), slot (byteInverse - 1), text, cursorVisible.
    serverDecode(17) {
        val worldId = readShortLittle()
        val slot = readByteInverse() - 1
        val text = readRSString()
        val cursorVisible = readUByte() == 0
        "slot=$slot text=\"$text\" priority=$cursorVisible worldId=$worldId"
    }
    // CHAT_FILTER_SETTINGS / SET_CHAT_FILTER_B (op 156, 1B).
    serverDecode(156) {
        "chatFilter=${readUByte()}"
    }
    // RUNCLIENTSCRIPT (op 110, varShort): types string, args in REVERSED type order, then scriptId.
    serverDecode(110) {
        val types = readRSString()
        val args = ArrayList<Any>(types.length)
        for (c in types.reversed()) {
            when (c) {
                'i' -> args.add(readInt())
                's' -> args.add("\"${readRSString()}\"")
                'l' -> args.add(readLong())
            }
        }
        val scriptId = readInt()
        args.reverse()
        "script=$scriptId types=\"$types\" args=$args"
    }
}

/** Skill names in server skill-id order (0..28). Purely cosmetic; the raw id is always shown too. */
private val SKILL_NAMES = arrayOf(
    "attack", "defence", "strength", "constitution", "ranged", "prayer", "magic", "cooking",
    "woodcutting", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining",
    "herblore", "agility", "thieving", "slayer", "farming", "runecrafting", "hunter",
    "construction", "summoning", "dungeoneering", "divination", "invention", "archaeology",
    "necromancy",
)

private fun varpRef(id: Int) = "${Gameval.varp(id) ?: "?"}($id)"
private fun varcRef(id: Int) = "${Gameval.varc(id) ?: "?"}($id)"
private fun varbitRef(id: Int) = "${Gameval.varbit(id) ?: "?"}($id)"
private fun ifaceRef(id: Int) = "${Gameval.interfaceName(id) ?: "?"}($id)"
private fun seqRef(id: Int) = if (id == -1) "none(-1)" else "${Gameval.seq(id) ?: "?"}($id)"
private fun skillRef(id: Int) = "${SKILL_NAMES.getOrNull(id) ?: "?"}($id)"
private fun slotRef(v: Int) = if (v == 0xFFFF) "-1" else v.toString()

/** Split a componentHash (interface<<16 | component) and name BOTH the component and its interface. */
private fun compRef(hash: Int): String {
    if (hash == -1) return "none(-1)"
    val iface = hash ushr 16
    val comp = hash and 0xFFFF
    val ifaceName = Gameval.interfaceName(iface) ?: "?"
    val compName = Gameval.component(iface, comp) ?: "?"
    return "$compName($iface:$comp) iface=$ifaceName($iface)"
}
