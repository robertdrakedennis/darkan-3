package world.gregs.voidps.cache.definition.data

import world.gregs.voidps.cache.Definition

/**
 * Body / wear-pos ("WearposDefaults") definition — DEFAULTS index (28), archive 6, file 0 per id.
 *
 * The RS3 NXT 948 client decodes this struct (`jag::game::WearposDefaults`) and the player-appearance
 * decoder (`jag::game::PlayerAvatar::DecodeAppearanceEquipment @0x100032450`) reads exactly two things
 * out of it:
 *  - the **slot count** (`WearposDefaults+0x08`) — for the human body this is `disabledSlots.size` (=19);
 *    it is the element count of the opcode-1 array, not a separately stored field, so [slotCount] derives
 *    it here.
 *  - the **disabled-slot mask** (`WearposDefaults+0x10`) — [disabledSlots]; entry `== 1` ⇒ that body slot
 *    emits nothing in the appearance block. For body 0 that disables slots {12, 13, 17}.
 *
 * **Appearance "K" is NOT on this definition.** Binary-verified (Mac 948-5): the kit-colours (field 7)
 * and kit-styles (field 8) loops in `DecodeAppearanceEquipment` are bounded by a **hard-coded 10**
 * (`MOV qword [RBX+0x60], 0xA` @0x1000329ed and `MOV qword [RBX+0x78], 0xA` @0x100032ab8), and the
 * cursor advances on every iteration — so the client always reads exactly 10 kit-colour + 10 kit-style
 * bytes regardless of any def. The `+0x240` object the loops dereference is a *separate* config def
 * (`ConfigProvider+0x240`, the per-channel value-clamp table), **not** `WearposDefaults+0x240`; it only
 * clamps out-of-range byte values to 0, it never sets a count. Do not derive an appearance count from
 * [anInt4506]/[anInt4504]/[anIntArray4501]/[anIntArray4507] — none of them is K.
 *
 * Opcode → field (this cache exercises only 1,3,4,5,6; coverage verified by clean decode of all 3074
 * bodies — an unhandled opcode would desync the no-`else` read loop and corrupt subsequent defs):
 *  - 1 → [disabledSlots] = g1-len array of g1 (also implies the +0x08 slot count)
 *  - 3 → [anInt4506] (g1; body 0 = 5)
 *  - 4 → [anInt4504] (g1; body 0 = 3)
 *  - 5 → [anIntArray4501] = g1-len array of g1 (body 0 = empty)
 *  - 6 → [anIntArray4507] = g1-len array of g1 (body 0 = empty)
 */
data class BodyDefinition(
    override var id: Int = -1,
    var disabledSlots: IntArray = IntArray(0),
    var anInt4506: Int = -1,
    var anInt4504: Int = -1,
    var anIntArray4501: IntArray? = null,
    var anIntArray4507: IntArray? = null
) : Definition {

    /**
     * Wear-pos slot count (`WearposDefaults+0x08`). The client reads this as the element count of the
     * disabled-slot array, so it is exactly `disabledSlots.size` (19 for the human body).
     */
    val slotCount: Int
        get() = disabledSlots.size

    /** Body-slot indices that are NOT disabled, in def order (the slots the appearance block emits). */
    val enabledSlotIndices: List<Int>
        get() = disabledSlots.indices.filter { disabledSlots[it] == 0 }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BodyDefinition

        if (id != other.id) return false
        if (!disabledSlots.contentEquals(other.disabledSlots)) return false
        if (anInt4506 != other.anInt4506) return false
        if (anInt4504 != other.anInt4504) return false
        if (anIntArray4501 != null) {
            if (other.anIntArray4501 == null) return false
            if (!anIntArray4501.contentEquals(other.anIntArray4501)) return false
        } else if (other.anIntArray4501 != null) return false
        if (anIntArray4507 != null) {
            if (other.anIntArray4507 == null) return false
            if (!anIntArray4507.contentEquals(other.anIntArray4507)) return false
        } else if (other.anIntArray4507 != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + disabledSlots.contentHashCode()
        result = 31 * result + anInt4506
        result = 31 * result + anInt4504
        result = 31 * result + (anIntArray4501?.contentHashCode() ?: 0)
        result = 31 * result + (anIntArray4507?.contentHashCode() ?: 0)
        return result
    }
}