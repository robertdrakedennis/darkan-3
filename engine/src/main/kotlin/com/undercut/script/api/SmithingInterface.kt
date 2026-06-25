package com.undercut.script.api

import world.gregs.voidps.cache.Cache
import com.undercut.game.bootstrap.Bootstrap
import com.undercut.game.interfaces.IFSlot
import com.undercut.script.Script

/**
 * Wrapper over the Artisans' Workshop smithing forge interface (interface [INTERFACE]).
 *
 * Mapped from clientscript 2585/2591/2598/2609:
 * - `varplayer_8333` = selected base object (the item the unfinished item will become).
 * - `varbitplayer_43239` = selected upgrade tier (0=Base, 1..5, 50=Burial); clamped to the item's max.
 * - `varplayer_8336` = quantity.
 * - `varplayer_8331/8332` = metal category / item-type dbrow tabs.
 * - item the player will actually create = `script2542(8333, 43239)`.
 * - the Make button is [MAKE_BUTTON]; each tier is its own button ([TIER_BUTTON]).
 * - item-list rows carry `cc_getparam(7811)` = the obj; clicking one sets `varplayer_8333`.
 */
object Smithing {
    const val INTERFACE = 37
    const val ITEM_NAME = 40

    /** The full-width "Begin project" button (verified live: op-1 click creates the unfinished item). */
    const val MAKE_BUTTON = 163
    const val ITEM_LIST = 32

    /** Make-X quantity counter: the slider container [QUANTITY_COUNTER] + its per-value overlay [QUANTITY_SELECT]. */
    const val QUANTITY_COUNTER = 34
    const val QUANTITY_SELECT = 35
    const val QUANTITY_MIN = 1

    const val BASE_OBJECT_VARP = 8333
    const val MATERIAL_DBROW_VARP = 8332
    const val METAL_CATEGORY_VARP = 8331
    const val TIER_VARBIT = 43239

    /** The authoritative chosen quantity Begin sends — committed server-side by the slider's Select op. */
    const val QUANTITY_VARP = 8336

    /** The "metal bank" / ore bank — bars and ores live here as a real inventory (clientscript 2549). */
    const val METAL_BANK_INV = 858
    val metalBank get() = Bootstrap.client.inventoryManager[METAL_BANK_INV]

    /** tier -> clickable button component. 0=Base, 1..5, 50=Burial. */
    val TIER_BUTTON = linkedMapOf(0 to 149, 1 to 161, 2 to 159, 3 to 157, 4 to 155, 5 to 153, 50 to 151)

    val isOpen get() = interfaces.isOpen(INTERFACE)
    val baseObjectId get() = varps.getVar(BASE_OBJECT_VARP)
    val tier get() = varps.getVarBit(TIER_VARBIT)
    val quantity get() = varps.getVar(QUANTITY_VARP)
    val selectedName get() = interfaces.getComponent(INTERFACE, ITEM_NAME)?.text ?: ""

    /** A tier button is present only when the selected item actually supports it. */
    fun tierAvailable(t: Int) =
        TIER_BUTTON[t]?.let { interfaces.getComponent(INTERFACE, it) != null } == true

    fun listRows() = interfaces.getComponent(INTERFACE, ITEM_LIST)?.slotChildren.orEmpty()

    /** Largest quantity the slider currently offers (one per-value entry per step). 0 if unreadable. */
    val quantityMax get() = (interfaces.getComponent(INTERFACE, QUANTITY_SELECT)?.slotChildren?.size ?: 0) + QUANTITY_MIN - 1
}

/**
 * Set the forge's Make-X quantity toward [target], confirmed against `varplayer_8336` (the value Begin
 * sends). The widget's per-value Select entries are created lazily on first interaction, so each attempt
 * first builds them client-side via CS2 ([buildMakeCounterEntries], no input), then commits the exact
 * value with a single Select op. The caller passes a [target] already sized to what the bars allow; the
 * game's own slider still clamps `varplayer_8336` to the makeable, so over-asking is harmless. Retries
 * because selecting a tier rebuilds + resets the slider. Returns the committed quantity (>=1 once it
 * lands), so the caller can proceed with whatever was set.
 */
suspend fun Script.smithSetQuantity(target: Int): Int {
    if (!Smithing.isOpen) return 0
    val clamped = target.coerceAtLeast(Smithing.QUANTITY_MIN)
    if (Smithing.quantity == clamped) return clamped
    makeXReaction()
    repeat(4) {
        if (!Smithing.isOpen) return 0
        buildMakeCounterEntries(Smithing.INTERFACE, Smithing.QUANTITY_SELECT, clamped)
        delay(650, 200)
        setMakeCounter(Smithing.INTERFACE, Smithing.QUANTITY_COUNTER, Smithing.QUANTITY_SELECT, Smithing.QUANTITY_MIN, clamped, clamped)
        delayUntil(1500) { Smithing.quantity == clamped }
        if (Smithing.quantity == clamped) return clamped
        delay(400, 150)
    }
    return Smithing.quantity
}

/** The intermediate carried in the backpack while an item is being smithed. */
const val UNFINISHED_SMITHING_ITEM = 47068

/** Click the tier button for [target] and confirm `varbitplayer_43239` switched. */
suspend fun Script.smithSelectTier(target: Int): Boolean {
    if (!Smithing.isOpen) return false
    if (Smithing.tier == target) return true
    val comp = Smithing.TIER_BUTTON[target] ?: return false
    if (interfaces.getComponent(Smithing.INTERFACE, comp) == null) return false
    makeXReaction()
    IFSlot(Smithing.INTERFACE, comp, -1).click(1)
    delayUntil(2500) { Smithing.tier == target }
    return Smithing.tier == target
}

/**
 * Select the base item whose name matches [match] from the item list, confirming `varplayer_8333`
 * changed. Best-effort: scans the visible list rows; the caller should ensure the correct metal/type
 * tab is active (or pre-select the item).
 */
suspend fun Script.smithSelectItem(match: (String) -> Boolean): Boolean {
    if (!Smithing.isOpen) return false
    if (match(Smithing.selectedName)) return true
    val row = Smithing.listRows().firstOrNull { it.text.isNotBlank() && match(it.text) } ?: return false
    val before = Smithing.baseObjectId
    makeXReaction()
    IFSlot(Smithing.INTERFACE, Smithing.ITEM_LIST, row.slotId).click(1)
    delayUntil(2500) { Smithing.baseObjectId != before }
    return match(Smithing.selectedName)
}

/** Click Make and wait for unfinished items to appear (or the interface to close). */
suspend fun Script.smithMake(): Boolean {
    if (!Smithing.isOpen) return false
    val before = inventory.count(UNFINISHED_SMITHING_ITEM)
    makeXReaction(700, 450)
    IFSlot(Smithing.INTERFACE, Smithing.MAKE_BUTTON, -1).click(1)
    delayUntil(6000) { inventory.count(UNFINISHED_SMITHING_ITEM) > before || !Smithing.isOpen }
    return inventory.count(UNFINISHED_SMITHING_ITEM) > before
}

/** Resolve the item the current selection will produce — `script2542(base, tier)` isn't exposed, so this
 *  reports the selected base name for display; the produced tiered item is derived in-game on Make. */
fun smithSelectedDisplay(): String {
    val base = Smithing.baseObjectId
    val name = if (base > 0) Cache.item(base)?.name ?: "?" else Smithing.selectedName
    val suffix = when (val t = Smithing.tier) {
        0 -> ""; 50 -> " (Burial)"; else -> " +$t"
    }
    return "$name$suffix"
}

// --- Metal bank ---
// Bar item + per-step/cumulative costs are derived from the cache recipes in the aiosmithing package
// (chainBarItem / chainCumulativeBars); these helpers only read the live metal bank.

/** Bars of [barItem] in the metal bank (inv 858), or 0 when the forge is closed and the bank is gone. */
fun smithBarsAvailable(barItem: Int): Int {
    if (barItem <= 0) return 0
    val manager = Bootstrap.client.inventoryManager
    return if (manager.exists(Smithing.METAL_BANK_INV)) manager[Smithing.METAL_BANK_INV].count(barItem) else 0
}

/** Bases to start so the bars carry a whole batch through [cumulativeBars] each: min(cap, bars/cumulative). */
fun smithBatchSize(barItem: Int, cumulativeBars: Int, cap: Int = 20): Int {
    if (cumulativeBars <= 0) return 0
    return minOf(cap, smithBarsAvailable(barItem) / cumulativeBars)
}
