package com.undercut.script.impl.trent.urncrafter

import com.undercut.game.items.Item
import com.undercut.script.api.inventory

private val IGNORE = RegexOption.IGNORE_CASE

/** Matches any urn name, capturing the family ("Decorated smithing urn") without the stage suffix. */
private val URN_NAME = Regex("^(.+ urn)(?: \\((?:unfired|no rune|empty|full)\\))?$", IGNORE)

/** Stage suffixes preferred as the auto-detect source — finished urns persist, transient ones don't. */
private val DETECT_PRIORITY = listOf("(empty)", "(full)", "(no rune)", "(unfired)")

fun urnFamilyOf(name: String): String? = URN_NAME.matchEntire(name)?.groupValues?.get(1)

/** The make-interface category for an urn family — the skill word, e.g. "Decorated smithing urn" -> "Smithing". */
fun categoryFor(family: String): String =
    family.substringBeforeLast(" urn").substringAfterLast(' ').replaceFirstChar { it.uppercase() }

fun unfiredRegex(family: String) = Regex("${Regex.escape(family)} \\(unfired\\)", IGNORE)
fun noRuneRegex(family: String) = Regex("${Regex.escape(family)} \\(no rune\\)", IGNORE)
fun emptyRegex(family: String) = Regex("${Regex.escape(family)} \\(empty\\)", IGNORE)

/** Detect the urn family the player is working on from whatever urn item is in the backpack. */
fun detectUrnFamily(): String? {
    val urns = inventory.filter { urnFamilyOf(it.name) != null }
    if (urns.isEmpty()) return null
    val best = urns.minByOrNull { item ->
        DETECT_PRIORITY.indexOfFirst { item.name.endsWith(it) }.let { if (it == -1) DETECT_PRIORITY.size else it }
    } ?: return null
    return urnFamilyOf(best.name)
}

/** The "Add fire rune" / "Add ruby" style inventory action carried by a "(no rune)" urn. */
fun addRuneAction(noRune: Item): String? = noRune.invOps.firstOrNull { it?.startsWith("Add ", true) == true }

/** "Add fire rune" -> "Fire rune" so the consumable can be checked in the backpack. */
fun runeNameFromAction(action: String): String =
    action.removePrefix("Add ").removePrefix("add ").trim().replaceFirstChar { it.uppercase() }
