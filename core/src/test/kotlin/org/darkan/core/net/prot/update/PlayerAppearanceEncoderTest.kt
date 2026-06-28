package org.darkan.core.net.prot.update

import org.darkan.core.net.prot.revision.rev948.Rev948ExtInfoTransforms
import org.darkan.core.net.prot.revision.rev948.Rev948PlayerUpdateMaskKey
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Byte-precise tests for the 948 PLAYER_INFO APPEARANCE block, per the binary-verified spec
 * `re-resources/docs/net/serverprot/player-appearance-948.md`:
 *  - Task 1: the ext-info framing transforms (length = mode 3, body = mode 2).
 *  - Task 2: the plain APPEARANCE payload field walk (flags, [title], [customisation], gender,
 *    slots, colourMask, kitColours, kitStyles, bas, name, combat, [combat2/skill], headbar).
 */
class PlayerAppearanceEncoderTest {

    private val EMIT_SLOTS = (0..18).filter { it !in setOf(12, 13, 17) } // 16 emitted human slots

    // ---------------------------------------------------------------------------------------------
    // Task 1 — ext-info framing transforms
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `appearance length byte is mode 3 transform (-128 - L)`() {
        assertEquals(0x5F, Rev948ExtInfoTransforms.appearanceLengthByte(33))
        assertEquals(0x80, Rev948ExtInfoTransforms.appearanceLengthByte(0))
        assertEquals(0x7F, Rev948ExtInfoTransforms.appearanceLengthByte(1))
        assertEquals(0x00, Rev948ExtInfoTransforms.appearanceLengthByte(128))
        for (l in 0..255) {
            val wire = Rev948ExtInfoTransforms.appearanceLengthByte(l)
            assertEquals(l and 0xFF, (-0x80 - wire) and 0xFF, "length round-trips for L=$l")
        }
    }

    @Test
    fun `appearance body byte is mode 2 transform (+128)`() {
        assertEquals(0x80, Rev948ExtInfoTransforms.appearanceBodyByte(0x00))
        assertEquals(0x00, Rev948ExtInfoTransforms.appearanceBodyByte(0x80))
        assertEquals(0x7F, Rev948ExtInfoTransforms.appearanceBodyByte(0xFF))
        for (b in 0..255) {
            val wire = Rev948ExtInfoTransforms.appearanceBodyByte(b)
            assertEquals(b and 0xFF, (wire - 0x80) and 0xFF, "body byte round-trips for b=$b")
        }
    }

    @Test
    fun `framed appearance block is length(mode 3) + body(mode 2), no mode-prefix byte`() {
        val payload = byteArrayOf(0x00, 0x05, 0x10, 0x7F, 0xFF.toByte())
        val out = BufferWriter(16)
        Rev948ExtInfoTransforms.write(out, payload)
        val wire = out.toArray()
        assertEquals(1 + payload.size, wire.size, "framed block is length byte + L body bytes only")
        assertEquals(Rev948ExtInfoTransforms.appearanceLengthByte(payload.size), wire[0].toInt() and 0xFF)
        for (i in payload.indices) {
            assertEquals(
                Rev948ExtInfoTransforms.appearanceBodyByte(payload[i].toInt() and 0xFF),
                wire[1 + i].toInt() and 0xFF,
                "body byte $i is +0x80 transformed",
            )
        }
    }

    @Test
    fun `registered APPEARANCE mask encoder applies the framing transforms`() {
        register948()
        val payload = byteArrayOf(0x00, 0x00, 0x11, 0x22)
        val out = BufferWriter(16)
        PlayerUpdateMaskEncoder.encode(out, Rev948PlayerUpdateMaskKey.APPEARANCE, UpdateMask.Appearance(payload))
        val wire = out.toArray()
        assertEquals(Rev948ExtInfoTransforms.appearanceLengthByte(payload.size), wire[0].toInt() and 0xFF)
        assertEquals(1 + payload.size, wire.size)
        assertEquals(Rev948ExtInfoTransforms.appearanceBodyByte(0x11), wire[3].toInt() and 0xFF)
    }

    // ---------------------------------------------------------------------------------------------
    // Task 2 — plain APPEARANCE payload field walk
    // ---------------------------------------------------------------------------------------------

    /** Cursor-driven reader mirroring the binary `DecodeAppearance` + `DecodeAppearanceEquipment`. */
    private class Walk(val p: ByteArray) {
        var i = 0
        fun u8() = p[i++].toInt() and 0xFF
        fun u16() = (u8() shl 8) or u8()
        fun smart(): Int { val b = p[i].toInt() and 0xFF; return if (b < 0x80) u8() else (u16() - 0x8000) }
        fun str(): String { val sb = StringBuilder(); while (i < p.size && p[i].toInt() != 0) sb.append((p[i++].toInt() and 0xFF).toChar()); i++; return sb.toString() }
    }

    @Test
    fun `default male avatar payload matches the binary field order`() {
        // 16 slots: kit in the body-style slots, empty elsewhere. Default colours/styles, bas 2699.
        val slots = EMIT_SLOTS.map { slotIndex ->
            when (slotIndex) {
                8 -> AppearanceSlot.Kit(3)    // hair
                4 -> AppearanceSlot.Kit(18)   // torso
                else -> AppearanceSlot.Empty
            }
        }
        val model = PlayerAppearance(
            gender = 0,
            slots = slots,
            colourMask = 0,
            kitColours = intArrayOf(3, 16, 16, 0, 0, 0, 0, 0, 0, 0),
            kitStyles = IntArray(10),
            bas = 2699,
            name = "Tester",
            combatLevel = 3,
            combat2 = null,
            skillLevel = 0,
            skillBonus = 0,
            headbar = null,
        )
        val w = Walk(PlayerAppearanceEncoder.encode(model))

        assertEquals(0x00, w.u8(), "flags = 0 (male, no title/cust, combat2 null)")
        assertEquals(0x00, w.u8(), "gender = 0 (male)")
        for ((idx, slotIndex) in EMIT_SLOTS.withIndex()) {
            when (slotIndex) {
                8 -> assertEquals(3 + PlayerAppearanceEncoder.KIT_BASE, w.u16(), "hair slot kit")
                4 -> assertEquals(18 + PlayerAppearanceEncoder.KIT_BASE, w.u16(), "torso slot kit")
                else -> assertEquals(0x00, w.u8(), "empty slot $slotIndex (1 byte)")
            }
        }
        assertEquals(0x0000, w.u16(), "colourMask = 0")
        for (c in intArrayOf(3, 16, 16, 0, 0, 0, 0, 0, 0, 0)) assertEquals(c, w.u8(), "kitColour")
        for (s in IntArray(10)) assertEquals(s, w.u8(), "kitStyle")
        assertEquals(2699, w.u16(), "bas")
        assertEquals("Tester", w.str(), "name")
        assertEquals(3, w.u8(), "combatLevel")
        assertEquals(0, w.u8(), "skillLevel")
        assertEquals(0, w.u8(), "skillBonus")
        assertEquals(0, w.u8(), "hasHeadbar = 0")
        assertEquals(w.p.size, w.i, "cursor must equal payload length (no trailing bytes)")
    }

    @Test
    fun `empty slot is a single zero byte and item slot adds 0x800`() {
        val slots = MutableList<AppearanceSlot>(EMIT_SLOTS.size) { AppearanceSlot.Empty }
        // slot list index 3 corresponds to body slot 3 (weapon) — put the bronze dagger there.
        slots[3] = AppearanceSlot.Item(1205)
        val model = PlayerAppearance(slots = slots, name = "", kitColours = IntArray(10), kitStyles = IntArray(10))
        val w = Walk(PlayerAppearanceEncoder.encode(model))
        w.u8(); w.u8() // flags, gender
        for (s in EMIT_SLOTS.indices) {
            if (s == 3) assertEquals(1205 + PlayerAppearanceEncoder.ITEM_BASE, w.u16(), "item = itemId + 0x800")
            else assertEquals(0x00, w.u8(), "empty slot index $s")
        }
        assertEquals(0x0000, w.u16(), "colourMask = 0")
        for (c in 0 until 10) w.u8() // colours
        for (s in 0 until 10) w.u8() // styles
        assertEquals(PlayerAppearance.DEFAULT_BAS, w.u16(), "default bas")
        assertEquals("", w.str(), "empty name")
        assertEquals(3, w.u8(), "default combat level")
        w.u8(); w.u8() // skill level/bonus
        assertEquals(0, w.u8(), "hasHeadbar")
        assertEquals(w.p.size, w.i)
    }

    @Test
    fun `kit values stay below 0x800 and items at-or-above 0x800 (the split boundary)`() {
        assertEquals(0x7FF, slotWire(AppearanceSlot.Kit(0x6FF)))
        assertTrue(slotWire(AppearanceSlot.Kit(0x6FF)) < PlayerAppearanceEncoder.ITEM_BASE)
        assertEquals(0x800, slotWire(AppearanceSlot.Item(0)), "item 0 encodes at exactly 0x800")
    }

    @Test
    fun `morph slot 0 writes 0xFFFF + bigSmart morph + gender byte and stops slot loop`() {
        // Only slot 0 is the morph; the morph stops the slot loop, so no further slot tokens AND no
        // colourMask are written (morphed path).
        val model = PlayerAppearance(
            gender = 0,
            slots = listOf(AppearanceSlot.Morph(morphId = 50, genderOverride = 1)),
            kitColours = IntArray(2), kitStyles = IntArray(2), name = "M",
        )
        val w = Walk(PlayerAppearanceEncoder.encode(model))
        assertEquals(0x00, w.u8(), "flags")
        assertEquals(0x00, w.u8(), "gender")
        assertEquals(0xFFFF, w.u16(), "morph sentinel")
        assertEquals(50, w.u16(), "bigSmart morphId (< 32767 ⇒ 2-byte)")
        assertEquals(1, w.u8(), "morph gender override byte")
        // No colourMask (morphed). Next are kitColours(2), kitStyles(2), bas, name...
        w.u8(); w.u8(); w.u8(); w.u8()
        assertEquals(PlayerAppearance.DEFAULT_BAS, w.u16(), "bas follows immediately (no colourMask when morphed)")
    }

    @Test
    fun `title and customisation gate their fields via flags`() {
        val model = PlayerAppearance(
            title = 7,
            customisation = listOf(Customisation(id = 0x1234, type = 2)),
            slots = List(EMIT_SLOTS.size) { AppearanceSlot.Empty },
            kitColours = IntArray(0), kitStyles = IntArray(0), name = "",
        )
        val w = Walk(PlayerAppearanceEncoder.encode(model))
        val flags = w.u8()
        assertTrue(flags and PlayerAppearanceEncoder.FLAG_HAS_TITLE != 0, "title sets 0x40")
        assertTrue(flags and PlayerAppearanceEncoder.FLAG_HAS_CUSTOMISATION != 0, "customisation sets 0x02")
        // Field 2 — title gSmart1or2 (7 < 128 → 1 byte) comes BEFORE customisation.
        assertEquals(7, w.u8(), "title")
        // Field 3 — customisation count + {g2 id, g1 type}.
        assertEquals(1, w.u8(), "customisation count")
        assertEquals(0x1234, w.u16(), "customisation id g2")
        assertEquals(2, w.u8(), "customisation type g1")
        assertEquals(0x00, w.u8(), "gender (after customisation)")
    }

    @Test
    fun `female gender sets the female flag bit`() {
        val model = PlayerAppearance(gender = 1, slots = listOf(AppearanceSlot.Empty),
            kitColours = IntArray(0), kitStyles = IntArray(0), name = "")
        val w = Walk(PlayerAppearanceEncoder.encode(model))
        assertTrue(w.u8() and PlayerAppearanceEncoder.FLAG_FEMALE != 0, "female sets flags 0x80")
        assertEquals(1, w.u8(), "gender byte = 1")
    }

    @Test
    fun `combat2 short form sets the 0x04 flag and writes g2`() {
        val model = PlayerAppearance(slots = listOf(AppearanceSlot.Empty), combat2 = 138,
            kitColours = IntArray(0), kitStyles = IntArray(0), name = "")
        val w = Walk(PlayerAppearanceEncoder.encode(model))
        assertTrue(w.u8() and PlayerAppearanceEncoder.FLAG_COMBAT2_IS_SHORT != 0, "combat2 sets 0x04")
        w.u8() // gender
        w.u8() // slot 0 empty
        w.u16() // colourMask
        // no kit colours/styles (size 0)
        w.u16() // bas
        w.str() // name
        w.u8()  // combat level
        assertEquals(138, w.u16(), "combat2 g2")
    }

    private fun slotWire(slot: AppearanceSlot): Int {
        val slots = mutableListOf<AppearanceSlot>(AppearanceSlot.Empty, slot)
        val p = PlayerAppearanceEncoder.encode(PlayerAppearance(slots = slots, kitColours = IntArray(0), kitStyles = IntArray(0), name = ""))
        // flags(1)+gender(1)+slot0 empty(1) → slot1 short at index 3.
        return ((p[3].toInt() and 0xFF) shl 8) or (p[4].toInt() and 0xFF)
    }
}
