package com.undercut.script.event.impl

import com.undercut.game.Tile

/**
 * [ManualDoAction] target for a ground-item interaction. Unlike [ManualItemTarget]
 * (inventory / use-item, no world position) this carries the [tile] the stack sits on,
 * captured from the action's tile params.
 */
class ManualGroundItem(val itemId: Int, val name: String, val tile: Tile)
