package org.darkan.core.net.prot.update

import org.darkan.core.net.prot.revision.rev948.Rev948ExtInfoTransforms
import org.darkan.core.net.prot.revision.rev948.Rev948PlayerUpdateMaskKey
import org.darkan.core.net.prot.revision.rev948.register948
import world.gregs.voidps.buffer.write.BufferWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Byte-precise tests for the 948 PLAYER_INFO APPEARANCE block, covering:
 *  - Task 1: the ext-info framing transforms (length = mode 3, body = mode 2) per
 *    `docs/protocol/player-appearance-948.md` §1.2 / §6 — the CORRECTION from the retired
 *    `writeByte(0)` mode-prefix model.
 *  - Task 2: the plain APPEARANCE payload field walk per §2/§3, including the 0/kit/item slot
 *    encoding (base 0x100 / threshold 0x800) and the §7 worked-example field order.
 */
class PlayerAppearanceEncoderTest {

    // ---------------------------------------------------------------------------------------------
    // Task 1 — ext-info framing transforms (§1.2)
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `appearance length byte is mode 3 transform (-128 - L)`() {
        // §1.2 worked value: L = 33 → wire = (-0x80 - 33) & 0xFF = 0x5F.
        assertEquals(0x5F, Rev948ExtInfoTransforms.appearanceLengthByte(33))
        // Boundary values.
        assertEquals(0x80, Rev948ExtInfoTransforms.appearanceLengthByte(0))   // (-128 - 0) & 0xFF = 0x80
        assertEquals(0x7F, Rev948ExtInfoTransforms.appearanceLengthByte(1))   // (-128 - 1) & 0xFF = 0x7F
        assertEquals(0x00, Rev948ExtInfoTransforms.appearanceLengthByte(128)) // (-128 - 128) & 0xFF = 0x00
        // The transform must be the client-recoverable inverse: client computes L = (-0x80 - wire) & 0xFF.
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
        // Client recovers each body byte as (wire - 0x80) & 0xFF.
        for (b in 0..255) {
            val wire = Rev948ExtInfoTransforms.appearanceBodyByte(b)
            assertEquals(b and 0xFF, (wire - 0x80) and 0xFF, "body byte round-trips for b=$b")
        }
    }

    @Test
    fun `framed appearance block is length(mode 3) + body(mode 2), no mode-prefix byte`() {
        val payload = byteArrayOf(0x00, 0x05, 0x10, 0x7F, 0xFF.toByte())   // arbitrary plain payload, L=5
        val out = BufferWriter(16)
        Rev948ExtInfoTransforms.write(out, payload)
        val wire = out.toArray()

        // Exactly 1 (length) + L (body) bytes — NO leading 0x00 mode byte (that was the retired model).
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
        register948()   // wires the PlayerUpdateMaskEncoder for Rev948PlayerUpdateMaskKey.APPEARANCE
        val payload = byteArrayOf(0x00, 0x00, 0x11, 0x22)   // L=4
        val out = BufferWriter(16)
        PlayerUpdateMaskEncoder.encode(out, Rev948PlayerUpdateMaskKey.APPEARANCE, UpdateMask.Appearance(payload))
        val wire = out.toArray()

        // Must NOT start with a 0x00 mode byte; first byte is the mode-3 length.
        assertEquals(Rev948ExtInfoTransforms.appearanceLengthByte(payload.size), wire[0].toInt() and 0xFF)
        assertEquals(1 + payload.size, wire.size)
        assertEquals(Rev948ExtInfoTransforms.appearanceBodyByte(0x11), wire[3].toInt() and 0xFF)
    }

    // ---------------------------------------------------------------------------------------------
    // Task 2 — plain APPEARANCE payload field walk (§2/§3)
    // ---------------------------------------------------------------------------------------------

    @Test
    fun `default male avatar payload matches the documented field order`() {
        // §7 default male Lumbridge avatar: flags=0, gender=0(male), 12 kit slots, colours present.
        val kits = (0 until 12).map { AppearanceSlot.Kit(it) }
        val model = PlayerAppearance(
            gender = 0,
            slots = kits,
            colours = intArrayOf(10, 20, 30, 40, 5),   // c0..c3 u16, c4 byte
            coloursPresent = true,
            recolourPaletteId = 0,
        )
        val payload = PlayerAppearanceEncoder.encode(model)

        // Decode field-by-field with a plain reader (this is the un-transformed payload).
        var i = 0
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "flags = 0 (no title/extras, scale 0, male)")
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "gender = 0 (male)")
        // 12 kit slots, each writeShort(kit + 0x100) BE.
        for (k in 0 until 12) {
            val hi = payload[i++].toInt() and 0xFF
            val lo = payload[i++].toInt() and 0xFF
            assertEquals(k + PlayerAppearanceEncoder.KIT_BASE, (hi shl 8) or lo, "slot $k = kit ${k} as (kit+0x100) BE")
        }
        // Field 6 — common form: baseByte + baseX + baseY (all 0).
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseByte")
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseX")
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseY")
        // Field 7 — colours: flag(1) + 4×g2 + 1×byte.
        assertEquals(0x01, payload[i++].toInt() and 0xFF, "colourFlag = 1")
        assertEquals(10, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "c0 u16")
        assertEquals(20, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "c1 u16")
        assertEquals(30, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "c2 u16")
        assertEquals(40, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "c3 u16")
        assertEquals(5, payload[i++].toInt() and 0xFF, "c4 is a SINGLE byte (not a short)")
        // Trailing recolour-palette g2.
        assertEquals(0, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "trailing recolour g2")
        assertEquals(payload.size, i, "cursor must equal payload length L (no trailing bytes)")
    }

    @Test
    fun `empty slot is a single zero byte and item slot adds 0x800`() {
        // One empty slot, one item slot, the rest empty — verify the 0/item encoding and slot count.
        val slots = MutableList<AppearanceSlot>(12) { AppearanceSlot.Empty }
        slots[4] = AppearanceSlot.Item(1205)   // §3.2 example item
        val model = PlayerAppearance(slots = slots, coloursPresent = false)
        val payload = PlayerAppearanceEncoder.encode(model)

        // flags(1) + gender(1) then 12 slots. Slots 0..3,5..11 = one 0x00 byte each; slot 4 = 2-byte item.
        var i = 2   // skip flags + gender
        for (s in 0 until 12) {
            if (s == 4) {
                val hi = payload[i++].toInt() and 0xFF
                val lo = payload[i++].toInt() and 0xFF
                assertEquals(1205 + PlayerAppearanceEncoder.ITEM_BASE, (hi shl 8) or lo, "item slot = itemId + 0x800 BE")
            } else {
                assertEquals(0x00, payload[i++].toInt() and 0xFF, "empty slot $s = single 0x00 byte")
            }
        }
        // Field 6 — common form model-override: baseByte + baseX + baseY (all 0 for a plain avatar).
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseByte")
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseX")
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "baseY")
        // colourFlag = 0 (no colours present).
        assertEquals(0x00, payload[i++].toInt() and 0xFF, "colourFlag = 0")
        // trailing recolour g2.
        i += 2
        assertEquals(payload.size, i)
    }

    @Test
    fun `kit values stay below 0x800 and items at-or-above 0x800 (the split boundary)`() {
        // §3.1: kit 0..0x6FF maps to 0x100..0x7FF (< 0x800); item maps to >= 0x800.
        val kitWire = slotWire(AppearanceSlot.Kit(0x6FF))
        assertTrue(kitWire < PlayerAppearanceEncoder.ITEM_BASE, "max kit stays below the 0x800 item threshold")
        assertEquals(0x7FF, kitWire)
        val itemWire = slotWire(AppearanceSlot.Item(0))
        assertEquals(0x800, itemWire, "item 0 encodes at exactly the 0x800 threshold")
        assertTrue(itemWire >= PlayerAppearanceEncoder.ITEM_BASE)
    }

    @Test
    fun `morph slot 0 writes 0xFFFF + bigSmart morph + gender byte`() {
        val slots = MutableList<AppearanceSlot>(12) { AppearanceSlot.Empty }
        slots[0] = AppearanceSlot.Morph(morphId = 50, genderOverride = 1)
        val model = PlayerAppearance(gender = 0, slots = slots, coloursPresent = false)
        val payload = PlayerAppearanceEncoder.encode(model)

        var i = 2   // flags + gender
        // slot 0 morph: 0xFFFF sentinel.
        assertEquals(0xFF, payload[i++].toInt() and 0xFF)
        assertEquals(0xFF, payload[i++].toInt() and 0xFF)
        // bigSmart(50) → since 50 in 0..32766 it is a 2-byte BE short (writeBigSmart).
        assertEquals(50, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "bigSmart morphId")
        // gender override byte = 1.
        assertEquals(1, payload[i++].toInt() and 0xFF, "morph gender override byte")
    }

    @Test
    fun `title and extra-models gate their fields via flags`() {
        val model = PlayerAppearance(
            title = 7,                                  // sets flags & 0x40
            extraModels = listOf(ExtraModel(value = 0x1234, type = 2)),   // sets flags & 0x02
            slots = List(12) { AppearanceSlot.Empty },
            coloursPresent = false,
        )
        val payload = PlayerAppearanceEncoder.encode(model)
        var i = 0
        val flags = payload[i++].toInt() and 0xFF
        assertTrue(flags and PlayerAppearanceEncoder.FLAG_HAS_TITLE != 0, "title sets flags bit 0x40")
        assertTrue(flags and PlayerAppearanceEncoder.FLAG_HAS_EXTRA_MODELS != 0, "extra-models sets flags bit 0x02")
        // Field 2 — title via gSmart1or2: 7 < 128 → single byte 0x07.
        assertEquals(7, payload[i++].toInt() and 0xFF, "title gSmart1or2 (< 128 → 1 byte)")
        // Field 3 — extra-model list: count byte then (g2 value + g1 type).
        assertEquals(1, payload[i++].toInt() and 0xFF, "extra-model count")
        assertEquals(0x1234, ((payload[i++].toInt() and 0xFF) shl 8) or (payload[i++].toInt() and 0xFF), "extra-model value g2")
        assertEquals(2, payload[i++].toInt() and 0xFF, "extra-model type g1")
    }

    private fun slotWire(slot: AppearanceSlot): Int {
        val slots = MutableList<AppearanceSlot>(12) { AppearanceSlot.Empty }
        slots[1] = slot   // a non-zero slot so a morph sentinel is never triggered
        val payload = PlayerAppearanceEncoder.encode(PlayerAppearance(slots = slots, coloursPresent = false))
        // flags(1)+gender(1)+slot0 empty(1) → slot1 starts at index 3.
        val hi = payload[3].toInt() and 0xFF
        val lo = payload[4].toInt() and 0xFF
        return (hi shl 8) or lo
    }
}
