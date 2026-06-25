package com.undercut.script.event.impl

import com.undercut.game.nxt.entity.HitType
import com.undercut.script.event.Event

class Hitsplat(val typeId: Int, val type: HitType, val damage: Int, val target: Any) : Event