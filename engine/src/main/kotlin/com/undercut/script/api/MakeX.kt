package com.undercut.script.api

import com.undercut.cache.type.items.ItemType
import com.undercut.game.cs2.CS2Executor
import com.undercut.game.interfaces.IFSlot
import com.undercut.script.Script
import com.undercut.util.hashFromInterface

/** Component type of a "New Make" counter/slider container. */
private const val MAKE_COUNTER_WIDGET_TYPE = 33

/** The widget's own per-value entry builder (clientscript) — runs client-side, sends no packet. */
private const val MAKE_COUNTER_BUILD_SCRIPT = 10451

/**
 * Materialise the [count] per-value "Select" entries on a counter/slider's [select] overlay by running
 * the widget's own builder ([MAKE_COUNTER_BUILD_SCRIPT]) via CS2 — purely client-side, no server input.
 * These entries are otherwise created lazily only on the first real slider interaction, so a freshly
 * built slider has none and [setMakeCounter] would no-op. Call this before [setMakeCounter] on a fresh
 * slider, then the only packet sent is the value commit itself.
 *
 * Args mirror the widget's own `script10447 -> script10451` call: the null label comp (-1, so the
 * server-touching branch is skipped), the overlay comp, two unused layout ints, min=1, max=[count], 0.
 */
fun buildMakeCounterEntries(interfaceId: Int, select: Int, count: Int) {
    CS2Executor.executeScript(
        MAKE_COUNTER_BUILD_SCRIPT,
        -1, hashFromInterface(interfaceId, select), 0, 0, 1, count.coerceAtLeast(1), 0,
    )
}

/**
 * Set a RS3 "New Make" counter/slider widget to [target] by firing the per-value **Select op** the
 * widget builds on [select] (child index `target-min`). That op is exactly what a real slider click
 * emits and is the *only* thing that commits the value server-side — the widget's own set-script
 * (`script10450`) updates only the client visual and never transmits, so it's deliberately not used
 * here (and firing it alongside the op raced it in practice).
 *
 * Generic across any interface using this widget; refuses to run unless [container] resolves to a
 * counter widget (type [MAKE_COUNTER_WIDGET_TYPE]) and [target] is within [[min], [max]] (so the
 * `target-min` child is one the widget actually built — never an out-of-range slot).
 *
 * @param container the counter container component (used only to validate the widget type)
 * @param select    its sibling overlay component carrying the per-value Select entries
 */
fun setMakeCounter(interfaceId: Int, container: Int, select: Int, min: Int, max: Int, target: Int): Boolean {
    val widget = interfaces.getComponent(interfaceId, container) ?: return false
    if (widget.type != MAKE_COUNTER_WIDGET_TYPE) return false
    if (target < min || target > max) return false
    return IFSlot(interfaceId, select, target - min).click(1)
}

/**
 * Thin, reusable wrapper over the RS3 "New Make" production interface (parent [PARENT] / panel [PANEL]).
 *
 * Reads are plain properties; the suspend extensions on [Script] drive the category list + item grid so
 * any production script can pick a craftable in one line:
 * ```
 * if (MakeX.isOpen) makeX({ it == "Decorated smithing urn (unfired)" }, { it.contains("Smithing") })
 * if (MakeX.isOpen) makeXConfirm()   // single-product steps (soften, fire, add rune)
 * ```
 *
 * Item grid: [GRID] on [PANEL], each item's "Select" op at slot `4*index+1` (clientscript 7120/7169).
 * Category list: open it via [CATEGORY_TOGGLE] (mirrors a player), then click the "Select" overlay on a
 * [CatList.selectComp], located over its sibling text row on [CatList.textComp] (clientscript 10435).
 */
object MakeX {
    const val PARENT = 1370
    const val PANEL = 1371
    const val MAKE_BUTTON = 30
    const val GRID = 22
    const val CATEGORY_TOGGLE = 28

    private const val SELECTED_ITEM_VARP = 1170
    private const val MAX_QUANTITY_VARP = 8846
    const val SUBCATEGORY_VARP = 1169

    val CATEGORY_LISTS = listOf(CatList(1477, 895, 896), CatList(906, 164, 165), CatList(744, 355, 356))

    val isOpen get() = interfaces.isOpen(PARENT)
    val hasPanel get() = interfaces.isOpen(PANEL)
    val inProgress get() = hasActiveMakeXProgress
    val selectedItemId get() = varps.getVar(SELECTED_ITEM_VARP)
    val maxQuantity get() = varps.getVar(MAX_QUANTITY_VARP)

    /** Craftable items currently rendered in the grid, with the slot to click to select each. */
    fun craftables(): List<Craftable> {
        val grid = interfaces.getComponent(PANEL, GRID) ?: return emptyList()
        return grid.slotChildren
            .filter { it.itemId > 0 }
            .map { Craftable(it.itemId, ItemType.get(it.itemId).name, it.slotId - 1) }
    }

    /** The category list currently populated (depends on which window the make interface is docked in). */
    fun activeCategoryList(): CatList? = CATEGORY_LISTS.firstOrNull {
        interfaces.getComponent(it.iface, it.textComp)?.slotChildren?.any { c -> c.text.isNotBlank() } == true
    }

    fun categories(): List<String> = activeCategoryList()?.let { cl ->
        interfaces.getComponent(cl.iface, cl.textComp)?.slotChildren?.filter { it.text.isNotBlank() }?.map { it.text }
    } ?: emptyList()
}

data class Craftable(val itemId: Int, val name: String, val selectSlot: Int)
data class CatList(val iface: Int, val textComp: Int, val selectComp: Int)

/**
 * Human-plausible reaction delay inserted before each synthetic click. Always applied (even when a
 * script's antiban is off) so dependent clicks are never fired back-to-back.
 */
suspend fun Script.makeXReaction(mean: Int = 820, variance: Int = 520) = delay(mean, variance)

/** Confirm the current selection at the default ("make-all") quantity. */
suspend fun Script.makeXConfirm(): Boolean {
    if (!MakeX.isOpen) return false
    makeXReaction(720, 460)
    continueMakeX()
    delayUntil(6000) { MakeX.inProgress || !MakeX.isOpen }
    return true
}

/**
 * Open the category list the way a player does (the [MakeX.CATEGORY_TOGGLE] dropdown), then click the
 * "Select" op on the row whose text satisfies [match].
 */
suspend fun Script.selectMakeCategory(match: (String) -> Boolean): Boolean {
    repeat(3) {
        // Expand the category ribbon if it isn't already (collapsed == zero slot-children).
        if (MakeX.activeCategoryList() == null) {
            makeXReaction()
            IFSlot(MakeX.PANEL, MakeX.CATEGORY_TOGGLE, -1).click(1)
            delayUntil(2500) { MakeX.activeCategoryList() != null }
        }
        val cl = MakeX.activeCategoryList()
        if (cl != null) {
            // Text rows on textComp are 1-per-category; each builds a hover catcher (2i) and the
            // "Select" op catcher (2i+1) on selectComp.
            val rows = interfaces.getComponent(cl.iface, cl.textComp)?.slotChildren?.filter { it.text.isNotBlank() }.orEmpty()
            val index = rows.indexOfFirst { match(it.text) }
            val selectSlot = 2 * index + 1
            if (index >= 0 && componentSlotExists(cl.iface, cl.selectComp, selectSlot)) {
                val before = varps.getVar(MakeX.SUBCATEGORY_VARP)
                makeXReaction(900, 500)
                if (componentSlotExists(cl.iface, cl.selectComp, selectSlot)) {
                    IFSlot(cl.iface, cl.selectComp, selectSlot).click(1)
                    delayUntil(2500) { varps.getVar(MakeX.SUBCATEGORY_VARP) != before }
                    if (varps.getVar(MakeX.SUBCATEGORY_VARP) != before) return true
                }
            }
        }
        // Didn't switch — collapse the ribbon so the next attempt rebuilds it cleanly.
        if (MakeX.activeCategoryList() != null) {
            IFSlot(MakeX.PANEL, MakeX.CATEGORY_TOGGLE, -1).click(1)
            delayUntil(1500) { MakeX.activeCategoryList() == null }
        }
    }
    return false
}

/** True only when [comp] on [iface] currently has a live slot-child at [slot] — i.e. a click would resolve. */
fun componentSlotExists(iface: Int, comp: Int, slot: Int): Boolean =
    interfaces.getComponent(iface, comp)?.slotChildren?.any { it.slotId == slot } == true

/**
 * Ensure the item matched by [itemMatch] is the selected craftable. If it isn't in the current grid and
 * a [categoryMatch] is given, switch the category first, then select. Returns true once selected.
 */
suspend fun Script.makeXSelect(itemMatch: (String) -> Boolean, categoryMatch: ((String) -> Boolean)? = null): Boolean {
    if (!MakeX.isOpen) return false
    if (selectCraftable(itemMatch)) return true
    if (categoryMatch != null && selectMakeCategory(categoryMatch)) {
        delayUntil(3000) { MakeX.craftables().any { itemMatch(it.name) } }
        if (selectCraftable(itemMatch)) return true
    }
    return false
}

/** Select the item (switching category if needed) then confirm at the default quantity. */
suspend fun Script.makeX(itemMatch: (String) -> Boolean, categoryMatch: ((String) -> Boolean)? = null): Boolean {
    if (!makeXSelect(itemMatch, categoryMatch)) return false
    return makeXConfirm()
}

private suspend fun Script.selectCraftable(match: (String) -> Boolean): Boolean {
    val target = MakeX.craftables().firstOrNull { match(it.name) } ?: return false
    if (MakeX.selectedItemId == target.itemId) return true
    if (!componentSlotExists(MakeX.PANEL, MakeX.GRID, target.selectSlot)) return false
    makeXReaction(980, 620)
    if (!componentSlotExists(MakeX.PANEL, MakeX.GRID, target.selectSlot)) return false
    IFSlot(MakeX.PANEL, MakeX.GRID, target.selectSlot).click(1)
    delayUntil(2500) { MakeX.selectedItemId == target.itemId }
    return MakeX.selectedItemId == target.itemId
}
