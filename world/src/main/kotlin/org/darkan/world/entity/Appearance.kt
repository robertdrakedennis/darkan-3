package org.darkan.world.entity

import org.darkan.core.model.Account
import org.darkan.core.net.prot.update.AppearanceSlot
import org.darkan.core.net.prot.update.PlayerAppearance
import org.darkan.core.net.prot.update.PlayerAppearanceEncoder

/**
 * Player appearance state. Derives the PLAYER_INFO APPEARANCE block (mask bit 3 / 0x08) per the
 * binary-verified spec `re-resources/docs/net/serverprot/player-appearance-948.md`:
 * `flags → [title] → [customisation] → gender → slots → colourMask → kitColours → kitStyles → bas
 *  → name → combat → [combat2/skill] → headbar`.
 *
 * The avatar is built from THREE inputs, exactly like the alerion 948 reference
 * (`Player.buildAppearanceLook`):
 *  1. **gender** — selects the gender-specific default identitykit ("body style") ids.
 *  2. **default identitykits** per body part — fill the renderable body slots (hair, torso, arms,
 *     legs, hands, feet, jaw) with [DEFAULT_MALE_BODY_STYLES] / [DEFAULT_FEMALE_BODY_STYLES].
 *  3. **worn equipment** — an item in a slot OVERRIDES that slot's default kit with `Item(id)`.
 *
 * Slots are emitted in the human Body/Wearpos-def order, skipping the disabled slots
 * ([DISABLED_SLOTS] = {12,13,17}) → 16 emitted tokens. The client reads this order/count from the
 * Body def at runtime; [BODY_SLOT_ORDER] is the verified human-body default (matches alerion's
 * `DEFAULT_DISABLED_SLOTS` and the cache `BodyDefinition` at DEFAULTS/archive-6/file-0). When a
 * cache-driven body-def lookup is wired (see the FLAG to cache-library-engineer below), the order
 * should come from `BodyDefinition.disabledSlots`.
 *
 * The encoded byte array is memoised in [cachedBytes]; any field mutation MUST set `cachedBytes = null`
 * (use [invalidate]) so the next encode re-renders.
 */
class Appearance(private val account: Account) {

    /** 0 = male, 1 = female. Selects the default identitykit set + sets the female render flag. */
    var gender: Int = 0
        set(value) { field = value; invalidate() }

    /**
     * Worn equipment indexed by **Body/Wearpos-def slot index** (NOT a 15-slot equipment array):
     * HAT=0, CAPE=1, AMULET=2, WEAPON=3, CHEST=4, SHIELD=5, LEGS=7, HANDS=9, FEET=10, RING=12,
     * ARROWS=13, AURA=14, POCKET=17, WINGS=18. `-1` = nothing equipped (slot uses its default kit).
     * Sized to cover all 19 body-def slot indices.
     */
    val equipment: IntArray = IntArray(19) { -1 }

    /** K kit-colour channels (field 7). alerion default = [DEFAULT_COLOURS]. */
    val colours: IntArray = DEFAULT_COLOURS.copyOf()

    /** K kit-style channels (field 8). alerion default = all-zero. */
    val styles: IntArray = IntArray(KIT_CHANNEL_COUNT)

    /** Render-anim ("bas") id (field 9). Default 2699 for a fresh character. */
    var bas: Int = PlayerAppearance.DEFAULT_BAS
        set(value) { field = value; invalidate() }

    /** Display name (field 10). */
    var displayName: String = account.displayName
        set(value) { field = value; invalidate() }

    /** Combat level (field 11). */
    var combatLevel: Int = 3
        set(value) { field = value; invalidate() }

    /** Skill total (field 12, skill-pair form). */
    var skillLevel: Int = 0
        set(value) { field = value; invalidate() }

    /** Skill bonus (field 12, skill-pair form; 0xff ⇒ -1). */
    var skillBonus: Int = 0
        set(value) { field = value; invalidate() }

    var skullIcon: Int = -1
    var prayerIcon: Int = -1
    var headIcon: Int = -1
    var title: Int? = null

    /** Cached encoded appearance block; null forces a rebuild on the next [ensureCachedBytes]. */
    var cachedBytes: ByteArray? = null
        private set

    fun invalidate() { cachedBytes = null }

    fun ensureCachedBytes(): ByteArray = cachedBytes ?: rebuildCachedBytes()

    /**
     * Resolve the appearance into a byte-correct [PlayerAppearance] and encode it. Each emitted body
     * slot is, in priority order:
     *  1. an equipped item (`Item(itemId)`), if [equipment] has one for that slot index; else
     *  2. the gender-specific default identitykit for that slot (`Kit(kitId)`), if the slot maps to a
     *     body style and the style id is ≥ 0; else
     *  3. empty (`Empty`).
     */
    fun rebuildCachedBytes(): ByteArray {
        val bodyStyles = if (gender == 1) DEFAULT_FEMALE_BODY_STYLES else DEFAULT_MALE_BODY_STYLES
        val slots = ArrayList<AppearanceSlot>(BODY_SLOT_ORDER.size)
        for (slotIndex in BODY_SLOT_ORDER) {
            val itemId = equipment.getOrElse(slotIndex) { -1 }
            if (itemId >= 0) {
                slots.add(AppearanceSlot.Item(itemId))
                continue
            }
            val styleIndex = BODY_STYLE_BY_SLOT[slotIndex]
            if (styleIndex != null) {
                val kitId = bodyStyles.getOrElse(styleIndex) { -1 }
                if (kitId >= 0) {
                    slots.add(AppearanceSlot.Kit(kitId))
                    continue
                }
            }
            slots.add(AppearanceSlot.Empty)
        }

        val model = PlayerAppearance(
            gender = gender,
            title = title,
            slots = slots,
            colourMask = 0,
            kitColours = colours.copyOf(),
            kitStyles = styles.copyOf(),
            bas = bas,
            name = displayName,
            combatLevel = combatLevel,
            combat2 = null,
            skillLevel = skillLevel,
            skillBonus = skillBonus,
            headbar = null,
        )
        return PlayerAppearanceEncoder.encode(model).also { cachedBytes = it }
    }

    companion object {
        /**
         * Human Body/Wearpos-def disabled-slot set ({12, 13, 17}) — these slots emit NOTHING.
         * Verified from the binary (`DecodeAppearanceEquipment`), alerion (`DEFAULT_DISABLED_SLOTS`),
         * and the cache `BodyDefinition` (DEFAULTS/archive-6/file-0, opcode 1).
         *
         * **FLAG → cache-library-engineer:** add a `Cache.body(id): BodyDefinition?` accessor (mirrors
         * the existing `Cache.idk(id)` pattern; `BodyDecoder` already exists). Once available, derive
         * [BODY_SLOT_ORDER] from `Cache.body(0)?.disabledSlots` so the order is fully cache-authoritative
         * instead of this verified-default constant.
         */
        val DISABLED_SLOTS: Set<Int> = setOf(12, 13, 17)

        /** Emitted body-slot indices in def order (0..18 minus [DISABLED_SLOTS]) → 16 slots. */
        val BODY_SLOT_ORDER: List<Int> = (0..18).filter { it !in DISABLED_SLOTS }

        /**
         * Maps a renderable body slot index → its identitykit "body style" array index (alerion
         * `BODY_STYLE_BY_SLOT`, matching the human Body def). Slots NOT in this map carry no default
         * kit (they are equipment-only slots: hat/cape/amulet/weapon/shield/etc.).
         */
        val BODY_STYLE_BY_SLOT: Map<Int, Int> = mapOf(
            4 to 2,   // chest/torso
            6 to 3,   // arms
            7 to 5,   // legs
            8 to 0,   // hair / head
            9 to 4,   // hands
            10 to 6,  // feet
            11 to 1,  // jaw / beard
        )

        /** Default male identitykit ids by body-style index 0..6 (alerion DEFAULT_MALE_BODY_STYLES). */
        val DEFAULT_MALE_BODY_STYLES: IntArray = intArrayOf(3, 14, 18, 26, 34, 38, 42)

        /**
         * Default female identitykit ids by body-style index 0..6 (alerion DEFAULT_FEMALE_BODY_STYLES).
         * Index 1 (jaw/beard) = -1 ⇒ no kit ⇒ the slot emits empty (females have no beard kit).
         */
        val DEFAULT_FEMALE_BODY_STYLES: IntArray = intArrayOf(48, -1, 57, 65, 68, 77, 80)

        /** Number of kit-colour / kit-style channels (K). alerion writes 10 of each. */
        const val KIT_CHANNEL_COUNT = 10

        /** Default kit-colour channels (alerion DEFAULT_COLOURS). */
        val DEFAULT_COLOURS: IntArray = intArrayOf(3, 16, 16, 0, 0, 0, 0, 0, 0, 0)
    }
}
