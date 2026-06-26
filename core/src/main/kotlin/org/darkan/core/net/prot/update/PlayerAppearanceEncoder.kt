package org.darkan.core.net.prot.update

import world.gregs.voidps.buffer.write.BufferWriter

/**
 * Builds the **plain** PLAYER_INFO APPEARANCE ext-info payload (the bytes that go inside
 * [UpdateMask.Appearance.data]) from a [PlayerAppearance] model, byte-precise per
 * `docs/protocol/player-appearance-948.md` §2 (`SetAppearanceAsPlayer @0x0014d5b0` field walk) and
 * §3 (`FUN_00141b40` body/equipment slot decoder).
 *
 * **This is the PLAIN payload only.** The ext-info framing transform — length byte (mode 3) and the
 * uniform body `+0x80` (mode 2) — is applied separately by the APPEARANCE encoder in
 * `Rev948ServerCodecsUpdateMasks` (`Rev948ExtInfoTransforms.write`). Keeping the two layers apart
 * matches the client: `QueueExtendedInfoPacket` un-transforms the body once, then `SetAppearanceAsPlayer`
 * reads plain g1/g2/gSmart fields out of it (§6 carve-out: the field reads are NOT individually
 * scrambled).
 *
 * **Field order (§2):** flags(g1) → title(gSmart1or2, if `flags & 0x40`) → extra-model list(if
 * `flags & 0x2`) → gender(g1 signed) → 12 body/equipment slots → model-override(§2 field 6) →
 * colours(§2 field 7) → trailing recolour-palette g2(§3.3).
 *
 * **Slot encoding (§3.1), base=0x100 / threshold=0x800 (hard-coded for the player avatar):**
 *  - empty  → `writeByte(0)` (1 byte)
 *  - kit k  → `writeShort(k + 0x100)`  (BE; value 0x100..0x7FF)
 *  - item i → `writeShort(i + 0x800)`  (BE; value ≥ 0x800)
 *  - morph (slot 0 only) → `writeShort(0xFFFF)` + `writeBigSmart(morphId)` + `writeByte(gender)`
 *
 * **⚠ Cache coupling (§4, FLAGGED for `cache-library-engineer`).** The slot COUNT and ORDER are
 * data-driven from the player body-part definition (`avatarDef+0x260`), NOT hard-fixed. This encoder
 * uses the documented classic 12-slot human base order ([HUMAN_BODY_PART_ORDER]); the exact 948 base
 * must be confirmed from the cache. This encoder is **ready-to-use but UNWIRED** — nothing emits its
 * output yet (the first pilot ships zero ext-info blocks).
 */
object PlayerAppearanceEncoder {

    // --- flags bits (§2 field 1) ---
    const val FLAG_VISIBLE = 0x01          // bit 0  — "is-other/visible"
    const val FLAG_HAS_EXTRA_MODELS = 0x02 // bit 1  — extra-model list present (field 3)
    const val FLAG_MODEL_OVERRIDE_NPC = 0x04 // bit 2 — field 6 reads g2 (NPC/morph form) not g1+g1
    const val FLAG_HAS_TITLE = 0x40        // bit 6  — title/prefix id present (field 2)
    const val FLAG_FEMALE_NAME_VARIANT = 0x80 // bit 7 — female name-render variant

    // --- slot value-space constants (§3.1; hard-coded in the client sub-object ctor) ---
    const val KIT_BASE = 0x100             // kitId    = value - 0x100  (value 0x100..0x7FF)
    const val ITEM_BASE = 0x800            // itemId   = value - 0x800  (value ≥ 0x800)
    const val MORPH_SENTINEL = 0xFFFF      // slot 0 only: NPC morph form follows

    /** scale 0..7 packs into flags bits 3..5: `(flags >> 3) & 7`, client adds +1 (§2 field-1 table). */
    fun scaleToFlagBits(scale: Int): Int = (scale and 0x7) shl 3

    /**
     * Classic 12-slot human body-part order (§3 / §4). **PROVISIONAL — cache-side authoritative.**
     * The client iterates `avatarDef+0x260` (`+8` = count, `+0x10` = per-slot type, `partType==1`
     * slots skipped). This is the well-known order but MUST be verified against the loaded 948 player
     * bodytype def. FLAGGED for `cache-library-engineer`.
     */
    val HUMAN_BODY_PART_ORDER: List<BodyPartSlot> = listOf(
        BodyPartSlot.HAT, BodyPartSlot.CAPE, BodyPartSlot.AMULET, BodyPartSlot.WEAPON,
        BodyPartSlot.TORSO, BodyPartSlot.SHIELD, BodyPartSlot.ARMS, BodyPartSlot.LEGS,
        BodyPartSlot.HAIR, BodyPartSlot.HANDS, BodyPartSlot.FEET, BodyPartSlot.JAW,
    )

    /**
     * Encode the plain APPEARANCE payload for [model] into a fresh byte array.
     *
     * [slotOrder] is the body-part-def slot order (defaults to [HUMAN_BODY_PART_ORDER]); pass the
     * cache-derived order once available. Each entry in [slotOrder] indexes [PlayerAppearance.slots].
     */
    fun encode(
        model: PlayerAppearance,
        slotOrder: List<BodyPartSlot> = HUMAN_BODY_PART_ORDER,
    ): ByteArray {
        val out = BufferWriter(64)
        encodeInto(out, model, slotOrder)
        return out.toArray()
    }

    /** Encode the plain APPEARANCE payload for [model] into an existing [out] writer. */
    fun encodeInto(
        out: BufferWriter,
        model: PlayerAppearance,
        slotOrder: List<BodyPartSlot> = HUMAN_BODY_PART_ORDER,
    ) {
        // Field 1 — flags (g1). Compose explicit flags + scale bits + gender-variant bit.
        var flags = model.flags or scaleToFlagBits(model.scale)
        if (model.title != null) flags = flags or FLAG_HAS_TITLE
        if (model.extraModels.isNotEmpty()) flags = flags or FLAG_HAS_EXTRA_MODELS
        out.writeByte(flags and 0xFF)

        // Field 2 — title / prefix id (gSmart1or2), gated flags & 0x40.
        if (flags and FLAG_HAS_TITLE != 0) {
            out.writeSmart(model.title ?: 0)
        }

        // Field 3 — extra-model list (g1 count, then count × (g2 value + g1 type)), gated flags & 0x2.
        if (flags and FLAG_HAS_EXTRA_MODELS != 0) {
            out.writeByte(model.extraModels.size and 0xFF)
            for (m in model.extraModels) {
                out.writeShort(m.value and 0xFFFF)
                out.writeByte(m.type and 0xFF)
            }
        }

        // Field 4 — gender / body-type (g1 signed, always).
        out.writeByte(model.gender and 0xFF)

        // Field 5 — body / equipment model slots (§3), in the body-part-def order, skipping
        // partType==1 slots (we model only the renderable slots, so we write each given slot).
        for ((i, part) in slotOrder.withIndex()) {
            val slot = model.slots.getOrNull(part.ordinal) ?: AppearanceSlot.Empty
            writeSlot(out, slot, isSlotZero = i == 0, gender = model.gender)
        }

        // Field 6 — base / model-override (§2 field 6, always present after the body models).
        out.writeByte(model.baseByte and 0xFF)
        if (flags and FLAG_MODEL_OVERRIDE_NPC != 0) {
            out.writeShort(model.modelOverrideId and 0xFFFF)   // NPC/morph form: g2 (0xFFFF = -1)
        } else {
            out.writeByte(model.baseX and 0xFF)                // common player form: two bytes
            out.writeByte(model.baseY and 0xFF)
        }

        // Field 7 — colours (§2 field 7): 1 flag byte; if non-zero, 4 × g2 + 1 × g1 (the 5th is a BYTE).
        if (model.coloursPresent) {
            out.writeByte(1)                                   // colourFlag != 0
            out.writeShort(model.colours[0] and 0xFFFF)        // c0 (BE u16)
            out.writeShort(model.colours[1] and 0xFFFF)        // c1
            out.writeShort(model.colours[2] and 0xFFFF)        // c2
            out.writeShort(model.colours[3] and 0xFFFF)        // c3
            out.writeByte(model.colours[4] and 0xFF)           // c4 — SINGLE byte (not a short!)
        } else {
            out.writeByte(0)                                   // colourFlag == 0 → all-default colours
        }

        // Trailing g2 from FUN_00141b40 (§3.3) — always read; for a default avatar the recolour /
        // aura palette id (typically the bodytype default, or 0).
        out.writeShort(model.recolourPaletteId and 0xFFFF)
    }

    /** Write one body/equipment slot per §3.1 (base=0x100, threshold=0x800). */
    private fun writeSlot(out: BufferWriter, slot: AppearanceSlot, isSlotZero: Boolean, gender: Int) {
        when (slot) {
            is AppearanceSlot.Empty -> out.writeByte(0)
            is AppearanceSlot.Kit -> out.writeShort((slot.kitId + KIT_BASE) and 0xFFFF)
            is AppearanceSlot.Item -> out.writeShort((slot.itemId + ITEM_BASE) and 0xFFFF)
            is AppearanceSlot.Morph -> {
                require(isSlotZero) { "APPEARANCE morph form is only valid in slot 0 (§3.1)" }
                out.writeShort(MORPH_SENTINEL)
                out.writeBigSmart(slot.morphId)               // gSmart2or4s
                out.writeByte((if (slot.genderOverride >= 0) slot.genderOverride else gender) and 0xFF)
            }
        }
    }
}

/**
 * Self-contained appearance model consumed by [PlayerAppearanceEncoder]. Lives in `core` (the encoder
 * is protocol code); the world-side `Appearance` maps onto this at the call site once emission is
 * wired. Defaults describe a plain default **male** Lumbridge avatar (§7): no title, no extras, scale
 * 0, all-empty equipment (caller fills kit slots), colours present.
 */
data class PlayerAppearance(
    /** Explicit flags bits the caller sets directly (visible/female-variant/etc.); scale + title +
     *  extra-models bits are derived by the encoder. */
    val flags: Int = 0,
    /** Avatar scale/size 0..7 (packed into flags bits 3..5; client adds +1). */
    val scale: Int = 0,
    /** Title / display-name-prefix index (gSmart1or2). `null` ⇒ no title (flags & 0x40 clear). */
    val title: Int? = null,
    /** Extra appended display models (auras/pets/cosmetics). Empty ⇒ flags & 0x2 clear. */
    val extraModels: List<ExtraModel> = emptyList(),
    /** Gender / body-type (g1 signed): 0 = male, 1 = female (§2 field 4). */
    val gender: Int = 0,
    /** Body/equipment slots, indexed by [BodyPartSlot.ordinal]; 12 entries for the human base. */
    val slots: List<AppearanceSlot> = List(BodyPartSlot.entries.size) { AppearanceSlot.Empty },
    /** Field-6 base byte (param_1+0x2c). 0 for a plain avatar. */
    val baseByte: Int = 0,
    /** Field-6 common-form X byte (param_1+0x30). 0 for a plain avatar. */
    val baseX: Int = 0,
    /** Field-6 common-form Y byte (param_1+0x34; 0xFF ⇒ -1). 0 for a plain avatar. */
    val baseY: Int = 0,
    /** Field-6 NPC/morph model id (only read when flags & 0x4). 0xFFFF ⇒ -1. */
    val modelOverrideId: Int = 0xFFFF,
    /** Whether the colour block is present (colourFlag != 0). */
    val coloursPresent: Boolean = true,
    /** 5 colour channels (hair, torso, legs, feet/boots, skin). First 4 are u16, the 5th is a byte. */
    val colours: IntArray = IntArray(5),
    /** Trailing recolour/aura palette id from FUN_00141b40 (§3.3). */
    val recolourPaletteId: Int = 0,
) {
    init {
        require(colours.size == 5) { "APPEARANCE requires exactly 5 colour channels (§2 field 7)" }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is PlayerAppearance &&
        flags == other.flags && scale == other.scale && title == other.title &&
        extraModels == other.extraModels && gender == other.gender && slots == other.slots &&
        baseByte == other.baseByte && baseX == other.baseX && baseY == other.baseY &&
        modelOverrideId == other.modelOverrideId && coloursPresent == other.coloursPresent &&
        colours.contentEquals(other.colours) && recolourPaletteId == other.recolourPaletteId)

    override fun hashCode(): Int {
        var result = flags
        result = 31 * result + scale
        result = 31 * result + (title ?: 0)
        result = 31 * result + extraModels.hashCode()
        result = 31 * result + gender
        result = 31 * result + slots.hashCode()
        result = 31 * result + colours.contentHashCode()
        result = 31 * result + recolourPaletteId
        return result
    }
}

/** One appended display model (§2 field 3): a model [value] + a [type] code. */
data class ExtraModel(val value: Int, val type: Int)

/**
 * The 12 classic human body-part slots in def order (§3/§4). `ordinal` indexes
 * [PlayerAppearance.slots]. **Slot order is cache-authoritative** — confirm against the 948 player
 * bodytype def (FLAGGED for `cache-library-engineer`).
 */
enum class BodyPartSlot {
    HAT, CAPE, AMULET, WEAPON, TORSO, SHIELD, ARMS, LEGS, HAIR, HANDS, FEET, JAW,
}

/** One body/equipment slot value (§3.1). */
sealed class AppearanceSlot {
    /** Empty slot — 1 wire byte `0x00`. */
    data object Empty : AppearanceSlot()

    /** Kit / body-part appearance — `writeShort(kitId + 0x100)`; valid kitId range 0..0x6FF. */
    data class Kit(val kitId: Int) : AppearanceSlot()

    /** Equipped item — `writeShort(itemId + 0x800)`. Must be a cache-defined item id (§4). */
    data class Item(val itemId: Int) : AppearanceSlot()

    /**
     * NPC morph form (slot 0 ONLY) — `writeShort(0xFFFF)` + `writeBigSmart(morphId)` + a gender byte.
     * [genderOverride] < 0 means "use the model gender".
     */
    data class Morph(val morphId: Int, val genderOverride: Int = -1) : AppearanceSlot()
}
