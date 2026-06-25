package com.undercut.game.nxt.entity.player

import com.undercut.game.nxt.entity.PathingEntity
import java.lang.foreign.MemorySegment

class Player(ptr: MemorySegment) : PathingEntity(ptr)