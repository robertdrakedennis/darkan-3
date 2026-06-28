package org.darkan.core.net.prot.update

import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Builds the **plain** PLAYER_INFO APPEARANCE ext-info payload (the bytes that go inside
 * [UpdateMask.Appearance.data]) from a [PlayerAppearance] model, byte-precise per
 * `re-resources/docs/net/serverprot/player-appearance-948.md`.
 *
 * **This is the PLAIN payload only.** The ext-info framing transform — length byte (mode 3) and the
 * uniform body `+0x80` (mode 2) — is applied separately by the APPEARANCE mask encoder in
 * `Rev948ServerCodecsUpdateMasks` (`Rev948ExtInfoTransforms.write`). The client un-transforms the body
 * once, then `PlayerAvatar::DecodeAppearance @0x100031480` (+ `DecodeAppearanceEquipment @0x100032450`)
 * reads plain g1/g2/gSmart/gjstr fields out of it.
 *
 * **Field order (binary-verified, 948-5 Mac):**
 *  1. flags (g1) — `0x02`=customisation present, `0x04`=combat2 g2, `0x40`=title present, `0x80`=female
 *  2. title (gSmart1or2) — if `flags & 0x40`
 *  3. customisation: count(g1) + count × {g2 id, g1 type} — if `flags & 0x02`
 *  4. bodyType / gender (g1 signed)
 *  5. body/equipment slot tokens (data-driven count/order; disabled slots emit nothing)
 *  6. colour-channel mask (g2) — only if NOT morphed (slot-0 token ≠ 0xffff)
 *  7. kit-colours (g1 × 10) — hardcoded client bound, not body-def-sized
 *  8. kit-styles (g1 × 10) — hardcoded client bound
 *  9. bas / render-anim id (g2) — default 2699
 *  10. name (gjstr CP1252, null-terminated)
 *  11. combat level (g1)
 *  12. combat2/skill — g2 if `flags & 0x04`, else 2×g1 (skill level + skill bonus)
 *  13. hasHeadbar (g1) + (if != 0) 4×g2 + g1
 *
 * **Slot encoding (base=0x100 / threshold=0x800, hard-coded for the player avatar):**
 *  - empty  → `writeByte(0)` (1 byte)
 *  - kit k  → `writeShort(k + 0x100)`  (BE; value 0x100..0x7FF)
 *  - item i → `writeShort(i + 0x800)`  (BE; value ≥ 0x800)
 *  - morph (slot 0 only) → `writeShort(0xFFFF)` + `writeBigSmart(morphId)` + `writeByte(gender)`
 *
 * The slot COUNT/ORDER is data-driven from the human Body/Wearpos def (cache DEFAULTS index, archive 6,
 * file 0; opcode 1 = disabledSlots). The caller resolves `slots` into the **emitted-slot order**
 * (disabled slots already removed). For the standard human body that is 16 slots (indices 0–11, 14, 15,
 * 16, 18 — disabled {12, 13, 17}).
 */
object PlayerAppearanceEncoder {

    // --- flags bits (field 1) ---
    const val FLAG_HAS_CUSTOMISATION = 0x02 // bit 1 — customisation/extra-model list present (field 3)
    const val FLAG_COMBAT2_IS_SHORT = 0x04  // bit 2 — combat2 read as g2 (field 12) instead of 2×g1
    const val FLAG_HAS_TITLE = 0x40         // bit 6 — title/prefix id present (field 2)
    const val FLAG_FEMALE = 0x80            // bit 7 — female render flag

    // --- slot value-space constants (hard-coded in DecodeAppearanceEquipment) ---
    const val KIT_BASE = 0x100             // kitId  = value - 0x100  (value 0x100..0x7FF)
    const val ITEM_BASE = 0x800            // itemId = value - 0x800  (value ≥ 0x800)
    const val MORPH_SENTINEL = 0xFFFF      // slot 0 only: NPC morph form follows

    /** scale 0..7 packs into flags bits 3..5; client adds +1. */
    fun scaleToFlagBits(scale: Int): Int = (scale and 0x7) shl 3

    /**
     * Encode the plain APPEARANCE payload for [model] into a fresh byte array.
     */
    fun encode(model: PlayerAppearance): ByteArray {
        val out = BufferWriter(64)
        encodeInto(out, model)
        return out.toArray()
    }

    /** Encode the plain APPEARANCE payload for [model] into an existing [out] writer. */
    fun encodeInto(out: BufferWriter, model: PlayerAppearance) {
        // Field 1 — flags (g1).
        var flags = model.flags or scaleToFlagBits(model.scale)
        if (model.title != null) flags = flags or FLAG_HAS_TITLE
        if (model.customisation.isNotEmpty()) flags = flags or FLAG_HAS_CUSTOMISATION
        if (model.gender == 1) flags = flags or FLAG_FEMALE
        if (model.combat2 != null) flags = flags or FLAG_COMBAT2_IS_SHORT
        out.writeByte(flags and 0xFF)

        // Field 2 — title / prefix id (gSmart1or2), gated flags & 0x40.
        if (flags and FLAG_HAS_TITLE != 0) {
            out.writeSmart(model.title ?: 0)
        }

        // Field 3 — customisation list (g1 count, then count × (g2 id + g1 type)), gated flags & 0x02.
        if (flags and FLAG_HAS_CUSTOMISATION != 0) {
            out.writeByte(model.customisation.size and 0xFF)
            for (c in model.customisation) {
                out.writeShort(c.id and 0xFFFF)
                out.writeByte(c.type and 0xFF)
            }
        }

        // Field 4 — bodyType / gender (g1 signed, always).
        out.writeByte(model.gender and 0xFF)

        // Field 5 — body / equipment slot tokens (in emitted-slot order; disabled slots already removed).
        var morphed = false
        for ((i, slot) in model.slots.withIndex()) {
            if (slot is AppearanceSlot.Morph) morphed = true
            writeSlot(out, slot, isSlotZero = i == 0, gender = model.gender)
            if (slot is AppearanceSlot.Morph) break // morph stops the slot loop (slot 0 only)
        }

        // Field 6 — colour-channel mask (g2). Only present when NOT morphed.
        if (!morphed) {
            out.writeShort(model.colourMask and 0xFFFF)
        }

        // Field 7 — kit-colours. Client reads a HARDCODED 10 g1 bytes (bound 0xA @0x1000329ed).
        for (c in model.kitColours) out.writeByte(c and 0xFF)

        // Field 8 — kit-styles. Client reads a HARDCODED 10 g1 bytes (bound 0xA @0x100032ab8).
        for (s in model.kitStyles) out.writeByte(s and 0xFF)

        // Field 9 — bas / render-anim id (g2).
        out.writeShort(model.bas and 0xFFFF)

        // Field 10 — name (gjstr CP1252, null-terminated).
        out.writeString(model.name)

        // Field 11 — combat level (g1).
        out.writeByte(model.combatLevel and 0xFF)

        // Field 12 — combat2/skill.
        if (model.combat2 != null) {
            out.writeShort(model.combat2 and 0xFFFF)       // g2 (0xffff ⇒ -1)
        } else {
            out.writeByte(model.skillLevel and 0xFF)       // skill level
            out.writeByte(model.skillBonus and 0xFF)       // skill bonus (0xff ⇒ -1)
        }

        // Field 13 — headbar.
        if (model.headbar != null) {
            out.writeByte(1)
            out.writeShort(model.headbar.a and 0xFFFF)
            out.writeShort(model.headbar.b and 0xFFFF)
            out.writeShort(model.headbar.c and 0xFFFF)
            out.writeShort(model.headbar.d and 0xFFFF)
            out.writeByte(model.headbar.e and 0xFF)
        } else {
            out.writeByte(0)
        }
    }

    /** Write one body/equipment slot per the slot loop (base=0x100, threshold=0x800). */
    private fun writeSlot(out: BufferWriter, slot: AppearanceSlot, isSlotZero: Boolean, gender: Int) {
        when (slot) {
            is AppearanceSlot.Empty -> out.writeByte(0)
            is AppearanceSlot.Kit -> out.writeShort((slot.kitId + KIT_BASE) and 0xFFFF)
            is AppearanceSlot.Item -> out.writeShort((slot.itemId + ITEM_BASE) and 0xFFFF)
            is AppearanceSlot.Morph -> {
                require(isSlotZero) { "APPEARANCE morph form is only valid in slot 0" }
                out.writeShort(MORPH_SENTINEL)
                out.writeBigSmart(slot.morphId)                // gSmart2or4s
                out.writeByte((if (slot.genderOverride >= 0) slot.genderOverride else gender) and 0xFF)
            }
        }
    }
}

/**
 * Self-contained appearance model consumed by [PlayerAppearanceEncoder]. Lives in `core` (the encoder
 * is protocol code); the world-side `Appearance` maps onto this at the call site.
 *
 * [slots] is the body/equipment slot list in the body-def **emitted-slot order** (disabled slots
 * already removed). [kitColours]/[kitStyles] must each be exactly 10 (the client's hardcoded bound).
 */
data class PlayerAppearance(
    /** Explicit flags bits the caller sets directly; scale + title + customisation + female + combat2
     *  bits are derived by the encoder. */
    val flags: Int = 0,
    /** Avatar scale/size 0..7 (packed into flags bits 3..5; client adds +1). */
    val scale: Int = 0,
    /** Title / display-name-prefix index (gSmart1or2). `null` ⇒ no title (flags & 0x40 clear). */
    val title: Int? = null,
    /** Customisation / extra appended models. Empty ⇒ flags & 0x02 clear. */
    val customisation: List<Customisation> = emptyList(),
    /** Gender / body-type (g1 signed): 0 = male, 1 = female. Drives the female flag bit too. */
    val gender: Int = 0,
    /** Body/equipment slots in body-def emitted-slot order (disabled slots already removed). */
    val slots: List<AppearanceSlot> = emptyList(),
    /** Field-6 colour-channel mask (g2). 0 ⇒ no per-slot recolours. Not written when morphed. */
    val colourMask: Int = 0,
    /** Field-7 kit-colours. The client reads a HARDCODED 10 g1 bytes (not body-def-sized); pass 10. */
    val kitColours: IntArray = IntArray(0),
    /** Field-8 kit-styles. The client reads a HARDCODED 10 g1 bytes (not body-def-sized); pass 10. */
    val kitStyles: IntArray = IntArray(0),
    /** Field-9 render-anim ("bas") id (g2). Default 2699. */
    val bas: Int = DEFAULT_BAS,
    /** Field-10 display name (gjstr CP1252). */
    val name: String = "",
    /** Field-11 combat level (g1). */
    val combatLevel: Int = 3,
    /** Field-12 combat2 (g2, 0xffff ⇒ -1) when set; `null` ⇒ skill-pair form (2×g1). */
    val combat2: Int? = null,
    /** Field-12 skill level (g1), used when [combat2] is null. */
    val skillLevel: Int = 0,
    /** Field-12 skill bonus (g1, 0xff ⇒ -1), used when [combat2] is null. */
    val skillBonus: Int = 0,
    /** Field-13 headbar block; `null` ⇒ no headbar (hasHeadbar byte 0). */
    val headbar: AppearanceHeadbar? = null,
) {
    override fun equals(other: Any?): Boolean = this === other || (other is PlayerAppearance &&
        flags == other.flags && scale == other.scale && title == other.title &&
        customisation == other.customisation && gender == other.gender && slots == other.slots &&
        colourMask == other.colourMask && kitColours.contentEquals(other.kitColours) &&
        kitStyles.contentEquals(other.kitStyles) && bas == other.bas && name == other.name &&
        combatLevel == other.combatLevel && combat2 == other.combat2 &&
        skillLevel == other.skillLevel && skillBonus == other.skillBonus && headbar == other.headbar)

    override fun hashCode(): Int {
        var result = flags
        result = 31 * result + scale
        result = 31 * result + (title ?: 0)
        result = 31 * result + customisation.hashCode()
        result = 31 * result + gender
        result = 31 * result + slots.hashCode()
        result = 31 * result + colourMask
        result = 31 * result + kitColours.contentHashCode()
        result = 31 * result + kitStyles.contentHashCode()
        result = 31 * result + bas
        result = 31 * result + name.hashCode()
        result = 31 * result + combatLevel
        result = 31 * result + (combat2 ?: -1)
        result = 31 * result + headbar.hashCode()
        return result
    }

    companion object {
        /** Default render-anim ("bas") id for a fresh character (alerion DEFAULT_BAS, capture-confirmed). */
        const val DEFAULT_BAS = 2699
    }
}

/** One customisation / extra appended model (field 3): a model [id] + a [type] code. */
data class Customisation(val id: Int, val type: Int)

/** Field-13 appearance headbar block: 4×g2 + 1×g1 (distinct from the HITMARKS [Headbar]). */
data class AppearanceHeadbar(val a: Int, val b: Int, val c: Int, val d: Int, val e: Int)

/** One body/equipment slot value. */
sealed class AppearanceSlot {
    /** Empty slot — 1 wire byte `0x00`. */
    data object Empty : AppearanceSlot()

    /** Kit / body-part appearance — `writeShort(kitId + 0x100)`; valid kitId range 0..0x6FF. */
    data class Kit(val kitId: Int) : AppearanceSlot()

    /** Equipped item — `writeShort(itemId + 0x800)`. Must be a cache-defined item id. */
    data class Item(val itemId: Int) : AppearanceSlot()

    /**
     * NPC morph form (slot 0 ONLY) — `writeShort(0xFFFF)` + `writeBigSmart(morphId)` + a gender byte.
     * [genderOverride] < 0 means "use the model gender". Stops the slot loop.
     */
    data class Morph(val morphId: Int, val genderOverride: Int = -1) : AppearanceSlot()
}
