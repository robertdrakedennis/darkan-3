package com.undercut.script.event.impl

/**
 * [ManualDoAction] target for an item interaction (inventory / ground item /
 * use-item). [name] is the menu's visible item label, [itemId] the entry's item id.
 */
class ManualItemTarget(val itemId: Int, val name: String)
