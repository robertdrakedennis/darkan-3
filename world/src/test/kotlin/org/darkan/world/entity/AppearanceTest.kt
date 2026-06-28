package org.darkan.world.entity

import org.darkan.core.model.Account
import org.darkan.core.net.prot.update.PlayerAppearanceEncoder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Validates that the world-side [Appearance] builder produces a byte-correct APPEARANCE payload that
 * matches the binary-verified spec (`re-resources/docs/net/serverprot/player-appearance-948.md`) and
 * the structure of the captured production `op22 PlayerInfo` appearance block for a default character:
 *  - 16 emitted slots in the human Body-def order (disabled {12,13,17}),
 *  - default identitykits in the 7 body-style slots (gender-specific),
 *  - equipped items override their body slot,
 *  - tail = colourMask + 10 kitColours + 10 kitStyles + bas(2699) + name + combat + headbar.
 *
 * This is the capture-comparison made deterministic: the OLD buggy builder emitted `Kit(slotIndex)`
 * for every slot (garbage); these assertions pin the corrected structure.
 */
class AppearanceTest {

    private val EMIT_SLOTS = (0..18).filter { it !in setOf(12, 13, 17) }

    /** Plain-payload reader mirroring the binary `DecodeAppearance` + `DecodeAppearanceEquipment`. */
    private class Walk(val p: ByteArray) {
        var i = 0
        fun u8() = p[i++].toInt() and 0xFF
        fun u16() = (u8() shl 8) or u8()
        fun str(): String { val sb = StringBuilder(); while (i < p.size && p[i].toInt() != 0) sb.append((p[i++].toInt() and 0xFF).toChar()); i++; return sb.toString() }
    }

    private fun account() = Account(username = "tester", displayName = "Tester")

    @Test
    fun `default male avatar emits 16 slots with default kits and the 2699 bas tail`() {
        val appr = Appearance(account())
        val w = Walk(appr.ensureCachedBytes())

        assertEquals(0x00, w.u8(), "flags = 0 (male, no title/cust)")
        assertEquals(0x00, w.u8(), "gender = 0 (male)")

        // 16 slots: body-style slots carry the male default kit; others are empty.
        val maleStyles = Appearance.DEFAULT_MALE_BODY_STYLES
        for (slotIndex in EMIT_SLOTS) {
            val styleIndex = Appearance.BODY_STYLE_BY_SLOT[slotIndex]
            if (styleIndex != null) {
                val expectedKit = maleStyles[styleIndex]
                assertEquals(expectedKit + PlayerAppearanceEncoder.KIT_BASE, w.u16(),
                    "slot $slotIndex carries male default kit $expectedKit")
            } else {
                assertEquals(0x00, w.u8(), "non-style slot $slotIndex is empty")
            }
        }

        assertEquals(0x0000, w.u16(), "colourMask = 0")
        for (c in Appearance.DEFAULT_COLOURS) assertEquals(c, w.u8(), "kitColour")
        for (s in 0 until Appearance.KIT_CHANNEL_COUNT) assertEquals(0, w.u8(), "kitStyle (default 0)")
        assertEquals(2699, w.u16(), "bas default = 2699")
        assertEquals("Tester", w.str(), "name")
        assertEquals(3, w.u8(), "combat level default = 3")
        w.u8(); w.u8() // skill level/bonus
        assertEquals(0, w.u8(), "hasHeadbar = 0")
        assertEquals(w.p.size, w.i, "no trailing bytes")
    }

    @Test
    fun `female avatar sets the female flag and uses female kits with no jaw kit`() {
        val appr = Appearance(account())
        appr.gender = 1
        val w = Walk(appr.ensureCachedBytes())

        assertTrue(w.u8() and PlayerAppearanceEncoder.FLAG_FEMALE != 0, "female flag 0x80 set")
        assertEquals(1, w.u8(), "gender byte = 1")

        val femaleStyles = Appearance.DEFAULT_FEMALE_BODY_STYLES
        for (slotIndex in EMIT_SLOTS) {
            val styleIndex = Appearance.BODY_STYLE_BY_SLOT[slotIndex]
            if (styleIndex != null && femaleStyles[styleIndex] >= 0) {
                assertEquals(femaleStyles[styleIndex] + PlayerAppearanceEncoder.KIT_BASE, w.u16(),
                    "slot $slotIndex female kit ${femaleStyles[styleIndex]}")
            } else {
                // jaw (style index 1 = -1) and non-style slots are empty.
                assertEquals(0x00, w.u8(), "slot $slotIndex empty (no female kit)")
            }
        }
    }

    @Test
    fun `equipped weapon overrides its body slot with an item token`() {
        val appr = Appearance(account())
        appr.equipment[Player.EQUIP_SLOT_WEAPON] = 1205 // bronze_dagger
        val w = Walk(appr.ensureCachedBytes())
        w.u8(); w.u8() // flags, gender

        for (slotIndex in EMIT_SLOTS) {
            when {
                slotIndex == Player.EQUIP_SLOT_WEAPON ->
                    assertEquals(1205 + PlayerAppearanceEncoder.ITEM_BASE, w.u16(), "weapon slot = bronze dagger item")
                Appearance.BODY_STYLE_BY_SLOT.containsKey(slotIndex) ->
                    w.u16() // a default kit short
                else ->
                    assertEquals(0x00, w.u8(), "empty slot $slotIndex")
            }
        }
        // bronze dagger (1205) is in body slot 3 (weapon), which has no default style — so it replaces
        // an otherwise-empty slot, confirming items occupy their wear slot.
        assertTrue(true)
    }

    @Test
    fun `mutating a field invalidates the cache and re-renders`() {
        val appr = Appearance(account())
        val first = appr.ensureCachedBytes()
        appr.bas = 1234
        val second = appr.ensureCachedBytes()
        assertTrue(!first.contentEquals(second), "changing bas re-renders the payload")
    }

    /**
     * The over-read regression: walk the FULL appearance block exactly as the binary
     * `DecodeAppearance @0x100031480` + `DecodeAppearanceEquipment @0x100032450` do, and assert the
     * client cursor consumes EXACTLY the emitted length (no over-read past the buffer, no under-read
     * leftover). The original crash was suspected here; the cache-engineer's binary trace proved the
     * bytes are exact — this test locks that invariant so a future field change can't silently
     * reintroduce a count/length mismatch that walks the client off the end.
     *
     * Covers the kit-only fresh-spawn avatar AND the equipped-item + female variants, since all must
     * close the cursor exactly.
     */
    @Test
    fun `full block consumes exactly the emitted length for every variant (no over read)`() {
        fun fullWalkConsumesExactly(appr: Appearance, label: String) {
            val w = Walk(appr.ensureCachedBytes())
            val flags = w.u8()
            // title gate (0x40) — gSmart1or2.
            if (flags and 0x40 != 0) { if ((w.p[w.i].toInt() and 0xFF) < 0x80) w.u8() else w.u16() }
            // customisation gate (0x02) — count + N×{g2,g1}.
            if (flags and 0x02 != 0) { val n = w.u8(); repeat(n) { w.u16(); w.u8() } }
            w.u8() // gender / bodyType
            var morphed = false
            for (slotIndex in EMIT_SLOTS) {
                val b = w.p[w.i].toInt() and 0xFF
                if (b == 0) { w.u8() } else {
                    val tok = w.u16()
                    if (slotIndex == 0 && tok == 0xFFFF) morphed = true
                }
            }
            if (!morphed) w.u16()             // colourMask g2
            repeat(Appearance.KIT_CHANNEL_COUNT) { w.u8() } // kitColours 10
            repeat(Appearance.KIT_CHANNEL_COUNT) { w.u8() } // kitStyles 10
            w.u16()                            // bas
            w.str()                            // name
            w.u8()                             // combat
            if (flags and 0x04 != 0) w.u16() else { w.u8(); w.u8() } // combat2 OR skill pair
            val hb = w.u8()                    // hasHeadbar
            if (hb != 0) { repeat(4) { w.u16() }; w.u8() }
            assertEquals(w.p.size, w.i, "[$label] client cursor must consume exactly the emitted length")
        }

        fullWalkConsumesExactly(Appearance(account()), "default-male-spawn (kit-only)")
        fullWalkConsumesExactly(Appearance(account()).apply { gender = 1 }, "female")
        fullWalkConsumesExactly(
            Appearance(account()).apply { equipment[Player.EQUIP_SLOT_WEAPON] = 1205 },
            "equipped-weapon",
        )
        fullWalkConsumesExactly(
            Appearance(account()).apply { title = 5; combatLevel = 138 },
            "with-title",
        )
    }
}
